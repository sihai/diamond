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
package com.openteach.diamond.network.waverider.slave;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.exception.NetworkException;
import com.openteach.diamond.network.waverider.SlaveNode;
import com.openteach.diamond.network.waverider.State;
import com.openteach.diamond.network.waverider.command.Command;
import com.openteach.diamond.network.waverider.command.CommandDispatcher;
import com.openteach.diamond.network.waverider.command.CommandFactory;
import com.openteach.diamond.network.waverider.command.CommandHandler;
import com.openteach.diamond.network.waverider.command.SampleCommandDispatcher;
import com.openteach.diamond.network.waverider.command.SlaveHeartbeatCommandHandler;
import com.openteach.diamond.network.waverider.common.WaveriderThreadFactory;
import com.openteach.diamond.network.waverider.config.WaveriderConfig;
import com.openteach.diamond.network.waverider.master.MasterState;
import com.openteach.diamond.network.waverider.network.DefaultNetWorkClient;
import com.openteach.diamond.network.waverider.network.NetWorkClient;
import com.openteach.diamond.network.waverider.slave.failure.slave.DefaultMasterFailureHandler;
import com.openteach.diamond.network.waverider.slave.failure.slave.DefaultMasterFailureMonitor;
import com.openteach.diamond.network.waverider.slave.failure.slave.MasterFailureMonitor;

