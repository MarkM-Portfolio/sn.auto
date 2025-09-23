package com.ibm.conn.auto.tests.communities;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseSurvey;
import com.ibm.conn.auto.appobjects.base.BaseSurveyQuestion;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Communities extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities.class);
	private CommunitiesUI ui;
	private FeedsUI fUI;
	private DogearUI uiBM;
	private FilesUI fileUI;
	private ForumsUI forumUI;
	private GlobalsearchUI sUI;
	CommonUICnx8 commonUI;
	private TestConfigCustom cfg;	
	private User testUser, testLookAheadUser, testUser1, guestUser;
	private Member member;
	private String communitiesURI, serverURL;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private boolean isOnPrem;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		member = new Member(CommunityRole.MEMBERS, testLookAheadUser);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fileUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI = FeedsUI.getGui(cfg.getProductName(), driver);
		forumUI = ForumsUI.getGui(cfg.getProductName(), driver);
		uiBM = DogearUI.getGui(cfg.getProductName(), driver);	
		sUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		commonUI  = new CommonUICnx8(driver);
		communitiesURI = Data.getData().ComponentCommunities.split("/")[0];
		
		isOnPrem = cfg.getProductName().equalsIgnoreCase("onprem");
		
		ui.addOnLoginScript(ui.getCloseTourScript());
	}	
	
	/**
	*<ul>
	*<li><B>Info:</B>Add and edit a bookmark to a community</li>
	*<li><B>Step:</B>Create a public community with name, tag and description</li>
	*<li><B>Step:</B>Add a bookmark and verify</li>
	*<li><B>Step:</B>Edit a bookmark and verify</li>
	*<li><B>Verify:</B>Bookmark is created and listed in the bookmark view</li>
	*<li><B>Verify:</B>Bookmark is created and listed in the Overview view</li>
	*<li><B>Verify:</B>Edited Bookmark is listed in the bookmark view</li>
	*<li><B>Verify:</B>Edited Bookmark is listed in the Overview view</li>
	*</ul>
	*/
	@Test(groups = {"level2", "cnx8ui-level2", "smokeonprem", "bvt", "bvtcloud", "smokecloud","regressioncloud"})
	public void communityBookmarkViews() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();

		BaseDogear bookmark = new BaseDogear.Builder(Data.getData().BookmarkName , Data.getData().BookmarkURL)
											.community(community)
											.tags(Data.getData().BookmarkTag)
											.description(Data.getData().BookmarkDesc)
											.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())
		{
			ui.loadComponent(Data.getData().ComponentHomepage);
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
			AppNavCnx8.COMMUNITIES.select(commonUI);
		}
		else
		{
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
		}
		
		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);

		//Check for the Tabbed Nav GK flag
		gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		value = ui.checkGKSetting(gk_flag);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Navigate to bookmark widget
		if(value) {
			logger.strongStep("Add a bookmark to the Community using the tabbed navigation bar");
			log.info("INFO: Add a bookmark to the Community using the tabbed nav bar");
			Community_TabbedNav_Menu.BOOKMARK.select(ui, 2);
		}else {
			logger.strongStep("Add a bookmark to the Community using the left navigation bar");
			log.info("INFO: Add a bookmark to the Community using the left nav bar");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
			
			Community_LeftNav_Menu.BOOKMARK.select(ui);
		}
		
		logger.strongStep("Click the 'Add bookmark' button");
		log.info("INFO: Click the 'Add bookmark' button");
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.AddBookmark),
				  "ERROR: Add Bookmark button is not displayed on the page");
		ui.clickLinkWait(DogearUIConstants.AddBookmark);
		
		//Now add a bookmark
		logger.strongStep("Fill out the bookmark form and save");
		log.info("INFO: Fill out the bookmark form and save");
		bookmark.create(uiBM);
		
		//Verify that the bookmark is appearing in the bookmark list view
		logger.weakStep("Verify that the bookmark appears in the bookmark list view");
		log.info("INFO: Verify that the bookmark appears in the bookmark list view");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + bookmark.getTitle()),
						  "ERROR: Bookmark is not in the bookmark view");

		//Now return to the overview page
		logger.strongStep("Return to the Overview page");
		log.info("INFO: Navigate to the Overview page");
		if(value) {
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}

		//Verify that the bookmark is appearing in the Overview
		logger.weakStep("Verify that the bookmark appears in the Overview page");
		log.info("INFO: Verify that the bookmark appears in the Overview page");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + bookmark.getTitle()),
						  "ERROR: Bookmark is not in the Overview page");

		//Change bookmark title
		logger.strongStep("Change the bookmark title");
		bookmark.setTitle(Data.getData().EditBookmarkName);
		bookmark.setDescription("Edited description for " + testName);
		bookmark.setURL(Data.getData().EditBookmarkURL);
		bookmark.setTags(Data.getData().EditBookmarkTag);
		
		//Now Edit the bookmark
		logger.strongStep("Edit the bookmark");
		log.info("INFO: Edit the bookmark");
		if(value) {
			bookmark.editWithDropDown(uiBM);
		}else {
			bookmark.edit(uiBM);
		}

		//Verify that the bookmark is appearing in the bookmark list view
		logger.weakStep("Verify that the bookmark appears in the bookmark list view");
		log.info("INFO: Verify that the bookmark appears in the bookmark list view");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + bookmark.getTitle()),
						  "ERROR: Bookmark is not in the bookmark view");

		//Now return to the overview page
		logger.strongStep("Navigate to the Overview page");
		log.info("INFO: Navigate to the Overview page");
		if(value) {
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}

		//Verify that the bookmark is appearing in the Overview
		logger.weakStep("Verify that the bookmark appears in the Overview page");
		log.info("INFO: Verify that the bookmark appears in the Overview page");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + bookmark.getTitle()),
						  "ERROR: Bookmark is not in the Overview page");
		
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Info:</B>Add forums post to a community</li>
	*<li><B>Step:</B>Create a public community with name, tag and description</li>
	*<li><B>Step:</B>Add a forum entry</li>
	*<li><B>Verify:</B>Validate the forum entry exists in the community</li>
	*</ul>
	*/
	@Test(groups = {"level2", "cnx8ui-level2", "bvt","regressioncloud"})
	public void createForumPost() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRandVal())
			 										  .tags(Data.getData().ForumTopicTag)
			 										  .description(Data.getData().commonDescription)
			 										  .partOfCommunity(community)
			 										  .build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}					


		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Add a forum topic with description and tag
		logger.strongStep("Add a forum topic with a description and a tag");
		log.info("INFO: Add a forum topic with a description and a tag");
		forumTopic.create(forumUI);
				
		//Check for the Tabbed Nav GK flag
		gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		value = ui.checkGKSetting(gk_flag);
				
		logger.strongStep("Navigate to the Overview page");
		log.info("INFO: Navigate to the Overview page");
		if(value) {
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		logger.weakStep("Validate that the forum topic is present");
		log.info("INFO: Validate that the forum topic is present");
		// add wait to avoid looking for the topic prematurely
		ui.fluentWaitElementVisible(ForumsUIConstants.Start_A_Topic);
		Assert.assertTrue(driver.isElementPresent(ForumsUI.selectComForumTopic(forumTopic)), 
						  "ERROR: Cannot locate the forum topic");
		
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> FEEDS WIDGET PART1: VERIFY CREATING NEW FEEDS (Test 2)</li>
	*<li><B>Info:</B> Create and View Feed in Overall Community page</li>
	*<li><B>Step:</B> [API] Create a public community with name, tag and description</li>
	*<li><B>Step:</B> Add a feed and include a tag and description</li>
	*<li><B>Verify:</B> The feed displays in the community overview fullpage</li>
	*<li><B>Verify:</B> Entry show title, person, date/time field, tags and more link are displayed</li>
	*<li><B>Verify:</B> Add a Feed link still appears at the top of the widget.</li>
	*<li><B>Verify:</B> View All is a clickable link that appears below the Feed entry.</li>
	*<li><B>Verify:</B> Feeds Widget page is displayed</li>
	*<li><B>Step: </B> [API] Delete the community</li>
	**<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0FF8E007348961D185257C8D0067F0CE">TTT-STATUS FEEDS WIDGET PART1: VERIFY CREATING NEW FEEDS</a></li>
	*</ul>
	*note: the cloud does not support this
	*/
	@Test(groups = {"cplevel2", "mt-exclude", "bvt", "regression"})
	public void createFeedAndViewInOverviewPage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
			
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test creating new feeds " + testName)
													.build();

		BaseFeed feed = new BaseFeed.Builder(Data.getData().FeedsTitle + Helper.genDateBasedRandVal(), cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL)
									.description(Data.getData().commonDescription)
									.tags(Data.getData().MultiFeedsTag)
									.build();
		
		//create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the Feeds widget to the Community");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FEEDS)) {
			log.info("INFO: Add the Feeds widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);
		}
		
		//GUI
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");


		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communtiy' view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		//Click Add Feed link
		logger.strongStep("Click on the 'Add Feed' Link");
		log.info("INFO: Select the 'Add Feed' link");
		ui.scrollIntoViewElement(CommunitiesUIConstants.AddAFeed);
		ui.clickLinkWait(CommunitiesUIConstants.AddAFeed);
		
		//Add the feed
		logger.strongStep("Add the feed to the Community");
		log.info("INFO: Add the feed to the Community");
		fUI.addFeed(feed);

		//Test feeds success message
		logger.weakStep("Verify that the 'Feed Success' message is posted");
		log.info("Verify that the 'Feed Success' message is posted");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FeedSuccessMsg),
										"Error : Feed success message is not shown properly");
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Go to Overview page
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Go to the Overview page");
			log.info("INFO: Navigate to the Overview page");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Go to the Overview page");
			log.info("INFO: Navigate to the Overview page");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		//Verify that the feed displays in widget
		logger.weakStep("Verify that the feed is present in the widget page");
		log.info("INFO: Validate the feed is present in the widget page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + feed.getTitle()),
							"ERROR: Feed title is not displayed");
		
		//Verify the person who added the feed displays
		logger.weakStep("Verify that the person who added the feed is displayed");
		log.info("INFO: Verify the person who added the feed displays");
		Assert.assertTrue(ui.fluentWaitElementVisible("//span[text()='Feeds']/../..//a[text()='"+testUser.getDisplayName()+"']"),"ERROR: Person who added the feed is not displayed");
		
		//Verify the Date field is displayed
		logger.weakStep("Verify that the Date field is displayed");
		log.info("INFO: Verify the Date field is displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.FeedDate),
							"ERROR: Date field is not displayed");
		
		//Verify the Tags field appears with the 2 tags
		logger.weakStep("Verify that the tag fields appear with the 2 tags");
		log.info("INFO: Verify that the News tag appears as a tag");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+Data.getData().MultiFeedsTag.split(" ")[0].trim()),
							"ERROR: News is not present as a tag");
		
		logger.weakStep("Verify that the tag fields appear with the 2 tags");
		log.info("INFO: Verify that the Travel tag appears as a tag");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+Data.getData().MultiFeedsTag.split(" ")[1].trim()),
							"ERROR: Travel is not present as a tag");
		
		//Verify Add a Feed link still appears
		logger.weakStep("Verify that the 'Add a Feed' link still appears");
		log.info("INFO: Verify that the 'Add a Feed' link still appears");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.AddFeedLink),
							"ERROR: 'Add a Feed' link is not present");
		
		//Verify View All link appears
		logger.weakStep("Verify that the 'View All' link appears");
		log.info("INFO: Verify that the 'View All' link appears");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.FeedsViewAllLink),
							"ERROR: 'View All' link is not present");
				
		//Click on View All
		logger.strongStep("Click on 'View All'");
		log.info("INFO: Click on View All");
		ui.clickLinkWait(CommunitiesUIConstants.FeedsViewAllLink);
		
		//Verify Feeds Widget page is opened
		logger.weakStep("Verify that the Feeds Widget page is opened");
		log.info("INFO: Verify that the Feeds Widget page is opened");
		Assert.assertTrue(ui.fluentWaitElementVisible(FeedsUI.AddFeedLink),
							"ERROR: Feeds Widget page is not opened");
		
		//Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> FEEDS WIDGET PART2: VERIFY EDITING & DELETING NEWS FEEDS (Test 1)</li>
	*<li><B>Info:</B> Create and Edit Feed in Public Community</li>
	*<li><B>Step:</B> [API] Create a public community with name, tag and description</li>
	*<li><B>Step:</B> [API] Add a feed and include a tag and description</li>
	*<li><B>Step:</B> [API] Add a second feed and include a tag and description</li>
	*<li><B>Step:</B> Edit first Feed</li>
	*<li><B>Verify:</B> The feed displays with updated info in the Feeds full page</li>
	*<li><B>Step:</B> Go to community Overview page</li>
	*<li><B>Verify:</B> The feed displays with updated info in the Overview page</li>
	*<li><B>Step: </B> [API] Delete the community</li>
	**<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B1E23BD6A5A4F26285257C8D0068FDF5">TTT-STATUS FEEDS WIDGET PART2: VERIFY EDITING & DELETING NEWS FEEDS</a></li>
	*</ul>
	*note: the cloud does not support this
	*/
	@Test(groups = {"level2", "cnx8ui-level2", "mt-exclude", "bvt", "regression"})
	public void createAndEditFeed() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String rndNum1 = Helper.genDateBasedRandVal();
		String rndNum2 = Helper.genDateBasedRandVal2();
		String rndNum3 = Helper.genDateBasedRandVal3();
		
			
		//Community
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test creating new feeds " + testName)
													.build();
		//first feed
		BaseFeed firstfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum1, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum1)
									.description(Data.getData().commonDescription + rndNum1)
									.tags(Data.getData().MultiFeedsTag)
									.build();
		
		//second feed
		BaseFeed secondfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum2, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum2)
									.description(Data.getData().commonDescription + rndNum2)
									.tags(Data.getData().MultiFeedsTag1)
									.build();
		
		//edit feed
		BaseFeed editfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum3, cfg.getTestConfig().getBrowserURL() + Data.getData().EditedFeedsURL + rndNum3)
										  .description(Data.getData().EditedTestDescription)
										  .tags(Data.getData().MultiFeedsTag2)
										  .build();
		
		//create community
		logger.strongStep("Create a Community as: " +testUser.getDisplayName());
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget
		logger.strongStep("Add Feeds widget to the Community");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FEEDS)) {
			log.info("INFO: Add Feeds widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);
		}
		
		//Add the first feed
		logger.strongStep("Add the first feed");
		log.info("INFO: Add the first feed via API");
		firstfeed.createAPI(apiOwner, comAPI);
		
		//Add the second feed
		logger.strongStep("Add the second feed");
		log.info("INFO: Add the second feed via API");
		secondfeed.createAPI(apiOwner, comAPI);
		
		//GUI
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}	
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communties' view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Go to Overview page
		if (value)
		{
			//Choose Feed from the tabbed nav
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Choose 'Feeds' from the tabbed navigation menu");
			log.info("INFO: Select 'Feeds' from the tabbed nav menu");
			Community_TabbedNav_Menu.FEEDS.select(ui,2);
		}else { 
			//Choose Feed from the left nav
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Choose 'Feeds' from the left navigation menu");
			log.info("INFO: Select 'Feeds' from the left nav menu");
			Community_LeftNav_Menu.FEEDS.select(ui);
		}

		//Edit first feed		
		//Click on Edit
		logger.strongStep("Click More > Edit on first feed");
		log.info("INFO: Click More > Edit on first feed");
		fUI.selectMoreLinkByFeed(firstfeed);
		fUI.selectEditLinkByFeed(firstfeed);
		
		//Edit feed
		logger.strongStep("Make modifications and save the feed");
		log.info("INFO: Edit the feed and save");
		fUI.editFeed(editfeed);
				
		//Verify that the updated feed displays in full page
		logger.strongStep("Verify that the updated feed displays in the full page");
		log.info("INFO: Validate the updated feed is present in the full page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + editfeed.getTitle()),
							"ERROR: Feed title is not found");
		
		//Verify the updated Tags field appears with the 2 tags
		logger.weakStep("Verify that the tags field appears with the 2 tags");
		log.info("INFO: Verify that the tags appear");
		Assert.assertTrue(driver.getFirstElement(FeedsUI.FeedTags).getSingleElement("link="+Data.getData().MultiFeedsTag2.split(" ")[0].trim()).isVisible(),
							"ERROR: Sports is not found as tag");
		Assert.assertTrue(driver.getFirstElement(FeedsUI.FeedTags).getSingleElement("link="+Data.getData().MultiFeedsTag2.split(" ")[1].trim()).isVisible(),
							"ERROR: Nontravel is not found as tag");

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Go to Overview page
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Go to the Overview page");
			log.info("INFO: Navigate to the Overview page");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Go to the Overview page");
			log.info("INFO: Navigate to the Overview page");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}
		
		//Verify that the updated feed displays in Overview widget
		logger.strongStep("Verify that the updated feed displays on the Overview page");
		log.info("INFO: Validate the updated feed is present on the Overview page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + editfeed.getTitle()),
							"ERROR: Feed title is not found");
		
		//Verify the updated Tags field appears with the 2 tags
		logger.weakStep("Verify that the updated tags field appear with the 2 tags");
		log.info("INFO: Verify that the updated tags appear");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+Data.getData().MultiFeedsTag2.split(" ")[0].trim()),
							"ERROR: Sports is not found as tag");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+Data.getData().MultiFeedsTag2.split(" ")[1].trim()),
							"ERROR: Nontravel is not found as tag");
		
		//Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> FEEDS WIDGET PART2: VERIFY EDITING & DELETING NEWS FEEDS (Test 2)</li>
	*<li><B>Info:</B> Create and Delete Feed in Public Community</li>
	*<li><B>Step:</B> [API] Create a public community with name, tag and description</li>
	*<li><B>Step:</B> [API] Add a feed and include a tag and description</li>
	*<li><B>Step:</B> [API] Add a second feed and include a tag and description</li>
	*<li><B>Verify:</B> The first and second feed displays in the feeds widget page</li>
	*<li><B>Step:</B> Delete second feed</li>
	*<li><B>Verify:</B> The delete warning message displays</li>
	*<li><B>Verify:</B> The delete feed no longer exists</li>
	*<li><B>Step:</B> Go to community overview page</li>
	*<li><B>Verify:</B> The delete feed no longer exists</li>
	*<li><B>Step: </B> [API] Delete the community</li>
	**<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B1E23BD6A5A4F26285257C8D0068FDF5">TTT-STATUS FEEDS WIDGET PART2: VERIFY EDITING & DELETING NEWS FEEDS</a></li>
	*</ul>
	*note: the cloud does not support this
	*/
	@Test(groups = {"mt-exclude", "bvt", "regression"})
	public void createAndDeleteFeed() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String rndNum1 = Helper.genDateBasedRandVal();
		String rndNum2 = Helper.genDateBasedRandVal2();
				
		//community
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test creating new feeds " + testName)
													.build();

		//first feed
		BaseFeed firstfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum1, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum1)
									.description(Data.getData().commonDescription + rndNum1)
									.tags(Data.getData().MultiFeedsTag)
									.build();
		//second feed
		BaseFeed secondfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum2, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum2)
									.description(Data.getData().commonDescription + rndNum2)
									.tags(Data.getData().MultiFeedsTag1)
									.build();
		
		//create community
		logger.strongStep("Create a Community as: " +testUser.getDisplayName());
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget
		logger.strongStep("Add the 'Feeds' widget to the Community");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FEEDS)) {
			log.info("INFO: Add the 'Feeds' widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);
		}
		
		//Add the first feed
		logger.strongStep("Add the first feed");
		log.info("INFO: Add the first feed via API");
		firstfeed.createAPI(apiOwner, comAPI);
		
		//Add the second feed
		logger.strongStep("Add the second feed");
		log.info("INFO: Add the second feed via API");
		secondfeed.createAPI(apiOwner, comAPI);
		
		//GUI
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}					

		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communties' view");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Click on View All in Feed widget
		logger.strongStep("Click on the 'View All' link in the Feeds widget");
		log.info("INFO: Click on 'View All'");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.FeedsViewAllLink);
			
		//Verify that the second feed displays in widget full page
		logger.weakStep("Validate that the second feed is present in the full page");
		log.info("INFO: Validate the second feed is present in the full page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + secondfeed.getTitle()),
							"ERROR: Second Feed title is not found");
		
		//Verify that the first feed displays in widget full page
		logger.weakStep("Verify that the first feed displays in the full page");
		log.info("INFO: Validate the first feed is present in the full page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + firstfeed.getTitle()),
							"ERROR: First Feed title is not found");
		
		//Click on Delete
		logger.strongStep("Click More > Delete on the second feed ");
		log.info("INFO: Click More > Delete on the second feed");
		fUI.selectMoreLinkByFeed(secondfeed);
		fUI.selectDeleteLinkByFeed(secondfeed);
		
		//Verify warning message
		logger.weakStep("Verify that the 'Delete Feed' warning dialog appears");
		log.info("INFO: Verify that the 'Delete Feed' warning dialog appears");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().DeleteFeedMsg),
							"ERROR: 'Delete Feed' dialog was not found for second feed");
		
		//Click on OK in Delete Feed dialog
		logger.strongStep("Click 'OK' in the 'Delete Feed' warning dialog");
		log.info("INFO: Click 'OK' in the warning dialog");
		ui.clickLinkWait(HomepageUIConstants.AddtoPersonalCalOkButton);
			
		//Verify that the feed is deleted
		logger.strongStep("Verify that the second feed was deleted");
		log.info("INFO: Validate the second feed is not present on the page");
		Assert.assertFalse(driver.isElementPresent("link=" + secondfeed.getTitle()),
							"ERROR: Deleted Second Feed was found");
		
		//Verify that the first feed displays
		logger.weakStep("Verify that the first feed displays on the page");
		log.info("INFO: Validate the first feed is present on the page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + firstfeed.getTitle()),
							"ERROR: First Feed title is not found");

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Go to Overview page
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Go to the Overview page");
			log.info("INFO: Navigate to the Overview page");
			Community_TabbedNav_Menu.OVERVIEW.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Go to the Overview page");
			log.info("INFO: Navigate to the Overview page");
			Community_LeftNav_Menu.OVERVIEW.select(ui);
		}

				
		//Verify that the feed is deleted
		logger.strongStep("Verify that the second feed was deleted");
		log.info("INFO: Validate that the second feed is not present on Overview page");
		Assert.assertFalse(driver.isElementPresent("link=" + secondfeed.getTitle()),
							"ERROR: Deleted Second Feed was found");
		
		//Verify that the first feed displays in overview page
		logger.weakStep("Verify that the first feed displays on the Overview page");
		log.info("INFO: Validate the first feed is present on the Overview page");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + firstfeed.getTitle()),
							"ERROR: First Feed title is not found on Overview page");
				
		//Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li>Info:<B> Tests that the filter bar filters by Name, Description and Tag</B></li>
	 *<li><B>Step:</B> [API] Create three communities with unique name, tag and description</li>
	 *<li><B>Step:</B> Use the catalog filter to search filter communities by <B>title</B></li>
	 *<li><B>Verify:</B> The filtered list of communities contains only the expected</li>
	 *<li><B>Step:</B> Use the catalog filter to search filter communities by <B>tag</B></li>
	 *<li><B>Verify:</B> The filtered list of communities contains only the expected</li>
	 *<li><B>Step:</B> Use the catalog filter to search filter communities by <B>description</B></li>
	 *<li><B>Verify:</B> The filtered list of communities contains only the expected</li>
	 *<li><B>Step:</B> [API] Delete each of the communities created by this test</li>
	 */
	@Test(groups = {"level2", "cnx8ui-level2", "bvt", "bvtcloud"})
	public void filterCommunityCatalog() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}					

		
		//Check Gatekeeper value for Catalog UI Updated [6.0 CR3]
		String gk_flag =  Data.getData().gk_catalog_ui_updated_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		if (value) {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so this test can be performed");
			log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so card view selectors will be used");
			
			String rndNum = Helper.genDateBasedRand();
			String comOneName = "filterCommunityCatalog Name Test" + rndNum;
			String comTwoTag = "filterByTag";
			String comThreeDescription = "Community Catalog Filter Bar Test Description";
			String comThreePartialDescription = "Filter Bar Test Description";
			
			// Generate communities with different title, tag and description
			BaseCommunity communityOne = new BaseCommunity.Builder(comOneName)
					.tags(Data.getData().commonTag + Helper.genDateBasedRand())
					.access(Access.PUBLIC)
					.description("Test description for testcase " + testName)
					.build();
			
			BaseCommunity communityTwo = new BaseCommunity.Builder("filterCommunityCatalog Tag Test" + rndNum)
					.tags(comTwoTag)
					.access(Access.PUBLIC)
					.description("Test creating new feeds " + testName)
					.build();
			
			BaseCommunity communityThree = new BaseCommunity.Builder("filterCommunityCatalog Description Test" + rndNum)
					.tags(Data.getData().commonTag + rndNum)
					.access(Access.PUBLIC)
					.description(comThreeDescription)
					.build();
			
			//create communities
			logger.strongStep("Create Community One using API");
			log.info("INFO: Create Community One using API");
			Community comAPI1 = communityOne.createAPI(apiOwner);
			communityOne.setCommunityUUID(comAPI1.getUuid());
			
			logger.strongStep("Create Community Two using API");
			log.info("INFO: Create Community Two using API");
			Community comAPI2 = communityTwo.createAPI(apiOwner);
			communityTwo.setCommunityUUID(comAPI2.getUuid());
			
			logger.strongStep("Create Community Three using API");
			log.info("INFO: Create Community Three using API");
			Community comAPI3 = communityThree.createAPI(apiOwner);
			communityThree.setCommunityUUID(comAPI3.getUuid());
			
			logger.strongStep("Navigate to the 'Public Communities' view");
			log.info("INFO: Navigate to Public Communities view");
			if (isCardView) {
				ui.goToDiscoverCardView();
			} else {
				Community_View_Menu.PUBLIC_COMMUNITIES.select(ui);
			}
			
			//Wait for catalog to update with the new communities
			log.info("INFO: Wait with refresh for catalog to contain our community");
			String communityOneLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(communityOne) : CommunitiesUI.getCommunityLink(communityOne);
			String communityTwoLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(communityTwo) : CommunitiesUI.getCommunityLink(communityTwo);
			String communityThreeLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(communityThree) : CommunitiesUI.getCommunityLink(communityThree);
			ui.fluentWaitPresentWithRefresh(communityThreeLink);
			
			//Select the filter bar and input the title of the first community
			logger.strongStep("Enter a unique community name into the catalog filter");
			ui.applyCatalogFilter(comOneName, isCardView);
			
			//Verify that the correct community is displayed and the others are not
			logger.strongStep("Verify that the appropriate community is displayed");
			log.info("INFO: Validate community one is in Public Communities view");
			Assert.assertTrue(ui.isElementPresent(communityOneLink),
			  					"ERROR: community one is not present in public communities view");
			
			log.info("INFO: Validate community two is not in Public Communities view");
			Assert.assertFalse(ui.isElementPresent(communityTwoLink),
			  					"ERROR: community two is present in public communities view");
			
			log.info("INFO: Validate community three is not in Public Communities view");
			Assert.assertFalse(ui.isElementPresent(communityThreeLink),
			  					"ERROR: community three is present in public communities view");
			
			//Select the filter bar and input the tag of the second community
			logger.strongStep("Enter a unique community tag into the catalog filter");
			ui.applyCatalogFilter(comTwoTag, isCardView);
			
			//Verify that the correct community is displayed and the others are not
			logger.strongStep("Verify that the appropriate community is displayed");
			log.info("INFO: Validate community two is in Public Communities view");
			Assert.assertTrue(ui.isElementPresent(communityTwoLink),
			  					"ERROR: community two is not present in public communities view");
			
			log.info("INFO: Validate community one is not in Public Communities view");
			Assert.assertFalse(ui.isElementPresent(communityOneLink),
			  					"ERROR: community one is present in public communities view");
			
			log.info("INFO: Validate community three is not in Public Communities view");
			Assert.assertFalse(ui.isElementPresent(communityThreeLink),
			  					"ERROR: community three is present in public communities view");
			
			//Select the filter bar and input the partial description of the third community
			logger.strongStep("Enter a unique community partial description into the catalog filter");
			ui.applyCatalogFilter(comThreePartialDescription, isCardView);

			
			//Verify that the correct community is displayed and the others are not
			logger.strongStep("Verify that the appropriate community is displayed");
			log.info("INFO: Validate community three is in Public Communities view");
			Assert.assertTrue(ui.isElementPresent(communityThreeLink),
			  					"ERROR: community three is not present in public communities view");
			
			log.info("INFO: Validate community one is not in Public Communities view");
			Assert.assertFalse(ui.isElementPresent(communityOneLink),
			  					"ERROR: community one is present in public communities view");
			
			log.info("INFO: Validate community three is not in Public Communities view");
			Assert.assertFalse(ui.isElementPresent(communityTwoLink),
			  					"ERROR: community two is present in public communities view");
			
			//Delete the communities created in this test case
			log.info("INFO: Removing the communities using the API");
			apiOwner.deleteCommunity(comAPI1);
			apiOwner.deleteCommunity(comAPI2);
			apiOwner.deleteCommunity(comAPI3);

		} else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so this test will be skipped");
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Uploads a file to a community</li>
	*<li><B>Step:</B>Create a community with name, tag and description</li>
	*<li><B>Step:</B>If community is onprem then add a handle</li>
	*<li><B>Step:</B>Select a community type - Public</li>
	*<li><B>Step:</B>Add a member to the community</li>
	*<li><B>Step:</B>Upload a file to the community</li>
	*<li><B>Verify:</B>Check that the community has being created - code checks for the description enter above and some widgets in the community</li>
	*<li><B>Verify:</B>Check the file was uploaded</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "level2", "bvt", "bvtcloud","regressioncloud"})
	public void createPublicCommunityAddFile() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();
		
		String testName = ui.startTest();
						
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(member).build();
		//create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);	
				
		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		//Check for the Tabbed Nav GK flag
		gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		value = ui.checkGKSetting(gk_flag);
		
		//Select Files from menu
		if(value){
			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed navigation menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		}else{
			logger.strongStep("Select Files from the left navigation menu");
			log.info("INFO: Select Files from the left navigation menu");
			Community_LeftNav_Menu.FILES.select(ui);
		}
		
		//Upload Community-owned file
		logger.strongStep("Upload a Community-owned file");
		fileA.upload(fileUI);
		
		//Delete the Community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info:</B>Add and remove members from a community</li>
	*<li><B>Step:</B>Create a community with name, tag and description</li>
	*<li><B>Step:</B>If community is onprem then add a handle</li>
	*<li><B>Step:</B>Select a community type - Private</li>
	*<li><B>Step:</B>Add a member to the community</li>
	*<li><B>Step:</B>Use Search users to locate the member that you are adding to the community
	*<li><B>Step:</B>Add additional members to the community</li>
	*<li><B>Step:</B>Remove 1 member from the community</li>
	*<li><B>Verify:</B>Check that the community has being created - code checks for the description enter above and some widgets in the community</li>
	*<li><B>Verify:</B>Check the member was removed</li>
	*</ul>
	*/
	//TODO Re-add groups "level2" and "infra" to test once the following is resolved
	//TODO 153207: Type ahead control intermittently disappears leading to intermittent pipeline failures
	
	@Test(groups = {"bvt","regressioncloud"})
	public void removeMemberPrivateCommunity() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testLookAheadUser2 = cfg.getUserAllocator().getUser();
		User testLookAheadUser3 = cfg.getUserAllocator().getUser();
		User testLookAheadUser4 = cfg.getUserAllocator().getUser();
		
		Member newMember1 = new Member(CommunityRole.MEMBERS, testLookAheadUser2);
		Member newMember2 =	new Member(CommunityRole.OWNERS, testLookAheadUser3);	
		Member newMember3 =	new Member(CommunityRole.MEMBERS, testLookAheadUser4);
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.RESTRICTED)
													.description("Test description for testcase " + testName)
													.shareOutside(false)
													.addMember(member)
													.build();

		//create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		//Go to the Members tab
		logger.strongStep("Navigate to Members tab");
		log.info("INFO: Navigating to Members tab");	
		ui.gotoMembers();
		
		//Adding 3 members to the community
		logger.strongStep("Add " + newMember1.getUser().getDisplayName() + " to the Community");
		log.info("INFO: Add " + newMember1.getUser().getDisplayName() + " to the Community");	
		ui.addMemberCommunity(newMember1);
		ui.clickSaveAddMember();

		//Add new owner to existing community
		logger.strongStep("Add " + newMember2.getUser().getDisplayName() + " to the Community");
		log.info("INFO: Add " + newMember2.getUser().getDisplayName() + " to the Community");	
		ui.addMemberCommunity(newMember2);
		ui.clickSaveAddMember();

		//Add new member to existing community
		logger.strongStep("Add " + newMember3.getUser().getDisplayName() + " to the Community");
		log.info("INFO: Add " + newMember3.getUser().getDisplayName() + " to the Community");	
		ui.addMemberCommunity(newMember3);
		ui.clickSaveAddMember();
	
		//Verify that the members exist
		logger.weakStep("Validate that " + testUser.getDisplayName() + " is listed in the Member section");
		log.info("INFO: Validate that " + testUser.getDisplayName() + " is listed in Member section");
		Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + "\n"),
						"ERROR: Display name not found for " + testUser.getDisplayName());
		
		logger.weakStep("Validate that " + member.getUser().getDisplayName() + " is listed in the Member section");
		log.info("INFO: Validate that " + member.getUser().getDisplayName() + " is listed in Member section");
		Assert.assertTrue(driver.isTextPresent(member.getUser().getDisplayName() + "\n"),
						"ERROR: Display name not found for " + member.getUser().getDisplayName());
		
		logger.weakStep("Validate that " + newMember1.getUser().getDisplayName() + " is listed in the Member section");
		log.info("INFO: Validate that " + newMember1.getUser().getDisplayName() + " is listed in Member section");
		Assert.assertTrue(driver.isTextPresent(newMember1.getUser().getDisplayName() + "\n"),
						"ERROR: Display name not found for " + newMember1.getUser().getDisplayName());
		
		logger.weakStep("Validate that " + newMember2.getUser().getDisplayName() + " is listed in the Member section");
		log.info("INFO: Validate that " + newMember2.getUser().getDisplayName() + " is listed in Member section");
		Assert.assertTrue(driver.isTextPresent(newMember2.getUser().getDisplayName() + "\n"),
						"ERROR: Display name not found for " + newMember2.getUser().getDisplayName());
		
		logger.weakStep("Validate that " + newMember3.getUser().getDisplayName() + " is listed in the Member section");
		log.info("INFO: Validate that " + newMember3.getUser().getDisplayName() + " is listed in Member section");
		Assert.assertTrue(driver.isTextPresent(newMember3.getUser().getDisplayName() + "\n"),
						"ERROR: Display name not found for " + newMember3.getUser().getDisplayName());

		//remove a member 
		logger.strongStep("Delete " + newMember3.getUser().getDisplayName());
		log.info("INFO: Delete " + newMember3.getUser().getDisplayName());		
		ui.removeMemberCommunity(newMember3);
		
		//assert user is not longer there
		logger.weakStep("Validate that the deleted user is no longer in the list of users");
		log.info("INFO: Validate that the deleted user is no longer in the list of users");
		Assert.assertTrue(driver.isTextNotPresent(newMember3.getUser().getDisplayName() + "\n"), 
						 "ERROR: User name " + newMember3.getUser().getDisplayName() + " is still there.");
		
		//Delete the Community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info:</B>Make a moderated community with members</li>
	*<li><B>Step:</B>Create a community with name, tag and description</li>
	*<li><B>Step:</B>If community is onprem then add a handle</li>
	*<li><B>Step:</B>Select a community type - Moderated</li>
	*<li><B>Step:</B>Add a member to the community</li>
	*<li><B>Verify:</B>Check that the community is appearing in the correct view</li>
	*<li><B>Verify:</B>Check that the community has being created - code checks for the description enter above and some widgets in the community</li>
	*</ul>
	*/
	@Test(groups = {"level2", "cnx8ui-level2", "bvt","regressioncloud"})
	public void moderatedCommunityViews() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.MODERATED)
													.description("Test description for testcase " + testName)
													.addMember(member)
													.build();
		
	
		//create community
		logger.strongStep("Create a Community using API");
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}	
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		if (isCardView) {
			// Trending: Name (ascending) (has all public communities there, but not sorted by update_date...)
			logger.strongStep("Navigate to the 'Trending' view");
			log.info("INFO: Navigate to Trending view");
			ui.goToDiscoverCardView();
		} else {
			logger.strongStep("Navigate to the 'Public Communities' view");
			log.info("INFO: Navigate to Public Communities view");
			Community_View_Menu.PUBLIC_COMMUNITIES.select(ui);
		}
		
		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain the community");
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		ui.fluentWaitPresentWithRefresh(communityLink);
		
		//Validate moderated community is listed in public communities
		logger.weakStep("Validate that the moderated community is listed in Public Communities view");
		log.info("INFO: Validate moderated community is in Public Communities view");
		ui.applyCatalogFilter(community.getName(), isCardView);
		Assert.assertTrue(ui.fluentWaitPresent(communityLink),
		  				  "ERROR: Moderated community is not present in Public Communities view");
		
		logger.strongStep("Navigate to the 'I'm am member' view");
		log.info("INFO: Navigate to the 'I'm a Member' view");
		if (isCardView) {
			ui.goToMemberCardView();
			
		} else {
			Community_View_Menu.IM_A_MEMBER.select(ui);
		}
		
		
		//Validate moderated community is in I'm a member
		logger.weakStep("Validate that the Moderated Community is not in the 'I'm a Member' view");
		log.info("INFO: Validate that the Moderated Community is in not the 'I'm a Member' view");		
		Assert.assertTrue(ui.fluentWaitTextNotPresent(communityLink), "ERROR: Moderated Community is present in the 'I'm a Member' view");
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Open community
		logger.strongStep("Open the Community via link");
		log.info("INFO: Open the Community via link");
		ui.goToDefaultIamOwnerView(isCardView);
		ui.clickLinkWait(communityLink);
		
		logger.weakStep("Validate that the community description is present");
		log.info("INFO: Validate that the Community description is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(community.getDescription()),
						 "ERROR: Unable to find the Community description");
		
		//Logout of Communities
		ui.logout();
		
		//Delete the community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Info:</B>Access a community from a different user</li>
	*<li><B>Step:</B>Create a public community with name, tag and description</li>
	*<li><B>Step:</B>If community is onprem then add a handle</li>
	*<li><B>Step:</B>Select a community type</li>
	*<li><B>Step:</B>Add a member to the community</li>
	*<li><B>Step:</B>Logout of the community and then login as the second member</li>
	*<li><B>Step:</B>Depending on the type of community - go to the view (Public/Private/Moderated)</li>
	*<li><B>Verify:</B>Check that the community is present - code checks for the description enter above and some widgets in the community</li>
	*</ul>
	*/
	@Test(groups = {"regression", "bvt","regressioncloud"})
	public void publicCommunityViews() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		// The member and API owner users that are allocated in setUpClass are deallocated after
		// each test, so allocate new users here to guarantee testUserPublic is unique
		User ownerUser = cfg.getUserAllocator().getUser();
		User memberUser = cfg.getUserAllocator().getUser();
		Member commMember = new Member(CommunityRole.MEMBERS, memberUser);
		APICommunitiesHandler myApiOwner = new APICommunitiesHandler(serverURL, ownerUser.getAttribute(cfg.getLoginPreference()), ownerUser.getPassword());
		
		User testUserPublic = cfg.getUserAllocator().getUser();
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(commMember)
													.build();
		
	
		//create community
		logger.strongStep("Create a Community using API");
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(myApiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of Community");
		community.getCommunityUUID_API(myApiOwner, comAPI);
		
		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}	
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Validate public community is not in Im a member for a non member
		logger.strongStep("Navigate to the 'I'm a Member' view");
		log.info("INFO: Navigate to the 'I'm a Member' view");
		if (isCardView) {
			ui.goToMemberCardView();
		} else {
			Community_View_Menu.IM_A_MEMBER.select(ui);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			myApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		logger.weakStep("Validate that the public Community is not in the 'I'm a Member' view for non members");
		log.info("INFO: Validate that the public Community is not in the 'I'm a Member view for non members");
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		Assert.assertFalse(driver.isElementPresent(communityLink),
		  					"ERROR: Public Community is present in the 'I'm a Member' view for a non member");

		//Validate public community is listed in public communities
		logger.strongStep("Validate that the public Community is listed in public Communities");
				
		log.info("INFO: Navigate to Public Communities view");
		if (isCardView) {
			ui.goToDiscoverCardView();
		} else {
			Community_View_Menu.PUBLIC_COMMUNITIES.select(ui);
		}
		
		
		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
		//try one more time
		if(!ui.fluentWaitPresent(communityLink))
			ui.fluentWaitPresentWithRefresh(communityLink);
		
		log.info("INFO: Validate public community is in Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresent(communityLink),
		  					"ERROR: Public community is not present in public communities view");
		
		//Open community
		logger.strongStep("Open the Community via link");
		log.info("INFO: Open the Community via link");
		ui.clickLinkWait(communityLink);
		
		logger.weakStep("Validate that the Community description is present");
		log.info("INFO: Validate that the Community description is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(community.getDescription()),
						 "ERROR: Unable to find the Community description");
		
		//Logout of Communities
		ui.logout();
		
		//Delete the Community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Info:</B>Make a private community</li>
	*<li><B>Step:</B>Create a community with name, tag and description</li>
	*<li><B>Step:</B>If community is onprem then add a handle</li>
	*<li><B>Step:</B>Select a community type private</li>
	*<li><B>Step:</B>Add a member to the community</li>
	*<li><B>Step:</B>Logout of the community and then login as a member</li>
	*<li><B>Verify:</B>Check the community is visible in I'm a Member</li>
	*<li><B>Verify:</B>Check the community isn't shown in Public Communities</li>
	*</ul>
	*/
	@Test(groups = {"level2", "cnx8ui-level2", "bvt","regressioncloud"})
	public void restrictedCommunityViews() throws Exception {	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.RESTRICTED)
													.description("Test description for testcase " + testName)
													.addMember(member)
													.shareOutside(false)
													.build();
		

		//create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}	
		
		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Log Out of Communities");
		ui.logout();
		//ui.close(cfg);
		driver.close();
		
		//Log in as Community member
		logger.strongStep("Log In as another member of the Community as: " + member.getUser().getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member.getUser());
		ui.waitForPageLoaded(driver);
		
		//Navigate to publicCommunity view
		// Update 12/12/18: Navigation step already executed in restrictedNotInPublic(), removed redundancy here.
		
		//Validate restricted community is not list in public communities
		logger.strongStep("Validate that the restricted Community is not listed in public Communites");
		ui.restrictedNotInPublic(community, isCardView);
		
		//Validate restricted community is in I'm a member
		logger.strongStep("Navigate to the 'I'm a Member' view");
		log.info("INFO: Navigate to the 'I'm a Member' view");
		if (isCardView) {
			ui.goToMemberCardView();
		} else {
			Community_View_Menu.IM_A_MEMBER.select(ui);
		}
		
		//check to confirm that Im a member view is the current page.
		if(!driver.getCurrentUrl().endsWith("mycommunities"))//check url.
		{
			logger.strongStep("Failed to navigate to the 'I'm a Member' view, attempting alternative method");
			log.info("INFO: Failed to navigate to the 'I'm a Member' view, attempting alternative method");
			if (isCardView) {
				// uhh... do it again I guess? "alternative method"
				ui.goToMemberCardView();
			} else {
				ui.clickLinkWait(CommunitiesUIConstants.MemberCommunitiesView);
			}
		}
		
		String restrictedCommunityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Wait for catalog to update with the new community
		logger.strongStep("Wait for the catalog to update with new Community");
		log.info("INFO: Wait for the catalog to refresh with our Community");
		ui.fluentWaitPresentWithRefresh(restrictedCommunityLink);
		
		logger.weakStep("Validate that the restricted Community is in 'I'm a Member' view");
		log.info("INFO: Validate that therestricted Community is in 'I'm a Member' view");
		Assert.assertTrue(ui.fluentWaitPresent(restrictedCommunityLink),
		  "ERROR: Restricted Community is not present in 'I'm a Member' view");

		//Open community
		logger.strongStep("Open Communities and login");
		log.info("INFO: Open the Community via link");
		ui.clickLinkWait(restrictedCommunityLink);
		
		logger.weakStep("Validate that the restricted Community description is present");
		log.info("INFO: Validate that the Community description is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(community.getDescription()),
						 "ERROR: Unable to find the Community description");
		
		//Delete the Community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}	

	
	/**
	*<ul>
	*<li><B>Info:</B>Make a community and validate it was created</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Verify:</B>Check the community has been created</li>
	*</ul>
	 */
	@Test(groups = {"bvt"})
	public void createCommunity() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(member).build();


		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Create Community
		logger.strongStep("Create a Community");
		community.createFromDropDown(ui);
		
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.endTest();
		
	}
	/**
	*<ul>
	*<li><B>Info:</B>Deletes a community</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>If community is onprem then add a handle</li>
	*<li><B>Step:</B>Select a community type - Public</li>
	*<li><B>Step:</B>Delete Community</li>
	*<li><B>Verify:</B>Check that the community has being deleted</li>
	*</ul>
	*/
	
	@Test(groups = {"level2", "bvt","regressioncloud"},enabled=true)
	public void deletePublicCommunity() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.description("Test description for testcase " + testName)
													.access(Access.PUBLIC).build();


		//Load component and login
		if(cfg.getUseNewUI())							
		{					
			ui.loadComponent(Data.getData().ComponentHomepage);				
			ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
			AppNavCnx8.COMMUNITIES.select(commonUI);				
		}					
		else					
		{					
			ui.loadComponent(Data.getData().ComponentCommunities);				
			ui.login(testUser);				
		}					

		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//create community
		if (value)
		{
			log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
			
			if (isCardView) {
				log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so expect a 'Start a Community' dropdown in the Card View");
				logger.strongStep("Create a Community from the Dropdown Menu in the Card View");
				log.info("INFO: Create a Community");
				community.createFromDropDownCardView(ui);
			} else {
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' dropdown");
				logger.strongStep("Create a Community from the Dropdown Menu");
				log.info("INFO: Create a Community");
				community.createFromDropDown(ui);
			}
		} else { 	
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' button");
			logger.strongStep("Create a Community");
			log.info("INFO: Create a Community");
			community.create(ui);
		}
         ui.waitForCommunityLoaded();
		//Delete Community
		logger.strongStep("Delete the Community");
		community.delete(ui, testUser);
		ui.waitForPageLoaded(driver);
		
		//Verify community deleted in "I'm an Owner" view
		logger.weakStep("Verify that the Community is deleted");
		log.info("Verify that the Community was deleted");
		log.info("Verify the deletion in the 'I'm an Owner' view");
		if (isCardView) {
			ui.goToOwnerCardView();
			ui.waitForPageLoaded(driver);
			// assert that the <p> element does not exist
			driver.changeImplicitWaits(5);
			Assert.assertFalse(driver.isElementPresent("css=div[aria-label='"+community.getName()+"']"),
					"ERROR: Community '" + community.getName() + "' shows up in 'I'm an Owner' view after deletion.");
			driver.turnOnImplicitWaits();
		} else {
			Community_View_Menu.IM_AN_OWNER.select(ui);
			ui.waitForPageLoaded(driver);
			Assert.assertTrue(ui.fluentWaitTextNotPresent(community.getName()),
					"ERROR: Community '" + community.getName() + "' shows up in 'I'm an Owner' view after deletion.");
		}
		
		// Verify deletion in Public Communities view
		logger.strongStep("Verify that community is deleted in public communities view");
		ui.deletedNotInPublic(community, isCardView);
		
		log.info("Community deletion verified.");
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info:</B>Use the Help Window</li>
	*<li><B>Step:</B>Click on the About link</li>
	*<li><B>Step:</B>Open the Help Window</li>
	*<li><B>Verify:</B>Check the Help Window has the correct information and was loaded properly</li>
	*<li><B>Step:</B>Close the window</li>
	*</ul>
	*/
	//@Test(groups = {"level2", "bvt","regressioncloud"})
	public void aboutCommunity() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
				
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Verify the About and Help page/window
		logger.strongStep("Verify that the 'About' and 'Help' page/window Title contains Communities");
		ui.communitiesHelpAndAbout();
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Access My Organization Communities page anonymously</li>
	*<li><B>Step:</B> Open Communities URL</li>
	*<li><B>Verify:</B> The URL redirects to My Organization Communities</li>
	*<li><B>Verify:</B> My Organization Communities displays in the left navigation</li>
	*<li><B>Verify:</B> Start a Community button is present</li>
	*</ul>
	*Note: the cloud does not support anon access
	*/
	@Test(groups = { "level2", "cnx8ui-level2", "bvt", "mt-exclude", "bvtonprem"})
	public void anonAccessPublicCommunitiesView() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		//Get communities' public URL
		String CommunitiesURL = Data.getData().ComponentCommunities.split("login")[0];
		
		ui.startTest();
		logger.strongStep("Load Communities but do not Log In");
		log.info("INFO: Load Communities component");
		ui.loadComponent(CommunitiesURL);
		
		logger.strongStep("Toggle to New UI as "+cfg.getUseNewUI());
		log.info("INFO: Toggle to New UI as "+cfg.getUseNewUI());
		commonUI.toggleNewUI(cfg.getUseNewUI());
		
		//Check Gatekeeper value for Restricted But Listed (RbL) setting
		String gk_flag = "communities-list-restricted";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		// check 6.0 CR5 Card Updated gatekeeper
		String gk_flag_card_updated = Data.getData().gk_catalog_card_updated;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardUpdated = ui.checkGKSetting(gk_flag_card_updated);
		log.info("INFO: Gatekeeper flag " + gk_flag_card_updated + " is set to " + isCardUpdated);

		if (value)
		{
			if (isCardUpdated) {
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect 'Discover' string");

				logger.weakStep("Validate that the page title contains Discover");
				log.info("INFO: Validate that the page title contains Discover");
				String pageTitle = driver.getTitle();
				Assert.assertTrue(pageTitle.contains("Discover"),
						"ERROR: Page title: '" + pageTitle + "', does not contain Discover");
			} else {
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect 'My Organization Communities' string");

				logger.weakStep("Validate that the page title contains My Organization Communities");
				log.info("INFO: Validate that the page title contains My Organization Communities");
				String pageTitle = driver.getTitle();
				Assert.assertTrue(pageTitle.contains("My Organization Communities"),
						"ERROR: Page title: '" + pageTitle + "', does not contain My Organization Communities");
			}

			if (isCardView) {
				logger.weakStep("Validate that \"Discover\" is visible in the top nav bar in the Card View");
				log.info("INFO: Validate that \"Discover\" is visible in the top nav bar in the Card View");
				Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.topNavDiscoverCardView),
						"ERROR: \"Discover\" was not found");
			} else {
				logger.weakStep("Validate that My Organization Communities is visible in left navigation menu");
				log.info("INFO: Validate that My Organization Communities is visible in left navigation menu");
				Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.leftNavPublicCommunities),
						"ERROR: My Organization Communities was not found");
			}

		} else { 	
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect 'Public Communities' string");
			logger.weakStep("Validate that the page title contains public Communities");
			log.info("INFO: Validate that the page title contains public Communities");
			String pageTitle = driver.getTitle();
			Assert.assertTrue(pageTitle.contains("Public Communities"), 
					"ERROR: Page title: '" + pageTitle + "', does not contain public Communities");

			if (isCardView) {
				logger.weakStep("Validate that \"Discover\" is visible in the top nav bar in the Card View");
				log.info("INFO: Validate that \"Discover\" is visible in the top nav bar in the Card View");
				Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.topNavDiscoverCardView),
						"ERROR: \"Discover\" was not found");
			} else {
				logger.weakStep("Validate that the public Communities left navigation menu is visible");
				log.info("INFO: Validate that the public Communities left navigation menu is visible");
				Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.leftNavPublicCommunities),
						"ERROR: Public Communities was not found");
			}
		
		}
		
		//Check Gatekeeper value for Communities Copy Community setting
		gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		value = ui.checkGKSetting(gk_flag);
		
		if (value)
		{
			if (isCardView) {
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' dropdown in the Card View");
				logger.weakStep("Validate that the Start a Community Dropdown displays in the Card View");
				log.info("INFO: Validate that the Start a Community Dropdown displays in the Card View");
				ui.clickLink(CommunitiesUIConstants.StartACommunityDropDownCardView);
				logger.weakStep("Validate that the Start a Community button displays");
				log.info("INFO: Validate that the Start a Community button displays");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.StartACommunityFromDropDownCardView),
						"ERROR: Start A Community Dropdown was not found");
			} else {
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' dropdown");
				logger.weakStep("Validate that the Start a Community Dropdown displays");
				log.info("INFO: Validate that the Start a Community Dropdown displays");
				ui.clickLink(CommunitiesUIConstants.StartACommunityDropDown);
				logger.weakStep("Validate that the Start a Community button displays");
				log.info("INFO: Validate that the Start a Community button displays");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.StartACommunityFromDropDown),
						"ERROR: Start A Community Dropdown was not found");
			}
		} else { 	
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' button");
			logger.weakStep("Validate that the Start a Community button displays");
			log.info("INFO: Validate that the Start a Community button displays");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.StartACommunity),
					"ERROR: Start A Community button was not found");
		
		}
				
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Restricted but Listed BVT test</li>
	 * <li><B>Info:</B> Test will verify both a Listed & Non-Listed community appears in the I'm an Owner view, but only the Listed one appears in My Organization Communities view</li>
	 * <li><B>Step:</B> Create a restricted but listed community </li>
	 * <li><B>Step:</B> Create another community that is NOT restricted but listed </li>
	 * <li><B>Step:</B> Navigate to the I'm an Owner catalog view </li>
	 * <li><B>Verify:</B> Verify both the listed & non-listed communities appear in the view </li>
	 * <li><B>Step:</B> Navigate to the My Organization Communities catalog view</li>
	 * <li><B>Verify:</B> Verify only the restricted but listed community appears in the My Organization Communities view</li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
	 *</ul>
	 */			

	@Test(groups = {"bvt","level2", "cnx8ui-level2"})
	public void rblCatalogViewsListedAndNonlisted() throws Exception {

			String rndNum = Helper.genDateBasedRand();
	
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder("Internal Restricted community - LISTED " + rndNum)
														.access(Access.RESTRICTED)	
														.description("Listed community - should appear in My Organization Communities")
														.rbl(true)													
			                                            .shareOutside(false)
			                                            .build();
			                                            
			BaseCommunity community2 = new BaseCommunity.Builder("Internal Restricted community - NON-LISTED " + rndNum)
			                                            .access(Access.RESTRICTED)	
			                                            .description("Non-Listed community - should NOT appear in My Organization Communities")
			                                            .rbl(false)
			                                            .shareOutside(false)
			                                            .build();
			
			//create the Listed community
			log.info("INFO: Create a Community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//get the community UUID
			log.info("INFO: Get the UUID of the first Community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			
			//create the Non-Listed community
			log.info("INFO: Create a new Community using API");
			Community comAPI2 = community2.createAPI(apiOwner);
					
			//get the community UUID
			log.info("INFO: Get the UUID of the new Community");
			community2.getCommunityUUID_API(apiOwner, comAPI2);		
		
			log.info("Load Communities,Log In as: " + testUser.getDisplayName() + " and Toggle to New UI as "+cfg.getUseNewUI());
			if(cfg.getUseNewUI())							
			{					
				ui.loadComponent(Data.getData().ComponentHomepage);				
				ui.loginAndToggleUI(testUser,cfg.getUseNewUI());				
				AppNavCnx8.COMMUNITIES.select(commonUI);				
			}					
			else					
			{					
				ui.loadComponent(Data.getData().ComponentCommunities);				
				ui.login(testUser);				
			}		      
			
			// check for 6.0 CR4 Catalog Card View
			String gk_flag_card = Data.getData().gk_catalog_card_view;
			log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
			boolean isCardView = ui.checkGKSetting(gk_flag_card);
			log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
			
			//navigate to the I'm an Owner catalog view
			log.info("INFO: Navigate to the 'I'm an Owner' catalog view");
			if (isCardView) {
				ui.goToOwnerCardView();
			} else {
				Community_View_Menu.IM_AN_OWNER.select(ui);
			}
			
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			String community2Link = isCardView ? CommunitiesUI.getCommunityLinkCardView(community2) : CommunitiesUI.getCommunityLink(community2);
			
			//verify the Listed community appears in the view
			log.info("INFO: Verify that the Listed Community appears in the 'I'm an Owner' view");
			ui.fluentWaitPresentWithRefresh(communityLink);
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR: The Community " + community.getName() + " does NOT appear in the 'I'm an Owner' view");
			
			//verify the Non-Listed community appears in the view
			log.info("INFO: Verify the Non-Listed Community appears in the 'I'm an Owner' view");
			ui.fluentWaitPresentWithRefresh(community2Link);
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(community2Link),
					"ERROR: The Community " + community2.getName() + " does NOT appear in the 'I'm an Owner' view");
			
			//navigate to the My Organization Communities catalog view
			log.info("INFO: Navigate to the My Organization Communities view");
			if (isCardView) {
				ui.goToDiscoverCardView();
			} else {
				ui.clickLinkWait(CommunitiesUIConstants.myOrgCommunitiesView);
			}
			
			//verify the Listed community appears in the My Organization Communities view
			log.info("INFO: Verify the Listed Community appears in the My Organization Communities view");
			ui.fluentWaitPresentWithRefresh(communityLink);
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR: The Community " + community.getName() + " does NOT appear in the My Organization Communities view");
			
			///verify the Non-Listed community does NOT appear in the My Organization Communities view
			log.info("INFO: Verify the Non-Listed Community does NOT appear in the My Organization Communities view");
			Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(community2Link),
					"ERROR: The Community " + community2.getName() + " appears in the My Organization Communities view");
			
			//Delete the Listed community created in this test case
			log.info("INFO: Removing the Listed community using the API");
			apiOwner.deleteCommunity(comAPI);
	
			//Delete the Non-Listed community created in this test case
			log.info("INFO: Removing the Non-Listed community using the API");
			apiOwner.deleteCommunity(comAPI2);
					
			ui.endTest();
	
	}
		
	/**
	*<ul>
	*<li><B>Info:</B>Use the Help Window anonymously</li>
	*<li><B>Step:</B>Click on the About link</li>
	*<li><B>Step:</B>Open the Help Window</li>
	*<li><B>Verify:</B>Check the Help Window has the correct information and was loaded properly</li>
	*<li><B>Step:</B>Close the window</li>
	*</ul>
	*/
	@Test(groups = {"infra"})
	public void anonymousLogin() throws Exception {
		
		HCBaseUI hc = new HCBaseUI(driver);
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
				
		//Load component but do not login
		logger.strongStep("Load Communities but do not Log In");
		ui.loadComponent(communitiesURI);
		
		if(hc.isElementVisibleWd(By.id("top-navigation"), 5))
		{
		log.info("INFO: Intentionally toggle after navigation");
		hc.waitForElementVisibleWd(By.id("theme-switcher-wrapper"), 4);
		hc.clickLinkWd(By.id("theme-switcher-wrapper"), "new UI toggle switch");
		hc.clickLinkWithJavaScriptWd(hc.findElement(By.cssSelector("#theme_switcher_options_modal_switch input")));
		hc.findElement(By.id("options_modal_save_button")).click();
		}
		
		//Verify the About and Help page/window
		logger.strongStep("Verify that the 'About' and 'Help' pages/windows exist");
		ui.communitiesHelpAndAbout();
				
		ui.endTest();
	}
		
	/**
	 *<ul>
	 *<li><B>Info: Inspect left navigation in the Community Catalog</B></li>
	 *<li><B>Steps: </B>Login to Communities</li>
	 *<li><B>Verify: </B>The six basic views display in the left navigation panel</li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud","bvtcloud", "smokecloud", "smokeonprem"} )
	public void communityCatalog() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String orgName;
		ui.startTest();
	
		//Load component and login	
		logger.strongStep("Load communities and login as: " +testUser.getDisplayName());
		log.info("INFO: Load component and login as: " +testUser.getDisplayName());	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser); 
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Determine organization name if IBM Connections change to My Organization
		orgName = ui.getNavOrgName();
		
		ui.goToDefaultCatalogView();
		
		if (isCardView){
			
			// for CR4, check the Filter view is expanded.
			if (!driver.isElementPresent( CommunitiesUIConstants.CatalogCardFilterExpanded)){
				log.info("INFO: Open Filter");
				ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);
			}
		}
		
		//Validate basic left menu items
		logger.weakStep("Verify left menu item 'I'm an owner' is present");
		log.info("INFO: Validate left menu item 'I'm an Owner'");
		Assert.assertTrue(driver.isTextPresent("I'm an Owner"), "I'm an Owner not found");
	

		logger.weakStep("Verify left menu item 'I'm a member' is present");
		log.info("INFO: Validate left menu item 'I'm a Member'");
		Assert.assertTrue(driver.isTextPresent("I'm a Member"), "I'm a Member not found");
		

		logger.weakStep("Verify left menu item 'I'm following' is present ");
		log.info("INFO: Validate left menu item 'I'm Following'");
		Assert.assertTrue(driver.isTextPresent("I'm Following"), "I'm Following not found");
		
		if (isCardView){
			
			logger.weakStep("Verify 'Invited' tab is present");
			log.info("INFO: Validate 'Invited' tab");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.topNavInvitedCardView), "Invited TAB not found");

			logger.weakStep("Verify 'Discover' tab is present");
			log.info("INFO: Validate 'Discover' tab");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.topNavDiscoverCardView), "Discover TAB not found");

		}
		else {
        	logger.weakStep("Verify left menu item 'I'm invited' is present");
        	log.info("INFO: Validate left menu item 'I'm Invited'");
        	Assert.assertTrue(driver.isTextPresent("I'm Invited"), "I'm Invited not found");
        
        	logger.weakStep("Verify left menu item '" + orgName + " Communities' is present");
        	log.info("INFO: Validate left menu item '" + orgName + " Communities'");
        	Assert.assertTrue(driver.isTextPresent(orgName + " Communities"), orgName + " Communities not found");
	
			logger.weakStep("Verify left menu item 'Trash' is present");
			log.info("INFO: Validate left menu item 'Trash'");
			Assert.assertTrue(driver.isTextPresent("Trash"), "Trash not found");
		}
	
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: Check search index ran within an hour</B></li>
	 *<li><B>Steps: 
	 *<li><B>Search for a term on global search page</B></li>
	 *<li><B>Verify: Time stamp is no older than an hour</B> </li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud","bvtcloud", "smokecloud", "smokeonprem"})
	public void searchIndex(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Allocate user
		User user = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		//load global search
		logger.strongStep("Load global search as: " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(user);
		
		//Search for activity with tag none
		logger.strongStep("Search for an Activity with tag 'none'");
		ui.searchCommunities("none");
		
		//Verify indexer ran within an hour
		logger.strongStep("Verify indexer ran within an hour");
		long indexerRan = driver.getBrowserDatetime().getTimeInMillis() - sUI.getLastRunTimeOfIndexer().getTimeInMillis();
		long ranMinutesAgo = indexerRan / 1000 / 60;
		log.info("INFO: indexer ran " + ranMinutesAgo + " minutes ago");
		
		// Nov. 18, 2014 Change to 120 minutes
		logger.weakStep("Indexer ran more than 2 hours ago");
		Assert.assertTrue(indexerRan < 7200000, "Indexer ran more than 2 hours ago. Indexer ran " + ranMinutesAgo + " minutes ago.");
		
		sUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: Create a Community and verify the UI displays it</B></li>
	 *<li><B>Steps: </B></li>
	 *<li><B>Create a Community Community Type: (Access: Public, Tags, Description, and a Member(Role:Member)</B></li>
	 *<li><B>Verify: Verify that the Community is created</B> </li>
	 *<li><B>Steps:  Log out
	 *<li><B>Steps:  Log in back as the same user
	 *<li><B>Verify: Verify that community be displayed at the owner's view</B> </li>
	 *</ul>
	 */
	@Test (groups = {"regressioncloud","bvtcloud", "smokecloud", "smokeonprem"})
	public void communityOwnerView() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest() + Helper.genDateBasedRand();
			
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName)
												.access(Access.RESTRICTED)
												.tags(Data.getData().commonTag)
												.description("Test Community for " + testName).build();
			
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load component and Log In");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser); 
		
		//Check Gatekeeper value for Communities Copy Community setting
		String gk_flag = Data.getData().gk_copycomm_flag;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
				
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = Data.getData().gk_catalog_card_view;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = ui.checkGKSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");

		//create community
		if (value)
		{
			if (isCardView) {
				log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so expect a 'Start a Community' dropdown in the Card View");
				logger.strongStep("Create a Community from the Dropdown Menu in the Card View");
				log.info("INFO: Create a Community");
				community.createFromDropDownCardView(ui);
			} else {
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' dropdown");
				logger.strongStep("Create a Community from the Dropdown Menu");
				log.info("INFO: Create a Community");
				community.createFromDropDown(ui);
			}
		} else { 	
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect a 'Start a Community' button");
			logger.strongStep("Create a restricted Community");
			log.info("INFO: Create a restricted Community using the UI");
			community.create(ui);
		}
		
		// verify the community be created
		logger.weakStep("Verify that the Community is created");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.RecentUpdates), 
					"New Community: " + testName + " was created"); 
			
		//Logout
		logger.strongStep("Log Out and close browser");
		log.info("INFO: Log Out and close browser");
		ui.waitForCommunityLoaded();
		ui.logout();		
		ui.close(cfg);
		
		//Load component and login
		logger.strongStep("Load component and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load component and Log In");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser); 

		// change to use Sort by Date for Cloud only
		sortViewByDateForCloud(isOnPrem,isCardView);	
	
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
		// Try to click on the link
		logger.weakStep("Try to click and find the Community");
		try{
			ui.fluentWaitPresent(communityLink);
		} catch (TimeoutException e){
			log.info("INFO: Failed finding the Community the first time, trying again");
			driver.navigate().refresh();
			Assert.assertTrue(ui.fluentWaitPresent(communityLink), 
									"New Created Community " + testName + " was displayed"); 
		}
			
		//delete community
		logger.strongStep("Delete the Community");
		log.info("INFO: Removing the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
			
		ui.endTest();
	}
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test that you can create and conduct a survey</B></li>
	 *<li><B>Step: Create a new community.</B></li> 
	 *<li><B>Step: Enable Survey.</B></li> 
	 *<li><B>Step: Create a survey</B></li>
	 *<li><B>Step: Add question to survey</B></li>
	 *<li><B>Step: Start the survey</B></li>
	 *<li><B>Step: Login as a community member to take the survey</B></li>
	 *<li><B>Step: Owner checks the survey results</B></li>
	 *<li><B>Verify: Validate that the community was created</B></li>
	 *<li><B>Verify: Validate the survey was created</B></li>
	 *<li><B>Verify: Validate the question was added to the survey</B></li>
	 *<li><B>CleanUp: Delete the Community.</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud","bvtcloud"} )
	public void completionOfSurvey() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		List<BaseSurveyQuestion> questions = new ArrayList<BaseSurveyQuestion>();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.build();
	
	
		
		
		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder("Questions", BaseSurveyQuestion.Type.TEXT_ONELINE)
															.build();
		questions.add(question);
		
		BaseSurvey survey = new BaseSurvey.Builder("test")
										  .description("Hello ")
										  .questions(questions)
										  .build();
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	
		question.getOptions();
		
		//Create A Community
		logger.strongStep("Create a Community");
		log.info("INFO: Create a Community");
		community.create(ui);
	
		//Enable Survey
		logger.strongStep("Add the survey");
		log.info("INFO: Adding the " + BaseWidget.SURVEYS.getTitle() + " widget to Community: "+ community.getName());
		community.addWidget(ui, BaseWidget.SURVEYS);
		
		logger.strongStep("Create a survey with a question");
		log.info("INFO: Create a survey with a question");
		survey.create(ui);
	
	    logger.strongStep("Start the survey");
		ui.startSurvey(survey);
	
		Community_LeftNav_Menu.OVERVIEW.select(ui);
		
		
		
		Community_LeftNav_Menu.SURVEYS.select(ui);
		
		logger.strongStep("Click on the survey");
		ui.clickLinkWait("link="+ survey.getName());
		
		//Logout As testUser1
		logger.strongStep("Log Out from the first user");
		log.info("INFO: Log Out from the first user");
		ui.logout();
		ui.close(cfg);
	
		//Login As A Secondary User
		logger.strongStep("Log In as a second user as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Select 'I'm a Member' from the left navigation menu");
		log.info("INFO: Select 'I'm a Member' from left navigation menu");
		Community_View_Menu.IM_A_MEMBER.select(ui);
	
		logger.strongStep("Select the Community link");
		log.info("INFO: Select the Community link");
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
			
		//Take The Survey
		logger.strongStep("Take the survey");
		log.info("INFO: Take the survey");
	
	
		
		//Owner Checks The Results
		logger.strongStep("Owner checks the results of the survey");
		log.info("INFO: Owner checks the results of the survey");
	
		//delete community
		logger.strongStep("Delete the Community");
		log.info("INFO: Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
	
		ui.endTest();
	
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info: Help menu</B></li>
	 *<li><B>Step: Check to see if the Help Menu exists</B></li>
	 *<li><B>Verify: Validate the help nav bar in menus</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud","bvtcloud"} )
	public void HelpMenu() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		//GUI
		//Login
		logger.strongStep("Load Communities and Login as: " + testUser.getDisplayName());
		log.info("INFO: Login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	
		//Validate existence of help menu
		logger.weakStep("Check to see if the 'Help' menu exists");
		log.info("INFO: Check to see if the 'Help' menu exists");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("HELP MENU NAV HERE"),
				  		"ERROR: Unable to find Help menu");
	
		ui.endTest();

	}
	

	/**
	 *<ul>
	 *<li><B>Info: Create a Community and enable survey option</B></li>
	 *<li><B>Steps: 
	 *<li><B>Create a Community Community Type: (Access: Public, Tags, Description</B></li>
	 *<li><B>Enable the surveys option widget</B></li>
	 **<li><B>Verify the surveys widget is enabled</B></li>
	 *<li><B>Verify: The community is created and that you can add the Survey Widget to the community</B> </li>
	 *</ul>
	 *@author Cheryl Wang
	 */
	@Deprecated
	@Test(groups = {"regressioncloud","bvtcloud", "smokecloud"})
	public void communitiesSurvey() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Create a moderated community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.access(defaultAccess)
											.description(Data.getData().commonDescription)
											.build();
		
		
		//create community
		logger.strongStep("Create a Community using API");
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);	
		
		// Enable Survey 
		logger.strongStep("Add a survey widget to the Community");
		log.info("INFO: Adding the " + BaseWidget.SURVEYS.getTitle() + " widget to Community: "+ community.getName());
		ui.addSurveyWidget();
		
		// Verify surveys can now be created
		logger.weakStep("Verifying ability to create a survey is now present");
		log.info("Checking if ability to create a survey is present on the page");
		ui.fluentWaitPresent(CommunitiesUIConstants.CreateaSurvey);
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	

	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test that you can add the media gallery widget and upload a file
	 *to it</B></li>
	 *<li><B>Step: Create a new community.</B></li> 
	 *<li><B>Step: Upload the Media Gallery widget.</B></li> 
	 *<li><B>Step: Upload a file to Media Gallery</B></li>
	 *<li><B>Verify: Validate that the widget was added to the community</B></li>
	 *<li><B>Verify: Validate that the new file exists</B></li>
	 *<li><B>CleanUp: Delete the Community.</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud","bvtcloud"} )
	public void uploadFileToMediaGallery() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
	
		String testName = ui.startTest();
	
	
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
	
		//GUI	
		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
	
		//Create A Community
		logger.strongStep("Create a Community");
		log.info("INFO: Create a Community");
		community.create(ui);	
	
		//Customize community - Add the widget
		logger.strongStep("Customize the Community by adding the Media Gallery widget");
		log.info("INFO: Adding the " + BaseWidget.MEDIA_GALLERY.getTitle()
									+ " widget to Community: " + community.getName());
		ui.addWidget(BaseWidget.MEDIA_GALLERY);
	
		//Upload a file to Media Gallery
		logger.strongStep("Upload a file to the Media Gallery");
		log.info("INFO: Upload a file to the Media Gallery");
		ui.uploadFileFromMediaGalleryWidget("New Photo", Data.getData().file2);	
	   
		//delete community
		logger.strongStep("Delete the Community");
	    log.info("INFO: Removing the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
	
	    ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: Check community page for a guest user</B></li>
	 *<li><B>Step: Log in as a guest user</B></li>
	 *<li><B>Verify: Verify that the Community owner view is present</B> </li>
	 *<li><B>Verify: Verify that the Community member view is present</B> </li>
	 *<li><B>Verify: Verify that the Community invites view is present</B> </li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud","bvtcloud", "smokecloud"} )
	public void guestCommunity() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		guestUser = cfg.getUserAllocator().getGuestUser();
		
		//Load component and login
		logger.strongStep("Load Communities and Log In as a Guest as: " + guestUser.getDisplayName());
		log.info("INFO: Load component and Log In as a Guest");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser); 
			
		//create community
		logger.weakStep("Check the Community Owner view");
		log.info("INFO: Check the Community Owner view");
		ui.fluentWaitPresent(CommunitiesUICloud.ownerView);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.ownerView),
					"ERROR:  Community Owner View Link is not present.");
		
		logger.weakStep("Validate that the Community Members view is present");
		log.info("INFO: Validate that the Community Members view is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.memberView),
					"ERROR:  Community Member View Link was not present.");
		
		logger.weakStep("Validate that the Community Invited view is present");
		log.info("INFO: Validate that the Community Invited view is present");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUICloud.InvitedView),
					"ERROR:  Community Invited View Link is not present.");

		ui.endTest();
	}
	

	
	/**
	 * This method will select sort order by Date tab, used by Cloud only
     */
	private void sortViewByDateForCloud( boolean isOnPremise, boolean isCardView) {
		
		//If environment is Cloud & the catalog UI GK flag is enabled then click on the sort by 'Date' tab
		//Content on cloud does not get cleared, community may not appear when sorted by Recently Visited due to all the communities
		//Community is easily found by clicking on Date
		if (!isOnPremise){
			if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
						
				if (!isCardView){
					log.info("INFO: Click the view sort by option 'Date'");
					ui.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
				}
			}
		}
		
	}
}



