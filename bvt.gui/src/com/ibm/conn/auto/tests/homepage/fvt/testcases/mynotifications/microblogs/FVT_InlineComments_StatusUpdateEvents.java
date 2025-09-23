package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
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
/* Copyright IBM Corp. 2015, 2017                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_StatusUpdateEvents extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private String statusUpdate, statusUpdateId;
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
		
		// Set the filters to be tested - "Profiles" filter is NOT available on Smart Cloud
		if(isOnPremise){
			TEST_FILTERS = new String[2];
			TEST_FILTERS[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS = new String[1];
		}
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		
		// User 1 will now post a status update
		statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
	}

	/**
	* test_LastTwoComments_YourStatusUpdateComment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 create a Status Update</B></li>
	*<li><B>Step: User 2 add 4 comments to it</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 delete all comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the last 2 comments are shown inline with a show previous link</B></li>
	*<li><B>Verify: Verify all comments have been deleted and the show previous link</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EADF0FE45CF9F56485257E2F0036A461">TTT - INLINE COMMENTS - 00090 - MICROBLOGGING EVENTS MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_LastTwoComments_YourStatusUpdateComment() {

		ui.startTest();
		
		// User 2 will now post 4 comments to User 1's status update
		String user2Comments[] = { Data.getData().commonStatusUpdate + Helper.genStrongRand(), Data.getData().commonStatusUpdate + Helper.genStrongRand(), Data.getData().commonStatusUpdate + Helper.genStrongRand(), Data.getData().commonStatusUpdate + Helper.genStrongRand() };
		String user2CommentIds[] = new String[user2Comments.length];
		for(int index = 0; index < user2Comments.length; index ++) {
			user2CommentIds[index] = ProfileEvents.addStatusUpdateComment(statusUpdateId, user2Comments[index], profilesAPIUser2);
		}
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news stories and elements to be verified
		String commentOnStatusEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_User(ui, testUser2.getDisplayName());
		String showPreviousLinkCSS = HomepageUIConstants.ShowPreviousComments.replace("PLACEHOLDER", commentOnStatusEvent);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status event, User 1's status update and the last 2 comments posted by User 2 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnStatusEvent, statusUpdate, user2Comments[2], user2Comments[3]}, filter, true);
			
			// Verify that the 'Show Previous' link is displayed in all views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{showPreviousLinkCSS}, null, true);
			
			// Verify that the first 2 comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[0], user2Comments[1]}, null, false);
		}
		// User 2 will now delete their 4 comments posted to User 1's status update
		for(int index = 0; index < user2CommentIds.length; index ++) {
			ProfileEvents.deleteComment(statusUpdateId, user2CommentIds[index], profilesAPIUser2);
		}
		
		// If this test is being run on Smart Cloud - ie. the filter / view is NOT changed - refresh the My Notifications view
		if(!isOnPremise) {
			UIEvents.gotoImFollowing(ui);
			UIEvents.gotoMyNotifications(ui);
		}
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on status event and all User 2's comments are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnStatusEvent, statusUpdate, user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, filter, false);
			
			// Verify that the 'Show Previous' link is NOT displayed in any of the views
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{showPreviousLinkCSS}, null, false);
		}
		ui.endTest();
	}
}