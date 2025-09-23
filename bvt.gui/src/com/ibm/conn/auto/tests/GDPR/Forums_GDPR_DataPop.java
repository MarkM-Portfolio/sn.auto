package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
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
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Forums_GDPR_DataPop extends SetUpMethods2{


	private static Logger log = LoggerFactory.getLogger(Forums_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private CommunitiesUI commUI;
	private ForumsUI fUI;
	private String serverURL;
	private User testUser1, testUser2;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private APIForumsHandler apiForumsOwner1, apiForumsOwner2;
	private boolean isOnPremise;
	
	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUI = ForumsUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
		
		//check environment to see if on-prem or on the cloud
				if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
					isOnPremise = true;
				} else {
					isOnPremise = false;
				}
					
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {		
		
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		apiForumsOwner1 = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsOwner2 = new APIForumsHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Standalone Forum: UserA Creates a Forum Topic & UserB Replies</li>
	*<li><B>Step:</B> UserA creates a stand-alone forum</li>
	*<li><B>Step:</B> UserA creates a forum topic</li>
	*<li><B>Step:</B> UserB replies to the forum topic</li>
	*</ul>
	* NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/
	
	@Test(groups = {"regression"}, enabled=false)
	public void userACreatesTopicUserBReplies(){

		String testName=fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().ForumTopicTag)
									   .description(Data.getData().commonDescription)
									   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().ForumTopicTag)
										   		 .description(Data.getData().commonDescription)
										   		 .build();
		
		if(isOnPremise){
			
		log.info("INFO: Create a Forum using the API");
		apiforum.createAPI(apiForumsOwner1);
		
		log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
		fUI.loadComponent(Data.getData().ComponentForums);
		fUI.login(testUser1);		
		
		log.info("INFO: Create a forum topic");
		createForumTopic(apiforum, topic);
				
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		fUI.logout();
		fUI.close(cfg);
		
		log.info("INFO: Login to Forums as UserB: " + testUser2.getDisplayName());
		fUI.loadComponent(Data.getData().ComponentForums);
		fUI.login(testUser2);
				
		log.info("INFO: Select 'Public Forums' from left menu");
		fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
		
		log.info("INFO: Select the Forum created by UserA: " + testUser1.getDisplayName());
		fUI.clickLinkWait("link=" + apiforum.getName());
		
		log.info("INFO: Select the Forum topic created by UserA: " + testUser1.getDisplayName());
		fUI.clickLinkWait("link=" + topic.getTitle());

		log.info("INFO: Create a reply to the Forum topic");
		fUI.replyToTopic(topic);
		
		log.info("INFO: Verify the forum topic reply appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().ReplyToForumTopic + topic.getTitle()),
				"ERROR: Reply to the forum topic does not appear");
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		fUI.logout();
		fUI.close(cfg);
		
		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}
		
		fUI.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Standalone Forum: UserB Creates a Forum Topic & UserA Replies</li>
	*<li><B>Step:</B> UserB creates a stand-alone forum</li>
	*<li><B>Step:</B> UserB creates a forum topic</li>
	*<li><B>Step:</B> UserA replies to the forum topic</li>
	*</ul>
	* NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/
	
	@Test(groups = {"regression"}, enabled=false)
	public void userBCreatesTopicUserAReplies(){

		String testName=fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().ForumTopicTag)
									   .description(Data.getData().commonDescription)
									   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().ForumTopicTag)
										   		 .description(Data.getData().commonDescription)
										   		 .build();
		
		
		if(isOnPremise){

			log.info("INFO: Create a Forum using the API");
			apiforum.createAPI(apiForumsOwner2);

			log.info("INFO: Log into Forums as UserB: " + testUser2.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser2);

			log.info("INFO: Create a forum topic");
			createForumTopic(apiforum, topic);

			log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

			log.info("INFO: Login to Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Select 'Public Forums' from left menu");
			fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);

			log.info("INFO: Select the Forum created by UserB: " + testUser2.getDisplayName());
			fUI.clickLinkWait("link=" + apiforum.getName());

			log.info("INFO: Select the Forum topic created by UserB: " + testUser2.getDisplayName());
			fUI.clickLinkWait("link=" + topic.getTitle());

			log.info("INFO: Create a reply to the Forum topic");
			fUI.replyToTopic(topic);

			log.info("INFO: Verify the forum topic reply appears");
			Assert.assertTrue(driver.isTextPresent(Data.getData().ReplyToForumTopic + topic.getTitle()),
					"ERROR: Reply to the forum topic does not appear");

			log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Standalone Forum: Edit a Forum Topic</li>
	*<li><B>Step:</B> UserA creates a stand-alone forum</li>
	*<li><B>Step:</B> UserA creates a forum topic</li>
	*<li><B>Step:</B> UserA edits the forum topic</li>
	*</ul>
	* NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/ 
	@Test(groups = {"regression"}, enabled=false)
	public void userACreatesAndEditsStandaloneForumTopic(){
		
		String testName=fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().MultiFeedsTag2)
									   .description(Data.getData().commonDescription)
									   .build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
  		                                         .tags(Data.getData().ForumTopicTag)
  		                                         .description(Data.getData().commonDescription)
  		                                         .build();
		
		BaseForumTopic newForumTopic = new BaseForumTopic.Builder("GDPR: " +Data.getData().EditForumTopicTitle + " by UserA")
                                                         .tags(Data.getData().ForumTopicTag)
                                                         .description(Data.getData().EditForumTopicContent + " by UserA")
                                                         .build();

		if(isOnPremise){

			log.info("INFO: Create a Forum using API");
			apiforum.createAPI(apiForumsOwner1);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Create a forum topic");
			createForumTopic(apiforum, topic);	

			log.info("INFO: Edit the forum topic");
			editForumTopic(newForumTopic);

			log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Standalone Forum: UserA Edits Topic Created by UserB</li>
	*<li><B>Step:</B> UserB creates a stand-alone forum</li>
	*<li><B>Step:</B> UserB adds UserA as an additional Owner to the forum</li>
	*<li><B>Step:</B> UserB creates a forum topic</li>
	*<li><B>Step:</B> UserA edits the forum topic</li>
	*</ul>
	* NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.
	*/ 
	@Test(groups = {"regression"}, enabled=false)
	public void userAEditsStandaloneForumTopicCreatedByUserB(){
		
		String testName=fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().MultiFeedsTag2)
									   .description(Data.getData().commonDescription)
									   .build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
  		                                         .tags(Data.getData().ForumTopicTag)
  		                                         .description(Data.getData().commonDescription)
  		                                         .build();
		
		BaseForumTopic newForumTopic = new BaseForumTopic.Builder("GDPR: " +Data.getData().EditForumTopicTitle + " by UserA")
                                                         .tags(Data.getData().ForumTopicTag)
                                                         .description(Data.getData().EditForumTopicContent + " by UserA")
                                                         .build();

		if(isOnPremise){

			log.info("INFO: Create a Forum using the API");
			apiforum.createAPI(apiForumsOwner2);

			log.info("INFO: Log into Forums as UserB: " + testUser2.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser2);

			log.info("INFO: Navigate to the 'Public Forums' view");
			fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);

			log.info("INFO: Click on the Forum");
			fUI.clickLinkWait("link=" + apiforum.getName());

			log.info("INFO: Add UserA: " + testUser1.getDisplayName() + " to the forum as an Owner");
			addOwnerToForum(testUser1);

			log.info("INFO: Create a Forum topic");
			topic.create(fUI);

			log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Select the forum topic");
			selectForumTopic(apiforum,topic);

			log.info("INFO: Edit the forum topic");
			editForumTopic(newForumTopic);

			log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();

	}

	/**	
	 * <ul>
	 * <li><B>Info:</B>Data Population - Standalone Forums: UserA Follows Forum Created by UserB</li>
	 * <li><B>Step:</B>UserB creates a Forum using the API</li>	
	 * <li><B>Step:</B>UserA f the forum</li>
	 * </ul>
	 *  NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.		  
	 */
	@Test(groups={"regression"}, enabled=false)
	public void userAFollowsStandaloneForumCreatedByUserB(){
		
		String testName = fUI.startTest();
		
		BaseForum forum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                               .tags(Data.getData().MultiFeedsTag2)
		                               .description(Data.getData().commonDescription).build();
		
		if(isOnPremise){
			log.info("INFO: Create a Forum using the API");
			forum.createAPI(apiForumsOwner2);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums, true);
			fUI.login(testUser1);

			log.info("INFO: UserA: " + testUser1.getDisplayName() + " follows the forum");
			followStandaloneForum(forum);

			log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();
	}
	
	/**	
	 * <ul>
	 * <li><B>Info:</B>Data Population - Standalone Forums: UserB Follows Forum Created by UserA</li>
	 * <li><B>Step:</B>UserA creates a Forum using API</li>	
	 * <li><B>Step:</B>UserB follows the forum</li>
	 * </ul>
	 *  NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.		  
	 */
	@Test(groups={"regression"}, enabled=false)
	public void userBFollowsStandaloneForumCreatedByUserA(){
		
		String testName = fUI.startTest();
		
		BaseForum forum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                               .tags(Data.getData().MultiFeedsTag2)
		                               .description(Data.getData().commonDescription).build();
		
		if(isOnPremise){
			log.info("INFO: Create a Forum using the API");
			forum.createAPI(apiForumsOwner1);

			log.info("INFO: Log into Forums as UserB: " + testUser2.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums, true);
			fUI.login(testUser2);

			log.info("INFO: UserB " + testUser2.getDisplayName() + " follows the forum");
			followStandaloneForum(forum);

			log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}
		fUI.endTest();

	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone - UserA Creates a Topic & Likes the Topic</li>
	 *<li><B>Step:</B> UserA creates forum via API</li>
	 *<li><B>Step:</B> UserA navigates to the forum and creates a topic</li>
	 *<li><B>Step:</B> UserA 'likes' the topic</li>
	 *</ul>
	 * NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.	
	 */	
	@Test(groups = {"regression"}, enabled=false)
	public void userALikesStandaloneForumTopic() {
		
		String testName = fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                       .tags(Data.getData().MultiFeedsTag2)
                                       .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
                                                 .tags(Data.getData().ForumTopicTag)
                                                 .description(Data.getData().commonDescription)
                                                 .build();
		
		if (isOnPremise){
			log.info("INFO: Create a Forum using the API");
			apiforum.createAPI(apiForumsOwner1);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Create a forum topic");
			createForumTopic(apiforum, topic);

			log.info("INFO: 'Like' the topic");
			fUI.clickLinkWait(ForumsUIConstants.LikeLink);

			log.info("INFO: Logout UserA " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);		
		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone - UserB Creates a Topic &  UserA Likes the Topic</li>
	 *<li><B>Step:</B> UserB creates a forum via API</li>
	 *<li><B>Step:</B> UserB navigates to the forum and creates a topic</li>
	 *<li><B>Step:</B> UserA 'likes' the topic</li>
	 *</ul>
	 * NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.	
	 */	
	@Test(groups = {"regression"}, enabled=false)
	public void userALikesStandaloneTopicCreatedByUserB() {
		
		String testName = fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                      .tags(Data.getData().MultiFeedsTag2)
                                      .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
                                                .tags(Data.getData().ForumTopicTag)
                                                .description(Data.getData().commonDescription)
                                                .build();
		
		if (isOnPremise){
			log.info("INFO: Create a Forum using API");
			apiforum.createAPI(apiForumsOwner2);

			log.info("INFO: Log into Forums as UserB: " + testUser2.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser2);

			log.info("INFO: Create a forum topic");
			createForumTopic(apiforum, topic);

			log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

			log.info("INFO: Log in as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Select the topic");
			selectForumTopic(apiforum, topic);

			log.info("INFO: 'Like' the topic");
			fUI.clickLinkWait(ForumsUIConstants.LikeLink);

			log.info("INFO: Logout UserA " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);	
		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone - UserA Creates a Topic as a Question</li>
	 *<li><B>Step:</B> UserA creates a forum via API</li>
	 *<li><B>Step:</B> UserA navigates to the forum and creates a topic as a question</li>
	 *</ul>
	 * NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.	
	 */	
	@Test(groups = {"regression"}, enabled=false)
	public void userACreatesTopicAsQuestion() {
		
		String testName = fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                      .tags(Data.getData().MultiFeedsTag2)
                                      .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
                                                .tags(Data.getData().ForumTopicTag)
                                                .description(Data.getData().commonDescription)
                                                .markAsQuestion(true)
                                                .build();
		
		if(isOnPremise){
			log.info("INFO: Create a Forum using the API");
			apiforum.createAPI(apiForumsOwner1);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Create a forum topic & mark it as a question");
			createForumTopic(apiforum, topic);		

			log.info("INFO: Logout UserA " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);		
		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone - UserB Creates a Forum, UserA Creates a Topic as a Question</li>
	 *<li><B>Step:</B> UserB creates forum via API</li>
	 *<li><B>Step:</B> UserA navigates to the forum and creates a topic as a question</li>
	 *</ul>
	 *NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.	
	 */	
	@Test(groups = {"regression"}, enabled=false)
	public void userBCreatesForumUserACreatesTopicAsQuestion() {
		
		String testName = fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                     .tags(Data.getData().MultiFeedsTag2)
                                     .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
                                               .tags(Data.getData().ForumTopicTag)
                                               .description(Data.getData().commonDescription)
                                               .markAsQuestion(true)
                                               .build();
		
		if(isOnPremise){
			log.info("INFO: Create a Forum using the API");
			apiforum.createAPI(apiForumsOwner2);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Create a forum topic");
			createForumTopic(apiforum, topic);	

			log.info("INFO: Logout UserA " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);	
		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();
	}

	
	/**
	 *
	 *<ul>
     *<li><B>Info:</B> Data Population: Standalone - UserA Marks UserB's Topic Reply as the Answer to the Question</li>
	 *<li><B>Step:</B> UserA creates a forum via API</li>
	 *<li><B>Step:</B> UserA navigates to the forum and creates a topic as a question</li>
	 *<li><B>Step:</B> UserB replies to the topic</li>
	 *<li><B>Step:</B> UserA accepts UserB's reply as the answer - clicks on the 'Accept this Answer' link</li>
	 *</ul>
	 *NOTE: this test is not supported in the cloud.  Standalone Forums does not exist in the cloud.	
	 */	
	@Test(groups = {"regression"}, enabled=false)
	public void userAMarksUserBReplyAsAnswerToTopicQuestion() {
		
		String testName = fUI.startTest();
		
		BaseForum apiforum = new BaseForum.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                    .tags(Data.getData().MultiFeedsTag2)
                                    .description(Data.getData().commonDescription).build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: " + testName + "_topic" + Helper.genDateBasedRandVal())
                                              .tags(Data.getData().ForumTopicTag)
                                              .description(Data.getData().commonDescription)
                                              .markAsQuestion(true)
                                              .build();

		if(isOnPremise){
			log.info("INFO: Create a Forum using the API");
			apiforum.createAPI(apiForumsOwner1);

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Create a forum topic");
			createForumTopic(apiforum, topic);	

			log.info("INFO: Logout UserA " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);	

			log.info("INFO: Log into Forums as UserB: " + testUser2.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser2);

			log.info("INFO: Select 'Public Forums' from left menu");
			fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);

			log.info("INFO: Select the Forum created by UserA: " + testUser1.getDisplayName());
			fUI.clickLinkWait("link=" + apiforum.getName());

			log.info("INFO: Select the Forum topic created by UserA: " + testUser1.getDisplayName());
			fUI.clickLinkWait("link=" + topic.getTitle());

			log.info("INFO: Create a reply to the Forum topic");
			fUI.replyToTopic(topic);	

			log.info("INFO: Logout UserB " + testUser2.getDisplayName());
			fUI.logout();
			fUI.close(cfg);	

			log.info("INFO: Log into Forums as UserA: " + testUser1.getDisplayName());
			fUI.loadComponent(Data.getData().ComponentForums);
			fUI.login(testUser1);

			log.info("INFO: Select the topic");
			selectForumTopic(apiforum, topic);

			log.info("INFO: Click on the Accept this Answer link from UserB's reply");
			fUI.clickLinkWait(ForumsUIConstants.AcceptAsAnswer);

			log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
			fUI.logout();
			fUI.close(cfg);

		}else {
			log.info("INFO: Cloud environment does not support standalone Forums - skipping this test");
		}

		fUI.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - Edit Community Forum Topic</li>
	 *<li><B>Step:</B> UserA creates a Public community via the API.</li>
	 *<li><B>Step:</B> UserA adds a forum topic to the community.</li>
	 *<li><B>Step:</B> UserA edits the forum topic title & content.</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditsCommForumTopic() {
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                    .access(Access.PUBLIC)
                                    .description("GDPR data pop: UserA adds & edits a forum topic.")
                                    .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		String commUUID = apiCommOwner1.getCommunityUUID(comAPI);	
		
		log.info("INFO: Get the default forum name");
		Forum apiForum = apiForumsOwner1.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		BaseForumTopic forumTopic = new BaseForumTopic.Builder("GDPR: " + Data.getData().ForumTopicTitle)
		                                              .tags(Data.getData().MultiFeedsTag2)
		                                              .description("GDPR data pop: topic added by UserA")
		                                              .partOfCommunity(community)
		                                              .parentForum(apiForum)
		                                              .build();

		BaseForumTopic newForumTopic = new BaseForumTopic.Builder("GDPR: " + Data.getData().EditForumTopicTitle + " by UserA")
		                                                 .tags(Data.getData().ForumTopicTag)
		                                                 .description(Data.getData().EditForumTopicContent + " by UserA")
		                                                 .partOfCommunity(community)
		                                                 .parentForum(apiForum)
		                                                 .build();

		log.info("Create forum topic using API");
		forumTopic.createAPI(apiForumsOwner1);

		log.info("Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);		
		commUI.login(testUser1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);
		
		log.info("INFO: Edit community forum topic");
		editCommunityForumTopic(forumTopic, newForumTopic);
		
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - Edit Community Forum Topic Created By UserB</li>
	 *<li><B>Step:</B> UserB creates a Public community, with testUser1 as an additional owner, via the API.</li>
	 *<li><B>Step:</B> UserB adds a forum topic to the community.</li>
	 *<li><B>Step:</B> UserA edits the forum topic title & content.</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditsCommForumTopicCreatedByUserB() {
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                   .access(Access.PUBLIC)
                                   .description("GDPR data pop: UserA edits the forum topic created by UserB.")
                                   .addMember(new Member(CommunityRole.OWNERS, testUser1))
                                   .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		String commUUID = apiCommOwner2.getCommunityUUID(comAPI);	
		
		log.info("INFO: Get the default forum name");
		Forum apiForum = apiForumsOwner2.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		BaseForumTopic forumTopic = new BaseForumTopic.Builder("GDPR: " + Data.getData().ForumTopicTitle)
		                                              .tags(Data.getData().MultiFeedsTag2)
		                                              .description("GDPR data pop: topic is edited by UserA")
		                                              .partOfCommunity(community)
		                                              .parentForum(apiForum)
		                                              .build();

		BaseForumTopic newForumTopic = new BaseForumTopic.Builder("GDPR: " + Data.getData().EditForumTopicTitle + " by UserA")
		                                                 .tags(Data.getData().ForumTopicTag)
		                                                 .description(Data.getData().EditForumTopicContent + " by UserA")
		                                                 .partOfCommunity(community)
		                                                 .parentForum(apiForum)
		                                                 .build();

		log.info("Create forum topic using API");
		forumTopic.createAPI(apiForumsOwner2);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Refresh the browser window to make sure the community appears in the view");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Open the community");
		commUI.clickLinkWait("link=" + community.getName());
		
		log.info("INFO: Edit community forum topic");
		editCommunityForumTopic(forumTopic, newForumTopic);
		
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserB Replies to Forum Topic Created By UserA</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds a forum topic</li>
	 *<li><B>Step:</B> UserB replies to the forum topic</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userBRepliesToCommForumTopicCreatedByUserA(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
                                                 .description("GDPR data pop: UserB replies to topic created by UserA")
                                                 .access(Access.MODERATED)
                                                 .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic");
		topic.create(fUI);
		
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log in as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);		
		
		log.info("INFO: Select the Forum topic created by UserA: " + testUser1.getDisplayName());
		commUI.clickLinkWait(ForumsUI.selectForumTopic(topic));

		log.info("INFO: Reply to the Forum topic");
		fUI.replyToTopic(topic);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserA Replies to Forum Topic Created By UserB</li>
	 *<li><B>Step:</B> UserB creates a community with UserA as a member via API</li>
	 *<li><B>Step:</B> UserB adds a forum topic</li>
	 *<li><B>Step:</B> UserA replies to the forum topic</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userARepliesToCommForumTopicCreatedByUserB(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                                .description("GDPR data pop: UserA replies to topic created by UserB")
                                                .access(Access.MODERATED)
                                                .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
	
		log.info("INFO: Log into communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic");
		topic.create(fUI);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log in as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);		
		
		log.info("INFO: Select the Forum topic created by UserA: " + testUser2.getDisplayName());
		commUI.clickLinkWait(ForumsUI.selectForumTopic(topic));

		log.info("INFO: Reply to the Forum topic");
		fUI.replyToTopic(topic);
				
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserA Likes A Forum Topic</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds a forum topic</li>
	 *<li><B>Step:</B> UserA 'Likes' the forum topic</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userALikesCommForumTopic(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                .access(Access.MODERATED)
                                                .description("GDPR data pop: UserA 'likes' their own forum topic")
                                                .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic");
		topic.create(fUI);
		
		log.info("INFO: 'Like' the forum topic");
		commUI.clickLinkWait(ForumsUIConstants.LikeLink);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserA Likes A Forum Topic Created By UserB</li>
	 *<li><B>Step:</B> UserB creates a community via API</li>
	 *<li><B>Step:</B> UserB adds a forum topic</li>
	 *<li><B>Step:</B> UserA 'Likes' the forum topic</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userALikesCommForumTopicCreatedByUserB(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                               .access(Access.PUBLIC)
                                               .description("GDPR data pop: UserA 'likes' a forum topic created by UserB")
                                               .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
	
		log.info("INFO: Log into communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic");
		topic.create(fUI);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log into communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Open the forum topic");
		commUI.clickLinkWait("link=" + topic.getTitle());
		
		log.info("INFO: 'Like' the forum topic");
		commUI.clickLinkWait(ForumsUIConstants.LikeLink);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserA Creates a Topic as a Question</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA creates a forum topic as a question</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userACreatesCommTopicAsQuestion(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                              .access(Access.PUBLIC)
                                              .description("GDPR data pop: UserA creates a topic & marks as a question")
                                              .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .markAsQuestion(true)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic - mark as a question");
		topic.create(fUI);
				
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserB Creates Community & UserA Creates a Topic as a Question</li>
	 *<li><B>Step:</B> UserB creates a community with UserA as a member via API</li>
	 *<li><B>Step:</B> UserA creates a forum topic as a question</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userBCreatesCommUserACreatesTopicAsQuestion(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                             .access(Access.PUBLIC)
                                             .description("GDPR data pop: UserB creates a community & UserA creates a topic & marks as a question")
                                             .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                             .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .markAsQuestion(true)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
	
		log.info("INFO: Log into communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic - mark as a question");
		topic.create(fUI);
						
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Forum - UserA Accepts UserB's Reply to a Topic as the Answer</li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds a forum topic & marks as a question</li>
	 *<li><B>Step:</B> UserB replies to the forum topic</li>
	 *<li><B>Step:</B> UserA accepts UserB's reply as the answer to the question</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud" }, enabled=false)
	public void userAMarksUserBReplyToCommTopicAsAnswerToTopicQuestion(){	
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                .addMember(new Member(CommunityRole.MEMBERS, testUser2))
                                                .access(Access.MODERATED)
                                                .description("GDPR data pop: UserA accepts UserB's reply as answer to topic question")
                                                .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("GDPR: community forum topic " + testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .markAsQuestion(true)
										   		 .build();
				
		log.info("INFO: Create a Community using the API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Create a Forum topic & mark as a question");
		topic.create(fUI);
		
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log in as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);		
		
		log.info("INFO: Select the Forum topic created by UserA: " + testUser1.getDisplayName());
		commUI.clickLinkWait(ForumsUI.selectForumTopic(topic));

		log.info("INFO: Reply to the Forum topic");
		fUI.replyToTopic(topic);
		
		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log back in as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);		
		
		log.info("INFO: Select the Forum topic created by UserA: " + testUser1.getDisplayName());
		commUI.clickLinkWait(ForumsUI.selectForumTopic(topic));
		
		log.info("INFO: Click on the Accept this Answer link from UserB's reply");
		fUI.clickLinkWait(ForumsUIConstants.AcceptAsAnswer);
		
		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		fUI.logout();
		fUI.close(cfg);
		
		commUI.endTest();
		
	}
	
	
	/**	
	 * <ul>
	 * <li><B>Info:</B>Data Population - Community Forums: UserB Follows Forum Created by UserA</li>
	 * <li><B>Step:</B>UserA creates a Public community with UserB as a member using the API</li>
	 * <li><B>Step:</B>UserA creates a Forum using the API</li>	
	 * <li><B>Step:</B>UserB logs into Communities</li>	 
	 * <li><B>Step:</B>UserB navigates to the forum's homepage</li>	 
	 * <li><B>Step:</B>UserB clicks on the Following Actions button</li>
	 * <li><B>Step:</B>UserB clicks on the Follow this Forum link</li>
	 * </ul>		  
	 */
	@Test(groups={"regression", "regressioncloud"}, enabled=false)
	public void userBFollowsCommForumCreatedByUserA(){
		
		String testName = fUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))	
													.description("GDPR data pop: UserB follows community forum created by UserA")
													.build();
		
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("use API to get the default community forum");
		String commUUID = apiCommOwner1.getCommunityUUID(comAPI);		
		Forum apiForum = apiForumsOwner1.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
	
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		fUI.loadComponent(Data.getData().ComponentCommunities);
		fUI.login(testUser2);
		
		log.info("INFO: Open the community");
		community.navViaUUID(commUI);
		
		log.info("INFO: Follow the community forum");
		followCommunityForum(apiForum);
				
		log.info("INFO: Log out as UserB: " + testUser2.getDisplayName());
		fUI.logout();
		fUI.close(cfg);
				
		fUI.endTest();
		
	}
	
	/**	
	 * <ul>
	 * <li><B>Info:</B>Data Population - Community Forums: UserA Follows Forum Created by UserB</li>
	 * <li><B>Step:</B>UserB creates a Public community with UserB as a member using the API</li>
	 * <li><B>Step:</B>UserB creates a Forum using the API</li>	
	 * <li><B>Step:</B>UserA logs into Communities</li>	 
	 * <li><B>Step:</B>UserA navigates to the forum's homepage</li>	 
	 * <li><B>Step:</B>UserA clicks on the Following Actions button</li>
	 * <li><B>Step:</B>UserA clicks on the Follow this Forum link</li>
	 * </ul>		  
	 */
	@Test(groups={"regression", "regressioncloud"}, enabled=false)
	public void userAFollowsCommForumCreatedByUserB(){
		
		String testName = fUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.addMember(new Member(CommunityRole.MEMBERS, testUser1))
													.description("GDPR data pop: UserA follows a community forum created by UserB")													
													.build();
		
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("use API to get the default community forum");
		String commUUID = apiCommOwner2.getCommunityUUID(comAPI);		
		Forum apiForum = apiForumsOwner2.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());
	
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		fUI.loadComponent(Data.getData().ComponentCommunities);
		fUI.login(testUser1);
		
		log.info("INFO: Open the community");
		community.navViaUUID(commUI);
		
		log.info("INFO: Follow the community forum");
		followCommunityForum(apiForum);
				
		log.info("INFO: Log out as UserA: " + testUser1.getDisplayName());
		fUI.logout();
		fUI.close(cfg);
				
		fUI.endTest();
		
	}
	
	
	
	
	/**
	* The followStandaloneForum method will: 
	* - click on the Public Forums view
	* - click on the Forum to be followed
	* - click the Follow Actions menu link
	* - click Follow this Forum
	* - verify the follow confirmation message displays
	* 
	* @param forum - forum to be followed
	*/	
	private void followStandaloneForum(BaseForum forum){
		log.info("INFO: Click on the Public Forums view link");
		fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);

		log.info("INFO: Click on the Forum link");
		String forumLink = "link=" + forum.getName();
		fUI.fluentWaitPresentWithRefresh(forumLink);
		fUI.clickLinkWait(forumLink);

		log.info("INFO: Click on the Follow Actions menu link");
		fUI.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);

		log.info("INFO: Click on the Follow this Forum link");
		fUI.clickLinkWait(ForumsUIConstants.Start_Following_Forum);

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
	* 
	* @param apiForum - forum to be followed
	*/		
	private void followCommunityForum(Forum apiForum) {
		
		log.info("INFO: Click on the Forums tab on the navigation menu");
		Community_TabbedNav_Menu.FORUMS.select(commUI);
		
		log.info("INFO: Click on the Forums view tab to display list of Forums");
		fUI.clickLinkWait(ForumsUIConstants.communityForumsTab);
		
		log.info("INFO: Click on the Forum to be followed");
		fUI.clickLinkWait("link=" + apiForum.getTitle());
		
		log.info("INFO: Click on the Follow Actions menu link");
		fUI.clickLinkWait(ForumsUIConstants.Forum_Follow_Actions);
		
		log.info("INFO: Click on the Follow this Forum link");
		fUI.clickLinkWait(ForumsUIConstants.Start_Following_Forum);
		
		log.info("INFO: Verify the following confirmation message: " + ForumsUIConstants.Forum_Following_Message + " displays.");
		Assert.assertTrue(driver.isTextPresent(ForumsUIConstants.Forum_Following_Message),
				"ERROR: The confirmation message does not appear");
		
	}
	
	/**
	* The addOwnerToForum method will: 
	* - click Add Owners link
	* - enter the name of user to be made an owner 
	* - select the user from typeahead results
	* - click the OK button
	* 
	* @param testUser - user to be added as an Owner
	*/		
	
	private void addOwnerToForum(User testUser){
		log.info("INFO: Adding " + testUser.getDisplayName() + " as an owner");
		fUI.clickLinkWait(ForumsUIConstants.AddOwners);
		fUI.fluentWaitElementVisible(ForumsUIConstants.ForumsAddOwnersInput);
		
		fUI.typeText(ForumsUIConstants.ForumsAddOwnersInput, testUser.getLastName());
		fUI.fluentWaitElementVisible(ForumsUIConstants.MemberTable);
		
		if(driver.isElementPresent(ForumsUIConstants.MemberSearchDir)) {
			fUI.clickLinkWait(ForumsUIConstants.MemberSearchDir);
		}
		
		fUI.clickLinkWait(ForumsUIConstants.MemberNames + ":contains(" + testUser.getEmail() + ")");
		fUI.fluentWaitPresent("css=span.lotusPerson[role='button']:contains(" + testUser.getDisplayName() + ")");
		fUI.clickLinkWait(ForumsUIConstants.MemberOkButton);
		Assert.assertTrue(fUI.isElementPresent("css=span.vcard > a:contains(" + testUser.getDisplayName() + ")"),
				"could not find element containing text [" + testUser.getDisplayName() + "]");
					
	}
	

	/**
	* createForumTopic (for standalone forums) will: 
	* - click on the Public Forums tab
	* - click on the forum entry
	* - create a forum topic
	* 
	* @param apiforum - forum to add topic to
	* @param topic - topic to be created
	*/		
			
	private void createForumTopic(BaseForum apiforum, BaseForumTopic topic){
		log.info("INFO: Navigate to the 'Public Forums' view");
		fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
		
		log.info("INFO: Click on the Forum");
		fUI.clickLinkWait("link=" + apiforum.getName());
	
		log.info("INFO: Create a Forum topic");
		topic.create(fUI);
		
	}
	

	/**
	* editForumTopic (for standalone forums) will: 
	* - click on the edit link
	* - edit the topic & save
	* - verify update title appears
	* 
	* @param newForumTopic - new topic content
	*/		
	
	private void editForumTopic(BaseForumTopic newForumTopic){
		
		log.info("INFO: Click on the forum topic Edit link");
		fUI.clickLinkWait(ForumsUIConstants.EditTopic);
		
		log.info("INFO: Edit forum topic title");
		fUI.clearText(ForumsUIConstants.Start_A_Topic_InputText_Title);
		fUI.typeText(ForumsUIConstants.Start_A_Topic_InputText_Title, newForumTopic.getTitle());

		log.info("INFO: Edit forum topic contents");
		fUI.typeInCkEditor(newForumTopic.getDescription());

		log.info("INFO: Save the forum topic changes");
		fUI.clickSaveButton();	

		log.info("INFO: Verify the updated forum topic title exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicTitle + " by UserA"),
				"ERROR: The Topic title " + Data.getData().EditForumTopicTitle + " does not appear.");
		
	
	}
	
	/**
	 * selectForumTopic (for standalone forums) will:
	 * - click on Public Forums view link
	 * - select the forum
	 * - select the forum topic
	 * 
	 * @param apiforum - forum to select
	 * @param topic - topic to select
	 */
	
	private void selectForumTopic(BaseForum apiforum, BaseForumTopic topic){
		
		log.info("INFO: Click on the 'Public Forums' view");
		fUI.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
				
		log.info("INFO: Click on the Forum");
		fUI.clickLinkWait("link=" + apiforum.getName());
		
		log.info("INFO: Click on the topic");
		fUI.clickLinkWait("link=" + topic.getTitle());
	}
	
	/**
	 * editCommunityForumTopic will: 
	 *  - select the topic
	 *  - click Edit link 
	 *  - edit the topic & save changes
	 *  - verify updated title appears
	 *  
	 *  @param apiForumTopic - topic to be edited
	 *  @param newForumTopic - new topic content
	 */
	
	private void editCommunityForumTopic(BaseForumTopic apiForumTopic, BaseForumTopic newForumTopic) {
	log.info("INFO: Open the forum topic");
	commUI.clickLinkWait("link=" + apiForumTopic.getTitle());

	log.info("INFO: Start to edit the topic");
	commUI.clickLinkWait(ForumsUIConstants.EditTopic);

	log.info("INFO: Edit forum topic title");
	commUI.clearText(ForumsUIConstants.Start_A_Topic_InputText_Title);
	commUI.typeText(ForumsUIConstants.Start_A_Topic_InputText_Title, newForumTopic.getTitle());

	log.info("INFO: Edit forum topic contents");
	commUI.typeInCkEditor(newForumTopic.getDescription());

	log.info("INFO: Save the forum topic changes");
	commUI.clickSaveButton();
	
	log.info("INFO: Verify the updated forum topic title exists");
	Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicTitle + " by UserA"),
			"ERROR: The Topic title " + Data.getData().EditForumTopicTitle + " does not appear.");
	
}
}
