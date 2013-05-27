/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.address.component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.datastore.DataStore;
import com.galaxy.hsf.common.exception.internal.RuleParseException;
import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.service.address.AddressService;
import com.galaxy.hsf.service.route.RouteResultCache;
import com.galaxy.hsf.service.route.RouteResultCalculator;
import com.galaxy.hsf.service.route.RouteRule;
import com.galaxy.hsf.service.route.RouteRuleParser;
import com.galaxy.hsf.service.route.RouteRuleParserException;
import com.galaxy.hsf.service.route.RouteService;
import com.galaxy.hsf.service.route.SimpleRouteResultCache;
import com.galaxy.hsf.service.route.WeightCalculator;
import com.galaxy.hsf.service.route.WeightRule;
import com.galaxy.hsf.service.route.WeightRuleParser;
import com.galaxy.hsf.service.route.WeightRuleResultCache;

/**
 * 描述：服务地址服务实现类<p>
 * 
 * 路由规则分为接口级-->方法级-->参数级<p>
 * 若某一级的路由规则不存在，即其返回的key为null，或在routingRuleMap中找不到相应值，<p>
 *     则认为这一级对路由地址不做限制，地址取上一级的全部地址。<p>
 *     
 * 方法级路由规则（正则式）最终地址列表会和接口级地址列表作交集<p>
 * 参数级路由规则（正则式）最终地址列表会和方法级地址列表作交集<p>
 * 
 * 地址（ip）正则式列表的语义为: 若一个地址满足任何一个列表中的正则式，则地址会被加入到最终调用列表中<p>
 * <p>
 * <p>
 * 路由规则是一段string表示的groovy代码，代码中包含一个groovy类，groovy类包含以下几个方法中的全部或者部分<p>
 *      Map<String, List<String>> routingRuleMap(); (key：用户定义的key; value：地址过滤的正则式列表)<p>
 *      String interfaceRoutingRule(); 返回routingRuleMap()中的key，对应接口级规则<p>
 *      String mathodRoutingRule(String methodName, String[] paramTypeStrs); 返回routingRuleMap()中的key，方法级规则<p>
 *      Object argsRoutingRule(String methodName, String[] paramTypeStrs); --参数级规则<p>
 *         返回一个groovy闭包，闭包接受一个Object[]参数，返回规则映射表routingRuleMap中的一个key<p>
 * <p>
 * HSF每次接到推送的路由规则，或新的地址列表后:<p>
 *    1. 调用groovy.routingRuleMap获得routingRuleMap；若方法不存在则认为不做任何限制，忽略以下各步骤。<p>
 *    2. 调用interfaceRoutingRule获得接口级key，以返回key取得routingRuleMap对应的正则式列表，计算接口级地址列表并缓存<p>
 *       如果interfaceRoutingRule方法不存在、返回null或routingRuleMap中不存在的key，<p>
 *       则取全部可用服务地址作为接口级地址列表<p>
 *    3. 将routingRuleMap中除接口级之外的其他正则式列表，分别作用于接口级地址列表，存储计算结果<p>
 *    4. 对每个接口方法，以其签名为参数，调用groovy.mathodRoutingRule，以返回key取得3中缓存的地址列表，<p>
 *       作为方法级地址列表。若mathodRoutingRule方法不存在、返回null或routingRuleMap中不存在的key，<p>
 *       则取接口级地址列表作为方法级地址列表<p>
 *    5. 对每个接口方法，以其签名为参数，调用groovy.argsRoutingRule, 若不为空，则hsf编译返回的闭包，缓存编译后的闭包对象 <p>
 *       若argsRoutingRule方法不存在或返回空，说明接口方法不需要参数路由。hsf在接口方法->闭包的映射表中不保存映射<p>
 *       
 * 方法正真调用时，HSF以方法签名在缓冲中查找groovy闭包对象，若找到，则将方法参数数组作为参数执行闭包，<p>
 *    以闭包执行结果作为key取得3中缓存的地址列表，与该方法的方法级地址列表取交集，并缓存交集结果。<p>
 *    闭包执行结果返回null，则认为不对地址列表做过滤，直接取方法签名本身对应的ip地址列表<p>
 * <p>
 * <p>
 *   所有可用服务地址  -->   接口级地址     -->   方法级地址       -->   参数级地址<p>
 * <p>
 *                     |                   |    ┌ xxx.xxx.xxx.xxx |                    ┌ xxx.xxx.xxx.xxx		<p>
 *   xxx.xxx.xxx.xxx   |                   | M1-| ...             |             ┌ key1-| ...					<p>
 *   xxx.xxx.xxx.xxx   | xxx.xxx.xxx.xxx   |    └ xxx.xxx.xxx.xxx | M1-closure1 |      └ xxx.xxx.xxx.xxx		<p>
 *   ...               | xxx.xxx.xxx.xxx   | ...                  |             └ ...							<p>
 *                     | ...               |                      | ...											<p>
 *                     |                   | ...                  |												<p>
 *                     |                   |                      |                    ┌ xxx.xxx.xxx.xxx		<p>
 *                     |                   |    ┌ xxx.xxx.xxx.xxx |             ┌ key1-| ...					<p>
 *                     | xxx.xxx.xxx.xxx   | Mn-| ...             | Mn-closuren |      └ xxx.xxx.xxx.xxx		<p>
 *   xxx.xxx.xxx.xxx   |                   |    └ xxx.xxx.xxx.xxx |             └ ...							<p>
 *                     |                   |                      |												<p>
 *                     ↓                   ↓                      ↓												<p>
 *                接口级路由           方法级路由              参数级路由															<p>
 *          interfaceRoutingRule    mathodRoutingRule      argsRoutingRule										<p>
 *  \                  |                   |                      |                                   /			<p>
 *   \                 |                   |                      |                                  /     		<p>
 *    \_________________Map<String, List<String>> routingRuleMap()__________________________________/			<p>
 *
 * @author sihai
 * 
 * 	
 * 	Groovy_v200907@package hqm.test.groovy
	public class RoutingRule{    
     	Map<String, List<String>> routingRuleMap(){        
        	return [
            	"ALL":["*"],"NULL":[]
            ];
    	}
	    String interfaceRoutingRule(){        
	        return "ALL";    
	    }    
	    String mathodRoutingRule(String methodName, String[] paramTypeStrs){ 
	        return "ALL";
	    } 
	    Object argsRoutingRule(String methodName, String[] paramTypeStrs){
	        return null;    
	    }
     	public static void main(String[] args){
		}
 	}
 *
 */
