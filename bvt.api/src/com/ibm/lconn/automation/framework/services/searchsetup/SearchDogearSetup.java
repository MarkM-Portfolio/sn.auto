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
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;

/**
 * @author James Golin & David Yogodzinski
 */
public class SearchDogearSetup {

	static UserPerspective user;

	private static DogearService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchDogearSetup.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Dogear Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.DOGEAR.toString());
		service = user.getDogearService();
		assertTrue("Dogear service problem, service is NULL",service != null);
		LOGGER.debug("Finished Initializing Search Dogear Data Setup Test");
	}

	// Create a bookmark for later search tests
	@Test
	public void createBookmark() {
		deleteTests();
		boolean found = false;

		Bookmark newBookmark = new Bookmark(StringConstants.SEARCH_DOGEAR_NAME,
				"This is a bookmark for the Google search engine.",
				"http://www.google.com", "carmen");
		Entry result = (Entry) service.createBookmark(newBookmark);
		assertTrue(result != null);

		// look for the created bookmark and confirm it was made in the public
		// feed
		Feed publicFeed = (Feed) service.getPublicBookmarks();
		for (Entry e : publicFeed.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_DOGEAR_NAME)) {
				LOGGER.debug("SUCCESS: Found created bookmark");
				assertTrue(true);
				found = true;
				break;
			}
		}
		if (!found) {
			LOGGER.debug("ERROR: Could not find created bookmark");
			assertTrue(false);
		}
	}

	// delete previous tests.
	public void deleteTests() {
		Feed bookmarks = (Feed) service.getMyBookmarks();
		for (Entry e : bookmarks.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_DOGEAR_NAME)) {
				service.deleteBookmark(e.getEditLink().getHref().toString());
				break;
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}

}
