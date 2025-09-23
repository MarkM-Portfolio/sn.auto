package com.ibm.conn.auto.tests.commonui.regression;

import java.util.Arrays;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseSurvey;
import com.ibm.conn.auto.appobjects.base.BaseSurveyQuestion;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.library.LibraryWidget;
import com.ibm.conn.auto.appobjects.library.ViewSelector;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.SurveysUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunitiesWidgets extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(CommunitiesWidgets.class);
	private ActivitiesUI aui;
	private BlogsUI blogUI;
	private CommunitiesUI ui;
	private DogearUI uiBM;
	private FilesUI flUI;
	private FeedsUI fdUI;
	private ForumsUI fmUI;
	private SurveysUI svUI;
	private WikisUI wikiUI;
	private TestConfigCustom cfg;	
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private APIFileHandler	fileHandler;
	String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {	
		
		cfg = TestConfigCustom.getInstance();
		//Load User
		testUser = cfg.getUserAllocator().getUser();	
		log.info("INFO: Using test user: " + testUser.getDisplayName());
			
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		fileHandler = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());		
	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		aui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		blogUI = BlogsUI.getGui(cfg.getProductName(), driver);
		fdUI = FeedsUI.getGui(cfg.getProductName(), driver);
		flUI = FilesUI.getGui(cfg.getProductName(), driver);
		fmUI = ForumsUI.getGui(cfg.getProductName(), driver);
		svUI = SurveysUI.getGui(cfg.getProductName(), driver);
		wikiUI = WikisUI.getGui(cfg.getProductName(), driver);
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());	
		
	}

	
	/**
	 * addBookmark()
	 *<ul>
	 *<li><B>Info: </B>Test case to test that you can create a bookmark within a community</li>
	 *<li><B>Step: </B>Create a new community using API</li> 
	 *<li><B>Step: </B>Add a bookmark</li> 
	 *<li><B>Verify: </B>Bookmark was added to the community</li>
	 *<li><B>CleanUp: Delete the Community</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 */
	@Test (groups = {"regression", "regressioncloud"} )
	public void addBookmark() throws Exception {
		
		String testName = ui.startTest();			
		uiBM = DogearUI.getGui(cfg.getProductName(), driver);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .tags("commTag")
												   .addMember(new Member(CommunityRole.MEMBERS, testUser))
												   .description("Test Community for " + testName).build();

		BaseDogear bookmark = new BaseDogear.Builder("My Bookmark" , Data.getData().IbmURL + "/us/en/")
											.community(community)
											.tags("bktag" + Helper.genDateBasedRand())
											.description(Data.getData().communitywidgettest + " " + testName)
											.build();


		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		//GUI
		//Login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Select Bookmark widget from left nav
		log.info("INFO: Clicking the Bookmarks tab");
		Community_LeftNav_Menu.BOOKMARK.select(ui);
		
		//Wait until Add a Bookmark button is visible
		log.info("INFO: Wait until Add a Bookmark button is visible");
		Assert.assertTrue(ui.fluentWaitPresent(DogearUIConstants.AddBookmark),
					     "ERROR: Add a Bookmark button was not found");
		
		//Click on Add a Bookmark button
		log.info("INFO: Click add bookmark button");
		ui.clickLink(DogearUIConstants.AddBookmark);
		
		//Now add a bookmark
		log.info("INFO: Fill out bookmark form and save");
		bookmark.create(uiBM);
					
		//Verify that the bookmark was created successfully
		log.info("INFO: Checking that the bookmark was created successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(bookmark.getTitle()), 
				   		 "ERROR: The bookmark was not created successfully");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}	

	
	/**
	 * addFeaturedSurveyWidget()
	 *<ul>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Use API to create a community</B> </li>
	 *<li><B>Step: Use API to create a Surveys widget</B> </li>
	 *<li><B>Step: Click Community Actions > Add Apps</B></li>
	 *<li><B>Step: Add Featured Survey widget</B> </li>
	 *<li><B>Verify: Featured survey widget is added to the right column </B> </li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 *Note: SmartCloud only
	 *Note: Surveys is a separate deployment which will not be deployed on bvtoracle or db2 servers hence
	 *disabling this test case. This test case functionality is covered in BVT_Level_2_Surveys, hence disabling it.
	 */
	@Test(groups = { "regressioncloud"}, enabled = false)
	public void addFeaturedSurveyWidget() throws Exception {

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test community - " + testName)
									 .access(defaultAccess)
									 .build();
	
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Survey widget using API
		log.info("INFO: Add Survey widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Add widget - Featured Survey
		log.info("INFO: Add Featured Survey widget");
		ui.addWidget(BaseWidget.FEATUREDSURVEYS);

		//Validate that Featured Survey widget is added to right column
		log.info("INFO: Validate that Featured Survey widget is added to right column");
		Assert.assertTrue(driver.isElementPresent(SurveysUI.featuredSurveyiFrame), 
				"ERROR: Featured Survey widget can not be found");	
		
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();		
	}
	
	
	/**
	* addFeed()
	*<ul>
	*<li><B>Info: </B>Test case to test that you can add a feed within a community</li>
	*<li><B>Step: </B> [API] Create a public community with name, tag and description</li>
	*<li><B>Step: </B> Click Community Actions > Add Apps</li>
	*<li><B>Step: </B> Add Feeds widget </li>
	*<li><B>Step: </B> Add a feed and include a tag and description</li>
	*<li><B>Verify: </B> Feed was created</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	*</ul>
	*Note: On Prem only
	*/
	@Test(groups = {"regression"})
	public void addFeed() throws Exception {
		
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags("commtag")
													.access(Access.PUBLIC)
													.description("Test Community - " + testName)
													.build();

		BaseFeed feed = new BaseFeed.Builder(testName + Helper.genDateBasedRandVal(), cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL)
		.description("Description for " + testName)
		.tags(Data.getData().MultiFeedsTag)
		.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Add widget - Feeds
		log.info("INFO: Add Feeds widget to community");
		ui.addWidget(BaseWidget.FEEDS);
		
		//Click on the Feeds in left nav
		log.info("INFO: Select Feeds from left navigation menu");
		Community_LeftNav_Menu.FEEDS.select(ui);
		
		//Click Add Feed link
		log.info("INFO: Select add feed link");
		ui.clickLinkWait(FeedsUI.AddFeedLink);
		
		//Add the feed
		log.info("INFO: Add the feed to the community");
		fdUI.addFeed(feed);

		//Test Feeds success message
		log.info("Test that feeds success message is posted");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FeedSuccessMsg),
										"Error : Feeds success message is not shown properly");
		
		//Verify that the feed displays in widget full-page
		log.info("INFO: Validate the feed is present");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + feed.getTitle()),
							"ERROR: Feed title is not displayed");
			
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}		

	
	/**
	* addIdeationBlogNewIdea() 
	*<ul>
	*<li><B>Info: </B>Add a new idea to an ideation Blog</li>
	*<li><B>Step: </B>[API]Create a community</li>
	*<li><B>Step: </B>Click Community Actions > Add Apps</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B>Click on Ideation Blog link in the left nav pane</li> 
	*<li><B>Step: </B>Click on the Contribute an Idea button</li>
	*<li><B>Step: </B>Create a New Idea and save</li>
	*<li><B>Verify: </B> The new idea was created</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void addIdeationBlogNewIdea() throws Exception {
		
		String testName = ui.startTest();
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
									 .access(defaultAccess)
									 .description("Test community for " + testName)
									 .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("Test Content for " + testName)
														 .build();	

		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
			
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		//Add widget - Ideation Blog
		log.info("INFO: Add Ideation Blog widget to community");
		ui.addWidget(BaseWidget.IDEATION_BLOG);
		
		//Click on the ideation blog link in the left nav
		log.info("INFO: Select Ideation blog from left nav menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(ui);

		//Click on ideation blog
		log.info("INFO: Select the default ideation blog link");
		blogUI.clickLinkWait(blogUI.getCommIdeationBlogLink(community));

		//Select New Idea button
		log.info("INFO: Select New Entry button");
		blogUI.clickLink(BlogsUIConstants.NewIdea);
		
		//Create a new idea
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogUI);
		
		//Verify that new idea exists
		log.info("INFO: Verify that the new idea exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(ideationBlogEntry.getTitle()), "ERROR: Entry not found"); 

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}


	/**
	 * addRelatedCommunity()
	 * <ul>
	 * <li><B>Info: </B>Add a related community to the Related Communities widget.</li>
	 * <li><B>Step: </B>Use API to create a community </li>
	 * <li><B>Step: </B> Click Community Actions > Add Apps</li>
	 * <li><B>Step: </B>Add Related Community widget</li>
 	 * <li><B>Step: </B>Select Related Community widget from left nav menu</li>
	 * <li><B>Step: </B>click Add a Community link</li>
	 * <li><B>Step: </B>Input the related community's name, url and description</li>
	 * <li><B>Step: </B>click Save button</li>
	 * <li><B>Verify: </B> Related community was created</li>	
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void addRelatedCommunity(){
		String testName = ui.startTest();
		
		String AddAComunityLink = "link=Add a Community";	
		
		log.info("user API to create a community");
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community forum "+rand;
			
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test community - " + testName)
									 .access(defaultAccess)
									 .build();
		

		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		//Get this community overview page's URL
		String overviewURL = serverURL	+"/communities/service/html/communityoverview?" + apiOwner.getCommunityUUID(comAPI);		

		//Add widget - Related Communities
		log.info("INFO: Add Related Communities widget to community");
		ui.addWidget(BaseWidget.RELATED_COMMUNITIES);
		
		// Select Related Communities from left nav menu
		log.info("INFO: Select Related Communities from left nav menu");
	    Community_LeftNav_Menu.RELATEDCOMMUNITIES.select(ui);
		
	    //Click on Add a Community link to launch Add a Community dialog
		log.info("click Add a Community Link from the Related Communities widget page");
		ui.clickLinkWait(AddAComunityLink);
		
		//Fill out the fields
		log.info("input the current community's URL, community name and description as the related community's info");
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityURL).type(overviewURL);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityName).type(communityName);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityDesc).type("testing Related Communities");
		
		log.info("click Save button in the Add a Community dialog");
		ui.clickSaveButton();
		
		//Verify the related community's description
		log.info("click More to check description of the community");
		ui.clickLinkWait(ForumsUIConstants.MoreLink);
		log.info("verify the related community is added");
		Assert.assertTrue(driver.isTextPresent("testing Related Communities"), "failed to check the related community's description");
	
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
			
		ui.endTest();		
	}
	
	
	/**
	 * addStatusMessage()
	 *<ul>
	 *<li><B>Info:</B> Create status message in Community</li>
	 *<li><B>Step:</B> Create a Public community as owner using API </li>
	 *<li><B>Step:</B> Select Status Updates from left nav menu </li>
	 *<li><B>Step:</B> Post a status message</li>
	 *<li><B>Verify: </B> Status was posted</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void addStatusMessage() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum )
										.access(Access.PUBLIC)
										.tags("commtag")
										.description("Test community - " + testName ).build();
	
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
			
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
			
		//Login as Owner
		ui.login(testUser);
			
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
						
		//Click on Status Update
		log.info("INFO: Click on Status Updates ");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
			
		//Type Status message
		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox("Posting a status message in community", true);
		
		//Ensures that the test is executed from the top of the page
		driver.executeScript("scroll(0, 0);");
			
		//Click on Post
		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
			
		//Test that Message has been posted successfully
		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
							"ERROR: The successfull message is not posted");
		
		//Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains("Posting a status message in community"),
							"Error: Status message is not getting displayed");
			
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
			
		ui.endTest();			
	}	
	
	
	/**
	* createCalendarEvent()
	*<ul>
	*<li><B>Info:</B>Tests creating an event through the Events widget</li>
	*<li><B>Step:</B>Create a community via API</li>
	*<li><B>Step:</B> Click Community Actions > Add Apps</li>
	*<li><B>Step:</B>Add Events widget</li>
	*<li><B>Step:</B>Open the community and click Events link in the left navigation menu</li>
	*<li><B>Step:</B>Create an event</li>
	*<li><B>Verify:</B> Event was created</li> 
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void createCalendarEvent() throws Exception {

		CalendarUI calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();

		
		//Create an event base state object
		BaseEvent event = new BaseEvent.Builder(testName + Helper.genDateBasedRand())
									   .tags("EventsTag" + Helper.genDateBasedRand())
									   .description(Data.getData().communitywidgettest + " " + testName)
									   .build();


		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Add widget - Events
		log.info("INFO: Add Events widget to community");
		ui.addWidget(BaseWidget.EVENTS);
		
		//Click on the Events link in the left nav
		log.info("INFO: Select Events from left navigation menu");
		Community_LeftNav_Menu.EVENTS.select(ui);

		//Create an Event
		event.create(calUI);
		
		//Validate that the page loads without Error
		ui.checkForErrorsOnPage();
		
		//Verify Event is created
		ui.fluentWaitTextPresent(event.getName());

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}	

	
	/**
    * createCommunityActivity()
	*<ul>
	*<li><B>Info:</B>Test the creation of an activity through the Activities widget</li>
	*<li><B>Step:</B>Create a community via API</li>
	*<li><B>Step:</B> Click Community Actions > Add Apps</li>
	*<li><B>Step:</B>Add the Activities widget</li>
	*<li><B>Step:</B>Open the community and click Activities link in the left navigation menu</li>
	*<li><B>Step:</B>Create an Activity</li>
	*<li><B>Verify:</B>Activity was created</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	*</ul>
	*@throws Exception
	*/	
	@Test(groups = { "regression", "regressioncloud"})
	public void createCommunityActivity(){

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();
	
		//Create an activity base state object
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		.tags(testName)
		.goal("Goal for "+ testName)
		.community(community)
		.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Add widget - Activities
		log.info("INFO: Add Activities widget to community");
		ui.addWidget(BaseWidget.ACTIVITIES);
		
		//Click on the Widget link in the nav
		log.info("INFO: Select Activities from left navigation menu");
		Community_LeftNav_Menu.ACTIVITIES.select(ui);
		
		//Create activity
		log.info("INFO: Create Activity");
		aui.create(activity);
		
		//Validate that the page loads without Error
		ui.checkForErrorsOnPage();
		
		//Verify activity is created
		ui.fluentWaitTextPresent(activity.getName());

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
	
		ui.endTest();
	}

	
	/**
	 * createFolderinFilesWidget()
	 *<ul>
	 *<li><B>Info:</B> Community owner creates a folder in Files widget</li>
	 *<li><B>Step:</B> Create a community using API</li>
	 *<li><B>Step:</B> Select Files in left nav drop down menu</li>
	 *<li><B>Step:</B> Create folder</li>
	 *<li><B>Verify:</B> Folder was created</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void createFolderinFilesWidget() throws Exception {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							               .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							               .tags("commTag")
		 							               .description("Test creating a folder in a community Files widget")
		 							               .build();
		
		BaseFolder folderA = new BaseFolder.Builder("CommunityFolderA")
		     							   .description("Description for CommunityFolderA")
		                                   .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Add widget
		log.info("INFO: Add media gallery widget to community");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.GALLERY);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		// Select Files from left menu
		log.info("INFO: Select Files from left nav menu");
		Community_LeftNav_Menu.FILES.select(ui);
 
        //Create a folder
		log.info("INFO: Create a folder");
		folderA.add(flUI);

		//Click on Community Folders from left nav
		log.info("INFO: Click on Community Folders from left nav");
		flUI.clickLinkWait(FilesUIConstants.navCommunityFolders);
			
		//Click on display list to show folders in list
        log.info("INFO: Click on Display List ");
        flUI.clickLinkWait(FilesUIConstants.DisplayList);
		
        //Verify folder was created
        log.info("INFO: Verify that the folder was created");
        Assert.assertTrue(driver.isElementPresent(FilesUI.selectMyFolder(folderA)),
				  "ERROR: Unable to find the folder");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
	}
	
	
	/**
	 * createSurvey()
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Use API to create a community</B> </li>
	 *<li><B>Step: Click Community Actions > Add Apps</B></li>
	 *<li><B>Step: Add Surveys widget</B> </li>
	 *<li><B>Step: Select Survey from left nav menu</B> </li>
	 *<li><B>Step: Create a survey</B> </li>
	 *<li><B>Verify: A survey is created </B> </li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 *Note: SmartCloud only
	 *Note: Surveys is a separate deployment which will not be deployed on bvtoracle or db2 servers hence
	 *disabling this test case. This test case functionality is covered in BVT_Level_2_Surveys, hence disabling it.
	 */
	@Test(groups = { "regressioncloud" }, enabled = false)
	public void createSurvey() throws Exception {

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test community - " + testName)
									 .access(defaultAccess)
									 .build();

		BaseSurveyQuestion question = new BaseSurveyQuestion.Builder(
				"Question A", BaseSurveyQuestion.Type.MULTIPLECHOICE_ONEANSWER)
				.addOption(	new BaseSurveyQuestion.Option("Answer A 1", "Answer A 1"))
				.addOption(	new BaseSurveyQuestion.Option("Answer A 2", "Answer A 2")).build();

		List<BaseSurveyQuestion> questions = Arrays.asList(question);

		BaseSurvey survey = new BaseSurvey.Builder(testName + Helper.genDateBasedRandVal())
				.description("Description for " + testName).questions(questions)
				.anonResponse(false).build();	

		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Add widget - Surveys
		log.info("INFO: Add Surveys widget");
		ui.addWidget(BaseWidget.SURVEYS);
			
		//Click on the Survey link in the left nav
		log.info("INFO: Select Survey from left nav menu");
		Community_LeftNav_Menu.SURVEYS.select(ui);
		
		//Survey creation
		log.info("INFO: Creating survey");
		svUI.createSurvey(survey);	
		svUI.fluentWaitTextPresent(survey.getName());
		
		log.info("INFO: Validate that survey is created");
		Assert.assertTrue(svUI.fluentWaitPresent(SurveysUI.addSurveyQuestionButton),
				"ERROR: Survey was not created successfuly");		

		//Add question to survey
		log.info("INFO: Adding question by question type");
		
		log.info("INFO: Validate that survey questions are added");
		Assert.assertTrue(svUI.addQuestionsByQuestionType(question),
				"ERROR: Issue adding questions to the survey");

		//Save survey
		log.info("INFO: Save survey");
		svUI.saveSurvey();
		
		log.info("INFO: Validate that survey with questions is saved");
		Assert.assertTrue(svUI.fluentWaitElementVisible(SurveysUI.surveySuccessImg),
				"ERROR: The survey was not saved successfully");		

		//Start survey
		log.info("INFO: Start survey");
		svUI.startSurvey(survey);
		Assert.assertTrue(svUI.fluentWaitElementVisible(svUI.getSurveyLinkInList(survey.getName())),
				"ERROR: Survey not started successfully");
		
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();		
	}
	
	
	/**
	 * createWikipage()
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and add a page to wiki widget</B> </li>
	 *<li><B>Verify: Wiki page was added</B> </li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void createWikipage() throws Exception {

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test community - " + testName)
									 .access(defaultAccess)
									 .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("wtag1, wtag2")
									.description("this is a test description for creating a Peer wiki page")
									.build();

		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		ui.fluentWaitElementVisible(CommunitiesUIConstants.communityActions);

		//Add Wiki widget if necessary
		if(!apiOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add Wiki widget to community");
			ui.addWidget(BaseWidget.WIKI);
		}
		
		//Select Wiki from left navigation menu
		log.info("INFO: Select Wikis from left navigation menu");
		Community_LeftNav_Menu.WIKI.select(ui);

		//Create a new wikipage
		log.info("INFO: Creating a WikiPage inside wiki");
		wikiPage.create(wikiUI);

		log.info("INFO: Validate that the wiki page is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(wikiPage.getName()),
							"ERROR: wiki page can not be found");
	
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();		
	}
	
	
	/**
	* postBlogEntry() 
	*<ul>
	*<li><B>Info: </B>Adding an Entry to Community Blog</li>
	*<li><B>Step: </B>[API]Create a Community with a description</li>
	*<li><B>Step: </B>[API]Create Blog widget</li>
	*<li><B>Step: </B>Click on Blog link in the left nav pane</li> 
	*<li><B>Step: </B>Select New Entry button</li>
	*<li><B>Step: </B>Add Entry</li>
	*<li><B>Verify: </B>Entry was created</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void postBlogEntry() throws Exception {
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .description("Test community - " + testName)
									 .access(defaultAccess)
									 .build();
		
		//Create a blog base state object
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
		 							.tags("btag1")
		 							.content("Test description for testcase " + testName)
		 							.build();

		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add widget - Blog
		log.info("INFO: Add Blog widget using API");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
				
		//Click on the blog link in the left nav
		log.info("INFO: Select blogs from left Navigation menu");
		Community_LeftNav_Menu.BLOG.select(ui);
			
		//Select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(blogUI);

		//Verify that new entry exists
		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), "ERROR: Entry not found");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}

	
	/**
	 * setupGallery()
	 * <ul>
	 * <li><B>Info: </B>Add files to the gallery widget</li>
	 * <li><B>Step: </B>Create a community using API</li>
	 * <li><B>Step: </B>Upload a file in Community using API</li>
	 * <li><B>Step: </B>Click Community Actions > Add Apps</li>
	 * <li><B>Step: </B>Add the Gallery widget</li>
	 * <li><B>Step: </B>Setup the gallery widget to point to all community files</li>
	 * <li><B>Verify: </B>Gallery widget title reflected the name of the community</li>
	 * <li><B>Verify: </B>Uploaded file appeared in the gallery widget</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 * </ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void setupGallery() throws Exception {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags("commtag")
		 							.description("Test Gallery in community")
		 							.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
									 .comFile(true)
									 .extension(".jpg")
									 .build();
	
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
     
		log.info("INFO: Upload a file using API");
		community.addFileAPI(commAPI, fileA, apiOwner, fileHandler);		
		
		//Add widget - Gallery
		log.info("INFO: Add Media Gallery widget to community");
		ui.addWidget(BaseWidget.GALLERY);
		
		//Click on Set up the Gallery link to launch Set Up a Gallery dialog box
		log.info("INFO: Click on Set up the Gallery link to launch Set Up a Gallery dialog box");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);
	
		//'All Community File' is selected as default, so just click on Set as Gallery button to close the dialog box
		log.info("INFO: All Community File is selected as default, click on Set as Gallery button to close the dialog box");
		driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();
		
		//Verify Gallery is visible on Community Overview page
		Assert.assertEquals(getGalleryTitle(commAPI), community.getName(),
				"INFO: Verified the gallery is in the Community with the Community title ");
				
		// Verify thumbnail for fileA is shown in Gallery
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"INFO: verified thumbnail for fileA is shown in Gallery");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}	
	

	/**
	* startForumsTopic()
	*<ul>
	*<li><B>Info: </B>Creating a community Forum Topic</li>
	*<li><B>Steps: </B>Create a community</li>
	*<li><B>Steps: </B>Select Forums widget from left nav menu</li>
	*<li><B>Steps: </B>Select Start a Topic</li>
	*<li><B>Steps: </B>Enter topic details and save</li>
	*<li><B>Verify: </B>Topic was created</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	*</ul>
	*/
	@Test(groups = { "regression", "regressioncloud" })
	public void startForumsTopic() throws Exception {
		
		String testName = ui.startTest();
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .access(defaultAccess)
												   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("Topic - " + testName)
										   		 .tags("tag1")
										   		 .description("description for " + testName)
										   		 .partOfCommunity(community)
										   		 .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Checking to see if widget is enabled.  If not enabled then enable it.");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add forum widget to community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//Select Forums from left menu
		log.info("INFO: Select Forums from left nav menu");
	    Community_LeftNav_Menu.FORUMS.select(ui);

	    //Make the page active
	    if(driver.isElementPresent(ForumsUIConstants.Start_A_Topic))
		    log.info("INFO: Locate Start A Topic button"); 
	    else
	    	log.info("INFO: Start A Topic button can not be found"); 
		
		//Create a new topic inside the Forum
		log.info("INFO: Create a new topic");
		topic.create(fmUI);

		//Verify Topic is created
		log.info("INFO: Verify Topic is created");
		Assert.assertTrue(driver.isTextPresent(topic.getTitle()),
			    "ERROR: Topic can not be found");		

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	

	/**
	 * uploadFileinLibraryWidget()
	 *<ul>
	 *<li><B>Info:</B> Community owner uploads a file in Library widget</li>
	 *<li><B>Step:</B> Create a community using API</li>
	 *<li><B>Step:</B> Click Community Actions > Add Apps</li>
	 *<li><B>Step:</B> Add Library widget</li>
	 *<li><B>Step:</B> Select Library in left nav menu</li>
	 *<li><B>Step:</B> Upload a file</li>
	 *<li><B>Verify:</B> File was uploaded</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Communities</a></li>
	 *</ul>
	 *Note: On Prem only
	 *Note: Library App widget is not supported for Connections 6.5 unless it
	 * was upgraded from Connections 6.0 that had FileNet deployed
	 * Hence disabling this 
	 */
	
	@Test(groups = { "regression" }, enabled = false)
	public void uploadFileinLibraryWidget() throws Exception {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							               .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							               .tags("commtag")
		 							               .description("Test: upload a file in a community Library widget")
		 							               .build();

		BaseFile file1 = new BaseFile.Builder(Data.getData().file1)
									 .extension(".jpg")
									 .build();	
		
	
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Add widget - Library
		log.info("INFO: Add Library widget to community");
		ui.addWidget(BaseWidget.LIBRARY);
			
		//Select Library widget in the left nav
		log.info("INFO: Select Library from left nav menu");
		Community_LeftNav_Menu.LIBRARY.select(ui);
		      
 		//Upload a file 
		log.info("INFO: Upload a file");
		flUI.libraryFileUpload(file1.getName());

		//Change to Details view to do the verification
		log.info("INFO: Change to the Details View");
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
        viewSelector.switchToView(ViewSelector.View.DETAILS_VIEW);

        //Validate file1 is visible
		log.info("INFO: Verify that the file is uploaded and displayed in the view");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.UploadedFileNameInView+"('"+file1.getName()+"')").getText().contains(file1.getName()),
							"ERROR: Uploaded File can not be found in Details view");
  
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
	}
		

	
	
    /**
    * Verify Gallery is visible on Community Overview page
	*/	
	private String getGalleryTitle(Community commAPI) {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
        String commUUID = apiOwner.getCommunityUUID(commAPI);
		
		log.info("INFO: commUID is " + commUUID);
		
		String widgetID = apiOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Gallery");
		
		logger.strongStep("Gallery id is 'widgetID' ");
		log.info("INFO: Gallery id is " + widgetID);
		
		logger.strongStep("Wait for Gallery title to refresh");
		log.info("INFO: Waiting for Gallery title to refresh");
			
		String galleryName = driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetID)).getText();
		
		logger.strongStep("Gallery name is 'gallery name'");
		log.info("INFO: Gallery name is " + galleryName);
		
		return galleryName;	
	}

	
	/**
	* Check if the LibraryTitle is not present then refreshes the page
	*/		
	private LibraryWidget getLibraryWidgetOnFullWidgetPage() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Get library widget on full widget page");
		//Check if the LibraryTitle is not present then refreshes the page.
		if(!driver.getSingleElement(CommunitiesUIConstants.CommunitiesFullpageWidgetContainer).isElementPresent(CommunitiesUIConstants.LibraryTitle))
			ui.fluentWaitPresentWithRefresh(CommunitiesUIConstants.LibraryTitle);
		return new LibraryWidget(driver.getSingleElement(CommunitiesUIConstants.CommunitiesFullpageWidgetContainer));
	}

}
