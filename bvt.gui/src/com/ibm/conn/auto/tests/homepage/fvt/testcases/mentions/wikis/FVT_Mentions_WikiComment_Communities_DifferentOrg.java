package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.wikis;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
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
/* Copyright IBM Corp. 2014, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_WikiComment_Communities_DifferentOrg extends SetUpMethods2 {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterWikis };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private APIWikisHandler wikisAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private boolean isOnPremise;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3;
	private Wiki moderatedCommunityWiki, publicCommunityWiki, restrictedCommunityWiki;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		wikisAPIUser1 = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());

		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())){
			isOnPremise = true;
		}
		else{
			isOnPremise = false;
		}
		
		// User 1 create a moderated community with the wiki widget
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseModeratedCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a public community with the wiki widget
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.WIKI, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a restricted community with the wiki widget and add User 2 as a member
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.WIKI, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
	
		// Retrieve all of the wikis for the communities for use in the tests
		moderatedCommunityWiki = CommunityWikiEvents.getCommunityWiki(moderatedCommunity, wikisAPIUser1);
		publicCommunityWiki = CommunityWikiEvents.getCommunityWiki(publicCommunity, wikisAPIUser1);
		restrictedCommunityWiki = CommunityWikiEvents.getCommunityWiki(restrictedCommunity, wikisAPIUser1);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* commentMention_publicCommunityWiki_discover_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a public community</B></li>
	*<li><B>Step: testUser1 add a wiki to the public community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the DIFFERENT organisation log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EC79B40C87E114F585257C6F007B55F7">TTT - DISCOVER - WIKIS - 00160 - MENTIONS IN WIKI COMMENT - PUBLIC COMMUNITY - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtdifforg"})
	public void commentMention_publicCommunityWiki_discover_diffOrg(){
		
		String testName = ui.startTest();
		
		// User 1 create a wiki page and add a comment mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(publicCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// User 3 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);

		ui.endTest();
	}

	/**
	* commentMention_modCommunityWiki_discover_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a moderated community</B></li>
	*<li><B>Step: testUser1 add a wiki to the moderated community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the DIFFERENT organisation log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4543C44221EED9B385257C6F007B55F6">TTT - DISCOVER - WIKIS - 00161 - MENTIONS IN WIKI COMMENT - MODERATE COMMUNITY - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtdifforg"})
	public void commentMention_modCommunityWiki_discover_diffOrg(){

		String testName = ui.startTest();

		// User 1 create a wiki page and add a comment mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(moderatedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// User 3 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String commentEvent = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);

		ui.endTest();
	}

	/**
	* commentMention_privateCommunityWiki_discover_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community</B></li>
	*<li><B>Step: testUser1 add a wiki to the private community</B></li>
	*<li><B>Step: testUser1 comment with a mentions to testUser2 on a wiki page</B></li>
	*<li><B>Step: testUser3 who is in the DIFFERENT organisation log into Homepage / Updates / Discover / All, Communities & Wikis</B></li>
	*<li><B>Verify: Verify that testUser3 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1E8AC32F8EE67A6285257C6F007B55F5">TTT - DISCOVER - WIKIS - 00162 - MENTIONS IN WIKI COMMENT - PRIVATE COMMUNITY - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtdifforg"})
	public void commentMention_privateCommunityWiki_discover_diffOrg(){

		String testName = ui.startTest();

		// User 1 create a wiki page and add a comment mentioning User 2
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityWikiEvents.createWikiPageAndAddCommentWithMentions(restrictedCommunityWiki, baseWikiPage, mentions, testUser1, wikisAPIUser1);
		
		// User 3 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String event = CommunityWikiNewsStories.getCommentOnTheirOwnWikiPageNewsStory(ui, baseWikiPage.getName(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{event, baseWikiPage.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
}