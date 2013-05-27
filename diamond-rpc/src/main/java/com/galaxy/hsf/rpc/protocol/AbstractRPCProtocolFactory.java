/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRPCProtocolFactory {

	/**
	 * 
	 * @param fullProtocolName
	 * @return
	 */
	protected Properties loadConfiguration(String fullProtocolName) {
		try {
			Properties properties = new Properties();
			InputStream in =Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("/rpc/protocols/client/%s.cnf", fullProtocolName));
			properties.load(in);
			
			// try to load custom override
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("%s.cnf", fullProtocolName));
			if(null != in) {
				properties.load(in); 
			}
			properties.load(in);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(String.format("Load configuration for protocol:%s failed", fullProtocolName), e);
		}
	}
	
	/**
	 * 
	 * @param properties
	 * @return
	 */
	protected RPCProtocolConfiguration properties2Configuration(Properties properties) {
		// TODO
		return new RPCProtocolConfiguration();
	}
}
