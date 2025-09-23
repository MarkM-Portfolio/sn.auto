package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_VisitorModel_StatusUpdatesView extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_StatusUpdatesView.class);
	
	private HomepageUI ui;
	private CommunitiesUI comUI;
	private APIProfilesHandler profilesAPIUser1,profilesAPIUser2;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APICommunitiesHandler apiOwner;
	private APICommunitiesHandler communitiesAPIUser1;
	private String serverURL_MT_orgA;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();		
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		profilesAPIUser1 = new APIProfilesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}

	/**
	*<ul>
	*<li><B>Info: Verify that external user can see the mentions by internal org user but can NOT see plain text messages without mentions</B></li>
	*<li><B>Step: Internal user log in and navigates to home page->status updates view</B></li>
	*<li><B>Step: [API] Internal user posts status message 1 with mentioning external user</B></li>
	*<li><B>Step: [API] Internal user posts status message 2 without mentioning external user i.e. plain text message</B></li>
	*<li><B>Step: Log in as a external user</B></li>
	*<li><B>Step: Go to Home page ->Status Updates view</B></li>
	*<li><B>Verify: Verify that external user is able to see status message 1 where he is mentioned.</B></li>
	*<li><B>Verify: Verify that external user is NOT able to see status message 2 where he is not mentioned.</B></li>
	*<li><B>Step: Go to Mention view</B></li>
	**<li><B>Verify: Verify that external user is able to see status message 1 where he is mentioned.</B></li>
	*</ul>
	*/ 
	
	@Test(groups = {"mtlevel2"})
	public void visitor_StatusUpdates() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname=ui.startTest();
		
		// Internal user will log in and navigate to the home page->Status Updates view
		log.info("INFO: Internal user will log in and navigate to the home page->Status Updates view");
		logger.strongStep("Internal user will log in and navigate to the home page->Status Updates view");
		LoginEvents.loginAndGotoStatusUpdates(ui, testInternalUser, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mention = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA, testname, "afterText");
		
		// Internal user will now post a status update with mentions - mentioning a visitor
		log.info("INFO: Internal user will now post a status update with mentions - mentioning a external user");
		logger.strongStep("Internal user will now post a status update with mentions - mentioning a external user");
		ProfileEvents.addStatusUpdateWithMentionsUsingUIMT(ui, driver, testInternalUser,testExternalUser, mention);
		
		// Internal user will now post a status update with plain text message
		log.info("INFO:  Internal user will now post a status update with plain text message");
		logger.strongStep(" Internal user will now post a status update with plain text message");
		String statusUpdatePlainTextMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateUsingUI(ui, testInternalUser, statusUpdatePlainTextMessage, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as external user and navigate to the status updates view
		log.info("INFO: Log in as external user and navigate to the status updates view");
		logger.strongStep("Log in as external user and navigate to the status updates view");
		LoginEvents.loginAndGotoStatusUpdates(ui, testExternalUser, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testInternalUser.getDisplayName());
		String mentionsText = mention.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() +" "+mention.getAfterMentionText();
		
		// Verify that the mentions event is displayed in the Updates view
		log.info("INFO: Verify that the mentions event is displayed in the Updates view");
		logger.strongStep("Verify that the mentions event is displayed in the Updates view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
		
		// Create the elements to be verified
		String statusPlainTextMessagebyInternalUser = ProfileNewsStories.getPostedAMessageToYouNewsStory(ui, testInternalUser.getDisplayName());
		log.info("statusPlainTextMessagebyInternalUser is :"+statusPlainTextMessagebyInternalUser);
		
		// Verify that the plain text status message is NOT displayed to external users's status update view
		log.info("INFO: Verify that the plain text status message is NOT displayed to external users's status update view");
		logger.strongStep("Verify that the plain text status message is NOT displayed to external users's status update view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusPlainTextMessagebyInternalUser}, null, false);
		ui.gotoMentions();
		
		// Verify that the mentions event is displayed in the Updates view
		log.info("INFO: Verify that the mentions event is displayed in the Updates view");
		logger.strongStep(" Verify that the mentions event is displayed in the Updates view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
	
		ui.endTest();
}
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see status updates for community he is member of</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member </B></li>
	*<li><B>Step: [API] Internal user posts status message 1 with mentioning external user from community status updates widget</B></li>
	*<li><B>Step: [API] Internal user posts status message 2 without mentioning external user i.e. plain text message from community status updates widget</B></li>
	*<li><B>Step: Log in as a external user</B></li>
	*<li><B>Step: Go to Home page ->Status Updates view</B></li>
	*<li><B>Verify: Verify that status update with mention should displayed in the status update view.</B></li>
	*<li><B>Step: External user navigate to the created community's status updates view</B></li>
	*<li><B>Verify: Verify that status update with mention should be displayed in the status update view.</B></li>
	*<li><B>Verify: Verify that status update without mentioned text should be displayed in the status update view.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitor_CommunityStatusUpdates() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser, communitiesAPIUser1, testExternalUser);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA,testName, "afterText");

		// Internal user will now post a status update with mentioning external user to the community
		log.info("INFO: Internal user will now post a status update with mentioning external user to the community");
		logger.strongStep("Internal user will now post a status update with mentioning external user to the community");
		CommunityEvents.addCommStatusUpdateWithMentions(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, mentions);
		
		log.info("INFO: Internal user will now post a status update with plain text and without mentioning external user to the community");
		logger.strongStep("Internal user will now post a status update with plain text and without mentioning external user to the community");
		String statusUpdatePlainTextMessage = Data.getData().commonStatusUpdate + " "+testName; 
		CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, statusUpdatePlainTextMessage);

		// Log in as external user and navigate to the Status updates view
		log.info("INFO: Log in as external uesr and navigate to the Status updates view");
		logger.strongStep("Log in as external uesr and navigate to the Status updates view");
		LoginEvents.loginAndGotoStatusUpdates(ui, testExternalUser, false);
				
		// Create the elements to be verified
		String mentionedYouEvent = CommunityNewsStories.getMentionedYouInAMessageNewsStory(ui, baseCommunity.getName(), testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that status update with mention should displayed in the status update view
		log.info("INFO: Verify that status update with mention should displayed in the status update view");
		logger.strongStep("Verify that status update with mention should displayed in the status update view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
		
		// Verify that status update without mention i.e. plain text message  should NOT displayed in the Home page->status update view
		log.info("INFO: Verify that status update with plain text and without mention should not be displayed");
		logger.strongStep("Verify that status update with plain text and without mention should not be displayed");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdatePlainTextMessage}, null, false);
		
		// Set communityUUID
		baseCommunity.setCommunityUUID(baseCommunity.getCommunityUUID_API(apiOwner, restrictedCommunity));
	
		// Navigate to the community
		log.info("INFO: Navigate to the community ");
		logger.strongStep("Navigate to the community");
		ui.clickLink(CommunitiesUIConstants.Visitor_communitiesLink);
		baseCommunity.navViaUUID(comUI);
		
		// Navigate to community status updates widget
		log.info("INFO: Select 'Status Updates' from the left navigation menu");
		logger.strongStep("Select 'Status Updates' from the left navigation menu");
		Community_TabbedNav_Menu.STATUSUPDATES.select(comUI, 2);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.communityStatusUpdates);

		// Verify that status update with mention should displayed in the community status update view
		log.info("INFO: Verify that status update with mention should displayed in the community status update view");
		logger.strongStep("Verify that status update with mention should displayed in the community status update view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsText}, null, true);

		// Verify that status update without mention should be displayed in the community status update view
		log.info("INFO: Verify that status update without mention should be displayed in the community status update view");
		logger.strongStep("Verify that status update without mention should be displayed in the community status update view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdatePlainTextMessage}, null, true);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
}
