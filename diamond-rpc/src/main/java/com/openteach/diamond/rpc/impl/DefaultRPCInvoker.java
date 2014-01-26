/**
 * Copyright 2013 Qiangqiang RAO
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
package com.openteach.diamond.rpc.impl;

import com.openteach.diamond.common.Request;
import com.openteach.diamond.common.Response;
import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.network.HSFNetworkServer;
import com.openteach.diamond.rpc.protocol.RPCProtocol4Client;

/**
 * 
 * @author sihai
 *
 */
public class DefaultRPCInvoker extends AbstractRPCInvoker {
	
	/**
	 * 
	 * @param handler
	 */
	public DefaultRPCInvoker(HSFNetworkServer.NetworkRequestHandler handler) {
		super(handler);
	}
	
	@Override
	public Response invoke(Request request) throws DiamondException {
		RPCProtocol4Client protocol = rpcProvider.newRPCProtocol4Client(request.getServiceURL());
		return protocol.invoke(request);
	}

}
