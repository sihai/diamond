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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 缓存并维护一份路由规则下的地址列表计算结果。
 * <p>
 * 
 *   所有可用服务地址  -->   接口级地址     -->   方法级地址       -->   参数级地址
 *    
 *                     |                   |    ┌ xxx.xxx.xxx.xxx |                    ┌ xxx.xxx.xxx.xxx
 *   xxx.xxx.xxx.xxx   |                   | M1-| ...             |             ┌ key1-| ...
 *   xxx.xxx.xxx.xxx   | xxx.xxx.xxx.xxx   |    └ xxx.xxx.xxx.xxx | M1-closure1 |      └ xxx.xxx.xxx.xxx
 *   ...               | xxx.xxx.xxx.xxx   | ...                  |             └ ...
 *                     | ...               |                      | ...
 *                     |                   | ...                  |
 *                     |                   |                      |                    ┌ xxx.xxx.xxx.xxx
 *                     |                   |    ┌ xxx.xxx.xxx.xxx |             ┌ key1-| ...
 *                     | xxx.xxx.xxx.xxx   | Mn-| ...             | Mn-closuren |      └ xxx.xxx.xxx.xxx
 *   xxx.xxx.xxx.xxx   |                   |    └ xxx.xxx.xxx.xxx |             └ ...
 *                     |                   |                      |
 *                     ↓                   ↓                      ↓
 *                接口级路由           方法级路由              参数级路由
 *          interfaceRoutingRule    mathodRoutingRule      argsRoutingRule
 *  \                  |                   |                      |                                   /
 *   \                 |                   |                      |                                  /     
 *    \_________________Map<String, List<String>> routingRuleMap()__________________________________/
 *    
 *    
 * 用法：设置allAvailableAddresses和routingRule，之后可以只设置其中一个，然后调用reset()以更新地址缓存
 *       调用getInterfaceAddresses、getMathodAddresses、getArgsAddresses以取得最新地址列表
 * 
 * 推送过来的路由规则中的原始"正则式"实际只是支持通配符*号和？号的服务地址。一种简化的匹配表示
 * 
 * @param <M>
 * 		含义参见 RoutingRule
 * 
 * @author sihai
 *
 */
public class SimpleRouteResultCache<M> implements RouteResultCache<M> {

	/**
	 * 输入：所有的可用地址 + 路由规则
	 */
	private List<String> newAllAvailableAddresses = new ArrayList<String>(); //所有可用的服务地址
	private RouteRule<M> newRoutingRule;

	/**
	 * 所有读取都通过该对象, reset时整个对象做引用切换，以此避免新旧两套数据被交叉读取。
	 */
	private volatile RefHolder<M> refs = new RefHolder<M>();
	
	public void setAllAvailableAddresses(List<String> allAvailableAddresses) {
		this.newAllAvailableAddresses = allAvailableAddresses;
	}
	
	public List<String> getAllAvailableAddresses(){
		return this.newAllAvailableAddresses;
	}

	public void setRouteRule(RouteRule<M> rule) {
		this.newRoutingRule = rule;
	}
	public RouteRule<M> getRouteRule(){
		return this.newRoutingRule;
	}
	
	public void setRefs(RefHolder<M> refs) {
		this.refs = refs;
	}
	
	public RefHolder<M> getRefs(){
		return this.refs;
	}
}
