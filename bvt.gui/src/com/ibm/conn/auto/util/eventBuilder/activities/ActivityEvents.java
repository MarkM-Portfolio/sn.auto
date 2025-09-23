package com.ibm.conn.auto.util.eventBuilder.activities;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

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

public class ActivityEvents {
	
	private static Logger log = LoggerFactory.getLogger(ActivityEvents.class);
	
	/**
	 * Create a standalone activity
	 * 
	 * @param activityCreator - The User instance of the user who is creating the activity
	 * @param apiActivityCreator - The APIActivitiesHandler instance of the user who is creating the activity
	 * @param baseActivity - The BaseActivity instance of the activity which is to be created
	 * @param isOnPremise - True if the test is being executed in the On Premise environment, false otherwise
	 * @return activity - An Activity object
	 */
	public static Activity createActivity(User activityCreator, APIActivitiesHandler apiActivityCreator, BaseActivity baseActivity, boolean isOnPremise) {

		log.info("INFO: " + activityCreator.getDisplayName() + " will now create a standalone activity with title: " + baseActivity.getName());
		Activity activity = apiActivityCreator.createActivity(baseActivity, isOnPremise);
		
		log.info("INFO: Verify that the new activity was created successfully");
		Assert.assertNotNull(activity, "ERROR: The activity was NOT created successfully and was returned as null");
		
		return activity;
	}
	
	/**
	 * Marks the specified activity as public
	 * 
	 * @param activityToBeMadePublic - The Activity instance of the activity to be made public
	 * @param userMakingActivityPublic - The User instance of the user who is making the activity public
	 * @param apiUserMakingActivityPublic - The APIActivitiesHandler instance of the user who is making the activity public
	 * @param isOnPremise - True if the test is being run On Premise, false if it is running on Smart Cloud
	 */
	public static void makeActivityPublic(Activity activityToBeMadePublic, User userMakingActivityPublic, APIActivitiesHandler apiUserMakingActivityPublic, boolean isOnPremise) {
		
		log.info("INFO: " + userMakingActivityPublic.getDisplayName() + " will now mark the activity as public with title: " + activityToBeMadePublic.getTitle());
		boolean markedAsPublic = apiUserMakingActivityPublic.makeActivityPublic(activityToBeMadePublic.toEntry(), isOnPremise);
		
		log.info("INFO: Verify that the activity was successfully marked as public");
		Assert.assertTrue(markedAsPublic, "ERROR: The activity could NOT be marked as public");
	}
	
	/**
	 * Adds a single member to an activity
	 * 
	 * @param activity - The Activity instance of the activity to which the member is to be added
	 * @param userAddingNewMember - The User instance of the user adding the new member to the activity
	 * @param apiUserAddingNewMember - The APIActivitiesHandler instance of the user adding the new member to the activity
	 * @param userToBeAddedAsAMember - The User instance of the user to be added as a member to the activity
	 */
	public static void addMemberSingleUser(Activity activity, User userAddingNewMember, APIActivitiesHandler apiUserAddingNewMember, User userToBeAddedAsAMember) {
		
		log.info("INFO: " + userAddingNewMember.getDisplayName() + " will now add " + userToBeAddedAsAMember.getDisplayName() + " as a member to the activity with title: " + activity.getTitle());
		apiUserAddingNewMember.addMemberToActivity(activity, userToBeAddedAsAMember);
	}
	
	/**
	 * Adds multiple users as members to an activity
	 * 
	 * @param activity - The Activity instance of the activity to which the members are to be added
	 * @param userAddingNewMembers - The User instance of the user adding the new members to the activity
	 * @param apiUserAddingNewMembers - The APIActivitiesHandler instance of the user adding the new members to the activity
	 * @param usersToBeAddedAsMembers - An array of User instances of the users to be added as members to the activity
	 */
	public static void addMemberMultipleUsers(Activity activity, User userAddingNewMembers, APIActivitiesHandler apiUserAddingNewMembers, User[] usersToBeAddedAsMembers) {
		
		for(User currentUser : usersToBeAddedAsMembers) {
			addMemberSingleUser(activity, userAddingNewMembers, apiUserAddingNewMembers, currentUser);
		}
	}
	
	/**
	 * Adds a single follower to an activity
	 * 
	 * @param activity - The Activity instance of the activity to be followed
	 * @param apiUserFollowingActivity - The APIActivitiesHandler instance of the user to follow the activity
	 */
	public static void addFollowerSingleUser(Activity activity, APIActivitiesHandler apiUserFollowingActivity) {
		
		log.info("INFO: The user will now follow the activity with title: " + activity.getTitle());
		apiUserFollowingActivity.createFollow(activity);
	}
	
	/**
	 * Adds multiple followers to an activity
	 * 
	 * @param activity - The Activity instance of the activity to be followed
	 * @param apiUsersToFollowTheActivity - An array of APIActivitiesHandler instances of the users to follow the activity
	 */
	public static void addFollowerMultipleUsers(Activity activity, APIActivitiesHandler[] apiUsersToFollowTheActivity) {
		
		for(APIActivitiesHandler apiUserToFollowTheActivity : apiUsersToFollowTheActivity) {
			addFollowerSingleUser(activity, apiUserToFollowTheActivity);
		}
	}
	
	/**
	 * Creates an activity with a single member
	 *  
	 * @param baseActivity - The BaseActivity instance of the activity which is to be created
	 * @param activityCreator - The User instance of the user who is creating the activity
	 * @param apiActivityCreator - The APIActivitiesHandler instance of the user who is creating the activity
	 * @param memberToBeAdded - The User instance of the member to be added to the community
	 * @param isOnPremise - True if the test is being executed in the On Premise environment, false otherwise
	 * @return activity - An Activity object
	 */
	public static Activity createActivityWithOneMember(BaseActivity baseActivity, User activityCreator, APIActivitiesHandler apiActivityCreator, User memberToBeAdded, boolean isOnPremise) {
		
		// Create the activity
		Activity activity = createActivity(activityCreator, apiActivityCreator, baseActivity, isOnPremise);
		
		// Add the member to the activity
		addMemberSingleUser(activity, activityCreator, apiActivityCreator, memberToBeAdded);
		
		return activity;
	}
	
