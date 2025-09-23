package com.ibm.conn.auto.lcapi.test;

import static org.testng.AssertJUnit.assertTrue;

import java.net.URISyntaxException;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIDogearHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/*
 * Author:		Anthony Cox
 * Date:		8th September 2015
 */

public class APIDogearTest extends SetUpMethods2 {
	
	private APIDogearHandler bookmarkOwner;
	private APIProfilesHandler testUser1Profile;
	private APIProfilesHandler testUser2Profile;
	private String testURL;
	private TestConfigCustom config;
	private User testUser1;
	private User testUser2;
	
	private static Abdera abdera;
	private static AbderaClient abderaClient;
	private static Logger log = LoggerFactory.getLogger(APIDogearTest.class);
	private static ServiceConfig serviceConfig;
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() throws URISyntaxException {
		
		// Configurations for each test
		config = TestConfigCustom.getInstance();
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		testUser1 = config.getUserAllocator().getUser(this);
		testUser2 = config.getUserAllocator().getUser(this);
		
		bookmarkOwner = new APIDogearHandler(testURL, testUser1.getAttribute(config.getLoginPreference()), 
												testUser1.getPassword());
		testUser1Profile = new APIProfilesHandler(testURL, testUser1.getAttribute(config.getLoginPreference()), 
													testUser1.getPassword());
		testUser2Profile = new APIProfilesHandler(testURL, testUser2.getAttribute(config.getLoginPreference()), 
													testUser2.getPassword());
		
		// Initialise Abdera
		abdera = new Abdera();
		abderaClient = new AbderaClient(abdera);
				
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
				
		// Get service config for server, assert that it was retrieved and contains the bookmarks service information
		try {
			serviceConfig = new ServiceConfig(abderaClient, testURL, true);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
				
		ServiceEntry bookmarks = serviceConfig.getService("dogear");
		assert(bookmarks != null);

		Utils.addServiceAdminCredentials(bookmarks, abderaClient);
	}
	
	@Test(groups = {"apitest"})
	public void API_CreateBookmark() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String bookmarkURL = Data.getData().commonURL;
		String bookmarkTags = Data.getData().commonTag + Helper.genStrongRand();
		String bookmarkDescription = Data.getData().commonDescription + Helper.genStrongRand();
		
		log.info("INFO: " + testUser1.getDisplayName() + " is now creating a bookmark");
		BaseDogear baseBookmark = new BaseDogear.Builder(testName, bookmarkURL)
												.tags(bookmarkTags)
												.description(bookmarkDescription)
												.build();
		Bookmark bookmark = bookmarkOwner.createBookmark(baseBookmark);
		log.info("INFO: Bookmark successfully created");
		
		assert bookmark.getId().toString().equals("") == false: "ERROR: The bookmark ID was not set correctly when the bookmark was created";
		assert bookmark.getId().toString().equals("null") == false: "ERROR: The bookmark ID was not set correctly when the bookmark was created";
		assert bookmark.getId().toString() != null: "ERROR: The bookmark ID was not set correctly when the bookmark was created";
		assert bookmark.getTitle().equals(testName) == true: "ERROR: The bookmark title does not match the title set in the baseBookmark class";
		assert bookmark.getContent().trim().equals(bookmarkDescription) == true: "ERROR: The bookmark description does not match the description set in the baseBookmark class";
		
		log.info("INFO: API test completed - clean up by deleting the bookmark again");
		bookmarkOwner.deleteBookmark(bookmark);
	}
	
	@Test(groups = {"apitest"})
	public void API_EditBookmarkDescription() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String bookmarkURL = Data.getData().commonURL;
		String bookmarkTags = Data.getData().commonTag + Helper.genStrongRand();
		String bookmarkDescription = Data.getData().commonDescription + Helper.genStrongRand();
		
		log.info("INFO: " + testUser1.getDisplayName() + " is now creating a bookmark");
		BaseDogear baseBookmark = new BaseDogear.Builder(testName, bookmarkURL)
												.tags(bookmarkTags)
												.description(bookmarkDescription)
												.build();
		Bookmark bookmark = bookmarkOwner.createBookmark(baseBookmark);
		log.info("INFO: Bookmark successfully created");
		
		log.info("INFO: Now editing the bookmark description");
		String editedBookmarkDescription = baseBookmark.getDescription() + Helper.genStrongRand();
		bookmarkOwner.editBookmarkDescription(bookmark, editedBookmarkDescription);
		
		assert bookmark.getContent().trim().equals(editedBookmarkDescription) == true: "ERROR: The bookmark description does not match the edited description";
		
		log.info("INFO: API test completed - clean up by deleting the bookmark again");
		bookmarkOwner.deleteBookmark(bookmark);
	}
	
	@Test(groups = {"apitest"})
	public void API_NotifyUserAboutBookmark() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();

		log.info("INFO: " + testUser1.getDisplayName() + " is now creating a bookmark");
		BaseDogear baseBookmark = new BaseDogear.Builder(testName, Data.getData().commonURL)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + testName)
												.build();
		Bookmark bookmark = bookmarkOwner.createBookmark(baseBookmark);
		log.info("INFO: Bookmark successfully created");
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now notify " + testUser2.getDisplayName() + " about the bookmark");
		boolean notificationSent = bookmarkOwner.notifyUserAboutBookmark(bookmark, testUser1Profile, testUser2Profile);
		
		assert notificationSent == true: "ERROR: The bookmark notification process failed";
		
		log.info("INFO: API test completed - clean up by deleting the bookmark again");
		bookmarkOwner.deleteBookmark(bookmark);
	}
	
	@Test(groups = {"apitest"})
	public void API_DeleteBookmark() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();

		log.info("INFO: " + testUser1.getDisplayName() + " is now creating a bookmark");
		BaseDogear baseBookmark = new BaseDogear.Builder(testName, Data.getData().commonURL)
												.tags(Data.getData().commonTag + Helper.genStrongRand())
												.description(Data.getData().commonDescription + testName)
												.build();
		Bookmark bookmark = bookmarkOwner.createBookmark(baseBookmark);
		log.info("INFO: Bookmark successfully created");
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now delete the bookmark");
		boolean deleted = bookmarkOwner.deleteBookmark(bookmark);
		
		assert deleted == true: "ERROR: There was an error while attempting to delete the bookmark";
	}
}
