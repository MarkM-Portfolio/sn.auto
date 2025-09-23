package com.ibm.conn.auto.tests.calendar.regression;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
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
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Events_MoreActions_Menu;
import com.ibm.conn.auto.util.menu.Events_View_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_3_Calendar extends SetUpMethods2{

	private static final Logger log = LoggerFactory.getLogger(BVT_Level_3_Calendar.class);
	private TestConfigCustom cfg;	
	private CommunitiesUI ui;
	private CalendarUI calUI;
	private GlobalsearchUI searchui;
	private User testUser;
	private Calendar sdate;
	private Calendar edate;
	private Calendar repeatUntil;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private SimpleDateFormat Datafmt;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		searchui = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		//Load User
		testUser = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + testUser.getDisplayName());
				
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	   
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		searchui = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		Datafmt = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		
		sdate = Calendar.getInstance();
		sdate.add(Calendar.DATE, 1);
		edate = Calendar.getInstance();
		edate.add(Calendar.DATE, 1);
		repeatUntil = Calendar.getInstance();
		repeatUntil.add(Calendar.DATE, 10);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
	}
	

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the week and five day calendar views.
	 *<li><B>Step: </B>Create a normal event.
	 *<li><B>Step: </B>Check it in five and week view.
	 *<li><B>Verify: </B>Delete a entry and make sure it cannot be seen anymore.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testCalendarView_WeekandFiveDay() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		
		
		BaseCommunity community = new BaseCommunity.Builder( testName + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .description("Test " + testName)
												   .build();
		
		BaseEvent event = new BaseEvent.Builder("Normal Event )(*&^%$" + Helper.genDateBasedRand())
									   .build();
		
		//Create Community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		logger.strongStep("Verify the Create an Event button is visible then switch to the Calendar View tab");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);		
		
		ui.clickLinkWait(CalendarUI.EventGridTab);
		
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		Assert.assertTrue(driver.getSingleElement(CalendarUI.CalendarView).getText().equals("Month"), "ERROR: The default view by choice is not Month.");
		
		logger.strongStep("Create a normal event");
		event.create(calUI);
		
		logger.strongStep("Switching to Five Day view");
		log.info("INFO: Switching to Five Day view");
		Events_View_Menu.FIVE_DAYS.select(ui);
		
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarView_Timecloumn).size(), 5, "ERROR: The 5 days view doesn't have 5 column .");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.CalendarViewEntry), "ERROR: The calendar View doesn't have the created entry.");
		
		logger.strongStep("Switching to Week view");
		log.info("INFO: Switching to Week view");
		Events_View_Menu.WEEK.select(ui);
		
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarView_Timecloumn).size(), 7, "ERROR: The week view doesn't have 7 column.");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.CalendarViewEntry), "ERROR: The calendar View doesn't have the created entry.");
		
		logger.strongStep("Deleting an entry from the Calendar");
		log.info("INFO: Click on an entry and delete it");
		ui.clickLinkWithJavascript(CalendarUI.CalendarViewEntry);
		ui.clickLinkWait(CalendarUI.CalendarViewDelBtn);
		calUI.fluentWaitTextPresent(CalendarUI.DelConfirmMsg);
		driver.getSingleElement(CalendarUI.DelBtn).click();
		
		logger.strongStep("Validating the deletion of the Calendar Entry");
		log.info("INFO: Verify that the Calendar Entry is no longer there");
		Assert.assertFalse(driver.isElementPresent(CalendarUI.CalendarViewEntry), "ERROR: The calendar View still can see the deleted entry.");
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests various instances when a session times out for a private event.
	 *<li><B>Step: </B>Create a private community with access as Restricted.
	 *<li><B>Step: </B>Create a normal event and provide the start and end time and date.
	 *<li><B>Step: </B>Delete all cookies and click on Create Event button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Add details, then delete all cookies and click on Save button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Add a comment and click on Save button after deleting all cookies.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and click on Will Not Attend button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and click on Follow button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and delete an Event.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and select Notify Other People.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and click on the Notify button from the Notify Other People dialog box.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and click on Edit button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Verify: </B>User should be returned to the previously opened application after logging back in.
	 *</ul>
	 */
	@Test(groups = {"regression", "noforIE"})
	public void testSessionTimeOutforPrivateEvent() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		User testUser1 = cfg.getUserAllocator().getUser();
		
		
		
		BaseCommunity privatecommunity = new BaseCommunity.Builder( testName + Helper.genDateBasedRand())
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.description("Test " + testName)														
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.access(Access.RESTRICTED)
														.shareOutside(false)
														.build();
		
		
		BaseEvent event = new BaseEvent.Builder("Normal Event !@#$$%^" + Helper.genDateBasedRand())
									   .tags("session timeout")
									   .description("session timeout description")
									   .location("session timeout location")
									   .startDate(sdate)
									   .startTime("9:00 AM")
									   .endDate(edate)
									   .endTime("9:00 PM")
									   .repeatUntil(repeatUntil)
									   .build();
		
		
		//Create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = privatecommunity.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		privatecommunity.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + privatecommunity.getName() + " using API");
		privatecommunity.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		privatecommunity.navViaUUID(ui);	
		
		//Choose Events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		WebDriver wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		logger.strongStep("Verifying that clicking on Create Event button logs the user out");
		log.info("INFO: Click on Create Event button and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.CreateEvent);
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());
		driver.getSingleElement(CalendarUI.EventTag).clear();		
		driver.getSingleElement(CalendarUI.EventTag).type(event.getTags());
		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(event.getStartDateText());
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(event.getEndDateText());
		driver.getSingleElement(CalendarUI.EventEditorStartTime).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartTime).type(event.getStartTime());
		driver.getSingleElement(CalendarUI.EventEditorEndTime).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndTime).type(event.getEndTime());		
		driver.getSingleElement(CalendarUI.EventLocation).clear();		
		driver.getSingleElement(CalendarUI.EventLocation).type(event.getLocation());		
		calUI.typeNativeInCkEditor(event.getDescription());	
		
		ui.closeGuidedTourPopup();
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		logger.strongStep("Verifying that clicking on Save button logs the user out");
		log.info("INFO: Click on Save button and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventTitle).getAttribute("value"), event.getName()
				, "ERROR: Event title is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventTag).getAttribute("value"), event.getTags()
				, "ERROR: Event tag is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventLocation).getAttribute("value"), event.getLocation()
				, "ERROR: Event location is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorStartDate).getAttribute("value"),
				Datafmt.format(event.getStartDate().getTime())
				, "ERROR: Event start date is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorEndDate).getAttribute("value"), 
				Datafmt.format(event.getEndDate().getTime())
				, "ERROR: Event end date is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorStartTime).getAttribute("value"), event.getStartTime()
				, "ERROR: Event start time is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorEndTime).getAttribute("value"), event.getEndTime()
				, "ERROR: Event end time is changed after login back.");
		
		ui.clickLinkWait(CalendarUI.EventSubmit);
		ui.clickLinkWait(calUI.getEventSelector(event));
		calUI.fluentWaitPresent(CalendarUI.AddAComment);
		
		logger.strongStep("Add a comment and save after deleting all cookies");
		log.info("INFO: Delete all cookies then add a comment and save");
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		ui.fluentWaitElementVisible(CalendarUI.AddAComment);
		driver.getSingleElement(CalendarUI.AddAComment).click();
		driver.switchToActiveElement().type("New Comment");
		logger.strongStep("Verifying that clicking on Save button logs the user out");
		log.info("INFO: Click on Save button and validate that the session expires"); 
		driver.getSingleElement(CalendarUI.AddCommentSaveButton).click();
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		logger.strongStep("Verifying that clicking on Will Not Attend button logs the user out");
		log.info("INFO: Click on Will Not Attend button and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.WillNotAttend);		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: user goes to the event list page after login back.");
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		logger.strongStep("Verifying that clicking on Follow button logs the user out");
		log.info("INFO: Click on Follow button and validate that the session expires");
		ui.clickLinkWait(CalendarUI.Follow);
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: user goes to the event list page after login back.");
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		Events_MoreActions_Menu.DELETE.select(calUI);
		logger.strongStep("Verifying that deleting an Event logs the user out");
		log.info("INFO: Delete an Event and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.DelBtn);	
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: user goes to the event list page after login back.");
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		logger.strongStep("Verifying that selecting Notify Other People logs the user out");
		log.info("INFO: Select Notify Other People and validate that the session expires");
		Events_MoreActions_Menu.NOTIFY_OTHER_PEOPLE.select(calUI); //TODO:

		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		
		log.info("INFO: Launch Notify Other People dialog");
		Events_MoreActions_Menu.NOTIFY_OTHER_PEOPLE.select(calUI);

		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleListCheckbox);
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		logger.strongStep("Verifying that clicking on Notify Other People dialog box's Notify button logs the user out");
		log.info("INFO: Click on Notify button in Notify Other People dialog box and validate that the session expires");
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleOKBtn);	
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		logger.strongStep("Verifying that clicking on Edit button logs the user out");
		log.info("INFO: Click on Edit button and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.Edit);
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		ui.clickLinkWait(calUI.getEventSelector(event));
		ui.clickLinkWait(CalendarUI.Edit);	
		
		ui.closeGuidedTourPopup();
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		ui.clickLinkWait(CalendarUI.EventAllDay);
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		ui.clickLinkWait(CalendarUI.EventRepeats);
		
		Select typeselectBox = new Select((WebElement) driver.getSingleElement(CalendarUI.EventRepeatFreq).getBackingObject());
	    log.info("INFO: Verify that default repeating selection is Daily");
		Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals("Daily"), "ERROR: default repeating selection is not Daily.");
	    calUI.selectComboValue(CalendarUI.EventRepeatFreq, "Weekly");
	    
	    driver.getSingleElement(CalendarUI.EventEditorUntilDate).clear();
	    driver.getSingleElement(CalendarUI.EventEditorUntilDate).type(event.getRepeatUntilText());
	    ui.clickLinkWait(CalendarUI.EventEditorSunCheckBox_Unchecked);
	    
	    ui.clickLinkWait(CalendarUI.EventSubmit);
	    calUI.login(testUser);
	    
	    logger.strongStep("Verify that all day selection is selected after logging back in");
	    log.info("INFO: Login back and check for all day selection");
	    Assert.assertTrue(driver.getSingleElement(CalendarUI.EventAllDay).isSelected(), "ERROR: all day selection is not selected after login back.");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventRepeatCheckbox).isSelected(), "ERROR: repeats selection is not selected after login back.");
		typeselectBox = new Select((WebElement) driver.getSingleElement(CalendarUI.EventRepeatFreq).getBackingObject());
	    Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals("Weekly"), "ERROR: repeating frequency is not weekly after login back.");
	    Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorUntilDate).getAttribute("value"), 
	    		Datafmt.format(event.getRepeatUntil().getTime())
	    		, "ERROR: repeating until is incorrect after login back.");
	   
	   if(driver.getElements(CalendarUI.EventEditorSunCheckBox_Checked).get(0).isVisible())
		   Assert.assertTrue(driver.getElements(CalendarUI.EventEditorSunCheckBox_Checked).get(0).isSelected()
				   , "ERROR: sunday checkbox is not selected after login back.");
	   else
		   Assert.assertTrue(driver.getElements(CalendarUI.EventEditorSunCheckBox_Checked).get(1).isSelected()
				   , "ERROR: sunday checkbox is not selected after login back.");
	    	    
		ui.clickLinkWait(CalendarUI.EventSubmit);
		calUI.fluentWaitTextPresent(event.getName());
		
	    calUI.endTest();
	}
	

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the month calendar view.
	 *<li><B>Step: </B>Create a repeating event.
	 *<li><B>Step: </B>Switch to Calendar View for the event.
	 *<li><B>Verify: </B>The number of repeating entries should be correct for the month.
	 *<li><B>Step: </B>Edit the event on Saturday and change the Event Title.
	 *<li><B>Verify: </B>The text is reflected in the Calendar Entry for Saturday.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testCalendarView_Month() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();

		
		BaseCommunity community = new BaseCommunity.Builder( testName + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .description("Test " + testName)
												   .build();
		
		BaseEvent RepeatEvent = new BaseEvent.Builder("Repeating Event )(*&^%$" + Helper.genDateBasedRand())
											 .repeat(true)
											 .build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//choose Events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		ui.clickLinkWait(CalendarUI.EventGridTab);
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		logger.strongStep("Create a repeating event");
		RepeatEvent.create(calUI);
		
		int iNum = driver.getVisibleElements(CalendarUI.CalendarViewEntry).size();
		boolean sBool = false;
		if(iNum<8) {
			logger.strongStep("Click on the Next Month link and then the Previous Month link");
			ui.clickLinkWait(CalendarUI.CalendarViewNextMonth);
			iNum += driver.getElements(CalendarUI.CalendarViewEntry).size();
			ui.clickLinkWait(CalendarUI.CalendarViewPrevMonth);
			sBool = true;
		}
		Assert.assertEquals(iNum, 8,  "ERROR: repeating entries number is incorrect.");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EditEventLink).getAttribute("aria-disabled").equals("true")
				, "ERROR: edit link is enabled on calendar view when no entry selected");
		int i=0,j=0;
		if(sBool == false){
			
			for(i=0;i<driver.getVisibleElements(CalendarUI.CalendarViewEntry).size();i++){
				if(driver.getVisibleElements(CalendarUI.CalendarViewEntryGrid).get(i).getAttribute("aria-label").contains("Saturday")){
					logger.strongStep("Click on the Calendar View Entry for Saturday");
					log.info("INFO: Clicking on the Calendar View Entry for Saturday");
					driver.getVisibleElements(CalendarUI.CalendarViewEntry).get(i).click();
					break;
				}
			}
			
			Assert.assertTrue(i<driver.getVisibleElements(CalendarUI.CalendarViewEntry).size(), "ERROR: no entry created for Saturday.");
		}
		else{
			
			for(j=0;j<driver.getVisibleElements(CalendarUI.CalendarViewEntry).size();j++){
				if(driver.getVisibleElements(CalendarUI.CalendarViewEntryGrid).get(j).getAttribute("aria-label").contains("Saturday")){
					logger.strongStep("Click on the Calendar View Entry");
					log.info("INFO: Clicking on the Calendar View Entry");
					driver.getVisibleElements(CalendarUI.CalendarViewEntry).get(j).click();
					break;
				}				
			}
			if(j == driver.getVisibleElements(CalendarUI.CalendarViewEntry).size()){
				driver.getSingleElement(CalendarUI.CalendarViewNextMonth).click();
				for(j=0;j<driver.getVisibleElements(CalendarUI.CalendarViewEntry).size();j++){
					if(driver.getVisibleElements(CalendarUI.CalendarViewEntryGrid).get(j).getAttribute("aria-label").contains("Saturday")){
						logger.strongStep("Click on the Calendar View Entry for Saturdar");
						log.info("INFO: Clicking on the Calendar View Entry for Saturday");
						driver.getVisibleElements(CalendarUI.CalendarViewEntry).get(j).click();
						break;
					}				
				}
				Assert.assertTrue(j<driver.getVisibleElements(CalendarUI.CalendarViewEntry).size(), "ERROR: no entry created for Saturday.");
			}
		}
		
		logger.strongStep("Edit the Event Title and verify the text is reflected in the Calendar Entry for Saturday");
		log.info("INFO: Editing the Event Title and verifying the text for the Saturday Entry");
		ui.clickLinkWait(CalendarUI.EditEventLink);
		ui.clickLinkWait(CalendarUI.EditEntireSeries);
		driver.getSingleElement(CalendarUI.EventTitle).type("updated");	
		ui.clickLinkWait(CalendarUI.EventSubmit);
		Assert.assertTrue(driver.getVisibleElements(CalendarUI.CalendarViewEntry).get(j).getText().contains("updated")
				, "ERROR: event is not updated successfully.");
		
		calUI.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the two-day calendar view.
	 *<li><B>Step: </B>Create a normal event.
	 *<li><B>Step: </B>Switch to Calendar View for the event.
	 *<li><B>Step: </B>Switch to two day view.
	 *<li><B>Step: </B>Double click the Event Entry to open and edit.
	 *<li><B>Step: </B>Edit the Entry and change the start and end dates to the second day.
	 *<li><B>Verify: </B>The position of the updated event should be over the third of the Window width.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testCalendarView_TwoDay() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		
		BaseCommunity community = new BaseCommunity.Builder( testName + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .description("Test " + testName)
												   .build();

		BaseEvent event = new BaseEvent.Builder("Normal Event )(*&^%$" + Helper.genDateBasedRand())
									   .startDate(sdate)
									   .startTime("2:00 PM")
									   .endDate(edate)
									   .endTime("3:00 PM")
									   .build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
				
		ui.clickLinkWait(CalendarUI.EventGridTab);
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		Assert.assertTrue(driver.getSingleElement(CalendarUI.CalendarView).getText().equals("Month")
				, "ERROR: default calendar view by choice is not month.");
		
		logger.strongStep("Create a normal event");
		event.create(calUI);
						
		logger.strongStep("Switch to Two Day view");
		log.info("INFO: Switching to Two Day view");
		Events_View_Menu.TWO_DAYS.select(ui);
		
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarView_Timecloumn).size(), 2
				, "ERROR: two day view doesn't have 2 columns.");
		
		//Added the condition because the time of execution for Extended BVT is 09:00pm, but the system where the test case is run has already moved to the next day as it is in a different time zone
		driver.changeImplicitWaits(3);
		
		if (!driver.isElementPresent(CalendarUI.CalendarViewEntry)) {
			ui.clickLinkWait(CalendarUI.CalendarViewNextDay);
		}
		
		driver.turnOnImplicitWaits();
		
		Assert.assertTrue(driver.isElementPresent(CalendarUI.CalendarViewEntry), "ERROR: calendar grid view doesn't show up the entry");
		String date = driver.getSingleElement(CalendarUI.CalendarViewEntry).getAttribute("calendar_date");
		

		Date d = fmt.parse(date);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 1);
		d = c.getTime();
		date = fmt.format(d);
		date = date.substring(4,6) + "/" + date.substring(6) + "/" + date.substring(0,4);
		
		logger.strongStep("Edit the event and change its start and end dates to the second day");
		log.info("INFO: Editing the event and changing its start and end dates to the second day");
		driver.getElements("css=div.s-cv-entry-innerframe:contains("+ event.getName() +")").get(0).click();
		ui.clickLinkWait(CalendarUI.EditEventLink);

		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(date);
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(date);
		
		ui.clickLinkWait(CalendarUI.EventSubmit);
		//ui.clickLinkWait(CalendarUI.BackToCommunityEventsLink);
		calUI.fluentWaitPresent(CalendarUI.CalendarViewEntry);
		String sStyle = driver.getSingleElement(CalendarUI.CalendarViewEntry).getAttribute("style");	
		int i=0;
		String s="";
		log.info("INFO: Validate the position of the updated event.");
		if (testConfig.browserIs(BrowserType.IE)) s = "LEFT";
		else if (testConfig.browserIs(BrowserType.FIREFOX) || testConfig.browserIs(BrowserType.CHROME))  s = "left";
		
		if(sStyle.indexOf(";",sStyle.indexOf(s)+6)>0)
			i = Integer.parseInt(sStyle.substring(sStyle.indexOf(s)+6,sStyle.indexOf(";",sStyle.indexOf(s)+6)-2));
		else
			i = Integer.parseInt(sStyle.substring(sStyle.indexOf(s)+6, sStyle.length()-2));
		String sWindowWidth = driver.executeScript("return window.innerWidth").toString();
		int nWindowWidth = Integer.parseInt(sWindowWidth);
		Assert.assertTrue(i>(nWindowWidth / 3),  "ERROR: the position of the updated event is incorrect. Expected: over " +
								  (nWindowWidth / 3));		
		calUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests various instances when a session times out for a public event.
	 *<li><B>Step: </B>Create a public community with access as Default.
	 *<li><B>Step: </B>Create a normal event and provide the start and end time and date.
	 *<li><B>Step: </B>Delete all cookies and click on Create Event button.
	 *<li><B>Step: </B>Enter the details and click on Save button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all the cookies then add a comment and click on Save button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and click on Will Not Attend button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and click on Follow button.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and delete an Event.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Delete all cookies and select Notify Other People.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Step: </B>Click on the Notify button in the Notify Other People dialog box.
	 *<li><B>Verify: </B>The user is logged out.
	 *<li><B>Step: </B>Log back in.
	 *<li><B>Verify: </B>User should be returned to the previously opened application after logging back in.
	 *</ul>
	 */
	@Test(groups = {"regression", "notforIE"})
	public void testSessionTimeOutforPublicEvent() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();

		
		BaseEvent event = new BaseEvent.Builder("Normal Event !@#$$%^" + Helper.genDateBasedRand())
										.tags("session timeout")
										.description("session timeout description")
										.location("session timeout location")
										.startDate(sdate)
										.startTime("9:00 AM")
										.endDate(edate)
										.endTime("9:00 PM")
										.repeatUntil(repeatUntil)
		   								.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);				
	
		//choose Events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				

		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		ui.closeGuidedTourPopup();
		
		logger.strongStep("Delete all cookies and click on Create Event button");
		log.info("INFO: Click on Create Event button after deleting all cookies"); 
		WebDriver wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		ui.clickLinkWait(CalendarUI.CreateEvent);		
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());
		driver.getSingleElement(CalendarUI.EventTag).clear();		
		driver.getSingleElement(CalendarUI.EventTag).type(event.getTags());
		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(event.getStartDateText());
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(event.getEndDateText());
		driver.getSingleElement(CalendarUI.EventEditorStartTime).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartTime).type(event.getStartTime());
		driver.getSingleElement(CalendarUI.EventEditorEndTime).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndTime).type(event.getEndTime());		
		driver.getSingleElement(CalendarUI.EventLocation).clear();		
		driver.getSingleElement(CalendarUI.EventLocation).type(event.getLocation());		
		calUI.typeNativeInCkEditor(event.getDescription());	
		
		logger.strongStep("Verifying that clicking on Save button after providing all details for the Event logs the user out");
		log.info("INFO: Click on Save button and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");

		calUI.login(testUser);
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventTitle).getAttribute("value"), event.getName()
				, "ERROR: Event title is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventTag).getAttribute("value"), event.getTags()
				, "ERROR: Event tag is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventLocation).getAttribute("value"), event.getLocation()
				, "ERROR: Event location is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorStartDate).getAttribute("value"), 
				Datafmt.format(event.getStartDate().getTime())
				, "ERROR: Event start date is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorEndDate).getAttribute("value"), 
				Datafmt.format(event.getEndDate().getTime())
				, "ERROR: Event end date is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorStartTime).getAttribute("value"), event.getStartTime()
				, "ERROR: Event start time is changed after login back.");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorEndTime).getAttribute("value"), event.getEndTime()
				, "ERROR: Event end time is changed after login back.");
		
		ui.clickLinkWait(CalendarUI.EventSubmit);
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		logger.strongStep("Add a comment and save after deleting all cookies");
		log.info("INFO: Delete all cookies then add a comment and save");
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		ui.clickLinkWait(CalendarUI.AddAComment);
		driver.switchToActiveElement().type("New Comment");
		logger.strongStep("Verifying that clicking on Save button logs the user out");
		log.info("INFO: Click on Save button and validate that the session expires");
		driver.getSingleElement(CalendarUI.AddCommentSaveButton).click();
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		logger.strongStep("Verifying that clicking on Will Not Attend button logs the user out");
		log.info("INFO: Click on Will Not Attend button and validate that the session expires");
		driver.getSingleElement(CalendarUI.WillNotAttend).click();		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: user goes to the event list page after login back.");
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		logger.strongStep("Verifying that clicking on Follow button logs the user out");
		log.info("INFO: Click on Follow button and validate that the session expires");
		driver.getSingleElement(CalendarUI.Follow).click();		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: user goes to the event list page after login back.");
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		Events_MoreActions_Menu.DELETE.select(calUI);
		logger.strongStep("Verifying that deleting an Event logs the user out");
		log.info("INFO: Delete an Event and validate that the session expires"); 
		ui.clickLinkWait(CalendarUI.DelBtn);
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		calUI.fluentWaitTextPresent(event.getName());
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: user goes to the event list page after login back.");
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		log.info("INFO: Launch Notify Other People dialog");
		Events_MoreActions_Menu.NOTIFY_OTHER_PEOPLE.select(calUI);
		
