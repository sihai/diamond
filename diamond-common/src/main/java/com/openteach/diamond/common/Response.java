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
package com.galaxy.diamond.common;

import java.io.Serializable;

/**
 * HSF Response
 * @author sihai
 *
 */
public class Response implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2547794167307553869L;

	/**
	 * 这个字段表示HSF层是否成功。
     * 如果HSF成功，而上层业务失败，那么这个字段应该填true。
     */
	private boolean succeed;
	
	private String errorMsg;
	
	/**
	 * 
	 */
	private Throwable exception;

	// 业务层的返回值或抛出的异常
    private Object appResponse;
    
    private transient Request request;
    
    public Object getAppResponse() {
        return appResponse;
    }
    public void setAppResponse(Object response) {
        appResponse = response;
    }

    public boolean isSucceed() {
        return succeed;
    }
    
    public void succeed() {
        this.succeed = true;
    }
    
    public void failed() {
        this.succeed = false;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String error) {
        errorMsg = error;
    }
    
    public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}

    public Request getRequest(){
    	return request;
    }
    
    public void setRequest(Request request){
    	this.request = request;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HSFResponse[");
        sb.append("HSF是否成功=").append(succeed).append(", ");
        sb.append("HSF异常消息=").append(errorMsg).append(", ");
        sb.append("业务层响应=").append(appResponse).append("]");
        return sb.toString();
    }
}