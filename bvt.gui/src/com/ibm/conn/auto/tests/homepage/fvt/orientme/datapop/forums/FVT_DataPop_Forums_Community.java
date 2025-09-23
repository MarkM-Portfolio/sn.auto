package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.forums;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityForumEvents;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	2nd March 2017
 */

public class FVT_DataPop_Forums_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIForumsHandler forumsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private BaseForum baseForum;
	private Community publicCommunity;
	private Forum communityForum;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		forumsAPIUser1 = initialiseAPIForumsHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
		
		// User 1 will now create a forum in the community
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		communityForum = CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumUpdated() {
		
		// User 1 will now create a forum in the community
		BaseForum baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		Forum communityForum = CommunityForumEvents.createForum(publicCommunity, serverURL, testUser1, communitiesAPIUser1, forumsAPIUser1, baseForum);
		
		// User 1 will now edit the description of the community forum
		String forumDescriptionEdit = Data.getData().commonDescription + Helper.genStrongRand();
		CommunityForumEvents.editForumDescription(communityForum, forumDescriptionEdit, testUser1, forumsAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicCreated() {
		
		// User 1 will now create a forum topic in the forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicUpdated() {
		
		// User 1 will now create a forum topic in the forum and will update the description of the topic
		String forumTopicDescriptionEdit = Data.getData().commonDescription + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndEditDescription(testUser1, forumsAPIUser1, baseForumTopic, forumTopicDescriptionEdit);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicLiked() {
		
		// User 1 will now create a forum topic in the forum and will like / recommend the topic
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndLikeTopic(testUser1, forumsAPIUser1, baseForumTopic);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicReplyCreated() {
		
		// User 1 will now create a forum topic in the forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
		
		// User 1 will now post a reply to the forum topic
		String user1Reply = Data.getData().commonComment + Helper.genStrongRand();
		CommunityForumEvents.createForumTopicReply(testUser1, forumsAPIUser1, forumTopic, user1Reply);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicReplyUpdated() {
		
		// User 1 will now create a forum topic, post a reply to the forum topic and will update the reply
		String user1Reply = Data.getData().commonComment + Helper.genStrongRand();
		String user1ReplyEdit = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndAddReplyAndEditReply(testUser1, forumsAPIUser1, baseForumTopic, user1Reply, user1ReplyEdit);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicReplyLiked() {
		
		// User 1 will now create a forum topic, post a reply to the forum topic and will like / recommend the reply
		String user1Reply = Data.getData().commonComment + Helper.genStrongRand();
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		CommunityForumEvents.createForumTopicInSpecifiedForumAndAddReplyAndLikeReply(testUser1, forumsAPIUser1, baseForumTopic, user1Reply);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Forum_ForumTopicReplyWithMentions() {
		
		// User 1 will now create a forum topic in the forum
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildCommunityBaseForumTopicWithCustomParentForum(getClass().getSimpleName() + Helper.genStrongRand(), baseCommunity, communityForum);
		ForumTopic forumTopic = CommunityForumEvents.createForumTopicInSpecifiedForum(testUser1, forumsAPIUser1, baseForumTopic);
				
		// User 1 will now post a reply with mentions to User 2 to the forum topic
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		CommunityForumEvents.createForumTopicReplyWithMentions(forumTopic, testUser1, forumsAPIUser1, mentions);
	}
}