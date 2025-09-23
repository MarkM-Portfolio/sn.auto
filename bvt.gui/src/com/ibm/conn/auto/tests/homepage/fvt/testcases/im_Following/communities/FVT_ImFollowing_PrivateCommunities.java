package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBookmarkEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBookmarkNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015 		                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 20/02/2014
 */

public class FVT_ImFollowing_PrivateCommunities extends SetUpMethodsFVT {
		
	private final String[] TEST_FILTERS_COMMUNITIES = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	private final String[] TEST_FILTERS_COMMUNITIES_BOOKMARKS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBookmarks, HomepageUIConstants.FilterCommunities  };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseCommunity;
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
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		// User 1 will now create a restricted community with User 2 added as a member and a follower
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollower(baseCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_Create_privateCommunity()</li>
	 * <li><B>Step: Log in to Communities as user 1</B></li>
	 * <li><B>Step: Create a new community with PRIVATE access as user 1, add user 2 as a member</B></li>
	 * <li><B>Step: Have User 2 FOLLOW this community</B></li>
	 * <li><B>Step: Log in to Home as user 2</B></li>
	 * <li><B>Step: Go to Homepage \ All Updates \ Communities </B></li>
	 * <li><B>Verify: Verify that the news story for community.created is NOT seen in the Communities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FCB619B9CD3ECC31852578F700425B5D">TTT - AS - FOLLOW - COMMUNITY - 00013 - community.created - PRIVATE COMMUNITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test (groups ={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_Create_privateCommunity(){
		
		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createCommunityEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create community event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createCommunityEvent}, TEST_FILTERS_COMMUNITIES, false);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddBookmark_privateCommunity()</li>
	 * <li><B>Step: Log in to Communities</B></li>
	 * <li><B>Step: Open a community with private access that you own</B></li>
	 * <li><B>Step: Create a bookmark</B></li>
	 * <li><B>Step: Log in to Home as a different user who is following the community</B></li>
	 * <li><B>Step: Go to Homepage \ All Updates \ Communities</B></li>
	 * <li><B>Step: Go to Homepage \ All Updates \ Bookmarks</B></li>
	 * <li><B>Verify: Verify that the news story for community.bookmark.created is seen in the Communities and Bookmarks view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F53E6FB88F430E72852578F7004A6269">TTT - AS - FOLLOW - COMMUNITY - 00033 - community.bookmark.created - PRIVATE COMMUNITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test (groups ={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddBookmark_privateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a bookmark in the community
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().bbcURL, baseCommunity);
		CommunityBookmarkEvents.createBookmark(restrictedCommunity, baseBookmark, testUser1, communitiesAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createBookmarkEvent = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseBookmark.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		String[] THIS_TEST_FILTERS;
		if(isOnPremise) {
			THIS_TEST_FILTERS = TEST_FILTERS_COMMUNITIES_BOOKMARKS;
		} else {
			THIS_TEST_FILTERS = TEST_FILTERS_COMMUNITIES;
		}
		
		// Verify that the create bookmark event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, baseBookmark.getDescription()}, THIS_TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_BookmarkEdit_privateCommunity()</li>
	 * <li><B>Step: Log in to Communities</B></li>
	 * <li><B>Step: Open a community with private access that you own</B></li>
	 * <li><B>Step: Update an existing bookmark</B></li>
	 * <li><B>Step: Log in to Home as a different user who is following the community</B></li>
	 * <li><B>Step: Go to Homepage \ I'm Following \ All, Bookmarks & Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.bookmark.updated is NOT seen in any of the views</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/789087231DCD8572852579BB005E2F3A">TTT - AS - FOLLOW - COMMUNITY - 00136 - community.bookmark.updated - PRIVATE COMMUNITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */	
	@Test (groups ={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_BookmarkEdit_privateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a bookmark in the community and will edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().Tv3URL, baseCommunity);
		CommunityBookmarkEvents.createBookmarkAndEditDescription(restrictedCommunity, baseBookmark, testUser1, communitiesAPIUser1, editedDescription);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String createBookmarkEvent = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseBookmark.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateBookmarkEvent = CommunityBookmarkNewsStories.getUpdateBookmarkNewsStory(ui, baseBookmark.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		String[] THIS_TEST_FILTERS;
		if(isOnPremise) {
			THIS_TEST_FILTERS = TEST_FILTERS_COMMUNITIES_BOOKMARKS;
		} else {
			THIS_TEST_FILTERS = TEST_FILTERS_COMMUNITIES;
		}
		for(String filter : THIS_TEST_FILTERS) {
			// Verify that the create bookmark event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createBookmarkEvent, baseBookmark.getDescription()}, filter, true);
			
			// Verify that the update bookmark event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateBookmarkEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}		
}