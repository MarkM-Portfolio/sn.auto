package com.ibm.conn.auto.tests.homepage.fvt.orientme.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.data.JSONData;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

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

@SuppressWarnings("serial")
public class TestCaseData extends HashMap<String, String> {

	private int testCaseDataIndex;
	
	/**
	 * The Key enum contains values for "key" attributes in JSON key / value pairs
	 * 
	 * @author Anthony Cox
	 */
	public enum Key {
		ACTION("action"),
		AUTHOR("author"),
		COMPONENT("component"),
		CONTENT("content"),
		DELETE("delete"),
		DISPLAY_NAME("display_name"),
		EMAIL("email"),
		FIRST_NAME("first_name"),
		ID("id"),
		INVITATION_TYPE("invitation_type"),
		LAST_NAME("last_name"),
		PARENT("parent"),
		PARENT_TYPE("parent_type"),
		PASSWORD("password"),
		RECEIVER("receiver"),
		SELF_LINK("self_link"),
		TITLE("title"),
		UID("uid"),
		UPDATE_CONTENT("update_content"),
		URL("url");
		
		private String key;
		
		private Key(String theKey) {
			key = theKey;
		}
		
		public String getKey() {
			return key;
		}
	}
	
	/**
	 * The Key enum contains values for "value" attributes in JSON key / value pairs
	 * 
	 * @author Anthony Cox
	 */
	public enum Value {
		BOARD_MESSAGE("board_message"),
		BOOKMARK("bookmark"),
		COMMENT("comment"),
		COMMUNITY("community"),
		CREATE("create"),
		FALSE("false"),
		FILE("file"),
		INITIALISE("initialise"),
		INVITATION("invitation"),
		LIKE("like"),
		NETWORK("network"),
		TRUE("true"),
		UNDEFINED("undefined"),
		UPDATE("update"),
		USER("user"),
		WIKI("wiki"),
		WIKIPAGE("wiki_page");
		
		private String value;
		
