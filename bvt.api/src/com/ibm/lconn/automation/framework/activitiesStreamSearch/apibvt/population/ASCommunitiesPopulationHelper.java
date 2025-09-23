package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.util.logging.Logger;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.commons.lang.RandomStringUtils;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Subcommunity;

public class ASCommunitiesPopulationHelper {

	
	private static CommunitiesService service;
	private static String communityType = null;
	private static Community moderatedCommunity;
	private static Community frenchCommunity1;
	private static Community frenchCommunity2;
	private static Community privateCommunity;
	private static Community publicCommunity;
	private static Community moderatedSubCommunity;
	private static Community privateSubCommunity;
	private static Community publicSubCommunity;

	public static Community getModeratedCommunity() {
		return moderatedCommunity;
	}

	public static Community getPrivateCommunity() {
		return privateCommunity;
	}

	public static Community getPublicCommunity() {
		return publicCommunity;
	}

	public static Community getFrenchCommunity1() {
		return frenchCommunity1;
	}

	public static Community getFrenchCommunity2() {
		return frenchCommunity2;
	}

	public static Community getModeratedSubCommunity() {
		return moderatedSubCommunity;
	}

	public static Community getPrivateSubCommunity() {
		return privateSubCommunity;
	}

	public static Community getPublicSubCommunity() {
		return publicSubCommunity;
	}

	private static int modComBookmarks = 10;
	private static int pubComBookmarks = 9;

