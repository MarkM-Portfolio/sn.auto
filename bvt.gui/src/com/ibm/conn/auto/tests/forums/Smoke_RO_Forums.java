package com.ibm.conn.auto.tests.forums;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.Data.Side;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;

public class Smoke_RO_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Forums.class);

	private ForumsUI ui;
	private CommunitiesUI communitiesUI;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		communitiesUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Validate Forum fields</li>
	 *<li><B>Step: </B>Create a Community</li>
	 *<li><B>Step: </B>Click Forums in the left nav pane</li>
	 *<li><B>Step: </B>Click Start a Topic Button</li>
	 *<li><B>Verify: </B>Title Field is present</li>
	 *<li><B>Verify: </B>Description field is present</li>
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
		
		boolean communityExists = communitiesUI.communityExist(Data.getData().productionCommunityName);
		if(!communityExists) 
			Assert.fail("Prerequisite community '" + Data.getData().productionCommunityName + "' does not exits.");
		
		communitiesUI.openCommunity(Data.getData().productionCommunityName);
		
		log.info("INFO: Select Forums from the left navigation menu");
		Community_LeftNav_Menu.FORUMS.select(communitiesUI);
		ui.waitForSameTime();
		
		ui.fluentWaitPresent(ForumsUIConstants.Start_A_Topic);
		
		ui.gotoStartATopic();
		
		ui.fluentWaitTextPresent("Start a Topic");
		
		//Check Title Field
		ui.fluentWaitPresent(CommunitiesUIConstants.TopicTitle);
		
		//Check Description
		ui.fluentWaitPresent(BaseUIConstants.CKEditor_div);
		
		ui.endTest();
		
	}

}
