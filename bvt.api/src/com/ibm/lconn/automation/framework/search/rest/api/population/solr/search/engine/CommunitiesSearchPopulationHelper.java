package com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population.ASCommunitiesPopulationHelper;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunitiesSearchPopulationHelper {

	private static String CLASS_NAME = CommunitiesSearchPopulationHelper.class.getName();
	private static Logger LOGGER = Logger.getLogger(CLASS_NAME);
	
	public Community createCommunity(String communityTitle,
			String communityContent, Permissions communityPermissions,
			String communityTags) {
		final String methodname = "createCommunity";
		LOGGER.entering(CLASS_NAME, methodname, new Object[]{communityTitle, communityContent, communityPermissions, communityTags});
		Community community = null;
		try {
			ASCommunitiesPopulationHelper communitiesPopulationHelper = new ASCommunitiesPopulationHelper();
			community =  communitiesPopulationHelper.createCommunityWithPermissions(communityTitle, communityContent, communityPermissions, communityTags);
			populateEntryForTestIfNeeded(community, communityPermissions);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "The community was not created");
		}
		LOGGER.exiting(CLASS_NAME, methodname, community);
		return community;		
	}
	
	public ForumTopic createCommunityTopic(Community community, String topicTitle, String topicContent, Permissions permission){
		final String methodname = "createCommunityTopic";
		LOGGER.entering(CLASS_NAME, methodname, new Object[]{community, topicTitle, topicContent, permission});
		ForumTopic communityForumTopic = null;
		try {
			ASCommunitiesPopulationHelper communitiesPopulationHelper = new ASCommunitiesPopulationHelper();
			communityForumTopic = communitiesPopulationHelper.createCommunityTopic(community, topicTitle, topicContent);
			populateEntryForTestIfNeeded(communityForumTopic, permission);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "The Forum topic was not created");
		}
		LOGGER.exiting(CLASS_NAME, methodname, communityForumTopic);
		return communityForumTopic;
	}
	
	public ForumTopic createCommunityTopic(Community community, ForumTopic forumTopic, Permissions permission){
		final String methodname = "createCommunityTopic";
		LOGGER.entering(CLASS_NAME, methodname, new Object[]{community, forumTopic, permission});
		ForumTopic communityForumTopic = null;
		try {
			ASCommunitiesPopulationHelper communitiesPopulationHelper = new ASCommunitiesPopulationHelper();
			communityForumTopic = communitiesPopulationHelper.createCommunityTopic(community, forumTopic);
			populateEntryForTestIfNeeded(communityForumTopic, permission);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "The Forum topic was not created");
		}
		LOGGER.exiting(CLASS_NAME, methodname, communityForumTopic);
		return communityForumTopic;
	}
	
	public ForumReply createCommunityTopicReply(ForumTopic parentTopic, ForumReply forumReply, Permissions permission){
		final String methodname = "createCommunityTopicReply";
		LOGGER.entering(CLASS_NAME, methodname, new Object[]{parentTopic, forumReply, permission});
		ForumReply communityForumReply = null;
		try {
			ASCommunitiesPopulationHelper communitiesPopulationHelper = new ASCommunitiesPopulationHelper();
			communityForumReply = communitiesPopulationHelper.createCommunityReply(parentTopic, forumReply);
			populateEntryForTestIfNeeded(communityForumReply, permission);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "The Forum topic was not created");
		}
		LOGGER.exiting(CLASS_NAME, methodname, communityForumReply);
		return communityForumReply;
	}
	
	private void populateEntryForTestIfNeeded(LCEntry lcEntry, Permissions permission){
		if(lcEntry != null){
			PopulatedData.getInstance().setPopulatedLcEntry(lcEntry, permission);
		}
	}	
}
