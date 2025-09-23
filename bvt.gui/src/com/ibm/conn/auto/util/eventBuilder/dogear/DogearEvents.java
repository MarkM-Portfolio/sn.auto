package com.ibm.conn.auto.util.eventBuilder.dogear;

import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author 	Anthony Cox
 * Date:	10th March 2016
 */

public class DogearEvents {

	private static Logger log = LoggerFactory.getLogger(DogearEvents.class);
	
	/**
	 * Creates a standalone bookmark
	 * 
	 * @param baseBookmarkTemplate - The BaseDogear instance of the bookmark to be created
	 * @param userCreatingBookmark - The User instance of the user creating the bookmark
	 * @param bookmarksAPIUser - The APIDogearHandler instance of the user creating the bookmark
	 * @return - The Bookmark instance of the newly created bookmark
	 */
	public static Bookmark addBookmark(BaseDogear baseBookmarkTemplate, User userCreatingBookmark, APIDogearHandler bookmarksAPIUser) {
		
		log.info("INFO: " + userCreatingBookmark.getDisplayName() + " will now create a new standalone bookmark with title: " + baseBookmarkTemplate.getTitle());
		Bookmark bookmark = bookmarksAPIUser.createBookmark(baseBookmarkTemplate);
		
		log.info("INFO: Verify that the bookmark was created successfully");
		Assert.assertNotNull(bookmark, 
								"ERROR: The new bookmark was NOT created successfully and was returned as null");
		
		log.info("INFO: The bookmark was created successfully");
		return bookmark;
	}
	
	/**
	 * Updates the description of any bookmark
	 * 
	 * @param bookmark - The Bookmark instance of the bookmark to be updated
	 * @param newDescription - The description to be set to the updated bookmark
	 * @param userUpdatingBookmark - The User instance of the user updating the bookmark
	 * @param bookmarksAPIUser - The APIDogearHandler instance of the user updating the bookmark
	 * @return - The Bookmark instance of the updated bookmark
	 */
	public static Bookmark updateBookmarkDescription(Bookmark bookmark, String newDescription, User userUpdatingBookmark, APIDogearHandler bookmarksAPIUser) {
		
		log.info("INFO: " + userUpdatingBookmark.getDisplayName() + " will now update the bookmark description");
		Bookmark updatedBookmark = bookmarksAPIUser.editBookmarkDescription(bookmark, newDescription);
		
		log.info("INFO: Verify that the bookmark description has been successfully set to '" + newDescription + "'");
		Assert.assertTrue(updatedBookmark.getContent().trim().equals(newDescription), 
							"ERROR: The bookmark description was not updated as expected to '" + newDescription + "'");
		return updatedBookmark;
	}
	
	/**
	 * Adds a new bookmark and then updates the bookmark description (in separate steps)
	 * 
	 * @param baseBookmarkTemplate - The BaseDogear instance of the bookmark to be created
	 * @param updatedDescription - The description to be set to the updated bookmark
	 * @param userCreatingBookmark - The User instance of the user creating the bookmark
	 * @param bookmarksAPIUser - The APIDogearHandler instance of the user creating the bookmark
	 * @return - The Bookmark instance of the created and updated bookmark
	 */
	public static Bookmark addAndUpdateBookmark(BaseDogear baseBookmarkTemplate, String updatedDescription, User userCreatingBookmark, APIDogearHandler bookmarksAPIUser) {
		
		// Create the bookmark
		Bookmark newBookmark = addBookmark(baseBookmarkTemplate, userCreatingBookmark, bookmarksAPIUser);
		
		// Update the bookmark description
		return updateBookmarkDescription(newBookmark, updatedDescription, userCreatingBookmark, bookmarksAPIUser);
	}
	
	/**
	 * Notifies the specified user about the specified bookmark
	 * 
	 * @param bookmark - The Bookmark instance of the bookmark which the specified user will be notified about
	 * @param bookmarksAPISendingNotification - The APIDogearHandler instance of the user sending the notification
	 * @param profilesAPISendingNotification - The APIProfilesHandler instance of the user sending the notification
	 * @param profilesAPIReceivingNotification - The APIProfilesHandler instance of the user receiving the notification
	 */
	public static void notifyUserAboutBookmark(Bookmark bookmark, APIDogearHandler bookmarksAPISendingNotification, APIProfilesHandler profilesAPISendingNotification, APIProfilesHandler profilesAPIReceivingNotification) {
		
		log.info("INFO: " + profilesAPISendingNotification.getDesplayName() + " will now notify " + profilesAPIReceivingNotification.getDesplayName() + " about the bookmark with title: " + bookmark.getTitle());
		boolean notificationSent = bookmarksAPISendingNotification.notifyUserAboutBookmark(bookmark, profilesAPISendingNotification, profilesAPIReceivingNotification);
		
		log.info("INFO: Verify that the notification about the bookmark was successfully sent");
		Assert.assertTrue(notificationSent, 
							"ERROR: The notification about the bookmark could NOT be sent as expected");
	}
}
