package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums.ee;

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
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_EE_ForumTopic_Reply_Communities extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_Mentions_EE_ForumTopic_Reply_Communities.class);

	private HomepageUI ui;
	private ForumsUI uiFo;
	private TestConfigCustom cfg;
	private BaseCommunity baseCom;
	private User testUser1, testUser2;
	private String eeComment = "";
	private String serverURL = "";
	private String testName = "";
	
	private APICommunitiesHandler commsAPI;
									   
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiFo = ForumsUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		commsAPI = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		
	}

	/**
	* mentions_ee_forumTopicLiked_differentOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a topic liked</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a comment and start to add an @mentions to a user in a different org</B></li>
	*<li><B>Verify: Verify the mentions cannot be added for a user in a different org</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9412337EE47190EB85257CA700396FA4">TTT - @Mentions - EE - Forum Reply - 00021 - User cannot add an @mentions to a user in a different org - SC only</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void mentions_ee_forumTopicLiked_differentOrg() throws Exception{
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(baseCom)
		  											  .build();
		
		log.info( "Creating Community");
		Community newCommunity = baseCom.createAPI(commsAPI);

		log.info("INFO: Adding testUser2 (" + testUser2.getDisplayName() + ") to community");
		commsAPI.addMemberToCommunity(testUser2, newCommunity, StringConstants.Role.MEMBER);
			
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}

		log.info("INFO: Navigate to the Forum");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		
		log.info("INFO: Start a topic");
		ui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic_Community);
		uiFo.createTopic(forumTopic);
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		
		//Like topic reply
		
		//Log into Homepage Activity Stream
		driver.navigate().to(Data.getData().HomepageImFollowing);
		
		//Go to the event of a file uploaded and open the EE for the story
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(eeComment), HomepageUIConstants.URLPreview);
		
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		ui.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		//Add a comment and start to add an @mentions to a user in a different org
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			ui.typeText(HomepageUIConstants.EEMentionsCommentField, eeComment + " @" + testUser2.getDisplayName());
		else
			ui.typeText(HomepageUIConstants.EECommentField, eeComment + " @" + testUser2.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		ui.clickLinkWait(HomepageUIConstants.EECommentPost);
		ui.fluentWaitTextPresent(eeComment);
		

		//Verify the mentions cannot be added for a user in a different org
		log.info("INFO: Verify the mentions is NOT displayed in the EE");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present in the EE");
				
		ui.endTest();
	}
	
	/**
	* mentions_ee_forumTopicLiked_guestUser() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a reply liked on a forum topic</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a comment and start to add an @mentions to a guest user</B></li>
	*<li><B>Verify: Verify the mentions cannot be added for a guest user</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DE8E84573ED708A585257CA700396FA5">TTT - @Mentions - EE - Forum Reply - 00022 - User cannot add an @mentions to a guest user - SC only</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void mentions_ee_fileComment_guestUser() throws Exception{
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(baseCom)
		  											  .build();
		
		log.info( "Creating Community");
		Community newCommunity = baseCom.createAPI(commsAPI);

		log.info("INFO: Adding testUser2 (" + testUser2.getDisplayName() + ") to community");
		commsAPI.addMemberToCommunity(testUser2, newCommunity, StringConstants.Role.MEMBER);
			
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}

		log.info("INFO: Navigate to the Forum");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		
		log.info("INFO: Start a topic");
		ui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic_Community);
		uiFo.createTopic(forumTopic);
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		
		//Like topic reply
		
		//Log into Homepage Activity Stream
		driver.navigate().to(Data.getData().HomepageImFollowing);
		
		//Go to the event of a file uploaded and open the EE for the story
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(eeComment), HomepageUIConstants.URLPreview);
		
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		ui.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		//Add a comment and start to add an @mentions to a guest user
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			ui.typeText(HomepageUIConstants.EEMentionsCommentField, eeComment + " @" + testUser2.getDisplayName());
		else
			ui.typeText(HomepageUIConstants.EECommentField, eeComment + " @" + testUser2.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		ui.clickLinkWait(HomepageUIConstants.EECommentPost);
		ui.fluentWaitTextPresent(eeComment);
		
		//Verify the mentions cannot be added for a guest user
		log.info("INFO: Verify the mentions is NOT displayed in the EE");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present in the EE");
				
		ui.endTest();
	}

	/**
	* mentions_ee_forumTopic_privateCommunity_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into a private community ensure User 2 is NOT a member</B></li>
	*<li><B>Step: Create a forum topic</B></li>
	*<li><B>Step: Go to Homepage Activity Stream</B></li>
	*<li><B>Step: Go to the event of the topic</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start to add a reply to the entry with and add User 2 as a mentions</B></li>
	*<li><B>Step: User 2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify the '@' is dropped and a message appears saying the user will not get the event</B></li>
	*<li><B>Verify: Verify the mentions event is NOT there</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/13B3270EDD6E86BA85257CA70041289D">TTT - @Mentions - EE - Forum Reply - 00015 - Adding a mentions in a private community - Non Member</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_ee_forumTopic_privateCommunity_nonMember() throws Exception{
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(baseCom)
		  											  .build();
		
		log.info( "Creating Community");
		baseCom.createAPI(commsAPI);
	
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}

		log.info("INFO: Navigate to the Forum");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		
		log.info("INFO: Start a topic");
		ui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic_Community);
		uiFo.createTopic(forumTopic);
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		
		//Log into Homepage Activity Stream
		driver.navigate().to(Data.getData().HomepageImFollowing);
		
		//Go to the event of a forum topic created and open the EE for the story
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(eeComment), HomepageUIConstants.URLPreview);
		
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		ui.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		//Add a comment and start to add an @mentions to a user in a different org
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			ui.typeText(HomepageUIConstants.EEMentionsCommentField, eeComment + " @" + testUser2.getDisplayName());
		else
			ui.typeText(HomepageUIConstants.EECommentField, eeComment + " @" + testUser2.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		ui.clickLinkWait(HomepageUIConstants.EECommentPost);
		ui.fluentWaitTextPresent(eeComment);
		
		//Verify the mentions cannot be added for a user who is NOT a member of the community
		log.info("INFO: Verify the warning message is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Warning message is NOT present in the EE");
				
		//Verify the mentions cannot be added for a user who is NOT a member of the community
		log.info("INFO: Verify the mentions is NOT displayed in the EE");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present in the EE");
		
		ui.logout();
		
		//Load the component
		ui.loadComponent(Data.getData().HomepageMentions, true);
		ui.login(testUser2);
		
		//Verify the comment and mentions are NOT present
		log.info("INFO: Verify the comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(eeComment), 
						 "ERROR: Comment is present");
		
		log.info("INFO: Verify the mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present");
		
				
		ui.endTest();
	}

	/**
	* mentions_ee_forumTopic_privateCommunity_visitor() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into a community that allows visitors as members</B></li>
	*<li><B>Step: Add User 2 (who is a visitor) to the community</B></li>
	*<li><B>Step: Create a forum topic</B></li>
	*<li><B>Step: Go to Homepage Activity Stream</B></li>
	*<li><B>Step: Go to the event of the topic</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start to add a reply to the entry with and add User 2 as a mentions</B></li>
	*<li><B>Step: User 2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify the user can be added as a mentions</B></li>
	*<li><B>Verify: Verify the mentions event is there</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/72A0E072B00F263A85257CA70041289E">TTT - @Mentions - EE - Forum Reply - 00016 - Adding a mentions in a community - Visitors can be members - On Prem Only</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void mentions_ee_forumTopic_privateCommunity_visitor() throws Exception{
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(baseCom)
		  											  .build();
		
		log.info( "Creating Community");
		Community newCommunity = baseCom.createAPI(commsAPI);

		log.info("INFO: Adding testUser2 (" + testUser2.getDisplayName() + ") to community");
		commsAPI.addMemberToCommunity(testUser2, newCommunity, StringConstants.Role.MEMBER);
			
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}

		log.info("INFO: Navigate to the Forum");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		
		log.info("INFO: Start a topic");
		ui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic_Community);
		uiFo.createTopic(forumTopic);
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		
		//Log into Homepage Activity Stream
		driver.navigate().to(Data.getData().HomepageImFollowing);
		
		//Go to the event of a forum topic created and open the EE for the story
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(eeComment), HomepageUIConstants.URLPreview);
		
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		ui.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		//Add a comment and start to add an @mentions to a user in a different org
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			ui.typeText(HomepageUIConstants.EEMentionsCommentField, eeComment + " @" + testUser2.getDisplayName());
		else
			ui.typeText(HomepageUIConstants.EECommentField, eeComment + " @" + testUser2.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		ui.clickLinkWait(HomepageUIConstants.EECommentPost);
		ui.fluentWaitTextPresent(eeComment);
		
		//Verify the mentions cannot be added for a user who is NOT a member of the community
		log.info("INFO: Verify the warning message is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Warning message is NOT present in the EE");
				
		//Verify the mentions cannot be added for a user who is NOT a member of the community
		log.info("INFO: Verify the mentions is NOT displayed in the EE");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present in the EE");
		
		ui.logout();
		
		//Load the component
		ui.loadComponent(Data.getData().HomepageMentions, true);
		ui.login(testUser2);
		
		//Verify the comment and mentions are present
		log.info("INFO: Verify the comment is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(eeComment), 
						 "ERROR: Comment is NOT present");
		
		log.info("INFO: Verify the mentions is displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is NOT present");
				
		ui.endTest();
	}


	/**
	* mentions_ee_forumTopic_publicCommunity_visitor() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into a community that does NOT allow visitors as members</B></li>
	*<li><B>Step: Create a forum topic</B></li>
	*<li><B>Step: Go to Homepage Activity Stream</B></li>
	*<li><B>Step: Go to the event of the topic</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start to add a reply to the entry with and add User 2(visitor) as a mentions</B></li>
	*<li><B>Step: User 2 go to Homepage / Mentions</B></li>
	*<li><B>Verify: Verify the '@' is dropped and a message appears</B></li>
	*<li><B>Verify: Verify the mentions event is NOT there</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9CDC53C9B624940785257CA70041289F">TTT - @Mentions - EE - Forum Reply - 00017 - Adding a mentions in a community - Visitors cant be members - On Prem Only</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void mentions_ee_forumTopic_publicCommunity_visitor() throws Exception{
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + Helper.genDateBasedRand())
		  											  .tags(Data.getData().ForumTopicTag)
		  											  .description(Data.getData().commonDescription)
		  											  .partOfCommunity(baseCom)
		  											  .build();
		
		log.info( "Creating Community");
		baseCom.createAPI(commsAPI);
	
		//Load the component
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}

		log.info("INFO: Navigate to the Forum");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);
		
		log.info("INFO: Start a topic");
		ui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic_Community);
		uiFo.createTopic(forumTopic);
		ui.fluentWaitPresent(ForumsUIConstants.Reply_to_topic);
		
		//Log into Homepage Activity Stream
		driver.navigate().to(Data.getData().HomepageImFollowing);
		
		//Go to the event of a forum topic created and open the EE for the story
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(eeComment), HomepageUIConstants.URLPreview);
		
		//Switch to the EE frame
		log.info("INFO: Switch to the Embedded Experience");
		ui.waitForAndSwitchToEEFrame(HomepageUIConstants.EEPopupFrameIdentifer, HomepageUIConstants.EECommentsTab, 3);
		
		//Add a comment and start to add an @mentions to a user in a different org
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		if(driver.isElementPresent(HomepageUIConstants.EEMentionsCommentField))
			ui.typeText(HomepageUIConstants.EEMentionsCommentField, eeComment + " @" + testUser2.getDisplayName());
		else
			ui.typeText(HomepageUIConstants.EECommentField, eeComment + " @" + testUser2.getDisplayName());
		
		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		ui.clickLinkWait(HomepageUI.getUserSelectAtMentionUser(testUser2.getDisplayName()));

		ui.clickLinkWait(HomepageUIConstants.EECommentPost);
		ui.fluentWaitTextPresent(eeComment);
		
		//Verify the mentions cannot be added for a user who is NOT a member of the community
		log.info("INFO: Verify the warning message is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Warning message is NOT present in the EE");
				
		//Verify the mentions cannot be added for a user who is NOT a member of the community
		log.info("INFO: Verify the mentions is NOT displayed in the EE");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present in the EE");
		
		ui.logout();
		
		//Load the component
		ui.loadComponent(Data.getData().HomepageMentions, true);
		ui.login(testUser2);
		
		//Verify the comment and mentions are NOT present
		log.info("INFO: Verify the comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(eeComment), 
						 "ERROR: Comment is present");
		
		log.info("INFO: Verify the mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=@" + testUser2.getDisplayName()), 
						 "ERROR: Mentions is present");
		
				
		ui.endTest();
	}
	
}
