/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.common;

import java.io.Serializable;

/**
 * HSF Response
 * @author sihai
 *
 */
public class HSFResponse implements Serializable {

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
    
    private transient HSFRequest request;
    
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

    public HSFRequest getRequest(){
    	return request;
    }
    
    public void setRequest(HSFRequest request){
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