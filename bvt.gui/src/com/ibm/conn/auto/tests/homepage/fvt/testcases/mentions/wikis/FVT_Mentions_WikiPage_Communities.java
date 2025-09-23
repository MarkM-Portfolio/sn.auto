package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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

public class FVT_Mentions_WikiPage_Communities extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2, wikisAPIUser3, wikisAPIUser4;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity, baseRestrictedCommunityWithMember;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity, restrictedCommunityWithMember;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
	private Wiki moderatedCommunityWiki, publicCommunityWiki, restrictedCommunityWiki, restrictedCommunityWikiWithMember;
	
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
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
		wikisAPIUser4 = initialiseAPIWikisHandlerUser(testUser4);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		
		// User 1 will now create a moderated community with wiki widget added (ie. including a wiki)
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a public community with wiki widget added (ie. including a wiki)
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community with no members and with the wiki widget added (ie. including a wiki)
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseRestrictedCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a restricted community with wiki widget added (ie. including a wiki) and User 4 (acting as User 2) added as a member
		baseRestrictedCommunityWithMember = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunityWithMember = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunityWithMember, BaseWidget.WIKI, testUser4, isOnPremise, testUser1, communitiesAPIUser1);
				
		// Retrieve all of the community wikis for use in the tests
		publicCommunityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		moderatedCommunityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
		restrictedCommunityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
		restrictedCommunityWikiWithMember = CommunityWikiEvents.getCommunityWiki(restrictedCommunityWithMember, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have the relevant users remove all communities used in all tests
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunityWithMember);
	}
 	
	/**
	* mention_publicCommunityWikiPage_mentionsView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a public community</B></li>
	*<li><B>Step: testUser1 add a wiki to the public community</B></li>
	*<li><B>Step: testUser1 add a wiki page with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DF790F437A84510F85257C93005045A5">TTT - @MENTIONS - 073 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - PUBLIC COMMUNITY WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_usingPublicCommunityWikiPage_mentionsView() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithMentionsToOneWikiFollower(publicCommunityWiki, baseWikiPage, mentions, wikisAPIUser2, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String newsStory = CommunityWikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicCommunity.getName(), testUser1.getDisplayName());
		String descriptionWithMentions = "" + baseWikiPage.getDescription() + ". " + mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the items are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, descriptionWithMentions}, TEST_FILTERS, true);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that the items are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, descriptionWithMentions}, null, true);
		
		ui.endTest();
	}

	/**
	* mention_usingModCommunityWikiPage_mentionsView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a moderated community</B></li>
	*<li><B>Step: testUser1 add a wiki to the moderated community</B></li>
	*<li><B>Step: testUser1 add a wiki page with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/420E05772AAEEE8F85257C93005045A6">TTT - @MENTIONS - 074 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - MODERATE COMMUNITY WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_usingModCommunityWikiPage_mentionsView() {
		
		/**
		 * In order to prevent 409: CONFLICT errors when these tests are run on the grid, this test case will use User 3 (as User 2)
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithMentionsToOneWikiFollower(moderatedCommunityWiki, baseWikiPage, mentions, wikisAPIUser3, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories to be verified
		String newsStory = CommunityWikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String descriptionWithMentions = "" + baseWikiPage.getDescription() + ". " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the items are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, descriptionWithMentions}, TEST_FILTERS, true);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that the items are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, descriptionWithMentions}, null, true);
		
		ui.endTest();
	}

	/**
	* mention_PrivateCommunityWikiPage_mentionsView_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a private community and add testUser2 as a member</B></li>
	*<li><B>Step: testUser1 add a wiki to the private community</B></li>
	*<li><B>Step: testUser1 add a wiki page with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/489E139F9AE72E9285257C93005045A7">TTT - @MENTIONS - 075 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - PRIVATE COMMUNITY WIKI - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunityWikiPage_mentionsView_withMember() {
		
		/**
		 * In order to prevent 409: CONFLICT errors when these tests are run on the grid, this test case will use User 4 (as User 2)
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser4, profilesAPIUser4, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithMentionsToOneWikiFollower(restrictedCommunityWikiWithMember, baseWikiPage, mentions, wikisAPIUser4, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser4, false);
		
		// Create the news stories to be verified
		String newsStory = CommunityWikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), baseRestrictedCommunityWithMember.getName(), testUser1.getDisplayName());
		String descriptionWithMentions = "" + baseWikiPage.getDescription() + ". " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the items are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, descriptionWithMentions}, TEST_FILTERS, true);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that the items are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, descriptionWithMentions}, null, true);
		
		ui.endTest();
	}
	
	/**
	* mention_privateCommunityWikiPage_mentionsView_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a private community</B></li>
	*<li><B>Step: testUser1 add a wiki to the private community</B></li>
	*<li><B>Step: testUser1 add a wiki page mentioning testUser2 who is not a member of the community</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/38BCFE10B534584C85257C93005045A8">TTT - @MENTIONS - 076 - MENTIONS DIRECTED TO YOU IN A WIKI PAGE - PRIVATE COMMUNITY WIKI - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunityWikiPage_mentionsView_nonMember() {
		
		/**
		 * In order to prevent 409: CONFLICT errors when these tests are run on the grid, this test case will use User 5 (as User 2)
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page mentioning User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser5, profilesAPIUser5, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithMentions(restrictedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser5, false);
		
		// Create the news stories to be verified
		String newsStory = CommunityWikiNewsStories.getMentionedYouInTheWikiPageNewsStory(ui, baseWikiPage.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String descriptionWithMentions = "" + baseWikiPage.getDescription() + ". " + mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the items are not displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, descriptionWithMentions}, TEST_FILTERS, false);
		
		// Navigate to the Mentions view
		ui.gotoMentions();
		
		// Verify that the items are not displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, descriptionWithMentions}, null, false);
		
		ui.endTest();
	}
}