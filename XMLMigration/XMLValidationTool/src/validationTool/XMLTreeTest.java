package validationTool;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import resources.XMLHelper;
import resources.XMLUtilities;
import resources.XMLValidation;


public class XMLTreeTest {
	
	//need to create an instance of XMLTestFiles because of the constructor
	XMLTestFiles files = new XMLTestFiles();

	@Test
	public final void testGetHighlightDiff() {
		assertTrue("alpha getHighlightDiff true", XMLTestFiles.getAlphaTree().getHighlightDiff()); //standard construction
		
		assertTrue("beta getHighlightDiff true", XMLTestFiles.getBetaTree().getHighlightDiff());
		assertFalse("betafail getHighlightDiff false", XMLTestFiles.getBeta2Tree().getHighlightDiff());
		
		assertTrue("delta getHighlightDiff true", XMLTestFiles.getDeltaTree().getHighlightDiff()); //standard construction
		assertFalse("delta2 getHightlightDiff false", XMLTestFiles.getDelta2Tree().getHighlightDiff());
		assertTrue("deltafail getHighlightDiff true", XMLTestFiles.getDeltaFailTree().getHighlightDiff());
	}
	
	@Test
	public final void testSetHighlightDiff() {
		assertTrue("alpha setHighlightDiff true", XMLTestFiles.getAlphaTree().getHighlightDiff());
		XMLTestFiles.getAlphaTree().setHighlightDiff(false);
		assertFalse("alpha setHighlightDiff false", XMLTestFiles.getAlphaTree().getHighlightDiff());

		assertFalse("betafail setHighlightDiff false", XMLTestFiles.getBeta2Tree().getHighlightDiff());
		XMLTestFiles.getBeta2Tree().setHighlightDiff(true);
		assertTrue("betafail setHighlightDiff true", XMLTestFiles.getBeta2Tree().getHighlightDiff());
	}

	@Test
	public final void testGetHighlightExtra() {
		assertTrue("alpha getHighlightExtra true", XMLTestFiles.getAlphaTree().getHighlightExtra()); //standard construction
		
		assertFalse("beta getHighlightExtra false", XMLTestFiles.getBetaTree().getHighlightExtra());
		assertTrue("betafail getHighlightExtra true", XMLTestFiles.getBeta2Tree().getHighlightExtra());
		
		assertTrue("delta getHighlightExtra true", XMLTestFiles.getDeltaTree().getHighlightExtra()); //standard construction
		assertTrue("delta2 getHightlightExtra true", XMLTestFiles.getDelta2Tree().getHighlightExtra());
		assertTrue("deltafail getHighlightExtra true", XMLTestFiles.getDeltaFailTree().getHighlightExtra());
	}

	@Test
	public final void testSetHighlightExtra() {
		assertTrue("alpha setHighlightExtra true", XMLTestFiles.getAlphaTree().getHighlightExtra());
		XMLTestFiles.getAlphaTree().setHighlightExtra(false);
		assertFalse("alpha setHighlightExtra false", XMLTestFiles.getAlphaTree().getHighlightExtra());

		assertFalse("beta setHighlightExtra false", XMLTestFiles.getBetaTree().getHighlightExtra());
		XMLTestFiles.getBetaTree().setHighlightExtra(true);
		assertTrue("beta setHighlightExtra true", XMLTestFiles.getBetaTree().getHighlightExtra());
	}

	@Test
	public final void testGetHighlightGuess() {
		assertTrue("alpha getHighlightGuess true", XMLTestFiles.getAlphaTree().getHighlightGuess()); //standard construction
		
		assertTrue("beta getHighlightGuess true", XMLTestFiles.getBetaTree().getHighlightGuess());
		assertFalse("betafail getHighlightGuess false", XMLTestFiles.getBeta2Tree().getHighlightGuess());
		
		assertTrue("delta getHighlightGuess true", XMLTestFiles.getDeltaTree().getHighlightGuess()); //standard construction
		assertTrue("delta2 getHightlightGuess true", XMLTestFiles.getDelta2Tree().getHighlightGuess());
		assertFalse("deltafail getHighlightGuess false", XMLTestFiles.getDeltaFailTree().getHighlightGuess());
	}

	@Test
	public final void testSetHighlightGuess() {
		assertTrue("alpha setHighlightGuess true", XMLTestFiles.getAlphaTree().getHighlightGuess());
		XMLTestFiles.getAlphaTree().setHighlightGuess(false);
		assertFalse("alpha setHighlightGuess false", XMLTestFiles.getAlphaTree().getHighlightGuess());

		assertFalse("betafail setHighlightGuess false", XMLTestFiles.getBeta2Tree().getHighlightGuess());
		XMLTestFiles.getBeta2Tree().setHighlightGuess(true);
		assertTrue("betafail setHighlightGuess true", XMLTestFiles.getBeta2Tree().getHighlightGuess());
	}

