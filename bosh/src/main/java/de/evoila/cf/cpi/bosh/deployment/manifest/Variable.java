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
    private Consumes consumes;

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

    public Consumes getConsumes() {
        return consumes;
    }

    public void setConsumes(Consumes consumes) {
        this.consumes = consumes;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    static class Consumes {
        
        private AlternativeName alternativeName;

        public AlternativeName getAlternativeName() {
            return alternativeName;
        }

        public void setAlternativeName(AlternativeName alternativeName) {
            this.alternativeName = alternativeName;
        }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class AlternativeName {
        
        private String from;
        private Properties properties;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Properties {
        
        private boolean wildcard;

        public boolean isWildcard() {
            return wildcard;
        }

        public void setWildcard(boolean wildcard) {
            this.wildcard = wildcard;
        }
    }
}