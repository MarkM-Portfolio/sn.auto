package com.ibm.conn.auto.util.eventBuilder.blogs;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;

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
 * @author 	Patrick Doherty
 * Date:	14th March 2016
 */

public class BlogEvents {
	
	private static Logger log = LoggerFactory.getLogger(BlogEvents.class);
	
	/**
	 * Creates a new standalone blog
	 * 
	 * @param blogCreator - The User instance of the user who is creating the blog
	 * @param apiBlogCreator - The APIBlogsHandler instance of the user who is creating the blog
	 * @param baseBlog - The BaseBlog instance of the blog which is to be created
	 * @return blog - A Blog object
	 */
	public static Blog createBlog(User blogCreator, APIBlogsHandler apiBlogCreator, BaseBlog baseBlog){

		log.info("INFO: " + blogCreator.getDisplayName() + " creating Blog: " + baseBlog.getName());
		Blog blog = apiBlogCreator.createBlog(baseBlog);

		log.info("INFO: Verify that the new blog was created successfully");
		Assert.assertNotNull(blog, "ERROR: The blog was NOT created successfully and was returned as null");

		return blog;
	}
	
	/**
	 * Follows any standalone blog
	 * 
	 * @param blogToBeFollowed - The Blog instance of the Blog to be followed
	 * @param userFollowingBlog - The User instance of the user who is following the blog
	 * @param apiUserFollowingBlog - The APIBlogsHandler instance of the user who is following the blog
	 */
	public static void followBlog(Blog blogToBeFollowed, User userFollowingBlog, APIBlogsHandler apiUserFollowingBlog) {
		
		log.info("INFO: " + userFollowingBlog.getDisplayName() + " will follow the Blog: " + blogToBeFollowed.getTitle());
		apiUserFollowingBlog.createFollow(blogToBeFollowed);
	}

	/**
	 * Creates a new blog and then has the specified user follow that blog
	 * 
	 * @param blogCreator - The User instance of the user who is creating the blog
	 * @param apiBlogCreator - The APIBlogsHandler instance of the user who is creating the blog
	 * @param baseBlog - The BaseBlog instance of the blog which is to be created
	 * @param blogFollower - The User instance of the user who is following the blog
	 * @param apiBlogFollower - The APIBlogsHandler instance of the user who is following the blog
	 * @return blog - A Blog object
	 */
	public static Blog createBlogWithFollower(User blogCreator, APIBlogsHandler apiBlogCreator, BaseBlog baseBlog, User blogFollower, APIBlogsHandler apiBlogFollower){

		// Create the new blog
		Blog blog = createBlog(blogCreator, apiBlogCreator, baseBlog);

		// Follow the blog
		followBlog(blog, blogFollower, apiBlogFollower);
		
		return blog;
		
	}
	
	/**
	 * Creates a blog entry on a standalone blog
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating the blog entry
	 * @param apiBlogPostCreator - The APIBlogsHandler instance of the user who is creating the blog entry
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param blog - The Blog instace of the blog to which the entry will be added
	 * @return blogPost - A BaseBlog object
	 */
	public static BlogPost createBlogPost(User blogPostCreator, APIBlogsHandler apiBlogPostCreator, BaseBlogPost baseBlogPost, Blog blog){

		log.info("INFO: " + blogPostCreator.getDisplayName() + " creating Blog Post: " + baseBlogPost.getTitle());
		BlogPost blogPost = baseBlogPost.createAPI(apiBlogPostCreator, blog);

		log.info("INFO: Verify that the new blog entry was created successfully");
		Assert.assertNotNull(blogPost, "ERROR: The blog entry was NOT created successfully and was returned as null");

		return blogPost;

	}
	
	/**
	 * Likes / Recommends a blog entry
	 * 
	 * @param userLikingBlogPost - The User instance of the user who is liking / recommending the blog post
	 * @param apiUserLikingBlogPost - The APIBlogsHandler instance of the user who is liking / recommending the blog post
	 * @param blogPost - The BlogPost instance of the blog entry which is to be liked / recommended
	 */
	public static void likeBlogPost(User userLikingBlogPost, APIBlogsHandler apiUserLikingBlogPost, BlogPost blogPost){

		log.info("INFO: " + userLikingBlogPost.getDisplayName() + " will now like the blog entry with title: " + blogPost.getTitle());
		apiUserLikingBlogPost.like(blogPost);		
	}
	
