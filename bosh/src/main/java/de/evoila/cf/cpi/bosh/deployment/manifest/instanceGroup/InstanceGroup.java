package de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup;

import ch.qos.logback.core.spi.LifeCycle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.evoila.cf.broker.model.NetworkReference;
import de.evoila.cf.cpi.bosh.deployment.manifest.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceGroup {
    private String name;
    private List<String> azs;
    private int instances;
    private List<JobV2> jobs;
    private String stemcell;
    private List<NetworkReference> networks;
    private Update update;
    private LifeCycle lifecycle;
    private Map<String, Object> properties;
    private String vm_type;
    private String persistent_disk_type;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public List<String> getAzs () {
        if(azs == null)
            azs = new ArrayList<>();
        return azs;
    }

    public void setAzs (List<String> azs) {
        this.azs = azs;
    }

    public int getInstances () {
        return instances;
    }

    public void setInstances (int instances) {
        this.instances = instances;
    }

    public List<JobV2> getJobs () {
        if(jobs == null)
            jobs = new ArrayList<>();
        return jobs;
    }

    public void setJobs (List<JobV2> jobs) {
        this.jobs = jobs;
    }

    public String getStemcell () {
        return stemcell;
    }

    public void setStemcell (String stemcell) {
        this.stemcell = stemcell;
    }

    public List<NetworkReference> getNetworks () {
        if(networks == null)
            networks = new ArrayList<>();
        return networks;
    }

    public void setNetworks (List<NetworkReference> networks) {
        this.networks = networks;
    }

    public Update getUpdate () {
        return update;
    }

    public void setUpdate (Update update) {
        this.update = update;
    }

    public LifeCycle getLifecycle () {
        return lifecycle;
    }

    public void setLifecycle (LifeCycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Map<String, Object> getProperties () {
        if(properties == null)
            properties = new HashMap<>();
        return properties;
    }

    public void setProperties (Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getVm_type() {
        return vm_type;
    }

    public void setVm_type(String vm_type) {
        this.vm_type = vm_type;
    }

    public String getPersistent_disk_type() {
        return persistent_disk_type;
    }

    public void setPersistent_disk_type(String persistent_disk_type) {
        this.persistent_disk_type = persistent_disk_type;
    }
}
