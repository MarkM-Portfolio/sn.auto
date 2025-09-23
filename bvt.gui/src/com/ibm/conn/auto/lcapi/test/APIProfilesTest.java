package com.ibm.conn.auto.lcapi.test;

import java.io.File;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class APIProfilesTest extends SetUpMethods2{

	
	private static Logger log = LoggerFactory.getLogger(APIProfilesTest.class);
	private TestConfigCustom cfg;	
	private User testUser,otherUser;
	private String testURL;

	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		otherUser = cfg.getUserAllocator().getUser(this);
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		
		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		config = new ServiceConfig(client, testURL, true);
		
		ServiceEntry communities = config.getService("communities");
		assert(communities != null);

		Utils.addServiceAdminCredentials(communities, client);
				
	}
	
	@Test(groups={"apitest"})
	public void API_PostMessage(){
		
	
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		apiHandler.postBoardMessage("TestMessage");
		
	}
	@Test(groups={"apitest"})
	public void API_PostStatusUpdate(){
		
		log.info("INFO: " + testUser.getDisplayName() + " posting status update");
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		apiHandler.postStatusUpdate("Status Update Test");
		
		
		
	}
	@Test(groups={"apitest"})
	public void API_PostMessage_ToOtherUser(){
		
		log.info("INFO: " + testUser.getDisplayName() + " posting message to " + otherUser.getDisplayName());
		
		APIProfilesHandler user2 = new APIProfilesHandler(testURL,otherUser.getUid(),otherUser.getPassword());
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		apiHandler.post_Message_User(user2.getUUID(), "Message to other user test");
		
		
		
	}
	@Test(groups={"apitest"})
	public void API_PostMessage_ToCommunity(){
		
		APICommunitiesHandler comAPI = new APICommunitiesHandler(testURL,testUser.getUid(),testUser.getPassword());
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		 BaseCommunity baseCom = new BaseCommunity.Builder("Community message post test" + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase ")
		   										   .build();
		
		//Community created
		Community newCommunity = baseCom.createAPI(comAPI);
	
		baseCom.setCommunityUUID(comAPI.getCommunityUUID(newCommunity)); 
		
		//post status update to community
		log.info("INFO: " + testUser.getDisplayName() + " posting status update to their community(API)");
		
		
		apiHandler.post_Message_Community(baseCom.getCommunityUUID().replace("communityUuid=", ""), "Community post message test");
		
		
		
	}
	@Test (groups ={"apitest"})
	public void API_Comment_StatusUpdate(){
		
		log.info("INFO: " + testUser.getDisplayName() + " adding comment to status update");
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		String updateID = apiHandler.postStatusUpdate("Status Update Test");
		
		apiHandler.postComment(updateID, "Comment test");
		
		
	}
	
	@Test (groups ={"apitest"})
	public void API_Comment_CommunityStatusUpdate(){

		APICommunitiesHandler comAPI = new APICommunitiesHandler(testURL,testUser.getUid(),testUser.getPassword());
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseCommunity baseCom = new BaseCommunity.Builder("Community message post test" + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase ")
		   										   .build();
		
		log.info("INFO: " + testUser.getDisplayName() + " creating community");
		Community newCommunity = baseCom.createAPI(comAPI);
	
		baseCom.setCommunityUUID(comAPI.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser.getDisplayName() + " posting status update to their community(API)");
		String updateID = apiHandler.post_Message_Community(baseCom.getCommunityUUID().replace("communityUuid=", ""), "Community post message test");

		log.info("INFO: " + testUser.getDisplayName() + " adding comment to community status update");
		String commentID = apiHandler.postCommunityComment(updateID, "Community Comment test");
		
		assert commentID != null: "Creation of community status update comment failed";
		
		assert commentID.contains("urn:lsid:lconn.ibm.com:communities.comment:"): "Community status update comment is malformed";
		
		
	}
	
	@Test (groups ={"apitest"})
	public void API_FollowUser(){
		
		APIProfilesHandler user2 = new APIProfilesHandler(testURL, otherUser.getUid(), otherUser.getPassword());
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getUid(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		log.info("INFO: " + testUser.getDisplayName() +  " following " + otherUser.getDisplayName());
		
		apiHandler.followUser(user2.getUUID());
	}
	
	@Test (groups ={"apitest"})
	public void API_UnfollowUser(){
		
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, otherUser.getAttribute(cfg.getLoginPreference()), otherUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now follow " + otherUser.getDisplayName());
		testUser1Profile.followUser(testUser2Profile.getUUID());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now unfollow " + otherUser.getDisplayName());
		boolean unfollowed = testUser1Profile.unfollowUser(testUser1Profile, testUser2Profile);
		
		assert unfollowed == true : "ERROR: There was a problem with unfollowing another user using the API";
	}
	
	@Test(groups={"apitest"})
	public void API_unlikeMicroblog(){
		
		log.info("INFO: " + testUser.getDisplayName() + " posting status update");
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		String microBlogID = apiHandler.postStatusUpdate("Status Update Test");
		
		APIProfilesHandler user2 = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());

		log.info("INFO: " + otherUser.getDisplayName() + " liking status update");
		user2.like(microBlogID);

		log.info("INFO: " + otherUser.getDisplayName() + " unliking status update");
		boolean deleted = user2.unlike(microBlogID);
		
		assert deleted == true:"Microblog unlike action failed";
		
	}
	
	@Test (groups ={"apitest"})
	public void API_DeleteComment_StatusUpdate(){
		
		log.info("INFO: " + testUser.getDisplayName() + " adding comment to status update");
		
		APIProfilesHandler apiHandler = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());	
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		log.info("INFO: " + testUser.getDisplayName() + " post a status update");
		String updateID = apiHandler.postStatusUpdate("Status Update Test");

		APIProfilesHandler user2 = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());

		log.info("INFO: " + otherUser.getDisplayName() + " add a comment on the status update");
		String commentID = user2.postComment(updateID, "Comment test");

		log.info("INFO: " + otherUser.getDisplayName() + " delete the comment from the status update");
		boolean deleted = apiHandler.deleteSUComment(updateID, commentID);
		
		assert deleted == true: "Deletion of comment failed";
	}
	
	@Test(groups={"apitest"})
	public void API_PostMentionsStatusUpdate(){
		
		APIProfilesHandler user1 = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler user2 = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());
		
		String beforeMentionsText = Helper.genStrongRand();
		String afterMentionsText = Helper.genStrongRand();

		Mentions mentions = new Mentions.Builder(otherUser, user2.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " posting status update with a mention to " + otherUser.getDisplayName());
		String updateID = user1.addMentionsStatusUpdate(mentions);
		
		assert updateID != null: "Post of status update with mention failed";
		
		assert updateID.contains("urn:lsid:lconn.ibm.com:profiles.note:"): "UpdateID is malformed";
		
	}
	
	@Test(groups={"apitest"})
	public void API_SaveStatusMessage() {
		
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		String statusMessage = "Status Message " + Helper.genStrongRand();
		
		log.info("INFO: Posting a status message to " + testUser.getDisplayName() + "'s news feed -> " + statusMessage);
		testUser1Profile.postStatusUpdate(statusMessage);
		
		log.info("INFO: Retrieve the status update story ID from the Discover view news feed");
		String discoverStoryId = testUser1Profile.getActivityStreamStoryId(statusMessage, true);
		log.info("INFO: The Discover view story ID was identified as " + discoverStoryId);
		
		log.info("INFO: Retrieve the status update story ID from the I'm Following view news feed");
		String imFollowingStoryId = testUser1Profile.getActivityStreamStoryId(statusMessage, false);
		log.info("INFO: The I'm Following view story ID was identified as " + imFollowingStoryId);
		
		log.info("INFO: Assert that the story ID's have been retrieved correctly from both views and that they are the same");
		assert discoverStoryId.equals("ERROR") == false: "ERROR: Story ID was not retrieved correctly from the Discover view";
		assert imFollowingStoryId.equals("ERROR") == false: "ERROR: Story ID was not retrieved correctly from the I'm Following view";
		assert discoverStoryId.equals(imFollowingStoryId) == true: "ERROR: The API method has retrieved different story ID's";
		
		log.info("INFO: Now save the status update news story for " + testUser.getDisplayName());
		boolean saved = testUser1Profile.saveNewsStory(discoverStoryId);
		
		log.info("INFO: Assert that the save operation was successful");
		assert saved == true: "ERROR: There was a problem with saving the news story using the API";
	}
	
	@Test(groups = {"apitest"})
	public void API_DeleteBoardMessage() {
		
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());
		
		log.info("INFO: Posting a board message to " + testUser2Profile.getDesplayName());
		String messageToUser2Content = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String messageToUser2Id = testUser1Profile.post_Message_User(testUser2Profile.getUUID(), messageToUser2Content);
		
		log.info("INFO: Deleting the board message");
		boolean deleted = testUser1Profile.deleteBoardMessage(messageToUser2Id);
		
		log.info("INFO: Assert that the delete operation was successful");
		assert deleted == true: "ERROR: There was a problem with deleting the board message posted to " + testUser2Profile.getDesplayName() + " using the API";
	}
	
	@Test(groups = {"apitest"})
	public void API_PostStatusUpdateWithFileAttachment() {
		
		// Create the users for the API test
		APIFileHandler fileOwner = new APIFileHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		// Set up the file name, path and File object 
		String fileName = "Desert" + Helper.genStrongRand() + ".jpg";
		String filePath = cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), 
																							Data.getData().file1);
		File file = new File(filePath);
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " is now creating a file to be attached to the status update");
		BaseFile baseFile = new BaseFile.Builder(fileName)
										.extension(".jpg")
										.rename(fileName)
										.shareLevel(ShareLevel.EVERYONE)
										.build();
		FileEntry publicFile = fileOwner.CreateFile(baseFile, file);
		
		// Set the status message content
		String statusUpdateContent = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " is now posting the status update with file attachment");
		String statusUpdateId = testUser1Profile.postStatusUpdateWithFileAttachment(statusUpdateContent, publicFile);
		
		assert statusUpdateId != null: "ERROR: The status update ID was returned as null";
		assert statusUpdateId.equals("null") == false: "ERROR: The status update ID is not in the correct format";
		assert statusUpdateId.indexOf(".note:") >= 0: "ERROR: The status update ID is not in the correct format";
		
		log.info("INFO: Clean up now API test has completed");
		testUser1Profile.deleteBoardMessage(statusUpdateId);
		fileOwner.deleteFile(publicFile);
	}
	
	@Test(groups = {"apitest"})
	public void API_InviteUserToJoinNetwork() {
		
		// Create the users for the API test
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now send an invite to " + testUser2Profile.getDesplayName() + " to join their network");
		Invitation inviteSent = testUser1Profile.inviteUserToJoinNetwork(testUser2Profile);
		
		log.info("INFO: Verify: The invitation was successfully sent to " + testUser2Profile.getDesplayName());
		assert inviteSent != null : "ERROR: There was a problem with sending the invite to join the network";
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now repeat the invite sending process to " + testUser2Profile.getDesplayName());
		Invitation inviteRepeatEntry = testUser1Profile.inviteUserToJoinNetwork(testUser2Profile);
		
		log.info("INFO: Verify: The repeated invitation was successfully sent to " + testUser2Profile.getDesplayName());
		assert inviteRepeatEntry != null : "ERROR: There was a problem with sending a repeat invitation to join the network";
		
		log.info("INFO: " + testUser2Profile.getDesplayName() + " will now accept the invitation");
		testUser2Profile.acceptNetworkInvitation(inviteSent, testUser1Profile);
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now repeat the invite sending process for a second and final time");
		Invitation inviteRepeatAgain = testUser1Profile.inviteUserToJoinNetwork(testUser2Profile);
		
		log.info("INFO: Verify: The second repeated invitation was successfully sent to " + testUser2Profile.getDesplayName());
		assert inviteRepeatAgain != null : "ERROR: There was a problem with sending the second repeat invitation to join the network";
	
		log.info("INFO: Perform clean up now that the API test has completed");
		testUser1Profile.deleteUserFromNetworkConnections(testUser2Profile);
	}
	
	@Test(groups = {"apitest"})
	public void API_AcceptNetworkInvitation() {
		
		// Create the users for the API test
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now send an invite to " + testUser2Profile.getDesplayName() + " to join their network");
		Invitation inviteSent = testUser1Profile.inviteUserToJoinNetwork(testUser2Profile);
		
		log.info("INFO: " + testUser2Profile.getDesplayName() + " will now accept the invitation that was sent to them by " + testUser1Profile.getDesplayName());
		boolean acceptBeforeUserDeleted = testUser2Profile.acceptNetworkInvitation(inviteSent, testUser1Profile);
		
		log.info("INFO: Verify: The invitation was successfully accepted by " + testUser2Profile.getDesplayName());
		assert acceptBeforeUserDeleted == true : "ERROR: There was a problem with accepting the invite to join the network";
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now remove " + testUser2Profile.getDesplayName() + " from their network");
		testUser1Profile.deleteUserFromNetworkConnections(testUser2Profile);
		
		log.info("INFO: " + testUser2Profile.getDesplayName() + " will now try accepting the invitation again");
		boolean acceptAfterUserDeleted = testUser2Profile.acceptNetworkInvitation(inviteSent, testUser1Profile);
		
		log.info("INFO: Verify: The invitation could not be accepted by " + testUser2Profile.getDesplayName());
		assert acceptAfterUserDeleted == false : "ERROR: The accept invitation process completed without error after " + testUser2Profile.getDesplayName() + " was removed from the network";
	}
	
	@Test(groups = {"apitest"})
	public void API_DeleteUserFromNetworkConnections() {
		
		// Create the users for the API test
		APIProfilesHandler testUser1Profile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, otherUser.getEmail(), otherUser.getPassword());
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now send an invite to " + testUser2Profile.getDesplayName() + " to join their network");
		Invitation inviteSent = testUser1Profile.inviteUserToJoinNetwork(testUser2Profile);
		
		log.info("INFO: " + testUser2Profile.getDesplayName() + " will now accept the invitation that was sent to them by " + testUser1Profile.getDesplayName());
		testUser2Profile.acceptNetworkInvitation(inviteSent, testUser1Profile);
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now remove " + testUser2Profile.getDesplayName() + " from their network");
		boolean deleted = testUser1Profile.deleteUserFromNetworkConnections(testUser2Profile);
		
		log.info("INFO: Verify: " + testUser2Profile.getDesplayName() + " was removed successfully");
		assert deleted == true : "ERROR: The removal operation failed - " + testUser2Profile.getDesplayName() + " could not be removed from " + testUser1Profile.getDesplayName() + "'s network";
		
		log.info("INFO: " + testUser1Profile.getDesplayName() + " will now try to remove " + testUser2Profile.getDesplayName() + " from their network again");
		deleted = testUser1Profile.deleteUserFromNetworkConnections(testUser2Profile);
		
		log.info("INFO: Verify: The delete operation will have failed since " + testUser2Profile.getDesplayName() + " is no longer part of the network");
		assert deleted == false : "ERROR: The removal operation succeeded after " + testUser2Profile.getDesplayName() + " was removed from the network";
	}
}
