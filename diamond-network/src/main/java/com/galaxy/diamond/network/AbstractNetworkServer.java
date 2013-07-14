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
package com.galaxy.diamond.network;

import java.io.IOException;
import java.net.SocketException;

import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.util.NetworkUtil;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractNetworkServer extends AbstractLifeCycle implements HSFNetworkServer {
	
	/**
	 * 
	 */
	private volatile int port;
	
	/**
	 * 
	 */
	private volatile String serverIp;
	
	/**
	 * 
	 */
	protected NetworkRequestHandler handler;
	
	@Override
	public int getServerPort() {
		if(0 == port) {
			synchronized(this) {
				if(0 == port) {
					try {
						port = NetworkUtil.getFreePort();
					} catch (IOException e) {
						throw new RuntimeException("Can not get free port", e);
					}
				}
			}
		}
		return port;
	}

	@Override
	public String getServerIp() {
		if(null == serverIp) {
			synchronized(this) {
				if(null == serverIp) {
					try {
						serverIp = NetworkUtil.getLocalIp();
					} catch (SocketException e) {
						throw new RuntimeException("Can not get ip of this host", e);
					}
				}
			}
		}
		return serverIp;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
	}

	@Override
	public NetworkRequestHandler register(NetworkRequestHandler handler) {
		NetworkRequestHandler old = this.handler;
		this.handler = handler;
		return old;
	}
}