/**
 * <p>
 * 系统默认的网络命令执行Slave节点
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class DefaultSlaveNode implements SlaveNode {
	
	private static final Log logger = LogFactory.getLog(DefaultSlaveNode.class);
	
	// Config
	private WaveriderConfig config;											// 配置信息
	
	private volatile MasterDeadCallback callback;							// 上层注册的监听Slave离开回调

	// Command execute
	private CommandDispatcher commandDispatcher;							// 命令分发器
	private Thread commandDispatchThread;									// 命令分发线程
	
	// Failure monitor
	private MasterFailureMonitor masterFailureMonitor;
	
	// Heartbeat
	private AtomicLong stateIDGenrator = new AtomicLong(0);
	private ScheduledExecutorService heartbeatScheduler;
	
	// Network
	private NetWorkClient netWorkClient;
	
	public DefaultSlaveNode(WaveriderConfig config) {
		this.config = config;
		this.commandDispatcher = new SampleCommandDispatcher();
	}
	
	@Override
	public boolean init() {
		commandDispatcher.addCommandHandler(0L, new SlaveHeartbeatCommandHandler(this));
		netWorkClient = new DefaultNetWorkClient(config.getMasterAddress(), config.getPort());
		if(!netWorkClient.init()) {
			return false;
		}
		masterFailureMonitor = new DefaultMasterFailureMonitor(new DefaultMasterFailureHandler(this), MasterFailureMonitor.DEFAULT_FAILURE_MONITOR_INTERVAL, MasterFailureMonitor.DEFAULT_FAILURE_MONITOR_WAIT_MASTER_STATE_TIME_OUT);
		if(!masterFailureMonitor.init()) {
			return false;
		}
		commandDispatchThread = new Thread(new CommandDispatchTask(), SLAVE_COMMAND_DISPATCHE_THREAD_NAME);
		commandDispatchThread.setDaemon(true);
		// heart beat
		heartbeatScheduler = Executors.newScheduledThreadPool(1, new WaveriderThreadFactory(SLAVE_HEART_BEAT_THREAD_NAME_PREFIX, null, true));
		return true;
	}

	@Override
	public boolean start() {
		while (true) {
			try {
				netWorkClient.start();
				break;
			} catch (Exception e) {
				logger.error(e);
				if (e.getCause() instanceof IOException || e.getCause() instanceof ConnectException) {
					try {
						logger.error("Can not connect to master , sleep 60s, then try again");
						Thread.sleep(60 * 1000);
					} catch (InterruptedException ex) {
						logger.error("OOPS：Exception：", ex);
						Thread.currentThread().interrupt();
					}
					continue;
				}
			}
		}

		masterFailureMonitor.start();
		commandDispatchThread.start();
		heartbeatScheduler.scheduleAtFixedRate(new HeartbeatTask(), WaveriderConfig.WAVERIDER_DEFAULT_HEART_BEAT_INTERVAL, WaveriderConfig.WAVERIDER_DEFAULT_HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
		
		return true;
	}

	@Override
	public boolean stop() {
		netWorkClient.stop();
		masterFailureMonitor.stop();
		commandDispatchThread.interrupt();
		heartbeatScheduler.shutdown();
		return true;
	}

	@Override
	public boolean restart() {
		return true;
	}
	
	@Override
	public void addCommandHandler(Long command, CommandHandler handler) {
		if(command == null || command.equals(0L)) {
			throw new IllegalArgumentException("command must not be null or 0");
		}
		commandDispatcher.addCommandHandler(command, handler);
	}
	
	@Override
	public State gatherStatistics() {
		SlaveState slaveState = new SlaveState();
		slaveState.setId(stateIDGenrator.addAndGet(1));
		slaveState.setIsMasterCandidate(config.isMasterCandidate());
		
		return slaveState;
	}
	
	@Override
	public void acceptStatistics(State state) {
		//logger.info(new StringBuilder("Slave Accept Master state : ").append(((MasterState)state).toString()).toString());
		masterFailureMonitor.process((MasterState)state);
	}
	
	/**
	 * 分发命令
	 * @throws Exception
	 */
	private void _process_() throws IOException, InterruptedException {
		
		Command command = netWorkClient.receive();
		if(command != null) {
			Command resultCommand = commandDispatcher.dispatch(command);
			command.getPayLoad().clear();
			if(resultCommand != null) {
				netWorkClient.send(resultCommand);
			}
		}
	}
	
	private void _heartbeat_() throws Exception {
		SlaveState slaveState  = (SlaveState)gatherStatistics();
		netWorkClient.send(CommandFactory.createHeartbeatCommand(slaveState.toByteBuffer()));
		logger.debug("Slave send one heartbeat command to Master");
	}
	
	@Override
	public void execute(Command command) throws NetworkException {
		try {
			netWorkClient.send(command);
		} catch (InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
			throw new NetworkException("Interrupted", e);
		}
	}
	// 命令分发执行
	private class CommandDispatchTask implements Runnable{
		@Override
		public void run() {
			logger.info("Waverider-Slave-Command-Dispatch-Thread started");
			while(!Thread.currentThread().isInterrupted()){
				try{
					_process_();
				} catch(InterruptedException e) {
					e.printStackTrace();
					logger.error("OOPS：Exception：", e);
					Thread.currentThread().interrupt();
				} catch(IOException e){
					e.printStackTrace();
					logger.error("OOPS：Exception：", e);
				} catch(Exception e){
					e.printStackTrace();
					logger.error("OOPS：Exception：", e);
				} catch (Throwable t) {
					t.printStackTrace();
					logger.error("OOPS：Exception：", t);
				}
			}
			logger.info("Waverider-Slave-Command-Dispatch-Thread stoped");
		}
	}
	
	private class HeartbeatTask implements Runnable {
		public void run() {
			try {
				_heartbeat_();
			} catch(InterruptedException e) {
				logger.error("OOPS：Exception：", e);
				e.printStackTrace();
				Thread.currentThread().interrupt();
			} catch(Exception e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void registerMasterDeadCallback(MasterDeadCallback callback) {
		this.callback = callback;
	}

	public void setNetWorkClient(NetWorkClient netWorkClient) {
		this.netWorkClient = netWorkClient;
	}
	
	public void setMasterFailureMonitor(MasterFailureMonitor masterFailureMonitor) {
		this.masterFailureMonitor = masterFailureMonitor;
	}
}
