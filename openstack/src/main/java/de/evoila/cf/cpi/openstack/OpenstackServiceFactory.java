/**
 * 
 */
package de.evoila.cf.cpi.openstack;

import de.evoila.cf.broker.bean.OpenstackBean;
import de.evoila.cf.broker.cpi.endpoint.EndpointAvailabilityService;
import de.evoila.cf.broker.model.cpi.AvailabilityState;
import de.evoila.cf.broker.model.cpi.EndpointServiceState;
import de.evoila.cf.broker.service.PlatformService;
import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 *
 */
public abstract class OpenstackServiceFactory implements PlatformService {

	private final static String OPENSTACK_SERVICE_KEY = "openstackFactoryService";

	private Logger log = LoggerFactory.getLogger(getClass());

	private String endpoint;

	private String username;

	private String password;

	private String userDomainName;

	private String projectDomainName;

	private String projectName;
	
	protected Map<String, Integer> ports;

	private EndpointAvailabilityService endpointAvailabilityService;

	private OpenstackBean openstackBean;

	public OpenstackServiceFactory(EndpointAvailabilityService endpointAvailabilityService, OpenstackBean openstackBean) {
		this.endpointAvailabilityService = endpointAvailabilityService;
		this.openstackBean = openstackBean;
	}

	@PostConstruct
	public void initialize() {
		endpoint = openstackBean.getEndpoint();
		username = openstackBean.getUser().getUsername();
		password = openstackBean.getUser().getPassword();
		userDomainName = openstackBean.getUser().getDomainName();
		projectDomainName = openstackBean.getProject().getDomainName();
		projectName = openstackBean.getProject().getProjectName();
		
		log.debug("Initializing Openstack Connection Factory");
		try {
			if (endpointAvailabilityService.isAvailable(OPENSTACK_SERVICE_KEY)) {
				OpenstackConnectionFactory.getInstance().setCredential(username, password, userDomainName).authenticate(endpoint,
						projectName, projectDomainName);

				log.debug("Reading heat template definition for openstack");

				endpointAvailabilityService.add(OPENSTACK_SERVICE_KEY,
						new EndpointServiceState(OPENSTACK_SERVICE_KEY, AvailabilityState.AVAILABLE));
			}
		} catch (Exception ex) {
			endpointAvailabilityService.add(OPENSTACK_SERVICE_KEY,
					new EndpointServiceState(OPENSTACK_SERVICE_KEY, AvailabilityState.ERROR, ex.toString()));
		}
	}

	public Map<String, Integer> getPorts() {
		return ports;
	}

	public void setPorts(Map<String, Integer> ports) {
		this.ports = ports;
	}

}
