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
package com.openteach.diamond.network.waverider.command;

import java.nio.ByteBuffer;

import com.openteach.diamond.network.waverider.network.NetWorkConstants;
import com.openteach.diamond.network.waverider.network.Packet;
import com.openteach.diamond.network.waverider.session.Session;

/**
 * Command工厂，负责创建Command
 * 
 * 0-10号命令系统保留
 * 0号Command保留给HeartbeatCommand
 * 
 * @author raoqiang
 *
 */
public class CommandFactory {
	
	/**
	 * 从数据包中解析出Command
	 * @param packet
	 * @return
	 */
	public static Command unmarshallCommandFromPacket(Packet packet) {
		Command command = Command.unmarshall(packet.getPayLoad());
		return command;
	}
	
	/**
	 * 创建命令
	 * @param session
	 * @param type
	 * @param payLoad
	 * @return
	 */
	public static Command createCommand(Session session, Long type, ByteBuffer payLoad) {
		Command command = new Command(session, type, payLoad);
		return command;
	}
	
	/**
	 * 创建命令
	 * @param type
	 * @param payLoad
	 * @return
	 */
	public static Command createCommand(Long type, ByteBuffer payLoad) {
		return createCommand(null, type, payLoad);
	}
	
	/**
	 * Master创建心跳命令
	 * @param session
	 * @return
	 */
	public static Command createHeartbeatCommand(Session session) {
		byte[] bytes = NetWorkConstants.WAVERIDER_HEART_BEAT_MSG.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		return CommandFactory.createCommand(session, Command.HEART_BEAT_COMMAND, buffer);
	}
	
	/**
	 * Slave端创建心跳命令
	 * @param payLoad
	 * @return
	 */
	public static Command createHeartbeatCommand(ByteBuffer payLoad) {
		return CommandFactory.createCommand(Command.HEART_BEAT_COMMAND, payLoad);
	}
	
	/**
	 * 创建心跳命令
	 * @return
	 */
	public static Command createHeartbeatCommand() {
		byte[] bytes = NetWorkConstants.WAVERIDER_HEART_BEAT_MSG.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		return CommandFactory.createCommand(Command.HEART_BEAT_COMMAND, buffer);
	}
}
