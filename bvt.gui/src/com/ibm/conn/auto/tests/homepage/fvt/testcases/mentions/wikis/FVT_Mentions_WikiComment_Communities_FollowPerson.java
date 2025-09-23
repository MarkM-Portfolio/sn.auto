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
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
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

public class FVT_Mentions_WikiComment_Communities_FollowPerson extends SetUpMethodsFVT {
	
	private String TEST_FILTERS[];

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2, testUser3;
	private Wiki moderatedCommunityWiki, publicCommunityWiki, restrictedCommunityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		
		// Add the testable filters to the filters array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS[2] = HomepageUIConstants.FilterWikis;
		
		// Have User 2 follow User 1 before any tests are executed
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a public community with wiki widget added (ie. including a wiki)
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community with wiki widget added (ie. including a wiki)
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community with User 3 added as a member and the wiki widget added (ie. including a wiki)
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.WIKI, testUser3, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve all of the relevant community wikis for use in the tests
		publicCommunityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		moderatedCommunityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
		restrictedCommunityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		
		// Have User 2 unfollow User 1 now that all tests have completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}

	/**
	* commentMention_publicCommunityWiki_imFollowing_followPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a public community</B></li>
	*<li><B>Step: testUser1 add a wiki to the public community</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities, People & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6EC7B82BC8DCA00E85257C6F007A6869">TTT - AS - FOLLOW - PERSON - WIKIS - 00473 - MENTION IN A WIKI COMMENT - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_publicCommunityWiki_imFollowing_followPerson(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community and add a comment with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(publicCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify the news stories appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* commentMention_modCommunityWiki_imFollowing_followPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a moderated community</B></li>
	*<li><B>Step: testUser1 add a wiki to the moderated community</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities, People & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/36D4CEE3A1E5303485257C6F007A686A">TTT - AS - FOLLOW - PERSON - WIKIS - 00474 - MENTION IN A WIKI COMMENT - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_modCommunityWiki_imFollowing_followPerson(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community and add a comment with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(moderatedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String newsStory = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify the news stories appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* commentMention_privateCommunityWiki_imFollowing_followPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser1 add a wiki to the private community</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities, People & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AA40A76F4158B0CD85257C6F007A686B">TTT - AS - FOLLOW - PERSON - WIKIS - 00475 - MENTION IN A WIKI COMMENT - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_privateCommunityWiki_imFollowing_followPerson(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community and add a comment with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(restrictedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
				
		// Create the news story to be verified
		String newsStory  = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify the news stories do NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}