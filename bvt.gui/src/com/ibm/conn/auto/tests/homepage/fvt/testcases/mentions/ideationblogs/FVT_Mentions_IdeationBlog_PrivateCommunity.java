package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.ideationblogs;

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

public class FVT_Mentions_IdeationBlog_PrivateCommunity extends SetUpMethodsFVT {
	
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
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* mention_privateCommunity_IdeationIdea_mentionsView_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is a member</B></li>
	*<li><B>Step: testUser1 customize and add the Ideation Blogs widget</B></li>
	*<li><B>Step: testUser1 add an idea mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B13D831534AFAB7485257CAC004E2645">TTT - @MENTIONS - 123 - MENTIONS DIRECTED TO YOU IN AN IDEATION BLOG IDEA - PRIVATE COMMUNITY IDEATION BLOG - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_IdeationIdea_mentionsView_member() {
		
		String testName = ui.startTest();
		
		// User 1 will now add an idea mentioning User 2 in the description to the community blog
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeationBlogIdeaWithMentions(testUser1, communityBlogsAPIUser1, mentions, baseBlogPost, restrictedCommunity);

		// User 2 logs in and goes to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
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
	* mention_privateCommunity_IdeationIdea_mentionsView_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is NOT a member</B></li>
	*<li><B>Step: testUser1 customize and add the Ideation Blogs widget</B></li>
	*<li><B>Step: testUser1 add an idea mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A27EE583CB96014C85257CAC004E2644">TTT - @MENTIONS - 122 - MENTIONS DIRECTED TO YOU IN AN IDEATION BLOG IDEA - PRIVATE COMMUNITY IDEATION BLOG - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_IdeationIdea_mentionsView_nonMember() {
		
		/**
		 * User 2 in this case will be User 3 since User 3 is NOT a member of the private community
		 */
		String testName = ui.startTest();
		
		// User 1 will now add an idea mentioning User 2 in the description to the community blog
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeationBlogIdeaWithMentions(testUser1, communityBlogsAPIUser1, mentions, baseBlogPost, restrictedCommunity);

		// User 2 logs in and goes to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser3, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInTheIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
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
	* mention_privateCommunity_IdeationIdea_Comment_mentionsView_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is a member</B></li>
	*<li><B>Step: testUser1 customize and add the Ideation Blogs widget</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/597AE1A14595FFD885257CAC005389FE">TTT - @MENTIONS - 143 - MENTIONS DIRECTED TO YOU IN AN IDEATION BLOG IDEA COMMENT - PRIVATE COMMUNITY IDEATION BLOG - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_IdeationIdea_Comment_mentionsView_member() {
		
		String testName = ui.startTest();
		
		// User 1 will now add an idea mentioning User 2 in a comment
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndAddCommentWithMention(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity, mentions);

		// User 2 logs in and goes to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInACommentOnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
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
	* mention_privateCommunity_IdeationIdea_Comment_mentionsView_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 go to a private community they own and testUser2 is a NOT member</B></li>
	*<li><B>Step: testUser1 customize and add the Ideation Blogs widget</B></li>
	*<li><B>Step: testUser1 add an idea</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Blogs</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3EA0F2849E9CEA085257CAC005389FD">TTT - @MENTIONS - 142 - MENTIONS DIRECTED TO YOU IN AN IDEATION BLOG IDEA COMMENT - PRIVATE COMMUNITY IDEATION BLOG - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mention_privateCommunity_IdeationIdea_Comment_mentionsView_nonMember() {
		
		/**
		 * User 2 in this case will be User 3 since User 3 is NOT a member of the private community
		 */
		String testName = ui.startTest();
		
		// User 1 will now add an idea mentioning User 2 in a comment
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndAddCommentWithMention(testUser1, communityBlogsAPIUser1, baseBlogPost, restrictedCommunity, mentions);

		// User 2 logs in and goes to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser3, false);
		
		// Create the news story to be verified
		String mentionsEvent = CommunityBlogNewsStories.getMentionedYouInACommentOnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
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