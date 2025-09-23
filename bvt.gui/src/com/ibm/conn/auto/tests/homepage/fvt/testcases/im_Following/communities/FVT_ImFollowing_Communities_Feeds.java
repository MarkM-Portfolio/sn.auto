package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities;

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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FeedBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFeedEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFeedNewsStories;
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

public class FVT_ImFollowing_Communities_Feeds extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
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
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);

		// User 1 will now create a moderated community and will add the Feeds widget to that community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseModeratedCommunity, testUser2, communitiesAPIUser2, BaseWidget.FEEDS, testUser1, communitiesAPIUser1, isOnPremise);
		
		// User 1 will now create a public community and will add the Feeds widget to that community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(basePublicCommunity, testUser2, communitiesAPIUser2, BaseWidget.FEEDS, testUser1, communitiesAPIUser1, isOnPremise);
		
		// User 1 will now create a restricted community and will add the Feeds widget to that community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseRestrictedCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1, BaseWidget.FEEDS, isOnPremise);
	}

	@AfterClass(alwaysRun = true)
	public void deleteAllCommunties() {

		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* <li><B>Name:</B>test_AddFeed_moderateCommunity</li>
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: Log in to Communities as User 1</B></li>
	* <li><B>Step: Open a community with moderate access that you own</B></li>
	* <li><B>Step: Create a feed</B></li>
	* <li><B>Step: Log in to Home as User 2 who is following the community</B></li>
	* <li><B>Step: Go to Homepage / I'm Following / All & Communities</B></li>
	* <li><B>Verify: Verify that the news story for community.feed.created is seen in the Communities view</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4AB6E3EF2F0411A4852578F70049197D">TTT - AS - FOLLOW - COMMUNITY - 00022 - community.feed.created - MODERATE COMMUNITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups ={"fvtonprem"})
	public void test_AddFeed_moderateCommunity(){
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(moderatedCommunity, baseFeed, testUser1, communitiesAPIUser1);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
			
	}

	/**
	* <li><B>Name:</B>test_AddFeed_PublicCommunity</li>
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: Log in to Communities as User 1</B></li>
	* <li><B>Step: Open a community with public access that you own</B></li>
	* <li><B>Step: Create a feed</B></li>
	* <li><B>Step: Log in to Home as User 2 who is following the community</B></li>
	* <li><B>Step: Go to Homepage / I'm Following / All & Communities</B></li>
	* <li><B>Verify: Verify that the news story for community.feed.created is seen in the Communities view</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3A460BA699584641852578F70047A794">TTT - AS - FOLLOW - COMMUNITY - 00021 - community.feed.created - PUBLIC COMMUNITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups ={"fvtonprem"})
	public void test_AddFeed_PublicCommunity(){
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(publicCommunity, baseFeed, testUser1, communitiesAPIUser1);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), basePublicCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
			
	}

	/**
	* <li><B>Name:</B>test_AddFeed_PrivateCommunity</li>
	* <ul>
	* <li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	* <li><B>Step: Log in to Communities as User 1</B></li>
	* <li><B>Step: Open a community with private access that you own</B></li>
	* <li><B>Step: Create a feed</B></li>
	* <li><B>Step: Log in to Home as User 2 who is following the community</B></li>
	* <li><B>Step: Go to Homepage / I'm Following / All & Communities</B></li>
	* <li><B>Verify: Verify that the news story for community.feed.created is seen in the Communities view</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/24487C9EB0174303852578F700495421">TTT - AS - FOLLOW - COMMUNITY - 00023 - community.feed.created - PRIVATE COMMUNITY</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups ={"fvtonprem"})
	public void test_AddFeed_PrivateCommunity(){
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(restrictedCommunity, baseFeed, testUser1, communitiesAPIUser1);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();	
	}
}