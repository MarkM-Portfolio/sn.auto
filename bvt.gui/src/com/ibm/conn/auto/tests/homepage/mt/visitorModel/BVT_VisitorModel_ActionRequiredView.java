package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

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
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_VisitorModel_ActionRequiredView extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_ActionRequiredView.class);
	
	private HomepageUI ui;
	private CommunitiesUI comUI;
	private APIProfilesHandler profilesAPIUser2;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APICommunitiesHandler apiOwner;
	private String serverURL_MT_orgA;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();		
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		apiOwner = new APICommunitiesHandler(serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
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
	*<li><B>Info: Verify that external user can see community invite events in Action Required view</B></li>
	*<li><B>Step: External user log into Action Required view/B></li>
	*<li><B>Step: External user checks the badge value displayed in action required view before community invite event is created</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community </B></li>
	*<li><B>Step: [API] Internal orgA user Invite external user to join restricted community </B></li>
	*<li><B>Step: External user refresh the page to check the updated badge value</B></li>
	*<li><B>Step: External user again checks the badge value displayed in action required view after community invite event is created</B></li>
	*<li><B>Verify: Verify that the badge value before invite action is less than the badge value after invite action</B></li>
	*<li><B>Verify: Verify that the badge value incremented by 1 after adding the community invite news story</B></li>
	*<li><B>Verify: Verify that community invite created entries appears in Action Required view</B></li>
	*<li><B>Verify: Verify that entry does show up 'Join this community' and 'Decline this invitation' link"</B></li>
	*<li><B>Verify: Verify that when community link from entry get clicked and opens in a new browser tab & brings you to the community's landing page</B></li>
	*<li><B>Verify: Verify the 'You have joined the community...' message displays</B></li>
	*<li><B>Step: Go to community Members widget </B></li>
	*<li><B>Verify: Verify that external user has been added successfully in members list</B></li>
	*<li><B>Step: Delete the community</B></li>
	*</ul>
	* @throws Exception 
	*/
	@Test(groups = { "mtlevel2" })
	public void visitor_ActionRequired_CommunityInviteEvent() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		log.info("INFO: External user log in and navigates to Action Required view");
		logger.strongStep("External user log in and navigates to Action Required view");
		LoginEvents.loginAndGotoActionRequired(ui, testExternalUser, false);
		
		// Retrieve the Action Required badge value before news stories are created
		int badgeValueBeforeNewsStoryAddition = UIEvents.getActionRequiredBadgeValue(driver);
		log.info("INFO: External user checks the badge value " +badgeValueBeforeNewsStoryAddition+" is displayed in action required view before community invite event is created");
		logger.strongStep("External user checks the badge value " +badgeValueBeforeNewsStoryAddition+" is displayed in action required view before community invite event is created");
		
		// Internal orgA user will now create a restricted community 
		log.info("INFO: Internal orgA user "+testInternalUser.getDisplayName()+" will now create a restricted community with external user added as a member");
		logger.strongStep("Internal orgA user "+testInternalUser.getDisplayName()+" will now create a restricted community with external user added as a member");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		
		log.info("INFO: " + testInternalUser.getDisplayName() + " creating Community " + baseCommunity.getName() + " using API method");
		Community restrictedCommunity = baseCommunity.createAPI(apiOwner);

		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(restrictedCommunity)); 	
		
		// Internal orgA user invites external user to join community
		log.info("INFO: Internal orgA user Invite "+testExternalUser.getDisplayName()+" to join restricted community");	
		logger.strongStep("Internal orgA user Invite "+testExternalUser.getDisplayName()+" to join restricted community");
		apiOwner.inviteUserToJoinCommunity(restrictedCommunity, profilesAPIUser2);
		
		// Refresh the page to get the updated action required badge value
		log.info("INFO: Refresh the page to get the updated action required badge value");
		logger.strongStep("Refresh the page to get the updated action required badge value");
		ui.waitForPageLoaded(driver);
		driver.navigate().refresh();
		
		// Retrieve the Action Required badge value now that a news story has been added
		int badgeValueAfterNewsStoryAddition = UIEvents.getActionRequiredBadgeValue(driver);
		log.info("INFO: External user checks the badge value " +badgeValueAfterNewsStoryAddition+" is displayed in action required view after community invite event is created");
		logger.strongStep("External user checks the badge value " +badgeValueAfterNewsStoryAddition+" is displayed in action required view after community invite event is created");

		// Verify that the badge value before addition is less than the badge value after addition
		log.info("INFO: Verify that the badge value before invite action is less than the badge value after invite action");
		logger.strongStep("Verify that the badge value before invite action is less than the badge value after invite action");
		HomepageValid.verifyBooleanValuesAreEqual(badgeValueBeforeNewsStoryAddition < badgeValueAfterNewsStoryAddition,true);

		// Verify that the badge value incremented by 1 after adding the news story
		log.info("INFO: Verify that the badge value incremented by 1 after adding the community invite news story");
		logger.strongStep("Verify that the badge value incremented by 1 after adding the community invite news story");
		HomepageValid.verifyIntValuesAreEqual((badgeValueAfterNewsStoryAddition - badgeValueBeforeNewsStoryAddition),1);
		
		// Create the news stories to be verified
		String joinCommunityEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME, baseCommunity.getName(), null, testInternalUser.getDisplayName());
				
		// Verify that the community activity created event is displayed in the Action Required view
		log.info("INFO: Verify that the community activity created event is displayed in the Action Required view");
		logger.strongStep("Verify that the community activity created event is displayed in the Action Required view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{joinCommunityEvent}, null, true);
		
		// Create elements for 'Join this community' link and 'Decline this invitation' link which is to be verified
		String joinCommunityLink = HomepageUIConstants.CommunityInvite_Join_Link.replaceAll("PLACEHOLDER", joinCommunityEvent);
		String declineInvitationLink = HomepageUIConstants.CommunityInvite_Decline_Link.replaceAll("PLACEHOLDER", joinCommunityEvent);

		logger.strongStep(" Verify that entry does show up 'Join this community' and 'Decline this invitation' link");
		log.info("INFO: Verify that entry does show up 'Join this community' and 'Decline this invitation' link");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{joinCommunityLink, declineInvitationLink}, null, true);
		
		log.info("INFO: Verify that when comunity link from entry get clicked it opens in a new browser tab & brings you to the community's landing page.");
		logger.strongStep("Verify that when comunity link from entry get clicked it opens in a new browser tab & brings you to the community's landing page.");
		String pageTitle;
		if(apiOwner.setup.getServiceConfig().isCnxVersion65())
		{
			pageTitle= "Overview - " +baseCommunity.getName();
			ui.verifyLinkOpenInNewBrowser(joinCommunityLink, pageTitle, CommunitiesUIConstants.communityOverview);
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.Communities_Joined_NoFollow),
					"ERROR: The 'You have joined the community...' message does not display.");

		}
 		else
 		{
			pageTitle= "Highlights - " +baseCommunity.getName();
 			ui.verifyLinkOpenInNewBrowser(joinCommunityLink, pageTitle, CommunitiesUIConstants.communityHighlights);
 			//TODO: TBD 7.0 behavior when CNXSERV-8910 is resolved.
 			/*Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(Data.Communities_Joined_NoFollow),
 					"ERROR: The 'You have joined the community...' message does not display.");*/

 		}
		
		ui.waitForPageLoaded(driver);
		
		baseCommunity.navViaUUID(comUI);
						
		// Select Members from top community widgets
		log.info("INFO: Select Members from top community widgets");
		logger.strongStep("Select Members from top community widgets");
		Community_TabbedNav_Menu.MEMBERS.select(comUI, 3);
		
		// Verify that external user has been added successfully in members list
		log.info("INFO: Verify that external user has been added successfully in members list");
		logger.strongStep("Verify that external user has been added successfully in members list");
		Assert.assertTrue(comUI.isMemberOfCommunity(testExternalUser), "ERROR: The external user "
				+ testExternalUser.getDisplayName() + " joined community via link could not be found in members list");

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see business card upon hovering over senders's name from community invite events in Action Required view</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community </B></li>
	*<li><B>Step: [API] Internal orgA user Invite external user to join restricted community </B></li>
	*<li><B>Step: External user log into Action Required view </B></li>
	*<li><B>Step: Verify that the community invite event is displayed in the action required view </B></li>
	*<li><B>Step: external user hover over the sender's name (internal user) link</B></li>
	*<li><B>Verify: Verify that correct business card for internal user should be displayed and sender's name on business card should not be a click-able link.</B></li>
	*<li><B>Step: Delete the community</B></li>
	*</ul>
	*/
	
	@Test(groups = { "mtlevel3" })
	public void visitor_ActionRequired_CommunityInviteBizCard() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		// Internal orgA user will now create a restricted community
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a restricted community ");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a restricted community ");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		
		log.info("INFO: " + testInternalUser.getDisplayName() + " creating Community " + baseCommunity.getName() + " using API method");
		Community restrictedCommunity = baseCommunity.createAPI(apiOwner);

		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(restrictedCommunity)); 	
		
		log.info("INFO: Inviting" + testExternalUser.getDisplayName() + "  to join the restricted community");	
		logger.strongStep("Inviting" + testExternalUser.getDisplayName() + "  to join the restricted community");
		apiOwner.inviteUserToJoinCommunity(restrictedCommunity, profilesAPIUser2);		
				
		log.info("INFO: External user log in and navigates to Action Required view");
		logger.strongStep("External user log in and navigates to Action Required view");
		LoginEvents.loginAndGotoActionRequired(ui, testExternalUser, false);
		
		// Create the news stories to be verified
		String joinCommunityEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME, baseCommunity.getName(), null, testInternalUser.getDisplayName());
				
		// Verify that the community invite event is displayed in the action required view
		log.info("INFO: Verify that the community invite event is displayed in the action required view");
		logger.strongStep("Verify that the community invite event is displayed in the action required view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{joinCommunityEvent}, null, true);
		
		// Verify business card displayed after hovering internal users' name
		log.info("INFO: Verify that correct business card for internal user should be displayed and sender's name on business card should not be a click-able link");
		logger.strongStep("Verify that correct business card for internal user should be displayed and sender's name on business card should not be a click-able link");
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", joinCommunityEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: Verify that external user can see the event in Action Required view when internal user assigns him a community activity ToDo </B></li>
	*<li><B>Step: External user log into Action Required view/B></li>
	*<li><B>Step: External user checks the badge value displayed in action required view before assigned ToDo event is created</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with Activity widget and external user as a member added </B></li>
	*<li><B>Step: [API] Internal orgA user creates community activity</B></li>
	*<li><B>Step: [API] Internal orgA user creates ToDo in community activity created in above step</B></li>
	*<li><B>Step: [API] Internal orgA user assigns ToDo to external user</B></li>
	*<li><B>Step: External user refresh the page to check the updated badge value</B></li>
	*<li><B>Step: External user again checks the badge value displayed in action required view before community activity ToDo assign event is created</B></li>
	*<li><B>Verify: Verify that the badge value before assigned action is less than the badge value after assigned action</B></li>
	*<li><B>Verify: Verify that the badge value incremented by 1 after adding the assigned ToDo news story</B></li>
	*<li><B>Verify: Verify that ToDo assigned entry appears in Action Required view</B></li>
	*<li><B>Step: external user hover over the sender's name (internal user) link</B></li>
	*<li><B>Verify: Verify that correct business card for internal user should be displayed and sender's name on business card should not be a click-able link.</B></li>	
	*<li><B>Verify: Verify that when ToDo link from entry get clicked and opens in a new browser tab & brings you to the respective community page</B></li>
	*<li><B>Step: Delete the community</B></li>
	*</ul>
	* @throws Exception 
	*/
	@Test(groups = { "mtlevel3" })
	public void visitor_ActionRequired_CommActivityToDoEvent() throws Exception {

		String testName = ui.startTest();
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		APIActivitiesHandler activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL_MT_orgA, testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		
		log.info("INFO: External user log in and navigates to Action Required view");
		logger.strongStep("External user log in and navigates to Action Required view");
		LoginEvents.loginAndGotoActionRequired(ui, testExternalUser, true);
		
		// Retrieve the Action Required badge value before news stories are created
		int badgeValueBeforeNewsStoryAddition = UIEvents.getActionRequiredBadgeValue(driver);
		log.info("INFO: External user checks the badge value " +badgeValueBeforeNewsStoryAddition+" is displayed in action required view before community-activity ToDo assign event is created");
		logger.strongStep("External user checks the badge value " +badgeValueBeforeNewsStoryAddition+" is displayed in action required view before community-activity ToDo assign event is created");
		
		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user creates a restricted community with Activity widget and external user as a member added");
		logger.strongStep("Internal orgA user creates a restricted community with Activity widget and external user as a member added");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity,BaseWidget.ACTIVITIES, testExternalUser, true, testInternalUser, apiOwner);
		
		// Internal orgA user will now create community activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create community activity");
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(baseCommunity.getName(), baseCommunity);
		Activity communityActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testInternalUser, activitiesAPIUser1, apiOwner, restrictedCommunity);
		
		// Internal orgA user will now create a to-do item in the activity
		log.info("INFO: Internal orgA user 'testInternalUser' will now create a to-do item to community activity");
		logger.strongStep("Internal orgA user 'testInternalUser' will now create a to-do item  to community activity");
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		Todo toDoItem = CommunityActivityEvents.createActivityTodo(testInternalUser, activitiesAPIUser1, baseActivityTodo, communityActivity);
		
		// Internal user assign to-do to external user in community activity 
		log.info("INFO: Internal user assign to-do to external user in community activity");
		logger.strongStep("Internal user assign to-do to external user in community activity");
		activitiesAPIUser1.assignToDoItemToUser(toDoItem, profilesAPIUser2);
		
		// Refresh the page to get the updated action required badge value 
		log.info("INFO: Refresh the page to get the updated action required badge value");
		logger.strongStep("Refresh the page to get the updated action required badge value");
		driver.navigate().refresh();
	
		// Retrieve the Action Required badge value now that a news story has been added
		int badgeValueAfterNewsStoryAddition = UIEvents.getActionRequiredBadgeValue(driver);
		log.info("INFO: External user checks the badge value " +badgeValueAfterNewsStoryAddition+" is displayed in action required view after community-activity ToDo assign event is created");
		logger.strongStep("External user checks the badge value " +badgeValueAfterNewsStoryAddition+" is displayed in action required view after community-activity ToDo assign event is created");
		
		// Verify that the badge value before addition is less than the badge value after addition
		log.info("INFO: Verify that the badge value before to-do assign action is less than the badge value after to-do assign action");
		logger.strongStep("Verify that the badge value before to-do assign action is less than the badge value after to-do assign action");
		HomepageValid.verifyBooleanValuesAreEqual(badgeValueBeforeNewsStoryAddition < badgeValueAfterNewsStoryAddition, true);
				
		// Verify that the badge value incremented by 1 after adding the news story
		log.info("INFO: Verify that the badge value incremented by 1 after adding the to-do assign news story");
		logger.strongStep("Verify that the badge value incremented by 1 after adding the to-do assign action news story");
		HomepageValid.verifyIntValuesAreEqual((badgeValueAfterNewsStoryAddition - badgeValueBeforeNewsStoryAddition), 1);
				
		// Create the news stories to be verified
		String communityActivityToDoAssignedEvent = ui.replaceNewsStory(Data.ASSIGNED_TODO_ITEM_YOU, baseActivityTodo.getTitle(), baseActivity.getName(), testInternalUser.getDisplayName());

		// Verify that ToDo assigned entry appears in Action Required view
		log.info("INFO: Verify that ToDo assigned entry appears in Action Required view");
		logger.strongStep("Verify that ToDo assigned entry appears in Action Required view");
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{communityActivityToDoAssignedEvent}, null, true);

		// Verify business card displayed after hovering internal users' name
		log.info("INFO: Verify that correct business card for internal user should be displayed and sender's name on business card should not be a click-able link.");
		logger.strongStep("Verify that correct business card for internal user should be displayed and sender's name on business card should not be a click-able link.");
		String RecipientUserNameCSSSelctornewsStory = HomepageUIConstants.SharedExternally_ExternalUserLink.replace("PLACEHOLDER", communityActivityToDoAssignedEvent);
		Element userNameLink = driver.getFirstElement(RecipientUserNameCSSSelctornewsStory);
		ui.verifyBizCard(userNameLink);

		// Verify that when community-activity entry link from notification get clicked, it opens in a new browser tab & brings you to the community-activity page
		log.info("INFO: Verify that when comunity-activity entry link from notification get clicked, it opens in a new browser tab & brings you to the community-activity page.");
		logger.strongStep("Verify that when comunity link from entry get clicked it opens in a new browser tab & brings you to the community overviewpage.");
		String communityActivityToDoLinkCSSSelector = HomepageUIConstants.SharedExternally_ActivityEntryLink.replace("PLACEHOLDER",communityActivityToDoAssignedEvent);
		String pageTitle = baseActivity.getName() + " Activity - "+ baseCommunity.getName() + " Community";
		ui.verifyLinkOpenInNewBrowser(communityActivityToDoLinkCSSSelector, pageTitle, CommunitiesUIConstants.tabNavCommunityName);

		log.info("INFO: Delete the community clean up");
		apiOwner.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

}
