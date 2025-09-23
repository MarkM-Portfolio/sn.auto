package com.ibm.conn.auto.tests.homepage;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;

public class BVT_Level_2_Homepage_MT_Boundary extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_MT_Boundary.class);
	private HomepageUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA, testUser_orgB;
	private String serverURL_MT_orgA;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test TypeAhead functionality in Home page.</li>
	  * <li><B>Step: </B> Login to OrgA Home page.</li>
	 * <li><B>Step: </B> TypeAhead User from OrgB in Status Updates.</li>
	 * <li><B>Verify: </B>Verify that 'No results found' message should be displayed with option 'Person not listed? Use full search...'</li>
	 * <li><B>Step: </B> Select option 'Person not listed? Use full search...' </li>
	 * <li><B>Verify: </B>Verify 'No results found' message is displayed</li>
	 *</ul>
	 */

	@Test(groups = { "mtlevel2" })
	public void typeAheadHomepage() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		//Load component and login
        logger.strongStep("Load homepage and login");
        log.info("INFO: Logging in with user: " + testUser_orgA.getEmail());
		ui.loadComponent(serverURL_MT_orgA, Data.getData().HomepageImFollowing);
		ui.login(testUser_orgA);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on the text field 'What do you want to share ?' displayed in the center pane on homepage");
		log.info("INFO: Click on the text field 'What do you want to share ?' displayed in the center pane on homepage");
		Element statusUpdateElement = driver.getFirstElement(BaseUIConstants.StatusUpdate_iFrame);
		//driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		driver.switchToFrame().selectFrameByElement(statusUpdateElement);
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Type With Delay @"+testUser_orgB.getUid() +"in Status Updates.");
		logger.strongStep("Type With Delay @"+testUser_orgB.getUid() +"in Status Updates.");
		ui.typeTextWithDelay(BaseUIConstants.StatusUpdate_Body, "@"+testUser_orgB.getUid());
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
		
		ui.endTest();
}

}
