package com.ibm.conn.auto.tests.profiles;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;

public class BVT_Cnx8UI_Profiles_Settings  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Profiles_Settings.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	private ProfilesUICnx8 profilesUICnx8;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		profilesUICnx8 = new ProfilesUICnx8(driver);	
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify email notification page layout</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'Settings' from top level navigation</li>
	 * <li><B>Verify:</B> Verify labels 'Send emails to this address:','Receive notifications from other people by email' along with checkbox, and 'Email language:' along with language dropdown from 'Notification preferences' section</li>
	 * <li><B>Verify:</B> Verify labels 'Mentions' and 'Responses to my content...'  from Browser Notification section and 'Email Notification' and 'How often do I want to be notified about:' from Email Notification section</li>
	 * <li><B>Verify:</B> Verify switches to turn on/off the setting respective to 'Mentions' and 'Responses to my content..'  from Browser Notification section</li>
	 * <li><B>Verify:</B> Verify all drop down labels from 'Email Notification'section</li>
	 * <li><B>Verify:</B> Verify all drop downs along with their default selected values from 'Email Notification'section</li>
	 * <li><B>Verify:</B> Verify buttons 'Restore Defaults','Save' and 'Close' present in footer section </li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T643</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyEmailNotificationPageLayout() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentSettings);
		profilesUICnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());
		profilesUICnx8.waitForPageLoaded(driver);
			
		List<String> elementsInNotificationPreferences= new ArrayList<>(Arrays.asList(ProfilesUIConstants.notificationsPreferences,ProfilesUIConstants.sendEmailsText,ProfilesUIConstants.receiveNotificationsTxt,ProfilesUIConstants.receiveNotificationsCheckBox,ProfilesUIConstants.emailLanguageText,ProfilesUIConstants.emailLanguageSelector));
		
		List<String> labelsInBrowserNotification= new ArrayList<>(Arrays.asList(ProfilesUIConstants.labelBrowserNotification, ProfilesUIConstants.labelMentionsInBrowserNotification,
				ProfilesUIConstants.labelResponsesToMyInBrowserNotification, ProfilesUIConstants.emailNotifications,ProfilesUIConstants.labelHowOftenDoIWantToBeNotified));
		
		List<String> labelInEmailNotifications = new ArrayList<>(Arrays.asList(ProfilesUICnx8.getLabel("profiles"), ProfilesUICnx8.getLabel("communities"), ProfilesUICnx8.getLabel("blogs"),
				ProfilesUICnx8.getLabel("tags"), ProfilesUICnx8.getLabel("activities"), ProfilesUICnx8.getLabel("forums"),
				ProfilesUICnx8.getLabel("files"), ProfilesUICnx8.getLabel("bookmarks")));
		
		
		log.info("INFO: Verify labels 'Send emails to this address:','Receive notifications from other people by email' along with checkbox, and 'Email language:' along with language dropdown from 'Notification preferences' section");
		logger.strongStep("Verify labels 'Send emails to this address:','Receive notifications from other people by email' along with checkbox, and 'Email language:' along with language dropdown from 'Notification preferences' section");
		verifyingLabels(elementsInNotificationPreferences);
		
		log.info("INFO: Verify labels 'Mentions' and 'Responses to my content...'  from Browser Notification section and 'Email Notification' and 'How often do I want to be notified about:' from Email Notification section");
		logger.strongStep("Verify labels 'Mentions' and 'Responses to my content...'  from Browser Notification section and 'Email Notification' and 'How often do I want to be notified about:' from Email Notification section");
		verifyingLabels(labelsInBrowserNotification);
		
		log.info("INFO: Verify switches to turn on/off the setting respective to 'Mentions' and 'Responses to my content..'  from Browser Notification section");
		logger.strongStep("Verify switches to turn on/off the setting respective to 'Mentions' and 'Responses to my content..'  from Browser Notification section");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.mentionsSwitch)),"switch for mentions is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.responsesToMySwitch)),"switch for Responses To My... is present");
		
		log.info("INFO: Verify all drop down labels from 'Email Notification'section");
		logger.strongStep("Verify all drop down labels from 'Email Notification'section");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.xpath(ProfilesUIConstants.labelHowOftenDoIWantToBeNotified));
		verifyingLabels(labelInEmailNotifications); 
		
		log.info("INFO: Verify all drop downs along with their default selected values from 'Email Notification'section");
		logger.strongStep("Verify all drop downs along with their default selected values from 'Email Notification'section");
		verifyDropdownsAlongwithDefaultvalue();
	
		log.info("INFO: Verify buttons 'Restore Defaults','Save' and 'Close' present in footer section");
		logger.strongStep("Verify buttons 'Restore Defaults','Save' and 'Close' present in footer section");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.restoreDefaultsBtn)),"Button 'Restore Defaults' is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.saveBtn)),"Button 'Save' is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.cancelBtn)),"Button 'Cancel' is present");
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Test to verify setting of different default homepages</li>
	 * <li><B>Step:</B> Login to Homepage and Toggle to the new UI</li>
	 * <li><B>Step:</B> Navigate to Settings Page from nav menu & Select 'Default Homepage' option on Settings page</li>
	 * <li><B>Step:</B> Select 'Latest Updates' as default homepage & Navigate back to the Homepage</li>
	 * <li><B>Verify:</B> Verify 'Latest Updates' is displayed as default homepage</li>
	 * <li><B>Step:</B> Navigate to the Settings page again & Select 'Default Homepage' option on Settings page</li>
	 * <li><B>Step:</B> Select 'Discover' as default homepage & Navigate back to the Homepage</li>
	 * <li><B>Verify:</B> Verify 'Discover' is displayed as default homepage</li>
	 * <li><B>Step:</B> Navigate to the Settings page again & Select 'Default Homepage' option on Settings page</li>
	 * <li><B>Step:</B> Select 'My Page' as default homepage & Navigate back to the Homepage</li>
	 * <li><B>Verify:</B> Verify 'My Page' is displayed as default homepage</li>
	 * <li><B>Step:</B> Navigate to the Settings page again & Select 'Default Homepage' option on Settings page</li>
	 * <li><B>Step:</B> Select 'Top Updates' as default homepage & Navigate back to the Homepage</li>
	 * <li><B>Verify:</B> Verify 'Top Updates' is displayed as default homepage</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T741</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyDefaultHomepage() {		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		logger.strongStep("Load Homepage and login as: " +testUser.getDisplayName()+ " and toggle to New UI as:" + cfg.getUseNewUI());
		log.info("INFO: Load Homepage and login as: " +testUser.getDisplayName()+ " and toggle to New UI as:" + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentHomepage);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Select 'Latest Updates' as default homepage from Settings & verify it on homepage");
		log.info("INFO: Select 'Latest Updates' as default homepage from Settings & verify it on homepage");
		selectAndVerifyDefaultHomepage("Latest Updates");
		
		logger.strongStep("Select 'Discover' as default homepage from Settings & verify it on homepage");
		log.info("INFO: Select 'Discover' as default homepage from Settings & verify it on homepage");
		selectAndVerifyDefaultHomepage("Discover");
		
		logger.strongStep("Select 'My Page' as default homepage from Settings & verify it on homepage");
		log.info("INFO: Select 'My Page' as default homepage from Settings & verify it on homepage");
		selectAndVerifyDefaultHomepage("My Page");
		
		logger.strongStep("Select 'Top Updates' as default homepage from Settings & verify it on homepage");
		log.info("INFO: Select 'Top Updates' as default homepage from Settings & verify it on homepage");
		selectAndVerifyDefaultHomepage("Top Updates");
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Test to verify Default Homepage is at top in Secondary Left Nav on Settings page</li>
	 * <li><B>Step:</B> Login to Profiles and Toggle to the new UI</li>
	 * <li><B>Step:</B> Click on Settings tab</li>
	 * <li><B>Verify:</B> Verify 'Default Homepage' option is at top in Secondary Left Nav on Settings page</li>
	 * <li><B>Step:</B> Click on Default Homepage tab</li>
	 * <li><B>Verify:</B> Verify 'Default Homepage' option is at top in Secondary Left Nav on Settings page</li>
	 * <li><B>Step:</B> Click on Email Notifications tab</li>
	 * <li><B>Verify:</B> Verify 'Default Homepage' option is at top in Secondary Left Nav on Settings page</li>
	 *  * <li><B>Step:</B> Click on Application Access tab</li>
	 * <li><B>Verify:</B> Verify 'Default Homepage' option is at top in Secondary Left Nav on Settings page</li>
	 *  * <li><B>Step:</B> Click on Globalization tab</li>
	 * <li><B>Verify:</B> Verify 'Default Homepage' option is at top in Secondary Left Nav on Settings page</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T737</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T738</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2"})
	public void verifySecondaryLefNavFromSettingPage() {		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		logger.strongStep("Load Homepage and login as: " +testUser.getDisplayName()+ " and toggle to New UI as:" + cfg.getUseNewUI());
		log.info("INFO: Load Homepage and login as: " +testUser.getDisplayName()+ " and toggle to New UI as:" + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Setting tab and verify that Default Homepage is on Top in Secondary Left Nav");
		log.info("INFO: Click on Setting tab and verify that Default Homepage is on Top in Secondary Left Nav");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.settingsTab), 4);
		profilesUICnx8.waitForElementsVisibleWd(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage), 4);
		cnxAssert.assertEquals(profilesUICnx8.findElements(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage)).get(0).getText(), 
				profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings)).getText()," Verify Top element is Default Homepage");

		logger.strongStep("Click on Default Homepage and verify that Default Homepage is on Top in Secondary Left Nav");
		log.info("INFO: Click on Default Homepage and verify that Default Homepage is on Top in Secondary Left Nav");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings), 4, "Click on Default Homepage link");
		profilesUICnx8.waitForElementsVisibleWd(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage), 4);
		cnxAssert.assertEquals(profilesUICnx8.findElements(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage)).get(0).getText(), 
				profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings)).getText()," Verify Top element is Default Homepage");
		
		logger.strongStep("Click on Email Notification and verify that Default Homepage is on Top in Secondary Left Nav");
		log.info("INFO: Click on Email Notification and verify that Default Homepage is on Top in Secondary Left Nav");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.emailNotificationsLinkAtSettings), 4, "Click on Email Notification link");
		profilesUICnx8.waitForElementsVisibleWd(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage), 4);
		cnxAssert.assertEquals(profilesUICnx8.findElements(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage)).get(0).getText(), 
				profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings)).getText()," Verify Top element is Default Homepage");
		
		logger.strongStep("Click on Application Access and verify that Default Homepage is on Top in Secondary Left Nav");
		log.info("INFO: Click on Application Access Notification and verify that Default Homepage is on Top in Secondary Left Nav");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.applicationAccessLinkAtSettings), 4, "Click on Application Access link");
		profilesUICnx8.waitForElementsVisibleWd(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage), 8);
		cnxAssert.assertEquals(profilesUICnx8.findElements(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage)).get(0).getText(), 
				profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings)).getText()," Verify Top element is Default Homepage");
		
		logger.strongStep("Click on Globalization and verify that Default Homepage is on Top in Secondary Left Nav");
		log.info("INFO: Click on Globalization Notification and verify that Default Homepage is on Top in Secondary Left Nav");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.globalizationLinkAtSettings), 4, "Click on Globalization link");

		profilesUICnx8.waitForElementsVisibleWd(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage), 4);
		cnxAssert.assertEquals(profilesUICnx8.findElements(By.xpath(ProfilesUIConstants.secondaryLeftNavSettingPage)).get(0).getText(), 
				profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings)).getText()," Verify Top element is Default Homepage");
		
		profilesUICnx8.endTest();
	}
	
	
	/**
	 * Selecting and Verifying default page on homepage 
	 * @param pageName
	 */
	private void selectAndVerifyDefaultHomepage(String pageName) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.waitForPageLoaded(driver);
		
		logger.strongStep("Select Settings in nav menu");
		log.info("INFO: Select Settings in nav menu");
		AppNavCnx8.PROFILE.select(profilesUICnx8);
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.settingsTab), 4);
		profilesUICnx8.clickLinkWithJavaScriptWd(profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.settingsTab)));
		
		logger.strongStep("Click on 'Default Homepage' option on Settings page");
		log.info("INFO: Click on 'Default Homepage' option on Settings page");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.defaultHomepageLinkAtSettings), 4, "Default Homepage link");
		
		logger.strongStep("Select " +pageName+ " radio button in default homepage view");
		log.info("INFO: Select " +pageName+ " radio button in default homepage view");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.topNavPageRadioBtn.replace("PLACEHOLDER", pageName)), 4, pageName +" radio btn");
		
		logger.strongStep("Select Homepage in nav menu");
		log.info("INFO: Select Homepage in nav menu");
		AppNavCnx8.HOMEPAGE.select(profilesUICnx8);
		profilesUICnx8.waitForPageLoaded(driver);
		
		logger.strongStep("Verify selected page " +pageName+ " is displayed as default Homepage");
		log.info("INFO : Verify selected page " +pageName+ " is displayed on Homepage by default");
		cnxAssert.assertEquals(profilesUICnx8.getElementTextWd(By.cssSelector(ProfilesUIConstants.topNavSelectedItem)), pageName, pageName +" page is displayed");
		
	}

	/**
	 * Verifying different element are visible 
	 * @param elementLocators
	 */
	private void verifyingLabels(List<String> elementLocators) {
		for (String ele : elementLocators) {
			cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.xpath(ele), 4), "Label is present");
		}
	}

	/**
	 * Verifying drop downs  along with their default values 
	 */
	private void verifyDropdownsAlongwithDefaultvalue() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		Map<String, String> dropdowns = new HashMap<String, String>();
		dropdowns.put(ProfilesUICnx8.getDropdown("mentions"), "Individual Emails");
		dropdowns.put(ProfilesUICnx8.getDropdown("responses"), "Individual Emails");
		dropdowns.put(ProfilesUICnx8.getDropdown("profiles"), "Daily Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("communities"), "Daily Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("blogs"), "Daily Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("tags"), "Weekly Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("activities"), "Daily Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("forums"), "Daily Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("files"), "Individual Emails");
		dropdowns.put(ProfilesUICnx8.getDropdown("wikis"), "Weekly Newsletter");
		dropdowns.put(ProfilesUICnx8.getDropdown("bookmarks"), "Weekly Newsletter");

		Set<Entry<String, String>> mapEntries = dropdowns.entrySet();
		for (Entry<String, String> entry : mapEntries) {
			String dropdownName = entry.getKey().substring(entry.getKey().indexOf("_") + 1).toUpperCase();
			String defaultValue = entry.getValue();
			log.info("INFO: Verify dropdown " + dropdownName + " is present ");
			logger.strongStep("Verify dropdown " + dropdownName + " is present   ");
			cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.id(entry.getKey()), 4),
					"dropdown to respective label is present");

			log.info("INFO: Verify dropdown " + dropdownName + " default value is " + defaultValue);
			logger.strongStep("Verify dropdown " + dropdownName + " default value is " + defaultValue);
			Select dropdown = new Select(profilesUICnx8.findElement(By.id(entry.getKey())));
			cnxAssert.assertTrue(dropdown.getFirstSelectedOption().getText().equals(entry.getValue()),
					"Expetced default value for dropdown is present ");

		}

	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Test to verify Globalization screen </li>
	 * <li><B>Step:</B> Login to Homepage and Toggle to the new UI</li>
	 * <li><B>Step:</B> Navigate to Settings Page from nav menu</li>
	 * <li><B>Step:</B> Select 'Globalization' from tertiary navigation</li>
	 * <li><B>Verify:</B> Verify page heading 'Globalization Settings' is present </li>
	 * <li><B>Verify:</B> Verify the text 'Specify which calendar you prefer, and the direction that user-generated text flows.' </li>
	 * <li><B>Verify:</B> Verify label 'Calendar' along with dropdown is presnt</li>
	 * <li><B>Verify:</B> Verify label 'Enable bidirectional text' along with label is present</li>
	 * <li><B>Verify:</B> Verify label 'Direction of user-generated text: ' along with dropdown is present</li>
	 * <li><B>Verify:</B> Verify  buttons 'Restore Defaults','Save' and 'Cancel' are present </li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128991</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyGlobalizationScreen() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentSettings);
		profilesUICnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());
		profilesUICnx8.waitForPageLoaded(driver);
		
		logger.strongStep("Click on 'Globalization' from tertiary navigation");
		log.info("INFO: Click on 'Globalization' from tertiary navigation");
		profilesUICnx8.clickLinkWd(By.xpath(ProfilesUIConstants.globalizationLinkAtSettings), "Select Gobalization");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.globalizationHeaderText), 5);
		
		logger.strongStep("Verify page heading 'Globalization Settings' is present ");
		log.info("INFO: Verify page heading 'Globalization Settings' is present ");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationHeaderText)), "Globalization page header is presnt");
		
		logger.strongStep("Verify calendar label along with dropdown is present ");
		log.info("INFO: Verify calendar label along with dropdown is present  ");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationCalendarLabel)), "Calendar label is presnt on Globalization page");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationCalendarDropdown)), "Calendar dropdown is presnt on Globalization page");
		
		logger.strongStep("Verify 'Enable bidirectional textEnable bidirectional text' label along with checkbox is present");
		log.info("INFO: Verify 'Enable bidirectional textEnable bidirectional text' label along with checkbox is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationCheckboxToEnableBidircetionalTxt)), "Checkbox to enable bidircetional txt is presnt on Globalization page");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationCheckboxLabel)), "Label of checkbox to enable bidircetional txt is presnt on Globalization page");
		
		logger.strongStep("Verify label 'Direction Of user generated text' along with dropdown is present");
		log.info("INFO: Verify label 'Direction Of user generated text' along with dropdown is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationdirectionOfUserGenTxt)), "Label of direction Of user generated txt is presnt on Globalization page");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.globalizationdirectionOfUserGenTxtDropdown)), "Dropdwon of direction Of user generated txt is presnt on Globalization page");
		
		logger.strongStep("Verify buttons 'Restore Defaults' , 'Save' and 'Cancel' is present");
		log.info("INFO: Verify buttons 'Restore Defaults' , 'Save' and 'Cancel' is present");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.restoreDefaultsBtn)), "'Restore Defaults' is presnt on Globalization page");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.saveBtn)), "'Save' button is presnt on Globalization page");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.cancelBtn)), "'Cancel' on Globalization page");
		
		profilesUICnx8.endTest();
	}	
}
