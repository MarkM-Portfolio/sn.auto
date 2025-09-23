package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
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
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_CommunityWikiEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2;
	private Wiki moderatedCommunityWiki, publicCommunityWiki, restrictedCommunityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		// User 1 will now create a moderated community with the wiki widget added
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a public community with the wiki widget added
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community with the wiki widget added and User 2 added as a member
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.WIKI, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve all of the community wikis for use in the tests
		moderatedCommunityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
		publicCommunityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		restrictedCommunityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	} 

	/**
	* test_WikiPageComment_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a wiki</B></li>
	*<li><B>Step: User 1 add a wiki page</B></li>
	*<li><B>Step: User 2 like the wiki page and add 4 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last 2 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><B>Verify: Verify there are no comments inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A784AA78263904B585257E2F0036A462">TTT - INLINE COMMENTS - 00070 - WIKI EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_WikiPageComment_WithPublicCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(publicCommunityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// User 2 will now like the community wiki page
		CommunityWikiEvents.likeWikiPage(communityWikiPage, testUser2, wikisAPIUser2);
		
		// User 2 will now post 4 comments to the wiki page
		String[] user2Comments = { Data.getData().commonComment + Helper.genStrongRand(),  Data.getData().commonComment + Helper.genStrongRand(), Data.getData().commonComment + Helper.genStrongRand(), Data.getData().commonComment + Helper.genStrongRand() };
		WikiComment[] user2WikiComments = CommunityWikiEvents.addMultipleCommentsToWikiPage(communityWikiPage, testUser2, wikisAPIUser2, user2Comments);
		
		// Log in as User 1 and navigate to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnYourWikiPageNewsStory_User(ui, baseWikiPage.getName(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and final two comments posted are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), user2Comments[2], user2Comments[3]}, filter, true);
			
			// Verify that the first two comments posted are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1]}, null, false);
		}
		
		// User 2 will now delete the last two comments posted to the wiki page
		CommunityWikiEvents.deleteCommentOnWikiPage(user2WikiComments[2], testUser2, wikisAPIUser2);
		CommunityWikiEvents.deleteCommentOnWikiPage(user2WikiComments[3], testUser2, wikisAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that all of the comments posted are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_WikiPageComment_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a wiki</B></li>
	*<li><B>Step: User 1 add a wiki page</B></li>
	*<li><B>Step: User 2 like the wiki page and add 4 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last 2 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><B>Verify: Verify there are no comments inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A784AA78263904B585257E2F0036A462">TTT - INLINE COMMENTS - 00070 - WIKI EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_WikiPageComment_WithModCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(moderatedCommunityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// User 2 will now like the community wiki page
		CommunityWikiEvents.likeWikiPage(communityWikiPage, testUser2, wikisAPIUser2);
		
		// User 2 will now post 4 comments to the wiki page
		String[] user2Comments = { Data.getData().commonComment + Helper.genStrongRand(),  Data.getData().commonComment + Helper.genStrongRand(), Data.getData().commonComment + Helper.genStrongRand(), Data.getData().commonComment + Helper.genStrongRand() };
		WikiComment[] user2WikiComments = CommunityWikiEvents.addMultipleCommentsToWikiPage(communityWikiPage, testUser2, wikisAPIUser2, user2Comments);
		
		// Log in as User 1 and navigate to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnYourWikiPageNewsStory_User(ui, baseWikiPage.getName(), baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and final two comments posted are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), user2Comments[2], user2Comments[3]}, filter, true);
			
			// Verify that the first two comments posted are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1]}, null, false);
		}
		
		// User 2 will now delete the last two comments posted to the wiki page
		CommunityWikiEvents.deleteCommentOnWikiPage(user2WikiComments[2], testUser2, wikisAPIUser2);
		CommunityWikiEvents.deleteCommentOnWikiPage(user2WikiComments[3], testUser2, wikisAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that all of the comments posted are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_WikiPageComment_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a wiki</B></li>
	*<li><B>Step: User 1 add a wiki page</B></li>
	*<li><B>Step: User 2 like the wiki page and add 4 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete the last 2 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are returned inline</B></li>
	*<li><B>Verify: Verify there are no comments inline</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A784AA78263904B585257E2F0036A462">TTT - INLINE COMMENTS - 00070 - WIKI EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_WikiPageComment_PrivateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(restrictedCommunityWiki, baseWikiPage, testUser2, wikisAPIUser2);
		
		// User 2 will now like the community wiki page
		CommunityWikiEvents.likeWikiPage(communityWikiPage, testUser1, wikisAPIUser1);
		
		// User 2 will now post 4 comments to the wiki page
		String[] user2Comments = { Data.getData().commonComment + Helper.genStrongRand(),  Data.getData().commonComment + Helper.genStrongRand(), Data.getData().commonComment + Helper.genStrongRand(), Data.getData().commonComment + Helper.genStrongRand() };
		WikiComment[] user2WikiComments = CommunityWikiEvents.addMultipleCommentsToWikiPage(communityWikiPage, testUser1, wikisAPIUser1, user2Comments);
		
		// Log in as User 1 and navigate to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnYourWikiPageNewsStory_User(ui, baseWikiPage.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and final two comments posted are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), user2Comments[2], user2Comments[3]}, filter, true);
			
			// Verify that the first two comments posted are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1]}, null, false);
		}
		
		// User 2 will now delete the last two comments posted to the wiki page
		CommunityWikiEvents.deleteCommentOnWikiPage(user2WikiComments[2], testUser1, wikisAPIUser1);
		CommunityWikiEvents.deleteCommentOnWikiPage(user2WikiComments[3], testUser1, wikisAPIUser1);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that all of the comments posted are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}	
}