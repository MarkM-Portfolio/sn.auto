package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_VisitorModel_IMFollwingView extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_IMFollwingView.class);
	
	private HomepageUI ui;
	private CommunitiesUI comUI;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APICommunitiesHandler apiOwner;
	private APICommunitiesHandler communitiesAPIUser1,apiFollower;
	private String serverURL_MT_orgA;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		apiFollower = new APICommunitiesHandler(serverURL_MT_orgA,testExternalUser.getAttribute(cfg.getLoginPreference()),testExternalUser.getPassword());		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		comUI= CommunitiesUI.getGui(cfg.getProductName(),driver);
		
	}

	private void openAndVerifyEE(String newsStory, BaseCommunity basecomm) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		log.info("INFO: Open the EE");
		logger.strongStep(" Open the EE");
		ui.filterNewsItemOpenEE(newsStory);

		logger.strongStep("Verify that the EE (Embedded Experience) dialog box pops up for selected entry");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
		HomepageValid.verifyItemsInAS(ui, driver,  new String[]{basecomm.getName()}, null, true);

		ui.switchToTopFrame();
	}

	private void verifyEntriesCreatedInIMFollowingAndVerifyEE(String entry, User internalUser, BaseCommunity bascom) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String newsStory, iconCssSlectornewsStory, messageCSSSelctornewsStory;

		// Create the news story text to be verified
		newsStory = ui.replaceNewsStory(entry, bascom.getName(), null, internalUser.getDisplayName());
		iconCssSlectornewsStory = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		messageCSSSelctornewsStory = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		logger.strongStep(" Verify that 'shared externally' text with yellow outward arrow should be displayed for the entry");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCssSlectornewsStory, messageCSSSelctornewsStory}, null, true);
		logger.strongStep(" Verify that " + newsStory + " should be displayed");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, true);

		// Verify entry links opens in EE
		openAndVerifyEE(newsStory, bascom);

		// Verify entry doesn't show any comment link
		verifyNoCommentLinkForEntries(newsStory);
	}

	private void verifyNoCommentLinkForEntries(String newsStory) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String commentLink = HomepageUIConstants.IMFollowingEntry_Comment_Link.replaceAll("PLACEHOLDER", newsStory);

		logger.strongStep("Verify that entry does not show up 'Comment' link");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{commentLink}, null, false);
	}

	private void verifyActivitySaveAndStopFollowingLinks(String entry, User internalUser, BaseCommunity bascom) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Create the activity news story text to be verified
		String newsStory = ui.replaceNewsStory(entry, bascom.getName(), null, internalUser.getDisplayName());

		if (entry.contains("entry")) {
			newsStory = ui.replaceNewsStory(entry, bascom.getName(), bascom.getName(), internalUser.getDisplayName());
		}

		String saveLink = HomepageUIConstants.CommunityInvite_SaveThis_Link.replaceAll("PLACEHOLDER", newsStory);
		String stopFollwingLink = HomepageUIConstants.StopFollowing_Link.replaceAll("PLACEHOLDER", newsStory);

		logger.strongStep("Verify that entry does show up 'Save' link");
		log.info("INFO: Verify that entry does show up 'Save' link");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{saveLink}, null, true);
		
		logger.strongStep("Verify that entry does show up 'StopFollowing' link");
		log.info("INFO: Verify that entry does show up 'StopFollowing' link");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{stopFollwingLink}, null, true);

		log.info("INFO: Verify that the save link contains the text: "+ Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT);
		logger.strongStep("Verify that the save link contains the text: "+ Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT);
		Element saveLinkElement = driver.getSingleElement(saveLink);
		Assert.assertTrue(saveLinkElement.getText().trim().contains(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT),
				"ERROR: The save link did NOT contain the expected text: "+ Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT);

		log.info("INFO: Verify that the stop following link contains the text: "+ Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_STOPFOLLOWING_LINK_TEXT);
		logger.strongStep(" Verify that the stop following link contains the text: "+ Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_STOPFOLLOWING_LINK_TEXT);
		Element stopFollowingElement = driver.getSingleElement(stopFollwingLink);
		Assert.assertTrue(stopFollowingElement.getText().trim().contains(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_STOPFOLLOWING_LINK_TEXT),
				"ERROR: The stop following link did NOT contain the expected text: "+ Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_STOPFOLLOWING_LINK_TEXT);
	}

	private void verifyCommActivityEntryNotificationInIMFollowingAndVerifyEE(String entry,User internalUser, BaseCommunity basecom)
	{   
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String newsStory ,iconCssSlectornewsStory,messageCSSSelctornewsStory;
		
		// Create the news story text to be verified
		 newsStory = ui.replaceNewsStory(entry, basecom.getName(), basecom.getName(),internalUser.getDisplayName());
		 iconCssSlectornewsStory = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		 messageCSSSelctornewsStory = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER",newsStory);

		logger.strongStep(" Verify that 'shared externally' text with yellow outward arrow should be displayed for the entry");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCssSlectornewsStory, messageCSSSelctornewsStory}, null, true);
		logger.strongStep(" Verify that "+newsStory+" should be displayed");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, null, true);
		
		openAndVerifyEE(newsStory,basecom);
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community blog events/entries for the community he is member of from Home page->I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user added as a member</B></li>
	*<li><B>Step: [API] Internal orgA user creates an entry in the community blog</B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Verify: Verify that the entries/events for the community blog created in 1 and 2 step should appear.</B></li>
	*</ul>
	*/
    @Test(groups = { "mtlevel2" })
	public void visitorModel_IMFollowing_CommBlogEvents() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		Community restrictedCommunity = null;
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		
		if(apiOwner.setup.getServiceConfig().isCnxVersion65())
		{
			restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.BLOG, testExternalUser, true, testInternalUser, apiOwner);
		}
		else
		{
			restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser, apiOwner, testExternalUser);
		}
		
		// Internal orgA user will now create an entry in the community blog
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a community blog");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create will now create a community blog");
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genDateBasedRandVal());
		CommunityBlogEvents.createBlogPost(testInternalUser, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		
		// External user follow the community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);
	
		//testExternalUser goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		logger.strongStep("Load Hompepage and Log In as " +testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);
		
		log.info("INFO: Navigate to I'm Following");
		logger.strongStep("Navigate to I'm Following");
		ui.gotoImFollowing();
		ui.filterBy("Blogs");
		
		// Verify that community blog entry with shared externally text, yellow outward arrow should be displayed
		verifyEntriesCreatedInIMFollowingAndVerifyEE(Data.CREATE_COMM_BLOG,testInternalUser,baseCommunity);
		
		log.info("INFO: Delete the community clean up");
		logger.weakStep("Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community forum events/entries for the community he is member of from Home page->I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user added as a member</B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Verify: Verify that the entries/events for the community forum should appear.</B></li>
	*</ul>
	*/
	
	@Test(groups = { "mtlevel3" })
	public void visitorModel_IMFollowing_CommForumEvents() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser,apiOwner,testExternalUser);

		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);
	
		//testExternalUser goes to I'm Following view
		logger.strongStep("Load Hompepage and Log In as " +testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();
		ui.filterBy("Forums");
		
		// Verify that community forum entry with shared externally text, yellow outward arrow should be displayed
		verifyEntriesCreatedInIMFollowingAndVerifyEE(Data.CREATE_FORUM,testInternalUser,baseCommunity);
		
		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community events/entries for the community he is member of from Homepage->I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user added as a member</B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Verify: Verify that the entries/events for the community created in step 1 should appear.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_IMFollowing_CommunityEvents() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser,apiOwner,testExternalUser);

		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);
	
		// External user goes to I'm Following view
		logger.strongStep("Load Hompepage and Log In as " +testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();
		
		// verify that external user is able to see the entry for community event
		verifyEntriesCreatedInIMFollowingAndVerifyEE(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME,testInternalUser,baseCommunity);
		
		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community activity events for the community he is member of from Home page->I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member and Activity widget added </B></li>
	*<li><B>Step: [API] Internal orgA user creates community activity</B></li>
	*<li><B>Step: [API] Internal orgA user creates a public entry to community activity</B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Verify: Verify that community activity created entries appears in I'M Following view.</B></li>
	*<li><B>Verify: Verify that 'Save this' and 'Stop Following' links for community activity events appears in I'M Following view.</B></li>
	*<li><B>Verify: Verify that community activity entry created entries appears in I'M Following view.</B></li>
	*<li><B>Verify: Verify that 'Save this' and 'Stop Following' links for community activity entry events appears in I'M Following view.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void visitorModel_IMFollowing_CommActivityEvents() {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		APIActivitiesHandler activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.ACTIVITIES, testExternalUser, true, testInternalUser, communitiesAPIUser1);
		
		// Internal orgA user will now create community activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create community activity");
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(baseCommunity.getName(), baseCommunity);
		Activity communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testInternalUser, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);
		
		// Internal orgA user will now create a public entry to community activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a public entry to community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a public entry to community activity");
		BaseActivityEntry basePublicEntry = ActivityBaseBuilder.buildBaseActivityEntry(baseCommunity.getName(), communityActivity, false);
		CommunityActivityEvents.createActivityEntry(testInternalUser, activitiesAPIUser1, basePublicEntry, communityActivity);
		
		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);
	
		// Set communityUUID
		baseCommunity.setCommunityUUID(baseCommunity.getCommunityUUID_API(apiOwner, restrictedCommunity));
	
		//testExternalUser goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		logger.strongStep("Load Hompepage and Log In as " +testExternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testExternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();
		
		// Verify that community activity created notification in I'M Following view
		verifyEntriesCreatedInIMFollowingAndVerifyEE(Data.CREATE_ACTIVITY,testInternalUser,baseCommunity);
		// Verify that 'Save this' and 'Stop Following' links for community activity notification in I'M Following view
		verifyActivitySaveAndStopFollowingLinks(Data.CREATE_ACTIVITY,testInternalUser,baseCommunity);
		
		// Verify that community activity entry created notification in I'M Following view
		verifyCommActivityEntryNotificationInIMFollowingAndVerifyEE(Data.CREATE_ACTIVITY_ENTRY,testInternalUser,baseCommunity);
		// Verify that 'Save this' and 'Stop Following' links for community activity entry notification in I'M Following view
		verifyActivitySaveAndStopFollowingLinks(Data.CREATE_ACTIVITY_ENTRY,testInternalUser,baseCommunity);
		
		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see EE for the entries in I'M Following view also community link opens in tab after EE title gets clicked</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member </B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Step: Open the EE for new community created</B></li>
	*<li><B>Verify: Verify that community opens in new tab after the EE header link(Community title) gets clicked clicked.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_EECommunity() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.ACTIVITIES, testExternalUser, true, testInternalUser, apiOwner);
		
		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);
	
		//External User goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		logger.strongStep("Load Hompepage and Log In as " +testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);
		
		log.info("INFO: Navigate to I'm Following");
		logger.strongStep("Navigate to I'm Following");
		ui.gotoImFollowing();
		
		log.info("INFO: Open the EE for new community user added to");
		logger.strongStep("Open the EE for new community "+testExternalUser.getDisplayName()+" added to");
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, baseCommunity.getName(), null,testInternalUser.getDisplayName());
		ui.filterNewsItemOpenEE(newsStory);
			
		// Verify that user navigated to new tab when clicking EE heading link i.e community
		String pageTitle;
		if (comUI.isHighlightDefaultCommunityLandingPage()) {
			pageTitle = "Highlights - " + driver.getFirstElement(HomepageUIConstants.EEHeading).getText();
		} else {
			pageTitle = "Overview - " + driver.getFirstElement(HomepageUIConstants.EEHeading).getText();
		}
		
		ui.verifyLinkOpenInNewBrowser(HomepageUIConstants.EEHeading, pageTitle, CommunitiesUIConstants.communityHighlights);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community activity events for the community he is member of from Home page->I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member and Activity widget added </B></li>
	*<li><B>Step: [API] Internal orgA user creates community activity</B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Step: Open the EE for new community activity created</B></li>
	*<li><B>Verify:  Verify that community activity opens in new tab after the EE header link(Community activity title) gets clicked clicked.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void visitorModel_EECommunityActivity() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		APIActivitiesHandler activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.ACTIVITIES, testExternalUser, true, testInternalUser, communitiesAPIUser1);
		
		// Internal orgA user will now create community activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create community activity");
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(baseCommunity.getName(), baseCommunity);
		CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testInternalUser, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);
		
		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);
	
		// testExternalUser goes to I'm Following view 
		logger.strongStep("Load Hompepage and Log In as " +testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);
		
		log.info("INFO: Navigate to I'm Following");
		logger.strongStep("Navigate to I'm Following");
		ui.gotoImFollowing();
		
		// Create news story for activity
		String newsStory = ui.replaceNewsStory(Data.CREATE_ACTIVITY, baseCommunity.getName(), null,testInternalUser.getDisplayName());
		
		log.info("INFO: Open the EE for new community activity created");
		logger.strongStep("Open the EE for new community activity created");
		ui.filterNewsItemOpenEE(newsStory);
		
		log.info("INFO: Verify that the EE header link is clickable");
		logger.strongStep("Verify that the EE header link is clickable");
		String pageTitle= driver.getFirstElement(HomepageUIConstants.EEHeading).getText()+" Activity - "+baseCommunity.getName()+" Community";
		
		// Verify that user navigated to new tab when clicking EE heading link i.e community activity
		ui.verifyLinkOpenInNewBrowser(HomepageUIConstants.EEHeading, pageTitle, CommunitiesUIConstants.tabNavCommunityName);
		
		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}	

	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community activity events for the community he is member of from Home page->I'M Following view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member </B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Step: Hover over the link for user's name who created entries (internal user) from the entry</B></li>
	*<li><B>Verify: Verify that correct business card for that user should appear.</B></li>
	*<li><B>Verify: Verify that user's name displayed on business card is not a click able link.</B></li>
	*<li><B>Verify: Verify that there are no links/ elements available in business card header section.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_InternalUserBizCard() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity=CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testExternalUser, communitiesAPIUser1, testExternalUser);

		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");
		logger.strongStep(testExternalUser.getDisplayName() + " follow the Community using API");
		apiFollower.followCommunity(restrictedCommunity);

		// testExternalUser goes to I'm Following view 
		logger.strongStep("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);

		log.info("INFO: Navigate to I'm Following");
		logger.strongStep("Navigate to I'm Following");
		ui.gotoImFollowing();

		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME,baseCommunity.getName(), null, testInternalUser.getDisplayName());
		String interUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER",newsStory);

		// Verify that business card id displayed on hovering user name's link from entry
		Element internalUserNameLink = driver.getFirstElement(interUserNameCSSSelctornewsStory);
		ui.verifyBizCard(internalUserNameLink);

		// Verify there are no links/ elements available in business card header section
		log.info("INFO: Verify there are no links/ elements available in business card header section");
		logger.strongStep("Verify there are no links/ elements available in business card header section");
		
		List<Element> cardHeader = driver.getElements("css=#cardTable tr#cardHeader td>table td");
		log.info("Number of elements in biz card header: " + cardHeader.size());
		Assert.assertTrue(cardHeader.isEmpty());

		log.info("INFO: Delete the community clean up");
		logger.strongStep("Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that no input box or comment link should visible to external user from Home page->I'M Following view</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->I'M Following view</B></li>
	*<li><B>Verify: Verify that no input box or comment link should visible to external user.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitorModel_NoInputBoxToUpdateStatus() throws Exception {

		ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// testExternalUser goes to I'm Following view
		logger.strongStep("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		log.info("Load Hompepage and Log In as " + testInternalUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testExternalUser);

		log.info("INFO: Navigate to I'm Following");
		logger.strongStep("Navigate to I'm Following");
		ui.gotoImFollowing();

		logger.strongStep("Verify that input box for status update should not be displayed");
		Assert.assertFalse(driver.isElementPresent(BaseUIConstants.StatusUpdate_Body));
		
		ui.endTest();
	}
}

