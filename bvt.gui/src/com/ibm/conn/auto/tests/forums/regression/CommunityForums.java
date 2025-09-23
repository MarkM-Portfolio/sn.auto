/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.forums.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class CommunityForums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(CommunityForums.class);
	private ForumsUI ui;
	private CommunitiesUI comui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner;
	private User testUser1, testUser2;
	private String serverURL;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		comui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/*
	 * step 1. navigate to ovewview page by access the community's url
	 * step 2. click Forums from left nav
	 * */
	private void naviToForums(String overview){
		log.info("INFO: navinate to the overview page : " + overview);
		driver.navigate().to(overview);
		log.info("INFO: click Forums from the left navigation bar");
		Community_LeftNav_Menu.FORUMS.select(comui);
	}
	/**
	 * return the exact community UUID, like c122a3d7-f577-49dd-9911-294bcb822c13
	 * @param commUUID -- like  "communityUuid=c122a3d7-f577-49dd-9911-294bcb822c13"
	 * @return -- like "c122a3d7-f577-49dd-9911-294bcb822c13"
	 */
	private String getUUID(String commUUID){
		int start = commUUID.indexOf("=");
		if(start!=-1){
			commUUID = commUUID.substring(start +1);
		}
		log.info("commUUID : "+ commUUID);
		return commUUID;	
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test answered and unanswered tabs</li>
	*<li><B>Step: </B>Use API to create a community</li>
	*<li><B>Step: </B>Use API to create 2 questions</li>
	*<li><B>Step: </B>Open browser, and login</li>
	*<li><B>Step: </B>Check different topic shows in different tabs</li>
	*<li><B>Step: </B>Check no open question in answered questions tab</li>
	*<li><B>Step: </B>Check 2 open question in open questions tab</li>
	*<li><B>Step: </B>Reply one open question</li>
	*<li><B>Step: </B>Accept the answer</li>
	*<li><B>Verify: </B>Check answered topic is shows in answered questions tab</li>
	*<li><B>Verify: </B>Check answered topic is disappears from open questions tab</li>
	*</ul>
	 * @throws Exception 
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void testAnsweredUnansweredTab() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		String commUUID = community.getCommunityUUID();
		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
	
		BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("Open Question1 " + Data.getData().ForumTopicTitle)
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(community)
		  											  .markAsQuestion(true)
		  											  .parentForum(apiForum)
		  											  .build();
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Open Question2 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
		  											  .markAsQuestion(true)
		  											  .parentForum(apiForum)
													  .build();
		
		logger.strongStep("Create two forum topics(Open Question1 and Open Question2) using API");
		log.info("INFO: Create two forum topics(Open Question1 and Open Question2) using API");
		forumTopic1.createAPI(apiForumsOwner);
		ForumTopic apiForumTopic2 = forumTopic2.createAPI(apiForumsOwner);
		
		
		//GUI
		//Load component and login
		logger.strongStep("Open browser and log in to Communities as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//verify different topic shows in different tabs
		logger.strongStep("Verify different topic shows in different tabs");
		log.info("INFO: verify different topic shows in different tabs");

		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
					+ "/communities/service/html/communityoverview?" + commUUID;
		comui.navViaUUID(community);
		Community_LeftNav_Menu.FORUMS.select(ui);
		
		logger.strongStep("Click the Answered Questions tab, verify no open questions");
		log.info("INFO: click the Answered Questions tab, verify no open questions");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		Assert.assertTrue(driver.isTextPresent(Data.getData().NoTopicMsg), 
				"ERROR: no topic message doesn't show up");
		
		logger.strongStep("Click the Open Questions tab, verify there are two open questions");
		log.info("INFO: click the Open Questions tab, verify there are two open questions");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		Assert.assertNotNull(driver.getElements(ui.getTopicSelector(forumTopic1)), 
				"ERROR: the topic1 doesn't show up");
		Assert.assertNotNull(driver.getElements(ui.getTopicSelector(forumTopic2)), 
				"ERROR: the topic 2 doesn't show up");
	
		logger.strongStep("Reply to " + apiForumTopic2.getTitle() + "and accept the reply as answer Using API");
		log.info("INFO: reply to " + apiForumTopic2.getTitle() + "and accept the reply as answer Using API");
		ForumReply apiReply = apiForumsOwner.createForumReply(apiForumTopic2, "testing reply");
		Assert.assertTrue(apiForumsOwner.acceptAsAnswer(apiReply));

		naviToForums(overview); //refresh the data
		
		logger.strongStep("Click Answered Question tab, and verify the answered topic is displayed at the Answer Questions tab");
		log.info("click Answered Question tab, and verify the answered topic is displayed at the Answer Questions tab ");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		Assert.assertTrue(driver.getVisibleElements(ui.getTopicSelector(forumTopic2)).size()==1, 
				"ERROR: the topic2 doesn't show up");
		
		logger.strongStep("Click Open Questions tab, and verify the answered topic disappears from the Open Questions tab");
		log.info("click Open Questions tab, and verify the answered topic disappears from the Open Questions tab");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		Assert.assertTrue(driver.getVisibleElements(ui.getTopicSelector(forumTopic2)).size()==0, 
				"ERROR: the topic2 shows up which is not expected");
		
		ui.endTest();	
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Test sort by for unanswered tabs</li>
	*<li><B>Step: </B>Use API to create a community</li>
	*<li><B>Step: </B>Use API to create 3 topics</li>
	*<li><B>Step: </B>Open browser, and login</li>
	*<li><B>Step: </B>navigate to the communities forum's page</li>
	*<li><B>Step: </B>Click forum in left navigation menu</li>
	*<li><B>Step: </B>Click open questions tab</li>
	*<li><B>Verify: </B>Check default sort by post time</li>
	*<li><B>Step: </B>Reply once for topic1</li> 
	*<li><B>Step: </B>Reply twice for topic2</li>
	*<li><B>Verify: </B>Check open questions tab and sort by replies</li>
	*<li><B>Step: </B>Like different topics with different times</li>
	*<li><B>Verify: </B>Check sort by likes</li>
	*</ul>
	 * @throws Exception 
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void testSortby_UnansweredTab() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		testUser1 = cfg.getUserAllocator().getUser();

		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
		
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());

		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		String commUUID = community.getCommunityUUID();
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
	
		BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("Question1 " + Data.getData().ForumTopicTitle)
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(community)
		  											  .markAsQuestion(true)
		  											  .parentForum(apiForum)
		  											  .build();
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Question2 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
		  											  .markAsQuestion(true)
		  											  .parentForum(apiForum)
													  .build();
		BaseForumTopic forumTopic3 = new BaseForumTopic.Builder("Question3 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .markAsQuestion(true)
													  .parentForum(apiForum)
													  .build();
		
		logger.strongStep("Create 3 topics using API");
		log.info("create 3 topics using API");
		ForumTopic apiForumTopic1 = forumTopic1.createAPI(apiForumsOwner);
		ForumTopic apiForumTopic2 = forumTopic2.createAPI(apiForumsOwner);
		ForumTopic apiForumTopic3 = forumTopic3.createAPI(apiForumsOwner);

		//GUI
		//Load component and login
		logger.strongStep("Open browser and log in to Communities as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comui);	
		
		logger.strongStep("Click Forums in left nav menu");
		log.info("click Forums in left nav menu");
		Community_LeftNav_Menu.FORUMS.select(comui);	

		logger.strongStep("Click the Open Questions tab");
		log.info("click the Open Questions tab");
		ui.fluentWaitElementVisible(ForumsUIConstants.OpenQuestionsTab);
		ui.clickLinkWithJavascript(ForumsUIConstants.OpenQuestionsTab);
		
		//default sort by post time(latest created topic will be displayed at top)
		logger.strongStep("Verify default sort by post time. " +
				"The first one is topic3, the second one is topic2, the last one is topic1");
		log.info("INFO:verify default sort by post time. " +
				"The first one is topic3, the second one is topic2, the last one is topic1");
		List<Element> topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic3.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic2.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic2.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic1.getTitle());
		
		//reply topic1 once
		logger.strongStep("Reply topic1 once using API");
		log.info("INFO: reply topic1 once using API");
		ForumReply apiForumReply11 = apiForumsOwner.createForumReply(apiForumTopic1, "reply");
		
		//reply topic2 twice
		logger.strongStep("Reply topic2 twice using API");
		log.info("INFO: reply topic2 twice using API");
		apiForumsOwner.createForumReply(apiForumTopic2, "reply");
		apiForumsOwner.createForumReply(apiForumTopic2, "reply");
		
		//sort by replies
		logger.strongStep("Check Open Questions tab and sort by replies");
		log.info("INFO: check Open Questions tab and sort by replies");

		log.info("click Open Questions tab");
		ui.loadComponent(Data.getData().ComponentForums,true);
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		
		log.info("click to sort by Replies");
		ui.clickLinkWait(ForumsUIConstants.RepliesLink);
		
		logger.strongStep("Verify there are 3 topics on the Open Questions tab, " +
				"topic2 is the first one, topic1 is the second one, and topic3 is the last one");
		log.info("verify there are 3 topics on the Open Questions tab, " +
				"topic2 is the first one, topic1 is the second one, and topic3 is the last one");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic2.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic2.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic1.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic3.getTitle());		
		
		//sort by Likes
		logger.strongStep("Select topic3 " + forumTopic3.getTitle() + " and like it using API");		
		log.info("INFO: select topic3 " + forumTopic3.getTitle() + " and like it using API");
		apiForumsOwner.like(apiForumTopic3);
		
		logger.strongStep("Select topic1 " + forumTopic1.getTitle() + " and like it and its reply using API");	
		log.info("INFO: select topic1 " + forumTopic1.getTitle() + " and like it and its reply using API.");		
		apiForumsOwner.like(apiForumTopic1);
		apiForumsOwner.like(apiForumReply11);

		logger.strongStep("Check open question tab and sort by Likes");	
		log.info("INFO: check open question tab and sort by Likes");
		
		log.info("click the Open Questions tab");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		log.info("click to sort by Likes");
		ui.clickLinkWait(ForumsUIConstants.LikesLink);
		
		logger.strongStep("Verify there are 3 topics on the Open Questions tab. " +
				"the First one is topic1, the second one is topic3, the last one is topic2");	
		log.info("Verify there are 3 topics on the Open Questions tab. " +
				"the First one is topic1, the second one is topic3, the last one is topic2");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic1.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic3.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic2.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic2.getTitle());		
						
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test sort by for answered tabs</li>
	*<li><B>Step: </B>Use API to create a community</li>
	*<li><B>Step: </B>Use API to create 3 topics</li>
	*<li><B>Step: </B>Use API to add 3 forum answered topics for created 3 topics</li>
	*<li><B>Step: </B>Open browser, and login</li>
	*<li><B>Step: </B>Click answered questions tab</li>
	*<li><B>Verify: </B>Check default sort by post time</li>
	*<li><B>Step: </B>Reply once for topic1</li>
	*<li><B>Step: </B>Reply twice for topic2</li> 
	*<li><B>Verify: </B>Check sort by replies in answered questions tab</li>
	*<li><B>Step: </B>Like different topics with different times</li>
	*<li><B>Verify: </B>Check sort by likes in answered questions tab</li>
	*</ul>
	 * @throws Exception 
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void testSortby_AnsweredTab() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.build();
	
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		String commUUID = community.getCommunityUUID();
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		
		BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("Question1 " + Data.getData().ForumTopicTitle)
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(community)
		  											  .markAsQuestion(true)
		  											  .parentForum(apiForum)
		  											  .build();
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Question2 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
		  											  .markAsQuestion(true)
		  											  .parentForum(apiForum)
													  .build();
		BaseForumTopic forumTopic3 = new BaseForumTopic.Builder("Question3 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .markAsQuestion(true)
													  .parentForum(apiForum)
													  .build();
		
		logger.strongStep("Create 3 topics using API");
		log.info("create 3 topics using API");
		ForumTopic apiForumTopic1 = forumTopic1.createAPI(apiForumsOwner);
		ForumTopic apiForumTopic2 = forumTopic2.createAPI(apiForumsOwner);
		ForumTopic apiForumTopic3 = forumTopic3.createAPI(apiForumsOwner);
		
		logger.strongStep("Use API to add forum answered topic " + forumTopic1.getTitle());
		log.info("INFO: use API to add forum answered topic " + forumTopic1.getTitle());		
		ForumReply apiForumReply1 = apiForumsOwner.createForumReply(apiForumTopic1, "reply");
		Assert.assertTrue(apiForumsOwner.acceptAsAnswer(apiForumReply1));

		logger.strongStep("Use Api to add forum answered topic" + forumTopic2.getTitle());
		log.info("INFO: use Api to add forum answered topic" + forumTopic2.getTitle());
		ForumReply apiForumReply2 = apiForumsOwner.createForumReply(apiForumTopic2, "reply");
		Assert.assertTrue(apiForumsOwner.acceptAsAnswer(apiForumReply2));

		logger.strongStep("Use api to add forum answered topic" + forumTopic3.getTitle());
		log.info("INFO: use api to add forum answered topic" + forumTopic3.getTitle());
		ForumReply apiForumReply3 = apiForumsOwner.createForumReply(apiForumTopic3, "reply");
		Assert.assertTrue(apiForumsOwner.acceptAsAnswer(apiForumReply3));
		
		//GUI
		//Load component and login
		logger.strongStep("Open browser and log in to Communities as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
						+ "/communities/service/html/communityoverview?" + commUUID;
		naviToForums(overview);
		
		logger.strongStep("Click the Answered Questions tab");
		log.info("INFO: click the Answered Questions tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		
		//default sort by post time
		logger.strongStep("Verify default sort by post time. " +
				"The first one is topic3, the second one is topic2, the last one is topic1");
		log.info("INFO: verify default sort by post time. " +
				"The first one is topic3, the second one is topic2, the last one is topic1");
		List<Element> topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic3.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic2.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic2.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic1.getTitle());
		
		//reply topic1 once
		logger.strongStep("Use API to reply " + forumTopic1.getTitle() + " once");
		log.info("INFO: use API to reply " + forumTopic1.getTitle() + " once");
		ForumReply apiReply11 = apiForumsOwner.createForumReply(apiForumTopic1, "reply");
				
		//reply topic2 twice
		logger.strongStep("Use API to reply " + forumTopic2.getTitle() + " twice");
		log.info("INFO: use API to reply " + forumTopic2.getTitle() + " twice");		
		apiForumsOwner.createForumReply(apiForumTopic2, "reply");
		apiForumsOwner.createForumReply(apiForumTopic2, "reply");
		
		//sort by replies
		logger.strongStep("Goto the Answered Questions tab and sort by Replies");
		log.info("INFO: goto the Answered Questions tab and sort by Replies");

		log.info("click the Answered Questions tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		log.info("click to sort by replies");
		ui.clickLinkWait(ForumsUIConstants.RepliesLink);
		
		logger.strongStep("Verify there are 3 topics on the Answered Questions tab. " +
				"the first one is topic2, the second one is topic1, the last one is topic3");
		log.info("verify there are 3 topics on the Answered Questions tab. " +
				"the first one is topic2, the second one is topic1, the last one is topic3");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic2.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic2.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic1.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic3.getTitle());		
		
		//sort by Likes
		logger.strongStep("Use API to select topic " + forumTopic3.getTitle() + " and like it");
		log.info("INFO: use API to select topic " + forumTopic3.getTitle() + " and like it");
		apiForumsOwner.like(apiForumTopic3);
		
		logger.strongStep("Use API to select topic " + forumTopic1.getTitle() + " and like topic and its reply .");
		log.info("INFO: use API to select topic " + forumTopic1.getTitle() + " and like topic and its reply .");
		apiForumsOwner.like(apiForumTopic1);
		apiForumsOwner.like(apiReply11);
		
		logger.strongStep("Goto Answered tab and sort by likes.");
		log.info("INFO: goto Answered tab and sort by likes.");
		
		log.info("click the Anwsered Questions tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		log.info("click to sort by likes");
		ui.clickLinkWait(ForumsUIConstants.LikesLink);
		
		logger.strongStep("Verify there are 3 topics on the Answered Questions tab. the first one is topic1, " +
				"the second one is topic3, the third one is topic2");
		log.info("verify there are 3 topics on the Answered Questions tab. the first one is topic1, " +
				"the second one is topic3, the third one is topic2");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic1.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic3.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic2.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic2.getTitle());
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Test pin and unpin topic  </li>
	*<li><B>Step: </B>Use API to create community</li>
	*<li><B>Step: </B>Use API to create 2 normal topics</li>
	*<li><B>Step: </B>Use API to create 2 open questions</li>
	*<li><B>Step: </B>Use API to create 2 answered questions</li>
	*<li><B>Step: </B>Open browser, and login</li>
	*<li><B>Verify: </B>Check topics on topics tab and sorted by topic creation</li>
	*<li><B>Verify: </B>Check 2 normal topics on open questions tab</li>
	*<li><B>Verify: </B>Check 2 answered questions on answered questions tab</li>
	*<li><B>Step: </B>Pin 5th topic</li>
	*<li><B>Step: </B>Pin 3rd topic</li>
	*<li><B>Step: </B>Pin 1st topic</li>
	*<li><B>Verify: </B>Check the order after pin the topics</li>
	*<li><B>Verify: </B>Check 1 normal topic and 1 pinned topic on answered questions tab</li>
	*<li><B>Verify: </B>Check 1 normal topic and 1 pinned topic on open questions tab</li> 
	*<li><B>Step: </B>Unpin 3rd topic</li>
	*<li><B>verify: </B>Check topic count after unpin the topic</li>
	*</ul>
	 * @throws Exception 
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void testPinUnpinTopic() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		String commUUID = community.getCommunityUUID();
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
	
		BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("Topic1 " + Data.getData().ForumTopicTitle)
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(community)
		  											  .parentForum(apiForum)
		  											  .build();
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Topic2 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .parentForum(apiForum)
													  .build();
		BaseForumTopic forumTopic3 = new BaseForumTopic.Builder("Question1 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .markAsQuestion(true)
													  .parentForum(apiForum)
													  .build();
		BaseForumTopic forumTopic4 = new BaseForumTopic.Builder("Question2 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .markAsQuestion(true)
													  .parentForum(apiForum)
													  .build();
		BaseForumTopic forumTopic5 = new BaseForumTopic.Builder("Answer1 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .markAsQuestion(true)
													  .parentForum(apiForum)
													  .build();
		BaseForumTopic forumTopic6 = new BaseForumTopic.Builder("Answer2 " + Data.getData().ForumTopicTitle)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)
													  .partOfCommunity(community)
													  .markAsQuestion(true)
													  .parentForum(apiForum)
													  .build();
		
		
		logger.strongStep("Use API to create 6 topics: topic1 and topic2 are normal topics, " +
				"topic3 and topic4 are open questions, topic5 and topic6 are answered questions");
		log.info("use API to create 6 topics: topic1 and topic2 are normal topics, " +
				"topic3 and topic4 are open questions, topic5 and topic6 are answered questions");
		forumTopic1.createAPI(apiForumsOwner);
		forumTopic2.createAPI(apiForumsOwner);
		forumTopic3.createAPI(apiForumsOwner);
		forumTopic4.createAPI(apiForumsOwner);		
		ForumTopic apiForumTopic5 = forumTopic5.createAPI(apiForumsOwner);
		ForumTopic apiForumTopic6 = forumTopic6.createAPI(apiForumsOwner);

		logger.strongStep("Use API to set topic5 and topic6 as answered");
		log.info("use API to set topic5 and topic6 as answered ");
		ForumReply apiForumReply5 = apiForumsOwner.createForumReply(apiForumTopic5, "reply");
		Assert.assertTrue(apiForumsOwner.acceptAsAnswer(apiForumReply5));
		ForumReply apiForumReply6 = apiForumsOwner.createForumReply(apiForumTopic6, "reply");
		Assert.assertTrue(apiForumsOwner.acceptAsAnswer(apiForumReply6));

		//GUI
		//Load component and login
		logger.strongStep("Open browser and log in to Communities as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()) 
						+ "/communities/service/html/communityoverview?" + commUUID;
		
		naviToForums(overview);
		//verify the order after creation
		logger.strongStep("Verify there are 6 normal topics on the Topics tab, and the order is correct as per topic creation");
		log.info("INFO: verify there are 6 normal topics on the Topics tab, and the order is correct as per topic creation");
		List<Element> topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 6, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic5.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic5.getTitle());		
		Assert.assertTrue(topicList.get(3).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 4th topic is not " + forumTopic3.getTitle());
		Assert.assertTrue(topicList.get(5).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 6th topic is not " + forumTopic1.getTitle());

		naviToForums(overview);

		logger.strongStep("Goto Open Question tab, and check 2 normal topics there");
		log.info("INFO: goto Open Question tab, and check 2 normal topics there");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		
		logger.strongStep("Verify there are 2 topics on the Open Questions tab and the first one is topic4");
		log.info("verify there are 2 topics on the Open Questions tab and the first one is topic4");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 2, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic4.getTitle()), 
				"ERROR: The 1st event is not " + forumTopic4.getTitle());	
		
		logger.strongStep("Goto Answered Question tab, and check 2 normal topics there");
		log.info("INFO: goto Answered Question tab, and check 2 normal topics there");
		
		log.info("click the Answered Questions tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		
		logger.strongStep("verify there are 2 anwsered questions on the Answered Questions tab " +
				"and the first one is topic6");
		log.info("verify there are 2 anwsered questions on the Answered Questions tab " +
				"and the first one is topic6");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 2, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic6.getTitle()), 
				"ERROR: The 1st event is not " + forumTopic6.getTitle());	
				
		//pin topics
		logger.strongStep("Pin topic " + forumTopic5.getTitle());
		log.info("INFO: pin topic " + forumTopic5.getTitle());
		ui.clickPinTopic(forumTopic5);
		
		logger.strongStep("Verify pin successful message");
		log.info("verify pin successful message");
		Assert.assertTrue(driver.isTextPresent(Data.getData().Successful_Pin_Msg), 
				"ERROR: Pin topic doesn't successful.");
		
		logger.strongStep("Click back to Forums tab");
		log.info("click back to Forums tab");
		ui.clickLinkWait(ui.getBackToCommunityForum(community));	
		
		logger.strongStep("pin topic " + forumTopic3.getTitle());
		log.info("INFO: pin topic " + forumTopic3.getTitle());
		ui.clickPinTopic(forumTopic3);
		
		logger.strongStep("Verify pin successful message");
		log.info("verify pin successful message");
		Assert.assertTrue(driver.isTextPresent(Data.getData().Successful_Pin_Msg), 
				"ERROR: Pin topic doesn't successful.");
		
		logger.strongStep("click back to Forums tab");
		log.info("click back to Forums tab");		
		ui.clickLinkWait(ui.getBackToCommunityForum(community));
		
		logger.strongStep("Pin topic " + forumTopic1.getTitle());
		log.info("INFO: pin topic " + forumTopic1.getTitle());
		ui.clickPinTopic(forumTopic1);
		
		logger.strongStep("Verify pin successful message");
		log.info("verify pin successful message");		
		Assert.assertTrue(driver.isTextPresent(Data.getData().Successful_Pin_Msg), 
				"ERROR: Pin topic doesn't successful.");
		
		logger.strongStep("Click back to Forums tab");
		log.info("click back to Forums tab");		
		ui.clickLinkWait(ui.getBackToCommunityForum(community));
		
		//verify the order after pin the topics
		logger.strongStep("3 normal topics after pin on the Topics tab");
		log.info("INFO: 3 normal topics after pin on the Topics tab");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all normal topics show up");
		
		logger.strongStep("3 Pinned topics in the Topics tab");
		log.info("INFO: 3 pinned topics in the Topics tab");
		topicList = driver.getElements(ForumsUIConstants.PinedTopicList);
		Assert.assertEquals(topicList.size(), 3, "ERROR: not all pined topics show up");
		Assert.assertTrue(topicList.get(0).getAttribute("class").contains("lotusPinnedRow"));
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic5.getTitle()), 
				"ERROR: The 1st topic is not " + forumTopic5.getTitle());		
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 2nd topic is not " + forumTopic3.getTitle());
		Assert.assertTrue(topicList.get(2).getText().contains(forumTopic1.getTitle()), 
				"ERROR: The 3rd topic is not " + forumTopic1.getTitle());
		
		log.info("INFO: 1 normal and 1 pinned topics in Answered Question tab");
		
		naviToForums(overview);
		logger.strongStep("Click the Answered Questions tab");
		log.info("click the Answered Questions tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab);
		
		logger.strongStep("Verify there is 1 normal topic on Answered Questions tab");
		log.info("verify there is 1 normal topic on Answered Questions tab");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 1, "ERROR: not all topics show up");
		
		logger.strongStep("Verify there is 1 pinned topic on the Answered Quesions tab");
		log.info("verify there is 1 pinned topic on the Answered Quesions tab");
		topicList = driver.getVisibleElements(ForumsUIConstants.PinedTopicList);
		Assert.assertEquals(topicList.size(), 1, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic5.getTitle()), 
				"ERROR: The pinned topic is not " + forumTopic5.getTitle());	
		
		log.info("INFO: 1 normal and 1 pinned topics in Open Questions tab");
		
		logger.strongStep("Click the Open Questions tab");
		log.info("click the Open Questions tab");		
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		
		logger.strongStep("Verify there is 1 normal topic on Open Questions tab");
		log.info("verify there is 1 normal topic on Open Questions tab");		
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 1, "ERROR: not all topics show up");
		
		logger.strongStep("Verify there is 1 pinned topic on Open Questions tab");
		log.info("verify there is 1 pinned topic on Open Questions tab");		
		topicList = driver.getVisibleElements(ForumsUIConstants.PinedTopicList);
		Assert.assertEquals(topicList.size(), 1, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(0).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The pinned topic is not " + forumTopic3.getTitle());	
			
		//Unpin the question
		logger.strongStep("Unpin the topic " + forumTopic3.getTitle() + " and verify the successful message");
		log.info("INFO: Unpin the topic " + forumTopic3.getTitle() + " and verify the successful message");
		ui.clickUnPinTopic(forumTopic3);
		Assert.assertTrue(driver.isTextPresent(Data.getData().Successful_UnPin_Msg), 
				"ERROR: UnPin topic doesn't successful.");
		
		logger.strongStep("Click back to Forums tab");
		log.info("click back to Forums tab");
		ui.clickLinkWait(ui.getBackToCommunityForum(community));
		
		//verify the topics count after unpin the topic
		logger.strongStep("4 normal topics in topic list tab");
		log.info("INFO: 4 normal topics in topic list tab");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 4, "ERROR: not all normal topics show up");
		
		logger.strongStep("2 pinned topics in topic list tab");
		log.info("INFO: 2 pinned topics in topic list tab");
		topicList = driver.getElements(ForumsUIConstants.PinedTopicList);
		Assert.assertEquals(topicList.size(), 2, "ERROR: not all pined topics show up");
		
		naviToForums(overview);
		
		logger.strongStep("2 normal topics in Open Question tab");
		log.info("INFO: 2 normal topics in Open Question tab");
		
		logger.strongStep("Click the Open Questions tab");
		log.info("click the Open Questions tab");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab);
		
		logger.strongStep("Verify there are 2 normal topics on the Open Questions tab after unpin");
		log.info("verify there are 2 normal topics on the Open Questions tab after unpin");
		topicList = driver.getVisibleElements(ForumsUIConstants.TopicList);
		Assert.assertEquals(topicList.size(), 2, "ERROR: not all topics show up");
		Assert.assertTrue(topicList.get(1).getText().contains(forumTopic3.getTitle()), 
				"ERROR: The 2 topic is not " + forumTopic3.getTitle());	
			
		
		ui.endTest();
	}


	/**
	*<ul>
	*<li><B>Info: Create a community with name, tag, public, add member and description</B></li>
	*<li><B>Step: </B>Open browser, and login </li>
	*<li><B>Step: </B>Create the community</li>
	*<li><B>Step: </B>Add a forum topic with description and tag</li>
	*<li><B>Step: </B>Logout as first user</li>
	*<li><B>Step: </B>Login as second user</li>
	*<li><B>Step: </B>Navigate to the public community view</li>
	*<li><B>Step: </B>Go to Forums</li>
	*<li><B>verify: </B>Check forum topic is present</li>
	*<li><B>Step: </B>Return to the overview view</li>
	*<li><B>verify: </B>Check topic is present in this view</li>
	*<li><B>Step: </B>Logout as second user</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void createPublicCommunityForum() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		//Start of the test
		String testName = ui.startTest();
		String communityName = testName + Helper.genDateBasedRand();
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + communityName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
	
		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(community)
		  											  .build();
		
		//create the community
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Load component and login
		logger.strongStep("Open browser and log in to Communities as: " + testUser1.getDisplayName());
		log.info("INFO:open browser and log in to Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		
		if (flag) {
			
		    logger.strongStep("Add the Overview page to the Community and make it the landing page");
		    log.info("INFO: Add the Overview page to the Community and make it the landing page");
		    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		
		}
		
		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(comui);
		String publicViewURL = driver.getCurrentUrl();
		
		//Add a forum topic with description and tag
		logger.strongStep("Add a forum topic");
		log.info("INFO: Add a forum topic");
		forumTopic.create(ui);
		Assert.assertTrue(driver.isTextPresent(forumTopic.getTitle()));
		
		//logout as user 1
		logger.strongStep("Logout as user1");
		log.info("INFO:logout as user1");
		ui.logout();
		
		//Login as user 2
		logger.strongStep("Log in to Communities as user2");
		log.info("INFO:log in to Communities as user2");
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);
		
		//Ensure that the public community view is loaded and then open the community from this view
		driver.navigate().to(publicViewURL);
		
		//now verify that the forum title is present in the forum view
		logger.strongStep("Select Forums from the left navigation menu");
		log.info("INFO: Select Forums from the left navigation menu");
		Community_LeftNav_Menu.FORUMS.select(comui);

		ui.waitForPageLoaded(driver);
		Assert.assertTrue(driver.isElementPresent(ui.getTopicSelector(forumTopic)));
		
		//now return to the Overview view and verify that the topic is listed in this view too
		logger.strongStep("Select Overview from community left navigation menu");
		log.info("INFO: Select Overview from community left navigation menu");
		Community_LeftNav_Menu.OVERVIEW.select(comui);
		
		ui.waitForPageLoaded(driver);
		Assert.assertTrue(driver.isElementPresent(ui.getTopicSelector(forumTopic)));
		
		//logout as user 2
		ui.logout();
		
		//End of test
		ui.endTest();
		
	}
	
}
