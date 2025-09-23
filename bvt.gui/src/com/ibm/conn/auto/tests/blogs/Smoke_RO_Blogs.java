package com.ibm.conn.auto.tests.blogs;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
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
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;

public class Smoke_RO_Blogs extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Smoke_RO_Blogs.class);

	private BlogsUI ui;
	private CommunitiesUI communitiesUI;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);	
		communitiesUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Adding a Blog widget</li>
	*<li><B>Step: </B>Create a Community</li>
	*<li><B>Step: </B>Click Community Actions > Add Apps</li>
	*<li><B>Step: </B> Add Blog widget</li>
	*<li><B>Step: </B>Click Blog in left nav pane</li> 
	*<li><B>Step: </B>Click New Entry button</li>
	*<li><B>Verify: </B>New Entry text is present</li>
	*<li><B>Verify: </B>Title field is present</li>
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
		
		Community_LeftNav_Menu.BLOG.select(communitiesUI);

		ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntry);
			
		log.info("INFO: Goto New Entry");
		ui.gotoNewEntry();
		
		//Check title text
		ui.fluentWaitTextPresent("New Entry");
		
		//Check Title field
		ui.fluentWaitPresent(CommunitiesUIConstants.NewIdea_Title);
		
		ui.endTest();
		
	}

}
