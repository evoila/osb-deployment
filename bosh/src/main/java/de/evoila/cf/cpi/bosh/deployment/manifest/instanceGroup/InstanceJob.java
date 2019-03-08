package de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup;

import ch.qos.logback.core.spi.LifeCycle;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.broker.model.catalog.plan.NetworkReference;
import de.evoila.cf.broker.model.volume.VolumeUnit;
import de.evoila.cf.cpi.bosh.deployment.manifest.Update;

import java.util.List;
import java.util.Map;

/**
 * @author Jannik Heyl, Johannes Hiemer.
 */
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class InstanceJob {

    private String name;

    private List<String> azs;

    private int instances;

    private List<JobV2> jobs;

    private String stemcell;

    private List<NetworkReference> networks;

    private Update update;

    private ch.qos.logback.core.spi.LifeCycle lifecycle;

    private Map<String, Object> properties;

    @JsonProperty(value = "vm_type")
    private String vmType;

    @JsonProperty(value = "persistent_disk_type")
    private String persistentDiskType;

    @JsonProperty(value = "persistent_disk")
    private Integer persistentDisk;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAzs() {
        return azs;
    }

    public void setAzs(List<String> azs) {
        this.azs = azs;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }

    public List<JobV2> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobV2> jobs) {
        this.jobs = jobs;
    }

    public String getStemcell() {
        return stemcell;
    }

    public void setStemcell(String stemcell) {
        this.stemcell = stemcell;
    }

    public List<NetworkReference> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NetworkReference> networks) {
        this.networks = networks;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public LifeCycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(LifeCycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getVmType() {
        return vmType;
    }

    public void setVmType(String vmType) {
        this.vmType = vmType;
    }

    public String getPersistentDiskType() {
        return persistentDiskType;
    }

    public void setPersistentDiskType(String persistentDiskType) {
        this.persistentDiskType = persistentDiskType;
    }

    public Integer getPersistentDisk() {
        return persistentDisk;
    }

    public void setPersistentDisk(Integer persistentDisk) {
        this.persistentDisk = persistentDisk;
    }

    public void getPersistentDisk(Integer persistent_disk) {
        this.persistentDisk = persistentDisk;
    }

    public void setPersistentDisk(Integer persistent_disk, VolumeUnit volumeUnit){
        switch (volumeUnit){
            case M:
                this.persistentDisk = persistentDisk;
                break;
            case G:
                this.persistentDisk = persistentDisk *1000;
                break;
            case T:
                this.persistentDisk = persistentDisk *1000000;
                break;
        }

    }
}
