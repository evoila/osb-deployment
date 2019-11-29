package de.evoila.cf.broker.bean.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import de.evoila.cf.broker.model.catalog.ServerAddress;
import de.evoila.cf.broker.util.ObjectMapperUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties
public class ExistingEndpoint {

    private String serverName;

    private String name;

    private String pcfHosts;

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

    public String getPcfHosts() {
        return pcfHosts;
    }

    public void setPcfHosts(String pcfHosts) throws IOException {
        List<String> pcfHostList = ObjectMapperUtils.getObjectMapper().readValue(pcfHosts, new TypeReference<List<String>>() {
        });
        for (String host : pcfHostList) {
            hosts.add(new ServerAddress(this.name, host, this.port));
        }

        this.pcfHosts = pcfHosts;
    }

    public List<ServerAddress> getHosts() {
        return hosts;
    }

    public void setHosts(List<ServerAddress> hosts) {
        this.hosts = hosts;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

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

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}