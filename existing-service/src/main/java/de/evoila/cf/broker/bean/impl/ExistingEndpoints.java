package de.evoila.cf.broker.bean.impl;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.bean.ExistingEndpointsBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marco Di Martino.
 */

@Configuration
@ConfigurationProperties(prefix="existing")
public class ExistingEndpoints {

    private List<ExistingEndpointBean> endpoints = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean(value = ExistingEndpointsBean.class)
    public ExistingEndpointsBean getEndpoints() {
        return new ExistingEndpointsBean() {
            @Override
            public List<ExistingEndpointBean> getEndpoints() {
                return endpoints;
            }

            @Override
            public void setEndpoints(List<ExistingEndpointBean> ep) {
                endpoints = ep;
            }

            public ExistingEndpointBean findByName(String name) {
                return getEndpoints().stream().filter(e -> e.getServerName().equals(name))
                        .findFirst().orElse(null);
            }
        };
    }

}
