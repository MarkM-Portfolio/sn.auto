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

public class FVT_ImFollowing_Person_PrivateCommunities extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	private String TEST_FILTERS_BOOKMARKS[];
	
	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseRestrictedCommunity;
	private Community restrictedCommunity;
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
		
		// User 1 will now create a restricted community and add the Events widget
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseRestrictedCommunity, BaseWidget.EVENTS, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the community now that the test has completed
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		
		// Have User 2 unfollow User 1 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}

	/**
	*<ul>
	*<li><B>Name: test_Person_CreateCommunity_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Create a new community with Private access, add User 2 as a member </B></li>	
	*<li><B>Step: testUser 2 Follow User 1</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.created is NOT displayed within the Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B5AE404475F6CE29852578FB0051A6F2">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00173 - community.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Person_CreateCommunity_PrivateCommunity(){
		
		String testName = ui.startTest();

		// User 1 create a private community
		BaseCommunity baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		CommunityEvents.createNewCommunity(baseRestrictedCommunity, testUser1, communitiesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String commCreationEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseRestrictedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the news story and description are NOT displayed in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commCreationEvent, baseRestrictedCommunity.getDescription()}, TEST_FILTERS, false);	
	
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Person_AddBookmark_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access</B></li>
	*<li><B>Step: testUser 1 Create a bookmark</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Bookmarks & people</B></li>
	*<li><B>Verify: Verify that the community.bookmark.created story is NOT displayed within the Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/84621E48926174FE852578FB0051AA31">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00183 - community.bookmark.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void AddBookmarkToPrivateCommunity(){

		String testName = ui.startTest();

		// User 1 create a community bookmark
		BaseDogear baseDogear = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().IbmURL, baseRestrictedCommunity);
		CommunityBookmarkEvents.createBookmark(restrictedCommunity, baseDogear, testUser1, communitiesAPIUser1);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String bookmarkCreated = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseDogear.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());

		String THIS_TEST_FILTERS[];
		if(isOnPremise){
			THIS_TEST_FILTERS = TEST_FILTERS_BOOKMARKS;
		}
		else{
			THIS_TEST_FILTERS = TEST_FILTERS;
		}
		// Verify that the create new bookmark event is NOT displayed in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{bookmarkCreated, baseDogear.getDescription()}, THIS_TEST_FILTERS, false);

		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateBookmark_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access</B></li>
	*<li><B>Step: testUser 1 Update an existing bookmark</B></li>
	*<li><B>Step: testUser 2 log into Homepage</B></li>		
	*<li><B>Step: testUser 2 Follow User 1</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Bookmarks & people</B></li>
	*<li><B>Verify: Verify that the community.bookmark.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3A0147464C1D694F852579BF0046FEBD">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00186 - community.bookmark.updated - PRIVATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void UpdateBookmarkToPrivateCommunity(){

		String testName = ui.startTest();

		// User 1 create a community bookmark and edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseDogear baseDogear = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().Tv3URL, baseRestrictedCommunity);
		CommunityBookmarkEvents.createBookmarkAndEditDescription(restrictedCommunity, baseDogear, testUser1, communitiesAPIUser1, editedDescription);

		//User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String bookmarkCreated = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseDogear.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String bookmarkUpdated = CommunityBookmarkNewsStories.getUpdateBookmarkNewsStory(ui, baseDogear.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());

		// Set the filters to be tested
		String[] THIS_TEST_FILTERS;
		if(isOnPremise){
			THIS_TEST_FILTERS = TEST_FILTERS_BOOKMARKS;
		}
		else{
			THIS_TEST_FILTERS = TEST_FILTERS;
		}	
		// Verify that the create bookmark event and update bookmark event are NOT displayed in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{bookmarkUpdated, bookmarkCreated, baseDogear.getDescription(), editedDescription}, THIS_TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_calendarEventEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Private access that you own</B></li>	
	*<li><B>Step: testUser 2 log in and follow User 1</B></li>		
	*<li><B>Step: testUser 1 customize the community and add the Events widget</B></li>
	*<li><B>Step: testUser 1 create an event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.created is NOT displayed</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E77EEC3FE2960C585257B860035FE99">TTT - AS - FOLLOW - PERSON - COMMUNITY EVENT - 00202 - community.calendar.event.entry.created - Private Community</a></li>
	*</ul>	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntry_PrivateCommunity(){
		
		String testName = ui.startTest();		

		// User 1 will now create a calendar event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEvent(restrictedCommunity, baseEvent, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_updatecalendarEventEntry_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Private access that you own</B></li>	
	*<li><B>Step: testUser 2 log in and follow User 1</B></li>		
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 update the event entry in the calendar</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.calendar.event.entry.updated story is NOT in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9A8F88DCF7FC1C7585257B860035FE9F">TTT -AS - FOLLOW - PERSON - COMMUNITY EVENT - 00213 - community.calendar.event.entry.updated - Private Community (NEG SC NOV)</a></li>
	*</ul>	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatecalendarEventEntry_restrictedCommunity(){
		
		String testName = ui.startTest();		

		// User 1 will now create a calendar event in the community and will then update that calendar event
		String newEventDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(restrictedCommunity, baseEvent, newEventDescription, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String updateCalendarEvent = CommunityCalendarNewsStories.getUpdateCalendarEventNewsStory(ui, baseEvent.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the updated event and original calendar event description are NOT displayed
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateCalendarEvent, createCalendarEvent, baseEvent.getDescription().trim(), newEventDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_calendarEventEntryComment_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Private access that you own</B></li>	
	*<li><B>Step: testUser 2 log in and follow User 1</B></li>		
	*<li><B>Step: testUser 1 go to an event entry that is in the calendar in the community</B></li>
	*<li><B>Step: testUser 1 comment on the event entry in the calendar</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: User 2 verify that the community.calendar.event.entry.comment.created story is NOT displayed </B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/221CF1E62725F61785257B860035FE9A">TTT - AS - FOLLOW - PERSON - COMMUNITY EVENT - 00222 - community.calendar.event.entry.comment.created - Private Community</a></li>
	*</ul>	 
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void calendarEventEntryComment_PrivateCommunity(){
		
		String testName = ui.startTest();		

		// User 1 will now create a calendar event in the community and will then comment on that event
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		CommunityCalendarEvents.addCalendarEventAndAddComment(restrictedCommunity, baseEvent, comment, testUser1, calendarAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnTheirOwnCalendarEventNewsStory(ui, baseEvent.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the event is NOT displayed in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription().trim(), comment}, TEST_FILTERS, false);
		
		ui.endTest();	
	}	
}