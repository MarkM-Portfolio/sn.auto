package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityActivityNewsStories extends BaseNewsStoryBuilder {

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
		return ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, entryTitle, activityTitle, userCommentingOnEntry);
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
		return ActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, entryTitle, activityTitle, userCommentingOnEntry);
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
		return ActivityNewsStories.getCompleteToDoNewsStory(ui, todoItemTitle, activityTitle, userCompletingTodo);
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
		return ActivityNewsStories.getCreateActivityNewsStory(ui, activityTitle, userCreatingActivity);
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
		return ActivityNewsStories.getCreateEntryNewsStory(ui, entryTitle, activityTitle, userCreatingEntry);
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
		return ActivityNewsStories.getCreateToDoItemNewsStory(ui, todoItemTitle, activityTitle, userCreatingTodo);
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
		return ActivityNewsStories.getMentionedYouInACommentOnTheEntryNewsStory(ui, entryTitle, activityTitle, userMentioning);
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
		return ActivityNewsStories.getMentionedYouInACommentOnTheToDoItemNewsStory(ui, todoTitle, activityTitle, userMentioning);
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
		return ActivityNewsStories.getMentionedYouInTheEntryNewsStory(ui, entryTitle, activityTitle, userMentioning);
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
		return ActivityNewsStories.getMentionedYouInTheToDoItemNewsStory(ui, todoTitle, activityTitle, userMentioning);
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
		return ActivityNewsStories.getReopenToDoItemNewsStory(ui, todoItemTitle, activityTitle, userReopeningTodo);
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
		return ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, entryTitle, activityTitle, userUpdatingEntry);
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
		return ActivityNewsStories.getUpdateEntryNewsStory(ui, entryTitle, activityTitle, userUpdatingEntry);
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
		return ActivityNewsStories.getUpdateToDoItemNewsStory(ui, todoItemTitle, activityTitle, userUpdatingTodo);
	}
}
