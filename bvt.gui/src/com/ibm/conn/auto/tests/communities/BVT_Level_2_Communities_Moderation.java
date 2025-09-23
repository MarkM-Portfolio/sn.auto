/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_Communities_Moderation extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities_Moderation.class);
	private CommunitiesUI ui;
	private BlogsUI bUI;
	private ForumsUI fUI;
	private FilesUI filesUI;
	private TestConfigCustom cfg;	
	private User testUser1;
	private User testUser2;
	private User testModerator,adminUser;
	private APICommunitiesHandler apiOwner;
	private boolean isOnPrem;
	private GatekeeperConfig gkc;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();		
		testUser2 = cfg.getUserAllocator().getUser();
		testModerator = cfg.getUserAllocator().getGroupUser("global_mods");
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
		// for checking Gatekeeper settings
		isOnPrem = cfg.getProductName().equalsIgnoreCase("onprem");
		adminUser = cfg.getUserAllocator().getAdminUser();
		if (isOnPrem) {
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else {
			gkc = GatekeeperConfig.getInstance(driver);
		}
	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		fUI = ForumsUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);

	}

	/**
	 *<ul>
	 *<li><B>Prerequisite:</B> Moderation needs to be setup on a separate server - This is a multi step test</li>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Steps: </B>Create a Moderate Community via the API</li>
	 *<li><B>Steps: </B>Select Community Actions > Add Apps and then add the Ideation Blog widget</li>
	 *<li><B>Steps: </B>Logout with the first user</li>
	 *<li><B>Steps: </B>Login as a second user</li>
	 *<li><B>Steps: </B>Navigate to the newly created community</li>
	 *<li><B>Steps: </B>Click Join this community to join the community</li>
	 *<li><B>Verify: </B>Text stating that you have been added to the community</li>
	 *<li><B>Steps: </B>Click Ideation Blog in the left nav drop down menu</li>
	 *<li><B>Steps: </B>Click the Contribute and Idea button and create a New Idea</li>
	 *<li><B>Verify: </B>Changes have been saved and sent to moderator for approval</li>
	 *<li><B>Steps: </B>Logout with user two</li>
	 *<li><B>Steps: </B>Login with user one</li>
	 *<li><B>Steps: </B>Navigate to the recently created community</li>
	 *<li><B>Steps: </B>Select Moderation in the left nav drop down menu</li>
	 *<li><B>Verify: </B>Content for Ideation Blog is present</li>
	 *<li><B>Steps: </B>Approve the content and logout</li>
	 *<li><B>Steps: </B>Login as user two</li>
	 *<li><B>Verify: </B>Content has been approved</li>
	 *</ul>
	 *@rewrite Ralph LeBlanc
	 */
	@Test(groups = {"level2", "bvt"})
	public void ModerateIdeationBlogs() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a moderated community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
												   .description(Data.getData().commonDescription)
												   .approvalRequired(true)
												   .build();
		
		BaseBlogPost NewIdea = new BaseBlogPost.Builder("Brand New Idea " + Helper.genDateBasedRandVal())
											   .tags("IdeaTag" + Helper.genDateBasedRandVal())
											   .content("BVT Ideation blog content").build();
		

		//create community
		logger.strongStep("create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget
		logger.strongStep("add widget");
		log.info("INFO: Add ideation blogs widget to community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		//GUI START
		//Load the component and Login
		logger.strongStep("Load Communities moderation and login: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);

		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//navigate to the API community
		logger.strongStep("navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Join the community
		logger.strongStep("Join the community");
		log.info("INFO: Join the community " + community.getName());
		ui.joinCommunity(community.getName());
		
		//create default parent
		logger.strongStep("create a default parent");
		NewIdea.setBlogParent(new BaseBlog.Builder(community.getName(),"").build());
		
		// Click on the Ideation Blog link in the nav
		// If GK is enabled use TabbedNav, else use LeftNav
		// Click on the Ideation blogs link in the nav
		logger.strongStep("Click on the Ideation Blog link in the navigation menu");
		log.info("INFO: Select Ideation blog from left nav menu");
		//Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		String gk_flag = Data.getData().commTabbedNav;
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = ui.checkGKSetting(gk_flag);
		
		if (value) {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Ideation Blogs link in the nav");
			log.info("INFO: Select Ideation blogs from the tabbed nav menu");
			Community_TabbedNav_Menu.IDEATIONBLOG.select(ui, 2);
		} else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Ideation Blogs link in the nav");
			log.info("INFO: Select Ideation blogs from left nav menu");
			Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		}
		
		logger.strongStep("Select the default ideation blog");
		log.info("INFO: Select the default ideation blog");
		driver.getFirstElement("css=table[dojoattachpoint='tableListAP'] h4[class='lotusBreakWord']>a:contains("+ community.getName() + ")").click();

		// select New Entry button
		logger.strongStep("select New Entry button");
		log.info("INFO: Select New Entry button");
		ui.clickLink(BlogsUIConstants.NewIdea);
		
		// Create an Ideation Blog : New Idea Entry
		logger.strongStep("Create an Ideation blog with a new idea entry");
		log.info("INFO: Create an Ideation Blog : New Idea");
		NewIdea.create(bUI);

		logger.strongStep("Wait for this text to appear: Your content has been submitted to moderator for approval");
		ui.fluentWaitTextPresent("Your content has been submitted to moderator for approval");
		log.info("INFO: Ideation Blog was submitted to the moderator for approval as expected");
		
		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();		

		//Load the component and Login with community owner
		logger.strongStep("Load the component and login as community owner: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//navigate to the API community
		logger.strongStep("navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Select Moderate from navigation menu");
		log.info("INFO: Select Moderate from navigation menu");
		// frozen browser observed when using Com_Action_Menu so use the nav bar instead.
		// Com_Action_Menu.MODERATE.select(ui);
		Community_TabbedNav_Menu.MODERATION.select(ui);

		// Open Blogs - Entries 
		logger.strongStep("Open Blogs: Entries and validate the view");
		log.info("Validating Blogs view ");
		ui.selectTreeOption("Content Approval", "Entries");
		
		//Verify file uploaded is present
		logger.weakStep("Verify file uploaded is present");
		log.info("INFO: Validate the new idea is present for the moderator to be approved");
		Assert.assertTrue(driver.isTextPresent(NewIdea.getTitle()), 
				 		"ERROR " + NewIdea.getTitle() + " should be present in Moderation view.");

		// Approve the items
		logger.strongStep("Approve the Ideation Blog Entry");
		log.info("INFO: Approving Ideation Blog Entry");
		ui.approveItems();		
		ui.clickLinkWait(CommunitiesUIConstants.ContentApproval);

		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
				
		//Load the component and Login with community owner
		logger.strongStep("Load the component and Login as community owner: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		log.info("INFO: User logged in : " + testUser2.getDisplayName());

		//Open Public Communities View
		logger.strongStep("Open Public Communities View");
		log.info("INFO: Open users 'Public Communities' view");
		ui.goToPublicView(isCardView);
		
		//Find and open community
		logger.strongStep("Find and open community");
		log.info("INFO: Open community " + community.getName());
		ui.clickLinkWait(communityLink);

		// Use Left Navigation bar and select Ideation Blog
		// If GK is enabled use TabbedNav, else use LeftNav
		// Click on the Ideation blogs link in the nav
		logger.strongStep("Use Left Navigation bar and select Ideation Blog");
		log.info("INFO: Use left navigation bar and select Ideation Blog");
		// Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		if (value) {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Ideation Blogs link in the nav");
			log.info("INFO: Select Ideation blogs from the tabbed nav menu");
			Community_TabbedNav_Menu.IDEATIONBLOG.select(ui, 2);
		} else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Ideation Blogs link in the nav");
			log.info("INFO: Select Ideation blogs from left nav menu");
			Community_LeftNav_Menu.IDEATIONBLOG.select(ui);
		}

		logger.strongStep("Go to default Idea");
		log.info("INFO: Go to default Idea");
		driver.getFirstElement("css=table[dojoattachpoint='tableListAP'] h4[class='lotusBreakWord']>a:contains("+ NewIdea.getBlogParent().getName() + ")").click();
	
		// Verify that it has been submitted for approval
		logger.weakStep("Validate that the approved Ideation Blog Entry (New Idea) is now visible");
		log.info("INFO: Validate that the approved Ideation Blog Entry " + NewIdea.getTitle() + " is now visible");
		Assert.assertTrue(driver.isTextPresent(NewIdea.getTitle()), NewIdea.getTitle() + " : Topic not present in community");
		
		apiOwner.deleteCommunity(comAPI);			

		ui.endTest();
		
	}

	/**
	 *<ul>
	 *<li><B>Prerequisite:</B> Moderation needs to be setup on a separate server - This is a multi step test</li>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Steps: </B>Create a Moderate Community via the API</li>
	 *<li><B>Steps: </B>Click Community Actions < Add Apps and add the Blog widget</li>
	 *<li><B>Steps: </B>Logout with the first user</li>
	 *<li><B>Steps: </B>Login with a second user and navigate the the newly created community</li>
	 *<li><B>Steps: </B>Click Join this Community</li>
	 *<li><B>Verify: </B>Text stating that you have joined the community</li>
	 *<li><B>Steps: </B>Select Blog from the left nav pane drop down menu</li>
	 *<li><B>Steps: </B>Click New Entry to create a new Blog Entry</li>
	 *<li><B>Verify: </B>Your entry has been submitted for approval</li>
	 *<li><B>Steps: </B>Logout with user two</li>
	 *<li><B>Steps: </B>Login with user one</li>
	 *<li><B>Steps: </B>Navigate to the new community and select Moderation from the left nav pane drop down menu</li>
	 *<li><B>Verify: </B>Content is present to be approved</li>
	 *<li><B>Steps: </B>Approve all content</li>
	 *<li><B>Steps: </B>Logout with user one</li>
	 *<li><B>Steps: </B>Login with user two</li>
	 *<li><B>Verify: </B>Content has been approved</li>
	 *</ul>
	 *@rewrite Ralph LeBlanc
	 */
	@Test(groups = {"level2", "bvt"})
	public void ModerateBlogs() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a moderated community base state object
		logger.strongStep("Create a moderated community");
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
												   .description(Data.getData().commonDescription)
												   .approvalRequired(true)
												   .build();
		
		//create a blog post
		logger.strongStep("create a blog post");
		BaseBlogPost blogPost = new BaseBlogPost.Builder(testName + Helper.genDateBasedRandVal())
														.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
														.content(Data.getData().commonDescription)
														.build();


		//create community
		logger.strongStep("create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget
		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}
		
		//GUI START
		//Load the component and Login
		logger.strongStep("Load communities moderation and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		

		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			
		//navigate to the API community
		logger.strongStep("navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Join the community
		logger.strongStep("Join the community");
		log.info("INFO: Join the community " + community.getName());
		ui.joinCommunity(community.getName());
		
		// Open the Blog view and Create Blog Entry
		logger.strongStep("Select blogs from the left navigation menu");
		log.info("INFO: Select Blogs from left navigation menu");
		Community_LeftNav_Menu.BLOG.select(ui);

		logger.strongStep("Select Blogs New Entry Button");
		log.info("INFO: Select Blogs New Entry Button");
		ui.clickLinkWait(CommunitiesUIConstants.BlogsNewEntryButton);

		logger.strongStep("Create a new Blog Entry");
		log.info("INFO: Create a new Blog Entry");
		blogPost.create(bUI);

		logger.strongStep("Wait for Text: Your entry has been submitted to moderator for approval.");
		ui.fluentWaitTextPresent("Your entry has been submitted to moderator for approval.");
		log.info("INFO: Blog was submitted to the moderator for approval as expected");
		
		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Load communities moderation and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//navigate to the API community
		logger.strongStep("navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Select Moderate from navigation menu");
		log.info("INFO: Select Moderate from navigation menu");
		// frozen browser observed when using Com_Action_Menu so use the nav bar instead.
		// Com_Action_Menu.MODERATE.select(ui);
		Community_TabbedNav_Menu.MODERATION.select(ui,2);
		
		// Open Blogs Entries - Content 
		logger.strongStep("Open Blogs Entries: Content");
		log.info("Validating Moderation view for Blogs");
		ui.selectTreeOption("Content Approval", "Entries");

		//Verify blog is present
		logger.weakStep("Validate the blogpost is present for the moderator to be approved");
		log.info("INFO: Validate the blogpost is present for the moderator to be approved");
		Assert.assertTrue(driver.isTextPresent(blogPost.getTitle()), 
				 		"ERROR " + blogPost.getTitle() + " should be present in Moderation view.");

		// Approve the items
		logger.strongStep("Approve the blog entries");
		log.info("INFO: Approve the items");
		ui.approveItems();
		
		// Return the view to normal unexpanded tree
		logger.strongStep("Return the view to the normal unexpanded tree");
		ui.clickLinkWait(CommunitiesUIConstants.ContentApproval);

		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Load communities moderation and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		log.info("INFO: User logged in : " + testUser2.getDisplayName());

		// Open the Community
		logger.strongStep("Open the Community");
		ui.goToPublicView(isCardView);
		ui.clickLinkWait(communityLink);

		// Open the Blog community view
		logger.strongStep("Select blogs from the left navigation menu");
		log.info("INFO: Select blogs from left navigation menu");
		Community_LeftNav_Menu.BLOG.select(ui);

		// Validate that the contributor can see materials that have been approved are visible
		logger.weakStep("Validate that blog post (blog name) is present in the community");
		log.info("Validating" + blogPost.getTitle() + " is present in community");
		Assert.assertTrue(driver.isTextPresent(blogPost.getTitle()), blogPost.getTitle() + " : Entry not present in community");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Prerequisite:</B> Moderation needs to be setup on a separate server - This is a multi step test</li>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: </B> Create a moderated community via the API</li>
	 *<li><B>Step: </B>Logout with the first user</li>
	 *<li><B>Step: </B>Login with a second user</li>
	 *<li><B>Step: </B>Navigate to the newly created community and Join the Community</li>
	 *<li><B>Step: </B>Select Start the First Topic button on the overview page and create a Forum</li>
	 *<li><B>Verify: </B>The forum has been submitted for approval</li>
	 *<li><B>Step: </B>Logout with user two</li>
	 *<li><B>Step: </B>Login with user one</li>
	 *<li><B>Step: </B>Navigate to the recently created community</li>
	 *<li><B>Step: </B>Click Moderation in the left nav pane drop down menu</li>
	 *<li><B>Verify: </B>Content is available to be approved</li>
	 *<li><B>Step: </B>Approve all content</li>
	 *<li><B>Step: </B>Logout with user one</li>
	 *<li><B>Step: </B>Login with user two</li>
	 *<li><B>Step: </B>Verify that content has been approved</li>
	 *</ul>
	 *@rewrite Ralph LeBlanc
	 */
	@Test(groups = {"level2", "bvt"})
	public void ModerateForums() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
												   .description(Data.getData().commonDescription)
												   .approvalRequired(true)
												   .build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder("Moderation Forum Post" + Helper.genDateBasedRandVal())
  		 										      .tags(Data.getData().ForumTopicTag)
  		 										      .description(Data.getData().commonDescription)
  		 										      .partOfCommunity(community)
  		 										      .build();

		//create community
		logger.strongStep("create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI START
		//Load the component and Login
		logger.strongStep("load communities moderation and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//navigate to the API community
		logger.strongStep("Navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Join the community
		logger.strongStep("Join the community");
		log.info("INFO: Join the community " + community.getName());
		ui.joinCommunity(community.getName());

		// Create forum topic
		logger.strongStep("Create forum topic");
		log.info("Create a new Forum Topic.");	
		forumTopic.create(fUI);

		logger.strongStep("Wait for text: The topic has been submitted for review");
		ui.fluentWaitTextPresent("The topic has been submitted for review");
		log.info("INFO: Forum topic was submitted to the moderator for approval as expected");
		
		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Load Communities moderation and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//navigate to the API community
		logger.strongStep("Navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Select Moderate from navigation menu");
		log.info("INFO: Select Moderate from navigation menu");
		// frozen browser observed when using Com_Action_Menu so use the nav bar instead.
		// Com_Action_Menu.MODERATE.select(ui);
		Community_TabbedNav_Menu.MODERATION.select(ui);

		// Open Forums - Posts 
		logger.strongStep("Open Forums: Posts ");
		log.info("Validating Moderation view for Forums");
		ui.selectTreeOption("Content Approval", "Posts");
		
		//Verify Forums is present
		logger.weakStep("Verify Forums is present");
		log.info("INFO: Validate the forum topic is present for the moderator to be approved");
		Assert.assertTrue(driver.isTextPresent(forumTopic.getTitle()), 
				 		"ERROR " + forumTopic.getTitle() + " should be present in Moderation view.");

		// Approve the items
		logger.strongStep("Approve the items");
		log.info("INFO: Approve items");
		ui.approveItems();
		
		// Return the view to normal unexpanded tree
		logger.strongStep("Return the view to normal unexpanded tree");
		ui.clickLinkWait(CommunitiesUIConstants.ContentApproval);

		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Open commnities moderation and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		log.info("INFO: User logged in : " + testUser2.getDisplayName());
		
		// Open the Community
		logger.strongStep("Open community");
		ui.goToPublicView(isCardView);
		ui.clickLinkWait(communityLink);
		
		// Open the Forums community view
		logger.strongStep("Open the Forums community view");
		log.info("INFO: Select Forums from left navigation menu");
		Community_LeftNav_Menu.FORUMS.select(ui);

		// Verify that it has been submitted for approval
		// Validate that the contributor can see materials that have been approved are visible
		logger.weakStep("Verify that forum topic (name of forum topic) is present in the community");
		log.info("Validating" + forumTopic + " Topic is present in community");
		Assert.assertTrue(driver.isTextPresent(forumTopic.getTitle()), forumTopic + " : Topic not present in community");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Prerequisite:</B> Moderation needs to be setup on a separate server - This is a multi step test</li>
	 *<li><B>Info:</B> Test that a file can be submitted and approved on a moderated Server</li>
	 *<li><B>Steps:</B> Create a Community via the API</li>
	 *<li><B>Steps:</B>Logout with user one</li>
	 *<li><B>Steps:</B>Login with a second user</li>
	 *<li><B>Steps:</B> Navigate to the Community and then click Join this Community</li>
	 *<li><B>Steps:</B>Click Files in the left nav pane drop down menu</li>
	 *<li><B>Steps:</B> Upload a file</li>
	 *<li><B>Verify:</B> File has been submitted for review</li>
	 *<li><B>Steps:</B> Logout with user two</li>
	 *<li><B>Steps:</B>Login with user one</li>
	 *<li><B>Steps:</B>Click Moderation in the left nav pane drop down menu</li>
	 *<li><B>Verify:</B>File shows up in the moderated view for approval</li>
	 *<li><B>Steps:</B> Approve all files</li>
	 *<li><B>Steps:</B> Logout with user one</li>
	 *<li><B>Steps:</B>Login with user two</li>
	 *<li><B>Verify:</B> Content has been approved</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "bvt"})
	public void ModerateFiles() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
												   .description(Data.getData().commonDescription)
												   .approvalRequired(true)
												   .build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		//create community
		logger.strongStep("create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI START
		//Load the component and Login
		logger.strongStep("Load communities moderation and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);

		//navigate to the API community
		logger.strongStep("navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Join the community
		logger.strongStep("Join the community (name of community) ");
		log.info("INFO: Join the community " + community.getName());
		ui.joinCommunity(community.getName());
		
		//Select Files from the left navigation menu
		logger.strongStep("Select Files from the left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(ui);

		// Upload community-owned file
		logger.strongStep("Upload community-owned file");
		log.info("INFO: Add a new file: " + fileA.getName());
		fileA.upload(filesUI);
		
		//Validate the file has been submitted for review message
		logger.weakStep("Upload community-owned file");
		log.info("INFO: Validate that the uploaded File has been submitted for review");
		Assert.assertTrue(ui.fluentWaitTextPresent("The file " + fileA.getName() + " has been submitted for review and will be available when approved."),
						  "ERROR: File has been submitted for review messages is not present");		
		
		//Logout
		logger.strongStep("Logout");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Load the component and Login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//navigate to the API community
		logger.strongStep("navigate to the community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Select Moderate from navigation menu");
		log.info("INFO: Select Moderate from navigation menu");
		// frozen browser observed when using Com_Action_Menu so use the nav bar instead.
		// Com_Action_Menu.MODERATE.select(ui);
		Community_TabbedNav_Menu.MODERATION.select(ui);

		// Open Files - Content 
		logger.strongStep("Open Files - Content");
		log.info("Validating Moderation view for Files");
		ui.selectTreeOption("Content Approval", "Content");

		//Verify file uploaded is present
		logger.weakStep("Verify file uploaded is present for the community owner to approve");
		log.info("INFO: Validate the file uploaded is present for the community owner to be approved");
		Assert.assertTrue(driver.isTextPresent(fileA.getName()), 
						 "ERROR " + fileA.getName() + " should be present in Moderation view.");

		//Logout
		logger.strongStep("Logout and quit browser");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Open Communities Moderation and login: " +testModerator.getDisplayName());
		ui.loadComponent(Data.getData().ComponentModeration);
		ui.login(testModerator);

		// Open Files - Content 
		logger.strongStep("Open Files - Content");
		log.info("Validating Moderation view for Files");	
		ui.selectTreeOption("Content Approval", "Content");

		//Verify file uploaded is present
		logger.weakStep("Verify file uploaded is present");
		log.info("INFO: Validate the file uploaded is present for the moderator to be approved");
		Assert.assertTrue(driver.isTextPresent(fileA.getName()), 
				 		"ERROR " + fileA.getName() + " should be present in Moderation view.");
		
		// Approve the items
		logger.strongStep("Approve the items");
		log.info("INFO: Approve all items");
		ui.approveItems();

		//Logout
		logger.strongStep("Logout");
		log.info("INFO: Logout and quit browser");
		ui.logout();
		driver.quit();	
		
		//Load the component and Login
		logger.strongStep("Load Communities Moderation and Login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);

		//navigate to the API community
		logger.strongStep("navigate to the API community");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		// Open the File community view
		logger.strongStep("Open the File community view");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(ui);
	
		// Validate that the contributor can see materials that have been approved are visible
		logger.weakStep("Validate that file (file name) is present in the community");
		log.info("Validating" + fileA.getName() + " is present in community");
		Assert.assertTrue(driver.isTextPresent(fileA.getName()), 
						"ERROR: " + fileA.getName() + " : Entry not present in community");
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
}
