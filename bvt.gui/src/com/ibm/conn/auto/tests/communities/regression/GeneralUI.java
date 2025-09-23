package com.ibm.conn.auto.tests.communities.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPage;
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
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.SurveysUI;
import com.ibm.conn.auto.webui.cloud.CommunitiesUICloud;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class GeneralUI  extends SetUpMethods2 {
	
	protected static Logger log = LoggerFactory.getLogger(GeneralUI.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private User testUser, testUser1, testUser2;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private boolean isOnPremise;
	
	/**
	 * PTC_EditCommunity
	 * PTC_LeaveCommunity
	 * PTC_VerifyCommWithHandle
	 * PTC_VerifyStartPageInCommunity
	 */
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
				
		//Load Users		
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser(); 
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		//check to see if environment is on-premises or on the cloud
				if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
					isOnPremise = true;
				} else {
					isOnPremise = false;
				}
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setup(){
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}	
		
	
	/**
	 * Test Scenario: Community Actions: Edit Community
	 *<ul>
	 *<li><B>Info:</B> Verify the ability to edit a community </li>
	 *<li><B>Step:</B> Login & create a public community as owner using API, then Edit this community</li>
	 *<li><B>Verify:</B> Verify that after canceling the edit operation the changes are not reflected</li>
	 *<li><B>Verify:</B> Verify that after saving the edit operation the changes are reflected</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/51C3918836D0DDB185257C8D0077F9B6">TTT-COMMUNITY ACTIONS: EDIT COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void editOperationOnCommunity(){
		
			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			String product = cfg.getProductName();
			log.info("INFO: Starting test case :- "+ testName);
						
				
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test community for " + testName ).build();
			
			
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
			
			// Try Edit the community & Cancel it
			//Edit the community
			log.info("INFO: Start editing the community");
			Com_Action_Menu.EDIT.select(ui);
						
			//Change the community name
			log.info("INFO: Change the community name");
			driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).clear();
			driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).typeWithDelay(community.getName()+"_updated");
			
			//Change the Description
			log.info("INFO: Change the description");
			ui.typeInCkEditor(community.getDescription()+"_updated");
			
			//Change the tag name
			log.info("INFO: change the tag name");
			driver.getSingleElement(CommunitiesUIConstants.CommunityTag).clear();
			driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(community.getTags()+"_updated");
			
			//Click on cancel, not save it
			log.info("INFO: Click on Cancel button");
			driver.getSingleElement(CommunitiesUIConstants.EditCommunityCancelButton).click();
			
			//Make sure page is loaded before doing the verification
			log.info("INFO: Check for the Stop following Communities button to make sure page is loaded");
			ui.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity);
			
			//Test after cancel the edit the community, updated community name is not applied
			log.info("INFO: Verify the old community name is displayed");
			Assert.assertTrue(driver.getTitle().contains(community.getName()),
					"ERROR:Old community name is not displayed");
			
			// Test updated description and tags are not applied
			log.info("INFO: Verify the old community description displays");
			Assert.assertTrue(ui.fluentWaitTextPresent(community.getDescription()),
					"ERROR: Old community description is not displayed");
			
			//Tags are not applied
			log.info("INFO: Verify the old community tag displays");
			Assert.assertTrue(ui.isElementPresent("link="+community.getTags().toLowerCase()),
					"ERROR: Old community tag is not displayed");

			// Try Edit the community & Save the community
			//Edit the community
			log.info("INFO: Change the community name");
			Com_Action_Menu.EDIT.select(ui);
						
			//Change the community name
			log.info("INFO: Change the community name");
			driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).clear();
			driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).typeWithDelay(community.getName()+"_updated");
			
			
			//determine if environment is SmartCloud			
			if(product.equalsIgnoreCase("cloud")){

				//This step, to change the name a 2nd time, was added to fix a failure seen on SmartCloud.  
				//Letters from the community name are getting dropped even though .typeWithDelay is used.
				//NOTE: this step does NOT need to be added to the name change above. That change is part of
				//the 'Cancel' button test. It does not matter what actually gets entered since it does not get saved.
				log.info("INFO: Change the community name a 2nd time to ensure the name was entered correctly");
				driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).clear();
				driver.getFirstElement(CommunitiesUIConstants.EditCommunityName).typeWithDelay(community.getName()+"_updated");
			}
			
			//Change the Description
			log.info("INFO: Change the description");
			ui.typeInCkEditor(community.getDescription()+"_updated");
			
			//change the tag name
			log.info("INFO: change tag name");
			driver.getSingleElement(CommunitiesUIConstants.CommunityTag).clear();
			driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(community.getTags()+"_updated");
			
			//Save the community
			log.info("INFO: Click Save button");
			ui.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);
							
			//Make sure page is loaded before doing the verification
			log.info("INFO: Check for the Stop following Communities button to make sure page is loaded");
			ui.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity);
			
			//Test after edit the community, updated community name is present
			log.info("INFO: Verify the updated community name displays");
			Assert.assertTrue(driver.getTitle().contains(community.getName()+"_updated"),
					"ERROR: Updated community name is not displayed");
			
			// Test updated description and tags are present
			log.info("INFO: Verify the updated community description displays");
			Assert.assertTrue(ui.fluentWaitTextPresent(community.getDescription()+"_updated"),
					"ERROR: Updated community description is not displayed");
			
			//Tags are present
			log.info("INFO: Verify the updated community tag displays");
			System.out.println("link="+community.getTags());
			Assert.assertTrue(ui.isElementPresent("link="+community.getTags().toLowerCase()+"_updated"),
					"ERROR: Updated community tag is not displayed");
			
			//set updated community name
			log.info("INFO: To set updated community name to Builder");
			community.setName(community.getName()+"_updated");
			
			//delete community
			log.info("INFO: Removing community");
			community.delete(ui, testUser);
			
			ui.endTest();
	}
	
	/**
	 * Test Scenario: Community Actions: leave community
	 *<ul>
	 *<li><B>Info:</B> Verify that a community member is able to leave the community</li>
	 *<li><B>Step:</B> Create a public community as owner & add one member to it using community API</li>
	 *<li><B>Verify:</B> Verify that the Community is getting listed in I'm Member</li>
	 *<li><B>Step:</B> Open community & click on leave community</li>
	 *<li><B>Verify:</B> Verify the message shown to member is correct & also the community should not get listed under I'm Member</li>
	 *<li><B>Step:</B> Logout as member & Login-as last owner</li>
	 *<li><B>Step:</B> Open community & click on leave community</li>
	 *<li><B>Verify:</B> Verify that last owner is not allowed to leave the public community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3BEB8CD25E11B6FF85257C900054A761">TTT-COMMUNITY ACTIONS: LEAVE COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void memberIsAllowedToLeavePublicCommunity() {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test leaving a community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community commAPI = community.createAPI(apiOwner);
			
			//Get the community UUID
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, commAPI);

            log.info("INFO: Log into Communities as a Member");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
			
			// check if catalog_card_view GK enabled
			boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
			
			// get the community link
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
			if (!isCardView) {
				log.info("INFO: Clicking on the communities link"  + communityLink);
				ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
			}
			
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);
			
			log.info("INFO: Verify the user is able to see the community under I'm a Member view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR : Member is not able to see his community under Member!!");
			
			log.info("INFO: Open community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLinkWait(communityLink);
			Com_Action_Menu.LEAVE.select(ui);
			
			log.info("INFO: Click Ok button on the Warning pop-up which comes before Deletion of Community");
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			log.info("INFO: Test message shown to Member is correct when he is leaving the community");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().NoLongerMemberForCommunityMsg),
								"ERROR: In-correct message is displayed when leaving the community");
			
			log.info("INFO: Verify the user remains on the overview page");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavOverviewButton),
								"ERROR: User is no more found in overview page");
						
			log.info("INFO: Navigate to the I'm an Owner catalog view");
			ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();
			
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);
			
			log.info("INFO: Verify community should not be listed under I'm Member view.");
			Assert.assertFalse(driver.isElementPresent(communityLink),
					"ERROR : Member is able to see his community under Member after leaving community!!");

			log.info("INFO: Log out as the community member");
			ui.logout();
			
			log.info("INFO: Log into Communities as the owner");
			ui.loadComponent(Data.getData().ComponentCommunities, true);
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
			
			if (!isCardView) {
				log.info("INFO: Clicking on the communities link");
				ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
			}
			
			log.info("INFO: Open community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLinkWait(communityLink);
						
			log.info("INFO: Click on link to leave the community");
			Com_Action_Menu.LEAVE.select(ui);
			
			log.info("INFO: Click Ok button on the Warning pop-up which comes before Deletion of Community");
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			log.info("INFO: Verify the message that the last owner cannot leave the public community displays");
			Assert.assertTrue((driver.getSingleElement(CommunitiesUIConstants.LastOwnerCommunityLeavingMessage).getText().toLowerCase()).contains(ui.lastOwnerLeaveCommunityMsg().toLowerCase()),
								"ERROR: Owner are not able to see the Community leaving message");
			
			log.info("INFO: Going back to community");
			ui.clickLinkWait(CommunitiesUIConstants.LastOwnerComLeavingThenReturnButton);
			
			if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
			// added this code to force the code back to 'My Communities', it is currently going back to I'm a Member
				//My Communities view
			   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
			   ui.goToMyCommunitiesView(isCardView);;
			}
			
			log.info("INFO: Verify the Owner sees the community in the view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR : Last Owner is not able to see his community under Member after returning back to community!!");
		
			log.info("INFO: Open community");
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
		}
	
	/**
	 * Test Scenario: Community Actions: leave community
	 *<ul>
	 *<li><B>Info:</B> Verify that a community member is able to leave the community</li>
	 *<li><B>Step:</B> Create a moderated community as owner & add one member to it using community API</li>
	 *<li><B>Verify:</B> Verify that the Community is getting listed in I'm Member</li>
	 *<li><B>Step:</B> Open community & click on leave community</li>
	 *<li><B>Verify:</B> Verify the message shown to member is correct & also the community should not get listed under I'm Member</li>
	 *<li><B>Step:</B> Logout as member & Login-as last owner</li>
	 *<li><B>Step:</B> Open community & click on leave community</li>
	 *<li><B>Verify:</B> Verify that last owner is not allowed to leave the moderated community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3BEB8CD25E11B6FF85257C900054A761">TTT-COMMUNITY ACTIONS: LEAVE COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void memberIsAllowedToLeaveModeratedCommunity(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
				
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.MODERATED)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test leaving a community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community commAPI = community.createAPI(apiOwner);
			
			//Get the community UUID
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, commAPI);
		    
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
			
			// check if catalog_card_view GK enabled
			boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
						
			// get the community link
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
					
			if (!isCardView) {		
				log.info("INFO: Clicking on the communities link");
				ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
			}
			
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);
			
			log.info("INFO: Verify the user is able to see the community under I'm a Member view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR : Member is not able to see his community under Member!!");
			
			log.info("INFO: Open community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Leave community");
			Com_Action_Menu.LEAVE.select(ui);
			
			log.info("INFO: Click Ok button on the Warning pop-up which comes before Deletion of Community");
			ui.fluentWaitElementVisible(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			log.info("INFO: Test message shown to Member is correct when he is leaving the community");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().NoLongerMemberForCommunityMsg),
								"ERROR: In-correct message is displayed when leaving the community");
			
			log.info("INFO: Verify the user remains on the overview page");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavOverviewButton),
								"ERROR: User is no more found in overview page");
						
			log.info("INFO: Navigate to the I'm an Owner catalog view");			
			ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();
			
			log.info("INFO: Verify community should not be listed under I'm Member view.");
			Assert.assertFalse(driver.isElementPresent(communityLink),
					"ERROR : Member is able to see his community under Member after leaving community!!");

			log.info("INFO: Log out as the member");
			ui.logout();
			
			log.info("INFO: Log in as the community owner");
			ui.loadComponent(Data.getData().ComponentCommunities, true);
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
			
			if (!isCardView) {	
				log.info("INFO: Clicking on the communities link");
				ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
			}
			
			log.info("INFO: Open community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Click on link to leave the community");
			Com_Action_Menu.LEAVE.select(ui);
			
			log.info("INFO: Click Ok button on the Warning pop-up which comes before Deletion of Community");
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			log.info("INFO: Verify the message that the last owner cannot leave the moderated community displays");
			Assert.assertTrue((driver.getSingleElement(CommunitiesUIConstants.LastOwnerCommunityLeavingMessage).getText().toLowerCase()).contains(ui.lastOwnerLeaveCommunityMsg().toLowerCase()),
								"ERROR: Owner are not able to see the Community leaving message");
			
			log.info("INFO: Going back to community");
			ui.clickLinkWait(CommunitiesUIConstants.LastOwnerComLeavingThenReturnButton);
			
			if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
			// added this code to force the code back to 'My Communities', it is currently going back to I'm a Member	
				//My Communities view
			   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
			   ui.goToMyCommunitiesView(isCardView);
			}
			
			log.info("INFO: Verify the Owner sees the community in the view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR : Last Owner is not able to see his community under Member after returning back to community!!");
					
			log.info("INFO: Open community");
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
		
		}
	
	/**
	 * Test Scenario: Community Actions: leave community
	 *<ul>
	 *<li><B>Info:</B> Verify that a community member is able to leave the community</li>
	 *<li><B>Step:</B> Create a restricted community as owner & add one member to it using community API</li>
	 *<li><B>Verify:</B> Verify that the Community is getting listed in I'm Member</li>
	 *<li><B>Step:</B> Open community & click on leave community</li>
	 *<li><B>Verify:</B> Verify the message shown to member is correct & also the community should not get listed under I'm Member</li>
	 *<li><B>Step:</B> Logout as member & Login-as last owner</li>
	 *<li><B>Step:</B> Open community & click on leave community</li>
	 *<li><B>Verify:</B> Verify that last owner is not allowed to leave the restricted community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3BEB8CD25E11B6FF85257C900054A761">TTT-COMMUNITY ACTIONS: LEAVE COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void memberIsAllowedToLeaveRestrictedCommunity(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.RESTRICTED)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test leaving a community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.shareOutside(false)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			//create community
			//log.info("INFO: Create community using API");
			Community commAPI = community.createAPI(apiOwner);
			
			//Get the community UUID
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, commAPI);
		
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities);
			
			//Login as Member
			ui.login(testUser1);
			
			// check if catalog_card_view GK enabled
			boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
									
			// get the community link
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
								
			if (!isCardView) {		
				//Click on the Communities link
				log.info("INFO: Clicking on the communities link");
				ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
			}
			
			//Click on I'm Member in catalog
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);
			
			//Test Member is able to see the community under I'm Member
			log.info("INFO: Verify the user is able to see the community under I'm a Member view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR : Member is not able to see his community under Member!!");
			
			//Open Community to leave it
			log.info("INFO: Open community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLinkWait(communityLink);
			
			//Leave community
			log.info("INFO: Select Leave community");
			Com_Action_Menu.LEAVE.select(ui);
			
			//Click OK button on the warning pop-up.
			log.info("INFO: Click Ok button on the Warning pop-up which comes before Deletion of Community");
			ui.fluentWaitPresent(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			//Test the message shown to Member is correct after leaving the community
			log.info("INFO: Test message shown to Member is correct when he is leaving the community");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().LeftTheCommunitySuccessMsg),
					            "ERROR: In-correct message is displayed when leaving the community");
            
			//If the catalog UI GK flag is enabled, verify the user is in the My Communities view; otherwise, verify 
			//they are brought to the I'm a Member view
			if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
				log.info("INFO: Verify that the user is brought to the My Communities view");
				Assert.assertTrue(ui.isElementPresent("linkpartial=" + Community_View_Menu.MY_COMMUNITIES.getMenuItemText()),
						"ERROR: User is not brought to the My Communities view");

				log.info("INFO: Verify the community is not listed under the My Communities view.");
				Assert.assertFalse(driver.isElementPresent(communityLink),
						"ERROR: The member is able to see the community under My Communities view after leaving the community");

			}else{				
				log.info("Verify that the user is brought to the I'm Member view");
				Assert.assertTrue(ui.isElementPresent("linkpartial=" + Community_View_Menu.IM_A_MEMBER.getMenuItemText()),
						"ERROR: User is not brought to the I'm Member view");
				
				log.info("INFO: Verify community should not be listed under I'm Member view.");
				Assert.assertFalse(driver.isElementPresent(communityLink),
						"ERROR : Member is able to see his community under Member after leaving community!!");
			}
					
			// logout member & login as Owner to delete the community
			ui.logout();
			
			//Load component and login
			ui.loadComponent(Data.getData().ComponentCommunities, true);
					
			//Login as Owner & delete the community created in this test
			ui.login(testUser);
			
			if (!isCardView) {	
				//Click on the Communities link
				log.info("INFO: Clicking on the communities link");
				ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
			}
			else {
				ui.goToMyCommunitiesView(isCardView);
			}
					
			//Open Community to leave it
			log.info("INFO: Open community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			ui.clickLinkWait(communityLink);
			
			//Leave community
			log.info("INFO: Click on link to leave the community");
			Com_Action_Menu.LEAVE.select(ui);
			
			//Click OK button on the warning pop-up.
			log.info("INFO: Click Ok button on the Warning pop-up which comes before Deletion of Community");
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			// Test the Message shown to last owner is correct.
			log.info("INFO: Verify the message that the last owner cannot leave the restricted community displays");
			Assert.assertTrue((driver.getSingleElement(CommunitiesUIConstants.LastOwnerCommunityLeavingMessage).getText().toLowerCase()).contains(ui.lastOwnerLeaveCommunityMsg().toLowerCase()),
								"ERROR: Owner are not able to see the Community leaving message");
			
			// Click on Back to my community view
			log.info("INFO: Going back to community");
			ui.clickLinkWait(CommunitiesUIConstants.LastOwnerComLeavingThenReturnButton);
			
			if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
			// added this code to force the code back to 'My Communities', it is currently going back to I'm a Member	
				//My Communities view
			   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
			   ui.goToMyCommunitiesView(isCardView);
			   
			 //If environment is Cloud & the catalog UI GK flag is enabled then click on the sort by 'Date' tab
				//Content on cloud does not get cleared, community may not appear when sorted by Recently Visited due to all the communities
				//Community is easily found by clicking on Date
				if (!isOnPremise){
					if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
						log.info("INFO: Click the view sort by option 'Date'");
						ui.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
					}
				}		
			   
			   log.info("INFO: Verify the Owner sees the community in the My Communities view");
			   Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					   "ERROR: Last Owner is not able to see his community in the My Communities view");
			}
			
			//Test after returning back Owner is able to see his community.
			log.info("INFO: Verify the Owner sees the community in the view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR : Last Owner is not able to see his community under Member after returning back to community!!");
			
			//Click to open the community
			log.info("INFO: Open community");
			ui.clickLinkWait(communityLink);
			
			// Deleting the community created in this test case
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
		
		}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> Verify the correct message displays when an additional owner leaves an external restricted community</li>
	 * <li><B>Info:</B> Test is currently run on the cloud only - cannot create external restricted communities on-prem unless Visitor Model is enabled
	 * <li><B>Step:</B> Create an external restricted community with an additional owner using the API </li>
	 * <li><B>Step:</B> Log into Communities as the additional Owner</li>
	 * <li><B>Step:</B> Open the community and click on the Community Actions link</li>
	 * <li><B>Step:</B> Click on the Leave Community link</li>
	 * <li><B>Step:</B> Click OK on the leave community confirmation dialog</li>
	 * <li><B>Verify:</B> 1) the user is brought to the I'm a Member view, 2) the community is NOT listed in the view</li>
	 * <li><B>Cleanup:</B> Delete the community using the API </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	
	@Test(groups = {"regressioncloud"} , enabled=false )
	public void memberIsAllowedToLeaveExternalRestrictedComm(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.RESTRICTED)
														.description("An additional Owner leaves an external restricted community test. ")
														.addMember(new Member(CommunityRole.OWNERS, testUser1))
														.shareOutside(true)
														.build();
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser1);
			
			// check if catalog_card_view GK enabled
			boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
												
			// get the community link
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
												
			log.info("INFO: Open the external restricted community");
			community.navViaUUID(ui);
						
			log.info("INFO: Select the Leave Community option on the Community Actions menu");
			Com_Action_Menu.LEAVE.select(ui);
			
			log.info("INFO: Click the OK button on the leave community pop-up");
			ui.fluentWaitPresent(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
			
			log.info("INFO: Verify the correct message displays after clicking OK to leave the community");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().LeftTheCommunitySuccessMsg),
								"ERROR: In-correct message is displayed when leaving the community");
			
			log.info("Verify the user is brought to the I'm a Member catalog view");
			Assert.assertTrue(ui.isElementPresent("linkpartial=" + Community_View_Menu.IM_A_MEMBER.getMenuItemText()),
								"ERROR: User is not brought to the I'm a Member catalog view");
			
			log.info("INFO: Verify the community is not listed in the I'm a Member catalog view.");
			Assert.assertFalse(driver.isElementPresent(communityLink),
					"ERROR : The user is able to see the community in the I'm a Member catalog view");
						
			log.info("INFO:Cleanup - Removing community for Test case " + testName );
			apiOwner.deleteCommunity(comAPI);
			
			ui.endTest();
		
		}

	/**
	 *<ul>
	 *<li>PTC_CommunityHandle</li>
	 *<li><B>Test Scenario:</B> Verify a community can be created & accessed with a handle</li>
	 *<li><B>Info:</B> Create a community with a handle and access it using the handle</li>
	 *<li><B>Step:</B> Create a Public community & give it a Handle</li>
	 *<li><B>Step:</B> Access the community using the URL with handle</li>
	 *<li><B>Verify:</B> The community can be accessed via Handle URL 
	 *<li><B>Verify:</B> The user is on the Overview page</li>
	 *<li><B>Step:</B> Access the community using the URL with handle, but change the casing of the handle</li>
	 *<li><B>Verify:</B> Community can't be accessed when the handle is not the same casing as when it was created...case sensitive</li>
	 *<li><B>Step:</B> Create a Sub community & give it a Handle</li>
	 *<li><B>Verify:</B> Access the Sub community using the URL with handle</li>
	 *<li><B>Step:</B> Cleanup - delete community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FE8897CD331243CA85257C900058ED23">TTT-HANDLES: VERIFY COMMUNITIES CAN BE CREATED & ACCESSED WITH A HANDLE (On-Prem only!)</a></li>
	 *</ul>
	 *Note: This test does not support on cloud
	 */	
	@Test(groups = {"regression"} , enabled=false )
	public void communityAccessWithHandler(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		log.info("INFO: Starting test case :- "+ testName);
			
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag )
									.description("Test community with" + testName )
									.commHandle(Data.getData().commonHandle + rndNum)
									.build();
				
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + rndNum)
										   .tags(Data.getData().commonTag)
										   .description("Test SubCommunity Access with Handler")
										   .commHandle("Sub" + Data.getData().commonHandle + rndNum)
										   .build();
				
		ui.loadComponent(Data.getData().ComponentCommunities);
		log.info("INFO: Login as user :" + testUser1);
		ui.login(testUser);
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		log.info("If GateKeeper setting for Copy Existing Community is enabled, click Start a Community menu");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
			
			log.info("INFO: If the guided tour pop-up box appears, close it");
			ui.closeGuidedTourPopup();

			log.info("INFO: Create a community with a handle");
			if (isCardView) {
				community.createFromDropDownCardView(ui);
			}
			else {
				community.createFromDropDown(ui);
			}

		}else{
			
			log.info("INFO: Create a community with a handle");
			community.create(ui);
		}
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
		log.info("INFO: Verify community with Handle is created");
		Assert.assertTrue(driver.getTitle().contains(community.getName()),
							"ERROR: Community can't be accessed via Handle");
		
		log.info("INFO: Log out of Communities");
		ui.logout();
		ui.close(cfg);
				
		log.info("INFO: Opening the community via handle");
		ui.loadComponent("communities/community/"+Data.getData().commonHandle+rndNum);
		
		log.info("INFO: Verify community can be accessed via Handle");
		Assert.assertTrue(driver.getTitle().contains(community.getName()),
				            "ERROR: Community can't be accessed via Handle");
		
		log.info("INFO: load the component with Handle with lower case & try to access the community");
		ui.loadComponent("communities/community/"+Data.getData().commonHandle.toLowerCase()+rndNum, true);
		
		log.info("INFO: Verify community is not displayed with incorrect handle");
		Assert.assertTrue(driver.getCurrentUrl().contains("error.title.community.doesnotexist"),
				          "ERROR: The community with incorrect handle is present ");
		
		log.info("INFO: Log in as the community owner");
		ui.loadComponent(Data.getData().ComponentCommunities, true);        
		ui.login(testUser);
	
		log.info("Click on Community link");
		ui.clickLinkWait(communityLink);
		
		log.info("INFO: Using action menu to create the SubCommunity");
		Com_Action_Menu.CREATESUB.select(ui);
		
		log.info("INFO: Open Advanced options");
		if(!driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).isVisible()){
			log.info("INFO: Open advanced options");
			ui.openAdvancedOptions();
		}
		
		log.info("Verify parent community Handle is displayed");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.WebAddressHandle);
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.WebAddressHandle).getText().toLowerCase().contains("communities/community/"+Data.getData().commonHandle.toLowerCase()+rndNum),
							"ERROR: Parent community Handle displayed is not correct");
		
		log.info("Cancel SubCommunity");
		ui.clickLinkWait(CommunitiesUIConstants.SubCommunityCancelButton);
		
		log.info("INFO: Creating SubCommunity ");
		subCommunity.create(ui);
		
		log.info("INFO: Verify link to parent community appears on the subcommunity overview page");
		Assert.assertTrue(driver.getTitle().contains(community.getName()),
				"ERROR: Community name does not appear on the subcommunity Overview page");		
		
		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(driver.isTextPresent(subCommunity.getName()),
							"ERROR: SubCommunity is not found in Overview page");
		
		log.info("INFO: Logout of Communities");
		ui.logout();
		
		log.info("INFO: Opening the SubCommunity via handle");
		ui.loadComponent("communities/community/"+Data.getData().commonHandle+rndNum+"/Sub"+Data.getData().commonHandle+rndNum, true);
		
		log.info("INFO: Verify Subcommunity can be accessed via Handle");
		Assert.assertTrue(driver.getTitle().contains(subCommunity.getName()),
				            "ERROR: SubCommunity can't be accessed via Handle");
		
		log.info("INFO: Log into Communities as the community owner");
		ui.loadComponent(Data.getData().ComponentCommunities, true);		
		ui.login(testUser);
	
		log.info("INFO: Open community overview page");
		ui.clickLinkWait("link="+community.getName());
			
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
		
		ui.endTest();
	}


