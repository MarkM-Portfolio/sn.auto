package com.ibm.conn.auto.util.newsStoryBuilder.dogear;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class DogearNewsStories extends BaseNewsStoryBuilder {
	
	/**
	 * Retrieves the news story corresponding the the 'Created A Bookmark' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param bookmarkTitle - The String content of the title of the bookmark that has been created
	 * @param userNameCreating - The String content of the user name of the user who has created the bookmark
	 * @return - The String content containing the 'Created A Bookmark' news story
	 */
	public static String getCreateBookmarkNewsStory(HomepageUI ui, String bookmarkTitle, String userNameCreating) {
		return createNewsStory(ui, Data.CREATE_BOOKMARK, bookmarkTitle, null, userNameCreating);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User1 Notified User2 About The Following Bookmarks' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameReceivingNotification - The String content of the user name of the user who has received the notification
	 * @param userNameSendingNotification - The String content of the user name of the user who has sent the notification
	 * @return - The String content containing the 'User1 Notified User2 About The Following Bookmarks' news story
	 */
	public static String getNotifiedAboutTheFollowingBookmarksNewsStory_UserNotifiedUser(HomepageUI ui, String userNameReceivingNotification, String userNameSendingNotification) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_SAVED_BOOKMARK_NOTIFY_FOR_ME, userNameReceivingNotification, null, userNameSendingNotification);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Notified You About The Following Bookmarks' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameSendingNotification - The String content of the user name of the user who has sent the notification
	 * @return - The String content containing the 'User Notified You About The Following Bookmarks' news story
	 */
	public static String getNotifiedAboutTheFollowingBookmarksNewsStory_UserNotifiedYou(HomepageUI ui, String userNameSendingNotification) {
		return createNewsStory(ui, Data.MY_NOTIFICATIONS_BOOKMARK_NOTIFY_FOR_ME, null, null, userNameSendingNotification);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Updated The Bookmark' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param bookmarkTitle - The String content of the title of the bookmark that has been updated
	 * @param userNameUpdating - The String content of the user name of the user who has updated the bookmark
	 * @return - The String content containing the 'Updated The Bookmark' news story
	 */
	public static String getUpdateBookmarkNewsStory(HomepageUI ui, String bookmarkTitle, String userNameUpdating) {
		return createNewsStory(ui, Data.UPDATE_BOOKMARK, bookmarkTitle, null, userNameUpdating);
	}
}
