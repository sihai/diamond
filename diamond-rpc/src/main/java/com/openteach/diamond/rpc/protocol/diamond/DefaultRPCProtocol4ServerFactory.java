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

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.openteach.diamond.network.HSFNetworkServer.NetworkRequestHandler;
import com.openteach.diamond.network.factory.NetworkServerFactory;
import com.openteach.diamond.rpc.protocol.AbstractRPCProtocolFactory;
import com.openteach.diamond.rpc.protocol.RPCProtocol4Server;
import com.openteach.diamond.rpc.protocol.RPCProtocol4ServerFactory;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCProtocol4ServerFactory extends AbstractRPCProtocolFactory implements RPCProtocol4ServerFactory {

	@Override
	public RPCProtocol4Server newProtocol(NetworkRequestHandler handler) {
		try {
			Properties properties = this.loadConfiguration(String.format("%s-server", DefaultRPCProtocol4Server.PROTOCOL));
			Class clazz = Class.forName(StringUtils.trim((String)properties.get("network.server.factory")));
			NetworkServerFactory serverFactory = (NetworkServerFactory)clazz.newInstance();
			DefaultRPCProtocol4Server protocol = new DefaultRPCProtocol4Server(properties2Configuration(properties), serverFactory, handler);
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
