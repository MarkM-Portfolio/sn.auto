package com.ibm.conn.auto.tests.profiles;

import java.text.ParseException;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;

public class BVT_BoschUAT_Profiles extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Profiles.class);
	private TestConfigCustom cfg;
	private HomepageUI ui;
	private ProfilesUI profilesUi,profilesUiBG,profilesUiAM;
	private HomepageUI homepageUi;
	private User testUserA,testUserB;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		testUserA = cfg.getUserAllocator().getUser(this);
		testUserB = cfg.getUserAllocator().getUser(this);	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpMethod() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		profilesUi = ProfilesUI.getGui(cfg.getProductName(), driver);	
		profilesUiBG = ProfilesUI.getGui(cfg.getProductName(), driver);	
		profilesUiAM = ProfilesUI.getGui(cfg.getProductName(), driver);	
		homepageUi = HomepageUI.getGui(cfg.getProductName(), driver);		

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the invitation sent by UserA is received by UserB</li>
	 *<li><B>Step:</B> Login with testUserA and Navigate To My Profile Directory</li> 
	 *<li><B>Verify:</B> Search for testUSerB and verify testUSerB displayed</li>
	 *<li><B>Verify:</B> Hover on testUSerB name and verify the Business card</li>
	 *<li><B>Step:</B> Click on More Actions Send Invitation from testUserB</li>
	 *<li><B>Step:</B> Logout as testUSerA and Login as testUserB</li>
	 *<li><B>Step:</B> Navigate To My Profile Network and click on Invitations in Left Nav</li>
	 *<li><B>Verify:</B> Verify that invitation sent from testUSerA is received and displayed in testUSerB invitation section</li>
	 *</ul>
	 */
	@Test(groups = {"regression","mt-exclude"})
	public void verifyBusinessCardActionInviteToMyNetwork() {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
				
		ui.startTest();
		// Load the component and login
		log.info("Load My Profiles and Log In as " + testUserA.getDisplayName());
		logger.strongStep("Load Activities and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUserA);
		
		logger.strongStep("INFO : Click on Directory tab");
		log.info("INFO : Click on Directory tab");
		profilesUi.clickLinkWait(ProfilesUIConstants.DirectoryTab);
		
		logger.strongStep("Search for " + testUserB.getDisplayName() +"and Verify " +  testUserB.getDisplayName() + "is displayed");
		log.info("Search for " + testUserB.getDisplayName() +"and Verify " +  testUserB.getDisplayName() + "is displayed");
		Assert.assertTrue(profilesUi.isDirectorySearchResultExactMatching(testUserB));
		

		logger.strongStep("Hover on " + testUserB.getLastName() + "and Verify Business card");
		log.info("Hover on " + testUserB.getLastName() + "and Verify Business card");
		homepageUi.verifyBizCardContentForInternalUser(testUserB);

		logger.strongStep("INFO :Click on More Action and Click on Invite To My Network");
		log.info("INFO : Click on More Action and Click on Invite To My Network");
		driver.getFirstElement(HomepageUIConstants.bizCardMoreActionsLink).click();
		driver.getFirstElement(HomepageUIConstants.bizCardInviteToMyNetwork).click();
		profilesUi.fluentWaitElementVisible(ProfilesUIConstants.SendInvite);
		driver.getFirstElement(ProfilesUIConstants.SendInvite).click();
		
		log.info("Logout as " + testUserA.getDisplayName() + "Load My Profiles and Log In as " + testUserB.getDisplayName());
		logger.strongStep("Logout as " + testUserA.getDisplayName() + "Load My Profiles and Log In as " + testUserB.getDisplayName());
		ui.logout();		
		ui.loadComponent(Data.getData().ComponentProfiles,true);
		ui.login(testUserB);
		
		logger.strongStep("INFO : Navigate to Invitations from My Profile My Network");
		log.info("INFO : Navigate to Invitations from My Profile My Network");
		profilesUi.clickLinkWait(ProfilesUIConstants.MyNetwork);
		profilesUi.clickLinkWait(ProfilesUIConstants.LeftNavInvitations);
		
		logger.strongStep("Verify that " + testUserA.getDisplayName()+ "is displayed under Invitation section of " + testUserB.getDisplayName());
		log.info("Verify that " + testUserA.getDisplayName()+ "is displayed under Invitation section of " + testUserB.getDisplayName());
		Assert.assertTrue(profilesUi.isInviterUserNameDisplayed(testUserA));
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify inserted Image Preview in Profile About Me and Profile BackGround</li>
	 *<li><B>Step:</B> Login with testUserA and Navigate To Edit My Profile</li> 
	 *<li><B>Step:</B> Navigate to Background tab</li>
	 *<li><B>Step:</B> Insert Link text and value in AboutMe CKEditor and Click OK</li>
	 *<li><B>Step:</B>Insert Link text and value in AboutMe CKEditor and Click OK</li>
	 *<li><B>Step:</B> Click Save and Close</li>
	 *<li><B>Verify:</B> Verify the image preview displayed when click on Link in AboutMe Description and Background Description</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyInsertImageURLProfiles() throws ParseException {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String urlInputValue = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQw1L39-k24Hyzoi0Sy7Bgg8auce5a8udLnZlFh-7ogvGq1pCB9";
		String urlTextValue = "backGroundAboutme";
		
		ui.startTest();
		
		log.info("Load Community and Log In as " + testUserA.getDisplayName());
		logger.strongStep("Load Community and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);
			
		logger.strongStep("Hover over the Profiles Mega Menu and click on Edit My Profile link");
		log.info("Hover over the Profiles Mega Menu and click on Edit My Profile link");
		driver.getFirstElement(ProfilesUIConstants.megaMenuProfiles).hover();
		ui.clickLinkWithJavascript(ProfilesUIConstants.megaMenuProfilesEditMyProfile);
		
		logger.strongStep("Go to Background tab");
		log.info("Go to Background tab");
		profilesUi.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
		
		logger.strongStep("Update locators for AboutMe CKEditor");
		log.info("Update locators for AboutMe CKEditor");
		ui.fluentWaitTextPresent("About me:");
		profilesUiBG.updateLocators("About me:");
		
		logger.strongStep("Clear AboutMe CKEditor");
		log.info("Clear AboutMe CKEditor");
		profilesUi.switchToFrameBySelector(profilesUiBG.statusUpdateIframeAmoutMeBackground);
		driver.getFirstElement(BaseUIConstants.ckEditorBodyPar).clear();
		profilesUi.switchToTopFrame();
		
		logger.strongStep("Click on  Inserted Link");
		log.info("INFO: Click on Inserted Link");
		profilesUi.clickLinkWait(profilesUiBG.insertLinkAboutMeBAckGround);
		
		logger.strongStep("Type in Url Text and Value and Click 'OK' button");
		log.info("INFO: Type in Url Text and Value and Click 'OK' button");
		profilesUi.typeText(BaseUIConstants.urlInputField, urlInputValue);
		profilesUi.typeText(BaseUIConstants.linkTextInputField,urlTextValue );
		profilesUi.clickLinkWait(BaseUIConstants.okButtonURLForm);
		
		logger.strongStep("Update locators for Background CKEditor");
		log.info("Update locators for Background CKEditor");
		profilesUiAM.updateLocators("Background:");
		
		logger.strongStep("Clear Background CKEditor");
		log.info("Clear Background CKEditor");
		profilesUi.switchToFrameBySelector(profilesUiAM.statusUpdateIframeAmoutMeBackground);
		driver.getFirstElement(BaseUIConstants.ckEditorBodyPar).clear();
		profilesUi.switchToTopFrame();
		
		logger.strongStep("Click on  Inserted Link");
		log.info("INFO: Click on Inserted Link");
		profilesUi.clickLinkWait(profilesUiAM.insertLinkAboutMeBAckGround);
		
		logger.strongStep("Type in Url Text and Value and Click 'OK' button");
		log.info("INFO: Type in Url Text and Value and Click 'OK' button");
		profilesUi.typeText(BaseUIConstants.urlInputField, urlInputValue);
		profilesUi.typeText(BaseUIConstants.linkTextInputField,urlTextValue );
		profilesUi.getFirstVisibleElement(BaseUIConstants.okButtonURLForm).click();
		
		logger.strongStep("Click on Save Button");
		log.info("INFO: Click on Inserted Link");
		profilesUi.clickLinkWait(ProfilesUIConstants.SaveAndCloseBtn);

		logger.strongStep("Verify inserted Link Preview in Profiles About Me and Background");
		log.info("Verify inserted Link Preview in Profiles About Me and Background");
		profilesUi.verifyInsertedLink(urlTextValue);
		
		ui.endTest();

		}

}
