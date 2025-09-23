package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following /people) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 10/2015
 */

public class FVT_ImFollowing_People_PrivateWiki extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterPeople, HomepageUIConstants.FilterWikis };
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1;
	private BaseWiki baseWiki;
	private User testUser1, testUser2, testUser3;
	private Wiki privateWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
			
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
				
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 2 will now follow User 1 using the API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a private standalone wiki with User 3 added as a member (for mentions)
		baseWiki = WikiBaseBuilder.buildBaseWikiWithOneMember(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, WikiRole.EDITOR, testUser3, profilesAPIUser3.getUUID());
		privateWiki = WikiEvents.createWiki(baseWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the wiki created during the test
		wikisAPIUser1.deleteWiki(privateWiki);
		
		// Have User 2 unfollow User 1 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: people_createPrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a new wiki with private visibility</B></li>	
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.library.created story is not displayed within the People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F473C2ECE1535931852578FC003AC896">TTT - AS - FOLLOW - PERSON - WIKIS - 00412 - wiki.library.created - PRIVATE WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 1)
	public void people_createPrivateWiki(){
		
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
	*<li><B>Name: test_People_CreateWikipage_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Wikis</B></li>
	*<li><B>Step: testUser 1 Create a new page within the  wiki with private visibility</B></li>	
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is not displayed within the People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/ADA2C1FE1BC95A04852578FC003ACEBE">TTT - AS - FOLLOW - PERSON - WIKIS - 00422 - wiki.page.created - PRIVATE WIKI</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void people_createWikipage_PrivateWiki(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPage(privateWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateWikipage_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 1 Update a page within the  wiki with private visibility</B></li>		
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1B30510D215D6508852578FC003AF21A">TTT - AS - FOLLOW - PERSON - WIKIS - 00432 - wiki.page.updated - PRIVATE WIKI (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void people_updateWikipage_PrivateWikis(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki and will then update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndEditWikiPage(privateWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateWikiPageEvent = WikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
				
		// Verify that the update wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**	
    *<ul>
	*<li><B>Name: test_People_LikeWikipage_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 1 Recommend a page within the  wiki with private visibility</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.recommended story is not displayed within the People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E569E0153D0BCDF3852578FC0042ADA4">TTT -AS - FOLLOW - PERSON - WIKIS - 00452 - wiki.page.recommended - PRIVATE WIKI</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem"}, priority = 2)
	public void people_likeWikipage_PrivateWiki(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki and will then like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndLikeWikiPage(privateWiki, baseWikiPage, testUser1, wikisAPIUser1);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String likeWikiPageEvent = WikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());	
						
		// Verify that the like / recommend wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
    }
	
	/**
	*<ul>
	*<li><B>Name: test_People_CommentedWikipage_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>		
	*<li><B>Step: testUser 1 Comment on a page within the wiki with private visibility</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.commented story is not displayed within the People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/40AE63D4114031ED852578FC0042B36F">TTT - AS - FOLLOW - PERSON - WIKIS - 00462 - wiki.page.commented - PRIVATE WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"}, priority = 2)
	public void people_commentedWikipage_PrivateWiki(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki and will then comment on the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddComment(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
						
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());	
								
		// Verify that the comment on wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, false);
		
		ui.endTest();
    }
	
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateWikipagecomment_PrivateWikis()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>	
	*<li><B>Step: testUser 1 Update an existing comment on a page within the wiki with private visibility</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.comment.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/53A0CAB3767E7D39852579BF004A591F">TTT -AS - FOLLOW - PERSON - WIKIS - 00467 - wiki.page.comment.updated - PRIVATE WIKI (NEG SC NOV)</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem"}, priority = 2)
	public void people_updateWikipageComment_PrivateWiki(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki and will then comment on the wiki page and update the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentAndEditComment(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);
								
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
								
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String updateCommentEvent = WikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
										
		// Verify that the comment and update comment events are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, comment, updatedComment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_Mention wikipage Comment_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Wikis</B></li>
	*<li><B>Step: testUser 1 create a private wiki</B></li>	
	*<li><B>Step: testUser 1 add a wiki page and comment adding an mentions to User 3</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Wikis & people</B></li>
	*<li><B>Verify: Verify the event of the mention does NOT appear in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1B73C14CC143780385257C6F007A6868">TTT - AS - FOLLOW - PERSON - WIKIS - 00472 - Mention in a wiki comment - PRIVATE WIKI</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem"}, priority = 2)
	public void people_mentionwikipagecomment_PrivateWiki(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the standalone wiki and will then post a comment with mentions to User 3 to the wiki page
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(privateWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the comment with mentions event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}