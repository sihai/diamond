/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.consumer;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.LifeCycle;


/**
 * 
 * @author sihai
 *
 */
public interface ServiceConsumer extends LifeCycle {

	/**
	 * 
	 * @return
	 */
	ServiceMetadata getMetadata();
	
	/**
	 * 
	 * @return
	 */
	Object getProxy();
	
	/**
	 * 
	 * @param methodName
	 * @param parameterTypes
	 * @param args
	 * @param protocol
	 * @return
	 * @throws HSFException
	 */
	Object invoke(String methodName, String[] parameterTypes, Object[] args, String protocol) throws HSFException;
}
