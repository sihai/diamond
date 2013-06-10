/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

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
			InputStream in = this.getClass().getResourceAsStream(String.format("/rpc/protocols/%s.cnf", fullProtocolName));
			properties.load(in);
			
			// try to load custom override
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("%s.cnf", fullProtocolName));
			if(null != in) {
				properties.load(in); 
			}
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
		RPCProtocolConfiguration configuration = new RPCProtocolConfiguration();
		String value = properties.getProperty("network.max.sessionPreHost");
		if(StringUtils.isNotBlank(value)) {
			configuration.setMaxSessionPreHost(Integer.valueOf(StringUtils.trim(value)));
		}
		return configuration;
	}
}
