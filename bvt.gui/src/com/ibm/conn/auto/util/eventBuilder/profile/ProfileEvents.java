package com.ibm.conn.auto.util.eventBuilder.profile;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

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
 * Date:	14th March 2016
 */

public class ProfileEvents {

	private static Logger log = LoggerFactory.getLogger(ProfileEvents.class);
	
	/**
	 * Allows one user to follow another user
	 * 
	 * @param apiUserToBeFollowed - The APIProfilesHandler instance of the user to be followed by another user
	 * @param apiUserFollowing - The APIProfilesHandler instance of the user to follow another user
	 */
	public static void followUser(APIProfilesHandler apiUserToBeFollowed, APIProfilesHandler apiUserFollowing) {
		
		log.info("INFO: " + apiUserFollowing.getDesplayName() + " will now follow the user with name: " + apiUserToBeFollowed.getDesplayName());
		apiUserFollowing.followUser(apiUserToBeFollowed.getUUID());
	}
	
	/**
	 * Allows one user to unfollow another user
	 * 
	 * @param apiUserToBeUnfollowed - The APIProfilesHandler instance of the user to be unfollowed by another user
	 * @param apiUserUnfollowing - The APIProfilesHandler instance of the user to unfollow another user
	 */
	public static void unfollowUser(APIProfilesHandler apiUserToBeUnfollowed, APIProfilesHandler apiUserUnfollowing) {
		
		log.info("INFO: " + apiUserUnfollowing.getDesplayName() + " will now unfollow the user with name: " + apiUserToBeUnfollowed.getDesplayName());
		apiUserUnfollowing.unfollowUser(apiUserUnfollowing, apiUserToBeUnfollowed);
	}
	
	/**
	 * Sends an invitation to a user to join another users network
	 * 
	 * @param apiUserSendingInvite - The APIProfilesHandler instance of the user sending the invitation to join their network
	 * @param apiUserReceivingInvite - The APIProfilesHandler instance of the user who will receive the invite to join the network
	 * @return - The Invitation instance of the sent invite
	 */
	public static Invitation inviteUserToJoinNetwork(APIProfilesHandler apiUserSendingInvite, APIProfilesHandler apiUserReceivingInvite) {
		
		log.info("INFO: " + apiUserSendingInvite.getDesplayName() + " will now invite '" + apiUserReceivingInvite + "' to join their network");
		Invitation sentInvite = apiUserSendingInvite.inviteUserToJoinNetwork(apiUserReceivingInvite);
		
		log.info("INFO: Verify that the invitation was sent successfully");
		Assert.assertNotNull(sentInvite, "ERROR: The invitation was NOT sent successfully and was returned as null");
		
		return sentInvite;
	}
	
	/**
	 * Likes a status update with the specified ID
	 * 
	 * @param apiUserLikingStatusUpdate - The APIProfilesHandler instance of the user liking the status update
	 * @param statusUpdateId - The ID of the status update to be liked
	 */
	public static void likeStatusUpdate(APIProfilesHandler apiUserLikingStatusUpdate, String statusUpdateId) {
		
		log.info("INFO: " + apiUserLikingStatusUpdate.getDesplayName() + " will now like the status update");
		apiUserLikingStatusUpdate.like(statusUpdateId);
	}
	
	/**
	 * Likes a board message with the specified ID
	 * 
	 * @param apiUserLikingBoardMessage - The APIProfilesHandler instance of the user liking the board message
	 * @param boardMessageId - The ID of the board message to be liked
	 */
	public static void likeBoardMessage(APIProfilesHandler apiUserLikingBoardMessage, String boardMessageId) {
		
		log.info("INFO: " + apiUserLikingBoardMessage.getDesplayName() + " will now like the board message");
		apiUserLikingBoardMessage.like(boardMessageId);
	}
	
	/**
	 * Unlikes a status update with the specified ID
	 * 
	 * @param apiUserUnlikingStatusUpdate - The APIProfilesHandler instance of the user unliking the status update
	 * @param statusUpdateId - The ID of the status update to be unliked
	 */
	public static void unlikeStatusUpdate(APIProfilesHandler apiUserUnlikingStatusUpdate, String statusUpdateId) {
		
		log.info("INFO: " + apiUserUnlikingStatusUpdate.getDesplayName() + " will now unlike the status update");
		boolean unlikedStatusUpdate = apiUserUnlikingStatusUpdate.unlike(statusUpdateId);
		
		log.info("INFO: Verify that the status update was unliked successfully");
		Assert.assertTrue(unlikedStatusUpdate, 
							"ERROR: The status update was NOT unliked - the API returned a negative response");
	}
	
	/**
	 * Likes a comment with the specified ID
	 * 
	 * @param apiUserLikingComment - The APIProfilesHandler instance of the user liking the comment
	 * @param commentId - The ID of the comment to be liked
	 */
	public static void likeComment(APIProfilesHandler apiUserLikingComment, String commentId) {
		
		log.info("INFO: " + apiUserLikingComment.getDesplayName() + " will now like the comment");
		apiUserLikingComment.like(commentId);
	}
	
	/**
	 * Unlikes a comment with the specified ID
	 * 
	 * @param apiUserUnlikingComment - The APIProfilesHandler instance of the user unliking the status update
	 * @param commentId - The ID of the comment to be unliked
	 */
	public static void unlikeComment(APIProfilesHandler apiUserUnlikingComment, String commentId) {
		
		log.info("INFO: " + apiUserUnlikingComment.getDesplayName() + " will now unlike the comment");
		boolean unlikedComment = apiUserUnlikingComment.unlike(commentId);
		
		log.info("INFO: Verify that the comment was unliked successfully");
		Assert.assertTrue(unlikedComment, 
							"ERROR: The comment was NOT unliked - the API returned a negative response");
	}
	
	/**
	 * Allows multiple users to like a comment with the specified ID
	 * 
	 * @param apiUsersLikingComment - An array of APIProfilesHandler instances of all users to like / recommend the comment
	 * @param commentId - The ID of the comment to be liked
	 */
	public static void likeCommentMultipleUsers(APIProfilesHandler[] apiUsersLikingComment, String commentId) {
		
		for(APIProfilesHandler apiUserLikingComment : apiUsersLikingComment) {
			
			// Have this user like / recommend the comment
			likeComment(apiUserLikingComment, commentId);
		}
	}
	
