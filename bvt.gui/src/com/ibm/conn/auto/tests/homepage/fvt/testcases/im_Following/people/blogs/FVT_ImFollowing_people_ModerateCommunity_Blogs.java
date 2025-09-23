package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.blogs;

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
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 06/2015
 */

public class FVT_ImFollowing_people_ModerateCommunity_Blogs extends SetUpMethodsFVT {

	private String TEST_FILTERS[];
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity, baseCommunityForEdit;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity, moderatedCommunityForEdit;
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
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		// Add the commonly used filters to the TEST_FILTERS array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterBlogs;
		TEST_FILTERS[2] = HomepageUIConstants.FilterCommunities;
		
		// User 2 will follow User 1 through API
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 1 create a moderated community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a moderated community to be used for editing the Blog title
		baseCommunityForEdit = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunityForEdit = CommunityEvents.createNewCommunityAndAddWidget(baseCommunityForEdit, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunityForEdit);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);	
	}

	/**
	*<ul>
	*<li><B>Name: test_people_CreateBlog_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Create a Moderate community</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Log back </B></li>
	*<li><B>Step: testUser 1 Create a Blog within the community</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & People </B></li>
	*<li><B>Verify: Verify that the story blog.created is displayed in communities, people and blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F8B2C569A1ED0627852578FB003B55AE">TTT - AS - FOLLOW - PERSON - BLOGS - 00073 - blog.created - MODERATE COMMUNTY BLOG</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_People_CreateBlog_ModerateCommunity(){

		ui.startTest();
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createBlogEvent = CommunityBlogNewsStories.getCreateBlogNewsStory(ui, baseCommunityForEdit.getName(), testUser1.getDisplayName());
		
		// Verify that the create blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createBlogEvent}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_BlogSettingUpdate_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>	
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Create a Moderated community</B></li>
	*<li><B>Step: testUser 2 Log in</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Log back </B></li>
	*<li><B>Step: testUser 1 update the settings of an existing blog within the community (i.e. Name)</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & People </B></li>	
	*<li><B>Verify: Verify that the story blog.settings.updated is displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9F25F2CF446EF945852579BF004562C5">TTT - AS - FOLLOW - PERSON - BLOGS - 00077 - blog.settings.udpated - MODERATE COMMUNTY BLOG (NEG SC NOV)</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_People_BlogSettingUpdate_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now log in and edit the name of the community blog
		String editedBlogName = testName + Helper.genStrongRand();
		CommunityBlogEvents.loginAndEditBlogName(moderatedCommunityForEdit, baseCommunityForEdit, ui, driver, uiCo, testUser1, communitiesAPIUser1, editedBlogName, false);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, true);
				
		// Create the news story to be verified
		String editBlogEvent = CommunityBlogNewsStories.getUpdateBlogNewsStory(ui, editedBlogName, testUser1.getDisplayName());
		
		// Verify that the edit blog event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{editBlogEvent}, TEST_FILTERS, true);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_AddBlogEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Create a Moderate community with Moderate Access</B></li>
	*<li><B>Step: testUser 1 Add the blogs widget within this community </B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Create a blog entry </B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & People</B></li>
	*<li><B>Verify: Verify that the news story for blog.entry.created is displayed in the Communities, People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/73579C32CAAD8CC0852578FB003C661E">TTT - AS - FOLLOW - PERSON - BLOGS - 00084 - blog.entry.created - MODERATE COMMUNITY BLOG</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_AddBlogEntry_ModerateCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_People_Update_BlogEntry_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new Moderated community with Moderate access</B></li>
	*<li><B>Step: testUser 2 FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Update a blog entry</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>
	*<li><B>Verify: Verify that the blog.entry.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C7195CAD980890CB852578FB003C6BAF">TTT - AS - FOLLOW - PERSON - BLOGS - 00093 - blog.entry.updated - MODERATE COMMUNITY BLOG (NEG SC NOV)</a></li>
	*</ul>
	*/		
	@Test (groups={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_Update_BlogEntry_ModerateCommunity(){

		String testName = ui.startTest();

		// User 1 will now add a blog entry to the community blog and will edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndEditDescription(moderatedCommunity, baseBlogPost, editedDescription, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityBlogNewsStories.getUpdateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create blog entry event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the update blog entry event and updated description are NOT displayed in any of the view
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, editedDescription}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_People_AddComment_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new Moderate community with Moderate access</B></li>
	*<li><B>Step: testUser 2 FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Create a comment on a blog entry</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>
	*<li><B>Verify:</B> Verify that the blog.comment.created is displayed within the Communities, People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E8D534E4A330F29E852578FB00499EA1">TTT - AS - FOLLOW - PERSON - BLOGS - 00103 - blog.comment.created - MODERATE COMMUNITY BLOG</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_AddComment_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community blog and will post a comment to the entry
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on entry event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_People_RecommendBlogEntry_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with Moderate access</B></li>
	*<li><B>Step: testUser 2 FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Recommend an existing blog entry</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>
	*<li><B>Verify:</B> Verify that the blog.entry.recommended story is displayed in the Communities, People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A4FE1A3C2BEF6789852578FB004D4E40">TTT - AS - FOLLOW - PERSON - BLOGS - 00123 - blog.entry.recommended - MODERATE COMMUNITY BLOG</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_RecommendBlogEntry_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community blog and will like / recommend the entry 
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndLikeBlogPost(moderatedCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String likeEntryEvent = CommunityBlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the like entry event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeEntryEvent, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name:</B> test_People_RecommendBlogComment_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with Moderate access</B></li>
	*<li><B>Step: testUser 2 FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Recommend an Comment within an existing blog entry</B></li>	
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>
	*<li><B>Verify:</B> Verify that the blog.comment.recommended story is displayed within the Communities, People and Blogs view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E22CEEBDB2894FE1852578FB004D5787">TTT - AS - FOLLOW - PERSON - BLOGS - 00133 - blog.comment.recommended - MODERATE COMMUNITY BLOG</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_RecommendBlogComment_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogEntry = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, moderatedCommunity);
		
		// User 1 will now post a comment to the entry and will like / recommend the comment
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.createCommentAndLikeComment(blogEntry, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the like comment event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Name:</B> test_People_DeleteBlogComment_ModerateCommunity()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 Log in to Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with Moderate access</B></li>
	*<li><B>Step: testUser 1 Add the blogs widget within this community</B></li>
	*<li><B>Step: testUser 2 FOLLOW User 1</B></li>
	*<li><B>Step: testUser 1 Comment on an existing blog entry</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ I'm Following Filtered by Blogs, People Communities - verification point #1 #2 #3</B></li>
	*<li><B>Step: testUser 1 Login</B></li>	
	*<li><B>Step: testUser 1 Delete comment</B></li>
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Blogs & people</B></li>
	*<li><B>Verify:</B> #1, #2, #3 Verify that the news story for blog.comment.created is seen in the Communities,Blogs and People view</B></li>
    *<li><B>Verify:</B> #4, #5, #6 Verify that created comment has been deleted from Communities, Blogs and People  view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A9BF809AC07CB52F85257B4F004E585F">TTT - AS - Follow - person - Blogs - 00151 - blog.comment.deleted - moderated community blog</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_People_DeleteBlogComment_ModerateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community blog and will post a comment to the entry
		BaseBlogComment baseBlogComment = BlogBaseBuilder.buildBaseBlogComment();
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogComment blogEntryComment = CommunityBlogEvents.createBlogPostAndAddComment(moderatedCommunity, baseBlogPost, baseBlogComment, testUser1, communityBlogsAPIUser1);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String commentOnEntryEvent = CommunityBlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on entry event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseBlogPost.getContent(), baseBlogComment.getContent()}, TEST_FILTERS, true);
		
		// User 1 will now delete the comment posted to the blog entry
		CommunityBlogEvents.deleteComment(blogEntryComment, testUser1, communityBlogsAPIUser1);
		
		// Create the news story to be verified
		String createEntryEvent = CommunityBlogNewsStories.getCreateEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the comment on entry event and User 1's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseBlogComment.getContent()}, null, false);
		}
		ui.endTest();
	}
}