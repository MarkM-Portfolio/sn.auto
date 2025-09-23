package com.ibm.lconn.automation.framework.services.common;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.abdera.protocol.client.RequestOptions;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServiceConfig object retrieves the service config document from 
 * a Connections server and parses it to retrieve the service URLs.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ServiceConfig {

	private AbderaClient client;
	private HashMap<String, ServiceEntry> services;
	private final static Logger LOGGER = LoggerFactory.getLogger(ServiceConfig.class.getName());
	private boolean emailHidden = false;
	
	private String ssl_ver = null;
	
	private String generatorVersion = "";

	public ServiceConfig(AbderaClient client, String baseURL, boolean useSSL) throws LCServiceException {
		
		getServiceConfig(client, baseURL, useSSL, StringConstants.USER_EMAIL, StringConstants.USER_PASSWORD);
	}
	
	public ServiceConfig(AbderaClient client, String baseURL, boolean useSSL, String userEmail, String pw) throws LCServiceException {
		getServiceConfig(client, baseURL, useSSL, userEmail, pw);
		
	}

	private void getServiceConfig(AbderaClient client, String baseURL,
			boolean useSSL, String userEmail, String pw) throws LCServiceException {
		LOGGER.debug("getServiceConfig : "+userEmail+"/"+pw);
		LOGGER.debug("baseURL : "+baseURL);
		this.client = client;
		this.services = new HashMap<String, ServiceEntry>();
		RequestOptions options = client.getDefaultRequestOptions();
		options.setUseExpectContinue(false);
		ClientResponse configResponse;
		
		
		try {
			ssl_ver = SSLVersionTest.getSSLVersion(baseURL);
			LOGGER.debug("getSSLVersion : "+ssl_ver);
			AbderaClient.registerFactory(new TLSSocketFactory(ssl_ver), 443);
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting server URL: " + e.getMessage(), e);
		}
		
		if ( StringConstants.AUTHENTICATION.equalsIgnoreCase(StringConstants.Authentication.BASIC.toString()) ){

			try {
				Utils.addNewsServiceCredentials(baseURL + URLConstants.NEWS_BASE, client, userEmail, pw);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String auth = userEmail+":"+pw;
			options.setHeader("Authorization", "Basic "+new String(Base64.encodeBase64(auth.getBytes())));
			
			// For SC basic auth , use communities to get serviceconfigs
			configResponse = this.client.get(baseURL + URLConstants.COMMUNITIES_BASE + URLConstants.ServiceConfigs);
			LOGGER.debug("ServiceConfig call with user/pwd: "+userEmail+"/"+pw);
			LOGGER.debug("ServiceConfig call: "+ baseURL + URLConstants.COMMUNITIES_BASE + URLConstants.ServiceConfigs+ " STATUS:"+configResponse.getStatus());
			
		} else {
			configResponse = LCService.getServiceConfigFeed(client, baseURL + URLConstants.NEWS_BASE + URLConstants.ServiceConfigs, userEmail, pw);
			LOGGER.debug("ServiceConfig call: "+ baseURL + URLConstants.NEWS_BASE + URLConstants.ServiceConfigs + " STATUS:"+configResponse.getStatus());
		}
		
		if (configResponse.getType() == ResponseType.SUCCESS) {
			Document<Feed> serviceFeedDoc = configResponse.getDocument();
	
			if(serviceFeedDoc.getContentType().getSubType().contains("html")){
				LOGGER.error("Can't get Service Configuration XML Doc..");
				LOGGER.error("Return html page :");
				LOGGER.error(serviceFeedDoc.toString());
				//System.exit(1);
				throw new LCServiceException("Error : Can't get Service Configuration XML Doc.." +
						"\n API test can't continue without Configuration XML Doc"+
						"\nThe Doc returned is : \n"+ serviceFeedDoc.toString());
			}
			Feed serviceFeed = serviceFeedDoc.getRoot();
			
			if (StringConstants.shouldServiceFeedLog){
				LOGGER.debug("====ServiceFeed :");
				LOGGER.debug(serviceFeed.toString());
				LOGGER.debug("====End");
				StringConstants.shouldServiceFeedLog = false;
			}
			
			// Defect 71138 - at generator, replace reference 'Lotus Connections'
			String generateText = serviceFeed.getGenerator().getText();
			if ( generateText.contains("Lotus Connections")){
				LOGGER.error("CONFIG_GENERATOR shoold replace Lotus with IBM");
			}
			
			Category emailConfig = serviceFeed.getCategories("http://www.ibm.com/xmlns/prod/sn/configuration").get(0);
			if(emailConfig != null) {
				if(emailConfig.getTerm().equals("email-hidden"))
					setEmailHidden(true);
				else if(emailConfig.getTerm().equals("email-exposed"))
					setEmailHidden(false);
			}
			for(Entry serviceEntry: serviceFeed.getEntries()) {
				services.put(serviceEntry.getTitle(), new ServiceEntry(serviceEntry, emailHidden, useSSL));
				//LOGGER.debug(services.get(serviceEntry.getTitle()).toString());
			}
			
			// save the product version string
			generatorVersion = serviceFeed.getGenerator().getVersion();
			
		} else {
			LOGGER.error(configResponse.getStatus() + ": " + configResponse.getStatusText());
		}
		
		configResponse.release();
	}

	public ServiceEntry getService(String serviceName) {
		return services.get(serviceName.toLowerCase());
	}

	public boolean isEmailHidden() {
		return emailHidden;
	}
	public boolean isServicesTableEmpty() {
		return services.isEmpty();
	}
	public void setEmailHidden(boolean emailHidden) {
		this.emailHidden = emailHidden;
	}
	
	public String getGeneratorVersion() {
		return generatorVersion;
	}
	
	public boolean isCnxVersion65() {
		return generatorVersion.contains("6.5");
	}
	
	public boolean isTargetCnxVersion(String regex) {
		return generatorVersion.matches(regex);
	}
}
