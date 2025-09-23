package displayModes;

import org.w3c.dom.Node;

import resources.XMLUtilities;

/**StoreAsStringVerbose
 * 
 * provides an implementation of DisplayMode that 
 * store information given to it as a string.
 * prints more information than the short version
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */ 
public class StoreAsStringVerbose extends StoreAsString 
{

	public StoreAsStringVerbose()
	{
		super();
	}

	@Override
	/**
	 */
	public void displayValid(Node node) 
	{
		messages = messages.concat(XMLUtilities.getType(node) + " is valid :" + node.getNodeName() + "\n");
	}
	
	@Override
	public void structureTest(boolean bool, String message) {
		super.structureTest(bool, messages);
		
		if (bool) {
			messages = messages.concat("The file structures match." + "\n");
			messages = messages.concat(message+"\n");
		}
	}
	
	@Override
	public void addMessage(String message)
	{
		messages = messages.concat(message+"\n");
	}
}
