/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import org.junit.Test;

import com.galaxy.hsf.common.exception.internal.RuleParseException;

/**
 * 
 * @author sihai
 *
 */
public class WeightRuleParserTest {

	static final String WRONG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
									"<service>" +
									"</service";
	
	static final String RIGHT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
									"<service>" +
										"<name>com.galaxy.hsf.TestService</name>" +
										"<ipweights>" +
											"<ipweight>" +
												"<ip>192.168.1.2</ip>" + 
												"<weight>1</ip>" + 
											"</ipweight>" +
											"<ipweight>" +
												"<ip>192.168.1.3</ip>" + 
												"<weight>2</ip>" + 
											"</ipweight>" +
											"<ipweight>" +
												"<ip>192.168.1.4</ip>" + 
												"<weight>3</ip>" + 
											"</ipweight>" +
										"</ipweights>"+
									"</service>";
	
	@Test(expected = RuleParseException.class)
	public void test_wrong_xml() throws Exception {
		WeightRuleParser parser = new WeightRuleParser();
		parser.parse(WRONG_XML);
	}
	
	@Test
	public void test_right_xml() {
		WeightRuleParser parser = new WeightRuleParser();
		try {
			WeightRule rule = parser.parse(RIGHT_XML);
		} catch (RuleParseException e) {
		}
	}
}
