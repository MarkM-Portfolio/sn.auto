package com.ibm.conn.auto.tests.forums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.PdfExportUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class BVT_ExportToPdf_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_ExportToPdf_Forums.class);
	private ForumsUI ui;
	private PdfExportUI pUi;
	private TestConfigCustom cfg;
	private APIForumsHandler apiOwner;
	private User testUser1;
	private String serverURL;
	private List<Forum> testForums = new ArrayList<Forum>();
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		pUi = PdfExportUI.getGui(cfg.getProductName(), driver);
	}

	/**
	*<ul>
	*<li><B>Info:</B>Acceptance test to verify export a forum as pdf.</li>
	*<li><B>Step:</B>(API) Create a forum as UserA and create a topic.</li>
	*<li><B>Step:</B>Go to the forum and click the Export as PDF button.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>PDF sidebar contains entries for forum's name, TOC and entry name</li>
	*<li><B>Verify:</B>PDF content contains forum's name, TOC and entry name</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "UnitOnAnsible", "PdfExport","cnx8ui-cplevel2" })
	public void smokeTestExportForumToPdf() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		
		BaseForum forum = new BaseForum.Builder(testName + rand)
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		logger.strongStep("Create a forum (API)");
		log.info("INFO: Create a forum (API)");
		Forum testForum = forum.createAPI(apiOwner);
		testForums.add(testForum);

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle + rand)
				.tags(Data.getData().ForumTopicTag)
				.description(Data.getData().commonDescription)
				.parentForum(testForum)
				.build();
		
		logger.strongStep("Create a topic (API)");
		log.info("INFO: Create a topic (API)");
		ForumTopic testForumTopic = apiOwner.createForumTopic(forumTopic);
		
		logger.strongStep("Log in to Forums");
		log.info("Load Forums and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Go to the Forum");
		log.info("INFO: Go to forum " + forum.getName());
		driver.navigate().to(testForum.getAlternateLink());
		ui.waitForPageLoaded(driver);
		
		if(cfg.getUseNewUI()) {
			logger.strongStep("Select the Forum Topic created earlier");
			log.info("INFO: Select the Forum Topic created earlier");
			ui.clickLinkWait("link=" + testForumTopic.getTitle());
		}
		
		// call common smoketest method
		pUi.smokeTest(pUi, logger, testForum.getTitle(), testForumTopic.getTitle(), true);
				
		pUi.endTest();
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		for (Forum forum : testForums)  {
			apiOwner.deleteForum(forum);
		}
	}
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a forum as pdf.</li>
	*<li><B>Step:</B>(API) Create a forum.</li>
	*<li><B>Step:</B>Create multiple topics (API).</li>
	*<li><B>Step:</B>Log in to Forums</li>
	*<li><B>Step:</B>Go to the Forum</li>
	*<li><B>Step:</B>List of the titles for validation</li>
	*<li><B>Verify:</B>PDF content validation based on the selected entries</li>
	*</ul>
	 */
	
	@Test(groups = { "PdfExport", "RegressionOnAnsible" ,"cnx8ui-cplevel2" })
	public void exportPdfContentValidation() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		
		BaseForum forum = new BaseForum.Builder(testName + rand)
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		logger.strongStep("Create a forum (API)");
		log.info("INFO: Create a forum (API)");
		Forum testForum = forum.createAPI(apiOwner);
		testForums.add(testForum);

		BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("ForumTopic1" + rand)
				.tags(Data.getData().ForumTopicTag)
				.description(Data.getData().commonDescription)
				.parentForum(testForum)
				.build();
		
		BaseForumTopic forumTopic2 = new BaseForumTopic.Builder("ForumTopic2" + rand)
				.tags(Data.getData().ForumTopicTag)
				.description(Data.getData().commonDescription)
				.parentForum(testForum)
				.build();
		
		BaseForumTopic forumTopic3 = new BaseForumTopic.Builder("ForumTopic3" + rand)
				.tags(Data.getData().ForumTopicTag)
				.description(Data.getData().commonDescription)
				.parentForum(testForum)
				.build();
		
		logger.strongStep("Create multiple topics (API)");
		log.info("INFO: Create multiple topics (API)");
		ForumTopic testForumTopic = apiOwner.createForumTopic(forumTopic1);
		ForumTopic testForumTopic2 = apiOwner.createForumTopic(forumTopic2);
		ForumTopic testForumTopic3 = apiOwner.createForumTopic(forumTopic3);
		
		logger.strongStep("Log in to Forums");
		log.info("Load Forums and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Go to the Forum");
		log.info("INFO: Go to forum " + forum.getName());
		driver.navigate().to(testForum.getAlternateLink());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("List of the titles for validation");
		log.info("INFO: List of the titles for validation");
		String Title1 = testForumTopic.getTitle();
		log.info("Entry 1:" +Title1);
		String Title2 = testForumTopic2.getTitle();
		log.info("Entry 2:" +Title2);
		String Title3 = testForumTopic3.getTitle();
		log.info("Entry 3:" +Title3);

		if(cfg.getUseNewUI()) {
			logger.strongStep("Select the Forum Topic created earlier");
			log.info("INFO: Select the Forum Topic created earlier");
			ui.clickLinkWait("link=" + testForumTopic.getTitle());
		}
		
		logger.strongStep("PDF content validation based on the selected entries");
		log.info("INFO: PDF content validation based on the selected entries");
		pUi.contentSelection(pUi, logger, testForum.getTitle(), testForumTopic.getTitle(), true , Title1, Title2, Title3);
				
		pUi.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a Forums as pdf.</li>
	*<li><B>Step:</B>(API) Create a forum.</li>
	*<li><B>Step:</B>Create a topic (API).</li>
	*<li><B>Step:</B>Log in to Forums</li>
	*<li><B>Step:</B>Go to the Forum</li>
	*<li><B>Step:</B>Go to the added Forum Topic, and click the Export as PDF button.</li>
	*<li><B>Verify:</B>Export PDF Window contains All Options in 'Information included Section'.</li>
	*<li><B>Step:</B>Select all Options from 'Information included Section' List of Export PDF Window.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>All Options Selected above are listed in the Export PDF Preview.</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible","cnx8ui-cplevel2" })
	public void infIncludedExportPdf() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		HashMap<String,String> ExportPdfmap = new HashMap<String,String>();
		
		BaseForum forum = new BaseForum.Builder(testName + rand)
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		logger.strongStep("Create a forum (API)");
		log.info("INFO: Create a forum (API)");
		Forum testForum = forum.createAPI(apiOwner);
		testForums.add(testForum);

		BaseForumTopic forumTopic = new BaseForumTopic.Builder("ForumTopic1" + rand)
				.tags(Data.getData().ForumTopicTag)
				.description(Data.getData().commonDescription)
				.parentForum(testForum)
				.build();
		logger.strongStep("Create a topic (API)");
		log.info("INFO: Create a topic (API)");
		ForumTopic testForumTopic = apiOwner.createForumTopic(forumTopic);
		
		logger.strongStep("Load Forums and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Go to the Forum");
		log.info("INFO: Go to forum " + forum.getName());
		driver.navigate().to(testForum.getAlternateLink());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("List of the titles for validation");
		log.info("INFO: List of the titles for validation");
		String Title1 = testForumTopic.getTitle();
		log.info("Forum Topic:" +Title1);
		
		String userid = pUi.getFirstVisibleElement("xpath=//a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		ExportPdfmap.put("author", userid);
		ExportPdfmap.put("parentdesc", forum.getDescription());
		ExportPdfmap.put("childdesc", forumTopic.getDescription());
		ExportPdfmap.put("parenttags", forum.getTags().toLowerCase());
		ExportPdfmap.put("childtags", forumTopic.getTags().toLowerCase());
		
		if(cfg.getUseNewUI()) {
			logger.strongStep("Select the Forum Topic created earlier");
			log.info("INFO: Select the Forum Topic created earlier");
			ui.clickLinkWait("link=" + testForumTopic.getTitle());
		}
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		String ValidationList = "titlepage:tableofcontent:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, ExportPdfmap, 
				forum.getName(),Title1, ValidationList);
		
		pUi.endTest();	
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify export a Forums as pdf.</li>
	*<li><B>Step:</B>(API) Create a forum.</li>
	*<li><B>Step:</B>Log in to Forums</li>
	*<li><B>Step:</B>Go to the Forum</li>
	*<li><B>Step:</B>Validate that 'PDF' icon/button is not displayed</li>
	*<li><B>Verify:</B>Create a new Forum topic</li>
	*<li><B>Step:</B>List of the title for validation</li>
	*<li><B>Step:</B>Validate the presence of 'PDF' icon/button</li>
	*<li><B>Verify:</B>PDF content validation based on the selected entries</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible","cnx8ui-cplevel2"  })
	public void validateContentBeforeGeneratePdf() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String rand = Helper.genDateBasedRandVal();
		HashMap<String,String> ExportPdfmap = new HashMap<String,String>();
		
		BaseForum forum = new BaseForum.Builder(testName + rand)
				.tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		logger.strongStep("Create a forum (API)");
		log.info("INFO: Create a forum (API)");
		Forum testForum = forum.createAPI(apiOwner);
		testForums.add(testForum);

		BaseForumTopic forumTopic = new BaseForumTopic.Builder("ForumTopic" + rand)
				.tags(Data.getData().ForumTopicTag)
				.description(Data.getData().commonDescription)
				.parentForum(testForum)
				.build();
		
		logger.strongStep("Log in to Forums");
		log.info("Load Forums and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());
		
		logger.strongStep("Go to the Forum");
		log.info("INFO: Go to forum " + forum.getName());
		driver.navigate().to(testForum.getAlternateLink());
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate that 'PDF' icon/button is not displayed");
		log.info("INFO: Validate that 'PDF' icon/button is not displayed");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is not Expected but still present.");
		driver.turnOnImplicitWaits();

		// Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		forumTopic.create(ui);
		
		logger.strongStep("List of the title for validation");
		log.info("INFO: List of the title for validation");
		String Title1 = forumTopic.getTitle();
		log.info("Entry 1:" +Title1);
		
		logger.strongStep("Validate the persence of 'PDF' icon/button");
		log.info("INFO: Validate the persence of 'PDF' icon/button");
		Assert.assertTrue(pUi.isElementPresent(PdfExportUI.pdfExportBtn),"Export PDF button is Expected but still Not present.");
		
		
		logger.strongStep("PDF content validation based on the selected entries");
		log.info("INFO: PDF content validation based on the selected entries");
		
		String userid = pUi.getFirstVisibleElement("xpath=//a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		logger.strongStep("Open the  entry");
		
		ExportPdfmap.put("author", userid);
		ExportPdfmap.put("parentdesc", forum.getDescription());
		ExportPdfmap.put("childdesc", forumTopic.getDescription());
		ExportPdfmap.put("parenttags", forum.getTags().toLowerCase());
		ExportPdfmap.put("childtags", forumTopic.getTags().toLowerCase());
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		String ValidationList = "titlepage:tableofcontent:title:author:summary:tags:creationdate:modifieddate";
		pUi.validateInformationIncludeSectionFunctionality(pUi, logger, ExportPdfmap, 
				forum.getName(),Title1, ValidationList);
		
		pUi.endTest();	
	}

}
