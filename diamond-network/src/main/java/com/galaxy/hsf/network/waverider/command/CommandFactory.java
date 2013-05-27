/**
 * waverider 
 */

package com.galaxy.hsf.network.waverider.command;

import java.nio.ByteBuffer;

import com.galaxy.hsf.network.waverider.network.NetWorkConstants;
import com.galaxy.hsf.network.waverider.network.Packet;
import com.galaxy.hsf.network.waverider.session.Session;

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
