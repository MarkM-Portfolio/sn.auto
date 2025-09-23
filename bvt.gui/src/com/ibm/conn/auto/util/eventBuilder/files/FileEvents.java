package com.ibm.conn.auto.util.eventBuilder.files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.BaseFileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
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
 * Date:	9th March 2016
 */

public class FileEvents extends BaseFileEvents {
	
	private static Logger log = LoggerFactory.getLogger(FileEvents.class);
	
	/**
	 * Create a new standalone file
	 * 
	 * @param baseFileTemplate - The BaseFile instance of the standalone file to be created
	 * @param userCreatingFile - The User instance of the user who is to creating the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is creating the file
	 * @return newFileEntry - The FileEntry instance of the newly created file
	 */
	public static FileEntry addFile(BaseFile baseFileTemplate, User userCreatingFile, APIFileHandler filesAPIUser) {
		
		log.info("INFO: Creating the absolute path for the file in the resources folder");
		String filePath = getResourcesDirAbsolutePath() + baseFileTemplate.getName();
		log.info("INFO: The absolute path to the file in the resources folder has been created: " + filePath);
		
		// Create the File instance of the file based on its filepath
		File newFile = new File(filePath);
		
		String fileAccess;
		if (baseFileTemplate.getShareLevel().equals(ShareLevel.EVERYONE)){
			fileAccess = "public";
		} else if(baseFileTemplate.getShareLevel().equals(ShareLevel.COMMUNITIEIS) || baseFileTemplate.getShareLevel().equals(ShareLevel.PEOPLE)) {
			fileAccess = "shared";
		} else {
			fileAccess = "private";
		}
		
		log.info("INFO: " + userCreatingFile.getDisplayName() + " will now create a new " + fileAccess + " file with title: " + baseFileTemplate.getRename() + baseFileTemplate.getExtension());
		FileEntry newFileEntry = filesAPIUser.createStandaloneFile(baseFileTemplate, newFile);
		
		log.info("INFO: Verify that the new file was created successfully");
		Assert.assertNotNull(newFileEntry, 
							"ERROR: The new file was NOT created successfully and was returned as null");
		return newFileEntry;
	}
	
	/**
	 * Likes / recommends a file as the owner / creator of the file
	 * 
	 * @param userLikingFile - The User instance of the user who is to liking the file 
	 * @param filesAPIUser - The APIFileHandler instance of the user who is liking the file
	 * @param fileEntry - The FileEntry instance of the file which is to be liked
	 * @return - The URL corresponding to the like / recommend file request
	 */
	public static String likeFile(User userLikingFile, APIFileHandler filesAPIUser, FileEntry fileEntry){

		log.info("INFO: " + userLikingFile.getDisplayName() + " will now like the file with file name: " + fileEntry.getTitle());
		String likedFileEntryURL = filesAPIUser.likeFile(fileEntry);

		log.info("INFO: Verify that the file was liked / recommended successfully");
		Assert.assertNotNull(likedFileEntryURL, 
								"ERROR: The file was NOT liked / recommended successfully and was returned as null");
		return likedFileEntryURL;
	}
	
	/**
	 * Adds a new file and then likes / recommends the file
	 * 
	 * @param baseFileTemplate - The BaseFile instance of the standalone file to be created and liked
	 * @param userLikingFile - The User instance of the user who is to creating and liking the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is creating and liking the file
	 * @return - The liked FileEntry instance
	 */
	public static FileEntry addAndLikeFile(BaseFile baseFileTemplate, User userLikingFile, APIFileHandler filesAPIUser){

		// Create the new file
		FileEntry fileEntry = addFile(baseFileTemplate, userLikingFile, filesAPIUser);

		// Like the created file
		likeFile(userLikingFile, filesAPIUser, fileEntry);
		
		return fileEntry;
	}

	/**
	 * Posts a comment to a file
	 * 
	 * @param userCommentingFile - The User instance of the user who is adding the comment to the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is adding the comment to the file
	 * @param fileEntry - The FileEntry instance of the file to which the comment will be added
	 * @param commentToBePosted - The comment content to be posted to the file
	 * @return commentAdded - A FileComment object
	 */
	public static FileComment addFileComment(User userCommentingFile, APIFileHandler filesAPIUser, FileEntry fileEntry, String commentToBePosted){

		log.info("INFO: " + userCommentingFile.getDisplayName() + " will now add a comment on the file with file name: " + fileEntry.getTitle());
		FileComment fileComment = new FileComment(commentToBePosted);
		FileComment commentAdded = filesAPIUser.CreateFileComment(fileEntry, fileComment);

		log.info("INFO: Verify that the file comment was added successfully");
		Assert.assertNotNull(commentAdded, 
							"ERROR: The file comment was NOT added successfully and was returned as null");

		return commentAdded;
	}
	
