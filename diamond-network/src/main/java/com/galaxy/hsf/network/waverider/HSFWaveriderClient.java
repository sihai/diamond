/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.AbstractNetworkClient;
import com.galaxy.hsf.network.Callback;
import com.galaxy.hsf.network.NetworkRequest;
import com.galaxy.hsf.network.NetworkResponse;
import com.galaxy.hsf.network.exception.NetworkException;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.CommandFactory;
import com.galaxy.hsf.network.waverider.command.CommandHandler;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;
import com.galaxy.hsf.network.waverider.factory.WaveriderFactory;
import com.galaxy.hsf.util.NetworkUtil;

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
		idGenerator = new AtomicLong(0);
		pendingRequestMap = new ConcurrentHashMap<String, PendingRequest>();
		slaveNode = WaveriderFactory.newInstance(config).buildSlave();
		slaveNode.addCommandHandler(COMMAND_HSF_RESPONSE, new CommandHandler() {

			@Override
			public Command handle(Command command) {
				NetworkResponse response = NetworkResponse.unmarshall(command.getPayLoad());
				PendingRequest pr = pendingRequestMap.get(response.getId());
				if(null == pr) {
					logger.error(String.format("Server send response but there is no request for this response: % request id:%d", response.getId()));
					return null;
				}
				pr.response = response;
				synchronized(pr.request) {
					pr.request.notifyAll();
				}
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
		idGenerator.set(0);
		pendingRequestMap.clear();
	}

	@Override
	public Object syncrequest(Object request) throws NetworkException {
		try {
			String id = alloceId();
			NetworkRequest r = new NetworkRequest(id, request);
			pendingRequestMap.put(id, new PendingRequest(r, null));
			slaveNode.execute(CommandFactory.createCommand(COMMAND_HSF_INVOKE, r.marshall()));
			synchronized(r) {
				try {
					// FIXME
					r.wait();
					return pendingRequestMap.remove(id).response.getPayload();
				} catch (InterruptedException e) {
					logger.error(e);
					Thread.currentThread().interrupt();
					throw new NetworkException("Interrupted", e);
				}
			}
		} catch (UnknownHostException e) {
			throw new NetworkException("OOPS, can not get local ip", e);
		}
		
	}

	@Override
	public void asyncrequest(Object request, Callback callback) {
		throw new UnsupportedOperationException("At now we not supported");
	}
	
	/**
	 * 
	 * @return
	 * @throws UnknownHostException
	 */
	private String alloceId() throws UnknownHostException {
		// FIXME
		return String.format("hsf-request-%s-%d", NetworkUtil.getLocalIp(), idGenerator.incrementAndGet());
	}
}