	/**
	 * Creates a new blog entry and then likes that blog entry (using the same user for both actions) 
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating and liking the blog entry
	 * @param apiBlogPostCreator - The APIBlogsHandler instance of the user who is creating and liking the blog entry
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created and liked
	 * @param blog - The Blog instace of the blog to which the entry will be added
	 * @return blogPost - A BaseBlog object
	 */
	public static BlogPost createBlogPostAndLike(User blogPostCreator, APIBlogsHandler apiBlogPostCreator, BaseBlogPost baseBlogPost, Blog blog){

		// Create the blog entry on the standalone blog
		BlogPost blogPost = createBlogPost(blogPostCreator, apiBlogPostCreator, baseBlogPost, blog);
		
		// Like / Recommend the blog entry
		likeBlogPost(blogPostCreator, apiBlogPostCreator, blogPost);
		
		return blogPost;
	}
	
	/**
	 * Edits the description of a blog entry
	 * 
	 * @param userEditingBlogPost - The User instance of the user who is editing the blog entry
	 * @param apiUserEditingBlogPost - The APIBlogsHandler instance of the user who is editing the blog entry
	 * @param blogPostToBeUpdated - The BlogPost instance of the blog entry to be updated
	 * @param newDescription - A String object containing the content of the new description to which the entry description will be edited
	 * @return - A BlogPost object
	 */
	public static BlogPost editBlogPostDescription(User userEditingBlogPost, APIBlogsHandler apiUserEditingBlogPost, BlogPost blogPostToBeUpdated, String newDescription) {

		log.info("INFO: " + userEditingBlogPost.getDisplayName() + " will now change the description of the blog entry '" + blogPostToBeUpdated.getTitle() + "' to be: " + newDescription);
		apiUserEditingBlogPost.editPost(blogPostToBeUpdated, newDescription);

		return blogPostToBeUpdated;
	}
	
	/**
	 * Creates a new blog entry and then edits the description of that entry
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating and editing the blog entry
	 * @param apiBlogPostCreator - The APIBlogsHandler instance of the user who is creating and editing the blog entry
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created and edited
	 * @param blog - The Blog instance of the blog to which the entry will be added
	 * @param newDescription - A String object containing the content of the new description to which the entry description will be edited
	 * @return blogPost - A BlogPost object
	 */
	public static BlogPost createBlogPostAndEditDescription(User blogPostCreator, APIBlogsHandler apiBlogPostCreator, BaseBlogPost baseBlogPost, Blog blog, String newDescription){

		// Create the new blog entry
		BlogPost blogPost = createBlogPost(blogPostCreator, apiBlogPostCreator, baseBlogPost, blog);

		// Edit the description of the blog entry
		return editBlogPostDescription(blogPostCreator, apiBlogPostCreator, blogPost, newDescription);
	}
	
	/**
	 * Posts a comment to a blog entry
	 * 
	 * @param userCommentingOnEntry - The User instance of the user who is creating the blog entry comment
	 * @param apiUserCommentingOnEntry - The APIBlogsHandler instance of the user who is creating the blog entry comment
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry to which the comment is to be created
	 * @param baseBlogComment - A BaseBlogComment instance of the blog comment which will be added to the blog entry
	 * @return comment - A BlogComment object
	 */
	public static BlogComment createBlogPostComment(User userCommentingOnEntry, APIBlogsHandler apiUserCommentingOnEntry, BlogPost blogPost, BaseBlogComment baseBlogComment){

		log.info("INFO: " + userCommentingOnEntry.getDisplayName() + " will now add a comment to the blog entry with title: " + blogPost.getTitle());
		BlogComment comment = apiUserCommentingOnEntry.createBlogComment(baseBlogComment.getContent().trim(), blogPost);

		log.info("INFO: Verify that the new blog entry comment was created successfully");
		Assert.assertNotNull(comment, "ERROR: The blog entry comment was NOT created successfully and was returned as null");

		return comment;
	}
	
