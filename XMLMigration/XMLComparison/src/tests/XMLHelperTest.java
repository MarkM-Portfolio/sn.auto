package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import resources.XMLHelper;
import resources.XMLTestFiles;

/**
 * Tests for XMLHelper
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-20
 */
public class XMLHelperTest {

	@Test
	public final void testBuildBranchList() {
		//make the expected branch list for alpha
		ArrayList<String> alpha_branches = new ArrayList<String>();
		alpha_branches.add("to");
		alpha_branches.add("from");
		alpha_branches.add("heading");
		alpha_branches.add("body");
		
		assertTrue("alpha branches true", XMLHelper.buildBranchList(XMLTestFiles.getAlphaDoc()).equals(alpha_branches));
		assertTrue("alpha2 branches true", XMLHelper.buildBranchList(XMLTestFiles.getAlpha2Doc()).equals(alpha_branches));
		assertTrue("alpha_fail branches true", XMLHelper.buildBranchList(XMLTestFiles.getAlphaFailDoc()).equals(alpha_branches));
		
		//make the expected branch list for delta
		ArrayList<String> delta_branches = new ArrayList<String>();
		delta_branches.add("element1");
		delta_branches.add("element2");
		delta_branches.add("element3");
		
		assertTrue("delta branches true", XMLHelper.buildBranchList(XMLTestFiles.getDeltaDoc()).equals(delta_branches));
		assertTrue("delta2 branches true", XMLHelper.buildBranchList(XMLTestFiles.getDelta2Doc()).equals(delta_branches));
		assertTrue("delta3 branches true", XMLHelper.buildBranchList(XMLTestFiles.getDelta3Doc()).equals(delta_branches));
		assertFalse("delta_fail branches false", XMLHelper.buildBranchList(XMLTestFiles.getDeltaFailDoc()).equals(delta_branches));
		
		delta_branches.add("element4");
		assertTrue("delta_fail2 branches true", XMLHelper.buildBranchList(XMLTestFiles.getDeltaFail2Doc()).equals(delta_branches));
		assertFalse("delta branches false", XMLHelper.buildBranchList(XMLTestFiles.getDeltaDoc()).equals(delta_branches));
		
		//make the expected branch list for delta_fail
		ArrayList<String> delta_fail_branches = new ArrayList<String>();
		delta_fail_branches.add("elementTest");
		
		assertTrue("deltafail branches true", XMLHelper.buildBranchList(XMLTestFiles.getDeltaFailDoc()).equals(delta_fail_branches));
	}

