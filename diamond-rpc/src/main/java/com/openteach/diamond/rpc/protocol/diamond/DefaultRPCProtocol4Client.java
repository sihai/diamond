/**
 * Copyright 2013 openteach
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.common.Request;
import com.openteach.diamond.common.Response;
import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.network.NetworkClient;
import com.openteach.diamond.network.exception.NetworkException;
import com.openteach.diamond.network.factory.NetworkClientFactory;
import com.openteach.diamond.rpc.protocol.AbstractRPCProtocol4Client;
import com.openteach.diamond.rpc.protocol.RPCProtocolConfiguration;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCProtocol4Client extends AbstractRPCProtocol4Client {

	public static final String PROTOCOL = "diamond";
	
	private static final Log logger = LogFactory.getLog(DefaultRPCProtocol4Client.class);
	
	/**
	 * 
	 */
	private NetworkClientFactory networkClientFactory;
	
	/**
	 * 
	 */
	private ConcurrentHashMap<Target, HSFNetworkClientsHolder> targetMap;
	
	/**
	 * 
	 * @param configuration
	 * @param networkClientFactory
	 */
	public DefaultRPCProtocol4Client(RPCProtocolConfiguration configuration, NetworkClientFactory networkClientFactory) {
		super(configuration);
		this.networkClientFactory = networkClientFactory;
	}

	@Override
	public void initialize() {
		super.initialize();
		targetMap = new ConcurrentHashMap<Target, HSFNetworkClientsHolder>();
	}

	@Override
	public void destroy() {
		super.destroy();
		HSFNetworkClientsHolder holder = null;
		for(Iterator<Target> iterator = targetMap.keySet().iterator(); iterator.hasNext();) {
			holder = targetMap.get(iterator.next());
			if(null != holder) {
				holder.destroy();
			}
		}
		targetMap.clear();
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	@Override
	protected Response invoke0(Request request) throws DiamondException {
		try {
			ServiceURL url = new ServiceURL(request.getServiceURL());
			Target target = new Target(url.getHost(), url.getPort());
			NetworkClient networkClient = allocHSFNetworkClient(target);
			return (Response)networkClient.syncrequest(request);
		} catch (MalformedURLException e) {
			throw new DiamondException(e);
		} catch (NetworkException e) {
			throw new DiamondException(e);
		}
	}
	
	/**
	 * 
	 * @param target
	 * @return
	 */
	private NetworkClient allocHSFNetworkClient(Target target) {
		HSFNetworkClientsHolder holder = targetMap.get(target);
		if(null == holder || holder.size() < configuration.getMaxSessionPreHost()) {
			NetworkClient networkClient = networkClientFactory.newNetworkClient(target.serverIp, target.port);
			if(null == holder) {
				holder = new HSFNetworkClientsHolder();
				HSFNetworkClientsHolder old = targetMap.putIfAbsent(target, holder);
				if(null != old) {
					holder = old;
				}
			}
			holder.addNetworkClient(networkClient);
		}
		return holder.random();
	}

	/**
	 * 
	 * @author sihai
	 *
	 */
	private class Target {
		String serverIp;
		int port;
		
		/**
		 * 
		 * @param serverIp
		 * @param port
		 */
		public Target(String serverIp, int port) {
			this.serverIp = serverIp;
			this.port = port;
		}

		@Override
		public int hashCode() {
			return 32 * serverIp.hashCode() + 32 * port;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(!(obj instanceof Target)) {
				return false;
			}
			return StringUtils.equals(serverIp, ((Target)obj).serverIp) && port == ((Target)obj).port;
		}
		
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	private class HSFNetworkClientsHolder {
		
		ReadWriteLock _rw_lock_ = new ReentrantReadWriteLock();
		
		List<NetworkClient> networkClientList = new ArrayList<NetworkClient>(configuration.getMaxSessionPreHost());
		
		/**
		 * 
		 * @return
		 */
		public NetworkClient random() {
			try {
				_rw_lock_.readLock().lock();
				return networkClientList.get(new Random(System.currentTimeMillis()).nextInt(networkClientList.size()));
			} finally {
				_rw_lock_.readLock().unlock();
			}
		}
		
		/**
		 * 
		 * @return
		 */
		public int size() {
			try {
				_rw_lock_.readLock().lock();
				return networkClientList.size();
			} finally {
				_rw_lock_.readLock().unlock();
			}
		}
		
		/**
		 * 
		 * @param networkClient
		 */
		public void addNetworkClient(NetworkClient networkClient) {
			try {
				_rw_lock_.writeLock().lock();
				networkClientList.add(networkClient);
			} finally {
				_rw_lock_.writeLock().unlock();
			}
		}
		
		/**
		 * 
		 */
		public void destroy() {
			try {
				_rw_lock_.writeLock().lock();
				for(Iterator<NetworkClient> iterator = networkClientList.iterator(); iterator.hasNext();) {
					try {
						iterator.next().destroy();
					} catch (Throwable t) {
						logger.error("OOPS, Stop one HSFNetworkClient failed", t);
					}
					iterator.remove();
				}
			} finally {
				_rw_lock_.writeLock().unlock();
			}
		}
	}
}
