package de.evoila.cf.broker.bean.impl;

import de.evoila.cf.broker.bean.ExistingEndpointsBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Di Martino.
 */

@Profile("pcf")
@Service()
public class PcfExistingEndpointsImpl implements ExistingEndpointsBean {

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
