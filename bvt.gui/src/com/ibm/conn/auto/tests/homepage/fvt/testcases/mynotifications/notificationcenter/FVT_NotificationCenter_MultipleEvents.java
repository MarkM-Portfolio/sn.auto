package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification Center Flyout] FVT UI Automation for Story 140633
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/143012
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_MultipleEvents extends SetUpMethodsFVT {
	
	private Activity standaloneActivity;
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private BaseFile baseFile;
	private Community publicCommunity;
	private FileEntry publicFile;
	private Mentions mentions;
	private String boardMessage, user1BoardMessageId, user1StatusUpdateId;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 2 will now create a public file - essential for one of the notifications to be tested
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser2, filesAPIUser2);
		
		// NOTIFICATION ONE: User 1 will now create a public community with User 2 added as a member
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUser1, communitiesAPIUser1, testUser2);
		
		// NOTIFICATION TWO: User 1 will now post a status update with mentions to User 2
		mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		user1StatusUpdateId = ProfileEvents.addStatusUpdateWithMentions(profilesAPIUser1, mentions);
		
		// NOTIFICATION THREE: User 1 will now post a board message to User 2
		boardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1BoardMessageId = ProfileEvents.addBoardMessage(boardMessage, profilesAPIUser1, profilesAPIUser2);
		
		// NOTIFICATION FOUR: User 1 will now create a standalone activity with User 2 added as a member
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), false);
		standaloneActivity = ActivityEvents.createActivityWithOneMember(baseActivity, testUser1, activitiesAPIUser1, testUser2, isOnPremise);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the public file created during the test
		filesAPIUser2.deleteFile(publicFile);
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		
		// Delete all of the status updates / board messages posted during the test
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdateId);
		profilesAPIUser1.deleteBoardMessage(user1BoardMessageId);
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(standaloneActivity);
	}

	/**
	* test_NotificationCenter_MultipleEvents() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 start a community and add User 2 as a member</B></li>
	*<li><B>Step: User 1 mention User 2 in a status update comment</B></li>
	*<li><B>Step: User 1 like a blog entry User 2 owns</B></li>
	*<li><B>Step: User 1 reopen a to-do that User 2 completed</B></li>
	*<li><B>Step: User 1 notify User 2 of a Community event</B></li>
	*<li><B>Step: User 1 leave a board message on User 2's profile</B></li>
	*<li><B>Step: User 1 comment on a file owned by User 2</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 click on the Notification Center Header in the top navigation</B></li>
	*<li><B>Verify: Verify the flyout contains the 6 notifications User 1 latest 6 notifications and a <show more link / scroll bar indicating there is another></B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D809E4D7974B561285257DC80053E18A">TTT - NOTIFICATION CENTER FLYOUT - 00022 - 7 NOTIFICATIONS SHOW MORE SCROOLBAR IS IN THE FLYOUT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_MultipleEvents(){

		String testName = ui.startTest();
		
		// NOTIFICATION FIVE: User 1 will now post a comment to the public file uploaded by User 2
		String user1FileComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser1, filesAPIUser1, publicFile, user1FileComment, profilesAPIUser2);
		
		// NOTIFICATION SIX: User 1 will now like a community forum topic created by User 2
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), baseCommunity);
		CommunityForumEvents.createForumTopicAndLikeTopic(testUser2, communitiesAPIUser2, publicCommunity, baseForumTopic, forumsAPIUser1);
		
		// Log in to Connections as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
		
		// Create all six of the notifications to be verified
		String addedToCommunityEvent = CommunityNewsStories.getAddedYouToTheCommunityNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String mentionedYouInStatusEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testUser1.getDisplayName());
		String postedBoardMessageEvent = ProfileNewsStories.getPostedAMessageToYouNewsStory(ui, testUser1.getDisplayName());
		String addedToActivityEvent = ActivityNewsStories.getNotifiedYouThatYouWereAddedToTheActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser1.getDisplayName());
		String likeForumTopicEvent = CommunityForumNewsStories.getLikeYourTopicNewsStory_User(ui, baseForumTopic.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Verify that the added to community notification is displayed in the Notification Center flyout
		HomepageValid.verifyNewsStoryIsInNotificationCenterFlyout(ui, addedToCommunityEvent);
		
		// Verify that the mentioned in a status update notification is displayed in the Notification Center flyout
		HomepageValid.verifyNewsStoryIsInNotificationCenterFlyout(ui, mentionedYouInStatusEvent);
		
		// Verify that the board message posted to User 2's profile notification is displayed in the Notification Center flyout
		HomepageValid.verifyNewsStoryIsInNotificationCenterFlyout(ui, postedBoardMessageEvent);
		
		// Verify that the added to activity notification is displayed in the Notification Center flyout
		HomepageValid.verifyNewsStoryIsInNotificationCenterFlyout(ui, addedToActivityEvent);
		
		// Verify that the comment on file notification is displayed in the Notification Center flyout
		HomepageValid.verifyNewsStoryIsInNotificationCenterFlyout(ui, commentOnFileEvent);
		
		// Verify that the like forum topic notification is displayed in the Notification Center flyout
		HomepageValid.verifyNewsStoryIsInNotificationCenterFlyout(ui, likeForumTopicEvent);
		
		ui.endTest();
	}	
}