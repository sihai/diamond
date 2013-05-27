/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author sihai
 *
 */
public class WeightRule {

	public static final String WEIGHT_RULE_COMPONENT_NAME = "com.galaxy.hsf.service.rule.weight";
	
	public static final String WEIGHT_RULE_COMPONENT_KEY = "_WEIGHTRULECACHE";
	
	// 服务名(支持正则表达式)
	private String serviceName;
	
	// 服务地址的正则式，及每个正则式的权重值
	private Map<String, Integer> ipAndWeights = new HashMap<String, Integer>();
	
	private int allWeight = 0;
	
	private List<Integer> everyWeight = new ArrayList<Integer>();
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public void addIPAndWeight(String ipRexp, int weight) {
		ipAndWeights.put(ipRexp, new Integer(weight));
		addWeight(weight);
	}
	public Map<String, Integer> getIPAndWeights() {
		return ipAndWeights;
	}
    
	private void addWeight(int weight) {
	    everyWeight.add(weight);
	    allWeight += weight;
	}
	
	public int getAllWeight(){
	    return allWeight;
	}
	
	public List<Integer> getEveryWeight(){
	    return everyWeight;
	}
}
