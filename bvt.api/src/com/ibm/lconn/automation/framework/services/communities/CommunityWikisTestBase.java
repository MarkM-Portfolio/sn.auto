package com.ibm.lconn.automation.framework.services.communities;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public abstract class CommunityWikisTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityWikisTestBase.class.getName());

	protected static UserPerspective user, imUser;

	protected static CommunitiesService service;

	protected static WikisService wikisService;

	@Test
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
				"Ian", "TagYoureIt");
		eEle = wikisService.createWikiPageInCommunity(addedCommunity.getUuid(),
				pageToAdd);
		assertEquals("Create Wiki page", 201, wikisService.getRespStatus());
		assertEquals("impersonated user Name not match", imUser.getRealName(),
				((Entry) eEle).getAuthor().getName());

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

		// create a wiki used to update the current wiki
		String newWikiTitle = "CommWiki" + randString;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(newWikiTitle, "Jim", "terran", wikiMembers,
				false);
		eEle = wikisService.putWikiOfCommunity(addedCommunity.getUuid(),
				newWiki);
		// assertEquals("update Wiki page", 201, service.getRespStatus());
		// assertEquals("impersonated user Name not match",
		// imUser.getRealName(), ((Entry)eEle).getAuthor().getName());
		// Error: impersonated user Name not match expected:<[Amy Jones2]> but
		// was:<[System User]>

		// get the entry document
		wikiOfCommunity = (Entry) wikisService
				.getWikiOfCommunity(addedCommunity.getUuid());
		assertTrue(wikiOfCommunity != null);

		// check if the wiki has the new title
		if (wikiOfCommunity.getTitle().equals(newWikiTitle)) {
			LOGGER.debug("Test Successful: Wiki associated with the created community was updated");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Wiki associated with community failed to update");
			assertTrue(false);
		}

		LOGGER.debug("Ending Test: GET wiki pages associated with a community");

	}

	@Test
	public void testCommunityWikisCrossOrg() throws Exception {
		/*
		 * Tests the ability to create a wiki page as a user from outside the
		 * org. Step 1: Create a community Step 2: Set the X-LConn-RunAs header
		 * as a user from outside the org Step 3: Create a wiki page. Step 4:
		 * Validate that the author is the currently logged in user, not the out
		 * of org user.
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Beginning test: Get wiki pages associated with community");
			ExtensibleElement eEle;
			String randString = RandomStringUtils.randomAlphanumeric(4);

			LOGGER.debug("Step 1: Create a community");
			Community mCommunity = new Community(
					"Test Wiki with Cross Org Impersonation." + randString,
					"content", Permissions.PRIVATE, "tagCommunities_"
							+ Utils.logDateFormatter.format(new Date()));
			Entry response = (Entry) (service.createCommunity(mCommunity)); // community
			// created
			assertEquals("Create file", 201, service.getRespStatus());

			LOGGER.debug("Step 2: Add a wiki widget (in that community)");
			Community addedCommunity = new Community(
					(Entry) service.getCommunity(response
							.getEditLinkResolvedHref().toString()));

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
				Widget widget = new Widget(
						StringConstants.WidgetID.Wiki.toString());
				eEle = service.postWidget(addedCommunity, widget.toEntry());
				assertEquals("Create Wiki widget", 201, service.getRespStatus());
				// assertEquals("impersonated user Name not match",
				// imUser.getRealName(), ((Entry)eEle).getAuthor().getName());

			}

			LOGGER.debug("Step 3: Add a page to the wiki");
			WikiPage pageToAdd = new WikiPage(
					"Wiki page using impersonation.  This one should work."
							+ randString, "Ian", "TagYoureIt");
			eEle = wikisService.createWikiPageInCommunity(
					addedCommunity.getUuid(), pageToAdd);
			// onprem 201, SC 200
			// assertEquals("Create Wiki page", 201, service.getRespStatus());
			assertEquals("impersonated user Name not match",
					imUser.getRealName(), ((Entry) eEle).getAuthor().getName());

			int ORG_B_REGULAR_USER_INDEX = 15;
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";
			boolean useSSL = true;

			// Org B regular user - Jill White
			UserPerspective orgBRegular = new UserPerspective(
					ORG_B_REGULAR_USER_INDEX, Component.COMMUNITIES.toString(),
					useSSL);

			LOGGER.debug("Before setting the request option, X-LConn-RunAs : "
					+ wikisService.getRequestOption("X-LConn-RunAs"));

			// Org A Admin uses Org B regular user for impersonation.
			wikisService.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId());
			LOGGER.debug("After setting the request option, X-LConn-RunAs : "
					+ wikisService.getRequestOption("X-LConn-RunAs"));

			// Create a wiki page. This should work, but the author must not be
			// the impersonated user. The author should
			// be the user who is acutally logged in.
			WikiPage pageToAdd2 = new WikiPage(
					"Wiki Page using Cross Org Impersonation. This should work, but the author should not the impersonated user."
							+ randString, "Ian", "tag_youre_it");
			Entry wikiEntry = (Entry) wikisService.createWikiPageInCommunity(
					addedCommunity.getUuid(), pageToAdd2);
			assertEquals("Expected response code not returned", 201,
					wikisService.getRespStatus());
			String authorName = wikiEntry.getAuthor().getName();
			assertEquals("Expected author name not returned",
					user.getRealName(), authorName);

		}
	} // end smartcloud if

}
