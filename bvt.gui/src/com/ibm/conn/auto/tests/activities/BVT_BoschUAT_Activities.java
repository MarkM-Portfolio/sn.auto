package com.ibm.conn.auto.tests.activities;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_BoschUAT_Activities extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Activities.class);
	private HomepageUI ui;
	private ActivitiesUI activitiesUi;
	private TestConfigCustom cfg;
	private User testUserA,testUserB ;
	private String serverURL;
	private APIActivitiesHandler apiOwner;


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		activitiesUi = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}


	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Mention comments in Activity Entry Comment</li>
	 *<li><B>Step:</B> Login to application and Create a Activity</li> 
	 *<li><B>Step:</B> Create Activity Entry</li>
	 *<li><B>Step:</B> Add a comment with Mentions to the created Activity Entry and Save the Comment</li>
	 *<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyCreateCommentsWithMentionsInActivitiesEntry() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.tags(testName)
				.goal(Data.getData().commonDescription + testName)
				.build();

		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
				.tags(Helper.genDateBasedRandVal())
				.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				.build();

		log.info("INFO: Create a new Activity using API");
		logger.strongStep("INFO: Create a new Acitivty using API");
		activity.createAPI(apiOwner);

		// Load the component and login
		log.info("INFO: Load Activities and Log In as " + testUserA.getDisplayName());
		logger.strongStep("INFO: Load Activities and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUserA);

		log.info("INFO: Open the Activity");
		logger.strongStep("INFO: Open the Activity");
		activitiesUi.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Add entry
		log.info("INFO: Create a new Entry");
		logger.strongStep("INFO: Create a new Entry");
		entry.create(activitiesUi);
		
		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		activitiesUi.clickLinkWait(ActivitiesUIConstants.ActivityAddCommentBtn);
		activitiesUi.typeMentionInCkEditor("Hello @"+testUserB.getDisplayName());
		
		logger.strongStep("INFO: Click n Save button");
		log.info("INFO: Click n Save button");
        activitiesUi.clickLinkWithJavascript(BaseUIConstants.SaveButton);
		
		//Verify that the comment is present
		logger.strongStep("INFO: Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent("Hello "+testUserB.getDisplayName()),
						  "ERROR: Comment not found");
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.mentionLink.replace("PLACEHOLDER", testUserB.getDisplayName())),
				  "ERROR: Mention link not present");

		ui.endTest();	
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Mention comments in Activity To Do section</li>
	 *<li><B>Step:</B> Login to application and Create a Activity</li> 
	 *<li><B>Step:</B> Create Activity To Do section</li>
	 *<li><B>Step:</B> Add a comment with Mentions to the created Activity To Do section and Save the Comment</li>
	 *<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyCreateCommentsWithMentionsInActivitiesToDo() {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
				.tags(testName)
				.goal(Data.getData().commonDescription + testName)
				.build();

		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "toDo" + Helper.genDateBasedRandVal())
				.tags(Helper.genDateBasedRandVal())
				.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
				.build();

		log.info("INFO: Create a new Activity using API");
		logger.strongStep("INFO: Create a new Acitivty using API");
		activity.createAPI(apiOwner);

		// Load the component and login
		log.info("INFO: Load Activities and Log In as " + testUserA.getDisplayName());
		logger.strongStep("INFO: Load Activities and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUserA);

		log.info("INFO: Open the Activity");
		logger.strongStep("INFO: Open the Activity");
		activitiesUi.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Add entry
		log.info("INFO: Add 'todo' for the Activity");
		logger.strongStep("Add 'todo' for the Activity");
		toDo.create(activitiesUi);
		
		log.info("INFO: Get the toDo link to be clicked on");
		logger.strongStep("INFO: Get the toDo link to be clicked on");
		String todoItemLink = ActivitiesUIConstants.Activity_Todo_Item_Link.replaceAll("PLACEHOLDER", toDo.getTitle().trim());
		driver.getFirstElement(todoItemLink).click();

		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		activitiesUi.clickLinkWait(ActivitiesUIConstants.ActivityAddCommentBtn);
		activitiesUi.typeMentionInCkEditor("Hello @"+testUserB.getDisplayName());
		
		logger.strongStep("INFO: Click n Save button");
		log.info("INFO: Click n Save button");
        activitiesUi.clickLinkWithJavascript(BaseUIConstants.SaveButton);

		//Verify that the comment is present
		logger.strongStep("INFO: Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent("Hello "+testUserB.getDisplayName()),
						  "ERROR: Comment not found");
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.mentionLink.replace("PLACEHOLDER", testUserB.getDisplayName())),
				  "ERROR: Mention link not present");

		ui.endTest();	
		
	}

}
