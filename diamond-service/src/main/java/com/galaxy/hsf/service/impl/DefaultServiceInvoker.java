/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.impl;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.router.ServiceRouter;
import com.galaxy.hsf.rpc.RPCProtocolProvider;
import com.galaxy.hsf.rpc.ServiceURL;
import com.galaxy.hsf.rpc.protocol.RPCProtocol4Client;
import com.galaxy.hsf.util.ExceptionUtil;

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
	protected Object invoke0(HSFRequest request, List<String> addresses) throws HSFException {
		int i = 0;
		List<Throwable> exceptions = new ArrayList<Throwable>(retryTimes);
		for(; i < retryTimes && i < addresses.size(); i++) {
			try {
				ServiceURL serviceURL = new ServiceURL(addresses.get(i));
				RPCProtocol4Client rpcProtocol = rpcProtocolProvider.newRPCProtocol4Client(serviceURL);
				request.setServiceURL(serviceURL.toString());
				HSFResponse response = rpcProtocol.invoke(request);
				return response.getAppResponse();
			} catch (MalformedURLException e) {
				logger.error(String.format("Wrong address:%s, then try others", addresses.get(i)));
				exceptions.add(e);
			} catch (Throwable t) {
				t.printStackTrace();
				logger.error(String.format("Invoke address:%s, then try others", addresses.get(i)));
				exceptions.add(t);
			}
		}
		throw new HSFException(String.format("Invoke service:%s, protocol:%s, failed, retry times:%d, addresses:%s\n", request.getServiceName(), request.getProtocol(), i, StringUtils.join(addresses.iterator(), ",")), ExceptionUtil.mergeException(exceptions));
	}
}
