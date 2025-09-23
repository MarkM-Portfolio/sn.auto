package com.ibm.conn.auto.tests.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.GlobalSearchCnx8;

public class BVT_Cnx8UI_Files_Search  extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Files_Search.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private CommonUICnx8 commonUI;
	private GlobalSearchCnx8 globalSearchUI;
	private FileViewerUI uiViewer;
	private User testUser;
	private SearchAdminService adminService;
	private User searchAdmin;
	private APICommunitiesHandler apiOwner;
	String serverURL;
	private APIFileHandler	fileHandler;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		fileHandler = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());		
	
		URLConstants.setServerURL(serverURL);
		searchAdmin = cfg.getUserAllocator().getAdminUser();
		adminService = new SearchAdminService();
		
	}

	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		commonUI = new CommonUICnx8(driver);
		globalSearchUI = new GlobalSearchCnx8(driver);
		cnxAssert = new Assert(log);
	}
	

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify the Search Box suggestion options on a Files page</li>
	 *<li><B>Step:</B> Login to Files Page and Toggle to new UI</li>
	 *<li><B>Verify:</B> Verify the Search text box is displayed</li>
	 *<li><B>Step:</B> Type text in search text box</li>
	 *<li><B>Verify:</B> Verify the text in Search Files Suggestion at the top</li>
	 *<li><B>Verify:</B> Verify the Search Suggestion list Default Order</li>
	 *<li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T662</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"},enabled=true)
	public void verifySearchSuggestionOrderAndDefaultAttributes()
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String text ="test";
		List<String> expectedSearchSuggestionTexts = new ArrayList<>(Arrays.asList(text+" - in Files", text+" - in All Content", text+" - in Communities", text+" - in People"));
		List<String> actualSearchSuggestionTexts = new ArrayList<>();

		globalSearchUI.startTest();

		logger.strongStep("Load Files, login to Connections and toggle to new UI");
		log.info("INFO : Load Files, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentFiles);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Verify that Search TextBox is displayed");
		log.info("INFO : Verify that Search TextBox is displayed");
		cnxAssert.assertTrue(globalSearchUI.isElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox),8),
				"Search TextBox is displayed");

		logger.strongStep("Type text in Search textBox");
		log.info("INFO : Type text in Search textBox");
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(text);
		
		logger.strongStep("Verify that "+ text + " in File Search Suggestion is displayed and People Search Suggestion is at the top");
		log.info("INFO : "+ "Verify that "+ text + " in File Search Suggestion is displayed and People Search Suggestion is at the top");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.fileInSearchSuggestion.replace("PLACEHOLDER", text))).getText(),
				globalSearchUI.findElements(By.xpath(GlobalSearchUIConstants.searchedSuggestionList)).get(0).getText(),
				text + "in Files Search Suggestion is displayed and Files Search Suggestion is at the top");

		logger.strongStep("Verify the order of Suggestions in Search");
		log.info("INFO : Verify the order of Suggestions in Search");
		actualSearchSuggestionTexts = globalSearchUI.textsInSearchSuggestion();
		cnxAssert.assertEquals(actualSearchSuggestionTexts,expectedSearchSuggestionTexts,
				"Verify the order of Suggestions in Search");
		
		globalSearchUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Searched standalone file on Full search page</li>
	 *<li><B>Step:</B> Create a fileA using API</li>
	 *<li><B>Step:</B> Login to Homepage Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Search for created fileA from Quick search</li>
	 *<li><B>Step:</B> Click on fileA in Files option from search dropdown</li>
	 *<li><B>Verify:</B> Verify File filter is selected by default on Full Search page</li>
	 *<li><B>Verify:</B> Verify fielA is displayed on Full Search page</li>
	 *<li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2469</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyStandaloneFileOptionOnQuickSearch() throws Exception {
    	
    	DefectLogger logger=dlog.get(Thread.currentThread().getId());

		BaseFile file = new BaseFile.Builder(Data.getData().file3)
									.extension(".jpg")
									.rename(Helper.genDateBasedRand())
									.build();
		
		commonUI.startTest();
		
		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename()+ file.getExtension());
		
		// Run indexer for communities
		adminService.indexNow("files", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
					
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO : Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Type "+file.getName()+" in Search textBox");
		log.info("INFO : Type "+file.getName()+" in Search textBox");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(file.getName());
		
		logger.strongStep("Click on "+ file.getName() + " - in Files option from suggestion list");
		log.info("INFO : Click on "+ file.getName() + " - in Files option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.fileInSearchSuggestion.replace("PLACEHOLDER", file.getName())), 4,
				" - in File option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);

		logger.strongStep("Verify 'Files' is selected in top filters label");
		log.info("INFO : Verify 'Files' is selected in top filters label");
		globalSearchUI.waitForPageLoaded(driver);
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "Files", "'Files' is selected");

		logger.strongStep("Verify searched standalone file is displayed on Full search page");
		log.info("INFO : Verify searched standalone file is displayed on Full search page");
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultsForFiles)).getText(), file.getName(), "File name is displayed");
		
		
		commonUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify Searched standalone file on Full search page</li>
	 *<li><B>Step:</B> Create a Community using API</li>
	 *<li><B>Step:</B> Create a fileA in that Community using API</li>
	 *<li><B>Step:</B> Login to Homepage Page and Toggle to new UI</li>
	 *<li><B>Step:</B> Search for created fileA from Quick search</li>
	 *<li><B>Step:</B> Click on fileA in Files option from search dropdown</li>
	 *<li><B>Verify:</B> Verify File filter is selected by default on Full Search page</li>
	 *<li><B>Verify:</B> Verify fielA is displayed on Full Search page</li>
	 *<li><B>JIRA
	 * Link:</B>https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2470</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyCommunityFileOptionOnQuickSearch() throws Exception {
		
    	DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName =commonUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		 							.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
		 							.tags("commtag")
		 							.description("Test Gallery in community")
		 							.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
									 .comFile(true)
									 .extension(".jpg")
									 .build();
	

		//Create community
		log.info("INFO: Create community using API");
		Community commAPI =community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
     
		log.info("INFO: Upload a file using API");
		fileA.setName(fileA.getRename()+ fileA.getExtension());
		community.addFileAPI(commAPI, fileA, apiOwner, fileHandler);	
		
		// Run indexer for communities
		adminService.indexNow("files", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());
				
		logger.strongStep("Load Homepage, login to Connections and toggle to new UI");
		log.info("INFO : Load Homepage, login to Connections and toggle to new UI");
		commonUI.loadComponent(Data.getData().ComponentHomepage);
		commonUI.loginAndToggleUI(testUser, cfg.getUseNewUI());

		logger.strongStep("Type "+fileA.getName()+" in Search textBox");
		log.info("INFO : Type "+fileA.getName()+" in Search textBox");
		globalSearchUI.waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);
		globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.searchTextBox)).sendKeys(fileA.getName());
		
		logger.strongStep("Click on "+ fileA.getName() + " - in Files option from suggestion list");
		log.info("INFO : Click on "+ fileA.getName() + " - in Files option from suggestion list");
		globalSearchUI.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.fileInSearchSuggestion.replace("PLACEHOLDER", fileA.getName())), 4,
				" - in File option");
		globalSearchUI.waitForElementInvisibleWd(By.cssSelector(GlobalSearchUIConstants.progressBar), 5);

		logger.strongStep("Verify 'Files' is selected in top filters label");
		log.info("INFO : Verify 'Files' is selected in top filters label");
		globalSearchUI.waitForPageLoaded(driver);
		cnxAssert.assertEquals(globalSearchUI.findElement(By.cssSelector(GlobalSearchUIConstants.selectedFilters)).getText(), "Files", "'Files' is selected");

		logger.strongStep("Verify searched community file is displayed on Full search page");
		log.info("INFO : Verify searched community file is displayed on Full search page");
		globalSearchUI.waitForElementVisibleWd(By.xpath(GlobalSearchUIConstants.searchResultsForFiles), 5);
		cnxAssert.assertEquals(globalSearchUI.findElement(By.xpath(GlobalSearchUIConstants.searchResultsForFiles)).getText(), fileA.getName(), "File name is displayed");
		
		
		commonUI.endTest();
	}
}
