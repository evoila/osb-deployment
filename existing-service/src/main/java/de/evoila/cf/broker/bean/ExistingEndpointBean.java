package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.impl.BackupCredentials;
import de.evoila.cf.broker.model.catalog.ServerAddress;

import java.util.List;
import java.util.Map;

/**
 * @author Yannic Remmet, Johannes Hiemer.
 */
public interface ExistingEndpointBean {

    String getName();

	List<ServerAddress> getHosts();

	int getPort();

	String getUsername();

	String getPassword();

	String getDatabase();

	String getServerName();

	String getDeployment();

    BackupCredentials getBackupCredentials();

    void setUsername(String username);
    void setPassword(String password);
    void setDatabase(String database);
    void setServerName(String serverName);
    void setHosts(List<ServerAddress> serverAddresses);

	Map<String, String> getParameters();
}
