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

import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;

/**
 * Supporting static methods for building BaseActivity, BaseActivityEntry and BaseActivityToDo objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class ActivityBaseBuilder {
	
	/**
	 * 
	 * @param activityName - The name of the Activity.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param isPrivate - a boolean to determine whether or not the returned BaseActivity object is for a 
	 * private, or public standalone Activity. 
	 * true = private
	 * false = public
	 * @return baseActivity - A BaseActivity object
	 */
	public static BaseActivity buildBaseActivity(String activityName, boolean isPrivate){
		
		BaseActivity baseActivity;
		
		if(isPrivate){

			baseActivity = new BaseActivity.Builder(activityName)
											.goal(Data.getData().commonDescription + Helper.genDateBasedRand())
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.shareExternal(false)
											.isPublic(false)
											.build();

		}
		else{

			baseActivity = new BaseActivity.Builder(activityName + Helper.genStrongRand())
											.goal(Data.getData().commonDescription + Helper.genStrongRand())
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.shareExternal(false)
											.isPublic(true)
											.build();
			
		}
		
		return baseActivity;
	}
	
	/**
	 * Creates a BaseActivity instance of an activity to be created - allows a custom tag to be set
	 * 
	 * @param activityName - The String name which is to be assigned to the activity
	 * @param customTag - The String content of the tag to be set to the activity
	 * @param isPrivate - A boolean to determine whether or not the returned BaseActivity object is for a private or public standalone activity.
	 * @return - A BaseActivity object
	 */
	public static BaseActivity buildBaseActivityWithCustomTag(String activityName, String customTag, boolean isPrivate) {
		
		BaseActivity baseActivity = buildBaseActivity(activityName, isPrivate);
		baseActivity.setTags(customTag);
		
		return baseActivity;
	}
	
	/**
	 * Creates a BaseActivity instance of an activity to be created - allows a custom goal / description to be set
	 * 
	 * @param activityName - The String name which is to be assigned to the activity
	 * @param goal - The String content which is to be assigned to the activity
	 * @param isPrivate - A boolean to determine whether or not the returned BaseActivity object is for a private or public standalone activity.
	 * @return baseActivity - A BaseActivity object
	 */
	public static BaseActivity buildBaseActivityWithCustomGoal(String activityName, String goal, boolean isPrivate) {
		
		BaseActivity baseActivity = buildBaseActivity(activityName, isPrivate);
		baseActivity.setGoal(goal);
		
		return baseActivity;
	}
	
	/**
	 * 
	 * @param activityName - The name of the Activity.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param baseCommunity - The community to which the activity will belong
	 * @return baseActivity - A BaseActivity object
	 */
	public static BaseActivity buildCommunityBaseActivity(String activityName, BaseCommunity baseCommunity){
		
		BaseActivity baseActivity;
		
		baseActivity = new BaseActivity.Builder(activityName)
										.goal(Data.getData().commonDescription + Helper.genStrongRand())
										.community(baseCommunity)
										.build();

		return baseActivity;
	}
	
	/**
	 * Creates a community activity BaseActivity instance with custom tag
	 * 
	 * @param activityName - The String content of the name of the community activity
	 * @param baseCommunity - The BaseCommunity instance of the parent community for this activity
	 * @param customTag - The String content of the tag to be assigned to the activity
	 * @return - The BaseActivity object
	 */
	public static BaseActivity buildCommunityBaseActivityWithCustomTag(String activityName, BaseCommunity baseCommunity, String customTag) {
		
		BaseActivity baseActivity = buildCommunityBaseActivity(activityName, baseCommunity);
		baseActivity.setTags(customTag);
		
		return baseActivity;
	}
	
	/**
	 * 
	 * @param entryName - The name of the Entry.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseActivityEntry - A BaseActivityEntry object
	 */
	public static BaseActivityEntry buildBaseActivityEntry(String entryName){

		BaseActivityEntry baseActivityEntry = BaseActivityEntry.builder(entryName)
															.tags(Data.getData().commonTag + Helper.genStrongRand())
															.description(Data.getData().commonDescription + Helper.genStrongRand())
															.build();
		
		return baseActivityEntry;
		
	}
	
	/**
	 * 
	 * @param entryName - The name of the Entry.  Ideally this should be created from the name of the test with a unique identifier, e.g. String testName = ui.startTest();
	 * @param parentActivity - The Activity instance of the parent activity to which the entry will be associated
	 * @param isPrivate - True if the entry being created is to be a private entry, false if it is to be a public entry
	 * @return - The BaseActivityEntry instance
	 */
	public static BaseActivityEntry buildBaseActivityEntry(String entryName, Activity parentActivity, boolean isPrivate) {
		
		BaseActivityEntry baseActivityEntry = buildBaseActivityEntry(entryName);
		baseActivityEntry.setParent(parentActivity);
		baseActivityEntry.setMarkPrivate(isPrivate);
		
		return baseActivityEntry;
	}
	
	/**
	 * Creates a BaseActivityEntry instance with custom tag
	 * 
	 * @param entryName - The String content of the name of the activity entry
	 * @param parentActivity - The Activity instance of the parent activity for the activity entry
	 * @param customTag - The String content of the tag to be assigned to the entry
	 * @param isPrivate - True if the entry being created is to be a private entry, false if it is to be a public entry
	 * @return - The BaseActivityEntry instance
	 */
	public static BaseActivityEntry buildBaseActivityEntryWithCustomTag(String entryName, Activity parentActivity, String customTag, boolean isPrivate) {
		
		BaseActivityEntry baseActivityEntry = buildBaseActivityEntry(entryName, parentActivity, isPrivate);
		baseActivityEntry.setTags(customTag);
		
		return baseActivityEntry;
	}
	
	/**
	 * 
	 * @param todoName - The name of the To-do item.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseActivityTodo - A BaseActivityTodo object
	 */
	public static BaseActivityToDo buildBaseActivityToDo(String todoName){

		BaseActivityToDo baseActivityTodo = BaseActivityToDo.builder(todoName)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.description(Data.getData().commonDescription + Helper.genStrongRand())
													.build();
		return baseActivityTodo;
	}
	
	/**
	 * @param todoName - The name of the To-do item.  Ideally this should be created from the name of the test with a unique identifier, e.g. String testName = ui.startTest();
	 * @param parentActivity - The Activity instance of the parent activity to which the to-do item will be associated
	 * @param isPrivate - True if the to-do item being created is to be a private to-do item, false if it is to be a public to-do item
	 * @return - The BaseActivityToDo instance
	 */
	public static BaseActivityToDo buildBaseActivityToDo(String todoName, Activity parentActivity, boolean isPrivate){

		BaseActivityToDo baseActivityTodo = buildBaseActivityToDo(todoName);
		baseActivityTodo.setParent(parentActivity);
		baseActivityTodo.setMarkPrivate(isPrivate);
		
		return baseActivityTodo;
	}
	
	/**
	 * Creates a BaseActivityToDo instance with a custom tag
	 * 
	 * @param todoName - The String content of the name of the to-do item
	 * @param parentActivity - The Activity instance of the parent activity to which the to-do item will be associated
	 * @param customTag - The String content of the tag to be assigned to the to-do item
	 * @param isPrivate - True if the to-do item being created is to be a private to-do item, false if it is to be a public to-do item
	 * @return - The BaseActivityToDo instance
	 */
	public static BaseActivityToDo buildBaseActivityToDoWithCustomTag(String todoName, Activity parentActivity, String customTag, boolean isPrivate) {
		
		BaseActivityToDo baseActivityTodo = buildBaseActivityToDo(todoName, parentActivity, isPrivate);
		baseActivityTodo.setTags(customTag);
		
		return baseActivityTodo;
	}
}
