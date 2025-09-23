package com.ibm.conn.auto.tests.homepage.fvt.orientme.configs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.data.JSONData;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.objects.TestCaseData.Key;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.objects.TestCaseData.Value;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Person;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	22nd February 2017
 */

public abstract class DataPopCleanUp extends DataPopSetup {
	
	private HashMap<String, String> mapOfBoardMessages;
	private HashMap<Invitation, String> mapOfInvitations;
	private int jsonIndex;
	private List<Community> listOfCommunities;
	private List<FileEntry> listOfFiles;
	private List<User> listOfUsers;
	private String jsonContent;
	
	@BeforeClass(alwaysRun=true)
	@Override
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);
		
		setFilename(null);
		setJsonIndex(0);
		setJsonContent("");
		setListOfCommunities(new ArrayList<Community>());
		setListOfFiles(new ArrayList<FileEntry>());
		setListOfUsers(new ArrayList<User>());
		setMapOfBoardMessages(new HashMap<String, String>());
		setMapOfInvitations(new HashMap<Invitation, String>());
	}
	
	@AfterClass(alwaysRun = true)
	@Override
	public void afterClass() {
		
		// Delete the JS file created during the data population process
		deleteTestCaseDataJSFile();
	}
	
	/**
	 * Adds the specified community to the 'listOfCommunities' attribute
	 * 
	 * @param communityInstance - The Community instance to be added to the 'listOfCommunities' attribute
	 */
	private void addCommunityToListOfCommunities(Community communityInstance) {
		listOfCommunities.add(communityInstance);
	}
	
	/**
	 * Adds the specified board message ID and author data to the 'mapOfBoardMessages' attribute
	 * 
	 * @param boardMessageId - The String content of the board message ID to be added to the 'mapOfBoardMessages' attribute
	 * @param messageAuthor - The String content of the author of the board message
	 */
	private void addDataToMapOfBoardMessages(String boardMessageId, String messageAuthor) {
		mapOfBoardMessages.put(boardMessageId, messageAuthor);
	}
	
	/**
	 * Adds the specified invitation and receiver data to the 'mapOfInvitations' attribute
	 * 
	 * @param invitationInstance - The Invitation instance to be added to the 'mapOfInvitations' attribute
	 * @param invitationReceiver - The String content of the receiver of the invitation
	 */
	private void addDataToMapOfInvitations(Invitation invitationInstance, String invitationReceiver) {
		mapOfInvitations.put(invitationInstance, invitationReceiver);
	}
	
	/**
	 * Adds the specified file to the 'listOfFiles' attribute
	 * 
	 * @param fileEntryInstance - The FileEntry instance to be added to the 'listOfFiles' attribute
	 */
	private void addFileToListOfFiles(FileEntry fileEntryInstance) {
		listOfFiles.add(fileEntryInstance);
	}
	
	/**
	 * Adds the specified user to the 'listOfUsers' attribute
	 * 
	 * @param userInstance - The User instance to be added to the 'listOfUsers' attribute
	 */
	private void addUserToListOfUsers(User userInstance) {
		listOfUsers.add(userInstance);
	}
		
	/**
	 * Retrieves the list of communities to be deleted from the loaded JSON content
	 */
	protected void createListOfCommunitiesToBeDeletedFromJsonContent() {
		
		log.info("INFO: Now searching through the JSON content for all communities which are to be deleted");
		retrieveDeletableComponentsFromJsonContent(Value.COMMUNITY);
	}
	
	/**
	 * Retrieves the list of files to be deleted from the loaded JSON content
	 */
	protected void createListOfFilesToBeDeletedFromJsonContent() {
		
		log.info("INFO: Now searching through the JSON content for all files which are to be deleted");
		retrieveDeletableComponentsFromJsonContent(Value.FILE);
	}
	
	/**
	 * Initialises and adds all relevant User instances from the 'jsonContent' attribute to the list of users
	 * 
	 * @param numberOfUsersToInitialise - The Integer number of how many users were used in the data population class
	 */
	protected void createListOfUsersFromJsonContent(int numberOfUsersToInitialise) {
		
		log.info("INFO: Now searching through the JSON content until " + numberOfUsersToInitialise + " users are found and initialised");
		
		// Initialise all variables and attributes before the loop
		setJsonIndex(0);
		String userKey = JSONData.createKeyValuePairAsString(Key.COMPONENT, Value.USER);
		String currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		
		while(currentJsonObject != null && getListOfUsers().size() < numberOfUsersToInitialise) {
			if(currentJsonObject.indexOf(userKey) > -1) {
				// Retrieve all relevant attributes for the current user from the full String of user data
				String uidContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.UID.getKey());
				String emailContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.EMAIL.getKey());
				String passwordContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.PASSWORD.getKey());
				String firstNameContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.FIRST_NAME.getKey());
				String lastNameContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.LAST_NAME.getKey());
				String displayNameContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.DISPLAY_NAME.getKey());
				
				log.info("INFO: A User object has been found in the JSON content for user with display name: " + displayNameContent);
				
				// Create a HashMap instance of the attributes for the user - this is required so that the User instance can be initialised correctly
				HashMap<String, String> userAttributes = new HashMap<String, String>();
				userAttributes.put(Key.UID.getKey(), uidContent);
				userAttributes.put(Key.EMAIL.getKey(), emailContent);
				userAttributes.put(Key.PASSWORD.getKey(), passwordContent);
				userAttributes.put(Key.FIRST_NAME.getKey().replace("_", " ").trim(), firstNameContent);
				userAttributes.put(Key.LAST_NAME.getKey().replace("_", " ").trim(), lastNameContent);
				userAttributes.put(Key.DISPLAY_NAME.getKey().replace("_", " ").trim(), displayNameContent);
				
				log.info("INFO: Now creating a User instance for the user with display name: " + displayNameContent);
				User currentUser = new User(userAttributes);
				addUserToListOfUsers(currentUser);
			}
			// Increment the 'jsonIndex' attribute and retrieve the next JSON object from the 'jsonContent' attribute
			setJsonIndex(getJsonIndex() + 1);
			currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		}
	}
	
	/**
	 * Retrieves the HashMap of board messages to be deleted from the loaded JSON content
	 */
	protected void createMapOfBoardMessagesToBeDeletedFromJsonContent() {
		
		log.info("INFO: Now searching through the JSON content for all board messages which are to be deleted");
		
		// Initialise all variables and attributes required
		setJsonIndex(0);
		String componentKey = JSONData.createKeyValuePairAsString(Key.COMPONENT, Value.BOARD_MESSAGE);	
		String currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		
		while(currentJsonObject != null) {
			if(currentJsonObject.indexOf(componentKey) > -1) {
				// Retrieve all relevant attributes for the current component from the full String of component data
				String idContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.ID.getKey());
				String authorContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.AUTHOR.getKey());
				
				log.info("INFO: A deletable component has been found in the JSON content with ID: " + idContent);
				addDataToMapOfBoardMessages(idContent, authorContent);
			}
			// Increment the 'jsonIndex' attribute and retrieve the next JSON object from the 'jsonContent' attribute
			setJsonIndex(getJsonIndex() + 1);
			currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		}
	}
	
	/**
	 * Retrieves the HashMap of network invitations to be deleted from the loaded JSON content
	 */
	protected void createMapOfInvitationsToBeDeletedFromJsonContent() {
		
		log.info("INFO: Now searching through the JSON content for all network invitations which are to be deleted");
		
		// Initialise all variables and attributes required
		setJsonIndex(0);
		String componentKey = JSONData.createKeyValuePairAsString(Key.COMPONENT, Value.INVITATION);
		String componentType = JSONData.createKeyValuePairAsString(Key.INVITATION_TYPE, Value.NETWORK);
		String currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		
		while(currentJsonObject != null) {
			if(currentJsonObject.indexOf(componentKey) > -1 && currentJsonObject.indexOf(componentType) > -1) {
				// Retrieve all relevant attributes for the current component from the full String of component data
				String authorContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.AUTHOR.getKey());
				String receiverContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.RECEIVER.getKey());
				String selfLinkContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.SELF_LINK.getKey());
				
				log.info("INFO: A deletable component has been found in the JSON content which was sent by: " + authorContent);
				
				// Create the author details for the component
				Person authorElement = Abdera.getNewFactory().newAuthor();
				authorElement.setName(authorContent);
				
				log.info("INFO: Now creating an Invitation instance for the invitation which was sent by: " + authorContent);
				Invitation invitation = new Invitation("", "", "", "");
				invitation.addAuthor(authorElement);
				
				// Add all relevant links to the invitation
				invitation.addLink(StringConstants.REL_SELF, StringConstants.MIME_NULL, selfLinkContent);
				invitation.addLink(StringConstants.REL_EDIT, StringConstants.MIME_ATOM_XML, selfLinkContent);
				
				addDataToMapOfInvitations(invitation, receiverContent);
			}
			// Increment the 'jsonIndex' attribute and retrieve the next JSON object from the 'jsonContent' attribute
			setJsonIndex(getJsonIndex() + 1);
			currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		}
	}
	
	/**
	 * Deletes the test case data file at the specified file path
	 * 
	 * @param filePathAndName - The absolute path to and name of the test case data file to be deleted
	 * @return - True if the deletion is successful, false if it is not successful
	 */
	private boolean deleteTestCaseDataJSFile() {
		
		File file = new File(getFilename());
		if(file.exists()) {
			return file.delete();
		} else {
			return false;
		}
	}
	
	/**
	 * Retrieves the 'jsonContent' attribute
	 * 
	 * @return - The String content of the 'jsonContent' attribute
	 */
	private String getJsonContent() {
		return jsonContent;
	}
	
	/**
	 * Retrieves the 'jsonIndex' attribute
	 * 
	 * @return - The Integer value of the 'jsonIndex' attribute
	 */
	private int getJsonIndex() {
		return jsonIndex;
	}
	
	/**
	 * Retrieves the 'listOfCommunities' attribute
	 * 
	 * @return - The List<Community> instance of the 'listOfCommunities' attribute
	 */
	protected List<Community> getListOfCommunities() {
		return listOfCommunities;
	}
	
	/**
	 * Retrieves the 'listOfFiles' attribute
	 * 
	 * @return - The List<FileEntry> instance of the 'listOfFiles' attribute
	 */
	protected List<FileEntry> getListOfFiles() {
		return listOfFiles;
	}
	
	/**
	 * Retrieves the 'listOfUsers' attribute
	 * 
	 * @return - The List<User> instance of the 'listOfUsers' attribute
	 */
	protected List<User> getListOfUsers() {
		return listOfUsers;
	}
	
	/**
	 * Retrieves the 'mapOfBoardMessages' attribute
	 * 
	 * @return - The HashMap<String, String> instance of the 'mapOfBoardMessages' attribute
	 */
	protected HashMap<String, String> getMapOfBoardMessages() {
		return mapOfBoardMessages;
	}
	
	/**
	 * Retrieves the 'mapOfInvitations' attribute
	 * 
	 * @return - The HashMap<Invitation, String> instance of the 'mapOfInvitations' attribute
	 */
	protected HashMap<Invitation, String> getMapOfInvitations() {
		return mapOfInvitations;
	}
	
	/**
	 * Reads the text content in from the specified file name and stores it in the 'jsonContent' attribute
	 */
	protected void readTestCaseDataFromFile() {
		
		// Create the BufferedReader instance to be used to read from the file and verify the file exists
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(getFilename()));
		} catch(FileNotFoundException fnfe) {
			log.info("ERROR: Could not find an existing file with path and name: " + getFilename());
			fnfe.printStackTrace();
			Assert.fail("ERROR: Failing currently executing class - existing file could NOT be found");
		}
		
		if(bufferedReader != null) {
			// Read all contents from the file and store all in the StringBuilder instance
			StringBuilder stringBuilder = new StringBuilder();
			String currentTextLine = "";
			while(currentTextLine != null) {
				try {
					currentTextLine = bufferedReader.readLine();
				} catch(IOException ioex) {
					currentTextLine = null;
				}
				if(currentTextLine != null) {
					stringBuilder.append(currentTextLine);
				}
			}
			// Close the BufferedReader instance and set the JSON content
			try {
				bufferedReader.close();
			} catch(IOException ioex) {
				log.info("ERROR: Could not close the BufferedReader instance cleanly");
				ioex.printStackTrace();
				Assert.fail("ERROR: Failing currently executing class - BufferedReader could NOT be closed cleanly");
			}
			setJsonContent(stringBuilder.toString());
			log.info("INFO: The JSON content has been retrieved from the file: " + getJsonContent());
		}
	}
	
	/**
	 * Retrieves all deletable components for the specified component value from the JSON content
	 * 
	 * @param componentValue - The Value instance of the component to be retrieved from the JSON content
	 */
	private void retrieveDeletableComponentsFromJsonContent(Value componentValue) {
		
		log.info("INFO: Now searching through the JSON content for all deletable components with value: " + componentValue.getValue());
		
		// Initialise all variables and attributes required
		setJsonIndex(0);
		String componentKey = JSONData.createKeyValuePairAsString(Key.COMPONENT, componentValue);
		String currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		
		while(currentJsonObject != null) {
			if(currentJsonObject.indexOf(componentKey) > -1) {
				// Retrieve whether this component action relates to a component which is to be deleted
				boolean deleteComponent = Boolean.parseBoolean(JSONData.getValueForSpecifiedKey(currentJsonObject, Key.DELETE.getKey()));
				
				if(deleteComponent == true) {
					// Retrieve all relevant attributes for the current component from the full String of component data
					String titleContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.TITLE.getKey());
					String authorContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.AUTHOR.getKey());
					String selfLinkContent = JSONData.getValueForSpecifiedKey(currentJsonObject, Key.SELF_LINK.getKey());
					
					log.info("INFO: A deletable component has been found in the JSON content with title: " + titleContent);
					
					// Create the author details for the component
					Person authorElement = Abdera.getNewFactory().newAuthor();
					authorElement.setName(authorContent);
					
					// Initialise all possible component instances as null
					Community community = null;
					FileEntry fileEntry = null;
					if(componentValue == Value.COMMUNITY) {
						log.info("INFO: Now creating a Community instance for the community with title: " + titleContent);
						community = new Community(titleContent, null, null, null);
						community.addAuthor(authorElement);
						
						// Add all relevant links to the community
						community.addLink(StringConstants.REL_SELF, StringConstants.MIME_NULL, selfLinkContent);
						community.addLink(StringConstants.REL_EDIT, StringConstants.MIME_NULL, selfLinkContent);
						
						addCommunityToListOfCommunities(community);
					} else if(componentValue == Value.FILE) {
						log.info("INFO: Now creating a FileEntry instance for the file with title: " + titleContent);
						fileEntry = new FileEntry(null, titleContent, null, "", Permissions.PUBLIC, false, null, null, new Date(), new Date(), false, false, SharePermission.EDIT, null, null);
						fileEntry.addAuthor(authorElement);
						
						// Add all relevant links to the file
						fileEntry.addLink(StringConstants.REL_SELF, StringConstants.MIME_NULL, selfLinkContent);
						fileEntry.addLink(StringConstants.REL_EDIT, StringConstants.MIME_ATOM_XML, selfLinkContent);
						
						addFileToListOfFiles(fileEntry);
					}
				}
			}
			// Increment the 'jsonIndex' attribute and retrieve the next JSON object from the 'jsonContent' attribute
			setJsonIndex(getJsonIndex() + 1);
			currentJsonObject = JSONData.getObjectFromJsonContent(getJsonIndex(), getJsonContent());
		}
	}
	
	/**
	 * Sets the value for the 'filename' attribute with the absolute path to the file and the file extension being added by default
	 * 
	 * @param filenameValue - The String content to be set as the value for the 'filename' attribute
	 */
	protected void setFilename(String filenameValue) {
		if(filenameValue == null) {
			filename = null;
		} else {
			filename = FILE_ABSOLUTE_PATH + filenameValue.replace("_CleanUp", "").trim() + FILE_EXTENSION;
			log.info("INFO: The file name and path to the JSON script to be imported have now been set to: " + filename);
		}
	}
	
	/**
	 * Sets the value for the 'jsonContent' attribute
	 * 
	 * @param contentValue - The String content to which the 'jsonContent' attribute will be set
	 */
	private void setJsonContent(String contentValue) {
		jsonContent = contentValue;
	}
	
	/**
	 * Sets the value for the 'jsonIndex' attribute
	 * 
	 * @param indexValue - The Integer value to which the 'jsonIndex' attribute will be set
	 */
	private void setJsonIndex(int indexValue) {
		jsonIndex = indexValue;
	}
	
	/**
	 * Sets the value for the 'listOfCommunities' attribute
	 * 
	 * @param listValue - The List<Community> instance to which the 'listOfCommunities' attribute will be set
	 */
	private void setListOfCommunities(List<Community> listValue) {
		listOfCommunities = listValue;
	}
	
	/**
	 * Sets the value for the 'listOfFiles' attribute
	 * 
	 * @param listValue - The List<FileEntry> instance to which the 'listOfFiles' attribute will be set
	 */
	private void setListOfFiles(List<FileEntry> listValue) {
		listOfFiles = listValue;
	}
	
	/**
	 * Sets the value for the 'listOfUsers' attribute
	 * 
	 * @param listValue - The List<User> instance to which the 'listOfUsers' attribute will be set
	 */
	private void setListOfUsers(List<User> listValue) {
		listOfUsers = listValue;
	}
	
	/**
	 * Sets the value for the 'mapOfBoardMessages' attribute
	 * 
	 * @param mapValue - The HashMap<String, String> instance to which the 'mapOfBoardMessages' attribute will be set
	 */
	private void setMapOfBoardMessages(HashMap<String, String> mapValue) {
		mapOfBoardMessages = mapValue;
	}
	
	/**
	 * Sets the value for the 'mapOfInvitations' attribute
	 * 
	 * @param mapValue - The HashMap<Invitation, String> instance to which the 'mapOfInvitations' attribute will be set
	 */
	private void setMapOfInvitations(HashMap<Invitation, String> mapValue) {
		mapOfInvitations = mapValue;
	}
}