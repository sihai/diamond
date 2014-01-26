/**
 * Copyright 2013 openteach
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
package com.openteach.diamond.network.waverider.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.SlaveWorker;
import com.openteach.diamond.network.waverider.command.Command;
import com.openteach.diamond.network.waverider.command.CommandDispatcher;
import com.openteach.diamond.network.waverider.command.CommandFactory;
import com.openteach.diamond.network.waverider.command.exception.ExecuteCommandException;
import com.openteach.diamond.network.waverider.common.WaveriderThreadFactory;
import com.openteach.diamond.network.waverider.network.NetWorkConstants;
import com.openteach.diamond.network.waverider.network.NetWorkServer;
import com.openteach.diamond.network.waverider.network.Packet;

/**
 * <p>
 * Session, Master为每一个链接到Master的Slave维持一个Session, Session负责处理对应Slave的所有请求,
 * Session默认实现
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class DefaultSession implements Session {
	
	private static final Log logger = LogFactory.getLog(DefaultSession.class);
	private static final String SESSION_THREAD_NAME_PREFIX = "Waverider-Session";
	
	private Long id;								// Sessio ID
	private volatile SessionStateEnum state;		// Session 状态, 初始空闲状态
	private NetWorkServer netWorkServer;			// 
	private SocketChannel channel;					// 网络通道
	private Thread thread;							// 工作线程
	private SlaveWorker slaveWorker;				// Slave信息
	private int inBufferSize;						// 网络数据输入缓冲大小
	private int outBufferSize;		  				// 网络数据输出缓冲大小
	private BlockingQueue<ByteBuffer> inputBuffer;	// 网络数据输入缓冲
	private BlockingQueue<Command> outputBuffer; 	// 网络数据输出缓冲
	private byte[] waitMoreDataLock;				// 
	private CommandDispatcher commandDispatcher;	// 命令分发器
	
	//
	private ReentrantLock runLock;		//
	private Condition run;				//
	private boolean isRun;				//
	
	private WaveriderThreadFactory threadFactory;
	
	public DefaultSession(Long id, int inBufferSize, int outBufferSize) {
		this.id = id;
		this.state = SessionStateEnum.WAVERIDER_SESSION_STATE_FREE;
		this.inBufferSize = inBufferSize;
		this.outBufferSize = outBufferSize;
		this.runLock = new ReentrantLock();
		this.run = runLock.newCondition();
		this.isRun = false;
		threadFactory = new WaveriderThreadFactory(SESSION_THREAD_NAME_PREFIX + "-" + String.valueOf(id), null, true);
	}
	
	public Long getId() {
		return id;
	}
	
	//==============================================================
	//				LifeCycle
	//==============================================================
	@Override
	public boolean init() {
		state = SessionStateEnum.WAVERIDER_SESSION_STATE_FREE;
		inputBuffer = new LinkedBlockingQueue<ByteBuffer>(inBufferSize);
		outputBuffer = new LinkedBlockingQueue<Command>(outBufferSize);
		waitMoreDataLock = new byte[0];
		thread = threadFactory.newThread(new SessionTask());
		thread.start();
		return true;
	}
	
	@Override
	public boolean start() {
		if(channel == null || inputBuffer == null || outputBuffer == null || commandDispatcher == null) {
			throw new IllegalArgumentException("Seesion not set corrected");
		}
		control(true);
		state = SessionStateEnum.WAVERIDER_SESSION_STATE_ALIVE;
		return true;
	}
	
	@Override
	public boolean stop() {
		thread.interrupt();
		inputBuffer.clear();
		outputBuffer.clear();
		channel = null;
		state = SessionStateEnum.WAVERIDER_SESSION_STATE_FREE;
		return true;
	}
	
	@Override
	public void free() {
		control(false);
		inputBuffer.clear();
		outputBuffer.clear();
		closeChannel();
		channel = null;
		state = SessionStateEnum.WAVERIDER_SESSION_STATE_FREE;
	}
	
	@Override
	public boolean restart() {
		throw new UnsupportedOperationException("Session not supported restart.");
	}
	
	@Override
	public void execute(Command command) throws ExecuteCommandException {
		try {
			outputBuffer.put(command);
			netWorkServer.notifyWrite(channel);
		} catch (InterruptedException e) {
			logger.error(e);
			Thread.currentThread().interrupt();
			throw new ExecuteCommandException("Interrupted", e);
		}
	}

	//==============================================================
	//				Session state
	//==============================================================
	@Override
	public SlaveWorker getSlaveWorker() {
		return this.slaveWorker;
	}
	
	@Override
	public SessionStateEnum getState() {
		return this.state;
	}
	
	@Override
	public SocketChannel getChannel() {
		return channel;
	}

	//==============================================================
	//				Session state transit
	//==============================================================
	@Override
	public void transit() {
		SessionStateEnum oldState = state;
		if (oldState == SessionStateEnum.WAVERIDER_SESSION_STATE_ALIVE) {
			state = SessionStateEnum.WAVERIDER_SESSION_STATE_WAITING_0;
		} else if (oldState == SessionStateEnum.WAVERIDER_SESSION_STATE_WAITING_1){
			state = SessionStateEnum.WAVERIDER_SESSION_STATE_WAITING_2;
		} else if (oldState == SessionStateEnum.WAVERIDER_SESSION_STATE_WAITING_2) {
			state = SessionStateEnum.WAVERIDER_SESSION_STATE_DEAD;
		}

		/*if (oldState != state) {
			logger.warn(new StringBuilder("Session id = ").append(id).append(" transit state from : ")
					.append(oldState.desc()).append(" to : ").append(state.desc()));
		}*/
	}
	
	@Override
	public void alive() {
		state = SessionStateEnum.WAVERIDER_SESSION_STATE_ALIVE;
		logger.warn(String.format("Session id = %d is alived.", id));
	}
	
	@Override
	public boolean isDead() {
		return state == SessionStateEnum.WAVERIDER_SESSION_STATE_DEAD;
	}
	
	//==============================================================
	//				网络回调方法
	//==============================================================
	@Override
	public void onRead() throws IOException, InterruptedException {
		logger.debug("onRead");
		ByteBuffer buffer = ByteBuffer.allocate(NetWorkConstants.DEFAULT_NETWORK_BUFFER_SIZE);
		int ret = 0;
		do {
			ret = channel.read(buffer);
		} while (ret > 0);
		
		if(ret == -1) {
			throw new IOException("EOF");
		}
		buffer.flip();
		if(buffer.hasRemaining()) {
			inputBuffer.put(buffer);
			synchronized(waitMoreDataLock) {
				waitMoreDataLock.notifyAll();
			}
		}
		//logger.info("Session is onRead, read " + buffer.remaining() + " bytes");
	}
	
	@Override
	public void onWrite() throws IOException {
		logger.debug("onWrite");
		int count = 0;
		Command command = null;
		Packet packet = null;
		ByteBuffer data = null;
		while((command = outputBuffer.poll()) != null) {
			packet = Packet.newDataPacket(command);
			data = packet.marshall();
			count += data.remaining();
			while (data.hasRemaining()) {
				channel.write(data);
			}
			// flush
			channel.socket().getOutputStream().flush();
		}
		
		//logger.info("Session is onWrite, write " + count + " bytes");
	}
	
	@Override
	public void onException(Exception e) {
		// TODO
	}

	@Override
	public boolean notifyWrite(SocketChannel channel) {
		logger.debug("notifyWrite");
		return this.netWorkServer.notifyWrite(this.channel);
	}

	@Override
	public boolean notifyRead(SocketChannel channel) {
		logger.debug("notifyRead");
		return this.netWorkServer.notifyRead(this.channel);
	}

	@Override
	public void waitMoreData(long timeout) throws InterruptedException {
		synchronized(waitMoreDataLock) {
			waitMoreDataLock.wait(timeout);
		}
	}

	//==============================================================
	//				DSL
	//==============================================================
	public DefaultSession withNetWorkServer(NetWorkServer netWorkServer) {
		this.netWorkServer = netWorkServer;
		return this;
	}
	
	public DefaultSession withChannel(SocketChannel channel) {
		this.channel = channel;
		return this;
	}
	
	public DefaultSession withSlaveWorker(SlaveWorker slaveWorker) {
		this.slaveWorker = slaveWorker;
		return this;
	}
	
	public DefaultSession withCommandDispatcher(CommandDispatcher commandDispatcher) {
		this.commandDispatcher = commandDispatcher;
		return this;
	}
	
	//==============================================================
	//				Private internal methods
	//==============================================================
	
	/**
	 * parse network data, convert to command
	 */
	private Command _parse_() throws IOException, InterruptedException {
		Command command = CommandFactory.unmarshallCommandFromPacket(Packet.parse(inputBuffer, this, channel));
		command.setSession(this);
		return command;
	}
		
	/**
	 * Session执行命令
	 * @throws IOException, InterruptedException
	 */
	private void _process_() throws IOException, InterruptedException {
		//logger.info("Session try to process one command");
		// 解析出命令
		Command command = _parse_();
		if (command != null) {
			// 分发命令到命令执行Handler
			Command resultCommand = commandDispatcher.dispatch(command);
			command.getPayLoad().clear();
			// 将命令执行结果(封装在命令中), 发往Slave
			if (resultCommand != null) {
				// 先到Session输出缓存
				outputBuffer.put(resultCommand);
				// 通知网络服务器, 我有数据要写
				netWorkServer.notifyWrite(channel);
				//logger.info("Session execute one command");
			}
		}
	}
	
	/**
	 * 控制Session
	 * @param isRun
	 */
	private void control(boolean isRun) {
		try {
			runLock.lock();
			this.isRun = isRun;
			this.run.signalAll();
		} finally {
			runLock.unlock();
		}
	}
	
	/**
	 * 关闭通道
	 */
	private void closeChannel() {
		try {
			channel.close();
		} catch(IOException e) {
			logger.error("OOPS：Exception：", e);
		}
	}
	
	//==============================================================
	//				Private thread task
	//				Session命令执行线程
	//==============================================================
	private class SessionTask implements Runnable {

		@Override
		public void run() {
			while(!Thread.currentThread().isInterrupted()) {
				try {
					try {
						runLock.lock();
						while(!isRun) {
							// idle
							logger.info(new StringBuilder(Thread.currentThread().getName()).append(" idle"));
							run.await();
							logger.info(new StringBuilder(Thread.currentThread().getName()).append(" started"));
						}
					} finally {
						runLock.unlock();
					}
					
					_process_();
				} catch(InterruptedException e) {
					logger.error("OOPS：Exception：", e);
					e.printStackTrace();
					Thread.currentThread().interrupt();
				} catch(IOException e) {
					logger.error("OOPS：Exception：", e);
					e.printStackTrace();
				} catch(Exception e) {
					logger.error("OOPS：Exception：", e);
					e.printStackTrace();
				} catch (Throwable t) {
					logger.error("OOPS, Exception:", t);
				} finally {
					
				}
			}
			logger.info(new StringBuilder(Thread.currentThread().getName()).append(" stoped"));
		}
	}
	
	//==============================================================
	//				Test
	//==============================================================
	public static void main(String[] args) {

		BlockingQueue<ByteBuffer> inputBuffer = new LinkedBlockingQueue<ByteBuffer>();
		/*for (int i = 0; i < 10; i++)
		{*/
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.put(makePacket().marshall());
		byteBuffer.put(makePacket().marshall());
		byteBuffer.flip();
		byte[] b = new byte[8];
		ByteBuffer halfBuf0 = ByteBuffer.allocate(8);
		byteBuffer.get(b);
		halfBuf0.put(b);
		halfBuf0.flip();
		inputBuffer.add(halfBuf0);
		inputBuffer.add(byteBuffer);
		/*}*/

		int size = 0;
		int oldSize = size;
		long length = Packet.getHeaderSize();
		ByteBuffer buffer = ByteBuffer.allocate(NetWorkConstants.DEFAULT_NETWORK_BUFFER_SIZE);
		ByteBuffer currentBuffer = null;

		while (size < length) {
			currentBuffer = inputBuffer.peek();
			oldSize = size;
			int position = currentBuffer.position();
			size += currentBuffer.remaining();
			buffer.put(currentBuffer);
			if (size >= Packet.getHeaderSize()) {
				length = buffer.getLong(Packet.getLengthPosition());
			}

			if (size <= length) {
				inputBuffer.remove();
			} else {
				currentBuffer.position(position);
				buffer.position(buffer.position() - currentBuffer.remaining());
				byte[] buf = new byte[(int) (length - oldSize)];
				currentBuffer.get(buf);
				buffer.put(buf);
			}
		}
		
		// buffer.position(0);
		buffer.flip();
		Packet packet = Packet.unmarshall(buffer);

		Command command = CommandFactory.createCommand(packet.getType(), packet.getPayLoad());

		String str = new String(command.getPayLoad().array());

		System.out.println(str);

	}

	public static Packet makePacket() {
		return Packet.newDataPacket(makeGreetCommand());
	}

	public static Command makeGreetCommand() {
		String HELLO = "Hello Master";
		ByteBuffer buffer = ByteBuffer.allocate(HELLO.getBytes().length);
		buffer.put(HELLO.getBytes());
		buffer.flip();
		Command command = new Command(1L, buffer);
		return command;
	}
}
