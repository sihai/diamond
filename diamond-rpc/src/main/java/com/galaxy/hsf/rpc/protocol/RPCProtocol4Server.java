/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import java.net.URL;
import java.util.Properties;

/**
 * 
 * @author sihai
 *
 */
public interface RPCProtocol4Server extends RPCProtocol {

	/**
	 * 
	 * @return
	 */
	String getServerIp();
	
	/**
	 * 
	 * @return
	 */
	int getServerPort();
	
	/**
	 * 
	 * @param serviceName
	 * @param properties
	 * @return
	 */
	URL constructURL(String serviceName, Properties properties);
}
