/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.spring.bean;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.galaxy.hsf.common.HSFConstants;
import com.galaxy.hsf.common.callback.CallbackHolder;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.common.log.Logger;
import com.galaxy.hsf.osgi.HSFActivator;
import com.galaxy.hsf.service.HSFServiceHolderComponent;
import com.galaxy.hsf.service.metadata.Consumer;
import com.galaxy.hsf.service.metadata.MethodSpecial;
import com.galaxy.hsf.service.metadata.ServiceMetadata;
import com.galaxy.hsf.service.metadata.ServiceMetadata.AsyncallMethod;

/**
 * 
 * @author sihai
 *
 */
public class HSFSpringConsumerBean implements FactoryBean, InitializingBean {

	private static final Log LOGGER = LogFactory.getLog(Logger.LOGGER_NAME);
	private static final String DEFAULT_IMPORTER = "default";

	private static volatile Consumer consumer;
	private static byte[]			 _lock_ = new byte[0];
	private final ServiceMetadata metadata = new ServiceMetadata();
	private AtomicBoolean initialized = new AtomicBoolean(false);
	
	public HSFSpringConsumerBean() {
        metadata.setGroup(ServiceMetadata.DEFAULT_GROUP); //服务所属的组别, 默认的组别名称为HSF
        metadata.setVersion(ServiceMetadata.DEFAULT_VERSION);
    }
	
	/**
     * 设置调用的服务的目标地址
     */
    public void setTarget(String target) {
        if ((target != null) && (!"".equals(target.trim()))) {
            // 将调用的目标地址放进去
            metadata.addProperty("target", target);
        }
    }

    /**
     * 设置接口名.如果该接口无法装载到，则抛出{@link IllegalArgumentException}
     */
    public void setInterfaceName(String interfaceName) {
        try {
            Class<?> clazz = Class.forName(interfaceName.trim());
            metadata.setIfClazz(clazz);
            metadata.setInterfaceName(interfaceName.trim());
        } catch (ClassNotFoundException cnfe) {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("ConsumerBean中指定的接口类不存在[");
            errorMsg.append(interfaceName).append("].");
            illArgsException(errorMsg.toString());
        }
    }

    /**
     * 设置调用的服务的版本
     */
    public void setVersion(String version) {
        metadata.setVersion(version);
    }

    /**
     * 设置调用的方式，支持多种rpc方式，但只能设置成一种
     * 
     * @param rpcProtocols
     */
    public void setRPCProtocols(Map<String, Properties> rpcProtocols){
    	metadata.setImporters(rpcProtocols);
    }

    /**
     * 设置需要异步调用的方法
     */
    public void setAsyncallMethods(List<String> asyncallMethods) throws Exception {
        for (String asyncFuncDesc : asyncallMethods) {
            parseAsyncFunc(asyncFuncDesc);
        }
    }

    public void setMethodSpecials(MethodSpecial[] methodSpecials) {
        metadata.setMethodSpecials(methodSpecials);
    }

    /**
     * 设置服务所属的组
     */
    public void setGroup(String group) {
        metadata.setGroup(group);
    }
    
    /**
	 * 在reliablecallback时，设置ConsumerBean的invokeContextThreadLocal属性，
	 * 该属性接受一个ThreadLocal<Serializable>对象
	 * 业务调用方法之前先调用这个ThreadLocal对象的set方法将想要传递的上下文传入
	 * 
	 * HSF通过这个ThreadLocal获得上下文并传递到服务端(通过HSFRequset增加byte[]类型的属性实现)
	 * 在服务端完成处理后，在回调客户端的callback方法时，将context传入
	 * 
	 * 不需要对服务接口有额外的约定。只需client在真正的服务调用前，
	 * 额外的做一次ThreadLocal.set(Serializable invokeContext)调用
	 * HSF在获得上下文后，会直接调用ThreadLocal.remove()清空这个线程变量
	 * 
	 * 如果interfaceMethodToAttachContext和invokeContextThreadLocal同时设置了，以最后一次额外调用为准
	 * 
	 * @param context 业务自定义的放置调用上下文的ThreadLocal对象
	 * @since 1.4.2
	 */
	public void setInvokeContextThreadLocal(ThreadLocal<Serializable> invokeContext) {
		this.metadata.setInvokeContext(invokeContext);
	}

