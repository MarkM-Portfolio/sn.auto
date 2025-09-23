package com.ibm.conn.auto.tests.activities.regression;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.communities.regression.Regression_Communities;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.FilesUI;

public class RegressionActivities extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(Regression_Communities.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	private APIActivitiesHandler apiOwner;

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);

		testUser = cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}

	/**
	 * Attempts to edit an activity from the "edit" link in the activity list,
	 * changes the start page to recent updates, and verifies that the recent
	 * updates show when clicking the link to the activity. Attempts to edit
	 * the same activity from the "edit" link in the activity list, changes
	 * the start page to To Do, and verifies that the To Do items show when
	 * clicking the link to the activity.
	 * @throws Exception
	 */	
	@Test(groups = {"regression", "regressioncloud"} )
	public void changeActivityStartPage() throws Exception {
		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		.tags(testName)
		.goal("Goal for "+ testName)
		.build();

		// Create activity with Atom API
		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);

		// Load the component
		ui.loadComponent(Data.getData().ComponentActivities);

		// Login as a user (ie. Amy Jones333)
		ui.login(testUser);

		//Go back to activities list
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);

		//Find the UUID of our activity
		String UUID = getActivityUUID(activity);

		//Click the "more" link for our activity
		ui.clickLink("css=tr[uuid='" + UUID + "'] td.lotusLastCell a:contains('More')");

		//Click edit for our activity
		ui.clickLinkWait("css=tr[id$='" + UUID + "detailsRow'] a:contains('Edit')");

		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.EditActivity_Save);

		//Edit the activity start page
		List<Element> start_select = driver.getElements(ActivitiesUIConstants.EditActivity_StartPage);
		start_select.get(start_select.size() - 1).useAsDropdown().selectOptionByValue("recent");
		ui.sleep(1000);

		//save
		ui.getFirstVisibleElement(ActivitiesUIConstants.EditActivity_Save).doubleClick();
		ui.fluentWaitTextNotPresent(Data.getData().EditActivity);

		//Go back to the main page of the activity created above
		ui.clickLinkWait("link=" + activity.getName());

		Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.recentUpdatesHeader),
				"The activity's start page was not properly changed to Recent Updates.");

		//Go back to activities list
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);

		//Click the "more" link for our activity
		ui.clickLinkWait("css=tr[uuid='" + UUID + "'] td.lotusLastCell a:contains('More')");

		//Click edit for our activity
		ui.clickLinkWait("css=tr[id$='" + UUID + "detailsRow'] a:contains('Edit')");

		//Wait for edit activity dialogue to appear
		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.EditActivity_Save);
		
		//Edit the activity start page
		start_select = driver.getElements(ActivitiesUIConstants.EditActivity_StartPage);
		start_select.get(start_select.size() - 1).useAsDropdown().selectOptionByValue("todo");
		ui.sleep(1000);

		//save
		List<Element> save = driver.getElements("css=input[value='Save']");
		save.get(save.size() - 1).click();
		ui.fluentWaitTextNotPresent(Data.getData().EditActivity);

		//Go back to the main page of the activity created above
		ui.clickLinkWait("link=" + activity.getName());

		Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.ToDoHeader),
				"The activity's start page was not properly changed to To Do.");

		ui.endTest();
	}
	/** 
	 * <ul>
	 * <li><B>Info: </B>Dismiss Welcome Panel</li>
	 * <li><B>Step: </B>Create an activity via the API</li>
	 * <li><B>Step: </B>Log in to Activities</li>
	 * <li><B>Step: </B>Select X to close the welcome panel titled "New to Activities?"</li>
	 * <li><B>Verify: </B>Check that the welcome panel was dismissed</li>
	 * <li><B>Step: </B>Select Log out link in the header region</li>
	 * <li><B>Step: </B>Log back in with same user</li>
	 * <li><B>Verify: </B>Check that the welcome panel state is remembered for the user and doesn't display</li>
	 * <li><B>Step: </B>Open exiting activity and return to Activities tab</li>
	 * <li><B>Verify: </B>Check that the welcome panel still doesn't display</li>
	 * <li><B>Step: </B>Log out and delete the cookie that stores the welcome panel state</li>
	 * <li><B>Step: </B>Log back in with same user</li>
	 * <li><B>Verify: </B>Check that the welcome panel displays since the cookie was deleted</li>
	 * <li><B>Step: </B>Open existing activity and return to Activities tab</li>
	 * <li><B>Verify: </B>Check that the welcome panel still displays </li> 
	 * </ul>
	 * @throws Exception
	 */
	
	//This test case is marked as disabled because Welcome panel on activity page is not part of current UI
	@Test(groups = {"regression"}, enabled=false  )
	public void dismissWelcomePanel() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
								.goal("Goal for "+ testName)
								.build();
		

		// Create activity with Atom API
		log.info("INFO: Create activity using API");
		logger.strongStep("INFO: Create activity using API");
		activity.createAPI(apiOwner);

		//Load the component and login
		logger.strongStep("Open browser and login to Community as: " + testUser.getDisplayName());
		log.info("INFO: Open browser and login to Community as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		//Close the welcome panel
		log.info("Click X to close Activities welcome panel");
		ui.clickLink(ActivitiesUIConstants.Close_Welcome);
		log.info("INFO: Verify the Activities welcome panel was dismissed");
		Assert.assertTrue(driver.isTextNotPresent(Data.getData().WelcomeText), "ERROR: Welcome panel displays");
		
		//Log out, but do not clear cookie which stores the welcome panel state
		driver.navigate().to("javascript:document.getElementById('logoutLink').click()");
		//driver.navigate().to(testConfig.getBrowserURL() + Data.getData().ComponentActivities);
		
		//Login as the same user 
		ui.login(testUser);
		
		//Verify welcome panel state is remembered for the user and doesn't display
		log.info("INFO: Verify the Activities welcome panel last state is recalled and doesn't display");
		Assert.assertTrue(driver.isTextNotPresent(Data.getData().WelcomeText), "ERROR: Welcome panel displays");
		
		//Open activity created via API
		log.info("INFO: Open activity " + activity.getName());
		ActivitiesUI.getActivityLink(activity);
		
		//Back to list of Activities
		log.info("INFO: Select Activities tab");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);
		
		//verify welcome panel still doesn't display
		log.info("INFO: Verify the Activities welcome panel still doesn't display");
		Assert.assertTrue(driver.isTextNotPresent(Data.getData().WelcomeText), "ERROR: Welcome panel displays");

		//Log out which deletes the cookie that stores the welcome panel state
		ui.logout();
		ui.close(cfg);

		//Load the component and login as same user
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);
		
		//Verify welcome panel displays since cookie was deleted
		log.info("INFO: Verify the Activities welcome panel displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().WelcomeText), "ERROR: Welcome panel not found");
		
		//Open activity created via API
		log.info("INFO: Open activity " + activity.getName());
		ActivitiesUI.getActivityLink(activity);

		//Back to activities
		log.info("INFO: Select Activities tab");
		ui.clickLinkWait(ActivitiesUIConstants.Activities_Tab);

		//Verify welcome panel still displays
		log.info("INFO: Verify the Activities welcome panel still displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().WelcomeText), "ERROR: Welcome panel not found");

		ui.endTest();
	}
	/**
	 * Verify that help links open popup windows with help content.
	 * This test fails because the help contents have changed and demo
	 * videos seem to no longer exist.
	 * @throws Exception
	 */
	//	@Test(groups = {"regression"} )
	public void verifyWelcomeLinks() throws Exception {
		String testName = ui.startTest();

		final int MAX_RETRIES = 5;

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
		.tags(testName)
		.goal("Goal for "+ testName)
		.build();

		// Load the component
		ui.loadComponent(Data.getData().ComponentActivities);

		// Login as user who has welcome page displayed
		ui.login(testUser);

		//Login as a different user until welcome text is displayed
		int retries = 0;
		while( !driver.isTextPresent(Data.getData().WelcomeText) ) {
			ui.logout();

			testUser =  cfg.getUserAllocator().getUser();

			retries++;
			Assert.assertTrue(retries < MAX_RETRIES, "Max retries exceeded" +
					" for " + testName);

			// Login
			ui.fluentWaitPresent(BaseUIConstants.Login_Button);
			ui.login(testUser);
		}

		//Get original window handle
		String originalWindow = driver.getWindowHandle();

		//Confirm pop ups load with appropriate data
		//TODO: Update help window titles for switch to window, "Help - IBM Connections" no longer displayed
		confirmPopUp(ActivitiesUIConstants.Activities_Help, "Help", "what_is_an_activity.html", originalWindow);

		confirmPopUp(ActivitiesUIConstants.Activities_Help_ToDo, "Help", "Using to-do entries to track tasks", originalWindow);

		confirmPopUp(ActivitiesUIConstants.View_Demo, "Using Activities - IBM Connections", "activities_demo_controller.swf", originalWindow);

		confirmPopUp(ActivitiesUIConstants.Footer_Demo, "Using Activities - IBM Connections", "activities_demo_controller.swf", originalWindow);

		//Create new activity
		ui.create(activity);

		//Confirm activity demo link loads pop up
		confirmPopUp(ActivitiesUIConstants.Activity_View_Demo, "Using Activities - IBM Connections", "activities_demo_controller.swf", originalWindow);

		//Confirm About demo pop up
		ui.clickLink(BaseUIConstants.PageFooterAbout);

		confirmPopUp(ActivitiesUIConstants.About_Watch_Demo, "Using Activities - IBM Connections", "activities_demo_controller.swf", originalWindow);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Adding an Entry with a file to an activity</li>
	 *<li><B>Step: </B>Create a new simple activity</li>
	 *<li><B>Step: </B>Add an entry with a file</li>
	 *<li><B>Step: </B>View the entry and attempt to download the file</li>
	 *<li><B>Verify: </B>Verify that the file was downloaded successfully</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void EntryDownloadFile() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String sourceFileName = Data.getData().file1;
		FilesUI fui = FilesUI.getGui(cfg.getProductName(), driver);

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();


		logger.strongStep("INFO: Create activity using API");
		log.info("INFO: Create activity using API");
		activity.createAPI(apiOwner);
		
		// Load the component and login
		logger.strongStep("Open browser and login to Activity as: " + testUser.getDisplayName());
		log.info("INFO: Open browser and login to Activity as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser);

		logger.strongStep("INFO: Open activity");
		log.info("INFO: Open activity");
		ui.clickLinkWait(ActivitiesUI.getActivityLink(activity));
		
		//Create a unique filename for uploading
		logger.strongStep("INFO: Creating entry with file");
		log.info("INFO: Creating entry with file");
		String newFileName = fui.createTempFileForUpload(sourceFileName);
		Assert.assertNotNull(newFileName, "ERROR: Could not create a file with a unique name to upload!");

		//Create New entry for activity created above		
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .addFile(newFileName)
												   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												   .build();

		//Add entry
		logger.strongStep("INFO: Create Entry");
		log.info("INFO: Create Entry");
		entry.create(ui);

		ui.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Find the link to the file
		logger.strongStep("INFO: Locating file link");
		log.info("INFO: Locating file link");
		Element fileElement = null;
		List<Element> fileLinks = driver.getVisibleElements(ActivitiesUIConstants.AttachDownload);
		for (Element element : fileLinks) {
			String content = element.getText();
			content = content.trim();
			if(content.equals(newFileName)) {
				fileElement = element;
			}
		}
		Assert.assertNotNull(fileElement, "ERROR: No link to the file " +
				newFileName + " was found on the entry's page!");
		
		try {
			//Set the directory for the download and ensure that it is empty
			logger.strongStep("INFO: Cleaning file download directory");
			log.info("INFO: Cleaning file download directory");
			fui.setupDirectory();
			
			//Download the file
			logger.strongStep("INFO: Downloading file: " + newFileName);
			log.info("INFO: Downloading file: " + newFileName);
			fui.download(fileElement);
			
			//Verify the file was downloaded
			logger.strongStep("INFO: Verify that the file is downloaded");
			log.info("INFO: Verify that the file is downloaded");
			fui.verifyFileDownloaded(newFileName);
		} catch (Exception e) {
			Assert.fail("ERROR: Exception was thrown when trying to " +
					"download file " + newFileName + " : " + e.getMessage());
		}
		
		ui.endTest();
	}	
	
	private void confirmPopUp(String linkSelector, String windowTitle, String confirmSelector, String originalWindow) throws Exception 
	{
		log.info("INFO: Popup has being open and will be verified");
		driver.getFirstElement(linkSelector).doubleClick();

		//Switch to Component window which should now be open
		driver.switchToFirstMatchingWindowByPageTitle(windowTitle);

		//Verify text/selector is present
		Assert.assertTrue(driver.getPageSource().contains(confirmSelector),
				"ERROR: The pop-up window did not contain " + confirmSelector);

		//close the popup window
		ui.close(cfg);

		//Switch back to original window
		driver.switchToWindowByHandle(originalWindow);
		log.info("INFO: Popup has being dismissed");
	}

	/**
	 * Attempts to return the UUID for the given activity. Assumes the client
	 * is currently on the main activities tab, and that the activity is
	 * listed in this tab.
	 * @param activity
	 * 		The activity to find the UUID of
	 * @return
	 * 		The UUID of the activity, if found
	 * @throws
	 * 		AssertionError, if the activity's UUID cannot be found
	 */
	private String getActivityUUID (BaseActivity activity) {
		//Find the link to our activity
		Element activityLink = driver.getSingleElement("link=" + activity.getName());
		Assert.assertFalse(activityLink == null, "No activity with found with name " + activity.getName());

		//extract the Activity UUID from the link with a regular expression 
		String activityLinkURL = activityLink.getAttribute("href");
		Pattern uidPattern = Pattern.compile(",([0-9a-f-]+)$");
		Matcher uidMatcher = uidPattern.matcher(activityLinkURL);
		Assert.assertTrue(uidMatcher.find(), "No valid UUID found for activity "
				+ activity.getName()
				+ ", link was " + activityLinkURL);
		return uidMatcher.group(1); 
	}

	private void verifyCompleteFormPrompt() {
		Assert.assertTrue(driver.isTextPresent(Data.getData().PleaseCompleteForm));
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.navigateAwayPromptOK));
		Assert.assertTrue(driver.isElementPresent(ActivitiesUIConstants.navigateAwayPromptCancel));
		ui.fluentWaitElementVisibleOnce(ActivitiesUIConstants.navigateAwayPromptClose);
		List<Element> closeButtons = driver.getVisibleElements(ActivitiesUIConstants.navigateAwayPromptClose);
		Assert.assertTrue(closeButtons.size() == 1, "Wrong number of elements visible for: {" +
				ActivitiesUIConstants.navigateAwayPromptClose +"}. " +
				closeButtons.size() + " elements found, 1 element expected.");
		closeButtons.get(0).click();
	}
}