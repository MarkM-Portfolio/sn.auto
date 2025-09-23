package com.ibm.conn.auto.tests.share;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.cnx8.ShareUICnx8;
import com.ibm.conn.auto.webui.constants.ShareUIContants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Cnx8_UI_ShareInConnections_MemberPicker extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8_UI_ShareInConnections_MemberPicker.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, testUser2;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private HomepageUI homepageUI;
	private ShareUICnx8 shareUI;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		homepageUI = HomepageUI.getGui(cfg.getProductName(), driver);
		shareUI = new ShareUICnx8(driver);
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b>Test case to verify share suggestions message in MemberPicker</li>
	 * <li><b>Step:</b>Login to Homepage</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Click on drop down icon in 'Select name' input field before searching any Username</li>
	 * <li><b>Verify:</b>Correct suggestions message is visible</li>
	 * <li><b>Step:</b>Logout and close the browser</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T670</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyRecentShareSuggestionMessage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String expectedSuggestionMsg = "Type the name of a person or community. As you grow your network, you'll see a list of names to pick from.";
		User testUser3 = cfg.getUserAllocator().getUser();
		
		homepageUI.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUser3.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser3.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser3, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Click on drop down icon in input feild");
		log.info("INFO: Click on drop down icon in input feild");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.searchTypeAhead), 4, "Input box");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.mUIDropDownIcon), 4, "Drop down icon");
		
		if(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUISuggestionMsgBox), 6)) {
			String actualSuggestionMsg = shareUI.getElementTextWd(By.cssSelector(ShareUIContants.mUISuggestionMsgBox));
			
			logger.strongStep("Verify Suggestions meassege is displayed");
			log.info("INFO: Verify Suggestions meassege is displayed");
			cnxAssert.assertEquals(actualSuggestionMsg, expectedSuggestionMsg, "Suggestion Message is visible");
		}else {
			logger.strongStep("Share suggestion msg is not visible. Check if Member list is dispalyed if network has been grown already");
			log.info("INFO: Share suggestion msg is not visible. Checking if Member list is dispalyed if network has been grown already");
			cnxAssert.assertTrue(shareUI.isElementDisplayedWd(By.cssSelector(ShareUIContants.mUIMemberList)), "Member list is visible instead of share suggestion msg");
		}
		
		logger.strongStep("Logout and close the browser");
		log.info("INFO: Logout and close the browser");
		homepageUI.logout();
		homepageUI.close(cfg);

		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b>Test case to verify that Avatar, Username and delete icon are displayed for selected Username in MUI chip component</li>
	 * <li><b>Step:</b>Login to Homepage Connections</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Type a Username in Select name field</li>
	 * <li><b>Step:</b>Select the searched user from the popup list</li>
	 * <li><b>Verify:</b>User avatar is shown in MUI Chip for selected User</li>
	 * <li><b>Verify:</b>Username is displayed in chip component for selected User</li>
	 * <li><b>Verify:</b>A remove icon is shown in chip component for selected user</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T664</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifySelectedUserInMUIChipComponent() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		homepageUI.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Type a Username in 'Select name' field");
		log.info("INFO: Type a Username in 'Select name' field");
		shareUI.typeWithDelayWd(testUser2.getDisplayName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Username from the list");
		log.info("INFO: Select Username from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", testUser2.getDisplayName())), 3, "Username Link");
		
		logger.strongStep("Verify User Avatar is displayed in Chip Component");
		log.info("INFO: Verify User Avatar is displayed in Chip Component");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUIChipAvatar),3), "User Avatar is visible");
		
		logger.strongStep("Verify Username is displayed in Chip Component");
		log.info("INFO: Verify Username is displayed in Chip Component");
		cnxAssert.assertEquals(shareUI.getElementTextWd(By.cssSelector(ShareUIContants.selectedUser)), testUser2.getDisplayName(), "Username is visible");
		
		logger.strongStep("Verify Delete icon is displayed in Chip Component");
		log.info("INFO: Verify Delete icon is displayed in Chip Component");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUIChipRemoveIcon),3), "Remove icon is visible");
		
		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();

		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b> Test case to verify that Avatar, Username and delete icon are displayed for selected Community in MUI chip component</li>
	 * <li><b>Step:</b>Create a community via API</li>
	 * <li><b>Step:</b>Login to Homepage Connections</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Type created community name in Select name field</li>
	 * <li><b>Step:</b>Select the searched community name from the popup list</li>
	 * <li><b>Verify:</b>Avatar is shown in MUI Chip for selected Community</li>
	 * <li><b>Verify:</b>Community name is visible in chip component for selected Community</li>
	 * <li><b>Verify:</b>A remove icon is shown in chip component for selected community</li>
	 * <li><b>Step:</b>Logout of the application</li>
	 * <li><b>Step:</b>Delete create community via API</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T665</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifySelectedCommunityInMUIChipComponent() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		homepageUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription).build();
		
		logger.strongStep("Create a Community using API");
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Type name of community in 'Select name' field");
		log.info("INFO: Type name of community in 'Select name' field");
		shareUI.typeWithDelayWd(community.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Community from the list");
		log.info("INFO: Select Community from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", community.getName())), 3, "Community name link");
		
		logger.strongStep("Verify Community Avatar is displayed in Chip Component");
		log.info("INFO: Verify Community Avatar is displayed in Chip Component");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUIChipAvatar),3), "Community Avatar is visible");
		
		logger.strongStep("Verify Community name is displayed in Chip Component");
		log.info("INFO: Verify Community name is displayed in Chip Component");
		cnxAssert.assertEquals(shareUI.getElementTextWd(By.cssSelector(ShareUIContants.selectedUser)), community.getName(), "Community name is visible");
		
		logger.strongStep("Verify Delete icon is displayed in Chip Component");
		log.info("INFO: Verify Delete icon is displayed in Chip Component");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUIChipRemoveIcon),3), "Remove icon is visible");
		
		logger.strongStep("Logout of the application");
		log.info("INFO:  Logout of the application");
		homepageUI.logout();
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);

		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b>Test to validate removing a selected user from selected member list in MemberPicker</li>
	 * <li><b>Step:</b>Login to Homepage</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Type a Username in Select name field</li>
	 * <li><b>Step:</b>Select that searched user from the pop-up list</li>
	 * <li><b>Step:</b>Click on remove icon of selected member</li>
	 * <li><b>Verify:</b>Username is not present in selected member list</li>
	 * <li><b>Step:</b>Enter name of same user in 'Select Name' field of memberPicker</li>
	 * <li><b>Verify:</b>The user in the pop-up list is selectable</li>
	 * <li><b>Step:</b>Logout and close the browser</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T666</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyRemovingSelectedUserInMemberPicker() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		homepageUI.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Type a Username in 'Select name' field");
		log.info("INFO: Type a Username in 'Select name' field");
		shareUI.typeWithDelayWd(testUser2.getDisplayName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Username from the list");
		log.info("INFO: Select Username from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", testUser2.getDisplayName())), 3, "Username Link");
		
		logger.strongStep("Click on remove icon to delete selected user");
		log.info("INFO: Click on remove icon to delete selected user");
		shareUI.clickLinkWaitWd(By.cssSelector(ShareUIContants.mUIChipRemoveIcon), 3, "Remove icon");
		
		logger.strongStep("Verify Selected username is not displayed in member list");
		log.info("INFO: Verify Selected username is not displayed in member list");
		cnxAssert.assertFalse(shareUI.isTextPresentWd(testUser2.getDisplayName()), "Username is not visible");
		
		logger.strongStep("Enter same username again in 'Select name' field");
		log.info("INFO: Enter same username again in 'Select name' field");
		shareUI.typeWithDelayWd(testUser2.getDisplayName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Verify Username is selectable in the pop-up list");
		log.info("INFO: Verify Username is selectable in the pop-up list");
		cnxAssert.assertTrue(shareUI.isElementDisplayedWd(By.xpath(ShareUIContants.mUIMemberAddIcon.replace("PLACEHOLDER", testUser2.getDisplayName()))), "Add icon is visible");
		
		logger.strongStep("Logout and close the browser");
		log.info("INFO: Logout and close the browser");
		homepageUI.logout();
		homepageUI.close(cfg);

		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b>Test to validate that the drop down icon and search icon is visible in memberPicker</li>
	 * <li><b>Step:</b>Login to Homepage</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Verify:</b>Verify Drop down icon is visible at the end of 'Select name' input field</li>
	 * <li><b>Step:</b>Enter a Username in 'Select Name' input field</li>
	 * <li><b>Verify:</b>Verify a search icon is displayed at the end of 'Select name' input field</li>
	 * <li><b>Step:</b>Logout and close the browser</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T667</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2", "cnx8ui-level2"})
	public void verifySearchAndDropDownIconInMemberPicker() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		homepageUI.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Verify a dropdown icon is visible at the end of 'Select Name' input field");
		log.info("INFO: Verify a dropdown icon is visible at the end of 'Select Name' input field");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUIDropDownIcon), 3), "Drop down icon is visible");
		
		logger.strongStep("Type a Username in 'Select name' field");
		log.info("INFO: Type a Username in 'Select name' field");
		shareUI.typeWithDelayWd(testUser2.getDisplayName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Verify a Search icon is visible at the end of 'Select Name' input field");
		log.info("INFO: Verify a Search icon is visible at the end of 'Select Name' input field");
		cnxAssert.assertTrue(shareUI.isElementVisibleWd(By.cssSelector(ShareUIContants.mUISearchIcon), 3), "Search icon is visible");
		
		logger.strongStep("Logout and close the browser");
		log.info("INFO: Logout and close the browser");
		homepageUI.logout();
		homepageUI.close(cfg);
		
		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b> Test to validate that User and Community both are selected in MUI chip component</li>
	 * <li><b>Step:</b>Create a community via API</li>
	 * <li><b>Step:</b>Login to Homepage</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Type Username in 'Select name' field</li>
	 * <li><b>Step:</b>Select the searched username from the pop-up list</li>
	 * <li><b>Step:</b>Type created community name in 'Select name' field</li>
	 * <li><b>Step:</b>Select the searched community name from the popup list</li>
	 * <li><b>Verify:</b>Selected User is visible in the list</li>
	 * <li><b>Verify:</b>Selected Community is displayed in the list</li>
	 * <li><b>Step:</b>Logout and close browser</li>
	 * <li><b>Step:</b>Delete create community via API</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T669</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyMemberAndCommunitySelectedInMUIChipComponent() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		homepageUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription).build();
		
		logger.strongStep("Create a Community using API");
		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Type a Username in 'Select name' field");
		log.info("INFO: Type a Username in 'Select name' field");
		shareUI.typeWithDelayWd(testUser2.getDisplayName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Username from the list");
		log.info("INFO: Select Username from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", testUser2.getDisplayName())), 3, "Username Link");
		
		logger.strongStep("Type name of community in 'Select name' field");
		log.info("INFO: Type name of community in 'Select name' field");
		shareUI.typeWithDelayWd(community.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Community from the list");
		log.info("INFO: Select Community from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", community.getName())), 3, "Community name link");
		
		logger.strongStep("Verify Selected username is displayed in member list");
		log.info("INFO: Verify Selected username is displayed in member list");
		cnxAssert.assertTrue(shareUI.isTextPresentWd(testUser2.getDisplayName()), "Username is visible");
		
		logger.strongStep("Verify Selected Community is displayed in member list");
		log.info("INFO: Verify Selected Community is displayed in member list");
		cnxAssert.assertTrue(shareUI.isTextPresentWd(community.getName()), "Community is visible");
		
		logger.strongStep("Logout and close the browser");
		log.info("INFO:  Logout and close the browser");
		homepageUI.logout();
		homepageUI.close(cfg);
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(comAPI);

		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b>Test to validate a error message when no search result is found in MemberPicker</li>
	 * <li><b>Step:</b>Login to Homepage</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Enter any user's email address</li>
	 * <li><b>Verify:</b>Error message when no search result is found</li>
	 * <li><b>Step:</b>Logout and close the browser</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T671</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyMessageWhenNoResultFound() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String expectedMsg = "User or Community not found";
		
		homepageUI.startTest();
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Type a Username in 'Select name' field");
		log.info("INFO: Type a Username in 'Select name' field");
		shareUI.typeWithDelayWd(testUser2.getEmail(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		String actualMsg = shareUI.getElementTextWd(By.cssSelector(ShareUIContants.mUISuggestionMsgBox));
		
		logger.strongStep("Verify correct error meassege is displayed");
		log.info("INFO: Verify correct error meassege is displayed");
		cnxAssert.assertEquals(actualMsg, expectedMsg, "Error Message is visible");
		
		logger.strongStep("Logout and close the browser");
		log.info("INFO: Logout and close the browser");
		homepageUI.logout();
		homepageUI.close(cfg);

		homepageUI.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b>Test to verify a community selected once will become non-selectable in pop-up list
	 * 	 in MUI chip component</li>
	 * <li><b>Step:</b>Create multiple communities via API</li>
	 * <li><b>Step:</b>Login to Homepage</li>
	 * <li><b>Step:</b>Toggle to the new UI</li>
	 * <li><b>Step:</b>Click on Share Button to display Share Dialog</li>
	 * <li><b>Step:</b>Type 1st community name in 'Select name' field</li>
	 * <li><b>Step:</b>Select the searched community name from the pop-up list</li>
	 * <li><b>Step:</b>Type 2nd community name in 'Select name' field</li>
	 * <li><b>Step:</b>Select the searched community name from the pop-up list</li>
	 * <li><b>Step:</b>Type again initial name of a community</li>
	 * <li><b>Verify:</b>Verify both selected community become non-selectable</li>
	 * <li><b>Step:</b>Logout and close browser</li>
	 * <li><b>Step:</b>Delete created communities via API</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T668</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyCommunityToBeSelectedOnce() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String commName = "MyCommunity";
		
		homepageUI.startTest();
		
		BaseCommunity community1 = new BaseCommunity.Builder(commName + "1" + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription).build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(commName + "2" + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description(Data.getData().commonDescription).build();
		
		logger.strongStep("Create 2 Communities using API");
		log.info("INFO: Create 2 Communities using API");
		Community comAPI1 = community1.createAPI(apiOwner);
		Community comAPI2 = community2.createAPI(apiOwner);
		
		logger.strongStep("Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load Homepage, login as " + testUser.getEmail() + " and Load new UI as " + cfg.getUseNewUI());
		homepageUI.loadComponent(Data.getData().HomepageImFollowing);
		homepageUI.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Share Button to display Share Dialogue");
		log.info("INFO: Click on Share Button to display Share Dialogue");
		shareUI.openShareInConnectionsDialog();
		
		logger.strongStep("Type 1st community name in 'Select name' field");
		log.info("INFO: Type 1st community name in 'Select name' field");
		shareUI.typeWithDelayWd(community1.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Community from the list");
		log.info("INFO: Select Community from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", community1.getName())), 3, "Community name link");
		
		shareUI.isTextPresentWd(community1.getName());
		
		logger.strongStep("Type 2nd community name in 'Select name' field");
		log.info("INFO: Type 2nd community name in 'Select name' field");
		shareUI.typeWithDelayWd(community2.getName(), By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Select Community from the list");
		log.info("INFO: Select Community from the list");
		shareUI.clickLinkWaitWd(By.xpath(ShareUIContants.resultListPopup.replace("PLACEHOLDER", community2.getName())), 3, "Community name link");
			
		shareUI.isTextPresentWd(community2.getName());
		
		logger.strongStep("Type initial name of community in 'Select name' field");
		log.info("INFO: Type initial name of community in 'Select name' field");
		shareUI.typeWithDelayWd(commName, By.cssSelector(ShareUIContants.searchTypeAhead));
		
		logger.strongStep("Verify both selected Communities are non-selectable in pup-up list");
		log.info("INFO: Verify both selected Communities are non-selectable in pup-up list");
		cnxAssert.assertTrue(shareUI.isElementDisplayedWd(By.xpath(ShareUIContants.mUIMemberCheckMarkIcon.replace("PLACEHOLDER", community1.getName()))), "CheckMark icon is visible");
		cnxAssert.assertTrue(shareUI.isElementDisplayedWd(By.xpath(ShareUIContants.mUIMemberCheckMarkIcon.replace("PLACEHOLDER", community2.getName()))), "CheckMark icon is visible");
		
		logger.strongStep("Logout and close the browser");
		log.info("INFO:  Logout and close the browser");
		homepageUI.logout();
		homepageUI.close(cfg);
		
		logger.strongStep("Delete both Communities via API");
		log.info("INFO: Delete both Communities via API");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);

		homepageUI.endTest();
		
	}

}
