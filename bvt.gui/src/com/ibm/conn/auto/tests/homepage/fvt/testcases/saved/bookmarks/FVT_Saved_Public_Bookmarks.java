package com.ibm.conn.auto.tests.homepage.fvt.testcases.saved.bookmarks;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseDogear.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.dogear.DogearEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.dogear.DogearNewsStories;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:		Anthony Cox
 * Date:		2nd September 2015
 */

public class FVT_Saved_Public_Bookmarks extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBookmarks };
	
	private APIDogearHandler bookmarksAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private BaseDogear baseBookmark1, baseBookmark2;
	private Bookmark publicBookmark1, publicBookmark2;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		bookmarksAPIUser1 = initialiseAPIDogearHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create the first of two public bookmarks
		baseBookmark1 = DogearBaseBuilder.buildBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().bbcURL, Access.PUBLIC);
		publicBookmark1 = DogearEvents.addBookmark(baseBookmark1, testUser1, bookmarksAPIUser1);
		
		// User 1 will now create the second of two public bookmarks
		baseBookmark2 = DogearBaseBuilder.buildBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().skyNewsURL, Access.PUBLIC);
		publicBookmark2 = DogearEvents.addBookmark(baseBookmark2, testUser1, bookmarksAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the bookmarks created during the test
		bookmarksAPIUser1.deleteBookmark(publicBookmark1);
		bookmarksAPIUser1.deleteBookmark(publicBookmark2);
	}
	
	/**
	* test_PublicBookmark_SavingBookmarkStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections - Go to Bookmarks</B></li>
	*<li><B>2. User 1 create a bookmark</B></li>
	*<li><B>3. User 1 go to Homepage / Discover / Bookmarks</B></li>
	*<li><B>4. User 1 go to the story of the bookmark creation and mark it as Saved</B></li>
	*<li><B>Verify: User 1 - Once the 'Save this' link has been clicked it should be blackened and unclickable</B></li>
	*<li><b>5: User 1 go to Homepage / Saved / Bookmarks</b></li>
	*<li><B>Verify: User 1 should see the bookmark added in Homepage / Saved / Bookmarks</B></li>
	*<li><b>6: User 1 notify another User 2 of the bookmark</b></li>
	*<li><b>7: User 2 go to Homepage / My Notifications / For Me / Bookmarks</b></li>
	*<li><b>8: User 2 mark the story of the created bookmark as Saved</b></li>
	*<li><B>Verify: User 2 - Once the 'Save this' link has been clicked it should be blackened and unclickable</B></li>
	*<li><b>9: User 2 go to Homepage / Saved / Bookmarks</b></li>
	*<li><B>Verify: User 2 should see the bookmark added in Homepage / Saved / Bookmarks</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/D71717386FE1471485257936004E4282">TTT: AS - Saved - 00011 - Marking Bookmark Stories as Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicBookmark_SavingBookmarkStories() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be saved and verified
		String createBookmarkEvent = DogearNewsStories.getCreateBookmarkNewsStory(ui, baseBookmark1.getTitle(), testUser1.getDisplayName());
		
		// Save the create blog event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, createBookmarkEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Verify that the create bookmark event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, baseBookmark1.getDescription()}, TEST_FILTERS, true);
		
		// User 1 will now notify User 2 about the bookmark
		DogearEvents.notifyUserAboutBookmark(publicBookmark1, bookmarksAPIUser1, profilesAPIUser1, profilesAPIUser2);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, true);
		
		// Create the news story to be saved
		String notifiedYouEvent = DogearNewsStories.getNotifiedAboutTheFollowingBookmarksNewsStory_UserNotifiedYou(ui, testUser1.getDisplayName());
		
		// Save the notification event news story using the UI
		UIEvents.saveNewsStoryUsingUI(ui, notifiedYouEvent);
		
		// Navigate to the Saved view
		UIEvents.gotoSaved(ui);
		
		// Create the news story to be verified
		String notifiedUserEvent = DogearNewsStories.getNotifiedAboutTheFollowingBookmarksNewsStory_UserNotifiedUser(ui, testUser2.getDisplayName(), testUser1.getDisplayName());
		
		// Verify that the notification event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{notifiedUserEvent, baseBookmark1.getTitle(), baseBookmark1.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	* test_PublicBookmark_RemovingSavedStories()
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1. User 1 log into Connections</B></li>
	*<li><B>2. User 1 go to Homepage / Saved / Bookmarks</B></li>
	*<li><B>3. Click the 'X' in the story related to the Bookmark that had been created by User 1</B></li>
	*<li><b>Verify: User 1 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 1 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>5: Go to Homepage / Discover / Bookmarks</b></li>
	*<li><B>Verify: User 1 - The story in Homepage / Discover / Bookmarks should have a clickable "Save this" link again</b></li>
	*<li><b>6: User 2 log into Connections</b></li>
	*<li><b>7: Go to Homepage / Saved / Bookmarks</b></li>
	*<li><b>8: Click the 'X' in the story related to the Bookmark that you have been notified by User 1</b></li>
	*<li><b>Verify: User 2 - Once the "X" is clicked the user will get a confirmation dialog</b></li>
	*<li><b>Verify: User 2 - When confirm removing from saved will get a green message to confirm it has been removed</b></li>
	*<li><b>9: Go to Homepage / My Notifications / For Me / Bookmarks</b></li>
	*<li><B>Verify: User 2 - The story in Homepage / My Notifications / For Me / Bookmarks should have a clickable "Save this" link again</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/C8D77A87DCDBD2388525793B0035E58F">TTT: AS - Saved - 00012 - Removing Bookmark Stories from Saved</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void test_PublicBookmark_RemovingSavedStories() {
		
		/**
		 * In order to prevent duplicate / identical news stories being saved and verified - this test case
		 * will use User 3 as User 2.
		 */
		ui.startTest();
		
		// Create the news story to be saved and verified
		String createBookmarkEvent = DogearNewsStories.getCreateBookmarkNewsStory(ui, baseBookmark2.getTitle(), testUser1.getDisplayName());
		
		// Save the news story using the API method
		ProfileEvents.saveNewsStory(profilesAPIUser1, createBookmarkEvent, true);
		
		// User 1 will now notify User 2 about the bookmark
		DogearEvents.notifyUserAboutBookmark(publicBookmark2, bookmarksAPIUser1, profilesAPIUser1, profilesAPIUser3);
		
		// Create the news story to be saved and verified
		String notifiedUserEvent = DogearNewsStories.getNotifiedAboutTheFollowingBookmarksNewsStory_UserNotifiedUser(ui, testUser3.getDisplayName(), testUser1.getDisplayName());
		
		// Save the news story using the API method
		ProfileEvents.saveNewsStory(profilesAPIUser3, notifiedUserEvent, false);
		
		// Log in as User 1 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser1, false);
		
		// Verify that the create bookmark event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, baseBookmark2.getDescription()}, TEST_FILTERS, true);
		
		// Delete the create bookmark event from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, createBookmarkEvent);
		
		// Verify that the create bookmark event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, baseBookmark2.getDescription()}, TEST_FILTERS, false);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Verify that the create bookmark event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, createBookmarkEvent);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Saved view
		LoginEvents.loginAndGotoSaved(ui, testUser3, true);
		
		// Verify that the notification event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{notifiedUserEvent, baseBookmark2.getTitle(), baseBookmark2.getDescription()}, TEST_FILTERS, true);
		
		// Delete the notification event from the Saved view
		UIEvents.removeNewsStoryFromSavedViewUsingUI(ui, notifiedUserEvent);
		
		// Verify that the notification event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{notifiedUserEvent, baseBookmark2.getTitle(), baseBookmark2.getDescription()}, TEST_FILTERS, false);
		
		// Navigate to the My Notifications view
		UIEvents.gotoMyNotifications(ui);
		
		// Create the news story to be verified
		String notifiedYouEvent = DogearNewsStories.getNotifiedAboutTheFollowingBookmarksNewsStory_UserNotifiedYou(ui, testUser1.getDisplayName());
		
		// Verify that the notification event news story now has a 'Save This' link displayed alongside it
		HomepageValid.verifyNewsStorySaveThisLinkIsDisplayed(ui, notifiedYouEvent);
		
		ui.endTest();
	}
}