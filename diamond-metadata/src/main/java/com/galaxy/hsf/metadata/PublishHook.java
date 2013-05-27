/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.metadata;


/**
 * 服务发布扩展点
 * @author sihai
 *
 */
public interface PublishHook {

	/**
	 * 在发布ServiceMetadata之前调用
	 * @param metadata
	 */
	void before(ServiceMetadata metadata);
	
	/**
	 * 在发布ServiceMetadata之后调用
	 * @param metadata
	 */
	void after(ServiceMetadata metadata);
}