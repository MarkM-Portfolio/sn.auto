package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.eecomment;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                    */
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

public class FVT_EEComment_CommunityFiles extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3;
	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private FilesUI filesUI;
	private User testUser1, testUser2, testUser3;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		filesAPIUser3 = initialiseAPIFileHandlerUser(testUser3);
		
		// User 1 will now create a public community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(basePublicCommunity, testUser1, communitiesAPIUser1);
		
		// User 2 (acting as User 1) will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser2, communitiesAPIUser2);
		
		// User 3 (acting as User 1) will now create a restricted community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseRestrictedCommunity, testUser3, communitiesAPIUser3);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
		communitiesAPIUser3.deleteCommunity(restrictedCommunity);
	}

	/**
	* fileOverlayComment_PublicCommunity_FileShared() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a public community</B></li>
	*<li><B>Step: Share a file with the community</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Files</B></li>
	*<li><B>Step: Open the file overlay for the public community file shared news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the file overlay</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayComment_PublicCommunity_FileShared() {

		ui.startTest();
		
		// User 1 will now share a file with the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, basePublicCommunity.getName(), testUser1.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, fileSharedEvent);
				
		// Post a comment to the file using the file details overlay
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentUsingUI(ui, filesUI, testUser1, user1Comment);
				
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
		
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the comment on file event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* fileOverlayComment_ModCommunity_FileShared() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a moderated community</B></li>
	*<li><B>Step: Share a file with the community</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Files</B></li>
	*<li><B>Step: Open the file overlay for the moderated community file shared news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the file overlay</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayComment_ModCommunity_FileShared(){

		/**
		 * To avoid duplicate "comment event" news stories appearing in the AS - this test case will use User 2 as User 1
		 */
		ui.startTest();
		
		// User 1 will now share a file with the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser2, filesAPIUser2);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be used to open the EE
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseModeratedCommunity.getName(), testUser2.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, fileSharedEvent);
				
		// Post a comment to the file using the file details overlay
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentUsingUI(ui, filesUI, testUser2, user1Comment);
				
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
		
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the comment on file event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* fileOverlayComment_PrivateCommunity_FileShared() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Create a private community</B></li>
	*<li><B>Step: Share a file with the community</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / Files</B></li>
	*<li><B>Step: Open the file overlay for the private community file shared news story</B></li>
	*<li><B>Step: Add a comment</B></li>
	*<li><B>Step: Click "Post"</B></li>
	*<li><B>Verify: Verify that the comment appears in the file overlay</B></li>
	*<li><B>Verify: Verify that the comment appears in the Activity Stream</B></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayComment_PrivateCommunity_FileShared(){

		/**
		 * To avoid duplicate "comment event" news stories appearing in the AS - this test case will use User 3 as User 1
		 */
		ui.startTest();
		
		// User 1 will now share a file with the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser3, filesAPIUser3);
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser3, false);
		
		// Create the news story to be used to open the EE
		String fileSharedEvent = CommunityFileNewsStories.getShareFileWithCommunityNewsStory(ui, baseRestrictedCommunity.getName(), testUser3.getDisplayName());
		
		// Open the file details overlay
		FileEvents.openFileOverlay(ui, fileSharedEvent);
				
		// Post a comment to the file using the file details overlay
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		FileEvents.addFileCommentUsingUI(ui, filesUI, testUser3, user1Comment);
				
		// Close the file details overlay
		FileEvents.closeFileOverlay(ui);
		
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnYourFileNewsStory_You(ui);
		
		// Refresh the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the comment on file event and User 1's comment are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnFileEvent, user1Comment}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}