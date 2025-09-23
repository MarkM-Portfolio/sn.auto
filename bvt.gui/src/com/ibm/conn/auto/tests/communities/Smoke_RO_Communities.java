package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Smoke_RO_Communities extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Communities.class);

	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Open Communities component</li>
	 *<li><B>Step:</B>Click Start a Community button</li>
	 *<li><B>Verify:</B>"Start a Community" text</li>
	 *<li><B>Verify:</B>Name textbox presence</li>
	 *</ul>
	 */
	@Test(groups = {"smoke"})
	public void testUILoad() throws Exception{
		User testUser = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		Side side = ui.replaceProductionCookies();
		ui.login(testUser);
		ui.validateSelectedNode(APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL()), side);
		
		ui.fluentWaitTextPresent("Communities");

		ui.fluentWaitPresent(CommunitiesUIConstants.StartACommunity);
		
		log.info("INFO: Goto Start a community");
		ui.gotoStartACommunity();
		
		ui.fluentWaitTextPresent("Start a Community");
		
		//Check Name field
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		
		ui.endTest();
		
	}

}
