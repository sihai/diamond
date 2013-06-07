/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.consumer.impl;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.consumer.ServiceConsumer;
import com.galaxy.hsf.consumer.factory.ServiceConsumerFactory;
import com.galaxy.hsf.service.ServiceInvoker;
import com.galaxy.hsf.service.ServiceSubscriber;

/**
 * 
 * @author sihai
 *
 */
public class DefaultServiceConsumerFactory implements ServiceConsumerFactory {

	@Override
	public ServiceConsumer newServiceConsumer(ServiceMetadata metadata, ServiceSubscriber subscriber, ServiceInvoker invoker) {
		DefaultServiceConsumer consumer = new DefaultServiceConsumer(metadata, subscriber, invoker);
		consumer.initialize();
		consumer.start();
		return consumer;
	}

}
