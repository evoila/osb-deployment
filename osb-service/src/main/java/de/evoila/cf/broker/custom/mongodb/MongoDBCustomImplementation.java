/**
 * 
 */
package de.evoila.cf.broker.custom.mongodb;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author René
 *
 */
@Service
public class MongoDBCustomImplementation implements CustomExistingService {

	private Logger log = LoggerFactory.getLogger(MongoDBCustomImplementation.class);
	
	/* (non-Javadoc)
	 * @see de.evoila.cf.cpi.existing.CustomExistingService#connection(java.lang.String, int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public CustomExistingServiceConnection connection(List<String> hosts, int port, String database, String username,
			String password) throws Exception {
		
		MongoDbService mongoDbService = new MongoDbService();
		
		List<ServerAddress> serverAddresses = Lists.newArrayList();
		for (String address: hosts) {
			ServerAddress newAdress = new ServerAddress("", address, port);
			serverAddresses.add(newAdress);
			log.info("Opening connection to " + address + ":" + port);
		}
		try {
			mongoDbService.createConnection(null, username, password, serverAddresses);
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
		if(connection instanceof MongoDbService) {
			createUserForDatabaseWithRoles((MongoDbService) connection, database, username, password, "readWrite", "userAdmin");
		}
	}

	public static void createUserForDatabase(MongoDbService mongoDbService, String database, String username,
			String password) {
			createUserForDatabaseWithRoles((MongoDbService) mongoDbService, database, username, password, "readWrite");
	}
		
	public static void createUserForDatabaseWithRoles(MongoDbService mongoDbService, String database, String username,
			String password, String... roles) {
		Map<String, Object> commandArguments = new BasicDBObject();
		commandArguments.put("createUser", username);
		commandArguments.put("pwd", password);
		commandArguments.put("roles", roles);
		BasicDBObject command = new BasicDBObject(commandArguments);

		mongoDbService.mongoClient().getDB(database).command(command);
	}
}