	/**
	 * This method enables a user other than the file owner to comment on the file
	 * 
	 * @param userCommentingFile - The User instance of the user who is adding the comment to the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is adding the comment to the file
	 * @param fileEntry - The FileEntry instance of the file to which the comment will be added
	 * @param commentToBePosted - The comment content to be posted to the file
	 * @param fileOwnerProfile - The APIProfilesHandler instance of the user who owns the file
	 * @return commentAdded - A FileComment object
	 */
	public static FileComment addFileCommentOtherUser(User userCommentingFile, APIFileHandler filesAPIUser, FileEntry fileEntry, String commentToBePosted, APIProfilesHandler fileOwnerProfile){
		
		log.info("INFO: " + userCommentingFile.getDisplayName() + " will now add a comment on the file owned by another user with file name: " + fileEntry.getTitle());
		FileComment fileComment = new FileComment(commentToBePosted);

		log.info("INFO: " + userCommentingFile.getDisplayName() + " adding a comment to the file using API method");
		FileComment commentAdded = filesAPIUser.CreateFileComment_OtherUser(fileEntry, fileComment, fileOwnerProfile.getUUID());

		log.info("INFO: Verify that the file comment was added successfully");
		Assert.assertNotNull(commentAdded, 
							"ERROR: The file comment was NOT added successfully and was returned as null");
		return commentAdded;
	}
	
	/**
	 * Posts a comment to a file
	 * 
	 * @param userCommentingFile - The User instance of the user who is adding the comment to the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is adding the comment to the file
	 * @param fileEntry - The FileEntry instance of the file to which the comment will be added
	 * @param commentToBePosted - The comment content to be posted to the file
	 * @return commentAdded - A FileComment object
	 */
	public static FileComment addCommentToFile(User userCommentingFile, APIFileHandler filesAPIUser, FileEntry fileEntry, String commentToBePosted){

		log.info("INFO: " + userCommentingFile.getDisplayName() + " will now add a comment on the file with file name: " + fileEntry.getTitle());
		FileComment fileComment = new FileComment(commentToBePosted);
		FileComment commentAdded = filesAPIUser.createFileComment_AnyUser(fileEntry, fileComment);

		log.info("INFO: Verify that the file comment was added successfully");
		Assert.assertNotNull(commentAdded, 
							"ERROR: The file comment was NOT added successfully and was returned as null");

		return commentAdded;
	}

	/**
	 * Updates a comment posted to a file
	 * 
	 * @param userCommentingFile - The User instance of the user who is editing the comment on the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is editing the comment on the file
	 * @param fileEntry - The FileEntry instance of the file on which the comment will be edited
	 * @param existingFileComment - The FileComment object which will be edited
	 * @param updatedComment - The comment content to which the existing comment will be updated
	 * @return commentEdited - A FileComment object
	 */
	public static FileComment updateFileComment(User userEditingFileComment, APIFileHandler filesAPIUser, FileEntry fileEntry, FileComment existingFileComment, String updatedComment){

		log.info("INFO: " + userEditingFileComment.getDisplayName() + " will now update a comment on the file to be: " + updatedComment);
		FileComment commentEdited = filesAPIUser.updateFileComment(existingFileComment, updatedComment);

		log.info("INFO: Verify that the file comment was edited successfully");
		Assert.assertTrue(commentEdited.getContent().trim().equals(updatedComment), 
							"ERROR: The file comment was NOT edited successfully and was returned with its original content still set");
		return commentEdited;
	}
	
	/**
	 * Uploads a new file version of an existing file
	 * 
	 * @param fileToBeUpdated - The FileEntry instance of the file to be updated
	 * @param baseFileNewVersion - The BaseFile instance of the new version of the file
	 * @param userUpdatingFile - The User instance of the user who is to update the file
	 * @param apiUserUpdatingFile - The APIFileHandler instance of the user who is updating the file
	 * @return - The FileEntry instance of the updated file
	 */
	public static FileEntry updateFileVersion(FileEntry fileToBeUpdated, BaseFile baseFileNewVersion, User userUpdatingFile, APIFileHandler apiUserUpdatingFile) {
		
		log.info("INFO: Now creating the absolute path to the new version of the file");
		String filePathNewVersion = getResourcesDirAbsolutePath() + baseFileNewVersion.getName();
		log.info("INFO: The absolute path to the new version of the file has been created: " + filePathNewVersion);
		
		// Create the File instance of the new file version based on its filepath
		File newFile = new File(filePathNewVersion);
						
		log.info("INFO: " + userUpdatingFile.getDisplayName() + " will now upload a new version of the file with filename '" + fileToBeUpdated.getTitle() + "'");
		FileEntry newFileVersion = apiUserUpdatingFile.uploadNewFileVersion(fileToBeUpdated, newFile);
		
		log.info("INFO: Verify that the new file version was uploaded successfully");
		Assert.assertNotNull(newFileVersion, 
								"ERROR: The new file version was NOT uploaded successfully and was returned as null");
		return newFileVersion;
	}
	
