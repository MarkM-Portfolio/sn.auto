package com.ibm.conn.auto.tests.activities;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Activity_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Activities extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Activities.class);
	private ActivitiesUI ui;
	private CommunitiesUI comUI;
	private TestConfigCustom cfg;
	private APIActivitiesHandler apiOwner;
	private APICommunitiesHandler apiComOwner;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, guestUser;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	
	 
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		testUser5 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiComOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Validate the Mega Menu</li>
	 *<li><B>Verify: </B>Verify mega menu Activity options</li>
	 *<li><B>Verify: </B>Verify mega menu item for Activities</li>
	 *<li><B>Verify: </B>Verify mega menu item for To Do List</li>
	 *<li><B>Verify: </B>Verify mega menu item for High Priority Activities</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "bvt", "regressioncloud"})
	public void validateMegaMenu() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
	
		ui.startTest();
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);
		
		//Click Mega Menu item
		log.info("INFO: Select the Activities 'Mega Menu' option");
		logger.strongStep("Select the Activities 'Mega Menu' option");
		ui.clickLinkWait(ui.getMegaMenuApps());	
		
		//Validate Activities option is contained with in drop down menu
		log.info("INFO: Validate that the 'Activities' option is contained within the drop down menu");
		logger.weakStep("Valdiate that the 'Activities' option is contatined within the drop down menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		
		// It's possible that both classic Activities and Activities+ exist in the menu
		// This test is to look for the classic Activities
		List<Element> activitiesInMenu = driver.getVisibleElements(ui.getActivitiesOption());
		boolean activitiesFound = false;
		for (Element menuItem : activitiesInMenu)  {
			if (menuItem.getText().equals("Activities")) {
				activitiesFound = true;
				break;
			}
		}
		Assert.assertTrue(activitiesFound,
						  "Unable to locate Mega Menu 'Activities' option in the drop down menu");

		//Validate To Do List option is contained with in drop down menu
		log.info("INFO: Validate that the 'To Do List' option is contained within the drop down menu");
		logger.weakStep("Validate that the 'To Do List' option is contained within the drop down menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());	
		Assert.assertTrue(ui.fluentWaitPresent(ui.getActivitiesToDoList()),
						  "Unable to locate Mega Menu 'To Do List' option in the drop down menu");

		//Validate High Priority Activities option is contained with in drop down menu
		log.info("INFO: Validate that the 'High Priority Activities' option is contained within the drop down menu");
		logger.weakStep("Validate that the 'High Priority Activities' option is contained within the drop down menu");
		ui.selectMegaMenu(ui.getMegaMenuApps());
		Assert.assertTrue(ui.fluentWaitPresent(ui.getActivitiesHighPriorityAct()),
						  "Unable to locate Mega Menu 'High Priority Activities' option in the drop down menu");

		ui.endTest();
	
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test that an activity can be created</li>
	 *<li><B>Step:</B> Go to Activities Page</li>
	 *<li><B>Step:</B> Create an activity</li>
	 *<li><B>Verify:</B> An activity has been created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "bvtcloud", "regressioncloud", "smokeonprem", "smokecloud"})
	public void createActivity() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		ActivityMember member2 = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
		ActivityMember member3 = new ActivityMember(ActivityRole.READER, testUser3, ActivityMember.MemberType.PERSON);
		ActivityMember member4 = new ActivityMember(ActivityRole.AUTHOR, testUser4, ActivityMember.MemberType.PERSON);
		ActivityMember member5 = new ActivityMember(ActivityRole.OWNER, testUser5, ActivityMember.MemberType.PERSON);
		
		
		List<ActivityMember> list = new ArrayList<ActivityMember>();
		list.add(member2);
		list.add(member3);
		list.add(member4);
		list.add(member5);
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.dueDateRandom()
												.useCalPick(true)
												.goal(Data.getData().commonDescription + testName)
												.addMembers(list)
												.build();
		
		// Load the component and login 
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Start an activity
		log.info("INFO: Creating a new Activity: " + activity.getName());
		logger.strongStep("Create a new Activity");
		activity.create(ui);

		//Go back to activities list
		logger.strongStep("Go back to the Acitivities list");
		ui.fluentWaitTextPresent("Activity Goal");
		if(cfg.getUseNewUI())
			ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.activityTabNewUI), 4, "Click on Activity tab");
		else
			ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		//Validate activity
        logger.weakStep("Validate that the Activity is present");
		ui.verifyActivityInfo(logger, activity);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Editing an Activity</li>
	 *<li><B>Step: </B>Create a new Activity</li>
	 *<li><B>Step: </B>Edit the new Activity</li>
	 *<li><B>Verify: </B>Verify that Activity has being edited successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void editActivity() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal(Data.getData().commonDescription + testName)
												.build();

		String newGoal = "modified description for " + activity.getName();
		
		log.info("INFO: Create a new Activity using API");
		logger.strongStep("Create a new Acitivty using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		//Go to Activities main page to see the activity
		logger.strongStep("Go to the Activities main page to see the Activity");
		if(cfg.getUseNewUI())
			ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.activityTabNewUI), 4, "Click on Activity tab");
		else
			ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		//edit description
		log.info("INFO: Edit the Activity goal from '" + activity.getGoal() + "' to '" + newGoal + "'");
		logger.strongStep("Edit Description from the current Activity Goal to the New Activity Goal");
		activity.editGoal(ui, newGoal);
		
		//validate Activity info
		log.info("INFO: Validate that the Activity is present");
		logger.strongStep("Validate that the Activity is present");
		ui.verifyActivityInfo(logger , activity);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Deleting an Activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Delete the new activity.</li>
	 *<li><B>Verify: </B>Verify that Activity has being deleted successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void deleteActivity() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		//Go to Activities list
		logger.strongStep("Go to the Activities List");
		if(cfg.getUseNewUI())
			ui.clickLinkWaitWd(By.xpath(ActivitiesUIConstants.activityTabNewUI), 4, "Click on Activity tab");
		else
			ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		logger.strongStep("Delete the Activity");
		activity.delete(ui);
		
		//Go to trash folder and verify deleted activity is present
		logger.weakStep("Verify that the Activity is in the trash");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_LeftNav_Trash);
		Assert.assertTrue(driver.isTextPresent(activity.getName()), 
						  "Deleted Activity was not found in trash. Activity name: " + activity.getName());
		
		log.info("INFO: Verified that the Activity is now in the trash");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Adding an Entry with a file to an activity</li>
	 *<li><B>Step:</B> Create a new simple activity</li>
	 *<li><B>Step:</B> Add an entry with a file</li>
	 *<li><B>Verify:</B> The Entry has being created along with an attached file successfully</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/C5D5B3407CC5D2B285257D50005A82D1/34843151B1FCC5F085257D500059555B">TTT Link to BVT test case</a></li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "smokeonprem", "regressioncloud", "bvtcloud", "smokecloud"})
	public void EntryAddFile() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

         
		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New entry for activity created above
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .addFile(Data.getData().file1)
												   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												   .build();

		//Add entry
		log.info("INFO: Create a new Entry with a file");
		logger.strongStep("Create a new Entry with a file");
		entry.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Verify entry info
		log.info("INFO: Validate that the Entry is present");
		logger.strongStep("Validate that the Entry is present");
		ui.validatePageInfo(logger,entry);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding a bookmark entry to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create an entry in that activity</li>
	 *<li><B>Verify: </B>Verify that Entry has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void EntryBookmark() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();
		
		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Acitvities and Log In as " + testUser1.getDisplayName()); 
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New entry for activity created above	
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
												   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												   .build();

		//Add entry
		log.info("INFO: Create a new Entry with a bookmark for the Activity");
		logger.strongStep("Create a new Entry with a bookmark for the Activity");
		entry.create(ui);

		
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Verify entry info
		log.info("INFO: Validate that the Entry is present");
		logger.strongStep("Validate that the Entry is present");
		ui.validatePageInfo(logger,entry);

		ui.endTest();
	}	

	/**
	 *<ul>
	 *<li><B>Info: </B>Adding an entry to an activity with Custom Fields</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create an entry in that activity</li>
	 *<li><B>Verify: </B>Verify that Entry has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void EntryCustomField() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();
		
		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New entry for activity created above
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .customText(Data.getData().CustomFieldName, "Custom text for " + testName )
												   .dateRandom()
												   .useCalPick(true)
												   .description(Data.getData().commonDescription + testName)
												   .build();

		//Add entry
		log.info("INFO: Create a new Custom Field Entry for the Activity");
		logger.strongStep("Create a new Custom Field Entry for the Activity");
		entry.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Verify entry info
		log.info("INFO: Validate that the Entry is present");
		logger.strongStep("Validate that the Entry is present");
		ui.validatePageInfo(logger,entry);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding an Entry Notify to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity </li>
	 *<li><B>Step: </B>Create an entry in that activity</li>
	 *<li><B>Step: </B>Set entry to Notify others</li>
	 *<li><B>Verify: </B>Verify the entry has been created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void EntryNotify() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New entry for activity created above
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												   .notifyPeople()
												   .notifyAllPeople()
												   .notifyMessage(Data.getData().commonComment)
												   .build();
		
		//Add entry
		log.info("INFO: Create a new Notify Entry for the Activity");
		logger.strongStep("Create a new Notify Entry for the Activity");
		entry.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Verify entry info
		log.info("INFO: Validate that the Entry is present");
		logger.strongStep("Validate the the Entry is present");
		ui.validatePageInfo(logger,entry);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Adding a ToDo Add file to an activity</li>
	 *<li><B>Step:</B> Create a new simple activity</li>
	 *<li><B>Step:</B> Create a ToDo in that activity with the file you wish to add</li>
	 *<li><B>Step:</B> Select the more link for the ToDo</li>
	 *<li><B>Verify:</B> The ToDo has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "smokeonprem", "regressioncloud", "bvtcloud", "smokecloud"})
	public void ToDoAddFile() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();


		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "toDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();

		//Add todo
		log.info("INFO: Add 'todo:Add File' for the Activity");
		logger.strongStep("Add 'todo:Add File' for the Activity");
		toDo.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Select more link for todo
		log.info("INFO: Select the 'More' link for the todo");
		logger.strongStep("Select the 'More' link for the todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		//Verify entry info
		log.info("INFO: Validate that the 'todo' is present");
		logger.strongStep("Validate that the 'todo' is present");
		ui.validatePageInfo(logger,toDo);
		ui.clickCancelButton();

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding a Todo Bookmark to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create a Todo in that activity with the bookmark you wish to have</li>
	 *<li><B>Step: </B>Select the more link for the Todo</li>
	 *<li><B>Verify: </B>Verify that the Todo has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void ToDoBookmark() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New todo for activity created above
	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "toDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.bookmark(Helper.genDateBasedRandVal() + "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();

		//Add entry
		log.info("INFO: Create a new 'todo:bookmark' for the Activity");
		logger.strongStep("Create a new 'todo:bookmark' for the Activity");
		toDo.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Select more link for todo
		log.info("INFO: Select the 'More' link of the todo");
		logger.strongStep("Select the 'More' link of the todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		//Verify entry info
		log.info("INFO: Validate that the 'todo' is present");
		logger.strongStep("Validate that the 'todo' is present");
		ui.validatePageInfo(logger,toDo);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding a Todo Custom Field to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create a Todo in that activity with the custom field you wish to have</li>
	 *<li><B>Step: </B>Select the more link for the Todo</li>
	 *<li><B>Verify: </B>Verify that the Todo has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void ToDoCustomField() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();
		
		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New entry for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + " entry" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.customText(Data.getData().CustomFieldName, "Custom text for " + testName )
												.dateRandom()
												.description(Data.getData().commonDescription + testName)
												.build();

		//Add entry
		log.info("INFO: Create a 'Todo:Custom Field' for the Activity");
		logger.strongStep("Create a 'Todo:Custom Field' for the Activity");
		toDo.create(ui);
		
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Select more link for todo
		log.info("INFO: Select the 'More' link of the todo");
		logger.strongStep("Select the 'More' link of the todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		//Verify entry info
		log.info("INFO: Validate that the 'todo' is present");
		logger.strongStep("Validate that the 'todo' is present");
		ui.validatePageInfo(logger,toDo);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding a Todo Notify to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create a Todo in that activity with the people you with to notify</li>
	 *<li><B>Step: </B>Select the more link for the Todo</li>
	 *<li><B>Verify: </B>Verify that the Todo has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2","cnx8ui-level2", "bvt", "regressioncloud"})
	public void ToDoNotify() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create an Activity using API");	
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New todo for activity created above		
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "toDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.notifyPeople()
												.notifyAllPeople()
												.notifyMessage("notify me")
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();

		//Add entry
		log.info("INFO: Create a new 'Todo:Notify' for the Activity");
		logger.strongStep("Create a new 'Todo:Notify' for the Activity");
		toDo.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Select more link for todo
		log.info("INFO: Select the 'More' link of the todo");
		logger.strongStep("Select the 'More' link of the todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		//Verify entry info
		log.info("INFO: Validate that the 'todo' is present");
		logger.strongStep("Validate that the 'todo' is present");
		ui.validatePageInfo(logger,toDo);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding a Todo Due Date to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create a Todo in that activity with the Due Date you wish to have</li>
	 *<li><B>Step: </B>Select the more link for the Todo</li>
	 *<li><B>Verify: </B>Verify that the Todo has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "bvt", "regressioncloud"})
	public void ToDoDueDate() {
    DefectLogger logger=dlog.get(Thread.currentThread().getId());
    
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();


		log.info("INFO: Create an Activity using API");	
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Create New todo for activity created above
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "toDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.dueDateRandom()
												.useCalPick(true)
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();

		//Add entry
		log.info("INFO: Create a 'Todo:Due Date' for the Activity");
		logger.strongStep("Create a 'Todo:Due Date' for the Activity");
		toDo.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Select more link for todo
		log.info("INFO: Select the 'More' link of the todo");
		logger.strongStep("Select the 'More' link of the todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		//Verify entry info
		log.info("INFO: Validate that the 'todo' is present");
		logger.strongStep("Validate that the 'todo' is present");
		ui.validatePageInfo(logger,toDo);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Adding an Entry with a file to a community activity</li>
	 *<li><B>Step: </B>Create a community</li>
	 *<li><B>Step: </B>Add the activity widget</li>
	 *<li><B>Step: </B>Create a community activity</li>
	 *<li><B>Step: </B>Add an entry with a file</li>
	 *<li><B>Verify: </B>The entry was created containing the attached file </li>>
	 *</ul>
	 */
	@Test(groups = {"level2", "cnx8ui-level2","regressioncloud", "bvt"})
	public void addComActivityEntryWithImage() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.access(defaultAccess)
		   											.build();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.goal("Goal for "+ testName)
												.community(community)
												.build();

		//Create community
		log.info("INFO: Create a Community using API");
		logger.strongStep("Create a Community using API");
		Community comAPI = community.createAPI(apiComOwner);

		//Add the events widget
		log.info("INFO: Add the 'Events' widget to the Community using API");
		logger.strongStep("Add the 'Events' widget to the Community using API");
		community.addWidgetAPI(comAPI, apiComOwner, BaseWidget.ACTIVITIES);

		//Add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiComOwner, comAPI);
		
		//Create activity
		log.info("INFO: Create an Activity using API");	
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner, community);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		//Check Gatekeeper value for Communities Tabbed Nav setting
		User adminUser;
		GatekeeperConfig gkc;
		String gk_flag = "communities-tabbed-nav";
		String gk_flag_card = "catalog-card-view";
		adminUser = cfg.getUserAllocator().getAdminUser();
		log.info("INFO: Check to see if the Gatekeeper " +gk_flag + " setting is enabled");
		if(cfg.getProductName().equalsIgnoreCase("onprem")){
			gk_flag = "COMMUNITIES_TABBED_NAV";
			gk_flag_card = "CATALOG_CARD_VIEW";
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else{
			gkc = GatekeeperConfig.getInstance(driver);
		}
		boolean value = gkc.getSetting(gk_flag);
		boolean isCardView = gkc.getSetting(gk_flag_card);

		
		//Navigate to owned communities
		log.info("INFO: Navigate to the 'Owned Communities' view");
		logger.strongStep("Navigate to the 'Owned Communities' view");
		comUI.goToDefaultIamOwnerView(isCardView);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiComOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// Navigate to the API community
		log.info("INFO: Navigate to the Community using UUID");
		logger.strongStep("Navigate to the API Community");
		community.navViaUUID(comUI);		
				
		ui.fluentWaitPresent(ActivitiesUI.getActivityLink(activity));

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Activities link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			log.info("INFO: Select 'Activites' in the tabbed nav panel");
			logger.strongStep("Select 'Activities' in the tabbed nav panel");
			Community_LeftNav_Menu.ACTIVITIES.select(comUI);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			log.info("INFO: Select 'Activites' in the left nav panel");
			logger.strongStep("Select 'Activities' in the left nav panel");
			Community_LeftNav_Menu.ACTIVITIES.select(comUI);
		}

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getCommunityActivityLink(activity));

		
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												.addFile(Data.getData().file3)
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();

		//Add entry
		log.info("INFO: Create a new Entry with a file");
		logger.strongStep("Add a new Entry with a file");
		entry.create(ui);
	
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		log.info("INFO: Verify that the file link displays");
		logger.weakStep("Verify that the file link displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().file3), 
						  "ERROR: " + Data.getData().file3 + " link is missing from entry");
		
		logger.strongStep("Delete the Community");
		apiComOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Creating a section in an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Create a new section in that activity</li>
	 *<li><B>Verify: </B>Verify that Section has being created successfully</li>
	 *</ul>
	 */
	@Test(groups = {"level2", "cnx8ui-level2","bvt", "regressioncloud"})
	public void createSection() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String sectionTitle = Data.getData().Section_InputText_Title_Data + Helper.genDateBasedRandVal();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();

		log.info("INFO: Create an Activity using API");
		logger.strongStep("Create an Activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		ui.loadComponent(Data.getData().ComponentActivities);
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		//create new section
		log.info("INFO: Add a section to the Activity");
		logger.strongStep("Add a new section to the Activity");
		ui.addSection(sectionTitle);
		
		log.info("INFO: Look for 'Section Action Menu'");
		logger.strongStep("Look for 'Section Action Menu'");
		ui.fluentWaitPresent(ActivitiesUIConstants.Section_Action_Menu);
		
		//Verify section title
		log.info("INFO: Verify that the section title is present");
		logger.weakStep("Verify that the section title is present");
		Assert.assertTrue(driver.isTextPresent(sectionTitle), 
						  "Title for Section is missing");

		ui.endTest();
	}

		
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/	
	/**
	 *<ul>
	 *<li><B>Info:</B> Create and then search for an external activity.</li>
	 *<li><B>Step:</B> Create an External Activity.</li> 
	 *<li><B>Step:</B> Click the Activities Tab.</li> 
	 *<li><B>Verify:</B> The external activity was created.</li>
	 *<li><B>Clean Up:</B> Delete the Activity.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void externalActivityCreation() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.build();


		//GUI
		// Load the component and login
		ui.loadComponent(Data.getData().ComponentActivities);
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.login(testUser1);

		log.info("INFO: Create an external Activity");
		logger.strongStep("Create an external Activity");
		activity.create(ui);

		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.activityTab);
		
		log.info("INFO: Validate that the Activity is an external Activity by finding the icon"); 
		logger.weakStep("Validate that the Activity is an external Activity by finding the icon");
		Assert.assertTrue(ui.fluentWaitPresent(ui.getActivityExternalLabel(activity)),
											  "ERROR: The External Activity icon was not present.");
		
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		log.info("INFO: Delete the Activity");
		logger.strongStep("Delete the Activity");
		activity.delete(ui);
		
		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Creating an Internal Activity</li>
	 *<li><B>Step:</B> Create an Internal Activity.</li> 
	 *<li><B>Step:</B> Go to the Activities Tab.</li> 
	 *<li><B>Verify:</B> Internal Activity creation.</li>
	 *<li><B>Clean Up:</B> Delete the Activity.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void internalActivityCreation() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.shareExternal(false)
												.build();


		//GUI
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Create an internal Activity");
		logger.strongStep("Create an internal Activity");
		activity.create(ui);

		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.activityTab);

		log.info("INFO: Validate that the Activity is an internal Activity by finding the icon"); 
		logger.weakStep("Validate that the Activity is an internal Activity by finding the icon");
		Assert.assertFalse(driver.isElementPresent(ui.getActivityExternalLabel(activity)),
							"ERROR: The internal Activity icon was not present.");
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		log.info("INFO: Delete the Activity");
		logger.strongStep("Delete the Activity");
		activity.delete(ui);
		
		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Adding an activity Member as an author</li>
	 *<li><B>Step:</B> Create an Activity.</li> 
	 *<li><B>Step:</B> Go to the Members Tab.</li>
	 *<li><B>Step:</B> Go to the Activities Tab.</li>
	 *<li><B>Verify:</B> The activity member is an author.</li> 
	 *<li><B>Clean Up:</B> Delete the Activity.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void addAuthorMember() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		ActivityMember actMember = new ActivityMember(ActivityRole.AUTHOR, testUser2, ActivityMember.MemberType.PERSON);

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.addMember(actMember)
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.shareExternal(false)
												.build();


		//GUI
		// Load the component and login
		ui.loadComponent(Data.getData().ComponentActivities);
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.login(testUser1);

		log.info("INFO: Create an Activity");
		logger.strongStep("Create an Activity");
		activity.create(ui);

		log.info("INFO: Select Members from left navigation menu");
		logger.strongStep("Select Members from left navigation menu");
		Activity_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Locate the Activity member");
		logger.strongStep("Locate the Activity member");
		Element user = ui.getMemberElement(actMember.getUser());
		
		log.info("INFO: Validate that the user is an author");
		logger.weakStep("Validate that the user is an author");
		Assert.assertTrue(user.getText().contains(actMember.getRole().toString()),
						  "ERROR: User is not listed as an Author");	
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		log.info("INFO: Delete the Activity");
		logger.strongStep("Delete the Activity");
		activity.delete(ui);
		
		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Adding Activities members as Owners </li>
	 *<li><B>Step:</B> Create an Activity.</li> 
	 *<li><B>Step:</B> Go to the Members Tab.</li>
	 *<li><B>Step:</B> Go to the Activities Tab.</li>
	 *<li><B>Verify:</B> The activity member is an owner.</li> 
	 *<li><B>Clean Up:</B> Delete the Activity.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void addOwnerMember() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		ActivityMember actMember = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
				
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.addMember(actMember)
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.shareExternal(false)
												.build();


		//GUI
		// Load the component and login
		logger.strongStep("Load Acitivities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Create an Activity");
		logger.strongStep("Create an Activity");
		activity.create(ui);

		log.info("INFO: Select Members from left navigation menu");
		logger.strongStep("Select Members from left navigation menu");
		Activity_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Locate the Activity member");
		logger.strongStep("Locate the Activity member");
		Element user = ui.getMemberElement(actMember.getUser());
		
		log.info("INFO: Validate that the member is an owner");
		logger.weakStep("Validate that the member is an owner");
		Assert.assertTrue(user.getText().contains(actMember.getRole().toString()),
						  "ERROR: Member is not listed as an owner");	
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		log.info("INFO: Delete the Activity");
		logger.strongStep("Delete the Activity");
		activity.delete(ui);
		
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Add an Activity Member as a reader</li>
	 *<li><B>Step:</B> Create an Activity.</li> 
	 *<li><B>Step:</B> Go to the Members Tab.</li>
	 *<li><B>Step:</B> Go to the Activities Tab.</li>
	 *<li><B>Verify:</B> The activity member is a reader.</li> 
	 *<li><B>Clean Up:</B> Delete the Activity.</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void addReaderMember() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		ActivityMember actMember = new ActivityMember(ActivityRole.READER, testUser2, ActivityMember.MemberType.PERSON);
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.addMember(actMember)
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.shareExternal(false)
												.build();


		//GUI
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		log.info("INFO: Create an Activity");
		logger.strongStep("Create an Activity");
		activity.create(ui);

		log.info("INFO: Select Members from left navigation menu");
		
		Activity_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Locate the Activity member");
		logger.strongStep("Locate the Activity member");
		Element user = ui.getMemberElement(actMember.getUser());
		
		log.info("INFO: Validate that the member is a Reader");
		logger.weakStep("Validate that the member is a Reader");
		Assert.assertTrue(user.getText().contains(actMember.getRole().toString()),
						  "ERROR: Member is not listed as a Reader");	
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		log.info("INFO: Delete the Activity");
		logger.strongStep("Delete the Activity");
		activity.delete(ui);
		
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Create Activity with a member and assign them a ToDo task</li>
	 *<li><B>Step:</B> Create an activity.</li> 
	 *<li><B>Step:</B> Assign a To Do to the added member.</li>
	 *<li><B>Step:</B> Select More Actions</li>
	 *<li><B>Step:</B> Select the More Link on the ToDo.</li>
	 *<li><B>Step:</B> Logout as the first user.</li>
	 *<li><B>Step:</B> Login as the second user.</li>
	 *<li><B>Step:</B> Click on the ToDo List Tab.</li>
	 *<li><B>Verify:</B> The ToDo is present.</li>
	 *<li><B>Verify:</B> The contents of the ToDo assigned.</li> 
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void assignToDo() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		String testName = ui.startTest();
	
		ActivityMember member2 = new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON);
	
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal())
												.addMember(member2)
												.build();
	
		//GUI
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);
	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName+ "ToDo" + Helper.genDateBasedRandVal())
												.tags(testName + Helper.genDateBasedRandVal())
												.assignTo(testUser2)
												.addFile(Data.getData().file1)
												.bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.dateRandom()
												.build();
		
		
		
		//Create A New External Activity
		log.info("INFO: Create an external Activity");
		logger.strongStep("Create an external Activity");
		activity.create(ui);
	
		//Add A To Do To The Added Member
		log.info("INFO: Add a ToDo to the added member " + testUser2.getDisplayName()); 
		logger.strongStep("Assign a ToDo to the added member " + testUser2.getDisplayName());
		toDo.create(ui);
	
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
	
		//Select more link for todo
		log.info("INFO: Select the 'More' link of the todo");
		logger.strongStep("Select the 'More' link for todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
	
		//Logout as current user
		log.info("INFO: Log Out from " + testUser1.getDisplayName());
		logger.strongStep("Log Out from " + testUser1.getDisplayName());
		ui.logout();
	
		//login as the second user
		log.info("INFO: Log In as " + testUser1.getDisplayName());
		logger.strongStep("Log In as " + testUser1.getDisplayName());
		ui.login(testUser2);
	
		//check that the activity/todo is present
		log.info("INFO: Click on the Activity's 'ToDo' tab");
		logger.strongStep("Click on the Activity's 'ToDo' tab");
		ui.clickLink(ActivitiesUIConstants.ToDoListTab);
	
	
		log.info("INFO: Validate the todo is present");
		logger.weakStep("Validate the todo is present");
		Assert.assertTrue(driver.isTextPresent(toDo.getTitle()),
						  "ERROR: Todo was not found on page");
		
		//Select the ToDo and expand it and then verify the contents of the ToDo
		log.info("INFO: Validate the contents of the assigned ToDo");
		logger.strongStep("Validate the contents of the assigned ToDo ");
		ui.clickLink(ui.getAssignedTodoByName(toDo));
	
		logger.weakStep("Validate that the title for the ToDo is present");
		Assert.assertTrue(driver.isTextPresent(toDo.getTitle()), 
						  "ERROR: Title for the ToDo is missing");
	    logger.weakStep("Validate that the tags for the ToDo are present");
		Assert.assertTrue(driver.isTextPresent(toDo.getTags()), 
						  "ERROR: Tags for the ToDo is missing");
	    logger.weakStep("Validate that the Description for the ToDo is present");
		Assert.assertTrue(driver.isTextPresent(toDo.getDescription()), 
						  "ERROR: Description for the ToDo is missing");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Create internal activity and verify the Change Permissions Link.</B></li>
	 *<li><B>Step:</B> Create an internal activity.</B></li> 
	 *<li><B>Verify:</B> The permission messsage'Activity content will be seen by people both inside and outside of your organization.' is no longer present</B></li> 
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void internalActivityMessage() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		ui.startTest();
	
		BaseActivity activity = new BaseActivity.Builder("")
												.shareExternal(false)
												.build();
		
		
		//GUI
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);
	
		//Start an Activity
		log.info("INFO: Create an Activity");
		logger.strongStep("Create an Activity");
		ui.clickLinkWait(ActivitiesUIConstants.Start_An_Activity);
	
		//Make sure it's an internal activity
		log.info("INFO: Select to not share Activity with people outside of the organization");
		logger.strongStep("Select to not share the Activity with people outside of the organization");
		ui.checkPermission(activity);
	
		//Verify The Permissions message is no longer present
		log.info("INFO: Verify The Permission message 'Activity content will be seen by people both inside and outside of your organization.' is not present");
		logger.weakStep("Verify The Permissions message is no longer present");
		Assert.assertFalse(driver.getSingleElement(ActivitiesUIConstants.Permissions_Warning).isVisible(),
						"ERROR: The Change Permissions warning 'Activity content will be seen by people both inside and outside of your organization.' was visible.");
	
		ui.endTest();
	
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Test that a guest user can see activity page</li>
	 *<li><B>Step:</B> Log in as a guest</li> 
	 *<li><B>Step:</B> Go to Activities page</li> 
	 *<li><B>Verify:</B> The "Start an Activity" button is present</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void guestActivity() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
	
		ui.startTest();
		guestUser = cfg.getUserAllocator().getGuestUser();
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + guestUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(guestUser);

		log.info("INFO: Verify the Activities page has the Start an Activity button");
	
		logger.weakStep("Validate that the Start an Activity button is present on the page");
		Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity),
										  "ERROR: The create Activity button was not present.");
	
		ui.endTest();
	}	
	

}
