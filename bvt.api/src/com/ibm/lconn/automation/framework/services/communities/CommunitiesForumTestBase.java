package com.ibm.lconn.automation.framework.services.communities;

import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class CommunitiesForumTestBase {
	protected final static Logger LOGGER = LoggerFactory.getLogger(CommunitiesForumTestBase.class.getName());

	// post
	protected Entry createPrivateCommunity(String CommunityTitle, CommunitiesService service){
		Community community = new Community(CommunityTitle, "Private community test.", Permissions.PRIVATE, null);
		Entry result = (Entry) service.createCommunity(community);
		return result;
	}

	// post
	protected Entry createPublicCommunity(String CommunityTitle, CommunitiesService service){
		Community community = new Community(CommunityTitle, "Public community test.", Permissions.PUBLIC, null);
		Entry result = (Entry) service.createCommunity(community);
		return result;
	}

	// get
	protected Entry retrieveCommunity(Entry communityEntry, CommunitiesService service){
		LOGGER.debug("==entry of method retrieveCommunity()==");
		Entry result = (Entry) service.getCommunity(communityEntry.getEditLinkResolvedHref().toString());
		return result;
	}

	// post
	protected Entry createTopic(Community community, String title, String content, CommunitiesService service ){
		LOGGER.debug("==Entry of method createTopic()==");
		ForumTopic topic = new ForumTopic(title, content, true, false, false, false);
		Entry result = (Entry) service.createForumTopic(community, topic);
		return result;
	}

	// get
	protected Entry retrieveTopic(Entry topic, CommunitiesService service){
		LOGGER.debug("==entry of method retrieveTopic()==");
		Entry result = (Entry) service.getForumTopic(topic.getEditLinkResolvedHref().toString());
		return result;
	}
	
	// put
	protected Entry editTopic(ForumTopic topic, CommunitiesService service){
		LOGGER.debug("==entry of method editTopic()==");
		Entry result = (Entry) service.editForumTopic(topic.getEditLink(), topic);
		return result;
	}

	// delete
	protected void deleteTopic(Entry topicEntry, CommunitiesService service){
		LOGGER.debug("==entry of method deleteTopic()==");
		service.deleteForumTopic(topicEntry.getEditLink().getHref().toString());
	}

	// post
	protected Entry createTopicReply(Entry topic, String title, String content, CommunitiesService service){
		LOGGER.debug("==entry of method createTopicReply()==");
		ForumReply reply = new ForumReply(title, content, topic, false);
		Entry result = (Entry) service.createForumReply(topic, reply);
		return result;	
	}

	// get
	protected Entry retrieveTopicReply(Entry reply, CommunitiesService service){
		LOGGER.debug("==entry of method retrieveTopicReply()==");
		Entry result = (Entry) service.getForumReply(reply.getEditLink().getHref().toString());
		return result;
	}
	
	// put
	protected Entry editTopicReply(ForumReply reply, CommunitiesService service){
		LOGGER.debug("==entry of method editTopicReply()==");
		Entry result = (Entry) service.editForumReply(reply.getEditLink(), reply);
		return result;
	}
	
	// delete reply
	protected void deleteTopicReply(Entry replyEntry, CommunitiesService service){
		LOGGER.debug("==Entry of deleteTopicReply()==");
		service.deleteForumReply(replyEntry.getEditLink().getHref().toString());
	}
}
