package com.ibm.conn.auto.tests.communities;

import static org.testng.Assert.assertTrue;

import java.io.File;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.forums.BVT_Level_2_Forums;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.DogearUI.SelectBookmarkViews;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_ComponentPack_QuickResults extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Forums.class);
	private CommunitiesUI ui;
	private ForumsUI fUi;
	private BlogsUI bUi;
	private DogearUI dUi;
	private WikisUI wUi;
	private CalendarUI cUi;
	private DogearUI dui;
	private FilesUI fileUI;
	private FileViewerUI uiViewer;
	private TestConfigCustom cfg;
	private User testUser;
	private APICommunitiesHandler comApiOwner;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	private HCBaseUI hc;
	
	@BeforeClass(alwaysRun=true)
	public void setupClass()
	{
		cfg=  TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		comApiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		hc = new HCBaseUI(driver);
	}


	@BeforeMethod(alwaysRun=true)
	public void setUpclass() {
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUi = ForumsUI.getGui(cfg.getProductName(), driver);
		bUi = BlogsUI.getGui(cfg.getProductName(), driver);
		dUi = DogearUI.getGui(cfg.getProductName(), driver);
		wUi = WikisUI.getGui(cfg.getProductName(), driver);
		cUi = CalendarUI.getGui(cfg.getProductName(), driver);
		dui = DogearUI.getGui(cfg.getProductName(), driver);
		fileUI = FilesUI.getGui(cfg.getProductName(), driver);
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		ui.addOnLoginScript(ui.getCloseTourScript());

	}

	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Community Forum and Community Forum Topic</li>
	*<li><B>Step: </B>Create a Community via the API</li>
	*<li><B>Step: </B>Login to application and navigate to community overview page</li>
	*<li><B>Step: </B>Create new Forum Topic in same community</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for Forum created by default with community's name</li>
	*<li><B>Verify: </B>Verify that user is navigated to Forum page and created forum name is displayed</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for created Forum Topic </li>
	*<li><B>Verify: </B>Verify that user is navigated to Forum Topic page and created forum topic name is displayed</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/ 
	@Test(groups = { "cplevel2", "mtlevel2"})
	public void verifyQuickResultsForCommunityForumTopic()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();

		BaseForumTopic topic = new BaseForumTopic.Builder("topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription)
				.build();

		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comApi = community.createAPI(comApiOwner);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comApi);

		logger.strongStep("Check to see if the Forum widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it");
		if(!comApiOwner.hasWidget(comApi, BaseWidget.FORUM))
		{
			community.addWidget(ui, BaseWidget.FORUM);
		}

		logger.strongStep("Load Community and Log In as: " + testUser.getDisplayName());
		log.info("Load Community and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			comApiOwner.editStartPage(comApi, StartPageApi.OVERVIEW);
		}

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		logger.strongStep("INFO: Select Forums from Tab nav menu");
		log.info("INFO: Select Forums from Tab nav menu");
		Community_TabbedNav_Menu.FORUMS.select(ui,2);
	    
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(fUi);

		logger.strongStep("Search newly created forum");
		log.info("INFO: Search newly created forum");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,community.getName(), "Forum viewed");
		
		logger.strongStep("Verify that Forum page landed successfully");
		log.info("INFO: Verify that Forum page landed successfully");
		Assert.assertTrue(driver.getFirstElement(ForumsUIConstants.forumNameLink).getText().trim().equals(community.getName()));

		logger.strongStep("Search newly created forum topic");
		log.info("INFO: Search newly created forum topic");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,topic.getTitle(), "Forum viewed");

		logger.strongStep("Validate that the 'Reply to topic' button displays in the forum");
		log.info("INFO: Validate that the 'Reply to topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Reply_to_topic),
				"ERROR: Unable to locate the 'Reply to topic' button in the forum");

		logger.strongStep("Verify the topic created successfully and topic's name shown up");
		log.info("INFO:verify the topic created successfully and topic's name shown up");
		Assert.assertTrue(driver.isElementPresent(ForumsUI.getForumTopicTitle(topic)),"failed to find the topic");

		logger.strongStep("Delete the Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Community Blog and Community Blog Entry</li>
	*<li><B>Step: </B>Create a Community via the API</li>
	*<li><B>Step: </B>Login to application and navigate to community overview page</li>
	*<li><B>Step: </B>Create new Blog Entry in same community</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for Blog created by default with community's name</li>
	*<li><B>Verify: </B>Verify that user is navigated to Blog page and created Blog name is displayed</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for created Blog Entry</li>
	*<li><B>Verify: </B>Verify that user is navigated to Blog Entry page and created Blog Entry name is displayed</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/
	@Test(groups = { "cplevel2", "mtlevel2"})
	public void verifyQuickResultsForCommunityBlogEntry() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.build();
	
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
				 .tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				 .content("Test description for testcase " + testName)
				 .build();
		
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comApi = community.createAPI(comApiOwner);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comApi);

		logger.strongStep("Check to see if the Blog widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Blog widget is enabled. If it is not enabled, then enable it");
		if(!comApiOwner.hasWidget(comApi, BaseWidget.BLOG))
		{
			community.addWidget(ui, BaseWidget.BLOG);
		}

		logger.strongStep("Load Community and Log In as: " + testUser.getDisplayName());
		log.info("Load Community and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			comApiOwner.editStartPage(comApi, StartPageApi.OVERVIEW);
		}

		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
				
		//Add an Entry
		logger.strongStep("Add a new Blog Entry");
		log.info("INFO: Add the new Blog entry");
		Community_TabbedNav_Menu.BLOG.select(ui,2);
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		blogEntry.create(bUi);
		
		logger.strongStep("Search newly created Blog");
		log.info("INFO: Search newly created Blog");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,community.getName(), "Blog viewed");
		
		logger.strongStep("Verify that New Entry button is displayed on Blogs Page");
		log.info("INFO: Verify that New Entry button is displayed on Blogs Page");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntry));

		logger.strongStep("Verify that Blog name in header is displayed on Blogs Page");
		log.info("INFO: Verify that Blog name in header is displayed on Blogs Page");
		Assert.assertTrue(driver.getFirstElement(BlogsUIConstants.BlogNameHeaderText).getText().trim().equals(community.getName()));

		logger.strongStep("Search newly created Blog entry");
		log.info("INFO: Search newly created Blog entry");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,blogEntry.getTitle(), "Blog viewed");
		
		logger.strongStep("Verify that New Entry button is displayed on Blogs Entry Page");
		log.info("INFO: Verify that New Entry button is displayed on Blogs Entry Page");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntry));

		logger.strongStep("Verify that Blog Entry name in header is displayed on Blogs Page");
		log.info("INFO: Verify that Blog Entry name in header is displayed on Blogs Page");
		Assert.assertTrue(driver.getFirstElement(BlogsUIConstants.ideationBlogTitle).getText().trim().equals(blogEntry.getTitle()));
		
		logger.strongStep("Delete Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Community Wiki page and child page</li>
	*<li><B>Step: </B>Create a Community with API</li>
	*<li><B>Step: </B>Login to connections and navigate to community overview page</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for wiki page created by default with community's name</li>
	*<li><B>Step: </B>Click listed wiki page</li>
	*<li><B>Verify: </B>Verify that user is navigated to Wiki page and created Wiki name is displayed</li>
	*<li><B>Step: </B>Go to wiki page and create a child page under existing wiki page</li>
	*<li><B>Step: </B>Click on Search icon on left top from community overview page and search for created wiki child page</li>
	**<li><B>Step: </B>Click listed child wiki page</li>
	*<li><B>Verify: </B>Verify that user is navigated to Child wiki page and created child wiki page is displayed</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyQuickResultsForCommunityWiki() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		// Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand()).access(Access.PUBLIC)
				.tags(Data.getData().commonTag).description("Test Community for " + testName).build();

		BaseWikiPage childPage = new BaseWikiPage.Builder("Child_Wiki_" + Helper.genDateBasedRand(), PageType.Child)
				.tags("tag1, tag2").description("this is a test description for " + testName).build();

		// create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Creating a new Community using API");
		Community comAPI = community.createAPI(comApiOwner);

		logger.strongStep("Get the UUID of the Community");
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(comApiOwner, comAPI);

		logger.strongStep("Add the Wiki widget to the Community using API");
		log.info("INFO: Add the Wiki widget to the Community using API");
		if (!comApiOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.WIKI);
		}

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communites and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		logger.strongStep("Search wiki page created from default community is shown in Quick Results");
		log.info("Search wiki page created from default community is shown in Quick Results");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,community.getName(), "Wiki viewed");
     	 	ui.waitForPageLoaded(driver);
        	ui.waitForJQueryToLoad(driver);

		logger.strongStep("Verify that wiki page created from default community is available in wiki page");
		log.info("Verify that wiki page created from default community is available in wiki page");
		Assert.assertTrue(driver.getFirstElement(wUi.getWikiLink(community.getName())).getText().trim()
				.equals(community.getName() + " Wiki"));

		// add wiki page
		logger.strongStep("Create a new Child Wiki page");
		log.info("Create a new Child Wiki page");
		childPage.create(wUi);

		// Verify pages have been created
		logger.weakStep("Validate that the Child Page exists");
		log.info("INFO: Validate that the Child Page exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(childPage.getName()),
				"ERROR:" + childPage.getName() + "Page does not exist");

		logger.strongStep("Search wiki child page created is shown in Quick Results");
		log.info("Search wiki child page created is shown in Quick Results");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,childPage.getName(), "Wiki viewed");

		logger.strongStep("Verify that wiki child page created from is available in wiki page");
		log.info("Verify that wiki child page created from is available in wiki page");
		Assert.assertTrue(driver.getFirstElement(WikisUI.getWikiPageTitle(childPage)).getText().trim()
				.equals(childPage.getName()));

		logger.strongStep("Delete the Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));


		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Community Bookmark page</li>
	*<li><B>Step: </B>Create a Community with API</li>
	*<li><B>Step: </B>Create a Bookmark in with API</li>
	*<li><B>Step: </B>Login to connections and navigate to community overview page</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for Community Bookmark page</li>
	*<li><B>Step: </B>Click on Community Bookmark link</li>
	*<li><B>Verify: </B>Verify that user is navigated to Community Bookmark page</li>
	*<li><B>Step: </B>Delete the Community with API</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/
	@Test (groups = {"cplevel2", "mtlevel2"} )
	public void verifyQuickResultsForCommunityBookmark() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();			

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.access(defaultAccess)
				.tags(Data.getData().commonTag)
				.addMember(new Member(CommunityRole.MEMBERS, testUser))
				.description("Test Community for " + testName).build();


		BaseDogear bookmark = new BaseDogear.Builder(Data.getData().BookmarkName , Data.getData().BookmarkURL)
				.community(community)
				.tags(Data.getData().BookmarkTag+ Helper.genDateBasedRand())
				.description(Data.getData().BookmarkDesc)
				.build();
		

		//create community
		logger.strongStep("Create a community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);
		
		logger.strongStep("Create a Bookmark in community using API");
		log.info("INFO: Create a Bookmark in community using API");
		bookmark.createAPI(comApiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);
				
		logger.strongStep("Load communities and login: " +testUser.getDisplayName());
		log.info("Load communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		logger.strongStep("Search newly created Bookmark");
		log.info("INFO: Search newly created Bookmark");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,bookmark.getTitle(), "Bookmark viewed");
		
		logger.strongStep("Verify that Add Bookmark button is displaying on Bookmark page");
		log.info("INFO: Verify that Add Bookmark button is displaying on Bookmark page");
		Assert.assertTrue(dUi.isElementPresent(DogearUIConstants.AddBookmark));
		
		logger.strongStep("Verify that newly created Bookmark link is displayed on Bookmark page");
		log.info("INFO: Verify that newly created Bookmark link is displayed on Bookmark page");
		Assert.assertTrue(dUi.getElementText(DogearUIConstants.bookmarkLink).equals(bookmark.getTitle()));
		
		logger.strongStep("Delete the Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Calendar Event</li>
	*<li><B>Step: </B>Create a Community via the API</li>
	*<li><B>Step: </B>Login to application and navigate to community overview page</li>
	*<li><B>Step: </B>Create new Calendar Event in same community</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page and search for Calendar Event created</li>
	*<li><B>Verify: </B>Verify that user is navigated to Calendar Event page and created Calendar Event name is displayed</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "mtlevel2"})
	public void verifyQuickResultsForCommunityCalendarEvent() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create an event base state object
		BaseEvent event = new BaseEvent.Builder(testName + " event" + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();
		
		//create community
		logger.strongStep("create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);		

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);

		logger.strongStep("Add Calendar Event widget to the Community using API");
		log.info("INFO: Add Calendar Event widget to the Community using API");
		if (!comApiOwner.hasWidget(comAPI, BaseWidget.EVENTS)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.EVENTS);
		}

		//Load component and login
		logger.strongStep("Load Calendar Event and login: " +testUser.getDisplayName());
		log.info("Load Calendar Event  and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("INFO: Select Events from Tab nav menu");
		log.info("INFO: Select Events from Tab nav menu");
		Community_TabbedNav_Menu.EVENTS.select(ui,2);

		logger.strongStep("Create a Calendar Event");
		log.info("INFO: Create a Calendar event");
		event.create(cUi);
		
		logger.strongStep("Search newly created Calendar Event");
		log.info("INFO: Search newly created Calendar Event");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,event.getName(), "Event viewed");
		
		logger.strongStep("INFO: Verify that newly created Calendar Event name header is displayed on Calendar Events page");
		log.info("INFO: Verify that newly created Calendar Event name header is displayed on Calendar Events page");
		Assert.assertTrue(driver.getFirstElement(CalendarUI.EventHeader).getText().trim().equals(event.getName()));
		
		logger.strongStep("INFO: Verify that More action button is displayed on Calendar Events page");
		log.info("INFO: Verify that More action button is displayed on Calendar Events page");
		Assert.assertTrue(driver.getFirstElement(CalendarUI.MoreActions).isDisplayed());	
		
		logger.strongStep("Delete the Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for New Idea in Ideation Blog</li>
	*<li><B>Step: </B>[API]Create a community</li>
	*<li><B>Step: </B> Add the Ideation Blog widget to community</li>
	*<li><B>Step: </B>Click on Ideation Blog from Tabbed nav header</li> 
	*<li><B>Step: </B>Click on New Idea button</li>
	*<li><B>Step: </B>Create a New Idea to Ideation Blog and save</li>
	*<li><B>Step: </B>Search for newly created Idea</li>
	*<li><B>Verify: </B>Verify that user navigated to The New Idea page successfully</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/ 
	@Test(groups = {"cplevel2", "mtlevel2"})
	public void verifyQuickResultsForCommunityIdeationBlogNewIdea() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
									 .access(defaultAccess)
									 .description("Test Widgets inside community for " + testName)
									 .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder(testName + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("Test Content for " + testName)
														 .build();	

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		logger.strongStep("Add the Ideation Blog widget to the Community using API");
		log.info("INFO: Add the Ideation Blog widget to the Community using API");
		log.info("INFO: Add the Wiki widget to the Community using API");
		community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.IDEATION_BLOG);
	
		logger.strongStep("Open Ideation Blogs and login: " + testUser.getDisplayName());
		log.info("Open Ideation Blogs and login: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		logger.strongStep("INFO: Select Ideation Blog from Tab nav menu");
		log.info("INFO: Select Ideation Blog from Tab nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui,2);

		logger.strongStep("Click on Ideation Blog link");
		log.info("INFO: Click on Ideation Blog link");
		bUi.clickLink(BlogsUIConstants.ideationBlogLinks.replace("PLACEHOLDER", ideationBlogEntry.getTitle()));
		
		logger.strongStep("Select New Idea button");
		log.info("INFO: Select New Entry button");
		bUi.clickLink(BlogsUIConstants.NewIdea);
		
		logger.strongStep("Create a new idea");
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(bUi);

		logger.strongStep("Search newly created Calendar Event");
		log.info("INFO: Search newly created Calendar Event");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,ideationBlogEntry.getTitle(), "Blog viewed");
				
		logger.strongStep("Verify that newly created ideation blog text displayed");
		log.info("INFO: Verify that newly created ideation blog text displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(ideationBlogEntry.getTitle()), "ERROR: Entry not found"); 
		
		logger.strongStep("Verify that Gradute button on newly created ideation blog is displayed");
		log.info("INFO: Verify that Gradute button on newly created ideation blog is displayed");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsGraduate));
		
		logger.strongStep("Delete the Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comAPI);

		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();
			
		ui.endTest();

	}

	/**
	* <ul>
	*<li><B>Info: </B>Verify Quick Results for login message over public communities url without login</li>
	*<li><B>Step: </B>Navigate to public communities url</li>
	*<li><B>Step: </B>Click on Search icon on top right corner of public communities page and search for any text</li>
	*<li><B>Verify: </B>Verify that user is informed with a message saying "Log in to see more results including 
	*Files, Wikis, Communities and more."</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2"})
	public void verifyQuickResultsLoginMessage() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
				
		logger.strongStep("Load public communities url");
		log.info("Load public communities url");
		ui.loadComponent(Data.getData().publicCommunityURL);
		
		//Switch to Old UI
		if(hc.isElementVisibleWd(By.id("top-navigation"), 5))
		{
			log.info("INFO: Intentionally toggle to old UI");		
			hc.waitForElementVisibleWd(By.id("theme-switcher-wrapper"), 4);
			hc.clickLinkWd(By.id("theme-switcher-wrapper"), "new UI toggle switch");
			hc.clickLinkWithJavaScriptWd(hc.findElement(By.cssSelector("#theme_switcher_options_modal_switch input")));
			hc.findElement(By.id("options_modal_save_button")).click();	
		}
		
		logger.strongStep("Click on the Search Panel's search icon");
		log.info("INFO: Click on the Search Panel's search icon");
		ui.clickLinkWithJavascript(HomepageUIConstants.SearchPanelSearchIcon_2);
		
		logger.strongStep("Type text \"tes\" in search text box");
		log.info("INFO: Type text \"tes\" in search text box");
		ui.typeText(HomepageUIConstants.SearchPanelTextBox,"tes");
		String ActualLoginMessage = driver.getFirstElement(HomepageUIConstants.SearchedContentLoginMessage).getText();
			
		logger.strongStep("Verify login message in search text box");
		log.info("INFO: Verify login message in search text box");
		Assert.assertEquals(ActualLoginMessage, HomepageUIConstants.SCExpectedLoginMessage);
		
		ui.endTest();
			
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for stand alone Forum and Forum Topic</li>
	*<li><B>Step: </B>Login to application and navigate to stand alone Forums page</li>
	*<li><B>Step: </B>Create new Forum </li>
	*<li><B>Step: </B>Create new Forum Topic</li>
	*<li><B>Step: </B>Navigate to home page</li>
	*<li><B>Step: </B>Click on Search icon on top right corner of home page 
	*and search for Forum created</li>
	*<li><B>Verify: </B>Verify that user is navigated to Forum page 
	*and created forum name is displayed</li>
	*<li><B>Step: </B>Click on Search icon on top right corner of home page 
	*and search for created Forum Topic </li>
	*<li><B>Verify: </B>Verify that user is navigated to Forum Topic page 
	*and created forum topic name is displayed</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/ 
	@Test(groups = { "cplevel2"})
	public void verifyQuickResultsForStandloneForumTopic()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription)
				.build();

		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(fUi);
	    
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(fUi);
		
		logger.strongStep("Load Home page");
		log.info("INFO: Load Home page");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);

		logger.strongStep("Search newly created forum");
		log.info("INFO: Search newly created forum");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,forum.getName(), "Forum viewed");
		
		logger.strongStep("Verify that Forum page landed successfully");
		log.info("INFO: Verify that Forum page landed successfully");
		Assert.assertTrue(driver.getFirstElement(ForumsUIConstants.forumNameLink).getText().trim().equals(forum.getName()));

		logger.strongStep("Search newly created forum topic");
		log.info("INFO: Search newly created forum topic");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,topic.getTitle(), "Forum viewed");

		logger.strongStep("Validate that the 'Reply to topic' button displays in the forum");
		log.info("INFO: Validate that the 'Reply to topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Reply_to_topic),
				"ERROR: Unable to locate the 'Reply to topic' button in the forum");

		logger.strongStep("Verify the topic created successfully and topic's name shown up");
		log.info("INFO:verify the topic created successfully and topic's name shown up");
		Assert.assertTrue(driver.isElementPresent(ForumsUI.getForumTopicTitle(topic)),"failed to find the topic");

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Standalone Wiki and Wiki child page</li>
	*<li><B>Step: </B>Login to application and navigate to Wii page</li>
	*<li><B>Step: </B>Create new Wiki page</li>
	*<li><B>Step: </B>Create new Child Wiki page</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from Home page and search for created Wiki</li>
	*<li><B>Verify: </B>Verify that user is navigated to Wiki page successfully</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from Home page and search for created Child Wiki </li>
	*<li><B>Verify: </B>Verify that user is navigated to Child Wiki page successfully</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/ 
	@Test(groups = {"cplevel2"})
	public void verifyQuickResultsForStandaloneWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		User testUser = cfg.getUserAllocator().getUser();
		
		BaseWiki wiki = new BaseWiki.Builder("Wiki" + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("Description for test " + testName)
									.build();
		
		BaseWikiPage childWiki = new BaseWikiPage.Builder("ChildWiki" + Helper.genDateBasedRand(), PageType.Child)
				.tags("tag2").description("this is a test description for " + testName).build();
		
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//create a new wiki
		logger.strongStep("Create a new Wiki page");
		log.info("INFO: Create a new Wiki page");
		wiki.create(wUi);
		
		logger.strongStep("Create a new Child Wiki page");
		log.info("INFO: Create a new Child Wiki page");
		childWiki.create(wUi);

		logger.strongStep("Load Home page");
		log.info("INFO: Load Home page");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);

		logger.strongStep("Search newly created Wiki page");
		log.info("INFO: Search newly created Wiki page");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,wiki.getName(), "Wiki viewed");
		
		logger.strongStep("Verify Wiki page named link displayed");
		log.info("INFO: Verify Wiki page named link displayed");
		Assert.assertTrue(driver.getFirstElement(WikisUIConstants.wikiNameInBreadcrumb).getText().trim().equals(wiki.getName()));

		logger.strongStep("Verify that Page Actions Buttons is displayed on Wiki Page");
		log.info("INFO: Verify that Page Actions Buttons is displayed on Wiki Page");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button));
				
		logger.strongStep("Search newly created Child Wiki Page");
		log.info("INFO: Search newly created Child Wiki Page");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,childWiki.getName(), "Wiki viewed");
		
		logger.strongStep("Verify Child Wiki page named header displayed");
		log.info("INFO: Verify Child Wiki page named header displayed");
		Assert.assertTrue(driver.getFirstElement(WikisUIConstants.childPageName).getText().trim().equals(childWiki.getName()));

		logger.strongStep("Verify that Page Actions Buttons is displayed on Wiki Page");
		log.info("INFO: Verify that Page Actions Buttons is displayed on Wiki Page");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button));
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button));

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Standalone Blog and Blog Entry  page</li>
	*<li><B>Step: </B>Login to application</li>
	*<li><B>Step: </B>Create new Blog</li>
	*<li><B>Step: </B>Create new Blog Entry</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from Home page and search for created Blog</li>
	*<li><B>Verify: </B>Verify that user is navigated to Blog page successfully</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from Home page and search for created Blog Entry</li>
	*<li><B>Verify: </B>Verify that user is navigated to Blog Entry page successfully</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/ 
	@Test(groups = {"cplevel2"})
	public void verifyQuickResultsForStandaloneBlogEntry() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
				
		String testName = ui.startTest();
				
		String randval = Helper.genDateBasedRandVal();
		
		BaseBlog blog = new BaseBlog.Builder("Blog" + randval, Data.getData().BlogsAddress1 + randval)
									.tags("Tag for "+testName  + randval)
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand()).blogParent(blog)
												 .tags(testName + Helper.genDateBasedRand())
												 .content("Test description for testcase " + testName)
												 .build();
				
		//Load component and login
		logger.strongStep("Open blogs and login: " + testUser.getDisplayName());
		log.info("INFO: Load blogs component and login");
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser);

		//create blog
		logger.strongStep("Create a new blog");
		log.info("INFO: Create a new blog");
		blog.create(bUi);

		logger.strongStep("Open the blog");
		log.info("INFO: Open blog");
		ui.clickLink("link=" + blog.getName());
		
		//select New Entry button
		logger.strongStep("Select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		logger.strongStep("Add an Entry A");
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(bUi);

		logger.strongStep("Select to manage blog");
		log.info("INFO: Select to manage blog");
		ui.clickLink(BlogsUIConstants.blogManage);
		
		logger.strongStep("Load Home page");
		log.info("INFO: Load Home page");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);

		logger.strongStep("Search newly created Blog");
		log.info("INFO: Search newly created Blog");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,blog.getName(), "Blog viewed");
		
		logger.strongStep("Verify that New Entry button is displayed on Blogs Page");
		log.info("INFO: Verify that New Entry button is displayed on Blogs Page");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntry));

		logger.strongStep("Verify that Blog name in header is displayed on Blogs Page");
		log.info("INFO: Verify that Blog name in header is displayed on Blogs Page");
		Assert.assertTrue(driver.getFirstElement(BlogsUIConstants.BlogNameHeaderText).getText().trim().equals(blog.getName()));
		
		logger.strongStep("Search newly created Blog Entry");
		log.info("INFO: Search newly created Blog Entry");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,blogEntry.getTitle(), "Blog viewed");
		
		logger.strongStep("Verify that New Entry button is displayed on Blogs Entry Page");
		log.info("INFO: Verify that New Entry button is displayed on Blogs Entry Page");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsNewEntry));

		logger.strongStep("Verify that Blog Entry name in header is displayed on Blogs Page");
		log.info("INFO: Verify that Blog Entry name in header is displayed on Blogs Page");
		Assert.assertTrue(driver.getFirstElement(BlogsUIConstants.ideationBlogTitle).getText().trim().equals(blogEntry.getTitle()));
		
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();
		
		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Standalone Bookmarks</li>
	*<li><B>Step: </B>Login to application and navigate to stand alone Bookmarks page</li>
	*<li><B>Step: </B>Create new Bookmark </li>
	*<li><B>Step: </B>Navigate to home page</li>
	*<li><B>Step: </B>Click on Search icon on top right corner of home page and search for Bookmark created</li>
	*<li><B>Verify: </B>Verify that user is navigated to Bookmark page and created Bookmark name is displayed</li>
	*<li><B>Step: </B>Logout from application</li>
	*</ul>
	*/ 
	@Test(groups = { "cplevel2"})
	public void verifyQuickResultsForStandaloneBookmark()
	{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String dateRandVal = Helper.genDateBasedRandVal();
		String testName = ui.startTest() + dateRandVal;
		String url = Data.getData().commonURL + dateRandVal;
		
		BaseDogear bookmark = new BaseDogear.Builder(testName , url)
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.description(Data.getData().commonDescription + testName).build();

		// Load the component and login
		logger.strongStep("Load dogear and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentDogear);
		ui.login(testUser);
		
		//Create public bookmark to Google with tags and description from My Bookmarks view
		logger.strongStep("Create public bookmark to google with tags and description from 'My Bookmarks' view");
		dui.create(bookmark);
		
		//Switch to Public Bookmarks view and verify public bookmark is there as well
		logger.strongStep("Switch to 'Public Bookmarks' view and verify that 'public bookmarks' is there as well");
		log.info("INFO: Switch to Public Bookmarks view");
		dui.selectBookmarkView(SelectBookmarkViews.PublicBookmarks);
		
		//reliability check
		logger.strongStep("If the bookmark is not present refresh the browser");
		if(driver.isTextNotPresent(testName))
		{
			log.info("INFO:Bookmark not detected, ...refreshing browser ");
			driver.navigate().refresh();
		}
		
		logger.weakStep("Validate that the bookmark displays in the 'Public Bookmarks' view");
		log.info("INFO: Validate bookmark: " +testName+ " displays in Public Bookmarks view");
		assertTrue(ui.fluentWaitTextPresent(bookmark.getTitle()), 
				   "ERROR: Bookmark: " + bookmark.getTitle() + " not found");
		
		logger.strongStep("Load Home page");
		log.info("INFO: Load Home page");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);

		logger.strongStep("Search newly created Bookmark");
		log.info("INFO: Search newly created Bookmark");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,bookmark.getTitle(), "Bookmark viewed");
		
		logger.strongStep("Verify that Add Bookmark button is displaying on Bookmark page");
		log.info("INFO: Verify that Add Bookmark button is displaying on Bookmark page");
		Assert.assertTrue(dUi.isElementPresent(DogearUIConstants.AddABookmark));
		
		logger.strongStep("Verify that newly created Bookmark link is displayed on Bookmark page");
		log.info("INFO: Verify that newly created Bookmark link is displayed on Bookmark page");
		Assert.assertTrue(dUi.getElementText(dUi.getBookmarkSelector(bookmark)).equals(bookmark.getTitle()));
		
		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Community Files</li>
	*<li><B>Step: </B>Create a Community with API and add file to this community with API</li>
	*<li><B>Step: </B>Login to connections and navigate to community overview page</li>
	*<li><B>Step: </B>Go to files tab and open file uploaded with community</li>
	*<li><B>Step: </B>Close file viewer</li>
	*<li><B>Step: </B>Go to overview tab of community</li>
	*<li><B>Step: </B>Click on Search icon on top right corner from community overview page 
	*and search for file recently opened from files tab</li>
	*<li><B>Step: </B>Click listed file name</li>
	*<li><B>Verify: </B>Verify that user is navigated to file viewer page and file is displayed</li>
	*/
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyQuickResultsForCommunityWithFiles() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
			
		APIFileHandler filesApiOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
	   
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal()).build();
		
		String fileName = testName + "File" + Helper.genDateBasedRand();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .extension(".jpg").rename(fileName)
				 .build();
				
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(comApiOwner);
		
		logger.strongStep("Upload the file '" + fileA.getName() + "'");
		log.info("INFO: Upload the file '" + fileA.getName() + "' via API");
		File fileToUpload = new File(FilesUI.getFileUploadPath(fileA.getName(), cfg));
		fileA.createAPI(filesApiOwner, fileToUpload, comAPI);
		fileA.setName(fileName+fileA.getExtension());
		
		// Get UUID of community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);

		// add widget if necessary
		logger.strongStep("Add the File widget to the Community using API");
		if (!comApiOwner.hasWidget(comAPI, BaseWidget.FILES)) {
			log.info("INFO: Add the File widget to the Community using API");
			community.addWidgetAPI(comAPI, comApiOwner, BaseWidget.FILES);
		}

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communites and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		log.info("INFO: Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the Communtiy");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		// Navigate to the Files and add different files
		logger.strongStep("Navigate to Files");
		log.info("INFO: Navigate to Files");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		logger.strongStep("Click on Tiles view for Files");
		log.info("INFO: Click on Tiles view for Files");
		Files_Display_Menu.TILE.select(ui);

		logger.strongStep("Open uploaded file and close");
		log.info("INFO: Open uploaded file and close");
		ui.clickLinkWait(CommunitiesUI.communityFileOrFolderLink(fileA));
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);
	
		logger.strongStep("Search File from default community is shown in Quick Results");
		log.info("INFO: Search File from default community is shown in Quick Results");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIcon_2,fileA.getName(), "File viewed");

		logger.strongStep("Verify that File from default community is available in file page");
		log.info("INFO: Verify that File from default community is available in file page");
		Assert.assertTrue(driver.getFirstElement(FilesUIConstants.fileviewer_previewLinkTitle).getText().trim()
				.equals(fileA.getName()));
		
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);

		logger.strongStep("Delete the Community");
		log.info("Delete the Community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();
		
		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Verify Quick Results for Standalone Files</li>
	*<li><B>Step: </B>Upload a file with API</li>
	*<li><B>Step: </B>Login to connections and navigate to Files page</li>
	*<li><B>Step: </B>Go to my files tab and click on uploaded file from api </li>
	*<li><B>Step: </B>Close file viewer</li>
	*<li><B>Step: </B>Go to Homepage</li>
	*<li><B>Step: </B>Click on Search icon on top right corner of Homepage
	*and search for file recently opened from files tab</li>
	*<li><B>Step: </B>Click listed file name</li>
	*<li><B>Verify: </B>Verify that user is navigated to file viewer page and file is displayed</li>
	*/
	@Test(groups = { "cplevel2" })
	public void verifyQuickResultsForStandaloneFiles() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.shareLevel(ShareLevel.NO_ONE)
									.build();
		
		//Start of test
		ui.startTest();
		
		//Upload a file
		logger.strongStep("Upload a private file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		//Load component and login
		logger.strongStep("Load files and login");
		log.info("INFO: Load component and login");	
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		// change the view list format
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		fileUI.clickMyFilesView();
		
		logger.strongStep("Select Tile display button");
		log.info("INFO: Select Tile display button");
		Files_Display_Menu.TILE.select(ui);

		logger.strongStep("Open uploaded file in file viewer and close it");
		log.info("INFO: Open uploaded file in file viewer and close it");
		ui.clickLinkWait(CommunitiesUI.communityFileOrFolderLink(file));
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);

		logger.strongStep("Load Home page");
		log.info("INFO: Load Home page");
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
	
		logger.strongStep("Search File from standlone files is shown in Quick Results");
		log.info("INFO: Search File from standlone files is shown in Quick Results");
		searchForDocument(HomepageUIConstants.SearchPanelSearchIconHomePage,file.getName(), "File viewed");

		logger.strongStep("Verify that File from standlone files is available in file page");
		log.info("INFO: Verify that File from standlone files is available in file page");
		Assert.assertTrue(driver.getFirstElement(FilesUIConstants.fileviewer_previewLinkTitle).getText().trim()
				.equals(file.getName()));
		
		logger.strongStep("Close file viewer");
		log.info("INFO: Close file viewer");
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);

		//Logout as current user
		log.info("INFO: Log Out as " + testUser.getDisplayName());
		logger.strongStep("Log Out as " + testUser.getDisplayName());
		ui.logout();
		
		ui.endTest();

	}
		
	/**
	 * searchForDocument - Search for a document from overview page
	 * @param searchLocator Search locator needed to search document
	 * @param documentName which needs to be searched
	 * @param documentViewedLocator document parent locator partial text
	 */
	public void searchForDocument(String searchLocator,String documentName,String documentViewedLocator)
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		if (searchLocator.equalsIgnoreCase(HomepageUIConstants.SearchPanelSearchIcon_2)) {
			logger.strongStep("Navigate to Community Overview page");
			log.info("INFO: Navigate to Community Overview page");
			Community_TabbedNav_Menu.OVERVIEW.select(ui, 2);
		}

		logger.strongStep("Click on the Search Panel's search icon");
		log.info("INFO: Click on the Search Panel's search icon");
		ui.clickLinkWait(searchLocator);
		
		logger.strongStep("Type in the name of document in search text box and click on document link");
		log.info("INFO: Type in the name of document in search text box and click on document link");
		ui.typeText(HomepageUIConstants.SearchPanelTextBox,documentName);
		driver.getFirstElement(HomepageUIConstants.SearchedContentDropdownList.replace("PLACEHOLDER1", documentViewedLocator)).click();
	}
	

}
