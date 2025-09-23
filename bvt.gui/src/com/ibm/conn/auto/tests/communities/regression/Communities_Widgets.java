package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Communities_Widgets extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(Communities_Widgets.class);

	private static CommunitiesUI ui;
	private static TestConfigCustom cfg;
	private static User testUser;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private final static BaseWidget[] defaultCommunityWidgets = {
		BaseWidget.COMMUNITYDESCRIPTION,
		BaseWidget.FORUM,
		BaseWidget.FILES,
		BaseWidget.BOOKMARKS,
		BaseWidget.IMPORTANTBOOKMARKS,
		BaseWidget.MEMBERS,
		BaseWidget.TAGS,
		BaseWidget.WIKI,
		BaseWidget.BLOG
	};

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() throws Exception {

	cfg = TestConfigCustom.getInstance();

	testUser = cfg.getUserAllocator().getUser();
	log.info("INFO: Using test user: " + testUser.getDisplayName());

	serverURL = APIUtils.formatBrowserURLForAPI(testConfig
			.getBrowserURL());
	apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
			testUser.getPassword());
	}
	
	public BaseCommunity setupCommunity(BaseWidget widget) {

		BaseCommunity.Access defaultAccess = CommunitiesUI
				.getDefaultAccess(cfg.getProductName());


		BaseCommunity community = new BaseCommunity.Builder(Helper.stamp(Data.getData().WF_CommunityName)).access(defaultAccess)
				.description("Test Widgets inside community").build();

		// create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Check the widget is not already on the page, which would cause an API error
		boolean addWidget = true;
		for (BaseWidget existingWidget : defaultCommunityWidgets) {
			if (widget.equals(existingWidget)) {
				addWidget = false;
				break;
			}
		}
		
		if(addWidget) {
			//add the widget to the community
			log.info("INFO: Adding the " + widget.getTitle() +
					" widget to community " + community.getName() + " using API");
			community.addWidgetAPI(comAPI, apiOwner, widget);
		}

		return community;

	}




	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Forums widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Forums widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Remove_ForumsWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Forums  widget  </B> </li>
	 *<li><B>Click Hide from Forums widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Forums widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_ForumsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Forums widget  </B> </li>
	 *<li><B>Click Help from Forums widget context menu</B> </li>
	 *<li><B>Verify: Forums widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_ForumsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Forums widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Forums widget context menu</B> </li>
	 *<li><B>Verify: Forums widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_ForumsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Forums widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Forums widget context menu</B> </li>
	 *<li><B>Click Maximize from Forums widget context menu</B> </li>
	 *<li><B>Verify: Forums widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_ForumsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Forums widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Forums widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_ForumsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Forums widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Forums widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_ForumsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FORUM;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Activities widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Activities widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Remove_ActivitiesWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Activities  widget  </B> </li>
	 *<li><B>Click Hide from Activities widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Activities widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Hide_ActivitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.waitForPageLoaded(driver);
		
		community.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Activities widget  </B> </li>
	 *<li><B>Click Help from Activities widget context menu</B> </li>
	 *<li><B>Verify: Activities widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_ActivitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Activities widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Activities widget context menu</B> </li>
	 *<li><B>Verify: Activities widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_ActivitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Activities widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Activities widget context menu</B> </li>
	 *<li><B>Click Maximize from Activities widget context menu</B> </li>
	 *<li><B>Verify: Activities widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_ActivitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Activities widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Activities widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_ActivitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Activities widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Activities widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_ActivitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.ACTIVITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add IdeationBlog widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: IdeationBlog widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Remove_IdeationBlogWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add IdeationBlog  widget  </B> </li>
	 *<li><B>Click Hide from IdeationBlog widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: IdeationBlog widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_IdeationBlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add IdeationBlog widget  </B> </li>
	 *<li><B>Click Help from IdeationBlog widget context menu</B> </li>
	 *<li><B>Verify: IdeationBlog widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_IdeationBlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add IdeationBlog widget onto overview page</B> </li>
	 *<li><B>Click Minimize from IdeationBlog widget context menu</B> </li>
	 *<li><B>Verify: IdeationBlog widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_IdeationBlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add IdeationBlog widget onto overview page</B> </li>
	 *<li><B>Click Minimize from IdeationBlog widget context menu</B> </li>
	 *<li><B>Click Maximize from IdeationBlog widget context menu</B> </li>
	 *<li><B>Verify: IdeationBlog widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_IdeationBlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add IdeationBlog widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: IdeationBlog widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_IdeationBlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add IdeationBlog widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: IdeationBlog widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_IdeationBlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IDEATION_BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}


	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Files widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Files widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Remove_FilesWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Files  widget  </B> </li>
	 *<li><B>Click Hide from Files widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Files widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_FilesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Files widget  </B> </li>
	 *<li><B>Click Help from Files widget context menu</B> </li>
	 *<li><B>Verify: Files widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_FilesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Files widget onto overview page</B> </li>
	 *<li><B>Click Minimize from files widget context menu</B> </li>
	 *<li><B>Verify: files widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_FilesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Files widget onto overview page</B> </li>
	 *<li><B>Click Minimize from files widget context menu</B> </li>
	 *<li><B>Click Maximize from files widget context menu</B> </li>
	 *<li><B>Verify: files widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_FilesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add files widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: files widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_FilesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add files widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: files widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_FilesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FILES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Files widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Blog widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud"})
	public void Remove_BlogWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Blog  widget  </B> </li>
	 *<li><B>Click Hide from Blod widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Blog widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_BlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Files widget  </B> </li>
	 *<li><B>Click Help from Files widget context menu</B> </li>
	 *<li><B>Verify: Files widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_BlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Blog widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Blog widget context menu</B> </li>
	 *<li><B>Verify: Blog widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_BlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Blog widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Blog widget context menu</B> </li>
	 *<li><B>Click Maximize from Blog widget context menu</B> </li>
	 *<li><B>Verify: Blog widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_BlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Blog widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Blog widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_BlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Blog widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Blog widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_BlogWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.BLOG;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}


	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Wiki widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Wiki widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Remove_WikiWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Wiki  widget  </B> </li>
	 *<li><B>Click Hide from Wiki widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Wiki widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Hide_WikiWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements("xpath=//span[contains(text(),'Wiki')]//ancestor::h2//parent::div").get(0).getWebElement());
		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Wiki widget  </B> </li>
	 *<li><B>Click Help from Wiki widget context menu</B> </li>
	 *<li><B>Verify: Wiki widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_WikiWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Wiki widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Wiki widget context menu</B> </li>
	 *<li><B>Verify: Wiki widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_WikiWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Wiki widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Wiki widget context menu</B> </li>
	 *<li><B>Click Maximize from Wiki widget context menu</B> </li>
	 *<li><B>Verify: Wiki widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_WikiWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Wiki widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Wiki widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_WikiWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Wiki widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Wiki widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_WikiWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.WIKI;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}


	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Subcommunities widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Subcommunities widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Remove_SubcommunitiesWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Subcommunities  widget  </B> </li>
	 *<li><B>Click Hide from Subcommunities widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Subcommunities widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_SubcommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Subcommunities widget  </B> </li>
	 *<li><B>Click Help from Subcommunities widget context menu</B> </li>
	 *<li><B>Verify: Subcommunities widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_SubcommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Subcommunities widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Subcommunities widget context menu</B> </li>
	 *<li><B>Verify: Subcommunities widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_SubcommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Subcommunities widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Subcommunities widget context menu</B> </li>
	 *<li><B>Click Maximize from Subcommunities widget context menu</B> </li>
	 *<li><B>Verify: Subcommunities widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_SubcommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Subcommunities widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Subcommunities widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_SubcommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Subcommunities widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Subcommunities widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_SubcommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.SUBCOMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}


	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Feeds widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Feeds widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" })
	public void Remove_FeedsWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Feeds  widget  </B> </li>
	 *<li><B>Click Hide from Feeds widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Feeds widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Hide_FeedsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Feeds widget  </B> </li>
	 *<li><B>Click Help from Feeds widget context menu</B> </li>
	 *<li><B>Verify: Feeds widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Help_FeedsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Feeds widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Feeds widget context menu</B> </li>
	 *<li><B>Verify: Feeds widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Minimize_FeedsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Feeds widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Feeds widget context menu</B> </li>
	 *<li><B>Click Maximize from Feeds widget context menu</B> </li>
	 *<li><B>Verify: Feeds widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression"} , enabled=false )
	public void Maximize_FeedsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Feeds widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Feeds widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression" })
	public void MoveToTop_FeedsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Feeds widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Feeds widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression" })
	public void MoveToBottom_FeedsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.FEEDS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}


	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Files widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Library widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Remove_LibraryWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Library  widget  </B> </li>
	 *<li><B>Click Hide from Library widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Library widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Hide_LibraryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Library widget  </B> </li>
	 *<li><B>Click Help from Library widget context menu</B> </li>
	 *<li><B>Verify: Library widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression"} , enabled=false )
	public void Help_LibraryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Blog widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Blog widget context menu</B> </li>
	 *<li><B>Verify: Blog widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression"} , enabled=false )
	public void Minimize_LibraryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Library widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Library widget context menu</B> </li>
	 *<li><B>Click Maximize from Library widget context menu</B> </li>
	 *<li><B>Verify: Library widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Maximize_LibraryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
	
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Library widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Library widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void MoveToTop_LibraryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Library widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Library widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void MoveToBottom_LibraryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.LIBRARY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}



	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Members widget  </B> </li>
	 *<li><B>Click Help from Members widget context menu</B> </li>
	 *<li><B>Verify: Members widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_MembersWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Members widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Members widget context menu</B> </li>
	 *<li><B>Verify: Members widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_MembersWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Library widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Library widget context menu</B> </li>
	 *<li><B>Click Maximize from Library widget context menu</B> </li>
	 *<li><B>Verify: Library widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_MembersWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}
	
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Members widget Hide link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Hide from the widget context menu</li>
	 *<li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 *<li><B>Verify:</B> Members widget is hidden from the Overview page</li>
	 *<li><B>Step:</B> Re-add the hidden widget back to the community via Add Apps palette</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" } , enabled=false )
	public void Hide_MembersWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
			
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Members widget Remove link test</li>
	 * <li><B>Step:</B> Create a community</li>
	 * <li><B>Step:</B> Click Remove from the Members widget context menu</li>
	 * <li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 * <li><B>Verify:</B> Members widget is removed from the community</li>
	 * <li><B>Step:</B> Re-add the deleted widget</li>
	 * <li><B>Cleanup:</B> Delete the community</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void Remove_MembersWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Members widget Move Up link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Move Up from the widget context menu - if it is not already in the top-most position</li>
	 *<li><B>Verify:</B> Members widget is successfully moved up a position</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void MoveToTop_MembersWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Members widget Move Down link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Move Down from the widget context menu - if it is not already in the bottom-most position</li>
	 *<li><B>Verify:</B> Members widget is successfully moved down a position</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" } , enabled=false )
	public void MoveToBottom_MembersWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.MEMBERS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Gallery widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Gallery widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" })
	public void Remove_GalleryWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Gallery  widget  </B> </li>
	 *<li><B>Click Hide from Gallery widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Gallery widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression"} , enabled=false )
	public void Hide_GalleryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Gallery widget  </B> </li>
	 *<li><B>Click Help from Gallery widget context menu</B> </li>
	 *<li><B>Verify: Gallery widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Help_GalleryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Gallery widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Gallery widget context menu</B> </li>
	 *<li><B>Verify: Gallery widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Minimize_GalleryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Gallery widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Gallery widget context menu</B> </li>
	 *<li><B>Click Maximize from Gallery widget context menu</B> </li>
	 *<li><B>Verify: Gallery widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression" } , enabled=false )
	public void Maximize_GalleryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Gallery widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Gallery widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression" })
	public void MoveToTop_GalleryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Gallery widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Gallery widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression" })
	public void MoveToBottom_GalleryWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.GALLERY;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add Events widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: Events widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud"})
	public void Remove_EventsWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Events  widget  </B> </li>
	 *<li><B>Click Hide from Events widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: Events widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_EventsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Events widget  </B> </li>
	 *<li><B>Click Help from Events widget context menu</B> </li>
	 *<li><B>Verify: Events widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_EventsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Events widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Events widget context menu</B> </li>
	 *<li><B>Verify: Events widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_EventsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Events widget onto overview page</B> </li>
	 *<li><B>Click Minimize from Events widget context menu</B> </li>
	 *<li><B>Click Maximize from Events widget context menu</B> </li>
	 *<li><B>Verify: Events widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_EventsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Events widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: Events widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_EventsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add Events widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: Events widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_EventsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.EVENTS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);
		
		ui.waitForCommunityLoaded();

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>It can be assumed that each testcase starts with login
	 * and ends with logout and the browser being closed</li>
	 * <li><B>Step: Create a community and then add RelatedCommunities widget </B></li>
	 * <li><B>Click Remove from widget context menu</B></li>
	 * <li><B>Specify widget name and current user on remove widget dialog</B></li>
	 * <li><B>Click OK button on remove widget dialog</B></li>
	 * <li><B>Verify: RelatedCommunities widget is removed from the community</B></li>
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void Remove_RelatedCommunitiesWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add RelatedCommunities  widget  </B> </li>
	 *<li><B>Click Hide from RelatedCommunities widget context menu</B> </li>
	 *<li><B>Click OK button on hide widget dialog</B> </li>
	 *<li><B>Verify: RelatedCommunities widget is hided from overview page</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Hide_RelatedCommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add RelatedCommunities widget  </B> </li>
	 *<li><B>Click Help from RelatedCommunities widget context menu</B> </li>
	 *<li><B>Verify: RelatedCommunities widget help page can be opened.</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Help_RelatedCommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Help on Widget: "+ widget.getTitle());
		help(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add RelatedCommunities widget onto overview page</B> </li>
	 *<li><B>Click Minimize from RelatedCommunities widget context menu</B> </li>
	 *<li><B>Verify: RelatedCommunities widget minimized. Only title bar is displayed</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Minimize_RelatedCommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add RelatedCommunities widget onto overview page</B> </li>
	 *<li><B>Click Minimize from RelatedCommunities widget context menu</B> </li>
	 *<li><B>Click Maximize from RelatedCommunities widget context menu</B> </li>
	 *<li><B>Verify: RelatedCommunities widget maximized. </B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" } , enabled=false )
	public void Maximize_RelatedCommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add RelatedCommunities widget </B> </li>
	 *<li><B>Step: Click Move up from widget context menu if it is not in the first row </B> </li>
	 *<li><B>Verify: RelatedCommunities widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToTop_RelatedCommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
	}

	/*
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each testcase starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a community and then add RelatedCommunities widget </B> </li>
	 *<li><B>Step: Click Move down from widget context menu if it is not in the last row </B> </li>
	 *<li><B>Verify: RelatedCommunities widget is added to the community and do the Move up action successfully </B> </li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression", "bvtcloud" })
	public void MoveToBottom_RelatedCommunitiesWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.RELATED_COMMUNITIES;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
	}
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Tags widget Hide link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Hide from the widget context menu</li>
	 *<li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 *<li><B>Verify:</B> Tags widget is hidden from the Overview page</li>
	 *<li><B>Step:</B> Re-add the hidden widget back to the community via Add Apps palette</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void Hide_TagsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.TAGS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
			
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Tags widget Maximize link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Minimize from the Tags widget context menu</li>
	 *<li><B>Step:</B> Click Maximize from the Tags widget context menu</li>
	 *<li><B>Verify:</B> The Tags widget maximized</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud"} , enabled=false )
	public void Maximize_TagsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.TAGS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Tags widget Minimize link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Minimize from the Tags widget context menu</li>
	 *<li><B>Step:</B> Click Maximize from the Tags widget context menu</li>
	 *<li><B>Verify:</B> The Tags widget minimized. Only title bar is displayed</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 *</ul>
	 *@throws Exception
	 */
	
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" } , enabled=false )
	public void Minimize_TagsWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.TAGS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Tags widget Remove link test</li>
	 * <li><B>Step:</B> Create a community</li>
	 * <li><B>Step:</B> Click Remove from the Tags widget context menu</li>
	 * <li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 * <li><B>Verify:</B> Tags widget is removed from the community</li>
	 * <li><B>Step:</B> Re-add the deleted widget</li>
	 * <li><B>Cleanup:</B> Delete the community</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void Remove_TagsWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.TAGS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
		
		log.info("INFO: Removing community");
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);
		

		ui.endTest();
	}

	
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Important Bookmarks widget Hide link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Hide from the widget context menu</li>
	 *<li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 *<li><B>Verify:</B> Important Bookmarks widget is hidden from the Overview page</li>
	 *<li><B>Step:</B> Re-add the hidden widget back to the community via Add Apps palette</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" } , enabled=false )
	public void Hide_ImportantBkmksWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IMPORTANTBOOKMARKS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Important Bookmarks widget Maximize link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Minimize from the Important Bookmarks widget context menu</li>
	 *<li><B>Step:</B> Click Maximize from the Important Bookmarks widget context menu</li>
	 *<li><B>Verify:</B> The Important Bookmarks widget maximized</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud"} , enabled=false )
	public void Maximize_ImportantBkmksWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IMPORTANTBOOKMARKS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Important Bookmarks widget Minimize link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Minimize from the Important Bookmarks widget context menu</li>
	 *<li><B>Step:</B> Click Maximize from the Important Bookmarks widget context menu</li>
	 *<li><B>Verify:</B> The Important Bookmarks widget minimized. Only title bar is displayed</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 *</ul>
	 *@throws Exception
	 */
	
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" } , enabled=false )
	public void Minimize_ImportantBkmksWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IMPORTANTBOOKMARKS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Important Bookmarks widget Move Down link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Move Down from the widget context menu - if it is not already in the bottom-most position</li>
	 *<li><B>Verify:</B> Important Bookmarks widget is successfully moved down a position</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void MoveToBottom_ImportantBkmksWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IMPORTANTBOOKMARKS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Important Bookmarks widget Move Up link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Move Up from the widget context menu - if it is not already in the top-most position</li>
	 *<li><B>Verify:</B> Important Bookmarks widget is successfully moved up a position</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void MoveToTop_ImportantBkmksWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.IMPORTANTBOOKMARKS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Important Bookmarks widget Remove link test</li>
	 * <li><B>Step:</B> Create a community</li>
	 * <li><B>Step:</B> Click Remove from the Important Bookmarks widget context menu</li>
	 * <li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 * <li><B>Verify:</B> Important Bookmarks widget is removed from the community</li>
	 * <li><B>Step:</B> Re-add the deleted widget</li>
	 * <li><B>Cleanup:</B> Delete the community</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void Remove_ImportantBkmksWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.IMPORTANTBOOKMARKS;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Community Description widget Hide link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Hide from the widget context menu</li>
	 *<li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 *<li><B>Verify:</B> Community Description widget is hidden from the Overview page</li>
	 *<li><B>Step:</B> Re-add the hidden widget back to the community via Add Apps palette</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void Hide_CommDescriptionWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.COMMUNITYDESCRIPTION;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Hide Widget: "+ widget.getTitle());
		hide(widget);

		log.info("Add hidden widget: "+ widget.getTitle());
		ui.addHiddenWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Community Description widget Maximize link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Minimize from the Community Description widget context menu</li>
	 *<li><B>Step:</B> Click Maximize from the Community Description widget context menu</li>
	 *<li><B>Verify:</B> The Community Description widget maximized</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud"} , enabled=false )
	public void Maximize_CommDescriptionWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.COMMUNITYDESCRIPTION;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Maximize on Widget: "+ widget.getTitle());
		maximize(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Community Description widget Minimize link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Minimize from the Community Description widget context menu</li>
	 *<li><B>Step:</B> Click Maximize from the Community Description widget context menu</li>
	 *<li><B>Verify:</B> The Community Description widget minimized. Only title bar is displayed</li>
	 *<li><B>Cleanup:</B> Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 *</ul>
	 *@throws Exception
	 */
	
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" } , enabled=false )
	public void Minimize_CommDescriptionWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.COMMUNITYDESCRIPTION;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		community.navViaUUID(ui);

		log.info("Minimize on Widget: "+ widget.getTitle());
		minimize(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/*
	 *<ul>
	 *<li><B>Info:</B> Community Description widget Move Down link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Move Down from the widget context menu - if it is not already in the bottom-most position</li>
	 *<li><B>Verify:</B> Community Description widget is successfully moved down a position</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void MoveToBottom_CommDescriptionWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.COMMUNITYDESCRIPTION;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);

		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToBottom(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}

	
	/*
	 *<ul>
	 *<li><B>Info:</B> Community Description widget Move Up link test</li>
	 *<li><B>Step:</B> Create a community</li>
	 *<li><B>Step:</B> Click Move Up from the widget context menu - if it is not already in the top-most position</li>
	 *<li><B>Verify:</B> Community Description widget is successfully moved up a position</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void MoveToTop_CommDescriptionWidgetAction() throws Exception {
		ui.startTest();
		BaseWidget widget = BaseWidget.COMMUNITYDESCRIPTION;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		community.navViaUUID(ui);

		log.info("Move to Top on Widget: "+ widget.getTitle());
		moveToTop(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Community Description widget Remove link test</li>
	 * <li><B>Step:</B> Create a community</li>
	 * <li><B>Step:</B> Click Remove from the Community Description widget context menu</li>
	 * <li><B>Step:</B> Click OK button on hide widget pop-up dialog.  NOTE: small confirmation dialog.  Does not require app or user info.</li>
	 * <li><B>Verify:</B> Community Description widget is removed from the community</li>
	 * <li><B>Step:</B> Re-add the deleted widget</li>
	 * <li><B>Cleanup:</B> Delete the community</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li
	 * </ul>
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "regression","cnx8ui-regression","bvtcloud" })
	public void Remove_CommDescriptionWidgetAction() throws Exception {

		ui.startTest();
		BaseWidget widget = BaseWidget.COMMUNITYDESCRIPTION;
		BaseCommunity community = setupCommunity(widget);

		ui.loadComponent(Data.getData().ComponentCommunities);

		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		Community comAPI = apiOwner.getCommunity(community.getCommunityUUID());

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		community.navViaUUID(ui);

		log.info("Remove Widget: "+ widget.getTitle());
		remove(widget);

		log.info("Add widget: "+ widget.getTitle());
		ui.addWidget(widget);
		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();
	}

	private void moveToTop(BaseWidget widget) {
		int row =  ui.getWidgetLocation(widget);
		for(;row>1;row--){
			log.info("INFO: Perform action " + Widget_Action_Menu.MOVEUP.getMenuItemText() + " on widget " + widget.getTitle());
			log.info("Current location of widget "+widget.getTitle()+" is: " + row);
			ui.performCommWidgetAction(widget, Widget_Action_Menu.MOVEUP);
			Assert.assertEquals(ui.getWidgetLocation(widget), row - 1, "ERROR: Failed to move up widget: " + widget);
		} 
		if(row ==1)
			Assert.assertFalse(ui.checkWidgetMenuPresent(widget, Widget_Action_Menu.MOVEUP),"ERROR: Move up is still enabled when widget is in the first row");
	}

	private void moveToBottom(BaseWidget widget) {
		int row =  ui.getWidgetLocation(widget);
		int size=0;
		if(widget.getColumn().equals("col2"))
			size = ui.getCenterColumnSize();
		else if (widget.getColumn().equals("col3"))
			size = ui.getRightColumnSize();
		else if (widget.getColumn().equals("col1"))
			size = ui.getLeftColumnSize();
		for(;row <size; row++){
			log.info("INFO: Perform action " + Widget_Action_Menu.MOVEDOWN.getMenuItemText() + " on widget " + widget.getTitle());
			log.info("Current location of widget "+widget.getTitle()+" is: " + row);
			ui.performCommWidgetAction(widget, Widget_Action_Menu.MOVEDOWN);
			Assert.assertEquals(ui.getWidgetLocation(widget), row+1, "ERROR: Failed to move down widget: " + widget);
		} 
		if(row == size)
			Assert.assertFalse(ui.checkWidgetMenuPresent(widget, Widget_Action_Menu.MOVEDOWN),"ERROR: Move down menu is still enabled when widget is the in the last row");
	}


	private void help(BaseWidget widget) {
		log.info("INFO: Click Help on widget "+ widget.getTitle());
		String OriginalWindow = driver.getWindowHandle();
		ui.performCommWidgetAction(widget, Widget_Action_Menu.HELP);
		String helpPageTitle = ui.readWidgetHelpPageTitle();
		Assert.assertTrue(helpPageTitle.contains(ui.getWidgetHelpTitle(widget)),"Help title is not correct on widget " + widget.getTitle());
		ui.close(cfg);
		driver.switchToWindowByHandle(OriginalWindow);
	}

	private void hide(BaseWidget widget) {
		log.info("INFO: Hide widget "+ widget.getTitle());
		ui.performCommWidgetAction(widget, Widget_Action_Menu.HIDE);

		log.info("INFO: Clicking Hide button on Hide Widget dialog");
		ui.waitForClickableElementWd(ui.createByFromSizzle(CommunitiesUIConstants.WidgetHideButton), 5);
		ui.clickLinkWait(CommunitiesUIConstants.WidgetHideButton);
				
		ui.waitForElementInvisibleWd(ui.createByFromSizzle(CommunitiesUIConstants.WidgetHideButton), 2);
	
		Assert.assertFalse(
				driver.isElementPresent(ui.getWidgetByTitle(widget)),
				"ERROR: Failed to hide widget " + widget.getTitle());

	}

	private void minimize(BaseWidget widget){
		log.info("INFO: Minimize widget "+ widget.getTitle());
		ui.performCommWidgetAction(widget, Widget_Action_Menu.MINIMIZE);

		Element menu = ui.getFirstVisibleElement(ui.getWidgetByTitle(widget));	
		String id = menu.getAttribute("id").substring(17);

		Element subarea = driver.getFirstElement("css=div[id='" + id +"SubArea" + "']");
		String style = subarea.getAttribute("style");

		Assert.assertEquals(style, "display: none;", "ERROR: Failed to minize widget "+ widget.getTitle());

	}

	private void maximize(BaseWidget widget) {
		ui.performCommWidgetAction(widget, Widget_Action_Menu.MINIMIZE);
		log.info("INFO: Minimize widget "+ widget.getTitle());
		ui.performCommWidgetAction(widget, Widget_Action_Menu.MAXIMIZE);

		Element menu = ui.getFirstVisibleElement(ui.getWidgetByTitle(widget));	
		String id = menu.getAttribute("id").substring(17);

		Element subarea = driver.getFirstElement("css=div[id='" + id +"SubArea" + "']");
		String style = subarea.getAttribute("style");

		Assert.assertEquals(style, "", "ERROR: Failed to maximize widget "+ widget.getTitle());
	}


	private void remove(BaseWidget widget) {

		Assert.assertTrue(
				driver.isElementPresent(ui.getWidgetByTitle(widget)),
				"ERROR: Widget is not existing on overview page: "
						+ widget.getTitle() + "");

		if (widget.equals(BaseWidget.SUBCOMMUNITIES) 
				| widget.equals(BaseWidget.GALLERY)
				| widget.equals(BaseWidget.MEMBERS)
				| widget.equals(BaseWidget.TAGS)
			    | widget.equals(BaseWidget.COMMUNITYDESCRIPTION)
			    | widget.equals(BaseWidget.IMPORTANTBOOKMARKS)) {
			ui.performCommWidgetAction(widget, Widget_Action_Menu.REMOVE);
			ui.removeWidget();
		} else {
		  ui.performCommWidgetAction(widget, Widget_Action_Menu.DELETE);
			ui.removeWidget(widget, testUser);
		}
		Assert.assertFalse(
				driver.isElementPresent(ui.getWidgetByTitle(widget)),
				"ERROR: Failed to remove widget " + widget.getTitle());
	}

}
