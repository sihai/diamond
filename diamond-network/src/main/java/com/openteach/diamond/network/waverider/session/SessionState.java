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
package com.openteach.diamond.network.waverider.session;

import java.io.Serializable;

/**
 * 
 * @author raoqiang
 *
 */
public class SessionState implements Serializable{
	
	private static final long serialVersionUID = -4783160458658167854L;
	
	private String 	ip;							//
	private Long	priority;					// 转换成Master的优先级，越大越优先
	private Boolean isMasterCandidate;			// 是不是Master的候选者
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Long getPriority() {
		return priority;
	}
	public void setPriority(Long priority) {
		this.priority = priority;
	}
	public Boolean getIsMasterCandidate() {
		return isMasterCandidate;
	}
	public void setIsMasterCandidate(Boolean isMasterCandidate) {
		this.isMasterCandidate = isMasterCandidate;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ip:");
		sb.append(ip);
		sb.append(",priority:");
		sb.append(priority);
		sb.append(",isMasterCandidate:");
		sb.append(isMasterCandidate);
		sb.append("}");
		
		return sb.toString();
	}
}
