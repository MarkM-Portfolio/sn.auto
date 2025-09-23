package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json;

import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;

import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class JsonClient {
	protected final static Logger LOGGER = Logger.getLogger(JsonClient.class.getName());
	String userName;
	String userPassword;
	String hostName;
	boolean secure;
	int port;
	private static AbderaClient client;
	private ProfilesService profileService;
	
	public JsonClient(String userName, String userPassword, String hostName)
			throws KeyManagementException, NoSuchAlgorithmException, FileNotFoundException, IOException {
		this(userName, userPassword, hostName, 443, true);

	}

	public JsonClient(String userName, String userPassword, String hostName,
			int port, boolean secure) throws KeyManagementException,
			NoSuchAlgorithmException, FileNotFoundException, IOException {
		
		this.userName = userName;
		this.userPassword = userPassword;
		this.hostName = hostName;
		this.secure = secure;
		this.port = port;
		
		
		

		Abdera abdera = new Abdera();
		client = new AbderaClient(abdera);
		
				
		AbderaClient.registerTrustManager();
		
		try {
			ServiceConfig config = new ServiceConfig(client, URLConstants.SERVER_URL, true,
					userName, userPassword);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}		
		

	}
	
	public String GetUserIdOnCloud (){
		
		String userId = "";
		
		try {
			ServiceConfig config = new ServiceConfig(client, URLConstants.SERVER_URL, true,
					userName, userPassword);
			ServiceEntry profiles = config.getService("profiles");

			profileService = new ProfilesService(client, profiles);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		if (profileService != null){
		VCardEntry vCard = profileService.getUserVCard();
		LinkedHashMap<String, String> maps = vCard.getVCardFields();
		userId = maps.get("X_LCONN_USERID");
		}else {
			LOGGER.fine("Profiles service is null on SC");
		}
		return userId;
	}

	public static JsonResponse execute(String url) throws Exception {
		JSONObject json =null;
		JsonResponse jResponse = null;
		StringBuffer jsonStr ;
		
		StringBuffer finalUrlBuffer = new StringBuffer();
		finalUrlBuffer.append(URLConstants.SERVER_URL);
		finalUrlBuffer.append(url);
		String finalUrl = finalUrlBuffer.toString();
		ClientResponse asResponse = client.get(finalUrl);
		
		int statusCode = asResponse.getStatus();

		if (asResponse.getType() == ResponseType.SUCCESS) {
				
		
			if (asResponse.getContentLength() != 0) {
				InputStream instream = asResponse.getInputStream();
				jsonStr = new StringBuffer();
				try {

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(instream));
					
					String line = reader.readLine();
					jsonStr.append(line);
					

				 }finally {
					
					instream.close();
				}
				try {
					json = JSONObject.parse(jsonStr.toString());
					jResponse = new JsonResponse(json);
				} catch (Exception e) {
					// TODO: handle exception
					LOGGER.fine("Wrong JSON response received for request:"+url+", response: "+jsonStr.toString()+" status:"+statusCode);
				}
			}
			 else
			{
				LOGGER.fine("Response is empty for url:"+url);
			

		}
			}else {
			LOGGER.fine("Response status code :"+statusCode);
		}
		
		
		
		
		return jResponse;

	}

	
	public static int getJsonResponseEntries(JsonResponse jResponse) {
		int entriesNumber = jResponse
				.count(PopStringConstantsAS.JSON_ROOT_ENTRY);
		return entriesNumber;
	}

	public static Boolean executeJson(String requestURL) throws Exception {

		boolean testRes;
		JsonResponse js = getJsonResponse(requestURL);
		
		testRes = checkJsonValidity(js);
		if (testRes) {

			int entriesCount = getJsonResponseEntries(js);

			

		}
		return testRes;
	}
	
	public  static JsonResponse getJsonResponse(String requestURLToExecute)
			throws Exception {
		JsonResponse js = execute(requestURLToExecute);
		return js;
	}

	// Return true if JSON response is valid or false if not valid
	public static Boolean checkJsonValidity(JsonResponse jResponse) {
		boolean jsonValid;
		if (jResponse == null) {
			
			jsonValid = false;
		} else {
			
			jsonValid = true;
		}

		return jsonValid;
	}
}
