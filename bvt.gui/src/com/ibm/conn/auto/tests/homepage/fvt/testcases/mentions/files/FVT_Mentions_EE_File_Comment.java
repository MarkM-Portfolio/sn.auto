package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

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

public class FVT_Mentions_EE_File_Comment extends SetUpMethodsFVT {
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private FileEntry publicFile;
	private FilesUI uiFiles;
	private User testUser1, testUser2;
				
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiFiles = FilesUI.getGui(cfg.getProductName(),driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will upload a new public file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
	
		uiFiles = FilesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	* mentions_ee_fileComment_twoCharactersTypeahead() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start to add a comment to the entry with and add @xx</B></li>
	*<li><B>Verify: Verify that this brings up a dialog with names that match and email</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2F6F7630E43BA02C85257C6D0051D6DF">TTT - @Mentions - EE - File Comment - 00001 - Typeahead should appear when at least 2 characters have been entered</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_ee_fileComment_twoCharactersTypeahead() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a partial mention to the comments input field and retrieve the list of users from the typeahead
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		ArrayList<String> typeaheadList = FileEvents.openFileOverlayAndTypePartialMentionAndGetTypeaheadMenuTextContents(ui, driver, uiFiles, fileUploadedEvent, mentions, 2);
		
		for(String userText : typeaheadList) {
			
			// Remove all unwanted characters from the user text (necessary step on SC)
			userText = userText.replaceAll("\n", " ").trim();
			
			// Verify that the found user text contains with the same 2 letters as the partial mention used in the comment input field
			HomepageValid.verifyStringContainsSubstring(userText, mentions.getUserToMention().getDisplayName().substring(0, 2));
			
			// Retrieve the email for this user from the text
			String userEmail = userText.substring(userText.lastIndexOf(" ") + 1);
			
			/**
			 *  Find where the numerical value begins in the email address (eg. in ajones123, the numerical "123" begins at index position 6)
			 *  
			 *  PLEASE NOTE: 	The values 47 and 58, used in the loop below, are the decimal number representations of ASCII characters.
			 *  				In this case, all numerical values from '0' to '9' are within the decimal ASCII range of 48 to 57 inclusive.
			 *  				I am therefore asking the loop to begin at position 0 in the string and work forwards until it finds the first character
			 *  				that is inside of that decimal range (and therefore a numerical character).
			 */
			int index = 0;
			while(userEmail.charAt(index) <= 47 || userEmail.charAt(index) >= 58) {
				index ++;
			}
			
			// Retrieve the user name from the email address (eg. "ajones" for OnPrem or "rory" for I1)
			String userNameFromEmail = userEmail.substring(0, index);
			
			// Convert the retrieved user name into the correct format
			String userNameToCheckFor;
			if(userNameFromEmail.equals("ajones")) {
				/**
				 * Here, we set the user name to "Amy Jones" and also add the numerical value onto the end of the string
				 * to make the actual OnPrem user name, such as "Amy Jones123". This proves that the user names and emails match in On Premise.
				 */
				userNameToCheckFor = "Amy Jones" + userEmail.substring(index, userEmail.indexOf("@"));
			} else {
				/**
				 * SC emails contain the first name of the user, so we convert the first character from the email to upper case and add a space to the end of the user
				 * name to make the actual I1 user name, such as "Rory " (we don't need to check for the surnames as the first names are currently 100% unique).
				 * 
				 * In the case of user names like "anna-diana", we need to convert both the "a" and "d" characters to upper case.
				 */
				char firstChar = Character.toUpperCase(userNameFromEmail.charAt(0));
				int indexOfHyphen = userNameFromEmail.indexOf('-');
				if(indexOfHyphen == -1) {
					userNameToCheckFor = "" + firstChar + userNameFromEmail.substring(1) + " ";
				} else {
					char secondUpperChar = Character.toUpperCase(userNameFromEmail.charAt(indexOfHyphen + 1));
					userNameToCheckFor = "" + firstChar + userNameFromEmail.substring(1, indexOfHyphen + 1) + secondUpperChar + userNameFromEmail.substring(indexOfHyphen + 2);
				}
			}
			// Verify that the found user text contains the user name from the email address (ie. user name and email addresses match) at String index position 0
			HomepageValid.verifyIntValuesAreEqual(userText.indexOf(userNameToCheckFor), 0);
		}
		ui.endTest();
	}
	
