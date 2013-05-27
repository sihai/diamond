/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;


/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRPCProtocol extends AbstractLifeCycle implements RPCProtocol {

	/**
	 * 
	 */
	protected RPCProtocolConfiguration configuration;
	
	/**
	 * 
	 * @param configuration
	 */
	public AbstractRPCProtocol(RPCProtocolConfiguration configuration) {
		this.configuration = configuration;
	}
}
