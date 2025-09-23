package com.ibm.conn.auto.tests.dogear;

import com.ibm.conn.auto.webui.constants.DogearUIConstants;
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
import com.ibm.conn.auto.webui.DogearUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;

public class Smoke_RO_Bookmarks extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Bookmarks.class);

	private DogearUI ui;
	private CommunitiesUI communitiesUI;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = DogearUI.getGui(cfg.getProductName(), driver);
		communitiesUI = CommunitiesUICloud.getGui(cfg.getProductName(), driver);
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Loading Boomarks widget Full Page</li>
	*<li><B>Step: </B>Create a Community</li>
	*<li><B>Step: </B>Click Bookmarks in the left nav pane</li>
	*<li><B>Step: </B>Add Bookmark button</li>
	*<li><B>Verify: </B>Add Bookmark text is present</li>
	*<li><B>Verify: </B>URL text is present</li>
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
		
		log.info("INFO: Select Bookmarks from the left navigation menu");
		Community_LeftNav_Menu.BOOKMARK.select(communitiesUI);	
			
		ui.fluentWaitPresent(DogearUIConstants.AddBookmark);
		
		ui.gotoAddBookmark();
		
		ui.fluentWaitTextPresent("Add Bookmark");
		
		ui.fluentWaitTextPresent("URL");
		
		ui.endTest();
		
	}

}
