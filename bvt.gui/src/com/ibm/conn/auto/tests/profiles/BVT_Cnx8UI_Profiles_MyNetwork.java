package com.ibm.conn.auto.tests.profiles;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.conn.auto.webui.constants.ItmNavUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.menu.MyContacts_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Profile_Widget_Action_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.cnx8.ProfilesUICnx8;

public class BVT_Cnx8UI_Profiles_MyNetwork  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Profiles_MyNetwork.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser,testUser1,testUser2,testUser3,testUserAddedToITM,adminUser,adminUser2;
	private ProfilesUICnx8 profilesUICnx8;
	private ProfilesUI ui;
	private String serverURL; 
	private ItmNavCnx8 itmNavCnx8;
	private SearchAdminService adminService;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2,profilesAPIUser3;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		adminUser=cfg.getUserAllocator().getAdminUser();
		adminUser2=cfg.getUserAllocator().getModUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getEmail(), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getEmail(), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getEmail(), testUser3.getPassword());
		adminService = new SearchAdminService();

		try {
			String components = "evidence,graph,manageremployees,tags,taggedby,communitymembership";
			adminService.sandIndexNow(components, adminUser.getAttribute(cfg.getLoginPreference()),adminUser.getPassword());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	@BeforeMethod(alwaysRun = true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
		profilesUICnx8 = new ProfilesUICnx8(driver);
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		itmNavCnx8 = new ItmNavCnx8(driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify user lands successfully on Invitation, Following and Followers when respective menu selected from left panel on My Network page</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Go to My Network tab</li>
	 * <li><B>Step:</B> Click and verify 'All People' page is displayed</li>
	 * <li><B>Step:</B> Select 'Invitations' from left panel</li>
	 * <li><B>Verify:</B> Verify 'Invitations' page is displayed</li>
	 * <li><B>Step:</B> Select 'Following' from left panel</li>
	 * <li><B>Verify:</B> Verify 'Following' page is displayed</li>
	 * <li><B>Step:</B> Select 'Followers' from left panel</li>
	 * <li><B>Verify:</B> Verify 'Followers' page is displayed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128964</li>
	 * <li><B>Step 4 of this link (All people link validation):</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T636</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyMyNetworkPage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("INFO: Select 'People' from left nav panel");
		logger.strongStep("Select 'People' from left nav panel");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.LeftNavInvitations), 8);
		
		log.info("INFO: Click and verify 'All People' page is displayed ");
		logger.strongStep("INFO: Click and verify 'All People' page is displayed");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.allPeopleLink), 5, "Click on 'All Poeple' link");
		cnxAssert.assertEquals(profilesUICnx8.getElementTextWd(By.xpath(ProfilesUIConstants.allPeoplePageSubTitle)), "No network contacts are associated with this profile","'All People' page sub heading is displayed ");

		log.info("INFO: Select 'Invitations' from left panel");
		logger.strongStep("Select 'Invitations' from left panel");
		MyContacts_LeftNav_Menu.INVITATIONS.open(ui);

		log.info("INFO: Verify 'Invitations' page is displayed");
		logger.strongStep("Verify 'Invitations' page is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.cssSelector(ProfilesUIConstants.newInvitaionText), 5),"Invitaion page is displayed ");

		log.info("INFO: Select 'Following' from left panel");
		logger.strongStep("Select 'Following' from left panel");
		MyContacts_LeftNav_Menu.FOLLOWING.open(ui);

		log.info("Verify 'Following' page is displayed");
		logger.strongStep("Verify 'Following' page is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.xpath(ProfilesUIConstants.followingPageSubTitle), 5),"Following page is displayed");

		log.info("INFO: Select 'Followers' from left panel");
		logger.strongStep("Select 'Followers' from left panel");
		MyContacts_LeftNav_Menu.FOLLOWERS.open(ui);

		log.info("INFO: Verify 'Follows' page is displayed");
		logger.strongStep("Verify 'Follows' page is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.xpath(ProfilesUIConstants.followersPageSubTitle), 5),"Followers page is displayed");
		profilesUICnx8.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the count of invitations, following, followers displaying on the left panel</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left nav panel</li>
	 * <li><B>Verify:</B> Verify 'My Network' page should be displayed</li>
	 * <li><B>Verify:</B> Verify count of Invitations should be displayed in left panel</li>
	 * <li><B>Verify:</B> Verify count of Following should be displayed in left panel</li>
	 * <li><B>Verify:</B> Verify count of Followers should be displayed in left panel</li>
	 * <li><B>Verify:</B> Verify font size of 'Remove' and 'Invite to Connect' buttons are same</li>
	 * <li><B>Step:</B> Clicking on 'Invite to Connect' button</li>
	 * <li><B>Verify:</B> Verify 'Invite To My Network' pop up is displayed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128833</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128964</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2","cnx8ui-level2" })
	public void verifyCountOnMyNetworkPage() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		logger.strongStep("INFO: Load Profiles and login: " +adminUser2.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentHomepage);
		profilesUICnx8.loginAndToggleUI(adminUser2,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("INFO: Select 'People' from left nav panel");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		
		logger.strongStep("INFO: Verify 'My Network' page should be displayed");
		String expPageTitle = "My Network - Profiles";
		profilesUICnx8.waitForTitleIsPresentWd(expPageTitle, 5);
		String actPageTitle = driver.getTitle();
		cnxAssert.assertTrue(actPageTitle.equals(expPageTitle), "User is landing on 'My Network' page succesfully");
		
		ui.waitForPageLoaded(driver);
		logger.strongStep("INFO: Verify count of Invitations should be displayed in left panel");
		WebElement invitaionEle = ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavInvitations));
		verifyCountWithRegex("[0-9]+", invitaionEle);
		
		logger.strongStep("INFO: Verify count of Following should be displayed in left panel");
		WebElement followingEle = ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavFollowing));
		verifyCountWithRegex("[0-9]+", followingEle);

		logger.strongStep("INFO: Verify count of Followers should be displayed in left panel");
		WebElement followersEle = ui.findElement(By.cssSelector(ProfilesUIConstants.Followers));
		verifyCountWithRegex("[0-9]+", followersEle);
		
		logger.strongStep("INFO: Verify font size of 'Remove' and 'Invite to Connect' buttons are same");
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.removeBtn), 5);
		String removeBtnFontSize = ui.findElement(By.cssSelector(ProfilesUIConstants.removeBtn)).getCssValue("font-size");
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 3);
		String inviteToConnectBtnFontSize = ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect)).getCssValue("font-size");
		cnxAssert.assertTrue(removeBtnFontSize.equals(inviteToConnectBtnFontSize), "Font size of button 'Remove' and 'Invite To Connect' is same");

		log.info("INFO: Clicking on 'Invite to Connect' button");
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 4);
		profilesUICnx8.scrollToElementWithJavaScriptWd(ui.findElement(By.xpath(ProfilesUIConstants.doyouknowActions)));
		profilesUICnx8.clickLinkWaitWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 3,"Click Invite to connect button");
		
		log.info("INFO: Verify 'Invite To My Network' pop up is displayed");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(By.xpath(ProfilesUIConstants.InviteToMyNetworkDailogueBox), 4),"Invite to Network dialogue box is displayed");
		profilesUICnx8.endTest();
	}
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the count of invitations displayed in left panel is decreased by 1 after accepting the invite</li>
	 * <li><B>Step:</B> [API] User1 sends invite to User2</li>
	 * <li><B>Step:</B> User2 login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Select 'Invitations' from left panel</li>
	 * <li><B>Step:</B> Accept the Invitation from User1</li>
	 * <li><B>Step:</B> Refresh the page</li>
	 * <li><B>Verify:</B> Verify the invitations count should be decreased by one on the left panel.</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128833</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyInvitationCount() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		// User 1 will now invite User 2 to join their network
		log.info("INFO: "+testUser1.getDisplayName()+" sends invite to "+testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName()+" sends invite to "+testUser2.getDisplayName());
		ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);

		log.info("INFO: Load Profiles and login: " + testUser2.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser2.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser2, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.LeftNavInvitations), 8);
		
		// Retrieve Invitations count before accepting invite
		int invitationCountBeforeAccept = retrieveCount(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavInvitations)));
		log.info("INFO: Invitations count before accepting invite is "+invitationCountBeforeAccept);
        
		log.info("INFO: Select 'Invitations' from left panel");
		logger.strongStep("Select 'Invitations' from left panel");
		MyContacts_LeftNav_Menu.INVITATIONS.open(ui);
		
		log.info("Accept the Invitation from "+testUser1.getDisplayName());
		logger.strongStep("Accept the Invitation from "+testUser1.getDisplayName());
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUI.getAcceptLink(testUser1)), 7, "Click Acceptlink");
		
		log.info("Refresh the page");
		logger.strongStep("Refresh the page");
		driver.navigate().refresh();
		ui.waitForPageLoaded(driver);
		
		// Retrieve Invitations count after accepting invite
		int invitationCountAfterAccept = retrieveCount(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavInvitations)));
		log.info("INFO: Invitations count after accepting invite is "+invitationCountAfterAccept);
		
		log.info("INFO: Verify the invitations count should be decrease by one on the left panel");
		logger.strongStep("Verify the invitations count should be decrease by one on the left panel");
		cnxAssert.assertEquals(invitationCountBeforeAccept-invitationCountAfterAccept, 1, "Invitation count decrease by 1 after accept");
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the count of Following displayed in left panel is increased by 1 after clicking 'Invite to Connect' and decrease by 1 after clicking 'Stop following'</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Click 'Invite to Connect' button from 'People You may know' section</li>
	 * <li><B>Step:</B> Click 'Send Invitation' on Invite to my network dialogue box</li>
	 * <li><B>Step:</B> Refresh the page</li>
	 * <li><B>Verify:</B> Verify the following count should be increased by 1 in the left panel.</li>
	 * <li><B>Step:</B> Select 'Following' from left panel</li>
	 * <li><B>Step:</B> Select check-box displayed against user invited in above step</li>
	 * <li><B>Step:</B> Select 'Stop Following' button</li>
	 * <li><B>Step:</B> Verify the following count should be decrease by one on the left panel</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128833</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyFollowingCount() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: Load Profiles and login: " + adminUser2.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + adminUser2.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(adminUser2, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.LeftNavFollowing), 8);
		
		// Retrieve following count before accepting invite
		int followingCountBeforeSendingInvite= retrieveCount(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavFollowing)));
		log.info("INFO: Following count before sending invite is "+followingCountBeforeSendingInvite);

		// Get the user from people you may know section to whom invite is being sent
		log.info("INFO: Get the user from people you may know section to whom invite is being sent");
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.PYMKUser), 4);
		String DYK_User = ui.findElement(By.cssSelector(ProfilesUIConstants.PYMKUser)).getText();
	
		log.info("INFO: Click 'Invite to Connect' button from 'People You may know' section");
		logger.strongStep(" Clicking on 'Invite to Connect' button from 'People You may know' section");
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 4);
		profilesUICnx8.scrollToElementWithJavaScriptWd(ui.findElement(By.xpath(ProfilesUIConstants.doyouknowActions)));
		profilesUICnx8.clickLinkWaitWd(ui.createByFromSizzle(ProfilesUIConstants.Invitetoconnect), 3,"Click Invite to connect button");

		log.info("INFO: Click 'Send Invitaion' on Invite to my network dialogue box");
		logger.strongStep("Clicking 'Send Invitaion' on Invite to my network dialogue box");		
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.InviteToMyNetworkDailogueBox), 4);
		profilesUICnx8.clickLinkWithJavaScriptWd(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.SendInvite)));
		profilesUICnx8.waitForElementInvisibleWd(ui.createByFromSizzle(ProfilesUIConstants.SendInvite), 4);
	
		log.info("Refresh the page");
		logger.strongStep("Refresh the page");
		driver.navigate().refresh();
		
		// Retrieve Following count after accepting invite
		int followingCountAfterSendingInvite= retrieveCount(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavFollowing)));
		log.info("INFO: Following count after sending invite is "+followingCountAfterSendingInvite);
		
		log.info("INFO: Verify the Following count should be increase by one on the left panel");
		logger.strongStep("Verify the Following count should be increase by one on the left panel");
		cnxAssert.assertEquals(followingCountAfterSendingInvite-followingCountBeforeSendingInvite, 1, "Invitation count decrease by 1 after accept");
		
		log.info("INFO: Select 'Following' from left panel");
		logger.strongStep("Select 'Following' from left panel");
		MyContacts_LeftNav_Menu.FOLLOWING.open(ui);
		
		log.info("INFO: Select unfollow link for "+DYK_User);
		logger.strongStep("Select unfollow link for "+DYK_User);
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUI.getUserUnfollowLink(DYK_User)), 7);
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUI.getUserUnfollowLink(DYK_User)), 4, "Select unfollow link for "+DYK_User);
		profilesUICnx8.waitForElementInvisibleWd(By.cssSelector(ProfilesUI.getUserUnfollowLink(DYK_User)), 4);
				
		// Retrieve Following count after stop following the user
		int followingCountAfterStopFollowing= retrieveCount(ui.findElement(ui.createByFromSizzle(ProfilesUIConstants.LeftNavFollowing)));
		log.info("INFO: Following count after stop following is "+followingCountAfterStopFollowing);
		
		log.info("INFO: Verify the following count should be decrease by one on the left panel");
		logger.strongStep("Verify the following count should be decrease by one on the left panel");
		cnxAssert.assertEquals(followingCountAfterSendingInvite-followingCountAfterStopFollowing, 1, "Following count decrease by 1 after stop following the user");
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the count of Followers displayed for User2 in left panel is increased by 1 when User1 follow him</li>
	 * <li><B>Step:</B> User2 login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> [API] User1 follow User2</li>
	 * <li><B>Step:</B> Refresh the page</li>
	 * <li><B>Verify:</B> Verify the followers count should be increased by 1 in the left panel.</li>
	 * <li><B>Step:</B> Click on 'Followers' Link from left navigation pane</li>
	 * <li><B>Verify:</B> Verify the pagination count is displayed '1'</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128833</li>
	 * <li><B>Defect Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14226</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyFollowersCount() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		log.info("INFO: Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser2, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		profilesUICnx8.waitForPageLoaded(driver);
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		profilesUICnx8.waitForElementVisibleWd(By.cssSelector(ProfilesUIConstants.LeftNavFollowers), 8);
		
		// Retrieve followers count before User1 follow User2
		int followersCountBeforeFollowingBySomeUser= retrieveCount(ui.findElement(By.cssSelector(ProfilesUIConstants.LeftNavFollowers)));
		log.info("INFO: Followers count before following is "+followersCountBeforeFollowingBySomeUser);
		
		// User 1 will now follow User 2
		log.info("INFO: "+testUser1.getDisplayName()+" follow "+testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName()+" follow "+testUser2.getDisplayName());
		ProfileEvents.followUser(profilesAPIUser2, profilesAPIUser1);
		
		log.info("Refresh the page");
		logger.strongStep("Refresh the page");
		driver.navigate().refresh();
		
		// Retrieve followers count after User1 follow User2
		int followersCountAfterFollowingBySomeUser= retrieveCount(ui.findElement(By.cssSelector(ProfilesUIConstants.LeftNavFollowers)));
		log.info("INFO: Followers count before following any user is "+followersCountAfterFollowingBySomeUser);
		
		log.info("INFO: Verify the followrs count should be increased by  left 1 in left panel");
		logger.strongStep("Verify the following count should be decrease by one on the left panel");
		cnxAssert.assertEquals(followersCountAfterFollowingBySomeUser-followersCountBeforeFollowingBySomeUser, 1, "Following count decrease by 1 after stop following the user");
		
		log.info("INFO: Click on 'Followers' Link from left navigation pane");
		logger.strongStep("Click on 'Followers' Link from left navigation pane");
		profilesUICnx8.clickLinkWd(profilesUICnx8.findElement(By.cssSelector(ProfilesUIConstants.LeftNavFollowers)), "Click on 'Followers' Link from left navigation pane");
		profilesUICnx8.waitForPageLoaded(driver);
		
		log.info("INFO: Verify the pagination count is displayed '1'");
		logger.strongStep("Verify the pagination count is displayed '1'");
		int actualCount = retrieveCount(ui.findElement(By.cssSelector(ProfilesUIConstants.LeftNavFollowers)));
		cnxAssert.assertEquals(actualCount, 1, "Pagination count is dislayed as '1'");
		
		profilesUICnx8.endTest();		
	}

	/**
	 * Verify that element text contains regex
	 * @param regex
	 * @param element
	 */
	private void verifyCountWithRegex(String regex, WebElement element) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		Pattern pattern = Pattern.compile(regex);
		String eleText = element.getText();
		String textToBeMatch = eleText.substring(eleText.indexOf("(") + 1, eleText.indexOf(")"));

		logger.strongStep("INFO: Verify " + eleText + "contains " + regex);
		cnxAssert.assertTrue(pattern.matcher(textToBeMatch).matches(), "String matches with given regular expression");

	}
	
	/**
	 * Retrieve count from text in webelement
	 * @param webelement
	 */
	private int retrieveCount(WebElement webelement) {
		int count;
		String webelementLabel = webelement.getText();
		String webelementLabelWithCount = webelementLabel.substring(webelementLabel.indexOf("(") + 1,webelementLabel.indexOf(")"));
		count = Integer.parseInt(webelementLabelWithCount);
		return count;
	}	
	
	/**
	 * <ul>
	 * <li><B>Defect - Info:</B> Verify user is able to navigate to the person's profile when clicking on filter icon associated with person in ITM </li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Search for userB</li>
	 * <li><B>Step:</B> Go to another userB's profiles</li>
	 * <li><B>Step:</B> Add another person from ITM</li>
	 * <li><B>Step:</B> Click on 'Filter' icon</li>
	 * <li><B>Verify:</B> Verify that user is able to navigate to the user's profile added in ITM in step 4</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14053</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyClickOnPersonFilterInITMFromProfilesPage()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);

		log.info("Search for " + testUser2.getDisplayName());
		logger.strongStep("Search for the user");
		ui.searchForUser(testUser2);

		log.info("Click on " + testUser2.getDisplayName());
		logger.strongStep("Click on user");
		profilesUICnx8.mouseHoverAndClickWd(ui.findElement(By.xpath(GlobalSearchUIConstants.searchResultForPeople.replace("PLACEHOLDER1", testUser2.getFirstName()).replace("PLACEHOLDER2", testUser2.getLastName()))));
		profilesUICnx8.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.ProfilePhoto), 7);

		// Data setup
		log.info("Add " + testUserAddedToITM.getDisplayName());
		logger.strongStep("Add user in ITM");
		WebElement user = itmNavCnx8.getItemInImportantToMeList(testUserAddedToITM.getDisplayName(), false);
		if (user == null) {
			log.info("Add " + testUserAddedToITM.getDisplayName() + " to the Important to Me list.");
			itmNavCnx8.addImportantItem(testUserAddedToITM.getDisplayName(), true);
		} else {
			log.info(testUserAddedToITM.getDisplayName() + " is already in the Important to Me list.");
		}

		log.info("Click on filter icon associated with " + testUserAddedToITM.getDisplayName());
		logger.strongStep("Click on filter icon associated with user in ITM");
		WebElement item = itmNavCnx8.getItemInImportantToMeList(testUserAddedToITM.getDisplayName(), false);
		itmNavCnx8.scrollToElementWithJavaScriptWd(item);
		profilesUICnx8.mouseHoverWd(item);
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ItmNavCnx8.getFilterIcon(testUserAddedToITM.getDisplayName())), 4, "");
		profilesUICnx8.waitForTitleIsPresentWd(testUserAddedToITM.getDisplayName() + " Profile", 4);
				
		log.info("Verify that user is able to navigate to " + testUserAddedToITM.getDisplayName()+ " profile added in ITM ");
		logger.strongStep("Verify that user is able to navigate to selected user's profiles");
		cnxAssert.assertTrue(driver.getTitle().equals(testUserAddedToITM.getDisplayName() + " Profile"),"User is able to navigate to " + testUserAddedToITM.getDisplayName() + " profiles");
		
		// Removing entry from ITM
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ItmNavUIConstants.removeImportantItem), 6, "Click");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(CommonUICnx8.getRemoveIcon(testUserAddedToITM.getDisplayName())), 7);
		profilesUICnx8.clickLinkWaitWd(By.xpath(CommonUICnx8.getRemoveIcon(testUserAddedToITM.getDisplayName())), 7);
		profilesUICnx8.waitForElementInvisibleWd(By.xpath(CommonUICnx8.getRemoveIcon(testUserAddedToITM.getDisplayName())), 7);
		profilesUICnx8.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Defect - Info:</B> Verify that label 'People you may know' remains intact after refreshing widget</li>
	 * <li><B>Step:</B> Login to Profiles Page and Toggle to the new UI</li>
	 * <li><B>Step:</B> Select 'People' from left nav panel</li>
	 * <li><B>Step:</B> Click on action icon of 'People you may know' widget displayed in left pane</li>
	 * <li><B>Step:</B> Click on 'Refresh'</li>
	 * <li><B>Verify:</B> Verify that label text 'People you may know' remains intact</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-14056</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void refreshInPeopleYouMayKnow() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();

		log.info("INFO: Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);

		log.info("INFO: Select 'People' from left nav panel");
		logger.strongStep("Select3 'People' from left nav panel");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);

		log.info("INFO: Click on action icon of 'People you may know' widget displayed in left pane");
		logger.strongStep(" Click on action icon of 'People you may know' widget displayed in left pane");
		profilesUICnx8.scrollToElementWithJavaScriptWd(By.id(ProfilesUIConstants.ActionsforPeopleoyouknow));
		profilesUICnx8.clickLinkWaitWd(By.id(ProfilesUIConstants.ActionsforPeopleoyouknow), 4, "Click");

		log.info("INFO: Click on 'Refresh'");
		logger.strongStep("Click on 'Refresh'");
		Profile_Widget_Action_Menu.REFRESH.actionsforDoyouknow(ui);
		Profile_Widget_Action_Menu.REFRESH.select(ui);

		log.info("INFO: Verify that label text 'People you may know' remains intact");
		logger.strongStep("Verify that label text 'People you may know' remains intact");
		profilesUICnx8.waitForElementInvisibleWd(By.cssSelector(ProfilesUIConstants.peopleoyouknowLodingText), 5);
		cnxAssert.assertTrue(ui.findElement(By.id("sand_DYKId")).getText().equals("People you may know"),"Label text 'People you may know' is intact");
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify remove person from my network</li>
	 * <li><B>Step:</B> [API] User 1 will now invite User 2 to join their network</li>
	 * <li><B>Step:</B> [API] User 2 will now accept invite from User 1 to join their network</li>
	 * <li><B>Step:</B> Load Profiles and login with User 1</li>
	 * <li><B>Step:</B>  Select 'People' from left side navigation</li>
	 * <li><B>Step:</B>  Select checkbox for user to be removed</li>
	 * <li><B>Step:</B>  Click 'Remove from My Network' button</li>
	 * <li><B>Verify:</B> Verify message 'Network contact(s) removed.' is displayed</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128964</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyRemovePersonFromMyNetworkPage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);

		log.info("INFO: " + testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		Invitation user2NetworkInvite = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);

		log.info("INFO: "+testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		logger.strongStep(testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		ProfileEvents.acceptInvitationToJoinANetwork(user2NetworkInvite, profilesAPIUser2, profilesAPIUser1);
		
		log.info("INFO: Load Profiles and login: " + testUser1.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser1.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		
		log.info("INFO: Select checkbox against :"+testUser2.getDisplayName());
		logger.strongStep("Select checkbox for user to be removed");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.removFromMyNetworkBtn), 5);
		WebElement ele = ui.findElement(profilesUICnx8.createByFromSizzle(ProfilesUI.getFollowingUserCheckbox(testUser2.getDisplayName())));
		profilesUICnx8.clickLinkWithJavaScriptWd(ele);
		
		log.info("INFO: Click 'Remove from My Network' button");
		logger.strongStep("Click 'Remove from My Network' button");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.removFromMyNetworkBtn), 4, "Click");
		
		log.info("INFO: Verify message 'Network contact(s) removed.' is displayed");
		logger.strongStep("Verify message 'Network contact(s) removed.' is displayed");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.removedFromNetworkMsg), 4);
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUIConstants.removedFromNetworkMsg)), "Verify 'Network contact(s) removed.' ");
		
		profilesUICnx8.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the changes on My Network page for People</li>
	 * <li><B>Step:</B> [API] User 1 will now invite User 2 and User 3 to join their network</li>
	 * <li><B>Step:</B> [API] User 2 and User3 will now accept invite from User 1 to join their network</li>
	 * <li><B>Step:</B> Load Profiles and login with User 1</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Click on user name displayed in My Network list</li>
	 * <li><B>Step:</B> Click on 'View All' link</li>
	 * <li><B>Step:</B> Verify that users in my network contact should be displayed</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Click on user 2's name displayed in My Network list</li>
	 * <li><B>Verify:</B> Verify that it should displays the user information page of that user</li>
	 * <li><B>Step:</B> Click on chat icon </li>
	 * <li><B>Verify:</B> It should redirect to chatting application like MS teams </li>
	 * <li><B>Step:</B> Close current window </li>
	 * <li><B>Step:</B> Click on three dots icon(more action) </li>
	 * <li><B>Verify:</B> It should display more action popup window </li>
	 * <li><B>Verify:</B> Popup window should displays 'Remove From Network' ,'Stop Following', 'Share a File' , 'Download vCard' options on it</li>
	 * <li><B>Step:</B> Add a tag </li>
	 * <li><B>Step:</B> click on added Tag </li>
	 * <li><B>Verify:</B> It should redirect to Directory page which display the search result related to that tag.</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128975</li>
	 * </ul>
	 */
	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyAnotherUserProfile() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String tag = Helper.genDateBasedRand();
		profilesUICnx8.startTest();
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser3);

		log.info("INFO: " + testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		Invitation user2NetworkInvite = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);

		log.info("INFO: "+testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		logger.strongStep(testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		ProfileEvents.acceptInvitationToJoinANetwork(user2NetworkInvite, profilesAPIUser2, profilesAPIUser1);
		
		log.info("INFO: " + testUser1.getDisplayName() + " follow " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " follow " + testUser2.getDisplayName());
		ProfileEvents.followUser(profilesAPIUser2, profilesAPIUser1);
		 
		log.info("INFO: "+testUser2.getDisplayName()+" add "+testUser3.getDisplayName()+" to it's network");
		logger.strongStep(testUser2.getDisplayName()+" add "+testUser3.getDisplayName()+" to it's network");
		Invitation user3NetworkInvite =ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser3);
		ProfileEvents.acceptInvitationToJoinANetwork(user3NetworkInvite, profilesAPIUser3, profilesAPIUser1);

		log.info("INFO: Load Profiles and login: " + testUser1.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser1.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		
		log.info("INFO: Click on "+testUser2.getDisplayName()+" displayed in My Network list");
		logger.strongStep("Click on "+testUser2.getDisplayName()+"  displayed in My Network list");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserLinkFromAllPeople(testUser2.getDisplayName())), 4);
		profilesUICnx8.mouseHoverAndClickWd(ui.findElement(By.xpath(ProfilesUICnx8.getUserLinkFromAllPeople(testUser2.getDisplayName()))));
		
		log.info("INFO: Click on 'View All' link");
		logger.strongStep(" Click on 'View All' link");
		profilesUICnx8.waitForElementVisibleWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.networkViewAllLink), 4);
		profilesUICnx8.clickLinkWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.networkViewAllLink)," Click on 'View All' link");
	
		log.info("INFO: Verify that user in my network contact should be displayed");
		logger.strongStep("Verify that user in my network contact should be displayed");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserFromMyNetworkUserList(testUser1.getDisplayName())), 4);
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUICnx8.getUserFromMyNetworkUserList(testUser1.getDisplayName()))),"User not displayed in my network list");
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		
		log.info("INFO: Click on "+testUser2.getDisplayName()+" displayed in My Network list");
		logger.strongStep("Click on "+testUser2.getDisplayName()+"  displayed in My Network list");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserLinkFromAllPeople(testUser2.getDisplayName())), 4);
		profilesUICnx8.mouseHoverAndClickWd(ui.findElement(By.xpath(ProfilesUICnx8.getUserLinkFromAllPeople(testUser2.getDisplayName()))));
		
		log.info("INFO: Verify that it should displays the user information page of that user");
		logger.strongStep("Verify that it should displays the user information page of that user");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4);
		cnxAssert.assertTrue(driver.getTitle().equals(testUser2.getDisplayName()+" Profile"), "It should displays the user information page of "+testUser2.getDisplayName());
		
		log.info("INFO: Click on chat icon ");
		logger.strongStep("Click on chat icon ");
		profilesUICnx8.clickLinkWaitWd(By.cssSelector(ProfilesUIConstants.chatIconOnProfile), 4, "Click");
		String parentWindowID = ((WebDriver) driver.getBackingObject()).getWindowHandle();
		profilesUICnx8.switchToNextWindowWd("teams");
		
		String currentUrl = driver.getCurrentUrl();
		String msLoginUrl = "microsoft";
		logger.strongStep("Verify Redirected to MS Teams URL");
		log.info("INFO:Verify Redirected to MS Teams URL");
		cnxAssert.assertTrue(currentUrl.contains(msLoginUrl), "Redirected to MS Teams URL");

		logger.strongStep("Close Current Window ");
		log.info("INFO:Close Current Window");
		profilesUICnx8.closeCurrentWindowAndMoveToParentWindowWd(parentWindowID);
		
		log.info("INFO: Click on three dots icon(more action)");
		logger.strongStep("Click on three dots icon(more action)");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.threeDotsActionIcon), 4, "Click ");
		
		log.info("INFO: It should display more action popup window.");
		logger.strongStep("It should display more action popup window.");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUIConstants.anotherUserProfActionMenuTable)), "It should display more action popup window");

		List<String> expActionMenu = new ArrayList<>(Arrays.asList("Remove From Network ","Stop Following ", "Share a File ", "Download vCard "));
		List<String> actualActionMenu = new ArrayList<>();

		List<WebElement> actionMenus = ui.findElements(By.xpath(ProfilesUIConstants.anotherUserProfActionMenu));
		for (WebElement menu : actionMenus) {
			String menuLabel = menu.getAttribute("aria-label");
			actualActionMenu.add(menuLabel);
		}

		log.info("INFO: Popup window should display 'Remove From Network', 'Share a File' , 'Download vCard' options on it");
		logger.strongStep("Popup window should display 'Remove From Network' ,'Share a File' , 'Download vCard' options on it");
		cnxAssert.assertTrue(actualActionMenu.equals(expActionMenu), "Popup window should display all expected options");
		
		log.info("INFO: Add a tag "+tag);
		logger.strongStep("Add a tag");
		profilesUICnx8.typeWithDelayWd(tag, profilesUICnx8.createByFromSizzle(ProfilesUIConstants.ProfilesTagTypeAhead));
		profilesUICnx8.clickLinkWaitWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.ProfilesAddTag), 4, "Click on added tag "+tag);
		
		log.info("INFO: Click on added tag "+tag);
		logger.strongStep("Click on tag link");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getTagLink(tag)), 4);
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUICnx8.getTagLink(tag)), 4, "Click on added tag "+tag );
		
		log.info("INFO: Verify that it should redirect to Directory page which display the search result related to that tag");
		logger.strongStep("Verify that it should redirect to Directory page which display the search result related to that tag.");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getFilterOfTag(tag)), 4);
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.xpath(ProfilesUICnx8.getFilterOfTag(tag))), "Verify that filtered tag chip should be displayed");
		cnxAssert.assertTrue(driver.getTitle().equals("Search - Profiles"), "Verify taht it should redirect to directory search page");
		cnxAssert.assertTrue(profilesUICnx8.isElementDisplayedWd(By.xpath(ProfilesUICnx8.getSearchGrid(testUser2.getEmail()))), "Verify that search result should be rturned for the clicked tag");
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify the changes on My Network page for People- Another user overflow</li>
	 * <li><B>Step:</B> [API] User 1 will now invite User 2 to join their network</li>
	 * <li><B>Step:</B> [API] User 2 will now accept invite from User 1 to join their network</li>
	 * <li><B>Step:</B> [API] User 1 will follow User 2</li>
	 * <li><B>Step:</B> Load Profiles and login with User 1</li>
	 * <li><B>Step:</B> Select 'People' from left side navigation</li>
	 * <li><B>Step:</B> Click on user 2's name displayed in My Network list</li>
	 * <li><B>Step:</B> Click on three dots icon(more action)</li>
	 * <li><B>Step:</B> Select 'Remove From Network' from action menu</li>
	 * <li><B>Verify:</B> Verify message : {user2} has been removed from your network contact list. </li>
	 * <li><B>Step:</B> Select 'X' to close the message </li>
	 * <li><B>Step:</B> Click on three dots icon(more action) </li>
	 * <li><B>Verify:</B> Select 'Stop Following' from action menu"</li>
	 * <li><B>Verify:</B> Verify message : {user2} has been removed from your following list</li>
	 * <li><B>Step:</B> Select 'X' to close the message </li>
	 * <li><B>Step:</B> Select 'Download vCard' from action menu </li>
	 * <li><B>Verify:</B> Verify the dialog contains International Radio button choice</li>
	 * <li><B>Verify:</B> Verify the dialog contains Western European button choice</li>
	 * <li><B>Verify:</B> Verify the dialog contains Japanese Radio button choice</li>
	 * <li><B>Verify:</B> Verify the dialog contains Download button</li>
	 * <li><B>Verify:</B> Verify the dialog contains Cancel button</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/128980</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void verifyAnotherUserProfilesActionMenu() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
		profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser3);

		log.info("INFO: " + testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		Invitation user2NetworkInvite = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);

		log.info("INFO: "+testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		logger.strongStep(testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		ProfileEvents.acceptInvitationToJoinANetwork(user2NetworkInvite, profilesAPIUser2, profilesAPIUser1);
		 
		// User 1 will now follow User 2
		log.info("INFO: " + testUser1.getDisplayName() + " follow " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " follow " + testUser2.getDisplayName());
		ProfileEvents.followUser(profilesAPIUser2, profilesAPIUser1);
						
		log.info("INFO: Load Profiles and login: " + testUser1.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profiles and login: " + testUser1.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		log.info("INFO: Select 'People' from left side navigation");
		logger.strongStep("Select 'People' from left side navigation");
		AppNavCnx8.PEOPLE.select(profilesUICnx8);
		
		log.info("INFO: Click on "+testUser2.getDisplayName()+" displayed in My Network list");
		logger.strongStep("Click on "+testUser2.getDisplayName()+"  displayed in My Network list");
		profilesUICnx8.waitForElementVisibleWd(By.xpath(ProfilesUICnx8.getUserLinkFromAllPeople(testUser2.getDisplayName())), 4);
		profilesUICnx8.mouseHoverAndClickWd(ui.findElement(By.xpath(ProfilesUICnx8.getUserLinkFromAllPeople(testUser2.getDisplayName()))));
		
		logger.strongStep("Select 'Remove From Network' from action menu");
		profilesUICnx8.selectActionMenufromTable("Remove From Network");
		
		log.info("INFO: Verify message :"+testUser2.getDisplayName()+" has been removed from your network contact list");
		logger.strongStep("Verify message :"+testUser2.getDisplayName()+" has been removed from your network contact list");
		cnxAssert.assertTrue(profilesUICnx8.isTextPresentWd(testUser2.getDisplayName()+" has been removed from your network contact list."), "Verify '"+testUser2.getDisplayName()+" has been removed from your network contact list.' message is displayed");
		
		log.info("INFO: Select 'X' to close the message");
		logger.strongStep(" Select 'X' to close the message");
		profilesUICnx8.clickLinkWithJavaScriptWd(profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.closeButtonForMsg)));
		profilesUICnx8.waitForElementInvisibleWd(By.xpath(ProfilesUIConstants.closeButtonForMsg), 4);
		
		logger.strongStep("Select 'Stop Following' from action menu");
		profilesUICnx8.selectActionMenufromTable("Stop Following");
		
		log.info("INFO: Verify message :"+testUser2.getDisplayName()+" has been removed from your following list");
		logger.strongStep("Verify message :"+testUser2.getDisplayName()+" has been removed from your following list");
		cnxAssert.assertTrue(profilesUICnx8.isTextPresentWd(testUser2.getDisplayName()+" has been removed from your following list"), "Verify '"+testUser2.getDisplayName()+" has been removed from your following list");
		
		log.info("INFO: Select 'X' to close the message");
		logger.strongStep(" Select 'X' to close the message");
		profilesUICnx8.clickLinkWithJavaScriptWd(profilesUICnx8.findElement(By.xpath(ProfilesUIConstants.closeButtonForMsg)));
		profilesUICnx8.waitForElementInvisibleWd(By.xpath(ProfilesUIConstants.closeButtonForMsg), 4);
		
		logger.strongStep("Select 'Download vCard' from action menu");
		profilesUICnx8.selectActionMenufromTable("Download vCard");
		cnxAssert.assertTrue(profilesUICnx8.isElementVisibleWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.ExportVcardDialog),4), "Verify export vacrd dialogue is displayed");
		
		log.info("INFO: Verify the dialog contains International Radio button choice");
		logger.strongStep("Verify the dialog contains International Radio button choice");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.InternationalizedRadioButton)), "The dialog contains International Radio button choice");

		log.info("INFO: Verify the dialog contains Western European button choice");
		logger.strongStep("Verify the dialog contains Western European button choice");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.WesternEuropeanRadioButton)), "The dialog contains Western European button choice");
		
		log.info("INFO: Verify the dialog contains Japanese Radio button choice");
		logger.strongStep("Verify the dialog contains Japanese Radio button choice");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(profilesUICnx8.createByFromSizzle(ProfilesUIConstants.JapaneseRadioButton)), "The dialog contains Japanese Radio button choice");
		
		log.info("INFO: Verify the dialog contains Download button");
		logger.strongStep("Verify the dialog contains Download button");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.downloadBtnVcard)), "The dialog contains Download button");
		
		log.info("INFO: Verify the dialog contains Cancel button");
		logger.strongStep(" Verify the dialog contains Cancel button");
		cnxAssert.assertTrue(profilesUICnx8.isElementPresentWd(By.cssSelector(ProfilesUIConstants.cancelBtnVcard)), "The dialog contains Cancel button");
		
		profilesUICnx8.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Click and verify UserProfile image under NetworkSection</li>
	 * <li><B>Step:</B> Invite User to join network</li>
	 * <li><B>Step:</B> Accept Invitation to join network</li>
	 * <li><B>Step:</B> User 1 will now follow User 2</li>
	 * <li><B>Verify:</B> Load Homepage and login</li>
	 * <li><B>Step:</B> Click on User Image under test user1 Network section</li>
	 * <li><B>Verify:</B> Verify that test user2 People page is displayed</li>
	 * <li><B>JIRA Link 5th step:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T636</li>
	 * </ul>
	 */

	@Test(groups = { "cnx8ui-cplevel2" })
	public void clickAndverifyUserProfileUnderNetworkSection() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		profilesUICnx8.startTest();
		
		// Invite User to join network
		log.info("INFO: " + testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " sends invite to " + testUser2.getDisplayName());
		Invitation user2NetworkInvite = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);
		
		// Accept Invitation to join network
		log.info("INFO: "+testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		logger.strongStep(testUser2.getDisplayName()+" now accept invite from "+testUser1.getDisplayName());
		ProfileEvents.acceptInvitationToJoinANetwork(user2NetworkInvite, profilesAPIUser2, profilesAPIUser1);
		 
		// User 1 will now follow User 2
		log.info("INFO: " + testUser1.getDisplayName() + " follow " + testUser2.getDisplayName());
		logger.strongStep(testUser1.getDisplayName() + " follow " + testUser2.getDisplayName());
		ProfileEvents.followUser(profilesAPIUser2, profilesAPIUser1);

		log.info("INFO: Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		logger.strongStep("Load Profile and login: " + testUser.getDisplayName() + " and toggle to New UI as "+ cfg.getUseNewUI());
		profilesUICnx8.loadComponent(Data.getData().ComponentProfiles);
		profilesUICnx8.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);

		logger.strongStep("Click on User Image under " + testUser2.getDisplayName() + " Network Section");
		log.info("Click on User Image under " + testUser2.getDisplayName() + " Network Section");
		profilesUICnx8.clickLinkWaitWd(By.xpath(ProfilesUIConstants.userImageUnderNetworkSection), 5, "Clicked on User Image");
		
		logger.strongStep("Verify that " + testUser2.getDisplayName() + " is displayed");
		log.info("Verify that " + testUser2.getDisplayName() + " is displayed");
		cnxAssert.assertEquals(profilesUICnx8.getElementTextWd(By.xpath(ProfilesUIConstants.profileName)), testUser2.getDisplayName(),"" + testUser2.getDisplayName() + " People page is displayed ");
	
		profilesUICnx8.endTest();
	}
}
