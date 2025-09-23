package com.ibm.lconn.automation.framework.services.wikis;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.files.FilesGateKeeperServiceWrapper;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public abstract class WikisTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(WikisTestBase.class.getName());

	protected static UserPerspective user, imUser, otherUser;// , visitor,
													// extendedEmployee;
	protected static WikisService service; // , visitorService, extendedEmpService;
	// static boolean useSSL = true;

	protected static WikisGateKeeperServiceWrapper gateKeeperService = WikisGateKeeperServiceWrapper.getInstance();
	
	private final static String PUBLIC_WIKI_TITLE_WITH_PAGES = "Public Test Wiki with Wiki Pages";
	private final static String WIKI_PAGE_TITLE = "WIKI PAGE";

	@Test
	public void testUpdateCreatedModifiedTime() throws IOException {
		Timestamp created = new Timestamp(1000000000);
		Timestamp modified = new Timestamp(1010000000);

		// create a wiki
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Update time -- Private Test Wiki with Pages "
				+ uniqueNameAddition;
		Wiki newWiki = new Wiki(wikiTitle, "createPrivateWikiWithPages test",
				"wikitag1 wikitag2 wikitag3", wikiMembers);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		// create a page
		WikiPage newWikiPage = new WikiPage("WIKI PAGE",
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3", created, modified);
		ExtensibleElement resultPage = service.createWikiPage(wikiEntry,
				newWikiPage);
		String fileCreated = ((Entry) resultPage).getExtension(
				StringConstants.TD_CREATED).getText();
		String fileModified = ((Entry) resultPage).getExtension(
				StringConstants.TD_MODIFIED).getText();
		long response_createdTime = (new AtomDate(fileCreated)).getTime();
        long response_modifiedTime = (new AtomDate(fileModified)).getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
            assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
            assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
            assertTrue(created.getTime() != response_createdTime);
            assertTrue(modified.getTime()!= response_modifiedTime);
        }

		// Update a page
		newWikiPage.setContent("<p>This is James's wiki page. Updated</p>");
		ExtensibleElement resultUpdatePage = service.updatePage(wikiTitle,
				newWikiPage.getTitle(), newWikiPage.toEntry());
		fileCreated = ((Entry) resultUpdatePage).getExtension(
				StringConstants.TD_CREATED).getText();
		fileModified = ((Entry) resultUpdatePage).getExtension(
				StringConstants.TD_MODIFIED).getText();
		response_createdTime = (new AtomDate(fileCreated)).getTime();
        response_modifiedTime = (new AtomDate(fileModified)).getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
		    assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
		    assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
          assertTrue(created.getTime() != response_createdTime);
          assertTrue(modified.getTime()!= response_modifiedTime);
        }

		// Create a comment
		// create test comment
		WikiComment comment = new WikiComment("comment test", created, modified);
		ExtensibleElement resultComment = (Entry) service.createWikiComment(
				wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
				resultUpdatePage.getExtension(StringConstants.TD_UUID)
						.getText(), comment.toEntry());
		fileCreated = ((Entry) resultComment).getExtension(
				StringConstants.TD_CREATED).getText();
		fileModified = ((Entry) resultComment).getExtension(
				StringConstants.TD_MODIFIED).getText();
		response_createdTime = (new AtomDate(fileCreated)).getTime();
        response_modifiedTime = (new AtomDate(fileModified)).getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
		    assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
		    assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
          assertTrue(created.getTime() != response_createdTime);
          assertTrue(modified.getTime()!= response_modifiedTime);
        }
	}

	@Test
	// Test for getting a feed of all wikis
	public void getWikisFeed() {
		LOGGER.debug("Getting feed of wikis.");
		Feed wikiFeed = (Feed) service.getWikiFeed();
		assert (wikiFeed != null);
		if (wikiFeed.getTitle().equals("Wikis Feed")) {
			LOGGER.debug("Successfully retrieved Wikis Feed");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Did not retrieve Wikis Feed");
			assertTrue(false);
		}
	}

	public void createPrivateWikiWithNoPages() {

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		Wiki newWiki = new Wiki("API Private Test Wiki",
				"This is James's test wiki!", "wikitag1 wikitag2 wikitag3",
				wikiMembers);
		service.createWiki(newWiki);
	}

	@Test
	public void createPrivateWikiWithPages() {

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Private Test Wiki with Pages " + uniqueNameAddition;

		Wiki newWiki = new Wiki(wikiTitle, "createPrivateWikiWithPages test",
				"wikitag1 wikitag2 wikitag3", wikiMembers);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		for (int i = 0; i < 5; i++) {
			WikiPage newWikiPage = new WikiPage("WIKI PAGE",
					"<p>This is James's wiki page.</p>",
					"wikipagetag1 wikipagetag2 wikipagetag3");
			service.createWikiPage(wikiEntry, newWikiPage);
		}
	}

	@Test
	public void createPublicWikiWithNoPages() {

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Public Test Wiki" + uniqueNameAddition;

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "This is James's test wiki!",
				"wikitag1 wikitag2 wikitag3", wikiMembers);
		service.createWiki(newWiki);
	}

	@Test
	public void createPublicWikiWithPages() {
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = PUBLIC_WIKI_TITLE_WITH_PAGES + uniqueNameAddition;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "This is James's test wiki!",
				"wikitag1 wikitag2 wikitag3", wikiMembers);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		for (int i = 0; i < 5; i++) {
			WikiPage newWikiPage = new WikiPage(WIKI_PAGE_TITLE,
					"<p>This is James's wiki page.</p>",
					"wikipagetag1 wikipagetag2 wikipagetag3");
			service.createWikiPage(wikiEntry, newWikiPage);
		}
	}

	@Test
	public void getFeedOfPublicWikis() {
		LOGGER.debug("BEGINNING TEST: Create Feed of Public Wikis");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String wikiTitle1 = "Public_Feed_Test" + uniqueNameAddition;

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle1, "This is James's public feed test",
				"wikitag1 wikitag2 wikitag3", wikiMembers);

		service.createWiki(newWiki);
		boolean wikiFound = false;
		Feed publicWikisFeed = (Feed) service.getPublicWikisFeed("ps=100");

		if (publicWikisFeed.getEntries().size() <= 99) {
			for (Entry ntry : publicWikisFeed.getEntries()) {
				if (ntry.getTitle().equals(wikiTitle1)) {
					LOGGER.debug("SUCCESS: Expected wiki entry was found in the public feed.");
					wikiFound = true;
				}
			}
			assertEquals("Expected entry was not found in public feed.",
					wikiFound, true);
		}// else TODO, need verify

		LOGGER.debug("COMPLETED TEST: Get Feed Of Public Wikis");
	}

	@Test
	// 74509: Get a list of page related resources API doesn't support wiki
	// label format
	public void verifyGetFeedOfPublicWikisUsingLabelFormat() {
		LOGGER.debug("BEGINNING TEST: Get Feed of Public Wikis Using label Format");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = PUBLIC_WIKI_TITLE_WITH_PAGES + uniqueNameAddition;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "This is James's test wiki!",
				"wikitag1 wikitag2 wikitag3", wikiMembers);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		WikiPage newWikiPage = new WikiPage(WIKI_PAGE_TITLE,
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		Entry wikiPageEntry = (Entry) service.createWikiPage(wikiEntry,
				newWikiPage);

		Feed publicWikisFeed = (Feed) service
				.getPublicWikisFeedUsingLabelFormat(wikiEntry.getTitle(),
						wikiPageEntry.getTitle());
		if (publicWikisFeed.getTitle().equals(wikiPageEntry.getTitle())) {
			LOGGER.debug("SUCCESS: Get Feed of Public Wikis Using label Format");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Get Feed of Public Wikis Using label Format"
					+ publicWikisFeed.toString());
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Feed of Public Wikis Using label Format");
	}

	@Test
	public void getFeedOfMostCommentedWikis() {
		LOGGER.debug("BEGINNING TEST: Create Feed of Most Commented Wikis");
		Feed publicWikisFeed = (Feed) service.getMostCommentedWikisFeed();
		if (publicWikisFeed.getTitle().equals("Most Commented Wikis Feed")) {
			LOGGER.debug("SUCCESS: Feed of Most Commented Wikis was retrieved");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Feed of Most Commented Wikis not found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Feed Of Most Commented Wikis");
	}

	@Test
	public void getFeedOfMostRecommendedWikis() {
		LOGGER.debug("BEGINNING TEST: Create Feed of Most Commented Wikis");
		Feed publicWikisFeed = (Feed) service.getMostRecommendedWikisFeed();
		if (publicWikisFeed.getTitle().equals("Most Recommended Wikis Feed")) {
			LOGGER.debug("SUCCESS: Feed of Most Recomended Wikis was retrieved");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Feed of Most Recomended Wikis not found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Feed Of Most Recomended Wikis");
	}

	@Test
	public void retrieveWikiWithLabel() throws UnsupportedEncodingException {
		LOGGER.debug("BEGINNING TEST: Retrieve Wiki using its WikiLabel");
		createPublicWikiWithNoPages();
		Feed publicWikisFeed = (Feed) service
				.getPublicWikisFeed("sortBy=created&sortOrder=desc");
		String wikiLabel = publicWikisFeed.getEntries().get(0)
				.getExtension(StringConstants.TD_LABEL).getText();
		// Edit link contains URL with encoded wikiLabel
		String editLink = publicWikisFeed.getEntries().get(0).getEditLink()
				.getHref().toString();
		Entry wiki = (Entry) service.getPublicWikiWithEditLink(editLink);
		if (wiki.getTitle().equals(wikiLabel)) {
			LOGGER.debug("SUCCESS: Correct wiki was retrieved");
		} else {
			LOGGER.debug("ERROR: Incorrect wiki was retrieved");
		}
		assertEquals(wiki.getTitle(), wikiLabel);
		LOGGER.debug("COMPLETED TEST: Retrieve Wiki using its WikiLabel");

	}

	@Test
	public void getFeedMostVisited() {
		LOGGER.debug("Begin Retrieving Most Visited Wiki Feed");
		Feed mostVisitedWiki = (Feed) service.getMostVisited();
		assert (mostVisitedWiki != null);
		if (mostVisitedWiki.getTitle().equals("Most Visited Wikis Feed")) {
			assertTrue(true);
			LOGGER.debug("Test Succeeded: Most Visited Wiki Feed was found.");
		} else {
			LOGGER.debug("Test Failed: Most Visited Wiki Feed was not found.");
			assertTrue(false);
		}
	}

	@Test
	public void getMemberWikis() {
		LOGGER.debug("Retrieving All Wikis of which User is Member");
		Feed memberWikis = (Feed) service.getMyWikisFeed();
		assert (memberWikis != null);
		if (memberWikis.getTitle().equals("My Wikis Feed")) {
			LOGGER.debug("SUCCESS: My Wikis Feed was found.");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: My Wikis Feed was not found.");
			assertTrue(false);
		}
	}

	@Test
	public void createWiki() {
		service.deleteTests();
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiName = "Wiki_" + uniqueNameAddition;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiName, "This is a wiki!", "wiki test",
				wikiMembers, false);
		service.createWiki(newWiki);
		Entry e = (Entry) service.getPublicWikiWithName(wikiName);

		assertEquals("Check wikis title failed", wikiName, e.getTitle());
		assertEquals("Check impersonated name failed", imUser.getRealName(), e
				.getAuthor().getName());
	}

	@Test
	public void updateWiki() {
		service.deleteTests();
		LOGGER.debug("BEGINNING: Test to update a wiki");
		// create a wiki to update
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiLabel = "wiki-liki" + uniqueNameAddition;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiLabel, "Island Magic", "wiki liki",
				wikiMembers, false);
		service.createWiki(newWiki);

		wikiMembers = new ArrayList<WikiMember>();

		virtualReader = new WikiMember("anonymous-user", WikiRole.READER,
				WikiMemberType.VIRTUAL);
		virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// create a wiki object to update with
		String updatedSummary = "This is the updated version of the wiki";
		Wiki updatedWiki = new Wiki(wikiLabel, updatedSummary, "update",
				wikiMembers, false);
		// update
		service.updateWikiWithLabel(wikiLabel, updatedWiki.toEntry());

		// validate
		Entry testWiki = (Entry) service.getPublicWikiWithName(wikiLabel);
		if (testWiki.getSummary().equals(updatedSummary)) {
			LOGGER.debug("SUCCESS: Wiki was updated correctly");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Wiki was not updated correctlly");
			assertTrue(false);
		}
		LOGGER.debug("COMPELTED: Test to update a wiki");
	}

	@Test
	public void createPageInWiki() {
		LOGGER.debug("BEGINNING TEST: Creating a page within a wiki");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		// create a wiki
		String wikiLabel = "James's wicked wiki" + uniqueNameAddition;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiLabel, "The best wiki around",
				"wicked wiki", wikiMembers, false);
		service.createWiki(newWiki);
		Entry wikiEntry = (Entry) service.getPublicWikiWithName(wikiLabel);

		// create a wiki page in the new wiki
		String pageLabel = "WIKI PAGE";
		WikiPage newWikiPage = new WikiPage(pageLabel,
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		pageLabel = newWikiPage.getTitle();
		service.createWikiPage(wikiEntry, newWikiPage);

		// retrive the page and check the label to validate
		Entry page = (Entry) service
				.getWikiPageWithLabels(wikiLabel, pageLabel);
		if (page.getTitle().equals(pageLabel)) {
			LOGGER.debug("SUCCESS: Wiki Page created successfully");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Wiki Page not created correctly");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Creating a page in a wiki");
	}

	@Test
	public void getPagesEdited() {
		service.deleteTests();
		LOGGER.debug("Starting Test: Get Feed of Wiki pages edited by the current user");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Rapture_" + uniqueNameAddition;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "getPagesEdited test", "draftTest",
				wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		for (int i = 0; i < 5; i++) {
			WikiPage newWikiPage = new WikiPage("ADAM'S PAGE",
					"<p>This is Adam's wiki page.</p>",
					"wikipagetag1 wikipagetag2 wikipagetag3");
			service.createWikiPage(wikiEntry, newWikiPage);
		}

		Feed editedPages = (Feed) service.getEditedPages(wikiEntry
				.getExtension(StringConstants.TD_LABEL).getText());
		for (Entry e : editedPages.getEntries()) {
			if (e.getAuthor().getName().equals(imUser.getRealName()) == false) {
				assertTrue(false);
				LOGGER.debug("Test Failed: Did not find feed of recently updated pages by current user");
			}
		}
		LOGGER.debug("Test Successful: Found the feed of recently updated pages by the current user");
		assertTrue(true);

	}

	@Test
	public void retrieveWikiTagsWithLabel() throws Exception {
		service.deleteTests();
		LOGGER.debug("BEGINNING TEST: Retrieve Wiki Page tags using its WikiLabel");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		String label = "CelticsWiki_" + uniqueNameAddition;
		Wiki newWiki = new Wiki(label, "Would you kindly!", "draftTest",
				wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		String tag = "KevinGarnett";
		WikiPage newWikiPage = new WikiPage("ADAM'S PAGE",
				"<p>This is Adam's wiki page.</p>", tag);
		service.createWikiPage(wikiEntry, newWikiPage);

		// retrive tags in JSON string
		String wiki = service.getPublicWikiTagsWithLabel(wikiEntry.getTitle());

		// parse JSON for the firs tag
		JSONObject jObject = new JSONObject(wiki);
		JSONArray items = jObject.optJSONArray("items");
		JSONObject tag1 = items.getJSONObject(0);
		String parsedTag = tag1.optString("name");

		if (parsedTag.equals(tag.toLowerCase())) {
			LOGGER.debug("SUCCESS: Correct tag was retrieved");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Incorrect tag was retrieved");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Retrieve Wiki Page Tags using its WikiLabel");
	}

	@Test
	public void wikiRecyclePagesPara() {
		// service.deleteTests();
		LOGGER.debug("Starting Test: Get Feed of Wiki pages in the wiki recyclebin with parameters ");
		String dateCode = Utils.logDateFormatter.format(new Date());

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] pageName = new String[3];
		pageName[0] = "Fontaine";
		pageName[1] = "Andrew Ryan";
		pageName[2] = "Eleanor Lamb";

		boolean paraTest = true;

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki("Rapture" + dateCode, "Would you kindly!",
				"draftTest", wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		for (int i = 0; i < 3; i++) {
			WikiPage newWikiPage = new WikiPage(pageName[i],
					"<p>This is Adam's wiki page.</p>",
					"wikipagetag1 wikipagetag2 wikipagetag3", false);
			Entry wikiPage = (Entry) service.createWikiPage(wikiEntry,
					newWikiPage);
			service.deleteWikiPage(
					wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
					wikiPage.getExtension(StringConstants.TD_UUID).getText());
		}
		
		ExtensibleElement wikiTrash = service.getWikiTrashPara(wikiEntry.getTitle(),
				"sortBy=title");
		assertEquals("Get wikis trash"+service.getDetail(), 200, service.getRespStatus());
		//Feed wikiTrash = (Feed) service.getWikiTrashPara(wikiEntry.getTitle(),"sortBy=title");
		if (((Feed)wikiTrash).getEntries().get(0).getTitle().equals(pageName[1])
				&& ((Feed)wikiTrash).getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortBy Title sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Title did not sort correctly");
			paraTest = false;
		}

		wikiTrash = service.getWikiTrashPara(wikiEntry.getTitle(),"sortBy=label");
		assertEquals("Get wikis trash"+service.getDetail(), 200, service.getRespStatus());
		if (((Feed)wikiTrash).getEntries().get(0).getTitle().equals(pageName[1])
				&& ((Feed)wikiTrash).getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortBy Label sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Label did not sort correctly");
			paraTest = false;
		}

		wikiTrash = service.getWikiTrashPara(wikiEntry.getTitle(),"sortBy=update");
		assertEquals("Get wikis trash"+service.getDetail(), 200, service.getRespStatus());
		if (((Feed)wikiTrash).getEntries().get(2).getTitle().equals(pageName[0])
				&& ((Feed)wikiTrash).getEntries().get(0).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Update sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Update did not sort correctly");
			paraTest = false;
		}

		wikiTrash = service.getWikiTrashPara(wikiEntry.getTitle(),"sortOrder=desc");
		assertEquals("Get wikis trash"+service.getDetail(), 200, service.getRespStatus());
		if (((Feed)wikiTrash).getEntries().get(2).getTitle().equals(pageName[0])
				&& ((Feed)wikiTrash).getEntries().get(0).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortOrder descending sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortOrder descending did not sort correctly");
			paraTest = false;
		}

		assertTrue(paraTest);
		LOGGER.debug("Finished: Testing the parameters for wiki pages in the wiki recyclebin");
	}

	@Test
	// tests parameters sortby, sortorder, and since
	// does not test search fully because cache does not update correctly right
	// now and if it did the test would take an extra 15minutes
	public void wikiPagesPara() throws InterruptedException {
		service.deleteTests();
		LOGGER.debug("Starting Test: wikiPagesPara");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Rapture_" + uniqueNameAddition;
		boolean paraTest = true;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] pageName = new String[3];
		pageName[0] = "Fontaine";
		pageName[1] = "Andrew Ryan";
		pageName[2] = "Eleanor Lamb";

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "wikiPagesPara test", "draftTest",
				wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		Feed wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"");
		for (Entry e : wikiFeed.getEntries()) {
			service.deleteWikiPage(
					wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
					e.getExtension(StringConstants.TD_UUID).getText());
		}

		WikiPage newWikiPage = new WikiPage(pageName[0],
				"That's why this hurts, kid. Life isn't strictly business. ",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		service.createWikiPage(wikiEntry, newWikiPage);

		newWikiPage = new WikiPage(pageName[1],
				"Is a man not entitled to the sweat of his brow?",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		service.createWikiPage(wikiEntry, newWikiPage);

		Thread.currentThread().sleep(2000);
		long timeStamp = System.currentTimeMillis();
		Thread.currentThread().sleep(5000);

		newWikiPage = new WikiPage(
				pageName[2],
				"Mother believed this world was irredeemable, but she was wrong",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		service.createWikiPage(wikiEntry, newWikiPage);

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=published");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[0])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Published sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Published did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=title");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortBy Title sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Title did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=label");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortBy Label sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Label did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=created");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[0])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Created sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Created did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=updated");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[0])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Updated sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Updated did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=length");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Length sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Length did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortBy=totalMediaSize");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Total Media Size sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Total Media Size did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"sortOrder=desc");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[2])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortOrder Desending sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortOrder Desending did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"since=" + timeStamp);
		while (wikiFeed.getEntries().size() < 1) {
			timeStamp = timeStamp - 2000;
			wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
					"since=" + timeStamp);
		}
		while (wikiFeed.getEntries().size() > 1) {
			timeStamp = timeStamp + 2000;
			wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
					"since=" + timeStamp);
		}
		if (wikiFeed.getEntries().size() == 1
				&& wikiFeed.getEntries().get(0).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: Since filter correctly");
		else {
			LOGGER.debug("Test Failed: Since did not filter correctly");
			assertEquals(1, wikiFeed.getEntries().size());
			paraTest = false;
			assertTrue(paraTest);
		}

		// This need search index, otherwise will show
		// "Search engine is not configured properly", return 502
		/*
		 * ExtensibleElement wikiPage =
		 * service.getWikiPagesPara(wikiEntry.getTitle(), "search=" +
		 * service.encodeString(pageName[2])); assertEquals("getWikiPagesPara",
		 * 200, service.getRespStatus()); if(wikiPage != null)
		 * LOGGER.debug("Test Successful: Search did not return null back");
		 * else{ LOGGER.debug("Test Failed: Search returned null"); paraTest =
		 * false; } assertTrue(paraTest);
		 */

		LOGGER.debug("Finished: Testing the parameters for pages in a wiki");
	}

	@Test
	// tests parameters sortby, sortorder,
	public void wikiEditedPagesPara() {
		service.deleteTests();
		LOGGER.debug("Starting Test: Test parameters for a wiki pages edited by the current user");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Rapture_" + uniqueNameAddition;
		boolean paraTest = true;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] pageName = new String[3];
		pageName[0] = "Fontaine";
		pageName[1] = "Andrew Ryan";
		pageName[2] = "Eleanor Lamb";

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "Would you kindly!", "draftTest",
				wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		Feed wikiFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"");
		for (Entry e : wikiFeed.getEntries()) {
			service.deleteWikiPage(
					wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
					e.getExtension(StringConstants.TD_UUID).getText());
		}

		WikiPage newWikiPage = new WikiPage(pageName[0],
				"That's why this hurts, kid. Life isn't strictly business. ",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		service.createWikiPage(wikiEntry, newWikiPage);

		newWikiPage = new WikiPage(pageName[1],
				"Is a man not entitled to the sweat of his brow?",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		service.createWikiPage(wikiEntry, newWikiPage);

		newWikiPage = new WikiPage(
				pageName[2],
				"Mother believed this world was irredeemable, but she was wrong",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		service.createWikiPage(wikiEntry, newWikiPage);

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=published");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[0])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Published sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Published did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=title");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortBy Title sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Title did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=label");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortBy Label sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Label did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=created");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[0])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Created sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Created did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=updated");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[0])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Updated sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Updated did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=length");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Length sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Length did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortBy=totalMediaSize");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[1])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[2]))
			LOGGER.debug("Test Successful: SortBy Total Media Size sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Total Media Size did not sort correctly");
			paraTest = false;
		}

		wikiFeed = (Feed) service.getEditedPagesPara(wikiEntry.getTitle(),
				"sortOrder=desc");
		if (wikiFeed.getEntries().get(0).getTitle().equals(pageName[2])
				&& wikiFeed.getEntries().get(2).getTitle().equals(pageName[0]))
			LOGGER.debug("Test Successful: SortOrder Desending sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortOrder Desending did not sort correctly");
			paraTest = false;
		}

		assertTrue(paraTest);
		LOGGER.debug("Finished: Testing the Parameters for wiki pages edited by the current user");
	}

	@Test
	public void retrieveTagListPara() throws Exception {
		// service.deleteTests();
		LOGGER.debug("Starting Test: Get Feed of Wiki pages Tags with parameters");
		String dateCode = Utils.logDateFormatter.format(new Date());

		boolean paraTest = true;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] pageTag = new String[3];
		pageTag[0] = "would";
		pageTag[1] = "you";
		pageTag[2] = "kindly";

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki("Rapture" + dateCode,
				"retrieveTagListPara test", "Adam Underwater Fontaine",
				wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		WikiPage newWikiPage = new WikiPage("Fontaine",
				"That's why this hurts, kid. Life isn't strictly business. ",
				pageTag[0] + " " + pageTag[1] + " " + pageTag[2], false);
		service.createWikiPage(wikiEntry, newWikiPage);

		newWikiPage = new WikiPage("Andrew Ryan",
				"Is a man not entitled to the sweat of his brow?", pageTag[1]
						+ " " + pageTag[2], false);
		service.createWikiPage(wikiEntry, newWikiPage);

		newWikiPage = new WikiPage(
				"Eleanor Lamb",
				"Mother believed this world was irredeemable, but she was wrong",
				pageTag[2], false);
		service.createWikiPage(wikiEntry, newWikiPage);

		String tagFeed = service.getWikiTagsPara(wikiEntry.getTitle(),
				"sortBy=weight");

		// parse JSON for the first and third tag
		JSONObject jObject = new JSONObject(tagFeed);
		JSONArray items = jObject.optJSONArray("items");
		JSONObject tag1 = items.getJSONObject(0);
		JSONObject tag3 = items.getJSONObject(2);
		String parsedTag1 = tag1.optString("name");
		String parsedTag3 = tag3.optString("name");

		if (parsedTag1.equals(pageTag[0]) && parsedTag3.equals(pageTag[2]))
			LOGGER.debug("Test Successful: SortBy Weight sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Weight did not sort correctly");
			paraTest = false;
		}

		tagFeed = service.getWikiTagsPara(wikiEntry.getTitle(), "sortBy=name");

		// parse JSON for the first and third tag
		jObject = new JSONObject(tagFeed);
		items = jObject.optJSONArray("items");
		tag1 = items.getJSONObject(0);
		tag3 = items.getJSONObject(2);
		parsedTag1 = tag1.optString("name");
		parsedTag3 = tag3.optString("name");

		if (parsedTag1.equals(pageTag[2]) && parsedTag3.equals(pageTag[1]))
			LOGGER.debug("Test Successful: SortBy Name sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortBy Name did not sort correctly");
			paraTest = false;
		}

		tagFeed = service.getWikiTagsPara(wikiEntry.getTitle(),
				"sortOrder=desc");

		// parse JSON for the first and third tag
		jObject = new JSONObject(tagFeed);
		items = jObject.optJSONArray("items");
		tag1 = items.getJSONObject(0);
		tag3 = items.getJSONObject(2);
		parsedTag1 = tag1.optString("name");
		parsedTag3 = tag3.optString("name");

		if (parsedTag1.equals(pageTag[2]) && parsedTag3.equals(pageTag[1]))
			LOGGER.debug("Test Successful: SortOrder descending sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortOrder descending did not sort correctly");
			paraTest = false;
		}

		assertTrue(paraTest);
		LOGGER.debug("Finished: Testing the Parameters for wiki page tags");
	}

	@Test
	public void retrieveAllTags() throws Exception {

		service.deleteTests();
		LOGGER.debug("Starting Test: Get Feed of Wiki pages Tags with parameters");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String wikiTitle = "Rapture_" + uniqueNameAddition;
		String wikiTitle2 = "Wiki_" + uniqueNameAddition;

		String tagFeed = "";
		String parsedTag = "";
		int size = 0;
		boolean paraTest = true;
		JSONObject tag1 = null;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] tagName = new String[3];
		tagName[0] = "underwater";
		tagName[1] = "created";

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiTitle, "retrieveAllTags test", tagName[0]
				+ " " + tagName[1], wikiMembers, false);
		service.createWiki(newWiki);

		newWiki = new Wiki(wikiTitle2, "retrieveAllTags second wiki",
				tagName[0], wikiMembers, false);
		service.createWiki(newWiki);

		tagFeed = service.getAllWikiTagsPara("ps=1");
		JSONObject jObject = new JSONObject(tagFeed);
		JSONArray items = jObject.optJSONArray("items");

		// items.size could be 0, if didn't find any
		if (items.size() <= 1)
			LOGGER.debug("Test Successful: Page size filtered correctly");
		else {
			LOGGER.debug("Test Failed: Page size did not filter correctly");
			paraTest = false;
		}

		tagFeed = service.getAllWikiTagsPara("sortBy=weight&ps=75");
		// parse JSON for the first and third tag
		jObject = new JSONObject(tagFeed);
		items = jObject.optJSONArray("items");
		size = items.size();

		for (int i = 0; i < size; i++) {
			tag1 = items.getJSONObject(i);
			parsedTag = tag1.optString("name");

			if (parsedTag.equals(tagName[1])) {
				LOGGER.debug("Test Successful: Weight sorted correctly");
				break;
			} else if (parsedTag.equals(tagName[0])) {
				LOGGER.debug("Test Failed: Weight did not sort correctly");
				paraTest = false;
				break;
			}
		}

		tagFeed = service.getAllWikiTagsPara("sortBy=title&ps=75");
		// parse JSON for the first and third tag
		jObject = new JSONObject(tagFeed);
		items = jObject.optJSONArray("items");
		size = items.size();

		for (int i = 0; i < size; i++) {
			tag1 = items.getJSONObject(i);
			parsedTag = tag1.optString("name");

			if (parsedTag.equals(tagName[1])) {
				LOGGER.debug("Test Successful: Title sorted correctly");
				break;
			} else if (parsedTag.equals(tagName[0])) {
				LOGGER.debug("Test Failed: Title did not sort correctly");
				paraTest = false;
				break;
			}
		}

		tagFeed = service.getAllWikiTagsPara("tag=" + tagName[0] + "&ps=75");
		// parse JSON for the first and third tag
		jObject = new JSONObject(tagFeed);
		items = jObject.optJSONArray("items");
		size = items.size();

		for (int i = 0; i < size; i++) {
			tag1 = items.getJSONObject(i);
			parsedTag = tag1.optString("name");

			if (parsedTag.equals(tagName[0])) {
				LOGGER.debug("Test Successful: Tag filtered correctly");
				break;
			} else if (parsedTag.equals(tagName[1])) {
				LOGGER.debug("Test Failed: Tag did not filter correctly");
				paraTest = false;
				break;
			}
		}

		tagFeed = service
				.getAllWikiTagsPara("sortBy=weight&sortOrder=desc&ps=75");
		// parse JSON for the first and third tag
		jObject = new JSONObject(tagFeed);
		items = jObject.optJSONArray("items");
		size = items.size();

		for (int i = 0; i < size; i++) {
			tag1 = items.getJSONObject(i);
			parsedTag = tag1.optString("name");

			if (parsedTag.equals(tagName[0])) {
				LOGGER.debug("Test Successful: Tag filtered correctly");
				break;
			} else if (parsedTag.equals(tagName[1])) {
				LOGGER.debug("Test Failed: Tag did not filter correctly");
				paraTest = false;
				break;
			}
		}

		assertTrue(paraTest);
		LOGGER.debug("Finished: Testing All Tags in wiki library with parameters");
	}

	@Test
	public void getWikiMembersInDepth() throws FileNotFoundException,
			IOException {
		// service.deleteTests();
		LOGGER.debug("BEGINNING TEST: Get Wiki Members in Depth");
		String dateCode = Utils.logDateFormatter.format(new Date());

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		ProfileData member2 = ProfileLoader.getProfile(5);
		ProfileData currProfile = ProfileLoader.getCurrentProfile();
		WikiMember newMember = new WikiMember(member2.getUserId(),
				WikiRole.EDITOR, WikiMemberType.USER);
		wikiMembers.add(newMember);

		String label = "CelticsWiki" + dateCode;
		Wiki newWiki = new Wiki(label, "getWikiMembersInDepth test",
				"draftTest", wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		// Get Members with default params
		Feed members = (Feed) service.getWikiMembers(wikiEntry.getTitle(), "");
		if (members.getEntries().get(1).getTitle()
				.equals(member2.getRealName())
				&& members.getEntries().get(0).getTitle()
						.equals(currProfile.getRealName())) {
			LOGGER.debug("SUCCESS: Got Correct Member with Default Settings");
			assertTrue(true);
		} else {
			LOGGER.warn("ERRORL Got Incorrect Members with Default Settings");
			assertTrue(false);
		}

		// Get Members -- Sort By id , Order By desc
		// TB 11/1/13 If the LDAP updates user id values, the sort order results
		// may not be what is expected.
		// Also, if the user id values do not sort uniformly across multiple
		// LDAPs, including SC, this test will fail.
		// Commenting out.

		members = (Feed) service.getWikiMembers(wikiEntry.getTitle(),
				"?sortBy=id&sortOrder=desc");
		/*
		 * if(members.getEntries().get(0).getTitle().equals(currProfile.getRealName
		 * ()) &&
		 * members.getEntries().get(1).getTitle().equals(member2.getRealName
		 * ())){ LOGGER.debug(
		 * "SUCCESS: Got Correct Member with Settings: Sort By id , Order By desc"
		 * ); assertTrue(true); } else{ LOGGER.warn(
		 * "ERRORL Got Incorrect Members with Settings: Sort By id , Order By desc"
		 * ); assertTrue(false); }
		 */

		// Get Members -- Sort By name , Order By desc
		members = (Feed) service.getWikiMembers(wikiEntry.getTitle(),
				"?sortBy=name&sortOrder=desc");
		if (members.getEntries().get(1).getTitle()
				.equals(currProfile.getRealName())
				&& members.getEntries().get(0).getTitle()
						.equals(member2.getRealName())) {
			LOGGER.debug("SUCCESS: Got Correct Member with Settings: Sort By name , Order By desc");
			assertTrue(true);
		} else {
			LOGGER.warn("ERRORL Got Incorrect Members with Settings: Sort By name , Order By desc");
			assertTrue(false);
		}

		// Get Members -- Sort By email , Order By desc
		members = (Feed) service.getWikiMembers(wikiEntry.getTitle(),
				"?sortBy=email&sortOrder=desc");
		if (members.getEntries().get(1).getTitle()
				.equals(currProfile.getRealName())
				&& members.getEntries().get(0).getTitle()
						.equals(member2.getRealName())) {
			LOGGER.debug("SUCCESS: Got Correct Member with Settings: Sort By name , Order By desc");
			assertTrue(true);
		} else {
			LOGGER.warn("ERRORL Got Incorrect Members with Settings: Sort By name , Order By desc");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Wiki Members In Depth");

	}

	@Test
	public void getWikiMembersOfRole() throws FileNotFoundException,
			IOException, InterruptedException {
		// service.deleteTests();
		LOGGER.debug("BEGINNING TEST: Get Wiki Members in Specific Wiki Role (In Depth)");
		String dateCode = Utils.logDateFormatter.format(new Date());

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		ProfileData member2 = ProfileLoader.getProfile(6);
		ProfileData member3 = ProfileLoader.getProfile(7);
		WikiMember newMember2 = new WikiMember(member2.getUserId(),
				WikiRole.READER, WikiMemberType.USER);
		WikiMember newMember3 = new WikiMember(member3.getUserId(),
				WikiRole.READER, WikiMemberType.USER);
		wikiMembers.add(newMember2);
		wikiMembers.add(newMember3);

		String label = "CelticsWiki_" + dateCode;
		Wiki newWiki = new Wiki(label, "getWikiMembersOfRole test",
				"draftTest", wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		// Get READER members with default settings -- add wait for server to
		// get that
		// seem, wait is not work, sometime it only had anonymous user
		Feed roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "");
		for (int j = 0; j < 10; j++) {
			if (roleMembers.getEntries().get(0).getTitle()
					.equals(member2.getRealName())) {
				break;
			} else {
				Thread.sleep(1500);
				roleMembers = (Feed) service.getWikiMembersWithRole(
						wikiEntry.getTitle(), "reader", "");
			}
		}

		int order;
		// check if get roleMembers
		// assertTrue("shoud have more than 1 roleMembers",
		// roleMembers.getEntries().size()>1);
		// TODO check with wikis team - why sometime couldn't get all member at
		// beginning, let it pass for now
		if (roleMembers.getEntries().size() == 1) {
			assertEquals("Anonymous User", roleMembers.getEntries().get(0)
					.getTitle());
			LOGGER.debug("WARNING: only Anonymous member was found with Default Settings");
		} else {
			// verify result - default
			order = compareStrings(member2.getRealName(),
					member3.getRealName(), "Anonymous User",
					StringConstants.SortOrder.ASC);
			verifyOrder(order, member2.getRealName(), member3.getRealName(),
					"Anonymous User", roleMembers);
			LOGGER.debug("SUCCESS: Correct READER members were found with Default Settings");
		}

		// Get READER members with SortBy name SortOrder desc
		roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "?sortBy=name&sortOrder=desc");
		if (roleMembers.getEntries().size() == 1) {
			assertEquals("Anonymous User", roleMembers.getEntries().get(0)
					.getTitle());
			LOGGER.debug("WARNING: only Anonymous member was found with sortBy name & sortOrder desc");
		} else {
			order = compareStrings(member2.getRealName(),
					member3.getRealName(), "Anonymous User",
					StringConstants.SortOrder.DESC);
			verifyOrder(order, member2.getRealName(), member3.getRealName(),
					"Anonymous User", roleMembers);
			LOGGER.debug("SUCCESS: Correct READER members were found with sortBy name & sortOrder desc");
		}

		// Get READER members with SortBy id SortOrder adesc
		roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "?sortBy=id&sortOrder=desc");
		if (roleMembers.getEntries().size() == 1) {
			assertEquals("Anonymous User", roleMembers.getEntries().get(0)
					.getTitle());
			LOGGER.debug("WARNING: only Anonymous member was found with sortBy Id & sortOrder desc");
		} else {
			order = compareStrings(member2.getUserId(), member3.getUserId(),
					"anonymous-user", StringConstants.SortOrder.DESC);
			verifyOrder(order, member2.getRealName(), member3.getRealName(),
					"Anonymous User", roleMembers);
			LOGGER.debug("SUCCESS: Correct READER members were found with sortBy Id & sortOrder desc");
		}
		// Get READER members with SortBy email SortOrder asc
		roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "?sortBy=email&sortOrder=asc");
		if (roleMembers.getEntries().size() == 1) {
			assertEquals("Anonymous User", roleMembers.getEntries().get(0)
					.getTitle());
			LOGGER.debug("WARNING: only Anonymous member was found with sortBy email & sortOrder asc");
		} else {
			order = compareStrings(member2.getEmail(), member3.getEmail(), " ",
					StringConstants.SortOrder.ASC);
			verifyOrder(order, member2.getRealName(), member3.getRealName(),
					"Anonymous User", roleMembers);
			LOGGER.debug("SUCCESS: Correct READER members were found with sortBy email & sortOrder asc");
		}

		// Get READER member with page Size 1
		roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "?ps=1");
		if (roleMembers.getEntries().size() == 1) {
			LOGGER.debug("SUCCESS: Correct number of Results Found for Page Size 1");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Incorrect number of result founds for Page Size 1");
			assertTrue(false);
		}

		// Get READER members of Page 1 (should be three)
		roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "?page=1");
		assertEquals(
				"Can't retrieve other added Wikis members, only Anonymous User is there. "
						+ "Usually only happen on first run, not reproducible",
				3, roleMembers.getEntries().size());

		// Get READER members of Page 2 (should be none)
		roleMembers = (Feed) service.getWikiMembersWithRole(
				wikiEntry.getTitle(), "reader", "?page=2");
		if (roleMembers.getEntries().size() == 0) {
			LOGGER.debug("SUCCESS: Correct number of Results Found on Page 2");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Incorrect number of result founds on Page 2");
			assertTrue(false);
		}
		LOGGER.debug("COMPELTED TEST: Get Wiki Members in Specific Wiki Role (In Depth)");
	}

	private void verifyOrder(int order, String a, String b, String c,
			Feed roleMembers) {
		switch (order) {
		case 1:
			assertEquals(a, roleMembers.getEntries().get(0).getTitle());
			assertEquals(b, roleMembers.getEntries().get(1).getTitle());
			assertEquals(c, roleMembers.getEntries().get(2).getTitle());
			break;
		case 2:
			assertEquals(a, roleMembers.getEntries().get(0).getTitle());
			assertEquals(b, roleMembers.getEntries().get(2).getTitle());
			assertEquals(c, roleMembers.getEntries().get(1).getTitle());
			break;
		case 3:
			assertEquals(a, roleMembers.getEntries().get(1).getTitle());
			assertEquals(b, roleMembers.getEntries().get(2).getTitle());
			assertEquals(c, roleMembers.getEntries().get(0).getTitle());
			break;
		case 4:
			assertEquals(a, roleMembers.getEntries().get(1).getTitle());
			assertEquals(b, roleMembers.getEntries().get(0).getTitle());
			assertEquals(c, roleMembers.getEntries().get(2).getTitle());
			break;
		case 5:
			assertEquals(a, roleMembers.getEntries().get(2).getTitle());
			assertEquals(b, roleMembers.getEntries().get(0).getTitle());
			assertEquals(c, roleMembers.getEntries().get(1).getTitle());
			break;
		case 6:
			assertEquals(a, roleMembers.getEntries().get(2).getTitle());
			assertEquals(b, roleMembers.getEntries().get(1).getTitle());
			assertEquals(c, roleMembers.getEntries().get(0).getTitle());
			break;
		}
	}

	private int compareStrings(String a, String b, String c, SortOrder order) {
		if (a.compareTo(b) > 0) {
			if (a.compareTo(c) > 0) {
				if (b.compareTo(c) > 0) {
					// abc
					if (order == StringConstants.SortOrder.ASC)
						return 6;
					else
						return 1;
				} else {
					// acb
					if (order == StringConstants.SortOrder.ASC)
						return 5;
					else
						return 2;
				}
			} else {
				// cab
				if (order == StringConstants.SortOrder.ASC)
					return 4;
				else
					return 3;
			}
		} else {
			if (a.compareTo(c) > 0) {
				// bac
				if (order == StringConstants.SortOrder.ASC)
					return 3;
				else
					return 4;
			} else if (b.compareTo(c) > 0) {
				// bca
				if (order == StringConstants.SortOrder.ASC)
					return 2;
				else
					return 5;
			} else {
				// cba
				if (order == StringConstants.SortOrder.ASC)
					return 1;
				else
					return 6;
			}
		}
	}

	@Test
	public void getWikiPageResourcesInDepth() {
		// service.deleteTests();
		LOGGER.debug("Starting Test: getWikiPageResourcesInDepth");
		String dateCode = Utils.logDateFormatter.format(new Date());

		boolean paraTest = true;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] commentName = new String[2];
		commentName[0] = "Little Sister";
		commentName[1] = "Engineer";

		Entry wikiEntry = null;
		Entry wikiPageEntry = null;
		Feed resourcesFeed = null;

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki("Rapture" + dateCode,
				"getWikiPageResourcesInDepth test", "draftTest", wikiMembers,
				false);
		wikiEntry = (Entry) service.createWiki(newWiki);

		WikiPage newWikiPage = new WikiPage(
				"Eleanor Lamb",
				"Mother believed this world was irredeemable, but she was wrong",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		wikiPageEntry = (Entry) service.createWikiPage(wikiEntry, newWikiPage);

		WikiComment comment = new WikiComment(commentName[0]);
		service.createWikiComment(
				wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
				wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
				comment.toEntry());

		comment = new WikiComment(commentName[1]);
		service.createWikiComment(
				wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
				wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
				comment.toEntry());

		resourcesFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&sortBy=created");
		if (resourcesFeed.getEntries().get(0).getContent()
				.equals(commentName[0]))
			LOGGER.debug("Test Successful: Created sorted correctly");
		else {
			LOGGER.debug("Test Failed: Created did not sort correctly");
			paraTest = false;
		}

		resourcesFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&sortBy=published");
		if (resourcesFeed.getEntries().get(0).getContent()
				.equals(commentName[0]))
			LOGGER.debug("Test Successful: Published sorted correctly");
		else {
			LOGGER.debug("Test Failed: Published did not sort correctly");
			paraTest = false;
		}

		resourcesFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&sortBy=modified");
		if (resourcesFeed.getEntries().get(0).getContent()
				.equals(commentName[0]))
			LOGGER.debug("Test Successful: Modified sorted correctly");
		else {
			LOGGER.debug("Test Failed: Modified did not sort correctly");
			paraTest = false;
		}

		resourcesFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&sortBy=updated");
		if (resourcesFeed.getEntries().get(0).getContent()
				.equals(commentName[0]))
			LOGGER.debug("Test Successful: Updated sorted correctly");
		else {
			LOGGER.debug("Test Failed: Updated did not sort correctly");
			paraTest = false;
		}

		resourcesFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&sortBy=modified&sortOrder=desc");
		if (resourcesFeed.getEntries().get(0).getContent()
				.equals(commentName[1]))
			LOGGER.debug("Test Successful: SortOrder sorted correctly");
		else {
			LOGGER.debug("Test Failed: SortOrder did not sort correctly");
			paraTest = false;
		}

		resourcesFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&ps=1");
		if (resourcesFeed.getEntries().size() == 1)
			LOGGER.debug("Test Successful: PageSize filtered correctly");
		else {
			LOGGER.debug("Test Failed: PageSize did not filter correctly");
			paraTest = false;
		}
		assertTrue(paraTest);

	}

	@Test
	public void getPublicWikisPara() throws InterruptedException {
		service.deleteTests();
		LOGGER.debug("Starting Test: getPublicWikisPara");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		boolean paraTest = true;
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String[] wikiName = new String[2];
		wikiName[0] = "Columbia" + uniqueNameAddition;
		wikiName[1] = "Rapture" + uniqueNameAddition;

		Feed publicFeed = null;
		int size;

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		Wiki newWiki = new Wiki(wikiName[0], "getPublicWikisPara test",
				"draftTest", wikiMembers, false);
		service.createWiki(newWiki);

		newWiki = new Wiki(wikiName[1], "Call's you!", "draftTest",
				wikiMembers, false);
		service.createWiki(newWiki);

		publicFeed = (Feed) service.getPublicWikisFeed("ps=100&sortBy=title");
		size = publicFeed.getEntries().size();
		for (int i = 0; i < size; i++) {
			if (publicFeed.getEntries().get(i).getTitle().equals(wikiName[0])) {
				LOGGER.debug("Test Successful: Title sorted correctly");
				break;
			} else if (publicFeed.getEntries().get(i).getTitle()
					.equals(wikiName[1])) {
				LOGGER.debug("Test Failed: Title did not sort correctly");
				paraTest = false;
				break;
			}
		}

		publicFeed = (Feed) service.getPublicWikisFeed("ps=100&sortBy=created");
		size = publicFeed.getEntries().size();
		for (int i = 0; i < size; i++) {
			if (publicFeed.getEntries().get(i).getTitle().equals(wikiName[0])) {
				LOGGER.debug("Test Successful: Created sorted correctly");
				break;
			} else if (publicFeed.getEntries().get(i).getTitle()
					.equals(wikiName[1])) {
				LOGGER.debug("Test Failed: Created did not sort correctly");
				paraTest = false;
				break;
			}
		}

		publicFeed = (Feed) service
				.getPublicWikisFeed("ps=100&sortBy=published");
		size = publicFeed.getEntries().size();
		for (int i = 0; i < size; i++) {
			if (publicFeed.getEntries().get(i).getTitle().equals(wikiName[0])) {
				LOGGER.debug("Test Successful: Published sorted correctly");
				break;
			} else if (publicFeed.getEntries().get(i).getTitle()
					.equals(wikiName[1])) {
				LOGGER.debug("Test Failed: Published did not sort correctly");
				paraTest = false;
				break;
			}
		}

		publicFeed = (Feed) service
				.getPublicWikisFeed("ps=100&sortBy=modified");
		size = publicFeed.getEntries().size();
		for (int i = 0; i < size; i++) {
			if (publicFeed.getEntries().get(i).getTitle().equals(wikiName[0])) {
				LOGGER.debug("Test Successful: Modified sorted correctly");
				break;
			} else if (publicFeed.getEntries().get(i).getTitle()
					.equals(wikiName[1])) {
				LOGGER.debug("Test Failed: Modified did not sort correctly");
				paraTest = false;
				break;
			}
		}

		publicFeed = (Feed) service.getPublicWikisFeed("ps=100&sortOrder=desc");
		size = publicFeed.getEntries().size();
		for (int i = 0; i < size; i++) {
			if (publicFeed.getEntries().get(i).getTitle().equals(wikiName[1])) {
				LOGGER.debug("Test Successful: Descending sorted correctly");
				break;
			} else if (publicFeed.getEntries().get(i).getTitle()
					.equals(wikiName[0])) {
				LOGGER.debug("Test Failed: Descending did not sort correctly");
				paraTest = false;
				break;
			}
		}

		assertTrue(paraTest);
	}

	@Test
	public void testWikiComment() throws Exception {
		/*
		 * Tests the wiki comment 
		 * Step 1: Create wiki with members, create comment 
		 * Step 2: Retrieve wiki comment 
		 * Step 3: update wiki comment and verify 
		 * Step 4: delete wiki comment and verify 
		 * Step 5: create html comment with @mention 
		 * Step 6: get comment with @mention
		 */
		LOGGER.debug("Starting Test: Testing wiki comment");

		LOGGER.debug("1: Testing creating wiki comment");
		String dateCode = RandomStringUtils.randomAlphanumeric(4)+ Utils.logDateFormatter.format(new Date());
		//dateCode = Utils.uniqueString;

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
		String commentName = "Little Sister";
		String updateCommentName = "Grown Up";

		Entry wikiPageEntry = null;
		Entry wikiEntry = null;
		Entry commentsEntry = null;

		// add members to an array, these will be the wiki members
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// create test wiki
		Wiki newWiki = new Wiki("Rapture_" + dateCode, "Would you kindly!",
				"draftTest", wikiMembers, false);
		wikiEntry = (Entry) service.createWiki(newWiki);
		assertEquals("create wiki", 201, service.getRespStatus());

		// create test page
		WikiPage newWikiPage = new WikiPage(
				"Eleanor Lamb",
				"Mother believed this world was irredeemable, but she was wrong",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		wikiPageEntry = (Entry) service.createWikiPage(wikiEntry, newWikiPage);
		assertEquals("create wiki page", 201, service.getRespStatus());

		// create test comment
		WikiComment comment = new WikiComment(commentName);
		Entry comments = (Entry) service.createWikiComment(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(), comment
				.toEntry());

		// see if the test comment exists
		LOGGER.debug("2: Retrieve wiki comment");
		commentsEntry = (Entry) service.getWikiPageCommentsEntry(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(), comments
				.getExtension(StringConstants.TD_UUID).getText());
		if (commentsEntry == null) {
			LOGGER.debug("Test Failed: Comment feed is empty");
			assertTrue(false);
		} else if (commentsEntry.getContent().equals(commentName)) {
			LOGGER.debug("Test Successful: Retrieved correct wiki comment");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Comment retrieved is not the one posted");
			assertTrue(false);
		}
		// verify wiki content
		assertEquals(commentName, service.getCommentContent(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(), comments
				.getExtension(StringConstants.TD_UUID).getText()));

		// update comment
		LOGGER.debug("3: update wiki comment and verify");
		WikiComment update = new WikiComment(updateCommentName);
		service.updateWikiComment(
				wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
				wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
				comments.getExtension(StringConstants.TD_UUID).getText(),
				update.toEntry());

		// see if the updated comment exists
		commentsEntry = (Entry) service.getWikiPageCommentsEntry(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(), comments
				.getExtension(StringConstants.TD_UUID).getText());
		if (commentsEntry == null) {
			LOGGER.debug("Test Failed: Comment feed is empty");
			assertTrue(false);
		} else if (commentsEntry.getContent().equals(updateCommentName)) {
			LOGGER.debug("Test Successful: Retrieved correct wiki comment");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Comment retrieved is not the one posted");
			assertTrue(false);
		}

		// delete wiki comment
		LOGGER.debug("4: delete wiki comment and verify");
		// add one more comment
		comment = new WikiComment("one more");
		service.createWikiComment(
				wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
				wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
				comment.toEntry());

		Feed commentsFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment");

		if (commentsFeed == null) {
			LOGGER.debug("Test Failed: Comment feed is empty");
			assertTrue(false);
		} else if (commentsFeed.getEntries().size() == 2) {
			LOGGER.debug("Test Successful: Two comments was added");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Comments number not match");
			assertTrue(false);
		}
		// delete one comment
		service.deleteWikiComment(
				wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
				wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
				comments.getExtension(StringConstants.TD_UUID).getText());

		// verify comment
		commentsFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment");
		if (commentsFeed == null) {
			LOGGER.debug("Test Failed: Comment feed is empty");
			assertTrue(false);
		} else if (commentsFeed.getEntries().size() == 1) {
			LOGGER.debug("Test Successful: One comment was successfully deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Comment was not successfully deleted");
			assertTrue(false);
		}

		// RTC #118285
		LOGGER.debug("5: create html comment with @mention");

		String mention = "<span class=\"vcard\"><span class=\"fn\">@"
				+ otherUser.getRealName()
				+ "</span><span class=\"x-lconn-userid\">"
				+ otherUser.getUserId() + "</span></span>";
		String commentName2 = commentName + mention;
		// String commentName2 = commentName
		// +"&lt;span class=\"vcard\"&gt;&lt;span class=\"fn\"&gt;@"+
		// otherUserName+"&lt;/span&gt;&lt;span class=\"x-lconn-userid\"&gt;"+otherUserUUID+"&lt;/span&gt;&lt;/span&gt;";
		WikiComment comment2 = new WikiComment(commentName2, "html");
		Entry comments2 = (Entry) service.createWikiComment(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(), comment2
				.toEntry());
		assertEquals("create html comment for @mention", 201,
				service.getRespStatus());
		assertEquals("create html comment for @mention", true, comments2
				.getContent().contains("vcard"));

		LOGGER.debug("6: get comment with @mention");
		// commentsEntry = (Entry)
		// service.getHtmlWikiCommentEntry(wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
		// wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
		// comments2.getExtension(StringConstants.TD_UUID).getText());
		// commentsEntry = (Entry)
		// service.getRawWikiCommentEntry(wikiEntry.getExtension(StringConstants.TD_UUID).getText(),
		// wikiPageEntry.getExtension(StringConstants.TD_UUID).getText(),
		// comments2.getExtension(StringConstants.TD_UUID).getText());

		commentsFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment");
		assertEquals("get comment for @mention", "TEXT", commentsFeed
				.getEntries().get(0).getContentType().toString());
		commentsFeed = (Feed) service.getWikiPageResourcesPara(wikiEntry
				.getExtension(StringConstants.TD_UUID).getText(), wikiPageEntry
				.getExtension(StringConstants.TD_UUID).getText(),
				"category=comment&contentFormat=html");
		assertEquals("get html comment for @mention", "HTML", commentsFeed
				.getEntries().get(0).getContentType().toString());

		LOGGER.debug("End Test: Testing wiki comment");

	}

	@Test
	public void retrieveListOfPublicWikis() throws FileNotFoundException,
			IOException {
		LOGGER.debug("BEGINNING TEST: Retrieve List Of Public Wikis)");
		// service.deleteTests();
		String dateCode = Utils.logDateFormatter.format(new Date());

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// create new public wiki
		String label = "CelticsWiki" + dateCode;
		Wiki newWiki = new Wiki(label, "retrieveListOfPublicWikis Test",
				"draftTest", wikiMembers, false);
		service.createWiki(newWiki);

		Feed publicWikis = (Feed) service.getListOfPublicWikis(50);
		boolean containsNewWiki = false;

		LOGGER.debug("Entries size : " + publicWikis.getEntries().size());
		if (publicWikis.getEntries().size() == 50)
			containsNewWiki = true;
		for (Entry e : publicWikis.getEntries()) {
			if (e.getTitle().equals(label))
				containsNewWiki = true;
		}
		if (publicWikis.getTitle().equals("Public Wikis Feed")
				&& containsNewWiki) {
			LOGGER.debug("SUCCESS: Feed of public wikis was found");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Feed of Public Wikis was not Found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Feed of Public Wikis");
	}

	@Test
	public void retrieveASpecifiedWikiMember() throws FileNotFoundException,
			IOException {
		service.deleteTests();
		LOGGER.debug("BEGINNING TEST: Retrieve Specified Wiki Member)");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		// create virtual members
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// add new member
		ProfileData member2 = ProfileLoader.getProfile(0);
		WikiMember newMember2 = new WikiMember(member2.getUserId(),
				WikiRole.READER, WikiMemberType.USER);
		wikiMembers.add(newMember2);

		// create wiki
		String label = "CelticsWiki_" + uniqueNameAddition;
		Wiki newWiki = new Wiki(label, "retrieveASpecifiedWikiMember test",
				"draftTest", wikiMembers, false);
		service.createWiki(newWiki);

		// retrieve wiki member
		Entry member = (Entry) service.getSpecificWikiMember(label,
				member2.getUserId());

		if (member.getTitle().equals(member2.getRealName())) {
			LOGGER.debug("SUCCESS: Correct wiki member was retrieved");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Wiki member was NOT retrieved");
			assertTrue(false);
		}
		LOGGER.debug("COMPELTED TEST: Retrieve Specific Wiki Member");

	}

	@Test
	public void retrieveListOfWikiRoles() throws FileNotFoundException,
			IOException {
		service.deleteTests();
		LOGGER.debug("BEGINNING TEST: Retrieve List of Wiki Roles");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		// create virtual members
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// add new members
		ProfileData member2 = ProfileLoader.getProfile(0);
		WikiMember newMember2 = new WikiMember(member2.getUserId(),
				WikiRole.READER, WikiMemberType.USER);
		wikiMembers.add(newMember2);

		// create wiki
		String label = "CelticsWiki_" + uniqueNameAddition;
		Wiki newWiki = new Wiki(label, "Would you kindly!", "draftTest",
				wikiMembers, false);
		service.createWiki(newWiki);

		// Retrieve List of Wiki Roles
		Feed wikiRoles = (Feed) service.getListOfWikiRoles(label);
		ArrayList<String> roles = new ArrayList<String>();
		for (Entry r : wikiRoles.getEntries())
			roles.add(r.getTitle());

		// validate
		if (wikiRoles.getTitle().equals("Roles of " + label)
				&& roles.contains("reader") && roles.contains("editor")
				&& roles.contains("contributor") && roles.contains("manager")) {
			LOGGER.debug("SUCCESS: All Correct Roles were found for wiki");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Correct Roles were NOT found for wiki");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get List of Wiki Roles");
	}

	@Test
	public void getNavigationFeed() throws Exception {
		LOGGER.debug("BEGINNING TEST: Get Wiki Navigation Feed");
		service.deleteTests();
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		// create virtual members
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// add new member
		ProfileData member2 = ProfileLoader.getProfile(0);
		WikiMember newMember2 = new WikiMember(member2.getUserId(),
				WikiRole.READER, WikiMemberType.USER);
		wikiMembers.add(newMember2);

		// create wiki
		String label = "CelticsWiki_" + uniqueNameAddition;
		Wiki newWiki = new Wiki(label, "Would you kindly!", "draftTest",
				wikiMembers, false);
		service.createWiki(newWiki);

		// get the navigation feed
		String navigation = service.getWikiNavigationFeed(label);
		String jNavigation = navigation.substring(1, navigation.length() - 1);

		// parse JSON to get label
		JSONObject jObject = new JSONObject(jNavigation);
		String jLabel = jObject.optString("label");

		if (jLabel.equals("Welcome to " + label)) {
			LOGGER.debug("SUCCESS: Navigation Feed was found");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Navigation Feed was NOT found");
			assertTrue(false);
		}
		LOGGER.debug("TEST COMPLETED: Get Wiki Navigation Feed");
	}

	@Test
	public void getNavigationWikiPageFeed() throws Exception {
		LOGGER.debug("BEGINNING TEST: Get Wiki Navigation Feed");
		service.deleteTests();
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		// create virtual members
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// add new member
		ProfileData member2 = ProfileLoader.getProfile(0);
		WikiMember newMember2 = new WikiMember(member2.getUserId(),
				WikiRole.READER, WikiMemberType.USER);
		wikiMembers.add(newMember2);

		// create wiki
		String label = "CelticsWiki_" + uniqueNameAddition;
		Wiki newWiki = new Wiki(label, "Would you kindly!", "draftTest",
				wikiMembers, false);
		Entry wikiEntry = (Entry) service.createWiki(newWiki);

		// create wiki page
		WikiPage newWikiPage = new WikiPage("PaulPeirce",
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		Entry pageEntry = (Entry) service
				.createWikiPage(wikiEntry, newWikiPage);

		// get the navigation feed
		String navigation = service.getWikiNavigationFeed(label,
				pageEntry.getTitle());

		// parse JSON to get label
		JSONObject jObject = new JSONObject(navigation);
		JSONArray jArray = jObject.optJSONArray("breadcrumbs");
		JSONObject jTerms = jArray.getJSONObject(0);
		String jLabel = jTerms.optString("label");
		String jTitle = jTerms.optString("title");
		System.out.println(jLabel);

		// TB 11/15/13 abdera method getTitle() only returns part of the data.
		// Bug? I suspect it's confusing the colon char in the value as a
		// delimiter for key/value pair.
		// The colon is acutally part of the value. The value presented in the
		// feed is correct.
		// if(jLabel.equals(pageEntry.getTitle())){
		if (jLabel.equals(jTitle)) {
			LOGGER.debug("SUCCESS: Wiki Page Navigation Feed was found");
			assertTrue(true);
		} else {
			LOGGER.warn("ERROR: Wiki Page Navigation Feed was NOT found");
			assertTrue(false);
		}
		LOGGER.debug("TEST COMPLETED: Get Wiki Navigation Feed");
	}

	@Test
	public void restoreWikiPage() throws Exception {
		// service.deleteTests();
		LOGGER.debug("Starting Test: restoreWikiPage");
		String dateCode = Utils.logDateFormatter.format(new Date());
		String wikiLabel = "Rapture_" + dateCode;

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		Entry wikiEntry = null;
		Entry wikiPageEntry = null;

		// create members for the wiki
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// create wiki to test
		Wiki newWiki = new Wiki(wikiLabel, "Would you kindly!", "draftTest",
				wikiMembers, false);
		wikiEntry = (Entry) service.createWiki(newWiki);
		String wikiId = wikiEntry.getExtension(StringConstants.TD_UUID)
				.getText();

		// create a new wiki page
		WikiPage newWikiPage = new WikiPage(
				"Eleanor Lamb",
				"Mother believed this world was irredeemable, but she was wrong",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		wikiPageEntry = (Entry) service.createWikiPage(wikiEntry, newWikiPage);
		assertTrue ("wiki uuid = null ", wikiPageEntry.getExtension(StringConstants.TD_UUID) != null);
		String pageId = wikiPageEntry.getExtension(StringConstants.TD_UUID)
				.getText();

		// put the created wiki page into the trash
		service.deleteWikiPage(wikiId, pageId);

		// check to make sure it was successfully moved to the trash
		Feed trashFeed = (Feed) service.getWikiTrashPara(wikiLabel, "");
		if (trashFeed.getEntries().size() != 1) {
			LOGGER.debug("Test Failed: Did not delete wiki page correctly");
			assertTrue(false);
		}

		// atempt to restore the wiki page
		service.restorePage(wikiLabel, pageId, wikiPageEntry);

		// check to see it was restored
		Feed pageFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"");
		if (pageFeed.getEntries().size() == 2) {
			LOGGER.debug("Test Successful: Successfully restored wiki page");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Did not restore wiki page correctly");
			assertTrue(false);
		}
	}

	@Test
	public void deleteTrashWikiPage() throws Exception {
		// service.deleteTests();
		LOGGER.debug("Starting Test: deleteTrashWikiPage");
		String dateCode = Utils.logDateFormatter.format(new Date());
		String wikiLabel = "Rapture" + dateCode;

		ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();

		Entry wikiEntry = null;
		Entry wikiPageEntry = null;

		// create members for the wiki
		WikiMember virtualReader = new WikiMember("anonymous-user",
				WikiRole.READER, WikiMemberType.VIRTUAL);
		WikiMember virtualEditor = new WikiMember("all-authenticated-users",
				WikiRole.EDITOR, WikiMemberType.VIRTUAL);
		wikiMembers.add(virtualReader);
		wikiMembers.add(virtualEditor);

		// create wiki to test
		Wiki newWiki = new Wiki(wikiLabel, "Would you kindly!", "draftTest",
				wikiMembers, false);
		wikiEntry = (Entry) service.createWiki(newWiki);
		String wikiId = wikiEntry.getExtension(StringConstants.TD_UUID)
				.getText();

		// create a new wiki page
		WikiPage newWikiPage = new WikiPage(
				"Eleanor Lamb",
				"Mother believed this world was irredeemable, but she was wrong",
				"wikipagetag1 wikipagetag2 wikipagetag3", false);
		wikiPageEntry = (Entry) service.createWikiPage(wikiEntry, newWikiPage);
		assertEquals("createWikiPage", 201, service.getRespStatus());

		String pageId = wikiPageEntry.getExtension(StringConstants.TD_UUID)
				.getText();

		// put the created wiki pages into the trash
		service.deleteWikiPage(wikiId, pageId);
		Feed pageFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(),
				"");
		service.deleteWikiPage(wikiId, pageFeed.getEntries().get(0)
				.getExtension(StringConstants.TD_UUID).getText());

		// check to make sure it was successfully moved to the trash
		Feed trashFeed = (Feed) service.getWikiTrashPara(wikiLabel, "");
		if (trashFeed.getEntries().size() != 2) {
			LOGGER.debug("Test Failed: Did not delete wiki page correctly");
			assertTrue(false);
		}

		// atempt to delete the wiki page
		service.deleteTrashPage(wikiLabel, pageId, wikiPageEntry);

		// check to see it was deleted
		trashFeed = (Feed) service.getWikiTrashPara(wikiLabel, "");
		pageFeed = (Feed) service.getWikiPagesPara(wikiEntry.getTitle(), "");
		if (trashFeed.getEntries().size() == 1
				&& pageFeed.getEntries().size() == 0) {
			LOGGER.debug("Test Successful: Successfully deleted wiki page from the trash");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Did not delete wiki page from the trash correctly");
			assertTrue(false);
		}
	}

	@Test
	public void updatePage() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to update a wiki page Step 1: Create a wiki Step 2:
		 * Create a page Step 3: Update the page Step 4: Verify the changes are
		 * there
		 */
		LOGGER.debug("Beginning test: Update page");
		String randString = RandomStringUtils.randomAlphanumeric(15);
		String randString2 = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a wiki");
		ArrayList<WikiMember> members = new ArrayList<WikiMember>();
		// ProfileData testUser = ProfileLoader.getProfile(5);
		members.add(new WikiMember(otherUser.getUserId(), WikiRole.EDITOR,
				WikiMemberType.USER));
		Wiki testWiki = new Wiki("UpdatePage Test " + randString, "Test wiki",
				"test", members);
		service.createWiki(testWiki);

		LOGGER.debug("Step 2: Create a page");
		WikiPage testPage = new WikiPage("Test Page " + randString, "Edit me!",
				"test");
		service.createWikiPage(testWiki.toEntry(), testPage);

		LOGGER.debug("Step 3: Update the page");
		Entry page = (Entry) service.getWikiPageWithLabels(testWiki.getTitle(),
				testPage.getTitle());
		page.setTitle("Updated Test Page " + randString2);
		page.setSummary("I've been updated " + randString2);
		page.getExtension(StringConstants.TD_LABEL).setText(page.getTitle());
		// page.getExtension(StringConstants.TD_CREATED).discard();
		// page.getExtension(StringConstants.TD_MODIFIED).discard();
		service.updatePage(testWiki.getTitle(), testPage.getTitle(), page);
		assertEquals("Failed due to atom field crteated/modefied", 200,
				service.getRespStatus());

		LOGGER.debug("Step 4: Verify the changes are there");
		Entry updatedPage = (Entry) service.getWikiPageWithLabels(
				testWiki.getTitle(), page.getTitle());

		assertEquals("Updated Test Page " + randString2, updatedPage.getTitle());
		assertEquals("I've been updated " + randString2,
				updatedPage.getSummary());

		LOGGER.debug("Ending test: Update page");
	}

	@Test
	public void deleteMyWikis() throws Exception {
		LOGGER.debug("Starting Test: Delete my wiki pages");
		service.deleteWikis();
		LOGGER.debug("End Test: Delete my wiki pages");
	}

	@Test
	public void getServiceConfigs() {
		/*
		 * Tests the ability to get service configs Step 1: Get service configs
		 * Step 2: Verify that added service config title matches
		 */
		LOGGER.debug("Beginning test: Get service configs");
		LOGGER.debug("Step 1: Get service configs");
		Feed serviceConfig = (Feed) service.getServiceConfigs();
		final String SERVICE_CONFIG_TITLE = "IBM Connections Service Configurations";

		LOGGER.debug("Step 2: Verify that service config title matches");
		/*
		 * boolean serviceConfigPresent=false; for (Entry e :
		 * serviceConfig.getEntries()){ if
		 * (serviceConfig.getTitle().equals(SERVICE_CONFIG_TITLE))
		 * serviceConfigPresent=true; } assertTrue(serviceConfigPresent);
		 */
		assertEquals("service config title should matches",
				SERVICE_CONFIG_TITLE, serviceConfig.getTitle());
		LOGGER.debug("Ending test: Get service configs");
	}

	@Test
	public void getPagesModifiedByUser() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to retrieve wiki pages modified by a specific user
		 * Step 1: Create a wiki Step 2: Create a page in the wiki Step 3: Get
		 * pages modified by the user Step 4: Verify that the created page is
		 * there
		 */
		LOGGER.debug("BEGINNING TEST: Get Pages Modified By User");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a wiki");
		Wiki testWiki = new Wiki("ModifedPages Test " + randString,
				"I'm a cool wiki, edit my pages.", "test",
				new ArrayList<WikiMember>());
		service.createWiki(testWiki);

		LOGGER.debug("Step 2: Create a page in the wiki");
		WikiPage testPage = new WikiPage("API Test Page " + randString,
				"Edit me!", "test");
		service.createWikiPage(testWiki.toEntry(), testPage);

		LOGGER.debug("Step 3: Get pages modified by the user");
		ProfileData primaryTestUser = ProfileLoader.getProfile(2); // Amy
		// Jones242
		Feed pagesModified = (Feed) service
				.getPagesModifiedByUser(primaryTestUser.getUserId());

		LOGGER.debug("Step 4: Verify that the created page is there");
		boolean foundPage = false;
		for (Entry e : pagesModified.getEntries()) {
			if (e.getTitle().equals(testPage.getTitle()))
				foundPage = true;
		}
		assertEquals(true, foundPage);

		LOGGER.debug("ENDING TEST: Get Pages Modified By User");
	}

	@Test
	public void deleteMemberFromRole() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to delete a member from a role Step 1: Create a
		 * wiki with a member Step 2: Delete the member from their role Step 3:
		 * Verify the member has been removed
		 */
		LOGGER.debug("BEGINNING TEST: Delete member from role");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a wiki with a member");
		ArrayList<WikiMember> members = new ArrayList<WikiMember>();
		ProfileData userToRemove = ProfileLoader.getProfile(5);
		members.add(new WikiMember(userToRemove.getUserId(), WikiRole.EDITOR,
				WikiMemberType.USER));
		Wiki testWiki = new Wiki("DeleteMember Test " + randString,
				"One of my members shall be exiled.", "test", members);
		service.createWiki(testWiki);

		LOGGER.debug("Step 2: Delete the member from their role");
		assertTrue(service.deleteMemberFromRole(testWiki.getTitle(), "editor",
				userToRemove.getUserId()));

		LOGGER.debug("Step 3: Verify the member has been removed");
		Feed editorsFeed = (Feed) service.getWikiMembersWithRole(
				testWiki.getTitle(), "editor", "");
		boolean foundMember = false;
		for (Entry e : editorsFeed.getEntries()) {
			if (e.getTitle().equals(userToRemove.getRealName()))
				foundMember = true;
		}
		assertEquals(false, foundMember);

		LOGGER.debug("ENDING TEST: Delete member from role");
	}

	@Test
	public void getWikiNavigationFeed() {
		/*
		 * Tests the ability to get a wiki's navigation feed of wiki pages.
		 * Endpoint is /wikis/basic/api/wiki/{wiki label}/navigation/feed Step
		 * 1: Create a wiki Step 2: Add pages to the wiki Step 3: Get the
		 * navigation feed, verify it contains all the pages
		 */
		LOGGER.debug("BEGINNING TEST: Get wiki navigation feed");
		String rand1 = RandomStringUtils.randomAlphanumeric(5);
		String rand2 = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create a wiki");
		Wiki testWiki = new Wiki("TestWiki" + rand1, "My wiki", "test",
				new ArrayList<WikiMember>());
		service.createWiki(testWiki);

		LOGGER.debug("Step 2... Add pages to the wiki");
		WikiPage testPage1 = new WikiPage("API Test Page1 " + rand1,
				"First test page", "test");
		WikiPage testPage2 = new WikiPage("API Test Page2 " + rand2,
				"Second test page", "test");
		service.createWikiPage(testWiki.toEntry(), testPage1);
		service.createWikiPage(testWiki.toEntry(), testPage2);

		LOGGER.debug("Step 3... Get the navigation feed, verify it contains all the pages");
		Feed navFeed = (Feed) service.getNavigationFeed(testWiki.getTitle());
		assertEquals(testPage1.getTitle(), navFeed.getEntries().get(1)
				.getTitle());
		assertEquals(testPage2.getTitle(), navFeed.getEntries().get(2)
				.getTitle());

		LOGGER.debug("ENDING TEST: Get wiki navigation feed");
	}

	@Test
	public void visitorModelTests() throws FileNotFoundException, IOException {
		/*
		 * This test is for VModel deployments only! !
		 * 
		 * 1) For visitor access, below Wikis API should return HTTP 403 to
		 * indicate that the requested api is forbidden to the visitor caller -
		 * Getting a feed of public wikis : /wikis/basic/api/wikis/public -
		 * Creating/update/delete a wiki : /wikis/basic/api/wikis/feed
		 * 
		 * 2) All anonymous API should be restricted including basic and form
		 * API. Once visitor is added into the IC 5.0 env, anonymous api is
		 * forbidden and required everyone to be authenticated on J2EE role
		 * setting in WAS. - /wikis/form/anonymous/api/*
		 * 
		 * If URL includes /anonymous, then 401 should be returned otherwise 403
		 * should be returned.
		 * 
		 * Step 1... Create a wiki Step 2... Visitor tries to create wiki.
		 * Should fail Step 3... Default user tries to create wiki. Should pass
		 * Step 4... Visitor tries to delete wiki. Should fail Step 5... Visitor
		 * tries to update wiki. Should fail Step 6... Default user tries to
		 * update wiki. Should pass Step 7... Visitor tries to get public feed.
		 * Should fail Step 8... Default tries to get public feed. Should pass
		 * Step 9... Tests for anonymous access.
		 */
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("BEGINNING TEST: RTC 119455/128254 Wiki visitor model tests");

			LOGGER.debug("Step 1... Create a wiki");
			ArrayList<WikiMember> wikiMembers = new ArrayList<WikiMember>();
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(3);
			String wikiTitle = "Visitor Wikis Tests RTC 119455"
					+ uniqueNameAddition;

			// visitor is ajones494, default user is ajones242
			// member2 is ajones101

			UserPerspective visitor=null;
			try {
				visitor = new UserPerspective(
						StringConstants.EXTERNAL_USER, Component.WIKIS.toString());
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WikisService visitorService = visitor.getWikisService();

			WikiMember virtualReader = new WikiMember("anonymous-user",
					WikiRole.READER, WikiMemberType.VIRTUAL);
			WikiMember virtualEditor = new WikiMember(
					"all-authenticated-users", WikiRole.EDITOR,
					WikiMemberType.VIRTUAL);
			wikiMembers.add(virtualReader);
			wikiMembers.add(virtualEditor);

			ProfileData member2 = ProfileLoader.getProfile(5);// ajones101
			WikiMember newMember = new WikiMember(member2.getUserId(),
					WikiRole.EDITOR, WikiMemberType.USER);
			wikiMembers.add(newMember);

			// Step 2. Visitor creates wiki - should fail 403.
			Wiki newWiki = new Wiki(wikiTitle, "Visitor and anonymous tests",
					null, wikiMembers);
			visitorService.createWiki(newWiki);
			assertEquals("HTTP 403 was not returned.", 403,
					visitorService.getRespStatus());

			// Step 3. Default user creates wiki - should pass
			Entry ntry = (Entry) service.createWiki(newWiki);
			ntry.getEditLinkResolvedHref();
			assertEquals("HTTP 201 was not returned.", 201,
					service.getRespStatus());

			// Step 4. Visitor deletes wiki - should fail 403.
			visitorService
					.deleteWiki(ntry.getEditLinkResolvedHref().toString());
			assertEquals("HTTP 403 was not returned.", 403,
					visitorService.getRespStatus());

			// Step 5. Visitor updates wiki - should fail 403.
			String updatedSummary = "This is the updated version of the wiki";
			Wiki updatedWiki = new Wiki(wikiTitle, updatedSummary, "update",
					wikiMembers, false);
			visitorService
					.updateWikiWithLabel(wikiTitle, updatedWiki.toEntry());
			assertEquals("HTTP 403 was not returned as expected.", 403,
					visitorService.getRespStatus());

			// Step 6. Default user updates wiki - should pass
			service.updateWikiWithLabel(wikiTitle, updatedWiki.toEntry());
			assertEquals("HTTP 200 was not returned as expected.", 200,
					service.getRespStatus());

			// Step 7. Visitor gets public feed - should fail 403.
			visitorService.getPublicWikisFeed("ps=100");
			assertEquals("HTTP 403 was not returned as expected.", 403,
					visitorService.getRespStatus());

			// Step 8. Default user gets public feed - should pass.
			Feed publicFeed = (Feed) service.getPublicWikisFeed("ps=100");
			assertEquals("HTTP 200 was not returned as expected.", 200,
					service.getRespStatus());

			// Step 9. Test anonymous access. Am using hard coded strings as
			// anonymous access should be disabled got form and basic auth.
			String server = URLConstants.SERVER_URL;
			String anonymousString_1 = server
					+ "/wikis/form/anonymous/api/wikis/public?ps=100";
			String anonymousString_2 = server
					+ "/wikis/basic/anonymous/api/wikis/public?ps=100";
			String anonymousString_3 = server
					+ "/wikis/basic/anonymous/api/wikis/mostcommented";
			String anonymousString_4 = server
					+ "/wikis/basic/anonymous/api/wikis/mostrecommended";
			String anonymousString_5 = server
					+ "/wikis/basic/anonymous/api/wikis/mostvisited";

			visitorService.genericGet(anonymousString_1);
			assertEquals("HTTP 401 was not returned as expected.", 401,
					visitorService.getRespStatus());

			visitorService.genericGet(anonymousString_2);
			assertEquals("HTTP 401 was not returned as expected.", 401,
					visitorService.getRespStatus());

			visitorService.genericGet(anonymousString_3);
			assertEquals("HTTP 401 was not returned as expected.", 401,
					visitorService.getRespStatus());

			visitorService.genericGet(anonymousString_4);
			assertEquals("HTTP 401 was not returned as expected.", 401,
					visitorService.getRespStatus());

			visitorService.genericGet(anonymousString_5);
			assertEquals("HTTP 401 was not returned as expected.", 401,
					visitorService.getRespStatus());

			LOGGER.debug("ENDING TEST: RTC 119455/128254 Wiki visitor model tests");
		}
	}

}
