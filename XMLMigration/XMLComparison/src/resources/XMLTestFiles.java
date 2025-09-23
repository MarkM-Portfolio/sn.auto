package resources;

import org.w3c.dom.Document;

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
	private static final String ALPHA_FAIL = "XMLFiles/note2.xml";
	private static final String ALPHA2 = "XMLFiles/note_copy.xml";
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
	
	private static final Document ALPHA_DOC = XMLValidation.getXMLDoc(ALPHA);
	private static final Document ALPHA_FAIL_DOC = XMLValidation.getXMLDoc(ALPHA_FAIL);
	private static final Document ALPHA2_DOC = XMLValidation.getXMLDoc(ALPHA2);
	private static final Document BETA_DOC = XMLValidation.getXMLDoc(BETA);
	private static final Document BETA2_DOC = XMLValidation.getXMLDoc(BETA2);
	private static final Document CHARLIE_DOC = XMLValidation.getXMLDoc(CHARLIE);
	private static final Document DELTA_DOC = XMLValidation.getXMLDoc(DELTA);
	private static final Document DELTA2_DOC = XMLValidation.getXMLDoc(DELTA2);
	private static final Document DELTA3_DOC = XMLValidation.getXMLDoc(DELTA3);
	private static final Document DELTA_FAIL_DOC = XMLValidation.getXMLDoc(DELTA_FAIL);
	private static final Document DELTA_FAIL2_DOC = XMLValidation.getXMLDoc(DELTA_FAIL2);
	private static final Document GAMMA_DOC = XMLValidation.getXMLDoc(GAMMA);
	private static final Document GAMMA_FAIL_DOC = XMLValidation.getXMLDoc(GAMMA_FAIL);

	public XMLTestFiles() {
		
	}
	
	public static String getAlpha() {
		return ALPHA;
	}

	public static String getAlphaFail() {
		return ALPHA_FAIL;
	}

	public static String getAlpha2() {
		return ALPHA2;
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

	public static Document getAlphaDoc() {
		return ALPHA_DOC;
	}

	public static Document getAlphaFailDoc() {
		return ALPHA_FAIL_DOC;
	}

	public static Document getAlpha2Doc() {
		return ALPHA2_DOC;
	}

	public static Document getBetaDoc() {
		return BETA_DOC;
	}

	public static Document getBeta2Doc() {
		return BETA2_DOC;
	}

	public static Document getCharlieDoc() {
		return CHARLIE_DOC;
	}

	public static Document getDeltaDoc() {
		return DELTA_DOC;
	}

	public static Document getDelta2Doc() {
		return DELTA2_DOC;
	}

	public static Document getDelta3Doc() {
		return DELTA3_DOC;
	}

	public static Document getDeltaFailDoc() {
		return DELTA_FAIL_DOC;
	}

	public static Document getDeltaFail2Doc() {
		return DELTA_FAIL2_DOC;
	}

	public static Document getGammaDoc() {
		return GAMMA_DOC;
	}

	public static Document getGammaFailDoc() {
		return GAMMA_FAIL_DOC;
	}
}
