/**
 * 
 */
package de.evoila.cf.cpi.openstack.custom;

import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.ServerAddress;

import java.util.List;

/**
 * @author Christian Brinker, evoila.
 *
 */
public abstract class IpAccessor {
	abstract public List<ServerAddress> getIpAddresses(String instanceId) throws PlatformException;
}
