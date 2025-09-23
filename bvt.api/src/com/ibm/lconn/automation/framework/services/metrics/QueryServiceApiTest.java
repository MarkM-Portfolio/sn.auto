package com.ibm.lconn.automation.framework.services.metrics;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.SetProfileData;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

public class QueryServiceApiTest {
	
	private static ProfileData profile;
	
	private final static Logger LOGGER = Logger.getLogger(QueryServiceApiTest.class.getName());
	
	private static MetricsHttpClient client;
	
	private static CommunitiesService comServiceUser;
	static UserPerspective comUser;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}
		SetProfileData.instance_flag = true;
		UsersEnvironment userEnv = new UsersEnvironment();
		SetProfileData.instance_flag = false;

		profile = ProfileLoader.getProfile(StringConstants.CURRENT_USER);
		client = new MetricsHttpClient(profile);
		
		comUser = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		comServiceUser = comUser.getCommunitiesService();
	}
	
	@Test
	/**
	 * the test will try to check if the request parameters from the client is valid.
	 */
	public void testParamsValidation() throws Exception {
		LOGGER.fine("BEGINNING TEST: testParamsValidation");
		if(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			//Create community
			String testNumber = RandomStringUtils.randomAlphanumeric(4);
			String pubCommunityNameOfUser = "IC148037-Pub-Com-User-"
					+ testNumber;
			Community pubCommunityofUser = new Community(pubCommunityNameOfUser,
					"IC148037-Pub-Com-User", Permissions.PUBLIC, null);
			Entry pubCommunityResultOfUser = (Entry) comServiceUser.createCommunity(pubCommunityofUser);
			assertTrue("pubCommunityResultOfUserA is not null."+comServiceUser.getDetail(),pubCommunityResultOfUser != null);
			
			Community testPubCommunityRetrievedOfUser = new Community((Entry) comServiceUser.getCommunity(pubCommunityResultOfUser.getEditLinkResolvedHref().toString()));

			assertNotNull(testPubCommunityRetrievedOfUser.getUuid());
			//give the empty map to get method, then it will send the request without any parameter
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("communityUuid", testPubCommunityRetrievedOfUser.getUuid());
			//The response status should be 400.
			String response = client.getQueryService(map, 400);
			System.out.println("result :" + response);
			assertNotNull(response);
			try {
				JSONObject json = JSONObject.parse(response);
				//response will return the json response and report that the time range parameter is invalid.
				assertTrue("TEST ERROR: testParamsValidation", json.containsKey("Time Range Error"));
			} catch (Exception e) {
				assertTrue("TEST ERROR: testParamsValidation", false);
				LOGGER.fine("Wrong JSON response received for test: testParamsValidation");
			}
		}
		
		LOGGER.fine("ENDING TEST: testParamsValidation");
	}
	
	@Test
	/**
	 * the test will try to get the metrics report from yesterday to today, and the result is 0
	 * because there is no any data in metrics.
	 */
	public void testEmptyQuery() throws Exception {
		LOGGER.fine("BEGINNING TEST: testEmptyQuery");
		if(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			//Create community
			String testNumber = RandomStringUtils.randomAlphanumeric(4);
			String pubCommunityNameOfUser = "IC148037-Pub-Com-User-"
					+ testNumber;
			Community pubCommunityofUser = new Community(pubCommunityNameOfUser,
					"IC148037-Pub-Com-User", Permissions.PUBLIC, null);
			Entry pubCommunityResultOfUser = (Entry) comServiceUser.createCommunity(pubCommunityofUser);
			assertTrue("pubCommunityResultOfUserA is not null."+comServiceUser.getDetail(),pubCommunityResultOfUser != null);
			
			Community testPubCommunityRetrievedOfUser = new Community((Entry) comServiceUser.getCommunity(pubCommunityResultOfUser.getEditLinkResolvedHref().toString()));

			assertNotNull(testPubCommunityRetrievedOfUser.getUuid());
			//generate request parameters.
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE, -1);
			Date date1 = calendar.getTime();
			String endTime = dateFormat.format(date);
			String startTime = dateFormat.format(date1);
			calendar.add(calendar.DATE, -2);
			String preEndTime = dateFormat.format(calendar.getTime());
			
			calendar.add(calendar.DATE, -3);
			String preStartTime = dateFormat.format(calendar.getTime());
			
			LOGGER.fine(startTime + "---" + endTime);
			//generate the map which contains the correct parameter
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("scope", "system");
			map.put("startTime", startTime);
			map.put("endTime", endTime);
			map.put("preStartTime", preStartTime);
			map.put("preEndTime", preEndTime);
			map.put("sourceName", "BLOGS,FORUMS");
			map.put("eventOpType", "CREATE,UPDATE");
			map.put("format", "json");
			map.put("reportTemplate", "time");
			map.put("aggregationFields", "time,source,eventOpType");
			map.put("interval", "day");
			map.put("communityUuid", testPubCommunityRetrievedOfUser.getUuid());
			//the response status is 200
			String response = client.getQueryService(map, 200);
			assertNotNull(response);
			try {
				// the json response is the empty metrics report.
				JSONObject json = JSONObject.parse(response);
				assertTrue("TEST ERROR: testParamsValidation", json.containsKey("counts"));
			} catch (Exception e) {
				assertTrue("TEST ERROR: testParamsValidation", false);
				LOGGER.fine("Wrong JSON response received for test: testParamsValidation");
			}
		LOGGER.fine("ENDING TEST: testEmptyQuery");
		}
  
	}
}
