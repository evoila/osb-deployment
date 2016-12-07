/**
 * 
 */
package de.evoila.cf.broker.custom.mongodb;

import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author René
 *
 */
public class MongoDbCustomImplementation implements CustomExistingService {

	private Logger log = LoggerFactory.getLogger(MongoDbCustomImplementation.class);
	
	/* (non-Javadoc)
	 * @see de.evoila.cf.cpi.existing.CustomExistingService#connection(java.lang.String, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CustomExistingServiceConnection connection(String host, int port, String database, String username,
			String password) throws Exception {
		
		MongoDbService mongoDbService = new MongoDbService();
		
		List<ServerAddress> serverAddresses = Lists.newArrayList(new ServerAddress("", host, port));
		log.info("Opening connection to " + host + ":" + port);
		try {
			mongoDbService.createConnection(username, serverAddresses);
		} catch (UnknownHostException e) {
			log.info("Could not establish connection", e);
			throw new ServiceBrokerException("Could not establish connection", e);
		}
		return mongoDbService;
	}

	/* (non-Javadoc)
	 * @see de.evoila.cf.cpi.existing.CustomExistingService#bindRoleToInstanceWithPassword(de.evoila.cf.cpi.existing.CustomExistingServiceConnection, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void bindRoleToInstanceWithPassword(CustomExistingServiceConnection connection, String database,
			String username, String password) throws Exception {
		// TODO Auto-generated method stub

	}

}