	/**
	 * Follows a standalone file
	 * 
	 * @param fileToBeFollowed - The FileEntry instance of the file to be followed
	 * @param userFollowingFile - The User instance of the user to follow the file
	 * @param apiUserFollowingFile - The APIFileHandler instance of the user to follow the file
	 */
	public static void followFile(FileEntry fileToBeFollowed, User userFollowingFile, APIFileHandler apiUserFollowingFile) {
		
		log.info("INFO: " + userFollowingFile.getDisplayName() + " will now follow the standalone file with file name: " + fileToBeFollowed.getTitle());
		Assert.assertTrue(apiUserFollowingFile.followFile(fileToBeFollowed), 
							"ERROR: The file could not be followed and returned a false result");
	}
	
	/**
	 * Posts a comment to a file and then edits that comment
	 * 
	 * @param userCommentingFile - The User instance of the user who is adding and editing the comment on the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is adding and editing the comment on the file
	 * @param fileEntry - The FileEntry instance of the file on which the comment will be added and edited
	 * @param commentToBePosted - The comment content to be posted to the file
	 * @param updatedComment - The comment content to which the existing comment will be updated
	 * @return - A FileComment object
	 */
	public static FileComment addAndEditFileComment(User userCommentingFile, APIFileHandler filesAPIUser, FileEntry fileEntry, String commentToBePosted, String updatedComment){

		// Add the comment to the file
		FileComment fileComment = addFileComment(userCommentingFile, filesAPIUser, fileEntry, commentToBePosted);

		// Update the comment posted to the file
		return updateFileComment(userCommentingFile, filesAPIUser, fileEntry, fileComment, updatedComment);
	}

	/**
	 * 
	 * @param userCommentingFile - The User instance of the user who is adding the mentions comment on the file
	 * @param filesAPIUser - The APIFileHandler instance of the user who is adding the mentions comment to the file
	 * @param fileEntry - The FileEntry instance of the file on which the mentions comment will be added
	 * @param mentions - A Mentions object whose attributes are required to mention a user in the file comment
	 * @return mentionCommentAdded - A FileComment object
	 */
	public static FileComment addFileMentionsComment(User userCommentingFile, APIFileHandler filesAPIUser, FileEntry fileEntry, Mentions mentions){

		log.info("INFO: " + userCommentingFile.getDisplayName() + " add a file comment with a mentions to " + mentions.getUserToMention().getDisplayName());
		FileComment mentionCommentAdded = filesAPIUser.addMentionFileCommentAPI(fileEntry, mentions);

		log.info("INFO: Verify that the file comment was added successfully");
		Assert.assertNotNull(mentionCommentAdded, 
							"ERROR: The file comment mentions was NOT added successfully and was returned as null");

		return mentionCommentAdded;
	}
	
	/**
	 * Adds a new standalone file and then the specified user follows that file
	 * 
	 * @param baseFileTemplate - The BaseFile instance of the standalone file to be created and followed
	 * @param userCreatingFile - The User instance of the user who is to creating the file
	 * @param apiUserAddingFile - The APIFileHandler instance of the user who is creating the file
	 * @param userFollowingFile - The User instance of the user to follow the file
	 * @param apiUserFollowingFile - The APIFileHandler instance of the user to follow the file
	 * @return - The FileEntry instance of the created and followed file
	 */
	public static FileEntry addFileWithOneFollower(BaseFile baseFileTemplate, User userAddingFile, APIFileHandler apiUserAddingFile, User userFollowingFile, APIFileHandler apiUserFollowingFile) {
		
		// Add the standalone file
		FileEntry newFileEntry = addFile(baseFileTemplate, userAddingFile, apiUserAddingFile);
		
		// Have the specified user follow the newly created file
		followFile(newFileEntry, userFollowingFile, apiUserFollowingFile);
		
		return newFileEntry;
	}
	
	/**
	 * Opens the file details overlay for a file in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the filterNewsItemOpenFileOverlay() method
	 * @param fileNewsStory - The news story corresponding to the file to be opened
	 */
	public static void openFileOverlay(HomepageUI ui, String fileNewsStory) {
		
		// Click on the 'Show More' link before searching the news feed for the news item
		UIEvents.clickShowMore(ui);
		
		log.info("INFO: Now opening the file details overlay for the news story with content: " + fileNewsStory);
		ui.openNewsStoryFileDetailsOverlay(fileNewsStory);
	}
	
