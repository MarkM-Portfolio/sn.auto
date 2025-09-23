package com.ibm.conn.auto.tests.profiles;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_Widget_Action_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_Cnx8UI_Profiles_MyProfile  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Profiles_MyProfile.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	private ProfilesUICnx8 profilesUICnx8;
	private ProfilesUI ui;
	private String serverURL;
	

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		profilesUICnx8 = new ProfilesUICnx8(driver);
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify User Name, Email Address and Local Time on My Profile Page</li>
	 * <li><B>Step:</B> Load home page and login</li>
	 * <li><B>Step:</B> Select 'Profile' from left navigation panel</li>
	 * <li><B>Step:</B> Click on the 'Edit Profile' button</li>
	 * <li><B>Step:</B> Scroll into TimeZone drop down at edit profile form</li>
	 * <li><B>Step:</B> Select 'GMT-12:00) International Date Line West' time zone from the drop down</li>
	 * <li><B>Step:</B> Click on the 'Save and Close' button</li>
	 * <li><B>Step:</B> Verify that 'Local Time' text is displayed in My Profile Page</li>
	 * <li><B>Verify:</B> Verify that 'Local Time' is displayed in "h:mm AM/PM" on My Profile Page</li>
	 * <li><B>Step:</B> Verify that User Name is displayed in My Profile Page</li>
	 * <li><B>Verify:</B> Verify that User Email Address is displayed in My Profile Page</li>
	 * <li><B>JIRA Link 1st step:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T636</li>
	 * </ul>
	 */
	
	//Disabled this test case due to https://jira.cwp.pnp-hcl.com/browse/CNXSERV-15352 this issue

	@Test(groups = { "cnx8ui-cplevel2" },enabled=false)
	public void verifyMyProfilePage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Homepage and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Homepage and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentHomepage);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("INFO: Select 'Profile' from left nav panel");
		logger.strongStep("Select 'Profile' from left nav panel");
		AppNavCnx8.PROFILE.select(profilesUICnx8);
		ui.waitForPageLoaded(driver);
		
		log.info("Click on the 'Edit Profile' button");
		logger.strongStep("Click on the 'Edit Profile' button");
		ui.editMyProfile();
		ui.waitForPageLoaded(driver);
		
		log.info("Scroll into TimeZone dropdown at edit profile form");
		logger.strongStep("Scroll into TimeZone dropdown at edit profile form");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getSingleElement(ProfilesUIConstants.TimeZoneDropDown).getWebElement());
		
		log.info("Select 'GMT-12:00) International Date Line West' time zone from the dropdown ");
		logger.strongStep("Select 'GMT-12:00) International Date Line West' time zone from the dropdown ");
		Select selectTimezone = new Select(profilesUICnx8.findElement(ui.createByFromSizzle(ProfilesUIConstants.TimeZoneDropDown)));
		//selecting any other time zone before selecting expected time zone to make Save and Close button enable
		selectTimezone.selectByVisibleText("(GMT-10:00) Hawaii");
		selectTimezone.selectByVisibleText("(GMT-12:00) International Date Line West");
		
		log.info("Click on the 'Save and Close' button");
		logger.strongStep("Click on the 'Save and Close' button");
		profilesUICnx8.saveAndcloseAtEditProfile();
		

		// Get Current UTC time in "h:mm a" this format
		Date date = new Date();
		SimpleDateFormat DateFormat = new SimpleDateFormat("h:mm a");
		DateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String currentExpectedTime = DateFormat.format(date);

		// Actual Time
		String curentActualTime = ui.getElementTextWd(By.xpath(ProfilesUIConstants.profileTime));
	
		logger.strongStep("Verify that 'Local Time' text is displayed in My Profile Page");
		log.info("Verify that 'Local Time' text is displayed in My Profile Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.profileLocalTimeTitle)),"Local Time:", "'Local Time' text is displayed");
		
		logger.strongStep("Verify that 'Local Time' is displayed in "+currentExpectedTime+" in expected format on My Profile Page");
		log.info("Verify that 'Local Time' is displayed "+currentExpectedTime+" in expected format on My Profile Page");
		cnxAssert.assertEquals(curentActualTime,currentExpectedTime, "'Local Time' is displayed in expected format");

		logger.strongStep("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		log.info("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		cnxAssert.assertTrue(ui.isUserNameDisplayed(testUser), "User name is displayed in My Profile Page");
		
		logger.strongStep("Verify that " + testUser.getEmail()+ "is displayed in My Profile Page");
		log.info("Verify that " + testUser.getEmail()+ "is displayed in My Profile Page");
		cnxAssert.assertTrue(ui.isUserEmailDisplayed(testUser),"User email address is displayed in My Profile Page");
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Navigate to Settings page from MyProfile and validate Default Homepage </li>
	 * <li><B>Step:</B> Load home page and login</li>
	 * <li><B>Step:</B> Select 'Profile' from left navigation panel</li>
	 * <li><B>Step:</B> Click On Setting Page from Second Level Navigation</li>
	 * <li><B>Verify:</B> Verify Default Homepage heading on Settings Page</li>
	 * <li><B>Verify:</B> Verify Top Updates text is displayed on Settings Page</li>
	 * <li><B>Verify:</B> Verify Latest Updates text is displayed on Settings Page</li>
	 * <li><B>Verify:</B> Verify Discover text is displayed on Settings Page</li>
	 * <li><B>Verify:</B> Verify My Page text is displayed on Settings Page</li>
	 * <li><B>JIRA Link 2nd step:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T636</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifySettingPagefromSecondLevelNavigation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Homepage and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Homepage and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentHomepage);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("INFO: Select 'Profile' from left nav panel");
		logger.strongStep("Select 'Profile' from left nav panel");
		AppNavCnx8.PROFILE.select(profilesUICnx8);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Click On Setting Page from Second Level Navigation");
		logger.strongStep("Click On Setting Page from Second Level Navigation");
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.settingsLink), 6, "Navigating to Settings Page from MyProfile");
		
		log.info("INFO: Click On Default Homepage Link on Settings Page");
		logger.strongStep("Click On Default Homepage Link on Settings Page");
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings), 6, "Navigating to Default Homepage Link on Settings");
		
		logger.strongStep("Verify Default Homepage heading on Settings Page");
		log.info("Verify Default Homepage heading on Settings Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.defaultHomepagePageHeading)),"Default homepage view", "'Default homepage view' text is displayed");
		
		logger.strongStep("Verify Top Updates text is displayed on Settings Page");
		log.info("Verify Top Updates text is displayed on Settings Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.defaultHomepagePageTopUpdatesRadioBtn)),"Top Updates", "'Top Updates' text is displayed");
		
		logger.strongStep("Verify Latest Updates text is displayed on Settings Page");
		log.info("Verify Latest Updates text is displayed on Settings Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.defaultHomepagePageLatestUpdatesRadioBtn)),"Latest Updates", "'Latest Updates' text is displayed");
		
		logger.strongStep("Verify Discover text is displayed on Settings Page");
		log.info("Verify Discover text is displayed on Settings Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.defaultHomepagePageDiscoverRadioBtn)),"Discover", "'Discover' text is displayed");
		
		logger.strongStep("Verify My Page text is displayed on Settings Page");
		log.info("Verify My Page text is displayed on Settings Page");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.defaultHomepagePageMyPageRadioBtn)),"My Page", "'My Page' text is displayed");	
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Background Tab Under Edit Profile Page </li>
	 * <li><B>Step:</B> Load Profile page and login</li>
	 * <li><B>Step:</B> Click on the 'Edit Profile' button</li>
	 * <li><B>Step:</B> Click On Background Link from Edit Profile page</li>
	 * <li><B>Verify:</B> Verify 'About Me:' title is displayed</li>
	 * <li><B>Step:</B> Clear and Type text into About Me description field</li>
	 * <li><B>Verify:</B> Verify 'Background:' title is displayed</li>
	 * <li><B>Step:</B> Click on the 'Save and Close' button</li>
	 * <li><B>Verify:</B> Verify that user name is displayed at My Profile page</li>
	 * <li><B>JIRA Link 2nd step:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T638</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyBackgroundTabAtEditPage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		log.info("Click on the 'Edit Profile' button");
		logger.strongStep("Click on the 'Edit Profile' button");
		ui.editMyProfile();
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Click On Background Link from Edit Profile page");
		logger.strongStep("Click On Background Link from Edit Profile page");
		ui.clickLinkWaitWd(ui.createByFromSizzle(ProfilesUIConstants.EditBackgroundTab), 6, "Clicked On Background Link");
		ui.waitForPageLoaded(driver);
		
		driver.switchToFrame();
		
		log.info("INFO: Verify 'About Me:' title is displayed");
		logger.strongStep("INFO: Verify 'About Me:' title is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.aboutmeBackground)),"About me:", "'About Me:' title is displayed");
		
		logger.strongStep("Clear and Type text into About Me description field");
		log.info("Clear and Type text into About Me description field");
		ui.switchToFrame(ProfilesUIConstants.iframeInsideRichText,ProfilesUIConstants.richTextEditorAtaboutmeBackground);
		ui.getFirstVisibleElement(ProfilesUIConstants.richTextEditorAtaboutmeBackground).clear();
		ui.getFirstVisibleElement(ProfilesUIConstants.richTextEditorAtaboutmeBackground).typeWithDelay("Description Entered on About me input field");
		ui.switchToTopFrame();
		
		log.info("INFO: Verify 'Background:' title is displayed");
		logger.strongStep("INFO: Verify 'Background:' title is displayed");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getSingleElement(ProfilesUIConstants.backgroundTitle).getWebElement());
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.backgroundTitle)),"Background:", "'Background:' title is displayed");
		ui.switchToTopFrame();
		
		log.info("Click on the 'Save and Close' button");
		logger.strongStep("Click on the 'Save and Close' button");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getSingleElement(ProfilesUIConstants.profileUserCancel).getWebElement());
		profilesUICnx8.saveAndcloseAtEditProfile();
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		log.info("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		cnxAssert.assertTrue(ui.isUserNameDisplayed(testUser), "User name is displayed in My Profile Page");

		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Photo tab Under Edit Profile Page </li>
	 * <li><B>Step:</B> Load Profile page and login</li>
	 * <li><B>Step:</B> Click on the 'Edit Profile' button</li>
	 * <li><B>Step:</B> Click On Photo Link from Edit Profile page</li>
	 * <li><B>Verify:</B> Verify 'Upload a new image:' title is displayed</li>
	 * <li><B>Verify:</B> Verify 'Upload Image' button is displayed</li>
	 * <li><B>Step:</B> Scroll Down to Cancel button</li>
	 * <li><B>Verify:</B> Verify 'Remove Image' button is displayed</li>
	 * <li><B>Step:</B> Upload Profile Image</li>
	 * <li><B>Step:</B> Click on the 'Save and Close' button</li>
	 * <li><B>Verify:</B> Verify that user name is displayed at My Profile page</li>
	 * <li><B>JIRA Link 3rd step:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T638</li>
	 * </ul>
	 * @throws Exception 
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyPhotoTabAtEditPage() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file6)
				 .extension(".jpg")
				 .build();

		log.info("INFO: Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		log.info("Click on the 'Edit Profile' button");
		logger.strongStep("Click on the 'Edit Profile' button");
		ui.editMyProfile();
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Click On Photo Link from Edit Profile page");
		logger.strongStep("Click On Photo Link from Edit Profile page");
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.photoTabEditProfile), 6, "Clicked On Photo Link");
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify 'Upload a new image:' title is displayed");
		logger.strongStep("INFO: Verify 'Upload a new image:' title is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.uploadNewImageText)),"Upload a new image:", "'Upload a new image:' title is displayed");
		
		log.info("INFO: Verify 'Upload Image' button is displayed");
		logger.strongStep("INFO: Verify 'Upload Image' button is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(ui.createByFromSizzle(ProfilesUIConstants.ChooseAFileButton)), "'Upload image' button is displayed");
		
		log.info("INFO: Scroll Down to Cancel button");
		logger.strongStep("INFO: Scroll Down to Cancel button");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getSingleElement(ProfilesUIConstants.profileUserCancel).getWebElement());
		
		log.info("INFO: Verify 'Remove Image' button is displayed");
		logger.strongStep("INFO: Verify 'Remove Image' button  is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(ui.createByFromSizzle(ProfilesUIConstants.RemoveImage)), "'Remove Image' button is displayed");
		
		log.info("INFO: Upload Profile Image");
		logger.strongStep("INFO: Upload Profile Image");
		ui.addProfilePhoto(baseFileImage);
		
		log.info("Click on the 'Save and Close' button");
		logger.strongStep("Click on the 'Save and Close' button");
		profilesUICnx8.saveAndcloseAtEditProfile();
		
		logger.strongStep("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		log.info("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		cnxAssert.assertTrue(ui.isUserNameDisplayed(testUser), "User name is displayed in My Profile Page");
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Pronunciation tab Under Edit Profile Page </li>
	 * <li><B>Step:</B> Load Profile page and login</li>
	 * <li><B>Step:</B> Click on the 'Edit Profile' button</li>
	 * <li><B>Step:</B> Click On Pronunciation Link from Edit Profile page</li>
	 * <li><B>Verify:</B> Verify 'Upload an audio file:' title is displayed</li>
	 * <li><B>Verify:</B> Verify 'Choose a File' button is displayed</li>
	 * <li><B>Step:</B> Scroll Down to Cancel button</li>
	 * <li><B>Step:</B> Click on Cancel button</li>
	 * <li><B>Verify:</B> Verify that user name is displayed at My Profile page</li>
	 * <li><B>JIRA Link 4th step:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T638</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyPronunciationTabAtEditPage(){
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		log.info("Click on the 'Edit Profile' button");
		logger.strongStep("Click on the 'Edit Profile' button");
		ui.editMyProfile();
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Click On Pronunciation Link from Edit Profile page");
		logger.strongStep("Click On Pronunciation Link from Edit Profile page");
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.pronunciationTabEditProfile), 6, "Clicked On Pronunciation Link");
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify 'Upload an audio file:' title is displayed");
		logger.strongStep("INFO: Verify 'Upload an audio file:' title is displayed");
		cnxAssert.assertEquals(ui.getElementTextWd(By.xpath(ProfilesUIConstants.uploadAudioFilePronunciationTab)),"Upload an audio file:", "'Upload an audio file:' title is displayed");
		
		log.info("INFO: Verify 'Choose a file' button is displayed");
		logger.strongStep("INFO: Verify 'Choose a file' button is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(ui.createByFromSizzle(FilesUIConstants.chooseAFile)), "'Choose a file' button is displayed");
		
		log.info("INFO: Scroll Down to Cancel button");
		logger.strongStep("INFO: Scroll Down to Cancel button");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getSingleElement(ProfilesUIConstants.profileUserCancel).getWebElement());
		
		log.info("INFO: Click on Cancel button");
		logger.strongStep("INFO: Click on Cancel button");
		ui.clickCancelButton();
		
		logger.strongStep("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		log.info("Verify that " + testUser.getDisplayName() + "is displayed in My Profile Page");
		cnxAssert.assertTrue(ui.isUserNameDisplayed(testUser), "User name is displayed in My Profile Page");
				
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify help action item for 'Report To chain' navigates to new window with title 'Viewing profiles'</li>
	 * <li><B>Step:</B> Load Profile page and login</li>
	 * <li><B>Step:</B> Click on action menu for 'Report to chain'</li>
	 * <li><B>Step:</B> Verify that it opens new window with title as 'Viewing profiles'</li>
	 * <li><B>JIRA Link</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14063</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyHelpLink() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);

		log.info("INFO: Click on action icon of 'Report to chain' widget displayed in right pane");
		logger.strongStep(" Click on action icon of 'Report to chain' widget displayed in right pane");
		Profile_Widget_Action_Menu.HELP.actionsforReportToChain(ui);

		log.info("INFO: Click on 'Help'");
		logger.strongStep("Click on 'Help'");
		Profile_Widget_Action_Menu.HELP.select(ui);
		ui.switchToNextWindowWd("c_pers_profiles");

		log.info("INFO: Verify that new window is opened up with title Viewing profiles");
		logger.strongStep("Verify that new window is opened up with title Viewing profiles");
		cnxAssert.assertTrue(driver.getTitle().equals("Viewing profiles"), "Verify the Title of help page");
		driver.close();
		
		profilesUICnx8.endTest();
	}

}
