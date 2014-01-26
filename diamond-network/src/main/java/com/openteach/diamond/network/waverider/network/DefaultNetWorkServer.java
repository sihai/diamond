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
package com.openteach.diamond.network.waverider.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.config.WaveriderConfig;
import com.openteach.diamond.network.waverider.session.Session;
import com.openteach.diamond.network.waverider.session.SessionManager;

/**
 * <p>
 * 	基于TCP/IP的默认Master端网络服务器实现, NIO
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class DefaultNetWorkServer implements NetWorkServer, Runnable {

	private static final Log logger = LogFactory.getLog(DefaultNetWorkServer.class);

	private String hostName; 							// 主机名
	private int port; 									// 监听端口
	private ServerSocketChannel serverSocketChannel; 	// 服务端socket通道
	private Selector selector; 							// 多路复用选择器
	private Thread netWorkServerThread; 				// 网络事件监听, 分发线程
	private SessionManager sessionManager;				// 会话管理器
	//private ExecutorService readerExecutor;				// 网络读线程池, 只有一个线程, 保证socket读线程安全
	//private ExecutorService writerExecutor;				// 网络写线程池, 只有一个线程, 保证socket读线程安全
	private AtomicBoolean isWeakuped; 					// selector唤醒标记
	private ConcurrentHashMap<SocketChannel, SocketChannelOPSChangeRequest> opsChangeRequstMap;	// 转换感兴趣的网络事件请求

	//=============================================================
	//				构造函数
	//=============================================================
	public DefaultNetWorkServer() {
		this(null, WaveriderConfig.WAVERIDER_DEFAULT_PORT);
	}
	
	public DefaultNetWorkServer(int port) {
		this(null, port);
	}

	public DefaultNetWorkServer(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
		isWeakuped = new AtomicBoolean(false);
	}

	//=============================================================
	//				LifeCycle
	//=============================================================
	@Override
	public boolean init() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(
					hostName == null ? new InetSocketAddress(port) : new InetSocketAddress(hostName, port));
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			// 后台读写线程
			// 这里只能有一个线程, socket读写非线程安全
			//readerExecutor = Executors.newFixedThreadPool(1, new WaveriderThreadFactory(NET_WORK_READER, null, true));
			//writerExecutor = Executors.newFixedThreadPool(1, new WaveriderThreadFactory(NET_WORK_WRITER, null, true));
			
			// 网络监听线程
			netWorkServerThread = new Thread(this, NET_WORK_SERVER_THREAD_NAME);
			netWorkServerThread.setDaemon(true);
		} catch (IOException e) {
			logger.error("Init DefaultNetworkServer failed: ", e);
			throw new RuntimeException(e);
		}
		
		return true;
	}

	@Override
	public boolean start() {
		opsChangeRequstMap = new ConcurrentHashMap<SocketChannel, SocketChannelOPSChangeRequest>();
		// 启动网络监听线程
		netWorkServerThread.start();
		return true;
	}

	@Override
	public boolean stop() {
		// 停止后台读写线程
		//readerExecutor.shutdown();
		//writerExecutor.shutdown();
		// 停止网络监听线程
		netWorkServerThread.interrupt();
		try {
			selector.close();
			serverSocketChannel.close();
			isWeakuped.set(false);
			opsChangeRequstMap.clear();
			opsChangeRequstMap = null;
		} catch (IOException e) {
			logger.error("OOPS：Exception：", e);
		}
		return true;
	}
	
	@Override
	public boolean restart() {
		return stop() && init() && start();
	}

	@Override
	public String getIp() {
		return serverSocketChannel.socket().getLocalSocketAddress().toString();
	}

	@Override
	public int getPort() {
		return port;
	}
	
	@Override
	public boolean notifyWrite(SocketChannel channel) {
		logger.debug("notifyWrite");
		if(channel == null) {
			return false;
		}
		
		SocketChannelOPSChangeRequest request = opsChangeRequstMap.get(channel);
		if(request == null) {
			// Socket被关闭了
			return false;
		}
		
		// 添加写请求
		request.addOps(SelectionKey.OP_WRITE);
		// 尝试唤醒selector
		weakup();
		
		return true;
	}
	
	@Override
	public boolean notifyRead(SocketChannel channel) {
		logger.debug("notifyRead");
		if(channel == null) {
			return false;
		}
		SocketChannelOPSChangeRequest request = opsChangeRequstMap.get(channel);
		if(request == null) {
			// Socket被关闭了
			return false;
		}
		// 添加读请求
		request.addOps(SelectionKey.OP_READ);
		// 尝试唤醒selector
		weakup();
		
		return true;
	}
	
	@Override
	public void waitMoreData(long timeout) throws InterruptedException {
		throw new UnsupportedOperationException();
	}

	private void dispatch() throws IOException {
		logger.debug("try dispatch");
		SelectionKey key = null;
		for(SocketChannelOPSChangeRequest request : opsChangeRequstMap.values()) {
			key = request.getChannel().keyFor(selector);
			if(key != null) {
				// 写优先
				if((request.getOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
					key.interestOps(SelectionKey.OP_WRITE);
					request.clearOps(SelectionKey.OP_WRITE);
				} else if((request.getOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
					key.interestOps(SelectionKey.OP_READ);
					request.clearOps(SelectionKey.OP_READ);
				}
			}
		}
		
		isWeakuped.set(false);
		if(selector.select(WaveriderConfig.WAVERIDER_DEFAULT_NETWORK_TIME_OUT) <=0) {
			return;
		}
		
		Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
		while (iterator.hasNext()) {
			key = (SelectionKey) iterator.next();
			iterator.remove();
			try {
				if (!key.isValid()) {
					continue;
				} else if (key.isAcceptable()) {
					onAccept(key);
				} else if (key.isReadable()) {
					//readerExecutor.execute(new NetworkTask(key, NETWORK_OPERATION_READ));
					onRead(key);
				} else if (key.isWritable()) {
					//writerExecutor.execute(new NetworkTask(key, NETWORK_OPERATION_WRITE));
					onWrite(key);
				}
			} catch (IOException e) {
				// 客户端连接出问题
				opsChangeRequstMap.remove((SocketChannel)key.channel());
				Session session = (Session) key.attachment();
				if(session != null) {
					session.onException(e);
					// 释放Session
					sessionManager.freeSession(session);
				}
				key.cancel();
				key.channel().close();
				e.printStackTrace();
				logger.error("OOPS：Exception：", e);
			}
		}
	}

	@Override
	public void run() {
		logger.info(new StringBuilder("Waverider-NetWork-Server started listen on ")
				.append(hostName == null ? "" : hostName + ":").append(port).toString());
		while(!Thread.currentThread().isInterrupted()) {
			try {
				dispatch();
			} catch(IOException e) {
				logger.error("OOPS：Exception：", e);
				e.printStackTrace();
			}
		}
		logger.info(new StringBuilder("Waverider-NetWork-Server stoped ").toString());
	}

	/**
	 * 有客户端连接可以接收, 分配Session, 启动Session, 
	 * 这个Session专门负责处理这个客户端的所有请求
	 * @param key
	 * @throws IOException
	 */
	private void onAccept(SelectionKey key) throws IOException {
		SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
		opsChangeRequstMap.put(channel, new SocketChannelOPSChangeRequest(channel, 0));
		if (logger.isWarnEnabled())
			logger.warn("Accept client from : " + channel.socket().getRemoteSocketAddress());
		channel.configureBlocking(false);
		Session session = sessionManager.newSession(this, channel, false);
		channel.register(selector, SelectionKey.OP_READ, session);
		session.start();
	}

	/**
	 * 有通道可读, 调用Session的onRead, 把数据读到Session的私有输入缓冲
	 * @param key
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void onRead(SelectionKey key) throws IOException {
		Session session = (Session) key.attachment();
		try {
			session.onRead();
		} catch(InterruptedException e) {
			logger.error("OOPS Exception:", e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 有通道可写, 调用Session的onWrite, 把Session的私有输出缓冲中数据写到网络
	 * @param key
	 * @throws IOException
	 */
	private void onWrite(SelectionKey key) throws IOException {
		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		Session session = (Session) key.attachment();
		session.onWrite();
		//key.interestOps(SelectionKey.OP_READ);
	}
	
	/**
	 * 尝试唤醒selector, 切换感兴趣的网络事件
	 */
	private void weakup() {
		logger.debug("try to weakup");
		if(isWeakuped.compareAndSet(false, true)) {
			this.selector.wakeup();
			logger.debug("weakuped");
		}
	}
	
	@Override
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}
	
	/**
	 * 网络读写后台线程类
	 * @author sihai
	 *
	 */
	private class NetworkTask implements Runnable {
		
		private SelectionKey key;
		private int operation;
		
		public NetworkTask(SelectionKey key, int operation) {
			this.key = key;
			this.operation = operation;
		}
		
		@Override
		public void run() {
			try {
				switch(operation) {
					case NETWORK_OPERATION_ACCEPT: {
						onAccept(key);
						break;
					}
					case NETWORK_OPERATION_READ: {
						onRead(key);
						break;
					}
					case NETWORK_OPERATION_WRITE: {
						onWrite(key);
						break;
					}
				}
			} catch (IOException e) {
				logger.error("OOPS：Exception：", e);
				try {
					// 客户端连接出问题
					Session session = (Session) key.attachment();
					if(session != null) {
						session.onException(e);
						// 释放Session
						sessionManager.freeSession(session);
					}
					opsChangeRequstMap.remove((SocketChannel)key.channel());
					key.cancel();
					key.channel().close();
				} catch (IOException ex) {
					logger.error("OOPS：Exception：", ex);
					ex.printStackTrace();
				}
			}
		}
	}
}
