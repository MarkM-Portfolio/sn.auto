package com.ibm.conn.auto.tests.wikis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.webui.PdfExportUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_ExportToPdf_Wikis extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_ExportToPdf_Wikis.class);
	private WikisUI ui;
	private PdfExportUI pUi;
	private TestConfigCustom cfg;
	private APIWikisHandler apiOwner;
	private User testUser1;
	private String serverURL;
	private List<Wiki> testWikis = new ArrayList<Wiki>();
	private APIWikisHandler wikiOwner;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
		pUi = PdfExportUI.getGui(cfg.getProductName(), driver);
		wikiOwner = new APIWikisHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Acceptance test to verify export a wiki as pdf.</li>
	*<li><B>Step:</B>(API) Create a wiki as UserA.</li>
	*<li><B>Step:</B>Go to the wiki and click the Export as PDF button.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>PDF sidebar contains entries for wiki name, TOC and welcome page name</li>
	*<li><B>Verify:</B>PDF content contains wiki name, TOC and welcome page name</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "UnitOnAnsible", "PdfExport", "cnx8ui-level2" })
	public void smokeTestExportWikiToPdf() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand())
				.description("Description for test " + testName)
				.build();

		logger.strongStep("Create an Wiki (API)");
		log.info("INFO: Create an activity (API) as " + testUser1.getDisplayName());
		Wiki testWiki = wiki.createAPI(apiOwner);
		testWikis.add(testWiki);
		
		logger.strongStep("Load Wikis and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the wiki");
		log.info("INFO: Open the wiki " + testWiki.getTitle());
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		// call common smoketest method
		pUi.smokeTest(pUi, logger, testWiki.getTitle(), "Welcome to " + testWiki.getTitle(), true);
			
		pUi.endTest();	
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		for (Wiki wiki : testWikis)  {
			apiOwner.deleteWiki(wiki);
		}
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a wiki as pdf.</li>
	*<li><B>Step:</B>Create an Wiki (API)</li>
	*<li><B>Step:</B>Load Wikis and Log In</li>
	*<li><B>Step:</B>Open the wiki</li>
	*<li><B>Step:</B>Create multiple Peer Pages in the Wiki</li>
	*<li><B>Step:</B>List of the titles for validation</li>
	*<li><B>Verify:</B>PDF content validation based on the selected entries</li>
	*</ul>
	 */

	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void exportPdfContentValidation() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		pUi.startTest();

		BaseWiki wiki = new BaseWiki.Builder("Wiki1" + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + "Wiki1").build();
		
		BaseWikiPage peerPage2 = new BaseWikiPage.Builder("Peer2" + Helper.genDateBasedRand(), PageType.Peer)
				.tags("tag1").description("this is a test description for creating a Peer 2 wiki page").build();
		
		BaseWikiPage peerPage3 = new BaseWikiPage.Builder("Peer3" + Helper.genDateBasedRand(), PageType.Peer)
				.tags("tag1").description("this is a test description for creating a Peer 3 wiki page").build();

		logger.strongStep("Create an Wiki (API)");
		log.info("INFO: Create an activity (API) as " + testUser1.getDisplayName());
		Wiki testWiki = wiki.createAPI(apiOwner);
		testWikis.add(testWiki);
		
		logger.strongStep("Load Wikis and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the wiki");
		log.info("INFO: Open the wiki " + testWiki.getTitle());
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		// create a peerpage
		logger.strongStep("Create multiple Peer Pages in the Wiki");
		log.info("INFO: Create multiple Peer Pages in the Wiki");
		peerPage2.create(ui);
		peerPage3.create(ui);
		
		logger.strongStep("List of the titles for validation");
		log.info("INFO: List of the titles for validation");
		String Title1 = "Welcome to " + testWiki.getTitle();
		log.info("Entry 1:" +Title1);
		String Title2 = peerPage2.getName();
		log.info("Entry 2:" +Title2);
		String Title3 = peerPage3.getName();
		log.info("Entry 3:" +Title3);

		logger.strongStep("PDF content validation based on the selected entries");
		log.info("INFO: PDF content validation based on the selected entries");
		pUi.contentSelectionforWiki(pUi, logger, testWiki.getTitle(), "Welcome to " + testWiki.getTitle(), true , Title1, Title2, Title3);
				
		pUi.endTest();	
	}
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a Wikis as pdf.</li>
	*<li><B>Step:</B>Create an Wiki (API)</li>
	*<li><B>Step:</B>Load Wikis and Log In</li>
	*<li><B>Step:</B>Open the wiki</li>
	*<li><B>Step:</B>Create a Peer Pages via API in the Wiki</li>
	*<li><B>Step:</B>Go to the added peer page, add some comments and click the Export as PDF button.</li>
	*<li><B>Verify:</B>Export PDF Window contains All Options in 'Information included Section'.</li>
	*<li><B>Step:</B>Select all Options from 'Information included Section' List of Export PDF Window.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>All Options Selected above are listed in the Export PDF Preview.</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void infIncludedExportPdf() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		
		HashMap<String,String> Informationmap = new HashMap<String,String>();
		
		BaseWiki wiki = new BaseWiki.Builder("Wiki1" + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + "Wikis").build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer2" + Helper.genDateBasedRand(), PageType.Peer)
				.tags("tag1").description("this is a test description for creating a wikis peer page").build();
		
		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser1.getDisplayName());
		Wiki testWiki = wiki.createAPI(apiOwner);
		testWikis.add(testWiki);
		
		logger.strongStep("Load Wikis and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the wiki");
		log.info("INFO: Open the wiki " + testWiki.getTitle());
		ui.clickLinkWait(WikisUI.getWiki(wiki));

		// create a peerpage
		logger.strongStep("Create a Peer Page in the Wiki");
		log.info("INFO: Create a Peer Page in the Wiki");
		wikiOwner.createWikiPage(peerPage, testWiki);
		
		//Add a comment and verify that the comment is added
		logger.strongStep("Add a comment");
		log.info("INFO: Add a comment");
		ui.addComment(Data.getData().Comment_For_Public_Wiki);
		
		logger.strongStep("List of the titles for validation");
		log.info("INFO: List of the titles for validation");
		String Title1 = "Welcome to " + testWiki.getTitle();
		log.info("Wikis Name:" +Title1);
		String Title2 = peerPage.getName();
		log.info("Wiki's Page Name:" +Title2);

		String userid = pUi.getFirstVisibleElement("xpath=//a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		logger.strongStep("Open the blog entry");
		String BlogsComment = Data.getData().Comment_For_Public_Wiki;
		
		Informationmap.put("author", userid);
		Informationmap.put("childcomment", BlogsComment);
		Informationmap.put("parentdesc", wiki.getDescription());
		Informationmap.put("childdesc", peerPage.getDescription());
		Informationmap.put("parenttags", wiki.getTags().toLowerCase());
		Informationmap.put("childtags", peerPage.getTags().toLowerCase());
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		String ValidationList = "titlepage:tableofcontent:comments:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, Informationmap, 
				Title1, Title2, ValidationList);
		
		pUi.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a Wikis as pdf.</li>
	*<li><B>Step:</B>Create an Wiki (API)</li>
	*<li><B>Step:</B>Load Wikis and Log In</li>
	*<li><B>Step:</B>Open the wiki</li>
	*<li><B>Step:</B>Move the default Wiki page to Trash</li>
	*<li><B>Step:</B>Validate that 'PDF' icon/button is not displayed</li>
	*<li><B>Step:</B>Navigate back to 'Wikis' main page</li>
	*<li><B>Step:</B>Create a new Wiki</li>
	*<li><B>Step:</B>Create a Peer Page in the Wiki</li>
	*<li><B>Verify:</B>Validate the presence of 'PDF' icon/button</li>
	*<li><B>Step:</B>Add a comment</li>
	*<li><B>Step:</B>List of the titles for validation</li>
	*<li><B>Verify:</B>Validate generate pdf and pdf contents</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible", "cnx8ui-level2" })
	public void validateContentBeforeGeneatePdfWiki() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		
		HashMap<String,String> Informationmap = new HashMap<String,String>();
		
		BaseWiki wiki = new BaseWiki.Builder("Wiki1" + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + "Wikis").build();
		
		BaseWiki newWiki = new BaseWiki.Builder("newWiki" + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + "Wikis").build();
		
		BaseWikiPage peerPage = new BaseWikiPage.Builder("Peer2" + Helper.genDateBasedRand(), PageType.Peer)
				.tags("tag1").description("this is a test description for creating a wikis peer page").build();
		
		logger.strongStep("Create a Blog (API)");
		log.info("INFO: Create a Blog (API) as " + testUser1.getDisplayName());
		Wiki testWiki = wiki.createAPI(apiOwner);
		testWikis.add(testWiki);
		
		logger.strongStep("Load Wikis and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Open the wiki");
		log.info("INFO: Open the wiki " + testWiki.getTitle());
		ui.clickLinkWait(WikisUI.getWiki(wiki));
		
		logger.strongStep("Move the default Wiki page to Trash");
		log.info("INFO: Move the default Wiki page to Trash");
		ui.clickLinkWait(WikisUIConstants.Page_Actions_Button);
		ui.clickLinkWait(WikisUIConstants.Menu_Move_To_Trash);
		ui.clickLinkWait(WikisUIConstants.Dialog_OK_Button);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that 'PDF' icon/button is not displayed");
		log.info("INFO: Validate that 'PDF' icon/button is not displayed");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is not Expected but still present.");
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Navigate back to 'Wikis' main page");
		log.info("INFO: Navigate back to 'Wikis' main page");
		ui.clickLink(WikisUIConstants.wikisHeadlineLink);

		logger.strongStep("Create a new Wiki");
		log.info("INFO: Create a new Wiki");
		newWiki.create(ui);

		logger.strongStep("Create a Peer Page in the Wiki");
		log.info("INFO: Create a Peer Page in the Wiki");
		peerPage.create(ui);
		
		logger.strongStep("Validate the persence of 'PDF' icon/button");
		log.info("INFO: Validate the persence of 'PDF' icon/button");
		driver.turnOffImplicitWaits();
		Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is Expected but still Not present.");
		driver.turnOnImplicitWaits();
		
		//Add a comment and verify that the comment is added
		logger.strongStep("Add a comment");
		log.info("INFO: Add a comment");
		ui.addComment(Data.getData().Comment_For_Public_Wiki);
		
		logger.strongStep("List of the titles for validation");
		log.info("INFO: List of the titles for validation");
		String Title1 = "Welcome to " + newWiki.getName();
		log.info("Wikis Name:" +Title1);
		String Title2 = peerPage.getName();
		log.info("Wiki's Page Name:" +Title2);

		String userid = pUi.getFirstVisibleElement("xpath=//a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		logger.strongStep("Open the entry");
		String WikisComment = Data.getData().Comment_For_Public_Wiki;
		
		Informationmap.put("author", userid);
		Informationmap.put("childcomment", WikisComment);
		Informationmap.put("parentdesc", newWiki.getDescription());
		Informationmap.put("childdesc", peerPage.getDescription());
		Informationmap.put("parenttags", newWiki.getTags().toLowerCase());
		Informationmap.put("childtags", peerPage.getTags().toLowerCase());
		
		logger.strongStep("Validate generate pdf and pdf contents");
		log.info("INFO: Validate generate pdf and pdf contents");
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		String ValidationList = "titlepage:tableofcontent:comments:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, Informationmap, 
				Title1, Title2, ValidationList);
		
		pUi.endTest();	
	}

}
