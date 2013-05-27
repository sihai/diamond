package com.galaxy.hsf.network.waverider.session;

import java.nio.channels.SocketChannel;

import com.galaxy.hsf.network.waverider.SlaveWorker;
import com.galaxy.hsf.network.waverider.command.CommandDispatcher;

/**
 * 
 * @author raoqiang
 *
 */
public abstract class SessionFactory {
	
	public static DefaultSession newSession(Long id, SocketChannel channel, int inBufferSize, int outBufferSize, SlaveWorker slaveWorker, CommandDispatcher commandDispatcher) {
		DefaultSession session = new DefaultSession(id, inBufferSize, outBufferSize);
		session.withChannel(channel).withSlaveWorker(slaveWorker).withCommandDispatcher(commandDispatcher);
		return session;
	}
}
