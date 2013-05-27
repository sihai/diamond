package com.galaxy.hsf.network.waverider.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author raoqiang
 *
 */
public class SlaveGreetCommandHandler implements CommandHandler {
	
	private static final Log logger = LogFactory.getLog(SlaveGreetCommandHandler.class);
	
	@Override
	public Command handle(Command command) {
		logger.info(new StringBuilder("Command type = ").append(command.getType()).toString());
		String hello = new String(command.getPayLoad().array());
		logger.info("Slave received greet from master:" + hello);
		return null;
	}

}
