package com.ibm.conn.auto.tests.communities.regression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class GeneralWidget extends SetUpMethods2 {
	
	protected static Logger log = LoggerFactory.getLogger(GeneralWidget.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private HomepageUI hUI;
	private User testUser, testUser1;
	private APICommunitiesHandler apiOwner;
	private User adminUser;
	private GlobalsearchUI gsui;
	private SearchAdminService adminService;
    private String serverURL;
	private BlogsUI bUI;
	private FeedsUI fUI;
	
	/**
	 * PTC_VerifyAddAppsPaletteAndAddingAllApps
	 * PTC_VerifyHideAndReAddWidget
	 * PTC_VerifyRecentUpdateWidget
	 * PTC_VerifyCreationEditAndDeleteBookmark
	 */
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		adminUser=cfg.getUserAllocator().getAdminUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());		
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setup(){
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		fUI = FeedsUI.getGui(cfg.getProductName(), driver);
		gsui = GlobalsearchUI.getGui(cfg.getProductName(), driver);

	}


	/**
	 *<li><B>Test Scenario: Widgets part1: Verify Widget Palette & adding all widgets (1 of 4) </B></li> 
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step:  Create a Public community </li>
	 *<li> Verify: Check that in community by default following wizards are present : (1)Overview (2) Recent Updates (3) Status updates (4)Member (5) Forums (6) Bookmark (7) Files</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E8B8C2DBF7C87C185257C8D005A0CA4">TTT-WIDGETS PART1: VERIFY WIDGET PALETTE & ADDING ALL WIDGETS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testDefaultAppsInCommunity(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Widgets for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
				
			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
				log.info("INFO: Verify the Overview tab appears on the top nav");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
						"Error: Overview tab does not appear on the top nav");

			}else {
				log.info("INFO: Verify the Overview link appears on the left nav");
				ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverviewButton);
				driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();
				
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.leftNavOverviewLink),
				"ERROR: Overview link does not appear on the left nav");
			}
			
			if(ui.checkGKSetting(Data.getData().commTabbedNav)){
				log.info("INFO: Verify presence of all default widgets on the top nav");
				Assert.assertTrue(ui.presenceOfDefaultWidgetsOnTopNav(),
						"ERROR: Presence of all the default widgets on the top nav is not correct");

			}else {
				log.info("INFO: Verify presence of all default widgets in Community card");
				Assert.assertTrue(ui.presenceOfDefaultWidgetsForCommunity(),
						"ERROR: Presence of all the default widgets in community card is not correct");
			}
			
			log.info("INFO: Cleanup - Removing community for Test case " + testName );
			//community.delete(ui, testUser);
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
			
			
	}
	
	/**
	 *<li><B>Test Scenario: Widgets part1: Verify Widget Palette & adding all widgets (2 of 4) </B></li> 
	 * Verification of Widgets
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step:  Create a Public community, go to it & then click on Add Apps  </li>
	 *<li> Verify: Check that in Add Apps list following are present : Blog,Ideation Blog,Activites,Wiki,Subcommunities,Feeds,Gallery,Events,Related Communities</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E8B8C2DBF7C87C185257C8D005A0CA4">TTT-WIDGETS PART1: VERIFY WIDGET PALETTE & ADDING ALL WIDGETS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void testAddOnAppsInCommunity(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Widgets for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);			
			ui.login(testUser);
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Select Add Apps on the community action menu");
			Com_Action_Menu.ADDAPP.select(ui);
			ui.waitForPageLoaded(driver);
			
			log.info("INFO: Verify following widgets are present in Add Apps palette");
			Assert.assertTrue(ui.presenceOfWidgetsInAddAppsPalette(),
						"ERROR: All the widgets are not present in Add Apps palette");
			
			log.info("INFO: Closing the Add Apps dialog");
			driver.getSingleElement(CommunitiesUIConstants.WidgetSectionClose).click();
			
			log.info("INFO: Cleanup - Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
			
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Widgets part1: Verify Widget Palette & adding all widgets (Test 3 of 4)</li>
	 *<li><B>Info:</B> The application count increases as each application gets added</li>
	 *<li><B>Info:</B> The applications to be added in this scenario are: Blogs,Ideation Blog,Activities,Wiki,Subcommunity,Feeds,Gallery,Events,Related Communities</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Open the widget palette:  Community Actions -> Add Apps</li>
	 *<li><B>Verify:</B> The default application count is 0</li>
	 *<li><B>Step:</B> Add an application to the community</li> 
	 *<li><B>Verify:</B> The application added count is increased by 1</li>
	 *<li><B>Step:</B> Add the next application & verify that the application count is increased by 1</li>
	 *<li><B>Step:</B> Repeat the add & verify steps until all the widgets listed above have been added</li>
	 *<li><B>Step:</B> Click on the X icon (upper right)</li>
	 *<li><B>Verify:</B> Verify that the widget palette closes, and the user is returned to the Overview page of the community. </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E8B8C2DBF7C87C185257C8D005A0CA4">TTT-WIDGETS PART1: VERIFY WIDGET PALETTE & ADDING ALL WIDGETS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void addOnAppsInCommunityCountIncreases(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			int widgetCount = 0;
			String widgetName;
			
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Widgets for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//Create community using API
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//Get the community UUID
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load communities and login as the community creator
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			//Navigate to the community using the UUID
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);			
			
			//Click on Community Actions link
			log.info("INFO: Click on the Community Actions link");				
			Com_Action_Menu.ADDAPP.open(ui);
			
			//Click on the Add Apps link to bring up the widget palette
			log.info("INFO: From the Community Actions drop-down menu click on Add Apps");
			ui.clickLinkWait(CommunitiesUIConstants.CommunityAddAppsLink);
								
			//collect all the disabled widget elements
			List<Element> widgets = ui.collectDisabledCommWidgets();
			
			//select the widgets from above
			for (Element widget : widgets){

				widgetName=widget.getText();
				
				//When Featured Survey is added the 'Add Surveys Widget' pop-up displays. Clicking OK automatically
				//adds Surveys. Surveys can only be added once.  Since it has already been added,
				//Surveys will no longer display as a link & can no longer be selected.
				if(widgetName.equals(""))
					continue;
				
				log.info("INFO: Enabling " + widgetName);
				
				//To skip adding Library
				if (widgetName.contains("Library"))
					continue;
				
				//Click on widget
				widget.click();
				ui.waitForPageLoaded(driver);
				
				log.info("INFO: Validate that " + widgetName + " is enabled.  If not try again.");		
				if(!widget.isVisible()){
					log.info("INFO: Widget " + widgetName + " enabled properly");
				}else if(widget.isVisible()){
					ui.waitForPageLoaded(driver);
					log.info("INFO: Widget " + widgetName + " not enabled attempting one more time");
					try{
						widget.click();
						ui.waitForPageLoaded(driver);
					}catch(ElementNotVisibleException e){
						log.info("INFO: Widget became not visible during double check");
					}
				} 
				widgetCount++;
				
				//Click on OK button on the 'Add Surveys Widget' pop-up dialog
				if(widgetName.contains("Featured Survey") && ui.fluentWaitPresent(CommunitiesUIConstants.RevokeInviteOK)){
					ui.clickLinkWait(CommunitiesUIConstants.RevokeInviteOK);
					
				//Add 1 to the widget count because Surveys is automatically added when OK button is clicked
					widgetCount++;
				}
				
				log.info("INFO: Verify the application count has increased by 1");
				ui.fluentWaitTextPresent(widgetCount + " application");
				if(widgetCount == 1)
					Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.AddAppsCountAfterAdditonofApps).getText(), widgetCount + " application added",
							"ERROR: The # of applications added is incorrect");
				else
					Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.AddAppsCountAfterAdditonofApps).getText(), widgetCount + " applications added",
							"ERROR: The # of applications added is incorrect");
			}
			
			//Close the application palette
			log.info("INFO: Close the application palette");
			ui.clickLinkWait(CommunitiesUIConstants.WidgetSectionClose);
			
			//Verify the user is returned to Overview page
			log.info("INFO: Verify the user is returned to Overview page of the community");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverviewButton),
								"ERROR: User is not in Overview page of the community");
			
			//Cleanup: Delete the community created in this test case
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
	}

	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Widgets part1: Verify Widget Palette & adding all widgets (Test 4 of 4)</li>
	 *<li><B>Info:</B> The widgets are added appropriately in Overview Page & in left nav(Community card)</li>
	 *<li><B>Info:</B> The applications to be added in this scenario are: Blogs,Ideation Blog,Activities,Wiki,Subcommunity,Feeds,Gallery,Events,Related Communities</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Add widgets to community</li>
	 *<li><B>Verify:</B> Verify the following widgets are added to the middle column in the Overview page, and a link is added to the left nav (community card): Files, Wikis, Forums, Subcommunities, Bookmarks, Activities, Blogs, & IdeationBlog, Feeds</li>
	 *<li><B>Verify:</B> Verify the following widgets are added to the right column in the Overview page:  Events, Gallery, and Related Communities</li> 
	 *<li><B>Verify:</B> Verify no errors appear in the widgets by validating the default text of widgets</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3E8B8C2DBF7C87C185257C8D005A0CA4">TTT-WIDGETS PART1: VERIFY WIDGET PALETTE & ADDING ALL WIDGETS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void displayOfAppsInOverviewPage(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			List<String> widgetList = new ArrayList<String>();
			boolean widgetCheck = false;
			
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Widgets for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//Create community using API
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//Get the community UUID
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
			
			//Load communities and login as the community creator
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			//Navigate to the community using the UUID
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Choose Add Apps from Community Actions
			log.info("INFO: Add Apps community");
			Com_Action_Menu.ADDAPP.select(ui);
			
			//collect all the disabled widget elements
			List<Element> widgets = ui.collectDisabledCommWidgets();

			//Add the widget names to String list
			log.info("INFO: Widgets enabled " + widgets.size());
			Iterator<Element> elementList = widgets.iterator();
			while(elementList.hasNext()){	
			    widgetList.add(elementList.next().getText());
			}
			
			//Remove Library from widgetList
			log.info("Remove Library widget as not part of our testing");
			widgetList.remove("Library");
			
			//Add all the enabled widgets to community & verify them in left Nav. community card
			log.info("INFO: Add all widgets to community page and Verify they are present in left Nav. community card");
			try {
				ui.addAllWidgets();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//Verify the following widgets are added to the middle or right column in the Overview page 
			for (String widget : widgetList){
				log.info("INFO: Checking middle or right column for" + widget);
				//Widgets on right column
				if(widget.equals("Events") || widget.equals("Gallery") || widget.equals("Related Communities") || widget.equals("Featured Survey")) {
					log.info("Verify widget on right column" + widget);
					List<Element> rightColumnWidget = driver.getElements(CommunitiesUIConstants.rightColumnWidgets);
					if (widget.equals("Events"))
						widget="Upcoming Events";
					
					log.info("INFO: size of right column area " + rightColumnWidget.size());
					for (Element rwidget : rightColumnWidget) {
						String widgetId = rwidget.getAttribute("widgetid");
						log.info("INFO: widget id : " + widgetId);
						log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
						if (driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(widget)) {
							widgetCheck = true;
							break;
						}
					}
					
					log.info("INFO: Verify if Widget is found in the right column " + widget);
					Assert.assertTrue(widgetCheck,
										"ERROR: " + widget + "is not found in the right column");
				} else {
					//Widgets on middle column
					log.info("Verify widget on middle column" + widget);
					List<Element> middleColumnWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets);
					
					log.info("INFO: size of center content area " + middleColumnWidgets.size());
					for(Element ewidget : middleColumnWidgets){			
						String widgetId = ewidget.getAttribute("widgetid");
						log.info("INFO: widget id : " + widgetId);
						log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
						if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(widget)){
							widgetCheck = true;
							break;
						}
					}
					
					log.info("INFO: Verify if Widget is found in the middle column " + widget);
					Assert.assertTrue(widgetCheck,
										"ERROR: " + widget + "is not found in the middle column");
				}
			} 
			
			//Verify all the widgets added are present in overview page without any errors
			log.info("INFO: Verify all the widgets added are present in overview page without any errors");
			Assert.assertTrue(ui.presenceOfWidgetsInOverviewPage(),
								"ERROR: The widgets which are present in Overview page has some errors");
			
			//Cleanup: Delete the community created in this test case
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
	}
	
	/**
	 *<li><B>Test Scenario: Widgets part2: Verify Widget Palette - Hide & Re-add widget</B></li> 
	 * Verification of Adding & then hiding of Widgets
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: </B> Create a Public community, Add Blog from Add Apps , Add a blog entry, then Hide the Blog entry , then go to Add apps & readd the Blog entry </li>
	 *<li><B>Verify:</B> After adding the blog it is getting listed  </li>
	 *<li><B>Verify: </B>After clicking on hide for Blog it goes off</li>
	 *<li><B>Step:</B> Go to Add Apps > Hidden & re-add the Blog </li>
	 *<li><B>Verify:</B> After re-adding the blog appears again</li>
	 *<li><B>Verify:</B> Verify that the data in the Blogs widget still appears after it was re-added.</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/403A9DFA39170F5F85257E120072FD75/8F277D675AEF04AF85257C8D005E2C14">TTT-WIDGETS PART2: VERIFY WIDGET PALETTE - HIDE & RE-ADD A WIDGET</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testDefaultAppsInCommunityHide(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Widgets for community " + testName)
														.build();
			
			BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + rndNum)
													 .tags(Data.getData().commonAddress )
													 .content("Test Blog Hide & Re-add widget" + testName)
													 .build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);

			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);

			/*log.info("INFO: Adding the " + BaseWidget.BLOG.getTitle() +
					" widget to community " + community.getName() + " using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);*/

			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);

			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
				log.info("INFO: Verify Blog tab appears on the top nav");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.tabbedNavBlogTab),
						"Error: Blog tab does not appear on the top nav");

				log.info("INFO: Click on the Blog tab");
				Community_TabbedNav_Menu.BLOG .select(ui);

			}else {
				log.info("INFO: Hover over the Overview button on the left nav");
				ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverviewButton);
				driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

				log.info("INFO: Testing after Blog addition, it appears on the left navigation");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddAppsBlogs),
						"Error: Blog link is not present after adding the Blog via Add Apps ");					

				log.info("INFO: Open Blog widget from Left Nav. community card");
				ui.clickLinkWait(CommunitiesUIConstants.AddAppsBlogs);

			}	
			log.info("INFO: Select New Entry button");
			ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
			
			log.info("INFO: Add a new entry to the blog");
			blogEntry.create(bUI);
			
			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
				log.info("INFO: Click on the Overview tab");
				Community_LeftNav_Menu.OVERVIEW.select(ui);
				
			}else {			
				log.info("INFO: Open Overview menu from Left Nav. community card");
				ui.clickLinkWait(CommunitiesUIConstants.OpenOverViewMenu);
				
				log.info("INFO: Click on the Overview link");
				ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);

			}	
			log.info("INFO: Verify Blog entry is found in Blogs Widget");
			Assert.assertTrue(ui.fluentWaitPresent("link=" + blogEntry.getTitle()),
					"ERROR: " + blogEntry.getTitle() + "is not present in Blogs Widget");

			log.info("INFO: Hide the Blog widget which was added from Add Apps ");
			ui.performCommWidgetAction(BaseWidget.BLOG,Widget_Action_Menu.HIDE );

			log.info("INFO: Verify the pop-up dialog box with the following text");
			Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.ContentInDialogBox).getText().toLowerCase().contains(Data.getData().WidgetHideMsg.toLowerCase()),
					"ERROR: Hide application pop-up dialog message is in-correct");

			log.info("INFO: Click the Hide widget confirmation button");
			ui.clickLinkWait(CommunitiesUIConstants.WidgetHideButton);
						
			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
				log.info("INFO: Verify the Blog tab DOES appear on the top nav even though the app was hidden");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.tabbedNavBlogTab),
						"Error: Blog tab does not appear on the top nav");
				
			}else {	
				log.info("INFO: Hover over the Overview button on the left nav");
				ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverviewButton);
				driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();
				
				log.info("INFO: Verify the Blog link DOES appear on the left nav even though the app was hidden");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddAppsBlogs),
						"Error: Blog link does not appear on the left nav ");					
				
			}
			
			log.info("INFO: Verify that the Blog summary widget no longer appears in the Community's Overview page");
			Assert.assertEquals(ui.getWidgetLocationInfo(BaseWidget.BLOG, true), 0,
					"ERROR: Blog summary widget appears in the Community's Overview page");
			
			log.info("INFO: Select Add Apps from the community action menu");
			Com_Action_Menu.ADDAPP.select(ui);

			log.info("INFO: Verify that now there is a new link on the left side of the palette Hidden");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.HiddenAppsLink),
					"ERROR: Hidden link on the left side of the palette does not appear");
			
			log.info("INFO: Click on the Hidden link");
			ui.clickLinkWait(CommunitiesUIConstants.HiddenAppsLink);
			
			log.info("INFO: Verify that Blog appears as a hidden app");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.AddAppsBlogs),
					"ERROR: Blog do not appear under Hidden link");
			
			log.info("INFO: Add Blogs from Hidden Apps link");
			ui.clickLinkWait(CommunitiesUIConstants.AddAppsBlogs);
			
			log.info("INFO: Verify that the button changes to Blog added");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().BlogAddedTitle),
					"ERROR: After clicking on Blog button does not change to Blog added");
			
			log.info("INFO: Click button to close the palette");
			ui.fluentWaitPresent(CommunitiesUIConstants.WidgetSectionClose);
			driver.getSingleElement(CommunitiesUIConstants.WidgetSectionClose).click();
			
			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
				log.info("INFO: Verify the user is on the Overview page");
				Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.communityOverviewPageURL),
						"Error: User is not on the Overview page");
				
			}else {
				log.info("INFO: Verify the user is on the Overview page");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
						"ERROR: User is not on the Overview page");
				
			}			
			
			log.info("INFO: verify that the Blog widget has been re-added to the bottom of the left column");
			Assert.assertEquals(ui.getWidgetLocationInfo(BaseWidget.BLOG, true), 1,
					"ERROR: Blog widget is not re-added to the bottom of the middle column");

			log.info("INFO: Verify that the data in the Blogs widget still appears after it was re-added");
			Assert.assertTrue(ui.fluentWaitPresent("link=" + blogEntry.getTitle()),
					"ERROR: " + blogEntry.getTitle() + "is not present in Blogs Widget after it was re-added");

			log.info("INFO: Cleanup - Removing community for Test case " + testName );
			//community.delete(ui, testUser);
			apiOwner.deleteCommunity(comAPI);

			ui.endTest();

	}
	
	/**
	 *<li><B>Test Scenario: Widgets part3: Delete a widget</B></li> 
	 * Verification of Deletion of Widgets
	 *<ul>
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a Public community, Add Blog from Add Apps , then remove the Blog  </li>
	 *<li><B>Verify: After adding the Added App ( Blog in this case ) is deleted from the Community view  </li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testDeleteBlogAppInCommunity(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Start Page for community " + testName)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);

			/*//Add Blog widget
			log.info("INFO: Adding the " + BaseWidget.BLOG.getTitle() +
					" widget to community " + community.getName() + " using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);*/
			
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			
			//Login as Owner
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Remove the Blog Widget from community
			log.info("INFO: Starting to remove the added Widget from the community");
			ui.performCommWidgetAction(BaseWidget.BLOG,Widget_Action_Menu.DELETE);
			
			log.info("INFO: Deleting the blog");
			driver.getSingleElement(CommunitiesUIConstants.commDeleteName).type("Blog");
			driver.getSingleElement(CommunitiesUIConstants.commDeleteUser).type(testUser.getDisplayName());
			
			//Remove the Blog from Community
			log.info("INFO: Delte the added widget");
			ui.clickLinkWait(CommunitiesUIConstants.commDeleteButton);
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Test that Blog is not deleted from the Community view
			log.info("INFO: Test after deletion the added widget should not appear");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.AddAppsBlogs),"Error: After deletion Blog link is still present");
			
			//Delete the community created for this test.
			log.info("INFO: Removing community for Test case " + testName );
			//community.delete(ui, testUser);
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
			
			
		}

	/**
	 * Recent Updates
	 *<ul>
	 *<li><B>Test Scenario:</B> RECENT UPDATES WIDGET: VERIFY THE RECENT UPDATE WIDGET (1 of 3)</li>
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: </B>Create a Public community Then go to recent update to check the details about the community creation  </li>
	 *<li><B>Verify: </B>Recent updates contain two updates for the newly created community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7D246CC8B3439FFB85257CAD0066140E">TTT-ON-PREM: RECENT UPDATES WIDGET: VERIFY THE RECENT UPDATE WIDGET</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testPostingAMessgaeInRecentUpdates(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
				
		//create community
		log.info("INFO: Create community using API");
					
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Recent updates for community " + testName)
													.build();
							
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		// Go to recent updates
		log.info("INFO: Click on Recent Updates link on the left nav");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		//Test Community Forum creation in recent updates
		log.info("INFO: Verify in Recent Updates the message for forum creation");
		Assert.assertTrue(hUI.findNewsItem(testUser.getDisplayName()+" created the "+community.getName()+" forum.").isVisible(),
							"Error: In Recent Updates the message for forum creation is wrong");
		
		// Test Community description is coming in Recent updates	
		log.info("INFO: Verify in Recent Updates posted message");
		Assert.assertTrue(hUI.findNewsItem(community.getDescription()).isVisible(),
							"Error: Posted message is not coming in Recent updates");
		
		//Test Name of the <owner> + created the community message is coming in Recent updates
		log.info("INFO: Verify name of the community owner");
		Assert.assertTrue(hUI.findNewsItem(testUser.getDisplayName()+ " created the community.").isVisible(),
							"Error: Name of the community owner is not coming properly");

		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
		
		ui.endTest();
		
	}
	
	/**
	 * Recent Updates
	 *<ul>
	 *<li><B>Test Scenario:</B> RECENT UPDATES WIDGET: VERIFY THE RECENT UPDATE WIDGET (2 of 3)</li>
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: </B>Create a Public community & add an App say BLOG & Ideation Blog, Then go to recent update to check the details about the Blog & Ideation Blog</li>
	 *<li><B>Verify: </B>Recent updates contain the details about the Blog & Ideation Blog added </li>
	 *<li><B>Step: </B>Click on the chevron icon in the entry that shows the community Blog was created</li>
	 *<li><B>Verify: </B>The EE (Embedded Experience gadget/dialog) opens for this entry and it contains information regarding the community Blog being created.</li>
	 *<li><B>Step:</B> Close EE (Embedded Experience gadget/dialog) & Delete the community created by user</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7D246CC8B3439FFB85257CAD0066140E">TTT-ON-PREM: RECENT UPDATES WIDGET: VERIFY THE RECENT UPDATE WIDGET</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testRecentUpdatesAfterAddingBlog(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
	
		//create community
		log.info("INFO: Create community using API");
					
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Recent Updates for community " + testName)
													.build();
							
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		/*//Add Blog widget
		log.info("INFO: Adding the " + BaseWidget.BLOG.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);*/
		
		//Add Ideation Blog widget
		log.info("INFO: Adding the " + BaseWidget.IDEATION_BLOG.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.IDEATION_BLOG);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		/*//Verify that Blogs is added to the community & now appear on the Overview page.
		log.info("INFO: Verify that Blogs widgets is added to the community & now appear on the Overview page.");
		Assert.assertEquals(ui.getWidgetLocationInfo(BaseWidget.BLOG, true), 2,
								"ERROR: Blog widget is not added to the community & does not appear on the Overview page.");
		*/
		//Verify that Ideation Blog is added to the community & now appear on the Overview page.
		log.info("INFO: Verify that Ideation Blog widgets is added to the community & now appear on the Overview page.");
		Assert.assertEquals(ui.getWidgetLocationInfo(BaseWidget.IDEATION_BLOG, true), 2,
								"ERROR: Ideation Blog widget is not added to the community & does not appear on the Overview page.");
		
		// Go to recent updates
		log.info("INFO: Click on Recent Updates link on the left nav");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		/*//Test addition of the Blog is getting listed in recent updates
		log.info("INFO: Verify Blog is getting listed in recent updates");
		Assert.assertTrue(hUI.findNewsItem("added the "+community.getName()+" community blog.").isVisible(),
								"Error: In Recent Updates the message for Blog Additions is not coming");*/
		
		//Test addition of the Ideation Blog is getting listed in recent updates
		log.info("INFO: Verify Ideation Blog is getting listed in recent updates");
		Assert.assertTrue(hUI.findNewsItem("added the "+community.getName()+" community Ideation Blog.").isVisible(),
								"Error: In Recent Updates the message for Blog Additions is not coming");
		
		//Commented below verification as they are not applicable since blog is default widget 
		/*
		//Select blog post and open EE
		log.info("INFO: Select blog post and open EE ");
		hUI.filterNewsItemOpenEE("added the "+community.getName()+" community blog.");

		//Verify the EE widget & blog details
		log.info("INFO: Verify the EE Blog details");
		Assert.assertTrue(driver.getSingleElement(HomepageUI.EEHistoryComment).getText().contains("added a blog to the "+community.getName()+" community."),
		 						"ERROR: The EE blog info is not correct");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
*/
		//delete community
		log.info("INFO: Removing community");
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li>PTC: Recent Updates </li>
	 *<li><B>Test Scenario:</B> RECENT UPDATES WIDGET: VERIFY THE RECENT UPDATE WIDGET </li>
	 *<li><B>Step:</B> Create a community </li>
	 *<li><B>Step:</B> Add the Events app to the community </li>
	 *<li><B>Step:</B> Navigate to Recent Updates </li>
	 *<li><B>Verify:</B> Verify the Events app does NOT appear on the page </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7D246CC8B3439FFB85257CAD0066140E">TTT-ON-PREM: RECENT UPDATES WIDGET: VERIFY THE RECENT UPDATE WIDGET</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void testRecentUpdatesAfterAddingEvent(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
				
			//create community
			log.info("INFO: Create community using API");
					
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test community for " + testName)
													.build();
							
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Select Add Apps from Community Actions
		log.info("INFO: Select Add Apps from the community action menu");
		Com_Action_Menu.ADDAPP.select(ui);					
		ui.clickLinkWait(CommunitiesUIConstants.leftNavEvents);
		
		//close the disabled widget palette
		driver.getSingleElement(CommunitiesUIConstants.WidgetSectionClose).click();
		
		// Go to recent updates
		log.info("INFO: Click on Recent Updates link on the left nav");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
						
		//Verify you are on the Recent Updates page.
		log.info("INFO: Verify you are on the Recent Updates page");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.recentUpdatesHeader);
		
		//Verify Events widget does NOT display on the Recent Updates page
		log.info("INFO: Verify the Events app does NOT display on the Recent Updates page");
		Assert.assertFalse(driver.isTextPresent(CommunitiesUIConstants.eventsAppDefaultMsg),
				"Error: Events app appears on the Recent Updates page");
		
		//delete community
		log.info("INFO: Removing community");
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
		
	}

	
	/**
	 *
	 * Edit and delete a bookmark in a Community
	 *<ul>
	 *<li><B>Test Scenario: BOOKMARKS WIDGET PART1: Verify creating new bookmarks
	 *<li><B>Test Scenario: BOOKMARKS WIDGET PART2: Verify editing & deleting bookmarks
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a Public community as owner using API & from the Mega Menu Share - Share a status message with Community</li>
	 *<li><B>Verify: Status message is successfully posted</B> </li>
	 *<li><B>Step: Go to Recent updates to check the message posted </B></li>
	 *<li><B>Verify: Status message is coming in recent updates</B> </li>
	 *<li><B></B> </li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void testDeleteAppsInCommunity(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test community for " + testName)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			if(ui.checkGKSetting(Data.getData().commTabbedNav)){
				log.info("INFO: Verify the Bookmarks appears on the top nav");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.tabbedNavBookmarksTab),
						"Error: Bookmarks does not appear on the top nav");
					
			}else {	
				log.info("INFO: Hover over the Overview button on the left nav");
				driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();
				
				log.info("INFO: Verify the Bookmarks link appears on the left nav by default");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.leftNavBookmarks),
						"Error: Bookmarks link does not appear on the left nav");
				
			}
			log.info("INFO: Verify the Add Your First Bookmark link appears on the Overview page");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.AddYourFirsBookMark),
					"Error: User can't create a bookmark");
			
			log.info("INFO: Click on the Add Your First Bookmark link");
			ui.clickLinkWait(CommunitiesUIConstants.AddYourFirsBookMark);
						
			log.info("INFO: Input Bookmarks URL ");
			driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);
			
			log.info("INFO: Input Bookmark Name");
			driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);
			
			log.info("INFO: Save the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();
			
			log.info("INFO: Verify the bookmark was added");
			Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName));
			
			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
				log.info("INFO: Click on the Overview tab");
				Community_LeftNav_Menu.BOOKMARK.select(ui);
				
			}else {			
				log.info("INFO: Open Overview menu from Left Nav. community card");
				ui.clickLinkWait(CommunitiesUIConstants.OpenOverViewMenu);
				
				log.info("INFO: Click on the Overview link");
				ui.clickLinkWait(CommunitiesUIConstants.leftNavBookmarksLink);

			}	
									
			log.info("INFO: Click on More link");
			ui.clickLinkWait(CommunitiesUIConstants.firstBookmarksMoreLink);
			
			log.info("INFO: Click on More Edit Bookmark");
			ui.clickLinkWait(CommunitiesUIConstants.EditLink);
			
			log.info("INFO: Start editing the bookmark URL & Name");			
			driver.getFirstElement(CommunitiesUIConstants.EditBookmarkURL).clear();
			driver.getFirstElement(CommunitiesUIConstants.EditBookmarkURL).type(Data.getData().EditBookmarkURL);
			driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).clear();
			driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).type(Data.getData().EditBookmarkName);
			driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription).clear();
			driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription).type(Data.getData().commonDescription);
						
			log.info("INFO: Save the changes in Bookmark");
			ui.clickLink(CommunitiesUIConstants.SaveButtonEntry);
			
			log.info("INFO: Verify the edited bookmark name appears");
			ui.fluentWaitTextPresent(Data.getData().EditBookmarkName);
			Assert.assertTrue(driver.isElementPresent("link="+Data.getData().EditBookmarkName),
					"ERROR: Edited Bookmark name does not appear");
						
			log.info("INFO: Click on More link to select Remove bookmark");
			ui.clickLinkWait(CommunitiesUIConstants.firstBookmarksMoreLink);
			
			log.info("INFO: Click on Delete the bookmark");
			ui.clickLinkWait(CommunitiesUIConstants.DeleteBookmark);
			
			log.info("INFO: Click on ok button to delete bookmark");
			ui.clickLinkWait(CommunitiesUIConstants.MemberImportSubmitButton);
			
			log.info("INFO: Verify the bookmark no longer displays");
			Assert.assertFalse(driver.isElementPresent("link="+Data.getData().EditBookmarkName),
					"ERROR: The bookmark did not get deleted");
			
			log.info("INFO: Cleanup - Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
			
		}
	
	/**
	*
	*<ul>
	*<li><B>Test Scenario:</B> BOOKMARKS WIDGET PART1: Important Bookmarks widget </li>
	*<li><B>Info:</B> This scenario will verify that when Add to Important Bookmarks is selected the bookmark appears in the Important Bookmarks widget </li>
	*<li><B>Step:</B> Create a Public community </li>
	*<li><B>Step:</B> Add a bookmark, but do NOT check the Add to Important Bookmarks checkbox </li>
	*<li><B>Step:</B> Add a 2nd bookmark and check Add to Important Bookmarks checkbox </li>
	*<li><B>Step:</B> Return to the Overview page 
	*<li><B>Verify:</B> Verify the 1st bookmark does NOT appear in the Important Bookmarks widget </li>
	*<li><B>Verify:</B> Verify the 2nd bookmark DOES appear in the Important Bookmarks widget </li>
	*<li><B>Cleanup:</B> Delete the community </li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/E22B0DF5F6A74B5E85257C8D00640E6C">TTT - BOOKMARKS WIDGET PART1: VERIFY CREATING NEW BOOKMARKS</a></li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void importantBookmarksWidget(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
		                                           .access(Access.PUBLIC)
		                                           .description("Test community for " + testName)
		                                           .build();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);

		//Login as Owner
		ui.login(testUser);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Click on the Add Your First Bookmark link
		log.info("INFO: Click on the Add Your First Bookmark link");
		ui.clickLinkWait(CommunitiesUIConstants.AddYourFirsBookMark);

		log.info("INFO: Input Bookmarks URL ");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);

		log.info("INFO: Input Bookmark Name");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);

		log.info("INFO: Save the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();

		//Verify the bookmark was added
		log.info("INFO: Verify the bookmark was added");
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
				"ERROR: The bookmark does not appear on the page");

		//Click on the Add Bookmark button
		log.info("INFO: Click on the Add Bookmark button");
		driver.getFirstElement(CommunitiesUIConstants.AddBookmarkButton).click();

		log.info("INFO: Input Bookmarks URL ");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().EditBookmarkURL);

		log.info("INFO: Input Bookmark Name");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().EditBookmarkName);

		log.info("INFO: Check the Add to Important Bookmarks checkbox");
		driver.getSingleElement(CommunitiesUIConstants.importantBkmkCheckbox).click();

		log.info("INFO: Save the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();

		log.info("INFO: Make sure the bookmark appears on the page");
		ui.fluentWaitTextPresent(Data.getData().EditBookmarkName);

		//Verify the bookmark was added
		log.info("INFO: Verify the bookmark was added");
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().EditBookmarkName),
				"ERROR: The bookmark does not appear on the page");

		//Navigate back to the Overview page
		log.info("INFO: Click on the Bookmarks link from the left nav");
		Community_LeftNav_Menu.OVERVIEW.select(ui);

		//Verify the bookmark with Important Bookmark checkbox unchecked does NOT appear in the Important Bookmarks widget
		log.info("INFO: Verify bookmark with 'Add to Important Bookmark' checkbox unchecked does NOT appear in the Important Bookmarks widget");
		Assert.assertFalse(ui.checkImportantBkmksApp(Data.getData().BookmarkName),
				"ERROR: Bookmark appears in the Important Bookmarks widget, but should NOT appear");

		//Verify the bookmark with the Important Bookmark checkbox checked appears in the Important Bookmarks app
		log.info("INFO: Verify bookmark with 'Add to Important Bookmark' checkbox checked appears in the Important Bookmarks widget");
		Assert.assertTrue(ui.checkImportantBkmksApp(Data.getData().EditBookmarkName),
				"ERROR: Bookmark does not appear in the Important Bookmarks widget, but should appear");

		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser);

		ui.endTest();



	}
	
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Edit Widget Title (1 of 4)</li>
	 * <li><B>Info:</B> This test will verify the widget title change is reflected in the summary widget on the overview page </li>
	 * <li><B>Step:</B> Create a moderated community using the API </li>
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Bookmarks widget </li>
	 * <li><B>Verify:</B> Verify "Change Title" is listed as an option on the action menu </li>
	 * <li><B>Step:</B> Click on "Change Title" </li>
	 * <li><B>Verify:</B> Verify the "Change Title" dialog displays </li>
	 * <li><B>Step:</B> Change the widget title and Save </li>
	 * <li><B>Verify:</B> Verify the title change is seen in the summary widget </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/9E3EB856836D1ED185257DDA0041B028 ">TTT- EDIT WIDGET TITLE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
    public void changeTitleAction_summaryWidget(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String widgetName = "";
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.MODERATED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Bookmarks widget title test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.BOOKMARKS;
		Widget_Action_Menu action=Widget_Action_Menu.CHANGETITLE;
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: get the widget title before title change");
		ui.getSummaryWidgetTitle(widget);
		
		log.info("INFO: Check Bookmarks action menu for Change Title");
		ui.checkWidgetMenuPresent(widget, action);
		
		log.info("INFO: Click on the action Change Title");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Validate the Change Title dialog displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.widgetChangeTitleDialog),
						  "ERROR: The Change Title dialog is not displayed!!");
		
		log.info("INFO: Clear default widget title text");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();
		
		log.info("INFO: Enter the new widget title");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(Data.getData().ChangeBookmarkAppTitle);
		
		log.info("INFO: Save the widget title change");
		ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);
		
		log.info ("INFO: Validate the user is on the Overview page");
		Assert.assertEquals(driver.getTitle(),("Overview - " + community.getName()),
				"ERROR: User is not on the parent community Overview page");
				
		log.info("INFO: Get the widget title after the title change");
		widgetName=ui.getSummaryWidgetTitle(widget);
				
		log.info ("INFO: Validate the title change appears on the Bookmarks summary widget");
		Assert.assertTrue(widgetName.contains(Data.getData().ChangeBookmarkAppTitle),
				"ERROR: Widget title does not match title change: Expected: \"" + Data.getData().ChangeBookmarkAppTitle +
				"\", actual: \"" + widgetName + "\"");
		
		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Edit Widget Title (2 of 4)</li>
	 * <li><B>Info:</B> This test will verify the widget title change is reflected on the left nav/community card on the overview page </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Bookmark widget </li>
	 * <li><B>Step:</B> Click on "Change Title" </li>
	 * <li><B>Step:</B> Change the widget title and Save </li>
	 * <li><B>Verify:</B> Verify the title change is seen in the left nav. panel </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/9E3EB856836D1ED185257DDA0041B028 ">TTT- EDIT WIDGET TITLE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
    public void changeTitleAction_leftNav(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String widgetName = "" ;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Bookmarks widget title test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.BOOKMARKS;
		Widget_Action_Menu action=Widget_Action_Menu.CHANGETITLE;
						
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		log.info("INFO: get the widget title before title change");
		ui.getSummaryWidgetTitle(widget);
		
		log.info("INFO: Check Bookmarks action menu for Change Title");
		ui.checkWidgetMenuPresent(widget, action);
		
		log.info("INFO: Click on the action Change Title");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Clear default widget title text");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();
		
		log.info("INFO: Enter the new widget title");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(Data.getData().ChangeBookmarkAppTitle);
		
		log.info("INFO: Save the widget title change");
		ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);
		
		if (driver.getVisibleElements(CommunitiesUIConstants.leftNavOverviewButton).size()!=0){
			
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();
			
		log.info("INFO: Get the widget title after the title change. ");
		widgetName=ui.getSummaryWidgetTitle(widget);	
		
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();
				
		log.info ("INFO: Validate the widget title change appears on the left navigation panel");
		String widgetText = driver.getFirstElement(ui.getLeftNavWidget(widgetName)).getText();
			Assert.assertEquals(widgetText, Data.getData().ChangeBookmarkAppTitle,
					"ERROR: New title does not appear on the left nav. panel");

		} else {

			log.info("INFO: Get the widget title after the title change. ");
			widgetName = ui.getSummaryWidgetTitle(widget);
			driver.navigate().refresh();

			// Get all elements form Tabbed Menu menu
			List<Element> tabbedMenu = driver.getElements(CommunitiesUIConstants.tabbedNavMenuItems);
			List<String> tabbedMenuItem = new ArrayList<>();
			log.info("INFO:Size of tabbed menu item is: " + tabbedMenu.size());

			for (int j = 0; j < tabbedMenu.size(); j++) {
				String tabbedMenuName = tabbedMenu.get(j).getText();
				log.info("Tabbed menu items is: "+tabbedMenuName);
				tabbedMenuItem.add(tabbedMenuName);
			}

			if (!tabbedMenuItem.contains(widgetName)) {
				ui.clickLinkWithJavascript(CommunitiesUIConstants.tabbedNavMoreTab);

				log.info("INFO: Validate the widget title change appears on under MORE menu");
				String widgetText = driver.getFirstElement("xpath=//td[contains(@id,'moreIdx_')][text()='"+widgetName+"']").getText();
				Assert.assertEquals(widgetText, Data.getData().ChangeBookmarkAppTitle,
						"ERROR: New title does not appear on the left nav. panel");
			} else {
				log.info("INFO: Validate the widget title change appears on the top/tabbed navigation");
				String widgetText = driver.getFirstElement(ui.getLeftNavWidget(widgetName)).getText();
				Assert.assertEquals(widgetText, Data.getData().ChangeBookmarkAppTitle,
						"ERROR: New title does not appear on the left nav. panel");
			}
		}

		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Edit Widget Title (3 of 4)</li>
	 * <li><B>Info:</B> Test to verify that the title change is seen on the search results page widget tab. </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From Overview page click on the action menu for the Bookmark widget </li>
	 * <li><B>Step:</B> Select the "Change Title" option </li>
	 * <li><B>Step:</B> Rename the widget title & Save" </li>
	 * <li><B>Step:</B> From Overview page perform a basic search - "This Community" search </li>
	 * <li><B>Verify:</B> The widget Tab on the search results page shows the title change </li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/9E3EB856836D1ED185257DDA0041B028 ">TTT- EDIT WIDGET TITLE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
    public void changeTitleAction_searchResultsTab(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String widgetName = "";
		String gk_flag = "search-history-view-ui";
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Bookmarks widget title test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.BOOKMARKS;
		Widget_Action_Menu action=Widget_Action_Menu.CHANGETITLE;
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: get the widget title before title change");
		widgetName=ui.getSummaryWidgetTitle(widget);
		
		log.info("INFO: Check Bookmarks action menu for Change Title");
		ui.checkWidgetMenuPresent(widget, action);
		
		log.info("INFO: Click on the action Change Title");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Clear default widget title text");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();
		
		log.info("INFO: Enter the new widget title");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(Data.getData().ChangeBookmarkAppTitle);
		
		log.info("INFO: Save the widget title change");
		ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);
		
		log.info("INFO: Get the widget title after the title change. ");
		widgetName=ui.getSummaryWidgetTitle(widget);
		
		log.info("INFO: Check to see if the Gatekeeper setting for the new search icon: " + gk_flag + " is enabled.  ");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean s = gkc.getSetting(gk_flag);
		if (!s)

		{

			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - new search icon is not used here");			

			log.info("INFO: Clear the search input field");
			driver.getFirstElement(CommunitiesUIConstants.SearchTextArea).clear();

			log.info("INFO: Enter the search string");
			driver.getFirstElement(CommunitiesUIConstants.SearchTextArea).type(Data.getData().commonHandle);

			log.info("INFO: Click on the search button");
			ui.clickLinkWait(CommunitiesUIConstants.SearchButton);

			log.info("INFO: Get the edited widget tab name on the search results page");
			ui.fluentWaitElementVisible(ui.getCommSearchResultsTabName(widgetName));
			String widgetText = driver.getFirstElement(ui.getCommSearchResultsTabName(widgetName)).getText();

			log.info("INFO: Verify a tab with the new widget title appears on the search results page");
			Assert.assertEquals(widgetText, Data.getData().ChangeBookmarkAppTitle,
					"ERROR: New widget title does not appear on the search results page widget tab");


		} else {
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - new search icon is being used");

			//Note: when a driver.clickAt(0,0) is used to bring user to the top of the page, the search icon doesn't display
			//doing a page refresh brings user to the top of the page & ensures the search icon appears
			log.info("INFO: Refresh page to ensure user is at the top of the page and search icon appears");
			UIEvents.refreshPage(driver);

			log.info("INFO: Click on the search icon");
			ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);

			log.info("INFO: Wait for the search text area to display");
			ui.fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);

			log.info("INFO: Enter a word to search on");
			ui.typeText(GlobalsearchUI.TextAreaInPanel, Data.getData().commonHandle);

			log.info("INFO: Click on 'This Community'");
			ui.clickLinkWait(CommunitiesUIConstants.thisCommunitySearchLink);

			log.info("INFO: Get the edited widget tab name on the search results page");
			ui.fluentWaitElementVisible(ui.getCommSearchResultsTabName(widgetName));
			String widgetText = driver.getFirstElement(ui.getCommSearchResultsTabName(widgetName)).getText();

			log.info("INFO: Verify a tab with the new widget title appears on the search results page");
			Assert.assertEquals(widgetText, Data.getData().ChangeBookmarkAppTitle,
					"ERROR: New widget title does not appear on the search results page widget tab");
		}
		
		log.info("INFO:Cleanup: Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Edit Widget Title (4 of 4)</li>
	 * <li><B>Info:</B> Test that an owner is able to edit the title of the Bookmarks widget. </li>
	 * <li><B>Step:</B> Create a moderated community using the API </li>
	 * <li><B>Step:</B> From Overview page click on the action menu for the Bookmarks widget </li>
	 * <li><B>Step:</B> Select the "Change Title" option </li>
	 * <li><B>Step:</B> Rename the widget title & Save" </li>
	 * <li><B>Step:</B> Navigate to the Edit Community form </li>
	 * <li><B>Step:</B> Verify the widget title change appears on the Start Page listbox </li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/9E3EB856836D1ED185257DDA0041B028 ">TTT- EDIT WIDGET TITLE</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
    public void changeTitleAction_startPage(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String widgetName = "";
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.MODERATED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Bookmarks widget title test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.BOOKMARKS;
		Widget_Action_Menu action=Widget_Action_Menu.CHANGETITLE;
		
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
				
		//navigate to the community using API
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//get Bookmarks title before title is changed
		log.info("INFO: get the widget title before title change");
		ui.getSummaryWidgetTitle(widget);
		
		//click on the Bookmarks action menu link & check that the action "Change Title" exists
		log.info("INFO: Check Bookmarks action menu for Change Title");
		ui.checkWidgetMenuPresent(widget, action);
		
		//click on the "Change Title" action
		log.info("INFO: Click on the action Change Title");
		ui.performCommWidgetAction(widget, action);
		
		//verify the Change Title dialog displays
		log.info("INFO: Validate the Change Title dialog displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.widgetChangeTitleDialog),
				"ERROR: The Change Title dialog is not displayed!!");
		
		//change widget title
		log.info("INFO: Clear the default widget title text");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();
		
		log.info("INFO: Enter the new widget title");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(Data.getData().ChangeBookmarkAppTitle);
		
		//save the new widget title
		log.info("INFO: Save the widget title change");
		ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);
					
		//get Bookmarks title after the title change		
		log.info("INFO: Get the widget title after the title change. ");
		widgetName = ui.getSummaryWidgetTitle(widget);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
						
		//open the Edit Community form
		log.info("INFO: Open the Edit Community form");
		Com_Action_Menu.EDIT.select(ui);
		
		//check to make sure the Edit Community page is loaded
		log.info("INFO: Check to make sure Edit Community page is loaded");	
		ui.scrollIntoViewElement(CommunitiesUIConstants.editCommStartPageListItem);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.editCommStartPageListItem);
		
		//list out Start Page listbox options
		log.info("INFO: Start Page listbox options are: " + driver.getFirstElement(CommunitiesUIConstants.editCommStartPageListItem) .getText());
		
		//verify the new widget title appears on the Start Page listbox					
		log.info("INFO: Verify the new widget title appears on the Edit Community Start Page listbox");	     
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.editCommStartPageListItem) .getText().contains(widgetName),
		      "ERROR: New widget title does not appear on the Start Page listbox");
				
		//Cleanup: Delete community
		log.info("INFO: Click Cancel button on Edit Community page to return to Overview page");
		ui.clickLinkWait(CommunitiesUIConstants.EditCommunityCancelButton);
		
		log.info ("INFO: Validate the user is on the Overview page - check Community Actions displays ");
		ui.fluentWaitElementVisible(BaseUIConstants.Community_Actions_Button);
		
		log.info("INFO: Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	/**
	 *<li><B>Test Scenario:</B> Edit Widget Title (Test 5)</li>
	 * <li><B>Info:</B> Test to verify the widget title change appears on the Change Title pop-up dialog. </li>
	 * <li><B>Step:</B> Create a Public community using the API </li>
	 * <li><B>Step:</B> From Overview page click on the action menu for the Bookmarks widget </li>
	 * <li><B>Step:</B> Select the "Change Title" option </li>
	 * <li><B>Step:</B> Rename the widget title & Save" </li>
	 * <li><B>Step:</B> Refresh the page </li>
	 * <li><B>Step:</B> Click on the Change Title link for Bookmarks widget </li>
	 * <li><B>Step:</B> Verify the widget title change appears on the Change Title pop-up dialog </li> 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/9E3EB856836D1ED185257DDA0041B028 ">TTT- EDIT WIDGET TITLE</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void changeTitleAction(){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String widgetName = "";
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Bookmarks widget title test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.BOOKMARKS; 
		Widget_Action_Menu action=Widget_Action_Menu.CHANGETITLE;
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		log.info("INFO: get the widget title before title change");
		widgetName = ui.getSummaryWidgetTitle(widget);
		
		log.info("INFO: Check Bookmarks action menu for Change Title");
		ui.checkWidgetMenuPresentClick(widgetName, action, true);
		
		log.info("INFO: Clear default widget title text");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();
		
		log.info("INFO: Enter the new widget title");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(Data.getData().ChangeBookmarkAppTitle);
		
		log.info("INFO: Save the widget title change");
		ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);
				
		log.info("INFO: Get the widget title after the title change");
		widgetName = ui.getSummaryWidgetTitle(widget);
		
		log.info("INFO: Refresh page to make sure change appears");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Check Bookmarks action menu for Change Title");
		ui.checkWidgetMenuPresentClick(widgetName,action, true);
		
		log.info ("INFO: Validate the title change appears on the Change Title dialog");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).getAttribute("value").contentEquals(Data.getData().ChangeBookmarkAppTitle),
				"ERROR: New widget title does not match the Title on the Change Title dialog.  Expected " + Data.getData().ChangeBookmarkAppTitle +
				" but got " + (driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).getAttribute("value")));
		
				String s = driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).getAttribute("value") ;
				log.info("INFO: Widget title is set to = " + s);
		
		log.info("INFO:Cleanup - Removing community " + testName);
		community.delete(ui, testUser);

		ui.endTest();			
				
}
	
	/**
	 *<li><B>Test Scenario: Widgets: Move Widgets to Previous & Next Columns (Test 1) </B></li> 
	 * <li><B>Info:</B> This test will verify the Members summary widget gets moved to the Previous column/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Check to make sure the Gatekeeper setting Change Layout is enabled - if False test will be skipped
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Members summary widget </li>
	 * <li><B>Step:</B> Click on the Move to Previous Column link </li>
	 * <li><B>Verify:</B> Verify Members summary is in the correct column </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 	 * 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/D7E6BBCA6950021485257DDA0047F8AE ">TTT- WIDGETS: MOVE WIDGETS TO PREVIOUS & NEXT COLUMNS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void moveMembersToPreviousColumn(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int columnNum = 0;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Widget action menu 'Move to Previous Column' test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.MEMBERS; 
		Widget_Action_Menu action=Widget_Action_Menu.MOVETOPREVIOUSCOLUMN;
				
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
			
		log.info("INFO: Click on the action Move to Previous Column on the Members action menu");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Get the column number the Members summary widget is located in");
		columnNum = ui.getWidgetLocationInfo(widget,true);
		
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Layout with Tabbed Nav is being used");
			log.info("INFO: Verify the Members summary widget appears in the correct column - middle column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Members widget is not in the correct column - middle column.  Expected column 2, but found in column " + columnNum);

		}else { 
			log.info("INFO: Layout with left nav is being used");
			log.info("INFO: Verify the Members summary widget appears in the correct column - left column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Members widget is not in the correct column - left column.  Expected column 1, but found in column " + columnNum);
		}
		
		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	
	/**
	 *<li><B>Test Scenario: Widgets: Move Widgets to Previous & Next Columns (Test 2) </B></li> 
	 * <li><B>Info:</B> This test will verify the Bookmarks widget gets moved to the Previous column/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Check to make sure the Gatekeeper setting Change Layout is enabled - if False test will be skipped
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Bookmarks widget </li>
	 * <li><B>Step:</B> Click on the Move to Previous Column link </li>
	 * <li><B>Verify:</B> Verify Bookmarks is in the correct column </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 	 * 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/D7E6BBCA6950021485257DDA0047F8AE ">TTT- WIDGETS: MOVE WIDGETS TO PREVIOUS & NEXT COLUMNS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void moveBookmarksToPreviousColumn(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int columnNum = 0;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Widget action menu 'Move to Previous Column' test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.BOOKMARKS; 
		Widget_Action_Menu action=Widget_Action_Menu.MOVETOPREVIOUSCOLUMN;

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		log.info("INFO: Click on the action Move to Previous Column on the Bookmarks action menu");
		ui.performCommWidgetAction(widget, action);

		log.info("INFO: Get the column number the Bookmarks widget is located in");
		columnNum = ui.getWidgetLocationInfo(widget,true);

		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Layout with Tabbed Nav is being used");
			log.info("INFO: Verify the Bookmarks widget appears in the correct column - left column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Bookmarks widget is not in the correct column - left column.  Expected column 1, but found in column " + columnNum);

		}else { 
			log.info("INFO: Layout with left nav is being used");
			log.info("INFO: Verify the Bookmarks widget appears in the correct column - right column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Bookmarks widget is not in the correct column - right column.  Expected column 3, but found in column " + columnNum);
		}

		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	/**
	 *<li><B>Test Scenario: Widgets: Move Widgets to Previous & Next Columns (Test 3) </B></li> 
	 * <li><B>Info:</B> This test will verify the Tags widget gets moved to the Previous column/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Check to make sure the Gatekeeper setting Change Layout is enabled - if False test will be skipped
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Tags widget </li>
	 * <li><B>Step:</B> Click on the Move to Previous Column link </li>
	 * <li><B>Verify:</B> Verify Tags is in the correct column </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 	 * 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/D7E6BBCA6950021485257DDA0047F8AE ">TTT- WIDGETS: MOVE WIDGETS TO PREVIOUS & NEXT COLUMNS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void moveTagsToPreviousColumn(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int columnNum = 0;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Widget action menu 'Move to Previous Column' test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.TAGS; 
		Widget_Action_Menu action=Widget_Action_Menu.MOVETOPREVIOUSCOLUMN;
				
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		log.info("INFO: Click on the action Move to Previous Column on the Tags action menu");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Get the column number the Tags widget is located in");
		columnNum = ui.getWidgetLocationInfo(widget,true);
		
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Layout with Tabbed Nav is being used");
			log.info("INFO: Verify the Tags widget appears in the correct column - banner area");
			Assert.assertEquals(columnNum, 4,
					"ERROR: Tags widget is not in the correct column.  Expected column 4, but found in column " + columnNum);

		}else { 
			log.info("INFO: Layout with left nav is being used");
			log.info("INFO: Verify the Tags widget appears in the correct column - middle column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Tags widget is not in the correct column - middle column.  Expected column 2, but found in column " + columnNum);
		}
		
		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	/**
	 *<li><B>Test Scenario: Widgets: Move Widgets to Previous & Next Columns (Test 4) </B></li> 
	 * <li><B>Info:</B> This test will verify the Important Bookmarks widget gets moved to the Next column/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Check to make sure the Gatekeeper setting Change Layout is enabled - if False test will be skipped
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Important Bookmarks widget </li>
	 * <li><B>Step:</B> Click on the Move to Next Column link </li>
	 * <li><B>Verify:</B> Verify Important Bookmarks is in the correct column </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 	 * 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/D7E6BBCA6950021485257DDA0047F8AE ">TTT- WIDGETS: MOVE WIDGETS TO PREVIOUS & NEXT COLUMNS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void moveImportantBookmarksToNextColumn(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int columnNum = 0;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Widget action menu 'Move to Next Column' test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.IMPORTANTBOOKMARKS; 
		Widget_Action_Menu action=Widget_Action_Menu.MOVETONEXTCOLUMN;
				
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the action Move to Next Column on the Important Bookmarks action menu");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Get the column number the Important Bookmarks widget is located in");
		columnNum = ui.getWidgetLocationInfo(widget,true);
		
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Layout with Tabbed Nav is being used");
			log.info("INFO: Verify the Important Bookmarks widget appears in the correct column - banner area");
			Assert.assertEquals(columnNum, 4,
					"ERROR: Important Bookmarks widget is not in the correct column - banner area.  Expected column 4, but found in column " + columnNum);

		}else { 
			log.info("INFO: Layout with left nav is being used");
			log.info("INFO: Verify the Important Bookmarks widget appears in the correct column - middle column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Important Bookmarks widget is not in the correct column - middle column.  Expected column 2, but found in column " + columnNum);
		}
		
		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	/**
	 *<li><B>Test Scenario: Widgets: Move Widgets to Previous & Next Columns (Test 5) </B></li> 
	 * <li><B>Info:</B> This test will verify the Community Description widget gets moved to the Next column/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Check to make sure the Gatekeeper setting Change Layout is enabled - if False test will be skipped
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Community Description widget </li>
	 * <li><B>Step:</B> Click on the Move to Next Column link </li>
	 * <li><B>Verify:</B> Verify Community Description is in the correct column </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 	 * 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/D7E6BBCA6950021485257DDA0047F8AE ">TTT- WIDGETS: MOVE WIDGETS TO PREVIOUS & NEXT COLUMNS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void moveCommDescToNextColumn(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int columnNum = 0;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Widget action menu 'Move to Next Column' test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.COMMUNITYDESCRIPTION; 
		Widget_Action_Menu action=Widget_Action_Menu.MOVETONEXTCOLUMN;
				
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the action Move to Next Column on the Community Description action menu");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Get the column number the Community Description widget is located in");
		columnNum = ui.getWidgetLocationInfo(widget,true);
		
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Layout with Tabbed Nav is being used");
			log.info("INFO: Verify the Community Description widget appears in the correct column - right column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Community Description widget is not in the correct column - right column.  Expected column 3, but found in column " + columnNum);

		}else { 
			log.info("INFO: Layout with left nav is being used");
			log.info("INFO: Verify the Community Description widget appears in the correct column - left column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Community Description widget is not in the correct column - left column.  Expected column 1, but found in column " + columnNum);
		}
		
		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	/**
	 *<li><B>Test Scenario: Widgets: Move Widgets to Previous & Next Columns (Test 6) </B></li> 
	 * <li><B>Info:</B> This test will verify the Tags widget gets moved to the Next column/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Check to make sure the Gatekeeper setting Change Layout is enabled - if False test will be skipped
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Tags widget </li>
	 * <li><B>Step:</B> Click on the Move to Next Column link </li>
	 * <li><B>Verify:</B> Verify Tags is in the correct column </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 	 * 
	 * <li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/D7E6BBCA6950021485257DDA0047F8AE ">TTT- WIDGETS: MOVE WIDGETS TO PREVIOUS & NEXT COLUMNS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"})
    public void moveTagsToNextColumn(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();		
		int columnNum = 0;
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Widget action menu 'Move to Next Column' test: " + testName)
												   .build();
		
		BaseWidget widget=BaseWidget.TAGS; 
		Widget_Action_Menu action=Widget_Action_Menu.MOVETONEXTCOLUMN;
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the action Move to Next Column on the Tags action menu");
		ui.performCommWidgetAction(widget, action);
		
		log.info("INFO: Get the column number the Tags widget is located in");
		columnNum = ui.getWidgetLocationInfo(widget,true);
		
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Layout with Tabbed Nav is being used");
			log.info("INFO: Verify the Tags widget appears in the correct column - middle column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Tags widget is not in the correct column - middle column.  Expected column 2, but found in column " + columnNum);

		}else { 
			log.info("INFO: Layout with left nav is being used");
			log.info("INFO: Verify the Tags widget appears in the correct column - right column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Tags widget is not in the correct column - right column.  Expected column 3, but found in column " + columnNum);
		}
		
		log.info("INFO:Cleanup - Removing community " + testName);
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();			
				
}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Community Description: In-Place Edit (Test 1)</li>
	 * <li><B>Info:</B> This test will verify the Edit action appears on the community description action menu</li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Community Description widget </li>
	 * <li><B>Verify:</B> Verify the Edit action appears on the drop-down menu </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/5CCB401020BA48E185257DDA004519CA">TTT- COMMUNITY DESCRIPTION: IN-PLACE EDIT</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void commDescriptionActionMenuEdit(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
					
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Community Description in-place test: " + testName)
												   .build();
				
		BaseWidget widget=BaseWidget.COMMUNITYDESCRIPTION;  
		Widget_Action_Menu action=Widget_Action_Menu.EDIT;
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//click on the Community Description action menu link & check that the action "Edit" exists
		log.info("INFO: Verify Edit appears on the Community Description action menu");		
		Assert.assertTrue(ui.checkWidgetMenuPresent(widget, action),
				"ERROR: Edit does not appear on the drop-down menu");

		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Community Description: In-Place Edit (Test 2) </li>
	 * <li><B>Info:</B> This test will verify the Edit dialog box displays ok</li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Community Description widget </li>
	 * <li><B>Step:</B> Click on the Edit link </li>
	 * <li><B>Verify:</B> Verify the CKEditor Toolbar displays </li>
	 * <li><B>Verify:</B> Verify the Save and Close button displays </li>
	 * <li><B>Verify:</B> Verify the Cancel button displays </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/5CCB401020BA48E185257DDA004519CA">TTT- COMMUNITY DESCRIPTION: IN-PLACE EDIT</a></li>
	 *</ul>
	 */
	
	
	@Test(groups = {"regression","regressioncloud"})
	public void commDescriptionInPlaceEditDialogBox(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Community Description in-place test: " + testName)
												   .build();
				
		BaseWidget widget=BaseWidget.COMMUNITYDESCRIPTION; 
		Widget_Action_Menu action=Widget_Action_Menu.EDIT;
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//click on the Community Description action menu link & check that the action "Edit" exists
		log.info("INFO: Verify Edit appears on the Community Description action menu");
		ui.checkWidgetMenuPresent(widget, action);
		Assert.assertTrue(ui.checkWidgetMenuPresent(widget, action),
				"ERROR: Edit does not appear on the drop-down menu");
		
		//click on the "Edit" action
		log.info("INFO: Click on the Edit action");
		ui.performCommWidgetAction(widget, action);	
		
		//verify the Editor Toolbar displays
		log.info("INFO: Verify the CKEditor toolbar displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.editCommDescEditorToolbar),
				"ERROR: CKEditor toolbar does not display");
				
		//verify the Save and Close button displays
		log.info("INFO: Verify the Save and Close button displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.editCommDescSaveAndCloseButton),
				"ERROR: Save and Close button does not display");
		
		//verify the Cancel button displays
		log.info("INFO: Verify the Cancel button displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.editCommDescCancelButton),
				"ERROR: Cancel button does not display");

		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Community Description: In-Place Edit (Test 3)</li>
	 * <li><B>Info:</B> This test will verify the to edit the community description in-place/li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page click on the action menu for the Community Description widget </li>
	 * <li><B>Step:</B> Click on the Edit link </li>
	 * <li><B>Step:</B> Edit the community description </li>
	 * <li><B>Step:</B> Click Save and Close </li>
	 * <li><B>Verify:</B> Verify the changes were saved </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/5CCB401020BA48E185257DDA004519CA ">TTT- COMMUNITY DESCRIPTION: IN-PLACE EDIT</a></li>
	 *</ul>
	 */
	
	
	@Test(groups = {"regression","regressioncloud"})
	public void commDescriptionInPlaceEdit(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		String descriptionText = "text added by automation keycode ("+Helper.genStrongRand()+")";		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Edit Community Description in-place test: " + testName)
												   .build();
				
		BaseWidget widget=BaseWidget.COMMUNITYDESCRIPTION; 
		Widget_Action_Menu action=Widget_Action_Menu.EDIT;
		
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
			
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
	
		log.info("INFO: Log into Communities as user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Verify Edit appears on the Community Description action menu");
		ui.checkWidgetMenuPresent(widget, action);
		Assert.assertTrue(ui.checkWidgetMenuPresent(widget, action),
				"ERROR: Edit does not appear on the drop-down menu");
		
		log.info("INFO: Click on the Edit action");
		ui.performCommWidgetAction(widget, action);	
		
		log.info("INFO: Check for the Communities link to make sure page is loaded");
		ui.isElementPresent(CommunitiesUIConstants.CommunitiesLink);
		
		log.info("INFO: Change the Community description text");			
		ui.typeNativeInCkEditor(descriptionText);
		
		log.info("INFO: Scroll down the page so the 'Save and Close' button appears on the screen");
		driver.executeScript("scroll(0,250)");
		
		log.info("INFO: Click the Save and Close button");
		driver.getSingleElement(CommunitiesUIConstants.editCommDescSaveAndCloseButton).click();
		
		log.info("INFO: Verify the Community Description change was saved");		
		Assert.assertTrue(driver.isTextPresent(descriptionText),
				"ERROR:community description text does not match edited text");
		
		log.info("INFO: Removing community for Test case " + testName );
		//community.delete(ui, testUser);
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> TAGS: VERIFY TAGS IN COMMUNITIES - TAGS WIDGET WITH CONTENT (Test 1)</li>
	 * <li><B>Info:</B> Test will verify the bookmark tag appears in both the Cloud & List views on the overview page Tags widget </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page add a bookmark with a tag </li>
	 * <li><B>Step:</B> Index communities </li>
	 * <li><B>Step:</B> Log out & log back into communities </li>
	 * <li><B>Step:</B> Open the community </li>
	 * <li><B>Verify:</B> Verify the bookmark tag appears in the Cloud view </li>
	 * <li><B>Step:</B> Click on the Tags widget List link </li>
	 * <li><B>Verify:</B> Verify the bookmark tag appears in the List view </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/3818FC71C327D00685257E280047AF5C">TAGS: VERIFY TAGS IN COMMUNITIES - TAGS WIDGET WITH CONTENT</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression"} , enabled=false )
	public void overviewTagsCloudAndListViews(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Tags widget on Overview page " + testName)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
									
			//login as Owner
			ui.login(testUser);
						
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//click on the Bookmark link Add Your First Bookmark
			log.info("INFO: Click on the Bookmark link Add Your First Bookmark");
			driver.getFirstElement(CommunitiesUIConstants.AddYourFirsBookMark).click();
			
			//enter a bookmark URL
			log.info("INFO: Enter a bookmark URL");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);
			
			//enter a bookmark name
			log.info("INFO: Enter a bookmark name");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);
			
			//enter a bookmark description
			log.info("INFO: Enter a description for the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkDescription).type(Data.getData().BookmarkDesc);
			
			//enter a bookmark tag
			log.info("INFO: Enter a tag for the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkTag).type(Data.getData().BookmarkTag);
			
			//click the Save button
			log.info("INFO: Click the Save button");
			driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();
										
			//run command to index Communities now
			log.info("INFO: Index communities by running indexNow command");
			try {
				gsui.indexNow(serverURL, adminService, Data.getData().BookmarkTag, "communities", testUser, adminUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//logout the user 
			ui.logout();
			ui.close(cfg);
			
			//load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
									
			//login as Owner
			ui.login(testUser);
						
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
							
			//verify the bookmark tag appears in Cloud view.
			log.info("INFO: Verify the bookmark tag appears in the Cloud view");
			Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.overviewPageTagInCloud).getText(), Data.getData().BookmarkTag.toLowerCase(),
					           "ERROR: Tag does not appear in Cloud view");
			
			//click on the List link
			log.info("INFO: Click on the Tags widget List link");
			ui.clickLinkWait(CommunitiesUIConstants.ListUnderTag);
			
			// Test the tag entry is appearing in the List
			log.info("INFO: Verify the bookmark tag appears in the List view");
			Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.TagInList).getText(), Data.getData().BookmarkTag.toLowerCase(),
					               "ERROR: Tag does not appear in List view");
										
			//delete community
			log.info("INFO: Removing community");
			community.delete(ui, testUser);
				
			ui.endTest();
			
			
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> TAGS: VERIFY TAGS IN COMMUNITIES - TAGS WIDGET WITH CONTENT (Test 2)</li>
	 * <li><B>Info:</B> Test will verify the bookmark tag appears on the search results page tabs </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page add a bookmark with a tag </li>
	 * <li><B>Step:</B> Index communities </li>
	 * <li><B>Step:</B> Log out & log back into communities </li>
	 * <li><B>Step:</B> Open the community </li>
	 * <li><B>Step:</B> Click on the bookmark tag in the Tags widget </li>
	 * <li><B>Verify:</B> Verify the Search Results page displays </li>
	 * <li><B>Verify:</B> Verify the bookmark tag appears on the Bookmarks tab</li>
	 * <li><B>Step:</B> Click on the Bookmarks tab </li>
	 * <li><B>Verify:</B> Verify the bookmark tag appears on the All Connections Content tab </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/3818FC71C327D00685257E280047AF5C">TAGS: VERIFY TAGS IN COMMUNITIES - TAGS WIDGET WITH CONTENT</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression"} , enabled=false )
	public void overviewTagsSearchResultsPage(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Tags widget on Overview page " + testName)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
									
			//login as Owner
			ui.login(testUser);
						
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//click on the Bookmark link Add Your First Bookmark
			log.info("INFO: Click on the Bookmark link Add Your First Bookmark");
			driver.getFirstElement(CommunitiesUIConstants.AddYourFirsBookMark).click();
			
			//enter a bookmark URL
			log.info("INFO: Enter a bookmark URL");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);
			
			//enter a bookmark name
			log.info("INFO: Enter a bookmark name");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);
			
			//enter a bookmark description
			log.info("INFO: Enter a description for the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkDescription).type(Data.getData().BookmarkDesc);
			
			//enter a bookmark tag
			log.info("INFO: Enter a tag for the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkTag).type(Data.getData().BookmarkTag);
			
			//click the Save button
			log.info("INFO: Click the Save button");
			driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();
										
			//run command to index Communities now
			log.info("INFO: Index communities by running indexNow command");
			try {
				gsui.indexNow(serverURL, adminService, Data.getData().BookmarkTag, "communities", testUser, adminUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//logout the user 
			ui.logout();
			ui.close(cfg);
			
			//load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
									
			//login as Owner
			ui.login(testUser);
						
			//navigate to the community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//click on the bookmark tag located in the Tags widget
			log.info("INFO: Click on the Bookmark tag located in the Tags widget");
			driver.getFirstElement(CommunitiesUIConstants.overviewPageTagInCloud).click();
			
			//verify the search results page displays
			log.info("INFO: Verify the search results page displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().communitySearchResults));
			
			//verify the bookmark appears on the Bookmarks tab
			log.info("INFO: Verify the bookmark appears on the Bookmarks tab");
			Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
					"ERROR: Bookmark does not appear on the Bookmarks tab ");

			//click on the All Connections Content tab
			log.info("INFO: Click on the All Connections Content tab");
			ui.clickLinkWait(CommunitiesUIConstants.commSearchResultsPageTabs+":contains(" + Data.getData() .searchAllConnectionsContentTab +")");
			
			//verify the bookmark appears on the All Connections Content tab
			log.info("INFO: Verify the bookmark appears on the Bookmarks tab");
			Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
					"ERROR: Bookmark does not appear on the All Connections Content tab ");
									
			//delete community
			log.info("INFO: Removing community");
			community.delete(ui, testUser);
				
			ui.endTest();
			
			
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> TAGS: VERIFY TAGS IN COMMUNITIES - TAGS WIDGET WITH CONTENT (Test 3)</li>
	 * <li><B>Info:</B> Do a This Community search & verify the bookmark appears on the search tabs  </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> From the Overview page add a bookmark with a tag </li>
	 * <li><B>Step:</B> Index communities </li>
	 * <li><B>Step:</B> Log out & log back into communities </li>
	 * <li><B>Step:</B> Open the community </li>
	 * <li><B>Step:</B> Enter the bookmark tag into the This Community search field </li>
	 * <li><B>Verify:</B> Verify the Search Results page displays </li>
	 * <li><B>Verify:</B> Verify the bookmark tag appears on the default tab All Connections Content</li>
	 * <li><B>Step:</B> Click on the Bookmarks tab </li>
	 * <li><B>Verify:</B> Verify the bookmark tag appears on the Bookmarks tab </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/3818FC71C327D00685257E280047AF5C">TAGS: VERIFY TAGS IN COMMUNITIES - TAGS WIDGET WITH CONTENT</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression"} , enabled=false )
	public void overviewTagsThisCommunitySearchResults(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Tags widget on Overview page " + testName)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
									
			//login as Owner
			ui.login(testUser);
						
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//click on the Bookmark link Add Your First Bookmark
			log.info("INFO: Click on the Bookmark link Add Your First Bookmark");
			driver.getFirstElement(CommunitiesUIConstants.AddYourFirsBookMark).click();
			
			//enter a bookmark URL
			log.info("INFO: Enter a bookmark URL");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);
			
			//enter a bookmark name
			log.info("INFO: Enter a bookmark name");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);
			
			//enter a bookmark description
			log.info("INFO: Enter a description for the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkDescription).type(Data.getData().BookmarkDesc);
			
			//enter a bookmark tag
			log.info("INFO: Enter a tag for the bookmark");
			driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkTag).type(Data.getData().BookmarkTag);
			
			//click the Save button
			log.info("INFO: Click the Save button");
			driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();
										
			//run command to index Communities now
			log.info("INFO: Index communities by running indexNow command");
			try {
				gsui.indexNow(serverURL, adminService, Data.getData().BookmarkTag, "communities", testUser, adminUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//logout the user 
			ui.logout();
			ui.close(cfg);
			
			//load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
									
			//login as Owner
			ui.login(testUser);
						
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//Click on the Search icon
			log.info("INFO: Click on the search icon");
			ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);

			//Wait for the search text area to display 
			log.info("INFO: Wait for the search text area to display");
			ui.fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);

			//Enter a word to search on
			log.info("INFO: Enter a word to search on");
			ui.typeText(GlobalsearchUI.TextAreaInPanel, Data.getData().BookmarkTag);

			//Click on 'This Community'
			log.info("INFO: Click on 'This Community'");
			ui.clickLinkWait(CommunitiesUIConstants.thisCommunitySearchLink);
			
			//On the communities search results page verify the widget title change is reflected on the widget tab
			Assert.assertTrue(driver.isTextPresent(Data.getData().BookmarkTag),
					"ERROR: New widget title does not appear on the search results page widget tab");
						
			//click on the Bookmarks tab
			log.info("INFO: Click on the Bookmarks tab");
			ui.clickLinkWait(CommunitiesUIConstants.commSearchResultsPageTabs+":contains(" + Data.getData() .appBookmarks +")");
			
			//verify the bookmark appears on the Bookmarks tab
			log.info("INFO: Verify the bookmark appears on the Bookmarks tab");
			Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
					"ERROR: Bookmark does not appear on the Bookmarks tab ");

			//click on the All Connections Content tab
			log.info("INFO: Click on the All Connections Content tab");
			ui.clickLinkWait(CommunitiesUIConstants.commSearchResultsPageTabs+":contains(" + Data.getData() .searchAllConnectionsContentTab +")");
			
			//verify the bookmark appears on the All Connections Content tab
			log.info("INFO: Verify the bookmark appears on the Bookmarks tab");
			Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
					"ERROR: Bookmark does not appear on the All Connections Content tab ");
			
			//delete community
			log.info("INFO: Removing community");
			community.delete(ui, testUser);
				
			ui.endTest();
			
			
	}
	
	/**
	*<ul>
	*<li><B>Test Scenario:</B> FEEDS WIDGET PART1: VERIFY CREATING NEW FEEDS (Test 1)</li>
	*<li><B>Info:</B> Create and View Feed in Feeds widget page</li>
	*<li><B>Step:</B> [API] Create a public community with name, tag and description</li>
	*<li><B>Step:</B> Add a feed and include a tag and description</li>
	*<li><B>Verify:</B> The feed displays in the feeds widget fullpage</li>
	*<li><B>Verify:</B> Entry show title, person, date/time field, tags and more link are displayed</li>
	*<li><B>Step: </B> [API] Delete the community</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/0FF8E007348961D185257C8D0067F0CE">TTT-STATUS FEEDS WIDGET PART1: VERIFY CREATING NEW FEEDS</a></li>
	*</ul>
	*note: the cloud does not support this
	*/
	@Test(groups = {"regression"})
	public void createFeedAndViewFeedsWidget(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test creating new feeds " + testName)
													.build();

		BaseFeed feed = new BaseFeed.Builder(Data.getData().FeedsTitle + Helper.genDateBasedRandVal(), cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL)
									.description(Data.getData().commonDescription)
									.tags(Data.getData().MultiFeedsTag)
									.build();
		
		//create community
		logger.strongStep("Create community");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
					
		//add widget
		logger.strongStep("Add widget to the community");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FEEDS)) {
			log.info("INFO: Add Feeds widget to community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
				
	    // get the community link
	    String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
		//Navigate to owned communities
		logger.strongStep("Navigate to owned community views");
		log.info("INFO: Navigate to the owned communtiy views");
		ui.goToOwnerView(isCardView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
			
		//Click on the Feeds in left nav
		logger.strongStep("Click on 'feeds' from the left navigation menu");
		log.info("INFO: Select Feeds from left navigation menu");
		Community_LeftNav_Menu.FEEDS.select(ui);

		//Click Add Feed link
		logger.strongStep("Click on the 'Add Feeds' Link");
		log.info("INFO: Select add feed link");
		ui.clickLinkWait(FeedsUI.AddFeedLink);
		
		//Add the feed
		logger.strongStep("Add the feed to the community");
		log.info("INFO: Add the feed to the community");
		try {
			fUI.addFeed(feed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Test Feeds success message
		logger.weakStep("Validate that 'feed success' message is posted");
		log.info("Test that feeds success message is posted");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FeedSuccessMsg),
										"Error : Feeds success message is not shown properly");
		
		//Verify that the feed displays in widget full-page
		logger.weakStep("Validate that the feed is present in the widget page");
		log.info("INFO: Validate the feed is present");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + feed.getTitle()),
							"ERROR: Feed title is not displayed");
		
		//Verify the person who added the feed displays
		logger.weakStep("Verify that the person who added the feed is displayed");
		log.info("INFO: Verify the person who added the feed displays");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+testUser.getDisplayName()),
							"ERROR: Person who added the feed is not displayed");
		
		//Verify the Date field is displayed
		logger.weakStep("Verify that the date field is displayed");
		log.info("INFO: Verify the Date field is displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible(FeedsUI.FeedDate),
							"ERROR: Date field is not displayed");
		
		//Verify the Tags field appears with the 2 tags
		logger.weakStep("Verify that the tags field appears with 2 tags");
		log.info("INFO: Verify the news tag field appears");
		Assert.assertTrue(driver.getSingleElement(FeedsUI.FeedTags).getSingleElement("link="+Data.getData().MultiFeedsTag.split(" ")[0].trim()).isVisible(),
							"ERROR: news is not present as tag");
		
		logger.weakStep("Verify that the travel tag field appears");
		log.info("INFO: Verify the travel tag field appears");
		Assert.assertTrue(driver.getSingleElement(FeedsUI.FeedTags).getSingleElement("link="+Data.getData().MultiFeedsTag.split(" ")[1].trim()).isVisible(),
							"ERROR: travel is not present as tag");
		
		//Verify More is clickable link
		logger.weakStep("Verify that 'More' is a clickable link");
		log.info("INFO: Verify More is clickable link");
		Assert.assertTrue(ui.fluentWaitElementVisible(FeedsUI.feedsMoreLink),
							"ERROR: More is not clickable link");
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test verifies the correct links appear on the Tags widget action menu</li>
	 * <li><B>Step:</B> Create a public community using the API</li>
	 * <li><B>Step:</B> Log into Communities</li>
	 * <li><B>Step:</B> Open the public community</li>
	 * <li><B>Step:</B> Click on the Tags action menu icon</li>
	 * <li><B>Step:</B> Get the list of actions on the drop-down menu</li>
	 * <li><B>Step:</B> Compare the list of actual menu items with what is expected</li>
	 * <li><B>Verify:</B> The actual items match the listed of expected items</li>
	 * <li><B>Cleanup:</B> Delete the community</li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"})
	public void tagsWidgetActionsMenu(){
		
			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			String expectedWidgetMenuLinks[]={"Minimize","Refresh","Change Title","Move Down","Move To Previous Column","Move To Next Column","Hide","Remove"};
			BaseWidget widget=BaseWidget.TAGS;
			Vector<String> actualWidgetMenuText;
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Tags, on the Overview page, widget action menu links test " + testName )
										.build();
					
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			log.info("INFO: Log into communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//get list of widget action menu items
			log.info("INFO: get list of the widget action menu items");
			actualWidgetMenuText=ui.getWidgetActionMenuLinks(widget);
			
			//Compare the list of expected action menu items with the actual list of menu items
			log.info("INFO: Compare the list of expected action menu items with the actual menu items");
			Assert.assertTrue(compareWidgetActionLinks(actualWidgetMenuText,expectedWidgetMenuLinks),
					"ERROR: The expected menu items do not match the actual menu items");
			
			//Cleanup:Delete the community created in this test case
					log.info("INFO:Cleanup: Delete community ");
					//community.delete(ui, testUser);
					apiOwner.deleteCommunity(comAPI);

					ui.endTest();	
			}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test verifies the correct links appear on the Community Description widget action menu</li>
	 * <li><B>Step:</B> Create a public community using the API</li>
	 * <li><B>Step:</B> Log into Communities</li>
	 * <li><B>Step:</B> Open the public community</li>
	 * <li><B>Step:</B> Click on the Community Description action menu icon</li>
	 * <li><B>Step:</B> Get the list of actions on the drop-down menu</li>
	 * <li><B>Step:</B> Compare the list of actual menu items with what is expected</li>
	 * <li><B>Verify:</B> The actual items match the listed of expected items</li>
	 * <li><B>Cleanup:</B> Delete the community</li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"})
	public void commDescriptionWidgetActionMenuLinks(){
		
			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			String expectedWidgetMenuLinks[]={"Minimize","Refresh","Edit","Change Title","Help","Move Down","Move To Previous Column","Move To Next Column","Hide","Remove"};
			BaseWidget widget=BaseWidget.COMMUNITYDESCRIPTION;
			Vector<String> actualWidgetMenuText;
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Community Description widget action menu links test " + testName )
										.build();
					
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			log.info("INFO: Log into communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//get list of widget action menu items
			log.info("INFO: get list of the widget action menu items");
			actualWidgetMenuText=ui.getWidgetActionMenuLinks(widget);
			
			//Compare the list of expected action menu items with the actual list of menu items
			log.info("INFO: Compare the list of expected action menu items with the actual menu items");
			Assert.assertTrue(compareWidgetActionLinks(actualWidgetMenuText,expectedWidgetMenuLinks),
					"ERROR: The expected menu items do not match the actual menu items");
			
			//Cleanup:Delete the community created in this test case
			log.info("INFO:Cleanup: Delete community ");
			//community.delete(ui, testUser);
			apiOwner.deleteCommunity(comAPI);

			ui.endTest();	
			}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test verifies the correct links appear on the Important Bookmarks widget action menu</li>
	 * <li><B>Step:</B> Create a public community using the API</li>
	 * <li><B>Step:</B> Log into Communities</li>
	 * <li><B>Step:</B> Open the public community</li>
	 * <li><B>Step:</B> Click on the Important Bookmarks action menu icon</li>
	 * <li><B>Step:</B> Get the list of actions on the drop-down menu</li>
	 * <li><B>Step:</B> Compare the list of actual menu items with what is expected</li>
	 * <li><B>Verify:</B> The actual items match the listed of expected items</li>
	 * <li><B>Cleanup:</B> Delete the community</li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"})
	public void importantBkmksWidgetActionMenuLinks(){
		
			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			String expectedWidgetMenuLinks[]={"Minimize","Refresh","Change Title","Help","Move Down","Move To Previous Column","Move To Next Column","Hide","Remove"};
			BaseWidget widget=BaseWidget.IMPORTANTBOOKMARKS;
			Vector<String> actualWidgetMenuText;
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Important Bookmarks widget action menu links test " + testName )
										.build();
					
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			log.info("INFO: Log into communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//get list of widget action menu items
			log.info("INFO: get list of the widget action menu items");
			actualWidgetMenuText=ui.getWidgetActionMenuLinks(widget);
			
			//Compare the list of expected action menu items with the actual list of menu items
			log.info("INFO: Compare the list of expected action menu items with the actual menu items");
			Assert.assertTrue(compareWidgetActionLinks(actualWidgetMenuText,expectedWidgetMenuLinks),
					"ERROR: The expected menu items do not match the actual menu items");
			
			//Cleanup:Delete the community created in this test case
					log.info("INFO:Cleanup: Delete community ");
					community.delete(ui, testUser);

					ui.endTest();	
			}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test verifies the correct links appear on the Members summary widget action menu</li>
	 * <li><B>Step:</B> Create a public community using the API</li>
	 * <li><B>Step:</B> Log into Communities</li>
	 * <li><B>Step:</B> Open the public community</li>
	 * <li><B>Step:</B> Click on the Members summary widget action menu icon</li>
	 * <li><B>Step:</B> Get the list of actions on the drop-down menu</li>
	 * <li><B>Step:</B> Compare the list of actual menu items with what is expected</li>
	 * <li><B>Verify:</B> The actual items match the listed of expected items</li>
	 * <li><B>Cleanup:</B> Delete the community</li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"})
	public void membersWidgetActionMenuLinks(){
		
			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			String expectedWidgetMenuLinks[]={"Minimize","Refresh","Change Title","Help","Move Up","Move To Previous Column","Move To Next Column","Hide","Remove"};
			BaseWidget widget=BaseWidget.MEMBERS;
			Vector<String> actualWidgetMenuText;
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Members summary widget action menu links test " + testName )
										.build();
					
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			log.info("INFO: Log into communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//get list of widget action menu items
			log.info("INFO: get list of the widget action menu items");
			actualWidgetMenuText=ui.getWidgetActionMenuLinks(widget);
			
			//Compare the list of expected action menu items with the actual list of menu items
			log.info("INFO: Compare the list of expected action menu items with the actual menu items");
			Assert.assertTrue(compareWidgetActionLinks(actualWidgetMenuText,expectedWidgetMenuLinks),
					"ERROR: The expected menu items do not match the actual menu items");
			
			//Cleanup:Delete the community created in this test case
					log.info("INFO:Cleanup: Delete community ");
					//community.delete(ui, testUser);
					apiOwner.deleteCommunity(comAPI);

					ui.endTest();	
			}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test verifies the correct links appear on the Tags widget action menu</li>
	 * <li><B>Step:</B> Create a public community using the API</li>
	 * <li><B>Step:</B> Log into Communities</li>
	 * <li><B>Step:</B> Open the public community</li>
	 * <li><B>Step:</B> Click on the Tags action menu</li>
	 * <li><B>Step:</B> Get the list of actions on the drop-down menu</li>
	 * <li><B>Step:</B> Compare the list of actual menu items with what is expected</li>
	 * <li><B>Verify:</B> The actual items match the listed of expected items</li>
	 * <li><B>Cleanup:</B> Delete the community</li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"})
	public void tagsWidgetActionMenuLinks(){
		
			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			String widgetMenuLinks[]={"Minimize","Refresh","Change Title","Move Down","Move To Previous Column","Move To Next Column","Hide","Remove"};
			BaseWidget widget=BaseWidget.TAGS;
			Vector<String> widgetMenuText;
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test community Tags widget action menu links " + testName )
										.build();
					
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//Load component and login
			log.info("INFO: Log into communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			//get list of widget action menu items
			log.info("INFO: get list of the widget action menu items");
			widgetMenuText=ui.getWidgetActionMenuLinks(widget);
			
			//Compare the list of expected action menu items with the actual list of menu items
			log.info("INFO: Compare the list of expected action menu items with the actual menu items");
			Assert.assertTrue(compareWidgetActionLinks(widgetMenuText,widgetMenuLinks),
					"ERROR: The expected menu items do not match the actual menu items");
			
			//Cleanup:Delete the community created in this test case
					log.info("INFO:Cleanup: Delete community ");
					//community.delete(ui, testUser);
					apiOwner.deleteCommunity(comAPI);

					ui.endTest();	
			}
			

	
	/**
	 * compareWidgetActionLinks - this compares the links listed on the widget action menu with the expected menu links
	 */

	public boolean compareWidgetActionLinks(Vector<String> actualLinks, String[] expectedLinks) {
		boolean match = false;

		//Make a copy of both the actual menu links 
		Vector<String> copyActualLinks = new Vector<String>(actualLinks);

		//Make an empty vector for the expected menu links
		Vector<String> copyExpectedLinks = new Vector<String>();

		//Remove any blank lines from the list of actual menu links
		while (copyActualLinks.contains(""))
		{copyActualLinks.remove("");}

		//Placing the list of actual menu links into the empty vector created above
		for (int x = 0; x < expectedLinks.length; x++) {
			copyExpectedLinks.add(expectedLinks[x]);
		}

		//Compare the list of expected menu links with the actual menu links
		matchLoop: {
			//make sure the size of the (2) lists are equal
			if (copyActualLinks.size() != copyExpectedLinks.size())
			{log.info("INFO: Menu has " + copyActualLinks.size() + " items, but expected " + copyExpectedLinks.size() + " items ");
				break matchLoop; }
			//Remove the expected menu links from the list of actual links
			copyActualLinks.removeAll(copyExpectedLinks);

			//If the list of actual links is empty then the actual & expected link lists match
			if (!copyActualLinks.isEmpty())
				{
				log.info("INFO: Actual menu items do not match what is expected. ");
				for (int x = 0; x < copyActualLinks.size(); x++) {
					log.info("INFO: Unexpected item on the actual menu: " + copyActualLinks.elementAt(x));
				}
				copyExpectedLinks.removeAll(actualLinks);
				log.info("INFO: Expected menu items do not match the actual menu items. ");
				for (int x = 0; x < copyExpectedLinks.size(); x++) {
					log.info("INFO: Extra item " + copyExpectedLinks.elementAt(x)+ " found on the expected list ");
				}
				break matchLoop;
				}
			match = true;

		} // end of matchloop

		log.info("INFO: The list of actual menu links match the expected menu links: " + match);

		return match;
	}
	
	
	
	}
