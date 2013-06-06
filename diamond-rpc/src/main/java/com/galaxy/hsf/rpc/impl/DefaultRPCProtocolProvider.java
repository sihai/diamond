/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;

import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.network.HSFNetworkServer.ResponseCallback;
import com.galaxy.hsf.network.NetworkRequest;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
import com.galaxy.hsf.rpc.protocol.RPCProtocol;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Client;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4ClientFactory;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Server;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4ServerFactory;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCProtocolProvider extends AbstractLifeCycle implements RPCProtocolProvider {

	/**
	 * 
	 */
	public static final String PROTOCOL_PROVIDER_CONFIG_FILE_NAME = "protocol-provider.cnf";
	
	/**
	 * 
	 */
	private Map<String, RPCProtocol4Client> protocol4ClientMap = new HashMap<String, RPCProtocol4Client>();
	
	/**
	 * 
	 */
	private ReadWriteLock _rw_lock_c_ = new ReentrantReadWriteLock();
	
	/**
	 * 
	 */
	private Map<String, RPCProtocol4Server> protocol4ServerMap = new HashMap<String, RPCProtocol4Server>();
	
	/**
	 * 
	 */
	private ReadWriteLock _rw_lock_s_ = new ReentrantReadWriteLock();
	
	/**
	 * 
	 */
	private HSFNetworkServer.NetworkRequestHandler handler;
	
	/**
	 * 
	 */
	private Properties properties;
	
	/**
	 * 
	 * @param handler
	 */
	public DefaultRPCProtocolProvider(HSFNetworkServer.NetworkRequestHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		properties = new Properties();
		try {
			// load default
			properties.load(this.getClass().getResourceAsStream(String.format("/rpc/protocols/%s", PROTOCOL_PROVIDER_CONFIG_FILE_NAME)));
			// try to load custom configuration override
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(String.format("%s", PROTOCOL_PROVIDER_CONFIG_FILE_NAME));
			if(null != in) {
				properties.load(in);
			}
		} catch (IOException e) {
			throw new RuntimeException(String.format("Load configuraiton for rpc provider failed, file name:%s", PROTOCOL_PROVIDER_CONFIG_FILE_NAME), e);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		// XXX
		try {
			_rw_lock_c_.writeLock().lock();
			for(RPCProtocol protocol : protocol4ClientMap.values()) {
				protocol.stop();
				protocol.destroy();
			}
			protocol4ClientMap.clear();
		} finally {
			_rw_lock_c_.writeLock().unlock();
		}
		
		try {
			_rw_lock_s_.writeLock().lock();
			for(RPCProtocol protocol : protocol4ServerMap.values()) {
				protocol.stop();
				protocol.destroy();
			}
			protocol4ServerMap.clear();
		} finally {
			_rw_lock_s_.writeLock().unlock();
		}
	}

	@Override
	public RPCProtocol4Client register(RPCProtocol4Client protocol) {
		try {
			_rw_lock_c_.writeLock().lock();
			return protocol4ClientMap.put(protocol.getProtocol(), protocol);
		} finally {
			_rw_lock_c_.writeLock().unlock();
		}
	}
	
	@Override
	public RPCProtocol4Client unregisterRPCProtocol4Client(String protocol) {
		try {
			_rw_lock_c_.writeLock().lock();
			return protocol4ClientMap.remove(protocol);
		} finally {
			_rw_lock_c_.writeLock().unlock();
		}
	}
	
	@Override
	public RPCProtocol4Server register(RPCProtocol4Server protocol) {
		try {
			_rw_lock_s_.writeLock().lock();
			return protocol4ServerMap.put(protocol.getProtocol(), protocol);
		} finally {
			_rw_lock_s_.writeLock().unlock();
		}
	}
	
	@Override
	public RPCProtocol4Server unregisterRPCProtocol4Server(String protocol) {
		try {
			_rw_lock_s_.writeLock().lock();
			return protocol4ServerMap.remove(protocol);
		} finally {
			_rw_lock_s_.writeLock().unlock();
		}
	}
	
	@Override
	public RPCProtocol4Client newRPCProtocol4Client(String protocol) throws HSFException {
		RPCProtocol4Client p = null;
		try {
			_rw_lock_c_.readLock().lock();
			p = protocol4ClientMap.get(protocol);
			if(null != p) {
				return p;
			}
		} finally {
			_rw_lock_c_.readLock().unlock();
		}
		
		// try to load protocol
		p = tryLoadRPCProtocol4Client(protocol);
		if(null == p) {
			throw new HSFException(String.format("Not supported protocol:%s", protocol));
		}
		return p;
	}

	@Override
	public RPCProtocol4Client newRPCProtocol4Client(URL url) throws HSFException {
		return newRPCProtocol4Client(url.getProtocol());
	}
	
	@Override
	public RPCProtocol4Server newRPCProtocol4Server(String protocol) throws HSFException {
		RPCProtocol4Server p = null;
		try {
			_rw_lock_s_.readLock().lock();
			p = protocol4ServerMap.get(protocol);
			if(null != p) {
				return p;
			}
		} finally {
			_rw_lock_s_.readLock().unlock();
		}
		
		// try to load protocol
		p = tryLoadRPCProtocol4Server(protocol);
		if(null == p) {
			throw new HSFException(String.format("Not supported protocol:%s", protocol));
		}
		return p;
	}

	@Override
	public RPCProtocol4Server newRPCProtocol4Server(URL url) throws HSFException {
		return newRPCProtocol4Server(url.getProtocol());
	}
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	private RPCProtocol4Client tryLoadRPCProtocol4Client(String protocol) {
		try {
			_rw_lock_c_.writeLock().lock();
			RPCProtocol4Client p = protocol4ClientMap.get(protocol);
			if(null != p) {
				return p;
			}
			p = loadRPCProtocol4Client(protocol);
			if(null != p) {
				protocol4ClientMap.put(protocol, p);
			}
			return p;
		} finally {
			_rw_lock_c_.writeLock().unlock();
		}
	}
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	private RPCProtocol4Server tryLoadRPCProtocol4Server(String protocol) {
		try {
			_rw_lock_s_.writeLock().lock();
			RPCProtocol4Server p = protocol4ServerMap.get(protocol);
			if(null != p) {
				return p;
			}
			p = loadRPCProtocol4Server(protocol);
			if(null != p) {
				protocol4ServerMap.put(protocol, p);
			}
			return p;
			
		} finally {
			_rw_lock_s_.writeLock().unlock();
		}
	}
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	private RPCProtocol4Client loadRPCProtocol4Client(String protocol) {
		try {
			String factoryClassName = StringUtils.trim(properties.getProperty(String.format("protocol.%s.client.factory", protocol)));
			if(StringUtils.isBlank(factoryClassName)) {
				return null;
			}
			Class clazz = Class.forName(factoryClassName);
			if(!RPCProtocol4ClientFactory.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(String.format("RPC protocol 4 client factory:%s must implements %s", factoryClassName, RPCProtocol4ClientFactory.class.getName()));
			}
			RPCProtocol4ClientFactory factory = (RPCProtocol4ClientFactory)clazz.newInstance();
			return factory.newProtocol();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	private RPCProtocol4Server loadRPCProtocol4Server(String protocol) {
		try {
			String factoryClassName = StringUtils.trim(properties.getProperty(String.format("protocol.%s.server.factory", protocol)));
			if(StringUtils.isBlank(factoryClassName)) {
				return null;
			}
			Class clazz = Class.forName(factoryClassName);
			if(!RPCProtocol4ServerFactory.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(String.format("RPC protocol 4 client factory:%s must implements %s", factoryClassName, RPCProtocol4ServerFactory.class.getName()));
			}
			RPCProtocol4ServerFactory factory = (RPCProtocol4ServerFactory)clazz.newInstance();
			return factory.newProtocol(handler);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static void main(String[] args) {
		try {
			DefaultRPCProtocolProvider provider = new DefaultRPCProtocolProvider(new HSFNetworkServer.NetworkRequestHandler() {

				@Override
				public void handle(NetworkRequest request, ResponseCallback callback) {
					callback.completed(request, new HSFResponse());
				}
				
			});
			provider.initialize();
			provider.start();
			provider.newRPCProtocol4Client(new URL("hsf://127.0.0.1:8206/demoService"));
			provider.newRPCProtocol4Server(new URL("hsf://127.0.0.1:8206/demoService"));
		} catch (HSFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