	@Test
	public final void testGetElementChildren() {
		Node alpha_root = XMLTestFiles.getAlphaDoc().getDocumentElement();
		ArrayList<Node> alpha_elements = XMLHelper.getElementChildren(alpha_root);
		
		Node alpha2_root = XMLTestFiles.getAlpha2Doc().getDocumentElement();
		ArrayList<Node> alpha2_elements = XMLHelper.getElementChildren(alpha2_root);
		
		Node alpha_fail_root = XMLTestFiles.getAlphaFailDoc().getDocumentElement();
		ArrayList<Node> alpha_fail_elements = XMLHelper.getElementChildren(alpha_fail_root);
		
		//make the expected list of alpha_roots element children (as a list of node names because equal nodes cant be made)
		ArrayList<String> alpha_children = new ArrayList<String>();
		alpha_children.add("to");
		alpha_children.add("to");
		alpha_children.add("from");
		alpha_children.add("from");
		alpha_children.add("heading");
		alpha_children.add("body");
		
		assertTrue("alpha elementChildren true", XMLHelper.convertList(alpha_elements).equals(alpha_children));
		assertTrue("alpha2 elementChildren true", XMLHelper.convertList(alpha2_elements).equals(alpha_children));
		assertFalse("alphafail elementChildren false", XMLHelper.convertList(alpha_fail_elements).equals(alpha_children));
		
		Node delta_root = XMLTestFiles.getDeltaDoc().getDocumentElement();
		ArrayList<Node> delta_elements = XMLHelper.getElementChildren(delta_root);
		
		Node delta2_root = XMLTestFiles.getDelta2Doc().getDocumentElement();
		ArrayList<Node> delta2_elements = XMLHelper.getElementChildren(delta2_root);
		
		Node delta3_root = XMLTestFiles.getDelta3Doc().getDocumentElement();
		ArrayList<Node> delta3_elements = XMLHelper.getElementChildren(delta3_root);
		
		Node delta_fail_root = XMLTestFiles.getDeltaFailDoc().getDocumentElement();
		ArrayList<Node> delta_fail_elements = XMLHelper.getElementChildren(delta_fail_root);
		
		//make the expected list of delta_root's element children (as a list of node names because equal nodes cant be made)
		ArrayList<String> delta_children = new ArrayList<String>();
		delta_children.add("element1");
		delta_children.add("element1");
		delta_children.add("element2");
		delta_children.add("element3");
		
		assertTrue("delta elementChildren true", XMLHelper.convertList(delta_elements).equals(delta_children));
		assertTrue("delta2 elementChildren true", XMLHelper.convertList(delta2_elements).equals(delta_children));
		assertTrue("delta3 elementChildren true", XMLHelper.convertList(delta3_elements).equals(delta_children));
		assertFalse("deltafail elementChildren false", XMLHelper.convertList(delta_fail_elements).equals(delta_children));
		
	}

	@Test
	public final void testChildlessNodeCounter() {
		Node alpha_root = XMLTestFiles.getAlphaDoc().getDocumentElement();
		ArrayList<Node> alpha_children = XMLHelper.getElementChildren(alpha_root);
		Node to = alpha_children.get(0); //the first "to" node
		Node heading = alpha_children.get(4); //the "heading" node
		
		assertTrue("alpha childlessNodeCounter to true", XMLHelper.childlessNodeCounter(to, alpha_children) == 1);
		assertTrue("alpha childlessNodeCounter to true", XMLHelper.childlessNodeCounter(to, XMLHelper.getElementChildren(to)) == 0);
		assertFalse("alpha childlessNodeCounter heading false", XMLHelper.childlessNodeCounter(heading, alpha_children) == 1);
		
		Node cheading = XMLHelper.getElementChildren(heading).get(2); //the heading's cheading node
		ArrayList<Node> cheading_list = XMLHelper.getElementChildren(cheading); //the children of cheading
		Node c1heading = cheading_list.get(0);//the cheading's c1heading node
		Node c2heading = cheading_list.get(1); //the cheading's c2heading's node
		ArrayList<Node> c2aheading_list = XMLHelper.getElementChildren(c2heading); //the children of c2heading
		Node c2aheading = c2aheading_list.get(0);
		
		assertTrue("alpha childlessNodeCounter ch2aheading true", XMLHelper.childlessNodeCounter(c2aheading, c2aheading_list) == 3);
		assertFalse("alpha childlessNodeCounter ch1heading false", XMLHelper.childlessNodeCounter(c1heading, cheading_list) == 0);
	}

