package de.evoila.cf.cpi.kubernetes.deployment;

import de.evoila.cf.broker.bean.KubernetesProperties;
import de.evoila.cf.broker.model.ServiceInstance;
import hapi.chart.ChartOuterClass.Chart;
import hapi.services.tiller.Tiller.GetReleaseStatusRequest;
import hapi.services.tiller.Tiller.InstallReleaseRequest;
import hapi.services.tiller.Tiller.UninstallReleaseRequest;
import org.microbean.helm.chart.URLChartLoader;
import org.springframework.core.env.Environment;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Niclas Feldhausen, Johannes Hiemer.
 */
public class KubernetesDeploymentManager {

    protected static String DEPLOYMENT_PREFIX = "sb-";

    protected File helmChart;

    protected KubernetesProperties kubernetesProperties;

    protected Environment environment;

    protected boolean useServiceLoadBalancer = false;

    public KubernetesDeploymentManager(KubernetesProperties kubernetesProperties,
                                       Environment environment,
                                       boolean useServiceLoadBalancer) {
        this.kubernetesProperties = kubernetesProperties;
        this.environment = environment;
        this.useServiceLoadBalancer = useServiceLoadBalancer;
    }

    public Chart.Builder loadChart() throws InstantiationException, IOException {
        Chart.Builder chart;

        this.helmChart = ResourceUtils.getFile("classpath:kubernetes/redis-4.2.2.tgz");

        try (final URLChartLoader chartLoader = new URLChartLoader()) {
            chart = chartLoader.load(helmChart.toURI().toURL());
        } catch (IOException e) {
            throw new InstantiationException("Could not load helm chart!");
        }

        return chart;
    }

    public InstallReleaseRequest.Builder createDeployment(ServiceInstance serviceInstance, boolean cluster, int volumeSize) {
        Map<String, Object> chartConfiguration = new HashMap<>();

        if (!cluster) {
            HashMap<String, Object> clusterMap = new HashMap<>();
            clusterMap.put("enabled", false);
            chartConfiguration.put("cluster", clusterMap);
        }

        HashMap<String, Object> master = getMasterConfiguration();

        if (this.useServiceLoadBalancer) {
            HashMap<String, Object> service = new HashMap<>();
            service.put("type", "LoadBalancer");
            master.put("service", service);
        }

        master.put("persistence", getSizeYaml(volumeSize));

        chartConfiguration.put("master", master);
        chartConfiguration.put("slave", getMasterConfiguration());

        InstallReleaseRequest.Builder requestBuilder = InstallReleaseRequest.newBuilder();
        requestBuilder.setName(deploymentName(serviceInstance));
        requestBuilder.setTimeout(300L);
        requestBuilder.setWait(true);
        requestBuilder.getValuesBuilder().setRaw(new Yaml().dump(chartConfiguration));


        return requestBuilder;
    }

    public UninstallReleaseRequest.Builder deleteDeployment(ServiceInstance serviceInstance) {
        UninstallReleaseRequest.Builder uninstallRequest = UninstallReleaseRequest.newBuilder();
        uninstallRequest.setName(deploymentName(serviceInstance));
        uninstallRequest.setPurge(true);


        return uninstallRequest;
    }

    public GetReleaseStatusRequest.Builder getDeployment(ServiceInstance serviceInstance) {
        GetReleaseStatusRequest.Builder getRequest = GetReleaseStatusRequest.newBuilder();
        getRequest.setName(deploymentName(serviceInstance));

        return getRequest;
    }

    private HashMap<String, Object> getMasterConfiguration() {
        HashMap<String, Object> port = new HashMap<>();
        HashMap<String, Object> portService = new HashMap<>();

        portService.put("type","NodePort");
        port.put("service",portService);

        return port;
    }

    private HashMap<String, Object> getSizeYaml(int size) {
        HashMap<String, Object> persistence = new HashMap<>();
        persistence.put("size", size+"Gi");

        return persistence;
    }

    public static String deploymentName(ServiceInstance instance) {
        return (DEPLOYMENT_PREFIX + instance.getId())
                    .replace("-","")
                    .toLowerCase();
    }

}
