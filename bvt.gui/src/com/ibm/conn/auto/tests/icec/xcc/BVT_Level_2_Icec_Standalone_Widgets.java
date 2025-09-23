package com.ibm.conn.auto.tests.icec.xcc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Level_2_Icec_Standalone_Widgets extends SetUpMethods2 {
	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Icec_Standalone_Widgets.class);
	private IcecUI ui;
	private CalendarUI calUI;
	private CommunitiesUI commUI;
	private TestConfigCustom cfg;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	private boolean isIcecLight;
	private User adminUser;
	private APICommunitiesHandler apiOwner;
	private ArrayList<Community> communitiesToDelete;
	private ArrayList<FileEntry> filesToDelete;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		adminUser = cfg.getUserAllocator().getAdminUser();
		communitiesToDelete = new ArrayList<Community>();
		isIcecLight = ui.isIcecLight();
		filesToDelete = new ArrayList<FileEntry>();
		URLConstants.setServerURL(serverURL);

	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

	    // initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		commUI  = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Community Description widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC Component.
	*<li><B>Step:</B> Remove the default Community Description widget.
	*<li><B>Step:</B> Create Community Description widget.
	*<li><B>Step:</B> Update the title of the Community Description widget.
	*<li><B>Step:</B> Select the community previously created as the source.
	*<li><B>Verify:</B> Check the title of the widget.
	*<li><B>Verify:</B> The widget contains the description of the community.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Verify:</B> Check edit view.
	*<li><B>Step:</B> Remove/Delete widget.
	*</ul>
	*/
	@Test(groups = {"regression"}, enabled=false) //This test case has been disabled because of the defect - https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10559.
	public void testCommunityDescription() throws UnsupportedEncodingException {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String widgetType = "Community Description";
		String communityName = testName + Helper.genDateBasedRand();
		String widgetTitle = "Description for " + communityName;
		String description = "Test Community for " + communityName;
		
		logger.strongStep("Login, create a community and navigate to the community");
		log.info("INFO: Login, create a community and navigate to the community");
		Community comAPI = ui.createLoginAndNavigateToCommunity(adminUser, serverURL, communityName, description);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default Community Description widget");
		log.info("INFO: Remove the default Community Description widget");
		removeAndDeleteWidgets(widgetType);
		
		logger.strongStep("Create Community Description Widget and update the widget title");
		log.info("INFO: Create Community Description Widget and update the widget title");
		createWidget("xccCommunityDescription");
		String title=driver.getSingleElement(IcecUI.getWidget(widgetType)).getAttribute("data-wname");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("CommunityDescription", widgetTitle, communityName);

		logger.strongStep("Verify the title of Community Description widget and that it contains the description of the community: " + communityName);
		log.info("INFO: Verify the title of Community Description widget and that it contains the description of the community: " + communityName);
		Assert.assertTrue(ui.getElementText(IcecUI.getCommunityDescriptionText(widgetTitle)).equals(description),
				"Description doesn't match expected: " + description);
		Assert.assertTrue(ui.getWidgetTitle(widgetTitle).equals(widgetTitle),
				"Community Description widget is not created, expected: " + widgetTitle);

		logger.strongStep("Check edit view");
		log.info("INFO: Check edit view");
		ui.editWidget(widgetTitle);
		ui.clickLinkWithJavascript(IcecUI.advancedSettingDropdown);
		Assert.assertTrue(ui.getElementText(IcecUI.editViewIdType).equals(title + " / Community Description"),
				"ID / Type doesn't match expected: " + title + " / Community Description");
		ui.saveWidget(widgetTitle);
		
		logger.strongStep("Delete created Community Description widget");
		log.info("INFO: Delete created Community Description widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Community Members widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and add a few members to it.
	*<li><B>Step:</B> Navigate to the community and trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Community Members widget.
	*<li><B>Step:</B> Create the Community Members Widget and update the widget title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check tiles for both users are displayed.
	*<li><B>Step:</B> Click tile toggle to flip tile.
	*<li><B>Verify:</B> Check owner/member is displayed.
	*<li><B>Step:</B> Click tile toggle to flip tile back.
	*<li><B>Step:</B> Click user1 tile.
	*<li><B>Step:</B> Switch to newly opened tab.
	*<li><B>Verify:</B> Check My Profile page is opened in new tab.
	*<li><B>Step:</B> Switch back to the ICEC tab.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testCommunityMembers() throws Exception {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser1 = adminUser;
		User testUser2 = cfg.getUserAllocator().getUser();
		String user1DisplayName = testUser1.getDisplayName();
		String user2DisplayName = testUser2.getDisplayName();

		String testName = ui.startTest();
		String widgetType = "Community Members";
		String communityName = testName + Helper.genDateBasedRand();
		String widgetTitle = "Members for " + communityName;
		String description = "Test Community for " + communityName;
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
				   .addMember(new Member(CommunityRole.MEMBERS, testUser2))
				   .description(description).build();

		logger.strongStep("Login, create a community and navigate to the community");
		log.info("INFO: Login, create a community and navigate to the community");
		Community comAPI = ui.createLoginAndNavigateToCommunity(adminUser, serverURL, communityName, description, community, null);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default Community Members widget");
		log.info("INFO: Remove the default Community Members widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create the Community Members Widget and update the widget title");
		log.info("INFO: Create the Community Members Widget and update the widget title");
		createWidget("xccCommunityMembers");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("CommunityMembers", widgetTitle, communityName);

		logger.strongStep("Check tiles for both users are displayed");
		log.info("INFO: Check tiles for both users are displayed");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMemberTileNameLinkXpath(widgetTitle, user1DisplayName)),
				user1DisplayName + " member not found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMemberTileNameLinkXpath(widgetTitle, user2DisplayName)),
				user2DisplayName + " member not found");

		logger.strongStep("Flip member user1 tile");
		log.info("INFO: Flip member user1 tile");
		ui.scrollIntoViewElement(IcecUI.getWidget(widgetTitle));
		driver.getSingleElement(IcecUI.getMemberTileLink(widgetTitle, user1DisplayName)).hover();
		ui.clickLink(IcecUI.getMemberTileToggle(widgetTitle));

		logger.strongStep("Check flipped user1 tile contains display name and member type");
		log.info("INFO: Check flipped user1 tile contains display name and member type");
		Assert.assertTrue(ui.getElementText(IcecUI.getMemberTitle(widgetTitle, user1DisplayName)).equals(user1DisplayName),
				"Title doesn't contain " + user1DisplayName + " as expected");
		Assert.assertTrue(ui.getElementText(IcecUI.getMemberTypeLabel(widgetTitle, user1DisplayName)).equalsIgnoreCase("owner"),
				"Tile doesn't contain 'owner' as expected");

		logger.strongStep("Flip member user1 tile back");
		log.info("INFO: Flip member user1 tile back");
		driver.getSingleElement(IcecUI.getMemberTypeLabel(widgetTitle, user1DisplayName)).hover();
		ui.clickLink(IcecUI.getMemberTileToggle(widgetTitle));

		logger.strongStep("Flip member user2 tile");
		log.info("INFO: Flip member user2 tile");
		driver.getSingleElement(IcecUI.getMemberTileLink(widgetTitle, user2DisplayName)).hover();
		ui.clickLink(IcecUI.getMemberTileToggle(widgetTitle));

		logger.strongStep("Check flipped user2 tile contains display name and member type");
		log.info("INFO: Check flipped user2 tile contains display name and member type");
		Assert.assertTrue(ui.getElementText(IcecUI.getMemberTitle(widgetTitle, user2DisplayName)).equals(user2DisplayName),
				"Title doesn't contain " + user2DisplayName + " as expected");
		Assert.assertTrue(ui.getElementText(IcecUI.getMemberTypeLabel(widgetTitle, user2DisplayName)).equalsIgnoreCase("member"),
				"Tile doesn't contain 'member' as expected");

		logger.strongStep("Flip member user2 tile back");
		log.info("INFO: Flip member user2 tile back");
		driver.getSingleElement(IcecUI.getMemberTypeLabel(widgetTitle, user1DisplayName)).hover();
		ui.clickLink(IcecUI.getMemberTileToggle(widgetTitle));

		logger.strongStep("Click user1 tile");
		log.info("INFO: Click user1 tile");
		ui.clickLink(IcecUI.getMemberTileLink(widgetTitle, user1DisplayName));

		logger.strongStep("Switch to new tab and ensure My Profile page opens");
		log.info("INFO: Switch to new tab and ensure My Profile page opens");
		ui.switchToNextTab();
		String userUrl = ui.replaceHttpWithHttps(Data.getData().networkUserUrl.replaceAll("SERVER", cfg.getServerURL()));
		Assert.assertTrue(driver.getCurrentUrl().contains(userUrl),
		    	"My Profile opened with incorrect URL: " + userUrl);
		
		logger.strongStep("Switch back to the ICEC tab");
		log.info("INFO: Switch back to the ICEC tab");
		driver.switchToFirstMatchingWindowByPageTitle("xcc");

		logger.strongStep("Delete created Community Members widget");
		log.info("INFO: Delete created Community Members widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Featured Communities widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Featured Communities widget.
	*<li><B>Step:</B> Create Featured Communities Widget and update the widget title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Step:</B> Edit Featured Communities widget.
	*<li><B>Verify:</B> Verify option to add additional channel sources.
	*<li><B>Verify:</B> Verify the title, description and image of the community: .
	*<li><B>Step:</B> Click community title.
	*<li><B>Verify:</B> Check community appears as expected in a new tab.
	*<li><B>Step:</B> Switch back to the ICEC tab.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testFeaturedCommunities() throws Exception {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Featured Communities";
		String communityName = testName + Helper.genDateBasedRand();
		String widgetTitle = widgetType + communityName;
		String description = "Test Community for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();
		
		logger.strongStep("Login, create a community and navigate to the community");
		log.info("INFO: Login, create a community and navigate to the community");
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default Featured Communities widget");
		log.info("INFO: Remove the default Featured Communities widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create Featured Communities Widget and update the widget title");
		log.info("INFO: Create Featured Communities Widget and update the widget title");
		createWidget("xccCommunityOverview");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("CommunityOverview", widgetTitle, communityName);
		
		logger.strongStep("Edit Featured Communities widget");
		log.info("INFO: Edit Featured Communities widget");
		ui.editWidget(widgetTitle);
		
		logger.strongStep("Verify option to add additional channel sources");
		log.info("INFO: Verify option to add additional channel sources");
		ui.clickLinkWithJavascript(IcecUI.contentSourcesDropdown);
		ui.scrollIntoViewElement("xpath=//button[text()='Add Channel']");
		ui.clickLinkWithJavascript("xpath=//button[text()='Add Channel']");
		Assert.assertTrue(ui.fluentWaitPresent("xpath=//span[text()='Channel 2']"),
				"Channel 2 isn't displayed");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.ec-channel:nth-child(2) label.input-group input"),
				"Channel 2 input source not displayed");
		ui.cancelWidget(widgetTitle);
		ui.clickLink("css=button.bx--btn--secondary:contains(Discard Changes)");
		driver.executeScript("scroll(0, -50);");
		
		logger.strongStep("Verify the title, description and image of the community: " + communityName);
		log.info("INFO: Verify the title, description and image of the community: " + communityName);
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccCommunityOverview h3.xccHeadline:contains(" + community.getName() + ")"),
				"Title isn't the expected : "  + community.getName());
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccCommunityOverview div.newsOverviewTeaser:contains(" + community.getDescription() + ")"),
				"Description isn't the expected : "  + community.getDescription());
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccCommunityOverview img"),
				"Community image not displayed");
		
		logger.strongStep("Click community title");
		log.info("INFO: Click community title");
		ui.clickLink("css=div.xccCommunityOverview h3.xccHeadline:contains(" + community.getName() + ")");

		logger.strongStep("Switch to new tab and ensure Community page opens");
		log.info("INFO: Switch to new tab and ensure Community page opens");
		ui.switchToNextTab();
		String communityUrl = ui.replaceHttpWithHttps(serverURL + "/communities/service/html/communityoverview?" + community.getCommunityUUID());
		Assert.assertTrue(driver.getCurrentUrl().contains(communityUrl),
		    	"Community opened with incorrect URL: " + communityUrl);
		
		logger.strongStep("Switch back to the ICEC tab");
		log.info("INFO: Switch back to the ICEC tab");
		driver.switchToFirstMatchingWindowByPageTitle("xcc");

		logger.strongStep("Delete created Featured Communities widget");
		log.info("INFO: Delete created Featured Communities widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test the HTML widget.
	*<li><B>Step:</B> Load the ICEC page and login as the admin user.
	*<li><B>Step:</B> Remove the default HTML widget.
	*<li><B>Step:</B> Create the HTML widget.
	*<li><B>Step:</B> Edit the widget and update its title.
	*<li><B>Step:</B> Edit the HTML widget.
	*<li><B>Step:</B> Add HTML to code block and save.
	*<li><B>Verify:</B> Check rendered HTML in the widget.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testHTML() throws Exception {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "HTML";
		String widgetTitle = "HTML for " + testName + Helper.genDateBasedRand();

		logger.strongStep("Load the ICEC component and login as " + testUser.getDisplayName());
		log.info("INFO: Load the ICEC component and login as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentIcec);
		ui.login(testUser);

		logger.strongStep("Remove the default HTML widget");
		log.info("INFO: Remove the default HTML widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create the HTML widget and update the widget title");
		log.info("INFO: Create the HTML widget and update the widget title");
		createWidget("xccClipping");
		updateWidgetTitle(widgetType, widgetTitle);
		
		logger.strongStep("Edit HTML widget and add HTML to code block");
		log.info("INFO: Edit HTML widget and add HTML to code block");
		ui.editWidget(widgetTitle);
		WebElement element = (WebElement) driver.getFirstElement("css=div.CodeMirror").getBackingObject();
		String html = IcecUI.getSampleHTML(testUser);
		driver.executeScript("arguments[0].CodeMirror.setValue('" + html + "');", element);
		ui.saveWidget(widgetTitle);

		logger.strongStep("Check rendered HTML in widget");
		log.info("INFO: Check rendered HTML in widget");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping h1:contains(" + IcecUI.HTML_HEADER + ")"),
				"HTML header expected: " + IcecUI.HTML_HEADER + " Actual: "
						+ ui.getElementText("css=div.xccClipping h1"));
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping p:contains(User: " + testUser.getDisplayName() + ")"),
				"HTML paragraph expected: User: " + testUser.getDisplayName() + " Actual: "
						+ ui.getElementText("css=div.xccClipping p"));
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping label[for='fName']:contains(First name:)"),
				"First Name label expected: 'First Name:' Actual: "
						+ ui.getElementText("css=div.xccClipping label[for='fName']"));
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping #fName"), "First Name input not found");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping label[for='lName']:contains(Last name:)"),
				"Last Name label expected: 'Last Name:' Actual: "
						+ ui.getElementText("css=div.xccClipping label[for='lName']"));
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping #lName"), "Last Name input not found");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping button"), "HTML button not found");

		logger.strongStep("Delete created HTML widget");
		log.info("INFO: Delete created HTML widget");
		removeAndDeleteWidget(widgetTitle);
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B> Test Important Links widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and add Important Bookmarks to it.
	*<li><B>Step:</B> Navigate to the community and trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Important Links widget.
	**<li><B>Step:</B> Create Important Links widget and update the widget title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check all bookmark links are displayed in widget.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Change number of items per page.
	*<li><B>Step:</B> Save.
	*<li><B>Verify:</B> Page through links and check content.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testImportantLinks() throws Exception{
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Bookmark List - Important";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Important Bookmarks for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		logger.strongStep("Login, create a community and navigate to the community");
		log.info("INFO: Login, create a community and navigate to the community");
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		BaseDogear[] bookmarks = ui.createBookmarks(testUser, serverURL, community, 6);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default 'Important Links' widget");
		log.info("INFO: Remove the default 'Important Links' widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create Important Links widget and update the widget title");
		log.info("INFO: Create Important Links widget and update the widget title");
		createWidget("xccImportantBookmark");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("ImportantBookmark", widgetTitle, communityName);

		logger.strongStep("Check all and only Important Links are displayed");
		log.info("INFO: Check all and only Important Links are displayed");
		for(int i = 0; i < bookmarks.length; i++) {
			if(i % 2 == 0) {
				Assert.assertTrue(ui.fluentWaitPresent("css=div.xccImportantBookmark a[href='" + bookmarks[i].getURL() + "']"),
						"Bookmark with title " + bookmarks[i].getTitle() + " not found");
			}  else {
				Assert.assertFalse(ui.isElementVisible("css=div.xccImportantBookmark a[href='" + bookmarks[i].getURL() + "']"),
						"Bookmark with title " + bookmarks[i].getTitle() + " displayed when it shouldn't be");
			}
		}

		logger.strongStep("Change number of items per page");
		log.info("INFO: Change number of items per page");
		ui.editWidget(widgetTitle);
		ui.clickLinkWithJavascript(IcecUI.contentSourcesDropdown);
		ui.clearAndTypeText(IcecUI.numberOfItemsPerPage, "1");
		ui.saveWidget(widgetTitle);
		driver.executeScript("scroll(0,-400);");

		logger.strongStep("Page through and check links");
		log.info("INFO: Page through and check links");
		for(int i = 0, j = 0; i < bookmarks.length; i++) {
			if(i % 2 == 0) {
				j++;
				Assert.assertTrue(ui.fluentWaitPresent("css=div.xccImportantBookmark a[href='" + bookmarks[i].getURL() + "']"),
						"Bookmark with title " + bookmarks[i].getTitle() + " not found");
				Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.lotusLeft").equals("Viewing " + j + "-" + j +" of 3 links"),
						"Paging text doesn't match expected: " + "Viewing " + j + "-" + j +" of 3 links");
				if(j<3) ui.clickLink(IcecUI.getWidget(widgetTitle) + " a[aria-label='Show next items']");
			}
		}
		
		logger.strongStep("Delete created Important Links widget");
		log.info("INFO: Delete created Important Links widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Links widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and add bookmarks to it.
	*<li><B>Step:</B> Navigate to the community and trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Links widget.
	*<li><B>Step:</B> Create Links widget and update its title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check all bookmark links are displayed in widget.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Change number of items per page.
	*<li><B>Step:</B> Save.
	*<li><B>Verify:</B> Page through links and check content.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Enable search.
	*<li><B>Step:</B> Save.
	*<li><B>Step:</B> Search for link.
	*<li><B>Verify:</B> Ensure link is displayed.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testLinks() throws Exception{
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Bookmark List";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Bookmarks for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		logger.strongStep("Login, create a community and navigate to the community");
		log.info("INFO: Login, create a community and navigate to the community");
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		logger.strongStep("Create bookmarks");
		log.info("INFO: Create bookmarks");
		BaseDogear[] bookmarks = ui.createBookmarks(testUser, serverURL, community, 6);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default 'Bookmarks - List' widget");
		log.info("INFO: Remove the default 'Bookmarks - List' widget");
		removeAndDeleteWidgets(widgetType);
		
		logger.strongStep("Create Links widget and update the widget title");
		log.info("INFO: Create Links widget and update the widget title");
		createWidget("xccBookmark");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("Bookmark", widgetTitle, communityName);

		logger.strongStep("Check all Links are displayed");
		log.info("INFO: Check all Links are displayed");
		for(BaseDogear bm : bookmarks) {
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[title='" + bm.getTitle() + "']"),
					"Bookmark with title " + bm.getTitle() + " not found");
		}

		logger.strongStep("Change number of items per page");
		log.info("INFO: Change number of items per page");
		ui.editWidget(widgetTitle);
		ui.clickLinkWithJavascript(IcecUI.contentSourcesDropdown);
		ui.clearAndTypeText(IcecUI.numberOfItemsPerPage, "2");
		ui.saveWidget(widgetTitle);
		driver.executeScript("scroll(0,-400);");

		logger.strongStep("Page through and check links");
		log.info("INFO: Page through and check links");
		for(int i = bookmarks.length - 1, j = 0; i >=0; i--, j++) {
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[title='" + bookmarks[i].getTitle() + "']"),
					"Bookmark with title " + bookmarks[i].getTitle() + " not found");
			if (j % 2 != 0 && i != 0) {
				Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.lotusLeft").equals("Viewing " + j + "-" + (j+1) +" of 6 links"),
						"Paging text doesn't match expected: " + "Viewing " + j + "-" + (j+1) +" of 6 links");
				ui.clickLink(IcecUI.getWidget(widgetTitle) + " a[aria-label='Show next items']");
			}
		}

		logger.strongStep("Enable search in widget");
		log.info("INFO: Enable search in widget");
		ui.editWidget(widgetTitle);
		ui.clickLinkWithJavascript(IcecUI.displaySettingDropdown);
		ui.selectCheckbox("css=input[aria-label='Search']");
		ui.saveWidget(widgetTitle);

		logger.strongStep("Search for link and ensure it's displayed");
		log.info("INFO: Search for link and ensure it's displayed");
		ui.typeText(IcecUI.getWidget(widgetTitle) + " input[rel='search']", bookmarks[3].getTitle());
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[title='" + bookmarks[4].getTitle() + "']"),
				"Bookmark with title " + bookmarks[4].getTitle() + " not found");
		
		logger.strongStep("Delete created Links widget");
		log.info("INFO: Delete created Links widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test My Links widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Navigate to the Profiles page and add a few My Links.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default My Links widget.
	*<li><B>Step:</B> Create My Links widget and update it title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check all bookmark links are displayed in widget.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Change number of items per page.
	*<li><B>Step:</B> Save.
	*<li><B>Verify:</B> Page through links and check content.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Enable search.
	*<li><B>Step:</B> Save.
	*<li><B>Step:</B> Search for link.
	*<li><B>Verify:</B> Ensure link is displayed.
	*<li><B>Step:</B> Delete the widget and the links.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testMyLinks() throws Exception{
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "My Links";
		String widgetTitle = "My Links for " + testName + Helper.genDateBasedRand();
		
		Map<String, String> links = new HashMap<String, String>();
		links.put("IBM" + Helper.genDateBasedRand(), "ibm.com" + Helper.genDateBasedRand());
		links.put("Google" + Helper.genDateBasedRand(), "google.com" + Helper.genDateBasedRand());
		links.put("Cisco" + Helper.genDateBasedRand(), "cisco.com" + Helper.genDateBasedRand());

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndLoadCurrentUrlWithHttps(testUser);

		logger.strongStep("Navigate to the Profiles page and add My Links");
		log.info("INFO: Navigate to the Profiles page and add My Links");
		ui.loadUrlWithHttps(serverURL + "/profiles/html/myProfileView.do");
		for(Map.Entry<String, String> link : links.entrySet()){
			ui.clickLink(ProfilesUIConstants.ProfilesAddLink);
			ui.typeText(ProfilesUIConstants.ProfilesAddLinkName, link.getKey());
			ui.typeText(ProfilesUIConstants.ProfilesAddLinkLinkname, link.getValue());
			ui.clickSaveButton();
		}
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default My Links widget");
		log.info("INFO: Remove the default My Links widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create My Links widget and update the widget title");
		log.info("INFO: Create My Links widget and update the widget title");
		createWidget("xccMyLinks");
		updateWidgetTitle(widgetType, widgetTitle);
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");
		
		logger.strongStep("Check all Links are displayed");
		log.info("INFO: Check all Links are displayed");
		for(Map.Entry<String, String> link : links.entrySet()) {
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a:contains(" + link.getKey() + ")"),
					"Link with title " + link.getKey() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[href*='" + link.getValue() + "']"),
					"Link " + link.getValue() + " not found");
		}
		
		logger.strongStep("Delete created MyLinks widget");
		log.info("INFO: Delete created MyLinks widget");
		removeAndDeleteWidget(widgetTitle);
		
		logger.strongStep("Delete added MyLinks");
		log.info("INFO: Delete added MyLinks");
		ui.deleteMyLinks(serverURL, links);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test My Quicklinks widget.
	*<li><B>Step:</B> Login to the ICEC component as the admin user.
	*<li><B>Step:</B> Remove the default My Quicklinks widget.
	*<li><B>Step:</B> Create My Quicklinks Widget.
	*<li><B>Step:</B> Update the widget title.
	*<li><B>Step:</B> Click on the 'Add new Quicklink' button in the widget.
	*<li><B>Step:</B> Create some Quicklinks using the widget.
	*<li><B>Verify:</B> Check all Quicklinks are displayed.
	*<li><B>Step:</B> Delete the My Quicklinks widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testMyQuicklinks() {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "My Quicklinks";
		String widgetTitle = "My Quicklinks for " + testName + Helper.genDateBasedRand();
		
		Map<String, String> links = new HashMap<String, String>();
		links.put("IBM" + Helper.genDateBasedRand(), "ibm.com" + Helper.genDateBasedRand());
		links.put("Google" + Helper.genDateBasedRand(), "google.com" + Helper.genDateBasedRand());
		links.put("Cisco" + Helper.genDateBasedRand(), "cisco.com" + Helper.genDateBasedRand());
		
		logger.strongStep("Load the ICEC component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the ICEC component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentIcec);
		ui.loginAndLoadCurrentUrlWithHttps(testUser);
		
		logger.strongStep("Remove the default My Quicklinks widget");
		log.info("INFO: Remove the default My Quicklinks widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create My Quicklinks Widget and update the widget title");
		log.info("INFO: Create My Quicklinks Widget and update the widget title");
		createWidget("xccMyQuicklinks");
		updateWidgetTitle(widgetType, widgetTitle);
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");
		
		logger.strongStep("Click on the 'Add new Quicklink' button and create some Quicklinks");
		log.info("INFO: Click on the 'Add new Quicklink' button and create some Quicklinks");
		for(Map.Entry<String, String> link : links.entrySet()) {
			ui.clickLinkWithJavascript("css=div.xccMyQuicklinks button.createBookmark");
			ui.typeText("css=input[name='bookmarkTitle']", link.getKey());
			ui.typeText("css=input.bookmarkUrl", link.getValue());
			ui.clickLink(IcecUI.widgetCreateButton);
		}
		
		logger.strongStep("Check all Quicklinks are displayed");
		log.info("INFO: Check all Quicklinks are displayed");
		for(Map.Entry<String, String> link : links.entrySet()) {
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a:contains(" + link.getKey() + ")"),
					"Link with title " + link.getKey() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[href*='" + link.getValue() + "']"),
					"Link " + link.getValue() + " not found");
		}

		logger.strongStep("Delete created My Quicklinks widget");
		log.info("INFO: Delete created My Quicklinks widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Tag Cloud widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Create tags using Forum Topics in the community.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default 'Tag Cloud' widget.
	*<li><B>Step:</B> Create Tag Cloud widget.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Verify all tags appear as expected.
	*<li><B>Step:</B> Click on a tag.
	*<li><B>Verify:</B> Switch to new tab and ensure tag search page opens.
	*<li><B>Step:</B> Switch back to the ICEC tab.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	//Disabling this test as tags are not visible on 'tag cloud' widget, a defect has been raised for this issue - https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10657.
	@Test(groups = {"regression"}, enabled=false)
	public void testTagCloud() throws UnsupportedEncodingException {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Tag Cloud";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Tag Cloud for " + communityName;
		String[] tags = {"atag" + Helper.genDateBasedRand(), "btag" + Helper.genDateBasedRand(),
				"ctag" + Helper.genDateBasedRand(), "dtag" + Helper.genDateBasedRand(), "etag" + Helper.genDateBasedRand()};

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .description(description).build();
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");

		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default 'Tag Cloud' widget");
		log.info("INFO: Remove the default 'Tag Cloud' widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create forum topics with tags");
		log.info("INFO: Create forum topics with tags");
		ui.createForumTopics(testUser, serverURL, community, tags.length, tags);
		
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("forums");
		
		logger.strongStep("Create Tag Cloud Widget and update the widget title");
		log.info("INFO: Create Tag Cloud Widget and update the widget title");
		createWidget("xccTagCloud");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("TagCloud", widgetTitle, communityName);
		
		logger.strongStep("Verify all tags appear as expected");
		log.info("INFO: Verify all tags appear as expected");
		for (String tag : tags) {
			Assert.assertTrue(ui.fluentWaitPresent("css=div.xccTagCloud a:contains(" + tag + ")"),
					"Tag " + tag + " not found");
		}
		
		logger.strongStep("Click on the first tag");
		log.info("INFO: Click on the first tag");
		ui.clickLink("css=div.xccTagCloud a:contains(" + tags[0] + ")");
		
		logger.strongStep("Switch to new tab and ensure tag search page opens");
		log.info("INFO: Switch to new tab and ensure tag search page opens");
		ui.switchToNextTab();
		ui.waitForPageLoaded(driver);
		String tagSearchUrl = ui.replaceHttpWithHttps(serverURL + "/communities/service/html/communityview?" 
				+ community.getCommunityUUID() + "#tag=" + tags[0]);
		Assert.assertTrue(driver.getCurrentUrl().contains(tagSearchUrl),
		    	"Tag search opened with incorrect URL: " + tagSearchUrl);
		
		logger.strongStep("Switch back to the ICEC tab");
		log.info("INFO: Switch back to the ICEC tab");
		driver.switchToFirstMatchingWindowByPageTitle("xcc");
		
		logger.strongStep("Delete created Tag Cloud widget");
		log.info("INFO: Delete created Tag Cloud widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Events widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create an event inside the community.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Events widget.
	*<li><B>Step:</B> Create Event Calendar widget and update the widget title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check event widget buttons are displayed.
	*<li><B>Step:</B> Click Day(List) button.
	*<li><B>Verify:</B> Check event listing is displayed.
	*<li><B>Step:</B> Click Week(List) button.
	*<li><B>Verify:</B> Check event listing is displayed.
	*<li><B>Step:</B> Click Upcoming button.
	*<li><B>Verify:</B> Check event listing is displayed.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Select Week(List) view.
	*<li><B>Step:</B> Click save.
	*<li><B>Verify:</B> Check correct view is displayed/li>
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testEvents() throws Exception {
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Event Calendar";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Events for " + communityName;
		BaseWidget[] widgetsToAdd = {BaseWidget.EVENTS};
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, null, widgetsToAdd);
		communitiesToDelete.add(comAPI);
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		
		Community_TabbedNav_Menu.EVENTS.select(ui);
		String eventName = "Event" + Helper.genDateBasedRand();
		String startTime = "02:15 PM";
		String endTime = "03:15 PM";
		
		logger.strongStep("Create event");
		log.info("INFO: Create event");
		ui.createEvent(eventName, startTime, endTime, calUI);

		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default 'Event Calendar' widget");
		log.info("INFO: Remove the default 'Event Calendar' widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create Event Calendar widget and update the widget title");
		log.info("INFO: Create Event Calendar widget and update the widget title");
		createWidget("xccEvent");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("Event", widgetTitle, communityName);

		logger.strongStep("Check Events buttons are present");
		log.info("INFO: Check Events buttons are present");
		String[] buttonSelectors = {IcecUI.prevButton, IcecUI.nextButton,
				IcecUI.todayButton, IcecUI.selectDateButton,
				IcecUI.dayButton, IcecUI.dayListButton,
				IcecUI.weekButton, IcecUI.weekListButton,
				IcecUI.monthButton, IcecUI.upcomingButton};
		ui.checkElementsArePresent(buttonSelectors);

		logger.strongStep("Click Day (List) Button and verify the event");
		log.info("INFO: Click Day (List) Button and verify the event");
		ui.clickLink(IcecUI.dayListButton);
		startTime = startTime.replace(" PM", "pm");
		endTime = endTime.replace(" PM", "pm");
		Assert.assertTrue(ui.getElementText("css=span.fc-time").equals(startTime + " - " + endTime),
				"Day(List) Event Time doesn't match expected: " + startTime + " - " + endTime);
		Assert.assertTrue(ui.getElementText("css=span.fc-title").equals(eventName),
				"Day(List) Event name doesn't match expected: " + eventName);

		logger.strongStep("Click Week (List) Button and verify the event");
		log.info("INFO: Click Week (List) Button and verify the event");
		ui.clickLink(IcecUI.weekListButton);
		Assert.assertTrue(ui.getElementText("css=span.fc-title").equals(eventName),
				"Week(List) Event name doesn't match expected: " + eventName);

		logger.strongStep("Click Upcoming Button and verify the event");
		log.info("INFO: Click Upcoming Button and verify the event");
		ui.clickLink(IcecUI.upcomingButton);
		Assert.assertTrue(ui.getElementText("css=span.fc-list-item-title a").equals(eventName),
				"Upcoming Event name doesn't match expected: " + eventName);

		logger.strongStep("Click Widget Edit Button");
		log.info("INFO: Click Widget Edit Button");
		ui.editWidget(widgetTitle);

		logger.strongStep("Select Week (List) from edit menu");
		log.info("INFO: Select Week (List) from edit menu");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.DISPLAY_SETTINGS.toString());
		ui.selectEventLayoutInEditView("basicWeek");
		ui.saveWidget(widgetTitle);
		driver.executeScript("scroll(0,-400);");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.fc-basicWeek-view"),
				"Week list view not found");
		
		logger.strongStep("Delete created Event Calendar widget");
		log.info("INFO: Delete created Event Calendar widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Forums widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and add forum topics to it.
	*<li><B>Step:</B> Navigate to the community and trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Forums widget.
	*<li><B>Step:</B> Create Forums widget and update its title.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check all forum topics are displayed in widget.
	*<li><B>Step:</B> Click on a topic.
	*<li><B>Verify:</B> Check forum topic popup appears as expected.
	*<li><B>Step:</B> Click like.
	*<li><B>Verify:</B> Check 'Unlike' is displayed and number of likes is 1.
	*<li><B>Step:</B> Click Unlike.
	*<li><B>Verify:</B> Check 'Like' is displayed and number of likes is 0.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testForums() throws Exception{
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Forum Viewer";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Forums for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");
		communitiesToDelete.add(comAPI);

		BaseForumTopic[] topics = ui.createForumTopics(testUser, serverURL, community, 6);
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("forums");
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default 'Forum Viewer' widget");
		log.info("INFO: Remove the default 'Forum Viewer' widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create Forum Viewer widget and update the widget title");
		log.info("INFO: Create Forum Viewer widget and update the widget title");
		createWidget("xccForum");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("Forum", widgetTitle, communityName);

		logger.strongStep("Verify that all forum topics from the community are visible in the widget");
		log.info("INFO: Verify that all forum topics from the community are visible in the widget");
		for(int i = 0; i < topics.length; i++) {
			BaseForumTopic topic = topics[i];
			logger.strongStep("Check forum topic " + topic.getTitle() + " appears as expected");
			log.info("INFO: Check forum topic " + topic.getTitle() + " appears as expected");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getForumTopicLink(serverURL, topic)),
					"Topic with title " + topic.getTitle() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getForumTopicDesc(serverURL, topic)),
					"Topic description " + topic.getDescription() + " not found");
		}

		logger.strongStep("Click on the forum topic: " + topics[0]);
		log.info("INFO: Click on the forum topic: " + topics[0]);
		ui.clickLink(IcecUI.getForumTopicLink(serverURL, topics[0]));

		logger.strongStep("Check forum topic popup for " + topics[0].getTitle() + " appears as expected");
		log.info("INFO: Check forum topic popup for " + topics[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Topic popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getTopicFlyoutTitle(serverURL, topics[0])).equals(topics[0].getTitle()),
				"Topic popup title doesn't equal: " + topics[0].getTitle() + ". Actual: " + ui.getElementText(IcecUI.getTopicFlyoutTitle(serverURL, topics[0])));

		logger.strongStep("Click like button");
		log.info("INFO: Click like button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Check 'Unlike' is displayed and number of likes is 1");
		log.info("INFO: Check 'Unlike' is displayed and number of likes is 1");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Unlike"),
				"'Like' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("1"),
				"'Like' text is not equal to '1'");

		logger.strongStep("Click like button");
		log.info("INFO: Click like button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Check 'Like' is displayed and number of likes is 0");
		log.info("INFO: Check 'Like' is displayed and number of likes is 0");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Like"),
				"'Unlike' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("0"),
				"'Like' text is not equal to '0'");

		logger.strongStep("Delete created Forum Viewer widget");
		log.info("INFO: Delete created Forum Viewer widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Ideation Blogs widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and add ideas to it using the Ideation Blogs application.
	*<li><B>Step:</B> Navigate to the community and trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default Ideation Blogs widget.
	*<li><B>Step:</B> Create Ideation Blogs widget.
	*<li><B>Step:</B> Add the community created before as the source of the widget.
	*<li><B>Verify:</B> Check all ideas are displayed in widget.
	*<li><B>Step:</B> Click Vote.
	*<li><B>Verify:</B> Check 'Unvote' is displayed and number of votes is 1.
	*<li><B>Step:</B> Click Unvote.
	*<li><B>Verify:</B> Check 'Vote' is displayed and number of votes is 0.
	*<li><B>Step:</B> Click on an idea.
	*<li><B>Verify:</B> Check idea popup appears as expected.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testIdeationBlogs() throws Exception{
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Ideation Blog";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Ideation Blogs for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("communities");

		BaseBlogPost[] ideas = ui.createIdeas(testUser, serverURL, comAPI, community, 4);
		
		logger.strongStep("Navigate to the ICEC Component");
		log.info("INFO: Navigating to the ICEC Component");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		
		logger.strongStep("Remove the default 'Ideation Blog' widget");
		log.info("INFO: Remove the default 'Ideation Blog' widget");
		removeAndDeleteWidgets(widgetType);

		logger.strongStep("Create Ideation Blog widget and update the widget title");
		log.info("INFO: Create Ideation Blog widget and update the widget title");
		createWidget("xccIdeationBlog");
		updateWidgetTitle(widgetType, widgetTitle);
		selectCommunitySource("IdeationBlog", widgetTitle, communityName);
		
		logger.strongStep("Verify that all ideas are visible in the widget");
		log.info("INFO: Verify that all ideas are visible in the widget");
		for(int i = 0; i < ideas.length; i++) {
			BaseBlogPost idea = ideas[i];
			logger.strongStep("Check idea " + idea.getTitle() + " appears as expected");
			log.info("INFO: Check idea " + idea.getTitle() + " appears as expected");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getIdeaLink(idea)),
					"Topic with title " + idea.getTitle() + " not found");
			Assert.assertTrue(ui.fluentWaitTextPresent(idea.getContent()),
					"Topic content for " + idea.getTitle() + " not found");
		}

		driver.executeScript("scroll(0, -250);");
		logger.strongStep("Click vote button");
		log.info("INFO: Click vote button");
		ui.clickLink(IcecUI.ideaVotingButton);
		driver.executeScript("scroll(0, -250);");
		
		logger.strongStep("Check 'Unvote' is displayed and number of votes is 1");
		log.info("INFO: Check 'Unvote' is displayed and number of votes is 1");
		Assert.assertTrue(ui.getFirstVisibleElement(IcecUI.ideaVotingNumber).getText().equals("1"),
				"Number of votes not equal to '1'");
		
		logger.strongStep("Click vote button again and verify the number of likes is 0");
		log.info("INFO: Click vote button again and verify the number of likes is 0");
		ui.clickLink(IcecUI.ideaVotingButton);
		Assert.assertTrue(ui.getFirstVisibleElement(IcecUI.ideaVotingNumber).getText().equals("0"),
				"Number of votes not equal to '0'");

		driver.executeScript("scroll(0, -250);");
		logger.strongStep("Click Unvote");
		log.info("INFO: Click Unvote");
		ui.clickLink(IcecUI.getIdeaLink(ideas[0]));

		logger.strongStep("Check idea popup for " + ideas[0].getTitle() + " appears as expected");
		log.info("INFO: Check idea popup for " + ideas[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Idea popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.blogPopupTitle).equals(ideas[0].getTitle()),
				"Idea popup title doesn't equal: " + ideas[0].getTitle() + ". Actual: " + ui.getElementText(IcecUI.blogPopupTitle));
		
		logger.strongStep("Delete created Ideation Blog widget");
		log.info("INFO: Delete created Ideation Blog widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test Static Content - Blogs widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and add a blog to it.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Step:</B> Navigate to the ICEC page.
	*<li><B>Step:</B> Remove the default 'Static Content - Blog' widget.
	*<li><B>Step:</B> Create Static Content - Blog widget and update the widget title.
	*<li><B>Step:</B> Edit widget and add the community created before as the source of the widget.
	*<li><B>Step:</B> Select Latest .
	*<li><B>Step:</B> Save widget .
	*<li><B>Verify:</B> Check post is displayed in widget.
	*<li><B>Step:</B> Edit widget .
	*<li><B>Step:</B> Select specific blog post .
	*<li><B>Step:</B> Save widget .
	*<li><B>Verify:</B> Check post is displayed in widget.
	*<li><B>Step:</B> Delete the widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testStaticContentBlog() throws Exception{
		if (isIcecLight) {
			log.info("Standalone ICEC not available. Skipping test.");
			return;
		}
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getAdminUser();

		String testName = ui.startTest();
		String widgetType = "Static Content - Blog";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Static Content - Blog for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		
		logger.strongStep("Trigger indexing for communities");
		log.info("INFO: Trigger indexing for communities");
		new SearchAdminService().indexNow("communities");

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 1);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Trigger indexing for blogs");
		log.info("INFO: Trigger indexing for blogs");
		new SearchAdminService().indexNow("blogs");
		
		log.info("INFO: Re-loading community after indexing");
		community.navViaUUID(commUI);
		
		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		log.info("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
		
		if (!flag) {
			
			if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty()) 	        	
				community.addWidget(commUI, BaseWidget.HIGHLIGHTS);
			
			logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
			log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
			Community_TabbedNav_Menu.HIGHLIGHTS.select(ui,2);
			
		}	
		
		logger.strongStep("Click on Blog Post, Edit and post the blog");
		log.info("INFO: Click on Blog Post, Edit and post the blog");
		ui.clickLink(IcecUI.getWidgetTile(blogPosts[0].getTitle()));
		ui.clickLink(IcecUI.editBlogPostLink);
		ui.clickLink(IcecUI.postBtn);

		logger.strongStep("Navigate to the ICEC Component and remove the existing widget");
		log.info("INFO: Navigating to the ICEC Component and remove the existing widget");
		ui.loadUrlWithHttps(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentIcec);
		removeAndDeleteWidgets(widgetType);
		
		logger.strongStep("Create Static Content - Blog widget and update the widget title");
		log.info("INFO: Create Static Content - Blog widget and update the widget title");
		createWidget("xccStaticContentBlog");
		updateWidgetTitle(widgetType, widgetTitle);

		logger.strongStep("Edit widget, Click on 'Content Sources' and search for community name");
		log.info("INFO: Edit widget, Click on 'Content Sources' and search for community name");
		ui.editWidget(widgetTitle);
		ui.clickLinkWithJavascript(IcecUI.contentSourcesDropdown);
		ui.typeText(IcecUI.selectComSourceField, communityName);
		ui.clickLink(IcecUI.getSelectedBlogEntry(communityName));
		
		logger.strongStep("Click Latest and Save");
		log.info("INFO: Click Latest and Save");
		ui.clickLink(IcecUI.latestBtn);
		ui.saveWidget(widgetTitle);

		logger.strongStep("Verify blog post is displayed in widget");
		log.info("INFO: Verify blog post is displayed in widget");
		Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog").equals(blogPosts[0].getContent()),
				"Blog entry doesn't equal: " + blogPosts[0].getContent() + ". Actual: " + ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog"));

		logger.strongStep("Edit widget");
		log.info("INFO: Edit widget");
		ui.editWidget(widgetTitle);
		
		logger.strongStep("Select specific blog post and Save");
		log.info("INFO: Select specific blog post and Save");
		ui.clickLinkWithJavascript(IcecUI.contentSourcesDropdown);
		ui.clickLink(IcecUI.selectedBtn);
		ui.scrollIntoViewElement(IcecUI.plusBtn);
		ui.clickLink(IcecUI.plusBtn);
		ui.typeText(IcecUI.selectPostTextField, blogPosts[0].getTitle());
		ui.clickLink(IcecUI.getSelectedBlogEntry(blogPosts[0].getTitle()));
		ui.saveWidget(widgetTitle);

		logger.strongStep("Verify blog post is displayed in widget");
		log.info("INFO: Verify blog post is displayed in widget");
		Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog").equals(blogPosts[0].getContent()),
				"Blog entry doesn't equal: " + blogPosts[0].getContent() + ". Actual: " + ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog"));
		
		logger.strongStep("Delete created Static Content - Blog widget");
		log.info("INFO: Delete created Static Content - Blog widget");
		removeAndDeleteWidget(widgetTitle);
		ui.endTest();
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanUp() {
		User adminTestUser = cfg.getUserAllocator().getAdminUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, adminTestUser.getAttribute(cfg.getLoginPreference()), adminTestUser.getPassword());
		for (Community community : communitiesToDelete) {
			log.info("INFO: Deleting community: " + community.getTitle());
			apiOwner.deleteCommunity(community);
		}
		APIFileHandler filesApiOwner = new APIFileHandler(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()),
				adminTestUser.getAttribute(cfg.getLoginPreference()), adminTestUser.getPassword());
		for (FileEntry fileEntry : filesToDelete) {
			log.info("INFO: Deleting file: " + fileEntry.getTitle());
			filesApiOwner.deleteFile(fileEntry);
		}
	}
	
	private void removeAndDeleteWidget(String widgetTitle) {
		String title=driver.getSingleElement(IcecUI.getWidget(widgetTitle)).getAttribute("data-wname");
		if (ui.isElementPresent(IcecUI.getWidget(widgetTitle))){
			log.info("INFO: Trying remove");
			ui.removeWidget(widgetTitle);
			ui.deleteWidgetTileFromPanel(title);
		}
	}
	
	private void removeAndDeleteWidgets(String partialWidgetTitle) {
		List<Element> widgetSelectors = driver.getElements(IcecUI.getWidget(partialWidgetTitle));
		log.info("INFO: Trying remove. Selectors Size : " + widgetSelectors.size());
		List<String> widgetTitles = new ArrayList<String>(); 
		for(Element widgetSelector : widgetSelectors) {
			log.info("INFO: Trying remove. Selector : " + widgetSelector.getText().split("\\s+")[0]);
			widgetTitles.add(widgetSelector.getText().split("\\s+")[0]);
		}
		for(String widgetTitle : widgetTitles) {
			removeAndDeleteWidget(widgetTitle);
		}
	}
	
	private void createWidget(String widgetTypeId) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		driver.executeScript("scroll(0,-250);");
		logger.strongStep("Create " + widgetTypeId + " widget");
		log.info("INFO: Create " + widgetTypeId + " widget");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.selectType(widgetTypeId);
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.fluentWaitTextNotPresent("has been created.");
	}
	
	private void updateWidgetTitle(String widgetType, String widgetTitle){
		ui.editWidget(widgetType);
		log.info("INFO: Enter ID");
		ui.clearAndTypeText(IcecUI.idTextField, widgetTitle);
		ui.clickLinkWait(IcecUI.widgetDialogCreateButton);
	}
	
	private void selectCommunitySource(String widgetTypeId, String widgetTitle, String communityName) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Select Community Source");
		log.info("INFO: Select Community Source");
		ui.editWidget(widgetTitle);
		ui.clickLinkWithJavascript(IcecUI.contentSourcesDropdown);
		ui.clearAndTypeText(IcecUI.getCommunitySourceInput(widgetTypeId), communityName);
		ui.clickLink(IcecUI.getSelectedBlogEntry(communityName));
		ui.saveWidget(widgetTitle);
	}
}
