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

public class FVT_Mentions_WikiComment_MentionsView extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis };

	private APIProfilesHandler profilesAPIUser2;
	private APIWikisHandler wikisAPIUser1;
	private BaseWiki basePrivateWiki, basePrivateWikiWithMember, basePublicWiki;
	private User testUser1, testUser2;
	private Wiki privateWiki, privateWikiWithMember, publicWiki;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a private wiki with no members
		basePrivateWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly);
		privateWiki = WikiEvents.createWiki(basePrivateWiki, testUser1, wikisAPIUser1);
				
		// User 1 will now create a private wiki with User 2 added as a member
		basePrivateWikiWithMember = WikiBaseBuilder.buildBaseWikiWithOneMember(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, WikiRole.EDITOR, testUser2, profilesAPIUser2.getUUID());
		privateWikiWithMember = WikiEvents.createWiki(basePrivateWikiWithMember, testUser1, wikisAPIUser1);
				
		// User 1 will now create a public wiki
		basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(basePublicWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the wikis created during the tests
		wikisAPIUser1.deleteWiki(privateWiki);
		wikisAPIUser1.deleteWiki(privateWikiWithMember);
		wikisAPIUser1.deleteWiki(publicWiki);
	}

	/**
	* commentMention_publicWiki_mentions() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 go to a public wiki you own</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Mentions / All & Wikis</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Mentions / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B20C1B813D05B56885257C6F007EA887">TTT - @MENTIONS - 070 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE COMMENT - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_publicWiki_mentions(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(publicWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getMentionedYouInACommentOnTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all items are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that all items are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, null, true);
		
		ui.endTest();
	}
	
	/**
	* commentMention_privateWiki_mentions_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 creates private wiki and adds testUser2 as a member</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Mentions / All & Wikis</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Mentions / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BB4C080F4255C26885257C6F007EA888">TTT - @MENTIONS - 071 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE COMMENT - PRIVATE WIKI - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_privateWiki_mentions_member(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(privateWikiWithMember, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getMentionedYouInACommentOnTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWikiWithMember.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all items are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that all items are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, null, true);
		
		ui.endTest();
	}

	/**
	* commentMention_privateWiki_mentions_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 creates private wiki</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 who is not a member of the wiki on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Mentions / All & Wikis</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Mentions / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE8BB0C0B2F0528585257C6F007EA889">TTT - @MENTIONS - 072 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE COMMENT - PRIVATE WIKI - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_privateWiki_mentions_nonMember(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(privateWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String newsStory = WikiNewsStories.getMentionedYouInACommentOnTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all items are not displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that all items are not displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, null, false);
		
		ui.endTest();
	}
}