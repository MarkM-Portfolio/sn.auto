package com.ibm.conn.auto.tests.files;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import junit.framework.Assert;


public class BVT_Level_2_Folders_SwitchURL_MT_Boundary extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Folders_SwitchURL_MT_Boundary.class);
	private FilesUI ui;
	private CommunitiesUI comUI;
	private APIFileHandler folderOwner;
	private TestConfigCustom cfg;
	private User testUser_orgA,testUser_orgA1,testUser_orgB;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private APICommunitiesHandler  apiHandler;
	

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgA1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();	
		folderOwner = new APIFileHandler(serverURL_MT_orgA, testUser_orgA.getEmail(), testUser_orgA.getPassword());

		
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);	
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());

	}

	private void navigateToFolderListView() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		logger.weakStep("Go to Folders. Change the 'View List' format");
		ui.clickMyFoldersView();
		ui.clickLinkWait(FilesUICloud.listView);
	}

	private void stopExternalSharing() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		log.info("INFO: Select Sharing tab from FiDO");
		logger.strongStep("Select Sharing tab from FiDO");
		ui.fluentWaitElementVisible(FilesUIConstants.changeLink);
		ui.clickLinkWithJavascript(FilesUIConstants.changeLink);

		log.info("INFO: Select 'OK' button on 'Make Internal' prompt");
		logger.strongStep("Select 'OK' button on 'Make Internal' prompt");
		ui.fluentWaitElementVisible(FilesUIConstants.makeFolderInternalPrompt);
		ui.clickLinkWithJavascript(FilesUIConstants.makeFolderInternal);
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

	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for created PUBLIC folder</li>
	*<li><B>Step:</B> [API] Create a Public folder</li>
	*<li><B>Step:</B> Go to 'Folder' view</li>
	*<li><B>Step:</B> Open newly created Public folder</li>
	*<li><B>Step:</B> Change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'Make Internal' on displayed prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
	
	@Test(groups={"mtlevel3"})
	public void createPublicFolder() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		Assert.assertNotNull(folderOwner);
	
		logger.weakStep(testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		FileEntry publicFolder = folderOwner.createFolder(baseFolder, Role.READER);
		log.info("INFO: Public folder created successfully");
		logger.strongStep("Public folder created successfully");

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFolderListView();

		// Select the folder
		logger.strongStep("Select 'my folders'");
		log.info("INFO: Select the folder under my folders");
		ui.clickLinkWait(FilesUI.selectFile(baseFolder));

		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing();

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete folder
		logger.strongStep("Perform clean-up now that the test has completed");
		log.info("INFO: Perform clean-up now that the test has completed");
		folderOwner.deleteFolder(publicFolder);
	}

	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for created PRIVATE folder</li>
	*<li><B>Step:</B> [API] Create a Private folder</li>
	*<li><B>Step:</B> Go to 'Folder' view</li>
	*<li><B>Step:</B> Open newly created Private folder</li>
	*<li><B>Step:</B> Change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'Make Internal' on displayed prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void createPrivateFolder() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		Assert.assertNotNull(folderOwner);

		logger.weakStep(testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											//.sharedWith(folderFollower.getUUID())
											.shareLevel(ShareLevel.NO_ONE)
											.build();
		FileEntry privateFolder = folderOwner.createFolder(baseFolder, Role.READER);
		log.info("INFO: private folder created successfully");
		logger.strongStep("Private folder created successfully");

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFolderListView();

		// Select the folder
		logger.strongStep("Select 'my folders'");
		log.info("INFO: Select the folder under my folders");
		ui.clickLinkWait(FilesUI.selectFile(baseFolder));

		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing();

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete folder
		logger.strongStep("Perform clean-up now that the test has completed");
		log.info("INFO: Perform clean-up now that the test has completed");
		folderOwner.deleteFolder(privateFolder);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for created Externally Shared folder</li>
	*<li><B>Step:</B> [API] Create a Externally Shared folder</li>
	*<li><B>Step:</B> Go to 'Folder' view</li>
	*<li><B>Step:</B> Open newly created Externally Shared folder</li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/	
	
	@Test(groups = { "mtlevel2" })
	public void createExternallySharedFolder() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		Assert.assertNotNull(folderOwner);
		logger.weakStep(testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		FileEntry externallySharedFolder = folderOwner.createFolder(baseFolder, Role.READER);
		log.info("INFO: Externally shared public folder created successfully");
		logger.strongStep("Externally shared public folder created successfully");

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFolderListView();

		// Select the folder
		logger.strongStep("Select 'my folders'");
		log.info("INFO: Select the folder under my folders");
		ui.clickLinkWait(FilesUI.selectFile(baseFolder));

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete folder
		logger.strongStep("Perform clean-up now that the test has completed");
		log.info("INFO: Perform clean-up now that the test has completed");
		folderOwner.deleteFolder(externallySharedFolder);
	}
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for created Shared With People folder</li>
	*<li><B>Step:</B> [API] Create a Shared With People folder</li>
	*<li><B>Step:</B> Go to 'Folder' view</li>
	*<li><B>Step:</B> Open newly created Shared With People folder</li>
	*<li><B>Step:</B> Change access level from External to Internal</li>
	*<li><B>Step:</B> Select 'Make Internal' on displayed prompt </li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void createFolderSharedWithPerson() {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		Assert.assertNotNull(folderOwner);
		APIProfilesHandler folderFollower = new APIProfilesHandler(serverURL_MT_orgA, testUser_orgA1.getEmail(),
				testUser_orgA1.getPassword());

		logger.weakStep(testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.sharedWith(folderFollower.getUUID())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		FileEntry publicFolder = folderOwner.createFolder(baseFolder, Role.READER);
		log.info("INFO: Public folder shared with person folder created successfully");
		logger.strongStep("Public folder shared with person created successfully");

		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		navigateToFolderListView();

		// Select the folder
		logger.strongStep("Select 'my folders'");
		log.info("INFO: Select the folder under my folders");
		ui.clickLinkWait(FilesUI.selectFile(baseFolder));

		// Make the file internal
		logger.weakStep("Change access level from External to Internal");
		log.info("INFO: Change access level from External to Internal");
		stopExternalSharing();

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		// Delete folder
		logger.strongStep("Perform clean-up now that the test has completed");
		log.info("INFO: Perform clean-up now that the test has completed");
		folderOwner.deleteFolder(publicFolder);
	}
	/**
	*<ul>
	*<li><B>Info:</B> Test that orgA user is not able switch URL to orgB for created Shared With Community folder</li>
	*<li><B>Step:</B> [API] Create a Public Community </li>
	*<li><B>Step:</B> [API] Create a Community folder</li>
	*<li><B>Step:</B> Go to Created community</li>
	*<li><B>Step:</B> Go Files and then Community Folder view</li>
	*<li><B>Step:</B> Open newly created Community folder</li>
	*<li><B>Step:</B> Switch orgA url to orgB </li>
	*<li><B>Verify:</B>Verify that "Access Denied " message should be displayed</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel3" })
	public void createFolderSharedWithCommunity() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		Assert.assertNotNull(folderOwner);
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(serverURL_MT_orgA, testUser_orgA.getEmail(),
				testUser_orgA.getPassword());

		logger.weakStep(testUser_orgA.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testUser_orgA.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
				.tags(Data.getData().commonTag + Helper.genStrongRand()).access(Access.PUBLIC)
				.description(Data.getData().commonDescription + Helper.genStrongRand()).build();

		Community newCommunity = baseCom.createAPI(communityOwner);

		log.info("INFO: Creating the folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder_" + testName + Helper.genStrongRand())
				.tags(Helper.genStrongRand()).shareLevel(ShareLevel.EVERYONE).build();
		FileEntry publicFolder = folderOwner.createCommunityFolder(newCommunity, baseFolder);
		log.info("INFO: Public folder shared with community created successfully");
		logger.strongStep("Public folder shared with community created successfully");

		// Add the UUID to community
		log.info("INFO: Get UUID of community");
		logger.strongStep("INFO: Get UUID of community");
		baseCom.setCommunityUUID(baseCom.getCommunityUUID_API(communityOwner, newCommunity));

		// Load component and login
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentCommunities);
		ui.login(testUser_orgA);

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		baseCom.navViaUUID(comUI);

		// Select Files from left menu
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);

		// Select Community Folders from community view
		logger.strongStep("Select 'Community Folders' from community view");
		log.info("INFO: Select 'Community Folders' from community view");
		ui.clickLinkWait(CommunitiesUIConstants.communityFolders);

		// Select the folder
		logger.strongStep("Select 'my folders'");
		log.info("INFO: Select the folder under my folders");
		ui.clickLinkWait(FilesUI.selectFile(baseFolder));

		// Switch URL to orgB and validate error message
		logger.strongStep("Switching url to orgB and vallidate 'Access Denied'error message");
		log.info("INFO: Switching url to orgB and vallidate 'Access Denied'error message");
		switchURLErrorValidation();

		log.info("INFO: Perform clean-up now that the test has completed");
		communityOwner.deleteCommunity(newCommunity);
		folderOwner.deleteFolder(publicFolder);
	}

	/**
	*<ul>
	*<li><B>Info: </B>Test TypeAhead functionality of Private, Public, External, Shared with People and Community Folder Sharing in orgA login for OrgB users</li>
	*<li><B>Step:</B>Create communities of different kinds in orgA with 'orga' keyword using API</li>
	*<li><B>Step:</B>Create communities of different kinds in orgB with 'orgb' keyword using API</li>
	*<li><B>Step:</B>Create Folders of different kinds in orgA using API</li>
	*<li><B>Step: </B>login as an orgA user</li>
	*<li><B>Step: </B>Click on All Folders types created in above step one at a time</li>
	*<li><B>Step: </B>Select Sharing Link</li>
	*<li><B>Step: </B>Select Sharing Type as Person and enter a user from OrgB</li>
	*<li><B>Verify: </B>Verify that "No results for 'USERB'" message should be displayed with option 'Person not listed? Use full search...'</li>
	*<li><B>Step: </B>Select option 'Person not listed? Use full search...' </li>
	*<li><B>Verify: </B>Verify "No results for 'USERB'" message is displayed</li>
	*<li><B>Step: </B>Select Sharing Type as Community and enter 'org' Keyword as community with 'org' keyword present in OrgA and OrgB.</li>
	*<li><B>Verify: </B>Verify that All community from orga appear</li>
	*<li><B>Verify: </B>Verify that No community from orgb get appear.</li>
	*<li><B>Step: </B>Close the FIDO viewer.</li>
	*</ul>
	*/
	@Test(groups = { "mtlevel2" })
	public void typeAheadFolderShare() throws Exception {
		boolean flag;
		List<String> Foldername = new ArrayList<String>();
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		APIProfilesHandler folderFollower = new APIProfilesHandler(serverURL_MT_orgA, testUser_orgA1.getEmail(),
				testUser_orgA1.getPassword());
		
		List <Community> orgbCommunities = new ArrayList<Community>();
		List <Community> orgaCommunities = new ArrayList<Community>();

		//Build the communities for orgB
		BaseCommunity orgbPublic = new BaseCommunity.Builder("tpeAhdOrgbPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbModerated = new BaseCommunity.Builder("tpeAhdOrgbModerated" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbExternalRestricted = new BaseCommunity.Builder("tpeAhdOrgbExternalRestricted" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbInternalRestricted = new BaseCommunity.Builder("tpeAhdOrgbInternalRestricted" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).allowExternalUserAccess(false).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		BaseCommunity orgbRBL = new BaseCommunity.Builder("tpeAhdOrgbRBL" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.RESTRICTED).rbl(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();

		//Build the communities for orgA
		BaseCommunity orgaPublic = new BaseCommunity.Builder("tpeAhdOrgaPublic" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.PUBLIC).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();
		
		BaseCommunity orgaModerated = new BaseCommunity.Builder("tpeAhdOrgaModerated" + Helper.genDateBasedRand())
				.tags("testTags" + Helper.genDateBasedRand()).access(Access.MODERATED).allowExternalUserAccess(true).shareOutside(true)
				.description("Test description for testcase " + testName).build();
		//Instantiate API Handler for OrgB
		log.info("INFO: Initiate the communities API Handler for: " + testUser_orgB.getDisplayName());
		apiHandler = new APICommunitiesHandler(serverURL_MT_orgB, testUser_orgB.getAttribute(cfg.getLoginPreference()),
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

		//Create different types of communities
		log.info("INFO: Create communities of different kinds in orgA using API");
		logger.strongStep("Create communities of different kinds in orgA using API");
		Community communityOrgaPublic = orgaPublic.createAPI(apiHandler);
		Community communityOrgaModerated = orgaModerated.createAPI(apiHandler);

		log.info("INFO: Add the orgA communities to the list previously created");
		logger.strongStep("Add the orgA communities to the list previously created");
		orgaCommunities.add(communityOrgaPublic);
		orgaCommunities.add(communityOrgaModerated);
 
		//Upload Public Folder
		BaseFile basePubFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.shareLevel(ShareLevel.EVERYONE)
											.build();
		
		folderOwner.createFolder(basePubFolder, Role.READER);
		log.info("INFO: Public folder created successfully");
		logger.strongStep("Public folder created successfully");

		//Upload Private Folder
		BaseFile basePvtFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
											.shareLevel(ShareLevel.NO_ONE)
											.build();
		
		folderOwner.createFolder(basePvtFolder, Role.READER);
		log.info("INFO: private folder created successfully");
		logger.strongStep("Private folder created successfully");

		//Upload Externally Shared Folder
		BaseFile baseExtrSharedFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();
		
		folderOwner.createFolder(baseExtrSharedFolder, Role.READER);
		log.info("INFO: Externally shared public folder created successfully");
		logger.strongStep("Externally shared public folder created successfully");
		
		//Upload Folder Shared with People
		BaseFile basePeopleShareFolder = new BaseFile.Builder("Folder"+ testName + Helper.genStrongRand())
				.sharedWith(folderFollower.getUUID())
				.shareLevel(ShareLevel.EVERYONE)
				.build();
		folderOwner.createFolder(basePeopleShareFolder, Role.READER);
		
		// Load the component
		logger.strongStep("Load files and login");
		ui.loadComponent(serverURL_MT_orgA, Data.getData().ComponentFiles);
		ui.login(testUser_orgA);

		// Navigate to Folder list view
		logger.strongStep(" Navigate to Folder list view");
		log.info("INFO: Navigate to Folder list view");
		navigateToFolderListView();		
		
		Foldername.add(basePubFolder.getName());
		Foldername.add(basePvtFolder.getName());
		Foldername.add(basePeopleShareFolder.getName());
		Foldername.add(baseExtrSharedFolder.getName());
		
		for(String folder : Foldername)
		{
			// select Newly created Folder
			ui.fluentWaitElementVisible(FilesUIConstants.FoldersLeftMenu);
			ui.getFirstVisibleElement(FilesUIConstants.FoldersLeftMenu).click();
			log.info("INFO: Open the Folder - " + folder);
			logger.strongStep("Open the Folder - " + folder);
			ui.fluentWaitPresent(FilesUIConstants.FolderLeftList.replace("PLACEHOLDER", folder));
			ui.scrollIntoViewElement(FilesUIConstants.FolderLeftList.replace("PLACEHOLDER", folder));
			ui.getFirstVisibleElement(FilesUIConstants.FolderLeftList.replace("PLACEHOLDER", folder)).doubleClick();
			
			//Test for Person
			ui.clickLinkWait(FilesUIConstants.folderActionDropDown.replaceAll("PLACEHOLDER", folder));
			ui.clickLink(FilesUIConstants.folderShareLink);
			ui.fluentWaitElementVisibleOnce(FilesUIConstants.folderShareType);
			Select LangOptions=new Select((WebElement) driver.getFirstElement(FilesUIConstants.folderShareType).getBackingObject());
			LangOptions.selectByVisibleText("a Person");
			log.info("INFO: Type User name from OrgB - " + testUser_orgB.getUid());
			logger.strongStep("Type User name from OrgB - " + testUser_orgB.getUid());
			
			ui.typeTextWithDelay(FilesUIConstants.FolderSharePersonCommunityName.replaceAll("PLACEHOLDER", "Person name or email..."), testUser_orgB.getUid());
			ui.switchToTopFrame();
			
			//Verify the message "No results for 'USERB'" appears after typing @USERNAME from OrgB in the text field
			log.info("INFO: Verify the message \"No results for '"+testUser_orgB.getUid()+"'\" appears after typing @"+testUser_orgB.getUid());
			logger.strongStep("Verify the message \"No results for '"+testUser_orgB.getUid()+"'\" appears after typing @"+testUser_orgB.getUid());
			Assert.assertTrue("The message No results for '"+testUser_orgB.getUid()+"' does not appear after typing @"+
			testUser_orgB.getUid() , driver.isTextPresent("No results for '"+testUser_orgB.getUid()+"'"));
					
			//Click on the option 'Person not listed? Use full search...'
			log.info("INFO: Click on the option 'Person not listed? Use full search...'");
			logger.strongStep("Click on the option 'Person not listed? Use full search...'");
			ui.clickLinkWithJavascript(FilesUIConstants.FolderSharesearchlinkDropdown);
	
			//Verify that no results are found for user from orgB
			log.info("INFO: Verify that \"No results for '"+testUser_orgB.getUid()+"'\" message is displayed");
			logger.strongStep("Validate that \"No results for '"+testUser_orgB.getUid()+"'\" message is displayed");
			Assert.assertTrue(driver.isTextPresent("No results for '"+testUser_orgB.getUid()+"'"));
	
			//Test for Community
			LangOptions=new Select((WebElement) driver.getFirstElement(FilesUIConstants.folderShareType).getBackingObject());
			LangOptions.selectByVisibleText("a Community");
			
			
			ui.typeTextWithDelay(FilesUIConstants.FolderSharePersonCommunityName.replaceAll("PLACEHOLDER", "Community name..."), "tpeAhdOrg");
			ui.switchToTopFrame();
			List <Element> typeAheadResults = driver.getVisibleElements("xpath=//body/div[@class='dijitPopup dijitComboBoxMenuPopup']/ul[contains(@id,'communities')]/li");
			
			for(Community Commname : orgbCommunities)
			{
				for (Element ele : typeAheadResults) 
					Assert.assertFalse(ele.getText().contains(Commname.getTitle()));
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
			LangOptions.selectByVisibleText("a Person");
			ui.clickLinkWait(FilesUIConstants.FolderSharePersonCancel);
		}

		ui.endTest();
	}
}
