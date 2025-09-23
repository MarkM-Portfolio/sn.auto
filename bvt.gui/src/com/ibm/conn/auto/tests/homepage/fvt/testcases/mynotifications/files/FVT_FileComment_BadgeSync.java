package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.files;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [FVT Automation for Story 154601] Synchronize real time updates with badging
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/156301
 * @author Patrick Doherty
 */

public class FVT_FileComment_BadgeSync extends SetUpMethodsFVT {
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
				
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now upload a public file which will be shared with User 2
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}

	/**
	* test_FileComment_BadgeSync() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a public</B></li>
	*<li><B>Step: User 1 share the public file with User 2</B></li>
	*<li><B>Step: User 2 comment on the file</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 look at the badge number of the My Notifications view and the Notification Center</B></li>
	*<li><B>Verify: Verify the badge shows the number '1'</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D027A201A8DA7F0C85257E6F00377D47">TTT - NOTIFICATIONS BADGING SYNCHRONIZATION - 00040 - FILES EVENTS MY NOTIFICATIONS BADGE SYNCED WITH HEADER</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_FileComment_BadgeSync() {

		ui.startTest();
		
		// Login and navigate to My Notifications
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Navigate to the I'm Following view - this will reset the My Notifications badge counter to 0
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the My Notifications and Notification Center badges have been reset to 0
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 0);
		
		// User 2 adds a comment to the file
		String comment = Data.getData().StatusComment + Helper.genStrongRand();
		FileEvents.addFileCommentOtherUser(testUser2, filesAPIUser2, publicFile, comment, profilesAPIUser1);
		
		// Refresh the badges by navigating to the I'm Following view
		UIEvents.gotoImFollowing(ui);

		// Verify that the My Notifications and Notification Center badges have now been set to 1
		HomepageValid.verifyMyNotificationsAndNotificationsCenterBadgeValues(driver, 1);
		
		ui.endTest();
	}
}