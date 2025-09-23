package com.ibm.conn.auto.tests.homepage;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ItmNavUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_CNX8UI_Homepage_ITM extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Homepage_Discover.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private User testUser, testUser1,testUserAddedToITM,searchAdmin;
	private SearchAdminService adminService;
	private HomepageUI ui;
	private CommunitiesUI comUI;
	private ItmNavCnx8 itmNavCnx8;
	private APICommunitiesHandler apiOwner,apiCommTestUserAddedToITM,apiOwner1;
	private String serverURL;
	private Community community;
	List<Community> communities = new ArrayList<Community>();


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getAdminUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		itmNavCnx8 = new ItmNavCnx8(driver);
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommTestUserAddedToITM = new APICommunitiesHandler(serverURL,testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());

	}

	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		cnxAssert = new Assert(log);
	}	


	/**
	*<ul>
	*<li><B>Info:</B>Verify click on Community bubble from Homepage Mypage and Administration page</li>
	*<li><B>Step:</B>Create community1 and community2 via API.</li>
	*<li><B>Step:</B> Login to Homepage and Toggle to new UI</li>
	*<li><B>Step:</B>Click on Mypage from Homepage secondary navigation.</li>
	*<li><B>Step:</B>Add created community1 from ITM</li>
	*<li><B>Step:</B>Click on created community1</li>
	*<li><B>Verify:</B>Verify thatUser is navigated to  community1 overview page</li>
	*<li><B>Step:</B>Click on Administration page from Homepage secondary navigation.</li>
	*<li><B>Step:</B>Add created community2 from ITM</li>
	*<li><B>Step:</B>Click on created community2</li>
	*<li><B>Verify:</B>Verify thatUser is navigated to  community2 overview page</li>
	*<li><B>Jira https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2489</li>
	*</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled= true)
	public void verifyCommunityBubbleFromHomeMyPageAndAdmin() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName  = ui.startTest();

		BaseCommunity community1 = new BaseCommunity.Builder("testB" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName)
				.build();

		BaseCommunity community2 = new BaseCommunity.Builder("testA" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName)
				.build();

		logger.strongStep("Create Community1 using API");
		log.info("INFO: Create Community using API");
		Community com1API = community1.createAPI(apiOwner);
		String com1 = community1.getName();

		logger.strongStep("Create Community2 using API");
		log.info("INFO: Create Community using API");
		Community com2API = community2.createAPI(apiOwner);
		String com2 = community2.getName();

		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Click on MyPage from Secondary Nav of Homepage");
		log.info("INFO: Click on MyPage from Secondary Nav of Homepage");
		ui.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.MyPageCnx8), 4) ;
		ui.clickLinkWd(By.cssSelector(HomepageUIConstants.MyPageCnx8), "click on top update link");

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(com1API, StartPageApi.OVERVIEW);
		}

		logger.strongStep("Add "+com1+ " to the Important to Me list");
		log.info("Add "+com1+ "  to the Important to Me list");
		WebElement comm1Elm = itmNavCnx8.addImportantItem(com1, true);

		logger.strongStep("Click on "+ comm1Elm + " from ITM bar");
		log.info("INFO : Click on "+ comm1Elm + " from ITM bar");
		itmNavCnx8.waitForElementVisibleWd(comm1Elm,6);
		itmNavCnx8.scrollToElementWithJavaScriptWd(comm1Elm);
		itmNavCnx8.clickLinkWd(comm1Elm, "click on Community bubble");

		logger.strongStep("Verify User navigated to Community overview page");
		log.info("INFO: Verify User navigated to Community overview page");
		cnxAssert.assertEquals(driver.getTitle(), "Overview - "+com1,"");

		logger.strongStep("Load Home page");
		log.info("INFO: Load Home page");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);

		logger.strongStep("Click on Administration page from Secondary Nav of Homepage");
		log.info("INFO: Click on Administration page from Secondary Nav of Homepage");
		ui.waitForElementVisibleWd(By.cssSelector(HomepageUIConstants.AdministrationCnx8), 8) ;
		ui.clickLinkWd(By.cssSelector(HomepageUIConstants.AdministrationCnx8), "click on top update link");

		logger.strongStep("Add "+com2+ " to the Important to Me list");
		log.info("Add "+com2+ "  to the Important to Me list");
		WebElement comm2Elm = itmNavCnx8.addImportantItem(com2, true);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		flag = comUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(com2API, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Click on "+ comm2Elm + " from ITM bar");
		log.info("INFO : Click on "+ comm2Elm + " from ITM bar");
		itmNavCnx8.waitForElementVisibleWd(comm2Elm,6);
		itmNavCnx8.scrollToElementWithJavaScriptWd(comm2Elm);
		itmNavCnx8.clickLinkWd(comm2Elm, "click on Community bubble");

		logger.strongStep("Verify User navigated to Community overview page");
		log.info("INFO: Verify User navigated to Community overview page");
		cnxAssert.assertEquals(driver.getTitle(), "Overview - "+com2,"");

		itmNavCnx8.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Down and Up arrow in ITM</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Verify:</B> Verify the presence of 'Collapse' and 'Expand' icons</li>
	 *<li><B>Verify:</B> Verify list of element is different when we click on down arrow in ITM</li>
	 *<li><B>Verify:</B> Verify list of element is same again when we click on up arrow after clicking down arrow in ITM</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifyArrowsInITM() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		itmNavCnx8.startTest();

		logger.strongStep("Load HomePage, Login to Connections and Toggle to New UI as "+cfg.getUseNewUI());
		itmNavCnx8.loadComponent(Data.getData().HomepageImFollowing);
		itmNavCnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Verify the presence of 'Collapse' and 'Expand' icons");
		cnxAssert.assertTrue(itmNavCnx8.validateCollapseAndExpandInITM(),"'Collapse' and 'Expand' icons are clickable");

		logger.strongStep("Verify list of elements in ITM after clicking on down/up arrow");
		cnxAssert.assertTrue(itmNavCnx8.verifyArrowButtons(),
				"The list of visible elements are differnet after clicking on Down arrow and are same after clicking on Up arrow");
			
		itmNavCnx8.endTest();
	}	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify no ITM bar and Homepage TopUpdate on non component pack env</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Verify:</B> Verify that no ITM bar is displayed on new UI Homepage</li>
	 *<li><B>Verify:</B> Verify that no Top Update link bar is displayed on new UI Homepage</li>
	 *</ul>
	 */
	//This test will fail due to defect https://jira.cwp.pnp-hcl.com/browse/CNXSERV-13722
	@Test(groups = {"blue-only"})
	public void verifyNoITMAndTopUpdate() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		itmNavCnx8.startTest();

		logger.strongStep("Load HomePage, Login to Connections and Toggle to New UI as "+cfg.getUseNewUI());
		itmNavCnx8.loadComponent(Data.getData().HomepageImFollowing);
		itmNavCnx8.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Verify that ITM bar is not displayed");
		cnxAssert.assertFalse(itmNavCnx8.isElementDisplayedWd(By.xpath(ItmNavUIConstants.ItmBar)),"ITM bar is displayed");

		logger.strongStep("Verify that Homepage TopUdpate link is not displayed");
		cnxAssert.assertFalse(itmNavCnx8.isElementVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesLink),7),"Homepage Topupdate is displayed");

		itmNavCnx8.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Add and remove People and Community from ITM bar</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B>Add community to ITM bar</li>
	 *<li><B>Step:</B>Add people to ITM bar</li>
	 *<li><B>Verify:</B>Verify that added community is displayed in ITM bar</li>
	 *<li><B>Step:</B>Remove the added community from ITM bar</li>
	 *<li><B>Verify:</B>Verify that the community is not displayed in ITM bar</li>
	 *<li><B>Verify:</B>Verify that added people is displayed in ITM bar</li>
	 *<li><B>Step:</B>Remove the added people from ITM bar</li>
	 *<li><B>Verify:</B>Verify that the people is not displayed in ITM bar</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyAddAndRemoveFromITMBar() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		User testUserForITM = cfg.getUserAllocator().getUser();

		String testName  = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder("test" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName)
				.build();

		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);
		String com = community.getName();

		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Validate Add a person or community place holder text");
		log.info("INFO:Validate Add a person or community place holder text");
		ui.clickLinkWd(By.cssSelector(ItmNavUIConstants.addImportantItem));
		String expectedText = "Add a person or community";
		String actualText = ui.getFirstVisibleElement(ItmNavUIConstants.addPersonComInput).getAttribute("placeholder");
		cnxAssert.assertEquals(actualText, expectedText, "'Add a person or community' is displayed");
		ui.clickLinkWd(By.cssSelector(ItmNavUIConstants.addImportantItem));

		log.info("Add " + testUserForITM.getDisplayName());
		logger.strongStep("Add user in ITM");
		WebElement peopleElm = itmNavCnx8.getItemInImportantToMeList(testUserForITM.getDisplayName(), false);
		if (peopleElm == null) {
			log.info("Add " + testUserForITM.getDisplayName() + " to the Important to Me list.");
			peopleElm=itmNavCnx8.addImportantItem(testUserForITM.getDisplayName(), true);
		} else 
		{
			log.info(testUserForITM.getDisplayName() + " is already in the Important to Me list.");
		}

		log.info("Add " + com);
		logger.strongStep("Add community in ITM");
		log.info("Add " + com + " to the Important to Me list.");
		WebElement commElm = itmNavCnx8.addImportantItem(com, true);

		logger.strongStep("Verify "+ commElm + " is displayd in ITM bar");
		log.info("INFO : Verify "+ commElm + " is displayd in ITM bar");
		itmNavCnx8.scrollToElementWithJavaScriptWd(commElm);
		cnxAssert.assertTrue(commElm.isDisplayed(),"Created community is visible in ITM");

		logger.strongStep("Remove community " + com + " from the Important to Me List");
		log.info("Remove from the Important to Me list: " + com);
		itmNavCnx8.removeItemFromImportantToMeList(com);
		
		logger.strongStep("Verify "+ commElm + " is not displayd in ITM bar");
		log.info("INFO : Verify "+ commElm + " is not displayd in ITM bar");
		cnxAssert.assertFalse(itmNavCnx8.waitForElementVisibleWd(commElm, 5), commElm+" is not displayed in ITM bar");
		
		logger.strongStep("Verify "+ testUserForITM.getDisplayName() + " is displayd in ITM bar");
		log.info("INFO : Verify "+ testUserForITM.getDisplayName() + " is displayd in ITM bar");
		itmNavCnx8.scrollToElementWithJavaScriptWd(peopleElm);
		cnxAssert.assertTrue(peopleElm.isDisplayed(),"Added user is visible in ITM");
		
		logger.strongStep("Remove people " + testUserForITM.getDisplayName() + " from the Important to Me List");
		log.info("Remove from the Important to Me list: " + testUserForITM.getDisplayName());
		itmNavCnx8.removeItemFromImportantToMeList(testUserForITM.getDisplayName());
		
		logger.strongStep("Verify "+ testUserForITM.getDisplayName() + " is not displayd in ITM bar");
		log.info("INFO : Verify "+ testUserForITM.getDisplayName() + " is not displayd in ITM bar");
		cnxAssert.assertFalse(itmNavCnx8.waitForElementVisibleWd(peopleElm, 5), peopleElm+" is not displayed in ITM bar");

		itmNavCnx8.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify user can reorder entries with drag and drop from ITM bar</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Reorder entries on ITM bar with drag and drop</li>
	 *<li><B>Verify:</B> Verify that entries are reordered successfully </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T581</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyDragDropEntriesInITM() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		User testUserB = cfg.getUserAllocator().getUser();
		int numIconsToDragAndDrop = 3;
		
		itmNavCnx8.startTest();
		
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		log.info("INFO: check to see if we have enough icons to test ");
		itmNavCnx8.waitForElementsVisibleWd(By.cssSelector(ItmNavUIConstants.importantToMeList), 5);
		List<WebElement> importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));

		// check again to see if we now have enough icons to test
		if (importantList.size() < numIconsToDragAndDrop) {
			log.info("Add more items to the important list.");
			for (int i = 0; i < 3 - importantList.size(); i++) {
				BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity("ITM_" + Helper.genRandString(5),Access.PUBLIC);
				Community community = CommunityEvents.createNewCommunity(baseCommunity, testUserB, apiOwner1);
				itmNavCnx8.addImportantItem(community.getTitle(), true);
				communities.add(community);
			}
			importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));
		}

		log.info("INFO: Double check we should have enough icons to test by now");
		cnxAssert.assertTrue(importantList.size() >= numIconsToDragAndDrop,"not enough important items: " + importantList.size());

		logger.strongStep("Reorder entries at position 2nd and 3rd with drag and drop.");
		log.info("Reorder entries at position 2nd and 3rd with drag and drop.");
		String secondIconLabel = itmNavCnx8.getItmIconLabel(importantList.get(1));
		String thirdIconLabel = itmNavCnx8.getItmIconLabel(importantList.get(2));
		itmNavCnx8.dragAndDropWd(importantList.get(1), importantList.get(2));

		importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));

		logger.strongStep("Verify that entries are reordered successfully.");
		log.info("Verify that entries are reordered successfully.");
		cnxAssert.assertEquals(itmNavCnx8.getItmIconLabel(importantList.get(2)), secondIconLabel,"2nd important item now moved to the 3rd.");
		cnxAssert.assertEquals(itmNavCnx8.getItmIconLabel(importantList.get(1)), thirdIconLabel,"3rd important item now moved to the 2nd.");
		
		log.info("Delete communities");
		if (communities.size() > 0) {
			for (Community comm : communities) {
				apiOwner1.deleteCommunity(comm);
			}
		}

		ui.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Associated items displayed when clicking on a person on the Important to Me bar.</li>
	 *<li><B>Prereq:</B>(API) Create a community as testUserAddedToITM and add member testUser1 </li>
	 *<li><B>Step:</B> Login to Homepage with testUser1</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Top Updates</li>
	 *<li><B>Step:</B> Add person entry to ITM for testUserAddedToITM if not there</li>
	 *<li><B>Step:</B> Hover over person entry and click on filter icon</li>
	 *<li><B>Verify:</B> Verify that user name should get filtered on the page</li>
	 *<li><B>Verify:</B> Verify that content created by user that is text user 'created a community now' should be displayed </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T602</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyPersonFilterFromTopUpdates() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		itmNavCnx8.startTest();
		
		log.info("INFO: Create Community with user "+testUserAddedToITM.getDisplayName()+" which is being added to ITM");
		String randomString = Helper.genStrongRand();
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + " " + randomString, Access.PUBLIC);
		community = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUserAddedToITM,apiCommTestUserAddedToITM, testUser1);
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()),searchAdmin.getPassword());
		
		logger.strongStep("Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load homepage, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		logger.strongStep("Click Top Updates link in secondary nav menu");
		log.info("INFO: Click Top Updates link in secondary nav menu");
		itmNavCnx8.clickLinkWaitWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 5, "Click Top Updates link in secondary nav menu");
		
		// Adding user to ITM if it is already not added
		log.info("Add " + testUserAddedToITM.getDisplayName());
		logger.strongStep("Add user in ITM");
		WebElement user = itmNavCnx8.getItemInImportantToMeList(testUserAddedToITM.getDisplayName(), false);
		if (user == null) {
			log.info("Add " + testUserAddedToITM.getDisplayName() + " to the Important to Me list.");
			itmNavCnx8.addImportantItem(testUserAddedToITM.getDisplayName(), true);
		} else {
			log.info(testUserAddedToITM.getDisplayName() + " is already in the Important to Me list.");
		}

		log.info("Click on filter icon associated with " + testUserAddedToITM.getDisplayName());
		logger.strongStep("Click on filter icon associated with user in ITM");
		WebElement item = itmNavCnx8.getItemInImportantToMeList(testUserAddedToITM.getDisplayName(), false);
		itmNavCnx8.scrollToElementWithJavaScriptWd(item);
		itmNavCnx8.mouseHoverWd(item);
		itmNavCnx8.clickLinkWithJavaScriptWd(ui.findElement(By.cssSelector(ItmNavCnx8.getFilterIcon(testUserAddedToITM.getDisplayName()))));
		itmNavCnx8.waitForPageLoaded(driver);
		
		log.info(" INFO: Verify that " + testUserAddedToITM.getDisplayName()+" should get filtered on page");
		logger.strongStep("Verify that " + testUserAddedToITM.getDisplayName()+" should get filtered on page");
		cnxAssert.assertTrue(itmNavCnx8.isElementVisibleWd(By.xpath("//div[@class='MuiChip-root MuiChip-deletable']//span[text()='"+testUserAddedToITM.getDisplayName()+"']"), 5), testUserAddedToITM.getDisplayName()+" get filtered");
		
		log.info("Verify that content created by user that is "+testUserAddedToITM.getDisplayName() + " created a community"+" should be dispalyed" );
		logger.strongStep("Verify that content created by user that is "+testUserAddedToITM.getDisplayName() + " created a community"+" should be dispalyed" );
		
		// Search for the community in filtered results
		String title = testUserAddedToITM.getDisplayName() + " added you to the " +community.getTitle() + " community";
		logger.strongStep("Verify community entry is found: " + title);
		itmNavCnx8.fluentWaitTextPresent(title);
		cnxAssert.assertTrue(itmNavCnx8.isTextPresent(title),"Verify community entry is found: " + title);
		
		log.info("INFO: Delete community");
		apiCommTestUserAddedToITM.deleteCommunity(community);
		itmNavCnx8.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify ITM Tooltips on sub-bubble 1.compose 2.filter 3.chat icons, 
	 * also profile & community name by hovering over entry in ITM</li>
	 *<li><B>Step:</B> Create a community via API</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Add community to ITM</li>
	 *<li><B>Step:</B> Hover over the added Community entry</li>
	 *<li><B>Verify:</B> Verify Community name in ITM</li>
	 *<li><B>Step:</B> Add a user to ITM if not already added</li>
	 *<li><B>Step:</B> Hover over the added user entry</li>
	 *<li><B>Verify:</B> Verify tooltip on compose sub-bubble in ITM</li>
	 *<li><B>Verify:</B> Verify tooltip on Filter sub-bubble in ITM</li>
	 *<li><B>Verify:</B> Verify tooltip on Teams web chat sub-bubble in ITM</li>
	 *<li><B>Verify:</B> Verify profile name in ITM</li>
	 *<li><B>JIRA:</B> https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T685</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyTooltipOnSubBubbleAndProfileCommunityNameInITM() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		itmNavCnx8.startTest();
		
		BaseCommunity community1 = new BaseCommunity.Builder("testB" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.build();
		
		logger.strongStep("Create Community1 using API");
		log.info("INFO: Create Community using API");
		Community com1API = community1.createAPI(apiOwner);
		String com1 = community1.getName();

		logger.strongStep("Load HomePage, Login to Connections and Toggle to New UI as " + cfg.getUseNewUI());
		log.info("INFO: Load HomePage, Login to Connections and Toggle to New UI as " + cfg.getUseNewUI());
		itmNavCnx8.loadComponent(Data.getData().ComponentHomepage);
		itmNavCnx8.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Add "+ com1 +" to the Important to Me list");
		log.info("Add "+ com1 +"  to the Important to Me list");
		WebElement comm1Elm = itmNavCnx8.addImportantItem(com1, true);
		itmNavCnx8.waitForPageLoaded(driver);
		
		// Prereq: add UserB to ITM if not already created/added by another test
		logger.strongStep("Add user to ITM if not already added");
		WebElement userBIcon = itmNavCnx8.getItemInImportantToMeList(testUserAddedToITM.getDisplayName(), false);
		if (userBIcon == null) {
			log.info("Add " + testUserAddedToITM.getDisplayName() + " to the Important to Me list.");
			userBIcon = itmNavCnx8.addImportantItem(testUserAddedToITM.getDisplayName(), true);
		} else {
			log.info(testUserAddedToITM.getDisplayName() + " is already in the Important to Me list.");
		}
		itmNavCnx8.waitForPageLoaded(driver);
		
		logger.strongStep("Mouse hover over Community entry bubble");
		log.info("INFO: Mouse hover over Community entry bubble");
		itmNavCnx8.mouseHoverWd(comm1Elm);
		
		logger.strongStep("Verify Community name is visible in ITM");
		log.info("INFO: Verify Community name is visible in ITM");
		itmNavCnx8.waitForElementVisibleWd(By.cssSelector(ItmNavUIConstants.itmEntryName.replace("PLACEHOLDER", com1)), 5);
		cnxAssert.assertTrue(itmNavCnx8.isElementDisplayedWd(By.cssSelector(ItmNavUIConstants.itmEntryName.replace("PLACEHOLDER", com1))), "Community name is visible");
		
		logger.strongStep("Mouse hover over user entry main bubble");
		log.info("INFO: Mouse hover over user entry main bubble");
		itmNavCnx8.mouseHoverWd(userBIcon);

		logger.strongStep("Verify Compose sub-bubble, mouse hover on it and verify tooltip on sub-bubble");
		log.info("INFO: Verify Compose sub-bubble, mouse hover on it and verify tooltip on sub-bubble");
		cnxAssert.assertTrue(itmNavCnx8.isElementDisplayedWd(By.cssSelector(ItmNavUIConstants.composeSubBubble.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))), "Compose SubBubble is visible");
		itmNavCnx8.mouseHoverWd(itmNavCnx8.findElement(By.cssSelector(ItmNavUIConstants.composeSubBubble.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))));
		cnxAssert.assertTrue(itmNavCnx8.isTextPresentWd("Compose"), "Compose tooltip is displayed");
		
		logger.strongStep("Verify Filter sub-bubble, mouse hover on it and verify tooltip on sub-bubble");
		log.info("INFO: Verify Filter sub-bubble, mouse hover on it and verify tooltip on sub-bubble");
		cnxAssert.assertTrue(itmNavCnx8.isElementDisplayedWd(By.cssSelector(ItmNavUIConstants.filterSubBubble.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))), "Filter SubBubble is visible");
		itmNavCnx8.mouseHoverWd(itmNavCnx8.findElement(By.cssSelector(ItmNavUIConstants.filterSubBubble.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))));
		cnxAssert.assertTrue(itmNavCnx8.isTextPresentWd("Filter"), "Filter tooltip is displayed");
		
		logger.strongStep("Verify Teams web chat sub-bubble, mouse hover on it and verify tooltip on sub-bubble");
		log.info("INFO: Verify Teams web chat sub-bubble, mouse hover on it and verify tooltip on sub-bubble");
		cnxAssert.assertTrue(itmNavCnx8.isElementDisplayedWd(By.cssSelector(ItmNavUIConstants.chatSubBubble.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))), "Chat SubBubble is visible");
		itmNavCnx8.mouseHoverWd(itmNavCnx8.findElement(By.cssSelector(ItmNavUIConstants.chatSubBubble.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))));
		cnxAssert.assertTrue(itmNavCnx8.isTextPresentWd("Teams web chat "), "Chat tooltip is displayed");
		
		logger.strongStep("Verify Profile name is visible in ITM");
		log.info("INFO: Verify Profile name is visible in ITM");
		cnxAssert.assertTrue(itmNavCnx8.isElementDisplayedWd(By.cssSelector(ItmNavUIConstants.itmEntryName.replace("PLACEHOLDER", testUserAddedToITM.getDisplayName()))), "Profile name is visible");
		
		logger.strongStep("Delete the Community via API");
		log.info("INFO: Delete the Community via API");
		apiOwner.deleteCommunity(com1API);
		
		itmNavCnx8.endTest();
	}
}
