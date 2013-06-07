/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.integrated.test;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.galaxy.diamond.metadata.ServiceMetadata;
import com.galaxy.hsf.consumer.ServiceConsumer;
import com.galaxy.hsf.consumer.factory.ServiceConsumerFactory;
import com.galaxy.hsf.consumer.impl.DefaultServiceConsumerFactory;
import com.galaxy.hsf.provider.ServiceProvider;
import com.galaxy.hsf.provider.factory.ServiceProviderFactory;
import com.galaxy.hsf.provider.impl.DefaultServiceProviderFactory;
import com.galaxy.hsf.rpc.protocol.hsf.HSFRPCProtocol4Client;
import com.galaxy.hsf.rpc.protocol.hsf.HSFRPCProtocol4Server;
import com.galaxy.hsf.service.HSFServiceFactory;

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
		metadata.addExporteProtocol(HSFRPCProtocol4Server.PROTOCOL, new Properties());
		ServiceProviderFactory factory = new DefaultServiceProviderFactory();
		ServiceProvider provider = factory.newServiceProvider(metadata, new DemoServiceImpl());
		HSFServiceFactory.getHSFService().register(metadata, provider.getMethodInvoker());
		// Consumer
		ServiceMetadata cmetadata = new ServiceMetadata();
		metadata.setName("test");
		metadata.setInterfaceName(DemoService.class.getName());
		metadata.setVersion("0.0.1");
		metadata.addImportPortocol(HSFRPCProtocol4Client.PROTOCOL, new Properties());
		ServiceConsumerFactory cfactory = new DefaultServiceConsumerFactory();
		ServiceConsumer consumer = cfactory.newServiceConsumer(metadata, HSFServiceFactory.getHSFService(), HSFServiceFactory.getHSFService());
		DemoService service = (DemoService)consumer.getProxy();
		String result = service.service("World");
		if(!StringUtils.equals(result, "Hello World")) {
			throw new Exception("failed");
		}
		System.out.println(result);
	}

}

