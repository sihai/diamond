/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.rpc;

import com.galaxy.hsf.common.HSFRequest;
import com.galaxy.hsf.common.exception.HSFException;
import com.galaxy.hsf.service.metadata.ServiceMetadata;

/**
 * 描述：具体的RPC协议的实现
 * 
 * @author sihai
 *
 */
public interface RPCProtocol {

	/**
	 * 注册服务提供者
	 * 
	 * @param metadata 服务元信息
	 * @throws HSFException 抛出异常
	 */
	void registerProvider(ServiceMetadata metadata) throws HSFException;
	
	/**
	 * 调用是否需要目标地址
	 * 
	 * @param metadata
	 * @param request
	 * @return 是否需要目标地址
	 */
	boolean isNeedTarget(ServiceMetadata metadata, HSFRequest request);
	
	/**
	 * 校验目标地址的可用性
	 * 
	 * @param targetURI
	 * @param metadata
	 * @return 目标地址是否可用，如不可用，外部将自动进行其他的选址动作
	 */
	boolean validateTarget(String targetURI, ServiceMetadata metadata);
	
	/**
	 * 具体的远程调用，已提供目标地址
	 * @param request
	 * @param metadata
	 * @param targetURI
	 * 
	 * @return Object
	 * @throws HSFException
	 */
	Object invoke(HSFRequest request, ServiceMetadata metadata, String targetURI) throws HSFException;
	
	/**
	 * RPC协议的类型，统一大写
	 * 
	 * @return String
	 */
	String getProtocol();
	
	/**
	 * RPC 协议端口
	 * @return int
	 */
	int getPublishPort();
}
