package com.ibm.conn.auto.util.newsStoryBuilder.community;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;

public class CommunityFileNewsStories extends BaseNewsStoryBuilder {
	
	/**
	 * Retrieves the news story corresponding the the 'Add File To Folder' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param folderTitle - The String content containing the title of the folder to which the file has been added
	 * @param userNameAddingFile - The String content containing the user name of the user who has added the file to the folder
	 * @return - The String content containing the 'Add File To Folder' news story
	 */
	public static String getAddFileToFolderNewsStory(HomepageUI ui, String folderTitle, String userNameAddingFile) {
		return FileNewsStories.getAddFileToFolderNewsStory(ui, folderTitle, userNameAddingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Comment On Their Own File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content containing the user name of the user who has commented on their own file
	 * @return - The String content containing the 'Comment On Their Own File' news story
	 */
	public static String getCommentOnTheirOwnFileNewsStory(HomepageUI ui, String userNameCommenting) {
		return FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameCommenting - The String content of the user name who commented on the file
	 * @return - The String content containing the 'You Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_User(HomepageUI ui, String userNameCommenting) {
		return FileNewsStories.getCommentOnYourFileNewsStory_User(ui, userNameCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - The String content containing the 'You Commented On Your File' news story
	 */
	public static String getCommentOnYourFileNewsStory_You(HomepageUI ui) {
		return FileNewsStories.getCommentOnYourFileNewsStory_You(ui);
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
		return FileNewsStories.getCreateFolderNewsStory(ui, folderTitle, userNameAddingFolder);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Edit File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameEditingFile - The String content containing the user name of the user who has edited / updated the file
	 * @return - The String content containing the 'Edit File' news story
	 */
	public static String getEditFileNewsStory(HomepageUI ui, String userNameEditingFile) {
		return FileNewsStories.getEditFileNewsStory(ui, userNameEditingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Like Their Own File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameLikingFile - The String content containing the user name of the user who has liked / recommended the file
	 * @return - The String content containing the 'Like Their Own File' news story
	 */
	public static String getLikeTheirOwnFileNewsStory(HomepageUI ui, String userNameLikingFile) {
		return FileNewsStories.getLikeTheirOwnFileNewsStory(ui, userNameLikingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Mentioned You In A Comment On A File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameMentioning - The String content of the user name mentioning the user in the comment
	 * @return - The String content containing the 'User Mentioned You In A Comment On A File' news story
	 */
	public static String getMentionedYouInACommentOnAFile(HomepageUI ui, String userNameMentioning) {
		return FileNewsStories.getMentionedYouInACommentOnAFile(ui, userNameMentioning);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Shared A File With The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityTitle - The String content of the community title in which the file has been shared
	 * @param userNameSharingFile - The String content containing the user name of the user who has shared the file
	 * @return - The String content containing the 'User Shared A File With The Community' news story
	 */
	public static String getShareFileWithCommunityNewsStory(HomepageUI ui, String communityTitle, String userNameSharingFile) {
		return createNewsStory(ui, Data.FILE_SHARED_WITH_COMM, communityTitle, null, userNameSharingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Shared The File With The Community' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param fileName - The String content of the file name of the file which has been shared
	 * @param communityTitle - The String content of the community title in which the file has been shared
	 * @param userNameSharingFile - The String content containing the user name of the user who has shared the file
	 * @return - The String content containing the 'User Shared The File With The Community' news story
	 */
	public static String getShareTheFileWithCommunityNewsStory(HomepageUI ui, String fileName, String communityTitle, String userNameSharingFile) {
		return createNewsStory(ui, Data.FILE_SHARED_WITH_COMM_ENTRY, fileName, communityTitle, userNameSharingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameUpdatingFile - The String content containing the user name of the user who has updated the file
	 * @return - The String content containing the 'Update File' news story
	 */
	public static String getUpdateFileNewsStory(HomepageUI ui, String userNameUpdatingFile) {
		return FileNewsStories.getUpdateFileNewsStory(ui, userNameUpdatingFile);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Upload File' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userNameUploadingFile - The String content containing the user name of the user who has uploaded the file
	 * @return - The String content containing the 'Upload File' news story
	 */
	public static String getUploadFileNewsStory(HomepageUI ui, String userNameUploadingFile) {
		return FileNewsStories.getUploadFileNewsStory(ui, userNameUploadingFile);
	}
}