package com.ibm.conn.auto.util.newsStoryBuilder.files;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class FileNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'Add File To Folder' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param folderTitle - The String content containing the title of the folder to which the file has been added
	 * @param userNameAddingFile - The String content containing the user name of the user who has added the file to the folder
	 * @return - The String content containing the 'Add File To Folder' news story
	 */
	public static String getAddFileToFolderNewsStory(HomepageUI ui, String folderTitle, String userNameAddingFile) {
		return createNewsStory(ui, Data.FILE_ADDED_TO_FOLDER, folderTitle, null, userNameAddingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Commented On A File' news story
	 */
	public static String getCommentOnAFileNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.FILE_COMMENTED_YOU, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Comment On Their Own File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on their own file
	 * @return - The String content containing the 'Comment On Their Own File' news story
	 */
	public static String getCommentOnTheirOwnFileNewsStory(HomepageUI ui, String userNameCommenting) {
		return createNewsStory(ui, Data.FILE_COMMENTED_OWN_FILE, null, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 And User 2 Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the current user to comment on the file
	 * @param previousUserCommenting - The String content containing the user name of the previous user to comment on the file
	 * @return - The String content containing the 'User 1 And User 2 Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_TwoUsers(HomepageUI ui, String currentUserCommenting, String previousUserCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_FILE_TWO_COMMENTERS, previousUserCommenting, null, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name who commented on the file
	 * @return - The String content containing the 'You Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_User(HomepageUI ui, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_FILE, null, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the current user to comment on the file
	 * @param numberOfOtherCommenters - The String content containing the number of other users who have commented on the file
	 * @return - The String content containing the 'User And X Others Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_UserAndMany(HomepageUI ui, String currentUserCommenting, String numberOfOtherCommenters) {
		return createNewsStory(ui, Data.COMMENT_YOUR_FILE_MANY, numberOfOtherCommenters, null, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content containing the user name of the user to comment on the file
	 * @return - The String content containing the 'User And You Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_UserAndYou(HomepageUI ui, String userNameCommenting) {
		return createNewsStory(ui, Data.COMMENT_YOUR_FILE_YOU_OTHER, null, null, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.COMMENT_YOUR_FILE_YOU_NO_FILENAME, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherCommenters - The String content containing the number of other users who have commented on the file
	 * @return - The String content containing the 'You And X Others Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherCommenters) {
		return createNewsStory(ui, Data.COMMENT_YOUR_FILE_YOU_MANY, numberOfOtherCommenters, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Folder' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param folderTitle - The String content containing the title of the folder that has been created
	 * @param userNameAddingFolder - The String content containing the user name of the user who has created the folder
	 * @return - The String content containing the 'Create Folder' news story
	 */
	public static String getCreateFolderNewsStory(HomepageUI ui, String folderTitle, String userNameAddingFolder) {
		return createNewsStory(ui, Data.FOLDER_CREATED, folderTitle, null, userNameAddingFolder);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Edited A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameEditingFile - The String content containing the user name of the user who has edited the file
	 * @return - The String content containing the 'User Edited A File' news story
	 */
	public static String getEditFileNewsStory(HomepageUI ui, String userNameEditingFile) {
		return createNewsStory(ui, Data.FILE_EDITED, null, null, userNameEditingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Edited The File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param fileName - The String content of the filename for the file which has been edited
	 * @param userNameEditingFile - The String content containing the user name of the user who has edited the file
	 * @return - The String content containing the 'User Edited The File' news story
	 */
	public static String getEditFileNewsStory_WithFileName(HomepageUI ui, String fileName, String userNameEditingFile) {
		return createNewsStory(ui, Data.FILE_EDITED_ENTRY, fileName, null, userNameEditingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLikingFile - The String content containing the user name of the user who has liked / recommended the file
	 * @return - The String content containing the 'User Liked A File' news story
	 */
	public static String getLikedAFileNewsStory(HomepageUI ui, String userNameLikingFile) {
		return createNewsStory(ui, Data.FILE_LIKE, null, null, userNameLikingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked The File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLikingFile - The String content containing the user name of the user who has liked / recommended the file
	 * @return - The String content containing the 'User Liked The File' news story
	 */
	public static String getLikedTheFileNewsStory_User(HomepageUI ui, String likedFileName, String userNameLikingFile) {
		return createNewsStory(ui, Data.RECOMMENDED_FILE, null, likedFileName, userNameLikingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Like Their Own File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLikingFile - The String content containing the user name of the user who has liked / recommended the file
	 * @return - The String content containing the 'Like Their Own File' news story
	 */
	public static String getLikeTheirOwnFileNewsStory(HomepageUI ui, String userNameLikingFile) {
		return createNewsStory(ui, Data.FILE_LIKE_THEIR_OWN_FILE, null, null, userNameLikingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 And User 2 Liked Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLikingFile - The String content containing the user name of the current user to have liked the file
	 * @param previousUserLikingFile - The String content containing the user name of the previous user to have liked the file
	 * @return - The String content containing the 'User 1 And User 2 Liked Your File' news story
	 */
	public static String getLikeYourFileNewsStory_TwoUsers(HomepageUI ui, String currentUserLikingFile, String previousUserLikingFile) {
		return createNewsStory(ui, Data.RECOMMENDED_YOUR_FILE_TWO_LIKES, previousUserLikingFile, null, currentUserLikingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLiking - The String content of the user name who has liked / recommended the file
	 * @return - The String content containing the 'User Liked Your File' news story
	 */
	public static String getLikeYourFileNewsStory_User(HomepageUI ui, String userNameLiking) {
		return createNewsStory(ui, Data.RECOMMENDED_YOUR_FILE, null, null, userNameLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And X Others Liked Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content of the current user name who has liked / recommended the file
	 * @param numberOfOtherLikers - The String content containing the number of other users who have liked / recommended the file
	 * @return - The String content containing the 'User And X Others Liked Your File' news story
	 */
	public static String getLikeYourFileNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherLikers) {
		return createNewsStory(ui, Data.RECOMMENDED_YOUR_FILE_MANY, numberOfOtherLikers, null, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking - The String content containing the user name of the user to like / recommend the file
	 * @return - The String content containing the 'User And You Liked Your File' news story
	 */
	public static String getLikeYourFileNewsStory_UserAndYou(HomepageUI ui, String currentUserLiking) {
		return createNewsStory(ui, Data.RECOMMENDED_YOUR_FILE_YOU_OTHER, null, null, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Liked Your File' news story
	 */
	public static String getLikeYourFileNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.RECOMMENDED_YOUR_FILE_YOU, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherLikers - The String content containing the number of other users who have liked / recommended the file
	 * @return - The String content containing the 'You And X Others Liked Your File' news story
	 */
	public static String getLikeYourFileNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherLikers) {
		return createNewsStory(ui, Data.RECOMMENDED_YOUR_FILE_YOU_MANY, numberOfOtherLikers, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Was Made An Editor Of A Folder' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameEditor - The String content of the user name who was made editor of the folder
	 * @return - The String content containing the 'User Was Made An Editor Of A Folder' news story
	 */
	public static String getMadeEditorOfAFolderNewsStory_User(HomepageUI ui, String userNameEditor) {
		return createNewsStory(ui, Data.FOLDER_MADE_EDITOR_FROM_ME, null, null, userNameEditor);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Were Made An Editor Of A Folder' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameEditor - The String content of the user name who was made editor of the folder
	 * @return - The String content containing the 'You Were Made An Editor Of A Folder' news story
	 */
	public static String getMadeEditorOfAFolderNewsStory_You(HomepageUI ui) {
		return createNewsStory(ui, Data.FOLDER_MADE_EDITOR_FOR_ME, null, null, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Mentioned You In A Comment On A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMentioning - The String content of the user name mentioning the user in the comment
	 * @return - The String content containing the 'User Mentioned You In A Comment On A File' news story
	 */
	public static String getMentionedYouInACommentOnAFile(HomepageUI ui, String userNameMentioning) {
		return createNewsStory(ui, Data.MENTIONED_YOU_FILE_COMMENT, null, null, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Pinned A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNamePinningFile - The String content of the user name who has pinned the file
	 * @return - The String content containing the 'User Pinned A File' news story
	 */
	public static String getPinnedAFileNewsStory_User(HomepageUI ui, String userNamePinningFile) {
		return createNewsStory(ui, Data.FILE_PINNED, null, null, userNamePinningFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Shared A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameSharingFile - The String content containing the user name of the user who has shared the file
	 * @return - The String content containing the 'User Shared A File' news story
	 */
	public static String getSharedAFileNewsStory(HomepageUI ui, String userNameSharingFile) {
		return createNewsStory(ui, Data.FILE_SHARED_BASIC, null, null, userNameSharingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Shared A File With You' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameSharingFile - The String content containing the user name of the user who has shared the file
	 * @return - The String content containing the 'User Shared A File With You' news story
	 */
	public static String getSharedAFileWithYouNewsStory(HomepageUI ui, String userNameSharingFile) {
		return createNewsStory(ui, Data.FILE_SHARED, null, null, userNameSharingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Shared The File With You' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param fileName - The String content of the filename of the file which was shared with the user
	 * @param userNameSharingFile - The String content containing the user name of the user who has shared the file
	 * @return - The String content containing the 'User Shared The File With You' news story
	 */
	public static String getSharedTheFileWithYouNewsStory(HomepageUI ui, String fileName, String userNameSharingFile) {
		return createNewsStory(ui, Data.FILE_SHARED_WITH_YOU_NOTIFICATION_CENTER, fileName, null, userNameSharingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Comment On File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param fileTitle - The String content containing the title of the file which has been updated
	 * @param userNameUpdatingComment - The String content containing the user name of the user who has updated the file comment
	 * @return - The String content containing the 'Update Comment On File' news story
	 */
	public static String getUpdateCommentNewsStory(HomepageUI ui, String fileTitle, String userNameUpdatingComment) {
		return createNewsStory(ui, Data.UPDATED_COMMENT_FILE, fileTitle, null, userNameUpdatingComment);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameUpdatingFile - The String content containing the user name of the user who has updated the file
	 * @return - The String content containing the 'Update File' news story
	 */
	public static String getUpdateFileNewsStory(HomepageUI ui, String userNameUpdatingFile) {
		return createNewsStory(ui, Data.FILE_UPDATED, null, null, userNameUpdatingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Uploaded A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameUploadingFile - The String content containing the user name of the user who has uploaded the file
	 * @return - The String content containing the 'User Uploaded A File' news story
	 */
	public static String getUploadFileNewsStory(HomepageUI ui, String userNameUploadingFile) {
		return createNewsStory(ui, Data.FILE_UPLOADED, null, null, userNameUploadingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Uploaded The File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param fileName - The String content of the filename for the file which has been uploaded
	 * @param userNameUploadingFile - The String content containing the user name of the user who has uploaded the file
	 * @return - The String content containing the 'User Uploaded The File' news story
	 */
	public static String getUploadFileNewsStory_WithFileName(HomepageUI ui, String fileName, String userNameUploadingFile) {
		return createNewsStory(ui, Data.FILE_UPLOADED_THE_FILE, fileName, null, userNameUploadingFile);
	}
}