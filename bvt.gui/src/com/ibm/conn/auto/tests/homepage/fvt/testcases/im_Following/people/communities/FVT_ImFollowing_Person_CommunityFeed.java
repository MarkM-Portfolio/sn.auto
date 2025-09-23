package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FeedBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFeedEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFeedNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 09/2015
 */

public class FVT_ImFollowing_Person_CommunityFeed extends SetUpMethodsFVT {
	
	private String TEST_FILTERS[];
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity basePublicCommunity, baseModeratedCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);	
				
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 2 will follow User 1 through API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);

		if(isOnPremise){
			TEST_FILTERS = new String[3];
			TEST_FILTERS[2] = HomepageUIConstants.FilterPeople;
		}
		else{
			TEST_FILTERS = new String[2];
		}
		
		// Set the common filters to be tested
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterCommunities;
		
		// User 1 will now create a moderated community and will add the Feeds widget to that community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.FEEDS, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community and will add the Feeds widget to that community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.FEEDS, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community and will add the Feeds widget to that community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseRestrictedCommunity, BaseWidget.FEEDS, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {

		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);	
	}

	/**
	*<ul>
	*<li><B>Name: test_Person_AddFeed_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with public access that you own</B></li>	
	*<li><B>Step: testUser 1 Create a feed</B></li>		
	*<li><B>Step: testUser 2 Log in to Home, who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.feed.created story is displayed within the Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87DEBAA512860EA3852578FB0051ABEE">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00191 - community.feed.created - PUBLIC COMMUNITY</a></li>
	*</ul>
	*@author Srinivas Vechha
	*/	
	@Test(groups ={"fvtonprem"})
	public void test_Person_AddFeed_PublicCommunity() {
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(publicCommunity, baseFeed, testUser1, communitiesAPIUser1);

		//User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), basePublicCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();			
	}

	/**
	*<ul>
	*<li><B>Name: test_Person_AddFeed_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>	
	*<li><B>Step: testUser 1 Create a feed</B></li>		
	*<li><B>Step: testUser 2 Log in to Home, who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.feed.created story is displayed within the Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8020E1CC05D3D634852578FB0051AD28">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00192 - community.feed.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*@author Srinivas Vechha
	*/	
	@Test(groups ={"fvtonprem"})
	public void test_Person_AddFeed_ModerateCommunity() {
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(moderatedCommunity, baseFeed, testUser1, communitiesAPIUser1);

		//User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_AddFeed_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>	
	*<li><B>Step: testUser 1 Open a community with Private access that you own</B></li>	
	*<li><B>Step: testUser 1 Create a feed</B></li>		
	*<li><B>Step: testUser 2 Log in to Home, who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & people</B></li>
	*<li><B>Verify: Verify that the community.feed.created story is NOT displayed within the Communities and People view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2979A3C66DCF8486852578FB0051AE45">TTT - AS - FOLLOW - PERSON - COMMUNITIES - 00193 - community.feed.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*@author Srinivas Vechha
	*/	
	@Test(groups ={"fvtonprem"})
	public void test_Person_AddFeed_PrivateCommunity() {
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(restrictedCommunity, baseFeed, testUser1, communitiesAPIUser1);

		//User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, false);

		ui.endTest();				
	}	
}