package de.evoila.cf.broker.bean.impl;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yannic Remmet, Johannes Hiemer.
 */
@Service
@Profile("!pcf")
@ConfigurationProperties(prefix = "existing.endpoint")
public class ExistingEndpointBeanImpl implements ExistingEndpointBean {

    private String name;

	private List<ServerAddress> hosts = new ArrayList<>();

	private int port;

	private String username;

	private String password;

	private String database;

    private String deployment;

    private BackupCredentials backupCredentials;

    private Map<String, String> parameters = new HashMap();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    @Override
    public BackupCredentials getBackupCredentials() {
        return backupCredentials;
    }

    public void setBackupCredentials(BackupCredentials backupCredentials) {
        this.backupCredentials = backupCredentials;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}

