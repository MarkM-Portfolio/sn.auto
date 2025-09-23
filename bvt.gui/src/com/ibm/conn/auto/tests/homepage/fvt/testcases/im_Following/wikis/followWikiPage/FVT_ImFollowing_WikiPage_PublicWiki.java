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
 * This is a functional test for the Homepage Activity Stream (I'm Following /Wikis) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_WikiPage_PublicWiki extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis };
	
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseWiki baseWiki;
	private BaseWikiPage baseWikiPage1, baseWikiPage2, baseWikiPage3, baseWikiPage4, baseWikiPage5;
	private User testUser1, testUser2;
	private Wiki publicWiki;
	private WikiPage wikiPage2, wikiPage3, wikiPage4, wikiPage5;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
			
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		// User 1 will now create a public standalone wiki
		baseWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(baseWiki, testUser1, wikisAPIUser1);
		
		/**
		 * User 1 will now create 5 wiki pages in the public wiki for use in all of the tests - each one with User 2 added as a follower.
		 * 
		 * It is necessary to create all wiki pages here sequentially in order to prevent 409 CONFLICT errors being thrown when these tests
		 * are being run across multiple VM's (these errors occur when multiple "follow wiki page" requests are made at the same time as each other).
		 */
		baseWikiPage1 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		WikiEvents.createWikiPageWithOneFollower(publicWiki, baseWikiPage1, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		baseWikiPage2 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		wikiPage2 =	WikiEvents.createWikiPageWithOneFollower(publicWiki, baseWikiPage2, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		baseWikiPage3 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		wikiPage3 =	WikiEvents.createWikiPageWithOneFollower(publicWiki, baseWikiPage3, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		baseWikiPage4 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		wikiPage4 =	WikiEvents.createWikiPageWithOneFollower(publicWiki, baseWikiPage4, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		baseWikiPage5 = WikiBaseBuilder.buildBaseWikiPage(getClass().getSimpleName() + Helper.genStrongRand());
		wikiPage5 =	WikiEvents.createWikiPageWithOneFollower(publicWiki, baseWikiPage5, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the wiki created during the test
		wikisAPIUser1.deleteWiki(publicWiki);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_CreateWikipage_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a page in the public Wiki</B></li>		
	*<li><B>Step: testUser 2 log into Homepage </B></li>
	*<li><B>Step: testUser 2 Navigate to the public Wiki and follow the Wiki page </B></li>
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / Wikis
	*<li><B>Verify: Verify the wiki.page.created story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D887F0DC4DF45D4285257BD60043BB4C">TTT -AS - FOLLOW - WIKI PAGE - 00011 - wiki.page.created - PUBLIC WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_createWikipage_PublicWiki(){
		
		ui.startTest();
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage1.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage1.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_UpdateWikipage_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki</B></li>
	*<li><B>Step: testUser 1 Update a page in the public Wiki</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.updated story is displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CB8CDB163C3AD31385257BD600460F71">TTT - AS - FOLLOW - WIKI PAGE - 00021 - wiki.page.updated - PUBLIC WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_updateWikipage_PublicWiki(){
		
		ui.startTest();

		// User 1 will now edit the wiki page
		WikiEvents.editWikiPage(wikiPage2, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String editWikiPageEvent = WikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage2.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{editWikiPageEvent, baseWikiPage2.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_Like Wiki Page_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Recommend a page in the public Wiki</B></li>		
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the wiki page </B></li>	
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / All & Wikis
	*<li><B>Verify: Verify the wiki.page.recommended story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C8103B6851B0537885257BD60047A6F8">TTT - AS - FOLLOW - WIKI PAGE - 00031 - wiki.page.recommended - PUBLIC WIKI (NEG ONPREM NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_likeWikipage_PublicWiki(){
			
		ui.startTest();

		// User 1 will now like / recommend the wiki page
		WikiEvents.likeWikiPage(wikiPage3, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified		
		String likeWikiPageEvent = WikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage3.getName(), baseWiki.getName(), testUser1.getDisplayName());	

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage3.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_CommentedWikipage_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>		
	*<li><B>Step: testUser 1 Comment a page in the public Wiki</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the wikiPage</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.commented story is displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CAE30F0AB1D67AEC85257BD6004A8A89">TTT - AS - FOLLOW - WIKI PAGE - 00041 - wiki.page.commented - PUBLIC WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_commentedWikipage_PublicWiki(){
			
		ui.startTest();

		// User 1 will now post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		WikiEvents.addCommentToWikiPage(wikiPage4, testUser1, wikisAPIUser1, comment);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage4.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create activity event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage4.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_UpdateWikipagecomment_PublicWikis()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>	
	*<li><B>Step: testUser 1 Comment a page in the public Wiki</B></li>
	*<li><B>Step: testUser 1 Update the Comment</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wikipage </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.comment.updated story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C2C39E028F4E07DA85257BD6004B0210">TTT - AS - FOLLOW - WIKI PAGE - 00051 - wiki.page.comment.updated - PUBLIC WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void WikiPage_updateComment_PublicWiki(){
		
		ui.startTest();

		// User 1 will now post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment wikiComment = WikiEvents.addCommentToWikiPage(wikiPage5, testUser1, wikisAPIUser1, comment);
		
		// User 1 will now edit the comment on the wiki page
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		WikiEvents.editCommentOnWikiPage(wikiComment, testUser1, wikisAPIUser1, updatedComment);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage5.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String updateCommentEvent = WikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage5.getName(), baseWiki.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage5.getDescription(), updatedComment}, filter, true);
			
			// Verify that the update comment event and updated comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();	
	}
}