	/**
	 * Switches focus to the comment input field in the file details overlay
	 * 
	 * @param ui - The FilesUI instance to invoke the switchToFileOverlayCommentFrame() method
	 */
	public static void switchToFileOverlayCommentFrame(FilesUI ui) {
		
		log.info("INFO: Now switching focus to the comment input frame in the file details overlay");
		ui.switchToFileOverlayCommentFrame();
	}
	
	/**
	 * Opens the file details overlay and switches focus to the comment frame
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 */
	public static void openFileOverlayAndSwitchToCommentFrame(HomepageUI homepageUI, FilesUI filesUI, String newsStory) {
		
		// Open the file details overlay
		openFileOverlay(homepageUI, newsStory);
		
		// Switch focus to the comment frame in the file details overlay
		switchToFileOverlayCommentFrame(filesUI);
	}
	
	/**
	 * Opens the file details overlay and types a partial mention into the comment box (does NOT post any comments)
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The integer representation of how many characters are to be typed in the partial mention
	 */
	public static void openFileOverlayAndTypePartialMention(HomepageUI homepageUI, FilesUI filesUI, String newsStory, Mentions mentions, int numberOfCharactersToType) {
		
		// Open the file details overlay and switch focus to the comment input frame
		openFileOverlayAndSwitchToCommentFrame(homepageUI, filesUI, newsStory);
		
		// Type the partial mentions into the comment input field
		UIEvents.typeBeforeMentionsTextAndTypePartialMentions(homepageUI, mentions, numberOfCharactersToType);
	}
	
	/**
	 * Opens the file details overlay and types a partial mention into the comment box (does NOT post any comments)
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The integer representation of how many characters are to be typed in the partial mention
	 * @return - The user name of the user selected from the typeahead
	 */
	public static String openFileOverlayAndTypePartialMentionAndSelectFirstTypeaheadMenuItem(HomepageUI homepageUI, RCLocationExecutor driver, FilesUI filesUI, String newsStory, Mentions mentions, int numberOfCharactersToType) {
		
		// Open the file details overlay and type the partial mention into the comment input field
		openFileOverlayAndTypePartialMention(homepageUI, filesUI, newsStory, mentions, numberOfCharactersToType);
		
		// Wait for the typeahead menu to appear
		UIEvents.waitForTypeaheadMenuToLoad(homepageUI);
		
		// Retrieve the first element from the typeahead menu and select it - returning the text value of that element
		String userNameSelected = UIEvents.getFirstTypeaheadMenuItemAndSelectUser(homepageUI, driver);
		
		// Switch focus back to the comments frame
		switchToFileOverlayCommentFrame(filesUI);
		
		// Verify that the mentions link is displayed for the selected user
		UIEvents.verifyMentionsLinkIsDisplayed(homepageUI, userNameSelected);
		
		return userNameSelected;
	}
	
	/**
	 * Opens the file details overlay and types a partial mention into the comment box (does NOT post any comments)
	 * This method then attempts to retrieve the photo element included with the menu item and returns a map of all elements with their respective photo
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The integer representation of how many characters are to be typed in the partial mention
	 * @return - A HashMap of the menu item Element instances mapped to their corresponding photo Element instances
	 */
	public static HashMap<Element, Element> openFileOverlayAndTypePartialMentionAndGetTypeaheadMenuItemsAndPhotos(HomepageUI homepageUI, RCLocationExecutor driver, 
																									FilesUI filesUI, String newsStory, Mentions mentions, int numberOfCharactersToType) {		
		// Open the file details overlay and type the partial mention into the comment input field
		openFileOverlayAndTypePartialMention(homepageUI, filesUI, newsStory, mentions, numberOfCharactersToType);
		
		// Wait for the typeahead menu to appear
		UIEvents.waitForTypeaheadMenuToLoad(homepageUI);
		
		// Retrieve all of the elements from the typeahead menu - including their photos
		return UIEvents.getTypeaheadMenuItemsAndPhotos(homepageUI, driver);
	}
	
	/**
	 * Opens the file details overlay, adds a partial mention to the comments input field and retrieves all text that appears in the typeahead selection menu
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param numberOfCharactersToType - The integer representation of how many characters are to be typed in the partial mention
	 * @return - An ArrayList<String> of the text contents of all of the typeahead menu items
	 */
	public static ArrayList<String> openFileOverlayAndTypePartialMentionAndGetTypeaheadMenuTextContents(HomepageUI homepageUI, RCLocationExecutor driver, FilesUI filesUI, 
																												String newsStory, Mentions mentions, int numberOfCharactersToType) {
		// Open the file details overlay and type the partial mention into the comment input field
		openFileOverlayAndTypePartialMention(homepageUI, filesUI, newsStory, mentions, numberOfCharactersToType);
		
		// Wait for the typeahead menu to appear
		UIEvents.waitForTypeaheadMenuToLoad(homepageUI);
		
		// Retrieve the text content from each of the visible menu items
		return UIEvents.getTypeaheadMenuItemsAsText(homepageUI, driver);
	}
	
