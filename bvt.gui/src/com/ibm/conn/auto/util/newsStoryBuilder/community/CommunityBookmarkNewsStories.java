package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityBookmarkNewsStories extends BaseNewsStoryBuilder {
	
	/**
	 * Retrieves the news story corresponding the the 'Add Bookmark' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param bookmarkTitle - The String content of the bookmark title which has been added to the community
	 * @param communityTitle - The String content of the community title to which the bookmark has been added
	 * @param userNameAddingBookmark - The String content of the user name who created / added the bookmark
	 * @return - The String content containing the 'Add Bookmark' news story
	 */
	public static String getAddBookmarkNewsStory(HomepageUI ui, String bookmarkTitle, String communityTitle, String userNameAddingBookmark) {
		return createNewsStory(ui, Data.ADD_COMMUNITY_BOOKMARK, bookmarkTitle, communityTitle, userNameAddingBookmark);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Bookmark' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param bookmarkTitle - The String content of the bookmark title which has been updated
	 * @param communityTitle - The String content of the community title which contains the bookmark that has been updated
	 * @param userNameAddingBookmark - The String content of the user name who updated the bookmark
	 * @return - The String content containing the 'Update Bookmark' news story
	 */
	public static String getUpdateBookmarkNewsStory(HomepageUI ui, String bookmarkTitle, String communityTitle, String userNameUpdatingBookmark) {
		return createNewsStory(ui, Data.UPDATE_COMMUNITY_BOOKMARK, bookmarkTitle, communityTitle, userNameUpdatingBookmark);
	}
}