	/**
	 * Creates a blog post and posts a comment to it (using the same user for all actions)
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating the blog entry
	 * @param apiBlogPostCreator - The APIBlogsHandler instance of the user who is creating the blog entry
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param blog - The Blog instance of the blog to which the entry will be added
	 * @param baseBlogComment - A BaseBlogComment instance of the blog comment which will be posted to the blog entry
	 * @return - A BlogComment object
	 */
	public static BlogComment createBlogPostAndAddComment(User blogPostCreator, APIBlogsHandler apiBlogPostCreator, BaseBlogPost baseBlogPost, Blog blog, BaseBlogComment baseBlogComment) {
		
		// Create the blog entry
		BlogPost blogPost = createBlogPost(blogPostCreator, apiBlogPostCreator, baseBlogPost, blog);

		// Post the comment to the blog entry
		return createBlogPostComment(blogPostCreator, apiBlogPostCreator, blogPost, baseBlogComment);
	}
	
	/**
	 * Likes / Recommends a comment posted to a blog entry
	 * 
	 * @param commentRecommender - The User instance of the user who is liking the blog entry comment
	 * @param apiCommentRecommender - The APIBlogsHandler instance of the user who is liking the blog entry comment
	 * @param blogComment - The BlogComment instance which will be liked
	 */
	public static void likeBlogPostComment(User commentRecommender, APIBlogsHandler apiCommentRecommender, BlogComment blogComment){

		log.info("INFO: " + commentRecommender.getDisplayName() + " will now like the blog entry comment with content: " + blogComment.getContent().trim());
		apiCommentRecommender.like(blogComment);
	}
	
	/**
	 * Creates a comment on a blog entry and then likes the comment (using the same user for all actions)
	 * 
	 * @param blogPostCommentCreator - The User instance of the user who is liking the blog entry comment
	 * @param apiBlogPostCommentCreator - The APIBlogsHandler instance of the user who is liking the blog entry comment
	 * @param blogPost - The BlogPost instance to which the comment will be added
	 * @param baseBlogComment - The BaseBlogComment instance which will be added to the blog entry
	 * @return - A BlogComment object
	 */
	public static BlogComment createAndLikeBlogPostComment(User blogPostCommentCreator, APIBlogsHandler apiBlogPostCommentCreator, BlogPost blogPost, BaseBlogComment baseBlogComment){

		// Create the comment on the blog entry
		BlogComment blogComment = createBlogPostComment(blogPostCommentCreator, apiBlogPostCommentCreator, blogPost, baseBlogComment);
		
		// Like / Recommend the comment posted to the blog entry
		likeBlogPostComment(blogPostCommentCreator, apiBlogPostCommentCreator, blogComment);
		
		return blogComment;
	}
	
	/**
	 * Creates a new blog entry, adds a comment to that entry and then likes / recommends the comment (using the same user for all actions)
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating the blog entry, posting the blog entry comment and liking the blog entry comment
	 * @param apiBlogPostCreator - The APIBlogsHandler instance of the who is creating the blog entry, posting the blog entry comment and liking the blog entry comment
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param blog - The Blog instace of the blog to which the entry will be added
	 * @param baseBlogComment - The BaseBlogComment instance of the comment to be posted to the blog entry
	 * @return - A BlogComment object
	 */
	public static BlogComment createBlogPostAndAddCommentAndLikeComment(User blogPostCreator, APIBlogsHandler apiBlogPostCreator, BaseBlogPost baseBlogPost, Blog blog, BaseBlogComment baseBlogComment){
		
		// Create the blog entry
		BlogPost blogPost = createBlogPost(blogPostCreator, apiBlogPostCreator, baseBlogPost, blog);
		
		// Post the comment to the blog entry and then like / recommend the blog comment
		return createAndLikeBlogPostComment(blogPostCreator, apiBlogPostCreator, blogPost, baseBlogComment);
	}
	
	/**
	 * UI method which navigates to the specified blog entry in Blogs UI
	 * 
	 * @param ui - The HomepageUI instance used to invoke the clickLinkWait and fluentWaitTextPresent and methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param baseBlog - The BaseBlog instance of the blog which contains the blog entry to which the user is navigating
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry to which the user is navigating
	 */
	public static void navigateToStandaloneBlogPost(HomepageUI ui, RCLocationExecutor driver, BaseBlog baseBlog, BaseBlogPost baseBlogPost){

		// Navigate to the blog in Blogs UI
		Assert.assertTrue(navigateToBlog(ui, driver, baseBlog), 
											"ERROR: Failed to navigate to the blog in Blogs UI - a clickable link for the blog was NOT found in Blogs UI");
		
		log.info("INFO: Now clicking on the link to navigate to the blog post with title: " + baseBlogPost.getTitle());
		ui.clickLinkWait("link=" + baseBlogPost.getTitle());
		
		// Wait for Blogs UI to load
		waitForBlogsUIPageToLoad(ui);
	}
	
