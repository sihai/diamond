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
package com.openteach.diamond.network.waverider.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.openteach.diamond.network.waverider.session.Session;
import com.openteach.diamond.network.waverider.slave.SlaveState;

/**
 * <p>
 * 命令
 * </p>
 * 
 * @author <a href="mailto:sihai@taobao.com">sihai</a>
 *
 */
public class Command {
	
	private static Log logger = LogFactory.getLog(Command.class);
	
	public static Long HEART_BEAT_COMMAND = 0L;			// 心跳Command Type值
	public static Long AVAILABLE_COMMAND_START = 10L;	// 上层可用Command Type起始值
	
	//=====================================================================
	//			Header
	//=====================================================================
	private Long type;			// 命令类型, 上层自定义, 0L-10L保留系统使用
	private Integer length;		// 整个报文的长度
	
	//=====================================================================
	//			Body(负载)
	//=====================================================================
	private ByteBuffer payLoad;	// 命令负载, 上层自定义

	/**
	 * 命令依附的Session, Master端有效
	 */
	private Session session;
	
	public Command() {
		this(null, null, null);
	}
	
	/**
	 * 
	 * @param type
	 * @param payLoad
	 */
	public Command(Long type, ByteBuffer payLoad){
		this(null, type, payLoad);
	}
	
	/**
	 * 
	 * @param type
	 * @param payLoad
	 */
	public Command(Session session, Long type, ByteBuffer payLoad){
		this.session = session;
		this.type = type;
		this.payLoad = payLoad;
	}
	
	/**
	 * 获取命令类型
	 * @return
	 */
	public Long getType(){
		return type;
	}
	
	/**
	 * 设置命令类型
	 * @param type
	 */
	public void setType(Long type){
		this.type = type;
	}
	
	/**
	 * 获取命令长度
	 * @return
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * 设置命令长度
	 * @param length
	 */
	public void setLength(Integer length) {
		this.length = length;
	}
	
	/**
	 * 
	 * @param payLoad
	 */
	public void setPayLoad(ByteBuffer payLoad){
		this.payLoad = payLoad;
	}
	
	/**
	 * 获取命令的负载
	 * @return
	 */
	public ByteBuffer getPayLoad(){
		return payLoad;
	}
	
	/**
	 * 获取命令依附的Session, Master端有效
	 * @return
	 */
	public Session getSession() {
		return session;
	}
	
	/**
	 * 设置命令依附的Session, Master端有效
	 * @return
	 */
	public void setSession(Session session) {
		this.session = session;
	}
	
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
	
	//=====================================================================
	//			工具方法
	//=====================================================================
	
	/**
	 * 命令头部长度
	 * @return
	 */
	public static int getHeaderSize() {
		int size = 0;
		size += Long.SIZE / Byte.SIZE;
		size += Integer.SIZE / Byte.SIZE;
		return size;
	}
	
	/**
	 * 将命令写到ByteBuffer中
	 * @return
	 */
	public ByteBuffer marshall(){
		int length = getSize();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.putLong(type);
		buffer.putInt(length);
		buffer.put(payLoad);
		buffer.flip();
		payLoad.clear();
		return buffer;
	}
	
	/**
	 * 将ByteBuffer转换成命令
	 * @param buffer
	 * @return
	 */
	public static Command unmarshall(ByteBuffer buffer) {
		if(buffer.remaining() < getHeaderSize()) {
			throw new RuntimeException("Wrong command.");
		}
		Command command = new Command();
		command.setType(buffer.getLong());
		command.setLength(buffer.getInt());
		// 在新缓冲区(payLoad)上调用array()方法还是会返回整个数组。
		command.setPayLoad(buffer.slice());
		return command;
	}
	
	public static void main(String[] args) {
		
		ByteArrayOutputStream bout = null;
		ObjectOutputStream objOutputStream = null;
		
		try {
			bout = new ByteArrayOutputStream();
			objOutputStream = new ObjectOutputStream(bout);
			SlaveState slaveState = new SlaveState();
			slaveState.setId(1L);
			slaveState.setIsMasterCandidate(false);
			objOutputStream.writeObject(slaveState);
			objOutputStream.flush();
			Command command = CommandFactory.createHeartbeatCommand(ByteBuffer.wrap(bout.toByteArray()));
			
			ByteBuffer buffer = command.marshall();
			Command cmd = Command.unmarshall(buffer);
			SlaveState ss = SlaveState.fromByteBuffer(cmd.getPayLoad());
			System.out.println(cmd.toString());
		}catch(IOException e) {
			throw new RuntimeException(e);
		}finally{
			try {
				if(objOutputStream != null) {
					objOutputStream.close();
				}
				if(bout != null) {
					bout.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
