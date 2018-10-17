package de.evoila.cf.cpi.bosh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.broker.util.RandomString;
import de.evoila.cf.cpi.bosh.connection.BoshConnection;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import io.bosh.client.DirectorException;
import io.bosh.client.deployments.Deployment;
import io.bosh.client.deployments.SSHConfig;
import io.bosh.client.errands.ErrandSummary;
import io.bosh.client.tasks.Task;
import io.bosh.client.vms.Vm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class BoshPlatformService implements PlatformService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String QUEUED = "queued";
    private static final int SLEEP = 3000;
    private static final String ERROR = "error";
    private static final String PROCESSING = "processing";
    private static final String DONE = "done";
    public static final String DEPLOYMENT_NAME_PREFIX = "sb-";

    protected final BoshConnection connection;

    private final PlatformRepository platformRepository;
    protected final ServicePortAvailabilityVerifier portAvailabilityVerifier;
    private final DeploymentManager deploymentManager;
    private final Optional<DashboardClient> dashboardClient;
    private final BoshProperties boshProperties;
    private final CatalogService catalogService;

    public BoshPlatformService(PlatformRepository repository,
                               CatalogService catalogService,
                               ServicePortAvailabilityVerifier availabilityVerifier,
                               BoshProperties boshProperties,
                               Optional<DashboardClient> dashboardClient,
                               DeploymentManager deploymentManager) {

        Assert.notNull(repository, "The platform repository can not be null");
        Assert.notNull(availabilityVerifier, "The ServicePortAvailabilityVerifier can not be null");
        Assert.notNull(boshProperties, "The BoshProperties can not be null");
        Assert.notNull(catalogService, "The CatalogService can not be null");
        Assert.notNull(deploymentManager, "The Deployment Manager can not be null");

        this.catalogService = catalogService;
        this.platformRepository = repository;
        this.portAvailabilityVerifier = availabilityVerifier;
        this.dashboardClient = dashboardClient;
        this.deploymentManager = deploymentManager;
        this.boshProperties = boshProperties;
        connection = new BoshConnection(boshProperties.getUsername(),
                                        boshProperties.getPassword(),
                                        boshProperties.getHost(),
                                        boshProperties.getPort(),
                                        boshProperties.getAuthentication()).authenticate();
    }

    public BoshConnection getConnection() { return connection; }

    @Override
    public boolean isSyncPossibleOnCreate(Plan plan) {
        return false;
    }

    @Override
    public boolean isSyncPossibleOnDelete(ServiceInstance instance) {
        return false;
    }

    @Override
    public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan) {
        return false;
    }

    @Override
    @PostConstruct
    public void registerCustomPlatformService() {
        this.platformRepository.addPlatform(Platform.BOSH, this);
        log.info("Added Platform-Service " + this.getClass().toString() + " of type " + Platform.BOSH);
    }

    @Override
    public ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance createInstance(ServiceInstance in, Plan plan, Map<String, Object> customParameters) throws PlatformException {
        ServiceInstance instance = createServiceInstanceObject(in, plan);
        try {
            Deployment deployment = deploymentManager.createDeployment(instance, plan, customParameters);
            Observable<Task> task = connection
                    .connection()
                    .deployments()
                    .create(deployment);

            waitForTaskCompletion(task.toBlocking().first());

            Observable<List<ErrandSummary>> errands = connection
                    .connection()
                    .errands()
                    .list(deployment.getName());
            runCreateErrands(instance, plan, deployment, errands);

            updateHosts(instance, plan, deployment);
        } catch (IOException e) {
            log.error("Couldn't create Service Instance via Bosh Deployment");
            log.error(e.getMessage());
            throw new PlatformException("Could not create Service Instance", e);
        }
        return instance;
    }

    @Override
    public ServiceInstance postCreateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        boolean available;
        try {
            available = portAvailabilityVerifier.verifyServiceAvailability(serviceInstance, false);
        } catch (Exception e) {
            throw new PlatformException("Service instance is not reachable. Service may not be started on instance.",
                    e);
        }

        if (!available) {
            throw new PlatformException("Service instance is not reachable. Service may not be started on instance.");
        }

        return serviceInstance;
    }

    protected void runCreateErrands(ServiceInstance instance, Plan plan, Deployment deployment,
                                    Observable<List<ErrandSummary>> errands) throws PlatformException {}

    protected void runUpdateErrands(ServiceInstance instance, Plan plan, Deployment deployment,
                                    Observable<List<ErrandSummary>> errands) throws PlatformException {}

    protected void runDeleteErrands(ServiceInstance instance, Deployment deployment,
                                    Observable<List<ErrandSummary>> errands) { }

    protected void waitForTaskCompletion(Task task) throws PlatformException {
        log.debug("Bosh Deployment started waiting for task to complete {}", task);
        if (task == null) {
            log.error("Deployment Task is null");
            throw new PlatformException("Could not alter Service Instance. No Bosh task is present");
        }
        switch (task.getState()) {
            case PROCESSING:
            case QUEUED:
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    throw new PlatformException(e);
                }
                Observable<Task> taskObservable = connection.connection().tasks().get(task.getId());
                waitForTaskCompletion(taskObservable.toBlocking().first());
                return;
            case ERROR:
                log.error(String.format("Could not create Service Instance. Task finished with error. [%s]  %s", task.getId(), task.getResult()));
                throw new PlatformException(String.format("Could not create Service Instance. Task finished with error. [%s]  %s", task.getId(), task.getResult()));
            case DONE:
                return;
        }
    }

    public ServiceInstance createServiceInstanceObject(ServiceInstance instance, Plan plan) {
        if(dashboardClient.isPresent()) {
            return new ServiceInstance(instance,
                    DashboardUtils.dashboard(catalogService.getServiceDefinition(instance.getServiceDefinitionId()), instance.getId()),
                    randomString());
        } else {
            return new ServiceInstance(instance, randomString());
        }
    }

    private String randomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {
        return new ServiceInstance(instance,
                DashboardUtils.dashboard(catalogService.getServiceDefinition(instance.getServiceDefinitionId()),
                        instance.getId()),
                null);
    }

    @Override
    public void preDeleteInstance(ServiceInstance serviceInstance) {}

    @Override
    public void deleteInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        try {
        	Deployment deployment = null;
        	Observable<List<ErrandSummary>> errands = null;
        	try {
        		deployment = connection
                        .connection()
                        .deployments()
                        .get(deploymentManager.getDeployment(serviceInstance).getName())
                        .toBlocking().first();
                errands = connection
                        .connection()
                        .errands()
                        .list(deployment.getName());
                
                runDeleteErrands(serviceInstance, deployment, errands);
                log.debug("Using the deployment and errands given by the bosh director.");
        	} catch (NullPointerException | DirectorException e) {
        		log.debug("Could not get the deployment from bosh. Creating an empty temporary one to delete the remaining VMs and the failed deployment.");
        		deployment = new Deployment();
        		deployment.setName(DeploymentManager.deploymentName(serviceInstance));
        	}
            Observable<Task> task = connection
                    .connection()
                    .deployments()
                    .delete(deployment);
            waitForTaskCompletion(task.toBlocking().first());
        } catch (Exception e) {
            throw new PlatformException("Could not delete failed service instance", e);
        }
    }

    @Override
    public ServiceInstance preUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
    public ServiceInstance updateInstance(ServiceInstance instance, Plan plan, Map<String, Object> customParameters) throws PlatformException {
        Deployment deployment = connection.connection().deployments()
                .get(deploymentManager.getDeployment(instance).getName())
                .toBlocking().first();

        Observable<List<ErrandSummary>> errands = connection
                .connection()
                .errands()
                .list(deployment.getName());

        runUpdateErrands(instance, plan, deployment, errands);
        try {
            deployment = deploymentManager.updateDeployment(instance, deployment, plan, customParameters);

            Observable<Task> taskObservable = connection
                    .connection()
                    .deployments()
                    .update(deployment);

            waitForTaskCompletion(taskObservable.toBlocking().first());
            updateHosts(instance, plan, deployment);
        } catch (IOException e) {
            throw new PlatformException("Could not update Service instance", e);
        }

        return new ServiceInstance(instance.getId(), instance.getServiceDefinitionId(), plan.getId(),
                instance.getOrganizationGuid(), instance.getSpaceGuid(), instance.getParameters(),
                instance.getDashboardUrl(), instance.getInternalId());
    }

    @Override
    public ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    protected abstract void updateHosts(ServiceInstance instance, Plan plan, Deployment deployment);

    protected List<Vm> getVms(ServiceInstance instance) {
        return this.connection
                .connection().vms()
                .listDetails(deploymentManager.getDeployment(instance)
                        .getName()).toBlocking().first();
    }

    protected ServerAddress toServerAddress(String namePrefix, Vm vm, int port) {
        return new ServerAddress(namePrefix + vm.getIndex(), vm.getIps().get(0), port);
    }

    protected ServerAddress toServerAddress(Vm vm, int port) {
        return toServerAddress(vm.getJobName(), vm, port);
    }

    protected Deployment getDeployment(ServiceInstance instance){
        return deploymentManager.getDeployment(instance);
    }

    protected Observable<Session> getSshSession(ServiceInstance instance,
                                                InstanceGroup instanceGroup,
                                                int index) throws JSchException {

        Deployment deployment = this.getDeployment(instance);
        JSch jsch = new JSch();
        KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
        ByteArrayOutputStream publicKeyBuff = new ByteArrayOutputStream(2048);

        keyPair.writePublicKey(publicKeyBuff, "SSHCerts");
        keyPair.writePrivateKey(privateKeyBuff);

        RandomString usernameRandomString = new RandomString(10);

        SSHConfig config = new SSHConfig(deployment.getName(),
                usernameRandomString.nextString(), publicKeyBuff.toString(),
                instanceGroup.getName(), index);

        return this.connection
                .connection()
                .vms()
                .ssh(config, privateKeyBuff.toString());
    }

    protected Observable<Session> getSshSession(String deploymentName, String instanceName,
                                                int index) throws JSchException {

        JSch jsch = new JSch();
        KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
        ByteArrayOutputStream publicKeyBuff = new ByteArrayOutputStream(2048);

        keyPair.writePublicKey(publicKeyBuff, "SSHCerts");
        keyPair.writePrivateKey(privateKeyBuff);

        RandomString usernameRandomString = new RandomString(10);

        SSHConfig config = new SSHConfig(deploymentName,
                usernameRandomString.nextString(), publicKeyBuff.toString(),
                instanceName, index);

        return this.connection
                .connection()
                .vms()
                .ssh(config, privateKeyBuff.toString());
    }

    public Manifest getManifest(Deployment deployment) throws IOException {
        return deploymentManager.readManifestFromString(deployment.getRawManifest());
    }

    protected Manifest getDeployedManifest(String deploymentName) throws IOException {
        String manifest = this.connection
                .connection()
                .deployments()
                .get(deploymentName)
                .toBlocking().first().getRawManifest();

        return deploymentManager.readManifestFromString(manifest);
    }
}
