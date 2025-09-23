package resources;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtilities {

	/**
	 * getType
	 * 
	 * returns a string representing the type of this node.
	 * NodeType is stored as a short, this uses the static fields of {@linkplain org.w3c.don.Node Node}
	 * to return the string representation
	 * 
	 * @param node - the node on which to determine the type
	 * @return - a string representing the type of this node
	 */
	public static String getType(Node node)
	{
		String type;
		
		switch(node.getNodeType())
		{
		case Node.ELEMENT_NODE:
			type = "Element"; break;
		case Node.ATTRIBUTE_NODE:
			type = "Attribute"; break;
		default:
			type = "unknown";
		}
		return type;
	}
	
	 /**
	  * applyStyleSheet
	  * 
	  * @param String XMLToApplyXSL
	  * @param String XSLFile
	  * @return The path/name of the transformed XML document relative to XMLToApplyXSL
	  * @throws Exception
	  */
	 public static String applyStyleSheet(String XMLToApplyXSL, String XSLFile) throws Exception 
	 {

	 		 String appliedXSL = (XMLToApplyXSL.substring(0,XMLToApplyXSL.length() - 4))+"New.xml";


	 		   try {
	 		 		     TransformerFactory tFactory = TransformerFactory.newInstance();
	 		 		     Transformer transformer = tFactory.newTransformer
	 		 		          (new javax.xml.transform.stream.StreamSource(XSLFile));

	 		 		     transformer.transform
	 		 		       (new javax.xml.transform.stream.StreamSource
	 		 		             (XMLToApplyXSL),
	 		 		        new javax.xml.transform.stream.StreamResult
	 		 		             ( new FileOutputStream(appliedXSL)));
	 		 		     }
	 		 		   catch (Exception e) {
	 		 		     e.printStackTrace( );
	 		 		     }

	 		 		 return appliedXSL;

	 }

	 /**
	  * getXMLDoc
	  * @param XMLFile - a string representing a path to an XML file
	  * @return a Document that represents the XML file
	  */
	 public static Document getXMLDoc(String XMLFile){

	 		 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	 		 DocumentBuilder docBuilder;
	 		 Document doc = null;
	 		 File file;
	 		 try {
	 		 		 docBuilder = docBuilderFactory.newDocumentBuilder();
	 		 file = new File(XMLFile);
	 		 doc = docBuilder.parse (file);
	 		 doc.getDocumentElement().normalize();
	 		 } catch (ParserConfigurationException e) {
	 		 		 System.out.println("ParserConfigurationException");
	 		 		 e.printStackTrace();
	 		 } catch (SAXException e) {
	 		 		 System.out.println("SAXException");
	 		 		 e.printStackTrace();
	 		 } catch (Exception e) {
	 		 		 System.out.println("OtherException");
	 		 		 e.printStackTrace();
	 		 }
	 		 return doc;

	 }


	 /**
	  * getNode
	  * 
	  * returns a element based on its tag name and attribute
	  * 
	  * @param Document doc - the document to search
	  * @param String XML_Element
	  * @param String Attribute
	  * @return Node 
	  * @throws Exception
	  */
	 public static Node getNode(Document doc, String XML_Element, String Attribute) throws Exception 
	 {

		Node nodeUnderTest = null;
       int totalServiceRef;
       int totalAttributes;

	     try {


       // normalize text representation

       NodeList listOfServiceReference = doc.getElementsByTagName(XML_Element);
       totalServiceRef = listOfServiceReference.getLength();
       for(int ServRef=0; ServRef< totalServiceRef ; ServRef++)
       {
	             totalAttributes = ((Node)listOfServiceReference.item(ServRef)).getAttributes().getLength();
	             for(int Attr=0; Attr< totalAttributes ; Attr++)
	             {
	             		 //add the attribute to a list
	                 if (((Node)listOfServiceReference.item(ServRef)).getAttributes().item(Attr).toString().startsWith(Attribute))
	                 {		
	                	 nodeUnderTest = ((Node)listOfServiceReference.item(ServRef));
                		 Attr = totalAttributes;
                		 ServRef = totalServiceRef;
	                 }
	             }
       }
   }catch (Throwable t) {
   t.printStackTrace ();
   }

   return nodeUnderTest;

	 }


	 /**
	  * getElementAttr
	  * @param XML_Element
	  * @param Attribute
	  * @return
	  * @throws Exception
	  */
	public static ArrayList<String> getElementAttr(Document doc, String XML_Element, String Attribute) throws Exception {

   ArrayList<String>  ServRefAttr = new ArrayList<String>();
   int totalServiceRef;
   int totalAttributes;

	     try {

       // normalize text representation
	     		 doc.getDocumentElement ().normalize ();
	     		 System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
       NodeList listOfServiceReference = doc.getElementsByTagName(XML_Element);
       totalServiceRef = listOfServiceReference.getLength();
       
       for(int ServRef=0; ServRef< totalServiceRef ; ServRef++)
       {
            totalAttributes = ((Node)listOfServiceReference.item(ServRef)).getAttributes().getLength();
            
            for(int Attr=0; Attr< totalAttributes ; Attr++)
            {
           	 //find node via attribute
                if (((Node)listOfServiceReference.item(ServRef)).getAttributes().item(Attr).toString().startsWith(Attribute))
                		 ServRefAttr.add(((Node)listOfServiceReference.item(ServRef)).getAttributes().item(Attr).toString());
            }
       }
   }catch (Throwable t) {
   t.printStackTrace ();
   }

	 		 return ServRefAttr;

	 }


	 /**
	  * validateXSD
	  * @param XMLtoMigrate
	  * @param XSDtoValidate
	  * @throws Exception
	  */
	 public static void validateXSD(String XMLtoMigrate, String
XSDtoValidate) throws Exception {

	 		 Source xmlFile = new StreamSource(new File(XMLtoMigrate));
	 		 Source schemaFile = new StreamSource(new File(XSDtoValidate));

	 		 SchemaFactory schemaFactory = SchemaFactory.newInstance
(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	 		 Schema schema = schemaFactory.newSchema(schemaFile);
	 		 Validator validator = schema.newValidator();

	 		 try{
	 		   validator.validate(xmlFile);
	 		   System.out.println(xmlFile.getSystemId() + " is valid");
	 		 }catch(SAXException e){
	 		   System.out.println(xmlFile.getSystemId() + " is NOT valid");
	 		   System.out.println("Reason: " + e.getLocalizedMessage());
	 		 }
	 }

	 /**
	  * printXMLTree
	  * prints out the elements of an XML file
	  * 
	  * @param XMLFile - String - a string representing a path to the XML file to be printed
	  */
	 public static void printXMLTree(String XMLFile)
	 {
		 Document doc = getXMLDoc(XMLFile);
		 printTree(doc.getDocumentElement(), 0);
	 }
	 
	 /**
	  * printTree
	  * print this element and calls itself on this element's children
	  * 
	  * @param node - Node - the element to print
	  * @param level - int - the current depth of the Tree
	  */
	 private static void printTree(Node node, int level)
	 {
		 node.normalize();
		 String spacer = "";
		 for(int j = 0; j < level; j++)
			 spacer = spacer.concat("  ");
		 
		 if(node.getNodeType() == Node.ELEMENT_NODE){
			 System.out.print(spacer +""+ node.getNodeName() + " Attr: ");
			 NamedNodeMap list = ((Element)node).getAttributes();
			 for(int k = 0; k < list.getLength(); k++)
			 {
				 System.out.print(" " + list.item(k).getNodeName() + " ");
			 }
			 System.out.println();
		 }
		 
		 NodeList nlist = node.getChildNodes();
		 for(int i = 0; i < nlist.getLength(); i++)
		 {
			 printTree(nlist.item(i), level+1);
		 }
	 } 
}
