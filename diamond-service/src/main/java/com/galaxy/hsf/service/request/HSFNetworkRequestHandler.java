/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.common.lifecycle.AbstractLifeCycle;
import com.galaxy.hsf.common.resource.ResourceConfig;
import com.galaxy.hsf.common.thread.CommonThreadFactory;
import com.galaxy.hsf.network.HSFNetworkServer.NetworkRequestHandler;
import com.galaxy.hsf.network.HSFNetworkServer.ResponseCallback;
import com.galaxy.hsf.network.NetworkRequest;
import com.galaxy.hsf.service.HSFServiceFactory;
import com.galaxy.hsf.service.request.executor.HSFRequestExecutor;

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
			HSFResponse response = executor.execute(HSFServiceFactory.getHSFService(), (HSFRequest)request.getPayload());
			callback.completed(request, response);
		}
	
	}
}