	/**
	 * Posts a status update to a users own profile
	 * 
	 * @param apiUserPostingStatus - The APIProfilesHandler instance of the user posting the status update
	 * @param statusUpdateBeingPosted - The String content of the status update to be posted
	 * @return - The ID of the status update
	 */
	public static String addStatusUpdate(APIProfilesHandler apiUserPostingStatus, String statusUpdateBeingPosted) {
		
		log.info("INFO: " + apiUserPostingStatus.getDesplayName() + " will now post a status update with content: " + statusUpdateBeingPosted);
		return apiUserPostingStatus.postStatusUpdate(statusUpdateBeingPosted);
	}
	
	/**
	 * Posts a status update (does NOT use delay) to a users own profile using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the addStatusUpdateUsingUI() method
	 * @param userPostingStatus - The User instance of the user posting the status update
	 * @param statusUpdateBeingPosted - The String content of the status update to be posted
	 * @param verifyStatusIsDisplayed = True if the status update is to be verified as displayed in the AS after posting, false if it is NOT to be verified as displayed
	 */
	public static void addStatusUpdateUsingUI(HomepageUI ui, User userPostingStatus, String statusUpdateBeingPosted, boolean verifyStatusIsDisplayed) {
		
		log.info("INFO: " + userPostingStatus.getDisplayName() + " will now post a status update with content: " + statusUpdateBeingPosted);
		ui.postHomepageUpdateWithoutDelay(statusUpdateBeingPosted);
		
		if(verifyStatusIsDisplayed) {
			log.info("INFO: Verify that the status message now appears in the Activity Stream");
			Assert.assertTrue(ui.fluentWaitTextPresent(statusUpdateBeingPosted.trim()), 
								"ERROR: The status message was NOT displayed in the Activity Stream after posting the status message using the UI");
		}
	}
	
	/**
	 * Enters a status update (does NOT use delay) and cancels the status update entry
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userEnteringStatus - The User instance of the user entering and cancelling the status update
	 * @param statusUpdateBeingEntered - The String content of the status update to be entered and cancelled
	 * @return - True if all operations are successful
	 */
	public static boolean typeStatusUpdateAndCancelStatusUpdateUsingUI(HomepageUI ui, RCLocationExecutor driver, User userEnteringStatus, String statusUpdateBeingEntered) {
		
		log.info("INFO: " + userEnteringStatus.getDisplayName() + " will now enter and then cancel a status update with content: " + statusUpdateBeingEntered);
		
		// Type the status update into the status update input field
		ui.typeHomepageUpdateWithoutDelay(statusUpdateBeingEntered);
		
		// Clear the contents of the status update input field
		UIEvents.clearStatusUpdate(ui);
		
		log.info("INFO: Verify that the cancelled status update is NOT displayed in the AS");
		Assert.assertFalse(driver.isTextPresent(statusUpdateBeingEntered), 
							"ERROR: The cancelled status update was displayed in the AS");
		
		// Switch focus back to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
		
		log.info("INFO: Verify that the cancelled status update is NOT displayed in the status update input field");
		Assert.assertFalse(driver.isTextPresent(statusUpdateBeingEntered), 
							"ERROR: The cancelled status update was displayed in the status update input field");
		return true;
	}
	
	/**
	 * Attempts to enter a status update which is too long into the status update input field and verifies all error messages appear and appropriate links are disabled
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingStatus - The User instance of the user posting the status update
	 * @param tooLongStatusMessage - The String content of the status update to be posted 
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addStatusUpdateTooLongUsingUI(HomepageUI ui, RCLocationExecutor driver, User userPostingStatus, String tooLongStatusMessage) {
		
		// Switch focus to the status updates frame
		UIEvents.switchToStatusUpdateFrame(ui);
		
		log.info("INFO: " + userPostingStatus.getDisplayName() + " will now enter a status update which is too long (ie. is greater than 1000 characters in length)");
		UIEvents.typeStringWithNoDelay(ui, tooLongStatusMessage);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		log.info("INFO: Verify the status update is too long warning message is displayed in the UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().TooLongStatusMessage),
							"ERROR: Status update is too long warning message was NOT displayed in the UI");
		
		// Verify that the Post link has been disabled
		verifyPostLinkIsDisabled(driver);
		
		return true;
	}
	
	/**
	 * Enters and posts a comment using the EE comment input field
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param statusMessageOrNewsStory - The String content of the status message or news story whose EE is to be opened and commented on
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param commentToBePosted - The String content of the comment to be posted 
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addEECommentUsingUI(HomepageUI ui, RCLocationExecutor driver, String statusMessageOrNewsStory, User userPostingComment, String commentToBePosted) {
		
		// Open the EE for the status message / news story to be commented on
		UIEvents.openEE(ui, statusMessageOrNewsStory);
		
		// Switch focus to the comments frame in the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, true);
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now enter a comment in the EE with content: " + commentToBePosted);
		UIEvents.typeStringWithNoDelay(ui, commentToBePosted);
		
		// Switch focus back to the main EE frame
		UIEvents.switchToEEFrame(ui);
		
		// Post the comment by clicking on the 'Post' link in the EE
		UIEvents.postEECommentOrReply(ui);
		
		log.info("INFO: Verify that the comment is displayed in the EE after posting");
		Assert.assertTrue(ui.fluentWaitTextPresent(commentToBePosted), 
							"ERROR: The comment was NOT displayed in the EE after posting with content: " + commentToBePosted);
		return true;
	}
	
	/**
	 * Attempts to enter a comment which is too long into the EE comment input field and verifies that the appropriate links are disabled
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param statusMessageOrNewsStory - The String content of the status message or news story whose EE is to be opened and commented on
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param tooLongComment - The String content of the comment to be posted 
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addEECommentTooLongUsingUI(HomepageUI ui, RCLocationExecutor driver, String statusMessageOrNewsStory, User userPostingComment, String tooLongComment) {
		
		// Open the EE for the status message / news story to be commented on
		UIEvents.openEE(ui, statusMessageOrNewsStory);
		
		// Switch focus to the comments frame in the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, true);
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now enter a comment in the EE which is too long (ie. is greater than 1000 characters in length)");
		UIEvents.typeStringWithNoDelay(ui, tooLongComment);
		
		// Switch focus back to the main EE frame
		UIEvents.switchToEEFrame(ui);
		
		// Verify that the Post link has been disabled
		verifyPostLinkIsDisabled(driver);
		
		return true;
	}
	
	/**
	 * Posts a status update with a mentions to a specified user
	 * 
	 * @param apiUserPostingStatus - The APIProfilesHandler instance of the user posting the status update with mentions
	 * @param mentions - The Mentions instance of the user to be mentioned in the status update
	 * @return - The ID of the status update
	 */
	public static String addStatusUpdateWithMentions(APIProfilesHandler apiUserPostingStatus, Mentions mentions) {
		
		log.info("INFO: " + apiUserPostingStatus.getDesplayName() + " will now post a mentions status update with a mentions to: " + mentions.getUserToMention().getDisplayName());
		return apiUserPostingStatus.addMentionsStatusUpdate(mentions);
	}
	
