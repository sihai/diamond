/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.HSFResponse;
import com.galaxy.hsf.network.AbstractHSFNetworkServer;
import com.galaxy.hsf.network.Request;
import com.galaxy.hsf.network.Response;
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
	private ConcurrentHashMap<Integer, PendingRequest> pendingRequestMap;
	
	/**
	 * 
	 * @param config
	 */
	public HSFWaveriderServer(WaveriderConfig config, HSFRequestHandler handler) {
		this.config = config;
		register(handler);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		callback = new ResponseCallback() {
			@Override
			public void completed(HSFRequest request, HSFResponse response) {
				Response r = new Response();
				PendingRequest pr = pendingRequestMap.remove(System.identityHashCode(request));
				r.setRequestId(pr.request.getRequestId());
				r.setResponse(response);
				pr.session.execute(CommandFactory.createCommand(COMMAND_HSF_RESPONSE, r.marshall()));
			}
		};
		pendingRequestMap = new ConcurrentHashMap<Integer, PendingRequest>();
		config.setPort(getServerPort());
		masterNode = WaveriderFactory.newInstance(config).buildMaster();
		masterNode.addCommandHandler(COMMAND_HSF_INVOKE, new CommandHandler() {

			@Override
			public Command handle(Command command) {
				Request request = Request.unmarshall(command.getPayLoad());
				pendingRequestMap.put(System.identityHashCode(request.getRequest()), new PendingRequest(request, command.getSession()));
				// async
				handler.handle(request.getRequest(), callback);
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
		Request request;
		Session session;
		
		/**
		 * 
		 * @param request
		 * @param session
		 */
		public PendingRequest(Request request, Session session) {
			this.request = request;
			this.session = session;
		}
	}
}
