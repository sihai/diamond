/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.common.exception.HSFException;

/**
 * 
 * @author sihai
 *
 */
public interface MethodInvoker {

	/**
	 * 
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws HSFException
	 */
	Object invoke(String method, String[] parameterTypes, Object[] args) throws HSFException;
}
