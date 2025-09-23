package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import displayModes.StoreAsString;

import resources.XMLValidator;

public class XMLValidatorTest2 {

	static ArrayList<XMLValidator> validators;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		validators = XMLValidator.configure("./ValidationConfigXML/string.xml");
	}


	@Test
	public void testGetXMLtoCheck() {
		assertEquals(validators.get(0).getXMLtoCheck(), "ConnectionsXMLFiles/LotusConnections-configBAD.xml");
		assertEquals(validators.get(1).getXMLtoCheck(), "ConnectionsXMLFiles/LotusConnections-configBADtag.xml");
		assertEquals(validators.get(2).getXMLtoCheck(), "ConnectionsXMLFiles/LotusConnections-config.xml");
		assertEquals(validators.get(3).getXMLtoCheck(), "SampleXML/sample.xml");
		assertEquals(validators.get(4).getXMLtoCheck(), "SampleXML/sample2.xml");
	}

	@Test
	public void testGetXMLReference() {
		assertEquals(validators.get(0).getXMLReference(), "ConnectionsXMLFiles/LotusConnections-config.xml");
		assertEquals(validators.get(1).getXMLReference(), "ConnectionsXMLFiles/LotusConnections-config.xml");
		assertEquals(validators.get(2).getXMLReference(), "ConnectionsXMLFiles/LotusConnections-config.xml");
		assertEquals(validators.get(3).getXMLReference(), "SampleXML/sample.xml");
		assertEquals(validators.get(4).getXMLReference(), "SampleXML/sample.xml");
	}

	@Test
	public void testGetMultiples() {
		assertTrue(validators.get(0).getMultiples().containsKey("sloc:serviceReference"));
		assertTrue(validators.get(1).getMultiples().containsKey("sloc:serviceReference"));
		assertTrue(validators.get(2).getMultiples().containsKey("sloc:serviceReference"));
		assertTrue(validators.get(3).getMultiples().containsKey("person") );
		assertTrue(validators.get(4).getMultiples().containsKey("person") );
	}

	@Test
	public void testGetDisplayType() {
		assertEquals(validators.get(0).getDisplayType(), "string");
		assertEquals(validators.get(1).getDisplayType(), "string");
		assertEquals(validators.get(2).getDisplayType(), "string");
		assertEquals(validators.get(3).getDisplayType(), "string");
		assertEquals(validators.get(4).getDisplayType(), "string");

	}

	@Test
	public void testGetDisplayLength() {
		assertEquals(validators.get(0).getDisplayLength(), "short");
		assertEquals(validators.get(1).getDisplayLength(), "short");
		assertEquals(validators.get(2).getDisplayLength(), "short");
		assertEquals(validators.get(3).getDisplayLength(), "short");
		assertEquals(validators.get(4).getDisplayLength(), "short");
	}


	@Test
	public void testValidateXML() throws Exception {
		
		validators.get(0).validateXML();
		assertEquals(((StoreAsString)validators.get(0).getDisplayMode()).messages,"!WARNING! Attribute is not valid :favoriteClown\n!WARNING! Element is not valid :sloc:serviceReference\n!WARNING! Element is not valid :mistake\n");
		
		validators.get(1).validateXML();
		assertEquals(((StoreAsString)validators.get(1).getDisplayMode()).messages,"!WARNING! Element is not valid :mistake\n");
		
		validators.get(2).validateXML();
		assertEquals(((StoreAsString)validators.get(2).getDisplayMode()).messages,"");
		
		validators.get(3).validateXML();
		assertEquals(((StoreAsString)validators.get(3).getDisplayMode()).messages,"");
		
		validators.get(4).validateXML();
		assertEquals(((StoreAsString)validators.get(4).getDisplayMode()).messages,"!WARNING! Element is not valid :Address\n!WARNING! Element is not valid :Recipient\n!WARNING! Element is not valid :House\n!WARNING! Element is not valid :Street\n!WARNING! Element is not valid :Town\n!WARNING! Element is not valid :PostCode\n!WARNING! Element is not valid :Country\n");
		
	}


}
