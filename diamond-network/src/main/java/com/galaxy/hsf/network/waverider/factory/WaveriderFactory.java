/**
 * waverider
 *  
 */
package com.galaxy.hsf.network.waverider.factory;

import com.galaxy.hsf.network.waverider.MasterNode;
import com.galaxy.hsf.network.waverider.SlaveNode;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;
import com.galaxy.hsf.network.waverider.master.DefaultMasterNode;
import com.galaxy.hsf.network.waverider.slave.DefaultSlaveNode;

/**
 * 
 * @author sihai
 *
 */
public class WaveriderFactory {

	/**
	 * 
	 */
	private WaveriderConfig config;
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	public static WaveriderFactory newInstance(WaveriderConfig config) {
		WaveriderFactory factory = new WaveriderFactory();
		factory.config = config;
		return factory;
	}
	
	/**
	 * 
	 * @return
	 */
	public MasterNode buildMaster() {
		if(WaveriderConfig.Mode.MASTER != config.getMode()) {
			throw new IllegalArgumentException("");
		}
		return new DefaultMasterNode(config);
	}
	
	/**
	 * 
	 * @return
	 */
	public SlaveNode buildSlave() {
		if(WaveriderConfig.Mode.SLAVE != config.getMode()) {
			throw new IllegalArgumentException("");
		}
		return new DefaultSlaveNode(config);
	}
}
