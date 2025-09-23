package com.ibm.conn.auto.tests.globalsearch.regression;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APISearchHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.GlobalsearchUI.SearchBy;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class Regression_Global_Search extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Regression_Global_Search.class);
	private TestConfigCustom cfg;
	private GlobalsearchUI ui;
	private SearchAdminService adminService;

	private User itemOwner, adminUser;
	private String serverURL;	


	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);

		cfg = TestConfigCustom.getInstance();
		ui = GlobalsearchUI.getGui(cfg.getProductName(), driver);

		itemOwner = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();


		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();

	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Create a todo entry for an activity and search for it</li>
	 *<li><B>Step: </B>Add an activity using the API</li>
	 *<li><B>Step: </B>Add a todo entry to the activity</li>
	 *<li><B>Step: </B>Create a list of text looking for Display Name, name, and description</li> 
	 *<li><B>Step: </B>Uncheck all checkboxes</li>
	 *<li><B>Step: </B>Enter a unique tag for the Activities checkbox</li>
	 *<li><B>Step: </B>Search for the tag</li>
	 *<li><B>Verify: </B>The search results are present</li>
	 *<li><B>Step: </B>Return to the search page</li>
	 *<li><B>Step: </B>Uncheck all checkboxes</li>
	 *<li><B>Step: </B>Enter a the title of the todo entry for the Activities checkbox</li>
	 *<li><B>Step: </B>Search for the tag</li>
	 *<li><B>Verify: </B>The search results are present</li>
	 *<li><B>Step: </B>Return to the search page</li>
	 *<li><B>Step: </B>Uncheck all checkboxes</li>
	 *<li><B>Step: </B>Enter a unique word in the todo entry's description for the Activities checkbox</li>
	 *<li><B>Step: </B>Search for the description</li>
	 *<li><B>Verify: </B>The search results are present</li>
	 *</ul>
	 */ 
	@Test(groups = { "regression" })
	public void advancedSearchForActivityToDo() throws Exception {

		BaseActivity baseAct = new BaseActivity.Builder("Search Activity" + Helper.genDateBasedRand())
		.goal(Data.getData().commonDescription + Helper.genDateBasedRand())
		.tags("acttag" + Helper.genDateBasedRand()+" acttag")
		.build();

		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL,
				itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());

		Todo toDoEntry;

		ui.startTest();

		//Populate activity
		log.info("INFO: Add activity using API");
		Activity searchAct = baseAct.createAPI(apiOwner);

		String toDoTag = Data.getData().ToDo_InputText_Tags_Data;
		String uniqueToDoTag = toDoTag + Helper.genDateBasedRand();

		//Create todo entry in activity
		log.info("INFO: Add todo using API");		
		toDoEntry = apiOwner.createActivityTodo(Data.getData().Public_ToDo_InputText_Title_Data + Helper.genDateBasedRand(),
				Data.getData().commonDescription + " " + Data.getData().Start_An_Activity_ToDo_Desc + Helper.genDateBasedRand(),
				uniqueToDoTag + " " + toDoTag,
				searchAct,
				false);	
		Assert.assertNotNull(toDoEntry, "ERROR: Create activity todo for search (through api) failed.");

		String description = toDoEntry.getContent();
		description = description.trim();
		String descLastWord = description.substring(description.lastIndexOf(' ') + 1);
		ArrayList<String> assertList = new ArrayList<String>();

		log.info("INFO: Successully created Acvitity ToDo with title: " + toDoEntry.getTitle());

		// Create list of text to look for that indicates correct result displayed
		assertList.add(toDoEntry.getTitle());
		assertList.add(description);
		assertList.add(itemOwner.getDisplayName());

		APISearchHandler search = new APISearchHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());
		adminService.indexNow("activities", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		boolean found = search.waitForIndexer("activities", baseAct.getName(), 2);
		Assert.assertTrue(found, "Failed to find created activity after indexer ran.");

		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(itemOwner);

		// Uncheck all checkboxes
		ui.advancedSearchClearAll();

		// Check the Activities checkbox and then enter the unique title for the todo entry
		log.info("INFO: Searching for title: [" + toDoEntry.getTitle() + "]");
		ui.searchAComponent(SearchBy.TITLE, GlobalsearchUI.ActivitiesCheckbox, toDoEntry.getTitle());

		// Verify the results
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		// Return to search
		driver.navigate().back();
		ui.fluentWaitPresent(GlobalsearchUI.AdvancedSearchTag);

		// Uncheck all checkboxes
		ui.advancedSearchClearAll();

		// Check the Activities checkbox and then enter the unique title for the todo entry
		log.info("INFO: Searching for tag: [" + uniqueToDoTag + "]");
		ui.searchAComponentForATag(GlobalsearchUI.ActivitiesCheckbox, uniqueToDoTag);

		// Verify the results
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		// Return to search
		driver.navigate().back();
		ui.fluentWaitPresent(GlobalsearchUI.AdvancedSearchTag);

		// Check the Activities checkbox and then enter the last word of the unique description for the todo entry
		log.info("INFO: Searching for description: [" + descLastWord + "]");
		ui.searchAComponent(SearchBy.DESCRIPTION, GlobalsearchUI.ActivitiesCheckbox, descLastWord);

		// Verify the results
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Create an activity and search for its title and goal</li>
	 *<li><B>Step: </B>Add an activity using the API</li>
	 *<li><B>Step: </B>Create a list of text looking for Display Name, name, and goal</li> 
	 *<li><B>Step: </B>Uncheck all checkboxes</li>
	 *<li><B>Step: </B>Enter a the title of the activity for the Activities checkbox</li>
	 *<li><B>Step: </B>Search for the tag</li>
	 *<li><B>Verify: </B>The search results are present</li>
	 *<li><B>Step: </B>Return to the search page</li>
	 *<li><B>Step: </B>Uncheck all checkboxes</li>
	 *<li><B>Step: </B>Enter a unique word in the activity's goal for the Activities checkbox</li>
	 *<li><B>Step: </B>Search for the description</li>
	 *<li><B>Verify: </B>The search results are present</li>
	 *</ul>
	 */ 
	@Test(groups = { "regression" })
	public void advancedSearchForActivities() throws Exception {
		BaseActivity baseAct = new BaseActivity.Builder("Search Activity" + Helper.genDateBasedRand())
		.goal(Data.getData().commonDescription + Helper.genDateBasedRand())
		.tags("acttag" + Helper.genDateBasedRand()+" acttag")
		.build();	

		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());

		ArrayList<String> assertList = new ArrayList<String>();
		
		ui.startTest();

		//Populate activity
		log.info("INFO: Add activity using API");
		baseAct.createAPI(apiOwner);
		
		APISearchHandler search = new APISearchHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());
		adminService.indexNow("activities", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		boolean found = search.waitForIndexer("activities", baseAct.getName(), 2);
		Assert.assertTrue(found, "Failed to find created entry after indexer ran.");

		// Create list of text to look for that indicates correct result displayed
		assertList.add(itemOwner.getDisplayName());
		assertList.add(baseAct.getName());
		assertList.add(baseAct.getGoal());
		String description = baseAct.getGoal();
		String descLastWord = description.substring(description.lastIndexOf(' ') + 1);

		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(itemOwner);

		// Uncheck all checkboxes
		ui.advancedSearchClearAll();

		// Check the Activities checkbox and then enter the title for the activity
		ui.searchAComponent(SearchBy.TITLE, GlobalsearchUI.ActivitiesCheckbox, baseAct.getName());

		// Verify the results
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);
		
		// Return to search
		driver.navigate().back();
		ui.fluentWaitPresent(GlobalsearchUI.AdvancedSearchTag);
		
		// Check the Activities checkbox and then enter the last word of the unique description for the activity
		ui.searchAComponent(SearchBy.DESCRIPTION, GlobalsearchUI.ActivitiesCheckbox, descLastWord);

		// Verify the results
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);		

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Create a private section for an activity and search for it</li>
	*<li><B>Step: </B>Add an activity using the API</li>
	*<li><B>Step: </B>Add a private section to the activity using the API</li>
	*<li><B>Step: </B>Create a list of text looking for Display Name, Name and Description</li> 
	*<li><B>Step: </B>Uncheck all checkboxes</li>
	*<li><B>Step: </B>Enter the title of the section for the Activities checkbox</li>
	*<li><B>Step: </B>Search for the title</li>
	*<li><B>Verify: </B>The search results are present</li>
	*</ul>
	*/ 
	@Test(groups = { "regression" })
	public void advancedSearchForActivitySection() throws Exception {
		advancedSearchForActivitySection(true);
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Create a public section for an activity and search for it</li>
	*<li><B>Step: </B>Add an activity using the API</li>
	*<li><B>Step: </B>Add a public section to the activity using the API</li>
	*<li><B>Step: </B>Create a list of text looking for Display Name, Name and Description</li> 
	*<li><B>Step: </B>Uncheck all checkboxes</li>
	*<li><B>Step: </B>Enter the title of the section for the Activities checkbox</li>
	*<li><B>Step: </B>Search for the title</li>
	*<li><B>Verify: </B>The search results are present</li>
	*</ul>
	*/ 
	@Test(groups = { "regression" })
	public void advancedSearchForPublicActivitySection() throws Exception {
		advancedSearchForActivitySection(false);
	}
	
	private void advancedSearchForActivitySection(boolean isPrivate) throws Exception {

		BaseActivity baseAct = new BaseActivity.Builder("Search Activity" + Helper.genDateBasedRand())
		.goal(Data.getData().commonDescription + Helper.genDateBasedRand())
		.tags("acttag" + Helper.genDateBasedRand()+" acttag")
		.build();

		APIActivitiesHandler apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL,
				itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());

		ActivityEntry section;

		ui.startTest();

		//Populate activity
		log.info("INFO: Add activity using API");
		Activity searchAct = baseAct.createAPI(apiOwner);

		String toDoTag = Data.getData().ToDo_InputText_Tags_Data;

		//Create section in activity
		log.info("INFO: Add todo using API");		
		section = apiOwner.createActivityEntry(Data.getData().Start_An_Activity_Section_Title + Helper.genDateBasedRand(),
				Data.getData().commonDescription + Helper.genDateBasedRand() + "2", // distinct from activity's
				toDoTag + Helper.genDateBasedRand() + " " + toDoTag,
				searchAct,
				isPrivate);	
		Assert.assertNotNull(section, "ERROR: Create activity section for search (through api) failed.");

		String description = section.getContent();
		description = description.trim(); // Clean leading and trailing whitespace
		ArrayList<String> assertList = new ArrayList<String>();

		log.info("INFO: Successully created Acvitity Section with title: " + section.getTitle());

		// Create list of text to look for that indicates correct result displayed
		assertList.add(section.getTitle());
		assertList.add(description);
		assertList.add(itemOwner.getDisplayName());

		APISearchHandler search = new APISearchHandler(serverURL, itemOwner.getAttribute(cfg.getLoginPreference()), itemOwner.getPassword());
		adminService.indexNow("activities", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		boolean found = search.waitForIndexer("activities", baseAct.getName(), 2);
		Assert.assertTrue(found, "Failed to find created activity after indexer ran.");

		ui.loadComponent(Data.getData().ComponentGlobalSearch);
		ui.login(itemOwner);

		// Uncheck all checkboxes
		ui.advancedSearchClearAll();

		// Check the Activities checkbox and then enter the unique title for the activity entry
		log.info("INFO: Searching for title: [" + section.getTitle() + "]");
		ui.searchAComponent(SearchBy.TITLE, GlobalsearchUI.ActivitiesCheckbox, section.getTitle());

		// Verify the results
		ui.assertAllTextPresentWithinElement(GlobalsearchUI.TopSearchResult, assertList);

		ui.endTest();
	}	

}
