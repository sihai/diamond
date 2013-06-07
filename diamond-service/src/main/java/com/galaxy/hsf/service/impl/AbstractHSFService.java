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
import com.galaxy.hsf.service.MethodInvoker;
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

	@Override
	public void register(ServiceMetadata metadata, MethodInvoker invoker) throws HSFException {
		serviceRegister.register(metadata, invoker);
		register(metadata.getUniqueName(), invoker);
	}

	@Override
	public void unregister(ServiceMetadata metadata) throws HSFException {
		serviceRegister.unregister(metadata);
		unregister(metadata.getUniqueName());
	}

	@Override
	public void subscribe(ServiceMetadata metadata) throws HSFException {
		serviceSubscriber.subscribe(metadata);
	}

	@Override
	public void register(String serviceName, MethodInvoker invoker) {
		serviceInvoker.register(serviceName, invoker);
	}

	@Override
	public void unregister(String serviceName) {
		serviceInvoker.unregister(serviceName);
	}
	@Override
	public Object invokeLocal(String serviceName, String method, String[] parameterTypes, Object[] args) throws HSFException {
		return serviceInvoker.invokeLocal(serviceName, method, parameterTypes, args);
	}
	
	@Override
	public Object invokeRemote(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws HSFException {
		return serviceInvoker.invokeRemote(serviceName, method, parameterTypes, args, protocol);
	}
	
}