	/**
	 * Creates a new activity, adds a single member to the activity and then allows the new member to follow the activity
	 * 
	 * @param baseActivity - The BaseActivity instance of the activity which is to be created
	 * @param activityCreator - The User instance of the user who is creating the activity
	 * @param apiActivityCreator - The APIActivitiesHandler instance of the user who is creating the activity
	 * @param userToBeAddedAsMemberAndFollower - The User instance of the member and follower to be added to the community
	 * @param apiUserToBeAddedAsMemberAndFollower - The APIActivitiesHandler instance of the member and follower to be added to the community
	 * @param isOnPremise - True if the test is being executed in the On Premise environment, false otherwise
	 * @return - The Activity instance of the newly created activity
	 */
	public static Activity createActivityWithOneMemberAndOneFollower(BaseActivity baseActivity, User activityCreator, APIActivitiesHandler apiActivityCreator,
																	User userToBeAddedAsMemberAndFollower, APIActivitiesHandler apiUserToBeAddedAsMemberAndFollower, boolean isOnPremise) {
		// Create the activity
		Activity activity = createActivity(activityCreator, apiActivityCreator, baseActivity, isOnPremise);
		
		// Add the specified member to the activity
		addMemberSingleUser(activity, activityCreator, apiActivityCreator, userToBeAddedAsMemberAndFollower);
		
		// Have the specified user follow the activity
		addFollowerSingleUser(activity, apiUserToBeAddedAsMemberAndFollower);
		
		return activity;
	}
	
	/**
	 * Creates a new activity and adds multiple members and followers to the activity
	 * 
	 * @param activityCreator - The User instance of the user who is creating the activity
	 * @param apiActivityCreator - The APIActivitiesHandler instance of the user who is creating the activity
	 * @param baseActivity - The BaseActivity instance of the activity which is to be created
	 * @param members - An array of User instances of the users to be added as members to the activity
	 * @param apiFollowers - An array of APIActivitiesHandler instances of the users to follow the activity
	 * @param isOnPremise - True if the test is being executed in the On Premise environment, false otherwise
	 * @return activity - An Activity object
	 */
	public static Activity createActivityWithMultipleMembersAndMultipleFollowers(User activityCreator, APIActivitiesHandler apiActivityCreator, BaseActivity baseActivity, User[] members, APIActivitiesHandler[] apiFollowers, boolean isOnPremise) {

		// Create the activity
		Activity activity = createActivity(activityCreator, apiActivityCreator, baseActivity, isOnPremise);
		
		// Add all members to the activity
		addMemberMultipleUsers(activity, activityCreator, apiActivityCreator, members);
		
		// Have all required followers follow the activity
		addFollowerMultipleUsers(activity, apiFollowers);
		
		return activity;	
	}
	
	/**
	 * Creates an entry in a standalone activity
	 * 
	 * @param entryCreator - The User instance of the user who is creating the activity entry
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @return activityEntry - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntry(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity) {

		baseActivityEntry.setParent(activity);

		log.info("INFO : " + entryCreator.getDisplayName() + " will now create an activity entry with title: " + baseActivityEntry.getTitle());
		ActivityEntry activityEntry = baseActivityEntry.createEntryAPI(apiEntryCreator);

		log.info("INFO: Verify that the new activity entry was created successfully");
		Assert.assertNotNull(activityEntry, "ERROR: The activity entry was NOT created successfully and was returned as null");
		
		return activityEntry;
	}
	
	/**
	 * Creates an activity entry with mentions to the specified user in the entry description
	 * 
	 * @param activity - The Activity instance of the activity in which the entry with mentions will be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the entry description
	 * @param entryCreator - The User instance of the user creating the activity
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user creating the activity
	 * @param isPrivateEntry - True if the entry is to be a private entry, false if it is to be a public entry
	 * @return - The ActivityEntry instance of the entry
	 */
	public static ActivityEntry createActivityEntryWithMentions(Activity activity, Mentions mentions, User entryCreator, APIActivitiesHandler apiEntryCreator, boolean isPrivateEntry) {
		
		log.info("INFO: " + entryCreator.getDisplayName() + " will now create an activity entry which will include mentions to " + mentions.getUserToMention().getDisplayName());
		ActivityEntry activityEntry = apiEntryCreator.addMention_ActivityEntry(activity, isPrivateEntry, mentions);
		
		log.info("INFO: Verify that the activity entry with mentions has been created successful");
		Assert.assertNotNull(activityEntry, 
								"ERROR: The activity entry with mentions was NOT created and was returned as null");
		return activityEntry;
	}
	
	/**
	 * Edits the description of an activity entry
	 * 
	 * @param entryEditor - The User instance of the user who is editing the activity entry
	 * @param apiEntryEditor - The APIActivitiesHandler instance of the user who is editing the activity entry
	 * @param activityEntry - The ActivityEntry instance of the activity entry which is to be edited
	 * @param newContent - A String object containing the content to which the activity entry's description will be updated
	 * @return - The updated ActivityEntry instance
	 */
	public static ActivityEntry editEntryDescription(User entryEditor, APIActivitiesHandler apiEntryEditor, ActivityEntry activityEntry, String newContent) {

		log.info("INFO: " + entryEditor.getDisplayName() + " will now edit the description of the entry with title: " + activityEntry.getTitle());
		ActivityEntry updatedEntry = apiEntryEditor.editActivityEntryDescription(activityEntry, newContent);
		
		log.info("INFO: Verify that the activity entry description was updated successfully");
		Assert.assertTrue(updatedEntry.getContent().trim().equals(newContent), 
							"ERROR: The activity entry description was NOT updated as expected to the new description with content: " + newContent);
		
		return updatedEntry;
	}
	
	/**
	 * Creates an activity entry and edits the description for that entry
	 * 
	 * @param entryCreator - The User instance of the user who is creating and editing the activity entry
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating and editing the activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the activity entry which is to be created and edited
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param newContent - A String object containing the content to which the activity entry's description will be updated
	 * @return activityEntry - An ActivityEntry object
	 */
	public static ActivityEntry createEntryAndEditDescription(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, String newContent) {
		
		// Create the activity entry
		ActivityEntry activityEntry = createActivityEntry(entryCreator, apiEntryCreator, baseActivityEntry, activity);
		
		// Edit the activity entry's content
		return editEntryDescription(entryCreator, apiEntryCreator, activityEntry, newContent);
	}
	
	/**
	 * Creates / posts a comment to an activity entry / to-do item
	 * 
	 * @param parentActivity - The Activity instance of the parent activity which contains the activity entry / to-do to which to post the comment
	 * @param parentEntry - The ActivityEntry instance of the activity entry to which the comment will be added, null if this comment is to be added to a to-do item
	 * @param parentTodo - The Todo instance of the to-do item to which the comment will be added, null if this comment is to be added to an activity entry
	 * @param comment - A String object containing the content of the comment
	 * @param commentCreator - The User instance of the user who is creating the comment
	 * @param apiCommentCreator - The APIActivitiesHandler instance of the user who is creating the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return - A Reply object
	 */
	public static Reply createComment(Activity parentActivity, ActivityEntry parentEntry, Todo parentTodo, String comment, User commentCreator, APIActivitiesHandler apiCommentCreator, boolean isPrivate) {
		
		Reply reply = null;
		
		if(parentEntry != null) {	
			log.info("INFO: " + commentCreator.getDisplayName() + " will now post a comment to the activity entry with title: " + parentEntry.getTitle());
			reply = apiCommentCreator.createActivityEntryReply(parentActivity, parentEntry, comment, isPrivate);
		}
		else {
			log.info("INFO: " + commentCreator.getDisplayName() + " will now post a comment to the to-do item with title: " + parentTodo.getTitle());
			reply = apiCommentCreator.createTodoItemReply(parentActivity, parentTodo, comment, isPrivate);
		}

		log.info("INFO: Verify that the new reply was created successfully");
		Assert.assertNotNull(reply, "ERROR: The reply was NOT created successfully and was returned as null");
		
		return reply;
	}
	
