package com.ibm.conn.auto.util.eventBuilder.community;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityActivityEvents {
	
	private static Logger log = LoggerFactory.getLogger(CommunityActivityEvents.class);
	
	/**
	 * Creates a community activity
	 * 
	 * @param baseActivity - The BaseActivity instance of the community activity which is to be created
	 * @param baseCommunity - The BaseCommunity instance of the community in which the activity is to be created
	 * @param userCreatingActivity - The User instance of the user who is creating the community activity
	 * @param apiUserCreatingActivity - The APIActivitiesHandler instance of the user who is creating the community activity
	 * @param apiCommunityOwner - The APICommunitiesHandler instance of the user who is creating the community activity
	 * @param community - The Community instance of the community in which the activity is to be created
	 * @return activity - An Activity object
	 */
	public static Activity createCommunityActivity(BaseActivity baseActivity, BaseCommunity baseCommunity, User userCreatingActivity, APIActivitiesHandler apiUserCreatingActivity, APICommunitiesHandler apiCommunityOwner, Community community){

		log.info("INFO: Set UUID of community: " + community.getTitle());
		baseCommunity.setCommunityUUID(apiCommunityOwner.getCommunityUUID(community)); 

		log.info("INFO: " + userCreatingActivity.getDisplayName() + " will now create a community activity with title: " + baseCommunity.getName());
		Activity activity = apiUserCreatingActivity.createActivity(baseActivity, baseCommunity);

		log.info("INFO: Verify that the new activity was created successfully");
		Assert.assertNotNull(activity, "ERROR: The activity was NOT created successfully and was returned as null");

		return activity;
	}
	
	/**
	 * Adds a single follower to an activity
	 * 
	 * @param activity - The Activity instance of the activity to be followed
	 * @param apiUserFollowingActivity - The APIActivitiesHandler instance of the user to follow the activity
	 */
	public static void addFollowerSingleUser(Activity activity, APIActivitiesHandler apiUserFollowingActivity) {
		
		// Have the specified user follow the activity
		ActivityEvents.addFollowerSingleUser(activity, apiUserFollowingActivity);
	}
	
	/**
	 * Creates a community activity and allows a single user to follow that activity
	 * 
	 * @param baseActivity - The BaseActivity instance of the community activity which is to be created
	 * @param baseCommunity - The BaseCommunity instance of the community in which the activity is to be created
	 * @param userCreatingActivity - The User instance of the user who is creating the community activity
	 * @param apiUserCreatingActivity - The APIActivitiesHandler instance of the user who is creating the community activity
	 * @param apiCommunityOwner - The APICommunitiesHandler instance of the user who is creating the community activity
	 * @param apiUserFollowingActivity - The APIActivitiesHandler instance of the user who is to follow the activity
	 * @param community - The Community instance of the community in which the activity is to be created
	 * @return activity - An Activity object
	 */
	public static Activity createCommunityActivityWithOneFollower(BaseActivity baseActivity, BaseCommunity baseCommunity, User userCreatingActivity, APIActivitiesHandler apiUserCreatingActivity, APICommunitiesHandler apiCommunityOwner, APIActivitiesHandler apiUserFollowingActivity, Community community){

		// Create the community activity
		Activity newActivity = createCommunityActivity(baseActivity, baseCommunity, userCreatingActivity, apiUserCreatingActivity, apiCommunityOwner, community);
		
		// Have the specified user follow the activity
		addFollowerSingleUser(newActivity, apiUserFollowingActivity);
		
		return newActivity;
	}
	
	/**
	 * Creates an activity entry
	 * 
	 * @param entryCreator - The User instance of the user who is creating the community activity entry
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the community activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the community activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @return - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntry(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity) {
		
		//Create the activity entry
		return ActivityEvents.createActivityEntry(entryCreator, apiEntryCreator, baseActivityEntry, activity);
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

		// Edits the description of the specified activity entry
		return ActivityEvents.editEntryDescription(entryEditor, apiEntryEditor, activityEntry, newContent);
	}
	
	/**
	 * Creates an activity entry and edits the description of that entry
	 * 
	 * @param parentActivity - The Activity instance of the activity in which the entry is to be created
	 * @param baseActivityEntry - The BaseActivityEntry instance of the entry to be created
	 * @param editedDescription - The description to which the entry description will be updated to after creation
	 * @param userCreatingEntry - The User instance of the user creating and editing the entry
	 * @param apiUserCreatingEntry - The APIActivitiesHandler instance of the user creating and editing the entry
	 * @return - An ActivityEntry object
	 */
	public static ActivityEntry createEntryAndEditDescription(Activity parentActivity, BaseActivityEntry baseActivityEntry, String editedDescription, 
																		User userCreatingEntry, APIActivitiesHandler apiUserCreatingEntry) {
		// Create the activity entry and edit the description
		return ActivityEvents.createEntryAndEditDescription(userCreatingEntry, apiUserCreatingEntry, baseActivityEntry, parentActivity, editedDescription);
	}
	
	/**
	 * Creates / posts a comment to an activity entry / to-do item
	 * 
	 * @param parentActivity - The Activity instance of the parent activity which contains the entry / to-do item to be commented on
	 * @param parentEntry - The ActivityEntry instance of the activity entry to which the comment will be added, null if this comment is to be added to a to-do item
	 * @param parentTodo - The Todo instance of the to-do item to which the comment will be added, null if this comment is to be added to an activity entry
	 * @param comment - A String object containing the content of the comment
	 * @param commentCreator - The User instance of the user who is creating the comment
	 * @param apiCommentCreator - The APIActivitiesHandler instance of the user who is creating the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return - A Reply object
	 */
	public static Reply createComment(Activity parentActivity, ActivityEntry parentEntry, Todo parentTodo, String comment, User commentCreator, APIActivitiesHandler apiCommentCreator, boolean isPrivate) {
		
		// Post a comment to an activity entry / to-do item
		return ActivityEvents.createComment(parentActivity, parentEntry, parentTodo, comment, commentCreator, apiCommentCreator, isPrivate);
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
		
		// Post multiple comments to the activity entry / to-do item
		return ActivityEvents.createMultipleComments(parentActivity, parentEntry, parentTodo, comments, commentCreator, apiCommentCreator, isPrivate);
	}
	
	/**
	 * Edits / updates a comment (Reply) posted to a community activity entry / to-do item
	 * 
	 * @param commentEditor - The User instance of the user who is editing the comment
	 * @param apiCommentEditor - The APIActivitiesHandler instance of the user who is editing the comment
	 * @param commentReply - The Reply instance of the comment which is to be edited
	 * @param commentEdit - A String object which contains the new content for the comment
	 * @return - The updated Reply instance
	 */
	public static Reply editComment(User commentEditor, APIActivitiesHandler apiCommentEditor, Reply commentReply, String commentEdit) {
		
		// Edit the comment posted to the activity entry / to-do item
		return ActivityEvents.editComment(commentEditor, apiCommentEditor, commentReply, commentEdit);
	}
	
	/**
	 * Creates an activity entry and adds a comment
	 * 
	 * @param entryCreator - The User instance of the user who is creating the community activity entry and comment
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the community activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the community activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param entryComment - A String object containing the content of the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntryAndComment(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, String entryComment, boolean isPrivate){
		
		//Create the activity entry and add a comment
		return ActivityEvents.createActivityEntryAndComment(entryCreator, apiEntryCreator, baseActivityEntry, activity, entryComment, isPrivate);
	}
	
	/**
	 * Creates an activity entry and adds a comment and then edits the comment
	 * 
	 * @param entryCreator - The User instance of the user who is creating the community activity entry and comment
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the community activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the community activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param entryComment - A String object containing the content of the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @param commentEdit - A String object which contains the new content for the comment
	 * @return - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntryAndAddCommentAndEditComment(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, String entryComment, boolean isPrivate, String commentEdit){
		
		//Create the activity entry and add a comment and then edit the comment
		return ActivityEvents.createActivityEntryAndAddCommentAndEditComment(entryCreator, apiEntryCreator, baseActivityEntry, activity, entryComment, isPrivate, commentEdit);
	}
	
	/**
	 * Creates an activity entry and then edits the entry's description
	 * 
	 * @param entryCreator - The User instance of the user who is creating the community activity entry
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the community activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the community activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param newContent - A String object containing the content to which the activity entry's description will be updated
	 * @return - An ActivityEntry object
	 */
	public static ActivityEntry createEntryAndEditDescription(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, String newContent){
		
		//Create the activity entry and then edit the entry
		return ActivityEvents.createEntryAndEditDescription(entryCreator, apiEntryCreator, baseActivityEntry, activity, newContent);
	}
	
	/**
	 * Creates an activity to-do item
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @return todo - A Todo object
	 */
	public static Todo createActivityTodo(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity) {
		
		// Create the activity todo
		return ActivityEvents.createTodo(todoCreator, apiTodoCreator, baseTodo, activity);
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

		// Edit the description of the specified to-do item
		return ActivityEvents.editTodoDescription(todoEditor, apiTodoEditor, todoToEdit, newDescription);
	}
	
	/**
	 * Creates an activity to-do item and then edits the to-do item's description
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created and edited
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @param newDescription - The String content to which the to-do item description will be set after the update
	 * @return todo - A Todo object
	 */
	public static Todo createTodoAndEditDescription(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity, String newDescription) {
		
		// Create the activity todo and then edit the description
		return ActivityEvents.createTodoAndEditDescription(todoCreator, apiTodoCreator, baseTodo, activity, newDescription);
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
		
		return ActivityEvents.markToDoItemAsCompleteOrIncomplete(userChangingTodoStatus, apiUserChangingTodoStatus, todo, markAsComplete);
	}
	
	/**
	 * Creates an activity to-do item and then marks it as complete
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created and completed
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @return todo - A Todo object
	 */
	public static Todo createTodoAndMarkAsCompleted(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity) {
		
		// Create the activity todo and complete it
		return ActivityEvents.createTodoAndMarkAsCompleted(todoCreator, apiTodoCreator, baseTodo, activity);
	}
	
	/**
	 * Creates an activity to-do item, marks it as complete and then reopens it
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created, completed and reopened
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @return todo - A Todo object
	 */
	public static Todo createTodoAndMarkAsCompletedAndReopen(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseTodo, Activity activity) {
		
		// Create the activity todo and complete it
		return ActivityEvents.createTodoAndMarkAsCompletedAndReopen(todoCreator, apiTodoCreator, baseTodo, activity);
	}
	
	/**
	 * Logs into Communities UI and navigates to the specified community activity
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to navigate to
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to
	 * @param userLoggingIn - The User instance of the user to log in and navigate to the community activity
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to log in and navigate to the community activity
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToCommunityActivity(HomepageUI ui, CommunitiesUI uiCo, Community community, BaseCommunity baseCommunity, Activity activityToNavigateTo,
															User userLoggingIn, APICommunitiesHandler apiUserLoggingIn, boolean preserveInstance) {
		
		// Log into Communities UI and navigate to the communities UI screen
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to the community activity in the UI
		navigateToCommunityActivity(uiCo, ui, activityToNavigateTo);
	}
	
	/**
	 * Logs in to Communities UI, navigates to the specified community activity and follows that activity
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param community - The Community instance of the community to navigate to
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to
	 * @param activityToFollow - The Activity instance of the activity to be followed
	 * @param userFollowingActivity - The User instance of the user who is to log in and follow the activity
	 * @param apiUserFollowingActivity - The APICommunitiesHandler instance of the user to log in and follow the activity
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndFollowCommunityActivity(HomepageUI ui, CommunitiesUI uiCo, Community community, BaseCommunity baseCommunity, Activity activityToFollow,
														User userFollowingActivity, APICommunitiesHandler apiUserFollowingActivity, boolean preserveInstance) {
		// Login and navigate to the activity UI screen
		loginAndNavigateToCommunityActivity(ui, uiCo, community, baseCommunity, activityToFollow, userFollowingActivity, apiUserFollowingActivity, preserveInstance);
		
		// Follow the activity
		followCommunityActivity(ui);
	}
	
	/**
	 * Creates an activity entry and adds a comment containing a mention to that entry
	 * 
	 * @param entryCreator - The User instance of the user who is creating the community activity entry and comment
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user who is creating the community activity entry
	 * @param baseActivityEntry - The BaseActivityEntry instance of the community activity entry which is to be created
	 * @param activity - The Activity instance of the activity to which the activity entry will be added
	 * @param mention - A Mentions object which contains the content of the comment with the mention
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return - An ActivityEntry object
	 */
	public static ActivityEntry createActivityEntryAndAddCommentWithMentions(User entryCreator, APIActivitiesHandler apiEntryCreator, BaseActivityEntry baseActivityEntry, Activity activity, Mentions mention, boolean isPrivate){

		// Create the activity entry and add a comment with a mention
		return ActivityEvents.createActivityEntryAndAddCommentWithMentions(entryCreator, apiEntryCreator, baseActivityEntry, activity, mention, isPrivate);
	}

	/**
	 * 
	 * Creates a to-do item and adds a comment containing a mention to that item
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @param mention - A Mentions object which contains the content of the comment with the mention
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return - A Todo object
	 */
	public static Todo createActivityTodoAndAddCommentWithMentions(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseActivityTodo, Activity activity, Mentions mention, boolean isPrivate){

		// Create the activity todo and add a comment with a mention
		return ActivityEvents.createActivityTodoAndAddCommentWithMentions(todoCreator, apiTodoCreator, baseActivityTodo, activity, mention, isPrivate);
	}
	
	/**
	 * Creates an activity to-do item and add a comment
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created and commented on
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @param comment - A String object containing the content of the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @return - A Todo object
	 */
	public static Todo createActivityTodoAndAddComment(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseActivityTodo, Activity activity, String comment, boolean isPrivate){
		
		// Create the community activity todo
		Todo todo = createActivityTodo(todoCreator, apiTodoCreator, baseActivityTodo, activity);
				
		// Add a comment to the to-do item
		createComment(activity, null, todo, comment, todoCreator, apiTodoCreator, isPrivate);
		
		return todo;
	} 
	
	/**
	 * Creates an activity to-do item, add a comment and then edit the comment
	 * 
	 * @param todoCreator - The User instance of the user who is creating the community activity to-do item
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user who is creating the community activity to-do item
	 * @param baseTodo - The BaseActivityToDo instance of the to-do item which is to be created and commented on
	 * @param activity - The Activity instance of the activity to which the activity to-do item will be added
	 * @param comment - A String object containing the content of the comment
	 * @param isPrivate - A boolean variable which determines whether or not the comment will be private, true if the comment is to be private, false if it is to be public
	 * @param commentEdit - A String object which contains the new content for the comment
	 * @return - A Todo object
	 */
	public static Todo createActivityTodoAndAddCommentAndEditComment(User todoCreator, APIActivitiesHandler apiTodoCreator, BaseActivityToDo baseActivityTodo, Activity activity, String comment, boolean isPrivate, String commentEdit){
		
		// Create the community activity todo
		Todo todo = createActivityTodo(todoCreator, apiTodoCreator, baseActivityTodo, activity);
				
		// Add a comment to the to-do item
		Reply commentReply = createComment(activity, null, todo, comment, todoCreator, apiTodoCreator, isPrivate);
				
		// Edit the comment posted to the to-do item
		editComment(todoCreator, apiTodoCreator, commentReply, commentEdit);
		
		return todo;
	}
	
	/**
	 * Creates an activity entry which includes mentions to the specified user
	 * 
	 * @param activity - The Activity instance of the activity in which the entry is to be created
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param entryCreator - The User instance of the user to create the activity entry
	 * @param apiEntryCreator - The APIActivitiesHandler instance of the user to create the activity entry
	 * @param isPrivateEntry - True if the activity entry is to be a private entry, false otherwise
	 * @return - The ActivityEntry instance of the activity entry
	 */
	public static ActivityEntry createActivityEntryWithMentions(Activity activity, Mentions mentions, User entryCreator, APIActivitiesHandler apiEntryCreator, boolean isPrivateEntry) {
		
		// Create an activity entry with mentions to the specified user
		return ActivityEvents.createActivityEntryWithMentions(activity, mentions, entryCreator, apiEntryCreator, isPrivateEntry);
	}
	
	/**
	 * Creates an activity to-do which includes mentions to the specified user
	 * 
	 * @param activity - The Activity instance of the activity in which the to-do item is to be created
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param todoCreator - The User instance of the user to create the activity to-do
	 * @param apiTodoCreator - The APIActivitiesHandler instance of the user to create the activity to-do
	 * @param isPrivateTodo - True if the activity to-do is to be a private entry, false otherwise
	 * @return - The ActivityEntry instance of the activity entry
	 */
	public static Todo createActivityTodoWithMentions(Activity activity, Mentions mentions, User todoCreator, APIActivitiesHandler apiTodoCreator, boolean isPrivateTodo) {
		
		// Create an activity to-do item with mentions to the specified user
		return ActivityEvents.createActivityTodoWithMentions(activity, mentions, todoCreator, apiTodoCreator, isPrivateTodo);
	}
	
	/**
	 * Navigates to a community activity in the UI (expects that you are at the Communities UI screen before using this method)
	 *  
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param activityToNavigateTo - The Activity instance of the activity to navigate to
	 */
	private static void navigateToCommunityActivity(CommunitiesUI uiCo, HomepageUI ui, Activity activityToNavigateTo) {
		
		log.info("INFO: Select activities from the left navigation menu");
		Community_LeftNav_Menu.ACTIVITIES.select(uiCo);
		
		log.info("INFO: Wait for the community activities UI screen to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseActivities);
		
		// Navigate to the activity in the UI
		ActivityEvents.navigateToActivity(ui, activityToNavigateTo);
	}
	
	/**
	 * Follows any community activity in the UI (expects that you have opened the activity to be followed before using this method)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void followCommunityActivity(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the 'Following Actions' tab");
		ui.clickLinkWait(ActivitiesUIConstants.CommunityFolowMenu);
		
		log.info("INFO: Now clicking on the visible 'Follow this Activity' link from the menu");
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivityFollow);
		
		log.info("INFO: Verify that the follow action success message is displayed before proceeding");
		ui.fluentWaitTextPresent(Data.getData().Activity_Followed_Success_Message);
	}
}