package com.ibm.conn.auto.tests.communities;


import java.util.ArrayList;
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
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_ComponentPack_Communities extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_ComponentPack_Communities.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private Member member;
	private String serverURL;
	private APICommunitiesHandler apiOwner, apiOwner1, apiFollower;
	private User testUser1, testUser2, testUser3, testUser4;
	private APIForumsHandler forumApiOwner;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		member = new Member(CommunityRole.MEMBERS, testUser2);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
		apiOwner1 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()),
				testUser3.getPassword());
		
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		forumApiOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My Community-Recently Visited for I'm member and Validate the Communities</li>
	 * <li><B>Step:</B>Create 4 Communities via API of access type Public, Moderated, Restricted, Restricted but Listed for UserA as Owner and UserB as member.</li>
	 * <li><B>Step:</B>Login via UserB in Connections.</li>
	 * <li><B>Step:</B>Navigate to all 4 communities created above in sequence</li>
	 * <li><B>Step:</B>Navigate to My Communities page</li>
	 * <li><B>Verify:</B>Verify By Default 'Recently Visited' Option is Selected</li>
	 * <li><B>Step:</B>Select I'm Member option from Filter on left panel of page</li>
	 * <li><B>Verify:</B>Verify All 4 Communities got filtered and Sequence is proper</li>
	 * <li><B>Step:</B>Delete All 4 community</li>
	 * </ul>
	 */
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyRecentMember() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());		
		String testName = ui.startTest();
		List <BaseCommunity> communities = new ArrayList<BaseCommunity>();
		
		BaseCommunity community = new BaseCommunity.Builder(testName +"_PUB_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.build();
		BaseCommunity community2 = new BaseCommunity.Builder(testName +"_MOD_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.MODERATED)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.build();
		BaseCommunity community3 = new BaseCommunity.Builder(testName +"_REST_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.shareOutside(false).build();
		BaseCommunity community4 = new BaseCommunity.Builder(testName +"_RBL_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.rbl(true).shareOutside(false).build();
		
		logger.strongStep("Add all communities to the list of communities");
		log.info("INFO: Add all communities to the list of communities");
		communities.add(community);
		communities.add(community2);
		communities.add(community3);
		communities.add(community4);

		// create community
		logger.strongStep("Create a Public Community " + community.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Public Community " + community.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI1 = community.createAPI(apiOwner);
		
		logger.strongStep("Create a Moderated Community " + community2.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Moderated Community " + community2.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI2 = community2.createAPI(apiOwner);
		
		logger.strongStep("Create a Restricted Community " + community3.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Restricted Community " + community3.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI3 = community3.createAPI(apiOwner);
		
		logger.strongStep("Create an RBL Community " + community4.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create an RBL Community " + community4.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI4 = community4.createAPI(apiOwner);
		
		// add the UUID to community
		logger.strongStep("Get the UUID of the Public Community: " + community.getName());
		log.info("INFO: Get the UUID of the Public Community: " + community.getName());
		community.getCommunityUUID_API(apiOwner, comAPI1);
		
		logger.strongStep("Get the UUID of the Moderated Community: " + community2.getName());
		log.info("INFO: Get the UUID of the Moderated Community: " + community2.getName());
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		logger.strongStep("Get the UUID of the Restricted Community: " + community3.getName());
		log.info("INFO: Get the UUID of the Restricted Community: " + community3.getName());
		community3.getCommunityUUID_API(apiOwner, comAPI3);
		
		logger.strongStep("Get the UUID of the RBL Community: " + community4.getName());
		log.info("INFO: Get the UUID of the RBL Community: " + community4.getName());
		community4.getCommunityUUID_API(apiOwner, comAPI4);
		
		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser2.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		ui.login(testUser2);

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community2.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community3.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community4.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);
		
		log.info("Go to my communities");
		ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
		
		logger.strongStep("Validate 'Recently Visited' is Selected by Default.");
		log.info("INFO: Validate 'Recently Visited' is Selected by Default.");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.viewSelectorDropDownCardView).contains("Recently Visited"), "'Recently Visited' is not Selected By Default.");
		
		//ui.goToMemberCardView();
		driver.turnOffImplicitWaits();
		
		logger.strongStep("Expand the Side Bar if it is not already expanded");
		log.info("INFO: Expand the Side Bar if it is not already expanded");
		if (ui.isElementVisible(CommunitiesUIConstants.filterSideBarNotExpanded)) {
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView); // filter side-bar expand
		}
		
		logger.strongStep("Click on the 'I'm a Member' view in the Side Bar");
		log.info("INFO: Click on the 'I'm a Member' view in the Side Bar");
		if (!ui.isElementVisible(CommunitiesUIConstants.filterSideBarMemberCardViewSelected)) {
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarMemberCardView); // filter side-bar member
		}
		driver.turnOnImplicitWaits();

		logger.strongStep("Wait for all communities to be visible on My Communities page");
		log.info("INFO: Wait for all communities to be visible on My Communities page");
		for(BaseCommunity comm : communities)
			waitForCommunityCardToLoad(comm);
		
		List<Element> communityCards = driver.getElements(CommunitiesUIConstants.ViewCardList);
		 
		logger.strongStep("Verify that the RBL Community " + community4.getName() + " is the first community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the RBL Community " + community4.getName() + " is the first community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(0).getAttribute("aria-label").equals(community4.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community4.getName())).getText().equals("Visited now"));
		
		logger.strongStep("Verify that the Restricted Community " + community3.getName() + " is the second community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the Restricted Community " + community3.getName() + " is the second community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(1).getAttribute("aria-label").equals(community3.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community3.getName())).getText().equals("Visited now"));

		logger.strongStep("Verify that the Moderated Community " + community2.getName() + " is the third community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the Moderated Community " + community2.getName() + " is the third community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(2).getAttribute("aria-label").equals(community2.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community2.getName())).getText().equals("Visited now"));

		logger.strongStep("Verify that the Public Community " + community.getName() + " is the fourth community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the Public Community " + community.getName() + " is the fourth community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(3).getAttribute("aria-label").equals(community.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community.getName())).getText().equals("Visited now"));
		 
		logger.strongStep("Delete all communities");
		log.info("INFO: Delete all communities");
		for(BaseCommunity comm : communities)
			apiOwner.deleteCommunity(apiOwner.getCommunity(comm.getCommunityUUID()));

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B>The time of visit for all communities is correct when logged in and validated as the owner of the communities</li>
	*<li><B>Step:</B>Create four communities - Public, Moderated, Restricted and RBL using API with the same owner</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the communities' owner</li>
	*<li><B>Step:</B>Navigate to all four communities one after another using their UUIDs</li>
	*<li><B>Step:</B>Navigate to My Communities page</li>
	*<li><B>Verify:</B>The 'Recently Visited' option is selected by default in the View selector drop down</li>
	*<li><B>Step:</B>Expand the Side Bar if it is not expanded and then select the 'I'm an Owner' view</li>
	*<li><B>Verify:</B>All community cards have the time of visit as 'Visited now'</li>
	*<li><B>Verify:</B>The community cards are displayed in the same order as they were visited</li>
	*<li><B>Step:</B>Delete all the communities</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "mtlevel2"})
	public void verifyMyCommunitiesRecentlyVisitedAsOwner() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		List <BaseCommunity> communities = new ArrayList<BaseCommunity>();

		BaseCommunity community = new BaseCommunity.Builder(testName +"_PUB_"+ Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.access(Access.PUBLIC)
			.description("This is the Test Description for the testcase: " + testName)
			.build();

		BaseCommunity community2 = new BaseCommunity.Builder(testName +"_MOD_"+ Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.access(Access.MODERATED)
			.description("This is the Test Description for the testcase: " + testName)
			.build();

		BaseCommunity community3 = new BaseCommunity.Builder(testName +"_REST_"+  Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
			.access(Access.RESTRICTED)
			.description("This is the Test Description for the testcase: " + testName)
			.shareOutside(false)
			.build();

		BaseCommunity community4 = new BaseCommunity.Builder(testName +"_RBL_"+  Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
			.access(Access.RESTRICTED)
			.description("This is the Test Description for the testcase: " + testName)
			.rbl(true)
			.shareOutside(false)
			.build();

		logger.strongStep("Add all communities to the list of communities");
		log.info("INFO: Add all communities to the list of communities");
		communities.add(community);
		communities.add(community2);
		communities.add(community3);
		communities.add(community4);

		// create community
		logger.strongStep("Create a Public Community " + community.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Public Community " + community.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI1 = community.createAPI(apiOwner1);
		
		logger.strongStep("Create a Moderated Community " + community2.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Moderated Community " + community2.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI2 = community2.createAPI(apiOwner1);
		
		logger.strongStep("Create a Restricted Community " + community3.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Restricted Community " + community3.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI3 = community3.createAPI(apiOwner1);
		
		logger.strongStep("Create an RBL Community " + community4.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create an RBL Community " + community4.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI4 = community4.createAPI(apiOwner1);

		// add the UUID to community
		logger.strongStep("Get the UUID of the Public Community: " + community.getName());
		log.info("INFO: Get the UUID of the Public Community: " + community.getName());
		community.getCommunityUUID_API(apiOwner, comAPI1);
		
		logger.strongStep("Get the UUID of the Moderated Community: " + community2.getName());
		log.info("INFO: Get the UUID of the Moderated Community: " + community2.getName());
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		logger.strongStep("Get the UUID of the Restricted Community: " + community3.getName());
		log.info("INFO: Get the UUID of the Restricted Community: " + community3.getName());
		community3.getCommunityUUID_API(apiOwner, comAPI3);
		
		logger.strongStep("Get the UUID of the RBL Community: " + community4.getName());
		log.info("INFO: Get the UUID of the RBL Community: " + community4.getName());
		community4.getCommunityUUID_API(apiOwner, comAPI4);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser1.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser3);

		// navigate to the API community
		logger.strongStep("Navigate to all the communities using their UUIDs");
		log.info("INFO: Navigate to all the communities using their UUIDs");
		community2.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community3.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community4.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);

		logger.strongStep("Go to My Communities page");
		log.info("INFO: Go to My Communities page");
		ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);

		logger.strongStep("Validate 'Recently Visited' option is selected by default in the View selector drop down");
		log.info("INFO: Validate 'Recently Visited' option is selected by default in the View selector drop down");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.viewSelectorDropDownCardView).contains("Recently Visited"), "'Recently Visited' is not Selected By Default.");

		driver.turnOffImplicitWaits();

		logger.strongStep("Expand the Side Bar if it is not expanded and then select the 'I'm an Owner' view");
		log.info("INFO: Expand the Side Bar if it is not expanded and then select the 'I'm an Owner' view");
		Element filterIcon = ui.getFirstVisibleElement(CommunitiesUIConstants.filterSideBar);
		if (!filterIcon.getAttribute("class").contains("expanded"))
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);

		ui.clickLinkWait(CommunitiesUIConstants.filterSideBarOwnerCardView);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.filterSideBarOwnerCardViewSelected);

		driver.turnOnImplicitWaits();

		logger.strongStep("Wait for all communities to be visible on My Communities page");
		log.info("INFO: Wait for all communities to be visible on My Communities page");
		for(BaseCommunity comm : communities)
			waitForCommunityCardToLoad(comm);

		logger.strongStep("Verify that all community cards have the time of visit as 'Visited now'");
		log.info("INFO: Verify that all community cards have the time of visit as 'Visited now'");
	        Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community.getName())).getText().equals("Visited now"));
	        Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community2.getName())).getText().equals("Visited now"));
	        Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community3.getName())).getText().equals("Visited now"));
	        Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community4.getName())).getText().equals("Visited now"));

		logger.strongStep("Validate that the community cards are displayed in the same order as they were visited");
		log.info("INFO: Validate that the community cards are displayed in the same order as they were visited");
		List<Element> communityCards = driver.getElements(CommunitiesUIConstants.ViewCardList);
		Assert.assertTrue(communityCards.get(0).getAttribute("aria-label").equals(community.getName()));
		Assert.assertTrue(communityCards.get(1).getAttribute("aria-label").equals(community4.getName()));
		Assert.assertTrue(communityCards.get(2).getAttribute("aria-label").equals(community3.getName()));
		Assert.assertTrue(communityCards.get(3).getAttribute("aria-label").equals(community2.getName()));

		logger.strongStep("Delete all the communities");
		log.info("INFO: Delete all the communities");
		for(BaseCommunity comm : communities)
			apiOwner.deleteCommunity(apiOwner.getCommunity(comm.getCommunityUUID()));

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>The time of visit for all communities is correct when logged in and validated as the follower of the communities</li>
	*<li><B>Step:</B>Create two communities - Public and Moderated using API</li>
	*<li><B>Step:</B>Follow both communities as the same user</li>
	*<li><B>Step:</B>Load the Communities component and Log In as the communities' follower</li>
	*<li><B>Step:</B>Navigate to both communities one after another using their UUIDs</li>
	*<li><B>Step:</B>Navigate to My Communities page</li>
	*<li><B>Verify:</B>The 'Recently Visited' option is selected by default in the View selector drop down</li>
	*<li><B>Step:</B>Expand the Side Bar if it is not expanded and then select the 'I'm Following' view</li>
	*<li><B>Verify:</B>Both community cards have the time of visit as 'Visited now'</li>
	*<li><B>Verify:</B>The community cards are displayed in the same order as they were visited</li>
	*<li><B>Step:</B>Delete both the communities</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "mtlevel2"})
	public void verifyMyCommunitiesRecentlyVisitedAsFollower() throws Exception{
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		List <BaseCommunity> communities = new ArrayList<BaseCommunity>();
		apiFollower = new APICommunitiesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(testName +"_PUB_"+ Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.access(Access.PUBLIC)
			.description("This is the Test Description for the testcase: " + testName)
			.build();

		BaseCommunity community2 = new BaseCommunity.Builder(testName +"_MOD_"+ Helper.genDateBasedRandVal())
			.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
			.access(Access.MODERATED)
			.description("This is the Test Description for the testcase: " + testName)
			.build();

		logger.strongStep("Add both communities to the list of communities");
		log.info("INFO: Add both communities to the list of communities");
		communities.add(community);
		communities.add(community2);

		// create community
		logger.strongStep("Create a Public Community " + community.getName() + " using API for User - "+testUser3.getDisplayName());
		log.info("INFO: Create a Public Community " + community.getName() + " using API for User - "+testUser3.getDisplayName());
		Community comAPI1 = community.createAPI(apiOwner1);
		
		logger.strongStep("Create a Moderated Community " + community2.getName() + " using API for User - "+testUser3.getDisplayName());
		log.info("INFO: Create a Moderated Community " + community2.getName() + " using API for User - "+testUser3.getDisplayName());
		Community comAPI2 = community2.createAPI(apiOwner1);

		// add the UUID to community
		logger.strongStep("Get the UUID of the Public Community: " + community.getName());
		log.info("INFO: Get the UUID of the Public Community: " + community.getName());
		community.getCommunityUUID_API(apiOwner, comAPI1);
		
		logger.strongStep("Get the UUID of the Moderated Community: " + community2.getName());
		log.info("INFO: Get the UUID of the Moderated Community: " + community2.getName());
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		logger.strongStep("Follow both communities as user '" + testUser4.getDisplayName() + "'");
		log.info("INFO: Follow both communities as user '" + testUser4.getDisplayName() + "'");
		community.followAPI(comAPI1, apiFollower, apiOwner1);
		community2.followAPI(comAPI2, apiFollower, apiOwner1);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser4.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser4);

		// navigate to the API community
		logger.strongStep("Navigate to both the communities using their UUIDs");
		log.info("INFO: Navigate to both the communities using their UUIDs");
		community2.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);

		logger.strongStep("Go to My Communities page");
		log.info("INFO: Go to My Communities page");
		ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);

		logger.strongStep("Validate 'Recently Visited' option is selected by default in the View selector drop down");
		log.info("INFO: Validate 'Recently Visited' option is selected by default in the View selector drop down");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.viewSelectorDropDownCardView).contains("Recently Visited"), "'Recently Visited' is not Selected By Default.");

		driver.turnOffImplicitWaits();

		logger.strongStep("Expand the Side Bar if it is not expanded and then select the 'I'm Following' view");
		log.info("INFO: Expand the Side Bar if it is not expanded and then select the 'I'm Following' view");
		ui.goToIamFollowingCardView();

		driver.turnOnImplicitWaits();

		logger.strongStep("Wait for both communities to be visible on My Communities page");
		log.info("INFO: Wait for both communities to be visible on My Communities page");
		for(BaseCommunity comm : communities)
			waitForCommunityCardToLoad(comm);

		logger.strongStep("Verify that both community cards have the time of visit as 'Visited now'");
		log.info("INFO: Verify that both community cards have the time of visit as 'Visited now'");
       		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community.getName())).getText().equals("Visited now"));
	        Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community2.getName())).getText().equals("Visited now"));

		logger.strongStep("Validate that the community cards are displayed in the same order as they were visited");
		log.info("INFO: Validate that the community cards are displayed in the same order as they were visited");
		List<Element> communityCards = driver.getElements(CommunitiesUIConstants.ViewCardList);
		Assert.assertTrue(communityCards.get(0).getAttribute("aria-label").equals(community.getName()));
		Assert.assertTrue(communityCards.get(1).getAttribute("aria-label").equals(community2.getName()));

		logger.strongStep("Delete both the communities");
		log.info("INFO: Delete both the communities");
		for(BaseCommunity comm : communities)
			apiOwner.deleteCommunity(apiOwner.getCommunity(comm.getCommunityUUID()));

		ui.endTest();

	}
	
         /**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My Community-Recently Visited for I'm member and Validate the Communities</li>
	 * <li><B>Step:</B>Create 4 Communities via API of access type Public, Moderated, Restricted, Restricted but Listed for UserA as Owner and UserB as member.</li>
	 * <li><B>Step:</B>Login via UserA in Connections.</li>
	 * <li><B>Step:</B>Navigate to all 4 communities created above in sequence</li>
	 * <li><B>Step:</B>Navigate to My Communities page</li>
	 * <li><B>Verify:</B>Verify By Default 'Recently Visited' Option is Selected</li>
	 * <li><B>Step:</B>Select I'm Creator option from Filter on left panel of page</li>
	 * <li><B>Verify:</B>Verify All 4 Communities got filtered and Sequence is proper</li>
	 * <li><B>Step:</B>Delete All 4 community</li>
	 * </ul>
	 */
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyRecentCreator() throws Exception {
	
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		List <BaseCommunity> communities = new ArrayList<BaseCommunity>();
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName +"_PUB_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.build();
		BaseCommunity community2 = new BaseCommunity.Builder(testName +"_MOD_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.MODERATED)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.build();
		BaseCommunity community3 = new BaseCommunity.Builder(testName +"_REST_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.shareOutside(false).build();
		BaseCommunity community4 = new BaseCommunity.Builder(testName +"_RBL_"+ Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.rbl(true).shareOutside(false).build();
		
		logger.strongStep("Add all communities to the list of communities");
		log.info("INFO: Add all communities to the list of communities");
		communities.add(community);
		communities.add(community2);
		communities.add(community3);
		communities.add(community4);

		// create community
		logger.strongStep("Create a Public Community " + community.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Public Community " + community.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI1 = community.createAPI(apiOwner);
		
		logger.strongStep("Create a Moderated Community " + community2.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Moderated Community " + community2.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI2 = community2.createAPI(apiOwner);
		
		logger.strongStep("Create a Restricted Community " + community3.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create a Restricted Community " + community3.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI3 = community3.createAPI(apiOwner);
		
		logger.strongStep("Create an RBL Community " + community4.getName() + " using API for User - "+testUser1.getDisplayName());
		log.info("INFO: Create an RBL Community " + community4.getName() + " using API for User - "+testUser1.getDisplayName());
		Community comAPI4 = community4.createAPI(apiOwner);
		
		// add the UUID to community
		logger.strongStep("Get the UUID of the Public Community: " + community.getName());
		log.info("INFO: Get the UUID of the Public Community: " + community.getName());
		community.getCommunityUUID_API(apiOwner, comAPI1);
		
		logger.strongStep("Get the UUID of the Moderated Community: " + community2.getName());
		log.info("INFO: Get the UUID of the Moderated Community: " + community2.getName());
		community2.getCommunityUUID_API(apiOwner, comAPI2);
		
		logger.strongStep("Get the UUID of the Restricted Community: " + community3.getName());
		log.info("INFO: Get the UUID of the Restricted Community: " + community3.getName());
		community3.getCommunityUUID_API(apiOwner, comAPI3);
		
		logger.strongStep("Get the UUID of the RBL Community: " + community4.getName());
		log.info("INFO: Get the UUID of the RBL Community: " + community4.getName());
		community4.getCommunityUUID_API(apiOwner, comAPI4);
		
		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser1.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		ui.login(testUser1);

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community2.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community3.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		community4.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);
		
		log.info("Go to my communities");
		ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
		
		logger.strongStep("Validate 'Recently Visited' is Selected by Default.");
		log.info("INFO: Validate 'Recently Visited' is Selected by Default.");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.viewSelectorDropDownCardView).contains("Recently Visited"), "'Recently Visited' is not Selected By Default.");
		
		driver.turnOffImplicitWaits();
		
		logger.strongStep("Expand the Side Bar if it is not already expanded");
		log.info("INFO: Expand the Side Bar if it is not already expanded");
		if (ui.isElementVisible(CommunitiesUIConstants.filterSideBarNotExpanded)) {
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView); // filter side-bar expand
		}
		
		logger.strongStep("Click on the 'I Created' view in the Side Bar");
		log.info("INFO: Click on the 'I Created' view in the Side Bar");
		if (!ui.isElementVisible(CommunitiesUIConstants.filterSideBarCreatedCardViewSelected)) {
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarCreatedCardView); // filter side-bar member
		}
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Wait for all communities to be visible on My Communities page");
		log.info("INFO: Wait for all communities to be visible on My Communities page");
		for(BaseCommunity comm : communities)
			waitForCommunityCardToLoad(comm);

		List<Element> communityCards = driver.getElements(CommunitiesUIConstants.ViewCardList);
		
		logger.strongStep("Verify that the RBL Community " + community4.getName() + " is the first community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the RBL Community " + community4.getName() + " is the first community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(0).getAttribute("aria-label").equals(community4.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community4.getName())).getText().equals("Visited now"));

		logger.strongStep("Verify that the Restricted Community " + community3.getName() + " is the second community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the Restricted Community " + community3.getName() + " is the second community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(1).getAttribute("aria-label").equals(community3.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community3.getName())).getText().equals("Visited now"));

		logger.strongStep("Verify that the Moderated Community " + community2.getName() + " is the third community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the Moderated Community " + community2.getName() + " is the third community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(2).getAttribute("aria-label").equals(community2.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community2.getName())).getText().equals("Visited now"));

		logger.strongStep("Verify that the Public Community " + community.getName() + " is the Fourth community on the page and its time of visit is 'Visited Now'");
		log.info("INFO: Verify that the Public Community " + community.getName() + " is the Fourth community on the page and its time of visit is 'Visited Now'");
		Assert.assertTrue(communityCards.get(3).getAttribute("aria-label").equals(community.getName()));
		Assert.assertTrue(driver.getFirstElement(CommunitiesUI.getTimeOfVisitForCommunityCard(community.getName())).getText().equals("Visited now"));
		 
		logger.strongStep("Delete all communities");
		log.info("INFO: Delete all communities");
		for(BaseCommunity comm : communities)
			apiOwner.deleteCommunity(apiOwner.getCommunity(comm.getCommunityUUID()));

		ui.endTest();
	}
	

	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My Community-Trending filter is working</li>
	 * <li><B>Step:</B>Create a Communities via API of access type Public for UserA.</li>
	 * <li><B>Step:</B>Login via UserA in Connections.</li>
	 * <li><B>Step:</B>Navigate to community created above.</li>
	 * <li><B>Step:</B>Navigate to My Communities page</li>
	 * <li><B>Verify:</B>Verify By Default 'Recently Visited' Option is Selected</li>
	 * <li><B>Step:</B>Select "Trending" option from the Drop Down.</li>
	 * <li><B>Verify:</B>Verify "Trending" is successfully selected and above created community is Displayed in treading card view.</li>
	 * <li><B>Step:</B>Delete the community</li>
	 * </ul>
	 */
	// This test was disabled due to https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12106 
	// Enabling this test case to monitor if issue occurs again after a fix provided as per mentioned in defect
	@Test(groups = { "cplevel2" })
	public void verifyTrending() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Community: this is a Test description for testcase " + testName)
				.webAddress(Data.getData().commonAddress + Helper.genDateBasedRandVal())
				.build();
		
		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal())
		   		 .tags(Data.getData().commonTag)
		   		 .description(Data.getData().commonDescription)
		   		 .partOfCommunity(community)
		   		 .build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("BlogEntry" + Helper.genDateBasedRand())
				.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
				.content("Test description for testcase " + testName)
				.build();

		Community publicCommunity = CommunityEvents.createNewCommunity(community, testUser1, apiOwner);

		String topicReply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicAndAddReply(testUser1, apiOwner, publicCommunity, topic, forumApiOwner, topicReply);
		
		// create blog entry
		apiOwner.createBlogEntry(blogEntry, publicCommunity);

		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, publicCommunity);
		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser1.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		ui.login(testUser1);

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);
		
		logger.strongStep("Navigate to My Communities page");
		log.info("INFO: Navigate to My Communities page");
		ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_ALLMY, true);
		
		logger.strongStep("Validate 'Recently Visited' is Selected by Default.");
		log.info("INFO: Validate 'Recently Visited' is Selected by Default.");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.viewSelectorDropDownCardView).contains("Recently Visited"), "'Recently Visited' is not Selected By Default.");
		
		logger.strongStep("Wait for the community to be visible on My Communities page");
		log.info("INFO: Wait for the community to be visible on My Communities page");
		waitForCommunityCardToLoad(community);

		//Select the Trending option
		ui.goToTrendingCardView();
		
		logger.strongStep("Validate 'Trending' is Selected.");
		log.info("INFO: Validate 'Trending' is Selected.");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.viewSelectorDropDownCardView).contains("Trending"), "'Trending' is not Selected.");
		
		List<Element> communityCards = driver.getElements(CommunitiesUIConstants.ViewCardList);
		int i=0;
		for(;i<communityCards.size();i++)
		{
			if(communityCards.get(i).getAttribute("aria-label").equals(community.getName()))
				break;
		}
		logger.strongStep("Validate above created Community is displayed in 'Trending' card view.");
		log.info("INFO: Validate above created Community is displayed in 'Trending' card view.");
		Assert.assertTrue(communityCards.get(i).getSingleElement(CommunitiesUIConstants.CommunityCardRecentUpdate).getText().contains("recent update"));
		Assert.assertTrue(communityCards.get(i).getAttribute("aria-label").equals(community.getName()));
		
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
		ui.endTest();

	}
	
	//Created this method instead of using fluentWaitPresentWithRefresh because it was waiting till the fluent wait timeout before refreshing
	public void waitForCommunityCardToLoad(BaseCommunity community) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		int i = 0;
		driver.changeImplicitWaits(5);
		while (i < 4) {
			logger.strongStep("Check if the community card is visible on My Communities page, else wait for 5 seconds and refresh the page");
			log.info("INFO: Check if the community card is visible on My Communities page, else wait for 5 seconds and refresh the page");
			if (driver.isElementPresent(CommunitiesUI.getCommunityCardByNameLink(community.getName()))) {
				break;
			} else {
				driver.navigate().refresh();
				i++;
			}
		}
		driver.turnOnImplicitWaits();
	}
}
