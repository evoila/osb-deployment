package de.evoila.cf.cpi.openstack.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterManager {

	public static final String RESOURCE_NAME =  "resource_name";
	public static final String NODE_NUMBER = "node_number";
	public static final String NETWORK_ID = "network_id";
	public static final String SECURITY_GROUPS = "security_groups";
	public static final String IMAGE_ID = "image_id";
	public static final String KEY_NAME = "KEY_NAME";
	public static final String FLAVOR = "flavor";
	public static final String AVAILABILITY_ZONE = "availability_zone";
	public static final String VOLUME_SIZE = "volume_size";
	public static final String SERVICE_DB = "service_db";
	public static final String ADMIN_USER = "admin_user";
	public static final String ADMIN_PASSWORD = "admin_password";
	public static final String CLUSTER = "cluster";
	public static final String PRIMARY_VOLUME_ID = "primary_volume_id";
	public static final String PRIMARY_PORT = "primary_port";
	public static final String PRIMARY_IP = "primary_ip";
	public static final String SECONDARY1_VOLUME_ID = "secondary1_volume_id";
	public static final String SECONDARY1_PORT = "secondary1_port";
	public static final String SECONDARY2_VOLUME_ID = "secondary2_volume_id";
	public static final String SECONDARY2_PORT = "secondary2_port";
	public static final String SECONDARIES_IP_LIST = "secondaries_ip_list";
	public static final String MONGODB_KEY = "mongodb_key";
	public static final String REPSET_NAME = "repset_name";
	
	static void updatePortParameters(Map<String, String> customParameters, List<String> ips, List<String> ports) {
		String primIp = ips.get(0);
		ips.remove(0);
		String primPort = ports.get(0);
		ports.remove(0);

		customParameters.put(ParameterManager.PRIMARY_PORT, primPort);
		customParameters.put(ParameterManager.PRIMARY_IP, primIp);
	}
	
	static void updateVolumeParameters(Map<String, String> customParameters, List<String> volumes) {
		String primaryVolume = volumes.get(0);
		volumes.remove(0);
		
		customParameters.put(ParameterManager.PRIMARY_VOLUME_ID, primaryVolume);
	}
	
	static int getSecondaryNumber(Map<String, String> customParameters) {
		return Integer.parseInt(customParameters.get(ParameterManager.NODE_NUMBER));
	}
	
	static Map<String, String> copyProperties(Map<String, String> completeList, String... keys) {
		Map<String, String> copiedProps = new HashMap<>();
		
		for(int i = 0; i < keys.length; i++) {
			String key = keys[i];
			copiedProps.put(key, completeList.get(key));
		}
		return copiedProps;
	}
}
