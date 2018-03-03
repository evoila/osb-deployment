package de.evoila.cf.cpi.bosh.connection;

import de.evoila.cf.broker.exception.PlatformException;
import io.bosh.client.DirectorClient;
import io.bosh.client.SpringDirectorClientBuilder;
import io.bosh.client.authentication.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.URISyntaxException;

/**
 * Created by reneschollmeyer, evoila on 09.10.17.
 */
public class BoshConnection {

    private final static Logger log = LoggerFactory.getLogger(BoshConnection.class);

    protected DirectorClient directorClient;

    private String host;
    private String username;
    private String password;
    private Authentication authentication;
    private Integer port;

    public BoshConnection(String username, String password, String host, Authentication authentication){
        Assert.notNull(host, "Bosh Director Host may not be empty, when initializing");
        Assert.notNull(username, "Bosh Director Username may not be empty, when initializing");
        Assert.notNull(password, "Bosh Director Password may not be empty, when initializing");
        this.host = host;
        this.username = username;
        this.password = password;
        this.authentication = authentication;
        this.port = port;
    }


    public BoshConnection authenticate() throws PlatformException {
        try {
            directorClient = new SpringDirectorClientBuilder()
                    .withHost(host)
                    .withCredentials(username, password)
                    .withAuthentication(authentication)
                    .build();
        } catch(URISyntaxException ex) {
            throw new PlatformException(ex);
        }

        return this;
    }


    public DirectorClient connection() throws PlatformException {
        this.authenticate();
        Assert.notNull(directorClient, "Connection must be initialized before calling any methods on it");
        return directorClient;
    }
}
