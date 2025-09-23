package com.ibm.lconn.automation.framework.services.files;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.stax.FOMService;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;

abstract public class FilesTestBase {

	static Abdera abdera = new Abdera();

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(FilesTestBase.class.getName());

	protected static UserPerspective user, impersonatedUser, otherUser, visitor,
			extendedEmployee;

	protected static FilesService service, impersonatedService, visitorService,
			extendedEmpService;

	protected static boolean useSSL = true;
	
	protected static FilesGateKeeperServiceWrapper gateKeeperService = FilesGateKeeperServiceWrapper.getInstance();

	static boolean initflag = true;

	private final static String fileComment = "this is a file comment";
	

	static String timeStamp = Utils.logDateFormatter.format(new Date());

	static String foldername = "FileFolder_" + timeStamp;

	static String filename1 = "File1_" + timeStamp;

	static String filename2 = "File2_" + timeStamp;

	static String folderUUID = "", fileUUID1 = "", fileUUID2 = "";

	static ExtensibleElement eEle1, eEle2;

	protected static void init() {

		LOGGER.debug("BEGINNING TEST: init File test:" + initflag);
		if (!initflag) {
			// do nothing

		} else {
			initflag = false;
			/*
			 * init Files API test Step 1: Create the file folder Step 2: Add
			 * two files to folder
			 */

			LOGGER.debug("step 1: Create File Folder");
			FileEntry testFolder = new FileEntry(null, foldername,
					"file folder", "folder tag", Permissions.PRIVATE, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, null, null);
			Entry result = (Entry) impersonatedService.createFolder(testFolder);
			assertEquals("Folder create", 201,
					impersonatedService.getRespStatus());
			folderUUID = result.getExtension(StringConstants.TD_UUID).getText();
			// folderUUID = result.getIdElement().toString();

			LOGGER.debug("step 2: Add Files to Folder");
			// String fileUUID1 = "", fileUUID2 = "";

			InputStream fin = FilesTestBase.class
					.getResourceAsStream("/resources/dogs.txt");

			// put public files in cretaed public folder
			FileEntry testFileEntry1 = new FileEntry(null, fin, filename1,
					"Stars Agent", "STARS", Permissions.PUBLIC, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, "hi shares", null, null,
					"this is not a file body");
			FileEntry testFileEntry2 = new FileEntry(null, filename2,
					"Stars Agent", "STARS", Permissions.PUBLIC, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, "hi shares", null, null,
					"this is a file body");

			// ExtensibleElement eEle1, eEle2;
			eEle1 = impersonatedService.postFileToMyUserLibrary(testFileEntry1);
			assertEquals("File1 create", 201,
					impersonatedService.getRespStatus());
			fileUUID1 = ((Entry) eEle1).getExtension(StringConstants.TD_UUID)
					.getText();
			eEle2 = impersonatedService.postFileToMyUserLibrary(testFileEntry2);
			assertEquals("File2 create", 201,
					impersonatedService.getRespStatus());
			fileUUID2 = ((Entry) eEle2).getExtension(StringConstants.TD_UUID)
					.getText();

			ArrayList<String> filesList = new ArrayList<String>();
			filesList.add(fileUUID1);
			filesList.add(fileUUID2);

			impersonatedService.addFilesToFolder(folderUUID, filesList);
			assertEquals("Add File to Folder", 204,
					impersonatedService.getRespStatus());

			LOGGER.debug("END Init");
		}
	}

	// @Test
	public void retrieveFileMetadata() {
		LOGGER.debug("BEGINNING TEST: Retrieve File Metadata");

		ExtensibleElement metadataElement = service
				.getFileFromMyuserlibraryByUuid(fileUUID2);
		assertEquals("Get matadata", 200, service.getRespStatus());
		assertEquals("matadata - UUID not match ", fileUUID2,
				((Entry) metadataElement).getExtension(StringConstants.TD_UUID)
						.getText());

		metadataElement = service.getFileMetaDataFeed(fileUUID2);
		assertEquals("Get matadata", 200, service.getRespStatus());
		assertEquals("matadata - UUID not match ", fileUUID2,
				((Entry) metadataElement).getExtension(StringConstants.TD_UUID)
						.getText());

		LOGGER.debug("COMPLETED TEST: Retrieve File Metadata");
	}

	public void testFilePin() {

		LOGGER.debug("BEGINNING TEST: Pin/unPin File");

		ExtensibleElement result = service.pinningFile(fileUUID1, eEle1);
		assertEquals("Pin file", 204, service.getRespStatus());

		service.unPinningFile(fileUUID1);
		assertEquals("unPin file", 204, service.getRespStatus());

		LOGGER.debug("Completed TEST: Pin/unPin File");
	}

	public void getDocumentsList() throws FileNotFoundException, IOException {
		/*
		 * Test endpoint: /basic/api/library/{library-id}/feed
		 * /basic/api/userlibrary/{user-id}/feedStep 1... Get user library feed
		 * with userIdStep 2... Get user library feed with libraryId //and
		 * verify init files are inside user library feed
		 */

		LOGGER.debug("Step 1... Get user library with user id");
		String userId = user.getUserId();
		String url2 = service.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		Feed userLib = (Feed) service.getUrlFeed(url2);
		assertEquals("Get user library with userId", 200,
				service.getRespStatus());
		// String libid = userLib.getId().toString();

		LOGGER.debug("Step 2... Get user library with library id");
		String string = userLib.getSelfLinkResolvedHref().toString();
		ExtensibleElement feed = service.getMyFeed(string);
		assertEquals("Get user library with libId", 200,
				service.getRespStatus());
		// assertTrue(feed.toString().contains(filename1));
		// assertTrue(feed.toString().contains(filename2));

		LOGGER.debug("Ending Test...getDocumentsList");
	}

	// post comment to file under loginUser library - not for impersonate
	public void testUpdateCreatedModifiedTime() throws IOException {
		// create a file
		Timestamp created = new Timestamp(1000000000);
		Timestamp modified = new Timestamp(1010000000);
		FileEntry fileMetaData = new FileEntry(null, null,
				"Lamborghini Murcielago LP6406"
						+ Utils.logDateFormatter.format(new Date()) + ".jpg",
				"This is one cool car!", "cool car lp640", Permissions.SHARED,
				true, Notification.ON, Notification.ON, created, modified,
				true, true, SharePermission.VIEW,
				"Hello world, this is my new share!", otherUser.getUserId());
		ExtensibleElement result = (Entry) service.createFile(fileMetaData,
				true);

		String docId = ((Entry) result).getExtension(StringConstants.TD_UUID)
				.getText();
		String fileCreated = ((Entry) result).getExtension(
				StringConstants.TD_CREATED).getText();
		String fileModified = ((Entry) result).getExtension(
				StringConstants.TD_MODIFIED).getText();
		long response_createdTime = (new AtomDate(fileCreated)).getTime();
	    long response_modifiedTime = (new AtomDate(fileModified)).getTime();
		if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
		  assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
		  assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
		} else {
		  assertTrue(created.getTime() != response_createdTime);
		  assertTrue(modified.getTime()!= response_modifiedTime);
		}

		String versionUUID = ((Entry) result).getExtension(
				StringConstants.TD_VERSIONUUID).getText();

		// update a file and create a new version
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String updatefilename = "updateFile" + timeStamp;

		File updatedFile = new File("updateFile.txt");
		FileEntry updatedEntry = new FileEntry(updatedFile, updatefilename,
				"The Bond Car", "Sick Ride!", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, created, modified, true,
				true, SharePermission.EDIT, null, null);

