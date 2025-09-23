/**
 * 
 */
package com.ibm.lconn.automation.framework.services.activities.test;

import static org.testng.AssertJUnit.assertTrue;

import java.util.Calendar;

import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.ValidationConstants;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

//import java.util.logging.FileHandler;

/**
 * JUnit Tests via Connections API for Activities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ActivitiesAPIValidationTests {

	static UserPerspective user;

	private static ActivitiesService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesAPIValidationTests.class.getName());

	// private static boolean useSSL = true;

	/**
	 * Test fixture to initialize Abdera, Connections Service Config, and
	 * Activities Service object. These objects are reused for all of the
	 * Activities test cases.
	 * 
	 * @throws Exception
	 *             if any of the above fail to initialize.
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Activities API Test");

		UsersEnvironment userEnv = new UsersEnvironment();

		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.ACTIVITIES.toString());
		service = user.getActivitiesService();

		LOGGER.debug("Finished Initializing Activities Data Population Test");
	}

	@Test
	public void validateCreateIncompleteActivity() {
		String title = ValidationConstants.ACTIVITY1_TITLE;
		String content = ValidationConstants.ACTIVITY1_CONTENT;
		// String tags = ValidationConstants.ACTIVITY1_TAGS;
		Calendar calendar = Calendar.getInstance();
		calendar.set(2013, 11, 20, 12, 30);

		LOGGER.debug("Creating Activity:");
		Activity incompleteActivity = new Activity(title, content, null,
				calendar.getTime(), false, false);
		Entry test = (Entry) service.createActivity(incompleteActivity);
		Activity activityResult = new Activity(test);

		assertTrue(activityResult.equals(incompleteActivity));
	}
}
