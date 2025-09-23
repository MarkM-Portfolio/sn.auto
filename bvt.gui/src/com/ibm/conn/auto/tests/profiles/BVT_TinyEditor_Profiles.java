package com.ibm.conn.auto.tests.profiles;


import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_TinyEditor_Profiles extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Profiles.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;

	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {
		super.beforeClass(context);

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My profile and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to My profile BackGround page</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Alignment functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Step:</B>Save My Profile</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on My Profile BackGround Description</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyMyProfileTinyEditorParagraphFunctionality() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("This is Edit Testcase " + testName)
				.background("This is Edit Testcase " + testName)
				.tinyEditorFunctionalityToRun("verifyIndentsInTinyEditor,verifyParaInTinyEditor,verifyRightLeftParagraphInTinyEditor"
						+ ",verifyAlignmentInTinyEditor").build();
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Validate Tiny Editor functionality in About me and BackGround");
		logger.strongStep("Navigate to Edit My Profile Background page");
		String TEText = profile.verifyTinyEditor(ui).trim();
		log.info("TEText is "  + TEText);
		
		log.info("INFO: Navigate to saved Background in My profile");
		logger.strongStep("Navigate to saved Background in My profile");
		ui.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);;
		
		String BackGroundDescriptionText = ui.getBackGroundDescriptionText();
		
		log.info("INFO: Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		logger.strongStep("Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		Assert.assertTrue(BackGroundDescriptionText.contains(TEText));
		
		ui.endTest();
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My profile and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to My profile BackGround page</li>
	 * <li><B>Verify:</B>Verify Permanent Pen functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Font attributes functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Font Size in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Font functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Other Text attributes and full screen functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Text Color functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Step:</B>Save My Profile</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on My Profile BackGround Description</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyMyProfileTinyEditorFontAttributeFunctionality() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("ThisisEditTestcase" + testName)
				.background("ThisisEditTestcase" + testName)
				.tinyEditorFunctionalityToRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
                        + "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
                        + "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor").build();
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Validate Tiny Editor functionality in About me and BackGround");
		logger.strongStep("Navigate to Edit My Profile Background page");
		String TEText = profile.verifyTinyEditor(ui).trim();
		log.info("TEText is "  + TEText);
		
		log.info("INFO: Navigate to saved Background in My profile");
		logger.strongStep("Navigate to saved Background in My profile");
		ui.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);
		
		String BackGroundDescriptionText = ui.getBackGroundDescriptionText();
		
		log.info("INFO: Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		logger.strongStep("Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		Assert.assertTrue(BackGroundDescriptionText.contains(TEText));
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My profile and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to My profile BackGround page</li>
	 * <li><B>Verify:</B>Verify Horizontal Line functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Rows and Columns, images, texts and nested table in  Table of  TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Block Quotes functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Step:</B>Save My Profile</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on My Profile BackGround Description</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyMyProfileTinyEditorLineBulletTableFunctionality() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("This is Edit Testcase " + testName)
				.background("This is Edit Testcase " + testName)
				.tinyEditorFunctionalityToRun("verifyHorizontalLineInTinyEditor,verifyBulletsAndNumbersInTinyEditor,"
						+ "verifyRowsCoulmnOfTableInTinyEditor,verifyBlockQuoteInTinyEditor").build();
		
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.clickLinkWithJavascript(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Validate Tiny Editor functionality in About me and BackGround");
		logger.strongStep("Navigate to Edit My Profile Background page");
		String TEText = profile.verifyTinyEditor(ui).trim();
		log.info("TEText is "  + TEText);
		
		log.info("INFO: Navigate to saved Background in My profile");
		logger.strongStep("Navigate to saved Background in My profile");
		ui.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);;
		
		String BackGroundDescriptionText = ui.getBackGroundDescriptionText();
		
		log.info("INFO: Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		logger.strongStep("Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		Assert.assertTrue(BackGroundDescriptionText.contains(TEText));

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My profile and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to My profile BackGround page</li>
	 * <li><B>Verify:</B>Verify Find and Replace functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Special Character functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Link Image functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Special check functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Undo Redo functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Emotions functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Verify:</B>Verify Word Count functionality in TinyEditor of About me and BackGround</li>
	 * <li><B>Step:</B>Save My Profile</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on My Profile BackGround Description</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyMyProfileTinyEditorFindReplaceSpellcheckUndoRedoSpecialCharLinkImageFunctionality() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("This is Edit Testcase " + testName)
				.background("This is Edit Testcase " + testName)
				.tinyEditorFunctionalityToRun( "verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,verifyUndoRedoInTinyEditor,"
						+ "verifySpecialCharacterInTinyEditor,verifyLinkImageInTinyEditor,verifyEmotionsInTinyEditor,"
						+ "verifyWordCountInTinyEditor,verifyCodeSampleIntinyEditor").build();
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.waitForElementVisibleWd(ui.createByFromSizzle(ProfilesUIConstants.EditBackgroundTab),4);
		ui.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Validate Tiny Editor functionality in About me and BackGround");
		logger.strongStep("Navigate to Edit My Profile Background page");
		String TEText = profile.verifyTinyEditor(ui).trim();
		log.info("TEText is "  + TEText);
		
		log.info("INFO: Navigate to saved Background in My profile");
		logger.strongStep("Navigate to saved Background in My profile");
		ui.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);;
		
		String BackGroundDescriptionText = ui.getBackGroundDescriptionText();
		
		log.info("INFO: Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		logger.strongStep("Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		Assert.assertTrue(BackGroundDescriptionText.contains(TEText));
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My profile and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to My profile BackGround page</li>
	 * <li><B>Step:</B>Insert links in Tiny Editor of BackGround and AboutMe and Save My Profile</li>
	 * <li><B>Verify:</B>Verify links navigations from background of My Profiles</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyMyProfileTinyEditorInsertLink() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("This is Edit Testcase " + testName)
				.background("This is Edit Testcase " + testName)
				.tinyEditorFunctionalityToRun( "verifyInsertLinkImageInTinyEditor").build();
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Validate Tiny Editor functionality in About me and BackGround");
		logger.strongStep("Navigate to Edit My Profile Background page");
		String TEText = profile.verifyTinyEditor(ui).trim();
		log.info("TEText is "  + TEText);
		
		log.info("INFO: Navigate to saved Background in My profile and verify links navigation");
		logger.strongStep("Navigate to saved Background in My profile and verify links navigation");
		ui.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);;

		ui.verifyInsertedLink("CurrentWindow_experience_"+profile.getName()+"~NewWindow_experience_"+profile.getName());
		ui.verifyInsertedLink("CurrentWindow_description_"+profile.getName()+"~NewWindow_description_"+profile.getName());
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to My profile and validate tiny editor edit features</li>
	 * <li><B>Step:</B>Navigate to My profile BackGround page</li>
	 * <li><B>Step:</B>Add some text in Tiny Editor of BackGround and AboutMe and Save My Profile</li>
	 * <li><B>Step:</B>Edit the saved text of Tiny Editor of BackGround and AboutMe and Save My Profile again</li>
	 * <li><B>Verify:</B>Verify Edited text in background of My Profiles</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyMyProfileEditFunctionalityInBlog() throws Exception {
		User testUser = cfg.getUserAllocator().getUser();

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseProfile profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("This is Edit Testcase " + testName)
				.background("This is Edit Testcase " + testName)
				.tinyEditorFunctionalityToRun( "verifyEditDescriptionInTinyEditor").build();
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Add default text in About me and BackGround");
		logger.strongStep("Add default text in About me and BackGround");
		profile.verifyTinyEditor(ui).trim();		
		
		profile = new BaseProfile.Builder(testName + Helper.genDateBasedRandVal())
				.aboutMe("This is Edit Testcase " + testName)
				.background("This is Edit Testcase " + testName)
				.tinyEditorFunctionalityToRun( "verifyEditDescriptionInTinyEditor").build();
		
		log.info("INFO: Navigate to Edit My Profile Background page");
		logger.strongStep("Navigate to Edit My Profile Background page");
		ui.myProfileView();

		ui.editMyProfile();
		
		ui.clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
			
		log.info("INFO: Validate Tiny Editor functionality in About me and BackGround");
		logger.strongStep("Navigate to Edit My Profile Background page");
		String TEText = profile.verifyTinyEditor(ui).trim();
		log.info("TEText is "  + TEText);
		
		log.info("INFO: Navigate to saved Background in My profile");
		logger.strongStep("Navigate to saved Background in My profile");
		ui.clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);;
		
		String BackGroundDescriptionText = ui.getBackGroundDescriptionText();
		
		log.info("INFO: Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		logger.strongStep("Validate the TE text passed in my profile background while edit is same as text saved in my profile background");
		Assert.assertTrue(BackGroundDescriptionText.contains(TEText));
		
		ui.endTest();

	}
}