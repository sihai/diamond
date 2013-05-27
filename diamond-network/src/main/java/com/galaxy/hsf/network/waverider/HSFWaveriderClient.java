/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.AbstractNetworkClient;
import com.galaxy.hsf.network.Callback;
import com.galaxy.hsf.network.Request;
import com.galaxy.hsf.network.Response;
import com.galaxy.hsf.network.exception.NetworkException;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.CommandFactory;
import com.galaxy.hsf.network.waverider.command.CommandHandler;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;
import com.galaxy.hsf.network.waverider.factory.WaveriderFactory;

/**
 * 
 * @author sihai
 *
 */
public class HSFWaveriderClient extends AbstractNetworkClient implements HSFWaverider {

	private static final Log logger = LogFactory.getLog(HSFWaveriderClient.class);
	
	/**
	 * 
	 */
	private WaveriderConfig config;
	
	/**
	 * 
	 */
	private SlaveNode slaveNode;
	
	/**
	 * 
	 */
	private AtomicLong idGenerator;
	
	/**
	 * 
	 */
	private ConcurrentHashMap<String, PendingRequest> pendingRequestMap;
	
	/**
	 * 
	 * @param config
	 */
	public HSFWaveriderClient(WaveriderConfig config) {
		this.config = config;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		pendingRequestMap = new ConcurrentHashMap<String, PendingRequest>();
		slaveNode = WaveriderFactory.newInstance(config).buildSlave();
		slaveNode.addCommandHandler(COMMAND_HSF_RESPONSE, new CommandHandler() {

			@Override
			public Command handle(Command command) {
				Response response = Response.unmarshall(command.getPayLoad());
				PendingRequest pr = pendingRequestMap.remove(response.getRequestId());
				if(null == pr) {
					logger.error(String.format("Server send response but there is no request for this response: % request id:%d", response.getRequestId()));
					return null;
				}
				pr.response = response;
				pr.request.notifyAll();
				return null;
			}
			
		});
		slaveNode.init();
		slaveNode.start();
	}

	@Override
	public void destroy() {
		super.destroy();
		slaveNode.stop();
		pendingRequestMap.clear();
	}

	@Override
	public Response syncrequest(Request request) {
		request.setRequestId(alloceId());
		pendingRequestMap.put(request.getRequestId(), new PendingRequest(request, null));
		slaveNode.execute(CommandFactory.createCommand(COMMAND_HSF_INVOKE, request.marshall()));
		synchronized(request) {
			try {
				// FIXME
				request.wait();
				return pendingRequestMap.get(request.getRequestId()).response;
			} catch (InterruptedException e) {
				logger.error(e);
				Thread.currentThread().interrupt();
				throw new NetworkException("Interrupted", e);
			}
		}
		
	}

	@Override
	public void asyncrequest(Request request, Callback callback) {
		throw new UnsupportedOperationException("At now we not supported");
	}
	
	private String alloceId() {
		return String.format("hsf-request-%s-%d", "HostName", idGenerator.incrementAndGet());
	}
}
