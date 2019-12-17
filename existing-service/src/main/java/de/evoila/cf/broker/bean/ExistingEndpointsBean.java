package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.impl.ExistingEndpoint;

import java.util.List;

/**
 * @author Marco Di Martino.
 */
public interface ExistingEndpointsBean {

    ExistingEndpoint findByName(String name);

    List<ExistingEndpoint> getEndpoints();

    void setEndpoints(List<ExistingEndpoint> endpoints);
}
