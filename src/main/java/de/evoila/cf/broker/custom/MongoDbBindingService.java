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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;

import de.evoila.cf.broker.custom.mongodb.MongoDbService;
import de.evoila.cf.broker.exception.ServiceBrokerException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.RouteBinding;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.ServiceInstanceBinding;
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
		ServerAddress host = serviceInstance.getHosts().get(0);
		MongoDbService mongoDbService = new MongoDbService();
		log.info("Opening connection to " + host.getIp() + ":" + host.getPort());
		try {
			mongoDbService.createConnection(serviceInstance.getId(), serviceInstance.getHosts());
		} catch (UnknownHostException e) {
			log.info("Could not establish connection", e);
			throw new ServiceBrokerException("Could not establish connection", e);
		}
		return mongoDbService;
	}

	protected Map<String, Object> createCredentials(String bindingId, ServiceInstance serviceInstance,
			List<ServerAddress> hosts) throws ServiceBrokerException {

		MongoDbService mongoDbService = connection(serviceInstance);

		SecureRandom random = new SecureRandom();
		String password = new BigInteger(130, random).toString(32);

		Map<String, Object> commandArguments = new BasicDBObject();
		commandArguments.put("createUser", bindingId);
		commandArguments.put("pwd", password);
		String[] roles = { "readWrite" };
		commandArguments.put("roles", roles);
		BasicDBObject command = new BasicDBObject(commandArguments);

		mongoDbService.mongoClient().getDB(serviceInstance.getId()).command(command);

		String formattedHosts = "";
		for (ServerAddress host : hosts) {
			if (formattedHosts != "")
				formattedHosts += ",";
			formattedHosts += String.format("%s:%d", host.getIp(), host.getPort());
		}

		String dbURL = String.format("mongodb://%s:%s@%s/%s", bindingId, password, formattedHosts,
				serviceInstance.getId());
		String replicaSet = serviceInstance.getParameters().get("replicaSet");
		if (replicaSet != null && !replicaSet.equals(""))
			dbURL += String.format("?replicaSet=%s", replicaSet);

		Map<String, Object> credentials = new HashMap<String, Object>();
		credentials.put("uri", dbURL);

		return credentials;
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

		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, externalAddresses);

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
		Map<String, Object> credentials = createCredentials(bindingId, serviceInstance, hosts);

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
			ServerAddress host) throws ServiceBrokerException {
		log.warn("de.evoila.cf.broker.custom.MongoDbBindingService#createCredentials( java.lang.String, "
				+ "de.evoila.cf.broker.model.ServiceInstance, de.evoila.cf.broker.model.ServerAddress) "
				+ "was used instead of de.evoila.cf.broker.custom.MongoDbBindingService#createCredentials( "
				+ "java.lang.String, de.evoila.cf.broker.model.ServiceInstance, "
				+ "java.util.List<de.evoila.cf.broker.model.ServerAddress>)");

		return createCredentials(bindingId, serviceInstance, Lists.newArrayList(host));
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
