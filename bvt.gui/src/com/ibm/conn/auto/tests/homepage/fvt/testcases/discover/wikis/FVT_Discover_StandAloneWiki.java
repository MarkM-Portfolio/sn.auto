package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.wikis;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.wikis.WikiNewsStories;
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

public class FVT_Discover_StandAloneWiki extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis };
	
	private APIWikisHandler wikisAPIUser1;
	private BaseWiki baseWikiPublic, baseWikiPrivate;
	private HashMap<Wiki, APIWikisHandler> wikisForDeletion = new HashMap<Wiki, APIWikisHandler>();
	private User testUser1, testUser2;
	private Wiki privateWiki, publicWiki;
									   
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		wikisAPIUser1 = initialiseAPIWikisHandlerUser(testUser1);
		
		// User 1 will now create a public wiki to be used in the tests
		baseWikiPublic = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(baseWikiPublic, testUser1, wikisAPIUser1);
		
		// User 1 will now create a private wiki to be used in the tests
		baseWikiPrivate = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly);
		privateWiki = WikiEvents.createWiki(baseWikiPrivate, testUser1, wikisAPIUser1);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the public wiki created during the test
		WikiEvents.deleteWiki(publicWiki, testUser1, wikisAPIUser1);
		
		// Delete the private wiki created during the test
		WikiEvents.deleteWiki(privateWiki, testUser1, wikisAPIUser1);
		
		// Delete all of the additional wikis created during the test
		Set<Wiki> wikisToDelete = wikisForDeletion.keySet();
		
		for(Wiki wiki: wikisToDelete){
			wikisForDeletion.get(wiki).deleteWiki(wiki);
		}
	}
	
	/**
	* wikiCreation_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Create a new wiki with public visibility</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that the news story for wiki.library.created is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2E75299AB05ABD9A852578760079E796">TTT - DISC - WIKIS - 00010 - WIKI.LIBRARY.CREATED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void wikiCreation_PublicWiki() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiEvent = WikiNewsStories.getCreateWikiNewsStory(ui, baseWikiPublic.getName(), testUser1.getDisplayName());

		// Verify that the create wiki event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, baseWikiPublic.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* wikiDeletion_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and add delete the entire wiki</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that any news story related to this Wiki Library no longer appears</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7F0D467E5DCDF7AC852578760079E7B9">TTT - DISC - WIKIS - 00080 - WIKI.LIBRARY.DELETED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiDeletion_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a public wiki
		BaseWiki baseWiki = WikiBaseBuilder.buildBaseWiki(testName + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		Wiki wiki = WikiEvents.createWiki(baseWiki, testUser1, wikisAPIUser1);

		// Add the wiki to the HashMap for AfterClass deletion
		wikisForDeletion.put(wiki, wikisAPIUser1);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiEvent = WikiNewsStories.getCreateWikiNewsStory(ui, baseWiki.getName(), testUser1.getDisplayName());

		// Verify that the create wiki event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, baseWiki.getDescription().trim()}, TEST_FILTERS, true);
		
		// Delete the wiki
		WikiEvents.deleteWiki(wiki, testUser1, wikisAPIUser1);
		wikisForDeletion.remove(wiki);

		// Verify that the create wiki event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, baseWiki.getDescription().trim()}, TEST_FILTERS, false);

		ui.endTest();
	}
	
	/**
	* wikiPageCreation_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and create a new page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that the news story for wiki.page.created is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CC06B2D8D05140CF852578760079E79B">TTT - DISC - WIKIS - 00020 - WIKI.PAGE.CREATED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageCreation_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());

		// Verify that the create wiki page event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* wikiPageDeletion_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and add delete an existing page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that any news story related to this Wiki Page no longer appears</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7406145D1AE75F28852578760079E7B4">TTT - DISC - WIKIS - 00070 - WIKI.PAGE.DELETED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageDeletion_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiPage wikiPage = WikiEvents.createWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());

		// Verify that the create wiki page event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription().trim()}, TEST_FILTERS, true);
		
		// Delete the wiki page
		WikiEvents.deleteWikiPage(publicWiki, wikiPage, testUser1, wikisAPIUser1);

		// Verify that the create wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* wikiPageEdit_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and edit an existing page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that the news story for wiki.page.updated is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/63D78B73B5DCFCA2852578760079E79F">TTT - DISC - WIKIS - 00030 - WIKI.PAGE.UPDATED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageEdit_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page and edit the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndEditWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String editWikiPageEvent = WikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());

		// Verify that the wiki page edited event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{editWikiPageEvent, baseWikiPage.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* wikiPageLike_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and add a recommend an existing page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that the news story for wiki.page.recommended is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4BDAB798BC9FCADF852578760079E7AA">TTT - DISC - WIKIS - 00050 - WIKI.PAGE.RECOMMENDED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageLike_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page and like / recommend the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndLikeWikiPage(publicWiki, baseWikiPage, testUser1, wikisAPIUser1);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeWikiPageEvent = WikiNewsStories.getLikeTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());

		// Verify that the wiki page liked event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeWikiPageEvent, baseWikiPage.getDescription().trim()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* wikiPageComment_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and add a comment on an existing page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that the news story for wiki.page.commented is seen</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B79DC66267A19CA3852578760079E7AF">TTT - DISC - WIKIS - 00060 - WIKI.PAGE.COMMENTED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageComment_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page and comment on the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddComment(publicWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String wikiPageCommentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());

		// Verify that the wiki page comment event is displayed in all of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{wikiPageCommentEvent, baseWikiPage.getDescription().trim(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* wikiPageCommentEdit_PublicWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with public visibility and update an existing comment on an existing page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Activity Stream / Discover</B></li>
	*<li><b>5: Filter by Wikis</li></b>
	*<li><B>Verify: Verify that the news story for wiki.page.comment.created is seen with the updated comment</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/96E46A5A9095E38F852579BC005E905B">TTT - DISC - WIKIS - 00070 - WIKI.PAGE.COMMENT.UPDATED - PUBLIC WIKI</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageCommentEdit_PublicWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page, comment on the wiki page and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentAndEditComment(publicWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());
		String updateCommentEvent = WikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPublic.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on wiki page event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription().trim(), updatedComment}, filter, true);
			
			// Verify that the update comment on wiki page event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}

	/**
	* wikiPageCommentEdit_PrivateWiki() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: Log into Wikis</B></li>
	*<li><B>2: Open a wiki with private visibility and update an existing comment on an existing page</B></li>
	*<li><B>3: Log into Home as a different user</B></li>
	*<li><B>4: Go to Home / Updates / Discover / All & Wikis</B></li>
	*<li><B>Verify: Verify that the news story for wiki.page.comment.updated is NOT seen - negative test</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/77495DB38EB8D3CB852579BC005E2BE5">TTT - DISC - WIKIS - 00070 - WIKI.PAGE.COMMENT.UPDATED - PRIVATE WIKI (NEG)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void wikiPageCommentEdit_PrivateWiki() {
		
		String testName = ui.startTest();
		
		// User 1 create a new wiki page, comment on the wiki page and edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String updatedComment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPageAndAddCommentAndEditComment(privateWiki, baseWikiPage, testUser1, wikisAPIUser1, comment, updatedComment);

		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified			
		String commentEvent = WikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPrivate.getName(), testUser1.getDisplayName());
		String updateCommentEvent = WikiNewsStories.getUpdateCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseWikiPrivate.getName(), testUser1.getDisplayName());
		
		// Verify that the comment event, update comment event, original comment and updated comment are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, updateCommentEvent, baseWikiPage.getDescription(), comment, updatedComment}, TEST_FILTERS, false);
					
		ui.endTest();
	}
}