/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import com.galaxy.hsf.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface RPCProtocol extends LifeCycle {

	/**
	 * 
	 * @return
	 */
	String getProtocol();
	
}
