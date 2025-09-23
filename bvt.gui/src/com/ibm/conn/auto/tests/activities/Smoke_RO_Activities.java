package com.ibm.conn.auto.tests.activities;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
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
import com.ibm.conn.auto.webui.ActivitiesUI;

public class Smoke_RO_Activities extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Activities.class);

	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Load Activities component</li>
	 *<li><B>Verify:</B>"My Activities" text presence</li>
	 *<li><B>Step:</B>Goto and click "Start an Activity"</li>
	 *<li><B>Verify:</B>"Start an Activity" title text is present</li>
	 *<li><B>Verify:</B>Name textbox element is present</li>
	 *<li><B>Verify:</B>Tags textbox element is present</li>
	 *</ul>
	 */	
	
	@Test(groups = {"smoke"})
	public void testUILoad() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start of the test
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentActivities);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		ui.waitForSameTime();
		
		ui.fluentWaitTextPresent("My Activities");
		
		log.info("INFO: Goto start Activity");
		ui.gotoStartActivity();

		ui.waitForSameTime();
		
		//Check title
		ui.fluentWaitTextPresent("Start an Activity");
		
		//Check name field
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		
		//Check tags field
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Tags);
		
		//End of the test
		ui.endTest();
		
	}

}
