/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network;

import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface HSFNetworkServer extends LifeCycle {

	/**
	 * 
	 * @param handler
	 * @return
	 */
	NetworkRequestHandler register(NetworkRequestHandler handler);
	
	/**
	 * 
	 * @return
	 */
	int getServerPort();
	
	/**
	 * 
	 * @return
	 */
	String getServerIp();
	
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	interface ResponseCallback {
		
		/**
		 * 
		 * @param request
		 * @param response
		 */
		void completed(NetworkRequest request, Object response);
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	interface NetworkRequestHandler {
		
		/**
		 * 
		 * @param request
		 * @param callback
		 */
		void handle(NetworkRequest request, ResponseCallback callback);
	}
}
