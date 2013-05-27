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
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @param protocol
	 * @return
	 * @throws HSFException
	 */
	Object invoke(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws HSFException;
}
