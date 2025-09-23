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
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/**
 * 
 * 
 * @author David Yogodzinski
 */
public class SearchForumsSetup {
	static UserPerspective user;

	private static ForumsService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchForumsSetup.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Forums Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.FORUMS.toString());
		service = user.getForumsService();
		assertTrue("Forums service problem, service is NULL",service != null);
		LOGGER.debug("Finished Initializing Search Forums Data Setup Test");
	}

	@Test
	public void createSingleForum() {
		deleteTests();
		boolean found = false;

		Forum forum = new Forum(StringConstants.SEARCH_FORUM_NAME, "search");
		service.createForum(forum);

		Feed myForums = (Feed) service.getMyForums(null, null, 0, 0, null,
				null, null, null, null, null, null);
		for (Entry e : myForums.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_FORUM_NAME)) {
				LOGGER.debug("SUCCESS: Found created fourm");
				assertTrue(true);
				found = true;
				break;
			}
		}
		if (!found) {
			LOGGER.debug("ERROR: Could not find created forum");
			assertTrue(false);
		}
	}

	public void deleteTests() {
		Feed forums = (Feed) service.getAllForums(null, null, 0, 100, null,
				null, null, null, null, null, null);
		assertTrue(forums != null);

		for (Entry e : forums.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_FORUM_NAME)) {
				service.deleteForum(e.getEditLinkResolvedHref().toString());
				break;
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}