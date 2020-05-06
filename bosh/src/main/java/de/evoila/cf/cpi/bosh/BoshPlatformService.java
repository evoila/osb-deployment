package de.evoila.cf.cpi.bosh;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.controller.utils.DashboardUtils;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.exception.ServiceDefinitionDoesNotExistException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.model.json.schema.JsonSchema;
import de.evoila.cf.broker.model.json.schema.utils.JsonSchemaUtils;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.CatalogService;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.bosh.connection.BoshClient;
import de.evoila.cf.cpi.bosh.deployment.DeploymentManager;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.security.utils.RandomString;
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
import java.util.*;

/**
 * @author Yannic Remmet, Johannes Hiemer.
 */
public abstract class BoshPlatformService implements PlatformService {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String QUEUED = "queued";

    private static final int SLEEP = 3000;

    private static final String ERROR = "error";

    private static final String PROCESSING = "processing";

    private static final String DONE = "done";

    protected final BoshClient boshClient;

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
        boshClient = new BoshClient(boshProperties.getUsername(),
                boshProperties.getPassword(),
                boshProperties.getHost(),
                boshProperties.getPort(),
                boshProperties.getAuthentication()).authenticate();
    }

    public BoshClient getBoshClient() {
        return boshClient;
    }

    @Override
    public boolean isSyncPossibleOnCreate(Plan plan) {
        return false;
    }

    @Override
    public boolean isSyncPossibleOnBind() {
        return true;
    }

    @Override
    public boolean isSyncPossibleOnUnbind() {
        return true;
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

    protected void runCreateErrands(ServiceInstance instance, Plan plan, Deployment deployment,
                                    Observable<List<ErrandSummary>> errands) throws PlatformException {
    }

    protected void runUpdateErrands(ServiceInstance instance, Plan plan, Deployment deployment,
                                    Observable<List<ErrandSummary>> errands) throws PlatformException {
    }

    protected void runDeleteErrands(ServiceInstance instance, Deployment deployment,
                                    Observable<List<ErrandSummary>> errands) {
    }

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
                Observable<Task> taskObservable = boshClient.client().tasks().get(task.getId());
                waitForTaskCompletion(taskObservable.toBlocking().first());
                return;
            case ERROR:
                log.error(String.format("Could not create Service Instance. Task finished with error. [%s]  %s", task.getId(), task.getResult()));
                throw new PlatformException(String.format("Could not create Service Instance. Task finished with error. [%s]  %s", task.getId(), task.getResult()));
            case DONE:
                return;
        }
    }

    public ServiceInstance createServiceInstanceObject(ServiceInstance instance, Plan plan) throws ServiceDefinitionDoesNotExistException {
        if (dashboardClient.isPresent()) {
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
    public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) throws ServiceDefinitionDoesNotExistException {
        return new ServiceInstance(instance,
                DashboardUtils.dashboard(catalogService.getServiceDefinition(instance.getServiceDefinitionId()),
                        instance.getId()),
                null);
    }

    @Override
    public ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance createInstance(ServiceInstance in, Plan plan, Map<String, Object> customParameters) throws PlatformException, ServiceDefinitionDoesNotExistException {
        ServiceInstance instance = createServiceInstanceObject(in, plan);
        try {
            Deployment deployment = deploymentManager.createDeployment(instance, plan, customParameters);
            Observable<Task> task = boshClient
                    .client()
                    .deployments()
                    .create(deployment);

            waitForTaskCompletion(task.toBlocking().first());

            Observable<List<ErrandSummary>> errands = boshClient
                    .client()
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

    @Override
    public ServiceInstance preUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
    public ServiceInstance updateInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) throws PlatformException {
        Deployment deployment = boshClient.client().deployments()
                .get(DeploymentManager.deploymentName(serviceInstance))
                .toBlocking().first();

        Observable<List<ErrandSummary>> errands = boshClient
                .client()
                .errands()
                .list(deployment.getName());

        runUpdateErrands(serviceInstance, plan, deployment, errands);

        try {
            deployment = deploymentManager.updateDeployment(serviceInstance, deployment, plan, customParameters);

            Observable<Task> taskObservable = boshClient
                    .client()
                    .deployments()
                    .update(deployment);

            waitForTaskCompletion(taskObservable.toBlocking().first());
            updateHosts(serviceInstance, plan, deployment);
        } catch (IOException e) {
            throw new PlatformException("Could not update Service instance", e);
        }

        return new ServiceInstance(serviceInstance, serviceInstance.getDashboardUrl(),
                serviceInstance.getInternalId(), serviceInstance.getHosts());
    }

    @Override
    public ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
    public void preDeleteInstance(ServiceInstance serviceInstance) {
    }

    @Override
    public void deleteInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        try {
            Deployment deployment = null;
            Observable<List<ErrandSummary>> errands = null;
            try {
                deployment = boshClient
                        .client()
                        .deployments()
                        .get(DeploymentManager.deploymentName(serviceInstance))
                        .toBlocking().first();
                errands = boshClient
                        .client()
                        .errands()
                        .list(deployment.getName());

                runDeleteErrands(serviceInstance, deployment, errands);
                log.debug("Using the deployment and errands given by the bosh director.");
            } catch (NullPointerException | DirectorException e) {
                log.debug("Could not get the deployment from bosh. Creating an empty temporary one to delete the remaining VMs and the failed deployment.");
                deployment = new Deployment();
                deployment.setName(DeploymentManager.deploymentName(serviceInstance));
            }
            Observable<Task> task = boshClient
                    .client()
                    .deployments()
                    .delete(deployment);
            waitForTaskCompletion(task.toBlocking().first());
        } catch (Exception e) {
            throw new PlatformException("Could not delete failed service instance", e);
        }
    }

    @Override
    public void postDeleteInstance(ServiceInstance serviceInstance) throws PlatformException {
    }

    ;

    @Override
    public ServiceInstance getInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        Manifest manifest;
        try {
            manifest = this.getDeployedManifest(serviceInstance);
        } catch (IOException ex) {
            throw new PlatformException("Cannot read HaProxy configuration", ex);
        }

        if (plan.getSchemas() != null && plan.getSchemas().getServiceInstance() != null
                && plan.getSchemas().getServiceInstance().getUpdate() != null) {
            JsonSchema jsonSchema = plan.getSchemas().getServiceInstance().getUpdate().getParameters();
            if (jsonSchema != null && jsonSchema.getType().equals(JsonFormatTypes.OBJECT)) {
                Map<String, JsonSchema> properties = jsonSchema.getProperties();

                HashMap<String, Object> result = new HashMap<>();
                for (Map.Entry<String, JsonSchema> property : properties.entrySet()) {
                    HashMap<String, Object> instanceGroupResult = new HashMap<>();

                    InstanceGroup instanceGroup = manifest.getInstanceGroups().stream().
                            filter(g -> g.getName().equals(property.getKey())).
                            findFirst().orElse(null);

                    if (instanceGroup != null) {
                        Map<String, Object> instanceGroupProperties = new HashMap<>();
                        if (instanceGroup.getProperties().containsKey(property.getKey()))
                            instanceGroupProperties = (Map<String, Object>) instanceGroup.getProperties().get(property.getKey());

                        JsonSchemaUtils.mergeMaps(property.getValue().getProperties(), instanceGroupProperties, instanceGroupResult);
                        result.put(instanceGroup.getName(), instanceGroupResult);
                    }
                }
                serviceInstance.setParameters(result);
            }
        }
        return serviceInstance;
    }

    protected abstract void updateHosts(ServiceInstance serviceInstance, Plan plan, Deployment deployment) throws PlatformException;

    protected List<Vm> getVms(ServiceInstance serviceInstance) {
        return this.boshClient
                .client().vms()
                .listDetails(DeploymentManager.deploymentName(serviceInstance))
                .toBlocking().first();
    }

    protected ServerAddress toServerAddress(String namePrefix, Vm vm, int port) {
        return new ServerAddress(namePrefix + vm.getIndex(), vm.getIps().get(0), port);
    }

    protected ServerAddress toServerAddress(Vm vm, int port) {
        return toServerAddress(vm.getJobName(), vm, port);
    }

    protected ServerAddress toServerAddress(Vm vm, int port, Plan plan) {
        ServerAddress serverAddress = toServerAddress(vm.getJobName(), vm, port);

        if (plan != null && plan.getMetadata() != null && plan.getMetadata().getBackup() != null
                && serverAddress.getName().contains(plan.getMetadata().getBackup().getInstanceGroup()))
            serverAddress.setBackup(plan.getMetadata().getBackup().isEnabled());

        return serverAddress;
    }

    protected Observable<Session> getSshSession(String serviceInstanceName, String instanceGroupName,
                                                int index) throws JSchException {

        JSch jsch = new JSch();
        KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA);
        ByteArrayOutputStream privateKeyBuff = new ByteArrayOutputStream(2048);
        ByteArrayOutputStream publicKeyBuff = new ByteArrayOutputStream(2048);

        keyPair.writePublicKey(publicKeyBuff, "SSHCerts");
        keyPair.writePrivateKey(privateKeyBuff);

        RandomString usernameRandomString = new RandomString(10);

        SSHConfig config = new SSHConfig(serviceInstanceName,
                usernameRandomString.nextString(), publicKeyBuff.toString(),
                instanceGroupName, index);

        return this.boshClient
                .client()
                .vms()
                .ssh(config, privateKeyBuff.toString());
    }

    protected Observable<Session> getSshSession(ServiceInstance serviceInstance, String instanceGroupName,
                                                int index) throws JSchException {
        return this.getSshSession(DeploymentManager.deploymentName(serviceInstance), instanceGroupName, index);
    }

    public Manifest getManifest(Deployment deployment) throws IOException {
        return deploymentManager.readManifestFromString(deployment.getRawManifest());
    }

    public Manifest getDeployedManifest(ServiceInstance serviceInstance) throws IOException {
        String manifest = this.boshClient
                .client()
                .deployments()
                .get(DeploymentManager.deploymentName(serviceInstance))
                .toBlocking().first().getRawManifest();

        return deploymentManager.readManifestFromString(manifest);
    }

}
