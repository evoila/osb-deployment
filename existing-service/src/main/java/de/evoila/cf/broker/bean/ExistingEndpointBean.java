package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.model.catalog.ServerAddress;

import java.util.List;

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

	String getDeployment();
}
