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
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
//import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;

public class RestrictedCommunities extends SetUpMethods2 {
	
	protected static Logger log = LoggerFactory.getLogger(RestrictedCommunities.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private User testUser,testUser1;
	private APICommunitiesHandler apiOwner;
	String serverURL;
	private boolean isOnPremise;
	

	/**
	 * PTC Files combined:
	 * PTC_Create_RestrictedCommunity
	*/
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();		
		
		//load user	
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		
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
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Restricted community appears in the catalog views (1 of 3)</li>
	 *<li><B>Info:</B> Verify the Restricted community appears in the I'm an Owner view, but NOT the Public Communities view</li>
	 *<li><B>Step:</B> Create a Restricted community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the Public Communities view</li>
	 *<li><B>Verify:</B> Verify the Restricted community is NOT listed in the Public Communities view</li>
	 *<li><B>Step:</B> Navigate to the I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the Restricted community is listed in the I'm an Owner view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon displays for Restricted community in I'm an Owner view</li>
	 *<li><B>Step:</B> Cleanup by deleting the restricted community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/473E499A2487705B85257CAD005A6F0C">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING RESTRICTED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void restrictedCommunityOwnerView() throws Exception {

		String testName = ui.startTest();
		Element widget;
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .description("Test community for " + testName )
												   .shareOutside(false)
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
										
		//Click on the Public Communities link from the catalog view
		log.info("INFO: Navigate to the Public Communities view");
		ui.goToPublicView(isCardView);
		
		//Verify the restricted community is NOT listed in the Public Communities view
		log.info("INFO: Verify the restricted community is NOT listed in the Public Communities view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertFalse(driver.isElementPresent(communityLink),
				"ERROR: The Restricted community appears in the Public Communities view");
		
		//Click on the I'm an Owner link 
		log.info("INFO: Navigate to the I'm an Owner view");
		ui.goToOwnerView(isCardView);
		
		//Verify the restricted community is listed in the I'm an Owner view
		log.info("INFO: Verify the restricted community is listed in the I'm an Owner view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Restricted community is NOT listed in the I'm an Owner view");
		
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(), isCardView);
				
		//Verify the restricted Icon displays for restricted community in I'm an Owner view
		log.info("INFO: Verify the restricted Icon displays for restricted community in I'm an Owner view");
		String restrictedIcon = isCardView ? CommunitiesUIConstants.RestrictedCardIcon : CommunitiesUIConstants.RestrictedIcon;
		Assert.assertTrue(widget.getSingleElement(restrictedIcon).isVisible(),
							"ERROR: Restricted Icon is not present for restricted community in I'm an Owner view");
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		log.info("INFO: Removing community for Test case " + testName);
		community.delete(ui, testUser1);

