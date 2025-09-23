package com.ibm.conn.auto.tests.files;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;

import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.NotificationCenterUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class BVT_Files_Ownership_Transfer extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(BVT_Files_Ownership_Transfer.class);
	private FilesUI ui;
	private HomepageUI hUI;
	private CommunitiesUI cUI;
	private APIFileHandler apiFileOwner,folderOwner;
	private APICommunitiesHandler apiCommunityOwner;
	private TestConfigCustom cfg;
	private User testUser,testUser2;
	private String serverURL;
	private APIProfilesHandler folderFollower;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		folderOwner = new APIFileHandler(serverURL, testUser.getEmail(), testUser.getPassword());
		apiCommunityOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());

	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify that a user is able to transfer the ownership of a private file from inside of a private folder</li>
	*<li><B>Step:</B>Upload a private file(.jpg) using API</li>
	*<li><B>Step:</B>Create a new private folder as its owner</li>
	*<li><B>Step:</B>Add the file previously uploaded to the folder via API</li>
	*<li><B>Step:</B>Login and logout user2 so that the user's name appears in the 'Transfer Ownership' typeahead</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel then click on the link for the folder</li>
	*<li><B>Step:</B>Click on the Grid view</li>
	*<li><B>Verify:</B>The thumbnail for the file is visible on the folder's page</li>
	*<li><B>Step:</B>Click on the thumbnail for the file</li>
	*<li><B>Step:</B>Click on the Ellipsis menu in the FIDO Viewer</li>
	*<li><B>Step:</B>Transfer the ownership of the file to user2 using the 'Transfer Ownership' dialog box</li>
	*<li><B>Step:</B>Click on the List view</li>
	*<li><B>Step:</B>Grab the file updation timestamp and click on the thumbnail for the file</li>
	*<li><B>Step:</B>Navigate to the Sharing tab in the FIDO Viewer</li>
	*<li><B>Verify:</B>The new owner of the file is user2</li>
	*<li><B>Verify:</B>The current user does not have editor rights to the file</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Verify:</B>The file is no longer visible on My Files page</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel and select the List view</li>
	*<li><B>Step:</B>Click on the link for the folder</li>
	*<li><B>Verify:</B>The link for the file is still visible on the folder's page</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Homepage component and login as user2</li>
	*<li><B>Step:</B>Click on the Notifications icon at the top of the page</li>
	*<li><B>Verify:</B>The notification for file ownership transfer is visible</li>
	*<li><B>Step:</B>Click on the 'My Notifications' button in the left panel</li>
	*<li><B>Verify:</B>The news item for the file ownership transfer is visible along with the image</li>
	*<li><B>Step:</B>Navigate to the Files component and click on My Files button in the left panel/li>
	*<li><B>Step:</B>Click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Verify:</B>The timestamp for updation of the file is correct</li>
	*<li><B>Step:</B>Click on the Details view and click on the Actions menu for the file</li>
	*<li><B>Verify:</B>The current user has owner rights to the file (Transfer Ownership and Deletion)</li>
	*<li><B>Verify:</B>The status of 'Sharing' for the file is 'Shared</li>
	*<li><B>Step:</B>Click on the link for the file</li>
	*<li><B>Verify:</B>The file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible</li>
	*<li><B>Step:</B>Click on the About tab in the FIDO Viewer</li>
	*<li><B>Verify:</B>The tags added to the file are visible in the About tab of the FIDO Viewer</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel and select the List view</li>
	*<li><B>Verify:</B>The folder created by user1 is not visible to user2 on My Folders page</li>
	*<li><B>Step:</B>Delete the file as well as the folder</li>
	*</ul>
	*/
	@Test(groups = { "level2" })
	public void transferOwnershipOfPrivateFileInAPrivateFolder() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String tag1 = "first_tag_" + Helper.genDateBasedRand();
		String tag2 = "second_tag_" + Helper.genDateBasedRand();
		String tag3 = "third_tag_" + Helper.genDateBasedRand();

		User testUserNew = cfg.getUserAllocator().getUser();

		ui.startTest();

		//Create the BaseFile instance of a private image file
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(tag1 + " " + tag2)
				.rename("PrivateFile" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE)
				.build();

		//Create the BaseFile instance of a private folder
		BaseFile baseFolder = new BaseFile.Builder("MyTopFolder" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE)
				.build();

		//Create the BaseFolder instance of the same private folder
 	    BaseFolder folder = new BaseFolder.Builder(baseFolder.getName())
                 .build();

		logger.strongStep("Upload a private file(.jpg) using API");
		log.info("INFO: Upload a private file(.jpg) using API");
		FileEntry privateFile = FileEvents.addFile(baseFileImage, testUser, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		logger.strongStep("Create a new private folder as its owner");
		log.info("INFO: Create a new private folder as its owner");
		FileEntry privateFolder = apiFileOwner.createFolder(baseFolder, Role.OWNER);

		logger.strongStep("Add the file previously uploaded to the folder");
		log.info("INFO: Add the file previously uploaded to the folder");
		FileEvents.addFileToFolder(testUser, apiFileOwner, privateFile, privateFolder);

		logger.strongStep("Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		log.info("INFO: Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		loginAndLogoutTheNewFileOwner(logger, testUserNew);

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		//Verify that the thumbnail for the file is present on the folder's page when Grid view is selected
		verifyPresenceOfFileOnAFolderPage(logger, "Grid", folder, baseFileImage);

		logger.strongStep("Click on the thumbnail for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the thumbnail for the file: " + baseFileImage.getName());
		driver.getSingleElement(FilesUI.selectFileInGridView(baseFileImage)).hover();
		ui.clickLinkWithJavascript(FilesUI.previewLinkInGridView(baseFileImage));

		logger.strongStep("Click on the Ellipsis menu in the FIDO Viewer");
		log.info("INFO: Click on the Ellipsis menu in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);

		//Transfer the ownership of the file to another user using the 'Transfer Ownership' dialog box
		transferOwnershipUsingDialogBox(logger, testUserNew, tag3, baseFileImage);

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Grab the file updation timestamp");
		log.info("INFO: Grab the file updation timestamp");
		String fileUpdationTimestamp = driver.getSingleElement(FilesUIConstants.fileUpdationTimestamp.replaceAll("PLACEHOLDER", baseFileImage.getName())).getText();

		logger.strongStep("Click on the link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Navigate to the Sharing tab in the FIDO Viewer");
		log.info("INFO: Navigate to the Sharing tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

		logger.strongStep("Verify that the new owner of the file is: " + testUserNew.getDisplayName());
		log.info("INFO: Verify that the new owner of the file is: " + testUserNew.getDisplayName());
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.fileOwnerUserLink).getText().equals(testUserNew.getDisplayName()),
				"The new owner of the file is: " + testUserNew.getDisplayName());

		//Verify the presence of 'Editors - 0' to make sure that there are no editors for the file 
		logger.strongStep("Verify that the current user '" + testUser.getDisplayName() + "' does not have editor rights to the file");
		log.info("INFO: Verify that the current user '" + testUser.getDisplayName() + "' does not have editor rights to the file");
		Assert.assertTrue(ui.fluentWaitTextPresent("Editors - 0"),
				"The file has zero editors");

		//Verify that the link for the file is not present on 'My Files' page when List view is selected
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, false);

		//Verify that the link for the file is present on the folder's page when List view is selected
		verifyPresenceOfFileOnAFolderPage(logger, "List", folder, baseFileImage);

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Homepage component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Homepage component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.login(testUserNew);

		//Verify the notification and the news item related to the transfer of ownership for the file on the Homepage
		verifyNewsItemAndNotificationForOwnershipTransfer(logger, testUser, baseFileImage);

		logger.strongStep("Navigate to the Files component");
		log.info("INFO: Navigate to the Files component");
		ui.loadComponent(Data.getData().ComponentFiles, true);

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		logger.strongStep("Verify that the timestamp for updation of the file is correct");
		log.info("INFO: Verify that the timestamp for updation of the file is correct");
		Assert.assertEquals(driver.getSingleElement("css=tr[dndelementtitle='" + baseFileImage.getName() + "'] li.lotusFirst").getText(), fileUpdationTimestamp,
				"The timestamp for updation of the file is correct");

		logger.strongStep("Click on the Custom view");
		log.info("INFO: Click on the Custom view");
		Files_Display_Menu.SUMMARY.select(ui);

		logger.strongStep("Click on the Actions menu for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the Actions menu for the file: " + baseFileImage.getName());
		ui.clickLinkWait(ui.fileSpecificActionMenu(baseFileImage));

		logger.strongStep("Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"The current user '" + testUserNew.getDisplayName() + "' has the owner right to delete the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"The current user '" + testUserNew.getDisplayName() + "' has the owner right to transfer the ownership of the file");

		logger.strongStep("Verify that the status of 'Sharing' for the file is 'Shared'");
		log.info("INFO: Verify that the status of 'Sharing' for the file is 'Shared'");
		Assert.assertEquals(driver.getSingleElement(ui.fileSpecificShareStatus(baseFileImage)).getAttribute("title"), "Shared",
				"The status of 'Sharing' for the file is not 'Shared'");

		logger.strongStep("Click on the link for the file");
		log.info("INFO: Click on the link for the file");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Verify that the file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible");
		log.info("INFO: Verify that the file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible");
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.EditFilenameLink).getText().equals(baseFileImage.getName()),
				"The name of the file is visible in the FIDO Viewer");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage),
				"The image preview for the file is visible in the FIDO Viewer");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_panelAboutThisFile),
				"The About tab of the FIDO Viewer is visible");

		logger.strongStep("Click on the About tab in the FIDO Viewer");
		log.info("INFO: Click on the About tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.fileviewer_panelAboutThisFile);

		logger.strongStep("Verify that all tags added to the file are visible in the About tab of the FIDO Viewer");
		log.info("INFO: Verify that all tags added to the file are visible in the About tab of the FIDO Viewer");
		ui.fluentWaitElementVisible(FileViewerUI.TagsContainer);
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag1) && 
				driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag2) &&
				driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag3),
				"The tags added to the file are visible in the About tab of the FIDO Viewer");

		logger.strongStep("Click on the Folders button in the left panel");
		log.info("INFO: Click on the Folders button in the left panel");
		ui.clickMyFoldersView();

		driver.changeImplicitWaits(3);

		logger.strongStep("Check if the text 'You have not created any folders.' is present");
		log.info("INFO: Check if the text 'You have not created any folders.' is present");
		if (!driver.isTextPresent(Data.getData().NoFoldersFound)) {

			logger.strongStep("Click on the List view");
			log.info("INFO: Click on the List view");
			Files_Display_Menu.DETAILS.select(ui);

			logger.strongStep("Verify that the folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");
			log.info("INFO: Verify that the folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");
			Assert.assertFalse(driver.isElementPresent(FilesUI.selectMyFolder(folder)),
					"The folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");

		}

		driver.turnOnImplicitWaits();

		logger.strongStep("Delete the file as well as the folder");
		log.info("INFO: Delete the file as well as the folder");
		apiFileOwner.deleteFile(privateFile);
		apiFileOwner.deleteFile(privateFolder);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify that a user is able to transfer the ownership of a shared file from inside of a private folder</li>
	*<li><B>Step:</B>Upload a file(.jpg) using API with user1 as the owner and share it with user2</li>
	*<li><B>Step:</B>Create a new private folder as its owner</li>
	*<li><B>Step:</B>Add the file previously uploaded to the folder via API</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel then click on the link for the folder</li>
	*<li><B>Step:</B>Click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on the folder's page</li>
	*<li><B>Step:</B>Click on the link for the file</li>
	*<li><B>Step:</B>Click on the Ellipsis menu in the FIDO Viewer</li>
	*<li><B>Step:</B>Transfer the ownership of the file to user2 using the 'Transfer Ownership' dialog box</li>
	*<li><B>Step:</B>Click on the List view</li>
	*<li><B>Step:</B>Grab the file updation link and click on the link for the file</li>
	*<li><B>Step:</B>Navigate to the Sharing tab in the FIDO Viewer</li>
	*<li><B>Verify:</B>The new owner of the file is user2</li>
	*<li><B>Verify:</B>The current user does not have editor rights to the file</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Verify:</B>The file is no longer visible on My Files page</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel and select the List view</li>
	*<li><B>Step:</B>Click on the link for the folder</li>
	*<li><B>Verify:</B>The link for the file is still visible on the folder's page</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Homepage component and login as user2</li>
	*<li><B>Step:</B>Click on the Notifications icon at the top of the page</li>
	*<li><B>Verify:</B>The notification for file ownership transfer is visible</li>
	*<li><B>Step:</B>Click on the 'My Notifications' button in the left panel</li>
	*<li><B>Verify:</B>The news item for the file ownership transfer is visible along with the image</li>
	*<li><B>Step:</B>Navigate to the Files component and click on My Files button in the left panel/li>
	*<li><B>Step:</B>Click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Verify:</B>The timestamp for updation of the file is correct</li>
	*<li><B>Step:</B>Click on the Details view and click on the Actions menu for the file</li>
	*<li><B>Verify:</B>The current user has owner rights to the file (Transfer Ownership and Deletion)</li>
	*<li><B>Verify:</B>The status of 'Sharing' for the file is 'Shared</li>
	*<li><B>Step:</B>Click on the link for the file</li>
	*<li><B>Verify:</B>The file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible</li>
	*<li><B>Step:</B>Click on the About tab in the FIDO Viewer</li>
	*<li><B>Verify:</B>The tags added to the file are visible in the About tab of the FIDO Viewer</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel and select the List view</li>
	*<li><B>Verify:</B>The folder created by user1 is not visible to user2 on My Folders page</li>
	*<li><B>Step:</B>Delete the file as well as the folder</li>
	*</ul>
	*/
	@Test(groups = { "regression" }) //Commented out the steps that are failing right now because of the defects - https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10332 and https://jira.cwp.pnp-hcl.com/browse/CNXSERV-10333
	public void transferOwnershipOfSharedFileInAPrivateFolder() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String tag1 = "first_tag_" + Helper.genDateBasedRand();
		String tag2 = "second_tag_" + Helper.genDateBasedRand();
		String tag3 = "third_tag_" + Helper.genDateBasedRand();

		User testUserNew = cfg.getUserAllocator().getUser();

		APIProfilesHandler apiFileFollower = new APIProfilesHandler(serverURL, testUserNew.getEmail(), testUserNew.getPassword());

		ui.startTest();

		//Create the BaseFile instance of a shared image file
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file2).extension(".jpg")
				.tags(tag1 + " " + tag2)
				.rename("SharedFile" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.PEOPLE).sharedWith(apiFileFollower.getUUID())
				.build();

		//Create the BaseFile instance of a private folder
		BaseFile baseFolder = new BaseFile.Builder("MyTopFolder" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE)
				.build();

		//Create the BaseFolder instance of the same private folder
 	    BaseFolder folder = new BaseFolder.Builder(baseFolder.getName())
                 .build();

		logger.strongStep("Upload a file(.jpg) using API with '" + testUser.getDisplayName() + "' as the owner and share it with '" + testUserNew.getDisplayName() + "'");
		log.info("INFO: Upload a file(.jpg) using API with '" + testUser.getDisplayName() + "' as the owner and share it with '" + testUserNew.getDisplayName() + "'");
		FileEntry privateFile = FileEvents.addFile(baseFileImage, testUser, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		logger.strongStep("Create a new private folder as its owner");
		log.info("INFO: Create a new private folder as its owner");
		FileEntry privateFolder = apiFileOwner.createFolder(baseFolder, Role.OWNER);

		logger.strongStep("Load the Files component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUserNew);

		logger.strongStep("Click on the 'Files Shared With Me' button in the left panel");
		log.info("INFO: Click on the 'Files Shared With Me' button in the left panel");
		ui.clickLinkWithJavascript(FilesUIConstants.filesSharedWithMe);

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Verify that the link for the file is visible on 'Files Shared With Me' page");
		log.info("INFO: Verify that the link for the file is visible on 'Files Shared With Me' page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(baseFileImage)),
				"The link for the file is visible on 'Files Shared With Me' page");

		logger.strongStep("Click on the link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Navigate to the Sharing tab in the FIDO Viewer");
		log.info("INFO: Navigate to the Sharing tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

		logger.strongStep("Verify that the user '" + testUser.getDisplayName() + "' has owner rights to the file");
		log.info("INFO: Verify that the user '" + testUser.getDisplayName() + "' has owner rights to the file");
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.fileOwnerUserLink).getText().equals(testUser.getDisplayName()),
				"The user '" + testUser.getDisplayName() + "' has owner rights to the file");

		logger.strongStep("Verify that the current user '" + testUserNew.getDisplayName() + "' has editor rights to the file");
		log.info("INFO: Verify that the current user '" + testUserNew.getDisplayName() + "' has editor rights to the file");
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.fileEditorUserLink).getText().equals(testUserNew.getDisplayName()),
				"The user '" + testUserNew.getDisplayName() + "' has editor rights to the file");

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles,true);
		ui.login(testUser);

		//Verify that the link for the file is present on 'My Files' page when List view is selected
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		logger.strongStep("Add the file previously uploaded to the folder");
		log.info("INFO: Add the file previously uploaded to the folder");
		FileEvents.addFileToFolder(testUser, apiFileOwner, privateFile, privateFolder);

		//Verify that the link for the file is present on the folder's page when List view is selected
		verifyPresenceOfFileOnAFolderPage(logger, "List", folder, baseFileImage);

		logger.strongStep("Click on the More link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the More link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(baseFileImage));

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		//Transfer the ownership of the file to another user using the 'Transfer Ownership' dialog box
		transferOwnershipUsingDialogBox(logger, testUserNew, tag3, baseFileImage);

		logger.strongStep("Grab the file updation timestamp");
		log.info("INFO: Grab the file updation timestamp");
		String fileUpdationTimestamp = driver.getSingleElement(FilesUIConstants.fileUpdationTimestamp.replaceAll("PLACEHOLDER", baseFileImage.getName())).getText();

		logger.strongStep("Click on the link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Navigate to the Sharing tab in the FIDO Viewer");
		log.info("INFO: Navigate to the Sharing tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

		logger.strongStep("Verify that the new owner of the file is: " + testUserNew.getDisplayName());
		log.info("INFO: Verify that the new owner of the file is: " + testUserNew.getDisplayName());
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.fileOwnerUserLink).getText().equals(testUserNew.getDisplayName()),
				"The new owner of the file is: " + testUserNew.getDisplayName());

		logger.strongStep("Verify that the current user '" + testUser.getDisplayName() + "' does not have editor rights to the file");
		log.info("INFO: Verify that the current user '" + testUser.getDisplayName() + "' does not have editor rights to the file");
		Assert.assertTrue(ui.fluentWaitTextPresent("Editors - 1"),
				"The file has one or more editors");
		
		//Verify that the link for the file is not present on 'My Files' page when List view is selected
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, false);

		//Verify that the link for the file is present on the folder's page when List view is selected
		verifyPresenceOfFileOnAFolderPage(logger, "List", folder, baseFileImage);

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Homepage component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Homepage component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentHomepage, true);
		ui.login(testUserNew);

		//Verify the notification and the news item related to the transfer of ownership for the file on the Homepage
		verifyNewsItemAndNotificationForOwnershipTransfer(logger, testUser, baseFileImage);

		logger.strongStep("Navigate to the Files component");
		log.info("INFO: Navigate to the Files component");
		ui.loadComponent(Data.getData().ComponentFiles, true);

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		logger.strongStep("Verify that the timestamp for updation of the file is correct");
		log.info("INFO: Verify that the timestamp for updation of the file is correct");
		Assert.assertEquals(driver.getSingleElement("css=tr[dndelementtitle='" + baseFileImage.getName() + "'] li.lotusFirst").getText(), fileUpdationTimestamp,
				"The timestamp for updation of the file is correct");

		logger.strongStep("Click on the Custom view");
		log.info("INFO: Click on the Custom view");
		Files_Display_Menu.SUMMARY.select(ui);

		logger.strongStep("Click on the Actions menu for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the Actions menu for the file: " + baseFileImage.getName());
		ui.clickLinkWait(ui.fileSpecificActionMenu(baseFileImage));

		logger.strongStep("Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"The current user '" + testUserNew.getDisplayName() + "' has the owner right to delete the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"The current user '" + testUserNew.getDisplayName() + "' has the owner right to transfer the ownership of the file");

		logger.strongStep("Verify that the status of 'Sharing' for the file is 'Shared'");
		log.info("INFO: Verify that the status of 'Sharing' for the file is 'Shared'");
		Assert.assertEquals(driver.getSingleElement(ui.fileSpecificShareStatus(baseFileImage)).getAttribute("title"), "Shared",
				"The status of 'Sharing' for the file is not 'Shared'");

		logger.strongStep("Click on the link for the file");
		log.info("INFO: Click on the link for the file");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Verify that the file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible");
		log.info("INFO: Verify that the file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible");
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.EditFilenameLink).getText().equals(baseFileImage.getName()),
				"The name of the file is visible in the FIDO Viewer");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage),
				"The image preview for the file is visible in the FIDO Viewer");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_panelAboutThisFile),
				"The About tab of the FIDO Viewer is visible");

		logger.strongStep("Click on the About tab in the FIDO Viewer");
		log.info("INFO: Click on the About tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.fileviewer_panelAboutThisFile);

		logger.strongStep("Verify that all tags added to the file are visible in the About tab of the FIDO Viewer");
		log.info("INFO: Verify that all tags added to the file are visible in the About tab of the FIDO Viewer");
		ui.fluentWaitElementVisible(FileViewerUI.TagsContainer);
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag1) && 
				driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag2) &&
				driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag3),
				"The tags added to the file are visible in the About tab of the FIDO Viewer");

		logger.strongStep("Click on the Folders button in the left panel");
		log.info("INFO: Click on the Folders button in the left panel");
		ui.clickMyFoldersView();

		driver.changeImplicitWaits(3);

		logger.strongStep("Check if the text 'You have not created any folders.' is present");
		log.info("INFO: Check if the text 'You have not created any folders.' is present");
		if (!driver.isTextPresent(Data.getData().NoFoldersFound)) {

			logger.strongStep("Click on the List view");
			log.info("INFO: Click on the List view");
			Files_Display_Menu.DETAILS.select(ui);

			logger.strongStep("Verify that the folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");
			log.info("INFO: Verify that the folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");
			Assert.assertFalse(driver.isElementPresent(FilesUI.selectMyFolder(folder)),
					"The folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");

		}

		driver.turnOnImplicitWaits();

		logger.strongStep("Click on the 'Files Shared With Me' button in the left panel");
		log.info("INFO: Click on the 'Files Shared With Me' button in the left panel");
		ui.clickLinkWithJavascript(FilesUIConstants.filesSharedWithMe);
		driver.turnOffImplicitWaits();
		
		logger.strongStep("Verify that the link for the file is not visible on 'Files Shared With Me' page anymore");
		log.info("INFO: Verify that the link for the file is not visible on 'Files Shared With Me' page anymore");
		Assert.assertFalse(ui.isElementVisible(FilesUI.getFileIsUploaded(baseFileImage)),
				"The link for the file is still visible on 'Files Shared With Me' page");
		driver.turnOnImplicitWaits();
		

		logger.strongStep("Delete the file as well as the folder");
		log.info("INFO: Delete the file as well as the folder");
		apiFileOwner.deleteFile(privateFile);
		apiFileOwner.deleteFile(privateFolder);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify that a user is able to transfer the ownership of a file that is locked by the same user</li>
	*<li><B>Step:</B>Login and logout user2 so that the user's name appears in the 'Transfer Ownership' typeahead</li>
	*<li><B>Step:</B>Upload a private file(.jpg) using API with user1 as the owner</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the More link for the file</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Step:</B>Select the 'Lock File' option from the menu</li>
	*<li><B>Verify:</B>The message 'The file is now locked.' appears on the screen</li>
	*<li><B>Verify:</B>The file is locked using the 'Locked by you' icon and the 'Unlock File' link</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file and select the 'Transfer Ownership' option</li>
	*<li><B>Step:</B>Transfer the ownership of the file to user2 using the 'Transfer Ownership' dialog box</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Verify:</B>The file is no longer visible on My Files page</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Files component and login as user2</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the More link for the file</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The current user has owner rights to the file (Transfer Ownership and Deletion)</li>
	*<li><B>Verify:</B>The user cannot edit the file as it is locked</li>
	*<li><B>Verify:</B>The file is locked by user1</li>
	*<li><B>Step:</B>Unlock the file</li>
	*<li><B>Verify:</B>The message 'The file is now unlocked.' appears on the screen</li>
	*<li><B>Verify:</B>The 'Add or Remove Tags' link is now visible for the file</li>
	*<li><B>Verify:</B>The 'Upload New Version...' link is now visible for the file</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The 'Edit Properties...' option is now visible in the 'More Actions' menu</li>
	*<li><B>Step:</B>Delete the file</li>
	*</ul>
	*/
	@Test(groups = { "level2","cnx8ui-level2" })
	public void transferOwnershipOfAFileLockedByTheFileCreator() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String tag1 = "first_tag_" + Helper.genDateBasedRand();
		String tag2 = "second_tag_" + Helper.genDateBasedRand();
		String tag3 = "third_tag_" + Helper.genDateBasedRand();

		User testUserNew = cfg.getUserAllocator().getUser();

		ui.startTest();

		//Create the BaseFile instance of a private image file
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file3).extension(".jpg")
				.tags(tag1 + " " + tag2)
				.rename("LockedPrivateFile" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE)
				.build();

		logger.strongStep("Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		log.info("INFO: Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		loginAndLogoutTheNewFileOwner(logger, testUserNew);
		ui.close(cfg);

		logger.strongStep("Upload a private file(.jpg) using API");
		log.info("INFO: Upload a private file(.jpg) using API");
		FileEntry privateFile = FileEvents.addFile(baseFileImage, testUser, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		logger.strongStep("Click on the More link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the More link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(baseFileImage));

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Select the 'Lock File' option from the menu");
		log.info("INFO: Select the 'Lock File' option from the menu");
		ui.clickLinkWithJavascript(FilesUIConstants.LockFileOption);

		logger.strongStep("Verify that the message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");
		log.info("INFO: Verify that the message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");
		Assert.assertTrue(ui.fluentWaitTextPresent(FilesUIConstants.fileLockedMessage),
				"The message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");

		logger.strongStep("Verify that the file is locked using the 'Locked by you' icon and the 'Unlock File' link");
		log.info("INFO: Verify that the file is locked using the 'Locked by you' icon and the 'Unlock File' link");
		Assert.assertTrue(ui.isFileLocked(baseFileImage),
				"The file has been locked properly since the 'Locked by you' icon and the 'Unlock File' link are visible");

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		//Transfer the ownership of the file to another user using the 'Transfer Ownership' dialog box
		transferOwnershipUsingDialogBox(logger, testUserNew, tag3, baseFileImage);

		//Verify that the link for the file is not present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, false);

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.loginAndToggleUI(testUserNew,cfg.getUseNewUI());

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		logger.strongStep("Click on the More link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the More link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(baseFileImage));

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"The current user '" + testUserNew.getDisplayName() + "' has the owner right to delete the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"The current user '" + testUserNew.getDisplayName() + "' has the owner right to transfer the ownership of the file");

		driver.turnOffImplicitWaits();

		logger.strongStep("Verify that the user cannot edit the file as it is locked");
		log.info("INFO: Verify that the user cannot edit the file as it is locked");
		Assert.assertFalse(ui.isElementVisible(FilesUIConstants.EditPropertiesOption),
				"The 'Edit Properties...' option is not visible in the 'More Actions' menu as the file is locked");

		driver.turnOnImplicitWaits();

		logger.strongStep("Verify that the file is locked by: " + testUser.getDisplayName());
		log.info("INFO: Verify that the file is locked by: " + testUser.getDisplayName());
		Assert.assertTrue(ui.fluentWaitElementVisible("xpath=//a[contains(@title,'" + baseFileImage.getName() + "')]/ancestor::tr[@class='lotusDetails']/descendant::a[contains(text(),'" + testUser.getDisplayName() + "')]"),
				"The file is locked by: " + testUser.getDisplayName());

		logger.strongStep("Unlock the file: " + baseFileImage.getName());
		log.info("INFO: Unlock the file: " + baseFileImage.getName());
		ui.unlockFile(baseFileImage);

		logger.strongStep("Verify that the message '" + FilesUIConstants.fileUnlockedMessage + "' appears on the screen");
		log.info("INFO: Verify that the message '" + FilesUIConstants.fileUnlockedMessage + "' appears on the screen");
		Assert.assertTrue(ui.fluentWaitTextPresent(FilesUIConstants.fileUnlockedMessage),
				"The message '" + FilesUIConstants.fileUnlockedMessage + "' appears on the screen");

		logger.strongStep("Verify that the 'Add or Remove Tags' link is now visible for the file");
		log.info("INFO: Verify that the 'Add or Remove Tags' link is now visible for the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.addOrRemoveTagsLink),
				"The 'Add or Remove Tags' link is visible for the file as the file is unlocked");

		logger.strongStep("Verify that the 'Upload New Version...' link is now visible for the file");
		log.info("INFO: Verify that the 'Upload New Version...' link is now visible for the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.uploadNewVersionLink),
				"The 'Upload New Version...' link is visible for the file as the file is unlocked");

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Verify that the 'Edit Properties...' option is now visible in the 'More Actions' menu");
		log.info("INFO: Verify that the 'Edit Properties...' option is now visible in the 'More Actions' menu");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.EditPropertiesOption),
				"The 'Edit Properties...' option is visible in the 'More Actions' menu as the file is unlocked");

		logger.strongStep("Delete the file");
		log.info("INFO: Delete the file");
		apiFileOwner.deleteFile(privateFile);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify that a user is able to transfer the ownership of a shared file that is locked by the editor of the file</li>
	*<li><B>Step:</B>Upload a shared file(.xls) using API with user1 as owner and user2 as the editor</li>
	*<li><B>Step:</B>Load the Files component and login as user2</li>
	*<li><B>Step:</B>Click on the 'Files Shared With Me' button in the left panel</li>
	*<li><B>Step:</B>Click on the Details view and click on the Actions menu for the file</li>
	*<li><B>Step:</B>Select the 'Lock File' option from the Actions menu</li>
	*<li><B>Verify:</B>The message 'The file is now locked.' appears on the screen</li>
	*<li><B>Step:</B>Click on the List view and click on the More link for the file</li>
	*<li><B>Verify:</B>The file is locked using the 'Locked by you' icon and the 'Unlock File' link</li>
	*<li><B>Verify:</B>The 'Add or Remove Tags' link is visible for the file since the file has been locked by the current user</li>
	*<li><B>Verify:</B>The 'Upload New Version...' link is visible for the file since the file has been locked by the current user</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The 'Edit Properties...' option is visible in the 'More Actions' menu since the file has been locked by the current user</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Step:</B>Click on the Details view and click on the Actions menu for the file</li>
	*<li><B>Step:</B>Select the 'Transfer Ownership' option in the Actions menu</li>
	*<li><B>Step:</B>Transfer the ownership of the file to user2 using the 'Transfer Ownership' dialog box</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Verify:</B>The file is no longer visible on My Files page</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Files component and login as user2</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the More link for the file</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The current user has owner rights to the file (Transfer Ownership and Deletion)</li>
	*<li><B>Step:</B>Unlock the file</li>
	*<li><B>Verify:</B>The message 'The file is now unlocked.' appears on the screen</li>
	*<li><B>Verify:</B>The file has been unlocked properly and that the 'Locked by you' icon and the 'Unlock File' link are not visible</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The file has been unlocked properly and the 'Lock File' option is visible in the 'More Actions' menu</li>
	*<li><B>Step:</B>Delete the file</li>
	*</ul>
	*/
	@Test(groups = { "regression" })
	public void transferOwnershipOfAFileLockedByTheFileEditor() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String tag1 = "first_tag_" + Helper.genDateBasedRand();
		String tag2 = "second_tag_" + Helper.genDateBasedRand();

		User testUserNew = cfg.getUserAllocator().getUser();

		APIProfilesHandler apiFileFollower = new APIProfilesHandler(serverURL, testUserNew.getEmail(), testUserNew.getPassword());

		ui.startTest();

		//Create the BaseFile instance of a shared Excel file
		BaseFile baseFileExcel = new BaseFile.Builder(Data.getData().file22).extension(".xls")
				.tags(tag1 + " " + tag2)
				.rename("SharedFile" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.PEOPLE).sharedWith(apiFileFollower.getUUID())
				.build();

		logger.strongStep("Upload a shared file(.xls) using API");
		log.info("INFO: Upload a shared file(.xls) using API");
		FileEntry sharedFile = FileEvents.addFile(baseFileExcel, testUser, apiFileOwner);
		baseFileExcel.setName(baseFileExcel.getRename() + baseFileExcel.getExtension());

		logger.strongStep("Load the Files component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUserNew);

		logger.strongStep("Click on the 'Files Shared With Me' button in the left panel");
		log.info("INFO: Click on the 'Files Shared With Me' button in the left panel");
		ui.clickLinkWithJavascript(FilesUIConstants.filesSharedWithMe);

		logger.strongStep("Click on the Custom view");
		log.info("INFO: Click on the Custom view");
		Files_Display_Menu.SUMMARY.select(ui);

		logger.strongStep("Click on the Actions menu for the file: " + baseFileExcel.getName());
		log.info("INFO: Click on the Actions menu for the file: " + baseFileExcel.getName());
		ui.clickLinkWait(ui.fileSpecificActionMenu(baseFileExcel));

		//Lock the file and then verify the file options
		lockAFileThenVerifyTheFileOptions(logger, false, baseFileExcel);

		logger.strongStep("Logout as the current user: " + testUserNew.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUserNew.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser);

		logger.strongStep("Click on the 'My Files' button in the left panel");
		log.info("INFO: Click on the 'My Files' button in the left panel");
		ui.clickMyFilesView();

		logger.strongStep("Click on the Custom view");
		log.info("INFO: Click on the Custom view");
		Files_Display_Menu.SUMMARY.select(ui);

		logger.strongStep("Click on the Actions menu for the file: " + baseFileExcel.getName());
		log.info("INFO: Click on the Actions menu for the file: " + baseFileExcel.getName());
		ui.clickLinkWait(ui.fileSpecificActionMenu(baseFileExcel));

		//Transfer the ownership of the file to another user using the 'Transfer Ownership' dialog box
		transferOwnershipUsingDialogBox(logger, testUserNew, "", baseFileExcel);

		//Verify that the link for the file is not present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileExcel, false);

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUserNew);

		//Verify the file as the user who locked the file and is also the new owner of the file post the transfer of ownership
		verifyAFileAsTheUserWhoLockedTheFileAndIsAlsoTheNewFileOwner(logger, testUserNew, baseFileExcel);

		logger.strongStep("Delete the file");
		log.info("INFO: Delete the file");
		apiFileOwner.deleteFile(sharedFile);

		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify that a user is able to transfer the ownership of a public file inside a public folder with everyone in the organization as editors and the file locked by one of the public editors</li>
	*<li><B>Step:</B>Upload a public file(.pdf) using API</li>
	*<li><B>Step:</B>Create a new public folder as its owner using API</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the Folders button in the left panel</li>
	*<li><B>Step:</B>Click on the List view and click on the More link for the folder created earlier</li>
	*<li><B>Step:</B>Click on the 'Share...' button for the folder</li>
	*<li><B>Step:</B>Select the checkbox 'Everyone in my organization can edit this folder'</li>
	*<li><B>Step:</B>Click on the Share button</li>
	*<li><B>Step:</B>Add the file previously uploaded to the folder</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Files component and login as user2</li>
	*<li><B>Step:</B>Click on the 'Public Files' button in the left panel</li>
	*<li><B>Step:</B>Click on the Grid view and click on the thumbnail for the file</li>
	*<li><B>Step:</B>Click on the Ellipsis menu in the FIDO Viewer</li>
	*<li><B>Step:</B>Select the 'Lock File' option from the menu</li>
	*<li><B>Verify:</B>The message 'The file is now locked.' appears on the screen</li>
	*<li><B>Step:</B>Close the FIDO viewer and click on the List view</li>
	*<li><B>Step:</B>Click on the More link for the file</li>
	*<li><B>Verify:</B>The file is locked using the 'Locked by you' icon and the 'Unlock File' link</li>
	*<li><B>Verify:</B>The 'Add or Remove Tags' link is visible for the file since the file has been locked by the current user</li>
	*<li><B>Verify:</B>The 'Upload New Version...' link is visible for the file since the file has been locked by the current user</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The 'Edit Properties...' option is visible in the 'More Actions' menu since the file has been locked by the current user</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Step:</B>Click on the Grid view and click on the thumbnail for the file</li>
	*<li><B>Step:</B>Click on the Ellipsis menu in the FIDO Viewer and select the 'Transfer Ownership' option</li>
	*<li><B>Step:</B>Transfer the ownership of the file to user2 using the 'Transfer Ownership' dialog box</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel</li>
	*<li><B>Verify:</B>The file is no longer visible on My Files page</li>
	*<li><B>Step:</B>Logout as the current user</li>
	*<li><B>Step:</B>Load the Files component and login as user2</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the More link for the file</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The current user has owner rights to the file (Transfer Ownership and Deletion)</li>
	*<li><B>Step:</B>Unlock the file</li>
	*<li><B>Verify:</B>The message 'The file is now unlocked.' appears on the screen</li>
	*<li><B>Verify:</B>The file has been unlocked properly and that the 'Locked by you' icon and the 'Unlock File' link are not visible</li>
	*<li><B>Step:</B>Click on the 'More Actions' button for the file</li>
	*<li><B>Verify:</B>The file has been unlocked properly and the 'Lock File' option is visible in the 'More Actions' menu</li>
	*<li><B>Step:</B>Delete the file as well as the folder</li>
	*</ul>
	*/
	@Test(groups = { "regression" })
	public void transferOwnershipOfAFileLockedByTheFolderEditor() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String tag1 = "first_tag_" + Helper.genDateBasedRand();
		String tag2 = "second_tag_" + Helper.genDateBasedRand();

		User testUserNew = cfg.getUserAllocator().getUser();

		ui.startTest();

		//Create the BaseFile instance of a public PDF file
		BaseFile baseFilePDF = new BaseFile.Builder(Data.getData().file19).extension(".pdf")
				.tags(tag1 + " " + tag2)
				.rename("PublicFile" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		//Create the BaseFile instance of a public folder
		BaseFile baseFolder = new BaseFile.Builder("MyPublicFolder" + Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		//Create the BaseFolder instance of the same public folder
 	    BaseFolder folder = new BaseFolder.Builder(baseFolder.getName())
                 .build();

		logger.strongStep("Upload a public file(.pdf) using API");
		log.info("INFO: Upload a public file(.pdf) using API");
		FileEntry publicFile = FileEvents.addFile(baseFilePDF, testUser, apiFileOwner);
		baseFilePDF.setName(baseFilePDF.getRename() + baseFilePDF.getExtension());

		logger.strongStep("Create a new public folder as its owner");
		log.info("INFO: Create a new public folder as its owner");
		FileEntry publicFolder = apiFileOwner.createFolder(baseFolder, Role.OWNER);

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		logger.strongStep("Click on the Folders button in the left panel");
		log.info("INFO: Click on the Folders button in the left panel");
		ui.clickMyFoldersView();

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Click on the More link for the folder: " + baseFolder.getName());
		log.info("INFO: Click on the More link for the folder: " + baseFolder.getName());
		ui.clickLinkWithJavascript(FilesUI.selectOneFolder(folder));

		logger.strongStep("Select the 'Share...' option from the folder's drop down menu");
		log.info("INFO: Select the 'Share...' option from the folder's drop down menu");
		Files_Folder_Dropdown_Menu.SHARE.select(ui);

		logger.strongStep("Select the checkbox 'Everyone in my organization can edit this folder'");
		log.info("INFO: Select the checkbox 'Everyone in my organization can edit this folder'");
		ui.clickLinkWithJavascript(FilesUIConstants.everyoneCanEditTheFolder);

		logger.strongStep("Click on the Share button");
		log.info("INFO: Click on the Share button");
		ui.clickLinkWithJavascript(FilesUIConstants.submitButton);

		logger.strongStep("Add the file '" + baseFilePDF.getName() + "' to the folder '" + baseFolder.getName() + "'");
		log.info("INFO: Add the file '" + baseFilePDF.getName() + "' to the folder '" + baseFolder.getName() + "'");
		ui.folderAddFiles(baseFolder.getName(), baseFilePDF.getName());

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUserNew);

		logger.strongStep("Click on the 'Public Files' button in the left panel");
		log.info("INFO: Click on the 'Public Files' button in the left panel");
		ui.clickLinkWithJavascript(FilesUIConstants.PublicFilesInNav);

		logger.strongStep("Click on the Grid view");
		log.info("INFO: Click on the Grid view");
		Files_Display_Menu.TILE.select(ui);

		logger.strongStep("Click on the thumbnail for the file: " + baseFilePDF.getName());
		log.info("INFO: Click on the thumbnail for the file: " + baseFilePDF.getName());
		driver.getSingleElement(FilesUI.selectFileInGridView(baseFilePDF)).hover();
		ui.clickLinkWithJavascript(FilesUI.previewLinkInGridView(baseFilePDF));

		logger.strongStep("Click on the Ellipsis menu in the FIDO Viewer");
		log.info("INFO: Click on the Ellipsis menu in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);

		//Lock the file and then verify the file options
		lockAFileThenVerifyTheFileOptions(logger, true, baseFilePDF);

		logger.strongStep("Logout as the current user: " + testUserNew.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUserNew.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser);

		logger.strongStep("Click on the 'My Files' button in the left panel");
		log.info("INFO: Click on the 'My Files' button in the left panel");
		ui.clickMyFilesView();

		logger.strongStep("Click on the Grid view");
		log.info("INFO: Click on the Grid view");
		Files_Display_Menu.TILE.select(ui);

		logger.strongStep("Click on the thumbnail for the file: " + baseFilePDF.getName());
		log.info("INFO: Click on the thumbnail for the file: " + baseFilePDF.getName());
		driver.getSingleElement(FilesUI.selectFileInGridView(baseFilePDF)).hover();
		ui.clickLinkWithJavascript(FilesUI.previewLinkInGridView(baseFilePDF));

		logger.strongStep("Click on the Ellipsis menu in the FIDO Viewer");
		log.info("INFO: Click on the Ellipsis menu in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);

		//Transfer the ownership of the file to another user using the 'Transfer Ownership' dialog box
		transferOwnershipUsingDialogBox(logger, testUserNew, "", baseFilePDF);

		//Verify that the link for the file is not present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFilePDF, false);

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUserNew);

		//Verify the file as the user who locked the file and is also the new owner of the file post the transfer of ownership
		verifyAFileAsTheUserWhoLockedTheFileAndIsAlsoTheNewFileOwner(logger, testUserNew, baseFilePDF);

		logger.strongStep("Delete the file as well as the folder");
		log.info("INFO: Delete the file as well as the folder");
		apiFileOwner.deleteFile(publicFile);
		apiFileOwner.deleteFile(publicFolder);

		ui.endTest();

	}

	/**
	*This method transfers the ownership of a file using the Transfer Ownership dialog box
	*@param logger - DefectLogger instance for logging
	*@param user - User instance for the new owner of the file
	*@param tag - String instance for the tags that need to be added to the file
	*@param file - BaseFile instance for the file that was transferred to a new user
	*/
	public void transferOwnershipUsingDialogBox(DefectLogger logger, User user, String tag, BaseFile file) {

		logger.strongStep("Click on the 'Transfer Ownership...' link");
		log.info("INFO: Click on the 'Transfer Ownership...' link");
		ui.clickLinkWithJavascript(FilesUIConstants.ClickForActionsOptionTransferOwnership);

		logger.strongStep("Verify that the 'Transfer Ownership' dialog box appears on the page");
		log.info("INFO: Verify that the 'Transfer Ownership' dialog box appears on the page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.transferOwnershipDialogBox),
				"The 'Transfer Ownership' dialog box appears on the page");

		logger.strongStep("Type '" + user.getDisplayName() + "' in the 'New Owner' text box");
		log.info("INFO: Type '" + user.getDisplayName() + "' in the 'New Owner' text box");
		ui.typeTextWithDelay(FilesUIConstants.shareFileDialogInputBox, user.getDisplayName());

		int i = 0;

		//Store all suggestions from the typeahead in a list
		List <Element> typeAheadResults = driver.getVisibleElements(FilesUIConstants.shareFolderDialogPersonTypeahead);
		for (Element ele : typeAheadResults) {
			if (ele.getText().contains(user.getDisplayName())) {
				logger.strongStep("Click on the suggestion for '" + user.getDisplayName() + "' in the typeahead");
				log.info("INFO: Click on the suggestion for '" + user.getDisplayName() + "' in the typeahead");
				ele.click();
				i++;
			}
		}

		logger.strongStep("Verify that atleast one suggestion appears for '" + user.getDisplayName() + "' in the typeahead");
		log.info("INFO: Verify that atleast one suggestion appears for '" + user.getDisplayName() + "' in the typeahead");
		Assert.assertTrue(i > 0, "The suggestion for '" + user.getDisplayName() + "' appeared  in the typeahead");

		logger.strongStep("Type a new tag in the Tags text box");
		log.info("INFO: Type a new tag in the Tags text box");
		ui.typeText(FilesUIConstants.UploadFiles_Tag, tag);

		logger.strongStep("Click on the Transfer button");
		log.info("INFO: Click on the Transfer button");
		ui.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);

		logger.strongStep("Verify that the message '" + file.getName() + " was successfully transferred to " + user.getDisplayName() + ".' appears on the page");
		log.info("INFO: Verify that the message '" + file.getName() + " was successfully transferred to " + user.getDisplayName() + ".' appears on the page");
		Assert.assertTrue(ui.fluentWaitTextPresent(file.getName() + " was successfully transferred to " + user.getDisplayName() + "."),
				"The message '" + file.getName() + " was successfully transferred to " + user.getDisplayName() + ".' appears on the page");

		driver.turnOffImplicitWaits();

		logger.strongStep("Verify that the 'Transfer Ownership' dialog box is not visible on the page anymore");
		log.info("INFO: Verify that the 'Transfer Ownership' dialog box is not visible on the page anymore");
		Assert.assertFalse(ui.isElementVisible(FilesUIConstants.transferOwnershipDialogBox),
				"The 'Transfer Ownership' dialog box is not visible on the page anymore");

		driver.turnOnImplicitWaits();

	}

	/**
	*This method verifies the News Item and Notification for Ownership Transfer
	*@param logger - DefectLogger instance for logging
	*@param oldOwner - User instance for the previous owner of the file
	*@param file - BaseFile instance for the file that was transferred to a new user
	*/
	public void verifyNewsItemAndNotificationForOwnershipTransfer(DefectLogger logger, User oldOwner, BaseFile file) {

		logger.strongStep("Click on the 'My Notifications' button in the left panel");
		log.info("INFO: Click on the 'My Notifications' button in the left panel");
		if(cfg.getUseNewUI())
		{
			ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.personalFilterBtn),3,"Click on Personal Filter");
			ui.clickLinkWaitWd(By.xpath(HomepageUIConstants.latestUpdatesMyNotificationsFilter),3,"Click on My Notification Menu");
		}
		else
			ui.clickLinkWithJavascript(HomepageUIConstants.HomepageMyNotifications);

		logger.strongStep("Verify that the news item '" + oldOwner.getDisplayName() + " transferred ownership of a file to you.' is visible");
		log.info("INFO: Verify that the news item '" + oldOwner.getDisplayName() + " transferred ownership of a file to you.' is visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(HomepageUI.getNewsItemForOwnershipTransfer(oldOwner, file)),
				"The news item '" + oldOwner.getDisplayName() + " transferred ownership of a file to you.' is visible");

		logger.strongStep("Click on the Notifications icon at the top of the page");
		log.info("INFO: Click on the Notifications icon at the top of the page");
		if(!cfg.getUseNewUI())
		{
			driver.getSingleElement(NotificationCenterUI.notificationIcon).click();
			logger.strongStep("Verify that the notification '" + oldOwner.getDisplayName() + " transferred ownership of a file to you.' is visible");
			log.info("INFO: Verify that the notification '" + oldOwner.getDisplayName() + " transferred ownership of a file to you.' is visible");
			Assert.assertTrue(ui.fluentWaitElementVisible(hUI.getNotificationForOwnershipTransfer(oldOwner)),
					"The notification '" + oldOwner.getDisplayName() + " transferred ownership of a file to you.' is visible");
		}

	}

	/**
	*This method verifies the presence of a file's link on My Files page
	*@param logger - DefectLogger instance for logging
	*@param file - BaseFile instance for the file that was transferred to a new user
	*@param available - Boolean value, look for a file if it is true, the file should not be visible if it is false
	*/
	public void verifyPresenceOfFileLinkOnMyFilesPage(DefectLogger logger, BaseFile file, boolean available) {

		logger.strongStep("Click on the 'My Files' button in the left panel");
		log.info("INFO: Click on the 'My Files' button in the left panel");
		ui.clickMyFilesView();

		if (available) {

			logger.strongStep("Click on the List view");
			log.info("INFO: Click on the List view");
			Files_Display_Menu.DETAILS.select(ui);

			logger.strongStep("Verify that the link for the file is visible on My Files page");
			log.info("INFO: Verify that the link for the file is visible on My Files page");
			Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(file)),
					"The link for the file is visible on My Files page");

		} else {

			driver.turnOffImplicitWaits();

			logger.strongStep("Verify that the file's thumbnail or link is not visible on My Files page");
			log.info("INFO: Verify that the file's thumbnail or link is not visible on My Files page");
			Assert.assertFalse(driver.isElementPresent(FilesUI.getFileIsUploaded(file)) ||
					driver.isElementPresent(FilesUI.selectFileInGridView(file)),
					"The file is not visible on My Files page after its ownership has been transferred");

			driver.turnOnImplicitWaits();

		}

	}

	/**
	*This method verifies the presence of either a file's link or thumbnail on a folder's page
	*@param logger - DefectLogger instance for logging
	*@param view - String value for the type of view (List or Grid) to be selected
	*@param folder - BaseFolder instance for the folder containing the file
	*@param file - BaseFile instance for the file that was transferred to a new user
	*/
	public void verifyPresenceOfFileOnAFolderPage(DefectLogger logger, String view, BaseFolder folder, BaseFile file) {

		logger.strongStep("Click on the Folders button in the left panel");
		log.info("INFO: Click on the Folders button in the left panel");
		ui.clickMyFoldersView();

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Click on the link for the folder: " + folder.getName());
		log.info("INFO: Click on the link for the folder: " + folder.getName());
		ui.clickLinkWithJavascript(FilesUI.selectMyFolder(folder));

		if (view.contains("Grid")) {

			logger.strongStep("Click on the Grid view");
			log.info("INFO: Click on the Grid view");
			Files_Display_Menu.TILE.select(ui);

			logger.strongStep("Verify that the thumbnail for the file is visible on the folder's page");
			log.info("INFO: Verify that the thumbnail for the file is visible on the folder's page");
			Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.selectFileInGridView(file)),
					"The thumbnail for the file is visible on the folder's page");

		} else if (view.contains("List")) {

			logger.strongStep("Verify that the link for the file is visible on the folder's page");
			log.info("INFO: Verify that the link for the file is visible on the folder's page");
			Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(file)),
					"The link for the file is visible on the folder's page");

		}

	}
	
	/**
	*This method Type User name in the 'New Owner' text area in File Transfer Ownership dialog.
	*@param newTestUser - User object
	*/
	public void typeNewOwnerName(User newTestUser)
	{
		log.info("INFO: Type '" + newTestUser.getDisplayName() + "' in the 'New Owner' text box");
		ui.typeTextWithDelay(FilesUIConstants.shareFileDialogInputBox, newTestUser.getDisplayName());
	
		//Store all suggestions from the type ahead in a list
		List <Element> typeAheadResults = driver.getVisibleElements(FilesUIConstants.shareFolderDialogPersonTypeahead);
		for (Element ele : typeAheadResults) {
			if (ele.getText().contains(newTestUser.getDisplayName())) {
				log.info("INFO: Click on the suggestion for '" + newTestUser.getDisplayName() + "' in the typeahead");
				ele.click();
			}
		}
	}
	
	/**
	*This method locks a file and then verifies the file options
	*@param logger - DefectLogger instance for logging
	*@param available - Boolean value to check if the FIDO Viewer has been used to lock the file
	*@param file - BaseFile instance for the file that has been locked by the currently logged in user
	*/
	public void lockAFileThenVerifyTheFileOptions(DefectLogger logger, boolean isFIDOViewer, BaseFile file) {

		logger.strongStep("Select the 'Lock File' option from the menu");
		log.info("INFO: Select the 'Lock File' option from the menu");
		ui.clickLinkWithJavascript(FilesUIConstants.moreActionMenuItem_3);

		logger.strongStep("Verify that the message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");
		log.info("INFO: Verify that the message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");
		Assert.assertTrue(ui.fluentWaitTextPresent(FilesUIConstants.fileLockedMessage),
				"The message '" + FilesUIConstants.fileLockedMessage + "' appears on the screen");

		if (isFIDOViewer) {
			logger.strongStep("Close the FIDO viewer");
			log.info("INFO: Close the FIDO viewer");
			ui.clickLinkWithJavascript(FilesUIConstants.FileOverlayClose);
		}

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Click on the More link for the file: " + file.getName());
		log.info("INFO: Click on the More link for the file: " + file.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(file));

		logger.strongStep("Verify that the file is locked using the 'Locked by you' icon and the 'Unlock File' link");
		log.info("INFO: Verify that the file is locked using the 'Locked by you' icon and the 'Unlock File' link");
		Assert.assertTrue(ui.isFileLocked(file),
				"The file has been locked properly since the 'Locked by you' icon and the 'Unlock File' link are visible");

		logger.strongStep("Verify that the 'Add or Remove Tags' link is visible for the file since the file has been locked by the current user");
		log.info("INFO: Verify that the 'Add or Remove Tags' link is visible for the file since the file has been locked by the current user");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.addOrRemoveTagsLink),
				"The 'Add or Remove Tags' link is visible for the file as the file has been locked by the current user");

		logger.strongStep("Verify that the 'Upload New Version...' link is visible for the file since the file has been locked by the current user");
		log.info("INFO: Verify that the 'Upload New Version...' link is visible for the file since the file has been locked by the current user");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.uploadNewVersionLink),
				"The 'Upload New Version...' link is visible for the file as the file has been locked by the current user");

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Verify that the 'Edit Properties...' option is visible in the 'More Actions' menu since the file has been locked by the current user");
		log.info("INFO: Verify that the 'Edit Properties...' option is visible in the 'More Actions' menu since the file has been locked by the current user");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.EditPropertiesOption),
				"The 'Edit Properties...' option is visible in the 'More Actions' menu as the file has been locked by the current user");

	}

	/**
	*This method verifies a file as the user who locked the file and is also the new owner of the file post the transfer of ownership
	*@param logger - DefectLogger instance for logging
	*@param user - User instance for the new owner of the file who also locked it
	*@param file - BaseFile instance for the file that was locked by the user and whose ownership has been transferred
	*/
	public void verifyAFileAsTheUserWhoLockedTheFileAndIsAlsoTheNewFileOwner(DefectLogger logger, User user, BaseFile file) {

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, file, true);

		logger.strongStep("Click on the More link for the file: " + file.getName());
		log.info("INFO: Click on the More link for the file: " + file.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(file));

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Verify that '" + user.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + user.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"The current user '" + user.getDisplayName() + "' has the owner right to delete the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"The current user '" + user.getDisplayName() + "' has the owner right to transfer the ownership of the file");

		logger.strongStep("Unlock the file: " + file.getName());
		log.info("INFO: Unlock the file: " + file.getName());
		ui.unlockFile(file);

		logger.strongStep("Verify that the message '" + FilesUIConstants.fileUnlockedMessage + "' appears on the screen");
		log.info("INFO: Verify that the message '" + FilesUIConstants.fileUnlockedMessage + "' appears on the screen");
		Assert.assertTrue(ui.fluentWaitTextPresent(FilesUIConstants.fileUnlockedMessage),
				"The message '" + FilesUIConstants.fileUnlockedMessage + "' appears on the screen");

		driver.turnOffImplicitWaits();

		logger.strongStep("Verify that the file has been unlocked properly and that the 'Locked by you' icon and the 'Unlock File' link are not visible");
		log.info("INFO: The file has not been unlocked properly and that the 'Locked by you' icon and the 'Unlock File' link are not visible");
		Assert.assertFalse(ui.isFileLocked(file),
				"The file has been unlocked properly since the 'Locked by you' icon and the 'Unlock File' link are not visible");

		driver.turnOnImplicitWaits();

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Verify that the file has been unlocked properly and the 'Lock File' option is visible in the 'More Actions' menu");
		log.info("INFO: Verify that the file has been unlocked properly and the 'Lock File' option is visible in the 'More Actions' menu");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.moreActionMenuItem_3),
				"The file has been unlocked properly since the 'Lock File' option is visible in the 'More Actions' menu");

	}

	/**
	*This method logs in the new file owner to the Files component and then logs out so that the user's name appears in the Transfer Ownership typeahead
	*@param logger - DefectLogger instance for logging
	*@param user - User instance for the new owner of the file
	*/
	public void loginAndLogoutTheNewFileOwner(DefectLogger logger, User user) {

		logger.strongStep("Load the Files component and login as: " + user.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + user.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(user,cfg.getUseNewUI());

		logger.strongStep("Wait for the 'My Files' button to be visible");
		log.info("INFO: Wait for the 'My Files' button to be visible");
		if(!cfg.getUseNewUI())
				ui.fluentWaitElementVisible(FilesUIConstants.openMyFilesView);

		logger.strongStep("Logout as the current user: " + user.getDisplayName());
		log.info("INFO: Logout as the current user: " + user.getDisplayName());
		ui.logout();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify File Transfer Ownership Dialog proper behavior.</li>
	*<li><B>Step:</B>Upload a public file(.jpg) using API</li>
	*<li><B>Step:</B>Login and logout user2 so that the user's name appears in the 'Transfer Ownership' typeahead</li>
	*<li><B>Step:</B>Load the Files component and login as user1</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>The link for the file is visible on My Files page</li>
	*<li><B>Step:</B>Click on the List Display view and navigate to Uploaded public file.</li>
	*<li><B>Step:</B>Click on the More link for the file</li>
	*<li><B>Step:</B>Click on the 'More Actions' button and select'Transfer Ownership' for the file</li>
	*<li><B>Verify:</B>Verify File Transfer Ownership dialog Appear.</li>
	*<li><B>Verify:</B>Verify hover help text for 'New Owner' field is "Person name or email"</li>
	*<li><B>Verify:</B>Verify hover help text for 'Tags' field is "Please enter the tags you want to add"</li>
	*<li><B>Verify:</B>Verify hover help text for 'Tags Help' Icon is "Help"</li>
	*<li><B>Verify:</B>Verify hover help text for 'X' Icon is "Close"</li>
	*<li><B>Step:</B>Click on the 'Tags Help' Icon</li>
	*<li><B>Verify:</B>Verify the Tags help content is correct</li>
	*<li><B>Verify:</B>Verify the hover help text for the 'X' icon is "Close"</li>
	*<li><B>Verify:</B>Verify the Tags help content is closed when clicking on the 'X' icon</li>
	*<li><B>Step:</B>Click on Transfer Button without entering any Name</li>
	*<li><B>Verify:</B>Verify 'New Owner is a required Field' Error message</li>
	*<li><B>Step:</B>Enter Current User name in New Owner Field</li>
	*<li><B>Verify:</B>Verify 'You already Own this File' Error message</li>
	*<li><B>Step:</B>Enter user2 in New Owner Field and select from drop down</li>
	*<li><B>Verify:</B>Verify there is a 'x' icon next to the user name</li>
	*<li><B>Verify:</B>Verify the hover help text for the 'x' icon is "Remove user2"</li>
	*<li><B>Step:</B>Click on the 'X' button of user2</li>
	*<li><B>Verify:</B>Verify user2 is removed</li>
	*<li><B>Step:</B>Click on Transfer Button.</li>
	*<li><B>Verify:</B>Verify 'New Owner is a required Field' Error message</li>
	*<li><B>Step:</B>Click Transfer Ownership from drop down</li>
	*<li><B>Step:</B>Enter New Owner as user2 and tags with length more then 100.</li>
	*<li><B>Verify:</B>Verify an error message with Shorten tag? link displays</li>
	*<li><B>Step:</B>Click on 'Shorten tag?' link.</li>
	*<li><B>Verify:</B>Verify tags is shorten to 100 chars</li>
	*<li><B>Step:</B>Click on 'Transfer' button.</li>
	*<li><B>Verify:</B>Verify file is transferred successfully and Logout</li>
	*<li><B>Step:</B>Logout from user1.</li>
	*<li><B>Step:</B>Load the Files component and login as user2.</li>
	*<li><B>Verify:</B>Verify the correct tag displays</li>
	*<li><B>Verify:</B>Verify the notification on Home page</li>
	*<li><B>Step:</B>Load the Files component</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the Custom Display view</li>
	*<li><B>Step:</B>Navigate to the uploaded file and Click on the down arrow icon and select Transfer Ownership</li>
	*<li><B>Verify:</B>Validate File Transfer Ownership dialog appear</li>
	*<li><B>Step:</B>Select on the List Display view</li>
	*<li><B>Step:</B>Navigate to the uploaded file and Click on the file name or thumbnail</li>
	*<li><B>Step:</B>Click on More Actions(ellipsis icon) and select Transfer Ownership</li>
	*<li><B>Verify:</B>Validate File Transfer Ownership dialog appear</li>
	*<li><B>Step:</B>Delete the file</li>
	*</ul>
	*/
	@Test(groups = { "level2" })
	public void transferOwnershipDialog() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();
		String tag = "1234567890abcdefghijklmnopqrstuvwxyz2234567890abcdefghijklmnopqrstuvwxyz3234567890abcdefghijklmnopqrstuvwxyz";
		
		User testUserNew = cfg.getUserAllocator().getUser();

		//Upload Public File
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testname + "_" + Helper.genDateBasedRand())
				.rename("Pub"+testname+ Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		Assert.assertNotNull(apiFileOwner, "APIFileHandler Reference is Null.");
		
		// Create the BaseFile instance of Public file
		logger.strongStep("Upload a public file via API.");
		log.info("INFO: Upload a public file via API.");
		FileEntry file=FileEvents.addFile(baseFileImage, testUser, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		logger.strongStep("Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		log.info("INFO: Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		loginAndLogoutTheNewFileOwner(logger, testUserNew);

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Click on the 'My Files' button in the left panel");
		log.info("INFO: Click on the 'My Files' button in the left panel");
		ui.clickMyFilesView();

		logger.strongStep("Click on the List Display view");
		log.info("INFO: Click on the List Display view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Verify that the link for the file is visible on My Files page");
		log.info("INFO: Verify that the link for the file is visible on My Files page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(baseFileImage)),
				"The link for the file is visible on My Files page");

		logger.strongStep("Click on the More link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the More link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(baseFileImage));

		logger.strongStep("Click on the 'More Actions' button for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the 'More Actions' button for the file: " + baseFileImage.getName());
		ui.clickLinkWait(FilesUIConstants.genericMore);
		
		logger.strongStep("Click on the 'Transfer Ownership...' link");
		log.info("INFO: Click on the 'Transfer Ownership...' link");
		ui.clickLinkWithJavascript(FilesUIConstants.ClickForActionsOptionTransferOwnership);
		
		logger.strongStep("Validate 'File Transfer Ownership' Dialog opens.");
		log.info("INFO: Validate 'File Transfer Ownership' Dialog opens.");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.transferOwnershipDialogBox), "'File Transfer Ownership' Dialog is Visible.");
		
		logger.strongStep("Validate Hover message for New Owner field is 'Person name or email...'.");
		log.info("INFO: Validate Hover message for New Owner field is 'Person name or email...'.");
		ui.getFirstVisibleElement(FilesUIConstants.shareFileDialogInputBox).hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.shareFileDialogInputBox).getAttribute("title") , "Person name or email...");
		
		logger.strongStep("Validate Hover message for Tags field is 'Please enter the tags you want to add.'");
		log.info("INFO: Validate Hover message for Tags field is 'Please enter the tags you want to add.'");
		ui.getFirstVisibleElement(FilesUIConstants.UploadFiles_Tag).hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.UploadFiles_Tag).getAttribute("title") , "Please enter the tags you want to add.");
		
		logger.strongStep("Validate Hover message for tag help icon is 'Help'.");
		log.info("INFO: Validate Hover message for tag help icon is 'Help'.");
		ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipTagHelpLink).hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipTagHelpLink).getAttribute("title") , "Help");
		
		logger.strongStep("Validate Hover message for Close icon of 'File Transfer Ownership' dialog is 'Close'.");
		log.info("INFO: Validate Hover message for Close icon of 'File Transfer Ownership' dialog is 'Close'.");
		ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogClose).hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogClose).getAttribute("title") , "Close");
		
		logger.strongStep("Click Tag Help '?' Button and Validate appeared Tag Help Text is .");
		log.info("INFO: Click Tag Help '?' Button and Validate appeared Tag Help Text.");
		ui.clickLinkWait(FilesUIConstants.fileTransferOwnershipTagHelpLink);
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileTransferOwnershipDialogTagHelpText));
		
		logger.strongStep("Hover Tag Help dialog 'close' button and Validate appeared Tag Help Message is - "
		+ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogTagHelpText+"/p").getText());
		log.info("INFO: Hover Tag Help dialog 'close' button and Validate appeared Tag Help Message is - "
		+ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogTagHelpText+"/p").getText());
		ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogClose).hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogClose).getAttribute("title") , "Close");
		ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipTagHelpLink).hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogTagHelpText+"/p").getText(),
				"Keywords to make content easier to find. Enter tags as single words, like payroll and "
				+ "human_resources. Separate multiple tags with commas or spaces.");
		
		logger.strongStep("Click on the Transfer button of 'File Transfer Ownership' dialog with empty 'New Owner' Field");
		log.info("INFO: Click on the Transfer button of 'File Transfer Ownership' dialog with empty 'New Owner' Field");
		ui.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);
		
		//verify Error message
		logger.strongStep("Validate Error Message 'New owner is a required field'");
		log.info("INFO: Validate Error Message 'New owner is a required field'");
		Assert.assertTrue(ui.getElementText(FilesUIConstants.fileTOAlert).contains("New owner is a required field"));
		
		logger.strongStep("Enter Current User - "+testUser.getDisplayName()+" in 'New Owner' field");
		log.info("INFO: Enter Current User - "+testUser.getDisplayName()+" in 'New Owner' field");
		typeNewOwnerName(testUser);
		
		//verify error message
		logger.strongStep("Validate Error Message 'You already own this file.'");
		log.info("INFO: Validate Error Message 'You already own this file.'");
		Assert.assertTrue(ui.getElementText(FilesUIConstants.fileTOAlert).contains("You already own this file."));
		
		//Type UserB as new New Owner
		logger.strongStep("Clear the 'New Owner' textarea and Enter New User name - "+testUserNew.getDisplayName()+" in 'New Owner' field");
		log.info("INFO: Clear the 'New Owner' textarea and Enter New User name - "+testUserNew.getDisplayName()+" in 'New Owner' field");
		ui.getFirstVisibleElement(FilesUIConstants.shareFileDialogInputBox).clear();
		typeNewOwnerName(testUserNew);
		
		//Verify there is a 'x' icon next to the userB name
		logger.strongStep("Validate Close 'X' icon next to "+testUserNew.getDisplayName());
		log.info("INFO: Validate Close 'X' icon next to "+testUserNew.getDisplayName());
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.fileTOUserClose.replaceAll("PLACEHOLDER", testUserNew.getDisplayName())));
		
		logger.strongStep("Validate Hover message for Close 'X' icon next to username is 'Remove "+testUserNew.getDisplayName()+"'.");
		log.info("INFO: Validate Hover message for Close 'X' icon next to username is 'Remove "+testUserNew.getDisplayName()+"'.");
		ui.getFirstVisibleElement(FilesUIConstants.fileTOUserClose.replaceAll("PLACEHOLDER", testUserNew.getDisplayName())+"/..").hover();
		Assert.assertEquals(ui.getFirstVisibleElement(FilesUIConstants.fileTOUserClose.replaceAll("PLACEHOLDER",
				testUserNew.getDisplayName())+"/..").getAttribute("title") , "Remove "+testUserNew.getDisplayName());
		
		logger.strongStep("Click on Close 'X' icon next to "+testUserNew.getDisplayName());
		log.info("INFO: Click on Close 'X' icon next to "+testUserNew.getDisplayName());
		ui.clickLinkWait(FilesUIConstants.fileTOUserClose.replaceAll("PLACEHOLDER", testUserNew.getDisplayName()));
		
		logger.strongStep("Click on the Transfer button in 'File Transfer Ownership' dialog with empty 'New Owner' field");
		log.info("INFO: Click on the Transfer button in 'File Transfer Ownership' dialog with empty 'New Owner' field");
		ui.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);
		
		//verify Error message
		logger.strongStep("Validate Error message 'New owner is a required field'.");
		log.info("INFO: Validate Error message 'New owner is a required field'.");
		Assert.assertTrue(ui.getElementText(FilesUIConstants.fileTOAlert).contains("New owner is a required field"));
		
		//Close the File transfer ownership dialog
		logger.strongStep("Close the 'File Transfer Ownership' dialog.");
		log.info("INFO: Close the 'File Transfer Ownership' dialog.");
		ui.clickLinkWait(FilesUIConstants.fileTransferOwnershipDialogClose);
		driver.turnOffImplicitWaits();
		logger.strongStep("Verify that the 'Transfer Ownership' dialog box is not visible on the page anymore");
		log.info("INFO: Verify that the 'Transfer Ownership' dialog box is not visible on the page anymore");
		Assert.assertFalse(ui.isElementVisible(FilesUIConstants.transferOwnershipDialogBox));
		driver.turnOffImplicitWaits();
		
		//Launch 'Transfer Ownership' Dialog add UserB as New Owner and close
		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);

		logger.strongStep("Click on the 'Transfer Ownership...' link");
		log.info("INFO: Click on the 'Transfer Ownership...' link");
		ui.clickLinkWithJavascript(FilesUIConstants.ClickForActionsOptionTransferOwnership);
		
		//Type UserB
		logger.strongStep("Enter New user name - "+testUserNew.getDisplayName()+" in 'New Owner' field.");
		typeNewOwnerName(testUserNew);
		logger.strongStep("Click Cancel button of 'File Transfer Ownership' dialog.");
		log.info("INFO: Click Cancel button of 'File Transfer Ownership' dialog.");
		ui.clickLinkWait(FilesUIConstants.FolderSharePersonCancel);
		
		driver.turnOffImplicitWaits();
		logger.strongStep("Verify that the 'Transfer Ownership' dialog box is not visible on the page anymore");
		log.info("INFO: Verify that the 'Transfer Ownership' dialog box is not visible on the page anymore");
		Assert.assertFalse(ui.isElementVisible(FilesUIConstants.transferOwnershipDialogBox));
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);
		
		logger.strongStep("Verify that '" + testUser.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + testUser.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"Error: The current user '" + testUser.getDisplayName() + "' has the owner right to file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"Error: The current user '" + testUser.getDisplayName() + "' has the owner right to file");

		logger.strongStep("Click on the 'Transfer Ownership...' link");
		log.info("INFO: Click on the 'Transfer Ownership...' link");
		ui.clickLinkWithJavascript(FilesUIConstants.ClickForActionsOptionTransferOwnership);
		
		//Type UserB
		logger.strongStep("Type new user name - "+testUserNew.getDisplayName()+" in 'New Owner' field.");
		log.info("INFO: Type new user name - "+testUserNew.getDisplayName()+"  in 'New Owner' field.");
		typeNewOwnerName(testUserNew);
		
		//Enter tag value having length more than 100.
		logger.strongStep("Enter value in Tag field with length more then 100 and click on 'Transfer' button");
		log.info("INFO: Enter value in Tag field with length more then 100 and click on 'Transfer' button");
		ui.typeText(FilesUIConstants.UploadFiles_Tag,tag);
		ui.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);
		
		//verify too long Tag error message
		logger.strongStep("Validate Error Message with 'Shorten tag?' option for Too long tag");
		log.info("INFO: Validate Error Message with 'Shorten tag?' option for Too long tag");
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.fileTOTagTooLongError));
		Assert.assertTrue(ui.getFirstVisibleElement(FilesUIConstants.fileTOTagTooLongError+"/a").getText().contains("Shorten tag?"));
		
		logger.strongStep("Click on link 'Shorten tag?' and Validate Error Message for Too long Tag get Disappear.");
		log.info("INFO: Click on link 'Shorten tag?' and Validate Error Message for Too long Tag get Disappear.");
		ui.clickLinkWait(FilesUIConstants.fileTOTagTooLongError+"/a");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.fileTOTagTooLongError));
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Click on Transfer Button.");
		log.info("INFO: Click on Transfer Button.");
		ui.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);
		ui.waitForPageLoaded(driver);
		
		//Verify file is transferred successfully
		logger.strongStep("Validate 'the Successfully File Ownership Transfer' message appear, Close the message.");
		log.info("INFO: Validate 'the Successfully File Ownership Transfer' message appear, Close the message..");
		ui.fluentWaitElementVisible(FilesUIConstants.fileTransferMessage.replaceAll("PLACEHOLDER1", baseFileImage.getName())
				.replaceAll("PLACEHOLDER2", testUserNew.getDisplayName()));
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.fileTransferMessage.replaceAll("PLACEHOLDER1", baseFileImage.getName())
				.replaceAll("PLACEHOLDER2", testUserNew.getDisplayName())));
		ui.clickLinkWait(FilesUIConstants.fileTransferMessage.replaceAll("PLACEHOLDER1", baseFileImage.getName()).
				replaceAll("PLACEHOLDER2", testUserNew.getDisplayName())+"/../../a");
		
		driver.turnOffImplicitWaits();
		logger.strongStep("Verify that the link for the file is not visible on My Files page");
		log.info("INFO: Verify that the link for the file is not visible on My Files page");
		Assert.assertFalse(ui.isElementPresent(FilesUI.getFileIsUploaded(baseFileImage)),
				"The link for the file is not visible on My Files page");
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Homepage component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Homepage component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.loginAndToggleUI(testUserNew,cfg.getUseNewUI());

		//Verify the notification and the news item related to the transfer of ownership for the file on the Homepage
		verifyNewsItemAndNotificationForOwnershipTransfer(logger, testUser, baseFileImage);

		//validate tag length.
		logger.strongStep("Validate the tag Length is 100.");
		log.info("INFO: Validate the tag Length is 100.");
		Assert.assertTrue(ui.isTextPresent(tag.substring(0, 100)+","));
		
		logger.strongStep("Navigate to the Files component");
		log.info("INFO: Navigate to the Files component");
		ui.loadComponent(Data.getData().ComponentFiles, true);

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		//Select Custom List View
		logger.strongStep("Click on the Custom Display view");
		log.info("INFO: Click on the Custom Display view");
		Files_Display_Menu.SUMMARY.select(ui);
		
		logger.strongStep("Click on the Actions button for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the Actions button for the file: " + baseFileImage.getName());
		ui.clickLinkWait(ui.fileSpecificActionMenu(baseFileImage));

		logger.strongStep("Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"The current user '" + testUserNew.getDisplayName() + "' has the right to delete the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"The current user '" + testUserNew.getDisplayName() + "' has the right to transfer the ownership of the file");
		logger.strongStep("Click on the 'Transfer Ownership...' link");
		log.info("INFO: Click on the 'Transfer Ownership...' link");
		ui.clickLinkWithJavascript(FilesUIConstants.ClickForActionsOptionTransferOwnership);

		logger.strongStep("Verify that the 'Transfer Ownership' dialog box appears on the page");
		log.info("INFO: Verify that the 'Transfer Ownership' dialog box appears on the page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.transferOwnershipDialogBox),
				"The 'Transfer Ownership' dialog box appears on the page");

		logger.strongStep("Click on Cancel button.");
		log.info("INFO: Click on Cancel button.");
		ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogClose).click();
		
		logger.strongStep("Click on the List Display view");
		log.info("INFO: Click on the List Display view");
		Files_Display_Menu.DETAILS.select(ui);
		
		// select uploaded file
		log.info("INFO: Open the file in FiDO");
		logger.strongStep("Open the file in FiDO");
		ui.fluentWaitPresent(FilesUIConstants.myFilename.replaceAll("PLACEHOLDER", baseFileImage.getName()));
		ui.clickLinkWithJavascript(FilesUIConstants.myFilename.replaceAll("PLACEHOLDER", baseFileImage.getName()));

		logger.strongStep("Click on the Ellipsis menu in the FIDO Viewer");
		log.info("INFO: Click on the Ellipsis menu in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);

		logger.strongStep("Click on the 'Transfer Ownership...' link");
		log.info("INFO: Click on the 'Transfer Ownership...' link");
		ui.clickLinkWithJavascript(FilesUIConstants.ClickForActionsOptionTransferOwnership);

		logger.strongStep("Verify that the 'Transfer Ownership' dialog box appears on the page");
		log.info("INFO: Verify that the 'Transfer Ownership' dialog box appears on the page");
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.transferOwnershipDialogBox),
				"The 'Transfer Ownership' dialog box appears on the page");

		logger.strongStep("Click on Cancel button.");
		log.info("INFO: Click on Cancel button.");
		ui.getFirstVisibleElement(FilesUIConstants.fileTransferOwnershipDialogClose).click();
				 
		 logger.strongStep("Delete the file"); 
		 log.info("INFO: Delete the file");
		 apiFileOwner.deleteFile(file);
		 
		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Verify Files creators have Owner right to Shared folders and new owner has No Access to the Shared Folder.</li>
	*<li><B>Step:</B>Upload a private file(.jpg) using API with UserA</li>
	*<li><B>Step:</B>Upload a Folder using API with UserA and shared with UserF</li>
	*<li><B>Step:</B>Login and logout userB so that the user's name appears in the 'Transfer Ownership' typeahead</li>
	*<li><B>Step:</B>Load the Files component and login as userA</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the List view</li>
	*<li><B>Verify:</B>Verify link for the file is visible on My Files page</li>
	*<li><B>Verify:</B>Verify Access Level of the File is 'private'.</li>
	*<li><B>Step:</B>Click on the Folder in Left panel.</li>
	*<li><B>Verify:</B>Verify link for the Folder created in second step is visible on Folder page</li>
	*<li><B>Step:</B>Open the Folder and click on Drop down in the end of Folder name and select Add Files... link.</li>
	*<li><B>Verify:</B>Verify File is successfully added to the Folder.</li>
	*<li><B>Verify:</B>Verify access level of the file is changed from 'Private' to 'Shared' in My Files.</li>
	*<li><B>Step:</B>Open the file in thumbnail view, Click on ellipsis menu and select Transfer Ownership.</li>
	*<li><B>Verify:</B>Verify File Transfer Ownership dialog Appear.</li>
	*<li><B>Step:</B>Enter new owner as UserB, add some tag.</li>
	*<li><B>Step:</B>Click on 'Transfer' button.</li>
	*<li><B>Verify:</B>Verify File Transfer Ownership Dialog is closed.</li>
	*<li><B>Verify:</B>Verify message for file is transferred successfully.</li>
	*<li><B>Verify:</B>Verify file is removed form 'MyFiles' Folder.</li>
	*<li><B>Step:</B>Logout from userA.</li>
	*<li><B>Step:</B>Load the Home page component and login as userB.</li>
	*<li><B>Verify:</B>Verify the correct tag displays</li>
	*<li><B>Verify:</B>Verify the notification on Home page</li>
	*<li><B>Step:</B>Load the Files component</li>
	*<li><B>Step:</B>Click on the 'My Files' button in the left panel then click on the Custom Display view</li>
	*<li><B>Verify:</B>Verify File appears on 'My Files' page and userB has Owner right.</li>
	*<li><B>Verify:</B>Verify userB don't have access to Folder created in second step.</li>
	*<li><B>Verify:</B>Verify file entry is updated by userA with correct time stamp</li>
	*<li><B>Verify:</B>Verify file access level is shared.</li>
	*<li><B>Verify:</B>Verify userB can open the file in FIDO view.</li>
	*<li><B>Step:</B>Logout from userB</li>
	*<li><B>Step:</B>Login via userF and load the Files component.</li>
	**<li><B>Verify:</B>Verify userF has access to the folder and file inside the folder created in first second step.</li>
	*<li><B>Verify:</B>Verify userF has reader access to the file.</li>
	*<li><B>Step:</B>Logout from userF</li>
	*<li><B>Step:</B>Delete the file</li>
	*</ul>
	*/
	@Test(groups = { "level2" })
	public void ownerRightSharedFolder() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testname = ui.startTest();
		String tag = "Tag_"+Helper.genDateBasedRand();
		String tag1 = testname + "_" + Helper.genDateBasedRand();
		
		User testUserNew = cfg.getUserAllocator().getUser();

		//Upload a Private File
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(tag1)
				.rename("Pvt"+testname+ Helper.genDateBasedRand())
				.shareLevel(ShareLevel.NO_ONE)
				.build();

		// Create the BaseFile instance of Private file
		logger.strongStep("Upload a private file - "+baseFileImage.getName()+" via API by User1 as "+testUser.getDisplayName());
		log.info("INFO: Upload a private file - "+baseFileImage.getName()+" via API  by User1 as "+testUser.getDisplayName());
		FileEntry file=FileEvents.addFile(baseFileImage, testUser, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		//Create a Shared Folder
		folderFollower = new APIProfilesHandler(serverURL, testUser2.getEmail(),
				testUser2.getPassword());

		logger.strongStep(testUser.getDisplayName() + " will now create a new shared standalone folder");
		log.info("INFO: " + testUser.getDisplayName() + " will now create a new shared standalone folder");
		BaseFile baseFolder = new BaseFile.Builder("Folder"+ testname + Helper.genStrongRand())
											.sharedWith(folderFollower.getUUID())
											.shareLevel(ShareLevel.NO_ONE)
											.build();
		//Create the BaseFolder instance of the same private folder
 	    BaseFolder folder = new BaseFolder.Builder(baseFolder.getName())
                 .build();
		FileEntry folderentry=folderOwner.createFolder(baseFolder, Role.OWNER);
		
		log.info("INFO: Successfully created folder shared with person - " + testUser2.getDisplayName());
		logger.strongStep("Successfully created folder shared with person - " + testUser2.getDisplayName());

		logger.strongStep("Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		log.info("INFO: Login and logout '" + testUserNew.getDisplayName() + "' so that the user's name appears in the 'Transfer Ownership' typeahead");
		loginAndLogoutTheNewFileOwner(logger, testUserNew);

		logger.strongStep("Load the Files component and login as: " + testUser.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());

		logger.strongStep("Click on the 'My Files' button in the left panel");
		log.info("INFO: Click on the 'My Files' button in the left panel");
		ui.clickMyFilesView();

		logger.strongStep("Click on the List Display view");
		log.info("INFO: Click on the List Display view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Verify that the link for the file - "+baseFileImage.getName()+" is visible on My Files page");
		log.info("INFO: Verify that the link for the file - "+baseFileImage.getName()+" is visible on My Files page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(baseFileImage)),
				"Link for the file is visible on My Files page");
		
		logger.strongStep("Verify that the File - "+baseFileImage.getName()+" Access level is 'Private'.");
		log.info("INFO: Verify that the File - "+baseFileImage.getName()+" Access level is 'Private'.");
		Assert.assertTrue(fileAccessLevel(baseFileImage.getName() , "Private") , "File Access Level is Private.");
		
		logger.strongStep("Add File - "+baseFileImage.getName()+" in the Folder - "+baseFolder.getName()+".");
		log.info("INFO: Add File - "+baseFileImage.getName()+" in the Folder - "+baseFolder.getName()+".");
		ui.folderAddFiles(baseFolder.getName(), baseFileImage.getName());	
		ui.fluentWaitElementVisible(FilesUIConstants.FoldersLeftMenu);
		ui.getFirstVisibleElement(FilesUIConstants.FoldersLeftMenu).click();
		
		//Verify File is added to Folder.
		logger.strongStep("Verify File - "+baseFileImage.getName()+" is Added successfully to Folder - "+baseFolder.getName()+".");
		log.info("INFO: Verify File - "+baseFileImage.getName()+" is Added successfully to Folder - "+baseFolder.getName()+".");
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.fileAddedtoFolderMessage),"File is Added to the Folder.");
		
		log.info("INFO: Select the Folder - "+baseFolder.getName());
		ui.getFirstVisibleElement(FilesUIConstants.FolderLeftList.replace("PLACEHOLDER",baseFolder.getName())).doubleClick();
		
		logger.strongStep("Verify that the link for the file is visible on My Files page");
		log.info("INFO: Verify that the link for the file is visible on My Files page");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUI.getFileIsUploaded(baseFileImage)),
				"Link for the file is visible on My Files page");
		
		//Verify Access of File changed from private to shared.
		logger.strongStep("Verify Access Level of File - "+baseFileImage.getName()+" changed from 'Private' to 'Shared'.");
		log.info("INFO: Verify Access Level of File - "+baseFileImage.getName()+" changed from 'Private' to 'Shared'.");
		Assert.assertTrue(fileAccessLevel(baseFileImage.getName() , "Shared") , "File Access level has changed from Private to Shared.");
		
		log.info("INFO: Select List Display view.");
		ui.clickLinkWait(FilesUIConstants.DisplayList);
		
		// select uploaded file
		log.info("INFO: Open the file in FiDO");
		logger.strongStep("Open the file in FiDO");
		ui.fluentWaitPresent(FilesUIConstants.myFilename.replace("PLACEHOLDER", baseFileImage.getName()));
		ui.clickLinkWithJavascript(FilesUIConstants.myFilename.replace("PLACEHOLDER", baseFileImage.getName()));
		
		logger.strongStep("Click on the Ellipsis menu in the FIDO Viewer");
		log.info("INFO: Click on the Ellipsis menu in the FIDO Viewer");
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);

		//Transfer the ownership of the file to another user using the 'Transfer Ownership' dialog box
		log.info("INFO: Transfer Ownership of File - "+baseFileImage.getName()+" to user - "+testUserNew.getDisplayName());
		logger.strongStep("Transfer Ownership of File - "+baseFileImage.getName()+" to user - "+testUserNew.getDisplayName());
		transferOwnershipUsingDialogBox(logger, testUserNew, tag, baseFileImage);

		logger.strongStep("Click on the List view");
		log.info("INFO: Click on the List view");
		Files_Display_Menu.DETAILS.select(ui);

		logger.strongStep("Grab the file updation timestamp");
		log.info("INFO: Grab the file updation timestamp");
		String fileUpdationTimestamp = driver.getSingleElement(FilesUIConstants.fileUpdationTimestamp.replaceAll("PLACEHOLDER", baseFileImage.getName())).getText();

		logger.strongStep("Click on the link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Navigate to the Sharing tab in the FIDO Viewer");
		log.info("INFO: Navigate to the Sharing tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.sharingTabInFiDO);

		logger.strongStep("Verify that the new owner of the file is: " + testUserNew.getDisplayName());
		log.info("INFO: Verify that the new owner of the file is: " + testUserNew.getDisplayName());
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.fileOwnerUserLink).getText().equals(testUserNew.getDisplayName()),
				"New owner of the file is : " + testUserNew.getDisplayName());

		//Verify the presence of 'Editors - 0' to make sure that there are no editors for the file 
		logger.strongStep("Verify that the current user '" + testUser.getDisplayName() + "' does not have editor rights to the file");
		log.info("INFO: Verify that the current user '" + testUser.getDisplayName() + "' does not have editor rights to the file");
		Assert.assertTrue(ui.fluentWaitTextPresent("Editors - 0"),
				"File has Zero editors");

		//Verify that the link for the file is not present on 'My Files' page when List view is selected
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, false);

		//Verify that the link for the file is present on the folder's page when List view is selected
		verifyPresenceOfFileOnAFolderPage(logger, "List", folder, baseFileImage);

		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Homepage component and login as: " + testUserNew.getDisplayName());
		log.info("INFO: Load the Homepage component and login as: " + testUserNew.getDisplayName());
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.login(testUserNew);

		//Verify the notification and the news item related to the transfer of ownership for the file on the Homepage
		verifyNewsItemAndNotificationForOwnershipTransfer(logger, testUser, baseFileImage);

		logger.strongStep("Navigate to the Files component");
		log.info("INFO: Navigate to the Files component");
		ui.loadComponent(Data.getData().ComponentFiles, true);

		//Verify that the link for the file is present on 'My Files' page when List view is selected 
		verifyPresenceOfFileLinkOnMyFilesPage(logger, baseFileImage, true);

		logger.strongStep("Verify that the timestamp for updation of the file is correct");
		log.info("INFO: Verify that the timestamp for updation of the file is correct");
		Assert.assertEquals(driver.getSingleElement("css=tr[dndelementtitle='" + baseFileImage.getName() + "'] li.lotusFirst").getText(), fileUpdationTimestamp,
				"Timestamp for updation of the file is correct");

		logger.strongStep("Click on the Details view");
		log.info("INFO: Click on the Details view");
		ui.clickLinkWithJavascript(FilesUIConstants.selectViewDetail);

		logger.strongStep("Click on the Actions button for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the Actions button for the file: " + baseFileImage.getName());
		ui.clickLinkWait(ui.fileSpecificActionMenu(baseFileImage));

		logger.strongStep("Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		log.info("INFO: Verify that '" + testUserNew.getDisplayName() + "' has owner rights to the file (Transfer Ownership and Deletion)");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"Current user '" + testUserNew.getDisplayName() + "' have the right to delete the file");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"Current user '" + testUserNew.getDisplayName() + "' have the right to transfer the ownership of the file");

		logger.strongStep("Verify that the status of 'Sharing' for the file is 'Shared'");
		log.info("INFO: Verify that the status of 'Sharing' for the file is 'Shared'");
		Assert.assertEquals(driver.getSingleElement(ui.fileSpecificShareStatus(baseFileImage)).getAttribute("title"), "Shared",
				"Status of the file is changed form 'Sharing' to 'Shared'");

		logger.strongStep("Click on the link for the file");
		log.info("INFO: Click on the link for the file");
		ui.clickLinkWithJavascript(FilesUI.getFileIsUploaded(baseFileImage));

		logger.strongStep("Verify that the file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible");
		log.info("INFO: Verify that the file opens in FIDO viewer and the name of file, the image preview and the About tab of the FIDO Viewer are visible");
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.EditFilenameLink).getText().equals(baseFileImage.getName()),
				"Name of the file is visible in the FIDO Viewer");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage),
				"Image preview for the file is visible in the FIDO Viewer");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_panelAboutThisFile),
				"About tab of the FIDO Viewer is visible");

		logger.strongStep("Click on the About tab in the FIDO Viewer");
		log.info("INFO: Click on the About tab in the FIDO Viewer");
		ui.clickLinkWithJavascript(FilesUIConstants.fileviewer_panelAboutThisFile);

		logger.strongStep("Verify that all tags added to the file are visible in the About tab of the FIDO Viewer");
		log.info("INFO: Verify that all tags added to the file are visible in the About tab of the FIDO Viewer");
		ui.fluentWaitElementVisible(FileViewerUI.TagsContainer);
		Assert.assertTrue(driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag.toLowerCase()) && 
				driver.getSingleElement(FileViewerUI.TagsContainer).getText().contains(tag1.toLowerCase()),
				"Tags added to the file are visible in the About tab of the FIDO Viewer");

		logger.strongStep("Click on the Folders button in the left panel");
		log.info("INFO: Click on the Folders button in the left panel");
		ui.clickMyFoldersView();

		driver.changeImplicitWaits(3);

		logger.strongStep("Check if the text 'You have not created any folders.' is present");
		log.info("INFO: Check if the text 'You have not created any folders.' is present");
		if (!driver.isTextPresent(Data.getData().NoFoldersFound)) {

			logger.strongStep("Click on the List view");
			log.info("INFO: Click on the List view");
			Files_Display_Menu.DETAILS.select(ui);

			logger.strongStep("Verify that the folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");
			log.info("INFO: Verify that the folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");
			Assert.assertFalse(driver.isElementPresent(FilesUI.selectMyFolder(folder)),
					"The folder '" + folder.getName() + "' is not visible to '" + testUserNew.getDisplayName() + "' on My Folders page");

		}

		driver.turnOnImplicitWaits();
		
		logger.strongStep("Logout as the current user: " + testUser.getDisplayName());
		log.info("INFO: Logout as the current user: " + testUser.getDisplayName());
		ui.logout();

		logger.strongStep("Load the Files component and login as: " + testUser2.getDisplayName());
		log.info("INFO: Load the Files component and login as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser2);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Click the Folder:Shared with Me Tab in the Left Navigation Panel");
		log.info("INFO: Click the Folder:Shared with Me Tab in the Left Navigation Panel");
		ui.fluentWaitElementVisible(FilesUIConstants.FoldersLeftMenu);
		ui.clickLinkWithJavascript(FilesUIConstants.FoldersLeftMenu);
		ui.fluentWaitElementVisible(FilesUIConstants.fileSharedwithMe);
		ui.clickLinkWithJavascript(FilesUIConstants.fileSharedwithMe);
		
		logger.strongStep("Click on the Details view");
		log.info("INFO: Click on the Details view");
		ui.clickLinkWithJavascript(FilesUIConstants.selectViewList);

		//Check folder Exist
		logger.strongStep("Validate Folder - "+folder.getName()+" exist inside 'Shared with Me'");
		log.info("INFO: Validate Folder - "+folder.getName()+" exist inside 'Shared with Me'");
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.myFilename.replaceAll("PLACEHOLDER", folder.getName())), "Folder is present in 'Shared with Me.'");
		ui.clickLinkWait(FilesUIConstants.myFilename.replaceAll("PLACEHOLDER", folder.getName()));
		
		//Check file Exist
		logger.strongStep("Validate File - "+baseFileImage.getName()+" exist inside 'Shared with Me':"+folder.getName());
		log.info("INFO: Validate File - "+baseFileImage.getName()+" exist inside 'Shared with Me':"+folder.getName());
		ui.fluentWaitElementVisible(FilesUIConstants.myFilename.replaceAll("PLACEHOLDER", baseFileImage.getName()));
		Assert.assertTrue(ui.isElementPresent(FilesUIConstants.myFilename.replaceAll("PLACEHOLDER", baseFileImage.getName())), "File is present in 'Shared with Me:"+folder.getName()+".'");
		
		//UserF has Reader access
		logger.strongStep("Click on the More link for the file: " + baseFileImage.getName());
		log.info("INFO: Click on the More link for the file: " + baseFileImage.getName());
		ui.clickLinkWithJavascript(ui.fileSpecificMore(baseFileImage));

		logger.strongStep("Click on the 'More Actions' button for the file");
		log.info("INFO: Click on the 'More Actions' button for the file");
		ui.clickLinkWait(FilesUIConstants.genericMore);
		
		driver.turnOffImplicitWaits();
		logger.strongStep("Verify that '" + testUser2.getDisplayName() + "' has reader rights to the file");
		log.info("INFO: Verify that '" + testUser2.getDisplayName() + "' has reader rights to the file");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.ClickForActionsOptionMoveToTrash),
				"The current user '" + testUser2.getDisplayName() + "' have reader rights to the file.");
		Assert.assertFalse(ui.isElementPresent(FilesUIConstants.ClickForActionsOptionTransferOwnership),
				"The current user '" + testUser2.getDisplayName() + "' have reader rights to the file.");

		driver.turnOnImplicitWaits();
		logger.strongStep("Delete the file as well as the folder");
		log.info("INFO: Delete the file as well as the folder");
		apiFileOwner.deleteFile(file); 
		apiFileOwner.deleteFolder(folderentry);
		 
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Verify the Personal File shared with community don't have Transfer Ownership option in Communty:Files widget. </li>
	*<li><B>Step:</B>Create a new community using API</li>
	*<li><B>Step:</B>Create 2 Files using API</li>
	*<li><B>Step:</B>Load Files component and login</li>
	*<li><B>Step:</B>Open both Files in FIDO view and share the file with community created in first step</li>
	*<li><B>Step:</B>Navigate to Community and open the Community created in first step.</li>
	*<li><B>Step:</B>Goto to Files Widget of the Community.</li>
	*<li><B>Step:</B>Select List Display View and select More link</li> 
	*<li><B>Verify:</B>Verify 'Transfer Ownership...' option not present for both Files.</li>
	*<li><B>Step:</B>Select Custom Display View and Click Action Drop down</li> 
	*<li><B>Verify:</B>Verify 'Transfer Ownership...' option not present for both Files.</li>
	*<li><B>Step:</B>Select GRID Display View.</li> 
	*<li><B>Verify:</B>Verify 'Transfer Ownership...' option not present for both Files.</li>
	*<li><B>Step:</B>Delete the community via API</li>
	*</ul>
	*/
	@Test(groups = { "level2","cnx8ui-level2" })
	public void PersonnalFileSharedwithCommunity() {		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		List<String> Filename = new ArrayList<String>();
		List<BaseFile> BaseFileList = new ArrayList<BaseFile>();
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
					.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
					.access(Access.PUBLIC)
					.description("Test description for testcase " + testName)
					.build();
		
		//Upload Public File
		BaseFile pubfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename("Pub"+testName+ Helper.genDateBasedRand())
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		//Upload Private File
		BaseFile pvtfile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags(testName + "_" + Helper.genDateBasedRand())
				.rename("Pvt"+testName + Helper.genDateBasedRand())
				.build();

		// create community
		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiCommunityOwner);

		// add the UUID to community
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommunityOwner, comAPI);

		// Create the BaseFile instance of Public file
		logger.strongStep("Upload public file - "+pubfile.getName()+" via API ");
		log.info("INFO: Upload public file - "+pubfile.getName()+" via API ");
		FileEntry pubFileEntry = FileEvents.addFile(pubfile, testUser, apiFileOwner);
		pubfile.setName(pubfile.getRename() + pubfile.getExtension());

		// Create the BaseFile instance of Private file
		logger.strongStep("Upload private file - "+pvtfile.getName()+" via API ");
		log.info("INFO: Upload private file - "+pvtfile.getName()+" via API ");
		FileEntry pvtFileEntry = FileEvents.addFile(pvtfile, testUser, apiFileOwner);
		pvtfile.setName(pvtfile.getRename() + pvtfile.getExtension());
		BaseFileList.add(pvtfile);
		BaseFileList.add(pubfile);
		
		// Load the component and login
		logger.strongStep("Load Files component and login as:" + testUser.getDisplayName());
		log.info("INFO: Load Files component and login as:" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		// Navigate to file list view
		logger.strongStep(" Navigate to file list view");
		log.info("INFO: Navigate to file list view");
		logger.weakStep("Go to My Files. Change the 'View List' format");
		ui.clickMyFilesView();
		ui.clickLinkWait(FilesUICloud.listView);		
		
		Filename.add(pubfile.getName());
		Filename.add(pvtfile.getName());

		for(String file : Filename)
		{
			// select uploaded file
			log.info("INFO: Open the file in FiDO");
			logger.strongStep("Open the file in FiDO");
			ui.fluentWaitPresent(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
			ui.clickLinkWithJavascript(FilesUIConstants.myFilename.replace("PLACEHOLDER", file));
			
			logger.strongStep("share the File - "+file+" with the Community - "+community.getName());
			log.info("INFO: Select Sharing tab.");
			ui.clickLinkWait(FilesUIConstants.sharingTabInFiDO);
			
			logger.strongStep("Select member type as 'Community' and role as 'editor' in Sharing tab.");
			log.info("INFO: Select member type as 'Community' and role as 'editor' in Sharing tab.");
			ui.clickLinkWait(FilesUIConstants.addPeopleOrCommunities);
			ui.selectFromDropdownWithValue(FilesUIConstants.memberTypeSelector, "community");
			ui.selectFromDropdownWithValue(FilesUIConstants.roleTypeSelector, "editor");
			
			logger.strongStep("Select community via Type ahead and select from drop down.");
			log.info("INFO: Select community via Type ahead and select from drop down.");
			ui.getFirstVisibleElement(FilesUIConstants.commSearchTextBox).click();
			ui.typeTextWithDelay(FilesUIConstants.commSearchTextBox, community.getName());
			driver.changeImplicitWaits(5);
			if (ui.isElementVisible(FilesUIConstants.searchButton))
				ui.clickLinkWait(FilesUIConstants.searchButton);
			driver.turnOnImplicitWaits();
			ui.clickLinkWithJavascript(FilesUIConstants.firstCommFromSearch);
			ui.clickLinkWait(FilesUIConstants.shareButtonForComm);
		}
		
		// Close the FIDO
		logger.strongStep("Verify Close tag is displayed and Close the FIDO");
		log.info("INFO: Verify Close tag is displayed");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.closeIcon), "ERROR:Close tag is not displayed");
		driver.getSingleElement(FilesUIConstants.closeIcon).click();
		
		// Select Communities->My Communities
		log.info("Select Communities->My Communities");
		logger.strongStep("Select Communities->My Communities");
		if(!cfg.getUseNewUI())
		{
			ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
			ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
			ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);
		}
		else
			AppNavCnx8.COMMUNITIES.select(ui);
			

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
		
		//verify 'Transfer Ownership' option not present for List Display
		for(BaseFile file : BaseFileList)
		{
			logger.strongStep("Click on the More link for the file: " + file.getName());
			log.info("INFO: Click on the More link for the file: " + file.getName());
			ui.clickLinkWithJavascript(ui.fileSpecificMore(file));
			driver.turnOffImplicitWaits();
			logger.strongStep("Verify 'More Actions:Transfer Ownership...' link is Not present.");
			log.info("INFO: Verify 'More Actions:Transfer Ownership...' link is Not present.");
			Assert.assertFalse(ui.isElementPresent(FilesUIConstants.genericMore),
					"'More Actions:Transfer Ownership...' link is Not present.");
			driver.turnOnImplicitWaits();
		}
		//verify 'Transfer Ownership' option not present for Custom Display
		logger.strongStep("Click on the Custom Display view");
		log.info("INFO: Click on the Custom Display view");
		ui.clickLinkWithJavascript(FilesUIConstants.selectViewDetail);
		for(BaseFile file : BaseFileList)
		{
			logger.strongStep("Click on the Actions button for the file: " + file.getName());
			log.info("INFO: Click on the Actions button for the file: " + file.getName());
			ui.clickLinkWithJavascript(ui.fileSpecificActionMenu(file));
			driver.turnOffImplicitWaits();
			logger.strongStep("Verify 'Transfer Ownership...' link is Not present.");
			log.info("INFO: Verify 'Transfer Ownership...' link is Not present.");
			Assert.assertFalse(ui.isElementPresent(FilesUIConstants.ClickForActionsOptionTransferOwnership),
					"File Transfer Ownership link is Not Present.");
			driver.turnOnImplicitWaits();
		}
		//verify 'Transfer Ownership' option not present for GRID Display
		logger.strongStep("Click on the Grid Display view");
		log.info("INFO: Click on the Grid Display view");
		ui.clickLinkWait(FilesUIConstants.selectViewGrid);
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isTextPresent("Transfer Ownership..."),"File Transfer Ownership link is Not Present.");
		driver.turnOnImplicitWaits();
		
		logger.strongStep("Delete Community");
		log.info("INFO: Delete Community");
		apiCommunityOwner.deleteCommunity(comAPI);
		apiFileOwner.deleteFile(pubFileEntry); 
		apiFileOwner.deleteFile(pvtFileEntry);
		ui.endTest();
		
	}

	/**
	*This method will tell in true/false whether the file with some specific access level exist or not
	*@param fileName - Name of any file
	*@param access - Access Level(eg-Public, Private, Shared etc.) of file.
	*@return boolean - True or False
	*/
	public boolean fileAccessLevel(String fileName , String access)
	{
		return ui.isElementPresent("xpath=//tr[@dndelementtitle='"+fileName+"']/td/a[@title='"+access+"']");
	}
}
