package com.ibm.conn.auto.tests.calendar.regression;

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
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Smoke_Community_Calendar extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Smoke_Community_Calendar.class);
	private CommunitiesUI ui;
	private CalendarUI calUI;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	String publicViewURL = "https://apps.collabservdaily.swg.usma.ibm.com/communities/service/html/allcommunities";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		log.info("INFO: Using test user: " + testUser1.getDisplayName());
		log.info("INFO: Using test user: " + testUser2.getDisplayName());

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	*<li><B>Step: Create a community and then add Events widget and add an event </B> </li>
	*<li><B>Verify: Events widget is added to the community and the event is created</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"bvtcloud"})
	public void communityCreateCalendarEvent() throws Exception {

		//Start of test
		String testName = ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//create community
		String communityName = testName + Helper.genDateBasedRand();
		BaseCommunity community = new BaseCommunity.Builder(communityName)
									 .tags(Data.getData().commonTag)
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(Access.PUBLIC)
									 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
									 .description("Test Widgets inside " + communityName)
									 .build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder("MyEvent" + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		
		
		community.create(ui);
		
		//Customize community - Add the Ideation Blogs widget
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community: "+ community.getName());
		ui.addWidget(BaseWidget.EVENTS);

		//Click on the Events link in the nav
		log.info("INFO: Select Events from left navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		//Verify the calendar view
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent), "'Create an Event' button not found.");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab), "'Events' tab not found: " + CalendarUI.EventListTab);
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab), "'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		//Create an Event
		event.create(calUI);
		
		//Validate that the page loads without Error
		ui.checkForErrorsOnPage();
		
		//Verify Event is created
		ui.fluentWaitTextPresent(event.getName());
		
		//Logout as the first user and login as the second user
		ui.logout();
		ui.login(testUser1);
		
		//Ensure that the public community view is loaded and then open the community from this view
		driver.navigate().to(publicViewURL);
		ui.openCommunity(communityName);
		
		//Click on the Events link in the nav
		log.info("INFO: Select Events from left navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//verify that the event is present
		ui.fluentWaitTextPresent(event.getName());
		
		//Logout now
		ui.logout();

		//End of test
		ui.endTest();
	}

}
