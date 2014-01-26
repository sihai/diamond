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
package com.openteach.diamond.service.route;

import java.util.List;

/**
 * 描述：服务地址路由服务，由路由服务来决定从一堆列表中获取哪个地址
 * 
 * @author sihai
 *
 */
public interface RouteService {

	/**
	 * 从一堆地址列表中获取可调用的目标地址
	 * @param serviceUniqueName
	 * @param addresses
	 * @return String 当没有可用的服务时，将返回null
	 */
	String getServiceAddress(String serviceUniqueName , List<String> addresses);
}
