package com.ibm.conn.auto.tests.commonui.regression;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APISearchHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.GlobalsearchUI.SearchBy;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class Search extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Search.class);
	private TestConfigCustom cfg;
	private GlobalsearchUI searchUI;
	private SearchAdminService adminService;
	private ActivitiesUI actUI;	
	private User itemOwner, adminUser;
	private String testName;
	private String serverURL;	

	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);

		cfg = TestConfigCustom.getInstance();
		searchUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
		itemOwner = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();

	}

	
	/**
	* advancedSearchForActivities_OnPrem() 
	*<ul>
	*<li><B>Info: </B>Create an activity and search for its title, tag and description</li>
	*<li><B>Step: </B>Add an activity using the API</li>
	*<li><B>Step: </B>Create a list of text looking for display name, activity name and description</li> 
	*<li><B>Step: </B>Uncheck all checkboxes</li>
	*<li><B>Step: </B>Select the Activities checkbox</li>
	*<li><B>Step: </B>Enter the title of the activity</li>
	*<li><B>Verify: </B>The search results are present</li>
	*<li><B>Step: </B>Return to the search page</li>
	*<li><B>Step: </B>Enter the description of the activity</li>
	*<li><B>Verify: </B>The search results are present</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Search</a></li>
	*</ul>
	*Note: For OnPrem
	*/ 
	@Test(groups = {"regression"})
	public void advancedSearchForActivities_OnPrem() throws Exception {
		
		BaseActivity baseAct = new BaseActivity.Builder("Search Activity" + Helper.genDateBasedRand())
											   .goal(Data.getData().commonDescription + Helper.genDateBasedRand())
											   .tags("acttag" + Helper.genDateBasedRand()+" acttag")
											   .build();	

		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());

		ArrayList<String> assertList = new ArrayList<String>();
		
		searchUI.startTest();	
				
		//Populate activity
		log.info("INFO: Add activity using API");
		baseAct.createAPI(apiOwner);

		//Run index and find the newly created entry
		log.info("Run index and find the newly created entry");
		APISearchHandler search = new APISearchHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());
		adminService.indexNow("activities", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		boolean found = search.waitForIndexer("activities", baseAct.getName(), 2);
		Assert.assertTrue(found, "Failed to find created entry after indexer ran.");

		//Create list of text to look for that indicates correct result displayed
		log.info("Create list of text to look for that indicates correct result displayed");
		assertList.add(itemOwner.getDisplayName());
		assertList.add(baseAct.getName());
		assertList.add(baseAct.getGoal());
		String description = baseAct.getGoal();
		String descLastWord = description.substring(description.lastIndexOf(' ') + 1);

		//Load Advanced Search page and log in
		log.info("Load Advanced Search page and log in");
		searchUI.loadComponent(Data.getData().ComponentGlobalSearch);
		searchUI.login(itemOwner);

		//Uncheck all checkboxes
		log.info("Uncheck all checkboxes");
		searchUI.advancedSearchClearAll();

		//Check the Activities checkbox and then enter the TITLE of the activity
		log.info("Check the Activities checkbox and then enter the title of the activity");
		searchUI.searchAComponent(SearchBy.TITLE, GlobalsearchUI.ActivitiesCheckbox, baseAct.getName());
		
		//Verify the results
		log.info("Verify the results");
		searchUI.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);
		
		//Return to search
		log.info("Return to search");
		driver.navigate().back();
		searchUI.fluentWaitPresent(GlobalsearchUI.AdvancedSearchTag);
	
		//Check the Activities checkbox and then enter the last word of the unique DESCRIPTION for the activity
		log.info("Check the Activities checkbox and then enter the last word of the unique description for the activity");
		searchUI.searchAComponent(SearchBy.DESCRIPTION, GlobalsearchUI.ActivitiesCheckbox, descLastWord);

		// Verify the results
		log.info("Verify the results");
		searchUI.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);			

		//Clean Up: Delete the activity
		log.info("Delete the activity");
		actUI.clickLinkWait(actUI.getMegaMenuApps());
		actUI.clickLinkWait(ActivitiesUIConstants.activitiesOption);
		baseAct.delete(actUI);	
		
		searchUI.endTest();
	}

	
	/**
	*quickSearchForActivities_Cloud() 
	*<ul>
	*<li><B>Info: </B>Test quick results for view action in Activities</li>
	*<li><B>Step: </B>Login as testUser1 and create activity and activity to do</li>
	*<li><B>Step: </B>Login as testUser2 and click on the items in order to simulate view action</li> 
	*<li><B>Step: </B>Do verification in order to allow data indexed</li> 
	*<li><B>Step: </B>Log out as testUser2 and log back in as testUser1</li>
	*<li><B>Step: </B>Select Search icon</li>
	*<li><B>Verify: </B>Verify that newly created activity appears in the search dropdown</li>
	*<li><B>Verify: </B>Verify that newly created todo appears in the search dropdown</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Search</a></li>
	*</ul>
	*Note: For SmartCloud
	*Note: Disabling this test case as The test is written for quickResults 
	*which requires Elastic Search and the server BVTOracle does not have Elastic Search deployed
	*/
	@Test(groups = { "regressioncloud" }, enabled = false)
	public void quickSearchForActivities_Cloud() {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();

		testName = actUI.startTest();
	
		ActivityMember member2 = new ActivityMember(ActivityRole.AUTHOR, testUser2, ActivityMember.MemberType.PERSON);
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.addMember(member2)
												.build();

		BaseActivityToDo toDo = BaseActivityToDo.builder("toDo" + testName + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.addPerson(testUser2)
												.build();	
		
		
		//Load the component and login as testUser1
		logger.strongStep("Load Activities and Log in as " + testUser1.getDisplayName());
		log.info("INFO: Load Activities and Log in as " + testUser1.getDisplayName());
		actUI.loadComponent(Data.getData().ComponentActivities);
		actUI.login(testUser1);

		//Create new activity
		logger.strongStep("Create new activity " + activity.getName());
		log.info("INFO: Create new activity " + activity.getName());
		activity.create(actUI);
		
		//Create ToDo item
		logger.strongStep("INFO: Create ToDo item");
		log.info("INFO: Create ToDo item");
		toDo.create(actUI);
		
		//Activity owner(testUser1) logs out
		logger.strongStep("INFO: Activity owner logs out");
		log.info("INFO: Activity owner logs out");
		actUI.logout();
			
		//Load the component and login as activity member(testUser2)
		logger.strongStep("Load the component and login as activity member");
		log.info("INFO: Load the component and login as activity member");	
		actUI.loadComponent(Data.getData().ComponentActivities, true);
		actUI.login(testUser2);
				
		logger.strongStep("Open an activity by clicking on activity link");
		log.info("INFO: Open an activity by clicking on activity link");
		actUI.clickLink("link="+activity.getName());
										
		//Check if activity member(testUser2) can see activity to do
		logger.strongStep("Check if activity member can see activity to do");
		log.info("INFO: Check if activity member can see activity to do");
		String todoRootUUID = actUI.getEntryUUID(toDo);
		actUI.expandEntry(todoRootUUID);

		//Note - Doing extra verification in order to allow data indexed
		//Verify the title, tag and description of the to do
		logger.strongStep("Verify the title, tag and description of the to do");
		log.info("INFO: Verify the title, tag and description of the to do");		
		Assert.assertTrue(driver.isTextPresent(toDo.getTitle()), "Title for ToDo is missing");
		if(toDo.getTags() != null)
			Assert.assertTrue(driver.isTextPresent(toDo.getTags()), "Tag for ToDo is missing");
		if(toDo.getDescription() != null)
			Assert.assertTrue(driver.isTextPresent(toDo.getDescription()), "Description for ToDo is missing");

		//Activity member(testUser2) logs out
		logger.strongStep("Activity member logs out");
		log.info("INFO: Activity member logs out");
		actUI.logout();		

		//Log back in as activity owner(testUser1)
		logger.strongStep("Log back in as activity owner");
		log.info("INFO: Log back in as activity owner");
		actUI.loadComponent(Data.getData().ComponentActivities, true);
		actUI.login(testUser1);
		
		//Switch to different Activities view in order to get true search results
		logger.strongStep("Switch to different Activities view in order to get true search results");
		log.info("INFO: Switch to different Activities view in order to get true search results");
		actUI.clickLinkWait(ActivitiesUIConstants.Activity_Template);
		
		//Click on the Search icon
		logger.strongStep("INFO: Click on the search icon");
		log.info("INFO: Click on the search icon");
		actUI.clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		
		//Wait for the search text area to display
		logger.strongStep("Wait for the search text area to display");
		log.info("INFO: Wait for the search text area to display");
		actUI.fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);
		
		//Verify that newly created activity appears in the search dropdown
		logger.strongStep("Verify that newly created activity appears in the search dropdown");
		log.info("Verify that newly created activity appears in the search dropdown");
		Assert.assertTrue(actUI.fluentWaitTextPresent(activity.getName()), "ERROR: newly created activity not present");
		
		//Verify that newly created todo appears in the search dropdown
		logger.strongStep("Verify that newly created todo appears in the search dropdown");
		log.info("Verify that newly created todo appears in the search dropdown");
		Assert.assertTrue(actUI.fluentWaitTextPresent(toDo.getTitle()), "ERROR: newly created todo not present");
		
		//Clean Up: Delete the activity
		logger.strongStep("Switch back to Activities tab ");
		log.info("INFO: Switch back to Activities tab ");
		actUI.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		logger.strongStep("Delete the activity");
		log.info("INFO: Delete the activity");
		activity.delete(actUI);
		
		actUI.endTest();
	}
	
}
