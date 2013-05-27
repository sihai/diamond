/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import com.galaxy.hsf.network.HSFNetworkClient;
import com.galaxy.hsf.network.factory.HSFNetworkClientFactory;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;

/**
 * 
 * @author sihai
 *
 */
public class HSFWaveriderNetworkClientFactory implements HSFNetworkClientFactory {

	@Override
	public HSFNetworkClient newNetworkClient(String serverIp, int serverPort) {
		// TODO parse some config from config file
		WaveriderConfig config = new WaveriderConfig();
		config.setMode(WaveriderConfig.Mode.SLAVE);
		config.setMasterAddress(serverIp);
		config.setPort(serverPort);
		HSFWaveriderClient client = new HSFWaveriderClient(config);
		client.initialize();
		client.start();
		return client;
	}

}