public class AddressComponent implements AddressService {

	private static final Log logger = LogFactory.getLog(Logger.LOGGER_NAME);
	
	public static final String COMPONENT_NAME = AddressComponent.class.getName();
	public static final String ROUTE_RESULT_CACHE_STORE_KEY="_RouteResultCache";
	
	private ReentrantLock lock = new ReentrantLock();
	

	/**
	 * 依赖OSGi注入的属性
	 */
    private RouteService routeService;    
    private DataStore dataStore;
    private List<RouteRuleParser> ruleParsers = new ArrayList<RouteRuleParser>();
    
    @SuppressWarnings("unchecked")
	private RouteResultCalculator routeCalculator = new RouteResultCalculator();
    
    private Random rand = new Random();
    
    public void setDataStore(DataStore dataStore) {
		this.dataStore = dataStore;
	}
	
	public void setRouteService(RouteService routeService){
        this.routeService = routeService;
    }

    public void setRouteRuleParser(RouteRuleParser parse){
    	this.ruleParsers.add(parse);
    }
    
    @SuppressWarnings("unchecked")
    private RouteResultCache<String> getRouteResultCache4Read(String serviceUniqueName){
		Map<String, RouteResultCache<String>> routeResultCaches = (Map<String, RouteResultCache<String>>) dataStore.get(COMPONENT_NAME, ROUTE_RESULT_CACHE_STORE_KEY);
		if(routeResultCaches == null){
			return null;
		}
		return routeResultCaches.get(serviceUniqueName);
    }
    
    @SuppressWarnings("unchecked")
    private  RouteResultCache<String> getRouteResultCache(String serviceUniqueName){
		Map<String, RouteResultCache<String>> routeResultCaches = (Map<String, RouteResultCache<String>>) dataStore.get(COMPONENT_NAME, ROUTE_RESULT_CACHE_STORE_KEY);
		if(routeResultCaches == null) {
			routeResultCaches = new HashMap<String, RouteResultCache<String>>();
			dataStore.put(COMPONENT_NAME, ROUTE_RESULT_CACHE_STORE_KEY, routeResultCaches);
		}
		RouteResultCache<String> cache = routeResultCaches.get(serviceUniqueName);
		if (cache == null) {
			//这个写入一般是每个线程写各自的key，忽略HashMap的线程安全问题
			cache = new SimpleRouteResultCache();
			cache = putIfAbsent(routeResultCaches, serviceUniqueName, cache);
		}
		return cache;
    }
    
