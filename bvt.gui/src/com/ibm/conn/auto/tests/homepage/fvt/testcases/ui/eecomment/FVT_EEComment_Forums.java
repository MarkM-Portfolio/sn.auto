package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
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
/* Copyright IBM Corp. 2015, 2016	                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_EEComment_Forums extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		// User 1 will now create a public community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(basePublicCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseRestrictedCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* eeComment_PublicCommunity_ForumTopic() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a public community</B></li>
	*<li><B>Step: Create a forum topic</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Forums</B></li>
	*<li><B>Step: Open the EE for the public community forum topic news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the EE</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void eeComment_PublicCommunity_ForumTopic(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), basePublicCommunity);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, publicCommunity, baseForumTopic);
		
		// Log in as User 1 and navigate to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String createTopicevent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), basePublicCommunity.getName(), testUser1.getDisplayName());
		
		// User 1 will now open the EE for the create forum topic event and will post a reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		boolean replyPostedInEE = CommunityForumEvents.createForumTopicReplyUsingUI(ui, testUser1, createTopicevent, topicReply);
		
		// Verify that the reply posted correctly in the EE - this includes verifying that the comment was displayed in the EE after posting
		HomepageValid.verifyBooleanValuesAreEqual(replyPostedInEE, true);
		
		// Switch focus back to the main frame again
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), basePublicCommunity.getName());
		
		// Verify that the create reply event and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* eeComment_ModCommunity_ForumTopic() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a moderated community</B></li>
	*<li><B>Step: Create a forum topic</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Forums</B></li>
	*<li><B>Step: Open the EE for the moderated community forum topic news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the EE</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void eeComment_ModCommunity_ForumTopic(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseModeratedCommunity);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, moderatedCommunity, baseForumTopic);
		
		// Log in as User 1 and navigate to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String createTopicevent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseModeratedCommunity.getName(), testUser1.getDisplayName());
		
		// User 1 will now open the EE for the create forum topic event and will post a reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		boolean replyPostedInEE = CommunityForumEvents.createForumTopicReplyUsingUI(ui, testUser1, createTopicevent, topicReply);
		
		// Verify that the reply posted correctly in the EE - this includes verifying that the comment was displayed in the EE after posting
		HomepageValid.verifyBooleanValuesAreEqual(replyPostedInEE, true);
		
		// Switch focus back to the main frame again
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseModeratedCommunity.getName());
		
		// Verify that the create reply event and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
				
		ui.endTest();
	}

	/**
	* eeComment_PrivateCommunity_ForumTopic() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a private community</B></li>
	*<li><B>Step: Create a forum topic</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Forums</B></li>
	*<li><B>Step: Open the EE for the private community forum topic news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the EE</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void eeComment_PrivateCommunity_ForumTopic(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseRestrictedCommunity);
		CommunityForumEvents.createForumTopic(testUser1, communitiesAPIUser1, restrictedCommunity, baseForumTopic);
		
		// Log in as User 1 and navigate to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String createTopicevent = CommunityForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		
		// User 1 will now open the EE for the create forum topic event and will post a reply
		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		boolean replyPostedInEE = CommunityForumEvents.createForumTopicReplyUsingUI(ui, testUser1, createTopicevent, topicReply);
		
		// Verify that the reply posted correctly in the EE - this includes verifying that the comment was displayed in the EE after posting
		HomepageValid.verifyBooleanValuesAreEqual(replyPostedInEE, true);
		
		// Switch focus back to the main frame again
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String createReplyEvent = CommunityForumNewsStories.getReplyToYourTopicNewsStory_You(ui, baseForumTopic.getTitle(), baseRestrictedCommunity.getName());
		
		// Verify that the create reply event and the comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createReplyEvent, baseForumTopic.getDescription(), topicReply}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}