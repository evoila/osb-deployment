package de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@JsonIgnoreProperties(
      ignoreUnknown = true
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JobV2 {
    private String name;
    private String release;
    private Map<String, Object> consumes;
    private Map<String, Object> provides;
    private ArrayList<Object> customProviderDefinitions;
    private Map<String, Object> properties;
    private String vmType;
    private String vmExtensions;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getRelease () {
        return release;
    }

    public void setRelease (String release) {
        this.release = release;
    }

    public Map<String, Object> getConsumes () {
        return consumes;
    }

    public void setConsumes (Map<String, Object> consumes) {
        this.consumes = consumes;
    }

    public Map<String, Object> getProvides () {
        return provides;
    }

    public void setProvides (Map<String, Object> provides) {
        this.provides = provides;
    }
    
    public ArrayList<Object> getCustomProviderDefinitions () {
        return customProviderDefinitions;
    }

    public void setCustomProviderDefinitions (ArrayList<Object> customProviderDefinitions) {
        this.customProviderDefinitions = customProviderDefinitions;
    }

    public Map<String, Object> getProperties () {
        return properties;
    }

    public void setProperties (Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getVmType () {
        return vmType;
    }

    public void setVmType (String vmType) {
        this.vmType = vmType;
    }

    public String getVmExtensions () {
        return vmExtensions;
    }

    public void setVmExtensions (String vm_extensions) {
        this.vmExtensions = vmExtensions;
    }
}
