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
package com.openteach.diamond.network.waverider.factory;

import com.openteach.diamond.network.waverider.MasterNode;
import com.openteach.diamond.network.waverider.SlaveNode;
import com.openteach.diamond.network.waverider.config.WaveriderConfig;
import com.openteach.diamond.network.waverider.master.DefaultMasterNode;
import com.openteach.diamond.network.waverider.slave.DefaultSlaveNode;

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
