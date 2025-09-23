package displayModes;

import org.w3c.dom.Node;

import resources.XMLException;
import resources.XMLUtilities;

/**StoreAsString
 * 
 * provides an implementation of DisplayMode that 
 * store information given to it as a string.
 * 
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class StoreAsString implements DisplayMode {

	public String messages;
	
	public StoreAsString()
	{
		messages = "";
	}
	
	@Override
	/**
	 */
	public void displayInvalid(Node node) throws XMLException 
	{
		messages = messages.concat(XMLUtilities.getType(node) +  " is not valid :" + node.getNodeName() + attr(node)+"\n");
	}
	
	@Override
	/**
	 */
	public void displayInvalid(Node node, boolean data, String message) throws XMLException
	{
		if(data)
			messages = messages.concat("\nData mismatch: "+message+"\n");
		this.displayInvalid(node);
	}

	@Override
	/**
	 */
	public void displayValid(Node node) 
	{
		//do nothing
	}

	@Override
	/**
	 */
	public String finishUp() {
		
		return messages;
	}

	@Override
	public String getState() {
		return messages;
	}
	
	@Override
	public void structureTest(boolean bool, String message) {
		if (!bool) {
			messages = messages.concat("The file structures do not match" + "\n");
			messages = messages.concat(message+"\n");
		}
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
	public void addMessage(String message) {
		
	}

	@Override
	public void error(String message) {
		messages = messages.concat(message+"\n");		
	}
}
