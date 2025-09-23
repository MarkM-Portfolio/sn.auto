package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.wikis.followWikiPage;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.wikis.WikiNewsStories;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

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
 * This is a functional test for the Homepage Activity Stream (I'm Following /Wikis) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_WikiPage_PrivateWiki extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = {HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis};
	
	private APIProfilesHandler profilesAPIUser2;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseWiki baseWiki;
	private User testUser1, testUser2;
	private Wiki privateWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
				
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// Create a wiki and add User 2 as a member
		baseWiki = WikiBaseBuilder.buildBaseWikiWithOneMember(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, WikiRole.READER, testUser2, profilesAPIUser2.getUUID());
		privateWiki = WikiEvents.createWiki(baseWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the standalone wiki created during the test
		wikisAPIUser1.deleteWiki(privateWiki);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_CreateWikipage_privateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a page in the private Wiki and add user 2 as a reader of the wiki</B></li>		
	*<li><B>Step: testUser 2 log into Homepage </B></li>
	*<li><B>Step: testUser 2 Navigate to the private Wiki and follow the Wiki page </B></li>
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / Wikis
	*<li><B>Verify: Verify the wiki.page.created story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1405F477085728EC85257BD600442AA8">TTT -AS - FOLLOW - WIKI PAGE- 00012 - wiki.page.created - PRIVATE WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_createWikipage_privateWiki(){
		
		String testName = ui.startTest();

		// User 1 create a wiki page in the standalone wiki with User 2 as a follower
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithOneFollower(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_UpdateWikipage_privateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 1 Create a page in the private Wiki and add user 2 as a reader of the wiki</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki</B></li>
	*<li><B>Step: testUser 1 Update a page in the private Wiki</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.updated story is displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B4383D125AAF50CC85257BD6004630DD">TTT - AS - FOLLOW - WIKI PAGE - 00022 - wiki.page.updated - PRIVATE WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_updateWikipage_privateWiki(){
		
		String testName = ui.startTest();

		// User 1 create a wiki page in the standalone wiki with User 2 as a follower and User 1 will then edit the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithOneFollowerAndEditWikiPage(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String editWikiPageEvent = WikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{editWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_Like Wiki Page_privateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a page in the private Wiki and add user 2 as a reader of the wiki</B></li>		
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the wiki page </B></li>	
	*<li><B>Step: testUser 1 Recommend a page in the private Wiki</B></li>	
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / All & Wikis
	*<li><B>Verify: Verify the wiki.page.recommended story is NOt displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/63C39B0901526FCE85257BD60047C1AD">TTT -AS - FOLLOW - WIKI PAGE - 00032 - wiki.page.recommended - PRIVATE WIKI (NEG ONPREM NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_likeWikipage_privateWiki(){
		
		String testName = ui.startTest();

		// User 1 create a wiki page in the standalone wiki with User 2 as a follower and User 1 will then like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithOneFollowerAndLikeWikiPage(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String likeWikiPageEvent = WikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());	

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_CommentedWikipage_privateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>	
	*<li><B>Step: testUser 1 Create a page in the private Wiki and add user 2 as a reader of the wiki</B></li>		
	*<li><B>Step: testUser 1 Comment a page in the private Wiki</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the wikiPage</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.commented story is displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/10681A24223D821385257BD6004A9C33">TTT -AS - FOLLOW - WIKI PAGE - 00042 - wiki.page.commented - PRIVATE WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_commentedWikipage_privateWiki(){
		
		String testName = ui.startTest();

		// User 1 create a wiki page in the standalone wiki with User 2 as a follower and User 1 will then post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithOneFollowerAndAddComment(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2, comment);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_UpdateWikipagecomment_privateWikis()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>	
	*<li><B>Step: testUser 1 Create a page in the private Wiki and add user 2 as a reader of the wiki</B></li>	
	*<li><B>Step: testUser 1 Comment a page in the private Wiki</B></li>
	*<li><B>Step: testUser 1 Update the Comment</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wikipage </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.comment.updated story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87F596E31ACF046685257BD6004B2638">TTT - AS - FOLLOW - WIKI PAGE - 00052 - wiki.page.comment.updated - PRIVATE WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_updateComment_privateWiki(){
		
		String testName = ui.startTest();
		
		// User 1 create a wiki page in the standalone wiki with User 2 as a follower and User 1 will then post a comment to the wiki page and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithOneFollowerAndAddCommentAndEditComment(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2, comment, updatedComment);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String updateCommentEvent = WikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), updatedComment}, filter, true);
			
			// Verify that the update comment event and updated comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();		
	}
}