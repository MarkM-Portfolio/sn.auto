package com.ibm.conn.auto.tests.webeditors.sharepointiwidget;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.wink.json4j.JSONException;
import org.testng.annotations.*;
import org.xml.sax.SAXException;

import com.ibm.conn.auto.tests.webeditors.ShpIWidgetBaseTest;
import com.ibm.conn.auto.util.webeditors.fvt.SharepointRestClient;
import com.ibm.conn.auto.webui.SharepointWidgetUI.FileType;

import static com.ibm.conn.auto.webui.SharepointWidgetUI.FileType.*;
import static com.ibm.conn.auto.webui.SharepointWidgetUI.Widget.IFRAME;

public final class ShpIWidgetO365FilesTest extends ShpIWidgetBaseTest {
	
	private static final String 
			UTF_8 = "UTF-8", // StandardCharsets.UTF_8.name();
			TESTFILE_FOLDER = "resources",
			TESTFILENAME_PREFIX = "WebEditorsTestFile";

	private static final int WAIT_TIME_BEFORE_FILE_DELETE_MSEC = 2000;
	
	private String contentBaseFolder;
	private SharepointRestClient sharepointRestClient;
	
	@BeforeClass(alwaysRun = true)
	public void beforeClassO3T() {
		// if this class ever gets loaded then we are going to need SharepointRestClient
		setupSharepointRestClient();
	}
	
	private void setupSharepointRestClient() {
		try {
			log.info("INFO: Creating the Sharepoint REST client");
			sharepointRestClient = new SharepointRestClient(SHAREPOINT_URL_SCHEME, SHAREPOINT_SERVER_NAME, SHAREPOINT_SERVER_PORT, true, 
															SHAREPOINT_USERNAME, SHAREPOINT_PASSWORD);
			
			log.info("INFO: Logging the REST client into Sharepoint");
			sharepointRestClient.performFormAuthentication();
		}
		catch(Exception e) {
			throw new RuntimeException("An error has occurred while attempting to setup the Sharepoint REST client!", e);
		}
	}
	
	@BeforeMethod(alwaysRun = true)
	public void beforeMethodO3T() throws UnsupportedEncodingException {
		contentBaseFolder = URLDecoder.decode( sharepointWidgetUI.getContentBaseFolder(), UTF_8 );
	}
	
	/**
	 * This method is not a test. It is meant to clear out all the test files and folders that were left on the Sharepoint server due to aborted tests.
	 * Is should be disabled by default because the target server *may* be used simultaneously by other tests which may be using these test files and folders 
	 * at the time this 'test' is executed. This method seeks out all 'Test(...)' files and folders created in the current test suite's 'contentBaseFolder',
	 * and deletes them all. This is done via Sharepoint's API. 
	 */
	//@Test(groups = { "SP_FVT", "WE_FVT" }, priority = 5)
	public void deleteAllTestFilesAndFoldersInSharepoint() 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, MalformedURLException, XPathExpressionException, UnsupportedEncodingException, IOException, URISyntaxException, ParserConfigurationException, SAXException {
		sharepointRestClient.deleteFilesWithPrefix(contentBaseFolder, "Test");
		sharepointRestClient.deleteFoldersWithPrefix(contentBaseFolder, "Test");
	}
	
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 10)
	public void createFilesTest() 
			throws URISyntaxException, InterruptedException, KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, XPathExpressionException, IOException, ParserConfigurationException, SAXException {
		loginAndNavigateToCommunity();
		
		executeContentTest();
		
		String filename;
		for(FileType fileType : Arrays.asList(new FileType[]{WORD, EXCL, PWPT}) ) {
			filename = sharepointWidgetUI.createTestFile(fileType);
			Thread.sleep(WAIT_TIME_BEFORE_FILE_DELETE_MSEC); // to avoid the http 423 'file locked' issue
			sharepointRestClient.deleteFileByName(contentBaseFolder, filename + fileType.getExtension());
		}
		
		String foldername;
		//for(FileType fileType : Arrays.asList(new FileType[]{ONEN, FLDR}) ) { // OneNote has been excluded from testing, as it was deemed scope creep (for now).
			foldername = sharepointWidgetUI.createTestFile(FLDR);
			Thread.sleep(WAIT_TIME_BEFORE_FILE_DELETE_MSEC); // to avoid the http 423 'file locked' issue
			sharepointRestClient.deleteFolderByName(contentBaseFolder, foldername);
		//}
		
		log.info("INFO: returning focus to the top frame (Connections web page)...");
		driver.switchToFrame().returnToTopFrame();
	}
	
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 20)
	public void openFileTest() 
			throws URISyntaxException, KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException, XPathExpressionException, ParserConfigurationException, SAXException, JSONException {

		loginAndNavigateToCommunity();

		executeContentTest();
		
		final FileType[] knownFileTypes = new FileType[]{WORD, EXCL, PWPT};
		for(FileType fileType : Arrays.asList(knownFileTypes)) {
			
			String testFilename = TESTFILENAME_PREFIX + (fileType == EXCL ? "Sheet" : "");
			String testFileRelativePath = TESTFILE_FOLDER + File.separator + testFilename + fileType.getExtension();
			
			log.info("INFO: adding '" + testFileRelativePath + "' to '" + contentBaseFolder + "'");
			sharepointRestClient.addFile(contentBaseFolder, testFileRelativePath);
			
			// refresh (?) / navigate to community > full page
			// driver.navigate().refresh(); // this breaks the widget state
			sharepointWidgetUI.navigateToOverviewFullpageMode();
			sharepointWidgetUI.navigateToWidgetFullpageMode(TITLE_ON_CONNECTIONS);
			
			log.info("INFO: locating the frame with the Sharepoint generated content, which is part of the Sharepoint Files widget");
			driver.switchToFrame().selectSingleFrameBySelector(IFRAME);

			// click the file itself
			sharepointWidgetUI.openFile(testFilename);
			
			sharepointRestClient.deleteFileByName(contentBaseFolder, testFilename + fileType.getExtension());
		}
		
		log.info("INFO: returning focus to the top frame (Connections web page)...");
		driver.switchToFrame().returnToTopFrame();
	}


}
