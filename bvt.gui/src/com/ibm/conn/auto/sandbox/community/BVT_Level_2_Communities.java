package com.ibm.conn.auto.sandbox.community;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseEvent.Days;
import com.ibm.conn.auto.appobjects.base.BaseEvent.RepeatEvery;
import com.ibm.conn.auto.appobjects.base.BaseEvent.RepeatType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;


public class BVT_Level_2_Communities extends SetUpMethods2{

	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities.class);
	private CommunitiesUI ui;
	private CalendarUI calUI;
	private TestConfigCustom cfg;
	private User testUser, testLookAheadUser;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();		
		
	}	

	/**
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: If community is onprem then add a handle</B></li>
	*<li><B>Step: Select a community type - Public</B></li>
	*<li><B>Step: Add a member to the community</B></li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvtcloud"})
	public void createPrivateCommunity() throws Exception {

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .description("Test description for testcase " + testName)
												   .build();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		community.create(ui);

		ui.addWidget(BaseWidget.EVENTS);
		
		Thread.sleep(10000);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a community with name, tag and description</B></li>
	*<li><B>Step: If community is onprem then add a handle</B></li>
	*<li><B>Step: Select a community type - Public</B></li>
	*<li><B>Step: Add a member to the community</B></li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvtcloud"})
	public void createModeratedCommunity() throws Exception {

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.MODERATED)
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testLookAheadUser)).build();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		community.create(ui);
		
		Thread.sleep(10000);
		
		ui.endTest();
	}


	@Test(groups = {"level2"})
	public void communityEventChangeDateTime() throws Exception{
		
		String testName = ui.startTest();
		
		Calendar sdate = new GregorianCalendar(2014,02,10);
		Calendar edate = new GregorianCalendar(2014,02,10);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .startDate(sdate)
									   .startTime("1:15 PM")
									   .endDate(edate)
									   .endTime("1:32 PM")
									   .description(Data.getData().commonDescription)
									   .build();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//create community
		community.create(ui);
		
		//Customize community - Add the Events widget
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.EVENTS);

		//Click on the Events link in the nav	
		Community_LeftNav_Menu.EVENTS.select(ui);

		//Verify the calendar view
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent), "'Create an Event' button not found.");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab), "'Events' tab not found: " + CalendarUI.EventListTab);
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab), "'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		//Create an Event
		event.create(calUI);

		Thread.sleep(10000);
		
		ui.endTest();
	}
	
	@Test(groups = {"level2"})
	public void communityEventAllDay() throws Exception{
		
		Calendar repeatUntil = new GregorianCalendar(2014,2,10);
		
		String testName = ui.startTest();
		
		List<Days> days = new ArrayList<Days>();
		days.add(Days.Sun);
		days.add(Days.Mon);
		days.add(Days.Sat);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .allDayEvent(true)
									   .repeat(true)
									   .repeatType(RepeatType.WEEKLY)
									   .repeatDays(days)
									   .repeatUntil(repeatUntil)
									   .repeatEvery(RepeatEvery.THREE_WEEKS)
									   .description(Data.getData().commonDescription)
									   .build();

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//create community
		community.create(ui);
		
		//Customize community - Add the Events widget
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.EVENTS);

		//Click on the Events link in the nav
		Community_LeftNav_Menu.EVENTS.select(ui);

		//Verify the calendar view
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent), "'Create an Event' button not found.");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab), "'Events' tab not found: " + CalendarUI.EventListTab);
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab), "'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		//Create an Event
		event.create(calUI);

		
		ui.endTest();
	}
	
	
}
