/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.AbstractHSFNetworkServer;
import com.galaxy.hsf.network.NetworkRequest;
import com.galaxy.hsf.network.NetworkResponse;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.CommandFactory;
import com.galaxy.hsf.network.waverider.command.CommandHandler;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;
import com.galaxy.hsf.network.waverider.factory.WaveriderFactory;
import com.galaxy.hsf.network.waverider.session.Session;

/**
 * 
 * @author sihai
 *
 */
public class HSFWaveriderServer extends AbstractHSFNetworkServer implements HSFWaverider  {

	private static final Long COMMAND_HSF_INVOKE = 99L;
	private static final Long COMMAND_HSF_RESPONSE = 100L;
	
	private static final Log logger = LogFactory.getLog(HSFWaveriderServer.class);
	
	/**
	 * 
	 */
	private WaveriderConfig config;
	
	/**
	 * 
	 */
	private MasterNode masterNode;
	
	/**
	 * 
	 */
	private ResponseCallback callback;
	
	/**
	 * 
	 */
	private ConcurrentHashMap<String, PendingRequest> pendingRequestMap;
	
	/**
	 * 
	 * @param config
	 */
	public HSFWaveriderServer(WaveriderConfig config, NetworkRequestHandler handler) {
		this.config = config;
		register(handler);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		callback = new ResponseCallback() {
			@Override
			public void completed(NetworkRequest request, Object response) {
				NetworkResponse r = new NetworkResponse(request.getId(), response);
				PendingRequest pr = pendingRequestMap.remove(request.getId());
				pr.session.execute(CommandFactory.createCommand(COMMAND_HSF_RESPONSE, r.marshall()));
			}
		};
		pendingRequestMap = new ConcurrentHashMap<String, PendingRequest>();
		config.setPort(getServerPort());
		masterNode = WaveriderFactory.newInstance(config).buildMaster();
		masterNode.addCommandHandler(COMMAND_HSF_INVOKE, new CommandHandler() {

			@Override
			public Command handle(Command command) {
				NetworkRequest request = NetworkRequest.unmarshall(command.getPayLoad());
				pendingRequestMap.put(request.getId(), new PendingRequest(request, command.getSession()));
				// async
				handler.handle(request, callback);
				return null;
			}
			
		});
		masterNode.init();
		masterNode.start();
	}

	@Override
	public void destroy() {
		super.destroy();
		masterNode.stop();
		pendingRequestMap.clear();
	}
	
	private class PendingRequest {
		NetworkRequest request;
		Session session;
		
		/**
		 * 
		 * @param request
		 * @param session
		 */
		public PendingRequest(NetworkRequest request, Session session) {
			this.request = request;
			this.session = session;
		}
	}
}
