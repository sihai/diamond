/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

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
