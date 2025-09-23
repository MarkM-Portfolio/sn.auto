package com.ibm.conn.auto.tests.forums.regression;


import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
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
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class Like extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Like.class);
	private ForumsUI ui;
	private CommunitiesUI comui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner;
	
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
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		comui = CommunitiesUI.getGui(cfg.getProductName(), driver);	
		
		
	}
	
	/**
	 * create a community
	 * @param testName
	 * @return the created community
	 */
	private Forum createCommForum(String testName){
		String rand = Helper.genDateBasedRand();
		String communityName = testName + rand;
		log.info("use API to create a community");
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		/*create community	*/	
		Community apiCommunity = community.createAPI(apiOwner);

		String commUUID = apiOwner.getCommunityUUID(apiCommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		return apiForum;
	}
	/**
	 * create a topic
	 * @param forum
	 * @param testName
	 * @return the created topic
	 */
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
	 * create a topic and a reply to this reply
	 * @param forum
	 * @param testName
	 * @return the topic's reply
	 */
	private ForumReply createTopicReply(Forum forum, String testName){
		ForumTopic topic = createTopic(forum, testName);
		return apiForumsOwner.createForumReply(topic, "create to test like/unlike");
		
		
	}

	/**
	 * verify the text 'People who like this ... ' shown up in the like widget's header
	 */
	private void verifyLikeHeader(){
		String headerText = ui.getElementText(ForumsUIConstants.likeHeader);
		Assert.assertEquals(headerText, Data.PeopleWhoLikeThis, "the like header text is wrong");	
	}

	/**
	 * verify the Like link switches to Unlink link
	 */
	private void verifyLikeSwitchesToUnlike(){
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnlikeLink),"Unlike link should be present");

	}
	/**
	 * verify the Unlike link switches to Like link
	 */
	private void verifyUnlikeSwitchesToLike(){
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.UnlikeLink),"Unlike link should not be present");
	}
	/**
	 * verify the number of people who like this topic is <i>
	 * @param i
	 */
	private void verifyLikeNumber(int i){
		String number = driver.getSingleElement(ForumsUIConstants.likeNumber).getText();
		Assert.assertEquals(number, ""+i, "failed to like the topic, the number of like is wrong");
	}
	/**
	 * verify the number of people who like this reply is <i>
	 * @param i
	 */
	private void verifyLikeReplyNumber(int i){
		String number = driver.getSingleElement(ForumsUIConstants.likeReplyNumber).getText();
		Assert.assertEquals(number, ""+i, "failed to like the topic, the number of like is wrong");
	}
	/**
	 * verify the number of people who like this topic is <i>
	 * @param i
	 */
	private void verifyLikeNumberOnList(int i){
		
		String number = driver.getSingleElement(ForumsUIConstants.likeNumberOnList).getText();
		Assert.assertEquals(number, ""+i, "failed to like the topic, the number of like is wrong");
	}
	/**
	 * verify the number of people who like this topic is <i>
	 * @param i
	 */
	private void verifyLikeNumberOverview(int i){
		
		String number = driver.getSingleElement(ForumsUIConstants.likeNumberOverview).getText();
		Assert.assertEquals(number, ""+i, "failed to like the topic, the number of like is wrong");
	}
	/**
	 * verify when your mouse is hovering over the like number, there will be '1 person likes this' shown up.
	 */
	private void verify1PersonLikesThis(){
		String title = driver.getSingleElement(ForumsUIConstants.OnePersonLikesThis).getAttribute("title");
		
		Assert.assertEquals(title,Data.OnePersonLikesThis,"failed to like the topic: 1 person likes this");
	}
	/**
	 * verify when your mouse is hovering over the like number, there will be '1 person likes this' shown up.
	 */
	private void verify1PersonLikesThisOnList(){
		String title = driver.getSingleElement(ForumsUIConstants.OnePersonLikesThisOnList).getAttribute("title");
		
		Assert.assertEquals(title,Data.OnePersonLikesThis,"failed to like the topic: 1 person likes this");
	}
	/**
	 * verify when your mouse is hovering over the like number, there will be '1 person likes this' shown up.
	 */
	private void verify1PersonLikesThisOverview(){
		String title = driver.getSingleElement(ForumsUIConstants.likeImg).getAttribute("title");
		
		Assert.assertEquals(title,Data.OnePersonLikesThis,"failed to like the topic: 1 person likes this");
	}
	/**
	 * verify when your mouse is hovering over the like number, there will be '1 person likes this' shown up.
	 */
	private void verify1PersonLikesThisReply(){
		String title = driver.getSingleElement(ForumsUIConstants.OnePersonLikesThisReply).getAttribute("title");
		
		Assert.assertEquals(title,Data.OnePersonLikesThis,"failed to like the topic: 1 person likes this");
	}
	/**
	 * verify when your mouse is hovering over the like number, there will be '0 person likes this' shown up.
	 */
	private void verifyNoPeopleLikeThisonList(){
		
		String title = driver.getSingleElement(ForumsUIConstants.NoPeopleLiksThisOnList).getAttribute("title");
		
		Assert.assertEquals(title,Data.NoPeopleLikeThis,"failed to like the topic: 0 person likes this");
	}
	/**
	 * verify when your mouse is hovering over the like number, there will be '0 person likes this' shown up.
	 */
	private void verifyNoPeopleLikeThisOverview(){		
			
			String title = driver.getSingleElement(ForumsUIConstants.likeImg).getAttribute("title");
			Assert.assertEquals(title,Data.NoPeopleLikeThis,"failed to like the topic: 0 person likes this");
		
	}
	/**
	 * verify there is no like widget shown up after click the like img
	 */
	private void verifyNoLikeWidget(){
		ui.clickLinkWait(ForumsUIConstants.likeDiv);
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.likeWidget),
				"Like widget should not show up here");	
	}
	/**
	 * verify there is no like widget shown up after click the like img
	 */
	private void verifyNoLikeWidgetForReply(){
		ui.clickLinkWait(ForumsUIConstants.likeReplyDiv);
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.likeWidget),
				"Like widget should not show up here");	
	}
	/**
	 * verify there is no like widget shown up after click the like img
	 */
	private void verifyNoLikeWidgetOverview(String widgetID){
		ui.clickLinkWait(ForumsUI.getLikeDivOnOverview(widgetID));
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.likeWidget),
				"Like widget should not show up here");	
	}
	/**
	 * verify there is no like widget shown up after click the like img
	 */
	private void verifyNoLikeWidgetForReplyOverview(){
		ui.clickLinkWait(ForumsUIConstants.likeReplyDiv);
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.likeWidget),
				"Like widget should not show up here");	
	}
	/**
	 * TEST CASE: Like a community forum's topic.
	 * <ul>
	 * <li><B>Info: </B>Like a community topic</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to create a topic. </li>	 
	 * <li><B>Step: </B>Open browser, and login to Forums</li>
	 * <li><B>Step: </B>navigate to the topic's Overview page</li>	
	 * <li><B>Step: </B>click Like </li>
	 * <li><B>Verify: </B>Verify like number changes to 1</li>
	 * <li><B>Verify: </B>Verify Like link changes to Unlike</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 person like this'</li>
	 * <li><B>Verify: </B>verify Like widget shows you as who likes this topic</li>
	 * <li><B>Verify: </B>Verify a Biz card shows</li>
	 * <li><B>Verify: </B>verify click the user name, it navigates to the person's profile</li>
	 * <li><B>Step: </B>navigate to the forum's home page by its url </li>
	 * <li><B>Verify: </B>verify topic list shows like number is 1</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 person like this'</li>
	 * <li><B>Step: </B>Navigate to the community's Overview page</li>
	 * <li><B>Verify: </B>verify topic list shows like number is 1</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 person like this'</li>
	 * <li><B>Verify: </B>verify No like widget</li>
	 * </ul>
	
	 */
	@Test(groups={"regression", "regressioncloud"} , enabled=false )
	public void testLikeCommTopic(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();

		logger.strongStep("Create a community using API");
		log.info("INFO: create a community using API");
		Forum apiForum = createCommForum(testName);
		
		logger.strongStep("Create a topic using API");
		log.info("INFO: create a topic using API");
		ForumTopic apiForumTopic = createTopic(apiForum, testName);
		
		//Load component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the topic's Overview page by its url");
		log.info("INFO: navigate to the topic's Overview page by its url");
		driver.navigate().to(apiForumTopic.getAlternateLink());
		
		logger.strongStep("Click Like to like the topic");
		log.info("INFO: click Like to like the topic");
		ui.clickLinkWait(ForumsUIConstants.LikeLink);
		
		logger.strongStep("Verify the like action is successful on the topic's page and like number changes to 1");
		log.info("INFO: verify the like action is successful on the topic's page");
		Assert.assertTrue(driver.isTextPresent(Data.getData().Expected_Like_Text),"failed to like the topic");
		log.info("INFO: verify like number changes to 1");		
		verifyLikeNumber(1);		
				
		logger.strongStep("Verify Like link changes to Unlike");
		log.info("INFO: verify Like link changes to Unlike");			
		verifyLikeSwitchesToUnlike();
		
		logger.strongStep("Verify the mouse on the like icon, it shows '1 person like this'");
		log.info("INFO: verify the mouse on the like icon, it shows '1 person like this'");
		verify1PersonLikesThis();
		
		logger.strongStep("Verify Like widget shows you as who likes this topic");
		log.info("INFO: verify Like widget shows you as who likes this topic");			
		ui.clickLinkWait(ForumsUIConstants.OnePersonLikesThis);
		verifyLikeHeader();
		
		logger.strongStep("Verify a Biz card shows");
		log.info("INFO: verify a Biz card shows");	
		ui.verifyBizCard();
		
		logger.strongStep("Verify click the user name, it navigates to the person's profile");
		log.info("INFO: verify click the user name, it navigates to the person's profile");
		ui.clickLinkWait(ForumsUIConstants.bizCardLink);
		
		logger.strongStep("Navigate to the forum's home page by its url");
		log.info("INFO: navigate to the forum's home page by its url");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Verify topic list shows like number is 1");
		log.info("INFO: verify topic list shows like number is 1");
		verifyLikeNumberOnList(1);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		verify1PersonLikesThisOnList();
		
		logger.strongStep("Navigate to the community's Overview Page");
		log.info("INFO: navigate to the Overview Page");
		Community_LeftNav_Menu.OVERVIEW.select(comui);
		
		logger.strongStep("Wait till the Forums widget is loaded");
		log.info("INFO: wait till the Forums widget is loaded");
		comui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic);
		
		logger.strongStep("Verify topic list shows like number is 1");
		log.info("INFO: verify topic list shows like number is 1");
		verifyLikeNumberOverview(1);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		verify1PersonLikesThisOverview();
		
		logger.strongStep("Verify no like widget shows");
		log.info("INFO: verify no like widget shows");
		ui.clickLinkWait(ForumsUIConstants.likeNumberOverview);
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.likeWidget),"Like widget should not show up here");
	}
	/**
	 * TEST CASE: Unlike a community forum's topic.
	 * <ul>
	 * <li><B>Info: </B>Like a community topic</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to create a topic </li>
	 * <li><B>Step: </B>Use API to like the topic</li>	 
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the topic's Overview page</li>
	 * <li><B>Verify: </B>Check topic is liked</li>
	 * <li><B>Verify: </B>Check text 'You like this' is present</li>
	 * <li><B>Verify: </B>Check Unlike link is here</li>	
	 * <li><B>Step: </B>click Unlike </li>
	 * <li><B>Verify: </B>verify text 'You like this' disappear</li>///
	 * <li><B>Verify: </B>verify Unlike link changes to Like</li>
	 * <li><B>Verify: </B>verify no like widget</li>
	 * <li><B>Step: </B>navigate to the forum's home page by its url </li>
	 * <li><B>Verify: </B>verify like number changes to 0</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 people like this'</li>
	 * <li><B>Step: </B>Navigate to the community's Overview page</li>
	 * <li><B>Verify: </B>verify topic list shows like number is 0</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 people like this'</li>
	 * <li><B>Verify: </B>verify No like widget</li>
	 * </ul>
	 * @throws Exception 
	
	 */
	@Test(groups={"regression", "regressioncloud"} , enabled=false )
	public void testUnlikeCommTopic() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();

		String rand = Helper.genDateBasedRand();
		String communityName = testName + rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		/*create community	*/	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apiCommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Create a topic using API");
		log.info("INFO: create a topic using API");
		ForumTopic apiForumTopic = createTopic(apiForum, testName);
		
		logger.strongStep("Like the topic using API");
		log.info("INFO: like the topic using API");
		apiForumsOwner.like(apiForumTopic);
		
		//Load component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the topic's Overview page by its url");
		log.info("INFO: navigate to the topic's Overview page by its url");
		driver.navigate().to(apiForumTopic.getAlternateLink());
		
		logger.strongStep("Verify the topic is liked and the text is 'You like this'");
		log.info("INFO: verify the topic is liked and the text is 'You like this'");
		Assert.assertTrue(driver.isTextPresent(Data.getData().Expected_Like_Text),"failed to like the topic");
		
		logger.strongStep("Verify the like number changes to 1");
		log.info("INFO: verify the like number changes to 1");
		verifyLikeNumber(1);
		
		logger.strongStep("Verify Unlike link is here");
		log.info("INFO: verify Unlike link is here");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnlikeLink),"failed to like the topic");
		
		logger.strongStep("Click Unlike");
		log.info("INFO: click Unlike");
		ui.clickLink(ForumsUIConstants.UnlikeLink);
		
		logger.strongStep("Verify the Unlike link changes to Like");
		log.info("INFO: verify the Unlike link changes to Like");
		verifyUnlikeSwitchesToLike();
		
		logger.strongStep("Verify no like widget");
		log.info("INFO: verify no like widget");
		verifyNoLikeWidget();	
		
		logger.strongStep("Navigate to the forum's home page by its url");
		log.info("INFO: navigate to the forum's home page by its url");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Verify topic list shows like number is 0");
		log.info("INFO: verify topic list shows like number is 0");
		verifyLikeNumberOnList(0);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		verifyNoPeopleLikeThisonList();
		
		logger.strongStep("Navigate to the community's Overview Page");
		log.info("INFO: navigate to the Overview Page");
		Community_LeftNav_Menu.OVERVIEW.select(comui);
		
		logger.strongStep("Verify topic list shows like number is 0");
		log.info("INFO: verify topic list shows like number is 0");
		verifyLikeNumberOverview(0);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		verifyNoPeopleLikeThisOverview();
		
		logger.strongStep("Verify no like widget shows");
		log.info("INFO: verify no like widget shows");
		String widgetID = apiOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Forum");
		verifyNoLikeWidgetOverview(widgetID);			
		
	}
	
	/**
	 * TEST CASE: like a community forum's topic reply
	 * <ul>
	 * <li><B>Info: </B>Like a community topic</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to create a topic and this topic's reply. </li>
	 * <li><B>Step: </B>Use API to like the reply </li>	 
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the topic's Overview page</li>	
	 * <li><B>Step: </B>click Like </li>
	 * <li><B>Verify: </B>verify like number changes to 1</li>
	 * <li><B>Verify: </B>verify Like link changes to Unlike</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 person like this'</li>
	 * <li><B>Verify: </B>verify Like widget shows you as who likes this topic</li>
	 * <li><B>Verify: </B>verify a Biz card shows</li>
	 * <li><B>Verify: </B>verify click the user name, it navigates to the person's profile</li>
	 * <li><B>Step: </B>navigate to the forum's home page by its url </li>
	 * <li><B>Verify: </B>verify topic list shows like number is 1</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 person like this'</li>
	 * <li><B>Step: </B>Navigate to the community's Overview page</li>
	 * <li><B>Verify: </B>verify topic list shows like number is 1</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '1 person like this'</li>
	 * <li><B>Verify: </B>verify No like widget</li>
	 * </ul>	
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testLikeCommTopicReply() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		String communityName = testName + rand;

		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();

		/*create community	*/	
		logger.strongStep("Create a Community using API");
		log.info("INFO: create a Community using API");
		Community comAPI = community.createAPI(apiOwner);

		String commUUID = apiOwner.getCommunityUUID(comAPI);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Create a topic and topic's reply using API");
		log.info("INFO: Create a topic and topic's reply using API");
		ForumReply apiForumTopicReply = createTopicReply(apiForum, testName);
		
		//Load Component and login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: Open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Check whether the Landing Page for the Community is Overview or Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		
		if (flag) {
			
		    logger.strongStep("Add the Overview page to the Community and make it the landing page");
		    log.info("INFO: Add the Overview page to the Community and make it the landing page");
		    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		
		}

		logger.strongStep("Get the handle for the current window");
		log.info("Get the handle for the current window");
		String originalWindow = driver.getWindowHandle();

		logger.strongStep("Navigate to the topic's Overview page by its url");
		log.info("INFO: navigate to the topic's Overview page by its url");
		driver.navigate().to(apiForumTopicReply.getAlternateLink());
		
		logger.strongStep("Click Like to like the topic reply");
		log.info("INFO: click Like to like the topic reply");
		ui.clickLinkWait(ForumsUIConstants.LikeReplyLink);
		
		logger.strongStep("Verify the like action is successful on the topic's page and like number changes to 1");
		log.info("INFO: verify the like action is successful on the topic's page and like number changes to 1");			
		Assert.assertTrue(driver.isTextPresent(Data.getData().Expected_Like_Text),"failed to like the topic");
		verifyLikeReplyNumber(1);
		
		logger.strongStep("Verify Like link changes to Unlike");
		log.info("INFO: verify Like link changes to Unlike");			
		verifyLikeSwitchesToUnlike();
		
		logger.strongStep("Verify the mouse on the like icon, it shows '1 person like this'");
		log.info("INFO: verify the mouse on the like icon, it shows '1 person like this'");
		verify1PersonLikesThisReply();
		
		logger.strongStep("Verify Like widget shows you as who likes this topic");
		log.info("INFO: verify Like widget shows you as who likes this topic");			
		ui.clickLinkWait(ForumsUIConstants.OnePersonLikesThisReply);
		verifyLikeHeader();
		
		logger.strongStep("Verify a Biz card shows");
		log.info("INFO: verify a Biz card shows");
		ui.verifyBizCard();
		
		logger.strongStep("Verify click the user name, it navigates to the person's profile");
		log.info("INFO: verify click the user name, it navigates to the person's profile");
		ui.clickLinkWait(ForumsUIConstants.bizCardLink);
		
		logger.strongStep("Navigate to the forum's home page by its url");
		log.info("INFO: navigate to the forum's home page by its url");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Verify topic list shows like number is 1");
		log.info("INFO: verify topic list shows like number is 1");
		verifyLikeNumberOnList(1);
		
		logger.strongStep("Verify in topic list, hover the mouse on the like icon, it shows '1 person likes this'");
		log.info("INFO: verify in topic list, hover the mouse on the like icon, it shows '1 person likes this'");
		verify1PersonLikesThisOnList();
		
		logger.strongStep("Close the new window and switch to the origial window");
		log.info("INFO: Closing new window and switching to the origial window");
		driver.switchToWindowByHandle(originalWindow);

		logger.strongStep("Navigate to the community's Overview Page");
		log.info("INFO: navigate to the Overview Page");
		Community_LeftNav_Menu.OVERVIEW.select(comui);
		
		logger.strongStep("Verify topic list shows like number is 1");
		log.info("INFO: verify topic list shows like number is 1");
		verifyLikeNumberOverview(1);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '1 person likes this'");
		verify1PersonLikesThisOverview();
		
		logger.strongStep("Verify no like widget shows");
		log.info("INFO: verify no like widget shows");
		ui.clickLinkWait(ForumsUIConstants.likeNumberOverview);
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.likeWidget),"Like widget should not show up here");
	
		ui.endTest();

	}

	/**
	 * TEST CASE: Unlike a community forum's topic's reply.
	 * <ul>
	 * <li><B>Info: </B>Like a community topic</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to create a topic and create a reply to this topic </li>
	 * <li><B>Step: </B>Use API to like the topic reply</li>	 
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the topic's Overview page</li>
	 * <li><B>Verify: </B>Verify the topic is liked and text is 'you like this'</li>
	 * <li><B>Verify: </B>Verify the like number changes to 1</li>
	 * <li><B>Verify: </B>Verify Unlike link is here</li>
	 * <li><B>Step: </B>click Unlike </li>
	 * <li><B>Verify: </B>Verify Unlike link changes to Like</li>
	 * <li><B>Verify: </B>Verify no like widget</li>
	 * <li><B>Step: </B>navigate to the forum's home page by its url </li>
	 * <li><B>Verify: </B>verify topic list shows like number is 0</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '0 people like this'</li>
	 * <li><B>Step: </B>Navigate to the community's Overview page</li>
	 * <li><B>Verify: </B>verify topic list shows like number is 0</li>
	 * <li><B>Verify: </B>verify the mouse on the like icon, it shows '0 people like this'</li>
	 * <li><B>Verify: </B>verify No like widget</li>
	 * </ul>
	 * @throws Exception 
	
	 */
	@Test(groups={"regression", "regressioncloud"} , enabled=false )	
	public void testUnlikeCommTopicReply() throws Exception{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();

		String rand = Helper.genDateBasedRand();
		String communityName = testName + rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		/*create community	*/	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apiCommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apiCommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		logger.strongStep("Use API to create a topic and a reply to this topic");
		log.info("INFO: use API to create a topic and a reply to this topic");
		ForumReply apiForumTopicReply = createTopicReply(apiForum, testName);
		
		logger.strongStep("Use API to like the topic reply");
		log.info("INFO: use API to like the topic reply");
		apiForumsOwner.like(apiForumTopicReply);
		
		//Load component and Login
		logger.strongStep("Open browser, and login to Forums as:" + testUser1.getDisplayName());
		log.info("INFO: Open browser, and login to Forums");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the topic's Overview page by its url");
		log.info("INFO: navigate to the topic's Overview page by its url");
		driver.navigate().to(apiForumTopicReply.getAlternateLink());
		
		logger.strongStep("Verify the topic is liked and text is 'You like this'");
		log.info("INFO: verify the topic is liked and the text is 'You like this'");
		Assert.assertTrue(driver.isTextPresent(Data.getData().Expected_Like_Text),"failed to like the topic");
		
		logger.strongStep("Verify the like number changes to 1");
		log.info("INFO: verify the like number changes to 1");
		verifyLikeReplyNumber(1);
		
		logger.strongStep("Verify Unlike link is here");
		log.info("INFO: verify Unlike link is here");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnlikeLink),"failed to like the topic");
		
		logger.strongStep("Click Unlike");
		log.info("INFO: click Unlike");
		ui.clickLink(ForumsUIConstants.UnlikeLink);
		
		logger.strongStep("Verify the Unlike link changes to Like");
		log.info("INFO: verify the Unlike link changes to Like");
		verifyUnlikeSwitchesToLike();
		
		logger.strongStep("Verify no like widget");
		log.info("INFO: verify no like widget");
		verifyNoLikeWidgetForReply();	
		
		logger.strongStep("Navigate to the forum's home page by its url");
		log.info("INFO: navigate to the forum's home page by its url");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Verify topic list shows like number is 0");
		log.info("INFO: verify topic list shows like number is 0");
		verifyLikeNumberOnList(0);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '0 people likes this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '0 people likes this'");
		verifyNoPeopleLikeThisonList();
		
		logger.strongStep("Navigate to the community's Overview Page");
		log.info("INFO: navigate to the Overview Page");
		Community_LeftNav_Menu.OVERVIEW.select(comui);
		
		logger.strongStep("Wait till the Forums widget is loaded");
		log.info("INFO: wait till the Forums widget is loaded");
		comui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic);
		
		logger.strongStep("Verify topic list shows like number is 0");
		log.info("INFO: verify topic list shows like number is 0");
		verifyLikeNumberOverview(0);
		
		logger.strongStep("Verify in topic list, however the mouse on the like icon, it shows '0 people like this'");
		log.info("INFO: verify in topic list, however the mouse on the like icon, it shows '0 people like this'");
		verifyNoPeopleLikeThisOverview();
		
		logger.strongStep("Verify no like widget shows");
		log.info("INFO: verify no like widget shows");
		String widgetID = apiOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Forum");
		verifyNoLikeWidgetOverview(widgetID);			
		
	}


}
