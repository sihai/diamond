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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存并维护一份路由规则下的地址列表计算结果。
 * 一个服务的路由结果
 * <p>
 * 
 * @param <M> 代表方法签名的对象。
 *            可以是java.lang.reflect.Method，也可以是一个方法名和参数类型连接起来的String
 *            如果是后者，连接符必须为RouteRule.METHOD_SIGS_JOINT_MARK
 *            连接的第一项为方法名，之后各项为每个参数的类名称(Class.getName()返回值)。
 *            
 * @author sihai
 *
 */
public interface RouteResultCache<M> {

	void setAllAvailableAddresses(List<String> allAvailableAddresses);
	List<String> getAllAvailableAddresses();

	RouteRule<M> getRouteRule();
	void setRouteRule(RouteRule<M> rule);
	
	RefHolder<M> getRefs();
	void setRefs(RefHolder<M> refs) ;
	
	/**
	 * 计算后的路由结果
	 * @author sihai
	 *
	 * @param <M>
	 */
	public static class RefHolder<M> {
		
		private List<String> allAvailableAddresses = new ArrayList<String>(); //所有可用的服务地址
		private RouteRule<M> routingRule = new RouteRule<M>();
		/**
		 * 对应路由规则groovy的Map<String, List<String>> routingRuleMap()方法。
		 * key：同routingRuleMap的key；
		 * value：用routingRuleMap的value--正则式列表过滤allAvailableAddresses后得到的结果地址列表
		 * 为了方便计算，所有Map属性保持任何时候不为null，至少是个空Map，可以直接调用get
		 */
		private Map<Object, List<List<String>>> allKeyedAddresses = new HashMap<Object, List<List<String>>>();
		private List<List<String>> interfaceAddresses; //接口级地址列表。用接口级路由规则过滤所有可用的服务地址后的结果
		private Map<M, List<List<String>>> mathodAddresses = new HashMap<M, List<List<String>>>(); //方法级地址列表。用方法级路由规则过滤接口级地址后的结果
		private Map<M, Map<Object, List<List<String>>>> mathodKeyedAddresses = new HashMap<M, Map<Object, List<List<String>>>>();
		private List<List<String>> allAvailableAddressList= new ArrayList<List<String>>();
		
		public List<String> getAllAvailableAddresses() {
			return allAvailableAddresses;
		}
		public void setAllAvailableAddresses(List<String> allAvailableAddresses) {
			this.allAvailableAddresses = allAvailableAddresses;
		}
		public RouteRule<M> getRoutingRule() {
			return routingRule;
		}
		public void setRoutingRule(RouteRule<M> routingRule) {
			this.routingRule = routingRule;
		}
		public Map<Object, List<List<String>>> getAllKeyedAddresses() {
			return allKeyedAddresses;
		}
		public void setAllKeyedAddresses(Map<Object, List<List<String>>> allKeyedAddresses) {
			this.allKeyedAddresses = allKeyedAddresses;
		}
		public List<List<String>> getInterfaceAddresses() {
			return interfaceAddresses;
		}
		public void setInterfaceAddresses(List<List<String>> interfaceAddresses) {
			this.interfaceAddresses = interfaceAddresses;
		}
		public Map<M,List<List<String>>> getMathodAddresses() {
			return mathodAddresses;
		}
		public void setMathodAddresses(Map<M,List<List<String>>> mathodAddresses) {
			this.mathodAddresses = mathodAddresses;
		}
		public Map<M, Map<Object, List<List<String>>>> getMathodKeyedAddresses() {
			return mathodKeyedAddresses;
		}
		public void setMathodKeyedAddresses(
				Map<M, Map<Object, List<List<String>>>> mathodKeyedAddresses) {
			this.mathodKeyedAddresses = mathodKeyedAddresses;
		}
        public List<List<String>> getAllAvailableAddressList() {
            return allAvailableAddressList;
        }
        public void setAllAvailableAddressList(
                List< List<String>> allAvailableAddressList) {
            this.allAvailableAddressList = allAvailableAddressList;
        }
	}
}
