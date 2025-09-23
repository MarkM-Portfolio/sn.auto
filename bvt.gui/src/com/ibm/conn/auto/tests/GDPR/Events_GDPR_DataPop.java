package com.ibm.conn.auto.tests.GDPR;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Events_GDPR_DataPop extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(Events_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private CommunitiesUI commUI;
	private CalendarUI calUI;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private String serverURL;
	private User testUser1, testUser2;


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
						
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
	}

		
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Events Data Population: Create Event</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds the Events widget via API</li>  
	 *<li><B>Step:</B> UserA creates an event</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void createBasicEvent() {
		
		String testName = commUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop: Community Events - UserA creates a basic event")
                                                   .build(); 
		
		BaseEvent event = null;
		
		try {
			event = new BaseEvent.Builder(testName)
			                     .useCalPick(true)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		userACreatesCommunityAndEvent(community, event);
		
		log.info("INFO: Log out user");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Events Data Population: Create Event in the Past</li>
	 *<li><B>Step:</B>UserA creates a community via API</li>
	 *<li><B>Step:</B>UserA adds the Events widget via API</li>
	 *<li><B>Step:</B>UserA creates an event with a date in the past</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void createPastEvent() {
		
		String testName = commUI.startTest();		
		Calendar pastdate = new GregorianCalendar(2014,1,13);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .description("GDPR data pop: Community Events - UserA creates an event with a date in the past")
												   .build();
		
		BaseEvent pastEvent = null;

		try {
			pastEvent = new BaseEvent.Builder("Past Event " + Helper.genDateBasedRand())
			                     .startDate(pastdate)
			                     .endDate(pastdate)
			                     .startTime("1:00 AM")
			                     .endTime("2:00 AM")
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
		userACreatesCommunityAndEvent(community,pastEvent);
						
		log.info("INFO: Check for the Show Past Events link");
		calUI.fluentWaitPresent(CalendarUI.pastEventLink);
		
		log.info("INFO: Click on the Show Past Events link");
		driver.getSingleElement(CalendarUI.pastEventLink).click();
		
		log.info("INFO: Check that the event appears");
		driver.isElementPresent(calUI.getEventSelector(pastEvent));
		
		commUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Events Data Population: 'Will Attend' Event</li>
	 *<li><B>Step:</B>UserB creates a community via API with UserA as a Member</li>
	 *<li><B>Step:</B>UserB adds the Events widget via API</li>
	 *<li><B>Step:</B>UserB creates an event</li>
	 *<li><B>Step:</B>UserA navigates to the Events full widget page</li>
	 *<li><B>Step:</B>UserA clicks the 'Will Attend' button</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void basicEventSelectWillAttendButton() {
		
		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .addMember(member)
												   .description("GDPR data pop: Community Events - UserB creates an event, UserA clicks the 'Will Attend' button")
												   .build();
		
		BaseEvent event = null;
		
		try {
			event = new BaseEvent.Builder(testName + Helper.genDateBasedRand())
			                     .useCalPick(true)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		userBCreatesCommunityAndEvent(community,event);
		
		log.info("INFO: Logout as UserB");
		commUI.logout();
	
		userALogsIntoCommAndOpensEvent(community, event);
		
		log.info("INFO: Click on the 'Will Attend' button");
		commUI.clickLinkWait(CalendarUI.WillAttend);
		
		log.info("INFO: Logout UserA");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Events Data Population: Create a Repeating Event</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds the Events widget via API</li>  
	 *<li><B>Step:</B> UserA creates a repeating event</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void createRepeatingEvent(){
		
		String testName = commUI.startTest();
		int numberofMonth = 7;
		Calendar startDate = Calendar.getInstance();
		Calendar repeatedUntilDate = Calendar.getInstance();
		repeatedUntilDate.add(Calendar.MONTH, numberofMonth);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop: Community Events - UserA creates a repeating event")
                                                   .build(); 

		BaseEvent event = null;

		try {
			event = new BaseEvent.Builder(testName)
			                     .startDate(startDate)
			                     .endDate(startDate)
			                     .repeatUntil(repeatedUntilDate)
			                     .location(Data.getData().BlogsAddress1)
			                     .description(Data.getData().commonDescription)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of the community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.EVENTS);
	
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Navigate to the Events full widget page");
		Community_TabbedNav_Menu.EVENTS.select(commUI);
		
		log.info("INFO: Create a monthly repeated event on every 21th of the month.");
		log.info("INFO: Click on the Create an Event button");
		commUI.clickLinkWait(CalendarUI.CreateEvent);
		
		log.info("INFO: Complete the Title, Tag, Start & End date fields");
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());		
				
		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(event.getStartDateText());
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(event.getEndDateText());
		
		log.info("INFO: Click on the Repeats link");
		commUI.clickLinkWait(CalendarUI.EventRepeats);
		
		log.info("INFO: Set frequency to 'Monthly'");
		calUI.selectComboValue(CalendarUI.EventRepeatFreq, "Monthly");
	
		log.info("INFO: Set the date value to 'the 21st'");
		calUI.selectComboValue(CalendarUI.EventMonthlyByDateValue, "the 21st");
		
		log.info("INFO: Set the repeat until date");
		driver.getSingleElement(CalendarUI.EventEditorUntilDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorUntilDate).type(event.getRepeatUntilText());
		driver.typeNative(Keys.TAB);

		log.info("INFO: Enter the event location info");
		driver.getSingleElement(CalendarUI.EventLocation).clear();
		driver.getSingleElement(CalendarUI.EventLocation).type(event.getLocation());

		log.info("INFO: Enter the event description info");
		calUI.typeNativeInCkEditor(event.getDescription());
		
		log.info("INFO: Click on the Save button");
		commUI.clickLinkWait(CalendarUI.EventSubmit);	

		log.info("INFO: Logout UserA");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Events Data Population: Create a Repeating Event & Select Will Attend</li>
	 *<li><B>Step:</B> UserB creates a community via API</li>
	 *<li><B>Step:</B> UserB adds the Events widget via API</li>  
	 *<li><B>Step:</B> UserB creates a repeating event</li>
	 *<li><B>Step:</B> UserA opens the repeating event</li>
	 *<li><B>Step:</B> UserA clicks the 'Will Attend' button</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void repeatingEventSelectWillAttendButton(){
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		String testName = commUI.startTest();
		int numberofMonth = 7;
		Calendar startDate = Calendar.getInstance();
		Calendar repeatedUntilDate = Calendar.getInstance();
		repeatedUntilDate.add(Calendar.MONTH, numberofMonth);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                  .access(Access.PUBLIC)
                                                  .addMember(member)
                                                  .description("GDPR data pop: Community Events - UserB creates a repeating event, UserA clicks 'Will Attend'")
                                                  .build(); 

		BaseEvent event = null;

		try {
			event = new BaseEvent.Builder(testName)
			                     .startDate(startDate)
			                     .endDate(startDate)
			                     .repeatUntil(repeatedUntilDate)
			                     .location(Data.getData().BlogsAddress1)
			                     .description(Data.getData().commonDescription)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get UUID of the community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.EVENTS);
	
		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Navigate to the Events full widget page");
		Community_TabbedNav_Menu.EVENTS.select(commUI);
		
		log.info("INFO: Create a monthly repeated event on every 21th of the month.");
		log.info("INFO: Click on the Create an Event button");
		commUI.clickLinkWait(CalendarUI.CreateEvent);
		
		log.info("INFO: Complete the Title, Tag, Start & End date fields");
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());		
				
		driver.getSingleElement(CalendarUI.EventEditorStartDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorStartDate).type(event.getStartDateText());
		driver.getSingleElement(CalendarUI.EventEditorEndDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorEndDate).type(event.getEndDateText());
		
		log.info("INFO: Click on the Repeats link");
		commUI.clickLinkWait(CalendarUI.EventRepeats);
		
		log.info("INFO: Set frequency to 'Monthly'");
		calUI.selectComboValue(CalendarUI.EventRepeatFreq, "Monthly");
	
		log.info("INFO: Set the date value to 'the 21st'");
		calUI.selectComboValue(CalendarUI.EventMonthlyByDateValue, "the 21st");
		
		log.info("INFO: Set the repeat until date");
		driver.getSingleElement(CalendarUI.EventEditorUntilDate).clear();
		driver.getSingleElement(CalendarUI.EventEditorUntilDate).type(event.getRepeatUntilText());
		driver.typeNative(Keys.TAB);

		log.info("INFO: Enter the event location info");
		driver.getSingleElement(CalendarUI.EventLocation).clear();
		driver.getSingleElement(CalendarUI.EventLocation).type(event.getLocation());

		log.info("INFO: Enter the event description info");
		calUI.typeNativeInCkEditor(event.getDescription());
		commUI.clickLinkWait(CalendarUI.EventSubmit);	

		log.info("INFO: Logout UserB");
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Navigate to the Events full widget page");
		Community_TabbedNav_Menu.EVENTS.select(commUI);	
		
		log.info("INFO: Refresh browser");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Select the event");
		calUI.getFirstVisibleElement("link=" + event.getName()).click();
		
		log.info("INFO: Click on the 'Will Attend' button");
		calUI.clickLinkWait(CalendarUI.WillAttend);
		
		log.info("INFO: Select the 'Attend the entire series' radio button");
		calUI.clickLinkWait(CalendarUI.attendEntireSeriesRadioButton);
		
		log.info("INFO: Click on the Attend button");
		calUI.clickLinkWait(CalendarUI.attendButtonOnConfirmDialog);		
		
		log.info("INFO: Logout UserA");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Events Data Population: Add Comment To Event</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds the Events widget via API</li>  
	 *<li><B>Step:</B> UserA creates an event</li>
	 *<li><B>Step:</B> UserA adds a comment to the event</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void addCommentToEvent() {
		
		String testName = commUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                  .access(Access.PUBLIC)
                                                  .description("GDPR data pop: Community Events - UserA adds a comment to an event")
                                                  .build(); 
		
		BaseEvent event = null;
		
		try {
			event = new BaseEvent.Builder(testName)
			                     .useCalPick(true)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		userACreatesCommunityAndEvent(community, event);
		
		log.info("INFO: Open the event");
		calUI.getFirstVisibleElement("link=" + event.getName()).click();
		
		log.info("INFO: Click on the link to add a comment");
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		log.info("INFO: Add a comment to the event");		
		calUI.addCommentToEvent(Data.getData().commonComment);
		
		log.info("INFO: Check that the comment is saved");
		calUI.fluentWaitTextPresent(Data.getData().commonComment);

		log.info("INFO: Log out user");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Events Data Population: Add Comment To Event Created By UserB</li>
	 *<li><B>Step:</B>UserB creates a community via API with UserA as a Member</li>
	 *<li><B>Step:</B>UserB adds the Events widget via API</li>
	 *<li><B>Step:</B>UserB creates an event</li>
	 *<li><B>Step:</B>UserA navigates to the Events full widget page</li>
	 *<li><B>Step:</B>UserA adds a comment to the event</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void addCommentToEventCreatedByUserB() {
		
		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .addMember(member)
												   .description("GDPR data pop: Community Events - UserB creates an event, UserA comments on the event")
												   .build();
		
		BaseEvent event = null;
		
		try {
			event = new BaseEvent.Builder(testName + Helper.genDateBasedRand())
			                     .useCalPick(true)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		userBCreatesCommunityAndEvent(community,event);
					
		log.info("INFO: Logout as UserB");
		commUI.logout();
		commUI.close(cfg);
	
		userALogsIntoCommAndOpensEvent(community, event);
		       
		log.info("INFO: Click on the link to add a comment");
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		log.info("INFO: Add a comment to the event");
		calUI.addCommentToEvent(Data.getData().commonComment);
		
		log.info("INFO: Check that the comment is saved");
		calUI.fluentWaitTextPresent(Data.getData().commonComment);
		
		log.info("INFO: Logout UserA");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Events Data Population: Follow Event</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds the Events widget via API</li>  
	 *<li><B>Step:</B> UserA creates an event</li>
	 *<li><B>Step:</B> UserA follows the event</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void followEvent() {
		
		String testName = commUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                 .access(Access.PUBLIC)
                                                 .description("GDPR data pop: Community Events - UserA follows the event")
                                                 .build(); 
		
		BaseEvent event = null;
		
		try {
			event = new BaseEvent.Builder(testName)
			                     .useCalPick(true)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
				
		userACreatesCommunityAndEvent(community, event);
		
		log.info("INFO: Open the event");
		calUI.getFirstVisibleElement("link=" + event.getName()).click();
		
		log.info("INFO: Click the Follow button");
		calUI.clickLinkWait(CalendarUI.EventFollow);

		log.info("INFO: Log out user");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Events Data Population: Follow Event Created By UserB</li>
	 *<li><B>Step:</B>UserB creates a community via API with UserA as a Member</li>
	 *<li><B>Step:</B>UserB adds the Events widget via API</li>
	 *<li><B>Step:</B>UserB creates an event</li>
	 *<li><B>Step:</B>UserA navigates to the Events full widget page</li>
	 *<li><B>Step:</B>UserA follows the event</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void followEventCreatedByUserB() {
		
		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .addMember(member)
												   .description("GDPR data pop: Community Events - UserB creates an event, UserA follows the event")
												   .build();
		
		BaseEvent event = null;
		
		try {
			event = new BaseEvent.Builder(testName + Helper.genDateBasedRand())
			                     .useCalPick(true)
			                     .build();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		userBCreatesCommunityAndEvent(community,event);
		
		log.info("INFO: Logout as UserB");
		commUI.logout();
		commUI.close(cfg);
				
		userALogsIntoCommAndOpensEvent(community, event);
		       
        log.info("INFO: Click the Follow button");
		calUI.clickLinkWait(CalendarUI.EventFollow);
		
		log.info("INFO: Logout UserA");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	/*
	 * This method will create a community & add the Events widget via the API.
	 * UserA will then create an Event.
	 */
	public void userACreatesCommunityAndEvent(BaseCommunity community, BaseEvent event) {

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of the community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.EVENTS);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Navigate to the Events full widget page");
		Community_TabbedNav_Menu.EVENTS.select(commUI);

		log.info("INFO: Create the event");
		try {
			event.create(calUI);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	
	/*
	 * This method will create a community & add the Events widget via the API.
	 * UserB will then create an Event.
	 */
	public void userBCreatesCommunityAndEvent(BaseCommunity community, BaseEvent event) {

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of the community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.EVENTS);

		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Navigate to the Events full widget page");
		Community_TabbedNav_Menu.EVENTS.select(commUI);

		log.info("INFO: Create the event");
		try {
			event.create(calUI);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * UserA logs into communities, navigates to the Events full widget page & opens the event
	 */
	public void userALogsIntoCommAndOpensEvent(BaseCommunity community, BaseEvent event) {

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Navigate to the Events full widget page");
		Community_TabbedNav_Menu.EVENTS.select(commUI);	

		log.info("INFO: Select the event");
		calUI.clickLinkWait("link=" + event.getName());

	}

}
