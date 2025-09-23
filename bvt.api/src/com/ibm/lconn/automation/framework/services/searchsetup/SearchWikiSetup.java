package com.ibm.lconn.automation.framework.services.searchsetup;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;

public class SearchWikiSetup {
	static UserPerspective user;

	private static WikisService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchWikiSetup.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Wikis Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.WIKIS.toString());
		service = user.getWikisService();
		assertTrue("Wikis service problem, service is NULL",service != null);
		LOGGER.debug("Finished Initializing Search Wikis Data Setup Test");
	}

	@Test
	public void createWiki() {
		deleteTests();

		// setup a new wiki used for testing
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(StringConstants.SEARCH_WIKI_NAME,
				"Can you find me", "carmen", wikiMembers, false);
		service.createWiki(newWiki);

		// look for the new wiki to confirm it was made
		Entry e = (Entry) service
				.getPublicWikiWithName(StringConstants.SEARCH_WIKI_NAME);
		if (e.getTitle().equals(StringConstants.SEARCH_WIKI_NAME)) {
			LOGGER.debug("SUCCESS: Found created wiki");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Could not find created wiki");
			assertTrue(false);
		}
	}

	public void deleteTests() {
		// delete the old tests to prevent conflicts
		Feed publicFeed = (Feed) service.getPublicWikisFeed("ps=50");
		for (Entry e : publicFeed.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_WIKI_NAME)) {
				service.deleteWiki(e.getEditLinkResolvedHref().toString());
				break;
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}