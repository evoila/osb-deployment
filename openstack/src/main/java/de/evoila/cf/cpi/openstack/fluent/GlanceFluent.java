/**
 * 
 */
package de.evoila.cf.cpi.openstack.fluent;

import de.evoila.cf.cpi.openstack.fluent.connection.OpenstackConnectionFactory;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.image.Image;

import java.util.List;

/**
 * @author Johannes Hiemer.
 *
 */
public class GlanceFluent {
	
	private OSClient client() {
		return OpenstackConnectionFactory.connection();
	}
	
	public List<? extends Image> list() {
		return client().images().list();
	}
	
	public Image get(String imageId) {
		return client().images().get(imageId);
	}

}
