package com.ibm.conn.auto.tests.touchpoint;

import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.TouchpointUIConstants;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.TouchpointUI;

public class BVT_Level_2_Touchpoint_AddYourInterestsScreen extends SetUpMethods2 {
	
	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Touchpoint_AddYourInterestsScreen.class);
	
	private TestConfigCustom cfg;	
	private TouchpointUI ui;
	private User testUser;
	DefectLogger logger = dlog.get(Thread.currentThread().getId());
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		if (cfg.getTestConfig().serverIsMT())  {
			testUser = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} else {
			testUser = cfg.getUserAllocator().getUser();
		}
		
		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify 'Add Your Interest' screen for existing user</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen.</li>
	 * <li><B>Step: </B>Go To 'Add your Interests' page by selecting button 'Let's Go' from 'Welcome' page->'Next'from 'Update Your Profile'</li>
	 * <li><B>Verify: </B>Verify user navigates to 'Add Your Interests' page</li>
	 * <li><B>Verify: </B>Verify search box appears at left side of 'Add Your Interests' page</li> 
	 * <li><B>Step: </B>Type some text in search field e.g. 'AAA' that does not appear in the 'Suggested Interests'</li>
	 * <li><B>Verify: </B>Verify that entered keyword should appears in type ahead search results like 'Create AAA'</li>
	 * <li><B>Verify: </B>Verify that tag 'AAA' gets added to the 'My Interests' section.</li>
	 * <li><B>Verify: </B>Verify the entries in the My Interests section have a dark blue background </li>
	 * <li><B>Verify: </B>Verify the entries in the My Interests section have white text</li>
	 * <li><B>Verify: </B>Verify each tag entry in the My Interests section associated with Remove icon like '-' and tag gets removed after selecting remove icon");</li>
	 * </ul>
	 */
	
	@Test(groups = { "cplevel2", "level2" })
	public void addYourInterestScreen() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
	
		log.info("INFO: Verify user navigates to 'Add Your Interests' page");
		logger.strongStep("Verify user navigates to 'Add Your Interests' page");
		ui.goToAddYourInterests();
		
		log.info("INFO: Verify search box appears at left side of 'Add Your Interests' page");
		logger.strongStep("Verify search box appears at left side of 'Add Your Interests' page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.searchBox), "ERROR: search box was not visible on the screen");
		
		log.info("INFO: Type some text in search field that does not appear in the 'Suggested Interests'");
		logger.strongStep("Type some text in search field that does not appear in the 'Suggested Interests'");
		String searchString = "bvt_"+ Helper.genStrongRand();
		ui.typeText(TouchpointUIConstants.searchBox, searchString);
		
		log.info("INFO: Verify that 'Create "+searchString +"' is visible in type ahead search results");
		logger.strongStep("Verify that 'Create "+searchString +"' is visible in type ahead search results");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.searchTypeaheadResult).getText(),"Create "+searchString, "ERROR: Entered text in serachbox was not present in type ahead search results");
		
		log.info("INFO: Select 'Create "+searchString +"' from in type ahead search results");
		logger.strongStep("Select 'Create "+searchString +"' from in type ahead search results");
		ui.clickLink(TouchpointUIConstants.searchTypeaheadResult);
		
		log.info("INFO: Verify that a new section appears on the screen: 'My Interests'");
		logger.strongStep("Verify that a new section appears on the screen: 'My Interests'");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.myInterestSection), "ERROR: 'My Interest section' was not visible on the screen");
		
		log.info("INFO: Verify that "+searchString+" gets added to the 'My Interests' section.");
		logger.strongStep("Verify that "+searchString+" gets added to the 'My Interests' section.");
		Assert.assertTrue(ui.getTagsInMyInterest(searchString), "ERROR: Tag was not found in My Interest section");
		
		log.info("INFO: Verify the entries in the My Interests section have a dark blue background ");
		logger.strongStep("Verify the entries in the My Interests section have a dark blue background");
		ui.verifyBackgroundColorForTagEntries();
		
		log.info("INFO: Verify the entries in the My Interests section have white text");
		logger.strongStep("Verify the entries in the My Interests section have white text");
		ui.verifyTextColorForTagEntries();
		
		log.info("INFO: Verify each tag entry in the My Interests section associated with Remove icon like '-' and tag gets removed after selecting remove icon");
		logger.strongStep(" Verify each tag entry in the My Interests section associated with Remove icon like '-' and tag gets removed after selecting remove icon");
		ui.verifyRemoveIconAndRemoveTagEntry(searchString);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromAddYourInterests();
		ui.endTest();

	}

	private void verifyAddedInterests(List<String> interests) {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		for (String tag : interests) {
			logger.strongStep(" Verify that " + tag + " is still saved in 'My Interests' section.");
			Assert.assertTrue(ui.isElementVisible(TouchpointUI.getTagEntry(tag)));
		}
	}

	private void removeAddedInterests(List<String> interests) {
		for (String interest : interests) {
			ui.clickLink(TouchpointUI.getTagEntryRemoveIcon(interest));
		}
	}

	private void verifyPlusIconForSuggestedInterests() {

		List<Element> suggetsedInterestsPlusIcon = driver.getVisibleElements(TouchpointUIConstants.getVisibleSuggestedInterests);
		log.info("Number of suggetsed interests are: " + suggetsedInterestsPlusIcon.size());
		for (Element interest : suggetsedInterestsPlusIcon) {
			WebElement ele = (WebElement) interest.getBackingObject();
			Assert.assertTrue(ele.isDisplayed());
		}
	}

	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that added interests remains saved even after user returns to 'Add Your Interests' page from it's subsequent page 'Follow Colleagues'</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen.</li>
	 * <li><B>Step: </B>Go To 'Add your Interests' page by selecting button 'Let's Go' from 'Welcome' page->'Next'from 'Update Your Profile'</li>
	 * <li><B>Step: </B>Add several interests</li>
	 * <li><B>Step: </B>Select 'Next' button</li>
	 * <li><B>Verify: </B>Verify that user navigates to next page: Follow Colleagues</li>
	 * <li><B>Step: </B>Select 'Back' button<</li>
	 * <li><B>Verify: </B>Verify all added interests are intact and still saved after returning to 'Add Your Interests' page.</li>
	 * </ul>
	 */

	@Test(groups = { "level3" })
	public void addMultipleInterests() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login with existing user: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
		logger.strongStep("Go To 'Add your Interests' page");
		ui.goToAddYourInterests();

		// Added multiple interests
		log.info("INFO: Add several interests");
		logger.strongStep(" Add several interests");
		List<String> interests = ui.addMultipleInterests();

		log.info("INFO: Select 'Next' button");
		logger.strongStep("Select 'Next' button");
		ui.clickLink(TouchpointUIConstants.nextButton);

		// Verify user brought to 'Follow Colleagues' page
		log.info("INFO: Verify that user navigates to next page:  Follow Colleagues");
		logger.strongStep(" Verify that user navigates to next page:  Follow Colleagues");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.followColleaguesPageHeader),"ERROR: User was not navigated to 'Follow Colleagues' screen");

		log.info("INFO: Select 'Back' button");
		logger.strongStep("Select 'Back' button");
		ui.clickLink(TouchpointUIConstants.backButton);
		ui.fluentWaitElementVisible(TouchpointUIConstants.addYourInterestPageHeader);

		// Verify all added interests are intact and still saved after returning to 'Add Your Interests' page
		log.info("INFO: Verify all added interests are intact and still saved after returning to 'Add Your Interests' page");
		verifyAddedInterests(interests);

		log.info("INFO: Remove added interest");
		removeAddedInterests(interests);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromAddYourInterests();
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify 'Suggested Interests' section and upon selecting + icon associated with interest card, it should get removed from 'Suggested' section and moves up to 'My Interests' section</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen.</li>
	 * <li><B>Step: </B>Go To 'Add your Interests' page by selecting button 'Let's Go' from 'Welcome' page->'Next'from 'Update Your Profile'</li>
	 * <li><B>Verify: </B>Verify 'Suggested Interests' header appears on the page</li>
	 * <li><B>Verify: </B>Verify 'Suggested Interests' section appears on right side of the page</li>
	 * <li><B>Verify: </B>Verify that Plus icon is associated with each interests in 'Suggested Interests' section</li>
	 * <li><B>Step: </B>Select plus icon that appears on suggested interests card</li>
	 * <li><B>Verify: </B>Verify that suggested interest card gets removed from 'Suggested' section and moves up to 'My Interests' section</li>
	 * </ul>
	 */

	@Test(groups = { "level2" })
	public void verifySuggestedInterests() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login with existing user: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();
		logger.strongStep("Go To 'Add your Interests' page");
		ui.goToAddYourInterests();

		// Verify 'Suggested Interests' header appears on the page
		log.info("INFO: Verify 'Suggested Interests' header appears on the page");
		logger.strongStep("Verify 'Suggested Interests' header appears on the page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.suggestedInterestsText),"ERROR: 'Suggetsed Intetests' header is not visible");

		// Verify 'Suggested Interests' section appears on right side of the page
		log.info("INFO: Verify 'Suggested Interests' section appears on right side of the page");
		logger.strongStep("Verify 'Suggested Interests' section appears on right side of the page");
		Assert.assertTrue(ui.isElementVisible(TouchpointUIConstants.suggestedInterestSection),"ERROR: 'Suggetsed Intetests' section is not visible");

		log.info("INFO: Verify that Plus icon is associated with each interests in 'Suggetsed Interests' section.");
		logger.strongStep("Verify that Plus icon is associated with each interests in 'Suggetsed Interests' section.");
		verifyPlusIconForSuggestedInterests();

		// Get title of first interest card from suggested view
		String firstUser = driver.getSingleElement(TouchpointUIConstants.getFirstSuggestedInterest).getText();
		log.info("firstUser is:" + firstUser);

		// Add first interest card form suggested view
		logger.strongStep("Select plus icon that appears on suggested interests card");
		driver.getSingleElement(TouchpointUI.getSuggestedInterest(firstUser)).click();

		logger.strongStep("Verify that suggetsed interest card gets added to the 'My Interests' section");
		log.info("INFO: Verify that " + firstUser + " gets added to the 'My Interests' section.");
		Assert.assertTrue(ui.getTagsInMyInterest(firstUser), "ERROR: Tag was not found in My Interest section");

		logger.strongStep("Verify that suggetsed interest card gets removed from 'Suggested Interests' section");
		log.info("INFO: Verify that " + firstUser + " gets removed from 'Suggested Interests' section");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.getTagsInSuggestedInterest(firstUser),"ERROR: Tag was found in 'Suggested Interest' section");
		driver.turnOnImplicitWaits();

		// Remove interest from 'My Interests' section
		ui.removeEntryOfMyInterests(firstUser);

		// Return to welcome screen
		ui.returnToWelcomeScreenfromAddYourInterests();
		ui.endTest();
	}

}
