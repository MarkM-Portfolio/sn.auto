package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.assearch;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.ActivityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.activities.ActivityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityWikiEvents;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.activities.ActivityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityWikiNewsStories;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

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

public class FVT_AS_Search_Events extends SetUpMethodsFVT {
	
	private APIActivitiesHandler activitiesAPIUser5;
	private Activity privateActivity;
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser4;
	private APIFileHandler filesAPIUser3;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private APIWikisHandler wikisAPIUser4;
	private BaseActivity baseActivity;
	private BaseCommunity basePublicCommunityUser1, basePublicCommunityUser2, basePublicCommunityUser4, baseRestrictedCommunity;
	private BaseFile baseFile;
	private Community publicCommunityUser1, publicCommunityUser2, publicCommunityUser4, restrictedCommunity;
	private FileEntry publicFile;
	private String statusUpdateUser1, statusUpdateUser1Id, statusUpdate1User2, statusUpdate1User2Id, statusUpdate2User2, testText;
	private User testUser1, testUser2, testUser3, testUser4, testUser5;
	private Wiki communityWikiUser4;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
				
		setListOfStandardUsers(5);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		
		activitiesAPIUser5 = initialiseAPIActivitiesHandlerUser(testUser5);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser4 = initialiseAPICommunitiesHandlerUser(testUser4);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		filesAPIUser3 = initialiseAPIFileHandlerUser(testUser3);
		
		wikisAPIUser4 = initialiseAPIWikisHandlerUser(testUser4);
		
		// Set the String to be used in multiple components in one of the tests (also required for creating some of the components in this BeforeClass method)
		testText = "This is an AS Search test";
		
		// User 4 will now create a public community and will also add the wikis widget to the community
		basePublicCommunityUser4 = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityUser4 = CommunityEvents.createNewCommunityAndAddWidget(basePublicCommunityUser4, BaseWidget.WIKI, isOnPremise, testUser4, communitiesAPIUser4);
				
		// User 1 will now create a private community with User 2 added as a member and follower
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMemberAndOneFollower(baseRestrictedCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community
		basePublicCommunityUser1 = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityUser1 = CommunityEvents.createNewCommunity(basePublicCommunityUser1, testUser1, communitiesAPIUser1);
		
		// User 2 will now create a public community
		basePublicCommunityUser2 = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunityUser2 = CommunityEvents.createNewCommunity(basePublicCommunityUser2, testUser2, communitiesAPIUser2);	
		
		// User 3 will now upload a public file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser3, filesAPIUser3);
		
		// User 5 will now create a private activity
		baseActivity = ActivityBaseBuilder.buildBaseActivityWithCustomGoal(getClass().getSimpleName() + Helper.genStrongRand(), testText, true);
		privateActivity = ActivityEvents.createActivity(testUser5, activitiesAPIUser5, baseActivity, isOnPremise);
		
		// User 1 will now post a status update to their profile
		statusUpdateUser1 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdateUser1Id = ProfileEvents.addStatusUpdate(profilesAPIUser1, statusUpdateUser1);
		
		// User 2 will now post a status update to their profile
		statusUpdate1User2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		statusUpdate1User2Id = ProfileEvents.addStatusUpdate(profilesAPIUser2, statusUpdate1User2);
		
		/**
		 * Set the content for User 2's second status update - this can be then verified as present / absent in multiple tests (where necessary)
		 * User 2 posts this status update using the UI during one of the tests - therefore it cannot be posted and / or deleted using an API
		 */
		statusUpdate2User2 = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		
		// Retrieve the Wiki instance of the community wiki for User 4's public community
		communityWikiUser4 = CommunityWikiEvents.getCommunityWiki(publicCommunityUser4, wikisAPIUser4);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunityUser1);
		communitiesAPIUser2.deleteCommunity(publicCommunityUser2);
		communitiesAPIUser4.deleteCommunity(publicCommunityUser4);
		
