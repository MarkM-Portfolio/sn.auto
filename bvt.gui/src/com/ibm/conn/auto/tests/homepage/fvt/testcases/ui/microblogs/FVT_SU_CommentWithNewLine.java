package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
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

public class FVT_SU_CommentWithNewLine extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };

	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
	}
	
	/**
	* addTwoLineComment_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Add a status update with a new line in update</B></li>
	*<li><B>Step: Add a comment in the AS to the update with a new line</B></li>
	*<li><B>Verify: Verify that the new line in the update and the comment has been preserved after posting</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E07E3E9962F16039852579BB0059CC67">TTT - Embedded Sharebox - 020 - Homepage AS - New Lines are to be preserved in Microblogs</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addTwoLineComment_AS(){

		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status update with a newline included
		String user1StatusUpdateLine1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateLine2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdate = user1StatusUpdateLine1 + "\n" + user1StatusUpdateLine2;
		ProfileEvents.addStatusUpdateUsingUI(ui, testUser1, user1StatusUpdate, false);
		
		// Create the status update to be used in order to post a comment to it
		String user1StatusUpdateForCommenting = user1StatusUpdateLine1 + " " + user1StatusUpdateLine2;
		
		// User 1 will now post a comment with a newline included to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand() + "\n" + Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateCommentUsingUI(ui, testUser1, user1StatusUpdateForCommenting, user1Comment);
		
		// Create the news story to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			if(filter.equals(HomepageUIConstants.FilterAll) == false) {
				// Verify that the comment on message event is displayed in all views except for the 'All' filter
				HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnMessageEvent}, null, true);
			}
			// Verify that User 1's status update and User 1's comment are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate, user1Comment}, null, true);
		}
		ui.endTest();
	}

	/**
	* addTwoLineComment_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Add a status update with a new line in update</B></li>
	*<li><B>Step: Add a comment in the EE to the update with a new line</B></li>
	*<li><B>Verify: Verify that the new line in the update and the comment has been preserved after posting</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E07E3E9962F16039852579BB0059CC67">TTT - Embedded Sharebox - 020 - Homepage AS - New Lines are to be preserved in Microblogs</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addTwoLineComment_EE(){

		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status update with a newline included
		String user1StatusUpdateLine1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdateLine2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1StatusUpdate = user1StatusUpdateLine1 + "\n" + user1StatusUpdateLine2;
		ProfileEvents.addStatusUpdateUsingUI(ui, testUser1, user1StatusUpdate, false);
		
		// Create the status update to be used in order to post a comment to it
		String user1StatusUpdateForCommenting = user1StatusUpdateLine1 + " " + user1StatusUpdateLine2;
		
		// User 1 will now post a comment with a newline included to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand() + "\n" + Data.getData().commonComment + Helper.genStrongRand();
		UIEvents.addEECommentUsingUI(ui, testUser1, user1StatusUpdateForCommenting, user1Comment);
		
		// Switch focus back to the main frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news story to be verified
		String commentOnMessageEvent = ProfileNewsStories.getCommentedOnYourMessageNewsStory_You(ui);
		
		// Verify that the comment on message event, User 1's status update and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnMessageEvent, user1StatusUpdate, user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}