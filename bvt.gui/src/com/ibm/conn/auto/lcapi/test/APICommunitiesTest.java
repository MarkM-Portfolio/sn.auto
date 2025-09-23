package com.ibm.conn.auto.lcapi.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.FeedLink;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

public class APICommunitiesTest extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(APICommunitiesTest.class);
	private TestConfigCustom cfg;	
	private User testUser, testUser2;
	private String testURL;
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
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
	public void createCommunityAPI() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Community " + testName);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		  										 	   .tags("testTags"+ Helper.genDateBasedRand())
		  										 	   .access(Access.PUBLIC)
		  										 	   .description("Test description for testcase " + "APITest" + Helper.genDateBasedRand())
		  										 	   .build();
		
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		Community publicCommunity = apiHandler.createCommunity(baseCommunity);

		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void createBookmarkAPI() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Community " + testName);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		  										 	   .tags("testTags" + Helper.genDateBasedRand())
		  										 	   .access(Access.PUBLIC)
		  										 	   .description("Test description for testcase " + "APITest" + Helper.genDateBasedRand())
		  										 	   .build();
		
		BaseDogear bookmark = new BaseDogear.Builder("Bookmark test for homepage regression testing"+ Helper.genDateBasedRand() , "http://www.test.com/doesnotexist")
											.tags("bmtagincommunity"+ Helper.genDateBasedRand())
											.community(baseCommunity)
											.description("This is a bookmark within a community"+ Helper.genDateBasedRand())
											.build();
		
		
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		Community publicCommunity = apiHandler.createCommunity(baseCommunity);

		apiHandler.createBookmark(bookmark);

		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(publicCommunity);
	}
	
	
	@Test(groups={"apitest"})
	public void createFeedAPI() {

		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Community " + testName);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		  										 	   .tags("testTags" + Helper.genDateBasedRand())
		  										 	   .access(Access.PUBLIC)
		  										 	   .description("Test description for testcase " + "APITest" + Helper.genDateBasedRand())
		  										 	   .build();
		
		BaseFeed baseFeed1 = new BaseFeed.Builder("Feed Title1", "http://rss.cnn.com/rss/cnn_topstories.rss")
										 .description("feed description")
										 .build();
		
		BaseFeed baseFeed2 = new BaseFeed.Builder("Feed Title2", "http://news.google.com/?output=rss")
										 .description("feed description")
										 .build();
		
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		Community community = apiHandler.createCommunity(baseCommunity);
		
		//Add the feeds widget	
		apiHandler.addWidget(community, BaseWidget.FEEDS);
		
		FeedLink feed1 = apiHandler.createFeed(community, baseFeed1);
		assert feed1 != null : "Add feed to community failed";
		
		FeedLink feed2 = apiHandler.createFeed(community, baseFeed2);
		assert feed2 != null : "Add feed to community failed";
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(community);
	}
	
	@Test(groups = {"apitest"})
	public void api_EditFeedDescription() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communitiesAPIUser1 = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genStrongRand())
		  										 	   .tags(Data.getData().commonTag + Helper.genStrongRand())
		  										 	   .access(Access.PUBLIC)
		  										 	   .description(Data.getData().commonDescription + Helper.genStrongRand())
		  										 	   .build();
		Community publicCommunity = communitiesAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: Now adding the Feeds widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.FEEDS);
		
		log.info("INFO: Now creating a new feed in the community");
		BaseFeed baseFeed = new BaseFeed.Builder(testName + Helper.genStrongRand(), Data.getData().FeedsURL_API)
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.build();
		FeedLink communityFeed = communitiesAPIUser1.createFeed(publicCommunity, baseFeed);
		
		assert communityFeed != null : "ERROR: There was a problem with creating a community feed using the API";
		
		log.info("INFO: Now editing the description for the community feed");
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		FeedLink updatedFeed = communitiesAPIUser1.editFeedDescription(communityFeed, editedDescription);
		
		assert updatedFeed.getContent().trim().equals(editedDescription) : "ERROR: The description of the community feed could NOT be updated using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void addWidgetAPI() {

		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Community " + testName);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		  										 	   .tags("testTags"+ Helper.genDateBasedRand())
		  										 	   .access(Access.PUBLIC)
		  										 	   .description("Test description for testcase " + "APITest" + Helper.genDateBasedRand())
		  										 	   .build();
		
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		Community community = apiHandler.createCommunity(baseCommunity);
		
		//Add the feeds widget	
		apiHandler.addWidget(community, BaseWidget.FEEDS);		
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(community);
	}
	
	@Test (groups = {"apitest"})
	public void createIdea(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		


		//Build the community to be created later
		log.info("INFO: Creating Community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										   .tags("testTags"+ Helper.genDateBasedRand())
   										   .access(Access.PUBLIC)
   										   .description("Test description for testcase " + testName)
   										   .build();
		//Instantiate APIHandler
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());


		//Community created
		Community newCommunity = baseCom.createAPI(apiHandler);
		

		//blog widget added to community
		baseCom.addWidgetAPI(newCommunity,apiHandler, BaseWidget.IDEATION_BLOG);


		//Build the Base Blog Post to be created later.
		BaseBlogPost newBaseBlogPost= new BaseBlogPost.Builder("Add Idea Test")
											.tags("testTags"+Helper.genDateBasedRand()).content("content" + Helper.genDateBasedRand()).allowComments(true)
											.numDaysCommentsAllowed(5).complete(true)
											.build();
		
		

		//API Code for creating BlogPost in Community
		BlogPost result = apiHandler.createIdea(newBaseBlogPost, newCommunity);
		assert result != null: "Add idea to community failed";
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(newCommunity);
	}
	
	@Test (groups = {"apitest"})
	public void createCommunityBlogEntryMentions(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Build the community to be created later
		log.info("INFO: Creating Community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										   .tags("testTags"+ Helper.genDateBasedRand())
   										   .access(Access.PUBLIC)
   										   .description("Test description for testcase " + testName)
   										   .build();
		
		//Instantiate APIHandler
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		//Community created
		Community community = baseCom.createAPI(apiHandler);
		
		//blog widget added to community
		baseCom.addWidgetAPI(community, apiHandler, BaseWidget.BLOG);
		
		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());

		//Build the Base Blog Post to be created later.
		BaseBlogPost newBaseBlogPost= new BaseBlogPost.Builder("Create Blog Entry Mentions test")
											.tags("testTags"+Helper.genDateBasedRand())
											.content("content" + Helper.genDateBasedRand())
											.allowComments(true)
											.numDaysCommentsAllowed(5).complete(true)
											.build();

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();
		
		log.info("INFO: Creating a Mentions object");
		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();

		//API Code for creating BlogPost with mentions in Community
		BlogPost result = apiHandler.createCommunityBlogEntryMentions(newBaseBlogPost, apiHandler.getCommunity(community), mentions);
		
		assert result != null: "Creation of blog entry mentions in community failed";
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(community);
	}
	
	@Test (groups = {"apitest"})
	public void createCommunityForumTopicMentions(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
										   .tags(Data.getData().commonTag + Helper.genStrongRand())
   										   .access(Access.PUBLIC)
   										   .description(Data.getData().commonDescription + Helper.genStrongRand())
   										   .build();
		
		//Instantiate APIHandler
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		log.info("INFO: " + testUser.getDisplayName() + " creating community");
		Community community = baseCom.createAPI(apiHandler);

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(testName + Helper.genStrongRand())
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.build();

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());

		String beforeMentionsText = Helper.genStrongRand();
		String afterMentionsText = Helper.genStrongRand();
		
		log.info("INFO: " + testUser.getDisplayName() + " creating a Mentions object");
		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();

		//API Code for creating Forum topic with mentions in Community
		ForumTopic result = apiHandler.createForumTopicMentions(community, baseTopic, mentions);
		
		assert result != null: "Creation of forum topic mentions in community failed";
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(community);
		
	}

	@Test (groups = {"apitest"})
	public void getWidgetID(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
   										   .access(Access.PUBLIC)
   										   .description(testName)
   										   .build();
		
		log.info("INFO: Instantiating APICommunitiesHandler object");
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: API user: " + testUser.getDisplayName());

		log.info("INFO: " + testUser.getDisplayName() + " creating Community");
		Community community = baseCom.createAPI(apiHandler);
		
		String widgetID = apiHandler.getWidgetID(ForumsUtils.getCommunityUUID(apiHandler.getCommunityUUID(community)),"Forum");
		log.info("Forums Widget iD is: " + widgetID);
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(community);
	}
	
	@Test(groups = {"apitest"})
	public void api_PostStatusUpdateCommentOnUpdate_LikeComment_DeleteComment() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler testUserProfile = new APIProfilesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .description(testName)
												   .build();
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Posting a status update to the community");
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = communityOwner.addStatusUpdate(publicCommunity, statusUpdate);
		
		assert statusUpdateId.equals("") == false: "ERROR: Status update API method returned an ID of an empty string";
		assert statusUpdateId.equals("null") == false: "ERROR: Status update API method returned an ID of a string containing 'null'";
		assert statusUpdateId != null: "ERROR: Status update API method returned null";
		
		log.info("INFO: Commenting on the status update");
		String statusComment = Data.getData().commonComment + Helper.genStrongRand();
		String statusCommentId = communityOwner.commentOnStatusUpdate(statusUpdateId, statusComment);
		
		assert statusCommentId.equals("") == false: "ERROR: Comment on status update API method returned an ID of an empty string";
		assert statusCommentId.equals("null") == false: "ERROR: Comment on status update API method returned an ID of a string containing 'null'";
		assert statusCommentId != null: "ERROR: Comment on status update API method returned null";
		
		log.info("INFO: Liking the comment");
		communityOwner.likeStatusComment(testUserProfile, statusCommentId);
		
		log.info("INFO: Deleting the comment");
		boolean deleted = communityOwner.deleteStatusComment(statusUpdateId, statusCommentId);
		
		assert deleted == true: "ERROR: The operation to delete the comment from the status update failed";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void createRestrictedButListedCommunity() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Community " + testName);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		  										 	   .tags("testTags"+ Helper.genDateBasedRand())
		  										 	   .access(Access.RESTRICTED)
		  										 	   .rbl(true)
		  										 	   .shareOutside(false)
		  										 	   .description("Test description for testcase " + "APITest" + Helper.genDateBasedRand())
		  										 	   .build();
		
		APICommunitiesHandler apiHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		Community result = apiHandler.createCommunity(baseCommunity);
		assert result != null: "Creation of restricted but listed community failed";
		
		log.info("INFO: API test completed - cleaning up");
		apiHandler.deleteCommunity(result);
	}
	
	@Test(groups = {"apitest"})
	public void api_SendCommunityInvitation() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler userToInvite = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseComPub = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .description(testName)
												   .build();
		Community publicCommunity = communityOwner.createCommunity(baseComPub);
		
		log.info("INFO: Now creating a new moderated community");
		BaseCommunity baseComMod = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
												   .description(testName)
												   .build();
		Community moderatedCommunity = communityOwner.createCommunity(baseComMod);
		
		log.info("INFO: Now creating a new restricted community");
		BaseCommunity baseComRes = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .description(testName)
												   .shareOutside(false)
												   .build();
		Community restrictedCommunity = communityOwner.createCommunity(baseComRes);
		
		log.info("INFO: Inviting " + userToInvite.getDesplayName() + " to join the public community");
		Invitation pubInvitation = communityOwner.inviteUserToJoinCommunity(publicCommunity, userToInvite);
		
		log.info("INFO: Inviting " + userToInvite.getDesplayName() + " to join the moderated community");
		Invitation modInvitation = communityOwner.inviteUserToJoinCommunity(moderatedCommunity, userToInvite);
		
		log.info("INFO: Inviting " + userToInvite.getDesplayName() + " to join the restricted community");
		Invitation resInvitation = communityOwner.inviteUserToJoinCommunity(restrictedCommunity, userToInvite);
		
		log.info("INFO: Assert that the public community invitation request did not fail");
		assert pubInvitation != null : "ERROR: Sending the invitation to the public community failed";
		
		log.info("INFO: Assert that the moderated community invitation request did not fail");
		assert modInvitation != null : "ERROR: Sending the invitation to the moderated community failed";
		
		log.info("INFO: Assert that the restricted community invitation request did not fail");
		assert resInvitation != null : "ERROR: Sending the invitation to the restricted community failed";
		
		log.info("INFO: Now delete each of the communities so as the next invitations sent will fail (also part of test cleanup)");
		communityOwner.deleteCommunity(publicCommunity);
		communityOwner.deleteCommunity(moderatedCommunity);
		communityOwner.deleteCommunity(restrictedCommunity);
		
		log.info("INFO: Now try inviting " + userToInvite.getDesplayName() + " to join the deleted public community");
		pubInvitation = communityOwner.inviteUserToJoinCommunity(publicCommunity, userToInvite);
		
		log.info("INFO: Now try inviting " + userToInvite.getDesplayName() + " to join the deleted moderated community");
		modInvitation = communityOwner.inviteUserToJoinCommunity(moderatedCommunity, userToInvite);
		
		log.info("INFO: Now try inviting " + userToInvite.getDesplayName() + " to join the deleted restricted community");
		resInvitation = communityOwner.inviteUserToJoinCommunity(restrictedCommunity, userToInvite);
		
		log.info("INFO: Assert that the public community invitation request failed");
		assert pubInvitation == null : "ERROR: The send invitation request was successful after the public community was deleted";
		
		log.info("INFO: Assert that the moderated community invitation request failed");
		assert modInvitation == null : "ERROR: The send invitation request was successful after the moderated community was deleted";
		
		log.info("INFO: Assert that the restricted community invitation request failed");
		assert resInvitation == null : "ERROR: The send invitation request was successful after the restricted community was deleted";
	}
	
	@Test(groups = {"apitest"})
	public void api_RevokeSentCommunityInvitation() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APIProfilesHandler userToInvite = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		log.info("INFO: Now creating a new moderated community");
		BaseCommunity baseComMod = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
												   .description(testName)
												   .build();
		Community moderatedCommunity = communityOwner.createCommunity(baseComMod);
		
		log.info("INFO: Now creating a new restricted community");
		BaseCommunity baseComRes = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .description(testName)
												   .shareOutside(false)
												   .build();
		Community restrictedCommunity = communityOwner.createCommunity(baseComRes);
		
		log.info("INFO: Inviting " + userToInvite.getDesplayName() + " to join the moderated community");
		Invitation modInvitation = communityOwner.inviteUserToJoinCommunity(moderatedCommunity, userToInvite);
		
		log.info("INFO: Inviting " + userToInvite.getDesplayName() + " to join the restricted community");
		Invitation resInvitation = communityOwner.inviteUserToJoinCommunity(restrictedCommunity, userToInvite);
		
		log.info("INFO: Assert that the moderated community invitation request did not fail");
		assert modInvitation != null : "ERROR: Sending the invitation to the moderated community failed";
		
		log.info("INFO: Assert that the restricted community invitation request did not fail");
		assert resInvitation != null : "ERROR: Sending the invitation to the restricted community failed";
		
		log.info("INFO: Now revoking the moderated community invitation that was sent to " + userToInvite.getDesplayName());
		boolean modRevoked = communityOwner.revokeCommunityInvitation(modInvitation);
		
		log.info("INFO: Now revoking the restricted community invitation that was sent to " + userToInvite.getDesplayName());
		boolean resRevoked = communityOwner.revokeCommunityInvitation(resInvitation);
		
		log.info("INFO: Assert that the revoking of the moderated community invitation was successful");
		assert modRevoked == true : "ERROR: The request to revoke the moderated community invitation failed";
		
		log.info("INFO: Assert that the revoking of the restricted community invitation was successful");
		assert resRevoked == true : "ERROR: The request to revoke the restricted community invitation failed";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(moderatedCommunity);
		communityOwner.deleteCommunity(restrictedCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_RequestToJoinCommunity() {
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getEmail(), testUser.getPassword());
		APICommunitiesHandler userSendingRequest = new APICommunitiesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseComPub = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .description(testName)
												   .build();
		Community publicCommunity = communityOwner.createCommunity(baseComPub);
		
		log.info("INFO: Now creating a new moderated community");
		BaseCommunity baseComMod = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
												   .description(testName)
												   .build();
		Community moderatedCommunity = communityOwner.createCommunity(baseComMod);
		
		log.info("INFO: Now creating a new restricted community");
		BaseCommunity baseComRes = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .description(testName)
												   .shareOutside(false)
												   .build();
		Community restrictedCommunity = communityOwner.createCommunity(baseComRes);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now send requests to join each of the communities");
		String requestToJoinMessage = "Please allow me to join your community.";
		
		log.info("INFO: Sending request to join the public community");
		boolean publicRequest = userSendingRequest.requestToJoinCommunity(publicCommunity, requestToJoinMessage);
		
		log.info("INFO: Sending request to join the moderated community");
		boolean moderatedRequest = userSendingRequest.requestToJoinCommunity(moderatedCommunity, requestToJoinMessage);
		
		log.info("INFO: Sending request to join the restricted community");
		boolean restrictedRequest = userSendingRequest.requestToJoinCommunity(restrictedCommunity, requestToJoinMessage);
		
		log.info("INFO: Assert that the request to join the public community failed to send");
		assert publicRequest == false : "ERROR: The request to join the public community was successfully sent";
		
		log.info("INFO: Assert that the request to join the moderated community was successfully sent");
		assert moderatedRequest == true : "ERROR: The request to join the moderated community failed to send";
		
		log.info("INFO: Assert that the request to join the restricted community failed to send");
		assert restrictedRequest == false : "ERROR: The request to join the restricted community was successfully sent";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
		communityOwner.deleteCommunity(moderatedCommunity);
		communityOwner.deleteCommunity(restrictedCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_RemoveMemberFromCommunity() {
		
		// Set all configurations for this test method
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communitiesAPIUser1 = new APICommunitiesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communitiesAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communitiesAPIUser1.addMemberToCommunity(testUser2, publicCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now remove " + testUser2.getDisplayName() + " from the community");
		boolean removed = communitiesAPIUser1.removeMemberFromCommunity(publicCommunity, testUser2Profile);
		
		assert removed == true : "ERROR: There was a problem with removing " + testUser2.getDisplayName() + " as a member from the community using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_DeleteWidget() {
		
		// Set all configurations for this test method
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communitiesAPIUser1 = new APICommunitiesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communitiesAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add the Activities widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.ACTIVITIES);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add the Blogs widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add the Events widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add the Feeds widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.FEEDS);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add the Ideation Blogs widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add the Wikis widget to the community");
		communitiesAPIUser1.addWidget(publicCommunity, BaseWidget.WIKI);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the Activities widget from the community");
		boolean deletedActivities = communitiesAPIUser1.deleteWidget(publicCommunity, BaseWidget.ACTIVITIES);
		assert deletedActivities == true : "ERROR: There was a problem with deleting / removing the Activities widget from the community using the API";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the Blogs widget from the community");
		boolean deletedBlogs = communitiesAPIUser1.deleteWidget(publicCommunity, BaseWidget.BLOG);
		assert deletedBlogs == true : "ERROR: There was a problem with deleting / removing the Blogs widget from the community using the API";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the Events widget from the community");
		boolean deletedEvents = communitiesAPIUser1.deleteWidget(publicCommunity, BaseWidget.EVENTS);
		assert deletedEvents == true : "ERROR: There was a problem with deleting / removing the Events widget from the community using the API";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the Feeds widget from the community");
		boolean deletedFeeds = communitiesAPIUser1.deleteWidget(publicCommunity, BaseWidget.FEEDS);
		assert deletedFeeds == true : "ERROR: There was a problem with deleting / removing the Feeds widget from the community using the API";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the Ideation Blogs widget from the community");
		boolean deletedIdeationBlogs = communitiesAPIUser1.deleteWidget(publicCommunity, BaseWidget.IDEATION_BLOG);
		assert deletedIdeationBlogs == true : "ERROR: There was a problem with deleting / removing the Ideation Blogs widget from the community using the API";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the Wikis widget from the community");
		boolean deletedWikis = communitiesAPIUser1.deleteWidget(publicCommunity, BaseWidget.WIKI);
		assert deletedWikis == true : "ERROR: There was a problem with deleting / removing the Wikis widget from the community using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_CreateBookmark_CommunityBookmark() {
		
		// Set all configurations for this test method
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communitiesAPIUser1 = new APICommunitiesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communitiesAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a bookmark in the community");
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().IbmURL, baseCommunity);
		Bookmark bookmark = communitiesAPIUser1.createBookmark(publicCommunity, baseBookmark);
		
		assert bookmark != null : "ERROR: There was a problem with creating a new community bookmark using the API";
		assert bookmark.getId().toString().indexOf("referenceId=") > -1 : "ERROR: The ID of the new community bookmark was NOT set correctly using the API";
		assert bookmark.getTitle().trim().equals(baseBookmark.getTitle().trim()) : "ERROR: The title of the new community bookmark was NOT set correctly using the API";
		assert bookmark.getContent().trim().equals(baseBookmark.getDescription().trim()) : "ERROR: The description of the new community bookmark was NOT set correctly using the API";
		assert bookmark.getSelfLink() != null : "ERROR: The self link of the new community bookmark was NOT set correctly using the API";
		assert bookmark.getEditLink() != null : "ERROR: The edit link of the new community bookmark was NOT set correctly using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_EditBookmarkDescription() {
		
		// Set all configurations for this test method
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communitiesAPIUser1 = new APICommunitiesHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communitiesAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a bookmark in the community");
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(testName + Helper.genStrongRand(), Data.getData().IbmURL, baseCommunity);
		Bookmark bookmark = communitiesAPIUser1.createBookmark(publicCommunity, baseBookmark);
		
		assert bookmark != null : "ERROR: There was a problem with creating a new community bookmark using the API";
		
		log.info("INFO: " + testUser.getDisplayName() + " will now edit the description of the bookmark");
		String oldDescription = bookmark.getContent().trim();
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		bookmark = communitiesAPIUser1.editBookmarkDescription(bookmark, editedDescription);
		
		assert bookmark.getContent().trim().equals(oldDescription) == false : "ERROR: The description of the bookmark did NOT update to the new description using the API";
		assert bookmark.getContent().trim().equals(editedDescription) : "ERROR: The description of the bookmark was NOT set to the new description correctly using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
}
