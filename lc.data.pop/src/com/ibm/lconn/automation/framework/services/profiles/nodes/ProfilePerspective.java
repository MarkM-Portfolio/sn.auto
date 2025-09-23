package com.ibm.lconn.automation.framework.services.profiles.nodes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.codec.binary.Base64;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;

public class ProfilePerspective {

	private ServiceConfig config;
	private String _email;
	private String _key;
	private String _sUser, _sPW;	
	private String _userId;
	private String _realName;
	
	private ServiceEntry _serviceEntry;
	private ProfilesService _profilesService;
	

	public ProfilePerspective(int userNumber, boolean useSSL) throws FileNotFoundException, IOException, LCServiceException{

		ProfileData profileData = ProfileLoader.getProfile(userNumber);
		_userId = profileData.getUserId(); 
		_email = profileData.getEmail();
		_sUser = profileData.getUserName();
		_realName = profileData.getRealName();
		_sPW = profileData.getPassword();

		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);
		AbderaClient.registerTrustManager();
		
		config = new ServiceConfig(client, URLConstants.SERVER_URL, useSSL, _email, _sPW);
		
		ServiceEntry profiles = null;
	
		profiles = config.getService("profiles");	

		Map<String, String> headers = new HashMap<String, String>();
		
			if ( StringConstants.AUTHENTICATION.equalsIgnoreCase(StringConstants.Authentication.BASIC.toString()) ) {
				//Utils.addServiceCredentials(profiles, client,_email,_sPW);
				String auth = _email+":"+_sPW;
				
				//String base64 = new String(Base64.encodeBase64(auth.getBytes())); 
				//headers.put("Authorization", "Basic "+Base64.encodeBase64String(auth.getBytes()));  
				headers.put("Authorization", "Basic "+new String(Base64.encodeBase64(auth.getBytes())));  
				
			}
						
		
			_profilesService = new ProfilesService(client, profiles, headers);
		
		
		VCardEntry userCard = _profilesService.getUserVCard();
		_key = userCard.getVCardFields().get(StringConstants.VCARD_PROFILE_KEY);		
		//_userId = userCard.getVCardFields().get("UID");
		_userId = userCard.getVCardFields().get(StringConstants.VCARD_GUID);
		_realName = userCard.getVCardFields().get(StringConstants.VCARD_DISPLAY_NAME);
		
	}
	
	public ServiceConfig getConfig() {
		return config;
	}
	
	public String get_Password() {
		return _sPW;
	}

	public ServiceEntry get_serviceEntry() {
		return _serviceEntry;
	}

	public String getUserId() {
		return _userId;
	}

	public String getEmail(){
		return _email;
	}
	
	public String getKey() {
		return _key; 
	}
	
	public ProfilesService getService() {
		return _profilesService; 
	}
	
	public String getUserName(){
		return _sUser;
	}
	
	public String getRealName() {
		return _realName;
	}

}