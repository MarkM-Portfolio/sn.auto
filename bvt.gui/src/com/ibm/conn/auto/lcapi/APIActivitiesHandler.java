package com.ibm.conn.auto.lcapi;

import static org.junit.Assert.assertTrue;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;


public class APIActivitiesHandler extends APIHandler<ActivitiesService> {
	
	private static final Logger log = LoggerFactory.getLogger(APIActivitiesHandler.class);
	private String userName;
	
	public APIActivitiesHandler(String product, String serverURL, String username, String password) {

		super("activities", serverURL, username, password);
		userName = username;
		
	}
	
	@Override
	protected ActivitiesService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		ActivitiesService activitiesService = null;
		try {
			activitiesService =  new ActivitiesService(abderaClient, generalService, false);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Activities service: " + e.getMessage());
		}
		return activitiesService;
	}

	
	public Activity createActivity(BaseActivity activity) {

		boolean partOfCommunity = activity.getCommunity() != null;
		
		Date dueDate;
		
		//if no due date was provided initialize a Date
		if (activity.getDueDate()==null){
			dueDate = new Date();
		}else{
			dueDate = activity.getDueDate().getTime();
		}

		log.info("API: Creating Activity:");

		Activity simpleActivity = new Activity(activity.getName(), activity.getGoal(), activity.getTags(), dueDate, activity.getComplete(), partOfCommunity);
		
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		log.info("Activity Headers: " + service.getDetail());
		log.info("Checking return code of createActivity call, it should be 201");
		int responseCode = service.getRespStatus();
		if (responseCode != 201){
			log.info("Activity not created successfully through API, User name: " + userName);
			Assert.fail("User: " + userName + " received response: " 
					+ responseCode + "; expected: 201; Activity was not created");
		}
		log.info("Activity successfully created through API");
		log.info("Retrieve that Activity for full info");
		log.info("createActivity activity result  is :" + activityResult);
		Activity newActivity = new Activity(activityResult);
		newActivity.setLinks(getActivityLinks(activityResult));
		if (APIUtils.resultSuccess(activityResult, "Activities")) {
			return newActivity;
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a standalone activity
	 * This method supports both private and public activities in both OnPrem and SmartCloud
	 * 
	 * @param baseActivity - The BaseActivity from which the activity will be created
	 * @param isOnPrem - True if the test is running OnPrem, False if it is running on SmartCloud
	 * @return - The newly created activity if the operation is successful, null otherwise
	 */
	public Activity createActivity(BaseActivity baseActivity, boolean isOnPrem) {
		
		log.info("INFO: Creating a new standalone activity with title: " + baseActivity.getName());
		
		// Set the due date for the activity to be created
		Date activityDueDate;
		if (baseActivity.getDueDate() == null) {
			activityDueDate = new Date();
		} else {
			activityDueDate = baseActivity.getDueDate().getTime();
		}
		
		// Create the activity instance to be created with the POST request
		Activity activity = new Activity(baseActivity.getName(), baseActivity.getGoal(), baseActivity.getTags(), 
											activityDueDate, baseActivity.getComplete(), false);
		log.info("INFO: The activity entry to be POSTed to the server has been created: " + activity.toEntry().toString());
		
		Entry createdActivityEntry = (Entry) service.createActivity(activity);
		
		if(APIUtils.resultSuccess(createdActivityEntry, "Activities")) {
			log.info("INFO: New activity successfully created with title: " + createdActivityEntry.getTitle());
			if(baseActivity.isPublic()) {
				if(!makeActivityPublic(createdActivityEntry, isOnPrem)) {
					log.info("ERROR: The process to make the activity public has failed");
					return null;
				}
			}
		} else {
			log.info("ERROR: The new activity could not be created");
			log.info(createdActivityEntry.toString());
			return null;
		}
		
		// Create an activity instance based on the created activity and return it
		Activity createdActivity = new Activity(createdActivityEntry);
		createdActivity.setLinks(getActivityLinks(createdActivityEntry));
		return createdActivity;
	}
	
	/**
	 * Makes a standalone activity public
	 * 
	 * @param activityEntry - The Entry instance of the activity to be made public
	 * @param isOnPrem - True if the test is an OnPrem test, False if it is a SmartCloud test
	 * @return - True if the operation is successful, False otherwise
	 */
	public boolean makeActivityPublic(Entry activityEntry, boolean isOnPrem) {
		
		log.info("INFO: Making the activity public with title: " + activityEntry.getTitle());
		Entry publicMemberEntry;
		
		if(isOnPrem) {
			log.info("INFO: Creating the public member entry to make the activity public for the OnPrem environment");
			publicMemberEntry = new Member("*", "*", Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP).toEntry();
		} else {
			log.info("INFO: Creating the public member entry to make the activity public for the SmartCloud environment");
			
			// Retrieve the list of members for this activity
			Feed membersFeed = (Feed) service.getFeed(activityEntry.getLink(StringConstants.REL_MEMBERS).getHref().toString());
			log.info("INFO: The members list for the activity has been retrieved: " + membersFeed.toString().replaceAll("\n", ""));
			
			// Retrieve the activities original author element
			Person feedAuthor = membersFeed.getAuthor();
			log.info("INFO: The author element of the activity has been retrieved: " + feedAuthor.toString().replaceAll("\n", ""));
			
			// From the author element - retrieve the user ID
			String snxUserId = feedAuthor.toString();
			snxUserId = snxUserId.substring(snxUserId.indexOf("<snx:userid"), snxUserId.indexOf("</snx:userid>"));
			snxUserId = snxUserId.substring(snxUserId.lastIndexOf('>') + 1);
			log.info("INFO: The user ID has been retrieved from the author element: " + snxUserId);
			
			// Create a new member instance using the user ID and clear the default contributors list
			Member publicMember = new Member(null, snxUserId, Component.ACTIVITIES, Role.MEMBER, null);
			publicMember.getContributors().clear();
			
			// Create the new contributor - base it on the details stored in the author
			Person contributor = Abdera.getNewFactory().newContributor();
			for(Element currentElement : feedAuthor.getExtensions()) {
				if(currentElement.toString().indexOf("snx:role") > -1) {
					currentElement.setText("member");
				}
				contributor.addExtension(currentElement);
			}
			
			// Set the new contributor to the new member
			publicMember.addContributor(contributor);
			
			// Change the category of this member to "organization"
			publicMemberEntry = publicMember.toEntry();
			publicMemberEntry.getCategories().get(0).setAttributeValue("term", "organization");
		}
		log.info("INFO: The public member entry for making the activity public has been created: " + publicMemberEntry.toString());
		
		// POST the new member to the URL
		Entry publicActivityResponse = (Entry) service.postFeed(activityEntry.getLink(StringConstants.REL_MEMBERS).getHref().toString(), publicMemberEntry);
		
		if(publicActivityResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The activity was successfully made public");
			return true;
		} else {
			log.info("ERROR: The activity could not be made public");
			log.info(publicActivityResponse.toString());
			return false;
		}
	}
	
	/**
	 * Creates a new activity in a community
	 * 
	 * @param baseActivity - The BaseActivity instance of the activity to be created
	 * @param baseCommunity - The BaseCommunity instance of the community in which the activity will be created
	 * @return - The Activity instance of the newly created community activity if all actions are created successfully, null otherwise
	 */
	public Activity createActivity(BaseActivity baseActivity, BaseCommunity baseCommunity) {

		log.info("INFO: Now creating a new community activity with title: " + baseActivity.getName());
		
		// Set additional attributes required for the creation of the activity
		boolean partOfCommunity = baseActivity.getCommunity() != null;
		
		Date dueDate;
		if (baseActivity.getDueDate()==null){
			// If no due date has been provided then initialise a date
			dueDate = new Date();
		} else {
			dueDate = baseActivity.getDueDate().getTime();
		}

		log.info("INFO: Now creating an Entry instance of the activity to be created");
		Activity activity = new Activity(baseActivity.getName(), baseActivity.getGoal(), baseActivity.getTags(), dueDate, baseActivity.getComplete(), partOfCommunity);
		Entry activityEntry = activity.toCommunityEntry(baseCommunity.getCommunityUUID().substring(14), "");
		log.info("INFO: The Entry instance of the new activity has been created: " + activityEntry.toString());
		
		log.info("INFO: Now creating the URL to which to POST the Entry instance of the activity");
		String postRequestURL = service.getServiceURLString() + URLConstants.ACTIVITIES_MY;
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		Entry postRequestResult = (Entry) service.postFeed(postRequestURL, activityEntry);
		
		if (APIUtils.resultSuccess(postRequestResult, "Activities")) {
			log.info("INFO: The community activity was created successfully");
			Activity newActivity = new Activity(postRequestResult);
			newActivity.setLinks(getActivityLinks(postRequestResult));
			return newActivity;
		} else {
			log.info("ERROR: The community activity could NOT be created");
			log.info(postRequestResult.toString());
			return null;
		}
	}
	
	/**
	 * Retrieves all of the relevant Links from the specified Entry instance
	 * 
	 * @param entryToRetrieveLinksFrom - The Entry instance from which all relevant Links (ie. Self link, Edit link etc.) will be retrieved from
	 * @return - The HashMap<String, Link> instance of all links in the correct format
	 */
	private HashMap<String, Link> getActivityLinks(Entry entryToRetrieveLinksFrom) {
		
		// Retrieve the Self and Edit links from the specified Entry
		HashMap<String, Link> activityLinks = new HashMap<String, Link>();
		activityLinks.put(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL, entryToRetrieveLinksFrom.getSelfLink());
		activityLinks.put(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML, entryToRetrieveLinksFrom.getEditLink());
		
		// Add a members list link if one exists in the entry response
		boolean membersLinkAdded = false;
		int index = 0;
		List<Link> listOfEntryLinks = entryToRetrieveLinksFrom.getLinks();
		while(index < listOfEntryLinks.size() && membersLinkAdded == false) {
			Link currentLink = listOfEntryLinks.get(index);
			
			if(currentLink.getRel().equals(StringConstants.REL_MEMBERS)) {
				activityLinks.put(StringConstants.REL_MEMBERS, currentLink);
				membersLinkAdded = true;
			}
			index ++;
		}
		return activityLinks;
	}

	/**
	 * Creates a public or private entry in an activity
	 * 
	 * @param name - The String content for the name of the entry to be created
	 * @param content - The String content for the description of the entry to be created
	 * @param tags - The String content for the tags to be set to the entry
	 * @param parentActivity - The Activity instance of the activity which will act as the parent activity for the entry
	 * @param isPrivate - The Boolean instance of whether the entry is to be a public entry (whereby this is set to false) or a private entry (set to true)
	 * @return - The ActivityEntry instance of the created activity entry if all actions are completed successfully, null otherwise
	 */
	public ActivityEntry createActivityEntry(String name, String content, String tags, Activity parentActivity, Boolean isPrivate){
		
		log.info("INFO: Now creating a new activity entry with title: " + name);
		
		log.info("INFO: Now creating an Entry instance of the new activity entry to be created");
		ActivityEntry activityEntry = new ActivityEntry(name, content, tags, 1, false, null, parentActivity.toEntry(), false);
		Entry activityEntryAsEntry = activityEntry.toEntry();
		
		if(isPrivate == true) {
			// Set the private category to the Entry to mark this activity entry as private
			activityEntryAsEntry.addCategory(StringConstants.SCHEME_FLAGS, "private", "Private");
		}
		log.info("INFO: The Entry instance of the new activity entry has been created: " + activityEntryAsEntry.toString());
		
		log.info("INFO: Now retrieving the URL to which the POST request to create the activity entry will be sent");
		String postRequestURL = parentActivity.getSelfLink();
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		Entry postRequestResult = (Entry) service.postFeed(postRequestURL, activityEntryAsEntry);
		
		if(postRequestResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The activity entry was successfully created");
			ActivityEntry activityEntryNew = new ActivityEntry(postRequestResult);
			activityEntryNew.setLinks(getActivityLinks(postRequestResult));
			return activityEntryNew;
		} else {
			log.info("ERROR: The activity entry could NOT be created");
			log.info(postRequestResult.toString());
			return null;
		}
	}
	
	/**
	 * Notifies the specified user about the specified activity entry
	 * 
	 * @param parentActivity - The Activity instance of the activity which contains the activity entry
	 * @param activityEntry - The ActivityEntry instance of the entry which the specified user will be notified about
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified about the entry
	 * @return - True if the notification has been sent successfully, false otherwise
	 */
	public boolean notifyUserAboutActivityEntry(Activity parentActivity, ActivityEntry activityEntry, APIProfilesHandler apiUserToNotify) {
		
		log.info("INFO: " + apiUserToNotify.getDesplayName() + " will now be notified about the activity entry with title: " + activityEntry.getTitle());
		
		log.info("INFO: Now retrieving the ID for the activity entry");
		String activityEntryId = activityEntry.getId().toString();
		activityEntryId = activityEntryId.substring(activityEntryId.lastIndexOf(':') + 1);
		log.info("INFO: The ID for the activity entry has been retrieved: " + activityEntryId);
		
		log.info("INFO: Now creating the URL to send the notification request to");
		String notifyURL = service.getServiceURLString() + "/service/html/notify?";
		notifyURL += "activityUuid=" + parentActivity.getActivityId();
		notifyURL += "&lconn_core_FilteringCheckbox_0notifyPerson=" + apiUserToNotify.getUUID();
		notifyURL += "&notifyMessage=" + Data.getData().ActivityEntryNotificationMessage.replace(" ", "%20");
		notifyURL += "&userids=" + apiUserToNotify.getUUID();
		notifyURL += "&uuid=" + activityEntryId;
		log.info("INFO: The URL for the notification request has been created: " + notifyURL);
		
		Entry postRequestResponse = (Entry) service.postFeed(notifyURL, Abdera.getNewFactory().newEntry());
		
		if(service.getRespStatus() >= 200 && service.getRespStatus() <= 204) {
			log.info("INFO: " + apiUserToNotify.getDesplayName() + " has been successfully notified about the activity entry with title: " + activityEntry.getTitle());
			return true;
		} else {
			log.info("ERROR: " + apiUserToNotify.getDesplayName() + " could NOT be notified about the activity entry");
			log.info("ERROR: " + postRequestResponse.toString());
			return false;
		}
	}

	/**
	 * Creates a public or private to-do item in an activity
	 * 
	 * @param title - The String content for the title of the to-do item to be created
	 * @param content - The String content for the description of the to-do item to be created
	 * @param tags - The String content for the tags to be set to the to-do item
	 * @param parentActivity - The Activity instance of the activity which will act as the parent activity for the to-do item
	 * @param isPrivate - The Boolean instance of whether the to-do item is to be a public to-do item (whereby this is set to false) or a private to-do item (set to true)
	 * @return - The Todo instance of the created to-do item if all actions are completed successfully, null otherwise
	 */
	public Todo createActivityTodo(String title, String content, String tags, Activity parentActivity, boolean isPrivate){
		
		log.info("INFO: Now creating a new to-do item with title: " + title);
		
		log.info("INFO: Now creating an Entry instance of the new to-do item to be created");
		Todo todoItem = new Todo(title, content, tags, 1, false, false, parentActivity.toEntry(), null, null);
		Entry todoItemEntry = todoItem.toEntry();
		todoItemEntry.setContentAsHtml(content);
		
		if(isPrivate == true) {
			// Set the private category to the Entry to mark this to-do item as private
			todoItemEntry.addCategory(StringConstants.SCHEME_FLAGS, "private", "Private");
		}
		log.info("INFO: The Entry instance of the new to-do item has been created: " + todoItemEntry.toString());
		
		log.info("INFO: Now retrieving the URL to which the POST request to create the to-do item will be sent");
		String postRequestURL = parentActivity.getSelfLink();
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		Entry postRequestResult = (Entry) service.postFeed(postRequestURL, todoItemEntry);
		
		if(postRequestResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The to-do item was successfully created");
			Todo newTodoItem = new Todo(postRequestResult);
			newTodoItem.setLinks(getActivityLinks(postRequestResult));
			return newTodoItem;
		} else {
			log.info("ERROR: The to-do item could NOT be created");
			log.info(postRequestResult.toString());
			return null;
		}
	}
	
	/**
	 * Assigns a user to a to-do item
	 * 
	 * @param toDoItem - The to-do item to which the user is to be assigned
	 * @param userToAssign - The user to be assigned the to-do item
	 * @return - Returns the updated Todo object instance
	 */
	public Todo assignToDoItemToUser(Todo toDoItem, APIProfilesHandler userToAssign) {
		
		log.info("INFO: Assigning the to-do item to " + userToAssign.getDesplayName());
		
		// Set the assigned to attribute of the to-do item to match the user to be assigned
		toDoItem.setAssignedTo(userToAssign.getDesplayName(), userToAssign.getUUID());
		log.info("INFO: The to-do's assigned element has been set: " + toDoItem.getAssignedToElement().toString());
		
		// Retrieve the URL to which the updated to-do item will be sent
		String updateToDoURL = toDoItem.getEditLink();
		log.info("INFO: The URL to update the to-do item has been set: " + updateToDoURL);
		
		Entry updateResponse = (Entry) service.editNodeInActivity(updateToDoURL, toDoItem);
		
		if(updateResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: " + userToAssign.getDesplayName() + " has been successfully assigned to the to-do item");
			return toDoItem;
		} else {
			log.info("ERROR: Failed to assign " + userToAssign.getDesplayName() + " to the to-do item");
			log.info(updateResponse.toString());
			return null;
		}
	}
	
	/**
	 * Assigns anyone to a to-do item (no specific user assigned)
	 * 
	 * @param toDoItem - The to-do item to which anyone will be assigned
	 * @return - Returns the updated Todo object instance
	 */
	public Todo assignToDoItemToAnyone(Todo toDoItem) {
		
		log.info("INFO: Setting the assignment of the to-do item to anyone");
		
		// Clear the assigned to attribute of the to-do item
		if(toDoItem.getAssignedToElement().size() > 0) {
			toDoItem.getAssignedToElement().clear();
		}
		log.info("INFO: The to-do's assigned element has been reset: " + toDoItem.getAssignedToElement().toString());
		
		// Retrieve the URL to which the updated to-do item will be sent
		String updateToDoURL = toDoItem.getEditLink();
		log.info("INFO: The URL to update the to-do item has been set: " + updateToDoURL);
		
		Entry updateResponse = (Entry) service.editNodeInActivity(updateToDoURL, toDoItem);
		
		if(updateResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The to-do item has been successfully assigned to anyone");
			return toDoItem;
		} else {
			log.info("ERROR: Failed to assign anyone to the to-do item");
			log.info(updateResponse.toString());
			return null;
		}
	}
	
	/**
	 * Marks a to-do item as complete/incomplete (with a single user assigned)
	 * 
	 * @param toDoItem - The to-do item to be marked as complete
	 * @param isComplete - True if the Todo item is to be closed (marked as complete),
	 * 						false if it is to be re-opened (marked as incomplete)
	 * @return - Returns the updated Todo object instance
	 */
	public Todo markToDoItemAsCompleteOrIncomplete(Todo toDoItem, boolean toBeCompleted) {
		
		log.info("INFO: Changing the completion status of the to-do item named " + toDoItem.getTitle());
		toDoItem.setIsComplete(toBeCompleted);
		Entry toDoItemEntry = toDoItem.toEntry();
		
		boolean statusChanged = false;
		int index = 0;
		List<Element> listOfToDoElements = toDoItemEntry.getElements();
		while(index < listOfToDoElements.size() && !statusChanged) {
			
			if(listOfToDoElements.get(index).toString().indexOf("snx:assignedto") > -1) {
				log.info("INFO: A single assignee has been detected for this to-do item. Now changing the to-do item status");
				
				log.info("INFO: Changing the status of the to-do item for user with user-name: " + listOfToDoElements.get(index).getAttributeValue("name"));
				listOfToDoElements.get(index).setAttributeValue("iscompleted", "" + toBeCompleted);
				statusChanged = true;
			}
			index ++;
		}
		
		Entry updateResponse = (Entry) service.editNodeInActivity(toDoItem.getEditLink(), toDoItemEntry);
		if(updateResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The completion status of the to-do item was successfully changed");
			return toDoItem;
		} else {
			log.info("ERROR: The completion status could not be changed");
			log.info(updateResponse.toString());
			return null;
		}
	}
	
	/**
	 * This method is now marked as deprecated as of 20th September 2016
	 * Please use the below new method createActivityEntryReply(Activity, ActivityEntry, String, boolean)
	 */
	@Deprecated
	public Reply createActivityReply(String activityReplyContent, ActivityEntry parentEntry, boolean isPrivate) {

		Reply reply = new Reply(parentEntry.getTitle(), activityReplyContent, 1 , isPrivate, parentEntry.toEntry());
	
		String publishURL = service.getServiceURLString() + URLConstants.ACTIVITIES_MY.replace("activities", "activity?activityUuid="+parentEntry.getActivityId().toString());
		
		Entry postResult = (Entry) service.addNodeToActivity(publishURL, reply);

		Reply replyResult = new Reply(postResult);
		
		log.info("INFO: Created activity comment ");
		return replyResult;
		
	}
	
	/**
	 * Posts a reply to the specified activity entry
	 * 
	 * @param parentActivity - The Activity instance of the parent activity which contains the activity entry
	 * @param activityEntry - The ActivityEntry instance of the activity entry to which the reply will be posted
	 * @param replyContent - The String content of the reply to be posted
	 * @param isPrivateReply - True if the reply is to be a private reply, false if it is to be a public reply
	 * @return - The Reply instance of the posted reply if all operations are successful, null otherwise
	 */
	public Reply createActivityEntryReply(Activity parentActivity, ActivityEntry activityEntry, String replyContent, boolean isPrivateReply) {
		
		log.info("INFO: Now creating a reply to the activity entry with title: " + activityEntry.getTitle());
		
		log.info("INFO: Now creating the Entry instance of the reply to be posted to the activity entry");
		Reply reply = new Reply(activityEntry.getTitle(), replyContent, 1, isPrivateReply, activityEntry.toEntry());
		log.info("INFO: The Entry instance of the reply has been created: " + reply.toEntry().toString());
		
		log.info("INFO: Now creating the URL to which to send the POST request");
		String postRequestURL = parentActivity.getSelfLink();
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		Entry postRequestResult = (Entry) service.postFeed(postRequestURL, reply.toEntry());
		
		if(postRequestResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The reply was successfully posted to the activity entry");
			Reply replyResult = new Reply(postRequestResult);
			replyResult.setLinks(getActivityLinks(postRequestResult));
			return replyResult;
		} else {
			log.info("ERROR: The reply could NOT be posted to the activity entry");
			log.info(postRequestResult.toString());
			return null;
		}
	}
	
	/**
	 * This method is now marked as deprecated as of 20th September 2016
	 * Please use the below new method createTodoItemReply(Activity, Todo, String, boolean)
	 */
	@Deprecated
	public Reply createActivityReply(String activityReplyContent, Todo parentTodo, boolean isPrivate) {

		Reply reply = new Reply(parentTodo.getTitle(), activityReplyContent, 1, isPrivate,parentTodo.toEntry());
	
		String publishURL = service.getServiceURLString() + URLConstants.ACTIVITIES_MY.replace("activities", "activity?activityUuid="+parentTodo.getActivityId().toString());
		
		Entry postResult = (Entry) service.addNodeToActivity(publishURL, reply);

		Reply replyResult = new Reply(postResult);
		log.info("INFO: Created activity comment ");
		
		return replyResult;	
	}
	
	/**
	 * Posts a reply to the specified to-do item
	 * 
	 * @param parentActivity - The Activity instance of the parent activity which contains the to-do item
	 * @param todoItem - The Todo instance of the todo item to which the reply will be posted
	 * @param replyContent - The String content of the reply to be posted
	 * @param isPrivateReply - True if the reply is to be a private reply, false if it is to be a public reply
	 * @return - The Reply instance of the posted reply if all operations are successful, null otherwise
	 */
	public Reply createTodoItemReply(Activity parentActivity, Todo todoItem, String replyContent, boolean isPrivateReply) {
		
		log.info("INFO: Now creating a reply to the to-do item with title: " + todoItem.getTitle());
		
		log.info("INFO: Now creating the Entry instance of the reply to be posted to the to-do item");
		Reply reply = new Reply(todoItem.getTitle(), replyContent, 1, isPrivateReply, todoItem.toEntry());
		log.info("INFO: The Entry instance of the reply has been created: " + reply.toEntry().toString());
		
		log.info("INFO: Now creating the URL to which to send the POST request");
		String postRequestURL = parentActivity.getSelfLink();
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		Entry postRequestResult = (Entry) service.postFeed(postRequestURL, reply.toEntry());
		
		if(postRequestResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The reply was successfully posted to the to-do item");
			Reply replyResult = new Reply(postRequestResult);
			replyResult.setLinks(getActivityLinks(postRequestResult));
			return replyResult;
		} else {
			log.info("ERROR: The reply could NOT be posted to the to-do item");
			log.info(postRequestResult.toString());
			return null;
		}
	}
	
	public ActivityEntry addMention_ActivityEntry(Activity parentActivity, Boolean privateEntry, Mentions mentions){
		
		ActivityEntry newActivityEntry = new ActivityEntry("@Mentions Test for Activity Entry_"+Helper.genDateBasedRand(),"content","tag",1,false,null,parentActivity.toEntry(),false);
		
		
		if (privateEntry==true){
	    
		Entry newEntry = newActivityEntry.toEntry();
			
	    newEntry.addCategory(StringConstants.SCHEME_FLAGS, "private", "Private");
	    
		newEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
	    ActivityEntry newActivityEntry2 = new ActivityEntry(newEntry);
	    
	    Entry postResult = (Entry) service.addNodeToActivity(parentActivity.getAppCollection().getHref().toString(), newActivityEntry2);
		
	    ActivityEntry response = new ActivityEntry(postResult);
		
		return response;
		
		}
		else{
			
			Entry newEntry = newActivityEntry.toEntry();
			
			newEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
			ActivityEntry newActivityEntry2 = new ActivityEntry(newEntry);
			
			Entry postResult = (Entry) service.addNodeToActivity(parentActivity.getAppCollection().getHref().toString(), newActivityEntry2);
			ActivityEntry response = new ActivityEntry(postResult);
			
			return response;
			
		}
			
	}
	
	public Reply addMention_EntryReply(ActivityEntry parentEntry, boolean isPrivate, Mentions mentions){
		
		Reply reply = new Reply(parentEntry.getTitle(), "content", 1, isPrivate,parentEntry.toEntry());
		Entry postEntry = reply.toEntry();
		
		postEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		String publishURL = service.getServiceURLString() + URLConstants.ACTIVITIES_MY.replace("activities", "activity?activityUuid=" + parentEntry.getActivityId().toString());
	
		Entry postResult = (Entry) service.postFeed(publishURL, postEntry);

		Reply replyResult = new Reply(postResult);
		
		log.info("INFO: Created activity comment ");
		return replyResult;
			
	}
	
	public Todo addMention_Todo(Activity parent, boolean isPrivate, Mentions mentions){
		
		Todo newToDo = new Todo("Test for Todo Mentions_" +Helper.genDateBasedRand(),"content","tagsString",1,false,false,parent.toEntry(),null,null);
		
		if(isPrivate==true){
			
			Entry privateTodoEntry = newToDo.toEntry();
			privateTodoEntry.addCategory(StringConstants.SCHEME_FLAGS, "private", "Private");
			
			privateTodoEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
			
			
			Entry postResult = (Entry)service.postFeed(parent.getAppCollection().getHref().toString(), privateTodoEntry);
			
		
			Todo resultTodo = new Todo(postResult);
			
			return resultTodo;
					
		}else{
		
		Entry todoEntry = newToDo.toEntry();
		
		todoEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		
		Entry postResult = (Entry) service.postFeed(parent.getAppCollection().getHref().toString(), todoEntry);
	
		Todo response = new Todo(postResult);
		
		return response;
		
		}
		
	}
	
	public Reply addMention_TodoReply(Todo parentTodo, boolean isPrivate, Mentions mentions){
		
		Reply reply = new Reply(parentTodo.getTitle(), "content", 1, isPrivate, parentTodo.toEntry());
		Entry postEntry = reply.toEntry();
		
		postEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		String publishURL = service.getServiceURLString() + URLConstants.ACTIVITIES_MY.replace("activities", "activity?activityUuid=" + parentTodo.getActivityId().toString());
	
		Entry postResult = (Entry) service.postFeed(publishURL, postEntry);

		Reply replyResult = new Reply(postResult);
		
		log.info("INFO: Created activity comment ");
		return replyResult;
	
	}
	
public void addMemberToActivity(Activity activity,User testUser){
	
	Entry activityResult = activity.toEntry();
	
	Member newMember1;

	newMember1 = new Member(testUser.getEmail(), null, Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);

	ExtensibleElement addMemberResult = service.addMemberToActivity(activityResult.getLink(StringConstants.REL_MEMBERS).getHref().toString(), newMember1);

	assertTrue(addMemberResult != null);	
}
	
	public void addMemberToActivity(Entry activityResult) {

		Member newMember1;

		if (!this.setup.getServiceConfig().isEmailHidden()) {

			newMember1 = new Member("*", null, Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		} else {

			newMember1 = new Member(null, "*", Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		} 

		ExtensibleElement addMemberResult = service.addMemberToActivity(activityResult.getLink(StringConstants.REL_MEMBERS).getHref().toString(), newMember1);

		assertTrue(addMemberResult != null);

	}
	/**
	 * This method has been deprecated in favour of editActivityEntryDescription(ActivityEntry, String)
	 */
	@Deprecated
	public void editEntry(ActivityEntry actEntry,String newContent){
		
		actEntry.setContent(newContent);
		String URL = actEntry.getEditLink();
		service.editNodeInActivity(URL, actEntry);
	}
	
	/**
	 * Edits / updates the description of an entry
	 * 
	 * @param activityEntry - The ActivityEntry instance of the activity entry to be updated
	 * @param newDescription - The String content to which the description of the activity entry will be set after updating
	 * @return - The updated ActivityEntry if all operations are successful, otherwise the existing ActivityEntry is returned
	 */
	public ActivityEntry editActivityEntryDescription(ActivityEntry activityEntry, String newDescription) {
		
		log.info("INFO: Now editing the activity entry with title: " + activityEntry.getTitle());
		String oldDescription = activityEntry.getContent().trim();
		
		// Set the content of the activity entry to match the new content
		activityEntry.setContent(newDescription);
		log.info("INFO: The Entry to be PUT to the URL has been created: " + activityEntry.toEntry().toString());
		
		// Retrieve the URL to which the PUT request will be sent
		String putRequestURL = activityEntry.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been created: " + putRequestURL);
		
		Entry editResponse = (Entry) service.editNodeInActivity(putRequestURL, activityEntry);
		
		if(editResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The activity entry description was updated successfully");
		} else {
			log.info("ERROR: The activity entry description could not be updated");
			log.info(editResponse.toString());
			activityEntry.setContent(oldDescription);
		}
		return activityEntry;
	}
	
	/**
	 * This method has been deprecated in favour of editTodoDescription(Todo, String)
	 */
	@Deprecated
	public void editTodo(Todo todo){
		
		todo.setContent("edit");
		String URL = todo.getEditLink();
		service.editNodeInActivity(URL, todo);	
	}
	
	/**
	 * Edits / updates the description of a to-do item
	 * 
	 * @param todo - The Todo instance of the to-do item to be updated
	 * @param newDescription - The String content to which the description of the to-do item will be set after updating
	 * @return - The updated Todo if all operations are successful, otherwise the existing Todo is returned
	 */
	public Todo editTodoDescription(Todo todo, String newDescription) {
		
		log.info("INFO: Now editing the to-do item with title: " + todo.getTitle());
		String oldDescription = todo.getContent().trim();
		
		// Set the content of the to-do item to match the new content
		todo.setContent(newDescription);
		log.info("INFO: The Entry to be PUT to the URL has been created: " + todo.toEntry().toString());
		
		// Retrieve the URL to which the PUT request will be sent
		String putRequestURL = todo.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been created: " + putRequestURL);
		
		Entry editResponse = (Entry) service.editNodeInActivity(putRequestURL, todo);
		
		if(editResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The to-do item description was updated successfully");
		} else {
			log.info("ERROR: The to-do item description could not be updated");
			log.info(editResponse.toString());
			todo.setContent(oldDescription);
		}
		return todo;
	}
	
	/**
	 * Edits / updates a reply posted to an activity entry / to-do item
	 * 
	 * @param reply - The Reply instance of the reply to be updated
	 * @param newReplyContent - The String content of the new content to be set to the reply
	 * @return - The updated Reply if all operations are successful, otherwise the existing Reply is returned
	 */
	public Reply editReply(Reply reply, String newReplyContent) {
		
		log.info("INFO: Now editing the activity entry / to-do item reply to contain the following new content: " + newReplyContent);
		
		// Store the old content in case of operation failure
		String oldReplyContent = reply.getContent();
		
		log.info("INFO: Now creating the Entry instance of the reply with updated content");
		reply.setContent(newReplyContent);
		log.info("INFO: The Entry instance of the reply with updated content has been created: " + reply.toEntry().toString());
		
		log.info("INFO: Now retrieving the URL to which a PUT request will be made to update the reply");
		String putRequestURL = reply.getEditLink();
		log.info("INFO: The URL to be used for updating the reply has been retrieving: " + putRequestURL);
		
		Entry putRequestResult = (Entry) service.editNodeInActivity(putRequestURL, reply);
		
		if(putRequestResult.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The reply was updated successfully");
		} else {
			log.info("ERROR: The reply could NOT be updated");
			log.info(putRequestResult.toString());
			reply.setContent(oldReplyContent);
		}
		return reply;
	}
	
	public void createFollow(Activity activity){
		
		Entry newEntry = Abdera.getNewFactory().newEntry();
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "activities", "activities");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type", "activity", "Activity");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", activity.getId().toString().substring(20), activity.getId().toString().substring(20));
		newEntry.addCategory(StringConstants.SCHEME_FLAGS, "following", "Following");
		
		String URL =service.getServiceURLString() + "/follow/atom/resources";
		
		ExtensibleElement test =service.postFeed(URL, newEntry);
		
			if(test !=null){
				log.info("INFO: Following action completed on activity");
			}else{
				log.info("ERROR: Following action not completed due to error");
			}
		}
	
	public void deleteActivity(Activity activity){
		
		service.deleteActivity(activity.getEditLink());
	}
	
	public boolean deleteEntryComment(Reply reply){
		
		boolean deleted = service.deleteActivity(reply.getEditLink());
		
		return deleted;
	}
	
	/**
	 * Deletes a to-do item 
	 * 
	 * @param todo - The to-do item to be deleted
	 * @return - True if the to-do item is successfully deleted, false otherwise
	 */
	public boolean deleteToDoItem(Todo todo) {
		
		boolean deleted = service.deleteActivity(todo.getEditLink());
		
		if(deleted) {
			log.info("INFO: To-do item was successfully deleted");
		} else {
			log.info("INFO: To-do item could not be deleted");
		}
		return deleted;
	}
}
