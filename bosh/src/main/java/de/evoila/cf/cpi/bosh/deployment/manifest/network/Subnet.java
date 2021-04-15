package de.evoila.cf.cpi.bosh.deployment.manifest.network;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Subnet {
    private Map<String, Object> cloudProperties;
    private List<String> dns;
    private String gateway;
    private String range;
    private List<String> reserved;
    private List<String> staticIps;

    public Map<String, Object> getCloudProperties () {
        return cloudProperties;
    }

    public void setCloud_properties (Map<String, Object> cloudProperties) {
        this.cloudProperties = cloudProperties;
    }

    public List<String> getDns () {
        if(dns == null)
            this.dns = new ArrayList<>();
        return dns;
    }

    public void setDns (List<String> dns) {
        this.dns = dns;
    }

    public String getGateway () {
        return gateway;
    }

    public void setGateway (String gateway) {
        this.gateway = gateway;
    }

    public String getRange () {
        return range;
    }

    public void setRange (String range) {
        this.range = range;
    }

    public List<String> getReserved () {
        if(reserved == null)
            this.reserved = new ArrayList<>();
        return reserved;
    }

    public void setReserved (List<String> reserved) {
        this.reserved = reserved;
    }

    public List<String> getStaticIps () {
        if(staticIps == null)
            this.staticIps = new ArrayList<>();
        return staticIps;
    }

    public void setStaticIps (List<String> staticIps) {
        this.staticIps = staticIps;
    }
}
