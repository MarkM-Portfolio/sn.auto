package com.ibm.conn.auto.tests.homepage.fvt.finalisation.news.mynotifications;

import java.util.ArrayList;

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
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	3rd October 2016
 */

public class FVT_ForumEvents_Rollup extends SetUpMethods2 {

private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4, communitiesAPIUser5;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser2, forumsAPIUser3, forumsAPIUser4, forumsAPIUser5;
	private BaseCommunity baseCommunity;
	private BaseForum baseForum;
	private boolean isOnPremise;
	private CommunitiesUI uiCo;
	private Community publicCommunity;
	private Forum communityForum;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 5 unique users are chosen from the CSV file
		ArrayList<User> listOfUsers = new ArrayList<User>();
		listOfUsers.add(cfg.getUserAllocator().getUser(this));
		
		do {
			User currentUser = cfg.getUserAllocator().getUser(this);
			int index = 0;
			boolean userAlreadyChosen = false;
			while(index < listOfUsers.size() && userAlreadyChosen == false) {
				if(listOfUsers.get(index).getDisplayName().equals(currentUser.getDisplayName())) {
					userAlreadyChosen = true;
				}
				index ++;
			}
			if(userAlreadyChosen == false) {
				listOfUsers.add(currentUser);
			}
		} while(listOfUsers.size() < 5);
		
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		testUser4 = listOfUsers.get(3);
		testUser5 = listOfUsers.get(4);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		communitiesAPIUser4 = new APICommunitiesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		communitiesAPIUser5 = new APICommunitiesHandler(serverURL, testUser5.getAttribute(cfg.getLoginPreference()), testUser5.getPassword());
		
		forumsAPIUser1 = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		forumsAPIUser2 = new APIForumsHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		forumsAPIUser3 = new APIForumsHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		forumsAPIUser4 = new APIForumsHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		forumsAPIUser5 = new APIForumsHandler(serverURL, testUser5.getAttribute(cfg.getLoginPreference()), testUser5.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a public community with all relevant members added
		User[] membersList = { testUser2, testUser3, testUser4, testUser5 };
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithMultipleMembers(baseCommunity, testUser1, communitiesAPIUser1, membersList);
		
		if(!isOnPremise) {
			// User 1 will now log in to the community - ensures the API calls will work consistently against G2, G3 etc.
			CommunityEvents.loginAndNavigateToCommunity(publicCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, false);
			
			// Log out as User 1
			LoginEvents.gotoHomeAndLogout(ui);
			
			// Close the current browser instance
			UIEvents.closeCurrentBrowserWindow(ui);
		}
		
		// User 1 will now add a forum to the community
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		communityForum = CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		if(!isOnPremise) {
			User[] usersToLogIn = { testUser2, testUser3, testUser4, testUser5 };
			APICommunitiesHandler[] apiUsersToLogIn = { communitiesAPIUser2, communitiesAPIUser3, communitiesAPIUser4, communitiesAPIUser5 };
			int index = 0;
			while(index < usersToLogIn.length) {
				// The User will now log in to the community forum - ensures the API calls will work consistently against G2, G3 etc.
				CommunityForumEvents.loginAndNavigateToCommunityForum(publicCommunity, baseCommunity, ui, uiCo, communityForum, usersToLogIn[index], apiUsersToLogIn[index], false);
				
				// Log out as the current User
				LoginEvents.gotoHomeAndLogout(ui);
				
				// Close the current browser instance - critical step to maintain reliability when executing these consistent logins against G2, G3 etc.
				UIEvents.closeCurrentBrowserWindow(ui);
				
				index ++;
			}
		}
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		CommunityEvents.deleteCommunity(publicCommunity, testUser1, communitiesAPIUser1);
	}

	/**
	* test_ForumTopic_Replies_Rollup() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>1: User 1 log into Communities</B></li>
	*<li><B>2: User 1 start a topic in the forum</B></li>
	*<li><b>3: User 2 follow the topic</b></li>
	*<li><B>4: User 2 log into Community forum</B></li>
	*<li><B>5: User 2 reply to the topic</B></li>
	*<li><B>6: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 1</B></li>
	*<li><B>7: User 3 reply to the topic</B></li>
	*<li><B>8: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 2</B></li>
	*<li><B>9: User 4 and User 5 reply to the topic</B></li>
	*<li><B>10: User 1 go to Homepage / My Notifications / All, Forums, Communities - verification point 3</B></li>
	*<li><B>Verify: Verify the event shows "{user 2} replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 3} and  {user 2} replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><B>Verify: Verify the event shows "{user 5} and 3 others replied to your {topicName} topic thread in the {forumName} forum."</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2F0E99CE14B04F8485257DEA003E5629">TTT - MY NOTIFICATIONS - FORUMS - 00010 - REPLY TO TOPIC ROLLS UP</a></li>
	*</ul>
	*/
	@Test(groups ={"fvt_final_onprem", "fvt_final_cloud"})
	public void test_ForumTopic_Replies_Rollup() {

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(testName + Helper.genStrongRand(), baseCommunity, communityForum);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 2 will now post a reply to the community forum topic
		String user2TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser2, forumsAPIUser2, forumTopic, user2TopicReply);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser2.getDisplayName());
		
		// Verify that the reply to forum topic event and User 2's topic reply are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2TopicReply}, TEST_FILTERS, true);
		
		// User 3 will now post a reply to the community forum topic
		String user3TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser3, forumsAPIUser3, forumTopic, user3TopicReply);
		
		if(!isOnPremise) {
			// Navigate to the I'm Following view
			UIEvents.gotoImFollowing(ui);
			
			// Re-load the My Notifications view - this will ensure that the My Notifications view is reset / updated consistently when testing against G2, G3 etc.
			UIEvents.gotoMyNotifications(ui);
		}
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName(), baseForumTopic.getTitle(), baseForum.getName());
		
		// Verify that the reply to forum topic event and User 2's and User 3's topic replies are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user2TopicReply, user3TopicReply}, TEST_FILTERS, true);
		
		// User 4 will now post a reply to the community forum topic
		String user4TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser4, forumsAPIUser4, forumTopic, user4TopicReply);
		
		// User 5 will now post a reply to the community forum topic
		String user5TopicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser5, forumsAPIUser5, forumTopic, user5TopicReply);
		
		if(!isOnPremise) {
			// Navigate to the I'm Following view
			UIEvents.gotoImFollowing(ui);
			
			// Re-load the My Notifications view - this will ensure that the My Notifications view is reset / updated consistently when testing against G2, G3 etc.
			UIEvents.gotoMyNotifications(ui);
		}
		// Create the news story to be verified
		replyToTopicEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_UserAndMany(ui, testUser5.getDisplayName(), "3", baseForumTopic.getTitle(), baseForum.getName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply to forum topic event and User 4's and User 5's topic replies are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), user4TopicReply, user5TopicReply}, filter, true);
			
			// Verify that User 2's and User 3's topic replies are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2TopicReply, user3TopicReply}, null, false);
		}
		ui.endTest();
	}
}