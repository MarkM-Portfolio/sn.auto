package test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import resources.Configuration;
import resources.XMLValidator;

public class ConfigurationTest {

	public String xmlRef = "ConnectionsXMLFiles/LotusConnections-config.xml";
	public String xmlChk = "ConnectionsXMLFiles/LotusConnections-configBAD.xml";
	public String threeOhFile = "C:\\Users\\IBM_ADMIN\\xmlmigration\\threeOhConfigXML\\profiles-config.xml";
	
	@Test
	public void testGuessMap() {

		Configuration config = new Configuration(xmlChk, xmlRef);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("sloc:serviceReference", "serviceName");
		map.put("language", "lang");
		map.put("attribute", "key");
		config.getRefMap();
		assertEquals(config.getRefMap(),map); 
	}
	
	@Ignore
	@Test
	public void testConfigure()
	{
		Configuration config = new Configuration(xmlChk, xmlRef, "console","short","");
		
		try {
			XMLValidator val = XMLValidator.configure(config);
			val.validateXML();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Ignore
	@Test
	public void testConfig2()
	{
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("sloc:serviceReference", "serviceName");
		Configuration config = new Configuration(xmlChk, xmlRef,"console" , "short", "", map2 );
		
		try {
			XMLValidator val = XMLValidator.configure(config);
			val.validateXML();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testConfigure3()
	{
		Map<String,String> map3 = new HashMap<String, String>();
		map3.put("attribute", null);
		map3.put("extensionAttribute", null);
		map3.put("editableAttribute", null);
		map3.put("action", "urlPattern");
		Configuration config = new Configuration(threeOhFile, threeOhFile, "console","short","", map3);
		
		try {
			XMLValidator val = XMLValidator.configure(config);
			val.validateXML();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testConfigure4()
	{
		Configuration config = new Configuration(threeOhFile, threeOhFile, "console","short","");
		
		try {
			XMLValidator val = XMLValidator.configure(config);
			val.validateXML();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	

}
