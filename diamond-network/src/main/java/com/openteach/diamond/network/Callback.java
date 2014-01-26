/**
 * Copyright 2013 openteach
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
package com.openteach.diamond.network;

import com.openteach.diamond.network.exception.NetworkException;

/**
 * 
 * @author sihai
 *
 */
public interface Callback {

	/**
	 * 
	 * @param request
	 * @param response
	 */
	void succeed(Object request, Object response);
	
	/**
	 * 
	 * @param request
	 * @param exception
	 */
	void failed(Object request, NetworkException exception);
}
