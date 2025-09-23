package validationTool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import resources.XMLValidation;

/**
 * This class is just a way to store a bunch of XML files to 
 * use when testing XML Migration related classes.
 * 
 * This has 10 example XML files, where a file named
 * NAME is some xml, a file named NAME_FAIL is a file
 * that is some variation of NAME that is not an equivalent
 * xml, and a file named NAME(#) is some equivalent
 * variation of NAME. 
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-20
 */
public class XMLTestFiles {
	
	private static final String ALPHA = "XMLFiles/note.xml";
	private static final String ALPHA2 = "XMLFiles/note_copy.xml";
	private static final String ALPHA_FAIL = "XMLFiles/note2.xml";
	private static final String ALPHA_FAIL2 = "XMLFiles/note3.xml";
	private static final String BETA = "XMLFiles/simple.xml";
	private static final String BETA2 = "XMLFiles/simple2.xml";
	private static final String CHARLIE = "XMLFiles/LotusConnections-config.xml";
	private static final String DELTA = "XMLFiles/eA.xml";
	private static final String DELTA2 = "XMLFiles/eA2.xml";
	private static final String DELTA3 = "XMLFiles/eA3.xml";
	private static final String DELTA_FAIL = "XMLFiles/eB.xml";
	private static final String DELTA_FAIL2 = "XMLFiles/eC.xml";
	private static final String GAMMA = "XMLFiles/match.xml";
	private static final String GAMMA_FAIL = "XMLFiles/match2.xml"; //NOTE: this will fail with XMLAnalyzer,but it technically matches if order is ignored
	
	Display display = Display.getDefault();
	Shell shell = new Shell (display);
	Composite comp = new Composite(shell, SWT.NONE);

	private static XMLTree ALPHA_TREE = null;
	private static XMLTree ALPHA2_TREE = null;
	private static XMLTree ALPHA_FAIL_TREE = null;
	private static XMLTree ALPHA_FAIL2_TREE = null;
	private static XMLTree BETA_TREE = null;
	private static XMLTree BETA2_TREE = null;
	private static XMLTree CHARLIE_TREE = null;
	private static XMLTree DELTA_TREE = null;
	private static XMLTree DELTA2_TREE = null;
	private static XMLTree DELTA3_TREE = null;
	private static XMLTree DELTA_FAIL_TREE = null;
	private static XMLTree DELTA_FAIL2_TREE = null;
	private static XMLTree GAMMA_TREE = null;
	private static XMLTree GAMMA_FAIL_TREE = null;
	

	public XMLTestFiles() {
		try {
			ALPHA_TREE = new XMLTree(comp, ALPHA, "left");
			ALPHA2_TREE = new XMLTree(comp, ALPHA2, "left");
			ALPHA_FAIL_TREE = new XMLTree(comp, ALPHA_FAIL, "left");
			ALPHA_FAIL2_TREE = new XMLTree(comp, ALPHA_FAIL2, "left");
			BETA_TREE = new XMLTree(comp, BETA, "left", true, false, true, true, false);
			BETA2_TREE = new XMLTree(comp, BETA2, "left", false, true, true, false, true);
			CHARLIE_TREE = new XMLTree(comp, CHARLIE, "left", false, false, false, false, false);
			DELTA_TREE = new XMLTree(comp, DELTA, "left");
			DELTA2_TREE = new XMLTree(comp, DELTA2, "left", false, true, true, true, false);
			DELTA3_TREE = new XMLTree(comp, DELTA3, "left");
			DELTA_FAIL_TREE = new XMLTree(comp, DELTA_FAIL, "left", true, true, true, false, true);
			DELTA_FAIL2_TREE = new XMLTree(comp, DELTA_FAIL2, "left");
			GAMMA_TREE = new XMLTree(comp, GAMMA, "left");
			GAMMA_FAIL_TREE = new XMLTree(comp, GAMMA_FAIL, "left");	
		} catch (SAXParseException e1) {
			//TODO
			e1.printStackTrace();
		} catch (SAXException e2) {
			//TODO
			e2.printStackTrace();
		} catch (Exception e3) {
			//TODO
			e3.printStackTrace();
		}
	}
	
	public static String getAlpha() {
		return ALPHA;
	}

	public static String getAlpha2() {
		return ALPHA2;
	}
	
	public static String getAlphaFail() {
		return ALPHA_FAIL;
	}
	
	public static String getAlpha2Fail() {
		return ALPHA_FAIL2;
	}

	public static String getBeta() {
		return BETA;
	}

	public static String getBeta2() {
		return BETA2;
	}

	public static String getCharlie() {
		return CHARLIE;
	}

	public static String getDelta() {
		return DELTA;
	}

	public static String getDelta2() {
		return DELTA2;
	}

	public static String getDelta3() {
		return DELTA3;
	}

	public static String getDeltaFail() {
		return DELTA_FAIL;
	}
	
	public static String getDeltaFail2() {
		return DELTA_FAIL2;
	}
	
	public static String getGamma() {
		return GAMMA;
	}

	public static String getGammaFail() {
		return GAMMA_FAIL;
	}

	public static XMLTree getAlphaTree() {
		return ALPHA_TREE;
	}

	public static XMLTree getAlpha2Tree() {
		return ALPHA2_TREE;
	}
	
	public static XMLTree getAlphaFailTree() {
		return ALPHA_FAIL_TREE;
	}

	public static XMLTree getBetaTree() {
		return BETA_TREE;
	}

	public static XMLTree getBeta2Tree() {
		return BETA2_TREE;
	}

	public static XMLTree getCharlieTree() {
		return CHARLIE_TREE;
	}

	public static XMLTree getDeltaTree() {
		return DELTA_TREE;
	}

	public static XMLTree getDelta2Tree() {
		return DELTA2_TREE;
	}

	public static XMLTree getDelta3Tree() {
		return DELTA3_TREE;
	}

	public static XMLTree getDeltaFailTree() {
		return DELTA_FAIL_TREE;
	}

	public static XMLTree getDeltaFail2Tree() {
		return DELTA_FAIL2_TREE;
	}

	public static XMLTree getGammaTree() {
		return GAMMA_TREE;
	}

	public static XMLTree getGamaFailTree() {
		return GAMMA_FAIL_TREE;
	}

	public static XMLTree getAlphaFail2Tree() {
		return ALPHA_FAIL2_TREE;
	}

}
