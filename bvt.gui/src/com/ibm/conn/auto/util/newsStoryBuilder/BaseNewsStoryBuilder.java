package com.ibm.conn.auto.util.newsStoryBuilder;

import com.ibm.conn.auto.webui.HomepageUI;

public abstract class BaseNewsStoryBuilder {

	/**
	 * Creates the relevant news story using all components provided (does NOT handle the REPLACE_THIS_TOO String replacement in a news story)
	 * 
	 * @param ui - The HomepageUI instance to invoke the replaceNewsStory() method
	 * @param dataComponent - The String content which corresponds to the news story String retrieved from the Data class
	 * @param placeholderComponent - The String content which is to replace the PLACEHOLDER String in the Data-retrieved news story
	 * @param replaceThisComponent - The String content which is to replace the REPLACE_THIS String in the Data-retrieved news story
	 * @param userComponent - The String content which is to replace the USER String in the Data-retrieved news story
	 * @return - The String content for the news story with all relevant components in place 
	 */
	protected static String createNewsStory(HomepageUI ui, String dataComponent, String placeholderComponent, String replaceThisComponent, String userComponent) {
		return ui.replaceNewsStory(dataComponent, placeholderComponent, replaceThisComponent, userComponent);
	}
	
	/**
	 * Creates the relevant news story using all components provided (also handles the REPLACE_THIS_TOO String replacement in a news story)
	 * 
	 * @param ui - The HomepageUI instance to invoke the replaceNewsStory() method
	 * @param dataComponent - The String content which corresponds to the news story String retrieved from the Data class
	 * @param placeholderComponent - The String content which is to replace the PLACEHOLDER String in the Data-retrieved news story
	 * @param replaceThisComponent - The String content which is to replace the REPLACE_THIS String in the Data-retrieved news story
	 * @param replaceThisTooComponent - The String content which is to replace the REPLACE_THIS_TOO String in the Data-retrieved news story
	 * @param userComponent - The String content which is to replace the USER String in the Data-retrieved news story
	 * @return - The String content for the news story with all relevant components in place 
	 */
	protected static String createNewsStory(HomepageUI ui, String dataComponent, String placeholderComponent, String replaceThisComponent, String replaceThisTooComponent, String userComponent) {
		return ui.replaceNewsStory(dataComponent, placeholderComponent, replaceThisComponent, replaceThisTooComponent, userComponent);
	}
}
