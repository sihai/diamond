/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
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
	HSFRequestHandler register(HSFRequestHandler handler);
	
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
		void completed(HSFRequest request, HSFResponse response);
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	interface HSFRequestHandler {
		
		/**
		 * 
		 * @param request
		 * @param callback
		 */
		void handle(HSFRequest request, ResponseCallback callback);
	}
}
