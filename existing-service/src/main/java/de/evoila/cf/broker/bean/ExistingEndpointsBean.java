package de.evoila.cf.broker.bean;

import java.util.List;

/**
 * @author Marco Di Martino.
 */
public interface ExistingEndpointsBean {

    ExistingEndpointBean findByName(String name);

    List<ExistingEndpointBean> getEndpoints();

    void setEndpoints(List<ExistingEndpointBean> endpoints);
}
