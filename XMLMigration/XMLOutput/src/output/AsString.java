package output;

/**
 * This class implements the Output interface, 
 * and handles storing information as a string
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-01
 */
public class AsString implements Output {

	
	/**
	 * variables
	 */
	String info;
	
	/**
	 * constructor
	 */
	public AsString() {
		this.info = "";
	}
	
	/**
	 * display
	 * 
	 * In this class, display doesn't 
	 * "display" the given string, it 
	 * stores it.
	 * 
	 * @param str - (String) The string to store
	 */
	@Override
	public void display(String str) {
		if (str == "\n") {
			info = info.concat(str);
		}
		
		else {
		info = info.concat(str + "\n");
		}
	}

	@Override
	public String viewDifferences() {
		return info;
	}
}
