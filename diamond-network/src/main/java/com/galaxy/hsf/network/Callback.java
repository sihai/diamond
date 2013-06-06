/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network;

import com.galaxy.hsf.network.exception.NetworkException;

/**
 * 
 * @author sihai
 *
 */
public interface Callback {

	/**
	 * 
	 * @param request
	 * @param response
	 */
	void succeed(Object request, Object response);
	
	/**
	 * 
	 * @param request
	 * @param exception
	 */
	void failed(Object request, NetworkException exception);
}
