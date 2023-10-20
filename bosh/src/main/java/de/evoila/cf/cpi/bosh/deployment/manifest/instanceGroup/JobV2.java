package de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(
      ignoreUnknown = true
)

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JobV2 {
    private String name;
    private String release;
    private Map<String, Consumes> consumes;
    private Map<String, Provides> provides;
    private List<CustomProvederDefenitions> customProviderDefinitions;
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

    public Map<String, Consumes> getConsumes () {
        return consumes;
    }

    public void setConsumes (Map<String, Consumes> consumes) {
        this.consumes = consumes;
    }

    public Map<String, Provides> getProvides () {
        return provides;
    }

    public void setProvides (Map<String, Provides> provides) {
        this.provides = provides;
    }
    
    public List<CustomProvederDefenitions> getCustomProviderDefinitions () {
        return customProviderDefinitions;
    }

    public void setCustomProviderDefinitions (List<CustomProvederDefenitions> customProviderDefinitions) {
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
    
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Provides {
        private String as;
        private boolean share;
        private List<Aliases> aliases;

        public String getAs() {
            return as;
        }

        public void setAs(String as) {
            this.as = as;
        }

        public boolean isShare() {
            return share;
        }

        public void setShare(boolean share) {
            this.share = share;
        }

        public List<Aliases> getAliases() {
            return aliases;
        }

        public void setAliases(List<Aliases> aliases) {
            this.aliases = aliases;
        }
    }


    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Aliases {

        public Aliases() {
        }

        public Aliases(String domain){
            this.domain = domain;
        }

        public Aliases(String domain, PlaceholderType placeholderType){
            this.domain = domain;
            this.placeholderType = placeholderType;
        }

        private String domain;
        private PlaceholderType placeholderType;

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public PlaceholderType getPlaceholderType() {
            return placeholderType;
        }

        public void setPlaceholderType(PlaceholderType placeholderType) {
            this.placeholderType = placeholderType;
        }
    }

   public static enum PlaceholderType {
       UUID,
       AVAILABILTTY_ZONE,
       NETWORK;

       @JsonValue
       public String forJackson() {
           return name().toLowerCase();
       }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Consumes {
        private String from;
        private String deployment;
        private String network;
        private boolean ipAddresses;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getDeployment() {
            return deployment;
        }

        public void setDeployment(String deployment) {
            this.deployment = deployment;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public boolean isIpAddresses() {
            return ipAddresses;
        }

        public void setIpAddresses(boolean ipAddresses) {
            this.ipAddresses = ipAddresses;
        }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class CustomProvederDefenitions {
        private String name;
        private String type;
        private List<String> properties;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getProperties() {
            return properties;
        }

        public void setProperties(List<String> properties) {
            this.properties = properties;
        }
    }
}
