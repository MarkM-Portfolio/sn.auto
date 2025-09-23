package com.ibm.conn.auto.tests.files;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Level_2_Files_SwitchURL_MT_Boundary extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Files_SwitchURL_MT_Boundary.class);
	private FilesUI ui;
	private CommunitiesUI comUI;
	private APIFileHandler apiFileOwner;

	private TestConfigCustom cfg;
	private User testUser_orgA,testUser_orgA1,testUser_orgB;
	private String serverURL_MT_orgA, serverURL_MT_orgB;	

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgA1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();	
		apiFileOwner = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()), testUser_orgA.getPassword());

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}

	private void switchURLErrorValidation() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// switch URL to orgB
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);

		// Validate error message
		logger.strongStep("Verify access denied error message should be displayed");
		log.info("Verify access denied error message should be displayed");
		ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");
		driver.navigate().back();
	}

	private void navigateToFileListView() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		logger.weakStep("Go to My Files. Change the 'View List' format");
		ui.clickMyFilesView();
		ui.clickLinkWait(FilesUICloud.listView);
	}
	
	private void stopExternalSharing(BaseFile file, Boolean value) {
		// change the view list format
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		if (value) {
			// select uploaded file
			log.info("INFO: Open the file in FiDO");
			logger.strongStep("Open the file in FiDO");
			ui.fluentWaitPresent(FilesUI.selectFile(file));
			ui.clickLinkWithJavascript(FilesUI.selectFile(file));
		}

		ui.fluentWaitElementVisible(FilesUIConstants.FileOverlayClose);

		log.info("INFO: Select Sharing tab from FiDO");
		logger.strongStep("Select Sharing tab from FiDO");
		ui.fluentWaitElementVisible(FilesUIConstants.sharingTabInFiDO);
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

		log.info("INFO: Select 'Stop Sharing Externally' link from FiDO");
		logger.strongStep("Select 'Stop Sharing Externally' link from FiDO");
		ui.fluentWaitElementVisible(FilesUIConstants.stopSharingExternallyLink);
		ui.clickLinkWithJavascript(FilesUIConstants.stopSharingExternallyLink);

		log.info("INFO: Select 'OK' button on 'Make Internal' prompt");
		logger.strongStep("Select 'OK' button on 'Make Internal' prompt");
		ui.fluentWaitElementVisible(FilesUIConstants.makeFileInternalPrompt);
		ui.clickLinkWithJavascript(FilesUIConstants.okOnPrompt);
	}

	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for uploaded file with PRIVATE access level</li>
	*<li><B>Step:</B> [API] Upload a private file</li>
	*<li><B>Step:</B> Open the file in FiDO</li>
	*<li><B>Step:</B> Select 'Stop sharing externally' link to change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'OK' button on 'Make Internal' prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/

	@Test(groups = { "mtlevel3" })
	public void uploadPrivateFile() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename(testName + Helper.genDateBasedRand())
				.build();
		
		Assert.assertNotNull(apiFileOwner);

		// Create the BaseFile instance of the file
		logger.strongStep("Upoad private file via API ");
		log.info("INFO: Upoad private file via API ");
		FileEntry privateFile = FileEvents.addFile(baseFileImage, testUser_orgA, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		// Load the component
		logger.strongStep("Load files and login");
		log.info("INFO: Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFileListView();

		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing(baseFileImage, true);

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete the uploaded file
		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(privateFile);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for uploaded file with PUBLIC access level</li>
	*<li><B>Step:</B> [API] Upload a public file</li>
	*<li><B>Step:</B> Open the file in FiDO</li>
	*<li><B>Step:</B> Select 'Stop sharing externally' link to change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'OK' button on 'Make Internal' prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
	
	@Test(groups = { "mtlevel3" })
	public void uploadPublicFile() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename(testName + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		Assert.assertNotNull(apiFileOwner);

		// Create the BaseFile instance of the file
		logger.strongStep("Upoad public file via API ");
		log.info("INFO: Upoad public file via API ");
		FileEntry publicFile = FileEvents.addFile(file, testUser_orgA, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFileListView();

		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing(file, true);

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete the uploaded file
		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(publicFile);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for uploaded file shared with people access level</li>
	*<li><B>Step:</B> [API] Upload a file shared with people</li>
	*<li><B>Step:</B> Open the file in FiDO</li>
	*<li><B>Step:</B> Select 'Stop sharing externally' link to change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'OK' button on 'Make Internal' prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
	
	@Test(groups = { "mtlevel3" })
	public void fileuploadSharedWithPerson() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		Assert.assertNotNull(apiFileOwner);
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(serverURL_MT_orgA, testUser_orgA1.getEmail(),
				testUser_orgA1.getPassword());

		// Create the BaseFile instance of the file
		logger.strongStep("Upoad public file via API ");
		log.info("INFO: Upoad public file via API ");
		BaseFile baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE,
				testUser2Profile);
		FileEntry sharedFile1 = FileEvents.addFile(baseFile1, testUser_orgA, apiFileOwner);

		// Get title of file entry created
		String fileName = sharedFile1.getTitle();
		log.info("File name is: " + fileName);

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFileListView();

		// select uploaded file
		log.info("INFO: Open the file in FiDO");
		logger.strongStep("Open the file in FiDO");
		ui.fluentWaitPresent("css=a[title='" + sharedFile1.getTitle() + "']");
		ui.clickLinkWithJavascript(("css=a[title='" + sharedFile1.getTitle() + "']"));

		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing(baseFile1, false);

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete the uploaded file
		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(sharedFile1);

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for uploaded file shared with community access level</li>
	*<li><B>Step:</B> [API] Upload a file shared with community</li>
	*<li><B>Step:</B> Navigate to community created in above step</li>
	*<li><B>Step:</B> Open the file in FiDO</li>
	*<li><B>Step:</B> Select 'Stop sharing externally' link to change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'OK' button on 'Make Internal' prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
		
	@Test(groups={"mtlevel3"})
	public void ShareFileWithCommunity() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getEmail(), testUser_orgA.getPassword());
		
		log.info("INFO: " + testUser_orgA.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
												   .tags(Data.getData().commonTag + Helper.genStrongRand())
												   .access(Access.PUBLIC)
												   .description(Data.getData().commonDescription + Helper.genStrongRand())
												   .build();
		Community publicCommunity = baseCom.createAPI(communityOwner);
		
		log.info("INFO: " + testUser_orgA.getDisplayName() + " creating a public file");
		BaseFile baseFile = new BaseFile.Builder(Data.getData().file1)
										.extension(".jpg")
										.shareLevel(ShareLevel.EVERYONE)
										.rename(testName + "_" + Helper.genDateBasedRand())
										.tags(testName + Helper.genStrongRand())
										.build();
		Assert.assertNotNull(apiFileOwner);
		log.info("INFO: " + testUser_orgA.getDisplayName() + " sharing file with community using API method");
		FileEntry publicFile = FileEvents.addFile(baseFile, testUser_orgA, apiFileOwner);
		baseFile.setName(baseFile.getRename() + baseFile.getExtension());

		log.info("INFO: Change permissions to public");
		apiFileOwner.changePermissions(baseFile, publicFile);

		log.info("INFO: Share file with the community");
		apiFileOwner.shareFileWithCommunity(publicFile, publicCommunity, Role.OWNER);

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		// logger.strongStep("INFO: Get UUID of community");
		baseCom.setCommunityUUID(baseCom.getCommunityUUID_API(communityOwner,publicCommunity));

		// Load component and login
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		baseCom.navViaUUID(comUI);
		
		// Select Files from left menu
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing(baseFile, true);
		
		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();
		
		log.info("INFO: Perform clean up now that the test has completed");
		logger.weakStep("INFO: Perform clean up now that the test has completed");
		apiFileOwner.deleteFile(publicFile);
		communityOwner.deleteCommunity(publicCommunity);		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for uploaded file with EXTERNAL access level</li>
	*<li><B>Step:</B> [API] Upload a externally shared file</li>
	*<li><B>Step:</B> Open the file in FiDO</li>
	*<li><B>Step:</B> Select 'Stop sharing externally' link to change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'OK' button on 'Make Internal' prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/

	@Test(groups = { "mtlevel2" })
	public void uploadExternalFileAPI() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseFile file = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand()).rename(testName + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE).build();

		Assert.assertNotNull(apiFileOwner);
		// Create the BaseFile instance of the file
		FileEntry externallySharedFile = FileEvents.addFile(file, testUser_orgA, apiFileOwner);
		file.setName(file.getRename() + file.getExtension());

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFileListView();

		// select uploaded file
		ui.fluentWaitPresent(FilesUI.selectFile(file));
		ui.clickLinkWithJavascript(FilesUI.selectFile(file));
		ui.fluentWaitElementVisible(FilesUIConstants.FileOverlayClose);

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete the uploaded file
		logger.weakStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		apiFileOwner.deleteFile(externallySharedFile);

		ui.endTest();
	}
		/**
		*<ul>
		*<li><B>Info: </B>Test TypeAhead functionality of Private, Public, External and Shared with People and Community File's FIDO:Comment in orgA login for OrgB users</li>
		*<li><B>Step:</B>Create communities of different kinds in orgA with 'orga' keyword using API</li>
		*<li><B>Step:</B>Create communities of different kinds in orgB with 'orgb' keyword using API</li>
		*<li><B>Step:</B>Create Files of different kinds in orgA using API</li>
		*<li><B>Step: </B>login as an orgA user and Load Files Component</li>
		*<li><B>Step: </B>Click on All File types created in above step one at a time and open in FIDO viewer</li>
		*<li><B>Step: </B>TypeAhead User from OrgB in Comments tab</li>
		*<li><B>Verify: </B>Verify that 'No results found' message should be displayed with option 'Person not listed? Use full search...'</li>
		*<li><B>Step: </B>Select option 'Person not listed? Use full search...' </li>
		*<li><B>Verify: </B>Verify 'No results found' message is displayed</li>
		*<li><B>Step: </B>Close the FIDO viewer.</li>
		*</ul>
		*/
		@Test(groups = { "mtlevel2" })
		public void typeAheadFilesComment() throws Exception {
			List<String> Filename = new ArrayList<String>();
			DefectLogger logger = dlog.get(Thread.currentThread().getId());

			String testName = ui.startTest();
			List <Community> orgbCommunities = new ArrayList<Community>();
			List <Community> orgaCommunities = new ArrayList<Community>();
			List <Element> typeAheadResults;

			//Build the communities in orgB
			BaseCommunity orgbPublic = new BaseCommunity.Builder("orgbPublic" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbModerated = new BaseCommunity.Builder("orgbModerated" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbExternalRestricted = new BaseCommunity.Builder("orgbExternalRestricted" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbInternalRestricted = new BaseCommunity.Builder("orgbInternalRestricted" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(false).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbRBL = new BaseCommunity.Builder("orgbRBL" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).rbl(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			//Build the communities in orgA
			BaseCommunity orgaPublic = new BaseCommunity.Builder("orgaPublic" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();
			
			BaseCommunity orgaModerated = new BaseCommunity.Builder("orgaModerated" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();
			
			//Instantiate API Handler for orgB
			log.info("INFO: Initiate the communities API Handler for: " + testUser_orgB.getDisplayName());
			APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
					testUser_orgB.getPassword());

			log.info("INFO: Create communities of different kinds in orgB using API");
			logger.strongStep("Create communities of different kinds in orgB using API");
			Community communityOrgbPublic = orgbPublic.createAPI(apiHandler);
			Community communityOrgbModerated = orgbModerated.createAPI(apiHandler);
			Community communityOrgbExternalRestricted = orgbExternalRestricted.createAPI(apiHandler);
			Community communityOrgbInternalRestricted = orgbInternalRestricted.createAPI(apiHandler);
			Community communityOrgbRBL = orgbRBL.createAPI(apiHandler);

			log.info("INFO: Add the orgB communities to the list previously created");
			logger.strongStep("Add the orgB communities to the list previously created");
			orgbCommunities.add(communityOrgbPublic);
			orgbCommunities.add(communityOrgbModerated);
			orgbCommunities.add(communityOrgbExternalRestricted);
			orgbCommunities.add(communityOrgbInternalRestricted);
			orgbCommunities.add(communityOrgbRBL);
			
			//Instantiate API Handler for orgA
			log.info("INFO: Initiate the communities API Handler for: " + testUser_orgA.getDisplayName());
			apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
					testUser_orgA.getPassword());

			//Create different types of communities using API in orgA
			log.info("INFO: Create communities of different kinds in orgA using API");
			logger.strongStep("Create communities of different kinds in orgA using API");
			Community communityOrgaPublic = orgaPublic.createAPI(apiHandler);
			Community communityOrgaModerated = orgaModerated.createAPI(apiHandler);

			log.info("INFO: Add the orgA communities to the list previously created");
			logger.strongStep("Add the orgA communities to the list previously created");
			orgaCommunities.add(communityOrgaPublic);
			orgaCommunities.add(communityOrgaModerated);

			//Upload Public File
			BaseFile pubfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand())
					.rename("Pub"+testName+ Helper.genDateBasedRand())
					.shareLevel(ShareLevel.EVERYONE)
					.build();

			Assert.assertNotNull(apiFileOwner);

			// Create the BaseFile instance of public file
			logger.strongStep("Upoad public file via API ");
			log.info("INFO: Upoad public file via API ");
			FileEvents.addFile(pubfile, testUser_orgA, apiFileOwner);
			pubfile.setName(pubfile.getRename() + pubfile.getExtension());

			//Upload Private File
			BaseFile pvtfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand())
					.rename("Pvt"+testName + Helper.genDateBasedRand())
					.build();
			
			Assert.assertNotNull(apiFileOwner);

			// Create the BaseFile instance of private file
			logger.strongStep("Upoad private file via API ");
			log.info("INFO: Upoad private file via API ");
			FileEvents.addFile(pvtfile, testUser_orgA, apiFileOwner);
			pvtfile.setName(pvtfile.getRename() + pvtfile.getExtension());

			//Upload Externally Shared File
			BaseFile extersharedfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand()).rename("ExtnShr"+testName + Helper.genDateBasedRand())
					.shareLevel(ShareLevel.NO_ONE).build();
			
			Assert.assertNotNull(apiFileOwner);
			
			// Create the BaseFile instance of externally shared file
			FileEvents.addFile(extersharedfile, testUser_orgA, apiFileOwner);
			extersharedfile.setName(extersharedfile.getRename() + extersharedfile.getExtension());
			
			
			//Upload File Shared with People
			Assert.assertNotNull(apiFileOwner);
			APIProfilesHandler testUser2Profile = new APIProfilesHandler(serverURL_MT_orgA, testUser_orgA1.getEmail(),
					testUser_orgA1.getPassword());
			logger.strongStep("Upoad public file via API ");
			log.info("INFO: Upoad public file via API ");
			BaseFile baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE,
					testUser2Profile);
			FileEntry sharedFile1 = FileEvents.addFile(baseFile1, testUser_orgA, apiFileOwner);
			
			//Upload File Share with Community
			BaseFile shrcommfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand())
					.rename("ShareComm"+testName + Helper.genDateBasedRand())
					.build();
			
			Assert.assertNotNull(apiFileOwner);

			// Create the BaseFile instance of the file shared with community
			logger.strongStep("Upoad private file via API ");
			log.info("INFO: Upoad private file via API ");
			FileEvents.addFile(shrcommfile, testUser_orgA, apiFileOwner);
			shrcommfile.setName(shrcommfile.getRename() + shrcommfile.getExtension());
			
			// Load the component
			logger.strongStep("Load files and login");
			ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
			ui.login(testUser_orgA);
			ui.waitForPageLoaded(driver);
			
			// Navigate to file list view
			logger.strongStep(" Navigate to file list view");
			log.info("INFO: Navigate to file list view");
			navigateToFileListView();		
			
			Filename.add(shrcommfile.getName());
			Filename.add(pubfile.getName());
			Filename.add(pvtfile.getName());
			Filename.add(extersharedfile.getName());
			Filename.add(sharedFile1.getTitle());
			
			for(String file : Filename)
			{
				// select uploaded file
				log.info("INFO: Open the file - "+file+" in FiDO");
				logger.strongStep("Open the file - "+file+" in FiDO");
				ui.fluentWaitPresent(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
				ui.clickLinkWithJavascript(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
				
				//Sharing the File with Community
				if(file.startsWith("ShareComm"))
				{
					ui.clickLinkWait(FilesUIConstants.sharingTabInFiDO);
					ui.clickLink(FilesUIConstants.sharingAddPeopleComm);
					Select LangOptions=new Select((WebElement) driver.getFirstElement(FilesUIConstants.sharingtypeoption).getBackingObject());
					LangOptions.selectByVisibleText("Community");
					ui.typeTextWithDelay(FilesUIConstants.sharingcommunityname.replaceAll("PLACEHOLDER", "community"), orgaPublic.getName().substring(0, 4));
					typeAheadResults = driver.getVisibleElements(FilesUIConstants.fidShareCommunityList);
					for (Element ele : typeAheadResults) {
						if (ele.getText().contains( orgaPublic.getName()))
							ele.click();
					}
					ui.clickLinkWait(FilesUIConstants.sharingbutton);
				}
				
				//Type user name from OrgB
				ui.clickLinkWait(FilesUIConstants.FileOverlayCommentsTab);
				driver.switchToFrame().selectFrameByElement(driver.getSingleElement(FilesUIConstants.FileOverlayCommentInputBox));
				ui.fluentWaitElementVisible(FilesUIConstants.FilesCommentTextField);
				log.info("INFO: Type With Delay @"+testUser_orgB.getUid() +"in Status Updates.");
				logger.strongStep("Type With Delay @"+testUser_orgB.getUid() +"in Status Updates.");
				ui.typeTextWithDelay(FilesUIConstants.FilesCommentTextField, "@"+testUser_orgB.getUid());
				ui.switchToTopFrame();
				
				//Verify the message 'No results found' 
				log.info("INFO: Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
				logger.strongStep("Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
				Assert.assertTrue(driver.isTextPresent("No results found"),
						"The message 'No results found' does not appear after typing @"+testUser_orgB.getUid());
						
				//Click on the option 'Full Search'
				log.info("INFO: Click on the option 'Person not listed? Use full search...'");
				logger.strongStep("Click on the option 'Person not listed? Use full search...'");
				ui.clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);
		
				//Verify that no results are found
				log.info("INFO: Verify that 'No results found' message is displayed");
				logger.strongStep("Validate that 'No results found' message is displayed");
				Assert.assertTrue(driver.isTextPresent("No results found"));
		
				ui.clickLinkWait(FilesUIConstants.FileOverlayClose);
			}

			ui.endTest();
		}
		
		/**
		*<ul>
		*<li><B>Info: </B>Test TypeAhead functionality of Private, Public, External, Shared with People and Community File's FIDO:Share in orgA login for OrgB users</li>
		*<li><B>Step:</B>Create communities of different kinds in orgA with 'orga' keyword using API</li>
		*<li><B>Step:</B>Create communities of different kinds in orgB with 'orgb' keyword using API</li>
		*<li><B>Step:</B>Create Files of different kinds in orgA using API</li>
		*<li><B>Step: </B>login as an orgA user and load Files Component</li>
		*<li><B>Step: </B>Click on All File types created in above step one at a time and open in FIDO viewer</li>
		*<li><B>Step: </B>Open Sharing tab</li>
		*<li><B>Step: </B>Select Sharing Type as 'Person' and enter a user from OrgB</li>
		*<li><B>Verify: </B>Verify that 'No results found' message should be displayed with option 'Person not listed? Use full search...'</li>
		*<li><B>Step: </B>Select option 'Person not listed? Use full search...' </li>
		*<li><B>Verify: </B>Verify 'No results found' message is displayed</li>
		*<li><B>Step: </B>Select Sharing Type as Community and enter 'org' Keyword as community name.</li>
		*<li><B>Verify: </B>Verify that All community from orga appear</li>
		*<li><B>Verify: </B>Verify that No community from orgb get appear.</li>
		*<li><B>Step: </B>Close the FIDO viewer.</li>
		*</ul>
		*/
		@Test(groups = { "mtlevel2" })
		public void typeAheadFilesShare() throws Exception {
			boolean flag;
			List<String> Filename = new ArrayList<String>();
			DefectLogger logger = dlog.get(Thread.currentThread().getId());
			String testName = ui.startTest();

			List <Community> orgbCommunities = new ArrayList<Community>();
			List <Community> orgaCommunities = new ArrayList<Community>();
			List <Element> typeAheadResults=null;

			//Build the communities in orgB
			BaseCommunity orgbPublic = new BaseCommunity.Builder("typeAheadOrgbPublic" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbModerated = new BaseCommunity.Builder("typeAheadOrgbModerated" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbExternalRestricted = new BaseCommunity.Builder("typeAheadOrgbExternalRestricted" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbInternalRestricted = new BaseCommunity.Builder("typeAheadOrgbInternalRestricted" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(false).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			BaseCommunity orgbRBL = new BaseCommunity.Builder("typeAheadOrgbRBL" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).rbl(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			//Build the communities in orgA
			BaseCommunity orgaPublic = new BaseCommunity.Builder("typeAheadOrgaPublic" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();
			
			BaseCommunity orgaModerated = new BaseCommunity.Builder("typeAheadOrgaModerated" + Helper.genDateBasedRand())
					.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
					.description("Test description for testcase " + testName).build();

			//Instantiate API Handler for OrgB
			log.info("INFO: Initiate the communities API Handler for: " + testUser_orgB.getDisplayName());
			APICommunitiesHandler apiHandler = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
					testUser_orgB.getPassword());

			//Create different types of communities using API in orgB
			log.info("INFO: Create communities of different kinds in orgB using API");
			logger.strongStep("Create communities of different kinds in orgB using API");
			Community communityOrgbPublic = orgbPublic.createAPI(apiHandler);
			Community communityOrgbModerated = orgbModerated.createAPI(apiHandler);
			Community communityOrgbExternalRestricted = orgbExternalRestricted.createAPI(apiHandler);
			Community communityOrgbInternalRestricted = orgbInternalRestricted.createAPI(apiHandler);
			Community communityOrgbRBL = orgbRBL.createAPI(apiHandler);

			log.info("INFO: Add the orgB communities to the list previously created");
			logger.strongStep("Add the orgB communities to the list previously created");
			orgbCommunities.add(communityOrgbPublic);
			orgbCommunities.add(communityOrgbModerated);
			orgbCommunities.add(communityOrgbExternalRestricted);
			orgbCommunities.add(communityOrgbInternalRestricted);
			orgbCommunities.add(communityOrgbRBL);
			
			//Instantiate API Handler for OrgA
			log.info("INFO: Initiate the communities API Handler for: " + testUser_orgA.getDisplayName());
			apiHandler = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getAttribute(cfg.getLoginPreference()),
					testUser_orgA.getPassword());

			//Create different types of communities using API in orgA
			log.info("INFO: Create communities of different kinds in orgA using API");
			logger.strongStep("Create communities of different kinds in orgA using API");
			Community communityOrgaPublic = orgaPublic.createAPI(apiHandler);
			Community communityOrgaModerated = orgaModerated.createAPI(apiHandler);

			log.info("INFO: Add the orgA communities to the list previously created");
			logger.strongStep("Add the orgA communities to the list previously created");
			orgaCommunities.add(communityOrgaPublic);
			orgaCommunities.add(communityOrgaModerated);

			//Upload Public File
			BaseFile pubfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand())
					.rename("Pub"+testName+ Helper.genDateBasedRand())
					.shareLevel(ShareLevel.EVERYONE)
					.build();

			Assert.assertNotNull(apiFileOwner);

			// Create the BaseFile instance of Public file
			logger.strongStep("Upoad public file via API ");
			log.info("INFO: Upoad public file via API ");
			FileEvents.addFile(pubfile, testUser_orgA, apiFileOwner);
			pubfile.setName(pubfile.getRename() + pubfile.getExtension());

			//Upload Private File
			BaseFile pvtfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand())
					.rename("Pvt"+testName + Helper.genDateBasedRand())
					.build();
			
			Assert.assertNotNull(apiFileOwner);

			// Create the BaseFile instance of Private file
			logger.strongStep("Upoad private file via API ");
			log.info("INFO: Upoad private file via API ");
			FileEvents.addFile(pvtfile, testUser_orgA, apiFileOwner);
			pvtfile.setName(pvtfile.getRename() + pvtfile.getExtension());

			//Upload Externally Shared File
			BaseFile extersharedfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand()).rename("ExtnShr"+testName + Helper.genDateBasedRand())
					.shareLevel(ShareLevel.NO_ONE).build();
			
			Assert.assertNotNull(apiFileOwner);
			// Create the BaseFile instance of Externally Shared file
			FileEvents.addFile(extersharedfile, testUser_orgA, apiFileOwner);
			extersharedfile.setName(extersharedfile.getRename() + extersharedfile.getExtension());
			
			
			//Upload a File Shared with People
			Assert.assertNotNull(apiFileOwner);
			APIProfilesHandler testUser2Profile = new APIProfilesHandler(serverURL_MT_orgA, testUser_orgA1.getEmail(),
					testUser_orgA1.getPassword());
			logger.strongStep("Upload a File Shared with People via API ");
			log.info("INFO: Upload a File Shared with People via API ");
			BaseFile baseFile1 = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE,
					testUser2Profile);
			FileEntry sharedFile1 = FileEvents.addFile(baseFile1, testUser_orgA, apiFileOwner);
			
			//Upload a File Share with Community
			BaseFile shrcommfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
					.tags(testName + "_" + Helper.genDateBasedRand())
					.rename("ShareComm"+testName + Helper.genDateBasedRand())
					.build();
			
			Assert.assertNotNull(apiFileOwner);

			// Create the BaseFile instance of file shared with community
			FileEvents.addFile(shrcommfile, testUser_orgA, apiFileOwner);
			shrcommfile.setName(shrcommfile.getRename() + shrcommfile.getExtension());
			
			// Load the component
			logger.strongStep("Load files and login");
			ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
			ui.login(testUser_orgA);

			// Navigate to file list view
			logger.strongStep(" Navigate to file list view");
			log.info("INFO: Navigate to file list view");
			navigateToFileListView();		
			
			Filename.add(shrcommfile.getName());
			Filename.add(pubfile.getName());
			Filename.add(pvtfile.getName());
			Filename.add(extersharedfile.getName());
			Filename.add(sharedFile1.getTitle());
			
			for(String file : Filename)
			{
				// select uploaded file
				log.info("INFO: Open the file in FiDO");
				logger.strongStep("Open the file in FiDO");
				ui.fluentWaitPresent(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
				ui.clickLinkWithJavascript(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
				
				//Sharing the File with Community
				if(file.startsWith("ShareComm"))
				{
					ui.clickLinkWait(FilesUIConstants.sharingTabInFiDO);
					ui.clickLink(FilesUIConstants.sharingAddPeopleComm);
					Select LangOptions=new Select((WebElement) driver.getFirstElement(FilesUIConstants.sharingtypeoption).getBackingObject());
					LangOptions.selectByVisibleText("Community");
					ui.typeTextWithDelay(FilesUIConstants.sharingcommunityname.replaceAll("PLACEHOLDER","community") , orgaPublic.getName().substring(0, 12));
					
					log.info(orgaPublic.getName());
					typeAheadResults = driver.getVisibleElements(FilesUIConstants.fidShareCommunityList);
					for (Element ele : typeAheadResults) {
						if (ele.getText().contains( orgaPublic.getName()))
							ele.click();
					}
					ui.clickLinkWait(FilesUIConstants.sharingbutton);
				}
				
				ui.clickLinkWait(FilesUIConstants.sharingTabInFiDO);
				ui.clickLink(FilesUIConstants.sharingAddPeopleComm);
				//use select community
				log.info("INFO: Select sharing Type as Person and Type User name - " + testUser_orgB.getUid());
				Select LangOptions=new Select((WebElement) driver.getFirstElement(FilesUIConstants.sharingtypeoption).getBackingObject());
				LangOptions.selectByVisibleText("Person");
				ui.typeTextWithDelay(FilesUIConstants.sharingcommunityname.replaceAll("PLACEHOLDER","person"), testUser_orgB.getUid());
				ui.switchToTopFrame();
				
				//Verify the message 'No results found'
				log.info("INFO: Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
				logger.strongStep("Verify the message 'No results found' appears after typing @"+testUser_orgB.getUid());
				Assert.assertTrue(driver.isTextPresent("No results found"),
						"The message 'No results found' does not appear after typing @"+testUser_orgB.getUid());
						
				//Click on the Full Search option
				log.info("INFO: Click on the option 'Person not listed? Use full search...'");
				logger.strongStep("Click on the option 'Person not listed? Use full search...'");
				ui.clickLinkWithJavascript(BaseUIConstants.searchlinkDropdown);
		
				//Verify that no results are found
				log.info("INFO: Verify that 'No results found' message is displayed");
				logger.strongStep("Validate that 'No results found' message is displayed");
				Assert.assertTrue(driver.isTextPresent("No results found"));
				ui.clickLinkWait(FilesUIConstants.FileOverlayClose);
				
				//Test for Community
				ui.clickLinkWithJavascript(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
				ui.clickLinkWait(FilesUIConstants.sharingTabInFiDO);
				ui.clickLink(FilesUIConstants.sharingAddPeopleComm);
				
				LangOptions=new Select((WebElement) driver.getFirstElement(FilesUIConstants.sharingtypeoption).getBackingObject());
				LangOptions.selectByVisibleText("Community");
				
				ui.typeTextWithDelay(FilesUIConstants.sharingcommunityname.replaceAll("PLACEHOLDER", "community"), "typeAheadOrg");
				ui.switchToTopFrame();
				typeAheadResults = driver.getVisibleElements(FilesUIConstants.fidShareCommunityList);
				for(Community Commname : orgbCommunities)
				{
					for (Element ele : typeAheadResults) 
						Assert.assertNotEquals(ele.getText(), Commname.getTitle());
				}	
				
				for(Community Commname : orgaCommunities)
				{
					flag=false;
					for (Element ele : typeAheadResults) 
					{	
						if(ele.getText().contains(Commname.getTitle()))
						{
							flag=true;
							break;
						}
					}
					Assert.assertTrue(flag);
				}
				ui.clickLinkWait(FilesUIConstants.FileOverlayClose);
			}

			ui.endTest();
		}
	}

