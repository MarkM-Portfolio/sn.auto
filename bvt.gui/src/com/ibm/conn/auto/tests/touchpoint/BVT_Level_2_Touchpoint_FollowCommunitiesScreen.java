package com.ibm.conn.auto.tests.touchpoint;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.TouchpointUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.TouchpointUI;
import com.ibm.lconn.automation.framework.services.catalog.CatalogService;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Touchpoint_FollowCommunitiesScreen extends SetUpMethods2 {
	
	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Touchpoint_FollowCommunitiesScreen.class);
	
	private TestConfigCustom cfg;	
	private TouchpointUI ui;
	private User testUser,testUser1,searchAdmin;
	private APICommunitiesHandler apiOwner;
	private String serverUrl;
	private SearchAdminService adminService;
	private HashMap<Community, APICommunitiesHandler> communitiesForDeletion = new HashMap<Community, APICommunitiesHandler>();
	DefectLogger logger = dlog.get(Thread.currentThread().getId());
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		if (cfg.getTestConfig().serverIsMT())  {
			testUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
			testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} else {
			testUser = cfg.getUserAllocator().getUser();
			testUser1 = cfg.getUserAllocator().getUser();
		}
		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
		serverUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
        apiOwner = new APICommunitiesHandler(serverUrl, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
        searchAdmin = cfg.getUserAllocator().getAdminUser();
        adminService = new SearchAdminService();
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
		if (testUser.getDisplayName().equals(testUser1.getDisplayName())) {
			testUser1 = cfg.getUserAllocator().getUser();
		}
	}
	
	private List<String> followCommunitiesAndValidate() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		List<String> followedCommunitiesList = new ArrayList<>();
		int counter=1;
		String countBeforeAddingToFollowList = "", countAfterAddingToFollowList = "";
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 5);
		List<WebElement> communityAvailableToFollow = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(TouchpointUIConstants.getAvailableCommunityToFollow),2));
		log.info("Number of communities available to follow: " + communityAvailableToFollow.size());
		String countBeforeAddingMultiToFollowList = driver.getSingleElement(TouchpointUIConstants.followedCommunitiesCounter).getText();
		for (WebElement community : communityAvailableToFollow) {
			countBeforeAddingToFollowList = driver.getSingleElement(TouchpointUIConstants.followedCommunitiesCounter).getText();
			log.info("Number of communities following before adding to followed list: " + countBeforeAddingToFollowList);

			Element communitiesToBeFollowedElement = driver.getSingleElement(TouchpointUI.getCommunitiesFromList(counter));
	
			String communitiesToBeFollowed = community.getText();
			log.info("Community is: " + communitiesToBeFollowed);
			followedCommunitiesList.add(communitiesToBeFollowed);

			driver.executeScript("arguments[0].scrollIntoView(true);", driver.getSingleElement(TouchpointUI.getcommFollowLink(communitiesToBeFollowed)).getWebElement());
			log.info("INFO: Follow the community " + communitiesToBeFollowed + " by selecting 'FOLLOW' button");
			logger.strongStep("Follow the community " + communitiesToBeFollowed + " by selecting 'FOLLOW' button");
			driver.getSingleElement(TouchpointUI.getcommFollowLink(communitiesToBeFollowed)).click();

			log.info("INFO: Verify 'FOLLOW' button on community card " + communitiesToBeFollowed+ " changes to 'STOP FOLLOWING'");
			logger.strongStep("Verify 'FOLLOW' button on community card " + communitiesToBeFollowed + " changes to 'STOP FOLLOWING'");
			Assert.assertTrue(driver.getSingleElement(TouchpointUI.getcommFollowButtonText(communitiesToBeFollowed)).getText().equals("STOP FOLLOWING"));

			log.info("INFO: Verify the last Updated appears below the Community or Sub-community name");
			logger.strongStep("Verify the last Updated appears below the Community or Sub-community name ");
			Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.updatedMessage), "ERROR: Last Updated message was not visible on the screen");
			
			Element communityCard = driver.getSingleElement(TouchpointUI.getCommunityCard(communitiesToBeFollowed));
			// #002847 is hex color code for dark blue color
			logger.strongStep("Verify that community's card becomes a dark blue background");
			ui.verifyBackgroundColor(communityCard, "#002847");

			// #ffffff is hex color code for white
			logger.strongStep("Verify that community's name becomes white in color");
			ui.verifyTextColor(communitiesToBeFollowedElement, "#ffffff");

			log.info("INFO: Verify that " + communitiesToBeFollowed + " should get added to 'Communities You Follow' list");
			logger.strongStep("Verify that " + communitiesToBeFollowed + " should get added to 'Communities You Follow' list");
			Assert.assertTrue(ui.isElementVisible(TouchpointUI.getCommunitiesFromFollowedList(communitiesToBeFollowed)));

			log.info("INFO: Hover over the Community Image");
			logger.strongStep(" Hover over the Community Image");
			driver.executeScript("window.scrollTo(0, 0)");
			Element communityImage = driver.getSingleElement(TouchpointUI.getCommunitiesFromFollowedList(communitiesToBeFollowed));
			communityImage.hover();

			log.info("INFO: Verify that the community name " + communitiesToBeFollowed + " appears as a tooltip");
			logger.strongStep(" Verify that the community name " + communitiesToBeFollowed + " appears as a tooltip");
			String toolTip = driver.getSingleElement(TouchpointUI.getCommunitiesFromFollowedList(communitiesToBeFollowed)).getAttribute("title");
			Assert.assertEquals(toolTip, communitiesToBeFollowed);

			countAfterAddingToFollowList = driver.getSingleElement(TouchpointUIConstants.followedCommunitiesCounter).getText();
			log.info("Number of communities following after adding to followed list: " + countAfterAddingToFollowList);

			log.info("INFO: Verify that Community you follow value incremented by 1 after selecting follow link");
			logger.strongStep("Verify that Community you follow value incremented by 1 after selecting follow link");
			Assert.assertEquals((Integer.parseInt(countAfterAddingToFollowList) - Integer.parseInt(countBeforeAddingToFollowList)), 1);
			counter++;
			if (counter == 4) {
				break;
			}
		}
		log.info("INFO: Verify that Communities you follow counter value should be incremented by 3");
		logger.strongStep("Verify that Communities you follow counter value should be incremented by 3");
		Assert.assertEquals((Integer.parseInt(countAfterAddingToFollowList) - Integer.parseInt(countBeforeAddingMultiToFollowList)),3);
		return followedCommunitiesList;
	}
	
	private void stopFollowing(List<String> followedCommunitiesList) {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		for (String community : followedCommunitiesList) {
			Element communityCard;
			log.info("community is: " + community);
			log.info("INFO: Unfollow the community " + community + " by selecting 'STOP FOLLOWING' button");
			logger.strongStep("Unfollow the community " + community + " by selecting 'STOP FOLLOWING' button");
			driver.executeScript("arguments[0].scrollIntoView(true);", driver.getSingleElement(TouchpointUI.getcommStopFollowingLink(community)).getWebElement());
			driver.getSingleElement(TouchpointUI.getcommStopFollowingLink(community)).click();

			log.info("INFO: Verify 'STOP FOLLOWING' button on  card " + community + " changes to 'FOLLOW'");
			logger.strongStep("Verify 'STOP FOLLOWING' button on  card " + community + " changes to 'FOLLOW'");
			Assert.assertTrue(driver.getSingleElement(TouchpointUI.getcommFollowButtonText(community)).getText().equals("FOLLOW"));
			communityCard = driver.getSingleElement(TouchpointUI.getCommunityCard(community));

			log.info("INFO: Verify " + community + " card disappears from 'Communities you follow' section");
			logger.strongStep("Verify " + community + " card disappears from 'Communities you follow' section");
			driver.turnOffImplicitWaits();
			Assert.assertFalse(ui.isElementPresent(TouchpointUI.getCommunitiesFromFollowedList(community)));
			driver.turnOnImplicitWaits();

			// #ffffff is hex color code for white color
			logger.strongStep("Verify that community's card becomes a white background");
			ui.verifyBackgroundColor(communityCard, "#ffffff");

			logger.strongStep("Verify that community's name becomes white in color");
			ui.verifyTextColor(communityCard, "#000000");
		}
	}
	
	private void validateCommunitySearch(String communityNameToBeSearched) {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: Verify the list of community displays to match the keyword you typed");
		logger.strongStep("Verify the list of community displays to match the keyword you typed");
		List<Element> searchResults = driver.getVisibleElements(TouchpointUIConstants.communityInSearchResults);
		log.info("Number of cards appearing in search results: " + searchResults.size());
		for (Element community : searchResults) {
			String communityName = community.getText();
			logger.strongStep("Verify that community contains the name you searched for");
			Assert.assertTrue(communityName.contains(communityNameToBeSearched));
		}
	}
	
	

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify 'Follow Communities' screen for existing user</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step: </B> Go to 'Follow Communities' page</li>
	 * <li><B>Verify: </B>Verify user navigates to 'Follow Communities' page</li>
	 * <li><B>Verify: </B>Verify header containing 'Your colleagues use communities to share ideas they care about' appears on the page'</li> 
	 * <li><B>Verify: </B>Verify field 'Search for communities' should appear at left side of the  page</li>
	 * <li><B>Verify: </B>Verify suggestion text appears below search box on the  page</li>
	 * <li><B>Verify: </B>Verify text 'You can add or remove Communities at anytime.' should appear on the page</li>
	 * <li><B>Verify: </B>Verify field 'Communities you follow' should appear at left side of the  page</li>
	 * <li><B>Verify: </B>Verify field 'Community counter'</li>
	 * </ul>
	 */
	@Test(groups = { "cplevel2", "level2" })
	public void followCommunitiesScreen() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
		
		log.info("INFO: Verify user navigates to 'Follow Communities' page");
		logger.strongStep("Verify user navigates to 'Follow Communities' page");
		ui.goToFollowCommunities();
		
		log.info("INFO: Verify header containing 'Your colleagues use communities to share ideas the'");
		logger.strongStep(" Verify header containing 'Your colleagues use communities to share ideas the'");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.followCommunitiesPageHeader2), "ERROR: Headr containing 'Your colleagues use communities to share ideas the' was not visible on the screen");
		
		log.info("INFO: Verify field 'Search for communities' should appear at left side of the  page");
		logger.strongStep("Verify field 'Search for communities' should appear at left side of the  page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.searchForCommunities), "ERROR:'Search for communities' was not visible on the screen");
		
		log.info("INFO: Verify suggestion text appears below search box on the  page");
		logger.strongStep(" Verify suggestion text appears below search box on the  page");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.suggestionTextfolCom1).getText(), "Follow suggested Communities, or search for ones to benefit from shared knowledge and start working together on topics of interest.");

		log.info("INFO: Verify text 'You can add or remove Communities at anytime.' should appear on the page");
		logger.strongStep("Verify text 'You can add or remove Communities at anytime.' should appear on the page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.suggestionTextfolCom2), "ERROR:'You can add or remove Communities at anytime.' was not visible on the screen");
		
		log.info("INFO: Verify field 'Communities you follow' should appear at left side of the  page");
		logger.strongStep("Verify field 'Communities you follow' should appear at left side of the  page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.communitiesYouFollow), "ERROR:'Communities you follow' was not visible on the screen");
		
		log.info("INFO: Verify field 'Community counter' ");
		logger.strongStep("Verify field 'Community counter' ");
		ui.getCommunitiesCount();

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowCommunity();
		ui.endTest();
		
		}
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that existing user is able to search community to follow</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step: </B> Verify user navigates to 'Follow Communities' page"</li>
	 * <li><B>Step: </B> Enter a word that appears in community title </li>
	 * <li><B>Verify: </B> Verify the list of community displays to match the keyword you typed  </li>
	 * </ul>
	 */

	@Test(groups = { "regression" })
	public void searchCommunities() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser);
		BaseCommunity community = new BaseCommunity.Builder(testName+Helper.genDateBasedRandVal())
		                .tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
		                .access(Access.PUBLIC)
		                .description("Test description for testcase " + testName).addMember(member).build();
		Community comAPI = community.createAPI(apiOwner);

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
		
		log.info("INFO: Verify user navigates to 'Follow Communities' page");
		logger.strongStep("Verify user navigates to 'Follow Communities' page");
		ui.goToFollowCommunities();
		
		log.info("INFO: Enter a word that appears in community title");
		logger.strongStep("INFO: Enter a word that appears in community title");
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).click();
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).clear();
		ui.typeText(TouchpointUIConstants.searchForCommunities,testName);
		WebElement element = (WebElement) ui.getFirstVisibleElement(TouchpointUIConstants.searchForCommunities).getBackingObject();
		element.sendKeys(Keys.ENTER);
		
		// Verify the list of community displays to match the keyword you typed
		validateCommunitySearch(testName);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowCommunity();
		apiOwner.deleteCommunity(comAPI);
		ui.endTest();
		
		}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that existing user is able to follow and unfollow people successfully from Searched results view</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step: </B>Verify user navigates to 'Follow Communities' page"</li>
	 * <li><B>Info: </B>Search for a word that appears is community title</li>
	 * * <li><B>Step: </B>User navigates back and then return to this screen</li>
	 * <li><B>Verify: </B>Verify that community you've selected to follow should be still saved</li>
	 * <li><B>Step: </B>Select Back and Next to verify user navigates to 'Follow Communities' page with all saved changes</li>
	 * <li><B>Verify: </B>Verify 'Done' button is displayed</li>
	 * <li><B>Verify: </B>Verify ' We're preparing your Connections Experience.' message over message box"</li>
	 * </ul>
	 * @throws UnsupportedEncodingException 
	 */

	@Test(groups = { "level2" },enabled = false)
	public void followUnfollowCommunitiesFromSearchedResults() throws UnsupportedEncodingException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
	
		String commName=communityDataPop(testUser);
		
		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
		
		log.info("INFO: Verify user navigates to 'Follow Communities' page");
		logger.strongStep("Verify user navigates to 'Follow Communities' page");
		ui.goToFollowCommunities();
		
		log.info("INFO: Search for a word that appears is community title ");
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).click();
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).clear();
		String stringToBeSearched = commName.substring(0, 9);
		ui.typeText(TouchpointUIConstants.searchForCommunities, stringToBeSearched);
		driver.typeNative(Keys.ENTER);
		ui.waitForPageLoaded(driver);

		// Follow several communities from searched results view and validate
		List<String> followedCommunities = followCommunitiesAndValidate();

		log.info("INFO: User navigates back and then return to this screen");
		logger.strongStep("User navigates back and then return to this screen");
		ui.clickLink(TouchpointUIConstants.backButton);
		ui.clickLink(TouchpointUIConstants.nextButton);

		for (String commuNity : followedCommunities) {
			logger.strongStep("Verify that community you've selected to follow should be still saved");
			log.info("INFO: Verify that community you've selected to follow should be still saved");
			Assert.assertTrue(ui.isElementVisible(TouchpointUI.getCommunitiesFromFollowedList(commuNity)));
		}

		// Stop Following communities which are already followed
		stopFollowing(followedCommunities);

		log.info("INFO: Select Back and Next to verify user navigates to 'Follow Communities' page with all saved changes");
		logger.strongStep("Select Next and verify user navigates to 'Follow Communities' page");
		ui.clickLink(TouchpointUIConstants.backButton);
		ui.clickLink(TouchpointUIConstants.nextButton);
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.followCommunityPageHeader));
		
		log.info("INFO: Verify 'Done' button is displayed");
		logger.strongStep("Verify 'Done' button is displayed");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.doneButton));
		ui.clickLink(TouchpointUIConstants.doneButton);
		
		log.info("INFO: Verify 'Application is navigated with Connection Homepage");
		logger.strongStep("Verify 'Application is navigated with Connection Homepage");
		ui.fluentWaitElementVisible(HomepageUIConstants.Ckpt_Updates);
		String connHomepageUpdates= driver.getSingleElement(HomepageUIConstants.Ckpt_Updates).getText();
		Assert.assertEquals("Updates", connHomepageUpdates);
		
		ui.endTest();
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {

		// Remove all of the communities created during the tests
		Set<Community> setOfCommunities = communitiesForDeletion.keySet();
		for (Community community : setOfCommunities) {
			communitiesForDeletion.get(community).deleteCommunity(community);
		}
	}

	public String communityDataPop(User userAddedAsMember) throws UnsupportedEncodingException {
		String CommName = "";
		for (int i = 0; i < 6; i++) {
			Member member = new Member(CommunityRole.MEMBERS, userAddedAsMember);
			BaseCommunity community = new BaseCommunity.Builder("Community_" + i + "_" + Helper.genDateBasedRandVal())
					.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
					.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
					.description("Test description for testcase " + Data.getData().commonName).addMember(member)
					.build();
			Community comAPI1 = community.createAPI(apiOwner);
			communitiesForDeletion.put(comAPI1, apiOwner);
			CommName = community.getName();
		}
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()),
				searchAdmin.getPassword());
		return CommName;
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify communities that you selected to follow through Touchpoint screen should appear on my communities->I'm Following page correctly</li>
	 * <li><B>Step: </B>[API] testUser1 creates few public communities to follow where testUser is member</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user testUser and make sure that is on welcome screen</li>
	 * <li><B>Step: </B>Go to 'Follow Communities' page</li>
	 * <li><B>Step: </B>Search for a word that appears in community's title</li>
	 * <li><B>Step: </B>Follow couple of communities from search result</li>
	 * <li><B>Step: </B>Complete the onboarding process selecting 'Done'</li> 
	 * <li><B>Step: </B>Go to My Communities->I'm Following page</li>
	 * <li><B>Verify: </B>Verify the communities you selected to follow through Touchpoint screen now appears on my communities->I'm Following page correctly</li>	
	 * </ul>
	 * @throws UnsupportedEncodingException 
	 */

	@Test(groups = { "level2" },enabled = false)
	public void followCommunity_E2E() throws UnsupportedEncodingException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		logger.strongStep(testUser1.getDisplayName() + " creates few public comunities to follow where "
				+ testUser.getDisplayName() + " is member");
		String commName = communityDataPop(testUser);

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		log.info("INFO: Go to 'Follow Communities' page");
		logger.strongStep("Go to 'Follow Communities' page");
		ui.goToFollowCommunities();

		log.info("INFO: Search for a word that appears in Community's title ");
		logger.strongStep("Search for a word that appears in Comunity's title ");
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).click();
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).clear();
		String stringToBeSearched = commName.substring(0, 9);
		ui.typeText(TouchpointUIConstants.searchForCommunities, stringToBeSearched);
		driver.typeNative(Keys.ENTER);
		ui.waitForPageLoaded(driver);

		// Follow several communities
		logger.strongStep("Follow couple of communities from search result");
		List<String> followedCommunitiesTouchpoint = ui.followCard(TouchpointUIConstants.searchedCommunityView);

		logger.strongStep("Complete the onboarding process selecting 'Done'");
		ui.clickLink(TouchpointUIConstants.doneButton);
		ui.fluentWaitPresent(HomepageUIConstants.HomepageImFollowing);

		logger.strongStep("Go to My Communities->I'm Following page");
		ui.loadComponent(CatalogService.CATALOG_VIEW_PATH_POSTFIX_FOLLOW, true);

		List<Element> myFollowedCommunitiesEle = driver.getVisibleElements(CommunitiesUIConstants.getCommunityLabels);
		List<String> myFollowedCommunities = new ArrayList<>();

		for (Element community : myFollowedCommunitiesEle) {
			log.info("Community name is: " + community.getText());
			myFollowedCommunities.add(community.getText().replace("\n", " "));
		}

		log.info("Followed Communities from Touchpoint screen are: " + followedCommunitiesTouchpoint);
		log.info("Communities from I'm Following page are: " + myFollowedCommunities);

		int counter = 0;
		for (int i = 0; i < myFollowedCommunities.size(); i++) {
			for (int j = 0; j < followedCommunitiesTouchpoint.size(); j++) {
				if (myFollowedCommunities.get(i).contains(followedCommunitiesTouchpoint.get(j))) {
					counter++;
				}
			}
		}
		log.info("counter value is: " + counter);
		logger.strongStep("Verify the communities you selected to follow through Touchpoint screen now appears on my communities->I'm Following page correctly");
		Assert.assertEquals(counter, 3);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that user is able to search Public, Moderated communities even if he is not a member and only Private communities of which he is member</li>
	 * <li><B>Step: </B>[API] testUser1 creates Private Community with testUser ADDED as a member</li>
	 * <li><B>Step: </B>[API] testUser1 creates Private Community where testUser is NOT a member</li>
	 * <li><B>Step: </B>[API] testUser1 creates Public Community where testUser is NOT a member</li>
	 * <li><B>Step: </B>[API] testUser1 creates Moderated Community where testUser is NOT a member</li>
	 * <li><B>Step: </B>kick of indexer so that created communities are available for search</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user testUser and make sure that user is on welcome screen</li>
	 * <li><B>Step: </B>Go to 'Follow Communities' page</li>
	 * <li><B>Verify: </B>Search for a word that appears in Private Community's title where testUser ADDED as a member and verify that search should return results for the private community</li>
	 * <li><B>Verify: </B>Search for a word that appears in Private Community's title where testUser is NOT a member and verify that search should NOT return results for the private community</li>
	 * <li><B>Verify: </B>Search for a word that appears in Public Community's title where testUser is NOT a member and verify that search returns results for any public community</li>
	 * <li><B>Verify: </B>Search for a word that appears in Moderated Community's title where testUser is NOT a member and verify that search returns results for any moderated community</li> 
	 * <li><B>Verify: </B>Search for a word that appears in "restricted but listed" Community's title where testUser is NOT a member and verify that search should NOT return results that community</li>	
	 * </ul>
	 * @throws UnsupportedEncodingException 
	 */
	
	@Test(groups = { "level3" })
	public void validateSearchCommunities() throws UnsupportedEncodingException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		Member member = new Member(CommunityRole.MEMBERS, testUser);
		BaseCommunity privateBaseComUserIsMember = new BaseCommunity.Builder(
				"privateCommIs" + "_" + Helper.genDateBasedRandVal())
						.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
						.rbl(false).shareOutside(false).description("Test description for testcase " + testName)
						.addMember(member).build();

		BaseCommunity privateBaseComUserIsNotMember = new BaseCommunity.Builder(
				"privateCommNot" + "_" + Helper.genDateBasedRandVal())
						.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED)
						.rbl(false).shareOutside(false).description("Test description for testcase " + testName)
						.build();

		BaseCommunity publicBaseCommunityUserIsNotMember = new BaseCommunity.Builder(
				"publicCommNot" + "_" + Helper.genDateBasedRandVal())
						.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
						.description("Test description for testcase " + testName).build();

		BaseCommunity moderatedBaseCommunityUserIsNotMember = new BaseCommunity.Builder(
				"moderatedCommNot" + "_" + Helper.genDateBasedRandVal())
						.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.MODERATED)
						.description("Test description for testcase " + Data.getData().commonName).build();

		BaseCommunity RBLBaseCommunity = new BaseCommunity.Builder("RBLCommunity" + "_" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal()).access(Access.RESTRICTED).rbl(true)
				.shareOutside(false).description("Test description for testcase " + testName).build();

		logger.strongStep(testUser1.getDisplayName() + " creates Private Comunity with " + testUser + " ADDED as a member");
		Community privateComUserIsMember = privateBaseComUserIsMember.createAPI(apiOwner);

		logger.strongStep(testUser1.getDisplayName() + " creates Private Comunity where " + testUser + " is NOT a member");
		Community privatComUserIsNotMember = privateBaseComUserIsNotMember.createAPI(apiOwner);

		logger.strongStep(testUser1.getDisplayName() + " creates Public Comunity where " + testUser + " is NOT a member");
		Community publicCommunityUserIsNotMember = publicBaseCommunityUserIsNotMember.createAPI(apiOwner);

		logger.strongStep(testUser1.getDisplayName() + " creates Moderated Comunity where " + testUser + " is NOT a member");
		Community moderatedCommunityUserIsNotMember = moderatedBaseCommunityUserIsNotMember.createAPI(apiOwner);

		logger.strongStep(testUser1.getDisplayName() + " creates RBL Comunity where " + testUser + " is NOT a member");
		Community RBLCommunity = RBLBaseCommunity.createAPI(apiOwner);

		logger.strongStep("kick of indexer so that created communities are available for search");
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()),searchAdmin.getPassword());

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		logger.strongStep("Go to 'Follow Communities' page");
		ui.goToFollowCommunities();

		// Validate that the community was found
		logger.strongStep("Search for a word that appears in Private Community's title where testUser ADDED as a member and verify that search should return results for the private community");
		Assert.assertTrue(ui.isCommFoundInSearchResult(privateBaseComUserIsMember),"ERROR: Unable to find community " + privateBaseComUserIsMember.getName() + " in search result");
		logger.strongStep("Search for a word that appears in Private Community's title where testUser is NOT a member and verify that search should NOT return results for the private community");
		Assert.assertFalse(ui.isCommFoundInSearchResult(privateBaseComUserIsNotMember), "ERROR: Community "+ privateBaseComUserIsNotMember.getName() + " found in search result even though user is not a member");
		logger.strongStep("Search for a word that appears in Moderated Community's title where testUser is NOT a member and verify that search returns results for any moderated community");		
		Assert.assertTrue(ui.isCommFoundInSearchResult(publicBaseCommunityUserIsNotMember), "ERROR: Unable to find community "+ publicBaseCommunityUserIsNotMember.getName() + " in search result");
		logger.strongStep("Search for a word that appears in Moderated Community's title where testUser is NOT a member and verify that search returns results for any moderated community");
		Assert.assertTrue(ui.isCommFoundInSearchResult(moderatedBaseCommunityUserIsNotMember), "ERROR: Unable to find community "+ moderatedBaseCommunityUserIsNotMember.getName() + " in search result");
		logger.strongStep("Search for a word that appears in 'restricted but listed' Community's title where testUser is NOT a member and verify that search should NOT return results that community");
		Assert.assertFalse(ui.isCommFoundInSearchResult(RBLBaseCommunity), "ERROR: Community "+ RBLBaseCommunity.getName() + " found in search result even though user is not a member");

		// Delete communities
		apiOwner.deleteCommunity(privateComUserIsMember);
		apiOwner.deleteCommunity(privatComUserIsNotMember);
		apiOwner.deleteCommunity(publicCommunityUserIsNotMember);
		apiOwner.deleteCommunity(moderatedCommunityUserIsNotMember);
		apiOwner.deleteCommunity(RBLCommunity);

		ui.endTest();
	}

}