		ui.endTest();			
	}
	 
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Restricted community appears in the catalog views (2 of 3)</li>
	 *<li><B>Info:</B> Verify the Restricted community appears in the I'm a Member view</li>
	 *<li><B>Step:</B> Create a Restricted community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm a Member or My Communities (CR3) view</li>
	 *<li><B>Verify:</B> Verify the Restricted community is listed in the I'm a Member or My Communities (CR3) view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon displays for Restricted community in I'm a Member view</li>
	 *<li><B>Step:</B> Cleanup by deleting the restricted community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/473E499A2487705B85257CAD005A6F0C">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING RESTRICTED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void restrictedCommunityMemberView() throws Exception {

		String testName = ui.startTest();
		Element widget;
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .description("Test community for " + testName )
												   .shareOutside(false)
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
		//Verify the community is listed in the view
		log.info("INFO: Verify the restricted community is listed in the default Catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);	
		Assert.assertTrue(driver.isElementPresent(communityLink),
					"ERROR: Restricted community is NOT listed in the default Catalog view");
			
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);
		
		//Verify the restricted Icon displays for restricted community in I'm a Member view
		log.info("INFO: Verify the restricted Icon displays for restricted community in I'm a Member view");
		String restrictedIcon = isCardView ? CommunitiesUIConstants.RestrictedCardIcon : CommunitiesUIConstants.RestrictedIcon;
		Assert.assertTrue(widget.getSingleElement(restrictedIcon).isVisible(),
							"ERROR: Restricted Icon is not present for restricted community in I'm a Member view");
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		log.info("INFO: Removing community for Test case " + testName );
		community.delete(ui, testUser1);

		ui.endTest();		
	}
	
	/**
	 *<li><B>Test Scenario:</B> Catalog View - Verify a Restricted community appears in the catalog views (3 of 3)</li>
	 *<li><B>Info:</B> Verify the Restricted community appears in the I'm Following view</li>
	 *<li><B>Step:</B> Create a Restricted community using the API</li>
	 *<li><B>Step:</B> Login as the community creator</li>
	 *<li><B>Step:</B> Navigate to the I'm Following view</li>
	 *<li><B>Verify:</B> Verify the Restricted community is listed in the I'm Following view</li>
	 *<li><B>Verify:</B> Verify the Restricted Icon displays for Restricted community in I'm Following view</li>
	 *<li><B>Step:</B> Cleanup by deleting the restricted community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/473E499A2487705B85257CAD005A6F0C">TTT-ON-PREM: ACTIONS IN CATALOG VIEW: VERIFY CREATING RESTRICTED COMMUNITY</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void followRestrictedCommunity() throws Exception {

		String testName = ui.startTest();
		Element widget;
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .access(Access.RESTRICTED)
												   .tags(Data.getData().commonTag + Helper.genDateBasedRand())
												   .description("Test community for " + testName )
												   .shareOutside(false)
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
				
			
		//Click on the I'm Following link from the catalog view
		log.info("INFO: Clicking on the I'm Following link from the catalog view");
		ui.goToIamFollowingView(isCardView, isOnPremise);
		
		//Verify the community is listed in the view
		log.info("INFO: Verify the restricted community is listed in the I'm Following view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: Restricted community is NOT listed in the I'm Following view");
	
		//Get community widget
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);
	
		//Verify the restricted Icon displays for restricted community in I'm Following view
		log.info("INFO: Verify the restricted Icon displays for restricted community in I'm Following view");
		String restrictedIcon = isCardView ? CommunitiesUIConstants.RestrictedCardIcon : CommunitiesUIConstants.RestrictedIcon;
		Assert.assertTrue(widget.getSingleElement(restrictedIcon).isVisible(),
							"ERROR: Restricted Icon is not present for restricted community in I'm Following view");

		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWithJavascript(communityLink);
		
		log.info("INFO: Removing community for Test case " + testName );
		//community.delete(ui, testUser1);
		apiOwner.deleteCommunity(commAPI);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
	 * <li><B>NOTE: this test is run on-prem only.  "Public Communities" is not used on SC </li>
	 * <li><B>Info:</B> Basic test to verify the catalog view 'My Organization Communities' exists & that 'Public Communities' does not appear </li>
	 * <li><B>Step:</B> Log into Communities </li>
	 * <li><B>Verify:</B> Verify the catalog view 'My Organization Communities'displays</li>
	 * <li><B>Verify:</B> Verify the catalog view 'Public Communities' does not display</li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
	 *</ul>
	 */			

	@Test(groups = {"regression"})
	public void myOrgCommCatalogViewDisplays() throws Exception {


		String publicCommView = "Public Communities";
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		if (!isCardView)	{			
			//Verify the view 'My Organization Communities' appears in the catalog view
			log.info("INFO: Verify the view 'My Organization Communities' appears in the catalog view");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.myOrgCommunitiesView),
				"ERROR: The view My Organization Communities does NOT appear in the catalog view");
		
			//Verify the view 'Public Communities' does not appear in the catalog view
			log.info("INFO: Verify the view 'Public Communities' does NOT appear in the catalog view");
			Assert.assertFalse(driver.isElementPresent("link=" + publicCommView),
				"ERROR: The view 'Public Communities' appears in the catalog view");
		}
			
		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
	 * <li><B>Info:</B> Basic test to verify that the listed but restricted (RbL) checkbox displays when Restricted access is selected </li>
	 * <li><B>Info:</B> Test will also verify that if the checkbox is select that a conditional warning message will display </li>
	 * <li><B>Info:</B> When the RbL checkbox is deselected the conditional message no longer displays </li>
	 * <li><B>Step:</B> Click on the Start a Community button </li>
	 * <li><B>Step:</B> Input content into the Name & Description fields </li>
	 * <li><B>Step:</B> On the create form select the Access level button 'Restricted' </li>
	 * <li><B>Verify:</B> Verify the listed but restricted checkbox displays </li>
	 * <li><B>Verify:</B> Verify the checkbox text displays </li>
	 * <li><B>Verify:</B> Verify the conditional warning message does not display </li>
	 * <li><B>Step:</B> Check the listed but restricted checkbox </li>
	 * <li><B>Verify:</B> Verify the conditional warning message now displays </li>
	 * <li><B>Step:</B> Uncheck the listed but restricted checkbox </li>
	 * <li><B>Verify:</B> Verify the conditional warning message no longer displays </li> 
	 * <li><B>Step:</B> Click on the Cancel button to close the create form </li>
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
	 *</ul>
	 */			

	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void createCommFormRblCheckbox() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String tag1 = "listed_restricted_checkbox";

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                           .access(Access.RESTRICTED)	
		                                           .tags(tag1)
		                                           .description("RbL checkbox & warning message test")	
		                                           .shareOutside(false)
		                                           .rbl(true)
		                                           .build();

		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Bring up the Start a Community form");
		ui.closeGuidedTourSelectStartFromNew();

		log.info("INFO: Wait for the create form to load");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

		log.info("INFO: Entering community name " + community.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

		log.info("INFO: Entering a description if we have one");
		if(community.getDescription() != null) {
			ui.typeInCkEditor(community.getDescription());

			log.info("INFO: Select the Access level radio button 'Restricted'");
			driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();

			log.info("INFO: Verify the 'listed but restricted' checkbox displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.listedRestrictedCheckboxComm),
					"ERROR: The listed but restricted checkbox does NOT display on the form");

			log.info("INFO: Verify the 'listed but restricted' checkbox text displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().listedRestrictedCheckboxText),
					"ERROR: The text for the listed but restricted checkbox does NOT display on the form");

			log.info("INFO: Verify the 'listed but restricted' conditional warning message does NOT display");
			Assert.assertFalse(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
					"ERROR: The listed but restricted checkbox is checked by default.  The conditional warning message displays.");

			log.info("INFO: Check the 'listed but restricted' checkbox");
			driver.getSingleElement(CommunitiesUIConstants.listedRestrictedCheckboxComm).click();

			log.info("INFO: Verify the 'listed but restricted' conditional warning message now displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
					"ERROR: The listed but restricted conditional warning message does NOT display when the checkbox is checked.");

			log.info("INFO: Uncheck the 'listed but restricted' checkbox");
			driver.getSingleElement(CommunitiesUIConstants.listedRestrictedCheckboxComm).click();

			log.info("INFO: Verify the 'listed but restricted' conditional warning message no longer displays");
			Assert.assertFalse(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
					"ERROR: The conditional warning message continues to display after unchecking the checkbox.");

			log.info("INFO: Click on the Cancel button");
			driver.getFirstElement(CommunitiesUIConstants.CancelButton).click();

			ui.endTest();

	}
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
	 * <li><B>Info:</B> This test will verify that an internal restricted LISTED community appears in the My Org Communities view </li>
	 * <li><B>Step:</B> Create an internal restricted community </li> 
	 * <li><B>Step:</B> Navigate to the I'm an Owner view </li>
	 * <li><B>Verify:</B> Verify the internal listed community appears in the view</li>
	 * <li><B>Step:</B> Navigate to the My Organization Communities view </li>
	 * <li><B>Verify:</B> Verify the community appears in the view </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
	 *</ul>
	 */			

	@Test(groups = {"regression","regressioncloud"})
	public void myOrgCommViewListedCommDisplays(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                            .access(Access.RESTRICTED)	
		                                            .description("RbL internal community appears in My Organization Communities view test")	
		                                            .rbl(true)
		                                            .shareOutside(false)
		                                            .build();

		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
	
	    log.info("INFO: Bring up the Start A Community form");
		ui.closeGuidedTourSelectStartFromNew();
		
		log.info("INFO: Wait for the create form to load");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

		log.info("INFO: Entering community name " + community.getName());
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

		log.info("INFO: Entering a description if we have one");
		if(community.getDescription() != null) {
			ui.typeInCkEditor(community.getDescription());

		log.info("INFO: Select the Access level radio button 'Restricted'");
		driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();
		
		log.info("INFO: Check the listed but restricted checkbox on the edit community form");
		driver.getFirstElement(CommunitiesUIConstants.listedRestrictedCheckboxComm).click();
		
		log.info("INFO: Click the Save button on the create form");
		driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
	    ui.fluentWaitElementVisible(CommunitiesUIConstants.communityActions);
	    ui.waitForPageLoaded(driver);
	
		if(isCardView){
		
			// get the UUID of the community from the weburl, this is needed to open the card.	  
			getCommUUIDFromWebURL(community);
		}
			
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			APICommunitiesHandler apiOwner1= new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
			Community communitycom = apiOwner1.getCommunity(community.getCommunityUUID());
			apiOwner1.editStartPage(communitycom, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");
		ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();

		log.info("INFO: Verify the internal listed community appears in the I'm an Owner catalog view");
		String commUUID = community.getCommunityUUID().replace("communityUuid=", "");
		communityLink= "css=div#community-card-" + commUUID.substring(0,commUUID.indexOf("#"));
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the I'm an Owner view");

		log.info("INFO: Clicking on the My Organization Communities link from the LeftNavigation");
		ui.goToPublicView(isCardView);

		log.info("INFO: Verify the internal listed community appears in the My Organization Communities catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the My Organization Communities view");
		
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		log.info("INFO: Cleanup - Removing community for Test case " + testName );
		community.delete(ui, testUser);
		
		ui.endTest();
		
			}
	}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
	 * <li><B>Info:</B> This test will verify that an internal restricted NON-LISTED community does NOT appear in the My Org Communities view </li>
	 * <li><B>Step:</B> Create an internal restricted community - NON-LISTED </li>
	 * <li><B>Step:</B> Navigate to the I'm an Owner view </li>
	 * <li><B>Verify:</B> Verify the internal NON-LISTED community appears in the view</li>
	 * <li><B>Step:</B> Navigate to the My Organization Communities view </li>
	 * <li><B>Verify:</B> Verify the NON-LISTED community does NOT appear in the view </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
	 *</ul>
	 */			

     @Test(groups = {"regression","regressioncloud"} , enabled=false )
     public void myOrgCommViewNonlistedComm(){

    	 String rndNum = Helper.genDateBasedRand();
    	 String testName = ui.startTest();

    	 BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
    	                                            .access(Access.RESTRICTED)	
    	                                            .description("UNLISTED internal community appears in My Organization Communities view test")	
    	                                            .rbl(true)
    	                                            .shareOutside(false)
    	                                            .build();

    	 log.info("INFO: Log into Communities");
    	 ui.loadComponent(Data.getData().ComponentCommunities);
    	 ui.login(testUser);
    	 
    	 // check if catalog_card_view GK enabled
		 boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

    	 log.info("INFO: Bring up the Start A Community form");
    	 ui.closeGuidedTourSelectStartFromNew();

    	 log.info("INFO: Wait for the create form to load");
    	 ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

    	 log.info("INFO: Entering community name " + community.getName());
    	 this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

    	 log.info("INFO: Entering a description if we have one");
    	 if(community.getDescription() != null) {
    		 ui.typeInCkEditor(community.getDescription());

    		 log.info("INFO: Select the Access level radio button 'Restricted'");
    		 driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();

    		 log.info("INFO: Click the Save button on the create form");
    		 driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
    		 
    		 if(isCardView){
    			// get the UUID of the community from the weburl, this is needed to open the card.	  
    			getCommUUIDFromWebURL(community);
    		 }
   			
    		 // get the community link
    		 String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
   	

    		 log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");
    		 ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();

    		 log.info("INFO: Verify the internal listed community appears in the I'm an Owner catalog view");
    		 ui.fluentWaitPresentWithRefresh(communityLink);

    		 log.info("INFO: Clicking on the My Organization Communities link from the LeftNavigation");
    		// ui.clickLinkWait(CommunitiesUI.leftNavPublicCommunities);
    		 ui.goToPublicView(isCardView);

    		 log.info("INFO: Verify the UNLISTED community does NOT appear in the My Organization Communities catalog view");
    		 Assert.assertFalse(ui.fluentWaitPresentWithRefresh(communityLink),
    				 "ERROR: The community" + community.getName() + " APPEARS in the My Organization Communities view");

    				
    		 log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
    		 ui.goToOwnerView(isCardView);

    		 log.info("INFO: Open community");
    		 ui.clickLinkWait(communityLink);

    		 log.info("INFO: Cleanup - Removing community for Test case " + testName );
    		 community.delete(ui, testUser);

    		 ui.endTest();

    	 }
     }

/**
 * <ul>
 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
 * <li><B>Info:</B> Test to verify that when the parent community has listed restricted select that the option appears on the create subcomm. form</li>
 * <li><B>Step:</B> Create a listed but restricted parent community </li>
 * <li><B>Step:</B> Open the create subcommunity form </li>
 * <li><B>Verify:</B> Verify the listed but restricted checkbox displays on the create subcomm form </li>
 * <li><B>Verify:</B> Verify the checkbox text displays on the create subcomm form </li>
 * <li><B>Verify:</B> Verify the RbL conditional warning message does not appear on the form by default </li>
 * <li><B>Step:</B> Check the RbL checkbox </li>
 * <li><B>Verify:</B> Verify the RbL conditional warning message now displays </li>
 * <li><B>Step:</B> Uncheck the RbL checkbox </li>
 * <li><B>Verify:</B> Verify the RbL conditional warning message no longer appears on the create form </li>
 * <li><B>Cleanup:</B> Delete the parent community </li> 
 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
 *</ul>
 */			

@Test(groups = {"regression","regressioncloud"} , enabled=false )
public void createSubcommFormListedParent(){

	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();

	BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
	                                           .access(Access.RESTRICTED)	
	                                           .description("Parent community - RbL subcommunity test")	
	                                           .shareOutside(false)
	                                           .build();
		
	log.info("INFO: Log into Communities");
	ui.loadComponent(Data.getData().ComponentCommunities);
	ui.login(testUser);
	
	// check if catalog_card_view GK enabled
	boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

	log.info("INFO: Bring up the Start A Community form");
	ui.closeGuidedTourSelectStartFromNew();

	log.info("INFO: Wait for the create form to load");
	ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

	log.info("INFO: Entering community name " + community.getName());
	this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

	log.info("INFO: Entering a description if we have one");
	if(community.getDescription() != null) {
		ui.typeInCkEditor(community.getDescription());
		
	log.info("INFO: Select the Access level radio button 'Restricted'");
	driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();
		
	log.info("INFO: Check the listed but restricted checkbox on the edit community form");
	driver.getFirstElement(CommunitiesUIConstants.listedRestrictedCheckboxComm).click();

	log.info("INFO: Click the Save button on the create form");
	driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
	
	if(isCardView){
		
		// get the UUID of the community from the weburl, this is needed to open the card.	  
		getCommUUIDFromWebURL(community);
	}
			
	// get the community link
	String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
	log.info("INFO: Click on the Create Subcommunity link from the Community Actions drop-down menu");
	Com_Action_Menu.CREATESUB.select(ui);
	
	log.info("INFO: Verify the 'listed but restricted' checkbox displays");
	Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.listedRestrictedCheckboxSubcomm),
			"ERROR: The listed but restricted checkbox does NOT display on the form");
	
	log.info("INFO: Verify the 'listed but restricted' checkbox text displays");
	Assert.assertTrue(driver.isTextPresent(Data.getData().listedRestrictedCheckboxText),
			"ERROR: The text for the listed but restricted checkbox does NOT display on the form");

	log.info("INFO: Verify the 'listed but restricted' conditional warning message does NOT display by default");
    Assert.assertFalse(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
		"ERROR: The listed but restricted checkbox is selected by default.  The conditional warning message displays on the form.");

	log.info("INFO: Check the 'listed but restricted' checkbox");
	driver.getSingleElement(CommunitiesUIConstants.listedRestrictedCheckboxSubcomm).click();

	log.info("INFO: Verify the 'listed but restricted' conditional warning message now displays");
	Assert.assertTrue(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
			"ERROR: The listed but restricted conditional warning message does NOT display on the form");

	log.info("INFO: Uncheck the 'listed but restricted' checkbox");
	driver.getSingleElement(CommunitiesUIConstants.listedRestrictedCheckboxSubcomm).click();

	log.info("INFO: Verify the 'listed but restricted' conditional warning message no longer displays");
	Assert.assertFalse(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
			"ERROR: The listed but restricted conditional warning message continues to display on the form");
	
	log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");
	ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();
	
	log.info("INFO: Open community");
	ui.clickLinkWait(communityLink);

	log.info("INFO: Cleanup - Removing community for Test case " + testName );
	community.delete(ui, testUser);

	ui.endTest();

}
}

