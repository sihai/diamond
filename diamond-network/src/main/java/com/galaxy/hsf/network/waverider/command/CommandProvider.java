package com.galaxy.hsf.network.waverider.command;

import java.util.List;

/**
 * Command提供者接口，业务代码提供实现该接口的Provider，Master循环取得Command分发到Slave
 * 
 * @author raoqiang
 *
 */
public interface CommandProvider {
	
	/**
	 * 产生Command
	 * @return
	 */
	Command produce();
	
	/**
	 * 批量产生Commnad
	 * 
	 * @param count
	 * @return
	 */
	List<Command> produce(long count);
	
	/**
	 * 
	 * @return
	 */
	String getName();
}
