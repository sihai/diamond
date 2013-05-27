/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.metadata;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ServiceMetadata implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8252203868359168229L;

	/**
	 * 默认的版本号
	 */
	public static final String DEFAULT_VERSION = "1.0.0";
	
	/**
	 * 默认的分组
	 */
	public static final String DEFAULT_GROUP = "HSF";
	
	/**
	 * 接口名
	 */
	private String interfaceName = "";
	
	/**
	 * 接口class
	 */
    private Class<?> interfaceClass = null;

	/**
     * 服务提供对象
     */
    private transient Object target;
    
    /**
     * 发布的服务的方法名称
     */
    private MethodSpecial[] methodSpecials;
    
    /**
     * 持有invokeContext的ThreadLocal
     * 任何时候不为空，便于后续流程直接使用
     */
    private ThreadLocal<Serializable> invokeContext = new ThreadLocal<Serializable>();
    
    /**
     * 服务分组
     */
    private String group = DEFAULT_GROUP;
    
	/**
     * 服务版本
     * 默认版本号是1.0.0
     */
	private String version = DEFAULT_VERSION;
    
	/**
     * 服务名称
     */
    private String name;
    
    /**
     * 服务描述
     */
    private String desc;
    
    /**
     * 是否支持异步调用, 默认不支持
     */
    private boolean isSupportAsyncall = false;
    
    /**
     * 回调处理器，每个服务方法对应的回调方法签名有如下约定：
     *     public void ${name}_callback(Object invokeContext, Object appResponse, Throwable t);
     * 回调发生时，HSF会在这个callbackHandler对象上调用约定的回调方法。
     * 具体描述参见HSFSpringConsumerBean
     */
    private Object callbackHandler;
    private String callbackMethodSuffix = "_callback";
    
	//==================================================================
    //		资源设置
    //==================================================================
    /**
     * 基于服务的线程最小数量
     */
    private int corePoolSize = 0;
    
    /**
     * 基于服务的线程最大数量
     */
    private int maxPoolSize = 0;
    
    //==================================================================
    //		属性
    //==================================================================
    private Properties serviceProperties = new Properties();
    static public final String METHOD_TO_INJECT_CONSUMERIP_PROP_KEY = "METHOD_TO_INJECT_CONSUMERIP_PROP_KEY";
    static public final String METHOD_TO_ATTACH_INVOKE_CONTEXT = "METHOD_TO_ATTACH_INVOKE_CONTEXT";
    static public final String CONSUMER_MAX_POOL_SIZE = "ConsumerMaxPoolSize";
    
    /**
     * 发布的方式，用来支持多种rpc协议
     * 
     * key为发布的RPC协议的关键字，统一为大写，例如HSF、HTTP和XFIRE
     * value为Properties，用于进行RPC协议的一些特殊配置
     */
    private Map<String, Properties> exportProtocols = new HashMap<String, Properties>();
    
    /**
     * 调用的方式，用来支持多种rpc协议
     * 
     * key为调用时采用的RPC协议的关键字，统一为大写，例如HSF、HTTP和XFIRE
     * value为Properties，用于进行RPC协议的一些特殊配置
     */
    private Map<String, Properties> importProtocols = new HashMap<String, Properties>();
    private Map<String, Properties> apps = new HashMap<String, Properties>();
    private Map<String, AsyncallMethod> asyncallMethods = new HashMap<String, AsyncallMethod>();
    private Map<String, MethodSpecial> methodSpecialMap = new HashMap<String, MethodSpecial>();
    
    public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
	public Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public MethodSpecial[] getMethodSpecials() {
		return methodSpecials;
	}

	public void setMethodSpecials(MethodSpecial[] methodSpecials) {
		this.methodSpecials = methodSpecials;
	}
	
	/**
     * 放置调用上下文的ThreadLocal对象。由业务在调用服务方法前，调用ThreadLocal.set()传入上下文，HSF在调用发生时取用
     * 传入的上下文对象只在ReliableCallback方式时使用
     * @return
     */
	public ThreadLocal<Serializable> getInvokeContext() {
		return invokeContext;
	}

	public void setInvokeContext(ThreadLocal<Serializable> invokeContext) {
		this.invokeContext = invokeContext;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
    
    public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
	
	public boolean isSupportAsyncall() {
		return isSupportAsyncall;
	}

	public void setSupportAsyncall(boolean isSupportAsyncall) {
		this.isSupportAsyncall = isSupportAsyncall;
	}
	
	/**
     * 获取服务属性
     */
    public Properties getServiceProperties() {
        return serviceProperties;
    }
    public Object getProperty(String key) {
        return serviceProperties.get(key);
    }
    public void addProperty(String key, Object value) {
    	serviceProperties.put(key, value);
    }
    
    /**
     * 获取服务的发布方式
     */
    public Map<String, Properties> getExportProtocols() {
        return exportProtocols;
    }
    public void setExportProtocols(Map<String, Properties> exportProtocols) {
        this.exportProtocols.putAll(exportProtocols);
    }
    public void addExporteProtocol(String protocol, Properties properties) {
        exportProtocols.put(protocol, properties);
    }
    /**
     * 获取是否为发布服务
     */
    public boolean isExportService() {
        return exportProtocols.size() > 0;
    }
    
    /**
     * 获取服务的调用方式
     */
    public Map<String, Properties> getImportProtocols() {
        return importProtocols;
    }
    public void setImportProtocols(Map<String, Properties> importProtocols) {
        this.importProtocols.putAll(importProtocols);
    }
    public void addImportPortocol(String protocol, Properties properties) {
        importProtocols.put(protocol, properties);
    }
    
    /**
     * 选择一种要订阅服务的RPC协议.<p>
     * 订阅服务时只能用一种RPC方式订阅, 当配置了多种订阅方式时需要从中选择一种.
     * 本方法提供了一种策略以从中选择一种.
     * 
     * @return 选择出来的RPC协议名
     */
    public String selectImporter() {
        // 目前的策略是: 直接选择第一个
        if (importProtocols.isEmpty()) {
            return "DEFAULT";
        }

        return importProtocols.keySet().iterator().next();
    }
    
    /**
     * 增加需要异步调用的方法<br>
     * method的格式为：name:方法名;type:调用方式;listener:回调listener类名<br>
     */
    public void addAsyncallMethod(AsyncallMethod asyncFuncDesc) {
        asyncallMethods.put(asyncFuncDesc.getName(), asyncFuncDesc);
    }
    
    public Object getCallbackHandler() {
		return callbackHandler;
	}

	public void setCallbackHandler(Object callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	public String getCallbackMethodSuffix() {
		return callbackMethodSuffix;
	}

	public void setCallbackMethodSuffix(String callbackMethodSuffix) {
		this.callbackMethodSuffix = callbackMethodSuffix;
	}
	
    /**
     * 
     * @return
     */
    public String getUniqueName() {
        return new StringBuilder(interfaceName).append(":").append(version).toString();
    }
    
    /**
     * 
     * @param protocol
     * @return
     */
    public String getUniqueNameWithProtocol(String protocol) {
        return String.format("%s://%s", protocol, getUniqueName());
    }
    
    /**
     * 获取异步调用方法的对象
     */
    public AsyncallMethod getAsyncallMethod(String method) {
        return asyncallMethods.get(method.toLowerCase());
    }

    /**
     * 将异步调用方法的对象信息转化为String
     */
    public String toAsyncallMethodString() {
        StringBuilder amethodString = new StringBuilder();
        for (AsyncallMethod amethod : asyncallMethods.values()) {
            amethodString.append(amethod.toString());
            amethodString.append("&");
        }
        return amethodString.toString();
    }

    /**
     * 判断是否存在持久的异步调用
     */
    public boolean isExistReliableCall() {
        for (AsyncallMethod amethod : asyncallMethods.values()) {
            if (amethod.isReliable()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isReliableCallback(){
        for (AsyncallMethod amethod : asyncallMethods.values()) {
            if (amethod.isReliableCallback()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isExistOnewayCall(){
    	 for (AsyncallMethod amethod : asyncallMethods.values()) {
             if (amethod.isOneway()) {
                 return true;
             }
         }
         return false;
    }
    
    public boolean isExistFutureCall(){
    	 for (AsyncallMethod amethod : asyncallMethods.values()) {
             if (amethod.isFuture()) {
                 return true;
             }
         }
         return false;
    }
    
    public boolean isExistCallbackCall(){
    	 for (AsyncallMethod amethod : asyncallMethods.values()) {
             if (amethod.isCallback()) {
                 return true;
             }
         }
         return false;
    }
    
    /**
     * 描述：异步调用的方法
     */
    public class AsyncallMethod implements Serializable {

        private static final long serialVersionUID = 1L;

        // 方法名
        private String name;
        private Method method;

        private Class<?> returnType;

        // 异步调用的类型
        private String type;

        // 回调的CALLBACK类名
        private String callback;
        private Object callbackInstance;
        
        /**
         * reliable时，是否需要可靠异步回调，关系到是否建立订阅notify回调消息
         */
        private boolean isReliableCallback;

        public String getName() {
            return name;
        }

		public String getType() {
			if(type != null){
				 return type.toUpperCase();
			}
           return null;
        }

        public void setName(String name) {
            this.name = name.toLowerCase();
        }

        public void setType(String type) {
            this.type = type;
        }

        /**
         * 是否为单向异步调用
         */
        public boolean isOneway() {
            return type == null || "".equals(type.trim())
                    || "oneway".equalsIgnoreCase(type);
        }

        /**
         * 是否为需要回调的异步调用
         */
        public boolean isCallback() {
            return "callback".equalsIgnoreCase(type);
        }

        /**
         * 是否为需要拿到future对象的异步调用
         */
        public boolean isFuture() {
            return "future".equalsIgnoreCase(type);
        }

        /**
         * 是否为可靠的单向异步调用，基于Notify
         */
        public boolean isReliable() {
            return "reliable".equalsIgnoreCase(type);
        }

        /**
         * reliable时，是否需要回调
         */
        public boolean isReliableCallback() {
			return this.isReliableCallback;
		}

        /**
         * 转化为可识别的String
         */
        public String toString() {
            StringBuilder strBuilder = new StringBuilder("name:");
            strBuilder.append(name);
            strBuilder.append(";type:");
            strBuilder.append(type);
            strBuilder.append(";listener:");
            strBuilder.append(callback);
            return strBuilder.toString();
        }

        public Class<?> getReturnType() {
            return returnType;
        }
        public void setReturnType(Class<?> returnType) {
            this.returnType = returnType;
        }

        public Method getMethod() {
            return method;
        }
        public void setMethod(Method method) {
            this.method = method;
        }

        public Object getCallbackInstance() {
            return callbackInstance;
        }
        public void setCallbackInstance(Object _callbackInstance) {
            callbackInstance = _callbackInstance;
        }

        public String getCallback() {
            return callback;
        }
        public void setCallback(String callback) {
            this.callback = callback;
        }

		public void setReliableCallback(boolean isReliableCallback) {
			this.isReliableCallback = isReliableCallback;
		}
    }
}