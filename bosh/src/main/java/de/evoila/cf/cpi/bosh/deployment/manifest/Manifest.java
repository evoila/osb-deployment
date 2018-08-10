package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.evoila.cf.cpi.bosh.deployment.manifest.addon.Addon;
import de.evoila.cf.cpi.bosh.deployment.manifest.job.Job;
import de.evoila.cf.cpi.bosh.deployment.manifest.network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Manifest {

    private String name;

    private List<Release> releases = new ArrayList<>();

    private Update update;

    private List<Stemcell> stemcells = new ArrayList<>();

    @JsonProperty("instance_groups")
    private List<InstanceGroup> instanceGroups = new ArrayList<>();

    private Map<String, Object> properties = new HashMap<>();

    private Compilation compilation;

    private List<Job> jobs = new ArrayList<>();

    private List<Network> networks = new ArrayList<>();

    @JsonProperty("resource_pools")
    private List<ResourcePool> resourcePools = new ArrayList<>();

    private List<Addon> addons = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public List<Stemcell> getStemcells() {
        return stemcells;
    }

    public void setStemcells(List<Stemcell> stemcells) {
        this.stemcells = stemcells;
    }

    public List<InstanceGroup> getInstanceGroups() {
        return instanceGroups;
    }

    public void setInstanceGroups(List<InstanceGroup> instanceGroups) {
        this.instanceGroups = instanceGroups;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public List<Job> getJobs() { return jobs; }

    public void setJobs(List<Job> jobs) { this.jobs = jobs; }

    public List<Network> getNetworks() { return networks; }

    public void setNetworks(List<Network> networks) { this.networks = networks; }

    public List<ResourcePool> getResourcePools() {
        return resourcePools;
    }

    public void setResourcePools(List<ResourcePool> resourcePools) {
        this.resourcePools = resourcePools;
    }

    public Compilation getCompilation() { return compilation; }

    public void setCompilation(Compilation compilation) { this.compilation = compilation; }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }
}
