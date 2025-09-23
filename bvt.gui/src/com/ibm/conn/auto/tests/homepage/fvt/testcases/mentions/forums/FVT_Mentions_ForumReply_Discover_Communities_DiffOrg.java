package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.HomepageUI;

/**
 * @author Patrick Doherty
 */


public class FVT_Mentions_ForumReply_Discover_Communities_DiffOrg extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_Mentions_ForumReply_Discover_Communities_DiffOrg.class);

	private HomepageUI ui;
	private ForumsUI uiFo;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity communityPub, communityMod, communityPriv;
	private String topicReply = "";
	private String serverURL = "";
	private String testName = "";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiFo = ForumsUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		testUser3 = cfg.getUserAllocator().getUser(this);

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
	}
	
	/**
	* replyMention_forumTopic_discover_publicCommunity_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 who is in a DIFFERENT organisation log into Homepage / Updates / Discover / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B6EE30732DE1556885257C84005FED32">TTT - DISCOVER - FORUMS - 00181 - FORUMS REPLY WITH MENTIONS - PUBLIC COMMUNITY FORUM - DIFFERENT ORGANIZATION</a></li>
	*</ul>
	*/
	@Test(groups = {"level3", "fvtcloud"})
	public void replyMention_forumTopic_discover_publicCommunity_diffOrg() throws Exception{
		
		topicReply = Data.getData().StatusComment + Helper.genDateBasedRandVal();
		
		testName = ui.startTest();
		
		communityPub = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
											.access(Access.PUBLIC)
											.tags("testTags"+ Helper.genDateBasedRand())
											.description("Test description for testcase " + testName)
											.build();

		//API code for creating a community	
		communityPub.createAPI(apiOwner);
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal3())
					   		 							.tags(Data.getData().commonTag)
					   		 							.description(Data.getData().commonDescription).build();
		
		/*
		 * Login testUser1 who will add a forum topic reply
		 * containing an @mentions to testUser3
		 */
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//If the wiki name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + communityPub.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		ui.clickLinkWait("link=" + communityPub.getName());
		ui.waitForPageLoaded(driver);
		
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		ui.waitForPageLoaded(driver);
		
		//Create a new topic inside the Forum
		log.info("INFO: Create a new topic");
		topic.create(uiFo);
		
		// Reply to topic
		log.info("INFO: Click top Reply to Topic button");
		ui.clickLink(ForumsUIConstants.Reply_to_topic);;

		//Validate that the ckeditor has loaded by switching to the frame and checking 
		//if body webelement contenteditable='true' before proceeding	
		ui.switchToFrame(ForumsUIConstants.forumReplyToCkEditor_frame, ForumsUIConstants.forumReplyToCkEditor_body);
		ui.switchToTopFrame();
		
		// Type text using native keystrokes (typing will occur in focused window
		log.info("INFO: Type text into reply");
		ui.typeNativeInCkEditor(topicReply + " @" + testUser3.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		driver.getSingleElement(HomepageUIConstants.typeAheadBox + " li[role='option']:contains("+testUser3.getDisplayName() + " "+")").click();
				
		// Save form
		log.info("INFO: Select the save button");
		ui.clickLinkWait(ForumsUIConstants.Save_Topic_Reply);
		
		ui.fluentWaitTextPresent("Re: " + topic.getTitle());
		
		ui.logout();
		/*
		 * Login testUser2 who will go to the "Discover" view
		 * on Homepage and verify that the @mentions to testUser3
		 * does NOT appear in the "All", Communities and "Forums" filters
		 */
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.login(testUser2);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the topic reply is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.filterBy(HomepageUIConstants.CommunitiesIFollow);

		log.info("INFO: Verify the wiki comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the wiki comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.endTest();
	}
	
	/**
	* replyMention_forumTopic_discover_modCommunity_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 who is in a DIFFERENT organisation log into Homepage / Updates / Discover / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B29F36C20EE5A12B85257C84005FED33">TTT - DISCOVER - FORUMS - 00182 - FORUMS REPLY WITH MENTIONS - MODERATE COMMUNITY FORUM - DIFFERENT ORGANIZATION</a></li>
	*</ul>
	*/
	@Test(groups = {"level3", "fvtcloud"})
	public void replyMention_forumTopic_discover_modCommunity_diffOrg() throws Exception{
		
		topicReply = Data.getData().StatusComment + Helper.genDateBasedRandVal();
		
		testName = ui.startTest();
		
		communityMod = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
											.access(Access.MODERATED)
											.tags("testTags"+ Helper.genDateBasedRand())
											.description("Test description for testcase " + testName)
											.build();

		//API code for creating a community	
		communityMod.createAPI(apiOwner);
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal3())
					   		 							.tags(Data.getData().commonTag)
					   		 							.description(Data.getData().commonDescription).build();
		
		/*
		 * Login testUser1 who will add a forum topic reply
		 * containing an @mentions to testUser3
		 */
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//If the wiki name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + communityMod.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		ui.clickLinkWait("link=" + communityMod.getName());
		ui.waitForPageLoaded(driver);
		
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		ui.waitForPageLoaded(driver);
		
		//Create a new topic inside the Forum
		log.info("INFO: Create a new topic");
		topic.create(uiFo);
		
		// Reply to topic
		log.info("INFO: Click top Reply to Topic button");
		ui.clickLink(ForumsUIConstants.Reply_to_topic);;

		//Validate that the ckeditor has loaded by switching to the frame and checking 
		//if body webelement contenteditable='true' before proceeding	
		ui.switchToFrame(ForumsUIConstants.forumReplyToCkEditor_frame, ForumsUIConstants.forumReplyToCkEditor_body);
		ui.switchToTopFrame();
		
		// Type text using native keystrokes (typing will occur in focused window
		log.info("INFO: Type text into reply");
		ui.typeNativeInCkEditor(topicReply + " @" + testUser3.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		driver.getSingleElement(HomepageUIConstants.typeAheadBox + " li[role='option']:contains("+testUser3.getDisplayName() + " "+")").click();
				
		// Save form
		log.info("INFO: Select the save button");
		ui.clickLinkWait(ForumsUIConstants.Save_Topic_Reply);
		
		ui.fluentWaitTextPresent("Re: " + topic.getTitle());
		
		ui.logout();
		/*
		 * Login testUser2 who will go to the "Discover" view
		 * on Homepage and verify that the @mentions to testUser3
		 * does NOT appear in the "All", Communities and "Forums" filters
		 */
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.login(testUser2);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the topic reply is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.filterBy(HomepageUIConstants.CommunitiesIFollow);

		log.info("INFO: Verify the wiki comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the wiki comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.endTest();
	}

	/**
	* replyMention_forumTopic_discover_privateCommunity_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 who is in a DIFFERENT organisation log into Homepage / Updates / Discover / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5270E0CC1843159685257C84005FED34">TTT - DISCOVER - FORUMS - 00183 - FORUMS REPLY WITH MENTIONS - PRIVATE COMMUNITY FORUM - DIFFERENT ORGANIZATION</a></li>
	*</ul>
	*/
	@Test(groups = {"level3", "fvtcloud"})
	public void replyMention_forumTopic_discover_privateCommunity_diffOrg() throws Exception{
		
		topicReply = Data.getData().StatusComment + Helper.genDateBasedRandVal();
		
		testName = ui.startTest();
		
		communityPriv = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
											.access(Access.RESTRICTED)
											.tags("testTags"+ Helper.genDateBasedRand())
											.description("Test description for testcase " + testName)
											.build();

		//API code for creating a community	
		communityPriv.createAPI(apiOwner);
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal3())
					   		 							.tags(Data.getData().commonTag)
					   		 							.description(Data.getData().commonDescription).build();
		
		/*
		 * Login testUser1 who will add a forum topic reply
		 * containing an @mentions to testUser3
		 */
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//If the wiki name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + communityPriv.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		ui.clickLinkWait("link=" + communityPriv.getName());
		ui.waitForPageLoaded(driver);
		
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		ui.waitForPageLoaded(driver);
		
		//Create a new topic inside the Forum
		log.info("INFO: Create a new topic");
		topic.create(uiFo);
		
		// Reply to topic
		log.info("INFO: Click top Reply to Topic button");
		ui.clickLink(ForumsUIConstants.Reply_to_topic);;

		//Validate that the ckeditor has loaded by switching to the frame and checking 
		//if body webelement contenteditable='true' before proceeding	
		ui.switchToFrame(ForumsUIConstants.forumReplyToCkEditor_frame, ForumsUIConstants.forumReplyToCkEditor_body);
		ui.switchToTopFrame();
		
		// Type text using native keystrokes (typing will occur in focused window
		log.info("INFO: Type text into reply");
		ui.typeNativeInCkEditor(topicReply + " @" + testUser3.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		driver.getSingleElement(HomepageUIConstants.typeAheadBox + " li[role='option']:contains("+testUser3.getDisplayName() + " "+")").click();
				
		// Save form
		log.info("INFO: Select the save button");
		ui.clickLinkWait(ForumsUIConstants.Save_Topic_Reply);
		
		ui.fluentWaitTextPresent("Re: " + topic.getTitle());
		
		ui.logout();
		/*
		 * Login testUser2 who will go to the "Discover" view
		 * on Homepage and verify that the @mentions to testUser3
		 * does NOT appear in the "All", Communities and "Forums" filters
		 */
		ui.loadComponent(Data.getData().HomepageDiscover, true);
		ui.login(testUser2);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the topic reply is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.filterBy(HomepageUIConstants.CommunitiesIFollow);

		log.info("INFO: Verify the wiki comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.filterBy(HomepageUIConstants.FilterSU);

		log.info("INFO: Verify the wiki comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(topicReply),
						 "Topic reply is displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + "@" + testUser3.getDisplayName()),
						 "@mentions is displayed");
		
		ui.endTest();
	}
	
}
