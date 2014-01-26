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
 * 
 */
package com.openteach.diamond.service;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.LifeCycle;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceInvoker extends LifeCycle {

	/**
	 * 
	 * @param name
	 * @param invoker
	 * @return
	 */
	void register(String serviceName, MethodInvoker invoker);
	
	/**
	 * 
	 * @param serviceName
	 * @return
	 */
	void unregister(String serviceName);
	
	/**
	 * 
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws DiamondException
	 */
	Object invokeLocal(String serviceName, String method, String[] parameterTypes, Object[] args) throws DiamondException;
	
	/**
	 * 
	 * @param serviceName
	 * @param method
	 * @param parameterTypes
	 * @param args
	 * @param protocol
	 * @return
	 * @throws DiamondException
	 */
	Object invokeRemote(String serviceName, String method, String[] parameterTypes, Object[] args, String protocol) throws DiamondException;
}
