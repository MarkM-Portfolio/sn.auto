package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
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
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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
/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_ForumEvents_NoVisitor extends SetUpMethods2{
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private APIForumsHandler forumsAPIUser1;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	private HomepageUI ui;	
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		forumsAPIUser1 = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);			
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		// Remove all of the communities created during the tests
		Set<Community> setOfCommunities = communitiesForDeletion.keySet();
		
		for(Community community : setOfCommunities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_forumAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a forum to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/435D8B5CB7DCD59085257C8A0040961D">TTT - VISITORS - ACTIVITY STREAM - 00064 - SHARED EXTERNALLY HEADER - FORUM EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_forumAdded() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a private community with User 3 added as a member and follower	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollower(baseCommunity, testUser3, communitiesAPIUser3, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(restrictedCommunity, communitiesAPIUser1);
		
		// User 1 will now create a forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(restrictedCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// Log in as User 3 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the elements to be verified
		String forumCreatedEvent = ui.replaceNewsStory(Data.CREATE_FORUM, baseForum.getName(), null, testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", forumCreatedEvent);
		String messageCSSSelctor = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", forumCreatedEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the forum created event and forum description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{forumCreatedEvent, baseForum.getDescription().trim()}, filter, true);
			
			// Verify that the 'Shared externally' icon and message are NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelctor}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_forumTopic() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a forum to this community</B></li>
	*<li><B>Step: testUser1 add a forum topic</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Forums - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/435D8B5CB7DCD59085257C8A0040961D">TTT - VISITORS - ACTIVITY STREAM - 00064 - SHARED EXTERNALLY HEADER - FORUM EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_forumTopic() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a private community with User 3 added as a member and follower	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollower(baseCommunity, testUser3, communitiesAPIUser3, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(restrictedCommunity, communitiesAPIUser1);
		
		// User 1 will now create a forum topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, restrictedCommunity, baseForumTopic);
		
		// Log in as User 3 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the elements to be verified
		String topicCreatedEvent = ui.replaceNewsStory(Data.CREATE_TOPIC, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", topicCreatedEvent);
		String messageCSSSelctor = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", topicCreatedEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the forum topic created event and topic description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{topicCreatedEvent, baseForumTopic.getDescription().trim()}, filter, true);
			
			// Verify that the 'Shared externally' icon and message are NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelctor}, null, false);
		}
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_forumTopicReply() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a forum to this community</B></li>
	*<li><B>Step: testUser1 add a forum topic</B></li>
	*<li><B>Step: testUser1 reply to the topic</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Forums - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appear beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/435D8B5CB7DCD59085257C8A0040961D">TTT - VISITORS - ACTIVITY STREAM - 00064 - SHARED EXTERNALLY HEADER - FORUM EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_forumTopicReply() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a private community with User 3 added as a member and follower	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollower(baseCommunity, testUser3, communitiesAPIUser3, testUser1, communitiesAPIUser1);
		communitiesForDeletion.put(restrictedCommunity, communitiesAPIUser1);
		
		// User 1 will now create a forum topic and will reply to that topic
		String forumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, restrictedCommunity, baseForumTopic, forumsAPIUser1, forumTopicReply);
		
		// Log in as User 3 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the elements to be verified
		String topicReplyEvent = ui.replaceNewsStory(Data.CREATE_THEIR_OWN_REPLY, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", topicReplyEvent);
		String messageCSSSelctor = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", topicReplyEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the forum topic reply event, topic description and topic reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{topicReplyEvent, baseForumTopic.getDescription().trim(), forumTopicReply}, filter, true);
			
			// Verify that the 'Shared externally' icon and message are NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelctor}, null, false);
		}
		ui.endTest();
	}
}