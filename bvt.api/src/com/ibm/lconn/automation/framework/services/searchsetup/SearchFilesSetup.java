package com.ibm.lconn.automation.framework.services.searchsetup;

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/**
 * @author David Yogodzinski & James Golin
 */
public class SearchFilesSetup {
	static UserPerspective user;

	private static FilesService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchFilesSetup.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Files Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.FILES.toString());
		service = user.getFilesService();
		assertTrue("Files service problem, service is NULL",service != null);
		LOGGER.debug("Finished Initializing Search Files Data Setup Test");
	}

	// create a file for later search tests
	@Test
	public void createPublicTestFileToSearch() {
		deleteAllFilesAndFolders();
		// boolean found = false;

		// create a new file to be used for testing
		File testFile = new File("forSearch.txt");
		FileEntry testFileEntry = new FileEntry(testFile,
				StringConstants.SEARCH_FILES_NAME, "The Bond Car", "carmen",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);
		Entry result = (Entry) service.createFileNoInputStream(testFileEntry);

		// check for conflict errors
		if (result.getAttributeValue(StringConstants.API_ERROR) != null) {
			if (Integer.parseInt(result.getExtension(
					StringConstants.API_RESPONSE_CODE).getText()) == 409) {
				assertTrue(true);
			} else {
				assertTrue(false);
				LOGGER.debug("Test Failed: Failed to create a new public file");
			}
		}

		// check to see the file was made successfully - not working if the file
		// is outside 50
		/*
		 * Feed publicFilesFeed = (Feed) service.getPublicFeedInDepth("&ps=50");
		 * 
		 * for(Entry e : publicFilesFeed.getEntries()){
		 * if(e.getTitle().equals(StringConstants.SEARCH_FILES_NAME)){
		 * LOGGER.info
		 * ("Test Successful: Found the test file in the public feed");
		 * assertTrue(true); found = true; break; } } if(!found){ LOGGER.debug(
		 * "Test Failed: Could not find the test file in the public feed");
		 * assertTrue(false); }
		 */
	}

	// delete current tests
	public void deleteAllFilesAndFolders() {
		// Get delete public files
		Feed publicFilesFeed = (Feed) service.getPublicFeedInDepth("&ps=50");
		for (Entry e : publicFilesFeed.getEntries()) {
			if (e.getTitle().equals(StringConstants.SEARCH_FILES_NAME)) {
				service.deleteFile(e.getExtension(StringConstants.TD_UUID)
						.getText());
				break;
			}
		}
		// empty trash
		service.purgeAllFilesFromTrash();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}
