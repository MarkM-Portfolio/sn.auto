package com.ibm.conn.auto.util.eventBuilder.community;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016   		                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class CommunityForumEvents {

	private static Logger log = LoggerFactory.getLogger(CommunityForumEvents.class);
	
	/**
	 * Create a new forum in the community
	 * 
	 * @param community - The Community instance of the community in which the forum will be created
	 * @param serverURL - The Server URL obtained by using APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()) in a test case
	 * @param userCreatingForum - The User instance of the user creating the forum
	 * @param communityAPIUserCreatingForum - The APICommunityHandler instance of the user creating the forum
	 * @param forumsAPIUserCreatingForum - The APIForumsHandler instance of the user creating the forum
	 * @param forumToBeCreated - The BaseForum instance of the forum to be created
	 * @return - The Forum instance of the created forum
	 */
	public static Forum createForum(Community community, String serverURL, User userCreatingForum, APICommunitiesHandler communityAPIUserCreatingForum,
										APIForumsHandler forumsAPIUserCreatingForum, BaseForum forumToBeCreated) {
		log.info("INFO: Now creating a new forum in the community with title: " + community.getTitle());
		
		// Retrieve the community UUID
		String communityUUID = communityAPIUserCreatingForum.getCommunityUUID(community);
		log.info("INFO: The community UUID has been retrieved: " + communityUUID);
		
		// Create the URL required to create the forum in a community
		String communityForumURL = serverURL + "/forums/atom/forums?" + communityUUID;
		log.info("INFO: The URL required to create the community forum has been created: " + communityForumURL);
		
		log.info("INFO: " + userCreatingForum.getDisplayName() + " will now create a community forum with title: " + forumToBeCreated.getName());
		return forumsAPIUserCreatingForum.createCommunityForum(communityForumURL, forumToBeCreated);
	}
	
	/**
	 * Allows a single specified user to follow a forum
	 * 
	 * @param forumToBeFollowed - The Forum instance of the forum to be followed
	 * @param userToFollowForum - The User instance of the user following the forum
	 * @param apiUserToFollowForum - The APIForumsHandler instance of the user following the forum
	 */
	public static void followForumSingleUser(Forum forumToBeFollowed, User userToFollowForum, APIForumsHandler apiUserToFollowForum) {
		
		// Follow the community forum as the specified user
		ForumEvents.followForumSingleUser(forumToBeFollowed, userToFollowForum, apiUserToFollowForum);
	}
	
	/**
	 * Edits / updates the description of a community forum
	 * 
	 * @param forum - The Forum instance of the forum which will be edited
	 * @param forumDescriptionEdit - A String object which contains the content of the forum description edit
	 * @param forumEditor - The User instance of the user who will edit the forum
	 * @param apiForumEditor - The APIForumsHandler instance of the user who will edit the forum
	 */
	public static void editForumDescription(Forum forum, String forumDescriptionEdit, User forumEditor, APIForumsHandler apiForumEditor) {
		
		// Update the description of the community forum
		ForumEvents.editForumDescription(forumEditor, apiForumEditor, forum, forumDescriptionEdit);
	}
	
	/**
	 * Create a forum with a specified user as a follower
	 * 
	 * @param community - The Community instance of the community in which the forum will be created
	 * @param serverURL - The Server URL obtained by using APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()) in a test case
	 * @param userCreatingForum - The User instance of the user creating the forum
	 * @param communityAPIUserCreatingForum - The APICommunityHandler instance of the user creating the forum
	 * @param forumsAPIUserCreatingForum - The APIForumsHandler instance of the user creating the forum
	 * @param forumToBeCreated - The BaseForum instance of the forum to be created
	 * @param userToFollowForum - The User instance of the user following the forum
	 * @param apiUserToFollowForum - The APIForumsHandler instance of the user following the forum
	 * @return - The Forum instance of the created forum
	 */
	public static Forum createForumWithOneFollower(Community community, String serverURL, User userCreatingForum, APICommunitiesHandler communityAPIUserCreatingForum,
													APIForumsHandler forumsAPIUserCreatingForum, BaseForum forumToBeCreated, User userToFollowForum, APIForumsHandler apiUserToFollowForum) {
		// Create the new community forum
		Forum communityForum = createForum(community, serverURL, userCreatingForum, communityAPIUserCreatingForum, forumsAPIUserCreatingForum, forumToBeCreated);
				
		// Have the specified user follow the forum
		followForumSingleUser(communityForum, userToFollowForum, apiUserToFollowForum);
		
		return communityForum;
	}
	
	/**
	 * Create a forum with a specified user as a follower and edit the description of the forum
	 * 
	 * @param community - The Community instance of the community in which the forum will be created
	 * @param serverURL - The Server URL obtained by using APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()) in a test case
	 * @param userCreatingForum - The User instance of the user creating the forum
	 * @param communityAPIUserCreatingForum - The APICommunityHandler instance of the user creating the forum
	 * @param forumsAPIUserCreatingForum - The APIForumsHandler instance of the user creating the forum
	 * @param forumToBeCreated - The BaseForum instance of the forum to be created
	 * @param userToFollowForum - The User instance of the user following the forum
	 * @param apiUserToFollowForum - The APIForumsHandler instance of the user following the forum
	 * @param editedForumDescription - A String object which contains the content of the forum description edit
	 * @return - The Forum instance of the created forum
	 */
	public static Forum createForumWithOneFollowerAndEditDescription(Community community, String serverURL, User userCreatingForum, APICommunitiesHandler communityAPIUserCreatingForum,
																		APIForumsHandler forumsAPIUserCreatingForum, BaseForum forumToBeCreated, User userToFollowForum,
																		APIForumsHandler apiUserToFollowForum, String editedForumDescription) {
		// Create the new community forum and have the specified user follow the forum
		Forum communityForum = createForumWithOneFollower(community, serverURL, userCreatingForum, communityAPIUserCreatingForum, forumsAPIUserCreatingForum, forumToBeCreated,
															userToFollowForum, apiUserToFollowForum);
		// Edit the description of the forum
		editForumDescription(communityForum, editedForumDescription, userCreatingForum, forumsAPIUserCreatingForum);
		
		return communityForum;
	}
	
	/**
	 * Creates a new forum topic in a community forum
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopic(User topicCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic) {

		log.info("INFO: " + topicCreator.getDisplayName() + " will now create a forum topic in the community with title: " + community.getTitle());
		ForumTopic forumTopic = apiTopicCreator.CreateForumTopic(community, baseForumTopic);

		log.info("INFO: Verify that the new forum topic was created successfully");
		Assert.assertNotNull(forumTopic, "ERROR: The forum topic was NOT created successfully and was returned as null");
		
		return forumTopic;
	}
	
	/**
	 * Creates a new forum topic in the specified community forum
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicInSpecifiedForum(User topicCreator, APIForumsHandler apiTopicCreator, BaseForumTopic baseForumTopic) {
		
		// Create the forum topic in the specified community forum
		return ForumEvents.createForumTopic(topicCreator, apiTopicCreator, baseForumTopic);
	}
	
	/**
	 * Creates a new forum topic, with mentions to the specified user in the forum topic description, in a community forum
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the forum topic description
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithMentions(Community community, BaseForumTopic baseForumTopic, Mentions mentions, User topicCreator, APICommunitiesHandler apiTopicCreator) {
		
		log.info("INFO: " + topicCreator.getDisplayName() + " will now create a community forum topic with mentions to: " + mentions.getUserToMention().getDisplayName());
		ForumTopic communityForumTopic = apiTopicCreator.createForumTopicMentions(community, baseForumTopic, mentions);
		
		log.info("INFO: Verify that the forum topic with mentions was created successfully");
		Assert.assertNotNull(communityForumTopic, "ERROR: The forum topic with mentions was NOT created successfully and was returned as null");
		
		return communityForumTopic;
	}
	
	/**
	 * Creates a new forum topic in the specified community forum and then edits the description of that topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating and editing the forum topic in the community forum
	 * @param apiTopicCreator - The APIForumsHandler instance of the user who is creating and editing the forum topic in the community forum
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created and edited
	 * @param topicEdit - The String content of the new description to be set to the forum topic
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicInSpecifiedForumAndEditDescription(User topicCreator, APIForumsHandler apiTopicCreator, BaseForumTopic baseForumTopic, String topicEdit) {
		
		// Create the forum topic in the specified community forum
		ForumTopic forumTopic = createForumTopicInSpecifiedForum(topicCreator, apiTopicCreator, baseForumTopic);
		
		// Edit the description of the forum topic
		editForumTopicDescription(topicCreator, apiTopicCreator, forumTopic, topicEdit);
		
		return forumTopic;
	}
	
	/**
	 * Creates a new forum topic in the specified community forum and then likes / recommends the topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating and liking the forum topic in the community forum
	 * @param apiTopicCreator - The APIForumsHandler instance of the user who is creating and liking the forum topic in the community forum
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created and liked
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicInSpecifiedForumAndLikeTopic(User topicCreator, APIForumsHandler apiTopicCreator, BaseForumTopic baseForumTopic) {
		
		// Create the forum topic in the specified community forum
		ForumTopic forumTopic = createForumTopicInSpecifiedForum(topicCreator, apiTopicCreator, baseForumTopic);
		
		// Like / recommend the forum topic
		likeForumTopic(topicCreator, apiTopicCreator, forumTopic);
		
		return forumTopic;
	}
	
	/**
	 * Creates a new forum topic in the specified community forum, posts a reply to that topic and then edits the reply
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic and creating and editing the reply on the topic
	 * @param apiTopicCreator - The APIForumsHandler instance of the user who is creating the forum topic and creating and editing the reply on the topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param topicReply - The String content of the reply to be posted to the forum topic
	 * @param replyEdit - The String content of the updated reply to be set to the forum topic
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicInSpecifiedForumAndAddReplyAndEditReply(User topicCreator, APIForumsHandler apiTopicCreator, BaseForumTopic baseForumTopic, String topicReply, String replyEdit) {
		
		// Create the forum topic in the specified community forum
		ForumTopic forumTopic = createForumTopicInSpecifiedForum(topicCreator, apiTopicCreator, baseForumTopic);
		
		// Create a forum topic reply
		ForumReply forumTopicReply = createForumTopicReply(topicCreator, apiTopicCreator, forumTopic, topicReply);
		
		// Edit the reply on the topic
		updateForumTopicReply(topicCreator, apiTopicCreator, forumTopicReply, replyEdit);
		
		return forumTopic;
	}
	
	/**
	 * Creates a forum topic in the specified community forum and adds a reply to that topic and likes / recommends the reply
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param topicReply - The String content of the reply to be posted and liked on the topic
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicInSpecifiedForumAndAddReplyAndLikeReply(User topicCreator, APIForumsHandler apiTopicCreator, BaseForumTopic baseForumTopic, String topicReply) {
		
		// Create the forum topic in the specified community forum
		ForumTopic forumTopic = createForumTopicInSpecifiedForum(topicCreator, apiTopicCreator, baseForumTopic);
		
		// Create a forum topic reply
		ForumReply forumTopicReply = createForumTopicReply(topicCreator, apiTopicCreator, forumTopic, topicReply);
		
		// Like / recommend the topic reply
		likeForumTopicReply(topicCreator, apiTopicCreator, forumTopicReply);
		
		return forumTopic;
	}
	
	/**
	 * Creates a reply to a community forum topic
	 * 
	 * @param topicReplyCreator - The User instance of the user who is creating the forum topic reply in the community forum
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who is creating the forum topic reply in the community forum
	 * @param forumTopic - The ForumTopic instance of the forum topic to which the reply will be posted
	 * @param topicReply - The String content of the reply to be posted to the forum topic
	 * @return - The ForumReply instance of the reply
	 */
	public static ForumReply createForumTopicReply(User topicReplyCreator, APIForumsHandler apiTopicReplyCreator, ForumTopic forumTopic, String topicReply) {
		
		// Create a forum topic reply
		return ForumEvents.createForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply);
	}
	
	/**
	 * Likes a forum topic reply
	 * 
	 * @param userLikingReply - The User instance of the user liking the reply
	 * @param apiUserLikingReply - The APIForumsHandler instance of the user liking the reply
	 * @param forumReply - The ForumReply instance of the topic reply to be liked
	 */
	public static void likeForumTopicReply(User userLikingReply, APIForumsHandler apiUserLikingReply, ForumReply forumReply) {
		
		// Like / recommend the forum topic reply
		ForumEvents.likeForumTopicReply(userLikingReply, apiUserLikingReply, forumReply);
	}
	
	/**
	 * Creates a new forum topic and then posts a reply to that topic
	 * 
	 * @param topicReplyCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who is creating the forum topic reply in the community forum
	 * @param topicReply - A String object containing the content of the forum topic reply
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReply(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicReplyCreator, String topicReply) {
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(topicReplyCreator, apiTopicCreator, community, baseForumTopic);
		
		// Create a forum topic reply
		createForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply);
		
		return forumTopic;
	}
	
	/**
	 * Creates a forum topic and allows a single user to follow the topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param topicFollower - The User instance of the user who is following the forum topic in the community forum
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who is following the forum topic in the community forum
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollower(User topicCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, User topicFollower, APIForumsHandler apiTopicFollower){
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(topicCreator, apiTopicCreator, community, baseForumTopic);
		
		// Have the user follow the topic
		ForumEvents.followForumTopicSingleUser(forumTopic, topicFollower, apiTopicFollower);
		
		return forumTopic;
	}
	
	/**
	 * Edits / updates the description of a forum topic
	 * 
	 * @param userEditingTopic - The User instance of the user editing the forum topic description
	 * @param apiUserEditingTopic - The APIForumsHandler instance of the user editing the forum topic description
	 * @param forumTopic - The ForumTopic instance of the forum topic to be edited
	 * @param topicEdit - The String content of the new description to be set to the forum topic
	 */
	public static void editForumTopicDescription(User userEditingTopic, APIForumsHandler apiUserEditingTopic, ForumTopic forumTopic, String topicEdit) {
		
		// Edit the topic description
		ForumEvents.editForumTopicDescription(userEditingTopic, apiUserEditingTopic, forumTopic, topicEdit);
	}
	
	/**
	 * Creates a new forum topic and then edits the description of that topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiForumOwner - The APIForumsHandler instance of the user who is editing the forum topic description in the community forum
	 * @param topicEdit - A String object which contains the content of the edit to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndEditDescription(User topicCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiForumOwner, String topicEdit){
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(topicCreator, apiTopicCreator, community, baseForumTopic);
		
		// Edit the topic description
		editForumTopicDescription(topicCreator, apiForumOwner, forumTopic, topicEdit);

		return forumTopic;
	}
	
	/**
	 * Creates a forum topic, allows one user to follow the topic and then edits the description of the topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiForumOwner - The APIForumsHandler instance of the user who is editing the forum topic description in the community forum
	 * @param topicFollower - The User instance of the user who is following the forum topic in the community forum
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who is following the forum topic in the community forum
	 * @param topicEdit - A String object which contains the content of the edit to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createTopicWithOneFollowerAndEditDescription(User topicCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiForumOwner, User topicFollower, APIForumsHandler apiTopicFollower, String topicEdit){
		
		//Create a forum topic in the community and have the second user follow it
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicCreator, apiTopicCreator, community, baseForumTopic, topicFollower, apiTopicFollower);
		
		//Edit the topic description
		editForumTopicDescription(topicCreator, apiForumOwner, forumTopic, topicEdit);

		return forumTopic;
	}
	
	/**
	 * Creates a forum topic, allows a single user to follow that topic and then posts a reply to the topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who will add the reply to the forum topic
	 * @param topicFollower - The User instance of the user who is following the forum topic in the community forum
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who is following the forum topic in the community forum
	 * @param topicReply - A String object which contains the content of the topic reply to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createTopicWithOneFollowerAndAddReply(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicReplyCreator, User topicFollower, APIForumsHandler apiTopicFollower, String topicReply){
		
		// Create a forum topic in the community and have the second user follow the topic
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicReplyCreator, apiTopicCreator, community, baseForumTopic, topicFollower, apiTopicFollower);
		
		// Create a forum topic reply
		createForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply);
		
		return forumTopic;
	}
	
	/**
	 * Edits / updates a reply posted to a forum topic
	 * 
	 * @param userUpdatingReply - The User instance of the user updating the forum topic reply
	 * @param apiUserUpdatingReply - The APIForumsHandler instance of the user updating the forum topic reply
	 * @param forumReply - The ForumReply instance of the reply to be updated
	 * @param replyEdit - The String content of the new reply to be assigned
	 */
	public static void updateForumTopicReply(User userUpdatingReply, APIForumsHandler apiUserUpdatingReply, ForumReply forumReply, String replyEdit) {
		
		// Edit the reply in the forum topic
		ForumEvents.updateForumTopicReply(userUpdatingReply, apiUserUpdatingReply, forumReply, replyEdit);
	}
	
	/**
	 * Creates a reply to a forum topic and then edits / updates the reply
	 * 
	 * @param userCreatingReply - The User instance of the user who will create the forum topic comment
	 * @param apiUserCreatingReply - The APIForumsHandler instance of the user who will create the forum topic comment
	 * @param forumTopic - The ForumTopic instance to which the comment will be added
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @param replyEdit - The String object which contains the content of the edit
	 * @return - A ForumReply instance of the forum topic reply
	 */
	public static ForumReply createForumTopicReplyAndEditReply(User userCreatingReply, APIForumsHandler apiUserCreatingReply, ForumTopic forumTopic, String topicReply, String replyEdit) {
		
		// Post a reply to the forum topic and update the reply
		return ForumEvents.createForumTopicReplyAndEditReply(userCreatingReply, apiUserCreatingReply, forumTopic, topicReply, replyEdit);
	}
	
	/**
	 * Creates a forum topic, adds a reply to the topic and then edits / updates that reply
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who will add the reply to the forum topic
	 * @param topicReply - A String object which contains the content of the topic reply to be made
	 * @param replyEdit - A String object which contains the content of the topic reply edit to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReplyAndEditReply(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicReplyCreator, String topicReply, String replyEdit){
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(topicReplyCreator, apiTopicCreator, community, baseForumTopic);
		
		// Post a reply to the forum topic and update the reply
		createForumTopicReplyAndEditReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply, replyEdit);

		return forumTopic;
	}
	
	/**
	 * Creates a forum topic, allows a user to follow the topic, posts a reply to the topic and then edits that reply
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who will add the reply to the forum topic
	 * @param topicFollower - The User instance of the user who is following the forum topic in the community forum
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who is following the forum topic in the community forum
	 * @param topicReply - A String object which contains the content of the topic reply to be made
	 * @param replyEdit - A String object which contains the content of the topic reply edit to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndAddReplyAndEditReply(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicReplyCreator, User topicFollower, APIForumsHandler apiTopicFollower, String topicReply, String replyEdit){
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicReplyCreator, apiTopicCreator, community, baseForumTopic, topicFollower, apiTopicFollower);
		
		// Post a reply to the forum topic and update the reply
		createForumTopicReplyAndEditReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply, replyEdit);

		return forumTopic;
	}
	
	/**
	 * Likes / recommends a forum topic
	 * 
	 * @param userLikingTopic - The User instance of the user liking the topic
	 * @param apiUserLikingTopic - The APIForumsHandler instance of the user liking the topic
	 * @param forumTopicBeingLiked - The ForumTopic instance of the forum topic to be liked
	 * @return - A String object containing a URL (see like(forumTopic) method in APIForumsHandler
	 */
	public static String likeForumTopic(User userLikingTopic, APIForumsHandler apiUserLikingTopic, ForumTopic forumTopicBeingLiked) {
		
		// Like the forum topic
		return ForumEvents.likeForumTopic(userLikingTopic, apiUserLikingTopic, forumTopicBeingLiked);
	}
	
	/**
	 * Unlikes the specified forum topic
	 * 
	 * @param userUnlikingTopic - The User instance of the user unliking the forum topic
	 * @param apiUserUnlikingTopic - The APIForumsHandler instance of the user unliking the forum topic
	 * @param forumTopicBeingUnliked - The ForumTopic instance of the forum topic to be unliked
	 * @param urlForUnlikingTopic - The String content of the URL required to unlike the forum topic (this value is returned from the likeForumTopic() method)
	 */
	public static void unlikeForumTopic(User userUnlikingTopic, APIForumsHandler apiUserUnlikingTopic, ForumTopic forumTopicBeingUnliked, String urlForUnlikingTopic) {
		
		// Unlike the forum topic
		ForumEvents.unlikeForumTopic(userUnlikingTopic, apiUserUnlikingTopic, forumTopicBeingUnliked, urlForUnlikingTopic);
	}

	/**
	 * Creates a forum topic and likes / recommends the topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicLiker - The APIForumsHandler instance of the user who will like the forum topic
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndLikeTopic(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicLiker){
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(topicReplyCreator, apiTopicCreator, community, baseForumTopic);
		
		// Like the forum topic
		likeForumTopic(topicReplyCreator, apiTopicLiker, forumTopic);

		return forumTopic;
	}

	/**
	 * Creates a forum topic, allows a single user to follow the topic and then likes / recommends the topic
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicLiker - The APIForumsHandler instance of the user who will like the forum topic
	 * @param topicFollower - The User instance of the user who is following the forum topic in the community forum
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who is following the forum topic in the community forum
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndLikeTopic(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicLiker, User topicFollower, APIForumsHandler apiTopicFollower){
		
		//Create a forum topic in the community
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicReplyCreator, apiTopicCreator, community, baseForumTopic, topicFollower, apiTopicFollower);
		
		// Like the forum topic
		likeForumTopic(topicReplyCreator, apiTopicLiker, forumTopic);

		return forumTopic;
	}

	/**
	 * Creates a forum topic, adds a reply to that topic and then likes / recommends the reply
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who will add the reply to the forum topic
	 * @param topicReply - A String object which contains the content of the topic reply to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReplyAndLikeReply(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicReplyCreator, String topicReply){
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(topicReplyCreator, apiTopicCreator, community, baseForumTopic);
		
		// Create a forum topic reply and like the reply
		ForumReply forumReply = createForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply);
		likeForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumReply);

		return forumTopic;
	}
	
	/**
	 * Creates a forum topic, allows a single user to follow that topic, posts a reply to the topic and then likes / recommends the reply
	 * 
	 * @param topicCreator - The User instance of the user who is creating the forum topic in the community forum
	 * @param apiTopicCreator - The APICommunitiesHandler instance of the user who is creating the forum topic in the community forum
	 * @param community - The Community instance of the community to which the forum topic will be added
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which is to be created
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who will add the reply to the forum topic
	 * @param topicFollower - The User instance of the user who is following the forum topic in the community forum
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who is following the forum topic in the community forum
	 * @param topicReply - A String object which contains the content of the topic reply to be made
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndAddReplyAndLikeReply(User topicReplyCreator, APICommunitiesHandler apiTopicCreator, Community community, BaseForumTopic baseForumTopic, APIForumsHandler apiTopicReplyCreator, User topicFollower, APIForumsHandler apiTopicFollower, String topicReply){
		
		//Create a forum topic in the community
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicReplyCreator, apiTopicCreator, community, baseForumTopic, topicFollower, apiTopicFollower);
		
		//Create a forum topic reply and like the reply
		ForumReply forumReply = createForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumTopic, topicReply);
		likeForumTopicReply(topicReplyCreator, apiTopicReplyCreator, forumReply);

		return forumTopic;
	}
	
	/**
	 * Opens the EE for the specified community forum topic news story in the AS and posts a reply using the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userPostingReply - The User instance of the user posting the reply
	 * @param newsStoryContent - The String content of the news story for which to open the EE
	 * @param replyContent - The String content of the reply to be posted to the forum topic
	 * @return - True if all actions are completed successfully
	 */
	public static boolean createForumTopicReplyUsingUI(HomepageUI ui, User userPostingReply, String newsStoryContent, String replyContent) {
		
		// Post the reply to the forum topic using the EE
		return ForumEvents.createForumTopicReplyUsingUI(ui, userPostingReply, newsStoryContent, replyContent);
	}
	
	/**
	 * Posts a reply with mentions to the specified user to the forum topic
	 * 
	 * @param forumTopic - The ForumTopic instance to which the reply with mentions will be posted
	 * @param userCreatingReply - The User instance of the user posting the reply with mentions
	 * @param apiUserCreatingReply - The APIForumsHandler instance of the user posting the reply with mentions
	 * @param mentions - The Mentions instance of the user to be mentioned in the topic reply
	 * @return - The ForumReply instance
	 */
	public static ForumReply createForumTopicReplyWithMentions(ForumTopic forumTopic, User userCreatingReply, APIForumsHandler apiUserCreatingReply, Mentions mentions) {
		
		// Add a reply with mentions to the specified user to the forum topic
		return ForumEvents.createForumTopicReplyWithMentions(forumTopic, userCreatingReply, apiUserCreatingReply, mentions);
	}
	
	/**
	 * Creates a forum topic and then adds a reply with mentions to the specified user to that forum topic
	 * 
	 * @param community - The Community instance in which the forum topic will be created
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param userCreatingTopic - The User instance of the user creating the topic
	 * @param communitiesAPIUserCreatingTopic - The APICommunitiesHandler instance of the user creating the topic
	 * @param forumsAPIUserCreatingTopic - The APIForumsHandler instance of the user creating the topic
	 * @param mentions - The Mentions instance of the user to be mentioned in the reply
	 * @return - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReplyWithMentions(Community community, BaseForumTopic baseForumTopic, User userCreatingTopic, APICommunitiesHandler communitiesAPIUserCreatingTopic, APIForumsHandler forumsAPIUserCreatingTopic, Mentions mentions) {
		
		// Create a forum topic in the community
		ForumTopic forumTopic = createForumTopic(userCreatingTopic, communitiesAPIUserCreatingTopic, community, baseForumTopic);
		
		// Add a reply with mentions to the specified user to the forum topic
		createForumTopicReplyWithMentions(forumTopic, userCreatingTopic, forumsAPIUserCreatingTopic, mentions);
		
		return forumTopic;
	}
	
	/**
	 * Logs in to CommunitiesUI and navigates to the specified community forum in the UI
	 * 
	 * @param community - The Community instance of the community to navigate to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to in the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param forumToNavigateTo - The Forum instance of the forum to navigate to in the UI
	 * @param userToLogIn - The User instance of the user to be logged in
	 * @param apiUserToLogIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToCommunityForum(Community community, BaseCommunity baseCommunity, HomepageUI ui, CommunitiesUI uiCo, Forum forumToNavigateTo, User userToLogIn, APICommunitiesHandler apiUserToLogIn, boolean preserveInstance) {
		
		// Log in and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userToLogIn, apiUserToLogIn, preserveInstance);
		
		// Select 'Forums' from the left navigation menu
		selectForumsFromLeftNavigationMenu(uiCo);
		
		// Wait for Forums UI to load
		waitForForumTopicsUIToLoad(ui);
		
		// Click on the 'Forum' tab in Forums UI
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		// Wait for Forums UI to load
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseForums);
		
		// Click on the forum title
		ui.clickLinkWait("link=" + forumToNavigateTo.getTitle());
		
		// Wait for Forums UI to load
		waitForForumTopicsUIToLoad(ui);
	}
	
	/**
	 * Enters and posts a reply to a forum topic using the EE replies input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userPostingReply - The User instance of the user posting the reply
	 * @param forumTopicNewsStory - The String content of the forum topic news story whose EE is to be opened and replied to
	 * @param replyToBePosted - The String content of the reply to be posted
	 * @return - True if all actions are completed successfully
	 */
	public static boolean openEEAndPostForumTopicReplyUsingUI(HomepageUI ui, User userPostingReply, String forumTopicNewsStory, String replyToBePosted) {
		
		// Open the EE for the specified forum topic news story and post a reply to that topic using the EE
		return ForumEvents.openEEAndPostForumTopicReplyUsingUI(ui, userPostingReply, forumTopicNewsStory, replyToBePosted);
	}
	
	/**
	 * Enters and posts a reply with mentions to a forum topic using the EE replies input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingReply - The User instance of the user posting the reply
	 * @param forumTopicNewsStory - The String content of the forum topic news story whose EE is to be opened and replied to
	 * @param mentions - The Mentions instance of the user to be mentioned in the reply
	 * @param mentionedUserIsACommunityMember - True if the user is a member of the community, false if they are not a member
	 * @return - True if all actions are completed successfully
	 */
	public static boolean openEEAndPostForumTopicReplyWithMentionsUsingUI(HomepageUI ui, RCLocationExecutor driver, User userPostingReply, String forumTopicNewsStory,
																			Mentions mentions, boolean mentionedUserIsACommunityMember) {
		// Open the EE for the specified forum topic news story
		UIEvents.openEE(ui, forumTopicNewsStory);
		
		// Switch focus to the 'Replies' frame of the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, false);
		
		// Type the before mentions text and then type the mentions to the specified user - also selecting them from the typeahead
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUserInEE(ui, driver, mentions);
		
		if(!mentionedUserIsACommunityMember) {
			log.info("INFO: Verify that the warning message for the mentioned user not being a member of the community is displayed in the EE");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().mentionErrorMsgGlobalSharebox), 
								"ERROR: The warning message for the mentioned user not being a member of the community was NOT displayed in the EE");
		}
		// Switch focus to the 'Replies' frame of the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, false);
		
		if(mentionedUserIsACommunityMember) {
			// Verify that an valid mentions link is displayed (ie. including the '@' character)
			UIEvents.verifyValidOrInvalidMentionsLink(ui, mentions.getUserToMention().getDisplayName(), false);
		} else {
			// Verify that an invalid mentions link is displayed (ie. without the '@' character)
			UIEvents.verifyValidOrInvalidMentionsLink(ui, mentions.getUserToMention().getDisplayName(), true);
		}
		
		// Finish entering the mentions comment in the 'Replies' frame of the EE
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		// Switch focus back to the main EE frame
		UIEvents.switchToEEFrame(ui);
				
		// Post the reply by clicking on the 'Post' link in the EE
		UIEvents.postEECommentOrReply(ui);
		
		// Create the reply with mentions content to be verified
		String mentionsComment = mentions.getBeforeMentionText() + " ";
		if(mentionedUserIsACommunityMember) {
			mentionsComment += "@";
		}
		mentionsComment += mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		mentionsComment = mentionsComment.trim();
		
		log.info("INFO: Verify that the reply with mentions is displayed in the EE after posting");
		Assert.assertTrue(ui.fluentWaitTextPresent(mentionsComment), 
							"ERROR: The reply with mentions was NOT displayed in the EE after posting with content: " + mentionsComment);
		return true;
	}
	
	/**
	 * Selects the "Forums" option from the left-side navigation menu in Communities UI
	 * 
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	private static void selectForumsFromLeftNavigationMenu(CommunitiesUI uiCo) {
		
		log.info("INFO: Select 'Forums' from the left navigation menu");
		Community_LeftNav_Menu.FORUMS.select(uiCo);
	}
	
	/**
	 * Waits for the Forum Topics UI screen to load
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void waitForForumTopicsUIToLoad(HomepageUI ui) {
		
		// Wait for Forum Topics UI to load
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseTopics);
	}
}