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
package com.openteach.diamond.network.waverider.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.command.Command;
import com.openteach.diamond.network.waverider.command.CommandFactory;
import com.openteach.diamond.network.waverider.slave.SlaveState;

/**
 * <p>
 * 网络数据报
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class Packet {
	
	private static final Log logger = LogFactory.getLog(Packet.class);
	
	private static final long PACKET_TYPE_DATA = 0;
	
	private static final AtomicLong sequenceGenerator = new AtomicLong(0);
	
	//=====================================================================
	//			Header
	//=====================================================================
	private String magic = NetWorkConstants.WAVERIDER_MAGIC;	// 报文魔数
	private Long sequence;										// 报文Sequence
	private Long type;											// 报文类型
	private Integer length;										// 整个报文的长度
	
	//=====================================================================
	//			Body(负载)
	//=====================================================================
	private ByteBuffer payLoad;									// 报文负载
	
	/**
	 * 获取整个报文长度
	 * @return
	 */
	public int getSize() {
		int size = 0;
		size += getHeaderSize();
		size += payLoad.remaining();
		
		return size;
	}
	
	/**
	 * 获取报文类型
	 * @return
	 */
	public Long getType() {
		return type;
	}

	/**
	 * 设置报文类型
	 * @param type
	 */
	public void setType(Long type) {
		this.type = type;
	}

	/**
	 * 获取报文长度
	 * @return
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * 设置报文长度
	 * @param length
	 */
	public void setLength(Integer length) {
		this.length = length;
	}
	
	/**
	 * 获取报文魔数
	 * @return
	 */
	public String getMagic() {
		return magic;
	}

	/**
	 * 设置报文魔数
	 * @param magic
	 */
	public void setMagic(String magic) {
		this.magic = magic;
	}
	
	/**
	 * 
	 * @return
	 */
	public Long getSequence() {
		return sequence;
	}

	/**
	 * 
	 * @param sequence
	 */
	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}
	
	/**
	 * 获取报文负载
	 * @return
	 */
	public ByteBuffer getPayLoad() {
		return payLoad;
	}

	/**
	 * 设置报文负载
	 * @param payLoad
	 */
	public void setPayLoad(ByteBuffer payLoad) {
		this.payLoad = payLoad;
	}
	
	//=====================================================================
	//			工具方法
	//=====================================================================
	
	/**
	 * 报文头部长度
	 * @return
	 */
	public static int getHeaderSize() {
		int size = 0;
		size += NetWorkConstants.WAVERIDER_MAGIC.getBytes().length;
		size += Long.SIZE / Byte.SIZE;
		size += Long.SIZE / Byte.SIZE;
		size += Integer.SIZE / Byte.SIZE;
		
		return size;
	}
	
	/**
	 * 获取报文长度字段的位置
	 * @return
	 */
	public static int getLengthPosition() {
		int size = 0;
		size += NetWorkConstants.WAVERIDER_MAGIC.getBytes().length;
		size += Long.SIZE / Byte.SIZE;
		size += Long.SIZE / Byte.SIZE;
		
		return size;
	}
	
	/**
	 * 将报文写到ByteBuffer中
	 * @return
	 */
	public ByteBuffer marshall(){
		int size = getSize();
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.put(magic.getBytes());
		buffer.putLong(sequence);
		buffer.putLong(type);
		buffer.putInt(size);
		buffer.put(payLoad);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * 将ByteBuffer转换成报文
	 * @param buffer
	 * @return
	 */
	public static Packet unmarshall(ByteBuffer buffer) {
		if(buffer.remaining() < getHeaderSize()) {
			throw new RuntimeException("Wrong packet.");
		}
		
		Packet packet = new Packet();
		byte[] str = new byte[NetWorkConstants.WAVERIDER_MAGIC.getBytes().length];
		buffer.get(str);
		packet.setMagic(new String(str));
		
		if(!NetWorkConstants.WAVERIDER_MAGIC.equals(packet.getMagic())){
			throw new RuntimeException("Wrong packet.");
		}
		
		packet.setSequence(buffer.getLong());
		packet.setType(buffer.getLong());
		packet.setLength(buffer.getInt());
		packet.setPayLoad(buffer.slice());
		return packet;
	}
	
	public static Packet newDataPacket(Command command) {
		return newDataPacket(command, sequenceGenerator.getAndAdd(2));
	}
	/**
	 * 通过命令创建报文
	 * @param command
	 * @return
	 */
	public static Packet newDataPacket(Command command, Long sequence) {
		Packet packet = new Packet();
		packet.setSequence(sequence);
		packet.setType(PACKET_TYPE_DATA);
		ByteBuffer commandByteBuffer = command.marshall();
		packet.setLength(getHeaderSize() + commandByteBuffer.remaining());
		packet.setPayLoad(commandByteBuffer);
		//logger.info("New one packat");
		//packet.dump();
		return packet;
	}
	
	/**
	 * 从网络数据缓冲中解析Packet, 可能会睡眠
	 * @param inputBuffer
	 * @return
	 * @throws IOException, InterruptedException
	 */
	public static Packet parse(BlockingQueue<ByteBuffer> inputBuffer, NetWorkEndPoint endPoint, SocketChannel channel) throws IOException, InterruptedException {
		// Buffer for packet header
		byte[] tmpBuf = new byte[NetWorkConstants.DEFAULT_NETWORK_BUFFER_SIZE];
		ByteBuffer header = ByteBuffer.allocate(Packet.getHeaderSize());
		ByteBuffer currentBuffer = null;
		int rest = 0;
		boolean isRemove = false;
		
		// 先取得报文头部
		while(true) {
			while((currentBuffer = inputBuffer.peek()) == null) {
				if(!endPoint.notifyRead(channel)) {
					throw new IOException("Socket closed by other thread");
				}
				// 等待数据
				//endPoint.waitMoreData(5);
				// FIXME 这里貌似引入了至少2ms的延迟
				//Thread.sleep(1);
				Thread.yield();
			}
			isRemove = false;
			rest = header.capacity() - header.position();
			if(currentBuffer.remaining() >= rest) {
				if(currentBuffer.remaining() == rest) {
					isRemove = true;
				}
				currentBuffer.get(tmpBuf, 0, rest);
				header.put(tmpBuf, 0, rest);
				if(isRemove) {
					inputBuffer.remove();
				}
				break;
			} else {
				header.put(currentBuffer);
				inputBuffer.remove();
			}
		}
		
		header.flip();
		
		// 校验头先, 下面是要拿到头里的长度域分配内存的
		
		// 取整个报文
		Integer size = header.getInt(Packet.getLengthPosition());
		// For test
		/*if(size < 0 || size > 100000) {
			logger.info("Error");
		}*/
		//logger.debug(new StringBuilder("Try to allocate ").append(size).append(" bytes memory"));
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.put(header);
		header.clear();
		
		// 取得报文负载
		while(true) {
			while((currentBuffer = inputBuffer.peek()) == null) {
				endPoint.notifyRead(channel);
				Thread.sleep(1000);
			}
			isRemove = false;
			rest = buffer.capacity() - buffer.position();
			if(currentBuffer.remaining() >= rest) {
				if(currentBuffer.remaining() == rest) {
					isRemove = true;
				}
				currentBuffer.get(tmpBuf, 0, rest);
				buffer.put(tmpBuf, 0, rest);
				if(isRemove) {
					inputBuffer.remove();
				}
				break;
			} else {
				buffer.put(currentBuffer);
				inputBuffer.remove();
			}
		}
		//buffer.position(0);
		buffer.flip();
		Packet packet = Packet.unmarshall(buffer);
		//logger.info("Parse one packet from network");
		//packet.dump();
		return packet;
	}
	
	// For debug
	public void dump() {
		logger.info("==========================================================================dump packet==========================================================================");
		logger.info(new StringBuilder("== magic:").append(magic));
		logger.info(new StringBuilder("== sequence:").append(sequence));
		logger.info(new StringBuilder("== type:").append(type));
		logger.info(new StringBuilder("== length:").append(length));
		logger.info(new StringBuilder("== payLoad Size:").append(payLoad.remaining()));
		logger.info(new StringBuilder("== binary:"));
		byte[] buffer = this.copy().marshall().array();
		StringBuilder sb = new StringBuilder("== ");
		for(int i = 0; i < buffer.length; i++) {
			sb.append(buffer[i]);
			if(i % 80 == 0 && i > 0) {
				logger.info(sb);
				sb.delete(0, sb.length());
			}
		}
		logger.info("========================================================================dump packet end========================================================================");
	}
	
	public static void dump(ByteBuffer buffer) {
		logger.info("==========================================================================dump data==========================================================================");
		byte[] bytes = buffer.array();
		StringBuilder sb = new StringBuilder("");
		for(int i = 0; i < bytes.length; i++) {
			sb.append(bytes[i]);
			if(i % 80 == 0 && i > 0) {
				logger.info(sb);
				sb.delete(0, sb.length());
			}
		}
		logger.info("==========================================================================dump data end======================================================================");
	}
	
	private Packet copy() {
		Packet packet = new Packet();
		packet.magic = magic;
		packet.sequence = sequence;
		packet.type = type;
		packet.length = length;
		packet.payLoad = payLoad.duplicate();
		
		return packet;
	}
	
	public static void main(String[] args) {
		/*System.out.println("[DEBUG] Packet Header size = " + getHeaderSize());
		
		SlaveState slaveState = new SlaveState();
		slaveState.setId(1L);
		slaveState.setIsMasterCandidate(false);
		Command command = CommandFactory.createHeartbeatCommand(slaveState.toByteBuffer());
		Packet packet = Packet.newDataPacket(command);
		ByteBuffer buffer = packet.marshall();
		Packet p = Packet.unmarshall(buffer);
		Command cmd = Command.unmarshall(p.getPayLoad());
		SlaveState ss = SlaveState.fromByteBuffer(cmd.getPayLoad());
		System.out.println(cmd.toString());
	
	
	// Test 2
		MasterState masterState = new MasterState();
		masterState.setId(1L);
		masterState.setIp("127.0.0.1");
		masterState.setPort(8206);
		List<SessionState> sessionStateList = new LinkedList<SessionState>();
		masterState.setSessionStateList(sessionStateList);
		SessionState sessionState = null;
		for(int i = 0; i < 10; i++) {
			sessionState = new SessionState();
			sessionState.setIp("127.0.0.1");
			sessionState.setPriority(1L);
			sessionState.setIsMasterCandidate(false);
			sessionStateList.add(sessionState);
		}
		Command command2 = CommandFactory.createHeartbeatCommand(masterState.toByteBuffer());
		Packet packet2 = Packet.newDataPacket(command2);
		ByteBuffer buffer2 = packet2.marshall();
		Packet p2 = Packet.unmarshall(buffer2);
		Command cmd2 = Command.unmarshall(p2.getPayLoad());
		MasterState ms = MasterState.fromByteBuffer(cmd2.getPayLoad());
		System.out.println(cmd.toString());
		*/
		
		System.out.println("[DEBUG] Packet Header size = " + getHeaderSize());
		
		BlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<ByteBuffer>();
		Random rand = new Random();
		int count = 0;
		int size = 0;
		for(int i = 0; i < 100000; i++) {
			SlaveState state = new SlaveState();
			state.setId(Long.valueOf(i));
			state.setIsMasterCandidate(true);
			Packet packet = Packet.newDataPacket(CommandFactory.createCommand(Command.AVAILABLE_COMMAND_START, state.toByteBuffer()));
			ByteBuffer buffer = packet.marshall();
			rand.setSeed(System.currentTimeMillis());
			count = rand.nextInt(100) + 1;
			size = buffer.remaining() / count;
			for(int j = 0; j < count; j++) {
				ByteBuffer buf = null;
				if(j == count - 1) {
					buf = ByteBuffer.allocate(buffer.remaining() - j * size);
					buf.put(buffer.array(), j * size, buffer.remaining() - j * size);
				}else{
					buf = ByteBuffer.allocate(size);
					buf.put(buffer.array(), j * size, size);
				}
				buf.flip();
				queue.add(buf);
			}
		}
		
		for(int i = 0; i < 100000; i++) {
			//Packet packet = Packet.parse(queue);
			//Command commad = Command.unmarshall(packet.getPayLoad());
			//SlaveState state = SlaveState.fromByteBuffer(commad.getPayLoad());
			//System.out.println(state.toString());
		}
	}
}
