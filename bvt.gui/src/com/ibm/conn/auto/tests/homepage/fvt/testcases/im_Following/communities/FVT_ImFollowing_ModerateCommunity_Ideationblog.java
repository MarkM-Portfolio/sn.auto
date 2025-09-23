package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.communities;

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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Community) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 01/2016
 */

public class FVT_ImFollowing_ModerateCommunity_Ideationblog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;	
	private Community moderatedCommunity;
	private User testUser1, testUser2;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		// User 1 will now create a moderated community with User 2 added as a follower and the Ideation Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, BaseWidget.IDEATION_BLOG, testUser1, communitiesAPIUser1, isOnPremise);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_Community IdeaBlog Creation_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Create an ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.created is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2391B2CB052F1767852578F7004AC14A">TTT - AS - FOLLOW - COMMUNITY - 00042 - community.ideationblog.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CommunityIdeationblog_ModerateCommunity() {

		ui.startTest();
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createIdeationBlogEvent = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create ideation blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeationBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name:</B> test_Community IdeaBlog Idea Creation_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Create an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.created is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/516DBC75FFC861CA852578F7004B3063">TTT - AS - FOLLOW - COMMUNITY - 00052 - community.ideationblog.idea.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_IdeationblogIdea_ModerateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the create idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name:</B> test_Community IdeaBlog Idea Updated_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Update an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.updated is NOT seen in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3AD45803B0D07DC9852578F7004BB8E9">TTT - AS - FOLLOW - COMMUNITY - 00062 - community.ideationblog.idea.updated - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdateIdea_ModerateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will update the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndEditDescription(moderatedCommunity, baseBlogPost, editedDescription, testUser1, communityBlogsAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateIdeaEvent = CommunityBlogNewsStories.getUpdateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create idea event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the update idea event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateIdeaEvent, editedDescription}, null, false);
		}	
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name:</B> test_AddIdeaComment_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Add a comment on an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.comment.created is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1085127BCE0037D1852578F7004C4100">TTT - AS - FOLLOW - COMMUNITY - 00072 - community.ideationblog.comment.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddIdeaComment_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will comment on the idea
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the comment on idea event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name:</B> test_VoteIdea_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Vote for an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.voted is NOT seen in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6A66AE9F983BBEE3852578F7004D311B">TTT - AS - FOLLOW - COMMUNITY - 00092 - community.ideationblog.idea.voted - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_VoteIdea_ModerateCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndVoteForIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
				
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
			
		for(String filter : TEST_FILTERS) {
			// Verify that the create idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the vote for idea event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{voteForIdeaEvent}, null, false);
		}	
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_IdeaCommentRecommended_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Recommend a comment within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.comment.recommended  is NOT seen in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/00A4573E36D4CE0D852578F7004DCC56">TTT - AS - FOLLOW - COMMUNITY - 00102 - community.ideationblog.comment.recommended - MODERATE COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_IdeaCommentRecommended_ModerateCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will comment on the idea
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogComment blogComment = CommunityBlogEvents.createIdeaAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now like / recommend the comment
		CommunityBlogEvents.likeComment(blogComment, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeCommentEvent = CommunityBlogNewsStories.getLikeTheirOwnCommentOnIdeaNewsStory(ui, baseBlogPost.getTitle(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, filter, true);
			
			// Verify that the like / recommend comment event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeCommentEvent}, null, false);
		}	
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name:</B> test_GraduateIdea_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a community with Moderate access that you own</B></li>
	*<li><B>Step: testUser 1 Graduate an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
    *<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.graduated is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F703369897F416A1852578F7004E3E20">TTT -  AS - FOLLOW - COMMUNITY - 00112 - community.ideationblog.idea.graduated - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_GraduateIdea_ModerateCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, moderatedCommunity, baseCommunity, baseBlogPost, testUser1, communitiesAPIUser1, false);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
				
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the graduate idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}