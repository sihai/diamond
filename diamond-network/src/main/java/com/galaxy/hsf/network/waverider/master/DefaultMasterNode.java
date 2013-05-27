/**
 * waverider
 * 
 */

package com.galaxy.hsf.network.waverider.master;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.waverider.MasterNode;
import com.galaxy.hsf.network.waverider.State;
import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.CommandDispatcher;
import com.galaxy.hsf.network.waverider.command.CommandHandler;
import com.galaxy.hsf.network.waverider.command.MasterHeartbeatCommandHandler;
import com.galaxy.hsf.network.waverider.command.SampleCommandDispatcher;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;
import com.galaxy.hsf.network.waverider.network.DefaultNetWorkServer;
import com.galaxy.hsf.network.waverider.network.NetWorkServer;
import com.galaxy.hsf.network.waverider.session.DefaultSessionManager;
import com.galaxy.hsf.network.waverider.session.Session;
import com.galaxy.hsf.network.waverider.session.SessionListener;
import com.galaxy.hsf.network.waverider.session.SessionManager;

/**
 * <p>
 * 分布式Master节点
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class DefaultMasterNode implements MasterNode {
	
	private static final Log logger = LogFactory.getLog(DefaultMasterNode.class);
	
	private WaveriderConfig config;							// 运行配置
	private SessionManager sessionManager;					// Slave session 管理
	private volatile SlaveListenter listener;				// 上层注册的监听Slave监听器
	private NetWorkServer netWorkServer;					// 底层网络服务
	private CommandDispatcher commandDispatcher;			// 命令分发器
	private AtomicLong stateIDGenrator = new AtomicLong(0);	// 状态计数器

	public DefaultMasterNode(WaveriderConfig config) {
		this.config = config;
		this.commandDispatcher = new SampleCommandDispatcher();
	}

	@Override
	public boolean init() {
		commandDispatcher.addCommandHandler(0L, new MasterHeartbeatCommandHandler(this));
		netWorkServer = new DefaultNetWorkServer(config.getPort());
		sessionManager = new DefaultSessionManager(config);
		sessionManager.setCommandDispatcher(commandDispatcher);
		sessionManager.registerSessionListener(new SessionListenerSupport());
		netWorkServer.setSessionManager(sessionManager);
		return netWorkServer.init() && sessionManager.init();
	}

	@Override
	public boolean start() {
		return netWorkServer.start() && sessionManager.start();
	}

	@Override
	public boolean stop() {
		return sessionManager.stop() && netWorkServer.stop();
	}

	@Override
	public boolean restart() {
		return sessionManager.restart() && netWorkServer.restart();
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
		MasterState masterState = new MasterState();
		masterState.setId(stateIDGenrator.addAndGet(1));
		masterState.setIp(netWorkServer.getIp());
		masterState.setPort(netWorkServer.getPort());
		masterState.setSessionStateList(sessionManager.generateSessionState());
		return masterState;
	}
	
	@Override
	public void acceptStatistics(State state) {
		//logger.info(new StringBuilder("Master Accept Slave state : ").append(((SlaveState)state).toString()).toString());
	}
	
	@Override
	public void registerSlaveListener(SlaveListenter listener) {
		this.listener = listener;
	}
	
	/**
	 * 
	 */
	private class SessionListenerSupport implements SessionListener {

		@Override
		public void sessionAllocated(Session session) {
			//
			logger.warn(String.format("New slave:%s joined !!!", session.getSlaveWorker()));
			// 通知上层
			if(listener != null) {
				listener.joined(session.getSlaveWorker());
			}
		}

		@Override
		public void sessionReleased(Session session) {
			//
			logger.warn(String.format("Slave:%s left !!!", session.getSlaveWorker()));
			// 通知上层
			if(listener != null) {
				listener.left(session.getSlaveWorker());
			}
		}
	}
}