	/**
	 * Creates / posts multiple comments to an activity entry / to-do item
	 * 
	 * @param parentActivity - The Activity instance of the activity which contains the entry / to-do item to be commented on
	 * @param parentEntry - The ActivityEntry instance of the activity entry to which the comments will be added, null if these comments are to be added to a to-do item
	 * @param parentTodo - The Todo instance of the to-do item to which the comment will be added, null if these comments are to be added to an activity entry
	 * @param comments - A String array containing all of the comments to be posted
	 * @param commentCreator - The User instance of the user who is creating the comment
	 * @param apiCommentCreator - The APIActivitiesHandler instance of the user who is creating the comments
	 * @param isPrivate - A boolean variable which determines whether or not the comments will be private, true if the comments are to be private, false if they are to be public
	 * @return - An array of Reply objects
	 */
	public static Reply[] createMultipleComments(Activity parentActivity, ActivityEntry parentEntry, Todo parentTodo, String[] comments, User commentCreator, APIActivitiesHandler apiCommentCreator, boolean isPrivate) {
		
		int index = 0;
		Reply[] replies = new Reply[comments.length];
		
		for(String comment : comments) {
			log.info("INFO: Now posting comment " + (index + 1) + " of " + comments.length + " to the activity entry / to-do item");
			replies[index] = createComment(parentActivity, parentEntry, parentTodo, comment, commentCreator, apiCommentCreator, isPrivate);
			index ++;
		}
		return replies;
	}
	
	/**
	 * Sends a notification to the specified user, notifying them about the specified activity entry
	 * 
	 * @param parentActivity - The Activity instance of the activity which contains the activity entry
	 * @param activityEntry - The ActivityEntry instance of the entry which the specified user will be notified about
	 * @param userSendingNotification - The User instance of the user who is sending the notification
	 * @param apiUserSendingNotification - The APIActivitiesHandler instance of the user who is sending the notification
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified about the entry
	 */
	public static void notifyUserAboutActivityEntry(Activity parentActivity, ActivityEntry activityEntry, User userSendingNotification, APIActivitiesHandler apiUserSendingNotification, APIProfilesHandler apiUserToNotify) {
		
		log.info("INFO: " + userSendingNotification.getDisplayName() + " will now send a notification to " + apiUserToNotify.getDesplayName());
		boolean notificationSent = apiUserSendingNotification.notifyUserAboutActivityEntry(parentActivity, activityEntry, apiUserToNotify);
		
		log.info("INFO: Verify that the notification was successfully sent to " + apiUserToNotify.getDesplayName());
		Assert.assertTrue(notificationSent, 
							"ERROR: The notification could NOT be sent to " + apiUserToNotify.getDesplayName());
	}
	
	/**
	 * Creates an activity entry and posts a comment to that entry
	 * 
	 * @param entryCreator - The User instance of the user who is creating the activity entry and commenting on it
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the activity entry and commenting on it
	 * @param baseActivityEntry - The BaseActivityEntry instance of the activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param entryComment - A String object containing the content of the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private
	 * @return activityEntry - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntryAndComment(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, String entryComment, boolean isPrivate) {
		
		// Create the activity entry
		ActivityEntry activityEntry = createActivityEntry(entryCreator, apiEntryCreator, baseActivityEntry, activity);
		
		// Add a comment to the activity entry
		createComment(activity, activityEntry, null, entryComment, entryCreator, apiEntryCreator, isPrivate);
		
		return activityEntry;
	}
	
	/**
	 * Edits / updates a comment (Reply) posted to an activity entry / to-do item
	 * 
	 * @param commentEditor - The User instance of the user who is editing the comment
	 * @param apiCommentEditor - The APIActivitiesHandler instance of the user who is editing the comment
	 * @param commentReply - The Reply instance of the acomment which is to be edited
	 * @param commentEdit - A String object which contains the new content for the comment
	 * @return - The updated Reply instance
	 */
	public static Reply editComment(User commentEditor, APIActivitiesHandler apiCommentEditor, Reply commentReply, String commentEdit) {

		log.info("INFO: " + commentEditor.getDisplayName() + " will now update the existing comment with content: " + commentReply.getContent());
		Reply updatedReply = apiCommentEditor.editReply(commentReply, commentEdit);
		
		log.info("INFO: Verify that the comment has been updated successfully");
		Assert.assertTrue(updatedReply.getContent().trim().equals(commentEdit), 
							"ERROR: The comment was NOT updated - the initial comment is still set as the comment content");
		return updatedReply;
	}
	
	/**
	 * Posts a comment to an activity entry / to-do item and then edits / updates the comment
	 * 
	 * @param parentActivity - The Activity instance of the parent activity which contains the entry / to-do item to be commented on
	 * @param activityEntry - The ActivityEntry instance of the activity entry to be commented on - assign this to null if you wish to comment on a to-do item
	 * @param todo - The Todo instance of the to-do item to be commented on - assign this to null if you wish to comment on an activity entry
	 * @param comment - The String content of the comment to be posted
	 * @param editedComment - The String content of the updated comment to be posted
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APIActivitiesHandler instance of the user posting the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private 
	 * @return - The Reply instance of the updated reply
	 */
	public static Reply createCommentAndEditComment(Activity parentActivity, ActivityEntry activityEntry, Todo todo, String comment, String editedComment, 
														User userPostingComment, APIActivitiesHandler apiUserPostingComment, boolean isPrivate) {
		// Post the specified comment
		Reply reply = createComment(parentActivity, activityEntry, todo, comment, userPostingComment, apiUserPostingComment, isPrivate);
		
		// Edit / update the comment
		return editComment(userPostingComment, apiUserPostingComment, reply, editedComment);
	}
	
