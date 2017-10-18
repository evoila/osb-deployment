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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
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
@ConditionalOnBean(HeatFluent.class)
// @ConditionalOnBean is evaluated after all configuration classes have been processed,
// i.e you can't use it to make a whole configuration class conditional on the presence of another bean.
// You can, however, use it where you have to make all of the configuration's beans conditional on the presence of another bean.
@ConditionalOnProperty(prefix="openstack",
	name = {"endpoint",
			   "user.username", "user.password", "user.domainName",
			   "project.domainName", "project.projectName",
			   "networkId", "subnetId", "imageId", "keypair",
			   "cinder.az"
	}, havingValue="")
@Primary
public class MongoIpAccessor extends CustomIpAccessor {

	public MongoIpAccessor(HeatFluent heatFluent, DefaultIpAccessor defaultIpAccessor, StackMappingRepository mappingRepository){
		this.heatFluent = heatFluent;
		this.defaultIpAccessor = defaultIpAccessor;
		this.stackMappingRepository = mappingRepository;
	}


	private HeatFluent heatFluent;
	private DefaultIpAccessor defaultIpAccessor;
	private StackMappingRepository stackMappingRepository;

	@Override
	public List<ServerAddress> getIpAddresses(String instanceId) throws PlatformException {


		ClusterStackMapping mapping = stackMappingRepository.findOne(instanceId);


		if (mapping == null) {
			return defaultIpAccessor.getIpAddresses(instanceId);
		}

		return Collections.unmodifiableList(mapping.getServerAddresses());
	}
}
