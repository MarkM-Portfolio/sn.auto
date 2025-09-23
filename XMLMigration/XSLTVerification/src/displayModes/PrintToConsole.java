package displayModes;

import org.w3c.dom.Node;

import resources.XMLException;
import resources.XMLUtilities;

/**PrintToConsole
 * 
 * provides an implementation of DisplayMode that 
 * sends its messages to the console
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class PrintToConsole implements DisplayMode {

	public PrintToConsole() {

	}

	@Override
	public void displayInvalid(Node node) throws XMLException {
		System.out.println("!WARNING! " + XMLUtilities.getType(node)
				+ " is not valid :" + node.getNodeName());
	}

	@Override
	public void displayValid(Node node) {
		// do nothing

	}

	@Override
	public String finishUp() {
		System.out.println("Validation Complete");
		return "Validation Complete";
	}

	@Override
	public String getState() {
		return "Please check the console";
	}

	@Override
	public void structureTest(boolean bool, String message) {
		if (!bool) {
			System.out.println("The file structures do not match");
			System.out.println(message);
		}
	}

	@Override
	public void addMessage(String message) {
		//do nothing
	}

	@Override
	public void error(String message) {
		System.out.println("ERROR " + message);
	}

	
	@Override
	public void displayInvalid(Node node, boolean data, String message) throws XMLException {
		if(data)
			System.out.println("Data Mismatch: " + message);
		this.displayInvalid(node);
	}

}
