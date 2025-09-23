package com.ibm.conn.auto.tests.sharepoint;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.SharepointWidgetUI;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;


public class BVT_Sharepoint extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Sharepoint.class);
	private Assert cnxAssert;
	private CustomizerUI uiCnx7; 
	private TestConfigCustom cfg;
	private User testUser;
	private String appRegAppName = "SharePoint Widget Library (ORG D)";
	private CommunitiesUI ui;
	private SharepointWidgetUI sui;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		uiCnx7 = new CustomizerUI(driver);
		cfg = TestConfigCustom.getInstance();
		if (cfg.getTestConfig().serverIsMT())  {
			testUser = cfg.getUserAllocator().getGroupUser("app_admin_users");
		} else {
			testUser = cfg.getUserAllocator().getAdminUser();
		}
		
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
	// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		sui = SharepointWidgetUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new Assert(log);
		}
	
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify user is able to Un configure and Configure Share Point Widgets</li>
	 * <li><B>Step: </B>Login to connection with admin user</li>
	 * <li><B>Step: </B>Open App registry URL</li>
	 * <li><B>Step: </B>Delete the Share Point App from Application registry.</li>
	 * <li><B>Step: </B>Create a Community.</li>
	 * <li><B>Verify: </B>Verify share point widget option is Not present for the created community</li> 
	 * <li><B>Step: </B>Add the Share Point App from Application registry.</li>
	 * <li><B>Verify: </B>Verify share point widget option is present for the created community</li>
	 * </ul>
	 */
	@Test(groups = { "mtlevel2" , "spconfig"})
	public void configureSharepoint()
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = sui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName).build();
		
		logger.strongStep("Load Homepage Component and login: " +testUser.getDisplayName());
		log.info("Info: Load Homepage Component and login: " +testUser.getDisplayName());
		sui.loadComponent(Data.getData().ComponentHomepage);
		sui.login(testUser);
		
		log.info("Info: Load Appregistry");
		sui.loadComponent(Data.getData().ComponentCustomizer,true);
		
		logger.strongStep("Un configure the SharePoint from App Registry.");
		log.info("Info: Un configure the SharePoint from App Registry.");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) 
		{			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);
		}
	
		sui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.waitForPageLoaded(driver);
		ui.createFromDropDown(community);
		
		// navigate to the API community
		logger.strongStep("Naviagate to the Community and check 'SharePoint Library' is not Present in the Available Added Widgets.");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		sui.waitForElementVisibleWd(sui.createByFromSizzle(CommunitiesUIConstants.communityActions), 5);
		List<WebElement> menu = sui.findElements(sui.createByFromSizzle(CommunitiesUIConstants.tabbedNavMenuItems+ " a"));
		log.info("INFO: Names in list: " + menu.size());
		for (int i = 0; i < menu.size(); i++) {
			// Get single element from list
			WebElement ele = menu.get(i);
			String menuName = ele.getText().trim().toLowerCase();
			log.info("INFO: Menu is: " + menuName);
			cnxAssert.assertNotEquals("SharePoint Library", menuName,"Verify 'SharePoint Library' is not Present in the Available Added Widgets.");
		}
		
		log.info("INFO: Click on Community Actions Dropdown link");
		sui.clickLinkWaitWd(sui.createByFromSizzle(CommunitiesUIConstants.communityActions),5, "");
		sui.waitForElementVisibleWd(sui.createByFromSizzle(CommunitiesUIConstants.addAppslink), 10);
		
		logger.strongStep("Click on Add Apps link");
		log.info("INFO: Click on Add Apps link");
		sui.clickLinkWaitWd(sui.createByFromSizzle(CommunitiesUIConstants.addAppslink), 5, "");
		driver.changeImplicitWaits(5);
		logger.strongStep("Validate Share point Widget is Not present in Add Apps");
		log.info("INFO: Validate Share point Widget is Not present in Add Apps");
		cnxAssert.assertFalse(sui.isElementPresentWd(sui.createByFromSizzle(CommunitiesUIConstants.sharePointWidgetAdd)),"Verify that Sharepoint widget is not present in Add app");
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Click on 'Close' icon from add apps window");
		log.info("INFO: Click on 'Close' icon from add apps window");
		sui.clickLinkWaitWd(sui.createByFromSizzle(CommunitiesUIConstants.closeAddAppsWindow), 5, "");
	
		log.info("Info: Load Appregistry");
		sui.loadComponent(Data.getData().ComponentCustomizer,true);
		
		//configure the Share point via App Reg
		logger.strongStep("Configure the Share point via App Reg");
		log.info("Info: Configure the Share point via App Reg");
		SharepointWidgetUI.sharePointConfig(testUser, appRegAppName, uiCnx7, cfg, driver);
		
		sui.loadComponent(Data.getData().ComponentCommunities,true);
		sui.waitForPageLoaded(driver);
		
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		sui.waitForElementVisibleWd(sui.createByFromSizzle(CommunitiesUIConstants.communityActions), 5);
			
		logger.strongStep("Click on Community Actions dropdown");
		log.info("INFO: Click on Community Actions dropdown");
		sui.clickLinkWaitWd(sui.createByFromSizzle(CommunitiesUIConstants.communityActions), 5, "");
		
		logger.strongStep("Click on Add Apps link");
		log.info("INFO: Click on Add Apps link");
		sui.clickLinkWaitWd(sui.createByFromSizzle(CommunitiesUIConstants.addAppslink), 5, "");
		driver.changeImplicitWaits(5);
		logger.strongStep("Share point Widget is present in Add Apps");
		log.info("INFO: Share point Widget is present in Add Apps");
		sui.waitForElementVisibleWd(sui.createByFromSizzle(CommunitiesUIConstants.sharePointWidgetAdd), 10);
		cnxAssert.assertTrue(sui.isElementPresentWd(sui.createByFromSizzle(CommunitiesUIConstants.sharePointWidgetAdd)),"Verify the Sharepoint widget displayed");
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Click on 'Close' icon from add apps window");
		log.info("INFO: Click on 'Close' icon from add apps window");
		sui.clickLinkWaitWd(sui.createByFromSizzle(CommunitiesUIConstants.closeAddAppsWindow), 5, "");
			
		ui.logout();
		sui.endTest();
	}
}