	/**
	 * 在reliablecallback时，设置ConsumerBean的interfaceMethodToAttachContext属性，以便回调时得到调用时传入的上下文
	 * 该属性指定一个服务接口上的特定方法，用来在可靠异步回调的调用之初，传递调用上下文。
	 * 这个服务接口中的特定方法接受一个Serializable对象作为参数，在业务每次实际调用reliablecallback之前调用一次，
	 * 例如：
	 *    <bean id="xService" class="com.taobao.hsf.app.spring.util.HSFSpringConsumerBean" init-method="init">
	 *       <property name="interfaceName" value="com.xxx.XService" />
	 *       <property name="interfaceMethodToAttachContext" value="setContext" />
	 *       ...
	 *    </bean>
	 * 则接口XService中需要有如下方法定义：
	 *    void setContext(Serializable context);
	 * 业务调用方法之前先调用xService.setContext(theInvokeContext)方法，将上下文传入。再调用业务的接口方法
	 * HSF会将传入的context传送给服务端。--通过HSFRequset增加byte[]类型的属性实现
	 * 在服务端完成处理后，在回调客户端的callback方法时，将context传入
	 * 
	 * 如果interfaceMethodToAttachContext和invokeContextThreadLocal同时设置了，以最后一次额外调用为准
	 * 
	 * @param methodName 接口中特别用来每次调用其他方法前，调用其传入上下文的方法的方法名
	 */
	public void setInterfaceMethodToAttachContext(String methodName) {
		this.metadata.addProperty(ServiceMetadata.METHOD_TO_ATTACH_INVOKE_CONTEXT, methodName);
	}

	/**
	 * 设置回调处理器。回调发生时，HSF会在这个处理器对象上调用相应的约定的回调方法。
	 * 如果某个接口方法在asyncallMethods属性中配置了type是callback或reliable，则callback时会调用该对象的相应方法：
	 * 方法名为相应的接口方法名后缀_callback，参数为调用上下文、接口方法返回值和异常对象。
	 * 即一个异步接口方法在callbackHandler上对应的回调方法的签名有如下约定：
	 *     public void ${name}_callback(Object invokeContext, Object appResponse, Throwable t);
	 * 其中${name}为接口方法名，后缀"_callback"可以通过设置callbackMethodSuffix属性修改，默认为"_callback"
	 * appResponse为服务端执行接口方法后的返回值。t为抛出的异常。
	 * 正常调用成功appResponse为返回值，t为null；若有异常抛出，appResponse为null，t为服务端或客户端抛出的异常
	 * invokeContext为调用时传入的调用上下文，调用时传入，hsf不做修改，回调时原样传回。
	 * invokeContext传入的方式参见invokeContextThreadLocal和interfaceMethodToAttachContext属性
	 * 
	 * @param callbackHandler 用户自定义的回调处理器
	 * @since 1.4.2 这个配置优先于asyncallMethods上的listener, 同时listener配置已不推荐
	 */
	public void setCallbackHandler(Object callbackHandler) {
		this.metadata.setCallbackHandler(callbackHandler);
	}

	/**
	 * 默认为_callback, 含义参见callbackHandler
	 */
	public void setCallbackMethodSuffix(String suffix){
		this.metadata.setCallbackMethodSuffix(suffix);
	}
	
	/**
	 * 用户调用的线程通常希望控制调用的线程池大小，该属性用来设置用户调用的线程池大小。
	 * @param poolSize  
	 */
	
	public void setMaxThreadPool(String poolSize){
		this.metadata.addProperty(ServiceMetadata.CONSUMER_MAX_POOL_SIZE, poolSize);
	}
	
	/**
     * 初始化
     */
    public void init() throws HSFException {
    	// 避免被初始化多次
    	if(!initialized.compareAndSet(false, true)){
    		return;
    	}
    	
    	try {
    		prepare();
    	} catch (InterruptedException e) {
    		Thread.currentThread().interrupt();
    		throw new HSFException(e);
    	}
    	
    	verifyCallbackConfig();
    	if(CallbackHolder.getCallback()!=null)
    		CallbackHolder.getCallback().init();
    	Logger.init();
    	// 如未配置调用类型，则使用默认方式
    	if(metadata.getImporters().size()==0){
    		metadata.addImporter(DEFAULT_IMPORTER, new Properties());
    	}
        try{
            metadata.setTarget(consumer.consume(metadata));
            LOGGER.warn("成功生成对接口为["+metadata.getInterfaceName()+"]版本为["+metadata.getVersion()+"]的HSF服务调用的代理！");
        } catch(Exception e){
            LOGGER.error("生成对接口为["+metadata.getInterfaceName()+"]版本为["+metadata.getVersion()+"]的HSF服务调用的代理失败",e);
        }
    }
    /**
     * 初始化下载需要的bundle,此时是否在bean的初始化阶段增加锁机制？
     * @throws Exception
     */
    
