package com.ibm.lconn.automation.framework.services.metrics;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.SetProfileData;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class QueryServiceApiTestForEventGeneratorInCommunity {
	
	private static ProfileData profile;
	
	private final static Logger LOGGER = Logger.getLogger(QueryServiceApiTestForEventGeneratorInCommunity.class.getName());
	
	private static MetricsHttpClient client;
	
	private static CommunitiesService comServiceUser;
	private static ActivitiesService actServiceUser;
	static UserPerspective actUser, comUser;
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

		comUser = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		comServiceUser = comUser.getCommunitiesService();
		
		actUser = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER, Component.ACTIVITIES.toString());
		actServiceUser = actUser.getActivitiesService();

		profile = ProfileLoader.getProfile(StringConstants.CURRENT_USER);
		client = new MetricsHttpClient(profile, actServiceUser);
	}
	
	//@Test
	public void testNumberOfNewActivity() throws Exception {
		LOGGER.fine("BEGINNING TEST: testNumberOfNewActivity");

		// Step1 create an community and add widget ACTIVITIES
		String testNumber = RandomStringUtils.randomAlphanumeric(4);
		String name = "MetricForNumberOfNewActivity: " + testNumber;
		LOGGER.fine("testNumberOfNewActivity: " + name);
		Community community = new Community(name, name, Permissions.PRIVATE, name);
		LOGGER.fine("Create community: " + community.toString());
		Entry response = (Entry) comServiceUser.createCommunity(community);
		Community returnCommunity = new Community(
				(Entry) comServiceUser.getCommunity(response.getEditLinkResolvedHref().toString()));
		Widget widget = new Widget(StringConstants.WidgetID.Activities.toString());
		comServiceUser.postWidget(returnCommunity, widget.toEntry());
		assertEquals(201, comServiceUser.getRespStatus());
		
		String communityUuid = returnCommunity.getUuid();

		//String communityUuid = "bdc8b149-a828-47cd-b202-5fa0c9629700";
		
		assertNotNull(communityUuid);
		//generate request parameters.
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String timeStr = dateFormat.format(date);

		//generate the map which contains the correct parameter
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("communityUuid", communityUuid);
		map.put("dimension", "all");
		map.put("dimensionLabel", "All people");
		map.put("typelink", "NUMBER_OF_ACTIVITIES");
		map.put("name", "participation");
		map.put("reportType", "line");
		map.put("itemType", "activity");
		map.put("source", "activities");
		map.put("eventOpType", "CREATE");
		map.put("group", "user");
		map.put("aggregationFields", "source,time");
		map.put("reportTemplate", "time");
		map.put("format", "json");
		map.put("interval", "year");
		map.put("scope", "community");
		map.put("dataStartDate", timeStr);
		map.put("eventStartDate", timeStr);
		map.put("cutOffDate", timeStr);
		map.put("startTime", timeStr);
		map.put("endTime", timeStr);
		LOGGER.fine("Param map1 --- : " + map.toString());
		

		//the response status is 200
		LOGGER.fine("Param map2 --- : " + map.toString());
		String responseJson = client.postQueryService(map, 200);
		assertNotNull(responseJson);
		LOGGER.fine("responseJson1: " + responseJson);
		assertTrue("assert \"total\":0", responseJson.indexOf("\"total\":0") != -1);

		// Step2 create one implicit community activity
		String activitiesURL = actServiceUser.getServiceURLString() + URLConstants.ACTIVITIES_MY;
		Activity simpleActivity = new Activity(name,
				name, null, null, false, true);
		Entry implicitActivityResult = (Entry) actServiceUser.createCommunityActivity(activitiesURL,
				simpleActivity, communityUuid, "");
		// max loop seconds to wait for event saved into ES
		int MAX_SECONDS = 8;
		for(int i=0; i<MAX_SECONDS; i++){
			Thread.sleep(1000);
			responseJson = client.postQueryService(map, 200);
			assertNotNull(responseJson);
			LOGGER.fine("responseJson1: " + responseJson);
			if(responseJson.indexOf("\"total\":1") != -1){
				break;
			}
		}
		assertTrue("assert \"total\":1", responseJson.indexOf("\"total\":1") != -1);
		
		LOGGER.fine("ENDING TEST: testNumberOfNewActivity");
	}
}
