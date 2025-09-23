package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.files;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
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
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

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
 * [2 last comments appear inline] FVT UI Automation for Story 146317
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/149989
 * @author Patrick Doherty
 */

public class FVT_InlineComments_CommunityFileEvents extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser3;
	private APIFileHandler filesAPIUser1, filesAPIUser2, filesAPIUser3, filesAPIUser4;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2, testUser3, testUser4;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(4);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		filesAPIUser2 = initialiseAPIFileHandlerUser(testUser2);
		filesAPIUser3 = initialiseAPIFileHandlerUser(testUser3);
		filesAPIUser4 = initialiseAPIFileHandlerUser(testUser4);

		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser3 = initialiseAPICommunitiesHandlerUser(testUser3);
		
		// User 1 will now create a public community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(basePublicCommunity, testUser1, communitiesAPIUser1);
		
		// User 2 (acting as User 1) will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser2, communitiesAPIUser2);
		
		// User 3 (acting as User 1) will now create a restricted community with User 4 (acting as User 2) added as a member
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseRestrictedCommunity, testUser3, communitiesAPIUser3, testUser4);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser2.deleteCommunity(moderatedCommunity);
		communitiesAPIUser3.deleteCommunity(restrictedCommunity);
	}

	/**
	* test_FileComment_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a file</B></li>
	*<li><B>Step: User 2 comment on the file and like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add another 6 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the reply is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 replies are shown inline in the view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/293681B5428E8B9C85257E2F0036A45F">TTT - INLINE COMMENTS - 00050 - FILE EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_FileComment_PublicCommunity(){

		ui.startTest();
		
		// User 1 will now upload a public file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 2 will now comment on the community file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(publicCommunity, communityFile, user2Comment, testUser2, filesAPIUser2);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser1, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser2.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that User 2's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, true);
		}
		// User 2 will now post a further 6 comments to the community file
		String user2Comments[] = new String[6];
		for(int index = 0; index < user2Comments.length; index ++) {
			// Set the comment to be posted to the community file
			user2Comments[index] = Data.getData().commonComment + Helper.genStrongRand();
			
			// User 2 will now post the comment to the community file
			CommunityFileEvents.addComment(publicCommunity, communityFile, user2Comments[index], testUser2, filesAPIUser2);
		}
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the last 2 comments posted by User 2 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[4], user2Comments[5]}, null, true);
			
			// Verify that all other comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_FileComment_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a file</B></li>
	*<li><B>Step: User 2 comment on the file and like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add another 6 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the reply is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 replies are shown inline in the view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/293681B5428E8B9C85257E2F0036A45F">TTT - INLINE COMMENTS - 00050 - FILE EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_FileComment_ModCommunity(){
		
		/**
		 * To prevent 409: CONFLICT errors when posting comments, this test case will use User 2 (as User 1) and User 3 (as User 2)
		 */
		ui.startTest();
		
		// User 1 will now upload a public file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser2, filesAPIUser2);
		
		// User 2 will now comment on the community file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(moderatedCommunity, communityFile, user2Comment, testUser3, filesAPIUser3);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser3.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that User 2's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, true);
		}
		// User 2 will now post a further 6 comments to the community file
		String user2Comments[] = new String[6];
		for(int index = 0; index < user2Comments.length; index ++) {
			// Set the comment to be posted to the community file
			user2Comments[index] = Data.getData().commonComment + Helper.genStrongRand();
			
			// User 2 will now post the comment to the community file
			CommunityFileEvents.addComment(moderatedCommunity, communityFile, user2Comments[index], testUser3, filesAPIUser3);
		}
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the last 2 comments posted by User 2 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[4], user2Comments[5]}, null, true);
			
			// Verify that all other comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}

	/**
	* test_FileComment_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 upload a file</B></li>
	*<li><B>Step: User 2 comment on the file and like the file</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 1</B></li>
	*<li><B>Step: User 2 add another 6 comments</B></li>
	*<li><B>Step: User 1 go to Homepage / My Notifications / For Me - verification point 2</B></li>
	*<li><B>Verify: Verify the reply is shown inline in the view</B></li>
	*<li><B>Verify: Verify the last 2 replies are shown inline in the view</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/293681B5428E8B9C85257E2F0036A45F">TTT - INLINE COMMENTS - 00050 - FILE EVENTS IN MY NOTIFICATIONS VIEW</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void test_FileComment_PrivateCommunity(){

		/**
		 * To prevent 409: CONFLICT errors when posting comments, this test case will use User 3 (as User 1) and User 4 (as User 2)
		 */
		ui.startTest();
		
		// User 1 will now upload a public file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser3, filesAPIUser3);
		
		// User 2 will now comment on the community file
		String user2Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityFileEvents.addComment(restrictedCommunity, communityFile, user2Comment, testUser4, filesAPIUser4);
		
		// Log in as User 1 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser3, false);
		
		// Create the news story to be verified
		String commentOnFileEvent = CommunityFileNewsStories.getCommentOnYourFileNewsStory_User(ui, testUser4.getDisplayName());
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that User 2's comment is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment}, null, true);
		}
		// User 2 will now post a further 6 comments to the community file
		String user2Comments[] = new String[6];
		for(int index = 0; index < user2Comments.length; index ++) {
			// Set the comment to be posted to the community file
			user2Comments[index] = Data.getData().commonComment + Helper.genStrongRand();
			
			// User 2 will now post the comment to the community file
			CommunityFileEvents.addComment(restrictedCommunity, communityFile, user2Comments[index], testUser4, filesAPIUser4);
		}
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS
			UIEvents.clickShowMore(ui);
			
			// Verify that the comment on file event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, commentOnFileEvent, baseFile);
			
			// Verify that the last 2 comments posted by User 2 are displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comments[4], user2Comments[5]}, null, true);
			
			// Verify that all other comments posted by User 2 are NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2Comment, user2Comments[0], user2Comments[1], user2Comments[2], user2Comments[3]}, null, false);
		}
		ui.endTest();
	}
}