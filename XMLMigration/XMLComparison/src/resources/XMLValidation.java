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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLValidation {

		 /**
		  * applyStyleSheet
		  * @param XMLToApplyXSL
		  * @param XSLFile
		  * @return
		  * @throws Exception
		  */
		 public static String applyStyleSheet(String XMLToApplyXSL, String
XSLFile) throws Exception {

		 		 String appliedXSL = (XMLToApplyXSL.substring(0,
XMLToApplyXSL.length() - 4))+"New.xml";


		 		   try {
		 		 		     TransformerFactory tFactory = TransformerFactory.
newInstance();
		 		 		     Transformer transformer = tFactory.newTransformer
		 		 		          (new javax.xml.transform.stream.StreamSource
		 		 		          (XSLFile));

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
		  * @param XMLFile
		  * @return
		  */
		 public static Document getXMLDoc(String XMLFile){

		 		 DocumentBuilderFactory docBuilderFactory =
DocumentBuilderFactory.newInstance();
		 		// docBuilderFactory.setNamespaceAware(true);
		 		 DocumentBuilder docBuilder;
		 		 Document doc = null;
		 		 try {	 
		 			 docBuilder = docBuilderFactory.newDocumentBuilder();

		 		 doc = docBuilder.parse (new File(XMLFile));
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
		  * @param XML_Element
		  * @param Attribute
		  * @return
		  * @throws Exception
		  */
		 public static Node getNode(Document doc, String XML_Element, String
Attribute) throws Exception {

		 		 Node nodeUnderTest = null;
        int totalServiceRef;
        int totalAttributes;

		     try {


            // normalize text representation

            NodeList listOfServiceReference = doc.getElementsByTagName
(XML_Element);
            totalServiceRef = listOfServiceReference.getLength();
            for(int ServRef=0; ServRef< totalServiceRef ; ServRef++){

		             totalAttributes = ((Node)listOfServiceReference.item
(ServRef)).getAttributes().getLength();
		             for(int Attr=0; Attr< totalAttributes ; Attr++){
		             		 //add the attribute to a list
		                 if (((Node)listOfServiceReference.item
(ServRef)).getAttributes().item(Attr).toString().startsWith(Attribute))
		                 		 {nodeUnderTest = ((Node)listOfServiceReference.item
(ServRef));
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
		 public static ArrayList<String> getElementAttr(Document doc, String
XML_Element, String Attribute) throws Exception {

        ArrayList<String>  ServRefAttr = new ArrayList<String>();
        int totalServiceRef;
        int totalAttributes;

		     try {

            // normalize text representation
		     		 doc.getDocumentElement ().normalize ();
		     		 System.out.println("Root element of the doc is " +
            		 		 doc.getDocumentElement().getNodeName());
            NodeList listOfServiceReference = doc.getElementsByTagName
(XML_Element);
            totalServiceRef = listOfServiceReference.getLength();
            for(int ServRef=0; ServRef< totalServiceRef ; ServRef++){

		             totalAttributes = ((Node)listOfServiceReference.item
(ServRef)).getAttributes().getLength();
		             for(int Attr=0; Attr< totalAttributes ; Attr++){
		             		 //find node via attribute
		                 if (((Node)listOfServiceReference.item
(ServRef)).getAttributes().item(Attr).toString().startsWith(Attribute))
		                 		 ServRefAttr.add(((Node)listOfServiceReference.item
(ServRef)).getAttributes().item(Attr).toString());
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
		  * @param XMLFile - a string representing a path to the XML file to be printed
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
		  * @param node - the element to print
		  */
		 public static void printTree(Node node, int level)
		 {
			 node.normalize();
			 String spacer = "";
			 for(int j = 0; j < level; j++)
				 spacer = spacer.concat("   ");

			 if(node.getNodeType() == Node.ELEMENT_NODE){
				 System.out.println(spacer +""+ node.getNodeName());}

 			 NodeList nlist = node.getChildNodes();
     		 for(int i = 0; i < nlist.getLength(); i++)
     		 {
     			 printTree(nlist.item(i), level+1);
     		 }
		 }
		 
		 public static void main(String[] args) {
			 String file = "C:/Users/IBM_ADMIN/xmlmigration/Config/LotusConnectionsConfig/LotusConnections-config3.xml";
			 String cfile = "C:/Users/IBM_ADMIN/xmlmigration/Config/LotusConnectionsConfig/LotusConnections-config3New.xml";
			 Document xdoc = getXMLDoc(file);
			 Document cdoc = getXMLDoc(cfile);
			 String element = "sloc:serviceReference";
			 String attr = "serviceName";
			 try {
				Node n = getNode(xdoc, element, attr);
				System.out.println(n.getNodeName() + "found!");
				System.out.println(n.getAttributes().getNamedItem(attr).getNodeValue());
				
				Node cnode = XMLValidation.getNode(cdoc, element, attr);
				System.out.println(cnode.getNodeName() + " found! CEEE");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }

}