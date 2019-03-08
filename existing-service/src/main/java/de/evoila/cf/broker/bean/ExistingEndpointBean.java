package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.model.catalog.ServerAddress;

import java.util.List;
import java.util.Map;

public interface ExistingEndpointBean {

	List<ServerAddress> getHosts();
	int getPort();
	String getUsername();
	String getPassword();
	String getDatabase();
	String getDeployment();
	Map<String, String> getParameters();
}
