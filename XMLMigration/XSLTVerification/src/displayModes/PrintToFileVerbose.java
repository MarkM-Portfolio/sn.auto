package displayModes;

import java.io.IOException;

import org.w3c.dom.Node;

import resources.XMLUtilities;

/**PrintToConsoleVerbose
 * 
 * provides an implementation of DisplayMode that 
 * sprints information to a log file
 * the information is appended to the end of the file 
 * with a date stamp
 * provides more information than the short version
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class PrintToFileVerbose extends PrintToFile {

	public PrintToFileVerbose(String fileName) throws IOException {
		super(fileName);
	}

	
	@Override
	/**
	 */
	public void displayValid(Node node) {
		super.displayValid(node);
		try {
			file.write("    "+XMLUtilities.getType(node) + " is valid :" + node.getNodeName() );
			file.newLine();
		} catch (IOException e) {
		}
	}
	
	@Override
	public void structureTest(boolean bool, String message) {
		super.structureTest(bool, message);
		
		if (bool) {
			try {
				file.write(structure);
				file.newLine();
				message.replaceAll("\n", System.getProperty("line.separator"));
				file.write(message);
				file.newLine();
			} catch (IOException e) {
				e.getLocalizedMessage();
			}
		}
	}
	
	@Override
	public void addMessage(String message) {
		try {
			message.replaceAll("\n", System.getProperty("line.separator"));
			file.write(message);
			file.newLine();
		} catch (IOException e) {
		}
	}
}
