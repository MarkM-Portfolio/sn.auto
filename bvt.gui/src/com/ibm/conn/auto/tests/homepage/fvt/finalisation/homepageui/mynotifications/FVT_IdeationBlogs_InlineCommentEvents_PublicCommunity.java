package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.mynotifications;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
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
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	9th November 2016
 */

public class FVT_IdeationBlogs_InlineCommentEvents_PublicCommunity extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2;
	private BaseCommunity basePublicCommunity;
	private boolean isOnPremise;
	private Community publicCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityBlogsAPIUser2 = new APICommunityBlogsHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a public community with the Ideation Blogs widget added
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_IdeationBlogs_PublicCommunity_InlineComments() 
	*<ul>
	*<li><B>1: User 1 create an Ideation Blog</B></li>
	*<li><B>2: User 1 add an idea</B></li>
	*<li><B>3: User 2 comment on the idea</B></li>
	*<li><B>4: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>5: User 2 add 3 more comments</B></li>
	*<li><B>6: User 2 vote for the idea</B></li>
	*<li><B>7: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the comment is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 comments are now shown inline in the view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/87BF60024C06CAE185257E2F0036A463">TTT - INLINE COMMENTS - 00080 - IDEATION BLOG EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_IdeationBlogs_PublicCommunity_InlineComments() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityIdea = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 2 will now post their first comment to the idea
		BaseBlogComment user2Comment1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityIdea, user2Comment1, testUser2, communityBlogsAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on idea event and User 2's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent(), user2Comment1.getContent()}, TEST_FILTERS, true);
		
		// User 2 will now post their second comment to the idea
		BaseBlogComment user2Comment2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityIdea, user2Comment2, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now post their third comment to the idea
		BaseBlogComment user2Comment3 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityIdea, user2Comment3, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now post their fourth comment to the idea
		BaseBlogComment user2Comment4 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createComment(communityIdea, user2Comment4, testUser2, communityBlogsAPIUser2);
		
		// User 2 will now vote for the idea
		CommunityBlogEvents.likeOrVote(communityIdea, testUser2, communityBlogsAPIUser2);
		
		// Create the news story to be verified
		String votedForIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_User(ui, baseBlogPost.getTitle(), basePublicCommunity.getName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on idea event, vote for idea event and User 2's third and fourth comments are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, votedForIdeaEvent, baseBlogPost.getContent(), user2Comment3.getContent(), user2Comment4.getContent()}, filter, true);
			
			// Verify that User 2's first and second comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment1.getContent(), user2Comment2.getContent()}, null, false);
		}
		ui.endTest();
	}
}