	/**
	 * Posts a status update with mentions from a users status update input box
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingStatus - The User instance of the user posting the status update with mentions
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param warningMessageIsDisplayed - True if the warning message for mentioned different org users is to be verified as displayed, false otherwise
	 * @return - True if all actions are completed successfully 
	 */
	public static boolean addStatusUpdateWithMentionsUsingUI(HomepageUI ui, RCLocationExecutor driver, User userPostingStatus, Mentions mentions, boolean warningMessageIsDisplayed) {
		
		// Switch focus to the status update frame
		UIEvents.switchToStatusUpdateFrame(ui);
		
		log.info("INFO: " + userPostingStatus.getDisplayName() + " will now post a status update with mentions to user: " + mentions.getUserToMention().getDisplayName());
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(ui, driver, mentions);
		
		// Verify that the warning message is displayed / not displayed as expected in the UI
		verifyDifferentOrgWarningMessageIsDisplayed(ui, driver, warningMessageIsDisplayed);
		
		// Switch focus back to the status update frame again
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// Verify whether a valid / invalid mentions link is displayed as expected in the UI
		UIEvents.verifyValidOrInvalidMentionsLink(ui, mentions.getUserToMention().getDisplayName(), warningMessageIsDisplayed);
		
		// Enter the remainder of the mentions text into the status update input field
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		// Post the status update with mentions
		postStatusUpdateUsingUI(ui);
		
		// Verify that all of the mentions text components are displayed in the UI after posting
		UIEvents.verifyMentionsTextIsDisplayedInUI(ui, mentions, !warningMessageIsDisplayed);
				
		return true;
	}
	
	/**
	 * Posts a status update with mentions from a users status update input box for MT environment
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingStatus - The User instance of the user posting the status update with mentions
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - True if all actions are completed successfully 
	 */
     public static boolean addStatusUpdateWithMentionsUsingUIMT(HomepageUI ui, RCLocationExecutor driver, User userPostingStatus,User usermentionedInStatus, Mentions mentions) {
		
		// Switch focus to the status update frame
		UIEvents.switchToStatusUpdateFrame(ui);
		
		log.info("INFO: " + userPostingStatus.getDisplayName() + " will now post a status update with mentions to user: " + mentions.getUserToMention().getDisplayName());
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUserMT(ui, driver, mentions,usermentionedInStatus);
		
		// Switch focus back to the status update frame again
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// Enter the remainder of the mentions text into the status update input field
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		// Post the status update with mentions
		postStatusUpdateUsingUI(ui);
				
		return true;
	}
	
	/**
	 * Posts a pre-entered status update to a users own profile using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void postStatusUpdateUsingUI(HomepageUI ui) {
		
		log.info("INFO: Now posting the status message entered into the status update input field");
		ui.postStatusUpdate(false);
	}
	
	/**
	 * Posts a pre-entered comment to a status update using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusUpdate - The String content of the status update on which the comment was entered
	 * @param comment - The String content of the comment to be verified as displayed (if the verification parameter is set to true)
	 * @param verifyCommentIsDisplayed - True if the comment is to be verified as displayed, false otherwise
	 */
	public static void postStatusUpdateCommentUsingUI(HomepageUI ui, String statusUpdate, String comment, boolean verifyCommentIsDisplayed) {
		
		log.info("INFO: Now posting the comment on the status update message");
		ui.postStatusUpdateComment(statusUpdate, comment, verifyCommentIsDisplayed);
	}
	
	/**
	 * Posts a pre-entered global sharebox status message to a users own profile using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void postGlobalShareboxUpdateUsingUI(HomepageUI ui) {
		
		log.info("INFO: Now posting the status message entered into the global sharebox");
		ui.postStatusUpdate(true);
	}
	
	/**
	 * Navigate to the profile page of the specified user in the UI
	 * 
	 * @param uiProfiles - The ProfilesUI instance to invoke all relevant methods
	 * @param apiUserToNavigateTo - The APIProfilesHandler instance of the user whose profile is to be navigated to in the UI
	 */
	public static void navigateToUserProfile(ProfilesUI uiProfiles, APIProfilesHandler apiUserToNavigateTo) {
		
		// Navigate to the users profile screen
		uiProfiles.navigateToUserProfile(apiUserToNavigateTo);
	}
	
