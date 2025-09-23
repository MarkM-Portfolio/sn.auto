package displayModes;

import org.w3c.dom.Node;

import resources.XMLException;
import resources.XMLUtilities;

/**ThrowException
 * 
 * provides an implementation of DisplayMode that 
 * throws an exception when there is an error
 * 
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com)
 *
 */
public class ThrowException implements DisplayMode 
{

	public ThrowException()
	{
	}
	
	@Override
	/**
	 */
	public void displayInvalid(Node node) throws XMLException
	{
		throw (new XMLException(XMLUtilities.getType(node) + " not found in reference ",node));
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
	public String finishUp() 
	{
		return "Validation completed successfully";
	}

	@Override
	public String getState() {
		return "No Errors";
	}
	
	@Override
	public void structureTest(boolean bool, String message) {
		if (!bool) {
			throw (new RuntimeException("The file structures do not match"));
		}
	}

	@Override
	public void addMessage(String message) {		
	}

	@Override
	public void error(String message) {
		throw new RuntimeException("Program encountered fatal error");
	}

	@Override
	public void displayInvalid(Node node, boolean data, String message) throws XMLException {
		throw new XMLException("Data Mismatch",node);
	}
	
	
}
