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
 */
package com.galaxy.diamond.network.waverider;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.diamond.network.AbstractNetworkServer;
import com.galaxy.diamond.network.NetworkRequest;
import com.galaxy.diamond.network.NetworkResponse;
import com.galaxy.diamond.network.waverider.command.Command;
import com.galaxy.diamond.network.waverider.command.CommandFactory;
import com.galaxy.diamond.network.waverider.command.CommandHandler;
import com.galaxy.diamond.network.waverider.config.WaveriderConfig;
import com.galaxy.diamond.network.waverider.factory.WaveriderFactory;
import com.galaxy.diamond.network.waverider.session.Session;

/**
 * 
 * @author sihai
 *
 */
public class WaveriderServer extends AbstractNetworkServer implements Waverider  {

	private static final Long COMMAND_HSF_INVOKE = 99L;
	private static final Long COMMAND_HSF_RESPONSE = 100L;
	
	private static final Log logger = LogFactory.getLog(WaveriderServer.class);
	
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
	public WaveriderServer(WaveriderConfig config, NetworkRequestHandler handler) {
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
				pr.end = System.currentTimeMillis();
				System.out.println(String.format("Request execute in server consume:%d ms", pr.end - pr.start));
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
				PendingRequest pr = new PendingRequest(request, command.getSession());
				pendingRequestMap.put(request.getId(), pr);
				pr.start = System.currentTimeMillis();
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
		
		long start;
		long end;
		
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
