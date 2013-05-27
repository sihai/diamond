/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import com.galaxy.hsf.network.Request;
import com.galaxy.hsf.network.Response;

/**
 * 
 * @author sihai
 *
 */
public interface HSFWaverider {

	Long COMMAND_HSF_INVOKE = 99L;
	Long COMMAND_HSF_RESPONSE = 100L;
	
	class PendingRequest {
		Request request;
		Response response;
		
		/**
		 * 
		 * @param request
		 * @param response
		 */
		public PendingRequest(Request request, Response response) {
			this.request = request;
			this.response = response;
		}
	}
}
