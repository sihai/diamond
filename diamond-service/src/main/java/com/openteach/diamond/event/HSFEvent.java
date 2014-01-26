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
 * 
 */
package com.openteach.diamond.event;

/**
 * 
 * @author sihai
 *
 */
public class HSFEvent {

	private EventType type;
	
	/**
	 * 参数data在以下事件中代表的参数如下：
	   <ul>
	     <li>1. SERVICE_ADDRESSES_ARRIVED情况下为List&lt;Object&gt;,其中object为String类型。</li>
	     <li>2. ALL_AVALIABLE_ADDRESSES情况下为List&lt;List&lt;String&gt;&gt;。</li>
	     <li>3. HSF_REAL_INVOKE_ADDRESS情况下为String。</li>
	     <li>4. HSF_SELECT_INVOKE_ADDRESS情况下为String。</li>
	     <li>5. HSF_BEFORE_REMOTE_INVOKE情况下为<tt>HSFRequest</tt></li>
	     <li>6. HSF_BEFORE_HANDLE_REQUEST情况下为HSFRequest</li>
	     <li>7. HSF_AFTER_HANDLE_REQUEST情况下为HSFResponse</tt></li>
	     <li>8. HSF_AFTER_REMOTE_INVOKE情况下为HSFRequest</tt></li>
	   </ul>
	 */
	private Object	data;
	
	public EventType getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

	public HSFEvent(EventType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	/**
	 * 事件类型
	 * @author sihai
	 *
	 */
	public static enum EventType {
		
		SERVICE_ADDRESS_CHANGED,
		SERVICE_ROUTE_CHANGED,
		SERVICE_WEIGHT_RULE_CHANGED;
	}
}
