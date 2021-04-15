package de.evoila.cf.cpi.bosh.deployment.manifest;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * @author Christian Brinker
 */

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
