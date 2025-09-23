package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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

public class FVT_Mentions_ForumReply_Discover_Communities_SameOrg extends SetUpMethodsFVT {
	
	private String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };

	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser3;
	private BaseCommunity moderatedBaseCommunity, publicBaseCommunity, restrictedBaseCommunity;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(3);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		testUser3 = listOfStandardUsers.get(2);

		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser3 = initialiseAPIProfilesHandlerUser(testUser3);
		
		// User 1 will now create a public community
		publicBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(publicBaseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community
		moderatedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(moderatedBaseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community
		restrictedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(restrictedBaseCommunity, testUser1, communitiesAPIUser1);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* replyMention_forumTopic_discover_publicCommunity_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Discover / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FF8C6E3F5905049485257C84005FEC0F">TTT - DISCOVER - FORUMS - 00171 - FORUMS REPLY WITH MENTIONS - PUBLIC COMMUNITY FORUM - SAME ORGANIZATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_discover_publicCommunity_sameOrg(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), publicBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(publicCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), publicBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply to forum topic event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* replyMention_forumTopic_discover_modCommunity_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Discover / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DAC6E0D115FDC6A885257C84005FEC10">TTT - DISCOVER - FORUMS - 00172 - FORUMS REPLY WITH MENTIONS - MODERATE COMMUNITY FORUM - SAME ORGANIZATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_discover_modCommunity_sameOrg(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), moderatedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(moderatedCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), moderatedBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply to forum topic event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}

	/**
	* replyMention_forumTopic_discover_privateCommunity_sameOrg() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser3</B></li>
	*<li><B>Step: testUser2 who is in the SAME organisation log into Homepage / Updates / Discover / All, Communities & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the Discover view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A4D6554C7A793E6085257C84005FEC11">TTT - DISCOVER - FORUMS - 00173 - FORUMS REPLY WITH MENTIONS - PRIVATE COMMUNITY FORUM - SAME ORGANIZATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_discover_privateCommunity_sameOrg(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 3 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), restrictedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(restrictedCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyToTopicEvent = CommunityForumNewsStories.getReplyToTheirOwnTopicNewsStory(ui, baseForumTopic.getTitle(), restrictedBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply to forum topic event and comment with mentions are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyToTopicEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, false);
		
		ui.endTest();	
	}
}