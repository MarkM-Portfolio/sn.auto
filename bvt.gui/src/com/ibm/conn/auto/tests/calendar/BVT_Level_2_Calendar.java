package com.ibm.conn.auto.tests.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
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
import com.ibm.conn.auto.util.menu.Events_MoreActions_Menu;
import com.ibm.conn.auto.util.menu.Events_View_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Calendar extends SetUpMethods2 {
	
	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Calendar.class);
	
	private TestConfigCustom cfg;	
	private CommunitiesUI ui;
	private CalendarUI calUI;
	private HomepageUI homeUI;
	private User testUser, testUser1;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	DefectLogger logger = dlog.get(Thread.currentThread().getId());
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		homeUI = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();	
				
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	   
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		homeUI = HomepageUI.getGui(cfg.getProductName(), driver);
		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Follow and stop following a community</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Open the event and select option: Follow</li>
	 * <li><B>Verify: </B>Check the button text changes to Stop Following and status message display</li>
	 * <li><B>Step: </B>Click the Close button (X) on the status message</li>
	 * <li><B>Verify: </B>Check the status message dismisses</li> 
	 * @throws Exception
	 * </ul>
	 */
	@Test(groups = {"level2", "bvt", "regressioncloud"})
	public void followCommunityEvent() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create an event base state object
		BaseEvent event = new BaseEvent.Builder(testName + " event" + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		
		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);		
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the blogs link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
	
		//Create an Event
		logger.strongStep("Create an Event");
		log.info("INFO: Create a new event");
		event.create(calUI);

		//Open the Event
		logger.strongStep("Open the Event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		//Verify user follows the event
		logger.strongStep("Select Follow button");
		log.info("INFO: Select Follow button");
		ui.clickLinkWait(CalendarUI.EventFollow);
		logger.weakStep("Verify button text changes to Stop Following");
		log.info("INFO: Verify button text changes to Stop Following");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventStopFollowOption), "ERROR: Stop Following button not found");
		logger.weakStep("Verify Following status message displays");
		log.info("INFO: Verify Following status message displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().followmsg), "ERROR: Follow message not found");
		logger.weakStep("Verify iCal Feed link displays");
		log.info("INFO: Verify iCal Feed link displays");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.EventSubscribeToEvent), "ERROR: iCal Feed link not found");
	
		
		//Verify user can dismiss the status message
		logger.strongStep("Click Close (X) button in the status message");
		log.info("INFO: Click Close (X) button in the status message");
		ui.clickLinkWait(CalendarUI.MsgBoxcloseButton);
		logger.weakStep("Verify the status message dismisses");
		log.info("INFO: Verify the status message dismisses");		
		Assert.assertFalse(driver.isElementPresent(CalendarUI.SuccessMsgBox),
				"ERROR: status message still displays");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Make an event and check that you aren't attending</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Open the event widget</li>
	 * <li><B>Verify: </B>The current user displays in attending list</li>
	 * <li><B>Step: </B>Select option: Will not Attend</li>
	 * <li><B>Verify: </B>Check the button text changes to Will Attend</li>
	 * <li><B>Verify: </B>Check status message displays and the user is removed from attending list</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void willNotAttendCommunityEvent() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create an event base state object
		BaseEvent event = new BaseEvent.Builder(testName + " event" + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();
		
		//create community
		logger.strongStep("create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);		

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
				
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
	
		//Create an Event
		logger.strongStep("Create an Event");
		log.info("INFO: Create a new event");
		event.create(calUI);

		//Open the Event
		logger.strongStep("Open the Event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		//Verify user is listed to attend the event
		logger.weakStep("Verify user is listed to attend the event and displays in people attending section");
		log.info("INFO: Verify " + testUser.getDisplayName() + " displays in people attending section" );
		Assert.assertTrue(driver.isElementPresent(calUI.AttendeeList(testUser.getDisplayName())),
				"ERROR: " + testUser.getDisplayName() + " doesn't display in attendee list");
		
		//Verify user will not attend
		logger.strongStep("Select the 'will not attend' button");
		log.info("INFO: Select Will Not Attend button");
		ui.clickLinkWait(CalendarUI.WillNotAttend);
		logger.weakStep("Verify button text changes to 'Will Attend'");
		log.info("INFO: Verify button text changes to Will Attend");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.WillAttend), "ERROR: Will Attend button not found");

		//Verify status message
		logger.weakStep("Verify that 'stopped attending' status message displays");
		String actualScopeText = driver.getSingleElement(CalendarUI.SuccessMsgBox).getText();
		log.info("INFO: Verify Stopped Attending status message displays");
		Assert.assertEquals(actualScopeText,Data.getData().stopAttendMsg, "ERROR: Stopped Attending message not found");
		
		//Verify user is not listed to attend the event
		logger.weakStep("Verify user is not listed to attend the event and doesn't display in people attending section");
		log.info("INFO: Verify " + testUser.getDisplayName() + " doesn't display in people attending section" );
		Assert.assertFalse(driver.isElementPresent(calUI.AttendeeList(testUser.getDisplayName())),
				"ERROR: " + testUser.getDisplayName() + " displays in attendee list");	
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Comment on a calendar event</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Create an event</li>
	 * <li><B>Step: </B>Open the event and add a comment</li>
	 * <li><B>Verify: </B>Check the comment is successfully added</li>
	 * <li><B>Step: </B>Add another comment</li>
	 * <li><B>Step: </B>Select Delete for the comment and confirm deletion in dialog</li>
	 * <li><B>Verify: </B>Check the comment is successfully deleted</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"cplevel2", "level2", "bvt", "regressioncloud"})
	public void addAndDeleteComments() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent eventA = new BaseEvent.Builder(testName + " A" + Helper.genDateBasedRand())
									    .tags(Data.getData().commonTag)
									    .description(Data.getData().commonDescription)
									    .build();

		String comment1 = "This is a comment" + 1;
		String comment2 = "This is a comment" + 2;
		String comment2uuid;

		
		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);		
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav and verify that the events tab is present
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
			ui.fluentWaitPresent(CalendarUI.EventTabFocus);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
			ui.waitForPageLoaded(driver);
			ui.fluentWaitPresentWithRefresh(CalendarUI.EventTabFocus);
		}
		
		//Create an Event
		logger.strongStep("Create an Event A");
		log.info("INFO: Create a new event");
		eventA.create(calUI);

		//Open the Event
		logger.strongStep("Open the Event A");
		log.info("INFO: Open event " + eventA.getName());
		ui.clickLinkWait(calUI.getEventSelector(eventA));
		
		//Get the id of the EventWidget
		logger.strongStep("Get the id of the EventWidget");
		logger.weakStep("Verify that id of EventWidget is gotten");
		List<Element> eventPanel = driver.getElements(CalendarUI.fullPageZone);
		String eventWidgetId = eventPanel.get(0).getAttribute("id");
		
		//Add a comment to event A
		logger.strongStep("Add a comment to event A");
		log.info("INFO: Select Add a comment... button");
		driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		ui.clickLinkWait(CalendarUI.AddAComment);
		log.info("INFO: Adding a new comment");
		calUI.addCommentToEvent(comment1);
		
		
		//Verify the comment displays
		logger.weakStep("Verify the comment displays");
		log.info("INFO: Verify comment displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment1), "ERROR: Comment doesn't display");
		//Get the count on the comment list
		int commentCount = calUI.getCommentList("css=#"+ eventWidgetId +"_commentList>ul>li").size();
		Assert.assertEquals(commentCount,1, "ERROR: Comment was not found");

		//Add another comment to event A
		logger.strongStep("Add another comment to event A");
		log.info("INFO: Select Add a comment... button");
		driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		ui.clickLinkWait(CalendarUI.AddAComment);
		log.info("INFO: Adding a new comment");
		calUI.addCommentToEvent(comment2);
		
		
		//Verify comment displays
		logger.weakStep("Verify comment displays");
		log.info("INFO: Verify comment displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(comment2), "ERROR: Comment doesn't display");
		
		//Get the UUID of the second comment
		comment2uuid = calUI.getCommentList("css=#"+ eventWidgetId +"_commentList>ul>li").get(1).getAttribute("uuid");
		//Delete the second comment
		logger.strongStep("Select 'delete' button for the second comment");
		log.info("INFO: Select Delete button for second comment");
		ui.clickLinkWait("css=a[uuid="+ comment2uuid +"]:contains(Delete)");
		logger.strongStep("Confirm the deletion comformation dialog of the comment");
		log.info("INFO: Confirm the deletion of the comment");
		ui.clickLinkWait(CalendarUI.ConfirmDialogDeleteButton);
		
		//Verify comment is deleted
		logger.weakStep("Verify that the second comment was deleted");
		log.info("INFO: Verify second comment was deleted");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(comment2), "ERROR: Comment still displays");
		//Get the count on the comment list
		logger.weakStep("Verify the comment count was decremented");
		commentCount = calUI.getCommentList("css=#"+ eventWidgetId +"_commentList>ul>li").size();
		Assert.assertEquals(commentCount,1, "ERROR: Comment was not deleted");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a repeating event</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Create a repeating event</li>
	 * <li><B>Step: </B>Open and edit today's instance</li>
	 * <li><B>Step: </B>Change time in the instance to 12:00AM - 11:45PM</li>
	 * <li><B>Verify: </B>Check time is updated for the single instance</li>
	 * <li><B>Step: </B>Delete the changed instance only</li>
	 * <li><B>Verify: </B>Check the instance was deleted from the repeating series</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void editRepeatingEventInstance() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + " A" +  Helper.genDateBasedRand())
									   .tags("BVTEvent")
									   .repeat(true)
									   .description(Data.getData().commonDescription)
									   .build();
	
		//Create community
		log.info("INFO: Create community using API");
		logger.strongStep("Create community");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}

		//Create a repeating event
		logger.strongStep("Create a repeating event");
		ui.fluentWaitPresent(CalendarUI.CreateEvent);
		log.info("INFO: Create a repeating event for 8 consective days");
		event.create(calUI);
		
		//Edit today's event and change time for a single instance

		List<Element> repeatingEventList = driver.getElements(calUI.getEventSelector(event));
		int repeatingEventListSize = repeatingEventList.size();

		log.info("INFO: Edit today's instance of the repeating event");
		logger.strongStep("Edit today's instance of the repeating event");
		repeatingEventList.get(0).click();
		ui.clickLinkWait(CalendarUI.Edit);
		
		//Set today's start time
		logger.strongStep("Set today's Event start time");
		ui.fluentWaitPresent(CalendarUI.EventEditorStartTime);
		driver.getSingleElement(CalendarUI.EventEditorStartTime).click();
		
		logger.strongStep("Select start time from Drop down list");
		List<Element> startTimeDropdownList = driver.getElements(CalendarUI.startTimeDropdownList);
		String newStartTime = startTimeDropdownList.get(0).getText();
		log.info("INFO: Select start time " + startTimeDropdownList.get(0).getText());
		startTimeDropdownList.get(0).click();
		
		//Set today's end time
		logger.strongStep("Set today's Event end time");
		ui.clickLinkWait(CalendarUI.EventEditorEndTime);
		List<Element> endTimeDropdownList = driver.getElements(CalendarUI.endTimeDropdownList);
		String newEndTime = endTimeDropdownList.get(endTimeDropdownList.size()-1).getText();
		log.info("INFO: Select end time " + newEndTime);
		endTimeDropdownList.get(endTimeDropdownList.size()-1).click();
		
		//Save the change
		logger.strongStep("Save the changes to the event");
		log.info("INFO: Save the changes to the event");
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		//Verify new time appear
		logger.weakStep("Verify that event time updated");
		String expectedText = "Time: "+ newStartTime +" - "+ newEndTime;
		log.info("INFO: Verify event time is updated");
		ui.fluentWaitTextPresent(driver.getSingleElement(CalendarUI.EventTime).getText());
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventTime).getText(),expectedText, "ERROR: The time was not updated.");
		
		//Delete the changed event
		logger.strongStep("Delete the repeating event");
		log.info("INFO: Delete today's instance of the repeating event");
		Events_MoreActions_Menu.DELETE.select(calUI);
        
		logger.strongStep("Confirm that the deletion conformation has appeared");
		log.info("INFO: Confirm that the deletion conformation has appeared");
		ui.clickLinkWait(CalendarUI.ConfirmDialogDeleteInstance);
		ui.clickLinkWait(CalendarUI.ConfirmDialogDeleteButton);
		
		//Verify the changed event is deleted successfully
		logger.weakStep("Verify that today's instance was deleted from the repeating series");
		List<Element> repeatingEventList1 = driver.getElements(calUI.getEventSelector(event));
		int repeatingEventListSize1 = repeatingEventList1.size();
		log.info("INFO: Verify today's instance was deleted from the repeating series");
		Assert.assertTrue(repeatingEventListSize == repeatingEventListSize1 + 1, "ERROR: Today's instance still exists.");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Repeat a single event</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Create a simple event</li>
	 * <li><B>Step: </B>Open and edit the event</li>
	 * <li><B>Step: </B>Add a Location and select Repeats button while keeping the default options</li>
	 * <li><B>Verify: </B>Check the Location is updated</li>
	 * <li><B>Verify: </B>Check the single event is now a repeating event</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "regressioncloud"})
	public void editSingleEvent() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		
	    String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();
		
		String expectedText = "Location:" + Data.getData().EventLocation;
		
		
		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);


		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}

		//Create an event
		logger.strongStep("Create an event");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		log.info("INFO: Create a new event");
		event.create(calUI);
	
		//Open the Event
		logger.strongStep("Open the Event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		//Edit all event field and update the single event to repeating event
		logger.strongStep("Select the Edit button");
		log.info("INFO: Select Edit button");
		ui.clickLinkWait(CalendarUI.Edit);
		logger.strongStep("Update Location field");
		log.info("INFO: Update Location field");
		ui.clickLinkWait(CalendarUI.EventLocation);
		ui.clearText(CalendarUI.EventLocation);
		driver.getSingleElement(CalendarUI.EventLocation).type(Data.getData().EventLocation);
		logger.strongStep("Select the Repeats button");
		log.info("INFO: Select Repeats button");
		ui.clickLinkWait(CalendarUI.EventRepeats);
		logger.strongStep("Keep Repeat default options and save the event");
		log.info("INFO: Keep Repeat default options and save the event");
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		//Verify updated location for the event
		logger.weakStep("Verify that the Location is updated");
		log.info("INFO: Verify Location is updated");
		ui.fluentWaitTextPresent(driver.getSingleElement(CalendarUI.locationText).getText());
		Assert.assertEquals(driver.getSingleElement(CalendarUI.locationText).getText(),expectedText, 
							"ERROR: Event location was not updated");
		
		//Verify single event changed to repeating event
		log.info("INFO: Select 'Back to community events' link");
		logger.strongStep("Select Back to community events link");
		ui.clickLinkWait(CalendarUI.BackToCommunityEventsLink);
		logger.weakStep("Verify single event is now a repeating event");
		log.info("INFO: Verify single event is now a repeating event");
		List<Element> repeatingEventList = driver.getElements(calUI.getEventSelector(event));
		Assert.assertTrue(repeatingEventList.size() > 1, 
						  "ERROR: The event didn't changed to a repeating event. The size is: " + repeatingEventList.size());
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Create a day long event on 2-Days view</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Change to 2-Days view and create an all day event</li>
	 * <li><B>Verify: </B>Calendar view remains on 2-Days view and all day event displays</li>
	 * <li><B>Step: </B>Select and edit the all day event from the 2-Days view</li>
	 * <li><B>Step: </B>Change the date to the next day</li>
	 * <li><B>Verify: </B>The all day event still displays on the 2-Days calendar view</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"cplevel2", "level2", "regressioncloud"})
	public void editAllDayEvent() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		//Create a event base state object
		BaseEvent eventA = new BaseEvent.Builder(testName + " A" + Helper.genDateBasedRand())
									   .tags("BVTEvent")
									   .allDayEvent(true)
									   .description(Data.getData().commonDescription)
									   .build();

		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);


		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);		
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
		
		//Change to 2-Days view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		logger.strongStep("Change to 2-Days view");
		log.info("INFO: Select Calendar View tab");
		ui.clickLinkWait(CalendarUI.EventGridTab);
		ui.clickLinkWait(CalendarUI.viewPickerImg);
		logger.strongStep("Switch view to display 2-Days calendar");
		log.info("INFO: Switch view to display 2-Days calendar");
		driver.getSingleElement(CalendarUI.CalendarView_TwoDay).click();
		
		//Create an all day event and verify calendar view not change
		logger.strongStep("Create an all day event and verify calendar view not change");
		String initialNavigatorText = driver.getSingleElement(CalendarUI.initialNavigatorText).getText();
		log.info("INFO: Create an all day event");
		eventA.create(calUI);

		ui.fluentWaitTextPresent(driver.getSingleElement(CalendarUI.initialNavigatorText).getText());
		String navigatorText = driver.getSingleElement(CalendarUI.initialNavigatorText).getText();
		logger.weakStep("Verify 2-Days calendar view displays after the event is created");
		log.info("INFO: Verify 2-Days calendar view displays after the event is created");
		Assert.assertEquals(initialNavigatorText,navigatorText, "ERROR: 2-Days calendar view not found.");
		logger.weakStep("Verify all day event displays on 2-Days calendar view");
		log.info("INFO: Verify all day event displays on 2-Days calendar view");
		Assert.assertTrue(driver.getElements("css=div.s-cv-entry-innerframe:contains(" + eventA.getName() + ")").size()>0,
				eventA.getName() + "ERROR: all day event not found on 2-Days calendar view");
		
		//Change the date to next day for the all day event and verify the event is still visible on 2-day calendar
		logger.strongStep("Select the event from the calendar view");
		log.info("Select the event from the calendar view");
		driver.getElements("css=div.s-cv-entry-innerframe:contains("+ eventA.getName() +")").get(0).click();
		logger.strongStep("Select the Edit Event button");
		log.info("Select Edit Event button");
		ui.clickLinkWait(CalendarUI.EditEventLink);		
		String todayDate = driver.getSingleElement(CalendarUI.EventStartDate).getAttribute("value");

		String nextDate = null;
		SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy");
		Date d = fmt.parse(todayDate);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 1);
		d = c.getTime();
		nextDate = fmt.format(d);
		
		log.info("Change event date to the next day");
		logger.strongStep("Change event date to the next day");
		driver.getSingleElement(CalendarUI.EventStartDate).clear();
		driver.getSingleElement(CalendarUI.EventStartDate).type(nextDate);
		
		//Save the change
		logger.strongStep("Save the changes to the event");
		log.info("INFO: Save the changes to the event");
		ui.clickLinkWait(CalendarUI.EventSubmit);
				
		//Workaround for intermittent eventsubmit - Defect #171648
		if (driver.getSingleElement(CalendarUI.EventSubmit).isVisible()){
			log.warn("WARNING: The Save button is still visible so attempting to save again");
			ui.clickLinkWait(CalendarUI.EventSubmit);
			}
		ui.fluentWaitTextPresent(eventA.getName());
		logger.weakStep("Verify all day event still displays on 2-Days calendar view");
		log.info("INFO: Verify all day event still displays on 2-Days calendar view");
		Assert.assertTrue(driver.getElements("css=div.s-cv-entry-innerframe:contains(" + eventA.getName() + ")").size()>0,
				eventA.getName() + "ERROR: all day event not found on 2-Days calendar view");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Send a notification to a member</li>
	 * <li><B>Step: </B>Create a community and add a member</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Create an all-day event</li>
	 * <li><B>Step: </B>Open the event and select option: More Actions > Notify Other People</li>
	 * <li><B>Verify: </B>The list only shows the member</li>
	 * <li><B>Step: </B>Send notification to the member</li>
	 * <li><B>Verify: </B>A status message indicating the notification was sent</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "regressioncloud"})
	public void notifyOtherPeople() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = calUI.startTest();
		
		User comMem = cfg.getUserAllocator().getUser();
		String sDisplayName2 = comMem.getDisplayName();	
		String sname = "";
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .description("Test Calendar for " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, comMem))
												   .build();
		
		//Create an event base state object	
		BaseEvent event = new BaseEvent.Builder("AllDayEvent" + Helper.genDateBasedRand())
									   .allDayEvent(true)
									   .build();
		
		
		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);

		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
		
		//Create event
		logger.strongStep("Create event");
		log.info("INFO: Create a new event");
		event.create(calUI);
		
		//Verify More Actions button -> Notify Other People
		logger.strongStep("Open the event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		log.info("INFO: Launch Notify Other People dialog");
		logger.strongStep("Launch 'Notify Other People' dialog");
		Events_MoreActions_Menu.NOTIFY_OTHER_PEOPLE.select(calUI);
		
		log.info("INFO: Verify Other People dialog displays");
		logger.weakStep("Verify 'Other People' dialog displays");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.NotifyOtherPeopleDialog),
				"ERROR: Notify Other People dialog doesn't display");
		
		//Expecting the list to have one member
		logger.strongStep("Expecting the list to have one member");
		log.info("INFO: Collect the list of user names from the dialog");
		driver.getSingleElement(CalendarUI.NotifyOtherPeopleMsg).type("/n" + community.getName() + "/n" + event.getName());
		
		log.info("INFO: Verify people list is not empty");
		logger.weakStep("Verify people list is not empty");
		Assert.assertEquals(driver.getElements(CalendarUI.NotifyOtherPeopleList).size(),1,
				"ERROR: Notify people list is empty");
		
		log.info("INFO: Compare the member's display name to the list");
		logger.weakStep("Compare the member's display name to the list");
		sname = driver.getElements(CalendarUI.NotifyOtherPeopleList + " label").get(0).getText();
		logger.weakStep("Verify that member displays in the notify people list");
		log.info("INFO: Verify member " + sDisplayName2 + " displays in the notify people list");
		Assert.assertEquals(sname, sDisplayName2, "ERROR: the display name is not the same in notify people list");
	
		//Selecting the checkbox next to members name
		logger.strongStep("Select the checkbox to add memebers to the notify list");
		log.info("INFO: Select checkbox to add member to the notify list");
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleList + " input");
		
		log.info("INFO: Select Notify button");
		logger.strongStep("Select the Notify button");
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleOKBtn);
		calUI.fluentWaitPresent(CalendarUI.SuccessMsgBox);
		logger.weakStep("Verify status message displays indicating that the notification was sent");
		log.info("INFO: Verify status message displays indicating the notification was sent");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.SuccessMsgBox),
				"ERROR: status message doesn't display");
		
		apiOwner.deleteCommunity(comAPI);
			
		calUI.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>View Personal Calendar</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the events (calendar) widget via API</li>
	 * <li><B>Step: </B>Click the view feed link titled Add to Personal Calendar</li>
	 * <li><B>Verify: </B>Verify new tab is opened, then Close the new tab and move back to parent window</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void addtoPersonalCalendarLinkinComm() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();

		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
			
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
		
		//Click view feed titled Add to Personal Calendar
		ui.fluentWaitPresent(CalendarUI.CreateEvent);
		logger.strongStep("Select view feed titled 'Add to Personal Calendar to launch a dialog'");
		log.info("INFO: Select view feed titled Add to Personal Calendar to launch a dialog");
		ui.clickLinkWait(CalendarUI.addToPersonalCalendar);
		
		// Verify new tab is opened
		log.info("INFO: Verify new tab is opened, then Close the new tab and move back to parent window");
		logger.strongStep("Verify new tab is opened, then Close the new tab and move back to parent window");
		Assert.assertTrue(ui.fluentWaitNumberOfWindowsEqual(2));
		ui.closeNewTabAndMoveToParentTab();
		
		apiOwner.deleteCommunity(comAPI);
	
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Add to personal Calendar from Homepage</li>
	 * <li><B>Step: </B>Go to the Homepage application</li>
	 * <li><B>Step: </B>Click Updates from the left navigation</li>
	 * <li><B>Step: </B>In the Events widget, click feed link titled Add to Personal Calendar</li>
	 * <li><B>Verify: </B>Add to Personal Calendar dialog launches</li>
	 * <li><B>Verify: </B>Check the subscription URL displays in the dialog</li>
	 * <li><B>Verify: </B>Add to Personal Calendar dialog dismisses</li>
	 * </ul>
	 * @throws Exception
	 * <em>Note * Cannot run successfully on Chrome & IE</em>
	 */
	
	@Test(groups = {"level2", "bvt", "regressioncloud"})
	public void addtoPersonalCalendarLinkinHomepage() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();

		//Load component and login (load classic Homepage direct link to avoid OrientMe if set as default)
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser);

		//Click on the Updates link
		logger.strongStep("Select 'Updates' on Homepage left navigation");
		log.info("Select Updates on Homepage left navigation");
		homeUI.gotoImFollowing();
	
		//This is only for the security deployments.
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
		{
			ui.waitForPageLoaded(driver);
			ui.clickLink(HomepageUIConstants.homepage);
		}
		
		//Wait for event widget to display then switch to the Event Widget Frame
		logger.strongStep("Wait for event widget to display then switch to the 'Event Widget' Frame");
		ui.fluentWaitPresent(HomepageUIConstants.eventsWidget);
		ui.clickLinkWait(HomepageUIConstants.eventsWidget);
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));
		
		//Click Add to Personal Calendar
		logger.strongStep("Select feed link to 'Add to Personal' Calendar");
		log.info("INFO: Select feed link Add to personal Calendar");
		
		ui.clickLinkWait(CalendarUI.addToPersonalCalendar);		
		// Close the New Tab and move back to Parent Tab
		log.info("INFO: Verify New Tab is opened and getting closed");
		Assert.assertTrue(ui.fluentWaitNumberOfWindowsEqual(2));
		ui.closeNewTabAndMoveToParentTab();
		
		// After the new UI change in Calendar, following code is not required to execute.
		// Switch back to main frame
		/*logger.weakStep("Verify that 'Add to personal' calendar dialog launches");
		driver.switchToFrame().returnToTopFrame();
		log.info("INFO: Verify Add to Personal Calendar Dialog launches");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.AddtoPersonalCalDia),
				"ERROR: Add to Personal Calendar dialog not found");
		
		//Go into the new iframe to trigger the loading operation
		logger.weakStep("Verify that subscription URL dialog displays in the dialog");
		log.info("INFO: Verify subscription URL displays in the dialog");
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUI.AddtoPersonalCalendarFrame));
		Assert.assertTrue(driver.isElementPresent(calUI.EventSubscribeWebcalLink()),
				"ERROR: Event subscribe link not found in dialog");		
		
		log.info("INFO: Close 'Add to Personal Calendar' Dialog");
		logger.strongStep("Close the 'Add to Personal Calendar' Dialog'"); 
		driver.getSingleElement(HomepageUI.AddtoPersonalCalOkButton).click();			

		//Switch back to main Frame
		logger.weakStep("Switch back to main Frame");
		driver.switchToFrame().returnToTopFrame();
		Assert.assertFalse(driver.isElementPresent(HomepageUI.AddtoPersonalCalendarFrame),
				"ERROR: Add to Personal Calendar dialog wasn't dismissed.");
		*/
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Display the calendar's different views</li>
	 * <li><B>Step: </B>Create a community</li>
	 * <li><B>Step: </B>Add the event (calendar) widget</li>
	 * <li><B>Step: </B>Switch to Calendar view</li>
	 * <li><B>Verify: </B>Check the month calendar view is the default display</li> 
	 * <li><B>Step: </B>Create an event and change to 1-Day view</li>
	 * <li><B>Verify: </B>Check the event displays in the 1-day view calendar</li>
	 * <li><B>Step: </B>Edit the event and change to an all day event</li>
	 * <li><B>Verify: </B>Check the entry displays on the correct day and timeslot</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "smoke", "regressioncloud"})
	public void calendarViewOneDay() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = calUI.startTest();

		
		BaseCommunity community = new BaseCommunity.Builder( testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .description("Test " + testName)
												   .build();
		
		BaseEvent event = new BaseEvent.Builder("Normal Event )(*&^%$" + Helper.genDateBasedRand())
									   .build();
		
		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Add the events widget
		logger.strongStep("Add the events widget");
		log.info("INFO: Add Events widget to the Community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// Get current date using javascript then set the event to that date
		// We need to get it from the browser because the test runner machine might not be  
		// in the same timezone as the browser machine hence could be on different date.
		Calendar fixedDate = Calendar.getInstance();
		long year = (long)driver.executeScript("return new Date().getFullYear()");
		long month = (long)driver.executeScript("return new Date().getMonth()");
		long date = (long)driver.executeScript("return new Date().getDate()");

		log.info("INFO: Fixed Calendar date: " + fixedDate.getTime());
		fixedDate.set((int)year, (int)month, (int)date, 12, 0, 0);
		event.setStartDate(fixedDate);
		
		// Also start event at 7am to avoid scrolling since it always shows the day starting at 7am
		event.setStartTime("7:00 AM");
		event.setEndTime("8:00 AM");
				
		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities views");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);
		
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
				
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}			
		
		//Verify the calendar view defaults to Monthly
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		logger.strongStep("Select the Calendar View tab");
		log.info("INFO: Select Calendar View tab");
		ui.clickLinkWait(CalendarUI.EventGridTab);
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		logger.weakStep("Verify the calendar view defaults to 'Monthly'");
		log.info("INFO: Verify the calendar view default to Monthly");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.CalendarView).getText().equals("Month"),
				"ERROR: Month view not found");
		
		//Create an event and switch to 1-Day view
		logger.strongStep("Create an event");
		log.info("INFO: Create an event");
		event.create(calUI);
        
		logger.strongStep("Switch view to display 1-Day view");
		log.info("INFO: Switch view to display 1-Day view");
		Events_View_Menu.ONE_DAY.select(ui);
		logger.weakStep("Verify the calendar displays the 1-Day view");
		log.info("INFO: Verify the calendar displays the 1-Day view");
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarView_Timecloumn).size(), 1, 
				"ERROR: The 1-day view displays more than one column.");
		log.info("INFO: Verify the entry displays in the 1-day view");
		logger.weakStep("Verify the entry displays in the 1-day view");
		List<Element> matchingElements = driver.getElements((CalendarUI.CalendarViewEntry));
		Assert.assertTrue(matchingElements.size() > 0,
				"ERROR: the event entry not found in 1-day view.");
		
		//Edit the event and switch to an all day event
		logger.strongStep("Select the event from the calendar view");
		log.info("INFO: Select the event from the calendar view: " + event.getName());
		String eventSelector = "css=div.s-cv-entry-innerframe div.s-cv-entry-innerframe:contains("+ event.getName() +")";
		// see if the event is visible, don't want to scroll to the element yet because it might actually scroll 
		// the event to the top but it's partially shown.
		List<Element> eventElements = driver.getVisibleElements(eventSelector);
		if (eventElements.size() == 0) {
			// now try to scroll to it
			driver.executeScript("arguments[0].scrollIntoView(true);", 
					driver.getElements(eventSelector).get(0).getWebElement());
			eventElements = driver.getVisibleElements(eventSelector);
			Assert.assertTrue(eventElements.size() > 0,
					"ERROR: event is not visible");
		}
		ui.clickLinkWait(eventSelector);
		logger.strongStep("Click the Edit Event button");
		log.info("INFO: Click Edit Event button");
		ui.clickLinkWait(CalendarUI.EditEventLink);	
		logger.strongStep("Select the All Day event option");
		log.info("INFO: Select All Day event option");
		ui.clickLinkWait(CalendarUI.EventAllDay);
		
		//Get the value of Event Start Date
		logger.strongStep("Get the value of Event Start Date");
		Calendar cal1 = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMMM d, yyyy");
		cal1.setTime(sdf1.parse(driver.getSingleElement(CalendarUI.EventEditorStartDate).getAttribute("value")));
		
		logger.strongStep("Save the changes to the event");
		log.info("INFO: Save the changes to the event");
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		// LC 198160 Improve test case adaptation
		logger.strongStep("Scroll up to the top of the page");
		log.info("INFO: Scroll up to the top of the page to make link shown");
		driver.executeScript("scroll(0,0);");
		
		logger.weakStep("Verify all day event still displays");
		calUI.fluentWaitPresent(CalendarUI.CalendarViewAllDay);
		log.info("INFO: Verify all day event still displays");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.CalendarViewAllDay + CalendarUI.CalendarViewEntry.substring(1)),
				"ERROR: all day event not found");

		//Get the value of Calendar View Header
		logger.strongStep("Get the value of Calendar View Header");
		Calendar cal2 = Calendar.getInstance();
		SimpleDateFormat sdf2 = new SimpleDateFormat("E d", Locale.US);
		cal2.setTime(sdf2.parse(driver.getSingleElement(CalendarUI.CalendarViewheader).getText()));

		log.info("INFO: Verify calendar view date is correct");
		logger.weakStep("Verify calendar view date is correct");
		Assert.assertEquals(cal1.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH),
				"ERROR: The Calendar View header date does not match Event Start Date.");
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Navigate and verify the contents of the events link</li>
	 * <li><B>Step: </B>Create a community via API</li>
	 * <li><B>Step: </B>Add the event (calendar) widget from the palette via GUI</li>
	 * <li><B>Step: </B>Select the Events link in the left nav</li>
	 * <li><B>Verify: </B>Check the UI contains the Create an Event button</li>
	 * <li><B>Verify: </B>Check the UI contains the Events tab</li>
	 * <li><B>Verify: </B>Check the UI contains the Calendar View tab</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"level2", "bvt", "regressioncloud"})
	public void addEventWidgetFromPalette() throws Exception{
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Testing the following: " + testName)
									 			   .build();
		
		//create community
		logger.strongStep("create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);


		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW" ;
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned communities views");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToDefaultIamOwnerView(isCardView);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Customize community - Add the Events widget
		logger.strongStep("Customize community - Add the Events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.EVENTS);
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
		
		//Verify the calendar view
		logger.weakStep("Verify Events UI contains the create button");
		log.info("INFO: Verify Events UI contains the create button");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent),
				"ERROR: 'Create an Event' button not found.");
		log.info("INFO: Verify Events UI contains the events tab");
		logger.weakStep("Verify Events UI contains the events tab");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab),
				"ERROR: 'Events' tab not found: " + CalendarUI.EventListTab);
		log.info("INFO: Verify Events UI contains the calendar view tab");
		logger.weakStep("Verify Events UI contains the calendar view tab");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab),
				"ERROR: 'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Test that the events widget is functional and is present to a second user.</li>
	 *<li><B>Step:</B> Create a new private community, add the second user to it.</li>
	 *<li><B>Step:</B> Add the Events widget to the community.</li> 
	 *<li><B>Step:</B> Create a new event, and add the second user.</li>
	 *<li><B>Step:</B> Login as second user</li>
	 *<li><B>Verify:</B> Verify the community and event are present</li>
	 *<li><B>Verify:</B> Validate that the Events widget was added to the community</li>
	 *<li><B>Verify:</B> Validate new event is present</li>
	 *<li><B>Clean Up:</B> Delete the Community.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void calendarEventCreation() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder("MyEvent" + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		//create community
		logger.strongStep("Create community");
		String communityName = testName + Helper.genDateBasedRand();
		BaseCommunity community = new BaseCommunity.Builder(communityName)
									 .tags(Data.getData().commonTag)
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(Access.PUBLIC)
									 .addMember(new Member(CommunityRole.MEMBERS, testUser1))
									 .description("Test Widgets inside " + communityName)
									 .build();

		//GUI		
		//Load component and login
		logger.strongStep("Open calendar and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		
		//Create A Private Community
		logger.strongStep("Create A Private Community");
		log.info("INFO: Create a private community");
		community.createFromDropDown(ui);

		//Customize community - Add the Events widget
		logger.strongStep("Customize community - Add the Events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.EVENTS);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on the Events link in the tabbed navigation menu");
			log.info("INFO: Select Events in the tabbed navigation");
			Community_TabbedNav_Menu.EVENTS.select(calUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Click on the Events link in the left navigation menu");
			log.info("INFO: Select Events in the left navigation");
			Community_LeftNav_Menu.EVENTS.select(calUI);
		}
		
		//Create an Event
		logger.strongStep("Create an Event");
		log.info("INFO: Create a new event");
		event.create(calUI);

		//Log Out As The First User
		logger.strongStep("Log out of the first user: " +testUser.getDisplayName());
		log.info("INFO: Log out as the first user");
		ui.logout();

		//Login As A Second User
		logger.strongStep("Login as the second user: " +testUser1.getDisplayName());
		log.info("INFO: Log in as a second user");
		ui.login(testUser1);

		//Verify The Event Was Created
		logger.weakStep("Verify the Event was created");
		log.info("INFO: Verify that the event was created.");
		Assert.assertTrue(ui.fluentWaitElementVisible(CalendarUI.CreateEvent),
						"ERROR: The event was not created.");

		//delete community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		
		ui.logout();

		ui.endTest();				

	}
}