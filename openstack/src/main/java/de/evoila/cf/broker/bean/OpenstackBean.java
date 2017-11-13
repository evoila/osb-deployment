package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl.Cinder;
import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl.Project;
import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl.User;


import java.util.List;

public interface OpenstackBean {

	public String getEndpoint();
	public User getUser();
	public Project getProject();
	public String getNetworkId();
	public String getSubnetId();
	public String getImageId();
	public String getKeypair();
	public Cinder getCinder();
	public String getPublicNetworkId();

	
}