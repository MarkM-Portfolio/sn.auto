package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityNewsStories extends BaseNewsStoryBuilder {
	
	/**
	 * Retrieves the news story corresponding the the 'User Added You To The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title to which the user has been added
	 * @param userWhoAddedMember - The String content of the user name who added the user to the community
	 * @return - The String content containing the 'User Added You To The Community' news story
	 */
	public static String getAddedYouToTheCommunityNewsStory(HomepageUI ui, String communityTitle, String userWhoAddedMember) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, communityTitle, null, userWhoAddedMember);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Their Own Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @param userNameCommenting - The String content of the user name who commented on the message
	 * @return - The String content containing the 'User Commented On Their Own Message' news story
	 */
	public static String getCommentOnTheirOwnMessageNewsStory_User(HomepageUI ui, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMUNITY_STATUS_UPDATE_COMMENT_SAME_USER, communityTitle, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserCommenting - The String content of the previous user name who commented on the message
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @param currentUserCommenting - The String content of the current user name who commented on the message
	 * @return - The String content containing the 'User1 And User2 Commented On Your Message' news story
	 */
	public static String getCommentOnYourMessageNewsStory_TwoUsers(HomepageUI ui, String previousUserCommenting, String communityTitle, String currentUserCommenting) {
		return createNewsStory(ui, Data.COMMUNITY_COMMENT_YOUR_MESSAGE_TWO_COMMENTERS, previousUserCommenting, communityTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @param userNameCommenting - The String content of the user name who commented on the message
	 * @return - The String content containing the 'User Commented On Your Message' news story
	 */
	public static String getCommentOnYourMessageNewsStory_User(HomepageUI ui, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMUNITY_COMMENT_YOUR_MESSAGE, communityTitle, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content of the current user name who commented on the message
	 * @param numberOfOtherComments - The String content of the number of other users who have commented on the message
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @return - The String content containing the 'User And X Others Commented On Your Message' news story
	 */
	public static String getCommentOnYourMessageNewsStory_UserAndMany(HomepageUI ui, String currentUserCommenting, String numberOfOtherComments, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_COMMENT_YOUR_MESSAGE_MANY, numberOfOtherComments, communityTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @param userNameCommenting - The String content of the user name who commented on the message
	 * @return - The String content containing the 'User And You Commented On Your Message' news story
	 */
	public static String getCommentOnYourMessageNewsStory_UserAndYou(HomepageUI ui, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMUNITY_COMMENT_YOUR_MESSAGE_YOU_OTHER, communityTitle, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @return - The String content containing the 'You Commented On Your Message' news story
	 */
	public static String getCommentOnYourMessageNewsStory_You(HomepageUI ui, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_COMMENT_YOUR_MESSAGE_YOU, communityTitle, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfCommenters - The String content containing the number of other commenters on the message
	 * @param communityTitle - The String content of the community title in which the message has been commented on
	 * @return - The String content containing the 'You And X Others Commented On Your Message' news story
	 */
	public static String getCommentOnYourMessageNewsStory_YouAndMany(HomepageUI ui, String numberOfCommenters, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_COMMENT_YOUR_MESSAGE_YOU_MANY, numberOfCommenters, communityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title which has been created
	 * @param userNameCommunityOwner - The String content of the user name who created / owns the community
	 * @return - The String content containing the 'Create Community' news story
	 */
	public static String getCreateCommunityNewsStory(HomepageUI ui, String communityTitle, String userNameCommunityOwner) {
		return createNewsStory(ui, Data.CREATE_COMMUNITY, communityTitle, null, userNameCommunityOwner);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Invited You To Join The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title to which the user was invited
	 * @param userNameSentInvite - The String content of the user name who sent the invite the join the community
	 * @return - The String content containing the 'Invited You To Join The  Community' news story
	 */
	public static String getInvitedYouToJoinTheCommunityNewsStory(HomepageUI ui, String communityTitle, String userNameSentInvite) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME, communityTitle, null, userNameSentInvite);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User Liked Their Own Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @param userNameLikingComment - The String content of the user name who liked / recommended the comment
	 * @return - The String content containing the 'User Liked Their Own Comment In The Community' news story
	 */
	public static String getLikeTheirOwnCommentInTheCommunityNewsStory(HomepageUI ui, String communityTitle, String userNameLikingComment) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU_SAVED, communityTitle, null, userNameLikingComment);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User1 And User2 Liked Your Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLiking - The String content of the previous user name who liked / recommended the comment
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @param currentUserLiking - The String content of the most recent user name who liked / recommended the comment
	 * @return - The String content containing the 'User1 And User2 Liked Your Comment In The Community' news story
	 */
	public static String getLikeYourCommentInTheCommunityNewsStory_TwoUsers(HomepageUI ui, String previousUserLiking, String communityTitle, String currentUserLiking) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_TWO_LIKES, previousUserLiking, communityTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User Liked Your Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @param userLikingComment - The String content of the user name who liked / recommended the comment
	 * @return - The String content containing the 'User Liked Your Comment In The Community' news story
	 */
	public static String getLikeYourCommentInTheCommunityNewsStory_User(HomepageUI ui, String communityTitle, String userLikingComment) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE, communityTitle, null, userLikingComment);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User And You Liked Your Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @param userLikingComment - The String content of the user name who liked / recommended the comment
	 * @return - The String content containing the 'User And You Liked Your Comment In The Community' news story
	 */
	public static String getLikeYourCommentInTheCommunityNewsStory_UserAndYou(HomepageUI ui, String communityTitle, String userLikingComment) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU_OTHER, communityTitle, null, userLikingComment);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User And X Others Liked Your Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the most recent user name who liked / recommended the comment
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the comment
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @return - The String content containing the 'User And X Others Liked Your Comment In The Community' news story
	 */
	public static String getLikeYourCommentInTheCommunityNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherLikes, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_MANY, numberOfOtherLikes, communityTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'You Liked Your Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @return - The String content containing the 'You Liked Your Comment In The Community' news story
	 */
	public static String getLikeYourCommentInTheCommunityNewsStory_You(HomepageUI ui, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU, communityTitle, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'You And X Others Liked Your Comment In The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the comment
	 * @param communityTitle - The String content of the community title in which the comment has been liked / recommended
	 * @return - The String content containing the 'You And X Others Liked Your Comment In The Community' news story
	 */
	public static String getLikeYourCommentInTheCommunityNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherLikes, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_COMMENT_MESSAGE_YOU_MANY, numberOfOtherLikes, communityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User1 And User2 Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserLiking - The String content of the previous user name who liked / recommended the status update
	 * @param communityTitle - The String content of the community title in which the status update has been liked / recommended
	 * @param currentUserLiking - The String content of the current user name who liked / recommended the status update
	 * @return - The String content containing the 'User1 And User2 Liked Your Message' news story
	 */
	public static String getLikeYourMessageNewsStory_TwoUsers(HomepageUI ui, String previousUserLiking, String communityTitle, String currentUserLiking) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_MESSAGE_TWO_LIKES, previousUserLiking, communityTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the status update has been liked / recommended
	 * @param userLikingMessage - The String content of the user name who liked / recommended the status update
	 * @return - The String content containing the 'User Liked Your Message' news story
	 */
	public static String getLikeYourMessageNewsStory_User(HomepageUI ui, String communityTitle, String userLikingMessage) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_MESSAGE, communityTitle, null, userLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User And X Others Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the current user name who liked / recommended the status update
	 * @param numberOfOtherLikes - The String content of the number of other users who have liked / recommended the status update
	 * @param communityTitle - The String content of the community title in which the status update has been liked / recommended
	 * @return - The String content containing the 'User And X Others Liked Your Message' news story
	 */
	public static String getLikeYourMessageNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherLikes, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_MESSAGE_MANY, numberOfOtherLikes, communityTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'User And You Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the status update has been liked / recommended
	 * @param userLikingMessage - The String content of the user name who liked / recommended the status update
	 * @return - The String content containing the 'User And You Liked Your Message' news story
	 */
	public static String getLikeYourMessageNewsStory_UserAndYou(HomepageUI ui, String communityTitle, String userLikingMessage) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_MESSAGE_YOU_OTHER, communityTitle, null, userLikingMessage);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'You Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the status update has been liked / recommended
	 * @return - The String content containing the 'You Liked Your Message' news story
	 */
	public static String getLikeYourMessageNewsStory_You(HomepageUI ui, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_MESSAGE_YOU, communityTitle, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding to the 'You And X Others Liked Your Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the status update has been liked / recommended
	 * @return - The String content containing the 'You And X Others Liked Your Message' news story
	 */
	public static String getLikeYourMessageNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherLikes, String communityTitle) {
		return createNewsStory(ui, Data.COMMUNITY_LIKE_YOUR_MESSAGE_YOU_MANY, numberOfOtherLikes, communityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Mentioned You In A Comment On User's Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the user has been mentioned
	 * @param userNameMentioning - The String content of the user name who mentioned the user in the community message
	 * @return - The String content containing the 'User Mentioned You In A Comment On User's Message' news story
	 */
	public static String getMentionedYouInACommentOnMessageNewsStory(HomepageUI ui, String communityTitle, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_COMMENT_COMM, communityTitle, null, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Mentioned You In A Message' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the user has been mentioned
	 * @param userNameMentioning - The String content of the user name who mentioned the user in the community message
	 * @return - The String content containing the 'User Mentioned You In A Message' news story
	 */
	public static String getMentionedYouInAMessageNewsStory(HomepageUI ui, String communityTitle, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_COMMUNITY, communityTitle, null, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Posted A Message To The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the user has posted the message
	 * @param userNamePosting - The String content of the user name who posted the message
	 * @return - The String content containing the 'User Posted A Message To The Community' news story
	 */
	public static String getPostedAMessageNewsStory(HomepageUI ui, String communityTitle, String userNamePosting) {
		return createNewsStory(ui, Data.ADD_COMMUNITY_STATUS_UPDATE, communityTitle, null, userNamePosting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Has Requested To Join Your Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title to which the user has requested to join
	 * @param userNameRequestingToJoin - The String content of the user name who has requested to join the community
	 * @return - The String content containing the 'User Has Requested To Join Your Community' news story
	 */
	public static String getRequestedToJoinYourCommunityNewsStory(HomepageUI ui, String communityTitle, String userNameRequestingToJoin) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_COMMUNITY_REQUEST_FOR_ME, communityTitle, null, userNameRequestingToJoin);
	}
}