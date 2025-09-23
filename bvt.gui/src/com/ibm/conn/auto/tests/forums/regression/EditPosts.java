package com.ibm.conn.auto.tests.forums.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.testng.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class EditPosts extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(EditPosts.class);
	private ForumsUI ui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner;
	private APIForumsHandler apiForumsMember;
	
	private User testUser1;
	private User testUser2;
	
	private String EditForumTopicTitle = "Topic Title EDITED";
	private String EditForumTopicContent = "Topic Content EDITED";
	private String EditForumReplyTitle = "Reply Title EDITED";
	private String EditForumReplyContent = "Reply Content EDITED";
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsMember = new APIForumsHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);	
	}
	
	/**
	 * 
	 * TEST CASE: COMMUNITY EDIT TOPIC - UI -- Community(a public community, including one community forum and one topic.)
	 * <ul>
	 * <li><B>Info: </B>Verify UI for edit topic in a public community by owner </li>
	 * <li><B>Info: </B>This case tests title and contents edit only, tags and attachments are tested in other cases </li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to create a topic</li>
	 * <li><B>Steps: </B>Open browser, Log in to community as Owner </li>
	 * <li><B>Steps: </B>navigate to Overview page </li>
	 * <li><B>Steps: </B>click Topic in Overview page </li>
	 * <li><B>Steps: </B>Edit Topic's title and contents </li>
	 * <li><B>Verify: </B>Topic's title is updated </li>
	 * <li><B>Verify: </B>Topic's contents is updated </li>
	 * <li><B>Verify: </B>Updated info shows out </li>
	 * </ul>
	 * 
	 */	 
	@Test(groups={"regression", "regressioncloud"} , enabled=false )	
	public void ComEditTitle(){	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a public community including only one community forum and a community member");
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community apiCommunity = community.createAPI(apiOwner);
			
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
			
		//create Topic by testUser1
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle)
													.tags(Data.getData().ForumTopicTag)
													.description(Data.getData().commonDescription)
													.partOfCommunity(community)
													.parentForum(apiForum)
													.build();
		//create Edited topic
		BaseForumTopic newForumTopic = new BaseForumTopic.Builder(EditForumTopicTitle)
													.tags(Data.getData().ForumTopicTag)
													.description(EditForumTopicContent)
													.partOfCommunity(community)
													.parentForum(apiForum)
													.build();
		
		logger.strongStep("Create Topic using API");
		log.info("INFO: create Topic using API");
		ForumTopic apiForumTopic = forumTopic.createAPI(apiForumsOwner);		
		
		log.info("end with preparing data");
		
		//Edit Topic by testUser1
		//Load component and login as testUser1
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: open browser and log in to Community as owner");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the Topic");
		log.info("INFO: navigate to the Topic");
		ui.clickLinkWait("link=" + community.getName());
		ui.clickLinkWait("link=" + apiForumTopic.getTitle());
		
		logger.strongStep("Select Edit");
		log.info("INFO: Select Edit");
		ui.clickLinkWait(ForumsUIConstants.EditTopic);
		
		//edit topic title
		logger.strongStep("Edit topic title");
		log.info("INFO: Edit topic title");
		ui.clearText(ForumsUIConstants.Start_A_Topic_InputText_Title);
		ui.typeText(ForumsUIConstants.Start_A_Topic_InputText_Title, newForumTopic.getTitle());
		
		//edit topic contents
		logger.strongStep("Edit topic contents");
		log.info("INFO: Edit topic contents");
		ui.typeInCkEditor(newForumTopic.getDescription());
		
		ui.clickSaveButton();
		
		//now verify that the topic is updated
		logger.strongStep("Check that topic is updated");
		log.info("INFO: Check that topic is updated");
		Assert.assertTrue(driver.isTextPresent(newForumTopic.getTitle()),
				"ERROR: The Topic title " + newForumTopic.getTitle() + " is not edited.");
		Assert.assertTrue(driver.isTextPresent(newForumTopic.getDescription()),
				"ERROR: The Topic description" + newForumTopic.getDescription() + " is not edited.");
		Assert.assertTrue(driver.isTextPresent("Updated on "),
				"ERROR: The Topic edit info is not shown");
		
		ui.endTest();
		
	}
	
	/**
	 * 
	 * TEST CASE: COMMUNITY EDIT REPLY - UI -- Community(a public community, including one community forum and one topic, one reply.)
	 * <ul>
	 * <li><B>Info: </B>Verify UI for edit reply in a public community by member </li>
	 * <li><B>Info: </B>This case tests title and contents edit only, tags and attachments are tested in other cases </li>
	 * <li><B>Steps: </B>Use API to create a community(having a community Member)</li>
	 * <li><B>Steps: </B>Use API to create a topic </li>
	 * <li><B>Steps: </B>Use API to reply to Topic by member </li>
	 * <li><B>Steps: </B>Open browser, Log in as Community-Member </li>
	 * <li><B>Steps: </B>Navigate to "I'm a Member" and go to the community </li>
	 * <li><B>Steps: </B>navigate to Overview page </li>
	 * <li><B>Steps: </B>click Topic in Overview page </li>
	 * <li><B>Steps: </B>Edit Topic's reply's title and contents </li>
	 * <li><B>Verify: </B>Reply's title is updated </li>
	 * <li><B>Verify: </B>Reply's contents is updated </li>
	 * <li><B>Verify: </B>Updated info shows out </li>
	 * </ul>
	 * 
	 */	 
	@Test(groups={"regression", "regressioncloud"} , enabled=false )
	
	public void ComEditReply(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a public community including only one community forum and a community member");
		log.info("INFO: test user1: "+ testUser1.getUid() +" as the owner ");
		log.info("INFO: test user2: " + testUser2.getUid() +" as a member ");
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community apiCommunity = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle)
		  											.tags(Data.getData().ForumTopicTag)
		  											.description(Data.getData().commonDescription)
		  											.partOfCommunity(community)
		  											.parentForum(apiForum)
		  											.build();
		
		//create Topic by testUser1
		logger.strongStep("Use API to create Topic by testUser1");
		log.info("INFO: use API to create Topic by testUser1");
		ForumTopic apiForumTopic = forumTopic.createAPI(apiForumsOwner);	
		
		//reply to Topic by testUser2
		logger.strongStep("Use API to reply to the topic by testUser2");
		log.info("INFO: Use API to reply to the topic by testUser2");
		apiForumsMember.createForumReply(apiForumTopic, "reply");
		
		log.info("end with preparing data");
		
		//edit reply by testUser2
		//Load component and login as testUser2
		logger.strongStep("Open browser and log in to Community as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and log in to Community as member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		//select my communities
		logger.strongStep("Select my community from left menu");
		log.info("INFO: Select my community from left menu");
		ui.clickLinkWait(CommunitiesUIConstants.MenuItem_My_Communities);
		
		logger.strongStep("Navigate to the community");
		log.info("INFO: navigate to the community");
		ui.clickLinkWait("link=" + community.getName());
		
		logger.strongStep("Navigate to the Topic");
		log.info("INFO: navigate to the Topic");
		ui.clickLinkWait("link=" + forumTopic.getTitle());
		
		logger.strongStep("Select Edit the reply");
		log.info("INFO: select Edit the reply");
		ui.clickLinkWait(ForumsUIConstants.EditFirstReply);
		
		//edit reply title
		logger.strongStep("Edit Reply Title");
		log.info("INFO: Edit Reply Title");
		ui.clickLinkWait(ForumsUIConstants.Edit_Reply_Title);
		ui.clearText(ForumsUIConstants.Reply_Title);
		ui.typeText(ForumsUIConstants.Reply_Title, EditForumReplyTitle);
				
		//edit reply contents
		logger.strongStep("Edit Reply Contents");
		log.info("INFO: Edit Reply Contents");
		ui.typeNativeInCkEditor(EditForumReplyContent);				
		ui.clickSaveButton();
		
		//now verify that the reply is updated
		logger.strongStep("Check that reply is updated");
		log.info("INFO: Check that reply is updated");
		Assert.assertTrue(driver.isTextPresent(EditForumReplyTitle),
				"ERROR: The Reply title" + EditForumReplyTitle + " is not edited.");
		Assert.assertTrue(driver.isTextPresent(EditForumReplyContent),
				"ERROR: The Reply content" + EditForumReplyContent + " is not edited.");
		Assert.assertTrue(driver.isTextPresent("Updated on "),
				"ERROR: The Reply edit info is not shown");
				
		ui.endTest();
		
	}

}
