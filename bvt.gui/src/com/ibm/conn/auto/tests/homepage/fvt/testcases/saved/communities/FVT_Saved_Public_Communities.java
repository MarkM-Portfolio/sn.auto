package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityCalendarNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:		Anthony Cox
 * Date:		3rd September 2015
 */

public class FVT_Saved_Public_Communities extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICalendarHandler calendarAPIUser1, calendarAPIUser2, calendarAPIUser3, calendarAPIUser4;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4;
	private APIFileHandler filesAPIUser1, filesAPIUser3;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser3, profilesAPIUser4;
	private BaseCommunity baseCommunityUser1, baseCommunityUser3;
	private Community publicCommunityUser1, publicCommunityUser3;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		communitiesAPIUser4 = initialiseAPICommunitiesHandlerUser(testUser4);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser3 = initialiseAPIFileHandlerUser(testUser3);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		calendarAPIUser2 = initialiseAPICalendarHandlerUser(testUser2);
		calendarAPIUser3 = initialiseAPICalendarHandlerUser(testUser3);
		calendarAPIUser4 = initialiseAPICalendarHandlerUser(testUser4);
		
		// User 1 will now create a public community with User 2 added as a member and a follower and the Events widget added
		baseCommunityUser1 = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityUser1 = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseCommunityUser1, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1, BaseWidget.EVENTS, isOnPremise);
		
		// User 3 will now create a public community with User 4 added as a member and a follower and the Events widget added
		baseCommunityUser3 = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityUser3 = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseCommunityUser3, testUser4, communitiesAPIUser4, testUser3, communitiesAPIUser3, BaseWidget.EVENTS, isOnPremise);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunityUser1);
		communitiesAPIUser3.deleteCommunity(publicCommunityUser3);
	}
	
	/**
	* test_PublicCommunity_SavingCommunityStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Communities</B></li>
	*<li><B>2. User 1 start a public community - User 2 join and follow this community</B></li>
	*<li><B>3. User 1 customize the community and add Calendar (newly named events) widget</B></li>
	*<li><B>4. User 1 add an event to the calendar</B></li>
	*<li><b>5: User 1 go to Homepage / Discover / Communities</b></li>
	*<li><b>6: User 1 go to the story of the public community creation and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>7: User 1 go to Homepage / Saved / Communities</b></li>
	*<li><b>Verify: User 1 should see the community creation in Homepage / Saved / Communities</b></li>
	*<li><b>8: User 2 log into connections go to Homepage / I'm Following / Communities</b></li>
	*<li><b>9: User 2 go to story of User 1 creating the event in the calendar being created and mark as Saved</b></li>
	*<li><B>Verify: User 2 - Once the "Save this" link has been clicked it should be blackened and unclickable</B></li>
	*<li><b>10: User 2 go to Homepage / Saved / Connections</b></li>
	*<li><b>Verify: User 2 should see event story in Homepage / Saved / Communities</b></li>
	*<li><b>11: User 2 comment on the event in the calendar</b></li>
	*<li><b>12: User 1 go to Homepage / My Notifications / For Me / Communities</b></li>
	*<li><b>13: User 1 go to the story of User 2 commenting on the event and mark it as Saved</b></li>
	*<li><b>Verify: User 1 - Once the "Save this" link has been clicked it should be blackened and unclickable</b></li>
	*<li><b>14: User 1 go to Homepage / Saved / Communities</b></li>
	*<li><b>Verify: User 1 should see event story in Homepage / Saved / Communities</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/DCADBC7EA51DD7A68525793B00350BCF">TTT: AS - Saved - 00011 - Marking Community Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_PublicCommunity_SavingCommunityStories() {
		
		String testName = ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved
		String createCommunityEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseCommunityUser1.getName(), testUser1.getDisplayName());
		
		// User 1 will now save the create community event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createCommunityEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create community event news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommunityEvent, baseCommunityUser1.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now add a calendar event to the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar communityCalendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunityUser1, baseEvent, testUser1, calendarAPIUser1);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Create the news story to be saved
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunityUser1.getName(), testUser1.getDisplayName());
		
		// User 2 will now save the create calendar event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createCalendarEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create calendar event news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription()}, TEST_FILTERS, true);
		
		// User 2 will now add a comment to the community calendar event
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(communityCalendarEvent, user2Comment, testUser2, calendarAPIUser2);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news story to be saved
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_User(ui, baseEvent.getName(), baseCommunityUser1.getName(), testUser2.getDisplayName());
		
		// User 1 will now save the coment on calendar event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, commentOnCalendarEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
				
		// Verify that the comment on calendar event news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription(), user2Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PublicCommunity_RemovingCommunityStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. User 1 go to Homepage / Saved / Communities</B></li>
	*<li><B>3. Click the 'X' in the story related to the Community that had been created by User 1</B></li>
	*<li><b>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>5: Go to Homepage / Discover / Communities</b></li>
	*<li><B>Verify: User 1 - The story in Homepage / Discover / Communities should have a clickable "Save this" link again</b></li>
	*<li><b>6: User 2 log into Connections</b></li>
	*<li><b>7: Go to Homepage / Saved / Communities</b></li>
	*<li><b>8: Click the 'X' in the story related to the calendar that was created by User 1</b></li>
	*<li><b>Verify: User 2 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>9: Go to Homepage / I'm Following / Communities</b></li>
	*<li><B>Verify: User 2 - The story in Homepage / I'm Following / Communities should have a clickable "Save this" link again</B></li>
	*<li><b>10: User 1 go to Homepage / Saved / Communities</b></li>
	*<li><b>11: Click the 'X' in the story related to the comment on the event from User 2</b></li>
	*<li><b>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>12: Go to Homepage / My Notifications / For Me / Communities</b></li>
	*<li><b>Verify: User 1 - The story in Homepage / For Me / Communities should have a clickable "Save this" link again</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/D9852E62813F3A888525793B0035E6D5">TTT: AS - Saved - 00012 - Removing Community Stories from Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_PublicCommunity_RemovingCommunityStories() {
		
		/**
		 * To avoid attempting to save / remove identical news stories to other parallel-run tests, this test case will use User 3 (as User 1) and User 4 (as User 2)
		 */
		String testName = ui.startTest();
		
		// Create the news story to be saved
		String createCommunityEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseCommunityUser3.getName(), testUser3.getDisplayName());
		
		if(isOnPremise) {
			// Save the create community event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser3, createCommunityEvent, true);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
			
			// User 1 will now save the create community event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createCommunityEvent);
			
			// Log out of Connections
			LoginEvents.logout(ui);
		}
		
		// User 1 will now add a calendar event to the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar communityCalendarEvent = CommunityCalendarEvents.addCalendarEvent(publicCommunityUser3, baseEvent, testUser3, calendarAPIUser3);
		
		// Create the news story to be saved
		String createCalendarEvent = CommunityCalendarNewsStories.getCreateCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunityUser3.getName(), testUser3.getDisplayName());
		
		if(isOnPremise) {
			// Save the create community event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser4, createCalendarEvent, false);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser4, true);
						
			// User 2 will now save the create calendar event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, createCalendarEvent);
						
			// Log out of Connections
			LoginEvents.logout(ui);
		}
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser3, !isOnPremise);
		
		// Verify that the create community event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommunityEvent, baseCommunityUser3.getDescription()}, TEST_FILTERS, true);
		
		// Remove the create community event news story from the Saved view using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createCommunityEvent);
		
		// Verify that the create community event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommunityEvent, baseCommunityUser3.getDescription()}, TEST_FILTERS, false);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the 'Save This' link is displayed on the create community event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createCommunityEvent);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser4, true);
		
		// Verify that the create calendar event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription()}, TEST_FILTERS, true);
		
		// Remove the create calendar event news story from the Saved view using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createCalendarEvent);
				
		// Verify that the create calendar event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCalendarEvent, baseEvent.getDescription()}, TEST_FILTERS, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the 'Save This' link is displayed on the create calendar event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createCalendarEvent);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// User 2 will now add a comment to the community calendar event
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityCalendarEvents.addCommentToCalendarEvent(communityCalendarEvent, user2Comment, testUser4, calendarAPIUser4);
				
		// Create the news story to be saved
		String commentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnYourCalendarEventNewsStory_User(ui, baseEvent.getName(), baseCommunityUser3.getName(), testUser4.getDisplayName());
			
		if(isOnPremise) {
			// Create the entry representation of the news story to be saved
			String entryCommentOnCalendarEvent = CommunityCalendarNewsStories.getCommentOnTheCalendarEventNewsStory(ui, baseEvent.getName(), baseCommunityUser3.getName(), testUser4.getDisplayName());
			
			// Save the comment on calendar event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser3, entryCommentOnCalendarEvent, true);
			
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser3, true);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser3, true);
			
			// User 1 will now save the comment on calendar event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, commentOnCalendarEvent);
			
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
					
		// Verify that the comment on calendar event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription(), user2Comment}, TEST_FILTERS, true);
		
		// Remove the create calendar event news story from the Saved view using the UI
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, commentOnCalendarEvent);
		
		// Verify that the comment on calendar event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnCalendarEvent, baseEvent.getDescription(), user2Comment}, TEST_FILTERS, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Verify that the 'Save This' link is displayed on the comment on calendar event news story
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, commentOnCalendarEvent);
		
		ui.endTest();
	}
	
	/**
	* test_PublicCommunity_SaveLikedCommentStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into a public community you own</B></li>
	*<li><B>2. Add a status update with a comment in the community</B></li>
	*<li><B>3. Like the comment</B></li>
	*<li><b>4: Go to Homepage / I'm Following / Communities</b></li>
	*<li><b>5: Save the story of the comment being liked</b></li>
	*<li><b>6: Go to Homepage / Saved / All & Communities</b></li>
	*<li><b>Verify: Verify that the community wall comment recommended added story appears</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/7EC2620103CA503E85257A8B00455145">TTT: AS - Saved - 00020 - Community Wall Comment Recommended Added</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UsingPublicCommunity_SaveLikedCommentStory() {
		
		ui.startTest();
		
		// User 1 will now post a status update in the community
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = CommunityEvents.addStatusUpdate(publicCommunityUser1, communitiesAPIUser1, profilesAPIUser1, statusUpdate);
		
		// User 1 will now post a comment to the community status update and will like / recommend the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateCommentAndLikeComment(profilesAPIUser1, profilesAPIUser1, statusUpdateId, comment);
		
		// Create the news story to be saved
		String likeCommentEvent = CommunityNewsStories.getLikeTheirOwnCommentInTheCommunityNewsStory(ui, baseCommunityUser1.getName(), testUser1.getDisplayName());
		
		if(isOnPremise) {
			// Save the like comment event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, likeCommentEvent, false);
						
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
			
			// Create the news story to be saved from the I'm Following view (this differs to the news story saved on On Premise)
			String youLikeYourCommentEvent = CommunityNewsStories.getLikeYourCommentInTheCommunityNewsStory_You(ui, baseCommunityUser1.getName());
						
			// User 1 will now save the like comment event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, youLikeYourCommentEvent);
						
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		
		// Verify that the like comment event news story is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, statusUpdate, comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PublicCommunity_SaveSharedFileStory()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities</B></li>
	*<li><B>2. Go to a public community you own</B></li>
	*<li><B>3. Share a file with the community</B></li>
	*<li><b>4: Go to Homepage / Discover / Communities</b></li>
	*<li><b>5: Go to the story of the file shared with the community and "Save This"</b></li>
	*<li><b>6: Go to Homepage / Saved / Communities</b></li>
	*<li><b>Verify: Verify that the story of the file shared with the community is saved</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/B719008F80050EA6852579B2005B35BC">TTT: AS - Saved - 00050 - Save a Story of a Community File Shared</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_PublicCommunity_SaveSharedFileStory() {
		
		ui.startTest();
		
		// User 1 will now share a file with the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(publicCommunityUser1, baseFile, testUser1, filesAPIUser1);
		
		// Create the news stories to be saved and used for verifications
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityUser1.getName(), testUser1.getDisplayName());
		
		if(isOnPremise) {
			// Create the news story required to save the file shared event
			String fileSharedEventEntry = CommunityFileNewsStories.getShareTheFileWithCommunityNewsStory(ui, baseFile.getRename() + baseFile.getExtension(), baseCommunityUser1.getName(), testUser1.getDisplayName());
			
			// Save the file shared event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser1, fileSharedEventEntry, true);
			
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
									
			// User 1 will now save the like comment event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, fileSharedEvent);
									
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
		
		// Verify that the file shared event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileSharedEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* test_PublicCommunity_UpdateSharedFileAfterSaved()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. Log into Communities</B></li>
	*<li><B>2. Go to a public community you own</B></li>
	*<li><B>3. Share a file with the community</B></li>
	*<li><b>4: Go to Homepage / Discover / Communities</b></li>
	*<li><b>5: Go to the story of the file shared with the community and "Save This"</b></li>
	*<li><b>6: Go to Homepage / Saved / Communities</b></li>
	*<li><b>Verify: Verify that the story of the file shared with the community is saved</b></li>
	*<li><b>7: Go back to the community and share a new version of the file</b></li>
	*<li><b>8: Go to Homepage / Discover / Communities</b></li>
	*<li><b>Verify: Verify that the story is now of the new version of the file shared with the community</b></li>
	*<li><b>9: Go to Homepage / Saved / Communities</b></li>
	*<li><b>Verify: Verify that the story is still of the file shared with the community in saved</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/D4CE7F362E381DB0852579B2005B3779">TTT: AS - Saved - 00051 - Community File Shared New Version Added After Story Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_PublicCommunity_UpdateSharedFileAfterSaved() {
		
		/**
		 * To avoid duplicate news stories appearing in the AS, this test case will use User 3 (as User 1) and User 4 (as User 2)
		 */
		ui.startTest();
		
		// User 1 will now share a file with the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunityUser3, baseFile, testUser3, filesAPIUser3);
				
		// Create the news stories to be saved and used for verifications
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseCommunityUser3.getName(), testUser3.getDisplayName());
				
		if(isOnPremise) {
			// Create the news story required to save the file shared event
			String fileSharedEventEntry = CommunityFileNewsStories.getShareTheFileWithCommunityNewsStory(ui, baseFile.getRename() + baseFile.getExtension(), baseCommunityUser3.getName(), testUser3.getDisplayName());
					
			// Save the file shared event news story using the API
			ProfileEvents.saveNewsStory(profilesAPIUser3, fileSharedEventEntry, true);
					
			// Log in as User 1 and go to the Saved view
			LoginEvents.loginAndGotoSaved(ui, testUser3, false);
		} else {
			// Log in as User 1 and go to the Discover view
			LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
											
			// User 1 will now save the like comment event news story using the UI
			UIEvents.saveNewsStoryUsingUI(ui, fileSharedEvent);
											
			// Navigate to the Saved view
			UIEvents.gotoSaved(ui);
		}
				
		// Verify that the file shared event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{fileSharedEvent}, TEST_FILTERS, true);
		
		// User 1 will now upload a new version of the file shared with the community
		BaseFile baseFileNewVersion = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.updateFileVersion(publicCommunityUser3, communityFile, baseFileNewVersion, testUser3, filesAPIUser3);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news story to be verified
		String fileEditedEvent = CommunityFileNewsStories.getEditFileNewsStory(ui, testUser3.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the file edited event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileEditedEvent}, filter, true);
			
			// Verify that the file shared event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileSharedEvent}, null, false);
		}
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the file shared event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileSharedEvent}, filter, true);
			
			// Verify that the file edited event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{fileEditedEvent}, null, false);
		}
		ui.endTest();
	}
}