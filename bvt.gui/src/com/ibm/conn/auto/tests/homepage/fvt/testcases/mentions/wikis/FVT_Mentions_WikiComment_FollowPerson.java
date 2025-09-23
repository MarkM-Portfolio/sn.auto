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

public class FVT_Mentions_WikiComment_FollowPerson extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis, HomepageUIConstants.FilterPeople };

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
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
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);

		// Have User 2 follow User 1 before any tests are executes
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 create a private wiki including User 3 as a member so as they can be mentioned
		basePrivateWiki = WikiBaseBuilder.buildBaseWikiWithOneMember(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, WikiRole.EDITOR, testUser3, profilesAPIUser3.getUUID());
		privateWiki = WikiEvents.createWiki(basePrivateWiki, testUser1, wikisAPIUser1);
		
		// User 1 create a public wiki
		basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(basePublicWiki, testUser1, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have User 1 delete all created wikis during the tests
		wikisAPIUser1.deleteWiki(privateWiki);
		wikisAPIUser1.deleteWiki(publicWiki);
		
		// Have User 2 unfollow User 1 now that all tests have completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}

	/**
	* commentMention_publicWiki_imFollowingPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 create a public wiki</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / I'm Following / All, People & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A698AEF4BC5886EC85257C6F007A6867">TTT - AS - FOLLOW - PERSON - WIKIS - 00471 - MENTION IN A WIKI COMMENT - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_publicWiki_imFollowingPerson(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 3
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(publicWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all elements are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* commentMention_privateWiki_imFollowingPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Wikis</B></li>
	*<li><B>Step: testUser1 create a private wiki</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / I'm Following / All, People & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1B73C14CC143780385257C6F007A6868">TTT - AS - FOLLOW - PERSON - WIKIS - 00472 - MENTION IN A WIKI COMMENT - PRIVATE WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void commentMention_privateWiki_imFollowingPerson(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page and comment on the page, mentioning User 3
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentWithMentions(privateWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePrivateWiki.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that all elements are not displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}