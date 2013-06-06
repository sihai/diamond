/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
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
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	public void register(ServiceMetadata metadata) throws HSFException {
		serviceRegister.register(metadata);
	}

	public void unregister(ServiceMetadata metadata) throws HSFException {
		serviceRegister.unregister(metadata);
	}

	public void subscribe(ServiceMetadata metadata) throws HSFException {
		serviceSubscriber.subscribe(metadata);
	}

	public Object invoke(String serviceName, String method,
			String[] parameterTypes, Object[] args, String protocol)
			throws HSFException {
		return serviceInvoker.invoke(serviceName, method, parameterTypes, args,
				protocol);
	}
	
}
