package com.ibm.conn.auto.tests.homepage.mt.visitorModel;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_VisitorModel_EventsWidget extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_VisitorModel_EventsWidget.class);
	
	private HomepageUI ui;
	private CommunitiesUI comUI;
	private CalendarUI calUI;
	private TestConfigCustom cfg;	
	private User testInternalUser,testExternalUser;
	private APICommunitiesHandler communitiesAPIUser1,communitiesAPIUser2;
	private String serverURL_MT_orgA;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		testInternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testExternalUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.externalOrgA, this);
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL_MT_orgA,testInternalUser.getAttribute(cfg.getLoginPreference()), testInternalUser.getPassword());
		communitiesAPIUser2 = new APICommunitiesHandler(serverURL_MT_orgA,testExternalUser.getAttribute(cfg.getLoginPreference()), testExternalUser.getPassword());
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {

		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
	}

	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the 'Event' widget from different views from home page</B></li>
	*<li><B>Step: External User log in as a External User to home page- Verification point1 </B></li>
	*<li><B>Step: Then Select 'Mentions' from left navigation- Verification point1</B></li>
	*<li><B>Step: Then select 'My Notifications' from left navigation - Verification point1</B></li>
	*<li><B>Step: Then select 'Action Required' from left navigation - Verification point1</B></li>
	*<li><B>Step: Then select 'saved' from left navigation - Verification point1</B></li>
	*<li><B>Verify: Verification point1 :Verify user is able to see the 'Event' widget in right panel </B></li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void visitor_EventWidget() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Log in to Connections as a external user
		log.info("INFO: Log in to Connections as a external user");
		logger.strongStep("Log in to Connections as a external user");
		LoginEvents.loginToHomepage(ui, testExternalUser, false);

		// Verify the 'Event' widget is displayed in right panel
		log.info("INFO: Verify that 'Event' widget appears in the right side of page");
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		// User will now click on the Mentions link
		log.info("INFO: Select the Mentions link.");
		logger.strongStep("Select the Mentions link");
		ui.clickLinkWithJavascript(HomepageUIConstants.AtMentions);

		// Verify the 'Event' widget is displayed in right panel
		log.info("INFO: Verify that 'Event' widget appears in the right side of page");
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		// User will now click on the My Notifications link
		log.info("INFO: Select the My Notifications link");
		logger.strongStep("Select the My Notifications link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_MyNotifications);

		// Verify the 'Event' widget is displayed in right panel
		log.info("INFO: Verify that 'Event' widget appears in the right side of page");
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		// User will now click on the Action Required link
		log.info("INFO: Select the Action Required link");
		logger.strongStep("Select the Action Required link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_ActionRequired);

		// Verify the 'Event' widget is displayed in right panel
		log.info("INFO: Verify that 'Event' widget appears in the right side of page");
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		// User will now click on the Saved link
		log.info("INFO: Select the Saved link");
		logger.strongStep("Select the Saved link");
		ui.clickLinkWithJavascript(HomepageUIConstants.Ckpt_Saved);

		// Verify the 'Event' widget is displayed in right panel
		log.info("INFO: Verify that 'Event' widget appears in the right side of page");
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: Verify that external user is able to see the Calendar event under 'Events' widget on home page</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with 'Events' widget added and external user as a member </B></li>
	*<li><B>Step: [API] External orgA user follow the community created above</B></li>
	*<li><B>Step: Internal orgA user navigate to created community view</B></li>
	*<li><B>Step: Internal user select 'Event' widget from community view</B></li>
	*<li><B>Step: Internal user select 'Create an event' button from event widget</B></li>
	*<li><B>Step: Internal user enters event details like title, tag and creates 'All day event'</B></li>
	*<li><B>Step: Internal user logs out from connection</B></li>
	*<li><B>Step: External User log in and navigate to the community created in above step</B></li>
	*<li><B>Step: External User selects 'Events' widget from community view</B></li>
	*<li><B>Step: Go to the event detail page</B></li>
	*<li><B>Step: External User follows the all day community event</B></li>
	*<li><B>Step: Navigate to Homepage view</B></li>
	*<li><B>Verify: Verify that the all day community event is displayed under 'Events' widget in right panel</B></li>
	*<li><B>Verify: Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget</B></li>
	*<li><B>Step: select 'Add to personal calendar' link</B></li>
	*<li><B>Verify: Verify user switches to new tab after selecting 'Add to Personal Calendar' link</B></li>
	*<li><B>Step: Close 'Add to personal calendar' window and move back to parent window</B></li>
	*<li><B>Step: Now select the all day event link</B></li>
	*<li><B>Verify: Verify user switches to new tab after selecting event link</B></li>
	*<li><B>Verify: Verify that it brings you to the community-Events widget page</B></li>
	*</ul>
	* @throws Exception 
	*/
    @Test(groups = { "mtlevel2" })
	public void visitor_Event_FollowedCommunityEvent() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user creates a restricted community with Event widget and external user as a member added");
		logger.strongStep("Internal orgA user creates a restricted community with Event widget and external user as a member added");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = baseCommunity.createAPI(communitiesAPIUser1);
		baseCommunity.addWidgetAPI(restrictedCommunity, communitiesAPIUser1, BaseWidget.EVENTS);
		communitiesAPIUser1.addMemberToCommunity(testExternalUser, restrictedCommunity, Role.MEMBER);

		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");
		logger.strongStep(testExternalUser.getDisplayName() + " follow the Community using API");
		communitiesAPIUser2.followCommunity(restrictedCommunity);

		// Internal orgA user navigates to created community view
		log.info("INFO: Internal orgA user navigates to created community view");
		logger.strongStep("Internal orgA user navigates to created community view");
		CommunityEvents.loginAndNavigateToCommunity(restrictedCommunity, baseCommunity, ui, comUI, testInternalUser,communitiesAPIUser1, false);

		// Internal user select 'Event' widget from community view
		log.info("INFO: Internal user select 'Event' widget from community view");
		logger.strongStep("Internal user select 'Event' widget from community view");
		Community_LeftNav_Menu.EVENTS.select(comUI);

		// Internal user select 'Create an event' button from event widget
		log.info("INFO: Internal user select 'Create an event' button from event widget");
		logger.strongStep("Internal user select 'Create an event' button from event widget");
		calUI.clickLinkWait(CalendarUI.CreateEvent);
		
		BaseEvent normalEvent = new BaseEvent.Builder("all Day Event " + Helper.genDateBasedRand())
			                                            	.allDayEvent(true)
			                                            	.tags("tag_"+testName)
			                                            	.description("all day event description")
			                                            	.build();
		
		// Internal user enters event details like title, tag and creates 'All day' events
		log.info("INFO: Internal user enters event details like title, tag and creates 'All day' events");
		logger.strongStep("Internal user enters event details like title, tag and creates 'All day' events");
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(normalEvent.getName());
		driver.getSingleElement(CalendarUI.EventTag).clear();
		driver.getSingleElement(CalendarUI.EventTag).type(normalEvent.getTags());
		calUI.typeNativeInCkEditor(normalEvent.getDescription());
		ui.clickLinkWait(CalendarUI.EventSubmit);

		log.info("INFO: Event is created successfully");
		logger.strongStep("Event is created successfully");
		ui.fluentWaitPresent(calUI.getEventSelector(normalEvent));

		// Internal user logs out from connection
		log.info("INFO: Internal user logs out from connection");
		logger.strongStep(" Internal user logs out from connection");
		LoginEvents.gotoHomeAndLogout(ui);

		// External User log in and navigate to the community created in above step
		log.info("INFO: External User log in and navigate to the community created in above step");
		logger.strongStep("External User log in and navigate to the community created in above step");
		CommunityEvents.loginAndNavigateToCommunity(restrictedCommunity, baseCommunity, ui, comUI, testExternalUser,communitiesAPIUser1, true);

		// External User selects 'Events' widget from community view
		log.info("INFO: External User selects 'Events' widget from community view");
		logger.strongStep("External User selects 'Events' widget from community view");
		Community_LeftNav_Menu.EVENTS.select(comUI);

		// Go to the event detail page
		log.info("INFO: Open event " + normalEvent.getName());
		logger.strongStep("Open event " + normalEvent.getName());
		ui.clickLinkWait(calUI.getEventSelector(normalEvent));

		// External User follows the all day community event
		log.info("INFO:External User follows the all day community event");
		logger.strongStep("External User follows the all day community event");
		calUI.fluentWaitElementVisible(CalendarUI.Follow);
		ui.clickLinkWait(CalendarUI.Follow);
		calUI.fluentWaitPresent(CalendarUI.StopFollowing);

		// Navigate to Homepage view
		log.info("INFO: Navigate to Homepage view");
		logger.strongStep("Navigate to Homepage view");
		ui.gotoHome();

		// Verify that 'Event' widget appears in the right side of page
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		// Create elements to be verified
		String eventLinkCSSSelector = HomepageUIConstants.eventLinkInEventWidget.replace("REPLACETHIS", normalEvent.getName());
		String eventCSSSelector = HomepageUIConstants.eventInEventWidget.replace("REPLACETHIS", normalEvent.getName());
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));

		// Verify that the all day community event is displayed under 'Events'widget in right panel
		log.info("INFO:Verify that the all day community event is displayed under 'Events' widget in right panel");
		logger.strongStep("Verify that the all day community event is displayed under 'Events' widget in right panel");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{eventLinkCSSSelector, eventCSSSelector}, null, true);

		// verify that community event is a click-able link
		driver.getSingleElement(eventLinkCSSSelector).getTagName().equals("a");

		// Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget
		log.info("INFO: Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget");
		logger.strongStep(" Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.addToPersonalCalendar),"ERROR: 'Add to Personal calendar' was NOT displayed under Event widget");

		// Select 'Add to personal calendar' link
		log.info("INFO: Select 'Add to personal calendar' link");
		logger.strongStep("Select 'Add to personal calendar' link");
		ui.clickLinkWait(CalendarUI.addToPersonalCalendar);
		driver.switchToFrame().returnToTopFrame();

		// Verify user switches to new tab after selecting 'Add to Personal Calendar' link
		log.info("INFO: Verify user switches to new tab after selecting 'Add to Personal Calendar' link");
		logger.strongStep("Verify user switches to new tab after selecting 'Add to Personal Calendar' link");
		Assert.assertTrue(ui.fluentWaitNumberOfWindowsEqual(2));

		// Close 'Add to personal calendar' window and move back to parent window
		log.info("INFO: Close 'Add to personal calendar' window and move back to parent window");
		logger.strongStep(" Close 'Add to personal calendar' window and move back to parent window");
		ui.closeNewTabAndMoveToParentTab();

		log.info("INFO: Select Event link from 'Events' widget");
		logger.strongStep("Select Event link from 'Events' widget");
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));
		ui.clickLinkWithJavascript(eventLinkCSSSelector);
		driver.switchToFrame().returnToTopFrame();

		// Verify user switches to new tab after selecting event link
		log.info("INFO: Verify user switches to new tab after selecting event link");
		logger.strongStep("Verify user switches to new tab after selecting event link");
		Assert.assertTrue(ui.fluentWaitNumberOfWindowsEqual(2));

		// Verify that it brings you to the community-Events widget page
		log.info("INFO: Verify that it brings you to the community-Events widget page");
		logger.strongStep("Verify that it brings you to the community-Events widget page");
		String expectedPageTitle = "Events - " + baseCommunity.getName();
		ui.switchToNextOpenBrowserWindowByHandle(driver.getWindowHandle());
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(CommunitiesUIConstants.tabNavCommunityName);
		Assert.assertEquals(driver.getTitle(), expectedPageTitle,"Error: User is not navigated to new browser tab after clicking link");

		log.info("INFO: Delete the community");
		logger.weakStep("Delete the community");
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
    /**
	*<ul>
	*<li><B>Info: Verify that external user is not able to see the Calendar event under 'Events' widget on home page if he is not following the community he is member of</B></li>
	*<li><B>Step: [API] Internal orgA user creates a restricted community with 'Events' widget added and external user as a member </B></li>s
	*<li><B>Step: Internal orgA user navigate to created community view</B></li>
	*<li><B>Step: Internal user select 'Event' widget from community view</B></li>
	*<li><B>Step: Internal user select 'Create an event' button from event widget</B></li>
	*<li><B>Step: Internal user enters event details like title, tag and creates 'All day' events</B></li>
	*<li><B>Step: Internal user logs out from connection</B></li>
	*<li><B>Step: External User log in and navigate to the community created in above step</B></li>
	*<li><B>Step: External User selects 'Events' widget from community view</B></li>
	*<li><B>Step: Check if user  can see the all day event created by internal user</B></li>
	*<li><B>Step: Navigate to Homepage view</B></li>
	*<li><B>Verify: Verify that the all day community event is NOT displayed under 'Events' widget in right panel</B></li>
	*<li><B>Verify: Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget</B></li>
	*</ul>
	* @throws Exception 
	*/
	@Test(groups = { "mtlevel3" })
	public void visitor_Event_NotFollowedCommunityEvent() throws Exception {

		String testName = ui.startTest();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Internal orgA user will now create a restricted community with external user added as a member
		log.info("INFO: Internal orgA user creates a restricted community with Activity widget and external user as a member added");
		logger.strongStep("Internal orgA user creates a restricted community with Activity widget and external user as a member added");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genDateBasedRandVal());
		Community restrictedCommunity = baseCommunity.createAPI(communitiesAPIUser1);
		baseCommunity.addWidgetAPI(restrictedCommunity, communitiesAPIUser1, BaseWidget.EVENTS);
		communitiesAPIUser1.addMemberToCommunity(testExternalUser, restrictedCommunity, Role.MEMBER);

		// External user follow community created above
		log.info("INFO: " + testExternalUser.getDisplayName() + " follow the Community using API");
		logger.strongStep(testExternalUser.getDisplayName() + " follow the Community using API");
		communitiesAPIUser2.followCommunity(restrictedCommunity);

		// Internal orgA user navigates to created community view
		log.info("INFO: Internal orgA user navigates to created community view");
		logger.strongStep("Internal orgA user navigates to created community view");
		CommunityEvents.loginAndNavigateToCommunity(restrictedCommunity, baseCommunity, ui, comUI, testInternalUser,communitiesAPIUser1, false);

		// Internal user select 'Event' widget from community view
		log.info("INFO: Internal user select 'Event' widget from community view");
		logger.strongStep("Internal user select 'Event' widget from community view");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		// Internal user select 'Create an event' button from event widget
		log.info("INFO: Internal user select 'Create an event' button from event widget");
		logger.strongStep("Internal user select 'Create an event' button from event widget");
		calUI.clickLinkWait(CalendarUI.CreateEvent);
		
		BaseEvent normalEvent = new BaseEvent.Builder("all Day Event " + Helper.genDateBasedRand())
			                                            	.allDayEvent(true)
			                                            	.tags("tag_"+testName)
			                                            	.description("all day event description")
			                                            	.build();
		
		// Internal user enters event details like title, tag and creates 'All day' events
		log.info("INFO: Internal user enters event details like title, tag and creates 'All day' events");
		logger.strongStep("Internal user enters event details like title, tag and creates 'All day' events");
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(normalEvent.getName());
		driver.getSingleElement(CalendarUI.EventTag).clear();
		driver.getSingleElement(CalendarUI.EventTag).type(normalEvent.getTags());
		calUI.typeNativeInCkEditor(normalEvent.getDescription());
		ui.clickLinkWait(CalendarUI.EventSubmit);

		log.info("INFO: Event is created successfully");
		logger.strongStep("Event is created successfully");
		ui.fluentWaitPresent(calUI.getEventSelector(normalEvent));

		// Internal user logs out from connection
		log.info("INFO: Internal user logs out from connection");
		logger.strongStep(" Internal user logs out from connection");
		LoginEvents.gotoHomeAndLogout(ui);

		// External User log in and navigate to the community created in above step
		log.info("INFO: External User log in and navigate to the community created in above step");
		logger.strongStep("External User log in and navigate to the community created in above step");
		CommunityEvents.loginAndNavigateToCommunity(restrictedCommunity, baseCommunity, ui, comUI, testExternalUser,communitiesAPIUser1, true);

		// External User selects 'Events' widget from community view
		log.info("INFO: External User selects 'Events' widget from community view");
		logger.strongStep("External User selects 'Events' widget from community view");
		Community_LeftNav_Menu.EVENTS.select(comUI);

		// Check if user can see the all day event created by internal user
		log.info("INFO: Check if user can see the all day event created by internal user");
		logger.strongStep("Check if user can see the all day event created by internal user");
		Assert.assertTrue(ui.fluentWaitPresent(calUI.getEventSelector(normalEvent)));

		// Navigate to Homepage view
		log.info("INFO: Navigate to Homepage view");
		logger.strongStep("Navigate to Homepage view");
		ui.gotoHome();

		// Verify that 'Event' widget appears in the right side of page
		logger.strongStep("Verify that 'Event' widget appears in the right side of page");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.eventsWidget), "ERROR: 'Event' widget is not displayed");

		// Create elements to be verified
		String eventLinkCSSSelector = HomepageUIConstants.eventLinkInEventWidget.replace("REPLACETHIS", normalEvent.getName());
		String eventCSSSelector = HomepageUIConstants.eventInEventWidget.replace("REPLACETHIS", normalEvent.getName());
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));

		// Verify that the all day community event is NOT displayed under'Events' widget in right panel
		log.info("INFO:Verify that the all day community event is NOT displayed under 'Events' widget in right panel");
		logger.strongStep("Verify that the all day community event is NOT displayed under 'Events' widget in right panel");
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{eventLinkCSSSelector, eventCSSSelector}, null, false);

		// Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget
		log.info("INFO: Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget");
		logger.strongStep(" Verify that 'Add to personal calendar' link is displayed at the bottom of 'Events' widget");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.addToPersonalCalendar),"ERROR: 'Add to Personal calendar' was NOT displayed under Event widget");
		driver.switchToFrame().returnToTopFrame();

		// Delete the community
		log.info("INFO: Delete the community");
		logger.weakStep("Delete the community");
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
}
