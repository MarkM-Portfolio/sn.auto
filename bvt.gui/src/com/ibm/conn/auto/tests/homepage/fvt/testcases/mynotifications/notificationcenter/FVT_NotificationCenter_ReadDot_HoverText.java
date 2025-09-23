package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
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
/* Copyright IBM Corp. 2015, 2016                              		 */
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

public class FVT_NotificationCenter_ReadDot_HoverText extends SetUpMethodsFVT {
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private String statusUpdateId;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now post a status update which mentions User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		statusUpdateId = ProfileEvents.addStatusUpdateWithMentions(profilesAPIUser1, mentions);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
	}
	
	/**
	* test_NotificationCenter_ReadDot_HoverText() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Homepage</B></li>
	*<li><B>Step: User 1 add a status update mentioning User 2</B></li>
	*<li><B>Step: User 1 open the notification center</B></li>
	*<li><B>Step: User 1 look at the unread notification of there update being commented on</B></li>
	*<li><B>Step: User 2 click on the blue dot</B></li>
	*<li><B>Step: User 1 hover over the blue dot</B></li>
	*<li><B>Verify: Verify there is hover text saying "Mark Unread"</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C26E30816E97A03D85257E2F0046FB1E">TTT - INDIVIDUAL NOTIFICATION - 00016 - READ DOT HAS HOVER TEXT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_ReadDot_HoverText() {
		
		ui.startTest();
		
		// Log in as User 2
		LoginEvents.loginToHomepage(ui, testUser2, false);
		
		// Create the news story to be verified in the notification center
		String mentionedYouStory = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testUser1.getDisplayName());
		
		// User 2 will now open the notification center and mark the notification news story as 'read'
		boolean hoverTextNowMarkAsUnread = UIEvents.openNotificationCenterAndMarkNewsStoryAsRead(ui, driver, mentionedYouStory);
		
		// Verify that the hover text was changed correctly
		HomepageValid.verifyBooleanValuesAreEqual(hoverTextNowMarkAsUnread, true);
		
		ui.endTest();
	}
}