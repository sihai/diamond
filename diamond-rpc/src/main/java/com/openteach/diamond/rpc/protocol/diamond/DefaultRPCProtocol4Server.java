/**
 * Copyright 2013 Qiangqiang RAO
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.openteach.diamond.rpc.protocol.diamond;

import java.net.MalformedURLException;
import java.util.Map;

import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.network.HSFNetworkServer;
import com.openteach.diamond.network.HSFNetworkServer.NetworkRequestHandler;
import com.openteach.diamond.network.factory.NetworkServerFactory;
import com.openteach.diamond.rpc.protocol.AbstractRPCProtocol4Server;
import com.openteach.diamond.rpc.protocol.RPCProtocolConfiguration;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCProtocol4Server extends AbstractRPCProtocol4Server {

	public static final String PROTOCOL = "diamond";
	
	/**
	 * 
	 */
	private NetworkServerFactory networkServerFactory;
	
	/**
	 * 
	 */
	private HSFNetworkServer networkServer;
	
	/**
	 * 
	 */
	private NetworkRequestHandler handler;
	
	/**
	 * 
	 * @param configuration
	 * @param networkServerFactory
	 * @param handler
	 */
	public DefaultRPCProtocol4Server(RPCProtocolConfiguration configuration, NetworkServerFactory networkServerFactory, NetworkRequestHandler handler) {
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
	public ServiceURL constructURL(String serviceName, Map<String, String> properties) {
		try {
			return new ServiceURL(String.format("%s://%s:%d/%s", getProtocol(), networkServer.getServerIp(), networkServer.getServerPort(), serviceName));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(String.format("Can not construct url for service:%s, protocol:%s", serviceName, getProtocol()), e);
		}
	}
	
}