	/**
	 * Log in to Connections as the specified user and then navigate to the required users profile page in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiProfiles - The ProfilesUI instance to invoke all relevant methods
	 * @param userToBeLoggedIn - The User instance of the user to be logged in
	 * @param apiUserToNavigateTo - The APIProfilesHandler instance of the user whose profile is to be navigated to in the UI
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToUserProfile(HomepageUI ui, ProfilesUI uiProfiles, User userToBeLoggedIn, APIProfilesHandler apiUserToNavigateTo, boolean preserveInstance) {
		
		// Log in to Connections
		LoginEvents.loginToHomepage(ui, userToBeLoggedIn, preserveInstance);
		
		// Navigate to the users profile screen
		navigateToUserProfile(uiProfiles, apiUserToNavigateTo);
	}
	
	/**
	 * Posts a board message to the specified users profile
	 * 
	 * @param boardMessageContent - The String content of the board message to be posted
	 * @param apiUserPostingBoardMessage - The APIProfilesHandler instance of the user posting the board message
	 * @param apiUserReceivingBoardMessage - The APIProfilesHandler instance of the user receiving the board message
	 * @return - The String content of the board message ID
	 */
	public static String addBoardMessage(String boardMessageContent, APIProfilesHandler apiUserPostingBoardMessage, APIProfilesHandler apiUserReceivingBoardMessage) {
		
		log.info("INFO: " + apiUserPostingBoardMessage.getDesplayName() + " will now post a board message to " + apiUserReceivingBoardMessage + "'s profile with content: " + boardMessageContent);
		return apiUserPostingBoardMessage.post_Message_User(apiUserReceivingBoardMessage.getUUID(), boardMessageContent);
	}
	
	/**
	 * Posts a board message with URL to a users profile (expects that you have already navigated to the users profile in the UI)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param boardMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the board message content
	 * @param urlPreviewGeneratesThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean postBoardMessageWithURLUsingUI(HomepageUI ui, String boardMessageBeforeURL, String url, boolean urlPreviewGeneratesThumbnailImage) {
		
		// Post the board message with URL to the users profile
		UIEvents.postStatusWithURL(ui, boardMessageBeforeURL, url, urlPreviewGeneratesThumbnailImage);
		
		// Create the CSS selectors to be verified before proceeding
		String uniqueStatusMessage = boardMessageBeforeURL + " " + url;
		String urlPreviewWidget = ui.replaceNewsStory(CommunitiesUIConstants.URLPreview_AfterPostingSU, uniqueStatusMessage, url, null);
		String thumbnailImage = ui.replaceNewsStory(CommunitiesUIConstants.URLPreview_AfterPostingSU_ThumbnailImage, uniqueStatusMessage, url, null);
		
		log.info("INFO: Verify that the URL preview widget is displayed in the Profiles UI after posting the board message with URL");
		Assert.assertTrue(ui.isElementPresent(urlPreviewWidget), 
							"ERROR: The URL preview widget was NOT displayed in Profiles UI after posting the board message with URL");
		
		if(urlPreviewGeneratesThumbnailImage) {
			log.info("INFO: Verify that the thumbnail image is displayed with the URL preview widget");
			Assert.assertTrue(ui.isElementVisible(thumbnailImage), 
								"ERROR: The thumbnail image was NOT displayed with the URL preview widget");
		} else {
			log.info("INFO: Verify that the thumbnail image is NOT displayed with the URL preview widget");
			Assert.assertFalse(ui.isElementVisible(thumbnailImage), 
								"ERROR: The thumbnail image was displayed with the URL preview widget");
		}
		return true;
	}
	
	/**
	 * Posts a comment to a status update
	 * 
	 * @param statusUpdateId - The String content of the ID for the status update to which the comment will be posted
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @param apiUserPostingComment - The APIProfilesHandler instance of the user posting the comment
	 * @return - The String ID of the comment
	 */
	public static String addStatusUpdateComment(String statusUpdateId, String commentToBePosted, APIProfilesHandler apiUserPostingComment) {
		
		log.info("INFO: " + apiUserPostingComment.getDesplayName() + " will now post a comment with content: " + commentToBePosted);
		return apiUserPostingComment.postComment(statusUpdateId, commentToBePosted);
	}
	
	/**
	 * Posts a comment to a board message
	 * 
	 * @param boardMessageId - The String content of the ID for the board message to which the comment will be posted
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @param apiUserPostingComment - The APIProfilesHandler instance of the user posting the comment
	 * @return - The String ID of the comment
	 */
	public static String addBoardMessageComment(String boardMessageId, String commentToBePosted, APIProfilesHandler apiUserPostingComment) {
		
		log.info("INFO: " + apiUserPostingComment.getDesplayName() + " will now post a comment with content: " + commentToBePosted);
		return apiUserPostingComment.postComment(boardMessageId, commentToBePosted);
	}
	
	/**
	 * Posts a comment to the specified status update in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param statusUpdate - The String content of the status update to be commented on
	 * @param commentToBePosted - The String content of the comment to be posted to the status update
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addStatusUpdateCommentUsingUI(HomepageUI ui, User userPostingComment, String statusUpdate, String commentToBePosted) {
		
		// Type the comment into the comment input field
		typeStatusUpdateCommentAndVerifyAllComponentsUsingUI(ui, userPostingComment, statusUpdate, commentToBePosted);
		
		// Post the comment to the status update
		postStatusUpdateCommentUsingUI(ui, statusUpdate, commentToBePosted, true);
		
		return true;
	}
	
	/**
	 * Enters a comment to the specified status update in the UI and verifies all components
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userEnteringComment - The User instance of the user entering the comment
	 * @param statusUpdate - The String content of the status update to be commented on
	 * @param commentToBeEntered - The String content of the comment to be entered
	 */
	private static void typeStatusUpdateCommentAndVerifyAllComponentsUsingUI(HomepageUI ui, User userEnteringComment, String statusUpdate, String commentToBeEntered) {
		
		// Click on the 'Comment' link for this status update and switch focus to the comment frame
		clickNewsStoryASCommentLinkAndSwitchToASCommentFrame(ui, statusUpdate);
		
		// Enter the comment into the comment input field
		UIEvents.typeStringWithNoDelay(ui, commentToBeEntered);
		
		log.info("INFO: Verify that the comment content is displayed in the comment input field after comment entry");
		Assert.assertTrue(ui.fluentWaitTextPresent(commentToBeEntered), 
							"ERROR: The comment content was NOT displayed in the comment input field after comment entry");
		
		// Switch focus back to the top frame again
		UIEvents.switchToTopFrame(ui);
		
		// Verify that both the 'Post' and 'Cancel' buttons are displayed before posting the comment
		String postCommentCSSSelector = HomepageUIConstants.PostComment_Unique.replace("PLACEHOLDER", statusUpdate);
		String cancelCommentCSSSelector = HomepageUIConstants.CancelComment_Unique.replace("PLACEHOLDER", statusUpdate);
		
		log.info("INFO: Verify that the 'Post' link for posting the comment is displayed");
		Assert.assertTrue(ui.isElementVisible(postCommentCSSSelector), 
							"ERROR: The 'Post' link for posting the comment was NOT displayed");
		
		log.info("INFO: Verify that the 'Cancel' link for cancelling the comment entry is displayed");
		Assert.assertTrue(ui.isElementVisible(cancelCommentCSSSelector), 
							"ERROR: The 'Cancel' link for cancelling the comment entry was NOT displayed");
	}
	
