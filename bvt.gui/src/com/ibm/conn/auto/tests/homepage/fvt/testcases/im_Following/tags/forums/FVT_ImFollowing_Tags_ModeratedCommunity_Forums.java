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

public class FVT_ImFollowing_Tags_ModeratedCommunity_Forums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterTags };
	
	private APICommunitiesHandler communitiesAPIUser2;
	private APIForumsHandler forumsAPIUser2;
	private BaseCommunity baseCommunity;
	private Community moderatedCommunity;
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
		
		// User 2 will now create a moderated community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser2, communitiesAPIUser2);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the community created during the test
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_CreateForum_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with Moderate access</B></li>
	*<li><B>Step: testUser 2 start a forum and add the tag that User 1 is following within the community</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Forums & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the forum.created is displayed in Homepage / All Updates filtered by Communities, Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1A77F1DB0163C635852578FD002B1087">TTT - AS - FOLLOW - TAG - FORUMS - 00163 - forum.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/		
	@Test(groups = {"fvtonprem", "fvtcloud"})	
	public void forumCreation_ModeratedCommunity(){			
		
		String testName = ui.startTest();		

		// User 2 create the forum topic with the tag User 1 is following in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForumWithCustomTag(testName + Helper.genStrongRand(), tagToFollow);
		CommunityForumEvents.createForum(moderatedCommunity, serverURL, testUser2, communitiesAPIUser2, forumsAPIUser2, baseForum);

		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createForumEvent = CommunityForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser2.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, true);

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_CreateTopic_ModeratedCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with Moderate access</B></li>
	*<li><B>Step: testUser 2 create a topic and add the tag that User 1 is following within the forum in the community</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Forums & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the forum.topic.created is displayed in Homepage / All Updates filtered by Communities, Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F1D4B0EB2B3BB8FC852578FD002B199C">TTT -AS - FOLLOW - TAG - FORUMS - 00183 - forum.topic.created - MODERATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void topicCreation_ModeratedCommunity(){
	
		String testName = ui.startTest();

		// User 2 create the forum topic with the tag User 1 is following
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityForumEvents.createForumTopic(testUser2, communitiesAPIUser2, moderatedCommunity, baseForumTopic);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_UpdateTopic_ModerateCommunity()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 go to a community they are the owner of with Moderated access</B></li>
	*<li><B>Step: testUser 2 create a forum topic with the tag that User 1 is following within the forum</B></li>
	*<li><B>Step: testUser 2 update a forum topic </B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Communities, Forums & Tags (All Tags / {TagName}</B></li>
	*<li><B>Verify: Verify that the forum.topic.created is displayed while the forum.topic.updated is NOT displayed in Homepage / All Updates filtered by Communities, Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/459D2949D50D50A2852579BB00774E46">TTT - AS - FOLLOW - TAG - FORUMS - 00187 - forum.topic.updated - MODERATE COMMUNITY</a></li>
	*</ul>
	*/	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void updatetopic_ModeratedCommunity(){
	
		String testName = ui.startTest();

		// User 2 create the forum topic with the tag User 1 is following and edit the topic's description
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomTag(testName + Helper.genStrongRand(), baseCommunity, tagToFollow);
		CommunityForumEvents.createForumTopicAndEditDescription(testUser2, communitiesAPIUser2, moderatedCommunity, baseForumTopic, forumsAPIUser2, editedDescription);
		
		// User 1 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news stories to be verified
		String createTopicEvent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());
		String updateTopicEvent = CommunityForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser2.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create topic event and original topic description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, filter, true);
			
			// Verify that the update topic event and edited description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, editedDescription}, null, false);
		}
		ui.endTest();			
	}	
}