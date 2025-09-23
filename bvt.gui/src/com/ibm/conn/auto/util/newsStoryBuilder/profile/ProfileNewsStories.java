package com.ibm.conn.auto.util.newsStoryBuilder.profile;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class ProfileNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'User Commented on Users Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMessageCreator - The String content of the user name of the user who created the message which was commented on
	 * @param userNameCommenting - The String content of the user name who has commented on the message
	 * @return - The String content containing the 'User Commented on Users Message' news story
	 */
	public static String getCommentedOnAnotherUsersMessageNewsStory_User(HomepageUI ui, String userNameMessageCreator, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_STATUSUPDATE_OTHER_USER, userNameMessageCreator, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented on Users Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMessageCreator - The String content of the user name of the user who created the message which was commented on
	 * @return - The String content containing the 'You Commented on Users Message' news story
	 */
	public static String getCommentedOnAnotherUsersMessageNewsStory_You(HomepageUI ui, String userNameMessageCreator) {
		return createNewsStory(ui, Data.COMMENT_STATUSUPDATE_OTHER_USER_YOU, userNameMessageCreator, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Their Own Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name of the user who has commented on the message
	 * @return - The String content containing the 'User Commented On Their Own Message' news story
	 */
	public static String getCommentedOnTheirOwnMessageNewsStory(HomepageUI ui, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_ON_THEIR_OWN_MESSAGE, null, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 Commented On Their Own Message Posted To User 2' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameReceivedMessage - The String content of the user name who the message was posted to
	 * @param userNameCommenting - The String content of the user name of the user who has commented on the message
	 * @return - The String content containing the 'User 1 Commented On Their Own Message Posted To User 2' news story
	 */
	public static String getCommentedOnTheirOwnMessagePostedToUserNewsStory(HomepageUI ui, String userNameReceivedMessage, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_USER_THEIR_OWN_MESSAGE_TO_OTHER_USER, userNameReceivedMessage, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 Commented On User 2's Message Posted To User 3' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name of the user who has commented on the message
	 * @param userNameMessageCreator - The String content of the user name of the user who created the message which has been commented on
	 * @param userNameMessageReceiver - The String content of the user name who was the receiver of the original message which has been commented on
	 * @return - The String content containing the 'User 1 Commented On User 2's Message Posted To User 3' news story
	 */
	public static String getCommentedOnUsersMessagePostedToUserNewsStory(HomepageUI ui, String userNameCommenting, String userNameMessageCreator, String userNameMessageReceiver) {
		return createNewsStory(ui, Data.COMMENT_BOARD_MESSAGE_RECIPIENT_USER_COMMENT, userNameMessageCreator, userNameMessageReceiver, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserCommenting - The String content of the user name of the previous user who has commented on the message
	 * @param currentUserCommenting - The String content of the user name of the most recent user who has commented on the message
	 * @return - The String content containing the 'User1 And User2 Commented On Your Message' news story
	 */
	public static String getCommentedOnYourMessageNewsStory_TwoUsers(HomepageUI ui, String previousUserCommenting, String currentUserCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_STATUSUPDATE_TWO_COMMENTERS, previousUserCommenting, null, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name of the user who has commented on the message
	 * @return - The String content containing the 'User Commented On Your Message' news story
	 */
	public static String getCommentedOnYourMessageNewsStory_User(HomepageUI ui, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_STATUSUPDATE, null, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name of the most recent user who has commented on the message
	 * @param numberOfOtherCommenters - The String content of the number of other users who have commented on the message
	 * @return - The String content containing the 'User And X Others Commented On Your Message' news story
	 */
	public static String getCommentedOnYourMessageNewsStory_UserAndMany(HomepageUI ui, String userNameCommenting, String numberOfOtherCommenters) {
		return createNewsStory(ui, Data.COMMENT_YOUR_STATUSUPDATE_MANY, numberOfOtherCommenters, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name of the user who has commented on the message
	 * @return - The String content containing the 'User And You Commented On Your Message' news story
	 */
	public static String getCommentedOnYourMessageNewsStory_UserAndYou(HomepageUI ui, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_STATUSUPDATE_OTHER_USER_YOU_OTHER, null, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Commented On Your Message' news story
	 */
	public static String getCommentedOnYourMessageNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.COMMENT_YOUR_STATUSUPDATE_YOU, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherCommenters - The String content of the number of other users who have commented on the message
	 * @return - The String content containing the 'You And X Others Commented On Your Message' news story
	 */
	public static String getCommentedOnYourMessageNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherCommenters) {
		return createNewsStory(ui, Data.COMMENT_YOUR_STATUSUPDATE_YOU_MANY, numberOfOtherCommenters, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Invited You To Become A Network Contact' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameWhoSentInvite - The String content of the user name of the user who sent the invite to join their network
	 * @return - The String content containing the 'User Invited You To Become A Network Contact' news story
	 */
	public static String getInvitedYouToBecomeANetworkContactNewsStory_You(HomepageUI ui, String userNameWhoSentInvite) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME, null, null, userNameWhoSentInvite);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Their Own Comment' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLikingComment - The String content of the user name who liked / recommended the comment on the message
	 * @return - The String content containing the 'User Liked Their Own Comment' news story
	 */
	public static String getLikedTheirOwnCommentNewsStory(HomepageUI ui, String userNameLikingComment) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT_YOU_USER, null, null, userNameLikingComment);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 Liked Their Own Comment On A Message Posted To User 2' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameWhoReceivedMessage - The String content of the user name who was the receiver of the message
	 * @param userNameLikingComment - The String content of the user name who liked / recommended the comment on the message
	 * @return - The String content containing the 'User 1 Liked Their Own Comment On A Message Posted To User 2' news story
	 */
	public static String getLikedTheirOwnCommentOnABoardMessageNewsStory(HomepageUI ui, String userNameWhoReceivedMessage, String userNameLikingComment) {
		return createNewsStory(ui, Data.LIKE_SU_THEIR_OWN_COMMENT_SAVED_VIEW, userNameWhoReceivedMessage, null, userNameLikingComment);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Their Own Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLikingMessage - The String content of the user name who liked / recommended the message
	 * @return - The String content containing the 'User Liked Their Own Message' news story
	 */
	public static String getLikedTheirOwnMessageNewsStory(HomepageUI ui, String userNameLikingMessage) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE_YOU_THEIR_OWN, null, null, userNameLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your Comment On A Message Posted To User' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameWhoReceivedMessage - The String content of the user name who was the receiver of the message
	 * @return - The String content containing the 'You Liked Your Comment On A Message Posted To User' news story
	 */
	public static String getLikedYourCommentOnABoardMessageNewsStory_You(HomepageUI ui, String userNameWhoReceivedMessage) {
		return createNewsStory(ui, Data.LIKE_SU_THEIR_OWN_COMMENT_YOU_USERS_BOARD_MESSAGE, userNameWhoReceivedMessage, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Liked Your Comment On A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLiking - The String content of the user name of the previous user liking / recommending the comment.
	 * @param currentUserLiking - The String content of the user name of the most recent user liking / recommending the comment.
	 * @return - The String content containing the 'User1 And User2 Liked Your Comment On A Message' news story
	 */
	public static String getLikedYourCommentOnAMessageNewsStory_TwoUsers(HomepageUI ui, String previousUserLiking, String currentUserLiking) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT_TWO_LIKES, previousUserLiking, null, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Your Comment On A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLiking - The String content of the user name of the user liking / recommending the comment.
	 * @return - The String content containing the 'User Liked Your Comment On A Message' news story
	 */
	public static String getLikedYourCommentOnAMessageNewsStory_User(HomepageUI ui, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT, null, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked Your Comment On A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the user name of the most recent user liking / recommending the comment
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the comment
	 * @return - The String content containing the 'User And X Others Liked Your Comment On A Message' news story
	 */
	public static String getLikedYourCommentOnAMessageNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherLikes) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT_MANY, numberOfOtherLikes, null, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your Comment On A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLiking - The String content of the user name of the user liking / recommending the comment.
	 * @return - The String content containing the 'User And You Liked Your Comment On A Message' news story
	 */
	public static String getLikedYourCommentOnAMessageNewsStory_UserAndYou(HomepageUI ui, String userNameLiking) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT_YOU_OTHER, null, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your Comment On A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Liked Your Comment On A Message' news story
	 */
	public static String getLikedYourCommentOnAMessageNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT_ON_MESSAGE, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your Comment On A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the comment
	 * @return - The String content containing the 'You And X Others Liked Your Comment On A Message' news story
	 */
	public static String getLikedYourCommentOnAMessageNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherLikes) {
		return createNewsStory(ui, Data.LIKE_SU_YOUR_COMMENT_YOU_MANY, numberOfOtherLikes, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLikingMessage - The String content of the user name of the previous user liking / recommending the message
	 * @param userLikingStatus - The String content of the user name of the most recent user liking / recommending the message
	 * @return - The String content containing the 'User1 And User2 Liked Your Message' news story
	 */
	public static String getLikedYourMessageNewsStory_TwoUsers(HomepageUI ui, String previousUserLikingMessage, String currentUserLikingMessage) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE_TWO_LIKES, previousUserLikingMessage, null, currentUserLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userLikingStatus - The String content of the user name of the user liking / recommending the message
	 * @return - The String content containing the 'User Liked Your Message' news story
	 */
	public static String getLikedYourMessageNewsStory_User(HomepageUI ui, String userLikingMessage) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE, null, null, userLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userLikingStatus - The String content of the user name of the most recent user liking / recommending the message
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the message
	 * @return - The String content containing the 'User And X Others Liked Your Message' news story
	 */
	public static String getLikedYourMessageNewsStory_UserAndMany(HomepageUI ui, String currentUserLikingMessage, String numberOfOtherLikes) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE_MANY, numberOfOtherLikes, null, currentUserLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userLikingStatus - The String content of the user name of the user liking / recommending the message
	 * @return - The String content containing the 'User And You Liked Your Message' news story
	 */
	public static String getLikedYourMessageNewsStory_UserAndYou(HomepageUI ui, String userLikingMessage) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE_YOU_OTHER, null, null, userLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Liked Your Message' news story
	 */
	public static String getLikedYourMessageNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE_YOU, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the message
	 * @return - The String content containing the 'You And X Others Liked Your Message' news story
	 */
	public static String getLikedYourMessageNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherLikes) {
		return createNewsStory(ui, Data.LIKE_YOUR_STATUSUPDATE_YOU_MANY, numberOfOtherLikes, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Mentioned You In A Comment On User's Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMessageCreator - The String content containing the user name of the user who created the message
	 * @return - The String content containing the 'User Mentioned You In A Comment On User's Message' news story
	 */
	public static String getMentionedYouInACommentOnMessageNewsStory(HomepageUI ui, String userNameMessageCreator) {
		return createNewsStory(ui, Data.MENTIONED_YOU_COMMENT, null, null, userNameMessageCreator);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Mentioned You In A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMessageCreator - The String content containing the user name of the user who created the message
	 * @return - The String content containing the 'User Mentioned You In A Message' news story
	 */
	public static String getMentionedYouInAMessageNewsStory(HomepageUI ui, String userNameMessageCreator) {
		return createNewsStory(ui, Data.MENTIONED_YOU, null, null, userNameMessageCreator);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1's Message Originally Posted To User 2' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMessageCreator - The String content containing the user name of the user who created the message
	 * @param userNameMessageReceiver - The String content containing the user name of the user who received the message
	 * @return - The String content containing the 'User 1's Message Originally Posted To User 2' news story
	 */
	public static String getMessageOriginallyPostedToUserNewsStory(HomepageUI ui, String userNameMessageCreator, String userNameMessageReceiver) {
		return createNewsStory(ui, Data.MESSAGE_ORIGINALLY_POSTED_TO, userNameMessageReceiver, null, userNameMessageCreator);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 Posted A Message To User 2' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameReceivingMessage - The String content containing the user name of the user who received the message
	 * @param userNamePostingMessage - The String content containing the user name of the user who posted the message
	 * @return - The String content containing the 'User 1 Posted A Message To User 2' news story
	 */
	public static String getPostedAMessageToUserNewsStory(HomepageUI ui, String userNameReceivingMessage, String userNamePostingMessage) {
		return createNewsStory(ui, Data.BOARD_MESSAGE_OTHER_USER, userNameReceivingMessage, null, userNamePostingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Posted A Message To You' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNamePostingMessage - The String content containing the user name of the user who posted the message
	 * @return - The String content containing the 'User Posted A Message To You' news story
	 */
	public static String getPostedAMessageToYouNewsStory(HomepageUI ui, String userNamePostingMessage) {
		return createNewsStory(ui, Data.BOARD_MESSAGE_TO_YOU, null, null, userNamePostingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Reposted' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameReposted - The String content containing the user name of the user who reposted the news story
	 * @return - The String content containing the 'User Reposted' news story
	 */
	public static String getUserRepostedNewsStory(HomepageUI ui, String userNameReposted) {
		return createNewsStory(ui, Data.REPOSTED_UPDATE, null, null, userNameReposted);
	}
}