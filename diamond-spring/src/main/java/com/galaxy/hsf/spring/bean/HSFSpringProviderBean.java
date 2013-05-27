/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.spring.bean;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.galaxy.hsf.common.HSFConstants;
import com.galaxy.hsf.common.callback.CallbackHolder;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.osgi.HSFActivator;
import com.galaxy.hsf.service.HSFServiceHolderComponent;
import com.galaxy.hsf.service.metadata.MethodSpecial;
import com.galaxy.hsf.service.metadata.Publisher;
import com.galaxy.hsf.service.metadata.ServiceMetadata;
import com.galaxy.hsf.util.ReflectionUtil;

/**
 * 
 * 提供给Spring使用的发布spring bean为HSF Service的bean
 *
 *       此类部署在HSF容器中，但需要提供给HSF之外的容器访问
 *       
 * @author sihai
 *
 */
public class HSFSpringProviderBean implements InitializingBean {

	private static final Log logger = LogFactory.getLog(HSFSpringProviderBean.class);
	
	private AtomicBoolean initialized = new AtomicBoolean(false);	// 
	
	private static volatile Publisher publisher;
	private static byte[]			  _lock_ = new byte[0];
	
	/**
	 * 服务metadata
	 */
	private ServiceMetadata metadata = new ServiceMetadata();
	
	public HSFSpringProviderBean() {
        metadata.setVersion(ServiceMetadata.DEFAULT_VERSION);
        metadata.setGroup(ServiceMetadata.DEFAULT_GROUP); 											// 服务所属的组别, 默认的组别名称为HSF
        metadata.setSupportAsyncall(false); 														// 默认不支持异步调用
        metadata.addProperty(HSFConstants.TIMEOUT_KEY, HSFConstants.DEFAULT_TIMEOUT); 				// 默认客户端调用超时时间：3s
        metadata.addProperty(HSFConstants.IDLE_TIMEOUT_KEY, HSFConstants.DEFAULT_IDLE_TIMEOUT); 	// 默认的客户端连接空闲超时时间：600秒
        metadata.addProperty(HSFConstants.SERIALIZE_TYPE_KEY, HSFConstants.DEFAULT_SERIALIZE_TYPE); // 序列化类型，默认为HESSIAN

        metadata.addProperty(HSFConstants.CLIENT_RETRY_CONNECTION_TIMES_KEY, HSFConstants.DEFAULT_CLIENT_RETRY_CONNECTION_TIMES);		// 
        metadata.addProperty(HSFConstants.CLIENT_RETRY_CONNECTION_TIMEOUT_KEY, HSFConstants.DEFAULT_CLIENT_RETRY_CONNECTION_TIMEOUT);	// 
    }
	
	public void setServiceGroup(String serviceGroup) {
        metadata.setGroup(serviceGroup);
    }

    public void setTarget(Object target) {
        metadata.setTarget(target);
    }

    public void setServiceInterface(String serviceInterface) {
        metadata.setInterfaceName(serviceInterface.trim());
    }

    public void setServiceVersion(String serviceVersion) {
        metadata.setVersion(serviceVersion);
    }

    public void setServiceName(String serviceName) {
        metadata.setName(serviceName);
    }

    public void setServiceDesc(String serviceDesc) {
        metadata.setDesc(serviceDesc);
    }

    public void setSupportAsynCall(boolean supportAsynCall) {
        metadata.setSupportAsyncall(supportAsynCall);
    }

    public void setClientTimeout(int clientTimeout) {
        metadata.addProperty(HSFConstants.TIMEOUT_KEY, clientTimeout);
    }

    public void setClientIdleTimeout(int clientIdleTimeout) {
        metadata.addProperty(HSFConstants.IDLE_TIMEOUT_KEY, clientIdleTimeout);
    }

    public void setSerializeType(String _serializeType) {
        metadata.addProperty(HSFConstants.SERIALIZE_TYPE_KEY, _serializeType);
    }
    
    public void setMethodToInjectConsumerIp(String methodName){
    	metadata.addProperty(ServiceMetadata.METHOD_TO_INJECT_CONSUMERIP_PROP_KEY, methodName);
    }

