package com.ibm.conn.auto.util.eventBuilder.community;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.BaseFileEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
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
 * Date:	8th March 2016
 */

public class CommunityFileEvents extends BaseFileEvents {

	private static Logger log = LoggerFactory.getLogger(CommunityFileEvents.class);
	
	/**
	 * Adds a new file to a community
	 * 
	 * @param community - The Community instance of the community to which the file is to be added
	 * @param baseFileTemplate - The BaseFile instance of the file to be added to the community
	 * @param userAddingFile - The User instance of the user who is to add the file to the community
	 * @param apiUserAddingFile - The APIFileHandler instance of the user who is to add the file to the community
	 * @return - The FileEntry instance of the newly added community file
	 */
	public static FileEntry addFile(Community community, BaseFile baseFileTemplate, User userAddingFile, APIFileHandler apiUserAddingFile) {
		
		log.info("INFO: Creating the absolute path for the file in the resources folder");
		String filePath = getResourcesDirAbsolutePath() + baseFileTemplate.getName();
		log.info("INFO: The absolute path to the file in the resources folder has been created: " + filePath);
		
		// Create the File instance of the file based on its filepath
		File newFile = new File(filePath);
		
		log.info("INFO: " + userAddingFile.getDisplayName() + " will now add a file to the community with title: " + community.getTitle());
		return apiUserAddingFile.CreateFile(baseFileTemplate, newFile, community);
	}
	
	/**
	 * Adds a comment to a community file
	 * 
	 * @param community - The Community instance of the community in which the file to be commented on is located
	 * @param communityFile - The FileEntry instance of the file to which the comment is to be posted
	 * @param commentToBePosted - The comment to be posted to the file
	 * @param userPostingComment - The User instance of the user commenting on the file
	 * @param apiUserPostingComment - The APIFileHandler instance of the user commenting on the file
	 * @return - The FileComment instance of the comment posted to the file
	 */
	public static FileComment addComment(Community community, FileEntry communityFile, String commentToBePosted, User userPostingComment, APIFileHandler apiUserPostingComment) {
		
		log.info("INFO: " + userPostingComment + " will now post a comment to the community file with file name: " + communityFile.getTitle());
		
		// Create the FileComment instance of the comment to be posted
		FileComment fileComment = new FileComment(commentToBePosted);
		
		return apiUserPostingComment.CreateFileComment(communityFile, fileComment, community);
	}
	
	/**
	 * Adds a comment with mentions to a community file
	 * 
	 * @param community - The Community instance of the community in which the file to be commented on is located
	 * @param communityFile - The FileEntry instance of the file to which the comment is to be posted
	 * @param mentions - The Mentions instance of the comment to be posted to the file
	 * @param userPostingComment - The User instance of the user commenting on the file
	 * @param apiUserPostingComment - The APIFileHandler instance of the user commenting on the file
	 * @return - The FileComment instance of the comment posted to the file
	 */
	public static FileComment addCommentWithMentions(Community community, FileEntry communityFile, Mentions mentions, User userPostingComment, APIFileHandler apiUserPostingComment) {
		
		log.info("INFO: " + userPostingComment + " will now post a comment with mentions to " + mentions.getUserToMention().getDisplayName() + " to the community file with file name: " + communityFile.getTitle());
		return apiUserPostingComment.addMentionFileCommentAPI(communityFile, community, mentions);
	}
	
	/**
	 * Updates the file name of an existing community file
	 * 
	 * @param community - The Community instance of the community in which the file is to be updated
	 * @param communityFile - The FileEntry instance of the community file to be updated
	 * @param newFileName - The new file name to which the files current file name will be updated
	 * @param userUpdatingFile - The User instance of the user who is to update the community file
	 * @param apiUserUpdatingFile - The APIFileHandler instance of the user who is updating the community file
	 * @return - The FileEntry instance of the updated community file
	 */
	public static FileEntry updateFileName(Community community, FileEntry communityFile, String newFileName, User userUpdatingFile, APIFileHandler apiUserUpdatingFile) {
		
		log.info("INFO: " + userUpdatingFile.getDisplayName() + " will now update the existing file with filename: " + communityFile.getTitle());
		return apiUserUpdatingFile.updateCommunityFile(communityFile, community, newFileName);
	}
	
