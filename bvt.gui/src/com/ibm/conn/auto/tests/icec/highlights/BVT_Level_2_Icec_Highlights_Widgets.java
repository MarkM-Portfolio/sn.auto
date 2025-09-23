package com.ibm.conn.auto.tests.icec.highlights;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.conn.auto.webui.onprem.ProfilesUIOnPrem;

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
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_Level_2_Icec_Highlights_Widgets extends SetUpMethods2 {
	private static Logger log = LoggerFactory
			.getLogger(BVT_Level_2_Icec_Highlights_Widgets.class);
	private IcecUI ui;
	private CommunitiesUI commUI;
	private CalendarUI calUI;
	private WikisUI wikisUi;
	private TestConfigCustom cfg;
	private BaseCommunity.Access defaultAccess;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private ArrayList<Community> communitiesToDelete;
	private ArrayList<FileEntry> filesToDelete;
	private User testUser;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		communitiesToDelete = new ArrayList<Community>();
		filesToDelete = new ArrayList<FileEntry>();
		URLConstants.setServerURL(serverURL);
		testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

	    // initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = IcecUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		wikisUi = WikisUI.getGui(cfg.getProductName(), driver);
		commUI  = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}
	 
	/**
	*<ul>
	*<li><B>Info:</B> Test the Community Description widget.
	*<li><B>Step:</B> Create a community then login and navigate to the Overview page in the community.
	*<li><B>Step:</B> Edit the Community Description widget and click on the 'Save and Close' button without making any changes.
	*<li><B>Step:</B> Navigate to Highlights tab using the navigation menu.
	*<li><B>Step:</B> Remove the default Community Description widget.
	*<li><B>Step:</B> Add a new Community Description widget by using the Customize button.
	*<li><B>Verify:</B> The title of the new Community Description widget is Community Description.
	*<li><B>Step:</B> Edit the new Community Description widget and prepend a text to the description.
	*<li><B>Verify:</B> The Community Description widget has the updated description.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "smokeonprem"}, enabled=true)
	public void testCommunityDescription() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String widgetTitle = "Community Description";
		String CommunityDescription = "Test Description for testcase: " + testName;
		String prependCommunityDescriptionText = "Updated the Community Description application.";
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				   .access(Access.PUBLIC)
				   .description(CommunityDescription)
				   .build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		communitiesToDelete.add(comAPI);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
		
		if (flag) {

		    logger.strongStep("Add the Overview page to the Community and make it the landing page");
		    log.info("INFO: Add the Overview page to the Community and make it the landing page");
		    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		
		}
		
		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Edit the Community Description widget and click on the 'Save and Close' button without making any changes");
		log.info("INFO: Edit the Community Description widget and click on the 'Save and Close' button without making any changes");
		ui.fluentWaitElementVisible(IcecUI.getOverviewActionsMenu("Community Description"));
		ui.clickLinkWithJavascript(IcecUI.getOverviewActionsMenu("Community Description"));
		ui.clickLinkWait(IcecUI.actionsMenuEdit);
		driver.executeScript("arguments[0].scrollIntoView(true);",
		driver.getSingleElement(IcecUI.actionsSaveAndClose).getWebElement());
		ui.clickLinkWithJavascript(IcecUI.actionsSaveAndClose);
		ui.fluentWaitElementVisible("css=div.communityOverview");
		driver.executeScript("scroll(0,-250);");
					  
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty()) {
	        	
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.HIGHLIGHTS);
			driver.navigate().refresh();
			ui.waitForPageLoaded(driver);
	        	
		}

		logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
		log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		  	
		logger.strongStep("Remove the default Community Description widget");
		log.info("INFO: Remove the default Community Description widget");
		ui.removeWidget("description");

		logger.strongStep("Add a new Community Description widget by using the Customize button");
		log.info("INFO: Create a new Community Description widget by using the Customize button");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.addWidgetsFromHiddenApps("description");

		logger.strongStep("Wait for Community Description Widget to display.");
		log.info("INFO: Wait for Community Description Widget to display");
		ui.fluentWaitElementVisible(IcecUI.communityDescription + " > div.wheader");

		logger.strongStep("Validate that the title of the Community Description widget is: " + widgetTitle);
		log.info("INFO: Validate that the title of the Community Description widget is: " + widgetTitle);
		Assert.assertTrue(ui.getElementText(IcecUI.communityDescription + " > div.wheader").equals(widgetTitle),
				"The title of the Community Description widget is not: " + widgetTitle);

		logger.strongStep("Edit the Community Description widget");
		log.info("INFO: Edit the Community Description widget");
		ui.editWidget(false, "description");

		logger.strongStep("Prepend '" + prependCommunityDescriptionText + "' to the Community Description text");
		log.info("INFO: Prepend '" + prependCommunityDescriptionText + "' to the Community Description text");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		ui.typeTextWithDelay(BaseUIConstants.StatusUpdate_Body, prependCommunityDescriptionText);
		driver.switchToFrame().returnToTopFrame();
		
		logger.strongStep("Click on the 'Save and Close' button");
		log.info("Click on the 'Save and Close' button");
		ui.clickLinkWithJavascript(IcecUI.actionsSaveAndClose);

		logger.strongStep("Validate that the Community Description widget now has the text '" + prependCommunityDescriptionText + CommunityDescription + "'");
		log.info("INFO: Validate that the Community Description widget now has the text '" + prependCommunityDescriptionText + CommunityDescription + "'");
		Assert.assertTrue(ui.getElementText(IcecUI.getCommunityDescriptionText("description")).equals(prependCommunityDescriptionText + CommunityDescription),
			"The Community Description widget does not have the text '" + prependCommunityDescriptionText + CommunityDescription + "'");

		ui.endTest();
	}
	
	/**
	 *
	*<ul>
	*<li><B>Info:</B> Test Events widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create event.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Events widget.
	*<li><B>Step:</B> Create Events widget.
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
	*</ul>
	*/
	//Disabling this test as it is causing error while selecting Event widget. The defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10695 has been raised for this.
	@Test(groups = {"regression"}, enabled=false)
	public void testEvents() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "Event Calendar";
		
		BaseWidget[] widgetsToAdd = {BaseWidget.EVENTS};
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, null, widgetsToAdd);
		communitiesToDelete.add(comAPI);

		logger.strongStep("Navigate to the Events page");
		log.info("INFO: Navigate to the Events page");
		Community_TabbedNav_Menu.EVENTS.select(ui);
		String eventName = "Event" + Helper.genDateBasedRand();
		String startTime = "02:15 PM";
		String endTime = "03:15 PM";
		
		logger.strongStep("Create event");
		log.info("INFO: Create event");
		ui.createEvent(eventName, startTime, endTime, calUI);
		
		logger.strongStep("Navigate to the Highlights page and remove default Events widget");
		log.info("INFO: Navigate to the Highlights page and remove default Events widget");
		ui.clickLinkWait(CommunitiesUIConstants.communityHighlights);
		ui.removeWidget(widgetType);

		logger.strongStep("Create Events widget");
		log.info("INFO: Create Events widget");
		ui.createHighlightsWidget("xccEvent", eventName, widgetType);
		
		logger.strongStep("Check Events buttons are present");
		log.info("INFO: Check Events buttons are present");
		driver.executeScript("scroll(0,+250);");
		String[] buttonSelectors = {IcecUI.prevButton, IcecUI.nextButton,
				IcecUI.todayButton, IcecUI.selectDateButton,
				IcecUI.dayButton, IcecUI.dayListButton,
				IcecUI.weekButton, IcecUI.weekListButton,
				IcecUI.monthButton, IcecUI.upcomingButton};
		ui.checkElementsArePresent(buttonSelectors);

		logger.strongStep("Click Day (List) Button");
		log.info("INFO: Click Day (List) Button");
		ui.clickLink(IcecUI.dayListButton);
		startTime = startTime.replace(" PM", "pm");
		endTime = endTime.replace(" PM", "pm");
		
		logger.strongStep("Verify Day Event Time and name are displayed");
		log.info("INFO: Verify Day Event Time and name are displayed");
		Assert.assertTrue(ui.getElementText("css=span.fc-time").equals(startTime + " - " + endTime),
				"Day(List) Event Time doesn't match expected: " + startTime + " - " + endTime);
		Assert.assertTrue(ui.getElementText("css=span.fc-title").equals(eventName),
				"Day(List) Event name doesn't match expected: " + eventName);

		logger.strongStep("Click Week (List) Button and verify Week event is displayed");
		log.info("INFO: Click Week (List) Button and verify Week event is displayed");
		ui.clickLink(IcecUI.weekListButton);
		Assert.assertTrue(ui.getElementText("css=span.fc-title").equals(eventName),
				"Week(List) Event name doesn't match expected: " + eventName);

		logger.strongStep("Click Upcoming Button and verify upcoming event is displayed");
		log.info("INFO: Click Upcoming Button and verify upcoming event is displayed");
		ui.clickLink(IcecUI.upcomingButton);
		Assert.assertTrue(ui.getElementText("css=span.fc-list-item-title a").equals(eventName),
				"Upcoming Event name doesn't match expected: " + eventName);

		logger.strongStep("Click Widget Edit Button");
		log.info("INFO: Click Widget Edit Button");
		ui.editWidget(eventName);

		logger.strongStep("Select Week (List) from edit menu");
		log.info("INFO: Select Week (List) from edit menu");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.DISPLAY_SETTINGS.toString());
		ui.selectEventLayoutInEditView("basicWeek");
		ui.saveWidget(eventName);
		
		logger.strongStep("Verify week list view displayed correctly");
		log.info("INFO: Verify week list view displayed correctly");
		driver.executeScript("scroll(0,-400);");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.fc-basicWeek-view"),
				"Week list view not found");

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B> Test the Members widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community with two members and navigate to it.
	*<li><B>Step:</B> Add Highlights widget if not already present.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove the default Members widget.
	*<li><B>Step:</B> Add the Members widget using Hidden Apps tab in the Customize menu.
	*<li><B>Verify:</B> The contact images for both members are displayed in the Members widget.
	*<li><B>Step:</B> Click on the 'View All (2 people)' link in the Members widget.
	*<li><B>Verify:</B> The Members page opens up.
	*<li><B>Verify:</B> The links for both members are visible.
	*<li><B>Step:</B> Navigate back to the Highlights page.
	*<li><B>Step:</B> Hover over the contact images for both members.
	*<li><B>Verify:</B> The business cards for both members are visible.
	*<li><B>Step:</B> Click on the Profile link in the Business Card for one of the members.
	*<li><B>Verify:</B> My Profile page is opened.
	*</ul>
	*/
	@Test(groups = {"level2", "bvt"})
	public void testMembers() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		
		String user1DisplayName = testUser1.getDisplayName();
		String user2DisplayName = testUser2.getDisplayName();

		String testName = ui.startTest();
		String widgetTitle = "CommunityMembers";
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
				   .addMember(new Member(CommunityRole.MEMBERS, testUser2))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser1, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
		
		if (!flag) {
			
			if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty()) {
	        	
				community.addWidgetAPI(comAPI, apiOwner, BaseWidget.HIGHLIGHTS);
				driver.navigate().refresh();
				ui.waitForPageLoaded(driver);

			}
			
			logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
			log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
			Community_TabbedNav_Menu.HIGHLIGHTS.select(ui,2);
			
		}	

		logger.strongStep("Remove the default Members widget");
		log.info("INFO: Remove the default Members widget");
		ui.removeWidget(widgetTitle);

		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");

		logger.strongStep("Add the Members widget using the 'Hidden Apps' section in the Customize Menu");
		log.info("INFO: Add the Members widget using the 'Hidden Apps' section in the Customize Menu");
		ui.clickLink(IcecUI.customizeButton);
		ui.addWidgetsFromHiddenApps(widgetTitle);
		ui.fluentWaitTextNotPresent("has been added to the page.");

		logger.strongStep("Verify that the contact images for both members are displayed in the Members widget");
		log.info("INFO: Verify that the contact images for both members are displayed in the Members widget");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMemberContactImage(widgetTitle, testUser1)),
				"The contact image for " + user1DisplayName + " member found");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMemberContactImage(widgetTitle, testUser2)),
				"The contact image for " + user2DisplayName + " member found");

		logger.strongStep("Click on the 'View All (2 people)' link in the Members widget");
		log.info("INFO: Click on the 'View All (2 people)' link in the Members widget");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.viewAllmembersLink);

		logger.strongStep("Verify that the Members page opens up by verifying the presence of the 'Export Members' button");
		log.info("INFO: Verify that the Members page opens up by verifying the presence of the 'Export Members' button");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.ExportMembersButton),
				"The Members page opened up as the 'Export Members' button is visible");

		logger.strongStep("Verify that the links for " + user1DisplayName + " and " + user2DisplayName + " are visible");
		log.info("INFO: Verify that the links for " + user1DisplayName + " and " + user2DisplayName + " are visible");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + user1DisplayName),
				"The link for " + user1DisplayName + " is visible on the Members page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + user2DisplayName),
				"The link for " + user2DisplayName + " is visible on the Members page");

		logger.strongStep("Navigate back to the Highlights page");
		log.info("INFO: Navigate back to the Highlights page");
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui,2);

		logger.strongStep("Hover over the contact image for " + user1DisplayName);
		log.info("INFO: Hover over the contact image for " + user1DisplayName);
		driver.getFirstElement(IcecUI.getMemberContactImage(widgetTitle, testUser1)).hover();

		logger.strongStep("Verify that the Business Card for " + user1DisplayName + " is visible");
		log.info("INFO: Verify that the Business Card for " + user1DisplayName + " is visible");
		Assert.assertEquals(commUI.getbusinesscard(testUser1), user1DisplayName,
				"The business card for " + user1DisplayName + " is visible");

		logger.strongStep("Click somewhere else in the widget so that the Business Card is no longer visible");
		log.info("INFO: Click somewhere else in the widget so that the Business Card is no longer visible");
		driver.getSingleElement(IcecUI.getWidget(widgetTitle)).clickAt(0, 0);

		logger.strongStep("Hover over the contact image for " + user2DisplayName);
		log.info("INFO: Hover over the contact image for " + user2DisplayName);
		driver.getFirstElement(IcecUI.getMemberContactImage(widgetTitle, testUser2)).hover();

		logger.strongStep("Verify that the Business Card for " + user2DisplayName + " is visible");
		log.info("INFO: Verify that the Business Card for " + user2DisplayName + " is visible");
		Assert.assertEquals(commUI.getbusinesscard(testUser2), user2DisplayName,
				"The business card for " + user2DisplayName + " is visible");

		logger.strongStep("Click on the Profile link in the Business Card for " + user2DisplayName);
		log.info("INFO: Click on the Profile link in the Business Card for " + user2DisplayName);
		ui.clickLinkWait(ProfilesUIOnPrem.VcardProfileLink);

		logger.strongStep("Verify that My Profile page opens");
		log.info("INFO: Verify that My Profile page opens");
		String userUrl = ui.replaceHttpWithHttps(Data.getData().networkUserUrl.replaceAll("SERVER", cfg.getServerURL()));
		Assert.assertTrue(driver.getCurrentUrl().contains(userUrl),
		    	"My Profile opened with correct URL: " + userUrl);

		ui.endTest();
	}

	/**
	*
	*<ul>
	*<li><B>Info:</B> Test the Links widget.
	*<li><B>Step:</B> Create a community then login and navigate to the community.
	*<li><B>Step:</B> Add the Highlights widget to the community if it is not a part of the default layout.
	*<li><B>Step:</B> Navigate to the HIGHLIGHTS tab in the navigation menu.
	*<li><B>Step:</B> Remove the default Links widget.
	*<li><B>Step:</B> Add a new Links widget by using the Customize button.
	*<li><B>Verify:</B> All bookmark links are displayed in widget.
	*<li><B>Step:</B> Edit the Links widget and change the number of items per page.
	*<li><B>Step:</B> Save the changes then page through the links.
	*<li><B>Verify:</B> The content is correct.
	*<li><B>Step:</B> Edit the Links widget and enable search by selecting the Search checkbox.
	*<li><B>Step:</B> Save the changes and search for the link.
	*<li><B>Verify:</B> The link is displayed.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "smokeonprem"})
	public void testLinks() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Bookmark";

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
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

		logger.strongStep("Create bookmarks");
		log.info("INFO: Create bookmarks");
		BaseDogear[] bookmarks = ui.createBookmarks(testUser, serverURL, community, 6);

		logger.strongStep("Remove the default Links widgets");
		log.info("INFO: Remove the default Links widgets");
		ui.removeWidget("Links");
		ui.removeWidget("Important Links");

		logger.strongStep("Add a new Links widget by using the Customize button");
		log.info("INFO: Create a new Links widget by using the Customize button");
		driver.executeScript("window.scrollTo(0, 0)");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.selectType("xccBookmark");
		
		logger.strongStep("Scroll up to the top of the page");
		log.info("INFO: Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");

		logger.strongStep("Check all links are displayed");
		log.info("INFO: Check all links are displayed");
		for(BaseDogear bm : bookmarks) {
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[title='" + bm.getTitle() + "']"),
					"Bookmark with title " + bm.getTitle() + " not found");
		}

		logger.strongStep("Edit the Links widget and change the number of items per page");
		log.info("INFO: Edit the Links widget and change the number of items per page");
		ui.editWidget(widgetTitle);
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
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
				ui.clickLinkWithJavascript(IcecUI.getWidget(widgetTitle) + " a[aria-label='Show next items']");
			}
		}

		logger.strongStep("Edit the Links widget and check the Search checkbox to enable search in widget");
		log.info("INFO: Edit the Links widget and check the Search checkbox to enable search in widget");
		ui.editWidget(widgetTitle);
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.DISPLAY_SETTINGS.toString());
		ui.selectCheckbox(IcecUI.searchCheckbox);

		ui.saveWidget(widgetTitle);

		logger.strongStep("Search for the link and ensure it's displayed");
		log.info("INFO: Search for the link and ensure it's displayed");
		ui.typeText(IcecUI.getWidget(widgetTitle) + " input[rel='search']", bookmarks[3].getTitle());
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[title='" + bookmarks[4].getTitle() + "']"),
				"Bookmark with title " + bookmarks[4].getTitle() + " not found");

		ui.endTest();
	}
	
	/**
	*
	*<ul>
	*<li><B>Info:</B> Test Links widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Links.
	*<li><B>Step:</B> Create Links widget.
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
	*</ul>
	*/
	@Test(groups = {"level2", "bvt"})
	public void testMyLinks() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		Map<String, String> links = new HashMap<String, String>();
		links.put("IBM" + Helper.genDateBasedRand(), "ibm.com" + Helper.genDateBasedRand());
		links.put("Google" + Helper.genDateBasedRand(), "google.com" + Helper.genDateBasedRand());
		links.put("Cisco" + Helper.genDateBasedRand(), "cisco.com" + Helper.genDateBasedRand());

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		logger.strongStep("Add My Links");
		log.info("INFO: Add My Links");
		ui.loadUrlWithHttps(serverURL + "/profiles/html/myProfileView.do");
		for(Map.Entry<String, String> link : links.entrySet()){
			ui.clickLink(ProfilesUIConstants.ProfilesAddLink);
			ui.typeText(ProfilesUIConstants.ProfilesAddLinkName, link.getKey());
			ui.typeText(ProfilesUIConstants.ProfilesAddLinkLinkname, link.getValue());
			ui.clickSaveButton();
		}
		String communityUrl = ui.replaceHttpWithHttps(serverURL + "/communities/service/html/communitystart?" + community.getCommunityUUID());
		ui.loadUrlWithHttps(communityUrl);
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);

		driver.changeImplicitWaits(5);
		logger.strongStep("Remove default My Links widget");
		log.info("INFO: Remove default My Links widget");
		ui.removeWidget("My Links");
		driver.turnOnImplicitWaits();

		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccMyLinks", widgetTitle, "My Links");
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
		ui.deleteMyLinks(serverURL, links);
		ui.endTest();
	}
	
	/**
	*
	*<ul>
	*<li><B>Info:</B> Test My QuickLinks widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Links.
	*<li><B>Step:</B> Create My QuickLinks widget and add links to it.
	*<li><B>Verify:</B> Check all links are displayed in widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testMyQuicklinks() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "My Quicklinks";
		String widgetTitle = "Description-" + communityName;
		
		Map<String, String> links = new HashMap<String, String>();
		links.put("IBM" + Helper.genDateBasedRand(), "ibm.com" + Helper.genDateBasedRand());
		links.put("Google" + Helper.genDateBasedRand(), "google.com" + Helper.genDateBasedRand());
		links.put("Cisco" + Helper.genDateBasedRand(), "cisco.com" + Helper.genDateBasedRand());

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Click on Highlights page");
		log.info("INFO: Click on Highlights page");
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		logger.strongStep("Remove default My Quicklinks widget");
		log.info("INFO: Remove default My Quicklinks widget");
		driver.changeImplicitWaits(5);
		ui.removeWidget(widgetType);
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Create " + widgetType + " widget");
		log.info("INFO: Create " + widgetType + " widget");
		ui.createHighlightsWidget("xccMyQuicklinks", widgetTitle, widgetType);
		
		log.info("INFO: Scroll down to the bottom of the page");
		driver.executeScript("scroll(0,+250);");
		
		logger.strongStep("Add links in " + widgetType + " widget");
		log.info("INFO: Add links in " + widgetType + " widget");
		for(Map.Entry<String, String> link : links.entrySet()) {
			ui.clickLinkWithJavascript("css=div.xccMyQuicklinks button.createBookmark");
			ui.typeText("css=input[name='bookmarkTitle']", link.getKey());
			ui.typeText("css=input.bookmarkUrl", link.getValue());
			ui.clickLink(IcecUI.widgetCreateButton);
		}
		
		logger.strongStep("Check all Links are displayed");
		log.info("INFO: Check all Links are displayed");
		for(Map.Entry<String, String> link : links.entrySet()) {
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a:contains(" + link.getKey() + ")"),
					"Link with title " + link.getKey() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getWidget(widgetTitle) + " a[href*='" + link.getValue() + "']"),
					"Link " + link.getValue() + " not found");
		}

		ui.endTest();
	}

	/**
	 *
	*<ul>
	*<li><B>Info:</B> Test Important Links widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Important Links.
	**<li><B>Step:</B> Create Important Links widget.
	*<li><B>Verify:</B> Check all bookmark links are displayed in widget.
	*<li><B>Step:</B> Click edit view.
	*<li><B>Step:</B> Change number of items per page.
	*<li><B>Step:</B> Save.
	*<li><B>Verify:</B> Page through links and check content.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testImportantLinks() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "Bookmark List - Important";
		String widgetTitle = "Description-" + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseDogear[] bookmarks = ui.createBookmarks(testUser, serverURL, community, 6);
		
		logger.strongStep("Re-loading community and Click on Highlights page");
		log.info("INFO: Re-loading community and Click on Highlights page");
		community.navViaUUID(commUI);		
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		logger.strongStep("Remove default Important Links widget");
		log.info("INFO: Remove default Important Links widget");
		driver.changeImplicitWaits(5);
		ui.removeWidget(widgetType);
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Create " + widgetType + " widget");
		log.info("INFO: Create " + widgetType + " widget");
		ui.createHighlightsWidget("xccImportantBookmark", widgetTitle, widgetType);
		
		log.info("INFO: Scroll down to the bottom of the page");
		driver.executeScript("scroll(0,+400);");

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
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
		ui.clearAndTypeText(IcecUI.numberOfItemsPerPage, "1");
		ui.saveWidget(widgetTitle);
		driver.executeScript("scroll(0,+400);");

		logger.strongStep("Page through and check links");
		log.info("INFO: Page through and check links");
		for(int i = 0, j = 0; i < bookmarks.length; i++) {
			if(i % 2 == 0) {
				j++;
				Assert.assertTrue(ui.fluentWaitPresent("css=div.xccImportantBookmark a[href='" + bookmarks[i].getURL() + "']"),
						"Bookmark with title " + bookmarks[i].getTitle() + " not found");
				Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.lotusLeft").equals("Viewing " + j + "-" + j +" of 3 links"),
						"Paging text doesn't match expected: " + "Viewing " + j + "-" + j +" of 3 links");
				if(j<3) ui.clickLinkWithJavascript(IcecUI.getWidget(widgetTitle) + " a[aria-label='Show next items']");
			}
		}
		ui.endTest();
	}

	/**
	*
	*<ul>
	*<li><B>Info:</B> Test the Forums widget.
	*<li><B>Step:</B> Create a community then login and navigate to the community.
	*<li><B>Step:</B> Add the Highlights widget to the community if it is not a part of the default layout.
	*<li><B>Step:</B> Create multiple forum topics.
	*<li><B>Step:</B> Remove the default Forum widget.
	*<li><B>Step:</B> Add a new Forum widget by using the Customize button.
	*<li><B>Verify:</B> All forum topics are displayed in widget.
	*<li><B>Step:</B> Click on a forum topic.
	*<li><B>Verify:</B> The forum topic popup for the topic appears as expected.
	*<li><B>Step:</B> Click on the Like button.
	*<li><B>Verify:</B> The 'Like' button has changed to 'Unlike' and the number of likes is 1.
	*<li><B>Step:</B> Click on the 'Unlike' button.
	*<li><B>Verify:</B> The 'Like' is visible and number of likes is 0.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"level2", "bvt"})
	public void testForums() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

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

		logger.strongStep("Create forum topics");
		log.info("INFO: Create forum topics");
		BaseForumTopic[] topics = ui.createForumTopics(testUser, serverURL, community, 6);

		logger.strongStep("Remove the default Forums widget");
		log.info("INFO: Remove the default Forums widget");
		ui.removeWidget("topicList");

		logger.strongStep("Add a new Forum widget by using the Customize button");
		log.info("INFO: Create a new Forum widget by using the Customize button");
		driver.executeScript("scroll(0,-250);");
		ui.clickLink(IcecUI.customizeButton);
		ui.selectType("xccForum");

		for(int i = 0; i < topics.length; i++) {
			BaseForumTopic topic = topics[i];
			logger.strongStep("Check the forum topic " + topic.getTitle() + " appears as expected");
			log.info("INFO: Check the forum topic " + topic.getTitle() + " appears as expected");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getForumTopicLink(serverURL, topic)),
					"Topic with title " + topic.getTitle() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getForumTopicDesc(serverURL, topic)),
					"Topic description " + topic.getDescription() + " not found");
		}

		logger.strongStep("Click on the forum topic: " + topics[0]);
		log.info("INFO: Click on the forum topic: " + topics[0]);
		ui.clickLinkWithJavascript(IcecUI.getForumTopicLink(serverURL, topics[0]));

		logger.strongStep("Check the forum topic popup for " + topics[0].getTitle() + " appears as expected");
		log.info("INFO: Check the forum topic popup for " + topics[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Topic popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getTopicFlyoutTitle(serverURL, topics[0])).equals(topics[0].getTitle()),
				"Topic popup title doesn't equal: " + topics[0].getTitle() + ". Actual: " + ui.getElementText(IcecUI.getTopicFlyoutTitle(serverURL, topics[0])));

		logger.strongStep("Click on the 'Like' button");
		log.info("INFO: Click on the 'Like' button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Verify that the 'Like' button has changed to 'Unlike' and the number of likes is 1");
		log.info("INFO: Check 'Unlike' button is visible and number of likes is 1");
		ui.fluentWaitTextPresent("Unlike");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Unlike"),
				"'Like' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("1"),
				"'Like' text is not equal to '1'");

		logger.strongStep("Click on the 'Unlike' button");
		log.info("INFO: Click on the 'Unlike' button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Verify that the 'Like' button is visible and number of likes is 0");
		log.info("INFO: Verify that the 'Like' button is visible and number of likes is 0");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Like"),
				"'Unlike' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("0"),
				"'Like' text is not equal to '0'");

		ui.endTest();
	}

	/**
	* <ul>
	*<li><B>Info:</B> Test Ideation Blogs widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Ideation Blogs widget.
	*<li><B>Step:</B> Create Ideation Blogs widget.
	*<li><B>Verify:</B> Check all ideas are displayed in widget.
	*<li><B>Step:</B> Click Vote.
	*<li><B>Verify:</B> Check 'Unvote' is displayed and number of votes is 1.
	*<li><B>Step:</B> Click Unvote.
	*<li><B>Verify:</B> Check 'Vote' is displayed and number of votes is 0.
	*<li><B>Step:</B> Click idea.
	*<li><B>Verify:</B> Check idea popup appears as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testIdeationBlogs() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseBlogPost[] ideas = ui.createIdeas(testUser, serverURL, comAPI, community, 4);

		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		driver.changeImplicitWaits(5);
		logger.strongStep("Remove default Ideation Blog widget");
		log.info("INFO: Remove default Ideation Blog widget");
		ui.removeWidget("Ideation Blog");
		driver.turnOnImplicitWaits();
		
		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccIdeationBlog", widgetTitle, "Ideation Blog");
		
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

		ui.endTest();
	}

	/**
	* <ul>
	*<li><B>Info:</B> Test Static Content Blogs widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Blog post and edit that.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create a Static Content Blog widget .
	*<li><B>Step:</B> Edit widget .
	*<li><B>Step:</B> Select Latest .
	*<li><B>Step:</B> Save widget .
	*<li><B>Verify:</B> Check post is displayed in widget.
	*<li><B>Step:</B> Edit widget .
	*<li><B>Step:</B> Select specific blog post .
	*<li><B>Step:</B> Save widget .
	*<li><B>Verify:</B> Check post is displayed in widget.
	*</ul>
	*/
	//Disabling test as Blog post is not visible on 'static content blog widget'. The defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10696 has been raised for this.
	@Test(groups = {"regression"}, enabled=false)
	public void testStaticContentBlog() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "Static Content - Blog";
		String widgetTitle = "Description-" + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 1);

		logger.strongStep("Re-loading community and edit Blog Post");
		log.info("INFO: Re-loading community and edit Blog Post");
		community.navViaUUID(commUI);
		ui.clickLink(IcecUI.getWidgetTile(blogPosts[0].getTitle()));
		ui.clickLink(IcecUI.editBlogPostLink);
		ui.clickLink(IcecUI.postBtn);

		logger.strongStep("Navigate to Highlights page");
		log.info("INFO: Navigate to Highlights page");
		ui.clickLinkWait(CommunitiesUIConstants.communityHighlights);
		
		logger.strongStep("Create " + widgetType + " widget");
		log.info("INFO: Create " + widgetType + " widget");
		ui.createHighlightsWidget("xccStaticContentBlog", widgetTitle, widgetType);
		
		logger.strongStep("Edit widget and click on 'Content Sources' dropdown");
		log.info("INFO: Edit widget and click on 'Content Sources' dropdown");
		driver.executeScript("scroll(0,-250);");
		ui.editWidget(widgetTitle);
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
		
		logger.strongStep("Click Latest and Save");
		log.info("INFO: Click Latest and Save");
		ui.clickLink(IcecUI.latestBtn);
		ui.saveWidget(widgetTitle);

		logger.strongStep("Verify blog post is displayed in widget");
		log.info("INFO: Verify blog post is displayed in widget");
		Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog").equals(blogPosts[0].getContent()),
				"Blog entry doesn't equal: " + blogPosts[0].getContent() + ". Actual: " + ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog"));

		logger.strongStep("Edit widget and click on 'Content Sources' dropdown");
		log.info("INFO: Edit widget and click on 'Content Sources' dropdown");
		ui.editWidget(widgetTitle);
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
		
		logger.strongStep("Select specific blog post and Save");
		log.info("INFO: Select specific blog post and Save");
		ui.clickLink(IcecUI.selectedBtn);
		ui.scrollIntoViewElement(IcecUI.plusBtn);
		ui.clickLink(IcecUI.plusBtn);
		ui.typeText(IcecUI.selectPostTextField, blogPosts[0].getTitle());
		ui.clickLink(IcecUI.getSelectedBlogEntry("Search Latest Additions"));
		ui.clickLink(IcecUI.getSelectedBlogEntry(blogPosts[0].getTitle()));
		ui.saveWidget(widgetTitle);

		logger.strongStep("Verify blog post is displayed in widget");
		log.info("INFO: Verify blog post is displayed in widget");
		Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog").equals(blogPosts[0].getContent()),
				"Blog entry doesn't equal: " + blogPosts[0].getContent() + ". Actual: " + ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentBlog"));

		ui.endTest();
	}

	/**
	* <ul>
	*<li><B>Info:</B> Test News Overview widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Blog post.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create News Overview widget.
	*<li><B>Verify:</B> Check all blog posts and images are displayed in widget.
	*<li><B>Step:</B> Click blog post.
	*<li><B>Verify:</B> Check blog post popup appears as expected.
	*<li><B>Step:</B> Click like.
	*<li><B>Verify:</B> Check 'Unlike' is displayed and number of likes is 1.
	*<li><B>Step:</B> Click Unlike.
	*<li><B>Verify:</B> Check 'Like' is displayed and number of likes is 0.
	*</ul>
	*/
	@Test(groups = {"regression", "bvt"})
	public void testNewsOverview() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "News - Overview";
		String widgetTitle = "Description-" + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 6);

		logger.strongStep("Re-loading community");
		log.info("INFO: Re-loading community");
		community.navViaUUID(commUI);
		
		logger.strongStep("Remove default News Overview widget");
		log.info("INFO: Remove default News Overview widget");
		ui.removeWidget(widgetType);

		logger.strongStep("Create News Overview widget");
		log.info("INFO: Create News Overview widget");
		ui.createHighlightsWidget("xccNewsOverview", widgetTitle, widgetType);
		
		logger.strongStep("Verify all blog posts and images are displayed in widget");
		log.info("INFO: Verify all blog posts and images are displayed in widget");
		for(int i = 0; i < blogPosts.length; i++) {
			BaseBlogPost blogPost = blogPosts[i];
			logger.strongStep("Check Blog post " + blogPost.getTitle() + " appears as expected");
			log.info("INFO: Check Blog post " + blogPost.getTitle() + " appears as expected");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getNewsOverviewLink(blogPost)),
					"Blog post with title " + blogPost.getTitle() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getNewsOverviewContent(blogPost)),
					"Blog post content " + blogPost.getContent() + " not found");
			Assert.assertTrue(ui.fluentWaitPresent("css=div[data-id='post0" + i + "'] img.newsOverviewImage"),
					"Blog post image for " + blogPost.getTitle() + " not found");
		}

		logger.strongStep("Click Blog post: " + blogPosts[0]);
		log.info("INFO: Click Blog post: " + blogPosts[0]);
		ui.clickLinkWithJavascript(IcecUI.getNewsOverviewLink(blogPosts[0]));

		logger.strongStep("Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		log.info("INFO: Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Blog post popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getBlogFlyoutTitle(blogPosts[0])).equals(blogPosts[0].getTitle()),
				"Blog post popup title doesn't equal: " + blogPosts[0].getTitle() + ". Actual: " + IcecUI.getBlogFlyoutTitle(blogPosts[0]));

		logger.strongStep("Click like button");
		log.info("INFO: Click like button");
		ui.clickLinkWithJavascript(IcecUI.likeButton);

		logger.strongStep("Check 'Unlike' is displayed and number of likes is 1");
		log.info("INFO: Check 'Unlike' is displayed and number of likes is 1");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Unlike"),
				"'Like' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("1"),
				"'Like' text is not equal to '1'");

		logger.strongStep("Click like button");
		log.info("INFO: Click like button");
		ui.clickLinkWithJavascript(IcecUI.likeButton);

		logger.strongStep("Check 'Like' is displayed and number of likes is 0");
		log.info("INFO: Check 'Like' is displayed and number of likes is 0");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Like"),
				"'Unlike' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("0"),
				"'Like' text is not equal to '0'");

		ui.endTest();
	}

	/**
	*
	*<ul>
	*<li><B>Info:</B> Test the News widget.
	*<li><B>Step:</B> Create a community then login and navigate to the community.
	*<li><B>Step:</B> Add the Highlights widget to the community if it is not a part of the default layout.
	*<li><B>Step:</B> Create multiple blog posts.
	*<li><B>Step:</B> Add a new News widget by using the Customize button.
	*<li><B>Verify:</B> All blog posts are displayed in widget.
	*<li><B>Verify:</B> The content of the blog post is accurate.
	*<li><B>Step:</B> Edit the News widget and select the 'Likes and Comments' and Allow checkboxes.
	*<li><B>Verify:</B> The 'Like Image', 'Likes Label', 'Comments Image', 'Comments Label' and 'Create News' links are visible.
	*<li><B>Step:</B> Click on a Blog post.
	*<li><B>Verify:</B> The Blog post popup for the clicked Blog post appears as expected.
	*<li><B>Step:</B> Click on the 'Like' button.
	*<li><B>Verify:</B> The 'Like' button has changed to 'Unlike' and the number of likes is 1.
	*<li><B>Step:</B> Click on the 'Unlike' button.
	*<li><B>Verify:</B> The 'Like' is visible and number of likes is 0.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"level2", "bvt"})
	public void testNews() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "News";

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

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

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 6);

		logger.strongStep("Add a new News widget by using the Customize button");
		log.info("INFO: Create a new News widget by using the Customize button");
		driver.executeScript("scroll(0,-250);");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.selectType("xccNews");
		ui.fluentWaitTextNotPresent("has been created.");

		for(int i = 0; i < blogPosts.length; i++) {
			BaseBlogPost blogPost = blogPosts[i];
			logger.strongStep("Check the Blog post " + blogPost.getTitle() + " appears as expected");
			log.info("INFO: Check the Blog post " + blogPost.getTitle() + " appears as expected");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getNewsLink(blogPost)),
					"Blog post with title " + blogPost.getTitle() + " not found");
		}

		logger.strongStep("Verify the content of the blog post");
		log.info("INFO: Verify the content of the blog post");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccNews div.xccTeaser div.newsContent:contains(Test description for BlogPost)"),
				"Blog post content not found");

		logger.strongStep("Edit the News widget and select the 'Likes and Comments' and Allow checkboxes");
		log.info("INFO: Edit the News widget and select the 'Likes and Comments' and Allow checkboxes");
		ui.editWidget(widgetTitle);
		driver.executeScript("scroll(0,-50);");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.SOCIAL_AND_CONTENT_CREATION.toString());
		ui.selectCheckbox(IcecUI.likesAndCommentsCheckbox);
		driver.executeScript("scroll(0,-50);");
		ui.selectCheckbox(IcecUI.allowContentCreationCheckbox);
		ui.saveWidget(widgetTitle);

		logger.strongStep("Verify the 'Like Image', 'Likes Label', 'Comments Image', 'Comments Label' and 'Create News' links are visible");
		log.info("INFO: Verify the 'Like Image', 'Likes Label', 'Comments Image', 'Comments Label' and 'Create News' links are visible");
		String[] selectorsToCheck = {"css=div.xccEntry span.likesImage", "css=div.xccEntry span.likesLabel",
										"css=div.xccEntry span.commentsImage", "css=div.xccEntry span.commentsLabel", "css=span.xccCreateNews a.xccMoreNewsLink"};
		ui.checkElementsArePresent(selectorsToCheck);
		
		Element blogPostElement = driver.getSingleElement(IcecUI.getNewsLink(blogPosts[0]));
		driver.executeScript("arguments[0].scrollIntoView(true);", blogPostElement.getWebElement());
		logger.strongStep("Click on the Blog post: " + blogPosts[0]);
		log.info("INFO: Click on the Blog post: " + blogPosts[0]);
		ui.clickLink(IcecUI.getNewsLink(blogPosts[0]));

		logger.strongStep("Verify the Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		log.info("INFO: Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Blog post popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getBlogFlyoutTitle(blogPosts[0])).equals(blogPosts[0].getTitle()),
				"Blog post popup title doesn't equal: " + blogPosts[0].getTitle() + ". Actual: " + IcecUI.getBlogFlyoutTitle(blogPosts[0]));

		logger.strongStep("Click on the 'Like' button");
		log.info("INFO: Click on the 'Like' button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Verify that the 'Like' button has changed to 'Unlike' and the number of likes is 1");
		log.info("INFO: Check 'Unlike' button is visible and number of likes is 1");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Unlike"),
				"'Like' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("1"),
				"'Like' text is not equal to '1'");

		logger.strongStep("Click on the 'Unlike' button");
		log.info("INFO: Click on the 'Unlike' button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Verify that the 'Like' button is visible and number of likes is 0");
		log.info("INFO: Verify that the 'Like' button is visible and number of likes is 0");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Like"),
				"'Unlike' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("0"),
				"'Like' text is not equal to '0'");

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test News List widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Blog Post via API.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create News List widget.
	*<li><B>Verify:</B> Check all blog posts are displayed in widget.
	*<li><B>Step:</B> Click blog post.
	*<li><B>Verify:</B> Check blog post popup appears as expected.
	*<li><B>Step:</B> Click like.
	*<li><B>Verify:</B> Check 'Unlike' is displayed and number of likes is 1.
	*<li><B>Step:</B> Click Unlike.
	*<li><B>Verify:</B> Check 'Like' is displayed and number of likes is 0.
	*</ul>
	*/
	@Test(groups = {"regression", "bvt"})
	public void testNewsList() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "News - List";
		String widgetTitle = "Description-" + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 3);

		logger.strongStep("Re-loading community and Click on Highlights page");
		log.info("INFO: Re-loading community and Click on Highlights page");
		community.navViaUUID(commUI);		
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		logger.strongStep("Remove default News List widget");
		log.info("INFO: Remove default News List widget");
		driver.changeImplicitWaits(5);
		ui.removeWidget(widgetType);
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Create " + widgetType + " widget");
		log.info("INFO: Create " + widgetType + " widget");
		ui.createHighlightsWidget("xccNewsList", widgetTitle, widgetType);
		driver.executeScript("scroll(0,+400);");
		
		for(int i = 0; i < blogPosts.length; i++) {
			BaseBlogPost blogPost = blogPosts[i];
			logger.strongStep("Check Blog post " + blogPost.getTitle() + " appears as expected");
			log.info("INFO: Check Blog post " + blogPost.getTitle() + " appears as expected");
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getNewsListLink(blogPost)),
					"Blog post with title " + blogPost.getTitle() + " not found");
		}

		logger.strongStep("Click Blog post: " + blogPosts[0]);
		log.info("INFO: Click Blog post: " + blogPosts[0]);
		ui.clickLinkWithJavascript(IcecUI.getNewsListLink(blogPosts[0]));

		logger.strongStep("Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		log.info("INFO: Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Blog post popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getBlogFlyoutTitle(blogPosts[0])).equals(blogPosts[0].getTitle()),
				"Blog post popup title doesn't equal: " + blogPosts[0].getTitle() + ". Actual: " + IcecUI.getBlogFlyoutTitle(blogPosts[0]));

		logger.strongStep("Click like button");
		log.info("INFO: Click like button");
		ui.clickLinkWithJavascript(IcecUI.likeButton);

		logger.strongStep("Check 'Unlike' is displayed and number of likes is 1");
		log.info("INFO: Check 'Unlike' is displayed and number of likes is 1");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Unlike"),
				"'Like' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("1"),
				"'Like' text is not equal to '1'");

		logger.strongStep("Click unlike button");
		log.info("INFO: Click unlike button");
		ui.clickLink(IcecUI.likeButton);

		logger.strongStep("Check 'Like' is displayed and number of likes is 0");
		log.info("INFO: Check 'Like' is displayed and number of likes is 0");
		Assert.assertTrue(ui.getElementText(IcecUI.likeButton).equals("Like"),
				"'Unlike' unsuccessful");
		Assert.assertTrue(ui.getElementText(IcecUI.noOfLikesLabel).equals("0"),
				"'Like' text is not equal to '0'");

		ui.endTest();
	}

	/**
	* <ul>
	*<li><B>Info:</B> Test News Slider widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create News Slider widget.
	*<li><B>Verify:</B> Check all blog posts are displayed in widget.
	*<li><B>Step:</B> Click blog post.
	*<li><B>Verify:</B> Check blog post popup appears as expected.
	*<li><B>Step:</B> Click like.
	*<li><B>Verify:</B> Check 'Unlike' is displayed and number of likes is 1.
	*<li><B>Step:</B> Click Unlike.
	*<li><B>Verify:</B> Check 'Like' is displayed and number of likes is 0.
	*<li><B>Step:</B> Edit and enable allow creation and autoslide.
	*<li><B>Verify:</B> Check link to create more posts is visible.
	*<li><B>Verify:</B> Check auto slide is functioning by checking the default post is not displayed.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testNewsSlider() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 6);

		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		driver.changeImplicitWaits(5);
		logger.strongStep("Remove default News Slider widget");
		log.info("INFO: Remove default News Slider widget");
		ui.removeWidget("News - Slider");
		driver.turnOnImplicitWaits();
		
		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccNewsSlider", widgetTitle,"News - Slider");
		
		for(int i = 0; i < blogPosts.length; i++) {
			ui.clickLink(IcecUI.previousButton);
			BaseBlogPost blogPost = blogPosts[i];
			logger.strongStep("Check Blog post " + blogPost.getTitle() + " appears as expected");
			log.info("INFO: Check Blog post " + blogPost.getTitle() + " appears as expected");
			Assert.assertTrue(ui.getElementText(IcecUI.currentSliderPostTitle).equals(blogPosts[i].getTitle()),
					"Blog post title doesn't equal: " + blogPosts[i].getTitle() + ". Actual: " + ui.getElementText(IcecUI.currentSliderPostTitle));
			Assert.assertTrue(ui.getElementText(IcecUI.currentSliderPostContent).equals(blogPosts[i].getContent()),
					"Blog post content doesn't equal: " + blogPosts[i].getContent() + ". Actual: " + ui.getElementText(IcecUI.currentSliderPostContent));
			Assert.assertTrue(ui.fluentWaitPresent("css=div.xccNewsSlider div.slick-current img"),
					"Blog post image for " + blogPost.getTitle() + " not found");
		}
		
		ui.clickLink(IcecUI.previousButton);
		logger.strongStep("Click Blog post: " + blogPosts[0].getTitle());
		log.info("INFO: Click Blog post: " + blogPosts[0].getTitle());
		driver.executeScript("scroll(0,-250);");
		ui.clickLink("css=div.xccNewsSlider div.slick-current h3 a");

		logger.strongStep("Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		log.info("INFO: Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Blog post popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getBlogFlyoutTitle(blogPosts[0])).equals(blogPosts[0].getTitle()),
				"Blog post popup title doesn't equal: " + blogPosts[0].getTitle() + ". Actual: " + IcecUI.getBlogFlyoutTitle(blogPosts[0]));

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
		ui.clickLink("css=button.lotusClose");

		logger.strongStep("Edit and enable allow creation and autoslide");
		log.info("INFO: Edit and enable allow creation and autoslide");
		ui.editWidget(widgetTitle);
		driver.executeScript("scroll(0,-50);");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.SOCIAL_AND_CONTENT_CREATION.toString());
		ui.selectCheckbox(IcecUI.allowContentCreationCheckbox);
		driver.executeScript("scroll(0,-50);");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.DISPLAY_SETTINGS.toString());
		ui.selectCheckbox(IcecUI.autoSlideCheckbox);
		ui.clearAndTypeText(IcecUI.slidingSpeedTextField, "500");
		ui.saveWidget(widgetTitle);

		logger.strongStep("Check link to create more posts is visible");
		log.info("INFO: Check link to create more posts is visible");
		Assert.assertTrue(ui.fluentWaitPresent("css=span.xccCreateNews a.xccMoreNewsLink"),
				"Create more posts link not found");

		logger.strongStep("Check auto slide is functioning by checking the default post is not displayed");
		log.info("INFO: Check auto slide is functioning by checking the default post is not displayed");
		Assert.assertFalse(ui.getElementText(IcecUI.currentSliderPostTitle).equals(blogPosts[blogPosts.length - 1].getTitle()),
					"Blog post popup title doesn't equal: " + blogPosts[blogPosts.length - 1].getTitle() + ". Actual: " + ui.getElementText(IcecUI.currentSliderPostTitle));

		ui.endTest();
	}

	/**
	* <ul>
	*<li><B>Info:</B> Test News Channel widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default News Channel widget.
	*<li><B>Step:</B> Create News Channel widget.
	*<li><B>Step:</B> Edit News Channel widget.
	*<li><B>Verify:</B> Verify option to add additional channel sources.
	*<li><B>Verify:</B> Check all blog posts are displayed in widget.
	*<li><B>Step:</B> Click blog post.
	*<li><B>Verify:</B> Check blog post popup appears as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testNewsChannel() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 6);
		
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		logger.strongStep("Remove default News Channel widget");
		log.info("INFO: Remove default News Channel widget");

		driver.executeScript("scroll(0,-50);");
		if(driver.isElementPresent("css=div[data-wname='NewsChannel'] div.wedit button")){
			ui.clickLink("css=div[data-wname='NewsChannel'] div.wedit button");
			ui.clickLink(IcecUI.getWidgetEditRemove);
		}
		
		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccNewsChannel", widgetTitle, "News");
		
		logger.strongStep("Edit News Channel widget");
		log.info("INFO: Edit News Channel widget");
		ui.editWidget(widgetTitle);
		
		logger.strongStep("Verify option to add additional channel sources");
		log.info("INFO: Verify option to add additional channel sources");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
		ui.clickLink("css=button.btn:contains(Add Channel)");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.ec-title span:contains(Channel 2)"),
				"Channel 2 isn't displayed");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.ec-channel:nth-child(2) label.glue-prev input"),
				"Channel 2 input source not displayed");
		ui.cancelWidget(widgetTitle);
		ui.clickLink("css=button.bx--btn--secondary:contains(Discard Changes)");
		driver.executeScript("scroll(0, -50);");
		
		for(int i = 0; i < blogPosts.length; i++) {
			ui.clickLink(IcecUI.getChannelPreviousLink(1));
			BaseBlogPost blogPost = blogPosts[i];
			logger.strongStep("Check Blog post " + blogPost.getTitle() + " appears as expected");
			log.info("INFO: Check Blog post " + blogPost.getTitle() + " appears as expected");
			Assert.assertTrue(ui.getElementText(IcecUI.getCurrentChannelPostTitle(1)).equals(blogPosts[i].getTitle()),
					"Blog post popup title doesn't equal: " + blogPosts[i].getTitle() + ". Actual: " + ui.getElementText(IcecUI.getCurrentChannelPostTitle(1)));
			Assert.assertTrue(ui.getElementText(IcecUI.getCurrentChannelPostContent(1)).equals(blogPosts[i].getContent()),
					"Blog post popup title doesn't equal: " + blogPosts[i].getContent() + ". Actual: " + ui.getElementText(IcecUI.getCurrentChannelPostContent(1)));
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getCurrentChannelImage(1)),
					"Blog post image for " + blogPost.getTitle() + " not found");
		}

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Static Content Wikis widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create wiki page and Edit that.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create Static Content Wikis widget.
	*<li><B>Step:</B> Edit widget .
	*<li><B>Step:</B> Select Latest .
	*<li><B>Step:</B> Save widget .
	*<li><B>Verify:</B> Check post is displayed in widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testStaticContentWiki() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetType = "Static Content - Wiki";

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseWikiPage[] wikiPages = createWikiPages(testUser, comAPI, community, 1);

		logger.strongStep("Re-loading community");
		log.info("INFO: Re-loading community");
		community.navViaUUID(commUI);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Edit the wiki page");
		log.info("INFO: Edit the wiki page");
		ui.fluentWaitElementVisible(IcecUI.getOverviewWikiLink(wikiPages[0].getName()));
		ui.scrollIntoViewElement(IcecUI.getOverviewWikiLink(wikiPages[0].getName()));
		ui.clickLinkWithJavascript(IcecUI.getOverviewWikiLink(wikiPages[0].getName()));
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(IcecUI.wikiEditButton);
		ui.clickLinkWait(IcecUI.wikiEditButton);
		ui.clickLinkWait(IcecUI.wikiEditSaveClose);
		
		logger.strongStep("Navigate to highlights page");
		log.info("INFO: Navigate to highlights page");
		ui.clickLinkWait(CommunitiesUIConstants.communityHighlights);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Create 'Static Content-Wiki' widget");
		log.info("INFO: Create 'Static Content-Wiki' widget");
		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccStaticContentWiki", widgetTitle, widgetType);
		
		logger.strongStep("Edit widget and click on 'Content Sources' dropdown");
		log.info("INFO: Edit widget and click on 'Content Sources' dropdown");
		ui.editWidget(widgetTitle);
		driver.executeScript("scroll(0,-250);");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
		
		logger.strongStep("Click Latest and Save");
		log.info("INFO: Click Latest and Save");
		ui.scrollIntoViewElement(IcecUI.latestBtn);
		ui.clickLink(IcecUI.latestBtn);
		ui.saveWidget(widgetTitle);

		logger.strongStep("Verify wiki description is displayed in widget");
		log.info("INFO: Verify wiki description is displayed in widget");
		Assert.assertTrue(ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentWiki").equals(wikiPages[0].getDescription()),
				"Wiki entry doesn't equal: " + wikiPages[0].getDescription() + ". Actual: " + ui.getElementText(IcecUI.getWidget(widgetTitle) + " div.xccStaticContentWiki"));

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Navigation widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Wiki Pages and child pages.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create Navigation widget.
	*<li><B>Verify:</B> Verify navigation sub wiki pages appear as expected to 3rd level.
	*</ul>
	*/
	//Disabling this test as the widget 'Navigation' is unavailable in the highlight widget list on cnx-robotize server.
	//Once 'Navigation' widget appears in the list, will enable the same
	@Test(groups = {"regression"}, enabled=false)
	public void testNavigation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Description-" + communityName;
		String widgetType = "Navigation";

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseWikiPage[] wikiPages = createWikiPages(testUser, comAPI, community, 2);
		
		logger.strongStep("Re-load community and Navigate to Wiki page");
		log.info("INFO: Re-load community and Navigate to Wiki page");
		community.navViaUUID(commUI);
		Community_TabbedNav_Menu.WIKI.select(ui);
		
		logger.strongStep("Create Wiki page and child page");
		log.info("INFO: Create Wiki page and child page");
		driver.executeScript("scroll(0,+250);");
		for(BaseWikiPage wikiPage : wikiPages){
			BaseWikiPage childPage = new BaseWikiPage.Builder("Child for " + wikiPage.getName(), PageType.Child)
					 .tags(Data.getData().TagForWikiPages)
					 .description(Data.getData().ChildPageDescription)
					 .build();
			ui.clickLink(IcecUI.getWikiPageLink(wikiPage.getName()));
			ui.clickLink(IcecUI.wikiEditButton);
			ui.clickLink(IcecUI.wikiEditSaveClose);
			childPage.create(wikisUi);
		}
		ui.clickLink(IcecUI.getWikiPageLink("Child for " + wikiPages[0].getName()));
		BaseWikiPage childPage = new BaseWikiPage.Builder("Another Child for " + wikiPages[0].getName(), PageType.Child)
				 .tags(Data.getData().TagForWikiPages)
				 .description(Data.getData().ChildPageDescription)
				 .build();
		childPage.create(wikisUi);
		
		logger.strongStep("Navigate to highlights page");
		log.info("INFO: Navigate to highlights page");
		ui.clickLinkWait(CommunitiesUIConstants.communityHighlights);

		logger.strongStep("Create 'Navigation' widget");
		log.info("INFO: Create 'Navigation' widget");
		ui.createHighlightsWidget("xccNavigationWidget", widgetTitle, widgetType);
		
		logger.strongStep("Verify navigation sub wikis appear as expected to 3rd level");
		log.info("INFO: Verify navigation sub wikis appear as expected to 3rd level");
		driver.executeAsyncScript("window.scrollTo(0, document.body.scrollHeight)");
		driver.getSingleElement("//a/span[text()='" + wikiPages[0].getName() + "']").hover();
		Assert.assertTrue(ui.fluentWaitPresent("//a/span[text()='Child for " + wikiPages[0].getName() + "']"),
				"Child for " + wikiPages[0].getName() + " not displayed");
		driver.getSingleElement("//a/span[text()='Child for " + wikiPages[0].getName() + "']").hover();
		Assert.assertTrue(ui.fluentWaitPresent("//a/span[text()='Another Child for " + wikiPages[0].getName() + "']"),
				"Another Child for " + wikiPages[0].getName() + " not displayed");
		driver.getSingleElement("//a/span[text()='Another Child for " + wikiPages[0].getName() + "']").hover();
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test the Activity Stream widget.
	*<li><B>Step:</B> Create a community then login and navigate to the community.
	*<li><B>Step:</B> Add the Highlights widget to the community if it is not a part of the default layout.
	*<li><B>Step:</B> Navigate to the HIGHLIGHTS tab in the navigation menu.
	*<li><B>Step:</B> Create a new 'Activity Stream' widget.
	*<li><B>Verify:</B> The Activity Stream entries appear as expected.
	*</ul>
	*/
	@Test(groups = {"level2", "bvt"})
	public void testActivityStream() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
	  
		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				   .access(Access.PUBLIC)
				   .description(description)
				   .build();
		
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, null, null);
		communitiesToDelete.add(comAPI);

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

		logger.strongStep("Add a new 'Activity Stream' widget by using the Customize button");
		log.info("INFO: Create a new 'Activity Stream' widget by using the Customize button");
		ui.clickLinkWithJavascript(IcecUI.customizeButton);
		ui.selectType("xccASWidget");

		logger.strongStep("Verify Activity Stream entry appears as expected");
		log.info("INFO: Verify Activity Stream entry appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent("css=li.normalPostEntryItem:nth-child(1) img[alt='Photo of " + testUser.getDisplayName() + "']"),
				"Profile image for " + testUser.getDisplayName() + " not displayed");
		
		Assert.assertTrue(ui.fluentWaitPresent("xpath=//li[@class='postObject-inside-eventTitle'][text()=' created a community named ']/span/a[text()='"+testUser.getDisplayName()+"']/../../a[text()='" + communityName + "']"),
				"Community link for " + communityName + " not displayed");

		Assert.assertTrue(ui.fluentWaitPresent("xpath=//ul/li[@class='normalPostEntryItem']//li[contains(text(),'created a community named')]/../../..//li[@class='postObject-inside-eventContent'][text()='" + description + "']"),
				"Description " + description + " not found");
		
		Assert.assertTrue(ui.fluentWaitPresent("xpath=//li[@class='normalPostEntryItem'][1]//button[@class='btn btn-primary btn-xs ASWidgetSaveSelectedPost']"),
				"Save button not found");
		
		Assert.assertTrue(ui.fluentWaitPresent("xpath=//li[@class='normalPostEntryItem'][1]//button[@class='btn btn-primary btn-xs FollowAction']"),
				"Save button not found");
	  
		ui.endTest();
	}

	/**
	* <ul>
	*<li><B>Info:</B> Test Tag Cloud widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create tags.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create Tag Cloud widget.
	*<li><B>Step:</B> Trigger indexing.
	*<li><B>Verify:</B> Verify all tags appear as expected.
	*<li><B>Step:</B> Click tag .
	*<li><B>Verify:</B> Switch to new tab and ensure tag search page opens.
	*</ul>
	*/
	//Disabling this as tags is not visible on TagCloud widget. A defect has been raised for this issue - https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10657.
	@Test(groups = {"regression"}, enabled=false)
	public void testTagCloud() throws UnsupportedEncodingException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String[] tags = {"atag" + Helper.genDateBasedRand(), "btag" + Helper.genDateBasedRand(),
				"ctag" + Helper.genDateBasedRand(), "dtag" + Helper.genDateBasedRand(), "etag" + Helper.genDateBasedRand()};

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .tags("commTag")
				   .description(description).build();
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Create forum topics with tags");
		log.info("INFO: Create forum topics with tags");
		ui.createForumTopics(testUser, serverURL, community, tags.length, tags);
		logger.strongStep("Trigger indexing");
		log.info("INFO: Trigger indexing");
		new SearchAdminService().indexNow("forums");

		community.navViaUUID(commUI);
		
		driver.changeImplicitWaits(5);
		logger.strongStep("Remove default Tag Cloud widget");
		log.info("INFO: Remove default Tag Cloud widget");
		ui.removeWidget("Tag Cloud");
		driver.turnOnImplicitWaits();

		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccTagCloud", widgetTitle, "Tag Cloud");
		
		logger.strongStep("Verify all tags appear as expected");
		log.info("INFO: Verify all tags appear as expected");
		for (String tag : tags) {
			Assert.assertTrue(ui.fluentWaitPresent("css=div.xccTagCloud a:contains(" + tag + ")"),
					"Tag " + tag + " not found");
		}
		
		logger.strongStep("Click tag");
		log.info("INFO: Click tag");
		ui.clickLink("css=div.xccTagCloud a:contains(" + tags[0] + ")");
		
		logger.strongStep("Switch to new tab and ensure tag search page opens");
		log.info("INFO: Switch to new tab and ensure tag search page opens");
		ui.switchToNextTab();
		String tagSearchUrl = ui.replaceHttpWithHttps(serverURL + "/communities/service/html/communityview?" 
				+ community.getCommunityUUID() + "#tag=" + tags[0]);
		Assert.assertTrue(driver.getCurrentUrl().contains(tagSearchUrl),
		    	"Tag search opened with incorrect URL: " + tagSearchUrl);

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Top News widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Create Top News widget.
	*<li><B>Verify:</B> Check blog post is displayed in widget.
	*<li><B>Step:</B> Edit and allow content creation.
	*<li><B>Verify:</B> Check create news element is available.
	*<li><B>Step:</B> Click blog post.
	*<li><B>Verify:</B> Check blog post popup appears as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testTopNews() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();

		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);

		BaseBlogPost[] blogPosts = ui.createBlogPosts(testUser, serverURL, comAPI, community, 1);

		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		driver.changeImplicitWaits(5);
		logger.strongStep("Remove default News widget");
		log.info("INFO: Remove default News widget");
		ui.removeWidget("News - Top");
		driver.turnOnImplicitWaits();

		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccTopNews", widgetTitle,"News - Top");

		BaseBlogPost blogPost = blogPosts[0];
		logger.strongStep("Check Blog post " + blogPost.getTitle() + " appears as expected");
		log.info("INFO: Check Blog post " + blogPost.getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getTopNewsLink(blogPost)),
				"Blog post with title " + blogPost.getTitle() + " not found");

		Assert.assertTrue(ui.fluentWaitPresent("css=div.xccTopNews div.topNewsTeaser:contains(Test description for BlogPost)"),
				"Blog post content not found");

		logger.strongStep("Edit and allow content creation");
		log.info("INFO: Edit and allow content creation");
		ui.editWidget(widgetTitle);
		driver.executeScript("scroll(0,-50);");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.SOCIAL_AND_CONTENT_CREATION.toString());
		ui.selectCheckbox(IcecUI.allowContentCreationCheckbox);
		ui.saveWidget(widgetTitle);

		String[] selectorsToCheck = {"css=span.xccCreateNews a.xccMoreNewsLink"};
		ui.checkElementsArePresent(selectorsToCheck);
		
		driver.executeScript("scroll(0,-250);");
		logger.strongStep("Click Blog post: " + blogPosts[0]);
		log.info("INFO: Click Blog post: " + blogPosts[0]);
		ui.clickLinkWithJavascript(IcecUI.getTopNewsLink(blogPosts[0]));

		logger.strongStep("Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		log.info("INFO: Check Blog post popup for " + blogPosts[0].getTitle() + " appears as expected");
		Assert.assertTrue(ui.fluentWaitPresent(IcecUI.blogPopup),
					"Blog post popup not visible");
		Assert.assertTrue(ui.getElementText(IcecUI.getBlogFlyoutTitle(blogPosts[0])).equals(blogPosts[0].getTitle()),
				"Blog post popup title doesn't equal: " + blogPosts[0].getTitle() + ". Actual: " + IcecUI.getBlogFlyoutTitle(blogPosts[0]));

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Featured Communities widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Featured Communities widget.
	*<li><B>Step:</B> Create Featured Communities widget.
	*<li><B>Step:</B> Edit Featured Communities widget.
	*<li><B>Verify:</B> Verify option to add additional channel sources.
	*<li><B>Verify:</B> Check community is displayed in widget.
	*<li><B>Step:</B> Click community.
	*<li><B>Verify:</B> Check community appears as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testFeaturedCommunities() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String widgetTitle = "Description-" + communityName;
		String widgetType = "Featured Communities";

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .tags("commTag")
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .description(description).build();
		
		logger.strongStep("Login, create a community and navigate to it");
		log.info("INFO: Login, create a community and navigate to it");
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		communitiesToDelete.add(comAPI);
		
		logger.strongStep("Navigate to Highlights page");
		log.info("INFO: Navigate to Highlights page");
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		logger.strongStep("Remove default Featured Communities widget");
		log.info("INFO: Remove default Featured Communities widget");
		driver.changeImplicitWaits(5);
		ui.removeWidget(widgetType);
		driver.turnOffImplicitWaits();
		
		logger.strongStep("Create " + widgetType + " widget");
		log.info("INFO: Create " + widgetType + " widget");
		ui.createHighlightsWidget("xccCommunityOverview", widgetTitle, widgetType);
		
		logger.strongStep("Edit Featured Communities widget");
		log.info("INFO: Edit Featured Communities widget");
		ui.editWidget(widgetTitle);
		
		logger.strongStep("Verify option to add additional channel sources");
		log.info("INFO: Verify option to add additional channel sources");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
		ui.fluentWaitElementVisible("css=button.btn:contains(Add Channel)");
		ui.scrollIntoViewElement("css=button.btn:contains(Add Channel)");
		ui.clickLinkWithJavascript("css=button.btn:contains(Add Channel)");
		
		Assert.assertTrue(ui.fluentWaitPresent("xpath=//div[@class='ec-title']/span[text()='Channel 2']"),
				"Channel 2 isn't displayed");
		
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.ec-channel:nth-child(2) label.input-group input"),
		  "Channel 2 input source not displayed");
		 
		
		ui.cancelWidget(widgetTitle);
		ui.clickLink("css=button.bx--btn--secondary:contains(Discard Changes)");
		driver.executeScript("scroll(0, -50);");
		
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
		ui.fluentWaitTextPresent("Craft rich content for your community");
		String communityUrl = ui.replaceHttpWithHttps(serverURL + "/communities/service/html/communityoverview?" + community.getCommunityUUID());
		Assert.assertTrue(driver.getCurrentUrl().contains(communityUrl),
		    	"Community opened with incorrect URL: " + communityUrl);

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Media Gallery widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Upload files.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Media Gallery widget.
	*<li><B>Step:</B> Create Media Gallery widget.
	*<li><B>Verify:</B> Check images are displayed as expected.
	*<li><B>Step:</B> Edit and select 4 grid.
	*<li><B>Verify:</B> Check images are displayed as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testMediaGallery() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		String description = "Test Community for " + communityName;
		String[] files = {Data.getData().file1, Data.getData().file2,
				Data.getData().file3, Data.getData().file5, Data.getData().file6, Data.getData().file7,
				Data.getData().file8, Data.getData().file9, Data.getData().file10};

		BaseCommunity community = new BaseCommunity.Builder(communityName)
				   .access(defaultAccess)
				   .addMember(new Member(CommunityRole.MEMBERS, testUser))
				   .tags("commTag")
				   .description(description).build();
		Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		for (String file : files){
			createFile(testUser, comAPI, file);
		}		
	
		communitiesToDelete.add(comAPI);
		
		Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
		
		driver.changeImplicitWaits(5);		
		logger.strongStep("Remove default Media Gallery widget");
		log.info("INFO: Remove default Media Gallery widget");
		ui.removeWidget("Media Gallery");
		driver.turnOnImplicitWaits();
		
		String widgetTitle = "Description-" + communityName;
		ui.createHighlightsWidget("xccMediaGallery", widgetTitle, "Media Gallery");
		
		mediaGalleryCheck(files, 3);
		
		logger.strongStep("Edit and select 4 grid");
		log.info("INFO: Edit and select 4 grid");
		ui.editWidget(widgetTitle);
		driver.executeScript("scroll(0,-50);");
		ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.DISPLAY_SETTINGS.toString());
		ui.clickLink("css=div.gridSwitch > label:nth-child(1) > fieldset:nth-child(2) > label:nth-child(2)");
		driver.executeScript("scroll(0,-50);");
		ui.saveWidget(widgetTitle);
		
		mediaGalleryCheck(files, 2);

		ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Files widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Files.
	*<li><B>Step:</B> Re-load community and click highlights.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Files widget.
	*<li><B>Step:</B> Create Files widget.
	*<li><B>Step:</B> Edit Files widget and set 'Show Details' and 'Items per page'.
	*<li><B>Verify:</B> Check files and file details are displayed as expected.
	*</ul>
	*/
	@Test(groups = {"level2", "bvt"})
	public void testFiles() {
	  DefectLogger logger = dlog.get(Thread.currentThread().getId());
	  User testUser = cfg.getUserAllocator().getUser();

	  String testName = ui.startTest();
	  String communityName = testName + Helper.genDateBasedRand();
	  String description = "Test Community for " + communityName;
	  String widgetType = "Files List";
	  String widgetTitle = "Description-" + communityName;
	  
	  String[] files = {Data.getData().file1, Data.getData().file2,
	      Data.getData().file3, Data.getData().file5, Data.getData().file6, Data.getData().file7,
	      Data.getData().file8, Data.getData().file9, Data.getData().file10};

	  BaseCommunity community = new BaseCommunity.Builder(communityName)
	         .access(defaultAccess)
	         .addMember(new Member(CommunityRole.MEMBERS, testUser))
	         .tags("commTag")
	         .description(description).build();
	  
	  logger.strongStep("Login, create a community and navigate to it");
	  log.info("INFO: Login, create a community and navigate to it");
	  Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
	  communitiesToDelete.add(comAPI);
	  
	  logger.strongStep("Add multiple files");
	  log.info("INFO: Add multiple files");
	  for (String file : files){
	    createFile(testUser, comAPI, file);
	  }		
	  
	  logger.strongStep("Re-loading community and click on highlights page");
	  log.info("INFO: Re-loading community and click on highlights page");
	  community.navViaUUID(commUI);
	  ui.clickLinkWait(CommunitiesUIConstants.communityHighlights);
	  
	  logger.strongStep("Remove default Files widget");
	  log.info("INFO: Remove default Files widget");
	  driver.changeImplicitWaits(5);
	  ui.removeWidget(widgetType);
	  driver.turnOnImplicitWaits();
	  
	  logger.strongStep("Create " + widgetType + " widget");
	  log.info("INFO: Create " + widgetType + " widget");
	  ui.createHighlightsWidget("xccFiles", widgetTitle, widgetType);
	  
	  logger.strongStep("Edit Files widget and set 'Show Details' and 'Items per page'");
	  log.info("INFO: Edit Files widget and set 'Show Details' and 'Items per page'");
	  ui.editWidget(widgetTitle);
	  ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.DISPLAY_SETTINGS.toString());
	  ui.scrollIntoViewElement("css=input[aria-label='Show Details']");
	  ui.selectCheckbox("css=input[aria-label='Show Details']");
	  ui.expandLiveEditorSectionIfPresent(IcecUI.LiveEditorSelections.CONTENT_SOURCES.toString());
	  ui.scrollIntoViewElement("css=input[placeholder='Number of Items per Page']");
	  ui.clearAndTypeText("css=input[placeholder='Number of Items per Page']", "10");
	  ui.saveWidget(widgetTitle);
	  
	  logger.strongStep("Verify files and file details are displayed correctly");
	  log.info("INFO: Verify files and file details are displayed correctly");
	  for(int i = 1; i < files.length + 1; i++) {
		  logger.strongStep("Test File: " + files[i-1]);
		  log.info("INFO: Test File: " + files[i-1]);
		  Assert.assertTrue(ui.fluentWaitPresent("css=div:nth-child(" + i + ") > div.icons > a.xccFileLink"),
				  "File icon for file number " + i + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent("css=div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > h4 > a > span"),
				  "File number " + i + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent("css=div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > span i"),
				  "Download icon for file number " + i + " not found.");
		  Assert.assertTrue(ui.getElementText("css=div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > span > span:nth-child(2) > span > a").equalsIgnoreCase(testUser.getDisplayName()),
				  "User " + testUser.getDisplayName() + " for file number " + i + " not as expected");
	  }

	  ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test Files Explorer widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Files.
	*<li><B>Step:</B> Create Folder.
	*<li><B>Step:</B> Add files to folder.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Files Explorer widget.
	*<li><B>Step:</B> Create Files Explorer widget.
	*<li><B>Verify:</B> Check files and file details are displayed as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testFilesExplorer() {
	  DefectLogger logger = dlog.get(Thread.currentThread().getId());
	  User testUser = cfg.getUserAllocator().getUser();

	  String testName = ui.startTest();
	  String communityName = testName + Helper.genDateBasedRand();
	  String description = "Test Community for " + communityName;
	  String[] files = {Data.getData().file1, Data.getData().file2,
	      Data.getData().file3, Data.getData().file5, Data.getData().file6, Data.getData().file7,
	      Data.getData().file8, Data.getData().file9, Data.getData().file10};
	  
	  ArrayList<FileEntry> fileEntries = new ArrayList<FileEntry>();

	  BaseCommunity community = new BaseCommunity.Builder(communityName)
	         .access(defaultAccess)
	         .addMember(new Member(CommunityRole.MEMBERS, testUser))
	         .tags("commTag")
	         .description(description).build();
	  Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
	  for (String file : files){
	    fileEntries.add(createFile(testUser, comAPI, file));
	  }
	  
	  FileEntry folder = createFolder(testUser, comAPI);
	  
	  folder = addFilesToFolder(testUser, folder, fileEntries);

	  communitiesToDelete.add(comAPI);
	  
	  Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
	  
	  logger.strongStep("Remove default Files Explorer widget");
	  log.info("INFO: Remove default Files Explorer widget");
	  driver.changeImplicitWaits(5);
	  ui.removeWidget("Files Explorer");
	  driver.turnOnImplicitWaits();
	  
	  String widgetTitle = "Description-" + communityName;
	  ui.createHighlightsWidget("xccFilesExplorer", widgetTitle, "Folders");
	  
	  logger.strongStep("Click into folder " + folder.getTitle());
	  log.info("INFO: Click into folder : " + folder.getTitle());
	  ui.clickLink("css=span.xccFileName:contains(" + folder.getTitle() + ")");
	  for(int i = 1; i < files.length +1; i++) {
		  logger.strongStep("Test File: " + files[i-1]);
		  log.info("INFO: Test File: " + files[i-1]);
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccFilesExplorer div:nth-child(" + i + ") > div.icons > a[name='folderLinks']"),
				  "File icon for file number " + i + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccFilesExplorer div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > h4 > a > span"),
				  "File number " + i + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccFilesExplorer div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > span i"),
				  "Download icon for file number " + i + " not found.");
		  driver.changeImplicitWaits(5);
		  if((!ui.isElementPresent("css=div.xccFilesExplorer div.entry.file-entry:nth-child("+i+")[style=''][class='entry file-entry']")) &
				  (ui.isElementPresent("css=div.xccFilesExplorer div a[aria-label='Show next items'][style='']")))
			  ui.clickLink("css=div.xccFilesExplorer div a[aria-label='Show next items']");
		  driver.turnOnImplicitWaits();
		  
		  Assert.assertTrue(ui.getElementText("css=div.xccFilesExplorer div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > span > span:nth-child(2) > span > a").equalsIgnoreCase(testUser.getDisplayName()),
				  "User " + testUser.getDisplayName() + " for file number " + i + " not as expected");
	  }

	  ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test My Files widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Create Files.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default Files Explorer widget.
	*<li><B>Step:</B> Create Files Explorer widget.
	*<li><B>Verify:</B> Check files and file details are displayed as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testMyFiles() {
	  DefectLogger logger = dlog.get(Thread.currentThread().getId());
	  User testUser = cfg.getUserAllocator().getUser();

	  String testName = ui.startTest();
	  String communityName = testName + Helper.genDateBasedRand();
	  String description = "Test Community for " + communityName;
	  String[] files = {Data.getData().file1, Data.getData().file2,
	      Data.getData().file3, Data.getData().file5, Data.getData().file6, Data.getData().file7,
	      Data.getData().file8, Data.getData().file9, Data.getData().file10};

	  BaseCommunity community = new BaseCommunity.Builder(communityName)
	         .access(defaultAccess)
	         .addMember(new Member(CommunityRole.MEMBERS, testUser))
	         .tags("commTag")
	         .description(description).build();
	  Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
	  for (String file : files){
	    createFile(testUser, null, file);
	  }		

	  communitiesToDelete.add(comAPI);
	  
	  Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
	  
	  driver.changeImplicitWaits(5);
	  logger.strongStep("Remove default My Files widget");
	  log.info("INFO: Remove default My Files widget");
	  ui.removeWidget("My Files");
	  driver.turnOnImplicitWaits();

	  String widgetTitle = "Description-" + communityName;
	  ui.createHighlightsWidget("xccMyFiles", widgetTitle, "My Files");
	  
	  for(int i = 1; i < files.length + 1; i++) {
		  logger.strongStep("Test File: " + files[i-1]);
		  log.info("INFO: Test File: " + files[i-1]);
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccMyFiles div:nth-child(" + i + ") > div.icons > a.xccFileLink"),
				  "File icon for file number " + i + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccMyFiles div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > h4 > a > span"),
				  "File number " + i + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccMyFiles div:nth-child(" + i + ") > div.entryBody.clearfix.filesEntryBody > span i"),
				  "Download icon for file number " + i + " not found.");
	  }

	  ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test My Communities widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create communities and navigate to first.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default My Communities widget.
	*<li><B>Step:</B> Create My Communities widget.
	*<li><B>Verify:</B> Check community details are displayed as expected.
	*<li><B>Step:</B> Click to flip tile.
	*<li><B>Verify:</B> Check community summary details are displayed as expected.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testMyCommunities() {
	  DefectLogger logger = dlog.get(Thread.currentThread().getId());
	  User testUser = cfg.getUserAllocator().getUser();
	  String testName = ui.startTest();
	  String communityName = testName + Helper.genDateBasedRand();
	  String description = "Test Community for " + communityName;
	  String widgetTitle = "Description-" + communityName;
	  
	  ArrayList<BaseCommunity> communities = new ArrayList<BaseCommunity>();
	  for(int i = 0; i < 3; i++) {
		   BaseCommunity community = new BaseCommunity.Builder(communityName)
		         .access(defaultAccess)
		         .addMember(new Member(CommunityRole.MEMBERS, testUser))
		         .tags("commTag")
		         .description(description).build();
		  Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
		  communitiesToDelete.add(comAPI);
		  communities.add(community);
		  
		  communityName = testName + Helper.genDateBasedRand();
		  description = "Test Community for " + communityName;
	  }
	  
	  Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
	  driver.changeImplicitWaits(5);
	  logger.strongStep("Remove default My Communities widget");
	  log.info("INFO: Remove default My Communities widget");
	  ui.removeWidget("My Communities");
	  driver.turnOnImplicitWaits();

	  ui.createHighlightsWidget("xccMyCommunities", widgetTitle, "My Communities");
	  
	  for(int i = 0; i < communities.size(); i++) {
		  BaseCommunity community = communities.get(i);
		  String commName = community.getName();
		  logger.strongStep("Check " + commName + " details are as expected");
		  log.info("INFO: Check " + commName + " details are as expected");
		  Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMyCommunitiesImage(i+1)),
				  "Community image for " + commName+ " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMyCommunitiesTitle(community)),
				  "Community title for " + commName + " not found.");
		  
		  logger.strongStep("Toggle tile");
		  log.info("INFO: Toggle tile");
		  WebElement element = (WebElement) driver.getFirstElement(IcecUI.getMyCommunitiesTileToggle(i+1)).getBackingObject();
		  driver.executeScript("arguments[0].style.display = 'block';", element);
		  ui.clickLink(IcecUI.getMyCommunitiesTileToggle(i+1));
		  
		  logger.strongStep("Check " + commName + " summary details are as expected");
		  log.info("INFO: Check " + commName + " summary details are as expected");
		  Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMyCommunitiesSummaryTitle(community)),
				  "Community summary title for " + commName + " not found.");
		  Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMyCommunitiesDescription(community)),
				  "Community summary description for " + commName + " not found.");
	  }
	  ui.endTest();
	}
	
	/**
	* <ul>
	*<li><B>Info:</B> Test HTML widget.
	*<li><B>Step:</B> Login.
	*<li><B>Step:</B> Create community and navigate to it.
	*<li><B>Step:</B> Navigate to Highlights.
	*<li><B>Step:</B> Remove default HTML widget.
	*<li><B>Step:</B> Create HTML widget.
	*<li><B>Step:</B> Edit HTML widget .add HTML to code block
	*<li><B>Step:</B> Add HTML to code block and save.
	*<li><B>Verify:</B> Check rendered HTML in widget.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void testHTML() {
	  DefectLogger logger = dlog.get(Thread.currentThread().getId());
	  User testUser = cfg.getUserAllocator().getUser();
	  String testName = ui.startTest();
	  String communityName = testName + Helper.genDateBasedRand();
	  String description = "Test Community for " + communityName;
	  String widgetTitle = "Description-" + communityName;
	  
	  BaseCommunity community = new BaseCommunity.Builder(communityName)
			  .access(defaultAccess)
		      .addMember(new Member(CommunityRole.MEMBERS, testUser))
		      .tags("commTag")
		      .description(description).build();
	  Community comAPI = ui.createLoginAndNavigateToCommunity(testUser, serverURL, communityName, description, community, null);
	  communitiesToDelete.add(comAPI);

	  Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
	  
	  driver.changeImplicitWaits(5);
	  logger.strongStep("Remove default HTML widget");
	  log.info("INFO: Remove default HTML widget");
	  ui.removeWidget("HTML");
	  driver.turnOnImplicitWaits();
	  
	  ui.createHighlightsWidget("xccClipping", widgetTitle, "HTML");
	  
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
			  "HTML header expected: " + IcecUI.HTML_HEADER + " Actual: " + ui.getElementText("css=div.xccClipping h1"));
	  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping p:contains(User: " + testUser.getDisplayName() + ")"),
			  "HTML paragraph expected: User: " + testUser.getDisplayName()+ " Actual: " + ui.getElementText("css=div.xccClipping p"));
	  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping label[for='fName']:contains(First name:)"),
			  "First Name label expected: 'First Name:' Actual: " + ui.getElementText("css=div.xccClipping label[for='fName']"));
	  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping #fName"),
			  "First Name input not found");
	  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping label[for='lName']:contains(Last name:)"),
			  "Last Name label expected: 'Last Name:' Actual: " + ui.getElementText("css=div.xccClipping label[for='lName']"));
	  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping #lName"),
			  "Last Name input not found");
	  Assert.assertTrue(ui.fluentWaitPresent("css=div.xccClipping button"),
			  "HTML button not found");
	  
	  ui.endTest();
	}
	
	@AfterClass(alwaysRun = true)
	public void cleanUp() {
		User adminTestUser = cfg.getUserAllocator().getAdminUser();
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
	
	private FileEntry createFile(User apiUser, Community community, String fileFromData) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Creating file: " + fileFromData);
		log.info("INFO: Creating file: " + fileFromData);
		APIFileHandler apiFileOwner = new APIFileHandler(
				APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()),
				apiUser.getAttribute(cfg.getLoginPreference()), apiUser.getPassword());

		String fileName = "File_" + Helper.genDateBasedRand();

		BaseFile baseFile = new BaseFile.Builder(fileFromData).extension(".jpg")
				.rename(fileName).build();
		String filePath = "resources" + File.separator + fileFromData;

		File file = new File(filePath);
		FileEntry fileEntry;

		if (community == null) {
			fileEntry = apiFileOwner.CreateFile(baseFile, file);
			filesToDelete.add(fileEntry);
		} else {
			fileEntry = apiFileOwner.CreateFile(baseFile, file, community);
		}
		Assert.assertTrue(fileEntry != null,
				"Failed to upload file using API.");
		return fileEntry;
	}
	
	private FileEntry createFolder(User apiUser, Community community) {
		APIFileHandler apiFileOwner = new APIFileHandler(
				APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()),
				apiUser.getAttribute(cfg.getLoginPreference()), apiUser.getPassword());

		String folderName = "FilesExplorerFolder" + Helper.genStrongRand();
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Creating folder: " + folderName);
		log.info("INFO: Creating folder: " + folderName);
		
		BaseFile baseFolder = new BaseFile.Builder(folderName)
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		FileEntry folderEntry = apiFileOwner.createCommunityFolder(community, baseFolder);
		Assert.assertTrue(folderEntry != null,
				"Failed to create folder: " + folderEntry);
		return folderEntry;
	}
	
	private FileEntry addFilesToFolder(User apiUser, FileEntry folderEntry, ArrayList<FileEntry> filesList) {
		APIFileHandler apiFileOwner = new APIFileHandler(
				APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()),
				apiUser.getAttribute(cfg.getLoginPreference()), apiUser.getPassword());
		ArrayList<String> newFilesList = new ArrayList<String>();
		for(FileEntry fileEntry : filesList){
			newFilesList.add(fileEntry.getId().toString().split(Data.getData().FileListPrefix)[1]);
		}
		
		return apiFileOwner.addFilestoFolder(folderEntry, newFilesList);
	}
	
	private BaseWikiPage[] createWikiPages(User testUser, Community comAPI, BaseCommunity community, int noOfPages) {
		return createWikiPages(testUser, comAPI, community, noOfPages, null, null);
	}
	
	
	private BaseWikiPage[] createWikiPages(User testUser, Community comAPI, BaseCommunity community, int noOfPages, PageType pageType, Wiki wiki) {
		BaseWikiPage[] wikiPages = new BaseWikiPage[noOfPages];
		APIWikisHandler apiWikiOwner = new APIWikisHandler(serverURL,
				testUser.getUid(), testUser.getPassword());
		if (pageType == null) pageType = PageType.Peer;
		
		log.info("Add Wiki widget to community if not already added");
		APICommunitiesHandler apiCommunityOwner = new APICommunitiesHandler(serverURL,
				testUser.getEmail(), testUser.getPassword());
		if(!apiCommunityOwner.hasWidget(comAPI, BaseWidget.WIKI)){
			community.addWidgetAPI(comAPI, apiCommunityOwner,
					BaseWidget.WIKI);
		}
				
		for(int i = 0; i < noOfPages; i++) {
			BaseWikiPage wikiPage = new BaseWikiPage.Builder("WikiPage" + Helper.genDateBasedRand(), pageType)
					.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
					.description("Test description for WikiPage " + Helper.genDateBasedRand()).build();
			log.info("Creating: " + wikiPage.getName());
			if (wiki == null) wiki = apiWikiOwner.getCommunityWiki(comAPI);
			wikiPage.createAPI(apiWikiOwner, wiki);

			wikiPages[i] = wikiPage;
			// Sleep to add delay before next api call to avoid creation in wrong order
			ui.sleep(1000);
		}
		return wikiPages;
	}
	
	private void mediaGalleryCheck(String[] files, int numOfCols) {
		for(int i = 0, j = 0; i < files.length; i++) {
			int col = i%numOfCols+1;
			if (i%numOfCols == 0) {
				j++;
			}
			log.info("INFO: Checking Row: " + j + " Column: " + col);
			Assert.assertTrue(ui.fluentWaitPresent(IcecUI.getMediaGalleryImage(j, col)),
					"Image " + files[i] + " not found");
			if (numOfCols == 2 && j == 2 && col == 2 && ui.isElementVisible(IcecUI.nextLinkMediaGallery)) {
				ui.clickLink(IcecUI.nextLinkMediaGallery);
				j = 0;
			}
		}
	}
}
