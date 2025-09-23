package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files.standalone;

import java.util.HashMap;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014, 2016                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_FileComment_Standalone_DiffOrg extends SetUpMethods2{
	
	private final String TEST_FILTERS[] = {HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles};
	
	private APIFileHandler filesAPIUser1, filesAPIUser2;
	private APIProfilesHandler profilesAPIUser3;
	private HashMap<FileEntry, APIFileHandler> filesForDeletion = new HashMap<FileEntry, APIFileHandler>();
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser4 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		filesAPIUser1 = new APIFileHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		filesAPIUser2 = new APIFileHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest(){
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Remove all of the files created during the tests
		Set<FileEntry> setOfFiles = filesForDeletion.keySet();
		
		for(FileEntry fileEntry : setOfFiles) {
			filesForDeletion.get(fileEntry).deleteFile(fileEntry);
		}
	}
	
	/**
	* mentions_publicFile_comment_discoverView_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Files</B></li>
	*<li><B>Step: testUser1 go to a text file with public access</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions to this file</B></li>
	*<li><B>Step: testUser2 log into Homepage as a user in a different organisation</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Verify: Verify that there is NOT a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/E054A6B2FB84641C85257C70003B4831">TTT - DISCOVER - FILES - 00120 - FILE COMMENT WITH A MENTIONS - STANDALONE PUBLIC - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtdifforg"})
	public void mentions_publicFile_comment_discoverView_diffOrg(){

		ui.startTest();

		// User 1 will now upload a public file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		filesForDeletion.put(fileEntry, filesAPIUser1);
		
		// User 1 will add a comment mentioning User 3 who is in the same organisation
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser1, filesAPIUser1, fileEntry, mentions);

		// User 4 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser4, false);

		// Create the news stories to be verified
		String commentEvent = ui.replaceNewsStory(Data.FILE_COMMENTED_OWN_FILE, null, null, testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser3.getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, mentionsText}, TEST_FILTERS, false);

		// Delete the file and remove from the HashMap
		filesAPIUser2.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}

	/**
	* mentions_privateFile_comment_discoverView_diffOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log into Files</B></li>
	*<li><B>Step: testUser1 go to a text file with private access</B></li>
	*<li><B>Step: testUser1 add a comment with a mentions</B></li>
	*<li><B>Step: testUser2 log into Homepage as a user in a different organisation</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All & Files</B></li>
	*<li><B>Verify: Verify that there is NOT a mentions event in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DCE677C55CD1DF5C85257C70003B4832">TTT - DISCOVER - FILES - 00121 - FILE COMMENT WITH A MENTIONS - STANDALONE PRIVATE - DIFFERENT ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtdifforg"})
	public void mentions_privateFile_comment_discoverView_diffOrg(){

		ui.startTest();

		// User 2 will now upload a private file
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.PEOPLE, profilesAPIUser3);
		FileEntry fileEntry = FileEvents.addFile(baseFile, testUser2, filesAPIUser2);
		filesForDeletion.put(fileEntry, filesAPIUser2);
		
		// User 2 will add a comment mentioning User 3 who is in the same organisation
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		FileEvents.addFileMentionsComment(testUser2, filesAPIUser2, fileEntry, mentions);

		// User 4 log in and go to Discover
		LoginEvents.loginAndGotoDiscover(ui, testUser4, false);

		// Create the news stories to be verified
		String commentEvent = ui.replaceNewsStory(Data.FILE_COMMENTED_OWN_FILE, null, null, testUser2.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + testUser3.getDisplayName() + " " + mentions.getAfterMentionText();

		// Verify the news story does NOT appear in any view
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentEvent, mentionsText}, TEST_FILTERS, false);
		
		//Delete the file and remove from the HashMap
		filesAPIUser2.deleteFile(fileEntry);
		filesForDeletion.remove(fileEntry);
		ui.endTest();
	}
}