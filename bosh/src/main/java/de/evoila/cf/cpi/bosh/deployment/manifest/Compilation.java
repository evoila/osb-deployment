package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.HashMap;


@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Compilation {
    private int workers;
    private boolean reuseCompilationVms;
    private String network;
    private HashMap<String, Object> cloudProperties;


    public int getWorkers () {
        return workers;
    }

    public void setWorkers (int workers) {
        this.workers = workers;
    }

    public boolean isReuseCompilationVms () {
        return reuseCompilationVms;
    }

    public void setReuseCompilationVms (boolean reuseCompilationVms) {
        this.reuseCompilationVms = reuseCompilationVms;
    }

    public String getNetwork () {
        return network;
    }

    public void setNetwork (String network) {
        this.network = network;
    }

    public HashMap<String, Object> getCloudProperties () {
        return cloudProperties;
    }

    public void setCloud_properties (HashMap<String, Object> cloudProperties) {
        this.cloudProperties = cloudProperties;
    }
}
