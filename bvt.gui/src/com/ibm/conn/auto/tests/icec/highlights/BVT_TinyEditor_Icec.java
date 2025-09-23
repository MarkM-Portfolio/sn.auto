package com.ibm.conn.auto.tests.icec.highlights;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseHighlights;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.communities.BVT_TinyEditor_Communities_RichContent;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.IcecUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_TinyEditor_Icec extends SetUpMethods2 {
	

	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_Communities_RichContent.class);
	private CommunitiesUI commUI;
	private FilesUI fui;
	private IcecUI ui;
	private TestConfigCustom cfg;
	private User testUser, testLookAheadUser;
	private Member member;
	private String serverURL;
	private APICommunitiesHandler apiOwner;
	private APIFileHandler apiFileOwner;
	String appName = "Highlights";


	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();
		cfg.getUserAllocator().getUser();
		member = new Member(CommunityRole.MEMBERS, testLookAheadUser);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui = IcecUI.getGui(cfg.getProductName(), driver);
		fui = FilesUI.getGui(cfg.getProductName(), driver);
		commUI.addOnLoginScript(commUI.getCloseTourScript());
	}

	/**
	 *
	 * <ul>
	 * <li><B>Info:</B> Test Highlights Rich Content widget Description</li>
	 * <li><B>Step:</B> Login to application</li>
	 * <li><B>Step:</B> Create community and navigate to it</li>
	 * <li><B>Step:</B> Add Rich Content to Created Community</li>
	 * <li><B>Step:</B> Add Highlights widget</li>
	 * <li><B>Step:</B> Navigate to Highlights</li>
	 * <li><B>Step:</B> Add Rich Content Widget and Changes Setting of it to allow edit of Rich content</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save Highlight Rich Content widget</li>
	 * <li><B>Verify:</B> Check widget is created successfully</li>
	 * <li><B>Verify:</B>Verify that text is same in Highlight Rich Content widget and Rich content in Overview section</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 * 
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyHighlightsTinyEditorParagraphFunctionality() throws InterruptedException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String richcontentDefaultText = "rich content";

		String testName = commUI.startTest();
		String communityName = "Rich Content";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member).build();
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
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Add Tiny Editor Text in Rich Content");
		logger.strongStep("Add Tiny Editor Text in Rich Content ");
		commUI.addRichContent(richcontentDefaultText);

		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
        if (!flag) {
            if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty())                 
                community.addWidget(commUI, BaseWidget.HIGHLIGHTS);


            logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
            log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
            Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
        }
        
		BaseHighlights highlights = new BaseHighlights.Builder(testName + Helper.genDateBasedRandVal())
				.description("this is test case description")
				.tinyEditorFunctionalitytoRun("verifyParaInTinyEditor,verifyRightLeftParagraphInTinyEditor,"
						+ "verifyAlignmentInTinyEditor,verifyIndentsInTinyEditor")
				.build();

		log.info("INFO:Validate Tiny Editor fucntionality.");
		logger.strongStep("Validate Tiny Editor fucntionality.");
		String TEText = highlights.verifyTinyEditor(ui).trim();
		ui.clickLink(IcecUI.richContentCloseMsg);
		log.info("INFO: Validate Tiny Editor text with Highlights Description");
		logger.strongStep("Validate Tiny Editor text with Highlights Description");
		String highlightRCText = ui.getHighlightsRichContentText(communityName).trim();
		Assert.assertEquals(TEText, highlightRCText);

		log.info("INFO: Navigate to Community Overview Page and Get text from Rich content");
		logger.strongStep("Navigate to Community Overview Page and Get text from Rich content");
		driver.executeScript("scroll(0,-250);");
		String ComText = commUI.getRichContentText().trim();

		log.info("INFO: Validate Rich contents text  gets updated with Highlights text");
		logger.strongStep(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		commUI.endTest();

	}

	/**
	 *
	 * <ul>
	 * <li><B>Info:</B> Test Highlights Rich Content widget Description</li>
	 * <li><B>Step:</B> Login to application</li>
	 * <li><B>Step:</B> Create community and navigate to it</li>
	 * <li><B>Step:</B> Add Rich Content to Created Community</li>
	 * <li><B>Step:</B> Add Highlights widget</li>
	 * <li><B>Step:</B> Navigate to Highlights</li>
	 * <li><B>Step:</B> Add Rich Content Widget and Changes Setting of it to allow edit of Rich content</li>
	 * <li><B>Verify:</B>Verify Permanent Pen functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Font attributes functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font Size in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Other Text attributes and full screen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Text Color functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Insert Media in TinyEditor</li>
	 * <li><B>Step:</B>Save Highlight Rich Content widget</li>
	 * <li><B>Verify:</B> Check widget is created successfully</li>
	 * <li><B>Verify:</B>Verify that text is same in Highlight Rich Content widget and Rich content in Overview section</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 * 
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyHighlightstTinyEditorFontAttributeFunctionality() throws InterruptedException {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String richcontentDefaultText = "rich content";

		String testName = commUI.startTest();
		String communityName = "Rich Content";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member).build();
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
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Add Tiny Editor Text in Rich Content");
		logger.strongStep("Add Tiny Editor Text in Rich Content ");
		commUI.addRichContent(richcontentDefaultText);
		
		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
        if (!flag) {
            if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty())                 
                community.addWidget(commUI, BaseWidget.HIGHLIGHTS);


            logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
            log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
            Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
        }
        
		BaseHighlights highlights = new BaseHighlights.Builder(testName + Helper.genDateBasedRandVal())
				.description("thisisTestdescriptionfortestcasehighlights")
				.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
						+ "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
						+ "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor,verifyInsertMediaInTinyEditor")
				.build();

		log.info("INFO:Validate Tiny Editor fucntionality.");
		logger.strongStep("Validate Tiny Editor fucntionality.");
		String TEText = highlights.verifyTinyEditor(ui).trim();
		ui.clickLink(IcecUI.richContentCloseMsg);
		log.info("INFO: Validate Tiny Editor text with Highlights Description");
		logger.strongStep("Validate Tiny Editor text with Highlights Description");
		String highlightRCText = ui.getHighlightsRichContentText(communityName).trim();
		Assert.assertEquals(TEText, highlightRCText);

		log.info("INFO: Navigate to Community Overview Page and Get text from Rich content");
		logger.strongStep("Navigate to Community Overview Page and Get text from Rich content");
		driver.executeScript("scroll(0,-250);");
		String ComText = commUI.getRichContentText().trim();

		log.info("INFO: Validate Rich contents text  gets updated with Highlights text");
		logger.strongStep(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		commUI.endTest();

	}


	/**
	 *
	 * <ul>
	 * <li><B>Info:</B> Test Highlights Rich Content widget Description</li>
	 * <li><B>Step:</B> Login to application</li>
	 * <li><B>Step:</B> Create community and navigate to it</li>
	 * <li><B>Step:</B> Add Rich Content to Created Community</li>
	 * <li><B>Step:</B> Add Highlights widget</li>
	 * <li><B>Step:</B> Navigate to Highlights</li>
	 * <li><B>Step:</B> Add Rich Content Widget and Changes Setting of it to allow edit of Rich content</li>
	 * <li><B>Verify:</B>Verify Horizontal Line functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Rows and Columns functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Rows and Columns, images, texts and nested table in  Table of  TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyBlockQuoteInTinyEditor in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyLinkImageInTinyEditor in TinyEditor</li>
	 * <li><B>Verify:</B>Verify uverifyInsertiFrameInTinyEditor in TinyEditor</li>
	 * <li><B>Step:</B>Save Highlight Rich Content widget</li>
	 * <li><B>Verify:</B> Check widget is created successfully</li>
	 * <li><B>Verify:</B>Verify that text is same in Highlight Rich Content widget and Rich content in Overview section</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 * 
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyHighlightsTinyEditorLineBulletTableImageIFrameFunctionality() throws InterruptedException {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String richcontentDefaultText = "rich content";

		String testName = commUI.startTest();
		String communityName = "Rich Content";

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test Browse description for testcase " + testName).addMember(member).build();
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
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Add Tiny Editor Text in Rich Content");
		logger.strongStep("Add Tiny Editor Text in Rich Content ");
		commUI.addRichContent(richcontentDefaultText);


		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
        if (!flag) {
            if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty())                 
                community.addWidget(commUI, BaseWidget.HIGHLIGHTS);


            logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
            log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
            Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
        }
        
		BaseHighlights highlights = new BaseHighlights.Builder(testName + Helper.genDateBasedRandVal())
				.description("this is Browse url test case description")
				.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyRowsCoulmnOfTableInTinyEditor,"
				+ "verifyBulletsAndNumbersInTinyEditor,verifyBlockQuoteInTinyEditor,"
				+ "verifyLinkImageInTinyEditor,verifyInsertiFrameInTinyEditor")
				.build();

		log.info("INFO:Validate Tiny Editor fucntionality.");
		logger.strongStep("Validate Tiny Editor fucntionality.");
		String TEText = highlights.verifyTinyEditor(ui).trim();
		ui.clickLink(IcecUI.richContentCloseMsg);
		log.info("INFO: Validate Tiny Editor text with Highlights Description");
		logger.strongStep("Validate Tiny Editor text with Highlights Description");
		String highlightRCText = ui.getHighlightsRichContentText(communityName).trim();
		Assert.assertEquals(TEText, highlightRCText);

		log.info("INFO: Navigate to Community Overview Page and Get text from Rich content");
		logger.strongStep("Navigate to Community Overview Page and Get text from Rich content");
		driver.executeScript("scroll(0,-250);");
		String ComText = commUI.getRichContentText().trim();

		log.info("INFO: Validate Rich contents text  gets updated with Highlights text");
		logger.strongStep(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		commUI.endTest();

	}

	 /**
	 *
	 * <ul>
	 * <li><B>Info:</B> Test Highlights Rich Content widget Description</li>
	 * <li><B>Step:</B> Login to application</li>
	 * <li><B>Step:</B> Create community and navigate to it</li>
	 * <li><B>Step:</B> Add Rich Content to Created Community</li>
	 * <li><B>Step:</B> Add Highlights widget</li>
	 * <li><B>Step:</B> Navigate to Highlights</li>
	 * <li><B>Step:</B> Add Rich Content Widget and Changes Setting of it to allow edit of Rich content</li>
	 * <li><B>Verify:</B>Verify Find and Replace functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special Character functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Link Image functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special check functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Undo Redo functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Emotions functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Word Count functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyCodeSampleIntinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save Highlight Rich Content widget</li>
	 * <li><B>Verify:</B> Check widget is created successfully</li>
	 * <li><B>Verify:</B>Verify that text is same in Highlight Rich Content widget and Rich content in Overview section</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 * 
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyHighlightsTinyEditorFindReplaceUndoRedoSpecialCharFunctionality()
			throws InterruptedException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String richcontentDefaultText = "rich content";

		String testName = commUI.startTest();
		String communityName = "Rich Content";

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member).build();
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
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Add Tiny Editor Text in Rich Content");
		logger.strongStep("Add Tiny Editor Text in Rich Content ");
		commUI.addRichContent(richcontentDefaultText);


		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
        if (!flag) {
            if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty())                 
                community.addWidget(commUI, BaseWidget.HIGHLIGHTS);


            logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
            log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
            Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
        }
        
		BaseHighlights highlights = new BaseHighlights.Builder(testName + Helper.genDateBasedRandVal())
				.description("this is test case description")
				.tinyEditorFunctionalitytoRun("verifyFindReplaceInTinyEditor,"
						+ "verifySpecialCharacterInTinyEditor,verifyWordCountInTinyEditor,"
						+ "verifyUndoRedoInTinyEditor,verifyCodeSampleIntinyEditor")
				.build();

		log.info("INFO:Validate Tiny Editor fucntionality.");
		logger.strongStep("Validate Tiny Editor fucntionality.");
		String TEText = highlights.verifyTinyEditor(ui).trim();

		ui.clickLink(IcecUI.richContentCloseMsg);
		log.info("INFO: Validate Tiny Editor text with Highlights Description");
		logger.strongStep("Validate Tiny Editor text with Highlights Description");
		String highlightRCText = ui.getHighlightsRichContentText(communityName).trim();
		Assert.assertEquals(TEText, highlightRCText);

		log.info("INFO: Navigate to Community Overview Page and Get text from Rich content");
		logger.strongStep("Navigate to Community Overview Page and Get text from Rich content");
		driver.executeScript("scroll(0,-250);");
		String ComText = commUI.getRichContentText().trim();

		log.info("INFO: Validate Rich contents text  gets updated with Highlights text");
		logger.strongStep(TEText + " : " + ComText);
		Assert.assertEquals(TEText, ComText);

		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		commUI.endTest();

	}
	
	/**
	 *
	 * <ul>
	 * <li><B>Info:</B> Test Highlights Rich Content widget Description</li>
	 * <li><B>Step:</B> Login to application</li>
	 * <li><B>Step:</B> Create community and navigate to it</li>
	 * <li><B>Step:</B> Add Rich Content to Created Community</li>
	 * <li><B>Step:</B> Add Highlights widget</li>
	 * <li><B>Step:</B> Navigate to Highlights</li>
	 * <li><B>Step:</B> Add Rich Content Widget and Changes Setting of it to allow edit of Rich content</li>
	 * <li><B>Step:</B>Insert Link for same window and new window in TinyEditor</li>* 
	 * <li><B>Step:</B>Save Highlight Rich Content widget</li>
	 * <li><B>Verify:</B> Check widget is created successfully</li>
	 * <li><B>Verify:</B>Verify Insert Link functionality for same window and new window in TinyEditor description</li>* 
	 * <li><B>Verify:</B>Verify that text is same in Highlight Rich Content widget and Rich content in Overview section</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 * 
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyHighlightsTinyEditorInsertLink() throws InterruptedException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String richcontentDefaultText = "rich content";

		String testName = commUI.startTest();
		String communityName = "Rich Content";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member).build();
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
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);
		
		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Add Tiny Editor Text in Rich Content");
		logger.strongStep("Add Tiny Editor Text in Rich Content ");
		commUI.addRichContent(richcontentDefaultText);

		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
        if (!flag) {
            if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty())                 
                community.addWidget(commUI, BaseWidget.HIGHLIGHTS);


            logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
            log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
            Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
        }
        
		BaseHighlights highlights = new BaseHighlights.Builder(testName + Helper.genDateBasedRandVal())
				.description("this is test case description")
				.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor")
				.build();

		log.info("INFO:Validate Tiny Editor fucntionality.");
		logger.strongStep("Validate Tiny Editor fucntionality.");
		highlights.verifyTinyEditor(ui).trim();
		ui.clickLink(IcecUI.richContentCloseMsg);
		ui.verifyInsertedLink(highlights,communityName);
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		commUI.endTest();
	}
	
	/**
	 *
	 * <ul>
	 * <li><B>Info:</B> Test Highlights Rich Content widget Description</li>
	 * <li><B>Step:</B> Login to application</li>
	 * <li><B>Step:</B> Create community and navigate to it</li>
	 * <li><B>Step:</B> Add Rich Content to Created Community</li>
	 * <li><B>Step:</B> Add Highlights widget</li>
	 * <li><B>Step:</B> Navigate to Highlights</li>
	 * <li><B>Step:</B> Add Rich Content Widget and Changes Setting of it to allow edit of Rich content</li>
	 * <li><B>Step:</B>Add default text in Highlight Rich Content TinyEditor</li>* 
	 * <li><B>Step:</B>Save Highlight Rich Content widget</li>
	 * <li><B>Verify:</B> Check widget is created successfully</li>
	 * <li><B>Verify:</B>Edit Rich content Description And Verify Edited Text Saved in TinyEditor description</li>* 
	 * <li><B>Verify:</B>Verify that text is same in Highlight Rich Content widget and Rich content in Overview section</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 * 
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyHighlightsTinyEditorEditFunctionality() throws InterruptedException {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String richcontentDefaultText = "rich content";

		String testName = commUI.startTest();
		String communityName = "Rich Content";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member).build();
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
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Add Tiny Editor Text in Rich Content");
		logger.strongStep("Add Tiny Editor Text in Rich Content ");
		commUI.addRichContent(richcontentDefaultText);
		
		Boolean flag = commUI.isHighlightDefaultCommunityLandingPage();
        if (!flag) {
            if (apiOwner.getWidgetID(comAPI.getUuid(), "Highlights").isEmpty())                 
                community.addWidget(commUI, BaseWidget.HIGHLIGHTS);


            logger.strongStep("Navigate to the Highlights page as it is not the default landing page");
            log.info("INFO: Navigate to the Highlights page as it is not the default landing page");
            Community_TabbedNav_Menu.HIGHLIGHTS.select(ui);
        }
		
        log.info("INFO: Edit Rich Content Widgets etting to add Tiny Editor Text");
		logger.strongStep("Edit Rich Content Widgets etting to add Tiny Editor Text");
		
		BaseHighlights highlights = new BaseHighlights.Builder(testName + Helper.genDateBasedRandVal())
				.tinyEditorFunctionalitytoRun("verifyEditDescriptionInTinyEditor")
				.description("this is test case description").build();

		log.info("INFO:Validate Tiny Editor fucntionality.");
		logger.strongStep("Validate Tiny Editor fucntionality.");
		String TEText = highlights.verifyTinyEditor(ui).trim();
		
		log.info("INFO: Validate Tiny Editor text with Highlights Edited Description");
		logger.strongStep("Validate Tiny Editor text with Highlights Edited Description");
		String editText = highlights.getDescription()+" concat";
		String value = ui.editDescriptionInTinyEditor(highlights, editText,communityName);
		Assert.assertEquals(value, editText);

		log.info("INFO: Get text from Rich content");
		logger.strongStep("Get text from Rich content");
		driver.executeScript("scroll(0,-250);");
		String ComText = commUI.getRichContentText().trim();

		log.info("INFO: Validate Rich contents text  gets updated with Highlights edited text");
		logger.strongStep("Validate Rich contents text  gets updated with Highlights edited text");
		logger.strongStep(TEText + " : " + ComText);
		Assert.assertEquals(ComText, editText);

		log.info("INFO: Delete the Community");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		commUI.endTest();

	}

}
