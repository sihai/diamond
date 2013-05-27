/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service;

import com.galaxy.hsf.service.metadata.ServiceMetadata;

/**
 * 服务消费扩展点
 * @author sihai
 *
 */
public interface ConsumerHook {

	/**
	 * 在消费ServiceMetadata之前调用
	 * @param metadata
	 */
	void before(ServiceMetadata metadata);
	
	/**
	 * 在消费ServiceMetadata之后调用
	 * @param metadata
	 */
	void after(ServiceMetadata metadata);
}