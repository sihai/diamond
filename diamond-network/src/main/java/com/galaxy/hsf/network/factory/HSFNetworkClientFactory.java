/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.factory;

import com.galaxy.hsf.network.HSFNetworkClient;

/**
 * 
 * @author sihai
 *
 */
public interface HSFNetworkClientFactory {

	/**
	 * 
	 * @param serverIp
	 * @param serverPort
	 * @return
	 */
	HSFNetworkClient newNetworkClient(String serverIp, int serverPort);
}
