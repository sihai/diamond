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
 * 
 */
package com.openteach.diamond.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.common.Request;
import com.openteach.diamond.common.Response;
import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.metadata.ServiceURL;
import com.openteach.diamond.router.ServiceRouter;
import com.openteach.diamond.rpc.RPCProtocolProvider;
import com.openteach.diamond.rpc.protocol.RPCProtocol4Client;
import com.openteach.diamond.util.ExceptionUtil;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceInvoker extends AbstractServiceInvoker {

	private static final Log logger = LogFactory.getLog(DefaultServiceInvoker.class);
	
	public static final int DEFAULT_RETRY_TIMES = 3;
	
	private int retryTimes = DEFAULT_RETRY_TIMES;
	
	/**
	 * 
	 * @param serviceRouter
	 * @param rpcProvider
	 */
	public DefaultServiceInvoker(ServiceRouter serviceRouter, RPCProtocolProvider rpcProtocolProvider) {
		this(serviceRouter, rpcProtocolProvider, DEFAULT_RETRY_TIMES);
	}
	
	/**
	 * 
	 * @param serviceRouter
	 * @param rpcProvider
	 * @param retryTimes
	 */
	public DefaultServiceInvoker(ServiceRouter serviceRouter,  RPCProtocolProvider rpcProtocolProvider, int retryTimes) {
		super(serviceRouter, rpcProtocolProvider);
		this.retryTimes = retryTimes;
	}
	
	@Override
	protected Object invoke0(Request request, List<ServiceURL> addresses) throws DiamondException {
		int i = 0;
		List<Throwable> exceptions = new ArrayList<Throwable>(retryTimes);
		for(; i < retryTimes && i < addresses.size(); i++) {
			try {
				ServiceURL serviceURL = addresses.get(i);
				RPCProtocol4Client rpcProtocol = rpcProtocolProvider.newRPCProtocol4Client(serviceURL);
				request.setServiceURL(serviceURL.toString());
				Response response = rpcProtocol.invoke(request);
				return response.getAppResponse();
			} catch (Throwable t) {
				t.printStackTrace();
				logger.error(String.format("Invoke address:%s, then try others", addresses.get(i)));
				exceptions.add(t);
			}
		}
		throw new DiamondException(String.format("Invoke service:%s, protocol:%s, failed, retry times:%d, addresses:%s\n", request.getServiceName(), request.getProtocol(), i, StringUtils.join(addresses.iterator(), ",")), ExceptionUtil.mergeException(exceptions));
	}
}
