package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.util.logging.Logger;

import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class ASForumsPopulationHelper {

	
	private static ForumsService service;
	
	private static int totalCountofForums = 0;

	
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	public ASForumsPopulationHelper() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry forumsEntry = restAPIUser.getService("forums");
		restAPIUser.addCredentials(forumsEntry);
		service = new ForumsService(restAPIUser.getAbderaClient(),
				forumsEntry);
		
	}

	

	public void populate() {
		try {

			
			if (service != null) {
				Entry forumCreationResult = createStandaloneForum(
						PopStringConstantsAS.STANDALONE_FORUM_TITLE + " "
								+ PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.STANDALONE_FORUM_CONTENT);
				if (forumCreationResult != null) {
					Forum forumToWork = new Forum(forumCreationResult);
					Entry topicCreationResult = createForumTopic(forumToWork,
							PopStringConstantsAS.STANDALONE_FORUM_TOPIC_TITLE,
							PopStringConstantsAS.STANDALONE_FORUM_TOPIC_CONTENT);
					if (topicCreationResult != null) {
						createTopicResponse(
								forumToWork,
								PopStringConstantsAS.STANDALONE_FORUM_TOPIC_RESPONSE_TITLE,
								PopStringConstantsAS.STANDALONE_FORUM_TOPIC_RESPONSE_CONTENT,
								topicCreationResult);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.fine("Exception in Forums population: " + e.getMessage());
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public Entry createStandaloneForum(String forumTitle, String forumContent) {

		Forum forum = new Forum(forumTitle, forumContent);
		Entry forumResult = (Entry) service.createForum(forum);

		if (forumResult == null) {

			LOGGER.fine("Failed to create Standalone Forum ");
		}
		return forumResult;

	}

	public Entry createForumTopic(Forum topicForum, String forumTitle,
			String forumContent) {

		ForumTopic newTopic = new ForumTopic(forumTitle, forumContent, false,
				false, false, false);
		Entry topicResult = (Entry) service.createForumTopic(topicForum,
				newTopic);
		if (topicResult == null) {
			LOGGER.fine("New Topic creation failed");
		}
		return topicResult;
	}

	public void createTopicResponse(Forum topicForum,
			String topicResponseTitle, String topicResponseContent,
			Entry topicParentEntry) {

		ForumReply reply = new ForumReply(topicResponseTitle,
				topicResponseContent, topicParentEntry, false);
		Entry replyResult = (Entry) service.createForumReply(topicParentEntry,
				reply);
		if (replyResult == null) {
			LOGGER.fine("Topic reply creation failed");
		}
	}

}
