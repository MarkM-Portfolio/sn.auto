package resources;

import displayModes.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLValidators compares XML files
 * 
 * it exists 
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class XMLValidator {

	private DisplayMode display;

	private String XMLtoCheck;
	private String XMLReference;
	private Map<String, String> multiples;

	private String displayType;
	private String displayLength;
	private String outputFile;

	/**
	 * runs validation as specified by the config file.
	 * 
	 * the config file path should be specified as the first command line argument
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		//get a list of XMLValidators from the configuration file
		ArrayList<XMLValidator> validators = configure(args[0]);

		//run validate on every XMLValidator
		for(XMLValidator x : validators)
		{
			try{
				x.validateXML();
			}
			catch(XMLException e)
			{
				System.out.print(e.getMessage());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public DisplayMode getDisplayMode()
	{
		return display;
	}


	/**
	 * @return the xMLtoCheck
	 */
	public String getXMLtoCheck() {
		return XMLtoCheck;
	}

	/**
	 * @return the xMLReference
	 */
	public String getXMLReference() {
		return XMLReference;
	}

	/**
	 * @return the multiples
	 */
	public Map<String, String> getMultiples() {
		return multiples;
	}

	/**
	 * @return the displayType
	 */
	public String getDisplayType() {
		return displayType;
	}

	/**
	 * @return the displayLength
	 */
	public String getDisplayLength() {
		return displayLength;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**XMLValidator
	 * 
	 * creates a new XMLValidator
	 * 
	 * this constructor is private.  it is only used by the configure method which takes its
	 * input from the specified xml file.  configure is the only way that this should be accessed
	 * 
	 * the main method of this class shows the recommended way to use this class
	 * 
	 * @param displayType - the type of DisplayMode to use
	 * @param displayLength - the verbosity of the DisplayMode
	 * @param outputFile - the output file, if configured
	 * @param XMLtoCheck - the path to the XML document to check
	 * @param XMLReference - the path to the reference XML document
	 * @param multiples - a map whose key is an element that may appear more than once, and whose value is the name of the attribute that differentiates them
	 */
	private XMLValidator(String displayType,
			String displayLength, 
			String outputFile, 
			String XMLtoCheck, 
			String XMLReference,
			Map<String, String> multiples)
	{
		this.displayType = displayType;
		this.displayLength = displayLength;
		this.outputFile = outputFile;
		this.XMLtoCheck = XMLtoCheck;
		this.XMLReference = XMLReference;
		this.multiples = multiples;
	}

	/**
	 * validateXML
	 * 
	 * validates an XML file against another reference XML file.
	 * <p>
	 * An element of the XML file under test is considered valid if
	 * <ol>
	 * 	<li>
	 * 		It appears in the reference XML file
	 * 	</li>
	 * 	<li>
	 * 		It's attributes appear in the corresponding XML reference element
	 * 	</li>
	 * </ol>
	 * </p>
	 * <br>
	 * This code assumes that the document has a single parent at the highest level, that is, 
	 * that the XML under test has one tag that encompasses the rest of the tags
	 * 
	 * It also performs a structural test to verify that the two xml documents have the same structure
	 * 
	 * @return - a String representing the end validation status of the XML under test
	 * 
	 * @throws XMLException when either an element or attribute of XMLtoCheck does not appear in the reference. This is optional depending on the DisplayMode
	 */
	public String validateXML() throws Exception
	{
		//constructs documents out of file paths
		Document ref = XMLUtilities.getXMLDoc(XMLReference);
		Document check = XMLUtilities.getXMLDoc(XMLtoCheck);
		newDisplayMode();

		//this block runs the check twice, in both directions, 
		//so as to capture both extra and missing elements
		display.addMessage("\n\nChecking reference against the migrated file\n\n");
		validatexml(ref.getDocumentElement(),check);
		display.addMessage("\n\nChecking migrated file against reference\n\n");
		validatexml(check.getDocumentElement(),ref);

		XMLAnalyzer xref = new XMLAnalyzer(ref);
		XMLAnalyzer xcheck = new XMLAnalyzer(check);

		display.addMessage("Checking structural integrity");
		display.structureTest(xref.hasEqualTree(xcheck), xref.printIfDifferent(xcheck, "string"));

		/*if (!xref.hasEqualTree(xcheck)) {
			xref.printDebug();
			xcheck.printDebug();
		}
		 */

		display.finishUp();
		return display.getState();
	}

	/**
	 * validateXML
	 * 
	 * implements the core of validateXML
	 * facilitates recursion
	 * 
	 * @param ref - the reference XML document
	 * @param check - the element under test
	 */
	private void validatexml(Node check, Document ref) throws Exception
	{
		boolean valid = true;
		boolean innerText = false;
		NamedNodeMap checkAttr;
		NodeList children;
		//only operate on elements
		if(check.getNodeType() == Node.ELEMENT_NODE)
		{
			Node refNode;
			//if check is in the map and is either a null or a text, skip it
			//if it is, safely ignore it, it will be caught in the structure test
			if(!(multiples.containsKey(((Element)check).getNodeName()) &&  // this is true when check is in the multiple map
					((multiples.get(((Element)check).getNodeName()) == null) ||  (multiples.get(((Element)check).getNodeName()).equals("TEXTTEXT"))))) //this is true when the element has a mapped value of either null, or text
			{

				//check for multiples
				if(multiples.containsKey(((Element)check).getNodeName()))
				{
					Node tempNode = check.getAttributes().getNamedItem(multiples.get(((Element)check).getNodeName()));
					//if the attribute can't be found, but the element was in the map, then this element was not in the 
					//reference document, and as such should be considered invalid.
					if(tempNode != null)
					{					
						// get the specific node by its unique attribute, specified in the map
						refNode = XMLUtilities.getNode(ref,((Element)check).getNodeName(),tempNode.toString());
					}
					else
					{
						refNode = null;
					}
				}
				else
				{
					// must be a single node, so get the first element with this tag
					refNode = ref.getElementsByTagName(((Element)check).getNodeName()).item(0);
				}
				
				//now we have the reference node

				//first checkpoint
				//does the refNode exist?
				if(refNode == null)
				{
					valid = false;
				}

				//second checkpoint
				//do the attributes of check appear in refNode?
				//this will also check to make sure that the data in the nodes is the same.
				checkAttr = check.getAttributes();
				//loop through all of check's attributes
				for(int i = 0; i < checkAttr.getLength() && valid; i++)
				{
					//if ref is missing the attribute, obviously its bad
					if(refNode.getAttributes().getNamedItem(checkAttr.item(i).getNodeName()) == null)
					{
						display.displayInvalid(checkAttr.item(i));
						valid = false;
					}
					else
					{
						//ref has the attribute, does it have the same value?
						if(!refNode.getAttributes().getNamedItem(checkAttr.item(i).getNodeName()).getNodeValue().equals(checkAttr.item(i).getNodeValue()))
						{
							// no it doesn't.  could be a problem.  print it
							display.displayInvalid(checkAttr.item(i),true,refNode.getAttributes().getNamedItem(checkAttr.item(i).getNodeName()).getNodeValue() + " vs " + checkAttr.item(i).getNodeValue());
							valid = false;
						}
						else
						{
							//yes, the attribute exists and has the same value
							display.displayValid(checkAttr.item(i));
						}
					}
				}

				//checking inner text nodes
				if(valid && //make sure that the node has not already been declared invalid
						check.hasChildNodes() && //make sure that the node has child nodes
						check.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE && //if the node has meaningful text content it will probably be the first node
						check.getChildNodes().item(0).getNodeValue().replaceAll("\\s", "").length() > 0) // make sure its not just whitespace characters
				{
					//the check node should contain the same text as the reference node.
					if(!check.getTextContent().replaceAll("\\s", "").equals(refNode.getTextContent().replaceAll("\\s", "")))
					{
						display.displayInvalid(check,true,check.getTextContent().replaceAll("\\s", "")+" vs "+refNode.getTextContent().replaceAll("\\s", ""));
					}
					else
					{
						display.displayValid(check);
					}
				}
				else
				{

					if(valid){
						//for verbose outputs
						display.displayValid(check);
					}
					else
					{
						display.displayInvalid(check);
					}
				}

			}
			//this is the second half of that first if statement
			//this one checks elements that have defining text children
			else if(multiples.containsKey(((Element)check).getNodeName()) && //make sure it's a multiple
					multiples.get(check.getNodeName()) != null && //make sure its not mapped to null
					multiples.get(((Element)check).getNodeName()).equals("TEXTTEXT")) //make sure it is mapped to text
			{
				//catches nodes that have been flagged as having meanigful text children
				//so we'll make sure that it shows up
				NodeList textList = ref.getElementsByTagName(check.getNodeName());
				String refText = "";
				String checkText = "";
				//first check, make sure ref has the check element
				if(textList != null)
				{
					//delete all the whitespace
					checkText = check.getTextContent().replaceAll("\\s", "");
					//check all the reference elements with the same name 
					//to make sure the check shows up
					for(int k = 0; k < textList.getLength() && !innerText; k++)
					{
						refText = textList.item(k).getTextContent().replaceAll("\\s", "");
						if(checkText.equals(refText))
						{
							innerText = true;
						}
					}
				}
				
				if(innerText)
				{
					display.displayValid(check);
				}
				else
				{
					display.displayInvalid(check,true,checkText);
				}
			}
			//last checkpoint
			//do all the children of this element appear in the reference doc?
			children = check.getChildNodes();
			for(int i = 0; i < children.getLength(); i++)
			{
				validatexml(children.item(i),ref);
			}
		}
	}

	/**
	 * configure
	 * 
	 * configures a list of XML validator based on the configuration file given, this includes creating the map
	 * and the {@linkplain displayModes.DisplayMode DisplayMode} object
	 * <br>
	 * acts as a factory for XMLValidators
	 * <br>
	 * sample XML config file
	 * <br>
	 * <pre>
	 * {@code
<?xml version="1.0" encoding="UTF-8"?>
<!--Contains configuration for XMLValidator-->

<config>
	<!-- output defines the output target as well as the verbosity level -->
	<!-- possible length: short verbose -->
	<!-- possible target: console file string exception -->
	<outputType length="short" target="file"/>

	<!-- if outputType target="file" file tag must be set target="path/filename" -->
	<file target="shortFile.log"/>

	<!--Batch mode-->
	<!--in this section, you can specify multiple XML files to check, along with their corresponding reference files-->
	<!-- the multiple tag defines elements in the XML to validate that can occur more than once and the attribute that uniquely defines them -->
	<!-- each reference should have its own set of multiple tags -->

	<reference file="ConnectionsXMLFiles/LotusConnections-config.xml">

		<checkFile file="ConnectionsXMLFiles/LotusConnections-configBAD.xml" />
		<checkFile file="ConnectionsXMLFiles/LotusConnections-configBADtag.xml" />
		<checkFile file="ConnectionsXMLFiles/LotusConnections-config.xml" />

		<multiple element="sloc:serviceReference" attr="serviceName" />

	</reference>

	<reference file="SampleXML/sample.xml">

		<checkFile file="SampleXML/sample.xml" />
		<checkFile file="SampleXML/sample2.xml" />


		<multiple element="person" attr="username" />

	</reference>
</config>
}
	 * </pre>
	 * 
	 * @param XMLconfig a string representing a path to an XML configuration file.
	 * @return an {@code ArrayList<XMLValidator>} of configured XMLValidators
	 */
	public static ArrayList<XMLValidator> configure(String XMLconfig)
	{
		//convert the XML file to a Document
		Document config = XMLUtilities.getXMLDoc(XMLconfig);

		//instance variables
		ArrayList<XMLValidator> validators = new ArrayList<XMLValidator>();
		String type, length, out="";
		String refFile;
		NodeList refNodes;
		ArrayList<String> check;
		XMLValidator validate;
		Map<String, String> map;

		//set the display type
		type = config.getElementsByTagName("outputType").item(0).getAttributes().getNamedItem("target").getNodeValue();
		//set how verbose the output will be
		length = config.getElementsByTagName("outputType").item(0).getAttributes().getNamedItem("length").getNodeValue();
		//if display goes out to a file, set the file name
		if(type.equalsIgnoreCase("file")){
			out = config.getElementsByTagName("file").item(0).getAttributes().getNamedItem("target").getNodeValue();}

		//get all the reference tags
		NodeList ref = config.getElementsByTagName("reference");

		//go through all the refernce tags
		for(int i=0; i < ref.getLength(); i++)
		{
			//create a new map for this reference
			map = new HashMap<String, String>();
			// create a list to hold the files to be checked against this reference
			check = new ArrayList<String>();

			//get all the child nodes of the reference
			refNodes = ref.item(i).getChildNodes();

			for(int j=0; j < refNodes.getLength(); j++)
			{
				//if the child node is a multiple tag, add it to the map,  
				//if the child is a file to check, add it to the list

				if(refNodes.item(j).getNodeName().equalsIgnoreCase("multiple"))
				{
					map.put(refNodes.item(j).getAttributes().getNamedItem("element").getNodeValue(), refNodes.item(j).getAttributes().getNamedItem("attr").getNodeValue() );
				}
				else if(refNodes.item(j).getNodeName().equalsIgnoreCase("checkFile"))
				{
					check.add(refNodes.item(j).getAttributes().getNamedItem("file").getNodeValue());
				}
			}
			//get the filepath for the reference
			refFile = ref.item(i).getAttributes().getNamedItem("file").getNodeValue();

			//create a new XMLValidator for each file to check, then add them to the list.
			for(String checkFile : check){
				validate = new XMLValidator(type, length, out, checkFile, refFile, map);
				validators.add(validate);
			}
		}

		return validators;

	}


	/**
	 * configure
	 * 
	 * overloads the regular configure method to provide a way to validate only two files 
	 * without having to start a file operation and create an XML file.
	 * 
	 * @param config - a Configuration object that holds parameters for this validation
	 * 
	 * @return a new XMLValidator based on the configuration supplied
	 * 
	 * @throws Exception - if either the reference file or the file to be checked is null
	 */
	public static XMLValidator configure(Configuration config) throws Exception
	{
		if(config.getRefMap() == null)
			config.refMap = new HashMap<String, String>();

		if(config.XMLcheck == null || config.XMLref == null)
			throw new Exception("File not specified");



		XMLValidator val = new XMLValidator(config.displayType,
				config.displayLength,
				config.outputFile,
				config.XMLcheck,
				config.XMLref,
				config.refMap);
		return val;
	}

	/**
	 * newDisplayMode
	 * 
	 *  acts as a DisplayMode factory method, creating new instances of the global display when needed
	 *  
	 *  
	 */
	private void newDisplayMode()
	{

		if(displayLength.equalsIgnoreCase("verbose"))
		{
			if(displayType.equalsIgnoreCase("file"))
			{
				try {
					display = new PrintToFileVerbose(outputFile);
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(displayType.equalsIgnoreCase("string"))
			{
				display = new StoreAsStringVerbose();
			}
			else if(displayType.equalsIgnoreCase("exception"))
			{
				display = new ThrowException();
			}
			else
			{
				display = new PrintToConsoleVerbose();
			}
		}
		else
		{
			if(displayType.equalsIgnoreCase("file"))
			{
				try {
					display = new PrintToFile(outputFile);
				} catch (DOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(displayType.equalsIgnoreCase("string"))
			{
				display = new StoreAsString();
			}
			else if(displayType.equalsIgnoreCase("exception"))
			{
				display = new ThrowException();
			}
			else
			{
				display = new PrintToConsole();
			}
		}
	}
}