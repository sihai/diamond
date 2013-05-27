/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.common.matcher.StringMatcher;
import com.galaxy.hsf.util.NumberUtil;

/**
 * 
 * @author sihai
 *
 */
public class WeightCalculator {

	private static final Log LOGGER = LogFactory.getLog(Logger.LOGGER_NAME);
	
	public static WeightRuleResultCache calculate(Map<String, RouteResultCache<String>> routeResultCaches, Map<String, WeightRule> ruleMap){
	    WeightRuleResultCache weightRuleResultCache = new WeightRuleResultCache();
	    weightRuleResultCache.setWeightRule(ruleMap);
	    
        Iterator<Map.Entry<String, RouteResultCache<String>>>  ite = routeResultCaches.entrySet().iterator();
        while(ite.hasNext()){
            Map.Entry<String, RouteResultCache<String>> entry =  ite.next();
            String serviceUniqueName = entry.getKey();
            RouteResultCache<String> cache = entry.getValue();
            if(cache == null){
                continue;
            }
            if(cache.getRouteRule() != null && cache.getRouteRule().getKeyedRules() != null){//有此服务的路由规则,
                continue;
            } else {//没有此服务的路由规则,查找该服务的权重规则
                
                List<String> allAvailableAddresses = cache.getAllAvailableAddresses();
                if(allAvailableAddresses == null || allAvailableAddresses.size() == 0){ //没可用地址
                    continue;
                }
                List<List<String>> weightAndurls = calculate(serviceUniqueName ,ruleMap, allAvailableAddresses);
                if(!weightAndurls.isEmpty()){
                    weightRuleResultCache.addWeightRuleCache(serviceUniqueName, weightAndurls); 
                }
         
            }
        }
		   
	    return weightRuleResultCache;
	}

    public static List<List<String>> calculate(String serviceUniqueName, WeightRule weightRule, List<String> allAvailableAddresses) {
        //
    	return null;
    }
}
