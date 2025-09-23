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

public class FVT_ImFollowing_People_PrivateCommunity_Wikis extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
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
		
		// User 1 will now create a restricted community with User 3 added as a member (for mentions) and will add the Wikis widget to the community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.WIKI, testUser3, isOnPremise, testUser1, communitiesAPIUser1);
	
		// Retrieve the community wiki for use in the tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_WikiWidget_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add the wikis widget within this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.library.created story is NOT displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6A7C76DC46B1ABCE852578FC003ACC3B">TTT - AS - FOLLOW - PERSON - WIKIS - 00415 - wiki.library.created - Private COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void wikiWidget_People_PrivateCommunity(){
		
		ui.startTest();
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent}, TEST_FILTERS, false);
		
		ui.endTest();
	}		
	/**
	*<ul>
	*<li><B>Name: test_People_createWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is NOT displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0E2CA235B2C7F286852578FC003AEACA">TTT - AS - FOLLOW - PERSON - WIKIS - 00425 - wiki.page.created - Private COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void createWikipage_People_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Update a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4A4FC9C0824C5503852578FC003AFBC0">TTT - AS - FOLLOW - PERSON - WIKIS - 00435 - wiki.page.updated - Private COMMUNITY (NEG SC NOV)</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void updateWikipage_People_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String updateWikiPageEvent = CommunityWikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the update wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_People_LikeWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Recommend a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.recommended story is NOT displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7E455D49E73C4241852578FC0042B115">TTT -  AS - FOLLOW - PERSON - WIKIS - 00455 - wiki.page.recommended - Private COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void likeWikipage_People_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will like the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndLikeWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the like wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_People_CommentedWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Comment on a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.commented story is Not displayed within the Communities, People and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/767A69BAA9A02A58852578FC0042B5B4">TTT -  AS - FOLLOW - PERSON - WIKIS - 00465 - wiki.page.commented - Private COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void commentedWikipage_People_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will comment on the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the comment on wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_People_UpdateCommentWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Update an existing comment on a page within the wiki in this community</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify that the wiki.page.comment.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/83A0201D6D35C4BE852579BF004AD89C">TTT -  AS - FOLLOW - PERSON - WIKIS - 00470 - wiki.page.comment.updated - Private COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void updatecomment_People_PrivateCommunity(){
		
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
				
		// Verify that the comment on wiki page event, the update comment, the original comment and the updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, comment, updatedComment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_Mention wikipage Comment_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 start a Private community</B></li>
	*<li><B>Step: testUser 1 create a wiki in the community</B></li>	
	*<li><B>Step: testUser 1 add a wiki page and comment adding an mentions to User 3</B></li>
	*<li><B>Step: testUser 2 who is following User 1</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Wikis & people</B></li>
	*<li><B>Verify: Verify the event of the mention NOT appears in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AA40A76F4158B0CD85257C6F007A686B">TTT -AS - FOLLOW - PERSON - WIKIS - 00475 - Mention in a wiki comment - Private COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void mentionwikipagecomment_People_PrivateCommunity(){
		
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
		
		// Verify that the comment with mentions event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
}