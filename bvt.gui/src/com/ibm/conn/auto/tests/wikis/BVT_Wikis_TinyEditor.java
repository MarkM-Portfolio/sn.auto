package com.ibm.conn.auto.tests.wikis;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.WikisUI;

public class BVT_Wikis_TinyEditor extends  SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Wikis.class);
	private WikisUI ui;
	private TestConfigCustom cfg;
	private String serverURL;
	
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Create Wikis with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to Wikis dash-board page</li>
	 * <li><B>Step:</B>Click on 'New Page' Link from Left Panel</li>
	 *  * <li><B>Step:</B>Enter the Name of the Wikis New Page</li>
	 * <li><B>Verify:</B>Verify Paragraph and Header functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Right to Left Paragraph functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Alignment functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyIndentsInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyUploadImageFromDiskInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Wikis New Page Description</li>
	 * <li><B>Step:</B>Delete Wikis</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorParagraphFunctionalityInWikis() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("this is Test description for testcase " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create New Page in Wikis
		BaseWikiPage basewikiPage = new BaseWikiPage.Builder("NewPage_" + testName + Helper.genDateBasedRand(), PageType.NavPage)
				.description("this is a test description for creating a GUI and Browse Peer wiki page")
				.tinyEditorFunctionalitytoRun("verifyParaInTinyEditor,verifyRightLeftParagraphInTinyEditor,"
				+ "verifyAlignmentInTinyEditor,verifyIndentsInTinyEditor,verifyUploadImageFromDiskInTinyEditor")
				.build();
		
		String expectedValue=ui.verifyTinyEditorInWikis(basewikiPage,testUser);
		log.info("Validate After Page Creation");
		String actualValue=ui.getWikisEntryDescText();
		Assert.assertEquals(actualValue, expectedValue);		
		
		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
				
		//Logout of wiki
		ui.endTest();	
	}
	

	/**
	 * <ul>
	 * <li><B>Info:</B>Create Wikis with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to Wikis dash-board page</li>
	 * <li><B>Step:</B>Click on 'New Page' Link from Left Panel</li>
	 * <li><B>Step:</B>Enter the Name of the Wikis New Page</li>
	 * <li><B>Verify:</B>Verify Permanent Pen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font attributes functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font Size in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Font functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Other Text attributes and full screen functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Text Color functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Back Ground functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyInsertMediaInTinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Wikis New Page Description</li>
	 * <li><B>Step:</B>Delete Wikis</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorFontAttributeFunctionalityInWikis() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());	
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("this is Test description for testcase " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.waitForPageLoaded(driver);
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create New Page in Wikis
		BaseWikiPage basewikiPage = new BaseWikiPage.Builder("NewPage_" + testName + Helper.genDateBasedRand(), PageType.NavPage)
				.description("thisisTestdescriptionfortestcasePeerwikipage")
				.tinyEditorFunctionalitytoRun("verifyPermanentPenInTinyEditor,verifyAttributesInTinyEditor,"
						+ "verifyFontSizeInTinyEditor,verifyFontInTinyEditor,verifyOtherTextAttributesAndFullScreenInTinyEditor,"
						+ "verifyTextColorInTinyEditor,verifyBackGroundColorInTinyEditor,verifyInsertMediaInTinyEditor")
				.build();
		
		String expectedValue=ui.verifyTinyEditorInWikis(basewikiPage,testUser);
		log.info("Validate After Page Creation");
		String actualValue=ui.getWikisEntryDescText();
		Assert.assertEquals(actualValue, expectedValue);		
		
		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
				
		//Logout of wiki
		ui.endTest();
	}
	

	/**
	 * <ul>
	 * <li><B>Info:</B>Create Wikis with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to Wikis dash-board page</li>
	 * <li><B>Step:</B>Click on 'New Page' Link from Left Panel</li>
	 * <li><B>Step:</B>Enter the Name of the Wikis New Page</li>
	 * <li><B>Verify:</B>Verify Horizontal Line functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Rows and Columns, images, texts and nested table in Table of TinyEditor</li>
	 * <li><B>Verify:</B>Verify Bullets and Numbers functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyBlockQuoteInTinyEditor in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyLinkImageInTinyEditor in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyInsertiFrameInTinyEditor in TinyEditor</li>
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Wikis New Page Description</li>
	 * <li><B>Step:</B>Delete Wikis</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorLineBulletTableImageIFrameFunctionalityInWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("this is Test description for testcase " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create New Page in Wikis
		BaseWikiPage basewikiPage = new BaseWikiPage.Builder("NewPage_" + testName + Helper.genDateBasedRand(), PageType.NavPage)
				.description("this is Test description with url and Browse a Peer wiki page")
				.tinyEditorFunctionalitytoRun("verifyHorizontalLineInTinyEditor,verifyRowsCoulmnOfTableInTinyEditor,"
						+ "verifyBulletsAndNumbersInTinyEditor,verifyBlockQuoteInTinyEditor,verifyLinkImageInTinyEditor,verifyInsertiFrameInTinyEditor")
				.build();
		
		String expectedValue=ui.verifyTinyEditorInWikis(basewikiPage,testUser);
		log.info("Validate After Page Creation");
		String actualValue=ui.getWikisEntryDescText();
		Assert.assertEquals(actualValue, expectedValue);
		
		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
				
		//Logout of wiki
		ui.endTest();
	}
	

	/**
	 * <ul>
	 * <li><B>Info:</B>Create Wikis with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to Wikis dash-board page</li>
	 * <li><B>Step:</B>Click on 'New Page' Link from Left Panel</li>
	 * <li><B>Step:</B>Enter the Name of the Wikis New Page</li>
	 * <li><B>Verify:</B>Verify Find and Replace functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Special Character functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Link Image functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Spell check functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Undo Redo functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Emotions functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify Word Count functionality in TinyEditor</li>
	 * <li><B>Verify:</B>Verify verifyCodeSampleIntinyEditor functionality in TinyEditor</li>
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Wikis New Page Description</li>
	 * <li><B>Step:</B>Delete Wikis</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorFindReplaceSpellcheckUndoRedoSpecialCharFunctionalityInWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("this is Test description for testcase " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create New Page in Wikis
		BaseWikiPage basewikiPage = new BaseWikiPage.Builder("NewPage_" + testName + Helper.genDateBasedRand(), PageType.NavPage)
				.description("this is a test description for creating a Peer wiki page")
				.tinyEditorFunctionalitytoRun("verifyFindReplaceInTinyEditor,verifySpellCheckInTinyEditor,"
						+ "verifySpecialCharacterInTinyEditor,verifyUndoRedoInTinyEditor,verifyEmotionsInTinyEditor,"
						+"verifyWordCountInTinyEditor,verifyCodeSampleIntinyEditor")
				.build();
		
		String expectedValue=ui.verifyTinyEditorInWikis(basewikiPage,testUser);
		log.info("Validate After Page Creation");
		String actualValue=ui.getWikisEntryDescText();
		Assert.assertEquals(actualValue, expectedValue);		
		
		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
				
		//Logout of wiki
		ui.endTest();
		
	}
	

	/**
	 * <ul>
	 * <li><B>Info:</B>Create Wikis with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to Wikis dash-board page</li>
	 * <li><B>Step:</B>Click on 'New Page' Link from Left Panel</li>
	 * <li><B>Step:</B>Enter the Name of the Wikis New Page</li>
	 * <li><B>Verify:</B>Verify Insert Link functionality in TinyEditor</li>
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Wikis New Page Description</li>
	 * <li><B>Step:</B>Delete Wikis</li>
	 * </ul>
	 */
	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorInsertLinkInWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("this is Test description for testcase " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create New Page in Wikis
		BaseWikiPage basewikiPage = new BaseWikiPage.Builder("NewPage_" + testName + Helper.genDateBasedRand(), PageType.NavPage)
				.description("this is a test description for creating a Peer wiki page")
				.tinyEditorFunctionalitytoRun("verifyInsertLinkImageInTinyEditor")
				.build();
		
		String expectedValue=ui.verifyTinyEditorInWikis(basewikiPage,testUser);
		log.info("Validate After Page Creation");
		String actualValue=ui.getWikisEntryDescText();
		Assert.assertEquals(actualValue, expectedValue);		
		
		ui.verifyInsertedLink("CurrentWindow_"+basewikiPage.getName()+"~NewWindow_"+basewikiPage.getName());
		
		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
				
		//Logout of wiki
		ui.endTest();
	}
	

	/**
	 * <ul>
	 * <li><B>Info:</B>Create Wikis with API and validate tiny editor features</li>
	 * <li><B>Step:</B>Navigate to Wikis dash-board page</li>
	 * <li><B>Step:</B>Click on 'New Page' Link from Left Panel</li>
	 * <li><B>Step:</B>Enter the Name of the Wikis New Page</li>
	 * <li><B>Step:</B>Write description and edit the description</li>
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Description of Wikis</li>
	 * <li><B>Step:</B>Select Edit for the created Wiki </li>
	 * <li><B>Step:</B>Enter the Edit description message</li> 
	 * <li><B>Step:</B>Click 'Save and Close' button in Wikis New Page</li>
	 * <li><B>Verify:</B>Verify tiny Editor text on Wikis New Page Description</li>
	 * <li><B>Step:</B>Delete Wikis</li>
	 * </ul>
	 */

	@Test(groups = { "TinyEditor" })
	public void verifyTinyEditorEditFunctionalityInWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		//Allocate user
		User testUser = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APIWikisHandler apiOwner = new APIWikisHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
									.tags("tag" + Helper.genDateBasedRand())
									.description("this is Test description for testcase " + testName)
									.build();

		logger.strongStep("Create a new Wiki using API");
		log.info("INFO: Create a new Wiki using API");
		wiki.createAPI(apiOwner);
				
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUser);

		//view the wiki
		logger.strongStep("View the Wiki");
		log.info("INFO: View the Wiki");
		Wiki_LeftNav_Menu.OWNER.select(ui);
		
		//Open Wiki created above
		logger.strongStep("Open the Wiki created via API");
		log.info("INFO: Open the Wiki created via API");
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Wait for the Wiki page header");
		log.info("INFO: Waiting for the Wiki page header");
		ui.waitForPageLoaded(driver);
		ui.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

		//Create New Page in Wikis
		BaseWikiPage basewikiPage = new BaseWikiPage.Builder("NewPage_" + testName + Helper.genDateBasedRand(), PageType.NavPage)
				.description("this is a test description for creating a Peer wiki page")
				.tinyEditorFunctionalitytoRun("verifyEditDescriptionInTinyEditor")
				.build();
		
		String expectedValue=ui.verifyTinyEditorInWikis(basewikiPage,testUser).trim();
		log.info("Validate After Page Creation");
		String actualValue=ui.getWikisEntryDescText().trim();
		
		//Assert.assertEquals(actualValue, expectedValue);
		Assert.assertTrue(actualValue.contains(expectedValue));
		String ediDesc = "Edit this is a test description for creating a Peer wiki page";
		ui.editDescriptionInTinyEditor(basewikiPage, ediDesc);
		
		//delete wiki
		logger.strongStep("Delete the Wiki");
		log.info("INFO: Delete the Wiki.");
		wiki.delete(ui, testUser);
				
		//Logout of wiki
		ui.endTest();	
	}
}