package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.blogs;

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
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 26/02/2014
 */

public class FVT_ImFollowing_ModeratedCommunity_IdeationBlogs extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
	private CommunitiesUI uiCo;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun = true)
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

		// User 1 create a moderated community and User2 follows it
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(baseCommunity, testUser2, communitiesAPIUser2, BaseWidget.IDEATION_BLOG, testUser1, communitiesAPIUser1, isOnPremise);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun = true)
	public void tearDown() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(moderatedCommunity, testUser1, communitiesAPIUser1);
	}

	/**
	*<ul>
	*<li><B>Name:</B> test_AddIdeaWidget_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the Ideation Blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new Ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the ideablog.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EEA2869DFC0C10B3852578760079EABB">TTT - NF - FOLLOW - IDEABLOGS - 00011 - IDEABLOG.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_AddIdeaWidget_ModeratedCommunity() {

		ui.startTest();

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateIdeationBlogNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_AddIdea_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A5D9991533B13759852578760079EAC0">TTT - NF - FOLLOW - IDEABLOGS - 00021 - IDEABLOG.IDEA.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddIdea_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Outline event to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_UpdateIdea_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea and update the idea</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.updated story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/110B676BCE31DA5E852578760079EAC3">TTT - NF - FOLLOW - IDEABLOGS - 00031 - IDEABLOG.IDEA.UPDATED - MODERATED COMMUNITY IDEABLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_UpdateIdea_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog and will edit the description of the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		String newDescription = Helper.genStrongRand();
		CommunityBlogEvents.createIdeaAndEditDescription(moderatedCommunity, baseBlogPost, newDescription, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Outline event to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String editIdeaEvent = CommunityBlogNewsStories.getUpdateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter: TEST_FILTERS) {
			// Verify that the create idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the update idea event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{editIdeaEvent, newDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_AddIdeaComment_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea and add a comment to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.commented story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BF36A9631D2739AA852578760079EAC6">TTT - NF - FOLLOW - IDEABLOGS - 00041 - IDEABLOG.COMMENT.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddIdeaComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog and will post a comment to the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createIdeaAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String ideaCommentEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{ideaCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_AddIdeaTrackback_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea and add a trackback to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.trackback.created story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/529EC69444F86927852578760079EAC7">TTT - NF - FOLLOW - IDEABLOGS - 00051 - IDEABLOG.TRACKBACK.CREATED - MODERATED COMMUNITY IDEABLOG</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_AddIdeaTrackback_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now log in and add a trackback comment to the idea
		String trackBackComment = Data.getData().StatusComment + Helper.genStrongRand();
		CommunityBlogEvents.loginAndAddIdeationBlogTrackbackComment(ui, driver, testUser1, baseCommunity, communitiesAPIUser1, moderatedCommunity, uiCo, baseBlogPost, trackBackComment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);

		// Outline the event to be verified
		String trkBackEvent = CommunityBlogNewsStories.getLeftATrackbackOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{trkBackEvent, baseBlogPost.getContent(), trackBackComment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_IdeaCommentRecommended_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea, add a comment to it and recommend (like) the comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.comment.recommended story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3DF2CC38A1C5869A852578760079EACD">TTT - NF - FOLLOW - IDEABLOGS - 00071 - IDEABLOG.COMMENT.RECOMMENDED - MODERATED COMMUNITY IDEABLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_IdeaCommentRecommended_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now post a comment to the idea and will like / recommend the comment
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndLikeComment(blogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline events to be verified
		String ideaCommentEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnTheirIdeaNewsStory(ui, baseBlogPost.getTitle(), testUser1.getDisplayName());

		for(String filter: TEST_FILTERS) {
			// Verify that the comment on idea event and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{ideaCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, filter, true);
			
			// Verify that the like comment event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeCommentEvent}, null, false);
		}
		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_GraduateIdea_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea and graduate the idea</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.graduated story is displayed in all filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3B7107AAB90D7FF8852578760079EAD0">TTT - NF - FOLLOW - IDEABLOGS - 00081 - IDEABLOG.IDEA.GRADUATED - MODERATED COMMUNITY IDEABLOG</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_GraduateIdea_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 1 will now log in and graduate the idea
		CommunityBlogEvents.loginAndGraduateIdea(ui, uiCo, moderatedCommunity, baseCommunity, baseBlogPost, testUser1, communitiesAPIUser1, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
		
		// Outline event to be verified		
		String graduateIdeaEvent = CommunityBlogNewsStories.getGraduatedTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{graduateIdeaEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_DeleteIdeaComment_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new blog by adding ideation blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea and add a comment to it</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities - Verification Point 1</B></li>
	*<li><B>Step: testUser 1 delete the comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities - Verification Point 2</B></li>
	*<li><B>Verify: Verify that the idea.comment.created story is displayed in all filters</B></li>
	*<li><B>Verify: Verify that the idea.comment.created story is NOT displayed in any filter</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C4BDBF4A91FA74785257B50004258C6">TTT - NF - FOLLOW - IDEABLOGS - 00091 - IDEABLOG.COMMENT.DELETED - MODERATED COMMUNITY IDEABLOG</a></li>
	* @author Hugh Caren
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_DeleteIdeaComment_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog and will post a comment to the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BlogComment blogComment = CommunityBlogEvents.createIdeaAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String ideaCommentEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		// Verify that the comment on idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{ideaCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		// User 1 delete the comment
		CommunityBlogEvents.deleteComment(blogComment, testUser1, communityBlogsAPIUser1);

		for(String filter: TEST_FILTERS) {
			// Verify that the create idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the comment on idea event and User 1's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{ideaCommentEvent, baseBlogComment.getContent()}, null, false);
		}
		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_VoteIdea_ModeratedCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 create a new community with moderated access</B></li>
	*<li><B>Step: testUser 2 follow this community</B></li>
	*<li><B>Step: testUser 1 go to a community you are the owner of with moderate access and add the ideation blogs widget to it</B></li>
	*<li><B>Step: testUser 1 create a new ideation blog by adding blogs from the customize menu</B></li>
	*<li><B>Step: testUser 1 create a new idea and vote for (like) the idea</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Blogs & Communities</B></li>
	*<li><B>Verify: Verify that the idea.recommended story is NOT displayed in any of the filters</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/05AF32B63424CB5A852578760079EACC">TTT - NF - FOLLOW - IDEABLOGS - 00061 - IDEABLOG.IDEA.VOTED - MODERATED COMMUNITY IDEABLOG (NEG SC NOV)</a></li>
	* @author Hugh Caren 
	*/
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_VoteIdea_ModeratedCommunity() {

		String testName = ui.startTest();

		// User 1 will now create an idea in the community ideation blog and will vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndVoteForIdea(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);

		// Outline event to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String likeIdeaEvent = CommunityBlogNewsStories.getVotedForTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());; 
		
		for(String filter: TEST_FILTERS) {
			// Verify that the create idea event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the vote for idea event is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{likeIdeaEvent}, null, false);
		}
		ui.endTest();		
	}	
}