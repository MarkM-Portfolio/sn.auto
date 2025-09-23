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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_WikiComment_Communities_FollowCommunity extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity;
	private Community moderatedCommunity, publicCommunity;
	private User testUser1, testUser2, testUser3;
	private Wiki moderatedCommunityWiki, publicCommunityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a public community with wiki widget added (ie. including a wiki)
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(basePublicCommunity, testUser2, communitiesAPIUser2, BaseWidget.WIKI, testUser1, communitiesAPIUser1, isOnPremise);
				
		// User 1 will now create a moderated community with wiki widget added (ie. including a wiki)
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseModeratedCommunity, testUser2, communitiesAPIUser2, BaseWidget.WIKI, testUser1, communitiesAPIUser1, isOnPremise);
		
		// Retrieve all of the relevant community wikis for use in the tests
		moderatedCommunityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
		publicCommunityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the communities now that the test has completed
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* commentMention_publicCommunityWiki_imFollowing_followComm() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create to a public community</B></li>
	*<li><B>Step: testUser1 add a wiki to the public community</B></li>
	*<li><B>Step: testUser2 follow the public community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DAEB0E3D832BCE0885257C6F0077F114">TTT - AS - FOLLOW - WIKIS - 00163 - MENTION IN A WIKI COMMENT - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_publicCommunityWiki_imFollowing_followComm(){

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
		
		// Verify that the news stories appear in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* commentMention_modCommunityWiki_imFollowing_followComm() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 create to a moderated community</B></li>
	*<li><B>Step: testUser1 add a wiki to the moderated community</B></li>
	*<li><B>Step: testUser2 follow the moderated community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser3 on a wiki page</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/784977C330FBD9AC85257C6F0077F115">TTT - AS - FOLLOW - WIKIS - 00164 - MENTION IN A WIKI COMMENT - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void commentMention_modCommunityWiki_imFollowing_followComm(){
		
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
		
		// Perform all verifications
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}