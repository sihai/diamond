/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol.hsf;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.galaxy.hsf.network.HSFNetworkServer.HSFRequestHandler;
import com.galaxy.hsf.network.factory.HSFNetworkServerFactory;
import com.galaxy.hsf.rpc.protocol.AbstractRPCProtocolFactory;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Server;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4ServerFactory;

/**
 * 
 * @author sihai
 *
 */
public class HSFRPCProtocol4ServerFactory extends AbstractRPCProtocolFactory implements RPCProtocol4ServerFactory {

	public static final String PROTOCOL = "hsf";
	
	@Override
	public RPCProtocol4Server newProtocol(HSFRequestHandler handler) {
		try {
			Properties properties = this.loadConfiguration(String.format("%s-server", PROTOCOL));
			Class clazz = Class.forName(StringUtils.trim((String)properties.get("network.server.factory")));
			HSFNetworkServerFactory serverFactory = (HSFNetworkServerFactory)clazz.newInstance();
			HSFRPCProtocol4Server protocol = new HSFRPCProtocol4Server(properties2Configuration(properties), serverFactory, handler);
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