	/**
	 * UI method which posts a trackback method to a blog entry
	 * 
	 * @param ui - The HomepageUI instance used to invoke the clickLinkWait methods
	 * @param driver - The RCLocationExecutor instance used to invoke methods during the process
	 * @param trackbackUser - The User instance of the user posting the trackback comment to the blog entry
	 * @param trackbackComment - The String content of the trackback comment to be posted to the blog entry
	 */
	public static void addTrackbackComment(HomepageUI ui, RCLocationExecutor driver, User trackbackUser, String trackbackComment) {

		log.info("INFO: " + trackbackUser.getDisplayName() + " adding a comment to the Blog Post");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		log.info("INFO: Now switching focus to the comment input field in order to enter the trackback comment on this blog entry");
		switchToBlogEntryCommentIFrame(ui);
		
		log.info("INFO: Entering the trackback comment content: " + trackbackComment);
		ui.typeStringWithNoDelay(trackbackComment);
		driver.switchToFrame().returnToTopFrame();
		
		log.info("INFO: " + trackbackUser.getDisplayName() + " checking the trackback checkbox");
		ui.clickLinkWait(BlogsUIConstants.BlogCommentTrackbackCheckBox);
		
		log.info("INFO: " + trackbackUser.getDisplayName() + " is now submitting the trackback comment");
		ui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
		log.info("INFO: Verify that the newly posted trackback comment appears in the UI");
		ui.fluentWaitTextPresent(trackbackComment);
	}
	
	/**
	 * Logs into Blogs UI, navigates to the specified blog entry and posts a trackback comment on the blog before returning to home and logging out again
	 * 
	 * @param ui - The HomepageUI instance used to invoke all relevant methods during the process
	 * @param driver - The RCLocationExecutor instance used to invoke all relevant methods during the process
	 * @param userToLogin - The User to be logged in during the process
	 * @param baseBlog - The BaseBlog instance of the blog which contains blog entry to which the user is navigating
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry to which the user is navigating
	 * @param trackbackComment - The content of the trackback comment to be posted to the blog entry
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndAddTrackbackComment(HomepageUI ui, RCLocationExecutor driver, User userToLogin, BaseBlog baseBlog, BaseBlogPost baseBlogPost, 
													String trackbackComment, boolean preserveInstance) {
		// Log into Blogs UI
		LoginEvents.loginToBlogs(ui, userToLogin, preserveInstance);
		
		// Navigate to the blog entry in the UI
		navigateToStandaloneBlogPost(ui, driver, baseBlog, baseBlogPost);
		
		// Post the trackback comment to the blog entry
		addTrackbackComment(ui, driver, userToLogin, trackbackComment);
		
		// Ensure that the Home icon is visible before attempting to click on it.
		UIEvents.resetASToTop(ui);
		
		// Return to home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
	}
	
	/**
	 * Switches to the comment box iFrame for a blog entry comment (currently, this corresponds to the 1st iFrame found on the page in Blogs UI)
	 * 
	 * @param ui - The HomepageUI instance to invoke the switchToCKEditorSUFrame() method
	 */
	public static void switchToBlogEntryCommentIFrame(HomepageUI ui) {
		
		log.info("INFO: Attempting to switch to and click into the comment box iFrame for a blog entry comment");
		ui.switchToCKEditorSUFrame(1);
	}
	
