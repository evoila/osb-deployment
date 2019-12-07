package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Christian Brinker
 */
public class VariableOptions {

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("alternative_names")
    private List<String> alternativeNames;

    @JsonProperty("is_ca")
    private boolean isCa;
    
    @JsonProperty("ca")
    private String ca;

    @JsonProperty("extended_key_usage")
    private List<String> extendedKeyUsage;

    @JsonProperty("username")
    private String username;

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public List<String> getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(List<String> alternativeNames) {
        this.alternativeNames = alternativeNames;
    }

    public String getCa() {
        return ca;
    }

    public void setCa(String ca) {
        this.ca = ca;
    }

    public boolean isIsCa() {
        return isCa;
    }

    public void setIsCa(boolean ca) {
        this.isCa = ca;
    }

    public List<String> getExtendedKeyUsage() {
        return extendedKeyUsage;
    }

    public void setExtendedKeyUsage(List<String> extendedKeyUsage) {
        this.extendedKeyUsage = extendedKeyUsage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
