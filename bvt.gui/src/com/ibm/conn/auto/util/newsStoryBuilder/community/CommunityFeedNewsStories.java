package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityFeedNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'Add Community Feed' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param feedTitle - The String content containing the title of the community feed that has been added
	 * @param communityTitle - The String content containing the title of the community to which the feed has been added
	 * @param userNameAddingFeed - The String content containing the user name of the user who has added the feed
	 * @return - The String content containing the 'Add Community Feed' news story
	 */
	public static String getAddFeedNewsStory(HomepageUI ui, String feedTitle, String communityTitle, String userNameAddingFeed) {
		return createNewsStory(ui, Data.ADD_COMMUNITY_FEED, feedTitle, communityTitle, userNameAddingFeed);
	}
}