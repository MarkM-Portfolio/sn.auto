package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

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
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Read/Unread Marker in AS Events] FVT UI Automation for Story 139476
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139568
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_ReadDot_NotDisplayed extends SetUpMethodsFVT {
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseFile baseFile;
	private FileEntry sharedFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);

		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public file and will share it with User 2
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.PEOPLE, profilesAPIUser2);
		sharedFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(sharedFile);
	}
	
	/**
	* test_NotificationCenter_ReadDot_NotDisplayed() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 upload a file</B></li>
	*<li><B>Step: User 1 share the file with User 2</B></li>
	*<li><B>Step: User 2 open the notification center</B></li>
	*<li><B>Step: User 2 look at the unread notification of being notified of the file shared</B></li>
	*<li><B>Step: User 2 click on the blue dot</B></li>
	*<li><B>Step: User 2 close and reopen the notification center</B></li>
	*<li><B>Step: User 2 hover over the file share event</B></li>
	*<li><B>Verify: Verify there is a hollow blue circle in the top right corner</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/92CA887D36B143F985257E2F0046A2D1">TTT - INDIVIDUAL NOTIFICATION - 00015 - HOLLOW BLUE CIRCLE FOR READ</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_ReadDot_NotDisplayed(){

		ui.startTest();
		
		// Log in as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Create the news story to be verified in the notification center
		String fileSharedWithYouStory = FileNewsStories.getSharedTheFileWithYouNewsStory(ui, baseFile.getRename() + baseFile.getExtension(), testUser1.getDisplayName());
		
		// User 2 will now open the notification center and mark the notification news story as 'read' and verify that the blue dot icon changes
		boolean blueDotChangedCorrectly = UIEvents.openNotificationCenterAndMarkNewsStoryAsReadAndVerifyBlueDotIconChanges(ui, driver, fileSharedWithYouStory);
		
		// Verify that the blue dot icon was changed correctly
		HomepageValid.verifyBooleanValuesAreEqual(blueDotChangedCorrectly, true);
		
		ui.endTest();
	}
}