	/**
	 * Creates an activity entry, adds a comment to that entry and then edits the comment
	 * 
	 * @param entryCreator - The User instance of the user who is creating the activity entry and commenting on it
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the activity entry and commenting on it
	 * @param baseActivityEntry - The BaseActivityEntry instance of the activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param entryComment - A String object containing the content of the comment to be posted
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private
	 * @param commentEdit - A String object containing the content of the edited comment
	 * @return activityEntry - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntryAndAddCommentAndEditComment(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, String entryComment, boolean isPrivate, String commentEdit){
		
		// Create the activity entry
		ActivityEntry activityEntry = createActivityEntry(entryCreator, apiEntryCreator, baseActivityEntry, activity);
		
		// Add a comment to the activity entry and edit the comment
		createCommentAndEditComment(activity, activityEntry, null, entryComment, commentEdit, entryCreator, apiEntryCreator, isPrivate);
		
		return activityEntry;
	}
	
	/**
	 * Creates a to-do item in a standalone activity
	 * 
	 * @param todoCreator - The User instance of the user who is creating the to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param activity - The Activity instance of the activity to which the to-do item will be added
	 * @return todo - A Todo object
	 */
	public static Todo createTodo(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity){

		baseTodo.setParent(activity);

		log.info("INFO: " + todoCreator.getDisplayName() + " will now create a to-do item with title: " + baseTodo.getTitle());
		Todo todo = baseTodo.createTodoAPI(apiTodoCreator);

		log.info("INFO: Verify that the new to-do object was created successfully");
		Assert.assertNotNull(todo, "ERROR: The to-do object was NOT created successfully and was returned as null");
		
		return todo;
	}
	
	/**
	 * Creates an activity to-do which includes mentions to the specified user
	 * 
	 * @param activity - The Activity instance of the activity in which the to-do item is to be created
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param todoCreator - The User instance of the user to create the activity to-do
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user to create the activity to-do
	 * @param isPrivateTodo - True if the activity to-do is to be a private entry, false otherwise
	 * @return - The Todo instance of the activity to-do item
	 */
	public static Todo createActivityTodoWithMentions(Activity activity, Mentions mentions, User todoCreator, APIActivitiesHandler apiTodoCreator, boolean isPrivateTodo) {
		
		log.info("INFO: " + todoCreator.getDisplayName() + " will now create an activity to-do which will include mentions to " + mentions.getUserToMention().getDisplayName());
		Todo activityTodo = apiTodoCreator.addMention_Todo(activity, isPrivateTodo, mentions);
		
		log.info("INFO: Verify that the activity to-do with mentions has been created successful");
		Assert.assertNotNull(activityTodo, 
								"ERROR: The activity to-do with mentions was NOT created and was returned as null");
		return activityTodo;
	}
	
	/**
	 * Creates a to-do item in a standalone activity and then posts a comment to the to-do item
	 * 
	 * @param parentActivity - The Activity instance of the activity to which the to-do item will be added
	 * @param baseActivityTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param comment - The String content of the comment to be posted to the to-do item
	 * @param userCreatingTodo - The User instance of the user who is creating the to-do item
	 * @param apiUserCreatingTodo - The APIActivitiesHandler instance of the user who is creating the to-do item
	 * @param isPrivateComment - True if the comment being posted is to be a private comment, false if it is a public comment
	 * @return - A Todo object
	 */
	public static Todo createTodoAndAddComment(Activity parentActivity, BaseActivityToDo baseActivityTodo, String comment, User userCreatingTodo, APIActivitiesHandler apiUserCreatingTodo, boolean isPrivateComment) {
		
		// Create the to-do item in the activity
		Todo todo = createTodo(userCreatingTodo, apiUserCreatingTodo, baseActivityTodo, parentActivity);
		
		// Post a comment to the to-do item
		createComment(parentActivity, null, todo, comment, userCreatingTodo, apiUserCreatingTodo, isPrivateComment);
		
		return todo;
	}
	
	/**
	 * Creates a to-do item in a standalone activity, posts a comment to the to-do item and then edits / updates the comment
	 * 
	 * @param parentActivity - The Activity instance of the activity to which the to-do item will be added
	 * @param baseActivityTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param comment - The String content of the comment to be posted to the to-do item
	 * @param updatedComment - The String content of the updated comment to be posted to the to-do item
	 * @param userCreatingTodo - The User instance of the user who is creating the to-do item
	 * @param apiUserCreatingTodo - The APIActivitiesHandler instance of the user who is creating the to-do item
	 * @param isPrivateComment - True if the comment being posted is to be a private comment, false if it is a public comment
	 * @return - A Todo object
	 */
	public static Todo createTodoAndAddCommentAndEditComment(Activity parentActivity, BaseActivityToDo baseActivityTodo, String comment, String updatedComment, 
																User userCreatingTodo, APIActivitiesHandler apiUserCreatingTodo, boolean isPrivateComment) {
		// Create the to-do item in the activity
		Todo todo = createTodo(userCreatingTodo, apiUserCreatingTodo, baseActivityTodo, parentActivity);
		
		// Add a comment to the activity entry and edit the comment
		createCommentAndEditComment(parentActivity, null, todo, comment, updatedComment, userCreatingTodo, apiUserCreatingTodo, isPrivateComment);
		
		return todo;
	}
	
	/**
	 * Assigns a to-do item to the specified user
	 * 
	 * @param todoToBeAssigned - The Todo instance of the to-do item to be assigned to the user
	 * @param userAssigningToDo - The User instance of the user assigning the to-do item
	 * @param apiUserAssigningToDo - The APIActiviesHandler instance of the user assigning the to-do item
	 * @param apiUserToBeAssignedToDo - The APIProfilesHandler instance of the user to be assigned the to-do item
	 * @return - The updated Todo item set to the assigned user 
	 */
	public static Todo assignTodoItemToUser(Todo todoToBeAssigned, User userAssigningToDo, APIActivitiesHandler apiUserAssigningToDo, APIProfilesHandler apiUserToBeAssignedToDo) {
		
		log.info("INFO: " + userAssigningToDo.getDisplayName() + " will now assign the to-do item to the user with user name: " + apiUserToBeAssignedToDo.getDesplayName());
		Todo assignedTodo = apiUserAssigningToDo.assignToDoItemToUser(todoToBeAssigned, apiUserToBeAssignedToDo);
		
		log.info("INFO: Verify that the to-do was successfully assigned to " + apiUserToBeAssignedToDo.getDesplayName());
		Assert.assertNotNull(assignedTodo, "ERROR: The to-do item was NOT assigned successfully and was returned as null");
		
		return assignedTodo;
	}
	
	/**
	 * Completes a to-do item / re-opens a completed to-do item
	 * 
	 * @param userChangingTodoStatus - The User instance of the user who is changing the status of the to-do item to completed / re-opened
	 * @param apiUserChangingTodoStatus - The APIActivitiesHandler instance of the user who is changing the status of the to-do item to completed / re-opened
	 * @param todo - The Todo instance of the to-do item whose status is to be changed
	 * @param markAsComplete - True if the to-do item is to be completed, false if it is to be re-opened
	 * @return - The Todo instance of the completed / re-opened to-do item
	 */
	public static Todo markToDoItemAsCompleteOrIncomplete(User userChangingTodoStatus, APIActivitiesHandler apiUserChangingTodoStatus, Todo todo, boolean markAsComplete) {
		
		String completeOrReopen;
		if(markAsComplete) {
			completeOrReopen = "mark the to-do item as completed";
		} else {
			completeOrReopen = "re-open the to-do item (ie. mark it as incomplete)";
		}
		
		log.info("INFO: " + userChangingTodoStatus.getDisplayName() + " will now " + completeOrReopen);
		todo = apiUserChangingTodoStatus.markToDoItemAsCompleteOrIncomplete(todo, markAsComplete);
		
		log.info("Verify that the to-do status change completed successfully");
		Assert.assertNotNull(todo, "ERROR: The to-do status did NOT change as expected and was returned as null");
		
		return todo;
	}
	
