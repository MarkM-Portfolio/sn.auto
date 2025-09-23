package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_ForumEvents_VisitorAdded extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_ForumEvents_VisitorAdded.class);

	private HomepageUI ui;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;	
	private APICommunitiesHandler apiOwner, apiFollower;
	private APIForumsHandler forumOwner;
	private String serverURL = "";	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);		
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL,testUser1.getAttribute(cfg.getLoginPreference()),testUser1.getPassword());
		apiFollower = new APICommunitiesHandler(serverURL,testUser3.getAttribute(cfg.getLoginPreference()),testUser3.getPassword());
		forumOwner = new APIForumsHandler(serverURL,testUser1.getAttribute(cfg.getLoginPreference()),testUser1.getPassword());	
	}
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);			
	}		

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_forumAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add add a forum to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/48F48323A602F98E85257C8A00409249">TTT - VISITORS - ACTIVITY STREAM - 00061 - SHARED EXTERNALLY HEADER - FORUM EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_forumAdded() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser3.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser2.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser2, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);			
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_FORUM, baseCommunity.getName(), null, testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelctor = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();	

		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, HomepageUIConstants.FilterForums, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory}, HomepageUIConstants.FilterForums, true);
		
		/*
		 * Checking in the EE to guarantee warning message and icon appear
		 * In Activity Stream these may be found if appearing in other stories
		 */
		log.info("INFO: Open the EE and verify that all elements are displayed");
		ui.filterNewsItemOpenEE(newsStory);

		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
	
		log.info("INFO: Delete the community clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_forumTopic() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a forum to this community</B></li>
	*<li><B>Step: testUser1 add a forum topic</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Forums - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/48F48323A602F98E85257C8A00409249">TTT - VISITORS - ACTIVITY STREAM - 00061 - SHARED EXTERNALLY HEADER - FORUM EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_forumTopic() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser3.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser2.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser2, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);	
		
		log.info("INFO: " + testUser1.getDisplayName() + " creating Forum Topic in Community");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);		
		ForumTopic topic = apiOwner.CreateForumTopic(newCommunity, baseForumTopic);
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_TOPIC, topic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelctor = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();	

		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseForumTopic.getDescription()}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseForumTopic.getDescription()}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, HomepageUIConstants.FilterForums, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseForumTopic.getDescription()}, HomepageUIConstants.FilterForums, true);
		
		/*
		 * Checking in the EE to guarantee warning message and icon appear
		 * In Activity Stream these may be found if appearing in other stories
		 */
		log.info("INFO: Open the EE and verify that all elements are displayed");
		ui.filterNewsItemOpenEE(newsStory);

		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
	
		log.info("INFO: Delete the community clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_visitorAdded_forumTopicReply() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a forum to this community</B></li>
	*<li><B>Step: testUser1 add a forum topic</B></li>
	*<li><B>Step: testUser1 reply to the topic</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Forums - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/48F48323A602F98E85257C8A00409249">TTT - VISITORS - ACTIVITY STREAM - 00061 - SHARED EXTERNALLY HEADER - FORUM EVENTS- PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_visitorAdded_forumTopicReply() {
		
		String testName = ui.startTest();
		
		// Creating the Public community with all setup steps completed.	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());	
		Community newCommunity = baseCommunity.createAPI(apiOwner);
		log.info("INFO: Set UUID of community");
		baseCommunity.setCommunityUUID(apiOwner.getCommunityUUID(newCommunity)); 
		
		log.info("INFO: " + testUser3.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser3, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser2.getDisplayName() + " Adding " + testUser1.getDisplayName() + " as a member to PrivateCommunity");
		apiOwner.addMemberToCommunity(testUser2, newCommunity, Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " follow the Community using API");		
		apiFollower.followCommunity(newCommunity);	
		
		log.info("INFO: " + testUser1.getDisplayName() + " creating Forum Topic in Community");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);		
		ForumTopic topic = apiOwner.CreateForumTopic(newCommunity, baseForumTopic);
		
		String topicReply = Helper.genStrongRand();
		
		log.info("INFO: " + testUser1.getDisplayName() + " creating response to topic (API)");
		forumOwner.createForumReply(topic, topicReply);
		
		//testUser3 goes to I'm Following view to verify that the "Shared Externally" warning is NOT displayed
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);			

		//Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_THEIR_OWN_REPLY, topic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelctor = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);
		
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();	

		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseForumTopic.getDescription(), topicReply}, HomepageUIConstants.FilterAll, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, null, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseForumTopic.getDescription(), topicReply}, HomepageUIConstants.FilterCommunities, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, null, true);
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseForumTopic.getDescription(), topicReply}, HomepageUIConstants.FilterForums, true);
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelctor, iconCSSSelector}, null, true);			
		
		/*
		 * Checking in the EE to guarantee warning message and icon appear
		 * In Activity Stream these may be found if appearing in other stories
		 */
		log.info("INFO: Open the EE and verify that all elements are displayed");
		ui.filterNewsItemOpenEE(newsStory);

		HomepageValid.verifyElementsInAS(ui, driver, new String[]{HomepageUIConstants.SharedExternally_Icon_EE, HomepageUIConstants.SharedExternally_Message_EE}, null, true);
				
		log.info("INFO: Delete the community clean up");		
		apiOwner.deleteCommunity(newCommunity);		
		ui.endTest();			
	}
}