	/**
	 * Uploads a new file version of an existing community file
	 * 
	 * @param community - The Community instance of the community in which the file is to be updated
	 * @param communityFile - The FileEntry instance of the community file to be updated
	 * @param baseFileNewVersion - The BaseFile instance of the new version of the file
	 * @param userUpdatingFile - The User instance of the user who is to update the community file
	 * @param apiUserUpdatingFile - The APIFileHandler instance of the user who is updating the community file
	 * @return - The FileEntry instance of the updated file
	 */
	public static FileEntry updateFileVersion(Community community, FileEntry communityFile, BaseFile baseFileNewVersion, User userUpdatingFile, APIFileHandler apiUserUpdatingFile) {
		
		log.info("INFO: Now creating the absolute path to the new version of the file");
		String filePathNewVersion = getResourcesDirAbsolutePath() + baseFileNewVersion.getName();
		log.info("INFO: The absolute path to the new version of the file has been created: " + filePathNewVersion);
		
		// Create the File instance of the new file version based on its filepath
		File newFile = new File(filePathNewVersion);
				
		log.info("INFO: " + userUpdatingFile.getDisplayName() + " will now upload a new version of the file with filename '" + communityFile.getTitle() + "' in the community with title: " + community.getTitle());
		FileEntry newFileVersion = apiUserUpdatingFile.uploadNewFileVersion(communityFile, newFile);
		
		log.info("INFO: Verify that the new file version was uploaded successfully");
		Assert.assertNotNull(newFileVersion, 
								"ERROR: The new file version was NOT uploaded successfully and was returned as null");
		return newFileVersion;
	}
	
	/**
	 * Updates a comment posted to a community file
	 * 
	 * @param community - The Community instance of the community in which the comment posted to the file is to be updated
	 * @param communityFile - The FileEntry instance of the community file to which the comment posted is to be updated
	 * @param existingFileComment - The FileComment instance of the comment which is to be updated
	 * @param updatedFileComment - The content of the updated comment
	 * @param userUpdatingComment - The User instance of the user updating the comment posted to the community file
	 * @param apiUserUpdatingComment - The APIFileHandler instance of the user updating the comment posted to the community file
	 * @return - The FileComment instance of the updated comment
	 */
	public static FileComment updateComment(Community community, FileEntry communityFile, FileComment existingFileComment, String updatedFileComment, User userUpdatingComment, APIFileHandler apiUserUpdatingComment) {
		
		log.info("INFO: " + userUpdatingComment.getDisplayName() + " will now update the existing comment on the community file with the new content: " + updatedFileComment);
		FileComment commentEdited = apiUserUpdatingComment.updateFileComment(existingFileComment, updatedFileComment);

		log.info("INFO: Verify that the file comment was edited successfully");
		Assert.assertTrue(commentEdited.getContent().trim().equals(updatedFileComment), 
							"ERROR: The file comment was NOT edited successfully and was returned with its original content still set");
		return commentEdited;
	}
	
	/**
	 * Shares a standalone file with a community
	 * 
	 * @param community - The Community instance of the community in which the file is to be shared
	 * @param fileToBeShared - The FileEntry instance of the file to be shared with the community
	 * @param role - The access rights of the community members to the file (Role.OWNER == Editor || Role.READER == Reader)
	 * @param userSharingFile - The User instance of the user sharing the file with the community
	 * @param filesAPIUser - The APIFileHandler instance of the user sharing the file with the community
	 */
	public static void shareFileWithCommunity(Community community, FileEntry fileToBeShared, Role role, User userSharingFile, APIFileHandler apiUserSharingFile) {
		
		log.info("INFO: " + userSharingFile.getDisplayName() + " will now share the file with title '" + fileToBeShared.getTitle() + " with the community with title: " + community.getTitle());
		Assert.assertTrue(apiUserSharingFile.shareFileWithCommunity(fileToBeShared, community, role), 
							"ERROR: The file could not be shared with the community with title: " + community.getTitle());
	}
	
	/**
	 * Follows a community file
	 * 
	 * @param community - The Community instance of the community in which the file is to be followed
	 * @param fileToBeFollowed - The FileEntry instance of the file to be followed
	 * @param userFollowingFile - The User instance of the user who will follow the file
	 * @param apiUserFollowingFile - The APIFileHandler instance of the user who will follow the file
	 */
	public static void followFile(Community community, FileEntry fileToBeFollowed, User userFollowingFile, APIFileHandler apiUserFollowingFile) {
		
		log.info("INFO: " + userFollowingFile.getDisplayName() + " will now follow the community file '" + fileToBeFollowed.getTitle() + "' in the community with title: " + community.getTitle());
		Assert.assertTrue(apiUserFollowingFile.followFile(fileToBeFollowed), 
							"ERROR: The file could not be followed in the community with title: " + community.getTitle());
	}
	
