/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.consumer.factory;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.consumer.ServiceConsumer;
import com.galaxy.hsf.service.ServiceInvoker;
import com.galaxy.hsf.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public interface ServiceConsumerFactory {

	/**
	 * 
	 * @param metadata
	 * @param subscriber
	 * @param invoker
	 * @return
	 */
	ServiceConsumer newServiceConsumer(ServiceMetadata metadata, ServiceSubscriber subscriber, ServiceInvoker invoker);
}
