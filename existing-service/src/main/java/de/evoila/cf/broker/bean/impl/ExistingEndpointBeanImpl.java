package de.evoila.cf.broker.bean.impl;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.model.ServerAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!pcf")
@ConfigurationProperties(prefix="existing.endpoint")
public class ExistingEndpointBeanImpl implements ExistingEndpointBean {

	private List<ServerAddress> hosts = new ArrayList<>();

	private int port;

	private String username;

	private String password;

	private String database;

    private String deployment;

    @Override
    public List<ServerAddress> getHosts() {
        return hosts;
    }

    public void setHosts(List<ServerAddress> hosts) {
        this.hosts = hosts;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    @Override
    public String getDeployment() {
        return deployment;
    }

    public void setDeployment(String deployment) {
        this.deployment = deployment;
    }
}

