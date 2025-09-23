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
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_WikiComment_OnPrem extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis };

	private APIProfilesHandler profilesAPIUser2;
	private APIWikisHandler wikisAPIUser1;
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
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a private wiki - User 2 will be added as a member so as the mentions will work correctly
		basePrivateWiki = WikiBaseBuilder.buildBaseWikiWithOneMember(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, WikiRole.EDITOR, testUser2, profilesAPIUser2.getUUID());
		privateWiki = WikiEvents.createWiki(basePrivateWiki, testUser1, wikisAPIUser1);
				
		// User 1 will now create a public wiki
		basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(basePublicWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the wikis created during the tests
		wikisAPIUser1.deleteWiki(privateWiki);
		wikisAPIUser1.deleteWiki(publicWiki);
	}

	/**
	* commentMention_publicWiki_discover() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 go to a public wiki you own</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the SAME organisation log into Homepage / Updates / Discover / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 can see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A49E828D3D99F3DC85257C6F007C21E5">TTT - DISCOVER - WIKIS - 00153 - MENTIONS IN WIKI COMMENT - PUBLIC WIKI - ON PREM ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_publicWiki_discover(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(publicWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 3 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all items are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* commentMention_privateWiki_discover() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 go to a private wiki you own</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the SAME organisation log into Homepage / Updates / Discover / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3876F39248BF504C85257C6F007C23D9">TTT - DISCOVER - WIKIS - 00154 - MENTIONS IN WIKI COMMENT - PRIVATE WIKI - ON PREM ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_privateWiki_discover(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(privateWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 3 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all items are not displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}