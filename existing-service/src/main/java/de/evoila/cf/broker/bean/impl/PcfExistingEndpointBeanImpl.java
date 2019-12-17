package de.evoila.cf.broker.bean.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.util.ObjectMapperUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author Yannic Remmet, Johannes Hiemer, Marco Di Martino.
 */
@Profile("pcf")
@Service
public class PcfExistingEndpointBeanImpl extends ExistingEndpoint {

    private String pcfHosts;

    public String getPcfHosts() {
        return pcfHosts;
    }

    public void setPcfHosts(String pcfHosts) throws IOException {
        List<String> pcfHostList = ObjectMapperUtils.getObjectMapper().readValue(pcfHosts, new TypeReference<List<String>>(){});
        for (String host : pcfHostList) {
            getHosts().add(new ServerAddress(getName(), host, getPort()));
        }

        this.pcfHosts = pcfHosts;
    }
}
