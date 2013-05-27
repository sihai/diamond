package com.galaxy.hsf.network.waverider.command;

import java.util.Map;

/**
 * 
 * @author raoqiang
 *
 */
public interface CommandDispatcher {
	
	/**
	 * 
	 * @param command
	 * @param handler
	 */
	void addCommandHandler(Long command, CommandHandler handler);
	
	/**
	 * 
	 * @param commandRoutingTable
	 */
	void setCommandRoutingTable(Map<Long, CommandHandler> commandRoutingTable);
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	Command dispatch(Command command);
}
