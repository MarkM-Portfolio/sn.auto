package output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class implements the Output interface, 
 * and handles writing to a specified filepath 
 * 
 * @author Eric Peterson (petersde@us.ibm.com)
 * @version 1.6
 * @since 2012-06-01
 */
public class ToFile implements Output {

	/**
	 * variables
	 * 
	 * fpath: the filepath to the file that will be written to
	 * 
	 * file: the BufferedWriter used to write to the file
	 */
	private String fpath;
	BufferedWriter file;
	DateFormat format;
	Date date;
	
	/**
	 * constructor
	 * 
	 * @param str - (String) The file path to the file being written to
	 * 
	 * @throws IOException 
	 */
	public ToFile(String str) throws IOException {
		this.fpath = str;
		this.file = new BufferedWriter(new FileWriter(fpath, true));

		this.format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		this.date = new Date();
		file.newLine();
		file.write(format.format(this.date));
		file.newLine();
	}
	
	/**
	 * (see Output interface)
	 */
	@Override
	public void display(String str) {
		try {
			file.write(str);
			if (str != "\n") {
				file.newLine();
			}
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String viewDifferences() {
		return "Check the file " + fpath + " to see the differences";
	}
}
