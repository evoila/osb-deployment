/**
 * 
 */
package de.evoila.cf.cpi.custom.props;

import java.util.Map;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.util.Base64Utils;

import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.ServiceInstance;

/**
 * @author Johannes Hiemer.
 *
 */
public class MongoDBCustomPropertyHandler extends DefaultDatabaseCustomPropertyHandler {

	/**
	 * 
	 */
	private static final String TEMPLATE = "template";
	private static final String REPLICA_SET = "replicaSet";
	private static final String SECONDARY_NUMBER = "secondary_number";
	private static final String DATABASE_KEY = "database_key";

	private BytesKeyGenerator secureRandom;

	/**
	 * @param keyLength
	 */
	public MongoDBCustomPropertyHandler(int keyLength) {
		secureRandom = KeyGenerators.secureRandom(keyLength);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.evoila.cf.cpi.openstack.custom.props.DomainBasedCustomPropertyHandler#
	 * addDomainBasedCustomProperties(de.evoila.cf.broker.model.Plan,
	 * java.util.Map, java.lang.String)
	 */
	@Override
	public Map<String, String> addDomainBasedCustomProperties(Plan plan, Map<String, String> customProperties,
			ServiceInstance serviceInstance) {
		super.addDomainBasedCustomProperties(plan, customProperties, serviceInstance);

		Object replicaSetOptional = plan.getMetadata().get(REPLICA_SET);

		if (replicaSetOptional != null && replicaSetOptional instanceof String) {
			String replicaSet = (String) replicaSetOptional;

			// customProperties.put(REPLICA_SET, replicaSet);

			String templatePath = (String) plan.getMetadata().get(TEMPLATE);
			customProperties.put(TEMPLATE, templatePath);
			
			int secondaryNumber = (int) plan.getMetadata().get(SECONDARY_NUMBER);
			customProperties.put(SECONDARY_NUMBER, Integer.toString(secondaryNumber));

			String key = Base64Utils.encodeToString(secureRandom.generateKey());
			customProperties.put(DATABASE_KEY, key);
		}

		return customProperties;
	}

}