/*		ui.clickLinkWait(CalendarUI.MoreActions);	
		ui.clickLinkWait(CalendarUI.MoreActionsNotifyOtherPeople);	*/
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleList + " input");
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleOKBtn);
		logger.strongStep("Verifying that clicking on Notify Other People dialog box's Notify button logs the user out");
		log.info("INFO: Click on Notify button in Notify Other People dialog box and validate that the session expires");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "ERROR: Login page doesn't show up.");
		calUI.login(testUser);
		ui.closeGuidedTourPopup();
		calUI.fluentWaitTextPresent(event.getName());
		
		wd = (WebDriver) driver.getBackingObject();
		wd.manage().deleteAllCookies();
		
		log.info("INFO: Edit the Event by clicking on the Edit button then saving it");
		log.info("INFO: Click on Edit button then edit the Event and save it");
		ui.clickLinkWait(CalendarUI.Edit);	
		ui.clickLinkWait(CalendarUI.EventAllDay);
		ui.clickLinkWait(CalendarUI.EventRepeats);
		
		Select typeselectBox = new Select((WebElement) driver.getSingleElement(CalendarUI.EventRepeatFreq).getBackingObject());
	    log.info("INFO: Verify that default repeating selection is Daily");
		Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals("Daily"), "ERROR: default repeating selection is not Daily.");
	    calUI.selectComboValue(CalendarUI.EventRepeatFreq, "Weekly");
	    
	    driver.getSingleElement(CalendarUI.EventEditorUntilDate).clear();
	    driver.getSingleElement(CalendarUI.EventEditorUntilDate).type(event.getRepeatUntilText());
	    driver.getSingleElement(CalendarUI.EventEditorSunCheckBox_Unchecked).click();
	    
	    ui.clickLinkWait(CalendarUI.EventSubmit);
	    calUI.login(testUser);
	    
	    logger.strongStep("Verify that all day selection is selected after logging back in");
	    log.info("INFO: Login back and check for all day selection");
	    Assert.assertTrue(driver.getSingleElement(CalendarUI.EventAllDay).isSelected()
	    		, "ERROR: all day selection is not selected after login back.");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventRepeatCheckbox).isSelected()
				, "ERROR: repeats selection is not selected after login back.");
		typeselectBox = new Select((WebElement) driver.getSingleElement(CalendarUI.EventRepeatFreq).getBackingObject());
	    Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals("Weekly")
	    		, "ERROR: repeating frequency is not weekly after login back.");
	    Assert.assertEquals(driver.getSingleElement(CalendarUI.EventEditorUntilDate).getAttribute("value"), 
	    		Datafmt.format(event.getRepeatUntil().getTime())
	    		, "ERROR: repeating until is incorrect after login back.");
	    
	   if(driver.getElements(CalendarUI.EventEditorSunCheckBox+"[aria-checked=true]").get(0).isVisible())
		   Assert.assertTrue(driver.getElements(CalendarUI.EventEditorSunCheckBox_Checked).get(0).isSelected()
				   , "ERROR: sunday checkbox is not selected after login back.");
	   else
		   Assert.assertTrue(driver.getElements(CalendarUI.EventEditorSunCheckBox_Checked).get(1).isSelected()
				   , "ERROR: sunday checkbox is not selected after login back.");
	    	    
		ui.clickLinkWait(CalendarUI.EventSubmit);
		calUI.fluentWaitTextPresent(event.getName());
		
	    calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can edit an event through the Homepage widget.
	 *<li><B>Step: </B>Create a normal event with tag, tagnormal.
	 *<li><B>Step: </B>Create an all day event with tag, tagallday.
	 *<li><B>Step: </B>Login as user2.
	 *<li><B>Step: </B>Create community2. 
	 *<li><B>Step: </B>Add repeating event.
	 *<li><B>Step: </B>Follow  normal and all day event.
	 *<li><B>Step: </B>Go to Homepage.
	 *<li><B>Verify: </B>Widget shows up all the events.
	 *<li><B>Step: </B>Cancel the edit operation. 
	 *<li><B>Step: </B>Change widget to show only 1 event.
	 *<li><B>Verify: </B>Make sure paging works correctly.
	 *<li><B>Step: </B>Edit widget.
	 *<li><B>Verify: </B>Only show attended event.
	 *<li><B>Step: </B>Check event.
	 *<li><B>Step: </B>Open edit widget.
	 *<li><B>Verify: </B>Make sure change works correctly.
	 *<li><B>Step: </B>Edit widget.
	 *<li><B>Verify: </B>Only show followed event.
	 *<li><B>Step: </B>Check event.
	 *<li><B>Step: </B>Follow repeating event.
	 *<li><B>Verify: </B>Widget shows up all the event.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testEditHomepageEventWidget() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		User comMember = cfg.getUserAllocator().getUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APICommunitiesHandler apiMember = new APICommunitiesHandler(serverURL,
				comMember.getAttribute(cfg.getLoginPreference()), comMember.getPassword());	
		
		BaseCommunity community1 = new BaseCommunity.Builder( testName + " Widget1 " + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + "1" + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .description("Test Calendar for " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, comMember))
												   .build();

		BaseCommunity community2 = new BaseCommunity.Builder(testName + " Widget2 " + Helper.genDateBasedRand())
		   											.commHandle(Data.getData().commonHandle + "2" + Helper.genDateBasedRand())
		   											.access(defaultAccess)
		   											.description("Test Calendar for " + testName)
		   											.build();
		
		BaseEvent ADevent = new BaseEvent.Builder( "community1-AllDayEvent" + Helper.genDateBasedRand())
										 .allDayEvent(true)
										 .build();
		
		BaseEvent event = new BaseEvent.Builder("community1-NormalEvent" + Helper.genDateBasedRand())
									   .build();
		
		BaseEvent RepeatEvent = new BaseEvent.Builder("community2-RepeatingEvent" + Helper.genDateBasedRand())
											 .repeat(true)
											 .build();
		
		//create community 1
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(apiOwner);
		
		//add the UUID to community 1
		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community1.getName() + " using API");
		community1.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//create community 2
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community com2API = community2.createAPI(apiMember);
		
		//add the UUID to community 2
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiMember, com2API);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community2.getName() + " using API");
		community2.addWidgetAPI(com2API, apiMember, BaseWidget.EVENTS);		

		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
				
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//Create events
		logger.strongStep("Create an all day event");
		ADevent.create(calUI);
		
		logger.strongStep("Create a normal event and verify the Create an Event button can be seen");
		event.create(calUI);
		calUI.fluentWaitElementVisible(CalendarUI.CreateEvent);
		
		logger.strongStep("Log out of and close the session");
		calUI.logout();
		ui.close(cfg);
		
		//login as member create another community
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		calUI.loadComponent(Data.getData().ComponentCommunities);
		calUI.login(comMember);

		community2.navViaUUID(ui);
		
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		calUI.fluentWaitElementVisible(CalendarUI.CreateEvent);
				
		logger.strongStep("Create a repeating event");
		RepeatEvent.create(calUI);
		
		calUI.loadComponent("communities",true);
		Community_View_Menu.IM_A_MEMBER.select(ui);
		ui.clickLinkWait("css=div[aria-label='" + community1.getName() + "']");
		
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		ui.clickLinkWait(calUI.getEventSelector(ADevent));	
		
		log.info("INFO: Follow the event");
		calUI.fluentWaitElementVisible(CalendarUI.Follow);	
		ui.clickLinkWait(CalendarUI.Follow);
		
		calUI.fluentWaitPresent(CalendarUI.StopFollowing);		
		ui.clickLinkWait(CalendarUI.BackToCommunityEventsLink);
		ui.clickLinkWait(calUI.getEventSelector(event));		

		ui.clickLinkWait(CalendarUI.Follow);
		calUI.fluentWaitPresent(CalendarUI.StopFollowing);		
		
		//goto homepage
		calUI.loadComponent("homepage",true);
		ui.clickLinkWait(HomepageUIConstants.Updates);
		//ui.closeGuidedTourPopup();
		calUI.fluentWaitPresent(HomepageUIConstants.eventsWidget);
		calUI.fluentWaitPresent(HomepageUIConstants.eventWidgetFrame);
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));
		calUI.fluentWaitElementVisible(HomepageUIConstants.eventWidgetPaging);
		
		String paging = driver.getSingleElement(HomepageUIConstants.eventWidgetPaging).getAttribute("title").toString();
		log.info("INFO: paging:" + paging);
		int i = Integer.parseInt(paging.substring(paging.indexOf("of")+3));
		Assert.assertTrue(i>2, "ERROR: event widget paging has more than 2 events.");
		Assert.assertTrue(paging.indexOf("1 through 5")>-1, "ERROR: the paging text is '1 through 5'.");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.eventWidgetPagingNextLink), "ERROR: the next link on paging bar is disabled.");

		if (testConfig.browserIs(BrowserType.IE) || testConfig.browserIs(BrowserType.CHROME)){
			driver.switchToFrame().returnToTopFrame();
			driver.getSingleElement(HomepageUIConstants.eventWidgetOpLink).click();
			driver.typeNative(Keys.ESCAPE, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.TAB, Keys.ENTER);
			driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));
			
		}else driver.getSingleElement(HomepageUIConstants.eventWidgetPagingNextLink).click();
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.eventWidgetPagingPrevLink), "ERROR: the previous link is disabled on HP events widget .");
		
		//edit event widget
		driver.switchToFrame().returnToTopFrame();
		driver.getElements(HomepageUIConstants.eventWidgetOpLink).get(2).click();
		ui.clickLinkWait(HomepageUIConstants.eventWidgetEditLink);
		
		//check default value is correct
		calUI.fluentWaitTextPresent(HomepageUIConstants.eventWidgetText);
		Select numselectBox = new Select((WebElement) driver.getSingleElement(HomepageUIConstants.eventWidgetNumberSel).getBackingObject());

		//TODO: this is a delay defects which should be 5 instead of 1. - Michelle on Feb. 14 2014
		Assert.assertTrue(numselectBox.getFirstSelectedOption().getText().equals("1")
				, "ERROR: default event widget entry number is not 1, it's " + numselectBox.getFirstSelectedOption().getText());
		
	    Select typeselectBox = new Select((WebElement) driver.getSingleElement(HomepageUIConstants.eventWidgetTypeSel).getBackingObject());
	    Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals(HomepageUIConstants.eventWidgetTypeOption1)
	    		, "ERROR: the evet widget selected type is " + typeselectBox.getFirstSelectedOption().getText());
		
	    logger.strongStep("Select 10 and Followed events from the 'Number of events to show:' and 'Show:' dropdowns respectively then click on Cancel");
	    log.info("INFO: Selecting 10 and Followed events from the 'Number of events to show:' and 'Show:' dropdowns respectively then clicking on Cancel");
	    calUI.selectComboValue(HomepageUIConstants.eventWidgetNumberSel, "10");
	    calUI.selectComboValue(HomepageUIConstants.eventWidgetTypeSel, HomepageUIConstants.eventWidgetTypeOption3);
	    ui.clickLinkWait(CalendarUI.CancelButton);
		
		driver.switchToFrame().returnToTopFrame();
		driver.getElements(HomepageUIConstants.eventWidgetOpLink).get(2).click();
		ui.clickLinkWait(HomepageUIConstants.eventWidgetEditLink);
		
		calUI.fluentWaitTextPresent(HomepageUIConstants.eventWidgetText);
	    log.info("INFO: 10 should be selected in the 'Number of events to show:' dropdown");
		//TODO: this is a delay defects which should be 5 instead of 10. - Michelle on Feb.14.2014
		Assert.assertTrue(numselectBox.getFirstSelectedOption().getText().equals("10")
				, "ERROR: Edit event widget doesn't keep the previous settings, the current value is " + numselectBox.getFirstSelectedOption().getText());	
	    Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals(HomepageUIConstants.eventWidgetTypeOption3)
	    		, "ERROR: the currect selected events type is " + typeselectBox.getFirstSelectedOption().getText());
	    
	    logger.strongStep("Select 1 and Attending events from the 'Number of events to show:' and 'Show:' dropdowns respectively then save the changes");
	    log.info("INFO: Selecting 1 and Attending events from the 'Number of events to show:' and 'Show:' dropdowns respectively then saving the changes");
	    calUI.selectComboValue(HomepageUIConstants.eventWidgetNumberSel, "1");
	    calUI.selectComboValue(HomepageUIConstants.eventWidgetTypeSel, HomepageUIConstants.eventWidgetTypeOption2);
	    ui.clickLinkWait(CalendarUI.SaveButton);
		
		calUI.fluentWaitElementVisible(HomepageUIConstants.eventWidgetFrame);
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));
		
		calUI.fluentWaitPresent(HomepageUIConstants.eventWidgetPaging);

		paging = driver.getSingleElement(HomepageUIConstants.eventWidgetPaging).getAttribute("title").toString();
		
		Assert.assertTrue(paging.indexOf("1 through 1")>-1, "ERROR: the current paging content doesn't include '1 through 1', it's " + paging);
		Assert.assertTrue(driver.getElements(HomepageUIConstants.linksineventWidget).size() == 1
				, "ERROR: the current link number in the event widget is not 1, it's " + driver.getElements(HomepageUIConstants.linksineventWidget).size());
		
		//change to follow widget
		driver.switchToFrame().returnToTopFrame();
		driver.getElements(HomepageUIConstants.eventWidgetOpLink).get(2).click();
		ui.clickLinkWait(HomepageUIConstants.eventWidgetEditLink);
		
		calUI.fluentWaitTextPresent(HomepageUIConstants.eventWidgetText);
		numselectBox = new Select((WebElement) driver.getSingleElement(HomepageUIConstants.eventWidgetNumberSel).getBackingObject());

		Assert.assertTrue(numselectBox.getFirstSelectedOption().getText().equals("1")
				, "ERROR: the entry number for event widget is not 1, it's " + numselectBox.getFirstSelectedOption().getText());	
		typeselectBox = new Select((WebElement) driver.getSingleElement(HomepageUIConstants.eventWidgetTypeSel).getBackingObject());
	    Assert.assertTrue(typeselectBox.getFirstSelectedOption().getText().equals(HomepageUIConstants.eventWidgetTypeOption2)
	    		, "ERROR: the event type is not correct, it's " + typeselectBox.getFirstSelectedOption().getText());
		
	    logger.strongStep("Select 5 and Followed events from the 'Number of events to show:' and 'Show:' dropdowns respectively then save the changes");
	    log.info("INFO: Selecting 5 and Followed events from the 'Number of events to show:' and 'Show:' dropdowns respectively then saving the changes");
		log.info("INFO: Select view feed titled Add to Personal Calendar to launch a dialog");
	    calUI.selectComboValue(HomepageUIConstants.eventWidgetNumberSel, "5");
	    calUI.selectComboValue(HomepageUIConstants.eventWidgetTypeSel, HomepageUIConstants.eventWidgetTypeOption3);
	    ui.clickLinkWait(CalendarUI.SaveButton);
		
		driver.switchToFrame().selectFrameByElement(driver.getSingleElement(HomepageUIConstants.eventWidgetFrame));
		calUI.fluentWaitPresent(HomepageUIConstants.eventWidgetPaging);

		paging = driver.getSingleElement(HomepageUIConstants.eventWidgetPaging).getAttribute("title").toString();
		
		Assert.assertTrue(driver.getElements(HomepageUIConstants.linksineventWidget).size() == 2
				,"ERROR: the current link number in the event widget is not 2, it's " + driver.getElements(HomepageUIConstants.linksineventWidget).size() );
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the tag cloud.
	 *<li><B>Step: </B>Create a normal event with tag, tagnormal.
	 *<li><B>Step: </B>Create an all day event with tag, tagallday.
	 *<li><B>Step: </B>Create repeating event with tag, tagrepeating.
	 *<li><B>Step: </B>Tag Cloud shows correctly.
	 *<li><B>Step: </B>Click "List" link in tagcloud.
	 *<li><B>Verify: </B>Displayed correctly.
	 *<li><B>Step: </B>Click "tag".
	 *<li><B>Verify: </B>All events show up.
	 *<li><B>Step: </B>Click "tagnormal".
	 *<li><B>Verify: </B>Only normal tags show up.
	 *<li><B>Step: </B>Remove "tagnormal" in cloud.
	 *<li><B>Verify: </B>All tags show up.
	 *<li><B>Step: </B>Enter "tag".
	 *<li><B>Step: </B>Choose "tagrepeating" in typeahead.
	 *<li><B>Verify: </B>Repeating show up.
	 *<li><B>Step: </B>Remove "tagrepeating" in the main section.
	 *<li><B>Verify: </B>All show up.
	 *<li><B>Step: </B>Enter "notexisted".
	 *<li><B>Verify: </B>No tag shows up.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testTagCloud() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .access(defaultAccess)
									 			   .description("Test Calendar for " + testName)
									 			   .build();

		BaseEvent eventAllDay = new BaseEvent.Builder("All Day Event" + Helper.genDateBasedRand())
		 									 .tags("tag tagallday")
		 									 .allDayEvent(true)
		 									 .description("All day")
		 									 .build();
		
		BaseEvent event = new BaseEvent.Builder("Normal Event" + Helper.genDateBasedRand())
									   .tags("tag tagnormal")
									   .build();
		
		BaseEvent eventrepeat = new BaseEvent.Builder("Repeating Event" + Helper.genDateBasedRand())
		 									 .tags("tag tagrepeating")
		 									 .repeat(true)
		 									 .build();
		
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Navigate to the Events via left menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//Create an all day Event
		logger.strongStep("Create an all day Event");
		log.info("INFO: Creating an all day Event");
		eventAllDay.create(calUI);
		
		//Create another Event
		logger.strongStep("Create a normal Event");
		log.info("INFO: Creating a normal Event");
		event.create(calUI);
		
		//Create a repeating
		logger.strongStep("Create a repeating event");
		log.info("INF: Creating a repeating event");
		eventrepeat.create(calUI);
				
		//Wait for tag cloud
		logger.strongStep("Wait for tag cloud Widget to be present");
		log.info("INFO: Waiting for tag cloud Widget to be present");
		calUI.fluentWaitPresent(CalendarUI.tagCloudWidget);
		
		//Validate the tags for the events are located within the cloud
		String[] sNormalTags = event.getTags().split(" ");
		String[] sRepeatTags = eventrepeat.getTags().split(" ");
		String[] sADTags = eventAllDay.getTags().split(" ");
		
		Assert.assertTrue(driver.isElementPresent(CalendarUI.tagCloudCloudView), "ERROR: tag cloud view doesn't exists");
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sADTags[0])), "ERROR: the tag " + sADTags[0] + "doesn't exists" );
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sNormalTags[1])), "ERROR: the tag " + sNormalTags[1] + "doesn't exists");
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sADTags[1])), "ERROR: the tag " + sADTags[1] + "doesn't exists");
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sRepeatTags[1])), "ERROR: the tag " + sRepeatTags[1] + "doesn't exists");
		
		logger.strongStep("Click 'List' link and verify if view changes correctly");
		log.info("INFO: Click 'List' link, verify if view changes correctly");
		ui.clickLinkWait(BaseUIConstants.ListLink);
		
		
		Assert.assertTrue(driver.isElementPresent(CalendarUI.tagCloudListView), "ERROR: tag cloud list view doesn't exists");
		Assert.assertTrue(driver.getElements(CalendarUI.TagListinListView).size()>0, "ERROR: tag list doesn't show any tags");

		logger.strongStep("Click tag link in tagcloud, check if all events show up");
		log.info("INFO: Click tag link in tagcloud, check if all events show up");
		ui.clickLinkWait(BaseUIConstants.CloudLink);
		
		calUI.fluentWaitPresent(calUI.sTagLinkinTagCloud(sNormalTags[0]));
		driver.getSingleElement(calUI.sTagLinkinTagCloud(sNormalTags[0])).hover();		
		ui.clickLinkWait(calUI.sTagLinkinTagCloud(sNormalTags[0]));
		
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(eventAllDay)), "ERROR: " + eventAllDay.getName() + " doesn't show up.");  
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " doesn't show up.");		
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(eventrepeat)).size()>0,  "ERROR: " + eventrepeat.getName() + " doesn't show up.");
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinTagCloud(sNormalTags[0])),  "ERROR: " + sNormalTags[0] + " doesn't show in cloud .");
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinmain(sNormalTags[0])),  "ERROR: " + sNormalTags[0] + " doesn't show up in main.");
		Assert.assertTrue(driver.getElements(CalendarUI.tagCloudRelatedTag).size()>0, "ERROR: related tags doesn't show up.");
		
		logger.strongStep("Click tag link in related tags in tagcloud, check if events show up correctly");
		log.info("INFO: Click tag link in related tags in tagcloud, check if events show up correctly");		
		ui.clickLinkWait("link=+" + sNormalTags[1]); 
		calUI.fluentWaitPresent(calUI.sRemoveTagLinkinTagCloud(sNormalTags[1]));
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(eventAllDay)), "ERROR: " + eventAllDay.getName() + " show up.");
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " doesn't show up.");
		Assert.assertFalse(driver.getElements(calUI.getEventSelector(eventrepeat)).size()>0, "ERROR: " + eventrepeat.getName() + " show up.");
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinTagCloud(sNormalTags[1])),  "ERROR: " + sNormalTags[0] + " doesn't show in cloud .");
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinmain(sNormalTags[1])),  "ERROR: " + sNormalTags[0] + " doesn't show up in main.");
		
		logger.strongStep("Remove tag link in tagcloud, check if events show up correctly");
		log.info("INFO: Remove tag link in tagcloud, check if events show up correctly");		
		driver.getSingleElement(calUI.sRemoveTagLinkinTagCloud(sNormalTags[1])).click();
		calUI.fluentWaitPresent(calUI.getEventSelector(eventAllDay));  
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(eventAllDay)), "ERROR: " + eventAllDay.getName() + " doesn't show up.");  
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " doesn't show up.");		
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(eventrepeat)).size()>0,  "ERROR: " + eventrepeat.getName() + " doesn't show up."); 
		Assert.assertFalse(driver.isElementPresent(calUI.sRemoveTagLinkinTagCloud(sNormalTags[1])),  "ERROR: " + sNormalTags[0] + " show in cloud .");
		Assert.assertFalse(driver.isElementPresent(calUI.sRemoveTagLinkinmain(sNormalTags[1])),  "ERROR: " + sNormalTags[0] + " show in cloud .");
		
		logger.strongStep("Enter tag in tagcloud, check if events show up correctly");
		log.info("INFO: Enter tag in tagcloud, check if events show up correctly");	
		ui.clickLinkWait(calUI.sRemoveTagLinkinTagCloud(sNormalTags[0]));
		
		int i;
		for(i=0; i<driver.getElements(BaseUIConstants.FindTag).size(); i++)
			if(driver.getElements(BaseUIConstants.FindTag).get(i).isVisible())
				break;
		
		driver.getElements(BaseUIConstants.FindTag).get(i).click();
		calUI.fluentWaitPresent(CalendarUI.tagTextBox);
		driver.getSingleElement(CalendarUI.tagTextBox).type("tag");
		calUI.fluentWaitPresent(CalendarUI.tagTypeahead);
		Assert.assertTrue(driver.getElements(CalendarUI.tagLinksinTagTypeahead).size()==7
				,  "ERROR: tagcloud typeahead shows more selections, total number is "+ driver.getElements(CalendarUI.tagLinksinTagTypeahead).size());
		//select the tag input using the "ENTER" key.
		logger.strongStep("Use the 'Enter' key to select a tag");
		log.info("INFO: Using the 'Enter' key select a tag");
		driver.typeNative(Keys.ENTER);										
		calUI.fluentWaitPresent(calUI.sTagLinkinTagCloud(sNormalTags[0]));
		
		//check for enter a long tag can see the typeahead
		driver.getSingleElement(CalendarUI.tagTextBox).type(sRepeatTags[1]);
		Assert.assertEquals(driver.getElements(CalendarUI.tagLinksinTagTypeahead).size(), 4);
		
		driver.getSingleElement(CalendarUI.tagTextBox).clear();
		driver.getSingleElement(CalendarUI.tagTextBox).type(sADTags[1]);
		driver.getVisibleElements(CalendarUI.tagLinksinTagTypeahead).get(1).click();
		ui.fluentWaitTextNotPresentWithoutRefresh(event.getName());
		
		
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(eventAllDay)), "ERROR: " + eventAllDay.getName() + " doesn't show up."); 
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " show up.");	
		Assert.assertFalse(driver.getElements(calUI.getEventSelector(eventrepeat)).size()>0, "ERROR: " + eventrepeat.getName() + " show up."); 
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinTagCloud(sADTags[1])),  "ERROR: " + sNormalTags[0] + " doesn't show in cloud .");
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinmain(sADTags[1])),  "ERROR: " + sNormalTags[0] + " doesn't show up in main.");
		
		for(i=0;i<driver.getElements(CalendarUI.tagCloudRelatedTag).size();i++)		
			Assert.assertFalse(driver.getElements(CalendarUI.tagCloudRelatedTag).get(i).isVisible()
					,  "ERROR: " + driver.getElements(CalendarUI.tagCloudRelatedTag).get(i) + " doesn't show up.");
		
		logger.strongStep("Remove tag link in main, check if events show up correctly");
		log.info("INFO: Removing tag link in main, checking if events show up correctly");		
		ui.clickLinkWait(calUI.sRemoveTagLinkinmain(sADTags[1]));
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(eventAllDay)), "ERROR: " + eventAllDay.getName() + " doesn't show up.");			
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " doesn't show up.");			
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(eventrepeat)).size()>0, "ERROR: " + eventrepeat.getName() + " doesn't show up.");	
		Assert.assertFalse(driver.isElementPresent(calUI.sRemoveTagLinkinTagCloud(sADTags[1])),  "ERROR: " + sNormalTags[0] + " show in cloud .");
		Assert.assertFalse(driver.isElementPresent(calUI.sRemoveTagLinkinmain(sADTags[1])),  "ERROR: " + sNormalTags[0] + " show up in main.");
		Assert.assertTrue(driver.getElements(CalendarUI.tagCloudRelatedTag).size()>0, "ERROR: Related tags doesn't show up");
		
		logger.strongStep("Enter notexisted tag in tagcloud then check if events show up correctly");
		log.info("INFO: Entering notexisted tag in tagcloud, checking if events show up correctly");	
		calUI.fluentWaitPresent(CalendarUI.tagTextBox);
		driver.getSingleElement(CalendarUI.tagTextBox).type("notexisted");
		
		Assert.assertFalse(driver.isElementPresent(CalendarUI.tagTypeahead), "ERROR: Tag typeahead still shows");
		ui.clickLinkWait(CalendarUI.tagSearchBtn);
		
		Assert.assertTrue(driver.isElementPresent(calUI.sRemoveTagLinkinmain("notexisted")), "ERROR: notexisted doesn't show in main");
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(eventAllDay)), "ERROR: " + eventAllDay.getName() + " show up.");	
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " show up.");			
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(eventrepeat)), "ERROR: " + eventrepeat.getName() + " show up.");	

		for(i=0;i<driver.getElements(CalendarUI.tagCloudRelatedTag).size();i++)		
			Assert.assertFalse(driver.getElements(CalendarUI.tagCloudRelatedTag).get(i).isVisible()
					,  "ERROR: " + driver.getElements(CalendarUI.tagCloudRelatedTag).get(i) + " show up.");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if a non-member cannot view a private and a public event through URL.
	 *<li><B>Step: </B>Create a public community and a private community with member.
	 *<li><B>Step: </B>Create a normal event in public and repeating in private.
	 *<li><B>Step: </B>Take the URL for four pages events widget/event detail in public/private community.
	 *<li><B>Verify: </B>Public content shows up without login.
	 *<li><B>Verify: </B>Private content shows up after login using member.
	 *<li><B>Verify: </B>Access denial after login using non-member.
	 *<li><B>Verify: </B>A member is able to view all events for a Private Community after logging in.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testURLAddressableforEvents() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();

		User testUser2 = cfg.getUserAllocator().getUser();
		User testUser3 = cfg.getUserAllocator().getUser();
		
		BaseCommunity commPublic = new BaseCommunity.Builder(testName + "public community " + Helper.genDateBasedRand())	
												    .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												    .description("Test Calendar for " + testName)
												    .addMember(new Member(CommunityRole.MEMBERS, testUser2))
												    .access(Access.PUBLIC)
												    .build();

		BaseCommunity commPrivate = new BaseCommunity.Builder(testName + "private community " + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .description("Test " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser2))
												   .access(Access.RESTRICTED)
												   .shareOutside(false)
												   .build();
		
		
		
		BaseEvent event = new BaseEvent.Builder("Normal Event !@#$$%^" + Helper.genDateBasedRand())
									   .build();
		
		BaseEvent RepeatEvents = new BaseEvent.Builder("Repeating Event )(*&^%$" + Helper.genDateBasedRand())
											  .repeat(true)
											  .build();
		
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community commPublicAPI = commPublic.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		commPublic.getCommunityUUID_API(apiOwner, commPublicAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + commPublic.getName() + " using API");
		commPublic.addWidgetAPI(commPublicAPI, apiOwner, BaseWidget.EVENTS);
		
		//create restricted community
		logger.strongStep("Create restricted community using API");
		log.info("INFO: Create restricted community using API");
		Community commPrivateAPI = commPrivate.createAPI(apiOwner);
		
		//add the UUID to restricted community
		log.info("INFO: Get UUID of community");
		commPrivate.getCommunityUUID_API(apiOwner, commPrivateAPI);
		
		//add the events widget to the restricted community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + commPrivate.getName() + " using API");
		commPrivate.addWidgetAPI(commPrivateAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the restricted API community	
		commPrivate.navViaUUID(ui);
		
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				

		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//create an event
		logger.strongStep("Create a repeating event");
		RepeatEvents.create(calUI);
		
		String sPrivateCalendarHandle = driver.getCurrentUrl();		
		Assert.assertTrue(driver.getElements(CalendarUI.RepeatingEvent).size()>0, "ERROR: repeating event doesn't show up");
		driver.getElements(CalendarUI.RepeatingEvent).get(0).click();
		String sPrivateEventHandle = driver.getCurrentUrl();		
		calUI.logout();
		calUI.close(cfg);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		commPublic.navViaUUID(ui);
				
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				

		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		logger.strongStep("Create a normal event");
		event.create(calUI);
	
		String sPublicCalendarHandle = driver.getCurrentUrl();

		ui.clickLinkWait(calUI.getEventSelector(event));
		String sPublicEventHandle = driver.getCurrentUrl();
	
		//verify url addressable	
		//smartcloud requires login no matter private or public community
		calUI.verifyPublicURL(sPublicCalendarHandle, sPublicEventHandle, event.getName().substring(0, 25), testUser3);
		calUI.logout();
		calUI.close(cfg);
		
		String handle = sPrivateEventHandle.substring(sPrivateEventHandle.indexOf("/", sPrivateEventHandle.indexOf("//")+2));
		logger.strongStep("Open a Private Community using its handle, it should display 'Access Denied' for a non-member");
		log.info("INFO: Private community event handle is: " + handle);
		
		calUI.loadComponent(handle, false);
		calUI.login(testUser3);
	
//		Assert.assertTrue(driver.isTextPresent("Access denied"), "ERROR: no access denied shows");
		driver.navigate().to(sPrivateCalendarHandle);
		Assert.assertTrue(driver.isTextPresent("Access denied"), "ERROR: no access denied shows");
		calUI.close(cfg);
		
		logger.strongStep("Open a Private Community using its handle, a member should be able to view the events");
		log.info("INFO: Verify that a member is able to view all events for a Private Community");
		calUI.loadComponent(handle, false);
		calUI.login(testUser2);
//		Assert.assertTrue(driver.isTextPresent(RepeatEvents.getName().substring(0,26)), "The repeating events doesn't show up");
		driver.load(sPrivateCalendarHandle, true);
				
		calUI.fluentWaitPresent(CalendarUI.EventListTab);

		Assert.assertTrue(driver.getElements(CalendarUI.RepeatingEvent).size()>0, "ERROR: Repeating events doesn't show up");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.EventListTab), "'Events' tab not found: " + CalendarUI.EventListTab);
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the update tag function.
	 *<li><B>Step: </B>Login as user1.
	 *<li><B>Step: </B>Create a normal event with tag tagnormal, tag.
	 *<li><B>Step: </B>Login as user2.
	 *<li><B>Step: </B>Create an all day event, enter tag to get typeahead.
	 *<li><B>Step: </B>Choose "tag" in the typeahead, and save.
	 *<li><B>Verify: </B>In tagcloud make sure can see both tags.
	 *<li><B>Step: </B>Edit all day event.
	 *<li><B>Step: </B>Add tag "tagallday".
	 *<li><B>Step: </B>Edit normal event.
	 *<li><B>Step: </B>Remove tag "tagnormal".
	 *<li><B>Step: </B>Add "?~@#$%^*){}|[]\\:<>./""'".
	 *<li><B>Verify: </B>In tagcloud make sure tagcloud shows the "tagallday", "?~@#$%^*){}|[]\\:<>./""'", and doesn't show "tagnormal".
	 *<li><B>Step: </B>Click tag in event detail view.
	 *<li><B>Verify: </B>The filter works correctly.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testUpdateTag() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		
		User comMember = cfg.getUserAllocator().getUser();
		
		//Create events
		String specialTag = "?~@#$%^*){}|[]\\:<>./\"\"\'";
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .access(defaultAccess)
									 			   .description("Test Calendar for " + testName)
									 			   .addMember(new Member(CommunityRole.MEMBERS, comMember))
									 			   .build();
		
		//Create an event base state object
		BaseEvent event = new BaseEvent.Builder("Normal Event" + Helper.genDateBasedRand())
									   .tags("tag tagnormal")
									   .description(Data.getData().commonDescription)
									   .build();
		
		//Create an event base state object
		BaseEvent adEvent = new BaseEvent.Builder("All Day Event" + Helper.genDateBasedRand())
										 .tags("tag tagallday")
										 .allDayEvent(true)
										 .build();
		
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
				
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//create event
		logger.strongStep("Create a normal event");
		log.info("INFO: Create a normal Event");
		event.create(calUI);
		
		//Wait for tag cloud
		calUI.fluentWaitPresent(CalendarUI.tagCloudWidget);		
		Assert.assertTrue(driver.isElementPresent(CalendarUI.tagCloudCloudView), "ERROR: cloud view doesn't show");
		
		//Validate the tags for the event are in the cloud
		String[] sTagFornormalEvent = event.getTags().split(" ");
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sTagFornormalEvent[0])), "ERROR: " + sTagFornormalEvent[0] + " doesn't show.");
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sTagFornormalEvent[1])), "ERROR: " + sTagFornormalEvent[1] + " doesn't show.");		
		
		calUI.logout();
		calUI.close(cfg);
		
		//Login as member
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		calUI.loadComponent(Data.getData().ComponentCommunities);
		calUI.login(comMember);
		
		//Select Member
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		//open community
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");
		
		//left nav options
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//check tag typeahead when new an event
		logger.strongStep("Click on 'Create an Event' link, then enter the tag and verify the typeahead is displayed");
		log.info("INFO: Click on 'Create an Event' link, then input the tag and make sure the typeahead is displayed");
		ui.clickLinkWait(CalendarUI.CreateEvent);
		driver.getSingleElement(CalendarUI.EventTag).type("tag");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.CalendartagTypeahead), "ERROR: tag typeahead doesn't show");
		Assert.assertTrue(driver.getElements(CalendarUI.CalendartagLinksinTypeahead).size() == 4
				, "ERROR: tag listed in typeahead is not 4, it's " + driver.getElements(CalendarUI.CalendartagLinksinTypeahead).size());
		ui.clickLinkWait(CalendarUI.EventCancel);
		
		logger.strongStep("Create an all day event");
		adEvent.create(calUI);
		calUI.fluentWaitPresent(CalendarUI.tagCloudWidget);		

		Assert.assertTrue(driver.isElementPresent(CalendarUI.tagCloudCloudView), "ERROR: tag cloud view doesn't show up");
		String[] sTagForAllDayEvent = adEvent.getTags().split(" ");
		Assert.assertTrue(driver.isElementPresent(calUI.sTagLinkinTagCloud(sTagForAllDayEvent[1])), "ERROR: " + sTagForAllDayEvent[1] + "doesn't show up");		
		
		ui.clickLinkWait(calUI.getEventSelector(adEvent));
		
		logger.strongStep("Edit the event, then enter \"?~@#$%^*){}|[]\\:<>./\"\"'\" as the tag and verify that the tag is displayed for the event");
		log.info("INFO: Editing the event, then inputting \"?~@#$%^*){}|[]\\:<>./\"\"'\" as the tag and verifying that the tag is displayed for the event");
		calUI.fluentWaitElementVisible(CalendarUI.Edit);
		ui.clickLinkWait(CalendarUI.Edit);
		
		driver.getSingleElement(CalendarUI.EventTag).clear();
		driver.getSingleElement(CalendarUI.EventTag).type(specialTag);
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		calUI.fluentWaitElementVisible(CalendarUI.Edit);
		Assert.assertEquals(driver.getElements(CalendarUI.lotusTag).get(0).getText(), specialTag.substring(0,23), "ERROR: specialTag doesn't show up");
		
		logger.strongStep("Check that the 'Cloud' link is displayed in the 'Tags' container");
		log.info("INFO: Verify the 'Cloud' link is displayed in the 'Tags' container");
		calUI.fluentWaitPresent(CalendarUI.tagCloudWidget);		
		Assert.assertTrue(driver.isElementPresent(CalendarUI.tagCloudCloudView), "ERROR: tag cloud view doesn't show up");
		Assert.assertFalse(driver.isElementPresent(calUI.sTagLinkinTagCloud(sTagForAllDayEvent[1])), "ERROR: " + sTagForAllDayEvent[1] + "shows in cloud");	
		
		int i=0;
		for(i=0; i<driver.getElements(BaseUIConstants.taglinks).size(); i++){
			if(driver.getElements(BaseUIConstants.taglinks).get(i).getAttribute("title").contains(specialTag.substring(0,23)))
				break;
		}
		Assert.assertTrue(i<driver.getElements(BaseUIConstants.taglinks).size(), "ERROR: no tag shows in cloud");
		driver.getElements(CalendarUI.lotusTag).get(i).click();
		Assert.assertTrue(driver.isElementPresent("css=div#calendar_event_viewer > div > h1:contains(" + adEvent.getName() + ")"), "ERROR: events " + adEvent.getName() + " doesn't show.");
