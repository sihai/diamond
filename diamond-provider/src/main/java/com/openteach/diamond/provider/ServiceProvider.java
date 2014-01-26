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
package com.openteach.diamond.provider;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.LifeCycle;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.service.MethodInvoker;


/**
 * 
 * @author sihai
 *
 */
public interface ServiceProvider extends LifeCycle {

	/**
	 * 
	 * @return
	 */
	ServiceMetadata getMetadata();
	
	/**
	 * 
	 * @return
	 */
	MethodInvoker getMethodInvoker();
	
	/**
	 * 
	 * @param methodName
	 * @param parameterTypes
	 * @param args
	 * @return
	 * @throws DiamondException
	 */
	Object invoke(String methodName, String[] parameterTypes, Object ... args) throws DiamondException;
}
