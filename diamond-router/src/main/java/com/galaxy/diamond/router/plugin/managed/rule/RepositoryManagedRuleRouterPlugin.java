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
package com.galaxy.diamond.router.plugin.managed.rule;

import java.util.List;

import com.galaxy.diamond.address.Protocol;
import com.galaxy.diamond.address.ServiceAddress;
import com.galaxy.diamond.router.plugin.AbstractRouterPlugin;

/**
 * 
 * @author sihai
 *
 */
public class RepositoryManagedRuleRouterPlugin extends AbstractRouterPlugin {

	@Override
	public void changed(ServiceAddress serviceAddress) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> route(String serviceName, Protocol protocol) {
		// TODO Auto-generated method stub
		return null;
	}

}
