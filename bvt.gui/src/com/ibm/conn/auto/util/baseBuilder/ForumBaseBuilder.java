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
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/**
 * Supporting static methods for building BaseForum and BaseForumTopic objects
 * The objective of this class is to reduce the number of lines
 * of code in test cases by moving the building of standard
 * versions of these objects
 * 
 * @author Patrick Doherty
 *
 */
public class ForumBaseBuilder {
	
	/**
	 * 
	 * @param forumName - The name of the forum.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @return baseForum - A BaseForum object
	 */
	public static BaseForum buildBaseForum(String forumName){

		BaseForum baseForum = new BaseForum.Builder(forumName)
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.description(Data.getData().commonDescription + Helper.genStrongRand())
											.build();
		return baseForum;
	}
	
	/**
	 * Creates a BaseForum instance of a new forum with custom tags
	 * 
	 * @param forumName - The String content of the name of the forum
	 * @param customTag - The String content of the custom tag to be set to the forum
	 * @return - The BaseForum object
	 */
	public static BaseForum buildBaseForumWithCustomTag(String forumName, String customTag) {
		
		BaseForum baseForum = buildBaseForum(forumName);
		baseForum.setTags(customTag);
		return baseForum;
	}
	
	/**
	 * 
	 * @param topicName - The name of the forum topic.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param forum - A Forum object to which the topic will belong
	 * @return baseForumTopic - A BaseForumTopic object
	 */
	public static BaseForumTopic buildBaseForumTopic(String topicName, Forum forum){

		BaseForumTopic baseForumTopic = new BaseForumTopic.Builder(topicName)
														.parentForum(forum)
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.build();
		return baseForumTopic;
	}
	
	/**
	 * Creates a BaseForumTopic instance of a forum topic with custom tags
	 * 
	 * @param topicName - The String content of the name to be given to the forum topic
	 * @param forum - The Forum instance of the parent forum to which the forum topic will belong
	 * @param customTag - The String content of the custom tag to be assigned to the forum topic
	 * @return - A BaseForumTopic object
	 */
	public static BaseForumTopic buildBaseForumTopicWithCustomTag(String topicName, Forum forum, String customTag) {
		
		BaseForumTopic baseForumTopic = buildBaseForumTopic(topicName, forum);
		baseForumTopic.setTags(customTag);
		
		return baseForumTopic;
	}

	/**
	 * 
	 * @param topicName - The name of the forum topic.  Ideally this should be created from the name of the test with
	 * a unique identifier, e.g. String testName = ui.startTest();
	 * 							 testName = testName + Helper.genStrongRand();
	 * @param parentCommunity - The BaseCommunity instance of the community to which the topic will belong
	 * @return baseForumTopic - A BaseTopic object
	 */
	public static BaseForumTopic buildCommunityBaseForumTopic(String topicName, BaseCommunity parentCommunity) {

		BaseForumTopic baseForumTopic = new BaseForumTopic.Builder(topicName)
														.partOfCommunity(parentCommunity)
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.build();
		return baseForumTopic;
	}
	
	/**
	 * Creates a BaseForumTopic instance of a community forum topic with custom tags
	 * 
	 * @param topicName - The String content of the name to be given to the forum topic
	 * @param parentCommunity - The BaseCommunity instance of the community to which the forum topic will belong
	 * @param customTag - The String content of the custom tag to be assigned to the forum topic
	 * @return - A BaseForumTopic object
	 */
	public static BaseForumTopic buildCommunityBaseForumTopicWithCustomTag(String topicName, BaseCommunity parentCommunity, String customTag) {
		
		BaseForumTopic baseForumTopic = buildCommunityBaseForumTopic(topicName, parentCommunity);
		baseForumTopic.setTags(customTag);
		
		return baseForumTopic;
	}
	
	/**
	 * Creates a BaseForumTopic instance for a community forum topic - allows the parent community and parent forum to be set
	 * 
	 * @param topicName - The String content to make up the name of the forum topic
	 * @param parentCommunity - The BaseCommunity instance of the community to which the topic will belong
	 * @param parentForum - The Forum instance of the community forum to which the topic will belong
	 * @return - A BaseTopic object
	 */
	public static BaseForumTopic buildCommunityBaseForumTopicWithCustomParentForum(String topicName, BaseCommunity parentCommunity, Forum parentForum) {
		
		BaseForumTopic baseForumTopic = buildCommunityBaseForumTopic(topicName, parentCommunity);
		baseForumTopic.setParentForum_API(parentForum);
		
		return baseForumTopic;
	}
	
	/**
	 * Creates a BaseForumTopic instance of a community forum topic with custom tags - allows the parent community and parent forum to be set
	 * 
	 * @param topicName - The String content of the name to be given to the forum topic
	 * @param parentCommunity - The BaseCommunity instance of the community to which the forum topic will belong
	 * @param parentForum - The Forum instance of the community forum to which the forum topic will belong
	 * @param customTag - The String content of the custom tag to be assigned to the forum topic
	 * @return - A BaseForumTopic object
	 */
	public static BaseForumTopic buildCommunityBaseForumTopicWithCustomParentForumAndCustomTag(String topicName, BaseCommunity parentCommunity, Forum parentForum, String customTag) {
		
		BaseForumTopic baseForumTopic = buildCommunityBaseForumTopicWithCustomParentForum(topicName, parentCommunity, parentForum);
		baseForumTopic.setTags(customTag);
		
		return baseForumTopic;
	}
}
