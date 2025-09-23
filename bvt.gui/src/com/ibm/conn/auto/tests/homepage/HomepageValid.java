package com.ibm.conn.auto.tests.homepage;

import static org.testng.Assert.assertTrue;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseHpWidget;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;

public class HomepageValid {
	
	private static Logger log = LoggerFactory.getLogger(HomepageValid.class);
	
	private static final String GLOBAL_SEARCH = "Global Search";
	
	public static void verifyWidgetToComponentPopupLink(HomepageUI ui, RCLocationExecutor driver, BaseHpWidget widget) {

		//Get original window handle
		String originalWindow = driver.getWindowHandle();
		
		//Click on widget title link to open component
		log.info("INFO: Click on widget title link to open component");
		ui.scrollIntoViewElement(HomepageUI.getWidgetTitleLinkSelector(widget.getTitle()));
		ui.clickLinkWithJavascript(HomepageUI.getWidgetTitleLinkSelector(widget.getTitle()));

		log.info("INFO: Wait for the new window to open");
		ui.fluentWaitNumberOfWindowsEqual(2);
		
		//Switch to Component window which should now be open
		log.info("INFO: Switch to " + widget.getPopupWindowTitle() + " window");
		driver.switchToFirstMatchingWindowByPageTitle(widget.getPopupWindowTitle());

		//Check that at least some text on the loaded page is correct
		log.info("INFO: Validate text on the loaded page is correct");
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		if (widget.getTitle().equals("My Files")) {
			if (!cfg.getUseNewUI()) {
				assertTrue(driver.isTextPresent(widget.getPopupBodyText()),
						"Expected text '" + widget.getPopupBodyText() + "' not found");
			} else {
				assertTrue(driver.isElementPresent(FilesUIConstants.recent),
						"Expected text '" + widget.getPopupBodyText() + "' not found");
			}
		}else {
			assertTrue(driver.isTextPresent(widget.getPopupBodyText()),
					"Expected text '" + widget.getPopupBodyText() + "' not found");
		}

		//close the popup window
		log.info("INFO: Close " + widget.getPopupWindowTitle() + " window");
		driver.close();
		
		//Switch back to original window
		log.info("INFO: Switch back to the original window");
		driver.switchToWindowByHandle(originalWindow);
			ui.fluentWaitElementVisible(HomepageUI.getWidgetTitleLinkSelector(widget.getTitle()));
	}

	
	/** Verify that the Help Window is opened */
	public static void verifyWidgetHelpPopupLink(HomepageUI ui, RCLocationExecutor driver, BaseHpWidget widget) {

		//find widget id
		String id= ui.findWidgetId(widget.getTitle());

		//click action menu
		ui.clickLinkWithJavascript(HomepageUI.getWidgetActionMenu(id));
		
		//click help option
		ui.clickLinkWithJavascript(HomepageUI.ClickForActionsOption(id, "Help"));

		//Get original window handle
		String originalWindow = driver.getWindowHandle();

		//Switch to Help window
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().HelpWindowTitle);

		/*if(ui.isElementPresent(HomepageUI.HelpIFrame)){
			ui.switchToFrameBySelector(HomepageUI.HelpIFrame);
		} else{
			ui.switchToFrameByName(HomepageUI.HelpFrameName);
			ui.switchToFrameByName(HomepageUI.HelpFrame_ContentFrameName);
			ui.switchToFrameByName(HomepageUI.HelpFrame_ContentFrame_ContentViewFrameName);
		}
		*/
		String WidgetTitle = widget.getHelpText();
		Assert.assertTrue(driver.isTextPresent(WidgetTitle.replaceFirst("app", "")), 
					 "ERROR: Help page title '" + WidgetTitle.replaceFirst("app", "") + "' not found.");

		driver.close();

