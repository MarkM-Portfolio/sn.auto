package com.ibm.conn.auto.tests.socialSidebar;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.tests.homepage.BVT_Cnx8UI_Homepage;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.cnx8.HomepageUICnx8;
import com.ibm.conn.auto.webui.constants.SocialSidebarUIConstants;
import static com.ibm.conn.auto.webui.constants.SocialSidebarUIConstants.*;

public class BVT_Cnx8_SocialSidebar extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser;
	private HomepageUICnx8 homepageCnx8ui;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
	}

	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		homepageCnx8ui = new HomepageUICnx8(driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	 * <ul>
	 * <li><b>Info:</b> Basic test case for Social Sidebar to verify various UI 
	 * elements</li>
	 * <li><b>Step:</b>Login to Homepage Connections with the target user and go
	 * to Social Sidebar</li>
	 * <li><b>Step:</b>Verify sidebar toggle is displayed.</li>
	 * <li><b>Step:</b>Click on sidebar toggle to open sidebar container</li>
	 * <li><b>Step:</b>Verify Share Container is displayed</li>
	 * <li><b>Step:</b>Verify various UI elements like user info, updates tab etc are present inside sidebar</li>
	 * <li><b>Step:</b>Load homepage and logout of the application</li>
	 * <li><B>JIRA Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T646</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"}) 
	public void testSocialSidebar() {	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		homepageCnx8ui.startTest();

		logger.strongStep("Load homepage, login as " + testUser.getEmail() + " and load social sidebar");
		log.info("INFO: Load homepage, login as " + testUser.getEmail() + " and load social sidebar");
		homepageCnx8ui.loadComponent(Data.getData().HomepageImFollowing);
		homepageCnx8ui.login(testUser);
		homepageCnx8ui.loadComponent(Data.getData().ComponentSocialSidebar,true);
		
		logger.strongStep("Verify sidebar toggle is displayed");
		log.info("Verify sidebar toggle is displayed");
		homepageCnx8ui.waitForClickableElementWd(By.cssSelector(SocialSidebarUIConstants.socialSidebarToggle),3);
		cnxAssert.assertTrue(homepageCnx8ui.isElementDisplayedWd(By.cssSelector(SocialSidebarUIConstants.socialSidebarToggle)), "Social Sidebar toggle displayed");
		
		logger.strongStep("Click on sidebar toggle and verify sidebar container is displayed");
		log.info("Click on sidebar toggle and verify sidebar container is displayed");
		homepageCnx8ui.clickLinkWd(By.cssSelector(SocialSidebarUIConstants.socialSidebarToggle), "Click on sidebar toggle");
		homepageCnx8ui.switchToFrameByName(SocialSidebarUIConstants.socialSidebarIframe);
		homepageCnx8ui.waitForElementVisibleWd(By.cssSelector(SocialSidebarUIConstants.socialSidebarContainer),3);
		cnxAssert.assertTrue(homepageCnx8ui.isElementDisplayedWd(By.cssSelector(SocialSidebarUIConstants.socialSidebarContainer)), "Social Sidebar Container displayed");
		
		logger.strongStep("Verify various UI elements are present in sidebar");
		log.info("Verify various UI elements are present in sidebar");
		String [] sidebarUIElements = {profilePicture, userDisplayName, postArea, iAmFollowingTab, statusUpdatesTab,
				notificationsTab, mentionsTab, communitiesTab, bookmarksTab};
		for(String uiElement : sidebarUIElements) {
			cnxAssert.assertTrue(homepageCnx8ui.isElementDisplayedWd(By.cssSelector(uiElement)), uiElement + " is available");		
		}
			
		logger.strongStep("Load homepage and logout of the application");
		log.info("INFO:  Load homepage and logout of the application");
		homepageCnx8ui.switchToTopFrame();
		homepageCnx8ui.loadComponent(Data.getData().HomepageImFollowing,true);
		homepageCnx8ui.logout();

		homepageCnx8ui.endTest();
	}
	

}