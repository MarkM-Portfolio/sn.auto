package com.ibm.conn.auto.tests.touchpoint;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.TouchpointUI;

public class BVT_Level_2_Touchpoint_UpdateYourProfileScreen extends SetUpMethods2 {
	
	private static final Logger log = LoggerFactory.getLogger(BVT_Level_2_Touchpoint_UpdateYourProfileScreen.class);
	
	private TestConfigCustom cfg;	
	private TouchpointUI ui;
	private ProfilesUI pui;
	private HomepageUI hUI;
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
		pui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		// Initialize the configuration		
		ui = TouchpointUI.getGui(cfg.getProductName(), driver);
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(hUI.getCloseTourScript());
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify 'Update Your Profile' screen for existing user</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen.</li>
	 * <li><B>Step: </B>Go To 'Update Your Profile' page</li>
	 * <li><B>Verify: </B>Verify user navigates to 'Update Your Profile' page</li>
	 * <li><B>Verify: </B>Verify primary header 'Update your Profile'</li>
	 * <li><B>Verify: </B>Verify secondary page header 'More people will connect with you if they can see who you are.'</li>
	 * <li><B>Step: </B>Hover over photo/default person icon</li>
	 * <li><B>Step: </B>Click on Profile Picture</li>
	 * <li><B>Step: </B>Click on photo to upload</li>
	 * <li><B>Verify: </B>Verify 'Crop your profile photo' dialog box is displayed </li>
	 * <li><B>Verify: </B>Verify Crop functionality is working on Profile Picture section</li>
	 * <li><B>Verify: </B>Click on Save button</li>
	 * <li><B>Verify: </B>Verify User name displayed</li>
	 * <li><B>Verify: </B>Enter and Validate JobResponsibility input field with set of special characters(!@#$%&* ( ) - _ = + :" ;' < > ? / , .) and max number of 50 characters</li>
	 * <li><B>Verify: </B>Enter and Validate Work Phone Number with set of special characters(.,(),-) and max number of 32 characters</li>
	 * <li><B>Verify: </B>Enter and Validate Mobile Phone Number with set of special characters(.,(),-) and max number of 32 characters</li>
	 * <li><B>Step: </B>Select Next button</li>
	 * <li><B>Step: </B>Select Back button</li>
	 * <li><B>Verify: </B>Verify Job Responsibility, Work and Mobile Phone Number are still saved even after returning back to 'Update your Profile' from subsequent page</li>
	 * </ul>
	 * @throws Exception 
	 */
	@Test(groups = { "regression" })
	public void updateYourProfile() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.build();
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);
		ui.checkScreenAndBringUserToWelcomeScreen();
		
		log.info("INFO: Verify user navigates to 'Update Your Profile' page");
		logger.strongStep("Verify user navigates to 'Update Your Profile' page");
		ui.goToUpdateYourProfile();

		log.info("INFO: Verify primary header 'Update your Profile' ");
		logger.strongStep(" Verify primary header 'Update your Profile'");
		Assert.assertTrue(ui.isElementPresent(TouchpointUIConstants.updateProfilePageHeader));
		
		log.info("INFO:Verify More people will connect with you if they can see who you are.");
		logger.strongStep("Verify More people will connect with you if they can see who you are.");
		Assert.assertTrue(ui.isElementPresent(TouchpointUIConstants.UpdateProfilePageSecondHeader));

		log.info("INFO:Hover over photo/default person icon");
		logger.strongStep("Hover over photo/default person icon");
		Element ele = driver.getSingleElement(TouchpointUIConstants.ProfilePicture);
		ele.hover();

		// Set Profile Picture pui.fluentWaitPresent(TouchpointUI.ProfilePicture);
		log.info("Click on Profile Picture");
		logger.strongStep("Click on Profile Picture");
		pui.clickLinkWait(TouchpointUIConstants.ProfileDefaultPicture);

		logger.strongStep("Click on photo to upload");
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		driver.getSingleElement(TouchpointUIConstants.ProfilePicture).typeFilePath(FilesUI.getFileUploadPath(file.getName(), cfg));

		log.info("Verify 'Crop your profile photo' dialog box is displayed");
		logger.strongStep("Verify 'Crop your profile photo' dialog box is displayed");
		ui.validateCropDialogBoxHeader();