//		ui.fluentWaitPresent("css=div#calendar_event_viewer > div > h3:contains(event.getName())");
		calUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can edit a community event.
	 *<li><B>Step: </B>Create a new community.
	 *<li><B>Step: </B>Add a member and add the Event widget.
	 *<li><B>Step: </B>Login as a member.
	 *<li><B>Step: </B>Create a normal event.
	 *<li><B>Step: </B>Click open event.
	 *<li><B>Verify: </B>See the edit link there.
	 *<li><B>Step: </B>Login as owner.
	 *<li><B>Step: </B>Edit event widget.
	 *<li><B>Step: </B>Change member to be read only.
	 *<li><B>Step: </B>Login as member.
	 *<li><B>Verify: </B>Cannot see the create event button.
	 *<li><B>Step: </B>Go to the event it creates.
	 *<li><B>Verify: </B>No edit link shows there.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testeditComEventWidget() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();	
		User comMember = cfg.getUserAllocator().getUser();
	
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .access(defaultAccess)
									 			   .description("Test Calendar for " + testName)
									 			   .addMember(new Member(CommunityRole.MEMBERS, comMember))
									 			   .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		ui.logout();
		ui.close(cfg);
		
		//Load component and login as a member of community
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(comMember);

		//Select Communities that the user is a member of
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		//Open community
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view.
		ui.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//Create an event
		logger.strongStep("Create an event");
		event.create(calUI);
	
		//Open the Event
		ui.clickLinkWait(calUI.getEventSelector(event));

		//Verify User is able to see Edit button
		Assert.assertTrue(driver.isElementPresent(CalendarUI.EditLink), "ERROR: edit link doesn't show");
		
		ui.logout();
		ui.close(cfg);
		
		//Load component and login as a member of community
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		
		if (flag) {

		    logger.strongStep("Add the Overview page to the Community and make it the landing page");
		    log.info("INFO: Add the Overview page to the Community and make it the landing page");
		    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		
		}

		//Select Communities that the user is a member of
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//Open community
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");

		//edit widget 
		log.info("INFO:  ---- edit widget ----");
		ui.performCommWidgetAction(BaseWidget.EVENTS, Widget_Action_Menu.EDIT);
		
		//Sometimes the Edit Calendar Settings section does not appear on icautomation and the page needs to be refreshed for that
		logger.strongStep("Keep refreshing the page until the Edit Calendar Settings section is visible");
		log.info("INFO: Refresh the page if the Edit Calendar Settings section does not appear");
		ui.fluentWaitTextPresent(CalendarUI.editWarnMsg);
		ui.fluentWaitPresentWithRefresh("text=" + CalendarUI.editMsg);
		
		Assert.assertTrue(driver.isTextPresent(CalendarUI.editWarnMsg), "ERROR: edit warn msg doesn't show up");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.authorRoleRB), "ERROR: author role radio btn doesn't show");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.readerRoleRB), "ERROR: reader role radio btn doesn't show");		
		Assert.assertTrue(driver.getSingleElement(CalendarUI.authorRoleRB).isSelected(), "ERROR: author role radio btn isn't selected");
		Assert.assertFalse(driver.getSingleElement(CalendarUI.readerRoleRB).isSelected(), "ERROR: reader role radio btn is selected.");
		
		ui.clickLinkWait(CalendarUI.readerRoleRB);
		Assert.assertTrue(driver.getSingleElement(CalendarUI.readerRoleRB).isSelected(), "ERROR: reader role radio btn is not selected");
		
		ui.clickLinkWait(CalendarUI.savecloseBtn);
		
		ui.fluentWaitTextPresent(Data.getData().upcomingEvents);
		
		ui.logout();
		ui.close(cfg);
		
		//Load component and login as a member of community
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(comMember);
		
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		//Open community
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		ui.fluentWaitPresent(calUI.getEventSelector(event));
		Assert.assertFalse(driver.getSingleElement(CalendarUI.CreateEvent).isVisible(), "ERROR: create event btn is visible");

		ui.clickLinkWait(calUI.getEventSelector(event));
		
		ui.fluentWaitPresent(CalendarUI.EditLink);
		String disabledAttribute = driver.getSingleElement(CalendarUI.EditLink).getAttribute("aria-disabled");
		Assert.assertTrue(disabledAttribute != null && disabledAttribute.equals("true"), "ERROR: edit link is not disabled");
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to make sure the order is correct when ordered by future | Note: Month value is 0-based. e.g., 0 for January.
	 *<li><B>Step: </B>Create a community, event widget.
	 *<li><B>Step: </B>Create a normal event, several repeating events, and all day event in the future.
	 *<li><B>Verify: </B>Make sure the order of future events is correct.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testeventsOrderinFuture() throws Exception{

		Calendar futuredate1 = Calendar.getInstance();
		futuredate1.add(Calendar.YEAR, 5);
		futuredate1.add(Calendar.DATE, 3);
		Calendar futuredate2 = Calendar.getInstance();
		futuredate2.add(Calendar.YEAR, 5);
		futuredate2.add(Calendar.DATE, 4);
		Calendar futuredate3 = Calendar.getInstance();
		futuredate3.add(Calendar.YEAR, 5);
		futuredate3.add(Calendar.DATE, 2);
		Calendar repeatUntil = Calendar.getInstance();
		repeatUntil.add(Calendar.YEAR, 5);
		repeatUntil.add(Calendar.DATE, 15);
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();

		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .description("Test " + testName)
												   .build();		
				
		BaseEvent normalEvent = new BaseEvent.Builder("Normal Event " + Helper.genDateBasedRand())
											 .startDate(futuredate1)
											 .startTime("1:00 AM")
											 .endTime("2:00 AM")
											 .build();
		
		BaseEvent allDayEvent = new BaseEvent.Builder("AllDay Event" + Helper.genDateBasedRand())
											 .allDayEvent(true)
											 .startDate(futuredate3)
											 .build();

		
		BaseEvent reDayEvent = new BaseEvent.Builder("Repeating Event" + Helper.genDateBasedRand())
											.repeat(true)
											.startDate(futuredate2)
											.startTime("3:00 AM")
											.endTime("4:00 AM")
											.repeatUntil(repeatUntil)
											.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				

		//Verify the calendar view
		logger.strongStep("Create normal, repeating and all day events");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(normalEvent);
		calUI.create(reDayEvent);
		calUI.create(allDayEvent);
		
		log.info("INFO: Verify that all future events show up in the correct order");
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(normalEvent)), "ERROR: " + normalEvent.getName() + " doesn't show up");
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(allDayEvent)), "ERROR: " + allDayEvent.getName() + " doesn't show up");
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(reDayEvent)).size()>0, "ERROR: " + reDayEvent.getName() + " doesn't show up");
		
		List<Element> eventList = driver.getElements(CalendarUI.EventList);
		Assert.assertEquals(eventList.size(), 10, "ERROR: not all future events show up");
		Assert.assertTrue(eventList.get(0).getText().contains(allDayEvent.getName()), "ERROR: The 1 event is not " + allDayEvent.getName());		
		Assert.assertTrue(eventList.get(1).getText().contains(normalEvent.getName()), "ERROR: The 2 event is not " + normalEvent.getName());
		Assert.assertTrue(eventList.get(3).getText().contains(reDayEvent.getName()), "ERROR: The 3 event is not " + reDayEvent.getName());
		
		
		 calUI.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to make sure the order is correct when ordered by past | Note: Month value is 0-based. e.g., 0 for January.
	 *<li><B>Step: </B>Create a community, event widget.
	 *<li><B>Step: </B>Create a normal event, several repeating events, and all day event in the past.
	 *<li><B>Verify: </B>Make sure the order of past events is correct.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testeventsOrderinPast() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		
		Calendar pastdate1 = new GregorianCalendar(2014,1,13);
		Calendar pastdate2 = new GregorianCalendar(2014,1,12);
		Calendar pastdate3 = new GregorianCalendar(2014,1,1);
		Calendar repeatUntil = new GregorianCalendar(2014,1,13); 
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();
				
		BaseEvent normalEvent = new BaseEvent.Builder("Normal Event " + Helper.genDateBasedRand())
											 .startDate(pastdate1)
											 .endDate(pastdate1)
											 .startTime("1:00 AM")
											 .endTime("2:00 AM")
											 .build();

		BaseEvent allDayEvent = new BaseEvent.Builder("AllDay Event" + Helper.genDateBasedRand())
											 .allDayEvent(true)
											 .startDate(pastdate2)
											 .build();
		
		BaseEvent reDayEvent = new BaseEvent.Builder("Repeating Event" + Helper.genDateBasedRand())
											.repeat(true)
											.startDate(pastdate3)
											.endDate(pastdate3)
											.startTime("3:00 AM")
											.endTime("4:00 AM")
											.repeatUntil(repeatUntil)
											.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			

		//Verify the calendar view
		logger.strongStep("Create normal, repeating and all day events");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(normalEvent);
		calUI.create(reDayEvent);
		calUI.create(allDayEvent);
		
		log.info("INFO: Click on 'Show Past Events' link and verify that all past events show up in the correct order");
		ui.fluentWaitPresent(CalendarUI.pastEventLink);
		driver.getSingleElement(CalendarUI.pastEventLink).click();
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(normalEvent)), "ERROR: " + normalEvent.getName() + " doesn't show up");
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(allDayEvent)), "ERROR: " + allDayEvent.getName() + " doesn't show up");
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(reDayEvent)).size()>0, "ERROR: " + reDayEvent.getName() + " doesn't show up");
		
		List<Element> eventList = driver.getElements(CalendarUI.EventList);
		Assert.assertEquals(eventList.size(), 10, "ERROR: not all past events show up");
		Assert.assertTrue(eventList.get(0).getText().contains(reDayEvent.getName()), "ERROR: The 1 event is not " + reDayEvent.getName());
		Assert.assertTrue(eventList.get(1).getText().contains(normalEvent.getName()), "ERROR: The 2 event is not " + normalEvent.getName());
		Assert.assertTrue(eventList.get(2).getText().contains(reDayEvent.getName()), "ERROR: The 3 event is not " + reDayEvent.getName());
		Assert.assertTrue(eventList.get(3).getText().contains(allDayEvent.getName()), "ERROR: The 4 event is not " + allDayEvent.getName());
		
		 calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if past events are shown correctly.
	 *<li><B>Step: </B>Create an normal event.
	 *<li><B>Verify: </B>Show normally.
	 *<li><B>Step: </B>Edit the event.
	 *<li><B>Step: </B>Change the start and end dates to a date in the past.
	 *<li><B>Verify: </B>The event should not appear under 'Show Upcoming Events'.
	 *<li><B>Step: </B>Click on 'Show Past Events' link.
	 *<li><B>Verify: </B>Event can be seen now.
	 *<li><B>Step: </B>Click on Overview link in the navigation menu.
	 *<li><B>Verify: </B>The event does not show up under Upcoming Events.
	 *<li><B>Step: </B>Go to the Homepage.
	 *<li><B>Step: </B>Go to the Updates tab.
	 *<li><B>Verify: </B>The event does not appear in the Events widget.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testpastEvents() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		String sPastDate = "10/23/2013";
		
		BaseCommunity community = new BaseCommunity.Builder( testName + Helper.genDateBasedRand())
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .access(defaultAccess)
												   .description("Test " + testName)
												   .build();

		BaseEvent event = new BaseEvent.Builder("Past Event )(*&^%$" + Helper.genDateBasedRand())
									   .build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		
		if (flag) {

		    logger.strongStep("Add the Overview page to the Community and make it the landing page");
		    log.info("INFO: Add the Overview page to the Community and make it the landing page");
		    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		
		}

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				

		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		logger.strongStep("Create a past event");
		event.create(calUI);
		
		logger.strongStep("Edit an event, change its start date and end date to a past date and save the changes");
		log.info("INFO: Edit an event, change its start date and end date to a past date and click on Save button");
		ui.clickLinkWait(calUI.getEventSelector(event));
		ui.clickLinkWait(CalendarUI.Edit);


		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(sPastDate);
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(sPastDate);
		
		ui.clickLinkWait(CalendarUI.EventSubmit);
		ui.clickLinkWait(CalendarUI.BackToCommunityEventsLink);

		logger.strongStep("Verify that the event now appears as a past event and not a future event");
		log.info("INFO: Verify that the event now appears as a past event and not a future event");
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " shows up.");
		
		ui.clickLinkWait(CalendarUI.pastEventLink);
		Assert.assertTrue(driver.isElementPresent(calUI.getEventSelector(event)), "ERROR: " + event.getName() + " doesn't shows up.");
		
		logger.strongStep("Click on Overview link in the navigation menu and verify the event does not show up under Upcoming Events");
		log.info("INFO: Select Overview from the navigation menu and verify the event does not show up under Upcoming Events");
		Community_LeftNav_Menu.OVERVIEW.select(ui);
		
		calUI.fluentWaitTextPresent(Data.getData().upcomingEvents);
		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelectoronCommPage(event)), "ERROR: " + event.getName() + " shows up.");
		
		logger.strongStep("Go to the Homepage then the Updates tab and verify the event does not appear in the Events widget");
		log.info("INFO: Go to the Updates tab of Homepage and verify the event does not appear in the Events widget");
		calUI.loadComponent("homepage", true);
		ui.clickLinkWait(HomepageUIConstants.Updates);
		ui.closeGuidedTourPopup();
		calUI.fluentWaitPresent(HomepageUIConstants.eventsWidget);

		Assert.assertFalse(driver.isElementPresent(calUI.getEventSelectoronHomePage(event))
				, "ERROR: " + event.getName() + " shows up in HP widget.");
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if guests can follow and attend.
	 *<li><B>Step: </B>Create a community, event widget.
	 *<li><B>Step: </B>Add a guest user as the Member.
	 *<li><B>Step: </B>Create a Normal Event.
	 *<li><B>Step: </B>Login as the guest user.
	 *<li><B>Verify: </B>User should be able to attend.
	 *<li><B>Step: </B>Click on 'Follow' link.
	 *<li><B>Verify: </B>The 'Stop Following' button, the follow message and the iCal feed link are displayed.
	 *<li><B>Step: </B>Click on 'Stop Following' link.
	 *<li><B>Verify: </B>The 'Follow' button and the stop follow message are displayed.
	 *<li><B>Step: </B>Click on 'Will Attend' link.
	 *<li><B>Verify: </B>The 'Will Not Attend' button, the attend message, the iCal feed link and the guest's name are displayed.
	 *<li><B>Step: </B>Click on 'Will Not Attend' link.
	 *<li><B>Verify: </B>The 'Will Attend' button, the success message, the stop attend message are displayed and the guest's name is no more displayed.
	 *</ul>
	 */
	@Deprecated //The sc_regression tag is related to smart cloud which is now obsolete.
	@Test(groups = {"sc_regression"}, enabled=false)
	public void testGuestcanFollowandAttend() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
