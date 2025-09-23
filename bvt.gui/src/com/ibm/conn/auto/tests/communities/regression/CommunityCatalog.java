package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
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
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityCatalog extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(CommunityCatalog.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser, testUser1, testUser2, testUser3;
	private APICommunitiesHandler apiOwner;
	private boolean isOnPremise;
	private String serverURL;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();

		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	
		//Check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
	}
	
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as owner (1 of 3)</li>
	 *<li><B>Info:</B> Test that an additional owner of a Public Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Public community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as an additional Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm an Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member or My Communities (CR3)</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Following</li>
	 *<li><B>Verify:</B> The community is not listed under I'm Invited</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AA5F277C802118E285257CAD0062E79D">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS OWNER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityOwner() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);

		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
	
		//Navigate to the I'm an Owner view
		ui.goToOwnerView(isCardView);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
					
		log.info("INFO: Validate an additional owner is able to see the community under I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional Owner is not able to see the community under I'm an Owner view!");
		
		// got to Default View for Cloud, and validate community exists
		if(!isOnPremise){
			gotoDefaultCloudView(isCardView ,communityLink);
		}
	   
		log.info("INFO: go to I'm Following view");
	    ui.goToIamFollowingView(isCardView,isOnPremise);
	    
	  	
	    log.info("INFO: Validate an additional owner is able to see the community under I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional owner is not able to see the community in I'm Following!!");
		
		log.info("INFO: go to Invited View");
		ui.goToInvitedView(isCardView);
				
		log.info("INFO: Validate the additional owner is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Additional owner is able to see the community in I'm Invited!!");
		
		log.info("INFO: go to public View");
		ui.goToPublicView(isCardView);
		
		ui.applyCatalogFilter(community.getName(), isCardView);

		log.info("INFO: Validate an additional owner is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: Additional Owner is not able to see the community under Public community!");
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
        log.info("INFO: Removing community");
		community.delete(ui, testUser2);

		ui.endTest();		
	}
		
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as owner (2 of 3)</li>
	 *<li><B>Info:</B> Test that an additional owner of a Moderated Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Moderated community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as an additional Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm an Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member or My Communities (CR3)</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Following</li>
	 *<li><B>Verify:</B> The community is not listed under I'm Invited</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AA5F277C802118E285257CAD0062E79D">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS OWNER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityOwner() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.MODERATED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
			
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);

		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
	
		//I'm Owner View
		ui.goToOwnerView(isCardView);
			
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
		

		log.info("INFO: Validate an additional owner is able to see the community under I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional Owner is not able to see the community under I'm an Owner view!");
		
		// got to Default View for Cloud, and validate community exists
		if(!isOnPremise){
			gotoDefaultCloudView(isCardView ,communityLink);
		}
		
		ui.goToIamFollowingView(isCardView, isOnPremise);
		

		log.info("INFO: Validate an additional owner is able to see the community under I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional owner is not able to see the community in I'm Following!!");
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		
		log.info("INFO: Validate the additional owner is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Additional owner is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);
		
		if(!getCatalogUIUpdatedGK()){
		   log.info("INFO: From Community catalog click on '100' link to show 100 communities");
		   ui.clickLinkWait(CommunitiesUIConstants.show100Comm);
		}

		ui.applyCatalogFilter(community.getName(), isCardView);

		log.info("INFO: Validate an additional owner is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Additional owner is not able to see the Moderated community under Public community!");
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser2);

		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as owner (3 of 3)</li>
	 *<li><B>Info:</B> Test that an additional owner of a Restricted Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Restricted community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as an additional Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm an Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member or My Communities (CR3)</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Following</li>
	 *<li><B>Verify:</B> The community is not listed under I'm Invited</li>
	 *<li><B>Verify:</B> The community is not listed under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/AA5F277C802118E285257CAD0062E79D">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS OWNER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void restrictedCommunityOwner() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
		
		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
	
		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
				
		log.info("INFO: Validate an additional owner is able to see the community under I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional Owner is not able to see the community under I'm an Owner view!");
	
		// got to Default View for Cloud, and validate community exists
		if(!isOnPremise){
			gotoDefaultCloudView(isCardView ,communityLink);
		}
		
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");	
		ui.goToIamFollowingView(isCardView, isOnPremise);
		
		log.info("INFO: Validate an additional owner is able to see the community under I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional owner is not able to see the community in I'm Following!!");
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the additional owner is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Additional owner is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);

		log.info("INFO: Validate an additional owner is not able to see the community under Public Communities view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						   "ERROR: Additional Owner is able to see his Restricted community in Public Communities view!!");
		
		//Navigating back to Owners view for deleting the community
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser2);

		ui.endTest();		
	}

	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as creator (1 of 3)</li>
	 *<li><B>Info:</B> Test that the creator of a Public Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Public community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Verify:</B> The presence of the community under I'm an Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member or My Communities (CR3)</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Following</li>
	 *<li><B>Verify:</B> The community is not listed under I'm Invited</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3D31EFF179E29D85257CAD005DB594">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS CREATOR</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityCreator() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
		
		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
	
		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
					
		log.info("INFO: Validate the creator is able to see the community under I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community under I'm an Owner view!");
		
		// got to Default View for Cloud, and validate community exists
		if(!isOnPremise){
			gotoDefaultCloudView(isCardView ,communityLink);
		}
		

		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);
		

		log.info("INFO: Validate the creator is able to see the community under I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community in I'm Following!!");
		
		if(getCatalogUIUpdatedGK()){
			//I've Created view
		   log.info("INFO: Clicking on the I'v Created link from the LeftNavigation");
		   ui.goToCreatedView(isCardView);
		   
		   // for Cloud, sort view by Date
		   sortViewByDateForCloud(isOnPremise, isCardView);
			   
		 }

		 log.info("INFO: Validate the creator is able to see the community under I've Created  view");
		  Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
									  "ERROR: The Creator is not able to see the community in I've Created !!");
	
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the creator is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Creator is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);

		ui.applyCatalogFilter(community.getName(), isCardView);

		log.info("INFO: Validate the creator is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community under Public community!");
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();		
	}
		
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as creator (2 of 3)</li>
	 *<li><B>Info:</B> Test that the creator of a Moderated Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Moderated community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Verify:</B> The presence of the community under I'm an Owner view</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member view or My Communities (CR3)</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Following view</li>
	 *<li><B>Verify:</B> The community is not listed under I'm Invited</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3D31EFF179E29D85257CAD005DB594">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS CREATOR</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityCreator() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.MODERATED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		log.info("community.getName()" + community.getName());
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
	
		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
					
		log.info("INFO: Validate the creator is able to see the community under I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community under I'm an Owner view!");
	
		//go to default Catalog view i.e I'm a Member or My Communities (CR3)
		ui.goToDefaultCatalogView();
		//Verify the community appears in the default Catalog view
		log.info("INFO: Validate the creator is able to see the community under default Catalogview");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						 "ERROR: The Creator is not able to see the community under default Catalog view!!");
		
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Validate the creator is able to see the community under I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community in I'm Following!!");
		
		// get the Catalog_UI_Updated GK flag
		boolean uiUpdatedGK = getCatalogUIUpdatedGK();
		if(uiUpdatedGK){
		   //I've Created view
		   log.info("INFO: Clicking on the I'v Created link from the LeftNavigation");
		   ui.goToCreatedView(isCardView);

		   log.info("INFO: Validate the creator is able to see the community under I've Created  view");
		   Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
								  "ERROR: The Creator is not able to see the community in I've Created !!");
		}
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the creator is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Creator is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);
		
		if(!uiUpdatedGK){
		   log.info("INFO: From Community catalog click on '100' link to show 100 communities");
		   ui.clickLinkWait(CommunitiesUIConstants.show100Comm);
		}
		
		ui.applyCatalogFilter(community.getName(), isCardView);
		
		log.info("INFO: Validate the creator is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community under Public community!");
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();	
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as creator (3 of 3)</li>
	 *<li><B>Info:</B> Test that the creator of a Restricted Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Restricted community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Verify:</B> The presence of the community under I'm an Owner</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member or My Communities (CR3)</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Following</li>
	 *<li><B>Verify:</B> The community is not listed under I'm Invited</li>
	 *<li><B>Verify:</B> The community is not listed under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5C3D31EFF179E29D85257CAD005DB594">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS CREATOR</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void restrictedCommunityCreator() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);

		//Wait for catalog to update with the new community
		log.info("INFO: Wait with refresh for catalog to contain our community");
		ui.fluentWaitPresentWithRefresh(communityLink);
	
		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
					
		log.info("INFO: Validate the creator is able to see the community under I'm an Owner view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community under I'm an Owner view!");
		
		// got to Default View for Cloud, and validate community exists
		if(!isOnPremise){
			gotoDefaultCloudView(isCardView ,communityLink);
		}
		
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Validate the creator is able to see the community under I'm Following view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The Creator is not able to see the community in I'm Following!!");
		
		if(getCatalogUIUpdatedGK()){
			//I've Created view
			log.info("INFO: Clicking on the I'v Created link from the LeftNavigation");
			ui.goToCreatedView(isCardView);

			if (!isOnPremise){
			   log.info("INFO: Click the view sort by option 'Date'");
			   ui.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
			}

			log.info("INFO: Validate the creator is able to see the community under I've Created  view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
					"ERROR: The Creator is not able to see the community in I've Created !!");
		}
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the creator is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Creator is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);

		log.info("INFO: Validate the creator is not able to see the community under Public Communities view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						   "ERROR: The Creator is able to see his Restricted community in Public Communities view!!");
		
		//Navigating back to Owners view for deleting the community
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);

		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
				
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();	
	}

	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as invited member (1 of 3)</li>
	 *<li><B>Info:</B> Test that a user invited to a Public community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Public community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Step:</B> Invite a new user to the community</li>
	 *<li><B>Step:</B> Logout the creator</li>
	 *<li><B>Step:</B> Login as the invited user</li>
	 *<li><B>Verify:</B> The user Invite count is greater then 0</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Invited view</li>
	 *<li><B>Verify:</B> The presence of Accept and Decline links under Invited community</li>
	 *<li><B>Verify:</B> The community name no longer exists in I'm Invited view once clicked on Accept link</li>
	 *<li><B>Verify:</B> Validation of reduction of number of invites after accepting the invite in I m Invited view</li>
	 *<li><B>Verify:</B> The community is not in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The community presence in the I'm Member view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Following view</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1522A6D7832CABBB85257CAD005F6AC3">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS INVITED MEMBER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityInvited(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int totalCount =0;
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName + rndNum)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
			
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Select Members from left nav
		log.info("INFO: Select Members from left nav");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Add members via Invite member
		log.info("INFO: Adding member via Invited member button");
		try {
			ui.inviteMemberCommunity(new Member(CommunityRole.MEMBERS, testUser3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
		
		//Logging out as a Creator
		ui.logout();
				
		//Load component and login as Invited member
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		// get the invitedUnreadCount 
		String invitedUnreadCount = isCardView ? CommunitiesUIConstants.InvitedCardUnreadCount : CommunitiesUIConstants.InvitedUnreadCount;
				
		//Validate the invite number is greater then 0
		log.info("INFO: Validate that the number of invites for this user is greater then 0");
		Assert.assertTrue(Integer.parseInt(ui.getElementText(invitedUnreadCount)) > 0,
						  "ERROR: Invite count for this user is not greater then 0");
		
		//Store the count value in a variable
		log.info("INFO: Store the count value in a variable");
		totalCount=Integer.parseInt(ui.getElementText(invitedUnreadCount));
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the invited member is able to see the community under I'm Invited view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Invited Member is not able to see the community in I'm Invited!!");
		
		if(!isCardView) {
			//Verifying the view contains Accept,Decline under Invited community
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getAcceptLink(community.getDescription())),
							"ERROR: Accept link is not present for Invited community");
		
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getDeclineLink(community.getDescription())),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(CommunitiesUI.getAcceptLink(community.getDescription()));
		
		}
		else {
			String acceptButtonLink = ui.getCommunityAcceptButtonLink(community);
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent( acceptButtonLink),
							"ERROR: Accept link is not present for Invited community: " + acceptButtonLink);
			
			String declineButtonLink = ui.getCommunityDeclineButtonLink(community);
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(declineButtonLink),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(acceptButtonLink);
			
		}
		
		log.info("INFO: Verify that community name no longer exists in I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),  
	         "ERROR: Community name is still existing in I'm Invited view");
		
		//Validating no of Invites reduces after accepting the Invite
		log.info("INFO: Validate that the number of invites for this user is reduced by one");
		if(totalCount == 1) 
		Assert.assertFalse(ui.isElementPresent(invitedUnreadCount),
				"ERROR: Number of Invites did not go down by 1");
		else
		Assert.assertTrue(totalCount > Integer.parseInt(ui.getElementText(invitedUnreadCount)),
	                     "ERROR: Number of Invites did not go down by 1");
	
				
		//Public Communities view
		log.info("INFO: Clicking on the Public Communities link from the LeftNavigation");
		ui.goToPublicView(isCardView);
		
		ui.applyCatalogFilter(community.getName(), isCardView);

		log.info("INFO: Validate the invited member is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Invited Member is not able to see the community under Public community!");
		
		//I'm an Owner view 
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		log.info("INFO: Validate the invited member is not able to see the community under I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						   "ERROR: Invited Member is able to see the community in I'm an Owner!");
		
		//I'm a Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);
		

		log.info("INFO: Validate the invited member is able to see the community under I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						   "ERROR: Invited member is not able to see community under I'm a Member!!");
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Validate the invited member is not able to see the community under I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
							"ERROR: Invited member is able to see the community in I'm Following!!");
		
		if(getCatalogUIUpdatedGK()){
		   //I've Created view
		   log.info("INFO: Clicking on the I've Created link from the LeftNavigation");
		   ui.goToCreatedView(isCardView);

		   log.info("INFO: Validate the invited member is not able to see the community under I've Created view");
		   Assert.assertFalse(driver.isElementPresent(communityLink),
									"ERROR: Invited member is able to see the community in I've Created!!");
		}
		
		//logging out as an Invited member			
		ui.logout();
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
						
		ui.endTest();	
	}	

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as invited member (2 of 3)</li>
	 *<li><B>Info:</B> Test that a user invited to a Moderated community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Moderated community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Step:</B> Invite a new user to the community</li>
	 *<li><B>Step:</B> Logout the creator</li>
	 *<li><B>Step:</B> Login as the invited user</li>
	 *<li><B>Verify:</B> The user Invite count is greater then 0</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Invited view</li>
	 *<li><B>Verify:</B> The presence of Accept and Decline links under Invited community</li>
	 *<li><B>Verify:</B> The community name no longer exists in I'm Invited view once clicked on Accept link</li>
	 *<li><B>Verify:</B> Validation of reduction of number of invites after accepting the invite in I m Invited view</li>
	 *<li><B>Verify:</B> The community is not in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The community presence in the I'm Member view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Following view</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1522A6D7832CABBB85257CAD005F6AC3">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS INVITED MEMBER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityInvited() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int totalCount=0;

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.MODERATED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName + rndNum)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
					
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Select Members from left nav
		log.info("INFO: Select Members from left nav");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Add members via Invite member
		log.info("INFO: Adding member via Invited member button");
		try {
			ui.inviteMemberCommunity(new Member(CommunityRole.MEMBERS, testUser3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
		
		//Logging out as a Creator
		ui.logout();
				
		//Load component and login as Invited member
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser3);
		
		// get the invitedUnreadCount 
		String invitedUnreadCount = isCardView ? CommunitiesUIConstants.InvitedCardUnreadCount : CommunitiesUIConstants.InvitedUnreadCount;
				
		//Validate the invite number is greater then 0
		log.info("INFO: Validate that the number of invites for this user is greater then 0");
		Assert.assertTrue(Integer.parseInt(ui.getElementText(invitedUnreadCount)) > 0,
						  "ERROR: Invite count for this user is not greater then 0");
		
		//Store the count value in a variable
		log.info("INFO: Store the count value in a variable");
		totalCount=Integer.parseInt(ui.getElementText(invitedUnreadCount));

		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the invited member is able to see the community under I'm Invited view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Invited Member is not able to see the community in I'm Invited!!");
		
		if (!isCardView) {
			//Verifying the view contains Accept,Decline links under Invited community
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getAcceptLink(community.getDescription())),
							"ERROR: Accept link is not present for Invited community");
		
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getDeclineLink(community.getDescription())),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(CommunitiesUI.getAcceptLink(community.getDescription()));

		}
		else {
			String acceptButtonLink = ui.getCommunityAcceptButtonLink(community);
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent( acceptButtonLink),
							"ERROR: Accept link is not present for Invited community: " + acceptButtonLink);
			
			String declineButtonLink = ui.getCommunityDeclineButtonLink(community);
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(declineButtonLink),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(acceptButtonLink);
		}
		
		log.info("INFO: Verify that community name no longer exists in I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),  
	         "ERROR: Community name is still existing in I'm Invited view");
	
		//Validating no of Invites reduces after accepting the Invite
		log.info("INFO: Validate that the number of invites for this user is reduced by one");
		if(totalCount == 1) 
			Assert.assertFalse(ui.isElementPresent(invitedUnreadCount),
				"ERROR: Number of Invites did not go down by 1");
		else
			Assert.assertTrue(totalCount > Integer.parseInt(ui.getElementText(invitedUnreadCount)),
	                     "ERROR: Number of Invites did not go down by 1");
		
		//Public Communities view
		log.info("INFO: Clicking on the Public Communities link from the LeftNavigation");
		ui.goToPublicView(isCardView);
				
		if(!getCatalogUIUpdatedGK()){
		   log.info("INFO: From Community catalog click on '100' link to show 100 communities");
		   ui.clickLinkWait(CommunitiesUIConstants.show100Comm);
		}
		
		ui.applyCatalogFilter(community.getName(), isCardView);
		
		log.info("INFO: Validate the invited member is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Invited Member is not able to see the community under Public community!");
		
		//I'm an Owner view 
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		log.info("INFO: Validate the invited member is not able to see the community under I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						   "ERROR: Invited Member is able to see the community in I'm an Owner!");
		
		//I'm a Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);

		log.info("INFO: Validate the invited member is able to see the community under I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						   "ERROR: Invited member is not able to see community under I'm a Member!!");
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);	

		log.info("INFO: Validate the invited member is not able to see the community under I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
							"ERROR: Invited member is able to see the community in I'm Following!!");
		
		if(getCatalogUIUpdatedGK()){
			//I've Created view
		   log.info("INFO: Clicking on the I've Created link from the LeftNavigation");
		   ui.goToCreatedView(isCardView);	

		   log.info("INFO: Validate the invited member is not able to see the community under I've Created view");
		   Assert.assertFalse(driver.isElementPresent(communityLink),
										"ERROR: Invited member is able to see the community in I've Created!!");
		}
		
		//logging out as an Invited member
		ui.logout();
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();		
	}	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as invited member (3 of 3)</li>
	 *<li><B>Info:</B> Test that a user invited to a Restricted community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Restricted community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the creator</li>
	 *<li><B>Step:</B> Invite a new user to the community</li>
	 *<li><B>Step:</B> Logout the creator</li>
	 *<li><B>Step:</B> Login as the invited user</li>
	 *<li><B>Verify:</B> The user Invite count is greater then 0</li>
	 *<li><B>Verify:</B> The presence of the community under I'm Invited view</li>
	 *<li><B>Verify:</B> The presence of Accept and Decline links under Invited community</li>
	 *<li><B>Verify:</B> The community name no longer exists in I'm Invited view once clicked on Accept link</li>
	 *<li><B>Verify:</B> Validation of reduction of number of invites after accepting the invite in I m Invited view</li>
	 *<li><B>Verify:</B> The community is not in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The community presence in the I'm a Member view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Following view</li>
	 *<li><B>Verify:</B> The community is not in the Public Communities view></li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1522A6D7832CABBB85257CAD005F6AC3">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS INVITED MEMBER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void restrictedCommunityInvited() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		int totalCount = 0;

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName + rndNum)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
					
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		// for Cloud, sort view by Date
		sortViewByDateForCloud(isOnPremise, isCardView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Select Members from left nav
		log.info("INFO: Select Members from left nav");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Add members via Invite member
		log.info("INFO: Adding member via Invited member button");
		try {
			ui.inviteMemberCommunity(new Member(CommunityRole.MEMBERS, testUser3));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Click on Send Invite button
		log.info("INFO: Click on the SendInviteButton");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
		
		//Logging out as a Creator
		ui.logout();
				
		//Load component and login as Invited member
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser3);
		
		// get the invitedUnreadCount 
		String invitedUnreadCount = isCardView ? CommunitiesUIConstants.InvitedCardUnreadCount : CommunitiesUIConstants.InvitedUnreadCount;
			
		//Validate the invite number is greater then 0
		log.info("INFO: Validate that the number of invites for this user is greater then 0");
		Assert.assertTrue(Integer.parseInt(ui.getElementText(invitedUnreadCount)) > 0,
						  "ERROR: Invite count for this user is not greater then 0");
		
		//Store the count value in a variable
		log.info("INFO: Store the count value in a variable");
		totalCount=Integer.parseInt(ui.getElementText(invitedUnreadCount));
		

		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);;
		
		log.info("INFO: Validate the invited member is able to see the community under I'm Invited view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Invited Member is not able to see the community in I'm Invited!!");
		
		if(!isCardView) {
			//Verifying the view contains Accept,Decline links under Invited community
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getAcceptLink(community.getDescription())),
							"ERROR: Accept link is not present for Invited community");
		
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getDeclineLink(community.getDescription())),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(CommunitiesUI.getAcceptLink(community.getDescription()));
		
			
		}
		else {
			String acceptButtonLink = ui.getCommunityAcceptButtonLink(community);
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent( acceptButtonLink),
							"ERROR: Accept link is not present for Invited community: " + acceptButtonLink);
			
			String declineButtonLink = ui.getCommunityDeclineButtonLink(community);
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(declineButtonLink),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(acceptButtonLink);
			
		}	
		
		log.info("INFO: Verify that community name no longer exists in I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),  
	         "ERROR: Community name is still existing in I'm Invited view");
	
		//Validating no of Invites reduces after accepting the Invite
		log.info("INFO: Validate that the number of invites for this user is reduced by one");
		if(totalCount == 1) 
			Assert.assertFalse(ui.isElementPresent(invitedUnreadCount),
				"ERROR: Number of Invites did not go down by 1");
		else
			Assert.assertTrue(totalCount > Integer.parseInt(ui.getElementText(invitedUnreadCount)),
	                     "ERROR: Number of Invites did not go down by 1");
		
		//Public Communities view
		log.info("INFO: Clicking on the Public Communities link from the LeftNavigation");
		ui.goToPublicView(isCardView);
		
		log.info("INFO: Validate the invited member is not able to see the community under Public Communities view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Invited Member is able to see the community under Public community!");
		
		//I'm an Owner view 
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
		
		log.info("INFO: Validate the invited member is not able to see the community under I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						   "ERROR: Invited Member is able to see the community in I'm an Owner!");
		
		//I'm a Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);

		log.info("INFO: Validate the invited member is able to see the community under I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						   "ERROR: Invited member is not able to see community under I'm a Member!!");
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);	

		log.info("INFO: Validate the invited member is not able to see the community under I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
							"ERROR: Invited member is able to see the community in I'm Following!!");
		
		//logging out as an Invited member
		ui.logout();
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();	
	}	
	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as member (1 of 3)</li>
	 *<li><B>Info:</B> Test that a member of a Public Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Public community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as a member</li>
	 *<li><B>Verify:</B> The community is not in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member</li>
	 *<li><B>Verify:</B> The community is not in the I'm Following view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Invited view</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1FA95EAC1195D88B85257CAD0061F1D4">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS MEMBER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommunityMember(){

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.PUBLIC)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
	
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
					
		log.info("INFO: Validate a member is not able to see the community under I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community under I'm an Owner view!");
	
		//I'm Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);
		
		log.info("INFO: Validate a member is able to see the community under I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: Member is not able to see the community under I'm a Member!!");
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Validate a member is not able to see the community under I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community in I'm Following!!");
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate a member is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);

		ui.applyCatalogFilter(community.getName(), isCardView);
		
		log.info("INFO: Validate a member is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Member is not able to see the community under Public community!");
		
		//logging out as a Member
		ui.logout();
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();		
	}
	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as a Member (2 of 3)
	 *<li><B>Info:</B> Test that a member of a Moderated Community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Moderated community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as the member</li>
	 *<li><B>Verify:</B> The community is not in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Following view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Invited view</li>
	 *<li><B>Verify:</B> The presence of the community under Public Communities view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1FA95EAC1195D88B85257CAD0061F1D4">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS MEMBER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void moderatedCommunityMember() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.MODERATED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		log.info("INFO: Login with user " + testUser1.getDisplayName());
		ui.login(testUser1);
	
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
					
		log.info("INFO: Validate a member is not able to see the community under I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community under I'm an Owner view!");
	
		//I'm Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);
		
		log.info("INFO: Validate a member is able to see the community under I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: Member is not able to see the community under I'm a Member!!");
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Validate a member is not able to see the community under I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community in I'm Following!!");
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the member is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);
		
		if(!getCatalogUIUpdatedGK()){
		   log.info("INFO: From Community catalog click on '100' link to show 100 communities");
		   ui.clickLinkWait(CommunitiesUIConstants.show100Comm);
		}

		ui.applyCatalogFilter(community.getName(), isCardView);

		log.info("INFO: Validate a member is able to see the community under Public Communities view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: A member is not able to see the community under Public community!");
		
		//Logging out as a Member
		ui.logout();
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Communities catalog views: Verify the views as member (3 of 3)</li>
	 *<li><B>Info:</B> Test that a member of a Restricted community is able to see the community in appropriate views</li>
	 *<li><B>Step:</B> Create a Restricted community using API & add one additional owner & one Member to the community</li>
	 *<li><B>Step:</B> Login as a member</li>
	 *<li><B>Verify:</B> The community is not in the I'm an Owner view</li>
	 *<li><B>Verify:</B> The presence of the community under I'm a Member view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Following view</li>
	 *<li><B>Verify:</B> The community is not in the I'm Invited view</li>
	 *<li><B>Verify:</B> The community is not in the Public Communities view</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1FA95EAC1195D88B85257CAD0061F1D4">TTT-ON-PREM: COMMUNITIES CATALOG VIEWS: VERIFY THE VIEWS AS MEMBER</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void restrictedCommunityMember() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test catalog views for community " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
												   .addMember(new Member(CommunityRole.OWNERS, testUser2))
												   .shareOutside(false)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//Get the community UUID
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);

		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//I'm Owner View
		log.info("INFO: Clicking on the I'm an Owner link from the LeftNavigation");
		ui.goToOwnerView(isCardView);
					
		log.info("INFO: Validate a member is not able to see the community under I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community under I'm an Owner view!");
	
		//I'm Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);
		
		log.info("INFO: Validate a member is able to see the community under I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: A member is not able to see the community under I'm a Member!!");
		
		//I'm Following view
		log.info("INFO: Clicking on the I'm Following link from the LeftNavigation");	
		ui.goToIamFollowingView(isCardView, isOnPremise);

		log.info("INFO: Validate a member is not able to see the community under I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: A member is able to see the community in I'm Following!!");
		
		//I'm Invited view
		log.info("INFO: Clicking on the I'm Invited link from the LeftNavigation");
		ui.goToInvitedView(isCardView);
		
		log.info("INFO: Validate the member is not able to see the community under I'm Invited view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: Member is able to see the community in I'm Invited!!");
		
		//Public Communities
		log.info("INFO: Clicking on the Public community link from the LeftNavigation");
		ui.goToPublicView(isCardView);

		log.info("INFO: Validate a member is not able to see the community under Public Communities view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: A member is able to see the community under Public community!");
		
		//Logging out as a Member
		ui.logout();
				
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Cleanup: Delete community
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
				
		log.info("INFO: Removing community");
		community.delete(ui, testUser);

		ui.endTest();		
	}
	
	/**
	 *<ul>
	 *<li>PTC_VerifyTagInCommunity</li>
	 *<li><B>Test Scenario:</B> Tag cloud in community catalog (1 of 2)</li>
	 *<li><B>Info:</B> Verify the community owner sees the tag in the tag cloud</li>
	 *<li><B>Step:</B> Create a Public community as owner using API with a tag to it</li>
	 *<li><B>Step:</B> Login as Owner & see if you are able to see the Tag</li>
	 *<li><B>Verify:</B> Verify the owner is able to see the Tag value in the tag cloud</li>
	 *<li><B>Step:</B> Click on the Tag cloud List link </li>
	 *<li><B>Step:</B> Click on the community tag </li>
	 *<li><B>Verify:</B> The community appears on the filtered by page </li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C9A133E3402540A585257C8D005616C5">TTT-GENERAL UI VERIFICATION: Verify tag cloud in Community Catalog</a></li>
	 *</ul>
	 *
	 *<li>To Do: additional validation needed</li>
	 *<li>additional test to click on tag click on list click on cloud</li>
	 *
	 *
	*/
	@Test(groups = {"regression"})
	public void tagCloudListLinks(){
		
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
									.access(Access.PUBLIC)
									.tags(Data.getData().communityTestTag )
									.description("Test community for " + testName )
									.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);	
	
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		if (!isCardView) {
			//Verify the Tags cloud appears in the view
			log.info("INFO: Validate the test cloud is appearing on the screen");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(CommunitiesUIConstants.TagOfCloud),
						  "ERROR: Cloud is not appearing after the Tag");
		
			//Verify the community tag entry appears in the tag cloud.
			log.info("INFO: Verify the community tag appears in the tag cloud.");
			Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.CatalogViewCloudTag).getText(), Data.getData().communityTestTag.toLowerCase(),
				"ERROR: Community tag does not appear in the tag cloud.");
		

			//Click on the List link
			log.info("INFO: Click on the Tags List link");
			ui.clickLinkWithJavascript(CommunitiesUIConstants.ListUnderTag);


			//Verify the community tag appears in the List view
			log.info("INFO: Verify the community tag appears in the List view");
			Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.CatalogViewListTag).getText(), Data.getData().communityTestTag.toLowerCase(),
				"ERROR: Tag is not appearing under List ");
		

			//Click on the community tag entry
			log.info("INFO: Click on the community tag");
			ui.clickLinkWait(CommunitiesUIConstants.CatalogViewListTag);
		}
		else {
			
			//
			// on CR4, only the Tags List is supported.
			//
			
			// Navigate to My Communities view
			ui.goToMyCommunitiesView(isCardView);
			
			// Wait for catalog to update with the new community
			log.info("INFO: Wait with refresh for catalog to contain our community");
			ui.fluentWaitPresentWithRefresh(communityLink);
			
			// Open Filter panel
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView); // filter side-bar expand
			
			// verify Tag list is present
			log.info("INFO: Verify the community Tags list appears in the Filter");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CatalogCardViewListTag),
					"ERROR:Tag list is not visible");
			
			// verify community tag is present
			log.info("INFO: Verify the community tag appears in the Filter List view");
			String tagLink = CommunitiesUI.getCommunityFilterTagLink(Data.getData().communityTestTag.toLowerCase());
			Assert.assertTrue(driver.isElementPresent(tagLink),			
					"ERROR:Tag is not visible " +tagLink);
			
			log.info("INFO:click on the Tag");
			driver.getFirstElement(tagLink).click();	
		
			// verify community tag is present in breadcrumb
			log.info("INFO: Verify the tag appears in the breadcrumb");
			String breadcrumbLink = CommunitiesUI.getCommunityFilterBreadcrumbsTagLink(Data.getData().communityTestTag.toLowerCase());
			Assert.assertTrue(driver.isElementPresent(breadcrumbLink),			
					"ERROR:Tag is not visible in breadcrumbs " +breadcrumbLink);
		}

		//Verify the community appears on the results page
		log.info("INFO: Verify the community appears on the filtered by results page");
		Assert.assertTrue(driver.isElementPresent(communityLink),
				"ERROR: The community associated with the tag is not visible");

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li>PTC_VerifyTagInCommunity</li>
	 *<li><B>Test Scenario:</B> Tag cloud in community catalog (2 of 2)</li>
	 *<li><B>Info:</B> Verify the owner is able to see the tag in each of the catalog views except I'm Invited</li>
	 *<li><B>Step:</B> Create a Public community as owner using API with a tag to it</li>
	 *<li><B>Step:</B> Login as Owner & go to different view & check if you able to see  the Tag</li>
	 *<li><B>Verify:</B> Verify that owner is able to see Tag value in I'm an Owner, I'm Member , I'm Following , Public community</li>
	 *<li><B>Verify:</B> Verify that Tag is not coming in I'm Invited community</li>
	 *<li><B>Step:</B> Cleanup: delete the community</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C9A133E3402540A585257C8D005616C5">TTT-GENERAL UI VERIFICATION: Verify tag cloud in Community Catalog</a></li>
	 *</ul>
	 *
	 *<li>To Do:add invited</li>
	*/
	@Test(groups = {"regression"})
	public void tagCatalogViews() {
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
									.access(Access.PUBLIC)
									.tags(Data.getData().communityTestTag )
									.description("Test community for " + testName )
									.build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//Go to Owners view
		log.info("INFO: Go to I'm an Owners view");
		ui.goToOwnerView(isCardView);
				
		if (isCardView) {
			// wait for community to display, so the tags will be displayed in Tag filter.
			log.info("INFO: Wait with refresh for catalog to contain our community");
			ui.fluentWaitPresentWithRefresh(communityLink);
		}
		else {
			//Wait for community to get loaded.
			log.info("INFO: Wait for community to load");
			ui.fluentWaitPresentWithRefresh(CommunitiesUIConstants.CatalogViewCloudTag);
		}

		//Verify the community tag appears in Tags widget in this view
		verifyTagExistInTagList(isCardView);
		
		// Go to I'm Following
		log.info("INFO: Go to I'm Following");
		ui.goToIamFollowingView(isCardView, isOnPremise);
		//Verify the community tag appears in Tags widget in this view
		verifyTagExistInTagList(isCardView);
		
		//go to default Catalog view i.e I'm a Member or My Communities (CR3)
		ui.goToDefaultCatalogView();
		//Verify the community tag appears in Tags widget in this view
		verifyTagExistInTagList(isCardView);
		
						
		//Go to I'm Invited
		log.info("INFO: Go to I'm Invited view");
		ui.goToInvitedView(isCardView);
		if (isCardView){
			
			// verify filter panel is not displayed in Invited View.
			log.info("INFO: Verify Filter panel is not displayed in Invited View");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.filterSideBarExpandCardView),
					"ERROR: Filter is not visible in invited view");
			
		}
		else {
			//Verify the community tag does not appear in the view
			log.info("INFO: Verify tag is not present in the I'm Invited view");
			Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.CatalogViewCloudTag),
						   "ERROR: Tag appears in the I'm Invited view");
		}
							
		//Go to I'm Public
		log.info("INFO: Go to Public view");
		ui.goToPublicView(isCardView);
		//Verify the community tag appears in Tags widget in this view
		verifyTagExistInTagList(isCardView);
		
		
		//Go to Owners view
		log.info("INFO: Go to I'm an Owners view");
		ui.goToOwnerView(isCardView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Panasonic UAT: Invite user to an external restricted community</li>
	 *<li><B>Info:</B> NOTE: For now this test will only be run on the cloud. External restricted communities not supported by default on-prem. </li>
	 *<li><B>Step:</B> Create an external restricted community using the API</li>
	 *<li><B>Step:</B> Login as a the community creator</li> 
	 *<li><B>Step:</B> Open the community & navigate to the Members page</li>
	 *<li><B>Step:</B> Click on the Invite Members button & invite a user </li>
	 *<li><B>Step:</B> Log out as the community creator & log in as the invited user</li>
	 *<li><B>Step:</B> Check the number of invites listed next to the I'm Invited view</li>
	 *<li><B>Step:</B> Navigate to the I'm Invited view</li>
	 *<li><B>Verify:</B> The Accept & Decline links appear for the community </li>
	 *<li><B>Step:</B> Click on the Accept link</li>
	 *<li><B>Verify:</B> 1) The accept invite confirmation msg displays, 2) the community no longer appears in the view, 3) invite count has decreased by 1</li>
	 *<li><B>Step/Verify:</B> Navigate to the Public Communities view & Verify the invited user sees the community</li>
	 *<li><B>Step/Verify:</B> Navigate to the I'm an Owner view & Verify the invited user does NOT see the community</li>
	 *<li><B>Step/Verify:</B> Navigate to the I'm a Member view & Verify the invited user sees the community</li>
	 *<li><B>Step/Verify:</B> Navigate to the I'm Following view & Verify the invited user does NOT see the community</li>
	 *<li><B>Step:</B> Log out as the invited user & log in as the community creator</li>
	 *<li><B>Step/Verify:</B> Navigate to the Members page of the community & Verify the invited user is listed as a 'member'</li>
	 *<li><B>Verify:</B> The user who joined the community is listed on the Members page & that they have 'Member' access</li>
	 *<li><B>Step:</B> Cleanup: Delete the community</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/87BDF8D08E0E560C85257F33004CA809/BB738C536AEAAFE985257D88005E998E">TTT-PANASONIC UAT FOR COMMUNITIES PLUS MISC REGRESSION TESTS</a></li>
	 *</ul>
	 */	
	
	@Test(groups = {"regressioncloud"} , enabled=false )
	public void externalRestrictedCommunityInvited() {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		int totalCount = 0;

		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
												   .access(Access.RESTRICTED)	
												   .tags(Data.getData().commonTag + rndNum )
												   .description("Test the I'm Invited catalog view for community " + testName + rndNum)
												   .shareOutside(true)
												   .build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		//GUI
		//Load component and login
		log.info("INFO: Log into communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// get the Catalog Card View GK flag
		boolean isCardView = getCatalogCardGK();
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);

		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Select Members from left nav
		log.info("INFO: Select Members from the left nav");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		//Click on the Invite Members button & invite a user
		log.info("INFO: Click on the Invite Members button & invite a user");
		try {
			ui.inviteMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Click on the Send Invitations button
		log.info("INFO: Click on the Send Invitations button");
		ui.clickLink(CommunitiesUIConstants.SendInvitesButton);
		
		//Logging out as the community Creator
		log.info("INFO: Log out as the community creator");
		ui.logout();
		ui.close(cfg);
				
		//Log into communities as the invited user
		log.info("INFO: Log into communities as the invited user");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser1);
				
		//Navigate to the I'm Invited catalog view
		log.info("INFO: Navigate to the I'm Invited catalog view");
		ui.goToInvitedView(isCardView);
		
		// get the invitedUnreadCount 
		String invitedUnreadCount = isCardView ? CommunitiesUIConstants.InvitedCardUnreadCount : CommunitiesUIConstants.InvitedUnreadCount;
						
		//Validate the invite number is greater then 0
		log.info("INFO: Validate that the number of invites for this user is greater then 0");
		Assert.assertTrue(Integer.parseInt(ui.getElementText(invitedUnreadCount)) > 0,
								  "ERROR: Invite count for this user is not greater then 0");
			
		//Store the invite count value in a variable
		log.info("INFO: Store the invite count value in a variable");
		totalCount=Integer.parseInt(ui.getElementText(CommunitiesUIConstants.InvitedUnreadCount));
		
		
		if (!isCardView) {
			//Verify the invited user is able to see the community in the I'm Invited view
			log.info("INFO: Verify the invited user is able to see the community in the I'm Invited view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						  "ERROR: The invited user does NOT see the community in the I'm Invited view");
		
			//Verifying the view contains Accept,Decline links under the community
			log.info("INFO: Verify the Accept link appears for the community the user is invited to");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getAcceptLink(community.getDescription())),
							"ERROR: Accept link is not present for the community");
		
			log.info("INFO: Verify the Decline link appears for the community the user is invited to");
			Assert.assertTrue(ui.isElementPresent(CommunitiesUI.getDeclineLink(community.getDescription())),
				"ERROR: Decline link is not present for the community");
		
			//Click the Accept link 
			log.info("INFO: Click the Accept link");
			ui.clickLinkWait(CommunitiesUI.getAcceptLink(community.getDescription()));
		
			//Verify the accept invite confirmation message displays
			log.info("INFO: Verify the accept invite confirmation message displays");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.acceptInviteConfirmationMsg),
				"ERROR: The accept invite confirmation message did not display");
		
			//Verify the community no longer appears in the I'm Invited view
			log.info("INFO: Verify that community no longer appears in the I'm Invited view");
			Assert.assertFalse(driver.isElementPresent(communityLink),  
		         "ERROR: Community still appears in I'm Invited view");
			
		}
		else {
			String acceptButtonLink = ui.getCommunityAcceptButtonLink(community);
			log.info("INFO: Verify the view contains Accept link under Invited community");
			Assert.assertTrue(ui.isElementPresent( acceptButtonLink),
							"ERROR: Accept link is not present for Invited community: " + acceptButtonLink);
			
			String declineButtonLink = ui.getCommunityDeclineButtonLink(community);
			log.info("INFO: Verify the view contains Decline link under Invited community");
			Assert.assertTrue(ui.isElementPresent(declineButtonLink),
				"ERROR: Decline link is not present for Invited community");
		
			//Clicking Accept link under I'm Invited view
			log.info("INFO: Clicking on Accept link in community under I'm Invited view");
			ui.clickLinkWait(acceptButtonLink);
			
			log.info("INFO: Verify that community name no longer exists in I'm Invited view");
			Assert.assertFalse(driver.isElementPresent(communityLink),  
		         "ERROR: Community name is still existing in I'm Invited view");
		}
		
		//Verify the number of invites is reduced by 1 after accepting the invite
		log.info("INFO: Verify the number of invites is reduced by 1 after accepting the invite");
		if(totalCount == 1) 
			Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.InvitedUnreadCount),
				"ERROR: The number of invites did not go down by 1");
		else
			Assert.assertTrue(totalCount > Integer.parseInt(ui.getElementText(CommunitiesUIConstants.InvitedUnreadCount)),
	                     "ERROR: The number of invites did not go down by 1");
			
		//Navigate to the Public Communities view
		log.info("INFO: Navigate to the Public/<org> Communities view");
		ui.goToPublicView(isCardView);
		
		//Verify the invited user does not see the community in the Public/<org> Communities view
		log.info("INFO: Verify the invited user does not see the community in the Public/<org> Communities view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						  "ERROR: The invited user sees the community in the Public/<org> Communities view");
		
		//Navigate to the I'm an Owner view 
		log.info("INFO: Navigate to the I'm an Owner view");
		ui.goToOwnerView(isCardView);
		
		//Verify the invited user does not see the community in the I'm an Owner view
		log.info("INFO: Verify the invited user does not see the community in the I'm an Owner view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
						   "ERROR: The invited user is able to see the community in I'm an Owner view");
		
		//Navigate to the I'm a Member view
		log.info("INFO: Navigate to the I'm a Member view");
		ui.goToMemberView(isCardView);

		//Verify the invited user sees the community in the I'm a Member view
		log.info("INFO: Verify the invited user sees the community in the I'm a Member view");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
						   "ERROR: The invited user does not see the community in the I'm a Member view");
		
		//Navigate to the I'm Following view
		log.info("INFO: Navigate to the I'm Following view");
		ui.goToIamFollowingView(isCardView, isOnPremise);

		//Verify the invited user does not see the community in the I'm Following view
		log.info("INFO: Verify the invited user does not see the community in the I'm Following view");
		Assert.assertFalse(driver.isElementPresent(communityLink),
							"ERROR: The invited user is able to see the community in the I'm Following view");
		
		//logging out as an Invited member
		log.info("INFO: Log out as the invited user");
		ui.logout();
		ui.close(cfg);
				
		//Load component and login
		log.info("INFO: Log into Communities as the community creator");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);
		
		//Open the community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	
		
		//Navigate to the Members page
		log.info("INFO: Navigate to the Members page");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
				
		//Verify the invited user is listed as a member
		log.info("INFO: Collect the members role from the member page");
		String memberInfo = ui.getMemberElement(member).getText();
		
		log.info("INFO: Verify the user has member access");
		Assert.assertTrue(memberInfo.contains("Member"),
						  "ERROR: User does not have Member role");
		
		log.info("INFO: Verify the user is a member of the community");		
		Assert.assertTrue(ui.isElementPresent("link=" + member.getUser().getDisplayName()),
						  "ERROR: User is not a member of the community");
		
		//Click on the Invitations tab
		log.info("INFO: Click on the Invitations tab");
		ui.clickLinkWait(CommunitiesUIConstants.InvitationsTab);
		
		//Verify the invited user is no longer listed
		log.info("INFO: Verify the invited user is no longer listed on the Invitations tab");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.resendInviteMsg),
				"ERROR: The invited user still appears on the Invitations tab");
		
		//Cleanup: Delete community		
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();	
	}	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> New to Communities Welcome Box Links test</li>
	 * <li><B>Info:</B> This test will verify help displays for each of the links in the 'New to Communities?' section</li>
	 * <li><B>Step:</B> From the catalog view click on the 'Join a community' link</li>
	 * <li><B>Verify:</B> Help on how to join a community displays</li>
	 * <li><B>Step:</B> From the catalog view click on the 'Participate in your community's forum' link</li>
	 * <li><B>Verify:</B> Help on how to add a topic to a community forum displays</li>
	 * <li><B>Step:</B> From the catalog view click on the 'Share useful web resources' link</li>
	 * <li><B>Verify:</B> Help on how to add a bookmark from a web page to a community displays</li>
	 * <li><B>Step:</B> From the catalog view click on the 'creating a community' link</li>
	 * <li><B>Verify:</B> Help on how to create a community displays</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void newToCommunitiesHelpLinks(){
		
			String gk_hideWelcomeBox = Data.getData().gk_hideWelcomeBox_flag;			
			
			log.info("INFO: Log into communities");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser);
			
			Boolean hikariTheme = ui.checkGKSetting(Data.getData().gk_hikariTheme_flag);
			Boolean hideWelcomeBox = ui.checkGKSetting(Data.getData().gk_hideWelcomeBox_flag);
			
			if (hikariTheme){
				log.info("INFO: Hikari theme is configured");
				log.info("INFO: Check Gatekeeper setting to hide the welcome box");
				if(ui.checkGKSetting(Data.getData().gk_hideWelcomeBox_flag)){
					log.info("INFO: Gatekeeper flag " + gk_hideWelcomeBox + " is set to " + hideWelcomeBox + " - skipping this test");
				}else {
					log.info("INFO: Check GateKeeper setting for Guided Tours, if enabled, close the Community Guided Tour popup window");
					if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag))

						ui.clickLinkWait(CommunitiesUIConstants.closeCommGuidedTour);

					log.info("INFO: Test the Welcome box links");
					this.clickWelcomeBoxLinks();
				}
			}else {
				log.info("INFO: Gen4 theme is configured");
				log.info("INFO: Check GateKeeper setting for Guided Tours, if enabled, close the Community Guided Tour popup window");
				if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag))

					ui.clickLinkWait(CommunitiesUIConstants.closeCommGuidedTour);

				log.info("INFO: Test the Welcome box links");
				this.clickWelcomeBoxLinks();
			}

			ui.endTest();	
	}
	
	/**
	 * This method will verify for the community tag exists
     */
	private void verifyTagExistInTagList(boolean isCardView){
		
		if (isCardView){
			
			// for CR4, look for the tag in the  Tag List.
			if (!driver.isElementPresent( CommunitiesUIConstants.CatalogCardFilterExpanded)){
				log.info("INFO: Open Filter");
				ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);
			}
			
			// verify community tag is present
			log.info("INFO: Verify the community tag appears in the Filter List view");
			String tagLink = CommunitiesUI.getCommunityFilterTagLink(Data.getData().communityTestTag.toLowerCase());
			Assert.assertTrue(driver.isElementPresent(tagLink),			
					"ERROR:Tag is not visible " +tagLink);
			
		}
		else {
			
			// for pre-CR4, look for the tag exist in Cloud Tag view.
			//Verify the community appears in the default Catalog view
			log.info("INFO: Verify the community tag appears in the view");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.CatalogViewCloudTag),
						  "ERROR: Tag does not appear in the Catalog view");
		}

	}
	/**
	 * This method will click on each of the welcome box links and verify the help page displays
     */
	private void clickWelcomeBoxLinks(){

		log.info("INFO: Click on the Join a Community");
		ui.clickLinkWait(CommunitiesUIConstants.newToCommJoinCommHelpLink);

		String Handle = driver.getWindowHandle();				

		log.info("INFO: Focus is placed on the help browser window");
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);

		log.info("INFO: Verify the Joining communities help displays");
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().joinCommHelpOnprem),
				"ERROR: Joining communities help did not display");

		log.info("INFO: Close the help browser window and switch back to the original browser window");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);

		log.info("INFO: Click on the 'Participate in your community's forum' link");
		ui.clickLinkWait(CommunitiesUIConstants.newToCommForumHelpLink);

		log.info("INFO: Focus is placed on the help browser window");
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);

		log.info("INFO: Verify the Adding topics to a forum help displays");
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().participateInCommForumHelpOnprem),
				"ERROR: Adding topics to a forum help did not display");

		log.info("INFO: Close the help browser window and switch back to the original browser window");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);

		log.info("INFO: Click on the 'Share useful web resources");
		ui.clickLinkWait(CommunitiesUIConstants.newToCommShareResourcesHelpLink);

		log.info("INFO: Focus is placed on the help browser window");
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);

		log.info("INFO: Verify the Adding a bookmark from a web page to a community help displays");
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().shareWebResourcesHelpOnprem),
				"ERROR: Adding a bookmark from a web page to a community help did not display");

		log.info("INFO: Close the help browser window and switch back to the original browser window");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);

		log.info("INFO: Click on the 'creating your own community' link");
		ui.clickLinkWait(CommunitiesUIConstants.newToCommCreateCommHelpLink);

		log.info("INFO: Focus is placed on the help browser window");
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().IBMConnectionsCloud);

		log.info("INFO: Verify creating a community help displays");
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().createACommHelpOnprem),
				"ERROR: Creating a community help did not display");

		log.info("INFO: Close the help browser window and switch back to the original browser window");
		ui.close(cfg);
		driver.switchToWindowByHandle(Handle);
	}
	
	/**
	 * This method will select sort order by Date tab, used by Cloud only
     */
	private void sortViewByDateForCloud( boolean isOnPremise, boolean isCardView) {
		
		//If environment is Cloud & the catalog UI GK flag is enabled then click on the sort by 'Date' tab
		//Content on cloud does not get cleared, community may not appear when sorted by Recently Visited due to all the communities
		//Community is easily found by clicking on Date
		if (!isOnPremise){
			if(getCatalogUIUpdatedGK()){
						
				if (!isCardView){
					log.info("INFO: Click the view sort by option 'Date'");
					ui.clickLinkWait(CommunitiesUIConstants.catalogViewSortByDateTab);
				}
			}
		}
		
	}
	
	/**
	 * This method will navigate to the correct default view, and verify community exists.
	 * it is used by Cloud only
     */
	private void gotoDefaultCloudView( boolean isCardView, String communityLink ) {
		
		if (getCatalogUIUpdatedGK()){
			
			// go to My Communities view
			log.info("INFO: Clicking on the My Communites link from the LeftNavigation");
			ui.goToMyCommunitiesView(isCardView);

			log.info("INFO: Validate an additional owner is able to see the community in My Communities view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
							"ERROR: Additional Owner is not able to see the community in My Communities view!!");


		}
		else {
			//go to I'm Member view
			log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
			ui.goToMemberView(isCardView);

			//Verify the community appears in the I'm a Member view
			log.info("INFO: Validate an additional owner is able to see the community under I'm a Member view");
			Assert.assertTrue(ui.fluentWaitPresentWithRefresh(communityLink),
				"ERROR: Additional Owner is not able to see the community under I'm a Member!!");
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


