package com.ibm.conn.auto.tests.orientme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ItmNavUIConstants;
import com.ibm.conn.auto.webui.constants.OrientMeUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.OrientMeUI;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class BVT_Level_2_OrientMe_ImportantToMe extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_OrientMe_ImportantToMe.class);
	private TestConfigCustom cfg;
	private OrientMeUI ui;
	private ItmNavCnx8 itmNavCnx8;
	private User testUserA, testUserB, searchAdmin;
	private String serverUrl;
	
	private APICommunitiesHandler apiCommTestUserB;
	private APIProfilesHandler apiProfilesTestUserA, apiProfilesTestUserB;
	private SearchAdminService adminService;
	
	List<Community> communities = new ArrayList<Community>();
	Map<String, String> resources = new HashMap<String, String>();
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
		cfg = TestConfigCustom.getInstance();
		ui = OrientMeUI.getGui(cfg.getProductName(), driver);
		itmNavCnx8 = new ItmNavCnx8(driver);
		testUserA = cfg.getUserAllocator().getUser();
		testUserB = cfg.getUserAllocator().getUser();
		
		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommTestUserB = new APICommunitiesHandler(serverUrl, 
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		apiProfilesTestUserA = new APIProfilesHandler(serverUrl,
				testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		apiProfilesTestUserB = new APIProfilesHandler(serverUrl,
				testUserB.getAttribute(cfg.getLoginPreference()), testUserB.getPassword());
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>'All Updates', 'Mentions', 'Responses' and 'Add Entry' icons are displayed.</li>
	*/
	@Test(groups = {"regression"})
	public void verifyLeftIcons() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Load OrientMe and Log In as: " + testUserA.getDisplayName());
		ui.goToOrientMe(testUserA, false);
		
		// Test: verify static left icons
		logger.strongStep("Verify left icons and order.");
		List<Element> icons = driver.getVisibleElements(OrientMeUIConstants.staticMenuSection);
		Assert.assertEquals(icons.size(), 3, "There are 3 icons on the left of the Important to Me bar.");
		Assert.assertEquals(ui.getItmIconLabel(icons.get(0)), "All Updates", "First icon is 'All Updates'");
		Assert.assertEquals(ui.getItmIconLabel(icons.get(1)), "Mentions", "Second icon is 'Mentions'");
		Assert.assertEquals(ui.getItmIconLabel(icons.get(2)), "Responses", "Third icon is Responses'");
		
		// Test: see if the placeholder Add Entry icon should display
		driver.turnOffImplicitWaits();
		List<Element> importantItems = driver.getVisibleElements(OrientMeUIConstants.importantToMeList);
		log.info("# of ITM items: " + importantItems.size());
		try {
			Element addEntry = ui.getFirstVisibleElement(OrientMeUIConstants.addEntryIcon);
			// Add Entry icon is not there, it's correct if there is no ITM items.
			if (importantItems.isEmpty()) {
				Assert.assertEquals(ui.getItmIconLabel(addEntry), "Add entry", "Add entry icon is displayed.");
			} else {
				Assert.assertTrue(false, "Add Entry is displayed but Important to Me size is > 0: " + importantItems.size());
			}
		} catch (AssertionError ae) {
			// Add Entry icon is not there, it's correct if there are ITM items.
			if (importantItems.size() > 0) {
				Assert.assertTrue(true, "Add Entry is hidden as expected");
			} else {
				Assert.assertTrue(false, "Add Entry is not displayed but Important to Me size is 0");
			}
		}
		driver.turnOnImplicitWaits();
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the ability to drag and drop the added community/people icons.</li>
	*/
	@Test(groups = {"regression"})
	public void verifyDragAndDropIcons() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		int numIconsToDragAndDrop = 2;
			
		ui.goToOrientMe(testUserA, false);

		// check to see if we have enough icons to test
		Actions action = new Actions((WebDriver) driver.getBackingObject());
		itmNavCnx8.waitForElementsVisibleWd(By.cssSelector(ItmNavUIConstants.importantToMeList), 5);
		List<WebElement> importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));
		List<WebElement> suggestedList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.suggestedImportantListActual));
		
		// drag and drop some items from the suggested list to the added important list
		// stop when we have enough or suggest list has no more to move	
		log.info("Important list size: " + importantList.size());
		log.info("Suggested list size: " + suggestedList.size());
		while (importantList.size() < numIconsToDragAndDrop && suggestedList.size() > 0 ) {		
			itmNavCnx8.scrollToElementWithJavaScriptWd(itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.suggestedImportantListActual)).get(0));
			action.dragAndDrop(itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.suggestedImportantListActual)).get(0),
					itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual)).get(0)).build().perform();
			
			importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));
			suggestedList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.suggestedImportantListActual));
		}
		
		// check again to see if we now have enough icons to test
		if (importantList.size() < numIconsToDragAndDrop)  {
			log.info("Add more items to the important list.");
			for (int i = 0; i < 2 - importantList.size(); i++)  {
				BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
						"ITM_" + Helper.genRandString(5), Access.PUBLIC);
				Community community = CommunityEvents.createNewCommunity(baseCommunity, testUserB, apiCommTestUserB);
				itmNavCnx8.addImportantItem(community.getTitle(), true);
				communities.add(community);
			}
			importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));

		}
		
		// double check we should have enough icons to test by now
		Assert.assertTrue(importantList.size() >=numIconsToDragAndDrop, "not enough important items: " + importantList.size());
		
		// swap the 1st icon with 2nd
		logger.strongStep("Drag the 2nd item in the Important list to the 1st position.");
		log.info("Drag the 2nd item in the Important list to the 1st position.");
		String firstIconLabel = itmNavCnx8.getItmIconLabel(importantList.get(0));
		String secnodIconLabel = itmNavCnx8.getItmIconLabel(importantList.get(1));
		action.dragAndDrop(importantList.get(1), 
				importantList.get(0)).build().perform();

		importantList = itmNavCnx8.findElements(By.cssSelector(ItmNavUIConstants.importantToMeListActual));

		Assert.assertEquals(itmNavCnx8.getItmIconLabel(importantList.get(0)), secnodIconLabel, "2nd important item now moved to the 1st.");
		Assert.assertEquals(itmNavCnx8.getItmIconLabel(importantList.get(1)), firstIconLabel, "1st important item now moved to the 2nd.");
			
		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Person and Community can be searched and add/remove to Important to Me.</li>
	*<li><B>Step:</B>(API) Create a community as UserB</li>
	*<li><B>Step:</B>As UserA, go to OrientMe.</li>
	*<li><B>Step:</B>Click on Add entry icon and search for the community.</li>
	*<li><B>Verify:</B>Community is added to the Important to Me bar.</li>
	*<li><B>Step:</B>Click on Remove entry icon and click the minus sign to remove the community.</li>
	*<li><B>Verify:</B>Community is removed from the Important to Me bar.</li>
	*<li><B>Step:</B>Click on Add entry icon again and search for a person who is not in the Important to Me list.</li>
	*<li><B>Verify:</B>The person is added to the Important to Me bar without error.</li>
	*<li><B>Step:</B>Click on Remove entry icon and click the minus sign to remove the person.</li>
	*<li><B>Verify:</B>Person is removed from the Important to Me bar.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*</ul>
	 */
	@Test(groups = {"regression"})
	public void addRemoveEntryFromItm() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		ui.goToOrientMe(testUserA, false);
		
		// Test: add/remove community to ITM
		// use a new community to save time checking if already in ITM
		logger.strongStep("Community can be added to the Important to Me list.");
		log.info("(API) Create a public community as " + testUserB.getDisplayName());
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
				"ITM_" + Helper.genRandString(5), Access.PUBLIC);
		Community community = CommunityEvents.createNewCommunity(baseCommunity, testUserB, apiCommTestUserB);
		communities.add(community);
		
		log.info("Add to Important to Me list: " + community.getTitle());
		itmNavCnx8.addImportantItem(community.getTitle(), true);
		
		logger.strongStep("Remove community " + community.getTitle() + " from the Important to Me List");
		log.info("Remove from the Important to Me list: " + community.getTitle());
		itmNavCnx8.removeItemFromImportantToMeList(community.getTitle());
		
		// Test: add/remove person to ITM
		logger.strongStep("Person can be added to the Important to Me list.");
		log.info("Check if " + testUserB.getDisplayName() + " is already in the Important to Me list.");
		boolean newUserFound = false;
		User newUser = testUserB;
		WebElement user = itmNavCnx8.getItemInImportantToMeList(testUserB.getDisplayName(), false);
		
		if (user == null)  {
			newUserFound = true;
		} else {
			// need to find someone else
			while (!newUserFound)  {
				try {
					newUser = cfg.getUserAllocator().getUser();
					// don't try to add the current user
					if (!newUser.getDisplayName().equals(testUserA.getDisplayName()))  {
						log.info("Check if " + newUser.getDisplayName() + " is already in the Important to Me list.");
						user = itmNavCnx8.getItemInImportantToMeList(newUser.getDisplayName(), false);
						if (user == null)  {
							newUserFound = true;
						}
					}
				} catch (RuntimeException re) {
					throw new RuntimeException("No more available user to try.");
				}
			}
		}
		
		log.info("Add " + newUser.getDisplayName() + " to the Important to Me list.");
		itmNavCnx8.addImportantItem(newUser.getDisplayName(), true);
		
		logger.strongStep("Remove " + newUser.getDisplayName() + " from the Important to Me List");
		itmNavCnx8.removeItemFromImportantToMeList(newUser.getDisplayName());
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Associated items displayed when clicking on a community on the Important to Me bar.</li>
	*<li><B>Prereq:</B>(API) Create a community as UserB with UserA as member.</li>
	*<li><B>UserB posts a status update to the community and mentions UserA</li>
	*<li><B>Step:</B>As UserA, go to OrientMe.</li>
	*<li><B>Step:</B>Hover over the community icon on the Important to Me bar and click 'View Community'.</li>
	*<li><B>Verify:</B>The community is open.</li>
	*<li><B>Step:</B>Close the new window. Click on the community icon on the Important to Me bar.</li>
	*<li></B>Click on the community icon on the Important to Me bar of top Update page for new ui.</li>
	*<li><B>Verify:</B>UserB status update is displayed.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T579</li>
	*</ul>
	 */
	@Test(groups = {"regression","cnx8ui-cplevel2"},enabled=true)
	public void clickOnCommunityInItm() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		ui.goToOrientMe(testUserA, false);
		if(cfg.getUseNewUI())
		{
		itmNavCnx8.waitForElementsVisibleWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), 4) ;
		itmNavCnx8.clickLinkWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), "click on top update link");
		}
		
		// Prereq: Create a community and add to ITM if not already created/added by another test
		boolean addCommToItm = true;
		WebElement commElm = null; 
		String commTitle;
		if (resources.isEmpty()) {
			resources = itmTestDataPop();
			commTitle = resources.get("community_title");
		} else {
			// data already seeded, need to check if community is already in ITM
			commTitle = resources.get("community_title");
			commElm = itmNavCnx8.getItemInImportantToMeList(commTitle, false);
			if (commElm != null) {
				addCommToItm = false;
			}
		}
		
		if (addCommToItm)  {
			log.info("Add community to the Important to Me list: " + commTitle);
			commElm = itmNavCnx8.addImportantItem(commTitle, true);
		}
		
		// Test: 'View the community' in the community icon
		HomepageUI hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		CommunitiesUI commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		if(!cfg.getUseNewUI()) {
			log.info("Get the main window handle");
		String mainBrowserWindowHandle = hUI.getCurrentBrowserWindowHandle();
		
		log.info("Handle community click based on the current UI, For new ui cick on community entry and click at subitem on old ui");
        itmNavCnx8.clickOnCommImportantItems(commTitle, commElm, hUI, mainBrowserWindowHandle);
				
		hUI.waitForPageLoaded(driver);
		commUI.waitForCommunityLoaded();

		logger.strongStep("INFO: Verify that user navigated to the correct page");
		String pageTitle;
		
		if (commUI.isHighlightDefaultCommunityLandingPage()) {
			pageTitle = "Highlights - "+ commTitle;
		} else {
			pageTitle = "Overview - "+ commTitle;
		}
		
		Assert.assertEquals(driver.getTitle(), pageTitle, "Error: User is not navigated to new browser tab after clicking View Community.");
		
			UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(hUI, mainBrowserWindowHandle);
		
			// Test: community filtering by clicking on the community in ITM
			logger.strongStep("Click on the community icon: " + commTitle);
			log.info("Click on the community icon: " + commTitle);
			commElm.click();
			ui.waitForPageLoaded(driver);
			
			logger.strongStep("Verify the community mention is found.");
			ui.fluentWaitTextPresent(testUserB.getDisplayName() + " mentioned you in a message posted to the " + commTitle);
			ui.fluentWaitTextPresent(resources.get("community_mention"));
		}
		else
		{
		commElm.click();
		ui.waitForPageLoaded(driver);
		logger.strongStep("Verify the community mention is found.");
		itmNavCnx8.isTextPresentWd(testUserB.getDisplayName() + " mentioned you in a message posted to the " + commTitle);
		itmNavCnx8.isTextPresentWd(resources.get("community_mention"));
		}

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Associated items displayed when clicking on a person on the Important to Me bar.</li>
	*<li><B>Prereq:</B>(API) Create a community as UserB with UserA as member.</li>
	*<li><B>UserB posts a status update to the community and mentions UserA</li>
	*<li><B>UserB is in UserA's Important to Me list.</li>
	*<li><B>Step:</B>As UserA, go to OrientMe.</li>
	*<li><B>Step:</B>Click on the person icon on the Important to Me bar.</li>
	*<li><B>Verify:</B>UserB status update is displayed in old ui, and 'Business Card is displayed in new ui.</li>
	*<li><B>Step:</B>Click on the 'Business Card' icon in UserB's person icon.</li>
	*<li><B>Verify:</B>UserB business card is displayed.</li>
	*<li><B>Step:</B>Click on the 'Filter' icon in UserB's person icon.</li>
	*<li><B>Verify:</B>UserB status update is displayed.</li>
	*<li><B>Jira Test Case: https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T111</li>
	*</ul>
	 */
	@Test(groups = {"level2", "cplevel2"})
	public void clickOnPersonInItm() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		if (resources.isEmpty()) {
			resources = itmTestDataPop();
		}
		
		ui.goToOrientMe(testUserA, false);
		
		// Prereq: add UserB to ITM if not already created/added by another test
		WebElement userBIcon = itmNavCnx8.getItemInImportantToMeList(testUserB.getDisplayName(), false);
		if (userBIcon == null)  {
			log.info("Add " + testUserB.getDisplayName() + " to the Important to Me list.");
			userBIcon = itmNavCnx8.addImportantItem(testUserB.getDisplayName(), true);
		} else {
			log.info(testUserB.getDisplayName() + " is already in the Important to Me list.");
		}
		
		// Test: person filtering by clicking on the person in ITM
		logger.strongStep("INFO: Click on " + testUserB.getDisplayName() + " on the Important to Me bar.");
		log.info("INFO: Click on " + testUserB.getDisplayName() + " on the Important to Me bar.");
		
		// observed that doing scrollToElementWithJavaScriptWd will accidentally click 
		// the top nav bar afterwards in cnx7 UI so only scroll in new UX
		if (cfg.getUseNewUI())  {
			itmNavCnx8.goToExtremeEndInITM(ItmNavUIConstants.downArrowInITMCarousel);
			itmNavCnx8.scrollToElementWithJavaScriptWd(userBIcon);
		}
		
		log.info("After clicking on People Bubble, verify based on newUI flag value");
		logger.strongStep("After clicking on People Bubble, verify based on newUI flag value");
		verifyClickOnPeopleBubble(userBIcon);
		
		log.info("After clicking on People SubBubble, verify based on newUI flag value");
		logger.strongStep("After clicking on People SubBubble, verify based on newUI flag value");
		verifyClickOnPeopleSubBubble(userBIcon);
  	
    	ui.endTest();
	}
	
	/**
	 * This method will verify after click on People Sub Bubble, based on newUI flag value.
 	 * If new UI flag value is true, then go to Top update, click on People Sub Bubble icon and verify community status update with mention will found
	 * If new UI flag value is false, then Hover on Main Bubble, click on Bizcard SubBubble and verify business card.
	 * @param WebElement elementInITM - Main bubble people icon on ITM
	 */
	public void verifyClickOnPeopleSubBubble(WebElement element)
	{
		if(!cfg.getUseNewUI())
		{
			log.info("Hover on Main Bubble, and click on Bizcard SubBubble to verify business card.");
			itmNavCnx8.clickSubItemfromITM(element, By.cssSelector(ItmNavUIConstants.bizCardIconInPersonIcon.replace("PLACEHOLDER", testUserB.getDisplayName())));

	   		HomepageUI hUI = HomepageUI.getGui(cfg.getProductName(), driver);
	    	hUI.verifyBizCardContent(testUserB.getDisplayName());
		}
		else
		{
			itmNavCnx8.clickLinkWd(By.cssSelector(HomepageUIConstants.topUpdatesLink), "Click on Top update link");
			itmNavCnx8.clickSubItemfromITM(element, By.cssSelector(ItmNavUIConstants.filterIconInPersonIcon.replace("PLACEHOLDER", testUserB.getDisplayName())));

			ui.waitForPageLoaded(driver);		
			log.info("Verify the community status update with mention is found.");
			itmNavCnx8.isTextPresentWd(testUserB.getDisplayName() + " mentioned you in a message posted to the " + resources.get("community_title"));
			itmNavCnx8.isTextPresentWd(resources.get("community_mention"));
		}
	}
	
	/**
	 * Click on people bubble on important to me, and verify user status update is displayed in old ui and Biz Card in new UI. 
	 * @param WebElement elementInITM - main bubble people icon on ITM
	 */
	public void verifyClickOnPeopleBubble(WebElement element)
	{
		if(cfg.getUseNewUI())
		{
			log.info("Verify Business Card on person bubble click");
			element.click();
	   		HomepageUI hUI = HomepageUI.getGui(cfg.getProductName(), driver);
			hUI.verifyBizCardContent(testUserB.getDisplayName());
		}
		else
		{
			element.click();
			ui.waitForPageLoaded(driver);		
			log.info("Verify the community status update with mention is found.");
			itmNavCnx8.isTextPresentWd(testUserB.getDisplayName() + " mentioned you in a message posted to the " + resources.get("community_title"));
			itmNavCnx8.isTextPresentWd(resources.get("community_mention"));
		}
	}
	
	/**
	 *<ul>
	 *<li><B>Data population for Important to Me test</B></li>
	 *<li>UserB creates a community and add UserA as a member</li>
	 *<li>UserB posts a status update to the community and mentions UserA</li>
	 *</ul>
	 *@return map of resource created
	 */
	private Map<String, String> itmTestDataPop() throws Exception {
		String randomString = Helper.genRandString(5);
		
		log.info("(API) Create a community as " + testUserB.getDisplayName() + " with member " + testUserA.getDisplayName());
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(
				"ITM_" + randomString, Access.RESTRICTED);
		Community community = CommunityEvents.createNewCommunityWithOneMember(baseCommunity, testUserB, apiCommTestUserB, testUserA);
		resources.put("community_title", community.getTitle());
		communities.add(community);
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		log.info("(API) Post a status update to the community as " + testUserB.getDisplayName() + " and mention " + testUserA.getDisplayName());
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUserA, apiProfilesTestUserA, serverUrl, testUserB.getDisplayName(), randomString);
		CommunityEvents.addCommStatusUpdateWithMentions(community, apiCommTestUserB, apiProfilesTestUserB, mentions);
		resources.put("community_mention", testUserB.getDisplayName() + " @" + testUserA.getDisplayName() + " " + randomString);
		adminService.indexNow("status_updates", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
		
		return resources;
	}
	
	
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		if (communities.size() > 0) {
			for (Community comm : communities)  {
				apiCommTestUserB.deleteCommunity(comm);
			}
		}
	}

}
