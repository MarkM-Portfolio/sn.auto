package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty - DOHERTYP@ie.ibm.com
 */

public class FVT_VisitorModel_SharedExternallyHeader_ActivityEvents extends SetUpMethods2{

	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterActivities, HomepageUIConstants.FilterCommunities };
	private User MEMBERS_TO_ADD[] = new User[2];
	
	private APIActivitiesHandler activitiesAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser3;
	private boolean isOnPremise;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	private HomepageUI ui;	
	private String serverURL;	
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;		
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while (testUser1.getDisplayName().equals(testUser3.getDisplayName()));		
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB);

		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communitiesAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		activitiesAPIUser1 = new APIActivitiesHandler("Activity", serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		MEMBERS_TO_ADD[0] = testUser2;
		MEMBERS_TO_ADD[1] = testUser3;
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		ui = HomepageUI.getGui(cfg.getProductName(),driver);		
	}

	@AfterClass(alwaysRun=true)
	public void tearDown(){
		Set<Community> communitiesToDelete = communitiesForDeletion.keySet();
		
		for(Community community: communitiesToDelete){
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_activityCreated_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Activities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_activityCreated_visitorAdded() {
		
		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);	

		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_ACTIVITY, baseActivity.getName(), null, testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivity.getGoal()}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}	
		ui.endTest();
	}	
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_todoCreated_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add a todo to the community activity</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Activities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_todoCreated_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);
		
		// User 1 will now create a to-do item in the activity
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createActivityTodo(testUser1, activitiesAPIUser1, baseActivityTodo, newActivity);

		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_TODO_ITEM, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityTodo.getDescription()}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}	
		ui.endTest();
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_todoEdited_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add a todo to the community activity</B></li>
	*<li><B>Step: testUser1 edit the todo</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_todoEdited_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);
		
		// User 1 will now create a to-do item in the activity and will then edit the to-do item description
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		String newDescription = Data.getData().commonDescription + Helper.genStrongRand();
		CommunityActivityEvents.createTodoAndEditDescription(testUser1, activitiesAPIUser1, baseActivityTodo, newActivity, newDescription);

		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.UPDATE_ACTIVITY_TODO, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, newDescription}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}	
		ui.endTest();
	}	

	/**
	* visitor_sharedExternallyHeader_privateCommunity_todoCompleted_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add a todo to the community activity</B></li> 
	*<li><B>Step: testUser1 complete the todo</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_todoCompleted_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);

		// User 1 will now create a to-do item in the activity and will then mark the to-do item as completed
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand(), newActivity, false);
		CommunityActivityEvents.createTodoAndMarkAsCompleted(testUser1, activitiesAPIUser1, baseActivityTodo, newActivity);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMPLETE_A_TODO_ITEM, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityTodo.getDescription()}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}		
		ui.endTest();		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_todoReopened_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add a todo to the community activity</B></li>
	*<li><B>Step: testUser1 complete the todo</B></li>
	*<li><B>Step: testUser1 reopen the todo</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_todoReopened_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);

		// User 1 will now create a to-do item in the activity, mark the to-do item as completed and will then re-open the to-do item
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		CommunityActivityEvents.createTodoAndMarkAsCompletedAndReopen(testUser1, activitiesAPIUser1, baseActivityTodo, newActivity);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.REOPEN_TODO_ITEM, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityTodo.getDescription()}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}	
		ui.endTest();		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_todoComment_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add a todo to the community activity</B></li>
	*<li><B>Step: testUser1 comment on the todo</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_todoComment_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);
		
		// User 1 will now create a to-do item in the activity and will post a comment to that to-do item
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityTodoAndAddComment(testUser1, activitiesAPIUser1, baseActivityTodo, newActivity, comment, false);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMENT_ON_THEIR_OWN_TODO, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityTodo.getDescription(), comment}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}
		ui.endTest();		
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_todoCommentEdit_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add a todo to the community activity</B></li>
	*<li><B>Step: testUser1 comment on the todo</B></li>
	*<li><B>Step: testUser1 edit the comment on the todo</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Activities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_todoCommentEdit_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);
		
		// User 1 will now create a to-do item in the activity, post a comment to the to-do item and will then edit the comment
		BaseActivityToDo baseActivityTodo = ActivityBaseBuilder.buildBaseActivityToDo(testName + Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String commentEdit = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityTodoAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityTodo, newActivity, comment, false, commentEdit);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMENT_ON_THEIR_OWN_TODO, baseActivityTodo.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityTodo.getDescription(), commentEdit}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}
		ui.endTest();		
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_entryCreated_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Activities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_entryCreated_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);	
		
		// User 1 will now create an entry in the activity
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(Helper.genStrongRand());
		CommunityActivityEvents.createActivityEntry(testUser1, activitiesAPIUser1, baseActivityEntry, newActivity);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_ACTIVITY_ENTRY, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());	
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription()}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}
		ui.endTest();			
	}
	
	/**
	*<ul>
	* visitor_sharedExternallyHeader_privateCommunity_entryEdited_visitorAdded() 
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser1 edit the entry</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_entryEdited_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);	
		
		// User 1 will now create an entry in the activity and will then edit the entry description
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(Helper.genStrongRand());
		String entryEdit = Data.getData().commonDescription + Helper.genStrongRand();
		CommunityActivityEvents.createEntryAndEditDescription(testUser1, activitiesAPIUser1, baseActivityEntry, newActivity, entryEdit);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.CREATE_ACTIVITY_ENTRY, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());	
		String nonEvent = ui.replaceNewsStory(Data.UPDATE_ACTIVITY_ENTRY, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());	
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription()}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{nonEvent, entryEdit}, null, false);
		}
		ui.endTest();		
	}	

	/**
	* visitor_sharedExternallyHeader_privateCommunity_entryComment_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser1 comment on the entry</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_entryComment_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);		
		
		// User 1 will now create an entry in the activity and will then post a comment to the entry
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityEntryAndComment(testUser1, activitiesAPIUser1, baseActivityEntry, newActivity, comment, false);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMENT_ON_THEIR_OWN_ACTIVITY, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());	
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription(), comment}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
		}
		ui.endTest();	
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_entryCommentEdit_visitorAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding a visitor as a member (User 2) and User 3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 create a new activity within this community</B></li>
	*<li><B>Step: testUser1 add an entry</B></li>
	*<li><B>Step: testUser1 comment on the entry</B></li>
	*<li><B>Step: testUser1 edit the comment</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following/ Activities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/9B3BC5A95DCE668585257C890052F9BE">TTT - VISITORS - ACTIVITY STREAM - 00032 - SHARED EXTERNALLY HEADER - ACTIVITY EVENTS - PRIVATE COMMUNITY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_entryCommentEdit_visitorAdded() {

		String testName = ui.startTest();		
		
		// Creating the private visitor enabled community with all setup steps completed	
		BaseCommunity baseCommunity =  CommunityBaseBuilder.buildVisitorModelBaseCommunity(testName + Helper.genStrongRand());
		Community newCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.ACTIVITIES, isOnPremise, testUser1, communitiesAPIUser1);
		CommunityEvents.addMemberMultipleUsers(newCommunity, testUser1, communitiesAPIUser1, MEMBERS_TO_ADD);
		CommunityEvents.followCommunitySingleUser(newCommunity, testUser3, communitiesAPIUser3);
		
		// Add the community to the HashMap for deletion in the AfterClass
		communitiesForDeletion.put(newCommunity, communitiesAPIUser1);
		
		// User 1 will now create an activity in the community
		BaseActivity baseActivity = ActivityBaseBuilder.buildCommunityBaseActivity(testName + Helper.genStrongRand(), baseCommunity);
		Activity newActivity = CommunityActivityEvents.createCommunityActivity(baseActivity, baseCommunity, testUser1, activitiesAPIUser1, communitiesAPIUser1, newCommunity);
		
		// User 1 will now create an entry in the activity, post a comment to the entry and will then edit that comment
		BaseActivityEntry baseActivityEntry = ActivityBaseBuilder.buildBaseActivityEntry(Helper.genStrongRand());
		String comment = Data.getData().commonComment + Helper.genStrongRand();
		String replyEdit = Data.getData().commonComment + Helper.genStrongRand();
		CommunityActivityEvents.createActivityEntryAndAddCommentAndEditComment(testUser1, activitiesAPIUser1, baseActivityEntry, newActivity, comment, false, replyEdit);
		
		// User 3 log in and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story
		String newsStory = ui.replaceNewsStory(Data.COMMENT_ON_THEIR_OWN_ACTIVITY, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String nonEvent = ui.replaceNewsStory(Data.COMMENT_UPDATE_THEIR_OWN_ACTIVITY_ENTRY, baseActivityEntry.getTitle(), baseActivity.getName(), testUser1.getDisplayName());
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStory);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStory);

		// Verify that the "Shared Externally" warning and icon is displayed in all filters
		for(String filter : TEST_FILTERS) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStory, baseActivityEntry.getDescription(), replyEdit}, filter, true);
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{messageCSSSelector, iconCSSSelector}, null, true);
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{nonEvent}, null, false);
		}
		ui.endTest();
	}
}