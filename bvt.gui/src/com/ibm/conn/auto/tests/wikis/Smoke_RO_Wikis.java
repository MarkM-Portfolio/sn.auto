package com.ibm.conn.auto.tests.wikis;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
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
import com.ibm.conn.auto.tests.blogs.Smoke_RO_Blogs;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;

public class Smoke_RO_Wikis extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Blogs.class);

	private WikisUI ui;
	private CommunitiesUI communitiesUI;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		communitiesUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Editing a Wiki page </li>
	 *<li><B>Step:</B> Open predefined community</li>
	 *<li><B>Step:</B> Click "Wikis" in left navigation</li>
	 *<li><B>Verify:</B> "Welcome to <Wiki name>" text is present </li>
	 *<li><B>Step:</B> Click "Edit"</li>
	 *<li><B>Verify:</B> "Page Title" text is present</li>
	 *<li><B>Verify:</B> CKEditor is present</li>
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
		
		log.info("INFO: Checking to see if community exists");
		boolean communityExists = communitiesUI.communityExist(Data.getData().productionCommunityName);
		if(!communityExists) 
			Assert.fail("Prerequisite community '" + Data.getData().productionCommunityName + "' does not exits.");
		
		log.info("INFO: Open the community");
		communitiesUI.openCommunity(Data.getData().productionCommunityName);
		
		log.info("INFO: Select Wikis from the left navigation menu");
		Community_LeftNav_Menu.WIKI.select(communitiesUI);	
		
		ui.fluentWaitTextPresent("Welcome to ProductionTestCommunity");
		
		ui.fluentWaitPresent(WikisUIConstants.Edit_Button);
		
		ui.gotoEditWiki();
		
		ui.fluentWaitTextPresent("Page Title");
		
		//Check Title field
		ui.fluentWaitPresent(WikisUIConstants.Page_Name_Textfield_In_Editor);
		
		ui.endTest();
		
	}

}