		ExtensibleElement updateResult = service.updateFileMetaData(docId,
				updatedEntry);
		fileCreated = ((Entry) updateResult).getExtension(
				StringConstants.TD_CREATED).getText();
		fileModified = ((Entry) updateResult).getExtension(
				StringConstants.TD_MODIFIED).getText();
		response_createdTime = (new AtomDate(fileCreated)).getTime();
        response_modifiedTime = (new AtomDate(fileModified)).getTime();
		if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
          assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
          assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
          assertTrue(created.getTime() != response_createdTime);
          assertTrue(modified.getTime()!= response_modifiedTime);
        }

		ExtensibleElement entryOfVersion1 = service.retrieveVersionOfFile(
				docId, versionUUID);
		fileCreated = ((Entry) entryOfVersion1).getExtension(
				StringConstants.TD_CREATED).getText();
		fileModified = ((Entry) entryOfVersion1).getExtension(
				StringConstants.TD_MODIFIED).getText();
		response_createdTime = (new AtomDate(fileCreated)).getTime();
        response_modifiedTime = (new AtomDate(fileModified)).getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
          assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
          assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
          assertTrue(created.getTime() != response_createdTime);
          assertTrue(modified.getTime()!= response_modifiedTime);
        }

		// create a comment
		FileEntry commentEntry = new FileEntry(null, null,
				"comment description", "Tag1 Tag2", null, true, null, null,
				created, modified, false, false, null, null, null);
		String userId = user.getUserId();
		ExtensibleElement commentResult = (Entry) service.createFileComment(
				commentEntry, docId, userId);
		// post comment to file under loginUser library - not for impersonate

		String commentCreated = ((Entry) commentResult).getExtension(
				StringConstants.TD_CREATED).getText();
		String commentModified = ((Entry) commentResult).getExtension(
				StringConstants.TD_MODIFIED).getText();
		response_createdTime = (new AtomDate(commentCreated)).getTime();
        response_modifiedTime = (new AtomDate(commentModified)).getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
          assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
          assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
          assertTrue(created.getTime() != response_createdTime);
          assertTrue(modified.getTime()!= response_modifiedTime);
        }

		// create a share
		FileEntry fileShareEntry = new FileEntry(null, updatefilename,
				"Stars Agent", "STARS", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, created, modified, true,
				true, SharePermission.VIEW, "hi shares", otherUser.getUserId(),
				docId, "this is a file body");
		ExtensibleElement shareResult = service.createFileShare(fileShareEntry,
				docId, userId);

		String shareUUID = ((Entry) shareResult).getExtension(
				StringConstants.TD_UUID).getText();
		shareResult = service.retrieveFileShare(shareUUID);

		long createdTime = ((Entry) shareResult).getPublished().getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
          assertEquals("testUpdateCreatedModifiedTime", created.getTime(), createdTime);
        } else {
          assertTrue(created.getTime() != createdTime);
        }

		// create a folder
		String foldername = "FileFolder"
				+ Utils.logDateFormatter.format(new Date());
		File testFile = new File(foldername + ".txt");
		FileEntry testFolder = new FileEntry(testFile, foldername,
				"file vfolder test", "folder tag", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, created, modified, true,
				true, SharePermission.VIEW, null, null);
		Entry folderResult = (Entry) service.createFolder(testFolder);
		assertEquals("Folder create", 201, service.getRespStatus());

		String folderCreated = ((Entry) folderResult).getExtension(
				StringConstants.TD_CREATED).getText();
		String folderModified = ((Entry) folderResult).getExtension(
				StringConstants.TD_MODIFIED).getText();
		response_createdTime = (new AtomDate(folderCreated)).getTime();
        response_modifiedTime = (new AtomDate(folderModified)).getTime();
        if (gateKeeperService.getGateKeeperSetting("FILES_MERGE_ORIGINAL_DATE")) {
          assertEquals("testUpdateCreatedModifiedTime", created.getTime(), response_createdTime);
          assertEquals("testUpdateCreatedModifiedTime", modified.getTime(), response_modifiedTime);
        } else {
          assertTrue(created.getTime() != response_createdTime);
          assertTrue(modified.getTime()!= response_modifiedTime);
        }
	}
	
	@Test
	public void createSharedFileAndUpdateCoolImageOnFile() {
		InputStream infile = this.getClass().getResourceAsStream(
				"/resources/lamborghini_murcielago_lp640.jpg");

		FileEntry fileMetaData = new FileEntry(null, infile,
				"Lamborghini Murcielago LP6406"
						+ Utils.logDateFormatter.format(new Date()) + ".jpg",
				"This is one cool car!", "cool car lp640", Permissions.SHARED,
				true, Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, "Hello world, this is my new share!",
				otherUser.getUserId());
		service.createFile(fileMetaData);
		assertEquals("createSharedFileAndUpdateCoolImageOnFile", 201,
				service.getRespStatus());
	}

	//@Test
	public void createSharedFileAndUpdateCoolImageOnFileByVisitor() {
		if (StringConstants.VMODEL_ENABLED) {
			InputStream infile = this.getClass().getResourceAsStream(
					"/resources/lamborghini_murcielago_lp640.jpg");
			FileEntry fileMetaData = new FileEntry(null, infile,
					"Lamborghini Murcielago LP6406"
							+ Utils.logDateFormatter.format(new Date())
							+ ".jpg", "This is one cool car!",
					"cool car lp640", Permissions.SHARED, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, "Hello world, this is my new share!",
					otherUser.getUserId());

			visitorService.createFile(fileMetaData);
			assertEquals("createSharedFileAndUpdateCoolImageOnFileByVisitor",
					201, visitorService.getRespStatus());
		}
	}

	public ExtensibleElement createFileFolder(String name, String description,
			String tag, Permissions permission) {
		File testFile = new File(name + ".txt");
		FileEntry testFolder = new FileEntry(testFile, name, description, tag,
				permission, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null);
		Entry result = (Entry) service.createFolder(testFolder);
		assertEquals("Folder create", 201, service.getRespStatus());
		return result;
	}

	public ExtensibleElement createFileFolderByVisitor(String name,
			String description, String tag, Permissions permission) {
		File testFile = new File(name + ".txt");
		FileEntry testFolder = new FileEntry(testFile, name, description, tag,
				permission, true, Notification.ON, Notification.ON, null, null,
				true, true, SharePermission.VIEW, null, null, true);
		Entry result = (Entry) visitorService.createFolder(testFolder);
		assertEquals("Folder create", 201, visitorService.getRespStatus());
		return result;
	}

	public ExtensibleElement createPublicFolder(String name, String desciption,
			String tag) {
		return createFileFolder(name, desciption, tag, Permissions.PUBLIC);
	}

	public void testFileUpdate() {

		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "FileUpdate" + timeStamp;

		LOGGER.debug("BEGINNING TEST: Updated File");

		String fileUUID = "";
		ExtensibleElement eEle = createSimplePublicFile(filename);
		fileUUID = ((Entry) eEle).getExtension(StringConstants.TD_UUID)
				.getText();

		// check tag
		/*
		 * String tags = service.getTagsFeed(); tags =
		 * service.getTagsFeed("Hi!"); tags = service.getTagsFeed("ccccHi!");
		 */

		// update Tag
		ExtensibleElement fileElement = (Entry) service.updateFileTag(eEle,
				fileUUID, "newTestTag");
		if (fileElement.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(false);
		} else
			assertTrue(true);

		// update filename
		String updatedFileName = "UpdatedFileName" + timeStamp;
		fileElement = (Entry) service.updateFileWithNameChange(fileUUID,
				updatedFileName);

		if (((Entry) fileElement).getTitle().compareTo(updatedFileName) == 0) {
			LOGGER.debug("Test Passed: Updated File With Name Change");
			// assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Updated File With Name Change: "
					+ fileElement.toString());
			// assertTrue(false);
		}
		assertEquals(updatedFileName, ((Entry) fileElement).getTitle());

		LOGGER.debug("COMPLETED TEST: Updated File With Name Change");

		LOGGER.debug("COMPLETED TEST: Updated File");
	}

	public void testFileComment() throws FileNotFoundException, IOException {
		/*
		 * Tests file comment Step 1: Create file Step 2: Post file comment Step
		 * 3: Retrieve comment Step 4: update file comment Step 5: various way
		 * to get file comment back Step 6: Remove file comment
		 */
		// TODO add vreify for each steps
		LOGGER.debug("BEGINNING TEST: File Comment");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "FileComment" + timeStamp;

		FileEntry fileMetaData = new FileEntry(null, filename,
				"This is one cool car!", "cool car lp640 private",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my new private share!", null, null,
				fileComment);
		Entry result = null;

		LOGGER.debug("Step 1: Create file");
		ExtensibleElement eEle = createTestFile(filename, false);
		assertEquals("impersonate not match", impersonatedUser.getRealName(),
				((Entry) eEle).getAuthor().getName());

		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		String commentLink = ((Entry) eEle)
				.getLink(StringConstants.REL_REPLIES).getHref().toString();
		LOGGER.debug("content :" + ((Entry) eEle).getContent());

		LOGGER.debug("Step 2: Post file comment");
		// String userId = user.getUserId();
		String userId = impersonatedUser.getUserId();
		result = (Entry) service.createFileComment(fileMetaData, fileUUID,
				userId);
		assertEquals("createFileComment", 201, service.getRespStatus());
		assertEquals("impersonate not match", impersonatedUser.getRealName(),
				((Entry) eEle).getAuthor().getName());

		String commentUUID = result.getExtension(StringConstants.TD_UUID)
				.getText();
		assertEquals(" Created File Comment match", fileComment,
				result.getContent());

		LOGGER.debug("Step 3: Retrieve comment");
		ExtensibleElement fileCommentElement = (Entry) impersonatedService
				.retrieveFileComment(fileUUID, commentUUID);
		assertEquals("retrieveFileComment", 200,
				impersonatedService.getRespStatus());
		assertEquals(" Retrieved File Comment match", fileComment,
				((Entry) fileCommentElement).getContent());

		LOGGER.debug("Step 4: update file comment");
		String updatedComment = "updated file comment";
		result.setContent(updatedComment);

		ExtensibleElement result1 = (Entry) service.updateFileComment(result,
				fileUUID, commentUUID);
		assertEquals("updateFileComment", 200, service.getRespStatus());
		assertEquals("impersonate not match", impersonatedUser.getRealName(),
				((Entry) eEle).getAuthor().getName());
		assertEquals(" Updated File Comment match", updatedComment,
				((Entry) result1).getContent());

		LOGGER.debug("Step 5: various way to get file comment back");
		// two API to get comment feed
		ExtensibleElement fileElement = impersonatedService
				.getMyFilesCommentsFeed(fileUUID);
		assertEquals("getMyFileCommentFeed 1", 200,
				impersonatedService.getRespStatus());

		fileElement = impersonatedService
				.getFilesCommentsFeed(userId, fileUUID);
		assertEquals("getMyFileCommentFeed 2", 200,
				impersonatedService.getRespStatus());

		// get the created file from mylibrary, then get comment
		Feed myLibrary = (Feed) impersonatedService.getMyLibraryFeed();
		assertEquals("getMyLibraryFeed ", 200,
				impersonatedService.getRespStatus());
		boolean found = false;
		for (Entry e : myLibrary.getEntries()) {
			// find the right entry in the my folders feed using the known title
			if (e.getTitle().equals(filename)) {
				// once found get the feed of the file's comments
				Feed fileReplies = (Feed) service.getUrlFeed(e
						.getLinkResolvedHref("replies").toString());
				// check the feeds title and make sure it is what is expected
				if (fileReplies.getTitle().equals(filename)) {
					found = true;
					LOGGER.debug("File found");
				}
				// if the feed's title is wrong then the feed is incorrect
				else {
					LOGGER.debug("Test Failed: File Comment feed is incorrect");
					assertTrue(false);
				}
			}
		}
		assertTrue("Found file failed", found);

		LOGGER.debug("Step 6: Remove file comment");
		assertTrue(impersonatedService.deleteFileComment(fileUUID, commentUUID));

		// another way
		FileComment comment = new FileComment(
				"API Mod Test Comment - this is a comment from file owner");
		result1 = service.postFileComment(commentLink, comment);
		System.out.println(((Entry) result1).getContent());

		LOGGER.debug("COMPLETED TEST: File Comment");
	}

	public void testFileRecylceBin() {
		LOGGER.debug("BEGINNING TEST: File Trash");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "FileTrash" + timeStamp;

		ExtensibleElement eEle = createTestFile(filename, false);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		assertEquals("impersonate not match", impersonatedUser.getRealName(),
				((Entry) eEle).getAuthor().getName());

		service.deleteFile(fileUUID);

		Entry result = (Entry) service.retrieveFileFromTrash(fileUUID);
		assertEquals("impersonate not match", impersonatedUser.getRealName(),
				result.getAuthor().getName());

		// make sure the retrieved version is the same that was requested
		if (((Entry) result).getExtension(StringConstants.TD_UUID).getText()
				.equals(fileUUID)) {
			LOGGER.debug("Test Passed: Retrieve File from Trash");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Retrieve File from Trash: "
					+ result.toString());
			assertTrue(false);
		}

		//
		service.restoreFileFromTrash(fileUUID, result);

		// verify that it has been restored
		Entry fileMetaData = (Entry) service.getFileMetaDataFeed(fileUUID);
		if (fileMetaData != null) {
			LOGGER.debug("SUCCESS: File was successfully restored");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File was not restored");
			assertTrue(false);
		}

		//
		LOGGER.debug("START TEST: Finding Files in Recylce Bin");
		Feed recyleBinFeed = (Feed) service.getFilesInRecycleBinFeed();
		if (recyleBinFeed.getTitle().equals("Recycle Bin")) {
			LOGGER.debug("SUCCESS: Recycle Bin Feed Retrieved");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Recycle Bin Feed NOT FOUND");
			assertTrue(false);
		}

		//
		LOGGER.debug("BEGINNING TEST: Purge File from Trash");
		eEle = createTestFile(null, false);
		fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		service.deleteFile(fileUUID);
		assertTrue(service.purgeFileFromTrash(fileUUID));
		LOGGER.debug("COMPLETED TEST: Purge File from Trash");

		LOGGER.debug("COMPLETED TEST: File Trash");
	}

	public ExtensibleElement createSimplePublicFile(String name) {
		return createSimplePublicFile(name, "A File for test", "Hi!");
	}

	public ExtensibleElement createSimplePublicFile(String name,
			String description, String tag) {
		LOGGER.debug("Create a simple file");
		if (name == null)
			name = "testFile_" + StringGenerator.randomSentence(2);

		FileEntry testFileEntry = new FileEntry(null, name, description, tag,
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null, null,
				"a comment");
		return service.createFileNoInputStream(testFileEntry);
	}

	private ExtensibleElement createSimpleSharedFile(String name,
			String desciption, String tag) {

		// File testFile = new
		// File(this.getClass().getResource("/resources/lamborghini_murcielago_lp640.jpg").getFile());
		// InputStream infile =
		// this.getClass().getResourceAsStream("/resources/lamborghini_murcielago_lp640.jpg");
		FileEntry testFileEntry = new FileEntry(null, name, desciption, tag,
				Permissions.SHARED, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW,
				"Hello world, this is my new share!", otherUser.getUserId());

		Entry result;

		result = (Entry) service.createFileNoInputStream(testFileEntry);
		if (result.getAttributeValue(StringConstants.API_ERROR) != null) {
			if (Integer.parseInt(result.getExtension(
					StringConstants.API_RESPONSE_CODE).getText()) == 409) {
				assertTrue(true);
			} else {
				assertTrue(false);
				LOGGER.debug("Test Failed: Failed to create a new public file");
			}
		}

		return result;
	}

	private ExtensibleElement createPublicSharedFile(String name,
			String description, String tag, String shareWith) {
		ExtensibleElement eEle = null;
		File testFile = new File(this.getClass()
				.getResource("/resources/lamborghini_murcielago_lp640.jpg")
				.getFile());

		FileEntry testFileEntry = new FileEntry(testFile, name, description,
				tag, Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", null, null, "this is a file body");

		String fileUUID = "";// , userUUID = otherUser.getUserId();
		// try {
		eEle = service.createFile(testFileEntry);
		fileUUID = ((Entry) eEle).getExtension(StringConstants.TD_UUID)
				.getText();

		FileEntry fileShareEntry = new FileEntry(testFile, name, description,
				tag, Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", shareWith, fileUUID, "this is a file body");

		String shareUUID = "";
		ExtensibleElement shareElement = null;
		// try {
		shareElement = service.createFileShare(fileShareEntry);
		shareUUID = ((Entry) shareElement)
				.getExtension(StringConstants.TD_UUID).getText();

		// ExtensibleElement fileElement = service.retrieveFileShare(shareUUID);
		service.retrieveFileShare(shareUUID);

		return eEle;
	}

	@Test(timeOut = 120000)
	public void downloadFileAPI() throws URISyntaxException {
		LOGGER.debug("BEGINNING TEST: Download File API");

		ExtensibleElement result = createTestFile(null, false);

		String fileUUID = ((Entry) result)
				.getExtension(StringConstants.TD_UUID).getText();
		Entry eEle = null;

		eEle = (Entry) service.downloadFileWithRedirect(fileUUID);

		if (eEle.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("Test Failed: Download File API: " + eEle.toString());
			assertTrue(false);
		} else {
			LOGGER.debug("Test Passed: Download File API");
			assertTrue(true);
		}
		LOGGER.debug("COMPLETED TEST: Download File API");
	}

	@Test
	// Test for finding the public feed
	public void getPublicFeed() {
		LOGGER.debug("Start finding the feed.");
		Feed publicFiles = (Feed) service.getPublicFeed();
		if (publicFiles.getTitle().equals("Public Files")) {
			assertTrue(true);
			LOGGER.debug("Finished finding the feed");
		} else {
			LOGGER.debug("Error: Public Feed was not found");
			assertTrue(false);

		}
	}

	// @Test
	public void getPublicFeedInDepth() {
		// TODO hang on SC test
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD
				&& !StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Begining Test: Public Feed Parameters");
			long timeStamp = System.currentTimeMillis();
			String file1 = "Nero_" + timeStamp;
			String file2 = "Dante_" + timeStamp;
			String file3 = "Vergil_" + timeStamp;

			createSimplePublicFile(file1, "Demon Arm", "Nieve");
			createSimplePublicFile(file2, "Son of Sparta", "Stylish");
			createSimplePublicFile(file3, "Son of Sparta", "Calm");
			int counter = 5;
			boolean suite = true;
			boolean filter = true;
			Feed publicFiles = (Feed) service.getPublicFeed();
			Feed publicFilesInDepth;

			// see if two files exist on the new feed that has a page size of 2
			publicFilesInDepth = (Feed) service.getPublicFeedInDepth("&ps=2");
			if (publicFilesInDepth.getEntries().size() <= 2) {
				LOGGER.debug("Test Successful: Can set the page size successfully");
			} else {
				LOGGER.debug("Test Failed: Can not set the page size");
				suite = false;
			}

			// make sure entry 2 equals entry 1 of indepth
			publicFilesInDepth = (Feed) service
					.getPublicFeedInDepth("&ps=1&page=2");
			// TODO: search index need time to setup, cause first time error
			/*
			 * if(publicFilesInDepth.getEntries().get(0).getTitle().equals(
			 * publicFiles.getEntries().get(1).getTitle())){
			 * LOGGER.debug("Test Successful: Can set page and page size"); }
			 * else{ LOGGER.debug("Test Failed: Can not set page"); suite =
			 * false; }
			 */

			// Download 2 files multiple times and make sure they are the first
			// 2 on the list
			for (Entry e : publicFiles.getEntries()) {
				if (e.getTitle().equals(file2)) {
					while (counter != 0) {
						service.getDownloadFeed(e.getExtension(
								StringConstants.TD_UUID).getText());
						counter--;
					}
				}

				if (e.getTitle().equals(file1)) {
					counter = 3;
					while (counter != 0) {
						service.getDownloadFeed(e.getExtension(
								StringConstants.TD_UUID).getText());
						counter--;
					}
				}
			}

			publicFilesInDepth = (Feed) service
					.getPublicFeedInDepth("&sortBy=downloaded&sortOrder=desc");
			for (Entry e : publicFilesInDepth.getEntries()) {
				if (e.getTitle().equals(file2)
						&& e.getTitle().equals(file1) == false) {
					LOGGER.debug("Test Successful: Sorts in decending order based on downloads");
					break;
				} else if (e.getTitle().equals(file1)
						&& e.getTitle().equals(file2) == false) {
					LOGGER.debug("Test Failed: Does not sort in descending order the files based on downloads");
				}
			}

			// permissions make sure not null
			publicFilesInDepth = (Feed) service
					.getPublicFeedInDepth("&acls=true");
			if (publicFilesInDepth.getEntries().get(0)
					.getExtension(StringConstants.TD_PERMISSIONS) != null) {
				LOGGER.debug("Test Successful: Can get permissions of stated that Permissions will be shown");
			} else {
				LOGGER.debug("Test Failed: Can not get permissions");
				suite = false;
			}

			// Path test TD_PaTH make sure not null
			publicFilesInDepth = (Feed) service
					.getPublicFeedInDepth("&includePath=true");
			if (publicFilesInDepth.getEntries().get(0)
					.getExtension(StringConstants.TD_PATH) != null) {
				LOGGER.debug("Test Successful: Can get path if set to be shown");
			} else {
				LOGGER.debug("Test Failed: Not able to get path if set to be shown");
				suite = false;
			}

			// test if you can now access the tags
			publicFilesInDepth = (Feed) service
					.getPublicFeedInDepth("&includeTags=true");
			for (Entry e : publicFilesInDepth.getEntries()) {
				if (e.getTitle().equals(file3)) {
					if (e.getCategories().get(1).getLabel().equals("calm")) {
						LOGGER.debug("Test Successful: Found the tag after URL stated to show tags");
					} else {
						LOGGER.debug("Test Failed: Not able to find the tag after URL stated to show tags");
						suite = false;
					}
					break;
				}
			}

			// filter by tag
			publicFilesInDepth = (Feed) service
					.getPublicFeedInDepth("&tag=calm&includeTags=true");
			for (Entry e : publicFilesInDepth.getEntries()) {
				if (e.getCategories().get(1).getLabel().equals("calm") == false) {
					LOGGER.debug("Test Failed: Tag did not filter correctly");
					filter = false;
					suite = false;
				}
			}
			if (filter == true) {
				LOGGER.debug("Test Successful: Tag filters correctly");
			}

			// test since
			timeStamp = publicFiles.getEntries().get(0).getPublished()
					.getTime();
			publicFilesInDepth = (Feed) service.getPublicFeedInDepth("&since="
					+ timeStamp);

			if (publicFilesInDepth.getEntries().size() == 1) {
				LOGGER.debug("Test Successful: Filtered list down based on time since lastest post");
			} else {
				LOGGER.debug("Test Failed: Filtered the list incorrectly");
				suite = false;
			}

			assertTrue(suite);
		}

	}

	@Test
	public void getFoldersFeed() {
		LOGGER.debug("Begin Retrieving Folders Feed");
		Feed foldersFeed = (Feed) service.getFoldersFeed();
		assertTrue(foldersFeed != null);
		if (foldersFeed.getTitle().equals("Folders Feed")) {
			LOGGER.debug("Test Succeeded: Folders Feed was found.");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Folders Feed was not found.");
			assertTrue(false);
		}
		LOGGER.debug("Completed Folders Feed Test");
	}

	@Test
	public void getFoldersFeedWithParameters() throws FileNotFoundException {
		// deleteAllFilesAndFolders();

		String timeStamp = Utils.logDateFormatter.format(new Date());
		createFileFolder("Gamma" + timeStamp, "Gamma folder", "GAMMA",
				Permissions.PRIVATE);
		createFileFolder("Alpha" + timeStamp, "Alpha folder", "ALPHA",
				Permissions.PUBLIC);
		createFileFolder("Beta" + timeStamp, "Beta folder", "BETA",
				Permissions.PRIVATE);
		Feed foldersFeed;
		String[] folders;
		int i = 0;

		// access: untested, requires authenticated request
		// creator: untested, requires User ID
		// shared: untested, requires sharing
		// sI: untested, no clue what it does

		// Test for sorted order
		LOGGER.debug("Retrieving Folders Feed sorted by title");
		foldersFeed = (Feed) service.getFoldersFeed("?sortBy=title");
		assert (foldersFeed != null);
		folders = new String[foldersFeed.getEntries().size()];
		i = 0;
		for (Entry e : foldersFeed.getEntries()) {
			folders[i] = e.getTitle();
			i++;
		}
		for (i = 1; i < folders.length; i++) {
			if (folders[i - 1].compareTo(folders[i]) > 0) {
				LOGGER.debug("Test Failed: Folders feed is not in alphabetical order.");
				assertTrue(false);
			} else {
				assertTrue(true);
			}
		}

		// Test page and page size
		LOGGER.debug("Retrieving Folders Feed with page size 1 starting at page 2");
		foldersFeed = (Feed) service.getFoldersFeed("?ps=1&page=2");
		Feed originalFeed = (Feed) service.getFoldersFeed();
		String page2 = foldersFeed.getEntries().get(0).getTitle();
		String original2 = originalFeed.getEntries().get(1).getTitle();
		if (page2.equals(original2)) {
			assertTrue(true);
			LOGGER.debug("Folder on page 2 is correct");
		} else {
			assertTrue(false);
			LOGGER.debug("ERROR: Folder on page 2 is the wrong folder");
		}

		// Test sort order
		LOGGER.debug("Retrieving Folders Feed sorted by title descending");
		foldersFeed = (Feed) service
				.getFoldersFeed("?sortBy=title&sortOrder=desc");
		assert (foldersFeed != null);
		// Test for descending sorted order
		folders = new String[foldersFeed.getEntries().size()];
		i = 0;
		for (Entry e : foldersFeed.getEntries()) {
			folders[i] = e.getTitle();
			i++;
		}
		for (i = 1; i < folders.length; i++) {
			if (folders[i - 1].compareTo(folders[i]) < 0) {
				LOGGER.debug("ERROR: Folders feed is not in descending alphabetical order.");
				assertTrue(false);
			} else {
				assertTrue(true);
			}
		}

		// Test retrieve by name
		LOGGER.debug("Retrieving Folders Feed with name \"Beta\"");
		foldersFeed = (Feed) service.getFoldersFeed("?title=Beta" + timeStamp);
		assert (foldersFeed != null);
		folders = new String[foldersFeed.getEntries().size()];
		i = 0;
		for (Entry e : foldersFeed.getEntries()) {
			folders[i] = e.getTitle();
			i++;
		}
		if (folders.length == 1) {
			if (folders[0].equals("Beta" + timeStamp)) {
				LOGGER.debug("Feed contains unique folder named \"Beta\"");
				assertTrue(true);
			} else {
				LOGGER.debug("ERROR: Feed does not contain folder named \"Beta\"");
				assertTrue(false);
			}
		} else {
			LOGGER.debug("ERROR: Feed does not contain unique folder");
			assertTrue(false);
		}

		// Test retrieve public folders
		LOGGER.debug("Retrieving Folders Feed with Public Folders");
		foldersFeed = (Feed) service.getFoldersFeed("?visibility=public");
		assert (foldersFeed != null);
		boolean passPublic = true;
		for (Entry e : foldersFeed.getEntries()) {
			if (e.getExtension(StringConstants.TD_VISIBILITY).getText()
					.equals("public"))
				;
			else {
				passPublic = false;
				LOGGER.debug("ERROR: Feed does not contain only public folders");
				assertTrue(false);
			}
		}
		if (passPublic) {
			LOGGER.debug("Feed contains only public folders");
			assertTrue(true);
		}

		LOGGER.debug("Retrieving Folders Feed with Private Folders");
		foldersFeed = (Feed) service.getFoldersFeed("?visibility=private");
		assert (foldersFeed != null);
		boolean passPrivate = true;
		for (Entry e : foldersFeed.getEntries()) {
			String visibility = e.getExtension(StringConstants.TD_VISIBILITY).getText();
			if (visibility.equalsIgnoreCase("private") || visibility.equalsIgnoreCase("shared"))
				;
			else {
				passPrivate = false;
				LOGGER.debug("ERROR: Feed does not contain only private folders");
				assertTrue(false);
			}
		}
		if (passPrivate) {
			LOGGER.debug("Feed contains only private folders");
			assertTrue(true);
		}

		LOGGER.debug("Completed Folders Feed with Parameters Test");
	}

	@Test
	// Test for finding your pinned files feed
	public void getYourPinnedFilesFeed() {
		LOGGER.debug("BEGINNING TEST: getYourPinnedFilesFeed");
		Feed pinnedFiles = (Feed) service.getMyPinnedFilesFeed();
		if (pinnedFiles.getTitle().equals("Pinned Files Feed")) {
			assertTrue(true);
			LOGGER.debug("Finished finding the Pinned Files Feed");
		} else {
			LOGGER.debug("Error: Pinned Files Feed was not found");
			assertTrue(false);
		}
	}

	@Test
	// Test for finding Your pinned folders feed based on the title
	public void getYourPinnedFoldersFeed() {
		LOGGER.debug("BEGINNING TEST: getYourPinnedFoldersFeed");
		Feed pinnedFolders = (Feed) service.getMyPinnedFoldersFeed();
		if (pinnedFolders.getTitle().equals("Pinned Folders Feed")) {
			assertTrue(true);
			LOGGER.debug("Finished finding the Pinned Folders Feed");
		} else {
			LOGGER.debug("Error: Pinned Folders Feed was not found");
			assertTrue(false);
		}
	}

	@Test
	// Test for finding public folders feed based on the title
	public void getPublicFoldersFeed() {
		Feed publicFolders = (Feed) service.getPublicFolderFeed();
		if (publicFolders.getTitle().equals("Folders Feed")) {
			assertTrue(true);
			LOGGER.debug("Finished finding the Public Folders feed");
		} else {
			LOGGER.debug("Error: Public Folders feed was not found");
			assertTrue(false);
		}
	}

	@Test
	public void getUserLibraryFeed() throws FileNotFoundException, IOException {
		// need to find the uuid from where??
		ProfileData usr = ProfileLoader.getCurrentProfile();
		String userUUID = usr.getUserId();
		LOGGER.debug("Begin finding a user " + userUUID + " library feed");

		ExtensibleElement fileElement = service.getUserLibraryFeed(userUUID);

		if (fileElement.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(false);
			LOGGER.debug("Error: Public User library feed was not found");
		} else {
			assertTrue(true);
			LOGGER.debug("Finished finding user " + userUUID + " library feed");
		}
	}

	@Test
	// test for finding Library feed based on the title compared to the author
	// of the feed
	public void getMyLibraryFeed() {
		LOGGER.debug("Begin finding the users library feed");
		Feed myLibrary = (Feed) service.getMyLibraryFeed();
		if (myLibrary.getTitle().equals(myLibrary.getAuthor().getName())) {
			assertTrue(true);
			LOGGER.debug("Finished finding the users library feed");
		} else {
			LOGGER.debug("Error: The user's library feed was not found");
			assertTrue(false);
		}
	}

	@Test
	// 73563: Files/Wikis parameter name since for API consistency
	public void getPublicDateFilteredFeed() {
		LOGGER.debug("BEGIN: finding the users library date filtered feed");

		String parm = "dF";
		Feed myLibrarydF = (Feed) service.getPublicDateFilteredFeed(parm);

		if (myLibrarydF.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(false);
			LOGGER.debug("FAIL: Files in users library date filtered feed: "
					+ parm);
		} else {
			assertTrue(true);
			LOGGER.debug("SUCCESS: Files users library date filtered feed: "
					+ parm);
		}

		parm = "since";
		Feed myLibrarySince = (Feed) service.getPublicDateFilteredFeed(parm);

		if (myLibrarySince.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(false);
			LOGGER.debug("FAIL: Files in users library date filtered feed: "
					+ parm);
		} else {
			assertTrue(true);
			LOGGER.debug("SUCCESS: Files users library date filtered feed: "
					+ parm);
		}

		parm = "dT";
		Feed myLibrarydT = (Feed) service.getPublicDateFilteredFeed(parm);

		if (myLibrarydT.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(false);
			LOGGER.debug("FAIL: Files in users library date filtered feed: "
					+ parm);
		} else {
			assertTrue(true);
			LOGGER.debug("SUCCESS: Files users library date filtered feed: "
					+ parm);
		}

		parm = "before";
		Feed myLibraryBefore = (Feed) service.getPublicDateFilteredFeed(parm);

		if (myLibraryBefore.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(false);
			LOGGER.debug("FAIL: Files in users library date filtered feed: "
					+ parm);
		} else {
			assertTrue(true);
			LOGGER.debug("SUCCESS: Files users library date filtered feed: "
					+ parm);
		}

		LOGGER.debug("COMPLETE: finding the users library date filtered feed");
	}

	@Test
	// test for finding Library feed based on the title compared to the author
	// of the feed
	public void getTagsFeed() throws Exception {
		LOGGER.debug("Begin finding the users library feed");
		String tag = "cool";
		createSimplePublicFile(null, "test file", tag);
		// delay(2000);
		String tags = service.getTagsFeed(tag);

		// parse JSON for the firs tag
		JSONObject jObject = new JSONObject(tags);
		JSONArray items = jObject.optJSONArray("items");

		if (!items.isEmpty()) {
			JSONObject tag1 = items.getJSONObject(0);
			String parsedTag = tag1.optString("name");

			if (parsedTag.equals(tag)) {
				assertTrue(true);
				LOGGER.debug("Finished finding the users library feed");
			} else {
				LOGGER.debug("Error: The user's library feed was not found");
				assertTrue(false);
			}
		} else {
			LOGGER.debug("Error: The dalay time is not enough");
		}
	}

	@Test
	// test for finding Library feed based on the title compared to the author
	// of the feed
	public void getRecentFolderFeed() {
		LOGGER.debug("Begin finding the feed of folders that just added files");
		Feed changeFolder = (Feed) service.getRecentFolderFeed();
		if (changeFolder.getTitle().equals("Folders Added To Feed")) {
			assertTrue(true);
			LOGGER.debug("Finished finding the folders that recently added files feed");
		} else {
			LOGGER.debug("Error: The change feed was not found");
			assertTrue(false);
		}
	}

	/*
	 * TB 10/11 Not sure why but some of this test uses Atom-based data, but
	 * some of the result sets are JSON....
	 */
	// @Test
	public void deleteShareLinks() throws Exception {
		/**
		 * Step 1: Get the user id of the user id the test is being run as. 
		 * Step 2: Build the url to the user's mylibrary, and do a GET on that feed
		 * Step 3: Get the library Id for that user 
		 * Step 4: Create a file, set share permissions, and post the test file 
		 * Step 5: Get the docId from the post and then build the URL to the endpoint 
		 * Step 6: Perform a GET on the endpoint and verify it is working 
		 * Step 7: Delete the share permissions 
		 * Step 8: Verify that the delete was successful
		 */
		LOGGER.debug("BEGINNING TEST: deleteShareLinks");
		LOGGER.debug("Step 1: Get the user id the test is being run as");

		UserPerspective user = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			user = new UserPerspective(5, Component.FILES.toString(), useSSL);
		} else {
			// TJB 10/29/13 Hack due to SES ticket 273887 - changes to LDAP
			// tds62ldap. Will revert once fixed.
			// user = new FilesPerspective(1, Component.FILES.toString(),
			// useSSL);
			user = new UserPerspective(5, Component.FILES.toString(), useSSL);
		}
		FilesService serviceB = user.getFilesService();
		String userId = user.getUserId();
		LOGGER.debug("user: id=" + user.getUserId() + ",email=" + user.getEmail() + ",username=" + user.getUserName());
		

		LOGGER.debug("Step 2: Build the url to the user's mylibrary, and do a GET on that feed");
		String url2 = serviceB.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		ExtensibleElement userLib = serviceB.getUrlFeed(url2);
		assertEquals("get mylibrary feed"+serviceB.getDetail(), 200, serviceB.getRespStatus());
		
		LOGGER.debug("Step 3: Get the library Id for that user");
		String ft = ((Feed)userLib).toString();
		int a = ft.indexOf("rel=\"self\"");
		String libId = ft.substring(a - 43, a - 7);

		LOGGER.debug("Step 4: Create a file, set share permissions, and post the test file");
		File testFile = new File("Mooses.txt");
		FileEntry testFileEntry = new FileEntry(testFile, "Burritos_"
				+ RandomStringUtils.randomAlphanumeric(5),
				"Beefy, Cheesey, and Yummy", "taste good great yum yummy",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null,
				ProfileLoader.getProfile(2).getUserId());
		Entry result = (Entry) serviceB.createFileNoInputStream(testFileEntry); 

		LOGGER.debug("Step 5: Get the docId from the post and then build the URL to the endpoint");
		String docId = result.getId().toString().split("td:")[1];
		String finalLink = serviceB.getURLString() + URLConstants.FILES_LIBRARY
				+ libId + "/document/" + docId;

		LOGGER.debug("Step 6: Perform a GET on the endpoint and verify it is working");
		String shareDocText = serviceB.getResponseString(finalLink
				+ "/sharelinks/feed");
		// if
		// (shareDocText.contains("{\"items\":[{\"target\":{\"userState\":\"active\",\"name\":")
		// == false){
		//if (shareDocText.contains("{\"items\":[{\"target\":{") == false) {
		//	LOGGER.debug("Document formatting mismatch (1)");
		//	assertTrue(false);
		//}

		if (shareDocText.contains("\"id\":\"" + userId + "\",") == false) {
			// checks the posting users id, verifies it is there
			LOGGER.debug("Id for the posting user was not found in the document");
			assertTrue(false);
		}
		if (shareDocText.contains("\"id\":\""
				+ ProfileLoader.getProfile(2).getUserId() + "\",") == false) { 
			// checks the person I'm sharing with, verifies they're present too
			LOGGER.debug("Id for the user that the file is being shared with was not found in the document");
			assertTrue(false);
		}

		LOGGER.debug("Step 7: Delete the share permissions (and the file?)");
		serviceB.deleteFileFullUrl(finalLink + "/permissions/feed"); // check
		assertEquals("Delete failed ", 204, serviceB.getRespStatus());
		// again

		LOGGER.debug("Step 8: Verify that the deletion of sharing permissions was successful. Should return 403");
		String verifyDelete = service.getResponseString(finalLink
				+ "/permissions/feed");
		// assertTrue(verifyDelete.contains("{\"items\":[]")); Former SmartCloud
		// verification.

		assertTrue(verifyDelete.contains("AccessDenied"));

		LOGGER.debug("Ending Test deleteShareLinks");
	}

	/**
	 * Tests the ability to get recommendations (likes)
	 * 
	 * @see 
	 *      /basic/api/library/{library-id}/document/{document-id}/recommendedby/
	 *      feed
	 * @param ?format=xml&page=%d&pageSize=%d
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	// @Test
	public void getRecommendations() throws Exception {
		/**
		 * Step 1: Get the user id of the user id the test is being run as. Step
		 * 2: Build the url to the user's mylibrary, and do a GET on that feed
		 * Step 3: Get the library Id for that user Step 4: Create and post a
		 * test file Step 5: Get the docId from the post and then build the URL
		 * to the endpoint Step 6: Build a recommendation entry and post it to
		 * the file's feed Step 7: Perform a GET on the endpoint and verify it
		 * is working.
		 */
		LOGGER.debug("Beginning Test: getRecommendations()");
		LOGGER.debug("Step 1: Get the user id the test is being run as");
		String userId = user.getUserId();

		LOGGER.debug("Step 2: Build the url to the user's mylibrary, and do a GET on that feed");
		String url2 = service.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		Feed userLib = (Feed) service.getUrlFeed(url2);

		LOGGER.debug("Step 3: Get the library Id for that user");
		String ft = userLib.toString();
		int a = ft.indexOf("rel=\"self\"");
		String libId = ft.substring(a - 43, a - 7);

		LOGGER.debug("Step 4: Create and post a test file");
		File testFile = new File("Mooses.txt");
		FileEntry testFileEntry = new FileEntry(testFile, "Burritos_"
				+ RandomStringUtils.randomAlphanumeric(5),
				"Beefy, Cheesey, and Yummy", "taste good great yum yummy",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);
		Entry result = (Entry) service.createFileNoInputStream(testFileEntry); // File
		// is
		// posted.

		LOGGER.debug("Step 5: Get the docId from the post and then build the URL to the endpoint");
		String docId = result.getId().toString().split("td:")[1];
		String finalLink = service.getURLString() + URLConstants.FILES_LIBRARY
				+ libId + "/document/" + docId;

		LOGGER.debug("Step 6: Build a recommendation entry and post it to the file's feed");
		Entry recommendationEntry = abdera.getFactory().newEntry();
		recommendationEntry.addCategory("tag:ibm.com,2006:td/type",
				"recommendation", "recommendation");
		service.postRecommendationToFile(finalLink + "/feed",
				recommendationEntry);

		LOGGER.debug("Step 7: Perform a GET on the endpoint and verify it is working");
		finalLink += "/recommendedby/feed";
		String recommendDocText = service.getResponseString(finalLink);

		// Assertion Statements for Verification
		if (recommendDocText
				.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?><results pageSize=\"10\" label=\"name\" totalSize=\"0\" identifier=\"id\" page=\"1\" xmlns:thr=\"http://purl.org/syndication/thread/1.0\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:snx=\"http://www.ibm.com/xmlns/prod/sn\" xmlns:td=\"urn:ibm.com/td\" xmlns=\"\"></results>")) {
			LOGGER.debug("Recommendations list matches template for blank (or no) recommendations; Test Failed");
			assertTrue(false);
		}
		if (!recommendDocText.contains("totalSize=\"1\"")) {
			LOGGER.debug("Recommendations is not equal to 1, Test Failed");
			assertTrue(false);
		}
		if (!recommendDocText.contains(userId)) {
			LOGGER.debug("The recommendations listing file does not contain the user the test was run as. Test Failed");
			assertTrue(false); // user test is run as also recommends/likes the
			// post
		}
		LOGGER.debug("Ending Test");
	}

	/**
	 * Tests the ability to delete recommendations (likes)
	 * 
	 * @see 
	 *      /basic/api/library/{library-id}/document/{document-id}/recommendation
	 *      /{recommendationId}/entry
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	// @Test
	public void deleteRecommendations() throws Exception {
		/**
		 * Step 1: Get the user id of the user id the test is being run as. Step
		 * 2: Build the url to the user's mylibrary, and do a GET on that feed
		 * Step 3: Get the library Id for that user Step 4: Create and post a
		 * test file Step 5: Get the docId from the post and then build the URL
		 * to the endpoint Step 6: Build a recommendation entry and post it to
		 * the file's feed Step 7: Perform a get on the recommendation then
		 * delete it
		 */
		LOGGER.debug("Beginning Test: getRecommendations()");
		LOGGER.debug("Step 1: Get the user id the test is being run as");
		String userId = user.getUserId();

		LOGGER.debug("Step 2: Build the url to the user's mylibrary, and do a GET on that feed");
		String url2 = service.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		Feed userLib = (Feed) service.getUrlFeed(url2);

		LOGGER.debug("Step 3: Get the library Id for that user");
		String ft = userLib.toString();
		int a = ft.indexOf("rel=\"self\"");
		String libId = ft.substring(a - 43, a - 7);

		LOGGER.debug("Step 4: Create and post a test file");
		File testFile = new File("Mooses.txt");
		FileEntry testFileEntry = new FileEntry(testFile, "Burritos_"
				+ RandomStringUtils.randomAlphanumeric(5),
				"Beefy, Cheesey, and Yummy", "taste good great yum yummy",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);
		Entry result = (Entry) service.createFileNoInputStream(testFileEntry); // File
		// is
		// posted.

		LOGGER.debug("Step 5: Get the docId from the post and then build the URL to the endpoint");
		String docId = result.getId().toString().split("td:")[1];
		String finalLink = service.getURLString() + URLConstants.FILES_LIBRARY
				+ libId + "/document/" + docId;

		LOGGER.debug("Step 6: Build a recommendation entry and post it to the file's feed");
		Entry recommendationEntry = abdera.getFactory().newEntry();
		recommendationEntry.addCategory("tag:ibm.com,2006:td/type",
				"recommendation", "recommendation");

		int initialSizeValue = sizeValue(finalLink);

		// post the recommendation therefore incrementing the totalSize by 1
		service.postRecommendationToFile(finalLink + "/feed",
				recommendationEntry);

		// update the recommendations feed/doc String, and it's variables, then
		// set the finalSizeValue
		int secondSizeValue = sizeValue(finalLink);
		assertEquals("final totalSize value incorrect", secondSizeValue,
				initialSizeValue + 1);

		LOGGER.debug("Deleting recommendation and confirming deletion");
		service.deleteFileFullUrl(finalLink + "/recommendation/" + userId
				+ "/entry");
		int finalSizeValue = sizeValue(finalLink);
		assertEquals(
				"Test Failure: Final number of recommendations is not equal to original number of recommendations",
				finalSizeValue, initialSizeValue);
		assertTrue(
				"Size value was unchanged from before the recommendation was deleted and after",
				finalSizeValue != secondSizeValue);

		LOGGER.debug("Ending Test");
	}

	/**
	 * Made for test above this to determine how many recommendations there are
	 * in the feed of recommendations
	 */
	private int sizeValue(String finalLink) throws Exception {
		String recFeedTxt = service.getResponseString(finalLink
				+ "/recommendedby/feed");
		int indexStart = recFeedTxt.indexOf("totalSize=") + 11;
		int indexEnd = recFeedTxt.indexOf("\"", indexStart);
		int toReturn = Integer.parseInt(recFeedTxt.substring(indexStart,
				indexEnd));
		return toReturn;
	}

	/**
	 * Tests the ability to delete recommendations (likes)
	 * 
	 * @see 
	 *      /basic/api/library/{library-id}/document/{document-id}/recommendation
	 *      /{recommendationId}/entry
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 */
	// @Test
	public void getDownloadInfoForUser() throws Exception {
		/**
		 * Step 1: Get the user id of the user id the test is being run as. Step
		 * 2: Build the url to the user's mylibrary, and do a GET on that feed
		 * Step 3: Get the library Id for that user Step 4: Create and post a
		 * test file Step 5: Get the docId from the post and then build the URL
		 * to the endpoint Step 6: Build a recommendation entry and post it to
		 * the file's feed Step 7: Perform a GET on the endpoint and verify it
		 * is working.
		 */
		LOGGER.debug("BEGINNING TEST: getDownloadInfoForUser()");
		LOGGER.debug("Step 1: Get the user id the test is being run as");
		// UserPerspective usr = new UserPerspective(2,
		// Component.FILES.toString(), useSSL); //ajones242
		String userId = user.getUserId();
		// FilesService serviceB = usr.getFilesService();

		LOGGER.debug("Step 2: Build the url to the user's mylibrary, and do a GET on that feed");
		String url2 = service.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		Feed userLib = (Feed) service.getUrlFeed(url2);

		LOGGER.debug("Step 3: Get the library Id for that user");
		String ft = userLib.toString();
		int a = ft.indexOf("rel=\"self\"");
		String libId = ft.substring(a - 43, a - 7);

		LOGGER.debug("Step 4: Create and post a test file");
		File testFile = new File("Mooses.txt");
		FileEntry testFileEntry = new FileEntry(testFile, "Burritos_"
				+ RandomStringUtils.randomAlphanumeric(5),
				"Beefy, Cheesey, and Yummy", "taste good great yum yummy",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);
		Entry result = (Entry) service.createFileNoInputStream(testFileEntry); // File
		// is
		// posted.
		result.getEditLinkResolvedHref();

		LOGGER.debug("Step 5: Get the docId from the post and then download the document (to generate doc info)");
		String docId = result.getId().toString().split("td:")[1];
		// You have to download the file before you can get the info for the
		// document
		service.getDownloadFeed(docId);

		LOGGER.debug("Step 6: Build the url to the document info");

		String finalLink = service.getURLString() + URLConstants.FILES_LIBRARY
				+ libId + "/document/" + docId + "/download/" + userId
				+ "/entry";

		Entry downloadInformation = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			downloadInformation = (Entry) service
					.getUrlFeedWithRedirect(finalLink);
		} else {
			downloadInformation = (Entry) service.getUrlFeed(finalLink);
		}

		// Validations - verify the category is "download" and author
		for (Category cat : downloadInformation.getCategories()) {
			assertEquals(true,
					cat.getAttributeValue("label").equalsIgnoreCase("download"));
		}

		Person author = downloadInformation.getAuthor();
		assertEquals(true, author.getName()
				.equalsIgnoreCase(user.getRealName()));

		LOGGER.debug("ENDING TEST: getDownloadInfoForUser");
	}

	/**
	 * Tests the ability to get information about a specific library
	 * 
	 * @see /files/basic/api/library/{library-id}/entry
	 * @param ?includeQuota=true
	 * @throws Exception
	 */
	//@Test
	public void getLibraryInfo() throws FileNotFoundException, Exception {
		/**
		 * Step 1: Get the user id the test is being run as Step 2: Build the
		 * url to the user's mylibrary, and do a GET on that feed Step 3: Get
		 * the library Id for that user Step 4: Get the library info document
		 * Step 5: Verify the library document by verifying author.
		 */
		LOGGER.debug("Beginning Test: getLibraryInfo()");
		LOGGER.debug("Step 1: Get the user id the test is being run as");
		ProfileData usr = ProfileLoader.getProfile(5); // run as Amy Jones 101
		String userId = usr.getUserId();
		LOGGER.debug("usr: id=" + usr.getUserId() + ",email=" + usr.getEmail() + ",username=" + usr.getUserName());

		LOGGER.debug("Step 2: Build the url to the user's mylibrary, and do a GET on that feed");
		String url2 = service.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		Feed userLib = (Feed) service.getUrlFeed(url2);

		LOGGER.debug("Step 3: Get the library Id for that user");
		String ft = userLib.toString();
		int a = ft.indexOf("rel=\"self\"");
		String libId = ft.substring(a - 43, a - 7);

		LOGGER.debug("Step 4: Get the library info document");
		Entry libraryInfo = (Entry) service.getUrlFeed(service.getURLString()
				+ URLConstants.FILES_LIBRARY + libId + "/entry");

		LOGGER.debug("Step 5: Verifying author...");
		assertEquals(
				true,
				libraryInfo.getAuthor().getName()
						.equalsIgnoreCase(usr.getRealName()));

		LOGGER.debug("Ending Test");
	}

	@Test(timeOut = 120000)
	public void downloadFile() {
		LOGGER.debug("BEGINNING TEST: Download a File");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();
		Entry fileToDownload = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToDownload = e;
			}
		}
		// ensure that the public file was found
		assertTrue(fileToDownload != null);
		String docId = fileToDownload.getId().toString().substring(20);
		Entry downloadResponse = (Entry) service.getDownloadFeed(docId);

		// Validate using header extensions
		List<Element> headers = downloadResponse.getExtensions();
		if (headers.size() > 5) {
			LOGGER.debug("SUCCESS: Download request was corerct");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Download request headers did not match downloaded file properties");
			assertTrue(false);
		}

		/*
		 * following code working on IBM http server for apache2.2.3, is get(9)
		 * & (12) String etag =
		 * downloadResponse.getExtensions().get(8).getText(); String idToVerify
		 * = etag.substring(1,etag.indexOf(":")); String contentType =
		 * downloadResponse.getExtensions().get(10).getText();
		 * 
		 * if(idToVerify.equals(docId) && contentType.equals("text/plain")){
		 * LOGGER.debug("SUCCESS: Download request was corerct");
		 * assertTrue(true); } else{ LOGGER.debug(
		 * "ERROR: Download request headers did not match downloaded file properties"
		 * ); assertTrue(false); }
		 */
		LOGGER.debug("TEST COMPLETED: Download a File");
	}

	@Test
	public void updateFileMetaData() {
		LOGGER.debug("BEGINNING TEST: Update File MetaData");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File" + timeStamp;
		String updatefilename = "updateFile" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();
		Entry fileToRetrieve = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToRetrieve = e;
			}
		}
		// ensure that the public file was found
		assertTrue(fileToRetrieve != null);
		String docId = fileToRetrieve.getExtension(StringConstants.TD_UUID)
				.getText();

		// create new meta data to update with
		File updatedFile = new File("updateFile.txt");
		FileEntry updatedEntry = new FileEntry(updatedFile, updatefilename,
				"The Bond Car", "Sick Ride!", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, null, null);

		// update file
		service.updateFileMetaData(docId, updatedEntry);

		// get file meta data to verify update
		Entry fileMetaData = (Entry) service.getFileMetaDataFeed(docId);
		// check the metadata against the input given
		if (fileMetaData.getTitle().equals(updatefilename)
				&& fileMetaData.getExtension(StringConstants.TD_UUID).getText()
						.equals(docId)) {
			LOGGER.debug("SUCCESS: File was successfully updated");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File was not updated correctly");
			assertTrue(false);
		}
		LOGGER.debug("TEST COMPLETED: Updating File Meta Data");
	}

	public void deleteFileAndFileMetaData() {
		LOGGER.debug("BEGINNING TEST: Delete File Meta Data");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();
		Entry fileToDelete = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToDelete = e;
			}
		}
		// ensure that the public file was created and found
		assertTrue(fileToDelete != null);

		// get document Id and delete file
		String docId = fileToDelete.getId().toString().substring(20);
		service.deleteFile(docId);

		// search for the same file again
		myFilesFeed = (Feed) service.getPublicFeed();
		boolean found = false;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getId().toString().substring(20).equals(docId)) {
				found = true;
			}
		}
		if (!found) {
			LOGGER.debug("SUCCESS: File and File MetaData were successfully deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File and File MetaData were not deleted");
			assertTrue(false);
		}
		LOGGER.debug("TEST Deleting File Meta Data COMPLETE");
	}

	@Test(timeOut = 120000)
	public void purgeAllFilesFromTrash() {
		LOGGER.debug("BEGINNING TEST: Purge All Files From Trash");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();
		Entry fileToDelete = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToDelete = e;
			}
		}
		// ensure that the public file was created and found
		assertTrue(fileToDelete != null);

		// get document Id and delete file
		String docId = fileToDelete.getId().toString().substring(20);
		service.deleteFile(docId);

		// check to see that the item is in the trash
		Feed trashFeed = (Feed) service.getFilesInRecycleBinFeed();
		boolean found = false;
		for (Entry trashItem : trashFeed.getEntries()) {
			if (trashItem.getExtension(StringConstants.TD_UUID).getText()
					.equals(docId)) {
				found = true;
			}
		}
		// Verify item was found
		assertTrue(found);

		// purge all items from trash
		service.purgeAllFilesFromTrash();

		// check to see that no items are in the trash
		trashFeed = (Feed) service.getFilesInRecycleBinFeed();
		if (trashFeed.getEntries().size() == 0) {
			LOGGER.debug("SUCCESS: No items were found in trash. All items have been purged.");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Items were found in the trash. All items were not purged successfully.");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Purge All Items From Trash");
	}

	@Test
	public void testFileShare() {
		/*
		 * Tests file share Step 1: Create file Step 2: Post file share Step 3:
		 * Retrieve shared file Step 4: Remove shared file Step 5: test file
		 * shares feed
		 */
		// TODO add vreify for each steps
		LOGGER.debug("BEGINNING TEST:  File Share");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "FileShare" + timeStamp;
		String fileUUID, shareUUID, userUUID = otherUser.getUserId();

		FileEntry testFileEntry = new FileEntry(null, filename, "Stars Agent",
				"STARS", Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", null, null, "this is a file body");
		ExtensibleElement eEle = service.createFile(testFileEntry);
		assertEquals("create file entry", 201, service.getRespStatus());
		fileUUID = ((Entry) eEle).getExtension(StringConstants.TD_UUID)
				.getText();

		FileEntry fileShareEntry = new FileEntry(null, filename, "Stars Agent",
				"STARS", Permissions.PUBLIC, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.VIEW,
				"hi shares", userUUID, fileUUID, "this is a file body");
		eEle = service.createFileShare(fileShareEntry);
		assertEquals("share with other user", 201, service.getRespStatus());
		shareUUID = ((Entry) eEle).getExtension(StringConstants.TD_UUID)
				.getText();

		if (StringConstants.VMODEL_ENABLED) {
			fileShareEntry = new FileEntry(null, filename, "Stars Agent",
					"STARS", Permissions.PUBLIC, true, Notification.ON,
					Notification.ON, null, null, true, true,
					SharePermission.VIEW, "hi shares", visitor.getUserId(),
					fileUUID, "this is a file body");
			eEle = service.createFileShare(fileShareEntry);
			assertEquals("share with visitor", 400, service.getRespStatus());
		}

		service.retrieveFileShare(shareUUID);

		boolean ret = service.deleteFileShare(fileUUID);

		if (ret == false) {
			LOGGER.debug("Test Failed: Delete File Share");
			assertTrue(false);
		} else {
			LOGGER.debug("Test Passed: Delete File Share");
			assertTrue(true);
		}

		LOGGER.debug("finding the file shares feed");
		Feed fileSharesFeed = (Feed) service.getFileSharesFeed();
		if (fileSharesFeed.getTitle().equals("Files Shared With You")) {
			LOGGER.debug("SUCCESS: File Shares Feed Retrieved");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Files Shares Feed was NOT Retrieved");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: File Share");
	}

	public ExtensibleElement createTestFile(String title, boolean createVersion) {

		Entry result = null;
		if (title == null)
			title = "testFile" + StringGenerator.randomSentence(2)
					+ Utils.logDateFormatter.format(new Date());

		FileEntry fileMetaData = new FileEntry(null, title,
				"This is test file", "cool test file", Permissions.PUBLIC,
				true, Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, "ShareSummary", null, null, "a comment");

		if (createVersion) {
			result = (Entry) service.createFile(fileMetaData, createVersion);
		} else
			result = (Entry) service.createFile(fileMetaData);

		return result;
	}

	@Test
	public void retrieveVersionOfFile() {
		LOGGER.debug("BEGINNING TEST: Retrieve Version of File");

		ExtensibleElement eEle = createTestFile(null, true);

		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		String versionUUID = ((Entry) eEle).getExtension(
				StringConstants.TD_VERSIONUUID).getText();

		ExtensibleElement result = service.retrieveVersionOfFile(fileUUID,
				versionUUID);

		// make sure the retrieved version is the same that was requested
		if (((Entry) result).getExtension(StringConstants.TD_UUID).getText()
				.equals(versionUUID)) {
			LOGGER.debug("Test Passed: Retrieve Version of File");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Retrieve Version of File: "
					+ result.toString());
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Retrieve Version of File");
	}

	// @Test
	public void downloadVersionOfFile() {
		LOGGER.debug("BEGINNING TEST: Download Version of File");

		ExtensibleElement eEle = createTestFile(null, true);

		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		String versionUUID = ((Entry) eEle).getExtension(
				StringConstants.TD_VERSIONUUID).getText();

		Entry result = (Entry) service.downloadVersionOfFile(fileUUID,
				versionUUID);

		// make sure the retrieved file is the same that was requested
		if (result.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("Test Passed: Download Version of File");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Download Version of File: "
					+ result.toString());
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Download Version of File");
	}

	// @Test
	public void deleteAllVersionsOfFile() {

		LOGGER.debug("BEGINNING TEST: Delete All Versions of File");

		ExtensibleElement eEle = createTestFile(null, true);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		if (service.deleteAllVersionsOfFile(fileUUID)) {
			assertTrue(true);
			LOGGER.debug("Test Passed: Delete All Versions of File");
		} else {
			assertTrue(false);
			LOGGER.debug("Test Failed: Delete All Versions of File");
		}
		LOGGER.debug("COMPLETED TEST: Delete All Versions of File");
	}

	@Test
	public void testFileVersion() {
		LOGGER.debug("BEGINNING TEST: File Version");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "FileVersion" + timeStamp;

		ExtensibleElement eEle = createTestFile(filename, true);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		String versionUUID = ((Entry) eEle).getExtension(
				StringConstants.TD_VERSIONUUID).getText();

		// can't retrieve by visitor and other user
		/*if (StringConstants.VMODEL_ENABLED) {
			eEle = visitorService.retrieveVersionOfFile(fileUUID, versionUUID);
			assertEquals("Retrieve by visitor", 403,
					visitorService.getRespStatus());

			eEle = extendedEmpService.retrieveVersionOfFile(fileUUID,
					versionUUID);
			assertEquals("Retrieve by extendedEmp", 404,
					extendedEmpService.getRespStatus());
		}*/

		ExtensibleElement result = service.retrieveVersionOfFile(fileUUID,
				versionUUID);
		// make sure the retrieved version is the same that was requested
		if (((Entry) result).getExtension(StringConstants.TD_UUID).getText()
				.equals(versionUUID)) {
			LOGGER.debug("Test Passed: Retrieve Version of File");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Retrieve Version of File: "
					+ result.toString());
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: File Version");
	}
	
	//@Test
	public void testFileVersionByVisitor() {
		LOGGER.debug("BEGINNING TEST: File Version");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "FileVersion" + timeStamp;

		ExtensibleElement eEle = createTestFile(filename, true);
		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();
		String versionUUID = ((Entry) eEle).getExtension(
				StringConstants.TD_VERSIONUUID).getText();

		// can't retrieve by visitor and other user
		if (StringConstants.VMODEL_ENABLED) {
			eEle = visitorService.retrieveVersionOfFile(fileUUID, versionUUID);
			assertEquals("Retrieve by visitor", 403,
					visitorService.getRespStatus());

			eEle = extendedEmpService.retrieveVersionOfFile(fileUUID,
					versionUUID);
			assertEquals("Retrieve by extendedEmp", 404,
					extendedEmpService.getRespStatus());
		}
	}

	@Test
	public void getMyServiceDocument() throws FileNotFoundException,
			IOException {
		LOGGER.debug("BEGINNING TEST: Get My (Current User) Service Document");

		UserPerspective user2=null;
		try {
			user2 = new UserPerspective(5,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FilesService service2 = user2.getFilesService();

		FOMService fs = (FOMService) service2.getMyServiceDocument();
		String serviceTitle = fs.getWorkspaces().get(0).getTitle();
		if (user2.getRealName().equals(serviceTitle)) {
			LOGGER.debug("SUCCESS: Service document for current user was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Service document for current user was not found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get My Service Document");
	}

	@Test
	public void getFileServiceDocument() {
		LOGGER.debug("BEGINNING TEST: Get Files Service Document");

		// obtain serviceDocument
		FOMService fs = (FOMService) service.getFilesServiceDocument();

		// Retrive workspaces and get the titles
		ArrayList<String> workspaceTitles = new ArrayList<String>();
		for (Workspace w : fs.getWorkspaces())
			workspaceTitles.add(w.getTitle());
		// Validate that document contains correct workspaces
		if (workspaceTitles.contains("Files")
				&& workspaceTitles.contains("Libraries")
				&& workspaceTitles.contains("Folders")
				&& workspaceTitles.contains("Recycle Bin")) {
			LOGGER.debug("SUCCESS: Service document for Files was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Service document for Files was not found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Files Service Document");
	}

	@Test
	public void getFileModDoc() throws URISyntaxException,
			FileNotFoundException, IOException {
		LOGGER.debug("Beginning Test: Find the Files Moderation Service document");
		if (StringConstants.MODERATION_ENABLED) {
			UserPerspective adminUser=null;
			try {
				adminUser = new UserPerspective(0,
						Component.FILES.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			FilesService adminService = adminUser.getFilesService();
			FOMService moddoc = (FOMService) adminService.getModSerDoc();

			if (moddoc.getWorkspaces().get(0).getTitle()
					.equals("Moderation of Files documents")) {
				LOGGER.debug("Test Successful: Found the Files Moderation Service document");
				assertTrue(true);
			} else {
				LOGGER.debug("Test Failed: Could not find the Files Moderation Service document");
				assertTrue(false);
			}
		}
	}

	@Test
	// 52899: [LC 4.0] GET people/feed with bad email address returns 500 error
	public void getInfoByBogusUsersEmail() {
		LOGGER.debug("Beginning Test: Get User By Bogus Email");
		// run with a dummy user to make sure does not return error
		ExtensibleElement eEle = service
				.getInfoByUsersEmail("dummyUser@dummyid.com");

		if (eEle.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("Test Failed: Get User By Bogus Email: "
					+ eEle.toString());
			assertTrue(false);
		} else {
			LOGGER.debug("Test Passed: Get User By Bogus Email");
			assertTrue(true);
		}
		LOGGER.debug("COMPLETED TEST: Get User By Bogus Email");
	}

	@Test
	public void getPeopleSelfFeed() {
		LOGGER.debug("Beginning Test: Get People feed");
		ExtensibleElement eEle = service.getPeopleSelfFeed();
		assertEquals("get people feed-xml ", 200, service.getRespStatus());
		assertEquals("get people feed-xml ", "name",
				eEle.getAttributeValue("label"));

		String json = service.getPeopleSelfJSON();
		assertEquals("get people feed-json ", 200, service.getRespStatus());
		assertTrue("get people feed-json ", json.contains("items"));

		LOGGER.debug("COMPLETED TEST: Get People Feed");
	}

	@Test
	// Document already unlocked is now returning 204 instead of 404
	public void verifyUnlockOnUnlockedFile() {
		LOGGER.debug("BEGINNING TEST: Verify Unlock On Unlocked File");

		ExtensibleElement eEle = createTestFile(null, false);

		String fileUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		int result = service.unlockFile(fileUUID);

		if (result == 204) {
			assertTrue(true);
			LOGGER.debug("Test Passed: Verify Unlock On Unlocked File Returned 204");
		} else {
			assertTrue(false);
			LOGGER.debug("Test Failed: Verify Unlock On Unlocked File -- Return is: "
					+ result);
		}
		LOGGER.debug("Completed TEST: Verify Unlock On Unlocked File");
	}

	@Test
	public void createFileUsingMultiPartPost() {
		LOGGER.debug("BEGINNING TEST: Creating public file with multi part post");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File4" + timeStamp;

		// create file entry and parameter tags
		File testFile = new File("forThePeople.txt");
		FileEntry testFileEntry = new FileEntry(testFile, filename,
				"The Bond Car", "JamesBond", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, null, null);
		String params = "?tag=updatedFromUrl";

		service.createFileMultiPartPost(params, testFileEntry);

		// retrieve file
		Feed myFilesFeed = (Feed) service.getPublicFeed();
		Entry fileToRetrieve = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {

				fileToRetrieve = e;
			}
		}
		// ensure that the public file was found
		assertTrue(fileToRetrieve != null);
		String docId = fileToRetrieve.getExtension(StringConstants.TD_UUID)
				.getText();

		// retrive file meta data
		Entry fileMetaData = (Entry) service.getFileMetaDataFeed(docId);

		// verify contains new tags
		boolean containsEntryTag = false;
		boolean containsParamTag = false;
		for (Category c : fileMetaData.getCategories()) {
			if (c.getTerm().equalsIgnoreCase("JamesBond"))
				containsEntryTag = true;
			if (c.getTerm().equalsIgnoreCase("updatedFromUrl"))
				containsParamTag = true;
		}
		// check the metadata against the input given
		if (containsParamTag
				&& containsEntryTag
				&& fileMetaData.getTitle().equals(filename)
				&& fileMetaData.getExtension(StringConstants.TD_UUID).getText()
						.equals(docId)) {
			LOGGER.debug("SUCCESS: File was successfully created");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File was not created correctly");
			assertTrue(false);
		}
		LOGGER.debug("TEST COMPLETED: Creating File Meta Data Multi-Part Put");
	}

	@Test
	public void deleteAllFilesAndFolders() {
		LOGGER.debug("BEGINNING TEST: deleteAllFilesAndFolders");

		// TJB 4/21. This vmodel call does not use anonymous access. Changed
		// expected return from 403 to 200.
		// https://lcauto48.swg.usma.ibm.com/files/basic/api/documents/feed?visibility=public&ps=10
		if (StringConstants.VMODEL_ENABLED) {
			service.getPublicFeedInDepth("&ps=10");
			assertEquals(200, service.getRespStatus());
		} else {
			Feed publicFilesFeed = (Feed) service
					.getPublicFeedInDepth("&ps=10");
			for (Entry e : publicFilesFeed.getEntries()) {
				service.deleteFile(e.getExtension(StringConstants.TD_UUID)
						.getText());
			}
		}

		// Delete my library files
		Feed myFilesFeed = (Feed) service.getMyLibraryFeed("ps=10");
		for (Entry e : myFilesFeed.getEntries()) {
			service.deleteFile(e.getExtension(StringConstants.TD_UUID)
					.getText());
		}
		// Delete public folders
		Feed publicFoldersFeed = (Feed) service.getFoldersFeed("visibility=public&ps=10");
		for (Entry e : publicFoldersFeed.getEntries()) {
			LOGGER.debug(e.getAuthor().getName());
			service.deleteFileFolder(e.getExtension(StringConstants.TD_UUID)
					.getText());

		}

		// empty trash
		service.purgeAllFilesFromTrash();
	}

	@Test
	public void deleteVersionId() {
		LOGGER.debug("BEGINNING TEST: Delete a version using the version ID");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File" + timeStamp;
		String updatefilename = "updateFile" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();

		Entry fileToRetrieve = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToRetrieve = e;
			}
		}
		// ensure that the public file was found
		assertTrue(fileToRetrieve != null);
		String docId = fileToRetrieve.getExtension(StringConstants.TD_UUID)
				.getText();

		// create new meta data to update with
		// File updatedFile = new File("updateFile.txt");
		FileEntry updatedEntry = new FileEntry(null, updatefilename,
				"The Bond Car", "JamesBond", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, null, null);
		String params = "?tag=update";

		// update file
		service.updateFileMetaDataMultiPartPut(docId, params, updatedEntry);
		assertEquals("file update", 200, service.getRespStatus());

		// get file meta data to verify update
		Entry fileMetaData = (Entry) service.getFileMetaDataFeed(docId);

		// verify contains new tags
		boolean containsEntryTag = false;
		boolean containsParamTag = false;
		for (Category c : fileMetaData.getCategories()) {
			if (c.getTerm().equalsIgnoreCase("JamesBond"))
				containsEntryTag = true;
			if (c.getTerm().equalsIgnoreCase("update"))
				containsParamTag = true;
		}
		// check the metadata against the input given
		if (containsParamTag
				&& containsEntryTag
				&& fileMetaData.getTitle().equals(updatefilename)
				&& fileMetaData.getExtension(StringConstants.TD_UUID).getText()
						.equals(docId)) {
			LOGGER.debug("SUCCESS: File was successfully updated");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File was not updated correctly");
			assertTrue(false);
		}

		Feed fileVersion = (Feed) service.getAllVersionsOfFile(docId);
		if (fileVersion.getEntries().size() != 2) {
			LOGGER.debug("Test Failed: New file version was not created");
			assertTrue(false);
		}

		Entry versionEntry = fileVersion.getEntries().get(1);
		Feed myLib = (Feed) service.getMyLibraryFeed();
		String libId = myLib.getExtension(StringConstants.TD_UUID).getText();
		String versionId = versionEntry.getExtension(StringConstants.TD_UUID)
				.getText();
		service.deleteVersionOfFile(libId, docId, versionId);

		fileVersion = (Feed) service.getAllVersionsOfFile(docId);
		if (fileVersion.getEntries().size() != 1) {
			LOGGER.debug("Test Failed: Old file version was not deleted");
			assertTrue(false);
		} else {
			LOGGER.debug("Test Successful: Old file version was deleted");
			assertTrue(true);
		}
	}

	@Test(timeOut = 120000)
	public void deleteUserVersionId() {
		LOGGER.debug("BEGINNING TEST: deleteUserVersionId");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File2" + timeStamp;
		String updatefilename = "updateFile2" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();

		Entry fileToRetrieve = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToRetrieve = e;
			}
		}
		// ensure that the public file was found
		assertTrue(fileToRetrieve != null);
		String docId = fileToRetrieve.getExtension(StringConstants.TD_UUID)
				.getText();

		// create new meta data to update with
		File updatedFile = new File("updateFile.txt");
		FileEntry updatedEntry = new FileEntry(updatedFile, updatefilename,
				"The Bond Car", "JamesBond", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, null, null);
		String params = "?tag=update";

		// update file
		service.updateFileMetaDataMultiPartPut(docId, params, updatedEntry);

		// get file meta data to verify update
		Entry fileMetaData = (Entry) service.getFileMetaDataFeed(docId);

		// verify contains new tags
		boolean containsEntryTag = false;
		boolean containsParamTag = false;
		for (Category c : fileMetaData.getCategories()) {
			if (c.getTerm().equalsIgnoreCase("JamesBond"))
				containsEntryTag = true;
			if (c.getTerm().equalsIgnoreCase("update"))
				containsParamTag = true;
		}
		// check the metadata against the input given
		if (containsParamTag
				&& containsEntryTag
				&& fileMetaData.getTitle().equals(updatefilename)
				&& fileMetaData.getExtension(StringConstants.TD_UUID).getText()
						.equals(docId)) {
			LOGGER.debug("SUCCESS: File was successfully updated");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File was not updated correctly");
			assertTrue(false);
		}

		Feed fileVersion = (Feed) service.getAllVersionsOfFile(docId);
		if (fileVersion.getEntries().size() != 2) {
			LOGGER.debug("Test Failed: New file version was not created");
			assertTrue(false);
		}

		Entry versionEntry = fileVersion.getEntries().get(1);
		String versionId = versionEntry.getExtension(StringConstants.TD_UUID)
				.getText();
		service.deleteVersionOfFileUser(docId, versionId);

		fileVersion = (Feed) service.getAllVersionsOfFile(docId);
		if (fileVersion.getEntries().size() != 1) {
			LOGGER.debug("Test Failed: Old file version was not deleted");
			assertTrue(false);
		} else {
			LOGGER.debug("Test Successful: Old file version was deleted");
			assertTrue(true);
		}
	}

	// @Test //userlib - not for impersonate
	public void deleteUserIdVersionId() throws FileNotFoundException,
			IOException {
		LOGGER.debug("BEGINNING TEST: deleteUserIdVersionId");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String filename = "File3" + timeStamp;
		String updatefilename = "updateFile3" + timeStamp;

		// Create file and find file for deletion
		createSimplePublicFile(filename);
		Feed myFilesFeed = (Feed) service.getPublicFeed();

		Entry fileToRetrieve = null;
		for (Entry e : myFilesFeed.getEntries()) {
			if (e.getTitle().equals(filename)) {
				fileToRetrieve = e;
			}
		}
		// ensure that the public file was found
		assertTrue(fileToRetrieve != null);
		String docId = fileToRetrieve.getExtension(StringConstants.TD_UUID)
				.getText();

		// create new meta data to update with
		// File updatedFile = new File("updateFile.txt");
		FileEntry updatedEntry = new FileEntry(null, updatefilename,
				"The Bond Car", "JamesBond", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, null, null);
		String params = "?tag=update";

		// update file
		service.updateFileMetaDataMultiPartPut(docId, params, updatedEntry);

		// get file meta data to verify update
		Entry fileMetaData = (Entry) service.getFileMetaDataFeed(docId);

		// verify contains new tags
		boolean containsEntryTag = false;
		boolean containsParamTag = false;
		for (Category c : fileMetaData.getCategories()) {
			if (c.getTerm().equalsIgnoreCase("JamesBond"))
				containsEntryTag = true;
			if (c.getTerm().equalsIgnoreCase("update"))
				containsParamTag = true;
		}
		// check the metadata against the input given
		if (containsParamTag
				&& containsEntryTag
				&& fileMetaData.getTitle().equals(updatefilename)
				&& fileMetaData.getExtension(StringConstants.TD_UUID).getText()
						.equals(docId)) {
			LOGGER.debug("SUCCESS: File was successfully updated");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: File was not updated correctly");
			assertTrue(false);
		}

		Feed fileVersion = (Feed) service.getAllVersionsOfFile(docId);
		if (fileVersion.getEntries().size() != 2) {
			LOGGER.debug("Test Failed: New file version was not created");
			assertTrue(false);
		}

		Entry versionEntry = fileVersion.getEntries().get(1);
		String versionId = versionEntry.getExtension(StringConstants.TD_UUID)
				.getText();

		String userId = user.getUserId();

		service.deleteVersionOfFileUserId(userId, docId, versionId);

		fileVersion = (Feed) service.getAllVersionsOfFile(docId);
		if (fileVersion.getEntries().size() != 1) {
			LOGGER.debug("Test Failed: Old file version was not deleted");
			assertTrue(false);
		} else {
			LOGGER.debug("Test Successful: Old file version was deleted");
			assertTrue(true);
		}
	}

	@Test
	public void retrieveListUsersWithLibId() throws Exception {
		LOGGER.debug("Beginning Test: Retrieve list of shared users using the library Id");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String fname = "Sparta" + timeStamp;

		// Create file and save its id
		Entry file = (Entry) createPublicSharedFile(fname, "Son of Sparta",
				"hotheaded showoff", otherUser.getUserId());
		String docId = file.getExtension(StringConstants.TD_UUID).getText();
		// get the library id
		Feed myLib = (Feed) service.getMyLibraryFeed();
		String libId = myLib.getExtension(StringConstants.TD_UUID).getText();

		String compressFile = service.getUserListLibId(libId, docId);

		// parse JSON for the name
		JSONObject jObject = new JSONObject(compressFile);
		JSONArray items = jObject.optJSONArray("items");
		JSONObject name = items.getJSONObject(0);
		String parsedName = name.optString("name");

		if (parsedName.equals(otherUser.getRealName())) {
			LOGGER.debug("Test Successful: Found list of shared users using the library id");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Did not find list of shared users using the library id");
			assertTrue(false);
		}
	}

	// @Test userlib
	public void retrieveListUsersWithUserId() throws Exception {
		LOGGER.debug("Beginning Test: Retrieve list of shared users using the user Id");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String fname = "Sparta2" + timeStamp;

		// Create file and save its id
		Entry file = (Entry) createPublicSharedFile(fname, "Son of Sparta",
				"hotheaded showoff", otherUser.getUserId());
		String docId = file.getExtension(StringConstants.TD_UUID).getText();
		String userId = user.getUserId();

		String compressFile = service.getUserListUserId(userId, docId);

		// parse JSON for the name
		JSONObject jObject = new JSONObject(compressFile);
		JSONArray items = jObject.optJSONArray("items");
		JSONObject name = items.getJSONObject(0);
		String parsedName = name.optString("name");

		if (parsedName.equals(otherUser.getRealName())) {
			LOGGER.debug("Test Successful: Found list of shared users based on current user id");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Did not find list of shared users based on current user id");
			assertTrue(false);
		}
	}

	@Test
	public void retrieveDocumentEntry() {
		LOGGER.debug("Beginning Test: Retrieve Document entry document");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String name = "Sparta3" + timeStamp;

		// Create file and save its id
		Entry file = (Entry) createPublicSharedFile(name, "Son of Sparta",
				"hotheaded showoff", otherUser.getUserId());
		String docId = file.getExtension(StringConstants.TD_UUID).getText();
		// get the library id
		Feed myLib = (Feed) service.getMyLibraryFeed();
		String libId = myLib.getExtension(StringConstants.TD_UUID).getText();

		Entry fileDoc = (Entry) service.getDocumentEntry(libId, docId);
		if (fileDoc.getTitle().equals(name)) {
			LOGGER.debug("Test Successful: Found file entry document");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Did not find the file entry document");
			assertTrue(false);
		}
	}

	/**
	 * Tests the ability to get a list of users that files have been shared with <br>
	 * This endpoint is DEPRECATED
	 * 
	 * @see /files/basic/api/myshares/users/feed
	 * @param (URL Param)is: ?pageSize=21&format=XML
	 */
	@Test
	public void getShareUsers() throws Exception {
		/**
		 * Step 1: Create a test file with SharePermissions Step 2: Post the
		 * test file Step 3: Perform a GET on the endpoint Step 4: Verify that
		 * the user that the file was shared with is present.
		 */
		LOGGER.debug("Beginning Test: getShareUsers()");
		// Because we have a mix of LDAPs and the props are not populated the
		// same for all users, i'll use different
		// users based on SC/On-Prem
		UserPerspective user = null;
		user = new UserPerspective(5, Component.FILES.toString(), useSSL);

		FilesService serviceB = user.getFilesService();

		String toCheck = "name=\"" + ProfileLoader.getProfile(2).getRealName()
				+ "\" id=\"" + ProfileLoader.getProfile(2).getUserId() + "\"";

		LOGGER.debug("Step 1: Create a test file with SharePermissions");
		File testFile = new File("Mooses.txt");
		FileEntry testFileEntry = new FileEntry(testFile, "getShareUsers_"
				+ RandomStringUtils.randomAlphanumeric(5), "getShareUsers",
				"getShareUsers", Permissions.PRIVATE, true, Notification.ON,
				Notification.ON, null, null, true, true, SharePermission.EDIT,
				null, ProfileLoader.getProfile(2).getUserId());

		LOGGER.debug("Step 2: Post the test file");
		serviceB.createFileNoInputStream(testFileEntry); // File is posted.

		LOGGER.debug("Step 3: Perform a GET on the endpoint");
		ExtensibleElement shareFeed = serviceB.getUrlFeed(serviceB
				.getURLString()
				+ "/basic/api/myshares/users/feed?pageSize=21&format=XML");
		assertEquals("get myshares feed-xml ", 200, serviceB.getRespStatus());

		LOGGER.debug("Step 4: Verify that the user that the file was shared with is present.");
		assertEquals("Data verification failed.", true, shareFeed.toString()
				.contains(toCheck));

		LOGGER.debug("Test Completed");
	}

	// @Test
	// public void postCMISFiles(){
	// //TODO clear out files first since test won't work properly if file
	// already there (won't add it)
	// /* Test GET CMIS files
	// * Step 1: Retrieve the repositoryId from service document
	// * Step 2: Create file
	// * Step 3: Post the file to the CMIS Feed
	// * Step 4: Get the CMIS File and verify it is the one added
	// */
	//
	// LOGGER.debug("Beginning POST CMIS files test");
	//
	// String mCMIS_URL= service.getURLString() + URLConstants.FILES_CMIS;
	// String randString=RandomStringUtils.randomAlphanumeric(10);
	// String fileName="CMIS_Post_Test-"+randString;
	// String filesEnding="/folderc/snx%3Afiles"; //list of all docs
	// String repositoryId; //found in service document
	//
	// LOGGER.debug("Step 1/4: Retrieve the repositoryId from the service document");
	// ExtensibleElement serviceDoc=service.getUrlFeed(mCMIS_URL +
	// URLConstants.FILES_SERVICE_DOCUMENT);
	// //find the id now that you have the service doc
	// int indexOfRepositoryId =
	// serviceDoc.toString().indexOf(("repositoryId"));
	// int indexOfFirstChar = serviceDoc.toString().indexOf(">",
	// indexOfRepositoryId)+1;
	// int indexOfLastChar = serviceDoc.toString().indexOf("<",
	// indexOfFirstChar);
	// repositoryId = serviceDoc.toString().substring(indexOfFirstChar,
	// indexOfLastChar);
	//
	// LOGGER.debug("Step 2/4: Create file");
	// File testFile = new File("text.txt");
	// FileEntry testFileEntry = new FileEntry(testFile, fileName,
	// "Details about this file", "TagTheTag", Permissions.PUBLIC, true,
	// Notification.ON, Notification.ON, null, null, true, true,
	// SharePermission.EDIT, null, null);
	// ExtensibleElement fileMade = null;
	// try {
	// fileMade=service.createFile(testFileEntry);
	// }catch(Exception e){}
	//
	// //finish building URL to the CMIS Repo
	// mCMIS_URL+= "/repository/" + repositoryId;
	//
	// LOGGER.debug("Step 3/4: Post the file to the CMIS Feed");
	// //This causes a "SEVERE: POST ERROR - 400: Bad Request" message to pop up
	// BUT IT IS STILL POSTED
	// ExtensibleElement mURL_Added =service.postUrlFeed(mCMIS_URL+filesEnding,
	// fileMade);
	// // try {service.doHttpPost(mCMIS_URL, "data");} catch (Exception e) {}
	//
	// LOGGER.debug("Step 4/4: Get the CMIS File and verify it is the one added");
	// Feed updatedFeed = (Feed)service.getUrlFeed(mCMIS_URL + filesEnding);
	// Entry entry = null;
	// try{
	// entry=updatedFeed.getEntries().get(0);
	// }catch(Exception e){
	// LOGGER.debug("POST was not made - feed is empty");
	// assertTrue(false);
	// }
	// if (entry.getTitle().equals(fileName))
	// assertTrue(true);
	// LOGGER.debug("Ending POST CMIS Files test");
	// }

	// @Test
	// public void deleteCMISFiles(){
	// //TODO: add file first/add in code to add file (getting error on post
	// method atm)
	// /* Test: Delete CMIS Files
	// * Step 1: Get the repositoryId from the service document
	// * Step 2: Get the feed of the objects in the CMIS feed
	// * Step 3: Get the CMIS object(s)
	// * Step 4: Remove the CMIS object(s)
	// */
	//
	// String mCMIS_URL= service.getURLString() + URLConstants.FILES_CMIS;
	// String repositoryId;
	// String fileFeedEnding="/folderc/snx%3Afiles"; //list of all docs
	//
	// LOGGER.debug("Step 1/4: Retrieve the repositoryId from the service document");
	// ExtensibleElement serviceDoc=service.getUrlFeed(mCMIS_URL +
	// URLConstants.FILES_SERVICE_DOCUMENT);
	//
	// //find the id now that you have the service doc
	// int indexOfRepositoryId =
	// serviceDoc.toString().indexOf(("repositoryId"));
	// int indexOfFirstChar = serviceDoc.toString().indexOf(">",
	// indexOfRepositoryId)+1;
	// int indexOfLastChar = serviceDoc.toString().indexOf("<",
	// indexOfFirstChar);
	// repositoryId = serviceDoc.toString().substring(indexOfFirstChar,
	// indexOfLastChar);
	//
	// mCMIS_URL += "/repository/" + repositoryId;
	//
	// LOGGER.debug("Step 2: Get the feed of the objects in the CMIS feed");
	// Feed feedOfObjects = (Feed) service.getUrlFeed((mCMIS_URL +
	// fileFeedEnding));
	// List listOfObjectsToDelete=((Feed) feedOfObjects).getEntries();
	//
	// LOGGER.debug("Step 3: Get the CMIS object to delete");
	// if (listOfObjectsToDelete.size() > 0){
	// Entry objectToDelete=(Entry) listOfObjectsToDelete.get(0);
	// String linkToObject =
	// objectToDelete.getSelfLinkResolvedHref().toString();
	//
	// LOGGER.debug("Step 4: Remove the CMIS object(s)");
	// service.deleteCMISFile(linkToObject);
	// assertTrue(true);
	// }else{
	// LOGGER.debug("Error: Nothing found to delete, test failed");
	// assertTrue(false);
	// }
	// LOGGER.debug("Ending \"Delete CMIS Files\" test.");
	// }

	@Test
	public void getOtherUserInfo() throws FileNotFoundException, IOException {
		/*
		 * Tests endpoint: /basic/api/userlibrary/{userid} Step 1.... Get the
		 * feed using the endpoint Step 2.... Verify if feed is retrieved by
		 * checking for a user id.
		 */
		LOGGER.debug("Beginning Test: getOtherUserInfo()");
		ProfileData user = ProfileLoader.getProfile(2);// user identification
		LOGGER.debug("getting feed with endpoint");
		ExtensibleElement endPoint = service.getUrlFeed(URLConstants.SERVER_URL
				+ "/files" + URLConstants.FILES_PERSON_LIBRARY_AUTH
				+ user.getUserId() + "/entry/");
		LOGGER.debug("verifying feed by checking if User Id is contained");
		assertEquals(true,
				endPoint.toString()
						.contains(user.getUserId() + "</snx:userid>"));
	}

	@Test
	public void getWebUrl() throws FileNotFoundException, IOException {
		/*
		 * Test endpoint: /basic/api/library/{library-id}/entryStep 1... Get
		 * library-idStep 2... execute endpoint, verify
		 */
		LOGGER.debug("Beginning Test: getWebUrl");
		// ProfileData usr = ProfileLoader.getAdminProfile(); //run as Amy Jones
		// 1 since 242 isn't used.
		ProfileData usr = ProfileLoader.getCurrentProfile();

		String userId = usr.getUserId();

		LOGGER.debug("Step 1... Get library-id");
		String url2 = service.getURLString()
				+ URLConstants.FILES_COMMENTS_ACCESS + "/" + userId + "/feed";
		Feed userLib = (Feed) service.getUrlFeed(url2);
		String ft = userLib.toString();
		int a = ft.indexOf("rel=\"self\"");
		String libId = ft.substring(a - 43, a - 7);

		LOGGER.debug("Step 2...execute endpoint, verify");
		ExtensibleElement targetUrl = service
				.getUrlFeed(URLConstants.SERVER_URL + "/files"
						+ URLConstants.FILES_LIBRARY + libId + "/entry/");
		assertTrue(targetUrl.toString().contains(libId));

	}

	@Test
	public void getMyShares() throws FileNotFoundException, IOException {
		/*
		 * Test the ability to get a feed your shares, both inbound (shared with
		 * you) and outbound (files you shared) Step 1: Create FilesPerspective
		 * users user1 and user2 Step 2: As user1, share a file with user2 Step
		 * 3: As user2, share a file with user1 Step 4: As user1, get feed of
		 * inbound shares, verify it contains the file shared by user2 Step 5:
		 * As user1, get feed of outbound shares, verify it contains the file
		 * shared with user2
		 */
		LOGGER.debug("BEGINNING TEST: Get My Shares");
		String rand1 = RandomStringUtils.randomAlphanumeric(10);
		String rand2 = RandomStringUtils.randomAlphanumeric(10);

		LOGGER.debug("Step 1... Create FilesPerspective users 1 and 2");
		UserPerspective user1=null,user2=null;
		try {
			user1 = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
			user2 = new UserPerspective(5,
					Component.FILES.toString(), useSSL);

		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		LOGGER.debug("Step 2... As user1, share a file with user2");
		// Create file entry object with share permission for user2
		// File testFile1 = new
		// File(this.getClass().getResource("/resources/fish.txt").getFile());
		FileEntry testFileSharedByMe = new FileEntry(null, "TestFileShare_"
				+ rand1 + ".txt", "I want to be shared.", "",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, null,
				user2.getUserId());
		// Upload the shared file
		user1.getFilesService().createFile(testFileSharedByMe);

		LOGGER.debug("Step 3... As user2, share a file with user1");
		// Create file entry object with share permission for user1
		// File testFile2 = new
		// File(this.getClass().getResource("/resources/fish.txt").getFile());
		FileEntry testFileSharedWithMe = new FileEntry(null, "TestFileShare_"
				+ rand2 + ".txt", "I want to be shared.", "",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, null,
				user1.getUserId());
		// Upload the shared file
		user2.getFilesService().createFile(testFileSharedWithMe);

		LOGGER.debug("Step 4... As user1, get feed of inbound shares, verify it contains the file shared by user2");
		Feed mySharesInbound = (Feed) user1.getFilesService().getMySharesFeed(
				"inbound"); // Get my shares feed
		boolean foundFile = false;
		for (Entry e : mySharesInbound.getEntries())
			if (e.getTitle().contains(testFileSharedWithMe.getTitle()))
				foundFile = true;
		assertEquals("Feed should contain shared file", true, foundFile);

		LOGGER.debug("Step 5... As user1, get feed of outbound shares, verify it contains the file shared with user2");
		Feed mySharesOutbound = (Feed) user1.getFilesService().getMySharesFeed(
				"outbound"); // Get my shares feed
		foundFile = false;
		for (Entry e : mySharesOutbound.getEntries())
			if (e.getTitle().contains(testFileSharedByMe.getTitle()))
				foundFile = true;
		assertEquals("Feed should contain shared file", true, foundFile);

		LOGGER.debug("ENDING TEST: Get My Shares");
	}

	@Test
	public void getLibraryFeed() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to get a feed of files in a library. Step 1: Create
		 * files perspective user Step 2: Upload a file Step 3: Get
		 * myuserlibrary feed, extract the library ID Step 4: Get library feed,
		 * verify it contains the file uploaded
		 */
		LOGGER.debug("BEGINNING TEST: Get Library feed");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry = new FileEntry(testFile, "TestFile" + rand
				+ ".jpg", "My file description.", "", Permissions.PRIVATE,
				true, Notification.ON, Notification.ON, null, null, true, true,
				null, null, null);
		user.getFilesService().createFile(testFileEntry);

		LOGGER.debug("Step 3... Get myuserlibrary feed, extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Get library feed, verify it contains the file uploaded");
		Feed libraryFeed = (Feed) user.getFilesService().getLibraryFeed(
				libraryID);
		boolean foundFile = false;
		for (Entry e : libraryFeed.getEntries())
			if (e.getTitle().equals(testFileEntry.getTitle()))
				foundFile = true;
		assertEquals("Feed should contain the uploaded file", true, foundFile);

		LOGGER.debug("ENDING TEST: Get Library feed");
	}

	@Test
	public void deleteLibraryFile() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to delete a file in a library Step 1: Create files
		 * perspective user Step 2: Upload a file Step 3: Get myuserlibrary
		 * feed, extract the library ID Step 4: Delete the file from the library
		 * Step 5: Get library feed, verify it does not contain the deleted file
		 */
		LOGGER.debug("BEGINNING TEST: Delete Library File");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry = new FileEntry(testFile, "TestFileDel" + rand
				+ ".jpg", "My file description.", "", Permissions.PRIVATE,
				true, Notification.ON, Notification.ON, null, null, true, true,
				null, null, null);
		Entry response = (Entry) user.getFilesService().createFile(
				testFileEntry);

		LOGGER.debug("Step 3... Get myuserlibrary feed, extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Delete the file from the library");
		String documentID = response.getId().toString().split(":td:")[1]; // Get
		// the
		// document
		// ID
		// of
		// the
		// file
		assertTrue(user.getFilesService().deleteLibraryFile(libraryID,
				documentID)); // Delete file, assert success

		LOGGER.debug("Step 5... Get library feed, verify it does not contain the deleted file");
		Feed libraryFeed = (Feed) user.getFilesService().getLibraryFeed(
				libraryID);
		boolean foundFile = false;
		for (Entry e : libraryFeed.getEntries())
			if (e.getTitle().equals(testFileEntry.getTitle()))
				foundFile = true;
		assertEquals("Feed should not contain deleted file", false, foundFile);

		LOGGER.debug("ENDING TEST: Delete Library File");
	}

	@Test
	public void purgeFileFromLibraryTrash() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to purge a file from a library's trash Step 1:
		 * Create files perspective user Step 2: Upload a file Step 3: Get
		 * myuserlibrary feed, extract the library ID Step 4: Delete the file
		 * from the library Step 5: Get library trash feed, verify it contains
		 * the deleted file Step 6: Purge the file from trash Step 7: Get the
		 * library trash feed, verify it does not contain the purged file
		 */
		LOGGER.debug("BEGINNING TEST: Purge File From Library Trash");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry = new FileEntry(testFile, "TestFilePurge"
				+ rand + ".jpg", "My file description.", "",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, null, null, null);
		Entry response = (Entry) user.getFilesService().createFile(
				testFileEntry);

		LOGGER.debug("Step 3... Get myuserlibrary feed, extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Delete the file from the library");
		String documentID = response.getId().toString().split(":td:")[1]; // Get
		// the
		// document
		// ID
		// of
		// the
		// file
		assertTrue(user.getFilesService().deleteLibraryFile(libraryID,
				documentID)); // Delete file, assert success

		LOGGER.debug("Step 5... Get library trash feed, verify it contains the deleted file");
		Feed libraryTrashFeed = (Feed) user.getFilesService()
				.getLibraryTrashFeed(libraryID);
		boolean foundFile = false;
		for (Entry e : libraryTrashFeed.getEntries())
			if (e.getTitle().equals(testFileEntry.getTitle()))
				foundFile = true;
		assertEquals("Feed should contain deleted file", true, foundFile);

		LOGGER.debug("Step 6... Purge the file from trash");
		assertTrue(user.getFilesService().purgeFileFromLibraryTrash(libraryID,
				documentID)); // purge file, assert success

		LOGGER.debug("Step 7... Get the library trash feed, verify it does not contain the purged file");
		libraryTrashFeed = (Feed) user.getFilesService().getLibraryTrashFeed(
				libraryID);
		foundFile = false;
		for (Entry e : libraryTrashFeed.getEntries())
			if (e.getTitle().equals(testFileEntry.getTitle()))
				foundFile = true;
		assertEquals("Feed should not contain purged file", false, foundFile);

		LOGGER.debug("ENDING TEST: Purge File From Library Trash");
	}

	@Test
	public void purgeAllFilesFromLibraryTrash() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to purge all files from a library's trash Step 1:
		 * Create files perspective user Step 2: Upload two files Step 3: Get
		 * myuserlibrary feed to extract the library ID Step 4: Delete the files
		 * from the library Step 5: Get library trash feed, verify it contains
		 * the deleted files Step 6: Purge all files from trash Step 7: Get the
		 * library trash feed, verify it does not contain the purged files
		 */
		LOGGER.debug("BEGINNING TEST: Purge All Files From Library Trash");
		String rand1 = RandomStringUtils.randomAlphanumeric(5);
		String rand2 = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload two files");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry1 = new FileEntry(testFile, "TestFilePurgeAll"
				+ rand1 + ".txt", "My file description!", "",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, null, null, null);
		FileEntry testFileEntry2 = new FileEntry(testFile, "TestFilePurgeAll"
				+ rand2 + ".png", "My file description.", "",
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, null, null, null);
		// Upload the files after creating file entry objects
		Entry response1 = (Entry) user.getFilesService().createFile(
				testFileEntry1);
		Entry response2 = (Entry) user.getFilesService().createFile(
				testFileEntry2);

		LOGGER.debug("Step 3... Get myuserlibrary feed to extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Delete the files from the library");
		String documentID1 = response1.getId().toString().split(":td:")[1]; // Get
		// the
		// document
		// ID
		// of
		// file1
		String documentID2 = response2.getId().toString().split(":td:")[1]; // Get
		// the
		// document
		// ID
		// of
		// file2
		// Delete files, assert success
		assertTrue(user.getFilesService().deleteLibraryFile(libraryID,
				documentID1));
		assertTrue(user.getFilesService().deleteLibraryFile(libraryID,
				documentID2));

		LOGGER.debug("Step 5... Get library trash feed, verify it contains the deleted files");
		Feed libraryTrashFeed = (Feed) user.getFilesService()
				.getLibraryTrashFeed(libraryID);
		boolean foundFile1 = false, foundFile2 = false;
		for (Entry e : libraryTrashFeed.getEntries()) {
			if (e.getTitle().equals(testFileEntry1.getTitle()))
				foundFile1 = true;
			else if (e.getTitle().equals(testFileEntry2.getTitle()))
				foundFile2 = true;
		}
		assertEquals("Feed should contain deleted files", true, foundFile1
				&& foundFile2);

		LOGGER.debug("Step 6... Purge all files from trash");
		assertTrue(user.getFilesService().purgeAllFilesFromLibraryTrash(
				libraryID)); // purge files, assert success

		LOGGER.debug("Step 7... Get the library trash feed, verify it does not contain the purged files");
		libraryTrashFeed = (Feed) user.getFilesService().getLibraryTrashFeed(
				libraryID);
		foundFile1 = false;
		foundFile2 = false;
		for (Entry e : libraryTrashFeed.getEntries()) {
			if (e.getTitle().equals(testFileEntry1.getTitle()))
				foundFile1 = true;
			else if (e.getTitle().equals(testFileEntry2.getTitle()))
				foundFile2 = true;
		}
		assertEquals("Feed should not contain purged files", false, foundFile1
				|| foundFile2);

		LOGGER.debug("ENDING TEST: Purge All Files From Library Trash");
	}

	@Test
	public void updateLibraryFileInfo() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to update the info/metadata of a file Step 1:
		 * Create files perspective user Step 2: Upload a file Step 3: Get
		 * myuserlibrary feed to extract the library ID Step 4: Update file info
		 * Step 5: Retrieve file info to verify it was updated
		 */
		LOGGER.debug("BEGINNING TEST: Update Library file info");
		String rand1 = RandomStringUtils.randomAlphanumeric(5);
		String rand2 = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry = new FileEntry(testFile, "TestFile" + rand1
				+ ".txt", "My file description!", "", Permissions.PRIVATE,
				true, Notification.ON, Notification.ON, null, null, true, true,
				null, null, null);
		Entry response = (Entry) user.getFilesService().createFile(
				testFileEntry);

		LOGGER.debug("Step 3... Get myuserlibrary feed to extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Update file info");
		// Get document ID, create updated file entry, send PUT request
		String documentID = response.getId().toString().split(":td:")[1];
		FileEntry updatedTestFileEntry = new FileEntry(testFile,
				"TestFileUPDATED" + rand2 + ".txt",
				"My updated file description!", "", Permissions.PRIVATE, true,
				Notification.ON, Notification.ON, null, null, true, true, null,
				null, null);
		user.getFilesService().updateLibraryFileInfo(libraryID, documentID,
				updatedTestFileEntry);

		LOGGER.debug("Step 5... Retrieve file info to verify it was updated");
		Entry fileInfo = (Entry) user.getFilesService().getLibraryFileInfo(
				libraryID, documentID);
		assertEquals(updatedTestFileEntry.getTitle(), fileInfo.getTitle()); // Verify
		// file
		// title
		assertEquals(updatedTestFileEntry.getContent(), fileInfo.getSummary()); // Verify
		// file
		// description

		LOGGER.debug("ENDING TEST: Update Library file info");
	}

	@Test
	public void restoreLibraryFileFromTrash() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to restore a file from a library's trash Step 1:
		 * Create files perspective user Step 2: Upload a file Step 3: Get
		 * myuserlibrary feed to extract the library ID Step 4: Delete the file,
		 * assert success Step 5: Restore the file, get the library feed to
		 * verify it contains the restored file
		 */
		LOGGER.debug("BEGINNING TEST: Restore library file from trash");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry = new FileEntry(testFile, "TestFile" + rand
				+ ".txt", "My file description!", "", Permissions.PRIVATE,
				true, Notification.ON, Notification.ON, null, null, true, true,
				null, null, null);
		Entry response = (Entry) user.getFilesService().createFile(
				testFileEntry);

		LOGGER.debug("Step 3... Get myuserlibrary feed to extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Delete the file, assert success");
		String documentID = response.getId().toString().split(":td:")[1];
		assertTrue(user.getFilesService().deleteLibraryFile(libraryID,
				documentID));

		LOGGER.debug("Step 5... Restore the file, get the library feed to verify it contains the restored file");
		// First retrieve the deleted file entry
		Entry fileEntry = (Entry) user.getFilesService().getLibraryFileInTrash(
				libraryID, documentID);
		// Restore the file
		user.getFilesService().restoreLibraryFileFromTrash(libraryID,
				documentID, fileEntry);

		// Verify
		Feed libraryFeed = (Feed) user.getFilesService().getLibraryFeed(
				libraryID);
		boolean foundFile = false;
		for (Entry e : libraryFeed.getEntries())
			if (e.getTitle().equals(testFileEntry.getTitle()))
				foundFile = true;
		assertEquals("Feed should contain the restored file", true, foundFile);

		LOGGER.debug("ENDING TEST: Restore library file from trash");
	}

	@Test
	public void getLibraryTags() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to get a feed of tags of files in a certain library
		 * Step 1: Create file perspective user Step 2: Upload a file with tags
		 * Step 3: Get myuserlibrary feed to extract the library ID Step 4: Get
		 * the library's tags feed, verify it contains the tag of the uploaded
		 * file
		 */
		LOGGER.debug("BEGINNING TEST: Get Library Tags");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(4,
					Component.FILES.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file with tags");
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		String tagsString = "cooltag" + rand; // Test tag
		FileEntry testFileEntry = new FileEntry(testFile, "TestFile" + rand
				+ ".txt", "My file description!", tagsString,
				Permissions.PRIVATE, true, Notification.ON, Notification.ON,
				null, null, true, true, null, null, null);
		user.getFilesService().createFile(testFileEntry);

		LOGGER.debug("Step 3... Get myuserlibrary feed to extract the library ID");
		Feed myLibrary = (Feed) user.getFilesService().getMyLibraryFeed();
		String libraryID = myLibrary.getId().toString().split(":td:")[1];

		LOGGER.debug("Step 4... Get the library's tags feed, verify it contains the tag of the uploaded file");
		boolean foundTag = false;
		int page = 1;
		ExtensibleElement tags = user.getFilesService().getLibraryTags(
				libraryID, 50, page);
		assertEquals("get library tags ", 200, user.getFilesService()
				.getRespStatus());
		while (!foundTag) {
			if (tags.getElements().size() == 0) // To stop from going into
				// infinite loop
				break;
			for (Element e : tags.getElements()) {
				if (e.getAttributeValue("name").equalsIgnoreCase(tagsString)) {
					foundTag = true;
					break;
				}
			}
			if (foundTag)
				break;
			tags = user.getFilesService().getLibraryTags(libraryID, 50, ++page);
			assertEquals("get library tags ", 200, user.getFilesService()
					.getRespStatus());
		}

		assertEquals("Tag of uploaded file should have been found", true,
				foundTag);

		LOGGER.debug("ENDING TEST: Get Library Tags");
	}

	// @Test
	public void testFileFolder_all() {
		/*
		 * Tests file folder 
		 * Step 1: Create file folder 
		 * Step 2: Retrieve file folder, by itself, otheruser, visitor(403) 
		 * Step 3: Add files to folder 
		 * Step 4: Remove a file from folder 
		 * Step 5: Update folder name
		 * Step 6: Pinning file folder 
		 * Step 7: UnPinning file folder 
		 * Step 8: Delete file folder
		 */
		LOGGER.debug("BEGINNING TEST: File Folder");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String foldername = "FileFolder" + timeStamp;
		ExtensibleElement eEle = null;
		String folderUUID = "";

		LOGGER.debug("step 1: Create File Folder");
		eEle = createFileFolder(foldername, "file vfolder test", "folder tag",
				//Permissions.PUBLIC); 
				Permissions.PRIVATE);
		folderUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("step 2: Retrieve File Folder");
		eEle = service.retrieveFileFolder(folderUUID);
		assertEquals("Retrieve File Folder", 200, service.getRespStatus());
		assertEquals("folderUUID not match", folderUUID,
				eEle.getExtension(StringConstants.TD_UUID).getText());

		if (StringConstants.VMODEL_ENABLED) {
			eEle = visitorService.retrieveFileFolder(folderUUID);
			assertEquals("Retrieve File Folder by visitor", 403,
					visitorService.getRespStatus());

			eEle = extendedEmpService.retrieveFileFolder(folderUUID);
			assertEquals("Retrieve File Folder by extendedEmp", 403, //200,
					extendedEmpService.getRespStatus());
		}

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
		// File addFile = new
		// File(this.getClass().getResource("/resources/dogs.txt").getFile());
		String fileUUID1 = "", fileUUID2 = "";

		InputStream fin = getClass().getResourceAsStream("/resources/dogs.txt");
		// put public files in cretaed public folder
		FileEntry testFileEntry1 = new FileEntry(null, fin, "file1_"
				+ timeStamp, "Stars Agent", "STARS", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, "hi shares", null, null,
				"this is not a file body");
		FileEntry testFileEntry2 = new FileEntry(null, "file2_" + timeStamp,
				"Stars Agent", "STARS", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, "hi shares", null, null,
				"this is a file body");

		ExtensibleElement eEle1, eEle2;
		eEle1 = service.postFileToMyUserLibrary(testFileEntry1);
		fileUUID1 = ((Entry) eEle1).getExtension(StringConstants.TD_UUID)
				.getText();
		eEle2 = service.postFileToMyUserLibrary(testFileEntry2);
		fileUUID2 = ((Entry) eEle2).getExtension(StringConstants.TD_UUID)
				.getText();

		ArrayList<String> filesList = new ArrayList<String>();
		filesList.add(fileUUID1);
		filesList.add(fileUUID2);

		service.addFilesToFolder(folderUUID, filesList);
		assertEquals("Add File to Folder", 204, service.getRespStatus());

		LOGGER.debug("Verify - Get File from Folder");
		Feed feed = (Feed) service.getFilesInFolderFeed(folderUUID);
		assertEquals("Get File from Folder", 200, service.getRespStatus());
		assertEquals(" Files count", 2, feed.getEntries().size());

		LOGGER.debug("Multiple API to Retrieve List of Collections Containing File");
		Feed lib = (Feed) service.getMyLibraryFeed();
		String libUUID = lib.getExtension(StringConstants.TD_UUID).getText();
		Feed collectionsList = (Feed) service
				.getCollectionsContainingFileInSpecifiedLibrary(libUUID,
						fileUUID1);
		assertEquals("Get collectionsList1", 200, service.getRespStatus());
		assertEquals("Folder Name from lib", foldername, collectionsList
				.getEntries().get(0).getTitle());

		//
		collectionsList = (Feed) service
				.getCollectionsContainingFileMyUserLibrary(fileUUID1);
		assertEquals("Get collectionsList2", 200, service.getRespStatus());
		assertEquals("Folder Name from my lib", foldername, collectionsList
				.getEntries().get(0).getTitle());

		//
		collectionsList = (Feed) service
				.getCollectionsContainingFile(fileUUID1);
		assertEquals("Get collectionsList3", 200, service.getRespStatus());
		assertEquals("Folder Name", foldername, collectionsList.getEntries()
				.get(0).getTitle());

		//
		collectionsList = (Feed) service.getCollectionsContainingFileByUser(
				user.getUserId(), fileUUID1);
		assertEquals("Get collectionsList4", 200, service.getRespStatus());
		assertEquals("Folder Name from userId", foldername, collectionsList
				.getEntries().get(0).getTitle());

		if (StringConstants.VMODEL_ENABLED) {
			eEle = visitorService.getFilesInFolderFeed(folderUUID);
			assertEquals("Retrieve Files by visitor", 403,
					visitorService.getRespStatus());
			eEle = extendedEmpService.getFilesInFolderFeed(folderUUID);
			assertEquals("Retrieve Files by extendedEmp", 403, //200,
					extendedEmpService.getRespStatus());

			eEle = visitorService.getFoldersFeed("ps=100");
			assertEquals("Retrieve File by visitor", 200,
					visitorService.getRespStatus());
			assertEquals("Retrieve File by visitor", 0, ((Feed) eEle)
					.getEntries().size());
			eEle = extendedEmpService.getFoldersFeed("ps=100");
			assertEquals("Retrieve File Folder by extendedEmp", 200,
					extendedEmpService.getRespStatus());
			System.out.println(((Feed) eEle).getEntries().size());

			eEle = visitorService.getFilesInFolderFeed(folderUUID);
			assertEquals("Get File from Folder", 403,
					visitorService.getRespStatus());
			eEle = extendedEmpService.getFilesInFolderFeed(folderUUID);
			assertEquals("Retrieve File from Folder by extendedEmp", 403, //200,
					extendedEmpService.getRespStatus());

			LOGGER.debug("Multiple API to Retrieve List of Collections Containing File");
			eEle = visitorService
					.getCollectionsContainingFileInSpecifiedLibrary(libUUID,
							fileUUID1);
			assertEquals("Get collectionsList1", 403,
					visitorService.getRespStatus());
			eEle = extendedEmpService
					.getCollectionsContainingFileInSpecifiedLibrary(libUUID,
							fileUUID1);
			assertEquals("Get collectionsList1", 403, //200,
					extendedEmpService.getRespStatus());

			eEle = visitorService
					.getCollectionsContainingFileMyUserLibrary(fileUUID1);
			assertEquals("Get collectionsList2", 403,
					visitorService.getRespStatus());
			eEle = extendedEmpService
					.getCollectionsContainingFileMyUserLibrary(fileUUID1);
			assertEquals("Get collectionsList2", 403, //404,
					extendedEmpService.getRespStatus());

			//
			eEle = visitorService.getCollectionsContainingFile(fileUUID1);
			assertEquals("Get collectionsList3", 403,
					visitorService.getRespStatus());
			eEle = extendedEmpService.getCollectionsContainingFile(fileUUID1);
			assertEquals("Get collectionsList3", 403, //200,
					extendedEmpService.getRespStatus());

			//
			eEle = visitorService.getCollectionsContainingFileByUser(
					user.getUserId(), fileUUID1);
			assertEquals("Get collectionsList4", 403,
					visitorService.getRespStatus());
			eEle = extendedEmpService.getCollectionsContainingFileByUser(
					user.getUserId(), fileUUID1);
			assertEquals("Get collectionsList4", 403, //200,
					extendedEmpService.getRespStatus());
		}

		LOGGER.debug("step 4: Remove a File from Folder");
		assertTrue(service.removeFileFromFolder(folderUUID, fileUUID2));
		LOGGER.debug("Verify - Get File from Folder");
		feed = (Feed) service.getFilesInFolderFeed(folderUUID);
		assertEquals("Get File from Folder", 200, service.getRespStatus());
		assertEquals(" Files count", 1, feed.getEntries().size());

		LOGGER.debug("step 5: Update Folder Name");
		File updateFile = new File("Albert.txt");
		FileEntry updateFolder = new FileEntry(updateFile, "updateFolder"
				+ timeStamp, "Stars Leader", "Traitor", Permissions.PUBLIC,
				true, Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.VIEW, null, null);
		service.updateFileFolder(folderUUID, updateFolder);
		assertEquals("Update File in Folder", 200, service.getRespStatus());

		Entry folderEntry = (Entry) service.retrieveFileFolder(folderUUID);
		assertEquals("Retrieve File Folder", 200, service.getRespStatus());
		assertEquals("Folder name", "updateFolder" + timeStamp,
				folderEntry.getTitle());

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

	//@Test
	public void testFileFolderByVisitor() {
		/*
		 * Tests file folder 
		 * Step 1: Create file folder 
		 * Step 2: Retrieve file folder 
		 * Step 3: Add files to folder 
		 * Step 4: Remove a file from folder
		 * Step 5: Update folder name 
		 * Step 6: Pinning file folder 
		 * Step 7: UnPinning file folder 
		 * Step 8: Delete file folder
		 */
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("BEGINNING TEST: File Folder by Visitor "+visitor.getRealName());
			String timeStamp = Utils.logDateFormatter.format(new Date());
			String foldername = "FileFolder" + timeStamp;
			ExtensibleElement eEle = null;
			String folderUUID = "";

			LOGGER.debug("step 1: Create File Folder");
			eEle = createFileFolderByVisitor(foldername, "file vfolder test",
					"folder tag", Permissions.SHARED);
			assertEquals("Create Shared File Folder", 201,
					visitorService.getRespStatus());
			folderUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

			LOGGER.debug("step 2: Retrieve File Folder");
			eEle = visitorService.retrieveFileFolder(folderUUID);
			assertEquals("Retrieve File Folder", 200,
					visitorService.getRespStatus());
			assertEquals("folderUUID not match", folderUUID,
					eEle.getExtension(StringConstants.TD_UUID).getText());

			// from folder feed
			Feed publicFolders = (Feed) visitorService.getFoldersFeed("ps=100");
			assertEquals("Get Folders", 200, visitorService.getRespStatus());
			boolean found = false;
			for (Entry e : publicFolders.getEntries()) {
				if (e.getTitle().equals(foldername)) {
					found = true;
					Feed fileFolder = (Feed) visitorService.getUrlFeed(e
							.getLinkResolvedHref("files").toString());
					assertEquals("Folder Name", foldername,
							fileFolder.getTitle());
					break;
				}
			}
			assertEquals("Folder found", true, found);

			// retrieveSingleRoleOfCollection
			String roleEntryString = visitorService
					.retrieveSingleCollectionRole(folderUUID, "reader");
			if (roleEntryString.contains("<title type=\"text\">reader</title>")) {
				LOGGER.debug("SUCCESS: Role was successfully found");
			} else {
				LOGGER.warn("ERROR: Role was not found");
				assertTrue("reader not found", false);
			}

			// getListOfCollectionRoles
			Feed roles = (Feed) visitorService.getCollectionRoles(folderUUID);
			assertEquals("Roles's foldername", "Roles of " + foldername,
					roles.getTitle());
			assertEquals("Roles's size", 3, roles.getEntries().size());

			LOGGER.debug("step 3: Add Files to Folder");
			timeStamp = Utils.logDateFormatter.format(new Date());
			File addFile = new File(this.getClass()
					.getResource("/resources/dogs.txt").getFile());
			String fileUUID1 = "", fileUUID2 = "";

			InputStream is = getClass().getResourceAsStream(
					"/resources/dogs.txt");
			// put public files in cretaed public folder
			FileEntry testFileEntry1 = new FileEntry(addFile, is,
					"addFileStream" + timeStamp, "Stars Agent", "STARS",
					Permissions.PRIVATE, true, Notification.ON,
					Notification.ON, null, null, true, true,
					SharePermission.VIEW, "hi shares", null, null,
					"this is a file body");
			FileEntry testFileEntry2 = new FileEntry(addFile, "addFile"
					+ timeStamp, "Stars Agent", "STARS", Permissions.PRIVATE,
					true, Notification.ON, Notification.ON, null, null, true,
					true, SharePermission.VIEW, "hi shares", null, null,
					"this is a file body");

			ExtensibleElement eEle1, eEle2;
			eEle1 = visitorService.createFile(testFileEntry1);
			fileUUID1 = ((Entry) eEle1).getExtension(StringConstants.TD_UUID)
					.getText();
			eEle2 = visitorService.createFile(testFileEntry2);
			fileUUID2 = ((Entry) eEle2).getExtension(StringConstants.TD_UUID)
					.getText();

			ArrayList<String> filesList = new ArrayList<String>();
			filesList.add(fileUUID1);
			filesList.add(fileUUID2);

			visitorService.addFilesToFolder(folderUUID, filesList);
			assertEquals("Add File to Folder", 204,
					visitorService.getRespStatus());

			LOGGER.debug("Verify - Get File from Folder");
			Feed feed = (Feed) visitorService.getFilesInFolderFeed(folderUUID);
			assertEquals("Get File from Folder", 200,
					visitorService.getRespStatus());
			assertEquals(" Files count", 2, feed.getEntries().size());

			LOGGER.debug("Multiple API to Retrieve List of Collections Containing File");
			Feed lib = (Feed) visitorService.getMyLibraryFeed();
			String libUUID = lib.getExtension(StringConstants.TD_UUID)
					.getText();
			Feed collectionsList = (Feed) visitorService
					.getCollectionsContainingFileInSpecifiedLibrary(libUUID,
							fileUUID1);
			assertEquals("Get collectionsList1", 200,
					visitorService.getRespStatus());
			assertEquals("Folder Name from lib", foldername, collectionsList
					.getEntries().get(0).getTitle());

			//
			collectionsList = (Feed) visitorService
					.getCollectionsContainingFileMyUserLibrary(fileUUID1);
			assertEquals("Get collectionsList2", 200,
					visitorService.getRespStatus());
			assertEquals("Folder Name from my lib", foldername, collectionsList
					.getEntries().get(0).getTitle());

			//
			collectionsList = (Feed) visitorService
					.getCollectionsContainingFile(fileUUID1);
			assertEquals("Get collectionsList3", 200,
					visitorService.getRespStatus());
			assertEquals("Folder Name", foldername, collectionsList
					.getEntries().get(0).getTitle());

			//
			collectionsList = (Feed) visitorService
					.getCollectionsContainingFileByUser(visitor.getUserId(),
							fileUUID1);
			assertEquals("Get collectionsList4", 200,
					visitorService.getRespStatus());
			assertEquals("Folder Name from userId", foldername, collectionsList
					.getEntries().get(0).getTitle());

			LOGGER.debug("step 4: Remove a File from Folder");
			assertTrue(visitorService.removeFileFromFolder(folderUUID,
					fileUUID2));
			LOGGER.debug("Verify - Get File from Folder");
			feed = (Feed) visitorService.getFilesInFolderFeed(folderUUID);
			assertEquals("Get File from Folder", 200,
					visitorService.getRespStatus());
			assertEquals(" Files count", 1, feed.getEntries().size());

			LOGGER.debug("step 5: Update Folder Name");
			File updateFile = new File("Albert.txt");
			FileEntry updateFolder = new FileEntry(updateFile, "updateFolder"
					+ timeStamp, "Stars Leader", "Traitor", Permissions.PUBLIC,
					true, Notification.ON, Notification.ON, null, null, true,
					true, SharePermission.VIEW, null, null);
			visitorService.updateFileFolder(folderUUID, updateFolder);
			assertEquals("Update File in Folder", 200,
					visitorService.getRespStatus());

			Entry folderEntry = (Entry) visitorService
					.retrieveFileFolder(folderUUID);
			assertEquals("Retrieve File Folder", 200,
					visitorService.getRespStatus());
			assertEquals("Folder name", "updateFolder" + timeStamp,
					folderEntry.getTitle());

			LOGGER.debug("Step 6: Pinning file folder");
			visitorService.pinningFolder(folderUUID, folderEntry);
			assertEquals("Pinning File Folder", 204,
					visitorService.getRespStatus());

			Feed myPinnedFolders = (Feed) visitorService
					.getMyPinnedFoldersFeed();
			int folders = myPinnedFolders.getEntries().size();

			LOGGER.debug("Step 7: UnPinning file folder");
			assertTrue("UnPinning file Folder failed",
					visitorService.unPinningFolder(folderUUID));

			myPinnedFolders = (Feed) visitorService.getMyPinnedFoldersFeed();
			assertEquals("Pinned File Folder count", folders - 1,
					myPinnedFolders.getEntries().size());

			LOGGER.debug("Step 8: Delete file folder");
			assertTrue("Delete file folder failed",
					visitorService.deleteFileFolder(folderUUID));
			visitorService.retrieveFileFolder(folderUUID);
			assertEquals("File folder should not found ", 404,
					visitorService.getRespStatus());
			LOGGER.debug("404: expected: File Folder not found");

			LOGGER.debug("END TEST: File Folder by Visitor");
		}
	}

	@Test
	public void testCompressFile() {
		/*
		 * Tests file folder Step 1: Create file folder 
		 * Step 2: Retrieve file folder 
		 * Step 3: Compress File test ( currently all failed ), but not reported TODO
		 * 
		 * Step 8: Delete file folder
		 */
		LOGGER.debug("BEGINNING TEST: CompressFile ");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String foldername = "CompressFileFolder" + timeStamp;
		String filename = "CompressFile" + timeStamp;
		ExtensibleElement eEle = null;
		String folderUUID = "";

		LOGGER.debug("step 1: Create File Folder");
		eEle = createFileFolder(foldername, "file vfolder test", "folder tag",
				Permissions.PUBLIC);
		folderUUID = eEle.getExtension(StringConstants.TD_UUID).getText();

		LOGGER.debug("step 2: Retrieve File Folder");
		eEle = service.retrieveFileFolder(folderUUID);
		assertEquals("Retrieve File Folder", 200, service.getRespStatus());
		assertEquals("folderUUID not match", folderUUID,
				eEle.getExtension(StringConstants.TD_UUID).getText());

		// Create test file
		String titlePrefix = "testCompressFile_";
		String title1 = titlePrefix + UUID.randomUUID().toString();
		createTestFile(title1, false);
		
		String title2 = titlePrefix + UUID.randomUUID().toString();
		createTestFile(title2, false);

		LOGGER.debug("step 3: Compress File Test...Below is just copy/paster from previous test");
		LOGGER.debug("Beginning Test: Return compressed file that includes library files in a folder");
		Entry compressFile = (Entry) service
				.filesInLibCompressFolder(folderUUID);
		int stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		int stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd)
						.equals("MyFolderFiles")) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		LOGGER.debug("Beginning Test: Return a compressed file which contains the specified files");
		compressFile = (Entry) service.filesInLibCompressFolder(folderUUID);
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd)
						.equals("MyFolderFiles")) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		LOGGER.debug("Beginning Test: Return compressed file that includes library files in a folder");
		compressFile = (Entry) service.filesInLibCompressFolder(folderUUID,
				filename);
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd).equals(filename)) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		LOGGER.debug("Beginning Test: Return a compressed file which contains the specified files");
		compressFile = (Entry) service.filesInLibCompressFolder(folderUUID,
				filename);
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd).equals(filename)) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		//
		LOGGER.debug("Beginning test: Return a compressed file with files shared with me");
		/*
		 * createSharedFileAndUpdateCoolImageOnFile(); compressFile = (Entry)
		 * service.sharedByMeCompress(); stringBeginning =
		 * compressFile.getExtensions().get(6).getText().lastIndexOf("'") + 1;
		 * stringEnd =
		 * compressFile.getExtensions().get(6).getText().lastIndexOf(".");
		 * 
		 * if(compressFile.getExtensions().get(8).getText().equals("application/zip"
		 * ) &&
		 * compressFile.getExtensions().get(6).getText().substring(stringBeginning
		 * , stringEnd).equals("SharedWithMeFiles")){
		 * LOGGER.debug("Test Successful: Found Compressed file"); } else{
		 * LOGGER.debug("Test Failed: Could not find the compressed file"); }
		 */
		//
		LOGGER.debug("Beginning Test: Return compressed file that library files; Name the compressed file");
		compressFile = (Entry) service.sharedByMeCompress(filename);
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd).equals(filename)) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		//
		LOGGER.debug("Beginning Test: Return compressed file that includes library files");
		Feed myLib = (Feed) service.getMyLibraryFeed();
		String libId = myLib.getExtension(StringConstants.TD_UUID).getText();

		compressFile = (Entry) service.filesInLibCompressLibId(libId, null, titlePrefix);
		if(service.getRespStatus() == 403 && compressFile.getExtensions().size() > 2){
			//download files again
			String downloadAnywayUrl = compressFile.getExtensions().get(2).getText();			
			compressFile = (Entry) service.getMyFeed(downloadAnywayUrl + "&title=" + titlePrefix);
		}
		assertEquals("get compressed file ", 200, service.getRespStatus());
		
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd)
						.equals("MyLibraryFiles")) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}
		//
		LOGGER.debug("Beginning Test: Return compressed file that library files; Name the compressed file");
		myLib = (Feed) service.getMyLibraryFeed();
		libId = myLib.getExtension(StringConstants.TD_UUID).getText();

		compressFile = (Entry) service.filesInLibCompressLibId(libId, filename, titlePrefix);
		if(service.getRespStatus() == 403 && compressFile.getExtensions().size() > 2){
			//download files again
			String downloadAnywayUrl = compressFile.getExtensions().get(2).getText();
			compressFile = (Entry) service.getMyFeed(downloadAnywayUrl);
		}
		assertEquals("get compressed file ", 200, service.getRespStatus());
		
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd).equals(filename)) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		//
		LOGGER.debug("Beginning Test: Return compressed file that library files through myuserlibrary; Name the compressed file");
		compressFile = (Entry) service.filesInLibCompressMyUserLib(filename, titlePrefix);
		if(service.getRespStatus() == 403 && compressFile.getExtensions().size() > 2){
			//download files again
			String downloadAnywayUrl = compressFile.getExtensions().get(2).getText();
			compressFile = (Entry) service.getMyFeed(downloadAnywayUrl);
		}
		assertEquals("get compressed file ", 200, service.getRespStatus());
		
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd).equals(filename)) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		//
		LOGGER.debug("Beginning Test: Return compressed file that library files use UserId; Name the compressed file");
		compressFile = (Entry) service.filesInLibCompressUserId(
				user.getUserId(), filename, titlePrefix);
		if(service.getRespStatus() == 403 && compressFile.getExtensions().size() > 2){
			//download files again
			String downloadAnywayUrl = compressFile.getExtensions().get(2).getText();
			compressFile = (Entry) service.getMyFeed(downloadAnywayUrl);
		}
		assertEquals("get compressed file ", 200, service.getRespStatus());
		
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd).equals(filename)) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		//
		LOGGER.debug("Beginning Test: Return compressed file that includes library files use UserId");
		compressFile = (Entry) service.filesInLibCompressUserId(user.getUserId(), titlePrefix);
		if(service.getRespStatus() == 403 && compressFile.getExtensions().size() > 2){
			//download files again
			String downloadAnywayUrl = compressFile.getExtensions().get(2).getText();
			compressFile = (Entry) service.getMyFeed(downloadAnywayUrl);
		}
		assertEquals("get compressed file ", 200, service.getRespStatus());
		
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd)
						.equals("MyLibraryFiles")) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}
		//
		LOGGER.debug("Beginning Test: Return compressed file that includes library files through myuserlibrary");
		compressFile = (Entry) service.filesInLibCompressMyUserLib(titlePrefix);
		if(service.getRespStatus() == 403 && compressFile.getExtensions().size() > 2){
			//download files again
			String downloadAnywayUrl = compressFile.getExtensions().get(2).getText();
			compressFile = (Entry) service.getMyFeed(downloadAnywayUrl);
		}
		assertEquals("get compressed file ", 200, service.getRespStatus());
		
		stringBeginning = compressFile.getExtensions().get(6).getText()
				.lastIndexOf("'") + 1;
		stringEnd = compressFile.getExtensions().get(6).getText()
				.lastIndexOf(".");

		if (compressFile.getExtensions().get(8).getText()
				.equals("application/zip")
				&& compressFile.getExtensions().get(6).getText()
						.substring(stringBeginning, stringEnd)
						.equals("MyLibraryFiles")) {
			LOGGER.debug("Test Successful: Found Compressed file");
		} else {
			LOGGER.debug("Test Failed: Could not find the compressed file");
		}

		LOGGER.debug("END TEST: Compress File ");
	}

	@Test
	public void uploadImage() throws FileNotFoundException, IOException {
		/*
		 * Tests uploading of actual file content by POSTing an InputStream Step
		 * 1: Create files perspective user Step 2: Upload a file Step 3: Get
		 * feed of my files, verify it contains the file title Step 4: Verify
		 * the file's size is greater than 0 bytes
		 */
		LOGGER.debug("BEGINNING TEST: Upload image");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create files perspective user");
		UserPerspective user=null;
		try {
			user = new UserPerspective(2,
					Component.FILES.toString());
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		LOGGER.debug("Step 2... Upload a file");
		InputStream is = this.getClass().getResourceAsStream(
				"/resources/IBM_logo.jpg");
		FileEntry testFileEntry = new FileEntry(null, is, "UploadFileTest_"
				+ rand + ".jpg", "Desciption", "", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, false, true,
				null, null, null);
		user.getFilesService().uploadFile(testFileEntry);

		LOGGER.debug("Step 3... Get feed of my files, verify it contains the file");
		Feed myLibraryFeed = (Feed) user.getFilesService().getMyLibraryFeed();
		boolean foundFile = false;
		Entry fileEntry = null;
		for (Entry e : myLibraryFeed.getEntries()) {
			if (e.getTitle().equals(testFileEntry.getTitle())) {
				foundFile = true;
				fileEntry = e;
			}
		}
		assertEquals("File should be found in myLibraryFeed", true, foundFile);

		LOGGER.debug("Step 4... From the retrieved entry for the file, verify the file size is greater than 0 bytes");
		int fileSize = Integer.parseInt(fileEntry.getExtension(
				StringConstants.TD_MEDIA_SIZE).getText());
		assertEquals("File size should be greater than 0", true, fileSize > 0);

		LOGGER.debug("ENDING TEST: Upload image");
	}

	@Test
	public void postFileWithSpecialHeaders() throws FileNotFoundException,
			IOException {
		/*
		 * Test for RTC story 119328 - allowing non-browser API access Step 0:
		 * Create user perspective, create FileEntry object Step 1: POST file w/
		 * 'origin' header set to the test server domain, verify 201 status Step
		 * 2: POST file w/ 'origin' header set to a different domain, verify 403
		 * status Step 3: POST file w/ 'origin' header set to a different domain
		 * AND 'X-Update-Nonce' header, verify 201 status Step 4: POST file w/
		 * 'X-Update-Nonce' header, verify 201 status
		 */
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) { // Change
																			// is
																			// not
																			// on
																			// SC
																			// yet
			LOGGER.debug("BEGINNING TEST... Post with special headers for RTC 119328");
			LOGGER.debug("Step 0... Create user perspective, create FileEntry object");
			UserPerspective user=null;
			try {
				user = new UserPerspective(2,
						Component.FILES.toString());
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FilesService userService = user.getFilesService();

			String rand = RandomStringUtils.randomAlphanumeric(5);
			InputStream is = this.getClass().getResourceAsStream(
					"/resources/IBM_logo.jpg");
			FileEntry testFileEntry = new FileEntry(null, is,
					"POSTFile-special_headers_" + rand + ".jpg", "Desciption",
					"", Permissions.PUBLIC, true, Notification.ON,
					Notification.ON, null, null, false, true, null, null, null);

			LOGGER.debug("Step 1... POST file w/ 'origin' header set to the test server domain, verify 201 status");
			userService.addRequestOption("origin", URLConstants.SERVER_URL);
			userService.uploadFile(testFileEntry);
			assertEquals("Should return 201 status", 201,
					userService.getRespStatus());
			System.out.println(userService.getRespStatus());

			LOGGER.debug("Step 2... POST file w/ 'origin' header set to a different domain, verify 403 status");
			testFileEntry.setTitle("newTitle_" + rand + ".jpg"); // change title
			// to avoid
			// 409 error
			userService.addRequestOption("origin", "http://www.npr.org");
			userService.uploadFile(testFileEntry);
			assertEquals("Should return 403 status", 403,
					userService.getRespStatus());
			System.out.println(userService.getRespStatus());

			LOGGER.debug("Step 3... POST file w/ 'origin' header set to a different domain AND 'X-Update-Nonce' header, verify 201 status");
			testFileEntry.setTitle("newTitle2_" + rand + ".jpg"); // change
			// title to
			// avoid 409
			// error
			userService.addRequestOption("origin", "http://www.npr.org");
			userService.addRequestOption("X-Update-Nonce", "any_value");
			userService.uploadFile(testFileEntry);
			assertEquals("Should return 201 status", 201,
					userService.getRespStatus());
			System.out.println(userService.getRespStatus());

			LOGGER.debug("Step 4... POST file w/ 'X-Update-Nonce' header, verify 201 status");
			testFileEntry.setTitle("newTitle3_" + rand + ".jpg"); // change
			// title to
			// avoid 409
			// error
			userService.addRequestOption("X-Update-Nonce", "any_value");
			userService.uploadFile(testFileEntry);
			assertEquals("Should return 201 status", 201,
					userService.getRespStatus());
			System.out.println(userService.getRespStatus());

			LOGGER.debug("ENDING TEST... Post with special headers for RTC 119328");
		}
	}

	@Test
	// for 58381: File name should be normalized for zip download
	//
	//
	public void createFileWithInvalidChars() {
		LOGGER.debug("BEGINNING TEST: Create File With Invalid Characters");
		String timestamp = Utils.logDateFormatter.format(new Date());
		String invalidString = "*|\\:\"<>?/";

		ExtensibleElement eEle = createTestFile(invalidString + timestamp,
				false);
		String fileLabel = eEle.getExtension(StringConstants.TD_LABEL)
				.getText();

		if (fileLabel.equals("_________" + timestamp)) {
			assertTrue(true);
			LOGGER.debug("Test Passed: Create File With Invalid Characters");
			service.deleteFile(eEle.getExtension(StringConstants.TD_UUID)
					.getText());
		} else {
			LOGGER.debug("Test Failed: Create File With Invalid Characters");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: Create File With Invalid Characters");
	}
	
	@Test
	/* RTC 168686: File Folders API Query exposes any users File names and Org data
	 * This test is for SmartCoud only - impersonation and non-impersonation
	 * Steps:
	 *	1) Org1 user A creates a public folder "folder_1".
	 *	2) Org1 user B executes API files/basic/api/collections/feed?creator=[user A's userid].
	 *	   Feed entry "folder_1" will be in the feed.
	 *	3) User C in Org2 executes the following,
	 *	   GET files/basic/api/collections/feed?creator=[user A's userid]&title=folder_1.
	 *	   Feed entry "folder_1" is NOT displayed as an entry.
	 */
	public void orgDataCheck() throws FileNotFoundException, IOException, LCServiceException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD){
			LOGGER.debug("BEGINNING TEST: RTC 168686 File Folders API Query exposes any users File names and Org data");
			FilesService otherUserService = otherUser.getFilesService();
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			
			int EXTERNAL_USER = 15;
			UserPerspective externalUser = new UserPerspective(EXTERNAL_USER,
					Component.FILES.toString(), useSSL);
			FilesService externalUserService = externalUser.getFilesService();
			
			String folderName2 = "RTC168686_OrgData_"+uniqueNameAddition;
			
			LOGGER.debug("Step 1: Create File Folder");
			FileEntry testFolder = new FileEntry(null, folderName2,
					"file folder", "folder tag", Permissions.PUBLIC, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.VIEW, null, null);
			Entry result = (Entry) impersonatedService.createFolder(testFolder);
			assertEquals("Folder create", 201, impersonatedService.getRespStatus());		

			LOGGER.debug("Step 2: As Org A User B - Get a feed of public folders");
			// GET  files/basic/api/collections/feed?creator=200082092&title=[folder created in init method.]
			boolean entryFound = false;
			Feed usrFeed = (Feed)otherUserService.getFoldersFeed("creator="+impersonatedUser.getUserId()+"&title="+folderName2);
			for (Entry ntry : usrFeed.getEntries()){
				if (ntry.getTitle().equalsIgnoreCase(folderName2)){
					entryFound = true;
				}
			}
			// Folder should be found in the Feed
			assertEquals("Entry not found",entryFound,true);
			
			LOGGER.debug("Step 3: As Org B User - Get a feed of public folders.  This should not have Org A User A's folder");
			// GET  files/basic/api/collections/feed?creator=200082092&title=[folder created in init method.]
			boolean entryFound2 = false;
			Feed usrFeed2 = (Feed)externalUserService.getFoldersFeed("creator="+impersonatedUser.getUserId()+"&title="+folderName2);
			for (Entry ntry : usrFeed2.getEntries()){
				if (ntry.getTitle().equalsIgnoreCase(folderName2)){
					entryFound2 = true;
				}
			}
			// Folder should not be in the Feed.
			assertEquals("Entry found but should not be found ",false,entryFound2);			
			
			LOGGER.debug("ENDING TEST: RTC 168686 File Folders API Query exposes any users File names and Org data");
		}
	}

}