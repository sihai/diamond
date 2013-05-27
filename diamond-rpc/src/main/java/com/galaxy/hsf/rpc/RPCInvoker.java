/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc;

import java.lang.reflect.Method;

import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.LifeCycle;
import com.galaxy.hsf.metadata.ServiceMetadata;

/**
 * 
 * @author sihai
 *
 */
public interface RPCInvoker extends LifeCycle {
	
	/**
	 * 
	 * @param serviceURL
	 * @param metdata
	 * @param method
	 * @param args
	 * @return
	 * @throws HSFException
	 */
	HSFResponse invoke(String serviceURL, ServiceMetadata metdata, Method method, Object ... args) throws HSFException;
}
