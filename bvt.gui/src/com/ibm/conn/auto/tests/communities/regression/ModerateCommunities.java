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
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class ModerateCommunities extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(ModerateCommunities.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private User testUser1,testUser2;
	private APICommunitiesHandler apiOwner;
	private boolean isOnPremise;
	private String serverURL; 
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
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
		
	}
	
	/**
	 *<ul>
	 *<li>PTC_Create_ModerateCommunities</li>
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Moderated community appears in the catalog views (1 of 4)</li>
	 *<li><B>Info:</B> Verify the Moderated community appears in the I'm an Owner view</li>
	 *<li><B>Step:</B> Create a Moderated community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify that Moderated community is listed in the I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon displays for Moderated community in I'm an Owner view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/60276F3363469E4485257CAD0065AF2A">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING MODERATED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityOwnerView() throws Exception {
		
		String testName = ui.startTest();
		Element widget;

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
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
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
				
		//Navigate to the I'm an Owner view
		log.info("INFO: Clicking on the I'm an Owner link on the left nav. bar");
		ui.goToOwnerView(isCardView);

		//Verify the Moderated community appears in the I'm an Owner view
		log.info("INFO: Verify the Moderated community is listed in the I'm an Owner view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
							"ERROR: Moderated community is NOT listed in the I'm an Owner view");
		
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(), isCardView);
			
		//Verify the Moderated Icon displays for Moderated community in I'm an Owner view
		log.info("INFO: Verify the Moderated Icon displays for Moderated community in I'm an Owner view");
		String moderateCardIcon = isCardView ? CommunitiesUIConstants.ModeratedCardIcon : CommunitiesUIConstants.ModeratedIcon;
		Assert.assertTrue(widget.getSingleElement(moderateCardIcon).isVisible(),
							"ERROR: Moderated Icon is not present for Moderated community in I'm an Owner view");
		
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		log.info("INFO: Removing community" + testName);
		community.delete(ui, testUser1);
		
		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 *<li>PTC_Create_ModerateCommunities</li>
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Moderated community appears in the catalog views (2 of 4)</li>
	 *<li><B>Info:</B> Verify the Moderated community appears in the I'm a Member view</li>
	 *<li><B>Step:</B> Create a Moderated community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm a Member or My Communities (CR3) view</li>
	 *<li><B>Verify:</B> Verify that Moderated community is listed in the I'm a Member or My Communities (CR3) view</li>
	 **<li><B>Verify:</B> Verify the Moderated Icon displays for Moderated community in I'm an Member view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/60276F3363469E4485257CAD0065AF2A">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING MODERATED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityMemberView() throws Exception {

		String testName = ui.startTest();
		Element widget;
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
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
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);	
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
		
		//go to default Catalog view i.e I'm a Member or My Communities (CR3)
		ui.goToDefaultCatalogView();
				
		//Verify the Moderated community appears in the default Catalog view
		log.info("INFO: Verify the Moderated community is listed in the default Catalog view");
			ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
					"ERROR: Moderated community is NOT listed in the default Catalog view");
		
		
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);
			
	
		//Verify the Moderated Icon displays for Moderated community in I'm an Member view
		log.info("INFO: Verify the Moderated Icon displays for Moderated community in I'm an Member view");
		String moderateCardIcon = isCardView ? CommunitiesUIConstants.ModeratedCardIcon : CommunitiesUIConstants.ModeratedIcon;
		Assert.assertTrue(widget.getSingleElement(moderateCardIcon).isVisible(),
							"ERROR: Moderated Icon is not present for Moderated community in I'm an Member view");
			
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community" + testName);
		community.delete(ui, testUser1);
				
		ui.endTest();
		
		
	}
	
	
	
	/**
	 *<ul>
	 *<li>PTC_Create_ModerateCommunities</li>
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Moderated community appears in the catalog views (3 of 4)</li>
	 *<li><B>Info:</B> Verify the Moderated community appears in the Public Communities view</li>
	 *<li><B>Step:</B> Create a Moderated community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the Public Communities view</li>
	 *<li><B>Verify:</B> Verify that Moderated community is listed in the Public Communities view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon displays for Moderated community in the Public Communities view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/60276F3363469E4485257CAD0065AF2A">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING MODERATED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityPublicView() throws Exception {

		String testName = ui.startTest();
		Element widget;
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
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
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);						
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
										
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			
		//Navigate to the Public Communities view
		log.info("INFO: Clicking on the Public Communities link on the left nav. bar");
		ui.goToPublicView(isCardView);
		
		ui.applyCatalogFilter(community.getName(), isCardView);
		
		//Click on the 100 link to display 100 communities on the page instead of only 10.  
		//Added this step so there is a greater chance the community will be found when there are a lot of public communities.
		if(!ui.checkGKSetting(Data.getData().gk_catalog_ui_updated_flag)){
		   log.info("INFO: From the catalog view click on '100' link to show 100 communities");
		   ui.clickLinkWait(CommunitiesUIConstants.show100Comm);
		}

		//Verify the Moderated community appears in the Public Communities view
		log.info("INFO: Verify the Moderated community is listed in the Public Communities view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Moderated community is NOT listed in the Public Communities view");
		
		
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);
		
		//Verify the Moderated Icon displays for Moderated community in the Public Communities view
		log.info("INFO: Verify the Moderated Icon displays for Moderated community in the Public Communities view");
		String moderateCardIcon = isCardView ? CommunitiesUIConstants.ModeratedCardIcon : CommunitiesUIConstants.ModeratedIcon;
		Assert.assertTrue(widget.getSingleElement(moderateCardIcon).isVisible(),
							"ERROR: Moderated Icon is not present for Moderated community in the Public Communities view");
		
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
						
		log.info("INFO: Removing community" + testName);
		community.delete(ui, testUser1);
				
		ui.endTest();
		
		
	}
	
	
	/**
	 *<ul>
	 *<li>PTC_Create_ModerateCommunities</li>
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Moderated community appears in the catalog views (4 of 4)</li>
	 *<li><B>Info:</B> Verify the Moderated community appears in the I'm Following view</li>
	 *<li><B>Step:</B> Create a Moderated community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm Following view</li>
	 *<li><B>Verify:</B> Verify that Moderated community is listed in the I'm Following view</li>
	 *<li><B>Verify:</B> Verify the Moderated Icon displays for Moderated community in the I'm Following view</li>
	 *<li><B>Verify:</B> Verify the word Moderated appears after the icon for Moderated community in the I'm Following view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/60276F3363469E4485257CAD0065AF2A">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING MODERATED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void followModeratedCommunity() throws Exception {

		String testName = ui.startTest();
		Element widget;
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
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
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);						
				
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Navigate to the I'm Following view
		log.info("INFO: Clicking on the I'm Following link on the left nav. bar");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		//Verify the Moderated community appears in the I'm Following view
		log.info("INFO: Verify the Moderated community is listed in the I'm Following view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Moderated community is NOT listed in the I'm Following view");
		
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);		
		
		//Verify the Moderated Icon displays for Moderated community in the I'm Following view
		log.info("INFO: Verify the Moderated Icon displays for Moderated community in the I'm Following view");
		String moderateCardIcon = isCardView ? CommunitiesUIConstants.ModeratedCardIcon : CommunitiesUIConstants.ModeratedIcon;
		Assert.assertTrue(widget.getSingleElement(moderateCardIcon).isVisible(),
							"ERROR: Moderated Icon is not present for Moderated community in the I'm Following view");		
		
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
						
		log.info("INFO: Removing community" + testName);
		community.delete(ui, testUser1);

		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Panasonic UAT: Request to Join a Moderated Community</li>
	 *<li><B>Info:</B> This test will verify the ability for a user to request to join a moderated community</li>
	 *<li><B>Step:</B> Create a moderated community using the API</li>
	 *<li><B>Step:</B> Login as a user who is not a member of the community</li> 
	 *<li><B>Step:</B> Navigate to the public/<org> communities catalog view</li>
	 *<li><B>Step:</B> Open the moderated community</li>
	 *<li><B>Verify:</B> The Request to Join this Community link appears on the Overview page
	 *<li><B>Step:</B> Click on the Request to Join this Community link </li>
	 *<li><B>Verify:</B> The request to join form displays</li>
	 *<li><B>Step: </B> Enter some text into the ckeditor field & click Send button </li>
	 *<li><B>Verify:</B> The message that the request was sent displays</li>
	 *<li><B>Step:</B> Cleanup: Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */	

	@Test(groups = {"regression", "regressioncloud"})
	public void requestToJoinModeratedComm() throws Exception {
		
		String testName = ui.startTest();
		String descriptionText = "Please add me to this Moderated community!";

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.MODERATED)
												   .description("Request to Join a Moderated community test.")
												   .build();
		
		
		//Create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
									
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
							
		//GUI
		//Load component and login as a user who is not a member of the community
		log.info("INFO: Log into the Moderated community as a user who is not a member of the community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(commAPI, StartPageApi.OVERVIEW);
		}
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
						
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		
		//navigate to the public/<org> communities view
		log.info("INFO: Navigate to the public/<org> Communities catalog view");
		ui.goToPublicView(isCardView);	
		
		ui.applyCatalogFilter(community.getName(), isCardView);
			
		//Verify the Moderated community appears in the <org> communities view
		log.info("INFO: Verify the Moderated community is listed in the <org> Communities view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Moderated community is NOT listed in the <org> communities view");
		
		//Open the Moderated community
		log.info("INFO: Open the Moderated community " + community.getName());
		ui.clickLinkWait(communityLink);
		
		//Verify the Request to Join this Community link appears
		log.info("INFO: Verify the Request to Join this Community link appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.Join_the_Community),
				"ERROR: The Request to Join this Community link does not appear on the Overview page");
		
		//Click on the Request to Join this Community link
		log.info("INFO: Click on the Request to Join this Community link");
		ui.clickLinkWait(CommunitiesUIConstants.Join_the_Community);
		
		//Verify the Request to Join form displays
		log.info("INFO: Verify the Request to Join form displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.reqToJoinModeratedCommForm),
				"ERROR: Form does not display");	
		
		//Enter the description in CKEditor
		log.info("INFO: Entering a description if we have one");
		ui.typeNativeInCkEditor(descriptionText);

		//Click the Send button to send the request to join the community
		log.info("INFO: Click the Send button to send the request to join the community");
		ui.clickLinkWait(CommunitiesUIConstants.SendJoinRequest_Btn);
		
		//Verify the membership request was sent message displays
		log.info("INFO: Verify the membership request was sent message displays.");
		Assert.assertTrue(driver.isTextPresent(Data.getData().MembershipRequestSent),
				"ERROR: The membership request was sent message does not display.");
		
		//Cleanup: deleting the community
		log.info("INFO: Cleanup: deleting the community");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
			
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test will check various parts of the create Community form for an Moderated Community</li>
	 * <li><B>Step:</B> Click on the Start a Community button</li>
	 * <li><B>Step:</B> Enter some text into the Name field</li>
	 * <li><B>Step:</B> Select the Access level of Moderated</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Verify:</B> The "+" sign to add external members does not display</li> 
	 * <li><B>Verify:</B> The Web Address section does not appear on SC, but does appear on-prem</li>
	 * <li><B>Step:</B> Add some text to the Description field </li>
	 * <li><B>Step:</B> Click on the Save button</li>
	 * <li><B>Step:</B> Navigate to the catalog view I'm an Owner</li>
	 * <li><B>Verify:</B> The community appears in the view, the Moderated icon display for the community</li>
	 * <li><B>Step:</B> Navigate to the I'm a Member catalog view</li>
	 * <li><B>Verify:</B> The community appears in the view, the Moderated icon display for the community</li>
	 * <li><B>Step:</B> Navigate to the I'm Following catalog view</li>
	 * <li><B>Verify:</B> The community appears in the view, the Moderated icon display for the community</li>
	 * <li><B>Step:</B> Navigate to the Public/<org> communities view</li>
	 * <li><B>Verify:</B> The community appears in the view, the Moderated icon display for the community</li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"} , enabled=false )			
	public void createModeratedCommForm() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Element widget;


		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                           .access(Access.MODERATED)	
		                                           .description("Test the create community form for a moderated community.")
		                                           .rbl(false)
		                                           .shareOutside(false)
		                                           .build();
		
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);			
		ui.login(testUser1);			
	
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		log.info("INFO: Click on the Start a Community button");
		ui.closeGuidedTourSelectStartFromNew();

		log.info("INFO: Enter the community name " + community.getName());
		driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

		log.info("INFO: Select the Access field radio button 'Moderated'");
		driver.getFirstElement(CommunitiesUIConstants.CommunityAccessModerated).click();
		
		log.info("INFO: Click on the Access Advanced Features link to expand the section");
		driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();
		
		if(!isOnPremise){
			log.info("INFO: The environment is Smart Cloud");

			log.info("INFO: Verify the 'External' access checkbox 'Allow people from outside of my org...' is unchecked after selecting Moderated");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.AllowExternalCheckboxDisabled),
					"ERROR: The external access checkbox is checked even after selected the option 'Moderated'");

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
		log.info("INFO: Verify the link to upload a community image exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.uploadCommunityImageLink),
				"ERROR: The link to upload a community image does not appear on the create form, but should");

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
		

		log.info("INFO: Clicking on the Communities link on the mega-menu to return to I'm an Owner view");			
		ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();

		log.info("INFO: Verify the community appears in the I'm an Owner catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the I'm an Owner catalog view, but should");

		
		log.info("INFO: Locate the community");
		widget = ui.getCommunityWidget(community.getName(),isCardView);

		log.info("INFO: Verify the Moderated icon displays for the community in I'm an Owner catalog view");
		String moderateCardIcon = isCardView ? CommunitiesUIConstants.ModeratedCardIcon : CommunitiesUIConstants.ModeratedIcon;
		Assert.assertTrue(widget.getSingleElement(moderateCardIcon).isVisible(),
				"ERROR: The Moderated icon does not appear for the community in the I'm an Owner catalog view, but should");

		
		//go to default Catalog view i.e I'm a Member or My Communities (CR3)
		ui.goToDefaultCatalogView();
		//Verify the Moderated community appears in the default Catalog view
        log.info("INFO: Verify the community appears in the default catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the default catalog view, but should");
		
		if (!isCardView) {
			log.info("INFO: Locate the community");
			widget = ui.getCommunityWidget(community.getName());

			log.info("INFO: Verify the Moderated icon displays for the community in I'm a Member catalog view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.ModeratedIcon).isVisible(),
				"ERROR: The Moderated icon does not appear for the community in the I'm a Member catalog view, but should");
		}
		
		log.info("INFO: Clicking on the I'm Following catalog view link");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Verify the community appears in the I'm Following catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the I'm Following catalog view, but should");

		if (!isCardView) {
			log.info("INFO: Locate the community");
			widget = ui.getCommunityWidget(community.getName());

			log.info("INFO: Verify the Moderated icon displays for the community in I'm Following catalog view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.ModeratedIcon).isVisible(),
				"ERROR: The Moderated icon does not appear for the community in the I'm Following catalog view, but should");
		}
		
		log.info("INFO: Clicking on the Public/Org Community catalog view link");
		ui.goToPublicView(isCardView);

		log.info("INFO: Verify the community appears in the public community catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the public community catalog view, but should");
		
		if (!isCardView) {
			log.info("INFO: Locate the community");
			widget = ui.getCommunityWidget(community.getName());

			log.info("INFO: Verify the Moderated icon displays for the community in Public Community catalog view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.ModeratedIcon).isVisible(),
				"ERROR: The Moderated icon does not appear for the community in the Public Community catalog view, but should");
		}
		
		log.info("INFO: Open the community");
		if (!isCardView) {
			ui.fluentWaitPresentWithRefresh(community.getName());
		}
		ui.clickLinkWait(communityLink);

		log.info("INFO: Navigate to the full Members page");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		if(!isOnPremise){
			log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org does not display, but should");

		}else{
			log.info("INFO: Verify the Moderated icon/text displays on the full Members page");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moderatedIconFullMembersPage),
					"ERROR: The Moderated icon does not appear on the full Members page, but should");
		}
		log.info("INFO: Cleanup - Removing community for Test case ");
		community.delete(ui, testUser1);

		ui.endTest();		
		
	}
	

}
