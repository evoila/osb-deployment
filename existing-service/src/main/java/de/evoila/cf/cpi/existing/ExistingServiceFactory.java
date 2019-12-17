package de.evoila.cf.cpi.existing;

import de.evoila.cf.broker.bean.ExistingEndpointsBean;
import de.evoila.cf.broker.bean.impl.ExistingEndpoint;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.catalog.plan.Plan;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import de.evoila.cf.security.credentials.credhub.CredhubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Christian Brinker, evoila.
 */
public abstract class ExistingServiceFactory implements PlatformService {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private PlatformRepository platformRepository;

	private ServicePortAvailabilityVerifier portAvailabilityVerifier;

    private ExistingEndpointsBean existingEndpoints;

    private CredhubClient credhubClient;

    public ExistingServiceFactory(PlatformRepository platformRepository, ServicePortAvailabilityVerifier portAvailabilityVerifier,
								  ExistingEndpointsBean existingEndpoints, CredhubClient credhubClient) {
    	this.platformRepository = platformRepository;
    	this.portAvailabilityVerifier = portAvailabilityVerifier;
    	this.existingEndpoints = existingEndpoints;
    	this.credhubClient = credhubClient;
	}

	@Override
	@PostConstruct
	public void registerCustomPlatformService () {
	    platformRepository.addPlatform(Platform.EXISTING_SERVICE, this);
		log.info("Added Platform-Service " + this.getClass().toString() + " of type " + Platform.EXISTING_SERVICE);
	}

	@Override
	public boolean isSyncPossibleOnBind() { return true; }

	@Override
	public boolean isSyncPossibleOnUnbind() { return true; }

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

	@Override
	public ServiceInstance getCreateInstancePromise(ServiceInstance instance, Plan plan) {
		return new ServiceInstance(instance, null, null);
	}

    @Override
    public ServiceInstance preCreateInstance(ServiceInstance serviceInstance, Plan plan) {
        return serviceInstance;
    }

    @Override
    public ServiceInstance postCreateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
		List<ExistingEndpoint> hosts = existingEndpoints.getEndpoints().stream().filter(e -> {
			if (e.getServerName().equals(plan.getMetadata().getEndpointName()))
				return true;
			else
				return false;
			}).collect(Collectors.toList());

    	hosts.stream().forEach(endpoint -> serviceInstance.setHosts(endpoint.getHosts()));


        boolean available;
        try {
            available = portAvailabilityVerifier.verifyServiceAvailability(serviceInstance, false);
        } catch (Exception e) {
            throw new PlatformException("Service instance is not reachable. Service may not be started on instance.",
                    e);
        }

        if (!available) {
            throw new PlatformException("Service instance is not reachable. Service may not be started on instance.");
        }

        return serviceInstance;
    }

    @Override
    public ServiceInstance preUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

	@Override
	public ServiceInstance updateInstance(ServiceInstance serviceInstance, Plan plan, Map<String, Object> customParameters) {
		return serviceInstance;
	}

    @Override
    public ServiceInstance postUpdateInstance(ServiceInstance serviceInstance, Plan plan) throws PlatformException {
        return serviceInstance;
    }

    @Override
    public void preDeleteInstance(ServiceInstance serviceInstance) {}

    @Override
    public void postDeleteInstance(ServiceInstance serviceInstance) {}

}
