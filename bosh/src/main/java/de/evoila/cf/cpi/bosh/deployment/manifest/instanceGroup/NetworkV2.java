package de.evoila.cf.cpi.bosh.deployment.manifest.instanceGroup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(
      ignoreUnknown = true
)
public class NetworkV2 {
    String name;
    List<String> static_ips;

    public NetworkV2() {}

    public NetworkV2(String network) {
        this.name = network;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public List<String> getStatic_ips () {
        if(static_ips == null)
            static_ips = new ArrayList<>();
        return static_ips;
    }

    public void setStatic_ips (List<String> static_ips) {
        this.static_ips = static_ips;
    }
}
