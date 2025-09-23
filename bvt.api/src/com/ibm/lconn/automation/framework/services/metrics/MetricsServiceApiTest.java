package com.ibm.lconn.automation.framework.services.metrics;

import static org.testng.AssertJUnit.assertTrue;
import java.util.HashMap;
import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;

public class MetricsServiceApiTest {
	private static ProfileData profile;
	
	private final static Logger LOGGER = Logger.getLogger( MetricsServiceApiTest.class.getName());
	
	private static MetricsHttpClient client;
	
	@BeforeClass
	public static void setUp() throws Exception {
		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}
		profile = ProfileLoader.getProfile(StringConstants.ADMIN_USER);
		client = new MetricsHttpClient(profile);
	}
	
	//@Test
	public void testMostActiveCommunityRandom() throws Exception {
		
		LOGGER.fine("BEGINNING TEST: testMostActiveCommunityEmpty");
		if(StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			
			String uri = "/communities/mostActive";
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("orgId", "a");
			map.put("topItemSize", "50");
			String jsonStr = client.postMetricsService(uri, map, 200);
			try {
				JSONObject.parse(jsonStr);
				assertTrue("TEST ERROR: testMostActiveCommunityEmpty: " + jsonStr, jsonStr.indexOf("\"orgId\":\"a\"") != -1);
				assertTrue("TEST ERROR: testMostActiveCommunityEmpty: " + jsonStr, jsonStr.indexOf("\"name\":\"communities\"") != -1);
			} catch (Exception e) {
				assertTrue("TEST ERROR: testMostActiveCommunityEmpty: " + jsonStr, false);
			}
		}
		LOGGER.fine("ENDING TEST: testMostActiveCommunityEmpty");
	}
}
