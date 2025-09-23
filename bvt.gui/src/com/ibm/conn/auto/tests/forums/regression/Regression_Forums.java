package com.ibm.conn.auto.tests.forums.regression;

import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class Regression_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Regression_Forums.class);
	private ForumsUI ui;
	private TestConfigCustom cfg;
	private String testName;

	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>Test creating forums with and without new topic notifications</li>
	 * <li><B>Step: </B>Use API to create a community </li>
	 * <li><B>Step: </B>Use API to create forum </li>
	 * <li><B>Step: </B>Load the component and Login to Forums</li>
	 * <li><B>Step: </B>Select forums tab and start a Forum </li>
	 * <li><B>Verify: </B>Verify notification for new topics is enabled by default</li>
	 * <li><B>Step: </B>Click cancel and navigate to the forum</li>
	 * <li><B>Step: </B>Click on Following Actions</li>
	 * <li><B>Verify: </B>Verify the action to 'Stop Following this forum' link appears in a forum with notifications</li>
	 * <li><B>Step: </B>Go back and create a forum without notifications</li>
	 * <li><B>Step: </B>Click on Following Actions</li>
	 * <li><B>Verify: </B>Verify the action 'Follow this Forum' link appears in a forum  without notifications</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression"})
	public void forumsNewTopicNotification() throws Exception {
		//Start of the test
		testName = ui.startTest();
				
		forumsNewTopicNotification(false);
		
		//End of the Test
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Test creating community forums with and without new topic notifications</li>
	 * <li><B>Step: </B>Use API to create a community </li>
	 * <li><B>Step: </B>Use API to create forum </li>
	 * <li><B>Step: </B>Load the component and Login to community</li>
	 * <li><B>Step: </B>Navigate to the Community in community List </li>
	 * <li><B>Step: </B>Navigate to the community forums </li>
	 * <li><B>Step: </B>Select forums tab and start a Forum </li>
	 * <li><B>Verify: </B>Verify notification for new topics is enabled by default</li>
	 * <li><B>Step: </B>Click cancel and navigate to the forum</li>
	 * <li><B>Step: </B>Click on Following Actions</li>
	 * <li><B>Verify: </B>Verify the action to 'Stop Following this forum' link appears in a forum with notifications</li>
	 * <li><B>Step: </B>Go back and create a forum without notifications</li>
	 * <li><B>Step: </B>Click on Following Actions</li>
	 * <li><B>Verify: </B>Verify the action 'Follow this Forum' link appears in a forum  without notifications</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void communityForumsNewTopicNotification() throws Exception {
		//Start of the test
		testName = ui.startTest();
		
		forumsNewTopicNotification(true);
		
		//End of the Test
		ui.endTest();
	}
	
	private void forumsNewTopicNotification(boolean isCommunityForum) {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testUser1 = cfg.getUserAllocator().getUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIForumsHandler apiOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		APICommunitiesHandler apiCommsOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		BaseCommunity community = null;
		
		if (isCommunityForum) {
			community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
			.tags(Data.getData().commonTag + Helper.genDateBasedRand())
			.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
			.access(Access.PUBLIC)
			.description("Test description for testcase " + testName + Helper.genDateBasedRand())
			.addMember(new Member(CommunityRole.MEMBERS, testUser1))
			.build();
			
			//Create community using API
			logger.strongStep("Create community using API");
			log.info("INFO: Create community using API");
			community.createAPI(apiCommsOwner);
		}
		
		BaseForum forumWithNotify;
		String forumWithNotifyName;
		
		if (isCommunityForum) {
			forumWithNotify = null;
			forumWithNotifyName = community.getName();
		} else {
			forumWithNotify = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
		   .tags(Data.getData().commonTag)
		   .newTopicNotify(true)
		   .description(Data.getData().commonDescription).build();
			forumWithNotifyName = forumWithNotify.getName();
		}
		
		BaseForum forumWithoutNotify = new BaseForum.Builder(testName + Helper.genDateBasedRandVal() + "2")
		   .tags(Data.getData().commonTag)
		   .newTopicNotify(false)
		   .description(Data.getData().commonDescription).build();
		
		if (!isCommunityForum) {
			//Create forum with notification using API
			logger.strongStep("Create forum using API");
			log.info("INFO: Create forum using API");
			forumWithNotify.createAPI(apiOwner);			
		}
		
		//Load the component and Login
		if (isCommunityForum){
			logger.strongStep("Open browser, and login to Community as:" + testUser1.getDisplayName());
			log.info("INFO: Open browser, and login to Community ");
			ui.loadComponent(Data.getData().ComponentCommunities);			
		}
		else{
			logger.strongStep("Open browser, and login to Forum  as:" + testUser1.getDisplayName());
			log.info("INFO: Open browser, and login to Forum ");
			ui.loadComponent(Data.getData().ComponentForums);
		}		
		ui.login(testUser1);
		
		if (isCommunityForum) {
			// navigate to community in community list
			logger.strongStep("Navigate to the Community in community List");
			log.info("INFO: navigate to the Community in community List");
			String communityLink = "link=" + community.getName();
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLink(communityLink);
			
			// navigate to community forums
			logger.strongStep("Navigate to the community forums");
			log.info("INFO: navigate to the community forums");
			Community_LeftNav_Menu.FORUMS.select(ui);
		}
		
		//Click the forums tab
		logger.strongStep("Select forums tab");
		log.info("INFO: select forums tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		//Go to the forum for creating a forum
		logger.strongStep("Click Start a Forum");
		log.info("INFO: Click Start a Forum");
		ui.clickLinkWait(ForumsUIConstants.Start_A_Forum);
		
		//Confirm notification for new forum topics enabled by default
		logger.strongStep("Verifying notification for new forum topics is enabled");
		log.info("INFO: Verifying notification for new forum topics is enabled");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_A_Forum_New_Topic_Notification_Enabled),
				"ERROR: notification for new forum topics is not enabled by default");
		
		//Go back
		logger.strongStep("Click on Cancel button");
		log.info("INFO: Click on Cancel button");
		ui.clickCancelButton();
		
		//Click the forums tab
		logger.strongStep("Select Forums tab");
		log.info("INFO: Select Forums tab");
		ui.fluentWaitElementVisible(ForumsUIConstants.Forum_Tab);
		ui.clickLink(ForumsUIConstants.Forum_Tab);
		
		//Go to the forum with notifications
		logger.strongStep("Navigating to forum: " + forumWithNotifyName);
		log.info("INFO: Navigating to forum: " + forumWithNotifyName);
		ui.clickLinkWait("css=table.dfForumTable a:contains(" + forumWithNotifyName + ")");
		
		//Select Following Actions
		logger.strongStep("Click on Following Actions");
		log.info("INFO: Click on Following Actions");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		ui.fluentWaitElementVisible(ForumsUIConstants.Follow_Forum);
		
		//Confirm Stop Following link appears
		logger.strongStep("Verify the action to 'Stop Following this forum' link appears in a forum with notifications");
		log.info("INFO: Verify the action to 'Stop Following this forum' link appears in a forum with notifications");
		String followText = ui.getFirstVisibleElement(ForumsUIConstants.Follow_Forum).getText();
		Assert.assertEquals(followText, Data.FORUM_STOP_FOLLOWING,
				"ERROR: The action to stop following a forum did not appear in a forum with notifications enabled.");
		
		//Go back
		logger.strongStep("Go back");
		log.info("INFO: Go back");
		driver.navigate().back();
		
		//Create the forum without notifications, this does not seem to be supported by the API
		logger.strongStep("Create the forum without notifications");
		log.info("INFO: Create the forum without notifications");
		ui.create(forumWithoutNotify);		
			
		//Select Following Actions
		logger.strongStep("Click on Following Actions");
		log.info("INFO: Click on Following Actions");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		ui.fluentWaitElementVisible(ForumsUIConstants.Follow_Forum);
		
		//Confirm Start Following link appears
		logger.strongStep("Verify the action 'Follow this Forum' link appears in a forum  without notifications");
		log.info("INFO: Verify the action 'Follow this Forum' link appears in a forum  without notifications");
		followText = ui.getFirstVisibleElement(ForumsUIConstants.Follow_Forum).getText();
		Assert.assertEquals(followText, Data.FORUM_START_FOLLOWING,
				"ERROR: The action to 'Follow this Forum' did not appear in a forum with notifications disabled.");
				
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>On-premise only</B>
	 * <li><B>Info: </B>Test adding and removing tags in forum topics</li>
	 * <li><B>Step: </B>Use API to create a forum</li>
	 * <li><B>Step: </B>Open browser, and login to Forums</li>
	 * <li><B>Step: </B>Navigate to forum tab</li>
	 * <li><B>Step: </B>Navigate to the forum</li>
	 * <li><B>Step: </B>Create a new topic</li>
	 * <li><B>Step: </B>Select add or remove tags</li>
	 * <li><B>Step: </B>Remove one of the tags</li>
	 * <li><B>Step: </B>Add a new tag</li>
	 * <li><B>Verify: </B>Verify tags were added and removed properly</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression"})
	public void forumsTopicTags() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		//Start of the test
		String testName = ui.startTest();
		User testUser1 = cfg.getUserAllocator().getUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIForumsHandler apiOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		String uniqueTopicTag = Data.getData().ForumTopicTag.toLowerCase() + Helper.genDateBasedRandVal3();
		String uniqueNewTag = Data.getData().commonTag.toLowerCase() + Helper.genDateBasedRandVal3();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
		   .tags(Data.getData().commonTag)
		   .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal())
  		 .tags(Data.getData().ForumTopicTag.toLowerCase() + " " + uniqueTopicTag)
  		 .description(Data.getData().commonDescription).build();
		
		//Create forum with notification using API
		logger.strongStep("Create a forum using API");
		log.info("INFO: Create a forum using API");
		forum.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("Open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		// Navigate to forum tab
		logger.strongStep("Click Forum Tab");
		log.info("click Forum Tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		//Go to the forum we created with the API
		logger.strongStep("Navigating to forum: " + forum.getName());
		log.info("INFO: Navigating to forum: " + forum.getName());
		ui.clickLinkWait("css=table.dfForumTable a:contains(" + forum.getName() + ")");
			
		// Start a topic
		logger.strongStep("Create a new topic");
		log.info("INFO: Create a new topic");
		topic.create(ui);
		
		// Click add/remove tags
		logger.strongStep("Select Add or Remove tags");
		log.info("select Add or Remove tags");
		ui.clickLinkWait(ForumsUIConstants.AddRemove_Tags);
		
		// Remove the first tag
		logger.strongStep("Removing the tag " + uniqueTopicTag);
		log.info("INFO: Removing the tag " + uniqueTopicTag);
		String removeTag = "css=img[title='Remove tag " + uniqueTopicTag + "']";
		ui.clickLinkWait(removeTag);
		
		// Add a new tag
		logger.strongStep("Adding the tag " + uniqueNewTag);
		log.info("INFO: Adding the tag " + uniqueNewTag);
		ui.typeText(ForumsUIConstants.AddTag, uniqueNewTag);
		ui.clickLinkWait(ForumsUIConstants.AddTag_OkButton);
		
		//Verify tags were added and removed properly
		logger.strongStep("Verify tags were added and removed properly");
		log.info("INFO: Verify tags were added and removed properly");
		Assert.assertTrue(ui.isElementPresent("css=a[lconntagname='" + uniqueNewTag + "']"),
				"ERROR: The new tag " + uniqueNewTag + " did not appear after adding it.");
		Assert.assertFalse(ui.isTextPresent(uniqueTopicTag),
				"ERROR: The old tag " + uniqueTopicTag + " still appears after removing it.");
		
		// Logout of Connections
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>On-premise only test case</li>
	 * <li><B>Info: </B>Test marking and unmarking questions as answered</li>
	 * <li><B>Step: </B>Create a forum using the API</li>
	 * <li><B>Step: </B>Create a forum topic using the API</li>
	 * <li><B>Step: </B>Open browser, and Login to the forum topic</li>
	 * <li><B>Step: </B>Navigate to forum tab</li>
	 * <li><B>Step: </B>Go to the forum</li>
	 * <li><B>Step: </B>Go to the topic</li>
	 * <li><B>Step: </B>Reply to topic</li>
	 * <li><B>Step: </B>Mark Topic as a Question</li>
	 * <li><B>Verify: </B>The topic shows as having an unanswered question</li> 
	 * <li><B>Step: </B>Reply to topic with answer</li>
	 * <li><B>Step: </B>Accept the answer</li>
	 * <li><B>Verify: </B>The topic shows as having an answered question</li>
	 * <li><B>Step: </B>Reopen the question</li>
	 * <li><B>Verify: </B>The topic shows as having an unanswered question</li>
	 * <li><B>Step: </B>Go to the list of forum topics</li>
	 * <li><B>Verify: </B>question icon indicates unanswered in topic list view</li>
	 * <li><B>Step: </B>Go to the forum topic</li>
	 * <li><B>Step: </B>Accept the answer</li>
	 * <li><B>Verify: </B>The topic shows as having an answered question</li>
	 * <li><B>Step: </B>Decline the answer</li>
	 * <li><B>Verify: </B>The topic shows as having an unanswered question</li>
	 * <li><B>Step: </B>Mark Topic as not Question</li>
	 * <li><B>Verify: </B>The topic shows as being a regular topic</li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void forumsTopicQuestion() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		//Start of the test
		String testName = ui.startTest();
		User testUser1 = cfg.getUserAllocator().getUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIForumsHandler apiOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
		   .tags(Data.getData().commonTag)
		   .description(Data.getData().commonDescription).build();
		
		//Create forum using API
		logger.strongStep("Create a forum using API");
		log.info("INFO: Create a forum using API");
		Forum apiForum = forum.createAPI(apiOwner);

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal())
		 	.tags(Data.getData().ForumTopicTag)
		 	.description(Data.getData().commonDescription)
			.parentForum(apiForum).build();
		
		//Create topic using API
		logger.strongStep("Create a topic using API");
		log.info("INFO: Create a topic using API");
		topic.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		// Navigate to forum tab
		logger.strongStep("Click Forum Tab");
		log.info("INFO: click Forum Tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		//Go to the forum we created with the API
		logger.strongStep("Navigating to forum: " + forum.getName());
		log.info("INFO: Navigating to forum: " + forum.getName());
		ui.clickLinkWait("css=table.dfForumTable a:contains(" + forum.getName() + ")");
		
		//Go to the topic we created with the API
		logger.strongStep("Navigating to topic: " + topic.getTitle());
		log.info("INFO: Navigating to topic: " + topic.getTitle());
		ui.clickLink("link=" + topic.getTitle());

		// Reply to topic
		logger.strongStep("Reply to topic");
		log.info("INFO: Reply to topic");
		ui.replyToTopic(topic);
		
		// Mark Topic as a Question
		logger.strongStep("Mark topic as a question");
		log.info("INFO: Mark topic as a question");
		ui.clickLinkWait(ForumsUIConstants.EditTopic);
		ui.clickLinkWait(ForumsUIConstants.MarkTopicAsQuestion);
		ui.clickSaveButton();
		
		ui.fluentWaitElementVisible(ForumsUIConstants.Reply_to_topic);
		
		logger.strongStep("Verify the topic shows as having an unanswered question");
		log.info("INFO: verify the topic shows as having an unanswered question");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnansweredQuestion),
				"ERROR: The forum does not show as having an unanswered question after " +
				"marking the topic as a question.");
		
		// Reply to topic with answer
		logger.strongStep("Reply to topic with answer");
		log.info("INFO: Reply to topic with answer");
		ui.replyToTopic(topic);
		
		// Accept the answer
		logger.strongStep("Accept the answer");
		log.info("INFO: Accept the answer");
		ui.clickLink(ForumsUIConstants.AcceptAsAnswer);
		
		logger.strongStep("Verify the topic shows as having an answered question");
		log.info("INFO: verify the topic shows as having an answered question");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AnsweredQuestion),//TODO replace this with the successful messages.
				"ERROR: The forum does not show as having an answered question after " +
				"marking a reply as an accepted answer.");
		
		driver.navigate().refresh();
		
		// Reopen the question
		logger.strongStep("Reopen the question");
		log.info("INFO: reopen the question");
		ui.clickLink(ForumsUIConstants.ReopenQuestion);
		
		logger.strongStep("Verify the topic shows as having an unanswered question");
		log.info("INFO: verify the topic shows as having an unanswered question");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnansweredQuestion),
				"ERROR: The forum does not show as having an unanswered question after " +
				"reopening a question.");
		
		// Confirm question icon indicates unanswered in topic list view
		logger.strongStep("Navigate to the list of forum topics");
		log.info("INFO: navigate to the list of forum topics");
		ui.clickLink(ForumsUI.getForumLink(forum));
		
		logger.strongStep("Verify the topic has an unanswered question icon in the topic list view");
		log.info("INFO: verify the topic has an unanswered question icon in the topic list view");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.QuestionIcon));
		ui.clickLink("link=" + topic.getTitle());
		
		// Accept the answer
		logger.strongStep("Accept the answer");
		log.info("INFO: Accept the answer");
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		ui.clickLink(ForumsUIConstants.AcceptAsAnswer);
		
		logger.strongStep("Verify the topic shows as having an answered question");
		log.info("INFO: verify the topic shows as having an answered question");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.AnsweredQuestion),
				"ERROR: The forum does not show as having an answered question after " +
				"marking a reply as an accepted answer.");
		
		// Decline the answer
		logger.strongStep("Decline the answer");
		log.info("INFO: decline the answer");
		ui.clickLink(ForumsUIConstants.DeclineAnswer);
		
		logger.strongStep("Verify the topic shows as having an unanswered question");
		log.info("INFO: verify the topic shows as having an unanswered question");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnansweredQuestion),
				"ERROR: The forum does not show as having an unanswered question after " +
				"declining as answer.");
		
		// Mark Topic as NOT a Question
		logger.strongStep("Mark topic as not a question");
		log.info("INFO: Mark topic as not a question");
		ui.clickLinkWait(ForumsUIConstants.EditTopic);
		ui.clickLinkWait(ForumsUIConstants.MarkTopicAsQuestion);
		ui.clickSaveButton();
		
		// Confirm topic question options/icons are not present
		logger.strongStep("Verify topic is no longer marked as a question");
		log.info("INFO: Verify topic is no longer marked as a question");
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		Assert.assertTrue(driver.isTextPresent(Data.FORUM_TOPIC_MARKED_REGULAR),
				"ERROR: The topic did not appear as a regular topic after unmarking " +
				"it as a question");
		
		// Logout of Connections
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>On-premise only</li>
	 * <li><B>Info: </B>Test adding and removing forum owners</li>
	 * <li><B>Step: </B>Create a forum using the API</li>
	 * <li><B>Step: </B>Log in and go to the created forum</li>
	 * <li><B>Verify: </B>The forum creator cannot remove himself from owners list</li>
	 * <li><B>Step: </B>Add a second user to the forum's owners</li>
	 * <li><B>Verify: </B>The second user appears in the forum's owners list</li>
	 * <li><B>Step: </B>Remove the second user from the forum's owners</li>
	 * <li><B>Verify: </B>The second user does not appear in the forum's owners list</li>
	 * <li><B>Step: </B>Add a second user to the forum's owners</li>
	 * <li><B>Verify: </B>The second user appears in the forum's owners list</li>
	 * <li><B>Step: </B>Log out</li>
	 * <li><B>Step: </B>Log in as the second user and go to the created forum</li>
	 * <li><B>Step: </B>Remove the first user (the forum creator) from the forum's owners</li>
	 * <li><B>Verify: </B>The first user does not appear in the forum's owners list</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression"})
	public void forumsOwners() throws Exception {
		//Start of the test
		testName = ui.startTest();
		
		forumsOwners(false);
		
		//End of the Test
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>On-premise only</li>
	 * <li><B>Info: </B>Test adding and removing forum owners as an admin</li>
	 * <li><B>Step: </B>Create a forum using the API</li>
	 * <li><B>Step: </B>Log in and go to the created forum</li>
	 * <li><B>Verify: </B>The forum creator cannot remove himself from owners list</li>
	 * <li><B>Step: </B>Add an admin user to the forum's owners</li>
	 * <li><B>Verify: </B>The admin user appears in the forum's owners list</li>
	 * <li><B>Step: </B>Log out</li>
	 * <li><B>Step: </B>Log in as the admin user and go to the created forum</li>
	 * <li><B>Step: </B>Remove the first user (the forum creator) from the forum's owners</li>
	 * <li><B>Verify: </B>The first user does not appear in the forum's owners list</li>
	 * </ul>
	 * This test fails with the default user configuration because the admin user Amy 
	 * Jones1 cannot be added. Amy Jones1 matches 111 different users (Amy Jones1, Amy 
	 * Jones 10-19, Amy Jones 100-199) which the typeahead field where the user is 
	 * entered cannot handle. This test must be run on a configuration where the admin 
	 * user is renamed.
	 * @throws Exception
	 */
	@Test(groups = {"regression"} , enabled=false )
	public void forumsAdmin() throws Exception {
		//Start of the test
		testName = ui.startTest();
		
		forumsOwners(true);
		
		//End of the Test
		ui.endTest();
	}
	
	public void forumsOwners(boolean isAdmin) throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser1 = cfg.getUserAllocator().getUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		APIForumsHandler apiOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		User testUser2;
		if (isAdmin) {
			testUser2 = cfg.getUserAllocator().getAdminUser();
		} else {
			testUser2 = cfg.getUserAllocator().getUser();
		}

		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
		   .tags(Data.getData().commonTag)
		   .description(Data.getData().commonDescription).build();
		
		//Create forum using API
		logger.strongStep("Create forum using API");
		log.info("INFO: Create forum using API");
		forum.createAPI(apiOwner);
		
		//Load the component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		// Navigate to forum tab
		logger.strongStep("Click Forum Tab");
		log.info("click Forum Tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		//Go to the forum we created with the API
		logger.strongStep("Navigating to forum: " + forum.getName());
		log.info("INFO: Navigating to forum: " + forum.getName());
		ui.clickLinkWait("css=table.dfForumTable a:contains(" + forum.getName() + ")");
		
		// Confirm forum creator cannot remove himself from owners list
		logger.strongStep("Verify forum creator cannot remove himself from owners list");
		log.info("INFO: verify forum creator cannot remove himself from owners list");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.RemoveOwner),
				"ERROR: The forum's creator should not be able to remove himself from owners list");
		
		// Add another user as an owner
		logger.strongStep("Add a second user to the forum's owners");
		log.info("INFO: add a second user to the forum's owners");
		addForumOwner(testUser2);
		
		if (!isAdmin) {
			// Forum creator can remove other owner
			removeForumOwner(testUser2);
		
			// Add another user as an owner again
			addForumOwner(testUser2);
		}
		
		// Logout of Connections
		logger.strongStep("Log out");
		log.info("INFO: Log out");
		ui.logout();
		
		// Login as added forum owner
		logger.strongStep("Load Forum component and login as:" + testUser2.getDisplayName());
		log.info("INFO: Load Forum component and Login as added forum owner");
		ui.loadComponent(Data.getData().ComponentForums, true);
		ui.login(testUser2);
		
		logger.strongStep("Click I'm an Owner");
		log.info("INFO: click I'm an Owner");
		ui.clickLink(ForumsUIConstants.Im_An_Owner);
		
		// Navigate to the forum owned
		logger.strongStep("Navigate to the forum");
		log.info("INFO: Navigate to the forum");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		ui.clickLinkWait("css=table.dfForumTable a:contains(" + forum.getName() + ")");
		
		// Forum owner can remove forum creator
		logger.strongStep("Remove first user from the forum's owner");
		log.info("INFO: remove first user from the forum's owner");
		removeForumOwner(testUser1);		
	}
	
	private void addForumOwner(User ownerToAdd) throws Exception {
		log.info("INFO: Adding " + ownerToAdd.getDisplayName() + " as an owner");
		ui.clickLink(ForumsUIConstants.AddOwners);
		ui.fluentWaitElementVisible(ForumsUIConstants.ForumsAddOwnersInput);
		
		ui.typeText(ForumsUIConstants.ForumsAddOwnersInput, ownerToAdd.getLastName());
		ui.fluentWaitElementVisible(ForumsUIConstants.MemberTable);
		
		if(driver.isElementPresent(ForumsUIConstants.MemberSearchDir)) {
			ui.clickLink(ForumsUIConstants.MemberSearchDir);
		}
		
		ui.clickLinkWait(ForumsUIConstants.MemberNames + ":contains(" + ownerToAdd.getEmail() + ")");
		ui.fluentWaitPresent("css=span.lotusPerson[role='button']:contains(" + ownerToAdd.getDisplayName() + ")");
		ui.clickLink(ForumsUIConstants.MemberOkButton);
		Assert.assertTrue(ui.isElementPresent("css=span.vcard > a:contains(" + ownerToAdd.getDisplayName() + ")"),
				"could not find element containing text [" + ownerToAdd.getDisplayName() + "]");
	}
	
	private void removeForumOwner(User ownerToRemove) throws Exception {
		log.info("INFO: Removing " + ownerToRemove.getDisplayName() + " as an owner");
		ui.clickLinkWait(ForumsUIConstants.RemoveOwner);
		ui.clickLinkWait(ForumsUIConstants.MemberOkButton);
		Assert.assertFalse(driver.isElementPresent("css=span.vcard > a:contains(" + ownerToRemove.getDisplayName() + ")"),
				"The user " + ownerToRemove.getDisplayName() + " was not properly removed.");
	}

}
