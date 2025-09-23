package com.ibm.conn.auto.tests.forums.regression;

import java.util.ArrayList;


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
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class MyForums extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(MyForums.class);
	private ForumsUI ui;

	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner, apiForumsOwner2;
	
	private User testUser1;
	private User testUser2;
	String serverURL ;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsOwner2 = new APIForumsHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
			
	}
	
	
	private ForumTopic createOpenQuestion(Forum forum, String testName){
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Question for " + testName)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(true)
													  .parentForum(forum)
													  .build();
		ForumTopic apiForumTopic = forumTopic2.createAPI(apiForumsOwner);
		return apiForumTopic;
		
	}
	
	private ForumTopic createAnsweredQuestion(Forum forum, String testName){
		ForumTopic apiForumTopic = createOpenQuestion(forum, testName);
		ForumReply apiReply = apiForumsOwner.createForumReply(apiForumTopic, "testing reply");
		try{
			apiForumsOwner.acceptAsAnswer(apiReply);
		}catch(Exception e){
			log.error("failed to accept as answer");
		}
		return apiForumTopic;
		
	}
	private void verifyForumsPresent(ArrayList<Forum> list){
		
		for(int i=0; i<list.size();i++){
			String title = list.get(i).getTitle();
			Assert.assertTrue(driver.isTextPresent(title), title + "is NOT on the Forums tab");
			
		}
	}

	private void verifyTopicsPresent(ArrayList<ForumTopic> list, String tabName){
		for(int i=0; i<list.size();i++){
			String title = list.get(i).getTitle();
			Assert.assertTrue(driver.isTextPresent(title), title + "is NOT on the "+ tabName +" tab");
			
		}
	}
	
	private ForumTopic createTopic(Forum forum, String testName){
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Topic for " + testName)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(false)
													  .parentForum(forum)
													  .build();
		ForumTopic apiForumTopic = forumTopic2.createAPI(apiForumsOwner);
		return apiForumTopic;
	}
	
	
	/**
	 * TEST CASE: verify self created forums and topics were followed, and shown up correctly on different tabs of I'm an owner view
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>verify different tabs of I'm an Owner view</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Get the default api community forum and add it to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the default community forum. </li>
	 * <li><B>Step: </B>Use API to create an open question in the default community forum. </li>
	 * <li><B>Step: </B>Use API to create an answered question in the default community forum. </li>
	 * <li><B>Step: </B>Use API to create the 2nd community forum </li>
	 * <li><B>Step: </B>Use API to add the 2nd community forum to the forum list</li>
	 * <li><B>Step: </B>Use API to create a topic in the 2nd community forum and add it to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the 2nd community forum and add it to the open question list</li>
	 * <li><B>Step: </B>Use API to create an answered question in the 2nd community forum and add it to the answered question list</li>
	 * <li><B>Step: </B>Use API to create a stand alone forum, </li>
	 * <li><B>Step: </B>Add the stand alone forum to the forum list </li>
	 * <li><B>Step: </B>Use API to add testUser2 to the stand alone forum as an owner </li>
	 * <li><B>Step: </B>Use API to create a topic in the stand alone forum and add it to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the stand alone forum and add it to the open question list </li>
	 * <li><B>Step: </B>Use API to create an answered question in the stand alone forum and add it to the answered question list </li>
	 
	 * <li><B>Step: </B>Open browser, and login to Forums as the Owner(Creator)</li>
	 * 
	 * <li><B>Step: </B>click I'm an Owner (it's on the Topics tab)</li>
	 * <li><B>Verify: </B>verify all topics, including common topics, answered questions, open questions, shown up on the Topics tab</li>
	 
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify every forum, including the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Forums tab</li>
	
	 * <li><B>Step: </B>click the Answered Questions tab</li>
	 * <li><B>Verify: </B>verify all answered questions, including questions in the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Answered Questions tab</li>
	 * 
	 * <li><B>Step: </B>click the Open Questions tab</li>
	 * <li><B>Verify: </B>verify all open questions, including questions in the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab</li>	 
	 * </ul>
	 */
	@Test(groups={"regression"})
	public void test4Tabs_ImAnOwner(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data");
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community "+rand;
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>(); 
		ArrayList<ForumTopic> topicList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> openQuestionList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> answeredQuestionList = new ArrayList<ForumTopic>();
		
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		
		//create community	
		logger.strongStep("Use API to create community");
		log.info("INFO: use API to create community");
		Community apiCommunity = community.createAPI(apiOwner);		
		
		logger.strongStep("Get the default api community forum");
		log.info("INFO: get the default api community forum");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Add the default api community forum to the forum list");
		log.info("INFO: add the default api community forum to the forum list");
		apiForumList.add(apiForum);
		
		logger.strongStep("Use API to create a common topic and add it to the topic list");
		log.info("INFO: use API to create a common topic and add it to the topic list");			
		topicList.add(createTopic(apiForum, testName));	
		
		logger.strongStep("Use API to create an open question and add it to the open question list");
		log.info("INFO: use API to create an open question and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum, testName));
		
		logger.strongStep("Use API to create an answered question and add it to the answered question list");
		log.info("INFO: use API to create an answered question and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum, testName));
						
		String urlToCreateCommunityForums = serverURL +"/forums/atom/forums?"+ commUUID;
		
		BaseForum forum2 = new BaseForum.Builder("second forum in the community "+ communityName)
											.tags(Data.getData().commonTag + rand)
											.description("second forum in the community")
											.build();
		
		logger.strongStep("Use API to create the 2nd community forum");
		log.info("INFO: use API to create the 2nd community forum");
		Forum apiForum2 = apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2);
		
		logger.strongStep("Use API to add the 2nd community forum to the forum list");
		log.info("INFO: use API to add the 2nd community forum to the forum list");
		apiForumList.add(apiForum2);
		
		logger.strongStep("use API to create a topic in the 2nd community forum, and add it to the topic list");
		log.info("INFO: use API to create a topic in the 2nd community forum, and add it to the topic list");
		topicList.add(createTopic(apiForum2, testName));			
		
		logger.strongStep("Use API to create an open question in the 2nd community forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the 2nd community forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum2, testName));		
		
		logger.strongStep("Use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum2, testName));
		
		BaseForum standAloneForum = new BaseForum.Builder("stand alone " + rand)
										.tags(testName)
										.description("Stand alone Forum, Test My Forums - I'm an Owner view")
										.build();
		
		logger.strongStep("Use API to create a stand alone forum");
		log.info("INFO: use API to create a stand alone forum");
		Forum apiStandAloneForum = standAloneForum.createAPI(apiForumsOwner);
		
		logger.strongStep("Add the stand alone forum to the forum list");
		log.info("INFO: add the stand alone forum to the forum list");
		apiForumList.add(apiStandAloneForum);
		
		logger.strongStep("Use API to add testUser2 to the stand alone forum as an owner");
		log.info("INFO: use API to add testUser2 to the stand alone forum as an owner");
		apiForumsOwner.addOwnertoForum(apiStandAloneForum, testUser2);
		
		logger.strongStep("Use API to create a topic in the stand alone forum and add it to the topic list");
		log.info("INFO: use API to create a topic in the stand alone forum and add it to the topic list");
		topicList.add(createTopic(apiStandAloneForum, testName));			
		
		logger.strongStep("Use API to create an open question in the stand a lone forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the stand a lone forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiStandAloneForum, testName));		
		
		logger.strongStep("Use API to create an answered question in the stand a lone forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the stand a lone forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiStandAloneForum, testName));
			
		log.info("INFO: END of preparing data");
		
		//Load component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: open browser and log in to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Click I'm an owner'");
		log.info("INFO: click I'm an owner'");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		logger.strongStep("Verify every common topic(including the topics in the default community forum, " +
				"the 2nd community forum, and the standalone forum) shown up on the Topics tab");
		log.info("INFO: verify every common topic(including the topics in the default community forum, " +
				"the 2nd community forum, and the standalone forum) shown up on the Topics tab");
		verifyTopicsPresent(topicList, "Topics");
		
		logger.strongStep("Verify every answered question, including questions in including the default community forum, " +
				"the 2nd community forum, and the standalone forum, is displayed at the Topics tab");
		log.info("INFO: verify every answered question, including questions in including the default community forum, " +
				"the 2nd community forum, and the standalone forum, is displayed at the Topics tab" );
		verifyTopicsPresent(answeredQuestionList, "Topics");
		
		logger.strongStep("Verify the open question, including questions in the default community forum, " +
				"the 2nd community forum, and the standalone forum, disappears from the Topics tab");
		log.info("INFO: verify the open question, including questions in the default community forum, " +
				"the 2nd community forum, and the standalone forum, disappears from the Topics tab");
		verifyTopicsPresent(openQuestionList, "Topics");
			
		logger.strongStep("Click the FORUMS tab");
		log.info("INFO: click the FORUMS tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		logger.strongStep("Verify every forum, including the default community forum, the 2nd community forum, " +
				"and the stand alone forum, shown up on the Forums tab");
		log.info("INFO: verify every forum, including the default community forum, the 2nd community forum, " +
				"and the stand alone forum, shown up on the Forums tab" );
		verifyForumsPresent(apiForumList);
		
		logger.strongStep("Click Answered Question tab");
		log.info("INFO: click Answered Question tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab_SA);
		
		logger.strongStep("Verify every answered question, including questions in including the default community forum, " +
				"the 2nd community forum, and the stand alone forum, is displayed at the Answer Questions tab");
		log.info("INFO: verify every answered question, including questions in including the default community forum, " +
				"the 2nd community forum, and the stand alone forum, is displayed at the Answer Questions tab" );
		verifyTopicsPresent(answeredQuestionList, "Answered Questions");
		
		logger.strongStep("Click Open Questions tab");
		log.info("INFO: click Open Questions tab ");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab_SA);
		
		logger.strongStep("Verify the open question, including questions in the default community forum, " +
				"the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab");
		log.info("INFO: verify the open question, including questions in the default community forum, " +
				"the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab");
		verifyTopicsPresent(openQuestionList, "Open Questions");
		
		
		ui.endTest();
		
			
	}
	/**
	 * TEST CASE: verify owner created the forums and topics show up correctly on different tabs of the I'm a Member view
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>verify different tabs of I'm a Member view</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Get the default community forum and add it to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the default community forum and add it to the topic list</li>
	 * <li><B>Step: </B>Use API to create an open question in the default community forum and add it to the open question list</li>
	 * <li><B>Step: </B>Use API to create an answered question in the default community forum and add it to the answered question list</li>
	 * <li><B>Step: </B>Use API to create the 2nd community forum </li>
	 * <li><B>Step: </B>Add the 2nd community forum to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the 2nd community forum and add it to the topic list</li>
	 * <li><B>Step: </B>Use API to create an open question in the 2nd community forum and add it to the open question list</li>
	 * <li><B>Step: </B>Use API to create an answered question in the 2nd community forum and add it to the answered question list</li>
		 
	 * <li><B>Step: </B>Open browser, and login to Forums as a Member</li>
	 * 
	 * <li><B>Step: </B>click I'm a Member (it's on the Topics tab)</li>
	 * <li><B>Verify: </B>verify all topics, including common topics, answered questions, open questions, shown up on the Topics tab</li>
	 
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify every forum, including the default community forum, and the 2nd community forum, shown up on the Forums tab</li>
	
	 * <li><B>Step: </B>click the Answered Questions tab</li>
	 * <li><B>Verify: </B>verify all answered questions, including questions in the default community forum, and the 2nd community forum, shown up on the Answered Questions tab</li>
	 * 
	 * <li><B>Step: </B>click the Open Questions tab</li>
	 * <li><B>Verify: </B>verify all open questions, including questions in the default community forum, and the 2nd community forum, shown up on the Open Questions tab</li>
	 * </ul>
	 */
	@Test(groups={"regression"})
	public void test4Tabs_ImAMember(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data");
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community "+rand;
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		ArrayList<ForumTopic> topicList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> openQuestionList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> answeredQuestionList = new ArrayList<ForumTopic>();
		
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community	
		logger.strongStep("Use API to create community");
		log.info("INFO: use API to create community");
		Community apiCommunity = community.createAPI(apiOwner);	
	
		logger.strongStep("Get the default community forum");
		log.info("INFO: get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);
		Forum apiDefaultForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Add the default community forum to the forum list");
		log.info("INFO: add the default community forum to the forum list");
		apiForumList.add(apiDefaultForum);	
		
		logger.strongStep("Use API to create a common topic in the default community forum, and add it to the topic list");
		log.info("INFO: use API to create a common topic in the default community forum, and add it to the topic list");
		topicList.add(createTopic(apiDefaultForum, testName));	
		
		logger.strongStep("Use API to create an open question in the default community forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the default community forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiDefaultForum, testName));
		
		logger.strongStep("Use API to create a answered question in the default community forum, and add it to the answered question list");
		log.info("INFO: use API to create a answered question in the default community forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiDefaultForum, testName));
		
		String urlToCreateCommunityForums = serverURL +"/forums/atom/forums?"+ commUUID;
		
		BaseForum forum2 = new BaseForum.Builder("second forum in the community "+ communityName)
											.tags(Data.getData().commonTag + rand)
											.description("second forum in the community")
											.build();
		
		logger.strongStep("Use API to create the 2nd community forum");
		log.info("INFO: use API to create the 2nd community forum");
		Forum apiForum2 = apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2);
		
		logger.strongStep("Add the 2nd community forum to the forum list");
		log.info("INFO: add the 2nd community forum to the forum list");
		apiForumList.add(apiForum2);
		
		logger.strongStep("Use API to create a common topic in the 2nd community forum, and add it to the topic list");
		log.info("INFO: use API to create a common topic in the 2nd community forum, and add it to the topic list");
		topicList.add(createTopic(apiForum2, testName));			
		
		logger.strongStep("Use API to create an open question in the 2nd community forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the 2nd community forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum2, testName));		
		
		logger.strongStep("Use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum2, testName));
		
		//Load component and Login
		logger.strongStep("Open browser, and login to Forums as:" + testUser2.getDisplayName());	
		log.info("INFO: open browser and login to Forums as a member");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser2);
		
		logger.strongStep("Click I'm a Member");
		log.info("INFO: click I'm a Member");
		ui.clickLinkWait("link=I'm a Member");
		
		logger.strongStep("Verify every common topic(including the topics in the default community forum, " +
				"and the 2nd community forum) shown up on the Topics tab");
		log.info("INFO: verify every common topic(including the topics in the default community forum, " +
				"and the 2nd community forum) shown up on the Topics tab");
		verifyTopicsPresent(topicList, "Topics");
		
		logger.strongStep("Verify every answered question, including questions in including the default community forum," +
				" and the 2nd community forum,  is displayed at the Topics tab");
		log.info("INFO: verify every answered question, including questions in including the default community forum," +
				" and the 2nd community forum,  is displayed at the Topics tab" );
		verifyTopicsPresent(answeredQuestionList, "Topics");
		
		logger.strongStep("Verify the open question, including questions in the default community forum," +
				" and the 2nd community forum, shown up on  the Topics tab");
		log.info("INFO: verify the open question, including questions in the default community forum," +
				" and the 2nd community forum, shown up on  the Topics tab");
		verifyTopicsPresent(openQuestionList, "Topics");
		
		logger.strongStep("Click the Forums tab");
		log.info("INFO: click the Forums tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		logger.strongStep("Verify every forum, including questions in including the default community forum, " +
				"and the 2nd community forum,  is displayed at the Forums tab");
		log.info("INFO: verify every forum, including questions in including the default community forum, " +
				"and the 2nd community forum,  is displayed at the Forums tab" );
		verifyForumsPresent(apiForumList);
		
		
		logger.strongStep("Click Answered Question tab, and verify the answered topic is displayed at the Answer Questions tab");
		log.info("INFO: click Answered Question tab, and verify the answered topic is displayed at the Answer Questions tab ");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab_SA);
		
		logger.strongStep("Verify every answered question, including questions in including the default community forum," +
				" and the 2nd community forum,  is displayed at the Answered Questions tab");
		log.info("INFO: verify every answered question, including questions in including the default community forum," +
				" and the 2nd community forum,  is displayed at the Answered Questions tab" );
		verifyTopicsPresent(answeredQuestionList, "Answered Questions");

		logger.strongStep("Click Open Questions tab, and verify the answered topic shown up on the Answered Questions tab");
		log.info("INFO: click Open Questions tab, and verify the answered topic shown up on the Answered Questions tab");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab_SA);
		
		logger.strongStep("Verify every open question, including questions in including the default community forum, " +
				"and the 2nd community forum,  is displayed at the Open Questions tab");
		log.info("INFO: verify every open question, including questions in including the default community forum, " +
				"and the 2nd community forum,  is displayed at the Open Questions tab" );
		verifyTopicsPresent(openQuestionList, "Open Questions");
		
		
		ui.endTest();
	}
	
	/**
	 * TEST CASE: verify self created forums and topics were followed, and not shown up on different tabs of I'm following view
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>verify different tabs of I'm Following view</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Get the default community forum and add it to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the default community forum and add it to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the default community forum and add it to the open question list</li>
	 * <li><B>Step: </B>Use API to create an answered question in the default community forum and add it to the answered question list</li>
	 * <li><B>Step: </B>Use API to create the 2nd community forum </li>
	 * <li><B>Step: </B>Use API to add the 2nd community forum to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the 2nd community forum and add it to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the 2nd community forum and add it to the open question list. </li>
	 * <li><B>Step: </B>Use API to create an answered question in the 2nd community forum and add it to the answered question list. </li>
	 * <li><B>Step: </B>Use API to create a stand alone forum, </li>
	 * <li><B>Step: </B>Add stand alone forum to the forum list </li>
	 * <li><B>Step: </B>Use API to add testUser2 to the stand alone forum as an owner </li>
	 * <li><B>Step: </B>Use API to create a topic in the stand alone forum. </li>
	 * <li><B>Step: </B>Use API to create an open question in the stand alone forum. </li>
	 * <li><B>Step: </B>Use API to create an answered question in the stand alone forum. </li>
	 
	 * <li><B>Step: </B>Open browser, and login to Forums as a Member(Not creator, for StandAlone forum, it's another forum owner)</li>
	 * 
	 * <li><B>Step: </B>click I'm Following (it's on the Topics tab)</li>
	 * <li><B>Verify: </B>verify all topics, including common topics, answered questions, open questions,NOT shown up on the Topics tab</li>
	 
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify every forum, including the default community forum, the 2nd community forum, and the stand alone forum, NOT shown up on the Forums tab</li>
	
	 * <li><B>Step: </B>click the Answered Questions tab</li>
	 * <li><B>Verify: </B>verify all answered questions, including questions in the default community forum, the 2nd community forum, and the stand alone forum, NOT shown up on the Answered Questions tab</li>
	 * 
	 * <li><B>Step: </B>click the Open Questions tab</li>
	 * <li><B>Verify: </B>verify all open questions, including questions in the default community forum, the 2nd community forum, and the stand alone forum, NOT shown up on the Open Questions tab</li>
	 * </ul>
	 */
	@Test(groups={"regression"})
	public void test4Tabs_ImFollowing_MemberNotAutoFollowing(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community "+rand;
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>(); 
		ArrayList<ForumTopic> topicList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> openQuestionList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> answeredQuestionList = new ArrayList<ForumTopic>();
		
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community	
		logger.strongStep("Use API to create community");
		log.info("INFO: use API to create community");
		Community apiCommunity = community.createAPI(apiOwner);
			
		logger.strongStep("Get the default api community forum");
		log.info("INFO: get the default api community forum");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Add the default api community forum to the forum list");
		log.info("INFO: add the default api community forum to the forum list");
		apiForumList.add(apiForum);
		
		logger.strongStep("Use API to create a common topic and add it to the topic list");
		log.info("INFO: use API to create a common topic and add it to the topic list");			
		topicList.add(createTopic(apiForum, testName));	
		
		logger.strongStep("Use API to create an open question and add it to the open question list");
		log.info("INFO: use API to create an open question and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum, testName));
		
		logger.strongStep("Use API to create an answered question and add it to the answered question list");
		log.info("INFO: use API to create an answered question and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum, testName));
			
		String urlToCreateCommunityForums = serverURL +"/forums/atom/forums?"+ commUUID;		

		BaseForum forum2 = new BaseForum.Builder("second forum in the community "+ communityName)
											.tags(Data.getData().commonTag + rand)
											.description("second forum in the community")
											.build();
		
		logger.strongStep("Use API to create the 2nd community forum");
		log.info("INFO: use API to create the 2nd community forum");
		Forum apiForum2 = apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2);
		
		logger.strongStep("Use API to add the 2nd community forum to the forum list");
		log.info("INFO: use API to add the 2nd community forum to the forum list");
		apiForumList.add(apiForum2);
		
		logger.strongStep("Use API to create a topic in the 2nd community forum, and add it to the topic list");
		log.info("INFO: use API to create a topic in the 2nd community forum, and add it to the topic list");
		topicList.add(createTopic(apiForum2, testName));			
		
		logger.strongStep("Use API to create an open question in the 2nd community forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the 2nd community forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum2, testName));		
		
		logger.strongStep("Use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum2, testName));
		
		logger.strongStep("Use API to create a stand alone forum");
		log.info("INFO: use API to create a stand alone forum");
		BaseForum standAloneForum = new BaseForum.Builder("stand alone " + rand)
										.tags(testName)
										.description("Stand alone Forum, Test My Forums - I'm an Owner view")
										.build();
		Forum apiStandAloneForum = standAloneForum.createAPI(apiForumsOwner);
		
		logger.strongStep("Add the stand alone forum to the forum list");
		log.info("INFO: add the stand alone forum to the forum list");
		apiForumList.add(apiStandAloneForum);
		
		logger.strongStep("Use API to add testUser2 to the stand alone forum as an owner");
		log.info("INFO: use API to add testUser2 to the stand alone forum as an owner");
		apiForumsOwner.addOwnertoForum(apiStandAloneForum, testUser2);
		
		logger.strongStep("Use API to create a topic in the stand alone forum and add it to the topic list");
		log.info("INFO: use API to create a topic in the stand alone forum and add it to the topic list");
		topicList.add(createTopic(apiStandAloneForum, testName));			
		
		logger.strongStep("Use API to create an open question in the stand a lone forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the stand a lone forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiStandAloneForum, testName));		
		
		logger.strongStep("Use API to create an answered question in the stand a lone forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the stand a lone forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiStandAloneForum, testName));
			
		log.info("INFO: END of preparing data");
		
		//Load component and Login
		logger.strongStep("Open browser, and login to Forums as:" + testUser2.getDisplayName());
		log.info("INFO: open browser, and login to Forums as Member, not creator");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser2);
		
		logger.strongStep("Click I'm Following, and verify the topics are not displayed at the Topics tab");
		log.info("INFO: click I'm Following, and verify the topics are not displayed at the Topics tab ");
		ui.clickLinkWait(ForumsUIConstants.Im_Following);
		for(int i=0; i<topicList.size();i++){
			String title = topicList.get(i).getTitle();
			Assert.assertTrue(driver.isTextNotPresent(title), title + "is NOT on the Topics tab");
			
		}
		
		logger.strongStep("Click Forums tab, and verify the Forums are NOT displayed at the Forums tab");
		log.info("INFO: click Forums tab, and verify the Forums are NOT displayed at the Forums tab ");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		for(int i=0; i<apiForumList.size();i++){
			String title = apiForumList.get(i).getTitle();
			Assert.assertTrue(driver.isTextNotPresent(title), title + "is NOT on the Forums tab");
			
		}
		
		logger.strongStep("Click Answered Question tab, and verify the answered topics are not displayed at the Answer Questions tab");
		log.info("INFO: click Answered Question tab, and verify the answered topics are not displayed at the Answer Questions tab ");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab_SA);
		for(int i=0; i<answeredQuestionList.size();i++){
			String title = answeredQuestionList.get(i).getTitle();
			Assert.assertTrue(driver.isTextNotPresent(title), title + "is NOT on the Answered Questions tab");
			
		}

		logger.strongStep("Click Open Questions tab, and verify the answered topic are not shown up at the Open Questions tab");
		log.info("INFO: click Open Questions tab, and verify the answered topic are not shown up at the Open Questions tab");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab_SA);
		for(int i=0; i<openQuestionList.size();i++){
			String title = openQuestionList.get(i).getTitle();
			Assert.assertTrue(driver.isTextNotPresent(title), title + "is NOT on the Open Questions tab");
			
		}
		
		ui.endTest();
	}
	
	
	/**
	 * TEST CASE: verify self created forums and topics were followed, and shown up correctly on different tabs of I'm Following view
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>verify different tabs of I'm Following view</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Get the default community forum and add it to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the default community forum and add it to the topic list.</li>
	 * <li><B>Step: </B>Use API to create an open question in the default community forum and add it to the open question list.</li>
	 * <li><B>Step: </B>Use API to create an answered question in the default community forum and add it to the answered question list.</li>
	 * <li><B>Step: </B>Use API to create the 2nd community forum </li>
	 * <li><B>Step: </B>Use API to add the 2nd community forum to the forum list </li>
	 * <li><B>Step: </B>Use API to create a topic in the 2nd community forum and add it to the topic list. </li>
	 * <li><B>Step: </B>Use API to create an open question in the 2nd community forum and add it to the open question list. </li>
	 * <li><B>Step: </B>Use API to create an answered question in the 2nd community forum and add it to the answered question list. </li>
	 * <li><B>Step: </B>Use API to create a stand alone forum, </li>
	 * <li><B>Step: </B>Use API to add the stand alone forum to the forum list </li>
	 * <li><B>Step: </B>Use API to add testUser2 to the stand alone forum as an owner </li>
	 * <li><B>Step: </B>Use API to create a topic in the stand alone forum and add it to the topic list. </li>
	 * <li><B>Step: </B>Use API to create an open question in the stand alone forum and add it to the open question list. </li>
	 * <li><B>Step: </B>Use API to create an answered question in the stand alone forum and add it to the answered question list. </li>
	 
	 * <li><B>Step: </B>Open browser, and login to Forums as owner/creator </li>
	 * 
	 * <li><B>Step: </B>click I'm Following (it's on the Topics tab)</li>
	 * <li><B>Verify: </B>verify all topics, including common topics, answered questions, open questions, shown up on the Topics tab</li>
	 
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify every forum, including the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Forums tab</li>
	
	 * <li><B>Step: </B>click the Answered Questions tab</li>
	 * <li><B>Verify: </B>verify all answered questions, including questions in the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Answered Questions tab</li>
	 * 
	 * <li><B>Step: </B>click the Open Questions tab</li>
	 * <li><B>Verify: </B>verify all open questions, including questions in the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab</li>
	 * </ul>
	 */
	@Test(groups={"regression"})
	public void test4Tabs_ImFollowing_OwnerAutoFollowing(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community "+rand;
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>(); 
		ArrayList<ForumTopic> topicList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> openQuestionList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> answeredQuestionList = new ArrayList<ForumTopic>();
		
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community	
		logger.strongStep("Use API to create community");
		log.info("INFO: use API to create community");
		Community apiCommunity = community.createAPI(apiOwner);
				
		logger.strongStep("Get default api community forum");
		log.info("INFO: get default api community forum");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);	
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Add the default api community forum to the forum list");
		log.info("INFO: add the default api community forum to the forum list");
		apiForumList.add(apiForum);
		
		logger.strongStep("Use API to create a common topic and add it to the topic list");
		log.info("INFO: use API to create a common topic and add it to the topic list");			
		topicList.add(createTopic(apiForum, testName));	
		
		logger.strongStep("Use API to create an open question and add it to the open question list");
		log.info("INFO: use API to create an open question and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum, testName));
		
		logger.strongStep("Use API to create an answered question and add it to the answered question list");
		log.info("INFO: use API to create an answered question and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum, testName));
		
				
		String urlToCreateCommunityForums = serverURL +"/forums/atom/forums?"+ commUUID;
		
		BaseForum forum2 = new BaseForum.Builder("second forum in the community "+ communityName)
											.tags(Data.getData().commonTag + rand)
											.description("second forum in the community")
											.build();
		
		logger.strongStep("Use API to create the 2nd community forum");
		log.info("INFO: use API to create the 2nd community forum");
		Forum apiForum2 = apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2);
		
		logger.strongStep("Use API to add the 2nd community forum to the forum list");
		log.info("INFO: use API to add the 2nd community forum to the forum list");
		apiForumList.add(apiForum2);
		
		logger.strongStep("Use API to create a topic in the 2nd community forum, and add it to the topic list");
		log.info("INFO: use API to create a topic in the 2nd community forum, and add it to the topic list");
		topicList.add(createTopic(apiForum2, testName));			
		
		logger.strongStep("Use API to create an open question in the 2nd community forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the 2nd community forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiForum2, testName));		
		
		logger.strongStep("Use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the 2nd community forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiForum2, testName));
		
		BaseForum standAloneForum = new BaseForum.Builder("stand alone " + rand)
										.tags(testName)
										.description("Stand alone Forum, Test My Forums - I'm an Owner view")
										.build();
		
		logger.strongStep("Use API to create a stand alone forum");
		log.info("INFO: use API to create a stand alone forum");
		Forum apiStandAloneForum = standAloneForum.createAPI(apiForumsOwner);
		
		logger.strongStep("Add the stand alone forum to the forum list");
		log.info("INFO: add the stand alone forum to the forum list");
		apiForumList.add(apiStandAloneForum);
		
		logger.strongStep("Use API to add testUser2 to the stand alone forum as an owner");
		log.info("INFO: use API to add testUser2 to the stand alone forum as an owner");
		apiForumsOwner.addOwnertoForum(apiStandAloneForum, testUser2);
		
		logger.strongStep("Use API to create a topic in the stand alone forum and add it to the topic list");
		log.info("INFO: use API to create a topic in the stand alone forum and add it to the topic list");
		topicList.add(createTopic(apiStandAloneForum, testName));			
		
		logger.strongStep("Use API to create an open question in the stand a lone forum, and add it to the open question list");
		log.info("INFO: use API to create an open question in the stand a lone forum, and add it to the open question list");
		openQuestionList.add(createOpenQuestion(apiStandAloneForum, testName));		
		
		logger.strongStep("Use API to create an answered question in the stand a lone forum, and add it to the answered question list");
		log.info("INFO: use API to create an answered question in the stand a lone forum, and add it to the answered question list");
		answeredQuestionList.add(createAnsweredQuestion(apiStandAloneForum, testName));
			
		log.info("INFO: END of preparing data");
		
		//Load component and Login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: open browser and login to Forums as owner/creator");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("click I'm Following'");
		log.info("INFO: click I'm Following'");
		ui.clickLinkWait(ForumsUIConstants.Im_Following);
		
		logger.strongStep("Verify every common topic(including the topics in the default community forum," +
				" the 2nd community forum, and the stand alone forum) shown up on the Topics tab");
		log.info("INFO: verify every common topic(including the topics in the default community forum," +
				" the 2nd community forum, and the stand alone forum) shown up on the Topics tab");
		verifyTopicsPresent(topicList, "Topics");
		
		logger.strongStep("Verify every answered question, including questions in including the default community forum," +
				" the 2nd community forum, and the stand alone forum, is displayed at the Topics tab");
		log.info("INFO: verify every answered question, including questions in including the default community forum," +
				" the 2nd community forum, and the stand alone forum, is displayed at the Topics tab" );
		verifyTopicsPresent(answeredQuestionList, "Topics");
		
		logger.strongStep("Verify the open question, including questions in the default community forum," +
				" the 2nd community forum, and the stand alone forum, disappears from the Topics tab");
		log.info("INFO: verify the open question, including questions in the default community forum," +
				" the 2nd community forum, and the stand alone forum, disappears from the Topics tab");
		verifyTopicsPresent(openQuestionList, "Topics");
				
		logger.strongStep("Click the FORUMS tab");
		log.info("INFO: click the FORUMS tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		logger.strongStep("Verify every forum, including the default community forum, the 2nd community forum," +
				" and the stand alone forum, shown up on the Forums tab");
		log.info("INFO: verify every forum, including the default community forum, the 2nd community forum," +
				" and the stand alone forum, shown up on the Forums tab" );
		verifyForumsPresent(apiForumList);
		
		logger.strongStep("Click Answered Question tab");
		log.info("INFO: click Answered Question tab");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab_SA);
		
		logger.strongStep("Verify every answered question, including questions in including the default community forum," +
				" the 2nd community forum, and the stand alone forum, is displayed at the Answer Questions tab");
		log.info("INFO: verify every answered question, including questions in including the default community forum," +
				" the 2nd community forum, and the stand alone forum, is displayed at the Answer Questions tab" );
		verifyTopicsPresent(answeredQuestionList, "Answered Questions");
				
		logger.strongStep("Click Open Questions tab");
		log.info("INFO: click Open Questions tab ");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab_SA);
		
		logger.strongStep("Verify the open question, including questions in the default community forum," +
				" the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab");
		log.info("INFO: verify the open question, including questions in the default community forum," +
				" the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab");
		verifyTopicsPresent(openQuestionList, "Open Questions");		
		
		ui.endTest();

	}
	/**
	 * TEST CASE: different tabs of I'm Following view. if the user is not owner/member of a forum and topic
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>verify different tabs of I'm Following view</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to get the first community forum and add it to the forum list </li>
	 * <li><B>Step: </B>Another User Uses API to follow the forum and topic. </li>
	 * <li><B>Step: </B>Use API to create a topic in the default community forum. </li>
	 * <li><B>Step: </B>Another user uses API to follow the topic</li>
	 * <li><B>Step: </B>Add topic to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the default community forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic. </li>
	 * <li><B>Step: </B>Add topic to the open Questions list </li>
	 * <li><B>Step: </B>Use API to create an answered question in the default community forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic. </li>
	 * <li><B>Step: </B>Add topic to the answered Questions list</li>
	 * <li><B>Step: </B>Use API to create the 2nd community forum  and add it to the forum list</li>
	 * <li><B>Step: </B>Another User Uses API to follow the forum . </li>
	 * <li><B>Step: </B>Use API to create a topic in the 2nd community forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic and add topic to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the 2nd community forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic and add topic to the open questions list</li>
	 * <li><B>Step: </B>Use API to create an answered question in the 2nd community forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic and add topic to answered questions list  </li>
	 * <li><B>Step: </B>Use API to create a stand alone forum,and add it to the forum list </li>
	 * <li><B>Step: </B>Another User Uses API to follow the forum. </li>
	 * <li><B>Step: </B>Use API to create a topic in the stand alone forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic and add topic to the topic list </li>
	 * <li><B>Step: </B>Use API to create an open question in the stand alone forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic and Add topic to the open questions list </li>
	 * <li><B>Step: </B>Use API to create an answered question in the stand alone forum. </li>
	 * <li><B>Step: </B>Another User Uses API to follow the topic and Add topic to the answered questions list </li>
	 
	 * <li><B>Step: </B>Open browser, and login to Forums as another user, not member or owner</li>
	 * 
	 * <li><B>Step: </B>click I'm Following (it's on the Topics tab)</li>
	 * <li><B>Verify: </B>verify all topics, which the user is following, including common topics, answered questions, open questions, shown up on the Topics tab</li>
	 
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify every forum, which the user is following, including the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Forums tab</li>
	
	 * <li><B>Step: </B>click the Answered Questions tab</li>
	 * <li><B>Verify: </B>verify all answered questions, which the user is following,  including questions in the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Answered Questions tab</li>
	 * 
	 * <li><B>Step: </B>click the Open Questions tab</li>
	 * <li><B>Verify: </B>verify all open questions, which the user is following,  including questions in the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Open Questions tab</li>
	 * </ul>
	 */
	@Test(groups={"regression"})
	public void test4Tabs_ImFollowing_NonMemberFollowingPublicForumsAndTopics(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a public community(community) including only one community forum and some pinned topics");
	
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community forum "+rand;
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		ArrayList<ForumTopic> topicList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> openQuestionList = new ArrayList<ForumTopic>();
		ArrayList<ForumTopic> answeredQuestionList = new ArrayList<ForumTopic>();
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community	
		logger.strongStep("Use API to create community A");
		log.info("INFO: use API to create community A");
		Community apicommunity = community.createAPI(apiOwner);
		
		logger.strongStep("Use API to get the first community forum");
		log.info("INFO: use API to get the first community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Add the default api community forum to the forum list");
		log.info("INFO: add the default api community forum to the forum list");
		apiForumList.add(apiForum);
		
		logger.strongStep("Another uers uses API to follow the default community forum");
		log.info("INFO: Another uers uses API to follow the default community forum");
		apiForumsOwner2.createFollow(apiForum);

		logger.strongStep("Use API to create topics");		
		log.info("INFO: use API to create topics");		
		ForumTopic topic = createTopic(apiForum, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);
	
		logger.strongStep("Add topic to the topic list");
		log.info("add topic to the topic list");
		topicList.add(topic);	
		
		logger.strongStep("Use API to create Open Question in the default community forum");
		log.info("INFO: use API to create Open Question in the default community forum");
		topic = createOpenQuestion(apiForum, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);
		
		logger.strongStep("Add topic to the open Questions list");
		log.info("add topic to the open Questions list");
		openQuestionList.add(topic);
		
		logger.strongStep("Use API to create Answered Question in the default community forum");
		log.info("INFO: use API to create Answered Question in the default community forum");
		topic = createAnsweredQuestion(apiForum, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);

		logger.strongStep("Add topic to the answered Questions list");
		log.info("add topic to the answered Questions list");
		answeredQuestionList.add(topic);
		
		String urlToCreateCommunityForums = serverURL +"/forums/atom/forums?"+ commUUID;
		
		BaseForum forum2 = new BaseForum.Builder("second forum in the community "+ communityName)
											.tags(Data.getData().commonTag + rand)
											.description("second forum in the community")
											.build();
		
		logger.strongStep("Use API to create the 2nd community forum");
		log.info("INFO: use API to create the 2nd community forum");
		Forum apiForum2 = apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2);
		
		logger.strongStep("Use API to add the 2nd community forum to the forum list");
		log.info("use API to add the 2nd community forum to the forum list");
		apiForumList.add(apiForum2);
		
		logger.strongStep("Another user uses API to follow the 2nd community forum");
		log.info("INFO: Another user uses API to follow the 2nd community forum");
		apiForumsOwner2.createFollow(apiForum2);
		
		logger.strongStep("Use API to create topics");
		log.info("INFO: use API to create topics");		
		topic = createTopic(apiForum2, testName);

		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);
		
		logger.strongStep("Add topic to the topic list");
		log.info("add topic to the topic list");
		topicList.add(topic);	
		
		logger.strongStep("Use API to create open Question in the 2nd community forum");
		log.info("INFO: use API to create open Question in the 2nd community forum");
		topic = createOpenQuestion(apiForum2, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);

		logger.strongStep("Add topic to the open Questions list");
		log.info("add topic to the open Questions list");
		openQuestionList.add(topic);
		
		logger.strongStep("Use API to create Answered Question in the 2nd community forum");
		log.info("INFO: use API to create Answered Question in the 2nd community forum");
		topic = createAnsweredQuestion(apiForum2, testName);
	
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);

		logger.strongStep("Add topic to the answered questions list");
		log.info("add topic to the answered questions list");
		answeredQuestionList.add(topic);
				
		BaseForum forumB = new BaseForum.Builder("stand alone " + rand)
										.tags(testName)
										.description("Stand alone Forum, Test My Forums - I'm an Owner view")
										.build();
		
		//create standalone forum
		logger.strongStep("Use API to create the stand alone forum");
		log.info("INFO: use API to create the stand alone forum");
		Forum apiForumB = forumB.createAPI(apiForumsOwner);
		
		logger.strongStep("Use API to add the stand alone forum to the forum list");
		log.info("use API to add the stand alone forum to the forum list");
		apiForumList.add(apiForumB);
		
		logger.strongStep("Another user uses API to follow the stand alone forum");
		log.info("INFO: Another user uses API to follow the stand alone forum");
		apiForumsOwner2.createFollow(apiForumB);

		logger.strongStep("Use API to create topics");
		log.info("INFO: use API to create topics");		
		topic = createTopic(apiForumB, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);
		
		logger.strongStep("Add topic to the topic list");
		log.info("add topic to the topic list");
		topicList.add(topic);	
		
		logger.strongStep("Use API to create open Question in the stand alone forum");
		log.info("INFO: use API to create open Question in the stand alone forum");
		topic = createOpenQuestion(apiForumB, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);

		logger.strongStep("Add topic to the open questions list");
		log.info("add topic to the open questions list");
		openQuestionList.add(topic);
		
		logger.strongStep("Use API to create Answered Question in the default community forum");
		log.info("INFO: use API to create Answered Question in the default community forum");
		topic = createAnsweredQuestion(apiForumB, testName);
		
		logger.strongStep("Another user uses API to follow the topic");
		log.info("INFO: Another user uses API to follow the topic");
		apiForumsOwner2.createFollow(topic);

		logger.strongStep("Add topic to the answered questions list");
		log.info("add topic to the answered questions list");
		answeredQuestionList.add(topic);
		
		//Load component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser2.getDisplayName());
		log.info("INFO: Open browser, login to Forums as another user, who is not member or owner of those prepared forums and topics, but following them");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser2);
		
		logger.strongStep("Click I'm Following");
		log.info("INFO: click I'm Following");
		ui.clickLinkWait(ForumsUIConstants.Im_Following);
		
		logger.strongStep("Verify every common topic(including the topics in the default community forum," +
				" the 2nd community forum, and the stand alone forum) shown up on the Topics tab");
		log.info("INFO: verify every common topic(including the topics in the default community forum," +
				" the 2nd community forum, and the stand alone forum) shown up on the Topics tab");	
		for(int i=0; i<topicList.size();i++){
			String title = topicList.get(i).getTitle();
			Assert.assertTrue(driver.isTextPresent(title), title + "is NOT on the Answered Questions tab");
			
		}
		
		logger.strongStep("Click Forums tab and verify every forum wihch the user is following, " +
				"including the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Forums tab");
		log.info("INFO: click Forums tab and verify every forum wihch the user is following, " +
				"including the default community forum, the 2nd community forum, and the stand alone forum, shown up on the Forums tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		for(int i=0; i<apiForumList.size();i++){
			String title = apiForumList.get(i).getTitle();
			Assert.assertTrue(driver.isTextPresent(title), title + "is NOT on the Forums tab");
			
		}
		
		logger.strongStep("click Answered Question tab, and verify the answered question which the user is following ," +
				"including in the default community forum, the 2nd community forum, and the stand alone forum, is displayed at the Answer Questions tab");
		log.info("INFO: click Answered Question tab, and verify the answered question which the user is following ," +
				"including in the default community forum, the 2nd community forum, and the stand alone forum, is displayed at the Answer Questions tab ");
		ui.clickLinkWait(ForumsUIConstants.AnsweredQuestionsTab_SA);
		for(int i=0; i<answeredQuestionList.size();i++){
			String title = answeredQuestionList.get(i).getTitle();
			Assert.assertTrue(driver.isTextPresent(title), title + "is NOT on the Answered Questions tab");
			
		}

		logger.strongStep("Click Open Questions tab, and verify the open question which the user is following," +
				" including in the default community forum, the 2nd community forum, and the stand alone forum, disappears from the Open Questions tab");
		log.info("INFO: click Open Questions tab, and verify the open question which the user is following," +
				" including in the default community forum, the 2nd community forum, and the stand alone forum, disappears from the Open Questions tab");
		ui.clickLinkWait(ForumsUIConstants.OpenQuestionsTab_SA);
		for(int i=0; i<openQuestionList.size();i++){
			String title = openQuestionList.get(i).getTitle();
			Assert.assertTrue(driver.isTextPresent(title), title + "is NOT on the Open Questions tab");
			
		}

		
		ui.endTest();		
		

	}
	

	


}
