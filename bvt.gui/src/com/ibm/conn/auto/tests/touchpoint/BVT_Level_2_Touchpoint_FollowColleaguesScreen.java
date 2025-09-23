package com.ibm.conn.auto.tests.touchpoint;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.conn.auto.webui.constants.TouchpointUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.TouchpointUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_Level_2_Touchpoint_FollowColleaguesScreen extends SetUpMethods2 {
	
	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Touchpoint_FollowColleaguesScreen.class);
	
	private TestConfigCustom cfg;	
	private TouchpointUI ui;
	private GlobalsearchUI gUI;
	private HomepageUI hUI;
	private User testUser,testUser1,adminUser;
	private String serverURL;
	private SearchAdminService adminService;
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
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		gUI = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		adminService = new SearchAdminService();
		adminUser=cfg.getUserAllocator().getAdminUser();

		// run command to index Recommended Users now
		log.info("INFO: Index Recommended Users by running sandIndexNows command");
		try {
			String components = "manageremployees,tags,taggedby,communitymembership,evidence,graph";
	        adminService.sandIndexNow(components, adminUser.getUid(), adminUser.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		ui = TouchpointUI.getGui(cfg.getProductName(), driver);		
	}

	private List<String> followMultiplePeopleAndValidate(String viewToBeFollowedFrom) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		List<String> followedPeopleList = new ArrayList<>();
		int counter=1;
		ui.fluentWaitElementVisible(viewToBeFollowedFrom);
		List<Element> peopleAvailableToFollow = driver.getVisibleElements(viewToBeFollowedFrom+ TouchpointUIConstants.getPeopleAvailableToFollow);
		Assert.assertTrue(peopleAvailableToFollow.size() > 3, "ERROR: There are no sufficient users to follow");
		String countBeforeAddingToFollowList = "", countAfterAddingToFollowList = "";
		String countBeforeAddingMultiToFollowList = driver.getSingleElement(TouchpointUIConstants.followedPeopleCounter).getText();
		for (Element person : peopleAvailableToFollow) {
			
			countBeforeAddingToFollowList = driver.getSingleElement(TouchpointUIConstants.followedPeopleCounter).getText();
			log.info("Number of people following before adding to followed list: " + countBeforeAddingToFollowList);

			Element personToBeFollowedElement = driver.getSingleElement(viewToBeFollowedFrom + TouchpointUI.getPersonFromList(counter));
			String personToBeFollowed = person.getText();
			log.info("person is: " + personToBeFollowed);
			followedPeopleList.add(personToBeFollowed);

			log.info("INFO: Follow the person " + personToBeFollowed + " by selecting 'FOLLOW' button");
			logger.strongStep("Follow the person " + personToBeFollowed + " by selecting 'FOLLOW' button");
			driver.getSingleElement(viewToBeFollowedFrom + TouchpointUI.getFollowLink(personToBeFollowed)).click();

			log.info("INFO: Verify 'FOLLOW' button on person card " + personToBeFollowed + " changes to 'STOP FOLLOWING'");
			logger.strongStep("Verify 'FOLLOW' button on person card " + personToBeFollowed + " changes to 'STOP FOLLOWING'");
			Assert.assertTrue(driver.getSingleElement(viewToBeFollowedFrom + TouchpointUI.getFollowButtonText(personToBeFollowed)).getText().equals("STOP FOLLOWING"));

			Element userCard = driver.getSingleElement(viewToBeFollowedFrom + TouchpointUI.getUserCard(personToBeFollowed));
			// #002847 is hex color code for dark blue color
			logger.strongStep("Verify that person's card becomes a dark blue background");
			ui.verifyBackgroundColor(userCard, "#002847");

			// #ffffff is hex color code for white
			logger.strongStep("Verify that person's name becomes white in color");
			ui.verifyTextColor(personToBeFollowedElement, "#ffffff");

			log.info("INFO: Verify that " + personToBeFollowed + " should get added to 'Colleagues You Follow' list");
			logger.strongStep("Verify that " + personToBeFollowed + " should get added to 'Colleagues You Follow' list");
			Assert.assertTrue(ui.isElementVisible(TouchpointUI.getPersonFromFollowedList(personToBeFollowed)));

			log.info("INFO: Hover over the user's photos");
			logger.strongStep(" Hover over the user's photos");
			Element personPic = driver.getSingleElement(TouchpointUI.getPersonFromFollowedList(personToBeFollowed));
			personPic.hover();

			log.info("INFO: Verify that the user's name " + personToBeFollowed + " appears as a tooltip");
			logger.strongStep(" Verify that the user's name " + personToBeFollowed + " appears as a tooltip");
			String toolTip = driver.getSingleElement(TouchpointUI.getPersonFromFollowedList(personToBeFollowed)).getAttribute("title");
			Assert.assertEquals(toolTip, personToBeFollowed);

			countAfterAddingToFollowList = driver.getSingleElement(TouchpointUIConstants.followedPeopleCounter).getText();
			log.info("Number of people following after adding to followed list: " + countAfterAddingToFollowList);

			log.info("INFO: Verify that Colleagues you follow value incremented by 1 after selecting follow link");
			logger.strongStep("Verify that Colleagues you follow value incremented by 1 after selecting follow link");
			Assert.assertEquals((Integer.parseInt(countAfterAddingToFollowList) - Integer.parseInt(countBeforeAddingToFollowList)),1);
			counter++;
			if (counter == 4) {
				break;
			}

		}
		log.info("INFO: Verify that Colleagues you follow counter value should be incremented by 3");
		logger.strongStep("Verify that Colleagues you follow counter value should be incremented by 3");
		Assert.assertEquals((Integer.parseInt(countAfterAddingToFollowList) - Integer.parseInt(countBeforeAddingMultiToFollowList)),3);
		return followedPeopleList;
	}

	private void stopFollowing(List<String> followedPeopleList, String viewSelection) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		for (String user : followedPeopleList) {
			Element userCard;
			log.info("user is: " + user);
			log.info("INFO: Unfollow the person " + user + " by selecting 'STOP FOLLOWING' button");
			logger.strongStep("Unfollow the person " + user + " by selecting 'STOP FOLLOWING' button");
			driver.getSingleElement(viewSelection + TouchpointUI.getStopFollowingLink(user)).click();

			log.info("INFO: Verify 'STOP FOLLOWING' button on person card " + user + " changes to 'FOLLOW'");
			logger.strongStep("Verify 'STOP FOLLOWING' button on person card " + user + " changes to 'FOLLOW'");
			Assert.assertTrue(driver.getSingleElement(viewSelection + TouchpointUI.getFollowButtonText(user)).getText().equals("FOLLOW"));
			userCard = driver.getSingleElement(viewSelection + TouchpointUI.getUserCard(user));

			log.info("INFO: Verify " + user + " card disappears from 'Colleagues you follow' section");
			logger.strongStep("Verify " + user + " card disappears from 'Colleagues you follow' section");
			driver.turnOffImplicitWaits();
			Assert.assertFalse(ui.isElementPresent(TouchpointUI.getPersonFromFollowedList(user)));
			driver.turnOnImplicitWaits();

			// #ffffff is hex color code for white color
			logger.strongStep("Verify that person's card becomes a white background");
			ui.verifyBackgroundColor(userCard, "#ffffff");

			logger.strongStep("Verify that person's name becomes white in color");
			ui.verifyTextColor(userCard, "#000000");
		}
	}

	/**
	 * validateSearchUserToBeFollowed - 
	 * @param userToBeSearched - keyword to be searched
	 * @param fullName - A boolean value. This will be true if searching with full user name but false in case searching with first/last name
	 */
	private void validateSearchUserToBeFollowed(String userToBeSearched, boolean fullName) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: Enter keyword to be searched ");
		driver.getSingleElement(TouchpointUIConstants.searchForColleagues).click();
		driver.getSingleElement(TouchpointUIConstants.searchForColleagues).clear();
		ui.typeText(TouchpointUIConstants.searchForColleagues, userToBeSearched);
		ui.fluentWaitElementVisible(TouchpointUIConstants.getSearchedResults);
		List<Element> searchResults = driver.getVisibleElements(TouchpointUIConstants.nameInSearchResults);
		int userCount = searchResults.size();
		logger.strongStep("Verify that search result list is not empty and there are users to follow");
		log.info("INFO: Verify that search result list is not empty and number of users are "+ searchResults.size());
		Assert.assertTrue(!(userCount == 0) && !(searchResults.isEmpty()),"ERROR: There are no searched users for you to follow");

		if (userCount > 1) {
			for (Element user : searchResults) {
				String useName = user.getText();
				logger.strongStep("Verify the search result has more than 1 match and it contains the name of user you searched for");
				Assert.assertTrue(useName.contains(userToBeSearched));
			}
		} else if (userCount == 1 && !(fullName)) {
			logger.strongStep("Verify the search result has only 1 match and it should contain the name you searched for");
			Assert.assertTrue(searchResults.get(0).getText().contains(userToBeSearched));
		} else if (userCount == 1 && fullName) {
			logger.strongStep("Verify search result has only 1 match and user name should be equals to the name of user you searched for");
			Assert.assertTrue(searchResults.get(0).getText().equals(userToBeSearched));
		}
	}


	/**
	 * <ul>
	 * <li><B>Info: </B>Verify 'Follow Colleagues' screen for existing user</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step:</B> Index Recommended Users displaying on 'Follow Colleague' screen</li>
	 * <li><B>Step: </B> Go to 'Follow Colleagues' page</li>
	 * <li><B>Verify: </B>Verify user navigates to 'Follow Colleagues' page</li>
	 * <li><B>Verify: </B>Verify header containing 'Select people relevant to you so you can keep up-to-date' appears on the page'</li> 
	 * <li><B>Verify: </B>Verify field 'Search for colleagues' should appear at left side of the  page</li>
	 * <li><B>Verify: </B>Verify suggestion text 'We've added some suggestions based......' appears below search box on the  page</li>
	 * <li><B>Verify: </B>Verify text 'You can add or remove people at anytime.' should appear on the page</li>
	 * <li><B>Verify: </B>Verify field 'Colleagues you follow' should appear at left side of the  page</li>
	 * <li><B>Verify: </B>Verify there are people in suggested people view for you to follow in right panel of page</li>
	 * <li><B>Verify: </B>Verify that each user card is associated with 'FOLLOW' button</li>
	 * </ul>
	 */
	@Test(groups = { "level2" })
	public void followColleaguesScreen() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
	
		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		log.info("INFO: Verify user navigates to 'Follow Colleagues' page");
		logger.strongStep("Verify user navigates to 'Follow Colleagues' page");
		ui.goToFollowColleagues();
		Assert.assertTrue(ui.isElementPresent(TouchpointUIConstants.followColleaguesPageHeader));

		log.info("INFO: Verify header containing 'Select people relevant to you so you can keep up-to-date' appears on the page");
		logger.strongStep("Verify header containing 'Select people relevant to you so you can keep up-to-date' appears on the page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.followColleaguesPageHeader2),"ERROR: Headr containing 'Select people relevant to you so you can keep up-to-date' was not visible on the screen");

		log.info("INFO: Verify field 'Search for colleagues' should appear at left side of the  page");
		logger.strongStep("Verify field 'Search for colleagues' should appear at left side of the  page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.searchForColleagues),"ERROR:'Search for colleagues' was not visible on the screen");

		log.info("INFO: Verify suggestion text 'We've added some suggestions based......' appears below search box on the  page");
		logger.strongStep(" Verify suggestion text 'We've added some suggestions based......' appears below search box on the  page");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.suggestionText).getText(),"We've added some suggestions based on expertise, interests, and peer relationships. We recommend that you follow at least 3.");

		log.info("INFO: Verify text 'You can add or remove people at anytime.' should appear on the page");
		logger.strongStep("Verify text 'You can add or remove people at anytime.' should appear on the page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.youCanAddorRemoveText),"ERROR:'You can add or remove people at anytime.' was not visible on the screen");

		log.info("INFO: Verify field 'Colleagues you follow' should appear at left side of the  page");
		logger.strongStep("Verify field 'Colleagues you follow' should appear at left side of the  page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.colleaguesYouFollow),"ERROR:'Colleagues you follow' was not visible on the screen");

		List<Element> suggestedPeople = driver.getVisibleElements(TouchpointUIConstants.suggestedpeolpeToFollow);
		int count = suggestedPeople.size();
		logger.strongStep("Verify there are people in suggested people view for you to follow in right panel of page");
		log.info("INFO: Verify that suggested people list is not empty and number of suggetsed people are " + count);
		Assert.assertTrue(!(count == 0) && !(suggestedPeople.isEmpty()),"ERROR: There are no suggested people for you to follow");

		List<Element> suggestedPeopleFollowLink = driver.getVisibleElements(TouchpointUIConstants.getSuggestedResults+ TouchpointUIConstants.getPeopleAvailableToFollow);
		log.info("Suggested peolple follow link: " + suggestedPeopleFollowLink.size());

		for (Element element : suggestedPeopleFollowLink) {
			String name = element.getText();
			log.info("First Name is: " + name);
			log.info("INFO: Verify that user card " + name + " is associated with 'FOLLOW' button");
			logger.strongStep("Verify that user card " + name + " is associated with 'FOLLOW' button");
			Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.getSuggestedResults + TouchpointUI.getFollowLink(name)),"ERROR: 'FOLLOW' button is not associated with user card " + name);
		}

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowColleagues();
		ui.endTest();

	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that existing user is able to follow and unfollow people successfully from Suggested people view</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step:</B> Index Recommended Users displaying on 'Follow Colleague' screen</li>
	 * <li><B>Step: </B> Go to 'Follow Colleagues' page</li>
	 * <li><B>Step: </B>Follow '3' person by selecting 'FOLLOW' button</li>
	 * <li><B>Verify: </B>Verify 'FOLLOW' button on respective users card changes to 'STOP FOLLOWING'</li>
	 * <li><B>Verify: </B>Verify that user cards becomes a dark blue background</li>
	 * <li><B>Verify: </B>Verify that user name becomes white in color</li>
	 * <li><B>Verify: </B>Verify that followed users should get added to 'Colleagues You Follow' list</li>
	 * <li><B>Verify: </B Verify that Colleagues you follow value incremented by 1 after selecting follow link</li>
	 * <li><B>Verify: </B>Verify that Colleagues you follow counter value should be incremented by '3'</li>
	 * <li><B>Step: </B>Unfollow the person by selecting 'STOP FOLLOWING' button</li>
	 * <li><B>Verify: </B>Verify 'STOP FOLLOWING' button on person card changes to 'FOLLOW'</li>
	 * <li><B>Verify: </B>Verify user card disappears from 'Colleagues you follow' section</li>
	 * <li><B>Verify: </B>Verify that person's card becomes a white background</li>
	 * </ul>
	 */
	@Test(groups = { "level2" },enabled=false)
	public void followUnfollowColleaguesFromSuggestedView() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		log.info("INFO: Verify user navigates to 'Follow Colleagues' page");
		logger.strongStep("Verify user navigates to 'Follow Colleagues' page");
		ui.goToFollowColleagues();

		// Follow couple of users and validate
		List<String> listOfFollowedPeople = followMultiplePeopleAndValidate(TouchpointUIConstants.getSuggestedResults);

		// Stop following users followed in above method
		stopFollowing(listOfFollowedPeople, TouchpointUIConstants.getSuggestedResults);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowColleagues();
		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that existing user is able to search people to follow</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step:</B> Index Recommended Users displaying on 'Follow Colleague' screen</li>
	 * <li><B>Step: </B> Go to 'Follow Colleagues' page</li>
	 * <li><B>Step: </B> Go to the Search field and enter the first name of a user who you want to follow </li>
	 * <li><B>Verify: </B>Verify the list of suggested people changes to match the name you typed. If there is more than 1 match, it should show all who match the word you entered. If there is only 1 person with that name, it should only show that 1 person's card</li>
	 * <li><B>Step: </B> Again go to the Search field and enter the full name of a user who you want to follow</li>
	 * <li><B>Verify: </B>Verify the list of suggested people changes to match the name you typed. If there is more than 1 match, it should show all who match the word you entered. If there is only 1 person with that name, it should only show that 1 person's card</li>
	 * </ul>
	 */
	@Test(groups = { "regression" })
	public void searchColleaguesToBeFollowed() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		log.info("INFO: Verify user navigates to 'Follow Colleagues' page");
		logger.strongStep("Verify user navigates to 'Follow Colleagues' page");
		ui.goToFollowColleagues();

		// Search with first name of user
		logger.strongStep("Go to search field and enter the first name of a user who you want to follow");
		String searchFirstName = testUser1.getFirstName();
		logger.strongStep("Verify the list of suggested people changes to match the name you typed. If there is more than 1 match, it should show all who match the word you entered. If there is only 1 person with that name, it should only show that 1 person's card");
		validateSearchUserToBeFollowed(searchFirstName, false);

		// Search with full name of user
		logger.strongStep("Go to search field and enter the full name of a user who you want to follow");
		String searchUserName = testUser1.getDisplayName();
		logger.strongStep("Verify the list of suggested people changes to match the name you typed. If there is more than 1 match, it should show all who match the word you entered. If there is only 1 person with that name, it should only show that 1 person's card");
		validateSearchUserToBeFollowed(searchUserName, true);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowColleagues();
		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that existing user is able to follow and unfollow people successfully from Searched results view</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step:</B> Index Recommended Users displaying on 'Follow Colleague' screen</li>
	 * <li><B>Step: </B> Go to 'Follow Colleagues' page</li>
	 * <li><B>Step: </B> Go to the Search field and enter the first name of a user who you want to follow which returns multiple results </li>
	 * <li><B>Step: </B>Follow '3' person by selecting 'FOLLOW' button</li>
	 * <li><B>Verify: </B>Verify 'FOLLOW' button on respective users card changes to 'STOP FOLLOWING'</li>
	 * <li><B>Verify: </B>Verify that user cards becomes a dark blue background</li>
	 * <li><B>Verify: </B>Verify that user name becomes white in color</li>
	 * <li><B>Verify: </B>Verify that followed users should get added to 'Colleagues You Follow' list</li>
	 * <li><B>Step: </B>Hover over the user's photos</li>
	 * <li><B>Verify: </B>Verify that the user's name appears as a tooltip</li>
	 * <li><B>Verify: </B Verify that Colleagues you follow value incremented by 1 after selecting follow link</li>
	 * <li><B>Verify: </B>Verify that Colleagues you follow counter value should be incremented by '3'</li>
	 * <li><B>Step: </B>User navigates back and then return to this screen</li>
	 * <li><B>Verify: </B>Verify that the people you've selected to follow should be still saved</li>
	 * <li><B>Step: </B>Unfollow the person by selecting 'STOP FOLLOWING' button</li>
	 * <li><B>Verify: </B>Verify 'STOP FOLLOWING' button on person card changes to 'FOLLOW'</li>
	 * <li><B>Verify: </B>Verify user card disappears from 'Colleagues you follow' section</li>
	 * <li><B>Verify: </B>Verify that person's card becomes a white background</li>
	 * <li><B>Verify: </B>Select Next and verify user navigates to 'Follow Communities' page</li>
	 * </ul>
	 */

	@Test(groups = { "level3" })
	public void followUnfollowPeopleFromSearchedResults() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		log.info("INFO: Verify user navigates to 'Follow Colleagues' page");
		logger.strongStep("Verify user navigates to 'Follow Colleagues' page");
		ui.goToFollowColleagues();

		logger.strongStep("Go to the Search field and enter the first name of a user who you want to follow which returns multiple results");
		log.info("INFO: Go to the Search field and enter the first name of a user who you want to follow which returns multiple results");
		driver.getSingleElement(TouchpointUIConstants.searchForColleagues).clear();
		driver.getSingleElement(TouchpointUIConstants.searchForColleagues).click();
		ui.typeText(TouchpointUIConstants.searchForColleagues, testUser.getFirstName());

		// Follow several people from searched results view and validate
		List<String> followedPeople = followMultiplePeopleAndValidate(TouchpointUIConstants.getSearchedResults);

		log.info("INFO: User navigates back and then return to this screen");
		logger.strongStep("User navigates back and then return to this screen");
		ui.clickLink(TouchpointUIConstants.backButton);
		ui.clickLink(TouchpointUIConstants.nextButton);

		for (String person : followedPeople) {
			logger.strongStep("Verify that people you've selected to follow should be still saved");
			log.info("INFO: Verify that people you've selected to follow should be still saved");
			Assert.assertTrue(ui.isElementVisible(TouchpointUI.getPersonFromFollowedList(person)));
		}
		
		// Unfollow people already followed
		stopFollowing(followedPeople, TouchpointUIConstants.getSearchedResults);

		log.info("INFO: Select Next and verify user navigates to 'Follow Communities' page");
		logger.strongStep("Select Next and verify user navigates to 'Follow Communities' page");
		ui.clickLink(TouchpointUIConstants.nextButton);
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.followCommunityPageHeader));

		// Return to welcome screen
		ui.returnToWelcomeScreenfromFollowCommunity();
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify users selected to follow through Touchpoint screen should appear on my profile->Following  page correctly</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step:</B> Index Recommended Users displaying on 'Follow Colleague' screen</li>
	 * <li><B>Step: </B> Go to 'Follow Colleagues' page</li>
	 * <li><B>Step: </B>Follow couple of users from suggested list</li>
	 * <li><B>Step: </B>Complete the onboarding process just navigating through remaining pages</li> 
	 * <li><B>Step: </B>Go to My Profile->My Network tab</li>
	 * <li><B>Step: </B>Select 'Following' from left navigation bar</li>
	 * <li><B>Verify: </B>Verify the users you selected to follow through Touchpoint screen now appears on 'Following' page correctly</li>	
	 * </ul>
	 */
	@Test(groups = { "level2" },enabled = false)
	public void followColleagues_E2E() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		logger.strongStep("Go to 'Follow Colleagues' page");
		ui.goToFollowColleagues();

		// Follow multiple people from suggested view
		logger.strongStep("Follow couple of users from suggested list");
		List<String> followingUserFromTouchpoint = ui.followCard(TouchpointUIConstants.getSuggestedResults);

		logger.strongStep("Complete the onboarding process just navigating through remaining pages");
		ui.clickLink(TouchpointUIConstants.nextButton);
		ui.fluentWaitPresent(TouchpointUIConstants.followCommunityPageHeader);
		ui.clickLink(TouchpointUIConstants.doneButton);
		ui.fluentWaitPresent(HomepageUIConstants.HomepageImFollowing);

		// Go to profiles
		logger.strongStep("Go to My Profile->My Network tab");
		hUI.gotoProfile();
		ui.clickLink(ProfilesUIConstants.MyNetwork);

		logger.strongStep("Select 'Following' from left navigation bar");
		ui.fluentWaitPresent(ProfilesUIConstants.LeftNavFollowing);
		ui.clickLink(ProfilesUIConstants.LeftNavFollowing);

		// get the list of users from My Profile ->My network-> Following page
		List<Element> followingUsersEle = driver.getVisibleElements(ProfilesUIConstants.getListOfFollowingUsers);
		List<String> followingUsersFromMyProfile = new ArrayList<>();
		for (Element user : followingUsersEle) {
			log.info("following user is: " + user.getText());
			followingUsersFromMyProfile.add(user.getText());
		}
		// List of users following from Tochpoint and My profile
		log.info("List of following user from Touchpoint are: " + followingUserFromTouchpoint);
		log.info("List of following user from my profile are: " + followingUsersFromMyProfile);

		logger.strongStep("Verify the users you followed through touchpoint screen now appears on 'Following' page correctly");
		Assert.assertTrue(followingUsersFromMyProfile.containsAll(followingUserFromTouchpoint));

		// This is clean up step to unfollow the users
		for (String user : followingUsersFromMyProfile) {
			ui.clickLink(ProfilesUI.getFollowingUserCheckbox(user));
		}
		ui.clickLink(ProfilesUIConstants.stopFollowingLink);

		ui.endTest();
	}
}
