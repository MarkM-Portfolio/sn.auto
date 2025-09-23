package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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

public class FVT_VisitorModel_SharedExternallyHeader_BlogsEvents_NoVisitor extends SetUpMethods2{
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private BaseCommunity baseCommunity;
	private boolean isOnPremise;
	private CommunitiesUI uiCo;
	private Community restrictedCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a restricted community with User 3 added as a member and a follower and with the Blog widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseCommunity, testUser3, communitiesAPIUser3, testUser1, communitiesAPIUser1, BaseWidget.BLOG, isOnPremise);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_blogAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 Log in to Communities</B></li>
	*<li><B>Step: User 1 creates a private community adding User 3</B></li>
	*<li><B>Step: User 3 follow the community</B></li>
	*<li><B>Step: User 1 add blogs within this community</B></li>
	*<li><B>Step: User 3 log into Homepage / Updates / I'm Following / All -- verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears DOES NOT appear beside story</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B44FE5BEA2AAB98285257C890056CE7D">TTT - VISITORS - ACTIVITY STREAM - 00041 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 1)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_blogAdded() {
		
		ui.startTest();
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the 'Shared Externally' header is NOT displayed with the create blog event in any of the views
		HomepageValid.verifySharedExternallyHeader(ui, driver, createBlogEvent, null, null, null, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_blogEntry() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add blogs within this community</B></li>
	*<li><B>Step: testUser1 add a blog entry</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears DOES NOT appear beside story</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B44FE5BEA2AAB98285257C890056CE7D">TTT - VISITORS - ACTIVITY STREAM - 00041 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 2)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_blogEntry() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an entry in the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity);
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the 'Shared Externally' header is NOT displayed with the create entry event in any of the views
		HomepageValid.verifySharedExternallyHeader(ui, driver, createEntryEvent, baseBlogPost.getContent(), null, null, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_blogEntryComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 Log in to Communities</B></li>
	*<li><B>Step: User 1 creates a private community adding User 3</B></li>
	*<li><B>Step: User 3 follow the community</B></li>
	*<li><B>Step: User 1 add blogs within this community</B></li>
	*<li><B>Step: User 1 add a blog entry</B></li>
	*<li><B>Step: User 1 comment on the blog entry</B></li>
	*<li><B>Step: User 3 log into Homepage / Updates / I'm Following / Communities -- verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears DOES NOT appear beside story</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B44FE5BEA2AAB98285257C890056CE7D">TTT - VISITORS - ACTIVITY STREAM - 00041 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 2)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_blogEntryComment() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an entry in the community blog and will comment on the entry
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndAddComment(restrictedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
				
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the 'Shared Externally' header is NOT displayed with the comment on entry event in any of the views
		HomepageValid.verifySharedExternallyHeader(ui, driver, commentOnEntryEvent, baseBlogPost.getContent(), baseBlogComment.getContent(), null, TEST_FILTERS, false);
				
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_trackBack() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 Log in to Communities</B></li>
	*<li><B>Step: User 1 creates a private community adding User 3</B></li>
	*<li><B>Step: User 3 follow the community</B></li>
	*<li><B>Step: User 1 add blogs within this community</B></li>
	*<li><B>Step: User 1 add a blog entry</B></li>
	*<li><B>Step: User 1 comment on the blog entry</B></li>
	*<li><B>Step: User 1 create a trackback on the blog</B></li>
	*<li><B>Step: User 3 log into Homepage / Updates / I'm Following / All -- verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears DOES NOT appear beside story</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B44FE5BEA2AAB98285257C890056CE7D">TTT - VISITORS - ACTIVITY STREAM - 00041 - SHARED EXTERNALLY HEADER - BLOG EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"}, priority = 2)
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_trackBack() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an entry in the community blog and will comment on the entry
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndAddComment(restrictedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now log in and post a trackback on the community blog
		String trackbackComment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddCommunityTrackbackComment(ui, driver, testUser1, baseCommunity, communitiesAPIUser1, restrictedCommunity, uiCo, baseBlogPost, trackbackComment, false);
		
		// Log in as User 3 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, true);
		
		// Create the news story to be verified
		String createTrackbackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the 'Shared Externally' header is NOT displayed with the left a trackback event in any of the views
		HomepageValid.verifySharedExternallyHeader(ui, driver, createTrackbackEvent, baseBlogPost.getContent(), baseBlogComment.getContent(), trackbackComment, TEST_FILTERS, false);
		
		ui.endTest();
	}
}