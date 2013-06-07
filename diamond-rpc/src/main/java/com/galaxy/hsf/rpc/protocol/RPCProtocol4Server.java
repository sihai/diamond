/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import java.util.Properties;

import com.galaxy.hsf.rpc.ServiceURL;

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
	ServiceURL constructURL(String serviceName, Properties properties);
}