	/**
	* mentions_ee_fileComment_selectUser_closeTypeahead() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Start adding a comment with @xxx</B></li>
	*<li><B>Step: Select a name from the dialog</B></li>
	*<li><B>Verify: Verify that the dialog closes when you select a name</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4B5A1E4931E0DED585257C6D0055CC22">TTT - @Mentions - EE - File Comment - 00010 - Clicking a name from the typeahead will close the dialog</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_ee_fileComment_selectUser_closeTypeahead() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a partial mention to the comments input field and select the first (random) user from the typeahead
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		String mentionedUser = FileEvents.openFileOverlayAndTypePartialMentionAndSelectFirstTypeaheadMenuItem(ui, driver, uiFiles, fileUploadedEvent, mentions, 3);
		
		// Verify that the selected user has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(mentionedUser, mentions.getUserToMention().getDisplayName().substring(0, 3));
		
		// Return to the main frame before the next verification is carried out
		ui.switchToTopFrame();
		
		// Verify that the typeahead menu is no longer displayed after selecting a user
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{HomepageUIConstants.MentionsTypeaheadSelection}, null, false);
		
		ui.endTest();
	}

	/**
	* mentions_ee_fileComment_selectUser_mentionLinkified() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a comment and have a @mentioned user in it</B></li>
	*<li><B>Step: Select the user from the dialog</B></li>
	*<li><B>Verify: Verify that when the user has been select it appears in blue "@user"</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DE6BCED0F6B304D085257C6D00561A3C">TTT - @Mentions - EE - File Comment - 00011 - When name is added it appears in blue</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_ee_fileComment_selectUser_mentionLinkified() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a mention to the comments input field and select the relevant user from the typeahead
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		/**
		 * This test case requires an assertion that the link to the mentioned user name appears in blue
		 * 
		 * This is too ambiguous for Selenium since the link itself returns an rgba value - even if the values for the rgb parameters
		 * were to change slightly (and still appear blue in the UI), Selenium would register a failure due to them not matching the
		 * exact blue we would need to hard code into any assertion for an exact colour match.
		 * 
		 * Because of this, we are instead asserting that the mention was entered correctly. The method used below already asserts that the
		 * correct user is in the typeahead menu, is selected correctly and that the CSS "link" selector is visible in the comments box after selecting the user
		 */
		boolean mentionsSuccessful = FileEvents.openFileOverlayAndTypeMentionAndSelectMentionedUser(ui, driver, uiFiles, fileUploadedEvent, mentions);
		HomepageValid.verifyBooleanValuesAreEqual(mentionsSuccessful, true);
		
		ui.endTest();
	}

	/**
	* mentions_ee_fileComment_backspaceKey_deleteMention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a comment and have a @mentioned user in it</B></li>
	*<li><B>Step: Select the user from the dialog</B></li>
	*<li><B>Step: Press backspace</B></li>
	*<li><B>Verify: Verify that the entire name is deleted and not just an individual character</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A6ABABC7DD747C6585257C6D00567B98">TTT - @Mentions - EE - File Comment - 00012 - If use back space after a @mentioned name deletes the entire name</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_ee_fileComment_backspaceKey_deleteMention() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a mention to the comments input field, select the relevant user from the typeahead and then delete the mention
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		/**
		 * This test case requires an assertion that the link to the mentioned user name (and their entire user name) is removed after pressing backspace
		 * 
		 * The method invoked to enter and then remove the mentioned user from the input box already asserts for this. Therefore the only assertion in
		 * this test case is to ensure that everything completes successfully. 
		 */
		boolean mentionDeleted = FileEvents.openFileOverlayAndTypeMentionAndSelectMentionedUserAndDeleteMention(ui, driver, uiFiles, fileUploadedEvent, mentions);
		HomepageValid.verifyBooleanValuesAreEqual(mentionDeleted, true);
		
		ui.endTest();
	}

	/**
	* mentions_ee_fileComment_deleteKey_deleteMention() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to the event of a file uploaded</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Step: Add a comment and have a @mentioned user in it</B></li>
	*<li><B>Step: Select the user from the dialog</B></li>
	*<li><B>Step: Highlight the name that is in the sharebox</B></li>
	*<li><B>Step: Press the delete key</B></li>
	*<li><B>Verify: Verify that the name is deleted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/D2D3EAD066A5EDFE85257C6D0056DA41">TTT - @Mentions - EE - File Comment - 00013 - User can highlight name and press backspace or delete to delete name</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_ee_fileComment_deleteKey_deleteMention() {

		ui.startTest();
		
		// Log in as User 1 and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Open the file details overlay for the file, add a mention to the comments input field, select the relevant user from the typeahead and then delete the mention
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		String fileUploadedEvent = FileNewsStories.getUploadFileNewsStory(ui, testUser1.getDisplayName());
		
		/**
		 * This test case requires an assertion that the link to the mentioned user name (and their entire user name) is removed after highlighting it and pressing delete
		 * 
		 * The method invoked to enter, highlight and then remove the mentioned user from the input box already asserts for this. Therefore the only assertion in
		 * this test case is to ensure that everything completes successfully. 
		 */
		boolean mentionDeleted = FileEvents.openFileOverlayAndTypeMentionAndSelectMentionedUserAndHighlightAndDeleteMention(ui, driver, uiFiles, fileUploadedEvent, mentions);
		HomepageValid.verifyBooleanValuesAreEqual(mentionDeleted, true);
		
		ui.endTest();
	}
}