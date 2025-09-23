package com.ibm.conn.auto.tests.profiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.menu.MyContacts_LeftNav_Menu;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.DogearUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import com.ibm.conn.auto.webui.onprem.ProfilesUIOnPrem;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

public class BVT_Cnx8UI_Profiles_Bizcard extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Profiles_Bizcard.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUserA, testUserB;
	private HomepageUICnx8 homepageCnx8ui;
	private ProfilesUI ui;
	private HomepageUI homepageUi;
	private ProfilesUICnx8 profilesUiCnx8;
	private String serverURL; 
	private APIProfilesHandler profilesAPIUserA,profilesAPIUserB;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		profilesAPIUserA = new APIProfilesHandler(serverURL, testUserA.getEmail(), testUserA.getPassword());
		profilesAPIUserB = new APIProfilesHandler(serverURL, testUserB.getEmail(), testUserB.getPassword());

	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		homepageCnx8ui = new HomepageUICnx8(driver);
		cnxAssert = new Assert(log);
		profilesUiCnx8 = new ProfilesUICnx8(driver);
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		homepageUi = HomepageUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Horizontal Ellipsis on User long Job title on Biz Card</li>
	 * <li><B>Step:</B> Login to Profiles Page with UserB and Toggle to the new UI</li>
	 * <li><B>Step:</B> Load My Profile view</li>
	 * <li><B>Step:</B> Go to contact information</li>
	 * <li><B>Verify:</B> Verify the User business card  is opened</li>
	 * <li><B>Step:</B> Click on Edit profile link</li>
	 * <li><B>Step:</B> Set long job title and Save</li>
	 * <li><B>Step:</B> Logout as UserB, login as UserA and Toggle to the new UI</li>
	 * <li><B>Step:</B> Load Profiles, Navigate to People page and Click on Directory tab</li>
	 * <li><B>Step:</B> Search for UserB and hover on UserB link to populate the Bizcard</li> 
	 * <li><B>Verify:</B> Verify horizontal ellipsis and tooltip on Job title on Biz Card</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T725</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T727</li>
	 * Note : Horizontal ellipsis does not have seperate tag in DOM and complete title is captured without ellipsis in <p> tag of Dom 
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyHorizontalEllipsisOnUserLongJobtitleOnBizcard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		homepageCnx8ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testUserB.getDisplayName())
				.jobTitle("Senior Chief Executive Officer of HCL Technologies Pune India").build();

		logger.strongStep("Load Profiles, login as " + testUserB.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Profiles, login as " + testUserB.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageCnx8ui.loadComponent(Data.getData().ComponentProfiles);
		homepageCnx8ui.loginAndToggleUI(testUserB, cfg.getUseNewUI());

		logger.strongStep("Select the Contact Information tab");
		log.info("INFO: Select the Contact Information tab");
		ui.gotoContactInformation();

		logger.strongStep("Click on Edit Profile and set long Job title");
		log.info("INFO: Click on Edit Profile and set long Job title");
		profile.setJobTitle(profile.getJobTitle());
		profile.edit(ui);

		logger.strongStep("Logout as "+testUserB.getEmail()+" and login as "+testUserA.getEmail());
		log.info("INFO: Logout as "+testUserB.getEmail()+" and login as "+testUserA.getEmail());
		ui.logout();
		driver.close();

		logger.strongStep("Load Profiles, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Profiles, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageCnx8ui.loadComponent(Data.getData().ComponentProfiles);
		homepageCnx8ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("INFO : Navigate to People page and Click on Directory tab");
		log.info("INFO : Navigate to People page and Click on Directory tab");
		AppNavCnx8.PEOPLE.select(profilesUiCnx8);
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab),3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));

		logger.strongStep("Search for " + testUserB.getDisplayName() + "and Verify " + testUserB.getDisplayName() + "is displayed");
		log.info("INFO : Search for " + testUserB.getDisplayName() + "and Verify " + testUserB.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUserB),
				"Profile Search result is displayed");

		logger.strongStep("Hover on " + testUserB.getLastName() + "and Verify Business card");
		log.info("INFO: Hover on " + testUserB.getLastName() + "and Verify Business card");
		Element userName = profilesUiCnx8.getFirstVisibleElement(HomepageUIConstants.userNamelink
				+ "[@title='PLACEHOLDER1']".replace("PLACEHOLDER1", testUserB.getDisplayName()));
		userName.hover();

		logger.strongStep("Verify the long job title and tooltip of "+testUserB.getDisplayName()+ " on Bizcard" );
		log.info("INFO: Verify the long job title and tooltip of "+testUserB.getDisplayName() + " on Bizcard");
		cnxAssert.assertEquals(profilesUiCnx8.findElement(By.cssSelector(HomepageUIConstants.bizCardUserJobTitle))
				.getAttribute("title").trim(), profile.getJobTitle(), "Verify  User job title and ITS tooltip");
		
		homepageCnx8ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify Disabled invite on logged-in user Biz Card</li>
	 * <li><B>Step:</B> Load Homepage, login to application with UserA and Toggle to the new UI</li>
	 * <li><B>Step:</B> Navigate to People page and click on Directory tab</li>
	 * <li><B>Step:</B> Search for UserA and hover on UserA link to populate the Bizcard</li> 
	 * <li><B>Verify:</B> Verify the disabled invite icon on right top of Biz Card</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T723</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyNoDisabledInviteOnSelfBizcard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		homepageCnx8ui.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageCnx8ui.loadComponent(Data.getData().ComponentHomepage);
		homepageCnx8ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		AppNavCnx8.PEOPLE.select(profilesUiCnx8);
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab),3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));

		logger.strongStep("Search for " + testUserA.getDisplayName() + "and Verify " + testUserA.getDisplayName() + "is displayed");
		log.info("INFO: Search for " + testUserA.getDisplayName() + "and Verify " + testUserA.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUserA),
				"Profile Search result is displayed");
		
		logger.strongStep("Hover on " + testUserA.getLastName());
		log.info("INFO: Hover on " + testUserA.getLastName());
		Element userName = profilesUiCnx8.getFirstVisibleElement(HomepageUIConstants.userNamelink
				+ "[@title='PLACEHOLDER1']".replace("PLACEHOLDER1", testUserA.getDisplayName()));
		userName.hover();
		
		logger.strongStep("Verify that invite to my network under vertical Ellipsis dropdown on Bizcard is not displayed" );
		log.info("INFO: Verify that invite to my network under vertical Ellipsis dropdown on Bizcard is not displayed");
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.bizCardVerticalEllipsisIcon), 4);
		cnxAssert.assertFalse(profilesUiCnx8.isElementVisibleWd(homepageCnx8ui.createByFromSizzle(HomepageUIConstants.bizCardInviteToMyNetwork),5),
				"Verify invite to my network is not displayed");
		
		logger.strongStep("Verify that disabled userIcon on Bizcard is not displayed" );
		log.info("INFO: Verify that disabled userIcon on Bizcard is not displayed");
		cnxAssert.assertFalse(profilesUiCnx8.isElementVisibleWd(By.cssSelector(HomepageUIConstants.bizCardDisabledInvite),5),
				"Verify disabled userIcon is not displayed");
		
		homepageCnx8ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify UserB Biz Card details</li>
	 * <li><B>Step:</B> Load Homepage, login to application with UserA and Toggle to the new UI</li>
	 * <li><B>Step:</B> Navigate to People page and click on Directory tab</li>
	 * <li><B>Step:</B> Search for UserB and hover on UserB link to populate the Bizcard</li> 
	 * <li><B>Verify:</B> Verify the elements displayed on Biz Card</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T719</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T720</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T726</li> 
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyDetailsOfBizcard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		homepageCnx8ui.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageCnx8ui.loadComponent(Data.getData().ComponentHomepage);
		homepageCnx8ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		AppNavCnx8.PEOPLE.select(profilesUiCnx8);
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab),3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));

		logger.strongStep("Search for " + testUserB.getDisplayName() + " and Verify " + testUserB.getDisplayName() + "is displayed");
		log.info("INFO: Search for " + testUserB.getDisplayName() + " and Verify " + testUserB.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUserB),
				"Profile Search result is displayed");
		
		logger.strongStep("Hover on " + testUserB.getLastName());
		log.info("INFO: Hover on " + testUserB.getLastName());
		Element userName = profilesUiCnx8.getFirstVisibleElement(HomepageUIConstants.userNamelink
				+ "[@title='PLACEHOLDER1']".replace("PLACEHOLDER1", testUserB.getDisplayName()));
		userName.hover();
		
		logger.strongStep("Hover on " + testUserB.getLastName() + "and Verify Business card");
		log.info("Hover on " + testUserB.getLastName() + "and Verify Business card");
		homepageUi.verifyBizCardContent(testUserB.getDisplayName());
		
		homepageCnx8ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify open Chat on  Biz Card</li>
	 * <li><B>Step:</B> Load Homepage, login to application with UserA and Toggle to the new UI</li>
	 * <li><B>Step:</B> Navigate to People page and click on Directory tab</li>
	 * <li><B>Step:</B> Search for UserB and hover on UserB link to populate the Bizcard</li> 
	 * <li><B>Step:</B> click on chat icon on Bizcard</li> 
	 * <li><B>Verify:</B> Verify new window of Teams get open</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T721</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyOpenChatOnBizcard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		homepageCnx8ui.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageCnx8ui.loadComponent(Data.getData().ComponentHomepage);
		homepageCnx8ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("INFO : Navigate to People page and Click on Directory tab");
		log.info("INFO : Navigate to People page and Click on Directory tab");
		AppNavCnx8.PEOPLE.select(profilesUiCnx8);
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab),3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));

		logger.strongStep("Search for " + testUserB.getDisplayName() + "and Verify " + testUserB.getDisplayName() + "is displayed");
		log.info("INFO: Search for " + testUserB.getDisplayName() + "and Verify " + testUserB.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUserB),
				"Profile Search result is displayed");
		
		logger.strongStep("Hover on " + testUserB.getLastName());
		log.info("INFO: Hover on " + testUserB.getLastName());
		Element userName = profilesUiCnx8.getFirstVisibleElement(HomepageUIConstants.userNamelink
				+ "[@title='PLACEHOLDER1']".replace("PLACEHOLDER1", testUserB.getDisplayName()));
		userName.hover();

		logger.strongStep("INFO : Click on Chat icon");
		log.info("INFO : Click on Chat icon");
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.bizCardChatIcon), 5);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(By.cssSelector(HomepageUIConstants.bizCardChatIcon)));
		
		logger.strongStep("INFO : Verify title of Teams window");
		log.info("INFO : Verify title of Teams window");
		
		if(testConfig.browserIs(BrowserType.FIREFOX))
		{
			homepageCnx8ui.switchToNextWindowWd("teams");
		}
		else 
		{
			homepageCnx8ui.switchToNextTab();
		}
		cnxAssert.assertEquals(((WebDriver)driver.getBackingObject()).getTitle(),"Join conversation","verify title of Teams window");
		
		logger.strongStep("INFO : Closing all windows");
		log.info("INFO : Closing all windows");
		driver.quit();
		
		homepageCnx8ui.endTest();
	
	}


	/**
	 * <ul>
	 * <li><B>Info:</B> Verify three dots drop down on Biz Card</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Load My Profile view</li>
	 * <li><B>Step:</B> Open Business card</li>
	 * <li><B>Verify:</B> Verify the User business card  is opened</li>
	 * <li><B>Step:</B> Click on ellipsis icon on Biz Card</li>
	 * <li><B>Verify:</B> Verify all links are presents under ellipsis drop-down on Biz Card</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T724</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T728</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyVerticalEllipsisActionListsOnProfileBizCard() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();

		logger.strongStep("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Load My Profile view");
		log.info("INFO: Load My Profile view");
		ui.myProfileView();
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Open Business card");
		log.info("INFO: Open Business card");
		ui.openProfileBusinessVcard();
		
		logger.strongStep("INFO: Verify the User business card  is opened");
		log.info("INFO: Verify the User business card  is opened");
		cnxAssert.assertTrue(ui.fluentWaitElementVisible(ProfilesUIConstants.BusinessCardWindow), "ERROR: The User business card  is opened");
		
		logger.strongStep("INFO: Click on ellipsis icon on Biz Card");
		log.info("INFO: Click on ellipsis icon on Biz Card");
		profilesUiCnx8.clickThreeDotsDropDownIcon();
		
		logger.strongStep("INFO: Verify all links are presents under ellipsis drop-down on Biz Card");
		log.info("INFO: Verify all links are presents under ellipsis icon drop-down on Biz Card");
		ui.verifyLinksInBusinessCard();
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify three dots drop down on Biz Card</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Load My Profile view</li>
	 * <li><B>Verify:</B> Select Component from Action list and validate</li>
	 * <li><B>Profile JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T729</li>
	 * <li><B>Communities JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T731</li>
	 * <li><B>Files JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T732</li>
	 * <li><B>Bookmarks JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T734</li>
	 * <li><B>Blogs JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T735</li>
	 * <li><B>Wikis JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T736</li>
	 * <li><B>Activities JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T730</li>
	 * <li><B>Forums JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T733</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2","cnx8ui-level2" })
	public void verifyComponentsLinkFromVerticalEllipsisActionLists() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();

		logger.strongStep("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);

		logger.strongStep("INFO: Select Component from Action list and validate");
		log.info("INFO: Select Component from Action list and validate");
		selectComponentAndPageValidation();
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify User should be able to see 'Network Contact' icon on Bizcard when that user is in network</li>
	 * <li><B>Step:</B> [API] UserA will now invite UserB to join their network</li>
	 * <li><B>Step:</B> [API] UserB will now accept invite from UserA to join their network</li>
	 * <li><B>Step:</B> Load Profiles and login with UserA</li>
	 * <li><B>Step:</B>  Select 'People' from left side navigation</li>
	 * <li><B>Step:</B>  Click on Directory and Search UserB </li>
	 * <li><B>Step:</B>  Hover on UseB and Bizcard will get displayed</li>
	 * <li><B>Verify:</B> Verify that disabled UserIcon is displayed on right top of Bizcard</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T723</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" ,"cnx8ui-level2"})
	public void verifyDisabledUserIconOnBizcard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		profilesUiCnx8.startTest();
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUserA.deleteUserFromNetworkConnections(profilesAPIUserB);
		
		log.info("INFO: " + testUserA.getDisplayName() + " sends invite to " + testUserB.getDisplayName());
		logger.strongStep(testUserA.getDisplayName() + " sends invite to " + testUserB.getDisplayName());
		Invitation user2NetworkInvite = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUserA, profilesAPIUserB);

		log.info("INFO: "+testUserB.getDisplayName()+" now accept invite from "+testUserA.getDisplayName());
		logger.strongStep(testUserB.getDisplayName()+" now accept invite from "+testUserA.getDisplayName());
		ProfileEvents.acceptInvitationToJoinANetwork(user2NetworkInvite, profilesAPIUserB, profilesAPIUserA);
		
		log.info("INFO: Load Profiles and login: " + testUserA.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Homepage and login: " + testUserA.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUiCnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUiCnx8.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		AppNavCnx8.PEOPLE.select(profilesUiCnx8);
		profilesUiCnx8.waitForClickableElementWd(profilesUiCnx8.createByFromSizzle(ProfilesUIConstants.DirectoryTab),3);
		profilesUiCnx8.clickLinkWithJavaScriptWd(profilesUiCnx8.findElement(profilesUiCnx8.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		profilesUiCnx8.waitForPageLoaded(driver);

		logger.strongStep("Search for " + testUserB.getDisplayName() + "and Verify " + testUserB.getDisplayName() + "is displayed");
		log.info("INFO: Search for " + testUserB.getDisplayName() + "and Verify " + testUserB.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUserB),
				"Profile Search result is displayed");
		
		logger.strongStep("Hover on " + testUserB.getLastName());
		log.info("INFO: Hover on " + testUserB.getLastName());
		Element userName = profilesUiCnx8.getFirstVisibleElement(HomepageUIConstants.userNamelink
				+ "[@title='PLACEHOLDER1']".replace("PLACEHOLDER1", testUserB.getDisplayName()));
		userName.hover();

		logger.strongStep("Verify that disabled userIcon on Bizcard is displayed" );
		log.info("INFO: Verify that disabled userIcon on Bizcard is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isElementVisibleWd(By.cssSelector(HomepageUIConstants.bizCardDisabledInvite),5),
				"Verify disabled userIcon is displayed");	
		
		profilesUiCnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify download Vcard on  Biz Card</li>
	 * <li><B>Step:</B> Load Homepage, login to application with UserA and Toggle to the new UI</li>
	 * <li><B>Step:</B> Navigate to People page and click on Directory tab</li>
	 * <li><B>Step:</B> Search for UserA and hover on UserA link to populate the Bizcard</li> 
	 * <li><B>Step:</B> click on vertical Ellipsis and click on download Vcard</li> 
	 * <li><B>Verify:</B> Verify new window appears of Vcard</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T7541</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" }, enabled = true)
	public void verifyDownloadVcardFromfBizcard() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		homepageCnx8ui.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUserA.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		homepageCnx8ui.loadComponent(Data.getData().ComponentHomepage);
		homepageCnx8ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());

		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		AppNavCnx8.PEOPLE.select(profilesUiCnx8);
		homepageCnx8ui.waitForClickableElementWd(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab),3);
		homepageCnx8ui.clickLinkWithJavaScriptWd(homepageCnx8ui.findElement(homepageCnx8ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));

		logger.strongStep("Search for " + testUserA.getDisplayName() + "and Verify " + testUserA.getDisplayName() + "is displayed");
		log.info("INFO: Search for " + testUserA.getDisplayName() + "and Verify " + testUserA.getDisplayName() + "is displayed");
		cnxAssert.assertTrue(profilesUiCnx8.isDirectorySearchResultExactMatching(testUserA),
				"Profile Search result is displayed");
		
		logger.strongStep("Hover on " + testUserA.getLastName());
		log.info("INFO: Hover on " + testUserA.getLastName());
		Element userName = profilesUiCnx8.getFirstVisibleElement(HomepageUIConstants.userNamelink
				+ "[@title='PLACEHOLDER1']".replace("PLACEHOLDER1", testUserA.getDisplayName()));
		userName.hover();
		
		logger.strongStep("Verify that invite to my network under vertical Ellipsis dropdown on Bizcard is not displayed" );
		log.info("INFO: Verify that invite to my network under vertical Ellipsis dropdown on Bizcard is not displayed");
		homepageCnx8ui.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.bizCardVerticalEllipsisIcon), 4);
		
		log.info("INFO: Click Download Vcard menu item");
		logger.strongStep("Click Download Vcard menu item");
		homepageCnx8ui.scrollToElementWithJavaScriptWd(By.xpath(HomepageUIConstants.bizCardDownloadVcard));
		homepageCnx8ui.clickLinkWd(By.xpath(HomepageUIConstants.bizCardDownloadVcard));
		
		log.info("INFO: Verify the Export vCard dialog is displayed");
		logger.strongStep("Verify the Export vCard dialog is displayed");
		homepageCnx8ui.switchToNextWindowWd("exportVCard");
		homepageCnx8ui.waitForElementVisibleWd(By.xpath(HomepageUIConstants.bizCardExportVcardForm), 5);
		cnxAssert.assertTrue(homepageCnx8ui.isElementDisplayedWd(By.xpath(HomepageUIConstants.bizCardExportVcardForm)),
				"The Export vCard dialog is displayed");
		
		homepageCnx8ui.endTest();
	}
	
		
			
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Select Component from Action List and Verify Page navigation</li>
	 * <li><B>Step:</B> Mouse hover on User Profile and Business Card</li>
	 * <li><B>Step:</B> Click on ellipsis icon on Biz Card</li>
	 * <li><B>Verify:</B> Validate Application is navigating with Component page</li> 
	 * <li><B>Step:</B>  Load Profile Page</li> 
	 * </ul>
	 */	
	public void selectComponentAndPageValidation() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		Map <String, String> component = new HashMap <String,String>();
		
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("profiles"), ProfilesUICloud.FriendsLink);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("activities"), ActivitiesUIConstants.huddoIcon);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("communities"), CommunitiesUIConstants.StartACommunityDropDownCardView);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("files"), FilesUIConstants.MyDriveLeftMenu);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("forums"), ForumsUIConstants.feedLink);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("dogear"), DogearUIConstants.AddABookmark);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("blogs"), BlogsUIConstants.myBlogsTab);
		component.put(ProfilesUICnx8.getEllipsisDropDownValue("wikis"), WikisUIConstants.publicWikisearchResult);

		for (Entry<String, String> entry : component.entrySet()) {
			String componentName = entry.getKey();
			String componentPageLocator = entry.getValue();
			
			logger.strongStep("INFO: Mouse hover on User Profile and Business Card");
			log.info("INFO: Mouse hover on User Profile and Business Card");
			ui.getFirstVisibleElement(ProfilesUIOnPrem.BusinessCardVcard).hover();
			ui.getFirstVisibleElement(ProfilesUIOnPrem.BusinessCardWindow).hover();
			
			logger.strongStep("INFO: Click on vertical ellipsis icon on Biz Card");
			log.info("INFO: Click on vertical ellipsis icon on Biz Card");
			profilesUiCnx8.clickThreeDotsDropDownIcon();
			
			logger.strongStep("INFO: Click on" + componentName +"Link from Ellipsis Action List");
			log.info("INFO: Click on" + componentName +"Link from Ellipsis Action List");
			homepageCnx8ui.waitForElementVisibleWd(By.xpath(componentName), 3);
			homepageCnx8ui.clickLinkWaitWd(By.xpath(componentName), 3,"Clicked on Component link");
			ui.waitForPageLoaded(driver);
			
			Boolean pageValidation = false;	

			logger.strongStep("Validate Application is navigating with Component page");
			log.info("INFO: Validate Application is navigating with Component page");
			if (cfg.getIsKudosboardEnabled()) {
				pageValidation = ui.isElementVisible(componentPageLocator);
				cnxAssert.assertTrue(pageValidation, "" + componentName + " page is validated");
			} else {
				if (componentName.contains("activities")) {
					component.put(ProfilesUICnx8.getEllipsisDropDownValue("activities"),
							ActivitiesUIConstants.Start_An_Activity);
				}
				componentPageLocator = entry.getValue();
				pageValidation = ui.isElementVisible(componentPageLocator);
				cnxAssert.assertTrue(pageValidation, "" + componentName + " page is validated");

			}
			
			logger.strongStep("INFO: Load Profile Page");
			log.info("INFO: Load Profile Page");
			ui.loadComponent(Data.getData().ComponentProfiles, true);
			
		}
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the changes on directory page for People - Another user overflow</li>
	 * <li><B>Step:</B> [API] User 1 will follow User 2</li>
	 * <li><B>Step:</B> Load Profiles and login with User 1</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Select Directory tab</li>
	 * <li><B>Step:</B> Search user in directory</li>
	 * <li><B>Step:</B> Click on searched user from result</li>
	 * <li><B>Verify:</B> Verify 'Invite to My Network' and 'Follow' icon is visible</li>
	 * <li><B>Step:</B> Click on 'Follow' icon</li>
	 * <li><B>Verify:</B> Verify 'Follow' icon turned to be 'Following' and is disabled</li>
	 * <li><B>Verify:</B> Verify 'Stop Following' option is available under 3 dot action menu</li>
	 * <li><B>Step:</B> Click on 'Stop Following' menu from list</li>
	 * <li><B>Verify:</B> Verify 'Follow' icon is enabled</li>
	 * <li><B>Verify:</B> Stop Following icon should not be visible in dropdown menu</li>
	 * <li><B>Verify:</B> Click on Invite to my network icon</li>
	 * <li><B>Verify:</B> Verify the invite icon on Profile Card should be changed to Network Contact and follow icon should be changed to Following and both the icons  should be disabled</li>
	 * <li><B>Verify:</B> Pending Inviation text should be displayed on the top right corner of profile card</li>
	 * <li><B>Verify:</B> Stop Following icon should be visible in dropdown menu</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T763</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T764</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T765</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyFollowUnfollowIcons() 
	{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUserA.deleteUserFromNetworkConnections(profilesAPIUserB);
		 
		log.info("INFO: Load Profiles and login: " + testUserA.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUserA.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUserA, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(ui);
		
		logger.strongStep("INFO: Select 'Directory' tab");
		ui.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.DirectoryTab)));
		
		log.info("INFO: Search user "+testUserB.getDisplayName()+" in directory");
		logger.strongStep("Search user in directory");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.directorSearchBox), 4);
		ui.typeWithDelayWd(testUserB.getDisplayName(), By.xpath(ProfilesUIConstants.directorSearchBox));
		
		log.info("INFO: Click on "+testUserB.getDisplayName()+" displayed in My Network list");
		logger.strongStep("Click on "+testUserB.getDisplayName()+"  displayed in My Network list");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserLinkFromDirSearchResult(testUserB.getDisplayName())), 4);
		ui.mouseHoverAndClickWd(ui.findElement(By.xpath(ProfilesUICnx8.getUserLinkFromDirSearchResult(testUserB.getDisplayName()))));
		
		log.info("INFO: Invite to my Network and Follow option should be visible on users profile card");
		logger.strongStep("Invite to my Network and Follow option should be visible on users profile card");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.id(ProfilesUIConstants.inviteToMyNetworkIcon), 5), "Invite To My Network icon is visible");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.id(ProfilesUIConstants.followIcon), 5), "Follow icon is visible");
		
		log.info("INFO: Click follow icon");
		logger.strongStep("Click follow icon");
		ui.clickLinkWithJavaScriptWd(ui.findElement(By.id(ProfilesUIConstants.followIcon)));
		cnxAssert.assertTrue(ui.waitForElementInvisibleWd(By.id(ProfilesUIConstants.followIcon), 4),"");
		
		log.info("INFO: Verify 'Follow' icon turned to be 'Following' and is disabled");
		logger.strongStep("Verify 'Follow' icon turned to be 'Following' and is disabled");
		cnxAssert.assertTrue(ui.findElement(By.id(ProfilesUIConstants.unFollowIcon)).getText().equals("Following"), "Follow icon turned to 'Following'");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.id(ProfilesUIConstants.unFollowIcon)), "Follow icon is disabled");
		
		log.info("INFO: Click on three dots icon(more action)");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4, "Click on three dots icon(more action) ");
		
		log.info("INFO: Verify 'Stop Following' option is available under 3 dot action menu");
		logger.strongStep("Verify 'Stop Following' option is available under 3 dot action menu");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(ProfilesUIConstants.stopFollowingOnThreeDots)), "Stop following option on action menu undr 3 dots");
		
		log.info("INFO: Click of 'Stop Following' option the user should again see the Follow icon on top ");
		logger.strongStep("Select 'Stop Following' option from 3 dot action menu");
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.stopFollowingOnThreeDots), 4, "Select Stop following option on action menu undr 3 dots");
		ui.fluentWaitTextPresent(testUserB.getDisplayName()+" has been removed from your following list");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.id(ProfilesUIConstants.followIcon), 5), "Follow icon is visible");
		
		log.info("INFO: Stop Following icon should not be visible in dropdown menu");
		logger.strongStep("Stop Following icon should not be visible in dropdown menu");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4, "Click on three dots icon(more action) ");
		cnxAssert.assertTrue(!ui.isElementPresentWd(By.id(ProfilesUIConstants.stopFollowingOnThreeDots)), "Stop following option on action menu undr 3 dots");
		
		log.info("INFO: Click on Invite to my network icon");
		logger.strongStep("Click on Invite to my network icon");
		ui.clickLinkWithJavaScriptWd(ui.findElement(By.xpath(ProfilesUIConstants.closeButtonForMsg)));
		ui.waitForElementInvisibleWd(By.xpath(ProfilesUIConstants.closeButtonForMsg), 4);
		ui.waitForClickableElementWd(By.id(ProfilesUIConstants.inviteToMyNetworkIcon), 5);
		ui.clickLinkWd(ui.findElement(By.id(ProfilesUIConstants.inviteToMyNetworkIcon)));
		ui.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.InviteToMyNetworkDailogueBox), 10);
		ui.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.SendInvite)));
		
		log.info("INFO: Verify the invite icon on Profile Card should be changed to Network Contact and follow icon should be changed to Following and both the icons  should be disabled");
		logger.strongStep("Verify the invite icon on Profile Card should be changed to Network Contact and follow icon should be changed to Following and both the icons  should be disabled");
		ui.fluentWaitTextPresent(testUserB.getDisplayName()+" has been invited to your network contact list.");
		cnxAssert.assertTrue(ui.findElement(By.id(ProfilesUIConstants.networkContactIcon)).getText().equals("Network Contact"), "Invite to my network icon turned to 'Network Contact'");
		cnxAssert.assertTrue(ui.findElement(By.id(ProfilesUIConstants.unFollowIcon)).getText().equals("Following"), "Follow icon turned to 'Following'");
		cnxAssert.assertTrue(!ui.isElementVisibleWd(By.id(ProfilesUIConstants.inviteToMyNetworkIcon), 5), "Stop following option on action menu undr 3 dots");
		cnxAssert.assertTrue(!ui.isElementVisibleWd(By.id(ProfilesUIConstants.followIcon), 5), "Stop following option on action menu undr 3 dots");
		
		log.info("INFO: Pending Inviation text should be displayed on the top right corner of profile card");
		logger.strongStep("Pending Inviation text should be displayed on the top right corner of profile card");
		cnxAssert.assertTrue(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.ConnectionIndicator)).getText().equals("Pending Invitation..."), "'Pending Invitation...' is displayed at the top right corner");
		
		log.info("INFO: Stop Following icon should be visible in dropdown menu");
		logger.strongStep("Stop Following icon should be visible in dropdown menu");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4, "Click on three dots icon(more action) ");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.xpath(ProfilesUIConstants.stopFollowingOnThreeDots)),"Stop following option on action menu undr 3 dots");
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify user should be able to accept invitation</li>
	 * <li><B>Step:</B> [API] testUserA 1 sends invite to testUserB</li>
	 * <li><B>Step:</B> Load Profiles and login with testUserB</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Select Invitations tab from left nav</li>
	 * <li><B>Step:</B> Go to testUserA's profile by clicking username link </li>
	 * <li><B>Verify:</B> Verify 'Accept Invitation' icon is visible</li>
	 * <li><B>Step:</B> Click on 'Accept Invitation' icon</li>
	 * <li><B>Verify:</B> Verify 'Accept Invitation' icon turned to be 'Network Contact' and is disabled</li>
	 * <li><B>Verify:</B> Verify 'Remove From Network' option is available under 3 dot action menu</li>
	 * <li><B>Verify:</B> User should be able to see Network Contact text on the top right corner of the profile card</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T766</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyAcceptInvitation()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		log.info("INFO: Load Profiles and login: " + testUserB.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUserB.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles,true);
		ui.loginAndToggleUI(testUserB, cfg.getUseNewUI());
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUserA.deleteUserFromNetworkConnections(profilesAPIUserB);
		
		log.info("INFO: " + testUserA.getDisplayName() + " sends invite to " + testUserB.getDisplayName());
		logger.strongStep(testUserA.getDisplayName() + " sends invite to " + testUserB.getDisplayName());
		ProfileEvents.inviteUserToJoinNetwork(profilesAPIUserA, profilesAPIUserB);

		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(ui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Select 'Invitations' from left panel");
		logger.strongStep("Select 'Invitations' from left panel");
		MyContacts_LeftNav_Menu.INVITATIONS.open(ui);
		
		log.info("INFO: Go to "+testUserA.getDisplayName()+" profile by selecting user from list");
		logger.strongStep("Go to "+testUserA.getDisplayName()+" profile by selecting user from list");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserFromInvitationTab(testUserA.getDisplayName())), 4);
		ui.mouseHoverAndClickWd(ui.findElement(By.xpath(ProfilesUICnx8.getUserFromInvitationTab(testUserA.getDisplayName()))));
		ui.waitForElementVisibleWd(By.id(ProfilesUIConstants.acceptInvitationIcon), 4);
		
		log.info("INFO: Verify 'Accept Invitation' icon is visible");
		logger.strongStep("Verify 'Accept Invitation' icon is visible");
		cnxAssert.assertTrue(ui.isElementPresentWd(By.id(ProfilesUIConstants.acceptInvitationIcon)), "Accept Invitation icon is displayed");
		
		log.info("INFO: Click on 'Accept Invitation' icon ");
		logger.strongStep("Click on 'Accept Invitation' icon ");
		ui.clickLinkWithJavaScriptWd(ui.findElement(By.id(ProfilesUIConstants.acceptInvitationIcon)));
		ui.fluentWaitTextPresent(testUserA.getDisplayName() + " has been added to your network contact list.");

		log.info("INFO: Verify 'Accept Invitation' icon turned to be 'Network Contact' and is disabled");
		logger.strongStep("Verify 'Accept Invitation' icon turned to be 'Network Contact' and is disabled");
		WebElement networkContactIcon = ui.findElement(By.id(ProfilesUIConstants.networkContactIcon));
		cnxAssert.assertTrue(networkContactIcon.getAttribute("aria-disabled").equals("true"), "'Accept Invitation' icon turned to be 'Network Contact' and is disabled");

		log.info("INFO: Click on three dots icon(more action)");
		ui.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4,"Click on three dots icon(more action) ");
		
		log.info("INFO: Verify 'Remove From Network' option is available under 3 dot action menu");
		logger.strongStep("Verify 'Remove From Network' option is available under 3 dot action menu");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(ProfilesUICnx8.getActionMenu("Remove From Network")), 5), "Remove From Network option is visible");
		
		//Below validation is removed based on latest design changes confirmed in https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14543
		/* log.info("INFO: User should be able to see 'Network Contact' text on the top right corner of the profile card");
		logger.strongStep("User should be able to see 'Network Contact' text on the top right corner of the profile card");
		cnxAssert.assertTrue(ui.isElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.ConnectionIndicator), 5), "'Network Contact' is displayed at the top right corner");
		cnxAssert.assertTrue(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.ConnectionIndicator)).getText().equals("Network Contact"), "'Network Contact' is displayed at the top right corner"); */
		ui.endTest();
	}

}
