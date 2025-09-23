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
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;

/**
 * Supporting static methods for building BaseCommunity objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class CommunityBaseBuilder{
	
	/**
	 * This method can be used to create a standard BaseCommunity object
	 * 
	 * @param communityName - The name of the community.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param access - The level of access required by the community
	 * @return baseCommunity - The BaseCommunity object that will be used to create the Community object
	 * 
	 */
	public static BaseCommunity buildBaseCommunity(String communityName, BaseCommunity.Access access){
			
			BaseCommunity baseCommunity;
			
			if (access.equals(Access.PUBLIC) || access.equals(Access.MODERATED)){

				baseCommunity = new BaseCommunity.Builder(communityName)
												.access(access)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + Helper.genStrongRand())
												.build();
				
			}
			else{

				baseCommunity = new BaseCommunity.Builder(communityName)
												.access(access)
												.shareOutside(false)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + Helper.genStrongRand())
												.build();
	
			}
			
			return baseCommunity;
		}
	
	/**
	 * This method can be used to create a standard BaseCommunity object with a custom tag
	 * 
	 * @param communityName - The name of the community.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param access - The level of access required by the community
	 * @param customTag - A uniquely identifiable tag, it is recommended to use: customTag = Helper.genStrongRand();
	 * @return baseCommunity - The BaseCommunity object that will be used to create the Community object
	 */
	public static BaseCommunity buildBaseCommunityWithCustomTag(String communityName, BaseCommunity.Access access, String customTag){

		BaseCommunity baseCommunity = buildBaseCommunity(communityName, access);
		baseCommunity.setTags(customTag);
		return baseCommunity;
	}
	
	/**
	 * This method can be used to create a visitor model BaseCommunity object
	 * In Visitor Model only private communities with external access enabled
	 * can accept external users as members
	 * 
	 * @param testName - The name of the community.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseCommunity - The BaseCommunity object that will be used to create the Community object
	 * 
	 */
	public static BaseCommunity buildVisitorModelBaseCommunity(String communityName){
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(communityName)
											.access(Access.RESTRICTED)
											.shareOutside(true)
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.description(Data.getData().commonDescription + Helper.genStrongRand())
											.build();

		return baseCommunity;
	}

}
