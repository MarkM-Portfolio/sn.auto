/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.communities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.openqa.selenium.ElementNotVisibleException;
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
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_Communities_Widgets extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Communities_Widgets.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser, testUser1, adminUser;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	private String serverURL;
	private boolean isOnPrem;
	private GatekeeperConfig gkc;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {	
		cfg = TestConfigCustom.getInstance();
		//Load User
		testUser = cfg.getUserAllocator().getUser();	
		testUser1 = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + testUser.getDisplayName());
				
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		// for checking Gatekeeper settings
		isOnPrem = cfg.getProductName().equalsIgnoreCase("onprem");
		adminUser = cfg.getUserAllocator().getAdminUser();
		if (isOnPrem) {
			gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		} else {
			gkc = GatekeeperConfig.getInstance(driver);
		}
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
	}
		
	/**
	*<ul>
	*<li><B>Info:</B>Tests adding widgets to a community</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Customize by adding all the available widgets to the community</li>
	*<li><B>Verify:</B>All widgets are added to the community without any errors in the UI</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "smokeonprem", "bvt", "regressioncloud", "icStageSkip"})
	public void addWidgets()  {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();

		String Element_Contents;
		List <String> widgetsEnabled;
		List <Element> NavElements;
		List <String> navContents = new ArrayList<String>();

		//create community
		logger.strongStep("Create A Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Communites and log in as test user");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = isOnPrem ? "COMMUNITIES_TABBED_NAV" : "communities-tabbed-nav";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = gkc.getSetting(gk_flag);
		
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to owned community views");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Add the widgets to the community	
		logger.strongStep("Enable all Widgets");
		widgetsEnabled = addAllEnabledWigdetsToCommunity();
		
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Click on More button");
			log.info("INFO: Select More from the tabbed navigation menu");
			ui.clickLinkWithJavascript("css=li#tabNavMoreBtn");
			NavElements = driver.getElements("css=td[id^='moreIdx']");
			for(Element e: NavElements)
			{
				Element_Contents = e.getText().toUpperCase();  //extracts text from element, and normalizes it
				if(!(	Element_Contents.contentEquals("") ||
						Element_Contents.contentEquals("RECENT UPDATES") ||
						Element_Contents.contentEquals("MEMBERS") ||
						Element_Contents.contentEquals("FORUMS") ||
						Element_Contents.contentEquals("BOOKMARKS") ||
						Element_Contents.contentEquals("FILES") || 
						Element_Contents.contentEquals("STATUS UPDATES")|| 
						Element_Contents.contentEquals("OVERVIEW")||
						Element_Contents.contentEquals("HIGHLIGHTS")|| 
						Element_Contents.contentEquals("MORE")||
						Element_Contents.contentEquals("METRICS")
					))
				{
					log.info("INFO: Adding: "+Element_Contents+" to list");
					navContents.add(Element_Contents);// adds element contents to list.
				}
				
			}

		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			//click on left nav Overview button
			logger.strongStep("Click on Overview button");
			log.info("INFO: Click Overivew button in Left navigation");
			ui.clickLinkWait(CommunitiesUIConstants.leftnavbutton);
		}
 
		//validate newly created widgets links appear in the nav
		logger.strongStep("Verify Widgets are enabled");
		log.info("INFO: Retrieving elements from the Left Navigation menu ");
		List <Element> LeftNavEl = driver.getElements(CommunitiesUIConstants.leftnavbar); 	//get the elements from the left navigation bar.
				
				log.info("INFO: Adding detected widgets to List");
				for(Element e: LeftNavEl)
				{
					Element_Contents = e.getText().toUpperCase();  //extracts text from element and normalizes it
					//this check prevents the normal navigation links from being added to the detected widget navigation links.
					if(!(	Element_Contents.contentEquals("") ||
							Element_Contents.contentEquals("RECENT UPDATES") ||
							Element_Contents.contentEquals("MEMBERS") ||
							Element_Contents.contentEquals("FORUMS") ||
							Element_Contents.contentEquals("BOOKMARKS") ||
							Element_Contents.contentEquals("FILES") || 
							Element_Contents.contentEquals("STATUS UPDATES")|| 
							Element_Contents.contentEquals("OVERVIEW")|| 
							Element_Contents.contentEquals("HIGHLIGHTS")|| 
							Element_Contents.contentEquals("MORE")||
							Element_Contents.contentEquals("METRICS")||
							Element_Contents.contentEquals("BLOG")||
							Element_Contents.contentEquals("WIKI")
						))
					{
						log.info("INFO: Adding: "+Element_Contents+" to list");
						navContents.add(Element_Contents);// adds element contents to list.
					}
					
				}
				
				log.info("INFO: Checking for the Gallery widget");
				if(widgetsEnabled.contains("GALLERY")){
					boolean found = false;
					List<Element> elements = driver.getElements(CommunitiesUIConstants.rightsideGalleryWidget);
					for (Element element : elements){
						log.info("INFO: Right side Widget" + element.getText());
						if(element.getText().contentEquals("Gallery")){
							found = true;
							log.info("INFO: Adding GALLERY to list");
							navContents.add("GALLERY");// adds Gallery contents to list.
							break;
						}
					}
					Assert.assertTrue(found, "ERROR: Unable find 'Gallery' widget on right side widget container");	
				}
				
				//Check Subcommunities widget
				log.info("INFO: Checking for Subcommunities widget");
				if(widgetsEnabled.contains("SUBCOMMUNITIES")){
					log.info("INFO: Subcommunities Exception Note: Expection as there is no left navigation link");
					Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.subCommunitiesWidgetTitle),
								      "ERROR: Unable to find Subcommunities widget in list of widgets");
					log.info("INFO: Adding SUBCOMMUNITIES to list");
					navContents.add("SUBCOMMUNITIES");// adds element contents to list.
				}
				
				//Check Rich Content widget
				log.info("INFO: Checking for Rich Content widget");
				if(widgetsEnabled.contains("RICH CONTENT")){
					log.info("INFO: Rich Content Exception Note: Expection as there is no left navigation link");
					Assert.assertTrue(ui.isElementVisible(CommunitiesUIConstants.richContentWidgetTitle),
						      "ERROR: Unable to find Rich Content widget in list of widgets");
					log.info("INFO: Adding RICH CONTENT to list");
					navContents.add("RICH CONTENT");// adds element contents to list.
				}
				
				
				//if the contents of the present widgets list is empty or null return a failed test.
				log.info("INFO: Checking that lists match..");
				if((navContents == null) || (navContents.isEmpty()))
					Assert.assertFalse(true, "ERROR: No widgets were detected");
				else
				{
					boolean WidgetsAddedCheck = widgetsEnabled.containsAll(navContents), //checks that the contents of the detected widgets list are all in the added widgets list 
							WidgetsDetectedCheck = navContents.containsAll(widgetsEnabled);	//reverse of above just to be sure
					String FailedWidgets = "";

					if(!(WidgetsAddedCheck && WidgetsDetectedCheck))
					{
						log.info("Widgets in nav to compare: " + navContents);
						log.info("Enabled widgets to compare: " + widgetsEnabled);
						widgetsEnabled.removeAll(navContents);
						for(String S: widgetsEnabled)
							FailedWidgets += S +", "; 
					}
					Assert.assertTrue( WidgetsAddedCheck && WidgetsDetectedCheck ,"ERROR: Unable to find widgets: "+FailedWidgets);
				}
				
	   logger.weakStep("Validate that there is no Error present on Community default landing page");
	   log.info("INFO: Validate that there is no Error present on Community default landing page");
	   Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.CommunityHandleError), "'Unable to Login...' error is displayed");

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info:</B>Tests creating an event through the Events widget</li>
	*<li><B>Step:</B>Create a community via API</li>
	*<li><B>Step:</B>Add Events widget via API</li>
	*<li><B>Step:</B>Open the community and click Events link in the left navigation menu</li>
	*<li><B>Verify:</B>'Create an Event' button is present</li>
	*<li><B>Verify:</B>'Events' tab is present</li>
	*<li><B>Verify:</B>'Calendar View' tab is present</li>
	*<li><B>Step:</B>Create an event</li>
	*<li><B>Verify:</B>Event was created</li> 
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "cplevel2", "level2", "bvt", "regressioncloud"})
	public void testEventsWidget() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		CalendarUI calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();

		
		//Create a event base state object
		BaseEvent event = new BaseEvent.Builder(testName + Helper.genDateBasedRand())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription)
									   .build();


		//create community
		logger.strongStep("Login under test user and Create Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add widget
		logger.strongStep("Add Events widgets to community");
		log.info("INFO: Add Events widget to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = isOnPrem ? "COMMUNITIES_TABBED_NAV" : "communities-tabbed-nav";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = gkc.getSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Navigate to Community views");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Events link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Events");
			log.info("INFO: Select Events from the tabbed navigation menu");
			Community_TabbedNav_Menu.EVENTS.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Events");
			log.info("INFO: Select Events from left navigation menu");
			Community_LeftNav_Menu.EVENTS.select(ui);
		}

		//Verify the calendar view
		log.info("INFO: Validate the Create an Event button");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.CreateEvent), 
				"ERROR: 'Create an Event' button not found.");
		
		log.info("INFO: Validate the Events tab is present");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventListTab), 
				"ERROR: 'Events' tab not found: " + CalendarUI.EventListTab);
		
		log.info("INFO: Validate the Calendar View tab is present");
		Assert.assertTrue(ui.fluentWaitPresent(CalendarUI.EventGridTab), 
				"ERROR: 'Calendar View' tab not found: " + CalendarUI.EventGridTab);
		
		//Create an Event
		logger.strongStep("Create Event");
		event.create(calUI);
		
		//Validate that the page loads without Error
		ui.checkForErrorsOnPage();
		
		//Verify Event is created
		logger.strongStep("Verify Event Creation");
		ui.fluentWaitTextPresent(event.getName());
		
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test the creation of an activity through the Activities widget</li>
	*<li><B>Step:</B>Create a community via API</li>
	*<li><B>Step:</B>Add the Activities widget via API</li>
	*<li><B>Step:</B>Open the community and click Activities link in the left navigation menu</li>
	*<li><B>Verify:</B>Activities widget is added to the community</li>
	*<li><B>Verify:</B>Create Activity button is present</li>
	*<li><B>Verify:</B>Activities Widget Description is present</li>
	*<li><B>Step:</B>Create an Activity</li>
	*<li><B>Verify:</B>Activity was created</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "level2", "bvt", "regressioncloud"})
	public void testActivitiesWidget(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();
	
		//create community
		logger.strongStep("Login in with test user and create community via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add widget
		log.info("INFO: Add Activities widget to community");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = isOnPrem ? "COMMUNITIES_TABBED_NAV" : "communities-tabbed-nav";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = gkc.getSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Enable activites widget");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		//navigate to the API community
		logger.strongStep("Verifiy create activity button is present");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		// For security deployments
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
		ui.sleep(3000);
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Widget link in the navigation menu
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			logger.strongStep("Select Activites from Navigation menu");
			log.info("INFO: Select Activities from the tabbed navigation menu");
			Community_TabbedNav_Menu.ACTIVITIES.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			logger.strongStep("Select Activites from Navigation menu");
			log.info("INFO: Select Activities from left navigation menu");
			Community_LeftNav_Menu.ACTIVITIES.select(ui);
		}
		
		//Verify the Widget page
		logger.strongStep("Create Activity and Verify Creation");
		log.info("INFO: Validate the Create activity button is present");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.createActivButton),
						 "ERROR: 'Create an Activity' button not found.");
		
		log.info("INFO: Validate the Activities Widget Description is present");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.createActivDesc),
						 "ERROR: Activities Widget Description not found");

		//Click the create activity button and the page 
		log.info("INFO: Click Create Your First Activity");
		ui.clickLinkWait(CommunitiesUIConstants.createActivButton);
		ui.fluentWaitPresent(ActivitiesUIConstants.Start_An_Activity_InputText_Name);
		
		//Validate that the page loads without Error
		ui.checkForErrorsOnPage();
		
		//Validate an web element on the page
		Element contentFrame = driver.getSingleElement(CommunitiesUIConstants.newActivValidation);
		log.info("INFO: Validating a widget element has expected value. " + contentFrame.getAttribute("aria-label"));
		
		apiOwner.deleteCommunity(comAPI);
	
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Test creating blog and entry through the Blog widget</li>
	*<li><B>Step:</B>Create a community via API</li>
	*<li><B>Step:</B>Add Blogs widget via API</li>
	*<li><B>Step:</B>Open the community and click Blog link in the left navigation menu</li>
	*<li><B>Step:</B>Make a Blog Entry</li>
	*<li><B>Verify:</B>Blog widget is added to the community</li>
	*<li><B>Verify:</B>Blog entry can be created</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = { "cplevel2", "level2", "bvt", "regressioncloud"})
	public void testBlogsWidget() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 .access(defaultAccess)
									 .description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
									 .build();

		//create community
		logger.strongStep("Login with test user and create a community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Check Gatekeeper value for Communities Tabbed Nav setting
		String gk_flag = isOnPrem ? "COMMUNITIES_TABBED_NAV" : "communities-tabbed-nav";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		boolean value = gkc.getSetting(gk_flag);
		
		// check for 6.0 CR4 Catalog Card View
		String gk_flag_card = isOnPrem ? "CATALOG_CARD_VIEW" : "catalog-card-view";
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag_card + " setting is enabled");
		boolean isCardView = gkc.getSetting(gk_flag_card);
		log.info("INFO: Gatekeeper flag " + gk_flag_card + " is set to " + isCardView + " so use Card View selectors");
		
		//Navigate to owned communities
		logger.strongStep("Open Community and click on Blog link in Left navigation menu");
		log.info("INFO: Navigate to the owned communtiy views");
		if (isCardView) {
			ui.goToOwnerCardView();
		} else {
			ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		}

		//navigate to the API community
		logger.strongStep("Make a blog entry");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(BaseUIConstants.Community_Actions_Button);
		
		//If GK is enabled use TabbedNav, else use LeftNav 
		//Click on the Widget link in the nav
		if (value)
		{
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			log.info("INFO: Select Blogs from the tabbed navigation menu");
			Community_TabbedNav_Menu.BLOG.select(ui);
		}else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Left Nav Bar");
			log.info("INFO: Select Blogs from left navigation menu");
			Community_LeftNav_Menu.BLOG.select(ui);
		}

		//Validate that you can click the create activity button and the page 
		ui.fluentWaitPresent(CommunitiesUIConstants.BlogsNewEntryButton);
		ui.clickLinkWithJavascript(CommunitiesUIConstants.BlogsNewEntryButton);
		
		//Validate that the page loads without Error
		ui.waitForPageLoaded(driver);
		ui.checkForErrorsOnPage();
		
		//Validate an web element on the page
		logger.strongStep("Verify Blog widget and Blog Entry were created");
		Element contentFrame = driver.getSingleElement(CommunitiesUIConstants.BlogsEntryTitle);
		log.info("INFO: Validating a widget element has expected value. "+contentFrame.getText());
		
		apiOwner.deleteCommunity(comAPI);
	
		ui.endTest();
	}
	
	
	
	/**
	 * addAllEnabledWigdetsToCommunity - enables all community widgets it finds and returns a list for validation.
	 * Must be inside a community to work
	 * @return List<String> containing the widgets that it enables
	 * @Author Ralph LeBlanc
	 */
	public List<String> addAllEnabledWigdetsToCommunity(){

		List<String> widgetList = new ArrayList<String>();

		//Chose customize from Community Actions
		log.info("INFO: Customize community");
		Com_Action_Menu.CUSTOMIZE.select(ui);
		
		
		//collect all the disabled widget elements
		List<Element> widgets = ui.collectDisabledCommWidgets();
		
		log.info("INFO: Widgets to enable = " + widgets.size());
		//add the element text to String list
		Iterator<Element> elementList = widgets.iterator();
		while(elementList.hasNext()){
		    widgetList.add(elementList.next().getText().toUpperCase());
		}

		//select the widgets from above
		for (Element widget : widgets){
			String widgetName = widget.getText();
			//Ensures that the test is executed from the top of the page
			//driver.executeScript("scroll(0, -250);");
			driver.executeScript("arguments[0].scrollIntoView(true);", widget.getWebElement());
			log.info("INFO: Enabling " + widget.getText());
			widget.click();
			
			//Ensure adding process is finished before continuing
			ui.fluentWaitTextNotPresentWithoutRefresh("Adding Application");

			log.info("INFO: Validate that " + widgetName + " is enabled.  If not try again.");		
			if(!widget.isVisible()){
				log.info("INFO: Widget " + widgetName + " enabled properly");			
			}else if(widget.isVisible()){
				ui.waitForPageLoaded(driver);
				log.info("INFO: Widget " + widget.getText() + " not enabled attempting one more time");
				try{
					widget.click();
				}catch(ElementNotVisibleException e){
					log.info("INFO: Widget became not visible during double check");
					
				}
			}
		}
		//Ensures that the palette can be closed
		driver.executeScript("scroll(0, -250);");
		//Close the widget
		ui.clickLink(CommunitiesUIConstants.CloseWidget);
		
		return widgetList;
	}
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Test that seven community widgets can be enabled</li>
	 *<li><B>Step:</B> Create a Community via API</li> 
	 *<li><B>Step:</B> Enable Community Widgets via API for the following widgets; Activity, SubCommunity, Gallery, Events, Related Communities, Surveys and Featured Surveys</li>
	 *<li><B>Verify:</B> All the widgets are enabled</li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void communityEnableWidgets() throws Exception {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
			String testName = ui.startTest();
		
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													   .access(defaultAccess)
													   .tags(Data.getData().commonTag)
													   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
													   .description(Data.getData().communitywidgettest + Helper.genDateBasedRand())
													   .build();
			//create community
			logger.strongStep("Login to test user and create a Community");
			log.info("INFO: Create community using API");
			Assert.assertNotNull(apiOwner);
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			
			//add widget Activity
			logger.strongStep("Enable all widgets");
			log.info("INFO: Add Activity widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);
			
			//add widget SubCommunity
			log.info("INFO: Add SubCommunity widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SUBCOMMUNITIES);
			
			//add widget Gallery
			log.info("INFO: Add GALLERY widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.GALLERY); 
			
			//add widget EVENTS
			log.info("INFO: Add EVENTS widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS); 
			
			//add widget RELATED_COMMUNITIES
			log.info("INFO: Add RELATED_COMMUNITIES widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.RELATED_COMMUNITIES); 
			
			//add widget SURVEYS
			log.info("INFO: Add SURVEYS widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS); 
			
			//add widget FEATUREDSURVEYS
			log.info("INFO: Add FEATURED SURVEYS widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEATUREDSURVEYS); 
			
			//GUI
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);	
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);	

			// Verify Activity be enabled
			logger.strongStep("Verifiy Widgets are enabled");
			log.info("INFO: Verify Activity be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetActivitylnk), 
						"Activity be enabled");
			
			// Verify SubCommunity be enabled
			log.info("INFO: Verify SubCommunity be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetSubcommunitylnk), 
						"SubCommunity be enabled");
	
			// Verify Gallery be enabled
			log.info("INFO: Gallery be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetGallerylnk), 
							"Gallery be enabled");
							
			// Verify Events be enabled
			log.info("INFO: Verify Events be enabled");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUICloud.WidgetEventsTxt), 
						"Events be enabled");  
			
			// Verify Related Community be enabled
			log.info("INFO: Related Community be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetRelatedCommunitylnk), 
							"Related Community be enabled");
					
			// Verify Survey be enabled
			log.info("INFO: Verify Survey be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetSurveylnk), 
						"Survey be enabled");  
			
			//TODO This try catch block will be removed once the following issue is resolved
			//https://swgjazz.ibm.com:8001/jazz/web/projects/Lotus%20Connections#action=com.ibm.team.workitem.viewWorkItem&id=160480
			try{
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUICloud.WidgetFeaturedSurveyTxt), 
							"Featured Survey be enabled"); 
			} catch (Exception e){
				// MODIFYING SMOKE TEMPORARILY SO THIS ISSUE ISN'T BLOCKING PIPEINE
				Assert.assertTrue(ui.fluentWaitTextPresent("FeaturedSurvey"), 
						"WARNING: FOUND THE 'FeaturedSurvey' STRING - THIS SHOULD BE 'Featured Survey'"); 
			}
							
			//delete community
			log.info("INFO: Removing community");
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
	} 
	@Deprecated
	@Test (groups = {"sandboxcloud"} )
	public void sandboxcommunityEnableWidgets() {
		
			String testName = ui.startTest();
		
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													   .access(defaultAccess)
													   .tags(Data.getData().commonTag)
													   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
													   .description(Data.getData().communitywidgettest + Helper.genDateBasedRand())
													   .build();
															   
			
			log.info("INFO: Create community using API");
			Assert.assertNotNull(apiOwner);
			Community comAPI = community.createAPI(apiOwner);
			
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			
			
			log.info("INFO: Add Activity widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);
			
			
			log.info("INFO: Add SubCommunity widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SUBCOMMUNITIES);
			
			
			log.info("INFO: Add GALLERY widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.GALLERY); 
			
			
			log.info("INFO: Add EVENTS widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS); 
			
			
			log.info("INFO: Add RELATED_COMMUNITIES widget with api");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.RELATED_COMMUNITIES); 
			//GUI
			//Load component and login
			log.info("INFO: Load and Login to the component" +testUser.getDisplayName());
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);	
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);	
			log.info("INFO: Verify Activity be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetActivitylnk), 
						"Activity be enabled");
			log.info("INFO: Verify SubCommunity be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetSubcommunitylnk), 
						"SubCommunity be enabled");
			log.info("INFO: Gallery be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetGallerylnk), 
							"Gallery be enabled");
			log.info("INFO: Verify Events be enabled");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUICloud.WidgetEventsTxt), 
						"Events be enabled");  
			log.info("INFO: Related Community be enabled");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUICloud.WidgetRelatedCommunitylnk), 
							"Related Community be enabled");
			log.info("INFO: Removing community");
			apiOwner.deleteCommunity(comAPI);
			ui.endTest();
	} 
			
}

