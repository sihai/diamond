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
package com.openteach.diamond.metadata;

import com.openteach.diamond.common.exception.DiamondException;
import com.openteach.diamond.common.lifecycle.LifeCycle;


/**
 * 描述：发布服务元数据的服务
 * 
 * @author sihai
 *
 */
public interface MetadataReadService extends LifeCycle {
	
	/**
	 * 订阅服务元数据信息
	 * 
	 * @param serviceName
	 * @return metadata 元数据对象
	 * @throws DiamondException
	 */
	ServiceMetadata subscribe(String serviceName) throws DiamondException;
	
	/**
	 * 
	 * @param listener
	 */
	void register(Listener listener);

}