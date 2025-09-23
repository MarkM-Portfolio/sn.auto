package com.ibm.conn.auto.sandbox.community;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class widgetExample extends SetUpMethods2{

	

	private static Logger log = LoggerFactory.getLogger(widgetExample.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser;
	private BaseCommunity.Access defaultAccess;
	private APICommunitiesHandler apiOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load User
		testUser = cfg.getUserAllocator().getUser();	
		log.info("INFO: Using test user: " + testUser.getDisplayName());
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getUid(), testUser.getPassword());		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
	}
	
	@Test(groups = { "level2"})
	public void hideEventsWidget() throws Exception {

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .tags("hide widget")
									 .access(defaultAccess)
									 .description("Test Widgets inside community")
									 .build();

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//create a community
		log.info("INFO: Create a community");
		community.create(ui);
		
		
		//Customize community - Add the blogs widget
		log.info("INFO: Adding the " + BaseWidget.BLOG.getTitle() + " widget to community: "+ community.getName());
		community.addWidget(ui, BaseWidget.BLOG);

		//hide the widget
		log.info("INFO: Hide the widget");
		BaseWidget.BLOG.preformMenuAction(Widget_Action_Menu.HIDE, ui);
		
		ui.clickLinkWait("css=input[value='Hide']");
		
		//validate that the widget is hidden
		
		//open the addapp
		Com_Action_Menu.ADDAPP.select(ui);
		
		//navigate to hidden
		
		//validate that widget is hidden
		
		ui.endTest();
	}
	
	@Test(groups = { "level2"})
	public void hideBlogWidgetAPI() throws Exception {

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .tags("hide widget")
									 .access(defaultAccess)
									 .description("Test Widgets inside community")
									 .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget
		log.info("INFO: Add media gallery widget to community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);		
		
		log.info("INFO: Hide the widget");
		BaseWidget.BLOG.preformMenuAction(Widget_Action_Menu.HIDE, ui);
		
		log.info("INFO: Click on hide button");
		ui.clickLinkWait(CommunitiesUIConstants.WidgetHideConfirm);
		
		//validate that the widget is hidden
		
		//open the addapp
		log.info("INFO: Selecting addapp from communities action menu");
		Com_Action_Menu.ADDAPP.select(ui);
		
		//navigate to hidden
		log.info("INFO: Navigate to hidden view");
		ui.clickLinkWait("link=Hidden");
		
		//validate that widget is hidden
		log.info("INFO: Validate that widget is Hidden");
		Assert.assertTrue(ui.fluentWaitPresent("css=a[title='Blog']"),
						 "ERROR: Unable to find blog in hidden view");
		
		ui.endTest();
	}
	
}