	/**
	 * Enters a comment to the specified status update in the UI and then cancels / clears the comment
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userEnteringComment - The User instance of the user entering the comment
	 * @param statusUpdate - The String content of the status update to be commented on
	 * @param commentToBeEntered - The String content of the comment to be entered and cleared
	 * @return - True if all actions are completed successfully
	 */
	public static boolean typeStatusUpdateCommentAndCancelCommentUsingUI(HomepageUI ui, RCLocationExecutor driver, User userEnteringComment, String statusUpdate, String commentToBeEntered) {
		
		// Type the comment into the comment input field
		typeStatusUpdateCommentAndVerifyAllComponentsUsingUI(ui, userEnteringComment, statusUpdate, commentToBeEntered);
		
		// Create the CSS selectors for the 'Post' and 'Cancel' links for the comment
		String postCommentCSSSelector = HomepageUIConstants.PostComment_Unique.replace("PLACEHOLDER", statusUpdate);
		String cancelCommentCSSSelector = HomepageUIConstants.CancelComment_Unique.replace("PLACEHOLDER", statusUpdate);
		
		// Click on the 'Cancel' link to cancel the comment entry
		ui.clickLinkWait(cancelCommentCSSSelector);
		
		// Verify that both the 'Post' and 'Cancel' buttons are NOT displayed after cancelling the comment
		log.info("INFO: Verify that the 'Post' link for posting the comment is NOT displayed after cancelling the comment");
		Assert.assertFalse(ui.isElementVisible(postCommentCSSSelector), 
							"ERROR: The 'Post' link for posting the comment was displayed after cancelling the comment");
		
		log.info("INFO: Verify that the 'Cancel' link for cancelling the comment entry is NOT displayed after cancelling the comment");
		Assert.assertFalse(ui.isElementVisible(cancelCommentCSSSelector), 
							"ERROR: The 'Cancel' link for cancelling the comment entry was displayed after cancelling the comment");
		
		log.info("INFO: Verify that the cancelled comment is NOT displayed in the AS");
		Assert.assertFalse(driver.isTextPresent(commentToBeEntered),
							"ERROR: The cancelled comment was displayed in the AS");
		return true;
	}
	
	/**
	 * Posts a comment with mentions to the specified status update in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param statusUpdate - The String content of the status update to be commented on
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param warningMessageIsDisplayed - True if the warning message for mentioned different org users is to be verified as displayed, false if it is to be verified as absent
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addStatusUpdateCommentWithMentionsUsingUI(HomepageUI ui, RCLocationExecutor driver, User userPostingComment, String statusUpdate,
																		Mentions mentions, boolean warningMessageIsDisplayed) {
		// Click on the 'Comment' link for this status update and switch focus to the comment frame
		clickNewsStoryASCommentLinkAndSwitchToASCommentFrame(ui, statusUpdate);
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now post a comment with mentions to user: " + mentions.getUserToMention().getDisplayName());
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(ui, driver, mentions);
		
		// Verify that the warning message is displayed / not displayed as expected in the UI
		verifyDifferentOrgWarningMessageIsDisplayed(ui, driver, warningMessageIsDisplayed);
		
		log.info("INFO: Now switching focus back to the comment input field again");
		UIEvents.switchToStatusUpdateCommentFrame(ui, statusUpdate);
		
		// Verify whether a valid / invalid mentions link is displayed as expected in the UI
		UIEvents.verifyValidOrInvalidMentionsLink(ui, mentions.getUserToMention().getDisplayName(), warningMessageIsDisplayed);
		
		// Enter the remainder of the mentions text into the comment input field
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		// Switch focus back to the top frame again
		UIEvents.switchToTopFrame(ui);
		
		// Post the comment with mentions
		postStatusUpdateCommentUsingUI(ui, statusUpdate, null, false);
		
		// Verify that all of the mentions text components are displayed in the UI after posting
		UIEvents.verifyMentionsTextIsDisplayedInUI(ui, mentions, !warningMessageIsDisplayed);
		
		return true;
	}
	
	/**
	 * Attempts to enter a comment on a status update which is too long into the AS comment input field and verifies all error messages appear
	 * and appropriate links are disabled
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param statusUpdate - The String content of the status update / news story to which to attempt to post the comment
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param tooLongComment - The String content of the invalid comment of greater than 1000 characters in length
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addStatusUpdateCommentTooLongUsingUI(HomepageUI ui, RCLocationExecutor driver, String statusUpdate, User userPostingComment, String tooLongComment) {
		
		// Click on the 'Comment' link for this status update and switch focus to the comment frame
		clickNewsStoryASCommentLinkAndSwitchToASCommentFrame(ui, statusUpdate);
		
		// Enter the comment with too many characters into the comment input field
		UIEvents.typeStringWithNoDelay(ui, tooLongComment);
		
		// Switch focus back to the top frame again
		UIEvents.switchToTopFrame(ui);
		
		log.info("INFO: Verify the comment is too long warning message is displayed in the UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().TooLongCommentMessage),
							"ERROR: The comment is too long warning message was NOT displayed in the UI");
		
		// Verify that the Post link has been disabled
		verifyPostLinkIsDisabled(driver);
		
		return true;
	}
	
	/**
	 * Verifies that the warning message for a mentioned user who is from a different org / is a visitor is displayed / absent in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param verifyIsDisplayed - True if the warning message is to be verified as displayed, false if it is to be verified as absent
	 */
	public static void verifyDifferentOrgWarningMessageIsDisplayed(HomepageUI ui, RCLocationExecutor driver, boolean verifyIsDisplayed) {
		
		String warningMessage = Data.getData().mentionErrorMsgVisitorModel;
		
		if(verifyIsDisplayed) {
			log.info("INFO: Verify that the different org / visitor model warning message is displayed: " + warningMessage);
			Assert.assertTrue(ui.fluentWaitTextPresent(warningMessage), 
								"ERROR: The warning message was NOT displayed with content: " + warningMessage);
		} else {
			log.info("INFO: Verify that the different org / visitor model warning message is NOT displayed: " + warningMessage);
			Assert.assertTrue(driver.isTextNotPresent(warningMessage), 
								"ERROR: The warning message was displayed with content: " + warningMessage);
		}
	}
	
