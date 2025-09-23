package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
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
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Mentions_WikiComment_FollowWiki extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis };

	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseWiki basePrivateWiki, basePublicWiki;
	private User testUser1, testUser2, testUser3;
	private Wiki privateWiki, publicWiki;
	
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
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a private wiki with Users 2 and 3 added as members - following the wiki and mentions won't work correctly unless these users are members
		User listOfMembers[] = { testUser2, testUser3 };
		String listOfMemberUUID[] = { profilesAPIUser2.getUUID(), profilesAPIUser3.getUUID() };
		WikiRole listOfWikiRoles[] = { WikiRole.EDITOR, WikiRole.READER };
		basePrivateWiki = WikiBaseBuilder.buildBaseWikiWithMultipleMembers(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, listOfWikiRoles, listOfMembers, listOfMemberUUID);
		privateWiki = WikiEvents.createWikiWithOneFollower(basePrivateWiki, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		// User 1 will now create a public wiki with User 2 as a follower of that wiki
		basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWikiWithOneFollower(basePublicWiki, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have User 1 delete all created wikis during the tests
		wikisAPIUser1.deleteWiki(privateWiki);
		wikisAPIUser1.deleteWiki(publicWiki);
	}

	/**
	* commentMention_publicWiki_imFollowingWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 create a public wiki</B></li>
	*<li><B>Step: testUser2 follow testUser1's public wiki</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / I'm Following / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A4610BC7FB14516085257C6F0077F112">TTT - AS - FOLLOW - WIKIS - 00161 - MENTION IN A WIKI COMMENT - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_publicWiki_imFollowingWiki() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 3
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(publicWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicWiki.getName(), testUser1.getDisplayName());
		String mentionsString = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all stories are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsString}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* commentMention_privateWiki_imFollowingWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 create a private wiki and add User 2 as a Editor Role and User 3 as a Reader Role of the Wiki</B></li>
	*<li><B>Step: testUser2 follow testUser1's private wiki</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / I'm Following / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CEA827650D812AAD85257C6F0077F113">TTT - AS - FOLLOW - WIKIS - 00162 - MENTION IN A WIKI COMMENT - PRIVATE WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_privateWiki_imFollowingWiki() {

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 3
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(privateWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWiki.getName(), testUser1.getDisplayName());
		String mentionsString = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all stories are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsString}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}