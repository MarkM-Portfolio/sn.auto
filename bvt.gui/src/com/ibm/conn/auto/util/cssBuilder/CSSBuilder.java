package com.ibm.conn.auto.util.cssBuilder;

import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Anthony Cox
 */

public class CSSBuilder {

	/**
	 * Generates the CSS selector for the URL Preview Widget in the EE for the specified status update with URL
	 * 
	 * @param url - The String content of the URL for the URL Preview Widget
	 * @return - The String content of the CSS selector for the URL Preview Widget
	 */
	public static String getURLPreviewWidgetSelector_EE(String url) {
		
		return HomepageUIConstants.URLPreview_EE_All.replace("PLACEHOLDER", url);
	}
	
	/**
	 * Generates the CSS selector for the URL Preview Widget in the news feed for the specified status update with URL
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStoryContent - The String content of the news story in which the URL Preview Widget has appeared
	 * @param url - The String content of the URL for the URL Preview Widget
	 * @return - The String content of the CSS selector for the URL Preview Widget
	 */
	public static String getURLPreviewWidgetSelector_NewsFeed(HomepageUI ui, String newsStoryContent, String url) {
		
		return ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_NEWS_FEED, newsStoryContent, url, null);
	}
	
	/**
	 * Generates the CSS selector for the thumbnail image of the URL Preview Widget in the EE for the specified status update with URL
	 * 
	 * @param url - The String content of the URL for the URL Preview Widget
	 * @return - The String content of the CSS selector for the URL Preview Widgets thumbnail image
	 */
	public static String getURLPreviewWidgetThumbnailImageSelector_EE(String url) {
		
		return HomepageUIConstants.URLPreview_EE_ThumbnailImage.replace("PLACEHOLDER", url);
	}
	
	/**
	 * Generates the CSS selector for the thumbnail image of the URL Preview Widget in the news feed for the specified status update with URL
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStoryContent - The String content of the news story in which the URL Preview Widget has appeared
	 * @param url - The String content of the URL for the URL Preview Widget
	 * @return - The String content of the CSS selector for the URL Preview Widgets thumbnail image
	 */
	public static String getURLPreviewWidgetThumbnailImageSelector_NewsFeed(HomepageUI ui, String newsStoryContent, String url) {
		
		return ui.replaceNewsStory(HomepageUIConstants.URL_PREVIEW_NEWS_FEED_THUMBNAIL_IMAGE, newsStoryContent, url, null);
	}
	
}
