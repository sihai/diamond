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
	void succeed(Request request, Response response);
	
	/**
	 * 
	 * @param request
	 * @param exception
	 */
	void failed(Request request, NetworkException exception);
}
