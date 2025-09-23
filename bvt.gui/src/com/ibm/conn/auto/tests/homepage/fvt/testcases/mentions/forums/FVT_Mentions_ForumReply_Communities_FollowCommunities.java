package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
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
 * @author Patrick Doherty
 */

public class FVT_Mentions_ForumReply_Communities_FollowCommunities extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2, communitiesAPIUser4, communitiesAPIUser5, communitiesAPIUser6, communitiesAPIUser7;
	private APIForumsHandler forumsAPIUser1, forumsAPIUser4, forumsAPIUser6;
	private APIProfilesHandler profilesAPIUser3;
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
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		communitiesAPIUser4 = initialiseAPICommunitiesHandlerUser(testUser4);
		communitiesAPIUser5 = initialiseAPICommunitiesHandlerUser(testUser5);
		communitiesAPIUser6 = initialiseAPICommunitiesHandlerUser(testUser6);
		communitiesAPIUser7 = initialiseAPICommunitiesHandlerUser(testUser7);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 4 (acting as User 1) will now create a moderated community with User 5 (acting as User 2) as a follower
		moderatedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneFollower(moderatedBaseCommunity, testUser5, communitiesAPIUser5, testUser4, communitiesAPIUser4);
				
		// User 1 will now create a public community with User 2 as a follower
		publicBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneFollower(publicBaseCommunity, testUser2, communitiesAPIUser2, testUser1, communitiesAPIUser1);
		
		// User 6 (acting as User 1) will now create a restricted community with User 7 (acting as User 2) and User 3 added as members and User 7 (acting as User 2) as a follower
		User[] members = { testUser3, testUser7 };
		restrictedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithMultipleMembersAndOneFollower(restrictedBaseCommunity, testUser6, communitiesAPIUser6, members, testUser7, communitiesAPIUser7);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser4.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser6.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* replyMention_forumTopic_publicCommunity_followCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/7B1D741D7D13EEDB85257C84005ECFE1">TTT - AS - FOLLOW - FORUMS - 00191 - FORUMS REPLY WITH MENTIONS - PUBLIC COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_publicCommunity_followCommunity() {

		String testName = ui.startTest();
		
		// User 1 will now add a topic to the public community forum and will add a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), publicBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(publicCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser2, false);
		
		// Create the news story to be verified
		String replyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), publicBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the topic reply with mentions is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* replyMention_forumTopic_modCommunity_followCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4E52672555673FE685257C84005ECFE2">TTT - AS - FOLLOW - FORUMS - 00192 - FORUMS REPLY WITH MENTIONS - MODERATE COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_modCommunity_followCommunity(){

		/**
		 * To prevent intermittent failures when these tests are run on the grid, this test case
		 * will use User 4 (as User 1) and User 5 (as User 2) with User 3 being used as normal for the mentions
		 */
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the moderated community forum and will add a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), moderatedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(moderatedCommunity, baseForumTopic, testUser4, communitiesAPIUser4, forumsAPIUser4, mentions);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser5, false);
		
		// Create the news story to be verified
		String replyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), moderatedBaseCommunity.getName(), testUser4.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the topic reply with mentions is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* replyMention_forumTopic_privateCommunity_followCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 add testUser2 to the community as a member</B></li>
	*<li><B>Step: testUser2 follow the community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1256E5753082CDD785257C84005ECFE3">TTT - AS - FOLLOW - FORUMS - 00193 - FORUMS REPLY WITH MENTIONS - PRIVATE COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_privateCommunity_followCommunity(){

		/**
		 * To prevent intermittent failures when these tests are run on the grid, this test case
		 * will use User 6 (as User 1) and User 7 (as User 2) with User 3 being used as normal for the mentions
		 */
		String testName = ui.startTest();
		
		// User 1 will now add a topic to the restricted community forum and will add a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), restrictedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(restrictedCommunity, baseForumTopic, testUser6, communitiesAPIUser6, forumsAPIUser6, mentions);
		
		// Log in as User 2 and go to I'm Following
		LoginEvents.loginAndGotoImFollowing(ui, testUser7, false);
		
		// Create the news story to be verified
		String replyEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), restrictedBaseCommunity.getName(), testUser6.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the topic reply with mentions is displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
}