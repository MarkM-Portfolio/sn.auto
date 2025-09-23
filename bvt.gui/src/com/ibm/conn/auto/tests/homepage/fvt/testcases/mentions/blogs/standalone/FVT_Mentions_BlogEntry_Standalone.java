package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.blogs.standalone;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2016		                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_BlogEntry_Standalone extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs };

	private APIBlogsHandler blogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseBlog baseBlog;
	private Blog standaloneBlog;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);	
		
		blogsAPIUser1 = initialiseAPIBlogsHandlerUser(testUser1);

		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);

		// User 1 will now create a standalone blog
		baseBlog = BlogBaseBuilder.buildBaseBlog(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneBlog = BlogEvents.createBlog(testUser1, blogsAPIUser1, baseBlog);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
	
		// Delete the standalone blog created during the test
		blogsAPIUser1.deleteBlog(standaloneBlog);
	}
	
	/**
	* directedMentions_standaloneBlog_entry_mentionsView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a blog</B></li>
	*<li><B>Step: testUser1 add an entry mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All & Blogs</B></li>
	*<li><B>Verify: Verify that there is a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0877D7ECF246838485257CAC004D50D6">TTT - @MENTIONS - 110 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY - STANDALONE BLOG - ON PREM ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void directedMentions_standaloneBlog_entry_mentionsView(){

		ui.startTest();

		// User 1 will now add a blog entry to the blog - the event will mention User 2 in the event description
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BlogPost blogPost = BlogEvents.createBlogPostWithMention(testUser1, blogsAPIUser1, standaloneBlog, mentions);

		// User 2 logs in and goes to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = BlogNewsStories.getMentionedYouInABlogEntryNewsStory(ui, blogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
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
	* directedMentions_standaloneBlog_entryComment_mentionsView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a blog</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser1 add a comment mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All & Blogs</B></li>
	*<li><B>Verify: Verify that there is a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8F1AF98A671B202B85257CAC004D50D1">TTT - @MENTIONS - 130 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY COMMENT - STANDALONE BLOG - ON PREM ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void directedMentions_standaloneBlog_entryComment_mentionsView(){
		
		String testName = ui.startTest();

		// User 1 will now add an entry and a comment with a mention to the standalone blog
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogEvents.createBlogPostAndAddCommentWithMention(testUser1, blogsAPIUser1, baseBlogPost, standaloneBlog, mentions);

		// User 2 logs in and goes to Mentions
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionsEvent = BlogNewsStories.getMentionedYouInACommentOnABlogEntryNewsStory(ui, baseBlogPost.getTitle(), baseBlog.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsEvent, mentionsText}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionsEvent, mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}