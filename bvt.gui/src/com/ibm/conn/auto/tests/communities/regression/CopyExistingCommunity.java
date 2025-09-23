package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_MegaMenu_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CopyExistingCommunity extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(CopyExistingCommunity.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser, testUser1, guestUser;
	private APICommunitiesHandler apiOwner;
	private APIFileHandler	fileHandler;
	private String serverURL;
	private boolean isOnPremise;
	private Community comAPI1,comAPI2,comAPI3,comAPI4,comAPI5,comAPI6,comAPI7,comAPI8,comAPI9,comAPI10,comAPI11,comAPI12,comAPI13,comAPI14,comAPI15,comAPI16,comAPI17,comAPI18,comAPI19,comAPI20,comAPI21,comAPI22,comAPI23;
	private BaseCommunity community1,community2,community3,community4,community5,community6,community7,community8,community9,community10,community11,community12,community13,community14,community15,community16,community17,community18,community19,community20,community21,community22,community23;	
    private BaseFile fileA;
	
    @BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();

		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		fileHandler = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		//Check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		//Set up members
		Member member1 = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Test communities
		community1 = new BaseCommunity.Builder("ValidateCopyLink" + Helper.genDateBasedRandVal())
		                              .access(Access.PUBLIC)
		                              .description("Checking 'Copy Community' menu item under Community Actions")
		                              .build();
		
		community2 = new BaseCommunity.Builder("CancelOutOfCopyCommForm" + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .description("Canceling out of Copy Community form, new community should not be created")
                                      .build();
		
		community3 = new BaseCommunity.Builder("CopyCommunitySave" + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
                                      .description("New community is created by using Copy Community feature")
                                      .build();
		
		community4 = new BaseCommunity.Builder("CopySubComm" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("New 'top level' community is created from copying existing Subcommunity")
                                      .build();
		
		community5 = new BaseCommunity.Builder("CopyCommEditFields" + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
                                      .description("Prepopulated fields in 'Copying an Existing Community' form can be edited ")
                                      .build();
		
		community6 = new BaseCommunity.Builder("CopyCommVerifyAccessPublic" + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Public(Open) access checkbox should be selected as default when making a copy of a public(open) community")
                                      .build();
		
		community7 = new BaseCommunity.Builder("CopyCommVerifyAccessModerated" + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .description("Modereated access checkbox should be selected as default when making a copy of a moderated community")
                                      .build();
		
		community8 = new BaseCommunity.Builder("CopyCommVerifyAccessInternalRestricted" + Helper.genDateBasedRandVal())
									  .access(Access.RESTRICTED)
									  .shareOutside(false) 			// internal
                                      .description("Internal Restricted access checkbox should be selected as default when making a copy of an internal restricted community")
                                      .build();

		community9 = new BaseCommunity.Builder("CommActions_MemberCopyComm" + Helper.genDateBasedRandVal())
		  							  .access(Access.PUBLIC)
		  							  .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		  							  .description("Member can copy community via Community Actions")
		  							  .addMember(member1)
		  							  .build();

		community10 = new BaseCommunity.Builder("chooseBySearching_OwnerCopyComm" + Helper.genDateBasedRandVal())
									   .access(Access.MODERATED)
									   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									   .description("Community owner can choose by searching for communities when selecting a community to copy from")
									   .build();

		community11 = new BaseCommunity.Builder("chooseBySearching_MemberCopyComm" + Helper.genDateBasedRandVal())
									   .access(Access.RESTRICTED)
									   .shareOutside(false) 			// internal									   
									   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									   .description("Community member can choose by searching for communities when selecting a community to copy from")
									   .addMember(member1)
									   .build();

		community12 = new BaseCommunity.Builder("nonMemberCopyPublicCommBySearch" + Helper.genDateBasedRandVal())
									   .access(Access.PUBLIC)
									   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									   .description("Nonmember can copy public community by searching")
									   .build();

	    community13 = new BaseCommunity.Builder("nonMemberCopyModeratedCommBySearch" + Helper.genDateBasedRandVal())
	    							   .access(Access.MODERATED)
	    							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
	    							   .description("Nonmember can copy moderated community by searching")
	    							   .build();

	    community14 = new BaseCommunity.Builder("nonMemberCanNotCopyListedComm" + Helper.genDateBasedRandVal())
		   							   .access(Access.RESTRICTED)
		   							   .shareOutside(false) 			// internal
		   							   .rbl(true)						// listed
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Nonmember can NOT copy listed restricted community")
		   							   .build();

	    community15 = new BaseCommunity.Builder("createMultipleCopiesOfListedComm" + Helper.genDateBasedRandVal())
		   							   .access(Access.RESTRICTED)
		   							   .shareOutside(false) 			// internal
		   							   .rbl(true)						// listed
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Appended number should increment when there are multiple copies of a listed restricted community")
		   							   .build();

	    community16 = new BaseCommunity.Builder("createMultipleCopiesOfModeratedComm" + Helper.genDateBasedRandVal())
		   							   .access(Access.MODERATED)
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Appended number should increment when there are multiple copies of a moderated community")
		   							   .build();

		community17 = new BaseCommunity.Builder("createMultipleCopiesOfPublicComm" + Helper.genDateBasedRandVal())
		   							   .access(Access.PUBLIC)
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Appended number should increment when there are multiple copies of a public community")
		   							   .build();
	
		community18 = new BaseCommunity.Builder("createMultipleCopiesOfRestrictedComm" + Helper.genDateBasedRandVal())
		   							   .access(Access.RESTRICTED)
		   							   .shareOutside(false) 			// internal									   
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("No appended number when there are multiple copies of a restricted community(not listed)")
		   							   .build();

		community19 = new BaseCommunity.Builder("memberNotCopied" + Helper.genDateBasedRandVal())
		   							   .access(Access.RESTRICTED)
		   							   .shareOutside(false) 			// internal									   
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Copy Community - Community member is not copied")
		   							   .addMember(member1)
		   							   .build();

		community20 = new BaseCommunity.Builder("widgetContentNotCopied" + Helper.genDateBasedRandVal())
									   .access(Access.PUBLIC)								   
									   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									   .description("Copy Community - Widget content is not copied")
									   .build();

		community21 = new BaseCommunity.Builder("removedWidgetNotCopied" + Helper.genDateBasedRandVal())
		   							   .access(Access.MODERATED)								   
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Copy Community - Removed widget is not copied")
		   							   .build();

		community22 = new BaseCommunity.Builder("hiddenWidgetIsCopied" + Helper.genDateBasedRandVal())
		   							   .access(Access.RESTRICTED)
		   							   .shareOutside(false) 			// internal									   
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Copy Community - Hidden widget is copied")
		   							   .build();

		community23 = new BaseCommunity.Builder("richContentInRTEIsCopied" + Helper.genDateBasedRandVal())
		   							   .access(Access.PUBLIC)									   
		   							   .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		   							   .description("Rich content widget and its content are copied from the original community")
		   							   .build();
		
		log.info("INFO: Create communities via the API");
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

		//Test files
		fileA = new BaseFile.Builder(Data.getData().file1)
		 					.comFile(true)
		 					.extension(".jpg")
		 					.build();
		
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
		apiOwner.deleteCommunity(comAPI22);
		apiOwner.deleteCommunity(comAPI23);
		
	}
		
	
	/**
	* copyExistingComm_ValidateAllViews() 
	*<ul>
	*<li><B>Info:</B> Validate the "Start a Community" menu & the "Copy an Existing Community" option are visible & work from ALL the Catalog views</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on each view link except Trash from the Left Navigation</li>
	*<li><B>Step:</B> Click on "Start a Community" menu</li>
	*<li><B>Step:</B> Select "Copy an Existing Community" option</li>
	*<li><B>Verify:</B> User should see the "Copy an Existing Community" Search dialog box</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*/
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void copyExistingComm_ValidateAllViews() {
		
		String testName = ui.startTest();	
			
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
		
			//I'm an Owner view
			log.info("INFO: Clicking on the I'm an Owner link from the Left Navigation");
			ui.goToOwnerView(isCardView);
			
			log.info("INFO: Validate 'Copy an Existing Community' menu option");
			validateCopyAnExistingStructureOption(isCardView);

			//I'm Member view
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);
			
			log.info("INFO: Validate 'Copy an Existing Community' menu option");
			validateCopyAnExistingStructureOption(isCardView);
			
			//I'm Following view
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
			ui.goToIamFollowingView(isCardView, isOnPremise);	

			log.info("INFO: Validate 'Copy an Existing Community' menu option");
			validateCopyAnExistingStructureOption(isCardView);
			
			//I'm Invited view
			log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
			ui.goToInvitedView(isCardView);
			
			log.info("INFO: Validate 'Copy an Existing Community' menu option");
			validateCopyAnExistingStructureOption(isCardView);
			
			//Public Communities
			log.info("INFO: Clicking on the Public community link from the LeftNavigation");
			ui.goToPublicView(isCardView);
			
			log.info("INFO: Validate 'Copy an Existing Community' menu option");
			validateCopyAnExistingStructureOption(isCardView);
				
			ui.endTest();	
	
		}else{

			log.info("INFO: 'Copy an Existing Community' menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

		}//End of if and else	

	}
	
	
	/**
	* createCopyCatalog_AnonymousAccess() 
	*<ul>
	*<li><B>Info:</B> Clicking on "Copy an Existing Community" option will take anonymous user to the Login page</li>
	*<li><B>Step:</B> Open Communities URL</li>
	*<li><B>Verify:</B> The URL redirects to My Organization Communities</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Verify:</B> Start a Community button is present</li>
	*<li><B>Step:</B> Click on Start a Community button</li>
	*<li><B>Step:</B> Click on Copy an Existing Community option</li>
	*<li><B>Verify:</B> Log in page displays</li>
	*<li><B>Step:</B> Enter the username and password</li>
	*<li><B>Step:</B> Click the Log In button</li>
	*<li><B>Verify:</B> User should see the "Copying an Existing Community" dialog box</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*Note: On Prem only. SmartCloud does not support anonymous access.
	*/
	@Test (groups = {"regression"})
	public void createCopyCatalog_AnonymousAccess() {
		
		String testName = ui.startTest();
		
		//Get communities' public URL
		String CommunitiesURL = Data.getData().ComponentCommunities.split("login")[0];
	
		log.info("INFO: Load Communities component");
		ui.loadComponent(CommunitiesURL);
		ui.toggleToOldUI();
		
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
				
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
			
			log.info("INFO: Start Community ...  Copy an Existing Community");
			startCommunityCopyExisting(isCardView);

			log.info("INFO: Login page displays by checking there is a 'User name' field");
			Assert.assertTrue(ui.isElementPresent(BaseUIConstants.Login_Button),
					"ERROR: Login page was not found");

			log.info("INFO: Log in as a valid user - enter user's email & password and then click Login button");
			ui.fluentWaitPresent(BaseUIConstants.USERNAME_FIELD);
			ui.typeText(BaseUIConstants.USERNAME_FIELD, testUser.getEmail());
			ui.typeText(BaseUIConstants.Password_FIELD, testUser.getPassword());
			ui.clickLinkWait(BaseUIConstants.Login_Button);
			ui.toggleToOldUI();
			
			log.info("Validate 'Copying an Existing Community' search dialog box appears by verifying the URL");
			System.out.println(driver.getCurrentUrl());  
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: Copying an Existing Community search dialog box does not appear");
			
			ui.endTest();
		
	    }else{

			log.info("INFO: Copy Community feature is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");	
			
	    }//End of if and else

	}	

	
	/**
	* createCopyCatalog_GuestUser()
	*<ul>
	*<li><B>Info: No Start a Community menu for guest user</B></li>
	*<li><B>Step: Log in to Communities as a guest user</B></li>
	*<li><B>Verify: No Start a Community menu is present</B> </li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*Note: SmartCloud only.
	*/
	@Test (groups = {"regressioncloud"} , enabled=false )
	public void createCopyCatalog_GuestUser() {
		
		ui.startTest();

		guestUser = cfg.getUserAllocator().getGuestUser();

		log.info("INFO: Load component and Log in as a Guest");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(guestUser); 

		log.info("INFO: Validate that the new Start a Community menu is not present");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.StartACommunityMenu),
				"ERROR: The new Start a Community menu should not be present, but it is");
		
		ui.endTest();

	}	

	
	/**
	* startFromNew_AnonymousAccess() 
	*<ul>
	*<li><B>Info:</B> Clicking on "Start from New" option will take anonymous user to the Login page</li>
	*<li><B>Step:</B> Open Communities URL</li>
	*<li><B>Verify:</B> The URL redirects to My Organization Communities</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Verify:</B> Start a Community button is present</li>
	*<li><B>Step:</B> Click on Start a Community button</li>
	*<li><B>Step:</B> Click on Start from New option</li>
	*<li><B>Verify:</B> Log in page displays</li>
	*<li><B>Step:</B> Enter the username and password</li>
	*<li><B>Step:</B> Click the Log In button</li>
	*<li><B>Verify:</B> User should see the "Start a Community" form</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*Note: On Prem only. SmartCloud does not support anonymous access.
	*/
	@Test (groups = {"regression"})
	public void startFromNew_AnonymousAccess() {
		
		String testName = ui.startTest();

		//Get communities' public URL
		String CommunitiesURL = Data.getData().ComponentCommunities.split("login")[0];
		
		log.info("INFO: Load Communities component");
		ui.loadComponent(CommunitiesURL);
		ui.toggleToOldUI();
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
		
			log.info("INFO: Click on Start from New");
			startCommunityFromNew(isCardView);
					
			log.info("INFO: Login page displays by checking there is a 'User name' field");
			Assert.assertTrue(ui.isElementPresent(BaseUIConstants.Login_Button),
					"ERROR: Login page was not found");

			log.info("INFO: Log in as a valid user - enter user's email & password and then click Login button");
			ui.fluentWaitPresent(BaseUIConstants.USERNAME_FIELD);
			ui.typeText(BaseUIConstants.USERNAME_FIELD, testUser.getEmail());
			ui.typeText(BaseUIConstants.Password_FIELD, testUser.getPassword());
			ui.clickLinkWait(BaseUIConstants.Login_Button);
			ui.toggleToOldUI();
						
			log.info("INFO: Validate 'Start a Community' form appears by verifying the URL");
			System.out.println(driver.getCurrentUrl());  
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.communityCreateURL),
					"ERROR: Start a Community form does not appear");
			
			ui.endTest();
		
	    }else{

			log.info("INFO: Copy Community feature is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");	
			
	    }//End of if and else

	}	

	
	/**
	* startFromNew_Cancel() 
	*<ul>
	*<li><B>Info:</B> New community is not created when user canceling out of 'Start a Community' form</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community button</li>
	*<li><B>Step:</B> Select Start from New menu option</li>
	*<li><B>Verify:</B> 'Start a Community' form appears</li>
	*<li><B>Step:</B> Enter the Name of Community</li>
	*<li><B>Step:</B> Click on the Access Advanced Features link to expand the section</li>
	*<li><B>Step:</B> Select the Access field radio button 'Moderated'</li>
	*<li><B>Step:</B> Enter the description in CKEditor</li>
	*<li><B>Step:</B> Click on Cancel button</li>
	*<li><B>Step:</B> Click on the I'm an Owner link from the Left Navigation</li>
	*<li><B>Verify:</B> New community is not created</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*/
	@Test (groups = {"regression", "regressioncloud"})
	public void startFromNew_Cancel() {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();	
			 
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
 
		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			//Create new community base state object
			BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
			                                           .access(Access.MODERATED)	
			                                           .description(Data.getData().descriptionModeratedComm)
			                                           .build();

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}

			log.info("INFO: Click on Start from New");
			startCommunityFromNew(isCardView);		
			
			log.info("INFO: Validate 'Start a Community' form appears by verifying the URL");
			System.out.println(driver.getCurrentUrl());  
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.communityCreateURL),
					"ERROR: Start a Community form does not appear");			
			
			log.info("INFO: Entering community name " + community.getName());
			driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			log.info("INFO: Select the Access field radio button 'Moderated'");
			driver.getFirstElement(CommunitiesUIConstants.CommunityAccessModerated).click();
		
			log.info("INFO: Enter text into the description field");
			ui.typeInCkEditor(community.getDescription());
				
			log.info("INFO: Click on the Cancel button");
			driver.getFirstElement(CommunitiesUIConstants.CancelButton).click();

			log.info("INFO: Click on the I'm an Owner link from the Left Navigation");
			ui.goToOwnerView(isCardView);
			
			log.info("INFO: Verify new community is not created");
			Assert.assertFalse(ui.isTextPresent(community.getName()),
					"ERROR: New community should not be created, but it does");		
					
			ui.endTest();	
	
		}else{

			log.info("INFO: 'Start from New' menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

		}//End of if and else	

	}
	
	
	/**
	* startFromNew_Save() 
	*<ul>
	*<li><B>Info:</B> Clicking on the new 'Start from New' menu option to create a new public community</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community button</li>
	*<li><B>Step:</B> Select Start from New menu option</li>
	*<li><B>Verify:</B> 'Start a Community' form appears</li>
	*<li><B>Step:</B> Enter the Name of Community</li>
	*<li><B>Step:</B> Click on the Access Advanced Features link to expand the section</li>
	*<li><B>Step:</B> If the environment is SmartCloud, then select the Access field radio button 'Open'</li>
	*<li><B>Step:</B> If the environment is On-Prem, 'Public' radio button is already selected as default</li>
	*<li><B>Step:</B> Enter the description in CKEditor</li>
	*<li><B>Step:</B> Click on Save button</li>
	*<li><B>Verify:</B> New community is created by validating the title and description</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*/
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void startFromNew_Save() {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
				 
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
 
		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			//Create new community base state object
			BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
			                                           .access(Access.PUBLIC)	
			                                           .description(Data.getData().descriptionPublicComm)
			                                           .build();

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
			
			log.info("INFO: Click on Start from New");
			startCommunityFromNew(isCardView);											
			
			log.info("INFO: Validate 'Start a Community' form appears by verifying the URL");
			System.out.println(driver.getCurrentUrl());  
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.communityCreateURL),
					"ERROR: Start a Community form does not appear");			
			
			log.info("INFO: Entering community name " + community.getName());
			driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			//Determine of SC or OP			
			if(isOnPremise){

				log.info("INFO: The environment is On-Prem, Public access option is checked by default.");
				
			}else{

				log.info("INFO: The environment is Smart Cloud");

				//Select the Access field radio button 'Open'
				log.info("INFO: Select the Access field radio button 'Open'");
				driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPublic).click();

			}		
			
			log.info("INFO: Enter text into the description field");
			ui.typeInCkEditor(community.getDescription());
		
			log.info("INFO: Click the Save button on the create form");
			driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
		
			log.info("INFO: Verify new community is created by checking the title");
			Assert.assertTrue(driver.getTitle().contains(community.getName()),
					"ERROR : New community is not created");

			log.info("INFO: Verify community description");
			Assert.assertTrue(ui.fluentWaitTextPresent(community.getDescription()),
					"ERROR: Community description is not displayed");
			
			log.info("Clean Up: Need to manually delete the community since it's not created by API");
			community.delete(ui, testUser);
		
			ui.endTest();	
	
		}else{

			log.info("INFO: 'Start from New' menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

		}//End of if and else	

	}		

	
	/**
	* startFromNew_ValidateAllViews() 
	*<ul>
	*<li><B>Info:</B> Validate the "Start a Community" menu & the "Start from New" option are visible & work from ALL the Catalog views except for TRASH</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on each view link except Trash from the Left Navigation</li>
	*<li><B>Step:</B> Click on "Start a Community" menu</li>
	*<li><B>Step:</B> Select "Start from New" option</li>
	*<li><B>Verify:</B> User should see the "Start a Community" form</li>
	*<li><B>Step:</B> Click on Cancel button</li>
	*<li><B>Step:</B> Click on the Trash link from the Left Navigation</li>
	*<li><B>Verify:</B> "Start a Community" menu is not present</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/46652D02F93050018525806B007658A8">TTT - COPY COMMUNITY 4: CREATE COMMUNITY BUTTON IS NOW A MENU (D59)</a></li>
	*</ul>
	*/
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void startFromNew_ValidateAllViews() {
		
		String testName = ui.startTest();	
				
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Existing Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
				
			//I'm an Owner view
			log.info("INFO: Clicking on the I'm an Owner link from the Left Navigation");
			ui.goToOwnerView(isCardView);
			
			log.info("INFO: Validate 'Start from New' menu option");
			validateStartFromNewOption(isCardView);

			//I'm Member view
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);
			
			log.info("INFO: Validate 'Start from New' menu option");
			validateStartFromNewOption(isCardView);
			
			//I'm Following view
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
			ui.goToIamFollowingView(isCardView, isOnPremise);	

			log.info("INFO: Validate 'Start from New' menu option");
			validateStartFromNewOption(isCardView);
			
			//I'm Invited view
			log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
			ui.goToInvitedView(isCardView);
			
			log.info("INFO: Validate 'Start from New' menu option");
			validateStartFromNewOption(isCardView);
			
			//Public Communities
			log.info("INFO: Clicking on the Public community link from the LeftNavigation");
			ui.goToPublicView(isCardView);
			
			log.info("INFO: Validate 'Start from New' menu option");
			validateStartFromNewOption(isCardView);

			//Trash view
			log.info("INFO: Clicking on the Trash link from the LeftNavigation");
			ui.goToTrashView(isCardView);			

			log.info("INFO: Validate that the new Start a Community menu is not present");
			Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.StartACommunityMenu),
					"ERROR: The new Start a Community menu should not be present, but it is");		
			
			ui.endTest();	
	
		}else{

			log.info("INFO: 'Start from New' menu is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

		}//End of if and else	

	}
	

	/**
	* createCopyCommActions_ValidateCopyLink()
	*<ul>
	*<li><B>Info:</B> Checking 'Copy Community' menu item under Community Actions</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Click on Community Actions on Overview page</li>
	*<li><B>Verify:</B> Action 'Copy Community' is listed on the drop-down menu</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_ValidateCopyLink() {

		String testName = ui.startTest();

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community1.navViaUUID(ui);
		
			log.info("INFO: Click on Community Actions");
			Com_Action_Menu.CUSTOMIZE.open(ui);
		
			log.info("INFO: Verify the action 'Copy Community' is listed on the drop-down menu");
			Assert.assertTrue(driver.isTextPresent(Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.getMenuItemText()),
					"ERROR: The Copy Community link should appear on the drop-down menu, but does not");
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			


	/**
	* createCopyCommActions_Cancel()
	*<ul>
	*<li><B>Info:</B> Canceling out of Copy Community form, new community should not be created.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' form displays</li>
	*<li><B>Step:</B> Click on Cancel</li>
	*<li><B>Step:</B> Navigate to I'm an Owner view</li>
	*<li><B>Verify:</B> New copy is NOT created</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_Cancel() {

		String testName = ui.startTest();
	
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community2.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
					
			log.info("INFO: Click on Cancel button");
			ui.clickLinkWait(CommunitiesUIConstants.CancelButton);

			log.info("INFO: Navigate to I'm an Owner view");
			ui.goToOwnerView(isCardView);
		
			log.info("INFO: Verify new copy is not created");
			Assert.assertFalse(ui.isTextPresent(community2.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR : New copy should not be created, but it does");
				
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			

	
	/**
	* createCopyCommActions_Save()
	*<ul>
	*<li><B>Info:</B> New community is created by using Copy Community feature.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original community</li>
	*<li><B>Verify:</B> Tag is the same as in the original community</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_Save() {

		String testName = ui.startTest();	
	
		log.info("INFO: Get UUID of community");
		community3.getCommunityUUID_API(apiOwner, comAPI3);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community3.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);

			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community3.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			log.info("INFO: Verify community description is the same as in the original community");
			Assert.assertTrue(ui.fluentWaitTextPresent(community3.getDescription()),
					"ERROR: Community description is not the same as in the original community");
		
			log.info("INFO: Verify tag is the same as in the original community");
			System.out.println("link=" + community3.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + community3.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original community");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community3.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			

		
	/**
	* createCopyCommActions_ EditFields()
	*<ul>
	*<li><B>Info:</B> New community is created from using Copy Community feature and prepopulated fields in 'Copying an Existing Community' form can be edited.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Change the community name</li>
	*<li><B>Step:</B> Change the Access field option from 'Moderated' to 'Restricted'</li>
	*<li><B>Step:</B> Change the Description</li>
	*<li><B>Step:</B> Change the tag name</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created by validating the updated community name is present</li>
	*<li><B>Verify:</B> Restricted icon displays</li>
	*<li><B>Verify:</B> Updated description is present</li>
	*<li><B>Verify:</B> Updated tag is present</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_EditFields() {

		String testName = ui.startTest();
		String updateCommName = "*(star), 100% and $500";
		String updateTag = "youme";
		String updateDescription = "1 + 1 = 2";
		
		log.info("INFO: Get UUID of community");
		community5.getCommunityUUID_API(apiOwner, comAPI5);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			apiOwner.editStartPage(comAPI5, StartPageApi.OVERVIEW);
		}

 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
							
			log.info("INFO: Navigate to the community using UUID");
			community5.navViaUUID(ui);
			ui.waitForPageLoaded(driver);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);

			log.info("INFO: Change the community name");
			ui.waitForPageLoaded(driver);
			ui.waitForJQueryToLoad(driver);
			ui.waitForCkEditorReady();
			driver.getFirstElement(CommunitiesUIConstants.CommunityName).clear();
			driver.getFirstElement(CommunitiesUIConstants.CommunityName).typeWithDelay(community5.getName() + updateCommName);

			log.info("INFO: Change the Access field option from 'Moderated' to 'Restricted'");
			driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPrivate).click();
		
			log.info("INFO: Change the description");
			ui.typeInCkEditor(community5.getDescription() + updateDescription);
		
			log.info("INFO: change the tag name");
			driver.getSingleElement(CommunitiesUIConstants.CommunityTag).clear();
			driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(updateTag);
			
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

			log.info("INFO: Verify new copy is created by validating updated community name is present");
			Assert.assertTrue(ui.fluentWaitTextPresent(community5.getName() + updateCommName),
					"ERROR: Updated community name is not displayed");	
			
			log.info("INFO: Verify the updated community description displays");
			Assert.assertTrue(ui.fluentWaitTextPresent(community5.getDescription() + updateDescription),
					"ERROR: Updated community description is not displayed");
					
			log.info("INFO: Verify the Restricted icon displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.RestrictedIconOverviewPage),
					"ERROR: The Restricted icon does not appear, but should");
				
			log.info("INFO: Verify the updated community tag displays");
			System.out.println("link="+community5.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + updateTag),
					"ERROR: Updated community tag is not displayed");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community5.getName()+updateCommName, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			


	/**
	* createCopyCommActions_VerifyAccessPublic()
	*<ul>
	*<li><B>Info:</B> Public(Open) access checkbox should be selected as default when making a copy of a public(open) community.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Verify:</B> Public access radio button is selected</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created by validating the name is present</li>
	*<li><B>Verify:</B> No icons display for public type</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_VerifyAccessPublic() {

		String testName = ui.startTest();

		log.info("INFO: Get UUID of community");
		community6.getCommunityUUID_API(apiOwner, comAPI6);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
 
		log.info("Execute the test if GateKeeper setting for Copy Community Structure is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
							
			log.info("INFO: Navigate to the community using UUID");
			community6.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		 	
			log.info("INFO: Verify the public access radio button is selected");
			String publicAccessRadioBtn="true";
			Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPublic).getAttribute("checked").contentEquals(publicAccessRadioBtn),
					"ERROR: The Public access radio button is not selected");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

			log.info("INFO: Verify new copy is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(community6.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");
			
			log.info("INFO: Verify public community does not displays any icons");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.RestrictedIconOverviewPage),
					"ERROR: Public community should not display the Restricted icon, but does");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.ModeratedIconOverviewPage),
					"ERROR: Public community should not display the Moderated icon, but does");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.ExternalIconOverviewPage),
					"ERROR: Public community should not display the External icon, but does");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community6.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
		}else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

		}//End of if and else	
		
	}			

	
	/**
	* createCopyCommActions_VerifyAccessModerated()
	*<ul>
	*<li><B>Info:</B> Moderated access checkbox should be selected as default when making a copy of a moderated community.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Verify:</B> Moderated access radio button is selected</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created by validating the name is present</li>
	*<li><B>Verify:</B> Moderated icon displays</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_VerifyAccessModerated() {
		String testName = ui.startTest();

		log.info("INFO: Get UUID of community");
		community7.getCommunityUUID_API(apiOwner, comAPI7);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community7.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		  	
			log.info("INFO: Verify the Moderated access radio button is selected");
			String moderatedAccessRadioBtn="true";
			Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.CommunityAccessModerated).getAttribute("checked").contentEquals(moderatedAccessRadioBtn),
					"ERROR: The Moderated access radio button is not selected");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
	
			log.info("INFO: Verify new copy is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(community7.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");
		
			log.info("INFO: Verify the Moderated icon displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.ModeratedIconOverviewPage),
					"ERROR: Moderated icon is not displayed");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community7.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
		
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			

	
	/**
	* createCopyCommActions_VerifyAccessInternalRestricted()
	*<ul>
	*<li><B>Info:</B> Internal Restricted access checkbox should be selected as default when making a copy of an internal restricted community.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create an internal restricted community using API</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Verify:</B> Internal Restricted access radio button is selected</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created by validating the name is present</li>
	*<li><B>Verify:</B> Restricted icon displays</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_VerifyAccessInternalRestricted() {

		String testName = ui.startTest();
		
		log.info("INFO: Get UUID of community");
		community8.getCommunityUUID_API(apiOwner, comAPI8);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
									
			log.info("INFO: Navigate to the community using UUID");
			community8.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		 	
			log.info("INFO: Verify the Restricted access radio button is selected");
			String restrictedAccessRadioBtn="true";
			Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPrivate).getAttribute("checked").contentEquals(restrictedAccessRadioBtn),
					"ERROR: The Restricted access radio button is not selected");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
	
			log.info("INFO: Verify new copy is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(community8.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");
		
			log.info("INFO: Verify the Restricted icon displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.RestrictedIconOverviewPage),
					"ERROR: Restricted icon is not displayed");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community8.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		

	
	/**
	* createCopyCommActions_CopySubComm()
	*<ul>
	*<li><B>Info:</B> New community is created by copying an existing subcommunity.</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Create a subcommunity</li>
	*<li><B>Step:</B> In the subcommunity, click on Community Actions/Copy Community</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original subcommunity</li>
	*<li><B>Verify:</B> Tag is the same as in the original subcommunity</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createCopyCommActions_CopySubComm() {

		String testName = ui.startTest();

		//Create a subcommunity base state object
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("Public Subcommunity" + Helper.genDateBasedRand())
														 	.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														 	.description("Copy subcommunity - New community is created")
														 	.build();
		
		log.info("INFO: Get UUID of community");
		community4.getCommunityUUID_API(apiOwner, comAPI4);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
											
			log.info("INFO: Navigate to the community using UUID");
			community4.navViaUUID(ui);

			log.info("INFO: Create subcommunity");
			subCommunity.create(ui);
			
			log.info("INFO: Verify subcommunity is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
					"ERROR : subcommunity is not created");
			
			log.info("INFO: Open the subcommunity Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);

			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");
			
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
	
			log.info("INFO: Verify new copy is created");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR : New copy is not created");
 
			log.info("INFO: Verify community description is the same as in the original subcommunity");
			Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getDescription()),
					"ERROR: Community description is not the same as in the original subcommunity");
		
			log.info("INFO: Verify tag is the same as in the original subcommunity");
			System.out.println("link="+subCommunity.getTags());
			Assert.assertTrue(ui.isElementPresent("link="+subCommunity.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original subcommunity");
			
			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(subCommunity.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{
	    	
			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}

	
	/**
	* memberCopyCommViaCommActions()
	*<ul>
	*<li><B>Info:</B> Member can copy community via Community Actions.</li>
	*<li><B>Step:</B> Create a public community using API & add one member to the community</li>
	*<li><B>Step:</B> Login as community member(non-owner)</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original community</li>
	*<li><B>Verify:</B> Tag is the same as in the original community</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F25F53E5F936C25E8525804C0065D23B">TTT - COPY COMMUNITY 1: TEST THE COPY COMMUNITY MENU PICK UNDER COMMUNITY ACTIONS MENU</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void memberCopyCommViaCommActions() {

		String testName = ui.startTest();	
	
		log.info("INFO: Get UUID of community");
		community9.getCommunityUUID_API(apiOwner, comAPI9);
		
		log.info("INFO: Load component and login as community member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community9.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);

			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community9.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			log.info("INFO: Verify community description is the same as in the original community");
			Assert.assertTrue(ui.fluentWaitTextPresent(community9.getDescription()),
					"ERROR: Community description is not the same as in the original community");
		
			log.info("INFO: Verify tag is the same as in the original community");
			System.out.println("link=" + community9.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + community9.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original community");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community9.getName()+ CommunitiesUIConstants.appendCopy, testUser1);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		
	

	/**
	* chooseBySearching_OwnerCopyComm()
	*<ul>
	*<li><B>Info:</B> Community owner can choose by searching for communities when selecting a community to copy from</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Login to Communities catalog view as community owner</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community/Copy an Existing Community</li>
	*<li><B>Verify:</B> 'Copy an Existing Community' search page appears</li>
	*<li><B>Step:</B> Entering community name in the 'Choose by Searching for Communities' text field</li>
	*<li><B>Step:</B> Select the result from the typeahead dropdown</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' prepopulated form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original community</li>
	*<li><B>Verify:</B> Tag is the same as in the original community</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/670382AD0A91F7A18525806C0055A727">TTT - COPY COMMUNITY 5: TEST FOR COPY EXISTING STRUCTURE SEARCH DLG BOX (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void chooseBySearching_OwnerCopyComm() {

		String testName = ui.startTest();	
			
		log.info("INFO: Load component and login as community owner");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
				
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
			
			log.info("INFO: Start Community ...  Copy an Existing Community");
			startCommunityCopyExisting(isCardView);
			
			log.info("INFO: Validate 'Copy an Existing Community' search page appears by verifying the URL");
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: 'Copy an Existing Community' search page does not appear");		
			
			log.info("INFO: Entering community name " + community10.getName() + "in the 'Choose by Searching for Communities' text field");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).type(community10.getName());
			
			log.info("INFO: Select the result from the typeahead dropdown");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup).click();
			
			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
		
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community10.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			log.info("INFO: Verify community description is the same as in the original community");
			Assert.assertTrue(ui.fluentWaitTextPresent(community10.getDescription()),
					"ERROR: Community description is not the same as in the original community");
		
			log.info("INFO: Verify tag is the same as in the original community");
			System.out.println("link=" + community10.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + community10.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original community");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community10.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}	

	
	/**
	* chooseBySearching_MemberCopyComm()
	*<ul>
	*<li><B>Info:</B> Community member can choose by searching for communities when selecting a community to copy from</li>
	*<li><B>Step:</B> Create an internal restricted community using API</li>
	*<li><B>Step:</B> Login to Communities catalog view as community member</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community/Copy an Existing Community</li>
	*<li><B>Verify:</B> 'Copy an Existing Community' search page appears</li>
	*<li><B>Step:</B> Entering community name in the 'Choose by Searching for Communities' text field</li>
	*<li><B>Step:</B> Select the result from the typeahead dropdown</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' prepopulated form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original community</li>
	*<li><B>Verify:</B> Tag is the same as in the original community</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/670382AD0A91F7A18525806C0055A727">TTT - COPY COMMUNITY 5: TEST FOR COPY EXISTING STRUCTURE SEARCH DLG BOX (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void chooseBySearching_MemberCopyComm() {

		String testName = ui.startTest();	
			
		log.info("INFO: Load component and login as community member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}

			log.info("INFO: Start Community ...  Copy an Existing Community");
			startCommunityCopyExisting(isCardView);

			log.info("INFO: Validate 'Copy an Existing Community' search page appears by verifying the URL");
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: 'Copy an Existing Community' search page does not appear");		
			
			log.info("INFO: Entering community name " + community11.getName() + "in the 'Choose by Searching for Communities' text field");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).type(community11.getName());
			
			log.info("INFO: Select the result from the typeahead popup");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup).click();
			
			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
		
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community11.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			log.info("INFO: Verify community description is the same as in the original community");
			Assert.assertTrue(ui.fluentWaitTextPresent(community11.getDescription()),
					"ERROR: Community description is not the same as in the original community");
		
			log.info("INFO: Verify tag is the same as in the original community");
			System.out.println("link=" + community11.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + community11.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original community");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community11.getName()+ CommunitiesUIConstants.appendCopy, testUser1);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		
	

	/**
	* nonMemberCopyPublicCommBySearch()
	*<ul>
	*<li><B>Info:</B> Nonmember can copy public community by searching</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Login to Communities catalog view as nonmember</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community/Copy an Existing Community</li>
	*<li><B>Verify:</B> 'Copy an Existing Community' search page appears</li>
	*<li><B>Step:</B> Entering community name in the 'Choose by Searching for Communities' text field</li>
	*<li><B>Step:</B> Select the result from the typeahead dropdown</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' prepopulated form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original community</li>
	*<li><B>Verify:</B> Tag is the same as in the original community</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/670382AD0A91F7A18525806C0055A727">TTT - COPY COMMUNITY 5: TEST FOR COPY EXISTING STRUCTURE SEARCH DLG BOX (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void nonMemberCopyPublicCommBySearch() {

		String testName = ui.startTest();	
			
		log.info("INFO: Load component and login as nonmember");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
			
			log.info("INFO: Start Community ...  Copy an Existing Community");
			startCommunityCopyExisting(isCardView);

			log.info("INFO: Validate 'Copy an Existing Community' search page appears by verifying the URL");
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: 'Copy an Existing Community' search page does not appear");		
			
			log.info("INFO: Entering the public community name " + community12.getName() + " in the 'Choose by Searching for Communities' text field");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).type(community12.getName());
			
			log.info("INFO: Select the result from the typeahead popup");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup).click();
			
			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
		
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community12.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			log.info("INFO: Verify community description is the same as in the original community");
			Assert.assertTrue(ui.fluentWaitTextPresent(community12.getDescription()),
					"ERROR: Community description is not the same as in the original community");
		
			log.info("INFO: Verify tag is the same as in the original community");
			System.out.println("link=" + community12.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + community12.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original community");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community12.getName()+ CommunitiesUIConstants.appendCopy, testUser1);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			
	
	
	/**
	* nonMemberCopyModeratedCommBySearch()
	*<ul>
	*<li><B>Info:</B> Nonmember can copy moderated community by searching</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Login to Communities catalog view as nonmember</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community/Copy an Existing Community</li>
	*<li><B>Verify:</B> 'Copy an Existing Community' search page appears</li>
	*<li><B>Step:</B> Entering community name in the 'Choose by Searching for Communities' text field</li>
	*<li><B>Step:</B> Select the result from the typeahead dropdown</li>
	*<li><B>Verify:</B> 'Copying an Existing Community' prepopulated form displays</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Description is the same as in the original community</li>
	*<li><B>Verify:</B> Tag is the same as in the original community</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/670382AD0A91F7A18525806C0055A727">TTT - COPY COMMUNITY 5: TEST FOR COPY EXISTING STRUCTURE SEARCH DLG BOX (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void nonMemberCopyModeratedCommBySearch() {

		String testName = ui.startTest();	
			
		log.info("INFO: Load component and login as nonmember");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
			
			log.info("INFO: Start Community ...  Copy an Existing Community");
			startCommunityCopyExisting(isCardView);

			log.info("INFO: Validate 'Copy an Existing Community' search page appears by verifying the URL");
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: 'Copy an Existing Community' search page does not appear");		
			
			log.info("INFO: Entering the moderated community name " + community13.getName() + " in the 'Choose by Searching for Communities' text field");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).type(community13.getName());
			
			log.info("INFO: Select the result from the typeahead popup");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTypeAheadPopup).click();
			
			log.info("INFO: Verify 'Copying an Existing Community' form appears");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
					"ERROR: 'Copying an Existing Community' form does not appear");

			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
		
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community13.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			log.info("INFO: Verify community description is the same as in the original community");
			Assert.assertTrue(ui.fluentWaitTextPresent(community13.getDescription()),
					"ERROR: Community description is not the same as in the original community");
		
			log.info("INFO: Verify tag is the same as in the original community");
			System.out.println("link=" + community13.getTags());
			Assert.assertTrue(ui.isElementPresent("link=" + community13.getTags().toLowerCase()),
					"ERROR: Community tag is not the same as in the original community");

			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community13.getName()+ CommunitiesUIConstants.appendCopy, testUser1);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}				
	

	/**
	* nonMemberCanNotCopyListedComm()
	*<ul>
	*<li><B>Info:</B> Nonmember can NOT copy listed restricted community</li>
	*<li><B>Step:</B> Create a listed internal restricted community using API</li>
	*<li><B>Step:</B> Login to Communities catalog view as nonmember</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Start a Community/Copy an Existing Community</li>
	*<li><B>Verify:</B> 'Copy an Existing Community' search page appears</li>
	*<li><B>Step:</B> Entering community name in the 'Choose by Searching for Communities' text field</li>
	*<li><B>Verify:</B> Typeahead popup displays 'No results found'</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/670382AD0A91F7A18525806C0055A727">TTT - COPY COMMUNITY 5: TEST FOR COPY EXISTING STRUCTURE SEARCH DLG BOX (D59)</a></li>
	*</ul>	
	*/
	@Test (groups = {"regression", "regressioncloud"})
	public void nonMemberCanNotCopyListedComm() {

		String testName = ui.startTest();	
			
		log.info("INFO: Load component and login as nonmember");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
					
				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();

			}
			
			log.info("INFO: Start Community ...  Copy an Existing Community");
			startCommunityCopyExisting(isCardView);
		
			log.info("INFO: Validate 'Copy an Existing Community' search page appears by verifying the URL");
			Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
					"ERROR: 'Copy an Existing Community' search page does not appear");		
			
			log.info("INFO: Entering the listed restricted community name " + community14.getName() + " in the 'Choose by Searching for Communities' text field");
			driver.getFirstElement(CommunitiesUIConstants.copyCommChooseBySearchingTextField).type(community14.getName());

			log.info("INFO: Verify typeahead popup displays 'No results found'");
			Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.copyCommChooseBySearchingNoResultsFound),
					"ERROR: Typeahead popup does not displays 'No results found'");

			log.info("INFO: Click on the Cancel button");
			ui.clickLink(CommunitiesUIConstants.CancelButtonCopyCommSearchDB);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		

	
	/**
	* createMultipleCopiesOfListedComm()
	*<ul>
	*<li><B>Info:</B> Appended number should increment when there are multiple copies of a listed restricted community.</li>
	*<li><B>Step:</B> Create an internal listed restricted community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>>
	*<li><B>Verify:</B> First copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Step:</B> Open the original community again</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> Second copy is created</li>
	*<li><B>Verify:</B> The word 'Copy 1' is appended at the end of community name</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createMultipleCopiesOfListedComm() {

		String testName = ui.startTest();	
	
		log.info("INFO: Get UUID of community");
		community15.getCommunityUUID_API(apiOwner, comAPI15);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community15.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify first copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.communityName + ":contains(" + community15.getName() + CommunitiesUIConstants.appendCopy + ")"),
					"ERROR: New copy is not created");	 		 
			
			//Determine of SC or OP			
			if(isOnPremise){

				if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
				   log.info("INFO: Click on 'My Communities' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.MY_COMMUNITIES.select(ui);
				}
				else {
				   log.info("INFO: Click on 'I'm an Owner' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.IM_AN_OWNER.select(ui);
				}
				
			}else{

				log.info("INFO: Click 'Communities' on Mega Menu and then select 'I'm an owner' view from left nav");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				Community_View_Menu.IM_AN_OWNER.select(ui);

			}		
						
			log.info("INFO: Navigate to the community using UUID");
			community15.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify second copy is created and the phrase 'Copy 1' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community15.getName() + CommunitiesUIConstants.appendCopy1),
					"ERROR: New copy is not created");	
			
			log.info("Clean Up: Need to manually delete the second copy since it's not created by API");
			ui.deleteCopyCommunity(community15.getName()+ CommunitiesUIConstants.appendCopy1, testUser);
			
			ui.applyCatalogFilter(community15.getName()+ CommunitiesUIConstants.appendCopy, isCardView);

			log.info("INFO: Open the first copy from the catalog view");
			if (isCardView){
				ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(community15.getName()+ CommunitiesUIConstants.appendCopy));
			}
			else {
				ui.clickLinkWait("link="+community15.getName()+ CommunitiesUIConstants.appendCopy);
			}

			log.info("Clean Up: Need to manually delete the first copy since it's not created by API");
			ui.deleteCopyCommunity(community15.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		

	
	/**
	* createMultipleCopiesOfModeratedComm()
	*<ul>
	*<li><B>Info:</B> Appended number should increment when there are multiple copies of a moderated community.</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> First copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Step:</B> Open the original community again</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> Second copy is created</li>
	*<li><B>Verify:</B> The word 'Copy 1' is appended at the end of community name</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createMultipleCopiesOfModeratedComm() {

		String testName = ui.startTest();	
	
		log.info("INFO: Get UUID of community");
		community16.getCommunityUUID_API(apiOwner, comAPI16);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community16.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify first copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community16.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			//Determine of SC or OP			
			if(isOnPremise){

				if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
				   log.info("INFO: Click on 'My Communities' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.MY_COMMUNITIES.select(ui);
				}
				else {
				   log.info("INFO: Click on 'I'm an Owner' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.IM_AN_OWNER.select(ui);
				}
				
			}else{

				log.info("INFO: Click 'Communities' on Mega Menu and then select 'I'm an owner' view from left nav");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				Community_View_Menu.IM_AN_OWNER.select(ui);

			}		
			
			log.info("INFO: Navigate to the community using UUID");
			community16.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify second copy is created and the phrase 'Copy 1' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community16.getName() + CommunitiesUIConstants.appendCopy1),
					"ERROR: New copy is not created");	
			
			log.info("Clean Up: Need to manually delete the second copy since it's not created by API");
			ui.deleteCopyCommunity(community16.getName()+ CommunitiesUIConstants.appendCopy1, testUser);
			
			ui.applyCatalogFilter(community16.getName()+ CommunitiesUIConstants.appendCopy, isCardView);

			log.info("INFO: Open the first copy from the catalog view");
			if (isCardView){
				ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(community16.getName()+ CommunitiesUIConstants.appendCopy));
			}
			else {
				ui.clickLinkWait("link="+community16.getName()+ CommunitiesUIConstants.appendCopy);
			}

			log.info("Clean Up: Need to manually delete the first copy since it's not created by API");
			ui.deleteCopyCommunity(community16.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			

	
	/**
	* createMultipleCopiesOfPublicComm()
	*<ul>
	*<li><B>Info:</B> Appended number should increment when there are multiple copies of a public community.</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> First copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Step:</B> Open the original community again</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> Second copy is created</li>
	*<li><B>Verify:</B> The word 'Copy 1' is appended at the end of community name</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void createMultipleCopiesOfPublicComm() {

		String testName = ui.startTest();	
	
		log.info("INFO: Get UUID of community");
		community17.getCommunityUUID_API(apiOwner, comAPI17);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community17.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify first copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community17.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			//Determine of SC or OP			
			if(isOnPremise){

				if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
				   log.info("INFO: Click on 'My Communities' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.MY_COMMUNITIES.select(ui);
				}
				else {
				   log.info("INFO: Click on 'I'm an Owner' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.IM_AN_OWNER.select(ui);
				}
				
			}else{

				log.info("INFO: Click 'Communities' on Mega Menu and then select 'I'm an owner' view from left nav");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				Community_View_Menu.IM_AN_OWNER.select(ui);

			}			
			
			log.info("INFO: Navigate to the community using UUID");
			community17.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify second copy is created and the phrase 'Copy 1' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community17.getName() + CommunitiesUIConstants.appendCopy1),
					"ERROR: New copy is not created");	
			
			log.info("Clean Up: Need to manually delete the second copy since it's not created by API");
			ui.deleteCopyCommunity(community17.getName()+ CommunitiesUIConstants.appendCopy1, testUser);
			
			log.info("INFO: Open the first copy from the catalog view");
			if (isCardView){
				ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(community17.getName()+ CommunitiesUIConstants.appendCopy));
			}
			else {
			    ui.clickLinkWait("link="+community17.getName()+ CommunitiesUIConstants.appendCopy);
			}

			log.info("Clean Up: Need to manually delete the first copy since it's not created by API");
			ui.deleteCopyCommunity(community17.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}			

	
	/**
	* createMultipleCopiesOfRestrictedComm()
	*<ul>
	*<li><B>Info:</B> No appended number is added when there are multiple copies of a restricted community(not listed).</li>
	*<li><B>Step:</B> Create an internal restricted community(not listed) using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> First copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Step:</B> Open the original community again</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save on the 'Copying an Existing Community' form</li>
	*<li><B>Verify:</B> Second copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void createMultipleCopiesOfRestrictedComm() {

		String testName = ui.startTest();	
	
		log.info("INFO: Get UUID of community");
		community18.getCommunityUUID_API(apiOwner, comAPI18);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View Gate keeper
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
						
			log.info("INFO: Navigate to the community using UUID");
			community18.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify first copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community18.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	

			//Determine of SC or OP			
			if(isOnPremise){

				if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
				   log.info("INFO: Click on 'My Communities' option from Community Mega Menu dropdwon");	
				   Community_MegaMenu_Menu.MY_COMMUNITIES.select(ui);
				}
				else {
				   log.info("INFO: Click on 'I'm an Owner' option from Community Mega Menu dropdwon");
				   Community_MegaMenu_Menu.IM_AN_OWNER.select(ui);
				}
				
			}else{

				log.info("INFO: Click 'Communities' on Mega Menu and then select 'I'm an owner' view from left nav");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				Community_View_Menu.IM_AN_OWNER.select(ui);

			}		
			
			log.info("INFO: Navigate to the community using UUID");
			community18.navViaUUID(ui);

			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify second copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community18.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	
			
			log.info("Clean Up: Need to manually delete the second copy since it's not created by API");
			ui.deleteCopyCommunity(community18.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			log.info("INFO: Navigate to My community and filter for first copy community");
			ui.goToMyCommunitiesView(isCardView);
			ui.applyCatalogFilter(community18.getName()+ CommunitiesUIConstants.appendCopy, isCardView);

			log.info("INFO: Open the first copy from the catalog view");
			if (isCardView){
				ui.clickLinkWait(CommunitiesUI.getCommunityCardByNameLink(community18.getName()+ CommunitiesUIConstants.appendCopy));
			}
			else {
				ui.clickLinkWait("link="+community18.getName()+ CommunitiesUIConstants.appendCopy);
			}

			log.info("Clean Up: Need to manually delete the first copy since it's not created by API");
			ui.deleteCopyCommunity(community18.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		

	
	/**
	* memberNotCopied()
	*<ul>
	*<li><B>Info:</B> Members are not copied when copying an existing community.</li>
	*<li><B>Step:</B> Create an internal restricted community with one member using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Step:</B> Log out
	*<li><B>Step:</B> Login as a member of original community
	*<li><B>Step:</B> Navigate to I'm an Member view</li>
	*<li><B>Verify:</B> Member of the original community doesn't see the copied community in Catalog view</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/70AE30E0F600195B8525677700369A2C/5FBBA9D100D1B1EF8525804C006464D0">TTT - COPY COMMUNITY 2: PRE-POPULATE SPECIFIC FIELDS ON THE NEW COMMUNITY (D58)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void memberNotCopied() {

		String testName = ui.startTest();
		
		log.info("INFO: Get UUID of community");
		community19.getCommunityUUID_API(apiOwner, comAPI19);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI19, StartPageApi.OVERVIEW);
		}
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
			
			// check if catalog_card_view GK enabled
			boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
						
			log.info("INFO: Navigate to the community using UUID");
			community19.navViaUUID(ui);
				
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
					
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);
			
			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community19.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");
			
			// get the community link
			String communityLink = community19.getName() + CommunitiesUIConstants.appendCopy;
		
			if(isCardView){
				// get the UUID of the community from the weburl, 
				  
				// Get url with params after UUID removed
				String webUrl = this.driver.getCurrentUrl().split("&")[0];
				 
				// Strip the UUID out of the weburl, left communityUuid= for legacy
				String commUuid = (webUrl.split("\\?")[1]);
				
				String UUID_PREFIX = "communityUuid=";
				
				if (commUuid != null) {
					communityLink =  "css=div#community-card-" + commUuid.replace(UUID_PREFIX, "");
				}
			}

			
			
			log.info("INFO: Community owner logout");
			ui.close(cfg);

			log.info("INFO: Load component and login as a member of original community");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1);
			
		
			log.info("INFO: Navigate to I'm an Member view");
			ui.goToMemberView(isCardView);
		
			log.info("INFO: Verify member of the original community doesn't see the copied community in Catalog view");
			Assert.assertFalse(ui.isTextPresent(community19.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR : Member should not see the copied community, but he/she does");

			log.info("INFO: member logout");
			ui.close(cfg);
			
			log.info("INFO: Load component and login as community owner");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);	
			
			log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
			if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){

				log.info("INFO: If the guided tour pop-up box appears, close it");
				ui.closeGuidedTourPopup();
			}
			
			log.info("INFO: Open the copy community");
			ui.clickLinkWait(communityLink);		

			log.info("Clean Up: Need to manually delete the copied community since it's not created by API");
			ui.deleteCopyCommunity(community19.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}				
	

	/**
	* widgetContentNotCopied()
	*<ul>
	*<li><B>Info:</B> Widget content is not copied when copying an existing community.</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Add Gallery widget to community using API</li>
	*<li><B>Step:</B> Upload a file in community using API</li>
	*<li><B>Step:</B> Open the community</li>
	*<li><B>Step:</B> Click on Set Up the Gallery link</li>
	*<li><B>Step:</B> Take default setting and click on Set as Gallery button</li>
	*<li><B>Verify:</B> Thumbnail for fileA is shown in Gallery</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Set Up the Gallery link is present which means Gallery widget structure is copied but not the content</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE0D2EE96DC46199852580570058729A">TTT - COPY COMMUNITY 3: COPY ANOTHER COMMUNITY'S LAYOUT AND WIDGET STRUCTURE (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void widgetContentNotCopied() {

		String testName = ui.startTest();
	
		log.info("INFO: Get UUID of community");
		community20.getCommunityUUID_API(apiOwner, comAPI20);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

			log.info("INFO: Add Gallery widget to community using API");
			community20.addWidgetAPI(comAPI20, apiOwner, BaseWidget.GALLERY);

			log.info("INFO: Upload a file using API");
			community20.addFileAPI(comAPI20, fileA, apiOwner, fileHandler);
			
			log.info("INFO: Navigate to the community using UUID");
			community20.navViaUUID(ui);
				
			log.info("INFO: Locate the action icon for the Gallery widget");
			Element galleryActionIcon = driver.getFirstElement(CommunitiesUIConstants.setupGalleryLink);
			
			log.info("INFO: Scroll down so the Gallery summary widget appears on the page");
			driver.clickAt((int)galleryActionIcon.getLocation().getX(), (int)galleryActionIcon.getLocation().getY());
						
			log.info("INFO: Click on Set up the Gallery link to launch Set Up a Gallery dialog box");
			ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);
		
			log.info("INFO: All Community File is selected as default, click on Set as Gallery button to close the dialog box");
			driver.getFirstElement(CommunitiesUIConstants.filePickerOkButton).click();
								
			log.info("INFO: Verify thumbnail for fileA is shown in Gallery");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
					"ERROR: Thumbnail for fileA is not shown in Gallery");
						
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
								
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community20.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");
			
			log.info("INFO: Verify Set Up the Gallery link is present which means Gallery widget structure is copied but not the content");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.setupGalleryLink),
					"ERROR: Set Up the Gallery link is not present");
			
			log.info("Clean Up: Need to manually delete the copied community since it's not created by API");
			ui.deleteCopyCommunity(community20.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}	

	
	/**
	* removedWidgetNotCopied()
	*<ul>
	*<li><B>Info:</B> Removed widget is not copied when copying an existing community.</li>
	*<li><B>Step:</B> Create a moderated community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Actions for Forums to remove Forums widget</li>
	*<li><B>Verify:</B> Forums tab does not appear on the top nav</li>
	*<li><B>Verify:</B> Forums widget is removed from Overview page</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Forums tab does not appear on the top nav</li>
	*<li><B>Verify:</B> Forums widget does not appear on Overview page</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE0D2EE96DC46199852580570058729A">TTT - COPY COMMUNITY 3: COPY ANOTHER COMMUNITY'S LAYOUT AND WIDGET STRUCTURE (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void removedWidgetNotCopied() {

		String testName = ui.startTest();
	
		BaseWidget widget = BaseWidget.FORUM;
		
		log.info("INFO: Get UUID of community");
		community21.getCommunityUUID_API(apiOwner, comAPI21);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
 
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI21, StartPageApi.OVERVIEW);
		}
		
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
			
			log.info("INFO: Navigate to the community using UUID");
			community21.navViaUUID(ui);

			log.info("INFO: Remove Forums widget");
	    	ui.performCommWidgetAction(widget, Widget_Action_Menu.DELETE);	    	
	    	ui.removeWidget(widget, testUser);

  		  	log.info("INFO: Verify Forums tab does not appear on the top nav");
  		  	Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.tabbedNavForumsTab),
  				    "ERROR : Forums tab appears on the top nav");

		  	log.info("INFO: Verify Forums widget is removed from Overivew page");
  		  	Assert.assertFalse(driver.isElementPresent(ui.getWidgetByTitle(widget)),
  		  			"ERROR: Failed to remove Forums widget");
  		  	
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
					
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community21.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");
			
 		  	log.info("INFO: Verify Forums tab does not appear on the top nav");
  		  	Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.tabbedNavForumsTab),
  				    "ERROR : Forums tab appears on the top nav");

		  	log.info("INFO: Verify Forums widget does not appear on Overivew page");
  		  	Assert.assertFalse(driver.isElementPresent(ui.getWidgetByTitle(widget)),
  		  			"ERROR: Failed to remove Forums widget");
  		  	
			log.info("Clean Up: Need to manually delete the copied community since it's not created by API");
			ui.deleteCopyCommunity(community21.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}	

	
	/**
	* hiddenWidgetIsCopied()
	*<ul>
	*<li><B>Info:</B> Hidden widget is copied when copying an existing community.</li>
	*<li><B>Step:</B> Create a restricted community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Actions for Files to hide Files widget</li>
	*<li><B>Verify:</B> Files tab still appears on the top nav</li>
	*<li><B>Verify:</B> Files widget is hidden on Overivew page</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Files tab still appears on the top nav</li>
	*<li><B>Verify:</B> Files widget is hidden on Overivew page</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/BE0D2EE96DC46199852580570058729A">TTT - COPY COMMUNITY 3: COPY ANOTHER COMMUNITY'S LAYOUT AND WIDGET STRUCTURE (D59)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"})
	public void hiddenWidgetIsCopied() {

		String testName = ui.startTest();
	
		BaseWidget widget = BaseWidget.FILES;
		
		log.info("INFO: Get UUID of community");
		community22.getCommunityUUID_API(apiOwner, comAPI22);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI22, StartPageApi.OVERVIEW);
		}
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
			
			log.info("INFO: Navigate to the community using UUID");
			community22.navViaUUID(ui);

			log.info("INFO: Hide Files widget");
	    	ui.performCommWidgetAction(widget, Widget_Action_Menu.HIDE);	    	    	
	    	ui.clickLinkWait(CommunitiesUIConstants.WidgetHideButton);
		  	
  		  	log.info("INFO: Verify Files tab still appears on the top nav");
  		  	Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavFilesTab),
  		  			"ERROR : Files tab does not appear on the top nav");
	    	
  		  	log.info("INFO: Verify Files widget is hidden on Overivew page");
  		  	Assert.assertFalse(driver.isElementPresent(ui.getWidgetByTitle(widget)),
  		  			"ERROR: Failed to hide Files widget");
	    	
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
					
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community22.getName() + CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");

	  		log.info("INFO: Verify Files tab still appears on the top nav");
	  		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavFilesTab),
	  				"ERROR : Files tab does not appear on the top nav");
			
 		  	log.info("INFO: Verify Files widget is hidden on Overivew page");
  		  	Assert.assertFalse(driver.isElementPresent(ui.getWidgetByTitle(widget)),
  		  			"ERROR: Failed to hide Files widget");
   	 		  	
			log.info("Clean Up: Need to manually delete the copied community since it's not created by API");
			ui.deleteCopyCommunity(community22.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}
	

	/**
	* richContentInRTEIsCopied()
	*<ul>
	*<li><B>Info:</B> Rich content widget and its content are copied from the original community.</li>
	*<li><B>Step:</B> Create a public community using API</li>
	*<li><B>Step:</B> Login to community</li>
	*<li><B>Step:</B> Check gatekeeper setting for Copy Community feature</li>
	*<li><B>Step:</B> If gatekeeper setting is enabled then proceeds with following steps, otherwise skip the test</li>
	*<li><B>Step:</B> Click on Rich Content widget/Add Content button</li>
	*<li><B>Step:</B> Add rich content in RTE and click on Save</li>
	*<li><B>Verify:</B> Rich content is saved</li>
	*<li><B>Step:</B> Click on Community Actions/Copy Community on Overview page</li>
	*<li><B>Step:</B> Click on Save</li>
	*<li><B>Verify:</B> New copy is created</li>
	*<li><B>Verify:</B> The word 'Copy' is appended at the end of community name</li>
	*<li><B>Verify:</B> Rich content is copied</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/70AE30E0F600195B8525677700369A2C/755E61F9490F6B9285258088006F488A">TTT - COPY COMMUNITY 8: COPY RICH CONTENT WIDGET CONTENT FROM AN EXISTING COMMUNITY TO A NEW COMMUNITY (D60)</a></li>
	*</ul>	
	*/	
	@Test (groups = {"regression", "regressioncloud"} , enabled=false )
	public void richContentInRTEIsCopied() {

		String testName = ui.startTest();
		String rteContnet = "rte unique *(star), 100% and $500";
		
		log.info("INFO: Get UUID of community");
		community23.getCommunityUUID_API(apiOwner, comAPI23);
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
 
		log.info("Execute the test if GateKeeper setting for Copy Community is enabled");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
							
			log.info("INFO: Navigate to the community using UUID");
			community23.navViaUUID(ui);
						
			log.info("INFO: Click on Rich Content widget/Add Content button");
			ui.clickLinkWait(CommunitiesUIConstants.rteAddContent);
			
			log.info("INFO: Add rich content");
			ui.typeInCkEditor(rteContnet);
			
			log.info("INFO: Scroll down the page so the 'Save' button is visible");
			driver.executeScript("scroll(0, 150);");
				
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.rteSave);

			log.info("INFO: Verify rich content is saved");
			Assert.assertTrue(ui.fluentWaitTextPresent(rteContnet),
					"ERROR: Rich content is not saved");
			
			log.info("INFO: Open the Community Actions menu & click on 'Copy Community' option");
			Com_Action_Menu.COPYCOMMUNITYSTRUCTURE.select(ui);
		
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

			log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
			Assert.assertTrue(ui.fluentWaitTextPresent(community23.getName()+ CommunitiesUIConstants.appendCopy),
					"ERROR: New copy is not created");	
			
			log.info("INFO: Verify rich content is copied");
			Assert.assertTrue(ui.fluentWaitTextPresent(rteContnet),
					"ERROR: Rich content is not copied");
					
			log.info("Clean Up: Need to manually delete the copy community since it's not created by API");
			ui.deleteCopyCommunity(community23.getName()+ CommunitiesUIConstants.appendCopy, testUser);
			
			ui.endTest();	
	
	    }else{

			log.info("INFO: Copy Community menu item is not enabled");
			throw new SkipException("Test '" + testName + "' is skipped");		

	    }//End of if and else	
		
	}		
	
	
	/**
	* Verify that 'Copy an Existing Community' search dialog box appears after clicking Start a Community/Copy an Existing Community menu option
	*/	
	private void validateCopyAnExistingStructureOption(boolean isCardView) {	
	
		startCommunityCopyExisting(isCardView);
		
		log.info("INFO: Validate 'Copy an Existing Community' search dialog box appears by verifying the URL");
		System.out.println(driver.getCurrentUrl());  
		Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.copyFromExistingURL),
				"ERROR: 'Copy an Existing Community' search dialog box does not appear");
		
		log.info("INFO: Click on the Cancel button");
		ui.clickLink(CommunitiesUIConstants.CancelButtonCopyCommSearchDB);
				
	}		

	
	/**
	* Verify that 'Start a Community' form appears after clicking Start a Community/Start from New menu option
	*/	
	private void validateStartFromNewOption(boolean isCardView) {	
	
		startCommunityFromNew(isCardView);
		
		log.info("INFO: Validate 'Start a Community' form appears by verifying the URL");
		System.out.println(driver.getCurrentUrl());  
		Assert.assertTrue(driver.getCurrentUrl().contains(CommunitiesUIConstants.communityCreateURL),
				"ERROR: Start a Community form does not appear");
						
		log.info("INFO: Click on the Cancel button");
		driver.getFirstElement(CommunitiesUIConstants.CancelButton).click();
		
	}	
	
	private void startCommunityFromNew (boolean isCardView) {
		
		if (isCardView) {
			
			log.info("INFO: Validate that the new Start a Community menu displays");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.StartACommunityDropDownCardView),
										"ERROR: The new Start a Community menu was not found");
									
			ui.clickLinkWait(CommunitiesUIConstants.StartACommunityDropDownCardView);
			this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDownCardView).click();
		}
		else {
			log.info("INFO: Click on Start a Community to expand the dropdown menu");
			ui.clickLinkWait(CommunitiesUIConstants.StartACommunityMenu);

			log.info("INFO: Click on Start from New");
			ui.clickLinkWait(CommunitiesUIConstants.StartFromNewOption);
		}
		
	}
	
	private void startCommunityCopyExisting (boolean isCardView){
		
		if (isCardView) {
			
			log.info("INFO: Validate that the new Start a Community menu displays");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.StartACommunityDropDownCardView),
							"ERROR: The new Start a Community menu was not found");
						
			ui.clickLinkWait(CommunitiesUIConstants.StartACommunityDropDownCardView);
			this.driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromCopyExistingCardView).click();
		}
		else {
			log.info("INFO: Click on Start a Community to expand the dropdown menu");
			ui.clickLinkWait(CommunitiesUIConstants.StartACommunityMenu);

			log.info("INFO: Click on Copy an Existing Community");
			ui.clickLinkWait(CommunitiesUIConstants.CopyExistingCommOption);
		}
	}

}
