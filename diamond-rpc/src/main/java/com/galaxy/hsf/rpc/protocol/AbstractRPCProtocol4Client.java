/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc.protocol;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.exception.HSFException;

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
	 * @throws HSFException
	 */
	protected void before(HSFRequest request) throws HSFException {
	}
	
	@Override
	public final HSFResponse invoke(HSFRequest request) throws HSFException {
		before(request);
		HSFResponse response = invoke0(request);
		after(request, response);
		return response;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws HSFException
	 */
	protected void after(HSFRequest request, HSFResponse response) throws HSFException {
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws HSFException
	 */
	protected abstract HSFResponse invoke0(HSFRequest request) throws HSFException;

}
