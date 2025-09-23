package com.ibm.conn.auto.webui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.TouchpointUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;

public class TouchpointUI extends ICBaseUI {

	public TouchpointUI(RCLocationExecutor driver) {
		super(driver);
		homeui=HomepageUI.getGui(cfg.getProductName(), driver);
	}

	protected static Logger log = LoggerFactory.getLogger(TouchpointUI.class);
	HomepageUI homeui;

	public static TouchpointUI getGui(String product, RCLocationExecutor driver) {
		return new TouchpointUI(driver);
	}
	
	/**
	 * getTagEntry - 
	 * @param title
	 * @return String - CSS location value of computed web element
	 */
	public static String getTagEntry(String title) {
		return "css=#tags-collection-wrapper #tags-collection li[title='" + title + "']"; 
	}
	/**
	 * getTagEntryRemoveIcon - 
	 * @param title
	 * @return String - CSS location value of computed web element
	 */
	public static String getTagEntryRemoveIcon(String title) {
		return getTagEntry(title)+" img";
	}
	/**
	 * getTagEntryText - 
	 * @param title
	 * @return String - CSS location value of computed web element
	 */
	public static String getTagEntryText(String title) {
		return getTagEntry(title)+">span";
	}
	/**
	 * getSuggestedInterest - 
	 * @param title
	 * @return String - CSS location value of computed web element
	 */
	public static String getSuggestedInterest(String title) {
		return "css=#content-profileTags #tags-from-colleagues li[data-value='"+title+"'] img"; 
	}
	/**
	 * getFollowLink -
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getFollowLink(String name) {
		return "//li//span[@title='" + name + "']//ancestor::div[contains(@class,'colleague-info')]//following-sibling::div[contains(@class,'follow-button')]/span[text()='Follow']";
	}
	/**
	 * getFollowButtonText -
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getFollowButtonText(String name) {
		return "//li//span[@title='" + name + "']//ancestor::div[contains(@class,'colleague-info')]//following-sibling::div[contains(@class,'follow-button')]";
	}

	/**
	 * getUserCard -
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getUserCard(String name) {
		return "//span[@title='" + name + "']//ancestor::li";
	}

	/**
	 * getPersonFromList -
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getPersonFromList(int i) {
		return "//li[" + i + "]//div/span[@class='colleague-line1']";
	}

	/**
	 * getPersonFromFollowedList -
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getPersonFromFollowedList(String name) {
		return "css=#followed-experts-collection li[title='" + name + "']";
	}

	/**
	 * getStopFollowingLink -
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getStopFollowingLink(String name) {
		return "//li//span[@title='" + name + "']//ancestor::div[contains(@class,'colleague-info')]//following-sibling::div[contains(@class,'follow-button')]/span[contains(text(),'Stop Following')]";
	}
	
	/**
	 * getCommunitiesFromFollowedList -
	 * 
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getCommunitiesFromFollowedList(String name) {
		return "css=#followed-communities-collection li[title='" + name + "']";
	}

	/**
	 * getCommunitiesFromList -
	 * 
	 * @param position
	 * @return String - CSS location value of computed web element
	 */
	public static String getCommunitiesFromList(int position) {
		return "//div[@id='content-followCommunities']//ul[@id='communityTypeahead-result']//li[" + position
				+ "]//div/span[@class='colleague-line1']";

	}

