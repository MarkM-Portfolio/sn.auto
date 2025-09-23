package com.ibm.conn.auto.tests.forums.regression;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class RelatedCommunities extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(RelatedCommunities.class);
	private ForumsUI ui;
	private CommunitiesUI comui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner;
	
	private User testUser1;
	private User testUser2;
	
	String serverURL ;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		comui = CommunitiesUI.getGui(cfg.getProductName(), driver);	
		
		
	}
	
	/**
	 * TEST CASE: Add a related community to the Related Communities widget.
	 * <ul>
	 * <li><B>Info: </B>Verify Add a Related Community operation from UI side.</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the community's Overview page</li>
	
	 * <li><B>Step: </B>add the Related Communities widget from UI</li>
	 * <li><B>Step: </B>click Add a Community link.</li>
	 * <li><B>Step: </B>Input the related community's name, url and description.</li>
	 * <li><B>Step: </B>click Save button.</li>
	 * <li><B>Verify: </B>verify the related community presents</li>
	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"} , enabled=false )
	public void testAddUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();
		String communityName = "community forum "+rand;
		
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		//Load component and login
		logger.strongStep("Open browser, and login to Community as:" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Community");
		comui.loadComponent(Data.getData().ComponentCommunities);
		comui.login(testUser1);
		
		logger.strongStep("Navigate to overview page");
		log.info("INFO: navigate to overview page");
		String overview = serverURL	+ "/communities/service/html/communityoverview?" + commUUIDA;
		driver.navigate().to(overview);
		
		logger.strongStep("Add Related Communities widget");
		log.info("INFO: add Related Communities widget ");		
		comui.addWidget(BaseWidget.RELATED_COMMUNITIES);
		
		logger.strongStep("Click Add a Community Link ");
		log.info("INFO: click Add a Community Link ");
		comui.clickLinkWait(ForumsUIConstants.AddACommunityLink);
		
		logger.strongStep("Input the current community's URL, community name and description as the related community's info");
		log.info("INFO: input the current community's URL, community name and description as the related community's info");
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityURL).type(overview);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityName).type(communityName);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityDesc).type("testing Related Communities");
		
		logger.strongStep("Click Save button in the Add a Community dialog");
		log.info("INFO: click Save button in the Add a Community dialog");
		comui.clickSaveButton();
		
		logger.strongStep("Verify the related community is added");
		log.info("INFO: verify the related community is added");
		Assert.assertTrue(driver.isElementPresent(ForumsUI.getRelatedCommunityLink(communityName)),
				"failed to add a community to Related Communities");
		
	}
	
	/**
	 * TEST CASE: Edit a related community.
	 * <ul>
	 * <li><B>Info: </B>Verify Edit a Related Community.</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to enable the Related Communities widget. </li>
	 * <li><B>Step: </B>Use API to add a related community, </li>
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the community's Overview page</li>
	 * <li><B>Verify: </B>Verify the Related Community is added successfully </li>
	 * <li><B>Step: </B>click View All</li>
	 * <li><B>Step: </B>click More.</li>
	 * <li><B>Step: </B>click Edit</li>
	 * <li><B>Step: </B>edit the related community's name, and description.</li>
	 * <li><B>Step: </B>click Save button.</li>
	 * <li><B>Verify: </B>verify the successful edited message.</li>
	 * <li><B>Step: </B>Click More to check description of the community</li>
	 * <li><B>Verify: </B>Verify that related community's name and related community's description are edited</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testEditUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		String rand = Helper.genDateBasedRand();	
		String communityName = testName+rand;
		
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community	
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		logger.strongStep("Use API to enable the Related Communities widget");
		log.info("INFO: use API to enable the Related Communities widget");
		apiOwner.addWidget(apiCommunityA, BaseWidget.RELATED_COMMUNITIES);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		String targetURL = serverURL + "/communities/recomm/atom/relatedCommunity?communityUuid=";		
		targetURL += ForumsUtils.getCommunityUUID(commUUIDA);	
		log.info("INFO: Add related community's targetURL: "+ targetURL);
		
		String overview = serverURL + "/communities/service/html/communityoverview?" + apiOwner.getCommunityUUID(apiCommunityA);
		String rcDesc = "Add a related community.";
		
		logger.strongStep("Use API to add a related community");
		log.info("INFO: use API to add a related community");
		Entry rcEntry = apiForumsOwner.addRelatedCommunity(targetURL, communityName, overview,rcDesc);
		
		log.info("INFO: rc Entry: "+ rcEntry.toString());
		
		String id = rcEntry.getId().toString();
		String rcUUID = id.substring(id.lastIndexOf(":")+1);		
		log.info("INFO: uuid:"+ rcUUID);
		
		//Load component and login
		logger.strongStep("Open browser, and login to Community as:" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Community ");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(apiCommunityA, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to overview page");
		log.info("INFO: navigate to overview page");		
		driver.navigate().to(overview);
		
		logger.strongStep("Verify the Related Community is added successfully");
		log.info("INFO: verify the Related Community is added successfully");
		Assert.assertTrue(driver.isElementPresent(ForumsUI.getRelatedCommunityLink(communityName)),
		"failed to add a community to Related Communities");
		
		logger.strongStep("Click View All in the Related Community widget");
		log.info("INFO: click View All in the Related Community widget");
		ui.clickLinkWait(ForumsUIConstants.ViewAllLink);
		
		logger.strongStep("Click More");
		log.info("INFO: click More");
		ui.clickLinkWait(ForumsUIConstants.MoreLink);
		
		logger.strongStep("Click Edit link");
		log.info("INFO: click Edit link");
		ui.clickLinkWait(ForumsUIConstants.EditLink);
		
		logger.strongStep("Add a random string after the current community name and description");
		log.info("INFO: Add a random string after the current community name and description");
		rand = Helper.genDateBasedRand();
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityName).type(rand);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityDesc).type(rand);
		
		logger.strongStep("Click Save button to submit the edited content");
		log.info("INFO: click Save button to submit the edited content");
		comui.clickSaveButton();
		
		logger.strongStep("Verify the successful edited message");
		log.info("INFO: verify the successful edited message");
		Assert.assertTrue(driver.isTextPresent("Successfully edited the related community."),
		"failed to remove a community from Related Communities");
		
		logger.strongStep("Click More to check description of the community");
		log.info("INFO: click More to check description of the community");
		ui.clickLinkWait(ForumsUIConstants.MoreLink);
		
		logger.strongStep("Verify that related community's name and related community's description are edited ");
		log.info("Verify that related community's name and related community's description are edited ");
		Assert.assertTrue(driver.isElementPresent("css=tr#"+rcUUID+" a:contains("+ communityName+rand+")"), 
				"failed to edit the related community's name");
		Assert.assertTrue(driver.isTextPresent(rcDesc+rand), "failed to edit the related community's description");
		
	}
	
	/**
	 * TEST CASE: Remove a related community.
	 * <ul>
	 * <li><B>Info: </B>Verify Remove a Related Community.</li>
	 * <li><B>Step: </B>Use API to create a community, </li>
	 * <li><B>Step: </B>Use API to enable the Related Communities widget. </li>
	 * <li><B>Step: </B>Use API to add a related community, </li>
	 * <li><B>Step: </B>Open browser, and login </li>
	 * <li><B>Step: </B>navigate to the community's Overview page</li>
	 * <li><B>Verify: </B>Verify community is added to the related community </li>	
	 * <li><B>Step: </B>click View All</li>
	 * <li><B>Step: </B>click More.</li>
	 * <li><B>Step: </B>click Remove this Community</li>	 
	 * <li><B>Step: </B>click Remove button.</li>
	 * <li><B>Verify: </B>verify the successful removed message.</li>
	 * <li><B>Verify: </B>Verify the related community disappears </li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testRemoveUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		String targetURL = serverURL + "/communities/recomm/atom/relatedCommunity?communityUuid=";
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = testName + rand;
		
		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community
		logger.strongStep("Use API to create a community");
		log.info("INFO: use API to create a community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		
		logger.strongStep("Use API to enable the Related Communities widget");
		log.info("INFO: use API to enable the Related Communities widget");
		apiOwner.addWidget(apiCommunityA, BaseWidget.RELATED_COMMUNITIES);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		targetURL += ForumsUtils.getCommunityUUID(commUUIDA);
		log.info("INFO: targetURL: "+ targetURL);
		
		String overview = serverURL	+"/communities/service/html/communityoverview?" + apiOwner.getCommunityUUID(apiCommunityA);
		String rcDesc = "Add a related community.";
		Entry rcEntry = apiForumsOwner.addRelatedCommunity(targetURL, communityName, overview,rcDesc);
		
		//Load component and login
		logger.strongStep("Open browser, and login to Community as:" + testUser1.getDisplayName());
		log.info("Open browser, and login to Community ");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityA, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to overview page");
		log.info("INFO: navigate to overview page");
		driver.navigate().to(overview);
		
		logger.strongStep("Verify the Related Community is added successfully");
		log.info("INFO: verify the Related Community is added successfully");
		Assert.assertTrue(driver.isElementPresent(ForumsUI.getRelatedCommunityLink(communityName)),"failed to add a community to Related Communities");
		
		logger.strongStep("Click View All from the Related Community widget");
		log.info("INFO: click View All from the Related Community widget");
		ui.clickLinkWait(ForumsUIConstants.ViewAllLink);
		
		logger.strongStep("Click More");
		log.info("INFO: click More");
		ui.clickLinkWait(ForumsUIConstants.MoreLink);
		
		logger.strongStep("Click Remove this Community link");
		log.info("INFO: click Remove this Community link");
		ui.clickLinkWait(ForumsUIConstants.RemoveThisCommunityLink);
		
		logger.strongStep("Click Remove button");
		log.info("INFO: click Remove button");
		ui.clickButton(Data.getData().buttonRemove);
		
		logger.strongStep("Verify the successful message");
		log.info("INFO: verify the successful message");
		Assert.assertTrue(driver.isTextPresent(Data.SuccessfulDelRCMsg),
						"Failed to remove a community from Related Communities");

		String id = rcEntry.getId().toString();
		String rcUUID = id.substring(id.lastIndexOf(":")+1);
		
		logger.strongStep("Verify the related community disappears");
		log.info("INFO: verify the related community disappears");
		Assert.assertFalse(driver.isElementPresent("css=tr#"+rcUUID+" a:contains("+ communityName+")"), 
		"failed to remove the related community");
		
	}
	/**
	*<ul>
	*<li><B>Info: </B>Test only the latest added 3 related community show up in the Related Community widget</li>
	*<li><B>Step: </B>use API to create a community</li>
	*<li><B>Step: </B>use API to enable Related Communities widget</li>
	*<li><B>Step: </B>use API to create 6 related communities</li>
	*<li><B>Step: </B>login from browser and navigate to the community's Overview page</li>
	*<li><B>Verify: </B>verify the first added related community is not there.</li>
	*<li><B>Verify: </B>Verify the latest added 3 related communities are there</li>

	*</ul>
	 */
	// This test is not fixable as the number of communities displayed varies 
	// with the screen resolution, and cannot be made consistent between
	// testing scenarios.
	//@Test(groups={"regression", "regressioncloud"})
	public void testLast5InWidget(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		String targetURL = serverURL + "/communities/recomm/atom/relatedCommunity?communityUuid=";
		
		String rand = Helper.genDateBasedRand();
		
		String communityName = "community forum "+rand;

		BaseCommunity communityA = new BaseCommunity.Builder(communityName)
													.tags(Data.getData().commonTag + rand)
													.commHandle(Data.getData().commonHandle + rand)
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + rand)
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community
		logger.strongStep("Use API to create community");
		log.info("INFO: use API to create community");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		log.info("INFO: current user is: " + testUser1.getUid());
		
		logger.strongStep("Use API to enable the Related Communities widget");
		log.info("INFO: use API to enable the Related Communities widget");
		apiOwner.addWidget(apiCommunityA, BaseWidget.RELATED_COMMUNITIES);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		targetURL += ForumsUtils.getCommunityUUID(commUUIDA);
		log.info("INFO: targetURL: "+ targetURL);
		
		logger.strongStep("Use API to add 6 related communities");
		log.info("INFO: use API to add 6 related communities");
		for(int i=0; i<6; i++){
			rand = Helper.genDateBasedRand();
			String name = "related "+rand;
			BaseCommunity community = new BaseCommunity.Builder(name)
													.access(Access.PUBLIC)
													.description("related community data " +i)													
													.build();
			Community apiCommunity = community.createAPI(apiOwner);
			String overview = serverURL +"/communities/service/html/communityoverview?" + apiOwner.getCommunityUUID(apiCommunity);
			apiForumsOwner.addRelatedCommunity(targetURL, "related "+i, overview,"");
		}
		
		//Load component and login
		logger.strongStep("Open browser, and login to Community as:" + testUser1.getDisplayName());
		log.info("INFO: open browser, and login to Community");
		comui.loadComponent(Data.getData().ComponentCommunities);		
		comui.login(testUser1);
		
		logger.strongStep("Navigate to the community's overview page");
		log.info("INFO: Navigate to the community's overview page");
		String overview = serverURL +"/communities/service/html/communityoverview?" + commUUIDA;
		driver.navigate().to(overview);
		
		logger.strongStep("Wait till the Related Communities Widget is loaded");
		log.info("INFO: Wait till the Related Communities Widget is loaded");
		comui.fluentWaitPresent(ForumsUIConstants.AddACommunityLink);
		
		logger.strongStep("Verify the first added related community is not there");
		log.info("INFO: verify the first added related community is not there");
		Assert.assertFalse(driver.isTextPresent("related 0"),"ERROR: the first added related community(related 0) should not be there");
		
		logger.strongStep("Verify the latest added 3 related communities are there");
		log.info("INFO: verify the latest added 3 related communities are there");
		for(int i=3; i<6; i++)
		Assert.assertTrue(driver.isTextPresent("related "+i),"ERROR: the latest added 5 related communities should be there");
		
	
	}

}
