/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author sihai
 *
 */
public final class ReflectionUtil {

	static private Map<String, Class<?>> primitiveTypes = new HashMap<String, Class<?>>();
    static private Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>();
    static {
        primitiveTypes.put("int", int.class);
        primitiveTypes.put("long", long.class);
        primitiveTypes.put("byte", byte.class);
        primitiveTypes.put("char", char.class);
        primitiveTypes.put("short", short.class);
        primitiveTypes.put("float", float.class);
        primitiveTypes.put("double", double.class);
        primitiveTypes.put("boolean", boolean.class);
        primitiveTypes.put("void", void.class);

        primitiveDefaults.put(int.class, 0);
        primitiveDefaults.put(long.class, 0L);
        primitiveDefaults.put(byte.class, (byte) 0);
        primitiveDefaults.put(char.class, (char) 0);
        primitiveDefaults.put(short.class, (short) 0);
        primitiveDefaults.put(float.class, (float) 0);
        primitiveDefaults.put(double.class, (double) 0);
        primitiveDefaults.put(boolean.class, false);
        primitiveDefaults.put(void.class, null);
    }
    
	/**
     * 判断一个类是否是接口或抽象类. 原子类型(byte,char,int,long,)返回false
     */
    static public boolean isAbstract(Class<?> clazz) {
        int mod = clazz.getModifiers();
        return !clazz.isPrimitive() && !clazz.isArray()
        && (Modifier.isInterface(mod) || Modifier.isAbstract(mod));
    }

    /**
     * 判断一个类是否有Final修饰. 原子类型(byte,char,int,long,)返回true
     */
    static public boolean isFinal(Class<?> clazz) {
        int mod = clazz.getModifiers();
        return Modifier.isFinal(mod);
    }

    static public Class<?> getPrimitiveType(String name) {
        return primitiveTypes.get(name);
    }

    static public Object defaultReturn(Method m) {
        if (m.getReturnType().isPrimitive()) {
            return primitiveDefaults.get(m.getReturnType());
        } else {
            return null;
        }
    }
}
