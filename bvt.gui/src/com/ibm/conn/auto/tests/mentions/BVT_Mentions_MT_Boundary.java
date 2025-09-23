package com.ibm.conn.auto.tests.mentions;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;


public class BVT_Mentions_MT_Boundary extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Mentions_MT_Boundary.class);
	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA, testUser_orgB;
	private String serverURL_MT_orgA;
	private APIActivitiesHandler apiOwner;
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL_MT_orgA,
				testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());
	
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	private void validateSearchBox() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		log.info("Info : Presence of search directory is :"+driver.getFirstElement(HomepageUIConstants.nameSearchList).isVisible());
		logger.weakStep("Info : Presence of search directory is :"+driver.getFirstElement(HomepageUIConstants.nameSearchList).isVisible());
		
		if (driver.getFirstElement(HomepageUIConstants.nameSearchList).isVisible()) {

			// select the search
			log.info("Select search");
			logger.strongStep("Select search");
			ui.clickLinkWithJavascript(HomepageUIConstants.nameSearchList);

			// Verify no results found for user from OrgB
			log.info("INFO: Verify that No results found message should be displayed");
			logger.weakStep("Validate that No results found message should be displayed");
			Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.NoResultFound).getText(), "No results found");

		} else {

			Assert.assertFalse(driver.getFirstElement(HomepageUIConstants.nameSearchList).isVisible(),
					"Error: Search box is present");
		}
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test that orgA user can not mention OrgB user form status comment</li>
	 *<li><B>Step:</B> Go to Home Page</li>
	 *<li><B>Step:</B> Mention orgB user from status comment</li>
	 *<li><B>Verify:</B> Verify that 'No results found' message should be displayed</li>
	 *</ul>
	 */
	
	@Test(groups = {"mtlevel2"})
	public void statusCommentMentionsUserB() {
	
	DefectLogger logger = dlog.get(Thread.currentThread().getId());
	
	String mention = Character.toString('@');

	//Load component and login
	log.info("INFO: Log into Homepage as UserA " + testUser_orgA.getDisplayName());
	logger.strongStep("Log into Homepage as UserA " + testUser_orgA.getDisplayName());
	ui.loadComponent(serverURL_MT_orgA,Data.getData().HomepageImFollowing);
	ui.login(testUser_orgA);
	
    //Type in user name 
	logger.strongStep("Mention user" + testUser_orgA.getDisplayName()+"from orgB");
	ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrameNew);
	driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrameNew);
	driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
	log.info("INFO: Types update post message: ");
	driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(mention + testUser_orgB.getDisplayName());
	driver.switchToFrame().returnToTopFrame();
	
	// Validate for search box and search user
	validateSearchBox();
	
	ui.endTest();

	}
	/**
	 *<ul>
	 *<li><B>Info:</B> Test that orgA user can not mention OrgB user form Profile</li>
	 *<li><B>Step:</B> Go to Profile Page</li>
	 *<li><B>Step:</B> Mention orgB user from recent updates</li>
	 *<li><B>Verify:</B> Verify that 'No results found' message should be displayed</li>
	 *</ul>
	 */
	

	@Test(groups = {"mtlevel2"})
	public void profileMentionsUserB() {
		
	DefectLogger logger = dlog.get(Thread.currentThread().getId());
	
	String mention = Character.toString('@');

	//Load profile component and login
	log.info("INFO: Log into Homepage as UserA " + testUser_orgA.getDisplayName());
	logger.strongStep("Log into Homepage as UserA " + testUser_orgA.getDisplayName());
	ui.loadComponent(serverURL_MT_orgA,Data.getData().ComponentProfiles);
	ui.login(testUser_orgA);
	
	//Type in user name
	logger.strongStep("Mention"+ testUser_orgA.getDisplayName()+"user from orgB" );
	ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrameNew);
	driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrameNew);
	driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
	driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(mention + testUser_orgB.getDisplayName());
	driver.switchToFrame().returnToTopFrame();
	
	// Validate for search box and search user
	validateSearchBox();
	
	ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test that orgA user can not mention OrgB user from To Do description</li>
	 *<li><B>Step:</B> [API] Create an activity with name, tag, due date, goal and description</li>
	 *<li><B>Step:</B> Add To Do item in created activity</li>
	 *<li><B>Step:</B> Mention orgB user from To Do description</li>
	 *<li><B>Verify:</B> Verify that 'No results found' message should be displayed</li>
	 *</ul>
	 */
	
	@Test(groups = {"mtlevel2"})
	public void toDoDesMentionsUserB() {

			
			DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
			String testName = ui.startTest();
			
			String mention = Character.toString('@');
		
			ActivityMember member = new ActivityMember(ActivityRole.OWNER, testUser_orgA, ActivityMember.MemberType.PERSON);
			
			BaseActivity baseactivity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
					.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
					.dueDateRandom()
					.useCalPick(true)
					.goal(Data.getData().commonDescription + testName)
					.addMember(member)
					.build();
			
			// Create activity using API
			log.info("INFO: Create a new Activity using API");
			logger.strongStep("Create a new Acitivty using API");
			Activity activity=baseactivity.createAPI(apiOwner);
		
			// Load the component and login
			logger.strongStep("Load Activities and Log In as " + testUser_orgA.getDisplayName());
			ui.loadComponent(serverURL_MT_orgA,Data.getData().ComponentActivities);
			ui.login(testUser_orgA);
			
			// Open the activity
			log.info("INFO: Open the Activity");
			logger.strongStep("Open the Activity");
			ui.clickLinkWait(ActivitiesUI.getActivityLink(baseactivity));
			
			// Select Add to do item
			log.info("INFO: Select Add To Do item");
			logger.strongStep("Select Add To Do item");
			ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

			// Enter To Do title
			log.info("INFO: Enter To Do title");
			logger.strongStep("Enter Add To Do title");
			ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, testName+"ToDO");
			
			ui.fluentWaitPresent(ActivitiesUIConstants.ToDo_More_Options);
			
			//Select To Do more options
			log.info("INFO: Select To Do more options");
			logger.strongStep("Select To Do more options");
			ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);
			
			// Enter To Do tag
			log.info("INFO: Enter To Do tag");
			logger.strongStep("Open the Activity");
			ui.typeText(ActivitiesUIConstants.ToDo_InputText_Tags, testName+"Tag");
			
			// Enter To-Do description
			log.info("INFO: Mention orgB user in decsription ");
			ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
			driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
			log.info("INFO: Mention user in description box: ");
			logger.strongStep("Mention user from another org in description box");
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(mention + testUser_orgB.getDisplayName());
			driver.switchToFrame().returnToTopFrame();

			// Validate for search box and search user
			 validateSearchBox();
			
			//Delete the activity
			apiOwner.deleteActivity(activity);

			ui.endTest();
	}
}