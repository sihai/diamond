package com.galaxy.hsf.network.waverider.command;

import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.network.waverider.session.Session;

/**
 * 
 * @author raoqiang
 *
 */
public class MasterGreetCommandHandler implements CommandHandler {
	
	private static final Log logger = LogFactory.getLog(MasterGreetCommandHandler.class);
	
	@Override
	public Command handle(Command command) {
		logger.info(new StringBuilder("Command type = ").append(command.getType()).toString());
		String hello = new String(command.getPayLoad().array());
		logger.info(new StringBuilder("Master receive greet from slave : ").append(command.getSession().getSlaveWorker().getIp()).append(", say:").append(hello));
		command.getSession().alive();
		return makeGreetCommand(command.getSession());
	}
	
	private Command makeGreetCommand(Session session) {
		String hello = new StringBuilder("Hello slave:").append(session.getSlaveWorker().getIp()).toString();
		ByteBuffer buffer = ByteBuffer.allocate(hello.getBytes().length);
		buffer.put(hello.getBytes());
		buffer.flip();
		
		Command command = new Command(1L, buffer);
		
		return command;
	}

}
