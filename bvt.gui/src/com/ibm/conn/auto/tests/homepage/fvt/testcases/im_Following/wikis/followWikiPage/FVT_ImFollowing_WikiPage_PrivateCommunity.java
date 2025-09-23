package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.wikis.followWikiPage;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

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

public class FVT_ImFollowing_WikiPage_PrivateCommunity extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity;
	private User testUser1, testUser2;
	private Wiki communityWiki;
	private WikisUI uiWiki;
			
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		uiWiki = WikisUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		// User 1 will now create a restricted community with User 2 added as a member and with the Wiki widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.WIKI, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve the community wiki for use in the tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {	
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		uiWiki = WikisUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_createWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Add a page to the Wiki in this community</B></li>	
	*<li><B>Step: testUser 2 Log in to Home Navigate to the Wiki and follow the wiki page</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is NOT displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DE2B94DA0546CEAE85257BD600418DAC">TTT -AS - FOLLOW - WIKI PAGE - 00015 - wiki.page.created - PRIVATE COMMUNITY</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_CreateWikipage_PrivateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 then following the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		/**
		 * Log in as User 2 and follow the wiki page using the UI - this is a critical step since using the API to follow the wiki pages in restricted communities
		 * intermittently returns 403: FORBIDDEN errors (even when User 2 has logged into the community beforehand).
		 */
		CommunityWikiEvents.loginAndNavigateToCommunityAndFollowCommunityWikiPage(restrictedCommunity, baseCommunity, communityWikiPage, ui, uiWiki, uiCo, testUser2, communitiesAPIUser2, false);
		
		// Navigate to Home and then to the I'm Following view
		UIEvents.gotoHomeAndGotoImFollowing(ui);
		
		// Create the news story to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_WikiPage_LikeWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki page</B></li>
	*<li><B>Step: testUser 1 Recommend a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.recommended story is Not displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7AB903E353D011B685257BD6004A6D72">TTT -AS - FOLLOW - WIKI PAGE - 00035 - wiki.page.recommended - PRIVATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_LikeWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 then following the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		/**
		 * Log in as User 2 and follow the wiki page using the UI - this is a critical step since using the API to follow the wiki pages in restricted communities
		 * intermittently returns 403: FORBIDDEN errors (even when User 2 has logged into the community beforehand).
		 */
		CommunityWikiEvents.loginAndNavigateToCommunityAndFollowCommunityWikiPage(restrictedCommunity, baseCommunity, communityWikiPage, ui, uiWiki, uiCo, testUser2, communitiesAPIUser2, false);
		
		// User 1 will now like / recommend the wiki page
		CommunityWikiEvents.likeWikiPage(communityWikiPage, testUser1, wikisAPIUser1);
		
		// Navigate to Home and then to the I'm Following view
		UIEvents.gotoHomeAndGotoImFollowing(ui);
		
		// Create the news story to be verified
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the like / recommend event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Name: test_WikiPage_CommentedWikipage_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Private access that you have owner access to</B></li>
	*<li><B>Step: testUser 2 Log in to Home who is following the wiki page</B></li>
	*<li><B>Step: testUser 1 Comment on a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the wiki.page.commented story is displayed within the Communities and Wikis view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9729D5E9E176A25685257BD6004ADF5A">TTT -AS - FOLLOW - WIKI PAGE - 00045 - wiki.page.commented - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikipage_CommentedWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki with User 2 then following the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage communityWikiPage = CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		/**
		 * Log in as User 2 and follow the wiki page using the UI - this is a critical step since using the API to follow the wiki pages in restricted communities
		 * intermittently returns 403: FORBIDDEN errors (even when User 2 has logged into the community beforehand).
		 */
		CommunityWikiEvents.loginAndNavigateToCommunityAndFollowCommunityWikiPage(restrictedCommunity, baseCommunity, communityWikiPage, ui, uiWiki, uiCo, testUser2, communitiesAPIUser2, false);
		
		// User 1 will now post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityWikiEvents.addCommentToWikiPage(communityWikiPage, testUser1, wikisAPIUser1, comment);
		
		// Navigate to Home and then to the I'm Following view
		UIEvents.gotoHomeAndGotoImFollowing(ui);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the comment event and comment content are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}