	@Test
	public final void testgetGuessStrict() {
		assertFalse("alpha getGuessStrict false", XMLTestFiles.getAlphaTree().getGuessStrict()); //standard construction
		
		assertFalse("beta getGuessStrict false", XMLTestFiles.getBetaTree().getGuessStrict());
		assertTrue("betafail getGuessStrict true", XMLTestFiles.getBeta2Tree().getGuessStrict());
		
		assertFalse("delta getGuessStrict false", XMLTestFiles.getDeltaTree().getGuessStrict()); //standard construction
		assertFalse("delta2 getGuessStrict false", XMLTestFiles.getDelta2Tree().getGuessStrict());
		assertTrue("deltafail getGuessStrict true", XMLTestFiles.getDeltaFailTree().getGuessStrict());
	}

	@Test
	public final void testSetGuessStrict() {
		assertFalse("alpha setGuessStrict false", XMLTestFiles.getAlphaTree().getGuessStrict());
		XMLTestFiles.getAlphaTree().setGuessStrict(true);
		assertTrue("alpha setGuessStrict false", XMLTestFiles.getAlphaTree().getGuessStrict());

		assertTrue("betafail setGuessStrict false", XMLTestFiles.getBeta2Tree().getGuessStrict());
		XMLTestFiles.getBeta2Tree().setGuessStrict(false);
		assertFalse("betafail setGuessStrict true", XMLTestFiles.getBeta2Tree().getGuessStrict());
	}
	
	@Test
	public final void testGetFPath() {
		assertTrue("alpha getFPath true", XMLTestFiles.getAlphaTree().getFPath() == XMLTestFiles.getAlpha());
		assertFalse("alpha getFPath false", XMLTestFiles.getAlpha2Tree().getFPath() == XMLTestFiles.getAlpha());
		assertTrue("delta getFPath true", XMLTestFiles.getDeltaTree().getFPath() == XMLTestFiles.getDelta());
		assertFalse("delta getFPath false", XMLTestFiles.getDeltaTree().getFPath() == XMLTestFiles.getDelta2());
	}

	@Test
	public final void testGetCPath() {
		assertTrue("alpha getCPath self true", XMLTestFiles.getAlphaTree().getCPath() == XMLTestFiles.getAlpha());
		XMLTestFiles.getAlphaTree().setCompareDoc(XMLTestFiles.getBeta()); //set a compare doc, which will change the CPath
		assertTrue("alpha getCPath beta true", XMLTestFiles.getAlphaTree().getCPath() == XMLTestFiles.getBeta());
		assertFalse("alpha getCPath self false", XMLTestFiles.getAlphaTree().getCPath() == XMLTestFiles.getAlpha());
	}

	@Test
	public final void testGetSide() {
		// TODO
	}

	@Test
	public final void testGetToggleValues() {
		boolean[] alpha_tog = XMLTestFiles.getAlpha2Tree().getToggleValues();
		boolean alpha_diff = alpha_tog[0];
		boolean alpha_extra = alpha_tog[1];
		boolean alpha_guess = alpha_tog[2];
		boolean alpha_strict = alpha_tog[3];
		
		assertTrue("alpha getToggleValues diff true", alpha_diff);
		assertTrue("alpha getToggleValues extra true", alpha_extra);
		assertTrue("alpha getToggleValues guess true", alpha_guess);
		assertFalse("alpha getToggleValues strict false", alpha_strict);
		
		boolean[] beta_tog = XMLTestFiles.getBetaTree().getToggleValues();
		boolean beta_diff = beta_tog[0];
		boolean beta_extra = beta_tog[1];
		boolean beta_guess = beta_tog[2];
		boolean beta_strict = beta_tog[3];
		
		assertTrue("beta getToggleValues diff true", beta_diff);
		assertFalse("beta getToggleValues extra false", beta_extra);
		assertTrue("beta getToggleValues guess true", beta_guess);
		assertFalse("beta getToggleValues strict false", beta_strict);
		
		boolean[] charlie_tog = XMLTestFiles.getCharlieTree().getToggleValues();
		boolean charlie_diff = charlie_tog[0];
		boolean charlie_extra = charlie_tog[1];
		boolean charlie_guess = charlie_tog[2];
		boolean charlie_strict = charlie_tog[3];
		
		assertFalse("charlie getToggleValues diff false", charlie_diff);
		assertFalse("charlie getToggleValues extra false", charlie_extra);
		assertFalse("charlie getToggleValues guess false", charlie_guess);
		assertFalse("charlie getToggleValues strict false", charlie_strict);
	}

