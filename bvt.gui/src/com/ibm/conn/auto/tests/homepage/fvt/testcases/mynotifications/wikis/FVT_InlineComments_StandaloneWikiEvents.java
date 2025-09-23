package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.wikis;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.wikis.WikiNewsStories;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

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
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_StandaloneWikiEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = {};

	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseWiki baseWiki;
	private User testUser1, testUser2;
	private Wiki publicWiki;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		// User 1 will now create a public wiki
		baseWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(baseWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the wiki created during the test
		wikisAPIUser1.deleteWiki(publicWiki);
	}

	/**
	* test_WikiPageComment_StandaloneWiki() 
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
	@Test(groups = {"fvtonprem"})
	public void test_WikiPageComment_StandaloneWiki() {

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage wikiPage = WikiEvents.createWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// User 2 will now like the wiki page
		WikiEvents.likeWikiPage(wikiPage, testUser2, wikisAPIUser2);
		
		// User 2 will now post 4 comments to the wiki page
		String[] user2Comments = new String[4];
		WikiComment[] user2WikiComments = new WikiComment[4];
		for(int index = 0; index < user2Comments.length; index ++) {
			user2Comments[index] = Data.getData().commonComment + Helper.genStrongRand();
			user2WikiComments[index] = WikiEvents.addCommentToWikiPage(wikiPage, testUser2, wikisAPIUser2, user2Comments[index]);
		}
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnWikiPageEvent = WikiNewsStories.getCommentOnYourWikiPageNewsStory_User(ui, baseWikiPage.getName(), baseWiki.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on wiki page event and User 2's final 2 comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), user2Comments[2], user2Comments[3]}, filter, true);
			
			// Verify that the first 2 comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1]}, null, false);
		}
		
		// User 2 will now delete the last 2 comments posted to the wiki page
		WikiEvents.deleteCommentOnWikiPage(user2WikiComments[3], testUser2, wikisAPIUser2);
		WikiEvents.deleteCommentOnWikiPage(user2WikiComments[2], testUser2, wikisAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that the first 2 comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}
}