	/**
	 * Opens the file details overlay and types in a full mentions string to the specified user, selecting them from the typeahead as it goes
	 * 
	 * PLEASE NOTE: This method does NOT post the comment with mention - it only types it into the comment input box in the file details overlay
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - If the method reaches its end (ie. everything works as expected) then it will return a true value indicating that everything was fine
	 */
	public static boolean openFileOverlayAndTypeMentionAndSelectMentionedUser(HomepageUI homepageUI, RCLocationExecutor driver, FilesUI filesUI, String newsStory, 
																				Mentions mentions) {
		// Open the file details overlay and switch focus to the comment input frame
		openFileOverlayAndSwitchToCommentFrame(homepageUI, filesUI, newsStory);
		
		// Enter the before mentions text and the mention text and select the correct user from the typeahead menu
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(homepageUI, driver, mentions);
		
		// Switch focus back to the comment frame in the file details overlay
		switchToFileOverlayCommentFrame(filesUI);
		
		// Verify that the mentions link is displayed for the selected user
		UIEvents.verifyMentionsLinkIsDisplayed(homepageUI, mentions.getUserToMention().getDisplayName());
		
		// Type in the after mentions text
		UIEvents.typeAfterMentionsText(homepageUI, mentions);
		
		return true;
	}
	
	/**
	 * Opens the file details overlay, types in the before mentions text and then mentions text. The mentioned user is then selected from the typeahead menu
	 * before the mention is deleted / removed again by pressing backspace.
	 * 
	 * PLEASE NOTE: This method does NOT post the comment with mention - it only types it into the comment input box in the file details overlay
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - If the method reaches its end (ie. everything works as expected) then it will return a true value indicating that everything was fine
	 */
	public static boolean openFileOverlayAndTypeMentionAndSelectMentionedUserAndDeleteMention(HomepageUI homepageUI, RCLocationExecutor driver, FilesUI filesUI, String newsStory, 
																							Mentions mentions) {
		// Open the file details overlay and switch focus to the comment input frame
		openFileOverlayAndSwitchToCommentFrame(homepageUI, filesUI, newsStory);
		
		// Enter the before mentions text and the mention text and select the correct user from the typeahead menu
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(homepageUI, driver, mentions);
		
		// Switch focus back to the comment frame in the file details overlay
		switchToFileOverlayCommentFrame(filesUI);
		
		// Verify that the mentions link is displayed for the selected user
		UIEvents.verifyMentionsLinkIsDisplayed(homepageUI, mentions.getUserToMention().getDisplayName());
		
		// Now press the backspace key twice to remove the mention again (the first strike of the backspace key is interpreted as a dummy key)
		return UIEvents.deleteMentionsLinkWithBackspaceKey(driver, homepageUI, mentions.getUserToMention().getDisplayName());
	}
	
	/**
	 * Opens the file details overlay, types in the before mentions text and then mentions text. The mentioned user is then selected from the typeahead menu
	 * before the mention link is highlighted and then deleted / removed again by pressing the delete key.
	 * 
	 * PLEASE NOTE: This method does NOT post the comment with mention - it only types it into the comment input box in the file details overlay
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - If the method reaches its end (ie. everything works as expected) then it will return a true value indicating that everything was fine
	 */
	public static boolean openFileOverlayAndTypeMentionAndSelectMentionedUserAndHighlightAndDeleteMention(HomepageUI homepageUI, RCLocationExecutor driver, 
																												FilesUI filesUI, String newsStory, Mentions mentions) {
		// Open the file details overlay and switch focus to the comment input frame
		openFileOverlayAndSwitchToCommentFrame(homepageUI, filesUI, newsStory);
		
		// Enter the before mentions text and the mention text and select the correct user from the typeahead menu
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(homepageUI, driver, mentions);
		
		// Switch focus back to the comment frame in the file details overlay
		switchToFileOverlayCommentFrame(filesUI);
		
		// Verify that the mentions link is displayed for the selected user
		UIEvents.verifyMentionsLinkIsDisplayed(homepageUI, mentions.getUserToMention().getDisplayName());
		
		// Highlight the link and delete the link
		UIEvents.deleteMentionsLinkWithHighlightAndDelete(homepageUI, driver, mentions);
		
		return true;
	}
	
