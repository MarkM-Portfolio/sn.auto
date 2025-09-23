package com.ibm.conn.auto.tests.communities.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_MegaMenu_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class SubCommunities extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(SubCommunities.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser,testUser1,testUser2,testUser3,testUser4;
	private APICommunitiesHandler apiOwner;
	private boolean isOnPremise;
	private String serverURL;
	
	/**
	 * ptc Files combined:
	 * ptc_SubCommunityVerification
	 * ptc_SubCommunityViaAddApps
	 * ptc_FollowUnfollowCommunity
	*/
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	
		//check to see if environment is on-premises or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}

	
	}
	
		@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}

	/**
	 * Sub-Community Verification
	 *<ul>SUBCOMMUNITIES PART1: CREATE A MODERATED SUBCOMMUNITY WITH ALL MEMBERS FROM PARENT ADDED TO SUBCOMM
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a public community as owner & add two member and two owners to it using community API </B> </li>
	 *<li><B>Step: Open the Community & then create a Sub-Community from Community Actions with all the members and owners from Parent comm added to Sub comm  </B> </li>
	 *<li><B>Verify: Verify that after creation of Sub-Community , we are at overview page of Sub-Community  </B> </li>
	 *<li><B>Verify: Verify the Sub-Community is having Overview, Recent Updates, Status Updates, Members, Forums, Bookmarks,Files at Community Card (Left Navigation)</B> </li>
	 *<li><B>Verify: Verify that parent Community link is present Above Sub-Community & when we click it we go to parent community & it has link for Sub-Community </B> </li>
	 *<li><B>Verify:</B> Verify the subcommunity link appears on the parent community card </li>
	 *<li><B>Verify:</B> Verify that the Subcommunities widget now contains a link to the new subcomm
	 *<li><B>Verify:</B> verify that the user is returned to the child subcommunity after clicking the link
	 *<li><B>Verify:</B>Verify that all the same Owners & Members from the parent community have been added to this child subcommunity
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/6249057C2E46D47D85257C8D00569780 ">TTT- SUBCOMMUNITIES PART1: CREATE A MODERATED SUBCOMMUNITY WITH ALL MEMBERS FROM PARENT ADDED TO SUBCOMM</a></li>
	 *</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void addSubcommunityActions(){

			String testName = ui.startTest();
			String subcommName = "SubCommunity" + Helper.genDateBasedRand();
			String commName = testName + Helper.genDateBasedRand();
			String gk_flag ;
			boolean value ; 
			
			BaseCommunity community = new BaseCommunity.Builder(commName)
									     .access(Access.PUBLIC)	
										 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
										 .description("Test creating a moderated subcommunity " + testName)
										 .addMember(new Member(CommunityRole.MEMBERS, testUser1))
										 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
										 .addMember(new Member(CommunityRole.OWNERS, testUser3))
										 .addMember(new Member(CommunityRole.OWNERS, testUser4))
										 .build();
			
			 		
			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(subcommName)
											   .access(BaseSubCommunity.Access.MODERATED)
											   .UseParentmembers(true)
			                                   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
											   .description("Test creating a moderated subcommunity " + testName)
											   .build();
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);

			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Creating Sub community ");
			subCommunity.create(ui);
			
			this.checkCommunityNameFieldEmptyMsg(subCommunity);
			
			log.info("INFO: Verify subcommunity is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
					"ERROR : subcommunity is not created");
			
			value = ui.checkGKSetting(Data.getData().commTabbedNav);
			gk_flag = Data.getData().commTabbedNav;

			log.info("Execute the test to check if the GateKeeper setting for Tabbed Navigation is enabled");
			if(value){				
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
				
				log.info("INFO: Verify the Overview tab appears on the tabbed nav");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
						"ERROR: Overview tab does not appear on the tabbed nav ");

				log.info("INFO: Verify Metrics appears on the tabbed nav menu");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavMetricsTab),
						"ERROR: Metrics link does not appear on the tabbed nav menu");

				log.info("INFO: Verify the default widgets appear on the tabbed nav menu");
				Assert.assertTrue(ui.presenceOfDefaultWidgetsOnTopNav(),
						"ERROR: Widgets on the top nav are not correct");

				log.info("INFO: Clicking on the parent community link");
				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavParentCommunityName);

				log.info("INFO: Verify the Subcommunity field displays on the top nav");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommLink),
						"ERROR: The Subcommunity field does not appear on the top nav");

				log.info("INFO: Click on the subcommunity drop-down menu icon");
				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavSubcommMenuIcon);

				log.info("INFO: Collect the list of subcommunities on subcomm menu");
				List<Element> subcommList = ui.collectListOfSubcommunities();  
				
				log.info("INFO: Click on the subcommunity drop-down menu icon");
				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavSubcommMenuIcon);

				log.info("INFO: Select the subcommunity from the drop-down menu");
				Assert.assertNotNull(ui.selectSubCommunity(subcommList,subcommName,true),
						"ERROR: Subcommunity was not found on the drop-down menu");
				
			}else { 

				log.info("INFO: Validate Overview option is present");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
						"ERROR : Overview link is not present");

				log.info("INFO: Verify presence of all default widgets in SubCommunity");
				Assert.assertTrue(ui.presenceOfDefaultWidgetsForCommunity(),
						"ERROR: Presence of all the default widgets in Subcommunity is not correct");

				log.info("INFO: Validate the presence of the parent community in SubCommunity");
				Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage).getText(), community.getName(),
						"ERROR : Parent Community link is not present");

				log.info("INFO: Clicking on the Communities link");
				ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);

				log.info("INFO: Validate Overview option is present");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
						"ERROR : Overview link is not present");

				log.info("INFO: Hover over the Overview button on the left nav");
				driver.getFirstElement(CommunitiesUIConstants.leftNavSubcommunitiesButton) .hover();

				log.info("INFO: Validate the SubCommunity link is present under parent community");
				Assert.assertTrue(ui.fluentWaitPresent("link="+ subCommunity.getName()), 
						"ERROR : SubCommunity link is not present under parent community");

				log.info("Subcommunities widget now contains a link to the new subcomm created");
				driver.getFirstElement("link="+ subCommunity.getName()).click();
			}
			log.info("INFO: Validate are Owners & Members from the parent community have been added to child sub community" );
			Community_TabbedNav_Menu.MEMBERS.select(ui);

			log.info("INFO: Validate the Owner appears on the Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser.getDisplayName()), 
					"ERROR : Owner is not present in Subcommunity");

			log.info("INFO: Validate the Owner appears on the Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser3.getDisplayName()), 
					"ERROR : Owner is not present in Subcommunity");

			log.info("INFO: Validate the Owner appears on the Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser4.getDisplayName()), 
					"ERROR : Owner is not present in Subcommunity");

			log.info("INFO: Validate the Member appears on the Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser1.getDisplayName()), 
					"ERROR : Member is not present in Subcommunity");

			log.info("INFO: Validate the Member appears on the Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser2.getDisplayName()), 
					"ERROR : Member is not present in Subcommunity");

			log.info("INFO: Clicking on the Communities link");
			ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);

			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);

			ui.endTest();

	}
	
			
	/**
	 * Sub-Community Verification
	 *<ul>SUBCOMMUNITIES PART1: CREATE A MODERATED SUBCOMMUNITY WITH ALL MEMBERS FROM PARENT ADDED TO SUBCOMM
	 *<li><B>Info: </B>It can be assumed that each test-case starts with login and ends with logout and the browser being closed</li>
	 *<li><B>Step: Create a public community as owner. Add one member one additional owner to it using community API </B> </li>
	 *<li><B>Step: Using Community action create a sub community under this community </B> </li>
	 *<li><B>Step: Select the Checkbox:  Add all current members of the parent community to this SubCommunity 
	 *<li><B>Verify: Verify in Sub-Community Members area both member and additional owner are present </B> </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/6249057C2E46D47D85257C8D00569780 ">TTT- SUBCOMMUNITIES PART1: CREATE A MODERATED SUBCOMMUNITY WITH ALL MEMBERS FROM PARENT ADDED TO SUBCOMM</a></li>
	 *</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"})
	 
	        public void ownerSubcommunityActions(){

			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
									     .access(Access.PUBLIC)	
									     .tags(Data.getData().commonTag + Helper.genDateBasedRand())
									     .description("Test creating a moderated subcommunity with all members from parent" + testName)
									     .addMember(new Member(CommunityRole.MEMBERS, testUser2))
									     .addMember(new Member(CommunityRole.OWNERS, testUser3))
									     .build();
					
			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
			                                                    .access(BaseSubCommunity.Access.MODERATED)
			                                                    .UseParentmembers(true)
																.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											   					.description("Test creating a moderated subcommunity with all members from parent " + testName)
											   					.build();

			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

			if (flag) {

			    log.info("INFO: Add the Overview page to the Community and make it the landing page");
			    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);

			}
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
		
			log.info("INFO: Creating Subcommunity ");
			subCommunity.create(ui);
			
			this.checkCommunityNameFieldEmptyMsg(subCommunity);
						
			log.info("INFO: Verify subcommunity is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
					"ERROR : subcommunity is not created");
			
			log.info("INFO: Click on the Members widget View All link");
			ui.clickLinkWait(CommunitiesUIConstants.membersWidgetViewAllLink);
			
			log.info("INFO: Validate the Owner appears on Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser3.getDisplayName()), 
							  "ERROR : Owner is not present in Subcommunity");
			
			log.info("INFO: Validate the member of the parent is present on the subcommunity Members page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser2.getDisplayName()), 
							   "ERROR : Member of parent community is not present in Subcommunity");
						
			log.info("INFO: Clicking on the Communities link");
			ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);
						
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
			
		}	
	

	/**
	 *<ul>
	 *<li>PTC Subcommunity </li>
	 *<li><B>Test Scenario:</B>SUBCOMMUNITIES PART2: CREATE A RESTRICTED SUBCOMMUNITY WITH NO MEMBERS FROM PARENT ADDED TO SUBCOMM (1 of 2)</li>
	 *<li><B>Step:</B> Create a public community &add 2 additional Owners, and 2 additional Members  (so there will be 5 people total added to this community) </li>
	 *<li><B>Step:</B> Add the subcommunity widget to the community </li>
	 *<li><B>Step:</B> Create a subcommunity by clicking on the Add Your First Subcommunity link </li>
	 *<li><B>Verify:</B> Verify after creating the subcommunity that we are on the subcommunity Overview page </li>
	 *<li><B>Verify:</B> Verify the Subcommunity community card has the following links: Overview, Recent Updates, Status Updates, Members, Forums, Bookmarks & Files </li>
	 *<li><B>Verify:</B> Verify the community card has a link to the parent community </li> 
	 *<li><B>Step:</B> Click on the parent community link </li>
	 *<li><B>Verify:</B> Verify the user is on the parent community Overview page </li>
	 *<li><B>Verify:</B> Verify the subcommunity link appears on the parent community card </li>
	 *<li><B>Verify:</B> Verify that the Subcommunities widget now contains a link to the new subcomm
	 *<li><B>Verify:</B> verify that the user is returned to the child subcommunity after clicking the link
	 *<li><B>Verify:</B> Verify that only the Owners were added to the subcomm with Owner access.
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/AF92ED7B5C07C53B85257C8D00580F6A ">TTT-SUBCOMMUNITIES PART2: CREATE A RESTRICTED SUBCOMMUNITY WITH NO MEMBERS FROM PARENT ADDED TO SUBCOMM</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void addSubcommunityWidget() {

			String testName = ui.startTest();
			String commName = testName + Helper.genDateBasedRand();
			String subcommName = "SubCommunity" + Helper.genDateBasedRand();
			String gk_flag ;
			boolean value ; 
			
			BaseCommunity community = new BaseCommunity.Builder(commName)
										 .access(Access.PUBLIC)	
										 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
										 .description("Test creating a restricted subcommunity " + testName)
										 .addMember(new Member(CommunityRole.MEMBERS, testUser1))
										 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
										 .addMember(new Member(CommunityRole.OWNERS, testUser3))
										 .addMember(new Member(CommunityRole.OWNERS, testUser4))
										 .build();
		
			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(subcommName)
			                                   .access(BaseSubCommunity.Access.RESTRICTED)
			                                   .UseParentmembers(false)
			                                   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
											   .description("Test creating a restricted subcommunity " + testName)
											   .useActionMenu(false)
											   .build();
			
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);

			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);

			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);

			log.info("INFO: Click on the Community Actions link");				
			Com_Action_Menu.ADDAPP.select(ui);

			log.info("INFO: Add subcommunity widget to " + community.getName());
			ui.clickLinkWait(CommunitiesUIConstants.AddAppsSubcommunities);

			log.info("INFO: Creating Sub community ");
			subCommunity.create(ui);
			
			this.checkCommunityNameFieldEmptyMsg(subCommunity);
			
			log.info("INFO: Verify subcommunity is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
					"ERROR : subcommunity is not created");

			value = ui.checkGKSetting(Data.getData().commTabbedNav);
			gk_flag = Data.getData().commTabbedNav;

			log.info("Execute the test to check if GateKeeper setting for Tabbed Navigation is enabled");
			if(value){				
				log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
				log.info("INFO: Verify the Overview tab appears on the tabbed nav");

				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
						"ERROR: Overview tab does not appear on the tabbed nav ");

				log.info("INFO: Verify Metrics appears on the tabbed nav menu");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavMetricsTab),
						"ERROR: Metrics link does not appear on the tabbed nav menu");

				//Page refresh added to slow down automation; otherwise, presenceOfDefaultWidgetsOnTopNav 
				//step will fail
				UIEvents.refreshPage(driver);

				log.info("INFO: Verify the default widgets appear on the tabbed nav menu");
				Assert.assertTrue(ui.presenceOfDefaultWidgetsOnTopNav(),
						"ERROR: Widgets on the top nav are not correct");

				log.info("INFO: Clicking on the parent community link");
				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavParentCommunityName);

				log.info("INFO: Verify the Subcommunity field displays on the top nav for the newly created community");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavSubcommLink),
						"ERROR: The Subcommunity field does not appear on the top nav");

				log.info("INFO: Click on the subcommunity drop-down menu icon");
				ui.clickLinkWait(CommunitiesUIConstants.tabbedNavSubcommMenuIcon);

				log.info("INFO: Collect the list of subcommunities on subcomm menu");
				List<Element> subcommList = ui.collectListOfSubcommunities();   	   			

				log.info("INFO: Select the subcommunity from the drop-down menu");
				Assert.assertNotNull(ui.selectSubCommunity(subcommList,subcommName,true),
						"ERROR: Subcommunity was not found on the drop-down menu");
			}else{

				log.info("INFO: Validate Overview option is present");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
						"ERROR : Overview link is not present");

				log.info("INFO: Verify presence of all default widgets in SubCommunity");
				Assert.assertTrue(ui.presenceOfDefaultWidgetsForCommunity(),
						"ERROR: Presence of all the default widgets in Subcommunity is not correct");

				log.info("INFO: Validate the presence of the parent community in SubCommunity");
				Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage).getText(), community.getName(),
						"ERROR : Parent Community link is not present");

				log.info("INFO: Clicking on the parent link on the community card");
				ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);

				log.info("INFO: Check to make sure the Overview page is loaded and user is on the same page");		
				Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.CommunitiesLink),
						"ERROR = User is not on overview page");

				log.info("INFO: Hover over the Overview button on the left nav");
				driver.getFirstElement(CommunitiesUIConstants.leftNavSubcommunitiesButton) .hover();

				log.info("Subcommunities widget now contains a link to the new subcomm created");
				driver.getFirstElement(CommunitiesUIConstants.SubcommLeftDropdown).click();

				ui.waitForPageLoaded(driver);
				
				log.info("User returned to child sub community");
				Assert.assertEquals(driver.getTitle(), "Overview - " + subCommunity.getName(),
						"ERROR: after clicking subcommunity link user is not returned to child sub community");
			}

			log.info("INFO: Validate are Owners & Members from the parent community have been added to child sub community" );
			ui.clickLinkWait(CommunitiesUIConstants.membersWidgetViewAllLink);

			log.info("INFO: Validate the Owner appears on Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser.getDisplayName()), 
					"ERROR : Owner is not present in Subcommunity");

			log.info("INFO: Validate the Owner appears on Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser3.getDisplayName()), 
					"ERROR : Owner is not present in Subcommunity");

			log.info("INFO: Validate the Owner appears on Members full page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser4.getDisplayName()), 
					"ERROR : Owner is not present in Subcommunity");

			log.info("INFO: Validate the Member is not appearing on SubCommunity");
			Assert.assertFalse(driver.isElementPresent("link=" + testUser1.getDisplayName()), 
					"ERROR : Member is present in Subcommunity");

			log.info("INFO: Validate the Member is not appearing on SubCommunity");
			Assert.assertFalse(driver.isElementPresent("link=" + testUser2.getDisplayName()), 
					"ERROR : Member is present in Subcommunity");
						
			log.info("INFO: Clicking on the Communities link");
			ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);

			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);

			ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li>PTC Subcommunity </li>
	 *<li><B>Test Scenario:</B>SUBCOMMUNITIES PART2: CREATE A RESTRICTED SUBCOMMUNITY WITH NO MEMBERS FROM PARENT ADDED TO SUBCOMM (2 of 2)</li>
	 *<li><B>Step:</B> Create a public community as owner, add one member and one additional owner to it using community API </li>
	 *<li><B>Step:</B> Add the subcommunity widget to the community </li>
	 *<li><B>Step:</B> Create a subcommunity by clicking on the Add Your First Subcommunity link </li>
	 *<li><B>Step:</B> Navigate to the subcommunity Members page by clicking on the Members link </li>
	 *<li><B>Verify:</B> Verify the community owner is listed on the subcommunity Members page </li>
	 *<li><B>Verify:</B> Verify the community member is NOT listed on the subcommunity Members page </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/AF92ED7B5C07C53B85257C8D00580F6A ">TTT-SUBCOMMUNITIES PART2: CREATE A RESTRICTED SUBCOMMUNITY WITH NO MEMBERS FROM PARENT ADDED TO SUBCOMM</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void ownerSubcommunityWidget(){

			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										 .access(Access.PUBLIC)	
										 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
										 .description("Test creating a restricted subcommunity with no members from parent " + testName)
										 .addMember(new Member(CommunityRole.MEMBERS, testUser1))
										 .addMember(new Member(CommunityRole.OWNERS, testUser2))
										 .build();
				
			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
			                                   .access(BaseSubCommunity.Access.RESTRICTED)
   											   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
   											   .UseParentmembers(false)
   											   .description("Test creating a restricted subcommunity with no members from parent " + testName)
   											   .useActionMenu(false)
   											   .build();

			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
						
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
			}
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Click on the Community Actions link");				
			Com_Action_Menu.ADDAPP.select(ui);
			
			log.info("INFO: Add subcommunity widget to " + community.getName());	
			ui.clickLinkWait(CommunitiesUIConstants.AddAppsSubcommunities);
						
			log.info("INFO: Creating Subcommunity ");
			subCommunity.create(ui);
			
			this.checkCommunityNameFieldEmptyMsg(subCommunity);
			
			log.info("INFO: Verify subcommunity is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
					"ERROR : subcommunity is not created");
						
			log.info("INFO: Click on the Members widget View All link");
			ui.clickLinkWait(CommunitiesUIConstants.membersWidgetViewAllLink);
			
			log.info("INFO: Validate the owner is listed on the subcommunity Members page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser.getDisplayName()), 
							  "ERROR : Owner is not present on the subcommunity Members page");
			
			log.info("INFO: Validate the owner is listed on the subcommunity Members page");
			Assert.assertTrue(driver.isElementPresent("link=" + testUser2.getDisplayName()), 
							  "ERROR : Adittional Owner is not present on the subcommunity Members page");
			
			driver.changeImplicitWaits(3);
			
			log.info("INFO: Validate the community member is NOT listed on the subcommunity Members page");
			Assert.assertFalse(driver.isElementPresent("link=" + testUser1.getDisplayName()), 
							   "ERROR : Member of parent community is present on the subcommunity Members page");
				
			driver.turnOnImplicitWaits();
			
			log.info("INFO: Clicking on the Communities link");
			ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);
			
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();			
		}	
	
	/**
	 *<ul>
	 *<li>PTC Follow & Unfollow community </li>
	 *<li><B>Test Scenario:</B> COMMUNITY ACTIONS: FOLLOW & UNFOLLOW COMMUNITY </li>
	 *<li><B>Step:</B> Create a Public community using the API and Member to the community </li>
	 *<li><B>Step:</B> Login as the community owner & open the Community </li>
	 *<li><B>Verify:</B> Verify the owner can see the link to Stop Following the community </li>
	 *<li><B>Step:</B> Create a subcommunity </li>
	 *<li><B>Verify:</B> Verify the owner can see the link to Stop Following the community for the subcommunity</B> </li>
	 *<li><B>Step:</B> Navigate back to the community catalog view by clicking on Communities </li>
	 *<li><B>Step:</B> Navigate to the I'm Following view </li>
	 *<li><B>Verify:</B> The community and subcommunity appear in the view </li>
	 *<li><B>Step:</B> Login as Member</li>
	 *<li><B>Step:</B> Navigate to the I'm Following view link</li>
	 *<li><B>Verify:</B> Verify that Member can't see the community under I'm Following</li>
	 *<li><B>Step:</B> Navigate to the Public Communities view</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Follow Community link</li>
	 *<li><B>Verify:</B>Verify that the link now changes to Stop Following This Community</li>
	 *<li><B>Verify:</B>Verify that the following message appears - You are following this community and will receive updates</li>
	 *<li><B>Step:</B> Navigate to the I'm Following view</li>
	 *<li><B>Verify:</B> Verify that Member is able to see the community in I'm Following view</li>
	 *<li><B>Step:</B> Open the community</li>
	 *<li><B>Step:</B> Click on the Stop Following the Community link</li>
	 *<li><B>Verify:</B>Verify that the link now changes to Follow This Community</li>
	 *<li><B>Verify:</B>Verify that the following message appears - You have stopped following this community</li>
	 *<li><B>Step:</B> Navigate to the I'm Following view</li>
	 *<li><B>Verify:</B> Verify that Member is not able to see the community in the I'm Following view</li>
	 *<li><B>Step:</B> Login as a Owner and Cleanup by deleting the community</li>
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/45733D195DCBA23585257C900063A360">TTT-COMMUNITY ACTIONS: FOLLOW & UNFOLLOW COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void subcommunityFollow(){

			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										 .access(Access.PUBLIC)	
										 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
										 .description("Test Start Page for community " + testName)
										 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
										 .build();
				
			BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
											   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
											   .UseParentmembers(false)
											   .description("Test Start Page for Sub community " + testName)
											   .build();
			
			log.info("INFO: Create community using API");
			Community commAPI = community.createAPI(apiOwner);
		
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, commAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			/*Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
			if (flag) {
				apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
			}*/
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Validate that a user is able to see Stop Following this community link.");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity),
							  "ERROR: Link for Stop following the community is not present");
			
			log.info("INFO: Creating Sub community ");
			subCommunity.create(ui);
			
			this.checkCommunityNameFieldEmptyMsg(subCommunity);
			
			log.info("INFO: Verify subcommunity is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
					"ERROR : subcommunity is not created");
			
			log.info("INFO: Validate that a user is able to see Stop Following this subcommunity link.");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity),
							  "ERROR: Link for Stop following the community is not present");
						
			if(!isOnPremise){
				log.info("INFO: Click on Communities link on the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

			}else{
				log.info("INFO: Return to the community catalog view");
				checkGKSettingReturnToCatalogView();
			}		
			
			boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
			
			// get the community link
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			String subcommunityLink = isCardView ? CommunitiesUI.getSubCommunityLinkCardView(subCommunity) : "link=" + subCommunity.getName();
		
			
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		    ui.goToIamFollowingView(isCardView, isOnPremise);
				
			log.info("INFO: Validate that the community is present in the I'm Following view");
			Assert.assertTrue(driver.isElementPresent(communityLink),
						     "ERROR : Community is not present in the I'm Following view!!");
			
			log.info("INFO: Validate that the sub-community is present in I'm Following");
		//	ui.fluentWaitPresentWithRefresh("link=" + subCommunity.getName());
		    String subCommUUID = subCommunity.getCommunityUUID().replace("communityUuid=", "");
			subcommunityLink= "css=div#community-card-" + subCommUUID.substring(0,subCommUUID.indexOf("#"));
			Assert.assertTrue(driver.isElementPresent(subcommunityLink),
					          "ERROR : SubCommunity is not present in the I'm Following view!!");
						
			ui.logout();
			
			log.info("INFO: Log into Communities as a Member");
			ui.loadComponent(Data.getData().ComponentCommunities, true);
			ui.login(testUser2);
			
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
			ui.goToIamFollowingView(isCardView, isOnPremise);
			
			driver.changeImplicitWaits(5);
			
			log.info("INFO: Verify Member should not see the community in I'm Following!");
			Assert.assertFalse(driver.isElementPresent(communityLink),
					"ERROR : Member is able to see the community in I'm Following!!");
			
			driver.turnOnImplicitWaits();
			
			log.info("INFO: Clicking on the Public community link from the LeftNavigation");
			ui.goToPublicView(isCardView);
			
			log.info("INFO: Open the community");
			ui.typeText(CommunitiesUIConstants.catalogFilterCardView, community.getName());
			ui.fluentWaitPresentWithRefresh("css=div[aria-label='" + community.getName() + "']");
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Start following the community" );
			ui.clickLinkWait(CommunitiesUIConstants.FollowThisCommunity);
			
			log.info("INFO: Verify that the link now changes to Stop Following This Community");
			Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.StopFollowingThisCommunity),
								"ERROR: Stop Following This Community link is not displayed");
						
			log.info("INFO: Verify that the following alert message displays at the top of the page");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FollowCommunityMsg),
								"ERROR: You are following this community and will receive updates about community content message is not displayed");
						
			if(!isOnPremise){
				log.info("INFO: Click on Communities link on the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

			}else{
				log.info("INFO: Return to the community catalog view");
				checkGKSettingReturnToCatalogView();
			
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
			ui.goToIamFollowingView(isCardView, isOnPremise);
			
			log.info("INFO: Verify Member is able to see the community in I'm Following!");
			ui.fluentWaitPresentWithRefresh(communityLink);
			Assert.assertTrue(driver.isElementPresent(communityLink),
					"ERROR : Member is not able to see the community in I'm Following!!");
						
			log.info("INFO: Open the community");
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Click on stop following the community");
			ui.clickLinkWait(CommunitiesUIConstants.StopFollowingThisCommunity);
			
			log.info("INFO: Verify that the link now changes to Follow This Community");
			Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.FollowThisCommunity),
								"ERROR:  Follow This Community link is not displayed");
			
			log.info("INFO: Verify that the stopped following alert message displays at the top of the page");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().StopFollowingCommunityMsg),
								"ERROR: You have stopped following this community message is not displayed");
									
			if(!isOnPremise){
				log.info("INFO: Click on Communities link on the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

			}else{
				log.info("INFO: Return to the community catalog view");
				checkGKSettingReturnToCatalogView();
			}
			
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");	
			ui.goToIamFollowingView(isCardView, isOnPremise);

			log.info("INFO: Verify Member should not see the community in I'm Following!");
			Assert.assertFalse(driver.isElementPresent(communityLink),
					"ERROR : Member is able to see the community in I'm Following!!");
			
			log.info("INFO: Log out as the Member & log in as the Owner");
			ui.logout();			
			ui.loadComponent(Data.getData().ComponentCommunities, true);
			ui.login(testUser);
							
			log.info("INFO: Open community");
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
		
			}
		}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>SUBCOMMUNITIES PART2: CREATE A RESTRICTED SUBCOMMUNITY WITH NO MEMBERS FROM PARENT ADDED TO SUBCOMM (2 of 2)</li>
	 *<li><B>Step:</B> Create a public community using community API </li>
	 *<li><B>Step:</B> Bring up the create subcommunity form </li>
	 *<li><B>Verify:</B> Verify the Public Access radio button displays </li>
	 *<li><B>Verify:</B> Verify the Moderated Access radio button displays </li>
	 *<li><B>Verify:</B> Verify the Restricted Access radio button displays </li>
	 *<li><B>Step:</B> Clearnup: Delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/AF92ED7B5C07C53B85257C8D00580F6A ">TTT-SUBCOMMUNITIES PART2: CREATE A RESTRICTED SUBCOMMUNITY WITH NO MEMBERS FROM PARENT ADDED TO SUBCOMM</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"})
	public void addSubcommAccessRadioButtons() throws Exception {

			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
										 .access(Access.PUBLIC)	
										 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
										 .description("Test Start Page for community " + testName)
										 .addMember(new Member(CommunityRole.MEMBERS, testUser2))
										 .build();
			
			
			//create community
			log.info("INFO: Create community using API");
			Community commAPI = community.createAPI(apiOwner);
		
			//add the UUID to community
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, commAPI);
		
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			//navigate to the API community
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
													
			//click on the Create Subcommunities link
			log.info("INFO: Click on the Create Subcommunities link");
			Com_Action_Menu.CREATESUB.select(ui);
			
			//verify the Access radio buttons on the create subcomm form: Public, Moderated, Restricted
			log.info("INFO: Verify the Public Access radio button displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.SubCommunityAccessPublic),
					"ERROR: Public access radio button does not exist");
			
			log.info("INFO: Verify the Moderated Access radio button displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.SubCommunityAccessModerated),
					"ERROR: Moderated access radio button does not exist");
			
			log.info("INFO: Verify the Restricted Access radio button displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.SubCommunityAccessPrivate),
					"ERROR: Restricted access radio button does not exist");
			//Cleanup: Delete community
			log.info("INFO: Removing community " + testName);
			community.delete(ui, testUser);
			
			
		}
	
	/**
	* check to see if the GK setting is enabled or not and then return to the catalog view
	*/	
	private void checkGKSettingReturnToCatalogView() {
		String gk_flag;
		boolean value;
		
		value = ui.checkGKSetting(Data.getData().commTabbedNav);
		gk_flag = Data.getData().commTabbedNav;	
		
		log.info("Execute the test to check if the GateKeeper setting for Tabbed Navigation is enabled");

		if(value){				
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			log.info("INFO: Verify the Overview tab appears on the tabbed nav");

			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
					"ERROR: Overview tab does not appear on the tabbed nav ");

			UIEvents.refreshPage(driver);

			if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
			   Community_MegaMenu_Menu.MY_COMMUNITIES.select(ui);
			}
			else {
			   Community_MegaMenu_Menu.IM_AN_OWNER.select(ui);
			}

		}else { 
			//Click on communities link
			log.info("INFO: Clicking on the communities link");
			ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
		}	
	}

	/**
	* check to make sure the community name field is not empty.  
	* if the error message appears, re-enter the subcommunity name
	*/
	
	private void checkCommunityNameFieldEmptyMsg(BaseSubCommunity subCommunity) {
		driver.changeImplicitWaits(3);
		log.info("INFO: Check for the message that the community name should not be empty");
		if (driver.isTextPresent(Data.getData().communityNameFieldIsEmptyMsg)){
			log.info("INFO: Entering community name " + subCommunity.getName());
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).clear();
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(subCommunity.getName());

			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			if(subCommunity.isUseParentMembers()) {
				log.info("INFO: Select the checkbox to add members from the parent community to the subcommunity");
				this.driver.getFirstElement(CommunitiesUIConstants.AddMemberscheckbox).click();
			}

			log.info("INFO: Saving the sub community " + subCommunity.getName());	
			this.driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();
		}
		driver.turnOnImplicitWaits();
	}
}


