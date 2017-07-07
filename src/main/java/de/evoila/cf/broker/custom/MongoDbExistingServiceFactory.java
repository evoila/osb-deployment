/**
 * 
 */
package de.evoila.cf.broker.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

import de.evoila.cf.broker.custom.mongodb.MongoDBCustomImplementation;
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
@ConditionalOnProperty(prefix = "existing.endpoint", name = { "port", "username", "password",
		"database" }, havingValue = "")
public class MongoDbExistingServiceFactory extends ExistingServiceFactory {
	
	@Autowired
	private MongoDBCustomImplementation mongodb;

	public void createDatabase(MongoDbService connection, String database) throws PlatformException {
		try {
			MongoClient mongo = connection.mongoClient();
			mongo.setWriteConcern(WriteConcern.JOURNAL_SAFE);
			DB db = mongo.getDB(database);
			DBCollection collection = db.getCollection("_auth");
			collection.save(new BasicDBObject("auth", "auth"));
			collection.drop();
		} catch(MongoException e) {
			throw new PlatformException("Could not add to database", e);
		}
	}

	public void deleteDatabase(MongoDbService connection, String database) throws PlatformException {
		try {
		connection.mongoClient().dropDatabase(database);
		} catch (MongoException e) {
			throw new PlatformException("Could not remove from database", e);
		}
	}

	@Override
	protected void deleteInstance(CustomExistingServiceConnection connection, String instanceId)
			throws PlatformException {
		if (connection instanceof MongoDbService)
			deleteDatabase((MongoDbService) connection, instanceId);
	}

	@Override
	protected CustomExistingService getCustomExistingService() {
		return mongodb;
	}

	@Override
	protected void createInstance(CustomExistingServiceConnection connection, String instanceId)
			throws PlatformException {
		if (connection instanceof MongoDbService)
			createDatabase((MongoDbService) connection, instanceId);
	}
}
