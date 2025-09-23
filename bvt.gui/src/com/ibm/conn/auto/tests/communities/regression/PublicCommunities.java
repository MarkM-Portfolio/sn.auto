package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.testng.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;

public class PublicCommunities extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(PublicCommunities.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private BlogsUI bUI;
	private User testUser1, testUser2, testUser3;
	private APICommunitiesHandler apiOwner;
	private boolean isOnPremise;
	private String serverURL;
	
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	
		//check to see if environment is on-premises or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}	
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setup()throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		
	}

	/**
	 *<ul>
	 *<li>PTC_Create_PublicCommunity</li>
	 *<li><B>Test Scenario:</B> Actions in Catalog View: Verify creating Public Communities (1 of 4)</li>
	 *<li><B>Info:</B> Test will verify that the community creator is able to see the public community in the I'm an Owner view</li>
	 *<li><B>Step:</B> Create a public community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the Public community is listed in the I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon or static text should not display  for public community in I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon or static text should not display for public community in I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the External Icon or static text should not display for public community in I'm an Owner view</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/414C5CBE3E72A5AF85257CAD00667099">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING PUBLIC COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityOwnerView() throws Exception {

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand() )
												   .description("Test community for " + testName )
												   .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
											
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
			
		//GUI
		//Load component and login as the community creator
		log.info("INFO: Loading Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//Navigate to the I'm an Owner view
		log.info("INFO: Clicking on the I'm an Owner link on the left nav. bar");
		ui.goToOwnerView(isCardView);
				
		//Verify the public community appears in the I'm an Owner view
		ui.fluentWaitPresentWithRefresh(communityLink);
		log.info("INFO: Verify the public community appears in the I'm an Owner view");
		Assert.assertTrue(driver.isElementPresent(communityLink),
							"ERROR: Public community does not appear in the I'm an Owner view");
	
		// Verify that NO icons appear on the Community
		verifyIconsOnPublicCommunity(community,isCardView );
		
		//Click on community link
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		
		//Cleanup: Delete community
		log.info("INFO: Removing community " + testName);
		//community.delete(ui, testUser1);
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();			
	}
	
	
	/**
	 *<ul>
	 *<li>PTC_Create_PublicCommunity</li>
	 *<li><B>Test Scenario:</B> Actions in Catalog View: Verify creating Public Communities (2 of 4)</li>
	 *<li><B>Info:</B> Test will verify that the community creator is able to see the public community in the I'm a Member view</li>
	 *<li><B>Step:</B> Create a public community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm a Member or My Communities (CR3)view</li>
	 *<li><B>Verify:</B> Verify the Public community is listed in the I'm a Member or My Communities (CR3) view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon or static text should not display  for public community in I'm a Member view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon or static text should not display for public community in I'm a Member view</li>
	 *<li><B>Verify:</B> Verify the External Icon or static text should not display for public community in I'm a Member view</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/414C5CBE3E72A5AF85257CAD00667099">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING PUBLIC COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityMemberView() throws Exception {

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand() )
												   .description("Test community for " + testName )
												   .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
													
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
												
		//GUI
		//Load component and login as the community creator
		log.info("INFO: Loading Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
        
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//go to default Community view i.e I'm a Member or My Communities (CR3)
		ui.goToDefaultCatalogView();
		
		//Verify the public community appears in the default Community view
		ui.fluentWaitPresentWithRefresh(communityLink);
		log.info("INFO: Verify the public community appears in the default community view");
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Public community does not appear in the default community view");
		
		// Verify that NO icons appear on the Community
		verifyIconsOnPublicCommunity(community,isCardView );
		
		//Click on community link
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//Cleanup: Delete community
		log.info("INFO: Removing community " + testName );
		community.delete(ui, testUser1);

		ui.endTest();		
	}
	
	
	
	/**
	 *<ul>
	 *<li>PTC_Create_PublicCommunity</li>
	 *<li><B>Test Scenario:</B> Actions in Catalog View: Verify creating Public Communities (3 of 4)</li>
	 *<li><B>Info:</B> Test will verify that the community creator is able to see the public community in the Public Communities view</li>
	 *<li><B>Step:</B> Create a public community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the Public Communities view</li>
	 *<li><B>Verify:</B> Verify the Public community is listed in the Public Communities view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon or static text should not display  for public community in Public Communities view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon or static text should not display for public community in Public Communities view</li>
	 *<li><B>Verify:</B> Verify the External Icon or static text should not display for public community in Public Communities view</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/414C5CBE3E72A5AF85257CAD00667099">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING PUBLIC COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityPublicView() throws Exception {

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand() )
												   .description("Test community for " + testName )
												   .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
															
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
																
		//GUI
		//Load component and login as the community creator
		log.info("INFO: Loading Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Navigate to the Public Communities view
		log.info("INFO: Clicking on the Public link from the left nav. bar");
		ui.goToPublicView(isCardView);
		
		ui.applyCatalogFilter(community.getName(), isCardView);
			
		//Verify public community is present in the Public Communities view
		log.info("INFO: Verify public community is present in the Public Communities view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
						  "ERROR: Public community does not appear in the Public Communities view");
		
		// Verify that NO icons appear on the Community
		verifyIconsOnPublicCommunity(community,isCardView );
		
		//Click on community link
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//Cleanup: Delete community
		log.info("INFO: Removing community " + testName );
		community.delete(ui, testUser1);

		ui.endTest();		
	}
	
	
	/**
	 *<ul>
	 *<li>PTC_Create_PublicCommunity</li>
	 *<li><B>Test Scenario:</B> Actions in Catalog View: Verify creating Public Communities (4 of 4)</li>
	 *<li><B>Info:</B> Test will verify that the community creator is able to see the public community in the I'm Following view</li>
	 *<li><B>Step:</B> Create a public community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm Following view</li>
	 *<li><B>Verify:</B> Verify the Public community is listed in the I'm Following view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon or static text should not display  for public community in I'm Following view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon or static text should not display for public community in I'm Following view</li>
	 *<li><B>Verify:</B> Verify the External Icon or static text should not display for public community in I'm Following view</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/414C5CBE3E72A5AF85257CAD00667099">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING PUBLIC COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityFollowingView() throws Exception {

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .description("Test community for " + testName )
												   .build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
																	
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
			
		//GUI
		//Load component and login as the community creator
		log.info("INFO: Loading Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Click on I'm Following view left Nav. bar
		log.info("INFO: Clicking on the I'm Following link from the left nav. bar");
		ui.goToIamFollowingView(isCardView, isOnPremise);
		
		//Verify public community is present in I'm Following view
		log.info("INFO: Verify public community is present in I'm Following view");	
		ui.fluentWaitPresentWithRefresh(communityLink);		
		Assert.assertTrue(driver.isElementPresent(communityLink),
						   "ERROR: Public community does not appear in the I'm Following view");
		
		// Verify that NO icons appear on the Community
		verifyIconsOnPublicCommunity(community,isCardView );
		
		//Click on community link
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//Cleanup: Delete community
		log.info("INFO: Removing community " + testName );
		community.delete(ui, testUser1);

		ui.endTest();
	}

	
	/**
	 *<ul>
	 *<li>PTC_DeleteCommunityAndTrashTest</li>
	 *<li><B>Test Scenario:</B> Community Actions: Delete community and trash test (1 of 3)</li>
	 *<li><B>Info:</B> Verify an additional owner can delete & restore a community</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as Additional owner</li>
	 *<li><B>Verify:</B> Verify the user is able to see the community in I'm an Owner view</li>
	 *<li><B>Step:</B> Delete the community</li>
	 *<li><B>Step:</B> Go to the Trash view</li> 
	 *<li><B>Verify:</B> Verify the delete community is listed in the Trash view</li>
	 *<li><B>Step:</B> Restore the community</li>
	 *<li><B>Verify:</B> Verify the message indicating the community was restored displays</li>
	 *<li><B>Verify:</B> Verify that the community name is an active link</li>
	 *<li><B>Verify:</B> Verify the community appears in the I'm an Owner view</li>
	 *<li><B>Step:</B> Cleanup by deleting the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF52283B172A067685257C8D006D3011">TTT-COMMUNITY ACTIONS: DELETE COMMUNITY & TRASH TEST</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void additionalOwnerDeleteAndRestoreCommunity() throws Exception {

		String testName = ui.startTest();
		Element widget;
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description("Test Start Page for community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.addMember(new Member(CommunityRole.OWNERS, testUser3))
													.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//Load component and login as Additional owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser3);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//Navigate to the I'm an Owner view
		log.info("INFO: Clicking on the I'm an Owner link on the left nav. bar");
		ui.goToOwnerView(isCardView);
			

		//If environment is Cloud & the catalog UI GK flag is enabled then click on the sort by 'Date' tab
		//Content on cloud does not get cleared, community may not appear when sorted by Recently Visited due to all the communities
		//Community is easily found by clicking on Date
		if (!isOnPremise){
			if(getCatalogUIUpdatedGK()){
				log.info("INFO: Click the view sort by option 'Date'");
				ui.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
			}
		}

		//Verify the user is able to see the community in I'm an Owner view
		log.info("INFO: Verify the user is able to see the community in I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				  "ERROR : Additional Owner is not able to see his community under I'm an Owner view");

		//Click on community link
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//delete community as additional user
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser3);
		
		//Navigate to Trash
		log.info("INFO: Go to the Public View");
		ui.goToTrashView(isCardView);
		
		//Verify the user is able to see the community in Trash view
		log.info("Verify the user is able to see the coomunity in Trash view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				          "ERROR : Additional Owner is not able to see his community under Trash View!");
		
		//Restore the community
		
		log.info("Click on Restore link for the community");
		if(isCardView) {
			
			//Click on Restore link
			ui.clickLinkWait(ui.getCommunityRestoreButtonLink(community));
			
			//Verify the message displays that the community was restored
			log.info("Verify the message displays that the community was restored");
			
			// wait for success message to display
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.SuccessMessageAfterCommunityCardRestore),
					"ERROR: The restore community SUCCESS message is not shown");
			
			// verify the message text is correct
			Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityCardRestore).getText(), "The community "+community.getName() +" was restored.",
		                   "ERROR: The restore community message is not correct");
			
		}
		else {
			
			//Get community widget
			log.info("INFO: Get community widget from view");
			widget = ui.getCommunityWidget(community.getName());
			
			//Click on Restore link
			log.info("Click on Restore link for the community");
			widget.getSingleElement(CommunitiesUIConstants.RestoreCommunityFromTrash).click();
		
			//Verify the message displays that the community was restored
			log.info("Verify the message displays that the community was restored");
			Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityRestore).getText(), "The community "+community.getName() +" was restored.",
		                   "ERROR: The restore community message is not shown");
		
			//Verify that the community name is an active link
			log.info("Verify that the community name is an active link");
			Assert.assertTrue(driver.isElementPresent(communityLink),
							"ERROR: Community name is not an active link");
		}
		
		//view the community in I'm an owner view
		ui.goToOwnerView(isCardView);
		
		//Community is present in I'm an Owner view
		log.info("INFO: Verify the user is able to see the community in I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresent(communityLink),
						  "ERROR : Additional Owner is not able to see his community under I'm an Owner!");
			
		//If environment is Cloud & the catalog UI GK flag is enabled then click on the sort by 'Date' tab
		//Content on cloud does not get cleared, community may not appear when sorted by Recently Visited due to all the communities
		//Community is easily found by clicking on Date
		if (!isOnPremise){
			if(getCatalogUIUpdatedGK()){
				log.info("INFO: Click the view sort by option 'Date'");
				ui.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
			}
		}
		
		//Click on community link
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		//delete community as additional user
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser3);
		
		ui.endTest();
	}

	
	/**
	 *<ul>
	 *<li>PTC_DeleteCommunityAndTrashTest</li>
	 *<li><B>Test Scenario:</B> Community Actions: Delete & Restore community as community creator (2 of 3)</li>
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
	 **<li><B>Step:</B> Cleanup by deleting the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF52283B172A067685257C8D006D3011">TTT-COMMUNITY ACTIONS: DELETE COMMUNITY & TRASH TEST</a></li>
     *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void ownerDeleteAndRestoreCommunity() throws Exception {

		String testName = ui.startTest();
		
		Element widget;
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description("Test Start Page for community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.addMember(new Member(CommunityRole.OWNERS, testUser3))
													.build();
				

		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//Navigate to the community using API
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Community creator deletes the community
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser1);
		
		//Navigate to the Trash view
		log.info("INFO: Click on Trash view");
		ui.goToTrashView(isCardView);
		
		//Verify the community is listed in the Trash view
		log.info("INFO: Verify community is listed in the Trash view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
		          "ERROR: Owner is not able to see his community under Trash View!");

		//Restore the community
		
		
		if(isCardView) {
			//Click on Restore link
			ui.clickLinkWait(ui.getCommunityRestoreButtonLink(community));
			
			//Verify the message displays that the community was restored
			log.info("Verify the message displays that the community was restored");
			
			// wait for success message to display
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.SuccessMessageAfterCommunityCardRestore),
					"ERROR: The restore community SUCCESS message is not shown");
			
			// verify the message text is correct
			Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityCardRestore).getText(), "The community "+community.getName() +" was restored.",
		                   "ERROR: The restore community message is not correct");
		
		}
		else {
			
		   //Get community widget
		   log.info("INFO: Get community widget from view");
		   widget = ui.getCommunityWidget(community.getName());
			
		   //Click on Restore link
		   log.info("Click on Restore link for the community");
		   widget.getSingleElement(CommunitiesUIConstants.RestoreCommunityFromTrash).click();
		
		   //Verify the community was restored message displays
		   log.info("Verify the message displays that the community was restored");
		   Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityRestore).getText(),"The community "+community.getName() +" was restored.",
				"ERROR: The community was restored message is not shown");
		
		   //Community name is an active link
		   log.info("Verify that the community name is an active link");
		   Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getCommunityLink(community)),
							"ERROR: Community name is not an active link");
		}
		
		//Navigate to the I'm an Owner view		
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		//Verify the community is listed in the I'm an Owner view
		log.info("INFO: Verify the user is able to see the community in I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresent(communityLink),
				"ERROR : Owner is not able to see his community under I'm an Owner!");
		        		
		//Open the community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
					
		//Cleanup: delete community
		log.info("INFO: Cleanup: Removing community for Test case " + testName );
		community.delete(ui, testUser1);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li>PTC_DeleteCommunityAndTrashTest</li>
	 *<li><B>Test Scenario:</B> Community Actions: Delete community and trash test (3 of 3)</li>
	 *<li><B>Info:</B> Test to make sure a member cannot see a community that has been deleted</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as a member of the community</li>
	 *<li><B>Verify:</B> Validate the user is able to see the community in I'm member view</li>
	 *<li><B>Step:</B> Logout and log back in as a additional owner</li>
	 *<li><B>Step:</B> Delete the community</li>
	 *<li><B>Step:</B> Logout and log back in as a member of the deleted community</li>
	 *<li><B>Step:</B> Navigate to the trash view</li>
	 *<li><B>Verify:</B> Validate that the community is not visible in the trash view</li>
	 *<li><B>Verify:</B> Validate the member do not see the community in I'm member view</li>
	 *<li><B>Step:</B> Logout and log back in as a additional owner of the deleted community</li>
	 *<li><B>Step:</B> Restore the community</li>
	 *<li><B>Step:</B> Logout and log back in as a member of restored community</li>
	 *<li><B>Verify:</B> Validate the user is able to see the community in I'm member view</li>
	 *<li><B>Step:</B> Login as a Owner and Cleanup by deleting the community</li>
     *<li> <li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EF52283B172A067685257C8D006D3011">TTT-COMMUNITY ACTIONS: DELETE COMMUNITY & TRASH TEST</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void memberDeleteRestoreCommunity() throws Exception {
		
		String testName = ui.startTest();
		Element widget;
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.description("Test Start Page for community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.addMember(new Member(CommunityRole.OWNERS, testUser3))
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//Load component and login as member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Navigate to the I'm a Member view
		log.info("INFO: Clicking on the I'm a Member link on the left nav. bar");
		ui.goToMemberView(isCardView);
				
		//Verify the user is able to see the community in I'm member view
		log.info("INFO: Verify the user is able to see the community in I'm member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				  "ERROR : Additional Member is not able to see his community under I'm member view");
		
		//logout member
		ui.logout();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//delete community as owner user
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser3);
		
		//navigate to the trash view and validate community is present
		log.info("INFO: navigate to trash");
		ui.goToTrashView(isCardView);
		
		//Verify the community is listed in the Trash view
		log.info("INFO: Verify community is listed in the Trash view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
		          "ERROR: additional Owner is not able to see his community under Trash View!");

		//logout owner
		ui.logout();
		
		//Load component and login as member
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);
		
		//navigate to the trash view and validate community is present
		log.info("INFO: navigate to trash");
		ui.goToTrashView(isCardView);
		
		//validate that we do not see the community to be restored in trash
		log.info("INFO: Validate the community is not present in the trash");
		Assert.assertFalse(driver.isElementPresent(communityLink),
				          "ERROR: Member is able to see the community under Trash View!");
		
		//Navigate to the I'm a Member view
		log.info("INFO: Clicking on the I'm a Member link on the left nav. bar");
		ui.goToMemberView(isCardView);
				
		//Verify the member do not see the community in I'm member view
		log.info("INFO: Verify the member do not see the community in I'm member view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
				  "ERROR : Additional Member is able to see his community under I'm member view");
			
		//logout member
		ui.logout();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		//Navigate to the Trash view
		log.info("INFO: Click on Trash view");
		ui.goToTrashView(isCardView);
		
		//Verify the community is listed in the Trash view
		log.info("INFO: Verify community is listed in the Trash view");
		Assert.assertTrue(ui.fluentWaitPresent(communityLink),
		          "ERROR: additional Owner is not able to see his community under Trash View!");

		//Restore the community
		
		if(isCardView) {
			
			//Click on Restore link
			ui.clickLinkWait(ui.getCommunityRestoreButtonLink(community));
			
			//Verify the message displays that the community was restored
			log.info("Verify the message displays that the community was restored");
			
			// wait for success message to display
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.SuccessMessageAfterCommunityCardRestore),
					"ERROR: The restore community SUCCESS message is not shown");
			
			// verify the message text is correct
			Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityCardRestore).getText(), "The community "+community.getName() +" was restored.",
		                   "ERROR: The restore community message is not correct");
			
		
		}
		else {
			
			//Get community widget
			log.info("INFO: Get community widget from view");
			widget = ui.getCommunityWidget(community.getName());
			
		   //Click on Restore link
		   log.info("Click on Restore link for the community");
		   widget.getSingleElement(CommunitiesUIConstants.RestoreCommunityFromTrash).click();
			
		   //Verify the community was restored message displays
		   log.info("Verify the message displays that the community was restored");
		   Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.MessageAfterCommunityRestore).getText(),"The community "+community.getName() +" was restored.",
				"ERROR: The community was restored message is not shown");
		
		   log.info("Verify that the community name is an active link");
		   Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getCommunityLink(community)),
							"ERROR: Community name is not an active link");
		}
		
		//logout owner
		ui.logout();
		
		//Load component and login as member
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);
		
		//Navigate to the I'm a Member view
		log.info("INFO: Clicking on the I'm a Member link on the left nav. bar");
		ui.goToMemberView(isCardView);
		
		//Verify the user is able to see the community in I'm member view
		log.info("INFO: Verify the user is able to see the community in I'm member view");
		Assert.assertTrue(driver.isElementPresent(communityLink),
				  "ERROR : Additional Member is not able to see his community under I'm member view");
		
		//logout member
		ui.logout();
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser1);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
			
		//Cleanup: delete community
		log.info("INFO: Cleanup: Removing community for Test case " + testName );
		community.delete(ui, testUser1);
		
		ui.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li>PTC_FollowUnfollowCommunity</li>
	 *<li><B>Test Scenario:</B> Community Actions: Follow and Un-follow a community</li>
	 *<li><B>Info:</B> Verify the ability to follow and un-follow a community</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add one Member to the community</li>
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
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/45733D195DCBA23585257C900063A360">TTT-COMMUNITY ACTIONS: FOLLOW & UNFOLLOW COMMUNITY</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression", "regressioncloud"})
	public void memberFollowUnfollowCommunity() {

			String testName = ui.startTest();
		//	String product = cfg.getProductName();
					
			//Create a community base state object
			BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
														.access(Access.PUBLIC)	
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														.description("Test Start Page for community " + testName)
														.addMember(new Member(CommunityRole.MEMBERS, testUser2))
														.build();

			log.info("INFO: Create community using API");
			Community commAPI =  community.createAPI(apiOwner);
			
			//Get the community UUID
			log.info("INFO: Get UUID of community");
			community.getCommunityUUID_API(apiOwner, commAPI);
			
			log.info("INFO: Log into Communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser2);
		
			// get the Catalog Card View GK flag
			boolean isCardView = getCatalogCardGK();
					
			// get the community link
			String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			
			log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
			ui.goToIamFollowingView(isCardView, isOnPremise);
			
			log.info("INFO: Verify Member should not see the community in I'm Following!");
			driver.turnOffImplicitWaits();
			Assert.assertFalse(driver.isElementPresent(communityLink),
					"ERROR : Member is able to see the community in I'm Following!!");
			driver.turnOnImplicitWaits();
			
			//Navigate to the Public Communities view
			log.info("INFO: Go to the Public View");
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
			
			log.info("INFO: Verify that the following message appears in  a green banner near the top of the page");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().FollowCommunityMsg),
								"ERROR: You are following this community and will receive updates about community content message is not displayed");
			
			
			if(!isOnPremise){
				log.info("INFO: The environment is Smart Cloud");
				
				log.info("INFO: Click on the Communities link in the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				
				log.info("INFO: Navigate to the I'm Following view");
				Community_View_Menu.IM_FOLLOWING.select(ui);

			}else{
				log.info("INFO: The environment is on-premise.");

				log.info("INFO: Click on the Communities link in the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				
				if(getCatalogUIUpdatedGK()){	
					
				   log.info("INFO: Click on the My Communities link");
				   ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
				   
				   log.info("INFO: Clicking on the I'm Following view");
				   ui.goToIamFollowingView(isCardView, isOnPremise);
				}
				else {
				   log.info("INFO: Click on the I'm Following link");
				   ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuFollow);
				}
			};						
							
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
			
			log.info("INFO: Verify that the following message appears in  a green banner near the top of the page");
			Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().StopFollowingCommunityMsg),
								"ERROR: You have stopped following this community message is not displayed");
			
			
			if(!isOnPremise){
				log.info("INFO: The environment is Smart Cloud");
				
				log.info("INFO: Click on the Communities link in the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				
				log.info("INFO: Navigate to the I'm Following view");
				Community_View_Menu.IM_FOLLOWING.select(ui);	


			}else{
				log.info("INFO: The environment is on-prem.");

				log.info("INFO: Click on the Communities link in the mega-menu");
				ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
				
				log.info("INFO: Click on the My Communities link");
			    ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
				
				if(getCatalogUIUpdatedGK()){
				    ui.goToIamFollowingView(isCardView, isOnPremise);
				}
				else {
				    log.info("INFO: Click on the I'm Following link");
				    ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuFollow);
				}
			};	
				
			log.info("INFO: Verify Member should not see the community in I'm Following!");
			driver.turnOffImplicitWaits();
			Assert.assertFalse(driver.isElementPresent("link="+ community.getName()),
					"ERROR : Member is able to see the community in I'm Following!!");
			driver.turnOnImplicitWaits();

			log.info("INFO: Log out as the community Member");
			ui.logout();
			
			log.info("INFO: Log into Communities as the owner");
			ui.loadComponent(Data.getData().ComponentCommunities, true);
			ui.login(testUser1);
			
			ui.goToDefaultIamOwnerView(isCardView);
			
			log.info("INFO: Open community");
			ui.clickLinkWait(communityLink);
			
			log.info("INFO: Removing community for Test case " + testName );
			//community.delete(ui, testUser1);
			apiOwner.deleteCommunity(commAPI);
			
			ui.endTest();
		
		}
	
	
	/**
	 *<ul>
	 *<li>PTC_Recommendations</li>
	 *<li><B>Test Scenario:</B> General UI Verification (before login): Verify Recommendations widget appears to the right column in Community Catalog (1 of 2)</li>
	 *<li><B>Info:</B> Verify an anonymous user sees the Recommendations widget in the Public Communities view</li>
	 *<li><B>Step:</B> Navigate to the Public Communities view without logging in</li>
	 *<li><B>Verify:</B> Recommendations default message displays
	 *<li><B>Verify:</B> Recommendations widget appears</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3716D8C716177FB85257C8D0056CE29">TTT-GENERAL UI VERIFICATION: Verify Recommendations widget appears to the right column in Community Catalog</a></li>
	 *</ul>
	*/				
	@Test(groups = {"regression"})
	public void recommendationsBeforeLogin() throws Exception {
		
		ui.startTest();
	
		//Navigate to Public Communities view
		ui.loadComponent(Data.getData().publicCommunityURL);
		   
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
					
		if(!isCardView){
		   //Verify the default Recommendations message displays
		   log.info("INFO: Without logging into Connections ensure that 'Log in to view recommendations' text is present");;
		   Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.RecommendationLoginToSee).getText().contentEquals("Log in to view recommendations"),
						  "ERROR: 'Log in to view recommendations' message is not present");
											
		   //Verify the Recommendations widget displays
		   log.info("INFO: Validate Recommendations is present on the Screen");
		   Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.Recommendation),
						  "ERROR: Recommendations link is not present");
		}
		
				
		ui.endTest();
		
	}

	//TODO: Clean this up
	/**
	 *<ul>
	 *<li>PTC_Recommendations</li>
	 *<li><B>Test Scenario:</B> General UI Verification (after login): Verify Recommendations widget appears to the right column in Community Catalog (2 of 2)</li>
	 *<li><B>Info:</B> Verify sees the Recommendations widget after logging into communities</li>
	 *<li><B>Step:</B> Login to communities</li>
	 *<li><B>Verify:</B> Recommendations appears</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F3716D8C716177FB85257C8D0056CE29">TTT-GENERAL UI VERIFICATION: Verify Recommendations widget appears to the right column in Community Catalog</a></li>
	 *</ul>
	*/	
	@Test(groups = {"regression", "regressioncloud"})
	public void recommendationsAfterLogin() throws Exception {
		
		ui.startTest();
		
		//Load component and login
        ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		if(!getCatalogUIUpdatedGK()){
		   //Test Recommendations is present on the Screen
		   log.info("INFO: Validate Recommendations is present on the Screen");
		   Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.Recommendation),
						  "ERROR Recommendations link is not present");
		
		   log.info("INFO: Select the Recommendation actions menu");
		   ui.clickLinkWait(CommunitiesUIConstants.RecommendationMenu);
		
		}
		ui.endTest();
		
	}
	
	
	/**
	 *<ul>
	 *<li>PTC_Tags</li>
	 *<li><B>Test Scenario:</B> Verify Tags in a community</li> 
	 *<li><B>Info:</B> Verify tags for added applications in a community</li>
	 *<li><B>Step:</B> Create an restricted community as owner using API & add Blog as widget to it using API</li>
	 *<li><B>Step:</B> Login as owner & then open community
	 *<li><B>Step:</B> Open the community & add the Blogs app</li>
	 *<li><B>Step:</B> Add a Blog entry with tags</li>
	 *<li><B>Verify:</B> Tag entry is appearing under tag Cloud & List views</li>
	 *<li><B>Verify:</B> That when you click on the Tag you are taken to the Blog Entry</li>
	 *<li><B>Step:</B>Go to community overview page</li>
	 *<li><B>Verify:</B> Verify in Tags section, No tags are displayed</li>
	 *<li><B>Verify:</B> Verify Tags help Icon is displayed and correct help text is present</li>
	 *<li><B>Step:</B> Cleanup by deleting the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/956B833853B0942785257C8D00592602">TTT-TAGS: Verify Tags in Communities</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression", "regressioncloud"})
	public void communityBlogEntryTag() throws Exception {
	
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
									 			   .description("Test Widgets inside community for " + testName)
									 			   .access(Access.RESTRICTED)
									 			   .shareOutside(false)
									 			   .build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
		 										 .tags(Data.getData().commonAddress )
		 										 .content("Test description for testcase " + testName)
		 										 .build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add widget blog
		/*log.info("INFO: Add blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);*/
	
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		//Click on the blogs link in the nav
		log.info("INFO: Click on the Blog link ");
		Community_LeftNav_Menu.BLOG.select(bUI);

		//select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(bUI);
						
		//Test that Tag entry is appearing under Cloud.
		log.info("INFO: Test that Tag entry is appearing under Cloud.");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.TagInCloud).getText(), Data.getData().commonAddress.toLowerCase(),
				            "ERROR: Tag is not appearing under cloud ");
		
		//Click on the List Link
		log.info("INFO: Click on the List under Tags");
		bUI.clickLinkWithJavascript(CommunitiesUIConstants.ListUnderTag);
		
		// Test the tag entry is appearing in the List
		log.info("INFO: Test the tag entry is appearing in the List");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.TagInList).getText(), Data.getData().commonAddress.toLowerCase(),
				               "ERROR: Tag is not appearing under List ");
		
		//Click on the Tag entry
		log.info("INFO: Select the tag entry");
		ui.clickLinkWait(CommunitiesUIConstants.TagInList);
		
		//Test when a user clicks on the Tag he is taken to the Blog entry
		log.info("INFO: Test when a user clicks on the Tag he is taken to the Blog entry");
		Assert.assertTrue(driver.isElementPresent("link="+blogEntry.getTitle()),"ERROR: The blog entry associated with the tag is not visible");
		
		//Click on the Overview link in the left nav
		log.info("INFO: Click on the Overview link ");
		Community_LeftNav_Menu.OVERVIEW.select(bUI);
		
		//No tags are displayed
		log.info("INFO: Verify no tags are displayed for community");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.emptyTags),
							"ERROR: Tags are present for community");
		
		//Tags help is displayed
		log.info("INFO: Verify Tags help Icon is displayed");
		Assert.assertTrue(driver.getFirstElement(BaseUIConstants.tagsHelp).isVisible(),
							"ERROR: Tags help is not displayed");
		
		//Tags help message is displayed
		log.info("INFO: Hover over the Tags help Icon");
		driver.getFirstElement(BaseUIConstants.tagsHelp) .hover();
		
		//Tags help displays correct information
		log.info("INFO: Verify the Tags help displays correct information");
		Assert.assertEquals(driver.getFirstElement(BaseUIConstants.tagsHelpLauncher).getText().toLowerCase(), BaseUIConstants.tagsHelpMessage.toLowerCase(),
				"ERROR: Tags help is not displaying correct info");
		
		//delete community
		log.info("INFO: Removing community");
		//community.delete(ui, testUser1);
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Panasonic UAT: Join a Public Community</li>
	 *<li><B>Info:</B> This test will verify the ability for a user to join a public community</li>
	 *<li><B>Step:</B> Create a public community using the API</li>
	 *<li><B>Step:</B> Login as a user who is not a member of the community</li> 
	 *<li><B>Step:</B> Navigate to the public/<org> communities catalog view</li>
	 *<li><B>Step:</B> Open the public community</li>
	 *<li><B>Verify:</B> The Join this Community link appears on the Overview page
	 *<li><B>Step:</B> Click on the Join this Community link </li>
	 *<li><B>Verify:</B> The 'You have joined...' message displays </li>
	 *<li><B>Step: </B> Navigate to the full Members page
	 *<li><B>Verify:</B> The user who joined the community is listed on the Members page & that they have 'Member' access</li>
	 *<li><B>Step:</B> Cleanup: Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */	
	@Test(groups = {"regression", "regressioncloud"})
	public void joinPublicCommunity() throws Exception {
		
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser2);

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.PUBLIC)
												   .description("Request to Join an Open/Public community test.")
												   .build();
		
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
									
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login as a user who is not a member of the community
		log.info("INFO: Log into the Public/Open community as a user who is not a member of the community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//navigate to the public/<org> communities catalog view
		log.info("INFO: Navigate to the public/<org> Communities catalog view");
		ui.goToPublicView(isCardView);
			
		//Verify the open/public community appears in the public/<org> communities view
		log.info("INFO: Verify the Open/Public community is listed in the public/<org> Communities view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Open/Public community is NOT listed in the public/<org> communities view");
		
		//Open the Open/Public community
		log.info("INFO: Open the community " + community.getName());
		ui.clickLinkWait(communityLink);
		
		//Verify the Join this Community link appears
		log.info("INFO: Verify the Join this Community link appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.Join_the_Community),
				"ERROR: The Join this Community link does not appear on the Overview page");
		
		//Click on the Join this Community link
		log.info("INFO: Click on the Request to Join link");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.Join_the_Community);
		
		//Verify the 'You have joined the community...' message displays
		log.info("INFO: Verify the 'You have joined the community...' message displays.");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.Communities_Joined_NoFollow),
				"ERROR: The 'You have joined the community...' message does not display.");
		
		//Navigate to the Members page
	    log.info("INFO: Navigate to the Members page");
	    Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Verify that the user who joined the community is listed on the Members page
	    log.info("INFO: Verify the user who joined the community " + testUser1.getDisplayName() + " is listed on the Members page");
	    Assert.assertTrue(ui.isElementPresent("link=" + testUser1.getDisplayName()),
						"ERROR: The user who joined the community is not listed on the Members page");
	    
	    //Verify that the user has "Member" access
	    log.info("INFO: Verify that the user has 'Member' access");
	    log.info("INFO: Collect the member text from member page");
	    String member1Info = ui.getMemberElement(member).getText();

	    log.info("INFO: Validating the added user is a member");
	    Assert.assertTrue(member1Info.contains("Member"),
	    		"ERROR: User does not contain member access");		
					
		//Cleanup: deleting the community
		log.info("INFO: Cleanup: deleting the community");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
			
	}
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test will check various parts of the create Community form for an Public Community</li>
	 * <li><B>Step:</B> Click on the Start a Community button</li>
	 * <li><B>Step:</B> Enter some text into the Name field</li>
	 * <li><B>Step:</B> On cloud: select the Open radio button, On-prem: Public is selected by default - leave as is </li>
	 * <li><B>Verify:</B> The Change Community Theme link does not appear on the form</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Verify:</B> The "+" sign to add external members does not display</li>
	 * <li><B>Verify:</B> The Web Address section does not appear on SC, but does appear on-prem</li>
	 * <li><B>Step:</B> Add some text to the Description field </li>
	 * <li><B>Step:</B> Click on the Save button</li>
	 * <li><B>Step:</B> Navigate to the catalog view I'm an Owner</li>
	 * <li><B>Verify:</B> The community appears in the view, no icon or text appears next to the community name</li>
	 * <li><B>Step:</B> Navigate to the I'm a Member catalog or My Communities (CR3) view</li>
	 * <li><B>Verify:</B> The community appears in the view, no icon or text appears next to the community name</li>
	 * <li><B>Step:</B> Navigate to the I'm Following catalog view</li>
	 * <li><B>Verify:</B> The community appears in the view, no icon or text appears next to the community name</li>
	 * <li><B>Step:</B> Navigate to the Public/<org> communities view</li>
	 * <li><B>Verify:</B> The community appears in the view, no icon or text appears next to the communityname</li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"} , enabled=false )			
	public void createPublicCommForm() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String product = cfg.getProductName();
		Element widget;


		//Create new community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                           .access(Access.PUBLIC)	
		                                           .description("Test the create form for a public community.")
		                                           .rbl(false)
		                                           .shareOutside(false)
		                                           .build();

		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);			
		ui.login(testUser1);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		log.info("INFO: If guided tour dialog appears, close it and bring up a start a community form");
		ui.closeGuidedTourSelectStartFromNew();

		log.info("INFO: Entering community name " + community.getName());
		driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

		log.info("INFO: Click on the Access Advanced Features link to expand the section");
		driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();
				
		if(!isOnPremise){
			log.info("INFO: The environment is Smart Cloud");

			log.info("INFO: Select the Access field radio button 'Open'");
			driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPublic).click();
			
			log.info("INFO: Verify the 'External' access checkbox 'Allow people from outside of my org...' is unchecked");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AllowExternalCheckboxDisabled),
					"ERROR: The external access checkbox is checked");

			//NOTE: To hide the link the HTML uses attribute style=display: none
			log.info("INFO: Verify the Change Community Theme link is hidden");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityThemeLinkHidden),
					"ERROR: Change Community Theme link displays when it should not");	

			log.info("INFO: Verify there is no Web Address section on the form");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.CommunityHandle),
					"ERROR: Web Address section appears on the create form, but should not");


		}else{
			log.info("INFO: The environment is on-prem.");

			log.info("Execute if GateKeeper setting " + Data.getData().gk_hikariTheme_flag + " is enabled");
			if(ui.checkGKSetting(Data.getData().gk_hikariTheme_flag)){		

				log.info("INFO: Verify the Change Community Theme link does not exist");
				Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.CommunityThemeLink),
						"ERROR: Change Community Theme link should not appear");				

			}else{
				log.info("INFO: Verify the Change Community Theme link exists");
				Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityThemeLink),
						"ERROR: Change Community Theme link does not exist");
			}
			log.info("INFO: Verify the Web Address section displays on the form");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityHandle),
					"ERROR: Web Address section does not appear on the create form, but should");

		}
		log.info("INFO: Verify the plus '+' sign to add external users does not display");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.addExtMemBtnHidden),
				"ERROR: The plus '+' sign to add external users should NOT display");	

		log.info("INFO: Enter text into the description field");
		ui.typeInCkEditor(community.getDescription());
			
		log.info("INFO: Click the Save button on the create form");
		driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
		
		if(isCardView){
		  // get the UUID of the community from the weburl, and set it on the community.
		  // this is needed to open the card.
		  
		  // Get url with params after UUID removed
		   String webUrl = this.driver.getCurrentUrl().split("&")[0];
		   community.setWebAddress(webUrl);
		   // Strip the UUID out of the weburl, left communityUuid= for legacy
		   community.setCommunityUUID(webUrl.split("\\?")[1]);
		}
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
		//determine if the environment is SC(cloud) or on-prem			
		if(!isOnPremise){
			log.info("INFO: The environment is Smart Cloud");

			log.info("INFO: Click on the Communities link on the mega-menu to return to the catalog views");			
			ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
					
			log.info("INFO: Click on the I'm an Owner catalog view");
			Community_View_Menu.IM_AN_OWNER.select(ui);
					
					
		}else{
			log.info("INFO: The environment is on-premise.");
					
			log.info("INFO: Click on the Communities link to return to the catalog views");			
			ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenu);
					
			if(getCatalogUIUpdatedGK()){
						
				log.info("INFO: Click on the My Communities link");
				ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
						
				log.info("INFO: go to I am owner view.");
				ui.goToOwnerView(isCardView);
				
			}
			else {
			    log.info("INFO: Click on the I'm an Owner link from the Communities drop-down menu on the mega menu");
			    ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuOwner);
			    ui.fluentWaitPresentWithRefresh("link="+ community.getName());
			}

		}
			
		log.info("INFO: Verify the community appears in the I'm an Owner catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
						  "ERROR: The community " + communityLink + " does NOT appear in the I'm an Owner catalog view, but should");
		
		log.info("INFO: Locate the community");
		widget = ui.getCommunityWidget(community.getName(),isCardView );
	
		if(!isCardView){
		   log.info("INFO: Verify no 'Moderated' icon/text displays next to the community name in the I'm an Owner catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ModeratedIconHidden),
				"ERROR: The 'Moderated' icon/text displays next to the community name, but should not");					

		   log.info("INFO: Verify no 'Restricted' icon/text displays next to the community name in the I'm an Owner catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.RestrictedIconHidden),
				"ERROR: The 'Restricted' icon/text displays next to the community name, but should not");

		   log.info("INFO: Verify no 'External' icon/text displays next to the community name in the I'm an Owner catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ExternalIconHidden),
				"ERROR: The 'External' icon/text displays next to the community name, but should not");
		}
		else {
			verifyIconsOnPublicCommunity(community,isCardView);
		}

		if(getCatalogUIUpdatedGK()){
			
			ui.goToMyCommunitiesView(isCardView);
			
			log.info("INFO: Verify the community appears in the My Communities catalog view");
		    Assert.assertTrue(driver.isElementPresent(communityLink),
					   "ERROR: The community " + communityLink + " does NOT appear in the My Communities catalog view, but should");			
		}
		else {
		   log.info("INFO: Clicking on the I'm a Member catalog view link");
		   Community_View_Menu.IM_A_MEMBER.select(ui);
		   
		   log.info("INFO: Verify the community appears in the I'm a Member catalog view");
			ui.fluentWaitPresentWithRefresh("link="+ community.getName());
		   Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community.getName()),
					"ERROR: The community " + community.getName() + " does NOT appear in the I'm a Member catalog view, but should");	
		}	

		
		if(!isCardView){
			
		   log.info("INFO: Locate the community");
		   widget = ui.getCommunityWidget(communityLink);

		   log.info("INFO: Verify no 'Moderated' icon/text displays next to the community name in the I'm a Member catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ModeratedIconHidden),
				"ERROR: The 'Moderated' icon/text displays next to the community name, but should not");					

		   log.info("INFO: Verify no 'Restricted' icon/text displays next to the community name in the I'm a Member catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.RestrictedIconHidden),
				"ERROR: The 'Restricted' icon/text displays next to the community name, but should not");

		   log.info("INFO: Verify no 'External' icon/text displays next to the community name in the I'm a Member catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ExternalIconHidden),
				"ERROR: The 'External' icon/text displays next to the community name, but should not");
		}
		
		
		ui.goToIamFollowingView(isCardView, isOnPremise);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				   "ERROR: The community " + communityLink + " does NOT appear in the I'm Following catalog view, but should");	

		
		if(!isCardView){
			
		   log.info("INFO: Locate the community");
		   widget = ui.getCommunityWidget(communityLink);
			
		   log.info("INFO: Verify no 'Moderated' icon/text displays next to the community name in the I'm Following catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ModeratedIconHidden),
				"ERROR: The 'Moderated' icon/text displays next to the community name, but should not");					

		   log.info("INFO: Verify no 'Restricted' icon/text displays next to the community name in the I'm Following catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.RestrictedIconHidden),
				"ERROR: The 'Restricted' icon/text displays next to the community name, but should not");

		   log.info("INFO: Verify no 'External' icon/text displays next to the community name in the I'm Following catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ExternalIconHidden),
				"ERROR: The 'External' icon/text displays next to the community name, but should not");
		}

		if (isCardView){
		   ui.goToDiscoverCardView();
		   Assert.assertTrue(driver.isElementPresent(communityLink),
					   "ERROR: The community " + communityLink + " does NOT appear in the Discovery view, but should");	

		}
		else {
		   log.info("INFO: Clicking on the Public/Org Community catalog view link");
		   Community_View_Menu.PUBLIC_COMMUNITIES.select(ui);

		   log.info("INFO: Verify the community appears in the public community catalog view");
		   ui.fluentWaitPresentWithRefresh("link="+ community.getName());
		   Assert.assertTrue(ui.fluentWaitPresentWithRefresh("link="+ community.getName()),
				"ERROR: The community " + community.getName() + " does NOT appear in the public community catalog view, but should");
		}
		

		if(!isCardView){
			
		   log.info("INFO: Locate the community");
		   widget = ui.getCommunityWidget(community.getName());
			
		   log.info("INFO: Verify no 'Moderated' icon/text displays next to the community name in the Public Community catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ModeratedIconHidden),
				"ERROR: The 'Moderated' icon/text displays next to the community name, but should not");					

		   log.info("INFO: Verify no 'Restricted' icon/text displays next to the community name in the Public Community catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.RestrictedIconHidden),
				"ERROR: The 'Restricted' icon/text displays next to the community name, but should not");

		   log.info("INFO: Verify no 'External' icon/text displays next to the community name in the Public Community catalog view");
		   Assert.assertTrue(widget.isElementPresent(CommunitiesUIConstants.ExternalIconHidden),
				"ERROR: The 'External' icon/text displays next to the community name, but should not");
		}

		log.info("INFO: Open the community");
		ui.fluentWaitPresentWithRefresh(communityLink);
		ui.clickLinkWait(communityLink);

		log.info("INFO: Navigate to the full Members page");		
		ui.clickLinkWait(CommunitiesUIConstants.tabbedNavMembersTab);
			
		if(!isOnPremise){
			log.info("INFO: The environment is Smart Cloud.");
			
			log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org does not display");

		}else{
			log.info("INFO: The environment is on-premise.");
			
			if(!isCardView){
			   log.info("INFO: Verify the Moderated icon/text does NOT display on the full Members page of a public community");
			   Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.moderatedIconFullMembersPage),
					"ERROR: The Moderated icon appears on the full Members page, but should not");

			   log.info("INFO: Verify the Restricted icon/text does NOT display on the full Members page of a public community");
			   Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.RestrictedIconMembersPage),
					"ERROR: The Restricted icon & text appear on the full Members page, but should not");

			   log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization message displays");
			   Assert.assertFalse(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org displays, but should not");
			}

		}
		log.info("INFO: Removing community for Test case ");
		community.delete(ui, testUser1);

		ui.endTest();		

	}
	
	private void verifyIconsOnPublicCommunity (BaseCommunity community, boolean isCardView)
	{
		//Get community widget
		log.info("INFO: Get community widget from view");
		Element widget = ui.getCommunityWidget(community.getName(), isCardView);
			
		if (isCardView) {
			
			// The icon is an overlay on the card, and is present or not.
			// So, this code will check for the 3 icons, it does not check for text, as the text is an attribute of the icon overlay.
			
			// NOTE: I noticed that looking for an element that is NOT present is slow, so another reason
			//       to only look for the icons.
			
			//Verify the Moderated Icon or static text should not display  for public community in Public Communities view
			log.info("INFO: Verify the Moderated Icon should not display  for public community in I'm an Owner view");
			Assert.assertFalse(widget.isElementPresent(CommunitiesUIConstants.ModeratedCardIcon),
								"ERROR: Moderated Icon is present for public community in I'm an Owner view");
			
			//Verify the Restricted Icon or static text should not display for public community in Public Communities view
			log.info("INFO: Verify the Restricted Icon should not display for public community in Public Communities view");
			Assert.assertFalse(widget.isElementPresent(CommunitiesUIConstants.RestrictedCardIcon),
						"ERROR: Restricted Icon is present for public community in Public Communities view");
	
			
			//Verify the External Icon or static text should not display for public community in Public Communities view
			log.info("INFO: Verify the External Icon should not display for public community in Public Communities view");
			Assert.assertFalse(widget.isElementPresent(CommunitiesUIConstants.ExternalCardIcon),
						"ERROR: External Icon is present for public community in Public Communities view");
	
		}
		else {
			//Verify the Moderated Icon or static text should not display  for public community in Public Communities view
			log.info("INFO: Verify the Moderated Icon should not display  for public community in Public Communities view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ModeratedIcon).isVisible(),
						"ERROR: Moderated Icon is present for public community in Public Communities view");
	
			//Moderated static text
			log.info("INFO: Verify the Moderated static text should not display for public community in Public Communities view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ModeratedText).isVisible(),
						"ERROR: Moderated static text is present for public community in Public Communities view");

			//Verify the Restricted Icon or static text should not display for public community in Public Communities view
			log.info("INFO: Verify the Restricted Icon should not display for public community in Public Communities view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.RestrictedIcon).isVisible(),
						"ERROR: Restricted Icon is present for public community in Public Communities view");
	
			//Restricted static text
			log.info("INFO: Verify the Restricted static text should not display for public community in Public Communities view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.RestrictedText).isVisible(),
						"ERROR: Restricted static text is present for public community in Public Communities view");

			//Verify the External Icon or static text should not display for public community in Public Communities view
			log.info("INFO: Verify the External Icon should not display for public community in Public Communities view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalIcon).isVisible(),
						"ERROR: External Icon is present for public community in Public Communities view");
	
			//External static text
			log.info("INFO: Verify the External static text should not display for public community in Public Communities view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalText).isVisible(),
						"ERROR: External static text is present for public community in Public Communities view");
	}
	}
	
	/**
	 * This method will get 6.0 CR4 Catalog Card View Gate keeper
     */
	private boolean getCatalogCardGK( ) {
		
		return ui.checkGKSetting(Data.getData().gk_catalog_card_view);
	}
	
	/**
	 * This method will get 6.0 CR3 Catalog UI Updated Gate keeper
     */
	private boolean getCatalogUIUpdatedGK( ) {
		
		return ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag);
	}
}