package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files.standalone;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
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

public class FVT_Mentions_FileComment_Standalone_SameOrg extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };

	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseFile basePublicFile, basePrivateFile;
	private FileEntry privateFile, publicFile;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a public file
		basePublicFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(basePublicFile, testUser1, filesAPIUser1);
		
		// User 1 will now create a private file
		basePrivateFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.NO_ONE);
		privateFile = FileEvents.addFile(basePrivateFile, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the files created during the test
		filesAPIUser1.deleteFile(publicFile);
		filesAPIUser1.deleteFile(privateFile);
	}

	/**
	* mentions_publicFile_comment_discoverView_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Files</B></li>
	*<li><B>Step: testUser1 go to a text file with public access</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions to testUser2 to this file</B></li>
	*<li><B>Step: testUser3 log into Homepage as a user in the same organisation</B></li>
	*<li><B>Step: testUser3 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Verify: Verify that there is a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B01BBFD5ECF3263285257C70003B482C ">TTT - DISCOVER - FILES - 00110 - FILE COMMENT WITH MENTIONS - STANDALONE PUBLIC - SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_publicFile_comment_discoverView_sameOrg() {

		ui.startTest();
		
		// User 1 will now post a comment to the file with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, publicFile, mentions);
				
		// Log in as User 3 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, basePublicFile);
						
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		}
		ui.endTest();
	}

	/**
	* mentions_privateFile_comment_discoverView_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Files</B></li>
	*<li><B>Step: testUser1 go to a text file with private access</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage as a user in the same organisation</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Verify: Verify that there is NOT a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/CAEABC96D296AC0F85257C70003B482D">TTT - DISCOVER - FILES - 00111 - FILE COMMENT WITH MENTIONS - STANDALONE PRIVATE - SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void mentions_privateFile_comment_discoverView_sameOrg() {

		ui.startTest();
		
		// User 1 will now post a comment to the file with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, privateFile, mentions);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, commentOnFileEvent, basePrivateFile);
						
			// Verify that the mentions text is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, false);
		}
		ui.endTest();
	}
}