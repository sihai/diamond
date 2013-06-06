/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.component;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.service.ConsumerHook;
import com.galaxy.hsf.service.RPCProtocolTemplateHolder;
import com.galaxy.hsf.service.metadata.Consumer;
import com.galaxy.hsf.service.metadata.MetadataService;
import com.galaxy.hsf.service.metadata.ServiceMetadata;

/**
 * 
 * @author sihai
 *
 */
public class ConsumerComponent implements Consumer {

	private static Log logger = LogFactory.getLog(Logger.LOGGER_NAME);
	
	private MetadataService metadataService = null;
	
	private List<ConsumerHook> hookList = new ArrayList<ConsumerHook>();
	
	public void setMetadataService(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public void unsetMetadataService(MetadataService metadataService) {
		if (this.metadataService == metadataService) {
			this.metadataService = null;
		}
	}
	
	@Override
	public Object consume(ServiceMetadata metadata) throws HSFException {
		for (ConsumerHook hook : hookList) {
			hook.before(metadata);
        }
        if(metadataService == null){
            throw new HSFException("订阅HSF服务时必须依赖的Metadataservice不存在！");
        }
        // 生成调用远程HSF服务的代理
        Class<?> interfaceClass = null;
        try {
            interfaceClass = Class.forName(metadata.getInterfaceName());
        } catch (ClassNotFoundException e) {
            throw new HSFException("无法加载HSF服务接口类，请确定此类是否存在：" + metadata.getInterfaceName());
        }
        InvocationHandler handler = new HSFServiceProxy(metadata);
        Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{interfaceClass}, handler);

        // 订阅服务信息
        metadataService.addressing(metadata);

        for (ConsumerHook hook : hookList) {
			hook.after(metadata);
        }
        return proxy;
	}

	@Override
	public List<ConsumerHook> getHooks() {
		return hookList;
	}
	
	public void addConsumerHook(ConsumerHook hook){
		hookList.add(hook);
    }

    public void unsetConsumerHook(ConsumerHook hook){
    	hookList.remove(hook);
    }

	//=======================================================================
	//							HSF Proxy
	//=======================================================================
	class HSFServiceProxy implements InvocationHandler {

		private static final String TOSTRING_METHOD = "toString";

        private ServiceMetadata serviceConsumerMetadata;
        private String method2AttachInvokeContext;
        private final String rpcType;
        private AtomicInteger maxPoolSize = null;

        public HSFServiceProxy(ServiceMetadata metadata){
            serviceConsumerMetadata = metadata;
            method2AttachInvokeContext = (String)serviceConsumerMetadata.getProperty(ServiceMetadata.METHOD_TO_ATTACH_INVOKE_CONTEXT);
            rpcType = serviceConsumerMetadata.getImportProtocols().keySet().iterator().next().toUpperCase();
            Integer tmp = (Integer)serviceConsumerMetadata.getProperty(ServiceMetadata.CONSUMER_MAX_POOL_SIZE);
            if(null != tmp) {
            	maxPoolSize = new AtomicInteger(tmp);
            }
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (TOSTRING_METHOD.equalsIgnoreCase(method.getName())) {
                return proxy.getClass().getName();
            }
            if(method.getName().equals(method2AttachInvokeContext) && args.length==1){
            	if(args[0] instanceof Serializable){
            		serviceConsumerMetadata.getInvokeContext().set((Serializable)args[0]);
            	}
            	return null;
            }
            if(maxPoolSize == null) {
            	return RPCProtocolTemplateHolder.get().invokeWithMethodObject(rpcType,serviceConsumerMetadata, method, args);
            } else {
            	int currentSize = maxPoolSize.decrementAndGet();
            	try {
	            	if(currentSize < 0) {
	            		logger.error("消费端线程池已满");
	            		throw new HSFException("消费端线程池已满");
	            	} else {
	            		return RPCProtocolTemplateHolder.get().invokeWithMethodObject(rpcType,serviceConsumerMetadata, method, args);
	            	}
            	} finally {
            		maxPoolSize.incrementAndGet();
            	}
            }
            
        }

    }
}