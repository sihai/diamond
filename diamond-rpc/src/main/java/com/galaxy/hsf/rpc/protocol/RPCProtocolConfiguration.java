/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

/**
 * 
 * @author sihai
 *
 */
public class RPCProtocolConfiguration {

	public static final int DEFAULT_MAX_SESSION_PRE_HOST = 32;
	
	/**
	 * 
	 */
	private int maxSessionPreHost = DEFAULT_MAX_SESSION_PRE_HOST;

	public int getMaxSessionPreHost() {
		return maxSessionPreHost;
	}

	public void setMaxSessionPreHost(int maxSessionPreHost) {
		this.maxSessionPreHost = maxSessionPreHost;
	}
}