		//Switch to original window
		driver.switchToWindowByHandle(originalWindow);
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		log.info("cfg.getUseNewUI-- "+ cfg.getUseNewUI());
		if(!cfg.getUseNewUI()) {
			ui.fluentWaitTextPresent(Data.getData().ComponentHomepageKeyText);
		}
	}

	/**
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitTextPresent method
	 * @param driver - The RCLocationExecutor object used to invoke the isTextPresent
	 * and isTextNotPresent methods
	 * @param itemsToBeVerified - The element locators which presence / absence in Activity Stream is to be verified
	 * @param filters - The array of Activity Stream filters where the verification will be performed
	 * @param visible - A boolean which indicates whether, or not the String should be present,
	 * i.e. true means it should be displayed and false means it shouldn't be displayed
	 */
	public static void verifyItemsInASUsingMultipleFilters(HomepageUI ui, RCLocationExecutor driver, String[] itemsToBeVerified, String[] filters, boolean visible) {
		
		for(String filter : filters) {
			verifyItemsInAS(ui, driver, itemsToBeVerified, filter, visible);
		}
	}
	
	/**
	 * 
	 * @param ui - The HomepageUI object used to invoke the filterBy, fluentWaitPresent and waitForASComponentsToLoad methods
	 * @param driver - The RCLocationExecutor object used to invoke the isTextPresent methods
	 * @param elementsToBeVerified - The CSS Selector(s) (String) which presence / absence in Activity Stream is to be verified
	 * @param filter - The Activity Stream filter where the verification will be performed
	 * @param visible - A boolean which indicates whether, or not the String should be present,
	 * i.e. true means it should be displayed and false means it shouldn't be displayed
	 */
	public static void verifyElementsInAS(HomepageUI ui, RCLocationExecutor driver, String[] elementsToBeVerified, String filter, boolean visible){
		
		if(filter != null) {
			changeFilterAndWaitForASComponentsToLoad(ui, filter, visible);
		}
		
		// Execute all verifications
		executeAllElementVerifications(ui, driver, elementsToBeVerified, filter, visible);
	}
	
	/**
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitTextPresent method
	 * @param driver - The RCLocationExecutor object used to invoke the isTextPresent
	 * and isTextNotPresent methods
	 * @param itemsToBeVerified - The string(s) which presence / absence in Activity Stream is to be verified
	 * @param filter - The Activity Stream filter where the verification will be performed
	 * @param visible - A boolean which indicates whether, or not the String should be present,
	 * i.e. true means it should be displayed and false means it shouldn't be displayed
	 */
	public static void verifyItemsInAS(HomepageUI ui, RCLocationExecutor driver, String[] itemsToBeVerified, String filter, boolean visible){
		
		if(filter != null) {
			changeFilterAndWaitForASComponentsToLoad(ui, filter, visible);
		}
		
		// Execute all verifications
		executeAllStringItemVerifications(ui, driver, itemsToBeVerified, filter, visible);
	}

	/**
	 * Verifies the given item(s) is displayed in the Global Search results screen
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitTextPresent method
	 * @param driver - The RCLocationExecutor object used to invoke the isTextNotPresent method
	 * @param itemsToBeVerified - The string(s) whose presence / absence in Global Search is to be verified
	 * @param visible - A boolean which indicates whether the verifications to follow will be for elements that should be present (true) or not present (false)
	 */
	public static void verifyItemsInGlobalSearch(HomepageUI ui, RCLocationExecutor driver, String[] itemsToBeVerified, boolean visible){
		
		// Wait for all page components to load before proceeding
		waitForGlobalSearchComponentsToLoad(ui);
		
		// Execute all verifications
		executeAllStringItemVerifications(ui, driver, itemsToBeVerified, GLOBAL_SEARCH, visible);
	}
		
	/**
	 * Verifies that the two supplied boolean values are equal
	 * 
	 * @param actualBooleanValue - The actual boolean value which is to be verified as being equal to the expected boolean value
	 * @param expectedBooleanValue - The expected boolean value to which the actual boolean value is verified to be equal to
	 */
	public static void verifyBooleanValuesAreEqual(boolean actualBooleanValue, boolean expectedBooleanValue) {
		
		log.info("INFO: Verifying that the actual boolean value of '" + actualBooleanValue + "' is equal to the expected boolean value of '" + expectedBooleanValue + "'");
		Assert.assertEquals(actualBooleanValue, expectedBooleanValue, 
							"ERROR: The actual boolean value of '" + actualBooleanValue + "' was NOT equal to the expected boolean value of '" + expectedBooleanValue + "'");
	}
	
	/**
	 * Verifies that the two supplied integer values are equal
	 * 
	 * @param actualValue - The actual value which is to be verified as being equal to the expected value
	 * @param expectedValue - The expected value to which the actual value is verified to be equal to
	 */
	public static void verifyIntValuesAreEqual(int actualValue, int expectedValue) {
		
		log.info("INFO: Verifying that the actual value of '" + actualValue + "' is equal to the expected value of '" + expectedValue + "'");
		Assert.assertEquals(actualValue, expectedValue, 
								"ERROR: The actual value of '" + actualValue + "' was NOT equal to the expected value of '" + expectedValue + "'");
	}
	
	/**
	 * Verifies that the two supplied String values are equal
	 * 
	 * @param actualStringValue - The actual value which is to be verified as being equal to the expected value
	 * @param expectedStringValue - The expected value to which the actual value is verified to be equal to
	 */
	public static void verifyStringValuesAreEqual(String actualStringValue, String expectedStringValue) {
		
		log.info("INFO: Verifying that the actual String value of '" + actualStringValue + "' is equal to the expected String value of '" + expectedStringValue + "'");
		Assert.assertTrue(actualStringValue.equals(expectedStringValue), 
							"ERROR: The actual String value of '" + actualStringValue + "' was NOT equal to the expected String value of '" + expectedStringValue + "'");
	}
	
	/**
	 * Verifies that the two supplied String values are NOT equal
	 * 
	 * @param firstStringValue - The String content of the first string to be verified as not being equal to the second string
	 * @param secondStringValue - The String content of the second string to be verified as not being equal to the first string
	 */
	public static void verifyStringValuesAreNotEqual(String firstStringValue, String secondStringValue) {
		
		log.info("INFO: Verifying that the two strings with content '" + firstStringValue + "' and '" + secondStringValue + "' are NOT the same");
		Assert.assertFalse(firstStringValue.equals(secondStringValue), 
							"ERROR: The first string value with content '" + firstStringValue + "' was determined to be the same as the second string with content: '" + secondStringValue + "'");
	}
	
	/**
	 * Verifies that a string contains the specified substring
	 * 
	 * @param fullStringValue - The string which is to be verified as containing the substring
	 * @param substringValue - The substring value to be verified as present in the full string
	 */
	public static void verifyStringContainsSubstring(String fullStringValue, String substringValue) {
		
		log.info("INFO: Verifying that the String value of '" + fullStringValue + "' contains the substring value of '" + substringValue + "'");
		Assert.assertTrue(fullStringValue.indexOf(substringValue) > -1 || fullStringValue.indexOf(substringValue.toLowerCase()) > -1,
							"ERROR: The String value of '" + fullStringValue + "' did NOT contain the substring value of '" + substringValue + "'");
	}
	
	/**
	 * Verifies that any Element instance is not a null reference
	 * 
	 * @param elementToBeVerified - The Element instance of the web element to be verified as not null
	 */
	public static void verifyOneElementIsNotNull(Element elementToBeVerified) {
		
		log.info("INFO: Verifying that the web element retrieved is NOT a null object");
		Assert.assertNotNull(elementToBeVerified, "ERROR: The web element retrieved was a null reference");
	}
	
	/**
	 * Verifies that the file details box is displayed correctly - verifies the icon, the author name, the file name and the tags are displayed correctly
	 * 
	 * @param ui - The HomepageUI object used to invoke the filterBy, fluentWaitPresent and waitForASComponentsToLoad methods
	 * @param driver - The RCLocationExecutor object used to invoke the isElementPresent methods
	 * @param baseFile - The BaseFile instance of the file whose details are being verified
	 * @param fileDetails - The name and extension of the file, e.g. abc123.pdf
	 * @param authorDetails - The name and Uuid of the owner of the file, e.g. John Smith12345678
	 * This can be obtained by concatenating the user name (testUser.getDisplayName()) 
	 * and their Uuid (apiProfilesUser1.getUUID()) where apiProfilesUser1 is an APIProfilesHandler object
	 * @param filter - The Activity Stream filter where the verification will be performed
	 * @param visible - A boolean which indicates whether, or not the CSS selector / element should be present,
	 * i.e. true means it should be displayed and false means it shouldn't be displayed
	 */
	public static void verifyFileDetailsBox(HomepageUI ui, RCLocationExecutor driver, BaseFile baseFile, String fileDetails, String authorDetails, String filter, boolean visible) {

		log.info("INFO: Create the elements whose presence / absence is to be verified");
		String fileName = HomepageUIConstants.fileDetailsBody.replace("PLACEHOLDER", fileDetails);
		String authorName = ui.replaceNewsStory(HomepageUIConstants.fileDetailsAuthor, fileDetails, authorDetails, null);
		String fileIcon = HomepageUIConstants.fileDetailsIcon.replace("PLACEHOLDER", fileDetails);
		
		// The tags must be set to lower case as this is how they appear in the file details box
		String tagDetails = ui.replaceNewsStory(HomepageUIConstants.fileDetailsTags, fileDetails, baseFile.getTags().toLowerCase(), null);
		
		log.info("INFO: Verifying the presence / absence and correctness of the file details box");
		verifyElementsInAS(ui, driver, new String[]{fileName, authorName, fileIcon, tagDetails}, filter, visible);
	}
	
	/**
	 * Verifies that the file details overlay is displayed in the UI and can be interacted with
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param baseFile - The BaseFile instance of the file which is to be displayed in the file details overlay
	 * @return - True if all actions are completed successfully
	 */
	public static boolean verifyFileDetailsOverlayIsDisplayed(HomepageUI ui, BaseFile baseFile) {
		
		log.info("INFO: Verify that the like button is displayed in the file details overlay");
		Assert.assertTrue(ui.fluentWaitElementVisible(FileViewerUI.LikeButton_FiDO),
							"ERROR: The like button was NOT displayed in the file details overlay");

		log.info("INFO: Verify that the correct file name '" + baseFile.getRename() + "' is displayed in the file details overlay");
		Assert.assertTrue(ui.fluentWaitTextPresent(baseFile.getRename()),
							"ERROR: The file name '" + baseFile.getRename() + "' was NOT displayed in the file details overlay");

		log.info("INFO: Click on the 'More Actions' button in the file details overlay");
		ui.clickLinkWait(FileViewerUI.TogglePanelButton);

		log.info("INFO: Close the file details overlay");
		ui.clickLinkWait(FileViewerUI.CloseButton);
		
		return true;
	}
	
	/**
	 * Verifies that the filtered tag / hashtag is displayed beside the 'Matching:' text in Global Search UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param tagToBeVerified - The String content of the tag to be verified as displayed
	 */
	public static void verifyFilteredTagIsDisplayedInGlobalSearchUI(HomepageUI ui, String tagToBeVerified) {
		
		// Verify that the tag with 'Matching:' text is displayed in Global Search UI
		verifyFilteredTagInGlobalSearchUI(ui, tagToBeVerified, true);
	}
	
	/**
	 * Verifies that the filtered tag / hashtag is NOT displayed beside the 'Matching:' text in Global Search UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param tagToBeVerified - The String content of the tag to be verified as absent
	 */
	public static void verifyFilteredTagIsNotDisplayedInGlobalSearchUI(HomepageUI ui, String tagToBeVerified) {
		
		// Verify that the tag with 'Matching:' text is NOT displayed in Global Search UI
		verifyFilteredTagInGlobalSearchUI(ui, tagToBeVerified, false);
	}
	
	/**
	 * Verifies that the specified element is fully contained inside the EE (ie. it is within its X and Y boundaries)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param cssSelectorInternalElement - The String content of the CSS selector of the element to be verified as being inside the EE
	 */
	public static void verifyElementIsInsideTheEE(HomepageUI ui, RCLocationExecutor driver, String cssSelectorInternalElement) {
		
		log.info("INFO: Now retrieving the element corresponding to the EE");
		ui.switchToEEFrame();
		Element eeElement = driver.switchToActiveElement();
		
		log.info("INFO: Now retrieving the element to be verified as an internal element of the EE");
		Element innerEEElement = driver.getSingleElement(cssSelectorInternalElement);
		
		// Retrieve the X, Y, height and width values for the EE element
		int eeXPos = eeElement.getLocation().x;
		int eeYPos = eeElement.getLocation().y;
		int eeHeight = eeElement.getSize().height;
		int eeWidth = eeElement.getSize().width;
		
		// Retrieve the X, Y, height and width values for the inner EE element
		int eeInnerXPos = innerEEElement.getLocation().x;
		int eeInnerYPos = innerEEElement.getLocation().y;
		int eeInnerHeight = innerEEElement.getSize().height;
		int eeInnerWidth = innerEEElement.getSize().width;
		
		log.info("INFO: Verify that the inner elements left-most position is within the boundaries of the EE");
		verifyBooleanValuesAreEqual(eeInnerXPos > eeXPos && eeInnerXPos < (eeXPos + eeWidth), true);
		
		log.info("INFO: Verify that the inner elements right-most position is within the boundaries of the EE");
		verifyBooleanValuesAreEqual((eeInnerXPos + eeInnerWidth) < (eeXPos + eeWidth), true);
		
		log.info("INFO: Verify that the inner elements upper-most position is within the boundaries of the EE");
		verifyBooleanValuesAreEqual(eeInnerYPos > eeYPos && eeInnerYPos < (eeYPos + eeHeight), true);
		
		log.info("INFO: Verify that the inner elements lower-most position is within the boundaries of the EE");
		verifyBooleanValuesAreEqual((eeInnerYPos + eeInnerHeight) < (eeYPos + eeHeight), true);
	}
	
	/**
	 * Verifies whether the 'Save This' link for any news story is displayed
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStory - The String content of the news story whose 'Save This' link is to be verified as visible / displayed
	 */
	public static void verifyNewsStorySaveThisLinkIsDisplayed(HomepageUI ui, String newsStory) {
		
		// Verify that the 'Save This' link for the specified news story is displayed
		String saveThisCSSSelector = ui.verifyNewsStorySaveLinkIsDisplayedUsingUI(newsStory);
		
		log.info("INFO: Verify that the 'Save This' link was displayed in the UI for the news story with content: " + newsStory);
		Assert.assertNotNull(saveThisCSSSelector, 
								"ERROR: The 'Save This' link for the news story was NOT displayed and was returned as null");
	}
	
	/**
	 * Verifies whether the specified news story is displayed in the Notification Center flyout
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param newsStory - The String content of the news story to be verified as visible / displayed in the Notification Center flyout
	 */
	public static void verifyNewsStoryIsInNotificationCenterFlyout(HomepageUI ui, String newsStory) {
		
		// Verify that the required news story is displayed in the flyout for the Notification Center
		boolean newsStoryIsDisplayed = ui.checkNotificationTitle(newsStory);
		
		log.info("INFO: Verify that the news story was displayed in the Notification Center as expected");
		Assert.assertTrue(newsStoryIsDisplayed, 
							"ERROR: The 'news story was NOT displayed in the Notification Center - a false response was returned");
	}
	
	/**
	 * Verifies that a hashtag link is displayed in the current view
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param hashTag - The String content of the hashtag which is to be verified as visible / displayed in the view
	 */
	public static void verifyHashtagLinkIsDisplayed(HomepageUI ui, String hashTag) {
		
		log.info("INFO: Verify that the hastag with content '" + hashTag + "' is displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible("link=" + hashTag), 
							"ERROR: The hashtag with content '" + hashTag + "' was NOT displayed as expected");
	}
	
	/**
	 * Verifies that a partial mentions link is displayed in the currently active element in the UI and that the mentions link appears in a blue colour
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param mentionsLinkText - The String content of the text which is displayed in the partial mentions link
	 */
	public static void verifyMentionsLinkIsDisplayedInBlueInCurrentActiveElement(HomepageUI ui, RCLocationExecutor driver, String mentionsLinkText) {
		
		final String MENTIONS_LINK_COLOUR_A = "#4178BE";
		final String MENTIONS_LINK_COLOUR_B = "#1970B0";
		String mentionsLinkToVerify = "link=" + mentionsLinkText;
		
		// Retrieve all elements which match the mentions link in the currently active element
		List<Element> listOfMatchingElements = driver.switchToActiveElement().getElements(mentionsLinkToVerify);
		
		log.info("INFO: Verify that the mentions links containing the text '" + mentionsLinkText + "' are displayed in the currently active UI element");
		Assert.assertTrue(listOfMatchingElements.size() > 0, 
							"ERROR: There were no elements found in the currently active element which match the mentions link with CSS selector: " + mentionsLinkToVerify);
		
		// Retrieve the matching mentions link from the list of all mentions links found in the currently active element
		Element matchingMentionsLink = null;
		boolean foundMatchingElement = false;
		int index = 0;
		while(index < listOfMatchingElements.size() && foundMatchingElement == false) {
			Element currentMentionsLink = listOfMatchingElements.get(index);
			
			if(currentMentionsLink.getText().equals(mentionsLinkText)) {
				matchingMentionsLink = currentMentionsLink;
				foundMatchingElement = true;
			}
			index ++;
		}
		
		log.info("INFO: Verify that a matching mentions link has been found in the UI");
		Assert.assertNotNull(matchingMentionsLink, 
								"ERROR: A matching mentions link with mentions text '" + mentionsLinkText + "' could NOT be found in the UI");
		
		// Retrieve the colour of the mentions link
		String colourOfMentionsLink = ui.getElementTextColourAsHex(matchingMentionsLink);
		
		log.info("INFO: Verify that the mentions link is displayed in a blue colour as expected");
		Assert.assertTrue(colourOfMentionsLink.equals(MENTIONS_LINK_COLOUR_A) || colourOfMentionsLink.equals(MENTIONS_LINK_COLOUR_B), 
							"ERROR: The mentions link was incorrectly displayed in the hexadecimal colour '" + colourOfMentionsLink + "'");
	}
	
	/**
	 * Verifies that a video is attached to a status update in the specified news story and contains a thumbnail, the expected title and the expected content
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStoryContent - The String content of the news story which contains the video file
	 * @param videoFileTitle - The String content of the title of the video (to be verified)
	 * @param videoFileContent - The String content of the content of the video (to be verified)
	 */
	public static void verifyVideoFileAttachmentWithStatusUpdate(HomepageUI ui, RCLocationExecutor driver, String newsStoryContent, String videoFileTitle, String videoFileContent) {
		
		// Create the CSS selectors to be used for all verifications
		String thumbnailCSS = HomepageUIConstants.SU_VIDEOFILEATTACHMENT_THUMBNAIL.replaceAll("PLACEHOLDER", newsStoryContent);
		String titleCSS = HomepageUIConstants.SU_VIDEOFILEATTACHMENT_CONTENT_TITLE.replaceAll("PLACEHOLDER", newsStoryContent);
		String contentCSS = HomepageUIConstants.SU_VIDEOFILEATTACHMENT_CONTENT_METADATA.replaceAll("PLACEHOLDER", newsStoryContent);
		
		log.info("INFO: Verify that the thumbnail component of the video attached to the status update is displayed");
		Assert.assertTrue(ui.fluentWaitElementVisible(thumbnailCSS), 
							"ERROR: The thumbnail component of the video attached to the status update is NOT displayed");
		
		log.info("INFO: Verify that the title of the video attached to the status update has expected content of '" + videoFileTitle + "'");
		Assert.assertTrue(driver.getFirstElement(titleCSS).getText().equals(videoFileTitle), 
							"ERROR: The title of the video attached to the status update did NOT have the expected content of '" + videoFileTitle + "' but instead contained '" + driver.getFirstElement(titleCSS).getText() + "'");
		
		log.info("INFO: Verify that the content of the video attached to the status update contains expected content of '" + videoFileContent + "'");
		Assert.assertTrue(driver.getFirstElement(contentCSS).getText().indexOf(videoFileContent) > -1, 
							"ERROR: The content of the video attached to the status update did NOT contain the expected content of '" + videoFileContent + "'");
	}
	
	/**
	 * Verifies that Activities UI is open and displayed for the specified activity
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param activityToBeVerified - The Activity instance of the activity to be verified in Activities UI
	 * @param ownerOptionsAreDisplayed - True if the activity owner options are displayed (Add Entry, Add To Do etc.), false otherwise
	 */
	public static void verifyActivitiesUIIsDisplayed(HomepageUI ui, RCLocationExecutor driver, Activity activityToBeVerified, boolean ownerOptionsAreDisplayed) {
		
		log.info("INFO: Wait for Activities UI to load");
		ui.fluentWaitTextPresent(Data.getData().feedForTheseEntries);
		
		log.info("INFO: Now expanding activity outline section for this activity");
		ui.clickLinkWait(ActivitiesUIConstants.ActivityOutline_More_ExpandDescription);
		
		if(ownerOptionsAreDisplayed) {
			log.info("INFO: Verify that the 'Add Section' button is displayed in Activities UI");
			Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.AddSection),
								"ERROR: The 'Add Section' button was NOT displayed in Activities UI");
			
			log.info("INFO: Verify that the 'Add Entry' button is displayed in Activities UI");
			Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.New_Entry),
								"ERROR: The 'Add Entry' button was NOT displayed in Activities UI");
			
			log.info("INFO: Verify that the 'Add To Do Item' button is displayed in Activities UI");
			Assert.assertTrue(ui.fluentWaitPresent(ActivitiesUIConstants.AddToDo),
								"ERROR: The 'Add To Do Item' button was NOT displayed in Activities UI");
		}		
		log.info("INFO: Verify that the tab title for the Activities UI screen matches the name of the activity to be verified");
		Assert.assertTrue(driver.getTitle().trim().equals(activityToBeVerified.getTitle().trim() + " Activity"), 
							"ERROR: The tab title for the Activities UI screen did NOT match the name of the activity to be verified");
		
		log.info("INFO: Verify that the title of the activity is displayed in Activities UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(activityToBeVerified.getTitle().trim()), 
							"ERROR: The title of the activity was NOT displayed in Activities UI");
		
		log.info("INFO: Verify that the description / goal of the activity is displayed in Activities UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(activityToBeVerified.getContent().trim()), 
							"ERROR: The description / goal of the activity was NOT displayed in Activities UI");
		
		log.info("INFO: Verify that the tags for the activity are displayed in Activities UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(activityToBeVerified.getTags().get(0).getTerm().toLowerCase().trim()), 
							"ERROR: The tags for the activity were NOT displayed in Activities UI");
	}
	
	/**
	 * Verifies that the Action Required screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyActionRequiredIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now verifying that the Action Required view is displayed in the UI");
		
		log.info("INFO: Waiting for the Action Required view to load");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries), 
							"ERROR: The Action Required view did NOT load correctly / in time with visible text: " + Data.getData().feedForTheseEntries);
		
		log.info("INFO: Verify that the Action Required description text is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().ActionRequiredText), 
							"ERROR: The Action Required description text was NOT displayed in the Action Required view");
	}
	
	/**
	 * Verifies that the My Notifications screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyMyNotificationsIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now verifying that the My Notifications view is displayed in the UI");
		
		log.info("INFO: Waiting for the My Notifications view to load");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries), 
							"ERROR: The My Notifications view did NOT load correctly / in time with visible text: " + Data.getData().feedForTheseEntries);
		
		log.info("INFO: Verify that the My Notifications description text is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().MyNotificationsText), 
							"ERROR: The My Notifications description text was NOT displayed in the My Notifications view");
		
		log.info("INFO: Verify that the 'For Me' tab is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.ForMeTab),
							"ERROR: The 'For Me' tab was NOT displayed in the My Notifications view");
		
		log.info("INFO: Verify that the 'From Me' tab is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.FromMeTab),
							"ERROR: The 'From Me' tab was NOT displayed in the My Notifications view");
	}
		
	/**
	 * Verifies that the specified activity entry is displayed in Activities UI along with all relevant Activities UI components
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param parentActivity - The Activity instance of the activity which is to be displayed in Activities UI
	 * @param activityEntry - The ActivityEntry instance of the activity entry which is to be displayed in Activities UI
	 * @param ownerOptionsAreDisplayed - True if the activity owner options are displayed (Add Entry, Add To Do etc.), false otherwise
	 */
	public static void verifyActivityEntryIsDisplayedInActivitiesUI(HomepageUI ui, RCLocationExecutor driver, Activity parentActivity, ActivityEntry activityEntry, boolean ownerOptionsAreDisplayed) {
		
		// Verify that Activities UI and all its components are displayed
		verifyActivitiesUIIsDisplayed(ui, driver, parentActivity, ownerOptionsAreDisplayed);
		
		log.info("INFO: Verify that the title of the activity entry is displayed in Activities UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(activityEntry.getTitle().trim()), 
							"ERROR: The title of the activity entry was NOT displayed in Activities UI");
		
		log.info("INFO: Verify that the description of the activity entry is displayed in Activities UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(activityEntry.getContent().trim()), 
							"ERROR: The description of the activity entry was NOT displayed in Activities UI");
		
		log.info("INFO: Verify that the tags for the activity entry are displayed in Activities UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(activityEntry.getTags().get(0).getTerm().toLowerCase().trim()), 
							"ERROR: The tags for the activity entry were NOT displayed in Activities UI");
	}
	
	/**
	 * Verifies the presence / absence of the 'Shared Externally' header with a news story in the AS
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStoryContent - The String content of the main news story
	 * @param newsStoryDescription - The String content of the news story description / content (null if this component is NOT to be verified)
	 * @param comment1 - The String content of the first comment posted with the news story (null if this component is NOT to be verified)
	 * @param comment2 - The String content of the second comment posted with the news story (null if this component is NOT to be verified)
	 * @param TEST_FILTERS - The String[] array of all filters to be tested
	 * @param sharedExtHeaderIsDisplayed - True if the 'Shared Externally' header is to be verified as displayed, False if it is to be verified as absent
	 */
	public static void verifySharedExternallyHeader(HomepageUI ui, RCLocationExecutor driver, String newsStoryContent, String newsStoryDescription, String comment1,
														String comment2, String[] TEST_FILTERS, boolean sharedExtHeaderIsDisplayed) {
		// Create the CSS selectors for the icon and the message
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStoryContent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStoryContent);
				
		for(String filter : TEST_FILTERS) {
			// Verify that the relevant news story components are displayed in the current filter
			if(newsStoryDescription == null && comment1 == null && comment2 == null) {
				verifyItemsInAS(ui, driver, new String[]{newsStoryContent}, filter, true);
			} else if(comment1 == null && comment2 == null) {
				verifyItemsInAS(ui, driver, new String[]{newsStoryContent, newsStoryDescription}, filter, true);
			} else if(comment2 == null){
				verifyItemsInAS(ui, driver, new String[]{newsStoryContent, newsStoryDescription, comment1}, filter, true);
			} else {
				verifyItemsInAS(ui, driver, new String[]{newsStoryContent, newsStoryDescription, comment1, comment2}, filter, true);
			}
			// Verify that the shared externally header is displayed / not displayed in the current filter
			verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, null, sharedExtHeaderIsDisplayed);
		}
	}
	
	/**
	 * Verifies that Profiles UI is displayed for a user viewing their own profile
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userProfile - The User instance of the user whose profile is to be verified as displayed
	 */
	public static void verifyProfilesUIIsDisplayed_ViewingUsersOwnProfile(HomepageUI ui, User userProfile) {
		
		// Wait for Profiles UI to load and verify that the common Profiles UI components are displayed
		waitForProfilesUIToLoadAndVerifyCommonProfilesUIComponents_AllUsers(ui, userProfile);
		
		log.info("INFO: Verify that the download v-card button is displayed in Profiles UI");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.DownloadVCardButton),
							"ERROR: The download v-card button was NOT displayed in Profiles UI");
	}
	
	/**
	 * Verifies that Profiles UI is displayed for a user viewing a specified users profile
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userProfile - The User instance of the user whose profile is to be verified as displayed
	 */
	public static void verifyProfilesUIIsDisplayed_ViewingUsersProfileAsAnotherUser(HomepageUI ui, User userProfile) {
		
		// Wait for Profiles UI to load and verify that the common Profiles UI components are displayed
		waitForProfilesUIToLoadAndVerifyCommonProfilesUIComponents_AllUsers(ui, userProfile);
		
		log.info("INFO: Verify that the invite to my network button is displayed in Profiles UI");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.Invite_OnPrem),
							"ERROR: The invite to my network button was NOT displayed in Profiles UI");
		
		log.info("INFO: Verify that the follow button is displayed in Profiles UI");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.FollowPerson),
							"ERROR: The follow button was NOT displayed in Profiles UI");
		
		log.info("INFO: Verify that the share a file button is displayed on the Profiles UI");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.ShareaFileButton),
							"ERROR: The share a file button was NOT displayed in Profiles UI");
	}
	
	/**
	 * Verifies that the AS Search components are displayed in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyASSearchIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Verify that the AS Search input box is displayed in the UI with CSS selector: " + HomepageUIConstants.AS_SearchTextBox);
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.AS_SearchTextBox),
								"ERROR: The AS Search input box was NOT displayed in the UI");

		log.info("INFO: Verify that the 'X' icon for closing the AS Search is displayed in the UI with CSS selector: " + HomepageUIConstants.AS_SearchClose);
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.AS_SearchClose),
								"ERROR: The 'X' icon for closing the AS Search was NOT displayed in the UI");
	}
	
	/**
	 * Verifies that the AS Search components are NOT displayed in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyASSearchIsNotDisplayed(HomepageUI ui) {
		
		log.info("INFO: Verify that the AS Search input box is NOT displayed in the UI with CSS selector: " + HomepageUIConstants.AS_SearchTextBox);
		Assert.assertFalse(ui.isElementVisible(HomepageUIConstants.AS_SearchTextBox),
								"ERROR: The AS Search input box was unexpectedly displayed in the UI");

		log.info("INFO: Verify that the 'X' icon for closing the AS Search is NOT displayed in the UI with CSS selector: " + HomepageUIConstants.AS_SearchClose);
		Assert.assertFalse(ui.isElementVisible(HomepageUIConstants.AS_SearchClose),
								"ERROR: The 'X' icon for closing the AS Search was unexpectedly displayed in the UI");
	}
	
	/**
	 * Verifies that the AS filter component is displayed in the UI when the AS Search panel is closed
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 */
	public static void verifyASFilterIsDisplayedWhenASSearchIsClosed(RCLocationExecutor driver) {
		
		/**
		 * Simply testing for the presence of the filter in the UI is NOT a consistent verification since Selenium still returns a true result
		 * for this even when the element is hidden (ie. when the AS Search panel is open)
		 * 
		 * Instead - the AS filter element gains a 'tabindex' attribute with a value of '-1' when the AS Search panel is open and loses that attribute
		 * completely when the AS Search panel is closed (ie. the attribute is returned as null).
		 * 
		 * Therefore this verification will test that the AS filter has a 'tabindex' attribute value of 'null'
		 */
		
		log.info("INFO: Verify that the 'tabindex' attribute of the AS filter component is a null attribute when the AS Search panel is closed");
		Assert.assertNull(driver.getSingleElement(HomepageUIConstants.FilterBy).getAttribute("tabindex"),
							"ERROR: The 'tabindex' attribute of the AS filter component was NOT a null attribute when the AS Search panel is closed");
	}
	
	/**
	 * Verifies that the AS filter component is NOT displayed in the UI when the AS Search panel is open
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 */
	public static void verifyASFilterIsNotDisplayedWhenASSearchIsOpen(RCLocationExecutor driver) {
		
		/**
		 * Simply testing for the absence of the filter in the UI is NOT a consistent verification since Selenium still returns a true result
		 * for this even when the element is hidden (ie. when the AS Search panel is open)
		 * 
		 * Instead - the AS filter element gains a 'tabindex' attribute with a value of '-1' when the AS Search panel is open and loses that attribute
		 * completely when the AS Search panel is closed (ie. the attribute is returned as null).
		 * 
		 * Therefore this verification will test that the AS filter does have a 'tabindex' attribute value of '-1'
		 */
		
		log.info("INFO: Verify that the 'tabindex' attribute of the AS filter component has a value of -1 when the AS Search panel is open");
		verifyStringValuesAreEqual(driver.getSingleElement(HomepageUIConstants.FilterBy).getAttribute("tabindex").trim(), "-1");
	}
	
	/**
	 * Simple verification for whether the specified text is displayed in the UI
	 * 
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param textToBeVerified - The String content of the text to be verified as displayed
	 * @return - True if the text is displayed, False if it is not displayed
	 */
	public static boolean isTextDisplayed(RCLocationExecutor driver, String textToBeVerified) {
		
		log.info("INFO: Now verifying if the text is displayed with content: " + textToBeVerified);
		return driver.isTextPresent(textToBeVerified);
	}
	
	/**
	 * Verifies that the My Notifications and Notification Center badges match the specified value
	 * 
	 * @param badgeValueToBeVerified - The Integer value to be verified
	 */
	public static void verifyMyNotificationsAndNotificationsCenterBadgeValues(RCLocationExecutor driver, int badgeValueToBeVerified) {
		
		// Retrieve the badge values for both the My Notifications and Notification Center badges
		int myNotificationsCounter = UIEvents.getMyNotificationsBadgeValue(driver);
		int notificationCenterCounter = UIEvents.getNotificationCenterBadgeValue(driver);
		
		// Verify that the My Notifications badge counter and Notification Center badge counters match the required value
		verifyIntValuesAreEqual(myNotificationsCounter, badgeValueToBeVerified);
		verifyIntValuesAreEqual(notificationCenterCounter, badgeValueToBeVerified);
	}
	
	/**
	 * Verifies that the mentions badge matches the specified value
	 * 
	 * @param badgeValueToBeVerified - The Integer value to be verified
	 */
	public static void verifyMentionsBadgeValue(RCLocationExecutor driver, int badgeValueToBeVerified) {
		
		// Retrieve the badge value for the Mentions badge
		int mentionsCounter = UIEvents.getMentionsBadgeValue(driver);
		
		// Verify that the Mentions badge counter matches the required value
		verifyIntValuesAreEqual(mentionsCounter, badgeValueToBeVerified);
	}
	
	/**
	 * Verifies that the specified news story is displayed in the AS along with its specified file and all other relevant components
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param fileNewsStory - The String content of the file news story to be verified as displayed
	 * @param fileToBeDisplayed - The BaseFile instance of the file which is linked to the news story
	 */
	public static void verifyFilesNewsStoryIsDisplayedInAS(HomepageUI ui, RCLocationExecutor driver, String fileNewsStory, BaseFile fileToBeDisplayed) {
		
		// Create the file name for the file to be verified
		String fileName =  fileToBeDisplayed.getRename() + fileToBeDisplayed.getExtension();
		
		// Create the CSS selector for the file news story element
		String fileNewsStoryCSS = HomepageUIConstants.NewsStoryInnerDiv_FileNewsStory;
		fileNewsStoryCSS = fileNewsStoryCSS.replace("PLACEHOLDER", fileNewsStory);
		fileNewsStoryCSS = fileNewsStoryCSS.replace("REPLACE_THIS", fileName);
		
		// Retrieve the file news story element
		Element fileNewsStoryElement = driver.getFirstElement(fileNewsStoryCSS);
		
		// Retrieve the text from the news story element
		String elementText = fileNewsStoryElement.getText();
		log.info("INFO: The file news story element has been found with text content: " + elementText);
		
		boolean previewUnavailable = elementText.contains(Data.FILE_PREVIEW_NOT_AVAILABLE);
		int numberOfRetries = 0;
		while(previewUnavailable == true && numberOfRetries < 3) {
			log.info("INFO: Refreshing the UI since the image did not display correctly");
			UIEvents.refreshPage(driver);
			
			// Wait for the AS to load
			ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
			
			// Click 'Show More' to expand the news feed
			UIEvents.clickShowMore(ui);
			
			// Reset the AS view back to the top of the AS
			UIEvents.resetASToTop(ui);
			
			log.info("INFO: Retry " + (numberOfRetries + 1) + " of 3: Waiting for the image to be displayed");
			
			// Retrieve the element again and obtain the text from the element
			fileNewsStoryElement = driver.getFirstElement(fileNewsStoryCSS);
			elementText = fileNewsStoryElement.getText();
			
			// Verify that the element now contains an image preview
			previewUnavailable = elementText.contains(Data.FILE_PREVIEW_NOT_AVAILABLE);
			
			numberOfRetries ++;
		}
		log.info("INFO: Verify that the image preview is displayed without the text '" + Data.FILE_PREVIEW_NOT_AVAILABLE + "'");
		Assert.assertFalse(previewUnavailable, 
							"ERROR: The image preview was displayed with the text '" + Data.FILE_PREVIEW_NOT_AVAILABLE + "'");
		
		log.info("INFO: Verify that the news story element contains the news story content: " + fileNewsStory);
		Assert.assertTrue(elementText.contains(fileNewsStory), 
							"ERROR: The news story element did NOT contain the news story content: " + fileNewsStory);
		
		log.info("INFO: Verify that the news story element contains the tag with content: 'Tags: " + fileToBeDisplayed.getTags().toLowerCase().trim() + "'");
		Assert.assertTrue(elementText.contains("Tags: " + fileToBeDisplayed.getTags().toLowerCase().trim()), 
							"ERROR: The news story element did NOT contain the tags string with content: 'Tags: " + fileToBeDisplayed.getTags().toLowerCase().trim() + "'");
		
		// Create the CSS selector for the image preview
		if(fileToBeDisplayed.getExtension().equals(".jpg")) {
			String fileNewsStoryImagePreview = HomepageUIConstants.NewsStoryInnerDiv_FileNewsStory_Image.replace("PLACEHOLDER", fileName);
			
			log.info("INFO: Verify that the news story element contains an image preview for the file with file name: " + fileName);
			Assert.assertTrue(fileNewsStoryElement.isElementPresent(fileNewsStoryImagePreview), 
								"ERROR: The news story element did NOT contain an image for the file with file name: " + fileName);
		} 
		log.info("INFO: Verify that the news story element contains a timestamp");
		Assert.assertTrue(fileNewsStoryElement.isElementPresent(HomepageUIConstants.NewsStoryInnerDiv_FileNewsStory_Timestamp),
							"ERROR: The news story element did NOT contain a timestamp");
	}
	
	/**
	 * Verifies that the specified news story is NOT displayed in the AS along with its specified file and all other relevant components
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param fileNewsStory - The String content of the file news story to be verified as displayed
	 * @param fileToNotBeDisplayed - The BaseFile instance of the file which is linked to the news story
	 */
	public static void verifyFilesNewsStoryIsNotDisplayedInAS(HomepageUI ui, RCLocationExecutor driver, String fileNewsStory, BaseFile fileToNotBeDisplayed) {
		
		// Create the file name for the file to be verified
		String fileName =  fileToNotBeDisplayed.getRename() + fileToNotBeDisplayed.getExtension();
		
		// Create the CSS selector for the file news story element which is to NOT be displayed
		String fileNewsStoryCSS = HomepageUIConstants.NewsStoryInnerDiv_FileNewsStory;
		fileNewsStoryCSS = fileNewsStoryCSS.replace("PLACEHOLDER", fileNewsStory);
		fileNewsStoryCSS = fileNewsStoryCSS.replace("REPLACE_THIS", fileName);
		
		log.info("INFO: Verify that the file news story is NOT displayed with content: " + fileNewsStory);
		Assert.assertFalse(ui.isElementVisible(fileNewsStoryCSS), 
							"ERROR: The file news story was displayed in the AS with content: " + fileNewsStory);
	}
	
	/**
	 * Verifies that the specified file news story is displayed in the AS while another specified file news story is NOT displayed in the AS
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param newsStoryToBeDisplayed - The String content of the file news story to be verified as displayed
	 * @param newsStoryToBeAbsent - The String content of the file news story to be verified as absent
	 * @param fileToBeDisplayed - The BaseFile instance of the file which is linked to the news story
	 */
	public static void verifyFileNewsStoriesAsPresentAndAbsentInAS(HomepageUI ui, RCLocationExecutor driver, String newsStoryToBeDisplayed, String newsStoryToBeAbsent, BaseFile fileToBeDisplayed) {
		
		// Verify that the expected news story is NOT displayed in the AS
		verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, newsStoryToBeAbsent, fileToBeDisplayed);
		
		// Verify that the expected news story is displayed in the AS
		verifyFilesNewsStoryIsDisplayedInAS(ui, driver, newsStoryToBeDisplayed, fileToBeDisplayed);
	}
	
	/**
	 * Waits for Profiles UI to load and verifies the commonly displayed components in Profiles UI
	 * (ie. these components are displayed no matter which user is viewing a profile)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param userProfile - The User instance of the user whose profile is to be verified as displayed
	 */
	private static void waitForProfilesUIToLoadAndVerifyCommonProfilesUIComponents_AllUsers(HomepageUI ui, User userProfile) {
		
		log.info("INFO: Ensure that the Profiles UI has loaded before proceeding");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		log.info("INFO: Verify that the email address link for " + userProfile.getDisplayName() + " is displayed on the Profiles screen");
		Assert.assertTrue(ui.fluentWaitPresent("link=" + userProfile.getEmail().toLowerCase()), 
							"ERROR: The email address link for " + userProfile.getDisplayName() + " was NOT displayed on the Profiles screen");
		
		log.info("INFO: Verify that the send email button is displayed in Profiles UI");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUIConstants.SendEmailButton),
							"ERROR: The send email button was NOT displayed in Profiles UI");
	}
	
	/**
	 * Verifies that the filtered tag / hashtag with 'Matching:' text is displayed / absent in Global Search UI
	 * 
	 * @param ui - The HomepageUI instance to invoke the fluentWaitPresent() method
	 * @param tagToBeVerified - The String content of the tag to be verified as displayed
	 * @param isDisplayed - True if the tag is to be verified as displayed, false if it is to be verified as absent
	 */
	private static void verifyFilteredTagInGlobalSearchUI(HomepageUI ui, String tagToBeVerified, boolean isDisplayed) {
		
		String filteredTagCSS = GlobalsearchUI.MatchingTag.replaceAll("PLACEHOLDER", tagToBeVerified.toLowerCase());
		if(isDisplayed) {
			log.info("INFO: Verify that the tag with content '" + tagToBeVerified + "' is displayed beside the text 'Matching' in the Global Search UI screen");
			Assert.assertTrue(ui.fluentWaitPresent(filteredTagCSS), 
						"ERROR: The the tag with content '" + tagToBeVerified + "' was NOT displayed beside the text 'Matching' in the Global Search UI screen");
		} else {
			log.info("INFO: Verify that neither the 'Matching' text nor the tag with content '" + tagToBeVerified + "' is displayed in the Global Search UI screen");
			Assert.assertFalse(ui.isElementVisible(filteredTagCSS), 
						"ERROR: The the tag with content '" + tagToBeVerified + "' was displayed beside the text 'Matching' in the Global Search UI screen");
		}
	}
	
	/**
	 * Changes the filter in the UI and waits for all UI components to load having changed the filter
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitTextPresent method
	 * @param filter - The filter to assign in the UI (null if the filter is NOT to be changed)
	 * @param visible - A boolean which refers to whether the items being verified are present in the UI (true) or not present (false)
	 */
	private static void changeFilterAndWaitForASComponentsToLoad(HomepageUI ui, String filter, boolean visible) {
		
		log.info("INFO: Filtering the Activity Stream by '" + filter + "'");
		ui.filterBy(filter);
			
		// Wait for all page components to load before proceeding
		waitForASComponentsToLoad(ui, visible);
	}
	
	/**
	 * Ensures that all relevant AS components have loaded correctly
	 * 
	 * @param ui - The HomepageUI object used to invoke the clickIfVisible and fluentWaitTextPresent methods
	 * @param visible - A boolean which indicates whether the verifications to follow will be for elements that should be present (true) or not present (false)
	 */
	private static void waitForASComponentsToLoad(HomepageUI ui, boolean visible) {
		
		log.info("INFO: Waiting for all AS components to load before verifications are carried out");
		
		if(!visible) {
			log.info("INFO: Wait for Activity Stream to load");
			ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		}
		log.info("INFO: Clicking on the 'Show More' link to make the test more robust");
		ui.clickIfVisible(HomepageUIConstants.ShowMore);
		
		log.info("INFO: Reset the AS back to the top since news stories being verified are more likely to be at the beginning of the AS");
		ui.resetASToTop();
	}

	/**
	 * Ensures that all relevant Global Search components have loaded correctly
	 * 
	 * @param ui - The HomepageUI object used to invoke the clickIfVisible and fluentWaitTextPresent methods
	 */
	private static void waitForGlobalSearchComponentsToLoad(HomepageUI ui) {
		
		log.info("INFO: Waiting for all Global Search components to load before verifications are carried out");
		
		log.info("INFO: Wait for the Global Search Results Stream to load");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseSearchResults);
	}
	
	/**
	 * Performs all required verifications for all String items in the verifications array
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitTextPresent method during verification
	 * @param driver - The RCLocationExecutor object used to invoke the isTextPresent method during verification
	 * @param verifications - The array of Strings to be verified as displayed / not displayed in the UI
	 * @param filter - The filter in which the item should be displayed
	 * @param visible - A boolean which indicates whether the verifications to follow will be for elements that should be present (true) or not present (false)
	 */
	private static void executeAllStringItemVerifications(HomepageUI ui, RCLocationExecutor driver, String[] verifications, String filter, boolean visible) {
		if (visible){
			verifyItemIsDisplayed(ui, verifications, filter);
		}
		else{
			verifyItemIsNotDisplayed(driver, verifications, filter);
		}
	}

	/**
	 * Performs all required verifications for all CSS selectors in the verifications array
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitPresent method
	 * @param driver - The RCLocationExecutor object used to invoke the isElementPresent method during verification
	 * @param verifications - The array of CSS selectors(Strings) to be verified as displayed / not displayed in the UI
	 * @param filter - The filter in which the item should be displayed
	 * @param visible - A boolean which indicates whether the verifications to follow will be for elements that should be present (true) or not present (false)
	 */
	private static void executeAllElementVerifications(HomepageUI ui, RCLocationExecutor driver, String[] verifications, String filter, boolean visible) {
		if (visible){
			verifyElementIsDisplayed(ui, verifications, filter);
		}
		else{
			verifyElementIsNotDisplayed(driver, verifications, filter);
		}
	}
	
	/**
	 * Verifies that all of the items are displayed in the array of items to be verified
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitTextPresent method
	 * @param itemContents - The array of items to be verified as displayed
	 * @param filter - The filter in which the item should be displayed
	 */
	private static void verifyItemIsDisplayed(HomepageUI ui, String[] itemContents, String filter) {		
		for(int index = 0; index < itemContents.length; index ++) {
			
			if(filter != null) {
				log.info("INFO: Verify that an item is displayed in '" + filter + "' with content: " + itemContents[index]);
			} else {
				log.info("INFO: Verify that an item is displayed with content: " + itemContents[index]);
			}
			Assert.assertTrue(ui.fluentWaitTextPresentRefresh(itemContents[index]),
							 	"ERROR: An item was NOT displayed in '" + filter + "' with content: " + itemContents[index]);
		}
	}

	/**
	 * Verifies that all of the items are not displayed in the array of items to be verified
	 * 
	 * @param driver - The RCLocationExecutor object used to invoke the isTextPresent method
	 * @param itemContents - The array of items to be verified as not displayed
	 * @param filter - The filter in which the item should not be displayed
	 */
	private static void verifyItemIsNotDisplayed(RCLocationExecutor driver, String[] itemContents, String filter) {		
		for(int index = 0; index < itemContents.length; index ++) {
			
			if(filter != null) {
				log.info("INFO: Verify that an item is NOT displayed in '" + filter + "' with content: " + itemContents[index]);
			} else {
				log.info("INFO: Verify that an item is NOT displayed with content: " + itemContents[index]);
			}
			Assert.assertTrue(driver.isTextNotPresent(itemContents[index]),
							 "ERROR: An item was displayed in '" + filter + "' with content: " + itemContents[index]);
		}
	}

	/**
	 * Verifies that all of the elements are displayed in the array of elements to be verified
	 * 
	 * @param ui - The HomepageUI object used to invoke the fluentWaitPresent method
	 * @param cssElements - The array of CSS selector elements to be verified as displayed
	 * @param filter - The filter in which the element should be displayed
	 */
	private static void verifyElementIsDisplayed(HomepageUI ui, String[] cssElements, String filter) {		
		for(int index = 0; index < cssElements.length; index ++) {
			
			if(filter != null) {
				log.info("INFO: Verify that an element is displayed in '" + filter + "' with CSS selector: " + cssElements[index]);
			} else {
				log.info("INFO: Verify that an element is displayed with CSS selector: " + cssElements[index]);
			}
			Assert.assertTrue(ui.fluentWaitPresent(cssElements[index]),
							 	"ERROR: An element was NOT displayed in '" + filter + "' with CSS selector: " + cssElements[index]);
		}
	}

	/**
	 * Verifies that all of the elements are displayed in the array of elements to be verified
	 * 
	 * @param driver - The RCLocationExecutor object used to invoke the isElementPresent method
	 * @param itemContents - The array of CSS selector elements to be verified as displayed
	 * @param filter - The filter in which the element should be displayed
	 */
	private static void verifyElementIsNotDisplayed(RCLocationExecutor driver, String[] cssElements, String filter) {		
		for(int index = 0; index < cssElements.length; index ++) {
			
			if(filter != null) {
				log.info("INFO: Verify that an element is NOT displayed in '" + filter + "' with CSS selector: " + cssElements[index]);
			} else {
				log.info("INFO: Verify that an element is NOT displayed with CSS selector: " + cssElements[index]);
			}
			Assert.assertFalse(driver.isElementPresent(cssElements[index]),
							 "ERROR: An element was displayed in '" + filter + "' with CSS selector: " + cssElements[index]);
		}
	}
}