	/**
	 * Opens the EE for the specified blogs news story and posts a comment with multiple mentions to the news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param blogNewsStory - The String content of the news story to be used to open the EE
	 * @param usersToBeMentioned - The Array of Mentions instances of all users to be mentioned in the comment
	 */
	public static void openEEAndPostCommentWithMultipleMentions(HomepageUI ui, RCLocationExecutor driver, String blogNewsStory, Mentions[] usersToBeMentioned) {
		
		// Open the EE for the blogs news story
		UIEvents.openEE(ui, blogNewsStory);
		
		// Switch focus to the comments frame in the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, true);
		
		String multipleMentionsText = "";
		for(Mentions mentions : usersToBeMentioned) {
			// Append the current user to be mentioned to the multiple mentions text (used for validations later)
			multipleMentionsText += "@" + mentions.getUserToMention().getDisplayName() + " ";
			
			// Enter the mentions to the current user into the EE replies input field
			UIEvents.typeMentionsOrPartialMentions(ui, mentions, mentions.getUserToMention().getDisplayName().length());
			
			// Wait for the EE typeahead menu to load
			UIEvents.waitForEETypeaheadMenuToLoad(ui);
			
			// Get the list of menu items from the typeahead menu and select the appropriate user
			UIEvents.getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
			
			// Switch focus back to the EE comments frame
			UIEvents.switchToEECommentOrRepliesFrame(ui, true);
			
			// Verify that the link to the mentioned user is displayed
			UIEvents.verifyMentionsLinkIsDisplayed(ui, mentions.getUserToMention().getDisplayName());
			
			// Add a space at the end of the mentions link
			UIEvents.typeStringWithNoDelay(ui, " ");
		}
		// Switch focus back to the EE frame in order to post the mentions comment
		UIEvents.switchToEEFrame(ui);
		
		// Post the reply with multiple mentions now that all users have been mentioned
		UIEvents.postEECommentOrReply(ui);
		
		log.info("INFO: Verify that the comment with multiple mentions is now displayed in the EE");
		multipleMentionsText = multipleMentionsText.trim();
		Assert.assertTrue(ui.fluentWaitTextPresent(multipleMentionsText), 
							"ERROR: The comment with multiple mentions was NOT displayed in the EE");
	}
	
	/**
	 * Opens the EE for the specified blogs news story and posts a comment to the news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param blogNewsStory - The String content of the news story to be used to open the EE
	 * @param commentToBePosted - The String content of the comment to be posted using the EE
	 */
	public static void openEEAndPostComment(HomepageUI ui, String blogNewsStory, String commentToBePosted) {
		
		// Open the EE for the blogs news story
		UIEvents.openEE(ui, blogNewsStory);
		
		// Switch focus to the comments frame in the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, true);
		
		// Enter the comment content into the comment input field
		UIEvents.typeStringWithNoDelay(ui, commentToBePosted);
		
		// Switch focus back to the EE frame in order to post the comment
		UIEvents.switchToEEFrame(ui);
		
		// Post the comment using the 'Post' link in the EE
		UIEvents.postEECommentOrReply(ui);
		
		log.info("INFO: Verify that the comment is now displayed in the EE with content: " + commentToBePosted);
		Assert.assertTrue(ui.fluentWaitTextPresent(commentToBePosted), 
							"ERROR: The comment was NOT displayed in the EE with content: " + commentToBePosted);
	}
	
	/**
	 * Logs in to the specified blog in Blogs UI and updates the name of the blog
	 * 
	 * @param baseBlogToBeEdited - The BaseBlog instance of the blog whose name is to be update
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userToLogin - The User instance of the user to be logged in
	 * @param editedBlogName - The String content of the name to which the blog will be updated
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndEditBlogName(BaseBlog baseBlogToBeEdited, HomepageUI ui, RCLocationExecutor driver, User userToLogin, String editedBlogName, boolean preserveInstance) {
		
		// Log in to Blogs UI
		LoginEvents.loginToBlogs(ui, userToLogin, preserveInstance);
		
		// Navigate to the blog in Blogs UI
		Assert.assertTrue(navigateToBlog(ui, driver, baseBlogToBeEdited), 
							"ERROR: Failed to navigate to the blog in Blogs UI - a clickable link for the blog was NOT found in Blogs UI");
		
		log.info("INFO: Now clicking on the 'Manage Blog' button in Blogs UI");
		ui.clickLinkWait(BlogsUIConstants.blogsSettings);
		
		// Wait for Blogs Settings UI screen to load
		waitForBlogsSettingsUIPageToLoad(ui);
		
		// Edit / update the blog name in Blogs Settings UI
		editBlogNameUsingUI(ui, driver, editedBlogName);
		
		// Return to the home and logout
		LoginEvents.gotoHomeAndLogout(ui);
	}
	
	/**
	 * Edits / updates the current blog name for the blog displayed in Blog Settings UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param editedBlogName - The String content of the name to which the blog will be updated
	 */
	public static void editBlogNameUsingUI(HomepageUI ui, RCLocationExecutor driver, String editedBlogName) {
		
		log.info("INFO: Retrieving the Element instance corresponding to the 'Name' input field");
		Element blogNameInputField = driver.getSingleElement(BlogsUIConstants.BlogsNameField);
		
		log.info("INFO: Now clicking into the 'Name' input field");
		ui.clickElement(blogNameInputField);
		
		log.info("INFO: Clear all existing text in the 'Name' input field");
		blogNameInputField.clear();
		
		log.info("INFO: Now entering the new name to be assigned to the community blog");
		UIEvents.typeStringWithNoDelay(ui, editedBlogName);
		
		log.info("INFO: Now clicking on the 'Update Blog Settings' button to confirm and save the changes made to the community blog name");
		ui.clickLinkWait(BlogsUIConstants.blogsUpdateSettings);
		
		log.info("INFO: Verify that the success message for updating the blog settings is displayed in Community Blogs UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().BlogsSettingsChangesSaved), 
							"ERROR: The success message was NOT displayed in Community Blogs UI after saving updated blog settings");
	}
	
