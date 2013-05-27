/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.event;

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
