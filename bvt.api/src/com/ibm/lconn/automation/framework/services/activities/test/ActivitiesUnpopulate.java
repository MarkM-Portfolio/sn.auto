/**
 * 
 */
package com.ibm.lconn.automation.framework.services.activities.test;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

/**
 * JUnit Tests via Connections API for Activities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ActivitiesUnpopulate {

	private static ActivitiesService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesUnpopulate.class.getName());

	// private static boolean useSSL = true;
	static UserPerspective user;

	/**
	 * Set Test Users Environmemt
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Activities Data Removal Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.ACTIVITIES.toString());
		service = user.getActivitiesService();

		assert (service != null);
		LOGGER.debug("Finished Initializing Activities Data Removal Test");
	}

	@Test
	public void deleteAllActivities() {
		LOGGER.debug("Start Deleting all of the current user's Activities");

		ArrayList<Activity> activities = service.getMy75Activities();

		if (activities != null) {
			LOGGER.debug("Number of activities to delete: " + activities.size());
		}

		for (Activity activity : activities) {
			// Make sure we have permissions to delete activity before trying to
			// delete
			if (activity.getPermissions().contains(
					StringConstants.PERMISSIONS_DELETE_ACTIVITY)) {
				if (service.deleteActivity(activity.getEditHref())) {
					LOGGER.debug("Deleted Activity: " + activity.getTitle());
				} else {
					LOGGER.error("Failed to delete Activity: "
							+ activity.getTitle());
				}
			} else {
				LOGGER.debug("Insufficient permissions to delete Activity: "
						+ activity.getTitle());
			}
		}
		LOGGER.debug("Finished Deleting all of the current user's Activities");
	}
}