		private Value(String theValue) {
			value = theValue;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public TestCaseData() {
		super();
		
		setTestCaseDataIndex(0);
	}
	
	/**
	 * Adds all relevant data for a 'create wiki comment' action
	 * 
	 * @param createdWikiComment - The WikiComment instance of the wiki comment which has been created
	 * @param parentWikiPage - The WikiPage instance of the parent wiki page to which the wiki comment belongs
	 * @param wikiCommentCreator - The User instance of the user who created the wiki comment
	 */
	public void addCommentOnWikiPageData(WikiComment createdWikiComment, WikiPage parentWikiPage, User wikiCommentCreator) {
		
		// Create the list of data to be stored about this created wiki comment
		List<String> wikiCommentData = initialiseListOfCommonData(Value.CREATE, Value.COMMENT, wikiCommentCreator.getDisplayName(), null, 
																	createdWikiComment.getContent(), null, false);
		wikiCommentData = incrementListOfDataWithParentData(wikiCommentData, parentWikiPage.getTitle(), Value.WIKIPAGE);
		
		// Add the created wiki comment data to this instance
		addDataToThisInstance(wikiCommentData);
	}
	
	/**
	 * Adds all relevant data for a 'create board message' action
	 * 
	 * @param boardMessageId - The String content of the ID of the board message
	 * @param boardMessageContent - The String content of the board message that has been created
	 * @param messageSender - The User instance of the user who sent the board message
	 * @param messageReceiver - The User instance of the user who received the board message
	 */
	public void addCreateBoardMessageData(String boardMessageId, String boardMessageContent, User messageSender, User messageReceiver) {
		
		List<String> boardMessageData = new ArrayList<String>();
		boardMessageData = incrementListOfDataWithActionAndComponentData(boardMessageData, Value.CREATE, Value.BOARD_MESSAGE);
		boardMessageData.add(JSONData.createKeyValuePairAsString(Key.ID, boardMessageId.trim()));
		boardMessageData = incrementListOfDataWithAuthorAndReceiverData(boardMessageData, messageSender.getDisplayName(), messageReceiver.getDisplayName());
		boardMessageData = incrementListOfDataWithContentData(boardMessageData, boardMessageContent);
		boardMessageData = incrementListOfDataWithDeleteData(boardMessageData, Value.UNDEFINED.getValue(), true);
		
		// Add the board message data to this instance
		addDataToThisInstance(boardMessageData);
	}
	
	/**
	 * Adds all relevant data for a 'create community' action
	 * 
	 * @param createdCommunity - The Community instance of the community which has been created
	 * @param baseCommunityTemplate - The BaseCommunity instance of the community which has been created
	 */
	public void addCreateCommunityData(Community createdCommunity, BaseCommunity baseCommunityTemplate) {
		
		// Create the list of data to be stored about this created community
		List<String> communityData = initialiseListOfCommonData(Value.CREATE, Value.COMMUNITY, createdCommunity.getAuthors().get(0).getName(),
																	baseCommunityTemplate.getName(), baseCommunityTemplate.getDescription(),
																	createdCommunity.getEditLink(), true);
		// Add the created community data to this instance
		addDataToThisInstance(communityData);
	}
	
	/**
	 * Adds all relevant data for a 'create file' action
	 * 
	 * @param createdFile - The FileEntry instance of the file which has been created
	 * @param parentCommunity - The Community instance of the parent community to which the file belongs (assign as null for standalone file)
	 * @param fileCreator - The User instance of the user who created the file
	 */
	public void addCreateFileData(FileEntry createdFile, Community parentCommunity, User fileCreator) {
		
		String fileDeleteURL;
		boolean toBeDeleted;
		if(parentCommunity == null) {
			// Standalone file which needs to be marked for deletion
			fileDeleteURL = createdFile.getEditLink();
			toBeDeleted = true;
		} else {
			// Community file - no need to mark this for deletion since the community deletion will automatically clean this up
			fileDeleteURL = null;
			toBeDeleted = false;
		}
		List<String> fileData = initialiseListOfCommonData(Value.CREATE, Value.FILE, fileCreator.getDisplayName(), createdFile.getTitle(), null, 
															fileDeleteURL, toBeDeleted);
		if(parentCommunity != null) {
			// Community file - add the parent community details to the list of data
			fileData = incrementListOfDataWithParentData(fileData, parentCommunity.getTitle(), Value.COMMUNITY);
		}
		// Add the created file data to this instance
		addDataToThisInstance(fileData);
	}
	
	/**
	 * Adds all relevant data for a 'send network invitation' action
	 * 
	 * @param createdInvitation - The Invitation instance of the invitation which has been sent
	 * @param userSendingInvitation - The User instance of the user sending the invitation
	 * @param userReceivingInvitation - The User instance of the user receiving the invitation
	 */
	public void addCreateInvitationData(Invitation createdInvitation, User userSendingInvitation, User userReceivingInvitation) {
		
		List<String> invitationData = new ArrayList<String>();
		invitationData = incrementListOfDataWithActionAndComponentData(invitationData, Value.CREATE, Value.INVITATION);
		invitationData.add(JSONData.createKeyValuePairAsString(Key.INVITATION_TYPE, Value.NETWORK));
		invitationData = incrementListOfDataWithAuthorAndReceiverData(invitationData, userSendingInvitation.getDisplayName(), userReceivingInvitation.getDisplayName());
		invitationData = incrementListOfDataWithContentData(invitationData, Data.NETWORK_INVITATION_MESSAGE);
		invitationData = incrementListOfDataWithDeleteData(invitationData, createdInvitation.getEditLink(), true);
		
		// Add the board message data to this instance
		addDataToThisInstance(invitationData);
	}
	
	/**
	 * Adds all relevant data for a 'create wiki' action - works for both standalone and community wikis
	 * 
	 * @param createdWiki - The Wiki instance of the wiki which has been created
	 * @param parentCommunity - The Community instance of the parent community to which the Wiki belongs (assign as null for standalone wiki)
	 * @param baseWikiTemplate - The BaseWiki instance of the wiki which has been created - set this to null for community wiki
	 * @param wikiCreator - The User instance of the user who created the wiki
	 */
	public void addCreateWikiData(Wiki createdWiki, Community parentCommunity, BaseWiki baseWikiTemplate, User wikiCreator) {
		
		String wikiDeleteURL, wikiTitle;
		boolean toBeDeleted;
		if(parentCommunity == null) {
			// Standalone wiki which needs to be marked for deletion
			wikiDeleteURL = createdWiki.getSelfLink();
			wikiTitle = baseWikiTemplate.getName();
			toBeDeleted = true;
		} else {
			// Community wiki - no need to mark this for deletion since the community deletion will automatically clean this up
			wikiDeleteURL = null;
			wikiTitle = parentCommunity.getTitle();
			toBeDeleted = false;
		}
		// Create the list of data to be stored about this created wiki
		List<String> wikiData = initialiseListOfCommonData(Value.CREATE, Value.WIKI, wikiCreator.getDisplayName(), wikiTitle, createdWiki.getSummary(), 
															wikiDeleteURL, toBeDeleted);
		if(parentCommunity != null) {
			// Community wiki - add the parent community details to the list of data
			incrementListOfDataWithParentData(wikiData, parentCommunity.getTitle(), Value.COMMUNITY);
		}
		// Add the created wiki data to this instance
		addDataToThisInstance(wikiData);
	}
	
	/**
	 * Adds all relevant data for a 'create wiki page' action
	 * 
	 * @param parentWiki - The Wiki instance of the parent wiki for the create wiki page
	 * @param createdWikiPage - The WikiPage instance of the wiki page which has been created
	 * @param baseWikiPageTemplate - The BaseWikiPage instance of the wiki page which has been created
	 */
	public void addCreateWikiPageData(Wiki parentWiki, WikiPage createdWikiPage, BaseWikiPage baseWikiPageTemplate) {
		
		// Create the list of data to be stored about this created wiki page
		List<String> wikiPageData = initialiseListOfCommonData(Value.CREATE, Value.WIKIPAGE, createdWikiPage.getAuthors().get(0).getName(),
																baseWikiPageTemplate.getName(), baseWikiPageTemplate.getDescription(), null, false);
		wikiPageData = incrementListOfDataWithParentData(wikiPageData, parentWiki.getTitle(), Value.WIKI);
		
		// Add the created wiki page data to this instance
		addDataToThisInstance(wikiPageData);
	}
	
	/**
	 * Adds the specified data String to this object instance and increments the index position counter
	 * 
	 * @param listOfData - The List<String> instance to be added to this object instance as its data
	 */
	private void addDataToThisInstance(List<String> listOfData) {
		put(JSONData.createJSONObjectIdentifierString(getTestCaseDataIndex()), JSONData.createStringOfAllJSONObjectKeyValuePairs(listOfData));
		setTestCaseDataIndex(getTestCaseDataIndex() + 1);
	}
	
	/**
	 * Adds all relevant data for a 'like wiki page' action 
	 * 
	 * @param parentWiki - The Wiki instance of the parent wiki to which the wiki page belongs
	 * @param likedWikiPage - The WikiPage instance of the wiki page which has been liked / recommended
	 * @param baseWikiPageTemplate - The BaseWikiPage instance of the wiki page which has been liked / recommended
	 * @param userLikingWikiPage - The User instance of the user who has liked / recommended the wiki page
	 */
	public void addLikeWikiPageData(Wiki parentWiki, WikiPage likedWikiPage, BaseWikiPage baseWikiPageTemplate, User userLikingWikiPage) {
		
		// Create the list of data to be stored about this liked wiki page
		List<String> wikiPageData = initialiseListOfCommonData(Value.LIKE, Value.WIKIPAGE, userLikingWikiPage.getDisplayName(),
																baseWikiPageTemplate.getName(), likedWikiPage.getContent(), null, false);
		wikiPageData = incrementListOfDataWithParentData(wikiPageData, parentWiki.getTitle(), Value.WIKI);
		
		// Add the like wiki page data to this instance
		addDataToThisInstance(wikiPageData);
	}
	
	/**
	 * Adds all relevant data for each user assignment in the test case
	 * 
	 * @param listOfUsers - The ArrayList<User> of all users assigned to the test case
	 */
	public void addUserAssignmentData(ArrayList<User> listOfUsers) {

		for(User user : listOfUsers) {
			// Create the list of data to be stored for this user
			List<String> userData = new ArrayList<String>();
			userData = incrementListOfDataWithActionAndComponentData(userData, Value.INITIALISE, Value.USER);
			userData.add(JSONData.createKeyValuePairAsString(Key.UID, user.getUid()));
			userData.add(JSONData.createKeyValuePairAsString(Key.EMAIL, user.getEmail()));
			userData.add(JSONData.createKeyValuePairAsString(Key.PASSWORD, user.getPassword()));
			userData.add(JSONData.createKeyValuePairAsString(Key.FIRST_NAME, user.getFirstName()));
			userData.add(JSONData.createKeyValuePairAsString(Key.LAST_NAME, user.getLastName()));
			userData.add(JSONData.createKeyValuePairAsString(Key.DISPLAY_NAME, user.getDisplayName()));
			
			// Add the user assignment data to this instance
			addDataToThisInstance(userData);
		}
	}
	
	/**
	 * Retrieves the value of the 'testCaseDataIndex' attribute
	 * 
	 * @return - The Integer value of the 'testCaseDataIndex' attribute
	 */
	private int getTestCaseDataIndex() {
		return testCaseDataIndex;
	}
	
	/**
	 * Increments the specified list of data with action and component data details
	 * 
	 * @param listOfData - The List<String> of current data to be updated with action and component data details
	 * @param valueOfAction - The Value instance of the value to be associated with the 'Action' key
	 * @param valueOfComponent - The Value instance of the value to be associated with the 'Component' key
	 * @return - The updated List<String> instance
	 */
	private List<String> incrementListOfDataWithActionAndComponentData(List<String> listOfData, Value valueOfAction, Value valueOfComponent) {
		
		// Add the action and component details to the existing list of data
		listOfData.add(JSONData.createKeyValuePairAsString(Key.ACTION, valueOfAction));
		listOfData.add(JSONData.createKeyValuePairAsString(Key.COMPONENT, valueOfComponent));
		
		return listOfData;
	}
	
	/**
	 * Increments the specified list of data with author and receiver data details
	 * 
	 * @param listOfData - The List<String> of current data to be updated with author and receiver data details
	 * @param authorName - The String content of the author / creator of the component
	 * @param receiverName - The String content of the receiver of the component
	 * @return - The updated List<String> instance
	 */
	private List<String> incrementListOfDataWithAuthorAndReceiverData(List<String> listOfData, String authorName, String receiverName) {
		
		// Add the author and receiver details to the existing list of data
		listOfData = incrementListOfDataWithAuthorData(listOfData, authorName);
		listOfData.add(JSONData.createKeyValuePairAsString(Key.RECEIVER, receiverName));
		
		return listOfData;
	}
	
	/**
	 * Increments the specified list of data with author data details
	 * 
	 * @param listOfData - The List<String> of current data to be updated with author data details
	 * @param authorName - The String content of the author / creator of the component
	 * @return - The updated List<String> instance
	 */
	private List<String> incrementListOfDataWithAuthorData(List<String> listOfData, String authorName) {
		
		// Add the author details to the existing list of data
		listOfData.add(JSONData.createKeyValuePairAsString(Key.AUTHOR, authorName));
		
		return listOfData;
	}
	
	/**
	 * Increments the specified list of data with content data details
	 * 
	 * @param listOfData - The List<String> of current data to be updated with content data details
	 * @param content - The String content of the content of the component
	 * @return - The updated List<String> instance
	 */
	private List<String> incrementListOfDataWithContentData(List<String> listOfData, String content) {
		
		// Add the content details to the existing list of data
		listOfData.add(JSONData.createKeyValuePairAsString(Key.CONTENT, content));
		
		return listOfData;
	}
	
	/**
	 * Increments the specified list of data with delete data
	 * 
	 * @param listOfData - The List<String> of current data to be updated with delete data details
	 * @param deleteLinkURL - The String content of the URL to be used to delete the component (assign as null if no deletion required)
	 * @param toBeDeleted - True if this component is to be deleted, false otherwise
	 * @return - The updated List<String> instance
	 */
	private List<String> incrementListOfDataWithDeleteData(List<String> listOfData, String deleteLinkURL, boolean toBeDeleted) {
		
		// Add the delete details to the existing list of data
		if(toBeDeleted) {
			listOfData.add(JSONData.createKeyValuePairAsString(Key.DELETE, Value.TRUE));
			listOfData.add(JSONData.createKeyValuePairAsString(Key.SELF_LINK, deleteLinkURL));
		} else {
			listOfData.add(JSONData.createKeyValuePairAsString(Key.DELETE, Value.FALSE));
		}
		return listOfData;
	}
	
	/**
	 * Increments the specified list of data with parent data details
	 * 
	 * @param listOfData - The List<String> of current data to be updated with parent data details
	 * @param parentTitle - The String content of the title of the parent for the component
	 * @param valueOfParentType - The Value instance of the type of component for the parent
	 * @return - The updated List<String> instance
	 */
	private List<String> incrementListOfDataWithParentData(List<String> listOfData, String parentTitle, Value valueOfParentType) {
		
		// Add the parent and parent type details to the existing list of data
		listOfData.add(JSONData.createKeyValuePairAsString(Key.PARENT, parentTitle));
		listOfData.add(JSONData.createKeyValuePairAsString(Key.PARENT_TYPE, valueOfParentType));
		
		return listOfData;
	}
	
	/**
	 * Initialises a list of data which contains common keys for use with all components
	 * 
	 * @param valueOfAction - The Value instance of the value to be associated with the 'Action' key
	 * @param valueOfComponent - The Value instance of the value to be associated with the 'Component' key
	 * @param authorName - The String content of the author / creator of the component
	 * @param title - The String content of the title for the component (assign as null if no title is to be set)
	 * @param content - The String content of the content for the component (assign as null if no content is to be set)
	 * @param deleteLinkURL - The String content of the URL to be used to delete the component (assign as null if no deletion required)
	 * @param toBeDeleted - True if this component is to be deleted, false otherwise
	 * @return - The List<String> of all data with all keys and values included
	 */
	private List<String> initialiseListOfCommonData(Value valueOfAction, Value valueOfComponent, String authorName, String title, String content,
													String deleteLinkURL, boolean toBeDeleted) {
		// Create the list of all common data
		List<String> listOfData = new ArrayList<String>();
		
		listOfData = incrementListOfDataWithActionAndComponentData(listOfData, valueOfAction, valueOfComponent);
		listOfData = incrementListOfDataWithAuthorData(listOfData, authorName);
		
		if(title != null) {
			listOfData.add(JSONData.createKeyValuePairAsString(Key.TITLE, title));
		}
		if(content != null) {
			listOfData = incrementListOfDataWithContentData(listOfData, content);
		}
		listOfData = incrementListOfDataWithDeleteData(listOfData, deleteLinkURL, toBeDeleted);
		
		return listOfData;
	}
	
	/**
	 * Sets the value of the 'testCaseDataIndex' attribute
	 * 
	 * @param value - The Integer value to set to the 'testCaseDataIndex' attribute
	 */
	private void setTestCaseDataIndex(int value) {
		testCaseDataIndex = value;
	}
}