    private void prepare() throws HSFException, InterruptedException {
    	synchronized(_lock_) {
	    	if (null == consumer) {
	    		if(null != consumer) {
	    			consumer = HSFServiceHolderComponent.getConsumer();
	    		}
	        }
	    	
	    	if(metadata.isExistOnewayCall() && !HSFActivator.isBundleExist(HSFConstants.HSF_INVOKE_ONEWAY_BUNDLE_NAME)){
	    		HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_INVOKE_ONEWAY_BUNDLE_NAME);
	    	}
	    	if(metadata.isExistFutureCall() && !HSFActivator.isBundleExist(HSFConstants.HSF_INVOKE_FUTURE_BUNDLE_NAME)){
	    		HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_INVOKE_FUTURE_BUNDLE_NAME);
	    	}
	    	if(metadata.isExistCallbackCall() && !HSFActivator.isBundleExist(HSFConstants.HSF_INVOKE_CALLBACK_BUNDLE_NAME)){
	    		HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_INVOKE_CALLBACK_BUNDLE_NAME);
	    	}
	    	if(metadata.isExistReliableCall() || metadata.isReliableCallback()){
	    		if(!HSFActivator.isBundleExist(HSFConstants.HSF_INVOKE_RELIABLE_BUNDLE_NAME)){
	    			int currentSize =  consumer.getHooks().size();
	    			HSFActivator.downloadAndInstallBundle(HSFConstants.HSF_INVOKE_RELIABLE_BUNDLE_NAME);
	    			HSFActivator.wait4HooksDS(currentSize);
	    		}
	    	
	    	}
	    	//支持webservice调用
	    	if(metadata.getImporters().containsKey(HSFConstants.HSF_WEBSERVICE_PROTOCOL)){
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
    	}
	}

	/*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
		init();
	}
    
    /**
     * @see FactoryBean#getObject()
     */
    public Object getObject() throws Exception {
        return metadata.getTarget();
    }

    /**
     * @see FactoryBean#getObjectType()
     */
    public Class<?> getObjectType() {
        // ? 这好像是spring的一个小bug，spring假设这个objectType是静态的，而不是根据注入的属性来决定<br>
        // 所以spring有可能会在属性注入前调用此方法，因此在此做了特殊处理
        if (metadata.getInterfaceName() == null) {
            return HSFSpringConsumerBean.class;
        }
        if (null == metadata.getIfClazz()) {
            return HSFSpringConsumerBean.class;
        } else {
            return metadata.getIfClazz();
        }
    }

    /**
     * @see FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    private void verifyCallbackConfig() throws IllegalArgumentException {
		//TODO 检查每个需要回调的方法，在callbackHandler上是否有对应的回调函数。
	}
	
	/**
	 * 解析异步方法描述 
	 */
	private void parseAsyncFunc(String desc) {
	    
	    // HSF V1.2.x的配置方式，即只支持ONEWAY方式
	    if (desc.indexOf(";") == -1) {
	    	LOGGER.warn("设置异步调用方式出错,字符串："+desc+" 中不存在 符合';' ,正确设置格式比如：name:methodName;type:one");
	        return;
	    }
	    AsyncallMethod amethod = metadata.new AsyncallMethod();
	    String[] methodDescs = desc.split(";");
	    for (String methodDesc : methodDescs) {
	        String[] methodDescProps = methodDesc.split(":");
	        String propName = methodDescProps[0].trim();
	        String propValue = methodDescProps[1].trim();
	        // 处理三种属性
	        if ("name".equalsIgnoreCase(propName)) {
	            amethod.setName(propValue);
	        } else if ("type".equalsIgnoreCase(propName)) {
	            amethod.setType(propValue);
	        } else if ("listener".equalsIgnoreCase(propName)) {
	            amethod.setCallback(propValue);
	            amethod.setType("callback");
	        } 
	        /*else if ("method".equalsIgnoreCase(propName)){
	        	amethod.setCallbackMethodName(propValue);
	        }*/
	    }
	    
	    if(amethod.getType() == null){
	    	LOGGER.warn("设置异步调用方式出错,字符串："+desc+" 中不存在 type属性 ,正确设置格式比如：name:methodName;type:one");
	        return;
	    }
	    /**
	     * 这里有点不自然。只是想reliablecallback和reliable用同样的处理逻辑，仅仅加了回调
	     * isReliableCallback用来决定需不需要回调，需要就订阅notify回调消息
	     */
	    if("reliablecallback".equalsIgnoreCase(amethod.getType())){
	    	amethod.setType("reliable");
	    	amethod.setReliableCallback(true);
	    }
	
	    if(amethod.isCallback() && amethod.getCallback()!=null){
	        //对于CALLBACK方式，如果设置了listener, 则创建CALLBACK实例
	        try {
	            Class<?> callbackClazz = Class.forName(amethod.getCallback());
	            amethod.setCallbackInstance(callbackClazz.newInstance());
	        } catch (Exception e) {
	            throw new RuntimeException("创建回调Listener类实例出现错误,需要创建的Listener的类名为："
	                    + amethod.getCallback(), e);
	        }
	    }
	
	    // 内省找到该异步方法
	    Method asyncFunc = null;
	    String asyncFuncName = amethod.getName();
	    Class<?> ifClazz = metadata.getIfClazz();
	    Method[] allFunc = ifClazz.getMethods();
	    for (int idx = 0; idx < allFunc.length && asyncFunc == null; idx++) {
	    	// 由于amethod.setName的地方会把name转为小写
	        if (allFunc[idx].getName().equalsIgnoreCase(asyncFuncName)) {
	            asyncFunc = allFunc[idx];
	            amethod.setMethod(asyncFunc);
	            metadata.addAsyncallMethod(amethod);
	        }
	    }
	    if (null == asyncFunc) {
	        throw new RuntimeException("没有找到类型[" + ifClazz.getName() + "上的方法[" + asyncFuncName + "].");
	    }
	    
	}
	
	private void illArgsException(String msg) {
        throw new IllegalArgumentException(msg);
    }
}
