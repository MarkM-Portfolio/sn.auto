package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.communities;

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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Srinivas Vechha
 * Date: 09/2015
 */

public class FVT_ImFollowing_Tags_CommunitiesFeed extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private BaseCommunity basePublicCommunity, baseModeratedCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private String tagToFollow;
	private User testUser1, testUser2;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);

		// Log in with User 1 and follow the tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);

		// User 2 will now create a moderated community and will add the Feeds widget to that community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.FEEDS, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a public community and will add the Feeds widget to that community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.FEEDS, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a restricted community and will add the Feeds widget to that community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseRestrictedCommunity, BaseWidget.FEEDS, isOnPremise, testUser2, communitiesAPIUser2);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown() {

		// Delete all of the communities created during the test
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
		communitiesAPIUser2.deleteCommunity(publicCommunity);
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
	}

	/**
	*<ul>
	*<li><B>Name: test_Tags_AddFeed_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>		
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are owner of and has public access</B></li>	
	*<li><B>Step: testUser 2 create a feed and add the tag that User 1 is following	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the community.feed.created story is displayed in Homepage / All Updates filtered by Tags and Communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/196DCE81B921CB56852578FC00518A4B">TTT - AS - FOLLOW - TAG - COMMUNITIES - 00121 - community.feed.created - PUBLIC COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void addFeedToPublicCommunity(){
	
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeedWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityFeedEvents.createFeed(publicCommunity, baseFeed, testUser2, communitiesAPIUser2);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);

		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), basePublicCommunity.getName(), testUser2.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddFeed_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>		
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are owner of and has Moderate access</B></li>	
	*<li><B>Step: testUser 2 create a feed and add the tag that User 1 is following	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the community.feed.created story is displayed in Homepage / All Updates filtered by Tags and Communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8B0B99C13B4B5B46852578FC00518B7D">TTT - AS - FOLLOW - TAG - COMMUNITIES - 00122 - community.feed.created - MODERATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void addFeedToModerateCommunity(){
	
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeedWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityFeedEvents.createFeed(moderatedCommunity, baseFeed, testUser2, communitiesAPIUser2);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);

		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseModeratedCommunity.getName(), testUser2.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_AddFeed_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>		
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community you are owner of and has Private access</B></li>	
	*<li><B>Step: testUser 2 create a feed and add the tag that User 1 is following	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the community.feed.created story is not displayed in Homepage / All Updates filtered by Tags and Communities</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B8444FF36CEECEF7852578FC00518CDD">TTT - AS - FOLLOW - TAG - COMMUNITIES - 00123 - community.feed.created - PRIVATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test(groups ={"fvtonprem"})
	public void addFeedToPrivateCommunity(){
	
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeedWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityFeedEvents.createFeed(restrictedCommunity, baseFeed, testUser2, communitiesAPIUser2);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);

		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseRestrictedCommunity.getName(), testUser2.getDisplayName());

		// Verify that the create feed event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, false);

		ui.endTest();			
	}
}