package resources;

/**
 * 
 * This class transforms one XML file into
 * another by applying an XSL to the XML to
 * restyle it according to the rules of the XSL,
 * as well as checking that the newly transformed 
 * XML is a valid XML by checking it with an XSD.
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @since 1.6
 * @version 2012-05-30
 *
 */
public class XMLTransformer {
	
	/**
	 * variables
	 * 
	 * xold: (String) The path to the xml being transformed
	 * 
	 * xsl: (String) The path to the xsl for transforming xold
	 * 
	 * xsd: (String) The path to the xsd for validating the new xml
	 * 
	 * xtransformed: (String) The transformed document
	 */
	private String xold;
	private String xsl;
	private String xsd;
	private String xtransformed;
	
	/**
	 * constructor
	 */

}
