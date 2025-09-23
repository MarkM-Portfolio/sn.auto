package com.ibm.conn.auto.tests.profiles;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;


public class BVT_Level_2_Profiles_SwitchURL_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Profiles_SwitchURL_MT_Boundary.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA, testUser_orgB;
	private String serverURL_MT_orgA, serverURL_MT_orgB;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();	
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);		
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able switch Profiles-My network URLs to orgB</li>
	 * <li><B>Step: </B> Go to My Profiles Page</li>
	 * <li><B>Step: </B> Select My Network tab</li>
	 * <li><B>Step: </B> Select different menus from left navigation like My Network contacts, Invitations, Following, Followers </li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 */		

	@Test(groups = { "mtlevel3" })
	public void switchURLProfilesMyNetwork() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and Login as a user
		logger.strongStep("Load Profile and Log In as " + testUser_orgA.getDisplayName());
		log.info("Load Profile and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentProfiles);
		ui.login(testUser_orgA);

		// select My Network tab
		logger.strongStep("Select My Network tab for profile");
		ui.clickLinkWait(ProfilesUIConstants.MyNetwork);

		// Get the list of element in my network left nav menu
		List<Element> names = driver.getElements(ProfilesUIConstants.NetworkLeftNavMenu);
		log.info("INFO: Names in list: " + names.size());
		logger.strongStep("Total number menu items in my network left nav menu: " + names.size());

		for (int i = 1; i < names.size(); i++) {
			
			ui.fluentWaitPresent(ProfilesUIConstants.NetworkLeftNav);
			names = driver.getElements(ProfilesUIConstants.NetworkLeftNavMenu);
			Element items = names.get(i);
			log.info("INFO: Menu is: " + items.getText());
			logger.strongStep("Element in my network left nav menu: " + items.getText());

			// Select left nav menu
			logger.strongStep("Select " + items.getText() + " element from my network left nav menu");
			items.click();
			ui.fluentWaitElementVisible(ProfilesUIConstants.pageSubTitle);

			// switch URL to orgB
			logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
			log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
			ui.switchToOrgBURL(serverURL_MT_orgB);

			// Validate error message
			logger.strongStep("Verify access denied error message should be displayed");
			log.info("Verify access denied error message should be displayed");
			ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");
			
			logger.strongStep("Navigate back from browser");
			driver.navigate().back();
		}

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test TypeAhead functionality for My Profiles & Directory in orgA login for OrgB users</li>
	 * <li><B>Step: </B> Login to OrgA.</li>
	 * <li><B>Step: </B> Go to My Profiles Page</li>
	 * <li><B>Step: </B> TypeAhead User from OrgB in Recent Updates tab</li>
	 * <li><B>Verify: </B>Verify that 'No results found' message should be displayed with option 'Person not listed? Use full search...'</li>
	 * <li><B>Step: </B> Select option 'Person not listed? Use full search...' </li>
	 * <li><B>Verify: </B>Verify 'No results found' message is displayed</li>
	 * <li><B>Step: </B> Go to Directory tab</li>
	 * <li><B>Step: </B> Search User name from OrgB.</li>
	 * <li><B>Verify: </B>Verify that 'Sorry, no results containing your search term(s) were found.' message displays</li>
	 * </ul>
	 */		

	@Test(groups = { "mtlevel2" })
	public void TypeAheadProfilesMyNetwork() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and Login as a user
		logger.strongStep("Load Profile and Log In as " + testUser_orgA.getDisplayName());
		log.info("Load Profile and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentProfiles);
		ui.login(testUser_orgA);

		ui.waitForPageLoaded(driver);
		
		//Validate1: Check TypeAhead Feature for Recently Updated
		//go to Recent updates tab
		logger.strongStep("Switch to the 'Recent Updates' tab");
		log.info("INFO: Switching to the Recent updates tab");
		ui.gotoRecentUpdates();
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.RecentupdateComment_iFrame);
		driver.getSingleElement(ProfilesUIConstants.UpdatesTextBox).click();
		
		log.info("INFO: Type With Delay @"+testUser_orgB.getUid() +"in Update Text Box.");
		logger.strongStep("Type With Delay @"+testUser_orgB.getUid() +"in Update Text Box.");
		driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).typeWithDelay("@"+testUser_orgB.getUid());
		ui.switchToTopFrame();
		
		//Verify the message 'No results found' appears after typing @USERNAME from OrgB in the text field
		log.info("INFO: Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
		logger.strongStep("Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
		Assert.assertTrue(driver.isTextPresent("No results found"),
				"The message 'No results found' does not appear after typing @"+testUser_orgB.getUid());
		
		//Click on the option 'Person not listed? Use full search...'
		log.info("INFO: Click on the option 'Person not listed? Use full search...'");
		logger.strongStep("Click on the option 'Person not listed? Use full search...'");
		ui.clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);

		//Verify that no results are found for user from orgB
		log.info("INFO: Verify that 'No results found' message is displayed");
		logger.strongStep("Validate that 'No results found' message is displayed");
		Assert.assertTrue(driver.isTextPresent("No results found"));
		
		//Click on Cancel Button
		log.info("INFO: Click on the Cancel button");
		logger.strongStep("Cancel the creation of community");
		ui.clickLinkWait(ProfilesUIConstants.clear);

		//Validate2: Check TypeAhead Functionality for Directory 
		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		ui.clickLinkWait(ProfilesUIConstants.DirectoryTab);
		logger.strongStep("Search for " + testUser_orgB.getDisplayName() +"and Verify " +  testUser_orgB.getDisplayName() + "is Not displayed");
		log.info("Search for " + testUser_orgB.getDisplayName() +"and Verify " +  testUser_orgB.getDisplayName() + "is Not displayed");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isDirectorySearchResultExactMatching(testUser_orgB));
		driver.turnOnImplicitWaits();
		ui.endTest();
	}
}
