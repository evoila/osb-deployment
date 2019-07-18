package de.evoila.cf.cpi.bosh.deployment.manifest;

/**
 * @author Christian Brinker
 */
public class Variable {

    private String name;

    private String type;

    private VariableOptions options;

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

    public VariableOptions getOptions() {
        return options;
    }

    public void setOptions(VariableOptions options) {
        this.options = options;
    }
}
