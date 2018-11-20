package de.evoila.cf.broker.bean;

import de.evoila.cf.broker.bean.impl.OpenstackBeanImpl;

public interface OpenstackBean {

	public String getEndpoint();
	public OpenstackBeanImpl.User getUser();
	public OpenstackBeanImpl.Project getProject();
	public String getNetworkId();
	public String getSubnetId();
	public String getImageId();
	public String getKeypair();
	public OpenstackBeanImpl.Cinder getCinder();
	public String getPublicNetworkId();
	public String getPool();
}