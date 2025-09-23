package com.ibm.conn.auto.util.eventBuilder.community;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.blogs.BlogEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityBlogEvents {
	
	private static Logger log = LoggerFactory.getLogger(CommunityBlogEvents.class);
	
	/**
	 * Creates a blog entry in a community
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating the blog entry in the community blog
	 * @param apiBlogPostCreator - The APICommunityBlogsHandler instance of the user who is creating the blog entry in the community blog
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param community - The Community instance of the community to which the blog entry will be added
	 * @return blogPost - A BlogPost object
	 */
	public static BlogPost createBlogPost(User blogPostCreator, APICommunityBlogsHandler apiBlogPostCreator, BaseBlogPost baseBlogPost, Community community){

		log.info("INFO: " + blogPostCreator.getDisplayName() + " will now add a blog entry to community with title: " + community.getTitle());
		BlogPost blogPost = apiBlogPostCreator.createBlogEntry(community, baseBlogPost);	

		log.info("INFO: Verify that the new blog entry was created successfully");
		Assert.assertNotNull(blogPost, "ERROR: The blog entry was NOT created successfully and was returned as null");

		return blogPost;
	}

	/**
	 * Creates a blog entry on a community blog and then likes the blog entry (using the same user)
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating the blog entry in the community blog
	 * @param apiBlogPostCreator - The APICommunityBlogsHandler instance of the user who is creating the blog entry in the community blog
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param community - The Community instance of the community to which the blog entry will be added
	 * @return blogPost - A BlogPost object
	 */
	public static BlogPost createBlogPostAndLikeBlogPost(Community community, BaseBlogPost baseBlogPost, User blogPostCreator, APICommunityBlogsHandler apiBlogPostCreator) {

		// Create a new blog entry in the community
		BlogPost blogPost = createBlogPost(blogPostCreator, apiBlogPostCreator, baseBlogPost, community);
		
		// Like / recommend the community blog entry
		return likeOrVote(blogPost, blogPostCreator, apiBlogPostCreator);
	}
	
	/**
	 * Creates a blog entry for a community blog and adds a comment to that blog entry
	 * 
	 * @param userCreatingEntry - The User instance of the user who is creating the blog entry and comment in the community blog
	 * @param apiUserCreatingEntry - The APICommunityBlogsHandler instance of the user who is creating the blog entry in the community blog
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param community - The Community instance of the community to which the blog entry and comment will be added
	 * @param baseBlogComment - A BaseBlogComment instance of the comment which is to be created
	 * @return - A BlogComment object
	 */
	public static BlogComment createBlogPostAndAddComment(Community community, BaseBlogPost baseBlogPost, BaseBlogComment baseBlogComment, User userCreatingEntry, APICommunityBlogsHandler apiUserCreatingEntry) {
		
		// Create a new blog entry
		BlogPost blogPost = createBlogPost(userCreatingEntry, apiUserCreatingEntry, baseBlogPost, community);
		
		// Add the comment to the community blog entry
		return createComment(blogPost, baseBlogComment, userCreatingEntry, apiUserCreatingEntry);
	}
	
	/**
	 * Creates a blog entry for a community blog, adds a comment to that blog entry and then edits / updates the comment
	 * 
	 * @param userCreatingEntry - The User instance of the user who is creating the blog entry and comment in the community blog
	 * @param apiUserCreatingEntry - The APICommunityBlogsHandler instance of the user who is creating the blog entry in the community blog
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param community - The Community instance of the community to which the blog entry and comment will be added
	 * @param baseBlogComment - A BaseBlogComment instance of the comment which is to be created and updated
	 * @param baseBlogCommentEdit - A BaseBlogComment instance of the content for the updated comment
	 * @return - A BlogComment object
	 */
	public static BlogComment createBlogPostAndAddCommentAndEditComment(Community community, BaseBlogPost baseBlogPost, BaseBlogComment baseBlogComment, BaseBlogComment baseBlogCommentEdit, User userCreatingEntry, APICommunityBlogsHandler apiUserCreatingEntry) {
		
		// Create a new blog entry
		BlogPost blogPost = createBlogPost(userCreatingEntry, apiUserCreatingEntry, baseBlogPost, community);
		
		// Add the comment to the community blog entry
		return CommunityBlogEvents.createCommentAndEditComment(blogPost, baseBlogComment, baseBlogCommentEdit, userCreatingEntry, apiUserCreatingEntry);
	}
	
	/**
	 * Creates a blog entry for a community blog, adds a comment to that entry and then likes / recommends the comment
	 * 
	 * @param blogPostCreator - The User instance of the user who is creating the blog entry and comment in the community blog
	 * @param apiBlogPostCreator - The APICommunityBlogsHandler instance of the user who is creating the blog entry in the community blog
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param community - The Community instance of the community to which the blog entry and comment will be added
	 * @param baseBlogComment - A BaseBlogComment instance of the comment which is to be created
	 * @return blogComment - A BlogComment object
	 */
	public static BlogComment createBlogPostAndAddCommentAndLikeComment(Community community, BaseBlogPost baseBlogPost, BaseBlogComment baseBlogComment, User blogPostCreator, APICommunityBlogsHandler apiBlogPostCreator) {
		
		// Create the blog entry and add a comment to the blog entry
		BlogComment blogComment = createBlogPostAndAddComment(community, baseBlogPost, baseBlogComment, blogPostCreator, apiBlogPostCreator);
		
		// Like / recommend the blog entry
		return likeComment(blogComment, blogPostCreator, apiBlogPostCreator);	
	}

	/**
	 * UI method which navigates to the specified blog entry in Blogs UI
	 * 
	 * @param ui - The HomepageUI instance used to invoke the clickLinkWait and fluentWaitTextPresent and methods
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry to which the user is navigating
	 */
	public static void navigateToCommunityBlogPost(HomepageUI ui, BaseBlogPost baseBlogPost){

		// Wait for Blogs UI to load
		waitForCommunityBlogsUIPageToLoad(ui);
		
		log.info("INFO: Accessing the blog post: " + baseBlogPost.getTitle());
		ui.clickLinkWait("link=" + baseBlogPost.getTitle());
		
		// Wait for Blogs UI to load
		waitForCommunityBlogsUIPageToLoad(ui);
	}
	
	/**
	 * Logs in, navigates to the community blog and then posts a trackback comment on that blog
	 * 
	 * @param ui - The HomepageUI instance used to invoke all relevant methods during the process
	 * @param driver - The RCLocationExecutor instance used to invoke all relevant methods during the process
	 * @param userToLogin - The User instance of the user to be logged into Communities UI
	 * @param baseCommunity - The BaseCommunity instance of the community to be navigated to in the UI
	 * @param communityAPIUser - The APICommunitiesHandler instance of the user to be logged into Communities UI
	 * @param community - The Community instance of the community to be navigated to in the UI
	 * @param uiCo - The CommunitiesUI instance used to invoke all relevant methods during the process
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry to be navigated to in the UI
	 * @param trackbackComment - The comment content of the trackback comment to be posted to the blog entry
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndAddCommunityTrackbackComment(HomepageUI ui, RCLocationExecutor driver, User userToLogin, BaseCommunity baseCommunity, APICommunitiesHandler communityAPIUser, Community community, CommunitiesUI uiCo, BaseBlogPost baseBlogPost, 
																String trackbackComment, boolean preserveInstance) {
		// Log into Communities UI and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userToLogin, communityAPIUser, preserveInstance);
		
		// Navigate to blogs
		selectBlogFromLeftNavigationMenu(uiCo);
			
		// Navigate to the blog entry in the UI
		navigateToCommunityBlogPost(ui, baseBlogPost);
			
		// Post the trackback comment to the blog entry
		BlogEvents.addTrackbackComment(ui, driver, userToLogin, trackbackComment);
		
		// Ensure that the Home icon is visible before attempting to click on it.
		UIEvents.resetASToTop(ui);
			
		// Return to home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);	
	}
	
	/**
	 * Edits / updates a blog entry description or an ideation blog idea
	 * 
	 * @param blogPost - The BlogPost instance of the blog entry / idea to be updated
	 * @param newDescription - The new description to be set to the blog entry / idea
	 * @param userUpdatingEntry - The User instance of the user updating the blog entry / idea
	 * @param apiUserUpdatingEntry - The APICommunityBlogsHandler instance of the user updating the blog entry / idea
	 * @return - The updated BlogPost instance
	 */
	public static BlogPost editDescription(BlogPost blogPost, String newDescription, User userUpdatingEntry, APICommunityBlogsHandler apiUserUpdatingEntry) {
		
		log.info("INFO: " + userUpdatingEntry.getDisplayName() + " will now update the description for the blog entry / idea with title: " + blogPost.getTitle());
		BlogPost updatedBlogPost = apiUserUpdatingEntry.editDescription(blogPost, newDescription);
		
		log.info("INFO: Verify that the blog entry / idea description was updated successfully");
		Assert.assertTrue(updatedBlogPost.getContent().equals(newDescription),
							"ERROR: The blog entry / idea description was not updated and was instead returned as: " + updatedBlogPost.getContent());
		return updatedBlogPost;
	}
	
	/**
	 * Adds a new blog entry and then edits / updates the description for that entry
	 * 
	 * @param community - The Community instance of the community in which the blog entry will be created
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry which is to be created
	 * @param newDescription - The new description to be set to the blog entry after updating it
	 * @param blogPostCreator - The User instance of the user creating and updating the blog entry
	 * @param apiBlogPostCreator - The APICommunityBlogsHandler instance of the user creating and updating the blog entry
	 * @return - The updated BlogPost instance
	 */
	public static BlogPost createBlogPostAndEditDescription(Community community, BaseBlogPost baseBlogPost, String newDescription, User blogPostCreator, APICommunityBlogsHandler apiBlogPostCreator) {
		
		// Create a new blog entry
		BlogPost blogPost = createBlogPost(blogPostCreator, apiBlogPostCreator, baseBlogPost, community);
		
		// Edit the blog entry description
		return editDescription(blogPost, newDescription, blogPostCreator, apiBlogPostCreator);
	}
	
	/**
	 * Posts a comment to a blog entry / idea
	 * 
	 * @param blogPost - The BlogPost instance of the blog entry / idea to which the comment will be posted
	 * @param baseBlogComment - The BaseBlogComment instance of the comment to be posted to the blog entry / idea
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APICommunityBlogsHandler instance of the user posting the comment
	 * @return - The BlogComment instance of the posted comment
	 */
	public static BlogComment createComment(BlogPost blogPost, BaseBlogComment baseBlogComment, User userPostingComment, APICommunityBlogsHandler apiUserPostingComment) {
		
		// Create the BlogComment instance of the comment to be posted
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent().trim(), blogPost.toEntry());
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now post a comment to the blog entry / idea with title: " + blogPost.getTitle());
		BlogComment postedComment = apiUserPostingComment.createComment(blogPost, blogComment);
		
		log.info("INFO: Verify that the comment was posted successfully");
		Assert.assertNotNull(postedComment, "ERROR: The comment did NOT post correctly and was returned as null");
		
		return postedComment;
	}
	
	/**
	 * Likes / recommends a blog entry / votes for an ideation blog idea
	 * 
	 * @param blogEntryToBeLiked - The BlogPost instance of the blog entry to be liked / idea to be voted for
	 * @param userLikingEntry - The User instance of the user to like the blog entry / vote for the idea
	 * @param apiUserLikingEntry - The APICommunityBlogsHandler instance of the user to like the blog entry / vote for the idea
	 * @return - The BlogPost instance of the liked entry / voted for idea
	 */
	public static BlogPost likeOrVote(BlogPost blogEntryToBeLiked, User userLikingEntry, APICommunityBlogsHandler apiUserLikingEntry) {
		
		log.info("INFO: " + userLikingEntry.getDisplayName() + " will now like the blog entry / vote for the idea with title: " + blogEntryToBeLiked.getTitle());
		BlogPost likedBlogPost = apiUserLikingEntry.likeOrVote(blogEntryToBeLiked);
		
		log.info("INFO: Verify that the blog entry was liked / idea was voted for successfully");
		Assert.assertNotNull(likedBlogPost, "ERROR: The blog entry was NOT liked / idea was NOT voted for successfully and was returned as null");
		
		return likedBlogPost;
	}
	
	/**
	 * Unlikes a blog entry / removes a vote on an ideation blog idea
	 * 
	 * @param blogEntryToBeUnliked - The BlogPost instance of the blog entry to be unliked / idea in which the vote will be removed
	 * @param userUnlikingEntry - The User instance of the user to unlike the blog entry / remove their vote on the idea
	 * @param apiUserUnlikingEntry - The APICommunityBlogsHandler instance of the user to unlike the blog entry / remove their vote on the idea
	 */
	public static void unlikeOrRemoveVote(BlogPost blogEntryToBeUnliked, User userUnlikingEntry, APICommunityBlogsHandler apiUserUnlikingEntry) {
		
		log.info("INFO: " + userUnlikingEntry.getDisplayName() + " will now unlike the blog entry / remove their vote on the idea with title: " + blogEntryToBeUnliked.getTitle());
		boolean unlikedOrVoteRemoved = apiUserUnlikingEntry.unlikeOrRemoveVote(blogEntryToBeUnliked);
		
		log.info("INFO: Verify that the blog entry was unliked / vote was removed from the idea successfully");
		Assert.assertTrue(unlikedOrVoteRemoved, "ERROR: The blog entry was NOT unliked / the vote on the idea was NOT removed successfully");
	}
	
	/**
	 * Likes / recommends a comment on a blog / idea
	 * 
	 * @param blogCommentToBeLiked - The BlogComment instance of the comment to be liked / recommended
	 * @param userLikingComment - The User instance of the user to like / recommend the comment
	 * @param apiUserLikingComment - The APICommunityBlogsHandler instance of the user to like / recommend the comment
	 * @return - The BlogComment instance of the liked / recommended comment
	 */
	public static BlogComment likeComment(BlogComment blogCommentToBeLiked, User userLikingComment, APICommunityBlogsHandler apiUserLikingComment) {
		
		log.info("INFO: " + userLikingComment.getDisplayName() + " will now like / recommend the comment with content: " + blogCommentToBeLiked.getContent());
		BlogComment likedBlogComment = apiUserLikingComment.likeComment(blogCommentToBeLiked);
		
		log.info("INFO: Verify that the comment was liked successfully");
		Assert.assertNotNull(likedBlogComment, "ERROR: The comment was NOT liked successfully and was returned as null");
		
		return likedBlogComment;
	}
	
	/**
	 * Posts a comment to a blog entry / idea and then likes / recommends the comment
	 * 
	 * @param blogPost - The BlogPost instance of the blog entry / idea to which the comment will be posted
	 * @param baseBlogComment - The BaseBlogComment instance of the comment to be posted to the blog entry / idea and then liked / recommended
	 * @param userPostingComment - The User instance of the user posting and liking / recommending the comment
	 * @param apiUserPostingComment - The APICommunityBlogsHandler instance of the user posting and liking / recommending the comment
	 * @return - The BlogComment instance of the posted comment
	 */
	public static BlogComment createCommentAndLikeComment(BlogPost blogPost, BaseBlogComment baseBlogComment, User userPostingComment, APICommunityBlogsHandler apiUserPostingComment) {
		
		// Post the comment to the blog entry / idea
		BlogComment blogComment = createComment(blogPost, baseBlogComment, userPostingComment, apiUserPostingComment);
		
		// Like / recommend the comment
		return likeComment(blogComment, userPostingComment, apiUserPostingComment);
	}
	
	/**
	 * Creates an idea in a community ideation blog
	 * 
	 * @param community - The Community instance of the community in which the idea is to be created
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be created
	 * @param userCreatingIdea - The User instance of the user creating the idea
	 * @param apiUserCreatingIdea - The APICommunityBlogsHandler instance of the user creating the idea
	 * @return - The BlogPost instance of the newly created idea
	 */
	public static BlogPost createIdea(Community community, BaseBlogPost baseBlogPost, User userCreatingIdea, APICommunityBlogsHandler apiUserCreatingIdea) {
		
		log.info("INFO: " + userCreatingIdea.getDisplayName() + " will now create an idea in the community ideation blog");
		BlogPost idea = apiUserCreatingIdea.createIdeationBlogIdea(community, baseBlogPost);
		
		log.info("INFO: Verify that the ideation blog idea was created successfully");
		Assert.assertNotNull(idea, "ERROR: The ideation blog idea was NOT created successfully and was returned as null");
		
		return idea;
	}
	
	/**
	 * Adds a new ideation blog idea and then edits / updates the description for that idea
	 * 
	 * @param community - The Community instance of the community in which the ideation blog idea will be created
	 * @param baseBlogPost - The BaseBlogPost instance of the idea which is to be created
	 * @param newDescription - The new description to be set to the idea after updating it
	 * @param blogPostCreator - The User instance of the user creating and updating the idea
	 * @param apiBlogPostCreator - The APICommunityBlogsHandler instance of the user creating and updating the idea
	 * @return - The updated BlogPost instance
	 */
	public static BlogPost createIdeaAndEditDescription(Community community, BaseBlogPost baseBlogPost, String newDescription, User blogPostCreator, APICommunityBlogsHandler apiBlogPostCreator) {
		
		// Create a new ideation blog idea
		BlogPost idea = createIdea(community, baseBlogPost, blogPostCreator, apiBlogPostCreator);
		
		// Edit the idea description
		return editDescription(idea, newDescription, blogPostCreator, apiBlogPostCreator);
	}
	
	/**
	 * Creates an idea on a community ideation blog and then votes for the idea (using different users)
	 * 
	 * @param community - The Community in which the ideation blog idea is to be created and voted for
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be created
	 * @param userCreatingIdea - The User instance of the user creating the idea
	 * @param apiUserCreatingIdea - The APICommunityBlogsHandler instance of the user creating the idea
	 * @param userVotingForIdea - The User instance of the user voting for the idea
	 * @param apiUserVotingForIdea - The APICommunityBlogsHandler instance of the user voting for the idea
	 * @return - The BlogPost instance of the created and voted for idea
	 */
	public static BlogPost createIdeaAndVoteForIdea(Community community, BaseBlogPost baseBlogPost, User userCreatingIdea, APICommunityBlogsHandler apiUserCreatingIdea, User userVotingForIdea, APICommunityBlogsHandler apiUserVotingForIdea) {

		// Create a new ideation blog idea
		BlogPost idea = createIdea(community, baseBlogPost, userCreatingIdea, apiUserCreatingIdea);
		
		// Vote for the idea
		return likeOrVote(idea, userVotingForIdea, apiUserVotingForIdea);
	}
	
	/**
	 * Logs in as the specified user and navigates to the ideation blog idea
	 * 
	 * @param ui - The HomepageUI instance which is used to invoke various methods throughout the process
	 * @param uiCo - The CommunitiesUI instance used to navigate to the communities UI screen
	 * @param community - The Community instance of the community to be navigated to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to be navigated to in the UI
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be navigated to in the UI
	 * @param userToLogin - The User instance of the user to be logged in and perform all actions
	 * @param apiUserToLogin - The APICommunityHandler instance of the user to be logged in and perform all actions
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToIdea(HomepageUI ui, CommunitiesUI uiCo, Community community, BaseCommunity baseCommunity, BaseBlogPost baseBlogPost, User userToLogin, APICommunitiesHandler apiUserToLogin,
												boolean preserveInstance) {
		// Log into Communities UI and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userToLogin, apiUserToLogin, preserveInstance);
		
		// Navigate to ideation blogs
		selectIdeationBlogFromLeftNavigationMenu(uiCo);
		
		// Navigate to the ideation blog idea
		navigateToIdeationBlogIdea(ui, baseBlogPost);
	}
	
	/**
	 * Logs in as the specified user, navigates to the ideation blog idea and graduates the idea
	 * 
	 * @param ui - The HomepageUI instance which is used to invoke various methods throughout the process
	 * @param uiCo - The CommunitiesUI instance used to navigate to the communities UI screen
	 * @param community - The Community instance of the community to be navigated to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to be navigated to in the UI
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be navigated to and graduated in the UI
	 * @param userToLogin - The User instance of the user to be logged in and perform all actions
	 * @param apiUserToLogin - The APICommunityHandler instance of the user to be logged in and perform all actions
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndGraduateIdea(HomepageUI ui, CommunitiesUI uiCo, Community community, BaseCommunity baseCommunity, BaseBlogPost baseBlogPost, User userToLogin, APICommunitiesHandler apiUserToLogin,
											boolean preserveInstance) {
		
		// Log into Communities UI and navigate to the idea
		CommunityBlogEvents.loginAndNavigateToIdea(ui, uiCo, community, baseCommunity, baseBlogPost, userToLogin, apiUserToLogin, preserveInstance);
		
		// Graduate the idea
		graduateIdea(ui);
		
		// Return to home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);	
	}
	
	/**
	 * Navigates to the ideation blogs UI screen
	 * 
	 * @param ui - The HomepageUI instance used to invoke the fluentWaitTextPresent() and clickLinkWait() methods
	 * @param baseBlogPost - The BaseBlogPost instance of the ideation blog idea to be navigated to in the UI
	 */
	public static void navigateToIdeationBlogIdea(HomepageUI ui, BaseBlogPost baseBlogPost) {
		
		log.info("INFO: Wait for the Ideation Blogs UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogs);
		
		log.info("INFO: Clicking on the 'Ideas' tab");
		ui.clickLinkWait(BlogsUIConstants.Ideation_IdeasTab);
		
		log.info("INFO: Wait for the Ideation Blog Ideas UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseIdeationBlogsIdeas);
		
		log.info("INFO: Clicking on the link to the ideation blog idea with title: " + baseBlogPost.getTitle());
		ui.clickLinkWait("link=" + baseBlogPost.getTitle());
		
		log.info("INFO: Wait for the Ideation Blog Ideas UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForIdeationBlogIdeas);
	}
	
	/**
	 * Graduates an idea using the UI
	 * 
	 * @param ui - The HomepageUI instance used to invoke the clickLinkWait() and fluentWaitTextPresent() methods
	 */
	public static void graduateIdea(HomepageUI ui) {
		
		log.info("INFO: Clicking on 'Graduate' to graduate the idea");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduate);
		
		log.info("INFO: Clicking 'OK' in the pop up dialog box to confirm idea graduation");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);
		
		log.info("INFO: Verify that the graduation successful message is displayed");
		ui.fluentWaitTextPresent(Data.getData().IdeaGraduatedMsg);
	}
	
	/**
	 * Creates an ideation blog idea and allows a specified user to post a comment to that idea
	 * 
	 * @param community - The Community instance of the community in which the idea will be created
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be created
	 * @param baseBlogComment - The BaseBlogComment instance of the idea comment to be posted
	 * @param userCreatingIdea - The User instance of the user creating the idea
	 * @param apiUserCreatingIdea - The APICommunityBlogsHandler instance of the user creating the idea
	 * @param userPostingComment - The User instance of the user posting the comment to the idea
	 * @param apiUserPostingComment - The APICommunityBlogsHandler instance of the user posting the comment to the idea
	 * @return - The BlogComment instance of the comment posted to the idea
	 */
	public static BlogComment createIdeaAndAddComment(Community community, BaseBlogPost baseBlogPost, BaseBlogComment baseBlogComment, User userCreatingIdea, APICommunityBlogsHandler apiUserCreatingIdea, User userPostingComment, APICommunityBlogsHandler apiUserPostingComment) {
		
		// Create a new ideation blog idea
		BlogPost idea = createIdea(community, baseBlogPost, userCreatingIdea, apiUserCreatingIdea);
		
		// Add the comment to the idea
		return createComment(idea, baseBlogComment, userPostingComment, apiUserPostingComment);
	}
	
	/**
	 * Edits / updates a comment posted to an ideation blog idea / blog entry
	 * 
	 * @param ideaCommentToBeUpdated - The BlogComment instance of the idea comment to be updated
	 * @param baseBlogComment - The BaseBlogComment instance of the updated comment
	 * @param userUpdatingComment - The User instance of the user updating the comment
	 * @param apiUserUpdatingComment - The APICommunityBlogsHandler instance of the user updating the comment
	 * @return - The updated BlogComment instance
	 */
	public static BlogComment editComment(BlogComment blogComment, BaseBlogComment baseBlogComment, User userUpdatingComment, APICommunityBlogsHandler apiUserUpdatingComment) {
		
		log.info("INFO: " + userUpdatingComment.getDisplayName() + " will now update the existing comment with content: " + blogComment.getContent());
		BlogComment updatedComment = apiUserUpdatingComment.editComment(blogComment, baseBlogComment);
		
		log.info("INFO: Verify that the comment has been updated successfully");
		Assert.assertTrue(updatedComment.getContent().equals(baseBlogComment.getContent()), 
							"ERROR: The comment was not updated and was instead returned as: " + updatedComment.getContent());
		return updatedComment;
	}
	
	/**
	 * Posts a comment to a blog entry / idea and then edits / updates the comment
	 * 
	 * @param blogPost - The BlogPost instance of the blog entry / idea to which the comment will be posted
	 * @param baseBlogComment - The BaseBlogComment instance of the comment to be posted to the blog entry / idea
	 * @param updatedBaseBlogComment - The BaseBlogComment instance of the updated comment
	 * @param userPostingComment - The User instance of the user posting the comment to the blog entry / idea and then updating the comment
	 * @param apiUserPostingComment - The APICommunityBlogsHandler instance of the user posting the comment to the blog entry / idea and then updating the comment
	 * @return - The BlogComment instance of the updated comment
	 */
	public static BlogComment createCommentAndEditComment(BlogPost blogPost, BaseBlogComment baseBlogComment, BaseBlogComment updatedBaseBlogComment, User userPostingComment, APICommunityBlogsHandler apiUserPostingComment) {
		
		// Post the comment to the blog entry / idea
		BlogComment blogComment = createComment(blogPost, baseBlogComment, userPostingComment, apiUserPostingComment);
		
		// Update the comment posted to the blog entry / idea
		return editComment(blogComment, updatedBaseBlogComment, userPostingComment, apiUserPostingComment);
	}
	
	/**
	 * Logs in, navigates to the community ideation blog idea and then posts a trackback comment on that idea
	 * 
	 * @param ui - The HomepageUI instance used to invoke all relevant methods during the process
	 * @param driver - The RCLocationExecutor instance used to invoke all relevant methods during the process
	 * @param userToLogin - The User instance of the user to be logged into Communities UI
	 * @param baseCommunity - The BaseCommunity instance of the community to be navigated to in the UI
	 * @param communityAPIUser - The APICommunitiesHandler instance of the user to be logged into Communities UI
	 * @param community - The Community instance of the community to be navigated to in the UI
	 * @param uiCo - The CommunitiesUI instance used to invoke all relevant methods during the process
	 * @param baseBlogPost - The BaseBlogPost instance of the ideation blog idea to be navigated to in the UI
	 * @param trackbackComment - The comment content of the trackback comment to be posted to the idea
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndAddIdeationBlogTrackbackComment(HomepageUI ui, RCLocationExecutor driver, User userToLogin, BaseCommunity baseCommunity, APICommunitiesHandler communityAPIUser, Community community, CommunitiesUI uiCo, BaseBlogPost baseBlogPost, 
																String trackbackComment, boolean preserveInstance) {
		// Log into Communities UI and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userToLogin, communityAPIUser, preserveInstance);
		
		// Navigate to ideation blogs
		selectIdeationBlogFromLeftNavigationMenu(uiCo);
				
		// Navigate to the ideation blog idea
		navigateToIdeationBlogIdea(ui, baseBlogPost);
			
		// Post the trackback comment to the ideation blog idea
		BlogEvents.addTrackbackComment(ui, driver, userToLogin, trackbackComment);
		
		// Ensure that the Home icon is visible before attempting to click on it.
		UIEvents.resetASToTop(ui);
			
		// Return to home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);	
	}
	
	/**
	 * Creates a idea with mentions to a specified user in the idea content / description
	 * 
	 * @param ideaOwner - The User instance of the user who will create the idea with the mention
	 * @param apiIdeaOwner - The APICommunityBlogsHandler instance of the user who will create the idea with the mention
	 * @param mentions - The Mentions instance of the user who will be mentioned
	 * @param baseBlogPost - The BaseBlogPost instance of the ideation blog idea which will contain the mentions
	 * @param community - The Community instance of the community in which the idea will be created
	 * @return - A BlogPost object
	 */
	public static BlogPost createIdeationBlogIdeaWithMentions(User ideaOwner, APICommunityBlogsHandler apiIdeaOwner, Mentions mentions, BaseBlogPost baseBlogPost, Community community){

		log.info("INFO: " + ideaOwner.getDisplayName() + " will now create an idea mentioning '" + mentions.getUserToMention().getDisplayName() + "' in the idea content / description");
		BlogPost blogPost = apiIdeaOwner.createIdeationBlogIdeaWithMentions(baseBlogPost, community, mentions);
		
		log.info("INFO: Verify that the idea with mentions was created successfully");
		Assert.assertNotNull(blogPost, "ERROR: The idea with mentions was NOT created successfully and was returned as null");
		
		return blogPost;
	}
	
	/**
	 * Creates a comment with mentions to a specified user on a blog post / idea
	 * 
	 * @param blogPost - The BlogPost instance of the blog post / idea to which the comment will be posted
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userCreatingComment - The User instance of the user creating the comment
	 * @param apiUserCreatingComment - The APICommunityBlogsHandler instance of the user creating the comment
	 * @return - A BlogComment object
	 */
	public static BlogComment createCommentWithMentions(BlogPost blogPost, Mentions mentions, User userCreatingComment, APICommunityBlogsHandler apiUserCreatingComment) {
		
		log.info("INFO: " + userCreatingComment + " will now create a comment with mentions to the user with user name: " + mentions.getUserToMention().getDisplayName());
		BlogComment blogComment = apiUserCreatingComment.createCommentWithMentions(blogPost, mentions);
		
		log.info("INFO: Verify that the comment with mentions was posted successfully");
		Assert.assertNotNull(blogComment, "ERROR: The comment with mentions could NOT be created and was returned as null");
		
		return blogComment;
	}
	
	/**
	 * Deletes a comment posted to a blog entry / idea
	 * 
	 * @param commentToBeDeleted - The BlogComment instance of the blog comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the blog comment
	 * @param apiUserDeletingComment - The APICommunityBlogsHandler instance of the user deleting the blog comment
	 * @return - True if all actions are completed successfully
	 */
	public static boolean deleteComment(BlogComment commentToBeDeleted, User userDeletingComment, APICommunityBlogsHandler apiUserDeletingComment) {
		
		log.info("INFO: " + userDeletingComment.getDisplayName() + " will now delete the blog comment with content: " + commentToBeDeleted.getContent());
		boolean deleted = apiUserDeletingComment.deleteComment(commentToBeDeleted);
		
		log.info("INFO: Verify that the blog comment was deleted successfully");
		Assert.assertTrue(deleted, "ERROR: The blog comment was NOT deleted as expected using the API");
		
		return true;
	}
	
	/**
	 * Opens the EE for the specified blogs news story and posts a reply with multiple mentions to the news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param blogNewsStory - The String content of the news story to be used to open the EE
	 * @param usersToBeMentioned - The Array of Mentions instances of all users to be mentioned in the comment
	 */
	public static void openEEAndPostCommentWithMultipleMentions(HomepageUI ui, RCLocationExecutor driver, String blogNewsStory, Mentions[] usersToBeMentioned) {
		
		// Open the EE for the blogs news story and post the comment with multiple mentions
		BlogEvents.openEEAndPostCommentWithMultipleMentions(ui, driver, blogNewsStory, usersToBeMentioned);
	}
	
	/**
	 * Opens the EE for the specified blogs news story and posts a comment to the news story
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param blogNewsStory - The String content of the news story to be used to open the EE
	 * @param commentToBePosted - The String content of the comment to be posted using the EE
	 */
	public static void openEEAndPostComment(HomepageUI ui, String blogNewsStory, String commentToBePosted) {
		
		// Open the EE for the blogs news story and post the comment
		BlogEvents.openEEAndPostComment(ui, blogNewsStory, commentToBePosted);
	}
	
	/**
	 * Log in and navigate to the community blog
	 * 
	 * @param community - The Community instance of the community to navigate to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to in the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to log in and navigate to the community blog
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to log in and navigate to the community blog
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToBlog(Community community, BaseCommunity baseCommunity, HomepageUI ui, CommunitiesUI uiCo, User userLoggingIn, APICommunitiesHandler apiUserLoggingIn, boolean preserveInstance) {
		
		// Log in and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to Blogs
		selectBlogFromLeftNavigationMenu(uiCo);
		
		// Wait for Blogs UI to load
		waitForCommunityBlogsUIPageToLoad(ui);
	}
	
	/**
	 * Log in, navigate to the community blog and edit / update the community blog name
	 * 
	 * @param community - The Community instance of the community to navigate to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to in the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to log in and update the community blog
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to log in and update the community blog
	 * @param editedBlogName - The String content of the name to which the community blog will be updated
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndEditBlogName(Community community, BaseCommunity baseCommunity, HomepageUI ui, RCLocationExecutor driver, CommunitiesUI uiCo, User userLoggingIn,
												APICommunitiesHandler apiUserLoggingIn, String editedBlogName, boolean preserveInstance) {
		// Log in and navigate to the community blog
		CommunityBlogEvents.loginAndNavigateToBlog(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		log.info("INFO: Now opening the actions menu for the community blog");
		ui.clickLinkWait(BlogsUIConstants.blogsActionMenu);
		
		log.info("INFO: Now clicking on the 'Manage Blog' option in the blog actions menu");
		ui.clickLinkWait(BlogsUIConstants.blogsManage);
		
		// Wait for Blogs Settings UI screen to load
		BlogEvents.waitForBlogsSettingsUIPageToLoad(ui);
		
		// Edit / update the blog name in Blogs Settings UI
		BlogEvents.editBlogNameUsingUI(ui, driver, editedBlogName);
		
		// Return to the home and logout
		LoginEvents.gotoHomeAndLogout(ui);
	}
	
	/**
	 * Creates a community blog entry with a mention to a user
	 * 
	 * @param entryCreator - The User instance of the user who is creating the blog entry with a mention
	 * @param entryCreatorAPI - The APICommunityBlogsHandler instance of the user who is creating the community blog entry with a mention
	 * @param baseBlogPost - The BaseBlogPost instance of the entry to be created
	 * @param community - The Community instance of the community to which the entry will be added
	 * @param mentions - The Mentions instance of the user to be mentioned in the blog entry description
	 * @return - A BlogPost object
	 */
	public static BlogPost createBlogPostWithMentions(User entryCreator, APICommunityBlogsHandler entryCreatorAPI, BaseBlogPost baseBlogPost, Community community, Mentions mentions){

		log.info("INFO: " + entryCreator.getDisplayName() + " creating a community blog entry with a mentions to " + mentions.getUserToMention().getDisplayName() + " using API method");
		BlogPost blogPost = entryCreatorAPI.createCommunityBlogEntryMentions(baseBlogPost, community, mentions);

		log.info("INFO: Verify that the new community blog entry with the mention was created successfully");
		Assert.assertNotNull(blogPost, "ERROR: The community blog entry with the mention was NOT created successfully and was returned as null");

		return blogPost;
	}
	
	/**
	 * Creates a community blog entry and then posts a comment to that entry with a mentions to a user
	 * 
	 * @param userCreatingComment - The User instance of the user who is creating the blog entry comment with a mention
	 * @param apiUserCreatingComment - The APICommunityBlogsHandler instance of the user who is creating the community blog entry comment with a mention
	 * @param baseBlogPost - The BaseBlogPost instance of the entry to be created
	 * @param community - The Community instance of the community to which the entry will be added
	 * @param mentions - The Mentions instance of the user to be mentioned in the blog entry description
	 * @return - A BlogPost object
	 */
	public static BlogPost createBlogPostAndAddCommentWithMentions(User userCreatingComment, APICommunityBlogsHandler apiUserCreatingComment, BaseBlogPost baseBlogPost, Community community, Mentions mentions){
		
		// Create the blog entry
		BlogPost blogPost = createBlogPost(userCreatingComment, apiUserCreatingComment, baseBlogPost, community);
		
		// Post a comment to the entry with mentions to the specified user
		createCommentWithMentions(blogPost, mentions, userCreatingComment, apiUserCreatingComment);
		
		return blogPost;
	}
	
	/**
	 * Creates an idea and adds a comment with a mentions
	 * 
	 * @param userCreatingComment - The User instance of the user who is creating the idea comment with a mention
	 * @param apiUserCreatingComment - The APICommunityBlogsHandler instance of the user who is creating the idea comment with a mention
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be created
	 * @param community - The Community instance of the community to which the idea will be added
	 * @param mentions - The Mentions instance of the user to be mentioned in the idea comment
	 * @return - A BlogPost object
	 */
	public static BlogPost createIdeaAndAddCommentWithMention(User userCreatingComment, APICommunityBlogsHandler apiUserCreatingComment, BaseBlogPost baseBlogPost, Community community, Mentions mentions){
		
		// Create the idea in the ideation blog
		BlogPost blogPost = createIdea(community, baseBlogPost, userCreatingComment, apiUserCreatingComment);
		
		// Post a comment to the idea with mentions to the specified user
		createCommentWithMentions(blogPost, mentions, userCreatingComment, apiUserCreatingComment);
		
		return blogPost;
	}
	
	/**
	 * Sends a notification to the specified user, notifying them about the specified blog entry
	 * 
	 * @param blogEntry - The BlogPost instance of the blog entry which the specified user will be notified about
	 * @param userSendingNotification - The User instance of the user sending the notification
	 * @param apiUserSendingNotification - The APICommunityBlogsHandler instance of the user sending the notification
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified about the blog entry
	 */
	public static void notifyUserAboutBlogEntry(BlogPost blogEntry, User userSendingNotification, APICommunityBlogsHandler apiUserSendingNotification, APIProfilesHandler apiUserToNotify) {
		
		log.info("INFO: " + userSendingNotification.getDisplayName() + " will now send a notification to " + apiUserToNotify.getDesplayName());
		boolean notificationSent = apiUserSendingNotification.notifyUserAboutBlogEntry(blogEntry, apiUserToNotify);
		
		log.info("INFO: Verify that the notification was successfully sent to " + apiUserToNotify.getDesplayName());
		Assert.assertTrue(notificationSent, 
							"ERROR: The notification could NOT be sent to " + apiUserToNotify.getDesplayName());
	}
	
	/**
	 * Logs in to the specified community blog as the specified user, opens the options for the specified blog entry and notifies the specified user about the blog entry
	 * 
	 * @param community - The Community instance of the community which contains the blog entry
	 * @param baseCommunity - The BaseCommunity instance of the community which contains the blog entry
	 * @param blogEntry - The BlogPost instance of the blog entry which the user is to be notified about
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified about the blog entry
	 * @param isOnPremise - True if the test case is being run On Premise, false if the test is a Smart Cloud test
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNotifyUserAboutBlogEntryUsingUI(Community community, BaseCommunity baseCommunity, BlogPost blogEntry, HomepageUI ui, CommunitiesUI uiCo, User userLoggingIn, APICommunitiesHandler apiUserLoggingIn, APIProfilesHandler apiUserToNotify, boolean isOnPremise, boolean preserveInstance) {
		
		// Log in to Communities UI and navigate to the community blog
		CommunityBlogEvents.loginAndNavigateToBlog(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		log.info("INFO: Clicking on the link to the blog entry with title: " + blogEntry.getTitle());
		ui.clickLinkWait("link=" + blogEntry.getTitle());
		
		// Wait for Blogs UI to load
		waitForCommunityBlogsUIPageToLoad(ui);
		
		// Notify the specified user about the blog entry
		notifyUserAboutBlogEntryUsingUI(ui, blogEntry, apiUserToNotify, isOnPremise);
		
		// Return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
	}

	/**
	 * Notify the specified user about the specified blog entry (assumes the user has already navigated to Blogs UI)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param blogEntry - The BlogPost instance of the blog entry which the user is to be notified about
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified about the blog entry
	 * @param isOnPremise - True if the test case is being run On Premise, false if the test is a Smart Cloud test
	 */
	private static void notifyUserAboutBlogEntryUsingUI(HomepageUI ui, BlogPost blogEntry, APIProfilesHandler apiUserToNotify, boolean isOnPremise) {
		
		log.info("INFO: Now clicking on the 'More Actions' link to expand on the blog entry options in Blogs UI");
		String blogEntryMoreActionsCSS = BlogsUIConstants.BlogEntry_MoreActions.replace("PLACEHOLDER", blogEntry.getTitle());
		ui.clickLinkWait(blogEntryMoreActionsCSS);
		
		log.info("INFO: Now clicking on the 'Notify Other People' option from the 'More Actions' menu for the blog entry");
		ui.clickLinkWait(BlogsUIConstants.BlogEntry_NotifyOtherPeople);
		
		log.info("INFO: Now clicking in to the notification form user name input field");
		ui.clickLinkWait(BlogsUIConstants.BlogEntry_NotificationForm_NameEntryField);
		
		log.info("INFO: Now entering the username to be notified about the blog entry into the name field");
		UIEvents.typeStringWithDelay(ui, apiUserToNotify.getDesplayName());
		
		log.info("INFO: Now waiting for the typeahead menu list to be displayed in the UI");
		ui.fluentWaitPresent(BlogsUIConstants.BlogEntry_NotificationForm_Typeahead_Menu);
		
		if(isOnPremise) {
			log.info("INFO: Now clicking on the 'Full Search' option in the typeahead menu");
			ui.clickLinkWait(BlogsUIConstants.BlogEntry_NotificationForm_Typeahead_Full_Search);
		}
		log.info("INFO: Now selecting the user from the typeahead with user ID: " + apiUserToNotify.getUUID());
		String typeaheadMenuItemCSS = BlogsUIConstants.BlogEntry_NotificationForm_Typeahead_MenuItem.replace("PLACEHOLDER", apiUserToNotify.getUUID());
		ui.clickLinkWait(typeaheadMenuItemCSS);
		
		log.info("INFO: Now clicking on the 'Send' button to post the notification to the user with ID: " + apiUserToNotify.getUUID());
		ui.clickLinkWait(BlogsUIConstants.BlogEntry_NotificationForm_SendButton);
		
		log.info("INFO: Verify that the success message is displayed indicating that the notification has been sent");
		ui.fluentWaitTextPresent(Data.getData().BlogEntry_NotificationSent_DialogSuccessMessage);
	}

	/**
	 * Selects the "Blog" option from the left-side navigation menu in Communities UI
	 * 
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	private static void selectBlogFromLeftNavigationMenu(CommunitiesUI uiCo) {
		
		log.info("INFO: Select blogs from the left navigation menu");
		Community_LeftNav_Menu.BLOG.select(uiCo);
	}
	
	/**
	 * Selects the "Ideation Blog" option from the left-side navigation menu in Communities UI
	 * 
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	private static void selectIdeationBlogFromLeftNavigationMenu(CommunitiesUI uiCo) {
		
		log.info("INFO: Select blogs from the left navigation menu");
		Community_LeftNav_Menu.IDEATIONBLOG.select(uiCo);
	}
	
	/**
	 * Waits for the Blogs page in Communities UI to load by ensuring the 'Feed for Blog Entries' text is displayed
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void waitForCommunityBlogsUIPageToLoad(HomepageUI ui) {
		
		log.info("INFO: Wait for the Blogs UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForBlogEntries);
	}
}