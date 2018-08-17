package de.evoila.cf.cpi.bosh.deployment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.util.GlobalConstants;
import de.evoila.cf.broker.util.MapUtils;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.cpi.bosh.deployment.manifest.Stemcell;
import io.bosh.client.deployments.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeploymentManager {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private static String DEPLOYMENT_PREFIX = "sb-";

    protected static final String NODES = "nodes";

    protected static final String VM_TYPE = "vm_type";

    protected static final String NETWORKS = "networks";

    protected static final String DISK_TYPE = "persistent_disk_type";

    protected static final String STEMCELL_VERSION = "stemcell_version";

    private final ObjectReader reader;
    private final ObjectMapper mapper;

    protected final BoshProperties boshProperties;

    public DeploymentManager(BoshProperties properties, Environment environment) {
        Assert.notNull(properties, "Bosh Properties cant be null");
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        this.boshProperties = properties;
        this.reader = mapper.readerFor(Manifest.class);

        if (environment != null) {
            if (Arrays.stream(environment.getActiveProfiles()).anyMatch(
                    env -> (env.equalsIgnoreCase(GlobalConstants.TEST_PROFILE)))) {
                this.DEPLOYMENT_PREFIX = "sb-test-";
            }
        }
    }

    protected void replaceParameters(ServiceInstance instance, Manifest manifest, Plan plan, Map<String, Object> customParameters) {
        manifest.getProperties().putAll(customParameters);
    }

    public Deployment createDeployment(ServiceInstance instance, Plan plan, Map<String, Object> customParameters) throws IOException {
        Deployment deployment = getDeployment(instance);

        Manifest manifest = readTemplate("bosh/manifest.yml");
        manifest.setName(DEPLOYMENT_PREFIX + instance.getId());
        addStemcell(manifest);
        replaceParameters(instance, manifest, plan, customParameters);

        deployment.setRawManifest(generateManifest(manifest));
        return deployment;
    }

    private void addStemcell(Manifest manifest) {
        Optional<Stemcell> stemcellOptional = manifest.getStemcells()
                .stream()
                .filter(s -> s.getAlias().equals("default")).findFirst();

        Stemcell defaultStemcell;

        if(stemcellOptional.isPresent()){
            defaultStemcell = stemcellOptional.get();
            defaultStemcell.setVersion(boshProperties.getStemcellVersion());
            defaultStemcell.setOs(boshProperties.getStemcellOs());
        } else {
            defaultStemcell = new Stemcell("default", boshProperties.getStemcellVersion(), boshProperties.getStemcellOs());
            manifest.getStemcells().add(defaultStemcell);
        }
    }

    public Manifest readTemplate(String path) throws IOException {
        String manifest = accessTemplate(path);
        return readManifestFromString(manifest);
    }

    public Manifest readManifestFromString(String manifest) throws IOException {
        return mapper.readValue(manifest, Manifest.class);
    }

    public String generateManifest(Manifest manifest) throws JsonProcessingException {
        return mapper.writeValueAsString(manifest);
    }

    public Deployment updateDeployment (ServiceInstance instance, Deployment deployment, Plan plan, Map<String, Object> customParameters) throws IOException {
        Manifest manifest = mapper.readValue(deployment.getRawManifest(), Manifest.class);

        log.debug("Updating deployment: " + deployment.getRawManifest());

        replaceParameters(instance, manifest, plan, customParameters);

        deployment.setRawManifest(generateManifest(manifest));
        return deployment;
    }

    private String accessTemplate(final String templatePath) throws IOException {
        InputStream inputStream = new ClassPathResource(templatePath).getInputStream();
        return this.readTemplateFile(inputStream);
    }

    private String readTemplateFile(InputStream inputStream) throws IOException {
        BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
            line = reader.readLine();
        }
        return stringBuilder.toString();
    }

    protected InstanceGroup getInstanceGroup(Manifest manifest, String name) {
        return manifest.getInstanceGroups()
                .stream()
                .filter(i -> i.getName().equals(name))
                .findAny().get();
    }

    protected void updateInstanceGroupConfiguration(Manifest manifest, Plan plan) {
        Metadata metadata = plan.getMetadata();

        for(InstanceGroup instanceGroup : manifest.getInstanceGroups()) {
            if(metadata != null) {
                updateSpecificInstanceGroupConfiguration(instanceGroup, metadata);


                InstanceGroupConfig instanceGroupConfig = metadata.getInstanceGroupConfig().stream()
                        .filter(i -> i.getName() != null && i.getName().equals(instanceGroup.getName()))
                        .findFirst()
                        .orElse(null);

                if(metadata.getInstanceGroupConfig() != null && instanceGroupConfig != null) {
                    updateSpecificInstanceGroupConfiguration(instanceGroup, instanceGroupConfig);
                }
            }
        }
    }

    private void updateSpecificInstanceGroupConfiguration(InstanceGroup instanceGroup, InstanceGroupConfig instanceGroupConfig) {
        if (instanceGroupConfig.getConnections() != null)
            instanceGroup.setConnections(instanceGroupConfig.getConnections());

        if (instanceGroupConfig.getNodes() != null)
            instanceGroup.setInstances(instanceGroupConfig.getNodes());

        if (instanceGroupConfig.getVmType() != null)
            instanceGroup.setVmType(instanceGroupConfig.getVmType());

        if (instanceGroupConfig.getPersistentDiskType() != null)
            instanceGroup.setPersistentDiskType(instanceGroupConfig.getPersistentDiskType());

        if (instanceGroupConfig.getProperties() != null && instanceGroupConfig.getProperties().size() > 0)
            MapUtils.deepMerge(instanceGroup.getProperties(), instanceGroupConfig.getProperties());

        /**
         * Note: it is really important to understand the behaviour of the following method. It only
         * replaces networks, that are NOT a floating network (see bosh cloud-config type VIP). The only
         * exception for a replacement of VIP network is, if the manifests does not yet contain a Static IP.
         * Then it is set.
         */
        if(instanceGroupConfig.getNetworks() != null) {
            List<NetworkReference> newNetworks = instanceGroup
                    .getNetworks()
                    .stream()
                    .map(n -> {
                        for (NetworkReference networkReference : instanceGroupConfig.getNetworks()) {
                            if (!n.getName().equals(boshProperties.getVipNetwork()) &&
                                    !networkReference.getName().equals(boshProperties.getVipNetwork())) {
                                return networkReference;
                            } else if (networkReference.getName().equals(boshProperties.getVipNetwork()) &&
                                    n.getStaticIps().isEmpty())
                                return networkReference;
                        }
                        return n;
                    }).collect(Collectors.toList());
            instanceGroup.setNetworks(newNetworks);
        }

        if (instanceGroupConfig.getAzs() != null && instanceGroupConfig.getAzs().size() > 0)
            instanceGroup.setAzs(instanceGroupConfig.getAzs());
    }

    public Deployment getDeployment(ServiceInstance serviceInstance) {
        Deployment deployment = new Deployment();
        deployment.setName(DeploymentManager.deploymentName(serviceInstance));
        return deployment;
    }

    public static String deploymentName(ServiceInstance instance) {
        return DEPLOYMENT_PREFIX + instance.getId();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
