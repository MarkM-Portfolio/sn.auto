package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.util.newsStoryBuilder.blogs.BlogNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityBlogNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On The Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry that has been commented on
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User And You Commented On The Entry' news story
	 */
	public static String getCommentOnTheEntryNewsStory_UserAndYou(HomepageUI ui, String entryTitle, String blogTitle, String userNameCommenting) {
		return BlogNewsStories.getCommentOnTheEntryNewsStory_UserAndYou(ui, entryTitle, blogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Commented On Their Own Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameCommenting - The String content of the user name of the user who commented on the entry
	 * @return - The String content containing the 'Commented On Their Own Blog Entry' news story
	 */
	public static String getCommentOnTheirOwnEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameCommenting) {
		return BlogNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, entryTitle, blogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On The Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on the idea
	 * @return - The String content containing the 'User Commented On The Idea' news story
	 */
	public static String getCommentOnTheIdeaNewsStory_User(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_IDEA, ideaTitle, ideationBlogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On The Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on the idea
	 * @return - The String content containing the 'User And You Commented On The Idea' news story
	 */
	public static String getCommentOnTheIdeaNewsStory_UserAndYou(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_IDEA_YOU_OTHER, ideaTitle, ideationBlogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Commented On Their Own Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on the idea
	 * @return - The String content containing the 'Commented On Their Own Idea' news story
	 */
	public static String getCommentOnTheirOwnIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_IDEATION_BLOG_IDEA, ideaTitle, ideationBlogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Commented On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserCommenting - The String content of the user name of the previous user that commented on the entry
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param currentUserCommenting - The String content of the user name of the most recent user that commented on the entry
	 * @return - The String content containing the 'User1 And User2 Commented On Your Blog Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_TwoUsers(HomepageUI ui, String previousUserCommenting, String entryTitle, String blogTitle, String currentUserCommenting) {
		return BlogNewsStories.getCommentOnYourEntryNewsStory_TwoUsers(ui, previousUserCommenting, entryTitle, blogTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameCommenting - The String content of the user name of the user who commented on the entry
	 * @return - The String content containing the 'User Commented On Your Blog Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_User(HomepageUI ui, String entryTitle, String blogTitle, String userNameCommenting) {
		return BlogNewsStories.getCommentOnYourEntryNewsStory_User(ui, entryTitle, blogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Commented On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name of the most recent user that commented on the entry
	 * @param numberOfOtherUsersCommenting - The String content containing the number of other users who have commented on the entry
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @return - The String content containing the 'User And X Others Commented On Your Blog Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_UserAndMany(HomepageUI ui, String userNameCommenting, String numberOfOtherUsersCommenting, String entryTitle, String blogTitle) {
		return BlogNewsStories.getCommentOnYourEntryNewsStory_UserAndMany(ui, userNameCommenting, numberOfOtherUsersCommenting, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameCommenting - The String content of the user name of the user who commented on the entry
	 * @return - The String content containing the 'User And You Commented On Your Blog Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_UserAndYou(HomepageUI ui, String entryTitle, String blogTitle, String userNameCommenting) {
		return BlogNewsStories.getCommentOnYourEntryNewsStory_UserAndYou(ui, entryTitle, blogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @return - The String content containing the 'You Commented On Your Blog Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_You(HomepageUI ui, String entryTitle, String blogTitle) {
		return BlogNewsStories.getCommentOnYourEntryNewsStory_You(ui, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersCommenting - The String content containing the number of other users who have commented on the entry
	 * @param entryTitle - The String content of the blog entry to which the comment has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @return - The String content containing the 'You And X Others Commented On Your Blog Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersCommenting, String entryTitle, String blogTitle) {
		return BlogNewsStories.getCommentOnYourEntryNewsStory_YouAndMany(ui, numberOfOtherUsersCommenting, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Commented On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserCommenting - The String content containing the user name of the previous user who has commented on the idea
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param currentUserCommenting - The String content containing the user name of the most recent user who has commented on the idea
	 * @return - The String content containing the 'User1 And User2 Commented On Your Idea' news story
	 */
	public static String getCommentOnYourIdeaNewsStory_TwoUsers(HomepageUI ui, String previousUserCommenting, String ideaTitle, String ideationBlogTitle, String currentUserCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_IDEA_TWO_COMMENTERS, previousUserCommenting, ideaTitle, ideationBlogTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on the idea
	 * @return - The String content containing the 'User Commented On Your Idea' news story
	 */
	public static String getCommentOnYourIdeaNewsStory_User(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_IDEA, ideaTitle, ideationBlogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the most recent user who has commented on the idea
	 * @param numberOfOtherUsersCommenting - The String content of the number of other users who commented on the idea
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'User Commented On Your Idea' news story
	 */
	public static String getCommentOnYourIdeaNewsStory_UserAndMany(HomepageUI ui, String currentUserCommenting, String numberOfOtherUsersCommenting, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_IDEA_MANY, numberOfOtherUsersCommenting, ideaTitle, ideationBlogTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on the idea
	 * @return - The String content containing the 'User And You Commented On Your Idea' news story
	 */
	public static String getCommentOnYourIdeaNewsStory_UserAndYou(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_IDEA_YOU_OTHER, ideaTitle, ideationBlogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the ideas that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'You Commented On Your Idea' news story
	 */
	public static String getCommentOnYourIdeaNewsStory_You(HomepageUI ui, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_IDEA_YOU, ideaTitle, ideationBlogTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersCommenting - The String content containing the number of other users who have commented on the idea
	 * @param ideaTitle - The String content containing the title of the ideas that has been commented on
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'You And X Others Commented On Your Idea' news story
	 */
	public static String getCommentOnYourIdeaNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersCommenting, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_IDEA_YOU_MANY, numberOfOtherUsersCommenting, ideaTitle, ideationBlogTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Created The Community Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param blogTitle - The String content containing the title of the blog that has been created
	 * @param userNameCreatingBlog - The String content containing the user name of the user who has created the blog
	 * @return - The String content containing the 'User Created The Community Blog' news story
	 */
	public static String getCreateBlogNewsStory(HomepageUI ui, String blogTitle, String userNameCreatingBlog) {
		return createNewsStory(ui, Data.CREATE_COMM_BLOG, blogTitle, null, userNameCreatingBlog);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Created A Blog Entry In The Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry that has been created
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameCreatingEntry - The String content containing the user name of the user who has created the entry
	 * @return - The String content containing the 'Created A Blog Entry In The Blog' news story
	 */
	public static String getCreateEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameCreatingEntry) {
		return BlogNewsStories.getCreateEntryNewsStory(ui, entryTitle, blogTitle, userNameCreatingEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Created The Idea In The Ideation Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been created
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameCreatingIdea - The String content containing the user name of the user who has created the idea
	 * @return - The String content containing the 'Created The Idea In The Ideation Blog' news story
	 */
	public static String getCreateIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameCreatingIdea) {
		return createNewsStory(ui, Data.CREATE_IDEATION_BLOG_IDEA, ideaTitle, ideationBlogTitle, userNameCreatingIdea);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Created The Community Ideation Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which has been created
	 * @param userNameCreatingBlog - The String content containing the user name of the user who has created the ideation blog
	 * @return - The String content containing the 'Created The Community Ideation Blog' news story
	 */
	public static String getCreateIdeationBlogNewsStory(HomepageUI ui, String ideationBlogTitle, String userNameCreatingBlog) {
		return createNewsStory(ui, Data.CREATE_IDEATION_BLOG, ideationBlogTitle, null, userNameCreatingBlog);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Graduated Their Own Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea which has been graduated 
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameGraduating - The String content containing the user name of the user who graduated the idea
	 * @return - The String content containing the 'Graduated Their Own Idea' news story
	 */
	public static String getGraduatedTheirOwnIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameGraduating) {
		return createNewsStory(ui, Data.GRADUATE_IDEA, ideaTitle, ideationBlogTitle, userNameGraduating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Left A Trackback On The Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea on which the trackback comment has been posted 
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNamePostingTrackback - The String content containing the user name of the user who posted the trackback
	 * @return - The String content containing the 'Left A Trackback On The Idea' news story
	 */
	public static String getLeftATrackbackOnTheIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNamePostingTrackback) {
		return createNewsStory(ui, Data.CREATE_TRACKBACK_IDEATION_OTHER_USER, ideaTitle, ideationBlogTitle, userNamePostingTrackback);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Left A Trackback On Their Own Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the title of the entry in which the trackback has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the entry
	 * @param userNamePostingTrackback - The String content of the user name of the user who has posted the trackback
	 * @return - The String content containing the 'Left A Trackback On Their Own Blog Entry' news story
	 */
	public static String getLeftATrackbackOnTheirOwnEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNamePostingTrackback) {
		return BlogNewsStories.getLeftATrackbackOnTheirOwnEntryNewsStory(ui, entryTitle, blogTitle, userNamePostingTrackback);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Left A Trackback On Their Own Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea on which the trackback comment has been posted 
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNamePostingTrackback - The String content containing the user name of the user who posted the trackback
	 * @return - The String content containing the 'Left A Trackback On Their Own Idea' news story
	 */
	public static String getLeftATrackbackOnTheirOwnIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNamePostingTrackback) {
		return createNewsStory(ui, Data.CREATE_TRACKBACK_IDEATION, ideaTitle, ideationBlogTitle, userNamePostingTrackback);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked A Comment On IdeaName' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content of the idea title to which the comment that has been liked / recommended has been posted
	 * @param userNameCommenting - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'Liked A Comment On IdeaName' news story
	 */
	public static String getLikeACommentOnIdeaNewsStory(HomepageUI ui, String ideaTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.LIKE_COMMENT_IDEA, ideaTitle, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked A Comment On Their Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry title to which the comment that has been liked / recommended has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameCommenting - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'Liked A Comment On Their Blog Entry' news story
	 */
	public static String getLikeACommentOnTheirEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameCommenting) {
		return BlogNewsStories.getLikeACommentOnTheirEntryNewsStory(ui, entryTitle, blogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked A Comment On Their Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content of the idea title to which the comment that has been liked / recommended has been posted
	 * @param userNameCommenting - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'Liked A Comment On Their Idea' news story
	 */
	public static String getLikeACommentOnTheirIdeaNewsStory(HomepageUI ui, String ideaTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_THEIR_IDEA, ideaTitle, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLiking - The String content of the user name of the previous user who liked / recommended the comment
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @param currentUserLiking - The String content of the user name of the most recent user who liked / recommended the comment
	 * @return - The String content containing the 'User1 And User2 Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_TwoUsers(HomepageUI ui, String previousUserLiking, String entryTitle, String currentUserLiking) {
		return BlogNewsStories.getLikeACommentOnYourEntryNewsStory_TwoUsers(ui, previousUserLiking, entryTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'User Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_User(HomepageUI ui, String entryTitle, String userNameLiking) {
		return BlogNewsStories.getLikeACommentOnYourEntryNewsStory_User(ui, entryTitle, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And X Others Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the user name of the most recent user who liked / recommended the comment
	 * @param numberOfUsersLiking - The String content of the number of other users who have liked / recommended the comment
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'User1 And X Others Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfUsersLiking, String entryTitle) {
		return BlogNewsStories.getLikeACommentOnYourEntryNewsStory_UserAndMany(ui, currentUserLiking, numberOfUsersLiking, entryTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'User And You Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_UserAndYou(HomepageUI ui, String entryTitle, String userNameLiking) {
		return BlogNewsStories.getLikeACommentOnYourEntryNewsStory_UserAndYou(ui, entryTitle, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'You Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_You(HomepageUI ui, String entryTitle) {
		return BlogNewsStories.getLikeACommentOnYourEntryNewsStory_You(ui, entryTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfUsersLiking - The String content of the number of other users who have liked / recommended the comment
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'You And X Others Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_YouAndMany(HomepageUI ui, String numberOfUsersLiking, String entryTitle) {
		return BlogNewsStories.getLikeACommentOnYourEntryNewsStory_YouAndMany(ui, numberOfUsersLiking, entryTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Liked A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLiking - The String content of the user name of the previous user who liked / recommended the comment
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @param currentUserLiking - The String content of the user name of the most recent user who liked / recommended the comment
	 * @return - The String content containing the 'User1 And User2 Liked A Comment On Your Idea' news story
	 */
	public static String getLikeACommentOnYourIdeaNewsStory_TwoUsers(HomepageUI ui, String previousUserLiking, String ideaTitle, String currentUserLiking) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_IDEA_TWO_LIKES, previousUserLiking, ideaTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'User Liked A Comment On Your Idea' news story
	 */
	public static String getLikeACommentOnYourIdeaNewsStory_User(HomepageUI ui, String ideaTitle, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_IDEA, ideaTitle, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the user name of the most recent user who liked / recommended the comment
	 * @param numberOfOtherUsersLiking - The String content of the number of other users who have liked / recommended the comment
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'User And X Others Liked A Comment On Your Idea' news story
	 */
	public static String getLikeACommentOnYourIdeaNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherUsersLiking, String ideaTitle) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_IDEA_MANY, numberOfOtherUsersLiking, ideaTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'User And You Liked A Comment On Your Idea' news story
	 */
	public static String getLikeACommentOnYourIdeaNewsStory_UserAndYou(HomepageUI ui, String ideaTitle, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_COMMENT_YOUR_IDEA_YOU_OTHER, ideaTitle, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'You Liked A Comment On Your Idea' news story
	 */
	public static String getLikeACommentOnYourIdeaNewsStory_You(HomepageUI ui, String ideaTitle) {
		return createNewsStory(ui, Data.LIKE_COMMENT_YOUR_IDEA_YOU, ideaTitle, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersLiking - The String content of the number of other users who have liked / recommended the comment
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'You And X Others Liked A Comment On Your Idea' news story
	 */
	public static String getLikeACommentOnYourIdeaNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersLiking, String ideaTitle) {
		return createNewsStory(ui, Data.LIKE_COMMENT_YOUR_IDEA_YOU_MANY, numberOfOtherUsersLiking, ideaTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked Their Own Comment On Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content of the idea to which the comment that has been liked / recommended has been posted
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'Liked Their Own Comment On Idea' news story
	 */
	public static String getLikeTheirOwnCommentOnIdeaNewsStory(HomepageUI ui, String ideaTitle, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_BLOG_COMMENT, ideaTitle, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked Their Own Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameCommenting - The String content of the user name of the user who liked / recommended the entry
	 * @return - The String content containing the 'Liked Their Own Blog Entry' news story
	 */
	public static String getLikeTheirOwnEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameCommenting) {
		return BlogNewsStories.getLikeTheirOwnEntryNewsStory(ui, entryTitle, blogTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Liked Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLiking - The String content of the user name of the previous user who has liked / recommended the blog entry
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param currentUserLiking - The String content of the user name of the most recent user who has liked / recommended the blog entry
	 * @return - The String content containing the 'User1 And User2 Liked Your Blog Entry' news story
	 */
	public static String getLikeYourEntryNewsStory_TwoUsers(HomepageUI ui, String previousUserLiking, String entryTitle, String blogTitle, String currentUserLiking) {
		return BlogNewsStories.getLikeYourEntryNewsStory_TwoUsers(ui, previousUserLiking, entryTitle, blogTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userLikingEntry - The String content of the user name who has liked / recommended the blog entry
	 * @return - The String content containing the 'User Liked Your Blog Entry' news story
	 */
	public static String getLikeYourEntryNewsStory_User(HomepageUI ui, String entryTitle, String blogTitle, String userLikingEntry) {
		return BlogNewsStories.getLikeYourEntryNewsStory_User(ui, entryTitle, blogTitle, userLikingEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the most recent user name who has liked / recommended the blog entry
	 * @param numberOfOtherUsersLiking - The String content of the number of other users who have liked / recommended the blog entry
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @return - The String content containing the 'User And X Others Liked Your Blog Entry' news story
	 */
	public static String getLikeYourEntryNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherUsersLiking, String entryTitle, String blogTitle) {
		return BlogNewsStories.getLikeYourEntryNewsStory_UserAndMany(ui, currentUserLiking, numberOfOtherUsersLiking, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userLikingEntry - The String content of the user name who has liked / recommended the blog entry
	 * @return - The String content containing the 'User And You Liked Your Blog Entry' news story
	 */
	public static String getLikeYourEntryNewsStory_UserAndYou(HomepageUI ui, String entryTitle, String blogTitle, String userLikingEntry) {
		return BlogNewsStories.getLikeYourEntryNewsStory_UserAndYou(ui, entryTitle, blogTitle, userLikingEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @return - The String content containing the 'You Liked Your Blog Entry' news story
	 */
	public static String getLikeYourEntryNewsStory_You(HomepageUI ui, String entryTitle, String blogTitle) {
		return BlogNewsStories.getLikeYourEntryNewsStory_You(ui, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherLikers - The String content of the number of other users who have liked / recommended the entry
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @return - The String content containing the 'You And X Others Liked Your Blog Entry' news story
	 */
	public static String getLikeYourEntryNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherLikers, String entryTitle, String blogTitle) {
		return BlogNewsStories.getLikeYourEntryNewsStory_YouAndMany(ui, numberOfOtherLikers, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry in which the mentions has been posted
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameMentioning - The String content containing the user name of the user who posted the entry with mentions
	 * @return - The String content containing the 'Mentioned You In A Blog Entry' news story
	 */
	public static String getMentionedYouInABlogEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameMentioning) {
		return BlogNewsStories.getMentionedYouInABlogEntryNewsStory(ui, entryTitle, blogTitle, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Comment On BlogEntryName In The Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry to which the comment with mentions has been posted
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameMentioning - The String content containing the user name of the user who posted the comment with mentions
	 * @return - The String content containing the 'Mentioned You In A Comment On BlogEntryName In The Blog' news story
	 */
	public static String getMentionedYouInACommentOnABlogEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameMentioning) {
		return BlogNewsStories.getMentionedYouInACommentOnABlogEntryNewsStory(ui, entryTitle, blogTitle, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Comment On Idea In The Ideation Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea to which the comment with mentions has been posted
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameMentioning - The String content containing the user name of the user who posted the comment with mentions
	 * @return - The String content containing the 'Mentioned You In A Comment On Idea In The Ideation Blog' news story
	 */
	public static String getMentionedYouInACommentOnIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_IDEA_COMMENT, ideaTitle, ideationBlogTitle, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In The Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea in which the mentions has been posted
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameMentioning - The String content containing the user name of the user who posted the idea with mentions
	 * @return - The String content containing the 'Mentioned You In The Idea' news story
	 */
	public static String getMentionedYouInTheIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_IDEA, ideaTitle, ideationBlogTitle, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Notified You About The Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the blog entry the user has been notified about
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameNotifying - The String content containing the user name of the user who sent the notification
	 * @return - The String content containing the 'User Notified You About The Blog Entry' news story
	 */
	public static String getNotifiedYouAboutTheBlogEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameNotifying) {
		return BlogNewsStories.getNotifiedYouAboutTheBlogEntryNewsStory(ui, entryTitle, blogTitle, userNameNotifying);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated The Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param blogTitle - The String content containing the title of the blog which has been updated
	 * @param userNameUpdating - The String content containing the user name of the user who updated the blog
	 * @return - The String content containing the 'Updated The Blog' news story
	 */
	public static String getUpdateBlogNewsStory(HomepageUI ui, String blogTitle, String userNameUpdating) {
		return BlogNewsStories.getUpdateBlogNewsStory(ui, blogTitle, userNameUpdating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated Their Own Comment On The Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry which contains the comment that has been updated
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameUpdating - The String content containing the user name of the user who updated the comment
	 * @return - The String content containing the 'Updated Their Own Comment On The Blog Entry' news story
	 */
	public static String getUpdateCommentOnTheEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameUpdating) {
		return BlogNewsStories.getUpdateCommentOnTheEntryNewsStory(ui, entryTitle, blogTitle, userNameUpdating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated A Comment On The Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea to which the updated commented has been posted
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameUpdatingComment - The String content containing the user name of the user who has updated the commented on the idea
	 * @return - The String content containing the 'Updated A Comment On The Idea' news story
	 */
	public static String getUpdateCommentOnTheIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameUpdatingComment) {
		return createNewsStory(ui, Data.UPDATE_IDEATION_COMMENT, ideaTitle, ideationBlogTitle, userNameUpdatingComment);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Updated A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry which contains the comment that has been updated
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameUpdating - The String content containing the user name of the user who updated the comment
	 * @return - The String content containing the 'User Updated A Comment On Your Blog Entry' news story
	 */
	public static String getUpdateCommentOnYourEntryNewsStory_User(HomepageUI ui, String entryTitle, String blogTitle, String userNameUpdating) {
		return BlogNewsStories.getUpdateCommentOnYourEntryNewsStory_User(ui, entryTitle, blogTitle, userNameUpdating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Updated A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry which contains the comment that has been updated
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @return - The String content containing the 'You Updated A Comment On Your Blog Entry' news story
	 */
	public static String getUpdateCommentOnYourEntryNewsStory_You(HomepageUI ui, String entryTitle, String blogTitle) {
		return BlogNewsStories.getUpdateCommentOnYourEntryNewsStory_You(ui, entryTitle, blogTitle);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Updated A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea which contains the comment that has been updated
	 * @param blogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameUpdating - The String content of the user name of the user who updated the comment
	 * @return - The String content containing the 'User Updated A Comment On Your Idea' news story
	 */
	public static String getUpdateCommentOnYourIdeaNewsStory_User(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameUpdating) {
		return createNewsStory(ui, Data.UPDATE_COMMENT_YOUR_IDEA, ideaTitle, ideationBlogTitle, userNameUpdating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Updated A Comment On Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea which contains the comment that has been updated
	 * @param blogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'You Updated A Comment On Your Idea' news story
	 */
	public static String getUpdateCommentOnYourIdeaNewsStory_You(HomepageUI ui, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.UPDATE_COMMENT_YOUR_IDEA_YOU, ideaTitle, ideationBlogTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated The Blog Entry In The Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the entry which has been updated
	 * @param blogTitle - The String content containing the title of the blog which contains the entry
	 * @param userNameUpdating - The String content containing the user name of the user who updated the entry
	 * @return - The String content containing the 'Updated The Blog Entry In The Blog' news story
	 */
	public static String getUpdateEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameUpdating) {
		return BlogNewsStories.getUpdateEntryNewsStory(ui, entryTitle, blogTitle, userNameUpdating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated The Idea In The Ideation Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea which has been updated
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameUpdating - The String content containing the user name of the user who updated the idea
	 * @return - The String content containing the 'Updated The Idea In The Ideation Blog' news story
	 */
	public static String getUpdateIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameUpdating) {
		return createNewsStory(ui, Data.UPDATE_IDEATION_BLOG_IDEA, ideaTitle, ideationBlogTitle, userNameUpdating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Voted For The Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameVoting - The String content containing the user name of the user who has voted for the idea
	 * @return - The String content containing the 'User Voted For The Idea' news story
	 */
	public static String getVotedForTheIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameVoting) {
		return createNewsStory(ui, Data.VOTE_FOR_BLOG, ideaTitle, ideationBlogTitle, userNameVoting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Voted For Their Own Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameVoting - The String content containing the user name of the user who has voted for the idea
	 * @return - The String content containing the 'User Voted For Their Own Idea' news story
	 */
	public static String getVotedForTheirOwnIdeaNewsStory(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameVoting) {
		return createNewsStory(ui, Data.VOTE_FOR_OWN_IDEA, ideaTitle, ideationBlogTitle, userNameVoting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Voted For Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserVoting - The String content containing the user name of the previous user who has voted for the idea
	 * @param ideaTitle - The String content containing the title of the idea that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param currentUserVoting - The String content containing the user name of the most recent user who has voted for the idea
	 * @return - The String content containing the 'User1 And User2 Voted For Your Idea' news story
	 */
	public static String getVotedForYourIdeaNewsStory_TwoUsers(HomepageUI ui, String previousUserVoting, String ideaTitle, String ideationBlogTitle, String currentUserVoting) {
		return createNewsStory(ui, Data.VOTED_YOUR_IDEA_TWO_VOTERS, previousUserVoting, ideaTitle, ideationBlogTitle, currentUserVoting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Voted For Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameVoting - The String content containing the user name of the user who has voted for the idea
	 * @return - The String content containing the 'User Voted For Your Idea' news story
	 */
	public static String getVotedForYourIdeaNewsStory_User(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameVoting) {
		return createNewsStory(ui, Data.VOTED_YOUR_IDEA, ideaTitle, ideationBlogTitle, userNameVoting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Voted For Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserVoting - The String content containing the user name of the most recent user who has voted for the idea
	 * @param numberOfOtherUsersVoting - The String content of the number of other users who have voted for the idea
	 * @param ideaTitle - The String content containing the title of the idea that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'User And X Others Voted For Your Idea' news story
	 */
	public static String getVotedForYourIdeaNewsStory_UserAndMany(HomepageUI ui, String currentUserVoting, String numberOfOtherUsersVoting, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.VOTED_YOUR_IDEA_MANY, numberOfOtherUsersVoting, ideaTitle, ideationBlogTitle, currentUserVoting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Voted For Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the idea that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @param userNameVoting - The String content containing the user name of the user who has voted for the idea
	 * @return - The String content containing the 'User And You Voted For Your Idea' news story
	 */
	public static String getVotedForYourIdeaNewsStory_UserAndYou(HomepageUI ui, String ideaTitle, String ideationBlogTitle, String userNameVoting) {
		return createNewsStory(ui, Data.VOTED_YOUR_IDEA_YOU_OTHER, ideaTitle, ideationBlogTitle, userNameVoting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Voted For Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param ideaTitle - The String content containing the title of the ideas that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'You Voted For Your Idea' news story
	 */
	public static String getVotedForYourIdeaNewsStory_You(HomepageUI ui, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.VOTED_YOUR_IDEA_YOU, ideaTitle, ideationBlogTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Voted For Your Idea' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUserVotes - The String content of the number of other users who have voted for the idea
	 * @param ideaTitle - The String content containing the title of the ideas that has been voted for
	 * @param ideationBlogTitle - The String content containing the title of the ideation blog which contains the idea
	 * @return - The String content containing the 'You And X Others Voted For Your Idea' news story
	 */
	public static String getVotedForYourIdeaNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUserVotes, String ideaTitle, String ideationBlogTitle) {
		return createNewsStory(ui, Data.VOTED_YOUR_IDEA_YOU_MANY, numberOfOtherUserVotes, ideaTitle, ideationBlogTitle, null);
	}
}