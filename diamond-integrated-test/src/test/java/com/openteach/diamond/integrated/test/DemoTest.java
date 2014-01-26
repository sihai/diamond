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
package com.galaxy.diamond.integrated.test;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.galaxy.diamond.consumer.ServiceConsumer;
import com.galaxy.diamond.consumer.factory.ServiceConsumerFactory;
import com.galaxy.diamond.consumer.impl.DefaultServiceConsumerFactory;
import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.diamond.provider.ServiceProvider;
import com.galaxy.diamond.provider.factory.ServiceProviderFactory;
import com.galaxy.diamond.provider.impl.DefaultServiceProviderFactory;
import com.galaxy.diamond.rpc.protocol.diamond.DefaultRPCProtocol4Client;
import com.galaxy.diamond.rpc.protocol.diamond.DefaultRPCProtocol4Server;
import com.galaxy.diamond.service.DiamondServiceFactory;

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
		metadata.addExporteProtocol(DefaultRPCProtocol4Server.PROTOCOL, new Properties());
		ServiceProviderFactory factory = new DefaultServiceProviderFactory();
		ServiceProvider provider = factory.newServiceProvider(metadata, new DemoServiceImpl());
		DiamondServiceFactory.getHSFService().register(metadata, provider.getMethodInvoker());
		// Consumer
		ServiceMetadata cmetadata = new ServiceMetadata();
		cmetadata.setName("test");
		cmetadata.setInterfaceName(DemoService.class.getName());
		cmetadata.setVersion("0.0.1");
		cmetadata.addImportPortocol(DefaultRPCProtocol4Client.PROTOCOL, new Properties());
		ServiceConsumerFactory cfactory = new DefaultServiceConsumerFactory();
		ServiceConsumer consumer = cfactory.newServiceConsumer(cmetadata, DiamondServiceFactory.getHSFService(), DiamondServiceFactory.getHSFService());
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