	@Test
	public final void testSetCompareDoc() {
		assertTrue("alpha setCompareDoc self true", XMLTestFiles.getAlphaTree().getCPath() == XMLTestFiles.getAlpha());
		XMLTestFiles.getAlphaTree().setCompareDoc(XMLTestFiles.getDelta()); //set a compare doc,
		assertTrue("alpha getCPath delta true", XMLTestFiles.getAlphaTree().getCPath() == XMLTestFiles.getDelta());
		assertFalse("alpha getCPath self false", XMLTestFiles.getAlphaTree().getCPath() == XMLTestFiles.getAlpha());
	}
	
	@Test
	public final void testGetTestRootNode() {
		assertTrue("alpha getTestRootNode null", XMLTestFiles.getAlphaTree().getTestRootNode() == null);
		XMLTestFiles.getAlphaTree().createContents();
		assertTrue("alpha getTestRootNode true", XMLTestFiles.getAlphaTree().getTestRootNode() != null);
		assertTrue("alpha getTestRootNode name", XMLTestFiles.getAlphaTree().getTestRootNode().getText() == "note");
		XMLTestFiles.getAlphaTree();
		assertTrue("alpha getTestRootNode warning", XMLTestFiles.getAlphaTree().getTestRootNode().getData("warning") == XMLTree.warning_none);
	}
	
	@Test
	public final void testGetTestBranchNodes() {
		assertTrue("alpha getTestBranchNodes null", XMLTestFiles.getAlphaTree().getTestBranchNodes() == null);
		XMLTestFiles.getAlphaTree().createContents();
		assertTrue("alpha getTestBranchNodes true", XMLTestFiles.getAlphaTree().getTestRootNode() != null);
		assertTrue("alpha getTestBranchNodes size", XMLTestFiles.getAlphaTree().getTestBranchNodes().size() == 4);
		assertTrue("alpha getTestBranchNodes text0", XMLTestFiles.getAlphaTree().getTestBranchNodes().get(0).getText().equals("[ to branch ]"));
		assertTrue("alpha getTestBranchNodes text1", XMLTestFiles.getAlphaTree().getTestBranchNodes().get(1).getText().equals("[ from branch ]"));
		assertTrue("alpha getTestBranchNodes text2", XMLTestFiles.getAlphaTree().getTestBranchNodes().get(2).getText().equals("[ heading branch ]"));
		assertTrue("alpha getTestBranchNodes text3", XMLTestFiles.getAlphaTree().getTestBranchNodes().get(3).getText().equals("[ body branch ]"));
		
		assertTrue("deltafail getTestBranchNodes null", XMLTestFiles.getDeltaFailTree().getTestBranchNodes() == null);
		XMLTestFiles.getDeltaFailTree().createContents();
		assertTrue("deltafail getTestBranchNodes true", XMLTestFiles.getDeltaFailTree().getTestRootNode() != null);
		assertTrue("deltafail getTestBranchNodes size", XMLTestFiles.getDeltaFailTree().getTestBranchNodes().size() == 1);
		assertTrue("deltafail getTestBranchNodes text0", XMLTestFiles.getDeltaFailTree().getTestBranchNodes().get(0).getText().equals("[ elementTest branch ]"));
	}
	
	@Test
	public final void testContent() {
		XMLTestFiles.getAlphaTree().createContents();
		TreeItem to_branch = XMLTestFiles.getAlphaTree().getTestBranchNodes().get(0);
		TreeItem to1 = to_branch.getItems()[0];
		assertFalse("alpha testContentFirstGen to null", to1.equals(null));
		assertTrue("alpha testContentFirstGen to name", to1.getText().equals("to (%)")); //ends in " (%)" because when AlphaTree is created, the highlight_guess is set to true
		TreeItem c1heading = to1.getItems()[0];
		assertFalse("alpha testContentFirstGen c1heading null", c1heading.equals(null));
		assertTrue("alpha testContentFirstGen c1heading name", c1heading.getText().equals("c1heading (%)"));  //ends in " (%)" because when AlphaTree is created, the highlight_guess is set to true
		
		XMLTestFiles.getDeltaTree().setCompareDoc(XMLTestFiles.getDeltaFail2());
		XMLTestFiles.getDeltaFail2Tree().setCompareDoc(XMLTestFiles.getDelta());
		XMLTestFiles.getDeltaTree().createContents();
		XMLTestFiles.getDeltaFail2Tree().createContents();
		
		TreeItem droot1 = XMLTestFiles.getDeltaTree().getTestRootNode();
		TreeItem droot2 = XMLTestFiles.getDeltaFail2Tree().getTestRootNode();
		assertFalse("delta testContentRoots name", droot1.getText().equals(droot2.getText()));
		assertTrue("deltafail2 testContentRoots ending", droot2.getText().endsWith(" <!>"));
		assertTrue("deltafail2 testContentRoots color", droot2.getForeground().equals(XMLTree.XDIFF_CHILDREN));
		assertTrue("deltafail2 testContentRoots warning", droot2.getData("warning") == XMLTree.warning_diff);
		
		TreeItem el4_branch = droot2.getItems()[3];
		assertTrue("deltafail2 testContentBranch name", el4_branch.getText().equals("[ element4 branch ] (+)"));
		assertTrue("deltafail2 testContentBranch color", el4_branch.getForeground().equals(XMLTree.XEXTRA_ELEMENT));
		assertTrue("deltafail2 testContentBranch warning", el4_branch.getData("warning") == XMLTree.warning_extra);
		
		TreeItem el4 = el4_branch.getItems()[0];
		assertTrue("deltafail2 testContentNode name", el4.getText().equals("element4 (+)"));
		assertTrue("deltafail2 testContentNode color", el4.getForeground().equals(XMLTree.XEXTRA_ELEMENT));
		assertTrue("deltafail2 testContentNode warning", el4.getData("warning") == XMLTree.warning_extra);
	}
	
