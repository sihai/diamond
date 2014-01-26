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
package com.openteach.diamond.rpc.protocol;

import com.openteach.diamond.common.Request;
import com.openteach.diamond.common.Response;
import com.openteach.diamond.common.exception.DiamondException;

/**
 * 
 * @author sihai
 *
 */
public abstract class AbstractRPCProtocol4Client extends AbstractRPCProtocol implements RPCProtocol4Client {

	/**
	 * 
	 * @param configuration
	 */
	public AbstractRPCProtocol4Client(RPCProtocolConfiguration configuration) {
		super(configuration);
	}
	
	/**
	 * 
	 * @param request
	 * @throws DiamondException
	 */
	protected void before(Request request) throws DiamondException {
	}
	
	@Override
	public final Response invoke(Request request) throws DiamondException {
		before(request);
		Response response = invoke0(request);
		after(request, response);
		return response;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws DiamondException
	 */
	protected void after(Request request, Response response) throws DiamondException {
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws DiamondException
	 */
	protected abstract Response invoke0(Request request) throws DiamondException;

}
