package de.evoila.cf.broker.bean.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix="existing")
public class ExistingEndpoints {

    private List<ExistingEndpoint> endpoints = new ArrayList<>();

    public List<ExistingEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<ExistingEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public ExistingEndpoint findByName(String name) {
        return getEndpoints().stream().filter(e -> e.getServerName().equals(name))
                .findFirst().orElse(null);
    }
}
