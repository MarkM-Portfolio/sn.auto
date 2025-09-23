package com.ibm.conn.auto.tests.communities.regression;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.data.metricsData;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.MetricsUI;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityTabbedNav extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(CommunityTabbedNav.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private boolean isOnPremise;
	private Community comAPI1,comAPI2,comAPI3,comAPI4,comAPI5,comAPI6,comAPI7,comAPI8,comAPI9,comAPI10;
	private BaseCommunity community1,community2,community3,community4,community5,community6,community7,community8,community9,community10;
	private String gk_flag;
	private boolean s;
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
				
		//Test communities
		community1 = new BaseCommunity.Builder("topNavAddAllApps" + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)
		                              .description("Testing top nav with all apps added")
		                              .build();
		
		community2 = new BaseCommunity.Builder("topNavDefaultApps" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Testing top nav with default apps only")
                                      .build();
		
		community3 = new BaseCommunity.Builder("topNavDefaultAppsClickAppTabs" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Testing ability to click on each of the default apps on top nav")
                                      .build();
		
		community4 = new BaseCommunity.Builder("topNavDefaultAppsSubcomm" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Make sure default apps appear on the top nav for a subcommunity")
                                      .build();
		
		community5 = new BaseCommunity.Builder("topNavHideUnhideWidget" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Test top nav after hiding & unhiding a widget")
                                      .build();
		
		community6 = new BaseCommunity.Builder("topNavMoveAppToLeftColumn" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Test top nav after moving an app to another column")
                                      .build();
		
		community7 = new BaseCommunity.Builder("topNavRemoveWidget" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Test top nav after removing an app")
                                      .build();
		
		community8 = new BaseCommunity.Builder("topNavRenameWidget" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Test top nav after renaming an app")
                                      .build();
		
		community9 = new BaseCommunity.Builder("topNavSubcommDropdownMenu" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Test the subcommunity dropdown menu")
                                      .build();
		
		community10 = new BaseCommunity.Builder("topNavSubcommMenu" + Helper.genDateBasedRandVal())
                                       .access(Access.PUBLIC)
                                       .description("Make sure newly created community has no subcomm menu & menu appears after adding a subcomm")
                                       .build();		
		
		//create test communities using API
		log.info("INFO: create communities via the API");
		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);
		comAPI3 = community3.createAPI(apiOwner);
		comAPI4 = community4.createAPI(apiOwner);
		comAPI5 = community5.createAPI(apiOwner);
		comAPI6 = community6.createAPI(apiOwner);
		comAPI7 = community7.createAPI(apiOwner);
		comAPI8 = community8.createAPI(apiOwner);
		comAPI9 = community9.createAPI(apiOwner);
		comAPI10 = community10.createAPI(apiOwner);
		
		//Check to see if the Gatekeeper setting for tabbed nav is enabled or not 
		//if GK setting is False the test is skipped
		gk_flag = Data.getData().commTabbedNav;
		
		log.info("INFO: Get the UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//NOTE: Using deprecated GK code because it works both on-prem & on the cloud.  Other code does not work on the cloud.   
		//OK per automation team to use deprecated version of GK
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		s = gkc.getSetting(gk_flag);
		
		ui.logout();
		driver.close();
								
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
		apiOwner.deleteCommunity(comAPI3);
		apiOwner.deleteCommunity(comAPI4);
		apiOwner.deleteCommunity(comAPI5);
		apiOwner.deleteCommunity(comAPI6);
		apiOwner.deleteCommunity(comAPI7);
		apiOwner.deleteCommunity(comAPI8);
		apiOwner.deleteCommunity(comAPI9);
		apiOwner.deleteCommunity(comAPI10);
	}
	
	
	
	  /**
   	 * <ul>
   	 * <li><B>Test Scenario:</B> Tabbed Navigation: All Apps Added
   	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
   	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li>
   	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
   	 * <li><B>Info:</B> This test will verify when all apps are added that each widget tab displays in the correct order</li>
   	 * <li><B>Step:</B> Create a public community using the API </li>
   	 * <li><B>Step:</B> Log into Communities</li>
   	 * <li><B>Step:</B> Open the community</li>
   	 * <li><B>Step:</B> Add all the widgets to the community
   	 * <li><B>Verify:</B> Each of the apps appear on the tabbed nav menu in the correct order </li>
   	 * <li><B>Cleanup:</B> Delete the community </li> 
   	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
   	 *</ul>
   	 */
    //test case is marked as disabled because library api is not supported from connection 6.5 onwards)
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void topNavAddAllApps()  {
				
		Vector<String> topNavMenuItems = new Vector<String>(16);
		Vector<String> expectedOnPremItems = new Vector<String>(16);
		Vector<String> expectedOnCloudItems = new Vector<String>(16);

		//On-premise: list of apps that should appear on top nav when all apps have been added
		expectedOnPremItems.add(Data.getData().overview.toUpperCase()); 
		expectedOnPremItems.add(Data.getData().appRecentUpdates.toUpperCase());
		expectedOnPremItems.add(Data.getData().appStatusUpdates.toUpperCase());
		expectedOnPremItems.add(Data.getData().appMembersSummary.toUpperCase());
		expectedOnPremItems.add(Data.getData().appForums.toUpperCase());
		expectedOnPremItems.add(Data.getData().appBookmarks.toUpperCase());
		expectedOnPremItems.add(Data.getData().appFiles.toUpperCase());
		expectedOnPremItems.add(Data.getData().appBlog.toUpperCase());
		expectedOnPremItems.add(Data.getData().appIdeationBlog.toUpperCase());
		expectedOnPremItems.add(Data.getData().appActivities.toUpperCase());
		expectedOnPremItems.add(Data.getData().appFeeds.toUpperCase());
		expectedOnPremItems.add(Data.getData().appWiki.toUpperCase());
	//	expectedOnPremItems.add(Data.getData().appLibrary.toUpperCase());
		expectedOnPremItems.add(Data.getData().appEvents.toUpperCase());
		expectedOnPremItems.add(Data.getData().appRelatedCommunities.toUpperCase());
		expectedOnPremItems.add(Data.getData().appMetrics.toUpperCase());

		//Cloud: list of apps that should appear on top nav when all apps have been added 
		expectedOnCloudItems.add(Data.getData().overview.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appRecentUpdates.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appStatusUpdates.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appMembersSummary.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appForums.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appBookmarks.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appFiles.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appBlog.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appIdeationBlog.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appActivities.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appSurvey.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appWiki.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appEvents.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appRelatedCommunities.toUpperCase());
		expectedOnCloudItems.add(Data.getData().appMetrics.toUpperCase());

		log.info("INFO: Get the UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);

   			ui.loadComponent(Data.getData().ComponentCommunities);
   			ui.login(testUser);
   			   			   							
   			if(s)
   			{
   				log.info("INFO: Navigate to the community using UUID");
   				community1.navViaUUID(ui);

   				log.info("INFO: Add Blog widget using API");
   				community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.BLOG);

   				log.info("INFO: Add Ideation Blog widget using API");
   				community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.IDEATION_BLOG);

   				log.info("INFO: Add Activities widget using API");
   				community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.ACTIVITIES);

   				log.info("INFO: Add Events widget using API");
   				community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.EVENTS);

   				log.info("INFO: Add Related Communities widget using API");
   				community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.RELATED_COMMUNITIES);

   				if(isOnPremise){
   					log.info("INFO: Environment is on-premise");
   					log.info("INFO: Add Feeds widget using API");
   					community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.FEEDS);	

   					log.info("INFO: Add Wiki widget using API");
   					community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.WIKI);
   				
   				    log.info("INFO: Add Library widget using API"); 
   					community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.LIBRARY);


   				}else{
   					log.info("INFO: Environment is cloud");
   					log.info("INFO: Add Surveys widget using API");
   					community1.addWidgetAPI(comAPI1, apiOwner, BaseWidget.SURVEYS);

   				};	

   				UIEvents.refreshPage(driver);

   				log.info("INFO: Create a list of items that appear on the top nav - unsorted");
   				topNavMenuItems.addAll(ui.getTopNavItems(false));

   				//NOTE: The id for Overview & Metrics do not have the same pattern (..._navItem) as the other widgets
   				//Overview gets added to the list of apps as part of the getTopNavItems method. 
   				//Metrics: if Metrics appears on the More link menu, it will get added as part of the getTopNavItems method.
   				//If there is no More link, Metrics will need to be added to the list of top nav items using tabbedNavMetricsTab selector

   				if(!driver.isElementPresent(CommunitiesUIConstants.tabbedNavMoreTab)){
   					log.info("INFO: Add Metrics to the list of top nav menu items");
   					topNavMenuItems.add(driver.getFirstElement(CommunitiesUIConstants.tabbedNavMetricsTab).getText());
   				}

   				Vector<String> expectList=new Vector<String>(16);
   				if (isOnPremise){
   					expectList.addAll(expectedOnPremItems);
   				}
   				else {
   					expectList.addAll(expectedOnCloudItems);

   				}   				

   				// Sort the two lists to make the comparison easier to do
   				Collections.sort(expectList);
   				Collections.sort(topNavMenuItems);

   				log.info("INFO: Number of Expected Top Nav Items = " + expectList.size());
   				for (String expected:expectList)
   				{
   					log.info("INFO: Expected Top Nav Items: "+ expected);
   				}

   				log.info("INFO: Number of Actual Top Nav Items = " + topNavMenuItems.size());
   				for (String actual:topNavMenuItems)
   				{
   					log.info("INFO: Actual Top Nav Items: "+ actual);
   				}

   				log.info("INFO: Verify the number of entries in the expected list is the same as the actual list");
   				Assert.assertEquals(expectList.size(), topNavMenuItems.size(),
   						"ERROR: The number of entries in the expected & actual tabs list does not match");

   				log.info("INFO: compare the list of expected & actual widgets to make sure they contain the same items");
   				for(int index=0;index<expectList.size();index++)
   				{
   					log.info("INFO: Comparing the actual tab " + topNavMenuItems.get(index)+
   							" with the expected " + expectList.get(index));
   					Assert.assertEquals(expectList.get(index).toLowerCase(),topNavMenuItems.get(index).toLowerCase(),
   							"ERROR: Mis-match in list of widgets ");
   				}

   				log.info("INFO: The menu lists match!");

   			}
   			else 

   			{   	   				
   				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");   				   	   				 
   			}


   			ui.endTest();
   					
   			
       }
    
    /**
  	 * <ul>
  	 * <li><B>Test Scenario:</B> Tabbed Navigation: Default Apps</li>
  	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li>
     * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
  	 * <li><B>Info:</B> This test will verify that the default apps appear on the tabbed nav. menu</li>
  	 * <li><B>Step:</B> Create a public community using the API </li>
  	 * <li><B>Step:</B> Log into Communities</li>
  	 * <li><B>Step:</B> Open the community</li>
  	 * <li><B>Verify:</B> Each of the default apps appear on the tabbed nav menu - Overview, Recent Updates, Status Updates, Members, Forums, Bookmarks, Files, Metrics</li>
  	 * <li><B>Verify:</B> If test is run on the cloud, Wiki should appear on the tabbed nav menu by default</li>
  	 * <li><B>Cleanup:</B> Delete the community </li> 
  	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
  	 *</ul>
  	 */
      
      @Test(groups = {"regression", "regressioncloud"} , enabled=false )
  	public void topNavDefaultApps()  {

    	  log.info("INFO: Get UUID of community");
    	  community2.getCommunityUUID_API(apiOwner, comAPI2);

    	  ui.loadComponent(Data.getData().ComponentCommunities);
    	  ui.login(testUser);

    	  if (s)
    	  {
    		  log.info("INFO: Navigate to the community using UUID");
    		  community2.navViaUUID(ui);	

    		  log.info("INFO: Verify Overview appears on the tabbed nav menu");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
    				  "ERROR : Overview link does not appear on the tabbed nav menu");

    		  log.info("INFO: Verify Metrics appears on the tabbed nav menu");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavMetricsTab),
    				  "ERROR: Metrics link does not appear on the tabbed nav menu");

    		  log.info("INFO: Verify the default widgets appear on the tabbed nav menu");
    		  Assert.assertTrue(ui.presenceOfDefaultWidgetsOnTopNav(),
    				  "ERROR: Widgets on the top nav are not correct");

    	  }		

    	  else 
    	  {
    		  log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

    	  } 

    	  ui.endTest();


      }

      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Default Apps - click on each tab to make sure full widget page displays</li> 
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li>	 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> This test will verify the full widget page displays when each default app tab is selected</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Click on each of the default app tabs</li>
     	 * <li><B>Verify:</B> The full widget page displays</li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */   
      
      @Test(groups = {"regression", "regressioncloud"} , enabled=false )
     	public void topNavDefaultAppsClickAppTabs()  {
     			     			
    	  log.info("INFO: Get UUID of community");
    	  community3.getCommunityUUID_API(apiOwner, comAPI3);

    	  ui.loadComponent(Data.getData().ComponentCommunities);
    	  ui.login(testUser);

    	  if (s)
    	  {    	  			        			
    		  log.info("INFO: Navigate to the community using UUID");
    		  community3.navViaUUID(ui);	

    		  //*** This section will make sure clicking on each of the default app tabs opens the full widget page

    		  log.info("INFO: Click on the Recent Updates tab");
    		  Community_TabbedNav_Menu.RECENT_UPDATES .select(ui);

    		  log.info("INFO: Verify the Recent Updates full page displays - check for the 'Recent Updates' header");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.recentUpdatesHeader),
    				  "ERROR: The full Recent Updates page did not display");

    		  log.info("INFO: Click on the Status Updates tab");
    		  Community_TabbedNav_Menu.STATUSUPDATES .select(ui);

    		  log.info("INFO: Verify the Status Updates full page displays - check for the 'Status Updates' header");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.statusUpdatesHeader),
    				  "ERROR: The full Status Updates page did not display");

    		  log.info("INFO: Click on the Members tab");
    		  Community_TabbedNav_Menu.MEMBERS .select(ui);

    		  log.info("INFO: Verify the Members full page displays - check for the 'Add Members' button");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
    				  "ERROR: The full Members page did not display");

    		  log.info("INFO: Click on the Forums tab");
    		  Community_TabbedNav_Menu.FORUMS .select(ui);

    		  log.info("INFO: Verify the Forums full page displays - check for the 'Forums' header");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StartATopicLink),
    				  "ERROR: The full Forums page did not display");

    		  log.info("INFO: Click on the Bookmarks tab");
    		  Community_TabbedNav_Menu.BOOKMARK .select(ui);

    		  log.info("INFO: Verify the Bookmarks full page displays - check for the 'Bookmarks' header");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddBookmarkButton),
    				  "ERROR: The full Bookmarks page did not display");

    		  log.info("INFO: Click on the Files tab");
    		  Community_TabbedNav_Menu.FILES .select(ui);

    		  log.info("INFO: Verify the Files full page displays - check for the 'Files' header");
    		  Assert.assertTrue(driver.isElementPresent(FilesUIConstants.navCommunityFiles),
    				  "ERROR: The full Files page did not display"); 

    		  if(!isOnPremise){
    			  log.info("INFO: Click on the Wiki tab");
    			  Community_TabbedNav_Menu.WIKI .select(ui);

    			  log.info("INFO: Verify the Wiki full page displays - check for 'Welcome to' header");
    			  Assert.assertTrue(driver.isElementPresent(WikisUIConstants.welcomeTo),
    					  "ERROR: The full Wiki page did not display");

    			  log.info("INFO: Click on the Metrics tab");
    			  Community_TabbedNav_Menu.METRICS .select(ui);

    			  log.info("INFO: Validate Metrics full page displays");       		  
    			  Assert.assertTrue(driver.isElementPresent(MetricsUI.participationDropDown_Updates_SC),
    					  "ERROR: Participation page DropDown list does not load");
    		  }else {

    		  log.info("INFO: Click on the Metrics tab");
    		  Community_TabbedNav_Menu.METRICS .select(ui);

    		  log.info("INFO: Verify the Metrics full page displays - check for 'Participation' view");
    		  Assert.assertTrue(driver.isTextPresent(metricsData.ParticipationView),
    				  "ERROR: The full Metrics page did not display");
    		  }
    		  
    		  log.info("INFO: Click on the Overview tab");
    		  Community_TabbedNav_Menu.OVERVIEW .select(ui);

    		  log.info ("INFO: Validate the user is on the Overview page");
    		  Assert.assertEquals(driver.getTitle(),"Overview - "+ community3.getName(),
    				  "ERROR: User is not on the Overview page");

    	  }



    	  else
    	  {
    		  log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");
    	  } 

    	  ui.endTest();


      }
	

      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Hide & Unhide Widget</li>
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li> 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> When a widget is hidden the widget does not appear on the tabbed nav menu, when re-added the widget will appear again</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Hide the Files widget</li>
     	 * <li><B>Step:</B> Check for the GK setting to show hidden apps in the tabbed nav, if enabled file will appear on tabbed nav</li>
     	 * <li><B>Verify:</B> If show hidden app GK setting is enabled, Files will appear on tabbed nav; otherwise, it will not appear</li>
     	 * <li><B>Verify:</B> Files summary widget does not appear on the Overview page</li>
     	 * <li><B>Step:</B> Unhide the Files widget</li>
     	 * <li><B>Verify:</B> Files appears on the tabbed nav menu</li>
     	 * <li><B>Verify:</B> Files summary widget appears on the Overview page again</li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */   
      @Test(groups = {"regression", "regressioncloud"} , enabled=false )
     	public void topNavHideUnhideWidget() {

    	  BaseWidget widget = BaseWidget.FILES;     			

    	  log.info("INFO: Get UUID of community");
    	  community5.getCommunityUUID_API(apiOwner, comAPI5);

    	  log.info("INFO: Log into Communities");
    	  ui.loadComponent(Data.getData().ComponentCommunities);
    	  ui.login(testUser);

    	  if (s)
    	  { 
    		  
    		  log.info("INFO: Navigate to the community using UUID");
    		  community5.navViaUUID(ui);	

    		  log.info("INFO: Remove Files widget");
    		  hide(widget);
    		  
    		  log.info("Check for GK setting to show hidden apps in the tabbed nav, if enabled hidden app should appear on top nav");
    		  if(ui.checkGKSetting(Data.getData().gk_showHiddenAppInTopNav_flag)){

    			  log.info("INFO: Verify Files widget appears on the top nav");
    			  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavFilesTab),
    					  "ERROR : Files widget does not appear on the top nav, but should");
    		  }else {

    			  log.info("INFO: Verify Files widget does not appear on the top nav");
    			  Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.tabbedNavFilesTab),
    					  "ERROR : Files widget appears on the top nav");
    		  }
    		  
    		  log.info("INFO: Verify the Files summary widget no longer appears on the Overview page");
    		  Assert.assertFalse(driver.isElementPresent(FilesUIConstants.ComFilesOverviewAddFirstFile_Button),
    				  "ERROR: Files summary widget appears on the Overview page - should be hidden");
    		  
    		  log.info("Add hidden widget: "+ widget.getTitle());
    		  ui.addHiddenWidget(widget);    		  
    		  
    		  log.info("INFO: Verify Files widget appears on the top nav");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavFilesTab),
    				  "ERROR : Files tab does not appear on the top nav");
    		  
    		  log.info("INFO: Verify the Files summary widget re-appears on the Overview page");
    		  Assert.assertTrue(driver.isElementPresent(FilesUIConstants.ComFilesOverviewAddFirstFile_Button),
    				  "ERROR: Files summary widget does not appear on the Overview page, but should");


    	  }
    	  else
    	  {
    		  log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

    	  } 

    	  ui.endTest();

      }
      
      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Move widget to a different column</li>
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li> 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> Move a widget to a different column, widget should display in the correct location on the tabbed nav menu</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Move the Events widget to the left narrow column</li>
     	 * <li><B>Verify:</B> Events appears in the left (narrow) column</li>
     	 * <li><B>Step:</B> Log out & then back into communities to refresh the tabbed nav menu</li>
     	 * <li><B>Verify:</B> Events widget appears in the correct location on the tabbed nav menu</li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */   
    //Need to work on this https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2367 before enable this test
      @Test(groups = {"regression", "regressioncloud"},enabled=false)
      public void topNavMoveAppToLeftColumn()  {
    	  
    	  DefectLogger logger=dlog.get(Thread.currentThread().getId());

    	  int columnNum = 0;

    	  //These Vectors will store the list of menu items before & after adding the "Events" widget
    	  Vector<String> topMenuBeforeAdd = new Vector<String>();
    	  Vector<String> topMenuAfterAdd = new Vector<String>();

    	  BaseWidget widget=BaseWidget.EVENTS; 
    	  Widget_Action_Menu action=Widget_Action_Menu.MOVETOPREVIOUSCOLUMN;

    	  log.info("INFO: Get UUID of community");
    	  community6.getCommunityUUID_API(apiOwner, comAPI6);

    	  log.info("INFO: Log into Communities");
    	  ui.loadComponent(Data.getData().ComponentCommunities);
    	  ui.login(testUser);
    	
    	  logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
          Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

          if (flag) {
        	  apiOwner.editStartPage(comAPI6, StartPageApi.OVERVIEW);
          }
    	  
    	  if (s)
    	  {
    		  log.info("INFO: Navigate to the community using UUID");
    		  community6.navViaUUID(ui);	

    		  //Collect the visible widgets on the top nav before adding the Events widget - Overview & Metrics are not included in the list of widgets
    		  //the 'id' for Overview & Metrics do not contain '_navItem' which is how the remaining widgets are selected
    		
    		  //Collect the visible widgets from the top nav menu
 				topMenuBeforeAdd.addAll(ui.getTopNavItems(false));
 				
 				if((driver.getVisibleElements(CommunitiesUIConstants.tabbedNavMoreTab).size())==0){
 					topMenuBeforeAdd.add(Data.getData().appMetrics);
 				}
 					
 				
    		  log.info("INFO: Number of top nav tabs before adding Events = " + topMenuBeforeAdd.size());

    		  log.info("INFO: List the widgets on the top nav before adding Events ");
    		  for(String widgetName : topMenuBeforeAdd){
    			  log.info("INFO: Widget " + widgetName);

    		  }
    		  	  

    		  log.info("INFO: Select Add Apps from the community action menu & select Events");
    		  Com_Action_Menu.ADDAPP.select(ui);		
    		  ui.clickLinkWait(CommunitiesUIConstants.leftNavEvents);
    		      	
    		  //driver.clickAt(0,0);

    		  log.info("INFO: Close the Add Apps palette");
    		  //Added this step as the add menu popup was not getting closed with click 
    		  ui.clickLinkWithJavascript(CommunitiesUIConstants.WidgetSectionClose);

    		  log.info("INFO: Click on the action Move to Previous Column on the Events action menu");
    		  ui.performCommWidgetAction(widget, action);
    		  
    		  log.info("INFO: Click on the action Move to Previous Column on the Events action menu again so widget is in left column");
    		  ui.performCommWidgetAction(widget, action);
    		  
    		  log.info("INFO: Get the column number the Events widget is located in");
    		  columnNum = ui.getWidgetLocationInfo(widget,true);

    		  log.info("INFO: Verify the Events widget appears in left column (col 1)");
    		  Assert.assertEquals(columnNum, 1,
    				  "ERROR: Events widget does not appear in left column (col 1).  Expected column 1, but found in column " + columnNum);

    		  UIEvents.refreshPage(driver);

    		  //Collect the visible widget on the top nav after adding the Events widget
    		  topMenuAfterAdd.addAll(ui.getTopNavItems(false));
    		  
    		  if((driver.getVisibleElements(CommunitiesUIConstants.tabbedNavMoreTab).size())==0){
					topMenuAfterAdd.add(Data.getData().appMetrics);
				}
				
    		  log.info("INFO: Number of top nav tabs after adding Events = " + topMenuAfterAdd.size());

    		  log.info("INFO: List the widgets on the top nav after adding Events");
    		  for(String widgetName : topMenuAfterAdd){
    			  log.info("INFO: Widget " + widgetName);

    		  }

    		  //Add "Events" to the list of widgets that was retrieved before the Events widget was added
    		  //Adding "Events" so that it appears after the required widget "Members". Events should be 4th tab listed
    		  topMenuBeforeAdd.add(5,Data.getData().appEvents.toLowerCase());
    		  
    		  //Verify the number of entries in before widget add with the addition of Events & after widget add vectors are the same size
    		  Assert.assertEquals(topMenuBeforeAdd.size(), topMenuAfterAdd.size(),
    				 "ERROR: The list of widgets are not the same size");

    		  log.info("INFO: compare the list of widgets from before & after to make sure they match up");
    		  for(int index=0; index<topMenuBeforeAdd.size(); index++){
    			  log.info("INFO: Comparing before entry: " +  topMenuBeforeAdd.get(index).toLowerCase() + " after entry: " + topMenuAfterAdd.get(index).toLowerCase());
    			  Assert.assertEquals(topMenuBeforeAdd.get(index).toLowerCase(),topMenuAfterAdd.get(index).toLowerCase(),
    					  "ERROR: Mis-match in list of widgets ");
    		  }
    		
    		  log.info("INFO: The before & after menus match!");

    	  }

    	  else
    	  {
    		  log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

    	  } 
    	  ui.endTest();

      }

      
      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Remove Widget</li>
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li> 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> When a widget is removed, it should no longer appear on the tabbed nav menu</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Remove the Forums widget</li>
     	 * <li><B>Verify:</B> The Forums widget no longer appears on the tabbed nav menu</li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */      
      
      @Test(groups = {"regression", "regressioncloud"})
     	public void topNavRemoveWidget() {
    	  
    	  DefectLogger logger=dlog.get(Thread.currentThread().getId());

    	  BaseWidget widget = BaseWidget.FORUM;

    	  log.info("INFO: Get UUID of community");
    	  community7.getCommunityUUID_API(apiOwner, comAPI7);

    	  ui.loadComponent(Data.getData().ComponentCommunities);
    	  ui.login(testUser);
    	  
    	  logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
          Boolean flag = ui.isHighlightDefaultCommunityLandingPage();


          if (flag) {
        	  apiOwner.editStartPage(comAPI7, StartPageApi.OVERVIEW);
          }

    	  if (s)
    	  {
    		  log.info("INFO: Navigate to the community using UUID");
    		  community7.navViaUUID(ui);	

    		  log.info("INFO: Remove Forums widget");
    		  remove(widget);

    		  log.info("INFO: Verify Forums does not appear on the top nav");
    		  Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.leftNavForums),
    				  "ERROR : Forums link appears on the top nav");

    	  }
    	  else
    	  {
    		  log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

    	  } 

    	  ui.endTest();

      }
      
      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Rename Widget</li>
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li> 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> When a widget is renamed the name change should appear on the tabbed nav menu</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Rename the Bookmarks widget</li>
     	 * <li><B>Verify:</B> The name change is reflected on the tabbed nav menu</li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */     
      
      @Test(groups = {"regression", "regressioncloud"})
     	public void topNavRenameWidget()  {
    	  
    	  DefectLogger logger=dlog.get(Thread.currentThread().getId());

    	  String widgetName = "";

    	  BaseWidget widget=BaseWidget.BOOKMARKS;
    	  Widget_Action_Menu action=Widget_Action_Menu.CHANGETITLE;

    	  log.info("INFO: Get UUID of community");
    	  community8.getCommunityUUID_API(apiOwner, comAPI8);

    	  ui.loadComponent(Data.getData().ComponentCommunities);
    	  ui.login(testUser);
    	  
    	  logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
    	  Boolean flag = ui.isHighlightDefaultCommunityLandingPage();


    	  if (flag) {
    	  	apiOwner.editStartPage(comAPI8, StartPageApi.OVERVIEW);
    	  }

    	  if (s)
    	  {
    		  //Navigate to the community using the UUID
    		  log.info("INFO: Navigate to the community using UUID");
    		  community8.navViaUUID(ui);	

    		  log.info("INFO: get the widget title before title change");
    		  ui.getSummaryWidgetTitle(widget);

    		  log.info("INFO: Check Bookmarks action menu for Change Title");
    		  ui.checkWidgetMenuPresent(widget, action);

    		  log.info("INFO: Click on the action Change Title");
    		  ui.performCommWidgetAction(widget, action);

    		  log.info("INFO: Validate the Change Title dialog displays");
    		  Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.widgetChangeTitleDialog),
    				  "ERROR: The Change Title dialog did not display");

    		  log.info("INFO: Clear default widget title text");
    		  driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();

    		  log.info("INFO: Enter the new widget title");
    		  driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(Data.getData().ChangeBookmarkAppTitle);

    		  log.info("INFO: Save the widget title change");
    		  ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);

    		  log.info("INFO: Get the widget title after the title change. ");
    		  widgetName=ui.getSummaryWidgetTitle(widget);   			

    		  log.info("INFO: Get the edited widget tab name on the tabbed nav");
    		  ui.fluentWaitElementVisible("css=h2[class='widgetTitle'] span:contains("+widgetName+")");
    		  String widgetText = driver.getFirstElement("css=h2[class='widgetTitle'] span:contains("+widgetName+")").getText();

    		  log.info("INFO: Verify the new widget name displays correctly on the tabbed nav");
    		  Assert.assertEquals(widgetText, Data.getData().ChangeBookmarkAppTitle,
    				  "ERROR: New widget title does not appear on the tabbed nav menu");

    	  }
    	  else
    	  {
    		  log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

    	  } 

    	  ui.endTest();

      }
           
      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Subcommunity Drop-down menu</li>
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li> 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> This test covers the subcommunity dropdown menu</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Create a Public subcommunity</li>
     	 * <li><B>Step:</B> Return to the parent community & create a Moderated subcommunity</li> 
     	 * <li><B>Step:</B> Return to the parent community & create a Restricted subcommunity</li>
     	 * <li><B>Step:</B> Return to the parent community</li>
     	 * <li><B>Verify:</B> The subcommunity dropdown menu displays on the parent community overview page</li>
     	 * <li><B>Verify:</B> The subcommunity count is correct</li>
     	 * <li><B>Verify:</B> The subcommunity menu drop-down arrow displays</li>
     	 * <li><B>Step:</B> From the subcommunity drop-down menu select the Public subcommunity</li>
     	 * <li><B>Verify:</B> Parent comm name is a link, subcomm name displays & there is no subcomm drop-down menu</li>
     	 * <li><B>Step:</B> Return to the parent comm & select the moderated subcomm from the drop-down menu</li>
     	 * <li><B>Verify:</B> Parent comm name is a link, subcomm name displays & there is no subcomm drop-down menu</li>
     	 * <li><B>Step:</B> Return to the parent comm & select the restricted subcomm from the drop-down menu</li>
     	 * <li><B>Verify:</> Parent comm name is a link, subcomm name displays & there is no subcomm drop-down menu</li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */     
      
      @Test(groups = {"regression", "regressioncloud"})
  	public void topNavSubcommDropdownMenu() {
    	  
    	  DefectLogger logger=dlog.get(Thread.currentThread().getId());

    	  String testName = ui.startTest();
    	  String publicSubComm = "Public SubCommunity" + Helper.genDateBasedRand();
    	  String moderatedSubComm = "Moderated SubCommunity" + Helper.genDateBasedRand();
    	  String restrictedSubComm = "Restricted SubCommunity" + Helper.genDateBasedRand();
  			  			 		
  			BaseSubCommunity subCommunityPublic = new BaseSubCommunity.Builder(publicSubComm)
  			                                   .access(BaseSubCommunity.Access.PUBLIC)
  											   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
  											   .UseParentmembers(false)
  											   .description("Tabbed Nav - Public SUBcomm " + testName)
  											   .build();
  				
  			BaseSubCommunity subCommunityModerated = new BaseSubCommunity.Builder(moderatedSubComm)
  			                                   .access(BaseSubCommunity.Access.MODERATED)
  											   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
  											   .UseParentmembers(false)
  											   .description("Tabbed Nav - Moderated SUBcomm " + testName)
  											   .build();
  					
  			BaseSubCommunity subCommunityRestricted = new BaseSubCommunity.Builder(restrictedSubComm)
  			                                   .access(BaseSubCommunity.Access.RESTRICTED)
  											   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
  											   .UseParentmembers(false)
  											   .description("Tabbed Nav - Restricted SUBcomm " + testName)
  											   .build();
  			
  			 			
  			log.info("INFO: Get UUID of community");
  			community9.getCommunityUUID_API(apiOwner, comAPI9);

  			log.info("INFO: Log into Communities");
  			ui.loadComponent(Data.getData().ComponentCommunities);
  			ui.login(testUser);
  			
  			logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
  			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();


  			if (flag) {
  				apiOwner.editStartPage(comAPI9, StartPageApi.OVERVIEW);
  			}

  			if (s)
  			{
  				log.info("INFO: Navigate to the community using UUID");
  				community9.navViaUUID(ui);

  				log.info("INFO: Verify there is no Subcommunity field for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavHiddenSubcommField),
  						"ERROR: The Subcommunity field appears on the top nav");

  				log.info("INFO: Create the 1st subcommunity - public ");
  				subCommunityPublic.create(ui);	
  				
  		        if (flag) {
  		            apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
  		            Community communitycom = apiOwner.getCommunity(subCommunityPublic.getCommunityUUID());
  		            apiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
  		            ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
  		            ui.fluentWaitPresentWithRefresh("css=div[aria-label='" + subCommunityPublic.getName() + "']");
  		            ui.clickLinkWait("css=div[aria-label='" + subCommunityPublic.getName() + "']");
  		        }

  				
  				ui.checkCommunityNameFieldEmptyMsg(subCommunityPublic);

  				log.info("INFO: Click on the parent community link to return to the parent community");
  				driver.getSingleElement(CommunitiesUIConstants.tabbedNavParentCommunityName).click();

  				log.info("INFO: Create the 2nd subcommunity - moderated");
  				subCommunityModerated.create(ui);
  				
  		        if (flag) {
  		            apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
  		            Community communitycom = apiOwner.getCommunity(subCommunityModerated.getCommunityUUID());
  		            apiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
  		            ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
  		            ui.fluentWaitPresentWithRefresh("css=div[aria-label='" + subCommunityModerated.getName() + "']");
  		            ui.clickLinkWait("css=div[aria-label='" + subCommunityModerated.getName() + "']");
  		        }
  				
  				ui.checkCommunityNameFieldEmptyMsg(subCommunityModerated);

  				log.info("INFO: Click on the parent community link to return to the parent community");
  				driver.getSingleElement(CommunitiesUIConstants.tabbedNavParentCommunityName) .click();

  				log.info("INFO: Create the 3rd subcommunity - internal restricted");
  				subCommunityRestricted.create(ui);
  				
  		        if (flag) {
  		            apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
  		            Community communitycom = apiOwner.getCommunity(subCommunityRestricted.getCommunityUUID());
  		            apiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
  		            ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
  		            ui.fluentWaitPresentWithRefresh("css=div[aria-label='" + subCommunityRestricted.getName() + "']");
  		            ui.clickLinkWait("css=div[aria-label='" + subCommunityRestricted.getName() + "']");
  		        }
  				
  				ui.checkCommunityNameFieldEmptyMsg(subCommunityRestricted);

  				log.info("INFO: Click on the parent community link to return to the parent community");
  				driver.getSingleElement(CommunitiesUIConstants.tabbedNavParentCommunityName) .click();

  				log.info("INFO: Verify the Subcommunity menu displays on the top nav for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommLink),
  						"ERROR: The Subcommunity field does not appear on the top nav");

  				log.info("INFO: Verify the correct subcommunity count displays");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommCount .replace("PLACEHOLDER", "3")),
  						"ERROR: The subcommunity menu displays the incorrect number of subcommunities");

  				log.info("INFO: Verify the Subcommunity menu drop-down arrow displays on the top nav");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommMenuIcon),
  						"ERROR: The Subcommunity menu drop-down arrow does not appear on the top nav");		
                
  				log.info("INFO: Click on the Subcommunity drop-down menu icon");
  				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavSubcommMenuIcon);

  				//***This section will click on the public subcommunity & verify the subcommunity overview page displays
  				log.info("INFO: Collect the list of subcommunities on subcomm menu");
				List<Element> subcommList = ui.collectListOfSubcommunities();  
				
				log.info("INFO: Select the subcommunity from the drop-down menu");
				Assert.assertNotNull(ui.selectSubCommunity(subcommList,publicSubComm,true),
						"ERROR: Subcommunity was not found on the drop-down menu");
  								
  				log.info("INFO: Verify a link to the parent community appears on the top nav of the subcommunity");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavParentCommunityName),
  						"ERROR: No link to the parent community appears on the subcommunity overview page");

  				log.info("INFO: Verify the public subcommunity name " + subCommunityPublic.getName() + " appears on the top nav");
  				Assert.assertTrue(driver.isTextPresent(subCommunityPublic.getName()),
  						"ERROR: Public subcommunity name does not appear on the top nav");

  				log.info("INFO: Verify there is no Subcommunity field for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavHiddenSubcommField),
  						"ERROR: The Subcommunity field appears on the top nav");

  				log.info("INFO: Click on the parent community link to return to the parent community");
  				driver.getSingleElement(CommunitiesUIConstants.tabbedNavParentCommunityName) .click();

  				//***This section will click on the moderated subcommunity & verify the subcommunity overview page displays
  				log.info("INFO: Click on the subcommunity menu drop-down arrow");
  				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavSubcommMenuIcon);

				log.info("INFO: Select the subcommunity from the drop-down menu");
				Assert.assertNotNull(ui.selectSubCommunity(subcommList,moderatedSubComm,true),
						"ERROR: Subcommunity was not found on the drop-down menu");
  				
  				log.info("INFO: Verify a link to the parent community appears on the top nav of the subcommunity");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavParentCommunityName),
  						"ERROR: No link to the parent community appears on the subcommunity overview page");

  				log.info("INFO: Verify the moderated subcommunity name " + subCommunityModerated.getName() + " appears on the top nav");
  				Assert.assertTrue(driver.isTextPresent(subCommunityModerated.getName()),
  						"ERROR: Moderated subcommunity name does not appear on the top nav");

  				log.info("INFO: Verify there is no Subcommunity field for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavHiddenSubcommField),
  						"ERROR: The Subcommunity field appears on the top nav");

  				log.info("INFO: Click on the parent community link to return to the parent community");
  				driver.getSingleElement(CommunitiesUIConstants.tabbedNavParentCommunityName) .click();

  				//***This section will click on the restricted subcommunity & verify the subcommunity overview page displays
  				log.info("INFO: Click on the subcommunity menu drop-down arrow");
  				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavSubcommMenuIcon);
  							
  				log.info("INFO: Collect the list of subcommunities on subcomm menu");
				List<Element> subcommList2 = ui.collectListOfSubcommunities();  
				
				log.info("INFO: Select the subcommunity from the drop-down menu");
				Assert.assertNotNull(ui.selectSubCommunity(subcommList2,restrictedSubComm,true),
						"ERROR: Subcommunity was not found on the drop-down menu");
  				
  				log.info("INFO: Verify a link to the parent community appears on the top nav of the subcommunity");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavParentCommunityName),
  						"ERROR: No link to the parent community appears on the subcommunity overview page");

  				log.info("INFO: Verify the restricted subcommunity name " + subCommunityRestricted.getName() + " appears on the top nav");
  				Assert.assertTrue(driver.isTextPresent(subCommunityRestricted.getName()),
  						"ERROR: Restricted subcommunity name does not appear on the top nav");

  				log.info("INFO: Verify there is no Subcommunity field for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavHiddenSubcommField),
  						"ERROR: The Subcommunity field appears on the top nav");

  			}
  			else
  			{
  				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

  			} 

  			ui.endTest();

      }

      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Subcommunity Menu</li>
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li> 
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> Subcommunity dropdown menu should not appear on the parent community overview page until a subcommunity is added</li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Verify:</B> There is no subcommunity dropdown menu on the tabbed nav menu</li>
     	 * <li><B>Step:</B> Add a subcommunity 
     	 * <li><B>Verify:</B> On the subcomm overview page there is a link to the parent comm, subcomm name displays and there is no subcomm drop-down menu<B>
     	 * <li><B>Step:</B> Subcomm drop-down menu appears on the tabbed nav of the parent comm
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */     
      
      @Test(groups = {"regression", "regressioncloud"})
  	public void topNavSubcommMenu() {
    	  
    	    DefectLogger logger=dlog.get(Thread.currentThread().getId());

  			String testName = ui.startTest();	
  			  			
  			//Create a sub-community base state object 		
  			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
  											   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
  											   .UseParentmembers(false)
  											   .description("Tabbed Nav - subcommunities test " + testName)
  											   .build();
  			  			
  			log.info("INFO: Get UUID of community");
  			community10.getCommunityUUID_API(apiOwner, comAPI10);

  			ui.loadComponent(Data.getData().ComponentCommunities);
  			ui.login(testUser);
  			
      	  logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
          Boolean flag = ui.isHighlightDefaultCommunityLandingPage();


          if (flag) {
        	  apiOwner.editStartPage(comAPI10, StartPageApi.OVERVIEW);
          }

  			if (s)
  			{  			
  				log.info("INFO: Navigate to the community using UUID");
  				community10.navViaUUID(ui);

  				log.info("INFO: Verify there is no Subcommunity field for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavHiddenSubcommField),
  						"ERROR: The Subcommunity field appears on the top nav");

  				log.info("INFO: Creating Subcommunity ");
  				subCommunity.create(ui);
  				
  		        if (flag) {
  		            apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),testUser.getPassword());
  		            Community communitycom = apiOwner.getCommunity(subCommunity.getCommunityUUID());
  		            apiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
  		            ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
  		            ui.fluentWaitPresentWithRefresh("css=div[aria-label='" + subCommunity.getName() + "']");
  		            ui.clickLinkWait("css=div[aria-label='" + subCommunity.getName() + "']");
  		        }

  				log.info("INFO: Verify a link to the parent community appears on the top nav of the subcommunity");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavParentCommunityName),
  						"ERROR: No link to the parent community appears on the subcommunity overview page");

  				log.info("INFO: Verify the subcommunity name appears on the top nav");
  				Assert.assertTrue(driver.isTextPresent(subCommunity.getName()),
  						"ERROR: Subcommunity name does not appear on the top nav");

  				log.info("INFO: Verify there is no Subcommunity field for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavHiddenSubcommField),
  						"ERROR: The Subcommunity field appears on the top nav");

  				log.info("INFO: Click on the parent community link to return to the parent community");
  				driver.getSingleElement(CommunitiesUIConstants.tabbedNavParentCommunityName) .click();

  				log.info("INFO: Verify the Subcommunity field displays on the top nav for the newly created community");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommLink),
  						"ERROR: The Subcommunity field does not appear on the top nav");

  				log.info("INFO: Verify the Subcommunity menu drop-down arrow displays on the top nav");
  				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommMenuIcon),
  						"ERROR: The Subcommunity menu drop-down arrow does not appear on the top nav");	

  			}
  			else
  			{
  				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

  			}

  			ui.endTest();

      }
      
      /**
     	 * <ul>
     	 * <li><B>Test Scenario:</B> Tabbed Navigation: Subcommunity - Default Apps
     	 * <li><B>NOTE:</B> ***Test will need to be updated once new layout with tabbed navigation is available.</li>
     	 * <li><B>NOTE:</B> ***Test currently only runs against an on-prem pool server with tabbed nav GK setting enabled</li>
     	 * <li><B>Info:</B> Check is done to see if the Gatekeeper setting is enabled or not, if not the community is deleted & test is skipped</li>
     	 * <li><B>Info:</B> This test will verify the default apps on the tabbed nav menu on a subcommunity </li>
     	 * <li><B>Step:</B> Create a public community using the API </li>
     	 * <li><B>Step:</B> Log into Communities</li>
     	 * <li><B>Step:</B> Open the community</li>
     	 * <li><B>Step:</B> Add a subcommunity
     	 * <li><B>Verify:</B> Each of the default apps appear on the tabbed nav menu </li>
     	 * <li><B>Cleanup:</B> Delete the community </li> 
     	 * <li><a HREF="Notes://wrangle/85257863004CBF81/088F49BF479C68258525751B0060FA97/597DD9D767EA627C8525805F0056C678 "> TTT: Tabbed Navigation</a></li>
     	 *</ul>
     	 */
      
      @Test(groups = {"regression", "regressioncloud"} , enabled=false )
     	public void topNavDefaultAppsSubcomm()  {

     			String testName = ui.startTest();
     			
     					
     			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
     			                                                    .tags(Data.getData().commonTag + Helper.genDateBasedRand())
     			                                                    .UseParentmembers(false)
     			                                                    .description("Tabbed Nav - subcommunities default apps tab test " + testName)
     			                                                    .build();
     			     			
     			log.info("INFO: Get UUID of community");
     			community4.getCommunityUUID_API(apiOwner, comAPI4);

     			log.info("INFO: Log into Communities");
     			ui.loadComponent(Data.getData().ComponentCommunities);
     			ui.login(testUser);

     			if (s)
     			{
     				log.info("INFO: Navigate to the community using UUID");
     				community4.navViaUUID(ui);	         			

     				log.info("INFO: Creating Sub community ");
     				subCommunity.create(ui);
     				
     				UIEvents.refreshPage(driver);

     				log.info("INFO: Verify Overview appears on the tabbed nav menu");
     				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
     						"ERROR : Overview link does not appear on the tabbed nav menu");

     				log.info("INFO: Verify Metrics appears on the tabbed nav menu");
     				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavMetricsTab),
     						"ERROR: Metrics link does not appear on the tabbed nav menu");

     				log.info("INFO: Verify the default widgets appear on the tabbed nav menu");
     				Assert.assertTrue(ui.presenceOfDefaultWidgetsOnTopNav(),
     						"ERROR: Widgets on the top nav are not correct");

     			}     			     			
     			else
     			{
     				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + s + " - skipping this test");

     			} 

     			ui.endTest();


      }
      
      private void hide(BaseWidget widget) {
    	  log.info("INFO: Hide widget "+ widget.getTitle());
    	  ui.performCommWidgetAction(widget, Widget_Action_Menu.HIDE);

    	  log.info("INFO: Clicking Hide button on Hide Widget dialog");
    	  ui.fluentWaitPresent(CommunitiesUIConstants.WidgetHideButton);
    	  driver.getSingleElement(CommunitiesUIConstants.WidgetHideButton).click();
    	  ui.fluentWaitTextNotPresent(CommunitiesUIConstants.WidgetHideButton);

    	  Assert.assertFalse(
    			  driver.isElementPresent(ui.getWidgetByTitle(widget)),
    			  "ERROR: Failed to hide widget " + widget.getTitle());

      }
    
    private void remove(BaseWidget widget) {

    	Assert.assertTrue(
    			driver.isElementPresent(ui.getWidgetByTitle(widget)),
    			"ERROR: Widget is not existing on overview page: "
    					+ widget.getTitle() + "");

    	if (widget.equals(BaseWidget.SUBCOMMUNITIES)
    			| widget.equals(BaseWidget.GALLERY)) {
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
