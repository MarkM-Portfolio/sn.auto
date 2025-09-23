package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

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

public class FVT_Mentions_ForumReply_Communities_FollowPerson extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS;

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser4, communitiesAPIUser6;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser4, forumsAPIUser6;
	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7;
	private BaseCommunity moderatedBaseCommunity, publicBaseCommunity, restrictedBaseCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(7);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);
		testUser4 = listOfStandardUsers.get(3);
		testUser5 = listOfStandardUsers.get(4);
		testUser6 = listOfStandardUsers.get(5);
		testUser7 = listOfStandardUsers.get(6);

		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		forumsAPIUser4 = initialiseAPIForumsHandlerUser(testUser4);
		forumsAPIUser6 = initialiseAPIForumsHandlerUser(testUser6);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser4 = initialiseAPICommunitiesHandlerUser(testUser4);
		communitiesAPIUser6 = initialiseAPICommunitiesHandlerUser(testUser6);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		profilesAPIUser4 = initialiseAPIProfilesHandlerUser(testUser4);
		profilesAPIUser5 = initialiseAPIProfilesHandlerUser(testUser5);
		profilesAPIUser6 = initialiseAPIProfilesHandlerUser(testUser6);
		profilesAPIUser7 = initialiseAPIProfilesHandlerUser(testUser7);
		
		// Initialise the test filters array and include the 'People' filter if the test is run On Premise
		if(isOnPremise) {
			TEST_FILTERS = new String[4];
			TEST_FILTERS[3] = HomepageUIConstants.FilterPeople;
		} else {
			TEST_FILTERS = new String[3];
		}
		
		// Set the common filters to be added to the test filters array
		TEST_FILTERS[0] = HomepageUIConstants.FilterAll;
		TEST_FILTERS[1] = HomepageUIConstants.FilterCommunities;
		TEST_FILTERS[2] = HomepageUIConstants.FilterForums;
		
		// User 2 will now follow User 1
		ProfileEvents.followUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 5 (acting as User 2) will now follow User 4 (acting as User 1)
		ProfileEvents.followUser(profilesAPIUser4, profilesAPIUser5);
		
		// User 7 (acting as User 2) will now follow User 6 (acting as User 1)
		ProfileEvents.followUser(profilesAPIUser6, profilesAPIUser7);
		
		// User 1 will now create a public community
		publicBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(publicBaseCommunity, testUser1, communitiesAPIUser1);
		
		// User 4 (acting as User 1) will now create a moderated community
		moderatedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(moderatedBaseCommunity, testUser4, communitiesAPIUser4);
		
		// User 6 (acting as User 1) will now create a restricted community with User 3 added as a member
		restrictedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(restrictedBaseCommunity, testUser6, communitiesAPIUser6, testUser3);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser4.deleteCommunity(moderatedCommunity);
		communitiesAPIUser6.deleteCommunity(restrictedCommunity);
		
		// User 2 will unfollow User 1 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser1, profilesAPIUser2);
		
		// User 5 will now unfollow User 4 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser4, profilesAPIUser5);
		
		// User 7 will now unfollow User 6 now that the test has completed
		ProfileEvents.unfollowUser(profilesAPIUser6, profilesAPIUser7);
	}
	
	/**
	* replyMention_forumTopic_publicCommunity_followPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities, People & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/386E99E7ADD6BD1485257C84005C2909">TTT - AS - FOLLOW - PERSON - FORUMS - 00321 - Forums reply with mentions - Public Community Forum</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_publicCommunity_followPerson(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), publicBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(publicCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), publicBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply to forum topic event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* replyMention_forumTopic_modCommunity_followPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities, People & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/745E111698DDB1C185257C84005C404E">TTT - AS - FOLLOW - PERSON - FORUMS - 00322 - Forums reply with mentions - Moderate Community Forum</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_modCommunity_followPerson(){

		/**
		 * To prevent intermittent failures when these tests are run on the grid, this test case
		 * will use User 4 (as User 1) and User 5 (as User 2) with User 3 being used as normal for the mentions
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), moderatedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(moderatedCommunity, baseForumTopic, testUser4, communitiesAPIUser4, forumsAPIUser4, mentions);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser5, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), moderatedBaseCommunity.getName(), testUser4.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply to forum topic event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* replyMention_forumTopic_privateCommunity_followPerson() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 add testUser2 to the community as a member</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities, People & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/22293DAA3ED572F685257C84005C4F7E">TTT - AS - FOLLOW - PERSON - FORUMS - 00323 - Forums reply with mentions - Private Community Forum</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_privateCommunity_followPerson(){

		/**
		 * To prevent intermittent failures when these tests are run on the grid, this test case
		 * will use User 6 (as User 1) and User 7 (as User 2) with User 3 being used as normal for the mentions
		 */
		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), restrictedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(restrictedCommunity, baseForumTopic, testUser6, communitiesAPIUser6, forumsAPIUser6, mentions);
		
		// Log in as User 2 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser7, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), restrictedBaseCommunity.getName(), testUser6.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply to forum topic event and comment with mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
}