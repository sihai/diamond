/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.provider.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.metadata.ServiceMetadata;
import com.galaxy.hsf.provider.AbstractServiceProvider;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceProvider extends AbstractServiceProvider {
	
	/**
	 * 
	 */
	private Map<String, Method> methodMap;
	
	/**
	 * 
	 * @param metadata
	 */
	public DefaultServiceProvider(ServiceMetadata metadata) {
		super(metadata);
	}
	
	@Override
	public void initialize() {
		super.initialize();
		if(null == metadata) {
			throw new IllegalArgumentException("metadata must not be null");
		}
		if(null == metadata.getInterfaceName() || null == metadata.getTarget()) {
			throw new IllegalArgumentException("Please set targetInterfaceName and target");
		}
		try {
			metadata.setInterfaceClass(Class.forName(metadata.getInterfaceName()));
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.format("Can not load interface:%s", metadata.getInterfaceName()), e);
		}
		
		if(!metadata.getInterfaceClass().isInstance(metadata.getTarget())) {
			throw new IllegalArgumentException(String.format("Target must implements interface:%s", metadata.getInterfaceName()));
		}
		Method[] methods = metadata.getInterfaceClass().getDeclaredMethods();
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
			return method.invoke(metadata.getTarget(), args);
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
}
