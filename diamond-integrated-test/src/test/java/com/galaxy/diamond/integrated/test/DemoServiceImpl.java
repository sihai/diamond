/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.diamond.integrated.test;

/**
 * 
 * @author sihai
 *
 */
public class DemoServiceImpl implements DemoService {

	@Override
	public String service(String args) {
		return String.format("Hello %s", args);
	}

}