	/**
	 * Waits for the Blog Settings UI screen to load
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void waitForBlogsSettingsUIPageToLoad(HomepageUI ui) {
		
		log.info("INFO: Waiting for the Blog Settings UI screen to load");
		ui.fluentWaitPresent(BlogsUIConstants.BlogsNameField);
		ui.fluentWaitPresent(BlogsUIConstants.blogsUpdateSettings);
	}
	
	/**
	 * Creates a blog entry with a mention to a user
	 * 
	 * @param userCreatingEntry - The User instance of the user who is creating the blog entry with a mention
	 * @param apiUserCreatingEntry - The APIBlogsHandler instance of the user who is creating the blog entry with a mention
	 * @param blog - The Blog instance of the blog to which the entry will be added
	 * @param mentions - The Mentions instance of the user to be mentioned in the blog entry description
	 * @return blogPost - A BlogPost object
	 */
	public static BlogPost createBlogPostWithMention(User userCreatingEntry, APIBlogsHandler apiUserCreatingEntry, Blog blog, Mentions mentions){

		log.info("INFO: " + userCreatingEntry.getDisplayName() + " creating a blog entry with a mentions to " + mentions.getUserToMention().getDisplayName() + " using API method");
		BlogPost blogPost = apiUserCreatingEntry.addMention_BlogEntryAPI(blog, mentions);

		log.info("INFO: Verify that the new blog entry with the mention was created successfully");
		Assert.assertNotNull(blogPost, "ERROR: The blog entry with the mention was NOT created successfully and was returned as null");

		return blogPost;
	}
	
	/**
	 * Creates a blog entry comment with a mention to a user
	 * 
	 * @param userCreatingComment - The User instance of the user who is creating the blog entry comment with a mention
	 * @param apiUserCreatingComment - The APIBlogsHandler instance of the user who is creating the blog entry comment with a mention
	 * @param blogPost - The BlogPost instance of the blog to which the entry will be added
	 * @param mentions - The Mentions instance of the user to be mentioned in the blog entry description
	 * @return blogComment - A BlogComment object
	 */
	public static BlogComment createBlogPostCommentWithMention(User userCreatingComment, APIBlogsHandler apiUserCreatingComment, BlogPost blogPost, Mentions mentions){

		log.info("INFO: " + userCreatingComment.getDisplayName() + " will create a blog entry comment with a mentions to " + mentions.getUserToMention().getDisplayName() + " using API method");
		BlogComment blogComment = apiUserCreatingComment.addBlogCommentMentionAPI(blogPost, mentions);

		log.info("INFO: Verify that the new blog entry comment with the mention was created successfully");
		Assert.assertNotNull(blogComment, "ERROR: The blog entry comment with the mention was NOT created successfully and was returned as null");

		return blogComment;
	}
	