	/**
	 * Creates a to-do item and assigns it to the specified user
	 * 
	 * @param parentActivity - The Activity instance of the activity to which the to-do item will be added
	 * @param baseActivityToDo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param todoCreator - The User instance of the user who is creating the to-do item and assigning it to another user
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the to-do item and assigning it to another user
	 * @param apiUserToBeAssignedToDo - The APIProfilesHandler instance of the user to be assigned the to-do item
	 * @return - A Todo object
	 */
	public static Todo createTodoAndAssignTodoItemToUser(Activity parentActivity, BaseActivityToDo baseActivityToDo, User todoCreator, APIActivitiesHandler apiTodoCreator, APIProfilesHandler apiUserToBeAssignedToDo) {
		
		// Create the to-do item
		Todo todo = createTodo(todoCreator, apiTodoCreator, baseActivityToDo, parentActivity);
		
		// Assign the to-do item to the specified user
		return assignTodoItemToUser(todo, todoCreator, apiTodoCreator, apiUserToBeAssignedToDo); 
	}
	
	/**
	 * Creates a to-do item, assigns it to the specified user and then marks that to-do item as completed
	 * 
	 * @param parentActivity - The Activity instance of the activity to which the to-do item will be added
	 * @param baseActivityToDo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param todoCreator - The User instance of the user who is creating the to-do item and assigning it to another user
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the to-do item and assigning it to another user
	 * @param apiUserToBeAssignedToDo - The APIProfilesHandler instance of the user to be assigned the to-do item
	 * @return - A Todo object
	 */
	public static Todo createTodoAndAssignTodoItemToUserAndMarkAsCompleted(Activity parentActivity, BaseActivityToDo baseActivityToDo, User todoCreator, APIActivitiesHandler apiTodoCreator, APIProfilesHandler apiUserToBeAssignedToDo) {
		
		// Create the to-do item and assign the to-do item to the specified user
		Todo todo = createTodoAndAssignTodoItemToUser(parentActivity, baseActivityToDo, todoCreator, apiTodoCreator, apiUserToBeAssignedToDo);
		
		// Mark the to-do item as completed
		return markToDoItemAsCompleteOrIncomplete(todoCreator, apiTodoCreator, todo, true);
	}
	
	/**
	 * Creates a to-do item in an activity and then marks it as completed
	 * 
	 * @param todoCreator - The User instance of the user who is creating the to-do item and completing it
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the to-do item and completing it
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param activity - The Activity instance of the activity to which the to-do item will be added
	 * @return todo - A Todo object
	 */
	public static Todo createTodoAndMarkAsCompleted(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity){

		// Create the to-do item
		Todo todo = createTodo(todoCreator, apiTodoCreator, baseTodo, activity);

		// Mark the to-do item as completed
		return markToDoItemAsCompleteOrIncomplete(todoCreator, apiTodoCreator, todo, true);
	}
	
	/**
	 * Creates a to-do item in an activity, marks it as completed and then re-opens the to-do item
	 * 
	 * @param todoCreator - The User instance of the user who is creating the to-do item, completing it and then re-opening it
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the to-do item, completing it and then re-opening it
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created, completed and re-opened
	 * @param activity - The Activity instance of the activity to which the to-do item will be added
	 * @return todo - A Todo object
	 */
	public static Todo createTodoAndMarkAsCompletedAndReopen(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity){

		// Create the to-do item and complete it
		Todo todo = createTodoAndMarkAsCompleted(todoCreator, apiTodoCreator, baseTodo, activity);
		
		// Re-open the to-do item
		return markToDoItemAsCompleteOrIncomplete(todoCreator, apiTodoCreator, todo, false);
	}
	
	/**
	 * Edits / updates the to-do item description
	 * 
	 * @param todoEditor - The User instance of the user who is editing the to-do item
	 * @param apiTodoEditor - The APIActivitiesHandler instance of the user who is editing the to-do item
	 * @param todoToEdit - The Todo instance of the to-do item which is to be edited
	 * @param newDescription - The String content to which the to-do item description will be set after the update
	 * @return - The updated Todo instance
	 */
	public static Todo editTodoDescription(User todoEditor, APIActivitiesHandler apiTodoEditor, Todo todoToEdit, String newDescription){

		log.info("INFO: " + todoEditor.getDisplayName() + " will now update the to-do item with title: " + todoToEdit.getTitle());
		Todo updatedTodo = apiTodoEditor.editTodoDescription(todoToEdit, newDescription);
		
		log.info("INFO: Verify that the to-do item description was updated successfully");
		Assert.assertTrue(updatedTodo.getContent().trim().equals(newDescription), 
							"ERROR: The to-do item description was NOT updated as expected to the new description with content: " + newDescription);
		
		return updatedTodo;
	}
	
	/**
	 * Creates a new to-do item and then edits the description of that to-do item
	 * 
	 * @param todoCreator - The User instance of the user who is creating and editing the to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating and editing the to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created and edited
	 * @param activity - The Activity instance of the activity to which the to-do item will be added and edited
	 * @param newDescription - The String content to which the to-do item description will be set after the update
	 * @return todoToEdit - A Todo object
	 */
	public static Todo createTodoAndEditDescription(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity, String newDescription){

		// Create the to-do item
		Todo todoToEdit = createTodo(todoCreator, apiTodoCreator, baseTodo, activity);
		
		// Edit the to-do item description
		return editTodoDescription(todoCreator, apiTodoCreator, todoToEdit, newDescription);
	}
	
