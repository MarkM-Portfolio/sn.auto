package com.ibm.conn.auto.sandbox.community;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class AddMediaGallery extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(AddMediaGallery.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	private APICommunitiesHandler apiOwner;
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();

		//Create a community base state object

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getUid(), testUser.getPassword());
	}
	
	@Test(groups = { "level2", "bvtcloud"})
	public void testMediaGallery() throws Exception {

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
									 			   .description("Test Widgets inside community")
									 			   .build();

		
		
		ui.startTest();

		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.MEDIA_GALLERY);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	
		log.info("INFO: Open community");
		ui.fluentWaitPresentWithRefresh(community.getName());		
		ui.clickLinkWait("link=" + community.getName());
		
		
		ui.endTest();
	}
	
	@Test(groups = { "level2", "bvtcloud"})
	public void testBlogs() throws Exception {

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
									 			   .description("Test Widgets inside community")
									 			   .build();

		
		
		ui.startTest();

		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add widget
		log.info("INFO: Add blog widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	
		log.info("INFO: Open community");
		ui.fluentWaitPresentWithRefresh(community.getName());		
		ui.clickLinkWait("link=" + community.getName());
		
		
		ui.endTest();
	}
	
}
