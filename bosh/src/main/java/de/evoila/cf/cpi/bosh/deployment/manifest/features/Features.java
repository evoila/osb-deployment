package de.evoila.cf.cpi.bosh.deployment.manifest.features;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Features {

    private boolean useDnsAddresses;
    private boolean convergeVariables;
    private boolean randomizeAzPlacement;
    private boolean useTmpfsConfig;
    private boolean useShortDnsAddresses;

    public boolean isUseDnsAddresses() {
        return useDnsAddresses;
    }

    public void  setUseDnsAddresses(boolean useDnsAddesses) {
        this.useDnsAddresses = useDnsAddesses;
    }

    public boolean isConvergeVariables() {
        return convergeVariables;
    }

    public void setConvergeVariables(boolean convergeVariables) {
        this.convergeVariables = convergeVariables;
    }

    public boolean isRandomizeAzPlacement() {
        return randomizeAzPlacement;
    }

    public void setRandomizeAzPlacement(boolean randomizeAzPlacement) {
        this.randomizeAzPlacement = randomizeAzPlacement;
    }

    public void setUseTmpfsConfig(boolean useTmpfsConfig) {
        this.useTmpfsConfig = useTmpfsConfig;
    }

    public boolean isUseShortDnsAddresses() {
        return useShortDnsAddresses;
    }

    public void setUseShortDnsAddresses(boolean useShortDnsAddresses) {
        this.useShortDnsAddresses = useShortDnsAddresses;
    }

    public boolean isUseTmpfsConfig() {
        return useTmpfsConfig;
    }
}
