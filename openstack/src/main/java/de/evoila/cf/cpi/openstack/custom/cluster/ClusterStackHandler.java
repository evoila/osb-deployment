/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom.cluster;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openstack4j.model.heat.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.cpi.openstack.custom.CustomStackHandler;
import de.evoila.cf.cpi.openstack.custom.StackHandler;

/**
 * @author Yannic Remmet, evoila
 *
 */
@Service
@ConditionalOnProperty(prefix = "openstack", name = { "keypair" }, havingValue = "")
public abstract class ClusterStackHandler extends CustomStackHandler {
	
	protected static final String PRE_IP_TEMPLATE = "/openstack/pre-ips.yaml";
	protected static final String PRE_VOLUME_TEMPLATE = "/openstack/pre-volume.yaml";
	
	protected static final String MAIN_TEMPLATE = "/openstack/main.yaml";
	
	protected static final String PRIMARY_TEMPLATE = "/openstack/primary.yaml";
	protected static final String SECONDARY_TEMPLATE = "/openstack/secondaries.yaml";
	
	protected static final String PORTS_KEY = "port_ids";
	protected static final String IP_ADRESS_KEY = "port_ips";
	protected static final String VOLUME_KEY = "volume_ids";
	
	@Value("${openstack.keypair}")
	private String keyPair;
	
	@Value("${openstack.networkId}")
	private String networkId;
	
	@Value("${openstack.subnetId}")
	private String subNetId;
	
	private final Logger log = LoggerFactory.getLogger(ClusterStackHandler.class);

	
	public ClusterStackHandler() {
		super();
	}
	

	@Override
	public String create(String instanceId, Map<String, String> customParameters)
			throws PlatformException, InterruptedException {
		log.debug(customParameters.toString());
		if (customParameters.containsKey(ClusterParameterManager.CLUSTER)) {
			customParameters.putAll(defaultParameters());
			customParameters.putAll(generateValues(instanceId));
			return createCluster(instanceId, customParameters);
		} 
		log.debug("Not Creating a cluster" + instanceId);
		return super.create(instanceId, customParameters);
	}


	protected abstract String createCluster(String instanceId, Map<String, String> customParameters)
			throws PlatformException, InterruptedException;
	
	@SuppressWarnings("unchecked")
	protected List<String>[] extractResponses(Stack stack, String... keys) {
		List<String>[] response = new List[keys.length];
		
		for (Map<String, Object> output : stack.getOutputs()) {
			Object outputKey = output.get("output_key");
			if (outputKey != null && outputKey instanceof String) {
				String key = (String) outputKey;
				for(int i=0; i< keys.length; i++) {
					if (key.equals(keys[i])) {
						Object value = output.get("output_value");
						response[i] = (List<String>) value;
					}
				}	
			}
		}
		return response;
	}
	
	private Map<? extends String, ? extends String> generateValues(String instanceId) {
		HashMap<String, String> valueMap = new HashMap();
		valueMap.put(ClusterParameterManager.ADMIN_PASSWORD, instanceId);
		valueMap.put(ClusterParameterManager.ADMIN_USER, instanceId);
		valueMap.put(ClusterParameterManager.SERVICE_DB, instanceId);
		valueMap.put(ClusterParameterManager.KEY_NAME, keyPair);
		valueMap.put(ClusterParameterManager.STANDBY_HOSTNAME, instanceId);
		return valueMap;
	}
	
	protected String getKeyPair() {
		return keyPair;
	}


}