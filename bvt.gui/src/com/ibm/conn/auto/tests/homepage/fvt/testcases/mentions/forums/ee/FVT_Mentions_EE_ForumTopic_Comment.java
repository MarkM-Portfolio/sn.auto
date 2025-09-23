package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums.ee;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

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

public class FVT_Mentions_EE_ForumTopic_Comment extends SetUpMethodsFVT {
	
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseForum baseForum;
	private BaseForumTopic baseForumTopic;
	private Forum publicForum;
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now create a standalone forum
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		publicForum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
		
		// User 1 will now create and add a topic to the forum
		baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(getClass().getSimpleName() + Helper.genStrongRand(), publicForum);
		ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the forum created during the test
		forumsAPIUser1.deleteForum(publicForum);
	}
	
	/**
	* mentions_ee_forumTopicComment_twoCharactersTypeahead() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a topic created in a forum</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start to add a comment to the entry with and add @xx</B></li>
	*<li><B>Verify: Verify that this brings up a dialog with names that match and email</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B41A91FF6656E5AC85257CA700396F9E">TTT - @Mentions - EE - Forum Reply - 00001 - Typeahead should appear when at least 2 characters have been entered</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void mentions_ee_forumTopicComment_twoCharactersTypeahead() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a partial mention to the comments input field and retrieve the list of users from the typeahead
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser1, profilesAPIUser1, serverURL, "", "");
		String forumTopicCreatedEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		ArrayList<String> typeaheadList = UIEvents.openEEAndTypeBeforeMentionsTextAndTypePartialMentionsAndGetTypeaheadMenuTextContents(ui, driver, forumTopicCreatedEvent, mentions, 2, false);
		
		for(String userText : typeaheadList) {
			// Verify that the found user text contains with the same 2 letters as the partial mention used in the comment input field
			HomepageValid.verifyStringContainsSubstring(userText, testUser1.getDisplayName().substring(0, 2));
			
			// Retrieve the email for this user from the text
			String userEmail = userText.substring(userText.lastIndexOf(" ") + 1);
			
			// Find where the numerical value begins in the email address (eg. in ajones123, the numerical "123" begins at index position 6)
			int index = 0;
			while(userEmail.charAt(index) <= 47 || userEmail.charAt(index) >= 58) {
				index ++;
			}
			// Create the user name to be verified - including the numerical
			String userNameToCheckFor = "Amy Jones" + userEmail.substring(index, userEmail.indexOf("@"));
			
			// Verify that the found user text contains the user name from the email address (ie. user name and email addresses match) at String index position 0
			HomepageValid.verifyIntValuesAreEqual(userText.indexOf(userNameToCheckFor), 0);
		}
		ui.endTest();
	}

	/**
	* mentions_ee_forumTopicComment_userSelection_Typeahead() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Go to a forum you own</B></li>
	*<li><B>Step: Start a topic in the forum</B></li>
	*<li><B>Step: Go to Homepage Activity Stream</B></li>
	*<li><B>Step: Open the EE for the story of the topic</B></li>
	*<li><B>Step: Click in to add a comment and start typing "@xx" (make sure the letters are in a name in the system)</B></li>
	*<li><B>Step: When the typeahead appears click on a name</B></li>
	*<li><B>Step: Start typing another name with "@xx"</B></li>
	*<li><B>Step: When typeahead appears use the up/down arrows to select a user and click "Enter" when you get the user you want</B></li>
	*<li><B>Step: Attempt to edit either of the names that have been selected</B></li>
	*<li><B>Verify: Verify that you can scroll though the name with the mouse and when click the users name is entered</B></li>
	*<li><B>Verify: Verify that you can scroll through the names with the up/down arrow and when you press enter the users name is entered</B></li>
	*<li><B>Verify: Verify that the user cannot edit either of the names that were selected</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A97D260474C22F8585257CA700412897">TTT - @Mentions - EE - Forum Reply - 00002 - User can selected from typeahead</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void mentions_ee_forumTopicComment_userSelection_Typeahead() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the mentions and news story to be verified and clicked in the UI
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser1, profilesAPIUser1, serverURL, "", "");
		String forumTopicCreatedEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		// Type in a partial mention, select any user with the mouse and verify that the mention has posted correctly
		String selectedUserWithMouse = UIEvents.openEEAndTypeBeforeMentionsTextAndTypePartialMentionsAndSelectFirstTypeaheadMenuItem(ui, driver, forumTopicCreatedEvent, mentions, 2, false);
		
		// Verify that the selected user with mouse has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(selectedUserWithMouse, testUser1.getDisplayName().substring(0, 2));
		
		// Switch back to the comments / replies frame of the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, false);
		
		// Type in a second partial mention, select any user with the arrow keys and verify that the mention has posted correctly
		String selectedUserWithArrowKeys = UIEvents.typePartialMentionInEEAndSelectTypeaheadUserWithArrowKeys(ui, mentions, 2, false);
		
		// Verify that the selected user with arrow keys has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(selectedUserWithArrowKeys, testUser1.getDisplayName().substring(0, 2));
		
		// Delete all of the mentions links with the backspace key
		boolean allMentionsLinksDeleted = UIEvents.deleteTwoMentionsLinksWithBackspaceKey(driver, ui, selectedUserWithMouse, selectedUserWithArrowKeys);
		
		// Verify that all mentions links have been deleted successfully
		HomepageValid.verifyBooleanValuesAreEqual(allMentionsLinksDeleted, true);
		
		ui.endTest();
	}

	/**
	* mentions_ee_forumTopicComment_selectUser_closeTypeahead() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a topic created in a forum</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start adding a comment with @xxx</B></li>
	*<li><B>Step: Select a name from the dialog</B></li>
	*<li><B>Verify: Verify that the dialog closes when you select a name</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4304D1AF2FEB476385257CA700396FA0">TTT - @Mentions - EE - Forum Reply - 00010 - Clicking a name from the typeahead will close the dialog</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 1)
	public void mentions_ee_forumTopicComment_selectUser_closeTypeahead() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the mentions and news story to be verified and clicked in the UI
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser1, profilesAPIUser1, serverURL, "", "");
		String forumTopicCreatedEvent = ForumNewsStories.getCreateTopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		
		// Type in a partial mention, select any user with the mouse and verify that the mention has posted correctly
		String mentionedUser = UIEvents.openEEAndTypeBeforeMentionsTextAndTypePartialMentionsAndSelectFirstTypeaheadMenuItem(ui, driver, forumTopicCreatedEvent, mentions, 3, false);
		
		// Verify that the selected user has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(mentionedUser, testUser1.getDisplayName().substring(0, 3));
		
		// Return to the main frame before the next verification is carried out
		ui.switchToTopFrame();
		
		// Verify that the typeahead menu is no longer displayed after selecting a user
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{HomepageUIConstants.MentionsTypeaheadSelection}, null, false);
		
		ui.endTest();
	}

	/**
	* mentions_ee_forumTopicReply_highlightMention_delete() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a reply updated in a forum topic</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a reply and have a @mentioned user in it</B></li>
	*<li><B>Step: Select the user from the dialog</B></li>
	*<li><B>Step: Highlight the name that is in the comment box</B></li>
	*<li><B>Step: Press the delete key</B></li>
	*<li><B>Step: Add another mentioned user and highlight</B></li>
	*<li><B>Step: Press the delete key</B></li>
	*<li><B>Verify: Verify that the name is deleted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CFC28C50D69EB9385257CA700396FA3">TTT - @Mentions - EE - Forum Reply - 00013 - User can highlight name and press backspace or delete to delete name</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"}, priority = 2)
	public void mentions_ee_forumTopicReply_highlightMention_delete() {
		
		String testName = ui.startTest();
		
		// User 1 will now create and add a topic to the forum and post a reply and edit the reply
		BaseForumTopic testBaseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), publicForum);
		String forumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		String editedForumTopicReply = Data.getData().commonComment + Helper.genStrongRand();
		ForumEvents.createForumTopicAndAddReplyAndEditReply(testUser1, forumsAPIUser1, testBaseForumTopic, forumTopicReply, editedForumTopicReply);
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the EE for the forum topic reply updated event, enter the mentions, select the user from the typeahead, highlight the mentions link and delete it
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser1, profilesAPIUser1, serverURL, "", "");
		String forumReplyUpdatedEvent = ForumNewsStories.getReplyToYourTopicNewsStory_You(ui, testBaseForumTopic.getTitle(), baseForum.getName());
		boolean deletedWithHighlightAndDelete = UIEvents.openEEAndTypeBeforeMentionsTextAndTypeMentionAndSelectMentionedUserAndHighlightAndDeleteMention(ui, driver, mentions, forumReplyUpdatedEvent, false);
		
		// Verify that the highlight and delete action completed successfully
		HomepageValid.verifyBooleanValuesAreEqual(deletedWithHighlightAndDelete, true);
		
		// Now add another mentions and delete it using the backspace key
		boolean deletedWithBackspace = UIEvents.typeMentionInEEAndSelectMentionedUserAndDeleteMentions(ui, driver, mentions, false);
		
		// Verify that the delete with backspace action completed successfully
		HomepageValid.verifyBooleanValuesAreEqual(deletedWithBackspace, true);
		
		ui.endTest();
	}
}