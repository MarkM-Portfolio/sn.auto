package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.tags.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

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

public class FVT_ImFollowing_Tags_StandaloneForums extends SetUpMethodsFVT {

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums, HomepageUIConstants.FilterTags };

	private APIForumsHandler forumsAPIUser2;	
	private BaseForum baseForum;
	private Forum standaloneForum;
	private String tagToFollow;
	private User testUser1, testUser2;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		forumsAPIUser2 = initialiseAPIForumsHandlerUser(testUser2);
		
		// User 1 will now log in and follow a tag
		tagToFollow = Helper.genStrongRand();
		UIEvents.followTag(ui, driver, testUser1, tagToFollow);
		
		// User 2 create a standalone forum
		baseForum = ForumBaseBuilder.buildBaseForumWithCustomTag(getClass().getSimpleName() + Helper.genStrongRand(), tagToFollow);
		standaloneForum = ForumEvents.createForum(testUser2, forumsAPIUser2, baseForum);
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the forum created during the test
		forumsAPIUser2.deleteForum(standaloneForum);
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_CreateForum_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 start a forum and add the tag that User 1 is following</B></li>		
	*<li><B>Step: testUser 2 log into Homepage / Updates / I'm Following / All, Forums & Tags</B></li>
	*<li><B>Verify: Verify that the forum.created story is displayed in Homepage / All Updates filtered by Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6980F6F8391AB299852578FD002B0C8C">TTT - AS - FOLLOW - TAG - FORUMS - 00161 - forum.created - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void createForum_Standalone(){

		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createForumEvent = ForumNewsStories.getCreateForumNewsStory(ui, baseForum.getName(), testUser2.getDisplayName());
		
		// Verify that the create forum event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createForumEvent, baseForum.getDescription()}, TEST_FILTERS, true);

		ui.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Name: test_Tags_CreateTopic_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 create a topic and add the tag that User 1 is following within forum</B></li>
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Forums & Tags</B></li>
	*<li><B>Verify: Verify that the forum.topic.created story is displayed in Homepage / All Updates filtered by Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1A339107778BB5A1852578FD002B1783">TTT - AS - FOLLOW - TAG - FORUMS - 00181 - forum.topic.created - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void createForumTopic_Standalone(){

		String testName = ui.startTest();

		// User 2 will now create a forum topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopicWithCustomTag(testName + Helper.genStrongRand(), standaloneForum, tagToFollow);
		ForumEvents.createForumTopic(testUser2, forumsAPIUser2, baseForumTopic);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser2.getDisplayName());

		// Verify that the create topic event is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTopicEvent, baseForumTopic.getDescription()}, TEST_FILTERS, true);

		ui.endTest();
		
	}
	/**
	*<ul>
	*<li><B>Name: test_Tags_UpdateForumTopic_Standalone()</B></li>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser 1 log into Connections</B></li>
	*<li><B>Step: testUser 1 follow a tag</B></li>
	*<li><B>Step: testUser 2 log into Connections</B></li>
	*<li><B>Step: testUser 2 update a forum topic with the tag that User 1 is following within the forum</B></li>	
	*<li><B>Step: testUser 1 log into Homepage / Updates / I'm Following / All, Forums & people</B></li>
	*<li><B>Verify: Verify that the focum.topic.created story is displayed while the forum.topic.updated story is NOT displayed in Homepage / All Updates filtered by Tags and Forums</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C2554A9607D9DB46852579BB0076CCC6">TTT - AS - FOLLOW - TAG - FORUMS - 00185 - forum.topic.updated - STANDALONE FORUM</a></li>
	*</ul>
	*/
	@Test(groups ={"fvtonprem"}, priority = 2)
	public void updateForumTopic_Standalone(){

		String testName = ui.startTest();

		// User 2 will now create a forum topic and will update the description of the topic
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopicWithCustomTag(testName + Helper.genStrongRand(), standaloneForum, tagToFollow);
		ForumEvents.createForumTopicAndEditTopicDescription(testUser2, forumsAPIUser2, baseForumTopic, editedDescription);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news stories to be verified
		String createTopicEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser2.getDisplayName());
		String updateTopicEvent = ForumNewsStories.getUpdateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser2.getDisplayName());

		for(String filter : TEST_FILTERS) {
			// Verify that the create topic event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createTopicEvent, baseForumTopic.getTitle()}, filter, true);
			
			// Verify that the update topic event and edited description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateTopicEvent, editedDescription}, null, false);
		}
		ui.endTest();		
	}	
}