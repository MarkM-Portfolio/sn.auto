package com.ibm.conn.auto.util.newsStoryBuilder.blogs;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class BlogNewsStories extends BaseNewsStoryBuilder {
 
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
		return createNewsStory(ui, Data.COMMENT_BLOG_ENTRY_YOU_OTHER, entryTitle, blogTitle, userNameCommenting);
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
		return createNewsStory(ui, Data.ADD_COMMENT_BLOG_ENTRY, entryTitle, blogTitle, userNameCommenting);
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
		return createNewsStory(ui, Data.COMMENT_YOUR_BLOG_ENTRY_TWO_COMMENTERS, previousUserCommenting, entryTitle, blogTitle, currentUserCommenting);
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
		return createNewsStory(ui, Data.COMMENT_YOUR_BLOG_ENTRY, entryTitle, blogTitle, userNameCommenting);
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
		return createNewsStory(ui, Data.COMMENT_YOUR_BLOG_ENTRY_MANY, numberOfOtherUsersCommenting, entryTitle, blogTitle, userNameCommenting);
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
		return createNewsStory(ui, Data.COMMENT_YOUR_BLOG_ENTRY_YOU_OTHER, entryTitle, blogTitle, userNameCommenting);
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
		return createNewsStory(ui, Data.COMMENT_YOUR_BLOG_ENTRY_BY_YOU, entryTitle, blogTitle, null);
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
		return createNewsStory(ui, Data.COMMENT_YOUR_BLOG_ENTRY_YOU_MANY, numberOfOtherUsersCommenting, entryTitle, blogTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Created A Blog' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param blogTitle - The String content containing the title of the blog which has been created
	 * @param userNameCreatingEntry - The String content containing the user name of the user who has created the blog
	 * @return - The String content containing the 'Created A Blog' news story
	 */
	public static String getCreateBlogNewsStory(HomepageUI ui, String blogTitle, String userNameCreatingEntry) {
		return createNewsStory(ui, Data.CREATE_BLOG, blogTitle, null, userNameCreatingEntry);
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
		return createNewsStory(ui, Data.CREATE_BLOG_ENTRY, entryTitle, blogTitle, userNameCreatingEntry);
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
		return createNewsStory(ui, Data.ADD_TB_BLOG_ENTRY, entryTitle, blogTitle, userNamePostingTrackback);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked A Comment On Their Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'Liked A Comment On Their Blog Entry' news story
	 */
	public static String getLikeACommentOnTheirEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_THEIR_BLOG_ENTRY, entryTitle, blogTitle, userNameLiking);
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
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_BLOG_ENTRY_TWO_LIKES, previousUserLiking, entryTitle, null, currentUserLiking);
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
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_BLOG_ENTRY_OTHER, entryTitle, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the user name of the most recent user who liked / recommended the comment
	 * @param numberOfUsersLiking - The String content of the number of other users who have liked / recommended the comment
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'User And X Others Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfUsersLiking, String entryTitle) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_BLOG_ENTRY_MANY, numberOfUsersLiking, entryTitle, null, currentUserLiking);
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
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_BLOG_ENTRY_YOU_OTHER, entryTitle, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked A Comment On Your Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @return - The String content containing the 'You Liked A Comment On Your Blog Entry' news story
	 */
	public static String getLikeACommentOnYourEntryNewsStory_You(HomepageUI ui, String entryTitle) {
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_BLOG_ENTRY_YOU, entryTitle, null, null);
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
		return createNewsStory(ui, Data.LIKE_A_COMMENT_YOUR_BLOG_ENTRY_YOU_MANY, numberOfUsersLiking, entryTitle, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked Their Own Comment On Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry to which the comment that has been liked / recommended has been posted
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the comment
	 * @return - The String content containing the 'Liked Their Own Comment On Entry' news story
	 */
	public static String getLikeTheirOwnCommentOnEntryNewsStory(HomepageUI ui, String entryTitle, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_BLOG_COMMENT, entryTitle, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Liked Their Own Blog Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the blog entry which has been liked / recommended
	 * @param blogTitle - The String content of the title of the blog which contains the blog entry
	 * @param userNameLiking - The String content of the user name of the user who liked / recommended the entry
	 * @return - The String content containing the 'Liked Their Own Blog Entry' news story
	 */
	public static String getLikeTheirOwnEntryNewsStory(HomepageUI ui, String entryTitle, String blogTitle, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_THEIR_OWN_BLOG_ENTRY, entryTitle, blogTitle, userNameLiking);
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
		return createNewsStory(ui, Data.LIKE_YOUR_BLOG_ENTRY_TWO, previousUserLiking, entryTitle, blogTitle, currentUserLiking);
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
		return createNewsStory(ui, Data.LIKE_YOUR_BLOG_ENTRY, entryTitle, blogTitle, userLikingEntry);
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
		return createNewsStory(ui, Data.LIKE_YOUR_BLOG_ENTRY_MANY, numberOfOtherUsersLiking, entryTitle, blogTitle, currentUserLiking);
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
		return createNewsStory(ui, Data.LIKE_YOUR_BLOG_ENTRY_YOU_OTHER, entryTitle, blogTitle, userLikingEntry);
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
		return createNewsStory(ui, Data.LIKE_YOUR_BLOG_ENTRY_YOU, entryTitle, blogTitle, null);
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
		return createNewsStory(ui, Data.LIKE_YOUR_BLOG_ENTRY_YOU_MANY, numberOfOtherLikers, entryTitle, blogTitle, null);
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
		return createNewsStory(ui, Data.MENTIONED_YOU_BLOG_ENTRY, entryTitle, blogTitle, userNameMentioning);
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
		return createNewsStory(ui, Data.MENTIONED_YOU_BLOG_ENTRY_COMMENT, entryTitle, blogTitle, userNameMentioning);
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
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_BLOG_ENTRY_NOTIFY_FOR_ME, entryTitle, blogTitle, userNameNotifying);
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
		return createNewsStory(ui, Data.EDIT_BLOG, blogTitle, null, userNameUpdating);
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
		return createNewsStory(ui, Data.UPDATE_THEIR_OWN_BLOG_COMMENT, entryTitle, blogTitle, userNameUpdating);
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
		return createNewsStory(ui, Data.UPDATE_YOUR_BLOG_COMMENT, entryTitle, blogTitle, userNameUpdating);
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
		return createNewsStory(ui, Data.UPDATE_BLOG_COMMENT_YOU_YOUR_ENTRY, entryTitle, blogTitle, null);
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
		return createNewsStory(ui, Data.UPDATE_BLOG_ENTRY, entryTitle, blogTitle, userNameUpdating);
	}
}