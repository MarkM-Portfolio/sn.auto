package com.ibm.conn.auto.util.newsStoryBuilder.activities;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class ActivityNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'Assigned You A To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoTitle - The String content containing the title of the to-do item which has been assigned
	 * @param activityTitle - The String content containing the title of the activity which contains the to-do item
	 * @param userAssignedTodo - The String content containing the user name of the user who has been assigned the to-do item
	 * @return - The String content containing the 'Assigned You A To-Do Item' news story
	 */
	public static String getAssignedYouAToDoItemNewsStory(HomepageUI ui, String todoTitle, String activityTitle, String userAssignedTodo) {
		return createNewsStory(ui, Data.ASSIGNED_TODO_ITEM_YOU, todoTitle, activityTitle, userAssignedTodo);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On The Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @param userCommentingOnEntry - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User Commented On The Entry' news story
	 */
	public static String getCommentOnEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userCommentingOnEntry) {
		return createNewsStory(ui, Data.COMMENT_ON_ACTIVITY, entryTitle, activityTitle, userCommentingOnEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On The Entry Added By You' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @param userCommentingOnEntry - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User Commented On The Entry Added By You' news story
	 */
	public static String getCommentOnTheEntryAddedByYouNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userCommentingOnEntry) {
		return createNewsStory(ui, Data.COMMENT_ON_YOUR_ENTRY, entryTitle, activityTitle, userCommentingOnEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Their Own Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @param userCommentingOnEntry - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User Commented On Their Own Entry' news story
	 */
	public static String getCommentOnTheirOwnEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userCommentingOnEntry) {
		return createNewsStory(ui, Data.COMMENT_ON_THEIR_OWN_ACTIVITY, entryTitle, activityTitle, userCommentingOnEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 and User 2 Commented On Your Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content of the current user who has posted a comment
	 * @param previousUserCommenting - The String content of the previous user who has posted a comment
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @return - The String content containing the 'User 1 and User 2 Commented On Your Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_TwoUsers(HomepageUI ui, String previousUserCommenting, String entryTitle, String activityTitle, String currentUserCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_ACTIVITY_TWO_COMMENTERS, previousUserCommenting, entryTitle, activityTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @param userCommentingOnEntry - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User Commented On Your Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_User(HomepageUI ui, String entryTitle, String activityTitle, String userCommentingOnEntry) {
		return createNewsStory(ui, Data.COMMENT_ON_YOUR_ACTIVITY, entryTitle, activityTitle, userCommentingOnEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Commented On Your Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherCommenters - The String content containing the number of other users who have commented on the activity entry
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @param userCommentingOnEntry - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User And X Others Commented On Your Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_UserAndMany(HomepageUI ui, String numberOfOtherCommenters, String entryTitle, String activityTitle, String userCommentingOnEntry) {
		return createNewsStory(ui, Data.COMMENT_YOUR_ACTIVITY_MANY, numberOfOtherCommenters, entryTitle, activityTitle, userCommentingOnEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @param userCommentingOnEntry - The String content containing the user name of the user who has commented on the entry
	 * @return - The String content containing the 'User And You Commented On Your Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_UserAndYou(HomepageUI ui, String entryTitle, String activityTitle, String userCommentingOnEntry) {
		return createNewsStory(ui, Data.COMMENT_YOUR_ACTIVITY_YOU_OTHER, entryTitle, activityTitle, userCommentingOnEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @return - The String content containing the 'You Commented On Your Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_You(HomepageUI ui, String entryTitle, String activityTitle) {
		return createNewsStory(ui, Data.COMMENT_BY_YOU_YOUR_ACTIVITY, entryTitle, activityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherCommenters - The String content containing the number of other users who have commented on the activity entry
	 * @param entryTitle - The String content containing the title of the activity entry which has been commented on
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been commented on
	 * @return - The String content containing the 'You And X Others Commented On Your Entry' news story
	 */
	public static String getCommentOnYourEntryNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherCommenters, String entryTitle, String activityTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_ACTIVITY_YOU_MANY, numberOfOtherCommenters, entryTitle, activityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Completed The To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoItemTitle - The String content of the title of the activity to-do item which has been completed
	 * @param activityTitle - The String content containing the title of the activity in which the to-do item has been completed
	 * @param userCompletingTodo - The String content of the user name who has completing the to-do item
	 * @return - The String content containing the 'User Completed The To-Do Item' news story
	 */
	public static String getCompleteToDoNewsStory(HomepageUI ui, String todoItemTitle, String activityTitle, String userCompletingTodo) {
		return createNewsStory(ui, Data.COMPLETE_A_TODO_ITEM, todoItemTitle, activityTitle, userCompletingTodo);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Created An Activity' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityTitle - The String content containing the title of the activity which has been created
	 * @param userCreatingActivity - The String content of the user name who has created the activity
	 * @return - The String content containing the 'User Created An Activity' news story
	 */
	public static String getCreateActivityNewsStory(HomepageUI ui, String activityTitle, String userCreatingActivity) {
		return createNewsStory(ui, Data.CREATE_ACTIVITY, activityTitle, null, userCreatingActivity);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Created The ActivityEntryName Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been created
	 * @param activityTitle - The String content containing the title of the activity to which the entry has been added
	 * @param userCreatingEntry - The String content of the user name who has created the entry
	 * @return - The String content containing the 'User Created The ActivityEntryName Entry' news story
	 */
	public static String getCreateEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userCreatingEntry) {
		return createNewsStory(ui, Data.CREATE_ACTIVITY_ENTRY, entryTitle, activityTitle, userCreatingEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Created A To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoItemTitle - The String content of the title of the activity to-do item which has been created
	 * @param activityTitle - The String content containing the title of the activity to which the to-do item has been added
	 * @param userCreatingTodo - The String content of the user name who has created the to-do item
	 * @return - The String content containing the 'User Created A To-Do Item' news story
	 */
	public static String getCreateToDoItemNewsStory(HomepageUI ui, String todoItemTitle, String activityTitle, String userCreatingTodo) {
		return createNewsStory(ui, Data.CREATE_TODO_ITEM, todoItemTitle, activityTitle, userCreatingTodo);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Made An Activity Public' event (appears in the EE for a 'Make Activity Public' event)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userMakingActivityPublic - The String content of the user name who has made the activity public
	 * @return - The String content containing the 'Made An Activity Public' news story
	 */
	public static String getMadeAnActivityPublicNewsStory(HomepageUI ui, String userMakingActivityPublic) {
		return createNewsStory(ui, Data.MADE_AN_ACTIVITY_PUBLIC, null, null, userMakingActivityPublic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Make Activity Public' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityTitle - The String content of the title of the activity which has been made public
	 * @param userMakingActivityPublic - The String content of the user name who has made the activity public
	 * @return - The String content containing the 'Make Activity Public' news story
	 */
	public static String getMakeActivityPublicNewsStory(HomepageUI ui, String activityTitle, String userMakingActivityPublic) {
		return createNewsStory(ui, Data.MAKE_ACTIVITY_PUBLIC, activityTitle, null, userMakingActivityPublic);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Comment On The Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the activity entry which includes comment the mentions
	 * @param activityTitle - The String content of the title of the activity which contains the entry
	 * @param userMentioning - The String content of the user name who has mentioned a user
	 * @return - The String content containing the 'Mentioned You In A Comment On The Entry' news story
	 */
	public static String getMentionedYouInACommentOnTheEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_ACTIVITY_ENTRY_COMMENT, entryTitle, activityTitle, userMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Comment On The To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoTitle - The String content of the activity to-do item which includes comment the mentions
	 * @param activityTitle - The String content of the title of the activity which contains the to-do item
	 * @param userMentioning - The String content of the user name who has mentioned a user
	 * @return - The String content containing the 'Mentioned You In A Comment On The To-Do Item' news story
	 */
	public static String getMentionedYouInACommentOnTheToDoItemNewsStory(HomepageUI ui, String todoTitle, String activityTitle, String userMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_ACTIVITY_ENTRY_COMMENT, todoTitle, activityTitle, userMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In The Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the activity entry which includes the mentions
	 * @param activityTitle - The String content of the title of the activity which contains the entry
	 * @param userMentioning - The String content of the user name who has mentioned a user
	 * @return - The String content containing the 'Mentioned You In The Entry' news story
	 */
	public static String getMentionedYouInTheEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_ACTIVITY_ENTRY, entryTitle, activityTitle, userMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In The To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoTitle - The String content of the activity to-do item which includes the mentions
	 * @param activityTitle - The String content of the title of the activity which contains the to-do item
	 * @param userMentioning - The String content of the user name who has mentioned a user
	 * @return - The String content containing the 'Mentioned You In The To-Do Item' news story
	 */
	public static String getMentionedYouInTheToDoItemNewsStory(HomepageUI ui, String todoTitle, String activityTitle, String userMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_TODO_ITEM, todoTitle, activityTitle, userMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User notified you about the activity entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content of the activity entry that the user is notified about
	 * @param userSendingNotification - The String content of the user name who has sent the notification
	 * @return - The String content containing the 'User notified you about the activity entry' news story
	 */
	public static String getNotifiedYouAboutTheActivityEntryItemNewsStory(HomepageUI ui, String entryTitle, String userSendingNotification) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_ACTIVITY_ENTRY_NOTIFICATION, entryTitle, null, userSendingNotification);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User notified you that you were added to the activity' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityTitle - The String content of the activity that the user has been added to
	 * @param userSendingNotification - The String content of the user name who has sent the notification
	 * @return - The String content containing the 'User notified you that you were added to the activity' news story
	 */
	public static String getNotifiedYouThatYouWereAddedToTheActivityNewsStory(HomepageUI ui, String activityTitle, String userSendingNotification) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_ACTIVITY_MEMBER_ADDED_FOR_ME, activityTitle, null, userSendingNotification);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Reopened The To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoItemTitle - The String content containing the title of the activity to-do item which has been reopened
	 * @param activityTitle - The String content containing the title of the activity which contains the to-do item which has been reopened
	 * @param userReopeningTodo - The String content containing the user name of the user who has reopened the to-do item
	 * @return - The String content containing the 'User Reopened The To-Do Item' news story
	 */
	public static String getReopenToDoItemNewsStory(HomepageUI ui, String todoItemTitle, String activityTitle, String userReopeningTodo) {
		return createNewsStory(ui, Data.REOPEN_TODO_ITEM, todoItemTitle, activityTitle, userReopeningTodo);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Updated Their Own Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been updated
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been updated
	 * @param userUpdatingEntry - The String content containing the user name of the user who has updated the entry
	 * @return - The String content containing the 'User Updated Their Own Entry' news story
	 */
	public static String getUpdateCommentOnEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userUpdatingEntry) {
		return createNewsStory(ui, Data.COMMENT_UPDATE_THEIR_OWN_ACTIVITY_ENTRY, entryTitle, activityTitle, userUpdatingEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Updated The Entry' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param entryTitle - The String content containing the title of the activity entry which has been updated
	 * @param activityTitle - The String content containing the title of the activity which contains the entry which has been updated
	 * @param userUpdatingEntry - The String content containing the user name of the user who has updated the entry
	 * @return - The String content containing the 'User Updated The Entry' news story
	 */
	public static String getUpdateEntryNewsStory(HomepageUI ui, String entryTitle, String activityTitle, String userUpdatingEntry) {
		return createNewsStory(ui, Data.UPDATE_ACTIVITY_ENTRY, entryTitle, activityTitle, userUpdatingEntry);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Updated The To-Do Item' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param todoItemTitle - The String content containing the title of the activity to-do item which has been updated
	 * @param activityTitle - The String content containing the title of the activity which contains the to-do item which has been updated
	 * @param userUpdatingTodo - The String content containing the user name of the user who has updated the to-do item
	 * @return - The String content containing the 'User Updated The To-Do Item' news story
	 */
	public static String getUpdateToDoItemNewsStory(HomepageUI ui, String todoItemTitle, String activityTitle, String userUpdatingTodo) {
		return createNewsStory(ui, Data.UPDATE_ACTIVITY_TODO, todoItemTitle, activityTitle, userUpdatingTodo);
	}
}