	@Override
	public void setServiceAddresses(String serviceUniqueName, List<String> addresses) {
		 //lock.lock();
	     try {
	        RouteResultCache<String> addressCache = this.getRouteResultCache(serviceUniqueName);
	        addressCache.setAllAvailableAddresses(addresses);
	        // 
	        routeCalculator.reset(addressCache);
	        // 重新计算该服务的权重
	        Object obj = dataStore.get(WeightRule.WEIGHT_RULE_COMPONENT_NAME, WeightRule.WEIGHT_RULE_COMPONENT_KEY);
	        if(obj != null) { //有权重规则
	            WeightRuleResultCache weightRuleResultCache = (WeightRuleResultCache)obj;
	            List<List<String>> weightRuleList = weightRuleResultCache.getWeightRuleCacheList(serviceUniqueName);
	            if(weightRuleList != null) { //有该服务的权重规则
	                Map<String, WeightRule> weightRule = weightRuleResultCache.getWeightRule();
	                List< List<String>> newList = WeightCalculator.calculate(serviceUniqueName, weightRule, addresses);
	                if(newList != null && !newList.isEmpty()){
	                    weightRuleResultCache.putWeightRuleResult(serviceUniqueName, newList);
	                }
	             
	            }
	        }
        } finally {
            //lock.unlock();
        }
	}

	@Override
	public String getServiceAddress(String serviceUniqueName,
			String methodName, String[] paramTypeStrs, Object[] args) {
		// 记录下RouteService不可用的现象
        if(null == routeService) {
        	logger.error("RouteService is null!");
            return null;
        }
        RouteResultCache<String> addressCache = getRouteResultCache4Read(serviceUniqueName);
        if(null == addressCache) {
        	logger.error("不能获取服务地址,服务名：["+serviceUniqueName+"] ,调用方法["+methodName+"]");
            return null;
        }
        Map<? extends Object, ? extends List<String>> keyedRules = addressCache.getRefs().getRoutingRule().getKeyedRules();
        if(keyedRules != null) {//有路由规则,使用路由规则找地址
            String methodSigs = RouteRule.joinMethodSigs(methodName, paramTypeStrs); 
            List< List<String>> weightMap = routeCalculator.getArgsAddresses(addressCache,methodSigs, args);
            return  calculateAddress(weightMap);
        } else {//使用权重规则找地址
            return routeService.getServiceAddress(serviceUniqueName,addressCache.getRefs().getAllAvailableAddresses());
        }
	}

	@Override
	public void setServiceRouteRule(String serviceUniqueName, Object rawRouteRuleObj) {
		Class<?> serviceInterface;
		try {
			String interfaceName = serviceUniqueName.substring(0, serviceUniqueName.indexOf(":"));
			serviceInterface = Class.forName(interfaceName);
		} catch (ClassNotFoundException cnfe) {
			logger.error("接口类不存在。serviceUniqueName=" + serviceUniqueName, cnfe);
			return;
		}
		
		List<String> allMethodSigs = new ArrayList<String>();
		for(Method m : serviceInterface.getMethods()){
			allMethodSigs.add(RouteRule.joinMethodSigs(m));
		}

		/**
		 * 获得推送的路由规则后，遍历解析器列表，取第一个可以解析的解析器解析之
		 */
		RouteRule<String> rule = null;
		for (RouteRuleParser parser : this.ruleParsers) {
			try {
				rule = parser.parse(rawRouteRuleObj, allMethodSigs);
			} catch (RouteRuleParserException e) {
				logger.error("解析路由规则失败。rawRouteRuleObj=" + rawRouteRuleObj, e);
				return;
			}
			if (rule != null) {
				break;
			}
		}
		if (rule != null) {
		    lock.lock();
		    try {
		        RouteResultCache<String> addressCache = this.getRouteResultCache(serviceUniqueName);
	            addressCache.setRouteRule(rule);
	            routeCalculator.reset(addressCache);
	            if(rule.getKeyedRules() == null){//用户清空之前路由规则,如果权重规则存在,则生成该服务权重规则
	                Object obj = dataStore.get(WeightRule.WEIGHT_RULE_COMPONENT_NAME, WeightRule.WEIGHT_RULE_COMPONENT_KEY);
	                if(obj != null){
	                    WeightRuleResultCache weightRuleResultCache = (WeightRuleResultCache)obj;
	                    Map<String, WeightRule>  weightRuleMap = weightRuleResultCache.getWeightRule();
	                    List<List<String>> weightAndUrls =WeightCalculator.calculate(serviceUniqueName, weightRuleMap, addressCache.getAllAvailableAddresses());
	                    if(weightAndUrls != null && !weightAndUrls.isEmpty()){
	                        weightRuleResultCache.addWeightRuleCache(serviceUniqueName, weightAndUrls);
	                    }
	                }
	                
	            }
            }finally{
                lock.unlock();
            }
		} else {
			logger.warn("Could not parse route rule:" + rawRouteRuleObj);
		}
	}

