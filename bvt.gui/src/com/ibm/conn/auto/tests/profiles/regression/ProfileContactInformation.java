package com.ibm.conn.auto.tests.profiles.regression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.appobjects.base.BaseProfile.otherDropDown;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;

public class ProfileContactInformation extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(ProfileContactInformation.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;
	private HomepageUI hUI;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
	}
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Background_AboutMe</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 12: My Profile Page - Background - About Me </li>
	*<li><B>Info:</B> The My Profile page's Background - About Me data</li>
	*<li><B>Step:</B> Open the My Profile page</li>
	*<li><B>Step:</B> Click Edit my Profile button </li>
	*<li><B>Step:</B> Select the "About Me" tab</li>
	*<li><B>Step:</B> Add 2048 characters to the Background and About Me fields, click Save button</li>
	*<li><B>Step:</B> Click the Background tab</li>
	*<li><B>Verify:</B> The data in the About Me and Background matches</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8D9378C7C72DC38785257E120066B8AA">SC - IC Profiles Regression 12: My Profile Page - Background - About Me</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void backgroundAboutMe() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Edit profile, Add 2048 characters to the Background and About Me fields, click Save button
		log.info("INFO: Edit profile, Add 2048 characters to the Background and About Me fields, click Save button");
		ui.updateProfileBackground(Data.getData().Chars1000+Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 47));
		
		//Click the Background tab
		log.info("INFO: Click the Background tab");
		ui.clickLinkWait(ProfilesUIConstants.BackgroundTab);
		
		//Verify the data in the About Me and Background matches
		log.info("INFO: Verify the data in the About Me matches");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.BackgroundInfoContent).getText().contains("About me:\n" + Data.getData().Chars1000+Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 47)),
				"ERROR: Data in the About Me does not matche");
		
		log.info("INFO: Verify the data in the Background matches");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.BackgroundInfoContent).getText().contains("Background:\n" + Data.getData().Chars1000+Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 47)),
				"ERROR: Data in the Background does not matche");
		
		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_MyProfilePage_Photo</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 17: My Profile Page - Photo </li>
	*<li><B>Info:</B> The My Profile page's Photo functionality</li>
	*<li><B>Step:</B> Open the My Profile page and mouse over the user's photo.</li>
	*<li><B>Verify:</B> The pencil edit icon is displayed and the pop up help text "Edit profile photo" is displayed. </li>
	*<li><B>Step:</B> Click the pencil edit photo action</li>
	*<li><B>Verify:</B> The Photo tab is opened in edit mode</li>
	*<li><B>Verify:</B> The message "Update information that you want to change" is displayed.</li>
	*<li><B>Verify:</B> The Choose a File, Remove Image and Cancel buttons are enabled.</li>
	*<li><B>Verify:</B> The Save and Save & Close buttons are disabled and the static help text between Choose a File and Current image</li>
	*<li><B>Step:</B> Click the Choose a File button</li>
	*<li><B>Verify:</B> The OS File Open dialog is presented, Select a file, Click Save & Close.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/F59CDFE927AC88A685257E18005C9EE5">SC - IC Profiles Regression 17: My Profile Page - Photo</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void editPhoto() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();

		//File to upload
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file6)
											 .extension(".jpg")
											 .build();
				
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Hover over the user's photo
		log.info("INFO: Hover over the user's photo");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		driver.executeScript(ProfilesUIConstants.mouseOverScript, (WebElement) driver.getSingleElement(ProfilesUIConstants.EditImageIcon).getBackingObject());
		
		//The pencil edit icon is displayed
		log.info("INFO: The pencil edit icon is displayed");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.PhotoPencilIcon).isDisplayed(),
				"ERROR: The pencil edit icon is not displayed");
		
		//The pop up help text "Edit profile photo" is displayed
		log.info("INFO: The pop up help text 'Edit profile photo' is displayed");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.ProfilePhoto).getAttribute("alt").contains(Data.getData().profilePhotoHoverText),
				"ERROR: The pop up help text 'Edit profile photo' is not displayed");
		
		//Click the pencil edit photo action
		log.info("INFO: Click the pencil edit photo action");
		ui.clickLink(ProfilesUIConstants.PhotoPencilIcon);
		
		//The Photo tab is opened in edit mode
		log.info("INFO: The Photo tab is opened in edit mode");
		//ui.clickLinkWait(ProfilesUI.EditPhotoTab);
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.EditPhotoTab).isEnabled(),
				"ERROR: The Photo tab is not opened in edit mode");
		
		//The message "Update information that you want to change" is displayed
		log.info("INFO: The message 'Update information that you want to change' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().editProfileInfo),
				"ERROR: The message 'Update information that you want to change' is not displayed");
		
		//The Choose a File, Remove Image and Cancel buttons are enabled
		log.info("INFO: The Choose a File, Remove Image and Cancel buttons are enabled");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.ChooseAFileButton).isEnabled(),
				"ERROR: The Choose a File button is disabled");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.RemoveImage).isEnabled(),
				"ERROR: The Remove Image button is disabled");
		Assert.assertTrue(ui.getFirstVisibleElement(BaseUIConstants.CancelButton).isEnabled(),
				"ERROR: The Cancel button is disabled");
		
		//The Save and Save & Close buttons are disabled
		log.info("INFO: The Save and Save & Close buttons are disabled");
		Assert.assertFalse(ui.getFirstVisibleElement(BaseUIConstants.SaveButton).isEnabled(),
				"ERROR: The Save button is disabled");
		Assert.assertFalse(ui.getFirstVisibleElement(ProfilesUIConstants.SaveAndCloseBtn).isEnabled(),
				"ERROR: The Save & Close button is disabled");
		
		//The static help text is present
		log.info("INFO: Verify the static help text is displayed");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.PhotoUploadDesc).getText().contentEquals(Data.getData().uploadPhotoDesc),
				"ERROR: Static help text is not appropriate"); 
		
		//Upload photo
		log.info("INFO: To Upload photo");
		ui.addProfilePhoto(baseFileImage);
		
		//Click Save & Close
		log.info("INFO: Click Save & Close");
		ui.clickLink(ProfilesUIConstants.SaveAndCloseBtn);
	
		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_Contact Information</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 11: My Profile Page - Contact Information (1 of 2)</li>
	*<li><B>Info:</B> The My Profile page's Contact Information data </li>
	*<li><B>Step:</B> Open the My Profile page -> Click Edit my Profile button</li>
	*<li><B>Verify:</B> The message "Update information that want to change in your profile." is displayed. </li>
	*<li><B>Step:</B> Add 1024 characters in the Address field, click Save button.</li>
	*<li><B>Verify:</B> The message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Add 1024 characters to the first Other Information field, click Save button.</li>
	*<li><B>Verify:</B> The message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Try again with 1024 characters in the second Other Information, click Save button.</li>
	*<li><B>Verify:</B> The message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Try again with 1024 characters in the third Other Information, click Save button.</li>
	*<li><B>Verify:</B> The message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Add 33 characters to the Office Number field, click Save button.</li>
	*<li><B>Verify:</B> the Error message "Office Number: cannot be greater than 32 characters."</li>  
    *<li><B>Step:</B> Try again with 32 characters in the Office Number, click Save button.</li>
    *<li><B>Verify:</B> the message "Profile data updated successfully." is displayed</li>
    *<li><B>Step:</B> Add 33 characters to the Mobile Number field, click Save button.</li>
    *<li><B>Verify:</B> the Error message "Mobile Number: cannot be greater than 32 characters."</li>  
    *<li><B>Step:</B> Try again with 32 characters in the Mobile Number, click Save button.</li>
    *<li><B>Verify:</B> the message "Profile data updated successfully." is displayed</li>
    *<li><B>Step:</B> Add 1025 characters to the Other fields, click Save button.</li>
    *<li><B>Verify:</B> the Error message "Other: cannot be greater than 1024 characters."</li>  
    *<li><B>Step:</B> Try again with 1024 characters in the Other, click Save button.</li>
    *<li><B>Verify:</B> the message "Profile data updated successfully." is displayed</li>
    *<li><B>Step:</B> Add 129 characters to the Job Title field, click Save button.</li>
	*<li><B>Verify:</B> the Error message "Job Title: cannot be greater than 128 characters.</li>
	*<li><B>Step:</B> Try again with 128 characters in the Job Title, click Save button.</li>
	*<li><B>Verify:</B> the message "Profile data updated successfully." is displayed</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/2E7E13B125F8E10085257E0C004E457E">SC - IC Profiles Regression 11: My Profile Page - Contact Information</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc"})
	public void editContactInformation() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
				
		//Edit My Profile
		log.info("INFO: Edit My Profile");
		ui.clickLinkWait(ProfilesUIConstants.MyICProfileCloud);
		
		//The message "Update information that you want to change" is displayed
		log.info("INFO: The message 'Update information that you want to change' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().editProfileInfo),
				"ERROR: The message 'Update information that you want to change' is not displayed");
		
		//Add 1024 characters in the Address field, click Save button
		log.info("INFO: Add 1024 characters in the Address field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserAddress, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- Address field is not successfully updated");
		
		//Add 1024 characters in the first Other Information, click Save button.
		log.info("INFO: Add 1024 characters in the first Other field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUICloud.EditFirstOtherInformationField, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- first Other field is not successfully updated");
		
		//Add 1024 characters in the second Other Information, click Save button.
		log.info("INFO: Add 1024 characters in the second Other field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUICloud.EditSecondOtherInformationField, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- second Other field is not successfully updated");
		
		//Add 1024 characters in the third Other Information, click Save button.
		log.info("INFO: Add 1024 characters in the third Other field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUICloud.EditThirdOtherInformationField, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- third Other field is not successfully updated"); 
		
		//Add 33 characters in the Office Number field, click Save button.
		log.info("INFO: Add 33 characters in the Office Number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserTele, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Office Number:").replaceAll("COUNT", "32")),
				"ERROR:- Office Number does not prompt error msg");
		
		//Add 32 characters in the Office Number field, click Save button.
		log.info("INFO: Add 32 characters in the Office Number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserTele, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- Office Number field is not successfully updated");
		
		//Add 33 characters in the Mobile number field, click Save button.
		log.info("INFO: Add 33 characters in the Mobile number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserMobile, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Mobile number:").replaceAll("COUNT", "32")),
				"ERROR:- Mobile number does not prompt error msg");
		
		//Add 32 characters in the Mobile number field, click Save button.
		log.info("INFO: Add 32 characters in the Mobile number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserMobile, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- Mobile number field is not successfully updated"); 
		
		//Add 1024 characters in the first Other Information, click Save button.
		log.info("INFO: Add 1024 characters in the first Other field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUICloud.FirstOtherTextField, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- first Other field is not successfully updated");
		
		//Add 1024 characters in the second Other Information, click Save button.
		log.info("INFO: Add 1024 characters in the second Other field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUICloud.SecondOtherTextField, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- second Other field is not successfully updated");
		
		//Add 1024 characters in the third Other Information, click Save button.
		log.info("INFO: Add 1024 characters in the third Other field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUICloud.ThirdOtherTextField, Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24), Data.getData().updateSuccessMsg),
				"ERROR:- third Other field is not successfully updated");
		
		//Add 129 characters to the Job Title field, click Save button.
		log.info("INFO: Add 129 characters to the Job Title field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileJobTitle, Data.getData().Chars1000.substring(0, 129), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Job Title").replaceAll("COUNT", "128")),
				"ERROR:- Job Title field does not prompt error msg"); 
		
		//Add 128 characters to the Job Title field, click Save button.
		log.info("INFO: Add 128 characters to the Job Title field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileJobTitle, Data.getData().Chars1000.substring(0, 128), Data.getData().updateSuccessMsg),
				"ERROR:- Job Title field is not successfully updated");
		
		//End test
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Profile_Contact Information</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 11: My Profile Page - Contact Information (2 of 2)</li>
	*<li><B>Info:</B> The My Profile page's Contact Information verify data in read mode </li>
	*<li><B>Step:</B> Open the My Profile page -> Select the Contact Information tab</li>
	*<li><B>Verify:</B> The "Contact Information" tab label, the default Contact info contains Name:  <user name> and Office:  <user email> </li>
	*<li><B>Step:</B> Click Edit my Profile button , update information and Save and Close</li>
	*<li><B>Verify:</B> The Profile information form and verify data in read mode.</li>
	*<li><B>Verify:</B> Open Profile information tab and verify data in read mode.Verify all data that was entered in previous tests</li>
	*<li><B>Verify:</B> The Send Email button is displayed and active.</li>
	*<li><B>Step:</B> Click the Send Email button</li>
	*<li><B>Verify:</B> The default email program is invoked.</li>
	*<li><B>Step:</B> Click the user's email address under the user's name</li>
	*<li><B>Verify:</B> The default email program is invoked.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/2E7E13B125F8E10085257E0C004E457E ">SC - IC Profiles Regression 11: My Profile Page - Contact Information</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc"})
	public void verifyContactInfoDetails() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		BaseProfile profile = new BaseProfile.Builder(testUser1.getDisplayName())
											 .build();
		
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Select the Contact Information tab
		log.info("INFO: Select the Contact Information tab");
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		ui.gotoContactInformation();
		
		//The "Contact Information" tab label
		log.info("INFO: Verify the 'Contact Information' tab label");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ContactInfoTab),
				"ERROR: 'Contact Information' tab label is not present");
		
		//The Default Contact info contains User Name and Email.
		log.info("INFO: Verify Contact Information contains User Name");
		String contactTabDetails = driver.getFirstElement(ProfilesUIConstants.ContactInfoDetails).getText(); //Contains Contact Information Details
		Assert.assertTrue(contactTabDetails.contains("Name:\n"+ testUser1.getDisplayName()),
				"ERROR: Contact Information is missing User Name");
		
		log.info("INFO: Verify Contact Information contains User Email");
		Assert.assertTrue(contactTabDetails.contains("Office email:\n"+ testUser1.getEmail()),
				"ERROR: Contact Information is missing User Email");
		
		//Edit My profile
		log.info("INFO: To Edit My Profile");
		profile.setAddress(Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24));
		profile.setFirstOtherInfo(Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24));
		profile.setSecondOtherInfo(Data.getData().Chars1001+Data.getData().Chars1000.substring(0, 23));
		profile.setThirdOtherInfo(Data.getData().Chars1001+Data.getData().Chars1001.substring(0, 23));
		profile.setOfficeNum(Data.getData().Chars1000.substring(0, 32));
		profile.setMobileNum(Data.getData().Chars1000.substring(0, 32));
		profile.setFirstOther(otherDropDown.OTHER.setMenuItemText(Data.getData().Chars1000+Data.getData().Chars1000.substring(0, 24)));
		profile.setSecondOther(otherDropDown.CELL.setMenuItemText(Data.getData().Chars1000.substring(0, 32)));
		profile.setThirdOther(otherDropDown.FAX.setMenuItemText(Data.getData().Chars1000.substring(0, 32)));
		profile.setJobTitle(Data.getData().Chars1000.substring(0, 128));
		profile.edit(ui);

		//verify data in BusinessCard Details
		log.info("INFO: Verify data in BusinessCard Details");
		String userName = driver.getFirstElement(ProfilesUIConstants.BusinessCardContent).getText(); //Contains Profile UserName
		String businessCardDetails = driver.getFirstElement(ProfilesUIConstants.BusinessCardDetails).getText(); //Contains BusinessCard Details
		
		//User Name is displayed
		log.info("INFO: Verify Profile User Name in BusinessCard Content");
		Assert.assertTrue(userName.contains(testUser1.getDisplayName()),
				"ERROR: Profile User Name is not present in BusinessCard Content");
		
		//JobTitle is displayed
		log.info("INFO: Verify updated Profile JobTitle in BusinessCard Content");
		Assert.assertTrue(businessCardDetails.contains(profile.getJobTitle()+"\n"),
				"ERROR: Updated Profile JobTitle is not present in BusinessCard Content");
		
		//Office Number is displayed
		log.info("INFO: Verify updated Profile Office Number in BusinessCard Content");
		Assert.assertTrue(businessCardDetails.contains(profile.getOfficeNum()+"\n"),
				"ERROR: Updated Profile Office Number is not present in BusinessCard Content");
		
		//Office Email is displayed
		log.info("INFO: Verify Profile Office Email in BusinessCard Content");
		Assert.assertTrue(businessCardDetails.contains(testUser1.getEmail()),
				"ERROR: Profile Office Email is not present in BusinessCard Content"); 
				
		//Local time is displayed
		log.info("INFO: Verify Profile Local time in BusinessCard Content");
		Pattern localTime = Pattern.compile(Data.getData().localTimePattern);
		Matcher matcher = localTime.matcher(businessCardDetails);
		Assert.assertTrue(matcher.find(),
				"ERROR: Profile Local time is not present in BusinessCard Content");
		
		//Select the Contact Information tab
		log.info("INFO: Select the Contact Information tab");
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		ui.gotoContactInformation();
				
		//verify updated data in Contact Information Details
		log.info("INFO: Verify updated data in Contact Information Details");
		String contactInfo = driver.getFirstElement(ProfilesUIConstants.ContactInfoDetails).getText(); //Contains Contact Information details
		
		//Updated Address is displayed
		log.info("INFO: Verify Updated Address is displayed");
		Assert.assertTrue(contactInfo.contains("Address:\n"+ profile.getAddress()),
				"ERROR: Updated Address is not displayed");
		
		//Updated Other Information1 is displayed
		log.info("INFO: Verify Updated Other Information1 is displayed");
		Assert.assertTrue(contactInfo.contains("Other information:\n"+ profile.getFirstOtherInfo()),
				"ERROR: Updated Other Information1 is not displayed");
		
		//Updated Other Information2 is displayed
		log.info("INFO: Verify Updated Other Information2 is displayed");
		Assert.assertTrue(contactInfo.contains("Other information:\n"+ profile.getSecondOtherInfo()),
				"ERROR: Updated Other Information2 is not displayed");
				
		//Updated Other Information3 is displayed
		log.info("INFO: Verify Updated Other Information3 is displayed");
		Assert.assertTrue(contactInfo.contains("Other information:\n"+ profile.getThirdOtherInfo()),
				"ERROR: Updated Other Information3 is not displayed");
		
		//Updated Office number is displayed
		log.info("INFO: Verify Updated Office number is displayed");
		Assert.assertTrue(contactInfo.contains("Office number:\n"+ profile.getOfficeNum()),
				"ERROR: Updated Office number is not displayed");
				
		//Updated Mobile number is displayed
		log.info("INFO: Verify Updated Mobile number is displayed");
		Assert.assertTrue(contactInfo.contains("Mobile number:\n"+ profile.getMobileNum()),
				"ERROR: Updated Mobile number is not displayed");

		//Updated Other details are displayed
		log.info("INFO: Verify Other details are displayed");
		Assert.assertTrue(contactInfo.contains("Other:\n"+ profile.getFirstOther().getMenuItemText()) ,
				"ERROR: Updated Other details are not displayed");
		
		//Updated Cell details are displayed
		log.info("INFO: Verify Cell details are displayed");
		Assert.assertTrue(contactInfo.contains("Cell:\n"+ profile.getSecondOther().getMenuItemText()),
				"ERROR: Updated Cell details is not displayed");
		
		//Updated Fax details are displayed
		log.info("INFO: Verify Fax details are displayed");
		Assert.assertTrue(contactInfo.contains("Fax:\n"+ profile.getThirdOther().getMenuItemText()),
				"ERROR: Updated Fax details is not displayed");
						
		//Updated JobTitle is displayed
		log.info("INFO: Verify updated JobTitle is displayed");
		Assert.assertTrue(contactInfo.contains("Job title:\n"+ profile.getJobTitle()),
				"ERROR: Updated JobTitle is not displayed");
		
		//The Send Email button is displayed and active
		log.info("INFO: Verify Send Email button is displayed and active");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.SendEmailButton).isDisplayed() && driver.getSingleElement(ProfilesUIConstants.SendEmailButton).isEnabled(),
				"ERROR: Send Email button is not displayed and active");
		
		//TODO: Click on Send Email/Email address it should open default email program. Verify default email program, cannot be automated(It is windows based email program).
		
		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_Contact Information</B></li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 11: My Profile Page - Contact Information (1 of 2)</li>
	*<li><B>Info:</B> The My Profile page's Contact Information data </li>
	*<li><B>Step:</B> Open the My Profile page -> Click Edit my Profile button</li>
	*<li><B>Verify:</B> The message "Update information that want to change in your profile." is displayed. </li>
	*<li><B>Step:</B> Add 65 characters to the Building field</li>
	*<li><B>Verify:</B> The message "Profile changed. Click the save button to save your changes" and Click Save button. Verify The Error message "Building ID: Cannot be greater than 64 characters"</li>
	*<li><B>Step:</B> Try again with 64 characters in the Building field, click Save button. Verify The message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Add 17 characters to the FLOOR field, click Save button. Verify The message: "Floor: cannot be greater than 16 characters"</li>
	*<li><B>Step:</B> Try again with 16 characters in the FLOOR click Save button. Verify The message "Profile data updated successfully." is displayed</li>
    *<li><B>Step:</B> Add 33 characters to the OFFICE field, click Save button. Verify The message: "Office Name: cannot be greater than 32 characters"</li>
    *<li><B>Step:</B> Try again with 32 characters in the OFFICE, click Save button. Verify The message "Profile data updated successfully." is displayed</li> 
    *<li><B>Step:</B> Add 33 characters to the OFFICE NUMBER field, click Save button. Verify The message: "Office Number: cannot be greater than 32 characters"</li>
    *<li><B>Step:</B> Try again with 32 characters in the OFFICE NUMBER, click Save button. Verify The message "Profile data updated successfully." is displayed</li>
    *<li><B>Step:</B> Add 33 characters to the IP TELEPHONY NUMBER field, click Save button. the Error message "IP TELEPHONY NUMBER: cannot be greater than 32 characters."</li>
    *<li><B>Verify:</B> Try again with 32 characters in the IP TELEPHONY NUMBER, click Save button. Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Verify:</B> Add 33 characters to the Mobile Number field, click Save button. Verify the Error message "Mobile Number: cannot be greater than 32 characters."</li>
	*<li><B>Verify:</B> Try again with 32 characters in the Mobile Number, click Save button. Verify The message "Profile data updated successfully." is displayed</li>
	*<li><B>Verify:</B>  Add 33 characters to the Pager Number fields, click Save button. Verify the Error message "Pager Number: cannot be greater than 32 characters."</li>
	*<li><B>Verify:</B>  Try again with 32 characters in the Pager Number, click Save button. Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Verify:</B>  Add 33 characters to the Fax Number fields, click Save button. Verify the Error message "Fax Number: cannot be greater than 32 characters."</li>
	*<li><B>Verify:</B> Try again with 32 characters in the Fax Number, click Save button. Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Verify :</B>  Add 129 characters to the Alternate Email fields, click Save button. Verify the Error message "Alternate Email: cannot be greater than 128 characters." </li>
	*<li><B>Verify:</B> Try again with 128 characters in the Alternate Email, click Save button. Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Verify:</B> Add 257 characters to the Blog Link fields, click Save button. Verify the Error message "Blog Link: cannot be greater than 256 characters." </li>
	*<li><B>Verify:</B> Try again with 256 characters in the Blog Link, click Save button. Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Verify:</B> Add 129 characters to the Job Title fields, click Save button. Verify the Error message "Job Title: cannot be greater than 128 characters."</li>
	*<li><B>Verify:</B> Try again with 128 characters in the Job Title, click Save button.Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Start typing a vaild user name in the Assistant field....</li>
	*<li><B>Verify:</B> the popup list box with valid matches is presented.</li>
	*<li><B>Step:</B> Select one of the valid matches and click the Save button. Verify the message "Profile data updated successfully." is displayed</li>
	*<li><B>Step:</B> Click the Time Zone field.</li>
	*<li><B>Verify:</B> The popup list box with valid time zones is displayed.</li>
	*<li><B>Verify:</B> Select GMT -5:00 and click Save button.Verify the message "Profile data updated successfully." is displayed</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/05F394D11ACEC0B885257ED100441C96">OP - IC Profiles Regression 11: My Profile Page - Contact Information</a></li>
	*</ul>
	*/
	@Test(groups={"ptc"})
	public void editContactInformationOnPrem() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
				
		//Edit My Profile
		log.info("INFO: Edit My Profile");
		ui.clickLinkWait(ProfilesUIConstants.MyICProfileCloud);
		
		//The message "Update information that you want to change" is displayed
		log.info("INFO: The message 'Update information that you want to change' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().editProfileInfo),
				"ERROR: The message 'Update information that you want to change' is not displayed");
		
		//Add 65 characters to the Building field
		log.info("INFO: Add 65 characters to the Building field");
		ui.typeText(ProfilesUIConstants.EditProfileBuilding, Data.getData().Chars1000.substring(0, 65));
		
		//The message "Profile changed. Click the save button to save your changes" is displayed
		log.info("INFO: The message 'Profile changed. Click the save button to save your changes' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().profileChangedMsg),
				"ERROR: The message 'Profile changed. Click the save button to save your changes' is not displayed");
		
		//Add 65 characters in the Building field, click Save button
		log.info("INFO: Add 65 characters in the Building field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditProfileBuilding, Data.getData().Chars1000.substring(0, 65), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Building ID:").replaceAll("COUNT", "64")),
				"ERROR:- Building field did not show up error msg");
		
		//Add 64 characters in the Building field, click Save button
		log.info("INFO: Add 64 characters in the Building field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditProfileBuilding, Data.getData().Chars1000.substring(0, 64), Data.getData().updateSuccessMsg),
				"ERROR:- Building field is not successfully updated");
			
		//Add 17 characters to the FLOOR field, click Save button.
		log.info("Add 17 characters to the FLOOR field, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditProfileFloor, Data.getData().Chars1000.substring(0, 17), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Floor:").replaceAll("COUNT", "16")),
				"ERROR:- Floor field did not show up error msg");
		
		//Add 16 characters to the FLOOR field, click Save button.
		log.info("Add 16 characters to the FLOOR field, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditProfileFloor, Data.getData().Chars1000.substring(0, 16), Data.getData().updateSuccessMsg),
				"ERROR:- Floor field is not successfulyy updated");
		
		//Add 33 characters to the OFFICE field, click Save button.
		log.info("Add 33 characters to the OFFICE field, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditProfileOffice, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Office Name:").replaceAll("COUNT", "32")),
				"ERROR:- OFFICE field did not show up error msg");
		
		//Add 32 characters to the OFFICE field, click Save button.
		log.info("Add 32 characters to the OFFICE field, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditProfileOffice, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- OFFICE field is not successfulyy updated");
		
		//Add 33 characters in the Office Number field, click Save button.
		log.info("INFO: Add 33 characters in the Office Number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserTele, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Office Number:").replaceAll("COUNT", "32")),
				"ERROR:- Office Number does not prompt error msg");
		
		//Add 32 characters in the Office Number field, click Save button.
		log.info("INFO: Add 32 characters in the Office Number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserTele, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- Office Number field is not successfully updated"); 
				
		//Add 33 characters in the IP Telephone Number field, click Save button.
		log.info("INFO: Add 33 characters in the IP Telephone Number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditIpTelephoneNumber, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "IP Telephone Number:").replaceAll("COUNT", "32")),
				"ERROR:- IP Telephone Number does not prompt error msg");
		
		//Add 32 characters in the IP TELEPHONY NUMBER field, click Save button.
		log.info("INFO: Add 32 characters in the IP TELEPHONY NUMBER field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditIpTelephoneNumber, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- IP TELEPHONY NUMBER field is not successfully updated");
		
		//Add 33 characters in the Mobile number field, click Save button.
		log.info("INFO: Add 33 characters in the Mobile number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserMobile, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Mobile number:").replaceAll("COUNT", "32")),
				"ERROR:- Mobile number does not prompt error msg");
		
		//Add 32 characters in the Mobile number field, click Save button.
		log.info("INFO: Add 32 characters in the Mobile number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserMobile, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- Mobile number field is not successfully updated"); 
		
		//Add 33 characters to the Pager Number fields, click Save button. 
		log.info("INFO: Add 33 characters in the Mobile number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditPagerNo, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Pager Number:").replaceAll("COUNT", "32")),
				"ERROR:- Pager number does not prompt error msg");
		
		//Add 32 characters to the Pager Number fields, click Save button. 
		log.info("INFO: Add 32 characters in the Pager number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditPagerNo, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- Pager number field is not successfully updated");
		
		//Add 33 characters to the Fax Number fields, click Save button. 
		log.info("INFO: Add 33 characters in the Fax number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserFax, Data.getData().Chars1000.substring(0, 33), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Fax Number:").replaceAll("COUNT", "32")),
				"ERROR:- Fax number does not prompt error msg");
		
		//Add 32 characters to the Fax Number fields, click Save button. 
		log.info("INFO: Add 32 characters in the Fax number field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileUserFax, Data.getData().Chars1000.substring(0, 32), Data.getData().updateSuccessMsg),
				"ERROR:- Fax number field is not successfully updated");
		
		// Add 129 characters to the Alternate Email fields, click Save button.
		log.info("INFO:  Add 129 characters to the Alternate Email fields, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditAlternateEmail, Data.getData().Chars1000.substring(0, 129), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Alternate Email:").replaceAll("COUNT", "128")),
				"ERROR: Alternate Email field does not prompt error msg");
		
		// Add 128 characters to the Alternate Email fields, click Save button.
		log.info("INFO:  Add 128 characters to the Alternate Email fields, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditAlternateEmail, Data.getData().Chars1000.substring(0, 128), Data.getData().updateSuccessMsg),
				"ERROR: Alternate Email field is not successfully updated"); 
		
		//Add 257 characters to the Blog Link fields, click Save button. 
		log.info("INFO: Add 257 characters to the Blog Link fields, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditBlogURL, Data.getData().Chars1000.substring(0, 257), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Blog URL:").replaceAll("COUNT", "256")),
				"ERROR: Blog Url field does not prompt error msg");
		
		//Add 256 characters to the Blog Link fields, click Save button. 
		log.info("INFO: Add 256 characters to the Blog Link fields, click Save button. Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.EditBlogURL, Data.getData().Chars1000.substring(0, 256), Data.getData().updateSuccessMsg),
				"ERROR: Blog Url field is not successfully updated");
		
		//Add 129 characters to the Job Title field, click Save button.
		log.info("INFO: Add 129 characters to the Job Title field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileJobTitle, Data.getData().Chars1000.substring(0, 129), Data.getData().wordCountExceedLimitMsg.replaceAll("FIELD", "Job Title").replaceAll("COUNT", "128")),
				"ERROR:- Job Title field does not prompt error msg"); 
		
		//Add 128 characters to the Job Title field, click Save button.
		log.info("INFO: Add 128 characters to the Job Title field, click Save button and Verify msg is displayed");
		Assert.assertTrue(ui.updateContactInfoAndSave(ProfilesUIConstants.profileJobTitle, Data.getData().Chars1000.substring(0, 128), Data.getData().updateSuccessMsg),
				"ERROR:- Job Title field is not successfully updated"); 
		
		//Start typing a valid user name in the Assistant field...
		log.info("INFO: Start typing a valid user name in the Assistant field...");
		ui.clearText(ProfilesUIConstants.EditAssistant);
		ui.typeTextWithDelay(ProfilesUIConstants.EditAssistant, testUser1.getDisplayName());
		
		//Verify the popup list box with valid matches is presented
		log.info("INFO: Verify the popup list box with valid matches is presented");
		Assert.assertTrue(ui.typeaheadAndValidateMatches(testUser1.getDisplayName(), ProfilesUIConstants.AssistantTypeAHead),
				"ERROR:- Popup list box does not have valid matches");
		
		//Select one of the valid matches and click the Save button.
		log.info("INFO: Select one of the valid matches and click the Save button");
		hUI.typeaheadSelection(testUser1.getDisplayName(), ProfilesUIConstants.AssistantTypeAHead);
		
		//Click on Save button
		log.info("INFO: Click on Save button");
		ui.clickSaveButton();
		
		//The message "Profile data updated successfully." is displayed
		log.info("INFO: The message 'Profile data updated successfully.' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().updateSuccessMsg),
				"ERROR: The message 'Profile data updated successfully.' is not displayed");
		
		//Click the Time Zone field.
		log.info("INFO: Click the Time Zone field.");
		ui.clickLink(ProfilesUIConstants.TimeZoneDropDown);
		
		//The popup list box with valid time zone is displayed.
		log.info("INFO: Verify the popup list box with valid time zone is displayed.");
		Assert.assertTrue(ui.isTextPresent(Data.getData().easternTimeZone),
				"ERROR: The popup list box with valid time zone is not displayed.");
		
		//Select (GMT-05:00) Eastern Time (US & Canada) and click Save button
		log.info("INFO: Select (GMT-05:00) Eastern Time (US & Canada)");
		driver.getSingleElement(ProfilesUIConstants.TimeZoneDropDown).useAsDropdown().selectOptionByVisibleText(Data.getData().easternTimeZone);
		
		//Click on Save button
		log.info("INFO: Click on Save button");
		ui.clickSaveButton();
		
		//The message "Profile data updated successfully." is displayed
		log.info("INFO: The message 'Profile data updated successfully.' is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().updateSuccessMsg),
				"ERROR: The message 'Profile data updated successfully.' is not displayed");	
		
		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_Contact Information</B></li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 11: My Profile Page - Contact Information (2 of 2)</li>
	*<li><B>Info:</B> The My Profile page's Contact Information verify data in read mode </li>
	*<li><B>Step:</B> Open the My Profile page -> Select the Contact Information tab</li>
	*<li><B>Verify:</B> The "Contact Information" tab label, the default Contact info contains Name:  <user name> and Office:  <user email> </li>
	*<li><B>Step:</B> Click Edit my Profile button , update information and Save and Close</li>
	*<li><B>Verify:</B> The Profile information form and verify data in read mode.</li>
	*<li><B>Verify:</B> Open Profile information tab and verify data in read mode.Verify all data that was entered in previous tests</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/05F394D11ACEC0B885257ED100441C96">OP - IC Profiles Regression 11: My Profile Page - Contact Information</a></li>
	*</ul>
	*/
	@Test(groups={"ptc"})
	public void verifyContactInfoDetailsOnPrem() throws Exception {
		
		//Start Test
		ui.startTest();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		BaseProfile profile = new BaseProfile.Builder(testUser1.getDisplayName())
											 .build();
		
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Select the Contact Information tab
		log.info("INFO: Select the Contact Information tab");
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		ui.gotoContactInformation();
		
		//The "Contact Information" tab label
		log.info("INFO: Verify the 'Contact Information' tab label");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ContactInfoTab),
				"ERROR: 'Contact Information' tab label is not present");
		
		//The Default Contact info contains User Name and Email.
		log.info("INFO: Verify Contact Information contains User Name");
		String contactTabDetails = driver.getFirstElement(ProfilesUIConstants.ContactInfoDetails).getText(); //Contains Contact Information Details
		Assert.assertTrue(contactTabDetails.contains("Name:\n"+ testUser1.getDisplayName()),
				"ERROR: Contact Information is missing User Name");
		
		log.info("INFO: Verify Contact Information contains User Email");
		Assert.assertTrue(contactTabDetails.contains("Office email:\n"+ testUser1.getEmail()),
				"ERROR: Contact Information is missing User Email");
		
		//Edit My profile
		log.info("INFO: To Edit My Profile");
		profile.setBuilding(Data.getData().Chars1000.substring(0, 64));
		profile.setFloor(Data.getData().Chars1000.substring(0, 16));
		profile.setOffice(Data.getData().Chars1000.substring(0, 32));
		profile.setOfficeNum(Data.getData().Chars1000.substring(0, 32));
		profile.setIPTelephone(Data.getData().Chars1000.substring(0, 32));
		profile.setMobileNum(Data.getData().Chars1000.substring(0, 32));
		profile.setPager(Data.getData().Chars1000.substring(0, 32));
		profile.setFaxNum(Data.getData().Chars1000.substring(0, 32));
		profile.setAlternateEmail(Data.getData().Chars1000.substring(0, 128));
		profile.setBlog(Data.getData().Chars1000.substring(0, 256));
		profile.setJobTitle(Data.getData().Chars1000.substring(0, 128));
		profile.setAssistant(testUser1.getDisplayName());
		profile.edit(ui);
		
		//Select the Contact Information tab
		log.info("INFO: Select the Contact Information tab");
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		ui.gotoContactInformation();
				
		//verify updated data in Contact Information Details
		log.info("INFO: Verify updated data in Contact Information Details");
		ui.fluentWaitTextPresent("Name:\n"+ testUser1.getDisplayName());
		String contactInfo = driver.getFirstElement(ProfilesUIConstants.ContactInfoDetails).getText(); //Contains Contact Information details
		
		//Updated Building is displayed
		log.info("INFO: Verify Updated Building is displayed");
		Assert.assertTrue(contactInfo.contains("Building:\n"+ profile.getBuilding()),
				"ERROR: Updated Building is not displayed");
		
		//Updated Floor is displayed
		log.info("INFO: Verify Updated Floor is displayed");
		Assert.assertTrue(contactInfo.contains("Floor:\n"+ profile.getFloor()),
				"ERROR: Updated Floor is not displayed");
		
		//Updated Office is displayed
		log.info("INFO: Verify Updated Office is displayed");
		Assert.assertTrue(contactInfo.contains("Office:\n"+ profile.getOffice()),
				"ERROR: Updated Other Information2 is not displayed");
		
		//Updated Office number is displayed
		log.info("INFO: Verify Updated Office number is displayed");
		Assert.assertTrue(contactInfo.contains("Office number:\n"+ profile.getOfficeNum()),
				"ERROR: Updated Office number is not displayed");
				
		//Updated Mobile number is displayed
		log.info("INFO: Verify Updated Mobile number is displayed");
		Assert.assertTrue(contactInfo.contains("Mobile number:\n"+ profile.getMobileNum()),
				"ERROR: Updated Mobile number is not displayed");
		
		//Updated IP telephone number is displayed
		log.info("INFO: Verify Updated IP telephone number is displayed");
		Assert.assertTrue(contactInfo.contains("IP telephony number:\n"+ profile.getIPTelephone()),
				"ERROR: Updated IP telephone number is not displayed");
				
		//Updated Pager number is displayed
		log.info("INFO: Verify Updated Pager number is displayed");
		Assert.assertTrue(contactInfo.contains("Pager number:\n"+ profile.getPager()),
				"ERROR: Updated Pager number is not displayed");
		
		//Updated Fax number is displayed
		log.info("INFO: Verify Updated Fax number is displayed");
		Assert.assertTrue(contactInfo.contains("Fax number:\n"+ profile.getFaxNum()),
				"ERROR: Updated Fax number is not displayed");
		
		//Updated Alternate Email is displayed
		log.info("INFO: Verify Alternate Email is displayed");
		Assert.assertTrue(contactInfo.contains("Alternate email:\n"+ profile.getAlternateEmail()) ,
				"ERROR: Updated Alternate Email is not displayed");
		
		//Updated Blog link is displayed
		log.info("INFO: Verify Blog link is displayed");
		Assert.assertTrue(contactInfo.contains("Blog link:\n"+ Data.getData().BlogLink),
				"ERROR: Blog link is not displayed");
		Assert.assertTrue(driver.getFirstElement("link="+Data.getData().BlogLink).getAttribute("href").contains(profile.getBlog()),
				"ERROR: Updated Blog link data is not displayed");
		
		//Updated Assistant details is displayed
		log.info("INFO: Verify Assistant details is displayed");
		Assert.assertTrue(contactInfo.contains("Assistant:\n"+ profile.getAssistant()),
				"ERROR: Updated Assistant details is not displayed");
						
		//Updated JobTitle is displayed
		log.info("INFO: Verify updated JobTitle is displayed");
		Assert.assertTrue(contactInfo.contains("Job title:\n"+ profile.getJobTitle()),
				"ERROR: Updated JobTitle is not displayed");
				
		//End Test
		ui.endTest();
	}
}
