package  com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.activities;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
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
/*
 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 26/02/2014
 */

public class FVT_ImFollowing_PublicCommunity_Activities extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };

	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();

	private Activity communityActivity;
	private APIActivitiesHandler activitiesAPIUser1, activitiesAPIUser2;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseActivity baseActivity;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);
		activitiesAPIUser2 = initialiseAPIActivitiesHandlerUser(testUser2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);

		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		
		// User 1 will now add an activity to the community with User 2 as a follower
		baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity);
		communityActivity = CommunityActivityEvents.createCommunityActivityWithOneFollower(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, activitiesAPIUser2, publicCommunity);
	}
	
	@AfterClass(alwaysRun=true)
	public void tearDown(){
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
				
		// Delete any additional communities created during the test
		Set<Community> communitiesToDelete = communitiesForDeletion.keySet();
		
		for(Community community: communitiesToDelete){
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivity_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1</B></li>	
	 *<li><B>Step: Have user 2 FOLLOW this community</B></li>
	 *<li><b>Step: Create a new activity within this community as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B0E5793B7BFECC39852578760079E8AD">TTT -  AS - FOLLOW - ACTIVITY - 00013 - ACTIVITY.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */	
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateActivity_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a public community with User 2 as a follower (this is the only test case where User 2 must follow the community)
		BaseCommunity testBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community testPublicCommunity = CommunityEvents.createNewCommunityWithOneFollowerAndAddWidget(testBaseCommunity, testUser2, communitiesAPIUser2, BaseWidget.ACTIVITIES, testUser1, communitiesAPIUser1, isOnPremise);
		communitiesForDeletion.put(testPublicCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity testBaseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), testBaseCommunity);
		CommunityActivityEvents.createCommunityActivity(testBaseActivity, testBaseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, testPublicCommunity);

		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String createActivityEvent = CommunityActivityNewsStories.getCreateActivityNewsStory(ui, testBaseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent, testBaseActivity.getGoal()}, TEST_FILTERS, true);
		
		// Perform clean up now that the test has completed
		communitiesAPIUser1.deleteCommunity(testPublicCommunity);
		communitiesForDeletion.remove(testPublicCommunity);
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateActivityEntry_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1</B></li>	
	 *<li><b>Step: Create a new activity within this community as user 1</b></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2E1C02F9274298B5852578760079E8B6">TTT -  AS - FOLLOW - ACTIVITY - 00023 - ACTIVITY.ENTRY.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateActivityEntry_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false); 
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
		
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateEntry_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1</B></li>	
	 *<li><b>Step: Create a new activity within this community as user 1</b></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1 - set the visibility of the entry to "private"</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.created is NOT seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/36C78ED272F7A464852578760079E8B3">TTT -  AS - FOLLOW - ACTIVITY - 00024 - ACTIVITY.ENTRY.CREATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreatePrivateEntry_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a private entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false); 
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryComment_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a comment to activity entry as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.comment.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/63E1C20810B2B042852578760079E8BB">TTT -  AS - FOLLOW - ACTIVITY - 00033 - ACTIVITY.ENTRY.COMMENT.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateEntryComment_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will comment on that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription(), comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateEntryPrivateComment_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>	
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a comment to activity entry as user 1 - mark the comment private</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.entry.comment.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.entry.comment.created is NOT seen in the Activities view</B></li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E728CDDDE1D4A730852578760079E8BA">TTT -  AS - FOLLOW - ACTIVITY - 00034 - ACTIVITY.ENTRY.COMMENT.CREATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE COMMENT (NEG)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreateEntryPrivateComment_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will post a private comment to that entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, true);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, comment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreateTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.todo.created is seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is seen in the Activities view</B></li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/57ECEE69B5CB89F2852578760079E8C0">TTT -  AS - FOLLOW - ACTIVITY - 00043 - ACTIVITY.TO-DO.CREATED - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_CreateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CreatePrivateTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>	
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1, mark the todo as private</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify: Verify that the news story for activity.todo.created is NOT seen in the Communities view</b></li>
	 *<li><B>Verify: Verify that the news story for activity.todo.created is NOT seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D0934CEDC14C03CB852578760079E8C1">TTT -   AS - FOLLOW - ACTIVITY - 00044 - ACTIVITY.TO-DO.CREATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CreatePrivateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String newsStory = CommunityActivityNewsStories.getCreateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_CompleteTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity todo as user 1</b></li>
	 *<li><b>Step: Mark the todo as complete as user 1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Communities - verification point #1</b></li>
	 *<li><b>Step: Go to Home \ Activity Stream \ I'm Following  Filter by Activities - verification point #2</b></li>
	 *<li><b>Verify that the news story for activity.todo.completed is seen in the Communities view</b></li>
	 *<li><B>Verify that the news story for activity.todo.completed is seen in the Activities view</B></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E1902608CD07A998852578760079E8C9">TTT - AS - FOLLOW - ACTIVITY - 00054 - ACTIVITY.TO-DO.COMPLETED - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_CompleteTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity and will mark the to-do item as completed
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String newsStory = CommunityActivityNewsStories.getCompleteToDoNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntryComment_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1</b></li>
	 *<li><b>Step: Create a new comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Homepage \ I'm Following \ All, Activities & Communities</b></li>
	 *<li><b>Verify that the news story for activity.reply.updated is NOT seen in any of the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C04F05F79B0969EB852579BB0057F580">TTT - AS - FOLLOW - ACTIVITY - 00063 - ACTIVITY.REPLY.UPDATED - PUBLIC COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateEntryComment_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity, will comment on the entry and will then edit the comment on the entry
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false, replyEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as this news story should still appear in the Activity Stream
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on activity entry event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), replyEdit}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntryComment_PrivateEntry_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: Log in to Communities as user 1</B></li>
	 *<li><B>Step: Create a new community with public access as user 1, add user 2 as a member</B></li>
	 *<li><B>Step: Create a new activity within this community as user 1</B></li>
	 *<li><b>Step: Have user 2 FOLLOW this activity</b></li>
	 *<li><b>Step: Create a new activity entry as user 1 - set the visibility of the entry to "private"</b></li>
	 *<li><b>Step: Create a new comment as user1</b></li>
	 *<li><b>Step: Edit and save the comment as user1</b></li>
	 *<li><b>Step: Log in to Home as user 2</b></li>
	 *<li><b>Step: Go to Homepage \ I'm Following \ All, Activities & Communities</b></li>
	 *<li><b>Verify that the news story for activity.reply.updated is NOT seen in any of the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/39B91C46B030709C852579BB00583ACF">TTT - AS - FOLLOW - ACTIVITY - 00064 - ACTIVITY.REPLY.UPDATED- PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateEntryComment_PrivateEntry_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a private activity entry in the community activity, will post a comment to the entry and then edit the comment
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().StatusComment + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, comment, false, replyEdit);
		
		//User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as this news story should still appear in the Activity Stream
		String commentEvent = CommunityActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateCommentEvent = CommunityActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment event and updated comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentEvent, baseActivityEntry.getDescription(), replyEdit}, filter, true);
			
			// Verify that the update comment event and original comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateCommentEvent, comment}, null, false);
		}
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntry_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to an entry in the activity</B></li>	
	 *<li><B>Step: User 1 update the entry</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of activity.entry.updated does NOT appear in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/09C280E5028E6F4085257A620051F0D8">TTT -  AS - FOLLOW - ACTIVITY - 00073 - ACTIVITY.ENTRY.UPDATED - PUBLIC COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren 
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateEntry_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create an entry in the activity and will update the description of the entry
		String entryDescriptionEdit = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand());
		CommunityActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryDescriptionEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the create entry event and original description are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{createEntryEvent, baseActivityEntry.getDescription()}, filter, true);
			
			// Verify that the update entry event and updated description are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{updateEntryEvent, entryDescriptionEdit}, null, false);
		}
		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateEntry_PrivateEntry_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a private entry in the activity</B></li>	
	 *<li><B>Step: User 1 update the entry</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of activity.entry.updated does NOT appear in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8C67C72C4EAF4C8D85257A620051F214">TTT - AS - FOLLOW - ACTIVITY - 00074 - ACTIVITY.ENTRY.UPDATED - PUBLIC COMMUNITY ACTIVITY / PRIVATE ENTRY (NEG)</a></li>
	 * @author Hugh Caren 
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_UpdateEntry_PrivateEntry_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a private entry in the activity and will update the description of the entry
		String entryDescriptionEdit = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, communityActivity, entryDescriptionEdit);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String createEntryEvent = CommunityActivityNewsStories.getCreateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryEvent = CommunityActivityNewsStories.getUpdateEntryNewsStory(ui, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify that the create and update events for the private entry are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, updateEntryEvent, baseActivityEntry.getDescription(), entryDescriptionEdit}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_UpdateTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a todo in the activity</B></li>	
	 *<li><B>Step: User 1 update the todo in the activity</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of the activity.todo.updated appears in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3670CE2B99D9FEBE85257A6300368EFE">TTT - AS - FOLLOW - ACTIVITY - 00083 - ACTIVITY.TO-DO.UPDATED - PUBLIC COMMUNITY ACTIVITY (NEG SC NOV)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_UpdateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will create a to-do item in the activity and will edit the description of that to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, communityActivity, newDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(),testUser1.getDisplayName());

		// Verify that the update event for the to-do item is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_EditPrivateTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a private todo in the activity</B></li>	
	 *<li><B>Step: User 1 update the todo in the activity</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the story of the activity.todo.updated does NOT appear in the views</b></li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/097B629F61F840B285257A630036901F">TTT - AS - FOLLOW - ACTIVITY - 00084 - ACTIVITY.TO-DO.UPDATED - PUBLIC COMMUNITY ACTIVITY / PRIAVTE ENTRY (NEG)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups ={"fvtonprem", "fvtcloud"})
	public void test_EditPrivateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will create a private to-do item in the activity and will edit the description of that to-do item
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseTodo, communityActivity, newDescription);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified as non event should NOT appear in the Activity Stream
		String updateTodoEvent = CommunityActivityNewsStories.getUpdateToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(),testUser1.getDisplayName());

		// Verify that the update event for the to-do item is NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, newDescription}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_ReopenTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a completed todo in the community</B></li>	
	 *<li><B>Step: User 1 reopen the completed todo</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the activity.todo.reopened story appears in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6F047F9D935CA19285257A63002F63FB">TTT - AS - FOLLOW - ACTIVITY - 00093 - ACTIVITY.TO-DO.REOPENED - PUBLIC COMMUNITY ACTIVITY</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_ReopenTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a to-do item in the activity, will mark the to-do item as completed and will then re-open the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String newsStory = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story appears in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Name: test_ReopenTodo_PrivateTodo_PublicCommunity()</B></li>
	 *<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	 *<li><B>Step: User 1 log into a public community you are the owner of that has an activity</B></li>
	 *<li><B>Step: User 2 ensure you are following the activity</B></li>
	 *<li><B>Step: User 1 go to a completed private todo in the community</B></li>	
	 *<li><B>Step: User 1 reopen the completed todo</B></li>
	 *<li><b>Step: User 2 go to Home / I'm Following / Communities</b></li>
	 *<li><b>Step: User 2 go to  Home / I'm Following / All & Activities</b></li>
	 *<li><b>Verify that the activity.todo.reopened story does NOT appear in the views</b></li>
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/566414DA1C89361C85257A63002F6670">TTT - AS - FOLLOW - ACTIVITY - 00094 - ACTIVITY.TO-DO.REOPENED - PUBLIC COMMUNITY ACTIVITY / PRIVATE TO-DO (NEG)</a></li>
	 * @author Hugh Caren
	 */
	@Test(groups={"fvtonprem", "fvtcloud"})
	public void test_ReopenTodo_PrivateTodo_PublicCommunity(){

		String testName = ui.startTest();

		// User 1 will now create a private to-do item in the activity, will mark the to-do item as completed and will then re-open the to-do item
		BaseActivityToDo baseTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), communityActivity, true);
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseTodo, communityActivity);
		
		// User 2 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Assign event to be verified
		String newsStory = CommunityActivityNewsStories.getReopenToDoItemNewsStory(ui, baseTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{newsStory, baseTodo.getDescription()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
}