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
package com.openteach.diamond.network.waverider;

import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.AbstractNetworkClient;
import com.openteach.diamond.network.Callback;
import com.openteach.diamond.network.NetworkRequest;
import com.openteach.diamond.network.NetworkResponse;
import com.openteach.diamond.network.exception.NetworkException;
import com.openteach.diamond.network.waverider.command.Command;
import com.openteach.diamond.network.waverider.command.CommandFactory;
import com.openteach.diamond.network.waverider.command.CommandHandler;
import com.openteach.diamond.network.waverider.config.WaveriderConfig;
import com.openteach.diamond.network.waverider.factory.WaveriderFactory;
import com.openteach.diamond.util.NetworkUtil;

/**
 * 
 * @author sihai
 *
 */
public class WaveriderClient extends AbstractNetworkClient implements Waverider {

	private static final Log logger = LogFactory.getLog(WaveriderClient.class);
	
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
	public WaveriderClient(WaveriderConfig config) {
		this.config = config;
	}
	
	@Override
	public void initialize() {
		super.initialize();
		idGenerator = new AtomicLong(0);
		pendingRequestMap = new ConcurrentHashMap<String, PendingRequest>();
		slaveNode = WaveriderFactory.newInstance(config).buildSlave();
		slaveNode.addCommandHandler(COMMAND_DIAMOND_RESPONSE, new CommandHandler() {

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
		long start = System.currentTimeMillis();
		try {
			String id = alloceId();
			NetworkRequest r = new NetworkRequest(id, request);
			pendingRequestMap.put(id, new PendingRequest(r, null));
			slaveNode.execute(CommandFactory.createCommand(COMMAND_DIAMOND_INVOKE, r.marshall()));
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
		} catch (SocketException e) {
			throw new NetworkException("OOPS, can not get local ip", e);
		} finally {
			System.out.println(String.format("syncrequest consume:%d ms", System.currentTimeMillis() - start));
		}
		
	}

	@Override
	public void asyncrequest(Object request, Callback callback) {
		throw new UnsupportedOperationException("At now we not supported");
	}
	
	/**
	 * 
	 * @return
	 * @throws SocketException
	 */
	private String alloceId() throws SocketException {
		// FIXME
		return String.format("hsf-request-%s-%d", NetworkUtil.getLocalIp(), idGenerator.incrementAndGet());
	}
}
