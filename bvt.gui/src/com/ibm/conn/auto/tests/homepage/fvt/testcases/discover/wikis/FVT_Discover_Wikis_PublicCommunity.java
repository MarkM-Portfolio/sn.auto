package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.wikis;

import java.util.HashMap;
import java.util.Set;

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
 * @author Patrick Doherty
 */

public class FVT_Discover_Wikis_PublicCommunity extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
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
		
		// User 1 will now create a public community with the Wiki widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Retrieve the community wiki for use in all of the tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// Delete any additional communities created during the test
		Set<Community> setOfCommunities = communitiesForDeletion.keySet();
		
		for(Community community : setOfCommunities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}

	/**
	* discover_Wikis_publicCommunity_wikiCreated() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 adds the Wikis widget to the community</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the community wiki library creation news story in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DDF4537E730987AE852578760079E793">TTT - DISC - WIKIS - 00010 - WIKI.LIBRARY.CREATED - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void discover_Wikis_publicCommunity_wikiCreated(){

		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String createWelcomeWikiPageEvent = CommunityWikiNewsStories.getCreateWikiWelcomePageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create community wiki event and create welcome wiki page event are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, createWelcomeWikiPageEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* discover_Wikis_publicCommunity_wikiPageCreated() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 adds the Wikis widget to the community</B></li>
	*<li><B>Step: testUser1 creates a new wiki page the wiki in the community</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser2 can see the wiki page created news story in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3D6A5A4EF967CB5852578760079E799">TTT - DISC - WIKIS - 00020 - WIKI.PAGE.CREATED - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void discover_Wikis_publicCommunity_wikiPageCreated(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
				
		// Create the news story to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_updateWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to and has the wikis widget deployed</B></li>	
	*<li><B>Step: testUser 1 Edit an existing wiki page</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / Discover/ All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the news story for wiki.page.updated is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBB069E7ED5F7F16852578760079E79E">TTT - Disc - Wikis - 00030 - wiki.page.updated - public community</a></li>
	*@author Srinivas Vechha
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void discover_Wikis_publicCommunity_wikiPageUpdated(){

		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will then edit / update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified			
		String updateWikiPageEvent = CommunityWikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the update wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}		
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_LikeWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to and has the wikis widget deployed</B></li>	
	*<li><B>Step: testUser 1 Recommend a page within the wiki in this community</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / Discover / All, Communities & Wikis </B></li>
	*<li><B>Verify: Verify that the news story for wiki.page.recommended is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9BCB009E6AAB551A852578760079E7A9">TTT -Disc - Wikis - 00050 - wiki.page.recommended - public community</a></li>
	*@author Srinivas Vechha
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void discover_Wikis_LikeWikipage_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will then like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndLikeWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified			
		String likeWikiPageEvent = CommunityWikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the update wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}		
	
	/**
	*<ul>
	*<li><B>Name: test_Wikis_CommentedWikipage_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to and has the wikis widget deployed</B></li>
	*<li><B>Step: testUser 2 Comment on an existing wiki page</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / Discover / All, Communities & Wikis </B></li>
	*<li><B>Verify:Verify that the news story for wiki.page.commented is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/906A4E57AD160298852578760079E7AE">TTT -Disc - Wikis - 00060 - wiki.page.commented - public community</a></li>
	*@author Srinivas Vechha
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void discover_Wikis_CommentedWikipage_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will then comment on the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified			
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the update wiki page event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}	

	/**
	*<ul>
	*<li><B>Name: test_Discover_Wikis_UpdateCommentWikipage_PublicCommunityCloud()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to and has the wikis widget deployed</B></li>
	*<li><B>Step: testUser 1 Update an existing wiki page comment</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that the news story for wiki.page.comment.created is seen with the updated comment/B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FE4E68891E65CF48852579BC005E615E">TTT -Disc - Wikis - 00070 - wiki.page.comment.updated - public community</a></li>
	*@author Srinivas Vechha
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void discover_Wikis_Updatecomment_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a wiki page in the community wiki and will then comment on the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentAndEditComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
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
	*<li><B>Name: test_Discover_Wikis_wikiLibraryDeleted_PublicCommunity</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with public access that you have owner access to and has the wikis widget deployed</B></li>
	*<li><B>Step: testUser 1 Delete an entire wiki by removing the wiki widget</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that any news story related to this Wiki Library no longer appears</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F5116C216F41B2E1852578760079E7B7">TTT -Disc - Wikis - 00080 - wiki.library.deleted - public community</a></li>
	*@author Srinivas Vechha
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void discover_Wikis_deletedWikiLibrary_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a public community with the Wiki widget added
		BaseCommunity baseComm = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseComm, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news stories to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseComm.getName(), testUser1.getDisplayName());
		String createWelcomeWikiPageEvent = CommunityWikiNewsStories.getCreateWikiWelcomePageNewsStory(ui, baseComm.getName(), testUser1.getDisplayName());
		
		// Verify that the create community wiki event and create welcome wiki page event are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, createWelcomeWikiPageEvent}, TEST_FILTERS, true);
		
		// User 1 will now remove the Wiki widget from the community (this will delete the community wiki)
		CommunityEvents.deleteWidget(newCommunity, BaseWidget.WIKI, testUser1, communitiesAPIUser1);
		
		// Verify that the create community wiki event and create welcome wiki page event are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, createWelcomeWikiPageEvent}, TEST_FILTERS, false);
				
		// Perform clean up now that the test has completed
		communitiesAPIUser1.deleteCommunity(newCommunity);
		communitiesForDeletion.remove(newCommunity);
		ui.endTest();		
	}	
}	