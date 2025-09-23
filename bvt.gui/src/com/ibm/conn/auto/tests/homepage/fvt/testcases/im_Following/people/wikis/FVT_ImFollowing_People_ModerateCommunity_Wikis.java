package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
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
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / People) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_People_ModerateCommunity_Wikis extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private User testUser1, testUser2, testUser3;
	private Wiki communityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
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
		
		// Set the common filters to be added to the array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS[2] = HomepageUIConstants.FilterWikis;
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 will now create a moderated community and will add the Wikis widget to the community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve the community wiki instance for use throughout the test
		communityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_WikiWidget_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the wikis widget within this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.library.created story is displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C70F8517C4BB77E2852578FC003ACB15">TTT - AS - FOLLOW - PERSON - WIKIS - 00414 - wiki.library.created - MODERATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void People_wikiWidget_ModerateCommunity(){
		
		ui.startTest();
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}		
	/**
	*<ul>
	*<li><B>Name: test_People_createWikipage_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A101FF005C70B549852578FC003AE988">TTT - AS - FOLLOW - PERSON - WIKIS - 00424 - wiki.page.created - MODERATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void createWikipage_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateWikipage_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Update a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/38C163A868B08060852578FC003AFA67">TTT - AS - FOLLOW - PERSON - WIKIS - 00434 - wiki.page.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateWikipage_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news stories to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateWikiPageEvent = CommunityWikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		for(String filter : TEST_FILTERS) {
			// Verify that the create wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that the update wiki page event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateWikiPageEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_LikeWikipage_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Recommend a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.recommended story is displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9FE5B5DA9AB9B4B4852578FC0042AFF0">TTT -  AS - FOLLOW - PERSON - WIKIS - 00454 - wiki.page.recommended - MODERATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void likeWikipage_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will like the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndLikeWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the like wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_People_CommentedWikipage_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Comment on a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.commented story is displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DB528E02754DC893852578FC0042B487">TTT -  AS - FOLLOW - PERSON - WIKIS - 00464 - wiki.page.commented - Moderate COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void commentedWikipage_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will comment on the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the comment on wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateCommentWikipage_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Update an existing comment on a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.comment.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/872D5750453BD1B0852579BF004AAE6B">TTT -  AS - FOLLOW - PERSON - WIKIS - 00469 - wiki.page.comment.updated - Moderate COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void updatecomment_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will comment on the wiki page and then edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentAndEditComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityWikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), updatedComment}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_Mention wikipage Comment_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 start a Moderate community</B></li>
	*<li><B>Step: testUser 1 create a wiki in the community</B></li>	
	*<li><B>Step: testUser 1 add a wiki page and comment adding an mentions to User 3</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify the event of the mention appears in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/36D4CEE3A1E5303485257C6F007A686A">TTT -AS - FOLLOW - PERSON - WIKIS - 00474 - Mention in a wiki comment - Moderate COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void mentionwikipagecomment_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will post a comment with mentions to User 3 to the wiki page
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(communityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the comment with mentions event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
}