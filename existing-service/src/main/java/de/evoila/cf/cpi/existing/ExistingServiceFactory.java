package de.evoila.cf.cpi.existing;

import de.evoila.cf.broker.bean.ExistingEndpointBean;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.Plan;
import de.evoila.cf.broker.model.Platform;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.repository.PlatformRepository;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.broker.service.availability.ServicePortAvailabilityVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 *
 * @author Christian Brinker, evoila.
 *
 */
public abstract class ExistingServiceFactory implements PlatformService {

	protected Logger log = LoggerFactory.getLogger(getClass());

	private PlatformRepository platformRepository;

	private ServicePortAvailabilityVerifier portAvailabilityVerifier;

    private ExistingEndpointBean existingEndpointBean;

    public ExistingServiceFactory(PlatformRepository platformRepository, ServicePortAvailabilityVerifier portAvailabilityVerifier,
								  ExistingEndpointBean existingEndpointBean) {
    	this.platformRepository = platformRepository;
    	this.portAvailabilityVerifier = portAvailabilityVerifier;
    	this.existingEndpointBean = existingEndpointBean;
	}

	@Override
	@PostConstruct
	public void registerCustomPlatformService () {
	    platformRepository.addPlatform(Platform.EXISTING_SERVICE, this);
		log.info("Added Platform-Service " + this.getClass().toString() + " of type " + Platform.EXISTING_SERVICE);
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
        serviceInstance.setHosts(existingEndpointBean.getHosts());

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
