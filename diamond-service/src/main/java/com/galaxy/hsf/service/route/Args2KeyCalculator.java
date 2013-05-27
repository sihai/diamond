/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

/**
 * 将具体的参数映射为一个key的接口
 * 
 * @author sihai
 *
 */
public interface Args2KeyCalculator {

	/**
	 * 
	 * @param args
	 * @return
	 */
	Object calculate(Object[] args);
}