	/**
	 * Opens the file details overlay, types in the before mentions text and then mentions a different org / guest user.
	 * 
	 * @param homepageUI - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param filesUI - The FilesUI instance to invoke all relevant methods
	 * @param newsStory - The news story corresponding to the file to be opened
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - If the method reaches its end (ie. everything works as expected) then it will return a true value indicating that everything was fine
	 */
	public static boolean openFileOverlayAndTypeMentionToDifferentOrgUserOrGuest(HomepageUI homepageUI, RCLocationExecutor driver, FilesUI filesUI, String newsStory, Mentions mentions) {
		
		// Open the file details overlay and switch focus to the comment input frame
		openFileOverlayAndSwitchToCommentFrame(homepageUI, filesUI, newsStory);
		
		// Type in the mentions to the different org / guest user
		UIEvents.typeMentionsToDifferentOrgUserAndGuestUser(homepageUI, driver, mentions);
		
		return true;
	}
	/**
	 * Creates a folder
	 * 
	 * @param userCreatingFolder - The User instance of the user who is creating the folder
	 * @param apiUserCreatingFolder - The APIFileHandler instance of the user who is creating the folder
	 * @param baseFolder - The BaseFile instance of the folder which is to be created
	 * @param role - The StringConstants.Role for any users who are shared with the folder, e.g. Role.All = contributor, Role.Owner = manager, Role.Reader = reader
	 * @return folderEntry - A FileEntry object
	 */
	public static FileEntry createFolder(User userCreatingFolder, APIFileHandler apiUserCreatingFolder, BaseFile baseFolder, StringConstants.Role role){
		
		log.info("INFO: " + userCreatingFolder.getDisplayName() + " will now create a new folder with title: " + baseFolder.getName());
		FileEntry folderEntry = apiUserCreatingFolder.createFolder(baseFolder, role);
		
		log.info("INFO: Verify that the folder was successfully created");
		Assert.assertNotNull(folderEntry, 
								"ERROR: The folder was NOT successfully created and was returned as null");
		
		return folderEntry;
	}
	
	/**
	 * Follows a folder
	 * 
	 * @param folderToBeFollowed - The FileEntry instance of the folder to be followed
	 * @param userFollowingFolder - The User instance of the user to follow the folder
	 * @param apiUserFollowingFolder - The APIFileHandler instance of the user to follow the folder
	 */
	public static void followFolder(FileEntry folderToBeFollowed, User userFollowingFolder, APIFileHandler apiUserFollowingFolder) {
		
		log.info("INFO: " + userFollowingFolder.getDisplayName() + " will now follow the folder with title: " + folderToBeFollowed.getTitle());
		boolean folderFollowed = apiUserFollowingFolder.followFolder(folderToBeFollowed);
		
		log.info("INFO: Verify that the folder was followed successfully");
		Assert.assertTrue(folderFollowed, 
							"ERROR: The folder could NOT be followed - a negative result was returned from the API method call");
	}
	
	/**
	 * Creates a new folder and then allows a specified user to follow the folder
	 * 
	 * @param baseFolder - The BaseFile instance of the folder which is to be created
	 * @param role - The StringConstants.Role for any users who are shared with the folder, e.g. Role.All = contributor, Role.Owner = manager, Role.Reader = reader
	 * @param userCreatingFolder - The User instance of the user who is creating the folder
	 * @param apiUserCreatingFolder - The APIFileHandler instance of the user who is creating the folder
	 * @param userFollowingFolder - The User instance of the user to follow the folder
	 * @param apiUserFollowingFolder - The APIFileHandler instance of the user to follow the folder
	 * @return folder - A FileEntry object
	 */
	public static FileEntry createFolderAndFollowFolder(BaseFile baseFolder, StringConstants.Role role, User userCreatingFolder, APIFileHandler apiUserCreatingFolder,
															User userFollowingFolder, APIFileHandler apiUserFollowingFolder) {
		// Create the new folder
		FileEntry folder = createFolder(userCreatingFolder, apiUserCreatingFolder, baseFolder, role);
		
		// Have the specified user follow the folder
		followFolder(folder, userFollowingFolder, apiUserFollowingFolder);
		
		return folder;
	}
	
	/**
	 * Adds a file to a folder
	 * 
	 * @param userAddingFileToFolder - The User instance of the user who is adding the file to the folder
	 * @param apiUserAddingFile - The APIFileHandler instance of the user who is adding the file to the folder
	 * @param fileEntry - The FileEntry instance of the file which is to be added to the folder
	 * @param folderEntry - The FileEntry instance of the folder to which the file is to be added
	 * @return newFileEntry - A FileEntry object
	 */
	public static FileEntry addFileToFolder(User userAddingFileToFolder, APIFileHandler apiUserAddingFile, FileEntry fileEntry, FileEntry folderEntry){

		ArrayList<String> filesList = new ArrayList<String>();
		filesList.add(fileEntry.getId().toString().split(Data.getData().FileListPrefix)[1]);
		
		log.info("INFO: " + userAddingFileToFolder.getDisplayName() + " will add the file to the folder with file name: " + fileEntry.getTitle());
		FileEntry newFileEntry = apiUserAddingFile.addFilestoFolder(folderEntry, filesList);

		log.info("INFO: Verify that the file was successfully added to the folder");
		Assert.assertNotNull(newFileEntry, 
								"ERROR: The file was NOT successfully added to the folder and was returned as null");
		
		return newFileEntry;
	}
	
