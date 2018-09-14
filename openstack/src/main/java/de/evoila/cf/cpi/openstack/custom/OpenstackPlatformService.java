/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.broker.cpi.endpoint.EndpointAvailabilityService;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.VolumeUnit;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.cpi.openstack.OpenstackServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotSupportedException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Johannes Hiemer.
 *
 */
@Service
@EnableConfigurationProperties
@ConditionalOnBean(OpenstackBean.class)
public class OpenstackPlatformService extends OpenstackServiceFactory {

	private static final String VOLUME_SIZE = "volume_size";
	private static final String FLAVOR = "flavor";
	private static final String CLUSTER = "cluster";
	private static final String SECURITY_GROUPS = "security_groups";
	private static final String NODE_NUMBER = "node_number";
	
	private final Logger log = LoggerFactory.getLogger(OpenstackPlatformService.class);

	private StackHandler stackHandler;

	@Qualifier(value = "defaultStackHandler")
	private StackHandler defaultStackHandler;

	private PlatformRepository platformRepository;

	private ServicePortAvailabilityVerifier portAvailabilityVerifier;

	private IpAccessor ipAccessor;

	public OpenstackPlatformService(StackHandler defaultStackHandler, PlatformRepository platformRepository, ServicePortAvailabilityVerifier portAvailabilityVerifier,
									IpAccessor ipAccessor, EndpointAvailabilityService endpointAvailabilityService, OpenstackBean openstackBean) {
		super(endpointAvailabilityService, openstackBean);
		this.defaultStackHandler = defaultStackHandler;
		this.platformRepository = platformRepository;
		this.portAvailabilityVerifier = portAvailabilityVerifier;
		this.ipAccessor = ipAccessor;
	}

    @Override
    public boolean isSyncPossibleOnCreate(Plan plan) {
        return false;
    }

    @Override
    public boolean isSyncPossibleOnDelete(ServiceInstance instance) {
        return false;
    }

    @Override
    public boolean isSyncPossibleOnUpdate(ServiceInstance instance, Plan plan) {
        return false;
    }

	@Autowired(required = false)
	private void setStackHandler(CustomStackHandler customStackHandler) {
		if (customStackHandler != null) {
			stackHandler = customStackHandler;
		} else {
			stackHandler = defaultStackHandler;
		}
	}

	@Override
	@PostConstruct
	public void registerCustomPlatformService() {
		if (platformRepository != null)
			platformRepository.addPlatform(Platform.OPENSTACK, this);

		if (stackHandler == null) {
			stackHandler = defaultStackHandler;
		}
	}

	@Override
    public ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

	@Override
	public ServiceInstance createInstance(ServiceInstance serviceInstance, Plan plan,
			Map<String, Object> customProperties) throws PlatformException {
		String instanceId = serviceInstance.getId();
			
		Map<String, String> platformParameters = new HashMap<String, String>();
		/*if(plan.getMetadata().containsKey(CLUSTER)) {
			platformParameters.put(SECURITY_GROUPS, plan.getMetadata().get(SECURITY_GROUPS).toString());
			platformParameters.put(CLUSTER, plan.getMetadata().get(CLUSTER).toString());
			platformParameters.put(NODE_NUMBER, plan.getMetadata().containsKey(NODE_NUMBER) ? plan.getMetadata().get(NODE_NUMBER).toString() : "1");
		}

		platformParameters.putAll(customProperties);

		try {
			String internalId = stackHandler.create(instanceId, platformParameters);

			List<ServerAddress> tmpAddresses = ipAccessor.getIpAddresses(instanceId);
			List<ServerAddress> serverAddresses = Lists.newArrayList();
			
			if(this.ports != null && !plan.getMetadata().containsKey(CLUSTER)) {
				for (Entry<String, Integer> port : this.ports.entrySet()) {
					for (ServerAddress tmpAddress : tmpAddresses) {
						ServerAddress serverAddress = new ServerAddress(tmpAddress);
						serverAddress.setName(port.getKey());
						if(port.getValue() != null)
							serverAddress.setPort(port.getValue());
						serverAddresses.add(serverAddress);
					}
				}
			} else {
				serverAddresses = tmpAddresses;
			}
			
			serviceInstance = new ServiceInstance(serviceInstance, "http://currently.not/available", internalId,
					serverAddresses);
		} catch (Exception e) {
			throw new PlatformException(e);
		}*/
		return serviceInstance;
	}

	@Override
    public ServiceInstance getCreateInstancePromise(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance postCreateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        boolean available;
        try {
            available = portAvailabilityVerifier.verifyServiceAvailability(serviceInstance, true);
        } catch (Exception e) {
            throw new PlatformException("Service instance is not reachable. Service may not be started on instance.",
                    e);
        }

        if (!available) {
            throw new PlatformException("Service instance is not reachable. Service may not be started on instance.");
        }

        return serviceInstance;
    }

	private String volumeSize(int volumeSize, VolumeUnit volumeUnit) {
		if (volumeUnit.equals(VolumeUnit.M))
			throw new NotAcceptableException("Volumes in openstack may not be smaller than 1 GB");
		else if (volumeUnit.equals(VolumeUnit.G))
			return String.valueOf(volumeSize);
		else if (volumeUnit.equals(VolumeUnit.T))
			return String.valueOf(volumeSize * 1024);
		return String.valueOf(volumeSize);
	}

    @Override
    public void preDeleteInstance(ServiceInstance serviceInstance) { }

	@Override
	public void deleteInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
		stackHandler.delete(serviceInstance.getInternalId());
	}

    @Override
    public void postDeleteInstance(ServiceInstance serviceInstance) { }

    @Override
    public ServiceInstance preUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
    public ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
	public ServiceInstance updateInstance(ServiceInstance instance, Plan plan, Map<String, Object> customParameters) {
		throw new NotSupportedException("Updating Service Instances is currently not supported");
	}

}
