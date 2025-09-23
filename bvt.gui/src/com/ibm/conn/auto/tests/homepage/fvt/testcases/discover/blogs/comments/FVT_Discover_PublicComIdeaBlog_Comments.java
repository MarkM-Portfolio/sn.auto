package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.blogs.comments;

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
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
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

public class FVT_Discover_PublicComIdeaBlog_Comments extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1, communityBlogsAPIUser2, communityBlogsAPIUser3;
	private BaseBlogComment baseBlogCommentUser1, baseBlogCommentUser2;
	private BaseBlogPost baseBlogPost;
	private BaseCommunity baseCommunity;
	private BlogComment blogCommentUser1, blogCommentUser2, blogCommentUser3;
	private BlogPost blogPost;
	private Community publicCommunity;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);

		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		communityBlogsAPIUser2 = initialiseAPICommunityBlogsHandlerUser(testUser2);
		communityBlogsAPIUser3 = initialiseAPICommunityBlogsHandlerUser(testUser3);

		// User 1 create a public community and add the Ideation Blogs widget
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
		
		// Set all other relevant global test components to null
		blogPost = null;
		blogCommentUser1 = null;
		blogCommentUser2 = null;
		blogCommentUser3 = null;
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Remove the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	 * <ul>
	 * <li><B>Name:</B> PublicComIdeaComment()</li>
	 * <li><B>How:</B> Use the API to create public community owned by testUser1, add an idea blog, an Idea and a comment to the idea</li>
	 * <li><B>Step:</B> testUser1 log in to Communities</li>
	 * <li><B>Step:</B> testUser1 open a community you own with public access</li>
	 * <li><B>Step:</B> testUser1 add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> testUser1 add an idea in the widget</li>
	 * <li><B>Step:</B> testUser3 add a comment to the idea</li>
	 * <li><B>Step:</B> testUser2 log in to Connections</li>
	 * <li><B>Step:</B> testUser2 go to Home / Updates / Discover / All, Blogs & Communities</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.comment.created is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5A14F3D14ABAF417852578760079EAA8">TTT - DISC - IDEABLOGS - 00040 - IDEABLOG.COMMENT.CREATED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	 * @author Naomi Pakenham
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 1)
	public void PublicComIdeaComment() {

		ui.startTest();

		// User 1 will now create an idea in the ideation blog
		createBlogPostIfRequired();
		
		// User 3 will now comment on the idea
		BaseBlogComment baseBlogCommentUser3 = BlogBaseBuilder.buildBaseBlogComment();
		blogCommentUser3 = CommunityBlogEvents.createComment(blogPost, baseBlogCommentUser3, testUser3, communityBlogsAPIUser3);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String ideaCommentEvent = CommunityBlogNewsStories.getCommentOnTheIdeaNewsStory_User(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser3.getDisplayName());

		// Verify that the comment on idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{ideaCommentEvent, baseBlogPost.getContent(), baseBlogCommentUser3.getContent()}, TEST_FILTERS, true);
		
		// User 3 will now delete their blog comment posted from this test case
		deleteUser3BlogCommentIfRequired();
				
		ui.endTest();
	}
	
	/**
	* <ul>
	* <li><B>Name:</B> PublicComIdeaEditComment()()</li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with public access</B></li>
	*<li><B>Step: testUser 1 add the Ideation blog widget and create a new ideation blog and idea</B></li>
	*<li><B>Step: testUser 1 add a comment to the idea</B></li>	
	*<li><B>Step: testUser 2 login to Homepage</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \Discover Filter by Blogs</B></li>
	*<li><B>Step: testUser 2 click on entry link to open the idea created by testUser 1</B></li>
	*<li><B>Step: testUser 2 add a comment to the idea created by testUser 1</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: testUser 2 filter by Blogs - validation point #1</B></li>	
	*<li><B>Step: testUser 2 now edits their comment on the idea - validation point #2</B></li>
	*<li><B>Step: testUser 1 log in to Blogs again</B></li>
	*<li><B>Step: testUser 1 edits the idea comment that they created</B></li>
	*<li><B>Step: testUser 2 logs in to Home and goes to Home / Updates / Discover / All, Blogs & Communities - validation point #3</B></li>
	*<li><B>Verify: #1 - testUser 2 sees two comments on the idea, one created by themselves, the other created by User 1</B></li>
	*<li><B>Verify: #2 - testUser 2 sees 2 comments on the idea, one edited by themselves and one unedited by user 1</B></li>
	*<li><B>Verify: #3 - testUser 2 sees two edited comments on the idea</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/24E6FD13F8BEE03B85257B500047C26B">TTT - DISC - IDEABLOGS - 00112 - IDEABLOG.COMMENT.EDITED - PUBLIC COMMUNITY IDEABLOG</a></li>
	*</ul>	
	* @author Naomi Pakenham
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 2)
	public void PublicComIdeaEditComment() {

		ui.startTest();

		// User 1 will now create an idea in the ideation blog
		createBlogPostIfRequired();
		
		// User 3 will now delete their blog comment from a previous test case (only deletes the comment if it has not already been deleted)
		deleteUser3BlogCommentIfRequired();
		
		// User 1 will now post a comment to the ideation blog
		createUser1BlogCommentIfRequired();
		
		// User 2 will now post a comment to the ideation blog
		createUser2BlogCommentIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String twoCommentsOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheIdeaNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the two comments on idea event and both User 1 and User 2's comments are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnIdeaEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent(), baseBlogCommentUser2.getContent()}, TEST_FILTERS, true);

		// User 2 will now edit / update their comment
		BaseBlogComment editedBaseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.editComment(blogCommentUser2, editedBaseBlogCommentUser2, testUser2, communityBlogsAPIUser2);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the two comments on idea event, User 1's comment and User 2's updated comment are displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{twoCommentsOnIdeaEvent, baseBlogPost.getContent(), baseBlogCommentUser1.getContent(), editedBaseBlogCommentUser2.getContent()}, filter, true);
			
			// Verify that User 2's original comment is NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogCommentUser2.getContent()}, null, false);
		}
		// User 1 will now edit / update their comment
		BaseBlogComment editedBaseBlogCommentUser1 = BlogBaseBuilder.buildBaseBlogComment();
		CommunityBlogEvents.editComment(blogCommentUser1, editedBaseBlogCommentUser1, testUser1, communityBlogsAPIUser1);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the two comments on idea event, User 1's updated comment and User 2's updated comment are displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{twoCommentsOnIdeaEvent, baseBlogPost.getContent(), editedBaseBlogCommentUser1.getContent(), editedBaseBlogCommentUser2.getContent()}, filter, true);
			
			// Verify that User 1 and User 2's original comments are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseBlogCommentUser1.getContent(), baseBlogCommentUser2.getContent()}, null, false);
		}
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_deleteCommentToIdea_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log in to Communities</B></li>
	*<li><B>Step: testUser 1 creates a new community with public access</B></li>
	*<li><B>Step: testUser 1 add the Ideation Blog widget and create a new ideation blog and idea</B></li>
	*<li><B>Step: testUser 1 add a comment to the idea</B></li>	
	*<li><B>Step: testUser 2 login to Homepage</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \Discover Filter by Blogs</B></li>
	*<li><B>Step: testUser 2 click on entry link to open the idea created by testUser 1</B></li>
	*<li><B>Step: testUser 2 add a comment to the idea created by testUser 1</B></li>
	*<li><B>Step: testUser 2 go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: testUser 2 filter by Blogs - validation point #1</B></li>	
	*<li><B>Step: testUser 2 now deletes their comment on the idea - validation point #2</B></li>
	*<li><B>Step: testUser 1 log in to Blogs again</B></li>
	*<li><B>Step: testUser 1 deletes the idea comment that they created</B></li>
	*<li><B>Step: testUser 2 logs in to Home and go to Home / Updates / Discover / All, Blogs & Communities - validation point #3</B></li>
	*<li><B>Verify: #1 - testUser 2 sees two comments on the idea, one created by themselves, the other created by User 1</B></li>
	*<li><B>Verify: #2 - testUser 2 sees one comment on the idea, the comment author is testUser 1</B></li>
	*<li><B>Verify: #3 - testUser 2 sees no comments on the idea</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/390E15D4B4D39D4885257B5000478280">TTT - DISC - IDEABLOGS - 00102 - IDEABLOG.COMMENT.DELETED - PUBLIC COMMUNITY IDEABLOG</a></li>
	*</ul>
	* @author Patrick Doherty
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_deleteCommentToIdea_PublicCommunity() {
		
		ui.startTest();

		// User 1 will now create an idea in the ideation blog
		createBlogPostIfRequired();
		
		// User 3 will now delete their blog comment from a previous test case (only deletes the comment if it has not already been deleted)
		deleteUser3BlogCommentIfRequired();
		
		// User 1 will now post a comment to the ideation blog
		createUser1BlogCommentIfRequired();
		
		// User 2 will now post a comment to the ideation blog
		createUser2BlogCommentIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String twoCommentsOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheIdeaNewsStory_UserAndYou(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the comment on the idea event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{twoCommentsOnIdeaEvent, baseBlogPost.getContent(), blogCommentUser1.getContent(), blogCommentUser2.getContent()}, TEST_FILTERS, true);

		// User 2 deletes their comment
		BlogComment tempBlogCommentUser2 = blogCommentUser2;
		CommunityBlogEvents.deleteComment(blogCommentUser2, testUser2, communityBlogsAPIUser2);
		blogCommentUser2 = null;
		
		// Create the news story to be verified
		String oneCommentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnTheirOwnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the comment on idea event and User 1's comment are displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{oneCommentOnIdeaEvent, baseBlogPost.getContent(), blogCommentUser1.getContent()}, filter, true);
			
			// Verify that the two comments on idea event and User 2's comment are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{twoCommentsOnIdeaEvent, tempBlogCommentUser2.getContent()}, null, false);
		}
		// User 1 deletes their comment
		BlogComment tempBlogCommentUser1 = blogCommentUser1;
		CommunityBlogEvents.deleteComment(blogCommentUser1, testUser1, communityBlogsAPIUser1);
		blogCommentUser1 = null;
		
		// Create the news story to be verified
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create idea event and idea description are displayed in all views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createIdeaEvent, baseBlogPost.getContent()}, filter, true);
			
			// Verify that the two comments on idea event, the comment on idea event, User 1's comment and User 2's comment are NOT displayed in any of the views 
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{twoCommentsOnIdeaEvent, oneCommentOnIdeaEvent, tempBlogCommentUser1.getContent(), tempBlogCommentUser2.getContent()}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> PublicComIdeaCommentLike()</li>
	 * <li><B>Step:</B> testUser1 log in to Communities</li>
	 * <li><B>Step:</B> testUser1 open a community with public access that you have owner access to</li>
	 * <li><B>Step:</B> testUser1 add the ideablogs widget within this community</li>
	 * <li><B>Step:</B> testUser1 add an idea and a comment on the idea</li>
	 * <li><B>Step:</B> testUser3 recommend the comment</li>
	 * <li><B>Step:</B> testUser2 log in to Connections</li>
	 * <li><B>Step:</B> testUser2 go to Home / Updates / Discover / All, Blogs & Communities</li>
	 * <li><B>Verify:</B> Verify that the news story for ideablog.comment.recommended is seen</li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C1813F4D590E68C6852578760079EAB6">TTT - DISC - IDEABLOGS - 00070 - IDEABLOG.COMMENT.RECOMMENDED - PUBLIC COMMUNITY IDEABLOG</a></li>	
	 * </ul>
	 * @author Naomi Pakenham
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void PublicComIdeaCommentLike() {

		ui.startTest();

		// User 1 will now create an idea in the ideation blog
		createBlogPostIfRequired();
		
		// User 3 will now delete their blog comment from a previous test case (only deletes the comment if it has not already been deleted)
		deleteUser3BlogCommentIfRequired();
		
		// User 2 will now delete their blog comment from a previous test case (only deletes the comment if it has not already been deleted)
		if(blogCommentUser2 != null) {
			CommunityBlogEvents.deleteComment(blogCommentUser2, testUser2, communityBlogsAPIUser2);
		}
		// User 1 will now post a comment to the ideation blog
		createUser1BlogCommentIfRequired();
		
		// User 3 will now like / recommend the comment posted to the idea
		CommunityBlogEvents.likeComment(blogCommentUser1, testUser3, communityBlogsAPIUser3);

		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String likeCommentEvent = CommunityBlogNewsStories.getLikeACommentOnIdeaNewsStory(ui, baseBlogPost.getTitle(), testUser3.getDisplayName());
		
		// Verify that the like comment event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeCommentEvent, baseBlogPost.getContent(), blogCommentUser1.getContent()}, TEST_FILTERS, true);

		ui.endTest();
	}

	private void createBlogPostIfRequired() {
		if(blogPost == null) {
			// User 1 will now create an idea in the ideation blog
			baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(getClass().getSimpleName() + Helper.genStrongRand());
			blogPost = CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		}
	}
	
	private void createUser1BlogCommentIfRequired() {
		if(blogCommentUser1 == null) {
			// User 1 will now post a comment to the ideation blog
			baseBlogCommentUser1 = BlogBaseBuilder.buildBaseBlogComment();
			blogCommentUser1 = CommunityBlogEvents.createComment(blogPost, baseBlogCommentUser1, testUser1, communityBlogsAPIUser1);
		}
	}
	
	private void createUser2BlogCommentIfRequired() {
		if(blogCommentUser2 == null) {
			// User 2 will now post a comment to the ideation blog
			baseBlogCommentUser2 = BlogBaseBuilder.buildBaseBlogComment();
			blogCommentUser2 = CommunityBlogEvents.createComment(blogPost, baseBlogCommentUser2, testUser2, communityBlogsAPIUser2);
		}
	}
	
	private void deleteUser3BlogCommentIfRequired() {
		if(blogCommentUser3 != null) {
			CommunityBlogEvents.deleteComment(blogCommentUser3, testUser3, communityBlogsAPIUser3);
			blogCommentUser3 = null;
		}
	}
}