/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.network.waverider;

import com.galaxy.hsf.network.HSFNetworkServer;
import com.galaxy.hsf.network.HSFNetworkServer.NetworkRequestHandler;
import com.galaxy.hsf.network.factory.HSFNetworkServerFactory;
import com.galaxy.hsf.network.waverider.config.WaveriderConfig;

/**
 * 
 * @author sihai
 *
 */
public class HSFWaveriderNetworkServerFactory implements HSFNetworkServerFactory {

	@Override
	public HSFNetworkServer newNetworkServer(NetworkRequestHandler handler) {
		// TODO parse some config from config file
		WaveriderConfig config = new WaveriderConfig();
		config.setMode(WaveriderConfig.Mode.MASTER);
		HSFWaveriderServer server = new HSFWaveriderServer(config, handler);
		server.initialize();
		server.start();
		return server;
	}

}
