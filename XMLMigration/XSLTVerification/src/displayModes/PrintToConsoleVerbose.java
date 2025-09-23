package displayModes;

import org.w3c.dom.Node;

import resources.XMLUtilities;

/**PrintToConsoleVerbose
 * 
 * provides an implementation of DisplayMode that 
 * sends its messages to the console and includes
 * more information than the short version
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class PrintToConsoleVerbose extends PrintToConsole {

	
	public PrintToConsoleVerbose()
	{
	}

	@Override
	public void displayValid(Node node) 
	{
		System.out.println(XMLUtilities.getType(node) + " is valid :" + node.getNodeName());
	}

	@Override
	public void structureTest(boolean bool, String message) {
		super.structureTest(bool, message);
		if (bool) {
			System.out.println("The file structures match");
			System.out.println(message);
		}
	}
	
	@Override
	public void addMessage(String message)
	{
		System.out.println(message);
	}
}