	private static boolean useSSL = true;
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	
	public ASCommunitiesPopulationHelper() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.ASSEARCH);

		ServiceEntry commServiceEntry = restAPIUser.getService("communities");
		restAPIUser.addCredentials(commServiceEntry);
		service = new CommunitiesService(restAPIUser.getAbderaClient(),
				commServiceEntry);
		
	}

	
	public void createModeratedCommunity() {
		if (service != null) {
			moderatedCommunity = createCommunityWithPermissions(
					PopStringConstantsAS.MODERATED_COMMUNITY_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.MODERATED_COMMUNITY_CONTENT,
					Permissions.PUBLICINVITEONLY,
					PopStringConstantsAS.MODERATED_COMMUNITY_TAG);
			if (moderatedCommunity != null) {

				createCommunityBookmarksAndTopic(
						moderatedCommunity,
						PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_TITLE,
						PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_CONTENT,
						PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_TAG,
						PopStringConstantsAS.MODERATED_COMMUNITY_TOPIC_TITLE,
						PopStringConstantsAS.MODERATED_COMMUNITY_TOPIC_CONTENT);

				moderatedSubCommunity = createSubCommunity(
						PopStringConstantsAS.MODERATED_COMMUNITY_TITLE + " "
								+ PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.MODERATED_COMMUNITY_TITLE + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX
								+ " " + PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.MODERATED_COMMUNITY_CONTENT + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
						PopStringConstantsAS.MODERATED_COMMUNITY_TAG + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
						Permissions.PUBLICINVITEONLY);

				if (moderatedSubCommunity != null) {
					LOGGER.fine("Creating Moderated SubCommunity Bookmark and Topic in user's SubCommunities:");
					createCommunityBookmarksAndTopic(
							moderatedSubCommunity,
							PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_TITLE
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_CONTENT
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_TAG
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.MODERATED_COMMUNITY_TOPIC_TITLE
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.MODERATED_COMMUNITY_TOPIC_CONTENT
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX);
				} else {
					LOGGER.fine("SubCommunity creation failed, stopping Bookmark, Topic events creation of SubCommunity");
				}
				for (int i = 0; i < modComBookmarks; i++) {
					createCommunityBookmark(
							moderatedCommunity,
							PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_TITLE
									+ " " + i,
							PopStringConstantsAS.MODERATED_COMMUNITY_BOOKMARK_CONTENT
									+ " " + i, "modtag" + " " + i);

				}

			} else {
				LOGGER.fine("Community creation failed, stopping Bookmark, Topic, SubCommunity and all related events creation");
			}
		}
	}

	public void createPrivateCommunity() {
		if (service != null) {
			privateCommunity = createCommunityWithPermissions(
					PopStringConstantsAS.PRIVATE_COMMUNITY_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PRIVATE_COMMUNITY_CONTENT,
					Permissions.PRIVATE,
					PopStringConstantsAS.PRIVATE_COMMUNITY_TAG);
			if (privateCommunity != null) {
				LOGGER.fine("Creating Private Community Bookmark and Topic in user's Communities:");
				createCommunityBookmarksAndTopic(
						privateCommunity,
						PopStringConstantsAS.PRIVATE_COMMUNITY_BOOKMARK_TITLE,
						PopStringConstantsAS.PRIVATE_COMMUNITY_BOOKMARK_CONTENT,
						PopStringConstantsAS.PRIVATE_COMMUNITY_BOOKMARK_TAG,
						PopStringConstantsAS.PRIVATE_COMMUNITY_TOPIC_TITLE,
						PopStringConstantsAS.PRIVATE_COMMUNITY_TOPIC_CONTENT);

				LOGGER.fine("Creating Private SubCommunity: "
						+ PopStringConstantsAS.PRIVATE_COMMUNITY_TITLE + " "
						+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX + " "
						+ PopStringConstantsAS.eventIdent);
				privateSubCommunity = createSubCommunity(
						PopStringConstantsAS.PRIVATE_COMMUNITY_TITLE + " "
								+ PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.PRIVATE_COMMUNITY_TITLE + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX
								+ " " + PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.PRIVATE_COMMUNITY_CONTENT + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
						PopStringConstantsAS.PRIVATE_COMMUNITY_TAG + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
						Permissions.PRIVATE);
				if (privateSubCommunity != null) {
					LOGGER.fine("Creating Private Sub Community Bookmark and Topic in user's Communities:");
					createCommunityBookmarksAndTopic(
							privateSubCommunity,
							PopStringConstantsAS.PRIVATE_COMMUNITY_BOOKMARK_TITLE
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PRIVATE_COMMUNITY_BOOKMARK_CONTENT
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PRIVATE_COMMUNITY_BOOKMARK_TAG
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PRIVATE_COMMUNITY_TOPIC_TITLE
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PRIVATE_COMMUNITY_TOPIC_CONTENT
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX);
				} else {
					LOGGER.fine("SubCommunity creation failed, stopping Bookmark, Topic events creation of SubCommunity");
				}
			} else {
				LOGGER.fine("Community creation failed, stopping Bookmark, Topic, SubCommunity and all related events creation");
			}
		}
	}

	public void createPublicCommunity() {
		if (service != null) {
			publicCommunity = createCommunityWithPermissions(
					PopStringConstantsAS.PUBLIC_COMMUNITY_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PUBLIC_COMMUNITY_CONTENT,
					Permissions.PUBLIC,
					PopStringConstantsAS.PUBLIC_COMMUNITY_TAG);
			if (publicCommunity != null) {
				LOGGER.fine("Creating Public Community Bookmark and Topic in user's Communities:");
				createCommunityBookmarksAndTopic(publicCommunity,
						PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_TITLE,
						PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_CONTENT,
						PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_TAG,
						PopStringConstantsAS.PUBLIC_COMMUNITY_TOPIC_TITLE,
						PopStringConstantsAS.PUBLIC_COMMUNITY_TOPIC_CONTENT);

				LOGGER.fine("Creating Moderated SubCommunity: "
						+ PopStringConstantsAS.PUBLIC_COMMUNITY_TITLE + " "
						+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX + " "
						+ PopStringConstantsAS.eventIdent);
				publicSubCommunity = createSubCommunity(
						PopStringConstantsAS.PUBLIC_COMMUNITY_TITLE + " "
								+ PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.PUBLIC_COMMUNITY_TITLE + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX
								+ " " + PopStringConstantsAS.eventIdent,
						PopStringConstantsAS.PUBLIC_COMMUNITY_CONTENT + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
						PopStringConstantsAS.PUBLIC_COMMUNITY_TAG + " "
								+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
						Permissions.PUBLIC);
				if (publicSubCommunity != null) {
					LOGGER.fine("Creating Public Sub Community Bookmark and Topic in user's Communities:");
					createCommunityBookmarksAndTopic(
							publicSubCommunity,
							PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_TITLE
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_CONTENT
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_TAG
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PUBLIC_COMMUNITY_TOPIC_TITLE
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX,
							PopStringConstantsAS.PUBLIC_COMMUNITY_TOPIC_CONTENT
									+ " "
									+ PopStringConstantsAS.SUB_COMMUNITY_SUFFIX);
				} else {
					LOGGER.fine("SubCommunity creation failed, stopping Bookmark, Topic events creation of SubCommunity");
				}
				LOGGER.fine("Creating Public Community Bookmarks for Top Communities Test:");
				for (int i = 0; i < pubComBookmarks; i++) {
					createCommunityBookmark(
							publicCommunity,
							PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_TITLE
									+ " " + i,
							PopStringConstantsAS.PUBLIC_COMMUNITY_BOOKMARK_CONTENT
									+ " " + i, "pubtag" + " " + i);

				}
			} else {
				LOGGER.fine("Community creation failed, stopping Bookmark, Topic, SubCommunity and all related events creation");
			}
		}
	}

	public void createPublicFrenchCommunityUnicode() {
		if (service != null) {
			frenchCommunity1 = createCommunityWithPermissions(
					PopStringConstantsAS.PUBLIC_COMMUNITY_1_TITLE_UNICODE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PUBLIC_COMMUNITY_1_CONTENT_UNICODE,
					Permissions.PUBLIC,
					PopStringConstantsAS.PUBLIC_COMMUNITY_1_TAG);
			if (frenchCommunity1 == null) {
				LOGGER.fine("French1 Community creation failed");
			}
		}
	}

	public void createPrivateFrenchCommunity2Unicode() {
		if (service != null) {
			frenchCommunity2 = createCommunityWithPermissions(
					PopStringConstantsAS.PUBLIC_COMMUNITY_1_CONTENT_UNICODE
							+ " " + PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PUBLIC_COMMUNITY_1_TITLE_UNICODE,
					Permissions.PRIVATE,
					PopStringConstantsAS.PUBLIC_COMMUNITY_1_TAG);
			if (frenchCommunity2 == null) {
				LOGGER.fine("French2 Community creation failed");
			}
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	// Create community
	public Community createCommunityWithPermissions(String communityTitle,
			String communityContent, Permissions communityPermissions,
			String communityTags) {

		if (communityPermissions.equals(Permissions.PUBLICINVITEONLY)) {
			communityType = "Moderated";
		} else if (communityPermissions.equals(Permissions.PRIVATE)) {
			communityType = "Private";
		} else if (communityPermissions.equals(Permissions.PUBLIC)) {
			communityType = "Public";
		}

		LOGGER.fine("Creating " + communityType + " Community:");

		Community newCommunity = new Community(communityTitle,
				communityContent, communityPermissions, communityTags);
		LOGGER.fine("New community title: " + newCommunity.getTitle());
		Entry communityResult = (Entry) service.createCommunity(newCommunity);

		if (communityResult != null) {

			LOGGER.fine("New community " + newCommunity);

			LOGGER.fine("Community: " + newCommunity.getTitle()
					+ " was created successfully @ "
					+ communityResult.getEditLinkResolvedHref());
			LOGGER.fine("Finished creating " + communityType + " Community...");
			String communityEditLink = communityResult
					.getEditLinkResolvedHref().toString();

			ExtensibleElement communityElement = service
					.getCommunity(communityEditLink);
			LOGGER.fine("Getting the result of the community edit link = "
					+ communityElement);
			Community testCommunityRetrieved = new Community(
					(Entry) communityElement);
			return testCommunityRetrieved;

		} else {
			return null;
		}
	}

	// Create Bookmark in community
	public void createCommunityBookmark(Community bookmarkCommunity,
			String bookmarkTitle, String bookmarkContent, String bookmarkTags) {
		LOGGER.fine("Creating bookmarks in Community: "
				+ bookmarkCommunity.getTitle());
		Bookmark bookmark = new Bookmark(bookmarkTitle, bookmarkContent,
				"http://www.google.com/?q="
						+ RandomStringUtils.randomAlphabetic(10), bookmarkTags);
		LOGGER.fine("New bookmark title: " + bookmark.getTitle());
		LOGGER.fine("Community UUID: " + bookmarkCommunity.getUuid());
		Entry bookmarkResponse = (Entry) service.createCommunityBookmark(
				bookmarkCommunity, bookmark);
		if (bookmarkResponse == null) {
			LOGGER.fine("Bookmark creation failed");
		}
	}
	
	// Create Topic in community
	public ForumTopic createCommunityTopic(Community topicCommunity,
			String topicTitle, String topicContent) {
		LOGGER.fine("Creating topic in Community: " + topicCommunity.getTitle());
		ForumTopic communityForumTopic = null;		
		ForumTopic forumTopic = new ForumTopic(topicTitle, topicContent, false,
				false, false, false);
		LOGGER.fine("New ForumTopic title: " + forumTopic.getTitle());
		LOGGER.fine("Community UUID: " + topicCommunity.getUuid());
		Entry forumTopicResult = (Entry) service.createForumTopic(
				topicCommunity, forumTopic);
		if (forumTopicResult == null) {
			LOGGER.fine("Topic creation failed");
		} else {			
			String forumTopicEditLink = forumTopicResult.getEditLinkResolvedHref().toString();
			ExtensibleElement communityForumTopicEleemnt = service.getForumTopic(forumTopicEditLink);
			communityForumTopic = new ForumTopic((Entry) communityForumTopicEleemnt);
		}
		return communityForumTopic;
	}
	
	public ForumTopic createCommunityTopic(Community topicCommunity, ForumTopic forumTopic) {
		LOGGER.fine("Creating topic in Community: " + topicCommunity.getTitle());
		ForumTopic communityForumTopic = null;		
		LOGGER.fine("New ForumTopic title: " + forumTopic.getTitle());
		LOGGER.fine("Community UUID: " + topicCommunity.getUuid());
		Entry forumTopicResult = (Entry) service.createForumTopic(topicCommunity, forumTopic);
		if (forumTopicResult == null) {
			LOGGER.fine("Topic creation failed");
		} else {			
			String forumTopicEditLink = forumTopicResult.getEditLinkResolvedHref().toString();
			ExtensibleElement communityForumTopicEleemnt = service.getForumTopic(forumTopicEditLink);
			communityForumTopic = new ForumTopic((Entry) communityForumTopicEleemnt);
		}
		return communityForumTopic;
	}
	
	public ForumReply createCommunityReply(ForumTopic parentTopic, ForumReply forumReply) {
		ForumReply communityForumReply = null;
		if (parentTopic != null && forumReply != null) {
			LOGGER.fine("Creating reply to topic:" + parentTopic.getTitle());
			LOGGER.fine("New reply title: " + forumReply.getTitle());
			Entry communityForumReplyResult = (Entry) service.createForumReply(parentTopic.toEntry(), forumReply);
			if (communityForumReplyResult != null) {
				String forumReplyEditLink = communityForumReplyResult.getEditLinkResolvedHref().toString();
				ExtensibleElement communityForumReplyElement = service.getForumReply(forumReplyEditLink);
				communityForumReply = new ForumReply((Entry) communityForumReplyElement);
			} else {
				LOGGER.fine("Reply creation failed");
			}
		}
		return communityForumReply;
	}

	public Community createSubCommunity(String commToWorkTitle,
			String subCommTitle, String subCommContent, String subCommTags,
			Permissions commPermission) {
		Community community;
		if (commToWorkTitle
				.contains(PopStringConstantsAS.PUBLIC_COMMUNITY_TITLE)) {
			community = getPublicCommunity();
			Subcommunity subcomm = new Subcommunity(subCommTitle,
					subCommContent, commPermission, subCommTags);
			Entry subcommResponse = (Entry) service.createSubcommunity(
					community, subcomm);
			String communityEditLink = subcommResponse
					.getEditLinkResolvedHref().toString();
			ExtensibleElement communityElement = service
					.getCommunity(communityEditLink);
			publicSubCommunity = new Community((Entry) communityElement);
			if (publicSubCommunity != null) {
				return publicSubCommunity;
			}
		}
		if (commToWorkTitle
				.contains(PopStringConstantsAS.PRIVATE_COMMUNITY_TITLE)) {
			community = getPrivateCommunity();
			Subcommunity subcomm = new Subcommunity(subCommTitle,
					subCommContent, commPermission, subCommTags);
			Entry subcommResponse = (Entry) service.createSubcommunity(
					community, subcomm);
			String communityEditLink = subcommResponse
					.getEditLinkResolvedHref().toString();
			ExtensibleElement communityElement = service
					.getCommunity(communityEditLink);
			privateSubCommunity = new Community((Entry) communityElement);
			if (privateSubCommunity != null) {
				return privateSubCommunity;
			}
		}
		if (commToWorkTitle
				.contains(PopStringConstantsAS.MODERATED_COMMUNITY_TITLE)) {
			community = getModeratedCommunity();
			Subcommunity subcomm = new Subcommunity(subCommTitle,
					subCommContent, commPermission, subCommTags);
			Entry subcommResponse = (Entry) service.createSubcommunity(
					community, subcomm);
			String communityEditLink = subcommResponse
					.getEditLinkResolvedHref().toString();
			ExtensibleElement communityElement = service
					.getCommunity(communityEditLink);
			moderatedSubCommunity = new Community((Entry) communityElement);

			if (moderatedSubCommunity != null) {
				return moderatedSubCommunity;
			}

		}
		return null;
	}

	public void createCommunityBookmarksAndTopic(Community community,
			String bookmarkTitle, String bookmarkContent, String bookmarkTags,
			String topicTitle, String topicContent) {
		LOGGER.fine("Creating Sub Community Bookmark and Topic in user's Communitie");

		createCommunityBookmark(community, bookmarkTitle, bookmarkContent,
				bookmarkTags);
		createCommunityTopic(community, topicTitle, topicContent);
	}

	public void populate() {
		try {
			
			createModeratedCommunity();
			createPublicCommunity();
			createPrivateCommunity();
			createPublicFrenchCommunityUnicode();
			createPrivateFrenchCommunity2Unicode();
		} catch (Exception e) {
			LOGGER.fine("Exception in communities population: "
					+ e.getMessage());
		}
	}

}
