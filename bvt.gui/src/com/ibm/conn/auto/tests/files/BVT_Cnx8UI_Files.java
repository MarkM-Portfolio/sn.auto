package com.ibm.conn.auto.tests.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseFolder.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.FilesUICnx8;
import com.ibm.conn.auto.webui.cnx8.ItmNavCnx8;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Cnx8UI_Files extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Cnx8UI_Files.class);
	private Assert cnxAssert;
	private TestConfigCustom cfg;
	private FilesUICnx8 ui;
	private User testUser,testUserAddedToITM,testUser2;
	private APIProfilesHandler profilesAPIUser;
	private String serverURL;
	private ItmNavCnx8 itmNavCnx8;
	private FilesUI fUI;
	private APIFileHandler apiFileOwner,apiFileOwnerTestUser;
	private FileViewerUI uiViewer;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		// get a test user
		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUserAddedToITM = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		apiFileOwner = new APIFileHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		apiFileOwnerTestUser = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		profilesAPIUser = new APIProfilesHandler(serverURL, testUserAddedToITM.getAttribute(cfg.getLoginPreference()), testUserAddedToITM.getPassword());
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void SetUpMethod() {
		ui = new FilesUICnx8(driver);
		itmNavCnx8 = new ItmNavCnx8(driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new Assert(log);
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Test switching to Files via first level nav</li>
	 *<li><B>Step:</B> Login to Homepage</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Click Files in nav menu</li>
	 *<li><B>Verify:</B> My Drive icon is displayed</li>
	 *<li><B>Verify:</B> Files is selected in nav</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui"})
	public void testFirstLevelNav() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		ui.startTest();
		
		logger.strongStep("Login to Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);
		
		logger.strongStep("Toggle to the new UI");
		CommonUICnx8 commonUI = new CommonUICnx8(driver);
		commonUI.toggleNewUI(true);
		
		logger.strongStep("Select Files in nav menu");
		log.info("INFO: Select Files in nav menu");		
		AppNavCnx8.FILES.select(ui);
		
		logger.strongStep("Verify My Drive icon is displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(
				By.cssSelector(".iconMyDrive"), 3), "My Drive icon is displayed");		
		logger.strongStep("Verify Files is selected in nav");
		cnxAssert.assertTrue(AppNavCnx8.FILES.isAppSelected(ui),
				"Files is selected in navigation");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify clicking on the filter icon of a person on the ITM bar from files should show files belonging to that user </li>
	 *<li><B>Prereq:</B>[API] testUserAddedToITM uploads public file </li>
	 *<li><B>Step:</B> Login to Files with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Add person entry to ITM for testUserAddedToITM if not there</li>
	 *<li><B>Step:</B> Hover over person entry and click on filter icon</li>
	 *<li><B>Verify:</B> Verify that user navigates to page with URL Server_URL/files/app#/person/${USER_ID}</li>
	 *<li><B>Verify:</B> Verify that files belonging to the testUserAddedToITM whose filter icon is clicked should be displayed </li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T602</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyClickingPersonFilterFromFile() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName= ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename(testName + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();
		
	
		logger.strongStep("Upoad public file via API ");
		log.info("INFO: Upoad public file via API ");
		FileEntry publicFile = FileEvents.addFile(file, testUserAddedToITM, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());
		
		logger.strongStep("Load files, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load files, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		// Adding user to ITM if it is already not added
		logger.strongStep("Add user in ITM and click on filter icon");
		itmNavCnx8.addUserToITMAndClickFilterIcon(testUserAddedToITM);
		ui.waitForElementVisibleWd(By.xpath(FilesUICnx8.getFileLink(file)),5);
		
		log.info("INFO: Verify that "+testUserAddedToITM.getDisplayName() +" Files page is opened");
		logger.strongStep("Verify that "+testUserAddedToITM.getDisplayName() +" Files page is opened");	
		String expectedUrl = Data.getData().userMyFilesPageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", profilesAPIUser.getUUID());
		cnxAssert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase()),"User navigates to "+expectedUrl);
		
		log.info("Verify that files belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		logger.strongStep("Verify that files belonging to "+testUserAddedToITM.getDisplayName() + " should be displayed" );
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUICnx8.getFileLink(file)),5),"File is displayed");
		
		log.info("INFO: Delete file");
		apiFileOwner.deleteFile(publicFile);
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify left navigation after clicking on options of top level secondary navigation</li>
	 *<li><B>Step:</B> Login to Files with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Files and Folders from Sec Top Nav</li>
	 *<li><B>Step:</B> Verify that left nav is displayed</li>
	 *<li><B>Step:</B> Click on Communities Files from Sec Top Nav</li>
	 *<li><B>Step:</B> Verify that left nav is not displayed</li>
	 *<li><B>Step:</B> Click on Public Fils from Sec Top Nav</li>
	 *<li><B>Step:</B> Verify that left nav is not 	displayed</li>
	 *<li><B>Step:</B> Click on Trash from Sec Top Nav</li>
	 *<li><B>Step:</B> Verify that left nav is not displayed</li>
	 *<li><B>Step:</B> Go to Files and Folders</li>
	 *<li><B>Step:</B> Expand Files from left Nav</li>
	 *<li><B>Step:</B> Verify that Files sub items are 'My Files', 'Pinned Files', 'Shared With Me'</li>
	 *<li><B>Step:</B> Expand Folders from left Nav</li>
	 *<li><B>Step:</B> Verify that Folders sub items are 'Pinned Folders', 'Shared With Me', 'Public Folders'</li>
	 *<li><B>Step:</B> Create a New Folder starting with small letter name</li>
	 *<li><B>Step:</B> Create another New Folder starting with capital letter name</li>
	 *<li><B>Verify:</B> Verify that Folders sub items are displayed in Sequence</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T713</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T716</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T777</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifySecondaryNavigationOnFilePage() throws Exception
	{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		BaseFolder folder = new BaseFolder.Builder("newFolder" + Helper.genDateBasedRand())
				   .description(Data.getData().FolderDescription)
				   .build();
		
		BaseFolder folder2 = new BaseFolder.Builder("AnewFolder" + Helper.genDateBasedRand())
				   .description(Data.getData().FolderDescription)
				   .build();

		logger.strongStep("Load files, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		log.info("INFO: Load files, login as "+ testUser.getEmail()+ "and Load new UI as "+cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Click on Files and Folder from Sec Top Nav");
		log.info("INFO: Click on Files and Folder from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		
		logger.strongStep("Verify that left nav is displayed");
		log.info("INFO: Verify that left nav is displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUIConstants.leftNavigation),5),"Verify Left nav is displayed");

		logger.strongStep("Click on Commununities Files from Sec Top Nav");
		log.info("INFO: Click on Commununities Files from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.communityFilesSecTopNav), 4, "Click on Commununities Files from Top Nav");
		
		logger.strongStep(" Verify that left nav is not displayed");
		log.info("INFO:  Verify that left nav is not displayed");
		cnxAssert.assertTrue(ui.waitForElementInvisibleWd(By.xpath(FilesUIConstants.leftNavigation), 4),"Verify Left nav is not displayed");

		logger.strongStep("Click on Public Files from Sec Top Nav");
		log.info("INFO: Click on Public Files from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.publicFilesSecTopNav), 4, "Click on Public Files from Top Nav");
		
		logger.strongStep(" Verify that left nav is not displayed");
		log.info("INFO:  Verify that left nav is not displayed");
		cnxAssert.assertTrue(ui.waitForElementInvisibleWd(By.xpath(FilesUIConstants.leftNavigation), 4),"Verify Left nav is not displayed");
		
		logger.strongStep("Click on Trash from Sec Top Nav");
		log.info("INFO: Click on Trash from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.trashSecTopNav), 4, "Click on Trash from Top Nav");
		
		logger.strongStep(" Verify that left nav is not displayed");
		log.info("INFO:  Verify that left nav is not displayed");
		cnxAssert.assertTrue(ui.waitForElementInvisibleWd(By.xpath(FilesUIConstants.leftNavigation), 4),"Verify Left nav is not displayed");
		
		logger.strongStep("Go to Files and Folders");
		log.info("INFO: Go to Files and Folders");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		
		logger.strongStep("Expand Files from left Nav");
		log.info("INFO: Expand Files from left Nav");
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.expandFiles), 4);
		ui.clickLinkWaitWd(By.cssSelector(FilesUIConstants.expandFiles), 4, "Expand Files from left nav");
		
		logger.strongStep("Verify that Files subitems are 'My Files', 'Pinned Files', 'Shared With Me'");
		log.info("INFO: Verify that Files subitems are 'My Files', 'Pinned Files', 'Shared With Me'");
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.fileChildTable), 4);
		String arrFilesSubItem[] = { "My Files", "Pinned Files", "Shared With Me" };
		verifySubItems("Files", arrFilesSubItem, By.cssSelector(FilesUIConstants.fileChildItems));

		logger.strongStep("Expand Folders from left Nav");
		log.info("INFO: Expand Folders from left Nav");
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.expandFolders), 4);
		ui.clickLinkWaitWd(By.cssSelector(FilesUIConstants.expandFolders), 4, "Expand Folders from left nav");
		
		logger.strongStep("Verify that Folders subitems are 'Pinned Folders', 'Shared With Me', 'Public Folders'");
		log.info("INFO: Verify that Folders subitems are 'Pinned Folders', 'Shared With Me', 'Public Folders'");
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.folderChildTable), 4);
		String arrFoldersSubItem[] = { "Pinned Folders", "Shared With Me", "Public Folders" };
		verifySubItems("Folders", arrFoldersSubItem, By.cssSelector(FilesUIConstants.folderChildItems));
		
		logger.strongStep("Create a New Folder starting with small letter name");
		log.info("INFO: Create a New Folder starting with small letter name");
		folder.create(fUI);
		
		logger.strongStep("Create another New Folder starting with capital letter name");
		log.info("INFO: Create another New Folder starting with capital letter name");
		folder2.create(fUI);

		logger.strongStep("Verify that Folders subitems are displayed in Sequence like 'Pinned Folders', 'Shared With Me', 'Public Folders'" + "," + folder2.getName()+ "," +folder.getName());
		log.info("INFO: Verify that Folders subitems are displayed in Sequence like'Pinned Folders', 'Shared With Me', 'Public Folders'" + "," + folder2.getName()+ "," +folder.getName());
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.folderChildTable), 4);
		String arrFoldersNewSubItem[] = { "Pinned Folders", "Shared With Me", "Public Folders", folder2.getName(), folder.getName(),};
		verifySubItems("Folders", arrFoldersNewSubItem, By.cssSelector(FilesUIConstants.folderChildItems));

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify file action button tool bar on File Page</li>
	 *<li><B>Step:</B> Upload a file via API</li>
	 *<li><B>Step:</B> Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B> Click on Files and Folders from Sec Top Nav</li>
	 *<li><B>Verify:</B> Verify that left nav is displayed</li>
	 *<li><B>Verify:</B> Verify 'Download' Button is displayed as disabled</li>
	 *<li><B>Verify:</B> Verify 'Share' Button is displayed as disabled</li>
	 *<li><B>Verify:</B> Verify 'Add To' Button is displayed as disabled</li>
	 *<li><B>Verify:</B> Verify 'Add Tags' Button is displayed as disabled</li>
	 *<li><B>Verify:</B> Verify 'Transfer Ownership' Button is displayed as disabled</li>
	 *<li><B>Verify:</B> Verify 'Move To Trash' Button is displayed as disabled</li>
	 *<li><B>Step:</B> Click on Select All Check Box</li>
	 *<li><B>Verify:</B> Verify 'Download' Button is displayed as enabled</li>
	 *<li><B>Verify:</B> Verify 'Share' Button is displayed as enabled</li>
	 *<li><B>Verify:</B> Verify 'Add To' Button is displayed as enabled</li>
	 *<li><B>Verify:</B> Verify 'Add Tags' Button is displayed as enabled</li>
	 *<li><B>Verify:</B> Verify 'Transfer Ownership' Button is displayed as enabled</li>
	 *<li><B>Verify:</B> Verify 'Move To Trash' Button is displayed as enabled</li>
	 *<li><B>Step:</B> Open and download file</li>
	 *<li><B>Step:</B> Validate that the file was downloaded</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T772</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyFilesActionButtonToolbarOnFilePage() throws Exception
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg").rename(Helper.genDateBasedRand())
				.build();
		

		ui.startTest();

		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Click on Files and Folder from Sec Top Nav");
		log.info("INFO: Click on Files and Folder from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		ui.waitForPageLoaded(driver);
		ui.sleep(5000);
		
		logger.strongStep("Verify that left nav is displayed");
		log.info("INFO: Verify that left nav is displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUIConstants.leftNavigation),5),"Verify Left nav is displayed");
		
		logger.strongStep(" Verify 'Download' Button is displayed as disabled");
		log.info("INFO:  Verify 'Download Button' is displayed as disabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.downloadBulkFilesLink.replace("PLACEHOLDER", "true")))),"'Download' Button is displayed as disabled");
		
		logger.strongStep(" Verify 'Share' Button is displayed as disabled");
		log.info("INFO:  Verify 'Share' Button is displayed as disabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.shareFilesLink.replace("PLACEHOLDER", "true")))),"'Share' Button is displayed as disabled");
		
		logger.strongStep(" Verify 'Add To' Button is displayed as disabled");
		log.info("INFO:  Verify 'Add To' Button is displayed as disabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.addToBulkFilesLink.replace("PLACEHOLDER", "true")))),"'Add To' Button is displayed as disabled");
		
		logger.strongStep(" Verify 'Add Tags' Button is displayed as disabled");
		log.info("INFO:  Verify 'Add Tags' Button is displayed as disabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.addTagsLink.replace("PLACEHOLDER", "true")))),"'Add tags' Button is displayed as disabled");
		
		logger.strongStep(" Verify 'Transfer Ownership' Button is displayed as disabled");
		log.info("INFO:  Verify 'Transfer Ownership' Button is displayed as disabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.transferFilesLink.replace("PLACEHOLDER", "true")))),"'Transfer Ownership' Button is displayed as disabled");
		
		logger.strongStep(" Verify 'Move To Trash' Button is displayed as disabled");
		log.info("INFO:  Verify 'Move To Trash' Button is displayed as disabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.moveToTrashFilesLink.replace("PLACEHOLDER", "true")))),"'Move To Trash' Button is displayed as disabled");

		logger.strongStep("Click on Select All Check Box");
		log.info("INFO: Click on Select All Check Box");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.selectAllCheckBox), 4, "Click on Select All Check Box");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep(" Verify 'Download' Button is displayed as enabled");
		log.info("INFO:  Verify 'Download Button' is displayed as enabled");
		ui.waitForClickableElementWd((By.cssSelector(FilesUIConstants.downloadBulkFilesLink.replace("PLACEHOLDER", "false"))), 10);
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.downloadBulkFilesLink.replace("PLACEHOLDER", "false")))),"'Download' Button is displayed as enabled");
		
		logger.strongStep(" Verify 'Share' Button is displayed as enabled");
		log.info("INFO:  Verify 'Share' Button is displayed as enabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.shareFilesLink.replace("PLACEHOLDER", "false")))),"'Share' Button is displayed as enabled");
		
		logger.strongStep(" Verify 'Add To' Button is displayed as enabled");
		log.info("INFO:  Verify 'Add To' Button is displayed as enabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.addToBulkFilesLink.replace("PLACEHOLDER", "false")))),"'Add To' Button is displayed as enabled");
		
		logger.strongStep(" Verify 'Add Tags' Button is displayed as enabled");
		log.info("INFO:  Verify 'Add Tags' Button is displayed as enabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.addTagsLink.replace("PLACEHOLDER", "false")))),"'Add tags' Button is displayed as enabled");
		
		logger.strongStep(" Verify 'Transfer Ownership' Button is displayed as enabled");
		log.info("INFO:  Verify 'Transfer Ownership' Button is displayed as enabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.transferFilesLink.replace("PLACEHOLDER", "false")))),"'Transfer Ownership' Button is displayed as enabled");
		
		logger.strongStep(" Verify 'Move To Trash' Button is displayed as enabled");
		log.info("INFO:  Verify 'Move To Trash' Button is displayed as enabled");
		cnxAssert.assertTrue((ui.isElementDisplayedWd(By.cssSelector(FilesUIConstants.moveToTrashFilesLink.replace("PLACEHOLDER", "false")))),"'Move To Trash' Button is displayed as enabled");

		logger.strongStep("Change the 'View List' format");
		ui.clickLinkWait(FilesUICloud.listView);

		logger.strongStep("Open and download file");
		log.info("INFO: Open and download file");
		fUI.download(file);

		logger.strongStep("Validate that the file was downloaded");
		log.info("INFO: Check if file was downloaded");
		fUI.verifyFileDownloaded(file.getRename() + file.getExtension());

		ui.endTest();
	}

	/**
	 * Verify subitems of specified left nav menu
	 * @param leftNavMenu
	 * @param arr
	 * @param locator
	 */
	private void verifySubItems(String leftNavMenu, String arr[], By locator) {
		List<WebElement> SubItemEle = ui.findElements(locator);
		List<String> expSubItem = new ArrayList<>(Arrays.asList(arr));
		List<String> actSubItem = new ArrayList<>();
		for (WebElement SubItem : SubItemEle) {
			String SubItemText = SubItem.getText();
			log.info("Subitem text in " + leftNavMenu + "is" + SubItemText);
			actSubItem.add(SubItemText);
		}
		cnxAssert.assertTrue(actSubItem.containsAll(expSubItem), "Actual subitems matches with expected subitems");
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify user is able to drag and drop private file from My Files to public folder successfully</li>
	 *<li><B>Step:</B> Login to Files with testUser</li>
	 *<li><B>Step:</B> Toggle to the new UI</li>
	 *<li><B>Step:</B> [API] Upload private file</li>
	 *<li><B>Step:</B> Create public folder from UI</li>
	 *<li><B>Step:</B> Verify public folder is created successfully</li>
	 *<li><B>Step:</B> Go to my files</li>
	 *<li><B>Step:</B> Drag a file created in above step and drop to the newly created folder</li>
	 *<li><B>Step:</B> Verify that confirmation pop up is displayed</li>
	 *<li><B>Step:</B> Click Ok</li>
	 *<li><B>Step:</B> Verify file copied into test folder</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T716</li>
	 *</ul>
	 * @throws Exception 
	 */
	@Test
	public void dragDropFileFromFilesToFolder() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName + Helper.genDateBasedRand())
                .description(Data.getData().FolderDescription)
                .access(Access.PUBLIC)
                .build();
		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename(testName + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE)
				.build();
		
		logger.strongStep("Upoad public file via API ");
		log.info("INFO: Upoad public file via API ");
		FileEntry privateFile = FileEvents.addFile(file, testUser, apiFileOwnerTestUser);
		file.setName(file.getRename() + file.getExtension());
	
		logger.strongStep("Create public folder from UI");
		log.info("INFO: Create public folder from UI");
		fUI.create(folder);
		
		logger.strongStep("Verify public folder is created successfully");
		log.info("INFO: Verify public folder "+folder.getName()+" is created successfully");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUICnx8.getFolder(folder.getName()))), serverURL);
		
		logger.strongStep("Select My Files from left nav");
		log.info("INFO: Select My Files from left nav");
		ui.clickLinkWd(By.xpath(FilesUIConstants.myFiles), "Select My Files from left nav");
		ui.waitForElementVisibleWd(By.xpath(FilesUICnx8.getFileLink(file)), 4);
		
		WebElement sourceEle = ui.findElement(By.xpath(FilesUICnx8.getFileLink(file)));
		WebElement targetEle = ui.findElement(By.xpath(FilesUICnx8.getFolder(folder.getName())));

		logger.strongStep("Drag and drop the file to folder");
		log.info("INFO: Drag and drop the file "+ file.getName()+" to folder"+folder.getName());
		ui.dragAndDropWd(sourceEle, targetEle);
		
		logger.strongStep("Verify that confirmation pop up is displayed");
		log.info("INFO: Verify that confirmation pop up is displayed");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.confirmationPopUp), 4);
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUIConstants.confirmationPopUp)), "Confirmation pop up is displayed");
		
		logger.strongStep("Click OK");
		log.info("INFO: Click OK");
		ui.clickLinkWaitWd(ui.createByFromSizzle(FilesUIConstants.okButton), 5, "Click OK button");
		
		logger.strongStep("Verify file copied into test folder");
		log.info("INFO: Verify file copied into test folder");
		ui.fluentWaitTextPresent( file.getName() + " was added to " + folder.getName());
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUICnx8.getFileLink(file))), "File is copied to folder");

		logger.strongStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwnerTestUser.deleteFile(privateFile);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify file action button tool bar on File Page</li>
	 *<li><B>Step:</B>Upload a file via API</li>
	 *<li><B>Step:</B>Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B>Click on Files and Folders from Sec Top Nav</li>
	 *<li><B>Verify:</B>Verify that left nav is displayed</li>
	 *<li><B>Step:</B>Expand Files from left Nav</li>
	 *<li><B>Verify:</B>Verify that Files sub items are 'My Files', 'Pinned Files', 'Shared With Me' </li>
	 *<li><B>Step:</B>Expand 'My Files' from left Nav</li>
	 *<li><B>Verify:</B>Validate that newly uploaded file is visible after clicking on My Files </li>
	 *<li><B>Step:</B> Expand 'Pinned Files' from left Nav</li>
	 *<li><B>Verify:</B>Validate empty message after clicking on 'Pinned Files'</li>
	 *<li><B>Step:</B>Expand 'Shared With Me' from left Nav</li>
	 *<li><B>Verify:</B>Validate empty message after clicking on 'Shared With Me'</li>
	 *<li><B>Step:</B>Click on 'Folders' from left Nav</li>
	 *<li><B>Verify:</B>Validate empty message after clicking on 'Folders'</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T779</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyFilesSubMenuOptions() throws Exception
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser = cfg.getUserAllocator().getUser();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg").rename(Helper.genDateBasedRand())
				.build();
		
		ui.startTest();

		//Upload a file
		logger.strongStep("Upload a file via API");
		uiViewer.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Click on Files and Folder from Sec Top Nav");
		log.info("INFO: Click on Files and Folder from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that left nav is displayed");
		log.info("INFO: Verify that left nav is displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUIConstants.leftNavigation),5),"Verify Left nav is displayed");
		
		logger.strongStep("Expand Files from left Nav");
		log.info("INFO: Expand Files from left Nav");
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.expandFiles), 4);
		ui.clickLinkWaitWd(By.cssSelector(FilesUIConstants.expandFiles), 4, "Expand Files from left nav");
		
		logger.strongStep("Verify that Files subitems are 'My Files', 'Pinned Files', 'Shared With Me'");
		log.info("INFO: Verify that Files subitems are 'My Files', 'Pinned Files', 'Shared With Me'");
		ui.waitForElementVisibleWd(By.cssSelector(FilesUIConstants.fileChildTable), 4);
		String arrFilesSubItem[] = { "My Files", "Pinned Files", "Shared With Me" };
		verifySubItems("Files", arrFilesSubItem, By.cssSelector(FilesUIConstants.fileChildItems));
		
		logger.strongStep("Expand 'My Files' from left Nav");
		log.info("INFO: Expand 'My Files' from left Nav");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.myFilesSubMenuLink), 4);
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.myFilesSubMenuLink), 4, "Expand My Files from left nav");
		
		logger.strongStep("Validate that newly uploaded file is visible after clicking on My Files");
		log.info("INFO: Validate that newly uploaded file is visible after clicking on My Files");
		Files_Display_Menu.DETAILS.select(ui);
		cnxAssert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),"Newly uploaded file is displayed " + file.getName());

		logger.strongStep("Expand 'Pinned Files' from left Nav");
		log.info("INFO: Expand 'Pinned Files' from left Nav");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.pinnedFilesSubMenuLink), 4);
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.pinnedFilesSubMenuLink), 4, "Expand Pinned Files from left nav");
		
		logger.strongStep("Validate empty message after clicking on 'Pinned Files'");
		log.info("INFO: Validate empty message after clicking on 'Pinned Files'");
		String expMsgOnPinnedFiles = ui.getElementTextWd(By.xpath(FilesUIConstants.emptyMessage));
		String actMsg = "Keep the files you are working on readily available by pinning them to this list. You can add files that you own, files that are shared with you, or public files by clicking the pin icon .";
		cnxAssert.assertEquals(expMsgOnPinnedFiles, actMsg, "Empty message is displayed after clicking on 'Pinned Files'");
		
		logger.strongStep("Expand 'Shared With Me' from left Nav");
		log.info("INFO: Expand 'Shared With Me' from left Nav");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.sharedwithmeSubMenuLink), 4);
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.sharedwithmeSubMenuLink), 4, "Expand Shared With Me from left nav");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate empty message after clicking on 'Shared With Me'");
		log.info("INFO: Validate empty message after clicking on 'Shared With Me'");
		String expMsgOnSharedWithMe = ui.getElementTextWd(By.xpath(FilesUIConstants.emptyMessage));
		cnxAssert.assertEquals(expMsgOnSharedWithMe, "There are no files shared with you.", "Empty message is displayed after clicking on 'Shared With Me'");
		
		logger.strongStep("Click on 'Folders' from left Nav");
		log.info("INFO: Click on 'Folders' from left Nav");
		ui.waitForElementVisibleWd(ui.createByFromSizzle(FilesUIConstants.MyFoldersLeftMenu), 4);
		ui.clickLinkWaitWd(ui.createByFromSizzle(FilesUIConstants.MyFoldersLeftMenu), 4, "Click on 'Folders' from left nav");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Validate empty message after clicking on 'Folders'");
		log.info("INFO: Validate empty message after clicking on 'Folders'");
		ui.waitForElementInvisibleWd(ui.findElement(By.xpath(FilesUIConstants.emptyMessage)), 10);
		String expMsgOnFolders = ui.getElementTextWd(By.xpath(FilesUIConstants.emptyMessage));
		cnxAssert.assertEquals(expMsgOnFolders, "You have not created any folders.", "Empty message is displayed after clicking on 'Folders'");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify file action button tool bar on File Page</li>
	 *<li><B>Step:</B>Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B>Click on Files and Folders from Sec Top Nav</li>
	 *<li><B>Verify:</B>Verify that left nav is displayed</li>
	 *<li><B>Step:</B>Click on 'About' Link from Page Footer section</li>
	 *<li><B>Verify:</B>Verify that left nav is not displayed</li>
	 *<li><B>Step:</B>Click on 'About' Link from Page Footer section</li>
	 *<li><B>Verify:</B>Verify that left nav is not displayed</li>
	 *<li><B>Step:</B>Click on 'Files' Link from About Page</li>
	 *<li><B>Verify:</B>Validate 'Files and Folder' link is displayed from Sec Top Nav</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T780</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void verifyAboutLinkOnFiles() throws Exception
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Click on Files and Folder from Sec Top Nav");
		log.info("INFO: Click on Files and Folder from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify that left nav is displayed");
		log.info("INFO: Verify that left nav is displayed");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUIConstants.leftNavigation),5),"Verify Left nav is displayed");
		
		logger.strongStep("Click on 'About' Link from Page Footer section");
		log.info("INFO: Click on 'About' Link from Page Footer section");
		driver.executeScript("arguments[0].scrollIntoView(true);", ui.findElement(By.xpath(FilesUIConstants.aboutLinkAtFooter)));
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.aboutLinkAtFooter), 4);
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.aboutLinkAtFooter), 4, "Click on 'About' Link from Page Footer section");
		
		logger.strongStep("Verify that left nav is not displayed");
		log.info("INFO: Verify that left nav is not displayed");
		cnxAssert.assertFalse(ui.isElementVisibleWd(By.xpath(FilesUIConstants.leftNavigation),5),"Verify Left nav is not displayed");
		
		logger.strongStep("Click on 'Files' Link from About Page");
		log.info("INFO: Click on 'Files' Link from About Page");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.fileLinkAtAboutPage), 4);
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.fileLinkAtAboutPage), 4, "Click on 'Files' Link from About Page");
		
		logger.strongStep("Validate 'Files and Folder' link is displayed from Sec Top Nav");
		log.info("INFO: Validate 'Files and Folder' link is displayed  from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		cnxAssert.assertEquals((ui.getElementTextWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav))),"Files and Folders", "'Files and Folder' link is displayed");	
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B>Verify 'Add File' functionality of folder properties edit option</li>
	 *<li><B>Step:</B>[API] Upload public file</li>
	 *<li><B>Step:</B>[API] Create public folder</li>
	 *<li><B>Step:</B>Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B>Click on newly created folder in above step</li>
	 *<li><B>Step:</B>Click on edit option</li>
	 *<li><B>Step:</B>Select 'Add File' option</li>
	 *<li><B>Step:</B>Select the uploaded file checkbox displayed on Add Files pop up</li>
	 *<li><B>Step:</B>Select 'Add Files' button</li>
	 *<li><B>Verify:</B>Verify success message</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T778</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void addFilesToFolder() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		BaseFile baseFolder = new BaseFile.Builder("BVT_Folder_" + Helper.genStrongRand())
				.shareLevel(ShareLevel.EVERYONE).build();
		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand()).rename(testName + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE).build();

		logger.strongStep("Upoad public file via API ");
		log.info("INFO: Upoad public file via API ");
		FileEntry publicFile = FileEvents.addFile(file, testUser, apiFileOwnerTestUser);
		file.setName(file.getRename() + file.getExtension());
		
		log.info("INFO: Public folder created successfully");
		logger.strongStep("Public folder created successfully");
		FileEntry publicFolder = apiFileOwnerTestUser.createFolder(baseFolder, Role.READER);
		
		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		// Select newly created folder
		selectFolder(publicFolder);
			
		logger.strongStep("Select 'Add Files...'");
		log.info("INFO:Select 'Add Files...'");
		Files_Folder_Dropdown_Menu.ADD_FILES.select(ui);
		
		logger.strongStep("Select file "+file.getRename() + file.getExtension()+" checkbox");
		log.info("INFO: Select file "+file.getRename() + file.getExtension()+" checkbox");
		String fileName = file.getRename() + file.getExtension();
		ui.clickLink(FilesUI.getFileCheckbox(fileName));

		logger.strongStep("INFO: Click on Add files button");
		log.info("INFO: Click on Add files button");
		ui.clickLinkWaitWd(ui.createByFromSizzle(FilesUIConstants.addFileButton), 5);

		logger.strongStep("Verify File - " + file.getRename() + " is Added successfully to Folder - " + baseFolder.getName() + ".");
		log.info("INFO: Verify File - " + file.getRename() + " is Added successfully to Folder - " + baseFolder.getName() + ".");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.successMessage), 8);
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.successMessage)).getText().contains(file.getRename() +file.getExtension()+" was added to "+baseFolder.getName()+"."),"File - " + file.getRename() +" is Added successfully to Folder - " + baseFolder.getName() );
		
		logger.strongStep("Delete File");
		log.info("INFO: Delete File");
		apiFileOwnerTestUser.deleteFile(publicFolder);
		apiFileOwnerTestUser.deleteFile(publicFile);
		
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify 'Move to' functionality of folder properties edit option</li>
	 *<li><B>Step:</B>[API] Create public folder as a target folder to move current folder into it </li>
	 *<li><B>Step:</B>[API] Create public folder that will be moved to folder created in above step</li>
	 *<li><B>Step:</B>Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B>Click on newly created folder in above step</li>
	 *<li><B>Step:</B>Click on edit option</li>
	 *<li><B>Step:</B>Select 'Move to' option</li>
	 *<li><B>Step:</B>Select the folder checkbox displayed in 'My Folders' on 'Move to...'pop up</li>
	 *<li><B>Step:</B>Select 'Move Here' button</li>
	 *<li><B>Verify:</B>Verify success message</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T778</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void moveToFolder() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		BaseFile baseFolder = new BaseFile.Builder("BVT_Folder_" + Helper.genStrongRand())
				.shareLevel(ShareLevel.EVERYONE).build();
		
		BaseFile baseFolderMovedTo = new BaseFile.Builder(testName + Helper.genStrongRand())
				.shareLevel(ShareLevel.EVERYONE).build();

		log.info("INFO: Create public folder as a target folder "+baseFolderMovedTo.getName()+" to move the current folder into it ");
		logger.strongStep("Create public folder as a target folder "+baseFolderMovedTo.getName()+" to move the current folder into it ");
		FileEntry folderMovedTo = apiFileOwnerTestUser.createFolder(baseFolderMovedTo, Role.READER);
		
		log.info("INFO: Create public folder "+baseFolder.getName()+" that will be moved to folder created in above step");
		logger.strongStep("Create public folder "+baseFolder.getName()+" that will be moved to folder created in above step");
		FileEntry folderMovedToBe = apiFileOwnerTestUser.createFolder(baseFolder, Role.READER);
		
		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		// Select newly created folder 
		selectFolder(folderMovedToBe);
		
		logger.strongStep("Select 'Move to...'");
		log.info("INFO:Select 'Move to...'");
		Files_Folder_Dropdown_Menu.MOVE_TO.select(ui);
	
		logger.strongStep("Select 'My Folders' from dropdown");
		log.info("INFO: Select 'My Folders' from dropdown");
		ui.waitForElementVisibleWd(ui.createByFromSizzle(FilesUIConstants.pickerMenu), 4);
		ui.selectElementByText(ui.createByFromSizzle(FilesUIConstants.pickerMenu), "My Folders");
		
		logger.strongStep("Select folder "+baseFolder.getName()+" checkbox");
		log.info("INFO: Select folder "+baseFolder.getName()+" checkbox");
		ui.waitForElementVisibleWd(By.xpath(FilesUICnx8.getFolderCheckbox(baseFolderMovedTo.getName())), 4);
		ui.clickLinkWd(By.xpath(FilesUICnx8.getFolderCheckbox(baseFolderMovedTo.getName())));

		logger.strongStep("INFO: Click on 'Move Here' button");
		log.info("INFO: Click on 'Move Here' button");
		ui.clickLinkWaitWd(By.cssSelector(FilesUIConstants.moveHereButton), 5);

		logger.strongStep("Verify message 'The folder was moved to " + baseFolder.getName() + ".");
		log.info("INFO: Verify message 'The folder was moved to  - " + baseFolder.getName() + ".");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.successMessage), 4);
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.successMessage)).getText().equals("The folder was moved to "+folderMovedTo.getTitle()+"."),"");

		logger.strongStep("Delete File");
		log.info("INFO: Delete File");
		apiFileOwnerTestUser.deleteFile(folderMovedTo);
		apiFileOwnerTestUser.deleteFile(folderMovedToBe);
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify 'Add to My drive', 'Stop Following', 'Edit Properties' and 'Delete' functionality of folder properties edit option</li>
	 *<li><B>Step:</B>[API] Create public folder</li>
	 *<li><B>Step:</B>Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B>Click on newly created folder in above step</li>
	 *<li><B>Step:</B>Click on edit option</li>
	 *<li><B>Step:</B>Select 'Add to My drive'' option</li>
	 *<li><B>Verify:</B>Verify success message</li>
	 *<li><B>Step:</B>Select 'Stop Following'' option</li>
	 *<li><B>Verify:</B>Verify message 'You have stopped following this folder:..'</li>
	 *<li><B>Step:</B>Select 'Edit Properties' option</li>
	 *<li><B>Step:</B>Edit the name of folder</li>
	 *<li><B>Step:</B>Select 'Save' button on edit pop up</li>
	 *<li><B>Verify:</B>Verify message 'Folder name was saved successfully'</li>
	 *<li><B>Step:</B>Select 'Delete' option</li>
	 *<li><B>Step:</B>Select 'Delete' button on delete pop up</li>
	 *<li><B>Verify:</B>Verify message 'The folder was deleted successfully'</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T778</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void folderEditProperties() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String editFolderName= "editedFolder_"+Helper.genStrongRand(4);
		ui.startTest();
		
		BaseFile baseFolder = new BaseFile.Builder("BVT_Folder_" + Helper.genStrongRand())
				.shareLevel(ShareLevel.EVERYONE).build(); 
		log.info("INFO: Public folder created successfully");
		logger.strongStep("Public folder created successfully");
		FileEntry pubFolder = apiFileOwnerTestUser.createFolder(baseFolder, Role.READER);
		
		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		// Select newly created folder
		selectFolder(pubFolder);
		
		logger.strongStep("Select 'Add to my drive'");
		log.info("INFO:Select 'Add to my drive'");
		Files_Folder_Dropdown_Menu.ADD_TO_MY_DRIVE.select(ui);

		logger.strongStep("Verify message 'The folder can now be accessed in the root of My Drive.'");
		log.info("INFO: Verify message 'The folder can now be accessed in the root of My Drive.'");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.successMessage), 6);
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.successMessage)).getText().equals("The folder can now be accessed in the root of My Drive."),"The folder can now be accessed in the root of My Drive.");
		
		logger.strongStep("Select 'Stop Following'");
		log.info("INFO:Select 'Stop Following'");
		Files_Folder_Dropdown_Menu.STOP_FOLLOWING.select(ui);
		
		logger.strongStep("Verify message 'You have stopped following this folder: '"+baseFolder.getName());
		log.info("INFO: Verify message 'You have stopped following this folder: '"+baseFolder.getName());
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.successMessage), 6);
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.successMessage)).getText().equals("You have stopped following this folder: "+baseFolder.getName()+"."),"You have stopped following this folder:"+baseFolder.getName());
		
		logger.strongStep("Select 'Edit Properties'");
		log.info("INFO:Select 'Edit Properties'");
		Files_Folder_Dropdown_Menu.EDIT_PROPERTIES.select(ui);
		
		logger.strongStep("Edit folder name to "+editFolderName); 
		log.info("INFO: Edit folder name to  "+editFolderName);
		ui.waitForElementVisibleWd(ui.createByFromSizzle(FilesUIConstants.editPropertiesName), 6);
		ui.findElement(ui.createByFromSizzle(FilesUIConstants.editPropertiesName)).clear();
		ui.typeWithDelayWd(editFolderName, ui.createByFromSizzle(FilesUIConstants.editPropertiesName));
		
		logger.strongStep("INFO: Click on Save button");
		log.info("INFO: Click on Save button");
		ui.clickLinkWd(By.cssSelector(FilesUIConstants.saveButton),"Click on Save button");
		
		logger.strongStep("Verify message '"+editFolderName+" was saved succssfully.'");
		log.info("INFO: Verify message '"+editFolderName+" was saved succssfully.'");
		ui.waitForTextToBePresentInElementWd(By.xpath(FilesUIConstants.successMessage), editFolderName, 6);
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.successMessage)).getText().equals( editFolderName+" was saved successfully."),"Folder was saved successfully.");
		
		logger.strongStep("Select 'Delete'");
		log.info("INFO:Select 'Delete'");
		Files_Folder_Dropdown_Menu.DELETE.select(ui);
		ui.clickLinkWait(FilesUIConstants.DeleteButton);
		
		logger.strongStep("Verify message 'The folder was deleted'");
		log.info("INFO: Verify message 'The folder was deleted' ");
		ui.waitForElementVisibleWd(By.xpath(FilesUIConstants.successMessage), 4);
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.successMessage)).getText().equals("The folder was deleted."),"The folder was deleted");
		
		logger.strongStep("Delete File");
		log.info("INFO: Delete File");
		apiFileOwnerTestUser.deleteFile(pubFolder);

		ui.endTest();
	}
	
	private void selectFolder(FileEntry publicFolder)
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Select 'Folders' from left nav");
		log.info("INFO:Select 'Folders' from left nav");
		ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on the List Display view");
		log.info("INFO: Click on the List Display view");
		Files_Display_Menu.TILE.select(ui);

		logger.strongStep("Click on newly created folder");
		log.info("INFO: Click on " + publicFolder.getTitle() + " folder");
		ui.waitForElementVisibleWd(By.xpath(FilesUICnx8.getFolderFromGridView(publicFolder.getTitle())), 4);
		ui.mouseHoverAndClickWd(ui.findElement(By.xpath(FilesUICnx8.getFolderFromGridView(publicFolder.getTitle()))));

	}

	/**
	 *<ul>
	 *<li><B>Info:</B>Verify file action button tool bar on File Page</li>
	 *<li><B>Step:</B>Log in and Toggle to the new UI</li>
	 *<li><B>Step:</B>Click on Files and Folders from Sec Top Nav</li>
	 *<li><B>Verify:</B>Verify  'Recent' Link is displayed on secondery left Navigation</li>
	 *<li><B>Step:</B>Click on Recent Link from left Navigation</li>
	 *<li><B>Step:</B>Type User name on Search Panel</li>
	 *<li><B>Verify:</B>Verify 'Files belogning to.' text is displayed on after search</li>
	 *<li><B>Step:</B>Clear the Search Panel for new entry</li>
	 *<li><B>Step:</B>Type 'nothing' on Search Panel</li>
	 *<li><B>Step:</B>Click on Search All Content button</li>
	 *<li><B>Step:</B>Click on All Content Filter button</li>
	 *<li><B>Verify:</B>Verify No search result page</li>
	 *<li><B>Step:</B>Click on Communities Filter button</li>
	 *<li><B>Verify:</B>Verify No search result page</li>
	 *<li><B>Step:</B>Click on 'X' icon on search result page</li>
	 *<li><B>Verify:</B>Verify  'Recent' Link is displayed after close the search result page</li>
	 *<li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T784</li>
	 *</ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void searchContentOnFiles() throws Exception
	{
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		logger.strongStep("Click on Files and Folder from Sec Top Nav");
		log.info("INFO: Click on Files and Folder from Sec Top Nav");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.filesAndFoldersSecTopNav), 4, "Click on Files and Folder from Top Nav");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify  'Recent' Link is displayed on secondery left Navigation");
		log.info("INFO: Verify  'Recent' Link is displayed on secondery left Navigation");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUIConstants.recentLink),5),"Recent Link is displayed");
		
		logger.strongStep("Click on Recent Link from left Navigation");
		log.info("INFO: Click on Recent Link from left Navigation");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.recentLink), 4, "Click on Recent Link from left Navigation");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Type User name on Search Panel");
		log.info("INFO: Type User name  on Search Panel");
		ui.typeTextWithDelay(FilesUIConstants.localSearchPanelOnFiles, testUser2.getDisplayName());
		
		logger.strongStep("Verify 'Files belogning to.' text is displayed on after search");
		log.info("INFO: Verify 'Files belogning to.' text is displayed on after search");
		cnxAssert.assertEquals((ui.getElementTextWd(ui.createByFromSizzle(FilesUIConstants.filesBelongingToLabel))),"Files belonging to...", "'Files belonging to...' message is displayed");
		
		logger.strongStep("Clear the Search Panel for new entry");
		log.info("INFO: Clear the Search Panel for new entry");
		ui.findElement(By.xpath(FilesUIConstants.localSearchPanelOnFiles)).clear();
		
		logger.strongStep("Type 'nothing' on Search Panel");
		log.info("INFO: Type 'nothing'  on Search Panel");
		ui.typeTextWithDelay(FilesUIConstants.localSearchPanelOnFiles, "nothing");
		
		logger.strongStep("Click on Search All Content button");
		log.info("INFO: Click on Search All Content button");
		ui.clickLinkWaitWd(ui.createByFromSizzle(FilesUIConstants.searchAllContentBtn), 6, "Click on Search All Content button");
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on All Content Filter button");
		log.info("INFO: Click on All Content Filter button");
		ui.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.allContentFilterButton), 6, "Click on All Content Filter button");
		
		logger.strongStep("Verify No search result page");
		log.info("INFO: Verify No search result page");
		cnxAssert.assertEquals((ui.getElementTextWd(By.xpath(GlobalSearchUIConstants.noSearchResultFound))),"No search result found for given search term", "No search result message is displayed");
		
		logger.strongStep("Click on Communities Filter button");
		log.info("INFO: Click on Communities Filter button");
		ui.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.communitiesFilterButton), 6, "Click on Communities Filter button");
		
		logger.strongStep("Verify No search result page");
		log.info("INFO: Verify No search result page");
		cnxAssert.assertEquals((ui.getElementTextWd(By.xpath(GlobalSearchUIConstants.noSearchResultFound))),"No search result found for given search term", "No search result message is displayed");
		
		logger.strongStep("Click on 'X' icon on search reslut page");
		log.info("INFO: Click on 'X' icon on search reslut page");
		ui.clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.closeIconOnSearchResult), 6, "Click on 'X' icon on search reslut page");
		
		logger.strongStep("Verify  'Recent' Link is displayed after close the search result page");
		log.info("INFO: Verify  'Recent' Link is displayed after close the search result page");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUIConstants.recentLink),5),"Recent Link is displayed");
		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Edit folder's properties and functionality of 'New folder' action item</li>
	 * <li><B>Step:</B>Log in to files and Toggle to the new UI</li>
	 * <li><B>Step:</B>Create Parent folder from UI</li>
	 * <li><B>Step:</B>Select grid view</li>
	 * <li><B>Verify:</B>Verify newly folder is listed in drop-down</li>
	 * <li><B>Step:</B>Click on newly created folder</li>
	 * <li><B>Verify:</B>Verify Folder Edit properties along with bread-crumb option</li>
	 * <li><B>Step:</B>Click on folder's action item drop-down</li>
	 * <li><B>Verify:</B>Verify drop-down gets displayed with all Edit properties option</li>
	 * <li><B>Step:</B>Click 'New Folder' action menu</li>
	 * <li><B>Verify:</B>Verify New folder creation dialog box open</li>
	 * <li><B>Step:</B>Create child_level_1 folder</li>
	 * <li><B>Verify:</B>Verify above Folder gets created and displayed as child folder under Parent Folder</li>
	 * <li><B>Step:</B>Click on newly created child_level_1 folder</li>
	 * <li><B>Verify:</B>Verify edit properties drop-down displayed for child_level_1 folder</li>
	 * <li><B>Verify:</B>Verify bread-crumb hierarchy</li>
	 * <li><B>Step:</B>Now create child_level_2 folder and repeat above 4 steps</li>
	 * <li><B>https://jira.cwp.pnp-hcl.com/secure/Tests.jspa#/testCase/CNXQUTY-T778</li>
	 * </ul>
	 */
	@Test(groups = {"cnx8ui-cplevel2"})
	public void editFolderOptions() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		BaseFolder folder = new BaseFolder.Builder("Parent_Folder" + Helper.genDateBasedRand())
                .description(Data.getData().FolderDescription)
                .access(Access.PUBLIC)
                .build();
		BaseFolder childFolder = new BaseFolder.Builder("Child_Folder_Level1" + Helper.genDateBasedRand())
                .description("First level child")
                .access(Access.PUBLIC)
                .build();
		BaseFolder secondLevelchildFolder = new BaseFolder.Builder("Child_Folder_Level2" + Helper.genDateBasedRand())
                .description("Second level child")
                .access(Access.PUBLIC)
                .build();

		logger.strongStep("Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		log.info("INFO: Load files, login as " + testUser.getEmail() + "and Load new UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser, cfg.getUseNewUI());
		
		logger.strongStep("Click on Folders from left nav");
		log.info("INFO:Click on Folders from left nav");
		ui.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Create Parent folder from UI ");
		log.info("INFO: Create Parent folder from UI");
		fUI.create(folder);
		
		logger.strongStep("Select grid view");
		log.info("INFO:Select grid view");
		ui.clickLinkWd(By.cssSelector(FilesUIConstants.gridView), "Select grid view");
				
		logger.strongStep("Verify newly folder is listed in dropdown");
		log.info("INFO: Verify folder "+folder.getName()+" listed in dropdown");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUICnx8.getFolder(folder.getName()))), "Verify folder listed in dropdown");
		
		logger.strongStep("Click on newly created folder");
		log.info("INFO: Click on "+folder.getName()+" folder");
		ui.clickLinkWaitWd(By.xpath(FilesUICnx8.getFolderFromPeronalView(folder)), 4, "Click on folder");
		
		logger.strongStep("Verify Folder Edit properties along with breadcrumb option");
		log.info("INFO: Verify Folder Edit properties along with breadcrumb option");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.cssSelector(FilesUICnx8.getFolderBreadcrumb(folder)),4), "Verify Folder Edit properties along with breadcrumb option");
		
		logger.strongStep("Click on folder's action item dropdown");
		log.info("INFO: Click on "+folder.getName()+" action item dropdown");
		ui.clickLinkWaitWd(By.xpath(FilesUICnx8.getFolderActionMenuDropdown(folder)), 10, "Select dropdown");

		List<WebElement> listOfActionItems = ui.findElements(By.xpath(FilesUIConstants.folderActionItems));
		List<String> actualActionItems = new ArrayList<>();
		String arrExpectedActionItems[] = { "New Folder... ", "Add Files... ", "Move to... ", "Share... ",
				"Add to My Drive... ", "Stop Following ", "Edit Properties... ", "Delete Folder " };
		List<String> expectedActionItems = new ArrayList<>(Arrays.asList(arrExpectedActionItems));
		for (WebElement actionItemEle : listOfActionItems) {
			String actionItemText = actionItemEle.getAttribute("aria-label");
			actualActionItems.add(actionItemText);
		}
		
		log.info("INFO: List of action items present under dropdown: "+actualActionItems);
		
		logger.strongStep("Verify dropdown gets displayed with all Edit properties option");
		log.info("INFO: Verify dropdown gets displayed with all Edit properties option."+expectedActionItems);
		cnxAssert.assertTrue(actualActionItems.equals(expectedActionItems),"Folder action dropdown contains all expected items");

		// Creates first level child folder
		createChildFolderFromDropdown(childFolder);
		
		// Retrieving list of breadcrumb nodes 
		List<WebElement> breadCrumbNodes = ui.findElements(By.xpath(FilesUIConstants.breadCrumb));
		
		logger.strongStep("Verify the breadcrumb hierarchy ");
		log.info("INFO: Verify the breadcrumb hierarchy :"+folder.getName()+">"+childFolder.getName());
		cnxAssert.assertTrue(breadCrumbNodes.size()==2, "Verify number of breadcrumb nodes are 2");
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.breadCrumb_ParentNode)).getAttribute("title").equals(folder.getName()), "Verify Folder Edit properties breadcrumb option");
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUICnx8.getBreadCrumbNode(2))).getAttribute("title").contains(childFolder.getName()), "Verify Folder Edit properties breadcrumb option");

		logger.strongStep("Click on child folder's action item dropdown");
		log.info("INFO: Click on "+childFolder.getName()+" action item dropdown");
		ui.clickLinkWaitWd(By.xpath(FilesUICnx8.getFolderActionMenuDropdown(childFolder)), 10, "Select dropdown");

		// Create and verify second level child folder
		createChildFolderFromDropdown(secondLevelchildFolder);
		
		// Retrieving list of breadcrumb nodes after adding second level child
		breadCrumbNodes = ui.findElements(By.xpath(FilesUIConstants.breadCrumb));
		
		logger.strongStep("Verify the breadcrumb hierarchy ");
		log.info("INFO: Verify the breadcrumb hierarchy :"+folder.getName()+">"+childFolder.getName()+">"+secondLevelchildFolder.getName());
		cnxAssert.assertTrue(breadCrumbNodes.size()==3, "Verify Folder Edit properties breadcrumb option");
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.breadCrumb_ParentNode)).getAttribute("title").equals(folder.getName()), "Verify Folder Edit properties breadcrumb option");
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUIConstants.breadCrumb_ChildNode)).getAttribute("title").contains(childFolder.getName()), "Verify Folder Edit properties breadcrumb option");
		cnxAssert.assertTrue(ui.findElement(By.xpath(FilesUICnx8.getBreadCrumbNode(3))).getAttribute("title").contains(secondLevelchildFolder.getName()), "Verify Folder Edit properties breadcrumb option");
		
		ui.endTest();
	}
	
	/**
	 * Create and verify new folder from folder's edit properties dropdown 
	 * @param folder
	 */
	public void createChildFolderFromDropdown(BaseFolder folder){
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		logger.strongStep("Click 'New Folder' action menu");
		log.info("INFO: Click 'New Folder' action menu");
		ui.clickLinkWaitWd(By.xpath(FilesUIConstants.newFolderActionMenu), 4, "Click new folder action menu");
		
		logger.strongStep("Verify New folder creation dialog box open");
		log.info("INFO: Verify New folder creation dialog box open");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUIConstants.newFolderDialoguebox)),"Verify New folder creation dialog box open");
		
		logger.strongStep("Create child folder");
		log.info("INFO: Create child folder");
		ui.typeText(FilesUIConstants.CreateFolderName, folder.getName());
		fUI.clickCreateButton();
		ui.waitForPageLoaded(driver);
		ui.fluentWaitTextPresent("Successfully created " + folder.getName() + ".");
		
		logger.strongStep("Verify Folder get created and displayed as child folder under Parent Folder");
		log.info("INFO: Verify Folder get created and displayed as child folder under Parent Folder");
		cnxAssert.assertTrue(ui.isElementDisplayedWd(By.xpath(FilesUICnx8.getFolderFromPeronalView(folder))), "Verify folder is created under parent folder");
		
		logger.strongStep("Click on newly created child folder");
		log.info("INFO: Click on newly created child folder");
		ui.clickLinkWaitWd(By.xpath(FilesUICnx8.getFolderFromPeronalView(folder)), 5, "click on folder");
		
		logger.strongStep("Verify edit properties dropdown displayed for child folder");
		log.info("INFO: Verify edit properties dropdown displayed for child folder");
		cnxAssert.assertTrue(ui.isElementVisibleWd(By.xpath(FilesUICnx8.getFolderActionMenuDropdown(folder)),4), "Verify Folder Edit properties breadcrumb option");
	}
}
