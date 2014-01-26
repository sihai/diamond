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
package com.openteach.diamond.rpc;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.LifeCycle;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.rpc.protocol.RPCProtocol4Client;
import com.openteach.diamond.rpc.protocol.RPCProtocol4Server;

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
	RPCProtocol4Client newRPCProtocol4Client(String protocol) throws DiamondException;
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws DiamondException
	 */
	RPCProtocol4Client newRPCProtocol4Client(ServiceURL url) throws DiamondException;
	
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
	RPCProtocol4Server newRPCProtocol4Server(String protocol) throws DiamondException;
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws DiamondException
	 */
	RPCProtocol4Server newRPCProtocol4Server(ServiceURL url) throws DiamondException;

	
	
}
