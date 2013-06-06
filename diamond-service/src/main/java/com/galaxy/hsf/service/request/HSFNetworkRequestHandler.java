/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.request;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.network.HSFNetworkServer.NetworkRequestHandler;
import com.galaxy.hsf.network.HSFNetworkServer.ResponseCallback;
import com.galaxy.hsf.network.NetworkRequest;
import com.galaxy.hsf.service.HSFServiceFactory;
import com.galaxy.hsf.service.request.executor.HSFRequestExecutor;

/**
 * 
 * @author sihai
 *
 */
public class HSFNetworkRequestHandler implements NetworkRequestHandler {

	/**
	 * 
	 */
	private HSFRequestExecutor executor;
	
	/**
	 * 
	 * @param factory
	 */
	public HSFNetworkRequestHandler(HSFRequestExecutor executor) {
		this.executor = executor;
	}
	
	@Override
	public void handle(NetworkRequest request, ResponseCallback callback) {
		// FIXME
		HSFResponse response = executor.execute(HSFServiceFactory.getHSFService(), (HSFRequest)request.getPayload());
		callback.completed(request, response);
	}

}
