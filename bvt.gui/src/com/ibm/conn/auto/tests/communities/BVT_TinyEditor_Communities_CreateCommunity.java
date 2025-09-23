package com.ibm.conn.auto.tests.communities;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_TinyEditor_Communities_CreateCommunity extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_TinyEditor_Communities_CreateCommunity.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser, testLookAheadUser;
	private Member member;
	private String serverURL;
	private APICommunitiesHandler apiOwner;

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

		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Navigate to create community and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to create community page</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on community Description widget</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorParagraphFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyIndentsInTinyEditor,verifyParaInTinyEditor,verifyRightLeftParagraphInTinyEditor,verifyAlignmentInTinyEditor")
				.build();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to Create Community Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Navigate to Create Community Page and validate Tiny Editor fucntionality.");
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
	 * <li><B>Info:</B>Navigate to create community and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to create community page</li>
	 * <li><B>Verify:</B>Verify Permanent Pen functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Font attributes functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font Size in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Other Text attributes and full screen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Text Color functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on community Description widget</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorFontAttributeFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("thisisTestdescriptionfortestcase" + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
                        + "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
                        + "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor")
				.build();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to Create Community Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Navigate to Create Community Page and validate Tiny Editor fucntionality.");
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
	 * <li><B>Info:</B>Navigate to create community and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to create community page</li>
	 * <li><B>Verify:</B>Verify Horizontal Line functionality in TinyEditor
	 * <li><B>Verify:</B>Verify Bullets and Numbers functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Rows and Columns, images, texts and nested table in  Table of  TinyEditor</li>
	 * <li><B>Step:</B>Save community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on community Description widget</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorLineBulletTableFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyBulletsAndNumbersInTinyEditor,"
						+ "verifyRowsCoulmnOfTableInTinyEditor,verifyBlockQuoteInTinyEditor")
				.build();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to Create Community Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Navigate to Create Community Page and validate Tiny Editor fucntionality.");
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
	 * <li><B>Info:</B>Navigate to create community and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to create community page</li>
	 * <li><B>Verify:</B>Verify Find and Replace functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special Character functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Link Image functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special check functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Undo Redo functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Emotions functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Word Count functionality in TinyEditor</li>
	 * <li><B>Step:</B>Save community</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on community Description widget</li>
	 * <li><B>Step:</B>Delete community</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyCreateCommunityTinyEditorFindReplaceSpellcheckUndoRedoSpecialCharLinkImageFunctionality() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("this is Test description for testcase " + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,verifyUndoRedoInTinyEditor,"
						+ "verifySpecialCharacterInTinyEditor,verifyLinkImageInTinyEditor,verifyEmotionsInTinyEditor,"
						+ "verifyWordCountInTinyEditor,verifyCodeSampleIntinyEditor")
				.build();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to Create Community Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Navigate to Create Community Page and validate Tiny Editor fucntionality.");
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
	 * <li><B>Info:</B>Navigate to create community and validate tiny editor
	 * features</li>
	 * <li><B>Step:</B>Navigate to create community page</li>
	 * <li><B>Verify:</B>Verify Insert Link functionality in TinyEditor</li>* 
	 * <li><B>Step:</B>Save community</li>
	 * <li></B>Verify Link is Inserted with Current Window and New Window functionality in Community</li>
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
				.description("this is Test description for testcase " + testName).addMember(member)
				.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor")
				.build();

		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		log.info("INFO: Navigate to Create Community Page and validate Tiny Editor fucntionality.");
		logger.strongStep("Navigate to Create Community Page and validate Tiny Editor fucntionality.");
		community.verifyTinyEditor(ui).trim();
		
		ui.verifyInsertedLink(community);
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Create community with API and validate edit description community description widget</li>
	 * <li><B>Step:</B>Navigate to community dash-board page</li>
	 * <li><B>Step:</B>Select edit description and edit community description</li>
	 * <li><B>Step:</B>Save the description</li>
	 * <li><B>Verify:</B>Verify edited description on community description widget
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
				.description("this is Test description for testcase " + testName).addMember(member)
				.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
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
		logger.strongStep("Verify edit functionality of tiny editor componenet in community description widget");
		log.info("Verify edit functionality of tiny editor componenet");
		
		String EditedDescripton = community.getDescription().concat("Edited");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitElementVisible(CommunitiesUIConstants.communityDescription);
		String DescAfterEdit = ui.editDescriptionInTinyEditor(community,EditedDescripton);
		
		Assert.assertEquals(DescAfterEdit, EditedDescripton);
		
		log.info("INFO: Delete the Community.");
		logger.strongStep("Delete the Community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();

	}
		
	
}
