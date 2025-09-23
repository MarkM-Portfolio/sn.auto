/**
 * 
 */
package com.ibm.lconn.automation.framework.search.rest.api.tests.cloud.only;

import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class UnpopulateForSearchTests {

	private static ActivitiesService service;
	private static CommunitiesService commService;

	protected final static Logger LOGGER = LoggerFactory.getLogger(UnpopulateForSearchTests.class.getName());

	static UserPerspective user;

	/**
	 * Set Test Users Environmemt
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.debug("Start Initializing Data Removal Test");

		UsersEnvironment userEnv = new UsersEnvironment();

		user = userEnv.getLoginUserEnvironment(3, Component.ACTIVITIES.toString());
		LOGGER.debug("User for removal is: " + user.getEmail());
		service = user.getActivitiesService();

		user = userEnv.getLoginUserEnvironment(3, Component.COMMUNITIES.toString());
		commService = user.getCommunitiesService();

		assert (service != null);
		LOGGER.debug("Finished Initializing Data Removal Test");
	}

	@Test
	public void deleteAllActivities() {
		LOGGER.debug("Start Deleting activities");

		ArrayList<Activity> activities = service.getMy75Activities();

		int count = 0;
		for (Activity activity : activities) {
			// Delete only activities created for Japanese tests
			String japaneseExecId = SearchRestAPIUtils.getExecId(Purpose.JAPANESE);
			if (activity.getTitle().contains(japaneseExecId)) {
				count++;
				// Make sure we have permissions to delete activity before trying to
				// delete
				if (activity.getPermissions().contains(StringConstants.PERMISSIONS_DELETE_ACTIVITY)) {
					if (service.deleteActivity(activity.getEditHref())) {
						LOGGER.debug("Deleted Activity: " + activity.getTitle());
					} else {
						LOGGER.error("Failed to delete Activity: " + activity.getTitle());
					}
				} else {
					LOGGER.debug("Insufficient permissions to delete Activity: " + activity.getTitle());
				}
			}
		}
		LOGGER.debug("Finished Deleting activities, number of deleted activities is: " + count);
	}

	@Test
	public void deleteRecommendationsCommunities() {
		LOGGER.debug("Start Deleting communities");

		ArrayList<Community> communities = new ArrayList<Community>();
		ExtensibleElement communitiesFeed = commService.getAllCommunities(true, null, 0, 0, null, null, null,
				SearchRestAPIUtils.generateTagValue(Purpose.RECOMMENDATIONS), null);

		for (Entry communityEntry : ((Feed) communitiesFeed).getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			// Make sure we have permissions to delete activity before trying to delete
			if (commService.deleteCommunity(community.getEditLink())) {
				LOGGER.debug("Deleted Community: " + community.getTitle());
			} else {
				LOGGER.error("Failed to delete Community: " + community.getTitle());
			}
		}
	}
}
