package de.evoila.cf.cpi.bosh.connection;

import io.bosh.client.Authentication;
import io.bosh.client.DirectorClient;
import io.bosh.client.Scheme;
import io.bosh.client.SpringDirectorClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author Rene Schollmeyer, Johannes Hiemer.
 */
public class BoshClient {

    private final static Logger log = LoggerFactory.getLogger(BoshClient.class);

    protected DirectorClient directorClient;

    private String host;

    private String username;

    private String password;

    private Authentication authentication;

    private Integer port;

    public BoshClient(String username, String password, String host, int port, Authentication authentication){
        Assert.notNull(host, "Bosh Director Host may not be empty, when initializing");
        Assert.notNull(username, "Bosh Director Username may not be empty, when initializing");
        Assert.notNull(password, "Bosh Director Password may not be empty, when initializing");
        this.host = host;
        this.username = username;
        this.password = password;
        this.authentication = authentication;
        this.port = port;
    }

    public BoshClient authenticate() {
        directorClient = new SpringDirectorClientBuilder()
                .withScheme(Scheme.https)
                .withHost(host)
                .withPort(25555)
                .withCredentials(username, password, authentication)
                .build();

        return this;
    }

    public DirectorClient client() {
        this.authenticate();
        Assert.notNull(directorClient, "Connection must be initialized before calling any methods on it");
        return directorClient;
    }
}
