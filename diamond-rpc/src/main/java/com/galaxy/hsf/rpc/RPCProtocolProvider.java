/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc;

import java.net.URL;

import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.lifecycle.LifeCycle;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Client;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Server;

/**
 * 
 * @author sihai
 *
 */
public interface RPCProtocolProvider extends LifeCycle {

	//====================================================================
	//					Client Side
	//====================================================================
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	RPCProtocol4Client register(RPCProtocol4Client protocol);
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	RPCProtocol4Client unregisterRPCProtocol4Client(String protocol);
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	RPCProtocol4Client newRPCProtocol4Client(String protocol) throws HSFException;
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws HSFException
	 */
	RPCProtocol4Client newRPCProtocol4Client(URL url) throws HSFException;
	
	//====================================================================
	//					Server Side
	//====================================================================
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	RPCProtocol4Server register(RPCProtocol4Server protocol);
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	RPCProtocol4Server unregisterRPCProtocol4Server(String protocol);
	
	/**
	 * 
	 * @param protocol
	 * @return
	 */
	RPCProtocol4Server newRPCProtocol4Server(String protocol) throws HSFException;
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws HSFException
	 */
	RPCProtocol4Server newRPCProtocol4Server(URL url) throws HSFException;

	
	
}
