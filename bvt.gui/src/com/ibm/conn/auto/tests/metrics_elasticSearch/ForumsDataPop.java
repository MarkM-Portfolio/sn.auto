package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class ForumsDataPop extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(ForumsDataPop.class);
	private TestConfigCustom cfg;
	private ForumsUI ui;
	private CommunitiesUI commUI;	
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner;
	private String serverURL;
	private User testUser1, testUser2, testUser3, testUser4;


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser(); 
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		URLConstants.setServerURL(serverURL);

	}

	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - Add Forum Topic</li>
	 *<li><B>Step:</B> Public community is created via the API</li>
	 *<li><B>Step:</B> Create a forum topic</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void addForumTopicToCommunity() {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                     .access(Access.PUBLIC)
                                     .description("Create a Public community & add a forum topic. ")
                                     .build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_Forumtopic" + Helper.genDateBasedRandVal())
  		                                         .tags(Data.getData().ForumTopicTag)
  		                                         .description(Data.getData().commonDescription)
  		                                         .partOfCommunity(community)
  		                                         .build();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI1);
	
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		log.info("INFO: Open the community");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);

		ui.endTest();
	
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - Edit Community Forum Topic</li>
	 *<li><B>Step:</B> Create a Public community.</li>
	 *<li><B>Step:</B> Add a forum topic to the community.</li>
	 *<li><B>Step:</B> Edit the forum topic title & content.</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void editCommunityForumTopic() {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community.  Add & edit forum topic. ")
                                      .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get UUID of community");
		String commUUID = apiOwner.getCommunityUUID(comAPI);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle)
		                                              .tags(Data.getData().MultiFeedsTag2)
		                                              .description(Data.getData().commonDescription)
		                                              .partOfCommunity(community)
		                                              .parentForum(apiForum)
		                                              .build();

		BaseForumTopic newForumTopic = new BaseForumTopic.Builder(Data.getData().EditForumTopicTitle)
		                                                 .tags(Data.getData().ForumTopicTag)
		                                                 .description(Data.getData().EditForumTopicContent)
		                                                 .partOfCommunity(community)
		                                                 .parentForum(apiForum)
		                                                 .build();

		log.info("create Topic using API");
		ForumTopic apiForumTopic = forumTopic.createAPI(apiForumsOwner);	

		log.info("Login Community as owner");
		ui.loadComponent(Data.getData().ComponentCommunities);		
		ui.login(testUser1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Open the forum topic");
		ui.clickLinkWait("link=" + apiForumTopic.getTitle());

		log.info("INFO: Start to edit the topic");
		ui.clickLinkWait(ForumsUIConstants.EditTopic);

		log.info("INFO: Edit forum topic title");
		ui.clearText(ForumsUIConstants.Start_A_Topic_InputText_Title);
		ui.typeText(ForumsUIConstants.Start_A_Topic_InputText_Title, newForumTopic.getTitle());

		log.info("INFO: Edit forum topic contents");
		ui.typeInCkEditor(newForumTopic.getDescription());

		log.info("INFO: Save the forum topic changes");
		ui.clickSaveButton();
		
		log.info("INFO: Verify the updated forum topic title exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicTitle),
				"ERROR: The Topic title " + Data.getData().EditForumTopicTitle + " does not appear.");
		
		log.info("INFO: Verfiy the updated forum topic content exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicContent),
				"ERROR: The Topic title " + Data.getData().EditForumTopicContent + " does not appear.");

		ui.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - Reply to Forum Topic</li>
	 *<li><B>Step:</B> Create a community as User1.</li>
	 *<li><B>Step:</B> Add a forum topic as User1</li>
	 *<li><B>Step:</B> Add (1) reply to the topic as User2</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" })
	public void replyToCommForumTopic(){	
		
		String testName = ui.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                   .addMember(member)
                                                   .access(Access.MODERATED)
                                                   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("community forum topic " + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
				
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it.");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add forum widget to community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Log into communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		log.info("INFO: Logout: " + testUser1.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log in as: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities,true);
		commUI.login(testUser2);
		
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);		
		
		log.info("INFO: Select the Forum topic created by: " + testUser1.getDisplayName());
		ui.clickLink(ForumsUI.selectForumTopic(topic));

		log.info("INFO: Reply to the Forum topic");
		ui.replyToTopic(topic);
		
		log.info("INFO: Select Overview from the tabbed nav menu");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);
		
		log.info("INFO: Select the Forum topic again");
		ui.clickLink(ForumsUI.selectForumTopic(topic));
		
		log.info("INFO: Verify the forum topic reply appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().ReplyToForumTopic + topic.getTitle()),
				"ERROR: Reply to the forum topic does not appear");
		
		ui.endTest();
		
	}
		
	/**
	*<ul>
	*<li><B>Info: </B>Data Population - Standalone Forum: Create a Forum</li>
	*<li><B>Step: </B>Create a stand-alone forum</li>
	*</ul>
	* note: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/ 
	@Test(groups = { "regression"})
	public void createStandaloneForum(){
		
		String testName = ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().MultiFeedsTag2)
									   .description(Data.getData().commonDescription).build();

		log.info("INFO: Log into Forums as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the 'Owned Forums' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		log.info("INFO: Create a new Forum");
		forum.create(ui);
				
		log.info("INFO: Validate that the 'Start a topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_A_Topic),
				  			"ERROR: Unable to locate the 'Start a topic' button in the forum");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B> Data Population - Standalone Forum: Create a Forum Topic</li>
	*<li><B>Step: </B> Create a stand-alone forum </li>
	*<li><B>Step: </B> create a forum topic </li>
	*</ul>
	* note: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/ 
	@Test(groups = { "regression"})
	public void createStandaloneForumTopic(){
		
		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().MultiFeedsTag2)
									   .description(Data.getData().commonDescription)
									   .build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
  		                                         .tags(Data.getData().ForumTopicTag)
  		                                         .description(Data.getData().commonDescription)
  		                                         .build();

		log.info("INFO: Log into Forums as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the 'Owned Forums' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		log.info("INFO: Create a new Forum");
		forum.create(ui);
				
		log.info("INFO: Validate that the 'Start a topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_A_Topic),
				  			"ERROR: Unable to locate the 'Start a topic' button in the forum");

		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		log.info("INFO: Validate that the 'Reply to topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Reply_to_topic),
				  			"ERROR: Unable to locate the 'Reply to topic' button in the forum");
				
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B> Data Population - Standalone Forum: Create a Forum Topic Reply</li>
	*<li><B>Step: </B> As User1 create a stand-alone forum </li>
	*<li><B>Step: </B> Create a forum topic </li>
	*<li><B>Step: </B> Logout as User1 & login as User2 </li>
	*<li><B>Step: </B> Create a forum topic reply as User2 </li>
	*</ul>
	* note: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/
	
	@Test(groups = { "regression"})
	public void createStandaloneForumTopicReply(){

		ui.startTest();
		
		BaseForum forum = new BaseForum.Builder("standaloneForum " + Helper.genDateBasedRandVal())
									   .tags(Data.getData().ForumTopicTag)
									   .description(Data.getData().commonDescription)
									   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("standaloneForumTopic " + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().ForumTopicTag)
										   		 .description(Data.getData().commonDescription)
										   		 .build();
		
		log.info("INFO: Log into Forums as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the 'Owned Forums' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		log.info("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);
		
		log.info("INFO: Validate that the 'Start a topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_A_Topic),
				"ERROR: Unable to locate the 'Start a topic' button in the forum");

		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		log.info("INFO: Select the 'Forums' link in top left corner");
		ui.clickLinkWait(ForumsUIConstants.topComponentForumLink);
		
		log.info("INFO: Log out: " + testUser1.getDisplayName());
		ui.logout();
		
		log.info("INFO: Login to Forums as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser2);
				
		log.info("INFO: Select 'Public Forums' from left menu");
		ui.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
		
		log.info("INFO: Select the Forum created by " + testUser1.getDisplayName());
		ui.clickLinkWait("link=" + forum.getName());
		
		log.info("INFO: Select the Forum topic created by " + testUser1.getDisplayName());
		ui.clickLinkWait("link=" + topic.getTitle());

		log.info("INFO: Create a reply to the Forum topic");
		ui.replyToTopic(topic);
		
		log.info("INFO: Verify the forum topic reply appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().ReplyToForumTopic + topic.getTitle()),
				"ERROR: Reply to the forum topic does not appear");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B> Data Population - Standalone Forum: Edit a Forum Topic </li>
	*<li><B>Step: </B> Create a stand-alone forum </li>
	*<li><B>Step: </B> Create a forum topic </li>
	*<li><B>Step: </B> Edit the forum topic </li>
	*</ul>
	* note: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/ 
	@Test(groups = { "regression"})
	public void editStandaloneForumTopic(){
		
		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().MultiFeedsTag2)
									   .description(Data.getData().commonDescription)
									   .build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
  		                                         .tags(Data.getData().ForumTopicTag)
  		                                         .description(Data.getData().commonDescription)
  		                                         .build();
		
		BaseForumTopic newForumTopic = new BaseForumTopic.Builder(Data.getData().EditForumTopicTitle)
                                                         .tags(Data.getData().ForumTopicTag)
                                                         .description(Data.getData().EditForumTopicContent)
                                                         .build();

		log.info("INFO: Log into Forums as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the 'Owned Forums' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		log.info("INFO: Create a new Forum");
		forum.create(ui);
				
		log.info("INFO: Validate that the 'Start a topic' button displays in the forum");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.Start_A_Topic),
				  			"ERROR: Unable to locate the 'Start a topic' button in the forum");

		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		log.info("INFO: Click on the forum topic Edit link");
		ui.clickLinkWait(ForumsUIConstants.EditTopic);
		
		log.info("INFO: Edit forum topic title");
		ui.clearText(ForumsUIConstants.Start_A_Topic_InputText_Title);
		ui.typeText(ForumsUIConstants.Start_A_Topic_InputText_Title, newForumTopic.getTitle());

		log.info("INFO: Edit forum topic contents");
		ui.typeInCkEditor(newForumTopic.getDescription());

		log.info("INFO: Save the forum topic changes");
		ui.clickSaveButton();	

		log.info("INFO: Verify the updated forum topic title exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicTitle),
				"ERROR: The Topic title " + Data.getData().EditForumTopicTitle + " does not appear.");
		
		log.info("INFO: Verfiy the updated forum topic content exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicContent),
				"ERROR: The Topic title " + Data.getData().EditForumTopicContent + " does not appear.");
				
		ui.endTest();
		
	}
	
	/**	
	 * <ul>
	 * <li><B>Info:</B>Data Population - Community Forums:  (2) Members Follow a Forum</li>
	 * <li><B>Step:</B>Create a Public community with an additional owner & member using the API</li>	
	 * <li><B>Step:</B>Login to Communities as a member</li>	 
	 * <li><B>Step:</B>Navigate to the forum's homepage</li>	 
	 * <li><B>Step:</B>Click on the Following Actions button</li>
	 * <li><B>Step:</B>Click on the Follow this Forum link</li>
	 * <li><B>Step:</B>Logout the community member</li>
	 * <li><B>Step:</B>Login to Communities as the additional owner</li>	 
	 * <li><B>Step:</B>Navigate to the forum's homepage</li>	 
	 * <li><B>Step:</B>Click on the Following Actions button</li>
	 * <li><B>Step:</B>Click on the Follow this Forum link</li>
	 * </ul>		  
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void followCommunityForum(){
		
		String testName = ui.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.addMember(member)
													.addMember(member1)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("use API to get the default community forum");
		String commUUID = apiOwner.getCommunityUUID(comAPI);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
		
		log.info("INFO: Check to see if the Forums widget is enabled. If it is not enabled, then enable it.");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add Forums widget to the community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		log.info("INFO: Open the community");
		community.navViaUUID(commUI);
		
		this.followCommunityForum(apiForum);
				
		log.info("INFO: Log out as user: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		log.info("INFO: Open the community");
		community.navViaUUID(commUI);
		
		this.followCommunityForum(apiForum);
		
		ui.endTest();
		
	}
	
	/**	
	 * <ul>
	 * <li><B>Info:</B>Data Population - Standalone Forums:  (3)Users Follow a Forum</li>
	 * <li><B>Step:</B>Create a Public Forum using API</li>	
	 * <li><B>Step:</B>Log in as UserA & follow the forum</li>	 
	 * <li><B>Step:</B>Log out as UserA & in as UserB</li>	 
	 * <li><B>Step:</B>UserB follows the forum</li>
	 * <li><B>Step:</B>Log out as UserB & in as UserC</li>
	 * <li><B>Step:</B>UserC follows the forum</li>
	 * </ul>
	 *  NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.		  
	 */
	@Test(groups={"regression"})
	public void followStandaloneForum(){
		
		String testName = ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
		                               .tags(Data.getData().MultiFeedsTag2)
		                               .description(Data.getData().commonDescription).build();
		
		log.info("INFO: Create a new Forum using API");
		forum.createAPI(apiForumsOwner);
		
		log.info("INFO: Log into Forums as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums, true);
		ui.login(testUser2);
		
		this.followStandaloneForum(forum);
		
		log.info("INFO: Logout as: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Forums as: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums, true);
		ui.login(testUser3);
				
		this.followStandaloneForum(forum);
		
		log.info("INFO: Logout as: " + testUser3.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Forums as: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums, true);
		ui.login(testUser4);
		
		this.followStandaloneForum(forum);

		ui.endTest();

		
	}
	
	/**
	* The followStandaloneForum method will: 
	* - click on the Public Forums view
	* - click on the Forum to be followed
	* - click the Follow Actions menu link
	* - click Follow this Forum
	* - verify the follow confirmation message displays
	*/	
	private void followStandaloneForum(BaseForum forum){
		log.info("INFO: Click on the Public Forums view link");
		ui.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);

		log.info("INFO: Click on the Forum link");
		String forumLink = "link=" + forum.getName();
		ui.fluentWaitPresentWithRefresh(forumLink);
		ui.clickLink(forumLink);

		log.info("INFO: Click on the Follow Actions menu link");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);

		log.info("INFO: Click on the Follow this Forum link");
		ui.clickLinkWait(ForumsUIConstants.Start_Following_Forum);

		log.info("INFO: Verify the following confirmation message: " + ForumsUIConstants.Forum_Following_Message + " displays.");
		Assert.assertTrue(driver.isTextPresent(ForumsUIConstants.Forum_Following_Message),
				"ERROR: The confirmation message does not appear");
	}
	
	/**
	* The followCommunityForum method will: 
	* - click Forums tab on the nav. menu
	* - click Forums view tab 
	* - click on the Forum to be followed
	* - click Follow Actions menu link
	* - click on the Follow this Forum link
	* - verify the follow confirmation message displays
	*/		
	private void followCommunityForum(Forum apiForum) {
		
		log.info("INFO: Click on the Forums tab on the navigation menu");
		Community_TabbedNav_Menu.FORUMS.select(commUI);
		
		log.info("INFO: Click on the Forums view tab to display list of Forums");
		ui.clickLinkWait(ForumsUIConstants.communityForumsTab);
		
		log.info("INFO: Click on the Forum to be followed");
		ui.clickLinkWait("link=" + apiForum.getTitle());
		
		log.info("INFO: Click on the Follow Actions menu link");
		ui.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		log.info("INFO: Click on the Follow this Forum link");
		ui.clickLinkWait(ForumsUIConstants.Start_Following_Forum);
		
		log.info("INFO: Verify the following confirmation message: " + ForumsUIConstants.Forum_Following_Message + " displays.");
		Assert.assertTrue(driver.isTextPresent(ForumsUIConstants.Forum_Following_Message),
				"ERROR: The confirmation message does not appear");
		
	}
}
