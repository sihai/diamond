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
package com.galaxy.diamond.address;

import com.galaxy.diamond.address.listener.Listener;
import com.galaxy.diamond.common.exception.DiamondException;
import com.galaxy.diamond.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface AddressingService extends LifeCycle {
	
	/**
	 * 寻址
	 * @param serviceName
	 * @return
	 * @throws DiamondException
	 */
	ServiceAddress addressing(String serviceName) throws DiamondException;
	
	/**
	 * Register one listener for all address change
	 * @param listener
	 */
	void register(Listener listener);
}