	/**
	 * getcommStopFollowingLink -
	 * 
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getcommStopFollowingLink(String name) {
		return "//div[@id='communities-collection']//ul[@id='communityTypeahead-result'] //li//span[@title='" + name
				+ "']//ancestor::div[@class='box colleague-info']//following-sibling::div[contains(@class,'follow-button')]/span[text()='Stop Following']";
	}

	/**
	 * getcommFollowLink -
	 * 
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getcommFollowLink(String name) {
		return "//div[@id='communities-collection']//ul[@id='communityTypeahead-result'] //li//span[@title='" + name
				+ "']//ancestor::div[@class='box colleague-info']//following-sibling::div[contains(@class,'follow-button')]/span[text()='Follow']";
	}

	/**
	 * getcommFollowButtonText -
	 * 
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getcommFollowButtonText(String name) {
		return "//div[@id='communities-collection']//ul[@id='communityTypeahead-result'] //li//span[@title='" + name
				+ "']//ancestor::div[@class='box colleague-info']//following-sibling::div[contains(@class,'follow-button')]";
	}

	/**
	 * getCommunityCard -
	 * 
	 * @param name
	 * @return String - CSS location value of computed web element
	 */
	public static String getCommunityCard(String name) {
		return "//div[@id='communities-collection']//ul[@id='communityTypeahead-result']//span[@title='" + name
				+ "']//ancestor::li";
	}

	
	/**
	 * For existing user who do not get touchpoint(on-boarding) screens by default, it goes to the homepage upon login. This method hits the
	 * Touchpoint URL again after login.
	 * @param testUser - The User instance of the user to be logged in
	 * @param preserveInstance - A boolean value.  This will be false if this is the first login in the test case, but
	 * 				true for subsequent logins
	 */
	public void goToTouchpoint(User testUser, boolean preserveInstance) {
		LoginEvents.goToTouchpoint(homeui, testUser, driver, TouchpointUIConstants.welcomeMessage, preserveInstance);
	}
	
	/**
	 * Ensure user is on welcome screen
	 */
	public void checkScreenAndBringUserToWelcomeScreen() {
		String screenID = driver.getSingleElement(TouchpointUIConstants.visibleScreen).getAttribute("id");
		if (screenID.contains("welcome")) {
			log.info("user is on Welcome screen " + screenID);
		} else if (screenID.contains("editProfile")) {
			log.info("user is on Update Your Profile screen " + screenID);
			returnToWelcomeScreenfromUpdateProfile();
		} else if (screenID.contains("profileTags")) {
			log.info("user is on Add Your Interests screen " + screenID);
			returnToWelcomeScreenfromAddYourInterests();
		} else if (screenID.contains("findColleagues")) {
			log.info("user is on  Follow Colleagues screen " + screenID);
			returnToWelcomeScreenfromFollowColleagues();
		} else if (screenID.contains("followCommunities")) {
			log.info("user is on Follow Community screen " + screenID);
			returnToWelcomeScreenfromFollowCommunity();
		}
	}

	/**
	 * Navigates to 'Update Your Profile' page
	 */
	public void goToUpdateYourProfile() {
		if (cfg.getTestConfig().serverIsMT()) {
			validateAndClickAcceptPolicy();
		}
		fluentWaitElementVisible(TouchpointUIConstants.buttonLetsGo);
		clickLink(TouchpointUIConstants.buttonLetsGo);
		fluentWaitElementVisible(TouchpointUIConstants.updateProfilePageHeader);
	}

	/**
	 * Navigates to 'Add Your Interests' page
	 */
	public void goToAddYourInterests() {
		goToUpdateYourProfile();
		clickLink(TouchpointUIConstants.nextButton);
		Assert.assertTrue(fluentWaitElementVisible(TouchpointUIConstants.addYourInterestPageHeader));
	}
	
	/**
	 * Navigates to 'Follow Colleagues' page
	 */
	public void goToFollowColleagues() {
		goToAddYourInterests();
		clickLink(TouchpointUIConstants.nextButton);
		fluentWaitElementVisible(TouchpointUIConstants.followColleaguesPageHeader);
	}

	/**
	 * Navigates to 'Follow Communities' page
	 */
	public void goToFollowCommunities() {
		goToAddYourInterests();
		clickLink(TouchpointUIConstants.nextButton);
		Assert.assertTrue(fluentWaitElementVisible(TouchpointUIConstants.followColleaguesPageHeader));
		clickLink(TouchpointUIConstants.nextButton);
		Assert.assertTrue(fluentWaitElementVisible(TouchpointUIConstants.followCommunityPageHeader));
	}

	/**
	 * Navigates back to 'Welcome' page from 'Update Your Profile'
	 */
	public void returnToWelcomeScreenfromUpdateProfile() {
		fluentWaitElementVisible(TouchpointUIConstants.backButton);
		clickLink(TouchpointUIConstants.backButton);
		fluentWaitElementVisible(TouchpointUIConstants.welcomeMessage);
	}

