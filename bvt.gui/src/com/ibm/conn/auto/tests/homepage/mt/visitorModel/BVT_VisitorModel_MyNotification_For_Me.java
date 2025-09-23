package com.ibm.conn.auto.tests.homepage.mt.visitorModel;



import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_VisitorModel_MyNotification_For_Me extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_MyNotification_For_Me.class);
	
	private HomepageUI ui;
	private ProfilesUI profilesUI;
	private APIProfilesHandler profilesAPIUser1,profilesAPIUser2;
	private TestConfigCustom cfg;
	private User testInternalUser, testExternalUser;
	private APICommunitiesHandler apiOwner;
	private APICommunitiesHandler communitiesAPIUser1;
	private String serverURL_MT_orgA;
	private APIFileHandler filesAPIUser1;
	
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
		filesAPIUser1= new APIFileHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		profilesUI = ProfilesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
		
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for status messages mentioning external user posted from Homepage in My Notifications->For Me view</B></li>
	*<li><B>Step: Internal user log into Homepage</B></li>
	*<li><B>Step: Go to status updates</B></li>
	*<li><B>Step: Internal user add a status update mentioning a external user </B></li>
	*<li><B>Step: Internal user log out </B></li>
	*<li><B>Step: External user log into Homepage->My Notifications->For Me view</B></li>
	*<li><B>Verify: Verify that external user is able to see the entry with status messages where he has been mentioned</B></li>
	*</ul>
	*/
	@Test(groups = {"mtlevel2"})
	public void visitor_Notification_MentionedInStatusUpdate() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		
		// Internal user will log in and navigate to the Status Updates view
		log.info("INFO: Internal user "+testInternalUser.getDisplayName()+" log in and navigate to the Status Updates view");
		logger.strongStep("Internal user "+testInternalUser.getDisplayName()+" log in and navigate to the Status Updates view");
		LoginEvents.loginAndGotoStatusUpdates(ui, testInternalUser, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA, testName+Helper.genDateBasedRandVal(), "after text");
		
		// Internal user will now post a status update with mentions - mentioning a external user
		log.info("INFO: Internal user "+testInternalUser.getDisplayName()+" will now post a status update with mentions - mentioning a external user"+testExternalUser.getDisplayName());
		logger.strongStep("Internal user "+testInternalUser.getDisplayName()+" will now post a status update with mentions - mentioning a external user"+testExternalUser.getDisplayName());
		ProfileEvents.addStatusUpdateWithMentionsUsingUIMT(ui, driver, testInternalUser,testExternalUser, mentions);
		
		// Log out from Connections
		log.info("INFO: Log out from Connections");
		logger.strongStep("Log out from Connections");
		LoginEvents.logout(ui);
		
		// Log in as external user and navigate to My Notifications ->For Me view
		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify business card displayed after hovering internal users' name
		log.info("INFO: Verify business card displayed after hovering internal users' name");
		logger.strongStep("Verify business card displayed after hovering internal users' name");
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", mentionedYouEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);
		
		// Verify that the mentions event is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the mentions event is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions event is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for the status messages with mentioning external user posted from profiles in My Notifications->For Me view</B></li>
	*<li><B>Step: Internal user log into Profiles</B></li>
	*<li><B>Step: Go to Recent updates</B></li>
	*<li><B>Step: Internal user add a status update mentioning a external user </B></li>
	*<li><B>Step: Internal user log out </B></li>
	*<li><B>Step: External user log into Homepage->My Notifications->For Me view</B></li>
	*<li><B>Verify: Verify that external user is able to see the entry with status messages where he has been mentioned</B></li>
	*</ul>
	*/
	@Test(groups = {"mtlevel3"})
	public void visitor_Notification_MentionedInBoardMessage() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName=ui.startTest();
		
		// Internal user will log in and navigate to the Status Updates view
		log.info("INFO: Internal user "+testInternalUser.getDisplayName()+" log in and navigate to the Status Updates view");
		logger.strongStep("Internal user "+testInternalUser.getDisplayName()+" log in and navigate to the Status Updates view");
		ProfileEvents.loginAndNavigateToUserProfile(ui, profilesUI, testInternalUser, profilesAPIUser1, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testExternalUser, profilesAPIUser2, serverURL_MT_orgA, testName+Helper.genDateBasedRandVal(),"after text" );
		
		// Internal user will now post a board message with mentions - mentioning a visitor
		log.info("INFO: Internal user "+testInternalUser.getDisplayName()+" will now post a board message with mentions - mentioning external user"+testExternalUser.getDisplayName());
		logger.strongStep("Internal user "+testInternalUser.getDisplayName()+" will now post a board message with mentions - mentioning external user"+testExternalUser.getDisplayName());
		ProfileEvents.addStatusUpdateWithMentionsUsingUIMT(ui, driver, testInternalUser, testExternalUser, mentions);

		// Return to the Home screen and log out from Connections
		log.info("INFO: Log out from Connections");
		logger.strongStep("Log out from Connections");
		LoginEvents.gotoHomeAndLogout(ui);

		// Log in as external user and navigate to My Notifications ->For Me view
		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testInternalUser.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testExternalUser.getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify business card displayed after hovering internal users' name
		log.info("INFO: Verify business card displayed after hovering internal users' name");
		logger.strongStep("Verify business card displayed after hovering internal users' name");
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", mentionedYouEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);
		
		// Verify that the mentions event is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the mentions event is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the mentions event is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for he is being removed from community in Home page->My Notifications->For Me view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user added as a member</B></li>
	*<li><B>Step: [API] Remove external user form community created above</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->My Notification->For Me view</B></li>
	*<li><B>Verify: Verify that the entry for user has removed from community should appear.</B></li>
	*</ul> 
	*/
	
	@Test(groups = { "mtlevel2" })
	public void visitor_Notification_RemovedFromCommunity() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser,apiOwner,testExternalUser);

		// External user will be removed from community
		log.info("INFO: " + testExternalUser.getDisplayName() + " removed from the Community using API");		
		logger.strongStep(testExternalUser.getDisplayName()+ " removed from the Community using API");
		communitiesAPIUser1.removeMemberFromCommunity(restrictedCommunity, profilesAPIUser2);
	
		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);
		
		ui.waitForPageLoaded(driver);

		// Create the elements to be verified
		String removedYouEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_REMOVE_MEMBER_FOR_ME, baseCommunity.getName(), null, testInternalUser.getDisplayName());
		log.info("removedYouEvent is: " + removedYouEvent);

		// Verify that the removed event is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the removed event is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the removed event is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{removedYouEvent}, null, true);
		
		// Verify business card displayed after hovering internal users' name
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", removedYouEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);
		
		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for he is being added to community in Home page->My Notifications->For Me view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user added as a member</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->My Notification->For Me view</B></li>
	*<li><B>Verify: Verify that the entry for user has been added to community should appear.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void visitor_Notification_AddedToCommunity() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testInternalUser,apiOwner,testExternalUser);

		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);

		// Create the elements to be verified
		String addedYouEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, baseCommunity.getName(), null, testInternalUser.getDisplayName());
		log.info("addedYouEvent is: " + addedYouEvent);

		// Verify that the added you event is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the added you event is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the added you event is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{addedYouEvent}, null, true);
		
		// Verify business card displayed after hovering internal users' name
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", addedYouEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);
		
		log.info("INFO: Verify that when comunity link from entry get clicked it opens in a new browser tab & brings you to the community overviewpage.");
		logger.strongStep("Verify that when comunity link from entry get clicked it opens in a new browser tab & brings you to the community overviewpage.");
		String pageTitle= "Overview - " +baseCommunity.getName();
		
		String communityLinkCSSSelector= HomepageUIConstants.SharedExternally_ActivityEntryLink.replace("PLACEHOLDER",addedYouEvent);
		ui.verifyLinkOpenInNewBrowser(communityLinkCSSSelector, pageTitle, CommunitiesUIConstants.communityOverview);

		log.info("INFO: Delete the community");
		logger.strongStep("Delete the community");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see community activity events for the community he is member of from Home page->My Notifications->For Me view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member and Activity widget added </B></li>
	*<li><B>Step: [API] Internal orgA user creates community activity</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page - My Notifications->For Me view</B></li>
	*<li><B>Verify: Verify that community activity created entries appears </B></li>
	*<li><B>Verify: Verify that community activity entry created entries appears</B></li>
	*</ul>
	 * @throws Exception 
	*/
	@Test(groups = { "mtlevel3" })
	public void visitor_Notification_CommActivityEvents() throws Exception {

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
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(baseCommunity.getName(), communityActivity, false);
		ActivityEntry activityEntry=CommunityActivityEvents.createActivityEntry(testInternalUser, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// Internal user notify external user in community activity entry
		log.info("INFO: External user notify internal user in community activity entry");
		logger.strongStep("External user notify internal user in community activity entry");
		activitiesAPIUser1.notifyUserAboutActivityEntry(communityActivity, activityEntry, profilesAPIUser2);
				
		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);
		
		// Create the news stories to be verified
		String communityActivityEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_ACTIVITY_ENTRY_NOTIFICATION, baseActivityEntry.getTitle(), null, testInternalUser.getDisplayName());
				
		// Verify that the community activity created event is displayed in the My Notifications->For Me view
		log.info("INFO: Verify that the community activity created event is displayed in the My Notifications->For Me view");
		logger.strongStep("Verify that the community activity created event is displayed in the My Notifications->For Me view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{communityActivityEvent}, null, true);
		
		// Verify business card displayed after hovering internal users' name
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", communityActivityEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);
		
		// Verify that when comunity-activity entry link from notification get clicked, it opens in a new browser tab & brings you to the community-activity page
		log.info("INFO: Verify that when comunity-activity entry link from notification get clicked, it opens in a new browser tab & brings you to the community-activity page.");
		logger.strongStep("Verify that when comunity link from entry get clicked it opens in a new browser tab & brings you to the community overviewpage.");	
		String communityActivityLinkCSSSelector= HomepageUIConstants.SharedExternally_ActivityEntryLink.replace("PLACEHOLDER",communityActivityEvent);
		String pageTitle= driver.getFirstElement(communityActivityLinkCSSSelector).getText()+" Activity - "+baseCommunity.getName()+" Community";
		ui.verifyLinkOpenInNewBrowser(communityActivityLinkCSSSelector,pageTitle, CommunitiesUIConstants.tabNavCommunityName);
		
		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for file shared by internal user with him</B></li>
	*<li><B>Step: [API] Internal user uploads file which is shared with external user</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->My Notifications->For Me view</B></li>
	*<li><B>Verify: Verify that external user is able to see the entry for file shared by internal user with him.</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitor_Notification_sharedStandAloneFile() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
	
		// Internal user will now upload a private file
		log.info("INFO: Internal user will now upload a private file");
		logger.strongStep("Internal user will now upload a private file");
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE,profilesAPIUser2);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testInternalUser, filesAPIUser1);

		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);

		// Create the news stories to be verified
		String sharedFileEvent = ui.replaceNewsStory(Data.FILE_SHARED, null, null,testInternalUser.getDisplayName());
		logger.strongStep("Verify that notification is being displayed for the file shared by internal user with external user");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{sharedFileEvent}, null, true);

		// Verify business card displayed after hovering internal users' name
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", sharedFileEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);

		// Delete the file
		log.info("INFO: Delete the file");
		logger.weakStep("Delete the file");
		filesAPIUser1.deleteFile(fileEntry);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the entry for folder shared by internal user with him</B></li>
	*<li><B>Step: [API] Internal user creates folder which is shared with external user with reader role</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->My Notifications->For Me view</B></li>
	*<li><B>Verify: Verify that external user is able to see the entry that that "You were made reader of a folder.".</B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void visitor_Notification_sharedFolder() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
	
		logger.strongStep(testInternalUser.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testInternalUser.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder(testName + Helper.genStrongRand())
											.sharedWith(profilesAPIUser2.getUUID())
											.shareLevel(ShareLevel.PEOPLE)
											.build();
		
		FileEntry sharedFolder = filesAPIUser1.createFolder(baseFolder, Role.READER);
		log.info("INFO: Public folder shared with person folder created successfully");
		logger.strongStep("Public folder shared with person created successfully");
		
		log.info("INFO: External user log in and navigates to My Notifications ->For Me view");
		logger.strongStep("External user log in and navigates to My Notifications ->For Me view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);
		
		// Create the news stories to be verified
		String sharedFileEvent = ui.replaceNewsStory(Data.FOLDER_MADE_READER_FOR_ME, null, null, null);
		logger.strongStep("Verify that notification is being displayed for the folder shared by internal user with external user");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{sharedFileEvent}, null, true);

		// Delete the file
		log.info("INFO: Delete the file");
		logger.weakStep("Delete the file");
		filesAPIUser1.deleteFolder(sharedFolder);
		ui.endTest();
	}
}
