package output;

/**
 * This interface defines the methods implemented
 * by various output classes. These classes 
 * handle the data output to the XMLAnalyzer and
 * XMLPrinter class
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-01
 *
 */

public interface Output {
	
	/**
	 * display
	 * 
	 * Takes in a string and handles it accordingly
	 * 
	 * @param str - (String) The string to be output
	 *
	 * @return void
	 */
	public void display(String str);
	
	/**
	 * viewDifferences
	 * 
	 * Returns a string that informs the viewer
	 * about the changes, or where they can see them
	 * 
	 * @return String
	 */
	public String viewDifferences();
}
