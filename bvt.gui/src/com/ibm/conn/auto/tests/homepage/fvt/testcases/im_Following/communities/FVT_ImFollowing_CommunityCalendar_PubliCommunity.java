package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityCalendarNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Community) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 01/2016
 */

public class FVT_ImFollowing_CommunityCalendar_PubliCommunity extends SetUpMethodsFVT { 
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);			
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);	
		
		// User 1 will now create a public community with User 2 as a follower and add the Events widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, BaseWidget.EVENTS, testUser1, communitiesAPIUser1, isOnPremise);		
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {

		// Delete the community created during the test
		CommunityEvents.deleteCommunity(publicCommunity, testUser1, communitiesAPIUser1);
	}
	
	/**
	*<ul>
	*<li><B>Name: calendarEventEntry_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with public access that you own</B></li>	
	*<li><B>Step: testUser 2 log into Communities and follow the public community</B></li>		
	*<li><B>Step: testUser 1 customize the community and add the Events widget</B></li>
	*<li><B>Step: testUser 1 create an event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.created story is in all view </B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4490FECF29D6D5398525797C0038E88F">TTT - AS - FOLLOW - COMMUNITY - 00140 - community.calendar.event.entry.created - Public Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntry_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: updateCalendarEventEntry_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 go to a public community you are the owner of</B></li>	
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 update the event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify:  Verify that the community.calendar.event.entry.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/70CBD5C57EA653AA8525797C003C598B">TTT - AS - FOLLOW - COMMUNITY - 00150 - community.calendar.event.entry.updated - Public Community (NEG SC NOV)</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updateCalendarEventEntry_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community and will then update that calendar event
		String newEventDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(publicCommunity, baseEvent, newEventDescription, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateCalendarEvent = CommunityCalendarNewsStories.getUpdateCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create event and original calendar event description are displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, filter, true);
			
			// Verify that the update event and updated calendar event description are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCalendarEvent, newEventDescription}, null, false);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: calendarEventEntrydeleted_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>			
	*<li><B>Step: testUser 1 go to a public community you own with an event in the calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 1 delete the event</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All& Communities </B></li>
	*<li><B>Verify: Verify the community.calendar.event.entry.created story is deleted from all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B13319D61F917C8F8525797C0047786E">TTT - AS - FOLLOW - COMMUNITY - 00210 - community.calendar.event.entry.deleted - Public Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntrydeleted_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		Calendar calendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, true);
		
		// User 1 will now delete the calendar event
		CommunityCalendarEvents.deleteCalendarEventFirstInstance(calendarEvent, testUser1, calendarAPIUser1);
		
		// Verify that the create event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: calendarEventEntryComment_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>			
	*<li><B>Step: testUser 1 go to a public community you are a member of and following</B></li>
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 comment on the event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All& Communities </B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.comment.created story is in all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/135055923A29287E8525797C003E453E">TTT - AS - FOLLOW - COMMUNITY - 00160 - community.calendar.event.entry.comment.created - Public Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntryComment_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community and will then comment on that event
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndAddComment(publicCommunity, baseEvent, comment, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnTheirOwnCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), comment}, TEST_FILTERS, true);
				
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: calendarEventEntryCommentDelete_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>			
	*<li><B>Step: testUser 1 go to a public community you own with an event in the calendar that is commented on</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 1 delete the comment on the event</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All& Communities </B></li>
	*<li><B>Verify: Verify the community.calendar.event.entry.comment.created story is deleted from all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C0764684D6073F8525797C00477445">TTT - AS - FOLLOW - COMMUNITY - 00200 - community.calendar.event.entry.comment.deleted - Public Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntryCommentDelete_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		Calendar calendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// User 1 will now comment on that calendar event
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommentToEvent calendarEventComment = CommunityCalendarEvents.addCommentToCalendarEvent(calendarEvent, comment, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnTheirOwnCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), comment}, TEST_FILTERS, true);
		
		// User 1 will now delete the comment posted to the calendar event
		CommunityCalendarEvents.deleteCommentFromCalendarEvent(calendarEventComment, testUser1, calendarAPIUser1);
		
		// Verify that the event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: calendarSeriesEntry_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with public access that you own</B></li>	
	*<li><B>Step: testUser 2 log into Communities and follow the public community</B></li>		
	*<li><B>Step: testUser 1 customize the community and add the Events widget</B></li>
	*<li><B>Step: testUser 1 start a repeating event in the community calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify that the community.calendar.series.entry.created story is in all views </B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4783B64DF296B0A18525797C0041BAF5">TTT - AS - FOLLOW - COMMUNITY - 00170 - community.calendar.series.entry.created - Public Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarSeriesEntry_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateRepeatingCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}			
	
	/**
	*<ul>
	*<li><B>Name: calendarSeriesEntryInstanceUpdate_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 go to a public community you are the owner of</B></li>	
	*<li><B>Step: testUser 1 select to Edit the repeating event and select the radio button to "edit this instance"</B></li>		
	*<li><B>Step: testUser 1 update the repeating event in the community calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify that the community.calendar.series.entry.instance.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/275135103231A4538525797C0046D74E">TTT - AS - FOLLOW - COMMUNITY - 00190 - community.calendar.series.entry.instance.updated - Public Community (NEG SC NOV)</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarSeriesEntryInstanceUpdate_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		String newEventDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(publicCommunity, baseEvent, newEventDescription, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateRepeatingCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateCalendarEvent = CommunityCalendarNewsStories.getUpdateInstanceOfRepeatingCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create event and original calendar event description are displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, filter, true);
			
			// Verify that the update event and updated calendar event description are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCalendarEvent, newEventDescription}, null, false);
		}
		ui.endTest();
	}			
	
	/**
	*<ul>
	*<li><B>Name: calendarSeriesEntryInstanceDelete_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 go to a public community you own</B></li>	
	*<li><B>Step: testUser 1 create a repeating event in the public community</B></li>		
	*<li><B>Step: testUser 1 delete the repeating event</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify the community.calendar.series.entry.instance.created story is deleted from the Homepage AS</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EA712C63B1617D5D8525797C00477C44">TTT - AS - FOLLOW - COMMUNITY - 00220 - community.calendar.series.entry.instance.deleted - Public Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarSeriesEntryInstanceDelete_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar repeatingCalendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateRepeatingCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create event appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, true);
		
		// User 1 will now delete the repeating calendar event
		CommunityCalendarEvents.deleteCalendarEventSeries(repeatingCalendarEvent, testUser1, calendarAPIUser1);
		
		// Verify that the create event does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}				
}