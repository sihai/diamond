/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.address;

import java.util.List;

/**
 * 
 * @author sihai
 *
 */
public interface AddressService {

	/**
     * 设置目标服务的地址
     *
     * @param serviceUniqueName
     * @param addresses
     */
    void setServiceAddresses(String serviceUniqueName, List<String> addresses);

    /**
     * 获取目标服务的地址
     *
     * @param serviceUniqueName
     * @param methodName
     * @param paramTypeStrs
     * @param args
     * @return String 当没有可用的服务地址的时候，将会返回null
     */
    String getServiceAddress(String serviceUniqueName, String methodName, String[] paramTypeStrs, Object[] args);
    
    /**
	 * 设置服务的路由规则
	 * 
	 * @param serviceUniqueName 服务名
	 * @param rawRouteRuleObj 路由规则
	 */
	void setServiceRouteRule(String serviceUniqueName, Object rawRouteRuleObj);
	
	/**
	 * 
	 * @param serviceUniqueName 
	 * @return 返回Repository 推送过滤后的地址
	 */
	List<List<String>> getAllFilteredServiceAddresses(String serviceUniqueName);
	
	/**
	 * 设置服务的权重规则
	 * @param serviceUniqueName
	 * @param rule
	 */
	void setServiceWeightRule(String serviceUniqueName, String rule);
}
