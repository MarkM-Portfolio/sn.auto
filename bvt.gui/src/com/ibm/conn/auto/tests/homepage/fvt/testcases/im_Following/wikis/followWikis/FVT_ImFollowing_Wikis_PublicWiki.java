package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.wikis.followWikis;

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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following /wikis) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_Wikis_PublicWiki extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = {HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis};
	
	private APIProfilesHandler profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseWiki baseWiki;
	private User testUser1 , testUser2, testUser3;
	private Wiki publicWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);	
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// Create a wiki with User 2 as a follower
		baseWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWikiWithOneFollower(baseWiki, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the wiki created during the test
		wikisAPIUser1.deleteWiki(publicWiki);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_Wiki library Created_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a new wiki with public visibility</B></li>		
	*<li><B>Step: testUser 2 log into Homepage (Log in to Home as a different user)</B></li>
	*<li><B>Step: testUser 2 Follow the  Wiki</B></li>
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / Wikis
	*<li><B>Verify: Verify that the news story for wiki.library.created is NOT seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BB8063FCA7F1C1CE852578F8003C4D19">TTT -AS - FOLLOW - WIKIS - 00011 - wiki.library.created - PUBLIC WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 1)
	public void Wikis_createwikilibrary_PublicWiki(){
		
		ui.startTest();

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiEvent = WikiNewsStories.getCreateWikiNewsStory(ui, baseWiki.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, baseWiki.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_create Wiki Page_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a page in the public Wiki</B></li>		
	*<li><B>Step: testUser 2 log into Homepage who is following the wiki </B></li>	
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / All & Wikis
	*<li><B>Verify: Verify the wiki.page.created story is displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CFA5927EBBAC7290852578F8002B4002">TTT - AS - FOLLOW - WIKIS - 00021 - wiki.page.created - PUBLIC WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void Wikis_createWikipage_PublicWiki(){
		
		String testName = ui.startTest();

		// User 1 will now create a wiki page in the standalone wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the wiki page creation event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_UpdateWikipage_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 1 Update a page in the public Wiki</B></li>		
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.updated story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8C68299F51CDC474852578F800480023">TTT - AS - FOLLOW - WIKIS - 00031 - wiki.page.updated - PUBLIC WIKI (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void Wikis_updateWikipage_PublicWiki(){
		
		String testName = ui.startTest();

		// User 1 will now create a wiki page in the standalone wiki and will update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndEditWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String updateWikiPageEvent = WikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that the update wiki page event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateWikiPageEvent}, null, false);
		}
		ui.endTest();
  	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_Like Wiki Page_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Recommend a page in the public Wiki</B></li>		
	*<li><B>Step: testUser 2 log into Homepage </B></li>
	*<li><B>Step: testUser 2 Follow the  Wiki</B></li>
	*<li><B>Step: testUser 2 log into Homepage/ Updates / I'm Following / All & Wikis
	*<li><B>Verify: Verify the wiki.page.recommended story is NOT displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0BFCF4A594D2CBA2852578F8004FA5A3">TTT - AS - FOLLOW - WIKIS - 00051 - wiki.page.recommended - PUBLIC WIKI (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void Wikis_likeWikipage_PublicWiki(){
		
		String testName = ui.startTest();

		// User 1 will now create a wiki page in the standalone wiki and will like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndLikeWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String likeWikiPageEvent = WikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());	

		for(String filter : TEST_FILTERS) {
			// Verify that the create wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that the like / recommend wiki page event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeWikiPageEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_CommentedWikipage_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>		
	*<li><B>Step: testUser 1 Comment a page in the public Wiki</B></li>
	*<li><B>Step: testUser 2 Log in to Home as User 2 who is following the wiki</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.commented story is displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C600DAF84C959250852578F800505DCB ">TTT - AS - FOLLOW - WIKIS - 00061 - wiki.page.commented - PUBLIC WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void Wikis_commentedWikipage_PublicWiki(){
		
		String testName = ui.startTest();

		// User 1 will now create a wiki page in the standalone wiki and will then post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddComment(publicWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the wiki page commented event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_UpdateWikipagecomment_PublicWikis()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>	
	*<li><B>Step: testUser 1 Comment a page in the public Wiki</B></li>
	*<li><B>Step: testUser 1 Update the Comment</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.comment.updated story is Not displayed in the Wiki Filter</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/31EE74FD500AF386852579BB006CEE69">TTT -AS - FOLLOW - WIKIS - 00071 - wiki.page.comment.updated - PUBLIC WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void Wikis_updateWikipageComment_PublicWiki(){
		
		String testName = ui.startTest();

		// User 1 will now create a wiki page in the standalone wiki and will then post a comment to the wiki page and update the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentAndEditComment(publicWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String updateCommentEvent = WikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), updatedComment}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_Mention wikipage Comment_PublicWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 1 create a public wiki</B></li>	
	*<li><B>Step: testUser 2 who is following Wiki</B></li>
	*<li><B>Step: testUser 1 add a wiki page and comment adding an mentions to User 3</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All & Wikis </B></li>
	*<li><B>Verify: Verify the event of the mention appears in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A4610BC7FB14516085257C6F0077F112">TTT - AS - FOLLOW - WIKIS - 00161 - Mention in a wiki comment - PUBLIC WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void Wikis_mentionwikipagecomment_PublicWiki(){
		
		String testName = ui.startTest();

		// User 1 will now create a wiki page in the standalone wiki and will then post a comment with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(publicWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify that the wiki page commented event is seen in all filters
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
}