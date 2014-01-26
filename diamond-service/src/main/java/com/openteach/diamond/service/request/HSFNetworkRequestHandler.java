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
package com.galaxy.diamond.service.request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.common.Request;
import com.galaxy.diamond.common.Response;
import com.galaxy.diamond.common.lifecycle.AbstractLifeCycle;
import com.galaxy.diamond.common.resource.ResourceConfig;
import com.galaxy.diamond.common.thread.CommonThreadFactory;
import com.galaxy.diamond.network.NetworkRequest;
import com.galaxy.diamond.network.HSFNetworkServer.NetworkRequestHandler;
import com.galaxy.diamond.network.HSFNetworkServer.ResponseCallback;
import com.galaxy.diamond.service.DiamondServiceFactory;
import com.galaxy.diamond.service.request.executor.HSFRequestExecutor;

/**
 * 
 * @author sihai
 *
 */
public class HSFNetworkRequestHandler extends AbstractLifeCycle implements NetworkRequestHandler {

	/**
	 * 
	 */
	private Log logger = LogFactory.getLog(HSFNetworkRequestHandler.class);
	
	/**
	 * 
	 */
	private ResourceConfig resourceConfig;
	
	/**
	 * 
	 */
	private ThreadPoolExecutor threadpool;
	
	/**
	 * 
	 */
	private BlockingQueue<Runnable> queue;
	
	/**
	 * 
	 */
	private HSFRequestExecutor executor;
	
	/**
	 * 
	 * @param executor
	 * @param resourceConfig
	 */
	public HSFNetworkRequestHandler(HSFRequestExecutor executor, ResourceConfig resourceConfig) {
		this.executor = executor;
		this.resourceConfig = resourceConfig;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		queue = new ArrayBlockingQueue<Runnable>(resourceConfig.getQueueSize());
		threadpool = new ThreadPoolExecutor(resourceConfig.getMinThreadCount(), resourceConfig.getMaxThreadCount(), resourceConfig.getKeepAliveTime(), 
				TimeUnit.SECONDS, queue, new CommonThreadFactory("HSF-Request-Handler-Thread", null, true));
	}

	@Override
	public void destroy() {
		threadpool.shutdown();
		queue.clear();
		threadpool = null;
		queue = null;
		super.destroy();
	}
	
	@Override
	public void handle(NetworkRequest request, ResponseCallback callback) {
		submit(new RequestTask(request, callback));
	}

	/**
	 * 
	 * @param runnable
	 */
	private void submit(Runnable runnable) {
		for(;;) {
			try {
				threadpool.execute(runnable);
				break;
			} catch (RejectedExecutionException e) {
				logger.warn("HSF-Request-Handler-Thread-Pool full");
			}
		}
	}
	
	/**
	 * 
	 * @author sihai
	 *
	 */
	private class RequestTask implements Runnable {
		
		private NetworkRequest request;
		
		private ResponseCallback callback;
		
		/**
		 * 
		 * @param request
		 * @param callback
		 */
		public RequestTask(NetworkRequest request, ResponseCallback callback) {
			this.callback = callback;
			this.request = request;
		}

		@Override
		public void run() {
			// FIXME
			Response response = executor.execute(DiamondServiceFactory.getHSFService(), (Request)request.getPayload());
			callback.completed(request, response);
		}
	
	}
}
