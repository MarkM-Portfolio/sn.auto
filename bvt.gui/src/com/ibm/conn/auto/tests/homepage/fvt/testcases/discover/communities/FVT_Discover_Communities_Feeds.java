package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.communities;

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

public class FVT_Discover_Communities_Feeds extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
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

	@AfterClass(alwaysRun = true)
	public void deleteAllCommunties() {

		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddFeed_moderateCommunity()</li>
	 * <li><B>Step: User 1 log into Communities</B></li>
	 * <li><B>Step: User 1 open a community with moderated access that you have owner access to</B></li>
	 * <li><b>Step: User 1 create a feed</b></li>
	 * <li><B>Step: Log in to Home as User 2</B></li>
	 * <li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	 * <li><B>Step: User 2 filter by Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.feed.created is seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19E7B0A3F7EF73F2852578760079E769">TTT - DISC - COMMUNITIES - 00030 - COMMUNITY.FEED.CREATED - MODERATED COMMUNITY</a></li>
	 * @author Patrick Doherty
	 */	
	@Test (groups ={"fvtonprem"})
	public void test_AddFeed_moderateCommunity() {
		
		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(moderatedCommunity, baseFeed, testUser1, communitiesAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();	
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddFeed_PublicCommunity()</li>
	 * <li><B>Step: User 1 log into Communities</B></li>
	 * <li><B>Step: User 1 open a community with public access that you have owner access to</B></li>
	 * <li><b>Step: User 1 create a feed</b></li>
	 * <li><B>Step: Log in to Home as User 2</B></li>
	 * <li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	 * <li><B>Step: User 2 filter by Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.feed.created is seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E02D2CD7ADDC1E4852578760079E768">TTT - DISC - COMMUNITIES - 00030 - COMMUNITY.FEED.CREATED - PUBLIC COMMUNITY</a></li>
	 * @author Patrick Doherty
	 */	
	@Test (groups ={"fvtonprem"})
	public void test_AddFeed_PublicCommunity() {

		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(publicCommunity, baseFeed, testUser1, communitiesAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), basePublicCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddFeed_PrivateCommunity()</li>
	 * <li><B>Step: User 1 log into Communities</B></li>
	 * <li><B>Step: User 1 open a community with private access that you have owner access to</B></li>
	 * <li><b>Step: User 1 create a feed</b></li>
	 * <li><B>Step: Log in to Home as User 2</B></li>
	 * <li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	 * <li><B>Step: User 2 filter by Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.feed.created is NOT seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/95FF83D6908E021D852578760079E76A">TTT - DISC - COMMUNITIES - 00030 - COMMUNITY.FEED.CREATED - PRIVATE COMMUNITY (NEG)</a></li>
	 * @author Patrick Doherty
	 */	
	@Test (groups ={"fvtonprem"})
	public void test_AddFeed_PrivateCommunity() {

		String testName = ui.startTest();

		// Add a new feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(restrictedCommunity, baseFeed, testUser1, communitiesAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String feedAddedEvent = CommunityFeedNewsStories.getAddFeedNewsStory(ui, baseFeed.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create feed event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{feedAddedEvent, baseFeed.getDescription()}, TEST_FILTERS, false);

		ui.endTest();
	}
}