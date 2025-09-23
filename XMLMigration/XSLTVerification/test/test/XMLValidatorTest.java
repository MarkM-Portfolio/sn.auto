package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import displayModes.PrintToFile;

import resources.XMLValidator;

public class XMLValidatorTest {

	static ArrayList<XMLValidator> validators;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		validators = XMLValidator.configure("./ValidationConfigXML/ValidationConfig.xml");
	}

	@Test
	public void testGetDisplayMode() throws Exception {
		testValidateXML();
		assertTrue(validators.get(0).getDisplayMode() instanceof PrintToFile );
		assertTrue(validators.get(1).getDisplayMode() instanceof PrintToFile );
		assertTrue(validators.get(2).getDisplayMode() instanceof PrintToFile );
		assertTrue(validators.get(3).getDisplayMode() instanceof PrintToFile );
		assertTrue(validators.get(4).getDisplayMode() instanceof PrintToFile );
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
		assertEquals(validators.get(0).getDisplayType(), "file");
		assertEquals(validators.get(1).getDisplayType(), "file");
		assertEquals(validators.get(2).getDisplayType(), "file");
		assertEquals(validators.get(3).getDisplayType(), "file");
		assertEquals(validators.get(4).getDisplayType(), "file");

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
	public void testGetOutputFile() {
		assertEquals(validators.get(0).getOutputFile(), "shortFile.log");
		assertEquals(validators.get(1).getOutputFile(), "shortFile.log");
		assertEquals(validators.get(2).getOutputFile(), "shortFile.log");
		assertEquals(validators.get(3).getOutputFile(), "shortFile.log");
		assertEquals(validators.get(4).getOutputFile(), "shortFile.log");
	}

	public void testValidateXML() throws Exception {
		validators.get(0).validateXML();
		validators.get(1).validateXML();
		validators.get(2).validateXML();
		validators.get(3).validateXML();
		validators.get(4).validateXML();
	}


}
