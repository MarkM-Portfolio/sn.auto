package com.ibm.conn.auto.tests.files;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_HCL_Docs extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_HCL_Docs.class);
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;	
	private User testUser,testUser1,testUser2;
	private FilesUI ui;
	private APIFileHandler fileHandler;
	private FileViewerUI fViewerUI;
	private ForumsUI forumsUI;
	private ActivitiesUI actUI;
	private APIActivitiesHandler apiOwner1;

	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		fileHandler = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiOwner1 = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp(){		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		
		forumsUI = ForumsUI.getGui(cfg.getProductName(), driver);
		fViewerUI = FileViewerUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		actUI = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Document is created with correct name and contents</li>
	*<li><B>Step:</B>Load Files and login</li>
	*<li><B>Step:</B>Select Document from 'New' dropdown, Enter name and click on create </li>
	*<li><B>Step:</B>Edit and publish the Document</li> 
	*<li><B>Verify:</B>Verify Document is created in 'My Files' view</li>
	*<li><B>Step:</B>Open Document in FIDO view</li>
	*<li><B>Verify:</B>Verify correct contents displays in preview panel</li>
	*<li><B>Step:</B>Again select the Document from my files view</li> 
	*<li><B>Step:</B>Select edit in Docs option from more menu</li> 
	*<li><B>Step:</B>Edit contents of document and publish it</li> 
	*<li><B>Step:</B>Again select and open document from my files in FIDO View</li> 
	*<li><B>Verify:</B>Verify edited contents displays in preview panel</li>
	*</ul>
	*/ 
	@Test(groups = {"Docs"})
	public void CreateAndEditDocument() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();

		BaseFile file = new BaseFile.Builder(Helper.genDateBasedRand())
									.rename(testname + Helper.genDateBasedRand())
									.extension(".docx")
									.description("This file is of Document Type")
									.build();
		
		file.setName(file.getRename() + file.getExtension());
		
		// Load the component and login
		logger.strongStep("Load Files and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		createEditAndVerifyDoc(file,logger, null, testUser);
		
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify the Mega Menu Option in FIDO</li>
	*<li><B>Step:</B>Load Files and login</li>
	*<li><B>Step:</B>Select Document Type and Create a new Document </li>
	*<li><B>Step:</B>Go to My Files view. Select 'Details' display button</li>
	*<li><B>Step:</B>Open created document </li> 
	*<li><B>Verify:</B>Pin the file and validate the message</li>
	*<li><B>Verify:</B>Un-Pin the file and validate the message</li>
	*<li><B>Verify:</B>Verify Like the file tag is displayed </li>
	*<li><B>Verify:</B>Verify Un-Like the file tag is displayed</li>
	*<li><B>Verify:</B>Verify Download File tag is displayed</li> 
	*<li><B>Verify:</B>Verify More Action tag is displayed</li>
	*<li><B>Verify:</B>Verify More Action options are displayed</li>
	*<li><B>Verify:</B>Verify Hide details panel is displayed </li>
	*<li><B>Verify:</B>Verify Show details panel is displayed</li>
	*<li><B>Verify:</B>Verify Close tag is displayed and Close the FIDO</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void verifyMenuOptionsInFIDO() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();

		BaseFile file = new BaseFile.Builder(Helper.genDateBasedRand())
									.rename(testname + Helper.genDateBasedRand())
				                    .extension(".docx")
				                    .description("This file is of Document Type")
				                    .build();
		
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Select 'Document' from 'New' Dropdown, Enter Doc Name and click on submit");
		log.info("INFO: Select 'Document' from 'New' Dropdown, Enter Doc Name and click on submit");
		ui.waitForPageLoaded(driver);
		ui.createDoc(FilesUIConstants.documentInNewDropdown, FilesUIConstants.docName, file);
		
		logger.strongStep("Edit the created Document and publish");
		log.info("INFO: Edit the created Document and publish");
		ui.editDoc(FilesUIConstants.docBody, file.getRename(), file.getDescription());
		
		logger.strongStep("Verify the New Document file is created");
		log.info("INFO: Verify the New Document file is created");
		verifyDocCreated(file);

		// Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Open created file");
		log.info("INFO: Open created file");
		driver.getFirstElement(ui.getUploadFileName(file)).click();

		// Pin the File
		logger.strongStep("Pin the file and validate the message");
		log.info("INFO: Pin the file and validate message");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.pinIcon), "ERROR: Pin Icon is not displayed");
		ui.clickLinkWithJavascript(FilesUIConstants.pinIcon);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.pinMsg_FIDO).getText(), FilesUIConstants.PinMessage);

		// Unpin the File
		logger.strongStep("Un-Pin the file and validate the message");
		log.info("INFO: Un-Pin the file and validate message");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.unpinIcon), "ERROR: Pin Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.unpinIcon).click();
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.unpinMsg_FIDO).getText(), FilesUIConstants.UnPinMessage);

		// Like the File
		logger.strongStep("Verify Like the file tag is displayed");
		log.info("INFO: Verify Like the file tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.likeIcon), "ERROR:Like Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.likeIcon).click();

		// Unlike the File
		logger.strongStep("Verify Un-Like the file tag is displayed");
		log.info("INFO: Verify Un-Like the file tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.unlikeIcon), "ERROR: Unlike Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.unlikeIcon).click();

		// Download File
		logger.strongStep("Verify Download File tag is displayed");
		log.info("INFO: Verify Download File tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.downloadIcon), "ERROR: Download File Icon is not displayed");

		// More Action
		logger.strongStep("Verify More Action tag is displayed");
		log.info("INFO: Verify More Action tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.moreActionIcon), "ERROR: More Action Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.moreActionIcon).click();

		logger.strongStep("Verify More Action options are displayed");
		log.info("INFO: Verify More Action options are displayed");
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_1).getText(), FilesUIConstants.MenuItem_1);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_2).getText(), FilesUIConstants.MenuItem_2);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_3).getText(), FilesUIConstants.MenuItem_3);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_4).getText(), FilesUIConstants.MenuItem_4);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_5).getText(), FilesUIConstants.MenuItem_5);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_6).getText(), FilesUIConstants.MenuItem_6);
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.moreActionMenuItem_7).getText(), FilesUIConstants.MenuItem_7);

		// Hide and Show Details Panel
		logger.strongStep("Verify Hide details panel is displayed");
		log.info("INFO: Verify Hide details panel is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.hidepanelIcon),"ERROR: Hide details panel Icon is not displayed");
		log.info("INFO: Click on Hide Details Panel Icon");
		driver.getSingleElement(FilesUIConstants.hidepanelIcon).click();

		log.info("INFO: Verify Show details panel is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.showDetailspanelIcon),"ERROR: Show details panel Icon is not displayed");
		log.info("INFO: Click on Hide Details Panel Icon");
		driver.getSingleElement(FilesUIConstants.showDetailspanelIcon).click();

		// Close the FIDO
		logger.strongStep("Verify Close tag is displayed and Close the FIDO");
		log.info("INFO: Verify Close tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.closeIcon), "ERROR:Close tag is not displayed");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Presentation is created with correct name and contents</li>
	*<li><B>Step:</B>Load Files and login</li>
	*<li><B>Step:</B>Select Presentation from 'New' drop-down, Enter name and click on create </li>
	*<li><B>Step:</B>Edit and publish the Presentation</li> 
	*<li><B>Verify:</B>Verify Presentation is created in 'My Files' view</li>
	*<li><B>Step:</B>Open Presentation in FIDO view</li>
	*<li><B>Verify:</B>Verify correct contents displays in preview panel</li>
	*<li><B>Step:</B>Edit the file by opening in 'Edit in Docs'</li>
	*<li><B>Step:</B>Enter some content and publish</li>
	*<li><B>Verify:</B>Verify updated contents displays in preview panel</li>
	*</ul>
	*/
	@Test(groups = {"Docs"})
	public void CreateAndEditPresentation() {		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testname = ui.startTest();
		String EditedContent = "Presentation Subtitle";
		
		BaseFile file = new BaseFile.Builder(testname)
									.rename(testname + Helper.genDateBasedRand())
									.extension(".pptx")
									.description("Presentation Title")
									.build();
		
		file.setName(file.getRename() + file.getExtension());
		
		// Load the component and login
		logger.strongStep("Load Files and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Select 'Presentation' from 'New' dropdown, Enter Doc Name and click on submit");
		log.info("INFO: Select 'Presentation' from 'New' dropdown, Enter Doc Name and click on submit");
		ui.createDoc(FilesUIConstants.pptInNewDropdown, FilesUIConstants.pptNameField, file);
		
		logger.strongStep("Edit the presentation and publish");
		log.info("INFO: Edit the presentation and publish");
		ui.editPresentation(FilesUIConstants.pptTitleBody, file.getRename(), file.getDescription());
		
		ui.logout();

		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser);

		ui.clickMyFilesView();

		Files_Display_Menu.DETAILS.select(ui);

		
		logger.strongStep("Verify the New Presentation file is created");
		log.info("INFO: Verify Presentation file is created");
		verifyDocCreated(file);
			
		logger.strongStep("Verify correct content displays in FIDO view");
		log.info("INFO: Verify correct content displays in FIDO view");
		verifyDocContent(file, FilesUIConstants.pptTitleContentInFIDO);
		
		log.info("INFO: Logout from application");
		ui.logout();

		logger.strongStep("Closing browser instance so that existing sessions of HCL Docs should not be retained");
		log.info("INFO: Closing browser instance so that existing sessions of HCL Docs should not be retained");
		driver.close();

		logger.strongStep("Load Files and component and login to application");
		log.info("INFO: Load Files and component and login to application");
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser);

		logger.strongStep("Click on My files and details view");
		log.info("INFO: Click on My files and details view");
		ui.clickMyFilesView();
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Go to 'More' and click on 'Edit in Docs'");
		log.info("INFO: Go to 'More' and click on 'Edit in Docs'");
		ui.clickLinkWait(ui.fileSpecificMore(file));
		ui.clickLinkWait(ui.docSpecificEdit(file));
		
		logger.strongStep("Enter some content and publish");
		log.info("INFO: Enter some content and publish");
		ui.editPresentation(FilesUIConstants.pptSubtitleBody, file.getRename(), EditedContent);
		
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		Files_Display_Menu.DETAILS.select(ui);
		
		file.setDescription(EditedContent);
		
		logger.strongStep("Verify updated content displays in FIDO view");
		log.info("INFO: Verify updated content displays in FIDO view");
		verifyDocContent(file, FilesUIConstants.pptSubtitleContentInFIDO);
		
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);
				
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create and Open a New Spreadsheet</li>
	*<li><B>Step:</B>Load Files and login</li>
	*<li><B>Step:</B>Select Spreadsheet Type and Create a new Spreadsheet </li>
	*<li><B>Step:</B>Select 'Spreadsheet' from 'New' dropdown, Enter Doc Name and click on submit</li> 
	*<li><B>Verify:</B>Verify the New Spreadsheet file is created</li>
	*</ul>
	*/  
	@Test(groups = {"Docs"})
	public void CreateAndOpenSpreadsheet() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();

		BaseFile file = new BaseFile.Builder(testname).rename(testname + Helper.genDateBasedRand())
									.description("Spreadsheet Entered")
									.extension(".xlsx")
									.build();
		
		file.setName(file.getRename() + file.getExtension());
		
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Select 'Spreadsheet' from 'New' dropdown, Enter Doc Name and click on submit");
		log.info("INFO: Select 'Spreadsheet' from 'New' dropdown, Enter Doc Name and click on submit");
		ui.waitForPageLoaded(driver);
		ui.createDoc(FilesUIConstants.spreadsheetInNewDropdown, FilesUIConstants.spreadsheetName, file);

		logger.strongStep("Edit the Spreadsheet and publish");
		log.info("INFO: Edit the Spreadsheet and publish");
		ui.editSpreadsheet(FilesUIConstants.spreadsheetEntry, file.getRename(), file.getDescription());

		logger.strongStep("Verify the New Spreadsheet file is created");
		log.info("INFO: Verify the New Spreadsheet file is created");
		verifyDocCreated(file);
		
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Presentation file in community is created with correct name and contents</li>
	*<li><B>Step:</B>Create a new community using API</li>
	*<li><B>Step:</B>Load Communities component and login</li>
	*<li><B>Step:</B>Navigate to the created community</li>
	*<li><B>Step:</B>Navigate to Files</li>
	*<li><B>Step:</B>Select Presentation from 'Add' drop-down, Enter name and click on create </li>
	*<li><B>Step:</B>Edit and publish the Presentation</li> 
	*<li><B>Verify:</B>Verify Presentation is created</li>
	*<li><B>Step:</B>Open Presentation in FIDO view</li>
	*<li><B>Verify:</B>Verify correct contents displays in preview panel</li>
	*<li><B>Step:</B>Edit the file by opening in 'Edit in Docs'</li>
	*<li><B>Step:</B>Enter some content and publish</li>
	*<li><B>Verify:</B>Verify updated contents displays in preview panel</li>
	*<li><B>Step:</B>Delete the community via API</li>
	*</ul>
	*/
	@Test(groups = {"Docs"})
	public void CreateCommunityPresentationFile() {		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String EditedContent = "Presentation Subtitle";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		
		BaseFile file = new BaseFile.Builder(Helper.genDateBasedRand())
									.rename(testName + Helper.genDateBasedRand())
									.extension(".pptx")
									.comFile(true)
									.description("Community Presentation Title")
									.build();
		
		file.setName(file.getRename() + file.getExtension());
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		cUI.waitForCommunityLoaded();
		
		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui,2);
		
		logger.strongStep("Select 'Presentation' from 'Add' dropdown, Enter Doc Name and click on submit");
		log.info("INFO: Select 'Presentation' from 'Add' dropdown, Enter Doc Name and click on submit");		
		ui.createDoc(FilesUIConstants.pptInNewDropdown, FilesUIConstants.pptNameField, file);

		logger.strongStep("Edit the presentation and publish");
		log.info("INFO: Edit the presentation and publish");
		ui.editPresentation(FilesUIConstants.pptTitleBody, file.getRename(), file.getDescription());
		
		logger.strongStep("Logout from application");
		log.info("INFO: Logout from application");
		ui.logout();
			
		logger.strongStep("Load communities component and login to application");
		log.info("INFO: Load communities component and login to application");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Verify New Presentation file is created");
		log.info("INFO: Verify Presentation file is created");
		verifyDocCreated(file);
		
		logger.strongStep("Verify correct content displays in FIDO view");
		log.info("INFO: Verify correct content displays in FIDO view");
		verifyDocContent(file, FilesUIConstants.pptTitleContentInFIDO);
		
		logger.strongStep("Logout from application");
		log.info("INFO: Logout from application");
		ui.logout();
		
		logger.strongStep("Closing browser instance so that existing sessions of HCL Docs should not be retained");
		log.info("INFO: Closing browser instance so that existing sessions of HCL Docs should not be retained");
		driver.close();
		
		logger.strongStep("Load communities component and login to application");
		log.info("INFO: Load communities component and login to application");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Go to 'More' and click on 'Edit in Docs'");
		log.info("INFO: Go to 'More' and click on 'Edit in Docs'");
		ui.clickLinkWait(ui.fileSpecificMore(file));
		ui.clickLinkWait(ui.docSpecificEdit(file));
		
		logger.strongStep("Enter some content and publish");
		log.info("INFO: Enter some content and publish");
		ui.editPresentation(FilesUIConstants.pptSubtitleBody, file.getRename(), EditedContent);
		
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		file.setDescription(EditedContent);
		
		logger.strongStep("Verify updated content displays in FIDO view");
		log.info("INFO: Verify updated content displays in FIDO view");
		verifyDocContent(file, FilesUIConstants.pptSubtitleContentInFIDO);
		
		logger.strongStep("Delete Community");
		log.info("INFO: Delete Community");
		apiOwner.deleteCommunity(comAPI);
				
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Document is created in Community files with correct name and contents</li>
	*<li><B>Step:</B>Load Community component and login</li>
	*<li><B>Step:</B>Select Document from 'Add' dropdown, Enter name and click on create </li>
	*<li><B>Step:</B>Edit and publish the Document</li> 
	*<li><B>Verify:</B>Verify Document is created in 'All Community Files' view</li>
	*<li><B>Step:</B>Open Document in FIDO view</li>
	*<li><B>Verify:</B>Verify correct contents displays in preview panel and close FIDO</li>
	*<li><B>Step:</B>Again select the Document from 'All Community Files' view</li> 
	*<li><B>Step:</B>Select edit in Docs option from more menu</li> 
	*<li><B>Step:</B>Edit contents of document and publish it</li> 
	*<li><B>Step:</B>Again select and open document from my files in FIDO View</li> 
	*<li><B>Verify:</B>Verify edited contents displays in preview panel</li>
	*<li><B>Step:</B>Delete Community</li>
	*</ul>
	*/ 
	@Test(groups = {"Docs"})
	public void CreateAndEditCommunityDocument() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName)
				.build();

		BaseFile file = new BaseFile.Builder(testName)
									.rename(testName + Helper.genDateBasedRand())
									.extension(".docx")
									.comFile(true)
									.description("Community Document Title")
									.build();
		
		file.setName(file.getRename() + file.getExtension());

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		cUI.waitForCommunityLoaded();

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui,2);
		
		BaseCommunity bcom = community;
		
		logger.strongStep("Create, Edit and verify the document");
		log.info("INFO: Create, Edit and verify the document");
		createEditAndVerifyDoc(file,logger,bcom,testUser);
		
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Spreadsheet file in community is created with correct name and contents</li>
	*<li><B>Step:</B>Create a new community using API</li>
	*<li><B>Step:</B>Load Communities component and login</li>
	*<li><B>Step:</B>Navigate to the created community</li>
	*<li><B>Step:</B>Navigate to Files</li>
	*<li><B>Step:</B>Select Spreadsheet from 'Add' drop-down, Enter name and click on create </li>
	*<li><B>Step:</B>Edit and publish the Spreadsheet</li> 
	*<li><B>Verify:</B>Verify Spreadsheet is created</li>
	*<li><B>Step:</B>Delete the community via API</li>
	*</ul>
	*/
	@Test(groups = {"Docs"})
	public void CreateCommunitySpreadsheetFile() {		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		
		BaseFile file = new BaseFile.Builder(testName)
									.rename(testName + Helper.genDateBasedRand())
									.extension(".xlsx")
									.comFile(true)
									.description("Spreadsheet")
									.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		logger.strongStep("Select 'Spreadsheet' from 'Add' dropdown, Enter Doc Name and click on submit");
		log.info("INFO: Select 'Spreadsheet' from 'Add' dropdown, Enter Doc Name and click on submit");		
		ui.createDoc(FilesUIConstants.spreadsheetInNewDropdown, FilesUIConstants.spreadsheetName, file);

		logger.strongStep("Edit the Spreadsheet and publish");
		log.info("INFO: Edit the Spreadsheet and publish");
		ui.editSpreadsheet(FilesUIConstants.spreadsheetEntry, file.getRename(), file.getDescription());
		
		logger.strongStep("Verify New Spreadsheet file is created");
		log.info("INFO: Verify Spreadsheet file is created");
		verifyDocCreated(file);
		
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
				
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Mega Menu Option in FIDO for Community file</li>
	*<li><B>Step:</B>Create a community using API</li>
	*<li><B>Step:</B>Load Community component and login</li>
	*<li><B>Step:</B>Navigate to the community</li>
	*<li><B>Step:</B>Navigate to the Files Tab</li>
	*<li><B>Step:</B>Upload a file</li></li>
	*<li><B>Step:</B>Open Uploaded File in FIDO</li>
	*<li><B>Verify:</B>Verify Upload button is displayed</li> 
	*<li><B>Verify:</B>Verify Like the file tag is displayed </li>
	*<li><B>Verify:</B>Verify Un-Like the file tag is displayed</li>
	*<li><B>Verify:</B>Verify Download File tag is displayed</li> 
	*<li><B>Verify:</B>Verify More Action tag is displayed</li>
	*<li><B>Verify:</B>Verify More Action options are displayed</li>
	*<li><B>Verify:</B>Verify Hide details panel is displayed </li>
	*<li><B>Verify:</B>Verify Show details panel is displayed</li>
	*<li><B>Verify:</B>Verify Close tag is displayed and Close the FIDO</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void verifyFIDOMenuOptionsInCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file16)
									.rename(Helper.genDateBasedRand())
				                    .extension(".docx")
				                    .comFile(true)
				                    .build();

		// Use API to create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui,2);
		
		// Upload Community-owned file
		logger.strongStep("Upload a file");
		log.info("INFO: Upload a file");
		file.upload(ui);

		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Open Uploaded File");
		log.info("INFO: Open Uploaded File");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// Upload Button
		logger.strongStep("Verify Upload button is displayed");
		log.info("INFO: Verify Upload button is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.uploadButton), "ERROR:Upload button is not displayed");

		// Like the File
		logger.strongStep("Verify Like the file tag is displayed");
		log.info("INFO: Verify Like the file tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.likeIcon), "ERROR:Like Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.likeIcon).click();

		// Unlike the File
		logger.strongStep("Verify Un-Like the file tag is displayed");
		log.info("INFO: Verify Un-Like the file tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.unlikeIcon), "ERROR: Unlike Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.unlikeIcon).click();

		// Download File
		logger.strongStep("Verify Download File tag is displayed");
		log.info("INFO: Verify Download File tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.downloadIcon), "ERROR: Download File Icon is not displayed");

		// More Action
		logger.strongStep("Verify More Action tag is displayed");
		log.info("INFO: Verify More Action tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.moreActionIcon), "ERROR: More Action Icon is not displayed");
		driver.getSingleElement(FilesUIConstants.moreActionIcon).click();
		
		logger.strongStep("Verify More Action options are displayed");
		log.info("INFO: Verify More Action options are displayed");
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.moreActionMenuItem_2).getText(), FilesUIConstants.MenuItem_2);
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.moreActionMenuItem_3).getText(), FilesUIConstants.MenuItem_3);
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.moreActionMenuItem_6).getText(), FilesUIConstants.MenuItem_6);
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.moreActionMenuItem_7).getText(), FilesUIConstants.MenuItem_7);

		// Hide and Show Details Panel
		logger.strongStep("Verify Hide details panel is displayed and then click hide detail panel");
		log.info("INFO: Verify Hide details panel is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.hidepanelIcon),"ERROR: Hide details panel Icon is not displayed");
		log.info("INFO: Click on Hide Details Panel Icon");
		driver.getSingleElement(FilesUIConstants.hidepanelIcon).click();

		log.info("INFO: Verify Show details panel is displayed and then click show detail panel");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.showDetailspanelIcon),"ERROR: Show details panel Icon is not displayed");
		log.info("INFO: Click on Show Details Panel Icon");
		driver.getSingleElement(FilesUIConstants.showDetailspanelIcon).click();

		// Close the FIDO
		logger.strongStep("Verify Close tag is displayed and Close the FIDO");
		log.info("INFO: Verify Close tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.closeIcon), "ERROR:Close tag is not displayed");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Version tab in FIDO</li>
	*<li><B>Step:</B>Upload a file via API</li>
	*<li><B>Step:</B>Load Files and Login</li>
	*<li><B>Step:</B>Go to My Files view. Select 'Details' display button</li>
	*<li><B>Step:</B>Open uploaded document </li> 
	*<li><B>Verify:</B>Verify that Version tab is displayed and click on link</li> 
	*<li><B>Verify:</B>Verify that Download a new version link is displayed</li> 
	*<li><B>Verify:</B>Verify that Restore version link is displayed</li> 
	*<li><B>Verify:</B>Verify that Delete version link is displayed</li> 
	**<li><B>Verify:</B>Verify Close tag is displayed and Close the FIDO</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void verifyVersionTabInFIDO() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file30)
				.rename(testname + Helper.genDateBasedRandVal2())
                .extension(".dotx")
                .build();
		
		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		// Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Open created file");
		log.info("INFO: Open created file");
		driver.getFirstElement(ui.getUploadFileName(file)).click();
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// Version Tab Validation
		logger.strongStep("Verify that Version tab is displayed and click on link");
		log.info("INFO: Verify that Version tab is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.versionsLink), "ERROR:Version tab is not displayed");
		
		log.info("INFO: Click on Version tab");
		driver.getSingleElement(FilesUIConstants.versionsLink).click();
		
		logger.strongStep("Verify that Upload a new version link is displayed");
		log.info("INFO: Verify that Upload a new version link is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.uploadNewVersionLink), "ERROR: Upload a new Version link is not displayed");
		
		logger.strongStep("Verify that Download a new version link is displayed");
		log.info("INFO: Verify that Download a new version link is displayed");
		driver.getFirstElement(FilesUIConstants.downloadVersionLink).hover();
		Assert.assertEquals(driver.getFirstElement(FilesUIConstants.downloadVersionLink).getAttribute("title"),"Download this version","ERROR: Restore Version link is not displayed");
		
		logger.strongStep("Verify that Restore version link is displayed");
		log.info("INFO: Verify that Restore version link is displayed");
		driver.getFirstElement(FilesUIConstants.userDetailsArea).hover();
		Assert.assertEquals(driver.getFirstElement(FilesUIConstants.restoreVersionLink).getAttribute("title"),"Restore this version","ERROR: Restore Version link is not displayed");
		
		logger.strongStep("Verify that Delete version link is displayed");
		log.info("INFO: Verify that Delete version link is displayed");
		Assert.assertEquals(driver.getFirstElement(FilesUIConstants.deleteVersionLink).getAttribute("title"),"Delete this version","ERROR: Delete Version link is not displayed");

		// Close the FIDO
		logger.strongStep("Verify Close tag is displayed and Close the FIDO");
		log.info("INFO: Verify Close tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.closeIcon), "ERROR:Close tag is not displayed");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the About tab in FIDO</li>
	*<li><B>Step:</B>Upload a file via API</li>
	*<li><B>Step:</B>Load Files and Login</li>
	*<li><B>Step:</B>Go to My Files view. Select 'Details' display button</li>
	*<li><B>Step:</B>Open uploaded document </li> 
	*<li><B>Verify:</B>Verify that About tab is displayed and click on link</li> 
	*<li><B>Verify:</B>Verify that Add a Description link is displayed</li> 
	*<li><B>Step:</B>Click on add a description link, type description and save</li> 
	*<li><B>Verify:</B>Verify that Delete version link is displayed</li> 
	*<li><B>Verify:</B>Verify that Add Tag link is displayed</li> 
	*<li><B>Step:</B>Click on Add Tag link, type tag and save</li>
	*<li><B>Verify:</B>Verify that Likes tag is displayed</li>
	*<li><B>Verify:</B>Verify that Get Links link is displayed</li>
	*<li><B>Verify:</B>Verify that Link to file tag is displayed on Get links pop up</li>
	*<li><B>Verify:</B>Verify that Link to preview tag file is displayed on Get links pop up</li>
	*<li><B>Verify:</B>Verify that Link to download file is displayed on Get links pop up</li>
	*<li><B>Verify:</B>Click on Close button on Get links pop up</li>
	*<li><B>Verify:</B>Verify Close tag is displayed and Close the FIDO</li>
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void verifyAboutTabInFIDO() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file17)
									.rename(testname + Helper.genDateBasedRand())
				                    .extension(".odp")
				                    .build();
		
		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		// Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Open created file");
		log.info("INFO: Open created file");
		driver.getFirstElement(ui.getUploadFileName(file)).click();
				
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// About Tab Validation
		logger.strongStep("Verify that About tab is displayed and click on link");
		log.info("INFO: Verify that About tab is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.aboutLink), "ERROR: About tab is not displayed");
		
		log.info("INFO: Click on About tab");
		driver.getSingleElement(FilesUIConstants.aboutLink).click();
		
		//Add a Description Validation
		logger.strongStep("Verify that Add a Description link is displayed");
		log.info("INFO: Verify that Add a Description link is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.addDescriptionLink), "ERROR: Add a Descrption link is not displayed");
		
		logger.strongStep("Click on add a descrption link, type description and save");
		log.info("INFO: Click on add a descrption link, type description and save");
		driver.getFirstElement(FilesUIConstants.addDescriptionLink).click();
		ui.typeTextWithDelay(FilesUIConstants.addDescriptionArea, testname);
		driver.getFirstElement(FilesUIConstants.addDescriptionSaveBtn).click();
		
		//Add Tag Validation
		logger.strongStep("Verify that Add Tag link is displayed");
		log.info("INFO: Verify that Add Tag link is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.addTagLink), "ERROR: Add Tag link is not displayed");
		
		logger.strongStep("Click on Add Tag link, type tag and save");
		log.info("INFO: Click on Add Tag link, type tag and save");
		driver.getFirstElement(FilesUIConstants.addTagLink).click();
		ui.typeTextWithDelay(FilesUIConstants.addTagArea, "File");
		driver.getFirstElement(FilesUIConstants.addTagSaveBtn).click();
		
		//Like Tag Validation
		logger.strongStep("Verify that Likes tag is displayed");
		log.info("INFO: Verify that Likes tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.likesTag), "ERROR: Likes tag is not displayed");
		
		// Get Links Link Validation
		logger.strongStep("Verify that 'Get Links..' link is displayed");
		log.info("INFO: Verify that Get Links link is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.getLinks), "ERROR: Get Links link is not displayed");
		driver.getFirstElement(FilesUIConstants.getLinks).click();
		
		log.info("INFO: Verify that 'Link to file' tag is displayed on Get links pop up");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.linksToFile), "ERROR: Link to File is not displayed");
		
		log.info("INFO: Verify that 'Link to preview file' tag is displayed on Get links pop up");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.linksToPreviousFile), "ERROR: Link to Preview File is not displayed");
		
		log.info("INFO: Verify that 'Link to download file' tag is displayed on Get links pop up");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.linksToDownloadFile), "ERROR: Link to Download File is not displayed");
		
		log.info("INFO: Click on Close button on Get links pop up");
		ui.getFirstVisibleElement(FilesUIConstants.closeButton).click();
		
		// Close the FIDO
		logger.strongStep("Verify Close tag is displayed and Close the FIDO");
		log.info("INFO: Verify Close tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.closeIcon), "ERROR:Close tag is not displayed");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Verify sharing tab in HCL docs community</li>
	 * <li><B>Step:</B>Create a community using API</li>
	 * <li><B>Step:</B>Create a folder in the same community using API</li>
	 * <li><B>Step:</B>Load Community component and login</li>
	 * <li><B>Step:</B>Navigate to the community</li>
	 * <li><B>Step:</B>Navigate to the Files Tab</li>
	 * <li><B>Step:</B>Upload a file</li></li>
	 * <li><B>Step:</B>Open Uploaded File in FIDO</li>
	 * <li><B>Verify:</B>Verify shared file folder count is set to zero</li>
	 * <li><B>Step:</B>Add file to community folder</li>
	 * <li><B>Verify:</B>Verify shared file folder count is increased by 1 after
	 	file is shared with folder</li>
	 * <li><B>Step:</B>Click on create link and then copy link</li>
	 * <li><B>Step:</B>Paste copied link to comments tab to copy it</li>
	 * <li><B>Step:</B>Open copied link in same browser</li>
	 * <li><B>Verify:</B>Verify name of file in newly opened link</li>
	 * </ul>
	 */
	@Test(groups = { "Docs" })
	public void verifySharingTabHCLDocsCommunity() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();

		BaseFile file = new BaseFile.Builder(Data.getData().file18)
									.rename(testName+Helper.genDateBasedRandVal())
									.extension(".ods")
									.comFile(true)
									.build();

		BaseFile baseFolder = new BaseFile.Builder("Folder_" + testName + Helper.genStrongRand())
										.tags(Helper.genStrongRand())
										.shareLevel(ShareLevel.EVERYONE)
										.comFile(true)
										.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Creating the folder");
		fileHandler.createCommunityFolder(comAPI, baseFolder);
		log.info("INFO: Public folder shared with community created successfully");
		logger.strongStep("Public folder shared with community created successfully");

		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser,comAPI);
		file.setName(file.getRename() + file.getExtension());

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);

		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Open Uploaded File");
		log.info("INFO: Open Uploaded File");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
		
		log.info("INFO: Select Sharing tab from FiDO");
		logger.strongStep("Select Sharing tab from FiDO");
		ui.fluentWaitElementVisible(FilesUIConstants.sharingTabInFiDO);
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

		log.info("INFO: Verify community sharing message for community files");
		logger.strongStep("Verify community sharing message for community files");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.commSharingMsg), FilesUIConstants.commSharingMsgExpected);

		log.info("INFO: Verify file is shared with community file");
		logger.strongStep("Verify file is shared with community file");
		ui.shareFileWithFolder(baseFolder);

		log.info("INFO: Click on create link button and copy link");
		logger.strongStep("Click on create link button and copy link");
		ui.clickLink(FilesUIConstants.createLinkButton);
		ui.clickLink(FilesUIConstants.copyLinkButton);

		// Open Comments Panel to get copied link
		logger.strongStep("Click on 'Comments' tab");
		log.info("INFO: Click on 'Comments' tab");
		ui.clickLinkWait(FilesUIConstants.commentsTabLink);

		logger.strongStep("Paste copied link in comments");
		log.info("INFO: Paste copied link in comments");
		ui.switchToFrameBySelector(FilesUIConstants.commentArea1_iframe);
		ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).click();
		forumsUI.pasteFromClipboard();

		logger.strongStep("Copy file link from comments tab and open it in same browser");
		log.info("INFO: Copy file link from comments tab and open it in same browser");
		String copiedLink = ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).getText();
		((WebDriver) driver.getBackingObject()).navigate().to(copiedLink);

		logger.strongStep("Verify file name in newly opened link");
		log.info("INFO: Verify file name in newly opened link");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.fidoFileName), file.getName());

		// Clean Up: Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Verify sharing tab in HCL docs standalone</li>
	 * <li><B>Step:</B>Create a community using API</li>
	 * <li><B>Step:</B>Create a folder in the same community using API</li>
	 * <li><B>Step:</B>Upload a file</li></li>
	 * <li><B>Step:</B>Load Standalone Files component and login</li>
	 * <li><B>Step:</B>Open file in FIDO view from my files</li>
	 * <li><B>Step:</B>Open sharing tab</li>
	 * <li><B>Step:</B>Share file with user as an editor</li>
	 * <li><B>Verify:</B>Verify editor count is increased by 1 for user</li>
	 * <li><B>Step:</B>remove file shared with user as an editor</li>
	 * <li><B>Step:</B>Share file with user as a reader</li></li>
	 * <li><B>Verify:</B>Verify reader count is increased by 1 for user</li>
	 * <li><B>Step:</B>remove file shared with user as an reader</li>
	 * <li><B>Step:</B>Share file with community as a editor</li></li>
	 * <li><B>Verify:</B>Verify editor count is increased by 1</li>
	 * <li><B>Step:</B>remove file shared with community as an editor</li>
	 * <li><B>Step:</B>Share file with community as a reader</li></li>
	 * <li><B>Verify:</B>Verify reader count is increased by 2</li>
	 * <li><B>Step:</B>remove file shared with community as an reader</li>
	 * <li><B>Step:</B>remove file shared with everyone</li>
	 * <li><B>Verify:</B>Verify shared file folder count is set to zero</li>
	 * <li><B>Step:</B>Add file to community folder</li>
	 * <li><B>Verify:</B>Verify shared file folder count is increased by 1 </li>
	 * <li><B>Step:</B>Click on create link and then copy link</li>
	 * <li><B>Step:</B>Paste copied link to comments tab to copy it</li>
	 * <li><B>Step:</B>Open copied link in same browser</li>
	 * <li><B>Verify:</B>Verify name of file in newly opened link</li>
	 * <li><B>Step:</B>Delete the file via UI</li> 
	 * <li><B>Step:</B>Delete the folder via API</li> 
	 * <li><B>Step:</B>Delete the Community via API</li> 
	 * </ul>
	 */
	@Test(groups = { "Docs" })
	public void verifySharingTab_HCLDocsStandalone() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();

		BaseFile file = new BaseFile.Builder(Data.getData().file19)
									.rename(testName + Helper.genDateBasedRandVal())
									.extension(".pdf")
									.shareLevel(ShareLevel.NO_ONE)
									.build();

		BaseFile baseFolder = new BaseFile.Builder("Folder_" + testName + Helper.genStrongRand())
											.tags(Helper.genStrongRand())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// Create a Folder
		FileEntry publicFolder = fileHandler.createFolder(baseFolder, Role.READER);
		log.info("INFO: Public folder created successfully");
		logger.strongStep("Public folder created successfully");

		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		logger.strongStep("Go to My Files view");
		log.info("INFO: Go to My Files view");
		ui.clickMyFilesView();

		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Open Uploaded File");
		log.info("INFO: Open Uploaded File");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
		
		log.info("INFO: Select Sharing tab from FiDO");
		logger.strongStep("Select Sharing tab from FiDO");
		ui.fluentWaitElementVisible(FilesUIConstants.sharingTabInFiDO);
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);
		
		log.info("INFO: Share file with user as an Editor and reader");
		logger.strongStep("Share file with user as an Editor and reader");
		ui.shareFileWithCommunityOrPeople(testUser1.getDisplayName(),"editor", FilesUIConstants.editorTitle,"user");
		ui.shareFileWithCommunityOrPeople(testUser2.getDisplayName(),"reader", FilesUIConstants.readerTitle,"user");
		
		log.info("INFO: Share file with community as an Editor");
		logger.strongStep("Share file with community as an Editor");
		ui.shareFileWithCommunityOrPeople(community.getName(),"editor", FilesUIConstants.editorTitle,"community");
		
		log.info("INFO: Share file with community as an reader");
		logger.strongStep("Share file with community as an reader");
		ui.shareFileWithCommunityOrPeople(community.getName(),"reader", FilesUIConstants.readerTitle,"community");
			
		log.info("INFO: Verify community sharing message for community files");
		logger.strongStep("Verify community sharing message for community files");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.stadaloneSharingMsg), FilesUIConstants.standaloneSharingMsgExpected);
		
		log.info("INFO: Verify file is shared with community file");
		logger.strongStep("Verify file is shared with community file");
		ui.shareFileWithFolder(baseFolder);
		
		log.info("INFO: Click on create link button and copy link");
		logger.strongStep("Click on create link button and copy link");
		ui.clickLink(FilesUIConstants.createLinkButton);
		ui.clickLink(FilesUIConstants.copyLinkButton);

		// Open Comments Panel to get copied link
		logger.strongStep("Click on 'Comments' tab");
		log.info("INFO: Click on 'Comments' tab");
		ui.clickLinkWait(FilesUIConstants.commentsTabLink);

		logger.strongStep("Paste copied link in comments");
		log.info("INFO: Paste copied link in comments");
		ui.switchToFrameBySelector(FilesUIConstants.commentArea1_iframe);
		ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).click();
		forumsUI.pasteFromClipboard();

		logger.strongStep("Copy file link from comments tab and open it in same browser");
		log.info("INFO: Copy file link from comments tab and open it in same browser");
		String copiedLink = ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).getText();
		((WebDriver) driver.getBackingObject()).navigate().to(copiedLink);

		logger.strongStep("Verify file name in newly opened link");
		log.info("INFO: Verify file name in newly opened link");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.fidoFileName), file.getName());
		
		logger.strongStep("Close file open in FIDO");
		log.info("INFO: Close file open in FIDO");
		ui.clickLinkWait(FilesUIConstants.closeIcon);
		
		logger.strongStep("Go to My Files view");
		log.info("INFO: Go to My Files view");
		ui.clickMyFilesView();

		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		// Clean Up: Delete File
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		// Delete folder
		logger.strongStep("Delete Folder");
		log.info("INFO: Delete Folder");
		fileHandler.deleteFolder(publicFolder);
		
		// Clean Up: Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Post/Edit/Delete comments in Comment Panel of FIDO for Community files</li>
	*<li><B>Step:</B>Create a Community using API</li>
	*<li><B>Step:</B>Add a File in Community using API</li>
	*<li><B>Step:</B>Load Community component and login</li>
	*<li><B>Step:</B>Navigate to the community</li>
	*<li><B>Step:</B>Navigate to the Files Tab</li>
	*<li><B>Step:</B>Open Uploaded File in FIDO</li>
	*<li><B>Step:</B>Click on Comments panel</li>
	*<li><B>Step:</B>Post a comment</li>  
	*<li><B>Verify:</B>Comment is displayed</li>
	*<li><B>Step:</B>Edit that comment</li>
	*<li><B>Verify:</B>Edited Comment is displayed</li> 
	*<li><B>Step:</B>Delete the comment</li>
	*<li><B>Verify:</B>No comment is displayed</li>
	*<li><B>Verify:</B>'Feed for these comments' Link is displayed</li>
	*<li><B>Step:</B>Close FIDO View</li>
	*<li><B>Step:</B>Delete the community</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void verifyFIDOCommentTabInCommunity() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		

		BaseFile file = new BaseFile.Builder(Data.getData().file30)
									.rename(testName+Helper.genDateBasedRandVal())
				                    .extension(".dotx")
				                    .comFile(true)
				                    .build();
		
		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser,comAPI);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		cUI.waitForCommunityLoaded();

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui,2);
		
		VerifyCommentsInFIDO(file, logger);
		
		// Clean Up: Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify Post/Edit/Delete comments in Comment Panel of FIDO for Standalone Files</li>
	*<li><B>Step:</B>Add a File using API</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Navigate to the 'My Files' View and open details view</li>
	*<li><B>Step:</B>Open Uploaded File in FIDO</li>
	*<li><B>Step:</B>Click on Comments panel</li>
	*<li><B>Step:</B>Post a comment</li>  
	*<li><B>Verify:</B>Comment is displayed</li>
	*<li><B>Step:</B>Edit that comment</li>
	*<li><B>Verify:</B>Edited Comment is displayed</li> 
	*<li><B>Step:</B>Delete the comment</li>
	*<li><B>Verify:</B>No comment is displayed</li>
	*<li><B>Verify:</B>'Feed for these comments' Link is displayed</li>
	*<li><B>Step:</B>Close FIDO View</li>
	*<li><B>Step:</B>Delete the file</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void verifyCommentTabInFIDO() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file21)
									.rename(testName + Helper.genDateBasedRandVal())
				                    .extension(".pptx")
				                    .build();
		
		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Go to My Files view");
		log.info("INFO: Go to My Files view");
		ui.clickMyFilesView();
		
		VerifyCommentsInFIDO(file, logger);
		
		// Clean Up: Delete File
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create a document from a document Template file of '.dotx' type,
	*				 Edit and verify the content in created file in Standalone Files</li>
	*<li><B>Step:</B>Add a File using API</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Navigate to the 'My Files' View and open grid view</li>
	*<li><B>Verify:</B>Verify the file is displayed in grid view</li>
	*<li><B>Step:</B>Open Uploaded File in FIDO</li>
	*<li><B>Step:</B>Click 'Create File' from Upload dropdown</li>
	*<li><B>Step:</B>Enter some content in created file and publish it</li>  
	*<li><B>Step:</B>Close the FIDO</li>
	*<li><B>Step:</B>Go to 'My Files' view and click Grid view</li>
	*<li><B>Verify:</B>Verify Uploaded and Created files is visible</li> 
	*<li><B>Verify:</B>Verify the added content in created file</li>
	*<li><B>Step:</B>Delete the files</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void CreateDocumentFromTemplateFile() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file30)
									.rename(testName + Helper.genDateBasedRandVal2())
				                    .extension(".dotx")
				                    .build();
		
		BaseFile file1 = new BaseFile.Builder(Helper.genDateBasedRand())
									.rename("New" + Helper.genDateBasedRand())
									.extension(".docx")
									.description("Edited Page")
									.build();
		
		file1.setName(file1.getRename() + file1.getExtension());
		
		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Go to My Files view, Select 'Grid' view button");
		log.info("INFO: Go to My Files view, Select 'Grid' view button");
		ui.clickMyFilesView();
		Files_Display_Menu.TILE.select(ui);
		
		// Verify Thumbnail for file is shown in Gallery
		logger.strongStep("Verify file is displayed in grid view");
		log.info("INFO: Verify file is displayed in grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "File:" + file.getName() + " is not shown");
		
		logger.strongStep("Click on File's Thumbnail to open in FIDO view");
		log.info("INFO: Click on File's Thumbnail to open in FIDO view");
		ui.clickLink(ui.getFileThumbnail(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// Create a new file from uploaded Template file
		logger.strongStep("Select 'Create File' from Upload dropdown");
		log.info("INFO: Select 'Create File' from Upload dropdown");
		ui.createFileFromUploadDropdown(file1);
		
		logger.strongStep("Edit the created document from Template file, add a content and publish it");
		log.info("INFO: Edit the created document from Template file, add a content and publish it");
		ui.editDoc(FilesUIConstants.docBody, file1.getRename(), file1.getDescription());
		
		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Go to My Files view, Select 'Grid' view button");
		log.info("INFO: Go to My Files view, Select 'Grid' view button");
		ui.clickMyFilesView();
		Files_Display_Menu.TILE.select(ui);
		
		//Verify Thumbnails of both files
		logger.strongStep("Verify both uploaded and created Files are visible on grid view");
		log.info("INFO: Verify both uploaded and created Files are visible on grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "File:" + file.getName() + " is not shown");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file1)), "File:" + file1.getName() + " is not shown");
		
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Verify Added content in the file created from template");
		log.info("INFO: Verify Added content in the file created from template");
		verifyDocContent(file1, FilesUIConstants.docBody);
		
		// Clean Up: Delete All Files
		logger.strongStep("Move files to trash and Empty the trash");
		log.info("INFO: Move files to trash and Empty the trash");
		file1.trash(ui);
		file.trash(ui);
		file.delete(ui);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create a Presentation from a Presentation Template file of '.potx' type,
	*				 Edit and verify the content in created file in Standalone Files</li>
	*<li><B>Step:</B>Add a File using API</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Navigate to the 'My Files' View and open grid view</li>
	*<li><B>Verify:</B>Verify the file is displayed in grid view</li>
	*<li><B>Step:</B>Open Uploaded File in FIDO</li>
	*<li><B>Step:</B>Click 'Create File' from Upload dropdown</li>
	*<li><B>Step:</B>Enter some content in created file and publish it</li>  
	*<li><B>Step:</B>Close the FIDO</li>
	*<li><B>Step:</B>Go to 'My Files' view and click Grid view</li>
	*<li><B>Verify:</B>Verify Uploaded and Created both files is visible</li> 
	*<li><B>Verify:</B>Verify the added content in created file</li>
	*<li><B>Step:</B>Delete the files</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void CreatePresentationFromTemplateFile() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file29)
									.rename(testName + Helper.genDateBasedRandVal2())
				                    .extension(".potx")
				                    .build();
		
		BaseFile file1 = new BaseFile.Builder(Helper.genDateBasedRand())
									.rename("New" + Helper.genDateBasedRand())
									.extension(".pptx")
									.description("Edited Title")
									.build();
		
		file1.setName(file1.getRename() + file1.getExtension());
		
		// Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		// Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Go to My Files view, Select 'Grid' view button");
		log.info("INFO: Go to My Files view, Select 'Grid' view button");
		ui.clickMyFilesView();
		Files_Display_Menu.TILE.select(ui);
		
		// Verify Thumbnail of file is shown in Gallery
		logger.strongStep("Verify file is displayed in grid view");
		log.info("INFO: Verify file is displayed in grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "File:" + file.getName() + " is not shown");
		
		logger.strongStep("Click on File's Thumbnail to open in FIDO view");
		log.info("INFO: Click on File's Thumbnail to open in FIDO view");
		ui.clickLink(ui.getFileThumbnail(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// Create a new file from uploaded Template file
		logger.strongStep("Select 'Create File' from Upload dropdown");
		log.info("INFO: Select 'Create File' from Upload dropdown");
		ui.createFileFromUploadDropdown(file1);
		
		logger.strongStep("Edit presentation file created from Template file, add a content and publish it");
		log.info("INFO: Edit presenatation file created from Template file, add a content and publish it");
		ui.editPresentation(FilesUIConstants.pptTitleBody, file1.getRename(), file1.getDescription());
		
		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Go to My Files view, Select 'Grid' view button");
		log.info("INFO: Go to My Files view, Select 'Grid' view button");
		ui.clickMyFilesView();
		Files_Display_Menu.TILE.select(ui);
		
		//Verify Thumbnails of both files
		logger.strongStep("Verify both uploaded and created Files are visible on grid view");
		log.info("INFO: Verify both uploaded and created Files are visible on grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "File:" + file.getName() + " is not shown");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file1)), "File:" + file1.getName() + " is not shown");
		
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Verify Added content in the file created from template");
		log.info("INFO: Verify Added content in the file created from template");
		verifyDocContent(file1, FilesUIConstants.pptTitleContentInFIDO);
				
		// Clean Up: Delete All Files
		logger.strongStep("Move files to trash and Empty the trash");
		log.info("INFO: Move files to trash and Empty the trash");
		file1.trash(ui);
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Create and Verify a Spreadsheet from a Spreadsheet Template file of '.ots' type in community</li>
	*<li><B>Step:</B>Create a community using API</li>
	*<li><B>Step:</B>Load Community component and login</li>
	*<li><B>Step:</B>Navigate to the Community</li>
	*<li><B>Step:</B>Navigate to Files tab navigation menu</li>
	*<li><B>Step:</B>Add a Spreadsheet template file of '.ots' type</li>
	*<li><B>Step:</B>Click on Grid view</li>
	*<li><B>Verify:</B>Verify the file is displayed in grid view</li>
	*<li><B>Step:</B>Open Uploaded File in FIDO</li>
	*<li><B>Step:</B>Click 'Create File' from Upload dropdown</li>
	*<li><B>Step:</B>Enter some content in created file and publish it</li>  
	*<li><B>Step:</B>Close the FIDO</li>
	*<li><B>Step:</B>Select 'Grid' view button</li>
	*<li><B>Verify:</B>Verify Uploaded file is visible</li>
	*<li><B>Verify:</B>Verify Created file is visible</li> 
	*<li><B>Step:</B>Delete the Community via API</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void CreateSpreadsheetFromTemplateFileInCommunity() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file31)
									.rename(testName)
				                    .extension(".ots")
				                    .comFile(true)
				                    .build();
		
		BaseFile file1 = new BaseFile.Builder(Helper.genDateBasedRand())
									.rename("New" + Helper.genDateBasedRand())	
									.extension(".ods")
									.description("Edited Spreadsheet")
									.comFile(true)
									.build();
		
		file1.setName(file1.getRename() + file1.getExtension());
		
		// create community
		logger.strongStep("Create a Community vie API");
		log.info("INFO: Create a Community via API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);

		logger.strongStep("Select Files from the tabbed navigation menu");
		log.info("INFO: Select Files from the tabbed navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		logger.strongStep("Add file:" + file.getName() + " in community");
		log.info("INFO: Add file:" + file.getName() + " in community");
		file.upload(ui);
		
		logger.strongStep("Select 'Grid' view button");
		log.info("INFO: Select 'Grid' view button");
		Files_Display_Menu.TILE.select(ui);
		
		// Verify Thumbnail of file is shown in Gallery
		logger.strongStep("Verify file is displayed in grid view");
		log.info("INFO: Verify file is displayed in grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "file:" + file.getName() + " is not shown");
		
		logger.strongStep("Click on File's Thumbnail to open in FIDO view");
		log.info("INFO: Click on File's Thumbnail to open in FIDO view");
		ui.clickLinkWait(ui.getFileThumbnail(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// Create a new file from uploaded Template file
		logger.strongStep("Select 'Create File' from Upload dropdown");
		log.info("INFO: Select 'Create File' from Upload dropdown");
		ui.createFileFromUploadDropdown(file1);
		
		logger.strongStep("Edit Spreadsheet file created from Template file, add a content and publish it");
		log.info("INFO: Edit Spreadsheet file created from Template file, add a content and publish it");
		ui.editSpreadsheet(FilesUIConstants.spreadsheetEntry, file1.getRename(), file1.getDescription());
		
		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Select 'Grid' view button");
		log.info("INFO: Select 'Grid' view button");
		Files_Display_Menu.TILE.select(ui);
		
		//Verify Thumbnails of both files
		logger.strongStep("Verify both uploaded and created Files are visible on grid view");
		log.info("INFO: Verify both uploaded and created Files are visible on grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "Spreadsheet Template file:" + file.getName() + " is not shown");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file1)), "Created file:" + file1.getName() + " is not shown");
		
		// Clean Up: Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify a file uploaded in activity entry can be open in FIDO view</li>
	 *<li><B>Step:</B> Upload a file via API as a Pre-requisite of this test case</li>
	 *<li><B>Step:</B> Create an Activity using API</li>
	 *<li><B>Step:</B> Load Activities and Log In</li>
	 *<li><B>Step:</B> Open the Activity</li>
	 *<li><B>Step:</B> Create a new Entry with Link to file</li>
	 *<li><B>Step:</B> Click on 'View Details' link for recently added File via 'Link to File'</li>
	 *<li><B>Verify:</B> Verify the file title on FIDO view</li>
	 *</ul>
	 */
	@Test(groups = {"Docs"})
	public void fidoViewforFileAddedInActivity() {	
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		

		BaseFile file = new BaseFile.Builder(Data.getData().file16)
									.extension(".docx")
									.rename(testName+Helper.genDateBasedRand())
									.build();
			
		// Pre-requisite: Upload a standalone file for the test user,
		//same file will be added as link to file as Activity Entry
		logger.strongStep("Upload a file via API as a Pre-requisite of this test case");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(testName)
												.goal("Goal for "+ testName)
												.build();
		
		BaseActivityEntry entry = BaseActivityEntry.builder(testName + " entry" + Helper.genDateBasedRandVal())
												   .tags(Helper.genDateBasedRandVal())
												   .addFile(Data.getData().file15)
												   .addLinkToFile(file.getName())
												   .description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												   .build();
		
		logger.strongStep("Create an Activity using API");
		log.info("INFO: Create an Activity using API");
		activity.createAPI(apiOwner1);
		
		// Load the component and login
		logger.strongStep("Load Activities and Log In as:" + testUser.getDisplayName());
		log.info("INFO: Load Activities and Log In as:" + testUser.getDisplayName());
		actUI.loadComponent(Data.getData().ComponentActivities);
		actUI.login(testUser);

		logger.strongStep("Open the Activity");
		log.info("INFO: Open the Activity");
		actUI.clickLinkWait(ActivitiesUI.getActivityLink(activity));

		//Add entry with Link to file
		log.info("INFO: Create a new Entry with Link to file");
		logger.strongStep("Create a new Entry with Link to file");
		entry.create(actUI);
		
		actUI.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		
		//Click on View Details link
		logger.strongStep("Click on 'View Details' link for recently added File via 'Link to File'");
		log.info("INFO: Click on 'View Details' link for recently added File via 'Link to File'");
		actUI.clickLinkWait(ActivitiesUIConstants.viewDetails);
		
		logger.strongStep("Verify the file title on FIDO view");
		log.info("INFO:Verify the file title on FIDO view");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.fileViewerTitle), file.getName());
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify view of a Non Template Document file of '.odt' type</li>
	*<li><B>Step:</B>Add a File of '.odt' type using API</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Navigate to the 'My Files' View and open grid view</li>
	*<li><B>Verify:</B>Verify the file is displayed in grid view</li>
	*<li><B>Step:</B>Click on File's Thumbnail to open in FIDO view</li>
	*<li><B>Verify:</B>Verify the file title on FIDO view</li>  
	*<li><B>Step:</B>Close the FIDO</li>
	*<li><B>Step:</B>Select 'Details' display button</li> 
	*<li><B>Step:</B>Delete the files</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void viewNonTemplateDocumentTypeODT() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file25)
									.rename(testName + Helper.genDateBasedRand())
									.extension(".odt")
									.build();
		
        // Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

        // Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		logger.strongStep("Go to My Files view, Select 'Grid' view button");
		log.info("INFO: Go to My Files view, Select 'Grid' view button");
		ui.clickMyFilesView();
		Files_Display_Menu.TILE.select(ui);

        // Verify Thumbnail for file is shown in Gallery
		logger.strongStep("Verify file is displayed in grid view");
		log.info("INFO: Verify file is displayed in grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "ERROR: File " + file.getName() + " is not shown");

		logger.strongStep("Click on File's Thumbnail to open in FIDO view");
		log.info("INFO: Click on File's Thumbnail to open in FIDO view");
		ui.clickLink(ui.getFileThumbnail(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		logger.strongStep("Verify the file title on FIDO view");
		log.info("INFO:Verify the file title on FIDO view");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.fileViewerTitle), file.getName());

		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);

		// Clean Up: Delete All Files
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify view of a Non Template Presentation file of '.odp' type</li>
	*<li><B>Step:</B>Add a File of '.odp' type using API</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Navigate to the 'My Files' View and open grid view</li>
	*<li><B>Verify:</B>Verify the file is displayed in grid view</li>
	*<li><B>Step:</B>Click on File's Thumbnail to open in FIDO view</li>
	*<li><B>Verify:</B>Verify the file title on FIDO view</li>  
	*<li><B>Step:</B>Close the FIDO</li>
	*<li><B>Step:</B>Select 'Details' display button</li> 
	*<li><B>Step:</B>Delete the files</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void viewNonTemplatePresentationTypeODP() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file17)
									.rename(testName + Helper.genDateBasedRand())
									.extension(".odp")
									.build();
		
        // Upload a file
		logger.strongStep("Upload a file via API");
		fViewerUI.upload(file, testConfig, testUser);
		file.setName(file.getRename() + file.getExtension());

        // Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		logger.strongStep("Go to My Files view, Select 'Grid' view button");
		log.info("INFO: Go to My Files view, Select 'Grid' view button");
		ui.clickMyFilesView();
		Files_Display_Menu.TILE.select(ui);

        // Verify Thumbnail for file is shown in Gallery
		logger.strongStep("Verify file is displayed in grid view");
		log.info("INFO: Verify file is displayed in grid view");
		Assert.assertTrue(driver.isElementPresent(ui.getFileThumbnail(file)), "ERROR: File " + file.getName() + " is not shown");

		logger.strongStep("Click on File's Thumbnail to open in FIDO view");
		log.info("INFO: Click on File's Thumbnail to open in FIDO view");
		ui.clickLink(ui.getFileThumbnail(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		logger.strongStep("Verify the file title on FIDO view");
		log.info("INFO:Verify the file title on FIDO view");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.fileViewerTitle), file.getName());

		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();

		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);

		// Clean Up: Delete All Files
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify uploaded files in community files by clicking on 'View All' link 
	*				in file summary widget on overview page</li>
	*<li><B>Step:</B>Create a Community using API</li>
	*<li><B>Step:</B>Add multiple types of Files in Community using API</li>
	*<li><B>Step:</B>Load Community component and login</li>
	*<li><B>Step:</B>Navigate to the community</li>
	*<li><B>Step:</B>Scroll down and Click on 'View All' link</li>
	*<li><B>Step:</B>Open 'Details' view panel</li>
	*<li><B>Verify:</B>Verify All files are visible</li>
	*<li><B>Step:</B>Delete the community</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void fileWidgetViewAllLink() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.tags(Data.getData().commonTag + Helper.genDateBasedRandVal2())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName)
													.build();
		
		BaseFile file1 = new BaseFile.Builder(Data.getData().file2)
									.rename("Image" + Helper.genDateBasedRandVal())
				                    .extension(".jpg")
				                    .comFile(true)
				                    .build();
		
		BaseFile file2 = new BaseFile.Builder(Data.getData().file24)
									.rename("Document" + Helper.genDateBasedRandVal())
									.extension(".doc")
									.comFile(true)
									.build();
		
		BaseFile file3 = new BaseFile.Builder(Data.getData().file21)
									.rename("Presentation" + Helper.genDateBasedRandVal())
									.extension(".pptx")
									.comFile(true)
									.build();
		
		BaseFile file4 = new BaseFile.Builder(Data.getData().file23)
									.rename("Spreadsheet" + Helper.genDateBasedRandVal())
									.extension(".xlsx")
									.comFile(true)
									.build();
		
		file1.setName(file1.getRename() + file1.getExtension());
		file2.setName(file2.getRename() + file2.getExtension());
		file3.setName(file3.getRename() + file3.getExtension());
		file4.setName(file4.getRename() + file4.getExtension());

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add different type of files in community via API");
		log.info("INFO: Add different type of files in community via API");		
		community.addFileAPI(comAPI, file1, apiOwner, fileHandler);
		community.addFileAPI(comAPI, file2, apiOwner, fileHandler);
		community.addFileAPI(comAPI, file3, apiOwner, fileHandler);
		community.addFileAPI(comAPI, file4, apiOwner, fileHandler);

		// Load the component and login
		logger.strongStep("Load Communities component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Communities component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click on the 'View All' link from Files summary widget");
		log.info("INFO: Click on the 'View All' link from Files summary widget");
		ui.clickLinkWait(FilesUIConstants.fileViewAllLink);
		
		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Validate that All uploaded Files are visible");
		log.info("INFO: Validate that All uploaded Files are visible");
		Assert.assertTrue(driver.isElementPresent(ui.getUploadFileName(file1)), "ERROR: Unable to find the file " + file1.getName());
		Assert.assertTrue(driver.isElementPresent(ui.getUploadFileName(file2)), "ERROR: Unable to find the file " + file2.getName());
		Assert.assertTrue(driver.isElementPresent(ui.getUploadFileName(file3)), "ERROR: Unable to find the file " + file3.getName());
		Assert.assertTrue(driver.isElementPresent(ui.getUploadFileName(file4)), "ERROR: Unable to find the file " + file4.getName());
		
		// Clean Up: Delete the community
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify a file is opening in FIDO view by clicking on file name link in prompted message 
	*				which appears after uploading a file</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Upload a file</li>
	*<li><B>Verify:</B>Verify uploaded successful message/li>
	*<li><B>Step:</B>Click on File link present in prompted message to open in FIDO</li>
	*<li><B>Verify:</B>Verify the title of file is correct</li> 
	*<li><B>Step:</B>Close FIDO View</li>
	*<li><B>Step:</B>Click on My Files and Switch to details view</li>
	*<li><B>Step:</B>Delete the file</li> 
	*</ul>
	*/  	
	@Test(groups = {"Docs"})
	public void fileNameLinkInPromptedMessage() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file23)
									.rename(testName+Helper.genDateBasedRandVal())
				                    .extension(".xlsx")
				                    .build();
		

		// Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Upload a file:" + file.getName());
		log.info("INFO: Upload a file:" + file.getName());
		file.upload(ui);
		file.setName(file.getRename() + file.getExtension());

		logger.strongStep("Verify that the message 'Successfully uploaded' displays");
		log.info("INFO: Verify that the message 'Successfully uploaded' displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().UploadMessage + file.getName()),
				"ERROR: Successfully uploaded message is not displayed");
		
		logger.strongStep("Click on" + file.getName() + "link in prompted message to open in FIDO");
		log.info("INFO: Click on" + file.getName() + "link in prompted message to open in FIDO");
		ui.clickLinkWithJavascript(ui.getFileNameFromUploadMessage(file));
		
		logger.strongStep("Verify the File title in FIDO view");
		log.info("INFO:Verify the File title in FIDO view");
		Assert.assertEquals(ui.getElementText(FilesUIConstants.fileViewerTitle), file.getName());
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		// Switch the display from default Tile to Details
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		ui.clickMyFilesView();
		Files_Display_Menu.DETAILS.select(ui);
		
		// Clean Up: Delete File
		logger.strongStep("Move file to trash and Empty the trash");
		log.info("INFO: Move file to trash and Empty the trash");
		file.trash(ui);
		file.delete(ui);

		ui.endTest();
	}
	
	/**
	 * VerifyCommentsTabInFIDO(): This method will Post, edit and delete a comment in FIDO view
	 * @param BaseFile - file
 	 * @param DefectLogger - logger
	 */
	private void VerifyCommentsInFIDO(BaseFile file, DefectLogger logger) {
		String comment = "This is comment";
		String commentEdited = "Edited Comment";
		
		// Switch the display from default Tile to Details
		logger.strongStep("Select 'Details' display button");
		log.info("INFO: Select 'Details' display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		logger.strongStep("Open Uploaded File");
		log.info("INFO: Open Uploaded File");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(file));
		
		logger.strongStep("Verify if preview not available message is displayed");
		log.info("INFO: Verify if preview not available message is displayed");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.previewnotAvailableMessage),"Preview for the file is visible in the FIDO Viewer");
				
		// Open Comments Panel
		logger.strongStep("Click on 'Comments' tab");
		log.info("INFO: Click on 'Comments' tab");
		ui.clickLinkWait(FilesUIConstants.commentsTabLink);
		
		// Post a Message
		logger.strongStep("Add a comment and post the comment");
		log.info("INFO: Add a comment and post the comment");
		ui.switchToFrameBySelector(FilesUIConstants.commentArea1_iframe);
		ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).click();
		ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).type(comment);
		ui.switchToTopFrame();
		ui.clickLinkWait(FilesUIConstants.commentSaveBtn);
		
		logger.strongStep("Verify Comment is Visible");
		log.info("INFO: Verify Comment is Visible");
		Assert.assertTrue(ui.isTextPresent(comment), "Comment is not displayed");
		
		// Edit Comment and post
		logger.strongStep("Click on 'Edit this comment' icon, Edit the comment and Post");
		log.info("INFO: Click on 'Edit this comment' icon, Edit the comment and Post");
		ui.clickLinkWait(FilesUIConstants.commentEditBtn);
		ui.switchToFrameBySelector(FilesUIConstants.commentArea2_iframe);
		ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).clear();
		ui.getFirstVisibleElement(FilesUIConstants.CommentTextArea).type(commentEdited);
		ui.switchToTopFrame();
		ui.getFirstVisibleElement(FilesUIConstants.commentSaveBtn).click();
		
		logger.strongStep("Verify Edited comment is Visible");
		log.info("INFO: Verify Edited comment is Visible");
		Assert.assertTrue(ui.isTextPresent(commentEdited), "Edited comment is not displayed");
		
		// Delete Comment
		logger.strongStep("Click on 'Delete this comment' icon");
		log.info("INFO: Click on Delete this comment icon");
		ui.clickLinkWait(FilesUIConstants.commentDeleteBtn);
		ui.clickLinkWait(FilesUIConstants.commentDeleteOkBtn);
		
		logger.strongStep("Verify no comment is displayed");
		log.info("INFO: Verify no comment is displayed");
		Assert.assertTrue(ui.isTextPresent("There are no comments."));
		
		//Check 'Feed for these comments' Link
		logger.strongStep("Verify 'Feed for these comments' Link is displayed");
		log.info("INFO: Verify 'Feed for these comments' Link is displayed");
		Assert.assertTrue(driver.getFirstElement(FilesUIConstants.commentFeedLink).isDisplayed());
		
		// Close the FIDO
		logger.strongStep("Close the FIDO");
		log.info("INFO: Close the FIDO");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
	}
		
	/**
	 * verifyDocCreated(): This method will verify the Document is Visible
	 * @param BaseFile - file
	 */
	private void verifyDocCreated(BaseFile file) {
		// Switch the display from default Tile to Details
		if(!file.getComFile()) {
			log.info("Go to My Files view");
			ui.clickMyFilesView();
		}		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
		
		log.info("INFO: Validate that file: "+ file.getName() + " is visible");
		Assert.assertTrue(driver.isElementPresent(ui.getUploadFileName(file)),
				"ERROR: Unable to find the file " + file.getName());

	}

	/**
	 * verifyDocContent(): This method will verify the content of document
	 * @param BaseFile - File
 	 * @param String - Body
	 */
	private void verifyDocContent(BaseFile file, String body) {

		driver.getFirstElement(ui.getUploadFileName(file)).click();
		ui.switchToFrameByTitle(FilesUIConstants.docViewer_iFrame);
		if (file.getExtension().contains(".docx")) {
			ui.switchToFrameByTitle(FilesUIConstants.docEditor_iFrame);
		}
		Assert.assertEquals(ui.getElementText(body), file.getDescription());
		ui.switchToTopFrame();
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
	}
	
	/**
	 * createEditAndVerifyDoc(): This method will verify create, edit and verify the document of type docx
	 * @param testUser3 
	 * @param bcom 
	 * @param BaseFile - file
 	 * @param DefectLogger - logger
	 */
	private void createEditAndVerifyDoc(BaseFile file, DefectLogger logger, BaseCommunity bcom, User testUser3) {
		String EditedContent = file.getDescription()+" Edited";
		
		ui.waitForPageLoaded(driver);

		logger.strongStep("Select 'Document' from 'New' dropdown, Enter Doc Name and click on submit");
		log.info("INFO: Select 'Document' from 'New' dropdown, Enter Doc Name and click on submit");
		ui.createDoc(FilesUIConstants.documentInNewDropdown, FilesUIConstants.docName, file);

		logger.strongStep("Edit the created Document and publish");
		log.info("INFO: Edit the created Document and publish");
		ui.editDoc(FilesUIConstants.docBody, file.getRename(), file.getDescription());
		
		logger.strongStep("Logout from application");
		log.info("INFO: Logout from application");
		ui.logout();
		
		if (!(bcom == null)) {
			logger.strongStep("Load communities component and login to application");
			log.info("INFO: Load communities component and login to application");
			ui.loadComponent(Data.getData().ComponentCommunities,true);
			ui.login(testUser3);

			// navigate to the API community
			logger.strongStep("Naviagate to the Community");
			log.info("INFO: Navigate to the Community using UUID");
			bcom.navViaUUID(cUI);

			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed navigation menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		} else {
			logger.strongStep("Load files component and login to application");
			log.info("INFO: Load files component and login to application");
			ui.loadComponent(Data.getData().ComponentFiles,true);
			ui.login(testUser3);
		}
		
		logger.strongStep("Verify the New Document file is created");
		log.info("INFO: Verify the New Document file is created");
		verifyDocCreated(file);

		logger.strongStep("Verify Document content");
		log.info("INFO: Verify Document content");
		verifyDocContent(file, FilesUIConstants.docContentInFIDO);
		
		logger.strongStep("Logout from application");
		log.info("INFO: Logout from application");
		ui.logout();

		logger.strongStep("Closing browser instance so that existing sessions of HCL Docs should not be retained");
		log.info("INFO: Closing browser instance so that existing sessions of HCL Docs should not be retained");
		driver.close();
		
		if (!(bcom == null)) {
			logger.strongStep("Load communities component and login to application");
			log.info("INFO: Load communities component and login to application");
			ui.loadComponent(Data.getData().ComponentCommunities);
			ui.login(testUser3);

			// navigate to the API community
			logger.strongStep("Naviagate to the Community");
			log.info("INFO: Navigate to the Community using UUID");
			bcom.navViaUUID(cUI);

			logger.strongStep("Select Files from the tabbed navigation menu");
			log.info("INFO: Select Files from the tabbed navigation menu");
			Community_TabbedNav_Menu.FILES.select(ui);
		} else {
			logger.strongStep("Load files component and login to application");
			log.info("INFO: Load files component and login to application");
			ui.loadComponent(Data.getData().ComponentFiles,true);
			ui.login(testUser3);
			
			logger.strongStep("Go to my files ad details view");
			log.info("INFO: Go to my files ad details view");
			ui.clickMyFilesView();
			Files_Display_Menu.DETAILS.select(ui);
		}

		logger.strongStep("Go to 'More' and click on 'Edit in Docs'");
		log.info("INFO: Go to 'More' and click on 'Edit in Docs'");
		ui.clickLinkWait(ui.fileSpecificMore(file));
		ui.clickLinkWait(ui.docSpecificEdit(file));
		
		logger.strongStep("Enter some content and publish");
		log.info("INFO: Enter some content and publish");
		ui.editDoc(FilesUIConstants.docBody, file.getRename(), " Edited");
		
		logger.strongStep("Go to My Files view. Select 'Details' display button");
		log.info("INFO: Go to My Files view. Select 'Details' display button");
		if(!file.getComFile()) {
			ui.clickMyFilesView();
		}
		Files_Display_Menu.DETAILS.select(ui);
		
		file.setDescription(EditedContent);
		
		logger.strongStep("Verify updated content displays in FIDO view");
		log.info("INFO: Verify updated content displays in FIDO view");
		verifyDocContent(file, FilesUIConstants.docContentInFIDO);
		
	}
	
}
