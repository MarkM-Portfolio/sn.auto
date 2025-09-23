package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

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
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_WikiEvents_NoVisitor extends SetUpMethods2{
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseCommunity;
	private boolean isOnPremise;
	private Community restrictedCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;
	private Wiki communityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);		
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));	
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		wikisAPIUser1 = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a restricted community with User 3 added as a member and a follower and with the Wikis widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseCommunity, testUser3, communitiesAPIUser3, testUser1, communitiesAPIUser1, BaseWidget.WIKI, isOnPremise);
	
		// Retrieve the community wiki for use in the tests
		communityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);				
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_WikisAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add Wikis Widget to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7B06B6921744BDEA85257C8A003D2ACE">TTT - VISITORS - ACTIVITY STREAM - 00058 - SHARED EXTERNALLY HEADER - WIKI EVENTS- PRIVATE COMMUNITY -VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 1)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_WikisAdded() {
		
		ui.startTest();
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories and CSS selectors to be verified
		String createWikiEvent = CommunityWikiNewsStories.getCreateWikiNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String sharedExternallyIcon = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", createWikiEvent);
		String sharedExternallyMessage = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", createWikiEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create wiki event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createWikiEvent, baseCommunity.getDescription()}, filter, true);
			
			// Verify that the 'Shared Externally' icon and message components are NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{sharedExternallyIcon, sharedExternallyMessage}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_wikiPage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add Wikis Widget to  this community</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Wikis - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7B06B6921744BDEA85257C8A003D2ACE">TTT - VISITORS - ACTIVITY STREAM - 00058 - SHARED EXTERNALLY HEADER - WIKI EVENTS- PRIVATE COMMUNITY -VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 2)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_wikiPage() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPage(communityWiki, baseWikiPage, testUser1, wikisAPIUser1);
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories and CSS selectors to be verified
		String createWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String sharedExternallyIcon = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", createWikiPageEvent);
		String sharedExternallyMessage = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", createWikiPageEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createWikiPageEvent, baseWikiPage.getDescription()}, filter, true);
			
			// Verify that the 'Shared Externally' icon and message components are NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{sharedExternallyIcon, sharedExternallyMessage}, null, false);
		}
		ui.endTest();
	}
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_wikiPageComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add Wikis Widget to this community</B></li>
	*<li><B>Step: testUser1 add a wiki page</B></li>
	*<li><B>Step: testUser1 comment on the wiki page</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7B06B6921744BDEA85257C8A003D2ACE">TTT - VISITORS - ACTIVITY STREAM - 00058 - SHARED EXTERNALLY HEADER - WIKI EVENTS- PRIVATE COMMUNITY -VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 2)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_wikiPageComment() {
		
		String testName = ui.startTest();
		
		// User 1 will now add a wiki page to the community wiki and will post a comment to the wiki page
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, testUser1, wikisAPIUser1, comment);
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news stories and CSS selectors to be verified
		String commentOnWikiPageEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseCommunity.getName(), testUser1.getDisplayName());
		String sharedExternallyIcon = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", commentOnWikiPageEvent);
		String sharedExternallyMessage = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", commentOnWikiPageEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on wiki page event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnWikiPageEvent, baseWikiPage.getDescription(), comment}, filter, true);
			
			// Verify that the 'Shared Externally' icon and message components are NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{sharedExternallyIcon, sharedExternallyMessage}, null, false);
		}
		ui.endTest();
	}
}