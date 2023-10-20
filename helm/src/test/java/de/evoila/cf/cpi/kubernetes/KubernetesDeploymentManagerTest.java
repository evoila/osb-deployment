package de.evoila.cf.cpi.kubernetes;

import de.evoila.cf.broker.bean.KubernetesApi;
import de.evoila.cf.broker.bean.KubernetesCertificateData;
import de.evoila.cf.broker.bean.KubernetesCertificates;
import de.evoila.cf.broker.bean.KubernetesProperties;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.cpi.kubernetes.deployment.KubernetesDeploymentManager;
import io.fabric8.kubernetes.api.model.Service;
import org.springframework.core.env.Environment;

import java.util.HashMap;

/**
 * @author Johannes Hiemer.
 */
public class KubernetesDeploymentManagerTest {

  //  @Test
    public void runLoadBalancerDeployment() throws Exception {
        KubernetesProperties kubernetesProperties = new KubernetesProperties();
        kubernetesProperties.setMaster("https://192.168.99.100:8443");
        KubernetesApi kubernetesApi = new KubernetesApi();
        kubernetesApi.setVersion("v1");
        kubernetesProperties.setApi(kubernetesApi);

        KubernetesCertificates kubernetesCertificates = new KubernetesCertificates();
        KubernetesCertificateData kubernetesCertificateData = new KubernetesCertificateData();
        kubernetesCertificateData.setData("""
                -----BEGIN CERTIFICATE-----
                MIIC5zCCAc+gAwIBAgIBATANBgkqhkiG9w0BAQsFADAVMRMwEQYDVQQDEwptaW5p
                a3ViZUNBMB4XDTE4MTIwNjE1MzIwMloXDTI4MTIwNDE1MzIwMlowFTETMBEGA1UE
                AxMKbWluaWt1YmVDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALJJ
                2dxJkPyr0pZ5QHu6ujS/ccwGPQwhYbJFIZrvwqGkIJNKGQ1C+MvYKZwQmDQiTeED
                mYpoBXsw+MpdYcbP9CTormzBq4f9tAPLaOmpAjssBWRj/ZOQG6yMlTydawPAmHqF
                CQ2AHgFDKUiZXJqtEQHZKPSAkI6vOVqvfOjYUj67Y+GpvBDM2wPIdcXyiYH8eTQ+
                m5wPkrdx7qRkJxRHM2DOnfQUWgtUH7Jth7O08XE2zc9BiilAD2ARggX1F6VgUx/Y
                cveyd28C4EQI5JdPLW2YdhNsaI7Z5M5eZjm/R+xP9ucJNN8yaolLOUeJ2xslfTEX
                heRFe8dQk4BsKhSEIA0CAwEAAaNCMEAwDgYDVR0PAQH/BAQDAgKkMB0GA1UdJQQW
                MBQGCCsGAQUFBwMCBggrBgEFBQcDATAPBgNVHRMBAf8EBTADAQH/MA0GCSqGSIb3
                DQEBCwUAA4IBAQCex9NQqOGWlHOLA/dWOn93pbcMXCuxrFeglFWBPNzKQL+aQpGc
                1gOpGlwgoSeDKIkDONj0JkWFbcFb6An8FKLLXdR7Sjkxn5k+CjeECHpW7DSvu826
                GeFSA7gbScR8qRUfUx728a5e91RDBItJF5LOTF31KZa3rSWmKxRHe97RIURhZP9w
                Ilt1kl7tvja3oocbhWtZ8nS20zezE6FTJ+uxiAqM9twHj7iLrl5Lq0MG2F+qTqwX
                9OTkc57UvCKf2aL5phW2BIuGPBc65FgsDNUqLuuZT/72W168agH5JlZlrrBeTrBv
                T9UJn9voKmdzmlVhcC4HvdriUYp+6UtmV6aS
                -----END CERTIFICATE-----\
                """);
        kubernetesCertificates.setCa(kubernetesCertificateData);
        kubernetesProperties.setCerts(kubernetesCertificates);

        Environment environment = null;

        KubernetesPlatformService kubernetesPlatformService = new KubernetesPlatformService(null,
                null,
                kubernetesProperties, null,
                new KubernetesDeploymentManager(kubernetesProperties, environment, true),
                environment, true) {
            @Override
            protected void updateHosts(ServiceInstance serviceInstance, Plan plan, Service service) {

            }
        };

        ServiceInstance serviceInstance = new ServiceInstance("id", "serviceDefinitionId", "planId",
                "organizationGuid","spaceGuid", new HashMap<String, Object>(), "dashboardUrl");
        kubernetesPlatformService.deployLoadBalancer(serviceInstance, 6379);
    }
}
