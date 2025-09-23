package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.BookMarks;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseDogear.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.dogear.DogearEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.dogear.DogearNewsStories;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Srinivas Vechha
 * Date: 09/2015
 */

public class FVT_ImFollowing_Tags_dogearBookMark extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBookmarks, HomepageUIConstants.FilterTags };
	
	private APIDogearHandler bookmarksAPIUser2;
	private BaseDogear baseBookmarkPrivate, baseBookmarkPrivateToBeUpdated, baseBookmarkPublic, baseBookmarkPublicToBeUpdated;
	private Bookmark privateBookmark, privateBookmarkToBeUpdated, publicBookmark, publicBookmarkToBeUpdated;
	private String tagToFollow;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
						
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		bookmarksAPIUser2 = initialiseAPIDogearHandlerUser(testUser2);
		
		// Log in with User 1 and follow the tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 add a public bookmark (for the create public bookmark test case) with the tag that User 1 is following
		baseBookmarkPublic = DogearBaseBuilder.buildBaseDogearWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().IbmURL, Access.PUBLIC, tagToFollow);
		publicBookmark = DogearEvents.addBookmark(baseBookmarkPublic, testUser2, bookmarksAPIUser2);

		// User 2 add a private bookmark (for the create private bookmark test case) with the tag that User 1 is following
		baseBookmarkPrivate = DogearBaseBuilder.buildBaseDogearWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().skyURL, Access.RESTRICTED, tagToFollow);
		privateBookmark = DogearEvents.addBookmark(baseBookmarkPrivate, testUser2, bookmarksAPIUser2);

		// User 2 add a public bookmark (for the update public bookmark test case) with the tag that User 1 is following
		baseBookmarkPublicToBeUpdated = DogearBaseBuilder.buildBaseDogearWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().skyNewsURL, Access.PUBLIC, tagToFollow);
		publicBookmarkToBeUpdated = DogearEvents.addBookmark(baseBookmarkPublicToBeUpdated, testUser2, bookmarksAPIUser2);

		// User 2 add a private bookmark (for the update private bookmark test case) with the tag that User 1 is following
		baseBookmarkPrivateToBeUpdated = DogearBaseBuilder.buildBaseDogearWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Data.getData().bbcURL, Access.RESTRICTED, tagToFollow);
		privateBookmarkToBeUpdated = DogearEvents.addBookmark(baseBookmarkPrivateToBeUpdated, testUser2, bookmarksAPIUser2);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete all of the bookmarks created during the tests
		bookmarksAPIUser2.deleteBookmark(publicBookmark);
		bookmarksAPIUser2.deleteBookmark(privateBookmark);
		bookmarksAPIUser2.deleteBookmark(publicBookmarkToBeUpdated);
		bookmarksAPIUser2.deleteBookmark(privateBookmarkToBeUpdated);	
	}

	/**
	*<ul>
	*<li><B>Name: addBookmarkdogear_PublicBookMark()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 add a  public bookmark and add the tag that User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the dogear.bookmark.added story is displayed in Homepage / All Updates filtered by Tags and Bookmarks</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1575A3CF56A5E64C852578FC00507A47">TTT - AS - FOLLOW - TAG - BOOKMARK-DOGEAR - 00081 - dogear.bookmark.added - PUBLIC BOOKMARK</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void addBookmarkdogear_PublicBookMark() {
	
		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story	
		String newsStory = DogearNewsStories.getCreateBookmarkNewsStory(ui, baseBookmarkPublic.getTitle(), testUser2.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseBookmarkPublic.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: addBookmarkdogear_PrivateBookMark()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 add a  private bookmark and add the tag that User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the dogear.bookmark.added story is not displayed in Homepage / All Updates filtered by Tags and Bookmarks</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A23C53068CCFB08F852578FC00507B7E">TTT - AS - FOLLOW - TAG - BOOKMARK-DOGEAR - 00082 - dogear.bookmark.added - PRIVATE BOOKMARK</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void addBookmarkdogear_PrivateBookMark() {
	
		ui.startTest();
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story	
		String newsStory = DogearNewsStories.getCreateBookmarkNewsStory(ui, baseBookmarkPrivate.getTitle(), testUser2.getDisplayName());
		
		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseBookmarkPrivate.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: editBookmark_PublicBookMark()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 edit a  public bookmark and add the tag that User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the dogear.bookmark.edited story is displayed in Homepage / All Updates filtered by Tags and Bookmarks</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/673A9FB5E44DEA41852579BB007444C9">TTT - AS - FOLLOW - TAG - BOOKMARK-DOGEAR - 00083 - dogear.bookmark.edited - PUBLIC BOOKMARK</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void editBookmark_PublicBookMark() {
	
		ui.startTest();
		
		// User 2 will now edit the bookmark description
		String updatedContent = Data.getData().commonDescription + Helper.genStrongRand();
		DogearEvents.updateBookmarkDescription(publicBookmarkToBeUpdated, updatedContent, testUser2, bookmarksAPIUser2);
				
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
				
		// Create the news story	
		String newsStory = DogearNewsStories.getUpdateBookmarkNewsStory(ui, baseBookmarkPublicToBeUpdated.getTitle(), testUser2.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, updatedContent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: editBookmark_PrivateBookMark()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 edit a  public bookmark and add the tag that User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: verify that the dogear.bookmark.edited story is not displayed in Homepage / All Updates filtered by Tags and Bookmarks</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/170DDD41F094A492852579BB00747EBA">TTT - AS - FOLLOW - TAG - BOOKMARK-DOGEAR - 00084 - dogear.bookmark.edited - PRIVATE BOOKMARK</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void editBookmark_PrivateBookMark() {
	
		ui.startTest();
		
		// User 2 will now edit the bookmark description
		String updatedContent = Data.getData().commonDescription + Helper.genStrongRand();
		DogearEvents.updateBookmarkDescription(privateBookmarkToBeUpdated, updatedContent, testUser2, bookmarksAPIUser2);
				
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story	
		String newsStory = DogearNewsStories.getUpdateBookmarkNewsStory(ui, baseBookmarkPrivateToBeUpdated.getTitle(), testUser2.getDisplayName());
				
		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, updatedContent}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}		