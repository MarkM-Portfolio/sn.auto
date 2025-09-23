package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.communities;

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
/* Copyright IBM Corp. 2015, 2016                                	 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Discovery_PrivateCommunity extends SetUpMethodsFVT {

	private final String TEST_FILTERS_COMMUNITY[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	private final String TEST_FILTERS_COMMUNITY_BOOKMARK[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterBookmarks };

	private APICommunitiesHandler communitiesAPIUser1;
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

		// User 1 will now create a restricted community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void deleteAllCommunties() {

		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_createPrivateCommunity()</li>
	 * <li><B>Step: User 1 log into Communities</B></li>
	 * <li><B>Step: User 1 open a community with private access that you have owner access to</B></li>
	 * <li><B>Step: Log in to Home as User 2</B></li>
	 * <li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	 * <li><B>Step: User 2 filter by Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.created is NOT seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F2F57673B4D1ED23852578760079E764">TTT - DISC - COMMUNITIES - 00010 - COMMUNITY.CREATED - PRIVATE COMMUNITY (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_createPrivateCommunity() {

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commCreated = CommunityNewsStories.getCreateCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create community event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commCreated, baseCommunity.getDescription()}, TEST_FILTERS_COMMUNITY, false);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> test_createPrivateCommunityBookmark()</li>
	 * <li><B>Step: User 1 log into Communities</B></li>
	 * <li><B>Step: User 1 open a community with private access that you have owner access to</B></li>
	 * <li><B>Step: User 1 create a bookmark</B></li>
	 * <li><B>Step: Log in to Home as User 2</B></li>
	 * <li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	 * <li><B>Step: User 2 filter by Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.bookmark.created is NOT seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D54BA291B48173FB852578760079E767">TTT - DISC - COMMUNITIES - 00020 - COMMUNITY.BOOKMARK.CREATED - PRIVATE COMMUNITY (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_createPrivateCommunityBookmark() {

		String testName = ui.startTest();

		// User 1 create a community bookmark
		BaseDogear baseDogear = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().IbmURL, baseCommunity);
		CommunityBookmarkEvents.createBookmark(restrictedCommunity, baseDogear, testUser1, communitiesAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String bookmarkCreated = CommunityBookmarkNewsStories.getAddBookmarkNewsStory(ui, baseDogear.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the create new bookmark event is NOT displayed in any of the views
		if(isOnPremise){
			HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{bookmarkCreated, baseDogear.getDescription()}, TEST_FILTERS_COMMUNITY_BOOKMARK, false);
		} else {
			HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{bookmarkCreated, baseDogear.getDescription()}, TEST_FILTERS_COMMUNITY, false);
		}
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_BookmarkEdit_PrivateCommunity()</li>
	 * <li><B>Step: User 1 log into Communities</B></li>
	 * <li><B>Step: User 1 open a community with private access that you have owner access to</B></li>
	 * <li><B>Step: User 1 update an existing bookmark</B></li>
	 * <li><B>Step: Log in to Home as User 2</B></li>
	 * <li><B>Step: User 2 go to Home \ Activity Stream \ Discover</B></li>
	 * <li><B>Step: User 2 filter by Communities</B></li>
	 * <li><B>Verify: Verify that the news story for community.bookmark.updated is NOT seen</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6FB464691A40FAB7852579BC005976FA">TTT - DISC - COMMUNITIES - 00050 - COMMUNITY.BOOKMARK.UPDATED - PRIVATE COMMUNITY (NEG)</a></li>	
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_BookmarkEdit_PrivateCommunity() {

		String testName = ui.startTest();

		// User 1 create a community bookmark and edit the description of the bookmark
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseDogear baseDogear = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().Tv3URL, baseCommunity);
		CommunityBookmarkEvents.createBookmarkAndEditDescription(restrictedCommunity, baseDogear, testUser1, communitiesAPIUser1, editedDescription);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String bookmarkUpdated = CommunityBookmarkNewsStories.getUpdateBookmarkNewsStory(ui, baseDogear.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Set the filters to be tested
		String[] THIS_TEST_FILTERS;
		if(isOnPremise) {
			THIS_TEST_FILTERS = TEST_FILTERS_COMMUNITY_BOOKMARK;
		} else {
			THIS_TEST_FILTERS = TEST_FILTERS_COMMUNITY;
		}
		
		// Verify that the update bookmark event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{bookmarkUpdated, editedDescription, baseDogear.getDescription()}, THIS_TEST_FILTERS, false);
		
		ui.endTest();
	}			
}