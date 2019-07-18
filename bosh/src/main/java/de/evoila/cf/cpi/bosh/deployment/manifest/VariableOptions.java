package de.evoila.cf.cpi.bosh.deployment.manifest;

import java.util.List;

/**
 * @author Christian Brinker
 */
public class VariableOptions {
    private String common_name;

    private List<String> alternative_names;

    private boolean is_ca;

    private List<String> extended_key_usage;

    public String getCommon_name() {
        return common_name;
    }

    public void setCommon_name(String common_name) {
        this.common_name = common_name;
    }

    public List<String> getAlternative_names() {
        return alternative_names;
    }

    public void setAlternative_names(List<String> alternative_names) {
        this.alternative_names = alternative_names;
    }

    public boolean isIs_ca() {
        return is_ca;
    }

    public void setIs_ca(boolean is_ca) {
        this.is_ca = is_ca;
    }

    public List<String> getExtended_key_usage() {
        return extended_key_usage;
    }

    public void setExtended_key_usage(List<String> extended_key_usage) {
        this.extended_key_usage = extended_key_usage;
    }
}
