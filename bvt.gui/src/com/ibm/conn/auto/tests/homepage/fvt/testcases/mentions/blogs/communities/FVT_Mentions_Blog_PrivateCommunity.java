package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.blogs.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
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
 * 	@author Anthony Cox
 *	Date:	20th October 2016
 */

public class FVT_Mentions_Blog_PrivateCommunity extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };

	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
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

		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);

		// User 1 will now create a restricted community with User 2 added as a member (User 3 will NOT be added as a member and will be used for non-member tests)
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.BLOG, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* mention_privateCommunity_blogEntry_mentionsView_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is a member</B></li>
	*<li><B>Step: testUser1 customize and add the Blogs widget</B></li>
	*<li><B>Step: testUser1 add an entry mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8348EB5B4DADBEC785257CAC004D50DA">TTT - @MENTIONS - 114 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY - PRIVATE COMMUNITY FORUM - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_blogEntry_mentionsView_member() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community with mentions to User 2 who is a member
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostWithMentions(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity, mentions);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInABlogEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* mention_privateCommunity_blogEntry_mentionsView_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is NOT a member</B></li>
	*<li><B>Step: testUser1 customize and add the Blogs widget</B></li>
	*<li><B>Step: testUser1 add an entry mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/51390F1F10231F0085257CAC004D50D9">TTT - @MENTIONS - 113 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY - PRIVATE COMMUNITY BLOG - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_blogEntry_mentionsView_nonMember() {
		
		/**
		 * User 2 in this case will be User 3 since User 3 is NOT a member of the private community
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community with mentions to User 2 who is NOT a member
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostWithMentions(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity, mentions);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser3, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInABlogEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText}, null, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* mention_privateCommunity_blogEntry_Comment_mentionsView_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is a member</B></li>
	*<li><B>Step: testUser1 customize and add the Blogs widget</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DC361AF7FA64F83085257CAC004D50D5">TTT - @MENTIONS - 134 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY COMMENT - PRIVATE COMMUNITY BLOG - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_blogEntry_Comment_mentionsView_member() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community and will now post a comment to the entry with mentions to User 2 who is a member
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndAddCommentWithMentions(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity, mentions);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInACommentOnABlogEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText, baseBlogPost.getContent()}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText, baseBlogPost.getContent()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* mention_privateCommunity_blogEntry_Comment_mentionsView_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is NOT a member</B></li>
	*<li><B>Step: testUser1 customize and add the Blogs widget</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E103859600C4F68385257CAC004D50D4">TTT - @MENTIONS - 133 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY COMMENT - PRIVATE COMMUNITY BLOG - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_blogEntry_Comment_mentionsView_nonMember() {
		
		/**
		 * User 2 in this case will be User 3 since User 3 is NOT a member of the private community
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a blog entry in the community and will now post a comment to the entry with mentions to User 2 who is NOT a member
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createBlogPostAndAddCommentWithMentions(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity, mentions);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser3, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInACommentOnABlogEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText, baseBlogPost.getContent()}, null, false);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);

		// Verify that the mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText, baseBlogPost.getContent()}, TEST_FILTERS, false);
				
		ui.endTest();
	}
}