	/**
	 * Clicks on the global search bar component in the UI and then searches through all visible global search bar options for the specified option
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param optionToSearchFor - The String content of the option to be searched for in the global search bar options list
	 * @return - True if the option is displayed, false if it is absent 
	 */
	public static boolean openGlobalSearchBarAndSearchForOption(HomepageUI ui, String optionToSearchFor) {
		
		// Open the global search bar options list by clicking on the global search bar element
		openGlobalSearchBar(ui);
		
		// Search through all available options and determine if the target option is displayed
		return searchForOptionInGlobalSearchBar(ui, optionToSearchFor);
	}
	
	/**
	 * Generates a random hashtag in the correct format which can be appended to status updates
	 * 
	 * PLEASE NOTE:
	 * It was found, during local tests, that Helper.genStrongRand() sometimes generated Strings that ended with a '-' or '_' character
	 * such as 123ABC- or 123ABC_ and when this string is used in a tag, these tags are not properly rendered in the UI. Any tag using that
	 * string would be shortened to #BVT123ABC without the '-' or '_' character at the end. This means that the tag would not be picked 
	 * up correctly by Selenium and therefore would cause the test to fail.
	 * 
	 * @return - A randomly generated tag in the correct format
	 */
	public static String generateValidHashtag() {
		
		String hashTag = Helper.genStrongRand();
		while(hashTag.charAt(hashTag.length() - 1) == '-' || hashTag.charAt(hashTag.length() - 1) == '_') {
			hashTag = Helper.genStrongRand();
		}
		return hashTag;
	}
	
	/**
	 * Requests that indexing be performed on the specified tag by the specified user
	 * 
	 * @param globalSearchUI - The GlobalsearchUI instance to invoke the indexNow() method
	 * @param tagToBeIndexed - The String content of the tag to be indexed
	 * @param userThatPostedTag - The User instance of the user who posted the tag and is now requesting that indexing is performed
	 * @param serverURL - The String content of the server URL
	 * @param searchAdminService - The SearchAdminService instance to invoke all relevant methods during the indexing process
	 * @param adminUser - The User instance of the administrative user
	 */
	public static void performIndexingForSpecifiedStatusUpdateTag(GlobalsearchUI globalSearchUI, String tagToBeIndexed, User userThatPostedTag, String serverURL,
																	SearchAdminService searchAdminService, User adminUser) {
		log.info("INFO: Requesting that indexing is performed for the tag with content: " + tagToBeIndexed);
		boolean indexingSuccessful = false;
		try {
			globalSearchUI.indexNow(serverURL, searchAdminService, tagToBeIndexed, "status_updates", userThatPostedTag, adminUser);
			indexingSuccessful = true;
		} catch(Exception e) {
			log.info("ERROR: An exception was thrown while trying to perform indexing for the tag with content: " + tagToBeIndexed);
			e.printStackTrace();
		}
		
		log.info("INFO: Verifying that the indexing operation completed successfully for the tag with content: " + tagToBeIndexed);
		Assert.assertTrue(indexingSuccessful, 
							"ERROR: There was a problem with performing the indexing for the tag with content: " + tagToBeIndexed);
	}
	
