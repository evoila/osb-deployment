/**
 * 
 */
package de.evoila.cf.broker.custom.mongodb;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author Johannes Hiemer
 *
 */
public class MongoDbService {

	private String host;

	private int port;

	private MongoClient mongoClient;

	public boolean isConnected() {
		return mongoClient != null && mongoClient.getUsedDatabases() != null;
	}

	public void createConnection(String id, List<de.evoila.cf.broker.model.ServerAddress> hosts)
			throws UnknownHostException {
		List<ServerAddress> serverAddresses = Lists.newArrayList();
		for (de.evoila.cf.broker.model.ServerAddress host : hosts) {
			serverAddresses.add(new ServerAddress(host.getIp(), host.getPort()));
		}

		this.host = hosts.get(0).getIp();
		this.port = hosts.get(0).getPort();

		MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(id, "admin", id.toCharArray());
		mongoClient = new MongoClient(serverAddresses, Arrays.asList(mongoCredential));
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public MongoClient mongoClient() {
		return mongoClient;
	}

}
