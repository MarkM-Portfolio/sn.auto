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
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Events_MoreActions_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;


public class BVT_VisitorModel_MyNotification_From_Me extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_MyNotification_From_Me.class);
	
	private HomepageUI ui;
	private CommunitiesUI comUI;
	private CalendarUI calUI;
	private APIProfilesHandler profilesAPIUser1;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APICommunitiesHandler apiOwner;
	private APICommunitiesHandler communitiesAPIUser2;
	private String serverURL_MT_orgA;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1;
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser2 = new APICommunitiesHandler(serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
		profilesAPIUser1 = new APIProfilesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		filesAPIUser1= new APIFileHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		filesAPIUser2= new APIFileHandler(serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(),driver);
		calUI = CalendarUI.getGui(cfg.getProductName(),driver);
		
	}
		/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the notification from MyNotifications->From Me view for community activity entry after notifying internal org user</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member and Activity widget added to it</B></li>
	*<li><B>Step: [API] Internal orgA user creates community activity under above community</B></li>
	*<li><B>Step: [API] Internal orgA user creates activity entry in community activity created in above step</B></li>
	*<li><B>Step: [API] External user notify internal user in community activity entry</B></li>
	*<li><B>Step: Log in as a external user</B></li>
	*<li><B>Step: Go to Home page ->My Notifications->From Me view</B></li>
	*<li><B>Verify: Verify that external users receives notification for notifying Internal user from activity entry in My Notifications->From Me view.</B></li>
	*<li><B>Verify: Verify that the recipient's name is a link, and when hover over it, it brings up the sender's business card.</B></li>
	*<li><B>Verify: Verify that recipient's Verify that recipient's name on business card is not a click-able link.</B></li>
	*<li><B>Verify: Verify that when the external user clicks the activity entry link, it opens a new browser tab & brings you to the community activity that was linked.</B></li>
	*</ul>
	*/

	@Test(groups = { "mtlevel2" })
	public void visitorModel_notify_ActivityEntry() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		APIActivitiesHandler activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		APIActivitiesHandler activitiesAPIUser2 = new APIActivitiesHandler("Activity", serverURL_MT_orgA, testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.ACTIVITIES, testExternalUser, true, testInternalUser, apiOwner);

		// Internal orgA user will now create community activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create community activity");
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(baseCommunity.getName(),baseCommunity);
		Activity communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity,testInternalUser, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);

		// Internal orgA user will now create a public entry to community activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a public entry to community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a public entry to community activity");
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(baseCommunity.getName(), communityActivity, false);
		ActivityEntry activityEntry=CommunityActivityEvents.createActivityEntry(testInternalUser, activitiesAPIUser1, baseActivityEntry, communityActivity);
	
		
		// External user notify internal user in community activity entry
		log.info("INFO: External user notify internal user in community activity entry");
		logger.strongStep("External user notify internal user in community activity entry");
		activitiesAPIUser2.notifyUserAboutActivityEntry(communityActivity, activityEntry, profilesAPIUser1);
		 
		//LoginEvents.gotoHomeAndLogout(ui);
		log.info("INFO: External user log in and navigates to My Notificatios view");
		logger.strongStep("External user log in and navigates to My Notificatios view");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);

		log.info("INFO: Navigate to the From Me tab");
		logger.strongStep("Navigate to the From Me tab");
		ui.clickLinkWait(HomepageUIConstants.FromMeTab);
		
		// Create the news stories to be verified
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_ACTIVITY_NOTIFY_ENTRY_FROM_ME_SingleUser, baseActivityEntry.getTitle(), null, testInternalUser.getDisplayName());
		String activityEntryCSSSelector= HomepageUIConstants.SharedExternally_ActivityEntryLink.replace("PLACEHOLDER",newsStory);
		
		log.info("INFO: Verify that notification should be displayed for notifying internal user in community activity entry");
		logger.strongStep("Verify that notification should be displayed for notifying internal user in community activity entry");
		HomepageValid.verifyElementsInAS(ui, driver, new String[] {activityEntryCSSSelector}, null, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[] {newsStory}, null, true);

		// Verify business card displayed after hovering internal users' name
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", newsStory);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);
		
		ui.clickLinkWait(HomepageUIConstants.FromMeTab);

		// Verify user switch to new tab after activity entry link from event is clicked
		String pageTitle = driver.getFirstElement(activityEntryCSSSelector).getText() + " Activity - "+ baseCommunity.getName() + " Community";
		ui.verifyLinkOpenInNewBrowser(activityEntryCSSSelector, pageTitle, CommunitiesUIConstants.communityActivityHeader);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the notification from MyNotifications->From Me view for community calendar event  after notifying internal org user</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with external user as a member and Calendar widget added to it</B></li>
	*<li><B>Step: [API] Internal orgA user creates calendar event under above community</B></li>
	*<li><B>Step: [API] External user notifies internal user in community calendar event</B></li>
	*<li><B>Step: Log in as a external user</B></li>
	*<li><B>Step: Go to Home page ->My Notifications->From Me view</B></li>
	*<li><B>Verify: Verify that external users receives notification for notifying Internal user in calendar event in My Notifications->From Me view.</B></li>
	*<li><B>Verify: Verify that the recipient's name is a link, and when hover over it, it brings up the sender's business card.</B></li>
	*<li><B>Verify: Verify that recipient's name on business card is not a click-able link.</B></li>
	*<li><B>Verify: Verify that when the external user clicks the calendar event link, it opens a new browser tab & brings you to the community calendar event that was linked.</B></li>
	*</ul>
	*/	
	
	@Test(groups = { "mtlevel3" })
	public void visitorModel_notify_EventCalender() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		APICalendarHandler calendarAPIUser2 = new APICalendarHandler(serverURL_MT_orgA,testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());

		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community with external user 'testExternalUser' added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.EVENTS, testExternalUser, true, testInternalUser, communitiesAPIUser1);

		// Set community UUID
		baseCommunity.setCommunityUUID(baseCommunity.getCommunityUUID_API(communitiesAPIUser1, restrictedCommunity));

		// External user will now create a calendar event under community
		log.info("INFO: External user will now create a calendar event under community");
		logger.strongStep(" External user will now create a calendar event under community");
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName + Helper.genDateBasedRandVal(), false);
		Calendar calendarEvent =CommunityCalendarEvents.addCalendarEvent(restrictedCommunity, baseEvent, testExternalUser, calendarAPIUser2);
		
		// External user login and navigate to the community
		log.info("INFO: External user login and navigate to the community");
		logger.strongStep(" External user login and navigate to the community");
		CommunityEvents.loginAndNavigateToCommunity(restrictedCommunity, baseCommunity, ui, comUI, testExternalUser,communitiesAPIUser2, false);

		// Click on the Events link in the nav
		log.info("INFO: Select Events in the left navigation");
		logger.strongStep("Select Events in the left navigation");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		log.info("INFO: Select past event link");
		logger.strongStep("INFO: Select past event link");
		ui.clickLinkWait(CalendarUI.pastEventLink);

		// goto the event detail page -> more actions button -> notify other people link.
		log.info("INFO: Open event " + baseEvent.getName());
		logger.strongStep("INFO: Open event " + baseEvent.getName());
		ui.clickLinkWait(calUI.getEventSelector(baseEvent));

		log.info("INFO: Launch Notify Other People dialog");
		logger.strongStep("Launch Notify Other People dialog");
		Events_MoreActions_Menu.NOTIFY_OTHER_PEOPLE.select(calUI);

		// click the check box for the internal user
		log.info("INFO: Select checkbox for the user to add to notify list");
		logger.strongStep("Select checkbox for the user to add to notify list");
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleListCheckbox);

		log.info("INFO: Select Notify button");
		logger.strongStep("Select the Notify button");
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleOKBtn);
		calUI.fluentWaitPresent(CalendarUI.SuccessMsgBox);
		
		// Go to Home-> My notifications-> From Me
		log.info("INFO: Go to Home and select My notifictaions-> From Me");
		logger.strongStep("Go to Home and select My notifictaions-> From Me");
		ui.gotoHome();
		ui.gotoMyNotifications();
		ui.clickLinkWait(HomepageUIConstants.FromMeTab);

		// Create the news stories to be verified
		String eventNotifystrory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_EVENT_INVITE_FROM_ME,calendarEvent.getTitle(), baseCommunity.getName(), testInternalUser.getDisplayName());
		String eventEntryCSSSelector = HomepageUIConstants.SharedExternally_ActivityEntryLink.replace("PLACEHOLDER",eventNotifystrory);
		
		log.info("INFO: Verify that notification is recieved for notifying internal user in caledar event");
		logger.strongStep("Verify that notification is recieved for notifying internal user in caledar event");
		HomepageValid.verifyItemsInAS(ui, driver, new String[] {eventNotifystrory}, null, true);

		// Verify business card displayed after hovering internal users' name
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", eventNotifystrory);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);

		// Verify user switch to new tab after calendar event link is clicked
		String pageTitle = "Events - " + baseCommunity.getName();
		ui.verifyLinkOpenInNewBrowser(eventEntryCSSSelector, pageTitle, CommunitiesUIConstants.communityEventHeader);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the notification from MyNotifications->From Me view after sharing file with internal org user</B></li>
	*<li><B>Step: [API] External user uploads file which is shared with Internal user</B></li>
	*<li><B>Step: testExternalUser log in as a External User</B></li>
	*<li><B>Step: Go to Home page ->My Notifications-> From Me view</B></li>
	*<li><B>Verify: Verify that external users receives notification for sharing file with internal user in My Notifications->From Me view.</B></li>
	*</ul>
	*/

	@Test(groups = { "mtlevel2" })
	public void visitorModel_notify_StanaloneFile() {
	  
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
	
		// External user will now upload a file shared with internal user
		log.info("INFO: External user"+testExternalUser.getDisplayName()+ "will now upload a file shared with "+testInternalUser.getDisplayName()+"+internal user");
		logger.strongStep("External user"+testExternalUser.getDisplayName()+ "will now upload a file shared with "+testInternalUser.getDisplayName()+"+internal user");
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE,profilesAPIUser1);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testExternalUser, filesAPIUser2);

 		// Login and navigate to My notifications 
		log.info("INFO: External user"+testExternalUser.getDisplayName()+ "login and navigate to My notifications");
		logger.strongStep("External user"+testExternalUser.getDisplayName()+ "login and navigate to My notifications");
		LoginEvents.loginAndGotoMyNotifications(ui, testExternalUser, true);

		// Navigate to the From Me tab
		log.info("INFO: Navigate to the From Me tab");
		logger.strongStep("Navigate to the From Me tab");
		ui.clickLinkWait(HomepageUIConstants.FromMeTab);
		
		// Create the news stories to be verified
		String sharedFileEvent = ui.replaceNewsStory(Data.FILE_SHARED_BASIC, null, null, testExternalUser.getDisplayName());
		logger.strongStep("Verify that notification is being displayed for the file shared by external user with iternal user");
		HomepageValid.verifyItemsInAS(ui, driver, new String[] {sharedFileEvent}, null, true);
		
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
}