	/**
	 * Likes / Recommends a community file
	 * 
	 * @param community - The Community instance of the community in which the file is to be liked
	 * @param fileToBeLiked - The FileEntry instance of the file to be liked
	 * @param userLikingFile - The User instance of the user who is liking the file
	 * @param apiUserLikingFile - The APIFileHandler instance of the user who is liking the file
	 * @return - The FileEntry instance of the liked file
	 */
	public static FileEntry likeFile(Community community, FileEntry fileToBeLiked, User userLikingFile, APIFileHandler apiUserLikingFile) {
		
		log.info("INFO: " + userLikingFile.getDisplayName() + " will now like / recommend the community file '" + fileToBeLiked.getTitle() + "' in the community with title: " + community.getTitle());
		return apiUserLikingFile.likeCommunityFile(fileToBeLiked, community);
	}
	
	/**
	 * Adds a new file to the community and then updates the name of the file (in separate steps)
	 * 
	 * @param community - The Community instance of the community in which the file is to be both added and then updated
	 * @param baseFileTemplate - The BaseFile instance of the file to be added to the community
	 * @param newFileName - The new file name to which the files file name will be updated
	 * @param userUploadingFile - The User instance of the user who is to both add the file to the community and then update it
	 * @param apiUserUploadingFile - The APIFileHandler instance of the user who is both adding the file to the community and then updating it
	 * @return - The FileEntry instance of the added and updated community file
	 */
	public static FileEntry addAndUpdateFileName(Community community, BaseFile baseFileTemplate, String newFileName, User userUploadingFile, APIFileHandler apiUserUploadingFile) {
		
		// Add the file to the community
		FileEntry newCommunityFile = addFile(community, baseFileTemplate, userUploadingFile, apiUserUploadingFile);
		
		// Update the community file
		return updateFileName(community, newCommunityFile, newFileName, userUploadingFile, apiUserUploadingFile);
	}
	
	/**
	 * Adds a new file to the community and then updates the version of the file (in separate steps)
	 * 
	 * @param community - The Community instance of the community in which the file is to be both added and then updated
	 * @param baseFileTemplate - The BaseFile instance of the file to be added to the community
	 * @param baseFileNewVersion - The BaseFile instance of the new version of the file
	 * @param userUploadingFile - The User instance of the user who is to both add the file to the community and then update it
	 * @param apiUserUploadingFile - The APIFileHandler instance of the user who is both adding the file to the community and then updating it
	 * @return- The FileEntry instance of the added and updated community file
	 */
	public static FileEntry addAndUpdateFileVersion(Community community, BaseFile baseFileTemplate, BaseFile baseFileNewVersion, User userUploadingFile, APIFileHandler apiUserUploadingFile) {
		
		// Add the file to the community
		FileEntry newCommunityFile = addFile(community, baseFileTemplate, userUploadingFile, apiUserUploadingFile);
		
		// Update the version of the community file
		return updateFileVersion(community, newCommunityFile, baseFileNewVersion, userUploadingFile, apiUserUploadingFile);
	}
	
	/**
	 * Posts a comment to a community file and then updates that comment
	 * 
	 * @param community - The Community instance of the community in which the comment to be posted to the file will be created and updated
	 * @param communityFile - The FileEntry instance of the community file to which the comment will be posted to and then updated
	 * @param commentToBePosted - The comment content to be posted as a comment to the file (comment creation)
	 * @param updatedCommentToBePosted - The updated comment content to overwrite the existing comment content on the file (comment update) 
	 * @param userPostingComment - The User instance of the user creating and updating the comment on the community file
	 * @param apiUserPostingComment - The User instance of the user creating and updating the comment on the community file
	 * @return - The FileComment instance of the updated comment that was posted to the community file
	 */
	public static FileComment addCommentAndUpdateComment(Community community, FileEntry communityFile, String commentToBePosted, String updatedCommentToBePosted, User userPostingComment, APIFileHandler apiUserPostingComment) {
		
		// Add a comment to the community file
		FileComment fileComment = addComment(community, communityFile, commentToBePosted, userPostingComment, apiUserPostingComment);
		
		// Update the comment posted to the community file
		return updateComment(community, communityFile, fileComment, updatedCommentToBePosted, userPostingComment, apiUserPostingComment);
	}

