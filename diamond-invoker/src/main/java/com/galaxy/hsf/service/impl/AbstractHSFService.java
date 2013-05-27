/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.service.HSFService;
import com.galaxy.hsf.service.ServiceInvoker;
import com.galaxy.hsf.service.ServiceRegister;
import com.galaxy.hsf.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractHSFService extends AbstractLifeCycle implements HSFService {

	private static final Log logger = LogFactory.getLog(AbstractHSFService.class);
	
	/**
	 * 
	 */
	protected ServiceRegister serviceRegister;
	
	/**
	 * 
	 */
	protected ServiceSubscriber serviceSubscriber;
	
	/**
	 * 
	 */
	protected ServiceInvoker serviceInvoker;
	
	/**
	 * 
	 * @param serviceRegister
	 * @param serviceSubscriber
	 * @param serviceInvoker
	 */
	public AbstractHSFService(ServiceRegister serviceRegister, ServiceSubscriber serviceSubscriber, ServiceInvoker serviceInvoker) {
		this.serviceRegister = serviceRegister;
		this.serviceSubscriber = serviceSubscriber;
		this.serviceInvoker = serviceInvoker;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		serviceRegister.initialize();
		serviceSubscriber.initialize();
		serviceInvoker.initialize();
	}

	@Override
	public void destroy() {
		super.destroy();
		serviceRegister.destroy();
		serviceSubscriber.destroy();
		serviceInvoker.destroy();
	}

	@Override
	public Object invoke(String method, String[] parameterTypes, Object[] args,
			String protocol, ServiceMetadata metadata) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(ServiceMetadata metadata) {
		logger.debug(String.format("Register service:%s", metadata.toString()));
		serviceRegister.register(metadata);
	}

	@Override
	public void unregister(ServiceMetadata metadata) {
		logger.debug(String.format("Unregister service:%s", metadata.toString()));
		serviceRegister.unregister(metadata);
	}

	@Override
	public void subscribe(ServiceMetadata metadata) {
		logger.debug(String.format("Subscribe service:%s", metadata.toString()));
		serviceSubscriber.subscribe(metadata);
	}

}
