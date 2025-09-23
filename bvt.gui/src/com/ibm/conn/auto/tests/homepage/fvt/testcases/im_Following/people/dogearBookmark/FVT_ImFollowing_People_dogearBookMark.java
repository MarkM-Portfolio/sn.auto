package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.dogearBookmark;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
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
import com.ibm.conn.auto.util.newsStoryBuilder.dogear.DogearNewsStories;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following/person) Component of IBM Connections
 * Created By: Srinivas Vechha
 * Date: 08/2015
 */

public class FVT_ImFollowing_People_dogearBookMark extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBookmarks, HomepageUIConstants.FilterPeople };
	
	private APIDogearHandler bookmarksAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseDogear basePrivateBookmark, basePrivateBookmarkForEdit, basePublicBookmark, basePublicBookmarkForEdit;
	private Bookmark privateBookmark, privateBookmarkForEdit, publicBookmark, publicBookmarkForEdit;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());	
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		bookmarksAPIUser1 = initialiseAPIDogearHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
				
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a public bookmark
		basePublicBookmark = DogearBaseBuilder.buildBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().Tv3URL, Access.PUBLIC);
		publicBookmark = DogearEvents.addBookmark(basePublicBookmark, testUser1, bookmarksAPIUser1);
		
		// User 1 will now create a public bookmark to be used for editing
		basePublicBookmarkForEdit = DogearBaseBuilder.buildBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().skyURL, Access.PUBLIC);
		publicBookmarkForEdit = DogearEvents.addBookmark(basePublicBookmarkForEdit, testUser1, bookmarksAPIUser1);
		
		// User 1 will now create a private bookmark
		basePrivateBookmark = DogearBaseBuilder.buildBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().bbcURL, Access.RESTRICTED);
		privateBookmark = DogearEvents.addBookmark(basePrivateBookmark, testUser1, bookmarksAPIUser1);
		
		// User 1 will now create a private bookmark to be used for editing
		basePrivateBookmarkForEdit = DogearBaseBuilder.buildBaseDogear(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().ebayIrlURL, Access.RESTRICTED);
		privateBookmarkForEdit = DogearEvents.addBookmark(basePrivateBookmarkForEdit, testUser1, bookmarksAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// User 1 will now delete all of the bookmarks created during the test
		bookmarksAPIUser1.deleteBookmark(privateBookmark);
		bookmarksAPIUser1.deleteBookmark(privateBookmarkForEdit);
		bookmarksAPIUser1.deleteBookmark(publicBookmark);
		bookmarksAPIUser1.deleteBookmark(publicBookmarkForEdit);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_bookmark added_PUBLIC BOOKMARK</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Bookmarks</B></li>
	*<li><B>Step: testUser 1 Create a bookmark with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, BookMarks & people</B></li>
	*<li><B>Verify: Verify that the dogear.bookmark.added story is displayed within the All, People and Bookmarks view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/91A7E3015F01CC96852578FB004F22DF">TTT - AS - FOLLOW - PERSON - BOOKMARK-DOGEAR - 00141 - dogear.bookmark.added - PUBLIC BOOKMARK</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups ={"fvtonprem"})
	public void addPublicBookMark(){
	
		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBookmarkEvent = DogearNewsStories.getCreateBookmarkNewsStory(ui, basePublicBookmark.getTitle(), testUser1.getDisplayName());
		
		// Verify that the create bookmark event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, basePublicBookmark.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdatePublicBookMark</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Bookmarks</B></li>
	*<li><B>Step: testUser 1 Edit/Change an existing bookmark with public access</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, BookMarks & People</B></li>
	*<li><B>Verify: Verify that the dogear.bookmark.edited story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/80F50C63D6845B6E852579BB005EECC6">TTT - AS - FOLLOW - PERSON - BOOKMARK-DOGEAR - 00143 - dogear.bookmark.edited - PUBLIC BOOKMARK (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups ={"fvtonprem"})
	public void UpdatePublicBookMark(){
	
		ui.startTest();
		
		// User 1 will now edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		DogearEvents.updateBookmarkDescription(publicBookmarkForEdit, editedDescription, testUser1, bookmarksAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBookmarkEvent = DogearNewsStories.getCreateBookmarkNewsStory(ui, basePublicBookmarkForEdit.getTitle(), testUser1.getDisplayName());
		String updateBookmarkEvent = DogearNewsStories.getUpdateBookmarkNewsStory(ui, basePublicBookmarkForEdit.getTitle(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create bookmark event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createBookmarkEvent, basePublicBookmarkForEdit.getDescription()}, filter, true);
			
			// Verify that the update bookmark event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateBookmarkEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Person_addedPrivateBOOKMARK</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Bookmarks</B></li>
	*<li><B>Step: testUser 1 Create a bookmark with private access</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, people & BookMarks</B></li>
	*<li><B>Verify: Verify that the dogear.bookmark.added story is not displayed within the People and Bookmarks view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/702FADDA3A3DBB01852578FB004F2499">TTT - AS - FOLLOW - PERSON - BOOKMARK-DOGEAR - 00142 - dogear.bookmark.added - PRIVATE BOOKMARK</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups ={"fvtonprem"})
	public void addedPrivateBookMark(){
	
		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBookmarkEvent = DogearNewsStories.getCreateBookmarkNewsStory(ui, basePrivateBookmark.getTitle(), testUser1.getDisplayName());
		
		// Verify that the create bookmark event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, basePrivateBookmark.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdatePrivateBOOKMARK</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Bookmarks</B></li>
	*<li><B>Step: testUser 1 Edit/Change an exiting bookmark with private access</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, people & BookMarks</B></li>
	*<li><B>Verify: Verify that the dogear.bookmark.edited story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9257649C8815ED2C852579BB005F350D">TTT - AS - FOLLOW - PERSON - BOOKMARK-DOGEAR - 00144 - dogear.bookmark.edited - PRIVATE BOOKMARK (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/
	@Test (groups ={"fvtonprem"})
	public void UpdatePrivateBookMark(){
	
		ui.startTest();
		
		// User 1 will now edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		DogearEvents.updateBookmarkDescription(privateBookmarkForEdit, editedDescription, testUser1, bookmarksAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBookmarkEvent = DogearNewsStories.getCreateBookmarkNewsStory(ui, basePrivateBookmarkForEdit.getTitle(), testUser1.getDisplayName());
		String updateBookmarkEvent = DogearNewsStories.getUpdateBookmarkNewsStory(ui, basePrivateBookmarkForEdit.getTitle(), testUser1.getDisplayName());
		
		// Verify that the create bookmark event, update bookmark event, original description and updated description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, updateBookmarkEvent, basePrivateBookmarkForEdit.getDescription(), editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}