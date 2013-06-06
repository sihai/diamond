/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import com.galaxy.hsf.network.NetworkRequest;
import com.galaxy.hsf.network.NetworkResponse;

/**
 * 
 * @author sihai
 *
 */
public interface HSFWaverider {

	Long COMMAND_HSF_INVOKE = 99L;
	Long COMMAND_HSF_RESPONSE = 100L;
	
	class PendingRequest {
		NetworkRequest request;
		NetworkResponse response;
		
		/**
		 * 
		 * @param request
		 * @param response
		 */
		public PendingRequest(NetworkRequest request, NetworkResponse response) {
			this.request = request;
			this.response = response;
		}
	}
}
