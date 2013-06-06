/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.request.executor;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.lifecycle.LifeCycle;
import com.galaxy.hsf.service.HSFService;

/**
 * 
 * @author sihai
 *
 */
public interface HSFRequestExecutor extends LifeCycle {

	/**
	 * 
	 * @param service
	 * @param request
	 * @return
	 */
	HSFResponse execute(HSFService service, HSFRequest request);
}
