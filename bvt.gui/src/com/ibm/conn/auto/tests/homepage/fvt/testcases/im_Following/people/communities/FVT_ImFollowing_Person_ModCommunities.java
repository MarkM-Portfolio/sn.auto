package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBookmarkEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBookmarkNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityCalendarNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_ImFollowing_Person_ModCommunities extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	private String TEST_FILTERS_BOOKMARKS[];
	
	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseModeratedCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);	

		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
				
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 2 will follow User 1 through API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);

		// User 1 will now create a moderated community and add the Events widget
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.EVENTS, isOnPremise, testUser1, communitiesAPIUser1);
		
		if(isOnPremise){
			TEST_FILTERS = new String[3];
			TEST_FILTERS_BOOKMARKS = new String[4];
			TEST_FILTERS[2] = TEST_FILTERS_BOOKMARKS[3] = HomepageUIConstants.FilterPeople;
		}
		else{
			TEST_FILTERS = new String[2];
			TEST_FILTERS_BOOKMARKS = new String[3];
		}
			
		// Add the commonly used filters to the TEST_FILTERS array
		TEST_FILTERS[0] = TEST_FILTERS_BOOKMARKS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = TEST_FILTERS_BOOKMARKS[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS_BOOKMARKS[2] = HomepageUIConstants.FilterBookmarks;
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		
		// Have User 2 unfollow User 1 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);	
	}

	/**
	*<ul>
	*<li><B>Name: test_Person_CreateCommunity_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Create a new community with Moderate access </B></li>	
	*<li><B>Step: testUser 2 Follow User 1</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.created is displayed within the Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8A7C34C5C087D893852578FB0051A5DD">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00172 - community.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_Person_CreateCommunity_ModerateCommunity(){
		
		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String commCreationEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseModeratedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story and description are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commCreationEvent, baseModeratedCommunity.getDescription()}, TEST_FILTERS, true);	

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_AddBookmark_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access</B></li>
	*<li><B>Step: testUser 1 Create a bookmark</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Bookmarks & people</B></li>
	*<li><B>Verify: Verify that the community.bookmark.created story is displayed within the Bookmarks, Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AE547FE6D0929DFD852578FB0051A919">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00182 - community.bookmark.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 2)
	public void AddBookmarkToModerateCommunity(){

		String testName = ui.startTest();

		// User 1 create a community bookmark
		BaseDogear baseDogear = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().IbmURL, baseModeratedCommunity);
		CommunityBookmarkEvents.createBookmark(moderatedCommunity, baseDogear, testUser1, communitiesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String bookmarkCreated = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseDogear.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());

		String THIS_TEST_FILTERS[];
		if(isOnPremise){
			THIS_TEST_FILTERS = TEST_FILTERS_BOOKMARKS;
		}
		else{
			THIS_TEST_FILTERS = TEST_FILTERS;
		}
		// Verify that the create new bookmark event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{bookmarkCreated, baseDogear.getDescription()}, THIS_TEST_FILTERS, true);

		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateBookmark_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access</B></li>
	*<li><B>Step: testUser 1 Update an existing bookmark</B></li>
	*<li><B>Step: testUser 2 log into Homepage</B></li>		
	*<li><B>Step: testUser 2 Follow User 1</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Bookmarks & people</B></li>
	*<li><B>Verify: Verify that the community.bookmark.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/34BA413DD9BB3C26852579BF0046D5EE">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00185 - community.bookmark.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 2)
	public void UpdateBookmarkToModerateCommunity(){

		String testName = ui.startTest();

		// User 1 create a community bookmark and edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseDogear baseDogear = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().Tv3URL, baseModeratedCommunity);
		CommunityBookmarkEvents.createBookmarkAndEditDescription(moderatedCommunity, baseDogear, testUser1, communitiesAPIUser1, editedDescription);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news stories to be verified
		String bookmarkCreated = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseDogear.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String bookmarkUpdated = CommunityBookmarkNewsStories.getUpdateBookmarkNewsStory(ui, baseDogear.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());

		// Set the filters to be tested
		String[] THIS_TEST_FILTERS;
		if(isOnPremise){
			THIS_TEST_FILTERS = TEST_FILTERS_BOOKMARKS;
		}
		else{
			THIS_TEST_FILTERS = TEST_FILTERS;
		}
		
		for(String filter: THIS_TEST_FILTERS) {
			// Verify that the create bookmark event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{bookmarkCreated, baseDogear.getDescription()}, filter, true);
			
			// Verify that the update bookmark event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{bookmarkUpdated, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_calendarEventEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>	
	*<li><B>Step: testUser 2 log in and follow User 1</B></li>		
	*<li><B>Step: testUser 1 customize the community and add the Events widget</B></li>
	*<li><B>Step: testUser 1 create an event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.created story is in all view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/02526B69094F73A185257B860035FE98">TTT - AS - FOLLOW - PERSON - COMMUNITY EVENT - 00201 - community.calendar.event.entry.created - Moderate Community</a></li>
	*</ul>	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void calendarEventEntry_ModerateCommunity(){
		
		String testName = ui.startTest();		

		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEvent(moderatedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_updatecalendarEventEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>	
	*<li><B>Step: testUser 2 log in and follow User 1</B></li>		
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 update the event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.updated story is NOT in any view</B></li>
	*<li><a HREF=" Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F976EC49DF4B694785257B860035FE9E">TTT -  AS - FOLLOW - PERSON - COMMUNITY EVENT - 00211 - community.calendar.event.entry.updated - Moderate Community (NEG SC NOV)</a></li>
	*</ul>	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void updatecalendarEventEntry_ModerateCommunity(){
		
		String testName = ui.startTest();		

		// User 1 will now create a calendar event in the community and will then update that calendar event
		String newEventDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(moderatedCommunity, baseEvent, newEventDescription, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String updateCalendarEvent = CommunityCalendarNewsStories.getUpdateCalendarEventNewsStory(ui, baseEvent.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		
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
	*<li><B>Name: test_Person_calendarEventEntryComment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>	
	*<li><B>Step: testUser 2 log in and follow User 1</B></li>		
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 comment on the event entry in the calendar</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.comment.created story is in all views </B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A580B8E0916E8A7885257B860035FE9B">TTT - AS - FOLLOW - PERSON - COMMUNITY EVENT - 00221 - community.calendar.event.entry.comment.created - Moderate Community</a></li>
	*</ul>	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void calendarEventEntryComment_ModerateCommunity(){
		
		String testName = ui.startTest();		

		// User 1 will now create a calendar event in the community and will then comment on that event
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndAddComment(moderatedCommunity, baseEvent, comment, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnTheirOwnCalendarEventNewsStory(ui, baseEvent.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), comment}, TEST_FILTERS, true);
		
		ui.endTest();		
	}
}