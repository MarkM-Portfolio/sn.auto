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
/* Copyright IBM Corp. 2016                                         */
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

public class FVT_ImFollowing_PrivateCommunity_Ideationblog extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;	
	private Community restrictedCommunity;
	private User testUser1 , testUser2;	
	
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
		
		// User 1 will now create a restricted community with User 2 added as a member and follower and the Ideation Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1, BaseWidget.IDEATION_BLOG, isOnPremise);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_Community IdeaBlog Creation_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Create an ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.created is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4E731A2F319F21F9852578F7004AFC0B">TTT - AS - FOLLOW - COMMUNITY - 00043 - community.ideationblog.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CommunityIdeationblog_PrivateCommunity(){
	
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
	*<li><B>Name:</B> test_Community IdeaBlog Idea Creation_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Create an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.created is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A6FE1BF10908B0B7852578F7004B31EA">TTT - AS - FOLLOW - COMMUNITY - 00053 - community.ideationblog.idea.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_IdeationblogIdea_PrivateCommunity(){
	
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
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
	*<li><B>Name:</B> test_Community IdeaBlog Idea Updated_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a Private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Update an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.updated is NOT seen in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E87BE560DDC937CF852578F7004BBA2C">TTT - AS - FOLLOW - COMMUNITY - 00063 - community.ideationblog.idea.updated - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdateIdea_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will update the description of the idea
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndEditDescription(restrictedCommunity, baseBlogPost, editedDescription, testUser1, communityBlogsAPIUser1);
				
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
	*<li><B>Name:</B> test_AddIdeaComment_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a Private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Add a comment on an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.comment.created is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3026A15D631568BF852578F7004C422F">TTT - AS - FOLLOW - COMMUNITY - 00073 - community.ideationblog.comment.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddIdeaComment_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will comment on the idea
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndAddComment(restrictedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
				
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
	*<li><B>Name:</B> test_VoteIdea_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a Private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Vote for an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.voted is NOT seen in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9C88F512D0AA7BA8852578F7004D325F">TTT - AS - FOLLOW - COMMUNITY - 00093 - community.ideationblog.idea.voted - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_VoteIdea_PrivateCommunity(){
	
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndVoteForIdea(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
				
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
	*<li><B>Name:</B> test_IdeaCommentRecommended_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a Private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Recommend a comment within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.comment.recommended  is NOT seen in any of the views</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5CA56183830FECF6852578F7004DCDA6">TTT - AS - FOLLOW - COMMUNITY - 00103 - community.ideationblog.comment.recommended - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_IdeaCommentRecommended_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will comment on the idea
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogComment blogComment = CommunityBlogEvents.createIdeaAndAddComment(restrictedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
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
	*<li><B>Name:</B> test_GraduateIdea_PrivateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Open a Private community that you have owner access to and add user 2 as a member</B></li>
	*<li><B>Step: testUser 1 Graduate an idea within the ideation blog</B></li>
	*<li><B>Step: testUser 2 Log in to Home as a different user who is following the community</B></li>		
	*<li><B>Step: testUser 2 Homepage / Updates / I'm Following / All, Communities & Blogs </B></li>	
	*<li><B>Verify:</B> Verify that the news story for community.ideationblog.idea.graduated is seen in the Communities and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A353929EFD3BDB2D852578F7004E3F34">TTT - AS - FOLLOW - COMMUNITY - 00113 - community.ideationblog.idea.graduated - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_GraduateIdea_PrivateCommunity(){
	
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(restrictedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, restrictedCommunity, baseCommunity, baseBlogPost, testUser1, communitiesAPIUser1, false);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
				
		// Create the news story to be verified
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the graduate idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
				
		ui.endTest();		
	}
}