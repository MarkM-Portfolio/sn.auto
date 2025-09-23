package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016 			                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

/**
 * @author 	Anthony Cox
 * Date:	11th April 2016
 */

public class FVT_Mentions_EE_File_Comment_Guest extends SetUpMethods2 {
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler testUser1Profile;
	private FilesUI uiFiles;
	private HashMap<FileEntry, APIFileHandler> listOfFiles = new HashMap<FileEntry, APIFileHandler>();
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2;
				
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiFiles = FilesUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		testUser1Profile = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		uiFiles = FilesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		Set<FileEntry> filesToBeDeleted = listOfFiles.keySet();
		
		for(FileEntry file : filesToBeDeleted) {
			listOfFiles.get(file).deleteFile(file);
		}
	}
	
	/**
	* mentions_ee_fileComment_guestUser() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a comment and start to add an @mentions to a guest user</B></li>
	*<li><B>Verify: Verify the mentions cannot be added for a guest user</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBD62AF6EC03949285257C6D00578BA6">TTT - @Mentions - EE - File Comment - 00025 - User cannot add an @mentions to aguest user - SC only</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtguest"})
	public void mentions_ee_fileComment_guestUser() {
		
		ui.startTest();
		
		// User 1 will upload a new public file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		listOfFiles.put(publicFile, filesAPIUser1);
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		/**
		 *  Open the file details overlay for the file, add a mention to the comments input field which includes a guest user
		 *  
		 *  PLEASE NOTE: The use of testUser1Profile in the mentions to the guest user is deliberate. Since user 2 is a guest user,
		 *  it is impossible to instantiate an APIProfilesHandler instance for this user. Therefore, I am including a valid APIProfilesHandler
		 *  instance in order to create the Mentions object successfully - it does NOT affect the test case since this is a UI test and only
		 *  the user name needs to be correct as the user name is what is entered in the file details overlay.
		 */
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, testUser1Profile, serverURL, "", "");
		String fileUploadedEvent = ui.replaceNewsStory(Data.FILE_UPLOADED, null, null, testUser1.getDisplayName());
		
		/**
		 * The test case requires that the guest user cannot be mentioned. In the UI, the typeahead menu will display NO users when the guest
		 * user is mentioned. The method invoked, below, already verifies that there are no users in the typeahead and returns true if all actions complete
		 * successfully. Therefore the only test case assertion is to verify that true is returned from the method.
		 */
		boolean couldNotMention = FileEvents.openFileOverlayAndTypeMentionToDifferentOrgUserOrGuest(ui, driver, uiFiles, fileUploadedEvent, mentions);
		HomepageValid.verifyBooleanValuesAreEqual(couldNotMention, true);
		
		ui.endTest();
	}
}