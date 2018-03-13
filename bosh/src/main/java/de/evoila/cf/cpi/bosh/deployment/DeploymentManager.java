package de.evoila.cf.cpi.bosh.deployment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.evoila.cf.broker.bean.BoshProperties;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.cpi.bosh.deployment.manifest.InstanceGroup;
import de.evoila.cf.cpi.bosh.deployment.manifest.Manifest;
import de.evoila.cf.cpi.bosh.deployment.manifest.Stemcell;
import io.bosh.client.deployments.Deployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class DeploymentManager {

    public static final String NODES = "nodes";
    public static final String VM_TYPE = "vm_type";
    public static final String NETWORKS = "networks";
    public static final String DISK_TYPE = "disk_type";
    public static final String STEMCELL_VERSION = "stemcell_version";
    private static final String DEPLOYMENT_PREFIX = "sb-";
    private final ObjectReader reader;
    private final ObjectMapper mapper;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final BoshProperties boshProperties;

    public DeploymentManager(BoshProperties properties) {
        Assert.notNull(properties, "Bosh Properties cant be null");
        this.mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        this.boshProperties = properties;
        this.reader = mapper.readerFor(Manifest.class);
    }

    protected void replaceParameters(ServiceInstance instance, Manifest manifest, Plan plan, Map<String, String> customParameters) {
        manifest.getProperties().putAll(plan.getMetadata());
        manifest.getProperties().putAll(customParameters);
    }

    public Deployment createDeployment(ServiceInstance instance, Plan plan, Map<String, String> customParameters) throws IOException {
        Deployment deployment = getDeployment(instance);
        Manifest manifest = readTemplate("bosh/manifest.yml");
        manifest.setName(DEPLOYMENT_PREFIX + instance.getId());
        addStemcell(manifest);
        replaceParameters(instance, manifest,plan, customParameters);
        deployment.setRawManifest(generateManifest(manifest));
        return deployment;
    }

    private void addStemcell(Manifest manifest) {
        Optional<Stemcell> stemcellOptional = manifest.getStemcells().stream().filter(s -> s.getAlias().equals("default")).findFirst();
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
        return mapper.readValue(manifest,Manifest.class);
    }

    public String generateManifest(Manifest manifest) throws JsonProcessingException {
        return mapper.writeValueAsString(manifest);
    }

    public Deployment updateDeployment (ServiceInstance instance, Deployment deployment, Plan plan) throws IOException {
        Manifest manifest = mapper.readValue(deployment.getRawManifest(), Manifest.class);
        replaceParameters(instance, manifest, plan, new HashMap<>());
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

    protected void updateInstanceGroupConfiguration(Manifest manifest, Plan plan) {
        Map<String, Object> metadata = plan.getMetadata();

        for(InstanceGroup instanceGroup : manifest.getInstance_groups()) {
            updateSpecificInstanceGroupConfiguration(instanceGroup, metadata);

            if(metadata.containsKey(instanceGroup.getName())) {
                updateSpecificInstanceGroupConfiguration(instanceGroup,
                        (Map<String, Object>) metadata.get(instanceGroup.getName()));
            }
        }
    }

    private void updateSpecificInstanceGroupConfiguration(InstanceGroup instanceGroup, Map<String, Object> instanceGroupData) {
        if(instanceGroupData.containsKey(NODES)) {
            instanceGroup.setInstances((Integer) instanceGroupData.get(NODES));
        }

        if(instanceGroupData.containsKey(VM_TYPE)) {
            instanceGroup.setVm_type((String) instanceGroupData.get(VM_TYPE));
        }

        if(instanceGroupData.containsKey(NETWORKS)) {
            instanceGroup.setNetworksFromMap((LinkedHashMap<String, String>) instanceGroupData.get(NETWORKS));
        }

        if(instanceGroupData.containsKey(DISK_TYPE)) {
            instanceGroup.setPersistent_disk_type((String) instanceGroupData.get(DISK_TYPE));
        }
    }

    public Deployment getDeployment(ServiceInstance serviceInstance) {
        Deployment deployment = new Deployment();
        deployment.setName(DeploymentManager.deploymentName(serviceInstance));
        return deployment;
    }

    public static String deploymentName(ServiceInstance instance) {
        return DEPLOYMENT_PREFIX + instance.getId();
    }
}
