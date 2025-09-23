package com.ibm.conn.auto.lcapi.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class APIForumsTest extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(APIWikisTest.class);
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

		ServiceEntry forums = config.getService("forums");
		assert(forums != null);

		Utils.addServiceAdminCredentials(communities, client);
				
	}

	@Test (groups = {"apitest"})
	public void createForumTopicReplyMentions(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();

		BaseForum baseForum = new BaseForum.Builder(testName + Helper.genDateBasedRand())
		   								.tags(Data.getData().commonTag)
		   								.description(Data.getData().commonDescription)
		   								.build();
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum using API method");
		Forum forum = apiHandler.createForum(baseForum);

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal3())
													.parentForum(forum)
													.tags(Data.getData().commonTag)
													.description(Data.getData().commonDescription)
													.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum topic using API method");
		ForumTopic forumTopic = apiHandler.createForumTopic(baseTopic);

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		log.info("INFO: Creating a Mentions object");
		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum topic reply with a mentions to " + testUser2.getDisplayName() + " using API method");
		ForumReply result = apiHandler.createTopicReplyMention(forumTopic, mentions);
		
		assert result != null: "Creation of forum topic reply mentions failed";
				
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Unit test for public void apiCreateTopicMention(baseTopic, mentions) in APIForumsHandler.</li>
	 * <li><B>Steps: </B>Use API to create a stand alone forum</li>
	 * <li><B>Steps: </B>Use API to create a topic with a mentions in the stand alone forum</li>
	 * </ul>
	 */
	@Test (groups = {"apitest"})
	public void createForumTopicMentions(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();

		BaseForum baseForum = new BaseForum.Builder(testName + Helper.genStrongRand())
		   								.tags(Data.getData().commonTag + Helper.genStrongRand())
		   								.description(Data.getData().commonDescription + Helper.genStrongRand())
		   								.build();
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum using API method");
		Forum forum = apiHandler.createForum(baseForum);

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(testName + Helper.genStrongRand())
													.parentForum(forum)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.description(Data.getData().commonDescription + Helper.genStrongRand())
													.build();

		String beforeMentionsText = Helper.genStrongRand();
		String afterMentionsText = Helper.genStrongRand();

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());

		log.info("INFO: Creating a Mentions object");
		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum topic with a mentions to " + testUser2.getDisplayName() + " using API method");
		ForumTopic result = apiHandler.apiCreateTopicMention(baseTopic, mentions);

		assert result != null: "Creation of forum topic mentions failed";
				
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Unit test for public void addOwnertoForum(Forum apiForum, User testuser) in APIForumsHandler.</li>
	 * <li><B>Steps: </B>Use API to create a stand alone forum, </li>
	 * <li><B>Steps: </B>add user2 as owner to the forum  </li>
	 
	
	 * @throws Exception
	 */
	@Test(groups={"apitest"})
	public void testAddOwnertoForum() throws Exception{
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		
		BaseForum forum = new BaseForum.Builder("stand alone " + Helper.genDateBasedRand())
										.tags("apitest")
										.description("Stand alone Forum, for api test")
										.build();
		Forum apiForum = forum.createAPI(apiHandler); 
		
		log.info("call the addOwnertoForum(...) method");
		apiHandler.addOwnertoForum(apiForum, testUser2);
		
	
		
		
	}
	/**
	 * <ul>
	 * <li><B>Info: </B>Unit test for public void addRelatedCommunity(String targetURL, String communityName, String communityURL) in APIForumsHandler.</li>
	 * <li><B>Steps: </B>Use API to create a community, </li>
	 * <li><B>Steps: </B>use API to enable the community's Related Communities widget  </li>
	 * <li><B>Steps: </B>use API to call addRelatedCommunity(...) method. </li>
	 
	
	 * @throws Exception
	 */
	@Test(groups={"apitest"})
	public void testAddRelatedCommunity() throws Exception{
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		
		String rand = Helper.genDateBasedRand();
		String communityName = "Community "+ rand;
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("for api test")
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community		
		log.info("use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		String targetURL = testURL + "/communities/recomm/atom/relatedCommunity?communityUuid=" 
							+ ForumsUtils.getCommunityUUID(commUUIDA);
		log.info("targetURL: "+ targetURL);

		log.info("use API to enable the Related Communities widget");
		apiOwner.addWidget(apiCommunityA, BaseWidget.RELATED_COMMUNITIES);
		
		String overview = testURL
						+"/communities/service/html/communityoverview?" + apiOwner.getCommunityUUID(apiCommunityA);
		log.info("overview is:"+ overview);
		
		log.info("call addRelatedCommunity(...)");
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		apiHandler.addRelatedCommunity(targetURL, communityName, overview,"unit test");
		
	}
	
	@Test(groups={"apitest"})
	public void testStopFollowing() throws Exception{
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		
		String rand = Helper.genDateBasedRand();
		String communityName = "Community "+ rand;
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("for api test")
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community		
		log.info("use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		Forum forum = apiHandler.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUIDA), communityA.getName());
		log.info("user is"+ testUser.getDisplayName());
		
		log.info("call stopFollowing(...)");
		apiHandler.stopFollowing(forum);
		
	}
	
	@Test(groups={"apitest1"})
	public void testStopFollowingTopic() throws Exception{
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		
		String rand = Helper.genDateBasedRand();
		String communityName = "Community "+ rand;
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("for api test")
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community		
		log.info("use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		
		
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		Forum forum = apiHandler.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUIDA), communityA.getName());
		log.info("user is"+ testUser.getDisplayName());
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Topic for stopping following" )
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(false)
													  .parentForum(forum)
													  .build();
		ForumTopic apiForumTopic = forumTopic2.createAPI(apiHandler);
		
		log.info("call stopFollowing(...)");
		apiHandler.stopFollowing(apiForumTopic);
		
	}

	@Test (groups = {"apitest"})
	public void likeForumTopic(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();

		BaseForum baseForum = new BaseForum.Builder(testName + Helper.genDateBasedRand())
		   								.tags(Data.getData().commonTag)
		   								.description(Data.getData().commonDescription)
		   								.build();
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum using API method");
		Forum forum = apiHandler.createForum(baseForum);

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal3())
													.parentForum(forum)
													.tags(Data.getData().commonTag)
													.description(Data.getData().commonDescription)
													.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum topic using API method");
		ForumTopic forumTopic = apiHandler.createForumTopic(baseTopic);

		APIForumsHandler user2 = new APIForumsHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		log.info("INFO: " + testUser2.getDisplayName() + " like the forum topic by " + testUser.getDisplayName() + " using API method");
		String URL = user2.like(forumTopic);
		
		assert URL != null: "Like of forum topic failed";
		
		assert URL.contains("/forums/atom/recommendation/entries?postUuid="): "URL is malformed";
				
	}

	@Test (groups = {"apitest"})
	public void unlikeForumTopic(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();

		BaseForum baseForum = new BaseForum.Builder(testName + Helper.genDateBasedRand())
		   								.tags(Data.getData().commonTag)
		   								.description(Data.getData().commonDescription)
		   								.build();
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum using API method");
		Forum forum = apiHandler.createForum(baseForum);

		BaseForumTopic baseTopic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal3())
													.parentForum(forum)
													.tags(Data.getData().commonTag)
													.description(Data.getData().commonDescription)
													.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create a forum topic using API method");
		ForumTopic forumTopic = apiHandler.createForumTopic(baseTopic);

		APIForumsHandler user2 = new APIForumsHandler(testURL, testUser2.getEmail(), testUser2.getPassword());

		log.info("INFO: " + testUser2.getDisplayName() + " like the forum topic by " + testUser.getDisplayName() + " using API method");
		String URL = user2.like(forumTopic);

		log.info("INFO: " + testUser2.getDisplayName() + " unlike the forum topic by " + testUser.getDisplayName() + " using API method");
		boolean deleted = user2.unlike(URL);
		
		assert deleted == true: "Unlike of forum topic failed";
				
	}
	
	/**
	 * Unit test offlagCommunityTopic() method in APIForumshandler.java
	 * @throws Exception
	 */
	@Test(groups={"apitest"})
	public void flagCommunityTopic() throws Exception{
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		String rand = Helper.genDateBasedRand();
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String communityName = testName + Helper.genDateBasedRand();
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("for api test")
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community		
		log.info("use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		log.info("UUUID" + commUUIDA);
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		Forum forum = apiHandler.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUIDA), communityA.getName());
		log.info("user is"+ testUser.getDisplayName());
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("FlagCommunityTopic" )
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(false)
													  .parentForum(forum)
													  .build();
		ForumTopic apiForumTopic = forumTopic2.createAPI(apiHandler);
		
		
		log.info("call flagCommunityTopic(...)");
		boolean flag = apiHandler.flagCommunityTopic(apiForumTopic);
		assert flag == true: "Flagging of community topic failed";
		
	}

	/**
	 * Unit test of flagCommunityReply() method of APIForumsHandler 
	 * @throws Exception
	 */
	@Test(groups={"apitest"})
	public void flagCommunityReply() throws Exception{
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		String rand = Helper.genDateBasedRand();
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String communityName = testName + Helper.genDateBasedRand();
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("for api test")
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community		
		log.info("use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		log.info("UUUID" + commUUIDA);
		
		APIForumsHandler apiHandler = new APIForumsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		Forum forum = apiHandler.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUIDA), communityA.getName());
		log.info("user is"+ testUser.getDisplayName());
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("FlagCommunityTopic" )
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(false)
													  .parentForum(forum)
													  .build();
		ForumTopic apiForumTopic = forumTopic2.createAPI(apiHandler);
		
		String sReply = "Reply" + Helper.genDateBasedRand();
		ForumReply reply = apiHandler.createForumReply(apiForumTopic,
				sReply);
		
		log.info("call flagCommunityReply(...)");
		boolean flag = apiHandler.flagCommunityReply(reply);
		assert flag == true: "Flagging of community reply failed";
		
	}

}
