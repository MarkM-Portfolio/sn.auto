package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.blogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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
 * This is a functional test for the Homepage Activity Stream (I'm Following / people) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 10/2015
 */

public class FVT_ImFollowing_people_CommunityIdeationBlog extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;
	
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());	
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);		
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		// Set the common filters to be added to the array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterBlogs;
		TEST_FILTERS[2] = HomepageUIConstants.FilterCommunities;
		
		// User 1 create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {

		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_People_DeleteIdeaBlogComment_publicCommunityBlog()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 1 Add the Ideablogs widget within this community</B></li>
	*<li><B>Step: testUser 2 Login and FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Comment on an existing Ideablog entry</B></li>	
	*<li><B>Verify:</B> test User 2 Login and Verify that the news story for blog.comment.created is seen in the Communities,Blogs and People view</B></li>
	*<li><B>Step: testUser 1 Login and Delete Comment</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>	
    *<li><B>Verify:</B> Verify that created comment has been deleted from Communities, blogs and People  view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/05F1EB98E432958685257B50004D566E">TTT - AS - Follow - person - Ideablogs - 00152 - Ideablog.comment.deleted - public community Ideablog</a></li>
	*</ul>
	*/
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void deleteIdeaBlogComment_publicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea for the ideation blog in the community and will then post a comment to the idea
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogComment ideaComment = CommunityBlogEvents.createIdeaAndAddComment(publicCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment event and comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent().trim(), baseBlogComment.getContent().trim()}, TEST_FILTERS, true);
		
		// User 1 will now delete the comment posted to the idea
		boolean deleted = CommunityBlogEvents.deleteComment(ideaComment, testUser1, communityBlogsAPIUser1);
		HomepageValid.verifyBooleanValuesAreEqual(deleted, true);
		
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent().trim()}, filter, true);
			
			// Verify that the comment on idea event and the comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, baseBlogComment.getContent().trim()}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_People_Edit IdeaBlog Comment_publicCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 1 Add the Ideablogs widget within this community</B></li>
	*<li><B>Step: testUser 2 FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Comment on an existing Ideablog entry</B></li>
	*<li><B>Verify:</B> testUser 2 Login and Verify that the news story for blog.comment.created is seen in I'm Following / All, Communities, Blogs & people</B></li>	
	*<li><B>Step: testUser 1 Login and Edit comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>	
    *<li><B>Verify:</B> Verify that the news story for Ideablog.comment.edited is NOT seen in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6C34BC4F0339024385257B50004DA9BF">TTT - AS - Follow - person - Ideablogs - 00162 - Ideablog.comment.edited - public community Ideablog (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void editIdeaBlogComment_PublicCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create an idea for the ideation blog in the community and will then post a comment to the idea
		BaseBlogComment baseBlogCommentBeforeEdit = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogComment blogComment = CommunityBlogEvents.createIdeaAndAddComment(publicCommunity, baseBlogPost, baseBlogCommentBeforeEdit, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment event and comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent().trim(), baseBlogCommentBeforeEdit.getContent().trim()}, TEST_FILTERS, true);
		
		// User 1 will now edit the comment posted to the idea
		BaseBlogComment baseBlogCommentAfterEdit = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.editComment(blogComment, baseBlogCommentAfterEdit, testUser1, communityBlogsAPIUser1);
		
		// Create the news story to be verified
		String updateCommentEvent =  CommunityBlogNewsStories.getUpdateCommentOnTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent().trim(), baseBlogCommentAfterEdit.getContent().trim()}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, baseBlogCommentBeforeEdit.getContent().trim()}, null, false);
		}
		ui.endTest();
	}			
}