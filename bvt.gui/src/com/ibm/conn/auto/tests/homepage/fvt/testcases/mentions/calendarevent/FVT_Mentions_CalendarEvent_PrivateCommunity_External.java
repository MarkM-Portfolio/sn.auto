package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.calendarevent;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_CalendarEvent_PrivateCommunity_External extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_Mentions_CalendarEvent_PrivateCommunity_External.class);

	private HomepageUI ui;
	private CalendarUI uiCal;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity communityPriv;
	private User testUser1, testUser2;
	private String serverURL = "";
	private String testName = "";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCal = CalendarUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());

	}
	
	/**
	* calendarEvents_directedMention_privateCommunity_event_external() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community and add testUser2, who is an external user, as a member</B></li>
	*<li><B>Step: testUser1 add the events widget to the private community</B></li>
	*<li><B>Step: testUser1 create an event mentioning testUser2 in the description</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that the mentions event does NOT appear in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A3BE97A1C8C4BDD885257C98005B73E6">TTT - @MENTIONS - 115 - MENTIONS DIRECTED TO YOU IN CALENDAR EVENT COMMENT - EXTERNAL USER - SC ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void calendarEvents_directedMention_privateCommunity_event_external() throws Exception{
				
		testName = ui.startTest();
		
		communityPriv = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										.access(Access.RESTRICTED)
										.tags("testTags"+ Helper.genDateBasedRand())
										.description("Test description for testcase " + testName)
										.build();
		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();


		//API code for creating a community	
		Community community = communityPriv.createAPI(apiOwner);
		
		log.info("INFO: Adding testUser2 to the community");
		apiOwner.addMemberToCommunity(testUser2, community, StringConstants.Role.MEMBER);
		
		//API code for adding a widget to a community
		communityPriv.addWidgetAPI(community, apiOwner, BaseWidget.EVENTS);
		
		/*
		 * Login testUser1 who will add a comment to a wiki page
		 * containing an @mentions to testUser2
		 */
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		ui.clickLinkWait(BaseUIConstants.Im_Owner);
		ui.waitForPageLoaded(driver);
		
		if(!driver.isElementPresent("link=" + communityPriv.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		//Go to the community just created
		ui.clickLinkWait("link=" + communityPriv.getName());
		ui.waitForPageLoaded(driver);

		//Go to Events using the left nav bar
		log.info("INFO: Add status update to community using left nav bar");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);

		//Click on the Events link in the nav
		log.info("INFO: Select Events from left navigation menu");
		Community_LeftNav_Menu.EVENTS.select(uiCal);
		
		ui.waitForPageLoaded(driver);
		
				
		log.info(driver.getSingleElement(CalendarUI.EventTabFocus).getAttribute("class"));

		//find the view you are currently using (Event or Calendar)	
		if(driver.getSingleElement(CalendarUI.EventTabFocus).getAttribute("class").contentEquals("lotusSelected")){						
			//User is currently in Event view
			log.info("INFO: User is using the Event Tab view");
			driver.getFirstElement(CalendarUI.Event_EventCreateButton).click();
		}else if(driver.getSingleElement(CalendarUI.CalendarTabFocus).getAttribute("class").contentEquals("lotusSelected")){
			//User is currently in Calendar view
			log.info("INFO: User is using the Calendar Tab view");
			driver.getFirstElement(CalendarUI.Cal_EventCreateButton).click();			
		}else{
			//Unable to determine view attempting generic CreateEvent click
			log.info("WARNING: Unable to detect which Event view is being used");
			log.info("INFO: Attempting to click on Generic CreateEvent button");
			driver.getFirstElement(CalendarUI.CreateEvent).click();
		}
		
		log.info("INFO: Adding Title of Event");
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());
		
		/**If we have tags add them */
		if(!event.getTags().isEmpty()){
			log.info("INFO: Adding Tags");
			driver.getSingleElement(CalendarUI.EventTag).clear();
			driver.getSingleElement(CalendarUI.EventTag).type(event.getTags());
		}
				
		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getStartDateText().isEmpty()){
			//change start time subject to 24 hour limitation
			log.info("INFO: Changing Start date to " + event.getStartDateText());
			ui.pickDojoDate(CalendarUI.EventStartDate, event.getStartDate(), false);
		}
		
		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getStartTime().isEmpty()){
			//change start time subject to 24 hour limitation
			log.info("INFO: Changing Start time to " + event.getStartTime());
			driver.getSingleElement(CalendarUI.EventStartTime).clear();
			driver.getSingleElement(CalendarUI.EventStartTime).type(event.getStartTime());			
		}
		
		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getEndDateText().isEmpty()){
			//change end date subject to 24 hour limitation
			log.info("INFO: Changing end date to " + event.getEndDateText());	
			ui.pickDojoDate(CalendarUI.EventEndDate,  event.getEndDate(), false);
		}

		/** If we are changing end date and it is greater then 24 hours from start date this will fail*/
		if(!event.getEndTime().isEmpty()){
			//change end time subject to 24 hour limitation
			log.info("INFO: Changing end time to " + event.getEndTime());
			driver.getSingleElement(CalendarUI.EventEndTime).clear();
			driver.getSingleElement(CalendarUI.EventEndTime).type(event.getEndTime());			
		}		
		
		//if we have a description add it
		if(!event.getDescription().isEmpty()){
			log.info("INFO: Adding Description");
			ui.typeNativeInCkEditor(event.getDescription() + " @" + testUser2.getDisplayName());
		}
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		//Save the new Event
		driver.getSingleElement(CalendarUI.EventSave).click();
		
		ui.logout();
		
		/*
		 * Login testUser2 who will verify that the @mentions to testUser2
		 * appears in the "Mentions" view
		 */
		ui.loadComponent(Data.getData().HomepageMentions, true);
		ui.login(testUser2);
		
		ui.clickLinkWait(HomepageUIConstants.Mentions);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the activity entry is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent("created the event " + event.getName() + " in the " + communityPriv.getName() + " community."),
						 "ERROR: activity entry is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");

		ui.endTest();
	}

	/**
	* calendarEvents_directedMention_privateCommunity_eventComment_external() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community and add testUser2, who is an external user, as a member</B></li>
	*<li><B>Step: testUser1 add the events widget to the private community</B></li>
	*<li><B>Step: testUser1 create an event</B></li>
	*<li><B>Step: testUser1 mentions testUser2 in a comment on the event</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that the mentions event does NOT appear in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/546F08517AD34D5885257C98005B3859">TTT - @MENTIONS - 114 - MENTIONS DIRECTED TO YOU IN CALENDAR EVENT COMMENT - GUEST USER - SC ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void calendarEvents_directedMention_privateCommunity_eventComment_external() throws Exception{
		
		String mentionsComment = Data.getData().StatusComment + Helper.genDateBasedRandVal();
		
		testName = ui.startTest();
		
		communityPriv = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										.access(Access.RESTRICTED)
										.tags("testTags"+ Helper.genDateBasedRand())
										.description("Test description for testcase " + testName)
										.build();

		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();

		//API code for creating a community	
		Community community = communityPriv.createAPI(apiOwner);

		log.info("INFO: Adding testUser2 to the community");
		apiOwner.addMemberToCommunity(testUser2, community, StringConstants.Role.MEMBER);
		
		//API code for adding a widget to a community
		communityPriv.addWidgetAPI(community, apiOwner, BaseWidget.EVENTS);
		
		/*
		 * Login testUser1 who will add a comment to a wiki page
		 * containing an @mentions to testUser2
		 */
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		ui.clickLinkWait(BaseUIConstants.Im_Owner);
		ui.waitForPageLoaded(driver);
		
		if(!driver.isElementPresent("link=" + communityPriv.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		//Go to the community just created
		ui.clickLinkWait("link=" + communityPriv.getName());
		ui.waitForPageLoaded(driver);

		//Go to events using the left nav bar
		log.info("INFO: Go to Events using left nav bar");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);

		//Click on the Events link in the nav
		log.info("INFO: Select Events from left navigation menu");
		Community_LeftNav_Menu.EVENTS.select(uiCal);
		
		log.info("INFO: Creating an Event");
		event.create(uiCal);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Going to the Event");
		ui.clickLinkWait("link=" + communityPriv.getName());
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Adding a comment to an Event");
		driver.getSingleElement(CalendarUI.AddCommentTextField).click();
		ui.typeText(CalendarUI.AddCommentTextField, mentionsComment + " @" + testUser2.getDisplayName());

		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		log.info("INFO: Save comment");
		ui.clickLinkWait(CalendarUI.AddCommentSaveButton);
		ui.logout();
		
		/*
		 * Login testUser2 who will verify that the @mentions to testUser2
		 * appears in the "Mentions" view
		 */
		ui.loadComponent(Data.getData().HomepageMentions, true);
		ui.login(testUser2);
		
		ui.clickLinkWait(HomepageUIConstants.Mentions);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(mentionsComment),
						 "ERROR: Comment is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");

		ui.endTest();
	}

}
