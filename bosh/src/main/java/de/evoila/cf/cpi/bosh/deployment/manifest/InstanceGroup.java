package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import de.evoila.cf.broker.model.catalog.plan.NetworkReference;
import de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup.JobV2;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InstanceGroup {
    
    private String name;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Integer instances;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Integer connections;

    private String vmType;

    private String stemcell;

    private String lifecycle;

    private String persistentDiskType;

    private int persistentDisk;

    private List<String> azs = new ArrayList<>();

    private List<NetworkReference> networks = new ArrayList<>();

    private List<JobV2> jobs = new ArrayList<>();

    private Map<String, Object> properties = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public Integer getConnections() {
        return connections;
    }

    public void setConnections(Integer connections) {
        this.connections = connections;
    }

    public String getVmType() {
        return vmType;
    }

    public void setVmType(String vmType) {
        this.vmType = vmType;
    }

    public String getStemcell() {
        return stemcell;
    }

    public void setStemcell(String stemcell) {
        this.stemcell = stemcell;
    }

    public String getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(String lifecycle) {
        this.lifecycle = lifecycle;
    }

    public String getPersistentDiskType() {
        return persistentDiskType;
    }

    public void setPersistentDiskType(String persistentDiskType) {
        this.persistentDiskType = persistentDiskType;
    }

    public int getPersistentDisk() {
        return persistentDisk;
    }

    public void setPersistentDisk(int persistentDisk) {
        this.persistentDisk = persistentDisk;
    }

    public List<String> getAzs() {
        return azs;
    }

    public void setAzs(List<String> azs) {
        this.azs = azs;
    }

    public List<NetworkReference> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NetworkReference> networks) {
        this.networks = networks;
    }

    public List<JobV2> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobV2> jobs) {
        this.jobs = jobs;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Optional<JobV2> getJob(String name){
        return getJobs().stream().filter(ig ->  ig.getName().equals(name)).findFirst();
    }
}
