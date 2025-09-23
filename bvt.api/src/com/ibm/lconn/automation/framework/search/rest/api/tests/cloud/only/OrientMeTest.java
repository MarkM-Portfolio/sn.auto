package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.wink.json4j.JSONException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;
import com.ibm.lconn.automation.framework.search.rest.api.OrientMeConstants;
import com.ibm.lconn.automation.framework.search.rest.api.OrientMeResponse;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;


public class OrientMeTest {
	private static AbderaClient client;
	protected final static Logger LOGGER = Logger.getLogger("Test Logger");
	protected String baseUrl;
	
	@BeforeTest
	public void setUp (){
	
		Abdera abdera = new Abdera();
		client = new AbderaClient(abdera);
		
				
		AbderaClient.registerTrustManager();
	
	String userEmail=OrientMeConstants.userProfile(0).getEmail();;
	String userPassword=OrientMeConstants.userProfile(0).getPassword() ;
	baseUrl = URLConstants.SERVER_URL;
	try {
		ServiceConfig config = new ServiceConfig(client,baseUrl , true,
				userEmail, userPassword);
	} catch (LCServiceException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		assertTrue(e.getMessage(), false);
	}		
	//https://apps.basesandbox30.swg.usma.ibm.com/connections/opensocial/rest/stacking/@me/@all/@all
	}
	
	@Test
	public void OrientMeRequestsTest () throws IOException, JSONException{
		String url = baseUrl + "/connections/opensocial/basic/rest/stacking/@me/@all/@all";
		ClientResponse asResponse = client.get(url);
		OrientMeResponse response = new OrientMeResponse(asResponse);
		int statusCode = asResponse.getStatus();
		
		assertTrue("statusCode: " +statusCode+" for Request: " + url , (statusCode==200));
		assertTrue("Results not found for Request: " + url , (response.getTimeBoxesCount()> 0));
	}
	
	
	}
