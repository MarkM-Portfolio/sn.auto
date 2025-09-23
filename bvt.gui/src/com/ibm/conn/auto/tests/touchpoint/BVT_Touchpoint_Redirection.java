package com.ibm.conn.auto.tests.touchpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CustomizerUI;
import com.ibm.conn.auto.webui.TouchpointUI;


public class BVT_Touchpoint_Redirection extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Touchpoint_Redirection.class);
	private CustomizerUI uiCnx7; 
	private TestConfigCustom cfg;
	private User testUser;
	private String appRegAppName = "Touchpoint";

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
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify user gets navigated to connections homepage when touchpoint is disabled on MT server of not configured</li>
	 * <li><B>Step: </B>Login to connection with admin user</li>
	 * <li><B>Step: </B>Open App registry URL</li>
	 * <li><B>Verify: </B>Verify touchpoint is not enabled or configured</li> 
	 * <li><B>Step: </B>Open touchpoint URL in browser</li>
	 * <li><B>Verify: </B>Verify user should get redirected to homepage</li>
	 * </ul>
	 */
	@Test(groups = { "mtlevel2"})
	public void verifyTouchpointRedirection()
	{
		TouchpointUI ui = TouchpointUI.getGui(cfg.getProductName(), driver);	
				
		log.info("Info: Load Componenet and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
				
		log.info("Info: Load Appregistry");
		ui.loadComponent(Data.getData().ComponentCustomizer,true);
		
		log.info("Info: Verify if touchpoint is configured or enabled and disable it if present");
		try {
			if (uiCnx7.isAppEnabled(appRegAppName)) {
				uiCnx7.disableAppFromAppReg(appRegAppName);
			}
		} catch (IllegalArgumentException e) {
			log.error(appRegAppName+" is not configured");
		}
		
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		
		log.info("Switch to Touchpoint: " + cfg.getTestConfig().getBrowserURL()
				+ Data.getData().ComponentTouchpoint);
		driver.navigate().to(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentTouchpoint);
		ui.waitForPageLoaded(driver);

		Assert.assertTrue(ui.isTextPresent("Share Something"), "User is redirected to Homepage");
				
		ui.logout();
	}
}
