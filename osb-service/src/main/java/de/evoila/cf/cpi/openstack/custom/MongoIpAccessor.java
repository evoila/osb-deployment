/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.evoila.cf.broker.persistence.mongodb.repository.ClusterStackMapping;
import de.evoila.cf.broker.persistence.mongodb.repository.StackMappingRepository;
import org.openstack4j.model.heat.Stack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;
import de.evoila.cf.cpi.openstack.fluent.HeatFluent;
import jersey.repackaged.com.google.common.collect.Lists;

/**
 * @author Christian Brinker, evoila.
 *
 */
@Service
@Primary
@ConditionalOnBean(HeatFluent.class)
public class MongoIpAccessor extends CustomIpAccessor {
	@Autowired
	private HeatFluent heatFluent;

	@Autowired
	private DefaultIpAccessor defaultIpAccessor;
	@Autowired
	StackMappingRepository stackMappingRepository;

	@Override
	public List<ServerAddress> getIpAddresses(String instanceId) throws PlatformException {


		ClusterStackMapping mapping = stackMappingRepository.findOne(instanceId);


		if (mapping == null) {
			return defaultIpAccessor.getIpAddresses(instanceId);
		}

		return Collections.unmodifiableList(mapping.getServerAddresses());
	}
}
