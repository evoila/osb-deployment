package de.evoila.cf.cpi.bosh.deployment.manifest.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import de.evoila.cf.cpi.bosh.deployment.manifest.network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Job {
    private String name;
    private int persistentDisk;
    private int instances;
    private String resourcePool;
    private List<Template> templates;
    private List<Network> networks;
    private Map<String, Object> properties;



    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getPersistentDisk () {
        return persistentDisk;
    }

    public void setPersistentDisk (int persistentDisk) {
        this.persistentDisk = persistentDisk;
    }

    public int getInstances () {
        return instances;
    }

    public void setInstances (int instances) {
        this.instances = instances;
    }

    public String getResourcePool () {
        return resourcePool;
    }

    public void setResourcePool (String resourcePool) {
        this.resourcePool = resourcePool;
    }

    public List<Template> getTemplates () {
        if(templates == null)
            templates = new ArrayList<>();
        return templates;
    }

    public void setTemplates (List<Template> templates) {
        this.templates = templates;
    }

    public List<Network> getNetworks () {
        if(networks == null)
            networks = new ArrayList<>();
        return networks;
    }

    public void setNetworks (List<Network> networks) {
        this.networks = networks;
    }

    public Map<String, Object> getProperties () {
        if(properties == null)
            properties = new HashMap<>();
        return properties;
    }

    public void setProperties (Map<String, Object> properties) {
        this.properties = properties;
    }
}