/**
 * Test Scenario: Start Page: Verify Start Page works correctly in communities (1 of 4)
 *<ul>
 *<li><B>Info:</B> Verify the Start Page option does not exist on the community create form</li>
 *<li><B>Step:</B> Click on Start a Community</li>
 *<li><B>Verify:</B> Start page option is not shown on the Create a community page</li>
 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF77D3AFA73C099885257C8D006C5C51">TTT-START PAGE: Verify Start Page Works Correctly in Communities</a></li>
 *</ul>
 */
	@Test(groups = {"regression", "regressioncloud", "cnx8ui-regression"})
	public void startPageOptionNotPresentWhileCreatingCommunity() {
	
		
		log.info("INFO: Load Communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);			
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
	
		
		log.info("If GateKeeper setting for Copy Existing Community is enabled, click Start a Community menu");
		if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){
			
			log.info("INFO: If the guided tour pop-up box appears, close it");
			ui.closeGuidedTourPopup();

			if (isCardView){
				log.info("INFO: Click on Start a Community to expand the dropdown menu");
				ui.clickLinkWait(CommunitiesUIConstants.StartACommunityDropDownCardView);
				
				log.info("INFO: Click on Start from New");
				ui.clickLinkWait(CommunitiesUIConstants.StartACommunityFromDropDownCardView);
			}
			else {
				log.info("INFO: Click on Start a Community to expand the dropdown menu");
				ui.clickLinkWait(CommunitiesUIConstants.StartACommunityMenu);

				log.info("INFO: Click on Start from New");
				ui.clickLinkWait(CommunitiesUIConstants.StartFromNewOption);
			}

		}else{
			log.info("INFO: Click on the Start a Community button");
			ui.getFirstVisibleElement(CommunitiesUIConstants.StartACommunity).click();
		}

		log.info("INFO: Verify Start Page option is not on the create community form");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.editCommunityStartPageDropDown),
				"ERROR: Start page option is coming on the Community creation page");

		log.info("INFO: Verify the Start Page label is not on the create community form");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.StartPageInEditCommunity),
				"ERROR : Start page label is coming on the Community creation page");

		log.info("INFO: Click on the Cancel button");
		ui.clickLinkWait(CommunitiesUIConstants.CancelButton);

		ui.endTest();

}	

