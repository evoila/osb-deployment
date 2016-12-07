/**
 * 
 */
package de.evoila.cf.broker.custom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.custom.mongodb.MongoDbService;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.cpi.existing.CustomExistingService;
import de.evoila.cf.cpi.existing.CustomExistingServiceConnection;
import de.evoila.cf.cpi.existing.ExistingServiceFactory;

/**
 * @author René Schollmeyer
 *
 */

@Service
@ConditionalOnProperty(prefix = "existing.endpoint", name = { "host", "port", "username", "password",
		"database" }, havingValue = "")
public class MongoDbExistingServiceFactory extends ExistingServiceFactory {

	public void createDatabase(MongoDbService connection, String database) throws PlatformException {
		connection.mongoClient().getDB(database);
	}

	public void deleteDatabase(MongoDbService connection, String database) throws PlatformException {
		connection.mongoClient().dropDatabase(database);
	}

	@Override
	protected void deleteInstance(CustomExistingServiceConnection connection, String instanceId)
			throws PlatformException {
		if (connection instanceof MongoDbService)
			deleteDatabase((MongoDbService) connection, instanceId);

	}

	@Override
	protected CustomExistingService getCustomExistingService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createInstance(CustomExistingServiceConnection connection, String instanceId)
			throws PlatformException {
		if (connection instanceof MongoDbService)
			createDatabase((MongoDbService) connection, instanceId);

	}

}
