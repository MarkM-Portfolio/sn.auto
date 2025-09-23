/**
 * 
 */
package com.ibm.lconn.automation.framework.search.rest.api;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.search.data.QuickResultsProfileData;
import com.ibm.lconn.automation.framework.services.search.data.QuickResultsProfileLoader;

/**
 * @author reuven
 * 
 */
public class RestAPIUser {

	private AbderaClient client;
	private ServiceConfig config;
	private RequestOptions options;

	public RequestOptions getOptions() {
		return options;
	}

	public void setOptions(RequestOptions options) {
		this.options = options;
	}

	private boolean _useSSL;
	UsernamePasswordCredentials _credentials;
	private ProfileData _profData;

	public QuickResultsProfileData getQuickResultsProfData() {
		return (QuickResultsProfileData) _profData;
	}

	public ProfileData getProfData() {
		return _profData;
	}

	public static enum UserType {
		ADMIN, LOGIN, PROFILE, QUICKRESULTS, ASSEARCH, RECOMMEND,ORIENTME;
	}
	public RestAPIUser(UserType userType) throws FileNotFoundException,
	IOException {
		this(userType,0);
	}
	public RestAPIUser(UserType userType,int profileNumber) throws FileNotFoundException,
			IOException {
		if (userType == UserType.ADMIN) {
			_profData = ProfileLoader.getProfile(0);
		} else if (userType == UserType.LOGIN) {
			_profData = ProfileLoader.getProfile(3);
		} else if (userType == UserType.RECOMMEND) {
			_profData = ProfileLoader.getProfile(5);
		} else if (userType == UserType.PROFILE) {
			if (profileNumber == 0){
			_profData = ProfileLoader.getProfile(2);
			}else{
				_profData = ProfileLoader.getProfile(profileNumber);
			}
		} else if (userType == UserType.QUICKRESULTS) {			
				_profData = QuickResultsProfileLoader.getQuickResultsProfile(2);
		}else if (userType == UserType.ASSEARCH) {
				_profData = ProfileLoader.getProfile(2);
		}else if (userType == UserType.ORIENTME) {
				_profData = OrientMeConstants.userProfile(profileNumber);
		} else {
			_profData = ProfileLoader.getProfile(2);
		}

		Abdera abdera = new Abdera();
		client = new AbderaClient(abdera);
		options = client.getDefaultRequestOptions();
		options.setUseLocalCache(false);
		options.setFollowRedirects(false);

		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();

		_useSSL = true;
		
		
         if (getConfig() == null){
        	 try {
 				Thread.sleep(30000);
 			} catch (InterruptedException e) {
 				e.printStackTrace();
 			}
        	 
        	 if (getConfig ()== null){
        		 try {
      				Thread.sleep(30000);
      			} catch (InterruptedException e) {
      				e.printStackTrace();
      			}
        		 getConfig ();
        	 }
         }
		_credentials = new UsernamePasswordCredentials(_profData.getEmail(),
				_profData.getPassword());

	}

	public RequestOptions getDefaultRequestOptions() {
		return options;
	}

	public ServiceConfig getConfigService() {
		return config;
	}

	public ServiceEntry getService(String serviceName) {
		
		return config.getService(serviceName);
	}

	public AbderaClient getAbderaClient() {
		return client;
	}

	public AbderaClient clearCredentials() {
		return client.clearCredentials();
	}

	public void logout() {

		client.clearCookies();
		client.clearCredentials();
	}

	private void addCredentials() throws URISyntaxException {
		client.clearCredentials();
		client.clearCookies();

		try {
			client.addCredentials(URLConstants.SERVER_URL,
					StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, _credentials);
			client.addCredentials(URLConstants.SERVER_URL,
					StringConstants.AUTH_REALM_COMMUNITIES,
					StringConstants.AUTH_BASIC, _credentials);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addCredentials(ServiceEntry serviceEntry)
			throws URISyntaxException {
		Component componet = serviceEntry.getComponent();
		String realmApp = null;
		if (componet.equals(Component.COMMUNITIES)) {
			realmApp = StringConstants.AUTH_REALM_COMMUNITIES;
		} else if (componet.equals(Component.ACTIVITIES)) {
			realmApp = StringConstants.AUTH_REALM_ACTIVITIES;
		} else if (componet.equals(Component.BLOGS)) {
			realmApp = StringConstants.AUTH_REALM_BLOGS;
		} else if (componet.equals(Component.CRE)) {
			realmApp = StringConstants.AUTH_REALM_CRE;
		} else if (componet.equals(Component.DOGEAR)) {
			realmApp = StringConstants.AUTH_REALM_DOGEAR;
		} else if (componet.equals(Component.FILES)) {
			realmApp = StringConstants.AUTH_REALM_FILES;
		} else if (componet.equals(Component.FORUMS)) {
			realmApp = StringConstants.AUTH_REALM_FORUMS;
		} else if (componet.equals(Component.WIKIS)) {
			realmApp = StringConstants.AUTH_REALM_WIKIS;
		} else if (componet.equals(Component.PROFILES)) {
			realmApp = StringConstants.AUTH_REALM_PROFILES;
		}

		if (_useSSL) {
			client.addCredentials(serviceEntry.getSslHrefString(), realmApp,
					StringConstants.AUTH_BASIC, _credentials);
			client.addCredentials(serviceEntry.getSslHrefString(),
					StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, _credentials);
		} else {
			client.addCredentials(serviceEntry.getHrefString(), realmApp,
					StringConstants.AUTH_BASIC, _credentials);
			client.addCredentials(serviceEntry.getHrefString(),
					StringConstants.AUTH_REALM_FORCED,
					StringConstants.AUTH_BASIC, _credentials);
		}
	}

	public void basicAuthenticationLogin(String username, String password) {
		try {
			config = new ServiceConfig(client, URLConstants.SERVER_URL, true);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			assertTrue(e1.getMessage(), false);
		}
		// clear basic auto added in serviceConfig
		clearCredentials();

		_credentials = new UsernamePasswordCredentials(username, password);
		try {
			addCredentials();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void formBaseAuthenticationLogin() {
		formBaseAuthenticationLogin(_profData.getUserName(),
				_profData.getPassword());
	}

	public void formBaseAuthenticationLogin(String username, String password) {

		HttpClient http = new HttpClient();
		http.getState().clearCookies();

		Cookie[] cookies;
		cookies = LCService.executeJLogin(http, username, password);
		for (Cookie cookie : cookies) {
			client.addCookies(cookie);
		}
	}
	private ServiceConfig getConfig () {
	try {
		config = new ServiceConfig(client, URLConstants.SERVER_URL, true,
				_profData.getEmail(), _profData.getPassword());
	} catch (LCServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		assertTrue(e.getMessage(), false);
	}
	if (config.isServicesTableEmpty()== true){
		return null;
	}
	return config;
	}
}