/**
 * Test Scenario: Start Page: Verify Start Page works correctly in communities (2 of 4)
 *<ul>
 *<li><B>Info:</B> Verify the Start Page option is seen on the edit community form</li>
 *<li><B>Step:</B> Create a community using an API , then go to edit community page</li>
 *<li><B>Verify:</B> Start page option should be coming on the edit a community page & Verify the Start Page option is on the edit community form</li>
 *<li><B>Verify:</B> Verify Overview is the default selection for Start page drop down</li>
 *<li><B>Verify:</B> By default their should be six options present in alphabetical order</li>
 *<li><B>Step:</B> Create a SubCommunity, then go to edit SubCommunity page</li>
 *<li><B>Verify:</B> Start page option should be coming on the edit a SubCommunity page & Verify the Start Page option is on the edit SubCommunity form</li>
 *<li><B>Verify:</B> Verify Overview is the default selection for Start page drop down</li>
 *<li><B>Verify:</B> By default their should be six options present in alphabetical order</li>
 *<li><B>Step:</B> Delete community</li>
 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF77D3AFA73C099885257C8D006C5C51">TTT-START PAGE: Verify Start Page Works Correctly in Communities</a></li>
 *</ul>
 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void startPageOptionsAppearOnEditCommunityPage(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		List<String> navMenuList;
		
		log.info("INFO: Starting test case :- "+ testName);
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + rndNum )
									.description("Test community for " + testName ).build();
			
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + rndNum)
										   .tags(Data.getData().commonTag)
										   .description("Test SubCommunity Access with Handler")
										   .build();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(), testUser.getPassword());
		
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
			log.info("INFO: Collect top Nav. items to a list");
			navMenuList=ui.getTopNavItems(true);			

		}else {
			log.info("INFO: Collect left Nav. Menu items to a list");
			navMenuList=ui.getLeftNavMenu();

		}	

		log.info("INFO: Remove Members & Metrics from default menu list - they do not appear on start page menu list");
		navMenuList.remove("members");
		navMenuList.remove("metrics");

		log.info("INFO: Edit the community");
		Com_Action_Menu.EDIT.select(ui);
		
		log.info("INFO: Verify the Start Page option is on the edit community form");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.editCommunityStartPageDropDown),
				"ERROR: Start page label is not shown on Comunity Edit page");
		
		log.info("INFO: Verify the Start Page label is on the edit community form");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StartPageInEditCommunity),
				"ERROR : Start page label is not coming on the Editing the Community");
		
		log.info("INFO: Verify Overview is the default selection for Start page drop down");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.OverviewSelected),
							"ERROR: Overview is not the default option selected in Start Page drop down");
		
		log.info("INFO: Verify Start page options on the Community Edit page");
		verifyStartPageOptions(navMenuList);
		
		log.info("INFO: Click on Cancel button");
		ui.clickLinkWait(CommunitiesUIConstants.EditCommunityCancelButton);
		
		log.info("INFO: Create the subcommunity");
		subCommunity.create(ui);
		
		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
				"ERROR : subcommunity is not created");
		
		log.info("INFO: Verify a link to the parent community appears on the top nav of the subcommunity");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavParentCommunityName),
				"ERROR: No link to the parent community appears on the subcommunity overview page");
		
		log.info("INFO: Edit the SubCommunity");
		Com_Action_Menu.EDITSUB.select(ui);
		
		log.info("INFO: Verify the Start Page option is on the edit SubCommunity form");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.editCommunityStartPageDropDown),
				"ERROR: Start page label is not shown on SubComunity Edit page");
		
		log.info("INFO: Verify the Start Page label is on the edit SubCommunity form");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StartPageInEditCommunity),
				"ERROR : Start page label is not coming on the Editing the SubCommunity");
		
		log.info("INFO: Verify Overview is the default selection for Start page drop down");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.OverviewSelected),
							"ERROR: Overview is not the default option selected in Start Page drop down");
		
		log.info("INFO: Verify Start page options on the SubCommunity Edit page");
		verifyStartPageOptions(navMenuList);
		
		log.info("INFO: Click on Cancel button");
		ui.clickLinkWait(CommunitiesUIConstants.EditSubCommunityCancelBtn);
		
		log.info("INFO: Click on community name link");
		ui.fluentWaitElementVisible("link="+community.getName());
		ui.clickLinkWait("link="+community.getName());
		
		log.info("INFO:Cleanup - Removing community");
		community.delete(ui, testUser);
		
		ui.endTest();
		
	}

/**
 * Test Scenario: Start Page: Verify Start Page works correctly in communities (3 of 4)
 *<ul>
 *<li><B>Info:</B> Test to verify when the Start Page is changed the correct page displays</li>
 *<li><B>Step:</B> Create a community, edit the community so that the start page is Status Updates, & save the community</li>
 *<li><B>Verify:</B> When a user logs opens this community he is shown Status Update page by default rather than Overview page</li>
 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF77D3AFA73C099885257C8D006C5C51">TTT-START PAGE: Verify Start Page Works Correctly in Communities</a></li>
 *</ul>
 */
@Test(groups = {"regression", "regressioncloud"} , enabled=false )
public void defaultPageAsStatusUpdate(){
	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();

	BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)										
	                                           .tags(testName)
	                                           .description("Description for " + testName)
	                                           .startPage(StartPage.STATUSUPDATES)
	                                           .build();

	log.info("INFO: Enter URL for Communities");
	ui.loadComponent(Data.getData().ComponentCommunities);

	log.info("INFO: Login with user " + testUser.getDisplayName());
	ui.login(testUser);

	// check if catalog_card_view GK enabled
	boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
	
	log.info("If GateKeeper setting for Copy Existing Community is enabled, click Start a Community menu");
	if(ui.checkGKSetting(Data.getData().gk_copycomm_flag)){

		log.info("INFO: If the guided tour pop-up box appears, close it");
		ui.closeGuidedTourPopup();

		log.info("INFO: Create a community with a handle");
		if (isCardView) {
			community.createFromDropDownCardView(ui);
		}
		else {
			community.createFromDropDown(ui);
		}

	}else{		
		log.info("INFO: Create a community with a handle");
		community.create(ui);
	}
	log.info("INFO: Edit community");
	Com_Action_Menu.EDIT.select(ui);

	log.info("INFO: Change Start Page for the community to Status Updates");
	ui.fluentWaitPresent(CommunitiesUIConstants.editCommunityStartPageDropDown);
	driver.getSingleElement(CommunitiesUIConstants.editCommunityStartPageDropDown).useAsDropdown().selectOptionByVisibleText(community.getStartPage().getMenuItemText());

	log.info("INFO: Save the changes to the community");
	ui.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);
	
	//NOTE: this step was added to slow the automation down. 
	//Without this step the automation would fail on the 'Validate the default page is status updates' step.
	log.info("INFO: Verify the Status Updates header appears on the page");
	Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.statusUpdatesHeader),
			"ERROR: Status Updates header does not appear");

	log.info("INFO: Validate that the default page is status updates.");
	Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.communitiesStatusPostPage),
			"ERROR: Unable to locate Status Page");
	
	log.info("INFO: Log out of Communities and log in again");
	ui.logout();
	ui.loadComponent(Data.getData().ComponentCommunities, true);
	ui.login(testUser);

	log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
	Community_View_Menu.IM_AN_OWNER.select(ui);
	
	log.info("INFO: If the guided tour pop-up box appears, close it");
	ui.closeGuidedTourPopup();

	log.info("INFO: Open community " + community.getName());
	ui.clickLinkWait("link=" + community.getName());

	log.info("INFO: Validate that the default page is Status Updates.");
	Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.communitiesStatusPostPage),
			"ERROR: Unable to locate Status Page");

	log.info("INFO: Delete the community named " + community.getName());
	community.delete(ui, testUser);

	log.info("INFO: Log out of Communities");
	ui.logout();

	ui.endTest();
}

