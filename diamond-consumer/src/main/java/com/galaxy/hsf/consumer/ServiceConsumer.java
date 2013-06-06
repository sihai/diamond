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
	 * @param methodName
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws HSFException
	 */
	Object invoke(String methodName, String[] parameterTypes, Object ... args) throws HSFException;
}
