package com.ibm.conn.auto.util.newsStoryBuilder.forums;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class ForumNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'Create Forum' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param forumTitle - The String content containing the title of the forum that has been created
	 * @param userNameCreatingForum - The String content containing the user name of the user who has created the forum
	 * @return - The String content containing the 'Create Forum' news story
	 */
	public static String getCreateForumNewsStory(HomepageUI ui, String forumTitle, String userNameCreatingForum) {
		return createNewsStory(ui, Data.CREATE_FORUM, forumTitle, null, userNameCreatingForum);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been created
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been created
	 * @param userNameAddingTopic - The String content containing the user name of the user who has created the topic
	 * @return - The String content containing the 'Create Forum Topic' news story
	 */
	public static String getCreateTopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameAddingTopic) {
		return createNewsStory(ui, Data.CREATE_TOPIC, topicTitle, forumTitle, userNameAddingTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Like Reply To Their Own Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @param userNameLikingReply - The String content containing the user name of the user who has liked the reply
	 * @return - The String content containing the 'Like Reply To Their Own Forum Topic' news story
	 */
	public static String getLikeReplyToTheirOwnTopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameLikingReply) {
		return createNewsStory(ui, Data.FORUM_LIKE_RESPONSE_THEIR_OWN_TOPIC, topicTitle, forumTitle, userNameLikingReply);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked A Reply To Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @param userNameLikingReply - The String content containing the user name of the user who has liked the reply
	 * @return - The String content containing the 'User Liked A Reply To Your Topic' news story
	 */
	public static String getLikeReplyToYourTopicNewsStory_User(HomepageUI ui, String topicTitle, String forumTitle, String userNameLikingReply) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_THREAD_RESPONSE, topicTitle, forumTitle, userNameLikingReply);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked A Reply To Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'You Liked A Reply To Your Topic' news story
	 */
	public static String getLikeReplyToYourTopicNewsStory_You(HomepageUI ui, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_THREAD_RESPONSE_YOU, topicTitle, forumTitle, null);
	}

	/**
	 * Retrieves the news story corresponding the the 'Like Their Own Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been liked
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been liked
	 * @param userNameLikingTopic - The String content containing the user name of the user who has liked the topic
	 * @return - The String content containing the 'Like Their Own Forum Topic' news story
	 */
	public static String getLikeTheirOwnTopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameLikingTopic) {
		return createNewsStory(ui, Data.FORUM_LIKE_OWNTOPIC, topicTitle, forumTitle, userNameLikingTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 And User 2 Liked Your Reply To The Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content containing the user name of the current user to like / recommend the reply
	 * @param previousUserLiking - The String content containing the user name of the last / previous user to like / recommend the reply
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'User 1 And User 2 Liked Your Reply To The Topic' news story
	 */
	public static String getLikeYourReplyToTheTopicNewsStory_TwoUsers(HomepageUI ui, String currentUserLiking, String previousUserLiking, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_RESPONSE_TWO_LIKES, previousUserLiking, topicTitle, forumTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked Your Reply To The Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content containing the user name of the current user to like / recommend the reply
	 * @param numberOfOtherUsersLiking - The String content containing the number of other users who have liked / recommended the reply
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'User And X Others Liked Your Reply To The Topic' news story
	 */
	public static String getLikeYourReplyToTheTopicNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherUsersLiking, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_RESPONSE_MANY, numberOfOtherUsersLiking, topicTitle, forumTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your Reply To The Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content containing the user name of the current user to like / recommend the reply
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'User And You Liked Your Reply To The Topic' news story
	 */
	public static String getLikeYourReplyToTheTopicNewsStory_UserAndYou(HomepageUI ui, String topicTitle, String forumTitle, String currentUserLiking) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_RESPONSE_YOU_OTHER, topicTitle, forumTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your Reply To The Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersLiking - The String content containing the number of other users who have liked / recommended the reply
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'You And X Others Liked Your Reply To The Topic' news story
	 */
	public static String getLikeYourReplyToTheTopicNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersLiking, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_RESPONSE_YOU_MANY, numberOfOtherUsersLiking, topicTitle, forumTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 And User 2 Liked Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content containing the user name of the current user to like / recommend the topic
	 * @param previousUserLiking - The String content containing the user name of the last / previous user to like / recommend the topic
	 * @param topicTitle - The String content containing the title of the topic which has been liked
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been liked
	 * @return - The String content containing the 'User 1 And User 2 Liked Your Topic' news story
	 */
	public static String getLikeYourTopicNewsStory_TwoUsers(HomepageUI ui, String currentUserLiking, String previousUserLiking, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_TOPIC_TWO_LIKES, previousUserLiking, topicTitle, forumTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been liked
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been liked
	 * @param userNameLikingTopic - The String content containing the user name of the user who has liked the topic
	 * @return - The String content containing the 'User Liked Your Topic' news story
	 */
	public static String getLikeYourTopicNewsStory_User(HomepageUI ui, String topicTitle, String forumTitle, String userNameLikingTopic) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_TOPIC, topicTitle, forumTitle, userNameLikingTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content containing the user name of the current user to like / recommend the topic
	 * @param numberOfOtherUsersLiking - The String content containing the number of other users who have liked / recommended the topic
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'User And X Others Liked Your Topic' news story
	 */
	public static String getLikeYourTopicNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherUsersLiking, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_TOPIC_MANY, numberOfOtherUsersLiking, topicTitle, forumTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been liked
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been liked
	 * @param userNameLikingTopic - The String content containing the user name of the user who has liked the topic
	 * @return - The String content containing the 'User And You Liked Your Topic' news story
	 */
	public static String getLikeYourTopicNewsStory_UserAndYou(HomepageUI ui, String topicTitle, String forumTitle, String userNameLikingTopic) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_TOPIC_YOU_OTHER, topicTitle, forumTitle, userNameLikingTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been liked
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been liked
	 * @return - The String content containing the 'You Liked Your Topic' news story
	 */
	public static String getLikeYourTopicNewsStory_You(HomepageUI ui, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_TOPIC_YOU, topicTitle, forumTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersLiking - The String content containing the number of other users who have liked / recommended the topic
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been liked
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @return - The String content containing the 'You And X Others Liked Your Topic' news story
	 */
	public static String getLikeYourTopicNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersLiking, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.FORUM_LIKE_YOUR_TOPIC_YOU_MANY, numberOfOtherUsersLiking, topicTitle, forumTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Reply To A Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic to which the reply was posted
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been added
	 * @param userNameMentioningYou - The String content containing the user name of the user who has mentioned another user in the topic reply
	 * @return - The String content containing the 'Mentioned You In A Reply To A Topic' news story
	 */
	public static String getMentionedYouInAReplyToATopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameMentioningYou) {
		return createNewsStory(ui, Data.MENTIONED_YOU_FORUM_TOPIC_REPLY, topicTitle, forumTitle, userNameMentioningYou);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic in which the user has been mentioned
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been added
	 * @param userNameMentioningYou - The String content containing the user name of the user who has mentioned another user in the topic
	 * @return - The String content containing the 'Mentioned You In A Topic' news story
	 */
	public static String getMentionedYouInATopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameMentioningYou) {
		return createNewsStory(ui, Data.MENTIONED_YOU_FORUM_TOPIC, topicTitle, forumTitle, userNameMentioningYou);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Reply To Their Own Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @param userNameReplyingToTopic - The String content containing the user name of the user who has replied to the topic
	 * @return - The String content containing the 'Reply To Their Own Forum Topic' news story
	 */
	public static String getReplyToTheirOwnTopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameReplyingToTopic) {
		return createNewsStory(ui, Data.CREATE_THEIR_OWN_REPLY, topicTitle, forumTitle, userNameReplyingToTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Replied To The Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @param userNameReplyingToTopic - The String content containing the user name of the user who has replied to the topic
	 * @return - The String content containing the 'User Replied To The Forum Topic' news story
	 */
	public static String getReplyToTheTopicNewsStory_User(HomepageUI ui, String topicTitle, String forumTitle, String userNameReplyingToTopic) {
		return createNewsStory(ui, Data.CREATE_REPLY, topicTitle, forumTitle, userNameReplyingToTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 And User 2 Replied To Your Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the current user to comment on the topic
	 * @param previousUserCommenting - The String content containing the user name of the last / previous user to comment on the forum
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @return - The String content containing the 'User 1 And User 2 Replied To Your Forum Topic' news story
	 */
	public static String getReplyToYourTopicNewsStory_TwoUsers(HomepageUI ui, String currentUserCommenting, String previousUserCommenting, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.CREATE_REPLY_YOUR_TOPIC_TWO_REPLIES, previousUserCommenting, topicTitle, forumTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Replied To Your Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @param userNameReplyingToTopic - The String content containing the user name of the user who has replied to the topic
	 * @return - The String content containing the 'User Replied To Your Forum Topic' news story
	 */
	public static String getReplyToYourTopicNewsStory_User(HomepageUI ui, String topicTitle, String forumTitle, String userNameReplyingToTopic) {
		return createNewsStory(ui, Data.CREATE_REPLY_YOUR_TOPIC, topicTitle, forumTitle, userNameReplyingToTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Replied To Your Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the current user to comment on the forum topic
	 * @param numberOfOtherUsersCommented - The String content containing the number of other users who have commented on the forum topic
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @return - The String content containing the 'User And X Others Replied To Your Forum Topic' news story
	 */
	public static String getReplyToYourTopicNewsStory_UserAndMany(HomepageUI ui, String currentUserCommenting, String numberOfOtherUsersCommented, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.CREATE_REPLY_YOUR_TOPIC_MANY, numberOfOtherUsersCommented, topicTitle, forumTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Replied To Your Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @param userNameReplyingToTopic - The String content containing the user name of the user who has replied to the topic
	 * @return - The String content containing the 'User And You Replied To Your Forum Topic' news story
	 */
	public static String getReplyToYourTopicNewsStory_UserAndYou(HomepageUI ui, String topicTitle, String forumTitle, String userNameReplyingToTopic) {
		return createNewsStory(ui, Data.CREATE_REPLY_YOUR_TOPIC_YOU_OTHER, topicTitle, forumTitle, userNameReplyingToTopic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Replied To Your Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @return - The String content containing the 'You Replied To Your Forum Topic' news story
	 */
	public static String getReplyToYourTopicNewsStory_You(HomepageUI ui, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.CREATE_REPLY_YOUR_TOPIC_YOU, topicTitle, forumTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Replied To Your Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersCommented - The String content containing the number of other users who have commented on the forum topic
	 * @param topicTitle - The String content containing the title of the topic which has been replied to
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been replied to
	 * @return - The String content containing the 'You And X Others Replied To Your Forum Topic' news story
	 */
	public static String getReplyToYourTopicNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersCommented, String topicTitle, String forumTitle) {
		return createNewsStory(ui, Data.CREATE_REPLY_YOUR_TOPIC_YOU_MANY, numberOfOtherUsersCommented, topicTitle, forumTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Forum' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param forumTitle - The String content containing the title of the forum which has been updated
	 * @param userNameUpdatingForum - The String content containing the user name of the user who has updated the forum
	 * @return - The String content containing the 'Update Forum' news story
	 */
	public static String getUpdateForumNewsStory(HomepageUI ui, String forumTitle, String userNameUpdatingForum) {
		return createNewsStory(ui, Data.UPDATE_FORUM, forumTitle, null, userNameUpdatingForum);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Reply To Their Own Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic in which the reply has been updated
	 * @param forumTitle - The String content containing the title of the forum which contains the topic
	 * @param userNameUpdatingReply - The String content containing the user name of the user who has updated the reply to the topic
	 * @return - The String content containing the 'Update Reply To Their Own Forum Topic' news story
	 */
	public static String getUpdateReplyToTheirOwnTopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameUpdatingReply) {
		return createNewsStory(ui, Data.UPDATE_REPLY_THEIR_OWN_THREAD, topicTitle, forumTitle, userNameUpdatingReply);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Forum Topic' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param topicTitle - The String content containing the title of the topic which has been updated
	 * @param forumTitle - The String content containing the title of the forum in which the topic has been updated
	 * @param userNameUpdatingTopic - The String content containing the user name of the user who has updated the topic
	 * @return - The String content containing the 'Update Forum Topic' news story
	 */
	public static String getUpdateTopicNewsStory(HomepageUI ui, String topicTitle, String forumTitle, String userNameUpdatingTopic) {
		return createNewsStory(ui, Data.UPDATE_TOPIC, topicTitle, forumTitle, userNameUpdatingTopic);
	}
}