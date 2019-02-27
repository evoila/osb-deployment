package de.evoila.cf.cpi.kubernetes;

import de.evoila.cf.broker.bean.KubernetesProperties;
import de.evoila.cf.broker.bean.auth.KubernetesAuthMode;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.kubernetes.deployment.KubernetesDeploymentManager;
import hapi.chart.ChartOuterClass;
import hapi.services.tiller.Tiller;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.microbean.helm.ReleaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author Johannes Hiemer.
 */
public abstract class KubernetesPlatformService implements PlatformService {

    protected Logger log = LoggerFactory.getLogger(getClass());

    public static final String DEPLOYMENT_NAME_PREFIX = "sb-";

    private boolean usesServiceLoadBalancer = false;

    protected PlatformRepository platformRepository;

    protected ServicePortAvailabilityVerifier portAvailabilityVerifier;

    protected DefaultKubernetesClient kubernetesClient;

    protected Optional<DashboardClient> dashboardClient;

    protected KubernetesDeploymentManager kubernetesDeploymentManager;

    protected Environment environment;

    private String encodePemFile(String pemFile) {
        if (pemFile != null)
            return Base64.getEncoder().encodeToString(pemFile.getBytes());
        else
            return  null;
    }

    public KubernetesPlatformService(PlatformRepository platformRepository,
                                     ServicePortAvailabilityVerifier portAvailabilityVerifier,
                                     KubernetesProperties kubernetesProperties,
                                     Optional<DashboardClient> dashboardClient,
                                     KubernetesDeploymentManager kubernetesDeploymentManager,
                                     Environment environment,
                                     boolean usesServiceLoadBalancer) {
        this.platformRepository = platformRepository;
        this.portAvailabilityVerifier = portAvailabilityVerifier;
        this.usesServiceLoadBalancer = usesServiceLoadBalancer;
        this.dashboardClient = dashboardClient;
        this.environment = environment;
        Config config = new ConfigBuilder()
                .withMasterUrl(kubernetesProperties.getMaster())
                .withApiVersion(kubernetesProperties.getApi().getVersion())
                .build();

        config.setCaCertFile(null);
        config.setClientKeyFile(null);
        config.setCaCertData(encodePemFile(kubernetesProperties.getCerts().getCa().getData()));

        if (kubernetesProperties.getAuth() != null &&
                kubernetesProperties.getAuth().getMode().equals(KubernetesAuthMode.X509)) {
            config.setCaCertData(encodePemFile(kubernetesProperties.getCerts().getCa().getData()));
            config.setClientCertData(encodePemFile(kubernetesProperties.getCerts().getClient().getData()));
            config.setClientKeyData(encodePemFile(kubernetesProperties.getCerts().getClient().getKey().getData()));
            config.setClientKeyPassphrase(kubernetesProperties.getCerts().getClient().getKey().getPassphrase());

        } else if (kubernetesProperties.getAuth() != null &&
                kubernetesProperties.getAuth().getMode().equals(KubernetesAuthMode.BASIC_AUTH)) {
            config.setUsername(kubernetesProperties.getAuth().getBasic().getUsername());
            config.setPassword(kubernetesProperties.getAuth().getBasic().getPassword());
        } else if (kubernetesProperties.getAuth() != null &&
                kubernetesProperties.getAuth().getMode().equals(KubernetesAuthMode.TOKEN)) {
            config.setOauthToken(kubernetesProperties.getAuth().getToken());
        }

        if (kubernetesProperties.getConnectionTimeout() != null)
            config.setConnectionTimeout(kubernetesProperties.getConnectionTimeout());

        if (kubernetesProperties.getRequestTimeout() != null)
            config.setRequestTimeout(kubernetesProperties.getRequestTimeout());

        if (kubernetesProperties.getWatch() != null &&
                kubernetesProperties.getWatch().getReconnectInternal() != null)
            config.setWatchReconnectInterval(kubernetesProperties.getWatch().getReconnectInternal());

        if (kubernetesProperties.getWatch() != null &&
                kubernetesProperties.getWatch().getReconnectLimit() != null)
            config.setWatchReconnectLimit(kubernetesProperties.getWatch().getReconnectLimit());

        if (kubernetesProperties.getTruststore() != null &&
                kubernetesProperties.getTruststore().getFile() != null)
            config.setTrustStoreFile(kubernetesProperties.getTruststore().getFile());

        if (kubernetesProperties.getTruststore() != null &&
                kubernetesProperties.getTruststore().getPassphrase() != null)
            config.setTrustStorePassphrase(kubernetesProperties.getTruststore().getPassphrase());

        if (kubernetesProperties.getKeystore() != null &&
                kubernetesProperties.getKeystore().getFile() != null)
            config.setKeyStoreFile(kubernetesProperties.getKeystore().getFile());

        if (kubernetesProperties.getKeystore() != null &&
                kubernetesProperties.getKeystore().getPassphrase() != null)
            config.setKeyStorePassphrase(kubernetesProperties.getKeystore().getPassphrase());

        this.kubernetesClient = new DefaultKubernetesClient(config);

        this.kubernetesDeploymentManager = new KubernetesDeploymentManager(kubernetesProperties,
                this.environment,
                this.usesServiceLoadBalancer);
    }

    @Override
    @PostConstruct
    public void registerCustomPlatformService () {
        platformRepository.addPlatform(Platform.KUBERNETES, this);
        log.info("Added Platform-Service " + this.getClass().toString() + " of type " + Platform.KUBERNETES);
    }

