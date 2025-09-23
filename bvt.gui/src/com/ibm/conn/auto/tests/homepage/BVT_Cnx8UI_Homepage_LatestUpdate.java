package com.ibm.conn.auto.tests.homepage;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Cnx8UI_Homepage_LatestUpdate extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_LatestUpdate.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser,testUser1;
	private HomepageUI ui;
	private HomepageUICnx8 homepageCnx8ui;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser1=cfg.getUserAllocator().getUser();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		homepageCnx8ui = new HomepageUICnx8(driver);
		cnxAssert = new Assert(log);
	}	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Like/Unlike on Latest Updates post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step</B> Click on Like link of created Post</li>
	 *<li><B>Verify:</B>Verify Unlike text is visible</li>
	 *<li><B>Step:</B>Click on Unlike of created post</li>
	 *<li><B>Verify:</B>Verify like text is visible</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T582</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T583</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T584</li>
	 *</ul>
	 */

	@Test(groups = {"cnx8-level2"},enabled= true)
	public void verifyLatestUpdatesLikeUnlike() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		String message = testName + " message " + Helper.genDateBasedRand();
		String likePost=HomepageUIConstants.likePostOnLatestUpdates.replace("PLACEHOLDER1", message).replace("PLACEHOLDER2","Like");
		String unLikePost=HomepageUIConstants.likePostOnLatestUpdates.replace("PLACEHOLDER1", message).replace("PLACEHOLDER2","Unlike");

		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to latest updates");
		log.info("INFO : Navigate to latest updates");
		ui.gotoUpdates();

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
	 *<li><B>Info:</B> Verify Repost on Latest Updates post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step:</B> Click on Repost of created Post</li>
	 *<li><B>Verify:</B>Validate that message is successfully Reposted</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T582</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T583</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T587</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyLatestUpdatesRepost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		String message = testName + " message " + Helper.genDateBasedRand();
		String rePost=HomepageUIConstants.rePostOnLatestUpdates.replace("PLACEHOLDER1", message);
		
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to latest updates");
		log.info("INFO : Navigate to latest updates");
		ui.gotoUpdates();

		logger.strongStep("Post status update with @mention about "+ testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about : " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Click on Repost of created Post");
		log.info("INFO: Click on Repost of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(rePost),3,"Click on Repost of created Post");

		logger.strongStep("Validate that  Repost message was successfully Reposted");
		log.info("INFO: Validate that  Repost message was successfully Reposted");
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.repostConfirmation), 3);
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
	 *<li><B>Info:</B> Verify Save on Latest Updates post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step:</B>Click on More option of created Post</li>
	 *<li><B>Step:</B>Click on Save post of created Post</li>
	 *<li><B>Verify:</B>Verify that post is Saved successfully</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T582</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T583</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T588</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyLatestUpdatesSavePost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		String message = testName + " message " + Helper.genDateBasedRand();
		String actionMoreLink= HomepageUIConstants.actionMoreLinkLatestUpdates.replace("PLACEHOLDER1", message);
		String savePost= HomepageUIConstants.savePostLatestUpdates.replace("PLACEHOLDER1", message);
		String savePostSuccessful=HomepageUIConstants.savePostSuccessfulLatestUpdates.replace("PLACEHOLDER1", message);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to latest updates");
		log.info("Navigate to latest updates");
		ui.gotoUpdates();

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
	 *<li><B>Info:</B> Verify Comment on Latest Updates post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Link</li>
	 *<li><B>Step:</B> Post a message</li>
	 *<li><B>Step:</B>Click on comment Link of created Post</li>
	 *<li><B>Step:</B>Type Comment Message in Comment text Area</li>
	 *<li><B>Step:</B>Click on Post to post Comment Message</li>
	 *<li><B>Verify:</B>Verify comment Message is displayed as expected</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T582</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T583</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T585</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2","cnx8ui-level2"},enabled= true)
	public void verifyLatestUpdatesCommentOnPost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		String message = testName + " message " + Helper.genDateBasedRand();
		String commentOnDiscoverPost= HomepageUIConstants.commentOnLatestUpdatesPost.replace("PLACEHOLDER1", message);
		String commentTextElement=HomepageUIConstants.latestUpdatesCommentText.replace("PLACEHOLDER1", message);
		String commentMessage="This is test Comment to post"; 

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to latest updates");
		log.info("Navigate to latest updates");
		ui.gotoUpdates();

		logger.strongStep("Post status update with @mention about " + testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about User B " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Click on comment Link of created Post");
		log.info("INFO: Click on comment Link of created Post");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(commentOnDiscoverPost),3,"Click on comment Link of created Post");

		logger.strongStep("Type Comment Message in Comment text Area");
		log.info("INFO: Type Comment Message in Comment text Area");
		homepageCnx8ui.commentOnLatestUpdatesPost(commentMessage,message);

		logger.strongStep("Click on Post to post Comment Message");
		log.info("INFO: Click on Post to post Comment Message");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesCommentPost.replace("PLACEHOLDER1", message)), 3, "click on post to Psot Comment Message");

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
	 *<li><B>Info:</B> Verify Delete on Latest Updates post in Homepage</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Link</li>
	 *<li><B>Step:</B> Type MentionMessage</li>
	 *<li><B>Step:</B> Click on Post to post message</li>
	 *<li><B>Step:</B> Wait for created Post is visible</li>
	 *<li><B>Step:</B>MouseOver on created Post</li>
	 *<li><B>Step:</B>Click on Delete Icon of created Post</li>
	 *<li><B>Step:</B>Click on Delete Button of Modal</li>
	 *<li><B>Verify:</B>Validate that post was deleted Successfully</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T582</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T583</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T586</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyLatestUpdatesDeletePost() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
	
		String message = testName + " message " + Helper.genDateBasedRand();
		String deletePost= HomepageUIConstants.deleteLatestUpdatesPost.replace("PLACEHOLDER1", message);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Navigate to latest updates");
		log.info("Navigate to latest updates");
		ui.gotoUpdates();

		logger.strongStep("Post status update with @mention about "+ testUser.getEmail());
		log.info("INFO: Creating and posting the @mention about User B: " + testUser.getEmail());
		ui.postAtMentionUserUpdate(testUser, message);
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);

		logger.strongStep("Wait for created Post is visible");
		log.info("INFO: Wait for created Post is visible");
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.rePostOnLatestUpdates.replace("PLACEHOLDER1", message)),3);

		logger.strongStep("Mouse over created Post");
		log.info("INFO: Mouse Over Created Post");
		homepageCnx8ui.mouseHoverWd(homepageCnx8ui.findElement(By.xpath(HomepageUIConstants.rePostOnLatestUpdates.replace("PLACEHOLDER1", message))));

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
	 *<li><B>Info:</B> Verify I'm Following Blogs on Latest Updates post in Homepage</li>
	 *<li><B>Step:</B> Create Public Community with user1</li>
	 *<li><B>Step:</B> Add user2 as Member in Community</li>
	 *<li><B>Step:</B> Add Blogs Widget to created public community using API</li>
	 *<li><B>Step:</B> Create Blog in Public Community using API</li>
	 *<li><B>Step:</B> Follow Community for user2</li>
	 *<li><B>Step:</B> Login to Homepage with user2</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Go to Latest Updates Page</li>
	 *<li><B>Step:</B> Select I'm Following Personal Filter</li>
	 *<li><B>Step:</B> Select Blogs from Filter Menu</li>
	 *<li><B>Verify:</B> Verify created Blog Post is visible</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T582</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T589</li>
	 *<li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T590</li>
	 *</ul>
	 */	
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyLatestUpdatesImFollowingBlogs() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		APICommunitiesHandler apiMember = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		BaseCommunity community = new BaseCommunity.Builder("community" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName)
				.addMember(new Member(CommunityRole.MEMBERS, testUser1))
				.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("Entry " + testName + Helper.genDateBasedRandVal())
				.tags("IdeaTag" + Helper.genDateBasedRand())
				.content("Test Content for " + testName)
				.build();

		logger.strongStep("Create community using API with " + testUser.getDisplayName());
		log.info("INFO: Create community using API with "  + testUser.getDisplayName());
		Community comAPI = community.createAPI(apiOwner);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Create Blog Entry in community");
		log.info("INFO:Create Blog Entry  in community");
		apiOwner.createBlogEntry(blogEntry, comAPI);

		logger.strongStep("Follow Community with " + testUser1.getDisplayName());
		log.info("INFO:Create Blog Entry with "+ testUser1.getDisplayName());
		apiMember.followCommunity(comAPI);

		//Load component and login
		logger.strongStep("Load homepage, login as "+ testUser1.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser1.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		logger.strongStep("Navigate to discover is new UI"+cfg.getUseNewUI());
		log.info("Navigate to discover is new UI"+cfg.getUseNewUI());
		ui.gotoUpdates();

		log.info("INFO: Click on Personal Filter");
		logger.strongStep("Click on Personal Filter");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),5,"Click on Personal Filter");

		log.info("INFO: Click on I'm Following Filter Menu");
		logger.strongStep("Click on I'm Following Filter Menu");
		homepageCnx8ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesImFollowingFilter), 5, "Click on I'm Following Filter Menu");

		logger.strongStep("Click on Filter Button");
		log.info("Click on Filter Button");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),5);
		homepageCnx8ui.clickLinkWd(By.cssSelector(HomepageUIConstants.discoverLatestUpdateFilterButton),"Click on Filter Button");	
		
		logger.strongStep("Click on Blogs Filter Menu");
		log.info("Click on Blogs Filter Menu");
		homepageCnx8ui.waitForElementsVisibleWd((By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Blogs"))),5);
		homepageCnx8ui.clickLinkWd(By.xpath(HomepageUIConstants.discoverLatestUpdateFilterValue.replace("PLACEHOLDER", "Blogs")),"Click on Blog Filter");	

		logger.strongStep("Verify created Post is visible");
		log.info("INFO: Verify created Post is visible");
		cnxAssert.assertTrue(homepageCnx8ui.isElementVisibleWd(By.xpath(HomepageUIConstants.linkInpostContents.replace("PLACEHOLDER1", blogEntry.getTitle())),5), "Verify created Post is visible");

		logger.strongStep("Logout of Application");
		log.info("INFO: Logging out of Application");
		ui.logout();

		logger.weakStep("Close browser");
		log.info("INFO: Closing browser");
		ui.close(cfg);
		ui.endTest();	
	}
}
