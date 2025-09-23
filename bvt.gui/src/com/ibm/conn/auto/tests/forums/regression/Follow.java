package com.ibm.conn.auto.tests.forums.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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

public class Follow extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Follow.class);
	private ForumsUI ui;
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
		
		
	}
	/**
	 * Test Case: verify if the user is following a community forum, he can see 
	 * Stop Following this Forum and Stop Following this Community in the Following Actions
	 * <ul>
	 * <li><B>Info: </B>Verify a user can stop following forum and community </li>
	 * <li><B>Step: </B>Use API to create a community, and get the default community forum</li>	
	 * <li><B>Step: </B>Open browser, and login to Communities as an owner</li>
	 * <li><B>Step: </B>navigate to the forum's homepage</li>	 
	 * <li><B>Step: </B>click the Following Actions button</li>
	 * <li><B>Verify: </B>verify after click Following Actions, because the user created the community and forum,
	 *					there will be Stop Following this Forum option and Stop Following this Community option</li>
	 *</ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testCommunityForumFollowActionsAsOwner(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();	
		
		String rand = Helper.genDateBasedRand();
		String communityName = testName + "community forum "+rand;
		
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apicommunity = community.createAPI(apiOwner);		
		
		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		//GUI
		//Load component and login
		logger.strongStep("Open browser, and login as the community owner" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login as the community owner");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the forum's homepage");
		log.info("INFO: navigate to the forum's homepage");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Click the Following Actions button");
		log.info("INFO: click the Following Actions button");
		ui.fluentWaitElementVisible(ForumsUIConstants.Forum_Follow_Actions);
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Verify after click Following Actions, because the user created the community, forum and topic, " +
				"there will be Stop Following this Forum option and Stop Following this Community option");
		log.info("INFO: verify after click Following Actions, because the user created the community, forum and topic, " +
				"there will be Stop Following this Forum option and Stop Following this Community option");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Forum),
				"failed to find Follow this Forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Community),
				"failed to find Follow this Community");
		
	}
	/**
	 * TestCase: Verify the member of a community, can see the Following options are:
	 * Follow this Forum
	 * Follow this Community
	 * Test Case: verify if the user is following a community forum, he can see 
	 * Stop Following this Forum and Stop Following this Community in the Following Actions
	 * <ul>
	 * <li><B>Info: </B>Verify a member can stop following forum and community </li>
	 * <li><B>Step: </B>Use API to create a community, and get the default community forum</li>	
	 * <li><B>Step: </B>Open browser, and login to Communities as a member</li>
	 * <li><B>Step: </B>navigate to the forum's homepage</li>	 
	 * <li><B>Step: </B>click the Following Actions button</li>
	 * <li><B>Verify: </B>verify after click Following Actions, because the user is just a member, he is not following this forum,
	 *					there will be Follow this Forum option and Follow this Community option</li>
	 *</ul>		  
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testCommunityForumFollowActionsAsMember(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = testName + "community forum "+rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community		
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apicommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		//GUI
		//Load component and login
		logger.strongStep("Open browser, and login as community Member" + testUser2.getDisplayName());
		log.info("INFO: open browser, and login as community Member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		logger.strongStep("Navigate to the forum's homepage");
		log.info("INFO: navigate to the forum's homepage");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");
		ui.fluentWaitElementVisible(ForumsUIConstants.Forum_Follow_Actions);
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Verify the following actions are 'Follow this Forum' and 'Follow this Community'");
		log.info("INFO: verify the following actions are 'Follow this Forum' and 'Follow this Community'");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_Following_Forum),
				"Not found Follow this Forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_Following_Community),
				"Not found Follow this Community");
	}
	/**
	 * TestCase: verify an owner who creates a community, community forum, and topic can see 
	 * Stop Following this Topic, Stop Following this Forum, Stop Following this Community
	 * TEST CASE: different tabs of I'm an owner view
	 * <ul>
	 * <li><B>Info: </B>verify different tabs of I'm an Owner view</li>
	 * <li><B>Step: </B>Use API to create a community,and get the default community forum </li>
	 * <li><B>Step: </B>Use API to create a topic in the default community forum. </li>		 
	 * <li><B>Step: </B>Open browser, and login to Forums </li>
	 * <li><B>Step: </B>navigate to the topic's home page </li>
	 * <li><B>Verify: </B>verify after click Following Actions, because the user created the community, forum and topic,
	 *				there will be Stop Following this Forum option and Stop Following this Community option
	 *				and Stop Following this Topic option</li>
	 *</ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testFollowingATopicUIActions(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = testName + "community forum "+rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apicommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("Topic for " + testName)
													  .tags(Data.getData().ForumTopicTag)
													  .description(Data.getData().commonDescription)		  
													  .markAsQuestion(false)
													  .parentForum(apiForum)
													  .build();
		logger.strongStep("Use API to create a topic");
		log.info("INFO: use API to create a topic");
		ForumTopic topic = forumTopic2.createAPI(apiForumsOwner);

		//GUI
		//Load component and login
		logger.strongStep("Open browser, and login as the owner" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login as the owner");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the topic's home page");
		log.info("INFO: navigate to the topic's home page");
		driver.navigate().to(topic.getAlternateLink());
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");
		ui.fluentWaitElementVisible(ForumsUIConstants.Forum_Follow_Actions);
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Verify the option list is 'Stop Following this Topic', 'Stop Following this Forum', " +
				"and 'Stop Following this Community'");
		log.info("INFO: verify the option list is 'Stop Following this Topic', 'Stop Following this Forum', " +
				"and 'Stop Following this Community'");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Topic),
				"failed to find Follow this Topic");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Forum),
				"failed to find Follow this Forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Community),
				"failed to find Follow this Community");
		
	}
	/**
	 * Test Case: verify a user is not owner or member of a public community, click any community forum link to open forum page, the Follow button is invisible to this user,
	 * and the related button is "Community Actions"
	 * <ul>
	 * <li><B>Info: </B>verify a non-member of a public community, click any community forum link to open forum page and Follow button is invisible to this user</li>
	 * <li><B>Step: </B>Use API to create a community, and get the default community forum</li>	
	 * <li><B>Step: </B>Open browser, and login to Communities as a non-Member</li>
	 * <li><B>Step: </B>navigate to the topic's homepage</li> 
	 * <li><B>Verify: </B>Check there is not the Following Actions button, but Join the Community button</li>
	 *</ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testNonMemberFollowPublicTopicUIActions(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();

		String rand = Helper.genDateBasedRand();
		
		String communityName = testName + "community forum "+rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apicommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		//GUI
		//Load component and login
		logger.strongStep("Open browser, and log in as non-member" + testUser2.getDisplayName());
		log.info("INFO: open browser, and log in as non-member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		logger.strongStep("Navigate to the topic's homepage");
		log.info("INFO: navigate to the topic's homepage");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Verify there is no Following Actions button");
		log.info("INFO: verify there is no Following Actions button");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.Forum_Follow_Actions),
				"Follow button should not show here.");
		
		logger.strongStep("Verify there is the Join the Community button");
		log.info("INFO: verify there is the Join the Community button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.Join_the_Community),
				"Join this Community is Expected");
		
	}
	/**
	 * Test Case: click any community forum link to open forum page, 
	 * verify users can follow/stop following this forum/community by clicking the buttons in "Follow" drop-down list.
	 * <ul>
	 * <li><B>Info: </B>Verify users can follow/stop following this forum/community by clicking the buttons in "Follow" drop-down list.</li>
	 * <li><B>Step: </B>Use API to create a community, and get the default community forum</li>	
	 * <li><B>Step: </B>Open browser, and login to Communities as an owner</li>
	 * <li><B>Step: </B>navigate to the forum's homepage</li> 
	 * <li><B>Step: </B>click the Following Actions button</li>
	 * <li><B>Verify: </B>verify after click Following Actions, because the user created the community and forum,
	 * there will be Stop Following this Forum option and Stop Following this Community option</li>
	 * <li><B>Step: </B>click Stop Following this Forum</li>
	 * <li><B>Verify: </B>verify there is Follow this Forum option</li>
	 * <li><B>Step: </B>click Stop Following this Community</li>
	 * <li><B>Verify: </B>verify there is Follow this Community option</li>	
	 *</ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testFollowActionListStatusSwitch(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = testName + "community forum "+rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apicommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		//GUI
		//Load component and login
		logger.strongStep("open browser, and login as the community owner" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login as the community owner");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the forum's homepage");
		log.info("INFO: navigate to the forum's homepage");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");
		ui.fluentWaitElementVisible(ForumsUIConstants.Forum_Follow_Actions);
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Verify that 'Stop Following this Forum' and 'Stop Following this Community' links are displayed");
		log.info("INFO: verify that 'Stop Following this Forum' and 'Stop Following this Community' links are displayed");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Forum),
				"failed to find Stop Following this Forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Community),
				"failed to find Stop Following this Community");
		
		logger.strongStep("Click Stop Following this Forum");
		log.info("INFO: click Stop Following this Forum");		
		ui.clickLinkWait(ForumsUIConstants.Stop_Following_Forum);
		
		logger.strongStep("Verify that 'Stop following forum' is not displayed and 'Follow this Forum' is displayed");
		log.info("INFO: Verify that 'Stop following forum' is not displayed and 'Follow this Forum' is displayed");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.Stop_Following_Forum),
				"Stop Followting this Forum should disappear");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_Following_Forum),
				"failed to find Follow this Forum");
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");		
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);

		logger.strongStep("Click Follow this Forum");
		log.info("INFO: click Follow this Forum");
		ui.clickLinkWait(ForumsUIConstants.Start_Following_Forum);
		
		logger.strongStep("Verify that 'Stop Following this Forum' is displayed and 'Follow this Forum' is not displayed");
		log.info("INFO: verify that 'Stop Following this Forum' is displayed and 'Follow this Forum' is not displayed");	
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Forum),
				"failed to find Follow this Forum");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.Start_Following_Forum),
				"Follow this Forum should disappear");
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Click Stop Following this Community");
		log.info("INFO: click Stop Following this Community");
		ui.clickLinkWait(ForumsUIConstants.Stop_Following_Community);
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Verify that 'stop Following this Community' is not displayed and 'Follow this Community' is displayed");
		log.info("INFO: verify that 'stop Following this Community' is not displayed and 'Follow this Community' is displayed");		
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.Stop_Following_Community),
				"stop Following this Community should disappear");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_Following_Community),
				"failed to find Follow this Community");
		
		logger.strongStep("Click Follow this Community");
		log.info("INFO: click Follow this Community");
		ui.clickLinkWait(ForumsUIConstants.Start_Following_Community);
		
		logger.strongStep("Click Following Actions");
		log.info("INFO: click Following Actions");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		logger.strongStep("Verify that 'stop Following this Community' is displayed and 'Follow this Community' is not displayed");
		log.info("INFO: verify that 'stop Following this Community' is displayed and 'Follow this Community' is not displayed");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Stop_Following_Community),
				"failed to find Stop Following this Community");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.Start_Following_Community),
				"Follow this Community should disappear");
		
		
	}
	/**
	 * <ul>
	 * <li><B>Info: </B>Test Case: On-Premise Only.</li>
	 * <li><B>Info: </B>verify the followed forums will be displayed in My Forums - I'm Following view, 
	 * 				and also verify the forums will disappear from My Forums-I'm Following view if user stops following it.</li>
	 * <li><B>Step: </B>Use API to create a community, and get the default community forum</li>	
	 * <li><B>Step: </B>Open browser, and login to Forums as an owner</li>
	 * <li><B>Step: </B>click I'm Following</li>	 
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify the following forum is shown up on the ImFollowing_Forums tab</li>
	 * <li><B>Step: </B>use API to stop following this forum</li>
	 * <li><B>Step: </B>refresh the page</li>
	 * <li><B>Step: </B>click I'm Following</li>
	 * <li><B>Step: </B>click the Forums tab</li>
	 * <li><B>Verify: </B>verify the following forum disappears from the ImFollowing_Forums tab</li>
	 * </ul>	
	 */
	@Test(groups={"regression"})
	public void testAfterStopFollowingForumNotPresentImFollowing(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = testName + "community forum "+rand;
		BaseCommunity community = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)													
													.build();
		//create community	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apicommunity = community.createAPI(apiOwner);

		logger.strongStep("Use API to get the default community forum");
		log.info("INFO: use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(apicommunity);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		//GUI
		//Load component and login
		logger.strongStep("Open browser, and login to Forums as the owner" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Forums as the owner");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		logger.strongStep("Click I'm Following");
		log.info("INFO: click I'm Following");
		ui.clickLinkWait(ForumsUIConstants.Im_Following);
		
		logger.strongStep("Click Forums tab");
		log.info("INFO: click Forums tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		logger.strongStep("Verify the following forum is shown up on the ImFollowing_Forums tab");
		log.info("INFO: verify the following forum is shown up on the ImFollowing_Forums tab");
		Assert.assertTrue(driver.isTextPresent(apiForum.getTitle()), 
				apiForum.getTitle() + "should on the Forums tab of I'm Following view");
		
		logger.strongStep("Use API to stop following the forum");
		log.info("INFO: use API to stop following the forum");
		apiForumsOwner.stopFollowing(apiForum);
		
		logger.strongStep("Refresh the page");
		log.info("INFO: refresh the page");
		driver.navigate().refresh();
		
		logger.strongStep("Click I'm Following");
		log.info("INFO: click I'm Following");
		ui.clickLinkWait(ForumsUIConstants.Im_Following);
		
		logger.strongStep("Click Forums tab");
		log.info("INFO: click Forums tab");
		ui.clickLinkWait(ForumsUIConstants.Forum_Tab);
		
		logger.strongStep("Verify after stop following, the forum disappears from the ImFollowing_Forums tab");
		log.info("INFO: verify after stop following, the forum disappears from the ImFollowing_Forums tab");
		Assert.assertTrue(driver.isTextNotPresent(apiForum.getTitle()), 
				apiForum.getTitle() + "should NOT on the Forums tab of I'm Following view");
		
	}
		
}