		log.info("Verify Crop functionality is working on Profile Picture section");
		logger.strongStep("Verify Crop functionality is working on Profile Picture section");
		String newStyle = "width: 118px";
		WebElement cropElement = (WebElement) ui.getFirstVisibleElement(TouchpointUIConstants.CropElement).getBackingObject();
		driver.executeScript("arguments[0].setAttribute('style', '" + newStyle + "')", cropElement);

		logger.strongStep("Click on Save button");
		log.info("INFO: Click on Save button");
		ui.clickLinkWait(TouchpointUIConstants.ProfilePicSaveBtn);

		log.info("INFO: Verify User name");
		String expextedUserName = testUser.getDisplayName();
		String actualUserName = driver.getSingleElement(TouchpointUIConstants.displayedUserName).getAttribute("value");
		log.info("Value is: " + actualUserName);
		Assert.assertEquals(actualUserName, expextedUserName);

		logger.strongStep("Enter and Validate JobResponsibility input field with set of special characters(!@#$%&* ( ) - _ = + : ;' < > ? / , .) and max number of 50 characters");
		log.info("INFO: Verify JobResponsibility input field");
		ui.validateJobTitle(new String[] { Data.getData().All_SpecialChars,"Entering_characters_more_than_50_which_is_not_acceptable", "T", "0123456789","abcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZ" });
		String jobResponsibility = ui.enterJobTitle("Test Lead");

		logger.strongStep("Enter and Validate Work Phone Number with set of special characters(.,(),-) and max number of 32 characters");
		log.info("INFO: Verify Work Phone Number");
		ui.validateWorkPhoneNumber(new String[] { "+91-(89)41254121.412542222222222", "+020-78965412365","12345678901234567890123456789012345" });
		String workPhnNum = ui.enterWorkPhoneNumber("+020-78965412365");
		
		logger.strongStep("Enter and Validate Mobile Phone Number with set of special characters(.,(),-) and max number of 32 characters");
		log.info("INFO: Verify Mobile Phone Number");
		ui.validateMobileNumber(new String[] { "+91-(00)74125355555555555211211", "+91-1234567852" , "12345678901234567890123456789012345"});
		String mobilePhnNum= ui.enterMobileNumber("+91-1234567852");
		
		logger.strongStep("Select 'Next' button");
		ui.clickLink(TouchpointUIConstants.nextButton);
		
		logger.strongStep("Select 'Back' button");
		ui.clickLink(TouchpointUIConstants.backButton);
		