/**
 * <ul>
 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
 * <li><B>Info:</B> This test will verify that the listed but resticted checkbox does not appear on the create subcomm form if the parent is non-listed </li>
 * <li><B>Step:</B> Create an internal restricted community - NON-LISTED </li>
 * <li><B>Step:</B> Click on the Create Subcommunity link </li>
 * <li><B>Verify:</B> Verify the listed but restricted checkbox does not apear on the create a subcomm form </li>
 * <li><B>Cleanup:</B> Delete the parent community </li> 
 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
 *</ul>
 */			

@Test(groups = {"regression","regressioncloud"} , enabled=false )
public void createSubcommFormNonlistedParent(){

	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();

	BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
	                                           .access(Access.RESTRICTED)	
	                                           .description("Parent community is nonlisted - RbL checklist should not appear on create subcomm form")	
	                                           .shareOutside(false)
	                                           .build();

	log.info("INFO: Log into Communities");
	ui.loadComponent(Data.getData().ComponentCommunities);
	ui.login(testUser);
	
	// check if catalog_card_view GK enabled
	boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);

	log.info("INFO: Bring up the Start A Community form");
	ui.closeGuidedTourSelectStartFromNew();

	log.info("INFO: Wait for the create form to load");
	ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

	log.info("INFO: Entering community name " + community.getName());
	this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

	log.info("INFO: Entering a description if we have one");
	if(community.getDescription() != null) {			
		ui.typeInCkEditor(community.getDescription());

		log.info("INFO: Select the Access level radio button 'Restricted'");
		driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();

		log.info("INFO: Click the Save button on the create form");
		driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
		
		if(isCardView){
			
			// get the UUID of the community from the weburl, this is needed to open the card.	  
			getCommUUIDFromWebURL(community);
		}
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		

		log.info("INFO: Click on the Create Subcommunity link from the Community Actions drop-down menu");
		Com_Action_Menu.CREATESUB.select(ui);

		log.info("INFO: Verify the 'listed but restricted' checkbox does NOT display");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.listedRestrictedCheckboxSubcomm),
				"ERROR: The listed but restricted checkbox appears on the create subcomm form");

		log.info("INFO: Verify the 'listed but restricted' checkbox text does NOT display");
		Assert.assertFalse(driver.isTextPresent(Data.getData().listedRestrictedCheckboxText),
				"ERROR: The listed but restricted checkbox appears on the create subcomm form");

		log.info("INFO: Verify the 'listed but restricted' conditional warning message does NOT display");
		Assert.assertFalse(driver.isTextPresent(Data.getData().listedRestrictedWarningMsg),
				"ERROR: The listed but restricted conditional warning message displays on the create subcomm form");

		log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");	
		ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();

		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);

		log.info("INFO: Cleanup - Removing community for Test case " + testName );
		community.delete(ui, testUser);

		ui.endTest();

	}
}

