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
 * 
 */
package com.openteach.diamond.service.route;

import java.util.List;

/**
 * 路由规则解析器
 * 
 * @author sihai
 *
 */
public interface RouteRuleParser {

	/**
	 * 如果不属于本Parser解析的格式，返回null
	 * 如果解析失败抛出 RoutingRuleParserException
	 * 
     * @param <M> 代表方法签名的对象。
     *            可以是java.lang.reflect.Method，也可以是一个方法名和参数类型连接起来的String
	 * @param obj 推送来的规则对象
	 * @param serviceInterface 服务的接口 
	 * @return 一个代表路由规则的RoutingRule对象
	 */
	<M> RouteRule<M> parse(Object rawRouteRuleObj, List<M> methodSigs) throws RouteRuleParserException;
}
