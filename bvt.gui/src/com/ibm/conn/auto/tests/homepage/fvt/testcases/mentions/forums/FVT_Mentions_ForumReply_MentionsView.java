package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.forums;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.forums.ForumNewsStories;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

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
 * [Forums mentions events] UI FVT Automation for Story 97434
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/115865
 * @author Patrick Doherty
 */

public class FVT_Mentions_ForumReply_MentionsView extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterForums };

	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseForum baseForum;
	private Forum standaloneForum;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
				
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a standalone forum
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum = ForumEvents.createForum(testUser1, forumsAPIUser1, baseForum);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the forum created during the test
		forumsAPIUser1.deleteForum(standaloneForum);
	}
	
	/**
	* replyMention_forumTopic_mentions() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 logs into Forums</B></li>
	*<li><B>Step: testUser1 start a forum</B></li>
	*<li><B>Step: testUser1 add a topic</B></li>
	*<li><B>Step: testUser1 reply to the topic with a mentions to testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Mentions / All & Forums</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates / I'm Following / All & Forums</B></li>
	*<li><B>Verify: Verify that testUser2 can see the mentions event in the filters in the both view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3D2AB42AF6F872C485257C84005ADED7">TTT - @MENTIONS - 080 - MENTIONS DIRECTED TO YOU IN A FORUM TOPIC REPLY - STANDALONE FORUM - ON PREM ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void replyMention_forumTopic_mentions(){

		String testName = ui.startTest();
		
		// User 1 will now add a topic to the standalone forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumTopic forumTopic = ForumEvents.createForumTopic(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 1 will now post a reply with mentions to User 2 to the forum topic 
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		ForumEvents.createForumTopicReplyWithMentions(forumTopic, testUser1, forumsAPIUser1, mentions);
		
		// Log in as User 2 and go to the Mentions view
		LoginEvents.loginAndGotoMentions(ui, testUser2, false);
		
		// Create the news story to be verified
		String mentionedYouEvent = ForumNewsStories.getMentionedYouInAReplyToATopicNewsStory(ui, baseForumTopic.getTitle(), baseForum.getName(), testUser1.getDisplayName());
		String mentionsComment = mentions.getBeforeMentionText() + " @" + mentions.getUserToMention().getDisplayName() + " " + mentions.getAfterMentionText();
		
		// Verify that the mentions event and comment with mentions are displayed in the Mentions view
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, baseForumTopic.getDescription(), mentionsComment}, null, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Verify that the mentions event and comment with mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{mentionedYouEvent, baseForumTopic.getDescription(), mentionsComment}, TEST_FILTERS, true);
				
		ui.endTest();
	}	
}