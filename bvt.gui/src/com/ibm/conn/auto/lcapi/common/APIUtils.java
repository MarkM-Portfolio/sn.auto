package com.ibm.conn.auto.lcapi.common;

import java.net.URISyntaxException;

import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;

public class APIUtils extends Utils {

	private static final Logger log = LoggerFactory.getLogger(APIUtils.class);

	public static void addServiceCredentials(ServiceEntry entry, AbderaClient client, String user, String password) throws URISyntaxException {
		
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);

		client.addCredentials(entry.getServiceURLString(), getAuthRealm(entry.getComponent()), StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(entry.getServiceURLString(), StringConstants.AUTH_REALM_FORCED, StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(entry.getServiceURLString(), null, StringConstants.AUTH_BASIC, credentials);
	}

	public static boolean resultSuccess(Entry entry, String component) {

		if (entry != null && (entry.getAttributeValue(StringConstants.API_ERROR) == null || Boolean.parseBoolean(entry.getAttributeValue(StringConstants.API_ERROR)) != true)) {
			log.info("API: " + component + ": " + entry.getTitle() + " action completed successfully.");
			return true;
		} else {
			if (entry.getExtension(StringConstants.API_RESPONSE_CODE) != null) log.warn("Response Code: " + entry.getExtension(StringConstants.API_RESPONSE_CODE).getText());
			log.warn("API: " + component + " action failure. Returning null.");
			return false;
		}
	}

	/**
	 * Change http to https
	 * Remove trailing /
	 * Change port number to ssl if needed
	 * @param browserURL
	 * @return
	 */
	public static String formatBrowserURLForAPI(String browserURL) {

		String url = browserURL;
		url = url.replaceFirst("http://", "https://");
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		Integer port = null;
		try {
			port = Integer.parseInt(url.substring(url.length() - 4, url.length()));
		} catch(NumberFormatException ex) {}
		if(port != null && (port >= 9080 && port <= 9180) && url.charAt(url.length() - 5) == ':') {
			url = url.substring(0, url.length() - 4) + (9443 + (port - 9080));
		}
				
		return url;
	}
}
