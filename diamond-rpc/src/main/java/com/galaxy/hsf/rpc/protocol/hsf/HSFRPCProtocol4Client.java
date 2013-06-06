/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol.hsf;

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

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.network.HSFNetworkClient;
import com.galaxy.hsf.network.exception.NetworkException;
import com.galaxy.hsf.network.factory.HSFNetworkClientFactory;
import com.galaxy.hsf.rpc.protocol.AbstractRPCProtocol4Client;
import com.galaxy.hsf.rpc.protocol.RPCProtocolConfiguration;

/**
 * 
 * @author sihai
 *
 */
public class HSFRPCProtocol4Client extends AbstractRPCProtocol4Client {

	public static final String PROTOCOL = "hsf";
	
	private static final Log logger = LogFactory.getLog(HSFRPCProtocol4Client.class);
	
	/**
	 * 
	 */
	private HSFNetworkClientFactory networkClientFactory;
	
	/**
	 * 
	 */
	private ConcurrentHashMap<Target, HSFNetworkClientsHolder> targetMap;
	
	/**
	 * 
	 * @param configuration
	 * @param networkClientFactory
	 */
	public HSFRPCProtocol4Client(RPCProtocolConfiguration configuration, HSFNetworkClientFactory networkClientFactory) {
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
	protected HSFResponse invoke0(HSFRequest request) throws HSFException {
		Target target = new Target(request.getServiceURL().getHost(), request.getServiceURL().getPort());
		HSFNetworkClient networkClient = allocHSFNetworkClient(target);
		try {
			return (HSFResponse)networkClient.syncrequest(request);
		} catch (NetworkException e) {
			throw new HSFException(e);
		}
	}
	
	/**
	 * 
	 * @param target
	 * @return
	 */
	private HSFNetworkClient allocHSFNetworkClient(Target target) {
		HSFNetworkClientsHolder holder = targetMap.get(target);
		if(null == holder || holder.size() < configuration.getMaxSessionPreHost()) {
			HSFNetworkClient networkClient = networkClientFactory.newNetworkClient(target.serverIp, target.port);
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
		
		List<HSFNetworkClient> networkClientList = new ArrayList<HSFNetworkClient>(configuration.getMaxSessionPreHost());
		
		/**
		 * 
		 * @return
		 */
		public HSFNetworkClient random() {
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
		public void addNetworkClient(HSFNetworkClient networkClient) {
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
				for(Iterator<HSFNetworkClient> iterator = networkClientList.iterator(); iterator.hasNext();) {
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
