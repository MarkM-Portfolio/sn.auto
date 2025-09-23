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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityForumNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

public class FVT_Mentions_ForumReply_Communities_MentionsView extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities, HomepageUIConstants.FilterForums };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity moderatedBaseCommunity, publicBaseCommunity, restrictedBaseCommunity, restrictedBaseCommunityWithMember;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity, restrictedCommunityWithMember;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);

		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public community
		publicBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(publicBaseCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a moderated community
		moderatedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(moderatedBaseCommunity, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a restricted community with no members
		restrictedBaseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(restrictedBaseCommunity, testUser1, communitiesAPIUser1);
				
		// User 1 will now create a restricted community with User 2 added as a member
		restrictedBaseCommunityWithMember = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunityWithMember = CommunityEvents.createNewCommunityWithOneMember(restrictedBaseCommunityWithMember, testUser1, communitiesAPIUser1, testUser2);
	}

	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities now that the test has completed
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunityWithMember);
	}
	
	/**
	* replyMention_forumTopic_mentionsView_publicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a public community</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5F2B75385C4FB79A85257C84005B1B1E">TTT - @MENTIONS - 091 - MENTIONS DIRECTED TO YOU IN A FORUM TOPIC REPLY - PUBLIC COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_mentionsView_publicCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 2 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), publicBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(publicCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// User 2 will now log in and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news stories to be verified
		String replyMentionEvent = CommunityForumNewsStories.getMentionedYouInAReplyToATopicNewsStory(ui, baseForumTopic.getTitle(), publicBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the reply mentions event and mentions text are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the reply mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
		
		ui.endTest();
	}
	
	/**
	* replyMention_forumTopic_mentionsView_modCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a moderated community</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5A0451224BEB9BD785257C9200420352">TTT - @MENTIONS - 092 - MENTIONS DIRECTED TO YOU IN A FORUM TOPIC REPLY - MODERATE COMMUNITY FORUM</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_mentionsView_modCommunity(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 2 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), moderatedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(moderatedCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// User 2 will now log in and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
				
		// Create the news stories to be verified
		String replyMentionEvent = CommunityForumNewsStories.getMentionedYouInAReplyToATopicNewsStory(ui, baseForumTopic.getTitle(), moderatedBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
				
		// Verify that the reply mentions event and mentions text are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the reply mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
				
		ui.endTest();
	}

	/**
	* replyMention_forumTopic_mentionsView_privateCommunity_nonMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 add testUser2 to the community as a member</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A67CADDB16028F6C85257C84005B3410">TTT - @MENTIONS - 093 - MENTIONS DIRECTED TO YOU IN A FORUM TOPIC REPLY - PRIVATE COMMUNITY FORUM - NON MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_mentionsView_privateCommunity_nonMember(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 2 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), restrictedBaseCommunity);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(restrictedCommunity, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// User 2 will now log in and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
						
		// Create the news stories to be verified
		String replyMentionEvent = CommunityForumNewsStories.getMentionedYouInAReplyToATopicNewsStory(ui, baseForumTopic.getTitle(), restrictedBaseCommunity.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
						
		// Verify that the reply mentions event and mentions text are NOT displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, null, false);
				
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the reply mentions event and mentions text are NOT displayed in any of the views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, false);
				
		ui.endTest();
	}
	
	/**
	* replyMention_forumTopic_mentionsView_privateCommunity_member() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Communities</B></li>
	*<li><B>Step: testUser1 start a private community</B></li>
	*<li><B>Step: testUser1 add testUser2 to the community as a member</B></li>
	*<li><B>Step: testUser2 follow testUser1</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All, Communities & Forums</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the I'm Following view</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the Mentions view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/08E8EAEF4610C54F85257C84005B5C29">TTT - @MENTIONS - 094 - MENTIONS DIRECTED TO YOU IN A FORUM TOPIC REPLY - PRIVATE COMMUNITY FORUM - MEMBER</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void replyMention_forumTopic_mentionsView_privateCommunity_member(){

		String testName = ui.startTest();
		
		// User 1 will now create a forum topic in the community forum and will post a reply with mentions to User 2 to the topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopic(testName + Helper.genStrongRand(), restrictedBaseCommunityWithMember);
		CommunityForumEvents.createForumTopicAndAddReplyWithMentions(restrictedCommunityWithMember, baseForumTopic, testUser1, communitiesAPIUser1, forumsAPIUser1, mentions);
		
		// User 2 will now log in and navigate to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
						
		// Create the news stories to be verified
		String replyMentionEvent = CommunityForumNewsStories.getMentionedYouInAReplyToATopicNewsStory(ui, baseForumTopic.getTitle(), restrictedBaseCommunityWithMember.getName(), testUser1.getDisplayName());
		String mentionsText = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
						
		// Verify that the reply mentions event and mentions text are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, null, true);
				
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
				
		// Verify that the reply mentions event and mentions text are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{replyMentionEvent, baseForumTopic.getDescription(), mentionsText}, TEST_FILTERS, true);
				
		ui.endTest();
	}
}