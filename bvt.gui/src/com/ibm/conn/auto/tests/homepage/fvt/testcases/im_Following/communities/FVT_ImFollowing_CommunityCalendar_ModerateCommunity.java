package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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

public class FVT_ImFollowing_CommunityCalendar_ModerateCommunity extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
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
		
		// User 1 will now create a moderated community with User 2 as a follower and add the Events widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, BaseWidget.EVENTS, testUser1, communitiesAPIUser1, isOnPremise);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(moderatedCommunity, testUser1, communitiesAPIUser1);
	}
	
	/**
	*<ul>
	*<li><B>Name: calendarEventEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>	
	*<li><B>Step: testUser 2 log into Communities and follow the Moderate community</B></li>		
	*<li><B>Step: testUser 1 customize the community and add the Events widget</B></li>
	*<li><B>Step: testUser 1 create an event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.created story is in all view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/651B6F1FED0AEFED8525797C003C56D9">TTT - AS - FOLLOW - COMMUNITY - 00141 - community.calendar.event.entry.created - Moderate Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntry_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: updateCalendarEventEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 go to a Moderate community you are the owner of</B></li>	
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 update the event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify:  Verify that the community.calendar.event.entry.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/69DFFD551A0F7EBD8525797C003C5AAC">TTT - AS - FOLLOW - COMMUNITY - 00151 - community.calendar.event.entry.updated - Moderate Community (NEG SC NOV)</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updateCalendarEventEntry_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community and will then update that calendar event
		String newEventDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(moderatedCommunity, baseEvent, newEventDescription, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: calendarEventEntryDeleted_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>			
	*<li><B>Step: testUser 1 go to a Moderate community you own with an event in the calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 1 delete the event</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All& Communities </B></li>
	*<li><B>Verify: Verify the community.calendar.event.entry.created story is deleted from all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F668583ECD405488525797C00477999">TTT - AS - FOLLOW - COMMUNITY - 00211 - community.calendar.event.entry.deleted - Moderate Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntryDeleted_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		Calendar calendarEvent = CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: calendarEventEntryComment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>			
	*<li><B>Step: testUser 1 go to a Moderate community you are a member of and following</B></li>
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 comment on the event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All& Communities </B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.comment.created story is in all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/41EA02D3674962708525797C003E46C8">TTT - AS - FOLLOW - COMMUNITY - 00161 - community.calendar.event.entry.comment.created - Moderate Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntryComment_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community and will then comment on that event
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndAddComment(moderatedCommunity, baseEvent, comment, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: calendarEventEntryCommentDelete_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>			
	*<li><B>Step: testUser 1 go to a Moderate community you own with an event in the calendar that is commented on</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 1 delete the comment on the event</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All& Communities </B></li>
	*<li><B>Verify: Verify the community.calendar.event.entry.comment.created story is deleted from all views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DB982716C2BAEEDE8525797C0047760A">TTT -AS - FOLLOW - COMMUNITY - 00201 - community.calendar.event.entry.comment.deleted - Moderate Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntryCommentDelete_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		Calendar calendarEvent = CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: calendarSeriesEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>	
	*<li><B>Step: testUser 2 log into Communities and follow the Moderate community</B></li>		
	*<li><B>Step: testUser 1 customize the community and add the Events widget</B></li>
	*<li><B>Step: testUser 1 start a repeating event in the community calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify that the community.calendar.series.entry.created story is in all views </B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2783FB44CA1FE8248525797C0041BC96">TTT - AS - FOLLOW - COMMUNITY - 00171 - community.calendar.series.entry.created - Moderate Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarSeriesEntry_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: calendarSeriesEntryInstanceUpdate_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 go to a Moderate community you are the owner of</B></li>	
	*<li><B>Step: testUser 1 select to Edit the repeating event and select the radio button to "edit this instance"</B></li>		
	*<li><B>Step: testUser 1 update the repeating event in the community calendar</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify that the community.calendar.series.entry.instance.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/02B6E0246DA821998525797C0046D8D9">TTT - AS - FOLLOW - COMMUNITY - 00191 - community.calendar.series.entry.instance.updated - Moderate Community (NEG SC NOV)</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarSeriesEntryInstanceUpdate_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		String newEventDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(moderatedCommunity, baseEvent, newEventDescription, testUser1, calendarAPIUser1);
		
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
	*<li><B>Name: calendarSeriesEntryInstanceDelete_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 go to a Moderate community you own with an repeating event in the calendar</B></li>	
	*<li><B>Step: testUser 1 create a repeating event in the public community</B></li>		
	*<li><B>Step: testUser 1 delete the repeating event</B></li>
	*<li><B>Step: testUser 2 log into communities who is following the community </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Communities</B></li>
	*<li><B>Verify: Verify the community.calendar.series.entry.instance.created story is deleted from the Homepage AS</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/544152F1DE2F560A8525797C00477D73">TTT - AS - FOLLOW - COMMUNITY - 00221 - community.calendar.series.entry.instance.deleted - Moderate Community</a></li>
	*</ul>
	*@author Srinivas Vechha	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarSeriesEntryInstanceDelete_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a repeating calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar repeatingCalendarEvent = CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
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