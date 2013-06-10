/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network;

import java.io.IOException;
import java.net.SocketException;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.util.NetworkUtil;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractHSFNetworkServer extends AbstractLifeCycle implements HSFNetworkServer {
	
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
