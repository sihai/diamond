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
 * 
 */
package com.openteach.diamond.service.route;

import org.junit.Test;

import com.openteach.diamond.common.exception.internal.RuleParseException;
import com.openteach.diamond.service.route.WeightRule;
import com.openteach.diamond.service.route.WeightRuleParser;

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
