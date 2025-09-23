package com.ibm.lconn.automation.framework.services.files.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.files.FilesTestBase;

/**
 * JUnit Tests via Connections API for Files Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class FilesPopulate extends FilesTestBase {

	private static boolean useSSL = true;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(FilesPopulate.class.getName());

	@BeforeClass
	public static void setUp() throws IOException {
		LOGGER.debug("===========================================");
		LOGGER.debug("FilesPopulate.java");
		LOGGER.debug("===========================================");
		
		LOGGER.debug("Start Initializing Files Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.CURRENT_USER,
				StringConstants.CURRENT_USER, Component.FILES.toString());
		user = userEnv.getLoginUser();
		LOGGER.debug("user service for FilesPopulate: id=" + user.getUserId() + ",email=" + user.getEmail() + ",username=" + user.getUserName());
		service = user.getFilesService();
		impersonatedUser = userEnv.getImpersonatedUser();
		LOGGER.debug("impersonatedUser for FilesPopulate: id=" + impersonatedUser.getUserId() + ",email=" + impersonatedUser.getEmail() + ",username=" + impersonatedUser.getUserName());		
		impersonatedService = impersonatedUser.getFilesService();

		try {
			otherUser = userEnv.getLoginUserEnvironment(
					StringConstants.RANDOM1_USER, Component.FILES.toString());
			if (StringConstants.VMODEL_ENABLED) {
				visitor = new UserPerspective(StringConstants.EXTERNAL_USER,
						Component.FILES.toString(), useSSL);
				visitorService = visitor.getFilesService();

				extendedEmployee = new UserPerspective(
						StringConstants.EMPLOYEE_EXTENDED_USER,
						Component.FILES.toString(), useSSL);
				extendedEmpService = extendedEmployee.getFilesService();
			}
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		init();

		LOGGER.debug("Finished Initializing Files Data Population Test");
	}

	@Test
	public void getDownloadInfoForUser() throws Exception {
		super.getDownloadInfoForUser();
	}

	@Test
	public void testUpdateCreatedModifiedTime() throws IOException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE)
			super.testUpdateCreatedModifiedTime();
	}

	@Test
	public void createSharedFileAndUpdateCoolImageOnFile() {
		super.createSharedFileAndUpdateCoolImageOnFile();
	}

	@Test
	public void createSharedFileAndUpdateCoolImageOnFileByVisitor() {
		super.createSharedFileAndUpdateCoolImageOnFileByVisitor();
	}

	@Test
	public void downloadFileAPI() throws URISyntaxException {
		super.downloadFileAPI();
	}

	@Test
	public void retrieveFileMetadata() {
		super.retrieveFileMetadata();
	}

	@Test
	public void testFileUpdate() {
		super.testFileUpdate();
	}

	@Test
	// Test for finding the public feed
	public void getPublicFeed() {
		super.getPublicFeed();
	}

	@Test
	public void getPublicFeedInDepth() {
		super.getPublicFeedInDepth();
	}

	@Test
	public void getFoldersFeed() {
		super.getFoldersFeed();
	}

	@Test
	public void getFoldersFeedWithParameters() throws FileNotFoundException {
		super.getFoldersFeedWithParameters();
	}

	@Test
	// Test for finding your pinned files feed
	public void getYourPinnedFilesFeed() {
		super.getYourPinnedFilesFeed();
	}

	@Test
	// Test for finding Your pinned folders feed based on the title
	public void getYourPinnedFoldersFeed() {
		super.getYourPinnedFoldersFeed();
	}

	@Test
	// Test for finding public folders feed based on the title
	public void getPublicFoldersFeed() {
		super.getPublicFoldersFeed();
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
	public void getLibraryInfo() throws FileNotFoundException, Exception {
		super.getLibraryInfo();
	}

	@Test
	// 73563: Files/Wikis parameter name since for API consistency
	public void getPublicDateFilteredFeed() {
		super.getPublicDateFilteredFeed();
	}

	@Test
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

	@Test
	public void testFileComment() throws FileNotFoundException, IOException {
		super.testFileComment();
	}

	@Test
	public void testFileRecylceBin() {
		super.testFileRecylceBin();
	}

	@Test
	public void testFilePin() {
		super.testFilePin();
	}

	@Test
	public void getMyShares() throws FileNotFoundException, IOException {
		super.getMyShares();
	}

	@Test
	public void retrieveListUsersWithLibId() throws Exception {
		super.retrieveListUsersWithLibId();
	}

	@Test
	// userlib
	public void retrieveListUsersWithUserId() throws Exception {
		super.retrieveListUsersWithUserId();
	}

	@Test
	public void retrieveDocumentEntry() {
		super.retrieveDocumentEntry();
	}

	@Test
	public void getDocumentsList() throws FileNotFoundException, IOException {
		super.getDocumentsList();
	}

	@Test(timeOut = 120000)
	// userlib - not for impersonate
	public void deleteUserIdVersionId() throws FileNotFoundException,
			IOException {
		super.deleteUserIdVersionId();
	}

	@Test
	// userlib - not for impersonate
	public void getRecommendations() throws Exception {
		super.getRecommendations();
	}

	@Test
	// userlib - not for impersonate
	public void deleteRecommendations() throws Exception {
		super.deleteRecommendations();
	}

	@Test
	// userlib - not for impersonate
	public void deleteShareLinks() throws Exception {
		super.deleteShareLinks();
	}

	@Test
	public void testFileFolder_all() {
		super.testFileFolder_all();
	}

	public void getLibraryTags() throws FileNotFoundException, IOException {
		super.getLibraryTags();
	}
	
	@Test
	public void postFileWithSpecialHeaders() throws FileNotFoundException,
			IOException {
		super.postFileWithSpecialHeaders();
	}
	
	@Test
	public void createFileUsingMultiPartPost() {
		super.createFileUsingMultiPartPost();
	}
	
	public void orgDataCheck() throws FileNotFoundException, IOException, LCServiceException {
		super.orgDataCheck();
	}
	
	@Test
	public void testFileFolderByVisitor() {
		super.testFileFolderByVisitor();
	}
	
	@Test
	public void testFileVersionByVisitor() {
		super.testFileFolderByVisitor();
	}
	
	@Test
	public void testFileShare() {
		super.testFileShare();
	}
	
	@Test
	public void updateFileMetaData() {
		super.updateFileMetaData();
	}
	
	@Test
	public void testCompressFile() {
		super.testCompressFile();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}
