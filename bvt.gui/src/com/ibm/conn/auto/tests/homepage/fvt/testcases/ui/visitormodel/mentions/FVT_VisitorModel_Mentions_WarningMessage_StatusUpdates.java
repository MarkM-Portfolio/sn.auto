package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.mentions;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.profile.ProfileNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ProfilesUI;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_Mentions_WarningMessage_StatusUpdates extends SetUpMethods2{
	
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3;
	private ArrayList<String> listOfStatusUpdateIds = new ArrayList<String>();
	private HomepageUI ui;
	private ProfilesUI uiProfiles;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiProfiles = ProfilesUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		} while(testUser1.getDisplayName().equalsIgnoreCase(testUser3.getDisplayName()));
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		uiProfiles = ProfilesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Have User 1 delete all API-created status updates once the tests have completed
		for(String statusUpdateId : listOfStatusUpdateIds) {
			profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		}
	}
	
	/**
	* visitor_mentions_StatusUpdate() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Homepage</B></li>
	*<li><B>Step: testUser1 add a status update mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they cannot see public updates" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/92536E14C875477F85257C8D0044F12C">TTT - MENTIONS - 00010 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - STATUS UPDATE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_mentions_StatusUpdate() {

		ui.startTest();
		
		// User 1 will log in and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		
		// User 1 will now post a status update with mentions - mentioning a visitor
		boolean statusPostedCorrectly = ProfileEvents.addStatusUpdateWithMentionsUsingUI(ui, driver, testUser1, mentions, true);
		
		// Verify that the status update with mentions posted correctly and all relevant warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(statusPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, false);
		
		ui.endTest();
	}
	
	/**
	* visitor_mentions_StatusUpdate_Comment() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Homepage</B></li>
	*<li><B>Step: testUser1 go to Homepage / Updates / Discover / Status Updates</B></li>
	*<li><B>Step: testUser1 add a comment mentioning a visitor to a status update there - verification point 1</B></li>
	*<li><B>Step: testUser1 post the comment</B></li>
	*<li><B>Step: testUser1 2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they cannot see public updates" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/17C0B02F70D28B3385257C8D00457AD4">TTT - MENTIONS - 00012 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - COMMENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_mentions_StatusUpdate_Comment() {
		
		ui.startTest();
		
		// User 1 will now post a status update
		String statusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdate);
		listOfStatusUpdateIds.add(statusUpdateId);
		
		// User 1 will log in and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		
		// User 1 will now post a comment with mentions to User 2 to the status update
		boolean commentPostedCorrectly = ProfileEvents.addStatusUpdateCommentWithMentionsUsingUI(ui, driver, testUser1, statusUpdate, mentions, true);
		
		// Verify that the comment with mentions posted correctly and all relevant warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(commentPostedCorrectly, true);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInACommentOnMessageNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, statusUpdate, mentionsText}, null, false);
		
		ui.endTest();
	}
	
	/**
	* visitor_mentions_BoardMessage() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into another users profile</B></li>
	*<li><B>Step: testUser1 add a board message mentioning a visitor - verification point 1</B></li>
	*<li><B>Step: testUser1 post the update</B></li>
	*<li><B>Step: testUser2 who is the visitor log into Homepage / Mentions - verification point 2</B></li>
	*<li><B>Verify: Verify the warning "The following people cannot view this message because they cannot see public updates" appears</B></li>
	*<li><B>Verify: Verify there is no mentions event</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5517994CCF5E51FA85257C8D004558E9">TTT - MENTIONS - 00011 - VISITORS - WARNING MESSAGE WHEN VISITOR CANT BE MENTIONED - BOARD MESSAGE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_mentions_BoardMessage() {

		ui.startTest();
		
		// User 1 will log in and navigate to User 3's profile
		ProfileEvents.loginAndNavigateToUserProfile(ui, uiProfiles, testUser1, profilesAPIUser3, false);
		
		// Create the Mentions instance of the visitor to be mentioned
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		
		// User 1 will now post a board message with mentions - mentioning a visitor
		boolean boardMessagePostedCorrectly = ProfileEvents.addStatusUpdateWithMentionsUsingUI(ui, driver, testUser1, mentions, true);
		
		// Verify that the board message with mentions posted correctly and all relevant warning messages were displayed as expected during the process
		HomepageValid.verifyBooleanValuesAreEqual(boardMessagePostedCorrectly, true);
		
		// Return to the Home screen and log out from Connections
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 2 and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, true);
		
		// Create the elements to be verified
		String mentionedYouEvent = ProfileNewsStories.getMentionedYouInAMessageNewsStory(ui, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser2.getDisplayName() + " VISITOR " + mentions.getAfterMentionText();
		
		// Verify that the mentions event is NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, mentionsText}, null, false);
				
		ui.endTest();
	}
}