	@Test
	public final void testParentFinder() {
		Document alpha = XMLTestFiles.getAlphaDoc();
		Node alpha_root = alpha.getDocumentElement();
		ArrayList<Node> aroot_children = XMLHelper.getElementChildren(alpha_root);
		Node to = aroot_children.get(0);
		Node to_cheading = XMLHelper.getElementChildren(to).get(0);
		
		assertTrue("alpha parentFinder to true", XMLHelper.parentFinder(to, alpha).equals(to));
		assertTrue("alpha parentFinder to_cheading true", XMLHelper.parentFinder(to_cheading, alpha).equals(to));
		
		Document beta = XMLTestFiles.getBetaDoc();
		Node beta_root = beta.getDocumentElement();
		//tests with beta doc
		
		Document delta = XMLTestFiles.getDeltaDoc();
		Node delta_root = delta.getDocumentElement();
		//tests with delta doc
		
		Document delta_fail = XMLTestFiles.getDeltaFailDoc();
		Node delta_fail_root = delta_fail.getDocumentElement();
		ArrayList<Node> dfroot_children = XMLHelper.getElementChildren(delta_fail_root);
		Node elementTest = dfroot_children.get(0);
		Node element2 = XMLHelper.getElementChildren(elementTest).get(2); //element2 is the third element
		Node element1 = XMLHelper.getElementChildren(elementTest).get(3);
		Node elementi = XMLHelper.getElementChildren(element1).get(0); //element1 inside the last element1
		
		assertTrue("delta_fail parentFinder elementTest true", XMLHelper.parentFinder(elementTest, delta_fail).equals(elementTest));
		assertTrue("delta_fail parentFinder element2 true", XMLHelper.parentFinder(element2, delta_fail).equals(elementTest));
		assertTrue("delta_fail parentFinder element1 true", XMLHelper.parentFinder(element1, delta_fail).equals(elementTest));
		assertTrue("delta_fail parentFinder elementi true", XMLHelper.parentFinder(elementi, delta_fail).equals(elementTest));
		
		Document gamma = XMLTestFiles.getGammaDoc();
		Node gamma_root = gamma.getDocumentElement();
	}

	@Test
	public final void testGetFirstBorns() {
		Node alpha_root = XMLTestFiles.getAlphaDoc().getDocumentElement();
		NodeList alpha_nodes = alpha_root.getChildNodes();
		ArrayList<Node> alpha_first = XMLHelper.convertListNode(alpha_nodes);
		
		assertTrue("alpha getFirstBorns root true",XMLHelper.getFirstBorns(alpha_nodes, XMLTestFiles.getAlphaDoc()).equals(alpha_first));
		
		NodeList element1_nodes = XMLTestFiles.getDeltaDoc().getElementsByTagName("element1");
		assertTrue("delta getFirstBorns element1 true", XMLHelper.getFirstBorns(element1_nodes, XMLTestFiles.getDeltaDoc()).size() < element1_nodes.getLength());
		assertFalse("delta getFirstBorns element1 false", (XMLHelper.getFirstBorns(element1_nodes, XMLTestFiles.getDeltaDoc()).equals(element1_nodes)));
	}

	@Test
	public final void testGetNodeInfo() {
		//TODO
	}

	@Test
	public final void testConvertListArrayListOfNode() {
		ArrayList<Node> alpha_first_gen = XMLHelper.getElementChildren(XMLTestFiles.getAlphaDoc().getDocumentElement());
		//make the list of strings first_gen should convert to
		ArrayList<String> alpha_converted = new ArrayList<String>();
		alpha_converted.add("to");
		alpha_converted.add("to");
		alpha_converted.add("from");
		alpha_converted.add("from");
		alpha_converted.add("heading");
		alpha_converted.add("body");
		
		assertTrue("alpha convertListArrayListOfNode firstgen true", XMLHelper.convertList(alpha_first_gen).equals(alpha_converted));
		assertFalse("alpha convertListArrayListOfNode children true", XMLHelper.convertList(alpha_first_gen).equals(XMLTestFiles.getAlphaDoc().getDocumentElement()));
	}

	@Test
	public final void testConvertListNodeList() {
		// TODO
	}

	@Test
	public final void testConvertListNode() {
		// TODO
	}
	
	/*
	 * The following methods that belong to XMLHelper are
	 * tested in XMLTreeTest because they essentially required
	 * XMLTrees to test, and XMLTrees can't be imported here
	 * because of the organization of the buildpath.
	 * 
	 * - findClosestMatch
	 * 
	 * - countChildMatches
	 * 
	 * - countExtras
	 * 
	 * - removeChild(Node)
	 * 
	 * - removeChild(TreeItem)
	 */
}
