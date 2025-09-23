package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.communities;

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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBookmarkNewsStories;
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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Srinivas Vechha
 * Date: 5/06/2015
 */

public class FVT_ImFollowing_Tags_ModeratedCommunity extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	private final String TEST_FILTERS_BOOKMARKS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBookmarks, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2 ;
	private BaseCommunity baseCommunity, baseCommunityWithTag;
	private Community moderatedCommunity, moderatedCommunityWithTag;
	private String tagToFollow;
	private User testUser1 , testUser2;	
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);

		// Log in with User 1 and follow the tag
		tagToFollow = Data.getData().commonTag + Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);

		// User 2 will now create a moderated community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser2, communitiesAPIUser2);

		// User 2 will now create a moderated community with the tag User 1 is following
		baseCommunityWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED, tagToFollow);
		moderatedCommunityWithTag = CommunityEvents.createNewCommunity(baseCommunityWithTag, testUser2, communitiesAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void tearDown() {

		// Delete all of the communities created during the test
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
		communitiesAPIUser2.deleteCommunity(moderatedCommunityWithTag);	
	}

	/**
	*<ul>
	*<li><B>Name: test_Tags_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 create a Moderated community and add the tag that User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the community.created story is displayed in Homepage / All Updates filtered by Tags and Communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/40B673E03555309F852578FC0051843C">TTT - AS - FOLLOW - TAG - COMMUNITIES - 00102 - community.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Tags_ModeratedCommunity(){
	
		ui.startTest();

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);

		// Create the news story to be verified
		String commCreationEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseCommunityWithTag.getName(), testUser2.getDisplayName());

		// Verify that the create community event and description are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commCreationEvent, baseCommunityWithTag.getDescription()}, TEST_FILTERS, true);	

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_Tags_AddBookmark_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with Moderated access</B></li>
	*<li><B>Step: testUser 2 create a public bookmark and add the tag that User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the community.bookmark.created story is displayed in Homepage / All Updates filtered by Tags and Communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C87204F59B96945852578FC005187A3">TTT - AS - FOLLOW - TAG - COMMUNITIES - 00112 - community.bookmark.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Tags_AddBookmark_ModeratedCommunity(){
	
		String testName = ui.startTest();
		 
		// User 2 will now create a bookmark in the community
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogearWithCustomTag(testName + Helper.genStrongRand(), Data.getData().bbcURL, baseCommunity, tagToFollow);
		CommunityBookmarkEvents.createBookmark(moderatedCommunity, baseBookmark, testUser2, communitiesAPIUser2);
		 
		// Log in as User 1 and navigate to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		 
		// Create the news story to be verified
		String createBookmarkEvent = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseBookmark.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		 
		// Create the array of filters to be tested
		String[] FILTERS_FOR_TEST;
		if(isOnPremise) {
			FILTERS_FOR_TEST = TEST_FILTERS_BOOKMARKS;
		} else {
			FILTERS_FOR_TEST = TEST_FILTERS;
		}
		// Verify that the create bookmark event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBookmarkEvent, baseBookmark.getDescription()}, FILTERS_FOR_TEST, true);
		 
		ui.endTest();
	}
	 
	/**
	*<ul>
	*<li><B>Name: test_Tags_BookmarkUpdate_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with Moderated access</B></li>
	*<li><B>Step: testUser 2 update a public bookmark with the the tag that User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, BookMarks & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the community.bookmark.updated story is displayed in Homepage / All Updates filtered by Tags and Communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D29197C10645C691852579BB0073817F">TTT - AS - FOLLOW - TAG - COMMUNITIES - 00132 - community.bookmark.updated - MODERATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_Tags_BookmarkUpdate_ModeratedCommunity(){
	
		String testName = ui.startTest();
		 
		// User 2 will now create a bookmark in the community and will update the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogearWithCustomTag(testName + Helper.genStrongRand(), Data.getData().Tv3URL, baseCommunity, tagToFollow);
		CommunityBookmarkEvents.createBookmarkAndEditDescription(moderatedCommunity, baseBookmark, testUser2, communitiesAPIUser2, editedDescription);
		 
		// Log in as User 1 and navigate to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
				 
		// Create the news story to be verified
		String updateBookmarkEvent = CommunityBookmarkNewsStories.getUpdateBookmarkNewsStory(ui, baseBookmark.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
				 
		// Create the array of filters to be tested
		String[] FILTERS_FOR_TEST;
		if(isOnPremise) {
			FILTERS_FOR_TEST = TEST_FILTERS_BOOKMARKS;
		} else {
			FILTERS_FOR_TEST = TEST_FILTERS;
		}		 
		// Verify that the update bookmark event and updated description are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateBookmarkEvent, editedDescription}, FILTERS_FOR_TEST, true);
				 
		ui.endTest();
	}	
}