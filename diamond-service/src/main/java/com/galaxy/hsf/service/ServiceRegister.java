/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceRegister extends LifeCycle {

	/**
	 * 
	 * @param metadata
	 * @param invoker
	 * @throws HSFException
	 */
	void register(ServiceMetadata metadata, MethodInvoker invoker) throws HSFException;
	
	/**
	 * 
	 * @param metadata
	 * @throws HSFException
	 */
	void unregister(ServiceMetadata metadata) throws HSFException;
}
