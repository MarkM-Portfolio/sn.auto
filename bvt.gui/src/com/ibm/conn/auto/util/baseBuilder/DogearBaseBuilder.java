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

import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseDogear.Access;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;

/**
 * Supporting static methods for building BaseDogear objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class DogearBaseBuilder {
	
	/**
	 * 
	 * @param bookmarkName - The name of the bookmark.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param bookmarkURL - The URL to which the bookmark links to
	 * @param access - The Access rights for the bookmark (PUBLIC or RESTRICTED)
	 * @return baseDogear - A BaseDogear object
	 */
	public static BaseDogear buildBaseDogear(String bookmarkName, String bookmarkURL, Access access){

		BaseDogear baseDogear  = new BaseDogear.Builder(bookmarkName, bookmarkURL)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + Helper.genStrongRand())
												.access(access)
												.build();
		return baseDogear;
	}
	
	/**
	 * 
	 * @param bookmarkName - The name of the bookmark.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param bookmarkURL - The URL to which the bookmark links to
	 * @param access - The Access rights for the bookmark (PUBLIC or RESTRICTED)
	 * @param customTag - The tag to be assigned to the bookmark
	 * @return baseDogear - A BaseDogear object
	 */
	public static BaseDogear buildBaseDogearWithCustomTag(String bookmarkName, String bookmarkURL, Access access, String customTag){

		BaseDogear baseDogear  = buildBaseDogear(bookmarkName, bookmarkURL, access);
		baseDogear.setTags(customTag);
		
		return baseDogear;
	}
	
	/**
	 * 
	 * @param bookmarkName - The name of the bookmark.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param bookmarkURL - The URL to which the bookmark links to
	 * @param baseCommunity - The BaseCommunity object to which the bookmark belongs
	 * @return baseDogear - A BaseDogear object
	 */
	public static BaseDogear buildCommunityBaseDogear(String bookmarkName, String bookmarkURL, BaseCommunity baseCommunity){

		BaseDogear baseDogear  = new BaseDogear.Builder(bookmarkName, bookmarkURL)
												.community(baseCommunity)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + Helper.genStrongRand())
												.build();
		return baseDogear;	
	}
	
	/**
	 * Creates a community bookmark with a custom tag set
	 * 
	 * @param bookmarkName - The String content of the name to be given to the bookmark
	 * @param bookmarkURL - The String content of the URL to which the bookmark links to
	 * @param baseCommunity - The BaseCommunity instance of the community to which the bookmark belongs
	 * @param customTag - The String content of the custom tag to be assigned to the bookmark
	 * @return - A BaseDogear object
	 */
	public static BaseDogear buildCommunityBaseDogearWithCustomTag(String bookmarkName, String bookmarkURL, BaseCommunity baseCommunity, String customTag) {
		
		BaseDogear baseDogear = buildCommunityBaseDogear(bookmarkName, bookmarkURL, baseCommunity);
		baseDogear.setTags(customTag);
		
		return baseDogear;
	}
}