	/**
	 * Navigates back to 'Welcome' page from 'Add Your Interests'
	 */
	public void returnToWelcomeScreenfromAddYourInterests() {
		fluentWaitElementVisible(TouchpointUIConstants.backButton);
		clickLink(TouchpointUIConstants.backButton);
		fluentWaitElementVisible(TouchpointUIConstants.updateProfilePageHeader);
		returnToWelcomeScreenfromUpdateProfile();
	}

	public void returnToWelcomeScreenfromFollowColleagues() {
		clickLink(TouchpointUIConstants.backButton);
		fluentWaitElementVisible(TouchpointUIConstants.addYourInterestPageHeader);
		returnToWelcomeScreenfromAddYourInterests();
	}
	
	public void returnToWelcomeScreenfromFollowCommunity() {
		clickLink(TouchpointUIConstants.backButton);
		fluentWaitElementVisible(TouchpointUIConstants.followColleaguesPageHeader);
		returnToWelcomeScreenfromFollowColleagues();
	}

	/**
	 * Returns true if specified tag entry is present in 'My Interest' section
	 * @param tagSearchingFor
	 * @return true if tag is present
	 */
	public Boolean getTagsInMyInterest(String tagSearchingFor) {

		Boolean found = false;
		List<Element> tags = driver.getVisibleElements(TouchpointUIConstants.tagsInMyInterest);
		for (Element tag : tags) {
			String tagname = tag.getText();
			if (tagname.equals(tagSearchingFor)) {
				log.info("INFO: tag is " + tagname + " found");
				found = true;
				break;
			}
		}
		return found;
	}

	public void verifyBackgroundColor(Element ele, String colorCode) {
		RemoteWebElement tagRWE = (RemoteWebElement) ele.getBackingObject();
		log.info("color code is : " + tagRWE.getCssValue("background-color"));
		String backgroundColor = tagRWE.getCssValue("background-color");
		
		String hexCode = Color.fromString(backgroundColor).asHex();
		log.info("INFO: Verify the entry in the My Interests section have a dark blue background");
		Assert.assertEquals(hexCode, colorCode);
	}

	public void verifyBackgroundColorForTagEntries() {
		List<Element> tags = driver.getVisibleElements(TouchpointUIConstants.tagsInMyInterest);

		for (int i = 0; i < tags.size(); i++) {
			Element tag = tags.get(i);
			
			// #002847 is hex color code for dark blue color
			verifyBackgroundColor(tag, "#002847");
		}
	}

	public void verifyTextColor(Element ele, String colorCode) {

		RemoteWebElement tagNameRWE = (RemoteWebElement) ele.getBackingObject();
		String color = tagNameRWE.getCssValue("color");
		String hexCode = Color.fromString(color).asHex();

		log.info("INFO: Verify the text " + ele.getText() + " have white color");
		Assert.assertEquals(hexCode, colorCode);
	}

	public void verifyTextColorForTagEntries() {

		List<Element> tags = driver.getVisibleElements(TouchpointUIConstants.tagsInMyInterest);
		for (int i = 0; i < tags.size(); i++) {
			Element tag = tags.get(i);
			String title = tag.getAttribute("title");
			Element tagName = driver.getSingleElement(getTagEntryText(title));
			// #ffffff is hex color code for white color
			verifyTextColor(tagName,"#ffffff");
		}
	}

	public void verifyRemoveIconAndRemoveTagEntry(String tagEntryName) {

		List<Element> tags = driver.getVisibleElements(TouchpointUIConstants.tagsInMyInterest);
		for (int i = 0; i < tags.size(); i++) {

			Element tag = tags.get(i);
			String title = tag.getAttribute("title");

			// verify remove icon is associated with each tag entry
			log.info("INFO: Verify each tag entry in the My Interests section is associated with  remove icon i.e '-' minus icon ");
			Assert.assertTrue(isElementVisible(getTagEntryRemoveIcon(title)));

			// Remove specifically added tag entry by selecting remove icon
			if (title.equals(tagEntryName)) {
				removeEntryOfMyInterests(title);
			}
		}
	}

