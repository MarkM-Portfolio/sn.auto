package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import resources.XMLAnalyzer;
import resources.XMLUtilities;

/**
 * 
 * This test class is primarily for testing 2 methods of 
 * XMLUtilities:
 * 
 * - applyStyleSheet
 * - validateXSD
 * 
 * These two methods are critical in transforming
 * an xml document into another (in this case,
 * from the 3.0 to 4.0 version)
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-05-30
 */
public class XMLUtilitiesTest {
	
	/**
	 * constants
	 */
	//lists to fill
	ArrayList<String> xmlxs_list = new ArrayList<String>();
	ArrayList<String> xml_list = new ArrayList<String>();
	ArrayList<String> xml2_list = new ArrayList<String>();
	ArrayList<String> xsd_list = new ArrayList<String>();
	ArrayList<String> xsl_list = new ArrayList<String>(); 
	ArrayList<String> config_list = new ArrayList<String>();
	
	//XMLFiles (input) 
	String bbook = "XMLFiles/badbook.xml";
	String gbook = "XMLFiles/goodbook.xml";
	String order = "XMLFiles/order.xml";
	String ship = "XMLFiles/ship.xml";
	String config = "LotusXML/config.xml";
	
	String img = "XMLFiles/images.xml";
	String impr = "XMLFiles/import.xml";
	String ps = "XMLFiles/persons.xml";
	
	//XMLFiles (expected output)
	String img2 = "XMLFiles/images2.xml";
	String impr2 = "XMLFiles/import2.xml";
	String ps2 = "XMLFiles/persons2.xml";
	String config2 = "LotusXML/config2.xml";
	
	//XSDFiles
	String bookxsd = "XSDFiles/book.xsd";
	String orderxsd = "XSDFiles/order.xsd";
	String shipxsd = "XSDFiles/ship.xsd";
	String configxsd = "LotusXSD/config.xsd";
	
	//XSLTFiles
	String imgxsl = "XSLTFiles/images.xsl";
	String imprxsl = "XSLTFiles/import.xsl";
	String psxsl = "XSLTFiles/persons.xsl";
	String configxsl = "LotusXSL/config.xsl";
	
	@Before
	public void initialize() {
		xmlxs_list.add(bbook);
		xmlxs_list.add(gbook);
		xmlxs_list.add(order);
		xmlxs_list.add(ship);
		
		xml_list.add(img);
		xml_list.add(impr);
		xml_list.add(ps);
		
		xml2_list.add(img2);
		xml2_list.add(impr2);
		xml2_list.add(ps2);
		
		xsd_list.add(bookxsd);
		xsd_list.add(bookxsd);
		xsd_list.add(orderxsd);
		xsd_list.add(shipxsd);
		
		xsl_list.add(imgxsl);
		xsl_list.add(imprxsl);
		xsl_list.add(psxsl);
		
		config_list.add(config);
		config_list.add(config2);
		config_list.add(configxsd);
		config_list.add(configxsl);
	}
	
	@Test
	public void applyStyleSheetTest() {
		for(int i = 0; i < xml_list.size(); i++) {
			String xml = xml_list.get(i);
			String xml2 = xml2_list.get(i);
			String xsl = xsl_list.get(i);
			
			Document ex_doc = XMLUtilities.getXMLDoc(xml2);
			XMLAnalyzer xa = new XMLAnalyzer(ex_doc);
			
			try {
				String new_str = XMLUtilities.applyStyleSheet(xml, xsl);
				Document new_doc = XMLUtilities.getXMLDoc(new_str);
				XMLAnalyzer new_xa = new XMLAnalyzer(new_doc);
				
				assertTrue(xml, xa.hasEqualTree(new_xa));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		Document ref_config = XMLUtilities.getXMLDoc(config_list.get(1));
		XMLAnalyzer refc = new XMLAnalyzer(ref_config);
		try {
			String con_str = XMLUtilities.applyStyleSheet(config_list.get(0), config_list.get(3));
			Document new_con = XMLUtilities.getXMLDoc(con_str);
			XMLAnalyzer xcon = new XMLAnalyzer(new_con);
			
			assertTrue(config_list.get(0), refc.hasEqualTree(xcon, true));
			
		}catch (Exception e) {
			
		};*/
	}
	
	@Test
	public void validateXSDTest() {
		for(int i = 0; i < xmlxs_list.size(); i++) {
			String xml = xmlxs_list.get(i);
			String xsd = xsd_list.get(i);
			
			try {
				XMLUtilities.validateXSD(xml, xsd);
			} catch (Exception e) {
				fail();
				e.printStackTrace();
			}
		}
		
		//testing actual config XSD
		try {
			XMLUtilities.validateXSD(config_list.get(0), config_list.get(3));
		} catch (Exception e) {
			//fail();
			e.printStackTrace();
		}
		
	}

}