	/**
	 * Posts a status update with file attachment using the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke the fileToUploadUsingRobotClass() method
	 * @param userUploadingFile - The User instance of the user uploading the file
	 * @param baseFile - The BaseFile instance of the file to be uploaded
	 * @param statusUpdateContent - The String content of the status update to be posted with the file attachment
	 * @return - True if all actions are completed successfully, false otherwise
	 */
	public static boolean addStatusUpdateWithFileAttachmentUsingUI(HomepageUI ui, RCLocationExecutor driver, FilesUI filesUI, User userUploadingFile, 
																	BaseFile baseFile, String statusUpdateContent) {
		// Switch focus to the status update frame
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// Type in the status update content into the status update input field
		UIEvents.typeStringWithNoDelay(ui, statusUpdateContent);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		log.info("INFO: Now clicking on the 'Add a File' link to open the 'Add a File' dialog");
		ui.clickLinkWait(HomepageUIConstants.AttachAFile);
		
		log.info("INFO: Now clicking on the 'My Computer' tab in the 'Add a File' dialog");
		ui.clickLinkWait(HomepageUIConstants.MyComputer);
		
		log.info("INFO: Now clicking on the 'Browse' button in the 'Add a File' dialog");
		ui.clickLinkWait(FilesUIConstants.getBrowseButton);
		
		log.info("INFO: " + userUploadingFile.getDisplayName() + " will now select a file to be uploaded from the local hard disk with filename: " + baseFile.getName());
		boolean fileUploaded = filesUI.uploadFileUsingRobotClass(baseFile.getName());
		
		log.info("INFO: Verify that the file name was selected correctly from the local hard disk");
		Assert.assertTrue(fileUploaded, 
							"ERROR: The file was NOT selected correctly from the local hard disk with filename: " + baseFile.getName());
		
		log.info("INFO: Verify that an icon for the selected file is displayed in the 'Add a File' dialog");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.AddAFile_UploadedFileIcon),
							"ERROR: A file icon for the selected file was NOT displayed in the 'Add a File' dialog");
		
		log.info("INFO: Verify that a clickable file name for the selected file is displayed in the 'Add a File' dialog");
		String clickableFilename = HomepageUIConstants.AddAFile_UploadedFileName.replaceAll("PLACEHOLDER", baseFile.getName().trim());
		Assert.assertTrue(ui.fluentWaitPresent(clickableFilename), 
							"ERROR: A clickable file name for the selected file was NOT displayed in the 'Add a File' dialog");
		
		log.info("INFO: Now clicking into the Tags input field in the 'Add a File' dialog");
		ui.clickElement(driver.getSingleElement(HomepageUIConstants.AddAFile_Tags_Input_Field));
		
		log.info("INFO: Now entering the necessary tags to the file to be uploaded with content: " + baseFile.getTags());
		UIEvents.typeStringWithNoDelay(ui, baseFile.getTags());
		
		log.info("INFO: Now clicking on the 'OK' button to attach the file with tags to the status update");
		ui.clickLinkWait(BaseUIConstants.OKButton);
		
		log.info("INFO: Verify that the 'Add a File' link is NOT displayed in the UI now that a file has been attached to the status update");
		Assert.assertFalse(ui.isElementVisible(HomepageUIConstants.AttachAFile),
							"ERROR: The 'Add a File' link was displayed in the UI after attaching a file to the status update");
		
		// Post the status update with file attachment
		postStatusUpdateUsingUI(ui);
		
		return true;
	}
	
	/**
	 * Posts a status update with file attachment using the UI - the file attachment in this case is taken from the users 'My Files' list
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userUploadingFile - The User instance of the user attaching the file
	 * @param baseFile - The BaseFile instance of the file to be attached
	 * @param statusUpdateContent - The String content of the status update to be posted with the file attachment
	 * @return - True if all actions are completed successfully, false otherwise
	 */
	public static boolean addStatusUpdateWithFileAttachmentUsingUI_FromMyFiles(HomepageUI ui, User userUploadingFile, BaseFile baseFile, String statusUpdateContent) {
		
		// Switch focus to the status update frame
		UIEvents.switchToStatusUpdateFrame(ui);
		
		// Type in the status update content into the status update input field
		UIEvents.typeStringWithNoDelay(ui, statusUpdateContent);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		log.info("INFO: Now clicking on the 'Add a File' link to open the 'Add a File' dialog");
		ui.clickLinkWait(HomepageUIConstants.AttachAFile);
		
		log.info("INFO: Now clicking on the 'Files' tab in the 'Add a File' dialog");
		ui.clickLinkWait(HomepageUIConstants.MyFilesTab);
		
		log.info("INFO: " + userUploadingFile.getDisplayName() + " will now select a file to be attached from the files list with filename: " + baseFile.getRename() + baseFile.getExtension());
		ui.clickLinkWait(HomepageUIConstants.MyFilesTab_SelectFile_RadioButton.replace("PLACEHOLDER", baseFile.getRename() + baseFile.getExtension()));
		
		if(baseFile.getShareLevel().equals(ShareLevel.NO_ONE) || baseFile.getShareLevel().equals(ShareLevel.PEOPLE)) {
			log.info("INFO: Verify that the warning message relating to the file being made visible to everyone is displayed in the UI");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FileMadePublicMessage), 
								"ERROR: The warning message relating to the file being made visible to everyone was NOT displayed in the UI");
		}
		
		log.info("INFO: Now clicking on the 'OK' button to attach the file with tags to the status update");
		ui.clickLinkWait(BaseUIConstants.OKButton);
		
		log.info("INFO: Verify that the 'Add a File' link is NOT displayed in the UI now that a file has been attached to the status update");
		Assert.assertFalse(ui.isElementVisible(HomepageUIConstants.AttachAFile),
							"ERROR: The 'Add a File' link was displayed in the UI after attaching a file to the status update");
		
		// Post the status update with file attachment
		postStatusUpdateUsingUI(ui);
				
		return true;
	}
	
	/**
	 * Deletes the specified status update using the UI by clicking on the 'X' removal icon for the status update in the AS
	 * 
	 * @param ui - The HomepageUI instance to invoke the deleteStatusUpdateUsingUI() method
	 * @param statusUpdateToBeDeleted - The String content of the status update to be deleted in the AS
	 */
	public static void deleteStatusUpdateUsingUI(HomepageUI ui, String statusUpdateToBeDeleted) {
		
		log.info("INFO: Now using the UI to delete the status update with content: " + statusUpdateToBeDeleted);
		ui.deleteStatusUpdateUsingUI(statusUpdateToBeDeleted);
	}
	
	/**
	 * Deletes the specified comment from the specified news story using the UI by clicking on the 'X' removal icon for the status update in the AS
	 * 
	 * @param ui - The HomepageUI instance to invoke the deleteNewsStoryCommentUsingUI() method
	 * @param newsStoryContent - The String content of the news story to which the comment to be deleted is posted
	 * @param commentToBeDeleted - The String content of the comment to be deleted from the news story
	 */
	public static void deleteCommentUsingUI(HomepageUI ui, String newsStoryContent, String commentToBeDeleted) {
		
		log.info("INFO: Now using the UI to delete the comment with content: " + commentToBeDeleted);
		ui.deleteNewsStoryCommentUsingUI(newsStoryContent, commentToBeDeleted);
	}
	
	/**
	 * Posts a status update with file attachment
	 * 
	 * @param apiUserPostingStatusUpdate - The APIProfilesHandler instance of the user posting the status update with file attachment
	 * @param statusUpdateContent - The String content of the status update to be posted
	 * @param fileToBeAttached - The FileEntry instance of the file to be attached to the status update
	 * @return - The String ID of the status update
	 */
	public static String postStatusUpdateWithFileAttachment(APIProfilesHandler apiUserPostingStatusUpdate, String statusUpdateContent, FileEntry fileToBeAttached) {
		
		log.info("INFO: " + apiUserPostingStatusUpdate.getDesplayName() + " will now post a status update with file attachment");
		String statusUpdateId = apiUserPostingStatusUpdate.postStatusUpdateWithFileAttachment(statusUpdateContent, fileToBeAttached);
		
		log.info("INFO: Verify that the status update with file attachment was posted successfully");
		Assert.assertNotNull(statusUpdateId, 
								"ERROR: The status update with file attachment was NOT posted and was returned as null");
		return statusUpdateId;
	}
	
	/**
	 * Saves any news story from either the I'm Following or Discover view using the API
	 * 
	 * @param newsStoryToBeSaved - The String content of the news story to be saved
	 * @param isDiscoverViewNewsStory - True if the news story to be saved is in the Discover view, false if it is in the I'm Following view
	 */
	public static void saveNewsStory(APIProfilesHandler apiUserSavingNewsStory, String newsStoryToBeSaved, boolean isDiscoverViewNewsStory) {
		
		log.info("INFO: " + apiUserSavingNewsStory.getDesplayName() + " will now save the news story with content: " + newsStoryToBeSaved);
		String newsStoryId = apiUserSavingNewsStory.getActivityStreamStoryId(newsStoryToBeSaved, isDiscoverViewNewsStory);
		
		log.info("INFO: Verify that the news story ID was retrieved successfully");
		Assert.assertFalse(newsStoryId.indexOf("ERROR") == 0, 
							"ERROR: The news story ID could NOT be retrieved using the API for the news story with content: " + newsStoryToBeSaved);
		
		log.info("INFO: Now saving the news story with ID: " + newsStoryId);
		boolean savedNewsStory = apiUserSavingNewsStory.saveNewsStory(newsStoryId);
		
		log.info("INFO: Verify that the news story was saved successfully");
		Assert.assertTrue(savedNewsStory, 
							"ERROR: The news story with ID '" + newsStoryId + "' could NOT be saved - false result returned");
	}
	
	/**
	 * Accepts an invitation to join another users network
	 * 
	 * @param invitationToBeAccepted - The Invitation instance of the network invite to be accepted
	 * @param apiUserAcceptingInvitation - The APIProfilesHandler instance of the user accepting the invite to join another users network
	 * @param apiUserWhoSentInvitation - The APIProfilesHandler instance of the user who sent out the network invitation
	 */
	public static void acceptInvitationToJoinANetwork(Invitation invitationToBeAccepted, APIProfilesHandler apiUserAcceptingInvitation, APIProfilesHandler apiUserWhoSentInvitation) {
		
		log.info("INFO: " + apiUserAcceptingInvitation.getDesplayName() + " will now accept the invitation to join " + apiUserWhoSentInvitation.getDesplayName() + "'s network");
		boolean inviteAccepted = apiUserAcceptingInvitation.acceptNetworkInvitation(invitationToBeAccepted, apiUserWhoSentInvitation);
		
		log.info("INFO: Verify that the invitation has been successfully accepted");
		Assert.assertTrue(inviteAccepted, 
							"ERROR: The invitation to join " + apiUserWhoSentInvitation.getDesplayName() + "'s network could NOT be accepted - a negative result was returned by the API");
	}
	
	/**
	 * Deletes a comment posted to a status update
	 * 
	 * @param statusUpdateId - The String content of the ID of the status update which contains the comment
	 * @param commentId - The String content of the ID of the comment which is to be deleted
	 * @param apiUserDeletingComment - The APIProfilesHandler instance of the user deleting the comment
	 */
	public static void deleteComment(String statusUpdateId, String commentId, APIProfilesHandler apiUserDeletingComment) {
		
		log.info("INFO: " + apiUserDeletingComment.getDesplayName() + " will now delete the comment with ID: " + commentId);
		boolean deletedComment = apiUserDeletingComment.deleteSUComment(statusUpdateId, commentId);
		
		log.info("INFO: Verify that the comment has been deleted successfully");
		Assert.assertTrue(deletedComment, 
							"ERROR: The comment could NOT be deleted - API returned a false response");
	}
	
	/**
	 * Clicks on the global search bar component in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the clickLinkWait() method
	 */
	private static void openGlobalSearchBar(HomepageUI ui) {
		
		log.info("INFO: Now clicking on the global search bar component");
		ui.clickLinkWait(BaseUIConstants.GlobalSearchBarDropdown);
	}
	
	/**
	 * Searches through all visible global search bar options for the specified option
	 * 
	 * @param ui - The HomepageUI instance to invoke the searchForElement() method
	 * @param optionToSearchFor - The String content of the option to be searched for in the global search bar options list
	 * @return - True if the option is displayed, false if it is absent
	 */
	private static boolean searchForOptionInGlobalSearchBar(HomepageUI ui, String optionToSearchFor) {
		
		log.info("INFO: Now searching through all global search bar options for the target option: " + optionToSearchFor);
		return ui.searchForElement(optionToSearchFor, BaseUIConstants.GlobalSearchBarContainer);
	}
	
	/**
	 * Verifies that a 'Post' link is disabled (ie. its aria-disabled attribute is set to "true")
	 * 
	 * @param driver - The RCLocationExecutor instance used to invoke the getSingleElement() method
	 */
	private static void verifyPostLinkIsDisabled(RCLocationExecutor driver) {
		
		// Retrieve the Post link as an Element so as it can be verified as disabled
		Element postLinkElement = driver.getSingleElement(HomepageUIConstants.PostComment);
		
		log.info("INFO: Verify that the 'Post' link is disabled having entered an invalid status update in the UI");
		Assert.assertTrue(postLinkElement.getAttribute("aria-disabled").equals("true"), 
							"ERROR: The 'Post' link was NOT disabled having entered an invalid status update in the UI");
	}
	
	/**
	 * Clicks on the 'Comment' link for the specified news story / status update and switches focus to the now visible AS comment frame 
	 * @param ui
	 * @param statusUpdateOrNewsStory
	 */
	private static void clickNewsStoryASCommentLinkAndSwitchToASCommentFrame(HomepageUI ui, String statusUpdateOrNewsStory) {
		
		// Click on the 'Comment' link for this status update / news story
		ui.clickStatusUpdateCommentLink(statusUpdateOrNewsStory);
				
		// Switch focus to the now visible comment frame
		UIEvents.switchToStatusUpdateCommentFrame(ui, statusUpdateOrNewsStory);
	}
}
