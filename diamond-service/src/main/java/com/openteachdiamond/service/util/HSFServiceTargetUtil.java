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
package com.openteachdiamond.service.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.openteach.diamond.metadata.MethodSpecial;

/**
 * 
 * @author sihai
 *
 */
public class HSFServiceTargetUtil {

	static private final char AND = '&';
    static private final char COLON = ':';
    static private final char QUEST = '?';
    static private final char EQUAL = '=';
    
    /**
     * 根据配置构造符合HSF格式的地址串.<p>
     * 格式:<p>
     * <quoteBlock><pre>
     * ip:port?paramA=valueA&paramB=valueB
     * </pre></quoteBlock>
     * 其中,"<b>?</b>"后面的为属性列表, 该列表包括两部分属性:
     * <ul>
     *  <li>对于所有RPC协议配置的公共属性, 如: 客户端超时时间, 是否支持异步, 异步方法类表等
     *  <li>针对每种RPC协议配置的特有属性, 如: 对于DEFAULT方式, 序列化类型为特有属性配置; 对于XFIRE方式, WSDL地址为特有配置.
     * </ul>
     * 
     * @param port 服务发布端口
     * @param pubProps 公共属性配置
     * @param rpcProps RPC协议特有属性配置
     * @param methodSpecials 方法特有属性配置
     * @return 符合HSF格式的地址串
     * @author 玄宵
     */
    public static String getTarget(int port, Properties pubProps, Properties rpcProps,
                                   MethodSpecial[] methodSpecials) {
        StringBuilder sb = new StringBuilder(getNetworkAddress());
        sb.append(COLON);
        sb.append(port);
        sb.append(QUEST);

     // 添加RPC协议特有属性
        if (null != rpcProps) {
            for (Map.Entry<Object, Object> entry : rpcProps.entrySet()) {
                String k = entry.getKey().toString();
                String v = entry.getValue().toString();
                sb.append(k).append(EQUAL);
                sb.append(v).append(AND);
            }
        }
        
        // 添加共有属性,私有属性默认覆盖公有属性
        if (null != pubProps) {
            for (Map.Entry<Object, Object> entry : pubProps.entrySet()) {
                String k = entry.getKey().toString();
                if(rpcProps != null && rpcProps.containsKey(k) ){
            		continue;
                }
                String v = entry.getValue().toString();
                sb.append(k).append(EQUAL);
                sb.append(v).append(AND);
            }
        }


        // 添加方法methodSpecial配置, 目前的实现中可以通过methodSpecial为方法设置不同的超时
        if (null != methodSpecials) {
            for (MethodSpecial special : methodSpecials) {
                sb.append(special.toString()).append(AND);
            }
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


    /**
     * 获取目标地址
     */
    public static String getTargetIP(String serviceTarget) {

        if ((serviceTarget == null) || ("".equals(serviceTarget.trim()))
                || (serviceTarget.indexOf(COLON) == -1)) {
            return null;
        }
        return serviceTarget.substring(0, serviceTarget.indexOf(COLON));
    }

    /**
     * 获取目标端口
     */
    public static String getTargetPort(String serviceTarget) {
        if ((serviceTarget == null) || ("".equals(serviceTarget.trim()))
                || (serviceTarget.indexOf(COLON) == -1)) {
            return null;
        }
        int index = serviceTarget.indexOf(QUEST);
        int sepIndex = serviceTarget.indexOf(COLON);
        if (index != -1) {
            return serviceTarget.substring(sepIndex + 1, index);
        } else {
            return serviceTarget.substring(sepIndex + 1);
        }
    }

    /**
     * 获取目标地址中的属性值
     */
    public static Properties getTargetProperties(String serviceTarget) {
        Properties props = new Properties();
        if ((serviceTarget == null) || ("".equals(serviceTarget.trim()))
                || (serviceTarget.indexOf(QUEST) == -1)) {
            return props;
        }
        String propsString = serviceTarget.substring(serviceTarget
                .indexOf(QUEST) + 1);
        String[] propPairs = propsString.split("" + AND);
        for (int i = 0; i < propPairs.length; i++) {
            String propPair = propPairs[i];
            int index = propPair.indexOf(EQUAL);
            props.put(propPair.substring(0, index), propPair
                    .substring(index + 1));
        }
        return props;
    }

    /**
     * 解析出一个TargetURL中包含的所有MethodSpecial配置. 保证不会返回NULL.
     */
    static public List<MethodSpecial> getMethodSpecials(String target) {
        List<MethodSpecial> specials = new ArrayList<MethodSpecial>();

        if (target.contains(MethodSpecial.split)) {
            for (String str : target.split("" + AND)) {
                MethodSpecial special = MethodSpecial.parseMethodSpecial(str);
                if (null != special) {
                    specials.add(special);
                }
            }
        }
        return specials;
    }

    /**
     * 在超过一块网卡时有点问题，因为这里每次都只是取了第一块网卡绑定的IP地址
     * TODO 找更合适的地方放置该代码
     */
    private static String getNetworkAddress() {
        Enumeration<NetworkInterface> netInterfaces;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces
                        .nextElement();
                Enumeration<InetAddress> addresses=ni.getInetAddresses();
                while(addresses.hasMoreElements()){
	                ip = (InetAddress) addresses.nextElement();
	                if (!ip.isLoopbackAddress()
	                        && ip.getHostAddress().indexOf(COLON) == -1) {
	                    return ip.getHostAddress();
	                } else {
	                    continue;
	                }
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