		logger.strongStep("Verify Job Responsibility, Work and Mobile Phone Number are still saved even after returning back to 'Update your Profile' from subsequent page");
		log.info("INFO: Verify Job Responsibility, Work and Mobile Phone Number are still saved even after returning back to 'Update your Profile' from subsequent page");
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.defaultJobtitle).getAttribute("value"),jobResponsibility);
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.workPhoneNum).getAttribute("value"), workPhnNum);
		Assert.assertEquals(driver.getSingleElement(TouchpointUIConstants.mobilePhoneNum).getAttribute("value"), mobilePhnNum);
		
		//Return to welcome screen
		ui.returnToWelcomeScreenfromUpdateProfile();
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info: </B>Verify that profile details like job title, mobile number, work number and tags added through Touchpoint screens should appears on my profile page correctly</li>
	 * <li><B>Step: </B>Log in to Touchpoint, with existing user and make sure that user is on welcome screen</li>
	 * <li><B>Step: </B> Go to 'Update Your Profile' page</li>
	 * <li><B>Step: </B>Enter job title, work phone number, mobile number and save it</li>
	 * <li><B>Step: </B>Go to next page 'Add Your Interests'</li> 
	 * <li><B>Step: </B>Create couple of new interests</li>
	 * <li><B>Step: </B>Complete the onboarding process just navigating through remaining pages</li> 
	 * <li><B>Step: </B>Go to My Profile</li>
	 * <li><B>Verify: </B>Verify the Job Responsibility you entered now appears below your display name</li>
	 * <li><B>Verify: </B>Click on the Contact Information tab, and verify the Phone Number you entered now appears in the "Office number" field</li>
	 * <li><B>Verify: </B>Verify the Mobile Number you entered now appears in the "Mobile number" field.</li>
	 * <li><B>Verify: </B>Locate the "Tags" widget (appears on the left side of  your profile) and verify the interests you selected on the "Add your Interests" on-boarding page now appear in the Tags widget</li>
	 * </ul>
	 */
	@Test(groups = { "level2" })
	public void updateProfileAndAddInterests_E2E() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();

		// Load component and login
		logger.strongStep("Open Touchpoint and login: " + testUser.getDisplayName());
		ui.goToTouchpoint(testUser, false);

		// Ensure that existing user is on Welcome screen
		ui.checkScreenAndBringUserToWelcomeScreen();

		logger.strongStep("Go to 'Update Your Profile' page");
		if (cfg.getTestConfig().serverIsMT()){
		ui.validateAndClickAcceptPolicy();
		}
		ui.clickLink(TouchpointUIConstants.buttonLetsGo);
		ui.fluentWaitPresent(TouchpointUIConstants.updateProfilePageHeader);

		// Add job title, mobile number and work phone number
		logger.strongStep("Enter job title, work phone number, mobile number and save it");
		String jobTitle = "Test Lead_" + Helper.genStrongRand();
		String workPhnNum = "+020 22" + Helper.genDateBasedRandVal();
		String mobNum = "+91 98" + Helper.genDateBasedRandVal();
		ui.enterJobTitle(jobTitle);
		ui.enterWorkPhoneNumber(workPhnNum);
		ui.enterMobileNumber(mobNum);

		logger.strongStep("Go to 'Add Your Interests' page");
		ui.clickLink(TouchpointUIConstants.nextButton);
		ui.fluentWaitPresent(TouchpointUIConstants.addYourInterestPageHeader);

		// Add multiple interests
		logger.strongStep("Create couple of new interests");
		List<String> tagsAddedFromTouchpoint = ui.addMultipleInterests();
		List<String> tagsAddedFromTouchpointLowerCase = new ArrayList<>();
		for (String tag : tagsAddedFromTouchpoint) {
			log.info("Tag value is: " + tag);
			tagsAddedFromTouchpointLowerCase.add(tag.toLowerCase());
		}

		logger.strongStep("Complete the onboarding process just navigating through remaining pages");
		ui.clickLink(TouchpointUIConstants.nextButton);
		ui.fluentWaitPresent(TouchpointUIConstants.followColleaguesPageHeader);
		ui.clickLink(TouchpointUIConstants.nextButton);
		ui.fluentWaitPresent(TouchpointUIConstants.followCommunityPageHeader);
		ui.clickLink(TouchpointUIConstants.doneButton);
		
		// Switch to the classic homepage
		logger.strongStep("Switch to the classic homepage");
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
	
		// Go to profiles
		logger.strongStep("Go to My Profile");
		ui.fluentWaitPresent(HomepageUIConstants.HomepageImFollowing);
		hUI.gotoProfile();

		logger.strongStep("Verify the Job Responsibility you entered now appears below your display name");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.jobTitle).getText().equals(jobTitle));

		logger.strongStep("Click on the Contact Information tab, and verify the Phone Number you entered now appears in the 'Office number' field");
		ui.clickLink(ProfilesUIConstants.ContactInfoTab);
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.officeNumber).getText().contains(workPhnNum));

		logger.strongStep("Verify the Mobile Number you entered now appears in the 'Mobile number' field");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.mobileNumber).getText().contains(mobNum));

		List<Element> tags = driver.getVisibleElements(ProfilesUIConstants.getTagsFromProfile);
		List<String> tagsFromMyProfile = new ArrayList<>();
		for (Element tag : tags) {
			log.info("Tag value  is: " + tag.getText());
			tagsFromMyProfile.add(tag.getText());
		}
		
		logger.strongStep("Locate the 'Tags' widget (appears on the left side of  your profile) and verify the interests you selected on the 'Add your Interests' on-boarding page now appear in the Tags widget");
		Assert.assertTrue(tagsFromMyProfile.containsAll(tagsAddedFromTouchpointLowerCase));

		// Clean up step to remove tags that's been added
		ui.loadComponent(Data.getData().ComponentTouchpoint, true);
		ui.goToAddYourInterests();
		for (String tagToRemove : tagsAddedFromTouchpointLowerCase) {
			log.info("Tag value is: " + tagToRemove);
			ui.removeEntryOfMyInterests(tagToRemove);
		}
		// Return to welcome screen
		ui.returnToWelcomeScreenfromAddYourInterests();
		ui.endTest();
	}

}
