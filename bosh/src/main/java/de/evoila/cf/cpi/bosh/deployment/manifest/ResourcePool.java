package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;
import java.util.Map;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class ResourcePool {
    private String name;
    private String network;
    private Stemcell stemcell;
    private Map<String, Object> cloudProperties = new HashMap<>();

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getNetwork () {
        return network;
    }

    public void setNetwork (String network) {
        this.network = network;
    }

    public Stemcell getStemcell () {
        return stemcell;
    }

    public void setStemcell (Stemcell stemcell) {
        this.stemcell = stemcell;
    }

    public Map<String, Object> getCloud_properties () {
        return cloudProperties;
    }

    public void setCloud_properties (Map<String, Object> cloudProperties) {
        this.cloudProperties = cloudProperties;
    }
}
