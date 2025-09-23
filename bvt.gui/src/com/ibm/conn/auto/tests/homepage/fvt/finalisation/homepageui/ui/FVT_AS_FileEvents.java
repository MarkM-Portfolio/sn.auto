package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.ui;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	30th September 2016
 */

public class FVT_AS_FileEvents extends SetUpMethods2 {
	
	private APIFileHandler filesAPIUser1;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		// User 1 will now create a public standalone file
		baseFile = FileBaseBuilder.buildBaseFileWithMultipleTags(Data.getData().file25, ".odt", ShareLevel.EVERYONE, 10);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}

	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	 * test_FileEventsInAS_MultipleTags() 
	 *<ul>
	 *<li><B>1: Log into Connections</B></li>
	 *<li><B>2: Upload a public .odt file with 10 tags added on upload</B></li>
	 *<li><B>3: Go to Home / Updates / Discover / All</B></li>
	 *<li><B>4: Look at the event of the file uploaded</B></li>
	 *<li><B>Verify: Verify 4 tags appear in the column and <no of tags> more</B></li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F928F6135B5BF7EC85257D0F004F00B6">FILE PREVIEW - 00022 - FILE CREATED - COMPATIBLE PUBLIC FILE - # OF TAGS SHOWN IN DETAILS BOX</a></li>
	 */
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_FileEventsInAS_MultipleTags() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Retrieve the tags string from the file details box in the UI
		String fileTagsString = UIEvents.getFileDetailsBoxTagsString(ui, baseFile.getRename() + baseFile.getExtension());
		
		// Create the list of tags to be verified as being present / absent in the UI string
		List<String> listOfFileTags = new ArrayList<String>();
		String fileTags = baseFile.getTags();
		while(fileTags.length() > 0) {
			int indexOfFirstSpace = fileTags.indexOf(" ");
			String currentTag;
			if(indexOfFirstSpace == -1) {
				currentTag = fileTags.trim();
				fileTags = "";
			} else {
				currentTag = fileTags.substring(0, indexOfFirstSpace).trim();
				fileTags = fileTags.substring(indexOfFirstSpace + 1).trim();
			}
			listOfFileTags.add(currentTag);
		}
		
		// Retrieve the number of tags that are displayed in the file details box UI string
		int numberOfTagsDisplayedInUI = 0;
		for(String tag : listOfFileTags) {
			if(fileTagsString.indexOf(tag.toLowerCase()) > -1) {
				numberOfTagsDisplayedInUI ++;
			}
		}
		// Set how many tags should be under the "and <no of tags> more" section of the UI string
		int numberOfTagsUnderMoreHeading = listOfFileTags.size() - numberOfTagsDisplayedInUI;
		
		// Verify that 4 tags are displayed in the files details box in the UI
		HomepageValid.verifyIntValuesAreEqual(numberOfTagsDisplayedInUI, 4);
		
		// Verify that the end of the UI string reads "and 6 more"
		HomepageValid.verifyStringContainsSubstring(fileTagsString, "and " + numberOfTagsUnderMoreHeading + " more");
		
		ui.endTest();
	}
}