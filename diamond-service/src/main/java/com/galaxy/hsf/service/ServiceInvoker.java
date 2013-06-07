/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceInvoker extends LifeCycle {

	/**
	 * 
	 * @param name
	 * @param invoker
	 * @return
	 */
	void register(String serviceName, MethodInvoker invoker);
	
	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	void unregister(String serviceName);
	
	/**
	 * 
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws HSFException
	 */
	Object invokeLocal(String serviceName, String method, String[] parameterTypes, Object[] args) throws HSFException;
	
	/**
	 * 
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @param protocol
	 * @return
	 * @throws HSFException
	 */
	Object invokeRemote(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws HSFException;
}
