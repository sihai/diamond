/**
 * waverider
 *  
 */
package com.galaxy.hsf.network.waverider.command.callback;

import com.galaxy.hsf.network.waverider.command.Command;
import com.galaxy.hsf.network.waverider.command.exception.ExecuteCommandException;

/**
 * 
 * @author sihai
 *
 */
public interface CommandCallback {

	/**
	 * 
	 * @param command
	 * @param resultCommand
	 */
	void succeed(Command command, Command resultCommand);
	
	/**
	 * 
	 * @param command
	 * @param e
	 */
	void failed(Command command, ExecuteCommandException e);
}
