package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityActivityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016 		                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */
public class FVT_InlineComments_CommunityActivityEvents extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };
	
	private Activity moderatedCommunityActivity, publicCommunityActivity, restrictedCommunityActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseActivity baseModeratedCommunityActivity, basePublicCommunityActivity, baseRestrictedCommunityActivity;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		// User 1 create a public community, add User 2 to the community as a member and add the activities widget to the community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(basePublicCommunity, BaseWidget.ACTIVITIES, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a moderated community, add User 2 to the community as a member and add the activities widget to the community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseModeratedCommunity, BaseWidget.ACTIVITIES, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 create a restricted community, add User 2 to the community as a member and add the activities widget to the community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndAddWidget(baseRestrictedCommunity, BaseWidget.ACTIVITIES, testUser2, isOnPremise, testUser1, communitiesAPIUser1);
				
		// User 1 create an activity in the public community
		basePublicCommunityActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), basePublicCommunity);
		publicCommunityActivity = CommunityActivityEvents.createCommunityActivity(basePublicCommunityActivity, basePublicCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, publicCommunity);
		
		// User 1 create an activity in the moderated community
		baseModeratedCommunityActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseModeratedCommunity);
		moderatedCommunityActivity = CommunityActivityEvents.createCommunityActivity(baseModeratedCommunityActivity, baseModeratedCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, moderatedCommunity);
	
		// User 1 create an activity in the restricted community
		baseRestrictedCommunityActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseRestrictedCommunity);
		restrictedCommunityActivity = CommunityActivityEvents.createCommunityActivity(baseRestrictedCommunityActivity, baseRestrictedCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, restrictedCommunity);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities that were created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_EntryTodo_FinalTwoComments_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an activity</B></li>
	*<li><B>Step: User 1 add an entry and a todo</B></li>
	*<li><B>Step: User 2 add 2 comments on each</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add a private comment to a todo</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify it is still the same 2 comments and the private one is not shown</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/216C4B44FD90028585257E2F0036A45B">TTT - INLINE COMMENTS - 00010 - ACTIVITY EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_EntryTodo_FinalTwoComments_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 add an entry to the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), publicCommunityActivity, false);
		ActivityEntry activityEntry = CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, publicCommunityActivity);
		
		// User 1 add a to-do item to the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), publicCommunityActivity, false);
		Todo activityTodo = CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, publicCommunityActivity);
		
		/**
		 * User 2 will now log in and navigate to the community activity entry
		 * 
		 * This step is critical for User 2's activities feed to be updated correctly which, in turn, allows the API method the
		 * permissions it needs to post all of the comments
		 */
		CommunityActivityEvents.loginAndNavigateToCommunityActivity(ui, uiCo, publicCommunity, basePublicCommunity, publicCommunityActivity, testUser2, communitiesAPIUser2, false);
		
		// User 2 will now post 2 comments to the activity entry
		String entryComment1 = Data.getData().commonComment + Helper.genStrongRand();
		String entryComment2 = Data.getData().commonComment + Helper.genStrongRand();
		String[] entryComments = { entryComment1, entryComment2 };
		
		CommunityActivityEvents.createMultipleComments(publicCommunityActivity, activityEntry, null, entryComments, testUser2, activitiesAPIUser2, false);
		
		// User 2 will now post 2 comments to the to-do item
		String todoComment1 = Data.getData().commonComment + Helper.genStrongRand();
		String todoComment2 = Data.getData().commonComment + Helper.genStrongRand();
		String[] todoComments = { todoComment1, todoComment2 };
		
		CommunityActivityEvents.createMultipleComments(publicCommunityActivity, null, activityTodo, todoComments, testUser2, activitiesAPIUser2, false);
		
		// Log out as User 2
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news stories to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), basePublicCommunityActivity.getName(), testUser2.getDisplayName());
		String commentOnTodoEvent = CommunityActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityTodo.getTitle(), basePublicCommunityActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on entry and comment on to-do item events are displayed in all views along with the two comments posted to each
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), entryComment1, entryComment2,
																commentOnTodoEvent, baseActivityTodo.getDescription(), todoComment1, todoComment2}, TEST_FILTERS, true);
		
		// User 2 will now post a private comment to the to-do item
		String todoComment3 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createComment(publicCommunityActivity, null, activityTodo, todoComment3, testUser2, activitiesAPIUser2, true);
		
		for(String filter : TEST_FILTERS) {	
			// Verify that the comment on entry and comment on to-do item events are displayed in the filter along with the two comments posted to each
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), entryComment1, entryComment2,
												commentOnTodoEvent, baseActivityTodo.getDescription(), todoComment1, todoComment2}, filter, true);
			
			// Verify that the private comment posted to the to-do item is not displayed in the filter
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{todoComment3}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_EntryTodo_FinalTwoComments_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an activity</B></li>
	*<li><B>Step: User 1 add an entry and a todo</B></li>
	*<li><B>Step: User 2 add 2 comments on each</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add a private comment to a todo</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify it is still the same 2 comments and the private one is not shown</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/216C4B44FD90028585257E2F0036A45B">TTT - INLINE COMMENTS - 00010 - ACTIVITY EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_EntryTodo_FinalTwoComments_ModCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 add an entry to the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), moderatedCommunityActivity, false);
		ActivityEntry activityEntry = CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, moderatedCommunityActivity);
		
		// User 1 add a to-do item to the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), moderatedCommunityActivity, false);
		Todo activityTodo = CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, moderatedCommunityActivity);
		
		/**
		 * User 2 will now log in and navigate to the community activity entry
		 * 
		 * This step is critical for User 2's activities feed to be updated correctly which, in turn, allows the API method the
		 * permissions it needs to post all of the comments
		 */
		CommunityActivityEvents.loginAndNavigateToCommunityActivity(ui, uiCo, moderatedCommunity, baseModeratedCommunity, moderatedCommunityActivity, testUser2, communitiesAPIUser2, false);
		
		// User 2 will now post 2 comments to the activity entry
		String entryComment1 = Data.getData().commonComment + Helper.genStrongRand();
		String entryComment2 = Data.getData().commonComment + Helper.genStrongRand();
		String[] entryComments = { entryComment1, entryComment2 };
		
		CommunityActivityEvents.createMultipleComments(moderatedCommunityActivity, activityEntry, null, entryComments, testUser2, activitiesAPIUser2, false);
		
		// User 2 will now post 2 comments to the to-do item
		String todoComment1 = Data.getData().commonComment + Helper.genStrongRand();
		String todoComment2 = Data.getData().commonComment + Helper.genStrongRand();
		String[] todoComments = { todoComment1, todoComment2 };
		
		CommunityActivityEvents.createMultipleComments(moderatedCommunityActivity, null, activityTodo, todoComments, testUser2, activitiesAPIUser2, false);
		
		// Log out as User 2
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news stories to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseModeratedCommunityActivity.getName(), testUser2.getDisplayName());
		String commentOnTodoEvent = CommunityActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityTodo.getTitle(), baseModeratedCommunityActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on entry and comment on to-do item events are displayed in all views along with the two comments posted to each
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), entryComment1, entryComment2,
																commentOnTodoEvent, baseActivityTodo.getDescription(), todoComment1, todoComment2}, TEST_FILTERS, true);
		
		// User 2 will now post a private comment to the to-do item
		String todoComment3 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createComment(moderatedCommunityActivity, null, activityTodo, todoComment3, testUser2, activitiesAPIUser2, true);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry and comment on to-do item events are displayed in the filter along with the two comments posted to each
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), entryComment1, entryComment2,
												commentOnTodoEvent, baseActivityTodo.getDescription(), todoComment1, todoComment2}, filter, true);
			
			// Verify that the private comment posted to the to-do item is not displayed in the filter
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{todoComment3}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_EntryTodo_FinalTwoComments_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create an activity</B></li>
	*<li><B>Step: User 1 add an entry and a todo</B></li>
	*<li><B>Step: User 2 add 2 comments on each</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add a private comment to a todo</B></li>
	*<li><B>Step: User1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline in the My Notifications view</B></li>
	*<li><B>Verify: Verify it is still the same 2 comments and the private one is not shown</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/216C4B44FD90028585257E2F0036A45B">TTT - INLINE COMMENTS - 00010 - ACTIVITY EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_EntryTodo_FinalTwoComments_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 add an entry to the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), restrictedCommunityActivity, false);
		ActivityEntry activityEntry = CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, restrictedCommunityActivity);
		
		// User 1 add a to-do item to the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), restrictedCommunityActivity, false);
		Todo activityTodo = CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, restrictedCommunityActivity);
		
		/**
		 * User 2 will now log in and navigate to the community activity entry
		 * 
		 * This step is critical for User 2's activities feed to be updated correctly which, in turn, allows the API method the
		 * permissions it needs to post all of the comments
		 */
		CommunityActivityEvents.loginAndNavigateToCommunityActivity(ui, uiCo, restrictedCommunity, baseRestrictedCommunity, restrictedCommunityActivity, testUser2, communitiesAPIUser2, false);
		
		// User 2 will now post 2 comments to the activity entry
		String entryComment1 = Data.getData().commonComment + Helper.genStrongRand();
		String entryComment2 = Data.getData().commonComment + Helper.genStrongRand();
		String[] entryComments = { entryComment1, entryComment2 };
		
		CommunityActivityEvents.createMultipleComments(restrictedCommunityActivity, activityEntry, null, entryComments, testUser2, activitiesAPIUser2, false);
		
		// User 2 will now post 2 comments to the to-do item
		String todoComment1 = Data.getData().commonComment + Helper.genStrongRand();
		String todoComment2 = Data.getData().commonComment + Helper.genStrongRand();
		String[] todoComments = { todoComment1, todoComment2 };
		
		CommunityActivityEvents.createMultipleComments(restrictedCommunityActivity, null, activityTodo, todoComments, testUser2, activitiesAPIUser2, false);
		
		// Log out as User 2
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, true);
		
		// Create the news stories to be verified
		String commentOnEntryEvent = CommunityActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityEntry.getTitle(), baseRestrictedCommunityActivity.getName(), testUser2.getDisplayName());
		String commentOnTodoEvent = CommunityActivityNewsStories.getCommentOnYourEntryNewsStory_User(ui, baseActivityTodo.getTitle(), baseRestrictedCommunityActivity.getName(), testUser2.getDisplayName());
		
		// Verify that the comment on entry and comment on to-do item events are displayed in all views along with the two comments posted to each
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), entryComment1, entryComment2,
																commentOnTodoEvent, baseActivityTodo.getDescription(), todoComment1, todoComment2}, TEST_FILTERS, true);
		
		// User 2 will now post a private comment to the to-do item
		String todoComment3 = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createComment(restrictedCommunityActivity, null, activityTodo, todoComment3, testUser2, activitiesAPIUser2, true);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on entry and comment on to-do item events are displayed in the filter along with the two comments posted to each
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnEntryEvent, baseActivityEntry.getDescription(), entryComment1, entryComment2,
												commentOnTodoEvent, baseActivityTodo.getDescription(), todoComment1, todoComment2}, filter, true);
			
			// Verify that the private comment posted to the to-do item is not displayed in the filter
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{todoComment3}, null, false);
		}
		ui.endTest();
	}	
}