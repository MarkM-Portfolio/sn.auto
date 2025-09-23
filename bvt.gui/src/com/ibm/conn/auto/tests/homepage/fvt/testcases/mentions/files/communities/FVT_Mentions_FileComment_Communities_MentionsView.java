package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.files.communities;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityFileNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016		                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_FileComment_Communities_MentionsView extends SetUpMethodsFVT {
	
	private final String TEST_FILTERS[] = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterFiles };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);

		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a public community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(basePublicCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser1, communitiesAPIUser1);
			
		// User 1 will now create a private community with User 2 added as a member (User 3 will be used for the non-member test)
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseRestrictedCommunity, testUser1, communitiesAPIUser1, testUser2);
	}

	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* fileComment_directedMention_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 upload a file</B></li>
	*<li><B>Step: testUser1 add a comment on the file mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify that the mentions event appears in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8884B44A1904A4CC85257C6F007EA41E">TTT - @MENTIONS - 063 - MENTIONS DIRECTED TO YOU IN A FILE COMMENT - PUBLIC COMMUNITY FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileComment_directedMention_publicCommunity(){

		ui.startTest();
		
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(publicCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now comment on the file with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(publicCommunity, communityFile, mentions, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionedYouFileCommentEvent = CommunityFileNewsStories.getMentionedYouInACommentOnAFile(ui, testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the mentioned in a file comment event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		}
		// User 2 navigates to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the mentioned in a file comment event is displayed in all views
		HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
		// Verify that the mentions text is displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		
		ui.endTest();
	}

	/**
	* fileComment_directedMention_modCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser1 upload a file</B></li>
	*<li><B>Step: testUser1 add a comment on the file mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify that the mentions event appears in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/51DD7EA52EB90E3885257C6F007EA41F">TTT - @MENTIONS - 064 - MENTIONS DIRECTED TO YOU IN A FILE COMMENT - MODERATE COMMUNITY FILE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileComment_directedMention_modCommunity(){

		ui.startTest();
		
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(moderatedCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now comment on the file with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(moderatedCommunity, communityFile, mentions, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionedYouFileCommentEvent = CommunityFileNewsStories.getMentionedYouInACommentOnAFile(ui, testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the mentioned in a file comment event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		}
		// User 2 navigates to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the mentioned in a file comment event is displayed in all views
		HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
		// Verify that the mentions text is displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		
		ui.endTest();
	}

	/**
	* fileComment_directedMention_privateCommunity_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community adding User 2 as a member</B></li>
	*<li><B>Step: testUser1 upload a file</B></li>
	*<li><B>Step: testUser1 add a comment on the file mentioning testUser2</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify that the mentions event appears in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/097BDA2D97D79F3B85257C6F007EA420">TTT - @MENTIONS - 065 - MENTIONS DIRECTED TO YOU IN A FILE COMMENT - PRIVATE COMMUNITY FILE - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileComment_directedMention_privateCommunity_member(){

		ui.startTest();
		
		/**
		 * In order for the mentions to User 2 to work correctly (given that this is a restricted community),
		 * it is necessary for User 2 to log in here and navigate to the community in the UI
		 */
		CommunityEvents.loginAndNavigateToCommunity(restrictedCommunity, baseRestrictedCommunity, ui, uiCo, testUser2, communitiesAPIUser2, false);
	
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now comment on the file with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(restrictedCommunity, communityFile, mentions, testUser1, filesAPIUser1);
		
		// Return to the Home screen and go to I'm Following
		UIEvents.gotoHomeAndGotoImFollowing(ui);
		
		// Create the news story to be verified
		String mentionedYouFileCommentEvent = CommunityFileNewsStories.getMentionedYouInACommentOnAFile(ui, testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the mentioned in a file comment event is displayed in all views
			HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
			// Verify that the mentions text is displayed in all views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		}
		// User 2 navigates to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the mentioned in a file comment event is displayed in all views
		HomepageValid.verifyFilesNewsStoryIsDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
		// Verify that the mentions text is displayed in all views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, true);
		
		ui.endTest();
	}

	/**
	* fileComment_directedMention_privateCommunity_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 upload a file</B></li>
	*<li><B>Step: testUser1 add a comment on the file mentioning testUser2 who is not a member of the community</B></li>
	*<li><B>Step: testUser2 go to Homepage / Mentions</B></li>
	*<li><B>Step: testUser2 go to Homepage / Updates / I'm Following / All & Files</B></li>
	*<li><B>Verify: Verify that the mentions event does NOT appear in the views</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/F2DE5563B2A3941485257C6F007EA421">TTT - @MENTIONS - 066 - MENTIONS DIRECTED TO YOU IN A FILE COMMENT - PRIVATE COMMUNITY FILE - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void fileComment_directedMention_privateCommunity_nonMember(){

		ui.startTest();
		
		// User 1 will now add a file to the community
		BaseFile baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file2, ".jpg", ShareLevel.EVERYONE);
		FileEntry communityFile = CommunityFileEvents.addFile(restrictedCommunity, baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now comment on the file with mentions to User 3
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityFileEvents.addCommentWithMentions(restrictedCommunity, communityFile, mentions, testUser1, filesAPIUser1);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionedYouFileCommentEvent = CommunityFileNewsStories.getMentionedYouInACommentOnAFile(ui, testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		for(String filter : TEST_FILTERS) {
			// Filter the AS by the specified filter
			UIEvents.filterBy(ui, filter);
			
			// Click 'Show More' to expand the AS feed
			UIEvents.clickShowMore(ui);
			
			// Verify that the mentioned in a file comment event is NOT displayed in any of the views
			HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
			
			// Verify that the mentions text is NOT displayed in any of the views
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, false);
		}
		// User 2 navigates to the Mentions view
		UIEvents.gotoMentions(ui);
		
		// Verify that the mentioned in a file comment event is NOT displayed in any of the views
		HomepageValid.verifyFilesNewsStoryIsNotDisplayedInAS(ui, driver, mentionedYouFileCommentEvent, baseFile);
		
		// Verify that the mentions text is NOT displayed in any of the views
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionsComment}, null, false);
		
		ui.endTest();
	}
}