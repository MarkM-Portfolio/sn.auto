package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.wiki;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.wikis.WikiNewsStories;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016  	                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 09/2015
 */

public class FVT_ImFollowing_Tags_PrivateWiki extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterWikis, HomepageUIConstants.FilterTags };
	
	private APIWikisHandler wikisAPIUser2;
	private BaseWiki baseWiki, baseWikiWithTag;
	private String tagToFollow;
	private User testUser1, testUser2;
	private Wiki privateWiki, privateWikiWithTag;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);

		// User 1 will now log in and follow a tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 will now create a private wiki with the tag followed by User 1
		baseWikiWithTag = WikiBaseBuilder.buildBaseWikiWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly, tagToFollow);
		privateWikiWithTag = WikiEvents.createWiki(baseWikiWithTag, testUser2, wikisAPIUser2);
		
		// User 2 will now create a private wiki
		baseWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.EditorsAndOwners, ReadAccess.WikiOnly);
		privateWiki = WikiEvents.createWiki(baseWiki, testUser2, wikisAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the standalone wikis created during the test
		wikisAPIUser2.deleteWiki(privateWiki);
		wikisAPIUser2.deleteWiki(privateWikiWithTag);
	}
	
	/**
	*<ul>
	*<li><B>Name: wikilibrarycreated_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 start a private wiki and add the tag User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Wikis & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the wiki.library.created story is not displayed in Homepage / All Updates filtered by Tags and Wikis</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/553BAC9FA9D92EB1852578FD002CF375">TTT - AS - FOLLOW - TAG - WIKIS - 00212 - wiki.library.created - PRIVATE WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void wikilibrarycreated_PrivateWiki() {
		
		ui.startTest();		
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createWikiEvent = WikiNewsStories.getCreateWikiNewsStory(ui, baseWikiWithTag.getName(), testUser2.getDisplayName());
		
		// Verify that the create wiki event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent, baseWikiWithTag.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
    }	
	
	/**
	*<ul>
	*<li><B>Name: createWikipage_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private wiki you are the owner of</B></li>	
	*<li><B>Step: testUser 2 create a wiki page and add the tag User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Wikis & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is not displayed in Homepage / All Updates filtered by Tags and Wikis</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/74D0E5D2D208E352852578FD002CF9D9">TTT - AS - FOLLOW - TAG - WIKIS - 00222 - wiki.page.created - PRIVATE WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void createWikipage_PrivateWiki() {
		
		String testName = ui.startTest();
		
		// User 2 will now create a wiki page in the standalone wiki using the tag followed by User 1
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPageWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		WikiEvents.createWikiPage(privateWiki, baseWikiPage, testUser2, wikisAPIUser2);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
				
		// Create the news story to be verified
		String createWikiPageEvent = WikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser2.getDisplayName());
				
		// Verify that the create wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: updatewikipage_PrivateWiki()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a private wiki you are the owner of</B></li>	
	*<li><B>Step: testUser 2 update a wiki page and add the tag User 1 is following</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Wikis & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the wiki.page.updated story is not displayed in Homepage / All Updates filtered by Tags and Wikis</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C15925D1A6E57737852578FD002D000F ">TTT -  AS - FOLLOW - TAG - WIKIS - 00232 - wiki.page.updated - PRIVATE WIKI</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem"})
	public void updatewikipage_PrivateWiki(){
		
		String testName = ui.startTest();
		
		// User 2 will now create a wiki page in the standalone wiki using the tag followed by User 1 and will then update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPageWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		WikiEvents.createWikiPageAndEditWikiPage(privateWiki, baseWikiPage, testUser2, wikisAPIUser2);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String updateWikiPageEvent = WikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseWiki.getName(), testUser2.getDisplayName());
		
		// Verify that the update wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
}