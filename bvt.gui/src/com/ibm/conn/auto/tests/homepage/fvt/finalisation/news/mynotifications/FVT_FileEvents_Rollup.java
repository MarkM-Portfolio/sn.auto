package com.ibm.conn.auto.tests.homepage.fvt.finalisation.news.mynotifications;

import java.util.ArrayList;

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
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	3rd October 2016
 */

public class FVT_FileEvents_Rollup extends SetUpMethods2 {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };
	
	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3, filesAPIUser4;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 4 unique users are chosen from the CSV file
		ArrayList<User> listOfUsers = new ArrayList<User>();
		listOfUsers.add(cfg.getUserAllocator().getUser(this));
		
		do {
			User currentUser = cfg.getUserAllocator().getUser(this);
			int index = 0;
			boolean userAlreadyChosen = false;
			while(index < listOfUsers.size() && userAlreadyChosen == false) {
				if(listOfUsers.get(index).getDisplayName().equals(currentUser.getDisplayName())) {
					userAlreadyChosen = true;
				}
				index ++;
			}
			if(userAlreadyChosen == false) {
				listOfUsers.add(currentUser);
			}
		} while(listOfUsers.size() < 4);
		
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		testUser4 = listOfUsers.get(3);
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		filesAPIUser2 = new APIFileHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		filesAPIUser3 = new APIFileHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		filesAPIUser4 = new APIFileHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		// User 1 will now create a public standalone file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
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
	* test_PublicFile_Comments_Likes_Rollup() 
	*<ul>
	*<li><B>1: User 1 log into Files</B></li>
	*<li><B>2: User 1 upload a Public file</B></li>
	*<li><b>3: User 2, User 3 and User 4 comment on the file</b></li>
	*<li><B>4: User 2 and User 3 like the file</B></li>
	*<li><B>5: User 1 go to Homepage / My Notifications / For Me / All, Files - Verification Point 1</B></li>
	*<li><B>6: User 2 and User 3 update their comments on the file</B></li>
	*<li><B>7: User 1 go to Homepage / My Notifications / For Me / All, Files - Verification Point 2</B></li>
	*<li><B>Verification Point 1: Verify that there are 2 events showing:</B></li>
	*<li><B>						<User 4> and 2 others commented on your file</b></li>
	*<li><b>						<User 3>  and <User 2> liked your file</b></li>
	*<li><B>Verification Point 2: Verify that there are 2 events showing:</B></li>
	*<li><B>						<User 4> and 2 others commented on your file</b></li>
	*<li><B>						<User 3>  and <User 2> liked your file</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E902AEB19B6C91CA85257DEB00428ADC">ROLL UP - MY NOTIFICATIONS VIEW - 00010 - COMMENTS LIKES AND UPDATED ROLL UP SEPARATELY</a></li>
	*</ul>
	*/
	@Test(groups ={"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PublicFile_Comments_Likes_Rollup() {
		
		ui.startTest();
		
		// User 2 will now post a comment to the file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileComment user2FileComment = FileEvents.addFileCommentOtherUser(testUser2, filesAPIUser2, publicFile, user2Comment, profilesAPIUser1);
		
		// User 3 will now post a comment to the file
		String user3Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileComment user3FileComment = FileEvents.addFileCommentOtherUser(testUser3, filesAPIUser3, publicFile, user3Comment, profilesAPIUser1);
				
		// User 4 will now post a comment to the file
		String user4Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser4, filesAPIUser4, publicFile, user4Comment, profilesAPIUser1);
		
		// User 2 will now like / recommend the file
		FileEvents.likeFileOtherUser(publicFile, testUser2, filesAPIUser2);
		
		// User 3 will now like / recommend the file
		FileEvents.likeFileOtherUser(publicFile, testUser3, filesAPIUser3);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news stories to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnYourFileNewsStory_UserAndMany(ui, testUser4.getDisplayName(), "2");
		String likeFileEvent = FileNewsStories.getLikeYourFileNewsStory_TwoUsers(ui, testUser3.getDisplayName(), testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on file event, like file event, User 3's comment and User 4's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFileEvent, likeFileEvent, user3Comment, user4Comment}, filter, true);
			
			// Verify that User 2's comment is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, false);
		}
		
		// User 2 will now update their comment posted to the file
		String user2UpdatedComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.updateFileComment(testUser2, filesAPIUser2, publicFile, user2FileComment, user2UpdatedComment);
		
		// User 3 will now update their comment posted to the file
		String user3UpdatedComment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.updateFileComment(testUser3, filesAPIUser3, publicFile, user3FileComment, user3UpdatedComment);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on file event, like file event, User 2's updated comment and User 3's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnFileEvent, likeFileEvent, user2UpdatedComment, user3UpdatedComment}, filter, true);
			
			// Verify that User 2's original comment, User 3's original comment and User 4's comment are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user3Comment, user4Comment}, null, false);
		}
		ui.endTest();
	}
}