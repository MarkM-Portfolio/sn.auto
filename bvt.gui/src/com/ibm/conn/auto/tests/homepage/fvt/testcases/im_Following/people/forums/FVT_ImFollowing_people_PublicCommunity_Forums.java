package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.people.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * This is a functional test for the Homepage Activity Stream (I'm Following / People) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 07/2015
 */

public class FVT_ImFollowing_people_PublicCommunity_Forums extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;	
	private User testUser1 , testUser2;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);		
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
				
		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		
		// Set the common filters to be added to the array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS[2] = HomepageUIConstants.FilterForums;
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);

		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// User 1 will delete the community
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// User 2 will now unfollow User 1
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Person_CreateForum_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Create a forum in the community </B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.created story is displayed within the Communities, People and Forums view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8F84E6C70E601636852578FC002E8C6E">TTT - AS - FOLLOW - PERSON - FORUMS - 00272 - forum.created - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_person_Createforum_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(testName + Helper.genStrongRand());
		CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser1.getDisplayName());
		
		// Verify that the create forum event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Name: test_Person_CreateTopic_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Create a topic in a forum that is already in the community </B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.topic.created story is displayed within the Communities, People and Forums view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8807CD33FB1121ED852578FC002E9DB0">TTT - AS - FOLLOW - PERSON - FORUMS - 00292 - forum.topic.created - PUBLIC COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_person_CreateTopic_PublicCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the create forum event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateTopic_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Update an existing topic in a forum that is already in the community </B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.topic.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F5D34FD9EF0D9665852579BF0048D4C7">TTT - AS - FOLLOW - PERSON - FORUMS - 00296 - forum.topic.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_person_UpdateTopic_PublicCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the community forum and will update the topic
		String updatedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndEditDescription(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, updatedDescription);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news stories to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateTopicEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create topic event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
			
			// Verify that the update topic event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, updatedDescription}, null, false);
		}
		
		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Name: test_Person_CreateResponse_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Create a response to a topic in a forum that is already in the community</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.response.created story is displayed within the Communities, People and Forums view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C19CC97C8C90AEFD852578FC002EA245">TTT - AS - FOLLOW - PERSON - FORUMS - 00302 - forum.response.created - PUBLIC COMMNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_person_CreateResponse_PublicCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the community forum and will add a reply to the topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndAddReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, topicReply);
				
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
				
		// Create the news story to be verified
		String replyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
				
		// Verify that the reply event, topic description and reply content are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_Person_UpdateResponse_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Communities</B></li>
	*<li><B>Step: testUser 1 Create a new community with public access</B></li>
	*<li><B>Step: testUser 2 Follow User 1</B></li>
	*<li><B>Step: testUser 1 Update an existing response to a topic in a forum that is already in the community</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify:Verify that the forum.topic.reply.updated story is NOT displayed in any view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/624EFB276A4A0D19852579BF00498DA2">TTT - AS - FOLLOW - PERSON - FORUMS - 00306 - forum.topic.reply.updated - PUBLIC COMMUNITY (NEG SC NOV)</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_person_UpdateResponse_PublicCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the community forum and will add a reply to the topic and then edit the reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		String updatedReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, topicReply, updatedReply);
								
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
								
		// Create the news stories to be verified
		String replyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		String updateReplyEvent = CommunityForumNewsStories.getUpdateReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the reply event and updated reply are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyEvent, baseForumTopic.getDescription(), updatedReply}, filter, true);
			
			// Verify that the update reply event and original reply are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateReplyEvent, topicReply}, null, false);
		}
		
		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Name: test_Person_LikeTopic(Recommended)_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 follow User 1</B></li>
	*<li><B>Step: testUser 1 go to a public community you own</B></li>
	*<li><B>Step: testUser 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: testUser 1 like the topic</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.topic.recommended story appears for All, Communities, Forums & people</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BA1490541AF4CB8D85257AC400552329">TTT - AS - FOLLOW - PERSON - FORUMS - 00310 - forum.topic.recommended - PUBLIC COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test( groups = {"fvtonprem", "fvtcloud"})
	public void test_person_LikeTopic_PublicCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the community forum and will like / recommend the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndLikeTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1);
								
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
								
		// Create the news story to be verified
		String likeTopicEvent = CommunityForumNewsStories.getLikeTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
								
		// Verify that the like topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}	
	/**
	*<ul>
	*<li><B>Name: test_Person_LikeResponse_PublicCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 2 follow User 1</B></li>
	*<li><B>Step: testUser 1 go to a public community you own</B></li>
	*<li><B>Step: testUser 1 start a topic in the forum in the community</B></li>
	*<li><B>Step: testUser 1 reply to the topic</B></li>
	*<li><B>Step: testUser 1 like the reply</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Communities, Forums & people</B></li>
	*<li><B>Verify: Verify that the forum.topic.reply.recommended story appears for points 6-9</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DAA94EF02C0A9E6C85257AC400552800">TTT - AS - FOLLOW - PERSON - FORUMS - 00314 - forum.topic.reply.recommended - PUBLIC COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_person_LikeResponse_PublicCommunity() {
	
		String testName = ui.startTest();
		
		// User 1 will now create a topic in the community forum and will add a reply to the topic
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyAndLikeReply(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic, forumsAPIUser1, topicReply);
						
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
						
		// Create the news story to be verified
		String likeReplyEvent = CommunityForumNewsStories.getLikeReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
						
		// Verify that the like reply event, topic description and reply content are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{likeReplyEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}