	/**
	 * Creates an activity entry and posts a comment with mentions to that entry
	 * 
	 * @param entryCreator - The User instance of the user who is creating the activity entry and commenting on it
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the activity entry and commenting on it
	 * @param baseActivityEntry - The BaseActivityEntry instance of the activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param mention - A Mentions object which contains the content of the comment with the mention
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private
	 * @return activityEntry - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntryAndAddCommentWithMentions(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, Mentions mention, boolean isPrivate) {
		
		// Create the activity entry
		ActivityEntry activityEntry = createActivityEntry(entryCreator, apiEntryCreator, baseActivityEntry, activity);
		
		// Add a comment with mentions to the activity entry
		createCommentWithMentions(entryCreator, apiEntryCreator, activityEntry, null, mention, isPrivate);
		
		return activityEntry;
	}
	

	/**
	 * Creates a to-do item and posts a comment with mentions to that item
	 * 
	 * @param todoCreator - The User instance of the user who is creating the to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the to-do item
	 * @param baseActivityTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param activity - The Activity instance of the activity to which the to-do item will be added
	 * @param mention - A Mentions object which contains the content of the comment with the mention
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private
	 * @return todo - A Todo object
	 */
	public static Todo createActivityTodoAndAddCommentWithMentions(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseActivityTodo, Activity activity, Mentions mention, boolean isPrivate) {
		
		// Create the activity entry
		Todo todo = createTodo(todoCreator, apiTodoCreator, baseActivityTodo, activity);
		
		// Add a comment with a mention to the activity entry
		createCommentWithMentions(todoCreator, apiTodoCreator, null, todo, mention, isPrivate);
		
		return todo;
	}
	
	/**
	 * Creates / posts a comment with mentions to an activity entry / to-do item
	 * 
	 * @param commentCreator - The User instance of the user who is creating the comment
	 * @param apiCommentCreator - The APIActivitiesHandler instance of the user who is creating the comment
	 * @param parentEntry - The ActivityEntry instance of the activity entry to which the comment will be added, null if this comment is to be added to a to-do item
	 * @param parentTodo - The Todo instance of the to-do item to which the comment will be added, null if this comment is to be added to an activity entry
	 * @param mention - A Mentions object which contains the content of the comment with the mention
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return reply - A Reply object
	 */
	public static Reply createCommentWithMentions(User commentCreator, APIActivitiesHandler apiCommentCreator, ActivityEntry parentEntry, Todo parentTodo, Mentions mention, boolean isPrivate) {
		
		Reply reply = null;
		
		if(parentEntry != null) {	
			log.info("INFO: " + commentCreator.getDisplayName() + " will now post a comment with mentions to the activity entry with title: " + parentEntry.getTitle());
			reply = apiCommentCreator.addMention_EntryReply(parentEntry, isPrivate, mention);
		}
		else {
			log.info("INFO: " + commentCreator.getDisplayName() + " will now post a comment with mentions to the to-do item with title: " + parentTodo.getTitle());
			reply = apiCommentCreator.addMention_TodoReply(parentTodo, isPrivate, mention);	
		}

		log.info("INFO: Verify that the new reply with mentions was created successfully");
		Assert.assertNotNull(reply, "ERROR: The reply was NOT created successfully and was returned as null");
		
		return reply;
	}
	
	/**
	 * Assigns a to-do item to "anyone" (ie. the equivalent of using the "anyone" selection in the UI)
	 *  
	 * @param todoAssignor - The User instance of the user who is assigning the to-do item
	 * @param apiTodoAssignor - The APIActivitiesHandler instance of the user who is assigning the to-do item
	 * @param todo - The Todo instance of the to-do item which is being assigned
	 * @return - A Todo object
	 */
	public static Todo assignTodoToAnyone(User todoAssignor, APIActivitiesHandler apiTodoAssignor, Todo todo) {
		
		log.info("INFO: " + todoAssignor.getDisplayName() + " will now set the to-do item as being assigned to anyone");
		Todo todoAssignedToAnyone = apiTodoAssignor.assignToDoItemToAnyone(todo);
		
		log.info("INFO: Verifying that the to-do item was correctly set as being assigned to anyone");
		Assert.assertNotNull(todoAssignedToAnyone, 
								"ERROR: The to-do item was NOT set as assigned to anyone and was returned as null");
		
		return todoAssignedToAnyone;
	}
	
	/**
	 * Navigates to the specified activity in the Activities UI screen
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to in the UI
	 */
	public static void navigateToActivity(HomepageUI ui, Activity activityToNavigateTo) {
		
		log.info("INFO: Clicking on the link to the activity with title: " + activityToNavigateTo.getTitle());
		ui.clickLinkWait("link=" + activityToNavigateTo.getTitle());
		
		log.info("INFO: Wait for the activity UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedForTheseEntries);
	}
	
	/**
	 * Logs into Activities UI and navigates to the specified activity
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to in the UI
	 * @param userToBeLoggedIn - The User instance of the user to log in and navigate to the activity
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToActivity(HomepageUI ui, Activity activityToNavigateTo, User userToBeLoggedIn, boolean preserveInstance) {
		
		// Log in to Activities UI
		LoginEvents.loginToActivities(ui, userToBeLoggedIn, preserveInstance);
		
		// Navigate to the activity in the Activities UI screen
		navigateToActivity(ui, activityToNavigateTo);
	}
	
	/**
	 * Logs into Activities UI, navigates to the specified activity and returns the URL for the activity
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to in the UI
	 * @param userToBeLoggedIn - The User instance of the user to log in and navigate to the activity
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 * @return - The URL of the activities main page
	 */
	public static String loginAndNavigateToActivityAndGetActivityURL(HomepageUI ui, RCLocationExecutor driver, Activity activityToNavigateTo, User userToBeLoggedIn, boolean preserveInstance) {
		
		// Log in to Activities UI and navigate to the activity
		loginAndNavigateToActivity(ui, activityToNavigateTo, userToBeLoggedIn, preserveInstance);
		
		// Return the URL for the activities main page
		return UIEvents.getCurrentURL(driver);
	}
	
	/**
	 * Logs into ActivitiesUI, navigates to the specified activity and then re-opens the specified to-do item in the activity
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiAct - The ActivitiesUI instance to invoke all relevant methods
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to in the UI
	 * @param todoItemToReopen - The Todo instance of the to-do item to be re-opened
	 * @param userToBeLoggedIn - The User instance of the user to log in and navigate to the activity
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToActivityAndReopenToDoItem(HomepageUI ui, ActivitiesUI uiAct, Activity activityToNavigateTo, Todo todoItemToReopen, User userToBeLoggedIn, boolean preserveInstance) {
		
		// Log in to Activities UI and navigate to the activity
		loginAndNavigateToActivity(ui, activityToNavigateTo, userToBeLoggedIn, preserveInstance);
		
		// Re-open the specified to-do item
		reopenToDoItemUsingUI(uiAct, todoItemToReopen);
	}
	
	/**
	 * Creates an assigned to-do item using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param baseActivityTodo - The BaseActivityToDo instance of the to-do item to be created
	 * @param userCreatingTodo - The User instance of the user creating the to-do item
	 * @param userBeingAssignedTheTodo - The User instance of the user to be assigned the to-do item
	 */
	public static void createAssignedTodoUsingUI(HomepageUI ui, RCLocationExecutor driver, BaseActivityToDo baseActivityTodo, User userCreatingTodo, User userBeingAssignedTheTodo) {
		
		log.info("INFO: " + userCreatingTodo.getDisplayName() + " will now create an assigned to-do item");
		
		log.info("INFO: Now clicking on the 'Add To Do Item' button in Activities UI");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);
		
		log.info("INFO: Now entering the to-do item title into the 'To Do:' input field with content: " + baseActivityTodo.getTitle());
		driver.getFirstElement(ActivitiesUIConstants.ToDo_InputText_Title).clear();
		driver.getFirstElement(ActivitiesUIConstants.ToDo_InputText_Title).type(baseActivityTodo.getTitle().trim());
		
		log.info("INFO: Now clicking on the 'More Options' link in Activities UI");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		
		log.info("INFO: Now entering the tags for this to-do item into the 'Tags:' input field with content: " + baseActivityTodo.getTags());
		driver.getSingleElement(ActivitiesUIConstants.ToDo_InputText_Tags).clear();
		driver.getSingleElement(ActivitiesUIConstants.ToDo_InputText_Tags).type(baseActivityTodo.getTags().trim());
		
		log.info("INFO: Now changing the 'Assigned To:' drop down menu selection to the 'One or more activity members' selection");
		driver.getFirstElement(ActivitiesUIConstants.ToDo_AssignTo_Pulldown).useAsDropdown().selectOptionByVisibleText((Data.getData().ToDo_ChooseAPerson));
		
		log.info("INFO: Now clicking on the checkbox to assign " + userBeingAssignedTheTodo.getDisplayName() + " to the to-do item");
		ui.clickLinkWait(ActivitiesUIConstants.Todo_Select_AssignedUser + "[title='" + userBeingAssignedTheTodo.getDisplayName() + "']");
		
		// Switch focus to the status update frame - this is the same frame as the Description frame for the to-do item in the UI
		UIEvents.switchToStatusUpdateFrame(ui);
		
		log.info("INFO: Now typing in the description text with content: " + baseActivityTodo.getDescription());
		UIEvents.typeStringWithNoDelay(ui, baseActivityTodo.getDescription().trim());
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		log.info("INFO: Now clicking on the 'Save' button to create the assigned to-do item");
		ui.clickLinkWait(ActivitiesUIConstants.Todo_SaveButton_AssignUser);
		
		log.info("INFO: Verify that the new activity to-do item link is now displayed in Activities UI");
		String todoItemLink = ActivitiesUIConstants.Activity_Todo_Item_Link.replaceAll("PLACEHOLDER", baseActivityTodo.getTitle().trim());
		Assert.assertTrue(ui.fluentWaitPresent(todoItemLink), 
							"ERROR: The activity to-do item link was NOT displayed in Activities UI");
	}
	
