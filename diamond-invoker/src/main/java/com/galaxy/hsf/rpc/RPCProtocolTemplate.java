/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc;

import java.lang.reflect.Method;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.service.metadata.ServiceMetadata;

/**
 * 
 * @author sihai
 *
 */
public interface RPCProtocolTemplate {

	/**
	 * 注册服务提供者
	 * 
	 * @param protocol RPC协议
	 * @param metadata 服务元信息
	 * @throws HSFException 抛出异常
	 */
	void registerProvider(String protocol, ServiceMetadata metadata) throws HSFException;
	
	/**
	 * 调用是否需要目标地址
	 * 
	 * @param protocol RPC协议
	 * @param metadata
	 * @param request
	 * @return 是否需要目标地址
	 */
	boolean isNeedTarget(String protocol, ServiceMetadata metadata, HSFRequest request);
	
	/**
	 * 校验目标地址的可用性
	 * 
	 * @param protocol RPC协议
	 * @param targetURI
	 * @param metadata 校验URL有时候需要传入metadata信息
	 * @return 目标地址是否可用，如不可用，外部将自动进行其他的选址动作
	 */
	boolean validTarget(String protocol, String targetURI, ServiceMetadata metadata);
	
	/**
     * 基于反射方式调用HSF服务
     *
     * @param protocol RPC协议
     * @param metadata 服务元信息对象
     * @param method 需要调用的服务的方法对象
     * @param args 调用方法的参数
     * @return Object 远程HSF服务执行后的响应对象
     * @throws HSFException 调用远程服务时出现超时、网络、业务异常时抛出
     * @throws Exception 业务异常
     */
	Object invokeWithMethodObject(String protocol, ServiceMetadata metadata, Method method, Object[] args) throws HSFException;
    
    /**
     * 基于非反射方式调用HSF服务
     *
     * @param protocol RPC协议
     * @param metadata 服务元信息对象
     * @param methodName 需要调用的方法名
     * @param parameterTypes 方法参数类型
     * @param args 调用方法的参数
     * @return Object 远程HSF服务执行后的响应对象
     * @throws HSFException 调用远程服务时出现超时、网络、业务异常时抛出
     * @throws Exception 业务异常
     */
    Object invokeWithMethodInfos(String protocol, ServiceMetadata metadata, String methodName, String[] parameterTypes, Object[] args) throws HSFException;
    
    /**
     * 根据协议返回相应的RPCProtocol
     * @param protocol
     * @return RPCProtocolService
     */
    RPCProtocol getRPCProtocol(String protocol) ;
}
