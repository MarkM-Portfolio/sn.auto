package com.ibm.conn.auto.util.eventBuilder.community;

import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityCalendarEvents {

	private static Logger log = LoggerFactory.getLogger(CommunityCalendarEvents.class);
	
	/**
	 * Creates a new calendar event in a community (adds either a series of events or a single event)
	 * 
	 * @param community - The Community instance of the community in which the calendar event will be created
	 * @param userCreatingCalendarEvent - The User instance of the user creating the calendar event
	 * @param apiUserCreatingCalendarEvent - The APICalendarHandler instance of the user creating the calendar event
	 * @param baseEvent - The BaseEvent instance of the calendar event to be created
	 * @return - The Calendar instance of the created calendar event
	 */
	public static Calendar addCalendarEvent(Community community, BaseEvent baseEvent, User userCreatingCalendarEvent, APICalendarHandler apiUserCreatingCalendarEvent) {
		
		log.info("INFO: " + userCreatingCalendarEvent.getDisplayName() + " will now create a calendar event in the community with title: " + community.getTitle());
		Calendar calendarEvent = apiUserCreatingCalendarEvent.addCalendarEvent(community, baseEvent);
		
		log.info("INFO: Verify that the calendar event was created successfully");
		Assert.assertNotNull(calendarEvent, 
								"ERROR: The calendar event could not be created and was returned as null");
		return calendarEvent;
	}
	
	/**
	 * Creates a new calendar event in a community which includes a mentions to the specified user in the calendar event description
	 * 
	 * @param community - The Community instance of the community in which the calendar event will be created
	 * @param baseEvent - The BaseEvent instance of the calendar event to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the calendar event description
	 * @param userCreatingCalendarEvent - The User instance of the user creating the calendar event
	 * @param apiUserCreatingCalendarEvent - The APICalendarHandler instance of the user creating the calendar event
	 * @return - The Calendar instance of the created calendar event
	 */
	public static Calendar addCalendarEventWithMentions(Community community, BaseEvent baseEvent, Mentions mentions, User userCreatingCalendarEvent, APICalendarHandler apiUserCreatingCalendarEvent) {
		
		log.info("INFO: " + userCreatingCalendarEvent.getDisplayName() + " will now create a community calendar event with mentions to: " + mentions.getUserToMention().getDisplayName());
		Calendar calendarEvent = apiUserCreatingCalendarEvent.addCalendarEventWithMentions(community, baseEvent, mentions);
		
		log.info("INFO: Verify that the calendar event with mentions was created successfully");
		Assert.assertNotNull(calendarEvent, 
								"ERROR: The calendar event with mentions could not be created and was returned as null");
		return calendarEvent;
	}
	
	/**
	 * Edits / updates the description for a community calendar event series (ie. a repeating event)
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to be updated
	 * @param newEventDescription - The String content of the new description to be set to the calendar event
	 * @param userUpdatingEvent - The User instance of the user updating the calendar event
	 * @param apiUserUpdatingEvent - The APICalendarHandler instance of the user updating the calendar event
	 * @return - The Calendar instance of the updated calendar event
	 */
	public static Calendar editCalendarEventSeriesDescription(Calendar calendarEvent, String newEventDescription, User userUpdatingEvent, APICalendarHandler apiUserUpdatingEvent) {
		
		log.info("INFO: " + userUpdatingEvent.getDisplayName() + " will now update the calendar event series description for the event with title: " + calendarEvent.getTitle());
		Calendar updatedCalendarEvent = apiUserUpdatingEvent.editCalendarEventDescription(calendarEvent, newEventDescription);
		
		log.info("INFO: Verify that the calendar event description was updated successfully");
		Assert.assertNotNull(updatedCalendarEvent, 
								"ERROR: The calendar event description could not be updated - a null event was returned");
		return updatedCalendarEvent;
	}
	
	/**
	 * Edits / updates the description for a community calendar events first single instance (ie. a non-repeating event OR first instance of a repeating event)
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to be updated
	 * @param newEventDescription - The String content of the new description to be set to the calendar event
	 * @param userUpdatingEvent - The User instance of the user updating the calendar event
	 * @param apiUserUpdatingEvent - The APICalendarHandler instance of the user updating the calendar event
	 * @return - The Calendar instance of the updated calendar event
	 */
	public static boolean editCalendarEventFirstInstanceDescription(Calendar calendarEvent, String newEventDescription, User userUpdatingEvent, APICalendarHandler apiUserUpdatingEvent) {
		
		log.info("INFO: " + userUpdatingEvent.getDisplayName() + " will now update the calendar event first instance description for the event with title: " + calendarEvent.getTitle());
		boolean updatedCalendarEvent = apiUserUpdatingEvent.editCalendarEventDescriptionFirstSingleInstance(calendarEvent, newEventDescription);
		
		log.info("INFO: Verify that the calendar event description was updated successfully");
		Assert.assertTrue(updatedCalendarEvent, 
							"ERROR: The calendar event description could not be updated - a false result was returned");
		return updatedCalendarEvent;
	}
	
	/**
	 * Deletes a calendar event series
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event series to be deleted
	 * @param userDeletingEvent - The User instance of the user deleting the calendar event series
	 * @param apiUserDeletingEvent - The APICalendarHandler instance of the user deleting the calendar event series
	 * @return - True if all actions are completed successfully
	 */
	public static boolean deleteCalendarEventSeries(Calendar calendarEvent, User userDeletingEvent, APICalendarHandler apiUserDeletingEvent) {
		
		log.info("INFO: " + userDeletingEvent.getDisplayName() + " will now delete the calendar event series with title: " + calendarEvent.getTitle());
		boolean deleted = apiUserDeletingEvent.deleteCalendarEvent(calendarEvent);
		
		log.info("INFO: Verify that the calendar event series was deleted successfully");
		Assert.assertTrue(deleted, 
							"ERROR: The calendar event series could not be deleted - a false result was returned");
		return deleted;
	}
	
	/**
	 * Deletes the first single instance of a calendar event series OR deletes a non-repeating calendar event
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to be deleted
	 * @param userDeletingEvent - The User instance of the user deleting the calendar event
	 * @param apiUserDeletingEvent - The APICalendarHandler instance of the user deleting the calendar event
	 * @return - True if all actions are completed successfully
	 */
	public static boolean deleteCalendarEventFirstInstance(Calendar calendarEvent, User userDeletingEvent, APICalendarHandler apiUserDeletingEvent) {
		
		log.info("INFO: " + userDeletingEvent.getDisplayName() + " will now delete the first instance of the calendar event with title: " + calendarEvent.getTitle());
		boolean deleted = apiUserDeletingEvent.deleteCalendarEventFirstSingleInstance(calendarEvent);
		
		log.info("INFO: Verify that the calendar event first single instance was deleted successfully");
		Assert.assertTrue(deleted, 
							"ERROR: The calendar event first single instance could not be deleted - a false result was returned");
		return deleted;
	}
	
	/**
	 * Posts a comment to a calendar event
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to be commented on
	 * @param commentToBeAdded - The String content of the comment to be posted to the calendar event
	 * @param userAddingComment - The User instance of the user posting the comment
	 * @param apiUserAddingComment - The APICalendarHandler instance of the user posting the comment
	 * @return - The CommentToEvent instance of the comment posted to the calendar event
	 */
	public static CommentToEvent addCommentToCalendarEvent(Calendar calendarEvent, String commentToBeAdded, User userAddingComment, APICalendarHandler apiUserAddingComment) {
		
		log.info("INFO: " + userAddingComment.getDisplayName() + " will now add a comment to the calendar event with content: " + commentToBeAdded);
		CommentToEvent commentOnCalendarEvent = apiUserAddingComment.commentOnCalendarEvent(calendarEvent, commentToBeAdded);
		
		log.info("INFO: Verify that the comment posted successfully to the calendar event");
		Assert.assertNotNull(commentOnCalendarEvent, 
								"ERROR: The comment could not be posted to the calendar event and was returned as null");
		return commentOnCalendarEvent;
	}
	
	/**
	 * Posts a comment with mentions to a calendar event
	 * 
	 * @param calendarEvent - The Calendar instance of the calendar event to be commented on
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userAddingComment - The User instance of the user posting the comment
	 * @param apiUserAddingComment - The APICalendarHandler instance of the user posting the comment
	 * @return - The CommentToEvent instance of the comment posted to the calendar event
	 */
	public static CommentToEvent addCommentWithMentionsToCalendarEvent(Calendar calendarEvent, Mentions mentions, User userAddingComment, APICalendarHandler apiUserAddingComment) {
		
		log.info("INFO: " + userAddingComment.getDisplayName() + " will now add a comment to the calendar event with mentions to: " + mentions.getUserToMention().getDisplayName());
		CommentToEvent commentOnCalendarEvent = apiUserAddingComment.commentWithMentionsOnCalendarEvent(calendarEvent, mentions);
		
		log.info("INFO: Verify that the comment with mentions posted successfully to the calendar event");
		Assert.assertNotNull(commentOnCalendarEvent, 
								"ERROR: The comment with mentions could not be posted to the calendar event and was returned as null");
		return commentOnCalendarEvent;
	}
	
	/**
	 * Deletes a comment posted to a calendar event
	 * 
	 * @param commentToBeDeleted - The CommentToEvent instance of the comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the comment
	 * @param apiUserDeletingComment - The APICalendarHandler instance of the user deleting the comment
	 * @return - True if all actions are completed successfully
	 */
	public static boolean deleteCommentFromCalendarEvent(CommentToEvent commentToBeDeleted, User userDeletingComment, APICalendarHandler apiUserDeletingComment) {
		
		log.info("INFO: " + userDeletingComment.getDisplayName() + " will now delete the calendar event comment with content: " + commentToBeDeleted.getContent().trim());
		boolean deleted = apiUserDeletingComment.deleteCommentOnCalendarEvent(commentToBeDeleted);
		
		log.info("INFO: Verify that the comment was successfully deleted from the calendar event");
		Assert.assertTrue(deleted, 
							"ERROR: The comment could not be deleted from the calendar event - a false result was returned");
		return deleted;
	}
	
	/**
	 * Adds a calendar event to a community and then updates the description for the entire series of calendar event (ie. a repeating event)
	 * 
	 * @param community - The Community instance of the community in which the calendar event will be created and updated
	 * @param baseEvent - The BaseEvent instance of the calendar event to be created and updated
	 * @param newEventDescription - The String content of the new description to be set to the calendar event
	 * @param userCreatingCalendarEvent - The User instance of the user creating and updating the calendar event
	 * @param apiUserCreatingCalendarEvent - The APICalendarHandler instance of the user creating and updating the calendar event
	 * @return - The Calendar instance of the updated calendar event
	 */
	public static Calendar addCalendarEventAndEditSeriesDescription(Community community, BaseEvent baseEvent, String newEventDescription, User userCreatingCalendarEvent, APICalendarHandler apiUserCreatingCalendarEvent) {
		
		// Create the new calendar event in the community
		Calendar calendarEvent = addCalendarEvent(community, baseEvent, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		// Edit the calendar event description
		return editCalendarEventSeriesDescription(calendarEvent, newEventDescription, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
	}
	
	/**
	 * Adds a calendar event to a community and then updates the description for the first single instance of the calendar event (ie. a non-repeating event OR first instance of a repeating event)
	 * 
	 * @param community - The Community instance of the community in which the calendar event will be created and updated
	 * @param baseEvent - The BaseEvent instance of the calendar event to be created and updated
	 * @param newEventDescription - The String content of the new description to be set to the calendar event
	 * @param userCreatingCalendarEvent - The User instance of the user creating and updating the calendar event
	 * @param apiUserCreatingCalendarEvent - The APICalendarHandler instance of the user creating and updating the calendar event
	 * @return - The Calendar instance of the updated calendar event
	 */
	public static Calendar addCalendarEventAndEditFirstInstanceDescription(Community community, BaseEvent baseEvent, String newEventDescription, User userCreatingCalendarEvent, APICalendarHandler apiUserCreatingCalendarEvent) {
		
		// Create the new calendar event in the community
		Calendar calendarEvent = addCalendarEvent(community, baseEvent, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		// Edit the calendar event description
		editCalendarEventFirstInstanceDescription(calendarEvent, newEventDescription, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		return calendarEvent;
	}
	
	/**
	 * Adds a calendar event to a community and then posts a comment to that calendar event
	 * 
	 * @param community - The Community instance of the community in which the calendar event will be created and commented on
	 * @param baseEvent - The BaseEvent instance of the calendar event to be created and commented on
	 * @param commentToBeAdded - The String content of the comment to be posted to the calendar event
	 * @param userCreatingCalendarEvent - The User instance of the user creating and commenting on the calendar event
	 * @param apiUserCreatingCalendarEvent - The APICalendarHandler instance of the user creating and commenting on the calendar event
	 * @return - The Calendar instance of the calendar event
	 */
	public static Calendar addCalendarEventAndAddComment(Community community, BaseEvent baseEvent, String commentToBeAdded, User userCreatingCalendarEvent, APICalendarHandler apiUserCreatingCalendarEvent) {
		
		// Create the new calendar event in the community
		Calendar calendarEvent = addCalendarEvent(community, baseEvent, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		// Post the comment to the calendar event
		addCommentToCalendarEvent(calendarEvent, commentToBeAdded, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		return calendarEvent;
	}
	
	/**
	 * Adds a calendar event to a community and then posts a comment with mentions to that calendar event
	 * 
	 * @param community - The Community instance of the community in which the calendar event will be created and commented on
	 * @param baseEvent - The BaseEvent instance of the calendar event to be created and commented on
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userCreatingCalendarEvent - The User instance of the user creating and commenting on the calendar event
	 * @param apiUserCreatingCalendarEvent - The APICalendarHandler instance of the user creating and commenting on the calendar event
	 * @return - The Calendar instance of the calendar event
	 */
	public static Calendar addCalendarEventAndAddCommentWithMentions(Community community, BaseEvent baseEvent, Mentions mentions, User userCreatingCalendarEvent, APICalendarHandler apiUserCreatingCalendarEvent) {
	
		// Create the new calendar event in the community
		Calendar calendarEvent = addCalendarEvent(community, baseEvent, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		// Post the comment with mentions to the calendar event
		addCommentWithMentionsToCalendarEvent(calendarEvent, mentions, userCreatingCalendarEvent, apiUserCreatingCalendarEvent);
		
		return calendarEvent;
	}
}