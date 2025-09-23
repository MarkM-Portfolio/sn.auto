package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
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
 * This is a functional test for the Homepage Activity Stream (I'm Following / Tags) Component of IBM Connections
 * Created By: Srinivas Vechha.
 * Date: 09/2015
 */

public class FVT_ImFollowing_Tags_PrivateCommunity_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APIForumsHandler forumsAPIUser2;
	private BaseCommunity baseCommunity;
	private Community restrictedCommunity;
	private String tagToFollow;
	private User testUser1 , testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		
		// User 1 will now log in and follow a tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 will now create a restricted community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser2, communitiesAPIUser2);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(restrictedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_CreateForum_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with Private access</B></li>
	*<li><B>Step: testUser 2 start a forum and add the tag that User 1 is following within the community</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Forums & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the forum.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BA3F372E2CF427FF852578FD002B11BC">TTT -AS - FOLLOW - TAG - FORUMS - 00164 - forum.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/		
	@Test (groups = {"fvtonprem", "fvtcloud"})	
	public void forumCreation_PrivateCommunity(){			

		String testName = ui.startTest();		

		// User 2 create the forum topic with the tag User 1 is following in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForumWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityForumEvents.createForum(restrictedCommunity, serverURL, testUser2, communitiesAPIUser2, forumsAPIUser2, baseForum);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, false);

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_CreateTopic_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with private access</B></li>
	*<li><B>Step: testUser 2 create a topic and add the tag that User 1 is following within the forum in the community</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Forums & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the forum.topic.created story is not displayed in Homepage / All Updates filtered by Communities, Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A0F39C6576C1FAC3852578FD002BE2DB">TTT -AS - FOLLOW - TAG - FORUMS - 00184 - forum.topic.created - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void topicCreation_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create the forum topic with the tag User 1 is following
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityForumEvents.createForumTopic(testUser2, communitiesAPIUser2, restrictedCommunity, baseForumTopic);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_UpdateTopic_PrivateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with private access</B></li>
	*<li><B>Step: testUser 2 create a forum topic with the tag that User 1 is following within the forum</B></li>
	*<li><B>Step: testUser 2 update a forum topic </B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Forums & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the forum.topic.updated story is not displayed in Homepage / All Updates filtered by Communities, Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E411C84A32DA40E9852579BB007787DF">TTT - AS - FOLLOW - TAG - FORUMS - 00188 - forum.topic.updated - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test (groups = {"fvtonprem", "fvtcloud"})
	public void updatetopic_PrivateCommunity(){

		String testName = ui.startTest();

		// User 2 create the forum topic with the tag User 1 is following and edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityForumEvents.createForumTopicAndEditDescription(testUser2, communitiesAPIUser2, restrictedCommunity, baseForumTopic, forumsAPIUser2, editedDescription);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news stories to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		String updateTopicEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		// Verify that the create forum topic event, update forum topic event, original description and edited description are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, updateTopicEvent, baseForumTopic.getDescription(), editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}	
}