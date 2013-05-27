/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

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
