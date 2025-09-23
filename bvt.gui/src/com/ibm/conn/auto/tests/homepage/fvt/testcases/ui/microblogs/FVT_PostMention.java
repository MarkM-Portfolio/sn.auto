package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
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
/* Copyright IBM Corp. 2015, 2017                                    */
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

public class FVT_PostMention extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS_IM_FOLLOWING = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	private String[] TEST_FILTERS_DISCOVER;

	private APIProfilesHandler profilesAPIUser2;
	private User testUser1, testUser2;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		/// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// Assign the filters to be used for verifications depending on whether the test is being run On Premise / Smart Cloud
		if(isOnPremise) {
			TEST_FILTERS_DISCOVER = new String[2];
			TEST_FILTERS_DISCOVER[1] = HomepageUIConstants.FilterProfiles;
		} else {
			TEST_FILTERS_DISCOVER = new String[1];
		}
		TEST_FILTERS_DISCOVER[0] = HomepageUIConstants.FilterSU;
	}
	
	/**
	* addStatusUpdateMention_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Discover /All</B></li>
	*<li><B>Step: Post a status update with a mention in the sharebox</B></li>
	*<li><B>Verify: Verify that the status update added successfully message appears.</B></li>
	*<li><B>Verify: Verify that the status update appears dynamically in the Activity Stream.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/64763F98356F5F77852579420041D7EC">TTT - Activity Stream Sharebox - 00016 - User should be able to post a status with a link</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdateMention_AS() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// User 1 will now post a status update with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		ProfileEvents.addStatusUpdateWithMentionsUsingUI(ui, driver, testUser1, mentions, false);
		
		// Create the status update with mentions to be verified
		String mentionsStatusUpdate = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the status update with mentions is displayed in the 'All' filter (ie. dynamically)
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsStatusUpdate}, null, true);
		
		for(String filter : TEST_FILTERS_DISCOVER) {
			// Verify that the status update with mentions is displayed in views other than 'All'
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsStatusUpdate}, filter, true);
		}
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the news story to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testUser1.getDisplayName());
		
		// Verify that the mentions event and mentions status update are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsStatusUpdate}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the mentions event and mentions status update are displayed in the all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, mentionsStatusUpdate}, TEST_FILTERS_IM_FOLLOWING, true);
		
		ui.endTest();
	}
}