	/**
	 * Logs into ActivitiesUI, navigates to the specified activity and then creates an assigned to-do item using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to in the UI
	 * @param todoItemToBeCreated - The BaseActivityToDo instance of the to-do item to be created
	 * @param userToBeLoggedIn - The User instance of the user to log in and navigate to the activity
	 * @param userBeingAssignedTheTodo - The User instance of the user to be assigned the to-do item
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToActivityAndCreateAssignedTodo(HomepageUI ui, RCLocationExecutor driver, Activity activityToNavigateTo, BaseActivityToDo todoItemToBeCreated, 
																		User userToBeLoggedIn, User userBeingAssignedTheTodo, boolean preserveInstance) {
		// Log in to Activities UI and navigate to the activity
		loginAndNavigateToActivity(ui, activityToNavigateTo, userToBeLoggedIn, preserveInstance);
		
		// Create an assigned to-do item to the specified user in the UI
		createAssignedTodoUsingUI(ui, driver, todoItemToBeCreated, userToBeLoggedIn, userBeingAssignedTheTodo);
	}
	
	/**
	 * Change the access of an activity to Public using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userMakingActivityPublic - The User instance of the user marking the activity as public
	 * @param isOnPremise - True if the test is being run On Premise, false if it is on Smart Cloud
	 */
	public static void makeActivityPublicUsingUI(HomepageUI ui, User userMakingActivityPublic, boolean isOnPremise) {
		
		log.info("INFO: " + userMakingActivityPublic.getDisplayName() + " will now make the activity public using the UI");
		
		log.info("INFO: Now selecting 'Members' from the Activities UI left nav menu");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Members);
		
		if(isOnPremise) {
			log.info("INFO: Clicking on the 'Change' link in Activities UI");
			ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.Change_Access);
			ui.getFirstVisibleElement(ActivitiesUIConstants.Change_Access).click();
			