	/**
	 * Removes a file from a folder
	 * 
	 * @param userRemovingFileFromFolder - The User instance of the user who is removing the file from the folder
	 * @param apiUserRemovingFile - The APIFileHandler instance of the user who is removing the file from the folder
	 * @param fileEntry - The FileEntry instance of the file which is to be removed from the folder
	 * @param folderEntry - The FileEntry instance of the folder from which the file is to be removed
	 */
	public static void removeFileFromFolder(User userRemovingFileFromFolder, APIFileHandler apiUserRemovingFile, FileEntry fileEntry, FileEntry folderEntry){

		log.info("INFO: " + userRemovingFileFromFolder.getDisplayName() + " will remove the file from the folder with file name: " + fileEntry.getTitle());		
		apiUserRemovingFile.removeFileFromFolder(fileEntry, folderEntry);
	}
	
	/**
	 * Pins a file
	 * 
	 * @param userPinningFile - The User instance of the user who is to pin the file
	 * @param apiUserPinningFile - The APIFileHandler instance of the user who is pinning the file
	 * @param fileEntry - The FileEntry instance of the file which will be pinned
	 * @return - The FileEntry instance of the pinned file
	 */
	public static FileEntry pinFile(User userPinningFile, APIFileHandler apiUserPinningFile, FileEntry fileEntry){

		log.info("INFO: " + userPinningFile.getDisplayName() + " will pin the file with file name: " + fileEntry.getTitle());
		return apiUserPinningFile.pinFile(fileEntry);
	}
	
	/**
	 * Verifies that the Files UI screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyFilesUIIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now verifying that the I'm Following view is displayed in the UI");
		
		log.info("INFO: Verify that the Files UI heading is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ComponentHPFiles), 
							"ERROR: The Files UI heading was NOT displayed in Files UI");
		
		log.info("INFO: Verify that the Files UI upload instructions text is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().DragAndDropFilesToUpload), 
							"ERROR: The Files UI upload instructions text was NOT displayed in Files UI");
	}
	
	/**
	 * Shares the specified file with the specified user
	 * 
	 * @param fileToBeShared - The FileEntry instance of the file to be shared
	 * @param userSharingFile - The User instance of the user sharing the file
	 * @param apiUserSharingFile - The APIFileHandler instance of the user sharing the file
	 * @param apiUserToShareFileWith - The APIProfilesHandler instance of the user to share the file with
	 */
	public static void shareFileWithUser(FileEntry fileToBeShared, User userSharingFile, APIFileHandler apiUserSharingFile, APIProfilesHandler apiUserToShareFileWith) {
		
		log.info("INFO: " + userSharingFile.getDisplayName() + " will now share a file with the user with user name: " + apiUserToShareFileWith.getDesplayName());
		boolean fileShared = apiUserSharingFile.shareFile(fileToBeShared, apiUserToShareFileWith.getUUID());
		
		log.info("INFO: Verify that the file was shared successfully");
		Assert.assertTrue(fileShared, 
							"ERROR: The file was NOT shared as expected - the API returned a negative response");
	}
	
	/**
	 * Likes / recommends a file as a user who is NOT the owner / creator of the file
	 * 
	 * @param userLikingFile - The User instance of the user who is to liking the file 
	 * @param filesAPIUser - The APIFileHandler instance of the user who is liking the file
	 * @param fileEntry - The FileEntry instance of the file which is to be liked
	 * @return likedFileEntryURL - The URL corresponding to the like / recommend file request
	 */
	public static String likeFileOtherUser(FileEntry fileToBeLiked, User userLikingFile, APIFileHandler apiUserLikingFile) {
		
		log.info("INFO: " + userLikingFile.getDisplayName() + " will now like / recommend the file with filename: " + fileToBeLiked.getTitle());
		String likedFileEntryURL = apiUserLikingFile.likeFile_OtherUser(fileToBeLiked);
		
		log.info("INFO: Verify that the file was liked / recommended successfully");
		Assert.assertNotNull(likedFileEntryURL, 
								"ERROR: The file was NOT liked / recommended successfully and was returned as null");
		return likedFileEntryURL;
	}
	
	/**
	 * Unlikes the specified file
	 * 
	 * @param fileToBeUnliked - The FileEntry instance of the file to be unliked
	 * @param userUnlikingFile - The User instance of the user unliking the file
	 * @param apiUserUnlikingFile - The APIFileHandler instance of the user unliking the file
	 */
	public static void unlikeFile(FileEntry fileToBeUnliked, String likeFileURL, User userUnlikingFile, APIFileHandler apiUserUnlikingFile) {
		
		log.info("INFO: " + userUnlikingFile.getDisplayName() + " will now unlike the file with filename: " + fileToBeUnliked.getTitle());
		boolean unlikedFile = apiUserUnlikingFile.unlikeFile(likeFileURL);
		
		log.info("INFO: Verify that the file has been unliked successfully");
		Assert.assertTrue(unlikedFile, 
							"ERROR: The file could NOT be unliked - the API returned a negative response");
	}
	
