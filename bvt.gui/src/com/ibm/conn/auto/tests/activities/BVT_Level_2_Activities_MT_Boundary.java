package com.ibm.conn.auto.tests.activities;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;


public class BVT_Level_2_Activities_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Activities_MT_Boundary.class);
	private ActivitiesUI ui;

	private TestConfigCustom cfg;
	private User testUser_orgA, testUser_orgB;
	private String serverURL_MT_orgA;
	private APIActivitiesHandler apiOwner;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}

	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that orgA user is not able to add orgB users while creating activity</li>
	 *<li><B>Step: </B> Go to Activities Page</li>
	 *<li><B>Step: </B> Select Start An Activity </li>
	 *<li><B>Step: </B> Enter other details</li>
	 *<li><B>Step: </B> Add orgB user as a member in Activity </li>
	 *<li><B>Verify: </B>Verify that No results found message should be displayed</li>
	 *</ul>
	 */
	@Test(groups = { "mtlevel2" })
	public void addOrgBMember_StartActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		ActivityMember actMember = new ActivityMember(ActivityRole.AUTHOR, testUser_orgB,
				ActivityMember.MemberType.PERSON);

		BaseActivity baseactivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName).addMember(actMember).build();

		// Load the component and login
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentActivities);
		logger.strongStep("Load Activities and Log In as " + testUser_orgA.getDisplayName());
		ui.login(testUser_orgA);

		// Start an Activity
		log.info("INFO: Create an activity");
		logger.strongStep("Start an activity");
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity);
		ui.clickLink(ActivitiesUIConstants.Start_An_Activity);

		// Enter Activity Name
		log.info("INFO: Enter an activity name");
		logger.strongStep("Enter an activity name");
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		ui.clearText(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		ui.typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Name, baseactivity.getName());
       
		// Enter Activity tag name
		log.info("INFO: Enter activity tag name");
		logger.strongStep("Enter activity tag name");
		ui.typeText(ActivitiesUIConstants.Start_An_Activity_InputText_Tags, baseactivity.getTags());
		
		// add orgB user
		log.info("INFO: Enter orgB user's name");
		logger.strongStep("Enter orgB user's name");
		ui.typeTextWithDelay(ActivitiesUIConstants.nameInputField, testUser_orgB.getDisplayName());

		// select the search
		log.info("INFO: Select search");
		logger.strongStep("Select search");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.nameListSearchIcon);

		// Verify no results found while searching orgB users  
		log.info("INFO: Verify that No results found message should be displayed");
		logger.weakStep("Validate that No results found message should be displayed");
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_Activity_NoResultFound);
		Element ele = driver.getFirstElement(ActivitiesUIConstants.Start_Activity_NoResultFound);
		String errorMsg = "No results found for: " + testUser_orgB.getDisplayName() + ".";
		String actualmsg = ele.getText();
		log.info("Msg is:"+ele.getText());
		logger.weakStep("Msg is:" + ele.getText());
		Assert.assertEquals(actualmsg, errorMsg);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that orgA user is not able to add orgB users from My Activities</li>
	 *<li><B>Step: </B> Create new simple activity</li>
	 *<li><B>Step: </B> Open created activity </li>
	 *<li><B>Step: </B> Add orgB user as member</li>
	 *<li><B>Verify: </B>Verify thatNo results found message should be displayed</li>
	 *</ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void addOrgBMember_MyActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		ActivityMember reader = new ActivityMember(ActivityRole.READER, testUser_orgB, ActivityMember.MemberType.PERSON);

		BaseActivity baseactivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName).build();
		
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA,
				testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		// Create activity using API
		log.info("INFO: Create a new Activity using API");
		logger.strongStep("Create a new Acitivty using API");
		Activity activity=baseactivity.createAPI(apiOwner);

		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentActivities);
		ui.login(testUser_orgA);

		// Open activity
		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(baseactivity));
		
		//Select Members from left navigation
		log.info("INFO: Add member " + testUser_orgB.getDisplayName() + "for this Community.");
		logger.strongStep("Add member " + testUser_orgB.getDisplayName() + "for this Community");
		ui.clickLinkWait(ActivitiesUIConstants.CommunityActivities_LeftNav_Members);
		
		//Select add members
		log.info("INFO: Select add members button");
		ui.clickLinkWait(ActivitiesUIConstants.AddMembersButton);
		
		//Select member type and role
		log.info("INFO: Adding user role");
		logger.strongStep("Adding user role");
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberType).useAsDropdown().selectOptionByVisibleText(reader.getMemberType().toString());
		driver.getSingleElement(ActivitiesUIConstants.Start_An_Activity_InputSelect_MemberRole).useAsDropdown().selectOptionByVisibleText(reader.getRole().toString());
		
		//Type in user name in input field
		log.info("INFO: Enter user name");
		logger.strongStep("Enter user name");
		ui.typeTextWithDelay(ActivitiesUIConstants.nameInputField, testUser_orgB.getDisplayName());

		//select the search
		log.info("INFO: Select search");
		logger.strongStep("Select search");
		ui.clickLinkWithJavascript(ActivitiesUIConstants.nameListSearchIcon);
		
		//Verify no results founds while searching orgB users
		log.info("INFO: Verify that No results found message should be displayed");
		logger.weakStep("Validate that No results found message should be displayed");
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_Activity_NoResultFound);
		Element ele = driver.getFirstElement(ActivitiesUIConstants.Start_Activity_NoResultFound);
		String errorMsg = "No results found for: " + testUser_orgB.getDisplayName() + ".";
		String actualmsg = ele.getText();
		log.info("Msg is:"+ele.getText());
		logger.weakStep("Msg is:" + ele.getText());
		Assert.assertEquals(actualmsg, errorMsg);

		//Delete the activity
        apiOwner.deleteActivity(activity);

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B> Test TypeAhead functionality for Activity Entry & ToDoItem in orgA login for OrgB users</li>
	 * <li><B>Step: </B> Create a Activity via API in OrgA.</li>
	 * <li><B>Step: </B> Login to OrgA.</li>
	 * <li><B>Step: </B> Open the Activity Created by API and Click New Entry Button</li>
	 * <li><B>Step: </B> TypeAhead User from OrgB in Description of New Entry</li>
	 * <li><B>Verify: </B> Verify that 'No results found' message should be displayed with option 'Person not listed? Use full search...'</li>
	 * <li><B>Step: </B> Select option 'Person not listed? Use full search...' </li>
	 * <li><B>Verify: </B> Verify 'No results found' message is displayed</li>
	 * <li><B>Step: </B> Click Cancel Button for New Entry.</li>
	 * <li><B>Step: </B> Click New ToDoItem Button</li>
	 * <li><B>Step: </B> TypeAhead User from OrgB in Description of New ToDoItem</li>
	 * <li><B>Verify: </B> Verify that 'No results found' message should be displayed with option 'Person not listed? Use full search...'</li>
	 * <li><B>Step: </B> Select option 'Person not listed? Use full search...' </li>
	 * <li><B>Verify: </B> Verify 'No results found' message is displayed</li>
	 * <li><B>Step: </B> Click Cancel Button for New ToDoItem.</li>
	 * </ul>
	 */	

	@Test(groups = { "mtlevel2" })
	public void typeAheadActivity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseActivity baseactivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand()).tags(testName)
				.goal("Goal for " + testName).build();
		
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA,
				testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

		// Create activity using API
		log.info("INFO: Create a new Activity using API");
		logger.strongStep("Create a new Acitivty using API");
		Activity activity=baseactivity.createAPI(apiOwner);

		// Load the component and login
		logger.strongStep("Load Activities and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentActivities);
		ui.login(testUser_orgA);

		// Open activity
		log.info("INFO: Open the Activity");
		logger.strongStep("Open the Activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(baseactivity));
		
		//Create Entry
		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);
		
		log.info("INFO: Type With Delay @"+testUser_orgB.getUid() +"in Entry Description.");
		logger.strongStep("Type With Delay @"+testUser_orgB.getUid() +"in Entry Description.");
		driver.getSingleElement(ActivitiesUIConstants.New_Entry_Description).typeWithDelay("@"+testUser_orgB.getUid());
		ui.switchToTopFrame();
		
		//Verify the message 'No results found' appears after typing @USERNAME from OrgB in the text field
		log.info("INFO: Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
		logger.strongStep("Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
		Assert.assertTrue(driver.isTextPresent("No results found"),
				"The message 'No results found' does not appear after typing @"+testUser_orgB.getUid());
		
		//Click on the option 'Person not listed? Use full search...'
		log.info("INFO: Click on the option 'Person not listed? Use full search...'");
		logger.strongStep("Click on the option 'Person not listed? Use full search...'");
		ui.clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);

		//Verify that no results are found for user from orgB
		log.info("INFO: Verify that 'No results found' message is displayed");
		logger.strongStep("Validate that 'No results found' message is displayed");
		Assert.assertTrue(driver.isTextPresent("No results found"));
		
		//Click on Cancel Button
		log.info("INFO: Click on the Cancel button");
		logger.strongStep("Cancel the creation of community");
		ui.clickCancelButton();
		
		//Validate2: AddToDO 
		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);
		
		log.info("INFO: Click on ToDo_More_Options button");
		logger.strongStep("Click on ToDo_More_Options button");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
		
		log.info("INFO: Type With Delay @"+testUser_orgB.getUid() +"in Description.");
		logger.strongStep("Type With Delay @"+testUser_orgB.getUid() +"in Description.");
		driver.getSingleElement(ActivitiesUIConstants.New_Entry_Description).typeWithDelay("@"+testUser_orgB.getUid());
		ui.switchToTopFrame();
		
		//Verify the message 'No results found' appears after typing @USERNAME from OrgB in the text field
		log.info("INFO: Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
		logger.strongStep("Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
		Assert.assertTrue(driver.isTextPresent("No results found"),
				"The message 'No results found' does not appear after typing @"+testUser_orgB.getUid());
		
		//Click on the option 'Person not listed? Use full search...'
		log.info("INFO: Click on the option 'Person not listed? Use full search...'");
		logger.strongStep("Click on the option 'Person not listed? Use full search...'");
		ui.clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);

		//Verify that 'No results found' message is displayed for user from orgB
		log.info("INFO: Verify that 'No results found' message is displayed");
		logger.strongStep("Validate that 'No results found' message is displayed");
		Assert.assertTrue(driver.isTextPresent("No results found"));
		
		//Click on Cancel Button
		log.info("INFO: Click on the Cancel button");
		logger.strongStep("Cancel the creation of community");
		ui.clickCancelButton();

		//Delete the activity
		log.info("INFO: Delete the created Activity.");
		logger.strongStep("Delete the created Activity.");
        apiOwner.deleteActivity(activity);

		ui.endTest();

	}
}
