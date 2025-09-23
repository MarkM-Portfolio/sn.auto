package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.wikis;

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

public class FVT_Mentions_StandaloneWiki_MentionsView extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis };
	
	private APIProfilesHandler profilesAPIUser2;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
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
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		// User 1 create a public wiki
		basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(basePublicWiki, testUser1, wikisAPIUser1);
		
		// User 1 create a private wiki with User 2 added as a member
		basePrivateWikiWithMember = WikiBaseBuilder.buildBaseWikiWithOneMember(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, WikiRole.EDITOR, testUser2, profilesAPIUser2.getUUID());
		privateWikiWithMember = WikiEvents.createWiki(basePrivateWikiWithMember, testUser1, wikisAPIUser1);
		
		// User 1 create a private wiki with no members
		basePrivateWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly);
		privateWiki = WikiEvents.createWiki(basePrivateWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the wikis created during the test
		wikisAPIUser1.deleteWiki(publicWiki);
		wikisAPIUser1.deleteWiki(privateWikiWithMember);
		wikisAPIUser1.deleteWiki(privateWiki);
	}
	
	/**
	* mention_publicWiki_wikiPage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 go to a public wiki you own</B></li>
	*<li><B>Step: testUser1 add a wiki page with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / I'm Following / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0E86AF3FE8A3446B85257C93005045A2">TTT - @MENTIONS - 070 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void mention_publicWiki_wikiPage() {
		
		String testName = ui.startTest();
		
		// User 1 add a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithMentionsToOneWikiFollower(publicWiki, baseWikiPage, mentions, wikisAPIUser2, testUser1, wikisAPIUser1);
		
		// Log in as User 2
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = WikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicWiki.getName(), testUser1.getDisplayName());
		String contentWithMentions = baseWikiPage.getDescription().trim() + ". " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, contentWithMentions}, TEST_FILTERS, true);
		
		// Navigate to the mentions view
		ui.gotoMentions();
		
		// Perform all verifications
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, contentWithMentions}, null, true);
		
		ui.endTest();
	}
	
	/**
	* mention_privateWiki_wikiPage_withMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 creates private wiki and adds testUser2 as a member</B></li>
	*<li><B>Step: testUser1 add a wiki page with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DC09B1A3BE298DCA85257C93005045A3">TTT - @MENTIONS - 071 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - PRIVATE WIKI - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void mention_privateWiki_wikiPage_withMember() {
		
		String testName = ui.startTest();
		
		// User 1 add a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithMentionsToOneWikiFollower(privateWikiWithMember, baseWikiPage, mentions, wikisAPIUser2, testUser1, wikisAPIUser1);
		
		// Log in as User 2
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = WikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWikiWithMember.getName(), testUser1.getDisplayName());
		String contentWithMentions = baseWikiPage.getDescription().trim() + ". " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, contentWithMentions}, TEST_FILTERS, true);
		
		// Navigate to the mentions view
		ui.gotoMentions();
		
		// Perform all verifications
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, contentWithMentions}, null, true);
		
		ui.endTest();
	}

	/**
	* mention_privateWiki_wikiPage_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 creates private wiki and adds testUser2 as a member</B></li>
	*<li><B>Step: testUser1 add a wiki page with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 who log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/375860080B1D8BC685257C93005045A4">TTT - @MENTIONS - 072 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - PRIVATE WIKI - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void mention_privateWiki_wikiPage_nonMember() {
		
		String testName = ui.startTest();
		
		// User 1 add a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageWithMentions(privateWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = WikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWiki.getName(), testUser1.getDisplayName());
		String contentWithMentions = baseWikiPage.getDescription().trim() + ". " + mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, contentWithMentions}, TEST_FILTERS, false);
		
		// Navigate to the mentions view
		ui.gotoMentions();
		
		// Perform all verifications
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, contentWithMentions}, null, false);
		
		ui.endTest();
	}
}