			log.info("INFO: Now selecting the radio button for 'Public' from the Public Access dialog");
			ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.PublicAccess_RadioBtn);
			ui.getFirstVisibleElement(ActivitiesUIConstants.PublicAccess_RadioBtn).click();
			
			log.info("INFO: Now clicking on the 'Save' button to save all changes and mark the activity as public");
			UIEvents.clickSaveButton(ui);
			
			log.info("INFO: Verify that the access for the activity has changed to 'Public' in the UI");
			Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.Activity_Public_Access_On_Prem),
								"ERROR: The access for the activity did NOT change to 'Public' in the UI as expected");
		} else {
			log.info("INFO: Clicking on the 'Change' link in Activities UI");
			ui.clickLinkWait(ActivitiesUIConstants.Change_Access_SC);
			
			log.info("INFO: Now selecting the radio button for 'Everyone' from the Access dialog");
			ui.clickLinkWait(ActivitiesUIConstants.PublicAccess_RadioBtn_SC);
			
			log.info("INFO: Now clicking on the 'Save' button to save all changes and mark the activity as public");
			ui.clickLinkWait(ActivitiesUIConstants.SaveButton_PermissionChange_SC);
			
			log.info("INFO: Verify that the access for the activity has changed to 'Everyone' in the UI");
			Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.Activity_Public_Access_SC),
								"ERROR: The access for the activity did NOT change to 'Everyone' in the UI as expected");
		}
	}
	
	/**
	 * Logs into ActivitiesUI, navigates to the specified activity and then changes the access of an activity to Public using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityToMakePublic - The Activity instance of the activity to navigate to in the UI and be made public
	 * @param userToBeLoggedIn - The User instance of the user to log in and navigate to the activity
	 * @param isOnPremise - True if the test is being run On Premise, false if it is on Smart Cloud
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToActivityAndMakeActivityPublic(HomepageUI ui, Activity activityToMakePublic, User userToBeLoggedIn, boolean isOnPremise, boolean preserveInstance) {
		
		// Log in to Activities UI and navigate to the activity
		loginAndNavigateToActivity(ui, activityToMakePublic, userToBeLoggedIn, preserveInstance);
		
		// Mark the activity as public
		makeActivityPublicUsingUI(ui, userToBeLoggedIn, isOnPremise);
	}
	
	/**
	 * Deletes a reply posted to an entry / to-do item
	 * 
	 * @param replyToBeDeleted - The Reply instance of the reply to be deleted
	 * @param userDeletingReply - The User instance of the user deleting the reply
	 * @param apiUserDeletingReply - The APIActivitiesHandler instance of the user deleting the reply
	 */
	public static void deleteReply(Reply replyToBeDeleted, User userDeletingReply, APIActivitiesHandler apiUserDeletingReply) {
		
		log.info("INFO: " + userDeletingReply.getDisplayName() + " will now delete the comment with content: " + replyToBeDeleted.getContent().trim());
		boolean deletedReply = apiUserDeletingReply.deleteEntryComment(replyToBeDeleted);
		
		log.info("INFO: Verify that the reply was deleted successfully");
		Assert.assertTrue(deletedReply, 
							"ERROR: The reply was NOT deleted - the API method returned a negative response");
	}
	
	/**
	 * Logs in to the specified activity as the specified user, opens the options for the specified activity entry and notifies the specified user about the activity entry
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param parentActivity - The Activity instance of the activity which contains the activity entry
	 * @param activityEntry - The ActivityEntry instance of the activity entry which the user is to be notified about
	 * @param userToBeLoggedIn - The User instance of the user to log in and navigate to the activity
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNotifyUserAboutActivityEntryUsingUI(HomepageUI ui, Activity parentActivity, ActivityEntry activityEntry, User userToBeLoggedIn, APIProfilesHandler apiUserToNotify, boolean preserveInstance) {
		
		// Log in to Activities UI and navigate to the activity
		loginAndNavigateToActivity(ui, parentActivity, userToBeLoggedIn, preserveInstance);
		
		// Notify the specified user about the activity entry
		notifyUserAboutActivityEntryUsingUI(ui, activityEntry, apiUserToNotify);
	}
	
	/**
	 * Deletes the specified to-do item
	 * 
	 * @param todoItemToBeDeleted - The Todo instance of the to-do item to be deleted
	 * @param userDeletingTodoItem - The User instance of the user who is deleting the to-do item
	 * @param apiUserDeletingTodoItem - The APIActivitiesHandler instance of the user who is deleting the to-do item
	 */
	public static void deleteTodoItem(Todo todoItemToBeDeleted, User userDeletingTodoItem, APIActivitiesHandler apiUserDeletingTodoItem) {
		
		log.info("INFO: " + userDeletingTodoItem.getDisplayName() + " will now delete the to-do item with title: " + todoItemToBeDeleted.getTitle());
		boolean deletedTodoItem = apiUserDeletingTodoItem.deleteToDoItem(todoItemToBeDeleted);
		
		log.info("INFO: Verify that the to-do item has been deleted successfully");
		Assert.assertTrue(deletedTodoItem, 
							"ERROR: The to-do item could NOT deleted with title: " + todoItemToBeDeleted.getTitle());
	}
	
	/**
	 * Deletes the specified activity
	 * 
	 * @param activityToBeDeleted - The Activity instance of the activity to be deleted
	 * @param userDeletingActivity - The User instance of the user who is deleting the activity
	 * @param apiUserDeletingActivity - The APIActivitiesHandler instance of the user who is deleting the activity
	 */
	public static void deleteActivity(Activity activityToBeDeleted, User userDeletingActivity, APIActivitiesHandler apiUserDeletingActivity) {
		
		log.info("INFO: " + userDeletingActivity.getDisplayName() + " will now delete the activity with title: " + activityToBeDeleted.getTitle());
		apiUserDeletingActivity.deleteActivity(activityToBeDeleted);
	}
	
	/**
	 * Re-opens an activity to-do item using the UI
	 * 
	 * @param uiAct - The ActivitiesUI instance to invoke all relevant methods
	 * @param todo - The Todo instance of the to-do item to be re-opened
	 */
	private static void reopenToDoItemUsingUI(ActivitiesUI uiAct, Todo todo) {
		
		// Re-open the to-do item using the UI
		uiAct.reopenToDoItemUsingUI(todo);
	}
	
	/**
	 * Notify the specified user about the specified activity entry (assumes the user has already navigated to Activities UI)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityEntry - The ActivityEntry instance of the activity entry which the user is to be notified about
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified
	 */
	private static void notifyUserAboutActivityEntryUsingUI(HomepageUI ui, ActivityEntry activityEntry, APIProfilesHandler apiUserToNotify) {
		
		log.info("INFO: Now clicking on the 'More' link to expand on the activity entry options in Activities UI");
		String activityEntryExpandCSS = ActivitiesUIConstants.ActivityEntry_ExpandThisEntry.replace("PLACEHOLDER", activityEntry.getTitle());
		ui.clickLinkWait(activityEntryExpandCSS);
		
		log.info("INFO: Now retrieving the ID for the activity entry");
		String activityEntryId = activityEntry.getId().toString();
		activityEntryId = activityEntryId.substring(activityEntryId.lastIndexOf(':') + 1);
		log.info("INFO: The ID for the activity entry has been retrieved: " + activityEntryId);
		
		log.info("INFO: Now clicking on the 'More Actions' link to expand on the activity entry options in Activities UI");
		String activityEntryMoreActionsCSS = ActivitiesUIConstants.ActivityEntry_MoreActions.replace("PLACEHOLDER", activityEntryId);
		ui.clickLinkWait(activityEntryMoreActionsCSS);
		
		log.info("INFO: Now clicking on the 'Notify Other People' option from the 'More Actions' menu for the activity entry");
		ui.clickLinkWait(ActivitiesUIConstants.actionNotify);
		
		log.info("INFO: Now clicking on the checkbox for the user with user name: " + apiUserToNotify.getDesplayName());
		String userCheckboxCSS = ActivitiesUIConstants.ActivityEntry_SelectUser_Checkbox.replace("PLACEHOLDER", apiUserToNotify.getUUID());
		ui.clickLinkWait(userCheckboxCSS);
		
		log.info("INFO: Now entering a notification message into the 'Notify Message:' field with content: " + Data.getData().ActivityEntryNotificationMessage);
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry_Notify_Message);
		ui.typeStringWithNoDelay(Data.getData().ActivityEntryNotificationMessage);
		
		log.info("INFO: Now clicking on the 'Send' button in the UI");
		ui.clickLinkWait(ActivitiesUIConstants.ActivityEntry_SendButton);
		
		log.info("INFO: Verify that the notification success message is displayed in the UI");
		ui.fluentWaitTextPresent(Data.getData().ActivityEntry_NotificationSent_DialogBoxHeading);
		ui.fluentWaitTextPresent(Data.getData().ActivityEntry_NotificationSent_DialogSuccessMessage);
		
		log.info("INFO: Click on 'OK' to close the dialog box");
		ui.clickLinkWait(ActivitiesUIConstants.Delete_Activity_Dialogue_OkButton);
	}
}