	@Test
	public final void testFindClosestMatch() {
		Document alpha_doc = XMLValidation.getXMLDoc(XMLTestFiles.getAlpha());
		Document alphafail2_doc = XMLValidation.getXMLDoc(XMLTestFiles.getAlpha2Fail());
		
		NodeList alpha_tos = alpha_doc.getElementsByTagName("to");
		ArrayList<Node> alpha = XMLHelper.convertListNode(alpha_tos);
		Node alpha_to0p = alpha_tos.item(0); //this "to" has the child c1heading
		Node alpha_to1 = alpha_tos.item(1); //this "to" has no children
		NodeList alphaf2_tos = alphafail2_doc.getElementsByTagName("to");
		ArrayList<Node> alphaf2 = XMLHelper.convertListNode(alphaf2_tos);
		Node alphaf2_to0 = alphaf2_tos.item(0); //this "to" has no children
		Node alphaf2_to1p = alphaf2_tos.item(1); //this "to" has the child c1heading
		
		assertTrue("alpha findClosestMatch smart true", XMLHelper.findClosestMatch(alpha_to0p, alphaf2, false).equals(alphaf2_to1p));
		assertTrue("alpha findClosestMatch strict true", XMLHelper.findClosestMatch(alpha_to0p, alphaf2, true).equals(alphaf2_to0));
		assertTrue("alpha findClosestMatch smart true2", XMLHelper.findClosestMatch(alpha_to1, alphaf2, false).equals(alphaf2_to0));
		assertTrue("alpha findClosestMatch strict true2", XMLHelper.findClosestMatch(alpha_to1, alphaf2, true).equals(alphaf2_to0));
		
		assertTrue("alphafail2 findClosestMatch smart true", XMLHelper.findClosestMatch(alphaf2_to1p, alpha, false).equals(alpha_to0p));
		assertTrue("alphafail2 findClosestMatch strict true", XMLHelper.findClosestMatch(alphaf2_to1p, alpha, true).equals(alpha_to0p));
		assertTrue("alphafail2 findClosestMatch smart true", XMLHelper.findClosestMatch(alphaf2_to0, alpha, false).equals(alpha_to1));
		assertTrue("alphafail2 findClosestMatch strict true", XMLHelper.findClosestMatch(alphaf2_to0, alpha, true).equals(alpha_to0p));
		
		assertTrue("alphafail2 findClosestMatch self smart true", XMLHelper.findClosestMatch(alphaf2_to1p, alphaf2, false).equals(alphaf2_to1p));
		assertFalse("alphafail2 findClosestMatch self strict false", XMLHelper.findClosestMatch(alphaf2_to1p, alphaf2, true).equals(alphaf2_to1p));
	}
	
	@Test
	public final void testCountChildMatches() {
		//TODO
	}
	
	@Test
	public final void testCountExtras() {
		//TODO
	}
	
	@Test
	public final void testRemoveChildNode() {
		// TODO
	}
	
	@Test
	public final void testRemoveChildTreeItem() {
		//TODO
	}
	
	public static void main (String[] args) {
		/*
		String fpath = args[0]; //must be a file path!
		String cpath = args[1]; //must be a file path!
 		Display display = Display.getDefault();
		final Shell shell = new Shell (display);
		shell.setSize(900, 7);	

		Composite singleTree = new Composite(shell, SWT.NONE);
		XMLTree xtree;
		try {
			xtree = new XMLTree(singleTree ,fpath, "left");
			xtree.setCompareDoc(cpath);
			xtree.createContents();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		shell.pack ();
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
		*/
	}
}
