package com.ibm.lconn.automation.framework.services.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.writer.Writer;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.lang.RandomStringUtils;

import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;

public class Utils {
	
	public static DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	public static DateFormat logDateFormatter = new SimpleDateFormat("yyyyMMdd't'hhmmss'z'");
	public static final ThreadLocal<DateFormat> tLdateFormatter = new ThreadLocal<DateFormat>(){
		    @Override
		    protected DateFormat initialValue() {
		        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		    }
	};

	public static String uniqueString = RandomStringUtils.randomAlphanumeric(4)+logDateFormatter.format(new Date());
	
	public static Comparator<Category> categoryComparator = new Comparator<Category>() {

        // Java 5 doesn't allow this, should re-enable when we move to Java 6
		//@Override
		public int compare(Category o1, Category o2) {
			int term = o1.getTerm().compareTo(o2.getTerm());
			
			if(term == 0) {
				return o1.getText().compareTo(o2.getText());
			}
			
			return term;
		}
	};

	public static String getMimeType(File file) {
		FileNameMap map = URLConnection.getFileNameMap();
		return map.getContentTypeFor(file.getPath());
	}
	
	public static Entry createErrorEntry(int errorCode, String errorMsg) {
		Factory factory = Abdera.getNewFactory();
		
		ExtensibleElement responseCode = factory.newExtensionElement(StringConstants.API_RESPONSE_CODE);
		responseCode.setText(Integer.toString(errorCode));
		
		ExtensibleElement responseMsg = factory.newExtensionElement(StringConstants.API_RESPONSE_MSG);
		responseMsg.setText(errorMsg);
		
		Entry errorEntry = factory.newEntry();
		errorEntry.setAttributeValue(StringConstants.API_ERROR, Boolean.toString(true));
		errorEntry.addExtension(responseCode);
		errorEntry.addExtension(responseMsg);
		
		return errorEntry;
	}
	
	public static void addNewsServiceCredentials(String url, AbderaClient client, String username, String password) throws URISyntaxException {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		client.clearCookies();
		client.addCredentials(url, StringConstants.AUTH_REALM_NEWS, StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(url, StringConstants.AUTH_REALM_FORCED, StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(url, null, StringConstants.AUTH_BASIC, credentials);
	}
	
	public static void addServiceCredentials(ServiceEntry entry, AbderaClient client) throws URISyntaxException {
		addServiceCredentials(entry, client, StringConstants.USER_EMAIL, StringConstants.USER_PASSWORD);
	}
	
	
	public static void addServiceCredentials(ServiceEntry entry, AbderaClient client,String username, String password) throws URISyntaxException {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		client.clearCookies();
		client.addCredentials(entry.getServiceURLString(), getAuthRealm(entry.getComponent()), StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(entry.getServiceURLString(), StringConstants.AUTH_REALM_FORCED, StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(entry.getServiceURLString(), null, StringConstants.AUTH_BASIC, credentials);
	}
	
	
	public static void addServiceAdminCredentials(ServiceEntry entry, AbderaClient client) throws URISyntaxException {
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(StringConstants.ADMIN_USER_EMAIL, StringConstants.ADMIN_USER_PASSWORD);

		client.addCredentials(entry.getServiceURLString(), getAuthRealm(entry.getComponent()), StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(entry.getServiceURLString(), StringConstants.AUTH_REALM_FORCED, StringConstants.AUTH_BASIC, credentials);
	}
	
	public static String getAuthRealm(Component componentName) {
		return StringConstants.componentRealmMap.get(componentName);
	}
	
	// TODO: Finish implementation... currently does not handle complciated CSV files.
	/* Parse CSV file and return the contents as ArrayList. First line is assumed to contain labels. */
	public static ArrayList<HashMap<String, String>> parseCSV(File csvFile) throws IOException {
		ArrayList<HashMap<String, String>> items = new ArrayList<HashMap<String,String>>();
		Scanner scanner = new Scanner(csvFile);
		
		String[] labels = null;
		String line;
		HashMap<String, String> tempMap = new HashMap<String, String>();
		
		while(scanner.hasNextLine()) {
			if((line = scanner.nextLine()) != null) {
				labels = line.split(",");
				break;
			}
		}
		
		while((scanner.hasNextLine())) {
			tempMap.clear();
			String[] components = scanner.nextLine().split(",");
			for(int i = 0; i < components.length && i < labels.length; i++) {
				tempMap.put(labels[i], components[i]);
			}
			items.add(tempMap);
		}
		scanner.close();
		
		return items;
	}
	
	public static byte[] getFileByte( File file) throws Exception{
		byte[] fileData = new byte[(int) file.length()];
		FileInputStream in = new FileInputStream(file);
		in.read(fileData);
		in.close();
		// now fileData contains the bytes of the file
		
		return fileData;
	}

	private static final Abdera ABDERA = new Abdera();
	private static final Writer WRITER = ABDERA.getWriterFactory().getWriter("prettyxml");

	public static void prettyPrint(Base base) throws Exception {
		prettyPrint(base, System.out);
	}
	public static void prettyPrint(Base base, OutputStream out) throws Exception
	{
		if (null != base) {
			WRITER.writeTo(base, out);
			out.write('\n');
		}
		else {
			System.out.println("prettyPrint(): NULL");
		}
	}

}