	public void removeEntryOfMyInterests(String interestTitle) {

		Element targetItem = driver.getSingleElement(getTagEntryRemoveIcon(interestTitle));
		log.info("INFO: Go to the My Interests section, and click on the minus icon");
		this.clickLink(getTagEntryRemoveIcon(interestTitle));
		log.info("INFO: Verify the suggestion is removed from the My Interests section");
		driver.turnOffImplicitWaits();
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);
		wait.until(ExpectedConditions.invisibilityOf(targetItem.getWebElement()));
		Assert.assertFalse(isElementPresent(getTagEntry(interestTitle)),"ERROR: Tag entry was not removed successfully");
		driver.turnOnImplicitWaits();
	}
	
	/**
	 * Returns welcome message removing extra spaces
	 */
	public String getWelcomeMessage() {
		String finalMessage = "";
		String actualWelcomeMsg = driver.getSingleElement(TouchpointUIConstants.welcomeMessage).getAttribute("innerText");
		String a[] = actualWelcomeMsg.split("\\s+");
		for (int i = 0; i < a.length; i++) {
			log.info("Word is " + a[i]);
			finalMessage = finalMessage + a[i] + " ";
		}

		return finalMessage;
	}
	
	public List<String> addMultipleInterests() {
		List<String> interests = new ArrayList<>();
		for (int i = 0; i < 3; i++) {

			String searchString = "BVT_Interests_" + Helper.genStrongRand();
			interests.add(searchString);

			// Enter tags to be created
			driver.getSingleElement(TouchpointUIConstants.searchBox).click();
			driver.getSingleElement(TouchpointUIConstants.searchBox).clear();
			typeText(TouchpointUIConstants.searchBox, searchString);

			// Create tag entries
			clickLink(TouchpointUIConstants.searchTypeaheadResult);
			fluentWaitPresent(TouchpointUI.getTagEntry(searchString));
		}
		return interests;
	}
	
	public Boolean getTagsInSuggestedInterest(String interestSearchingFor) {

		Boolean found = false;
		List<Element> intersts = driver.getVisibleElements("css=#content-profileTags #tags-from-colleagues li span");
		
		for (Element tag : intersts) {
			String tagname = tag.getText();
			if (tagname.equals(interestSearchingFor)) {
				log.info("INFO: tag is " + tagname + " found");
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Enter and Validate Job Responsibility input area
	 */
	public void validateJobTitle(String[] titleValidation) {
		Element jobResponsibility = driver.getSingleElement(TouchpointUIConstants.defaultJobtitle);
		Assert.assertTrue(isElementPresent(TouchpointUIConstants.editjobTitle));
		for (int i = 0; i < titleValidation.length; i++) {
			jobResponsibility.click();
			jobResponsibility.clear();
			typeText(TouchpointUIConstants.defaultJobtitle, titleValidation[i]);
			clickLink(TouchpointUIConstants.nextButton);
			fluentWaitElementVisible(TouchpointUIConstants.addYourInterestPageHeader);
			clickLink(TouchpointUIConstants.backButton);
			if (titleValidation[i].contains("more_than_50")) {
				Assert.assertEquals(jobResponsibility.getAttribute("value"),"Entering_characters_more_than_50_which_is_not_acce");
			} else {
				log.info("Actual Value: " + jobResponsibility.getAttribute("value"));
				Assert.assertEquals(jobResponsibility.getAttribute("value"), titleValidation[i]);
			}
		}
	}
	
	public String enterJobTitle(String jobResTitle ) {
		Element jobResponsibility = driver.getSingleElement(TouchpointUIConstants.defaultJobtitle);
		jobResponsibility.click();
		jobResponsibility.clear();
		typeText(TouchpointUIConstants.defaultJobtitle, jobResTitle);
		return jobResTitle;
	}
	public void validateCropDialogBoxHeader() {
		String actualHeader= "Crop your profile photo";
		Element corpHeader = driver.getSingleElement(TouchpointUIConstants.CropDialogBoxHeader);
		String expectedHeader= corpHeader.getText();
		Assert.assertEquals(actualHeader, expectedHeader);
	}
	/**
	 * This method validate the Work Number input field with set of special characters(.,(),-) and max number of 32 characters
	 */
	public void validateWorkPhoneNumber(String[] workPhoneNum) {
		
		for (int i = 0; i < workPhoneNum.length; i++) {
			Element worknum = driver.getSingleElement(TouchpointUIConstants.workPhoneNum);
			log.info("Clear existing work phone number ");
			worknum.click();
			worknum.clear();
			Assert.assertTrue(worknum.getAttribute("value").isEmpty());
			log.info("Enter new work phone number ");
			typeText(TouchpointUIConstants.workPhoneNum, workPhoneNum[i]);
			driver.getSingleElement("css=#profile-attributes").click();
			if(i==2)
			{
				Assert.assertEquals(worknum.getAttribute("value"), "12345678901234567890123456789012");
			}
			else {
			Assert.assertEquals(worknum.getAttribute("value"), workPhoneNum[i]);
			}
		}	
	}
	
	public String enterWorkPhoneNumber(String workNum1) {
		
     	Element worknum = driver.getSingleElement(TouchpointUIConstants.workPhoneNum);
		log.info("Clear existing work phone number ");
		worknum.clear();
		log.info("Enter new work phone number ");
		typeText(TouchpointUIConstants.workPhoneNum, workNum1);
		return workNum1;
	}
	/**
	 * This method validate the Mobile Number input field with set of special characters(.,(),-) and max number of 32 characters
	 */
	public void validateMobileNumber(String[] mobPhoneNum ) {
		
		Element mobileNum = driver.getSingleElement(TouchpointUIConstants.mobilePhoneNum);
		for (int i = 0; i < mobPhoneNum.length; i++) {
			log.info("Clear existing mobile phone number ");
			mobileNum.click();
			mobileNum.clear();
			Assert.assertTrue(mobileNum.getAttribute("value").isEmpty());
			log.info("Enter new mobile phone number ");
			typeText(TouchpointUIConstants.mobilePhoneNum, mobPhoneNum[i]);
			driver.getSingleElement("css=#profile-attributes").click();
			if(i==2)
			{
				Assert.assertEquals(mobileNum.getAttribute("value"), "12345678901234567890123456789012");
			}
			else {
			Assert.assertEquals(mobileNum.getAttribute("value"), mobPhoneNum[i]);
			}
		}
	}
	
	public String enterMobileNumber(String mobNum1) {
		
		Element worknum = driver.getSingleElement(TouchpointUIConstants.mobilePhoneNum);
		log.info("Clear existing mobile phone number ");
		worknum.clear();
		log.info("Enter new mobile phone number ");
		typeText(TouchpointUIConstants.mobilePhoneNum, mobNum1);
		return mobNum1;
	}

	/**
	 * Count the number of communities the user is following
	 */
	public void getCommunitiesCount() {
		String count = driver.getSingleElement(TouchpointUIConstants.communitiesFollowers).getText();
		if (count != null) {
			log.info("INFO:The number of Community followers is " + count);
		} else {
			log.info("INFO:The number of Community followers is " + count);
			Assert.assertEquals(count, "0");
		}
	}

	/**
	 * This method helps to follow multiple (3) cards
	 */
	public List<String> followCard(String viewToBeFollowedFrom) {
		List<String> followedCardsList = new ArrayList<>();
		int counter = 0;
		WebDriverWait wait = new WebDriverWait((WebDriver)driver.getBackingObject(), 3);		
		List<WebElement> cardsAvailableToFollow = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(viewToBeFollowedFrom + TouchpointUIConstants.getPeopleAvailableToFollow),2));

		for (WebElement card : cardsAvailableToFollow) {
			String cardToBeFollowed = card.getText();
			log.info("Card name is: " + cardToBeFollowed);
			followedCardsList.add(cardToBeFollowed);
			driver.executeScript("arguments[0].scrollIntoView(true);", driver.getSingleElement(viewToBeFollowedFrom + TouchpointUI.getFollowLink(cardToBeFollowed)).getWebElement());
			log.info("INFO: Follow the card " + cardToBeFollowed + " by selecting 'FOLLOW' button");
			driver.getFirstElement(viewToBeFollowedFrom + TouchpointUI.getFollowLink(cardToBeFollowed)).click();
			fluentWaitPresent(viewToBeFollowedFrom + TouchpointUI.getStopFollowingLink(cardToBeFollowed));
			counter++;
			if (counter == 3)
				break;
		}

		return followedCardsList;
	}
	/**
	 * Validate and select Accept Policy Check box on Welcome Screen
	 */
	public void validateAndClickAcceptPolicy() 
	{
		fluentWaitElementVisible(TouchpointUIConstants.networkExtral);
		Assert.assertTrue(isElementPresent(TouchpointUIConstants.acceptPolicyCheckBox));
		if (!driver.getSingleElement(TouchpointUIConstants.acceptPolicyCheckBox).isSelected()) {
			clickLink(TouchpointUIConstants.acceptPolicyCheckBox);

		}

	}
	
	public boolean isCommFoundInSearchResult(BaseCommunity baseComm) {
		boolean found = false;
		log.info("INFO: Search for a word that appears in Community's title ");
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).click();
		driver.getSingleElement(TouchpointUIConstants.searchForCommunities).clear();
		typeText(TouchpointUIConstants.searchForCommunities, baseComm.getName().substring(0, 13));
		driver.typeNative(Keys.ENTER);
		driver.turnOffImplicitWaits();
		try {
			WebDriverWait wait = new WebDriverWait((WebDriver) driver.getBackingObject(), 4);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(TouchpointUIConstants.searchedCommunityView)));
		} catch (TimeoutException e) {
			log.info("No search results returned for searched keyword");
		}

		List<Element> communitySearchResult = driver.getVisibleElements(TouchpointUIConstants.communityInSearchResults);
		driver.turnOnImplicitWaits();
		for (Element community : communitySearchResult) {
			String communityTitle = community.getText();
			log.info("Community title is: " + communityTitle);
			if (communityTitle.equalsIgnoreCase(baseComm.getName())) {
				found = true;
				break;
			}
		}
		return found;
	}
	/**
	 * Method to Configure Touchpoint via App Registry
	 * @param User
	 * @param String
	 * @param CustomizerUI
	 * @param TestConfigCustom
     * @param RCLocationExecutor	 
     */
	public static void touchPointConfig(User testUser, String appRegAppName, CustomizerUI uiCnx7,TestConfigCustom cfg,RCLocationExecutor driver)
	{
		TouchpointUI ui = TouchpointUI.getGui(cfg.getProductName(), driver);	
		// check TouchPoint config in Appreg and make configuration
		log.info("Info: Load Customizer and login: " +testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);
		ui.loadComponent(Data.getData().ComponentCustomizer,true);
		
		log.info("Info: Delete Touchpoint MT App, if already created");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		log.info("Info: Create Touchpoint MT App");
		try {
			uiCnx7.createAppViaAppReg( appRegAppName);
		} catch (IOException e) {
			log.info("IO Exception thrown during Touchpoint Configuration via App Reg: " + e.getMessage());
		}
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);
		ui.logout();
	}
	
	/**
	 * Method to Delete Touchpoint configuration via App Registry
	 * @param User
	 * @param String
	 * @param CustomizerUI
	 * @param TestConfigCustom
     * @param RCLocationExecutor	 
     */
	public static void touchPointUnConfig(User testUser, String appRegAppName, CustomizerUI uiCnx7,TestConfigCustom cfg,RCLocationExecutor driver)
	{
		TouchpointUI ui = TouchpointUI.getGui(cfg.getProductName(), driver);	
		// check TouchPoint config in Appreg and make configuration
		log.info("Info: Load Customizer and login: " +testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);
		ui.loadComponent(Data.getData().ComponentCustomizer,true);
		
		log.info("Info: Delete Touchpoint MT App, if already created");
		if(uiCnx7.isElementVisibleWd(By.xpath(uiCnx7.cardLayout.replace("PLACEHOLDER", appRegAppName)),3)) {			
			uiCnx7.deleteAppFromView(appRegAppName, appRegAppName);		
		}
		
		ui.loadComponent(Data.getData().ComponentHomepage,true);
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.megaMenuOptionCommunities);
		ui.logout();
	}
}
