/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.common.matcher.StringMatcher;
import com.galaxy.hsf.service.route.RouteResultCache.RefHolder;
import com.galaxy.hsf.util.NumberUtil;

/**
 * 
 * @author sihai
 *
 * @param <M>
 */
public class RouteResultCalculator<M> {

	private static final Log LOGGER = LogFactory.getLog(Logger.LOGGER_NAME);
	
	private static final String WEIGHT_SPLIT = "::";
	
	@SuppressWarnings("unchecked")
    public void reset(RouteResultCache<String> addressCache) {
		if(addressCache instanceof SimpleRouteResultCache) {
			SimpleRouteResultCache adsCache = (SimpleRouteResultCache)addressCache;
			
			Map<Object, List<List<String>>> _allKeyedAddresses = new HashMap<Object, List<List<String>>>();
            List<List<String>> _interfaceAddresses = null;
            Map<M, List<List<String>>> _mathodAddresses = new HashMap<M, List<List<String>>>();
            Map<M, Map<Object, List< List<String>>>> _mathodKeyedAddresses = new HashMap<M, Map<Object, List<List<String>>>>();
            
            
            //重新计算allKeyedAddresses
            RouteRule<M> newRoutingRule = adsCache.getRouteRule();
            List<String> newAllAvailableAddresses = adsCache.getAllAvailableAddresses();
            
            try {
                if (newRoutingRule == null || newRoutingRule.getKeyedRules() == null) {
                    //尚没有路由规则，直接新建空白规则; 规则中KeyedRules为空时，其他数据已无意义，只能忽略
                    newRoutingRule = new RouteRule<M>();
                    adsCache.setRouteRule(newRoutingRule);
                } else { //(routingRule.getKeyedRules() != null) {
                    for (Map.Entry<? extends Object, ? extends List<String>> e : newRoutingRule.getKeyedRules().entrySet()) {
                    	List< List<String>> rs = filter(newAllAvailableAddresses, e.getValue());
	                    if(rs != null ){
	                    	_allKeyedAddresses.put(e.getKey(), rs);
	                    }
                    }
                }
                
              //重新计算接口级地址列表this.interfaceAddresses
                Object key4InterfaceRule = newRoutingRule.getInterfaceRule();
                if (key4InterfaceRule != null) {
                    List<String> rule = newRoutingRule.getKeyedRules().get(key4InterfaceRule);
                    if (rule != null) {
                        _interfaceAddresses = filter(newAllAvailableAddresses, rule);
                    }
                }

                //重新计算方法级地址列表this.mathodAddresses
                Map<M, Object> methodRule = newRoutingRule.getMethodRule();
                if (methodRule != null) {
                    List<String> parentList = newAllAvailableAddresses;
                    if(_interfaceAddresses != null){
                        parentList = getAllAddress(_interfaceAddresses);
                        
                    }
                    for (Map.Entry<M, Object> e : methodRule.entrySet()) {
                        List<String> regxList = newRoutingRule.getKeyedRules().get(e.getValue());
                        List<List<String>> rs = filter(parentList, regxList);
                        if(rs != null){
                            _mathodAddresses.put(e.getKey(), rs); 
                        }
                    
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(" 解析路由规则出错", e);
                return;
            }
            
            // 为统一接口,增加可用地址Map
            List<List<String>> allAvailableAddressList = new ArrayList<List<String>>();
            allAvailableAddressList.add(newAllAvailableAddresses);
            //做引用切换
            RefHolder<M> newRefs = new RefHolder<M>();
            newRefs.setAllAvailableAddresses(newAllAvailableAddresses) ;
            newRefs.setRoutingRule(newRoutingRule);
            newRefs.setAllKeyedAddresses(_allKeyedAddresses);
            newRefs.setInterfaceAddresses(_interfaceAddresses);
            newRefs.setMathodAddresses(_mathodAddresses);
            newRefs.setMathodKeyedAddresses(_mathodKeyedAddresses) ;
            newRefs.setAllAvailableAddressList(allAvailableAddressList);
            adsCache.setRefs(newRefs) ; 
		}
	}
	
	/**
     * 获取所有地址
     * @param interfaceAddresses
     * @return
     */
    private List<String> getAllAddress(List<List<String>> interfaceAddresses) {
        List<String> allInterfaceAddress = new ArrayList<String>();
        for(List<String> addresses : interfaceAddresses){
            for(String address : addresses){
                if(!allInterfaceAddress.contains(address)){
                    allInterfaceAddress.add(address);
                }
            }
        }
        return allInterfaceAddress;
    }
    
    /**
     * 根据地址正则式过滤地址生成权重Map,其中的key为权重的每个值。
     * @param addresses
     * @param regxList
     * @return
     */
            
    public  List<List<String>> filter(List<String> addresses, List<String> regxList) {
        List<List<String>> rs = new ArrayList< List<String>>();
        if(hasWeight(regxList)){//有权重规则的
            for(String regx : regxList){
                int pos = regx.indexOf(WEIGHT_SPLIT);
                String ipRegx = null;
                int weight;
                if( pos > 0){
                     ipRegx = regx.substring(0,pos).trim();
                     weight = Integer.parseInt(regx.substring(pos+2).toString().trim());
                }else {//没权重 默认值为1
                    ipRegx = regx.trim();
                    weight = 1;
                }
                if(weight <= 0){
                	throw new RuntimeException("权重值不能小于或者等于0");
                }
                if(!ipRegx.endsWith("*")){
                	ipRegx = ipRegx+"*";
                }
                StringMatcher sm = new StringMatcher(ipRegx, true, false);
                List<String> realAddress = new ArrayList<String>();
                for(String address: addresses){
                    if(sm.match(address)){
                        realAddress.add(address);
                    }
                }
                if(realAddress.size() > 0){
                    int len = rs.size();
                    for(int i = 0 ;i< weight;i++){
                        rs.add(len+i, realAddress);
                    }
                }
               
                
            }
        }else {//无权重规则
            List<String> realAddress = new ArrayList<String>();
            for(String regx : regxList){
            	String reg = regx.trim();
            	if(!reg.endsWith("*")){
            		reg = reg+"*";
            	}
                StringMatcher sm = new StringMatcher(reg,true,false);
                for(String address: addresses){
                    if(sm.match(address)){
                        if(!realAddress.contains(address)){
                            realAddress.add(address);
                        }
                    }
                }
            }
            if(realAddress.size() != 0){
                rs.add(realAddress);
                return rs;
            }
           
        }
        if(rs.isEmpty()){
            return rs;
        }
        return compressList(rs);
    }

    private static boolean hasWeight(List<String> regxList) {
        for(String regx : regxList){
            if(regx.indexOf(WEIGHT_SPLIT) > 0){
                return true;
            }
        }
        return false;
    }
    
    private List<List<String>>  getInterfaceAddressesWithoutLock(RefHolder<M> refs) {
        return refs.getInterfaceAddresses() == null ? refs.getAllAvailableAddressList() : refs.getInterfaceAddresses();
    }

    

    private List<List<String>>  getMathodAddressesWithoutLock(RefHolder<M> refs ,M m) {
        List<List<String>>  res = refs.getMathodAddresses().get(m);
        return res == null ? this.getInterfaceAddressesWithoutLock(refs) : res;
    }

    /**
     * 返回参数级地址列表。单亲委派模式：参数级 -> 方法级 -> 接口级 -> 全部可用地址 
     */
    public List<List<String>>  getArgsAddresses(RouteResultCache<M> cache,M m, Object[] args) {
        RefHolder<M> refs = cache.getRefs();
        return this.getArgsAddressesWithoutLock(refs,m, args);
    }
    
    private List<List<String>>  getArgsAddressesWithoutLock(RefHolder<M> refs,M m, Object[] args) {
        
        if(refs.getRoutingRule().getArgsRule() == null){
            return this.getMathodAddressesWithoutLock(refs,m);
        }
        
        Args2KeyCalculator c = refs.getRoutingRule().getArgsRule().get(m);
        if (c == null) {
            return this.getMathodAddressesWithoutLock(refs,m);
        }

        Object key = c.calculate(args);
        if (key == null) {
            return this.getMathodAddressesWithoutLock(refs,m);
        }
        
        List<List<String>> addrs4Calculatedkey = refs.getAllKeyedAddresses().get(key);
        if(addrs4Calculatedkey == null){
            return this.getMathodAddressesWithoutLock(refs,m);
        }

        Map<Object, List<List<String>>> keyedAddresses = refs.getMathodKeyedAddresses().get(m);
        if (keyedAddresses == null) {
            //考虑并发: 多个线程同时写入的相互覆盖问题
            keyedAddresses = new HashMap<Object, List< List<String>>>();
            keyedAddresses = putIfAbsent(refs.getMathodKeyedAddresses(), m, keyedAddresses);
        }

        List<List<String>> res = keyedAddresses.get(key);
        if (res == null) {
            //考虑并发: 多个线程同时写入的相互覆盖问题
            res = intersection(addrs4Calculatedkey, this.getMathodAddressesWithoutLock(refs,m));
            if(res != null){
                res = putIfAbsent(keyedAddresses, key, res);
            }else{
                return this.getMathodAddressesWithoutLock(refs,m);
            }
            
        }
        return res;
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

    /**
     * 求参数列表与方法列表的交集.
     * @param argList
     * @param mathodList
     * @return
     */
    private  List<List<String>> intersection(List<List<String>> argList, List<List<String>> mathodList) {
        List< List<String>> rs = new ArrayList<List<String>>();
        List<String> mathodsAddress = getAllAddress(mathodList);
        List<String> preAddresses = null;
        List<String> realAddress = null;
        int count = 0;
        int len = argList.size();
        for(int i = 0;i<len ;i++){
            List<String> addresses = argList.get(i);
            if(preAddresses == addresses){//引用相等
                if(realAddress.size() > 0){//没被过滤掉,添加进结果集
                    rs.add(count, realAddress);
                }else{//被过滤掉
                    continue;
                }
            }else{//引用不相等,计算新的地址
                preAddresses = addresses;
                realAddress = new ArrayList<String>();
                for(String address : addresses){
                    if(mathodsAddress.contains(address)){
                        realAddress.add(address);
                    }
                }
                if(realAddress.size() > 0){//没被过滤掉
                    rs.add(count, realAddress);
                }else {//被过滤掉
                    continue;
                }
            }
            count++;
        }
        // 优化
        if(rs.size() == 0){
        	return rs;
        }
        return compressList(rs);
    }
    /**
     * 优化,有最大公约数的压缩下
     * @param rs
     * @return
     */
    private List<List<String>> compressList(List< List<String>> rs){
        int len = rs.size();
        if(len == 0){
            return null;
        }

        List< List<String>>  result = new ArrayList< List<String>>();
        List<Integer> everyWeight = new ArrayList<Integer>();
        List<String> preAddresses = null;
        int count = 0 ;
        Integer weight = 0;
        for(int i =0;i<len ;i++){
            List<String> currentAddresses = rs.get(i);
            weight = i;
            if(currentAddresses != preAddresses){
                if(preAddresses != null){
                    everyWeight.add(weight-count);
                    count = weight;
                }
                preAddresses = currentAddresses;
            }
        }
        everyWeight.add(weight-count+1);
        if(everyWeight.size() == 1){// 只有一个情况
            result.add(rs.get(0));
            return result;
        }
        int maxgys = NumberUtil.getMaxGYS(everyWeight);
        if(maxgys == 1){//最大公约数为一，说明不可以优化.
            return rs;
        }else{
            int pos = len/maxgys;
            for(int i = 0 ;i<pos;i++){
                result.add(i,  rs.get(i*maxgys));
            }
        }
        return result;
    }
}