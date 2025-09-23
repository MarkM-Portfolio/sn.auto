package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification Center Flyout] FVT UI Automation for Story 140633
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/143012
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_ProfilePic extends SetUpMethodsFVT {
	
	private APIFileHandler filesAPIUser1;
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
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now share the public file with User 2
		FileEvents.shareFileWithUser(publicFile, testUser1, filesAPIUser1, profilesAPIUser2);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	* test_NotificationCenter_ProfilePic() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Ensure User 2 has NO notification in the Homepage / My Notifications / For Me view</B></li>
	*<li><B>Step: Ensure User 1 has a profile pic</B></li>
	*<li><B>Step: User 1 log into File</B></li>
	*<li><B>Step: User 1 share a public image file with User 2</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 click on the Notification Center Header in the top navigation</B></li>
	*<li><B>Step: User 2 look at the event that appears in the flyout</B></li>
	*<li><B>Verify: Verify the event has the profile pic of User 1, the event title of the file shared event and the date the event was created</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/76D9DF09D05B12A085257DC800547C3D">TTT - NOTIFICATION CENTER FLYOUT - 00030 - EVENT CONTAINS PROFILE PIC - TITLE - DATE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_ProfilePic(){

		ui.startTest();
		
		// Log in to Connections as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
		
		// Create the news story to be used to verify the presence of the photo in the Notification Center
		String sharedFileEvent = FileNewsStories.getSharedTheFileWithYouNewsStory(ui, baseFile.getRename() + baseFile.getExtension(), testUser1.getDisplayName());
		
		// Verify that the profile picture element for the notification is displayed and is the profile picture for User 1
		boolean isUser1ProfilePic = UIEvents.checkNotificationCenterProfilePicture(ui, sharedFileEvent, profilesAPIUser1);
		HomepageValid.verifyBooleanValuesAreEqual(isUser1ProfilePic, true);
		
		ui.endTest();
	}
}