    @Override
    public boolean isSyncPossibleOnBind() { return true; }

    @Override
    public boolean isSyncPossibleOnUnbind() { return true; }

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
    public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {
        return new ServiceInstance(instance, null, null);
    }

    @Override
    public ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan,
                                          Map<String, Object> customParameters) throws PlatformException {
        try {
            Tiller.InstallReleaseRequest.Builder requestBuilder = kubernetesDeploymentManager
                    .createDeployment(serviceInstance,  false, 10);
            ChartOuterClass.Chart.Builder chart = kubernetesDeploymentManager.loadChart();

            ReleaseManager releaseManager = this.getNewReleaseManagerInstance();
            Future<Tiller.InstallReleaseResponse> releaseResponse = releaseManager.install(requestBuilder, chart);
            Tiller.InstallReleaseResponse response = releaseResponse.get();

            // TODO: Add host handling here
            this.updateHosts(serviceInstance, plan, null);
        } catch (Exception exception) {
            throw new PlatformException(exception);
        }

        return serviceInstance;
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
    public ServiceInstance updateInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
    public void preDeleteInstance(ServiceInstance serviceInstance) {}

    @Override
    public void deleteInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        try {
            Tiller.UninstallReleaseRequest.Builder builder = kubernetesDeploymentManager.deleteDeployment(serviceInstance);

            ReleaseManager releaseManager = this.getNewReleaseManagerInstance();
            Future<Tiller.UninstallReleaseResponse> uninstallResponse = releaseManager.uninstall(builder.build());
            Tiller.UninstallReleaseResponse response = uninstallResponse.get();
        } catch (Exception exception) {
            throw new PlatformException(exception);
        }
    }

    @Override
    public void postDeleteInstance(ServiceInstance serviceInstance) {}

    @Override
    public ServiceInstance getInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        try {
            ReleaseManager releaseManager = this.getNewReleaseManagerInstance();

            Tiller.GetReleaseStatusRequest.Builder getRequest = kubernetesDeploymentManager.getDeployment(serviceInstance);

            Future<Tiller.GetReleaseStatusResponse> getResponse = releaseManager.getStatus(getRequest.build());
            Tiller.GetReleaseStatusResponse response = getResponse.get();
        } catch (Exception exception) {
            throw new PlatformException(exception);
        }

        return serviceInstance;
    }

    protected abstract void updateHosts(ServiceInstance serviceInstance, Plan plan, Service service);

    private ReleaseManager getNewReleaseManagerInstance() throws IOException {
        org.microbean.helm.Tiller tiller = new org.microbean.helm.Tiller(this.kubernetesClient);
        ReleaseManager releaseManager = new ReleaseManager(tiller);

        return releaseManager;
    }

    private Service getService(String serviceName) throws PlatformException {
        Service service = this.kubernetesClient.services()
                .inNamespace("default").withName(serviceName).get();

        if (service == null) {
            throw new PlatformException("Could not find service for port");
        }

        return service;
    }

    private String getKubeIP() {
        return this.kubernetesClient.getMasterUrl().getHost();
    }

    public void deployLoadBalancer(ServiceInstance serviceInstance, int port) throws PlatformException {
        Map<String, String> selector = new HashMap<>();
        selector.put("release", KubernetesDeploymentManager.deploymentName(serviceInstance));

        List<ServicePort> servicePorts = new ArrayList<>();
        servicePorts.add(new ServicePortBuilder()
                .withPort(port)
                .withTargetPort(new IntOrString(port))
                .withProtocol("TCP")
                .build()
        );

        this.kubernetesClient.services()
                .inNamespace("default").createNew()
                .withKind("Service")
                .withApiVersion("v1")
                .withMetadata(new ObjectMetaBuilder()
                        .withName(KubernetesDeploymentManager.deploymentName(serviceInstance))
                        .build()
                )
                .withSpec(new ServiceSpecBuilder()
                        .withType("LoadBalancer")
                        .withSessionAffinity("None")
                        .withExternalTrafficPolicy("Cluster")
                        .withSelector(selector)
                        .withPorts(servicePorts)
                        .build()
                ).done();
    }

    public void deleteLoadBalancer(ServiceInstance serviceInstance) throws PlatformException {
        Service service = this.getService(KubernetesDeploymentManager.deploymentName(serviceInstance));

        this.kubernetesClient.services()
                .inNamespace("default")
                .delete(service);
    }

    private String getServiceEndpoint(String deploymentName) throws PlatformException {
        Service service = this.getService(deploymentName);

        if (service == null) {
            throw new PlatformException("Service not found (Manually deleted or modified) " + deploymentName);
        }

        return service.getStatus().getLoadBalancer().getIngress().get(0).getIp();
    }

    private int getServicePort(String deploymentName) throws PlatformException {
        Service service = this.getService(deploymentName);

        if (service == null) {
            throw new PlatformException("Service not found (Manually deleted or modified) " + deploymentName);
        }

        return service.getSpec().getPorts().get(0).getNodePort();
    }

    public String getCredentials(String deploymentName, String secretName) throws PlatformException {
        Secret secret = this.kubernetesClient.secrets().inNamespace("default")
                .withName(deploymentName).get();

        if (secret == null) {
            throw new PlatformException("Could not find service secret for deployment");
        }

        Map<String, String> secrets = secret.getData();
        String password = secrets.get(secretName);

        if (password == null)
            throw new PlatformException("Could not find password for deployment");

        return new String(Base64.getDecoder().decode(password));
    }


}
