package com.ibm.conn.auto.lcapi;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Connection;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Message;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.lcapi.common.Profile;
import com.ibm.conn.auto.util.Mentions;

public class APIProfilesHandler extends APIHandler<ProfilesService> {

	private static final Logger log = LoggerFactory.getLogger(APIProfilesHandler.class);

	public APIProfilesHandler(String serverURL, String username, String password) {

		super("profiles", serverURL, username, password);
	}
	
	@Override
	protected ProfilesService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		try {
			return new ProfilesService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Profiles service: " + e.getMessage());
			return null;
		}
	}

	public Profile setProfileTags(String tags) {

		log.info("API: Start setting profile tags: " + tags);

		VCardEntry vCard = service.getUserVCard();

		TagsEntry tagBundle = new TagsEntry(tags);

		Entry result;
		if (!this.setup.getServiceConfig().isEmailHidden()) {
			String profileEmail = vCard.getVCardFields().get(StringConstants.VCARD_EMAIL);
			result = (Entry) service.setProfileTags(tagBundle, profileEmail, null, profileEmail, null);
		} else {
			String profileKey = vCard.getVCardFields().get(StringConstants.VCARD_PROFILE_KEY);
			result = (Entry) service.setProfileTags(tagBundle, null, profileKey, null, profileKey);
		}

		if (APIUtils.resultSuccess(result, "Profiles")) {
			return new Profile(vCard, tags);
		} else {
			return null;
		}
	}
	
	public String getUUID(){
		String userID = service.getUserProfile().toString();
		int index1 = userID.indexOf("userid ") + 7;
		int index2 = userID.indexOf("</title>"); 
		return userID.substring(index1, index2);	 
	}
	
	public String getEmail(){
		String email = service.getUserProfile().toString();
		int index1 = email.indexOf("<email>") + 7;
		int index2 = email.indexOf("</email>");
		return email.substring(index1, index2);
	}

	public String getDesplayName() {
		String desplayName = service.getUserProfile().toString();
		int index1 = desplayName.indexOf("</id><title") + 24;
		int index2 = desplayName.indexOf("</title><updated>");
		return desplayName.substring(index1, index2);
	}
	
	/**
	 * This method will post a board message on the user's own profile page
	 * @param boardMessage - The board message to be posted
	 * @return newMessageResult - A new Message object
	 */
	public Message postBoardMessage(String boardMessage){
		
		log.info("INFO: Create a new Message object for the board message");
		Message newMessage = new Message(boardMessage);

		log.info("INFO: Post the board message");
		Entry entry = (Entry)service.addBoardMessage(newMessage);
		
		log.info("INFO: Create a new Message object");
		Message newMessageResult = new Message(entry);
		
		return newMessageResult;
		
	}

	/**
	 * Posts a status update
	 * 
	 * @param statusUpdate - The String instance of the status update to be posted
	 * @return statusUpdateId - A String object which can be used for further actions, e.g. commenting
	 */
	public String postStatusUpdate(String statusUpdate){
		
		log.info("INFO: A status update will now be posted with content: " + statusUpdate);
		
		// Create the URL to which the POST request will be sent
		String postURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@me/@all");
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postURL);
		
		// Create the JSON content to POST to the server
		String jsonContent = "{\"content\":\"" + statusUpdate + "\"}";
		log.info("INFO: The JSON content for the status update with mentions has been created: " + jsonContent);
		
		String postResponse = service.createASEntry(postURL, jsonContent);
		
		log.info("INFO: Now retrieving the ID for the status update from the JSON response");
		int indexOfNote = postResponse.indexOf(".note:");
		String statusUpdateId = "urn:lsid:lconn.ibm.com:profiles.note:" + postResponse.substring(indexOfNote + 6, indexOfNote + 42);
		log.info("INFO: The status update ID has been retrieved as: " + statusUpdateId);
		 
		return statusUpdateId;
	}
	
	/**
	 * Posts a status update with mentions
	 * 
	 * @param mentions - A Mentions object containing all the information for a successful mention
	 * @return statusUpdateId - A String object which can be used for further actions, e.g. commenting
	 */
	public String addMentionsStatusUpdate(Mentions mentions){

		log.info("INFO: A status update will now be posted with mentions to: " + mentions.getUserToMention().getDisplayName());
		
		// Create the URL to which the POST request will be sent
		String postURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@me/@all");
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postURL);
		
		// Create the JSON content to POST to the server
		String jsonContent = "{\"content\":\"" + mentions.getBeforeMentionText() + " <span class=\\\"vcard\\\"><span class=\\\"fn\\\">@" + mentions.getUserToMention().getDisplayName() + "</span><span class=\\\"x-lconn-userid\\\">" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + " \"}"; 
		log.info("INFO: The JSON content for the status update with mentions has been created: " + jsonContent);
		
		String postResponse = service.createASEntry(postURL, jsonContent);
		 
		log.info("INFO: Now retrieving the ID for the status update from the JSON response");
		int indexOfNote = postResponse.indexOf(".note:");
		String statusUpdateId = "urn:lsid:lconn.ibm.com:profiles.note:" + postResponse.substring(indexOfNote + 6, indexOfNote + 42);
		log.info("INFO: The status update ID has been retrieved as: " + statusUpdateId);
		
		return statusUpdateId;
	}
	
	/**
	 * This method saves any news story from any view based on its story ID 
	 * Use in conjunction with this.getActivityStreamStoryId(String, boolean) in order
	 * to get the correctly formatted storyId variable for the URL in this method
	 * 
	 * @param storyId - the ID of the news feed story to be saved
	 * @return boolean value representing the success or failure of the save operation
	 */
	public boolean saveNewsStory(String storyId) {
		log.info("INFO: Saving a news story using the API");
	
		String serviceUrl = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/activitystreams/@me/@all/@all/");
		String commId = "urn:lsid:lconn.ibm.com:";
		String url = serviceUrl + commId + storyId;
		log.info("INFO: URL at which the JSON entry is to be sent - " + url);
		
		String jsonEntry = "{\"id\":\"\",\"actor\":{\"id\":\"\"},\"verb\":\"\",\"object\":{\"id\":\"\"},\"connections\":{\"saved\":\"true\"}}";
		
		log.info("INFO: PUT the JSON entry to the URL");
		int putStatus = service.putResponse(url, jsonEntry).getStatus();
		
		if(putStatus == 200) {
			log.info("INFO: Save status update operation was successful");
			return true;
		} else {
			log.info("INFO: Save status update operation was not successful");
			return false;
		}
	}
	
	/**
	 * This method returns the Story ID of any news story based on the content of the news story.
	 * 
	 * @param newsStoryContent - A String containing the content of the news story to be identified
	 * @param isDiscoverViewStory - A boolean where "true" indicates that the news story to be identified
	 * 								is in the Discover view and "false" indicates that the news story is
	 * 								in the I'm Following view.
	 * @return storyId - A String containing the story ID
	 * 
	 * NOTE: If the news story in the UI reads "You commented on your..." then the newsStoryContent variable
	 * needs to be adjusted to "UserName commented on their own..." instead of what's displayed in the UI. The Entry
	 * retrieved from the URL contains this version of the story and not the version in the UI.
	 */
	public String getActivityStreamStoryId(String newsStoryContent, boolean isDiscoverViewStory) {
		
		log.info("INFO: Retrieving the story ID for the news feed story with content: " + newsStoryContent);
		
		// Create the URL to retrieve the relevant news feed
		String newsFeedURL = service.getServiceURLString().replaceAll("profiles", "news");
		if(isDiscoverViewStory == true) {
			newsFeedURL += URLConstants.NEWS_PUBLIC_UPDATES;
		} else {
			newsFeedURL += URLConstants.NEWS_FEED_UPDATES;
		}
		log.info("INFO: The URL from which the news feed will be retrieved has been created: " + newsFeedURL);
		
		// Set the relevant variables for the do / while loop
		boolean foundEntry = false;
		Entry targetNewsStory = null;
		Feed newsFeed = null;
		int numberOfTries = 1;
		
		do {
			log.info("INFO: Attempt " + numberOfTries + " of 3 - Retrieving the news story ID from the news feed");
			
			// Retrieve the news feed and generate the list of entries from the feed
			newsFeed = (Feed) service.getAnyFeed(newsFeedURL);
			List<Entry> listOfEntries = newsFeed.getEntries();
			
			// Search the list of entries for the news story
			int index = 0;
			while(index < listOfEntries.size() && foundEntry == false) {
				Entry currentNewsStory = listOfEntries.get(index);
				if(currentNewsStory.toString().indexOf(newsStoryContent) > -1) {
					log.info("INFO: The news story has been found in the news feed: " + currentNewsStory.toString());
					targetNewsStory = currentNewsStory;
					foundEntry = true;
				}
				index ++;
			}
			numberOfTries ++;
		} while(foundEntry == false && numberOfTries <= 3);
		
		if(foundEntry) {
			Pattern pattern = Pattern.compile("story-[a-z]*.[a-z.]*-[a-z0-9-]*");
			Matcher matcher = pattern.matcher(targetNewsStory.toString());
			
			if(matcher.find() == true) {
				log.info("INFO: Found the news story ID - now converting to the correct ID format");
				String allStoryDetails = matcher.group(0);
				
				// Get the story type identifier from the matching string (eg: story-profiles, story-blog etc)
				String rawStoryTypeIdent = allStoryDetails.substring(0, allStoryDetails.indexOf("."));
				
				// Remove the story identifier from the string (makes retrieving the story id more efficient)
				allStoryDetails = allStoryDetails.substring(allStoryDetails.indexOf(rawStoryTypeIdent) 
																+ rawStoryTypeIdent.length());
				
				// Get the story ID and re-organise the story type identifier into the correct format
				String rawStoryId = allStoryDetails.substring(allStoryDetails.indexOf("-") + 1);
				String storyTypeIdent = rawStoryTypeIdent.substring(rawStoryTypeIdent.indexOf("-") + 1) + "."
											+ rawStoryTypeIdent.substring(0, rawStoryTypeIdent.indexOf("-")) + ":";
				
				// Finally, return the correctly formatted story ID
				log.info("INFO: The story ID will be returned as: " + (storyTypeIdent + rawStoryId));
				return "" + storyTypeIdent + rawStoryId;
			} else {
				log.info("INFO: Story ID string could not be retrieved from the news story entry");
				log.info(targetNewsStory.toString());
				return "ERROR";
			}
			// Returns this ID: dogear.story:22dc9dfd-814b-46b2-8ae8-da225ff7b64e
		} else {
			log.info("ERROR: The news story could not be found in the news feed");
			log.info(newsFeed.toString());
			return "ERROR";
		}
	}
	
	/**
	 * Posts a board message to the specified users profile
	 * 
	 * @param userId - The String content of the ID for the user who is to receive the board message
	 * @param message - The String content of the message to be posted to the users profile
	 * @return - The ID of the board message
	 */
	public String post_Message_User(String userId, String message){
	
		log.info("INFO: " + getDesplayName() + " will now post a board message to the user with ID: " + userId);
		
		// Create the URL to POST the data to
		String postURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/");
		postURL += userId + "/@all";
		log.info("INFO: The URL to send the POST request to has been created: " + postURL);
		
		// Create the JSON content to be POSTed to the URL
		String jsonContent = "{\"content\":\""+ message +"\"}";
		log.info("INFO: The JSON content to be POSTed has been created: " + jsonContent);
		
		String postResponse = service.createASEntry(postURL, jsonContent);
		
		log.info("INFO: Now retrieving the ID for the board message from the JSON response");
		int indexOfNote = postResponse.indexOf(".note:");
		String boardMessageId = "urn:lsid:lconn.ibm.com:profiles.note:" + postResponse.substring(indexOfNote + 6, indexOfNote + 42);
		log.info("INFO: The board message ID has been retrieved as: " + boardMessageId); 
		
		return boardMessageId;
	}

	/**
	 * Posts a status update to the specified community
	 * 
	 * @param commUuid - The String content containing the UUID of the community to which to post the status update
	 * @param message - The String content containing the status update to be posted to the community
	 * @return - The ID of the community status update
	 */
	public String post_Message_Community(String commUuid, String message){
	
		log.info("INFO: Now posting a community status update with content: " + message);
		
		// Create the URL to POST the community status update to
		String commID = "urn:lsid:lconn.ibm.com:communities.community:" + commUuid;
		String postRequestURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/");
		postRequestURL += commID + "/@all";
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		// Create the JSON content to be sent with the POST request
		String jsonContent = "{\"content\":\"" + message + "\"}";
		log.info("INFO: The JSON content to be sent with the POST request has been created: " + jsonContent);
		
		String jsonResponse = service.createASEntry(postRequestURL, jsonContent);
		log.info("INFO: The JSON response has been received from the server: " + jsonResponse);
		
		String preStatusUpdateId = "urn:lsid:lconn.ibm.com:communities.note:";
		int statusUpdateIdStartIndex = jsonResponse.indexOf(preStatusUpdateId) + preStatusUpdateId.length();
		String communityUpdateID = jsonResponse.substring(statusUpdateIdStartIndex, statusUpdateIdStartIndex + 36);
		 
		return communityUpdateID;
	}
	
	/**
	 * Posts a status update with mention to the specified community
	 * 
	 * @param commUuid - The String content containing the UUID of the community to which to post the status update
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - The ID of the community status update
	 */
	public String addMentionsStatusUpdate(String commUuid,Mentions mentions){

		log.info("INFO: Community status update will now be posted with mentions to: " + mentions.getUserToMention().getDisplayName());
		
		// Create the URL to which the POST request will be sent
		String commID = "urn:lsid:lconn.ibm.com:communities.community:" + commUuid;
		String postRequestURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/");
		postRequestURL += commID + "/@all";
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		// Create the JSON content to POST to the server
		String jsonContent = "{\"content\":\"" + mentions.getBeforeMentionText() + " <span class=\\\"vcard\\\"><span class=\\\"fn\\\">@" + mentions.getUserToMention().getDisplayName() + "</span><span class=\\\"x-lconn-userid\\\">" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + " \"}"; 
		log.info("INFO: The JSON content for the status update with mentions has been created: " + jsonContent);
	
		String jsonResponse = service.createASEntry(postRequestURL, jsonContent);
		String preStatusUpdateId = "urn:lsid:lconn.ibm.com:communities.note:";
		int statusUpdateIdStartIndex = jsonResponse.indexOf(preStatusUpdateId) + preStatusUpdateId.length();
		String communityUpdateID = jsonResponse.substring(statusUpdateIdStartIndex, statusUpdateIdStartIndex + 36);
		
		return communityUpdateID;
	}
	
	/**
	 * Posts a status update with 2 mentions to the specified community
	 * 
	 * @param commUuid - The String content containing the UUID of the community to which to post the status update
	 * @param mentions1 - The Mentions instance of the first user to be mentioned, beforeMentionText will be used for this combined mention
	 * @param mentions2 - The Mentions instance of the second user to be mentioned, afterMentionText will be used for this combined mention
	 * @return - The ID of the community status update
	 */
	public String addTwoMentionsStatusUpdate(String commUuid,Mentions mentions1,Mentions mentions2){

		log.info("INFO: Community status update will now be posted with mentions to: " + mentions1.getUserToMention().getDisplayName() + " and " + mentions2.getUserToMention().getDisplayName());
		
		// Create the URL to which the POST request will be sent
		String commID = "urn:lsid:lconn.ibm.com:communities.community:" + commUuid;
		String postRequestURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/");
		postRequestURL += commID + "/@all";
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		// Create the JSON content to POST to the server
		String jsonContent = "{\"content\":\"" + mentions1.getBeforeMentionText() + " <span class=\\\"vcard\\\"><span class=\\\"fn\\\">@" + mentions1.getUserToMention().getDisplayName() + "</span><span class=\\\"x-lconn-userid\\\">" + mentions1.getUserUUID() + "</span></span>"; 
		jsonContent = jsonContent + " <span class=\\\"vcard\\\"><span class=\\\"fn\\\">@" + mentions2.getUserToMention().getDisplayName() + "</span><span class=\\\"x-lconn-userid\\\">" + mentions2.getUserUUID() + "</span></span> " + mentions2.getAfterMentionText() + " \"}";
		log.info("INFO: The JSON content for the status update with 2 mentions has been created: " + jsonContent);
	
		String jsonResponse = service.createASEntry(postRequestURL, jsonContent);
		String preStatusUpdateId = "urn:lsid:lconn.ibm.com:communities.note:";
		int statusUpdateIdStartIndex = jsonResponse.indexOf(preStatusUpdateId) + preStatusUpdateId.length();
		String communityUpdateID = jsonResponse.substring(statusUpdateIdStartIndex, statusUpdateIdStartIndex + 36);
		
		return communityUpdateID;
	}
	
	public String postComment(String updateID, String message){
		
		String URL1 = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@all/@all/");
		String URL = URL1  + updateID + "/comments";
		String post = "{\"content\":\""+ message +"\"}";
		String result = service.createASEntry(URL, post);
		
		//abstract ID of status update from the returned json string. This will be returned and used to identify the status update should commenting/liking be required
		 int test = result.indexOf("urn:lsid:lconn.ibm.com:profiles.comment");
		 String commentID1 = result.substring(test + 40, test + 76);
		 
		 String commentID = "urn:lsid:lconn.ibm.com:profiles.comment:" + commentID1;
		 return commentID;
		
		
		
	}

	/**
	 * This method is required to add comments to community status updates.
	 * The like method (below) can be used for liking the comments produced
	 * 
	 * @param communityUpdateID - The unique ID of the community status update
	 * @param comment - The comment which will be added to the community status update
	 * @return - A String object which is the unique ID of the community status update comment
	 */
	public String postCommunityComment(String communityUpdateID, String comment){
		
		String URL1 = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@all/@all/");
		String URL = URL1 + "urn:lsid:lconn.ibm.com:communities.note:" + communityUpdateID + "/comments";
		String post = "{\"content\":\""+ comment +"\"}";
		String result = service.createASEntry(URL, post);
		
		//abstract ID of status update from the returned json string. This will be returned and used to identify the comment should liking be required
		 int test = result.indexOf("urn:lsid:lconn.ibm.com:communities.comment");
		 String commentID1 = result.substring(test + 43, test + 79);
		 
		 String commentID = "urn:lsid:lconn.ibm.com:communities.comment:" + commentID1;
		 return commentID;
		
	}
	
	//MicroBlogID can be the ID of any status update or comment
	public void like(String microBlogID){
		
		String URL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@all/@all/" + microBlogID + "/likes" );
		String post = "";
		
		service.createASEntry(URL, post);
		
	}
	
	public boolean unlike(String microBlogID){
		
		String URL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@all/@all/" + microBlogID + "/likes" );
		
		boolean deleted = service.deleteUrlFeed(URL);
		
		return deleted;
		
	}
	
	public void followUser(String userID){

		Entry newEntry = Abdera.getNewFactory().newEntry();
	
		newEntry.addCategory(StringConstants.SCHEME_TYPE, "resource-follow", "");
		newEntry.addCategory(StringConstants.SCHEME_SOURCE, "profiles", "profiles");
		newEntry.addCategory(StringConstants.SCHEME_RESOURCE_TYPE, "profile", "profile");
		newEntry.addCategory(StringConstants.SCHEME_RESOURCE_ID,userID, "");
		newEntry.addCategory(StringConstants.SCHEME_FLAGS, "following", "Following");

		service.postFeed(service.getServiceURLString() + "/follow/atom/resources?source=profiles&amp;type=profile", newEntry);
	
	}
	
	/**
	 * Allows any user to unfollow any other user
	 * 
	 * @param userProfile1 - The user who will unfollow the user specfied in userProfile2
	 * @param userProfile2 - The user to be unfollowed by the user specified in userProfile1
	 * @return - True if the unfollow operation is successful, false otherwise
	 */
	public boolean unfollowUser(APIProfilesHandler userProfile1, APIProfilesHandler userProfile2) {
		
		log.info("INFO: " + userProfile1.getDesplayName() + " will now unfollow " + userProfile2.getDesplayName());
		
		// Retrieve the feed of all profiles followed by this user and retrieve all entries from the feed
		Feed followedProfilesFeed = (Feed) service.getFollowedProfiles();
		List<Entry> listOfFollowedProfiles = followedProfilesFeed.getEntries();
		
		// Loop through the entries - locate the entry representing the user to be unfollowed
		String unfollowURL = null;
		boolean foundUser2Profile = false;
		int index = 0;
		while(index < listOfFollowedProfiles.size() && foundUser2Profile == false) {
			Entry followedProfile = listOfFollowedProfiles.get(index);
			
			// Retrieve the edit link for this followed profile and verify whether the profile belongs to the user to be followed
			String editLinkHref = followedProfile.getEditLink().getHref().toString();
			if(editLinkHref.indexOf("resource=" + userProfile2.getUUID()) > -1) {
				unfollowURL = editLinkHref;
				foundUser2Profile = true;
			}
			index ++;
		}
		
		if(!foundUser2Profile) {
			log.info("ERROR: The profile to be unfollowed could not be found in the feed of all followed profiles for the user with username: " + userProfile1.getDesplayName());
			log.info(followedProfilesFeed.toString());
			return false;
		}
		log.info("INFO: The URL to which the DELETE request will be sent has been retrieved: " + unfollowURL);
		
		boolean unfollowedProfile = service.deleteAnyFeed(unfollowURL);
		
		if(unfollowedProfile) {
			log.info("INFO: " + userProfile1.getDesplayName() + " has successfully unfollowed " + userProfile2.getDesplayName());
			return true;
		} else {
			log.info("ERROR: The user with username " + userProfile2.getDesplayName() + " could not be unfollowed by " + userProfile1.getDesplayName());
			log.info(followedProfilesFeed.toString());
			return false;
		}
	}
	
	public boolean deleteSUComment(String updateID, String commentID){

		String URL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/@all/@all/" + updateID + "/comments/" + commentID);
		
		boolean deleted = service.deleteUrlFeed(URL);
		
		return deleted;
	}
	
	/**
	 * Deletes any single board message/status update posted on either a users own profile or to another users news feed
	 * 
	 * @param boardMessageId - The ID of the board message posted
	 * @return - True if deletion is successful, false otherwise
	 */
	public boolean deleteBoardMessage(String boardMessageId) {
		
		log.info("INFO: " + getDesplayName() + " will now delete the board message with ID: " + boardMessageId);
		
		// Create the URL to which the DELETE request will be sent
		String url = service.getServiceURLString().replace("profiles", "connections");
		url += "/opensocial/rest/ublog/@me/@all/";
		url += boardMessageId;											
		log.info("INFO: The URL to be used for deleting the board message has been created: " + url);
		
		return service.deleteUrlFeed(url);
	}
	
	/**
	 * Posts a status update with a file attachment for the current user
	 * 
	 * @param statusUpdateMessage - The message content of the status update
	 * @param fileEntry - The file to be attached to the status update 
	 * @return - Returns the status update ID
	 */
	public String postStatusUpdateWithFileAttachment(String statusUpdateMessage, FileEntry fileEntry) {
		
		log.info("INFO: Posting a status update with file attachment for " + getDesplayName());
		
		log.info("INFO: Retrieving the file library ID for " + getDesplayName());
		
		// Create the URL to retrieve the ID
		String allFilesURL = service.getServiceURLString().replace("profiles", "files") + URLConstants.FILES_MY_LIBRARY;
		log.info("INFO: User file library ID will be retrieved from " + allFilesURL);
		
		// Extract the raw ID string from the feed result
		String feed = service.getAnyFeed(allFilesURL).toString();
		String fullLibraryId = feed.substring(feed.indexOf("<id>") + 4, feed.indexOf("</id>"));
		String libraryId = fullLibraryId.substring(fullLibraryId.lastIndexOf(":") + 1);
		log.info("INFO: Library ID has been retrieved as: " + libraryId);
		
		// Set the remaining parameters required to create the status update with file attachment
		String fileId = fileEntry.getId().toString().substring(fileEntry.getId().toString().lastIndexOf(":") + 1);
		
		log.info("INFO: Setting the JSON source");
		String source = "{\"content\":\"STATUSMESSAGECONTENT.\",\"attachments\":[";
		source += "{\"author\":{\"id\":\"USERID\"},\"id\":\"FILEID\",\"displayName\":\"FILENAME\",";
		source += "\"url\":\"{files}/form/anonymous/api/library/LIBRARYID/document/FILEID/media/FILENAME\",";
		source += "\"summary\":\"\",\"published\":\"2015-09-28T13:12:39Z\",\"objectType\":\"file\",";
		source += "\"image\":{\"url\":\"{files}/form/anonymous/api/library/LIBRARYID/document/FILEID/";
		source += "thumbnail?renditionKind=largeview\"}}]}";
		
		source = source.replaceAll("STATUSMESSAGECONTENT", statusUpdateMessage);
		source = source.replaceAll("USERID", getUUID());
		source = source.replaceAll("FILEID", fileId);
		source = source.replaceAll("FILENAME", fileEntry.getTitle());
		source = source.replaceAll("LIBRARYID", libraryId);
		log.info("INFO: JSON Source will be sent to the URL as: " + source);
		
		// Create the URL to POST the JSON source to
		String postURL = service.getServiceURLString().replace("profiles", "connections/opensocial/rest/ublog/") + getUUID(); 
		log.info("INFO: JSON source will be sent to the following URL: " + postURL);
		
		String response = service.createASEntry(postURL, source);
		
		log.info("INFO: Extracting the status update ID from the JSON response");
		String statusUpdateId;
		int indexOfNote = response.indexOf("note");
		
		if(indexOfNote >= 0) {
			// Create the status update ID string
			String updateIdNumber = response.substring(indexOfNote + 5, indexOfNote + 41);
			statusUpdateId = "urn:lsid:lconn.ibm.com:profiles.note:" + updateIdNumber;
		} else {
			statusUpdateId = null;
		}
		
		log.info("INFO: Returning the status update ID as: " + statusUpdateId);
		return statusUpdateId;
	}
	
	/**
	 * Invites the specified user to join the current users network
	 * 
	 * @param userToInvite - The user that is to be invited to the network
	 * @return - The Invitation instance of the sent invite if the send request is successful, null otherwise
	 */
	public Invitation inviteUserToJoinNetwork(APIProfilesHandler userToInvite) {
		
		log.info("INFO: " + getDesplayName() + " is now inviting " + userToInvite.getDesplayName() + " to join their network");
		
		log.info("INFO: Checking the status between the two users to determine if an invite connection already exists or not");
		Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(userToInvite.getEmail(), userToInvite.getUserKey(), 
																			getEmail(), getUserKey());
		
		if(colleagueStatusEntry.toString().indexOf("resp:error=\"true\"") > -1) {
			
			log.info("INFO: No previous invitation / network connection exists. Sending an invite to " + userToInvite.getDesplayName());
			Connection connection = new Connection(Data.NETWORK_INVITATION_MESSAGE);
			Entry inviteEvent = (Entry) service.inviteColleague(connection, userToInvite.getEmail(), userToInvite.getUserKey());
			
			if(inviteEvent.toString().indexOf("resp:error=\"true\"") > -1) {
				log.info("ERROR: The invite event failed to complete successfully");
				log.info(inviteEvent.toString());
				return null;
			} else {
				log.info("INFO: The invite was successfully sent to " + userToInvite.getDesplayName());
				Invitation networkInvite = new Invitation(inviteEvent);
				networkInvite.addLink(StringConstants.REL_EDIT, StringConstants.MIME_ATOM_XML, inviteEvent.getEditLink().getHref().toString());
				
				return networkInvite;
			}
		} else {
			
			log.info("INFO: Checking if a previous invite or network connection has been set up between " + getDesplayName() + " and " + userToInvite.getDesplayName());
			boolean previousInvite = false;
			if(colleagueStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0).getTerm().equals("pending") == true) {
				log.info("INFO: A previous pending invitation has already been sent by " + getDesplayName() + " to " + userToInvite.getDesplayName());
				previousInvite = true;
				
			} else if(colleagueStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0).getTerm().equals("accepted") == true) {
				log.info("INFO: " + userToInvite.getDesplayName() + " is already part of " + getDesplayName() + "'s network");
				previousInvite = true;
			}
			if(previousInvite == true) {
				
				// Re-create the invitation with all relevant data
				Invitation networkInvite = new Invitation(userToInvite.getEmail(), userToInvite.getUUID(),
															"Invitation to join " + getDesplayName() + "'s network",
															"Please join my network");
				networkInvite.addLink(StringConstants.REL_EDIT, StringConstants.MIME_ATOM_XML, colleagueStatusEntry.getEditLink().getHref().toString()
										.replaceAll("&inclMessage=true", "").trim());
				
				log.info("INFO: The existing invitation has been successfully re-created");
				return networkInvite;
				
			} else {
				log.info("INFO: Cannot send invite request to " + userToInvite.getDesplayName());
				log.info(colleagueStatusEntry.toString());
				return null;
			}
		}
	}
	
	/**
	 * Accepts a sent invitation to join a users network
	 * 
	 * @param inviteEntry - The Entry instance of the invite that was sent to the user
	 * @param userThatSentInvite - The APIProfilesHandler instance of the user that sent the invite (not the receiver of the invite)
	 * @return - True if the accept invite process is successful, false otherwise
	 */
	public boolean acceptNetworkInvitation(Invitation inviteEvent, APIProfilesHandler userThatSentInvite) {
		
		log.info("INFO: " + getDesplayName() + " is accepting the invitation to join " + userThatSentInvite.getDesplayName() + "'s network");
		
		// Retrieve the colleague status between the current user and the user that sent the invitation
		Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(getEmail(), getUserKey(), 
																			userThatSentInvite.getEmail(), 
																			userThatSentInvite.getUserKey());
		
		if(colleagueStatusEntry.toString().indexOf("resp:error=\"true\"") > -1) {
			log.info("ERROR: No existing invitation sent from " + userThatSentInvite.getDesplayName() + " could be found for " + getDesplayName());
			log.info(colleagueStatusEntry.toString());
			return false;
		} else {
			if(colleagueStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0).getTerm().equals("pending") == true) {
				
				// Edit the entry - mark it as having accepted the invitation
				colleagueStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0).setTerm("accepted");
				
				// Send the accepted invitation entry to the edit URL of the original invite
				Entry acceptedInviteEntry = (Entry) service.acceptInvite(inviteEvent.getEditLink(), colleagueStatusEntry);
				
				if(acceptedInviteEntry.toString().indexOf("resp:error=\"true\"") == -1) {
					log.info("INFO: The invitation was accepted successfully by " + getDesplayName());
					return true;
				} else {
					log.info("ERROR: There was a problem with accepting the invitation");
					log.info(acceptedInviteEntry.toString());
					return false;
				}
			} else {
				log.info("INFO: The invitation status was not pending and therefore could not be accepted");
				log.info(colleagueStatusEntry.toString());
				return false;
			}
		}
	}
	
	/**
	 * Removes the specified user from the current users network connections list
	 * 
	 * @param userToBeRemoved - The APIProfilesHandler instance of the user who is to be removed
	 * @return - True if the removal operation is successful, false otherwise
	 */
	public boolean deleteUserFromNetworkConnections(APIProfilesHandler userToBeRemoved) {
		
		log.info("INFO: Removing the user with username " + userToBeRemoved.getDesplayName() + " from the network connections list owned by " + getDesplayName());
		
		// Retrieve the list of entries of all network connections for this user
		Feed colleaguesFeed = (Feed) service.getColleagueFeed(getEmail(), getUserKey());
		List<Entry> listOfColleagueEntries = colleaguesFeed.getEntries();
		
		// Loop through all entries until the specified user is found - delete them if found
		int index = 0;
		boolean foundUser = false;
		boolean deleted = false;
		while(index < listOfColleagueEntries.size() && foundUser == false) {
			Entry currentUser = listOfColleagueEntries.get(index);
			
			if(currentUser.toString().indexOf(userToBeRemoved.getEmail()) > -1) {
				log.info("INFO: " + userToBeRemoved.getDesplayName() + " has been successfully found in the network connections list for " + getDesplayName());
				foundUser = true;
				
				log.info("INFO: Removing " + userToBeRemoved.getDesplayName() + " from the network connections list");
				deleted = service.removeContact(currentUser.getEditLink().getHref().toString());
			}
			index ++;
		}
		
		if(foundUser == false) {
			log.info("ERROR: " + userToBeRemoved.getDesplayName() + " could not be found in the network connections list for " + getDesplayName());
		}
		else if(deleted == false) {
			log.info("ERROR: " + userToBeRemoved.getDesplayName() + " was found but could not be removed from the network connections list for " + getDesplayName());
		} else {
			log.info("INFO: " + userToBeRemoved.getDesplayName() + " was successfully removed from " + getDesplayName() + "'s network connections list");
		}
		return deleted;
	}
	
	/**
	 * Retrieves the key for the current user
	 * This method has Private access for now since it is not required for any external classes
	 * 
	 * @return userKey - Returns the key for the current user
	 */
	private String getUserKey() {
		
		log.info("INFO: Retrieving the key for user with username: " + getDesplayName());
		String userKey = service.getUserVCard().getVCardFields().get(StringConstants.VCARD_PROFILE_KEY);
		
		log.info("INFO: Returning the key for " + getDesplayName() + " as " + userKey);
		return userKey;
	}
}