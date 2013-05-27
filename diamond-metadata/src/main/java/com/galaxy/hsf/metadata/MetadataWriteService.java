/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.metadata;

import com.galaxy.hsf.common.lifecycle.LifeCycle;


/**
 * 描述：发布服务元数据的服务
 * 
 * @author sihai
 *
 */
public interface MetadataWriteService extends LifeCycle {

	/**
	 * 
	 * @param metadata
	 */
	void register(ServiceMetadata metadata);
	
	/**
	 * 
	 * @param metadata
	 */
	void unregister(ServiceMetadata metadata);

	/**
	 * 
	 * @param hook
	 */
	void addPublishHook(PublishHook hook);
}