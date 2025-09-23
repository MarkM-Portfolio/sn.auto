package com.ibm.lconn.automation.framework.search.rest.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class OrientMeConstants {
	public static ArrayList<ProfileData> users = new ArrayList<ProfileData>();

	 public static ProfileData userProfile(int index){
	    	return users.get(index);
	    }
	public static void loadOrientMeProperties() throws IOException {
		Properties _profileProperties = new Properties();
		String PROPERTIES_FILE_PATH = "/resources/orientme.properties";
		InputStream in = ProfileData.class
				.getResourceAsStream(PROPERTIES_FILE_PATH);
		if (in != null) {
			_profileProperties.load(in);
		}
		int i = 1;

		String serverUrl = URLConstants.SERVER_URL;
		if (URLConstants.SERVER_URL.contains("sandbox")) {
			serverUrl = "https://apps.basesandbox09.swg.usma.ibm.com";
		}
		while (_profileProperties.getProperty("user" + i + ".email") != null) {

			if (_profileProperties.getProperty("user" + i + ".serverUrl")
					.equalsIgnoreCase(serverUrl)) {
				String email = _profileProperties.getProperty("user" + i
						+ ".email");
				String password = _profileProperties.getProperty("user" + i
						+ ".password");
				String realname = _profileProperties.getProperty("user" + i
						+ ".realname");
				String orgId = _profileProperties.getProperty("user" + i
						+ ".orgId");
				ProfileData profilesData = new ProfileData("", password, email,
						"", "", realname, true, false, true, false, false,
						orgId);
				users.add(profilesData);

			}
			i++;
		}
	}
}
