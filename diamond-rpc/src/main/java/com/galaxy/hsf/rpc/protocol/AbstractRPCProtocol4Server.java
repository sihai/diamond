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
public abstract class AbstractRPCProtocol4Server extends AbstractRPCProtocol implements RPCProtocol4Server {
	
	/**
	 * 
	 * @param configuration
	 */
	public AbstractRPCProtocol4Server(RPCProtocolConfiguration configuration) {
		super(configuration);
	}
}
