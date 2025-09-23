package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
/* Copyright IBM Corp. 2010, 2015, 2017                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_PostMicroblogs_ASComment extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };

	private APIProfilesHandler profilesAPIUser1;
	private String user1StatusUpdate, user1StatusUpdateId;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);

		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now post a status update
		user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		user1StatusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, user1StatusUpdate);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the status update created during the test
		profilesAPIUser1.deleteBoardMessage(user1StatusUpdateId);
	}

	/**
	* addStatusUpdate_Comment_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update in the sharebox</B></li>
	*<li><B>Step: Post a comment on the status update from the Activity Stream</B></li>
	*<li><B>Verify: Verify that the comment is added successfully.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2573A910AAA6FA818525794200408BE5">TTT - Activity Stream Sharebox - 00012 - User should be able to post a status</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_Comment_AS(){

		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a comment to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateCommentUsingUI(ui, testUser1, user1StatusUpdate, user1Comment);
		
		// Create the news story to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			if(filter.equals(HomepageUIConstants.FilterAll) == false) {
				// Verify that the comment on message event is displayed in all views except for the 'All' view
				HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnMessageEvent}, null, true);
			}
			// Verify that User 1's status update and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate, user1Comment}, null, true);
		}
		ui.endTest();
	}
}