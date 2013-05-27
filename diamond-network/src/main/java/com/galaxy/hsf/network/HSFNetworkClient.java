/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network;

import com.galaxy.hsf.common.lifecycle.LifeCycle;
import com.galaxy.hsf.network.exception.NetworkException;


/**
 * 
 * @author sihai
 *
 */
public interface HSFNetworkClient extends LifeCycle {

	/**
	 * 
	 * @param request
	 * @return
	 * @throws NetworkException
	 */
	Response syncrequest(Request request) throws NetworkException;
	
	/**
	 * 
	 * @param request
	 * @param callback
	 */
	void asyncrequest(Request request, Callback callback);
}
