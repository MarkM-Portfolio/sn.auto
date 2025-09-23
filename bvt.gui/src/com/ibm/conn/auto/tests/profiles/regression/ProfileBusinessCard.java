package com.ibm.conn.auto.tests.profiles.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.OrgConfig;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;
import com.ibm.conn.auto.webui.onprem.ProfilesUIOnPrem;


public class ProfileBusinessCard extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(ProfileBusinessCard.class);
	private ProfilesUI ui;
	private ActivitiesUI Aui;
	private TestConfigCustom cfg;
	private FilesUI Fui;
	
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
		Aui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		Fui = FilesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	*<ul>
	*<li><B>Profile_BusinessCard_Create_Activity</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 06: My Profile Page - Business Card - Create Activity </li>
	*<li><B>Info:</B> To Create Activity and verify Activity displays in My Activities view</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Click - select the Business Card button, to the right of the user's name.</li>
	*<li><B>Step:</B> Click More Actions link in Business card and Click Start an Activity action</li>
	*<li><B>Verify:</B> The New Activity basic form is opened in a new browser tab.</li>
	*<li><B>Step :</B> Enter name, tag, activity goal, due date and Click on Save button.</li>
	*<li><B>Verify:</B> The Start an Activity tab closes</li> 
	*<li><B>Step:</B> Open Activities App</li>
	*<li><B>Verify:</B> The New Activity displays in the My Activities view.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/C7D504C426E75ED785257E05006A4AE7">SC - IC Profiles Regression 06: My Profile Page - Business Card - Create Activity</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc"})
	public void profileCreateActivity() throws Exception {
		
		//Unique number
		String uniqueId = Helper.genDateBasedRandVal();
		User testUser1; //testUser
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Create an Activity
		log.info("INFO: Select the Business Card, More Actions -> Start an Activity and Create an Activity");
		ui.createAnActivity(uniqueId);
		
		//Open Activities App and wait for page load
		Aui.gotoActivitiesMainPage();
		ui.waitForPageLoaded(driver);
		
		//The Start an Activity tab closes
		log.info("INFO: The Start an Activity tab closes");
		Assert.assertTrue( driver.getWindowHandles().size() == 1 && !driver.getTitle().contains(Data.getData().ComponentActivitiesKeyText),
				"ERROR: Start an Activity tab is not closed");
		
		//Open Activities App
		log.info("INFO: Open Activities App");
		Aui.gotoActivitiesMainPage();
		
		//The New Activity displays in the My Activities view
		log.info("INFO: Verify the New Activity displays in the My Activities view");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+ Data.getData().Start_An_Activity_InputText_Name_Data + uniqueId));
		
		//End test
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_BusinessCard_Basics</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 05: My Profile Page - Business Card - Basics (1 of 2)</li>
	**<li><B>Test Scenario:</B> OP - IC Profiles Regression 05: My Profile Page - Business Card - Basics</li>
	*<li><B>Info:</B> To Verify: the My Profile page's Business Card functionality</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Click - select the Business Card button, to the right of the user's name.</li>
	*<li><B>Verify:</B> The card contains Profiles, Files, Chat, More Actions and Start an Activity links</li>
	*<li><B>Verify:</B> The card contains Profiles, Wikis, Communities, Files, Blogs, Bookmarks, Forums, Activities, Send EmailFiles, More Actions - Send Email & Download VCard links</li>
	*<li><B>Verify:</B> The card contains the User's Name, Job Title, Address, email address, office phone#</li>
	*<li><B>Verify:</B> The card contains the User's Name, Job Title, Organization name, Address, email address, office phone# and Sametime chat status</li>
	*<li><B>Step:</B> Close the user's Business Card, by using the <Esc> key.</li>
	*<li><B>Verify :</B> The business card closes</li>
	*<li><B>Step:</B> Close the user's Business Card, by moving focus out of the Business Card.</li> 
	*<li><B>Verify:</B> The business card closes after a few seconds from when the focus leaves the card.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/810C109B3AFCDA2C85257E05005E8E7D">SC - IC Profiles Regression 05: My Profile Page - Business Card - Basics</a></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/28BA99423398AD9F85257ECA004E0167">OP - IC Profiles Regression 05: My Profile Page - Business Card - Basics</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ptc"})
	public void businessCardDetails() throws Exception {
		
		//Get UniqueId
		String uniqueId = Helper.genDateBasedRandVal();
		String orgName;
		User testUser1; //testUser
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//load the My Profile view
		log.info("INFO: Switch to my Profile view");
		ui.fluentWaitPresent(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.open(ui);
		orgName = driver.getFirstElement(Profile_View_Menu.ORG_Directory.getMenuItemLink()).getText().replaceAll("Directory", "").trim();
		Profile_View_Menu.MY_PROFILE.select(ui);

		//Click on the Edit Profile button
		log.info("INFO: Select the edit my profile button");
		ui.editMyProfile();

		//Update the Users Profile
		log.info("INFO: Update the Users Profile");
		ui.updateProfile(uniqueId);
		
		//Click on Business card
		log.info("INFO: open Business card menu drop icon");
		ui.waitForSameTime();
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		ui.openProfileBusinessVcard();

		//The User business card is opened
		log.info("INFO: Verify the User business card  is opened");
		Assert.assertTrue(ui.fluentWaitElementVisible(ProfilesUIConstants.BusinessCardWindow),
						 "ERROR: The User business card  is not opened");
		
		//Verify all the links are present in Business Card
		log.info("INFO: Verify all the links are present in Business Card");
		ui.verifyLinksInBusinessCard();
		
		//Open Business card and Close it by using <Esc> key
		log.info("INFO: Open Business card");
		ui.openProfileBusinessVcard();
		
		//Verify updated profile data in Business Card
		log.info("INFO: Verify updated profile data in Business Card");
		ui.verifyProfileDataInBusinessCard(testUser1, uniqueId, orgName);
		
		//Close it by using <Esc> key
		log.info("INFO: Close it by using <Esc> key");
		driver.getSingleElement(ProfilesUIConstants.BusinessCardWindow).type(Keys.ESCAPE);
		driver.getSingleElement(ProfilesUIConstants.MyICProfileCloud).hover();
		
		//The business card closes
		log.info("INFO: Verify business card closes");
		ui.sleep(5000);
		Assert.assertFalse(ui.isElementPresent(ProfilesUIConstants.BusinessCardWindow) && driver.getFirstElement(ProfilesUIConstants.BusinessCardWindow).isVisible(),
				"ERROR: Business card is not closed");
		
		//Open Business card and Close the user's Business Card, by moving focus out of the Business Card
		log.info("INFO: Open Business card");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
		
		//Close the user's Business Card, by moving focus out of the Business Card
		log.info("INFO: Close the user's Business Card, by moving focus out of the Business Card");
		driver.getSingleElement(ProfilesUIConstants.MyICProfileCloud).hover();
		
		//The business card closes
		log.info("INFO: Verify business card closes");
		ui.sleep(5000);
		Assert.assertFalse(ui.isElementPresent(ProfilesUIConstants.BusinessCardWindow) && driver.getFirstElement(ProfilesUIConstants.BusinessCardWindow).isVisible(),
				"ERROR: Business card is not closed");

		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_BusinessCard_Functionality</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 05: My Profile Page - Business Card - Basics (2 of 2)</li>
	*<li><B>Info:</B> To Verify: the My Profile page's Business Card functionality</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Click - select the Business Card button, to the right of the user's name.</li>
	*<li><B>Step:</B> Click My Profile link in Business card</li>
	*<li><B>Verify:</B> The user's My Profile page is opened</li>
	*<li><B>Step:</B> Click Files link in Business card.</li>
	*<li><B>Verify :</B> The My Files page is opened</li>
	*<li><B>Step:</B> Click Chat link in Business card</li> 
	*<li><B>Verify:</B> The Chat window is opened.</li>
	*<li><B>Step:</B> Click More Actions link in Business card</li> 
	*<li><B>Verify:</B> The Start an Activity action is displayed.</li>
	*<li><B>Step:</B> Click Start an Activity action</li> 
	*<li><B>Verify:</B> The New Activity basic form is opened in a new browser tab.</li>
	*<li><B>Step:</B> Click Organization link in Business card</li> 
	*<li><B>Verify:</B> The My Organization page is opened..</li>
	*<li><B>Step:</B> Click Email Address link in Business card</li> 
	*<li><B>Verify:</B> The default email application is opened and user name is inserted in the to field</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/810C109B3AFCDA2C85257E05005E8E7D">SC - IC Profiles Regression 05: My Profile Page - Business Card - Basics</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc"})
	public void businessCardFunctionality() throws Exception {
		
		String orgID = null, orgName; //To store OrgID, orgName
		User testUser1; //testUser
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Get current OrganizationId
		List<OrgConfig> orgs = OrgConfig.loadOrgs();
		for (OrgConfig orgConfig : orgs) {
			if(orgConfig.getURI().contains(cfg.getServerURL())) {
				orgID = orgConfig.getOrgID();
				break;
			}
		}
		
		//load the My Profile view
		log.info("INFO: Switch to my Profile view");
		ui.fluentWaitPresent(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.open(ui);
		orgName = driver.getFirstElement(Profile_View_Menu.ORG_Directory.getMenuItemLink()).getText().replaceAll("Directory", "").trim();
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Click on Business card
		log.info("INFO: open Business card menu drop icon");
		ui.openProfileBusinessVcard();
		
		//Click on Profile link in Business card & Verify the user's My Profile page is opened
		log.info("INFO: Click on Profile link in Business card & Verify the user's My Profile page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUICloud.VcardProfileLink, 
				Data.getData().userMyProfilePageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0])),
				"ERROR:- " + testUser1.getDisplayName() + " My Profile page is not opened");
		
		//Click on Business card
		log.info("INFO: open Business card menu drop icon");
		ui.openProfileBusinessVcard();
		
		//Click Files link in Business card & Verify the My Files page is opened
		log.info("INFO: Click Files link in Business card & Verify the My Files page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUICloud.VcardFilesLink, 
				Data.getData().userMyFilesPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0])),
				"ERROR:- My Files page is not opened");
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
				
		//Click Organization link in Business card & Verify the My Organization page is opened
		log.info("INFO: Click Organization link in Business card & the My Organization page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected("link="+orgName, 
				Data.getData().organizationPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("ORGID", orgID)),
				"ERROR:- The My Organization page is not opened"); 
			
		//Click Chat link in Business card & Verify the Chat window is opened
		log.info("INFO: Click Chat link in Business card & Verify the Chat window is opened");
		ui.waitForSameTime();
		if(cfg.isSametimeEnabled()) {		
			//Open My Profile and Click on Business card
			log.info("INFO: open Business card menu drop icon");
			Profile_View_Menu.MY_PROFILE.select(ui);
			ui.openProfileBusinessVcard();
			
			Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUICloud.VcardChatLink, Data.getData().webChatPageTitle.replaceAll("USERNAME", testUser1.getDisplayName()),true),
					"ERROR:- Chat window is not opened"); 
		}
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
		
		//Click More Actions link in Business card
		log.info("INFO: Click More Actions link in Business card");
		ui.clickLinkWithJavascript(ProfilesUICloud.MoreActionsLink);
		
		//Verify the Start an Activity action is displayed.
		log.info("INFO: Verify the Start an Activity action is displayed");
		Assert.assertTrue(ui.isElementPresent(ProfilesUICloud.StartAnActivityLink),
				"ERROR: The Start an Activity action is not displayed");
		
		//Click Start an Activity action & Verify the New Activity basic form is opened in a new browser 
		log.info("INFO: The New Activity basic form is opened in a new browser");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUICloud.StartAnActivityLink, Data.getData().ComponentActivitiesKeyText,
				Data.getData().userCreateActivityPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0])),
				"ERROR:- New Activity basic form is not opened in a new browser");
		
		//TODO - Verifying email application can not be automated
		
		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_Download_Vcard</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 10: My Profile Page - Download vCard</li>
	*<li><B>Info:</B> The My Profile page's Download vCard function</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Click on the Download vCard button</li>
	*<li><B>Verify:</B> Verify the "Export vCard" dialog is presented</li>
	*<li><B>Verify:</B> Verify the dialog contains three radio button choices: International, Western European, Japanese.</li>
	*<li><B>Step:</B> Click the Download button.</li>
	*<li><B>Verify :</B> The file can be saved to the file system.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/AC0E483C52188A1C85257E0A006763B1">SC - IC Profiles Regression 10: My Profile Page - Download vCard</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ptc"})
	public void downloadVcard() throws Exception {
		
		User testUser1; //testUser
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Set the directory for the download and ensure that it is empty
		Fui.setupDirectory();
				
		//load the My Profile view
		log.info("INFO: Switch to my Profile view");
		ui.fluentWaitPresent(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Click on the Download vCard button
		log.info("INFO: Click on the Download vCard button");
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		ui.clickLink(ProfilesUIConstants.DownloadVCardButton);

		//Verify the "Export vCard" dialog is present
		log.info("INFO: Verify the Export vCard dialog is present");
		Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.ExportVcardDialog),
				"ERROR: Export vCard dialog is not present");
		
		//Verify the dialog contains three radio button choices: International, Western European, Japanese.
		log.info("INFO: Verify the dialog contains International Radio button choice");
		Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.InternationalizedRadioButton),
				"ERROR: The dialog does not contain International Radio button choice");
		
		log.info("INFO: Verify the dialog contains Western European button choice");
		Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.WesternEuropeanRadioButton),
				"ERROR: The dialog does not contain Western European button choice");
		
		log.info("INFO: Verify the dialog contains Japanese Radio button choice");
		Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.JapaneseRadioButton),
				"ERROR: The dialog does not contain Japanese Radio button choice");
		
		//Click the Download button
		log.info("INFO: Click the Download button");
		ui.clickButton(ProfilesUIConstants.VcardDailogDownloadButton);
		
		//Verify the file has being downloaded - localhost currently
		Fui.verifyFileDownloaded(testUser1.getEmail()+".vcf");
				
		//End test
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Action_Buttons</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 19: Another User's Profile Page - Action Buttons (1 of 2)</li>
	*<li><B>Info:</B> The Another User's Profile page's action button functions</li>
	*<li><B>Step:</B> Open another user's Profile page</li>
	*<li><B>Step:</B> Click on the Send Email button.</li>
	*<li><B>Verify:</B> The default mail application is launch with the user's email address in the To field.</li>
	*<li><B>Step:</B> Click the Chat button.</li>
	*<li><B>Verify:</B> The Chat window is launched.</li>
	*<li><B>Step :</B> Click the More Actions - Add Contact Record button</li>
	*<li><B>Verify:</B> The "<User Name>" tab is displayed.</li>
	*<li><B>Step:</B> Click the Save button</li>
	*<li><B>Step:</B>Open the same user Profiles as in step 4# and Click the More Actions - View Contact Record button</li>
	*<li><B>Verify:</B>  The User's Contact Record page is displayed and the tab text is <user's name></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/2C71208F2750D49485257E1800690CED">SC - IC Profiles Regression 19: Another User's Profile Page - Action Buttons</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc"})
	public void profileActionButtonFunctionality() throws Exception {
		
		User testUser1, testUser2; //test Users
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//load the My Profile view
		log.info("INFO: Switch to my Profile view");
		ui.fluentWaitElementVisible(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Open testUser2 profile
		log.info("INFO: Open" + testUser2.getDisplayName() + " Profile page" );
		ui.openAnotherUserProfile(testUser2);
		
		//TODO - Verifying email application can not be automated
		
		//Click the Chat button
		log.info("INFO: Click Chat link in Business card & Verify the Chat window is opened");
		ui.waitForSameTime(); //Wait for sametime
		if(cfg.isSametimeEnabled()) {					
			Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.ChatButton, Data.getData().webChatPageTitle.replaceAll("USERNAME", testUser2.getDisplayName()),true),
					"ERROR:- Chat window is not opened"); 
		}
		
		//Verify More Actions -> Add Contact Record is displayed (Which means User is not added to Contact List)
		//Click on More Actions
		log.info("INFO: Click on More Actions");
		ui.fluentWaitTextPresent(Data.getData().feedFooter);
		if(driver.isElementPresent(ProfilesUIConstants.MoreActionButton) && driver.getFirstElement(ProfilesUIConstants.MoreActionButton).isVisible())
			driver.getFirstElement(ProfilesUIConstants.MoreActionButton).doubleClick();
		
		if(driver.isElementPresent(ProfilesUIConstants.AddContactRecordMenuItem) && driver.getFirstElement(ProfilesUIConstants.AddContactRecordMenuItem).isVisible()) {
			
			//Click on Add Contact Record and Verify User contact page is displayed
			log.info("INFO: Click on Add Contact Record and Verify " + testUser2.getDisplayName() + " page title is displayed");
			Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.AddContactRecordMenuItem, testUser2.getDisplayName(), false),
					"ERROR: " + testUser2.getDisplayName() + " contact page is not displayed");
		
			//Click on Save button
			log.info("INFO: Click on Save button");
			ui.clickLinkWait(ProfilesUIConstants.SaveContactButton);
			
			//Open testUser2 profile
			log.info("INFO: Open" + testUser2.getDisplayName() + " Profile page" );
			Profile_View_Menu.MY_PROFILE.select(ui);
			ui.openAnotherUserProfile(testUser2);
			
			//Click on More Actions
			log.info("INFO: Click on More Actions");
			if(driver.isElementPresent(ProfilesUIConstants.MoreActionButton) && driver.getFirstElement(ProfilesUIConstants.MoreActionButton).isVisible())
				driver.getFirstElement(ProfilesUIConstants.MoreActionButton).doubleClick();
		}
	
		//Click on View Contact Record and Verify User contact record page is displayed
		log.info("INFO: Click on View Contact Record and Verify " + testUser2.getDisplayName() + " contact record page is displayed");
		Assert.assertTrue(ui.validateWindowPageTitle(ProfilesUIConstants.ViewContactRecordMenuItem, testUser2.getDisplayName(), false),
				"ERROR: " + testUser2.getDisplayName() + " contact record page is not displayed");
		
		//Verify User contact record page url is appropriate
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().contactRecordPageUrl.replaceAll("SERVER", cfg.getServerURL())),
				"ERROR: "  + testUser2.getDisplayName() + " contact record page url is not appropriate");
				
		//End test
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Profile_Share_a_File</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 19: Another User's Profile Page - Action Buttons (2 of 2)</li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 19: Another User's Profile Page - Action Buttons</li>
	*<li><B>Info:</B> The Another User's Profile page's Share a File button function</li>
	*<li><B>Step:</B> Open another user's Profile page</li>
	*<li><B>Step:</B> Click the Share a File button.</li>
	*<li><B>Verify:</B> The "Select Files" dialog is displayed with the current User's files displayed.</li>
	*<li><B>Step:</B> Select a file and click OK</li>
	*<li><B>Verify:</B> The message " You have successfully shared a file "< file  name > with <user's name >"</li>
	*<li><B>Step :</B> Open the User's profile that we shared a file with and the Files tab - Shared with me</li>
	*<li><B>Verify:</B> The file that was just shared displays in the list.</li>
	*<li><B>Step:</B> Click the More Actions - Download VCard button.</li>
	*<li><B>Verify:</B> The "ExportvCard" dialog is presented.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/2C71208F2750D49485257E1800690CED">SC - IC Profiles Regression 19: Another User's Profile Page - Action Buttons</a></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/1A897CD9D92B2FAE85257EDE006BB979 ">OP - IC Profiles Regression 19: Another User's Profile Page - Action Buttons</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void shareAFileWithAnotherUserProfile() throws Exception {
		
		User testUser1, testUser2; //test Users
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		//File to upload
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1)
											 .extension(".jpg")
											 .shareLevel(ShareLevel.EVERYONE)
											 .rename(Fui.reName(Data.getData().file1))
											 .build();

		//Load component and login
		log.info("INFO: Load component and login");	
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.replaceProductionCookies();
		ui.login(testUser1);
		
		//Verify that the UI is available
		log.info("INFO: Verify the UI is Available");	
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent("My Files");
				
		//Upload file
		log.info("INFO: Upload file");	
		baseFileImage.upload(Fui);
		
		// change the view list format  & logout
		ui.clickLinkWait(FilesUICloud.listView);
		ui.logout();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser1);
		
		//Open testUser2 profile
		log.info("INFO: Open" + testUser2.getDisplayName() + " Profile page" );
		ui.openAnotherUserProfile(testUser2);
		
		//Click on Share a File button
		log.info("INFO: Click on Share a File button");
		ui.clickLinkWithJavascript(ProfilesUIConstants.ShareaFileButton);
		
		//Verify the "Select Files" dialog is displayed
		log.info("INFO: Select Files dialog is displayed");
		Assert.assertTrue(ui.isDailogDisplayed("Select Files"),
				"ERROR: Select Files dialog is not displayed");
		
		//Click Cancel button
		log.info("INFO: Click cancel button");
		ui.clickCancelButton();
		
		//Share a File
		log.info("INFO: Start Sharing a File");
		ui.shareAFile("Recent Files", baseFileImage.getRename() + baseFileImage.getExtension());
		
		//Successfully shared a file message is appropriate
		log.info("INFO: Successfully Share a File message is appropriate");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().shareAFileMsg.replaceAll("USERID", testUser2.getDisplayName()).replaceAll("FILENAME", baseFileImage.getRename() + baseFileImage.getExtension())),
				"ERROR:- Successfully Share a File message is not appropriate");

		//Click on More Actions
		log.info("INFO: Click on More Actions");
		if(driver.isElementPresent(ProfilesUIConstants.MoreActionButton) && driver.getFirstElement(ProfilesUIConstants.MoreActionButton).isVisible())
			driver.getFirstElement(ProfilesUIConstants.MoreActionButton).doubleClick();
		
		//Click Download Vcard
		log.info("INFO: Click Download Vcard menu item");
		ui.clickLink(ProfilesUIConstants.DownloadVCardMenuItem);
		
		//Verify the "Export vCard" dialog is displayed
		log.info("INFO: Verify the Export vCard dialog is displayed");
		Assert.assertTrue(driver.getFirstElement(ProfilesUIConstants.ExportVcardDialog).isDisplayed(),
				"ERROR: The Export vCard dialog is not displayed");
				
		//Logout of testUser1
		ui.logout();
		
		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser2);
		
		//Open Files tab 
		log.info("INFO: Open Files Tab");
		if(cfg.getProductName().toLowerCase().equals("cloud")) {
		ui.fluentWaitElementVisible(BaseUIConstants.CommunityFilesSidebar);
		ui.clickLink(BaseUIConstants.CommunityFilesSidebar);
		
		//Shared with me
		log.info("INFO: Show Shared with me");
		ui.clickLink(ProfilesUIConstants.ShowSharedWithMe);
		
		//The file that was just shared displays in the list
		log.info("INFO: Verify the file that was just shared displays in the list");
		Assert.assertTrue(ui.fluentWaitElementVisible("link="+baseFileImage.getRename() + baseFileImage.getExtension()),
				"ERROR: " + baseFileImage.getRename() + " is not displayed in Files tab");
		}
		//End test
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_BusinessCard_Functionality</B></li>
	*<li><B>Test Scenario:</B> OP - IC Profiles Regression 05: My Profile Page - Business Card - Basics</li>
	*<li><B>Info:</B> To Verify: the My Profile page's Business Card functionality</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Hover over User Name - To Open the Business Card</li>
	*<li><B>Step:</B> Click Profile link in Business card</li>
	*<li><B>Verify:</B> The user's My Profile page is opened</li>
	*<li><B>Step:</B> Click Files link in Business card.</li>
	*<li><B>Verify :</B> The My Files page is opened</li>
	**<li><B>Step:</B> Click Wikis link in Business card.</li>
	*<li><B>Verify :</B> A Search Page was opened for user's Public Wikis</li>
	**<li><B>Step:</B> Click Communities link in Business card.</li>
	*<li><B>Verify :</B> My Organization Communities page is opened.</li>
	**<li><B>Step:</B> Click Bookmarks link in Business card.</li>
	*<li><B>Verify :</B> The Bookmarks: Bookmarks for testUser page is opened</li>
	**<li><B>Step:</B> Click Forums link in Business card.</li>
	*<li><B>Verify :</B> the Forums page: Search results for <YourTestUser></li>
	**<li><B>Step:</B> Click Activities link in Business card.</li>
	*<li><B>Verify :</B> The My Activities page is opened</li>
	**<li><B>Step:</B> Click Email Address link in Business card.</li>
	*<li><B>Verify :</B> The default email application is opened and user name is inserted in the to field.</li>
	*<li><B>Step:</B> Click Send Email link in Business card</li> 
	*<li><B>Verify:</B> the default email application is opened and user name is inserted in the to field.</li>
	*<li><B>Step:</B> Click More Actions - Send Email link in Business card</li> 
	*<li><B>Verify:</B> The default email application is opened and user name is inserted in the to field.</li>
	*<li><B>Step:</B> Click More Actions - Download vCard link in Business card</li> 
	*<li><B>Verify:</B> the Export vCard dialog is displayed.</li>
	*<li><B>Step:</B> Click the Show less arrow control in the Business card.</li> 
	*<li><B>Verify:</B> The small business card is displayed with all the application links hidden.</li>
	*<li><B>Step:</B> Click the Show More arrow control in the Business card./li> 
	*<li><B>Verify:</B> The Full business card is displayed.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/28BA99423398AD9F85257ECA004E0167">OP - IC Profiles Regression 05: My Profile Page - Business Card - Basics</a></li>
	*</ul>
	*/
	@Test(groups={"ptc"})
	public void businessCardFunctionalityOnPrem() throws Exception {
		
		User testUser1; //test User
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//load the My Profile view
		log.info("INFO: Switch to my Profile view");
		ui.fluentWaitPresent(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Get Key, UID
		//Click on Business card
		log.info("INFO: open Business card");
		ui.openProfileBusinessVcard();
		
		//Click on Profile link in Business Card to get key
		log.info("INFO: Click on Profile link in Business Card to get key");
		ui.clickLinkWait(ProfilesUIOnPrem.VcardProfileLink);
		ui.waitForPageLoaded(driver);
		String key=driver.getCurrentUrl().split("key=")[1];
		
		//Click on Business card
		log.info("INFO: open Business card");
		ui.openProfileBusinessVcard();
				
		//Click on Wikis link in Business Card to get uid
		log.info("INFO: Click on Wikis link in Business Card to get uid");
		ui.clickLinkWait(ProfilesUIOnPrem.VcardWikisLink);
		ui.waitForPageLoaded(driver);
		String uid =driver.getCurrentUrl().split("uid=")[1].split("&")[0];
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
		
		//Click on Profile link in Business card & Verify the user's My Profile page is opened
		log.info("INFO: Click on Profile link in Business card & Verify the user's My Profile page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardProfileLink, 
				Data.getData().myProfilePageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("KEY", key)),
				"ERROR:- " + testUser1.getDisplayName() + " My Profile page is not opened");
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
		
		//Click Wikis link in Business card & Verify a Search Page was opened for user's Public Wikis
		log.info("INFO: Click Wikis link in Business card & Verify a Search Page was opened for user's Public Wikis");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardWikisLink, 
				Data.getData().publicWikisSearchPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid).replaceAll("USERNAME", testUser1.getDisplayName().replaceAll(" ", "%20"))),
				"ERROR:- Public Wikis page is not opened");
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
		
		//Click Files link in Business card & Verify the My Files page is opened
		log.info("INFO: Click Files link in Business card & Verify the My Files page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardFilesLink, 
				Data.getData().userMyFilesPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", uid)),
				"ERROR:- My Files page is not opened");
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
				
		//Click Communities link in Business card & Verify the My Organization Communities page is opened
		log.info("INFO: Click Communities link in Business card & Verify the My Organization Communities page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardCommunitiesLink, 
				Data.getData().myOrganizationCommunitiesUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid)),
				"ERROR:- My Organization Communities page is not opened"); 
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
				
		//Click Blogs link in Business card & Verify the My Blogs page is opened
		log.info("INFO: Click Blogs link in Business card & Verify the My Blogs page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardBlogsLink, 
				Data.getData().myBlogsUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid)),
				"ERROR:- My Blogs page is not opened"); 
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
				
		//Click Bookmarks link in Business card & Verify the Bookmarks page is opened
		log.info("INFO: Click Bookmarks link in Business card & Verify the Bookmarks page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardBookmarksLink, 
				Data.getData().userBookmarkUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid)),
				"ERROR:- Bookmarks page is not opened"); 
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard(); 
				
		//Click Forums link in Business card & Verify the Forums page: Search results page is opened
		log.info("INFO: Click Forums link in Business card & Verify the Forums page: Search results page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardForumsLink, 
				Data.getData().userForumsUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid).replaceAll("USERNAME", testUser1.getDisplayName().replaceAll(" ", "%20"))),
				"ERROR:- Forums search results page is not opened"); 
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard();
				
		//Click Activites link in Business card & Verify the My Activites page is opened
		log.info("INFO: Click Activites link in Business card & Verify the My Activites page is opened");
		Assert.assertTrue(ui.validateCurrentUrlWithExpected(ProfilesUIOnPrem.VcardActivitiesLink, 
				Data.getData().myActivitiesPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("UID", uid).replaceAll("USERNAME", testUser1.getDisplayName().replaceAll(" ", "%20"))),
				"ERROR:- My Activites page is not opened"); 
		
		//TODO - Verifying email application can not be automated 
		
		//Open My Profile and Click on Business card
		log.info("INFO: open Business card menu drop icon");
		Profile_View_Menu.MY_PROFILE.select(ui);
		ui.openProfileBusinessVcard(); 
		
		//Click More Actions link in Business card
		log.info("INFO: Click More Actions link in Business card");
		ui.clickLinkWithJavascript(ProfilesUIOnPrem.MoreActionsLink);
		driver.getSingleElement(ProfilesUIOnPrem.BusinessCardWindow).hover();
		
		//Click Download vCard link
		log.info("INFO: Click Download vCard link");
		ui.clickLinkWithJavascript(ProfilesUIOnPrem.MoreActionsDownloadVcardLink);
		
		//Switch to new window
		//Get original window handle
		String originalWindow = driver.getWindowHandle();
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().downloadvCardWindow);
		
		//Verify the "Export vCard" dialog is present
		log.info("INFO: Verify the Export vCard dialog is present");
		Assert.assertTrue(ui.isElementPresent(ProfilesUIConstants.ExportVcardDialog),
				"ERROR: Export vCard dialog is not present");
		
		//Close window and Switch to Main window
		ui.close(cfg);
		driver.switchToWindowByHandle(originalWindow);
		
		//Open Business card
		log.info("INFO: open Business card");
		ui.openProfileBusinessVcard();
		
		//Click the Show less arrow control in the Business card.
		log.info("INFO: Click the Show less arrow control in the Business card.");
		ui.clickLinkWait(ProfilesUIConstants.BusinessCardShowLessMenu);
		
		//Verify the small business card is displayed with all the application links hidden.
		log.info("INFO: Verify Profile link is hidden");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.getFirstElement(ProfilesUIOnPrem.VcardWikisLink).isVisible(),
				"ERROR: Application links are not hidden when we click on Show less arrow");
		driver.turnOnImplicitWaits();
		
		//Open Business card
		log.info("INFO: open Business card");
		ui.openProfileBusinessVcard();
				
		//Click the Show more arrow control in the Business card.
		log.info("INFO: Click the Show more arrow control in the Business card.");
		ui.clickLinkWait(ProfilesUIConstants.BusinessCardShowLessMenu);
		
		//Verify the Full business card is displayed
		log.info("INFO: Verify Profile link is shown up");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIOnPrem.VcardProfileLink),
				"ERROR: The Full business card is displayed");
		
		//End test
		ui.endTest();
	}

}
