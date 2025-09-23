package com.ibm.conn.auto.util.eventBuilder.forums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class ForumEvents {

	private static Logger log = LoggerFactory.getLogger(ForumEvents.class);
	
	/**
	 * Creates a new standalone forum
	 * 
	 * @param forumCreator - The User instance of the user who will create the standalone forum
	 * @param apiForumCreator - The APIForumsHandler instance of the user who will create the standalone forum
	 * @param baseForum - The BaseForum instance of the forum which will be created
	 * @return forum - A Forum object
	 */
	public static Forum createForum(User forumCreator, APIForumsHandler apiForumCreator, BaseForum baseForum) {

		log.info("INFO: " + forumCreator.getDisplayName() + " will now create a forum with title: " + baseForum.getName());
		Forum forum = apiForumCreator.createForum(baseForum);

		log.info("INFO: Verify that the new forum has been created successfully");
		Assert.assertNotNull(forum, "ERROR: The new forum was NOT created successfully and was returned as null");
		
		return forum;		
	}
	
	/**
	 * Allows the specified user to follow the standalone forum
	 * 
	 * @param forumToBeFollowed - The Forum instance of the forum to be followed
	 * @param userFollowingForum - The User instance of the user to follow the forum
	 * @param apiUserFollowingForum - The APIForumsHandler instance of the user to follow the forum
	 */
	public static void followForumSingleUser(Forum forumToBeFollowed, User userFollowingForum, APIForumsHandler apiUserFollowingForum) {
		
		log.info("INFO: " + userFollowingForum.getDisplayName() + " will now follow the forum with title: " + forumToBeFollowed.getTitle());
		apiUserFollowingForum.createFollow(forumToBeFollowed);
	}
	
	/**
	 * Creates a standalone forum and allows a single specified user to follow the forum
	 * 
	 * @param forumCreator - The User instance of the user who will create the standalone forum
	 * @param apiForumCreator - The APIForumsHandler instance of the user who will create the standalone forum
	 * @param forumFollower - The User instance of the user who will follow the standalone forum
	 * @param apiForumFollower - The APIForumsHandler instance of the user who will follow the standalone forum
	 * @param baseForum - The BaseForum instance of the forum which will be created
	 * @return forum - A Forum object
	 */
	public static Forum createForumWithOneFollower(User forumCreator, APIForumsHandler apiForumCreator, User forumFollower, APIForumsHandler apiForumFollower, BaseForum baseForum) {

		// Create the standalone forum
		Forum forum = createForum(forumCreator, apiForumCreator, baseForum);

		// Have the specified user follow the forum
		followForumSingleUser(forum, forumFollower, apiForumFollower);
		
		return forum;		
	}

	/**
	 * Edits / updates the description of a standalone forum
	 * 
	 * @param forumEditor - The User instance of the user who will edit the forum
	 * @param apiForumEditor - The APIForumsHandler instance of the user who will edit the forum
	 * @param forum - The Forum instance of the forum which will be edited
	 * @param forumDescriptionEdit - A String object which contains the content of the forum description edit
	 */
	public static void editForumDescription(User forumEditor, APIForumsHandler apiForumEditor, Forum forum, String forumDescriptionEdit){

		log.info("INFO: " + forumEditor.getDisplayName() + " will now edit the description of the forum with title: " + forum.getTitle());
		apiForumEditor.editForum(forum, forumDescriptionEdit);
	}
	
	/**
	 * Creates a forum topic in a standalone forum
	 * 
	 * @param forumTopicCreator - The User instance of the user who will create the forum topic
	 * @param apiForumTopicCreator - The APIForumsHandler instance of the user who will create the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopic(User forumTopicCreator, APIForumsHandler apiForumTopicCreator, BaseForumTopic baseForumTopic){

		log.info("INFO: " + forumTopicCreator.getDisplayName() + " will now create a forum topic with title: " + baseForumTopic.getTitle());
		ForumTopic forumTopic = apiForumTopicCreator.createForumTopic(baseForumTopic);

		log.info("INFO: Verify that the new forum topic has been created successfully");
		Assert.assertNotNull(forumTopic, "ERROR: The new forum topic was NOT created successfully and was returned as null");
		
		return forumTopic;
	}
	
	/**
	 * Allows the specified user to follow the forum topic
	 * 
	 * @param forumTopic - The ForumTopic instance of the forum topic to be followed
	 * @param userFollowingTopic - The User instance of the user to follow the forum topic
	 * @param apiUserFollowingTopic - The APIForumsHandler instance of the user to follow the forum topic
	 */
	public static void followForumTopicSingleUser(ForumTopic forumTopic, User userFollowingTopic, APIForumsHandler apiUserFollowingTopic) {
		
		log.info("INFO: " + userFollowingTopic.getDisplayName() + " will now follow the forum topic with title: " + forumTopic.getTitle());
		apiUserFollowingTopic.createFollow(forumTopic);
	}
	
	/**
	 * Creates a forum topic and allows a single specified user to follow the forum topic
	 * 
	 * @param forumTopicCreator - The User instance of the user who will create the forum topic
	 * @param apiForumTopicCreator - The APIForumsHandler instance of the user who will create the forum topic
	 * @param forumTopicFollower - The User instance of the user who will follow the forum topic
	 * @param apiForumTopicFollower - The APIForumsHandler instance of the user who will follow the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollower(User forumTopicCreator, APIForumsHandler apiForumTopicCreator, User forumTopicFollower, APIForumsHandler apiForumTopicFollower, BaseForumTopic baseForumTopic) {

		// Create the forum topic
		ForumTopic forumTopic = createForumTopic(forumTopicCreator, apiForumTopicCreator, baseForumTopic);

		// Have the specified user follow the forum topic
		followForumTopicSingleUser(forumTopic, forumTopicFollower, apiForumTopicFollower);
		
		return forumTopic;
	}
	
	/**
	 * Edits / updates the description of a forum topic
	 * 
	 * @param topicEditor - The User instance of the user who will edit the forum topic
	 * @param apiTopicEditor - The APIForumsHandler instance of the user who will edit the forum topic
	 * @param forumTopic - The ForumTopic instance of the forum topic which will be edited
	 * @param topicContentEdit - A String object containing the content of the edit
	 */
	public static void editForumTopicDescription(User topicEditor, APIForumsHandler apiTopicEditor, ForumTopic forumTopic, String topicContentEdit) {

		log.info("INFO: " + topicEditor.getDisplayName() + " will now edit the description of the forum topic with title: " + forumTopic.getTitle());
		apiTopicEditor.editTopic(forumTopic, topicContentEdit);
	}
	
	/**
	 * Creates a forum topic and then edits the forum topic description
	 * 
	 * @param topicEditor - The User instance of the user who will create and edit the forum topic
	 * @param apiTopicEditor - The APIForumsHandler instance of the user who will create and edit the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created and edited
	 * @param topicContentEdit - A String object containing the content of the edit
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndEditTopicDescription(User topicEditor, APIForumsHandler apiTopicEditor, BaseForumTopic baseForumTopic, String topicContentEdit) {
		
		// Create the forum topic
		ForumTopic forumTopic = createForumTopic(topicEditor, apiTopicEditor, baseForumTopic);

		// Edit / update the forum topic description
		editForumTopicDescription(topicEditor, apiTopicEditor, forumTopic, topicContentEdit);
		
		return forumTopic;
	}
	
	/**
	 * Creates a forum topic, allows the specified user to follow the forum topic and then edits / updates the forum topic description
	 * 
	 * @param topicEditor - The User instance of the user who will create and edit the forum topic
	 * @param apiTopicEditor - The APIForumsHandler instance of the user who will create and edit the forum topic
	 * @param topicFollower - The User instance of the user who will follow the forum topic
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who will follow the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created and edited
	 * @param topicContentEdit - A String object containing the content of the edit
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndEditTopicDescription(User topicEditor, APIForumsHandler apiTopicEditor, User topicFollower, APIForumsHandler apiTopicFollower, BaseForumTopic baseForumTopic, String topicContentEdit) {

		// Create the forum topic and have the specified user follow the forum topic
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicEditor, apiTopicEditor, topicFollower, apiTopicFollower, baseForumTopic);

		// Edit / update the forum topic description
		editForumTopicDescription(topicEditor, apiTopicEditor, forumTopic, topicContentEdit);
		
		return forumTopic;
	}
	
	/**
	 * Creates a reply to a forum topic
	 * 
	 * @param topicReplyCreator - The User instance of the user who will create the forum topic comment
	 * @param apiTopicReplyCreator - The APIForumsHandler instance of the user who will create the forum topic comment
	 * @param forumTopic - The ForumTopic instance to which the comment will be added
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @return - A ForumReply instance of the forum topic reply
	 */
	public static ForumReply createForumTopicReply(User topicReplyCreator, APIForumsHandler apiTopicReplyCreator, ForumTopic forumTopic, String topicReply) {

		log.info("INFO: " + topicReplyCreator.getDisplayName() + " will now create a reply to the forum topic with title: " + forumTopic.getTitle());
		ForumReply forumReply = apiTopicReplyCreator.createForumReply(forumTopic, topicReply);
		
		log.info("INFO: Verify that the forum reply was created successfully");
		Assert.assertNotNull(forumReply, "ERROR: The forum reply was NOT created successfully and was returned as null");
		
		return forumReply;
	}
	
	/**
	 * Edits / updates a reply to a forum topic
	 * 
	 * @param topicReplyEditor - The User instance of the user who will edit the forum topic comment
	 * @param apiTopicReplyEditor - The APIForumsHandler instance of the user who will edit the forum topic comment
	 * @param topicReply - The ForumReply instance of the topic reply which will be edited
	 * @param replyEdit - The String object which contains the content of the edit
	 */
	public static void updateForumTopicReply(User topicReplyEditor, APIForumsHandler apiTopicReplyEditor, ForumReply forumReply, String replyEdit) {

		log.info("INFO: " + topicReplyEditor.getDisplayName() + " will now edit / update the forum topic reply to read: " + replyEdit);
		apiTopicReplyEditor.editReply(forumReply, replyEdit);
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
		
		// Post a reply to the forum topic
		ForumReply forumReply = createForumTopicReply(userCreatingReply, apiUserCreatingReply, forumTopic, topicReply);
		
		// Update the reply that was posted to the forum topic
		updateForumTopicReply(userCreatingReply, apiUserCreatingReply, forumReply, replyEdit);
		
		return forumReply;
	}
	
	/**
	 * Creates a forum topic and then posts a reply to that topic
	 * 
	 * @param forumTopicCreator - The User instance of the user who will create the forum topic
	 * @param apiForumTopicCreator - The APIForumsHandler instance of the user who will create the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReply(User forumTopicCreator, APIForumsHandler apiForumTopicCreator, BaseForumTopic baseForumTopic, String topicReply) {

		// Create the forum topic
		ForumTopic forumTopic = createForumTopic(forumTopicCreator, apiForumTopicCreator, baseForumTopic);
		
		// Post a reply to the forum topic
		createForumTopicReply(forumTopicCreator, apiForumTopicCreator, forumTopic, topicReply);
		
		return forumTopic;
	}
	
	/**
	 * Creates a forum topic, posts a reply to that topic and then edits the reply
	 * 
	 * @param topicReplyEditor - The User instance of the user who will create the topic, post a reply and then edit the forum topic comment
	 * @param apiTopicReplyEditor - The APIForumsHandler instance of the user who will create the topic, post a reply and then edit the forum topic comment
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @param replyEdit - The String object which contains the content of the edit
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReplyAndEditReply(User topicReplyEditor, APIForumsHandler apiTopicReplyEditor, BaseForumTopic baseForumTopic, String topicReply, String replyEdit) {

		// Create the forum topic
		ForumTopic forumTopic = createForumTopic(topicReplyEditor, apiTopicReplyEditor, baseForumTopic);
		
		// Post a reply to the forum topic and update the reply
		createForumTopicReplyAndEditReply(topicReplyEditor, apiTopicReplyEditor, forumTopic, topicReply, replyEdit);
		
		return forumTopic;
	}
	
	/**
	 * Creates a forum topic with a single specified follower and then posts a reply to that topic
	 * 
	 * @param forumTopicCreator - The User instance of the user who will create the forum topic
	 * @param apiForumTopicCreator - The APIForumsHandler instance of the user who will create the forum topic
	 * @param forumTopicFollower - The User instance of the user who will follow the forum topic
	 * @param apiForumTopicFollower - The APIForumsHandler instance of the user who will follow the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndAddReply(User forumTopicCreator, APIForumsHandler apiForumTopicCreator, User forumTopicFollower, APIForumsHandler apiForumTopicFollower, BaseForumTopic baseForumTopic, String topicReply){

		// Create the forum topic and have the specified user follow the forum topic
		ForumTopic forumTopic = createForumTopicWithOneFollower(forumTopicCreator, apiForumTopicCreator, forumTopicFollower, apiForumTopicFollower, baseForumTopic);
		
		// Post a reply to the forum topic
		createForumTopicReply(forumTopicCreator, apiForumTopicCreator, forumTopic, topicReply);

		return forumTopic;
	}
	
	/**
	 * Creates a forum topic with a single specified follower, posts a reply to the topic and then edits the reply
	 * 
	 * @param topicReplyEditor - The User instance of the user who will create and then edit the forum topic comment
	 * @param apiTopicReplyEditor - The APIForumsHandler instance of the user who will create and then  edit the forum topic comment
	 * @param forumTopicFollower - The User instance of the user who will follow the forum topic
	 * @param apiForumTopicFollower - The APIForumsHandler instance of the user who will follow the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @param replyEdit - A String object which contains the edited / updated content of the forum topic reply
	 * @return
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndAddReplyAndEditReply(User topicReplyEditor, APIForumsHandler apiTopicReplyEditor, User forumTopicFollower, APIForumsHandler apiForumTopicFollower, BaseForumTopic baseForumTopic, String topicReply, String replyEdit){

		// Create the forum topic and have the specified user follow the forum topic
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicReplyEditor, apiTopicReplyEditor, forumTopicFollower, apiForumTopicFollower, baseForumTopic);
		
		// Post a reply to the forum topic and update the reply
		createForumTopicReplyAndEditReply(topicReplyEditor, apiTopicReplyEditor, forumTopic, topicReply, replyEdit);
		
		return forumTopic;
	}
	
	/**
	 * Likes / recommends a forum topic
	 * 
	 * @param topicLiker - The User instance of the user who will like the forum topic
	 * @param apiTopicLiker - The APIForumsHandler instance of the user who will like the forum topic
	 * @param forumTopic - The ForumTopic instance which will be liked
	 * @return - A String object containing a URL (see like(forumTopic) method in APIForumsHandler
	 */
	public static String likeForumTopic(User topicLiker, APIForumsHandler apiTopicLiker, ForumTopic forumTopic) {

		log.info("INFO: " + topicLiker.getDisplayName() + " will now like / recommend the forum topic with title: " + forumTopic.getTitle());
		return apiTopicLiker.like(forumTopic);
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
		
		log.info("INFO: " + userUnlikingTopic.getDisplayName() + " will now unlike the forum topic with title: " + forumTopicBeingUnliked.getTitle());
		boolean unlikedTopic = apiUserUnlikingTopic.unlike(urlForUnlikingTopic);
		
		log.info("INFO: Verify that the forum topic was unliked successfully");
		Assert.assertTrue(unlikedTopic, 
							"ERROR: The forum topic could NOT be unliked - the API method call returned a negative response");
	}
	
	/**
	 * Creates a forum topic and likes / recommends the topic
	 * 
	 * @param topicLiker - The User instance of the user who will create and like the forum topic
	 * @param apiTopicLiker - The APIForumsHandler instance of the user who will create and like the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndLikeTopic(User topicLiker, APIForumsHandler apiTopicLiker, BaseForumTopic baseForumTopic) {

		// Create the forum topic
		ForumTopic forumTopic = createForumTopic(topicLiker, apiTopicLiker, baseForumTopic);
		
		// Like / recommend the forum topic
		likeForumTopic(topicLiker, apiTopicLiker, forumTopic);
		
		return forumTopic;
	}
	
	/**
	 * Creates a forum topic with a single specified follower and likes / recommends the topic
	 * 
	 * @param topicLiker - The User instance of the user who will create and like the forum topic
	 * @param apiTopicLiker - The APIForumsHandler instance of the user who will create and like the forum topic
	 * @param topicFollower - The User instance of the user who will follow the forum topic
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who will follow the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndLikeTopic(User topicLiker, APIForumsHandler apiTopicLiker, User topicFollower, APIForumsHandler apiTopicFollower,  BaseForumTopic baseForumTopic){

		// Create the forum topic and have the specified user follow the forum topic
		ForumTopic forumTopic = createForumTopicWithOneFollower(topicLiker, apiTopicLiker, topicFollower, apiTopicFollower, baseForumTopic);
		
		// Like / recommend the forum topic
		likeForumTopic(topicLiker, apiTopicLiker, forumTopic);
		
		return forumTopic;
	}
	
	/**
	 * Likes / recommends a forum topic reply
	 * 
	 * @param replyLiker - The User instance of the user who will create and like the forum topic reply
	 * @param apiReplyLiker - The APIForumsHandler instance of the user who will create and like the forum topic reply
	 * @param forumReply - A ForumReply instance of the reply which will be liked
	 */
	public static void likeForumTopicReply(User replyLiker, APIForumsHandler apiReplyLiker, ForumReply forumReply){

		log.info("INFO: " + replyLiker.getDisplayName() + " will now like / recommend the forum topic reply with content: " + forumReply.getContent());
		boolean likeOperationSuccessful;
		try{
			apiReplyLiker.like(forumReply);
			likeOperationSuccessful = true;
		}
		catch(Exception ex){
			log.info("ERROR: The forum topic reply could not be liked / recommended");
			ex.printStackTrace();
			likeOperationSuccessful = false;
		}
		log.info("INFO: Verify that the like operation on the forum topic reply was successful");
		Assert.assertTrue(likeOperationSuccessful, "ERROR: The forum topic reply was NOT successfully liked / recommended");
		
		log.info("INFO: The forum topic reply was successfully liked by " + replyLiker.getDisplayName());
	}
	
	/**
	 * Creates a forum topic, posts a reply to that forum topic and then likes / recommends the forum topic
	 * 
	 * @param replyLiker - The User instance of the user who will create and like the forum topic reply
	 * @param apiReplyLiker - The APIForumsHandler instance of the user who will create and like the forum topic reply
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicAndAddReplyAndLikeReply(User replyLiker, APIForumsHandler apiReplyLiker, BaseForumTopic baseForumTopic, String topicReply){

		// Create the forum topic
		ForumTopic forumTopic = createForumTopic(replyLiker, apiReplyLiker, baseForumTopic);
		
		// Post a reply to the forum topic
		ForumReply forumReply = createForumTopicReply(replyLiker, apiReplyLiker, forumTopic, topicReply);
		
		// Like / recommend the forum topic reply
		likeForumTopicReply(replyLiker, apiReplyLiker, forumReply);
		
		return forumTopic;	
	}
	
	/**
	 * 
	 * @param replyLiker - The User instance of the user who will create and like the forum topic reply
	 * @param apiReplyLiker - The APIForumsHandler instance of the user who will create and like the forum topic reply
	 * @param topicFollower - The User instance of the user who will follow the forum topic
	 * @param apiTopicFollower - The APIForumsHandler instance of the user who will follow the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic which will be created
	 * @param topicReply - A String object which contains the content of the forum topic reply
	 * @return forumTopic - A ForumTopic object
	 */
	public static ForumTopic createForumTopicWithOneFollowerAndAddReplyAndLikeReply(User replyLiker, APIForumsHandler apiReplyLiker, User topicFollower, APIForumsHandler apiTopicFollower, BaseForumTopic baseForumTopic, String topicReply){

		// Create the forum topic and have the specified user follow the forum topic
		ForumTopic forumTopic = createForumTopicWithOneFollower(replyLiker, apiReplyLiker, topicFollower, apiTopicFollower, baseForumTopic);
		
		// Post a reply to the forum topic
		ForumReply forumReply = createForumTopicReply(replyLiker, apiReplyLiker, forumTopic, topicReply);
		
		// Like / recommend the forum topic reply
		likeForumTopicReply(replyLiker, apiReplyLiker, forumReply);
		
		return forumTopic;
	}
	
	/**
	 * Opens the EE for the specified forum topic news story in the AS and posts a reply using the EE
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userPostingReply - The User instance of the user posting the reply
	 * @param newsStoryContent - The String content of the news story for which to open the EE
	 * @param replyContent - The String content of the reply to be posted to the forum topic
	 * @return - True if all actions are completed successfully
	 */
	public static boolean createForumTopicReplyUsingUI(HomepageUI ui, User userPostingReply, String newsStoryContent, String replyContent) {
		
		log.info("INFO: " + userPostingReply.getDisplayName() + " will now post a reply to the forum topic with content: " + replyContent);
		UIEvents.openEEAndSwitchToEECommentOrRepliesFrame(ui, newsStoryContent, false);
		
		// Enter the reply content into the 'Replies' input field
		UIEvents.typeStringWithNoDelay(ui, replyContent);
		
		// Switch focus back to the EE frame again and post the reply by clicking on the 'Post' link
		UIEvents.switchToEEFrameAndPostCommentOrReply(ui);
		
		log.info("INFO: Verify that the forum topic reply has posted correctly in the EE with content: " + replyContent);
		Assert.assertTrue(ui.fluentWaitTextPresent(replyContent), 
							"ERROR: The forum topic reply was NOT displayed in the EE after clicking on the 'Post' link");
		return true;
	}
	
	/**
	 * Add a reply with mentions to the specified user to a forum topic
	 * 
	 * @param forumTopic - The ForumTopic instance of the forum topic to which the reply will be posted
	 * @param userCreatingReply - The User instance of the user creating the reply
	 * @param apiUserCreatingReply - The APIForumsHandler instance of the user creating the reply
	 * @param mentions - The Mentions instance of the user to be mentioned in the reply
	 * @return - The ForumReply instance of the reply
	 */
	public static ForumReply createForumTopicReplyWithMentions(ForumTopic forumTopic, User userCreatingReply, APIForumsHandler apiUserCreatingReply, Mentions mentions) {
		
		log.info("INFO: " + userCreatingReply.getDisplayName() + " will now post a reply with mentions to " + mentions.getUserToMention().getDisplayName() + " to the forum topic with title: " + forumTopic.getTitle());
		return apiUserCreatingReply.createTopicReplyMention(forumTopic, mentions);
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
		
		// Open the EE for the specified forum topic news story
		UIEvents.openEE(ui, forumTopicNewsStory);
		
		// Switch focus to the 'Replies' frame of the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, false);
		
		log.info("INFO: " + userPostingReply.getDisplayName() + " will now enter a reply in the EE with content: " + replyToBePosted);
		UIEvents.typeStringWithNoDelay(ui, replyToBePosted);
		
		// Switch focus back to the main EE frame
		UIEvents.switchToEEFrame(ui);
		
		// Post the reply by clicking on the 'Post' link in the EE
		UIEvents.postEECommentOrReply(ui);
		
		log.info("INFO: Verify that the reply is displayed in the EE after posting");
		Assert.assertTrue(ui.fluentWaitTextPresent(replyToBePosted), 
							"ERROR: The reply was NOT displayed in the EE after posting with content: " + replyToBePosted);
		return true;
	}
	
	/**
	 * Creates a forum topic which includes mentions to the specified user
	 * 
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the forum topic
	 * @param userCreatingTopic - The User instance of the user creating the topic
	 * @param apiUserCreatingTopic - The APIForumsHandler instance of the user creating the topic
	 * @return - The ForumTopic instance of the forum topic
	 */
	public static ForumTopic createForumTopicWithMentions(BaseForumTopic baseForumTopic, Mentions mentions, User userCreatingTopic, APIForumsHandler apiUserCreatingTopic) {
		
		log.info("INFO: " + userCreatingTopic.getDisplayName() + " will now create a forum topic with mentions to " + mentions.getUserToMention().getDisplayName());
		ForumTopic forumTopic = apiUserCreatingTopic.apiCreateTopicMention(baseForumTopic, mentions);
		
		log.info("INFO: Verify that the forum topic was created successfully");
		Assert.assertNotNull(forumTopic, 
								"ERROR: The forum topic was NOT created and was returned as null");
		return forumTopic;
	}
}