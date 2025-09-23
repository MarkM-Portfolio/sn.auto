package tests;
import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;

import resources.XMLAnalyzer;
import resources.XMLTestFiles;


/**
 * 
 * Tests for XMLAnalyzer class
 * 
 * @author Eric Peterson, petersde@us.ibm.com
 * @version 1.6
 * @since 2012-05-21
 *
 */
public class XMLAnalyzerTest {
	
	/**
	 * constants
	 */
	
	//XMLAnalyzers
	private static final XMLAnalyzer ALPHA_TREE = new XMLAnalyzer(XMLTestFiles.getAlphaDoc());
	private static final XMLAnalyzer ALPHA_FAIL_TREE = new XMLAnalyzer(XMLTestFiles.getAlphaFailDoc());
	private static final XMLAnalyzer ALPHA2_TREE = new XMLAnalyzer(XMLTestFiles.getAlpha2Doc());
	private static final XMLAnalyzer BETA_TREE = new XMLAnalyzer(XMLTestFiles.getBetaDoc());
	private static final XMLAnalyzer BETA2_TREE = new XMLAnalyzer(XMLTestFiles.getBeta2Doc());
	private static final XMLAnalyzer CHARLIE_TREE = new XMLAnalyzer(XMLTestFiles.getCharlieDoc());
	private static final XMLAnalyzer DELTA_TREE = new XMLAnalyzer(XMLTestFiles.getDeltaDoc());
	private static final XMLAnalyzer DELTA2_TREE = new XMLAnalyzer(XMLTestFiles.getDelta2Doc());
	private static final XMLAnalyzer DELTA3_TREE = new XMLAnalyzer(XMLTestFiles.getDelta3Doc());
	private static final XMLAnalyzer DELTA_FAIL_TREE = new XMLAnalyzer(XMLTestFiles.getDeltaFailDoc());
	private static final XMLAnalyzer GAMMA_TREE = new XMLAnalyzer(XMLTestFiles.getGammaDoc());
	private static final XMLAnalyzer GAMMA_FAIL_TREE = new XMLAnalyzer(XMLTestFiles.getGammaFailDoc());
	
	
	/**
	 * Test getXDoc
	 */
	@Test
	public void testGetXDoc() {
		assertTrue("alpha getXDoc true", ALPHA_TREE.getXDoc().equals(XMLTestFiles.getAlphaDoc()));
		assertFalse("alpha getXDoc false", ALPHA_TREE.getXDoc().equals(XMLTestFiles.getAlphaFailDoc()));
		assertTrue("alpha_fail getXDoc true", ALPHA_FAIL_TREE.getXDoc().equals(XMLTestFiles.getAlphaFailDoc()));
		assertTrue("alpha2 getXDoc true", ALPHA2_TREE.getXDoc().equals(XMLTestFiles.getAlpha2Doc()));
		assertTrue("beta getXDoc true", BETA_TREE.getXDoc().equals(XMLTestFiles.getBetaDoc()));
		assertFalse("beta getXDoc false", BETA_TREE.getXDoc().equals(XMLTestFiles.getBeta2Doc()));
		assertTrue("BETA2 getXDoc true", BETA2_TREE.getXDoc().equals(XMLTestFiles.getBeta2Doc()));
		assertTrue("charlie getXDoc true", CHARLIE_TREE.getXDoc().equals(XMLTestFiles.getCharlieDoc()));
		assertTrue("gamma getXDoc true", GAMMA_TREE.getXDoc().equals(XMLTestFiles.getGammaDoc()));
		assertFalse("gamma getXDoc false", GAMMA_TREE.getXDoc().equals(XMLTestFiles.getGammaFailDoc()));
	}
	
	/**
	 * Test hasEqualTree
	 */
	@Test
	public void testHasEqualTree() {
		assertTrue("alpha hasEqualTree true", ALPHA_TREE.hasEqualTree(ALPHA_TREE));
		assertTrue("alpha hasEqualTree true", ALPHA_TREE.hasEqualTree(ALPHA2_TREE));
		assertFalse("alpha hasEqualTree false", ALPHA_TREE.hasEqualTree(ALPHA_FAIL_TREE));
		assertTrue("beta hasEqualTree true", BETA_TREE.hasEqualTree(BETA2_TREE));
		assertFalse("beta hasEqualTree alpha false", BETA_TREE.hasEqualTree(ALPHA_TREE));
		assertTrue("delta hasEqualTree delta2 true", DELTA_TREE.hasEqualTree(DELTA2_TREE));
		assertTrue("delta hasEqualTree delta3 true", DELTA_TREE.hasEqualTree(DELTA3_TREE));
		assertFalse("delta hasEqualTree delta_fail false", DELTA_TREE.hasEqualTree(DELTA_FAIL_TREE));
		assertFalse("gamma hasEqualTree gamma_fail false",GAMMA_TREE.hasEqualTree(GAMMA_FAIL_TREE));
	}
	
	/**
	 * Test equals
	 * 
	 * alpha: an XML tree should only be equal to itself
	 * because to pass equality it must not only have an equal tree,
	 * but must also have the same document as its xdoc
	 */
	@Test
	public void testEquals() {
		assertTrue("alpha equals true", ALPHA_TREE.equals(ALPHA_TREE));
		assertFalse("alpha equals alpha false", ALPHA_TREE.equals(ALPHA2_TREE));
		assertFalse("alpha equals alpha2 false", ALPHA_TREE.equals(ALPHA_FAIL_TREE));
		assertFalse("alpha equals doc false", ALPHA_TREE.equals(XMLTestFiles.getAlphaDoc()));
		assertFalse("alpha equals string false", ALPHA_TREE.equals("XMLFiles/note.xml"));
		assertFalse("beta equals false", BETA_TREE.equals(BETA2_TREE));
		assertTrue("charlie equals true", CHARLIE_TREE.equals(CHARLIE_TREE));
		assertFalse("delta equals delta2 false", DELTA_TREE.equals(DELTA2_TREE));
		assertFalse("delta equals delta3 false", DELTA_TREE.equals(DELTA3_TREE));
		assertFalse("delta equals delta_fail false", DELTA_TREE.equals(DELTA_FAIL_TREE));
	}
	
	/**
	 * Test printIfDifferent
	 * 
	 */
	@Test
	public void testPrintIfDifferent() {
		String one = ALPHA_TREE.printIfDifferent(DELTA2_TREE, "file", "C:/Users/IBM_ADMIN/xmlmigration/XMLOutput/Logs/test2.txt");
		String two = ALPHA_TREE.printIfDifferent(DELTA2_TREE, "console");
		String three = ALPHA_TREE.printIfDifferent(DELTA_FAIL_TREE, "string");
		System.out.println(one);
		System.out.println(two);
		System.out.println(three);
	}
}
