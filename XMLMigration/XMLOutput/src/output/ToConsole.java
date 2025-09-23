package output;

/**
 * This class implements the Output interface, 
 * and handles printing to console
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-01
 */
public class ToConsole implements Output {

	/**
	 * 
	 */
	@Override
	public void display(String str) {
		if (str == "\n") {
			System.out.println();
		}
		else {
			System.out.println(str);
		}
	}

	@Override
	public String viewDifferences() {
		return "Check the console to see the differences";
	}
}
