package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.evoila.cf.broker.model.NetworkReference;
import de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup.JobV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceGroup {
    private String name;
    private int instances;
    private int connections;
    private String vm_type;
    private String stemcell;
    private String lifecycle;
    private String persistent_disk_type;
    private int persistent_disk;
    private List<String> azs;
    private List<NetworkReference> networks;
    private List<JobV2> jobs;
    private Map<String, Object> properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public String getVm_type() {
        return vm_type;
    }

    public void setVm_type(String vm_type) {
        this.vm_type = vm_type;
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

    public String getPersistent_disk_type() {
        return persistent_disk_type;
    }

    public void setPersistent_disk_type(String persistent_disk_type) {
        this.persistent_disk_type = persistent_disk_type; 
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

    public void setNetworks(List<NetworkReference> networks) { this.networks = networks; }

    public List<JobV2> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobV2> jobs) {
        this.jobs = jobs;
    }

    public Map<String, Object> getProperties() { return properties; }

    public void setProperties(Map<String, Object> properties) { this.properties = properties; }

    public int getPersistent_disk() {
        return persistent_disk;
    }

    public void setPersistent_disk(int persistent_disk) {
        this.persistent_disk = persistent_disk;
    }

    public void setNetworksFromMap(HashMap<String, NetworkReference> networks) {
        this.networks = new ArrayList<>(networks.values());
    }
}