/**
 * Test Scenario: Start Page: Verify Start Page works correctly in communities (4 of 4)
 *<ul>
 *<li><B>Info:</B> Verify that after adding a widget the added widget appears on the Start Page drop-down menu</li>
 *<li><B>Step:</B> Create a community using an API , then go to edit community page & add Blog & Events as Widget to this community</li>
 *<li><B>Verify:</B> Start page option should now contain Blog & Widget as options</li>
 *<li><B>Verify:</B> Verify by default all 6 options Plus Blogs & Events added are present</li>
 *<li><B>Verify:</B> Verify all the widgets are displayed and in alphabetical order</li>
 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AF77D3AFA73C099885257C8D006C5C51">TTT-START PAGE: Verify Start Page Works Correctly in Communities</a></li>
 *</ul>
 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
public void startPageOptionsChangesAfterAddingApps(){
	
	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();
	List<String> navMenuList;
	
	log.info("INFO: Starting test case :- "+ testName);
	
		
	BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
								.access(Access.PUBLIC)
								.tags(Data.getData().commonTag + rndNum )
								.description("Test community for " + testName ).build();
	
	
	String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	
	log.info("INFO: Create community using API");
	Community comAPI = community.createAPI(apiOwner);
	
	log.info("INFO: Get UUID of community");
	community.getCommunityUUID_API(apiOwner, comAPI);

	log.info("INFO: Add blog & Events widget with api");
	community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
	community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
	
	log.info("INFO: Lot into Communities");
	ui.loadComponent(Data.getData().ComponentCommunities);
	ui.login(testUser);
	
	log.info("INFO: Navigate to the community using UUID");
	community.navViaUUID(ui);

	if(ui.checkGKSetting(Data.getData().commTabbedNav)){
		log.info("INFO: Collect top Nav. items to a list");
		navMenuList=ui.getTopNavItems(true);			

	}else {
		log.info("INFO: Collect left Nav. Menu items to a list");
		navMenuList=ui.getLeftNavMenu();

	}	

	log.info("INFO: Remove Members & Metrics from default menu list - they should not appear as Start Page option");
	navMenuList.remove("members");
	navMenuList.remove("metrics");

	log.info("INFO: Select the option to Edit the community");
	Com_Action_Menu.EDIT.select(ui);

	log.info("INFO: Verify adding Blog & Events its getting listed in the Start page Options");
	verifyStartPageOptions(navMenuList);
	
	log.info("Click on Cancel button");
	ui.clickLinkWait(CommunitiesUIConstants.EditCommunityCancelButton);
	
	log.info("INFO: Navigate to the community using UUID");
	ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverviewButton);
	community.navViaUUID(ui);
	
	log.info("INFO: Removing community");
	community.delete(ui, testUser);
	
	ui.endTest();
	
}

	/**
	 *<li><B>Test Scenario:</B> Change Layout - 2 Columns </li> 
	 *<li><B>Info:</B> Test the ability to change the layout to the 2 Columns layout </li>
	 *<li><B>Step:</B> Create a Public community using the API </li>
	 *<li><B>Step:</B> Check to see if Change Layout is enabled in Gatekeeper - if not skip test</li>
	 *<li><B>Step:</B> Click on the Community Actions link </li>
	 *<li><B>Step:</B> Click on the Change Layout link </li>
	 *<li><B>Step:</B> Select the 2 Columns layout from the palette </li>
	 *<li><B>Verify:</B> Verify the user is on the Overview page </li>
	 *<li><B>Verify:</B> Verify there is a left narrow column </li>
	 *<li><B>Verify:</B> Verify there is a right wide column </li>
	 *<li><B>Verify:</B> Verify the widgets appear in the correct columns </li>
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/C57F5C2141552B2B85257DDA00484E22">TTT- CHANGE COMMUNITY LAYOUT</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud", "cnx8ui-regression"})
	public void changeLayout_2Columns(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();	
			int columnNum = 0;
			boolean wikiExists = false;
			String widget = "Wiki";
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Change Layout for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			BaseWidget widgetBkmks=BaseWidget.BOOKMARKS;
			BaseWidget widgetMembers=BaseWidget.MEMBERS;
			BaseWidget widgetTags=BaseWidget.TAGS;
			BaseWidget widgetCommDesc=BaseWidget.COMMUNITYDESCRIPTION;
			BaseWidget widgetFiles=BaseWidget.FILES;
			BaseWidget widgetForums=BaseWidget.FORUM;
			BaseWidget widgetImpBkmks=BaseWidget.IMPORTANTBOOKMARKS;
			BaseWidget widgetWiki = BaseWidget.WIKI;
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);

			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);

			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

			log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

			if (flag) {

			    log.info("INFO: Add the Overview page to the Community and make it the landing page");
			    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);

			}

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);

			log.info("INFO: Click on Community Actions");
			ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

			log.info("INFO: Click on the Change Layout link");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

			if(ui.checkGKSetting(Data.getData().commTabbedNav)){
				log.info("INFO: Select '2 Columns with side menu' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout2ColumnsWithSideMenu);

			}else {			
				log.info("INFO: Select '2 Columns' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout2ColumnsLink);

			}

			log.info("INFO: Check for the Communities link to make sure page is loaded");
			ui.isElementPresent(CommunitiesUIConstants.CommunitiesLink);

			log.info ("INFO: Validate the user is on the Overview page");
			Assert.assertEquals(driver.getTitle(),"Overview - "+ community.getName(),
					"ERROR: User is not on the parent community Overview page");

			log.info("INFO: Verifying the correct layout displays");
			log.info("INFO: Verify the left narrow column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutLeftColumn),
					"ERROR: Left narrow column does not exist for 2 Columns layout");

			log.info("INFO: Verify the wide middle column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutWideColumn),
					"ERROR: Wide column does not exist for 2 Columns layout");

			driver.changeImplicitWaits(3);
			
			log.info("INFO: Verify the right narrow column does not exist");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.communityLayoutRightColumn),
					"ERROR: Right narrow column exists for 2 Columns layout");

			driver.turnOnImplicitWaits();
			
			log.info("INFO: Get the column number the Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetBkmks,true);

			log.info("INFO: Verify the Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Members widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetMembers,true);

			log.info("INFO: Verify the Members widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Members widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Community Description widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetCommDesc,true);

			log.info("INFO: Verify the Community Description widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Community Description widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Files widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetFiles,true);

			log.info("INFO: Verify the Files widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Files widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Forums widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetForums,true);

			log.info("INFO: Verify the Forums widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Forums widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Important Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetImpBkmks,true);

			log.info("INFO: Verify the Important Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Important Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Tags widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetTags,true);

			log.info("INFO: Verify the Tags widget appears in the correct column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Tags widget is not in the correct column.  Expected column 1, but found in column " + columnNum);

			//Check to see if Wiki widgets appears on the Overview page
			//Wiki widget should appear by default in a cloud environment
			log.info("Looking for " + widget + " widget in wide column. ");
			List<Element> middleColumnWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets);

			log.info("INFO: size of center content area " + middleColumnWidgets.size());
			for(Element ewidget : middleColumnWidgets){			
				String widgetId = ewidget.getAttribute("widgetid");
				log.info("INFO: widget id : " + widgetId);
				log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
				if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(widget)){
					wikiExists = true;
					break;
				}
			}

			if (wikiExists){				
				log.info("INFO: If environment is Cloud, do a Wiki widget verification");

				log.info("INFO: Get the column number the Wiki widget is located in");
				columnNum = ui.getWidgetLocationInfo(widgetWiki,true);

				log.info("INFO: Verify the Wiki widget appears in the correct column");
				Assert.assertEquals(columnNum, 2,
						"ERROR: Wiki widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			}else {log.info("INFO: On-prem environment - No Wiki verification is done");
			}								
			log.info("INFO:Cleanup - Removing community for Test case " + testName );
			community.delete(ui, testUser);

			ui.endTest();
	}
	
	
	/**
	 *<li><B>Test Scenario: Change Layout - 3 Columns with Banner </B></li> 
	 *<li><B>Info:</B> Test the ability to change the layout to the 3 Columns with banner layout </li>
	 *<li><B>Step:</B> Create a Public community using the API </li>
	 *<li><B>Step:</B> Click on the Community Actions link </li>
	 *<li><B>Step:</B> Click on the Change Layout link </li>
	 *<li><B>Step:</B> Select the 3 Columns with banner layout from the palette </li>
	 *<li><B>Verify:</B> Verify the user is on the Overview page </li>
	 *<li><B>Verify:</B> If Tabbed Nav GK flag is enabled: Verify the Rich Content widget appears in the banner </li>
	 *<li><B>Verify:</B> If Tabbed Nav GK flag is not enabled: Verify the message Drag app(s) here displays in the banner area </li>
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/C57F5C2141552B2B85257DDA00484E22">TTT- CHANGE COMMUNITY LAYOUT</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud","cnx8ui-regression"})
	public void changeLayout_3ColumnWithBanner(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			int columnNum = 0;
			boolean wikiExists = false;
			String widget = "Wiki";
			
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Change Layout for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			BaseWidget widgetBkmks=BaseWidget.BOOKMARKS;
			BaseWidget widgetMembers=BaseWidget.MEMBERS;
			BaseWidget widgetTags=BaseWidget.TAGS;
			BaseWidget widgetCommDesc=BaseWidget.COMMUNITYDESCRIPTION;
			BaseWidget widgetFiles=BaseWidget.FILES;
			BaseWidget widgetForums=BaseWidget.FORUM;
			BaseWidget widgetImpBkmks=BaseWidget.IMPORTANTBOOKMARKS;
			BaseWidget widgetWiki=BaseWidget.WIKI;
			BaseWidget widgetRichContent=BaseWidget.RICHCONTENT;
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);			
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
			
			log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

			if (flag) {

			    log.info("INFO: Add the Overview page to the Community and make it the landing page");
			    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);

			}

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Click on Community Actions");
			ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
			
			log.info("INFO: Click on the Change Layout link");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

			if(ui.checkGKSetting(Data.getData().commTabbedNav)){
				log.info("INFO: Select '3 Columns with side menu and banner' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsSideMenuAndBanner);

			}else {			
				log.info("INFO: Select '3 Columns with banner' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithBannerLink);

			}

			log.info("INFO: Check for the Communities link to make sure page is loaded");
			ui.isElementPresent(CommunitiesUIConstants.CommunitiesLink);

			log.info ("INFO: Validate the user is on the Overview page");
			Assert.assertEquals(driver.getTitle(),"Overview - "+ community.getName(),
					"ERROR: User is not on the parent community Overview page");			

			if(ui.checkGKSetting(Data.getData().commTabbedNav)){
				log.info("INFO: Layout with Tabbed Nav is being used");

				log.info("INFO: Get the column number the Rich Content widget is located in");
				columnNum = ui.getWidgetLocationInfo(widgetRichContent,true);

				log.info("INFO: Verify the Rich Content widget appears in the banner area");
				Assert.assertEquals(columnNum, 4,
						"ERROR: Rich Content widget is not in the correct column.  Expected column 4 (banner area), but found in column " + columnNum);

			}else {			
				log.info("INFO: Verify the Drag app(s) here message displays in the banner");
				Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.communityBannerArea).getText().contentEquals(Data.getData().emptyColumnMessage),
						"ERROR: Drag app(s) here message does not display in the banner");	

			}	
			log.info("INFO: Verify the left narrow column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutLeftColumn),
					"ERROR: Left narrow column does not exist for 3 Columns with banner layout");

			log.info("INFO: Verify the middle column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutWideColumn),
					"ERROR: Middle column does not exist for 3 Columns with banner layout");

			log.info("INFO: Verify the right narrow column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutRightColumn),
					"ERROR: Right narrow column does not exist for 3 Columns with banner layout");	

			log.info("INFO: Get the column number the Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetBkmks,true);

			log.info("INFO: Verify the Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Members widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetMembers,true);

			log.info("INFO: Verify the Members widget appears in the correct column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Members widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Community Description widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetCommDesc,true);

			log.info("INFO: Verify the Community Description widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Community Description widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Files widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetFiles,true);

			log.info("INFO: Verify the Files widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Files widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Forums widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetForums,true);

			log.info("INFO: Verify the Forums widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Forums widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Important Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetImpBkmks,true);

			log.info("INFO: Verify the Important Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Important Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);

			log.info("INFO: Get the column number the Tags widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetTags,true);

			log.info("INFO: Verify the Tags widget appears in the correct column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Tags widget is not in the correct column.  Expected column 1, but found in column " + columnNum);

			//Check to see if Wiki widgets appears on the Overview page
			//Wiki widget should appear by default in a cloud environment
			log.info("Looking for " + widget + " widget in wide column. ");
			List<Element> middleColumnWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets);

			log.info("INFO: size of center content area " + middleColumnWidgets.size());
			for(Element ewidget : middleColumnWidgets){			
				String widgetId = ewidget.getAttribute("widgetid");
				log.info("INFO: widget id : " + widgetId);
				log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
				if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(widget)){
					wikiExists = true;
					break;
				}
			}

			if (wikiExists){
				log.info("INFO: If environment is Cloud, do a Wiki widget verification");

				log.info("INFO: Get the column number the Wiki widget is located in");
				columnNum = ui.getWidgetLocationInfo(widgetWiki,true);

				log.info("INFO: Verify the Wiki widget appears in the correct column");
				Assert.assertEquals(columnNum, 2,
						"ERROR: Wiki widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			}else {
				log.info("INFO: On-prem environment - No Wiki verification is done");
			}								

			log.info("INFO:Cleanup - Removing community for Test case " + testName );
			community.delete(ui, testUser);

			ui.endTest();


	}
	
	/**
	 *<li><B>Test Scenario: Change Layout - 2 Columns to 3 Columns (default) </B></li> 
	 *<li><B>Info:</B> Test the ability to change the layout from 2 column to 3 column default layout </li>
	 *<li><B>Step:</B> Create a Public community using the API </li>
	 *<li><B>Step:</B> Check to see if Change Layout is enabled in Gatekeeper - if not skip test</li>
	 *<li><B>Step:</B> Click on the Community Actions link </li>
	 *<li><B>Step:</B> Click on the Change Layout link </li>
	 *<li><B>Step:</B> Select the 2 Column layout from the palette </li>
	 *<li><B>Step:</B> Change the layout back to 3 column default </li>
	 *<li><B>Verify:</B> Verify the layout is the 3 column default layout </li>
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/C57F5C2141552B2B85257DDA00484E22">TTT- CHANGE COMMUNITY LAYOUT</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud","cnx8ui-regression"})
	public void changeLayout_2ColumnTo3ColDefault(){

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			int columnNum = 0;
			boolean wikiExists = false;
			String widget = "Wiki";
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Change Layout for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			BaseWidget widgetBkmks=BaseWidget.BOOKMARKS;
			BaseWidget widgetMembers=BaseWidget.MEMBERS;
			BaseWidget widgetTags=BaseWidget.TAGS;
			BaseWidget widgetCommDesc=BaseWidget.COMMUNITYDESCRIPTION;
			BaseWidget widgetFiles=BaseWidget.FILES;
			BaseWidget widgetForums=BaseWidget.FORUM;
			BaseWidget widgetImpBkmks=BaseWidget.IMPORTANTBOOKMARKS;
			BaseWidget widgetWiki = BaseWidget.WIKI;
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//load component and login as the community owner
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
			
			log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

			if (flag) {

			    log.info("INFO: Add the Overview page to the Community and make it the landing page");
			    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);

			}

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Check to see if GateKeeper setting " + Data.getData().gk_newWidgetLayouts_flag + " is enabled");			
			if(ui.checkGKSetting(Data.getData().gk_newWidgetLayouts_flag)){		

				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

				log.info("INFO: Select '2 Columns with side menu' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout2ColumnsWithSideMenu);

			}else{
				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
														
				//click on the Change Layout link
				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);
				
				log.info("INFO: Select '2 Columns' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout2ColumnsLink);
			}
			
			log.info("INFO: Changing layout back to 3 Columns (default)"); 
			
			if(ui.checkGKSetting(Data.getData().gk_newWidgetLayouts_flag)){		

				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

				log.info("INFO: Select '3 Columns (default)' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithSideMenu);

			}else{
				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
				
				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);
				
				log.info("INFO: Select '3 Columns (default)' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsLink);
			}
			log.info("INFO: Verifying the correct layout displays");
			
			driver.changeImplicitWaits(3);
			
			log.info("INFO: Verify there is no banner area - no Drag app(s) here msg should display");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.communityBannerArea),
					"ERROR: Banner area exists");
			
			driver.turnOnImplicitWaits();
			
			log.info("INFO: Verify the left narrow column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutLeftColumn),
					"ERROR: Left narrow column does not exist for 3 Columns(default) layout");
				
			log.info("INFO: Verify the middle column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutWideColumn),
					"ERROR: Middle column does not exist for 3 Columns(default) layout");
				
			log.info("INFO: Verify the right narrow column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutRightColumn),
					"ERROR: Right narrow column does not exist for 3 Columns(default) layout");	
			
			log.info("INFO: Get the column number the Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetBkmks,true);
			
			log.info("INFO: Verify the Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Members widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetMembers,true);
			
			log.info("INFO: Verify the Members widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Members widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Community Description widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetCommDesc,true);
			
			log.info("INFO: Verify the Community Description widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Community Description widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Files widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetFiles,true);
			
			log.info("INFO: Verify the Files widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Files widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Forums widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetForums,true);
			
			log.info("INFO: Verify the Forums widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Forums widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Important Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetImpBkmks,true);
			
			log.info("INFO: Verify the Important Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Important Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Tags widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetTags,true);
			
			log.info("INFO: Verify the Tags widget appears in the correct column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Tags widget is not in the correct column.  Expected column 1, but found in column " + columnNum);
			
			//Check to see if Wiki widgets appears on the Overview page
			//Wiki widget should appear by default in a cloud environment
			log.info("Looking for " + widget + " widget in wide column. ");
			List<Element> middleColumnWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets);
			
			log.info("INFO: size of center content area " + middleColumnWidgets.size());
			for(Element ewidget : middleColumnWidgets){			
				String widgetId = ewidget.getAttribute("widgetid");
				log.info("INFO: widget id : " + widgetId);
				log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
				if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(widget)){
					wikiExists = true;
					break;
				}
			}
			
			 if (wikiExists){
				//get the column number the Wiki widget is located in
				 log.info("INFO: If environment is Cloud, do a Wiki widget verification");

				 log.info("INFO: Get the column number the Wiki widget is located in");
				 columnNum = ui.getWidgetLocationInfo(widgetWiki,true);

				 //verify the Wiki widget is in the correct column
				 log.info("INFO: Verify the Wiki widget appears in the correct column");
				 Assert.assertEquals(columnNum, 2,
						 "ERROR: Wiki widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			 }else {log.info("INFO: On-prem environment - No Wiki verification is done");
			 }								
									
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
	
	
	}
	
	/**
	 *<li><B>Test Scenario: Change Layout - 3 Columns with banner to 3 Columns (default) </B></li> 
	 *<li><B>Info:</B> Test the ability to change the layout from 2 column to 3 column default layout </li>
	 *<li><B>Step:</B> Create a Public community using the API </li>
	 *<li><B>Step:</B> Click on the Community Actions link </li>
	 *<li><B>Step:</B> Click on the Change Layout link </li>
	 *<li><B>Step:</B> Select the 3 Column w/banner layout from the palette </li>
	 *<li><B>Step:</B> Change the layout back to 3 column default </li>
	 *<li><B>Verify:</B> Verify the 3 column default layout displays correctly </li>
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/C57F5C2141552B2B85257DDA00484E22">TTT- CHANGE COMMUNITY LAYOUT</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void changeLayout_3ColWithBannerTo3ColDefault() {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
			int columnNum = 0;
			boolean wikiExists = false;
			String widget = "Wiki";
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Change Layout for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser1))
														.build();
			
			BaseWidget widgetBkmks=BaseWidget.BOOKMARKS;
			BaseWidget widgetMembers=BaseWidget.MEMBERS;
			BaseWidget widgetTags=BaseWidget.TAGS;
			BaseWidget widgetCommDesc=BaseWidget.COMMUNITYDESCRIPTION;
			BaseWidget widgetFiles=BaseWidget.FILES;
			BaseWidget widgetForums=BaseWidget.FORUM;
			BaseWidget widgetImpBkmks=BaseWidget.IMPORTANTBOOKMARKS;
			BaseWidget widgetWiki=BaseWidget.WIKI;
			
			String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
			apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			
			log.info("INFO: Create community using API");
			Community comAPI = community.createAPI(apiOwner);
			
			
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, comAPI);
		
			//load component and login as the community owner
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);
			
			log.info("INFO: Check to see if GateKeeper setting " + Data.getData().gk_newWidgetLayouts_flag + " is enabled");
			log.info("INFO: If " + Data.getData().gk_newWidgetLayouts_flag + " is enabled change layout to 3 Columns with side men");
			if(ui.checkGKSetting(Data.getData().gk_newWidgetLayouts_flag)){		

				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

				log.info("INFO: Select '3 Columns with side menu' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithSideMenu);

			}else{
				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

				log.info("INFO: Select '3 Columns with banner' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithBannerLink);

				log.info("INFO: Change layout back to the 3 Columns (default)");			
				log.info("INFO: Click on Community Actions");
				ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

				log.info("INFO: Click on the Change Layout link");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

				log.info("INFO: Select '3 Columns (default)' from the layout palette");
				ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsLink);
			}
			
			log.info("INFO: Verifying the correct layout displays");
			
			log.info("INFO: Verify there is no banner area - no Drag app(s) here msg should display");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.communityBannerArea),
					"ERROR: Banner area exists");
				
			log.info("INFO: Verify the left narrow column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutLeftColumn),
					"ERROR: Left narrow column does not exist for 3 Columns(default) layout");
				
			log.info("INFO: Verify the middle column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutWideColumn),
					"ERROR: Middle column does not exist for 3 Columns(default) layout");
				
			log.info("INFO: Verify the right column exists");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityLayoutRightColumn),
					"ERROR: Right column does not exist for 3 Columns(default) layout");
			
			log.info("INFO: Get the column number the Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetBkmks,true);
			
			log.info("INFO: Verify the Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Members widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetMembers,true);
			
			log.info("INFO: Verify the Members widget appears in the correct column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Members widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Community Description widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetCommDesc,true);
			
			log.info("INFO: Verify the Community Description widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Community Description widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Files widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetFiles,true);
			
			log.info("INFO: Verify the Files widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Files widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Forums widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetForums,true);
			
			log.info("INFO: Verify the Forums widget appears in the correct column");
			Assert.assertEquals(columnNum, 2,
					"ERROR: Forums widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Important Bookmarks widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetImpBkmks,true);
			
			log.info("INFO: Verify the Important Bookmarks widget appears in the correct column");
			Assert.assertEquals(columnNum, 3,
					"ERROR: Important Bookmarks widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			
			log.info("INFO: Get the column number the Tags widget is located in");
			columnNum = ui.getWidgetLocationInfo(widgetTags,true);
			
			log.info("INFO: Verify the Tags widget appears in the correct column");
			Assert.assertEquals(columnNum, 1,
					"ERROR: Tags widget is not in the correct column.  Expected column 1, but found in column " + columnNum);
			
			//Check to see if Wiki widgets appears on the Overview page
			//Wiki widget should appear by default in a cloud environment
			log.info("Looking for " + widget + " widget in wide column. ");
			List<Element> middleColumnWidgets = driver.getElements(CommunitiesUIConstants.contentEnabledWidgets);
			
			log.info("INFO: size of center content area " + middleColumnWidgets.size());
			for(Element ewidget : middleColumnWidgets){			
				String widgetId = ewidget.getAttribute("widgetid");
				log.info("INFO: widget id : " + widgetId);
				log.info(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText());
				if(driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetId)).getText().contentEquals(widget)){
					wikiExists = true;
					break;
				}
			}
			
			 if (wikiExists){
				//get the column number the Wiki widget is located in
				 log.info("INFO: If environment is Cloud, do a Wiki widget verification");

				 log.info("INFO: Get the column number the Wiki widget is located in");
				 columnNum = ui.getWidgetLocationInfo(widgetWiki,true);

				 //verify the Wiki widget is in the correct column
				 log.info("INFO: Verify the Wiki widget appears in the correct column");
				 Assert.assertEquals(columnNum, 2,
						 "ERROR: Wiki widget is not in the correct column.  Expected column 2, but found in column " + columnNum);
			 }else {log.info("INFO: On-prem environment - No Wiki verification is done");
			 }								
									
			log.info("INFO: Removing community for Test case " + testName );
			community.delete(ui, testUser);
			
			ui.endTest();
	
	
	}
	
	/**
	*<li><B>Test Scenario: Change Layout Palette </B></li> 
	 *<li><B>Info:</B> Verify the options that appear on the layout palette </li>
	 *<li><B>Step:</B> Create a Public community using the API </li>
	 *<li><B>Step:</B> Click on the Community Actions link </li>
	 *<li><B>Step:</B> Click on the Change Layout link </li>
	 *<li><B>Verify:</B> If Tabbed Nav GK flag is enabled then verify (5) palette options exist: 2 Columns with side menu, 3 Columns with side menu, 3 Columns with side menu and banner </li>
	 *<li><B>Verify:</B> If Tabbed Nav GK flag is enabled verification continued: 3 Column with top menu, and 3 Columns with top menu and banner </li> 
	 *<li><B>Verify:</B> If Tabbed Nav GK flag is not enabled verify these options exist: 2 Columns, 3 Columns (default), 3 Columns with banner </li>
	 *<li><B>Cleanup:</B> Delete the community </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/088F49BF479C68258525751B0060FA97/C57F5C2141552B2B85257DDA00484E22">TTT- CHANGE COMMUNITY LAYOUT</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud", "cnx8ui-regression"})
	public void changeLayoutPalette() {

			String rndNum = Helper.genDateBasedRand();
			String testName = ui.startTest();
								
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + rndNum )
														.description("Test Change Layout for community " + testName)
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
			ui.loginAndToggleUI(testUser, cfg.getUseNewUI());

			log.info("INFO: Check whether the Landing Page for the Community is Overview or Highlights");
			Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

			if (flag) {

			    log.info("INFO: Add the Overview page to the Community and make it the landing page");
			    apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);

			}

			log.info("INFO: Navigate to the community using UUID");
			community.navViaUUID(ui);

			log.info("INFO: Click on Community Actions");
			ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

			log.info("INFO: Click on the Change Layout link");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

			if(ui.checkGKSetting(Data.getData().commTabbedNav)){
				log.info("INFO: Verify the link '2 Columns with side menu' appears on the layout palette");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.communityChangeLayout2ColumnsWithSideMenu),
						"ERROR: '2 Columns with side menu' link does not exist on the layout palette ");

				log.info("INFO: Verify the link '3 Columns with side menu' appears on the layout palette");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithSideMenu),
						"ERROR: '3 Columns with side menu' link does not exist on the layout palette ");

				log.info("INFO: Verify the link '3 Columns with side menu and banner' appears on the layout palette");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsSideMenuAndBanner),
						"ERROR: '3 Columns with side menu and banner' link does not exist on the layout palette ");	

				log.info("INFO: Verify the link '3 Columns with top menu' appears on the layout palette");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithTopMenu),
						"ERROR: '3 Columns with top menu' link does not exist on the layout palette ");	

				log.info("INFO: Verify the link '3 Columns with top menu and banner' appears on the layout palette");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithTopMenuAndBanner),
						"ERROR: '3 Columns with top menu and banner' link does not exist on the layout palette ");	

			}else {			
				log.info("INFO: Verify the link '2 Columns' appears on the layout palette");
				Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.communityChangeLayout2ColumnsLink),
						"ERROR: '2 Columns' link does not exist on the layout palette ");

				log.info("INFO: Verify the link '3 Columns' appears on the layout palette");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsLink),
						"ERROR: '3 Columns' link does not exist on the layout palette ");

				log.info("INFO: Verify the link '3 Columns with banner' appears on the layout palette");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.communityChangeLayout3ColumnsWithBannerLink),
						"ERROR: '3 Columns with banner' link does not exist on the layout palette ");		

			}
			log.info("INFO:Cleanup - Removing community for Test case " + testName );
			community.delete(ui, testUser);

			ui.endTest();
	}
	
	/**
	*Info: Verify StartPage menu for community/SubCommunity are as expected
	*@param: List widgets, widgets in left Nav.
	*@return: None
	*/
	private void verifyStartPageOptions(List<String> widgets) {
		//To validate start page options for community/subCommunity are present accordingly
		log.info("To validate start page options for community/subCommunity are present");
		boolean check = true;
		
		//Test Start page options on the Community/SubCommunity Edit page.
		log.info("INFO: Click on the Start Page drop-down menu");
		driver.getSingleElement(CommunitiesUIConstants.editCommunityStartPageDropDown).click();
		List<Element> menu = driver.getSingleElement(CommunitiesUIConstants.editCommunityStartPageDropDown).useAsDropdown().getOptions();
		
		//Test number of options present in Start page drop down
		log.info("INFO: Verify number of options on the Start page drop-down menu");
		Assert.assertEquals(menu.size(), widgets.size(),"ERROR: Number of options in Start Page is not matching with widgets in left Nav.(excluding Members, Metrics)");
		
		//Comparing options
		log.info("INFO: options in Start Page: "+ menu.size());
		log.info("INFO: Compare options in Start Page in order");
		for(int iterator = 0 ; iterator < menu.size() ; iterator++) {
				//To check if both the ArrayList items matches
				if(!menu.get(iterator).getText().toLowerCase().contains(widgets.get(iterator).toLowerCase()))
						check = false;
		}
		
		//Verify Start page options and their alphabetical order is same or not in Community/SubCommunity Edit page
		log.info("INFO: Verify Start page options and their alphabetical order is same or not in Community/SubCommunity Edit page");
		Assert.assertTrue(check,
							"ERROR: Start page options and their alphabetical order are not displayed correctly in Community/SubCommunity Edit page");
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Top Level Community Dialog Box UI </li>
	 * <li><B>Info:</B> This test will verify the UI of the dialog box that appears when moving a top level community</li>
	 * <li><B>Step:</B> Create a community using the API </li>
	 * <li><B>Step:</B> Login & open the community
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> From the Overview page click on Community Actions > Move Community </li>
	 * <li><B>Verify:</B> Verify the Move Community dialog displays</li>		
	 * <li><B>Verify:</B> Verify the option "Make this a subcommunity of:" displays</li>
	 * <li><B>Verify:</B> Verify the option "Make this a top level community" does not display</li>
	 * <li><B>Verify:</B> Verify the Move button displays</li>
	 * <li><B>Verify:</B> Verify the Cancel button displays</li>
	 * <li><B>Step:</B> Click on the Cancel button to clear the dialog </li>
	 * <li><B>Verify:</B> Verify the Move Community dialog no longer appears </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveToplevelCommDialogUI(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Top-Level Community";

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Test Move Community dialog for top-level community.")
													.build();
								
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
								
		//select Community Actions menu option Move Community
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
								
		//verify the move community dialog box displays
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommunityDialogBox),
				"ERROR: The Move Community dialog box did not display");
		
		//verify the text "Make this a subcommunity of:" displays on the dialog box
		log.info("INFO: Verify the text 'Make this a subcommunity of:' displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData() .makeThisASubcommOfMsg),
				"ERROR: The text 'Make this a subcommunity of:' does not appear on the dialog box");
						
		//verify the text "Make this a top level community" does NOT display on the dialog box
		log.info("INFO: Verify the text 'Make this a top level community' does not display");
		Assert.assertFalse(driver.isTextPresent(Data.getData().makeThisATopLevelCommMsg),
				"ERROR: The text 'Make this a top level community' appears on the dialog box");
		
		//verify the Close(X) icon displays
		log.info("INFO: Verify the Close(X) icon displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommDialogCloseButton),
				"ERROR: The Close(X) icon does not display on the dialog box");
		
		//verify Move button displays
		log.info("INFO: Verify the Move button exists on the dialog box");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveButton),
				"ERROR: The Move button does not exist on the dialog box");
		
		//verify Cancel button displays
		log.info("INFO: Verify the Cancel button exists on the dialog box");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveTopLevelCommCancelButton),
				"ERROR: The Cancel button does not exist on the dialog box");
		
		//click on the Cancel button
		log.info("INFO: Click on the Cancel button");
		driver.getFirstElement(CommunitiesUIConstants.moveTopLevelCommCancelButton).click();
		
		//verify the Move Community dialog no longer displays
		log.info("INFO: Verify that the Move Community dialog no longer displays after clicking the Cancel button");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.moveCommunityDialogBox),
				"ERROR: The Move Community dialog box still displays after clicking Cancel");
					
		//delete community
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser);	
			
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Subcommunity Dialog Box UI </li>
	 * <li><B>Info:</B> This test will verify the UI of the dialog box that appears when moving a subcommunity </li>
	 * <li><B>Step:</B> Create a community using the API </li>
	 * <li><B>Step:</B> Create a subcommunity </li>
	 * <li><B>Verify:</B> The subcommunity was successfully created </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> From the subcommunity Overview page click on Community Actions > Move Community </li>
	 * <li><B>Verify:</B> Verify the Move Community dialog displays</li>
	 * <li><B>Verify:</B> Verify the option "Make this a top level community" displays </li>
	 * <li><B>Verify:</B> Verify the option "Make this a subcommunity of:" displays </li>
	 * <li><B>Verify:</B> Verify the option "Make this a top level community" is selected by default </li>
	 * <li><B>Verify:</B> Verify the Move button displays</li>
	 * <li><B>Verify:</B> Verify the Cancel button displays</li>
	 * <li><B>Step:</B> Click on the Cancel button to clear the dialog </li>
	 * <li><B>Verify:</B> Verify the Move Community dialog no longer appears </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveSubcommDialogUI() {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Public Parent Comm1";
		String comm2 = "Re-Parent Me SubComm1";
		
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community.")
													.build();
		 		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm2 + rndNum)
										   .access(BaseSubCommunity.Access.MODERATED)
		                                   .tags(Data.getData().commonTag + rndNum)
										   .description("Subcommunity to test Move Community dialog UI")
										   .build();
									
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Creating a subcommunity ");
		subCommunity.create(ui);
		
		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
				"ERROR : subcommunity is not created");
		
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveSubcommDialogBox),
				"ERROR: The Move Community dialog box did not display");
		
		log.info("INFO: Verify the text 'Make this a subcommunity of:' displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().makeThisASubcommOfMsg),
				"ERROR: The text 'Make this a subcommunity of:' does not appear on the dialog box");
		
		log.info("INFO: Verify the text 'Make this a top level community' displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().makeThisATopLevelCommMsg),
				"ERROR: The text 'Make this a top level community' does not on the dialog box");
				
		log.info("INFO: Verify the option to make the subcommunity a top level community is selected by default");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.makeTopLevelRadioButton).getAttribute("checked").contentEquals("true"),
				"ERROR: The radio button to make the subcomm a top level is not selected by default");
		
		log.info("INFO: Verify the Close(X) icon displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveSubcommDialogCloseButton),
				"ERROR: The Close(X) icon does not display on the dialog box");
		
		log.info("INFO: Verify the Move button exists on the dialog box");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveButton),
				"ERROR: The Move button does not exist on the dialog box");
		
		log.info("INFO: Verify the Cancel button exists on the dialog box");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveSubcommCancelButton),
				"ERROR: The Cancel button does not exist on the dialog box");
		
		log.info("INFO: Click on the Cancel button");
		driver.getFirstElement(CommunitiesUIConstants.moveSubcommCancelButton).click();
		
		log.info("INFO: Verify that the Move Community dialog no longer displays after clicking the Cancel button");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.moveCommunityDialogBox),
				"ERROR: The Move Community dialog box still displays after clicking Cancel");
				
		log.info("INFO:Click on parent community link");
		ui.clickLinkWait(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage);
		
		log.info("INFO: Cleanup - Removing community for Test case " + testName );
		community.delete(ui, testUser);
			
		
		ui.endTest();
		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Subcommunity To Be a Top Level Community </li>
	 * <li><B>Info:</B> This test will verify moving a subcommunity to be a top level community </li>
	 * <li><B>Step:</B> Create a community using the API </li>
	 * <li><B>Step:</B> Create a subcommunity </li>
	 * <li><B>Verify:</B> The subcommunity was successfully created </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> From the subcommunity Overview page click on Community Actions > Move Community </li>
	 * <li><B>Verify:</B> Verify the Move Community dialog displays</li>
	 * <li><B>Step:</B> Click on the Move button </li>
	 * <li><B>Verify:</B> Verify the Move success message displays </li>
	 * <li><B>Verify:</B> Verify there is no link to the parent community in the breadcrumb </li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void moveSubcommToBeTopLevel(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Public Parent Comm1";
		String comm2 = "Subcomm1 - Make Me a Top Level Comm";

		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community.")
													.build();
				 		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm2 + rndNum)
										   .access(BaseSubCommunity.Access.MODERATED)
		                                   .tags(Data.getData().commonTag + rndNum)
										   .description("Subcommunity to become a top level community")
										   .build();
								
	
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
													
		log.info("INFO: Log into Communities as user: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Creating Subcommunity ");
		subCommunity.create(ui);
		
		log.info("INFO: Make sure the subcommunity Name field is not empty");
		ui.checkCommunityNameFieldEmptyMsg(subCommunity);
		
		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
				"ERROR : subcommunity is not created");
		
		log.info("INFO: Select customize under community action menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveSubcommDialogBox),
				"ERROR: The Move Community dialog box did not display");
		
		log.info("INFO: Verify the option to make the subcommunity a top level community is selected by default");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.makeTopLevelRadioButton).getAttribute("checked").contentEquals("true"),
				"ERROR: The radio button to make the subcomm a top level is not selected by default");
		
		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();
		
		log.info("INFO: Verify the success message displays after moving the subcomm to be a top level community");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommSuccessMsg),
				"ERROR: The success message did not display");
		
		log.info("INFO: Verify there is no link to the parent community on the breadcrumb");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage),
				"ERROR: There is a link to the parent community on the breadcrumb");
		
		log.info("INFO: Removing community for Test case " + testName );
		subCommunity.delete(ui, testUser);
		
		log.info("INFO: Clicking on the communities link");			
		ui.clickLinkWait(CommunitiesUIConstants.CommunitiesLink);
		
		log.info("INFO: Open community");
		ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
					
		ui.endTest();
		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Subcommunity To A New Parent Community </li>
	 * <li><B>Info:</B> This test will verify moving a subcommunity to a new parent community </li>
	 * <li><B>Step:</B> Create a community using the API </li>
	 * <li><B>Step:</B> Create a subcommunity </li>
	 * <li><B>Verify:</B> The subcommunity was successfully created </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> From the subcommunity Overview page click on Community Actions > Move Community </li>
	 * <li><B>Verify:</B> Verify the Move Community dialog displays</li>
	 * <li><B>Step:</B> Select the option to make the community the subcommunity of </li>
	 * <li><B>Step:</B> Start to enter the name of the community to be the new parent into typeahead </li>
	 * <li><B>Step:</B> Select the community to be the new parent from typeahead </li>
	 * <li><B>Step:</B> Click the Move button </li>
	 * <li><B>Verify:</B> Verify the Move Community success message displays </li>
	 * <li><B>Verify:</B> Verify there is a breadcrumb to the parent community </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveSubcommToNewParent(){
		
		String rndNum = Helper.genDateBasedRand();
		String comm1 = "Public Parent Comm2 To Become New Parent";
		String comm2 = "Public Parent Comm1 - Original Parent";
		String comm3 = "Re-parent me Subcomm1";

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community1 - to be new parent community")
													.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
    	                                            .access(Access.PUBLIC)	
                                                    .tags(Data.getData().commonTag + rndNum )
                                                    .description("Top-level community2 with a subcommunity")
                                                    .build();		
		
		//Create a sub-community base state object 		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm3 + rndNum)
										   .access(BaseSubCommunity.Access.MODERATED)
		                                   .tags(Data.getData().commonTag + rndNum)
										   .description("Subcommunity to be moved to a new parent")
										   .build();
								
	
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI2);
													
		//Load component and login as the community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Creating a subcommunity ");
		subCommunity.create(ui);
		
		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(ui.fluentWaitTextPresent(subCommunity.getName()),
				"ERROR : subcommunity is not created");
		
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveSubcommDialogBox),
				"ERROR: The Move Community dialog box did not display");
		
		log.info("INFO: Select the radio button 'Make this a subcommunity of:'");
		ui.clickLinkWait(CommunitiesUIConstants.makeSubcommRadioButton);
		
		log.info("INFO: Verify the community name input field displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommToSubcommInputField),
				"ERROR: The community name input field does not exist");
		
		log.info("INFO: Enter the name of the community to be the new parent community");
		ui.typeTextWithDelay(CommunitiesUIConstants.moveCommToSubcommInputField, (community.getName()));
					
		log.info("INFO: Select the community to be the new parent community from the typeahead results");
		ui.typeaheadSelection(community.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);
		
		log.info("INFO: Click on the Move button");
		//driver.getFirstElement(CommunitiesUI.moveButton).click();
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveButton);
		
		
		log.info("INFO: Verify the success message displays after moving the subcomm to be a new parent community");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommSuccessMsg),
				"ERROR: The success message did not display");
		
		log.info("INFO: Verify there is a link to the parent community on the breadcrumb");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage),
				"ERROR: There is no link to the parent community on the breadcrumb");
																	
		log.info("INFO: Cleanup: Delete communities");		
		apiOwner.deleteCommunity(comAPI);
		apiOwner.deleteCommunity(comAPI2);
			
		
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Community To Be a Subcommunity </li>
	 * <li><B>Info:</B> This test will verify the ability to make a top level community a subcommunity </li>
	 * <li><B>Step:</B> Create 2 communities using the API </li>
	 * <li><B>Step:</B> Open one of the communities </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>		 
	 * <li><B>Step:</B> Click on Community Actions & select Move Community </li>
	 * <li><B>Step:</B> Start entering the name of the community to make this a subcommunity of</li>
	 * <li><B>Step:</B> Select community to become the parent from the typeahead results</li>
	 * <li><B>Step:</B> Click on the Move button </li>
	 * <li><B>Verify:</B> Verify the Move success message displays </li>
	 * <li><B>Verify:</B> Verify there is a link to the parent community on the breadcrumb </li>
	 * <li><B>Cleanup:</B> Delete the community using API </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveTopLevelToBeSubcomm() {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Public Parent Comm1";
		String comm2 = "Make Me a Subcomm";
		
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Move Community: this will be the parent community")
													.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
	                                            	.access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum )
		                                            .description("Move Community: make this top-level comm a subcommunity")
		                                            .build();		
		
	
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
			
		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI2);
	
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		log.info("INFO: Navigate to the 2nd community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Verify the second community was created");
		Assert.assertTrue(driver.getTitle().contains(community2.getName()),
							"ERROR: Second community was not created");
		
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Verify the community name input field displays on the move community dialog");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommToSubcommInputField),
				"ERROR: The community name input field does not exist");
		
		log.info("INFO: Enter the name of the community to be the parent community");
		ui.typeTextWithDelay(CommunitiesUIConstants.moveCommToSubcommInputField, (community.getName()));
		
		log.info("INFO: Select the community to be the parent from the typeahead results");		
		ui.typeaheadSelection(community.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);
		
		log.info("INFO: Click on the Move button");
		//driver.getFirstElement(CommunitiesUI.moveButton).click();
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveButton);
		
		log.info("INFO: Verify the success message displays after moving the top level community a subcommunity");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommSuccessMsg),
				"ERROR: The success message did not display");
		
		log.info("INFO: Verify there is a link to the parent community on the breadcrumb");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage),
				"ERROR: There is no link to the parent community on the breadcrumb");
		
		log.info("INFO: Cleanup - Removing community for Test case " + testName );
		apiOwner.deleteCommunity(comAPI);
				
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Community 2nd Dialog Panel UI </li>
	 * <li><B>Info:</B> This test will verify the 2nd panel UI </li>
	 * <li><B>Step:</B> Create (2) communities using the API - (1) Moderated, (1) Public </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> Open the Public community </li>
	 * <li><B>Step:</B> Click Community Actions > Move Community
	 * <li><B>Verify:</B> Verify the Move Community dialog displays</li>
	 * <li><B>Step:</B> Type Moderated comm name into type-ahead </li>
	 * <li><B>Step:</B> Select the Moderated comm from type-ahead results </li>
	 * <li><B>Verify:</B> Verify the 2nd Move Community panel displays</li>
	 * <li><B>Verify:</B> Verify the warning icon displays</li>
	 * <li><B>Verify:</B> Verify the warning message displays</li>
	 * <li><B>Verify:</B> Verify the Close(X) icon displays</li>
	 * <li><B>Verify:</B> Verify the Move button displays </li>
	 * <li><B>Verify:</B> Verify the Cancel button displays </li>
	 * <li><B>Verify:</B> Verify the 'I acknowledge..." checkbox displays </li>
	 * <li><B>Verify:</B> Verify the 'I acknowledge..." message displays </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveComm2ndPanelUI() {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Moderated Parent Comm2";
		String comm2 = "Public Parent Comm1";
		
		BaseCommunity community1 = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.MODERATED)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community1")
													.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
    	                                            .access(Access.PUBLIC)	
                                                    .tags(Data.getData().commonTag + rndNum )
                                                    .description("Top-level community2 - make me a subcommunity ")
                                                    .build();		
												
	
		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
					
		// get the community link
		String communityLink1 = isCardView ? CommunitiesUI.getCommunityLinkCardView(community1) : CommunitiesUI.getCommunityLink(community1);
		String communityLink2 = isCardView ? CommunitiesUI.getCommunityLinkCardView(community2) : CommunitiesUI.getCommunityLink(community2);
				
		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);						
		
		log.info("INFO: Verify the second community was created");
		Assert.assertTrue(driver.getTitle().contains(community2.getName()),
							"ERROR: Second community was not created");
		
		log.info("INFO: Select Move Community from Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		log.info("INFO: Enter the 1st 2 characters in the name of the community to be the parent community");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommToSubcommInputField),
			"ERROR: The community name input field does not exist");
		
		log.info("INFO: Select the community to be the parent community from the typeahead results");
		ui.typeTextWithDelay(CommunitiesUIConstants.moveCommToSubcommInputField, (community1.getName()));
		ui.typeaheadSelection(community1.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);
		
		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();
		
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommunityDialogBox),
				"ERROR: The Move Community dialog box did not display");
		
		log.info("INFO: Verify the warning icon displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommWarningIcon),
				"ERROR: The warning message icon does not display");
		
				log.info("INFO: Verify the warning message displays");
				Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommWarningMsgText),
						"ERROR: The warning message does not display");
		
		log.info("INFO: Verify the Close(X) icon displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommDialogCloseButton),
				"ERROR: The Close(X) icon does not display on the dialog box");
		
		log.info("INFO: Verify the Move button exists on the dialog box");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveButton),
				"ERROR: The Move button does not exist on the dialog box");
		
		log.info("INFO: Verify the Cancel button exists on the dialog box");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveTopLevelCommCancelButton),
				"ERROR: The Cancel button does not exist on the dialog box");
		
		log.info("INFO: Verify the 'I acknowledge...' checkbox displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommIAcknowledgeCheckbox),
				"ERROR: The 'I acknowledge...' checkbox does not display on the dialog box");
		
		log.info("INFO: Verify the 'I acknowledge...' message displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommIAcknowledgeText),
				"ERROR: The 'I acknowledget...' message does not display");
		
		log.info("INFO: Click on the Cancel button");
		driver.getFirstElement(CommunitiesUIConstants.moveTopLevelCommCancelButton).click();
		
		log.info("INFO: Verify that the Move Community dialog no longer displays after clicking the Cancel button");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.moveCommunityDialogBox),
				"ERROR: The Move Community dialog box still displays after clicking Cancel");
		
		log.info("INFO: Navigate to the I'm an Owner catalog view");
		ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();
		
		log.info("INFO: Open community using UUID");
		community1.navViaUUID(ui);
			
		log.info("INFO: Removing community for Test case " + testName );
		community1.delete(ui, testUser);
		
		if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
		// added this code to force the code back to 'My Communities', it is currently going back to I'm a Member
			//My Communities view
		   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
		   ui.goToMyCommunitiesView(isCardView);
		}
		
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink2);
		
		log.info("INFO: Removing community for Test case " + testName );
		community2.delete(ui, testUser);
		
		ui.endTest();
		
	
	
}
	
	
	
	
	
	
	/**
	* <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Community Membership Change To Parent Community </li>
	 * <li><B>Info:</B> This test will verify the membership change to the parent community message displays </li>
	 * <li><B>Step:</B> Create (2) public communities - adding additional owner to the community that will become a subcomm </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> Open the Public community with the additional owner </li>
	 * <li><B>Step:</B> Click Community Actions > Move Community
	 * <li><B>Step:</B> Enter 2nd comm's name into type-ahead </li>
	 * <li><B>Step:</B> Select the comm from type-ahead </li>
	 * <li><B>Verify:</B> Verify the message that users will be added as members to the parent community displays </li>
	 * <li><B>Verify:</B> Verify the View Users twistie displays </li>
	 * <li><B>Step:</B> Expand the View Users twistie </li> 
	 * <li><B>Step:</B> Verify the user listed is the same as the additional owner of the subcommunity </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveCommViewMembers(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Top Level Community - Parent Community";
		String comm2 = "Top Level Community - To Become Subcommunity";
		String userToBeAddedAsMember = "";

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community1")
													.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
   	                                            .access(Access.PUBLIC)	
                                                   .tags(Data.getData().commonTag + rndNum )
                                                   .description("Top-level community2 - make me a subcommunity ")
                                                   .addMember(new Member(CommunityRole.OWNERS, testUser2))
                                                   .build();		
												
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//create a 2nd community
		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);
													
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
				
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
						
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);		
						
		//select Community Actions menu option Move Community
		log.info("INFO: Select customize under community action menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		//enter the name of the community to be the parent community
		log.info("INFO: Enter the name of the community to be the parent community");
		ui.typeText(CommunitiesUIConstants.moveCommToSubcommInputField, (community.getName()));
		
		//Select the community to become the parent
		log.info("INFO: Select the community to become the parent");
		ui.typeaheadSelection(community.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);
		
		//click on the Move button
		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();
		
		//verify the message that the users will be made members of the parent community displays
		log.info("INFO: Verify the message that users will be made members of the parent community displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommAddMembersToParentMsg),
				"ERROR: The message that users will be made members of the parent community did not display");
		
		//verify the Views Users twistie displays
		log.info("INFO: Verify the View Users twistie displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommViewMembersTwistie),
				"ERROR: The twistie to View Users to be added as members does not display");
					
		//click on the View Users twistie
		log.info("INFO: Click on the View Users twistie");
		//driver.getFirstElement(CommunitiesUI.moveCommViewMembersTwistie).click();
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveButton);
		
		//get the name of the user to be added as a Member to the parent community
		log.info("INFO: Get the name of the user to be added as a Member to the parent community");
		userToBeAddedAsMember=driver.getFirstElement("css=#lconn_comm_controls_NamesDisplay_0.lotusIndent20 span[dojoattachpoint='names']").getAttribute("innerHTML");
		
		//log the name of the user to be added as a Member & the name of the additional Owner in the subcomm: both should be the same
		log.info("INFO: User to be added to the parent community as a Member is '" + userToBeAddedAsMember + "'");
		log.info("INFO: Additional Owner of the subcommunity is '" + testUser2.getDisplayName() + "'");
		
		//Verify the user to be added as a Member to the parent community matches the additional owner of the subcommunity
		log.info("INFO: Verify the user to be added as a Member to the parent comm matches the additional owner of the subcomm");
		Assert.assertEquals(userToBeAddedAsMember, testUser2.getDisplayName(),
				"ERROR: User to be added as Member " + userToBeAddedAsMember + " does not match the additional Owner of the Subcommunity '" + testUser2.getDisplayName() + "");
													
		//click on the Cancel button
		log.info("INFO: Click on the Cancel button");
		driver.getFirstElement(CommunitiesUIConstants.moveTopLevelCommCancelButton).click();
															
		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		community2.delete(ui, testUser);
		
		if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
		// added this code to force the code back to 'My Communities', it is currently going back to I'm a Member
			//My Communities view
		   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
		   ui.goToMyCommunitiesView(isCardView);
		}
		
		//Open the community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
		ui.endTest();
		
	}
	
	
	
	/**
	* <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Community Membership Change To Subcomm </li>
	 * <li><B>Info:</B> This test will verify the membership change to the subcommunity message displays </li>
	 * <li><B>Step:</B> Create(2) public communities - adding additional owner to the community that will be the parent community </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> Open the Public community that will become the subcommunity </li>
	 * <li><B>Step:</B> Click Community Actions > Move Community
	 * <li><B>Step:</B> Enter 2nd comm's name into type-ahead </li>
	 * <li><B>Step:</B> Select the comm from type-ahead </li>
	 * <li><B>Verify:</B> Verify the message that users will be added as owner(s) to the subcommunity displays </li>
	 * <li><B>Verify:</B> Verify the View Users twistie displays </li>
	 * <li><B>Step:</B> Expand the View Users twistie </li> 
	 * <li><B>Step:</B> Verify the user listed is the same as the additional owner of the parent community </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveCommViewOwners(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Top Level Community - Public Parent Community";
		String comm2 = "Top Level Community - To Become Subcommunity";
		String userToBeAddedAsOwner = "";

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community1")														
													.addMember(new Member(CommunityRole.OWNERS, testUser1))
													.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
   	                                            .access(Access.PUBLIC)	
                                                   .tags(Data.getData().commonTag + rndNum )
                                                   .description("Top-level community2 - make me a subcommunity ")
                                                   .build();		
												
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//create a 2nd community
		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);
													
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
				
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community2.navViaUUID(ui);		
						
		//select Community Actions menu option Move Community
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
		
		//enter the name of the community to be made the parent community
		log.info("INFO: Enter the name of the community to be the parent community");
		ui.typeText(CommunitiesUIConstants.moveCommToSubcommInputField, (community.getName()));
		
		//Select the first community created
		log.info("INFO: Select the community to become the parent community from the typeahead results");
		ui.typeaheadSelection(community.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);
				
		//This step, to verify the Move button exists, was added to fix an intermittent failure on SmartCloud.  The Move button was not getting clicked.
		//Adding this step will ensure the Move button is present/loaded before it is clicked.
		log.info("INFO: Verify the Move button appears on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveButton),
				"ERROR: The Move button does not appear on the dialog box");
		
		//click on the Move button
		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();
								
		//verify the message "These (#) users will be made Owners of the subcommunity" displays
		log.info("INFO: Verify the message that users will be made Owners of the subcommunity displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommAddOwnersToSubcommMsg),
				"ERROR: The message that users will be made owners of the subcommunity did not display");
				
		//verify the Views Users twistie displays
		log.info("INFO: Verify the View Users twistie displays");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUIConstants.moveCommViewOwnersTwistie),
				"ERROR: The twistie to View Users to be added as owners does not display");
					
		//click on the View Users twistie
		log.info("INFO: Click on the View Users twistie");
		//driver.getFirstElement(CommunitiesUI.moveCommViewOwnersTwistie).click();
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveButton);
		
		//get the name of the user to be added as an Owner to the subcommunity
		log.info("INFO: Get the name of the user to be added as an Owner to the subcommunity");
		userToBeAddedAsOwner=driver.getFirstElement("css=#lconn_comm_controls_NamesDisplay_1.lotusIndent20 span[dojoattachpoint='names']").getAttribute("innerHTML");
		
		//log the name of the user to be added as an Owner & the name of the additional Owner in the parent community: both should be the same
		log.info("INFO: User to be added to the subcommunity as an Owner is '" + userToBeAddedAsOwner + "'");
		log.info("INFO: Additional Owner of the parent community is '" + testUser1.getDisplayName() + "'");
		
		//Verify the user to be added as a Member to the parent community matches the additional owner of the subcommunity
		log.info("INFO: Verify the user to be added as an Owner to the subcomm matches the additional owner of the parent community");
		Assert.assertEquals(userToBeAddedAsOwner, testUser1.getDisplayName(),
				"ERROR: User to be added as Owner does not match the additional Owner of the parent community '" + testUser1.getDisplayName() + "");
		
		//click on the Cancel button
		log.info("INFO: Click on the Cancel button");
		driver.getFirstElement(CommunitiesUIConstants.moveTopLevelCommCancelButton).click();
															
		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		community2.delete(ui, testUser);
		
		if(ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
			// added this code to force the code back to 'My Communities', it is currently going back to I'm a Member
			//My Communities view
		   log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
		   ui.goToMyCommunitiesView(isCardView);
		}
		
		//Open the community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
		ui.endTest();
		
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Move Community: Move Community Visibility & Membership Changes </li>
	 * <li><B>Info:</B> This test will verify the visibility & membership changes </li>
	 * <li><B>Step:</B> Create (2) communities using the API - (1) Moderated, (1) Public & add an additional owner to each community </li>
	 * <li><B>Step:</B> Check the Gatekeeper setting to see if Move Community is enabled. if yes - continue, if not - skip test </li>
	 * <li><B>Step:</B> Open the Public community </li>
	 * <li><B>Step:</B> Click Community Actions > Move Community
	 * <li><B>Step:</B> Type Moderated comm name into type-ahead </li>
	 * <li><B>Step:</B> Select the Moderated comm from type-ahead results </li>
	 * <li><B>Step:</B> Click the Move button on the 2nd move comm panel </li>
	 * <li><B>Step:</B> Navigate to the subcommunity full Members page </li>
	 * <li><B>Verify:</B> Verify the additional owner of the parent community appears as an Owner </li>
	 * <li><B>Verify:</B> Verify the community visibility has changed from Public to Moderated </li>
	 * <li><B>Step:</B> Open the parent community </li>
	 * <li><B>Step:</B> Navigate to the full Members page </li>
	 * <li><B>Verify:</B> Verify the additional owner of the subcommunity appears as a Member in the parent community </li> 
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/8FC47EB4CAD8210385257E45003B6FE9">MOVE COMMUNITY - D42</a></li>
	 *</ul>
	 */			
	
	@Test(groups = {"regression","regressioncloud","cnx8ui-regression"})
	public void moveCommMemberAndVisibilityChanges(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String comm1 = "Visibility Change - Moderated Community";
		String comm2 = "Public Community";

		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
													.access(Access.MODERATED)	
													.tags(Data.getData().commonTag + rndNum )
													.description("Top-level community1")														
													.addMember(new Member(CommunityRole.OWNERS, testUser1))
													.build();
		
		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
   	                                            .access(Access.PUBLIC)	
                                                   .tags(Data.getData().commonTag + rndNum )
                                                   .description("Top-level community2 - make me a subcommunity ")
                                                   .addMember(new Member(CommunityRole.OWNERS, testUser2))
                                                   .build();		
												
	
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//create a 2nd community
		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI2);
													
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
				
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
						
		//select Community Actions menu option Move Community
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(ui);
		ui.waitForPageLoaded(driver);
								
		//enter the name of the community you want to make the parent community
		log.info("INFO: Enter the name of the community to be the parent community");
		ui.typeTextWithDelay(CommunitiesUIConstants.moveCommToSubcommInputField, (community.getName()));
		
		//Select the community to be the parent community from typeahead results
		log.info("INFO: Select the to become the parent community from the typeahead results");
		ui.typeaheadSelection(community.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);
		
		//click on the Move button
		log.info("INFO: Click on the Move button");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveButton);
		
		//select the 'I acknowledge...' checkbox
		log.info("INFO: Select the 'I acknowledge...' checkbox");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveCommIAcknowledgeCheckbox);
		
		//click on the Move button on the 2nd move community panel
		log.info("INFO: Click on the Move button on the 2nd move community panel");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.moveButton);

		//verify the move success message displays
		log.info("INFO: Verify the success message displays after moving the top level to be a subcommunity");
		Assert.assertTrue(driver.isTextPresent(Data.getData().moveCommSuccessMsgWithMemberChanges),
		       "ERROR: The success message did not display " + Data.getData().moveCommSuccessMsgWithMemberChanges);
		
		//navigate to the full Members page
		log.info("INFO: Click on the members view all link");
		Community_LeftNav_Menu.MEMBERS.select(ui) ;	
		
		//Verify the additional owner from the parent community appears on the subcommunity members page as an Owner
		log.info("INFO: Verify the additional owner from the parent comm appears as an Owner on the subcomm Members page"); 
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.MemberDetailArea).getText(), testUser1.getDisplayName()+"\nOwner\nEdit | Remove",
							"ERROR: User does not appear on the Members page as an Owner");
		
		//Verify the Moderated icon appears on the full Members page
		log.info("INFO: Verify the Moderated icon appears on the full Members page");
		Assert.assertTrue(ui.fluentWaitElementVisible(CommunitiesUIConstants.moderatedIconFullMembersPage),
				"ERROR: The Moderated icon does not appear on the full Members page");
		
		//Open the parent community
		log.info("INFO: Open the parent community");
		//ui.clickLinkWait(CommunitiesUI.getCommunityLink(community));
		ui.clickLinkWait("xpath=//a[text()='"+community.getName()+"']");
		
		//navigate to the full Members page
		log.info("INFO: Click on the members view all link");
		Community_LeftNav_Menu.MEMBERS.select(ui) ;	
		
		//Verify the additional owner from the subcommunity appears as a Member on the parent community Members page
		log.info("INFO: Verify the additional owner from the subcommunity appears as a Member on the parent community Members page"); 
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.MemberDetailArea).getText(), testUser2.getDisplayName()+"\nMember\nEdit | Remove",
							"ERROR: User does not appear on the Members page as a Member");
							
		//Delete the community created in this test case
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
		ui.endTest();
		
	}
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> Email Privileges (Test 1): This test covers the default email option to mail the entire community</li>
	 * <li><B>Step:</B> Create a community with a member using the API</li>
	 * <li><B>Step:</B> Log into communities as the community creator</li>
	 * <li><B>Step:</B> Navigate to the Edit Community form</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Verify:</B> The Email Privileges option 'Members can email the entire community' is selected by default</li>
	 * <li><B>Verify:</B> The radio button 'Members can email owners only' exists</li>
	 * <li><B>Verify:</B> The radio button 'Members cannot email others' exists</li>
	 * <li><B>Step:</B> Logout as the community creator </li>
	 * <li><B>Step:</B> Login as the community member</li>
	 * <li><B>Step:</B> Open the community & click on Community Actions</li>
	 * <li><B>Step:</B> Click on the Mail Community link on the drop-down menu</li>
	 * <li><B>Verify:</B> The radio button 'Owners and Members' appears on the Email the Community dialog</li>
	 * <li><B>Verify:</B> The radio button 'Owners Only' appears on the Email the Community dialog</li>
	 * <li><B>Step:</B> Logout as the community member </li>
	 * <li><B>Step:</B> Login as the community creator
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void mailCommunityAllOwnersAndMembers(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		String defaultRadioButtonValue="true";

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test the 'Members can email the entire community' radio button " + testName + rndNum)
												   .addMember(member)
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//get the community UUID
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI
		//Load component and login
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//select Community Actions menu option Edit Community
		log.info("INFO: Select Edit Community on the community action menu");
		Com_Action_Menu.EDIT.select(ui);
		ui.waitForPageLoaded(driver);
		
		//click on the link Access Advanced Features
		log.info("INFO: Click on the link Access Advanced Features");
		driver.getSingleElement(CommunitiesUIConstants.comAdvancedLink).click();
		
		//verify the Email Privileges radio button 'Members can email the entire community' is select by default
		//verification needs to be done by checking the attribute 'checked' in the DOM.  If the radio button is selected, the attribute will be set to 'true'
		log.info("INFO: Verify the radio button 'Members can email the entire community' is selected by default");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.communitiesMailAllRadioBtn).getAttribute("checked").contentEquals(defaultRadioButtonValue),
				"ERROR: The radio button 'Members can email the entire community' is not selected by default");
		
		//verify the Email Privileges radio button 'Members can email owners only' displays
		log.info("INFO: Verify the radio button 'Members can email owners only' displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communitiesMailOwnersOnlyRadioBtn),
				"ERROR: The radio button 'Members can email the entire community' does not display");
		
		//verify the Email Privileges radio button "Members cannot email others' displays
		log.info("INFO: Verify the radio button 'Members cannot email others' displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communitiesMailNoOneRadioBtn),
				"ERROR: The radio button 'Members cannot email others' does not display");
		
		//log out of communities as the community creator
		log.info("INFO: Log out of communities as the community creator");
		ui.logout();
		ui.close(cfg);
		
		//log into communities as the community member
		log.info("INFO: Log into communities as the community member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//open the test community
		log.info("INFO: Open the test community");
		community.navViaUUID(ui);
			
		//Open the Community Actions menu & click on the Mail Community link
		log.info("INFO: Open the Community Actions menu & click on the Mail Community link");
		Com_Action_Menu.MAILCOMMUNITY.select(ui);
		
		//Switch focus to the Email the Community dialog
		log.info("INFO: Switch focus to the Email the Community dialog");	
		String originalWindow = driver.getWindowHandle();
		driver.switchToFirstMatchingWindowByPageTitle("Email the Community");
		ui.fluentWaitPresent(CommunitiesUICloud.CKEditor_iFrame_Text);		
				
		//verify the radio button 'Owners and Members' displays on the dialog box
		log.info("INFO: Verify the 'Owners and Members' radio button displays on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.emailCommDialogAllMembersRadioBtn),
				"ERROR: The radio button for Owners and Members does not appear, but should");
		
		//verify the radio button 'Owners Only' displays
		log.info("INFO: Verify the 'Owners Only' radio button displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.emailCommDialogOwnersOnlyRadioBtn),
				"ERROR: The radio button for Owners Only does not appear, but should");	
		
		//Click on the Cancel button to exit the Email the Community dialog
		log.info("INFO: Click on the Cancel button to close the Email the Community dialog");
		ui.clickLinkWait(CommunitiesUIConstants.emailCommDialogCancelBtn);
		
		// Return the to original window
		driver.switchToWindowByHandle(originalWindow);
		
		//log out of communities as the community member
		log.info("INFO: Log out of communities as the community member");
		ui.logout();
		ui.close(cfg);

		//log into communities as the community creator
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//Cleanup: Delete community

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();	
		
	}
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> Email Privileges (Test 2): This test covers the option to mail Owners only</li>
	 * <li><B>Step:</B> Create a community with a member using the API</li>
	 * <li><B>Step:</B> Log into communities as the community creator</li>
	 * <li><B>Step:</B> Navigate to the Edit Community form</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Step:</B> Select the radio button 'Members can email owners only'</li>
	 * <li><B>Step:</B> Click on the Save button to save the change
	 * <li><B>Step:</B> Logout as the community creator</li>
	 * <li><B>Step:</B> Login as the community member</li>
	 * <li><B>Step:</B> Open the community & click on Community Actions</li>
	 * <li><B>Step:</B> Click on the Mail Community link
	 * <li><B>Verify:</B> The radio button 'Owners and Members' does NOT appear on the Email the Community dialog</li>
	 * <li><B>Verify:</B> The radio button 'Owners Only' appears on the Email the Community dialog</li>
	 * <li><B>Step:</B> Logout as the community member </li>
	 * <li><B>Step:</B> Login as the community creator
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void mailCommunityOwnersOnly(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test the 'Members can email Owners only' radio button " + testName + rndNum)
												   .addMember(member)
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//get the community UUID
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI
		//Load component and login
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//select Community Actions menu option Edit Community
		log.info("INFO: Select Edit Community on the community action menu");
		Com_Action_Menu.EDIT.select(ui);
		ui.waitForPageLoaded(driver);
		
		//click on the link Access Advanced Features to see the Email Privileges
		log.info("INFO: Click on the link Access Advanced Features to see the Email Privileges");
		driver.getSingleElement(CommunitiesUIConstants.comAdvancedLink).click();
		
		//Select the radio button "Members can email owners only"
		log.info("INFO: Select the radio button 'Members can email owners only'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.communitiesMailOwnersOnlyRadioBtn).click();

		//click on the Save button				
		log.info("INFO: Click the Save button to save the change");
		driver.getFirstElement(CommunitiesUIConstants.editCommunitySaveButton).click();

		//log out of communities as the community creator
		log.info("INFO: Log out of communities as the community creator");
		ui.logout();
		ui.close(cfg);

		//log into communities as the community member
		log.info("INFO: Log into communities as the community member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//open the test community
		log.info("INFO: Open the test community");
		community.navViaUUID(ui);

		//Open the Community Actions menu & click on the Mail Community link
		log.info("INFO: Open the Community Actions menu & click on the Mail Community link");
		Com_Action_Menu.MAILCOMMUNITY.select(ui);

		//Switch the focus to the Email the Community dialog
		log.info("INFO: Switch the focus to the Email the Community dialog");			
		String originalWindow = driver.getWindowHandle();
		driver.switchToFirstMatchingWindowByPageTitle("Email the Community");
		ui.fluentWaitPresent(CommunitiesUICloud.CKEditor_iFrame_Text);	

		//verify the radio button 'Owners and Members' does NOT display on the dialog box
		log.info("INFO: Verify the 'Owners and Members' radio button does NOT display on the dialog box");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.emailCommDialogAllMembersRadioBtn),
				"ERROR: The radio button for Owners and Members appears, but should not");

		//verify the radio button 'Owners Only' displays
		log.info("INFO: Verify the 'Owners Only' radio button displays on the dialog box");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.emailCommDialogOwnersOnlyRadioBtn),
				"ERROR: The radio button for Owners Only does not appear, but should");	
		
		//Click on the Cancel button to exit the Email the Community dialog
		log.info("INFO: Click on the Cancel button to close the Email the Community dialog");
		ui.clickLinkWait(CommunitiesUIConstants.emailCommDialogCancelBtn);

		// Return the to original window
		driver.switchToWindowByHandle(originalWindow);

		//log out of communities as the community member
		log.info("INFO: Log out of communities as the community member");
		ui.logout();
		ui.close(cfg);

		//log into communities as the community creator
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Cleanup: Delete community

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();	

	}

	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> Email Privileges (Test 3): This test covers the option where a member cannot email others</li>
	 * <li><B>Step:</B> Create a community with a member using the API</li>
	 * <li><B>Step:</B> Log into communities as the community creator</li>
	 * <li><B>Step:</B> Navigate to the Edit Community form</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Step:</B> Select the radio button 'Members cannot email others'</li>
	 * <li><B>Step:</B> Click on the Save button to save the change
	 * <li><B>Step:</B> Logout as the community creator</li>
	 * <li><B>Step:</B> Login as the community member</li>
	 * <li><B>Step:</B> Open the community & click on Community Actions</li>
	 * <li><B>Verify:</B> The 'Mail Community' link does not appear on the community actions menu</li>
	 * <li><B>Step:</B> Logout as the community member </li>
	 * <li><B>Step:</B> Login as the community creator
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
		
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void mailCommunityCannotMailOthers(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test the 'Members cannot email others' radio button " + testName + rndNum)
												   .addMember(member)
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//get the community UUID
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//GUI
		//Load component and login
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//select Community Actions menu option Edit Community
		log.info("INFO: Select Edit Community on the community action menu");
		Com_Action_Menu.EDIT.select(ui);
		ui.waitForPageLoaded(driver);
		
		//click on the link Access Advanced Features
		log.info("INFO: Click on the link Access Advanced Features");
		driver.getSingleElement(CommunitiesUIConstants.comAdvancedLink).click();
		
		//Select the radio button "Members cannot email others"
		log.info("INFO: Select the radio button 'Members cannot email others'");
		ui.getFirstVisibleElement(CommunitiesUIConstants.communitiesMailNoOneRadioBtn).click();

		//Click on the Save button to save the change				
		log.info("INFO: Click the Save button to save the change");
		driver.getFirstElement(CommunitiesUIConstants.editCommunitySaveButton).click();

		//log out of communities as the community creator
		log.info("INFO: Log out of communities as the community creator");
		ui.logout();
		ui.close(cfg);

		//log into communities as the community member
		log.info("INFO: Log into communities as the community member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		//open the test community
		log.info("INFO: Open the test community");
		community.navViaUUID(ui);

		//click on the Community Actions link
		log.info("INFO: Click on the Community Actions link");
		ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		//Verify the Mail Community link does not appear on the drop-down menu
		log.info("INFO: Verify the Mail Community link does not appear on the drop-down menu");
		Assert.assertFalse(driver.isTextPresent(Com_Action_Menu.MAILCOMMUNITY.getMenuItemText()),
				"ERROR: The Mail Community appears on the drop-down menu, but should not");								

		//log out of communities as the community member
		log.info("INFO: Log out of communities as the community member");
		ui.logout();
		ui.close(cfg);

		//log into communities as the community creator
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Cleanup: Delete community

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();	

	}		
	
	/**
	 * <ul>
	 * <li><B>Info:</B> This test will verify that clicking a default widget link on the left nav bar will bring up the correct page</li>
	 * <li><B>Step:</B> Create a community using the API</li>
	 * <li><B>Info:</B> Default widgets: Recent Updates, Status Updates, Members, Files, Wikis (SC only), Forums, Bookmarks, Overview</li>
	 * <li><B>Step:</B> From the community Overview page, expand the left nav bar and click on one of the added widgets</li>
	 * <li><B>Verify:</B> The full widget page displays</li>
	 * <li><B>Step:</B> Repeat above step & verification for each of the added apps.</li>
	 * <li><B>Cleanup:</B> Delete community</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void leftNavFullAppsPageDefaultWidgets() {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String product = cfg.getProductName();
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)
												   .description("Left nav bar - full widget page test for widgets added by default " + testName)
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
		
		log.info("INFO: Check to see if Tabbed Nav gatekeeper setting is enabled, if yes - change layout to 3 columns with side menu");
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Click on Community Actions");
			ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

			log.info("INFO: Click on the Change Layout link");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

			log.info("INFO: Select '3 Columns with side menu' from the layout palette");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithSideMenu);

		}
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Recent Updates link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavRecentUpdates);

		log.info("INFO: Verify the Recent Updates full page displays - check for the 'Recent Updates' header");
		Assert.assertTrue(driver.isTextPresent(Data.getData().appRecentUpdates),
				"ERROR: The full Recent Updates page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Status Updates link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavStatusUpdates);

		log.info("INFO: Verify the Status Updates full page displays - check for the 'Status Updates' header");
		Assert.assertTrue(driver.isTextPresent(Data.getData().appStatusUpdates),
				"ERROR: The full Status Updates page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Members link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavMembers);

		log.info("INFO: Verify the Members full page displays - check for the 'Add Members' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddMembersToExistingCommunity),
				"ERROR: The full Members page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Forums link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavForums);

		log.info("INFO: Verify the Forums full page displays - check for the 'Start a Topic' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.StartATopicLink),
				"ERROR: The full Forums page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Bookmarks link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavBookmarks);

		log.info("INFO: Verify the Bookmarks full page displays - check for the 'Add Bookmark' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AddBookmarkButton),
				"ERROR: The full Bookmarks page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Files link");
		ui.clickLinkWait(CommunitiesUIConstants.tabbedNavFilesTab);

		log.info("INFO: Verify the Files full page displays - check for the 'All Community Files' page title");
		Assert.assertTrue(driver.isTextPresent(Data.getData().CommFilesFullWidgetPageTitle),
				"ERROR: The full Files page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Overview link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);

		log.info("INFO: Verify the Overview page displays - check for the 'Communities' link");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunitiesLink),
				"ERROR: The Overview page did not display");

		//determine of SC or OP				
		if(product.equalsIgnoreCase("cloud")){
			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			log.info("INFO: Click on Wiki link");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavWikis);

			log.info("INFO: Verify the Wiki page displays - check for the 'Page Actions' button");
			Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button),
					"ERROR: The Wiki full page did not display");

		}	
		log.info("INFO: Removing community for Test case ");
		community.delete(ui, testUser);

		ui.endTest();	

	}

	/**
	 * <ul>
	 * <li><B>Info:</B> This test will verify the full page displays when the app link is selected from the left navigation bar</li>
	 * <li><B>Info:</B> NOTE: not all apps added will appear on the left nav bar.  This test will only cover apps (from Add Apps palette) that appear on the left nav</li>
	 * <li><B>Step:</B> Create a community & add widgets using the API</li> 
	 * <li><B>Info:</B> Widgets added via API: Blogs, Ideation Blog, Activities, Events, Wiki, Related Communities, Feeds(on-prem only), and Survey (cloud only)</li>
	 * <li><B>Step:</B> From the community Overview page, expand the left nav bar and click on one of the added widgets</li>
	 * <li><B>Verify:</B> The full widget page displays</li>
	 * <li><B>Step:</B> Repeat above step & verification for each of the added apps.</li>
	 * <li><B>Cleanup:</B> Delete community</li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void leftNavFullAppsPageAddAppsWidgets(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                           .access(Access.PUBLIC)
		                                           .description("Left nav bar - full widget page test for widgets added via Add Apps palette " + testName)
		                                           .build();

		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);	

		log.info("INFO: Add Blog widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);

		log.info("INFO: Add Ideation Blog widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);

		log.info("INFO: Add Activities widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);

		log.info("INFO: Add Events widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		log.info("INFO: Add Related Communities widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.RELATED_COMMUNITIES);

		//determine of SC or OP				
		if(!isOnPremise){
			log.info("INFO: Add Surveys widget using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.SURVEYS);

		}else{
			log.info("INFO: Add Feeds widget using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FEEDS);	

			log.info("INFO: Add Wiki widget using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);
		}
        log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Click on Community Actions");
			ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

			log.info("INFO: Click on the Change Layout link");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayoutLink);

			log.info("INFO: Select '3 Columns with side menu' from the layout palette");
			ui.clickLinkWait(CommunitiesUIConstants.communityChangeLayout3ColumnsWithSideMenu);

		}

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Blogs link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavBlogs);

		log.info("INFO: Verify the Blogs full page displays - check for 'Feeds for Blog Entries' link");
		Assert.assertTrue(driver.isTextPresent(Data.getData().feedsForBlogEntries),
				"ERROR: The full Blogs page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Ideation Blogs link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavIdeationBlogs);

		log.info("INFO: Verify the Ideation Blog full page displays - check for the 'Contribute an Idea' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.Ideation_ContributeAnIdea),
				"ERROR: The full Ideation Blog page did not display");

		log.info("INFO: Click on the Activities link on the left nav bar");
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Activities link");	
		ui.clickLinkWait(CommunitiesUIConstants.tabbedNavActivitiesTab);

		log.info("INFO: Verify the Activities full page displays - check for 'Create Your First Activity' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.createActivButton),
				"ERROR: The full Activities page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Events link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavEvents);

		log.info("INFO: Verify the Events full page displays - check for 'Create an Event' button");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.Event_EventCreateButton),
				"ERROR: The full Events page did not display");		
		
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Wikis link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavWikis);

		log.info("INFO: Verify the Wiki page displays - check for the 'Page Actions' button");
		Assert.assertTrue(driver.isElementPresent(WikisUIConstants.Page_Actions_Button),
				"ERROR: The Wiki full page did not display");

		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		log.info("INFO: Click on Related Communities link");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavRelatedComm);

		log.info("INFO: Verify the Related Communities page displays - check for the 'Add a Community' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.addRelatedCommBtn),
				"ERROR: The Related Communities full page did not display");

		//determine of SC or OP				
		if(!isOnPremise){

			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			log.info("INFO: Click on Surveys link");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavSurveys);

			log.info("INFO: Verify the Surveys page displays - check for 'Create Survey' button");
			Assert.assertTrue(driver.isElementPresent(SurveysUI.createSurveyButton),				
					"ERROR: The Surveys full page did not display");

		}	else{
			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			log.info("INFO: Click on Feeds link");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavFeeds);

			log.info("INFO: Verify the Feeds page displays");
			Assert.assertTrue(driver.isElementPresent(FeedsUI.AddFeedLink),
					"ERROR: The Feeds full page did not display");
		}
		log.info("INFO: Removing community for Test case ");
		community.delete(ui, testUser);

		ui.endTest();	

	}
	

	
}
