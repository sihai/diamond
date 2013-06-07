/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.request.executor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.service.HSFService;
import com.galaxy.hsf.service.request.executor.HSFRequestExecutor;

/**
 * 
 * @author sihai
 *
 */
public class DefaultHSFRequestExecutor extends AbstractLifeCycle implements HSFRequestExecutor {

	private Log logger = LogFactory.getLog(DefaultHSFRequestExecutor.class);
	
	@Override
	public HSFResponse execute(HSFService service, HSFRequest request) {
		HSFResponse response = new HSFResponse();
		try {
			logger.debug(String.format("%s execute request:%s", this.getClass().getName(), request.toString()));
			Object object = service.invokeLocal(request.getServiceName(), request.getMethod(), request.getParameterTypes(), request.getArgs());
			// FIXME
			response.setAppResponse(object);
			response.succeed();
		} catch (Throwable t) {
			t.printStackTrace();
			response.setErrorMsg(String.format("OOPS: %s", t.getMessage()));
			response.failed();
			response.setException(t);
		}
		return response;
	}

}