		// Delete the activity created during the test
		activitiesAPIUser5.deleteActivity(privateActivity);
		
		// Delete the file created during the test
		filesAPIUser3.deleteFile(publicFile);
		
		// Delete all of the status updates created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateUser1Id);
		profilesAPIUser2.deleteBoardMessage(statusUpdate1User2Id);
	}

	/**
	* asSearch_discoverView_imFollowingAndPublicContent() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: testUser1 create a private community adding testUser2 as a member</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 add a status update the text 'this is a test'</B></li>
	*<li><B>Step: testUser3 upload a public file</B></li>
	*<li><B>Step: testUser3 comment on the file with the text 'this is a test'</B></li>
	*<li><B>Step: testUser4 start a public community and add a wiki page with the text 'this is a test'</B></li>
	*<li><B>Step: testUser5 add a private activity with the text 'this is a test'</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: testUser2 search for the text 'this is a test'</B></li>
	*<li><B>Verify: Verify that the community update, comment on the file, and wiki page events are returned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5E8FE56B8CEC942B85257C350052F9BC">TTT - AS SEARCH - 00033 - DISCOVER VIEW IS RETURNING CONTENT YOU ARE FOLLOWING AND PUBLIC CONTENT</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_discoverView_imFollowingAndPublicContent() {

		String testName = ui.startTest();
		
		// User 1 will now post a status message to the restricted community
		CommunityEvents.addStatusUpdate(restrictedCommunity, communitiesAPIUser1, profilesAPIUser1, testText);
			
		// User 3 will now comment on the public file
		FileEvents.addFileComment(testUser3, filesAPIUser3, publicFile, testText);
				
		// User 4 will now create a wiki page in the public community
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPageWithCustomDescription(testName + Helper.genStrongRand(), testText);
		CommunityWikiEvents.createWikiPage(communityWikiUser4, baseWikiPage, testUser4, wikisAPIUser4);
				
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// User 2 will now use the AS search to search for all events matching to the "This is a test" text
		UIEvents.searchUsingASSearch(ui, testUser2, testText);
		
		// Create the news stories to be verified
		String statusUpdateInPrivateCommunityEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		String commentOnPublicFileEvent = FileNewsStories.getCommentOnTheirOwnFileNewsStory(ui, testUser3.getDisplayName());
		String createPublicCommunityWikiPageEvent = CommunityWikiNewsStories.getCreateWikiPageNewsStory(ui, baseWikiPage.getName(), basePublicCommunityUser4.getName(), testUser4.getDisplayName());
		String createPrivateActivityEvent = ActivityNewsStories.getCreateActivityNewsStory(ui, baseActivity.getName(), testUser5.getDisplayName());
		
		// Verify that the events for User 3 commenting on their public file and User 4 creating the public community are displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnPublicFileEvent, createPublicCommunityWikiPageEvent}, null, true);
		
		// Verify that the events for User 1 posting a message to the private community and User 5 creating the private activity are NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdateInPrivateCommunityEvent, createPrivateActivityEvent}, null, false);
				
		ui.endTest();
	}
	
	/**
	* asSearch_userName_discoverView() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: Open the Search UI</B></li>
	*<li><B>Step: Add in a username that appears in the stream</B></li>
	*<li><B>Verify: Verify the event from that user is returned</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4991E8240AEFADCE85257C3500534D62">TTT - AS SEARCH - 00035 - SEARCH CAN BE DONE ON USERNAME</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_userName_discoverView() {
		
		ui.startTest();
		
		// Log in as User 3 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// User 3 will now use the AS search to search for all events matched to User 1's user name
		UIEvents.searchUsingASSearch(ui, testUser3, testUser1.getDisplayName());
		
		// Verify that User 1's status update event is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdateUser1}, null, true);
		
		/**
		 * Verify that User 2's status update events are NOT displayed in the AS Search results
		 * 
		 * Depending on the order of execution of these tests - User 2's second status update may have been posted by the time this verification takes place
		 * Either way - the status update should NOT be displayed. For completeness, this verification includes verifying that User 2's second status update
		 * is NOT displayed in the AS Search results.
		 */
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate1User2, statusUpdate2User2}, null, false);
		
		ui.endTest();
	}
	
	/**
	* asSearch_statusUpdate_SearchCancelled() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: Open the Search UI</B></li>
	*<li><B>Step: Search for something to bring up results</B></li>
	*<li><B>Step: Add a status update</B></li>
	*<li><B>Step: Close the search UI</B></li>
	*<li><B>Verify: Verify the status does not appear in the view while the Search results are returned</B></li>
	*<li><B>Verify: Verify the status update is in the re-rendered stream after the Search results are dismissed</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1B3693F996FF73DC85257C350053BB82">TTT - AS SEARCH - 00043 - ADDING STATUS UPDATE WHILE IN SEARCH RESULTS WILL NOT APPEAR TILL SEARCH IS CANCELLED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_statusUpdate_SearchCancelled() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// User 2 will now use the AS search to search for all events matched to User 1's status update
		UIEvents.searchUsingASSearch(ui, testUser2, statusUpdateUser1);
		
		// Verify that User 1's status update is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdateUser1}, null, true);
		
		// User 2 will now post a status update using the UI
		ProfileEvents.addStatusUpdateUsingUI(ui, testUser2, statusUpdate2User2, false);
		
		// Verify that User 1's status update is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdateUser1}, null, true);
		
		// Verify that User 2's newly posted status update is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate2User2}, null, false);
		
		// Close the AS Search panel
		UIEvents.cancelASSearchUsingMagnifyingGlassIcon(ui);
		
		// Verify that User 1's and User 2's status updates are now displayed in the AS
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdateUser1, statusUpdate2User2}, null, true);
		
		ui.endTest();
	}

	/**
	* asSearch_event_SearchCancelled() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Homepage</B></li>
	*<li><B>Step: Go to Homepage / Updates / Discover / All</B></li>
	*<li><B>Step: On each of the views click the search icon</B></li>
	*<li><B>Step: Add in some text that appears in an event in that view</B></li>
	*<li><B>Step: Click the Search icon at the end of the input box</B></li>
	*<li><B>Step: Ensure the event is returned that you are searching for</B></li>
	*<li><B>Step: Click the "X"</B></li>
	*<li><B>Verify: Verify the full stream is returned as normal</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/12D0EABC446CBDBB85257C350053BB81">TTT - AS SEARCH - 00042 - DISCOVER VIEW SEARCH CANCELLED BRINGS BACK STREAM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void asSearch_event_SearchCancelled() {
		
		ui.startTest();
		
		// Log in as User 3 and navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser3, false);
		
		// Create the news stories to be verified
		String user1CreateCommunityEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, basePublicCommunityUser1.getName(), testUser1.getDisplayName());
		String user2CreateCommunityEvent = CommunityNewsStories.getCreateCommunityNewsStory(ui, basePublicCommunityUser2.getName(), testUser2.getDisplayName());
		
		// Before searching via the AS Search panel - verify that all events are displayed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1CreateCommunityEvent, user2CreateCommunityEvent}, null, true);
		
		// User 3 will now use the AS search to search for all events matched to User 1's user name
		UIEvents.searchUsingASSearch(ui, testUser3, testUser1.getDisplayName());
		
		// Verify that User 1's create community event is displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1CreateCommunityEvent}, null, true);
		
		// Verify that User 2's create community event is NOT displayed in the AS Search results
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user2CreateCommunityEvent}, null, false);
		
		// Close the AS Search panel
		UIEvents.cancelASSearchUsingXIcon(ui);
		
		// Verify that all events are displayed in the AS now that the AS Search panel has been closed
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1CreateCommunityEvent, user2CreateCommunityEvent}, null, true);
		
		ui.endTest();
	}
}