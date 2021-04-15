package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Update {
    private int canaries;
    private String canaryWatchTime;
    private String updateWatchTime;

    private int maxInFlight;
    private boolean serial;

    public int getCanaries () {
        return canaries;
    }

    public void setCanaries (int canaries) {
        this.canaries = canaries;
    }

    public String getCanaryWatchTime () {
        return canaryWatchTime;
    }

    public void setCanaryWatchTime (String canaryWatchTime) {
        this.canaryWatchTime = canaryWatchTime;
    }

    public String getUpdateWatchTime () {
        return updateWatchTime;
    }

        public void setUpdateWatchTime (String updateWatchTime) {
        this.updateWatchTime = updateWatchTime;
    }

    public int getMaxInFlight () {
        return maxInFlight;
    }

    public void setMaxInFlight (int maxInFlight) {
        this.maxInFlight = maxInFlight;
    }

    public boolean isSerial () {
        return serial;
    }

    public void setSerial (boolean serial) {
        this.serial = serial;
    }
}