    public void setMethodSpecials(MethodSpecial[] methodSpecials) {
        metadata.setMethodSpecials(methodSpecials);
    }

    public void setCorePoolSize(int corePoolSize){
    	metadata.setCorePoolSize(corePoolSize);
    }
    
    public void setMaxPoolSize(int maxPoolSize){
    	metadata.setMaxPoolSize(maxPoolSize);
    }
    
    /**
     * 设置服务支持的RPC方式
     */
    public void setRPCProtocols(Map<String, Properties> protocols){
    	metadata.setExporters(protocols);
    }

    /**
     * 对外发布服务
     */
    public void init() throws HSFException {
    	
    	// 避免被初始化多次
    	if(!initialized.compareAndSet(false, true)) {
    		return;
    	}
    	
    	try {
    		//下载并安装需要的bundle
    		prepare();
    	} catch (InterruptedException e) {
    		Thread.currentThread().interrupt();
    		throw new HSFException(e);
    	}
    	
    	if(null != CallbackHolder.getCallback())
    		CallbackHolder.getCallback().init();
    	
    	Logger.init();
    	vaildateConfig();
    	
        try{
        	publisher.publish(metadata);
        	logger.warn("接口["+metadata.getInterfaceName()+"]版本["+metadata.getVersion()+"]发布为HSF服务成功！");
        } catch(HSFException e){
        	logger.error("接口["+metadata.getInterfaceName()+"]版本["+metadata.getVersion()+"]发布为HSF服务失败",e);
        	throw e;
        }
    }
    
    /**
     * 初始化下载需要的bundle,此时是否在bean的初始化阶段增加锁机制？
     * @throws HSFException, InterruptedException
     */
    
