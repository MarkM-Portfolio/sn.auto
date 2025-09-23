package com.ibm.conn.auto.tests.homepage;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Cnx8UI_Homepage_Discover extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_Discover.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	private HomepageUI ui;
	private HomepageUICnx8 homepageCnx8ui;
	private String serverURL;
	private BaseCommunity.Access defaultAccess;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		homepageCnx8ui = new HomepageUICnx8(driver);
		cnxAssert = new Assert(log);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Like/Unlike on Discover post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step</B> Click on Like link of created Post</li>
	 *<li><B>Verify:</B>Verify Unlike text is visible</li>
	 *<li><B>Step:</B>Click on Unlike of created post</li>
	 *<li><B>Verify:</B>Verify like text is visible</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T551</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T552</li>
	 *</ul>
	 */

	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2"},enabled= true)
	public void verifyDiscoverLikeUnlike() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String message = testName + " message " + Helper.genDateBasedRand();
		String likePost=HomepageUIConstants.likePostOnDiscover.replace("PLACEHOLDER1", message).replace("PLACEHOLDER2","Like");
		String unLikePost=HomepageUIConstants.likePostOnDiscover.replace("PLACEHOLDER1", message).replace("PLACEHOLDER2","Unlike");

		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover is new UI"+cfg.getUseNewUI());
		log.info("Navigate to discover is new UI"+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Post status update with @mention about "+ testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Click on Like link of created post");
		log.info("INFO: Click on Like link of created post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(likePost),3,"Click on Like of created post");

		logger.strongStep("Verify Unlike text is visible");
		log.info("INFO: Verify Unlike text is visible");
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(unLikePost), 3);
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(unLikePost)),"Unlike","Unlike is not dispalying");		

		logger.strongStep("Click on Unlike of created post");
		log.info("Click on Unlike of created post");
		homepageCnx8ui.clickLinkWd(homepageCnx8ui.findElement(By.xpath(unLikePost)));

		logger.strongStep("Verify like text is visible");
		log.info("INFO: Verify like text is visible");
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(likePost), 3);
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(likePost)),"Like","Like is not dispalying");

		logger.strongStep("Logout of application");
		log.info("INFO: Logging out of application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();		
	}


	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Repost on Discover post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step:</B> Click on Repost of created Post</li>
	 *<li><B>Verify:</B>Validate that message is successfully Reposted</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T551</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T555</li>
	 *</ul>
	 */	

	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyDiscoverRepost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		String message = testName + " message " + Helper.genDateBasedRand();
		String rePost=HomepageUIConstants.rePostOnDiscover.replace("PLACEHOLDER1", message);

		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		log.info("Navigate to discover is new UI"+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Post status update with @mention about "+ testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about : " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Click on Repost of created Post");
		log.info("INFO: Click on Repost of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(rePost),3,"Click on Repost of created Post");

		logger.strongStep("Validate that  Repost message was successfully Reposted");
		log.info("INFO: Validate that  Repost message was successfully Reposted");
		cnxAssert.assertTrue(homepageCnx8ui.isTextPresentWd("The update was successfully reposted to your followers."), "ERROR: The message was not successfully rePosted");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Save on Discover post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step:</B>Click on More option of created Post</li>
	 *<li><B>Step:</B>Click on Save post of created Post</li>
	 *<li><B>Verify:</B>Verify that post is Saved successfully</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T551</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T556</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyDiscoverSavePost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		String message = testName + " message " + Helper.genDateBasedRand();
		String actionMoreLink= HomepageUIConstants.actionMoreLink.replace("PLACEHOLDER1", message);
		String savePost= HomepageUIConstants.savePost.replace("PLACEHOLDER1", message);
		String savePostSuccessful=HomepageUIConstants.savePostSuccessful.replace("PLACEHOLDER1", message);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover is new UI"+cfg.getUseNewUI());
		log.info("Navigate to discover is new UI"+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Post status update with @mention about "+ testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about User A: " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Click on More option of created Post");
		log.info("INFO: Click on More option of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(actionMoreLink),3,"Click on More option of created post");

		logger.strongStep("Click on Save post of created Post");
		log.info("INFO: Click on Save post of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(savePost),3,"Click on Save post of created Post");

		logger.strongStep("Verify that post is Saved successfully");
		log.info("INFO: Verify that post is Saved successfully");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(savePostSuccessful)),"Saved","Save is not successful");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();		
	}


	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Comment on Discover post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step:</B>Click on comment Link of created Post</li>
	 *<li><B>Step:</B>Type Comment Message in Comment text Area</li>
	 *<li><B>Step:</B>Click on Post to post Comment Message</li>
	 *<li><B>Verify:</B>Verify comment Message is displayed as expected</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T551</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T553</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2"},enabled= true)
	public void verifyDiscoverCommentOnPost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		String message = testName + " message " + Helper.genDateBasedRand();
		String commentOnDiscoverPost= HomepageUIConstants.commentOnDiscoverPost.replace("PLACEHOLDER1", message);
		String commentTextElement=HomepageUIConstants.discoverCommentText.replace("PLACEHOLDER1", message);
		String commentMessage="This is test Comment to post"; 

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover is new UI"+cfg.getUseNewUI());
		log.info("Navigate to discover is new UI"+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Post status update with @mention about " + testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about User B " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Click on comment Link of created Post");
		log.info("INFO: Click on comment Link of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(commentOnDiscoverPost),3,"Click on comment Link of created Post");

		logger.strongStep("Type Comment Message in Comment text Area");
		log.info("INFO: Type Comment Message in Comment text Area");
		homepageCnx8ui.commentOnDiscoverPost(commentMessage,message);

		logger.strongStep("Click on Post to post Comment Message");
		log.info("INFO: Click on Post to post Comment Message");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.discoverCommentPost.replace("PLACEHOLDER1", message)), 3, "click on post to Psot Comment Message");

		logger.strongStep("Verify comment Message is displayed as expected");
		log.info("INFO: Verify comment Message is displayed as expected Message: "+commentMessage);
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(commentTextElement)),commentMessage,"Comment Message"+commentMessage+ "is Not displaying");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Delete on Discover post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Link</li>
	 *<li><B>Step:</B> Type MentionMessage</li>
	 *<li><B>Step:</B> Click on Post to post message</li>
	 *<li><B>Step:</B> Wait for created Post is visible</li>
	 *<li><B>Step:</B>MouseOver on created Post</li>
	 *<li><B>Step:</B>Click on Delete Icon of created Post</li>
	 *<li><B>Step:</B>Click on Delete Button of Modal</li>
	 *<li><B>Verify:</B>Validate that post was deleted Successfully</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T551</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T554</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyDiscoverDeletePost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		String message = testName + " message " + Helper.genDateBasedRand();
		String deletePost= HomepageUIConstants.deleteDiscoverPost.replace("PLACEHOLDER1", message);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover is new UI"+cfg.getUseNewUI());
		log.info("Navigate to discover is new UI"+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Post status update with @mention about "+ testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about User B: " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Wait for created Post is visible");
		log.info("INFO: Wait for created Post is visible");
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.rePostOnDiscover.replace("PLACEHOLDER1", message)),3);

		logger.strongStep("Mouse over created Post");
		log.info("INFO: Mouse Over Created Post");
		homepageCnx8ui.mouseHoverWd(homepageCnx8ui.findElement(By.xpath(HomepageUIConstants.rePostOnDiscover.replace("PLACEHOLDER1", message))));

		logger.strongStep("Click on Delete Icon of created Post");	
		log.info("INFO: Click on Delete Icon of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(deletePost),5,"Click on Delete Icon of created Post");

		logger.strongStep("Click on Delete Button of Modal");
		log.info("INFO: Click on Delete Button of Modal");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.deleteModal),3,"Click on Delete Button of Modal");

		logger.strongStep("Validate that post was deleted successfully");
		log.info("INFO: Validate that post was deleted successfully");
		cnxAssert.assertTrue(homepageCnx8ui.fluentWaitTextPresent("Message has been successfully deleted."), "ERROR: The post was not successfully deleted");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();		
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Forums Filter On Discover Homepage</li>
	 *<li><B>Step:</B> Create Forum using API</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Page</li>
	 *<li><B>Step:</B> Select Forums from Filter Menu</li>
	 *<li><B>Verify:</B> Verify Selected filter Forums is displayed</li>
	 *<li><B>Verify:</B> Verify created Forum Post is visible</li>
	 *<li><B>JIRA Link:</B> https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T557</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T558</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2", "mt-exclude"},enabled= true)
	public void verifyDiscoverForumsFilter() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRandVal();

		APIForumsHandler apiOwner = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

		BaseForum forum = new BaseForum.Builder(testName + rand)
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		logger.strongStep("Create a forum (API)");
		log.info("INFO: Create a forum (API)");
		forum.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover page "+cfg.getUseNewUI());
		log.info("INFO: Navigate to discover page "+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Click on Filter Button");
		log.info("Click on Filter Button");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),5);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),3,"Click on Filter Button");	

		logger.strongStep("Click on Forums Filter Menu");
		log.info("Click on Forums Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Forums")),3,"Click on Forums Filter Menu");

		logger.strongStep("Verify Selected filter Forums is displayed");
		log.info("INFO: Verify Selected filter Forums is displayed");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.discoverLatestUpdateSelectedFilter)),"Forums","Forums is Displayed on Chip");

		logger.strongStep("Verify created Forum Post is visible");
		log.info("INFO: Verify created Forum Post is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.linkInpostContents.replace("PLACEHOLDER1", forum.getName())),5),"Verify created Forum Post is visible");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	

	}
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Blogs Filter On Discover Homepage</li>
	 *<li><B>Step:</B> Create Blog using API</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Page</li>
	 *<li><B>Step:</B> Select Blogs from Filter Menu</li>
	 *<li><B>Verify:</B> Verify Selected filter Blogs is displayed</li>
	 *<li><B>Verify:</B> Verify created Blog Post is visible</li>
	 *<li><B>JIRA Link:</B> https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T557</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T558</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2", "mt-exclude"},enabled= true)
	public void verifyDiscoverBlogsFilter() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRandVal();

		APIBlogsHandler apiOwner = new APIBlogsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).build();

		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser.getDisplayName());
		blog.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI  as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI  as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover page "+cfg.getUseNewUI());
		log.info("INFO: Navigate to discover page "+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Click on Filter Button");
		log.info("Click on Filter Button");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),5);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),3,"Click on Filter Button");	

		logger.strongStep("Click on Blogs Filter Menu");
		log.info("Click on Blogs Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Blogs")),3,"Click on Blogs Filter Menu");

		logger.strongStep("Verify Selected filter Blogs is displayed");
		log.info("INFO: Verify Selected filter Blogs is displayed");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.discoverLatestUpdateSelectedFilter)),"Blogs","Blogs is Displayed on Chip");

		logger.strongStep("Verify created Post is visible");
		log.info("INFO: Verify created Post is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.linkInpostContents.replace("PLACEHOLDER1", blog.getName())),5), "Verify created Post is visible");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Wikis Filter On Discover Homepage</li>
	 *<li><B>Step:</B> Create Wiki using API</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Page</li>
	 *<li><B>Step:</B> Select Wikis from Filter Menu</li>
	 *<li><B>Verify:</B> Verify Selected filter Wikis is displayed</li>
	 *<li><B>Verify:</B> Verify created Wiki Post is visible</li>
	 *<li><B>JIRA Link:</B> https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T557</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T558</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2", "mt-exclude"},enabled= true)
	public void verifyDiscoverWikisFilter() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand())
				.description("Description for test " + testName)
				.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover page "+cfg.getUseNewUI());
		log.info("INFO: Navigate to discover page "+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Click on Filter Button");
		log.info("Click on Filter Button");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),5);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),3,"Click on Filter Button");	

		logger.strongStep("Click on Wikis Filter Menu");
		log.info("Click on Wikis Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Wikis")),3,"Click on Wikis Filter Menu");

		logger.strongStep("Verify Selected filter Wikis is displayed");
		log.info("INFO: Verify Selected filter Wikis is displayed");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.discoverLatestUpdateSelectedFilter)),"Wikis","Wikis is Displayed on Chip");

		logger.strongStep("Verify created Post is visible");
		log.info("INFO: Verify created Post is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.linkInpostContents.replace("PLACEHOLDER1", wiki.getName())),3),"Verify created Post is visible");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Activities Filter On Discover Homepage</li>
	 *<li><B>Step:</B> Create Public Community using API</li>
	 *<li><B>Step:</B> Add Activities Widget to created public community using API</li>
	 *<li><B>Step:</B> Create Activity in Public Community using API</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Page</li>
	 *<li><B>Step:</B> Select Activities from Filter Menu</li>
	 *<li><B>Verify:</B> Verify Selected filter Activities is displayed</li>
	 *<li><B>Verify:</B> Verify created Activity Post is visible</li>
	 *<li><B>JIRA Link:</B> https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T557</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T558</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyDiscoverActivitiesFilter() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		APICommunitiesHandler apiComOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.goal("Goal for "+ testName)
				.community(community)
				.build();

		//Create community
		log.info("INFO: Create a Community using API");
		logger.strongStep("Create a Community using API");
		Community comAPI = community.createAPI(apiComOwner);

		//Add the events widget
		log.info("INFO: Add the 'Events' widget to the Community using API");
		logger.strongStep("Add the 'Events' widget to the Community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.ACTIVITIES);

		//Add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);

		//Create activity
		log.info("INFO: Create an Activity using API");	
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner, community);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover page "+cfg.getUseNewUI());
		log.info("INFO: Navigate to discover page "+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Click on Filter Button");
		log.info("Click on Filter Button");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),5);
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),3,"Click on Filter Button");	

		logger.strongStep("Click on Activities Filter Menu");
		log.info("Click on Activities Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Activities")),3,"Click on Activities Filter Menu");

		logger.strongStep("Verify Selected filter Activities is displayed");
		log.info("INFO: Verify Selected filter Activities is displayed");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.discoverLatestUpdateSelectedFilter)),"Activities","Activities is Displayed on Chip");

		logger.strongStep("Verify created Post is visible");
		log.info("INFO: Verify created Post is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.linkInpostContents.replace("PLACEHOLDER1", activity.getName())),3),"verify created Post is visible");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Communities Filter On Discover Homepage</li>
	 *<li><B>Step:</B> Create Community using API</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Discover Page</li>
	 *<li><B>Step:</B> Select Communities from Filter Menu</li>
	 *<li><B>Verify:</B> Verify Selected filter Communities is displayed</li>
	 *<li><B>Verify:</B> Verify created Community Post is visible</li>
	 *<li><B>JIRA Link:</B> https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T557</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T558</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyDiscoverCommunitiesFilter() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.access(defaultAccess)
				.description("Test Widgets inside community for " + testName)
				.build();

		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Toggle to new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover page "+cfg.getUseNewUI());
		log.info("INFO: Navigate to discover page "+cfg.getUseNewUI());
		ui.gotoDiscover();

		logger.strongStep("Click on Filter Button");
		log.info("Click on Filter Button");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),5);
		homepageCnx8ui.clickLinkWd(homepageCnx8ui.findElement(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton)));	

		logger.strongStep("Click on Communities Filter Menu");
		log.info("Click on Communities Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Communities")),3,"Click on Communities Filter Menu");

		logger.strongStep("Verify Selected filter Communities is displayed");
		log.info("INFO: Verify Selected filter Communities is displayed");
		cnxAssert.assertEquals(homepageCnx8ui.getElementTextWd(By.xpath(HomepageUIConstants.discoverLatestUpdateSelectedFilter)),"Communities","Communities is Displayed on Chip");

		logger.strongStep("Verify created Post is visible");
		log.info("INFO: Verify created Post is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.linkInpostContents.replace("PLACEHOLDER1", community.getName())),5), "verify created Post is visible");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	

	}
}
