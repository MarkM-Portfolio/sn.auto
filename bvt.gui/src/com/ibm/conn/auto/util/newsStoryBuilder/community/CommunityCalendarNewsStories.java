package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityCalendarNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'User Commented On The Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @param userNameCommenting - The String content of the user name who posted / added the comment
	 * @return - The String content containing the 'User Commented On The Event' news story
	 */
	public static String getCommentOnTheCalendarEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.CALENDAR_ENTRY_COMMENT, calendarEventTitle, communityTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Comment On Their Own Calendar Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @param userNameCommenting - The String content of the user name who posted / added the comment
	 * @return - The String content containing the 'Comment On Their Own Calendar Event' news story
	 */
	public static String getCommentOnTheirOwnCalendarEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_ON_THEIR_OWN_EVENT, calendarEventTitle, communityTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 And User2 Commented On Your Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param previousUserCommenting - The String content of the user name of the previous user who posted / added a comment
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @param currentUserCommenting - The String content of the user name of the most recent user who posted / added a comment
	 * @return - The String content containing the 'User1 And User2 Commented On Your Event' news story
	 */
	public static String getCommentOnYourCalendarEventNewsStory_TwoUsers(HomepageUI ui, String previousUserCommenting, String calendarEventTitle, String communityTitle, String currentUserCommenting) {
		return createNewsStory(ui, Data.CALENDAR_YOUR_ENTRY_COMMENT_TWO_COMMENTERS, previousUserCommenting, calendarEventTitle, communityTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @param userNameCommenting - The String content of the user name who posted / added the comment
	 * @return - The String content containing the 'User Commented On Your Event' news story
	 */
	public static String getCommentOnYourCalendarEventNewsStory_User(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.CALENDAR_YOUR_ENTRY_COMMENT, calendarEventTitle, communityTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Commented On Your Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content of the user name of the most recent user who posted / added the comment
	 * @param numberOfOtherCommenters - The String content of the number of other users who have posted / added comments
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @return - The String content containing the 'User And X Others Commented On Your Event' news story
	 */
	public static String getCommentOnYourCalendarEventNewsStory_UserAndMany(HomepageUI ui, String currentUserCommenting, String numberOfOtherCommenters, String calendarEventTitle, String communityTitle) {
		return createNewsStory(ui, Data.CALENDAR_YOUR_ENTRY_COMMENT_MANY, numberOfOtherCommenters, calendarEventTitle, communityTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @param userNameCommenting - The String content of the user name who posted / added the comment
	 * @return - The String content containing the 'User And You Commented On Your Event' news story
	 */
	public static String getCommentOnYourCalendarEventNewsStory_UserAndYou(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameCommenting) {
		return createNewsStory(ui, Data.CALENDAR_YOUR_ENTRY_COMMENT_YOU_OTHER, calendarEventTitle, communityTitle, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @return - The String content containing the 'You Commented On Your Event' news story
	 */
	public static String getCommentOnYourCalendarEventNewsStory_You(HomepageUI ui, String calendarEventTitle, String communityTitle) {
		return createNewsStory(ui, Data.CALENDAR_YOUR_ENTRY_COMMENT_YOU, calendarEventTitle, communityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherCommenters - The String content of the number of other users who have posted / added comments
	 * @param calendarEventTitle - The String content of the calendar event title which has been commented on
	 * @param communityTitle - The String content of the community title which contains the calendar event that has been commented on
	 * @return - The String content containing the 'You And X Others Commented On Your Event' news story
	 */
	public static String getCommentOnYourCalendarEventNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherCommenters, String calendarEventTitle, String communityTitle) {
		return createNewsStory(ui, Data.CALENDAR_YOUR_ENTRY_COMMENT_YOU_MANY, numberOfOtherCommenters, calendarEventTitle, communityTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Calendar Event Single Instance' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been added to the community
	 * @param communityTitle - The String content of the community title to which the calendar event has been added
	 * @param userNameAddingEvent - The String content of the user name who created / added the calendar event
	 * @return - The String content containing the 'Create Calendar Event Single Instance' news story
	 */
	public static String getCreateCalendarEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameAddingEvent) {
		return createNewsStory(ui, Data.CREATE_COMMUNITY_CALENDAR_EVENT, calendarEventTitle, communityTitle, userNameAddingEvent);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Repeating Calendar Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been added to the community
	 * @param communityTitle - The String content of the community title to which the calendar event has been added
	 * @param userNameAddingEvent - The String content of the user name who created / added the calendar event
	 * @return - The String content containing the 'Create Repeating Calendar Event' news story
	 */
	public static String getCreateRepeatingCalendarEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameAddingEvent) {
		return createNewsStory(ui, Data.CREATE_COMMUNITY_CALENDAR_REPEATING_EVENT, calendarEventTitle, communityTitle, userNameAddingEvent);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Comment On The Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title in which the comment with mentions has been posted
	 * @param communityTitle - The String content of the community title which contains the calendar event
	 * @param userNameAddingEvent - The String content of the user name who has posted the comment with mentions
	 * @return - The String content containing the 'Mentioned You In A Comment On The Event' news story
	 */
	public static String getMentionedYouInACommentOnTheEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_EVENT_COMMENT, calendarEventTitle, communityTitle, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In The Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title in which a user has been mentioned
	 * @param communityTitle - The String content of the community title which contains the calendar event
	 * @param userNameAddingEvent - The String content of the user name who has posted the mentions
	 * @return - The String content containing the 'Mentioned You In The Event' news story
	 */
	public static String getMentionedYouInTheEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_EVENT, calendarEventTitle, communityTitle, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Calendar Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been updated
	 * @param communityTitle - The String content of the community title in which the calendar event has been updated
	 * @param userNameUpdatingEvent - The String content of the user name who updated the calendar event
	 * @return - The String content containing the 'Update Calendar Event' news story
	 */
	public static String getUpdateCalendarEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameUpdatingEvent) {
		return createNewsStory(ui, Data.UPDATE_COMMUNITY_CALENDAR_EVENT, calendarEventTitle, communityTitle, userNameUpdatingEvent);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated An Instance Of The Repeating Calendar Event' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param calendarEventTitle - The String content of the calendar event title which has been updated
	 * @param communityTitle - The String content of the community title in which the calendar event has been updated
	 * @param userNameUpdatingEvent - The String content of the user name who updated the calendar event
	 * @return - The String content containing the 'Updated An Instance Of The Repeating Calendar Event' news story
	 */
	public static String getUpdateInstanceOfRepeatingCalendarEventNewsStory(HomepageUI ui, String calendarEventTitle, String communityTitle, String userNameUpdatingEvent) {
		return createNewsStory(ui, Data.UPDATE_COMMUNITY_CALENDAR_EVENT_INSTANCE, calendarEventTitle, communityTitle, userNameUpdatingEvent);
	}
}