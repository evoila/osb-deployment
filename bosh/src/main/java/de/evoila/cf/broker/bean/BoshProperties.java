package de.evoila.cf.broker.bean;

import io.bosh.client.authentication.Authentication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * Created by reneschollmeyer, evoila on 09.10.17.
 */
@Service
@ConfigurationProperties(prefix = "bosh")
@ConditionalOnProperty(prefix = "bosh", name = {"host", "username", "password", "stemcellVersion", "stemcellOs", "authentication"}, havingValue = "")
public class BoshProperties {

    private String host;

    private String username;

    private String password;

    private String stemcellVersion;

    private String stemcellOs;

    private Authentication authentication;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStemcellVersion() {
        return stemcellVersion;
    }

    public void setStemcellVersion(String stemcellVersion) {
        this.stemcellVersion = stemcellVersion;
    }

    public String getStemcellOs() {
        return stemcellOs;
    }

    public void setStemcellOs(String stemcellOs) {
        this.stemcellOs = stemcellOs;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
