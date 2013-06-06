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
		logger.debug(String.format("%s execute request:%s", this.getClass().getName(), request.toString()));
		return null;
	}

}
