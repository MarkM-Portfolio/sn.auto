package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.wiki;

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
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
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

public class FVT_ImFollowing_Tags_PrivateCommunity_Wiki extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APIWikisHandler wikisAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityWithTag;
	private Community restrictedCommunity, restrictedCommunityWithTag;
	private String tagToFollow;	
	private User testUser1, testUser2;
	private Wiki communityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
				
		wikisAPIUser2 = initialiseAPIWikisHandlerUser(testUser2);
		
		// Have User 1 follow the tag for these tests
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 will now create a restricted community with the Wiki widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.WIKI, isOnPremise, testUser2, communitiesAPIUser2);
		
		// User 2 will now create a restricted community with custom tag included and with the Wiki widget added
		baseCommunityWithTag = CommunityBaseBuilder.buildBaseCommunityWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED, tagToFollow);
		restrictedCommunityWithTag = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityWithTag, BaseWidget.WIKI, isOnPremise, testUser2, communitiesAPIUser2);
	
		// Retrieve the relevant community wiki (ie. from the community without the custom tag) for use in all relevant tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
		communitiesAPIUser2.deleteCommunity(restrictedCommunityWithTag);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_wiki.library.created_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a Private community that you have owner access</B></li>
	*<li><B>Step: testUser 2 create a wiki library and add the tag User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Wikis & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the wiki.library.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Wikis</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/780C5CD87647A90D852578FD002CF747">TTT - AS - FOLLOW - TAG - WIKIS - 00215 - wiki.library.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void wikilibrarycreated_PrivateCommunity() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseCommunityWithTag.getName(), testUser2.getDisplayName());
		
		// Verify that the create wiki event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiEvent}, TEST_FILTERS, false);
		
		ui.endTest();			
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_wiki.page.created_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a Private community that you have owner access</B></li>
	*<li><B>Step: testUser 2 create a wiki page and add the tag User 1 is following</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Wikis & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the wiki.page.created story is not displayed in Homepage / All Updates filtered by Tags and Wikis</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0E5B6F362F0065B0852578FD002CFD93">TTT - AS - FOLLOW - TAG - WIKIS - 00225 - wiki.page.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void createWikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 2 will now create a wiki page in the community wiki which includes the tag being followed by User 1
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPageWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser2, wikisAPIUser2);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
				
		// Create the news story to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser2.getDisplayName());
				
		// Verify that the create wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_WikiPageUpdated_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a public community that you have owner access</B></li>
	*<li><B>Step: testUser 2 create the wikipage  and add the tag User 1 is following</B></li>
	*<li><B>Step: testUser 2 update a wiki page </B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Wikis & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the wiki.page.updated story is not displayed in Homepage / All Updates filtered by Communities, Tags and Wikis</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0AF2B4401030DE85852578FD002D037F">TTT - AS - FOLLOW - TAG - WIKIS - 00235 - wiki.page.updated - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void updatewikipage_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 2 will now create a wiki page in the community wiki which includes the tag being followed by User 1 and will then update the wiki page
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPageWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityWikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, testUser2, wikisAPIUser2);
				
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
						
		// Create the news story to be verified
		String updateWikiPageEvent = CommunityWikiNewsStories.getUpdateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser2.getDisplayName());
						
		// Verify that the update wiki page event is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateWikiPageEvent, baseWikiPage.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
}