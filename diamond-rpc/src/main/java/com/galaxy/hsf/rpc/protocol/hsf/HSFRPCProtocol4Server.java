/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol.hsf;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.network.HSFNetworkServer.HSFRequestHandler;
import com.galaxy.hsf.network.factory.HSFNetworkServerFactory;
import com.galaxy.hsf.rpc.protocol.AbstractRPCProtocol4Server;
import com.galaxy.hsf.rpc.protocol.RPCProtocolConfiguration;

/**
 * 
 * @author sihai
 *
 */
public class HSFRPCProtocol4Server extends AbstractRPCProtocol4Server {

	public static final String PROTOCOL = "hsf";
	
	/**
	 * 
	 */
	private HSFNetworkServerFactory networkServerFactory;
	
	/**
	 * 
	 */
	private HSFNetworkServer networkServer;
	
	/**
	 * 
	 */
	private HSFRequestHandler handler;
	
	/**
	 * 
	 * @param configuration
	 * @param networkServerFactory
	 * @param handler
	 */
	public HSFRPCProtocol4Server(RPCProtocolConfiguration configuration, HSFNetworkServerFactory networkServerFactory, HSFRequestHandler handler) {
		super(configuration);
		this.networkServerFactory = networkServerFactory;
		this.handler = handler;
	}

	@Override
	public void initialize() {
		super.initialize();
		networkServer = networkServerFactory.newNetworkServer(handler);
	}

	@Override
	public void destroy() {
		super.destroy();
		networkServer.destroy();
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}
 
	@Override
	public String getServerIp() {
		return networkServer.getServerIp();
	}

	@Override
	public int getServerPort() {
		return networkServer.getServerPort();
	}

	@Override
	public URL constructURL(String serviceName, Properties properties) {
		try {
			return new URL(String.format("%s://%s:%d/%s", getProtocol(), networkServer.getServerIp(), networkServer.getServerPort(), serviceName));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(String.format("Can not construct url for service:%s, protocol:%s", serviceName, getProtocol()), e);
		}
	}
	
}
