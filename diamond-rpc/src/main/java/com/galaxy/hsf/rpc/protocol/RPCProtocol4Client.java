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
public interface RPCProtocol4Client extends RPCProtocol {

	/**
	 * 
	 * @param request
	 * @return
	 * @throws HSFException
	 */
	HSFResponse invoke(HSFRequest request) throws HSFException;
}
