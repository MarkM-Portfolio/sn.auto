package com.ibm.conn.auto.lcapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class APIFileHandler extends APIHandler<FilesService> {
	
	private static final Logger log = LoggerFactory.getLogger(APIFileHandler.class);

	public APIFileHandler(String serverURL, String username, String password) {
		super("files", serverURL, username, password);
	}

	@Override
	protected FilesService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		log.info("INFO: Getting Files Service...");
		try {
			return new FilesService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Files service: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Creates a standalone file. This method fully supports public, shared and private files and
	 * automatically shares the file with any user specified (if required) at creation time
	 * 
	 * @param baseFile - The BaseFile instance of the file to be created
	 * @param file - The File instance of the file to be created
	 * @return - The FileEntry instance of the newly created file
	 */
	public FileEntry createStandaloneFile(BaseFile baseFile, File file) {
		
		log.info("INFO: Now creating a new standalone file with title: " + baseFile.getRename() + baseFile.getExtension());
		
		// Create the FileEntry instance of the file based on the BaseFile
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.setTitle(baseFile.getRename() + baseFile.getExtension());
		
		// Create the relevant tags for the file (supports a single or multiple tags)
		if(baseFile.getTags().indexOf(" ") == -1) {
			fileEntry.setTags(baseFile.getTags());
		} else {
			List<Category> listOfTags = new ArrayList<Category>();
			String baseFileTags = baseFile.getTags();
			
			while(baseFileTags.length() > 0) {
				// Retrieve the tag from the tags string
				int indexOfFirstSpace = baseFileTags.indexOf(" ");
				String currentTag;
				
				if(indexOfFirstSpace == -1) {
					// No spaces remaining in the tags string - set the last tag to be included
					currentTag = baseFileTags.trim();
					baseFileTags = "";
				} else {
					currentTag = baseFileTags.substring(0, indexOfFirstSpace).trim();
					baseFileTags = baseFileTags.substring(indexOfFirstSpace + 1);
				}
				// Convert the tag to the relevant Category instance
				Category tagAsCategory = Abdera.getNewFactory().newCategory();
				tagAsCategory.setTerm(currentTag);
				
				// Append the tag to the list
				listOfTags.add(tagAsCategory);
			}
			fileEntry.setTags(listOfTags);
		}
		
		// Set the permissions of the FileEntry, including any shared members if necessary
		if (baseFile.getShareLevel().equals(ShareLevel.EVERYONE)){
			log.info("INFO: A public file will be created");
			fileEntry.setPermission(Permissions.PUBLIC);
			
		} else if(baseFile.getShareLevel().equals(ShareLevel.COMMUNITIEIS) || baseFile.getShareLevel().equals(ShareLevel.PEOPLE)) {
			log.info("INFO: A shared file will be created and shared with the user with ID: " + baseFile.getSharedWith());
			fileEntry.setPermission(Permissions.SHARED);
			fileEntry.setShareWith(baseFile.getSharedWith());
			
		} else {
			log.info("INFO: A private file will be created");
			fileEntry.setPermission(Permissions.PRIVATE);
		}
		
		// Open an Input Stream and read in the file to be uploaded
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file.getAbsolutePath());
		} catch(FileNotFoundException fnfe) {
			log.info("ERROR: The file could not be found at the absolute file path: " + file.getAbsolutePath());
			fnfe.printStackTrace();
			inputStream = null;
		}
		
		if(inputStream == null) {
			log.info("ERROR: The FileEntry instance of the file could not be created");
			return null;
		}
		fileEntry.setInputStream(inputStream);
		log.info("INFO: The FileEntry instance of the file has been created successfully");
		
		Entry uploadResponse = (Entry) service.uploadNewFile(fileEntry);
		
		if(uploadResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The file was uploaded successfully");
			
			// Set the remaining parameters for the FileEntry before returning
			fileEntry.setId(uploadResponse.getId());
			fileEntry.setFile(file);
			fileEntry.setLinks(getFileLinks(uploadResponse));
			return fileEntry;
		} else {
			log.info("ERROR: There was an error with uploading the file with file name: " + fileEntry.getTitle());
			log.info(uploadResponse.toString());
			return null;
		}
	}
	
	public FileEntry CreateFile(BaseFile baseFile, File file){
		// create a file entry object to be used later
		// add necessary baseFile information to the fileEntry
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.setTitle(baseFile.getRename()+baseFile.getExtension());
		fileEntry.setTagsNoParsing(baseFile.getTags());
		
		//check to see if file is public
		if (baseFile.getShareLevel().equals(ShareLevel.EVERYONE)){
			fileEntry.setPermission(Permissions.PUBLIC);
		}

		try{
			// Open an input stream, read the file to be uploaded
			InputStream inputStream = new FileInputStream(file.getAbsolutePath());
			fileEntry.setInputStream(inputStream);
			
			// Upload the file
			Entry newEntry = (Entry)service.uploadFile(fileEntry);
			
			fileEntry.setInputStream(fileEntry.getInputStream());
			fileEntry.setId(newEntry.getId());
			fileEntry.setFile(file);
			fileEntry.setLinks(getFileLinks(newEntry));
			
			inputStream.close();
		}catch(FileNotFoundException e) {
	        log.info("ERROR: FILE NOT FOUND");
	        e.printStackTrace();
	    } catch (IOException e) {
	    	log.info("ERROR: IO EXCEPTION");
	        e.printStackTrace();
	    }
	   return fileEntry;
	}
	
	/**
	 * Retrieves the self, edit, alternate and replies links from a file entry in the correct format
	 * 
	 * @param fileEntry - The entry from which the files links are to be retrieved
	 * @return - A HashMap<String, Link> of all the files links in the correct format
	 */
	private HashMap<String, Link> getFileLinks(Entry fileEntry) {
		
		HashMap<String, Link> fileLinks = new HashMap<String, Link>();
		
		fileLinks.put(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL, fileEntry.getSelfLink());
		fileLinks.put(StringConstants.REL_ALTERNATE + ":" + StringConstants.MIME_TEXT_HTML, fileEntry.getAlternateLink());
		fileLinks.put(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML, fileEntry.getEditLink());
		fileLinks.put(StringConstants.REL_REPLIES + ":" + StringConstants.MIME_ATOM_XML, fileEntry.getLink(StringConstants.REL_REPLIES));
		
		return fileLinks;
	}
	
	public FileEntry CreateFile(BaseFile baseFile, File file, Community community){
		// create a file entry object to be used later
		// add necessary baseFile information to the fileEntry 
		FileEntry fileEntry = new FileEntry(file);
		fileEntry.setTitle(baseFile.getRename()+baseFile.getExtension());
		fileEntry.setTagsNoParsing(baseFile.getTags());
		
		try{
			// Open an input stream, read the file to be uploaded
			InputStream inputStream = new FileInputStream(file.getAbsolutePath());
			fileEntry.setInputStream(inputStream);
			
			// Upload the file
			Entry newEntry = (Entry) service.uploadFileToCommunity(fileEntry, community);
			
			fileEntry.setId(newEntry.getId());
			fileEntry.setLinks(getFileLinks(newEntry));
			
		}catch(FileNotFoundException e) {
	        log.info("ERROR: FILE NOT FOUND");
	        e.printStackTrace();
	    }
		
		return fileEntry;
	}

	public FileComment CreateFileComment(FileEntry fileEntry, FileComment fileComment) {
		Entry newEntry = (Entry)service.postFileComment( URLConstants.FILES_MY_DOCUMENT + 
				fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", fileComment);
		fileComment.setId(newEntry.getId());
		fileComment.setLinks(getFileLinks(newEntry));
		return fileComment;
	}

	public FileComment CreateFileComment(FileEntry fileEntry, FileComment fileComment, Community community) {
		Entry newEntry = (Entry)service.postFileComment( URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid()+ 
				"/document/" + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", fileComment);
		fileComment.setId(newEntry.getId());
		fileComment.setLinks(getFileLinks(newEntry));
		return fileComment;
	}
	
	/**
	 * 
	 * @param fileEntry - The fileEntry of the file
	 * @param fileComment - The fileComment object which will be added to the file
	 * @param userID - The userID of the file owner.  This can be obtained by creating a APIProfilesHandler object
	 * and using its getUUID() method
	 * @return fileComment - The FileComment object with an updated ID
	 */
	public FileComment CreateFileComment_OtherUser(FileEntry fileEntry, FileComment fileComment, String userID){

		Entry newEntry = (Entry)service.postFileComment( URLConstants.FILES_COMMENTS_ACCESS + "/" + userID + "/document/" + 
				fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", fileComment);
		fileComment.setId(newEntry.getId());
		fileComment.setLinks(getFileLinks(newEntry));
		return fileComment;
	}
	
	/**
	 * Posts a comment to the specified file
	 * 
	 * @param fileEntry - The FileEntry instance of the file to which the comment will be posted
	 * @param fileComment - The FileComment instance of the comment to be posted to the file
	 * @return - The FileComment instance of the comment if all operations are successful, null otherwise
	 */
	public FileComment createFileComment_AnyUser(FileEntry fileEntry, FileComment fileComment) {
		
		// Create the URL to POST the comment request to
		String postRequestURL = fileEntry.getRepliesLink();
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		// Create the Entry to be POSTed to the URL
		Entry fileCommentEntry = fileComment.toEntry();
		log.info("INFO: The Entry instance of the file comment has been created: " + fileCommentEntry.toString());
		
		Entry postResponse = (Entry) service.postUrlFeed(postRequestURL, fileCommentEntry);
		
		if(postResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The file comment was successfully posted to the file with filename: " + fileEntry.getTitle());
			fileComment.setId(postResponse.getId());
			fileComment.setLinks(getFileLinks(postResponse));
			return fileComment;
		} else {
			log.info("ERROR: The file comment was NOT posted to the file with filename: " + fileEntry.getTitle());
			log.info(postResponse.toString());
			return null;
		}
	}
	
	/**
	 * Updates a comment posted to a file
	 * 
	 * @param fileComment - The FileComment instance of the comment to be updated
	 * @param updatedFileCommentContent - The String content of the new comment content to be set
	 * @return - The updated FileComment instance if all operations are successful, the unchanged instance is returned otherwise
	 */
	public FileComment updateFileComment(FileComment fileComment, String updatedFileCommentContent) {
		
		// Store the old comment content in case of failure and set the new content
		String oldCommentContent = fileComment.getContent().trim();
		fileComment.setContent(updatedFileCommentContent);
		log.info("INFO: The Entry instance of the updated comment has been created: " + fileComment.toEntry().toString());
		
		// Create the URL to PUT the update request to
		String putRequestURL = fileComment.getEditLink();
		log.info("INFO: The URL to PUT the update request to has been retrieved: " + putRequestURL);
		
		int putResponse = service.putResponse(putRequestURL, fileComment.toEntry()).getStatus();
		
		if(putResponse >= 200 && putResponse <= 204) {
			log.info("INFO: The file comment has been updated successfully");
		} else {
			log.info("ERROR: The file comment could NOT be updated");
			fileComment.setContent(oldCommentContent);
		}
		return fileComment;
	}

	public FileComment updateCommunityFileComment(Community community, FileEntry fileEntry, FileComment fileComment){
		Entry newEntry = (Entry)service.updateCommunityFileComment(fileComment.toEntry(),community.getUuid(),fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1] , 
				fileComment.getId().toString().split("urn:lsid:ibm.com:td:")[1]);
		fileComment.setId(newEntry.getId());
		return fileComment;
	} 
	
	public FileEntry updateFile(FileEntry fileEntry, String newFileName) {
		 service.updateFileWithNameChange(fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1], 
					newFileName);
	    return fileEntry;
	}
	
	/**
	 * Updates a file by changing only its filename. Does not use an InputStream akin updateFileWithNameChange.
	 * 
	 * @param fileEntry - The file whose name is to be changed.
	 * @param newFileName - The new file name to replace the old file name.
	 * @return - An updated FileEntry object with new filename attached.
	 */
	public FileEntry updateFileNameOnly(FileEntry fileEntry, String newFileName) {
		fileEntry.setTitle(newFileName);
		service.updateCommunityFileNoInputStream(fileEntry.getEditLink(), fileEntry);
		return fileEntry;
	}
	
	public FileEntry updateCommunityFile(FileEntry fileEntry, Community community, String newFileName) {
		fileEntry.setTitle(newFileName);
		service.updateCommunityFileNoInputStream(service.getURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid() + "/document/" + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1] + "/entry?label=" + newFileName + "&title=" + newFileName, fileEntry);
	    return fileEntry;
	}
	
	public FileEntry changePermissions(BaseFile baseFile, FileEntry fileEntry) {
		if (baseFile.getShareLevel() == ShareLevel.EVERYONE){
			fileEntry.setPermission(Permissions.PUBLIC);
			ClientResponse response =service.postResponse(service.getURLString() + URLConstants.FILES_VERSION + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1] + "/feed?visibility=public", "share");
			if (response != null)
				response.release();
		}
		if (baseFile.getShareLevel() == ShareLevel.PEOPLE){
			fileEntry.setSharePermission(SharePermission.EDIT);
			fileEntry.setShareWith(baseFile.getSharedWith());
			fileEntry.setSharedWhat(fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]);
			service.createFileShare(fileEntry);
		}
	return fileEntry;
	}
	

	public void removeFileFromFolder(FileEntry fileEntry, FileEntry folderEntry){
		service.removeFileFromFolder(folderEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1], fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]);
	}
	
	public FileEntry createFolder(FileEntry folderEntry) {
		Entry newEntry = (Entry)service.createFolder(folderEntry);
		folderEntry.setId(newEntry.getId());
		folderEntry.setLinks(getFileLinks(newEntry));
		return folderEntry;
	}
	
	/**
	 * Creates a new standalone folder - this API method also supports sharing the folder with other users
	 * 
	 * @param baseFolder - The BaseFile instance of the folder to be created
	 * @param role - The Role instance of the shared users access to the folder (ALL, OWNER, READER)
	 * @return - The FileEntry of the new folder instance if the create operation is successful, null otherwise
	 */
	public FileEntry createFolder(BaseFile baseFolder, StringConstants.Role role) {
		
		log.info("INFO: Attempting to create a new folder with title: " + baseFolder.getName());
		
		// Create the entry to represent the folder
		Entry folderEntry = createFolderEntry(baseFolder.getName().trim());
		
		// Set whether this folder is a public or private folder
		Element visibility = Abdera.getNewFactory().newElement(new QName("urn:ibm.com/td", "visibility"));
		if(baseFolder.getShareLevel() == ShareLevel.EVERYONE) {
			visibility.setText("public");
		} else {
			visibility.setText("private");
		}
		folderEntry.addExtension(visibility);
		
		// Add shared users and their access rights to the folder if necessary
		ExtensibleElement sharedWith = Abdera.getNewFactory().newElement(new QName("urn:ibm.com/td", "sharedWith"));
		if(baseFolder.getSharedWith() != null) {
			Element member = Abdera.getNewFactory().newElement(new QName("http://www.ibm.com/xmlns/prod/composite-applications/v1.0", "member"));
			member.setAttributeValue("xmlns:ca", "http://www.ibm.com/xmlns/prod/composite-applications/v1.0");
			member.setAttributeValue("ca:id", "" + baseFolder.getSharedWith());
			member.setAttributeValue("ca:type", "user");
			
			if(role == Role.ALL) {
				member.setAttributeValue("ca:role", "contributor");
			} else if(role == Role.OWNER) {
				member.setAttributeValue("ca:role", "manager");
			} else {
				member.setAttributeValue("ca:role", "reader");
			}
			sharedWith.addExtension(member);
		}
		folderEntry.addExtension(sharedWith);
		log.info("INFO: The new folder entry has been created: " + folderEntry.toString());
		
		String createURL = service.getServiceURLString() + URLConstants.FILES_FOLDERS;
		log.info("INFO: The URL to POST the folder entry to has been created: " + createURL);
		
		Entry createResponse = (Entry) service.postUrlFeed(createURL, folderEntry);
		
		if(createResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The folder with title '" + baseFolder.getName() + "' was successfully created");
			return createFolderFileEntryInstance(createResponse, baseFolder);
		} else {
			log.info("ERROR: There was a problem with creating the new folder");
			log.info(createResponse.toString());
			return null;
		}
	}
	
	/**
	 * Creates the commonly used attributes of the Entry instance which represents a new folder to be created
	 * Private access for now since there is no current requirement to use this method externally
	 * 
	 * @param folderTitle - The name of the folder to be created
	 * @return - The Entry instance of the new folder to be created
	 */
	private Entry createFolderEntry(String folderTitle) {
		
		log.info("INFO: Creating the entry to represent the folder with title: " + folderTitle);
		
		// Create the entry to represent the folder
		Entry folderEntry = Abdera.getNewFactory().newEntry();
		folderEntry.addCategory(StringConstants.SCHEME_TD_TYPE, "collection", "collection");
		
		Element label = Abdera.getNewFactory().newElement(new QName("urn:ibm.com/td", "label"));
		label.setAttributeValue("makeUnique", "true");
		label.setText(folderTitle);
		folderEntry.addExtension(label);
		
		folderEntry.setTitle(folderTitle);
		folderEntry.setSummary("");
		
		return folderEntry;
	}
	
	/**
	 * Creates a FileEntry instance of the created folder based on the Entry returned from creating the folder and the initial BaseFile instance of the folder
	 * Private access for now since there is no requirement to use this method externally
	 * 
	 * @param createEntry - The Entry instance generated from creating the folder
	 * @param baseFolder - The BaseFile instance of the folder that was created
	 * @return - The FileEntry instance of the created folder
	 */
	private FileEntry createFolderFileEntryInstance(Entry createEntry, BaseFile baseFolder) {
		
		log.info("INFO: Creating the FileEntry instance of the folder that has been created");
		
		// Set the base FileEntry properties
		FileEntry folder = new FileEntry(null);
		folder.setTitle(baseFolder.getName().trim());
		folder.setContent(baseFolder.getName().trim());
		
		if(baseFolder.getShareLevel() == ShareLevel.EVERYONE) {
			folder.setPermission(Permissions.PUBLIC);
		} else {
			folder.setPermission(Permissions.PRIVATE);
		}
		folder.setNotification(true);
		folder.setCommentNotification(Notification.ON);
		folder.setMediaNotification(Notification.ON);
		folder.setIncludePath(true);
		folder.setPropagate(true);
		folder.setSharePermission(SharePermission.EDIT);
		folder.setShareSummary("shareSummary");
		folder.setShareWith(baseFolder.getSharedWith());
		
		// Add the ID and Links from the Entry to the folder
		folder.setId(createEntry.getId());
		folder.setLinks(getFileLinks(createEntry));
		
		return folder;
	}
	
	/**
	 * Creates a folder in any community
	 * 
	 * @param community - The Community in which the folder is to be created
	 * @param baseFolder - The BaseFile instance of the folder to be created
	 * @return - The FileEntry instance of the newly created folder if the operation is successful, null otherwise
	 */
	public FileEntry createCommunityFolder(Community community, BaseFile baseFolder) {
		
		log.info("INFO: Attempting to create a new folder in the community with title: " + community.getTitle());
		
		Entry folderEntry = createFolderEntry(baseFolder.getName().trim());
		log.info("INFO: The new folder entry has been created: " + folderEntry.toString());
		
		// Retrieve the collection feed for this community and, from that, retrieve the self link
		Feed collectionFeed = (Feed) service.getCommunityCollectionFeed(community.getUuid());
		String collectionSelfLink = collectionFeed.getSelfLink().getHref().toString();
		if(collectionSelfLink == null || collectionSelfLink.equals("null")) {
			log.info("ERROR: The self link from the collection feed for this community could not be retrieved");
			log.info(collectionFeed.toString());
			return null;
		}
		log.info("INFO: The URL to POST the create community folder request to has been retrieved: " + collectionSelfLink);
		
		Entry createResponse = (Entry) service.postUrlFeed(collectionSelfLink, folderEntry);
		
		if(createResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The community folder with title '" + baseFolder.getName() + "' was successfully created");
			return createFolderFileEntryInstance(createResponse, baseFolder);
		} else {
			log.info("ERROR: There was a problem with creating the new community folder");
			log.info(createResponse.toString());
			return null;
		}
	}
	
	public FileEntry createCommunityFolder(FileEntry folderEntry, Community community) {
		Entry newEntry = (Entry)service.createCommunityFolder(folderEntry, community.getUuid());
		folderEntry.setId(newEntry.getId());
		folderEntry.setLinks(getFileLinks(newEntry));
		return folderEntry;
	}

	public FileEntry addFilestoFolder(FileEntry folderEntry, ArrayList<String> filesList){
		service.addFilesToFolder(folderEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1], filesList);
		return folderEntry;
	}
	
	/**
	 * Likes / recommends the specified file as the owner / creator of the file
	 * 
	 * @param fileEntry - The FileEntry instance of the file to be liked / recommended
	 * @return - The URL corresponding to the like request if all operations are successful, null otherwise
	 */
	public String likeFile(FileEntry fileEntry) {
		
		// Create the Entry instance to be used to recommend / like the file
		Entry recommendationEntry = Abdera.getNewFactory().newEntry();
		recommendationEntry.addCategory("tag:ibm.com,2006:td/type", "recommendation", "recommendation");
		log.info("INFO: The Entry instance to be used to like / recommend the file has been created: " + recommendationEntry.toString());
		
		// Create the URL to which the Entry will be POSTed
		String postRequestURL = service.getURLString() + URLConstants.FILES_MY_DOCUMENT  + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1] + "/feed?category=recommendation";
		log.info("INFO: The URL to which the Entry will be POSTed has been created: " + postRequestURL);
		
		Entry likedFileEntry = (Entry) service.postRecommendationToFile(postRequestURL, recommendationEntry);
		
		if(likedFileEntry.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The file was successfully liked / recommended");
			return likedFileEntry.getSelfLink().getHref().toString();
		} else {
			log.info("ERROR: The file could NOT be liked / recommended");
			log.info(likedFileEntry.toString());
			return null;
		}
	}
	
	/**
	 * Unlikes the specified file
	 * 
	 * @param likeFileURL - The String instance of URL from the like file request (obtained via either of the likeFile() API methods)
	 * @return - True if the file is unliked successfully, false if the unlike operation is unsuccessful
	 */
	public boolean unlikeFile(String likeFileURL) {
		
		/**
		 * PLEASE NOTE: The use of the deleteCMISFile() method is the only way that a DELETE request could be sent to a URL
		 * 				All other delete() methods required additional parameters (which in this case is unnecessary)
		 */
		boolean unlikedFile = service.deleteCMISFile(likeFileURL);
		
		if(unlikedFile == true) {
			log.info("INFO: The file was unliked successfully");
		} else {
			log.info("ERROR: The file could NOT be unliked");
		}
		return unlikedFile;
	}

	/**
	 * Likes / recommends the specified file as another user (ie. NOT the owner / creator of the file)
	 * 
	 * @param fileEntry - The FileEntry instance of the file to be liked / recommended
	 * @return - The URL corresponding to the like request if all operations are successful, null otherwise
	 */
	public String likeFile_OtherUser(FileEntry fileEntry){
		
		// Create the Entry instance to be used to recommend / like the file
		Entry recommendationEntry = Abdera.getNewFactory().newEntry();
		recommendationEntry.addCategory("tag:ibm.com,2006:td/type", "recommendation", "recommendation");
		log.info("INFO: The Entry instance to be used to like / recommend the file has been created: " + recommendationEntry.toString());
		
		// Create the URL to which the Entry will be POSTed
		String postRequestURL = service.getURLString() + URLConstants.FILES_DOCUMENT  + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1] + "/feed?category=recommendation";
		log.info("INFO: The URL to which the Entry will be POSTed has been created: " + postRequestURL);
		
		Entry likedFileEntry = (Entry) service.postRecommendationToFile(postRequestURL, recommendationEntry);
		
		if(likedFileEntry.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The file was successfully liked / recommended");
			return likedFileEntry.getSelfLink().getHref().toString();
		} else {
			log.info("ERROR: The file could NOT be liked / recommended");
			log.info(likedFileEntry.toString());
			return null;
		}
	}

	public FileEntry likeCommunityFile(FileEntry fileEntry, Community community){
		Entry recommendationEntry = Abdera.getNewFactory().newEntry();
		recommendationEntry.addCategory("tag:ibm.com,2006:td/type", "recommendation", "recommendation");
		service.postRecommendationToFile(service.getURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid() + "/document/"  + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1] + "/feed" , recommendationEntry);
		return fileEntry;
	}
	
	public FileEntry pinFile(FileEntry fileEntry){
		ExtensibleElement bogusFileElement = service.getCollectionsContainingFile(fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]);
		service.pinningFile(fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1], bogusFileElement );
		return fileEntry;
	}
	
	public FileEntry pinFolder(FileEntry folderEntry){
		ExtensibleElement bogusFileElement = service.getFilesInFolderFeed(folderEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]);
		service.pinningFolder(folderEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1], bogusFileElement );
		return folderEntry;
	}
	
	/**
	 * This method is deprecated in favour of public FileComment addMentionFileCommentAPI(FileEntry fileEntry, Mentions mentions) - see below
	 * @param profile
	 * @param fileEntry
	 * @return
	 */
	@Deprecated
	public FileComment addMentionFileComment(APIProfilesHandler profile, FileEntry fileEntry){
		FileComment newComment = new FileComment("");
		Entry commentEntry = newComment.toEntry();
		commentEntry.setContentAsHtml("<span class=\"vcard\"><span class=\"fn\">@" + profile.getDesplayName() + "</span><span class=\"x-lconn-userid\">"+ profile.getUUID() +"</span></span>");
		
		Entry newEntry = (Entry)service.postRecommendationToFile(service.getURLString() + URLConstants.FILES_MY_DOCUMENT + 
				fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", commentEntry);
		
		newComment.setId(newEntry.getId());
		
		log.info("INFO: Created wiki comment ");
		return newComment;
		
	}
	
	/**
	 * 
	 * @param fileEntry - The entry for the file to which the comment with the mention will be added
	 * @param mentions - The Mentions object which contains details of the user who will be mentioned in the file comment
	 * @return newComment - A FileComment object
	 */
	public FileComment addMentionFileCommentAPI(FileEntry fileEntry, Mentions mentions){
		FileComment newComment = new FileComment("");
		Entry commentEntry = newComment.toEntry();
		commentEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class=\"vcard\"><span class=\"fn\">@" + mentions.getUserToMention().getDisplayName() + "</span><span class=\"x-lconn-userid\">" + mentions.getUserUUID() +"</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		Entry newEntry = (Entry)service.postRecommendationToFile(service.getURLString() + URLConstants.FILES_MY_DOCUMENT + 
				fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", commentEntry);
		
		newComment.setId(newEntry.getId());
		
		log.info("INFO: Created file comment ");
		return newComment;
		
	}
	
	/**
	 * 
	 * @param fileEntry - The FileEntry object for the file which will be commented on
	 * @param community - The community to which the file will be added
	 * @param mentions - A Mentions object which contains information about the user to be mentioned
	 * @return newComment - A FileComment object
	 */
	public FileComment addMentionFileCommentAPI(FileEntry fileEntry, Community community, Mentions mentions){
		FileComment newComment = new FileComment("");
		Entry commentEntry = newComment.toEntry();
		commentEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class=\"vcard\"><span class=\"fn\">@" + mentions.getUserToMention().getDisplayName() + "</span><span class=\"x-lconn-userid\">" + mentions.getUserUUID() +"</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		Entry newEntry = (Entry)service.postRecommendationToFile(service.getURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid()+ 
				"/document/" + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", commentEntry);
		
		newComment.setId(newEntry.getId());
		
		log.info("INFO: Created community file comment");
		return newComment;
		
	}
	
	/**
	 * This method has been deprecated in favour of public FileComment addMentionFileCommentAPI(FileEntry fileEntry, Community community, Mentions mentions) see above
	 * @param profile
	 * @param fileEntry
	 * @param community
	 * @return
	 */
	@Deprecated
	public FileComment addMentionFileComment(APIProfilesHandler profile, FileEntry fileEntry, Community community){
		FileComment newComment = new FileComment("");
		Entry commentEntry = newComment.toEntry();
		commentEntry.setContentAsHtml("<span class=\"vcard\"><span class=\"fn\">@" + profile.getDesplayName() + "</span><span class=\"x-lconn-userid\">"+ profile.getUUID() +"</span></span>");
		
		Entry newEntry = (Entry)service.postRecommendationToFile(service.getURLString() + URLConstants.FILES_COMMUNITY_LIBRARY + community.getUuid()+ 
				"/document/" + fileEntry.getId().toString().split("urn:lsid:ibm.com:td:")[1]+ "/feed?category=comment", commentEntry);
		
		newComment.setId(newEntry.getId());
		
		log.info("INFO: Created wiki comment ");
		return newComment;
		
	}
	
	/**
	 * Deletes a comment posted to a file
	 * 
	 * @param fileComment - The FileComment instance of the comment to be deleted
	 */
	public boolean deleteFileComment(FileComment fileComment){
		
		log.info("INFO: Now deleting the file comment with content: " + fileComment.getContent().trim());
		
		/**
		 * PLEASE NOTE: For this request, we needed to call a method which invoked deleteFeed() while ONLY accepting a URL as a parameter.
		 * 				deleteCMISFile() was the only method in FilesService matching these credentials and is therefore used for this process.
		 */
		return service.deleteCMISFile(fileComment.getEditLink());
	}
	
	/**
	 * <ul>
	 * <li><B>Purpose:</B>Share a File with another user through the API</li>
	 * <li><B>Parameters</B>FileEntry object represents a file uploaded to the connections server. See FVT_Discover_PublicFile. UserID is the ID of the user you wish to share the file with. to get ID , create APIPorfilesHandler object with the user and use getUUID() method.See FVT_General_StatusUpdates().<li>
	 * </ul>
	 * @param fileEntry the file that is to be shared
	 * @param UserID. ID of user that the file is to be shared with
	 */
	public boolean shareFile(FileEntry fileEntry,String UserID){

		Element shareWhat = Abdera.getNewFactory().newElement(StringConstants.SHARED_WHAT);
		shareWhat.setText(fileEntry.getId().toString().replace("urn:lsid:ibm.com:td:", ""));

		Element shareWith = Abdera.getNewFactory().newElement(StringConstants.SHARED_WITH);

		Element sharePermission = Abdera.getNewFactory().newElement(StringConstants.SHARE_PERMISSION,shareWith);
		sharePermission.setText("View");

		Element user = Abdera.getNewFactory().newElement(StringConstants.USER,shareWith);

		Element userID = Abdera.getNewFactory().newElement(StringConstants.USERID,user);
		userID.setText(UserID);

		Element sharePermission2 = Abdera.getNewFactory().newElement(StringConstants.SHARE_PERMISSION);
		sharePermission2.setText("View");

		Entry newEntry = Abdera.getNewFactory().newEntry();
		newEntry.addCategory("tag:ibm.com,2006:td/type", "share", "");
		newEntry.addExtension(shareWhat);
		newEntry.addExtension(shareWith);

		newEntry.addExtension(sharePermission2);

		ExtensibleElement element = service.shareFileWithCommunity(fileEntry.getId().toString().replace("urn:lsid:ibm.com:td:", ""), newEntry);
		
		boolean shared = false;
		
		if(element != null){
			shared = true;
		}
		
		return shared;

	}
	
	/**
	 * Shares a file with a community
	 * 
	 * @param fileToBeShared - The FileEntry instance of the file to be shared with the community
	 * @param community - The Community instance of the community in which the file is to be shared
	 * @param role - Role.OWNER specifies Editor access rights for the file, Role.READER specifies Reader access rights
	 * @return - True if the share operation is successful, false otherwise
	 */
	public boolean shareFileWithCommunity(FileEntry fileToBeShared, Community community, Role role) {
		
		log.info("INFO: Attempting to share the file with filename '" + fileToBeShared.getTitle() + "' with the community with title: " + community.getTitle());
		
		// Create the Feed which is to be used to share this file with the community
		Feed shareFeed = Abdera.getNewFactory().newFeed();
		Entry shareEntry = Abdera.getNewFactory().newEntry();
		
		shareEntry.addCategory("tag:ibm.com,2006:td/type", "community", "community");
		if(role.equals(Role.OWNER)) {
			Element sharePermission = shareEntry.addExtension("urn:ibm.com/td", "sharePermission", null);
			sharePermission.setText("Edit");
		}
		Element itemId = shareEntry.addExtension("urn:ibm.com/td", "itemId", null);
		itemId.setText(community.getUuid());
		
		shareFeed.addEntry(shareEntry);
		log.info("INFO: The Feed used to share this file with the community has been created: " + shareFeed.toString());
		
		// Create the URL to POST the feed to
		String postFeedURL = fileToBeShared.getRepliesLink();
		log.info("INFO: The URL to POST the Feed to has been created: " + postFeedURL);
		
		Entry shareResponse = (Entry) service.postUrlFeed(postFeedURL, shareFeed);
		
		if(shareResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The file was successfully shared with the community");
			return true;
		} else {
			log.info("ERROR: The file could not be shared with the community");
			log.info(shareResponse.toString());
			return false;
		}
	}
	
	/**
	 * Method to flag one community file with Moderation enabled 
	 * @param fileEntry
	 */
	public boolean flagCommunityFile(FileEntry fileEntry){
		log.info("INFO: Flag one community file");
		String fileID = fileEntry.getId().toString();
		fileID = fileID.substring(fileID.lastIndexOf(":")+1);
		
		boolean flag = false;
		try {
			service.FlagCommunityFile(fileID);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * Method to flag one comment of community file with moderation enabled
	 * @param fileComment
	 */
	public boolean flagCommunityFileComment(FileComment fileComment){
		log.info("INFO: Flag one community file comment");
		String commentID = fileComment.getId().toString();
		commentID = commentID.substring(commentID.lastIndexOf(":")+1);
		
		boolean flag = false;
		try {
			service.FlagCommunityFileComment(commentID);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * Deletes a standalone or community file (used as part of clean-up after test has completed)
	 * 
	 * @param fileEntry - The file to be deleted
	 * @return - Returns true if the deletion was successful, false otherwise
	 */
	public boolean deleteFile(FileEntry fileEntry) {
		
		log.info("INFO: Deleting the file with title: " + fileEntry.getTitle());
		int deleteResponse = service.deleteWithResponse(fileEntry.getEditLink()).getStatus();
		
		if(deleteResponse >= 200 && deleteResponse <= 204) {
			log.info("INFO: The file was successfully deleted");
			return true;
		} else {
			log.info("ERROR: The file could not be deleted");
			return false;
		}
	}
	
	/**
	 * Follows any file - works for both standalone and community files
	 * 
	 * @param file - The file to be followed
	 * @return - True if the follow operation is successful, false otherwise
	 */
	public boolean followFile(FileEntry file) {
		
		log.info("INFO: Attempting to follow the file with title: " + file.getTitle());
		
		// Create the URL to process the follow file request
		String followFileURL = file.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been retrieved: " + followFileURL);
		
		// Create the entry which will be used to follow the file
		Entry followFileEntry = Abdera.getNewFactory().newEntry();
		ExtensibleElement notifications = followFileEntry.addExtension("urn:ibm.com/td", "notifications", "");
		Element media = notifications.addExtension("urn:ibm.com/td", "media", "");
		media.setText("on");
		Element comment = notifications.addExtension("urn:ibm.com/td", "comment", "");
		comment.setText("on");
		log.info("INFO: The entry to follow the file has been created: " + followFileEntry.toString());
		
		int responseCode = service.putResponse(followFileURL, followFileEntry).getStatus();
		
		if(responseCode >= 200 && responseCode <= 204) {
			log.info("INFO: The file was followed successfully");
			return true;
		} else {
			log.info("ERROR: The file could not be followed");
			return false;
		}
	}
	
	/**
	 * Follows a folder - ONLY works for standalone folders and DOES NOT work for community folders
	 * 
	 * @param file - The FileEntry instance of the folder to be followed
	 * @return - True if the follow folder operation is successful, false otherwise
	 */
	public boolean followFolder(FileEntry folder) {
		
		log.info("INFO: Attempting to follow the folder with title: " + folder.getTitle());
		
		// Create the URL to process the follow folder request
		String followFolderURL = folder.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been retrieved: " + followFolderURL);
		
		// Create the entry which will be used to follow the folder
		Entry followFolderEntry = Abdera.getNewFactory().newEntry();
		ExtensibleElement notifications = followFolderEntry.addExtension("urn:ibm.com/td", "notifications", "");
		Element filesAdded = notifications.addExtension("urn:ibm.com/td", "filesAdded", "");
		filesAdded.setText("on");
		log.info("INFO: The entry to follow the folder has been created: " + followFolderEntry.toString());
		
		int responseCode = service.putResponse(followFolderURL, followFolderEntry).getStatus();
		
		if(responseCode >= 200 && responseCode <= 204) {
			log.info("INFO: The folder was followed successfully");
			return true;
		} else {
			log.info("ERROR: The folder could not be followed");
			return false;
		}
	}
	
	/**
	 * Deletes a standalone or community folder
	 * 
	 * @param folder - The folder to be deleted
	 * @return - Returns true if the deletion was successful, false otherwise
	 */
	public boolean deleteFolder(FileEntry folder) {
		return service.deleteFileFolder(folder.getId().toString().substring(20));
	}
	
	/**
	 * Uploads a new version of an existing file
	 * 
	 * @param currentFileEntry - The FileEntry instance of the currently uploaded file
	 * @param newFile - The File instance of the new file version to be uploaded
	 * @return - The FileEntry instance of the new file version if the operation is successful, null otherwise
	 */
	public FileEntry uploadNewFileVersion(FileEntry currentFileEntry, File newFile) {
		
		log.info("INFO: Now uploading a new version of the file with file name: " + currentFileEntry.getTitle());
		
		// Retrieve the update nonce which is needed for the update operation to go through correctly
		Entry nonceResponse = (Entry) service.getUrlFeed(service.getServiceURLString() + "/basic/api/nonce");
		log.info("INFO: The nonce response entry has been retrieved: " + nonceResponse.toString());
		
		// Retrieve the nonce value from the nonce response
		String nonceResponseString = nonceResponse.toString().toLowerCase();
		String nonceValue = nonceResponseString.substring(nonceResponseString.indexOf("<header:x-update-nonce>") + 23, 
															nonceResponseString.indexOf("</header:x-update-nonce>"));
		log.info("INFO: The nonce value has been retrieved: " + nonceValue);
		
		// Open an Input Stream and read in the new file version to be uploaded
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(newFile.getAbsolutePath());
		} catch(FileNotFoundException fnfe) {
			log.info("ERROR: The new file version could not be found at the absolute file path: " + newFile.getAbsolutePath());
			fnfe.printStackTrace();
			inputStream = null;
		}
		
		if(inputStream == null) {
			log.info("ERROR: The FileEntry instance of the new file version could not be created");
			return null;
		}
		log.info("INFO: The input stream for the new file version has been successfully created");
		
		// Create the URL to PUT the new file version to
		String newVersionURL = currentFileEntry.getEditLink() + "?nonce=" + nonceValue;
		log.info("INFO: The URL to which the PUT request will be sent has been created: " + newVersionURL);
		
		Entry updateResponse = (Entry) service.putFeed(newVersionURL, inputStream);
		
		if(updateResponse.toString().indexOf("resp:error=\"true\"") == -1) {
			log.info("INFO: The new version of the file was successfully uploaded");
			currentFileEntry.setInputStream(inputStream);
			return currentFileEntry;
		} else {
			log.info("ERROR: The new version of the file could not be uploaded");
			return null;
		}
	}
}
