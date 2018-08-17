package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.model.ServerAddress;

import java.util.List;

public interface ExistingEndpointBean {

	List<ServerAddress> getHosts();
	int getPort();
	String getUsername();
	String getPassword();
	String getDatabase();
	String getDeployment();
}
