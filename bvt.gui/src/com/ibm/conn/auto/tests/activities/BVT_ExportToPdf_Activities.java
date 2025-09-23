package com.ibm.conn.auto.tests.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.webui.PdfExportUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;

public class BVT_ExportToPdf_Activities extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_ExportToPdf_Activities.class);
	private ActivitiesUI ui;
	private PdfExportUI pUi;
	private TestConfigCustom cfg;
	private APIActivitiesHandler apiOwner;
	private User testUser1;
	private String serverURL;
	private List<Activity> testActs = new ArrayList<Activity>();
	
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL,
				testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		pUi = PdfExportUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Acceptance test to verify export an activity as pdf.</li>
	*<li><B>Step:</B>(API) Create an activity as UserA and create an entry.</li>
	*<li><B>Step:</B>Go to the activity and click the Export as PDF button.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>PDF sidebar contains entries for activity name, TOC and entry name</li>
	*<li><B>Verify:</B>PDF content contains activity name, TOC and entry name</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "UnitOnAnsible", "PdfExport" })
	public void smokeTestExportActivityToPdf() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();

		BaseActivity act = new BaseActivity.Builder(testName + 
				Helper.genDateBasedRandVal()).tags(testName).build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder("entry " + 
				Helper.genDateBasedRandVal()).tags(Helper.genDateBasedRandVal()).build();
		
		logger.strongStep("Create an Activity (API)");
		log.info("INFO: Create an activity (API) as " + testUser1.getDisplayName());
		Activity activity = act.createAPI(apiOwner);
		entry.setParent(activity);
		testActs.add(activity);
		
		logger.strongStep("Create an entry (API)");
		log.info("INFO: Create an entry (API)");
		apiOwner.createActivityEntry(entry.getTitle(), entry.getDescription(), entry.getTags(), activity, false);
		
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		logger.strongStep("Open the Activity");
		log.info("INFO: Open the Activity " + activity.getTitle());
		ui.navViaUUID(activity);
		
		// call common smoketest method, the entry does not exist in the sidebar 
		// in lieu of design change there is no headline on the page.
		pUi.smokeTest(pUi, logger, activity.getTitle(), entry.getTitle(), false);
			
		pUi.endTest();	
	}
	
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		for (Activity act : testActs)  {
			apiOwner.deleteActivity(act);
		}
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export an activity as PDF.</li>
	*<li><B>Step:</B>(API) Create an activity</li>
	*<li><B>Step:</B> Create Entries (API)</li>
	*<li><B>Step:</B>Load Activities and Log In</li>
	*<li><B>Step:</B>Open the Activity</li>
	*<li><B>Step:</B>Get the entries newly created for parent Activity</li>
	*<li><B>Verify:</B>PDF content validation based on the selected entries</li>
	*</ul>
	 */
	
	@Test(groups = { "PdfExport", "RegressionOnAnsible"  })
	public void exportPdfContentValidation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();

		BaseActivity act = new BaseActivity.Builder(testName + 
				Helper.genDateBasedRandVal()).tags(testName).build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder("entry1" + 
				Helper.genDateBasedRandVal()).tags(Helper.genDateBasedRandVal()).build();
		
		BaseActivityEntry entry2 = BaseActivityEntry.builder("entry2" + 
				Helper.genDateBasedRandVal()).tags(Helper.genDateBasedRandVal()).build();
		
		BaseActivityEntry entry3 = BaseActivityEntry.builder("entry3" + 
				Helper.genDateBasedRandVal()).tags(Helper.genDateBasedRandVal()).build();
		
		logger.strongStep("Create an Activity (API)");
		log.info("INFO: Create an activity (API) as " + testUser1.getDisplayName());
		Activity activity = act.createAPI(apiOwner);
		entry.setParent(activity);
		testActs.add(activity);
		entry2.setParent(activity);
		testActs.add(activity);
		entry3.setParent(activity);
		testActs.add(activity);
		
		logger.strongStep("Create Entries (API)");
		log.info("INFO: Create Entries (API)");
		apiOwner.createActivityEntry(entry.getTitle(), entry.getDescription(), entry.getTags(), activity, false);
		apiOwner.createActivityEntry(entry2.getTitle(), entry2.getDescription(), entry2.getTags(), activity, false);
		apiOwner.createActivityEntry(entry3.getTitle(), entry3.getDescription(), entry3.getTags(), activity, false);
		
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		logger.strongStep("Open the Activity");
		log.info("INFO: Open the Activity " + activity.getTitle());
		ui.navViaUUID(activity);
		
		logger.strongStep("Get the entries newly created for parent Activity");
		log.info("INFO: Get the etries created for Activity");
		
		String Title1 = entry.getTitle();
		log.info("Entry 1:" +Title1);
		String Title2 = entry2.getTitle();
		log.info("Entry 2:" +Title2);
		String Title3 = entry3.getTitle();
		log.info("Entry 3:" +Title3);

		logger.strongStep("PDF content validation based on the selected entries");
		log.info("INFO: PDF content validation based on the selected entries");
		pUi.contentSelection(pUi, logger, activity.getTitle(), entry.getTitle(), true, Title1, Title2, Title3);

		pUi.endTest();	
	}

	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a Activity as pdf.</li>
	*<li><B>Step:</B>Create an Activity without any Entry or ToDoIteams (API)</li>
	*<li><B>Step:</B>Load Activity and Log In</li>
	*<li><B>Step:</B>Open the Activity</li>
	*<li><B>Verify:</B>Export PDF Button Option should not be available for the created Activity.</li>
	*<li><B>Step:</B>Create a Entry and ToDoItems for this Activity via API.</li>
	*<li><B>Verify:</B>Export PDF Button Option should be available now for the created Activity.</li>
	*<li><B>Step:</B>click the Export as PDF button.</li>
	*<li><B>Verify:</B>Export PDF Window contains All Options in 'Information included Section'.</li>
	*<li><B>Step:</B>Select all Options from 'Information included Section' List of Export PDF Window apart from ToDoIteams Options.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>All Options Selected above are listed in the Export PDF Preview.</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*<li><B>Step:</B>click the Export as PDF button.</li>
	*<li><B>Verify:</B>Export PDF Window contains All Options in 'Information included Section'.</li>
	*<li><B>Step:</B>Select ToDoIteams Options from 'Information included Section' List of Export PDF Window.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>All Options Selected above are listed in the Export PDF Preview.</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible" })
	public void infIncludedExportPdfActivity() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		HashMap<String,String> Informationmap = new HashMap<String,String>();
		
		BaseActivity act = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(testName)
				.build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder("entry " + Helper.genDateBasedRandVal())
				.tags(testName+"Entry")
				.description("This is Activity Entry Description")
				.build();
		
		BaseActivityToDo toDo = BaseActivityToDo.builder("todo" + Helper.genDateBasedRand())
				.tags(testName+"ToDo")
				.description("This is Activity ToDo Description")
				.build();	
		
		logger.strongStep("Create an Activity (API)");
		log.info("INFO: Create an activity (API) as " + testUser1.getDisplayName());
		Activity activity = act.createAPI(apiOwner);
		entry.setParent(activity);
		testActs.add(activity);
		
		logger.strongStep("Load Activities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		logger.strongStep("Open the Activity");
		log.info("INFO: Open the Activity " + activity.getTitle());
		ui.navViaUUID(activity);
		
		driver.turnOffImplicitWaits();
		Assert.assertFalse(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is not Expected but still present.");
		driver.turnOnImplicitWaits();
				
		//Add an Entry
		logger.strongStep("Create an entry (API)");
		log.info("INFO: Create an entry (API)");
		apiOwner.createActivityEntry(entry.getTitle(), entry.getDescription(), entry.getTags(), activity, false);
		
		logger.strongStep("Create a ToDo (API)");
		log.info("INFO: Create a ToDo (API)");
		toDo.setParent(activity);
		Todo newTodo = toDo.createTodoAPI(apiOwner);
		
		ui.fluentWaitPresentWithRefresh(PdfExportUI.pdfExportBtn);
		
		driver.turnOffImplicitWaits();
		Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is Expected but still Not present.");
		driver.turnOnImplicitWaits();
		
		String userid = pUi.getFirstVisibleElement("xpath=//a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		String Comment = Data.getData().ComponentActivitiesKeyText;
		
		Informationmap.put("author", userid);
		Informationmap.put("childcomment", Comment);
		Informationmap.put("parentdesc", act.getName());
		Informationmap.put("childdesc", entry.getDescription());
		Informationmap.put("parenttags", testName.toLowerCase());
		Informationmap.put("childtags", entry.getTags().toLowerCase());
		
		logger.strongStep("Validate Export PDF Functionality");
		log.info("INFO: Validate Export PDF Functionality");
		pUi.validateInformationIncludeSectionList(pUi, logger);
		pUi.SelectInformationIncludeSection(testName);
		String ValidationList = "titlepage:tableofcontent:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, Informationmap, 
				act.getName(), entry.getTitle(), ValidationList);
		
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
		pUi.waitForExportDialogDisappear();
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		pUi.selectInformationIncludeToDo();
		
		Informationmap.clear();
		Informationmap.put("childdesc", newTodo.getContent().trim());
		Informationmap.put("Entryname", entry.getTitle());
		
		pUi.validateToDoItems(pUi, logger, Informationmap, 
				act.getName(), newTodo.getTitle());
		
		pUi.endTest();	
	}
	
}
