/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 一个表示路由规则的Bean
 * 一个服务的路由规则
 * 
 * 具体规则的表现形式为一个地址过滤正则式列表
 * 语义为：若一个地址满足列表中的某一个正则式，则会被选中，否则被过滤。
 * 被选中的地址会被加入到最终调用列表中
 * 
 * @param <M> 代表方法签名的对象。
 *            可以是java.lang.reflect.Method，也可以是一个方法名和参数类型连接起来的String
 *            如果是后者，连接符必须为RouteRule.METHOD_SIGS_JOINT_MARK
 *            连接的第一项为方法名，之后各项为每个参数的类名称(Class.getName()返回值)。
 *            
 * @author sihai
 *
 */
public class RouteRule<M> {

	/**
	 * 分隔符定义，及工具方法
	 * 分隔符的定义要求即能区分(不能用在类名定义中)，又能给String.split(regex)方法直接使用而不用转化
	 */
	public static final String METHOD_SIGS_JOINT_MARK = "#";
	
	private Map<? extends Object, ? extends List<String>> keyedRules;
	private Object interfaceRule; /* 在keyedRules中的key */								//接口级路由规则
	private Map<M, Object/* 在keyedRules中的key */> methodRule; 							//方法级路由规则
	private Map<M, Args2KeyCalculator/* calculate结果是keyedRules中的key */> argsRule; 	//参数级路由规则
	
	public static final String joinMethodSigs(String methodName, String[] paramTypeStrs) {
		StringBuilder sb = new StringBuilder(methodName);
		for (String type : paramTypeStrs) {
			sb.append(METHOD_SIGS_JOINT_MARK).append(type);
		}
		return sb.toString();
	}

	public static final String joinMethodSigs(Method m) {
		StringBuilder sb = new StringBuilder(m.getName());
		Class<?>[] paramTypes = m.getParameterTypes();
		for (Class<?> c : paramTypes) {
			sb.append(METHOD_SIGS_JOINT_MARK).append(c.getName());
		}
		return sb.toString();
	}
	
	public Map<? extends Object, ? extends List<String>> getKeyedRules() {
		return keyedRules;
	}

	public void setKeyedRules(Map<? extends Object, ? extends List<String>> keyedRules) {
		this.keyedRules = keyedRules;
	}

	public Object getInterfaceRule() {
		return interfaceRule;
	}

	public void setInterfaceRule(Object interfaceRule) {
		this.interfaceRule = interfaceRule;
	}

	public Map<M, Object> getMethodRule() {
		return methodRule;
	}

	public void setMethodRule(Map<M, Object> methodRule) {
		this.methodRule = methodRule;
	}

	public Map<M, Args2KeyCalculator> getArgsRule() {
		return argsRule;
	}

	public void setArgsRule(Map<M, Args2KeyCalculator> argsRule) {
		this.argsRule = argsRule;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()).append("{");
		sb.append("\nkeyedRules=").append(this.keyedRules);
		sb.append(",\ninterfaceRule=").append(this.interfaceRule);
		sb.append(",\nmethodRule=").append(this.methodRule);
		sb.append(",\nargsRule=").append(this.argsRule).append("\n}");
		return sb.toString();
	}
}
