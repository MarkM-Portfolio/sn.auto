package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;

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
 * [Read/Unread Marker in AS Events] FVT UI Automation for Story 139476
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139568
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_UnreadDot_HoverText extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private String statusUpdateId;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now post a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status update now that the test has completed
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
	}
	
	/**
	* test_NotificationCenter_UnreadDot_HoverText() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update</B></li>
	*<li><B>Step: User 2 comment on the update</B></li>
	*<li><B>Step: User 1 open the notification center</B></li>
	*<li><B>Step: User 1 look at the unread notification of there update being commented on</B></li>
	*<li><B>Step: User 1 hover over the blue dot</B></li>
	*<li><B>Verify: Verify the hover text "Mark read" appears</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E0BD1F19FD0484F285257E2F0044DC06">TTT - INDIVIDUAL NOTIFICATION - 00011 - UNREAD DOT HAS HOVER TEXT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_UnreadDot_HoverText(){

		ui.startTest();
		
		// User 2 will now post a comment to the status update
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comment, profilesAPIUser2);
		
		// User 1 will now log in to Homepage
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnStatusEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser2.getDisplayName());
		
		/**
		 * Verify that the news story is displayed with a blue dot which has hover text of 'Mark read' in the Notification Center
		 * The success of this method call will prove that the hover text is of the expected content in the UI and that the story is marked as unread
		 */
		UIEvents.openNotificationCenterAndVerifyNewsStoryIsUnread(ui, driver, commentOnStatusEvent);
		
		ui.endTest();
	}
}