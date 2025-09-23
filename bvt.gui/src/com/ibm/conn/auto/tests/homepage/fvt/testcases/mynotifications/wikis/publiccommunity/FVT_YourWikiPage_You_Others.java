package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.wikis.publiccommunity;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016	                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Roll Up of Notifcation Events] FVT UI automation for Story 141809
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/145048
 * @author Patrick Doherty
 */

public class FVT_YourWikiPage_You_Others extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3, wikisAPIUser4, wikisAPIUser5, wikisAPIUser6, wikisAPIUser7, wikisAPIUser8, wikisAPIUser9;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9;
	private Wiki communityWiki;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(9);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);
		testUser8 = listOfStandardUsers.get(7);
		testUser9 = listOfStandardUsers.get(8);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
		wikisAPIUser4 = initialiseAPIWikisHandlerUser(testUser4);
		wikisAPIUser5 = initialiseAPIWikisHandlerUser(testUser5);
		wikisAPIUser6 = initialiseAPIWikisHandlerUser(testUser6);
		wikisAPIUser7 = initialiseAPIWikisHandlerUser(testUser7);
		wikisAPIUser8 = initialiseAPIWikisHandlerUser(testUser8);
		wikisAPIUser9 = initialiseAPIWikisHandlerUser(testUser9);
		
		// User 1 will now create a public community with the Wiki widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve the community wiki for use in the test
		communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_YourWikiPage_Comment_You_Others_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a Wiki and add a wiki page</B></li>
	*<li><B>Step: User 1 comment on the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 1</B></li>
	*<li><B>Step: User 2 log into Community</B></li>
	*<li><B>Step: User 2 comment on the wiki page User 1 added</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 2</B></li>
	*<li><B>Step: User 3 to User 9 comment on the wiki page</B></li>
	*<li><B>Step: User 1 comment again on the wiki page</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / All, Wikis, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify no event appears</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} and you commented on your wiki page {wikiPageName} topic thread in the {wikiName} wiki."</B></li>
	*<li><B>Verify: Verify the event shows "You and 8 others commented on your wiki page {wikiPageName} topic thread in the {wikiName} wiki."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CEF0409203E7B7A85257DEA004B6EED">TTT - MY NOTIFICATIONS - WIKIS - 00021 - COMMENT ON YOUR OWN WIKI PAGE ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_YourWikiPage_Comment_You_Others_Rollup(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will post a comment to the wiki page
		String user1Comment1 = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, user1Comment1);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnYourWikiPageNewsStory_You(ui, baseWikiPage.getName(), baseCommunity.getName());
		
		// Verify that the comment on wiki page event and User 1's comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), user1Comment1}, TEST_FILTERS, false);
		
		// User 2 will now post a comment to the wiki page
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityWikiEvents.addCommentToWikiPage(communityWikiPage, testUser2, wikisAPIUser2, user2Comment);
		
		// Create the news story to be verified
		commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnYourWikiPageNewsStory_UserAndYou(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on wiki page event, User 1's comment and User 2'c comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), user1Comment1, user2Comment}, TEST_FILTERS, true);
		
		// Users 3 through to 9 will now post a comment on the wiki page with User 1 then posting a second comment to the wiki page
		User[] usersCommenting = { testUser3, testUser4, testUser5, testUser6, testUser7, testUser8, testUser9, testUser1 };
		APIWikisHandler[] apiUsersCommenting = { wikisAPIUser3, wikisAPIUser4, wikisAPIUser5, wikisAPIUser6, wikisAPIUser7, wikisAPIUser8, wikisAPIUser9, wikisAPIUser1 };
		String[] userComments = new String[usersCommenting.length];
		
		for(int index = 0; index < usersCommenting.length; index ++) {
			userComments[index] = Data.getData().commonComment + Helper.genStrongRand();
			CommunityWikiEvents.addCommentToWikiPage(communityWikiPage, usersCommenting[index], apiUsersCommenting[index], userComments[index]);
		}
		
		// Create the news story to be verified
		commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnYourWikiPageNewsStory_YouAndMany(ui, "8", baseWikiPage.getName(), baseCommunity.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on wiki page event, User 9's comment and User 1's second comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), userComments[6], userComments[7]}, filter, true);
			
			// Verify that User 1's first comment and all comments posted by Users 2 through to User 8 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1Comment1, user2Comment, userComments[0], userComments[1], userComments[2], userComments[3], userComments[4], userComments[5]}, null, false);
		}
		ui.endTest();	
	}
}