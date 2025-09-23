package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
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

public class FVT_Mentions_WikiComment_Communities_SameOrg extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser4, profilesAPIUser5;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
	private Wiki moderatedCommunityWiki, publicCommunityWiki, restrictedCommunityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		// User 1 will now create a moderated community with wiki widget added (ie. including a wiki)
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community with wiki widget added (ie. including a wiki)
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a restricted community with User 5 (acting as User 2) added as a member and with wiki widget added (ie. including a wiki)
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.WIKI, testUser5, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve all of the community wikis for use in the tests
		moderatedCommunityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
		publicCommunityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		restrictedCommunityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* commentMention_publicCommunityWiki_discover_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a public community</B></li>
	*<li><B>Step: testUser1 add a wiki to the public community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the SAME organisation log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 can see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4423F93EEFEAF05285257C6F007B55FA">TTT - DISCOVER - WIKIS - 00150 - MENTIONS IN WIKI COMMENT - PUBLIC COMMUNITY - SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_publicCommunityWiki_discover_sameOrg() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community and add a comment with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(publicCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 3 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String newsStory = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicCommunity.getName(), testUser1.getDisplayName());
		String mentionsString = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsString}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* commentMention_modCommunityWiki_discover_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a moderated community</B></li>
	*<li><B>Step: testUser1 add a wiki to the moderated community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the SAME organisation log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 can see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/979DF9F0AFF97D9485257C6F007B55F9">TTT - DISCOVER - WIKIS - 00151 - MENTIONS IN WIKI COMMENT - MODERATE COMMUNITY - SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_modCommunityWiki_discover_sameOrg() {
		
		/**
		 * In order to prevent 409: CONFLICT errors when this test is run on the grid - this test case will use User 4 (as User 2)
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community and add a comment with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser4, profilesAPIUser4, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(moderatedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 3 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String newsStory = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String mentionsString = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsString}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* commentMention_privateCommunityWiki_discover_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community</B></li>
	*<li><B>Step: testUser1 add a wiki to the private community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the SAME organisation log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C12EA27ABA7593A485257C6F007B55F8">TTT - DISCOVER - WIKIS - 00152 - MENTIONS IN WIKI COMMENT - PRIVATE COMMUNITY - SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_privateCommunityWiki_discover_sameOrg() {
		
		/**
		 * In order to prevent 409: CONFLICT errors when this test is run on the grid - this test case will use User 5 (as User 2)
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community and add a comment with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser5, profilesAPIUser5, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(restrictedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 3 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String newsStory = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String mentionsString = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsString}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}