    private void prepare() throws HSFException, InterruptedException {
    	synchronized(_lock_) {
	    	if (null == publisher) {
	    		if(null != publisher) {
	    			publisher = HSFServiceHolderComponent.getPublisher();
	    		}
	        }
    	
	    	//支持异步调用时，需要下载notify及可靠异步bundle。
	    	if(metadata.isSupportAsyncall()){
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_INVOKE_RELIABLE_BUNDLE_NAME)){
	    			int currentSize =  publisher.getHooks().size();
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_INVOKE_RELIABLE_BUNDLE_NAME);
	    			HSFActivator.wait4HooksDS(currentSize);
	    		}
	    	
	    	}
	    	//支持webservice调用
	    	if(metadata.getExporters().containsKey(HSFConstants.HSF_WEBSERVICE_PROTOCOL)){
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_XFIRE_LIB_BUNDLE_NAME)){
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_XFIRE_LIB_BUNDLE_NAME);
	    		}
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_XFIRE_SERVICES_BUNDLE_NAME)){
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_XFIRE_SERVICES_BUNDLE_NAME);
	    		}
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_SERVICE_RPC_XFIRE_BUNDLE_NAME)){
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_SERVICE_RPC_XFIRE_BUNDLE_NAME);
	    			//check for ds inject
	    			HSFActivator.wait4RPCProtocolDS(HSFConstants.HSF_WEBSERVICE_PROTOCOL);
	    		}
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_SERVICE_XFIRE_INVOKE_SYNC_BUNDLE_NAME)){
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_SERVICE_XFIRE_INVOKE_SYNC_BUNDLE_NAME);
	    		}
	    		
	    	}
	    	
	    	if(metadata.getExporters().containsKey(HSFConstants.PB_PROTOCOL)){
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_SERVICE_PB_BUNDLE_NAME)){
	    			int currentSize =  publisher.getHooks().size();
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_SERVICE_PB_BUNDLE_NAME);
	    			HSFActivator.wait4HooksDS(currentSize);
	    			HSFActivator.wait4RPCProtocolDS(HSFConstants.PB_PROTOCOL);
	    		}
	    	}
    	}
	}
    
	/*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
    	init();
	}

    // 检查上层业务的配置
    private void vaildateConfig() {
        String serviceInterface = metadata.getInterfaceName();
        Object target = metadata.getTarget();
        String serializeType = (String)metadata.getProperty(HSFConstants.SERIALIZE_TYPE_KEY);

        StringBuilder errorMsg = new StringBuilder();

        if (null == target) {
            errorMsg.append("未配置需要发布为服务的Object，服务名为: ").append(metadata.getUniqueName());
            invalidDeclaration(errorMsg.toString());
        }

        if (!HSFConstants.HESSIAN_SERIALIZE.equals(serializeType)
            && !HSFConstants.JAVA_SERIALIZE.equals(serializeType)) {
            errorMsg.append("不可识别的序列化类型[").append(serializeType).append("].");
            invalidDeclaration(errorMsg.toString());
        }

        // 声明的接口类是否存在
        Class<?> interfaceClass = null;
        try {
            interfaceClass = Class.forName(serviceInterface);
        } catch (ClassNotFoundException cnfe) {
            errorMsg.append("ProviderBean中指定的接口类不存在[");
            errorMsg.append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }

        // 必须声明接口类型
        if (!interfaceClass.isInterface()) {
            errorMsg.append("ProviderBean中指定的服务类型不是接口[");
            errorMsg.append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }

        // 真实的服务是否实现了服务接口
        if (!interfaceClass.isAssignableFrom(target.getClass())) {
            errorMsg.append("真实的服务对象[").append(target);
            errorMsg.append("]没有实现指定接口[").append(serviceInterface).append("].");
            invalidDeclaration(errorMsg.toString());
        }

        // 接口方法的输入参数和返回类型
        for (Method serviceMethod : interfaceClass.getMethods()) {
            validateMethodParamAndReturnTypes(serviceMethod);
        }
        
        // 检查服务的发布方式，如未配置则为默认方式
        if(metadata.getExporters().size()==0){
        	metadata.addExporter("DEFAULT", new Properties());
        }
    }

    private void validateMethodParamAndReturnTypes(Method method) {
        final String methodName = method.getName();
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] paramTypes = method.getParameterTypes();
        String serviceInterface = metadata.getInterfaceName();

        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("接口[").append(serviceInterface).append("], ");
        errorMsg.append("方法[").append(methodName).append("], ");

        // 如果方法返回抽象类型，或具体但非Final的类型，日志提示
        if (ReflectionUtil.isAbstract(returnType)) {
            errorMsg.append("有抽象返回类型[").append(returnType.getName()).append(
                    "]. ");
            errorMsg.append("请不要返回客户端没有的实现类型.");
            //            LOGGER.warn(errorMsg.toString());
        } else if (!ReflectionUtil.isFinal(returnType)) {
            errorMsg.append("有非Final的具体返回类型[").append(returnType.getName())
                    .append("]. ");
            errorMsg.append("建议给该类增加Final修饰符.");
            //            LOGGER.warn(errorMsg.toString());
        }

        // 如果参数类型为抽象类型，或具体但非Final的类型，日志提示
        for (Class<?> paramType : paramTypes) {
            if (paramType.isArray()) {
                paramType = paramType.getComponentType();
            }

            if (ReflectionUtil.isAbstract(paramType)) {
                errorMsg.append("有抽象参数类型[").append(paramType.getName()).append(
                        "]. ");
                errorMsg.append("请确保服务端有实现类型.");
                //                LOGGER.warn(errorMsg.toString());
            } else if (!ReflectionUtil.isFinal(paramType)) {
                errorMsg.append("有非Final的具体参数类型[").append(paramType.getName())
                        .append("]. ");
                errorMsg.append("建议给该类增加Final修饰符.");
                //                LOGGER.warn(errorMsg.toString());
            }
        }
    }

    private void invalidDeclaration(String msg) {
        throw new IllegalArgumentException(msg);
    }
    
    public Map<String, Object> register(){
    	return HSFServiceHolderComponent.getMetadataService().register(metadata);
    }
    
    public Map<String, Object> unregister(){
    	return HSFServiceHolderComponent.getMetadataService().unregister(metadata);
    }
}
