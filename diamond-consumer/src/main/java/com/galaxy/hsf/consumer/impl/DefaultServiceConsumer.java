/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.consumer.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.consumer.AbstractServiceConsumer;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceConsumer extends AbstractServiceConsumer {

	/**
	 * 
	 */
	private String targetInterfaceName;
	
	/**
	 * 
	 */
	private Class targetInferface;
	
	/**
	 * 
	 */
	private Object target;
	
	/**
	 * 
	 */
	private Map<String, Method> methodMap;
	
	@Override
	public void initialize() {
		super.initialize();
		if(null == targetInterfaceName || null == target) {
			throw new IllegalArgumentException("Please set targetInterfaceName and target");
		}
		try {
			targetInferface = Class.forName(targetInterfaceName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.format("Can not load interface:%s", targetInterfaceName), e);
		}
		
		if(!targetInferface.isInstance(target)) {
			throw new IllegalArgumentException(String.format("Target must implements interface:%s", targetInterfaceName));
		}
		Method[] methods = targetInferface.getDeclaredMethods();
		methodMap = new HashMap<String, Method>(methods.length);
		for(Method m : methods) {
			methodMap.put(getKey(m), m);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		methodMap.clear();
	}

	@Override
	public Object invoke(String methodName, String[] parameterTypes, Object... args) throws HSFException {
		String key = getKey(methodName, parameterTypes);
		Method method = methodMap.get(key);
		if(null == method) {
			throw new HSFException(String.format("Can not found method:%s, key:%s", methodName, key));
		}
		try {
			return method.invoke(target, args);
		} catch (IllegalArgumentException e) {
			throw new HSFException(e);
		} catch (IllegalAccessException e) {
			throw new HSFException(e);
		} catch (InvocationTargetException e) {
			throw new HSFException(e);
		}
	}
	
	/**
	 * 
	 * @param method
	 * @return
	 */
	private String getKey(Method method) {
		Class[] cs = method.getParameterTypes();
		String[] names = new String[cs.length + 1];
		names[0] = method.getName();
		for(int i = 0; i < cs.length; i++) {
			names[i + 1] = cs[i].getName();
		}
		return StringUtils.join(names, "-");
	}
	
	/**
	 * 
	 * @param methodName
	 * @param parameterTypes
	 */
	private String getKey(String methodName, String[] parameterTypes) {
		return String.format("%s-%s", methodName, StringUtils.join(parameterTypes, "-"));
	}

	public void setTargetInterfaceName(String targetInterfaceName) {
		this.targetInterfaceName = targetInterfaceName;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
}