	/**
	 * 
	 * @param userCreatingEntry - The User instance of the user who is creating the blog entry comment with a mention
	 * @param apiUserCreatingEntry - The APIBlogsHandler instance of the user who is creating the blog entry comment with a mention
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param blog - The Blog instace of the blog to which the entry will be added
	 * @param mentions - The Mentions instance of the user to be mentioned in the blog entry description
	 * @return blogPost - A BlogPost object
	 */
	public static BlogPost createBlogPostAndAddCommentWithMention(User userCreatingEntry, APIBlogsHandler apiUserCreatingEntry, BaseBlogPost baseBlogPost, Blog blog, Mentions mentions){
		
		// Create the entry to be commented on
		BlogPost blogPost = createBlogPost(userCreatingEntry, apiUserCreatingEntry, baseBlogPost, blog);
		
		// Post the comment with mentions to the entry
		createBlogPostCommentWithMention(userCreatingEntry, apiUserCreatingEntry, blogPost, mentions);
		
		return blogPost;
	}
	
	/**
	 * Deletes a comment posted to a blog entry
	 * 
	 * @param commentToBeDeleted - The BlogComment instance of the blog comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the blog comment
	 * @param apiUserDeletingComment - The APICommunityBlogsHandler instance of the user deleting the blog comment
	 * @return - True if all actions are completed successfully
	 */
	public static boolean deleteComment(BlogComment commentToBeDeleted, User userDeletingComment, APIBlogsHandler apiUserDeletingComment) {
		
		log.info("INFO: " + userDeletingComment.getDisplayName() + " will now delete the blog comment with content: " + commentToBeDeleted.getContent());
		boolean deleted = apiUserDeletingComment.deleteComment(commentToBeDeleted);
		
		log.info("INFO: Verify that the blog comment was deleted successfully");
		Assert.assertTrue(deleted, "ERROR: The blog comment was NOT deleted as expected using the API");
		
		return true;
	}
	
	/**
	 * Waits for the Blogs UI screen to load
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void waitForBlogsUIPageToLoad(HomepageUI ui) {
		
		log.info("INFO: Wait for the Blogs UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForBlogEntries);
	}
	
	/**
	 * Navigates to the specified blog in Blogs UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param baseBlog - The BaseBlog instance of the blog to navigate to in the UI
	 * @return - True if all actions are successful, false otherwise
	 */
	private static boolean navigateToBlog(HomepageUI ui, RCLocationExecutor driver, BaseBlog baseBlog) {
		
		log.info("INFO: Now attempting to find the blog title link for the blog with title: " + baseBlog.getName());
		/**
		 * Retrieve all clickable blog title elements and determine which element is the one we need to click
		 * 
		 * For some blogs with long titles, the UI will shorten the name to "FVT_Blog..." instead of, say, "FVT_Blog_Standalone"
		 * This shortening of the title and using the "..." string happens both in the UI AND in the CSS selector for that blog
		 * 
		 * Therefore the only reliable way to click on the correct link for all possible blog titles is to determine 
		 * which link contains a substring of the blog title to be clicked.
		 */
		List<Element> listOfBlogsLinks = driver.getElements(BlogsUIConstants.BlogsUIBlogTitleLink);
		int index = 0;
		boolean foundBlogTitleLink = false;
		Element blogTitleLink = null;
		while(index < listOfBlogsLinks.size() && foundBlogTitleLink == false) {
			Element currentBlogTitleLink = listOfBlogsLinks.get(index);
			
			// Retrieve the text for this element and remove any unwanted characters
			String currentBlogTitleLinkText = currentBlogTitleLink.getText();
			currentBlogTitleLinkText = currentBlogTitleLinkText.replace("...", "").trim();
			
			if(baseBlog.getName().trim().indexOf(currentBlogTitleLinkText) > -1) {
				log.info("INFO: Found clickable title link to the blog with title: " + baseBlog.getName());
				blogTitleLink = currentBlogTitleLink;
				foundBlogTitleLink = true;
			}
			index ++;
		}
		
		if(foundBlogTitleLink == false) {
			log.info("ERROR: Could not find a clickable link to the blog with title: " + baseBlog.getName());
			return false;
		}
		
		log.info("INFO: Now clicking on the link to navigate to the blog with title: " + baseBlog.getName());
		ui.clickElement(blogTitleLink);
		
		// Wait for Blogs UI to load
		waitForBlogsUIPageToLoad(ui);
		
		return true;
	}
}