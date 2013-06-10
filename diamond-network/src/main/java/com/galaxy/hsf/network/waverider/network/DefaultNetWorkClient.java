/**
 * waverider
 *  
 */

package com.galaxy.hsf.network.waverider.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.CommandFactory;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;

/**
 * <p>
 * 基于TCP/IP的网络客户端默认实现，运行在Slave节点
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class DefaultNetWorkClient implements NetWorkClient, Runnable {

	private static final Log logger = LogFactory.getLog(DefaultNetWorkClient.class);

	private String hostName;										// 服务端地址
	private int    port;											// 服务端端口
	private SocketChannel socketChannel;							// Socket通道
	private Selector selector;										// 多路复用选择器
	private Thread thread;											// 网络事件监听, 处理线程
	private AtomicInteger state; 									// 网络连接状态
	private AtomicBoolean isWeakuped; 								// 选择是否被唤醒了
	private SocketChannelOPSChangeRequest opsChangeRequest;			// 转换感兴趣的网络事件请求
	
	// Buffer
	private BlockingQueue<ByteBuffer> inputBuffer;					// 网络数据输入缓冲
	private BlockingQueue<Command> outputBuffer;					// 网络数据输出缓冲
	
	private byte[] waitMoreDataLock;								// 
	
	//=============================================================
	//				构造函数
	//=============================================================
	public DefaultNetWorkClient() {
		this(null, WaveriderConfig.WAVERIDER_DEFAULT_PORT);
	}
	
	public DefaultNetWorkClient(String hostName) {
		this(hostName, WaveriderConfig.WAVERIDER_DEFAULT_PORT);
	}
	
	public DefaultNetWorkClient(String hostName, int port){
		this.hostName = hostName;
		this.port = port;
		state = new AtomicInteger(NetworkStateEnum.NETWORK_STATE_DISCONNECT.value);
		isWeakuped = new AtomicBoolean(false);
		inputBuffer = new LinkedBlockingQueue<ByteBuffer>();
		outputBuffer = new LinkedBlockingQueue<Command>();
		waitMoreDataLock = new byte[0];
	}

	//=============================================================
	//				LifeCycle
	//=============================================================
	@Override
	public boolean init() {
		state.set(NetworkStateEnum.NETWORK_STATE_DISCONNECT.value);
		isWeakuped.set(false);
		thread = new Thread(this, NET_WORK_CLIENT_THREAD_NAME);
		thread.setDaemon(true);
		return true;
	}

	@Override
	public boolean start() {
		// 参数校验
		if(hostName == null || port == 0) {
			throw new IllegalArgumentException("hostName and port must be supply");
		}
		// 连接到服务器
		while(!connect() && !Thread.currentThread().isInterrupted()) {
			try {
				logger.warn("Can not connect to server , so sleep 60s, then try again");
				Thread.sleep(60 * 1000);
			} catch(InterruptedException e) {
				// 线程被中断了
				logger.error("OOPS：Exception：", e);
				state.set(NetworkStateEnum.NETWORK_STATE_DISCONNECT.value);
				Thread.currentThread().interrupt();
			}
		}
		
		// 启动启动线程
		thread.start();
		
		return state.get() == NetworkStateEnum.NETWORK_STATE_CONNECTED.value;
	}

	@Override
	public boolean stop() {
		// 停止线程
		thread.interrupt();
		// 端口
		disconnect();
		inputBuffer.clear();
		outputBuffer.clear();
		thread = null;
		socketChannel = null;
		selector = null;
		isWeakuped.set(false);
		
		return true;
	}
	
	@Override
	public boolean restart() {
		return stop() && init() && start();
	}
	
	@Override
	public boolean notifyRead(SocketChannel channel) {
		logger.debug("notifyRead");
		if(state.get() == NetworkStateEnum.NETWORK_STATE_CONNECTED.value) {
			// 添加读请求
			opsChangeRequest.addOps(SelectionKey.OP_READ);
			// 尝试唤醒selector
			weakup();
			return true;
		}
		
		return false;
	}

	@Override
	public boolean notifyWrite(SocketChannel channel) {
		logger.debug("notifyWrite");
		if(state.get() == NetworkStateEnum.NETWORK_STATE_CONNECTED.value) {
			// 添加写请求
			opsChangeRequest.addOps(SelectionKey.OP_WRITE);
			// 尝试唤醒selector
			weakup();
			return true;
		}
		
		return false;
	}
	
	@Override
	public void waitMoreData(long timeout) throws InterruptedException {
		synchronized(waitMoreDataLock) {
			waitMoreDataLock.wait(timeout);
		}
	}

	@Override
	public void run() {
		
		while(!Thread.currentThread().isInterrupted()) {
			try {
				dispatch();
			} catch(IOException e){
				logger.error("OOPS：Exception：", e);
				// 重连
				logger.warn(String.format("Slave try to reconnect to %s", hostName));
				reconnect();
			} catch(InterruptedException e){
				// 线程被中断
				logger.error("OOPS：Exception：", e);
				Thread.currentThread().interrupt();
			}
		}
		logger.info(new StringBuilder("Waverider-NetWork-Client stoped ").toString());
	}
	
	// 连接到服务器
	private boolean connect() {
		try {
			state.set(NetworkStateEnum.NETWORK_STATE_CONNECTING.value);
			socketChannel = SocketChannel.open();
			// 阻塞的连接上去
			socketChannel.connect(new InetSocketAddress(hostName, port));
			// 转换到非阻塞
			socketChannel.configureBlocking(false);
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_READ);
			opsChangeRequest = new SocketChannelOPSChangeRequest(socketChannel, 0);
			// 连接上了
			state.set(NetworkStateEnum.NETWORK_STATE_CONNECTED.value);
			logger.warn(String.format("Slave connected to %s", hostName));
			return true;
		} catch (IOException e) {
			logger.error("OOPS：Exception：", e);
		}
		state.set(NetworkStateEnum.NETWORK_STATE_DISCONNECT.value);
		return false;
	}
	
	// 断开连接
	private void disconnect()  {
		state.set(NetworkStateEnum.NETWORK_STATE_DISCONNECT.value);
		try {
			selector.close();
		} catch (IOException e) {
			logger.error("OOPS：Exception：", e);
		}
		try {
			socketChannel.close();
		} catch (IOException e) {
			logger.error("OOPS：Exception：", e);
		}
		socketChannel = null;
		selector = null;
		opsChangeRequest = null;
		isWeakuped.set(false);
	}
	
	// 重新连接
	private void reconnect() {
		// 尝试断开连接
		disconnect();
		while(!connect() && !Thread.currentThread().isInterrupted()) {
			try {
				logger.warn("Can not connect to server , so sleep 60s, then try again");
				Thread.sleep(60 * 1000);
			} catch(InterruptedException e) {
				// 线程被中断了
				logger.error("OOPS：Exception：", e);
				Thread.currentThread().interrupt();
			}
		}
	}
	/**
	 * 网络事件监听, 分发, 处理, 在需要的时候切换感兴趣的事件
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void dispatch() throws IOException, InterruptedException {
		logger.debug("try dispatch");
		SelectionKey key = null;
		key = opsChangeRequest.getChannel().keyFor(selector);
		if(key != null) {
			if((opsChangeRequest.getOps() & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
				// 写优先
				key.interestOps(SelectionKey.OP_WRITE);
				opsChangeRequest.clearOps(SelectionKey.OP_WRITE);
			} else if((opsChangeRequest.getOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
				key.interestOps(SelectionKey.OP_READ);
				opsChangeRequest.clearOps(SelectionKey.OP_READ);
			}
		}
		
		// 进入等待之前, 设置唤醒标记为未唤醒 
		isWeakuped.set(false);
		if(selector.select(WaveriderConfig.WAVERIDER_DEFAULT_NETWORK_TIME_OUT) <= 0) {
			return;
		}
		
		// 处理网络事件
		Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
		while (iterator.hasNext()) {
			key = (SelectionKey) iterator.next();
			iterator.remove();
			if (!key.isValid()) {
				continue;
			} else if (key.isReadable()) {
				onRead(key);
			} else if (key.isWritable()) {
				onWrite(key);
			} 
		}
	}
	
	/**
	 * 连接上服务器处理方法
	 * @param key
	 * @throws IOException
	 */
	private void onConnected(SelectionKey key) throws IOException {
		if(socketChannel.isConnectionPending()) {
	        socketChannel.finishConnect();
	        key.interestOps(SelectionKey.OP_READ);
	        // 设置连接状态标志
	        state.set(NetworkStateEnum.NETWORK_STATE_CONNECTED.value);
	        logger.info(new StringBuilder("Connected to server:").append(hostName).append(":").append(port));
		}
	}
	
	/**
	 * 网络可读, 读数据到inputBuffer
	 * @param key
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void onRead(SelectionKey key) throws IOException, InterruptedException {
		logger.debug("onRead");
		// 分配临时缓冲
		ByteBuffer readByteBuffer = ByteBuffer.allocate(NetWorkConstants.DEFAULT_NETWORK_BUFFER_SIZE);
		int ret = 0;
		do {
			ret = socketChannel.read(readByteBuffer);
		} while(ret > 0);
		
		// 出错, 遇到流结束
		if(ret == -1) {
			throw new IOException("EOF");
		}
		readByteBuffer.flip();
		if(readByteBuffer.hasRemaining()) {
			inputBuffer.put(readByteBuffer);
			synchronized(waitMoreDataLock) {
				waitMoreDataLock.notifyAll();
			}
		}
		
		//logger.info("Slave read " + readByteBuffer.remaining() + " bytes");
	}
	
	/**
	 * 网络可写, 写完所有的outputBuffer
	 * 一口气写完
	 * @param key
	 * @throws IOException
	 */
	private void onWrite(SelectionKey key) throws IOException {
		logger.debug("onWrite");
		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		Command command = null;
		Packet packet = null;
		ByteBuffer data = null;
		//int size = 0;
		while((command = outputBuffer.poll()) != null) {
			//size = 0;
			// 将命令转换成功数据报
			packet = Packet.newDataPacket(command);
			// 数据报转换成ByteBuffer
			data = packet.marshall();
			//size = data.remaining();
			while(data.hasRemaining()){
				socketChannel.write(data);
			}
			socketChannel.socket().getOutputStream().flush();
			//logger.info("Slave write one packet, " + size + " bytes");
		}
		//key.interestOps(SelectionKey.OP_READ);
	}
	
	/**
	 * 尝试唤醒selector, 来转换感兴趣的事件
	 */
	private void weakup() {
		logger.debug("try to weakup");
		if(isWeakuped.compareAndSet(false, true)) {
			this.selector.wakeup();
			logger.debug("weakuped");
		}
	}
	
	// parse network data, convert to command
	private Command _parse_() throws IOException, InterruptedException {
		return CommandFactory.unmarshallCommandFromPacket(Packet.parse(inputBuffer, this, socketChannel));
	}
	
	@Override
	public void send(Command command) throws InterruptedException {
		outputBuffer.put(command);
		if(state.get() == NetworkStateEnum.NETWORK_STATE_CONNECTED.value) {
			notifyWrite(socketChannel);
		} else {
			//logger.warn("Not connected to server");
			// 重启
			//restart();
			// 放缓点节奏
			Thread.sleep(1000);
		}
	}
	
	@Override
	public Command receive() throws IOException, InterruptedException {
		if(state.get() == NetworkStateEnum.NETWORK_STATE_CONNECTED.value) {
			return _parse_();
		} else {
			///logger.warn("Not connected to server");
			// 重启
			//restart();
			// 放缓点节奏
			Thread.sleep(1000);
		}
		return null;
	}
	
	
	// 网络连接状态
	private enum NetworkStateEnum {
		NETWORK_STATE_CONNECTING(0, "Connecting"),
		NETWORK_STATE_CONNECTED(1, "Connected"),
		NETWORK_STATE_DISCONNECT(2, "Disconnect");
		private int 	value;
		private String 	desc;
		
		private NetworkStateEnum(int value, String desc) {
			this.value = value;
			this.desc = desc;
		}
	}
}
