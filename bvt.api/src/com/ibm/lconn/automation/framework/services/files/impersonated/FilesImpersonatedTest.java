package com.ibm.lconn.automation.framework.services.files.impersonated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.FilesTestBase;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

//
/**
 * JUnit Tests via Connections API for Blogs Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class FilesImpersonatedTest extends FilesTestBase {

	private static UserPerspective impersonateByotherUser;

	private static FilesService otherUserImService;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(FilesImpersonatedTest.class.getName());

	@BeforeMethod
	public void setUp() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("FilesImpersonatedTest.java");
		LOGGER.debug("===========================================");

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.FILES.toString());
		user = userEnv.getLoginUser();
		LOGGER.debug("user service for FilesImpersonatedTest: id=" + user.getUserId() + ",email=" + user.getEmail() + ",username=" + user.getUserName());
		service = user.getFilesService();
		impersonatedUser = userEnv.getImpersonatedUser();
		LOGGER.debug("impersonatedUser for FilesImpersonatedTest: id=" + impersonatedUser.getUserId() + ",email=" + impersonatedUser.getEmail() + ",username=" + impersonatedUser.getUserName());		
		impersonatedService = impersonatedUser.getFilesService();

		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.FILES.toString());

		impersonateByotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.FILES.toString(),
				StringConstants.CURRENT_USER);
		otherUserImService = impersonateByotherUser.getFilesService();

		// init();

		LOGGER.debug("Finished Initializing Files impersonate Test");
	}

	// @Test -- post comment to file under loginUser library - not for
	// impersonate
	public void testUpdateCreatedModifiedTime() throws IOException {
		super.testUpdateCreatedModifiedTime();
	}

	@Test(timeOut = 120000)
	public void createSharedFileAndUpdateCoolImageOnFile() {
		super.createSharedFileAndUpdateCoolImageOnFile();
	}

	@Test(timeOut = 120000)
	public void downloadFileAPI() throws URISyntaxException {
		super.downloadFileAPI();
	}

	// @Test(timeOut=120000)
	public void retrieveFileMetadata() {
		super.retrieveFileMetadata();
	}

	@Test(timeOut = 120000)
	public void testFileUpdate() {
		super.testFileUpdate();
	}

	@Test
	// Test for finding the public feed
	public void getPublicFeed() {
		super.getPublicFeed();
	}

	// @Test
	public void getPublicFeedInDepth() {
		super.getPublicFeedInDepth();
	}

	@Test
	public void getFoldersFeed() {
		super.getFoldersFeed();
	}

	@Test(timeOut = 120000)
	public void getFoldersFeedWithParameters() throws FileNotFoundException {
		super.getFoldersFeedWithParameters();
	}

	@Test(timeOut = 120000)
	// Test for finding your pinned files feed
	public void getYourPinnedFilesFeed() {
		// super.getYourPinnedFilesFeed();
	}

	@Test(timeOut = 120000)
	// Test for finding Your pinned folders feed based on the title
	public void getYourPinnedFoldersFeed() {
		// super.getYourPinnedFoldersFeed();
	}

	@Test
	// Test for finding public folders feed based on the title
	public void getPublicFoldersFeed() {
		// super.getPublicFoldersFeed();
	}

	@Test
	public void getUserLibraryFeed() throws FileNotFoundException, IOException {
		super.getUserLibraryFeed();
	}

	@Test
	// test for finding Library feed based on the title compared to the author
	// of the feed
	public void getMyLibraryFeed() {
		super.getMyLibraryFeed();
	}

	@Test
	// 73563: Files/Wikis parameter name since for API consistency
	public void getPublicDateFilteredFeed() {
		super.getPublicDateFilteredFeed();
	}

	@Test(timeOut = 120000)
	// test for finding Library feed based on the title compared to the author
	// of the feed
	public void getTagsFeed() throws Exception {
		super.getTagsFeed();
	}

	@Test
	// test for finding Library feed based on the title compared to the author
	// of the feed
	public void getRecentFolderFeed() {
		super.getRecentFolderFeed();
	}

	@Test(timeOut = 120000)
	// -- post comment to file under loginUser library - not for impersonate
	public void testFileComment() throws FileNotFoundException, IOException {
		super.testFileComment();
	}

	@Test(timeOut = 120000)
	// -- get file under loginUser library - not for impersonate
	public void testFileFolder() {
		/*
		 * Tests file folder Step 1: Create file folder Step 2: Retrieve file
		 * folder, by itself, otheruser, visitor(403) Step 3: Add files to
		 * folder Step 4: Remove a file from folder Step 5: Update folder name
		 * Step 6: Pinning file folder Step 7: UnPinning file folder Step 8:
		 * Delete file folder
		 */
		LOGGER.debug("BEGINNING TEST: File Folder");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String foldername = "FileFolder" + timeStamp;
		ExtensibleElement eEle = null;
		String folderUUID = "";

		LOGGER.debug("step 1: Create File Folder");
		eEle = createFileFolder(foldername, "file vfolder test", "folder tag",
				Permissions.PUBLIC);
		folderUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("step 2: Retrieve File Folder");
		eEle = service.retrieveFileFolder(folderUUID);
		verificationImpersonationGet((Entry) eEle, "Retrieve File Folder");
		assertEquals("folderUUID not match", folderUUID,
				eEle.getExtension(StringConstants.TD_UUID).getText());

		// from folder feed
		Feed publicFolders = (Feed) service.getFoldersFeed("ps=100");
		assertEquals("Get Folders", 200, service.getRespStatus());
		boolean found = false;
		for (Entry e : publicFolders.getEntries()) {
			if (e.getTitle().equals(foldername)) {
				found = true;
				Feed fileFolder = (Feed) service.getUrlFeed(e
						.getLinkResolvedHref("files").toString());
				assertEquals("Folder Name", foldername, fileFolder.getTitle());
				assertEquals("impersonate not match",
						impersonatedUser.getRealName(), e.getAuthor().getName());
				break;
			}
		}
		if (!found && publicFolders.getEntries().size() > 99) {
			LOGGER.debug("-: the Folder is outside "
					+ publicFolders.getEntries().size());
			found = true; // already verified before
		}
		assertEquals("Folder found", true, found);

		// retrieveSingleRoleOfCollection
		String roleEntryString = service.retrieveSingleCollectionRole(
				folderUUID, "reader");
		if (roleEntryString.contains("<title type=\"text\">reader</title>")) {
			LOGGER.debug("SUCCESS: Role was successfully found");
		} else {
			LOGGER.warn("ERROR: Role was not found");
			assertTrue("reader not found", false);
		}

		// getListOfCollectionRoles
		Feed roles = (Feed) service.getCollectionRoles(folderUUID);
		assertEquals("Roles's foldername", "Roles of " + foldername,
				roles.getTitle());
		assertEquals("Roles's size", 3, roles.getEntries().size()); 

		LOGGER.debug("step 3: Add Files to Folder");
		timeStamp = Utils.logDateFormatter.format(new Date());
		File addFile = new File(this.getClass()
				.getResource("/resources/dogs.txt").getFile());
		String fileUUID1 = "", fileUUID2 = "";

		InputStream is = getClass().getResourceAsStream("/resources/dogs.txt");
		// put public files in cretaed public folder
		FileEntry testFileEntry1 = new FileEntry(addFile, is, "addFileStream"
				+ timeStamp, "Stars Agent", "STARS", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, "hi shares", null, null,
				"this is a file body");
		FileEntry testFileEntry2 = new FileEntry(addFile,
				"addFile" + timeStamp, "Stars Agent", "STARS",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, "hi shares",
				null, null, "this is a file body");

		ExtensibleElement eEle1, eEle2;
		eEle1 = service.createFile(testFileEntry1);
		assertEquals("Create file", 201, service.getRespStatus()); // update
		// file
		// content
		// failed
		// inside
		// fileUUID1 = ((Entry)
		// eEle1).getExtension(StringConstants.TD_UUID).getText();

		eEle2 = service.createFile(testFileEntry2);
		fileUUID2 = ((Entry) eEle2).getExtension(StringConstants.TD_UUID)
				.getText();
		assertEquals("Create file", 201, service.getRespStatus());
		assertEquals("folderUUID not match", fileUUID2,
				eEle2.getExtension(StringConstants.TD_UUID).getText());
		assertEquals("impersonated user Name not match",
				impersonatedUser.getRealName(), ((Entry) eEle2).getAuthor()
						.getName());

		eEle2 = otherUserImService.createFile(testFileEntry2);
		assertEquals("Create file", 403, otherUserImService.getRespStatus());
		// assertEquals("folderUUID not match",fileUUID2,
		// eEle2.getExtension(StringConstants.TD_UUID).getText());
		// assertEquals("impersonated user Name not match",
		// imUser.getRealName(), ((Entry)eEle2).getAuthor().getName());

		ArrayList<String> filesList = new ArrayList<String>();
		// filesList.add(fileUUID1);
		filesList.add(fileUUID2);

		service.addFilesToFolder(folderUUID, filesList);
		assertEquals("Add File to Folder", 204, service.getRespStatus());

		LOGGER.debug("Verify - Get File from Folder");
		Feed feed = (Feed) service.getFilesInFolderFeed(folderUUID);
		assertEquals("Get File from Folder", 200, service.getRespStatus());
		assertEquals(" Files count", 1, feed.getEntries().size());

		LOGGER.debug("Multiple API to Retrieve List of Collections Containing File");
		/*
		 * not working with impersonate Feed lib = (Feed)
		 * service.getMyLibraryFeed(); String libUUID =
		 * lib.getExtension(StringConstants.TD_UUID).getText(); Feed
		 * collectionsList = (Feed)
		 * service.getCollectionsContainingFileInSpecifiedLibrary(libUUID,
		 * fileUUID1); assertEquals("Get collectionsList1", 200,
		 * service.getRespStatus()); assertEquals("Folder Name from lib",
		 * foldername, collectionsList.getEntries().get(0).getTitle());
		 * 
		 * // collectionsList = (Feed)
		 * service.getCollectionsContainingFileMyUserLibrary(fileUUID1);
		 * assertEquals("Get collectionsList2", 200, service.getRespStatus());
		 * assertEquals("Folder Name from my lib", foldername,
		 * collectionsList.getEntries().get(0).getTitle());
		 * 
		 * // collectionsList = (Feed)
		 * service.getCollectionsContainingFile(fileUUID1);
		 * assertEquals("Get collectionsList3", 200, service.getRespStatus());
		 * assertEquals("Folder Name", foldername,
		 * collectionsList.getEntries().get(0).getTitle());
		 * 
		 * // notwork with impersonate /*collectionsList = (Feed)
		 * service.getCollectionsContainingFileByUser(user.getUserId(),
		 * fileUUID1); assertEquals("Get collectionsList4", 200,
		 * service.getRespStatus()); assertEquals("Folder Name from userId",
		 * foldername, collectionsList.getEntries().get(0).getTitle());
		 */

		LOGGER.debug("step 4: Remove a File from Folder");
		assertTrue(service.removeFileFromFolder(folderUUID, fileUUID2));
		LOGGER.debug("Verify - Get File from Folder");
		feed = (Feed) service.getFilesInFolderFeed(folderUUID);
		assertEquals("Get File from Folder", 200, service.getRespStatus());
		assertEquals(" Files count", 0, feed.getEntries().size());

		LOGGER.debug("step 5: Update Folder Name");
		File updateFile = new File("Albert.txt");
		FileEntry updateFolder = new FileEntry(updateFile, "updateFolder"
				+ timeStamp, "Stars Leader", "Traitor", Permissions.PUBLIC,
				true, Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, null, null);
		eEle = service.updateFileFolder(folderUUID, updateFolder);
		assertEquals("Update File in Folder", 200, service.getRespStatus());
		assertEquals("impersonated update Folder userName not match",
				impersonatedUser.getRealName(), ((Entry) eEle).getAuthor()
						.getName());

		Entry folderEntry = (Entry) service.retrieveFileFolder(folderUUID);
		assertEquals("Retrieve File Folder", 200, service.getRespStatus());
		assertEquals("Folder name", "updateFolder" + timeStamp,
				folderEntry.getTitle());
		assertEquals("impersonated user Name not match",
				impersonatedUser.getRealName(), folderEntry.getAuthor()
						.getName());

		LOGGER.debug("Step 6: Pinning file folder");
		service.pinningFolder(folderUUID, folderEntry);
		assertEquals("Pinning File Folder", 204, service.getRespStatus());

		Feed myPinnedFolders = (Feed) service.getMyPinnedFoldersFeed();
		int folders = myPinnedFolders.getEntries().size();

		LOGGER.debug("Step 7: UnPinning file folder");
		assertTrue("UnPinning file Folder failed",
				service.unPinningFolder(folderUUID));

		myPinnedFolders = (Feed) service.getMyPinnedFoldersFeed();
		assertEquals("Pinned File Folder count", folders - 1, myPinnedFolders
				.getEntries().size());

		LOGGER.debug("Step 8: Delete file folder");
		assertTrue("Delete file folder failed",
				service.deleteFileFolder(folderUUID));
		service.retrieveFileFolder(folderUUID);
		assertEquals("File folder should not found ", 404,
				service.getRespStatus());
		LOGGER.debug("404: expected: File Folder not found");

		LOGGER.debug("END TEST: File Folder");
	}

	private void verificationImpersonationGet(Entry entry, String message) {
		assertEquals(message, 200, service.getRespStatus());
		assertEquals("impersonated author not match",
				impersonatedUser.getRealName(), entry.getAuthor().getName());
	}

	@Test(timeOut = 120000)
	public void testFileRecylceBin() {
		super.testFileRecylceBin();
	}

	// @Test(timeOut=120000)
	public void testFilePin() {
		super.testFilePin();
	}

	// @Test -- get file under loginUser library - not for impersonate
	public void getDocumentsList() throws FileNotFoundException, IOException {
		super.getDocumentsList();
	}

	// @Test -- userLib
	public void retrieveListUsersWithUserId() throws Exception {
		super.retrieveListUsersWithUserId();
	}

	/*
	 * RTC
	 * 
	 * Impersonation security test. This test requires a second organization
	 * with an admin and regular user. The admin MUST have org admin and user
	 * rights in SC console.
	 * 
	 * Test cases: b. Org A Admin uses Org B regular user for impersonation in
	 * Org A.
	 */
	@Test(timeOut = 120000)
	public void negativeCrossOrgTestFiles() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Beginning test RTC 136834: Negative test for cross org operations");
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";
			String impersonationHeaderValue_userEmail = "userEmail";
			String impersonationHeaderValue_userName = "userName";
			String impersonationHeaderValue_userOrg = "userOrg";

			int ORG_B_REGULAR_USER_INDEX = 15;

			InputStream infile = this.getClass().getResourceAsStream(
					"/resources/lamborghini_murcielago_lp640.jpg");

			// Org B regular user - Jill White
			UserPerspective orgBRegular=null;
			try {
				orgBRegular = new UserPerspective(
						ORG_B_REGULAR_USER_INDEX, Component.FILES.toString(),
						useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOGGER.debug(service.getRequestOption("X-LConn-RunAs"));

			// Standard impersonation use case - this should pass.
			FileEntry fileMetaData = new FileEntry(null, infile,
					"CROSS ORG Test 1 - should work."
							+ Utils.logDateFormatter.format(new Date())
							+ ".jpg", "This is one cool car!",
					"cool car lp640", Permissions.SHARED, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, "Hello world, this is my new share!",
					otherUser.getUserId());
			service.createFile(fileMetaData);
			assertEquals("createSharedFileAndUpdateCoolImageOnFile", 201,
					service.getRespStatus());

			LOGGER.debug("Before setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// Org A Admin uses Org B regular user for impersonation.
			service.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId() + ",excludeRole=admin");
			LOGGER.debug("After setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// Cross org impersonation! This returns 201, and the file is
			// created under the Org A Admin account!
			// Is this the right response?
			FileEntry fileMetaData2 = new FileEntry(null, infile,
					"CROSS ORG Test 2 - should fail."
							+ Utils.logDateFormatter.format(new Date())
							+ ".jpg", "This is one cool car!",
					"crossorgnegative", Permissions.SHARED, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, "Hello world, this is my new share!",
					otherUser.getUserId());
			service.createFile(fileMetaData2);
			assertEquals("HTTP 201 was not returned", 201,
					service.getRespStatus());

			LOGGER.debug("Ending test RTC 136834: Negative test for cross org operations");
		}
	}

	@Test
	public void testFileShare() {
		// super.testFileShare();
	}

	@Test(timeOut = 120000)
	public void updateFileMetaData() {
		super.updateFileMetaData();
	}

	@Test(timeOut = 120000)
	public void deleteUserVersionId() {
		super.deleteUserVersionId();
	}

	@Test(timeOut = 120000)
	public void retrieveVersionOfFile() {
		super.retrieveVersionOfFile();
	}

	@Test(timeOut = 120000)
	public void testFileVersion() {
		super.testFileVersion();
	}

	@Test(timeOut = 120000)
	public void purgeAllFilesFromTrash() {
		super.purgeAllFilesFromTrash();
	}

	@Test(timeOut = 120000)
	public void retrieveDocumentEntry() {
		super.retrieveDocumentEntry();
	}

	@Test(timeOut = 120000)
	public void deleteAllFilesAndFolders() {
		super.deleteAllFilesAndFolders();
	}

	@Test
	public void getLibraryInfo() throws FileNotFoundException, Exception {
		// super.getLibraryInfo();
	}

	@AfterMethod
	public void tearDown() {
		service.tearDown();
		otherUserImService.tearDown();
	}
}