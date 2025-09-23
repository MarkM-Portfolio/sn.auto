package com.ibm.conn.auto.tests.profiles;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.Data.Side;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;

public class Smoke_RO_Profiles extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Profiles.class);

	private HomepageUI ui;
	private ProfilesUI profilesUI;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		profilesUI = ProfilesUI.getGui(cfg.getProductName(), driver);
	}
	/**
	*<ul>
	*<li><B>Info: </B>Loading My Profiles page</li>
	*<li><B>Step: </B>Click Profiles< My Profile</li>
	*<li><B>Verify: </B>My Profile Text is present</li>
	*<li><B>Verify: </B>Display name of user logged in is present on page</li>
	*<li><B>Verify: </B>Recent Updates Tab is present</li>
	*</ul>
	*/ 
	@Test(groups = {"smoke"})
	public void testUILoad() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentDashboard);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		ui.checkContinueAnnouncement(BaseUIConstants.continueAnnouncement);
		
		log.info("INFO: Goto profiles");
		ui.gotoProfile();
		ui.waitForSameTime();

		ui.fluentWaitTextPresent("Profile");
		
		ui.fluentWaitTextPresent(testUser.getDisplayName());
		
		//Check Recent Updates tab
		profilesUI.verifyUpdatesTextArea();
		
		ui.endTest();
		
	}

}
