/**
 * Copyright 2013 Qiangqiang RAO
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.galaxy.diamond.consumer.impl;

import com.galaxy.diamond.consumer.ServiceConsumer;
import com.galaxy.diamond.consumer.factory.ServiceConsumerFactory;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.service.ServiceInvoker;
import com.galaxy.diamond.service.ServiceSubscriber;

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
