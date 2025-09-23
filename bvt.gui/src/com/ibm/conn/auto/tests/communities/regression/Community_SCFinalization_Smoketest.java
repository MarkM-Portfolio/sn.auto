package com.ibm.conn.auto.tests.communities.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.Executor.Alert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.util.userBuilder.UserSelector;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Community_SCFinalization_Smoketest extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Community_SCFinalization_Smoketest.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;	 
    private FilesUI filesUI;
    private HomepageUI hUI;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private Community comAPI1,comAPI2,comAPI3,comAPI4,comAPI5,comAPI6,comAPI7,comAPI8,comAPI9,comAPI10, comAPI11, comAPI12,comAPI13,comAPI14,comAPI15,comAPI16,comAPI17,comAPI18,comAPI19,comAPI20,comAPI21,comAPI22,comAPI23;
	private BaseCommunity community1,community2,community3,community4,community5,community6,community7,community8,community9,community10,community11,community12,community13,community14,community15,community16,
	community17, community18, community19,community20,community21,community22, community23;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, adminUser;
	private String gk_flag;
	private boolean gk_value;
	private boolean isOnPremise;
	
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(),driver);
		hUI = HomepageUI.getGui(cfg.getProductName(),driver);

	}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(),driver);
		hUI = HomepageUI.getGui(cfg.getProductName(),driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		URLConstants.setServerURL(serverURL);
		
		
		//This sections locates an admin user that has a Connections subscription
		boolean adminfound = false;
		 
		 String userToken = getClass().getSimpleName() + Helper.genStrongRand();
			ArrayList<User> listOfTestUsers = UserSelector.selectUniqueUsers_Standard(cfg, userToken, 3);			
			testUser4 = listOfTestUsers.get(0);
			testUser5 = listOfTestUsers.get(1);
			testUser6 = listOfTestUsers.get(2);

			for (int x=0; x<5; x++){
					adminUser = UserSelector.selectUniqueUsers_Admin(cfg, userToken, 1).get(0);
					ui.loadComponent(Data.getData().ComponentCommunities);
					ui.login(adminUser);
					log.info("INFO: Attempt number "+x+ " to see if an Access Denied message appears when admin user " + adminUser.getDisplayName() + " logs into Connections");
					if (!driver.isTextPresent(Data.getData().accessDenied))
					{adminfound=true;
					log.info("INFO: Admin user with Connections subscription found");
					ui.logout();
					ui.close(cfg);
					break;
					}
					ui.logout();
					ui.close(cfg);

				}
				log.info("INFO: Verify that an admin user with Connections subscription is found");
				Assert.assertTrue(adminfound,
						"ERROR: No admin user with a Connections subscription was found");

				log.info("INFO: Admin user with Connections subsription is " + adminUser.getDisplayName());
		
		
		
		
		//check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		//Test communities
		community1 = new BaseCommunity.Builder("defaultAppsGetAddedToNewComm " + Helper.genDateBasedRandVal())
		                              .access(Access.RESTRICTED)
		                              .description("Verify default apps appear for newly created community.")
		                              .shareOutside(true)
		                              .build();
		
		community2 = new BaseCommunity.Builder("appsAppearInCorrectColumn " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Verify apps appear in the correct columns.")
                                      .shareOutside(true)
                                      .build();
		
		community3 = new BaseCommunity.Builder("noFeedsOnAddAppsDialog " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Verify Feeds is not listed on the Add Apps dialog.")
                                      .shareOutside(true)
                                      .build();
		
		community4 = new BaseCommunity.Builder("addWidgetAddingAppMsgDisplays " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Verify Adding Applications message displays when an app is being added.")
                                      .shareOutside(true)
                                      .build();
		
		community5 = new BaseCommunity.Builder("appsAppearOnLeftOrTopNav " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Verify apps appear ok.")
                                      .shareOutside(true)
                                      .build();
		
		community6 = new BaseCommunity.Builder("addMemberToCommunity " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Add a Member to the community.")
                                      .shareOutside(true)
                                      .build();
		
		community7 = new BaseCommunity.Builder("addOwnerToCommunity " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Add an Owner to the community.")
                                      .shareOutside(true)
                                      .build();
		
		community8 = new BaseCommunity.Builder("inviteUserToCommunity " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Invite a user to the community.")
                                      .shareOutside(true)
                                      .build();
		
		community9 = new BaseCommunity.Builder("moveCommDescAndMembersToBanner " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description(Data.getData().commonDescription)
                                      .shareOutside(true)
                                      .build();
		
		community10 = new BaseCommunity.Builder("moveWikiToLeftColumn " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Move Wiki to left column.")
                                      .shareOutside(true)
                                      .build();
		
		community11 = new BaseCommunity.Builder("statusUpdateWithHashtagAtMentionImage " + Helper.genDateBasedRandVal())
                                       .access(Access.RESTRICTED)
                                       .description("Create status update entry with a hashtag, mention & image.")
                                       .shareOutside(true)
                                       .build();
		
		community12 = new BaseCommunity.Builder("statusUpdateCommentWithHashtagAtMention " + Helper.genDateBasedRandVal())
                                       .access(Access.RESTRICTED)
                                       .description("Create status update comment with a hashtag & at mention.")
                                       .shareOutside(true)
                                       .build();
		
		community13 = new BaseCommunity.Builder("recentUpdatesASEntries " + Helper.genDateBasedRandVal())
                                       .access(Access.RESTRICTED)
                                       .description("Recent Updates activity stream entry test. ")
                                       .shareOutside(true)
                                       .build();
		
		community14 = new BaseCommunity.Builder("recentUpdatesPostMessageAndComment " + Helper.genDateBasedRandVal())
                                       .access(Access.RESTRICTED)
                                       .description("Community Recent Updates entry with hashtag, mention & image. ")
                                       .shareOutside(true)
                                       .build();
		
		community15 = new BaseCommunity.Builder("externalRestrictedCommCatalogViews " + Helper.genDateBasedRandVal())
                                       .access(Access.RESTRICTED)
                                       .description("External community - catalog views test. ")
                                       .shareOutside(true)
                                       .build();
		
		community16 = new BaseCommunity.Builder("ownerDeleteAndRestoreCommunity " + Helper.genDateBasedRand())
		                               .access(Access.PUBLIC)	
		                               .tags(Data.getData().commonTag + Helper.genDateBasedRand())
		                               .description("Test delete & restore community ")
		                               .build();
		
		community17 = new BaseCommunity.Builder("acceptInviteExternalComm " + Helper.genDateBasedRand())
		                               .access(Access.RESTRICTED)	
		                               .tags(Data.getData().commonTag + Helper.genDateBasedRand() )
		                               .description("Test accept community invitation " + Helper.genDateBasedRand())
		                               .shareOutside(true)
		                               .build();
		
		community18 = new BaseCommunity.Builder("copyExistingCommRecentlyVisited - public comm " + Helper.genDateBasedRand())
                                       .access(Access.PUBLIC)	
                                       .tags(Data.getData().commonTag + Helper.genDateBasedRand())
                                       .description("Copy existing community test ")
                                       .build();
		
		community19 = new BaseCommunity.Builder("copyExistingCommRecentlyVisited - moderated comm " + Helper.genDateBasedRand())
                                       .access(Access.MODERATED)	
                                       .tags(Data.getData().commonTag + Helper.genDateBasedRand())
                                       .description("Copy existing community test ")
                                       .build();
		
		community20 = new BaseCommunity.Builder("copyExistingCommRecentlyVisited - restricted but listed comm " + Helper.genDateBasedRand())
                                       .access(Access.RESTRICTED)
                                       .rbl(true)
                                       .tags(Data.getData().commonTag + Helper.genDateBasedRand())
                                       .description("Copy existing community test ")
                                       .build();
		
		community21 = new BaseCommunity.Builder("changeLayoutDialogHikariTheme " + Helper.genDateBasedRand())
                                       .access(Access.RESTRICTED)
                                       .rbl(true)
                                       .tags(Data.getData().commonTag + Helper.genDateBasedRand())
                                       .description("change layout dialog test ")
                                       .build();
		
		community22 = new BaseCommunity.Builder("orgAdminPublicCommChangeMemberToBusinessOwner " + Helper.genDateBasedRand())
                                       .access(Access.PUBLIC)	
                                       .tags(Data.getData().commonTag + Helper.genDateBasedRand())
                                       .description("Edit user with member access to be business owner ")
                                       .build();
		
		community23 = new BaseCommunity.Builder("orgAdminRestrictedCommChangeOwnerToBusinessOwner " + Helper.genDateBasedRand())
                                       .access(Access.RESTRICTED)	
                                       .tags(Data.getData().commonTag + Helper.genDateBasedRand())
                                       .description("Edit user with owner access to be business owner ")
                                       .build();

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
		comAPI11 = community11.createAPI(apiOwner);
		comAPI12 = community12.createAPI(apiOwner);
		comAPI13 = community13.createAPI(apiOwner);
		comAPI14 = community14.createAPI(apiOwner);
		comAPI15 = community15.createAPI(apiOwner);
		comAPI16 = community16.createAPI(apiOwner);
		comAPI17 = community17.createAPI(apiOwner);
		comAPI18 = community18.createAPI(apiOwner);
		comAPI19 = community19.createAPI(apiOwner);
		comAPI20 = community20.createAPI(apiOwner);
		comAPI21 = community21.createAPI(apiOwner);
		comAPI22 = community22.createAPI(apiOwner);
		comAPI23 = community23.createAPI(apiOwner);

		//Check to see if the Gatekeeper setting for tabbed nav is enabled or not 
		//NOTE: Using deprecated GK code because it works both on-prem & on the cloud.  Other code does not work on the cloud.   
		//OK per automation team to use deprecated version of GK		
		gk_flag = Data.getData().commTabbedNav;

		log.info("INFO: Get the UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		gk_value = gkc.getSetting(gk_flag);

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
		apiOwner.deleteCommunity(comAPI11);
		apiOwner.deleteCommunity(comAPI12);
		apiOwner.deleteCommunity(comAPI13);
		apiOwner.deleteCommunity(comAPI14);
		apiOwner.deleteCommunity(comAPI15);
		apiOwner.deleteCommunity(comAPI16);
		apiOwner.deleteCommunity(comAPI17);
		apiOwner.deleteCommunity(comAPI18);
		apiOwner.deleteCommunity(comAPI19);
		apiOwner.deleteCommunity(comAPI20);
		apiOwner.deleteCommunity(comAPI21);
		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B>External Restricted Community - Default Apps</li>
	 * <li><B>Info:</B>This test will verify the default apps appear for a newly created community</li>
	 * <li><B>Info:</B>Default Apps: Recent Updates,Status Updates,Members,Files, Wiki,Forums,Bookmarks,Community Description,Tags & Important Bookmarks.</li>
	 * <li><B>Step:</B>Create an external restricted community</li>
	 * <li><B>Verify:</B>Verify these apps exist by checking the left or tabbed nav (depends on GK setting): Recent Updates, Status Updates, Members, Files, Wiki, Forums & Bookmarks</li>
	 * <li><B>Verify:</B>Verify these apps exist by checking the summary widgets on the Overview page: Comm Description, Tags, Important Bookmarks & Members</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */
	
	 @Test(groups = {"regressioncloud"})
	   	public void defaultAppsGetAddedToNewComm()  {
		 	  
		 log.info("INFO: Get the UUID of the community");
		 community1.getCommunityUUID_API(apiOwner, comAPI1);

		 log.info("INFO: Load Communities & login");
		 ui.loadComponent(Data.getData().ComponentCommunities);
		 ui.login(testUser1);

		 log.info("INFO: Navigate to the community using the UUID");
		 community1.navViaUUID(ui);

		 if (gk_value){
			 log.info("INFO: Verify Overview appears on the tabbed nav menu");
			 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
					 "ERROR : Overview link does not appear on the tabbed nav menu");

			 log.info("INFO: Verify Metrics appears on the tabbed nav menu");
			 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavMetricsTab),
					 "ERROR: Metrics link does not appear on the tabbed nav menu");

			 log.info("INFO: Verify the default widgets appear on the tabbed nav menu");
			 Assert.assertTrue(ui.presenceOfDefaultWidgetsOnTopNav(),
					 "ERROR: Default widgets on the top nav are not correct");

		 }else {
			 log.info("INFO: Verify Overview appears on the left nav");
			 Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
					 "ERROR: Overview link does not appear on the left nav");

			 log.info("INFO: Hover over the Overview button on the left nav to see menu");
			 driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton).hover();

			 log.info("INFO: Verify Metrics appears on the left nav");
			 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavMetricsTab),
					 "ERROR: Metrics link does not appear on the left nav");

			 log.info("INFO: Verify default widgets appear on the left nav");
			 Assert.assertTrue(ui.presenceOfDefaultWidgetsForCommunity(),
					 "ERROR: Widgets on the left nav are not correct");
		 } 

		 log.info("INFO: Verify the Community Description widget appears on the Overview page");
		 Assert.assertTrue(driver.isTextPresent(Data.getData().appCommunityDescription),
				 "ERROR: Community Description widget does not exist on the Overview page");

		 log.info("INFO: Verify the Tags widget appears on the Overview page");
		 Assert.assertTrue(driver.isTextPresent(Data.getData().appTags),
				 "ERROR: Tags widget does not exist on the Overview page");

		 log.info("INFO: Verify the Members summary widget appears on the Overview page");
		 Assert.assertTrue(driver.isTextPresent(Data.getData().appMembersSummary),
				 "ERROR: Members summary widget does not exist on the Overview page");

		 log.info("INFO: Verify the Important Bookmarks widget appears on the Overview page");
		 Assert.assertTrue(driver.isTextPresent(Data.getData().appImportantBookmarks),
				 "ERROR: Important Bookmarks widget does not exist on the Overview page");


		 ui.endTest();

	 }	 
	 
	 /**
	  * <ul>
	  * <li><B>Test Scenario:</B>External Restricted Community - Apps Appear in the Correct Columns</li>
	  * <li><B>Info:</B>This test will verify non-default apps can be added to a community ok</li>
	  * <li><B>Step:</B>Create an external restricted community</li>
	  * <li><B>Step:</B>Add apps to the community:Activities, Subcommunities, Blog, Ideation Blog, Gallery, Events, Related Communities, Surveys & Featured Survey</li>
	  * <li><B>Verify:</B>Verify: the apps were added and appear in the correct columns</li>
	  * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	  *</ul>
	  */
		
	 @Test(groups = {"regressioncloud"})
	 public void appsAppearInCorrectColumn()  {
		 
		 log.info("INFO: Get the UUID of the community");
		 community2.getCommunityUUID_API(apiOwner, comAPI2);

		 log.info("INFO: Load Communities & login");
		 ui.loadComponent(Data.getData().ComponentCommunities);
		 ui.login(testUser1);

		 log.info("INFO: Navigate to the community using the UUID");
		 community2.navViaUUID(ui);

		 log.info("INFO: Add all widgets to the community");
		 try {
			 ui.addAllWidgets();
		 } catch (Exception e1) {
			 e1.printStackTrace();
		 }

		 log.info("INFO: Verify all the appears (default & non-default) appear in the correct columns");
		 this.verifyAddedAppsAppearInCorrectColumn();

		 ui.endTest();

	 }
	

	 /**
	  * <ul>
	  * <li><B>Test Scenario:</B>External Restricted Community - No Feeds link on the Add Apps Dialog</li>
	  * <li><B>Info:</B>This test will verify the Feeds app does not appear on the Add Apps Dialog</li>
	  * <li><B>Step:</B>Create an external restricted community</li>
	  * <li><B>Step:</B>Bring up the Add Apps dialog</li>
	  * <li><B>Verify:</B>Verify: Feeds does not appear on the dialog</li>
	  * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	  *</ul>
	  */

	 @Test(groups = {"regressioncloud"})
	 public void noFeedsAppOnAddAppsDialog()  {

		 log.info("INFO: Get the UUID of the community");
		 community3.getCommunityUUID_API(apiOwner, comAPI3);

		 log.info("INFO: Load Communities & login");
		 ui.loadComponent(Data.getData().ComponentCommunities);
		 ui.login(testUser1);

		 log.info("INFO: Navigate to the community using the UUID");
		 community3.navViaUUID(ui);

		 log.info("INFO: Select Add Apps from the community action menu");
		 Com_Action_Menu.ADDAPP.select(ui);	

		 log.info("INFO: Verify Feeds does not appear on the Add Apps dialog");
		 Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.WidgetFeeds),
				 "ERROR: Feeds appears on the Add Apps dialog");
		 ui.endTest();

	 }
	 
	 /**
	  * <ul>
	  * <li><B>Test Scenario:</B>External Restricted Community - Adding Application message</li>
	  * <li><B>Info:</B>This test will verify the Adding Application message displays when an app is being added</li>
	  * <li><B>Step:</B>Create an external restricted community</li>
	  * <li><B>Step:</B>Bring up the Add Apps dialog</li>
	  * <li><B>Step:</B>Click on Activities app</li>
	  * <li><B>Verify:</B>Verify: The Adding Application message displays</li>
	  * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	  *</ul>
	  */

	 @Test(groups = {"regressioncloud"})
	 public void addWidgetAddingAppMsgDisplays()  {

		 log.info("INFO: Get the UUID of the community");
		 community4.getCommunityUUID_API(apiOwner, comAPI4);

		 log.info("INFO: Load Communities & login");
		 ui.loadComponent(Data.getData().ComponentCommunities);
		 ui.login(testUser1);

		 log.info("INFO: Navigate to the community using the UUID");
		 community4.navViaUUID(ui);

		 log.info("INFO: Add Activities to the community");		 
		 ui.addWidget(BaseWidget.ACTIVITIES);  //addWidget method will verify the Adding Application message displays		 

		 ui.endTest();

	 }
	 
	 /**
	  * <ul>
	  * <li><B>Test Scenario:</B>External Restricted Community - Added apps appear on the left nav or tabbed nav</li>
	  * <li><B>Info:</B>This test will verify the added apps appear on either the left nav or tabbed nav - depending on GK setting</li>
	  * <li><B>Step:</B>Create an external restricted community</li>
	  * <li><B>Step:</B>Bring up the Add Apps dialog</li>
	  * <li><B>Step:</B>Add all apps to the community</li>
	  * <li><B>Verify:</B>Verify: the following apps appear on the left/tabbed nav: Blog, Ideation Blog, Events, Activities, Survey & Related Comm</li>
	  * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	  *</ul>
	  */

	 @Test(groups = {"regressioncloud"})
	 public void addedAppsAppearOnLeftOrTopNav()  {

		 log.info("INFO: Get the UUID of the community");
		 community5.getCommunityUUID_API(apiOwner, comAPI5);

		 log.info("INFO: Load Communities & login");
		 ui.loadComponent(Data.getData().ComponentCommunities);
		 ui.login(testUser1);

		 log.info("INFO: Navigate to the community using UUID");
		 community5.navViaUUID(ui);
		 
		 log.info("INFO: Add all widgets to the community");
		 try {
			 ui.addAllWidgets();
		 } catch (Exception e1) {
			 e1.printStackTrace();
		 }
		 
		 if (gk_value) {
			 log.info("INFO: Verify the following apps appear on the tabbed nav: Activities,Blog,Ideation Blog,Events,Related Communities & Surveys");
			 this.verifyAddedAppsOnTabbedNav();
		 }else {
			 log.info("INFO: Verify the following apps appear on the tabbed nav: Activities,Blog,Ideation Blog,Events,Related Communities & Surveys");
			 this.verifyAddedAppsOnLeftNav();
		 } 
		 
		 ui.endTest();

	 }
	 
	 /**
	 *<ul>
	 *<li><B>Test Scenario:</B>Add a Member to the Community</li>
	 *<li><B>Info:</B>This test will verify a Member can be added to the community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Login as owner</li> 
	 *<li><B>Step:</B>Open community using communityUUID</li>
	 *<li><B>Step:</B>Navigate to the Members page & Click Add Member button</li>
	 *<li><B>Step:</B>Select Role Member</li>
	 *<li><B>Step:</B>Enter user's name & select from type-ahead results</li>
	 *<li><B>Step:</B>Select Save button</li>
	 *<li><B>Verify:</B>Verify the user is added to the community</li>
	 *<li><B>Verify:</B>Verify that the new role is Member</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	  *</ul>
	 */	
	@Test(groups = {"regressioncloud"})
	public void addMemberToCommunity() {
	 
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
						
		log.info("INFO: Get the UUID of the community");
		community6.getCommunityUUID_API(apiOwner, comAPI6);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using the UUID");
		community6.navViaUUID(ui);

		log.info("INFO: Click on the Members widget View All link");
		ui.clickLinkWait(CommunitiesUIConstants.membersWidgetViewAllLink);

		log.info("INFO: Add a user to the community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembers_SaveButton);

		log.info("INFO: Collect the user's text from the members page");
		String memberInfo = ui.getMemberElement(member).getText();
		
		log.info("INFO: Verify the user is a Member");
		Assert.assertTrue(memberInfo.contains(Data.getData().memberRole),
						  "ERROR: User is not shown with Member access");
		
		log.info("INFO: Verify the user was added to the community");		
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: User is not listed on the members page");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Add an Owner to the Community</li>
	 *<li><B>Info:</B>This test will verify an Owner can be added to the community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Login as owner</li> 
	 *<li><B>Step:</B>Open community using communityUUID</li>
	 *<li><B>Step:</B>Navigate to the Members page & Click Add Member button</li>
	 *<li><B>Step:</B>Select Role Owner</li>
	 *<li><B>Step:</B>Enter user's name & select from type-ahead drop down menu</li>
	 *<li><B>Step:</B>Select Save button</li>
	 *<li><B>Verify:</B>Verify the user is added to the community</li>
	 *<li><B>Verify:</B>Verify that the new role is Owner</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	  *</ul>
	 */	
	@Test(groups = {"regressioncloud"})
	public void addOwnerToCommunity() {
	 
		Member member = new Member(CommunityRole.OWNERS, testUser2);
						
		log.info("INFO: Get the UUID of the community");
		community6.getCommunityUUID_API(apiOwner, comAPI6);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using the UUID");
		community6.navViaUUID(ui);

		log.info("INFO: Click on the Members widget View All link");
		ui.clickLinkWait(CommunitiesUIConstants.membersWidgetViewAllLink);

		log.info("INFO: Add an Owner to the community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembers_SaveButton);

		log.info("INFO: Collect the user's text from the members page");
		String memberInfo = ui.getMemberElement(member).getText();
		
		log.info("INFO: Verify the user is an Owner");
		Assert.assertTrue(memberInfo.contains(Data.getData().ownerRole),
						  "ERROR: User is not shown with Owner access");
		
		log.info("INFO: Verify the user was added to the community");		
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: User is not listed on the members page");
		
		ui.endTest();
	}
		 
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Invite a User to the Community</li>
	 *<li><B>Info:</B>This test will verify a user can be invited to a community</li>
	 *<li><B>Step:</B>Create an external restricted community</li>
	 *<li><B>Step:</B>Login as the community owner</li> 
	 *<li><B>Step:</B>Open community using communityUUID</li>
	 *<li><B>Step:</B>Navigate to the Members page & Click Invite Member button</li>
	 *<li><B>Step:</B>Enter a user's name & select from type-ahead</li>
	 *<li><B>Step:</B>Select Save button</li>
	 *<li><B>Verify:</B>Verify the user appears on the Invitations tab</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regressioncloud"})
	public void inviteUserToCommunity() {
	 
		Member member = new Member(CommunityRole.OWNERS, testUser2);
						
		log.info("INFO: Get the UUID of the community");
		community8.getCommunityUUID_API(apiOwner, comAPI8);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using the UUID");
		community8.navViaUUID(ui);

		log.info("INFO: Click on the Members widget View All link");
		ui.clickLinkWait(CommunitiesUIConstants.membersWidgetViewAllLink);
		
		log.info("INFO: Click on the Invite Members button & invite a user");
		try {
			ui.inviteMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Select save button");
		ui.clickLinkWait(CommunitiesUIConstants.SendInvitesButton);

		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);

		log.info("INFO: Verify the invited user is listed on the Invitations tab");	
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: The invited user does not appear on the Invitations tab");
		
		ui.endTest();
	}	 
		  

	/**
	 *<li><B>Test Scenario:</B>Move Community Description & Members to banner area</li> 
	 * <li><B>Info:</B>This test will verify the Community Description & Members apps get moved to the banner ok</li>
	 * <li><B>Step:</B>Create an external community using the API </li>
	 * <li><B>Step:</B>From the Overview page click on the action menu for the Community Description widget </li>
	 * <li><B>Step:</B>Move Community Description to the banner area</li>
	 * <li><B>Step:</B>Move the Members summary widget to the banner area</li>
	 * <li><B>Verify:</B>Verify the Community Description widget is in the banner area</li>	 * 
	 * <li><B>Verify:</B>Verify the Members widget is in the banner area</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regressioncloud"})
	public void moveCommDescAndMembersToBanner(){

		int columnNum = 0;

		BaseWidget commDesc=BaseWidget.COMMUNITYDESCRIPTION; 
		BaseWidget members=BaseWidget.MEMBERS;
		Widget_Action_Menu prevAction=Widget_Action_Menu.MOVETOPREVIOUSCOLUMN;
		Widget_Action_Menu nextAction=Widget_Action_Menu.MOVETONEXTCOLUMN;


		log.info("INFO: Get the UUID of the community");
		community9.getCommunityUUID_API(apiOwner, comAPI9);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community9.navViaUUID(ui);

		if (gk_value){
			log.info("INFO: Move Community Description & Members summary widget to the banner area");
			log.info("INFO: Click on the action Move to Next Column on the Community Description action menu");
			ui.performCommWidgetAction(commDesc, nextAction);

			log.info("INFO: Click on the action Move to Next Column on the Community Description action menu");
			ui.performCommWidgetAction(commDesc, nextAction);

			log.info("INFO: Click on the action Move To Next Column on the Members action menu");
			ui.performCommWidgetAction(members, nextAction);


		}else {	

			log.info("INFO: Change layout to be 3 Columns with top menu");

			log.info("INFO: Click on Community Actions");
			ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

			log.info("INFO: Click on the Change Layout link");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

			log.info("INFO: Select '3 Columns with banner' from the layout palette");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithBannerLink);

			log.info("INFO: Check for the Communities link to make sure page is loaded");
			ui.isElementPresent(CommunitiesUIConstants.CommunitiesLink);

			log.info("INFO: Move Community Description & Members summary widget to the banner area");
			log.info("INFO: Click on the action Move to Next Column on the Community Description action menu");
			ui.performCommWidgetAction(commDesc, prevAction);

			log.info("INFO: Click on the action Move to Next Column on the Community Description action menu");
			ui.performCommWidgetAction(commDesc, prevAction);

			log.info("INFO: Click on the action Move To Next Column on the Members action menu");
			ui.performCommWidgetAction(members, prevAction);

		}

		log.info("INFO: Get the column number the Community Description widget is located in");
		columnNum = ui.getWidgetLocationInfo(commDesc,true);

		log.info("INFO: Verify the Community Description widget appears in the correct location - banner area");
		Assert.assertEquals(columnNum, 4,
				"ERROR: Community Description widget is not in the correct location.  Expected widget in the banner area, but found in column " + columnNum);

		log.info("INFO: Verify the Community Description content displays ok");
		Assert.assertTrue(driver.isTextPresent(Data.getData().commonDescription),
				"ERROR: The description does not appear");		

		log.info("INFO: Get the column number the Members summary widget is located in");
		columnNum = ui.getWidgetLocationInfo(members,true);

		log.info("INFO: Verify Members summary widget appears in the correct location - banner area");
		Assert.assertEquals(columnNum, 4,
				"ERROR: Members summary widget is not in the correct location.  Expected widget in the banner area, but found in column " + columnNum);

		log.info("INFO: Verify the Members summary widget View All link appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.membersWidgetViewAllLink),
				"ERROR: View All link does not appear for the Members summary widget");

		ui.endTest();
	} 
	
	/**
	 *<li><B>Test Scenario:</B>Move Wiki to Left Column</li> 
	 * <li><B>Info:</B>This test will verify the Wiki app can be moved to the left column ok</li>
	 * <li><B>Step:</B>Create an external community using the API</li>
	 * <li><B>Step:</B>From the Overview page click on the action menu for the Wiki widget</li>
	 * <li><B>Step:</B>If tabbed nav is used, click Move to Previous Column link, if left nav is used, click Move to Next Column link</li>
	 * <li><B>Verify:</B>Verify the Wiki widget is in the left column</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regressioncloud"})
	public void moveWikiToLeftColumn(){

		int columnNum = 0;

		BaseWidget wiki=BaseWidget.WIKI; 
		Widget_Action_Menu prevAction=Widget_Action_Menu.MOVETOPREVIOUSCOLUMN;
		Widget_Action_Menu nextAction=Widget_Action_Menu.MOVETONEXTCOLUMN;

		log.info("INFO: Get the UUID of the community");
		community10.getCommunityUUID_API(apiOwner, comAPI10);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community10.navViaUUID(ui);

		if (gk_value){

			log.info("INFO: Move Wiki to the left column");
			ui.performCommWidgetAction(wiki, prevAction);

		}else {	

			log.info("INFO: Move Wiki to the left column");
			ui.performCommWidgetAction(wiki, nextAction);

		}

		log.info("INFO: Get the column number the Wiki widget is located in");
		columnNum = ui.getWidgetLocationInfo(wiki,true);

		log.info("INFO: Verify the Wiki widget appears in the correct column - left column");
		Assert.assertEquals(columnNum, 1,
				"ERROR: Wiki widget is not in the correct column.  Expected widget in the left column, but found in column " + columnNum);


		ui.endTest();
	} 
		
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B>Status Updates entry with hashtag, mentions & image</li>
	 * <li><B>Info:</B>This test will cover creating a status updates entry with a hashtag, mentions, and an image</li>
	 * <li><B>Step:</B>Create community using the API</li>
	 * <li><B>Step:</B>Log into Communities</li>
	 * <li><B>Step:</B>Open the Community created using the API</li>
	 * <li><B>Step:</B>Click on Status Updates link</li>
	 * <li><B>Step:</B>Post a status updates entry with a hashtag, mentions and image file</li>
	 * <li><B>Verify:</B>Verify the entry was successfully posted</li>
	 * <li><B>Step:</B>Like the entry</li>
	 * <li><B>Verify:</B>Verify the Unlike link now displays</li>
	 * <li><B>Step:</B>Open the EE pop-up</li>
	 * <li><B>Step:</B>Click on the Unlike link</li>
	 * <li><B>Step:</B>Return to the status updates entry</li>
	 * <li><B>Verify:</B>Verify the Unlike link no longer displays</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regressioncloud"})
	public void statusUpdateWithHashtagAtMentionImage(){	
		
		String statusUpdate = " look at this message with hashtag #test " + Helper.genDateBasedRand();
	    
	    BaseFile file = new BaseFile.Builder(Data.getData().file6)
                                    .rename(Helper.genStrongRand())
                                    .extension(".jpg")
                                    .build();
	    
		log.info("INFO: Get the UUID of the community");
		community11.getCommunityUUID_API(apiOwner, comAPI11);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community11.navViaUUID(ui);
		
		if (gk_value){
			log.info("INFO: Click on Status Updates tab");
            Community_TabbedNav_Menu.STATUSUPDATES .select(ui);
			
		}else {	
			log.info("INFO: Verify Overview appears on the left nav");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
					"Error : Overview link does not appear on the left nav");

			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			log.info("INFO: Verify Status Updates appears on the left nav drop-down menu");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavStatusUpdates),
					"Error : Status Updates link does not appear on the left nav drop-down menu");

			log.info("INFO: Click on Status Updates ");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavStatusUpdates);

		}
		
		log.info("INFO: Create status update entry with an @mention: " + testUser3.getEmail());
		hUI.postAtMentionUserUpdate(testUser3, statusUpdate);

		log.info("INFO: Click on the Add a File link");
		ui.clickLinkWait(CommunitiesUIConstants.AddFileLink);

		log.info("INFO: Add a file to the status update entry");
		log.info("INFO: Enter the file name and path");
		try {
			filesUI.fileToUpload(file.getName(), BaseUIConstants.FileInputField2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Click OK button to upload file");
		ui.clickLinkWait(CommunitiesUIConstants.OKButton);
		
		log.info("INFO: Scroll to the top of the page so Post link is visible");
		driver.clickAt(0, 0);

		log.info("INFO: Post the Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);

		log.info("INFO: Verify the message that the entry has been successfully posted displays");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
				"ERROR: The success message did not display");

		log.info("INFO: Click on the 'Like' link for the status updates entry with content: " + statusUpdate);
		hUI.moveToClick(HomepageUI.getStatusUpdateMesage(statusUpdate), FilesUIConstants.PopupLikeFile);

		log.info("INFO: Verify the 'Unlike' link now dislays");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
				"ERROR: The Unlike link does not appear for the status updates entry");

		log.info("INFO: Locate the status updates entry and open it in EE");
		hUI.filterNewsItemOpenEE(statusUpdate);

		log.info("INFO: Click on UnLike");
		ui.clickLinkWait(HomepageUIConstants.EELikeUndo);
		
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		log.info("INFO: Refresh page so the unlike gets reflected in the UI");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Verify the 'Unlike' link no longer dislays");
		Assert.assertFalse(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
				"ERROR: The Unlike link still appears for the status updates entry");

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B>Status Updates Comment with hashtag and at mentions</li>
	 * <li><B>Info:</B>This test will cover a status updates comment with a hashtag & at mentions</li>
	 * <li><B>Info:</B>This test will also cover liking & unliking the comment</li>
	 * <li><B>Step:</B>Create a community using the API</li>
	 * <li><B>Step:</B>Log into Communities</li>
	 * <li><B>Step:</B>Open the Community created using the API</li>
	 * <li><B>Step:</B>Click on Status Updates link</li>
	 * <li><B>Step:</B>Post a basic entry</li>
	 * <li><B>Step:</B>Post a comment to the status updates entry - comment contains a hashtag & an at mention</li>
	 * <li><B>Verify:</B>Verify the entry was successfully posted</li>
	 * <li><B>Step:</B>Open the EE pop-up for the entry</li>
	 * <li><B>Verify:</B>Verify the Like link appears for the comment</li>
	 * <li><B>Step:</B>Like the comment</li>
	 * <li><B>Step:</B>Close out the EE</li>
	 * <li><B>Verify:</B>Verify the Unlike link now displays</li>
	 * <li><B>Step:</B>Click on the Unlike link</li>
	 * <li><B>Verify:</B>Verify Unlike link no longer appears</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regressioncloud"})
	public void statusUpdateCommentWithHashtagAtMention(){	
		
		String statusUpdate = " message posted to status updates widget " + Helper.genDateBasedRand();
	    String statusComment = " comment contains an at mention & hashtag #comment " + Helper.genDateBasedRand() + " ";
	    
	    log.info("INFO: Get the UUID of the community");
	    community12.getCommunityUUID_API(apiOwner, comAPI12);

	    log.info("INFO: Load Communities & login");
	    ui.loadComponent(Data.getData().ComponentCommunities);
	    ui.login(testUser1);

	    log.info("INFO: Navigate to the community using UUID");
	    community12.navViaUUID(ui);

	    if (gk_value){
	    	log.info("INFO: Click on Status Updates tab");
	    	Community_TabbedNav_Menu.STATUSUPDATES .select(ui);


	    }else {	
	    	log.info("INFO: Verify Overview appears on the left nav");
	    	Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
	    			"Error : Overview link does not appear on the left nav");

	    	log.info("INFO: Hover over the Overview button on the left nav");
	    	driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

	    	log.info("INFO: Verify Status Updates appears on the left nav drop-down menu");
	    	Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavStatusUpdates),
	    			"Error : Status Updates link does not appear on the left nav drop-down menu");

	    	log.info("INFO: Click on Status Updates ");
	    	ui.clickLinkWait(CommunitiesUIConstants.leftNavStatusUpdates);

	    }

	    log.info("INFO: Create status update entry with an @mention: " + testUser3.getEmail());
	    hUI.postAtMentionUserUpdate(testUser3, statusUpdate);
	    
	    log.info("INFO: Scroll to the top of the page so the Post link is visible");
		driver.clickAt(0, 0);	    

	    log.info("INFO: Post the Status Message");
	    ui.clickLinkWait(CommunitiesUIConstants.StatusPost);

	    log.info("INFO: Verify the message that the entry has been successfully posted displays");
	    Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
	    		"ERROR: The success message did not display");
	    
	    log.info("INFO: Post a comment with a hashtag and at mention");
	    ui.postCommunityUpdateCommentWithAtMentions(statusUpdate,statusComment,testUser2);

	    log.info("INFO: Verify the comment was successfully posted");
	    Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getStatusUpdatesComment(statusComment)),
	    		"ERROR: Status Update comment does not display");

	    log.info("INFO: Locate the status message and open it in EE");
	    hUI.filterNewsItemOpenEE(statusUpdate);

	    log.info("INFO: Verify the 'Like' link dislays for the comment");
	    Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.statusUpdateFirstEECommentLike),
	    		"ERROR: The Like link does not appear for the comment");

	    log.info("INFO: Click on the Like link for the comment");
	    ui.clickLinkWait(CommunitiesUIConstants.statusUpdateFirstEECommentLike);

	    log.info("INFO: Click on the 'Unlike' link for the comment");
	    ui.clickLinkWait(HomepageUIConstants.EELikeUndo);

	    log.info("INFO: Verify the 'Like' link now displays for the comment");
	    Assert.assertFalse(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
	    		"ERROR: Unlike link appears for the comment");

	    ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B>Quick Smoketest for Communities on the Cloud</li>
	 * <li><B>Info:</B>This test will verify an Activity Stream entry is posted in Recent Updates for:</li>
	 * <li><B>Info:</B>community create, wikis, blogs, ideation blog and forums</li>
	 * <li><B>Step:</B>Community is created via the API</li>
	 * <li><B>Step:</B>Log into Communities</li>
	 * <li><B>Step:</B>Add Blogs, Ideation Blog, and if on-prem add the Wiki widget</li>
	 * <li><B>Step:</B>Navigate to the Recent Updates view</li>
	 * <li><B>Verify:</B>Verify there is an AS entry for the community create, forums, community wiki, wiki, blogs & ideation blogs</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */			
	@Deprecated
	@Test(groups = {"regressioncloud"})
	public void recentUpdatesASEntries(){

		BaseWidget widget1 = BaseWidget.BLOG;
		BaseWidget widget2 = BaseWidget.IDEATION_BLOG;
		BaseWidget widget3 = BaseWidget.WIKI;

		log.info("INFO: Get the UUID of the community");
	    community13.getCommunityUUID_API(apiOwner, comAPI13);

	    log.info("INFO: Load Communities & login");
	    ui.loadComponent(Data.getData().ComponentCommunities);
	    ui.login(testUser1);

	    log.info("INFO: Navigate to the community using UUID");
	    community13.navViaUUID(ui);

		log.info("INFO: Add Blog widget via API");
		community13.addWidgetAPI(comAPI13, apiOwner, widget1);

		log.info("INFO: Add Ideation Blog widget via API");
		community13.addWidgetAPI(comAPI13, apiOwner, widget2);
		
		if(isOnPremise){

			log.info("INFO: Add Wiki widget");
			community13.addWidgetAPI(comAPI13, apiOwner, widget3);	
		}
		
		log.info("Execute the test if GateKeeper setting for Tabbed Navigation is enabled");
		 if (gk_value){
			log.info("INFO: Click on Recent Updates from the tabbed nav");
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + gk_value + " so expect the Tabbed Nav Bar");
			Community_TabbedNav_Menu.RECENT_UPDATES.select(ui);

		}else { 			
			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			log.info("INFO: Verify Recent Updates is listed on the left nav");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavRecentUpdates),
					"Error : Recent Updates link is not listed on the left nav");

			log.info("INFO: Click on the Recent Updates link on the left nav");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavRecentUpdates);
		}
					
		log.info("INFO: Verify there is an Activity Stream entry for community created");
		Assert.assertTrue(driver.isTextPresent(hUI.replaceNewsStory(Data.CREATE_COMMUNITY_RECENTUPDATES, null, null, testUser1.getDisplayName())),
				"ERROR: There is no community created entry.");				
		
		log.info("INFO: Verify there is an Activity Stream entry for Forums");
		Assert.assertTrue(driver.isTextPresent(hUI.replaceNewsStory(Data.CREATE_FORUM, community13.getName(), null, testUser1.getDisplayName())),
				"ERROR: There is no created forum entry");
		
		log.info("INFO: Verify there is an Activity Stream entry for community Wikis");
		Assert.assertTrue(driver.isTextPresent(hUI.replaceNewsStory(Data.CREATE_WIKI, community13.getName(), null, testUser1.getDisplayName())),
				"ERROR: There is no created community wiki entry");
		
		log.info("INFO: Verify there is an Activity Stream entry for Wikis");
		Assert.assertTrue(driver.isTextPresent(hUI.replaceNewsStory(Data.WIKI_WELCOME_PAGE_CREATED, community13.getName(), community13.getName(), testUser1.getDisplayName())),
				"ERROR: There is no created wiki entry");
		
		log.info("INFO: Verify there is an Activity Stream entry for Blogs");
		Assert.assertTrue(driver.isTextPresent(hUI.replaceNewsStory(Data.CREATE_COMM_BLOG_RECENTUPDATES, community13.getName(), null, testUser1.getDisplayName())),
				"ERROR: There is no added blog entry");
		
		log.info("INFO: Verify there is an Activity Stream entry for Ideation Blogs");
		Assert.assertTrue(driver.isTextPresent(hUI.replaceNewsStory(Data.CREATE_IDEATION_BLOG_RECENTUPDATES, community13.getName(), null, testUser1.getDisplayName())),
				"ERROR: There is no added ideation blog entry");
		

		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B>Quick Smoketest for Communities on the Cloud</li>
	 * <li><B>Info:</B>Recent Updates test - at mentions, Likes/Unlikes, EE pop-up</li>
	 * <li><B>Step:</B>Create a Public Community using the API</li>
	 * <li><B>Step:</B>Log into the Community as the community owner</li>
	 * <li><B>Step:</B>Open the community using UUID</li>
	 * <li><B>Step:</B>Click on the Recent Updates link</li>
	 * <li><B>Step:</B>Post a Recent Updates entry with an at mention & some text</li>
	 * <li><B>Step:</B>Click on the Recent Updates entry to open the EE pop-up</li>
	 * <li><B>Step:</B>Post a comment with an at mention in the EE pop-up</li>
	 * <li><B>Verify:</B>Verify the recent updates message appears in the EE pop-up</li>
	 * <li><B>Verify:</B>Verify the comment posted in the EE pop-up appears</li>
	 * <li><B>Step:</B>From the EE pop-up, click on the 'Like' link for the status updates entry</li>
	 * <li><B>Verify:</B>Verify an 'Unlike' link now displays</li>
	 * <li><B>Step:</B>Return to the Recent Updates page </li>
	 * <li><B>Verify:</B>Verify the 'Unlike' link appears for the status updates entry</li>
	 * <li><B>Verify:</B>Verify the 'Like' count for the status updates entry is 1</li>
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/8B3C604EE244C1CB85257F93004B7FDA/E584B3CA657BBCC985257F470059ACEC">TTT - QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */			
	@Deprecated
	@Test(groups = {"regressioncloud"})
	public void recentUpdatesPostMessageAndComment() {

		String statusComment = "this comment contains a mention & hashtag #comment " + Helper.genDateBasedRand();
		String mention = Character.toString('@');

		log.info("INFO: Get the UUID of the community");
	    community14.getCommunityUUID_API(apiOwner, comAPI14);

	    log.info("INFO: Load Communities & login");
	    ui.loadComponent(Data.getData().ComponentCommunities);
	    ui.login(testUser1);

	    log.info("INFO: Navigate to the community using UUID");
	    community14.navViaUUID(ui);

		log.info("Execute the test if GateKeeper setting for Tabbed Navigation is enabled");
		 if (gk_value){
			log.info("INFO: Click on Recent Updates from the tabbed nav");
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + gk_value + " so expect the Tabbed Nav Bar");
			Community_TabbedNav_Menu.RECENT_UPDATES.select(ui);

		}else { 
			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			log.info("INFO: Verify presence of Recent Updates in Community card");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavRecentUpdates),
					"Error : Recent Updates link is not present");

			log.info("INFO: Click on Recent Updates ");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavRecentUpdates);
		}
		log.info("INFO: Create recent update entry with an @mention and some text: " + testUser1.getEmail());
		ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);
		
		log.info("INFO: Scroll to the top of the page so the Post button is visible");
		driver.clickAt(0, 0);

		log.info("INFO: Post of Recent Updates message - click Post button");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
		
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
				"Error: Status message is not getting displayed");			
		
		log.info("INFO: Select Status message entry and open it in the EE ");
		hUI.filterNewsItemOpenEE(Data.getData().UpdateStatus.trim());

		log.info("INFO: Verify the status entry displays in the EE dialog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), Data.getData().UpdateStatus.trim(),
				"ERROR: Status entry does not display in the EE dialog");

		log.info("INFO: Click on the Like link for the status message in the EE");
		driver.getFirstElement(HomepageUIConstants.EELike).click();

		log.info("INFO: Post a comment in EE");
		ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
		log.info("INFO: Switching to comments frame");
		Element commentframe = driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame);
		driver.switchToFrame().selectFrameByElement(commentframe);

		log.info("INFO: Enter some text into the EE comment field");
		ui.fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
		Element inputField = driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField);
		inputField.click();
		inputField.type(statusComment);

		log.info("INFO: Enter the @mention into the EE pop-up");
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(" " + mention + testUser1.getDisplayName());

		log.info("INFO: Select user from the mentions typeahead suggestion list");
		selectTypeaheadUserInEEUsingArrowKeys();	
		hUI.switchToEEFrame();

		log.info("INFO: Click the Post button");
		ui.clickLinkWait(HomepageUIConstants.OpenEEPostCommentButton);

		log.info("INFO: Verify the status update entry displays in the EE dialog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), Data.getData().UpdateStatus.trim(),
				"ERROR: Status update entry does not display in the EE dialog");

		log.info("INFO: Verify the comment is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusComment),
				"ERROR: Comment is not displayed in the EE");

		log.info("INFO: Verify the 'Unlike' link now dislays");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
				"ERROR: The Unlike link does not appear for the status updates entry");

		log.info("INFO: " + testUser1.getDisplayName() + " go to the Activity Stream and verify the comment appears");
		ui.switchToTopFrame();

		log.info("INFO: Verify the 'Unlike' link displays on the Recent updates page");
		Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
				"ERROR: The Unlike link does not appear for the status updates entry");

		log.info("INFO: Verify that the number 1 now appears before the 'Unlike' link");
		Assert.assertEquals(Integer.parseInt(driver.getFirstElement(HomepageUIConstants.EELikeCount).getText()), 1,
				"ERROR: The number of Like's is not equal to 1");	

		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Quick Smoketest for Communities on the Cloud</li>
	 *<li><B>Info:</B> Test that an external restricted community appears in the appropriate catalog views</li>
	 *<li><B>Step:</B> Create a Restricted community using API</li>
	 *<li><B>Verify:</B> The community appears in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The community appears in the I'm a Member view</li>
	 *<li><B>Verify:</B> The community appears in the I'm Following view</li>
	 *<li><B>Verify:</B> The community does not appear in the I'm Invited view</li>
	 *<li><B>Verify:</B> The community does not appear in the Public Communities view</li>
	 *<li><B>Verify:</B> The community does not appear in the Trash view</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li></a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void externalRestrictedCommCatalogViews() {

		log.info("INFO: Get the UUID of the community");
		community15.getCommunityUUID_API(apiOwner, comAPI15);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Wait with refresh for the community to appear in the catalog view");
		ui.fluentWaitPresentWithRefresh("link="+ community15.getName());

		log.info("INFO: Click on the I'm an Owner view link");
		Community_View_Menu.IM_AN_OWNER.select(ui);
					
		log.info("INFO: Verify the community appears in the I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community15.getName()),
				"ERROR: The community does not appear in the I'm an Owner view");

		log.info("INFO: Click on the I'm Member view link");
		Community_View_Menu.IM_A_MEMBER.select(ui);

		log.info("INFO: Verify the community appears in the I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community15.getName()),
				"ERROR: The community does not appear in the I'm a Member view");

		log.info("INFO: Click on the I'm Following view link");
		Community_View_Menu.IM_FOLLOWING.select(ui);	

		log.info("INFO: Verify the community appears in the I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community15.getName()),
				"ERROR: The community does not appear in the I'm Following view");

		log.info("INFO: Click on the I'm Invited view link");
		Community_View_Menu.IM_INVITED.select(ui);

		log.info("INFO: Verify the community does not appear in the I'm Invited view");
		Assert.assertFalse(driver.isElementPresent("link="+ community15.getName()),
				"ERROR: The community appears in the I'm Invited view");

		log.info("INFO: Click on the Public community view link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavPublicCommunities);

		log.info("INFO: Verify the community does not appear in the Public Communities view");
		Assert.assertFalse(driver.isElementPresent("link="+ community15.getName()),
				"ERROR: The community appears in the Public Communities view");

		log.info("INFO: Click on the Trash view link");
		ui.clickLinkWait(CommunitiesUIConstants.TrashLink);

		log.info("INFO: Verify the community does not appear in the Trash view");
		Assert.assertFalse(driver.isElementPresent("link="+ community15.getName()),
				"ERROR: The community appears in the Trash view");

		ui.endTest();		
	}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Community Actions: Delete & Restore community as community creator</li>
	 *<li><B>Info:</B> Verify the community creator can delete & restore a community</li>
	 *<li><B>Step:</B> Create a Public community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the community creator & delete the community</li>
	 *<li><B>Step:</B> Navigate to the Trash view</li>
	 *<li><B>Verify:</B> The community is listed in the Trash view</li>
	 *<li><B>Step:</B> Click on the Restore link</li>
	 *<li><B>Verify:</B> A message displays indicating that the community has been restored</li>
	 *<li><B>Verify:</B> Verify that the community name is an active link</li>
	 *<li><B>Step:</B> Navigate back to the I'm an Owner view</li>
	 *<li><B>Verify:</B> The community is listed in the I'm an Owner view</li>
	 *<li><B>Step:</B> Cleanup by deleting the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
     *</ul>
	 */
	@Deprecated
	@Test(groups = {"regressioncloud"})
	public void ownerDeleteAndRestoreCommunity(){

		Element widget;
		
		log.info("INFO: Get UUID of community");
		community16.getCommunityUUID_API(apiOwner, comAPI16);

        log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community16.navViaUUID(ui);

		log.info("INFO: Select Delete Community from the Community Actions menu");
		Com_Action_Menu.DELETE.select(ui);

		log.info("INFO: Complete the fields on the delete community dialog");
		driver.getSingleElement(CommunitiesUIConstants.commDeleteName).type(community16.getName());
		driver.getSingleElement(CommunitiesUIConstants.commDeleteUser).type(testUser1.getDisplayName());

		log.info("INFO: Verify the OK button to delete the community exists");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.commDeleteButton).isEnabled(),
				"ERROR: The button to delete a community is not enabled");
				
		//Use JS click to work around the button being off screen in FF 32
		log.info("INFO: Click on the OK button");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.commDeleteButton);

		log.info("INFO: Waiting for Move to Trash popup to disappear");
		ui.fluentWaitTextNotPresentWithoutRefresh(Data.getData().deleteCommunityWarning);

		log.info("INFO: Click on Trash view");
		ui.clickLinkWait(CommunitiesUIConstants.TrashLink);

		log.info("INFO: Verify community is listed in the Trash view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community16.getName()),
				"ERROR: Owner is not able to see his community under Trash View!");

		log.info("INFO: Locate the community in the Trash view");
		widget = ui.getCommunityWidget(community16.getName());
		
		log.info("Click on Restore link for the community");
		widget.getSingleElement(CommunitiesUIConstants.RestoreCommunityFromTrash).click();
		
		log.info("Verify the message displays that the community was restored");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityRestore).getText(),"The community "+community16.getName() +" was restored.",
				"ERROR: The community was restored message is not shown");

		log.info("Verify that the community name is an active link");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getCommunityLink(community16)),
				"ERROR: Community name is not an active link");

		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);

		log.info("INFO: Verify the user is able to see the community in I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresent("link="+ community16.getName()),
				"ERROR : Owner is not able to see his community under I'm an Owner!");

		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.clickLinkWait(CommunitiesUIConstants.MemberCommunitiesView);

		log.info("INFO: Verify the user is able to see the community in I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresent("link="+ community16.getName()),
				"ERROR : Owner is not able to see his community under I'm a Member!");
		
		ui.endTest();
	}
	

	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Accept Community Invite</li>
	 *<li><B>Info:</B> Test that a user invited to a Restricted community sees the invite in the I'm Invited view</li>
	 *<li><B>Step:</B> Create a Restricted community using API</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Step:</B> Invite a new user to the community</li>
	 *<li><B>Step:</B> Logout the creator</li>
	 *<li><B>Step:</B> Login as the invited user</li>
	 *<li><B>Verify:</B> The user Invite count is greater then 0</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Invited view</li>
	 *<li><B>Verify:</B> The presence of Accept and Decline links under Invited community</li>
	 *<li><B>Verify:</B> Click on the Accept link</>
	 *<li><B>Verify:</B> The community name no longer appears in the I'm Invited view</li>
	 *<li><B>Verify:</B> Verify the number of invites has decreased by 1</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void acceptInviteExternalComm() {

		int TotalCount;

		log.info("INFO: Get the UUID of the community");
		community17.getCommunityUUID_API(apiOwner, comAPI17);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
			
		log.info("INFO: Navigate to the community using UUID");
		community17.navViaUUID(ui);	
		
		log.info("INFO: Select Members from left nav");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Adding member via Invited member button");
		try {
			ui.inviteMemberCommunity(new Member(CommunityRole.MEMBERS, testUser3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
		
		log.info("INFO: Log out the community creator");
		ui.logout();
		
		log.info("INFO: Log in as the user invited to the community");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser3);

		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		Community_View_Menu.IM_INVITED.select(ui);

		log.info("INFO: If the guided tour pop-up appears, close it");
		ui.closeGuidedTourPopup();

		log.info("INFO: Validate that the number of invites for this user is greater then 0");
		Assert.assertTrue(Integer.parseInt(ui.getElementText(CommunitiesUIConstants.InvitedUnreadCount)) > 0,
				"ERROR: Invite count for this user is not greater then 0");

		log.info("INFO: Store the count value in a variable");
		TotalCount=Integer.parseInt(ui.getElementText(CommunitiesUIConstants.InvitedUnreadCount));

		log.info("INFO: Validate the invited member is able to see the community under I'm Invited view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community17.getName()),
				"ERROR: Invited Member is not able to see the community in I'm Invited!!");

		log.info("INFO: If the guided tour pop-up appears, close it");
		ui.closeGuidedTourPopup();

		log.info("INFO: Verify the view contains Accept link under Invited community");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getAcceptLink(community17.getDescription())),
				"ERROR: Accept link is not present for Invited community");

		log.info("INFO: Verify the view contains Decline link under Invited community");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getDeclineLink(community17.getDescription())),
				"ERROR: Decline link is not present for Invited community");

		log.info("INFO: Clicking on Accept link in community under I'm Invited view");
		ui.clickLinkWait(CommunitiesUI.getAcceptLink(community17.getDescription()));

		log.info("INFO: Verify the accept invite confirmation message displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.acceptInviteConfirmationMsg),
				"ERROR: The accept invite confirmation message did not display");

		log.info("INFO: Verify that community name no longer exists in I'm Invited view");
		Assert.assertFalse(driver.isElementPresent("link="+ community17.getName()),  
				"ERROR: Community name is still existing in I'm Invited view");

		log.info("INFO: Validate that the number of invites for this user is reduced by one");
		if(TotalCount == 1) 
			Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.InvitedUnreadCount),
					"ERROR: Number of Invites did not go down by 1");
		else
			Assert.assertTrue(TotalCount > Integer.parseInt(ui.getElementText(CommunitiesUIConstants.InvitedUnreadCount)),
					"ERROR: Number of Invites did not go down by 1");


		ui.endTest();	
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate the "Start a Community" menu & the "Copy an Existing Community" option are visible & work from ALL the Catalog views</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on each view link except Trash from the Left Navigation</li>
	*<li><B>Step:</B> Click on "Start a Community" menu</li>
	*<li><B>Step:</B> Select "Copy an Existing Community" option</li>
	*<li><B>Verify:</B> User should see the "Copy an Existing Community" Search dialog box</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	*</ul>
	*/
	@Test (groups = {"regressioncloud"})
	public void copyExistingCommRecentlyVisited() {
		
		String testName = ui.startTest();	
			
		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);

		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)|| ui.checkGKSetting(Data.getData().gk_searchQuickResultsScope_flag)){
			
			log.info("INFO: Navigate to the Org Public View");
			Community_View_Menu.PUBLIC_COMMUNITIES .select(ui);
			
			log.info("INFO: Validate the user is able to see the public community in the Public View");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community18.getName()),
					"ERROR: User does not see the public community in the Public View!");
			
			log.info("INFO: Click on the Public Community " + community18.getName());
			ui.clickLinkWait(CommunitiesUI.getCommunityLink(community18));
			
			log.info("INFO: Return to the catalog view - click on Communities link on mega-menu");
			ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
			
			log.info("INFO: Navigate to the Org Public View");
			Community_View_Menu.PUBLIC_COMMUNITIES .select(ui);
			
			log.info("INFO: Validate the user is able to see the moderated community in the Public View");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community19.getName()),
					"ERROR: User does not see the moderated community in the Public View!");
			
			log.info("INFO: Click on the moderated community " + community19.getName());
			ui.clickLinkWait(CommunitiesUI.getCommunityLink(community19));	
			
			log.info("INFO: Return to the catalog view - click on Communities link on mega-menu");
			ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
			
			log.info("INFO: Navigate to the Org Public View");
			Community_View_Menu.PUBLIC_COMMUNITIES .select(ui);
			
			log.info("INFO: Validate the user is able to see the restricted but listed community in the Public View");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community20.getName()),
					"ERROR: User does not see the restricted but listed community in the Public View!");
			
			log.info("INFO: Click on the moderated community " + community20.getName());
			ui.clickLinkWait(CommunitiesUI.getCommunityLink(community20));	
			
			log.info("INFO: Return to the catalog view - click on Communities link on mega-menu");
			ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

			log.info("INFO: Click on Start a Community to expand the dropdown menu");
			ui.clickLinkWait(CommunitiesUIConstants.StartACommunityMenu);

			log.info("INFO: Click on 'Copy an Existing Community' option");
			ui.clickLinkWait(CommunitiesUIConstants.CopyExistingCommOption);
			
			log.info("INFO: Validate 'Copy an Existing Community' search dialog box appears by verifying the URL");
			System.out.println(driver.getCurrentUrl());  
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: 'Copy an Existing Community' search dialog box does not appear");
			
			log.info("INFO: Validate the user is able to see the Public community under Choose from Recent Communities");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community18.getName()),
							  "ERROR: The user does not see the Public community under Choose from Recent Communities!");
			
			log.info("INFO: Validate the user is able to see the Public community under Choose from Recent Communities");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community19.getName()),
							  "ERROR: The user does not see the Moderated community under Choose from Recent Communities!");
			
			log.info("INFO: Validate the user does not see the restricted but listed community under Choose from Recent Communities");
			Assert.assertFalse(ui.fluentWaitPresentWithRefresh("link="+ community20.getName()),
							  "ERROR: The user does sees the restricted but listed community under Choose from Recent Communities!");
			
			log.info("INFO: open the public community");
			ui.clickLinkWait(CommunitiesUI.getCommunityLink(community18));
			
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify 'Copy' appears in the community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community18.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR : Copy does not appear in the community name");
	
		}else{

			log.info("INFO: 'Copy an Existing Community' menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

		}	

	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Validate the 5 layouts on the change layout dialog</li>
	*<li><B>Step:</B> Log into communities</li>
	*<li><B>Step:</B> Check gatekeeper setting for hikari theme</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceed with test; otherwise, skip the test</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Select Community Actions > Change Layout</li>
	*<li><B>Verify:</B> Each of the 5 layouts are listed</li>
	*<li><B>Verify:</B> The selected layout is '3 Columns with top menu and banner'</li>
	*<li><B>Verify:</B> Dialog box no longer displays after clicking the Close icon</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	*</ul>
	*/
	@Test (groups = {"regressioncloud"})
	public void  changeLayoutDialogHikariTheme() {
		
	 String testName = ui.startTest();
	 
	 log.info("INFO: Get the UUID of the community");
	 community21.getCommunityUUID_API(apiOwner, comAPI21);

	 log.info("INFO: Load Communities & login");
	 ui.loadComponent(Data.getData().ComponentCommunities);
	 ui.login(testUser1);

	 log.info("INFO: Navigate to the community using the UUID");
	 community21.navViaUUID(ui);
	 
	 if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){
		 log.info("INFO: Open the change layout dialog");
		 Com_Action_Menu.CHANGELAYOUT .select(ui);

		 log.info("INFO: Verify the '2 Columns with side menu' layout appears on the Change Layout dialog box");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout2ColumnsWithSideMenu),
				 "ERROR: The '2 columns with side menu' layout does not appear on the dialog");
		 
		 log.info("INFO: Verify '3 Columns with side menu' layout appears on the Change Layout dialog box");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithSideMenu),
				 "ERROR: The '3 columns with side menu' layout does not appear on the dialog");
		 
		 log.info("INFO: Verify '3 Columns with side menu and banner' layout appears on the Change Layout dialog box");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsSideMenuAndBanner),
				 "ERROR: The '3 columns with side menu and banner' layout does not appear on the dialog");
		 
		 log.info("INFO: Verify '3 Columns with top menu' layout appears on the Change Layout dialog box");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithTopMenu),
				 "ERROR: The '3 columns with top menu' layout does not appear on the dialog");
		 
		 log.info("INFO: Verify '3 Columns with top menu and banner' layout appears on the Change Layout dialog box");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithTopMenuAndBanner),
				 "ERROR: The '3 Columns with top menu and banner' layout does not appear on the dialog");
		 
		 log.info("INFO: Verify '3 Columns with top menu and banner' layout option is the selected");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.selectedLayout3ColumnsWithTopMenuAndBanner),
				 "ERROR: '3 Columns with top menu and banner' is not the selected layout");
			
		 log.info("INFO: Close the change layout dialog");
		 ui.clickLinkWait(CommunitiesUIConstants.CloseWidget);
		 
		 log.info("INFO: Verify the dialog box no longer displays");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddAppsDialog),
				 "ERROR: Change Layout dialog still appears");


	 }else {
		 log.info("INFO: Hikari theme is not enabled");
		 throw new SkipException("Test '" + testName + "' is skipped");	
	 }
	} 	
	
	/**
	 * <ul> 
	 * <li><B>Test Scenario:</B> Org Admin (non-member) changes user access from Member to Business Owner </li>
	 * <li><B>Step:</B> Create a public community using the API </li>
	 * <li><B>Step:</B> Log into Communities the business owner</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page & add a user with 'member' access</li>
	 * <li><B>Step:</B> Log out the business owner & log in as the org admin
	 * <li><B>Step:</B> Open the community & navigate to the Members page
	 * <li><B>Step:</B> Edit user's access level - change from Member to Business Owner</li>
	 * <li><B>Verify:</B> The access level was changed from Member to Business Owner</li>
	 * <li><B>Verify:</B> The original Business Owner now has Owner access</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminPublicCommChangeMemberToBizOwner() {
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member bizOwner = new Member(CommunityRole.OWNERS, testUser1);
		

		log.info("INFO: Get the UUID of community");
		community22.getCommunityUUID_API(apiOwner, comAPI22);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community22.navViaUUID(ui);
		
		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(ui);

		log.info("INFO: Add Member to community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Select save button");
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);

		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Validating the user is an member");
		Assert.assertTrue(memberInfo.contains("Member"),
				"ERROR: User does record does not contain owner");
		
		log.info("INFO: Log out the community owner " + testUser1.getDisplayName());
		ui.logout();

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community22.navViaUUID(ui);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Click on the Members tab");    		
			Community_TabbedNav_Menu.MEMBERS .select(ui);

		}else { 

			log.info("INFO: Clicking on the communities link");
			Community_LeftNav_Menu.MEMBERS .select(ui);
		}
		log.info("INFO: Click on the Edit link under the member named: " + member.getUser().getDisplayName());
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);

		log.info("INFO: Select the radio button option 'Busines Owner'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.businessOwnerRadioButton).click();

		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();
		
		log.info("INFO: Click on the OK button on the change business owner confirmation alert message");
		Alert changeBizOwnerAlert=driver.switchToAlert();
		changeBizOwnerAlert.accept();

		log.info("INFO: Get the access level for: " + member.getUser().getDisplayName());
		memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Verify the user " + member.getUser().getDisplayName() + " now has Business Owner access");
		Assert.assertTrue(memberInfo.contains(Data.getData().businessOwnerRole),
				"ERROR: The user's access level did not get changed to Owner access");	
		
		log.info("INFO: Verify the community member is now the Business Owner");
		Assert.assertTrue(memberInfo.contains("Business Owner"),
						 "ERROR: The original Member is not listed as the Business Owner");	
		
		log.info("INFO: Collect the members text from member page");
		String ownerInfo = ui.getMemberElement(bizOwner).getText();
		
		log.info("INFO: Verify the original Business Owner is now an Owner");
		Assert.assertTrue(ownerInfo.contains("Owner"),
				  "ERROR: The original Business Owner is not listed as the Owner");


		ui.endTest();
	}
	
	
	
	/**
	 * <ul> 
	 * <li><B>Test Scenario:</B> Org Admin (non-member) changes user access from Owner to Business Owner </li>
	 * <li><B>Step:</B> Create a restricted community using the API </li>
	 * <li><B>Step:</B> Log into Communities the business owner</li>
	 * <li><B>Step:</B> Open the community</li>
	 * <li><B>Step:</B> Navigate to the full Members page & add a user with Owner access</li>
	 * <li><B>Step:</B> Log out the business owner & log in as the org admin
	 * <li><B>Step:</B> Open the community & navigate to the Members page
	 * <li><B>Step:</B> Edit user's access level - change from Owner to Business Owner</li>
	 * <li><B>Verify:</B> The access level was changed from Owner to Business Owner</li>
	 * <li><B>Verify:</B> The original Business Owner now has Owner access</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/DB19F4F391881AD3852580DE0058BB95/E584B3CA657BBCC985257F470059ACEC"> TTT: QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */
	@Test(groups = {"regressioncloud"})
	public void orgAdminRestrictedCommChangeOwnerToBizOwner() {
		
		Member member = new Member(CommunityRole.OWNERS, testUser2);
		Member bizOwner = new Member(CommunityRole.OWNERS, testUser1);
		Member orgAdmin = new Member(CommunityRole.MEMBERS, adminUser);
		

		log.info("INFO: Get the UUID of community");
		community23.getCommunityUUID_API(apiOwner, comAPI23);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(adminUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community23.navViaUUID(ui);
		
		log.info("INFO: Verify the orgAdmin does not have access to the restricted community");
		Assert.assertTrue(driver.isTextPresent(Data.getData().accessDenied),
				"ERROR: the orgAdmin does not see the Access Denied message");
		
		log.info("INFO: Log out the orgAdmin " + adminUser.getDisplayName());
		ui.logout();

		log.info("INFO: Log into Communities as the community owner: " + testUser1.getDisplayName());
		ui.login(testUser1);

		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Click on the Members tab");    		
			Community_TabbedNav_Menu.MEMBERS .select(ui);

		}else { 

			log.info("INFO: Clicking on the communities link");
			Community_LeftNav_Menu.MEMBERS .select(ui);
		}

		log.info("INFO: Add user with Owner access to community");		
		try {
			ui.addMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("INFO: Click on the user role drop-down menu & select Members");
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText("Members");
						
		ui.typeText(BaseUIConstants.CommunityMembersTypeAhead, orgAdmin.getUser().getDisplayName());
		driver.isTextPresent(orgAdmin.getUser().getDisplayName());
		driver.typeNative(Keys.TAB);
		
		log.info("INFO: Click on the Save button");
		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);

		log.info("INFO: Get the user's access level from the member page");
		String memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Validate the user has owner access");
		Assert.assertTrue(memberInfo.contains(Data.getData().ownerRole),
				"ERROR: User does not have Owner access");
		
		log.info("INFO: Get the orgAdmin's access level from the member page");
		String orgAdminInfo = ui.getMemberElement(orgAdmin).getText();
		
		log.info("INFO: Validate the user is has member access");
		Assert.assertTrue(orgAdminInfo.contains(Data.getData().memberRole),
				"ERROR: User does not have Member access");
						
		log.info("INFO: Log out the community owner " + testUser1.getDisplayName());
		ui.logout();

		log.info("INFO: Log into Communities as an admin user: " + adminUser.getDisplayName());
		ui.login(adminUser);

		log.info("INFO: Navigate to the community using UUID");
		community22.navViaUUID(ui);
		
		log.info("INFO: Check if tabbed nav GK setting is enabled or not and then navigate to the Members page");
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Click on the Members tab");    		
			Community_TabbedNav_Menu.MEMBERS .select(ui);

		}else { 

			log.info("INFO: Clicking on the communities link");
			Community_LeftNav_Menu.MEMBERS .select(ui);
		}
			
		log.info("INFO: Click on the Edit link for the user " + member.getUser().getDisplayName());
		ui.clickEditMemberLink(member);

		log.info("INFO: Select the Business Owner radio button on the Edit Member dialog");
		ui.getFirstVisibleElement(CommunitiesUIConstants.businessOwnerRadioButton).click();

		log.info("INFO: Click on the Edit Member pop-up Save button");
		ui.getFirstVisibleElement(CommunitiesUIConstants.EditMemberDialogSaveButton).click();
		
		log.info("INFO: Click on the OK button on the change business owner confirmation alert message");
		Alert changeBizOwnerAlert=driver.switchToAlert();
		changeBizOwnerAlert.accept();

		log.info("INFO: Get the access level for: " + member.getUser().getDisplayName());
		memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Verify the user " + member.getUser().getDisplayName() + " now has Business Owner access");
		Assert.assertTrue(memberInfo.contains(Data.getData().businessOwnerRole),
				"ERROR: The user's access level did not get changed to Owner access");	
		
		log.info("INFO: Verify the community member is now the Business Owner");
		Assert.assertTrue(memberInfo.contains("Business Owner"),
						 "ERROR: The original Member is not listed as the Business Owner");	
		
		log.info("INFO: Collect the members text from member page");
		String ownerInfo = ui.getMemberElement(bizOwner).getText();
		
		log.info("INFO: Verify the original Business Owner is now an Owner");
		Assert.assertTrue(ownerInfo.contains("Owner"),
				  "ERROR: The original Business Owner is not listed as the Owner");


		ui.endTest();
	}
	
	
	
	 /**
	  * Verify the added apps appear on the left nav menu
	  */		    
	 private void verifyAddedAppsOnLeftNav(){
		 log.info("INFO: Verify Overview appears on the left nav");
		 Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
				 "ERROR: Overview link does not appear on the left nav");

		 log.info("INFO: Hover over the Overview button on the left nav");
		 driver.getFirstElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		 log.info("INFO: Verify Blogs appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavBlogs),
				 "ERROR: Blogs does not appear on the left nav");

		 log.info("INFO: Verify Ideation Blog appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavIdeationBlogs),
				 "ERROR: Ideation Blog does not appear on the left nav");

		 log.info("INFO: Verify Activities appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavActivitiesTab),
				 "ERROR: Activities does not appear on the left nav"); 		  

		 log.info("INFO: Verify Events appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavEvents),
				 "ERROR: Events does not appear on the left nav");

		 log.info("INFO: Verify Related Communities appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavRelatedComm),
				 "ERROR: Related Communities does not appear on the left nav");

		 log.info("INFO: Verify Surveys appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavSurveys),
				 "ERROR: Surveys does not appear on the left nav");


	 }		

	 /**
	  * Verify the added apps appear on the tabbed nav
	  */		    
	 private void verifyAddedAppsOnTabbedNav(){
		 log.info("INFO: Verify Overview appears on the left nav");
		 Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
				 "ERROR: Overview link does not appear on the left nav");

		 log.info("INFO: Verify Blogs appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavBlogTab),
				 "ERROR: Blogs does not appear on the left nav");

		 log.info("INFO: Verify Ideation Blog appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavIdeationBlogTab),
				 "ERROR: Ideation Blog does not appear on the left nav");

		 log.info("INFO: Verify Activities appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavActivitiesTab),
				 "ERROR: Activities does not appear on the left nav"); 		  

		 log.info("INFO: Verify Events appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavEventTab),
				 "ERROR: Events does not appear on the left nav");

		 log.info("INFO: Verify Related Communities appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavRelatedCommTab),
				 "ERROR: Related Communities does not appear on the left nav");

		 log.info("INFO: Verify Surveys appears on the left nav");
		 Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSurveysTab),
				 "ERROR: Surveys does not appear on the left nav");


	 }	
	 
	 
	 /**
	  * Verify all the apps (default & non-default) appear in the correct column
	  */		    
	 private void verifyAddedAppsAppearInCorrectColumn(){
		 int columnNum = 0;

		 BaseWidget tags=BaseWidget.TAGS;
		 BaseWidget commDesc=BaseWidget.COMMUNITYDESCRIPTION;
		 BaseWidget files=BaseWidget.FILES;
		 BaseWidget forums=BaseWidget.FORUM;
		 BaseWidget bkmks=BaseWidget.BOOKMARKS;
		 BaseWidget wiki=BaseWidget.WIKI;
		 BaseWidget subcomm=BaseWidget.SUBCOMMUNITIES;
		 BaseWidget activities=BaseWidget.ACTIVITIES;
		 BaseWidget blog=BaseWidget.BLOG;
		 BaseWidget ideation=BaseWidget.IDEATION_BLOG;
		 BaseWidget surveys=BaseWidget.SURVEYS;		 
		 BaseWidget impBkmks=BaseWidget.IMPORTANTBOOKMARKS;
		 BaseWidget members=BaseWidget.MEMBERS;
		 BaseWidget gallery=BaseWidget.GALLERY;
		 BaseWidget events=BaseWidget.EVENTS;
		 BaseWidget relatedComm=BaseWidget.RELATED_COMMUNITIES;
		 BaseWidget featuredSurvey=BaseWidget.FEATUREDSURVEYS;
		 		 
		
		 log.info("INFO: Get the column number the Tags widget is located in");
		 columnNum = ui.getWidgetLocationInfo(tags,true);
		 log.info("INFO: Verify the Tags widget appears in the correct column");
		 Assert.assertEquals(columnNum, 1,
				 "ERROR: Tags widget is not in the correct column.  Expected column 1, but found in column " + columnNum);
		 
		 log.info("INFO: Get the column number the Community Description widget is located in");
		 columnNum = ui.getWidgetLocationInfo(commDesc,true);
		 log.info("INFO: Verify the Community Description widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Community Description widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Files widget is located in");
		 columnNum = ui.getWidgetLocationInfo(files,true);
		 log.info("INFO: Verify the Files widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Files widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Forums widget is located in");
		 columnNum = ui.getWidgetLocationInfo(forums,true);
		 log.info("INFO: Verify the Forums widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Forums widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Bookmarks widget is located in");
		 columnNum = ui.getWidgetLocationInfo(bkmks,true);
		 log.info("INFO: Verify the Bookmarks widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Wiki widget is located in");
		 columnNum = ui.getWidgetLocationInfo(wiki,true);
		 log.info("INFO: Verify the Wiki widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Wiki widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Subcommunity widget is located in");
		 columnNum = ui.getWidgetLocationInfo(subcomm,true);
		 log.info("INFO: Verify the Subcommunity widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Subcommunity widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Activity widget is located in");
		 columnNum = ui.getWidgetLocationInfo(activities,true);
		 log.info("INFO: Verify the Activity widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Activity widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Blog widget is located in");
		 columnNum = ui.getWidgetLocationInfo(blog,true);
		 log.info("INFO: Verify the Blog widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Blog widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Ideation Blog widget is located in");
		 columnNum = ui.getWidgetLocationInfo(ideation,true);
		 log.info("INFO: Verify the Ideation Blog widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Ideation Blog widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Survey widget is located in");
		 columnNum = ui.getWidgetLocationInfo(surveys,true);
		 log.info("INFO: Verify the Survey widget appears in the correct column");
		 Assert.assertEquals(columnNum, 2,
				 "ERROR: Survey widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Important Bookmarks widget is located in");
		 columnNum = ui.getWidgetLocationInfo(impBkmks,true);
		 log.info("INFO: Verify the Important Bookmarks widget appears in the correct column");
		 Assert.assertEquals(columnNum, 3,
				 "ERROR: Important Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Members widget is located in");
		 columnNum = ui.getWidgetLocationInfo(members,true);
		 log.info("INFO: Verify the Members widget appears in the correct column");
		 Assert.assertEquals(columnNum, 3,
				 "ERROR: Members widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Gallery widget is located in");
		 columnNum = ui.getWidgetLocationInfo(gallery,true);
		 log.info("INFO: Verify the Gallery widget appears in the correct column");
		 Assert.assertEquals(columnNum, 3,
				 "ERROR: Gallery widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Events widget is located in");
		 columnNum = ui.getWidgetLocationInfo(events,true);
		 log.info("INFO: Verify the Events widget appears in the correct column");
		 Assert.assertEquals(columnNum, 3,
				 "ERROR: Events widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the Related Community widget is located in");
		 columnNum = ui.getWidgetLocationInfo(relatedComm,true);
		 log.info("INFO: Verify the Related Community widget appears in the correct column");
		 Assert.assertEquals(columnNum, 3,
				 "ERROR: Related Community widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

		 log.info("INFO: Get the column number the  widget is located in");
		 columnNum = ui.getWidgetLocationInfo(featuredSurvey,true);
		 log.info("INFO: Verify the Featured Survey widget appears in the correct column");
		 Assert.assertEquals(columnNum, 3,
				 "ERROR: Featured Survey widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

	 }
	 
	 
	 
	 /*
	  * this method will select a user from typeahead in the EE pop-up dialog
	  */

	 private String selectTypeaheadUserInEEUsingArrowKeys() {

		 log.info("INFO: Now pressing the UP ARROW key a number of times to move up the list of typeahead menu items");
		 for(int index = 0; index < 7; index ++) {
			 driver.switchToActiveElement().type(Keys.ARROW_UP);
		 }

		 hUI.waitForEETypeaheadMenuToLoad();

		 log.info("INFO: Retrieve all menu items to verify which one is about to be selected");
		 List<Element> menuItemElements = hUI.getTypeaheadMenuItemsList(false);

		 String selectedMenuItem = null;
		 if(menuItemElements.size() > 5) {
			 log.info("INFO: The fifth user from the typeahead menu is now being selected");
			 selectedMenuItem = menuItemElements.get(menuItemElements.size() - 5).getText();

		 } else if(menuItemElements.size() <= 5 && menuItemElements.size() > 1) {
			 log.info("INFO: The second user from the typeahead menu is now being selected");
			 selectedMenuItem = menuItemElements.get(1).getText();

		 } else {
			 log.info("INFO: The first user from the typeahead menu is now being selected");
			 selectedMenuItem = menuItemElements.get(0).getText();
		 }
		 log.info("INFO: Now pressing the ENTER key to select the highlighted user in the typeahead menu");
		 driver.switchToActiveElement().type(Keys.ENTER);

		 return selectedMenuItem;
	 }
}
