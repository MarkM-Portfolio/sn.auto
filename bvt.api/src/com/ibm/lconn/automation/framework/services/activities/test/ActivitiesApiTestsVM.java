/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
package com.ibm.lconn.automation.framework.services.activities.test;

import static org.testng.AssertJUnit.assertTrue;


import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

/**
 * JUnit Tests via Connections API for Activities Service(VM)
 * 
 * @author Jing Zhao
 */
public class ActivitiesApiTestsVM {

	private static ActivitiesService service,visitorService,extendedEmpService;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesApiTestsVM.class.getName());

	// private static boolean useSSL = true;
	static UserPerspective user,adminUser,visitor,invitor;;

	/**
	 * Set Test Users Environmemt
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Activities Data  Test");
		
		UsersEnvironment userEnv = new UsersEnvironment();
		
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.ACTIVITIES.toString());
		// get admin User, 0 is the admin user
		adminUser = userEnv.getLoginUserEnvironment(0,Component.ACTIVITIES.toString());
		service = user.getActivitiesService();
		LOGGER.debug("StringConstants.VMODEL_ENABLED:" + StringConstants.VMODEL_ENABLED );
		
		if (StringConstants.VMODEL_ENABLED) {
			visitor = userEnv.getLoginUserEnvironment(StringConstants.EXTERNAL_USER,Component.ACTIVITIES.toString());
			LOGGER.debug("visitor info :" + visitor.getEmail());
			visitorService = visitor.getActivitiesService();

			invitor = userEnv.getLoginUserEnvironment(StringConstants.EMPLOYEE_EXTENDED_USER,Component.ACTIVITIES.toString());
			extendedEmpService = invitor.getActivitiesService();
		}

		assert (service != null);
		LOGGER.debug("Finished Initializing Activities Data  Test");
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDown() throws Exception {
		service.tearDown();
		if (StringConstants.VMODEL_ENABLED)
		{
		 visitorService.tearDown();
		 extendedEmpService.tearDown();
		}
	}
	
	/**
	 * 1. check the status of the feature 2. if it is disabled, then enabled it
	 * and then check again. 3. any excpetion will return false
	 * 
	 * { "organisation": "00000000-0000-0000-0000-000000000000", "settings": [ {
	 * "description": "Included for test purposes only 1", "isDefault": false,
	 * "javascriptName": null, "name": "TEST_FEATURE1", "value": true } ],
	 * "source": "highway" }
	 * 
	 * @param feature
	 * @return
	 * @throws Exception
	 */
	public boolean testAndEnableFeature(String featureName, boolean isOnPremise)
			throws Exception {
		boolean bFeatureEanbled = false;
		if (isOnPremise) {
			try {
				String json_isDefualt_true = "\"isDefault\": true";
				String json_isDefualt_false = "\"isDefault\": false";
				String json_value_true = "\"value\": true";
				String json_value_false = "\"value\": false";

				ActivitiesService oaService = adminUser.getActivitiesService();
				String url = URLConstants.SERVER_URL
						+ "/connections/config/rest/gatekeeper/00000000-0000-0000-0000-000000000000/"
						+ featureName;
				String response = oaService.getResponseString(url);
				System.out.println("response1: " + response);
				// have meaningful content return.
				if (response.indexOf(featureName) != -1) {
					if (-1 == response.indexOf(json_value_true)) {
						// need to enable the feature
						boolean bFoundIsDefaultTrue = response
								.indexOf(json_isDefualt_true) != -1 ? true
								: false;
						String request = response;
						if (bFoundIsDefaultTrue) {
							request = response.replace(json_isDefualt_true,
									json_isDefualt_false);
						}
						request = request.replace(json_value_false,
								json_value_true);
						System.out.println("beforePost2: " + request);
						response = oaService.postResponseString(url, request);
						System.out.println("afterPost3: " + response);
						if (-1 != response.indexOf(json_value_true)) {
							bFeatureEanbled = true;
						}
					} else {
						bFeatureEanbled = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("testFeatureEnabled(): " + featureName + " = "
				+ bFeatureEanbled);
		return bFeatureEanbled;
	}
	
	@Test
	public  void visitorToCreateActivity() throws Exception {
		if (StringConstants.VMODEL_ENABLED && StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE && this.testAndEnableFeature(
						"CONNECTIONS_VISITOR_MODEL",true)) {
			// add an activity for test
			Activity simpleActivity = new Activity(
				"test activity no access post", "content", null, null,false, false);
			Entry activityResult = (Entry) visitorService.createActivity(simpleActivity);
			LOGGER.debug("visitorToCreateActivity: activity result:"+activityResult);
			assertTrue("Visitor can not create activity !" ,visitorService.getRespStatus()==403);
		}
	}
}
