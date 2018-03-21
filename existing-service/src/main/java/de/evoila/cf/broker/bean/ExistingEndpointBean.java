package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.model.ServerAddress;

import java.util.List;

public interface ExistingEndpointBean {

	List<String> getHosts();
	List<ServerAddress> getHostsWithServerAddress();
	int getPort();
	int getAdminport();
	String getUsername();
	String getPassword();
	String getDatabase();

}
