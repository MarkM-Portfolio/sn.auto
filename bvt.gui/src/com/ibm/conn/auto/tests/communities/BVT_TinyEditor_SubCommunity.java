package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.TinyEditorUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_TinyEditor_SubCommunity extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_SubCommunity.class);
	private CommunitiesUI ui;
	private FilesUI fui;
	private TestConfigCustom cfg;
	private User testUser, testLookAheadUser;
	private Member member;
	private String serverURL;
	private APICommunitiesHandler apiOwner;
	private APIFileHandler apiFileOwner;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();
		member = new Member(CommunityRole.MEMBERS, testLookAheadUser);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());

		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fui = FilesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save content</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Sub Community Description</li>
	 * <li><B>Step:</B>Delete Sub community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifySubCommunityTinyEditorParagraphFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyParaInTinyEditor,verifyRightLeftParagraphInTinyEditor,"
						+ "verifyAlignmentInTinyEditor,verifyIndentsInTinyEditor")
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
        Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
        if (flag) {
            apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
        }
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		// Verify tiny editor functionality
		logger.strongStep("Verify functionality of tiny editor componenet");
		log.info("Verify functionality of tiny editor componenet");
		String TEText = community.verifyTinyEditor(ui).trim();
		String ComText = ui.getCommunityText().trim();
		log.info("INFO: Text in  saved Community " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Permanent Pen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font attributes functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font Size in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Other Text attributes and full screen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Text Color functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save Sub community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Sub community Description</li>
	 * <li><B>Step:</B>Delete Sub community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorFontAttributeFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
						+ "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
						+ "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor")
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
	    Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (true) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		// Verify tiny editor functionality
		logger.strongStep("Verify functionality of tiny editor componenet");
		log.info("Verify functionality of tiny editor componenet");
		String TEText = community.verifyTinyEditor(ui).trim();
		String ComText = ui.getCommunityText().trim();
		log.info("INFO: Text in  saved Community " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Horizontal Line functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Rows and Columns, images, texts and nested table in Table of TinyEditor</li>
	 * <li><B>Step:</B>Save Sub community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Sub community Description</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateSubCommunityTinyEditorLineBulletTableFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyBulletsAndNumbersInTinyEditor,"
						+ "verifyRowsCoulmnOfTableInTinyEditor,verifyBlockQuoteInTinyEditor")
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
	    Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (true) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		// Verify tiny editor functionality
		logger.strongStep("Verify functionality of tiny editor componenet");
		log.info("Verify functionality of tiny editor componenet");
		String TEText = community.verifyTinyEditor(ui).trim();
		String ComText = ui.getCommunityText().trim();
		log.info("INFO: Text in  saved Community " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features.</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Find and Replace functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special Character functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Link Image functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special check functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Undo Redo functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Emotions functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Word Count functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save Sub community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on community Sub Description</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorFindReplaceSpellcheckUndoRedoSpecialCharLinkImageFunctionality()
			throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun(
						"verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,verifyUndoRedoInTinyEditor,"
								+ "verifySpecialCharacterInTinyEditor,verifyLinkImageInTinyEditor,verifyEmotionsInTinyEditor,"
								+ "verifyWordCountInTinyEditor,verifyCodeSampleIntinyEditor")
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
	    Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (true) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		// Verify tiny editor functionality
		logger.strongStep("Verify functionality of tiny editor componenet");
		log.info("Verify functionality of tiny editor componenet");
		String TEText = community.verifyTinyEditor(ui).trim();
		String ComText = ui.getCommunityText().trim();
		log.info("INFO: Text in  saved Community " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Insert Link functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save Sub community</li>
	 * <li></B>Verify Link is Inserted with Current Window and New Window functionality in Sub Community</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorInsertLink() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor").build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
	    Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (true) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		// Verify tiny editor functionality
		logger.strongStep("Verify functionality of tiny editor componenet");
		log.info("Verify functionality of tiny editor componenet");
		String TEText = community.verifyTinyEditor(ui).trim();
		String ComText = ui.getCommunityText().trim();
		log.info("INFO: Text in  saved Community " + ComText);
		log.info(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		ui.verifyInsertedLink(community);
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features.</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Step:</B>Select edit description and edit Sub community description</li>
	 * <li><B>Step:</B>Save the description</li>
	 * <li><B>Verify:</B>Verify edited description on Sub community description</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorEditDescriptionFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom " + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("").build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
	    Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (true) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("Create and Navigate to Sub Community");
		log.info("INFO: Create and Navigate to Sub Community");
		ui.verifyTinyEditor(community);
		
		// Verify tiny editor functionality
		logger.strongStep("Verify edit functionality of tiny editor componenet in community description widget");
		log.info("Verify edit functionality of tiny editor componenet");

		String EditedDescripton = community.getDescription().concat("Edited");

		String DescAfterEdit = ui.editDescriptionInTinyEditor(community, EditedDescripton);

		Assert.assertEquals(DescAfterEdit, EditedDescripton);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features.</li>
	 * <li><B>Step:</B>Created Images from API</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Insert Link to connections files from Files functionality in TinyEditor</li>*
	 * <li><B>Step:</B>Save community</li>
	 * <li></B>Verify link Images is added in Sub Community</li>
	 * <li></B>Verify link Images preview and image download in Sub Community</li>
	 * <li><B>Step:</B>Delete Image from API</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyLinkToConnectionsFilesFromFilesInTinyEditor() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand()).rename(testName + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE).build();

		Assert.assertNotNull(apiFileOwner);

		logger.strongStep("Upoad public image via API ");
		log.info("INFO: Upoad public image via API ");
		FileEntry imageFile = FileEvents.addFile(file, testUser, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyLinkToConnectionsFilesInTinyEditor").build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
	    Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (true) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		log.info("INFO: Navigate to Create Community Page and validate Link to Connections Files fucntionality");
		logger.strongStep("Navigate to Create Community Page and validate Link to Connections Files fucntionality");
		log.info(" image file name is " + file.getName());
		TinyEditorUI.setImageName(community.getDescription(), file.getName());

		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		community.verifyTinyEditor(ui).trim();

		log.info("INFO: Validate image download from Sub community");
		logger.strongStep("Validate image download from Sub community");
		TinyEditorUI tui=new TinyEditorUI(driver);
		tui.ImageDownload(CommunitiesUIConstants.communityDescription);
		
		fui.verifyFileDownloaded(file.getName());

		log.info("INFO: Validate image preview link from Sub community");
		logger.strongStep("Validate image preview link from Sub community");
		
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.communityDescription + " a:contains(View Details)").isDisplayed());
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(imageFile);

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate tiny editor features.</li>
	 * <li><B>Step:</B>Created Images from API</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Click on Create Sub Community</li>
	 * <li><B>Verify:</B>Verify Insert Link to connections files from This Community functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save community</li>
	 * <li></B>Verify link Images is added in Sub Community</li>
	 * <li></B>Verify link Images preview and image download in Sub Community</li>
	 * <li><B>Step:</B>Delete Image from API</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyLinkToConnectionsFilesFromThisCommunityInTinyEditor() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase Community Subcom" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyLinkToConnectionsFilesInTinyEditor").build();

		Community publicCommunity = community.createAPI(apiOwner);

		log.info("INFO: " + testUser.getDisplayName() + " creating a public file");
		BaseFile file = new BaseFile.Builder(Data.getData().file2).extension(".jpg").shareLevel(ShareLevel.EVERYONE)
				.rename(testName + "_" + Helper.genDateBasedRand()).tags(testName + Helper.genStrongRand()).build();
		Assert.assertNotNull(apiFileOwner);
		log.info("INFO: " + testUser.getDisplayName() + " sharing file with community using API method");
		FileEntry imageFile = FileEvents.addFile(file, testUser, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());

		log.info("INFO: Change permissions to public");
		apiFileOwner.changePermissions(file, imageFile);

		log.info("INFO: Share file with the community");
		apiFileOwner.shareFileWithCommunity(imageFile, publicCommunity, Role.OWNER);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		// logger.strongStep("INFO: Get UUID of community");
		community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, publicCommunity));

		// Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		log.info("INFO: Navigate to Create Community Page and validate Link to Connections Files fucntionality");
		logger.strongStep("Navigate to Create Community Page and validate Link to Connections Files fucntionality");

		log.info(" image file name is " + file.getName());
		TinyEditorUI.setImageName(community.getDescription(), file.getName());

		logger.strongStep("Verify link to connections files from files in tiny editor componenet");
		log.info("Verify link to connections files from files in tiny editor componenet");
		community.verifyTinyEditor(ui).trim();

		log.info("INFO: Validate image download from Sub community");
		logger.strongStep("Validate image download from Sub community");
		
		TinyEditorUI tui=new TinyEditorUI(driver);
		tui.ImageDownload(CommunitiesUIConstants.communityDescription);
		
		fui.verifyFileDownloaded(file.getName());

		log.info("INFO: Validate image preview link from Sub community");
		logger.strongStep("Validate image preview link from Sub community");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.communityDescription + " a:contains(View Details)").isDisplayed());

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(imageFile);

		ui.endTest();

	}

}
