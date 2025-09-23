package com.ibm.conn.auto.tests.homepage;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Homepage_AS extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_AS.class);
	private TestConfigCustom cfg;
	private HomepageUI ui;
	private OrientMeUI omUI;
	private ActivitiesUI uiAct;
	private APIBlogsHandler apiBlog;
	private APICommunitiesHandler apiOwner;
	private User testUser;
	private String homepageURI, serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		omUI = OrientMeUI.getGui(cfg.getProductName(),driver);
		uiAct = ActivitiesUI.getGui(cfg.getProductName(),driver);
	
		testUser = cfg.getUserAllocator().getUser(this);	
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		homepageURI = Data.getData().ComponentHomepage.split("/")[0];
		
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Tests the links that appear in the navigation panel</li>
	*<li><B>Step:</B>Open the Homepage component</li>
	*<li><B>Verify:</B>Check all the different links are present on the left navigation panel</li>
	*
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*/
	@Test(groups = {"level2", "mt-exclude", "regressioncloud", "bvt"})
	public void verifyNewsfeed() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
		logger.strongStep("Load Homepage and Login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
		
		//Check that all links are displayed in the left nav
		logger.weakStep("Validate that all the links (Getting Started, Updates, Action Required, Saved, and My Notifications)are displayed in the left navigation menu)");
		log.info("INFO: Validate links are displayed in the left nav");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.scGettingStarted));
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.Updates));
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.ActionRequired));
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.Saved));
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.MyNotifications));
		
		//verifyMyPageLink
		logger.strongStep("Verify 'My Page' link");
		ui.verifyMyPageLink();
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test that a status will appear in the Discovery Stream</li>
	*<li><B>Step:</B>Post a new status update</li>
	*<li><B>Step:</B>Click on Discover</li>
	*<li><B>Verify</B>Check the status update is visible in the Discovery Stream</li>
	*<li><B>Step:</B>Click on the entry in the AS to invoke the EE for this entry</li>
	*<li><B>Verify:</B>Check the status update is appearing in the AS and EE</li>
	*
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "regression", "regressioncloud", "bvt"})
	public void updateStatusAndVerifyEE() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String StatusUpdateMessage = Data.getData().UpdateStatus + Helper.genDateBasedRandVal(); 
		
		ui.startTest();

		//Load component and login
		logger.strongStep("Load Homepage and login");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);
		
		//click Status Updates
		logger.strongStep("Click 'Status Updates' in the left navigation menu");
		log.info("INFO: Click Updates on left menu");
		ui.clickLinkWait(HomepageUIConstants.Updates);

		//click Status Updates tab
		logger.strongStep("CLick on the 'Status Updates' tab");
		log.info("INFO: Click Status Updates tab");
		ui.clickLinkWait(HomepageUIConstants.StatusUpdates);
		
		//Add a status update
		logger.strongStep("Add a status update");
		log.info("INFO: Add a status update");
		ui.statusUpdate(StatusUpdateMessage);
		
		//Open the Discover view
        logger.strongStep("Open the 'Discover' View");
		log.info("INFO: Open the Discover view");
		ui.clickLink(HomepageUIConstants.Updates);
		ui.clickLinkWait(HomepageUIConstants.DiscoverTab);
		
		//Verify that update appears in AS
		logger.strongStep("Verify that the status update appears in AS");
		log.info("INFO: Verify that update appears in AS");
		ui.fluentWaitTextPresent(Data.getData().FeedForEntries);
		
		//click on status update to display EE
		logger.strongStep("Click on the status update to display EE");
		log.info("INFO: click on status update to display EE");
		ui.filterBy("Status Updates");
		ui.fluentWaitTextPresent(Data.getData().FeedForEntries);
		ui.clickLinkWait("css=div.lotusPostContent div.lotusPostAction:contains("+StatusUpdateMessage+")");
		
		//verify that EE id displayed
		logger.weakStep("Verify that the Embedded Experience ID displayed");
		log.info("INFO: Verify the Embedded Experience id displayed");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.EE_Header),
						 "EE element does not show up.");
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Validates blog creation, filtering, and the "Save this link" button</li>
	*<li><B>Step:</B>Create Blog</li>
	*<li><B>Step:</B>Click on the Discover link</li>
	*<li><B>Step:</B>Use the Filter By dropdown and choose Blogs</li>
	*<li><B>Step:</B>Find the blog that was created earlier. Click "Save this link"</li>
	*<li><B>Step:</B>Reload the Discover view</li>
	*<li><B>Verify:</B>Check the link has changed to "Saved"</li>
	*<li><B>Verify:</B>Check that the Blogs entry is present</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*note: this is not supported on the cloud
	*/
	@Test(groups = {"regression", "mt-exclude", "icStageSkip", "bvt"})
	public void blogsEntrySaved() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseBlog blog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), testName + Helper.genDateBasedRand())
		 							.description("Description for " + testName)
		 							.build();		

		//Create blog 
		apiBlog = initBlogsAPI(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		logger.strongStep("Create a blog using API");
		log.info("INFO: Create blog using API");
		blog.createAPI(apiBlog);
		
		//GUI START
		//Load component and login with community owner
		//ui.loadComponent(homepageURI, testUser);
		
		//security check.
		logger.strongStep("Load homepage and login");
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
		{	ui.loadComponent(homepageURI);
			ui.login(testUser);	
		}else
			LoginEvents.loginToClassicHomepage(ui, omUI, driver, testUser, true);
		
		//Close tour if open
		driver.executeScript(ui.getCloseTourScript());
		
		//Goto Discover
		logger.strongStep("Select 'Discover'");
		log.info("INFO: Select Discover");
		ui.gotoDiscover();

		//Select filter by Blogs
		logger.strongStep("Select 'Filter by Blogs'");
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//Save News Story
		logger.strongStep("Save News Story");
		log.info("INFO: Save News Story");
		ui.saveNewsStory("created a blog named " + blog.getName());

		//Goto discover
		logger.strongStep("Go to Updates and Click 'Discover'");
		log.info("INFO: Goto updates and click discover");
		ui.gotoDiscover();
		
		//Select filter by Blogs
		logger.strongStep("Select 'Filter by Blogs'");
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//validate that the news item shows up
		logger.weakStep("Validate that you can find the Discover Views Blog Filter");
		log.info("INFO: Validate that you can find the Discover Views Blog Filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(blog.getName()), 
		  					"ERROR: Story doesn't show up Discover view blogs filter");
		
		//Validate Saved News Story
		logger.strongStep("Validate Saved News Story");
		log.info("INFO: Validate Saved News Story");
		ui.clickLink(HomepageUIConstants.Saved);
		
		//Select filter by Blogs
		logger.strongStep("Select 'Filter by Blogs'");
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.fluentWaitPresent(HomepageUIConstants.SavedView);
		
		log.info("INFO: Validate that story is appearing in the Saved view");
		logger.weakStep("Validate that story appears in the 'Saved' View");
		Assert.assertTrue(ui.fluentWaitTextPresent(blog.getName()), 
						  "ERROR: Story doesn't show up in the Saved view");

		ui.endTest();
	}
		
	/**
	*<ul>
	*<li><B>Info:</B>Tests creating and saving a blog within a community</li>
	*<li><B>Step:</B>Create a Blog in a community</li>
	*<li><B>Step:</B>Click on the Discover link</li>
	*<li><B>Step:</B>Use the Filter By dropdown and choose Blogs</li>
	*<li><B>Step:</B>In the blog created, click the "Save this link"</li>
	*<li><B>Step:</B>Reload the Discover view</li>
	*<li><B>Verify:</B>Check the entry. Link should now be changed to Saved</li>
	*<li><a HREF="">TTT Link to this test</a></li>
	*</ul>
	*note: this is not supported on the cloud
	*/
	@Test(groups = {"cplevel2", "level2", "mt-exclude", "bvt"})
	public void comBlogEntrySaved() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String rand = Helper.genDateBasedRand();
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rand)
												   .description("Community description for " + testName)
												   .build();

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
									.description("Blog description for " + testName)							
									.build();		

 		BaseBlogPost blogEntry = new BaseBlogPost.Builder(blog.getName())
 												 .blogParent(blog)
 												 .content("Blog Entry for " + testName)
 												 .build();
		
		
		//create community
 		logger.strongStep("Create Community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}
		
		//Add blog entry to community
		apiBlog = initBlogsAPI(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		logger.strongStep("Create a blog for blog entry to attach to using API");
		log.info("INFO: Create blog for blog entry to attach to");
		Blog blogAPI = blog.createAPI(apiBlog, comAPI);
		
		//Add blog entry
		logger.strongStep("Create a community blog entry using API");
		log.info("INFO: Create community blog entry");
		blogEntry.createAPI(apiBlog, blogAPI);
		
		//GUI START
		//Load component and login with community owner
		logger.strongStep("Load homepage and login as community owner");
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))	//security check
		{	ui.loadComponent(homepageURI);
			ui.login(testUser);	
		}else
			LoginEvents.loginToClassicHomepage(ui, omUI, driver, testUser, true);
		
		//Goto Discover
		logger.strongStep("Select 'Discover'");
		log.info("INFO: Select Discover");
		ui.gotoDiscover();

		//Select filter by Blogs
		logger.strongStep("Select 'Filter by Blogs'");
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//Save News Story
		logger.strongStep("Save news Story");
		log.info("INFO: Save News Story");
		ui.saveNewsStory("created the " + blog.getName() + " community blog.");
		
		//Goto discover
		logger.strongStep("Go to 'Updates' and click 'Discover'");
		log.info("INFO: Goto updates and click discover");
		ui.gotoDiscover();
		
		//Select filter by Blogs
		logger.strongStep("Select 'Filter by Blogs'");
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		//validate that the news item shows up
		logger.weakStep("Validate that the story shows up in 'Discover View Blogs Filter'");
		log.info("INFO: Validate that you can find");
		Assert.assertTrue(ui.fluentWaitTextPresent(blog.getName()), 
		  					"ERROR: Story doesn't show up Discover view blogs filter");
		
		//Validate Saved News Story
		logger.strongStep("Validate saved news story");
		log.info("INFO: Validate Saved News Story");
		ui.clickLink(HomepageUIConstants.Saved);
		
		//Select filter by Blogs
		logger.strongStep("Select 'Filter by Blogs'");
		log.info("INFO: Select filter by Blogs");
		ui.filterBy("Blogs");
		
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.fluentWaitPresent(HomepageUIConstants.SavedView);
		
		log.info("INFO: Validate that story is appearing in the Saved view");
		logger.weakStep("Validate that story appears in saved view");
		Assert.assertTrue(ui.fluentWaitTextPresent(blog.getName()), 
						  "ERROR: Story doesn't show up in the Saved view");

		logger.strongStep("Delete community that created using API");
		apiOwner.deleteCommunity(comAPI);
		ui.endTest();
	}
		
	private APIBlogsHandler initBlogsAPI(String serverURL, String loginPref, String pwd)  {
		if (apiBlog == null)  {
			apiBlog = new APIBlogsHandler(serverURL, loginPref, pwd);
		}
		return apiBlog;
	}

}