/**
 * <ul>
 * <li><B>Test Scenario:</B> Restricted but Listed - My Organization Communities View </li>
 * <li><B>Info:</B> Test to verify a listed subcommunity appears in the My Organization Communities view</li>
 * <li><B>Step:</B> Create a listed but restricted parent community </li>
 * <li><B>Step:</B> Create a listed but restricted SUBcommunity </li>
 * <li><B>Step:</B> Navigate to the My Organization Communities catalog view</li>
 * <li><B>Verify:</B> Verify the community appears in the My Organization Communities view</li>
 * <li><B>Cleanup:</B> Delete the communities </li> 
 * <li><a HREF="Notes://Parallan/85257863004CBF81/088F49BF479C68258525751B0060FA97/4EE76D83112689EA85257EC2003FD9AB"</a></li>
 *</ul>
 */			

@Test(groups = {"regression","regressioncloud"} , enabled=false )
public void myOrgCommViewListedSubcomm(){

	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();

	BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
	                                           .access(Access.RESTRICTED)	
	                                           .description("Parent community - RbL subcommunity test")	
	                                           .shareOutside(false)
	                                           .build();
	 		
	BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
	                                   .access(BaseSubCommunity.Access.RESTRICTED)
									   .UseParentmembers(false)
									   .description("Listed restricted subcomm test")
									   .build();
	
	log.info("INFO: Log into Communities");
	ui.loadComponent(Data.getData().ComponentCommunities);
	ui.login(testUser);
	
	// check if catalog_card_view GK enabled
	boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
	log.info("INFO: Bring up the Start A Community form");
	ui.closeGuidedTourSelectStartFromNew();

	log.info("INFO: Wait for the create form to load");
	ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

	log.info("INFO: Entering community name " + community.getName());
	this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());

	log.info("INFO: Entering a description if we have one");
	if(community.getDescription() != null) {
	ui.typeInCkEditor(community.getDescription());
	
	log.info("INFO: Select the Access level radio button 'Restricted'");
	driver.getSingleElement(CommunitiesUIConstants.CommunityAccessPrivate).click();
	
	log.info("INFO: Check the listed but restricted checkbox on the edit community form");
	driver.getFirstElement(CommunitiesUIConstants.listedRestrictedCheckboxComm).click();

	log.info("INFO: Click the Save button on the create form");
	driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
	
	if(isCardView){
		
		// get the UUID of the community from the weburl, this is needed to open the card.	  
		getCommUUIDFromWebURL(community);
	}
		
	// get the community link
	String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
	
	log.info("INFO: Click on the Create Subcommunity link from the Community Actions drop-down menu");
	Com_Action_Menu.CREATESUB.select(ui);
	
	log.info("INFO: Entering community name " + subCommunity.getName());
	this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).clear();
	this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(subCommunity.getName());
	
	log.info("INFO: Entering a description if we have one");
	if(subCommunity.getDescription() != null) {
	ui.typeInCkEditor(subCommunity.getDescription());
	
	log.info("INFO: Check the 'listed but restricted' checkbox");
	driver.getSingleElement(CommunitiesUIConstants.listedRestrictedCheckboxSubcomm).click();
	
	log.info("INFO: Save the subcommunity");
	driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();
	
	
	
	if ( isCardView) {
		// set the UUID on the subCommunity, so we can navigate to card
		String webUrl = this.driver.getCurrentUrl().split("&")[0];
	 
		// Strip the UUID out of the weburl, left communityUuid= for legacy
		subCommunity.setCommunityUUID(webUrl.split("\\?")[1]);
	}
	
			
	// get the subcommunity link
	String subcommunityLink = isCardView ? CommunitiesUI.getSubCommunityLinkCardView(subCommunity) : CommunitiesUI.getCommunityLink(community);
			
	log.info("INFO: Verify a link to the parent community appears on the top nav of the subcommunity");
	Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.tabbedNavParentCommunityName),
				"ERROR: No link to the parent community appears on the subcommunity overview page");
		
	log.info("INFO: Verify the subcommunity name appears on the top nav");
	Assert.assertTrue(driver.isTextPresent(subCommunity.getName()),
					"ERROR: Subcommunity name does not appear on the top nav");
	
	log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");	
	ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();
	
	log.info("INFO: Verify the internal listed community appears in the I'm an Owner catalog view");
	ui.fluentWaitPresentWithRefresh(subcommunityLink);
	Assert.assertTrue(ui.fluentWaitPresentWithRefresh(subcommunityLink),
			"ERROR: The community " + subCommunity.getName() + " does NOT appear in the I'm an Owner view");

	log.info("INFO: Clicking on the My Organization Communities link from the LeftNavigation");
	ui.goToPublicView(isCardView);

	log.info("INFO: Verify the internal listed community appears in the My Organization Communities catalog view");
	ui.fluentWaitPresentWithRefresh(subcommunityLink);
	Assert.assertTrue(ui.fluentWaitPresentWithRefresh(subcommunityLink),
			"ERROR: The community " + subCommunity.getName() + " does NOT appear in the My Organization Communities view");
	
	log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");
	ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();
		
	log.info("INFO: Open community");
	ui.clickLinkWait(communityLink);

	log.info("INFO: Cleanup - Removing community for Test case " + testName );
	community.delete(ui, testUser);

	ui.endTest();

	}
	}
}
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test will check various parts of the create Community form for an Internal Restricted Community</li>
	 * <li><B>Step:</B> Click on the Start a Community button</li>
	 * <li><B>Step:</B> Enter some text into the Name field</li>
	 * <li><B>Step:</B> On cloud: deselect the radio button for external restricted comm, On-prem: select the Restricted radio button </li>
	 * <li><B>Verify:</B> The Change Community Theme link does not appear on the form</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Verify:</B> The "+" sign to add external members does not display</li>
	 * <li><B>Verify:</B> The Web Address section does not appear on SC, but does appear on-prem</li>
	 * <li><B>Verify:</B> The link to upload a community image displays</li>
	 * <li><B>Step:</B> Add some text to the Description field </li>
	 * <li><B>Step:</B> Click on the Save button</li>
	 * <li><B>Step:</B> Navigate to the catalog view I'm an Owner</li>
	 * <li><B>Verify:</B> The community appears in the view, the Restricted icon & text displays, but not the shared externally icon</li>
	 * <li><B>Step:</B> Navigate to the I'm a Member or My Communities (CR3) catalog view</li>
	 * <li><B>Verify:</B> The community appears in the view, the Restricted icon & text displays, but not the shared externally icon</li>
	 * <li><B>Step:</B> Navigate to the I'm Following catalog view</li>
	 * <li><B>Verify:</B> The community appears in the view, the Restricted icon & text displays, but not the shared externally icon</li>
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void createInternalRestrictedCommForm(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String product = cfg.getProductName();
		Element widget;

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                           .access(Access.RESTRICTED)	
		                                           .description("Test the create community form for an internal restricted community.")
		                                           .rbl(false)
		                                           .shareOutside(false)
		                                           .build();

		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);			
		ui.login(testUser);		
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
			
		log.info("INFO: Bring up the Start a Community form");
		ui.closeGuidedTourSelectStartFromNew();

		log.info("INFO: Enter the community name " + community.getName());
		driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(community.getName());
		
		if(!isOnPremise){
			log.info("INFO: The environment is Smart Cloud");

			log.info("INFO: Uncheck the Access field option 'Allow people from outside of my organization to become members of this community'");
			driver.getFirstElement(CommunitiesUIConstants.CommunityallowExternalBox).click();
			
			//Message should not display per this defect: SC Daily: On Create Community form the orange warning box does not appear when "external access" checkbox deselected (128220)
			log.info("INFO: Verify the internal community message does not display when the 'External' access checkbox is unchecked");
			Assert.assertFalse(driver.isTextPresent(CommunitiesUIConstants.InternalCommunityMsg),
					"ERROR: The internal community message displays when the external access checkbox is unchecked");
			
			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			log.info("INFO: Verify there is no Web Address section on the form");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.CommunityHandle),
					"ERROR: Web Address section appears on the create form, but should not");

			//NOTE: To hide the link the HTML uses attribute style=display: none
			log.info("INFO: Verify the Change Community Theme link is hidden");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityThemeLinkHidden),
					"ERROR: Change Community Theme link displays when it should not");			

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
			log.info("INFO: Select the Access field option 'Restricted'");
			driver.getFirstElement(CommunitiesUIConstants.CommunityAccessPrivate).click();

			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			log.info("INFO: Verify the Web Address section displays on the form when on-prem");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CommunityHandle),
					"ERROR: Web Address does not appear on the create form, but should");
		}
		log.info("INFO: Verify the link to upload a community image exists");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.uploadCommunityImageLink),
				"ERROR: The link to upload a community image does not appear on the create form, but should");

		log.info("INFO: Verify the plus '+' sign to add external users does not display");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.addExtMemBtnHidden),
				"ERROR: The plus '+' sign to add external users should NOT display");	

		log.info("INFO: Enter text into the description field");
		ui.typeInCkEditor(community.getDescription());
			
		log.info("INFO: Click the Save button");
		driver.getFirstElement(CommunitiesUIConstants.SaveButton).click();
				
		if(isCardView){
			// get the UUID of the community from the weburl, this is needed to open the card.	  
			getCommUUIDFromWebURL(community);
		}
			
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		log.info("INFO: Go to the I'm an Owner view using the Communities link on the mega-menu");	
		ui.goToImAnOwnerViewUsingCommLinkOnMegamenu();

		log.info("INFO: Verify the community appears in the I'm an Owner catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the I'm an Owner view, but should");

		log.info("INFO: Locate the community");
		widget = ui.getCommunityWidget(community.getName(),isCardView);

		if (!isCardView) {
			log.info("INFO: Verify the Restricted icon displays for the community in the I'm an Owner view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedIcon).isVisible(),
				"ERROR: The Restricted icon does not appear for the community in the I'm an Owner view, but should");

			log.info("INFO: Verify the Restricted text displays for the community in the I'm an Owner view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedText).isVisible(),
				"ERROR: The Restricted text does not appear for the community in the I'm an Owner view, but should");

			log.info("INFO: Verify the shared externally icon does not display for the community in the I'm an Owner view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalIcon).isVisible(),
				"ERROR: The shared externally icon appears for the community in the I'm an Owner view, but should not");

			log.info("INFO: Verify the shared externally text does not display for the community in the I'm an Owner view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalText).isVisible(),
				"ERROR: The shared externally text appears for the community in the I'm an Owner view, but should not");
		}
		else {
		
			log.info("INFO: Verify the Restricted icon displays for the community in the I'm an Owner view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedCardIcon).isVisible(),
				"ERROR: The Restricted icon does not appear for the community in the I'm an Owner view, but should");
		}
		
		//go to default Catalog view i.e I'm a Member or My Communities (CR3)
		ui.goToDefaultCatalogView();
		//Verify the community is listed in the view
		log.info("INFO: Verify the community appears in the default catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the default Catalog view, but should");
		
		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);

		if (!isCardView) {
			log.info("INFO: Verify the Restricted icon displays for the community in the I'm a Member view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedIcon).isVisible(),
				"ERROR: The Restricted icon does not appear for the community in the I'm a Member view, but should");

			log.info("INFO: Verify the Restricted text displays for the community in the I'm a Member view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedText).isVisible(),
				"ERROR: The Restricted text does not appear for the community in the I'm a Member view, but should");

			log.info("INFO: Verify the shared externally icon does not display for the community in the I'm a Member view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalIcon).isVisible(),
				"ERROR: The shared externally icon appears for the community in the I'm a Member view, but should not");

			log.info("INFO: Verify the shared externally text does not display for the community in the I'm a Member view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalText).isVisible(),
				"ERROR: The shared externally text appears for the community in the I'm a Member view, but should not");
		}
		else {
			
			log.info("INFO: Verify the Restricted icon displays for the community in the I'm an Owner view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedCardIcon).isVisible(),
				"ERROR: The Restricted icon does not appear for the community in the I'm an Owner view, but should");
		}
		
		log.info("INFO: Clicking on the I'm Following catalog view link");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Verify the community appears in the I'm Following catalog view");
		ui.fluentWaitPresentWithRefresh(communityLink);
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: The community " + community.getName() + " does NOT appear in the I'm Following catalog view, but should");

		log.info("INFO: Get community widget");
		widget = ui.getCommunityWidget(community.getName(),isCardView);

		if (!isCardView) {
			log.info("INFO: Verify the Restricted icon displays for the community in the I'm Following view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedIcon).isVisible(),
				"ERROR: The Restricted icon does not appear for the community in the I'm Following view, but should");

			log.info("INFO: Verify the Restricted text displays for the community in the I'm Following view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedText).isVisible(),
				"ERROR: The Restricted text does not appear for the community in the I'm Following view, but should");

			log.info("INFO: Verify the shared externally icon does not display for the community in the I'm Following view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalIcon).isVisible(),
				"ERROR: The shared externally icon appears for the community in the I'm Following view, but should not");	

			log.info("INFO: Verify the shared externally text does not display for the community in the I'm Following view");
			Assert.assertFalse(widget.getSingleElement(CommunitiesUIConstants.ExternalText).isVisible(),
				"ERROR: The shared externally text appears for the community in the I'm Following view, but should not");
		}
		else {
			
			log.info("INFO: Verify the Restricted icon displays for the community in the I'm an Owner view");
			Assert.assertTrue(widget.getSingleElement(CommunitiesUIConstants.RestrictedCardIcon).isVisible(),
				"ERROR: The Restricted icon does not appear for the community in the I'm an Owner view, but should");
		}
	
		
		log.info("INFO: Open the community");
		ui.fluentWaitPresentWithRefresh(community.getName());
		ui.clickLinkWait(communityLink);

		log.info("INFO: Navigate to the full Members page");
		Community_LeftNav_Menu.MEMBERS.select(ui);

		log.info("INFO: Verify the Restricted icon & text displays for the community the full Members page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.RestrictedIconMembersPage),
				"ERROR: The Restricted icon & text do not appear on the full Members page, but should");
			
		if(!isOnPremise){
			log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization message displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org does not display");

		}else{
			log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization message displays");
			Assert.assertFalse(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org displays on-prem, but should not");
		}

		log.info("INFO:Cleanup - Removing community for Test case ");
		community.delete(ui, testUser);

		ui.endTest();		

	
}
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Panasonic UAT for Communities Plus Misc Regression Tests</li>
	 * <li><B>Info:</B> This test verifies the ability to add a user during community create for an Internal Restricted Community</li>
	 * <li><B>Step:</B> Click on the Start a Community button</li>
	 * <li><B>Step:</B> Enter some text into the Name & Description fields</li>
	 * <li><B>Step:</B> Click on the Access Advanced Features link</li>
	 * <li><B>Step:</B> Add a user to the community with Member access</li>
	 * <li><B>Step:</B> Click on the Save button</li>
	 * <li><B>Step:</B> Navigate to the full Members page</li>
	 * <li><B>Verify:</B> The user appears on the Members page & has Member access</li>
	 * <li><B>Verify:</B> The restricted icon appears on the Members page</li>
	 * <li><B>Verify:</B> SC only: the message the community cannot have users outside the org as members</li>
	 * <li><B>Step:</B> Click on the Add Members button</li>
	 * <li><B>Verify:</B> The plus sign "+" to add external members does not display
	 * <li><B>Cleanup:</B> Delete the community </li> 
	 * <li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E ">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"})
	public void createInternalRestrictedCommAddMembers(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String product = cfg.getProductName();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
		                                           .access(Access.RESTRICTED)	
		                                           .description("Add member to internal restricted comm during create test.")
		                                           .addMember(member)
		                                           .rbl(false)
		                                           .shareOutside(false)
		                                           .build();

		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentCommunities);			
		ui.login(testUser);			

		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
	
		log.info("INFO: Create a restricted Community using the UI");		
		if (isCardView) {
			community.createFromDropDownCardView(ui);
		}
		else {
			community.createFromDropDown(ui);
		}
		
		

		log.info("INFO: Navigate to the full Members page");
		Community_LeftNav_Menu.MEMBERS.select(ui);

		log.info("INFO: Collect the members text from member page");
		String memberInfo = ui.getMemberElement(member).getText();

		log.info("INFO: Verify that the member was added to the community");		
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
				"ERROR: User is not listed on the Members page");

		log.info("INFO: Verify the user access is Member");
		Assert.assertTrue(memberInfo.contains("Member"),
				"ERROR: User does not have Member access");

		log.info("INFO: Verify the Restricted icon & text displays for the community the full Members page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.RestrictedIconMembersPage),
				"ERROR: The Restricted icon & text do not appear on the full Members page, but should");

		if(!isOnPremise){
			log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization message displays");
			Assert.assertTrue(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org does NOT display, but should");

		}else{
			log.info("INFO: Verify the message that this community cannot have members from outside of the ogranization message does NOT display");
			Assert.assertFalse(driver.isTextPresent(Data.getData().internalCommMembersMsg),				
					"ERROR: The message that the community cannot have members from outside of the org displays on-prem, but should not");
		}
		log.info("INFO: Click on the Add Members button");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembersToExistingCommunity);

		log.info("INFO: Verify the plus '+' sign to add external users does not display");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.addExtMemBtnHidden),
				"ERROR: The plus '+' sign to add external users should NOT display");	

		log.info("INFO:Cleanup - Removing community for Test case ");
		community.delete(ui, testUser);

		ui.endTest();	
	}
	
	private void getCommUUIDFromWebURL (BaseCommunity community) {
		
		// get the UUID of the community from the weburl, and set it on the community.
		// this is needed to open the card.
		
		// Get url with params after UUID removed
		log.info("this.driver.getCurrentUrl()" + this.driver.getCurrentUrl());
		String webUrl = this.driver.getCurrentUrl().split("&")[0];
		log.info("webUrl" + webUrl) ;
		community.setWebAddress(webUrl);
	
		// Strip the UUID out of the weburl, left communityUuid= for legacy
		log.info("webUrl after setWebAddress" + webUrl) ;

		log.info("webUrl.split('\\?')[1]" + webUrl.split("\\?")[1]);
		community.setCommunityUUID(webUrl.split("\\?")[1]);
	}
}



