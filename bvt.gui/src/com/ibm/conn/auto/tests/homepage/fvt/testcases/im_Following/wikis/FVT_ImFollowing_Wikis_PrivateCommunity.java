package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Wikis) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 12/2015
 */

public class FVT_ImFollowing_Wikis_PrivateCommunity extends SetUpMethodsFVT {
	
private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIProfilesHandler profilesAPIUser3;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser3;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private User testUser1 , testUser2, testUser3;
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
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser3 = initialiseAPIWikisHandlerUser(testUser3);
						
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a restricted community with User 2 and User 3 added as members and followers and with the Wiki widget added
		User[] membersList = { testUser2, testUser3 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndOneFollowerAndAddWidget(baseCommunity, testUser1, communitiesAPIUser1, membersList, testUser2, communitiesAPIUser2, BaseWidget.WIKI, isOnPremise);
	
		// Retrieve the community wiki for use in all of the tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_wiki.library.created_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with private access that you have owner access to add User 2 as Member to Community</B></li>	
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>
	*<li><B>Step: testUser 1 Add the wikis widget within this community</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that the wiki.library.created story is does appear with in the All, Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0183FBED714958D4852578F8003C5219">TTT -AS - FOLLOW - WIKIS - 00015 - wiki.library.created - PRIVATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void wikis_Wikilibrarycreated_PrivateCommunity(){
		
		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, baseCommunity.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_createWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with private access that you have owner access to add User 2 as Member to Community</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the community</B></li>
	*<li><B>Step: testUser 1 Add a page to the Wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/10766598EDEB7838852578F80046338A">TTT - AS - FOLLOW - WIKIS - 00025 - wiki.page.created - PRIVATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void wikis_CreateWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the create wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_updateWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to add User 2 as Member to Community</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the community</B></li>
	*<li><B>Step: testUser 1 Update a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F03F3A76F813FF36852578F8004805E2">TTT - AS - FOLLOW - WIKIS - 00035 - wiki.page.updated - PRIVATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void wikis_UpdateWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki and will update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
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
	*<li><B>Name: test_Wikis_LikeWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to add User 2 as Member to Community</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the community</B></li>
	*<li><B>Step: testUser 1 Recommend a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.recommended story is Not displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5258F4FAD7E1E173852578F8004FAACF">TTT - AS - FOLLOW - WIKIS - 00055 - wiki.page.recommended - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void wikis_LikeWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki and will like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndLikeWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news stories to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
			
		for(String filter : TEST_FILTERS) {
			// Verify that the create wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, filter, true);
						
			// Verify that the like wiki page event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeWikiPageEvent}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_CommentedWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to  to add User 2 as Member to Community</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the community</B></li>
	*<li><B>Step: testUser 1 Comment on a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.commented story is displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4BB635ACC9034B01852578F800506267">TTT - AS - FOLLOW - WIKIS - 00065 - wiki.page.commented - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void wikis_CommentedWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki and will post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
						
		// Verify that the comment on wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_Wikis_UpdateCommentWikipage_PrivateCommunityCloud()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to to add User 2 as Member to Community</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the community </B></li>
	*<li><B>Step: testUser 1 Comment on a page in the Wiki in this community
	*<li><B>Step: testUser 1 Update the comment </B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that the wiki.page.comment.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B51B64EF65827DE3852579BB006DD39B">TTT -AS - FOLLOW - WIKIS - 00075 - wiki.page.comment.updated - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void wikis_Updatecomment_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki, will post a comment to the wiki page and then edit / update the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentAndEditComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityWikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());		
						
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), updatedComment}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_Wikis_Mention in a wikipage Comment_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 User 1 start a private community adding User 2 and User 3 as members</B></li>
	*<li><B>Step: testUser 2 follow the Community and User 3 follow the WikiPage ('@' symbol go through should user 3 follow the WikiPage)</B></li>
	*<li><B>Step: testUser 1 create a wiki in the community</B></li>	
	*<li><B>Step: testUser 1 add a wiki page and comment adding an mentions to User 3</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify the event of the mention appears in the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/99BB7E1E2C68772B85257C6F0077F116">TTT - AS - FOLLOW - WIKIS - 00165 - Mention in a wiki comment - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void wikis_MentionInawikipagecomment_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// User 3 will now follow the wiki page - critical step for the mentions event to work correctly using the API
		CommunityWikiEvents.addWikiPageFollowerSingleUser(communityWikiPage, testUser3, wikisAPIUser3);
		
		// User 1 will now post a comment with mentions to User 3 to the community wiki page
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityWikiEvents.addCommentWithMentions(communityWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the comment with mentions event and mentions comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}		
}