	/**
	 * Adds a new file to the community and has another specified user follow the file
	 * 
	 * @param community - The Community instance of the community to which the file is to be added
	 * @param baseFileTemplate - The BaseFile instance of the file to be added to the community
	 * @param userAddingFile - The User instance of the user who is to add the file to the community
	 * @param apiUserAddingFile - The APIFileHandler instance of the user who is to add the file to the community
	 * @param userFollowingFile - The User instance of the user who will follow the file
	 * @param apiUserFollowingFile - The APIFileHandler instance of the user who will follow the file
	 * @return - The FileEntry instance of the newly added community file
	 */
	public static FileEntry addFileWithOneFollower(Community community, BaseFile baseFileTemplate, User userAddingFile, APIFileHandler apiUserAddingFile, User userFollowingFile, APIFileHandler apiUserFollowingFile) {
		
		// Add the file to the community
		FileEntry newCommunityFile = addFile(community, baseFileTemplate, userAddingFile, apiUserAddingFile);
		
		// Have the other user follow the file
		followFile(community, newCommunityFile, userFollowingFile, apiUserFollowingFile);
		
		return newCommunityFile;
	}
	
	/**
	 * Adds a folder to a community
	 * 
	 * @param community - The Community instance of the community to which the folder will be added
	 * @param baseFolder - The BaseFile instance of the folder to be added to the community
	 * @param userAddingFolder - The User instance of the user adding the folder
	 * @param apiUserAddingFolder - The APIFileHandler instance of the user adding the folder
	 * @return - The FileEntry instance of the folder
	 */
	public static FileEntry addFolder(Community community, BaseFile baseFolder, User userAddingFolder, APIFileHandler apiUserAddingFolder) {
		
		log.info("INFO: " + userAddingFolder.getDisplayName() + " will now add a folder to the community with title: " + baseFolder.getName());
		FileEntry communityFolder = apiUserAddingFolder.createCommunityFolder(community, baseFolder);
		
		log.info("INFO: Verify that the community folder was created successfully");
		Assert.assertNotNull(communityFolder, "ERROR: The community folder was NOT created and was returned as null");
		
		return communityFolder;
	}
	
	/**
	 * Adds a file to a community folder
	 * 
	 * @param userAddingFileToFolder - The User instance of the user who is adding the file to the folder
	 * @param apiUserAddingFile - The APIFileHandler instance of the user who is adding the file to the folder
	 * @param fileEntry - The FileEntry instance of the file which is to be added to the folder
	 * @param folderEntry - The FileEntry instance of the folder to which the file is to be added
	 * @return newFileEntry - A FileEntry object
	 */
	public static FileEntry addFileToFolder(User userAddingFileToFolder, APIFileHandler apiUserAddingFile, FileEntry fileEntry, FileEntry folderEntry) {
		
		// Add the specified file to the community folder
		return FileEvents.addFileToFolder(userAddingFileToFolder, apiUserAddingFile, fileEntry, folderEntry);
	}
	
	/**
	 * Removes a file from a folder
	 * 
	 * @param userRemovingFileFromFolder - The User instance of the user who is removing the file from the folder
	 * @param apiUserRemovingFile - The APIFileHandler instance of the user who is removing the file from the folder
	 * @param fileEntry - The FileEntry instance of the file which is to be removed from the folder
	 * @param folderEntry - The FileEntry instance of the folder from which the file is to be removed
	 */
	public static void removeFileFromFolder(User userRemovingFileFromFolder, APIFileHandler apiUserRemovingFile, FileEntry fileEntry, FileEntry folderEntry) {
		
		// Remove the specified file from the community folder
		FileEvents.removeFileFromFolder(userRemovingFileFromFolder, apiUserRemovingFile, fileEntry, folderEntry);
	}
	
	/**
	 * Logs in as the specified user and navigates to the community / files screen in Communities UI
	 * 
	 * @param community - The Community instance of the community to which the user will navigate
	 * @param baseCommunity - The BaseCommunity instance of the community to which the user will navigate
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToCommunityFiles(Community community, BaseCommunity baseCommunity, HomepageUI ui, CommunitiesUI uiCo, User userLoggingIn, 
															APICommunitiesHandler apiUserLoggingIn, boolean preserveInstance) {
		// Log into CommunitiesUI and navigate to the community
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Select 'Files' from the left nav menu
		selectFilesFromLeftNavigationMenu(uiCo);
	}
	
	/**
	 * Selects the "Files" option from the left-side navigation menu in Communities UI
	 * 
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	private static void selectFilesFromLeftNavigationMenu(CommunitiesUI uiCo) {
		
		log.info("INFO: Select 'Files' from the left navigation menu");
		Community_LeftNav_Menu.FILES.select(uiCo);
	}
}
