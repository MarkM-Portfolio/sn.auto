package com.ibm.conn.auto.tests.activities;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
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

public class BVT_TinyEditor_Activities extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Activities.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private APIActivitiesHandler apiOwner;
	private User testUser1;
	private String serverURL;

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
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate Activity Tiny Editor for ToDo section and Add Entry</li>
	 * <li><B>Step:</B> Create a new activity via API</li>
	 * <li><B>Step:</B>Navigate to created Activity and navigate to its New Entry</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality for New Entry Tiny Editor</li>
	 * <li><B>Step:</B>Save New Entry</li>
	 * <li><B>Verify:</B>Verify New Entry description on Activity page</li>
	 * <li><B>Step:</B>Navigate to its To Do Section</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header Functionality for To Do Section In Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality for To Do Section In Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality for To Do Section In Tiny Editor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality for To Do Section In Tiny Editor</li>
	 * <li><B>Step:</B>Save To Do Section</li>
	 * <li><B>Verify:</B>Verify To Do Section in description on Activity page</li>
	 * <li><B>Step:</B>Delete the activity</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyActivityTinyEditorParagraphFunctionality() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal()).tags(testName)
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
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + "Entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
													.description("this is Test description for testcase " + testName)
													.tinyEditorFunctionalitytoRun("verifyIndentsInTinyEditor,verifyParaInTinyEditor,"
															+ "verifyRightLeftParagraphInTinyEditor,verifyAlignmentInTinyEditor")
												   .build();
		

		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);

		log.info("INFO: Add title to Entry");
		logger.strongStep("Add title to Entry");
		ui.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		log.info("INFO: Validate Activity Entry Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor fucntionality.");
		
		// entry TE validation and save entry
		String expectedValue = ui.verifyTinyEditorInActivityEntry(entry, testUser1);
		String actualValue = ui.getActivityEntryDescText().trim();
		Assert.assertEquals(actualValue, expectedValue);

		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description("this is Test description for testcase " + testName)
												.tinyEditorFunctionalitytoRun("verifyIndentsInTinyEditor,verifyParaInTinyEditor,"
														+ "verifyRightLeftParagraphInTinyEditor,verifyAlignmentInTinyEditor")
												.build();

		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Validate Activity ToDo Section Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity ToDo Section Tiny Editor fucntionality.");
		
		// ToDo TE validation and save entry
		expectedValue=ui.verifyTinyEditorInActivityToDo(toDo, testUser1);
		ui.getFirstVisibleElement(ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", toDo.getTitle())).click();
		actualValue = ui.getActivityToDoItemDescText(toDo.getTitle()).trim();
		Assert.assertEquals(actualValue, expectedValue);
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		activity.delete(ui);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Validate Activity Tiny Editor for ToDo section and Add Entry</li>
	 * <li><B>Step:</B> Create a new activity via API</li>
	 * <li><B>Step:</B>Navigate to created Activity and navigate to its New Entry</li>
	 * <li><B>Verify:</B>Verify Permanent Pen Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Attributes Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Font Size Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Font Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Other Text Attribute And FullScreen Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Text Color Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Background Color Functionality for New Entry Tiny Editor</li>
	 * <li><B>Step:</B>Save New Entry</li>
	 * <li><B>Verify:</B>Verify New Entry description on Activity page</li>
	 * <li><B>Step:</B>Navigate to its To Do Section</li>
	 * <li><B>Verify:</B>Verify Permanent Pen Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Attributes Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Font Size Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Font Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Other Text Attribute And FullScreen Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Text Color Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Background Color Functionality for New EnTo Do Section inEditor</li>
	 * <li><B>Step:</B>Save To Do Section</li>
	 * <li><B>Verify:</B>Verify To Do Section in description on Activity page</li>
	 * <li><B>Step:</B>Delete the activity</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyActivityTinyEditorFontAttributeFunctionality() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal()).tags(testName)
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
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + "Entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
													.description("thisisTestdescriptionfortestcase" + testName)
													.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
															+ "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
															+ "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor")
												   .build();

		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);

		log.info("INFO: Add title to Entry");
		logger.strongStep("Add title to Entry");
		ui.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		log.info("INFO: Validate Activity Entry Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor fucntionality.");
		
		// entry TE validation and save entry
		String expectedValue = ui.verifyTinyEditorInActivityEntry(entry, testUser1);
		String actualValue = ui.getActivityEntryDescText().trim();
		Assert.assertEquals(actualValue, expectedValue);

		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description("thisisTestdescriptionfortestcase" + testName)
												.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
														+ "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
														+ "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor")
												.build();

		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Validate Activity ToDo Section Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity ToDo Section Tiny Editor fucntionality.");
		
		// ToDo TE validation and save entry
		expectedValue=ui.verifyTinyEditorInActivityToDo(toDo, testUser1);
		ui.getFirstVisibleElement(ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", toDo.getTitle())).click();
		actualValue = ui.getActivityToDoItemDescText(toDo.getTitle()).trim();
		Assert.assertEquals(actualValue, expectedValue);

		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		activity.delete(ui);
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate Activity Tiny Editor for ToDo section and Add Entry</li>
	 * <li><B>Step:</B> Create a new activity via API</li>
	 * <li><B>Step:</B>Navigate to created Activity and navigate to its New Entry</li>
	 * <li><B>Verify:</B>Verify Horizontal Line Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Row, Column, Nested table, Image Functionality inside Table for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Black Quote Functionality for New Entry Tiny Editor</li>
	 * <li><B>Step:</B>Save New Entry</li>
	 * <li><B>Verify:</B>Verify New Entry description on Activity page</li>
	 * <li><B>Step:</B>Navigate to its To Do Section</li>
	 * <li><B>Verify:</B>Verify Horizontal Line Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Row, Column, Nested table, Image Functionality inside Table for To Do Section in Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Black Quote Functionality for To Do Section in Tiny Editor</li>
	 * <li><B>Step:</B>Save To Do Section</li>
	 * <li><B>Verify:</B>Verify To Do Section in description on Activity page</li>
	 * <li><B>Step:</B>Delete the activity</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyActivityTinyEditorLineBulletTableFunctionality() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal()).tags(testName)
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
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + "Entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
													.description("this is Test description for testcase " + testName)
													.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyBulletsAndNumbersInTinyEditor,"
															+ "verifyRowsCoulmnOfTableInTinyEditor,verifyBlockQuoteInTinyEditor")
												   .build();

		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);

		log.info("INFO: Add title to Entry");
		logger.strongStep("Add title to Entry");
		ui.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		log.info("INFO: Validate Activity Entry Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor fucntionality.");
		
		// entry TE validation and save entry
		String expectedValue = ui.verifyTinyEditorInActivityEntry(entry, testUser1);
		String actualValue = ui.getActivityEntryDescText().trim();
		Assert.assertEquals(actualValue, expectedValue);

		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description("this is Test description for testcase " + testName)
												.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyBulletsAndNumbersInTinyEditor,"
														+ "verifyRowsCoulmnOfTableInTinyEditor,verifyBlockQuoteInTinyEditor")
												.build();

		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Validate Activity ToDo Section Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity ToDo Section Tiny Editor fucntionality.");
		
		// ToDo TE validation and save entry
		expectedValue=ui.verifyTinyEditorInActivityToDo(toDo, testUser1);
		ui.getFirstVisibleElement(ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", toDo.getTitle())).click();
		actualValue = ui.getActivityToDoItemDescText(toDo.getTitle()).trim();
		Assert.assertEquals(actualValue, expectedValue);

		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		activity.delete(ui);
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate Activity Tiny Editor for ToDo section and Add Entry</li>
	 * <li><B>Step:</B> Create a new activity via API</li>
	 * <li><B>Step:</B>Navigate to created Activity and navigate to its New Entry</li>
	 * <li><B>Verify:</B>Verify Find Replace Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Spell check Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Undo Redo Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Special Character Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Link Image Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Emotions Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Word Count Functionality for New Entry Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Code Sample Functionality for New Entry Tiny Editor</li>
	 * <li><B>Step:</B>Save New Entry</li>
	 * <li><B>Verify:</B>Verify New Entry description on Activity page</li>
	 * <li><B>Step:</B>Navigate to its To Do Section</li>
	 * <li><B>Verify:</B>Verify Find Replace Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Spell check Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Undo Redo Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Special Character Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Link Image Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Emotions Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Word Count Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Verify:</B>Verify Code Sample Functionality for To Do Section in  Tiny Editor</li>
	 * <li><B>Step:</B>Save To Do Section</li>
	 * <li><B>Verify:</B>Verify To Do Section in description on Activity page</li>
 	 * <li><B>Step:</B>Delete the activity</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyActivityTinyEditorFindReplaceSpellcheckUndoRedoSpecialCharLinkImageFunctionality() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal()).tags(testName)
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
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + "Entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
													.description("this is Test description for testcase " + testName)
													.tinyEditorFunctionalitytoRun("verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,verifyUndoRedoInTinyEditor,"
																	+ "verifySpecialCharacterInTinyEditor,verifyLinkImageInTinyEditor,verifyEmotionsInTinyEditor,"
																	+ "verifyCodeSampleIntinyEditor,verifyWordCountInTinyEditor")
												   .build();

		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);

		log.info("INFO: Add title to Entry");
		logger.strongStep("Add title to Entry");
		ui.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		log.info("INFO: Validate Activity Entry Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor fucntionality.");
		
		// entry TE validation and save entry
		String expectedValue = ui.verifyTinyEditorInActivityEntry(entry, testUser1);
		String actualValue = ui.getActivityEntryDescText().trim();
		Assert.assertEquals(actualValue, expectedValue);

		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description("this is Test description for testcase " + testName)
												.tinyEditorFunctionalitytoRun("verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,verifyUndoRedoInTinyEditor,"
														+ "verifySpecialCharacterInTinyEditor,verifyLinkImageInTinyEditor,verifyEmotionsInTinyEditor,"
														+ "verifyCodeSampleIntinyEditor,verifyWordCountInTinyEditor")
												.build();

		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Validate Activity ToDo Section Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity ToDo Section Tiny Editor fucntionality.");

		// ToDo TE validation and save entry
		expectedValue=ui.verifyTinyEditorInActivityToDo(toDo, testUser1);
		ui.getFirstVisibleElement(ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", toDo.getTitle())).click();
		actualValue = ui.getActivityToDoItemDescText(toDo.getTitle()).trim();
		Assert.assertEquals(actualValue, expectedValue);

		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		activity.delete(ui);
		
		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Validate Activity Tiny Editor for ToDo section and Add Entry</li>
	 * <li><B>Step:</B> Create a new activity via API</li>
	 * <li><B>Step:</B>Navigate to created Activity and navigate to its New Entry</li>
	 * <li><B>Step:</B>Add Tiny Editor description and Save New Entry</li>
	 * <li><B>Step:</B>Edit Tiny Editor description for same Entry and Save Entry</li>
	 * <li><B>Verify:</B>Verify Edited Entry description on Activity page</li>
	 * <li><B>Step:</B>Navigate to its To Do Section</li>
	 * <li><B>Step:</B>Add Tiny Editor description and Save To Do Section</li>
	 * <li><B>Step:</B>Edit Tiny Editor description for same To Do Section and Save To Do Section</li>
	 * <li><B>Verify:</B>Verify To Do Section in description on Activity page</li>
 	 * <li><B>Step:</B>Delete the activity</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyActivityNewEntryTinyEditorEditFunctionality() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal()).tags(testName).build();

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
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + "Entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
													.tinyEditorFunctionalitytoRun("verifyEditDescriptionInTinyEditor")
													.description("this is Test description for testcase " + testName)
												   .build();

		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);

		log.info("INFO: Add title to Entry");
		logger.strongStep("Add title to Entry");
		ui.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		ui.verifyTinyEditorInActivityEntry(entry,testUser1);
		
		String editedDescripton = entry.getDescription().concat(" Edited");
		
		log.info("INFO: Validate Activity Entry Tiny Editor edit fucntionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor edit fucntionality.");
		ui.editDescriptionInTinyEditorNewEntry(editedDescripton);
		String actualDescription = ui.getActivityEntryDescText().trim();
		Assert.assertEquals(actualDescription, editedDescripton);
		
		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description("this is Test description for testcase " + testName)
												.tinyEditorFunctionalitytoRun("verifyEditDescriptionInTinyEditor")
												.build();

		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		ui.verifyTinyEditorInActivityToDo(toDo, testUser1);
		
		editedDescripton = toDo.getDescription().concat(" Edited");	
		
		log.info("INFO: Validate Activity Entry Tiny Editor edit functionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor edit functionality.");
		ui.editDescriptionInTinyEditorToDoSection(editedDescripton,toDo.getTitle());
		actualDescription = ui.getActivityToDoItemDescText(toDo.getTitle()).trim();
		Assert.assertEquals(actualDescription, editedDescripton);

		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		activity.delete(ui);
		
		ui.endTest();
		
	}
	
	/**
	 * <li><B>Verify:</B>Verify Insert Image with Current Window and New Window Functionality for New Entry and New ToDoItem in Tiny Editor</li>
	 * <li><B>Step:</B> Create a new activity via API</li>
	 * <li><B>Step:</B>Navigate to created Activity and navigate to its New Entry</li>
	 * <li><B>Step:</B>Insert two Images one with Current Window and other with New Window Option and Save the Entry.</li>
	 * <li><B>Verify:</B>Verify 'Current Window' and 'New Window' Image Link Functionality in the created New Entry</li>
	 * <li><B>Step:</B>Navigate to its New ToDoItem</li>
	 * <li><B>Step:</B>Insert two Images one with Current Window and other with New Window Option and Save the ToDoItem.</li>
	 * <li><B>Verify:</B>Verify 'Current Window' and 'New Window' Image Link Functionality in the created New ToDoItem</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyActivityTinyEditorInsertLinkFunctionality() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRandVal()).tags(testName)
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
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + "Entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .bookmark(Helper.genDateBasedRandVal()+ "ActivitiesHome", driver.getCurrentUrl().replace("http://", "") + "activities")
													.description("this is Test description for testcase " + testName)
													.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor")
												   .build();
		activity.setName(entry.getTitle());
		log.info("INFO: Click on Add Entry button");
		logger.strongStep("Click on Add Entry button");
		ui.clickLinkWait(ActivitiesUIConstants.New_Entry);

		log.info("INFO: Add title to Entry");
		logger.strongStep("Add title to Entry");
		ui.clearText(ActivitiesUIConstants.New_Entry_InputText_Title);
		ui.typeText(ActivitiesUIConstants.New_Entry_InputText_Title, entry.getTitle());

		log.info("INFO: Validate Activity Entry Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity Entry Tiny Editor fucntionality.");
		
		// entry TE validation and save entry
		String expectedValue = ui.verifyTinyEditorInActivityEntry(entry, testUser1);
		String actualValue = ui.getActivityEntryDescText().trim();
		Assert.assertEquals(actualValue, expectedValue);

		ui.verifyInsertedLink("CurrentWindow_"+entry.getTitle()+"~NewWindow_"+entry.getTitle());
				
		//Create New todo for activity created above	
		BaseActivityToDo toDo = BaseActivityToDo.builder(testName + "ToDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.addFile(Data.getData().file1)
												.description("this is Test description for testcase " + testName)
												.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor")
												.build();
		
		
		log.info("INFO: Click on Add To Do button");
		logger.strongStep("Click on Add To Do button");
		ui.clickLinkWait(ActivitiesUIConstants.AddToDo);

		log.info("INFO: Add title to To Do Item");
		logger.strongStep("Add title to To Do Item");
		ui.clearText(ActivitiesUIConstants.ToDo_InputText_Title);
		ui.typeText(ActivitiesUIConstants.ToDo_InputText_Title, toDo.getTitle());

		log.info("INFO: Click on To Do More Options");
		logger.strongStep("Click on To Do More Options");
		ui.clickLinkWait(ActivitiesUIConstants.ToDo_More_Options);

		log.info("INFO: Validate Activity ToDo Section Tiny Editor fucntionality.");
		logger.strongStep("Validate Activity ToDo Section Tiny Editor fucntionality.");

		// ToDo TE validation and save entry
		expectedValue=ui.verifyTinyEditorInActivityToDo(toDo, testUser1);
		ui.getFirstVisibleElement(ActivitiesUIConstants.activityToDoItemLink.replace("PLACEHOLDER", toDo.getTitle())).click();
		actualValue = ui.getActivityToDoItemDescText(toDo.getTitle()).trim();
		Assert.assertEquals(actualValue, expectedValue);

		ui.verifyInsertedLink(toDo.getTitle()+"~CurrentWindow_"+toDo.getTitle()+"~NewWindow_"+toDo.getTitle());
		
		log.info("INFO: Select the 'Activities' tab");
		logger.strongStep("Select the 'Activities' tab");
		ui.clickLinkWait(ActivitiesUIConstants.ActivitiesTab);
		
		//activity.delete(ui);
		
		ui.endTest();
	}
}