//		User guest = cfg.getUserAllocator().getGuestUser();
		User guest = cfg.getUserAllocator().getGroupUser("guest");
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.description("Test " + testName)
														//.access(Access.RESTRICTED)
														.addexMember(new Member(CommunityRole.MEMBERS, guest))
														.build();		
				
		BaseEvent normalEvent = new BaseEvent.Builder("Normal Event " + Helper.genDateBasedRand())
											 .build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			

		//Verify the calendar view
		logger.strongStep("Create a normal event");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(normalEvent);
		calUI.fluentWaitElementVisible(calUI.getEventSelector(normalEvent));
		
		calUI.close(cfg);
		
		//Load component and login as member
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		calUI.loadComponent(Data.getData().ComponentCommunities);
		calUI.login(guest);
		
		Community_View_Menu.IM_A_MEMBER.select(ui);
		ui.clickLinkWait("link=" + community.getName());

		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		ui.clickLinkWait(calUI.getEventSelector(normalEvent));
		
		//follow works correctly
		ui.clickLinkWait(CalendarUI.EventFollow);
		logger.strongStep("Verify that once the 'Follow' button is clicked, the 'Stop Following' button, the follow message"
				+ " and the iCal feed link are displayed");
		log.info("INFO: Verify that the 'Stop Following' button, the follow message and the iCal feed link are displayed");
		calUI.fluentWaitPresent(CalendarUI.StopFollowing);
		
		Assert.assertTrue(driver.isTextPresent(Data.getData().followmsg), "ERROR: Follow message doesn't show up");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.EventSubscribeToEvent), "ERROR: Event subscribe link doesn't show up");
		
		//unfollow works correctly
		ui.clickLinkWait(CalendarUI.StopFollowing);
		logger.strongStep("Verify that once the 'Stop Following' button is clicked, the 'Follow' button and the stop follow"
				+ " message are displayed");
		log.info("INFO: Verify that the 'Follow' button and the stop follow message are displayed");
		calUI.fluentWaitPresent(CalendarUI.EventFollow);
		
		String actualScopeText = driver.getSingleElement(CalendarUI.Success_Message_Text).getText();
		Assert.assertEquals(actualScopeText,Data.getData().stopFollowMsg, "ERROR: Stop follow message doesn't show up");		
		
		//attend works correctly
		ui.clickLinkWait(CalendarUI.WillAttend);
		logger.strongStep("Verify that once the 'Will Attend' button is clicked, the 'Will Not Attend' button, the attend message"
				+ ", the iCal feed link and the guest's name are displayed");
		log.info("INFO: Verify that the 'Will Not Attend' button, the attend message, the iCal feed link and the attendee's"
				+ " name are displayed");
		calUI.fluentWaitPresent(CalendarUI.WillNotAttend);
		
		Assert.assertTrue(driver.isTextPresent(Data.getData().attendmsg), "ERROR: Attend message doesn't show up");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.EventSubscribeToEvent), "ERROR: Event subscribe link doesn't show up");
		Assert.assertTrue(driver.isElementPresent(calUI.AttendeeList(guest.getDisplayName())), "ERROR: Attendee doesn't show up");
		
		//unattend works correctly		
		ui.clickLinkWait(CalendarUI.WillNotAttend);
		logger.strongStep("Verify that once the 'Will Not Attend' button is clicked, the 'Will Attend' button, the success "
				+ "message, the stop attend message are displayed and the guest's name is no more displayed");
		log.info("INFO: Verify that the 'Will Attend' button, the success message, the stop attend message are displayed and"
				+ " the guest's name is no more displayed");
		calUI.fluentWaitPresent(CalendarUI.WillAttend);
		
		Assert.assertEquals(actualScopeText,Data.getData().stopAttendMsg, "ERROR: Stop attend message doesn't show up");		
		Assert.assertFalse(driver.isElementPresent(calUI.AttendeeList(guest.getDisplayName())), "ERROR: Attendee still shows up");		
		
		calUI.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the Page Change between communities.
	 *<li><B>Step: </B>Create a community, event widget.
	 *<li><B>Step: </B>Create a normal event and a special event with special name.
	 *<li><B>Verify: </B>The events should be displayed on the Events page.
	 *<li><B>Step: </B>Go to Members page.
	 *<li><B>Step: </B>Go back to Events page.
	 *<li><B>Verify: </B>The event detail page shows correctly for each event.
	 */
	@Test(groups = {"regression"})
	public void testPageChangeBetweenCommunities() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		BaseEvent Event2 = new BaseEvent.Builder("javascript:dojo.byId('receiver_0')._onclick();").build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Create the first and then the second event and verify that each can be seen on the Events page");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.fluentWaitElementVisible(calUI.getEventSelector(Event1));
		calUI.create(Event2);
		calUI.fluentWaitElementVisible(calUI.getEventSelectoronCommPage(Event2));
		
		ui.gotoMembers();
		calUI.fluentWaitTextPresent("Members");

		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		logger.strongStep("Verify that the events can be opened from the Events page");
		log.info("INFO: Verify that the events can be opened using their links on Events page");
		calUI.clickLinkWait(calUI.getEventSelectoronCommPage(Event2));
		Assert.assertTrue(driver.isTextPresent(Event2.getName()), "ERROR: " + Event2.getName() + "doesn't show up");
		calUI.gotoBackCommunityEvents();
		calUI.clickLinkWait(calUI.getEventSelector(Event1));
		Assert.assertTrue(driver.isTextPresent(Event1.getName()), "ERROR: " + Event1.getName() + "doesn't show up");
		
		calUI.endTest();
	}
		
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the search event UI.
	 *<li><B>Step: </B>Create a community.
	 *<li><B>Step: </B>Go to searchbox.
	 *<li><B>Step: </B>Input anything.
	 *<li><B>Verify: </B>Search tab for Calendar shows up.
	 *<li><B>Step: </B>Click on the search tab.
	 *<li><B>Verify: </B>No search result message is displayed.
	 *<li><B>Step: </B>Start indexing and wait for data pop to complete.
	 *<li><B>Step: </B>Perform the search.
	 *<li><B>Verify: </B>The event name should show up.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testSearchEventUI() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		long dataPopCompletionTime;
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);		
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		logger.strongStep("Searching in this community");
		log.info("INFO: Searching in this community");
		driver.getSingleElement(GlobalsearchUI.OpenSearchPanel).click();
		driver.getSingleElement(GlobalsearchUI.TextAreaInPanel).type("search");
		ui.clickLinkWait(GlobalsearchUI.ThisCommunityLink);
		
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CalendarSearchTab), "ERROR: calendar search tag doesn't show up");
		String sURL = driver.getCurrentUrl();
		ui.clickLinkWait(CommunitiesUIConstants.CalendarSearchTab);
		
		Assert.assertTrue(driver.isTextPresent(Data.getData().NoSearchResult), "ERROR: no search result msg doesn't show up");
		Assert.assertTrue(driver.isTextPresent(Data.getData().SearchIndexInfo), "ERROR: search index info doesn't show up");
		
		//check search index for Events.
		log.info("INFO: Start indexing....");
		BaseEvent Event = new BaseEvent.Builder("Search Event " + dateTime)
										.build();
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		logger.strongStep("Create an event");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event);
		calUI.fluentWaitElementVisible(calUI.getEventSelector(Event));		
		calUI.close(cfg);
		
		dataPopCompletionTime = System.currentTimeMillis();		
		searchui.waitForIndexer(testUser, dataPopCompletionTime);	
		
		driver.load(sURL, true);
		logger.strongStep("Start the search in this community and verify the event shows up");
		log.info("INFO: Searching in this community");
		ui.clickLinkWithJavascript(GlobalsearchUI.OpenSearchPanel);
		ui.fluentWaitElementVisible(GlobalsearchUI.TextAreaInPanel);
		driver.getSingleElement(GlobalsearchUI.TextAreaInPanel).type("Search Event");
		ui.clickLinkWithJavascript(GlobalsearchUI.ThisCommunityLink);
		ui.waitForPageLoaded(driver);
		
		Assert.assertTrue(driver.getFirstElement(calUI.getEventSelector(Event)).isVisible(), "ERROR: " + Event.getName() + "doesn't show up");
		ui.clickLinkWait(CommunitiesUIConstants.CalendarSearchTab);
		Assert.assertTrue(driver.getVisibleElements(calUI.getEventSelector(Event)).size()==1, "ERROR: " + Event.getName() + "doesn't show up");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if mention works correctly in entry for display name.
	 *<li><B>Step: </B>Create a community
	 *<li><B>Step: </B>add event widget.
	 *<li><B>Step: </B>Create a event.
	 **<li><B>Step: </B>Edit the event and add the mention for the user.
	 *<li><B>Verify: </B>Mention works correctly in entry for display name.
	 *<li><B>Step: </B>Add some text apart from the mention and save the changes.
	 *<li><B>Verify: </B>The complete text should be displayed correctly as the description of the Event.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testEntryMention_Basic_DisplayName() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		String sDisplayName = testUser.getDisplayName();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		logger.strongStep("Creating an event");
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.Edit);
		driver.getSingleElement(CalendarUI.Edit).click();
		
		logger.strongStep("Start typing @mention");
		log.info("INFO: start input @mention");
		typeInDescriptionBox("@" + sDisplayName.substring(0, 2));
		Assert.assertTrue(driver.isElementPresent(calUI.mentionTypeahead()), "ERROR: mention typahead doesn't show");
		typeInDescriptionBox(sDisplayName.substring(2));
		calUI.mention_addMember(sDisplayName);
		ui.switchToFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		Assert.assertTrue(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText().equals("@" + sDisplayName)
				, "ERROR: mention text is not correct.");
		ui.switchToTopFrame();
		typeInDescriptionBox("test");
		logger.strongStep("Save and verify that the changes reflect");
		log.info("INFO: Click on Save button and verify the text appears correctly as the description for the event");
		driver.getVisibleElements(CalendarUI.SaveButton).get(0).click();
		Assert.assertTrue(driver.getSingleElement("css=div[dojoattachpoint='description']").getText().equals("@" + sDisplayName + "test")
				, "ERROR: mention content is incorrect");
		
		calUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if the all day check box, when checked, will not hide the repeat link
	 *<li><B>Step: </B>Create a public community
	 *<li><B>Step: </B>Add event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Verify: </B>When the All-day Event checkbox is selected, the repeat link continues to be visible.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testAllDayCheckboxWillNotHideRepeatLink() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
						
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community.
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Select the 'All-day event' checkbox and verify the Repeats link does not disappear");
		log.info("INFO: Create and event, select the 'All-day event' checkbox and verify the Repeats link does not disappear");
		calUI.clickLinkWait(CalendarUI.CreateEvent);
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventRepeats).isVisible(), "ERROR: repeat link doesn't show");
		calUI.clickLinkWait(CalendarUI.EventAllDay);
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventRepeats).isVisible(), "ERROR: repeat link doesn't show");
				
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests cross day events.
	 *<li><B>Step: </B>Create a public community.
	 *<li><B>Step: </B>Add event widget.
	 *<li><B>Step: </B>Create a cross day event.
	 *<li><B>Verify: </B>When the first half of the event is clicked upon, both halves are highlighted.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testCrossDayEvent() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();			
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		logger.strongStep("Create an event, get the start date and then click on Cancel");
		calUI.clickLinkWait(CalendarUI.CreateEvent);
		String sdate = driver.getSingleElement(CalendarUI.EventStartDate).getAttribute("value");
		calUI.clickLinkWithJavascript(CalendarUI.CancelButton);

		Date startDt = Datafmt.parse(sdate);
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(startDt);
		Calendar endCal = (Calendar) startCal.clone();
		endCal.add(Calendar.DATE, 1);
		log.info("INFO: Event start date is: " + Datafmt.format(startCal.getTime()));
		log.info("INFO: Event end date is: " + Datafmt.format(endCal.getTime()));

		BaseEvent normalEvent = new BaseEvent.Builder("Cross Day Event " + Helper.genDateBasedRand())
											 .endDate(endCal)
											 .startDate(startCal)
											 .startTime("2:00 AM")
											 .endTime("1:00 AM")
											 .useCalPick(false)
											 .build();
		
		logger.strongStep("Create a normal event");
		normalEvent.create(calUI);
		calUI.fluentWaitElementVisible(calUI.getEventSelector(normalEvent));
		logger.strongStep("Switch to the Calendar View and verify the entry there");
		log.info("INFO: Click on the Calendar View tab and verify the entry in the Calendar");
		ui.clickLinkWait(CalendarUI.EventGridTab);
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarViewEntry).size(), 2
				, "ERROR: calendar view entry is incorrect");
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarViewEntrySelected).size(), 0
				, "ERROR: calendar view selected entry is incorrect");
		logger.strongStep("Click on the first entry and verify that the entries from both days are selected");
		log.info("INFO: Verify that clicking on the first entry selects entries from both days");
		driver.getElements(CalendarUI.CalendarViewEntry).get(0).click();
		Assert.assertEquals(driver.getElements(CalendarUI.CalendarViewEntrySelected).size(), 2
				, "ERROR: calendar view selected entry is incorrect");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can follow a community event.
	 *<li><B>Step: </B>Create a community via API.
	 *<li><B>Step: </B>Add the event (calendar) widget and verify the view UI.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Open the event and select option: Follow.
	 *<li><B>Verify: </B>The button text changes to Stop Following and status message displays along with iCal feed link.
	 *<li><B>Step: </B>Click on iCal feed link within the status message.
	 *<li><B>Verify: </B>Add to Personal Calendar dialog displays containing the link to add to personal calendar.
	 *<li><B>Step: </B>Click OK in the dialog box.
	 *<li><B>Verify: </B>The status message can be dismissed via the X close button.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void followCommunityEvent() throws Exception{
		
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

		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		//Verify the calendar view
		logger.strongStep("Verify the Events page contains the 'Create an Event' button, the 'Events' and 'Calendar View' tabs");
		log.info("INFO: Verify Events UI contains the create button");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent),
				"ERROR: 'Create an Event' button not found.");
		log.info("INFO: Verify Events UI contains the events tab");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab),
				"ERROR: 'Events' tab not found: " + CalendarUI.EventListTab);
		log.info("INFO: Verify Events UI contains the calendar view tab");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab),
				"ERROR: 'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		//Create an Event
		logger.strongStep("Create a new event");
		log.info("INFO: Create a new event");
		event.create(calUI);

		//Open the Event
		logger.strongStep("Open the Event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		//verify user follows the event.
		logger.strongStep("Follow the event by clicking on the Follow button and verify the button text changes to Stop Following");
		log.info("INFO: Select Follow button");
		ui.clickLinkWait(CalendarUI.EventFollow);
		log.info("INFO: Verify button text changes to Stop Following");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventStopFollowOption),
				"ERROR: 'Stop Following' button not found.");
		
		log.info("INFO: Verify Following status message displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().followmsg),
				"ERROR: Follow message not found.");
		
		logger.strongStep("Verify iCal feed link displays within the status message");
		log.info("INFO: Verify iCal feed link displays within the status message");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventSubscribeToEvent),
				"ERROR: Event subscribe link not found.");
		
		log.info("INFO: Select iCal feed link within the status message to launch Add to Personal Calendar dialog");
		ui.clickLinkWait(CalendarUI.EventSubscribeToEvent);
		
		logger.strongStep("Verify Add to Personal Calendar dialog displays containing the link to add to personal calendar");
		log.info("INFO: Verify Add to Personal Calendar dialog displays");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.AddtoPersonalCalDia),
				"ERROR: Add to Personal Calendar dialog not found.");
		
		log.info("INFO: Verify link to add to personal calendar displays within the dialog");
		Assert.assertTrue(driver.isElementPresent(calUI.EventSubscribeWebcalLink()),
				"ERROR: Add to personal calendar link not found.");
		
		logger.strongStep("Select OK in Add to Personal Calendar dialog");
		log.info("INFO: Select OK in Add to Personal Calendar dialog");
		ui.clickLinkWait(HomepageUIConstants.AddtoPersonalCalOkButton);

		//verify user can dismiss the status message
		logger.strongStep("Click Close (X) button in the status message and verify the status message disappears");
		log.info("INFO: Click Close (X) button in the status message");
		ui.clickLinkWait(CalendarUI.MsgBoxcloseButton);
		log.info("INFO: Verify the status message dismisses");		
		Assert.assertFalse(driver.isElementPresent(CalendarUI.SuccessMsgBox),
				"ERROR: message box still shows up after click close button.");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can notify other people.
	 *<li><B>Step: </B>Create a community with another member via the API.
	 *<li><B>Step: </B>Add the event (calendar) widget and create an all-day event.
	 *<li><B>Step: </B>Open the event and select option: More Actions > Notify Other People.
	 *<li><B>Verify: </B>The Notify Other People dialog box displays.
	 *<li><B>Verify: </B>The list only shows the member and not the owner.
	 *<li><B>Step: </B>Send notification to the community member.
	 *<li><B>Verify: </B>A status message indicating the notification was sent appears.
	 *<li><B>Step: </B>Close the status message by clicking on Close (X) button.
	 *<li><B>Verify: </B>The status message indicating the notification was sent disappears.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void notifyOtherPeople() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = calUI.startTest();
		
		User comMem = cfg.getUserAllocator().getUser();
		String sDisplayName1 = testUser.getDisplayName();
		String sDisplayName2 = comMem.getDisplayName();	
		String sname = "";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
												   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												   .description("Test Calendar for " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, comMem))
												   .build();
		
		//Create events		
		BaseEvent event = new BaseEvent.Builder("AllDayEvent" + Helper.genDateBasedRand())
									   .allDayEvent(true)
									   .build();
		
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//create event
		logger.strongStep("Creating an all day event");
		log.info("INFO: Create an all day event");
		event.create(calUI);
		
		//goto the event detail page -> more actions button -> notify other people link.
		logger.strongStep("Open the Event");
		log.info("INFO: Open event " + event.getName());
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		logger.strongStep("Launch Notify Other People dialog and validate the dialog box displays");
		log.info("INFO: Launch Notify Other People dialog");
		Events_MoreActions_Menu.NOTIFY_OTHER_PEOPLE.select(calUI);
		
		log.info("INFO: Validate Notify Other People dialog displays");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.NotifyOtherPeopleDialog),
				"ERROR: Notify Other People dialog doesn't display");
		
		logger.strongStep("Collect the list of user display names and verify the notify people list has one member");
		log.info("INFO: Collect the list of user display names");
		driver.getSingleElement(CalendarUI.NotifyOtherPeopleMsg).type("/n" + community.getName() + "/n" + event.getName());
		
		log.info("INFO: Verify notify people list is not empty");
		Assert.assertEquals(driver.getElements(CalendarUI.NotifyOtherPeopleList).size(),1,
				"ERROR: Notify people list is empty.");
		
		logger.strongStep("Verify the community member displays in the notify people list");
		log.info("INFO: Compare the user display name to the list");
		sname = driver.getElements(CalendarUI.NotifyOtherPeopleList + " label").get(0).getText();
		log.info("INFO: Verify member " +sDisplayName2 +" displays in the notify people list");
		Assert.assertEquals(sname, sDisplayName2, "ERROR: the display name is not the same in notify people list");
		
		logger.strongStep("Validate the owner does not appear in the list");
		driver.getSingleElement(CalendarUI.NotifyOtherPeopleInput).type(sDisplayName1);
		log.info("INFO: Verify owner doesn't display in the notify people list");
		Assert.assertEquals(driver.getElements(CalendarUI.NotifyOtherPeopleList).size(),0,
				"ERROR: the notify people list contains owner.");
		
		logger.strongStep("Clear the 'Select community members' text box and type the name of the community member");
		log.info("INFO: Clear and type the name of " +sDisplayName2);
		driver.getSingleElement(CalendarUI.NotifyOtherPeopleInput).clear();
		driver.getSingleElement(CalendarUI.NotifyOtherPeopleInput).type(sDisplayName2);
		/*Assert.assertEquals(driver.getElements(CalendarUI.NotifyOtherPeopleList).size(),1,
				"ERROR: the notify people list doesn't contain member.");*/

		//click the checkbox for the people selected, then click remove it.
		logger.strongStep("Selecting checkbox for the user to add to notify list");
		log.info("INFO: Select checkbox for the user to add to notify list");
		Element notifyCheckbox = ui.getFirstVisibleElement(CalendarUI.NotifyOtherPeopleListCheckbox);
		notifyCheckbox.click();
		
		logger.strongStep("Removing the selected user from the notify list");
		log.info("INFO: Remove the selected user from the notify list");
		ui.clickLinkWait(CalendarUI.NotifyOtherpeopleReceiverLink);
		Assert.assertFalse(driver.getSingleElement(CalendarUI.NotifyOtherPeopleReceiver).isVisible(),
				"ERROR: the selected people to notify still shows up.");
		Assert.assertFalse(notifyCheckbox.isSelected(),
				"ERROR: the people is still selected.");
		Assert.assertFalse(driver.getSingleElement(CalendarUI.NotifyOtherPeopleOKBtn).isEnabled(),
				"ERROR: the OK button for notification still clickable." );

		logger.strongStep("Selecting checkbox for the user once again and sending notification");
		log.info("INFO: Re-select checkbox for the user and notify");
		notifyCheckbox.click();
		ui.clickLinkWait(CalendarUI.NotifyOtherPeopleOKBtn);
		calUI.fluentWaitPresent(CalendarUI.SuccessMsgBox);
		logger.strongStep("Verify status message displays indicating the notification was sent");
		log.info("INFO: Verify status message displays indicating the notification was sent");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.SuccessMsgBox),
				"ERROR: status message doesn't display");

		logger.strongStep("Close the status message by clicking on Close (X) button");
		log.info("INFO: Click Close (X) button in the status message");
		ui.clickLinkWait(CalendarUI.MsgBoxcloseButton);
		log.info("INFO: Verify the status message dismisses");	
		Assert.assertFalse(driver.isElementPresent(CalendarUI.SuccessMsgBox),
				"ERROR: status message still displays");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can attend a community event
	 *<li><B>Step: </B>Create a community.
	 *<li><B>Step: </B>Add a calendar widget to the community.
	 *<li><B>Step: </B>Navigate to Events from the navigation menu.
	 *<li><B>Verify: </B>The Events page contains the 'Create an Event' button, the 'Events' and 'Calendar View' tabs
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Click on the event link.
	 *<li><B>Verify: </B>The current user should appear in the attendee list.
	 *<li><B>Step: </B>Click on "Will Not Attend" button.
	 *<li><B>Verify: </B>The 'Will Attend' button, the success message, the stop attend message are displayed and the guest's name is no more displayed.
	 *<li><B>Step: </B>Click on "Will attend" button.
	 *<li><B>Verify: </B>The Attending status message and iCal Feed link are displayed.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void attendCommunityEvent() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);		

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		logger.strongStep("Verify the Events page contains the 'Create an Event' button, the 'Events' and 'Calendar View' tabs");
		log.info("INFO: Verify the Events page contains the 'Create an Event' button, the 'Events' and 'Calendar View' tabs");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent),  
							"ERROR: 'Create an Event' button not found.");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab), 
							"ERROR: 'Events' tab not found: " + CalendarUI.EventListTab);
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab), 
							"ERROR: 'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		//Create an Event
		logger.strongStep("Creating an event");
		log.info("INFO: Create a new event");
		event.create(calUI);

		//Open the Event
		logger.strongStep("Select the event and verify the user's name appears in the attendee list");
		log.info("INFO: Click on the event link and verify the user's name appears in the attendee list");
		ui.clickLinkWait(calUI.getEventSelector(event));
		
		//Verify user has attended the event.
		Assert.assertTrue(driver.isElementPresent(calUI.AttendeeList(testUser.getDisplayName())),
				"ERROR: " + testUser.getDisplayName() + " doesn't shows in attendee list.");
		
		//verify user stop attending this event.
		logger.strongStep("Verify that once the 'Will Not Attend' button is clicked, the 'Will Attend' button, the success "
				+ "message, the stop attend message are displayed and the guest's name is no more displayed");
		log.info("INFO: Verify that the 'Will Attend' button, the success message, the stop attend message are displayed and"
				+ " the guest's name is no more displayed");
		ui.clickLinkWait(CalendarUI.WillNotAttend);
		ui.fluentWaitPresent(CalendarUI.WillAttend);
		String actualScopeText = driver.getSingleElement(CalendarUI.SuccessMsgBox).getText();
		Assert.assertEquals(actualScopeText,Data.getData().stopAttendMsg,
				"stop attending message doesn't show up correctly");
		
		//Verify people attending list
		Assert.assertFalse(driver.isElementPresent(calUI.AttendeeList(testUser.getDisplayName())),
				"ERROR: " + testUser.getDisplayName() + " shows in attendee list.");
			
		//Reset status to attending
		logger.strongStep("Click on the 'Will Attend link'");
		log.info("INFO: Select Will Attend button");
		ui.clickLinkWait(CalendarUI.WillAttend);
		
		//Verify status message
		logger.strongStep("Verify Attending status message and iCal Feed link display");
		log.info("INFO: Verify Attending status message displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().attendmsg), "ERROR: Attend message not found.");
		log.info("INFO: Verify iCal Feed link displays");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.EventSubscribeToEvent), "ERROR: iCal Feed link not found");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the operation of an event comment.
	 *<li><B>Step: </B>Create a new community and add the Event widget.
	 *<li><B>Step: </B>Create a community event.
	 *<li><B>Step: </B>Add a comment to the event.
	 *<li><B>Verify: </B>The comment can be added successfully.
	 *<li><B>Step: </B>Add a new comment to the event.
	 *<li><B>Verify: </B>The comment can be deleted successfully.
	 *<li><B>Verify: </B>The comment should not be visible anymore.
	 *<li><B>Step: </B>Create a community event with repeats.
	 *<li><B>Step: </B>Add a comment for the repeating event.
	 *<li><B>Verify: </B>The comments for repeating events only apply to instance, instead of the whole series.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void eventCommentOperation() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent eventA = new BaseEvent.Builder(testName + "A" + Helper.genDateBasedRand())
									    .tags(Data.getData().commonTag)
									    .description(Data.getData().commonDescription)
									    .build();
		
		//Create a event base state object
		BaseEvent eventB = new BaseEvent.Builder(testName + "B" + Helper.genDateBasedRand())
									    .tags("BVTEvent")
									    .repeat(true)
									    .description("This event will be held in the Dublin Lab")
									    .build();
		
		String expectedText;
		String comment1 = "This is a comment" + 1;
		String comment2 = "This is a comment" + 2;
		String comment2uuid;
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);		
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		ui.fluentWaitPresent(CalendarUI.CreateEvent);

		//Create an Event
		logger.strongStep("Creating an event");
		log.info("INFO: Create a new event");
		eventA.create(calUI);

		//Open the Event
		logger.strongStep("Opening the event");
		log.info("INFO: Open the event");
		ui.clickLinkWait(calUI.getEventSelector(eventA));
		
		//Get EventWidget id
		List<Element> eventPanel = driver.getElements(CalendarUI.fullPageZone);
		String eventWidgetId = eventPanel.get(0).getAttribute("id");
		
		//Verify comment can be added successfully
		logger.strongStep("Add a comment and verify that it was added successfully");
		log.info("INFO: Add a comment and verify that it was added successfully");
		ui.clickLinkWait(CalendarUI.AddAComment);
		calUI.addCommentToEvent(comment1);
		ui.fluentWaitTextPresent(comment1);
		
		int commentCount = calUI.getCommentList("css=#"+ eventWidgetId +"_commentList>ul>li").size();
		Assert.assertEquals(commentCount,1, "the comment is not added successfully.");
		
		//Add a new comment and verify the new comment can be delete successfully
		logger.strongStep("Add a new comment and verify that it can be deleted");
		log.info("INFO: Add a new comment and verify that it can be deleted");
		ui.clickLinkWait(CalendarUI.AddAComment);
		calUI.addCommentToEvent(comment2);
		ui.fluentWaitTextPresent(comment2);
		comment2uuid = calUI.getCommentList("css=#"+ eventWidgetId +"_commentList>ul>li").get(1).getAttribute("uuid");

		ui.clickLinkWait("css=a[uuid="+ comment2uuid +"]:contains(Delete)");
		ui.clickLinkWait(CalendarUI.ConfirmDialogDeleteButton);
		
		logger.strongStep("Verify the comment is not visible anymore");
		log.info("INFO: Checking that the comment does not appear anymore");
		calUI.fluentWaitTextNotPresent(comment2);
		commentCount = calUI.getCommentList("css=#"+ eventWidgetId +"_commentList>ul>li").size();
		Assert.assertEquals(commentCount,1, "the comment is not deleted successfully.");
		
		//Use go back to community events link
		logger.strongStep("Go to the Events page using 'Back to commuity events' link");
		log.info("INFO: Use go back to community events link");
		calUI.gotoBackCommunityEvents();

		//Create a repeating event
		logger.strongStep("Creating a repeating event");
		log.info("INFO: Create a new event that is repeating");
		eventB.create(calUI);

		//Verify Event is created
		logger.strongStep("Verifying the repeating event has been created");
		ui.fluentWaitTextPresent(eventB.getName());
		
		//Add comment in today event
		logger.strongStep("Retrieve the new event, add a comment to it and verify the comment has been added");
		log.info("INFO: Get the new event, add a comment to it and verify the comment has been added");
		List<Element> eventList = driver.getElements(calUI.getEventSelector(eventB));
		eventList.get(0).click();
		
		//add the comment
		ui.clickLinkWait(CalendarUI.AddAComment);
		calUI.addCommentToEvent("This is a comment");
		ui.fluentWaitTextPresent("This is a comment");
		
		//Use go back to community events link
		logger.strongStep("Go to the Events page using 'Back to commuity events' link");
		log.info("INFO: Use go back to community events link");
		calUI.gotoBackCommunityEvents();
		
		//switch to event
		ui.fluentWaitPresent(calUI.getEventSelector(eventA));
		logger.strongStep("Go to an instance of the repeating event different from the one for which the comment was added");
		log.info("INFO: Click on an instance of the repeating event different from the one for which the comment was added");
		eventList = driver.getElements(calUI.getEventSelector(eventB));
		eventList.get(2).click();
		
		//Verify no comment in tomorrow event
		logger.strongStep("Verify the comment does not appear for the instance of the repeating event");
		log.info("INFO: Check that the comment does not appear for the instance of the repeating event");
		ui.fluentWaitTextPresent(Data.getData().NoCommentMsg);
		expectedText = driver.getSingleElement("css=#eventcomments_"+ eventWidgetId +"_label").getText();
		Assert.assertEquals("Comments (0)", expectedText, "ERROR: the comment shows up in other repeating events.");

		ui.endTest();
		
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the operation of an all day event.
	 *<li><B>Step: </B>Create a new community and add the Event Widget.
	 *<li><B>Step: </B>Switch to Calendar View.
	 *<li><B>Step: </B>Change to Two Days view.
	 *<li><B>Step: </B>Create an all day event.
	 *<li><B>Verify: </B>The calendar view should not change.
	 *<li><B>Step: </B>Create a new event and change it to an all day event.
	 *<li><B>Verify: </B>The two events should appear in the Calendar.
	 *<li><B>Step: </B>Create another normal event.
	 *<li><B>Step: </B>Change it to an all day event and change the start date to the next day.
	 *<li><B>Verify: </B>The event should still be there in the Calendar.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void allDayEventOperation() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		//Create a event base state object
		BaseEvent eventA = new BaseEvent.Builder(testName + "A" + Helper.genDateBasedRand())
									   .tags("BVTEvent")
									   .allDayEvent(true)
									   .description(Data.getData().commonDescription)
									   .build();

		//Create a event base state object
		BaseEvent eventB = new BaseEvent.Builder(testName + "B" + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		//Create a event base state object
		BaseEvent eventC = new BaseEvent.Builder(testName + "C" + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community.
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);		
		
		//Click on the Events link in the nav
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		ui.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//Change to Two Days view
		logger.strongStep("Switch to Calendar View tab then select the Two Day option view");
		log.info("INFO: Click on the Calendar View tab then select the Two Day option view");
		ui.clickLinkWait(CalendarUI.EventGridTab);
		ui.clickLinkWait(CalendarUI.viewPickerImg);
		
		driver.getSingleElement(CalendarUI.CalendarView_TwoDay).click();
		
		//Create an all day event and verify calendar view not change
		logger.strongStep("Create an all day event and verify that the Calendar View does not change");
		log.info("INFO: Create an all day event and verify that the Calendar View remains the same");
		String initialNavigatorText = driver.getSingleElement(CalendarUI.initialNavigatorText).getText();
		eventA.create(calUI);

		ui.fluentWaitTextPresent(driver.getSingleElement(CalendarUI.initialNavigatorText).getText());
		String navigatorText = driver.getSingleElement(CalendarUI.initialNavigatorText).getText();
		Assert.assertEquals(initialNavigatorText,navigatorText, "calendar view is changed after create an all day event.");
		
		//Create a simple event and change it to an all day event, verify two events will not disappear.
		logger.strongStep("Create a new event");
		log.info("INFO: Create a new event");
		eventB.create(calUI);
		
		//Verify event created successfully
		ui.fluentWaitTextPresent(eventB.getName());
		
		//Edit Event and make all day Event
		logger.strongStep("Edit the new event and make it an all day event");
		log.info("INFO: Edit the event and make it an all day event");
		driver.getElements("css=div.s-cv-entry-innerframe:contains(" + eventB.getName() + ")").get(0).click();
		ui.clickLinkWait(CalendarUI.EditEventLink);
		driver.getSingleElement(CalendarUI.EventAllDay).click();
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		logger.strongStep("Verify that both events appear in the Calendar");
		log.info("INFO: Verify that both events are available in the Calendar");
		ui.fluentWaitTextPresent(eventB.getName());
		Assert.assertTrue(driver.getElements("css=div.s-cv-entry-innerframe:contains(" + eventB.getName() + ")").size()>0, eventB.getName() + " doesn't show up.");
		
		Assert.assertTrue(driver.getElements("css=div.s-cv-entry-innerframe:contains(" + eventA.getName() + ")").size()>0, eventA.getName() + " doesn't show up.");
		
		//Change normal event to all day event and change the date to the next day,verify the event still there.
		logger.strongStep("Create a new event and verify that it appears in the Calendar");
		log.info("INFO: Create an event and verify that the event can be seen in the Calendar");
		eventC.create(calUI);
		
		//Verify event created successfully
		ui.fluentWaitTextPresent(eventC.getName());	
		
		logger.strongStep("Click on the event, edit it and change it to an all day event");
		log.info("INFO: Edit the event and change it to an all day event");
		driver.getElements("css=div.s-cv-entry-innerframe:contains("+ eventC.getName() +")").get(0).click();
		ui.clickLinkWait(CalendarUI.EditEventLink);
		driver.getSingleElement(CalendarUI.EventAllDay).click();
		
		String todayDate = driver.getSingleElement(CalendarUI.EventStartDate).getAttribute("value");

		String nextDate = null;
		SimpleDateFormat fmt = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		Date d = fmt.parse(todayDate);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 1);
		d = c.getTime();
		nextDate = fmt.format(d);
		
		logger.strongStep("Change the start date to the next day and verify the event is still there in the Calendar");
		log.info("INFO: Change the start date to the next day and verify the event is still there in the Calendar");
		driver.getSingleElement(CalendarUI.EventStartDate).clear();
		driver.getSingleElement(CalendarUI.EventStartDate).type(nextDate);
		
		ui.clickLinkWait(CalendarUI.EventSubmit);
		ui.fluentWaitTextPresent(eventC.getName());
		Assert.assertTrue(driver.getElements("css=div.s-cv-entry-innerframe:contains(" + eventC.getName() + ")").size()>0, eventC.getName() + " doesn't show up.");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to create repeated events by monthly on Day.
	 *<li><B>Step: </B>Create a new community and add the Event widget.
	 *<li><B>Step: </B>Create a monthly repeating event from today to 8 month later.
	 *<li><B>Step: </B>Select "repeat event" and chose repeat event on every 2nd Tuesday.
	 *<li><B>Verify: </B>The number of event entries should be equal to 8 or the span of the event in number of months.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void TestEventRepeatedByMonthOnDay() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		int numberofMonth = 7;

		Calendar startDate = Calendar.getInstance();
		Calendar repeatedUntilDate = Calendar.getInstance();
		repeatedUntilDate.add(Calendar.MONTH, numberofMonth);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test for Calendar " + testName)
														.build();
		
		BaseEvent event = new BaseEvent.Builder("Repeating Event" + Helper.genDateBasedRand())
											.tags(Data.getData().commonTag)
											.startDate(startDate)
											.endDate(startDate)
											.repeatUntil(repeatedUntilDate)
											.description(Data.getData().commonDescription)
											.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
					" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);	
		
		// Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//create events
		logger.strongStep("Create an event and add the title, tag, start date and end date");
		log.info("INFO: Creating an event and providing details like the title, tag, start date and end date");
		ui.clickLinkWait(CalendarUI.CreateEvent);
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());
		driver.getSingleElement(CalendarUI.EventTag).clear();
		driver.getSingleElement(CalendarUI.EventTag).type(event.getTags());
		
		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(event.getStartDateText());
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(event.getEndDateText());
	
		logger.strongStep("Make the event a monthly repeating event and select the repeat day as the second Tuesday");
		log.info("INFO: Turn on repeats for the event and select the repeat day as the second Tuesday");
		ui.clickLinkWait(CalendarUI.EventRepeats);
		calUI.selectComboValue(CalendarUI.EventRepeatFreq, "Monthly");
		Assert.assertTrue(calUI.getRepeatFrequentOption().equals("Monthly"), "ERROR: default repeating selection is not equal to monthly.");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventCheckMonthlyByDate).isEnabled(),"ERROR: date selection is not enabled by deafult.");
		
		ui.clickLinkWait(CalendarUI.EventCheckMonthlyByDay);   
	    Assert.assertFalse(driver.getSingleElement(CalendarUI.EventMonthlyByDateValue).isEnabled(),"ERROR: date selection is enabled when switch to byDay");
	    Assert.assertTrue(driver.getSingleElement(CalendarUI.EventMonthlyByDateTypeWeek).isEnabled(),"ERROR: week selection is not enabled after switch to byDay");
	    Assert.assertTrue(driver.getSingleElement(CalendarUI.EventMonthlyByDatetypeDay).isEnabled(),"ERROR: day selection is not enabled after switch to byDay");
	    log.info("INFO: Change event repeat option to event 2nd Tuesday monthly");
	    calUI.selectComboValue(CalendarUI.EventMonthlyByDateTypeWeek, "the 2nd");
	    calUI.selectComboValue(CalendarUI.EventMonthlyByDatetypeDay, "Tue");
	    
	    logger.strongStep("Select the Until date as a date that comes seven months from the current date");
	    log.info("INFO: Add seven months to the current date and make it the Until date");
	    driver.getSingleElement(CalendarUI.EventEditorUntilDate).clear();
	    driver.getSingleElement(CalendarUI.EventEditorUntilDate).type(event.getRepeatUntilText());
	    driver.typeNative(Keys.TAB);
	    
	    driver.getSingleElement(CalendarUI.EventLocation).clear();
		driver.getSingleElement(CalendarUI.EventLocation).type(event.getLocation());
		
		logger.strongStep("Submit the details by clicking on the Save button");
		log.info("INFO: Click on the Save button and submit the details");
	    calUI.typeNativeInCkEditor(event.getDescription());
	    ui.clickLinkWait(CalendarUI.EventSubmit);
		
		//check events result on event list
	    logger.strongStep("Verify that the number of event entries is equal to 8 or the span of the event in number of months");
	    log.info("INFO: Verify that the number of event entries is equal to 8 or the span of the event in number of months");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(event)).size()>0, "ERROR: " + event.getName() + " doesn't show up");
		List<Element> eventList = driver.getElements(CalendarUI.EventList);
		Assert.assertEquals(eventList.size(), numberofMonth+1, "ERROR: not all events show up");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to create repeated events by monthly on Date.
	 *<li><B>Step: </B>Create a new community and add the Event widget.
	 *<li><B>Step: </B>Create a monthly repeated event 
	 *<li><B>Step: </B>Select the 31st as the repeat date for every month.
	 *<li><B>Verify: </B>Verify warning message for "month will be skipped" will shows up.
	 *<li><B>Step: </B>Change the repeat date to 21st and repeat until 7 month after.
	 *<li><B>Verify: </B>Verify the warning message for "month will be skipped" disappears.
	 *<li><B>Step: </B>Submit the details by clicking on the Save button.
	 *<li><B>Verify: </B>Verify each event from the event list appears on the Events page.
	 *</ul>
	 */
	@Test(groups = {"regression", "bvtcloud"})
	public void TestEventRepeatedByMonthOnDate() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();
		
		int numberofMonth = 7;
		Calendar startDate = Calendar.getInstance();
		Calendar repeatedUntilDate = Calendar.getInstance();
		repeatedUntilDate.add(Calendar.MONTH, numberofMonth);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();
		
		BaseEvent event = new BaseEvent.Builder("Repeating Event" + Helper.genDateBasedRand())
										.tags(Data.getData().commonTag)
										.startDate(startDate)
										.endDate(startDate)
										.repeatUntil(repeatedUntilDate)
										.description(Data.getData().commonDescription)
										.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the events widget to the community
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open calendar and login as member: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);	
		
		// Verify the calendar view
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		
		//create events
		logger.strongStep("Create an event repeating on the 21th of each month.");
		log.info("INFO: Create a monthly repeated event on every 21th of the month");
		ui.clickLinkWait(CalendarUI.CreateEvent);
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());
		driver.getSingleElement(CalendarUI.EventTag).clear();
		driver.getSingleElement(CalendarUI.EventTag).type(event.getTags());
				
		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(event.getStartDateText());
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(event.getEndDateText());
		
		logger.strongStep("Select the 31st as the Repeat date for the event repeating monthly and verify the Months will be "
				+ "skipped warning message appears");
		log.info("INFO: Select the repeat option for the event and the frequency as Monthly");
		ui.clickLinkWait(CalendarUI.EventRepeats);
		calUI.selectComboValue(CalendarUI.EventRepeatFreq, "Monthly");
		Assert.assertTrue(calUI.getRepeatFrequentOption().equals("Monthly"), "ERROR: default repeating selection is not equal to monthly.");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventCheckMonthlyByDate).isEnabled(),"ERROR: date selection is not enabled.");
	   
		log.info("INFO: Select the 31st as the Repeat date and verify the Months will be skipped warning message appears");
	    calUI.selectComboValue(CalendarUI.EventMonthlyByDateValue, "the 31st");
	    Assert.assertTrue(driver.isElementPresent(CalendarUI.EventMonthWillSkippedMsgDiv), "ERROR: Months will skipped warning message box does not show up");
		Assert.assertTrue(driver.getSingleElement(CalendarUI.EventMonthWillSkippedMsgDiv).getText().contains(Data.getData().MonthSkippedMsg), "ERROR: Months will skipped warnning message wrong, it should be " + Data.getData().MonthSkippedMsg);
		logger.strongStep("Set the repeat date to the 21st");
		log.info("INFO: Select the 21st as the repeat date");
		calUI.selectComboValue(CalendarUI.EventMonthlyByDateValue, "the 21st");
		Assert.assertFalse(driver.isElementPresent(CalendarUI.EventMonthWillSkippedMsgDiv), "ERROR: Months will skipped warning message box shows up");
		
		driver.getSingleElement(CalendarUI.EventEditorUntilDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorUntilDate).type(event.getRepeatUntilText());
		driver.typeNative(Keys.TAB);
		    
		driver.getSingleElement(CalendarUI.EventLocation).clear();
		driver.getSingleElement(CalendarUI.EventLocation).type(event.getLocation());
		
		logger.strongStep("Submit the details by clicking on the Save button");
		log.info("INFO: Click on the Save button to submit the details");
		calUI.typeNativeInCkEditor(event.getDescription());
		ui.clickLinkWait(CalendarUI.EventSubmit);
		
		//check events result on event list
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		Assert.assertTrue(driver.getElements(calUI.getEventSelector(event)).size()>0, "ERROR: " + event.getName() + " doesn't show up");
		
		List<Element> eventList = driver.getElements(CalendarUI.EventList);
		Assert.assertEquals(eventList.size(), numberofMonth, "ERROR: not all repeated events show up");
		logger.strongStep("Verify each event from the event list appears on the Events page");
		log.info("INFO: Verifying events from event list..");
		for(int count = 0;count<eventList.size();count++){
			Assert.assertTrue(eventList.get(count).getText().contains(event.getName()),"ERROR: the "+(count+1)+" event is not "+event.getName());
//			Assert.assertTrue(eventList.get(count).getText().contains(" 21"),"ERROR: the "+(count+1)+" event is not on 21th ");
		}
		
		 calUI.endTest();
	}
	
	public void typeInDescriptionBox(String text){
		ui.switchToFrame(BaseUIConstants.StatusUpdate_iFrame, BaseUIConstants.StatusUpdate_Body);
		ui.typeText(BaseUIConstants.StatusUpdate_Body, text);
		ui.switchToTopFrame();
	}
}
