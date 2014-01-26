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
package com.openteach.diamond.repository.client;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Key of data
 * @author sihai
 *
 */
public class Key implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1171314496745268310L;
	
	public static final String NONE_SUB_KEY = "_#none_sub_key_#_";
	public static final long INIT_SEQUENCE = -1L;
	
	/**
	 * 
	 */
	private String namespace;
	
	/**
	 * 
	 */
	private String key;
	
	/**
	 * 
	 */
	private String subKey = NONE_SUB_KEY;
	
	/**
	 * 
	 */
	private long sequence = 0;
	
	
	public Key() {}
	
	public Key(String namespace, String key) {
		this(namespace, key, NONE_SUB_KEY);
	}
	
	public Key(String namespace, String key, String subKey) {
		this(namespace, key, NONE_SUB_KEY, INIT_SEQUENCE);
	}
	
	public Key(String namespace, String key, long sequence) {
		this(namespace, key, NONE_SUB_KEY, sequence);
	}
	
	public Key(String namespace, String key, String subKey, long sequence) {
		this.namespace = namespace;
		this.key = key;
		this.subKey = subKey;
		this.sequence = sequence;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getSubKey() {
		return subKey;
	}

	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}
	
	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	
	public void incSequence() {
		this.sequence += 1;
	}
	
	@Override
	public int hashCode() {
		int code = 32 * namespace.hashCode() + 1;
		code += 32 * key.hashCode();
		if(null != subKey) {
			code += 32 * subKey.hashCode();
		}
		code += sequence;
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) {
			return true;
		}
		
		if(null == obj) {
			return false;
		}
		
		return StringUtils.equals(namespace, ((Key)obj).namespace) && StringUtils.equals(key, ((Key)obj).key) && StringUtils.equals(subKey, ((Key)obj).subKey) && sequence == ((Key)obj).sequence;
	}
	
	@Override
	public String toString() {
		return String.format("%s:%s:%s:%d", namespace, key, subKey, sequence);
	}
}
