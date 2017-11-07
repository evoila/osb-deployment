/**
 * 
 */
package de.evoila.cf.broker.custom;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.evoila.cf.broker.model.*;
import de.evoila.cf.broker.service.PlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import de.evoila.cf.broker.custom.mongodb.MongoDBCustomImplementation;
import de.evoila.cf.broker.custom.mongodb.MongoDbService;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.service.impl.BindingServiceImpl;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author Johannes Hiemer.
 *
 */
@Service
public class MongoDbBindingService extends BindingServiceImpl {

	private Logger log = LoggerFactory.getLogger(MongoDbBindingService.class);

	private MongoDbService connection(ServiceInstance serviceInstance) throws ServiceBrokerException {
		return connection(serviceInstance.getId(),serviceInstance.getId(),serviceInstance.getId(),serviceInstance);
	}

	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			List<ServerAddress> hosts, Plan p) throws ServiceBrokerException {
		MongoDbService mongoDbService;
		if(p.getPlatform() == Platform.BOSH)
			mongoDbService = connection("admin", serviceInstance.getId(), "admin", serviceInstance);
		else
			mongoDbService = connection(serviceInstance);

		SecureRandom random = new SecureRandom();
		
		String username = bindingId;
		String password = new BigInteger(130, random).toString(32);
		String database = serviceInstance.getId();

		MongoDBCustomImplementation.createUserForDatabase(mongoDbService, database, username, password);

		String formattedHosts = "";
		for (ServerAddress host : hosts) {
			if (formattedHosts != "")
				formattedHosts += ",";
			formattedHosts += String.format("%s:%d", host.getIp(), host.getPort());
		}

		String dbURL = String.format("mongodb://%s:%s@%s/%s", bindingId, password, formattedHosts, database);
		String replicaSet = serviceInstance.getParameters().get("replicaSet");
		if (replicaSet != null && !replicaSet.equals(""))
			dbURL += String.format("?replicaSet=%s", replicaSet);

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);
		credentials.put("username", username);
		credentials.put("password", password);
		credentials.put("database", database);

		return credentials;
	}

	private MongoDbService connection (String user, String password, String database, ServiceInstance instance) throws ServiceBrokerException {
		ServerAddress host = instance.getHosts().get(0);
		log.info("Opening connection to " + host.getIp() + ":" + host.getPort());
		MongoDbService mongoDbService = new MongoDbService();
		try {
			mongoDbService.createConnection(database, user, password, instance.getHosts());
		} catch (UnknownHostException e) {
			log.info("Could not establish connection", e);
			throw new ServiceBrokerException("Could not establish connection", e);
		}
		return mongoDbService;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindServiceKey(java.
	 * lang.String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.Plan, java.util.List)
	 */
	@Override
	protected ServiceInstanceBinding bindServiceKey(String bindingId, ServiceInstance serviceInstance, Plan plan,
			List<ServerAddress> externalAddresses) throws ServiceBrokerException {

		log.debug("bind service key");
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, externalAddresses, plan);

		ServiceInstanceBinding serviceInstanceBinding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(),
				credentials, null);
		serviceInstanceBinding.setExternalServerAddresses(externalAddresses);
		return serviceInstanceBinding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindService(java.lang
	 * .String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.Plan)
	 */
	@Override
	protected ServiceInstanceBinding bindService(String bindingId, ServiceInstance serviceInstance, Plan plan)
			throws ServiceBrokerException {

		log.debug("bind service");

		List<ServerAddress> hosts = serviceInstance.getHosts();
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, hosts, plan);

		return new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, null);
	}

	@Override
	protected void deleteBinding(String bindingId, ServiceInstance serviceInstance) throws ServiceBrokerException {
		MongoDbService mongoDbService = connection(serviceInstance);

		mongoDbService.mongoClient().getDB(serviceInstance.getId()).command(new BasicDBObject("dropUser", bindingId));
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#createCredentials(
	 * java.lang.String, de.evoila.cf.broker.model.ServiceInstance,
	 * de.evoila.cf.broker.model.ServerAddress)
	 */
	@Override
	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
													ServerAddress host, Plan plan) throws ServiceBrokerException {
		log.warn("de.evoila.cf.broker.custom.MongoDbBindingService#createCredentials( java.lang.String, "
				+ "de.evoila.cf.broker.model.ServiceInstance, de.evoila.cf.broker.model.ServerAddress) "
				+ "was used instead of de.evoila.cf.broker.custom.MongoDbBindingService#createCredentials( "
				+ "java.lang.String, de.evoila.cf.broker.model.ServiceInstance, "
				+ "java.util.List<de.evoila.cf.broker.model.ServerAddress>)");

		return createCredentials(bindingId, serviceInstance, Lists.newArrayList(host), plan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.broker.service.impl.BindingServiceImpl#bindRoute(de.evoila.
	 * cf.broker.model.ServiceInstance, java.lang.String)
	 */
	@Override
	protected RouteBinding bindRoute(ServiceInstance serviceInstance, String route) {
		throw new UnsupportedOperationException();
	}

}
