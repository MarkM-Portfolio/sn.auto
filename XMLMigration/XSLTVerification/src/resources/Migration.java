package resources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Represents the complete sequence of a an XML file migration
 * 
 * <ul>
 * <li>Load the initial File</li>
 * <li>Transform the file with the specified stylesheet</li>
 * <li>Validate the newly transformed file against the provided schema</li>
 * <li>Compare the newly transformed file against a provided reference</li>
 * </ul>
 * 
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class Migration {

	private String XMLcheck;
	private String XMLref;
	private String XSD;
	private String XSL;
	private XMLValidator val; 
	
	/**
	 * parameter 1 is a path to a configuration file
	 * 
	 * initializes migration objects, then uses their migration method
	 * 
	 * 
	 * @param args
	 * @throws SAXException
	 */
	public static void main(String args[]) throws SAXException
	{
		ArrayList<Migration> migrators = new ArrayList<Migration>();
		
		try{
			//get a list of Migrators from the configuration file
			migrators = createMigrations(args[0]);
		} catch (Exception e){
			System.out.println(e.getLocalizedMessage());
		}
		
		//run migrate on every Migrator
		for(Migration m : migrators)
		{
			m.migrate();
		}
	}
	
	/**
	 * @param xMLcheck
	 * @param xMLref
	 * @param xSD
	 * @param xSL
	 * @param val
	 */
	private Migration(String xMLcheck, String xMLref, String xSD, String xSL,
			XMLValidator val) {
		XMLcheck = xMLcheck;
		XMLref = xMLref;
		XSD = xSD;
		XSL = xSL;
		this.val = val;
	}

	/**
	 * performs a migration sequence
	 * @return indicating the status of the migration
	 */
	public String migrate()
	{
		String migrationMessage = "";
		String migratedXML ="";
		
		if(XMLcheck != null && new File(XMLcheck).isFile())
		{
			try {
				if(XSL != null && new File(XSL).isFile())
				{
					try {
						migratedXML = XMLUtilities.applyStyleSheet(XMLcheck,XSL);
						migrationMessage = migrationMessage.concat("File migrated successfully: "+migratedXML+"\n");
					} catch (Exception e){
						migrationMessage = migrationMessage.concat("Error during stylesheet transformation: "+e.getLocalizedMessage()+"\n");
					}
				}
				else
				{
					migrationMessage = migrationMessage.concat("No stylesheet specified. Skipping transformation.\n");
				}
				
			} catch (Exception e) {
				migrationMessage = migrationMessage.concat("Stylesheet error. Skipping transformation.\n");
			}
			
			
			try {
				if(XSD != null && new File(XSD).isFile())
				{
					try{
						if(new File(migratedXML).isFile())
							migrationMessage = migrationMessage.concat("Schema applied: "+XMLUtilities.validateXSD(migratedXML, XSD)+"\n");
					}
					catch (Exception e)
					{
						migrationMessage = migrationMessage.concat("Schema applied: "+XMLUtilities.validateXSD(XMLcheck, XSD)+"\n");
					}
					
				}
				else
				{
					migrationMessage = migrationMessage.concat("No schema specified. Skipping check.\n");
				}
			} catch (Exception e) {
				migrationMessage = migrationMessage.concat("Schema error: "+e.getLocalizedMessage()+"\n");
			}
					
			try {
				if(XMLref != null && new File(XMLref).isFile())
				{
					Configuration config;
					if(new File(migratedXML).isFile()){
						config = new Configuration(migratedXML, val.getXMLReference(), val.getDisplayType(), val.getDisplayLength(), val.getOutputFile());
					}
					else{
						config = new Configuration(XMLcheck, val.getXMLReference(), val.getDisplayType(), val.getDisplayLength(), val.getOutputFile());
					}
					
					
					
					val = XMLValidator.configure(config);
					
					String state = val.validateXML();
					
					
					migrationMessage = migrationMessage.concat("Xml validated:"+state+"\n");
					
				}
				else
				{
					migrationMessage = migrationMessage.concat("Reference file not specified, skipping validation.\n");
				}
				
			} catch (Exception e) {
				migrationMessage = migrationMessage.concat("Reference file error, skipping validation: "+e.getLocalizedMessage()+"\n");
			}
		}
		else
		{
			migrationMessage = "Unable to load file";
		}
		
		
		migrationMessage = migrationMessage.concat("\n");
		
		return migrationMessage;
	}
	
	/**
	 * creates a new Migration from a Configuration object
	 * 
	 * @param config
	 * @return a new Migration object
	 * @throws Exception
	 */
	public static Migration createMigration(Configuration config) throws Exception
	{
		return new Migration(config.XMLcheck, config.XMLref, config.XSD, config.XSL, XMLValidator.configure(config));
	}
	
	/**
	 * creates and initializes a list of migration objects from a configuration file
	 * 	 * <br>
	 * sample XML config file
	 * <br>
	 * <pre>
	 * {@code
<?xml version="1.0" encoding="UTF-8"?>
<!--Contains configuration for XMLValidator-->

<?xml version="1.0" encoding="UTF-8"?>
<!--Contains configuration for XMLValidator-->

<config>
	<!-- output defines the output target as well as the verbosity level -->
	<!-- possible length: short verbose -->
	<!-- possible target: console file string exception -->
	<outputType length="short" target="file"/>
	
	<!-- if outputType target="file" file tag must be set target="path/filename" -->
	<file target="C:\Users\IBM_ADMIN\xmlmigration\shortFile.log"/>
	
	<!--Batch mode-->
	<!--in this section, you can specify multiple XML files to check, along with their corresponding reference files-->
	<!-- the multiple tag defines elements in the XML to validate that can occur more than once and the attribute that uniquely defines them -->
	<!-- if a tag occurs multiple times and does not contain a unique attribute, specify this by attr=null -->
	<!-- each reference should have its own set of multiple tags -->
	<!-- each reference also contains a file path for an XSL stylesheet transformation file -->
	<!-- as well as a file path to an XSD schema definition file -->
	
	<reference file="C:\Users\IBM_ADMIN\xmlmigration\LotusConnectionsConfig\LotusConnections-config4.xml">
	
		<checkFile file="C:\Users\IBM_ADMIN\xmlmigration\LotusConnectionsConfig\LotusConnections-config3.xml" />
		<checkFile file="C:\Users\IBM_ADMIN\xmlmigration\LotusConnectionsConfig\LotusConnections-config301.xml" />
		
		<xsl file="C:\Users\IBM_ADMIN\xmlmigration\LotusConnectionsConfig\LotusConnections-config-update-301-40.xsl" />
		
		<xsd file="C:\Users\IBM_ADMIN\xmlmigration\LotusConnectionsConfig\LotusConnections-config.xsd" />
		
		<multiple element="sloc:serviceReference" attr="serviceName" />
	</reference>

</config>
}
	 * </pre>
	 * @param config - a path to a valid configruation file
	 * @return a list of Migration objects
	 * @throws Exception
	 */
	public static ArrayList<Migration> createMigrations(String config) throws Exception
	{
		Document con;
		con = XMLUtilities.getXMLDocNoCatch(config);
		
		
		
		//instance variables
		ArrayList<Migration> migrators = new ArrayList<Migration>();
		String type, length, out="";
		String refFile;
		NodeList refNodes;
		ArrayList<String> check;
		Migration migrate = null;
		Map<String, String> map;
		Configuration conf = new Configuration();
		
		//set the display type
		type = con.getElementsByTagName("outputType").item(0).getAttributes().getNamedItem("target").getNodeValue();
		conf.setDisplayType(type);
		
		//set how verbose the output will be
		length = con.getElementsByTagName("outputType").item(0).getAttributes().getNamedItem("length").getNodeValue();
		conf.setDisplayLength(length);
		
		//if display goes out to a file, set the file name
		if(type.equalsIgnoreCase("file")){
			out = con.getElementsByTagName("file").item(0).getAttributes().getNamedItem("target").getNodeValue();}
		conf.setOutputFile(out);
		
		//get all the reference tags
		NodeList ref = con.getElementsByTagName("reference");

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
				//if the child is the XSD filepath, add it
				//if the child is the XSL filepath, add it
				
				if(refNodes.item(j).getNodeName().equalsIgnoreCase("multiple"))
				{
					map.put(refNodes.item(j).getAttributes().getNamedItem("element").getNodeValue(), refNodes.item(j).getAttributes().getNamedItem("attr").getNodeValue() );
				}
				else if(refNodes.item(j).getNodeName().equalsIgnoreCase("checkFile"))
				{
					check.add(refNodes.item(j).getAttributes().getNamedItem("file").getNodeValue());
				}
				else if(refNodes.item(j).getNodeName().equalsIgnoreCase("xsl"))
				{
					conf.setXSL(refNodes.item(j).getAttributes().getNamedItem("file").getNodeValue());
				}
				else if(refNodes.item(j).getNodeName().equalsIgnoreCase("xsd"))
				{
					conf.setXSD(refNodes.item(j).getAttributes().getNamedItem("file").getNodeValue());
				}
			}
			//get the filepath for the reference
			refFile = ref.item(i).getAttributes().getNamedItem("file").getNodeValue();
			// set the new ref file Configuration
			conf.setXMLref(refFile);
			
			//set the map
			conf.setRefMap(map);
			
			//create a new Migration for each file to check, then add them to the list.
			for(String checkFile : check){
				conf.setXMLcheck(checkFile);
				try {
					migrate = Migration.createMigration(conf);
				} catch (Exception e) {
					e.printStackTrace();
				}
				migrators.add(migrate);
			}
		}
		
		return migrators;
	}

	/**
	 * @return String - the path to the file to be migrated
	 */
	public String getXMLtoCheck() {
		return XMLcheck;
	}
	
	
	/**
	 * @return String that is the outputFile that will be printed to.  only applicable if the contained XMLValidator is set to print to a file
	 */
	public String getOutputFile()
	{
		if(val != null)
		{
			if(val.getOutputFile() != null && !val.getOutputFile().equals(""))
			{
				return val.getOutputFile();
			}
			else
			{
				return "file not specified";
			}
		}
		return "file not specified";
	}
}
