package displayModes;

import org.w3c.dom.Node;

import resources.XMLException;

/**
 * interface DisplayMode
 * 
 * DisplayMode defines communication between XMLValidator and the rest of the world. 
 * XMLValidator uses a DisplayMode object to communicate the validation status of the 
 * node that it is currently looking at.  
 * 
 * At any time, getState can return a string that
 * represents the validation status of the check XML attached to the XMLValidator by way
 * of the DisplayMode object.  
 * 
 * FinishUp signals to the DisplayMode that there will be no
 * more calls made to displayValid or displayInvalid.  This enables the display mode to
 * change its state, print a final message, close a file, etc.. 
 * 
 * The XMLValidator validate method calls finshUp and the returns its DisplayMode.getState()
 * 
 * Right now, the difference between verbose output and shortened output is that the 
 * short output DisplayMode implementations have empty displayValid methods.
 * 
 * @author Mike Della Donna (mpdella@us.ibm.com) 
 *
 */
public interface DisplayMode
{
	
	/**
	 * displayInvalid
	 * 
	 * displays information about nodes that fail validation
	 * 
	 * @param node - the Node being checked
	 * @throws XMLException - it's possible to throw an exception at the first difference
	 */
	public void displayInvalid(Node node) throws XMLException;
	
	/**
	 * displayInvalid
	 * 
	 * displays information about nodes that fail validation
	 * 
	 * @param node - the Node being checked
	 * @param data - a boolean indicating whether or not the failure is caused by a data mismatch
	 * @throws XMLException - it's possible to throw an exception at the first difference
	 */
	public void displayInvalid(Node node, boolean data, String message) throws XMLException;
	
	/**
	 * displayValid
	 * 
	 * displays information about nodes that pass validation
	 * 
	 * @param node - the Node being checked
	 */
	public void displayValid(Node node);

	/**
	 * finishUp
	 * 
	 * Signals to the display object that the validation checking process has completed.
	 * finishUp returns a string summary of the validation
	 * 
	 * ------------------------------------------------------------------------------------
	 * A call to this function signals to the DsplayMode that no more calls to displayValid
	 * or displayInvalid will be made.  the behavior of those functions after calling finishUp 
	 * is undefined.
	 * ------------------------------------------------------------------------------------
	 * 
	 * @return - a String that represents a summary of the validation process
	 */
	public String finishUp();
	
	/**
	 * getState
	 * 
	 * returns a string representation of the state of this displayMode
	 * can be used after finishUp to get the final validation status of
	 * the XML file
	 * 
	 * @return - a String representing the state of the DisplayMode
	 */
	public String getState();
	
	/**
	 * structureTest
	 * 
	 * takes in a boolean resulting from a structure test
	 * between two XML files that whose element structures
	 * are being compared in XMLAnalyzer objects. XMLAnalyzer
	 * has a method that will return the boolean to be used here.
	 * 
	 * 
	 * @param boolean
	 * @param String message
	 */
	public void structureTest(boolean bool, String message);
	
	/**
	 * addMessage
	 * 
	 * adds a generic message to the display.  May or may not
	 * be displayed to the user.
	 * 
	 * @param String message
	 */
	public void addMessage(String message);
	
	/**
	 * error
	 * 
	 * adds an error message to the display.  This should 
	 * be shown to the user
	 * 
	 * @param String message
	 */
	public void error(String message);
}