	@Override
	public List<List<String>> getAllFilteredServiceAddresses(
			String serviceUniqueName) {
		RouteResultCache<String> addressCache = getRouteResultCache4Read(serviceUniqueName);
		if(addressCache == null){
			return null;
		}
		return addressCache.getRefs().getInterfaceAddresses();
	}

	@Override
	public void setServiceWeightRule(String serviceUniqueName, String rule) {
		try {
			WeightRule weightRule = new WeightRuleParser().parse(rule);
			Map<String, RouteResultCache<String>> routeResultCaches = (Map<String, RouteResultCache<String>>) dataStore.get(COMPONENT_NAME, ROUTE_RESULT_CACHE_STORE_KEY);
			if(null == weightRule) {
				// XXX ???
				if(null != routeResultCaches) {
					routeResultCaches.remove(serviceUniqueName);
				}
			} else {
				
			}
		} catch (RuleParseException e) {
			logger.error(e);
		}
		
		Map<String, WeightRule> weightRuleInfoMap = new WeightRuleParser().parse(rule);
		if(weightRuleInfoMap == null) {//解析权重规则出错
		    logger.error("error parse weight rule!");
		    return ;
		}
		lock.lock();
		try {
		    if(weightRuleInfoMap.isEmpty()) {//清空权重规则
	            dataStore.remove(WeightRule.WEIGHT_RULE_COMPONENT_NAME, WeightRule.WEIGHT_RULE_COMPONENT_KEY);
	            return ;
	        }
	        Map<String, RouteResultCache<String>> routeResultCaches = (Map<String, RouteResultCache<String>>) dataStore.get(COMPONENT_NAME, ROUTE_RESULT_CACHE_STORE_KEY);
	        WeightRuleResultCache  weightRuleResultCache = null;
	        //忽略线程安全
	        if(routeResultCaches == null) {
	            weightRuleResultCache = new WeightRuleResultCache();
	            weightRuleResultCache.setWeightRule(weightRuleInfoMap);
	        } else {
	            weightRuleResultCache = WeightCalculator.calculate(routeResultCaches, weightRuleInfoMap);
	        }
	        
	        dataStore.put(WeightRule.WEIGHT_RULE_COMPONENT_NAME, WeightRule.WEIGHT_RULE_COMPONENT_KEY , weightRuleResultCache);
        } finally {
            lock.unlock();
        }
	}
	
	
	private String calculateAddress(List<List<String>> weightList) {
	    if(weightList != null){
	        int len = weightList.size();
	        if(len == 0){
	            return null;
	        }else if(len == 1){
	            List<String> addresses = weightList.get(0);
	            return randomAddress(addresses);
	        }else {
	            int randWeight = rand.nextInt(len);
	            List<String> addresses = weightList.get(randWeight);
	            return  randomAddress(addresses);
	        }
	    }
        return null;
    }

	private String randomAddress(List<String> addresses) {
		if (addresses != null) {
			int addressSize = addresses.size();
			if (addressSize == 0) {
				return null;
			} else if (addressSize == 1) {
				return addresses.get(0);
			} else {
				int randNum = rand.nextInt(addressSize);
				return addresses.get(randNum);
			}
		}
		return null;
	}

    /**
	 * 线程安全的putIfAbsent，保证多线程间不会因相互覆盖而持有已不再map中的value引用。
	 * 语义不同于ConcurrentHashMap的putIfAbsent：
	 * 以传入的map对象作为同步锁，若传入key已存在于map中，则返回已存在值，否则put并返回传入的value
	 * @return 返回的value对象的引用，总是指向map中最终和key对应的value对象。
	 */
	private static <K, V> V putIfAbsent(Map<K, V> m, K key, V value) {
		synchronized (m) {
			V exist = m.get(key);
			if (exist == null) {
				m.put(key, value);
				return value;
			} else {
				return exist;
			}
		}
	}
}
