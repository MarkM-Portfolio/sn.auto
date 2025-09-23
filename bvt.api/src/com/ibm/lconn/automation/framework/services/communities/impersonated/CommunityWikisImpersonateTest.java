package com.ibm.lconn.automation.framework.services.communities.impersonated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.CommunityWikisTestBase;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Ping - wangpin@us.ibm.com
 */
public class CommunityWikisImpersonateTest extends CommunityWikisTestBase {

	private static UserPerspective impersonateByotherUser;

	private static WikisService otherUserImService;

	// fictitious - creation/update date
	private static Date created = new Date(1409656000);

	private static Date modified = new Date(1409659000);

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityWikisImpersonateTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing CommunityWikis impersonate Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();

		// For this test we only test Wikis impersonate, not community's
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.WIKIS.toString());
		user = userEnv.getLoginUser();
		wikisService = user.getWikisService();
		imUser = userEnv.getImpersonatedUser();

		impersonateByotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.WIKIS.toString(),
				StringConstants.CURRENT_USER);
		otherUserImService = impersonateByotherUser.getWikisService();

		LOGGER.debug("Finished Initializing Communities Data impersonate Test");
	}

	@Test(enabled = false)
	public void testCommunityWikis() throws Exception {
		/*
		 * Tests the ability to get wiki pages associated with the community.
		 * Step 1: Create a community Step 2: Add a wiki widget (in that
		 * community) Step 3: Add a page to the wiki Step 4: Get wiki pages
		 * associated with the community. Step 5: Verify that the added page is
		 * there. Step 6: Update and Verify wikis title.
		 */
		LOGGER.debug("Beginning test: Get wiki pages associated with community");
		ExtensibleElement eEle;
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a community");
		Community mCommunity = new Community("Test CommunityM1 " + randString,
				"content", Permissions.PRIVATE, "tagCommunities_"
						+ Utils.logDateFormatter.format(new Date()));
		Entry response = (Entry) (service.createCommunity(mCommunity)); // community
		// created
		assertEquals("Create file", 201, service.getRespStatus());
		// assertEquals("impersonated user Name not match",
		// imUser.getRealName(), ((Entry)eEle2).getAuthor().getName());

		LOGGER.debug("Step 2: Add a wiki widget (in that community)");
		Community addedCommunity = new Community(
				(Entry) service.getCommunity(response.getEditLinkResolvedHref()
						.toString()));

		LOGGER.debug("checking Is the widget already available in the community?"); // smartcloud
		// added
		// wiki
		// during
		// comm
		// creation
		Feed widgetsFinalFeed = (Feed) service
				.getCommunityWidgets(addedCommunity.getUuid());
		boolean foundWidget = false;
		for (Entry e : widgetsFinalFeed.getEntries()) {
			if (e.getTitle().equalsIgnoreCase("Wiki"))
				foundWidget = true;
		}

		if (!foundWidget) {
			Widget widget = new Widget(StringConstants.WidgetID.Wiki.toString());
			eEle = service.postWidget(addedCommunity, widget.toEntry());
			assertEquals("Create Wiki widget", 201, service.getRespStatus());
			// assertEquals("impersonated user Name not match",
			// imUser.getRealName(), ((Entry)eEle).getAuthor().getName());

		}

		LOGGER.debug("Step 3: Add a page to the wiki");
		WikiPage pageToAdd = new WikiPage("Captain of the tests" + randString,
				"Ian", "TagYoureIt", created, modified);
		eEle = wikisService.createWikiPageInCommunity(addedCommunity.getUuid(),
				pageToAdd);
		assertEquals("Create Wiki page", 201, wikisService.getRespStatus());
		assertEquals("impersonated user Name not match", imUser.getRealName(),
				((Entry) eEle).getAuthor().getName());

		// get publish/update Data
		// Wiki wiki = new Wiki((Entry)eEle);
		// publishedDate = wiki.getPublished();
		// updatedDate = wiki.getUpdated();
		String editUrl = ((Entry) eEle).getEditLink().getHref().toString();

		eEle = otherUserImService.createWikiPageInCommunity(
				addedCommunity.getUuid(), pageToAdd);
		assertEquals("Create Wiki page", 403,
				otherUserImService.getRespStatus());

		LOGGER.debug("Step 4: Get wiki pages associated with the community.");
		Feed pagesFeed = (Feed) wikisService
				.getWikiPagesInCommunity(addedCommunity.getUuid());
		assertEquals("Get Wiki feed", 200, wikisService.getRespStatus());

		LOGGER.debug("Step 5: Verify that the added page is there.");
		boolean foundPage = false;
		for (Entry pageEntry : pagesFeed.getEntries()) {
			if (pageEntry.getTitle().equals(pageToAdd.getTitle())) {
				assertEquals("impersonated user Name not match",
						imUser.getRealName(), pageEntry.getAuthor().getName());
				foundPage = true;
			}
		}
		assertTrue(foundPage);

		LOGGER.debug("Step 6: Update and Verify wikis title .");
		pagesFeed = (Feed) wikisService.getListWikisCommunity(addedCommunity
				.getUuid());
		Entry wikiOfCommunity = (Entry) wikisService
				.getWikiOfCommunity(addedCommunity.getUuid());
		assertEquals("get Wiki page", 200, wikisService.getRespStatus());
		assertTrue("impersonated user Name not match",
				imUser.getRealName() != wikiOfCommunity.getAuthor().getName());
		// get Author as <[System User]>

		// create a wiki used to update the current wiki
		String newWikiTitle = "update_Wiki_page" + randString;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		WikiPage updatedWikiPage = new WikiPage(newWikiTitle, "Jim", "terran",
				created, modified);
		// eEle = wikisService.putWikiOfCommunity(addedCommunity.getUuid(),
		// newWiki);
		eEle = wikisService.genericPut(editUrl, updatedWikiPage.toEntry());
		assertEquals("update Wiki page", 200, wikisService.getRespStatus());
		assertEquals("impersonated user Name not match", imUser.getRealName(),
				((Entry) eEle).getAuthor().getName());
		assertEquals("impersonated created time not match", created.getTime(),
				updatedWikiPage.getCreated().getTime());

		LOGGER.debug("Ending Test: GET wiki pages associated with a community");

	}

	//TJB 5/4/15 This test seems to break testCommunityWikis.  I've seen this same issue with other apps
	// where the cross org test had to be commented out to prevent problems with tests that execute
	// subsequently.  Research needed.
//	@Test
	public void testCommunityWikisCrossOrg() throws Exception {
		super.testCommunityWikisCrossOrg();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		wikisService.tearDown();
	}

}