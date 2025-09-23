package com.ibm.conn.auto.tests.homepage.fvt.finalisation.homepageui.mynotifications;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
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
/* Copyright IBM Corp. 2016                                    		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	27th September 2016
 */

public class FVT_PrivateFile_NotificationBadging_LikeEvents extends SetUpMethods2 {

	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private FileEntry privateFile;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		filesAPIUser2 = new APIFileHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		filesAPIUser3 = new APIFileHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		// User 1 will now create a private standalone file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE);
		privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now share the private file with Users 2 and 3
		FileEvents.shareFileWithUser(privateFile, testUser1, filesAPIUser1, profilesAPIUser2);
		FileEvents.shareFileWithUser(privateFile, testUser1, filesAPIUser1, profilesAPIUser3);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(privateFile);
	}
	
	/**
	* test_NotificationBadging_LikeEvents() 
	*<ul>
	*<li><B>1: User 1 upload a private file and share with User 2 and User 3</B></li>
	*<li><B>2: User 1 like the file</B></li>
	*<li><B>3: User 2 like the file</B></li>
	*<li><B>4: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>5: User 1 look at the badge number of the My Notifications view - verification point</B></li>
	*<li><B>6: User 3 like the file</B></li>
	*<li><B>7: User 1 refresh the I'm Following view and look at the badge again - verification point</B></li>
	*<li><B>8: User 1 unlike the file</B></li>
	*<li><B>9: User 1 refresh the I'm Following view again</B></li>
	*<li><B>10: User 1 check the badge - verification point</B></li>
	*<li><B>Verify: The badge shows the number '1'</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/858D82C4C3A1F8F885257E18003BBC23">NOTIFICATIONS BADGING - FILES - 00011 - FILE LIKE EVENTS</a></li>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_NotificationBadging_PrivateFile_LikeEvents() {
		
		ui.startTest();
		
		// Log in as User 1 and navigate to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view to reset the My Notifications counter to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter have been reset to 0
		verifyMyNotificationsAndNotificationsCenterBadgeValues(0);
				
		// User 1 will now like the file
		String user1LikeFileURL = FileEvents.likeFile(testUser1, filesAPIUser1, privateFile);
		
		// User 2 will now like the file
		FileEvents.likeFileOtherUser(privateFile, testUser2, filesAPIUser2);
		
		// Refresh the page by re-navigating to I'm Following
		UIEvents.gotoImFollowing(ui);
		
		if(UIEvents.getMyNotificationsBadgeValue(driver) != 1) {
			// Log out to give the server more time to update the My Notifications badges
			LoginEvents.logout(ui);
			
			// Log in as User 1 and navigate to the I'm Following view
			LoginEvents.loginAndGotoImFollowing(ui, testUser1, true);
		}
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		verifyMyNotificationsAndNotificationsCenterBadgeValues(1);
		
		// User 3 will now like the file
		FileEvents.likeFileOtherUser(privateFile, testUser3, filesAPIUser3);
		
		// Refresh the page by re-navigating to I'm Following
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		verifyMyNotificationsAndNotificationsCenterBadgeValues(1);
		
		// User 1 will now unlike the file
		FileEvents.unlikeFile(privateFile, user1LikeFileURL, testUser1, filesAPIUser1);
		
		// Refresh the page by re-navigating to I'm Following
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications badge counter and Notification Center badge counter now have a value of 1
		verifyMyNotificationsAndNotificationsCenterBadgeValues(1);
		
		ui.endTest();
	}
	
	/**
	 * Verifies that the My Notifications and Notification Center badges match the specified value
	 * 
	 * @param badgeValueToBeVerified - The Integer value to be verified
	 */
	private void verifyMyNotificationsAndNotificationsCenterBadgeValues(int badgeValueToBeVerified) {
		
		// Retrieve the badge values for both the My Notifications and Notification Center badges
		int myNotificationsCounter = UIEvents.getMyNotificationsBadgeValue(driver);
		int notificationCenterCounter = UIEvents.getNotificationCenterBadgeValue(driver);
		
		// Verify that the My Notifications badge counter and Notification Center badge counters match the required value
		HomepageValid.verifyIntValuesAreEqual(myNotificationsCounter, badgeValueToBeVerified);
		HomepageValid.verifyIntValuesAreEqual(notificationCenterCounter, badgeValueToBeVerified);
	}
}