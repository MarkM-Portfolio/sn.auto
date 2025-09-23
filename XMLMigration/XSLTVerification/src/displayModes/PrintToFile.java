package displayModes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import resources.XMLException;
import resources.XMLUtilities;

/**PrintToFile
 * 
 * provides an implementation of DisplayMode that 
 * prints information to a log file
 * the information is appended to the end of the file 
 * with a date stamp
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class PrintToFile implements DisplayMode {

	BufferedWriter file;
	int errors;
	String fileLocation;
	String structure;
	
	/**
	 * constructs a new PrintToFile that prints node information to  a file.
	 * 
	 * @param fileName name of the file to write to
	 * @throws IOException - if creating the file fails
	 */
	public PrintToFile(String fileName) throws IOException
	{
		errors = 0;
		fileLocation = null;
		DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		Date date = new Date();
		file = new BufferedWriter(new FileWriter(fileName, true));
		file.newLine();
		file.newLine();
		file.newLine();
		file.write(format.format(date));
		file.newLine();
	}
	
	@Override
	/**
	 */
	public void displayInvalid(Node node) throws XMLException {
		errors++;
		if(node.getNodeType() == Node.ELEMENT_NODE)
			fileLocation = node.getBaseURI();
			
		try {
			file.write(XMLUtilities.getType(node) +  " is not valid: " + node.getNodeName() + attr(node));
			file.newLine();
		} catch (IOException e) {
			throw new XMLException("Error writing to file for invalid node: ", node);
		}
	}
	
	@Override
	public void displayInvalid(Node node, boolean data, String message) throws XMLException 
	{
		if(data)
		{
			try {
				file.newLine();
				file.write("Data Mismatch: "+message);
				file.newLine();
			} catch (IOException e) {
				throw new XMLException("Error writing to file for invalid node: ", node);
			}
		}
		this.displayInvalid(node);
		
	}
	
	@Override
	/**
	 */
	public void displayValid(Node node) {

		if(fileLocation == null && node.getNodeType() == Node.ELEMENT_NODE)
			fileLocation = node.getBaseURI();

	}

	@Override
	/**
	 */
	public String finishUp()
	{
		try 
		{
			if(errors > 0){
				file.write(errors + " invalid nodes were identified in "+ fileLocation);}
			else{
				file.write("Validation complete, no errors reported in "+ fileLocation);}
			file.newLine();
			file.close();
			return "File closed successfully";
		}
		catch (IOException e)
		{
			return e.getLocalizedMessage();
		}
	}

	@Override
	/**
	 * 
	 */
	protected void finalize() throws Throwable {
	    try {
	        file.close();        // close open files
	    } finally {
	        super.finalize();
	    }
	}
	
	public String toString()
	{
		return "Printing to "+file;
	}

	@Override
	public String getState() {
		return "Found "+errors+" differences in "+fileLocation + "\n" + structure;
	}
	
	@Override
	public void structureTest(boolean bool, String message) {
		if (bool)
		{structure = "The file structures match.";}
		else
		{structure = "The file structures do not match.";}
		
		if (!bool) {
			try {
				file.write(structure);
				file.newLine();
				message.replaceAll("\n", System.getProperty("line.separator", "\r\n"));
				file.write(message);
				file.newLine();
			} catch (IOException e) {
				e.getLocalizedMessage();
			}
		}
	}

	@Override
	public void addMessage(String message) {
		
	}
	
	private String attr(Node node)
	{
		String temp = "";
		if(node.hasAttributes())
		{
			temp = " with attr: ";
			for(int i = 0; i < node.getAttributes().getLength(); i++)
			{
				temp = temp.concat(node.getAttributes().item(i).getNodeName() + "=" + node.getAttributes().item(i).getNodeValue()+", ");
			}
		}
		return temp;
	}

	@Override
	public void error(String message) {
		try {
			file.write("__ERROR__");
			file.newLine();
			message.replaceAll("\n", System.getProperty("line.separator"));
			file.write(message);
			file.newLine();
		} catch (IOException e) {
		}
	}

}
