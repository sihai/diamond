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
package com.openteach.diamond.integrated.test;

import java.util.Collections;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.openteach.diamond.consumer.ServiceConsumer;
import com.openteach.diamond.consumer.factory.ServiceConsumerFactory;
import com.openteach.diamond.consumer.impl.DefaultServiceConsumerFactory;
import com.openteach.diamond.metadata.ServiceMetadata;
import com.openteach.diamond.provider.ServiceProvider;
import com.openteach.diamond.provider.factory.ServiceProviderFactory;
import com.openteach.diamond.provider.impl.DefaultServiceProviderFactory;
import com.openteach.diamond.rpc.protocol.diamond.DefaultRPCProtocol4Client;
import com.openteach.diamond.rpc.protocol.diamond.DefaultRPCProtocol4Server;
import com.openteach.diamond.service.DiamondServiceFactory;

/**
 * 
 * @author sihai
 *
 */
public class DemoTest {

	@Test
	public void test() throws Exception {
		// Provider
		ServiceMetadata metadata = new ServiceMetadata();
		metadata.setName("test");
		metadata.setInterfaceName(DemoService.class.getName());
		metadata.setVersion("0.0.1");
		metadata.exportProtocol(DefaultRPCProtocol4Server.PROTOCOL, Collections.EMPTY_MAP);
		ServiceProviderFactory factory = new DefaultServiceProviderFactory();
		ServiceProvider provider = factory.newServiceProvider(metadata, new DemoServiceImpl());
		DiamondServiceFactory.getDiamondService().register(metadata, provider.getMethodInvoker());
		// Consumer
		ServiceMetadata cmetadata = new ServiceMetadata();
		cmetadata.setName("test");
		cmetadata.setInterfaceName(DemoService.class.getName());
		cmetadata.setVersion("0.0.1");
		cmetadata.addImportPortocol(DefaultRPCProtocol4Client.PROTOCOL, new Properties());
		ServiceConsumerFactory cfactory = new DefaultServiceConsumerFactory();
		ServiceConsumer consumer = cfactory.newServiceConsumer(cmetadata, DiamondServiceFactory.getDiamondService(), DiamondServiceFactory.getDiamondService());
		DemoService service = (DemoService)consumer.getProxy();
		String result = service.service("World");
		if(!StringUtils.equals(result, "Hello World")) {
			throw new Exception("failed");
		}
		System.out.println(result);
		
		for(int i = 0; i < 10000; i++) {
			System.out.println(service.service(String.format("%d", i)));
		}
	}

}

