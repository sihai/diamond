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
package com.galaxy.diamond.network.waverider.session;

/**
 * 
 * @author raoqiang
 *
 */
public enum SessionStateEnum {
	WAVERIDER_SESSION_STATE_FREE(-1, "free"),
	WAVERIDER_SESSION_STATE_ALIVE(0, "alive"),
	WAVERIDER_SESSION_STATE_WAITING_0(1, "waiting_0"),
	WAVERIDER_SESSION_STATE_WAITING_1(2, "waiting_1"),
	WAVERIDER_SESSION_STATE_WAITING_2(3, "waiting_2"),
	WAVERIDER_SESSION_STATE_DEAD(4, "dead");
	
	private int value;
	private String desc;
	
	private SessionStateEnum(int value, String desc) {
		this.value = value;
		this.desc = desc;
	}
	
	public int value() {
		return this.value;
	}
	
	public String desc() {
		return this.desc;
	}
}
