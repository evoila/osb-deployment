package de.evoila.cf.broker.bean;

import io.bosh.client.Authentication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Rene Schollmeyer.
 */
@Configuration
@ConfigurationProperties(prefix = "bosh")
@ConditionalOnProperty(prefix = "bosh", name = {"host", "username",
        "password", "stemcellVersion", "stemcellOs", "authentication"})
public class BoshProperties {

    private String host;

    private int port = 25555;

    private String username;

    private String password;

    private String stemcellVersion;

    private String stemcellOs;
    
    private boolean recreateManifest = false;

    private Authentication authentication;

    private String vipNetwork = "not-defined";

    public String getHost() { return host; }

    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }

    public void setPort(int port) { this.port = port;  }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getStemcellVersion() { return stemcellVersion; }

    public void setStemcellVersion(String stemcellVersion) { this.stemcellVersion = stemcellVersion; }

    public String getStemcellOs() { return stemcellOs; }

    public void setStemcellOs(String stemcellOs) { this.stemcellOs = stemcellOs; }

    public Authentication getAuthentication() { return authentication; }

    public void setAuthentication(Authentication authentication) { this.authentication = authentication; }

    public String getVipNetwork() { return vipNetwork; }

    public void setVipNetwork(String vipNetwork) { this.vipNetwork = vipNetwork; }
    
    public boolean isRecreateManifest() { return recreateManifest; }

    public void setRecreateManifest(boolean recreateManifest) { this.recreateManifest = recreateManifest; }
}
