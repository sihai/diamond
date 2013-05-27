/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.factory;

import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.network.HSFNetworkServer.HSFRequestHandler;

/**
 * 
 * @author sihai
 *
 */
public interface HSFNetworkServerFactory {

	/**
	 * 
	 * @param handler
	 * @return
	 */
	HSFNetworkServer newNetworkServer(HSFRequestHandler handler);
}
