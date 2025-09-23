package com.ibm.lconn.automation.framework.services.profiles.admin;

import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;

/**
 * JUnit Tests via Connections API for Profiles Admin Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */

public class GetUserID {
	
	private static ProfilesAdminService profileService;
	
	public static String getUserID(String username, boolean useSSL){
		String userID = null;

		try {
			Abdera abdera = new Abdera();
			AbderaClient client = new AbderaClient(abdera);
			ServiceConfig config = new ServiceConfig(client, URLConstants.SERVER_URL, useSSL);
			ServiceEntry profiles = config.getService("profiles");	
			Utils.addServiceAdminCredentials(profiles, client);
			profileService = new ProfilesAdminService(client, profiles);
			assert(profileService.isFoundService());			
			
			userID = profileService.getUserID(URLEncoder.encode(username, "UTF-8"));

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}			
		
		return userID;
		
	}

}