	/**
	 * Like / recommend a file using the 'Like' button in the file details overlay
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userLikingFile - The User instance of the user liking / recommending the file
	 */
	public static void likeFileUsingUI(HomepageUI ui, RCLocationExecutor driver, User userLikingFile) {
		
		log.info("INFO: Wait for the 'Like' file button to be displayed in the file details overlay");
		ui.fluentWaitPresent(FileViewerUI.LikeButton_FiDO);
		
		// Retrieve the element corresponding to the 'Like' button in the file details overlay
		Element likeFileButton = driver.getFirstElement(FileViewerUI.LikeButton_FiDO);
		
		int numberOfRetries = 0;
		boolean clickedLikeButton = false;
		do {
			log.info("INFO: " + userLikingFile.getDisplayName() + " will now try to like the file using the file details overlay");
			ui.clickElement(likeFileButton);
			
			log.info("INFO: Verifying that the 'Like' button has been clicked successfully (ie. the 'Unlike' button should now be displayed");
			if(ui.isElementVisible(FileViewerUI.UnlikeButton_FiDO) == true) {
				log.info("INFO: The 'Like' button was successfully clicked in the file details overlay");
				clickedLikeButton = true;
			} else {
				log.info("INFO: The 'Like' button could NOT clicked in the file details overlay");
				if((numberOfRetries + 1) < 3) {
					log.info("INFO: Now re-trying to click on the 'Like' button in the file details overlay");
				} else {
					log.info("ERROR: No further attempts will be made to click on the 'Like' button in the file details overlay");
					log.info("ERROR: Could NOT click on the 'Like' button after three attempts have been made to do so.");
				}
			}
			numberOfRetries ++;
		} while(numberOfRetries < 3 && clickedLikeButton == false);
		
		log.info("INFO: Verify that the 'unlike' button is now displayed in the file details overlay after liking / recommending the file");
		Assert.assertTrue(ui.fluentWaitPresent(FileViewerUI.UnlikeButton_FiDO), 
							"ERROR: The 'unlike' button was NOT displayed in the file details overlay after liking / recommending the file");
	}
	
	/**
	 * Closes the file details overlay by clicking on the 'Close' button
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void closeFileOverlay(HomepageUI ui) {
		
		log.info("INFO: Now closing the file details overlay");
		ui.clickLinkWait(FileViewerUI.CloseButton);
	}
	
	/**
	 * Posts a comment to a file using the file details overlay
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiFi - The FilesUI instance to invoke all relevant methods
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param commentToBePosted - The String content of the comment to be posted to the file
	 */
	public static void addFileCommentUsingUI(HomepageUI ui, FilesUI uiFi, User userPostingComment, String commentToBePosted) {
		
		log.info("INFO: " + userPostingComment.getDisplayName() + Helper.genStrongRand() + " will now post a comment to the file using the file details overlay");
		
		// Switch focus to the file details overlay comments frame
		switchToFileOverlayCommentFrame(uiFi);
		
		// Enter the comment into the comment frame
		UIEvents.typeStringWithNoDelay(ui, commentToBePosted);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		log.info("INFO: Now clicking on the 'Post' link to post the comment in the file details overlay");
		ui.clickLinkWait(FileViewerUI.PostCommentButton);
		
		log.info("INFO: Verify that the comment is now displayed in the file details overlay after posting");
		Assert.assertTrue(ui.fluentWaitTextPresent(commentToBePosted), 
							"ERROR: The comment was NOT displayed in the file details overlay after posting with content: " + commentToBePosted);
	}
	
	/**
	 * Deletes a comment posted to the specified file
	 * 
	 * @param commentToBeDeleted - The FileComment instance of the file comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the file comment
	 * @param apiUserDeletingComment - The APIFileHandler instance of the user deleting the file comment
	 */
	public static void deleteFileComment(FileComment commentToBeDeleted, User userDeletingComment, APIFileHandler apiUserDeletingComment) {
		
		log.info("INFO: " + userDeletingComment.getDisplayName() + " will now delete their comment posted to the file with content: " + commentToBeDeleted.getContent().trim());
		boolean commentDeleted = apiUserDeletingComment.deleteFileComment(commentToBeDeleted);
		
		log.info("INFO: Verify that the comment posted to the file has been deleted successfully");
		Assert.assertTrue(commentDeleted, 
							"ERROR: The file comment could NOT be deleted - API returned a false result");
	}
}