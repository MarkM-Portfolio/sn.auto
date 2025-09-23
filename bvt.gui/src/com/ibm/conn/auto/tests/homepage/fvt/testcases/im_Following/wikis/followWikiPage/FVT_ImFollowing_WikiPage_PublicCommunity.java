package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.wikis.followWikiPage;

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
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following / Wikis) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 11/2015
 */

public class FVT_ImFollowing_WikiPage_PublicCommunity extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1, wikisAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;
	private Wiki communityWiki;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		// User 1 will now create a public community with the Wiki widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve the community wiki for use in the tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_createWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: testUser 1 Add a page to the Wiki in this community</B></li>	
	*<li><B>Step: testUser 2 Log in to Home Navigate to the Wiki and follow the wiki page</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is NOT displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/672C42881F8A1DA285257BD60044665D">TTT -AS - FOLLOW - WIKI PAGE - 00013 - wiki.page.created - PUBLIC COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_CreateWikipage_PublicCommunity(){	
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 then following the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithOneFollower(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
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
	*<li><B>Name: test_WikiPage_updateWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to</B></li>	
	*<li><B>Step: testUser 1 Update a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki page</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify the wiki.page.updated is displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1957863884AC549E85257BD60046494B">TTT - AS - FOLLOW - WIKI PAGE- 00023 - wiki.page.updated - PUBLIC COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_UpdateWikipage_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 following the wiki page and User 1 will then edit the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithOneFollowerAndEditWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String updateWikiPageEvent = CommunityWikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the update event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_LikeWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki page</B></li>
	*<li><B>Step: testUser 1 Recommend a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.recommended story is Not displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CA35C53966162D1D85257BD60047D84B">TTT -AS - FOLLOW - WIKI PAGE - 00033 - wiki.page.recommended - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_LikeWikipage_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 following the wiki page and User 1 will then like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithOneFollowerAndLikeWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the like / recommend event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_CommentedWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki page</B></li>
	*<li><B>Step: testUser 1 Comment on a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.commented story is displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/888891D4493514C185257BD6004AAE5A">TTT -AS - FOLLOW - WIKI PAGE - 00043 - wiki.page.commented - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_CommentedWikipage_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 following the wiki page and User 1 will then post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithOneFollowerAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2, comment);	
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the comment event and comment content are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_WikiPage_UpdateCommentWikipage_PublicCommunityCloud()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wikipage</B></li>
	*<li><B>Step: testUser 1 Comment on a page in the Wiki in this community
	*<li><B>Step: testUser 1 Update the comment </B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify the wiki.page.comment.updated is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBF0CFB1106D050185257BD6004B3C3F">TTT -AS - FOLLOW - WIKI PAGE - 00053 - wiki.page.comment.updated - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_Updatecomment_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 following the wiki page and User 1 will post a comment to the wiki page and update the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageWithOneFollowerAndAddCommentAndEditComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, testUser2, wikisAPIUser2, comment, updatedComment);		
				
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
}