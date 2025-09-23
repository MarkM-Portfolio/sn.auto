package  com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.activities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

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
 * This is a functional test for the Homepage Activity Stream (Discover) Component of IBM Connections
 * Created By: Hugh Caren.
 * Date: 26/02/2014
 */

public class FVT_Discover_Standalone_Activities extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities };

	private Activity privateActivity;
	private ActivityEntry publicEntry;
	private APIActivitiesHandler activitiesAPIUser1;
	private BaseActivity baseActivity;
	private BaseActivityEntry basePublicEntry;
	private BaseActivityToDo basePublicTodo;
	private Reply publicReply;
	private Todo publicTodo;
	private User testUser1, testUser2;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		activitiesAPIUser1 = initialiseAPIActivitiesHandlerUser(testUser1);

		// User 1 creates a private activity
		baseActivity = ActivityBaseBuilder.buildBaseActivity(getClass().getSimpleName() + Helper.genStrongRand(), true);
		privateActivity = ActivityEvents.createActivity(testUser1, activitiesAPIUser1, baseActivity, isOnPremise);
		
		// Set all other relevant global test components to null
		publicEntry = null;
		publicTodo = null;
		publicReply = null;
	}

	@AfterClass(alwaysRun=true)
	public void tearDown() {
		
		// Delete the activity created during the test
		activitiesAPIUser1.deleteActivity(privateActivity);
	}
	
	/**
	* test_CreateDefaultActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Create a new private activity</B></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2ECE6CF25C594636852578760079E71F">TTT - DISC - ACTIVITY - 00010 - ACTIVITY.CREATED - STANDALONE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 1)
	public void test_CreateDefaultActivity_Standalone() {

		ui.startTest();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createActivityEvent}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CreateEntry_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a private activity that you are a member of</B></li>
	*<li><b>Step: Create a new entry in this activity</b></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F808EC34B70745FD852578760079E722">TTT - DISC - ACTIVITY - 00020 - ACTIVITY.ENTRY.CREATED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups ={"fvtonprem", "fvtcloud"}, priority = 2)
	public void test_CreateEntry_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createEntryEvent = ActivityNewsStories.getCreateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createEntryEvent, basePublicEntry.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateTodo_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a private activity that you are a member of</B></li>
	*<li><b>Step: Create a new todo in this activity</b></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DF444788BBC191E8852578760079E732">TTT - DISC - ACTIVITY - 00040 - ACTIVITY.TO-DO.CREATED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 3)
	public void test_CreateTodo_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String createTodoEvent = ActivityNewsStories.getCreateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{createTodoEvent, basePublicTodo.getDescription().trim()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateEntry_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activities</B></li>
	*<li><B>Step: User 1 go to a private activity you own</B></li>
	*<li><b>Step: User 1 go to an entry in the activity</b></li>
	*<li><B>Step: User 1 update the entry in the activity</B></li>
	*<li><b>Step: User 2 log into Home / Discover / All - verification point</b></li>
	*<li><B>Step: User 2 go to Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that there is no story in the views of the activity.entry.updated</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9D6A4C786CC151B785257A6200444477">TTT - DISC - ACTIVITY - 00090 - ACTIVITY.ENTRY.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 4)
	public void test_UpdateEntry_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
				
		// User 1 will now edit the description of the entry
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		publicEntry = ActivityEvents.editEntryDescription(testUser1, activitiesAPIUser1, publicEntry, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateEntryEvent = ActivityNewsStories.getUpdateEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateEntryEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateTodo_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into a private activity you own</B></li>
	*<li><B>Step: User 1 go to a todo in the activity</B></li>
	*<li><b>Step: User 1 update the todo</b></li>
	*<li><B>Step: User 2 log into Home / Discover / All - verification point</B></li>
	*<li><B>Step: User 2 go to Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that the activity.todo.updated story does NOT appear in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DDECCA8BEBB81F6F85257A630039E02E">TTT - DISC - ACTIVITY - 00100 - ACTIVITY.TO-DO.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 5)
	public void test_UpdateTodo_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now edit the description of the public to-do item
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		publicTodo = ActivityEvents.editTodoDescription(testUser1, activitiesAPIUser1, publicTodo, editedDescription);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String updateTodoEvent = ActivityNewsStories.getUpdateToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{updateTodoEvent, editedDescription}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_CreateEntryComment_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a private activity that you are a member of</B></li>
	*<li><b>Step: Create a comment on an entry in this activity</b></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.entry.comment.created is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8936F330C594EE77852578760079E72A">TTT - DISC - ACTIVITY - 00030 - ACTIVITY.ENTRY.COMMENT.CREATED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 6)
	public void test_CreateEntryComment_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
				
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, publicEntry.getContent().trim(), publicReply.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	/**
	* test_UpdateReply_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a private activity that you are a member of</B></li>
	*<li><b>Step: Update an existing comment on an entry in this activity</b></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.reply.updated is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/05BB7F8BB58D6998852579BC0057BC41">TTT - DISC - ACTIVITY - 00080 - ACTIVITY.REPLY.UPDATED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren hughcare@ie.ibm.com
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 7)
	public void test_UpdateReply_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public entry to the activity
		createPublicEntryIfRequired();
						
		// User 1 will now add a public comment to the public entry
		createPublicReplyIfRequired();
				
		// User 1 will now edit the public comment on the public entry
		String originalReplyContent = publicReply.getContent().trim();
		String user1EditedComment = Data.getData().commonComment + Helper.genStrongRand();
		ActivityEvents.editComment(testUser1, activitiesAPIUser1, publicReply, user1EditedComment);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String commentOnEntryEvent = ActivityNewsStories.getCommentOnTheirOwnEntryNewsStory(ui, basePublicEntry.getTitle(),baseActivity.getName(), testUser1.getDisplayName());
		String updateEntryCommentEvent = ActivityNewsStories.getUpdateCommentOnEntryNewsStory(ui, basePublicEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		
		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnEntryEvent, updateEntryCommentEvent, publicEntry.getContent().trim(), originalReplyContent, user1EditedComment}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_CompleteTodo_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log in to Activities</B></li>
	*<li><B>Step: Open a private activity that you are a member of and contains todo items in this activity</B></li>
	*<li><b>Step: Mark the todo as completed</b></li>
	*<li><B>Step: Log in to Home as a different user</B></li>
	*<li><B>Step: Go to Home \ Activity Stream \ Discover</B></li>
	*<li><B>Step: Filter by Activity</B></li>
	*<li><B>Verify: Verify that the news story for activity.todo.completed is not seen - negative test</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D7D411C38A8A49E7852578760079E73A">TTT - DISC - ACTIVITY - 00050 - ACTIVITY.TO-DO.COMPLETED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren
	*/	
	@Test(groups={"fvtonprem", "fvtcloud"}, priority = 8)
	public void test_CompleteTodo_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
				
		// User 1 will now mark the public to-do item as completed
		publicTodo = ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String completeTodoEvent = ActivityNewsStories.getCompleteToDoNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{completeTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();
	}
	
	/**
	* test_ReopenTodo_PrivateActivity_Standalone() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Activity</B></li>
	*<li><B>Step: User 1 go to a private activity</B></li>
	*<li><b>Step: User 1 go to a completed todo</b></li>
	*<li><B>Step: User 1 reopen the complete todo</B></li>
	*<li><B>Step: User 2 log into Home / Discover / All - verification point</B></li>
	*<li><B>Step: User 2 log into Home / Discover / Activities - verification point</B></li>
	*<li><B>Verify: Verify that there is no story of the activity.todo.reopened in the views</B></li>
	* <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/120FF4EE8BDA44A985257A6200493AA5">TTT - DISC - ACTIVITY - 00110 - ACTIVITY.TO-DO.REOPENED - STANDALONE PRIVATE ACTIVITY (NEG)</a></li>
	* @author Hugh Caren hughcare@ie.ibm.com
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"}, priority = 9)
	public void test_ReopenTodo_PrivateActivity_Standalone() {

		ui.startTest();

		// User 1 will now add a public to-do item to the activity
		createPublicTodoIfRequired();
		
		if(publicTodo.isComplete() == false) {
			// User 1 will now mark the public to-do item as completed
			publicTodo = ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, true);
		}
		// User 1 will now re-open the public to-do item again
		ActivityEvents.markToDoItemAsCompleteOrIncomplete(testUser1, activitiesAPIUser1, publicTodo, false);
		
		// User 2 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the event to be verified
		String reopenTodoEvent = ActivityNewsStories.getReopenToDoItemNewsStory(ui, basePublicTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{reopenTodoEvent, publicTodo.getContent().trim()}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
	
	private void createPublicEntryIfRequired() {
		if(publicEntry == null) {
			// User 1 will now add a public entry to the activity
			basePublicEntry = ActivityBaseBuilder.buildBaseActivityEntry(getClass().getSimpleName() + Helper.genStrongRand(), privateActivity, false);
			publicEntry = ActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, basePublicEntry, privateActivity);
		}
	}
	
	private void createPublicTodoIfRequired() {
		if(publicTodo == null) {
			// User 1 will now add a public to-do item to the activity
			basePublicTodo = ActivityBaseBuilder.buildBaseActivityToDo(getClass().getSimpleName() + Helper.genStrongRand(), privateActivity, false);
			publicTodo = ActivityEvents.createTodo(testUser1, activitiesAPIUser1, basePublicTodo, privateActivity);
		}
	}
	
	private void createPublicReplyIfRequired() {
		if(publicReply == null) {
			// User 1 will now add a public comment to the public entry
			String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
			publicReply = ActivityEvents.createComment(privateActivity, publicEntry, null, user1Comment, testUser1, activitiesAPIUser1, false);
		}
	}
}