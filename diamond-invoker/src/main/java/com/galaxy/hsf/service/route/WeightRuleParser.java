/**
 * High-Speed Service Framework (HSF)
 * 
 */
package com.galaxy.hsf.service.route;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.galaxy.hsf.common.exception.internal.RuleParseException;

/**
 * 
 * @author sihai
 *
 */
public class WeightRuleParser {

	private static final String SERVICE = "service";
	
	private static final String NAME = "name";
	
	private static final String IPWEIGHTS = "ipweights";
	
	private static final String IP = "ip";
	
	private static final String WEIGHT = "weight";
	
	private DocumentBuilder db = null;
	
	/**
	 * 
	 */
	public WeightRuleParser() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			 db = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("OOPS: crete document builder failed", e);
		}
	}
	
	/**
	 * 解析 config发送下来的权重规则
	 * @param serviceUniqueName 服务名
	 * @param rule 规则格式: 
	 *   	<service>
	 *   		<name>com.*</name>
	 *   		<ipweights>
	 *   			<ipweight>
	 *   				<ip></ip>
	 *   				<weight></weight>
	 *   			</ipweight>
	 *   		</ipweights>
	 *   	</service>
	 *  XXX TODO throw exception
	 * @return
	 * @throws RuleParseException
	 */
	public WeightRule parse(String rule) throws RuleParseException {
		
		if(StringUtils.isBlank(rule)) {
			// allowed empty weight rule of service
			return null;
		}
		
		InputStream stream = null;
		try {
			Document doc = null;
			try {
				stream = new ByteArrayInputStream(rule.getBytes());
				doc = db.parse(stream);
			} catch (SAXException e) {
				throw new RuleParseException(String.format("Parse rule failed, wrong xml format, rule:%s", rule));
			} catch (IOException e) {
				throw new RuleParseException("OMG, Not possible", e);
			}
			
			WeightRule w = new WeightRule();
			Element root = doc.getDocumentElement();
			NodeList nameNodeList = root.getElementsByTagName(NAME);
			if(null == nameNodeList || 1 != nameNodeList.getLength()) {
				throw new RuleParseException(String.format("Parse rule failed, rule must has only one <name> tag, rule:%s", rule));
			}
			// name
			Node nameNode = nameNodeList.item(0);
			w.setServiceName(StringUtils.trim(nameNode.getTextContent()));
			if(StringUtils.isBlank(w.getServiceName())) {
				throw new RuleParseException(String.format("Parse rule failed, <name> tag must not be empty, rule:%s", rule));
			}
			
			NodeList ipWeightsList = root.getElementsByTagName(IPWEIGHTS);
			if(null == ipWeightsList || ipWeightsList.getLength() != 1 ) {
				throw new RuleParseException(String.format("Parse rule failed, rule must has only one <ipweights> tag, rule:%s", rule));
			}
			
			// ipweights
			Node ipWeightsNode =  ipWeightsList.item(0);
			NodeList ipWeightNodeList  = ipWeightsNode.getChildNodes();
			if(null == ipWeightNodeList || 0 == ipWeightNodeList.getLength()) {
				throw new RuleParseException(String.format("Parse rule failed, tag <ipweights> must has one or more child tag <ipweight>, rule:%s", rule));
			}
			
			for(int i = 0; i < ipWeightNodeList.getLength(); i++) {
				String ip = null;
				String weight = null;
				Node ipWeightNode  = ipWeightNodeList.item(i);
				NodeList ipAndWeightList = ipWeightNode.getChildNodes();
				if(null == ipAndWeightList || 2 != ipAndWeightList.getLength()) {
					throw new RuleParseException(String.format("Parse rule failed, tag <ipweight> must has only one child tag <ip> and only one <weight>, rule:%s", rule));
				}
				for(int k = 0; k < ipAndWeightList.getLength(); k++) {
					Node ipAndWeightNode  = ipAndWeightList.item(k);
					if(IP.equalsIgnoreCase(ipAndWeightNode.getNodeName())) {
						ip = ipAndWeightNode.getTextContent();
					} else if(WEIGHT.equalsIgnoreCase(ipAndWeightNode.getNodeName())){
						weight = ipAndWeightNode.getTextContent();
					}
				}
				if(StringUtils.isBlank(ip) || StringUtils.isBlank(weight)) {
					throw new RuleParseException(String.format("Parse rule failed, tag <ipweight> must has only one child tag <ip> and only one <weight>, rule:%s", rule));
				}
				
				try {
					Integer tmp = Integer.valueOf(weight);
					if(tmp <= 0) {
						throw new RuleParseException(String.format("Parse rule failed, weight must be big then 0 integer, rule:%s", rule));
					}
					w.addIPAndWeight(ip, tmp);
				} catch (NumberFormatException e) {
					throw new RuleParseException(String.format("Parse rule failed, weight must be big then 0 integer, rule:%s", rule));
				}
			}
			return w;
		} finally {
			if(stream != null){
				try {
					stream.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
	}
}
