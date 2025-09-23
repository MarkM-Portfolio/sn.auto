package com.ibm.lconn.automation.framework.services.searchsetup;

import static org.testng.AssertJUnit.assertTrue;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class SearchCommunitiesSetup {

	static UserPerspective user;

	private static CommunitiesService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchCommunitiesSetup.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Communities Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();
		assertTrue("Community service problem, service is NULL",service != null);
		LOGGER.debug("Finished Initializing Search Communities Data Setup Test");
	}

	// Creates a community for later search tests
	@Test
	public void createCommunity() {
		deleteTests();
		boolean found = false;

		// create a community to test later
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(StringConstants.SEARCH_COMMUNITY_NAME,
					"Search for this Community", Permissions.PRIVATE, "carmen");
		} else {
			// On-Premise deployment.
			newCommunity = new Community(StringConstants.SEARCH_COMMUNITY_NAME,
					"Search for this Community", Permissions.PUBLIC, "carmen");
		}

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		// confirm that the community was made
		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				500, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		for (Entry e : communitiesFeed.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_COMMUNITY_NAME)) {
				LOGGER.debug("SUCCESS: Found created community");
				assertTrue(true);
				found = true;
				break;
			}
		}
		if (!found) {
			LOGGER.debug("ERROR: Could not find created community");
			assertTrue(false);
		}
	}

	public void deleteTests() {
		// delete the last test to prevent conflicts
		Feed communities = (Feed) service.getMyCommunities(false, null, 0, 50,
				null, null, null, null, null);
		assertTrue(communities != null);

		for (Entry community : communities.getEntries()) {
			if (community.getTitle().equals(
					StringConstants.SEARCH_COMMUNITY_NAME)) {
				service.deleteCommunity(community.getEditLinkResolvedHref()
						.toString());
				break;
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}

}
