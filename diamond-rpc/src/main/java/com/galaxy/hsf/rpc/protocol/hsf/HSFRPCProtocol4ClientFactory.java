/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol.hsf;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.galaxy.hsf.network.factory.HSFNetworkClientFactory;
import com.galaxy.hsf.rpc.protocol.AbstractRPCProtocolFactory;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Client;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4ClientFactory;

/**
 * 
 * @author sihai
 *
 */
public class HSFRPCProtocol4ClientFactory extends AbstractRPCProtocolFactory implements RPCProtocol4ClientFactory {

	public static final String PROTOCOL = "hsf";
	
	@Override
	public RPCProtocol4Client newProtocol() {
		try {
			Properties properties = this.loadConfiguration(String.format("%s-client", PROTOCOL));
			Class clazz = Class.forName(StringUtils.trim((String)properties.get("network.client.factory")));
			HSFNetworkClientFactory clientFactory = (HSFNetworkClientFactory)clazz.newInstance();
			HSFRPCProtocol4Client protocol = new HSFRPCProtocol4Client(properties2Configuration(properties), clientFactory);
			protocol.initialize();
			protocol.start();
			return protocol;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
