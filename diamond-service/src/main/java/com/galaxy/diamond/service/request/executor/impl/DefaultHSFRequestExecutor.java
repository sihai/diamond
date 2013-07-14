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
package com.galaxy.diamond.service.request.executor.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.common.Request;
import com.galaxy.diamond.common.Response;
import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.service.HSFService;
import com.galaxy.diamond.service.request.executor.HSFRequestExecutor;

/**
 * 
 * @author sihai
 *
 */
public class DefaultHSFRequestExecutor extends AbstractLifeCycle implements HSFRequestExecutor {

	private Log logger = LogFactory.getLog(DefaultHSFRequestExecutor.class);
	
	@Override
	public Response execute(HSFService service, Request request) {
		Response response = new Response();
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
