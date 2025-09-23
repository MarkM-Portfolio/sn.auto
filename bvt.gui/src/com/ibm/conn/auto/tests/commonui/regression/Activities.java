package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class Activities extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Activities.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	BaseActivity activity1, activity2;
	BaseActivityEntry entry;
	BaseActivityToDo toDo;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();		
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);		
		testUser = cfg.getUserAllocator().getUser();

		activity1 = new BaseActivity.Builder("addEntry" + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag)
									.goal("Goal - Add an Entry ")
									.build();

		entry = BaseActivityEntry.builder("Entry" + Helper.genDateBasedRandVal())
			   					 .tags(Helper.genDateBasedRandVal())
			   					 .description("Entry Description" + Helper.genDateBasedRandVal())
			   					 .build();

		activity2 = new BaseActivity.Builder("addToDo" + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag)
									.goal("Goal - Add a ToDo")
									.build();

		toDo = BaseActivityToDo.builder("ToDo" + Helper.genDateBasedRandVal())
							   .tags(Helper.genDateBasedRandVal())
							   .description("ToDo Description" + Helper.genDateBasedRandVal())
							   .build();	
		
	}
	

	/**
	* addEntry()
	*<ul>
	*<li><B>Info:</B> Adding an Entry to an activity</li>
	*<li><B>Step:</B> Create an activity</li>
	*<li><B>Step:</B> Add an entry</li>
	*<li><B>Verify:</B> The Entry has being created successfully</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Activities</a></li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void addEntry() {

		ui.startTest();
			
		log.info("INFO: Load Activities and Log in as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Create an activity: " + activity1.getName());
		activity1.create(ui);

		log.info("INFO: Create a new entry");
		entry.create(ui);
	
		log.info("INFO: Validate Entry title");
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		Assert.assertTrue(driver.isTextPresent(entry.getTitle()), 
						  "ERROR: Newly created entry is not found");

		log.info("Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");
		
		log.info("INFO: Go to the Activities List");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		log.info("Clean Up: Delete the activity");
		activity1.delete(ui);
		
		ui.endTest();
	}

	
	/**
	* addToDo()
	*<ul>
	*<li><B>Info:</B> Adding a ToDo to an activity</li>
	*<li><B>Step:</B> Create an activity</li>
	*<li><B>Step:</B> Create a ToDo</li>
	*<li><B>Verify:</B> The ToDo has being created successfully</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Activities</a></li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void addToDo() {
	
		ui.startTest();
					
		log.info("INFO: Load Activities and Log in as " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		log.info("INFO: Create an activity: " + activity2.getName());
		activity2.create(ui);
		
		log.info("INFO: Add a ToDo");
		toDo.create(ui);

		log.info("INFO: Validate ToDo title");
		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		Assert.assertTrue(driver.isTextPresent(toDo.getTitle()), 
						  "ERROR: Newly created ToDo is not found");

		log.info("Scroll up to the top of the page");
		driver.executeScript("scroll(0,-250);");
		
		log.info("INFO: Go to the Activities List");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		log.info("Clean Up: Delete the activity");
		activity2.delete(ui);
		
		ui.endTest();
	}
	
}