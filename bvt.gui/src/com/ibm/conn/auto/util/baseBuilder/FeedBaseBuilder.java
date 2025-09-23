package com.ibm.conn.auto.util.baseBuilder;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;

/**
 * Supporting static methods for building BaseFeed objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class FeedBaseBuilder {
	
	/**
	 * 
	 * @param feedName - The name of the bookmark.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseFeed - A BaseFeed object
	 */
	public static BaseFeed buildBaseFeed(String feedName){

		BaseFeed baseFeed = new BaseFeed.Builder(feedName, Data.getData().FeedsURL_API + Helper.genStrongRand())
											.description(Data.getData().commonDescription + Helper.genStrongRand())
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.build();
		
		return baseFeed;
	}
	
	/**
	 * 
	 * @param feedName - The name of the bookmark.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param customTag - A String object which is the tag which is being followed
	 * @return baseFeed - A BaseFeed object
	 */
	public static BaseFeed buildBaseFeedWithCustomTag(String feedName, String customTag){

		BaseFeed baseFeed = buildBaseFeed(feedName);
		baseFeed.setTags(customTag);
		
		return baseFeed;
	}
}