package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.population.solr.search.engine.CommunitiesSearchPopulationHelper;
import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiMemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.WikiRole;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.search.data.Application;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiMember;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class CommunitiesCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;
	private static Purpose content_purpose = Purpose.SEARCH_SCOPE;
	private CommunitiesService commService;
	private ActivitiesService _activitiesService;
	private WikisService wikisService;
	private FilesService fileService;
	RestAPIUser restAPIUser;

	public CommunitiesCreator() throws Exception {
		restAPIUser = new RestAPIUser(UserType.LOGIN);

		ServiceEntry commServiceEntry = restAPIUser.getService("communities");
		try {
			commService = new CommunitiesService(restAPIUser.getAbderaClient(), commServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The community is not created" + " LCServiceException: " + e.toString());
			assertTrue("Communities service problem, community is not created", false);
		}

	}

	public CommunitiesCreator(UserType userType, int userIndex) throws Exception {
		restAPIUser = new RestAPIUser(userType, userIndex);

		ServiceEntry commServiceEntry = restAPIUser.getService("communities");
		try {
			commService = new CommunitiesService(restAPIUser.getAbderaClient(), commServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The community is not created" + " LCServiceException: " + e.toString());
			assertTrue("Communities service problem, community is not created", false);
		}

	}

	public void createCommunity(Permissions permission) throws IOException {
		createCommunityWithPurpose(permission, Purpose.SEARCH);
	}

	public void createCommunity(Permissions permission, Purpose purpose) throws IOException {
		createCommunityWithPurpose(permission, purpose);
	}

	private void createCommunityWithPurpose(Permissions permission, Purpose purpose) throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.community, purpose);
		String tag = SearchRestAPIUtils.generateTagValue(purpose); // tag
		// is
		// inverted
		// execId
		String commDescription = SearchRestAPIUtils.generateDescription(title);// Content
		// is
		// inverted
		// title

		Community community = new Community(title.toString(), commDescription, permission, tag);
		LOGGER.fine("Create community: " + community.toString());

		if (commService != null) {
			ExtensibleElement response = commService.createCommunity(community);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The community is not created");
				assertTrue("Communities service problem, community is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(community, permission, purpose);
				LOGGER.fine("Community created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Communities service is NULL.");
			assertTrue("Communities service problem, community is not created", false);
		}
	}

	public void createCommunityWithForum(Permissions permission, Purpose purpose) throws IOException {
		final String methodName = "createCommunityWithForum";
		LOGGER.entering("CommunitiesCreator", methodName);
		CommunitiesSearchPopulationHelper communitiesHelper = new CommunitiesSearchPopulationHelper();
		String communityTitle = SearchRestAPIUtils.generateTitle(permission, Application.community, purpose);
		String communityContent = SearchRestAPIUtils.generateDescription(communityTitle);
		String communityTag = SearchRestAPIUtils.generateTagValue(purpose);
		Community community = communitiesHelper.createCommunity(communityTitle, communityContent, permission,
				communityTag);

		String topicTitle = SearchRestAPIUtils.generateTitle(permission, Application.forum, purpose);
		String topicContent = SearchRestAPIUtils.generateDescription(topicTitle);
		ForumTopic forumTopic = communitiesHelper.createCommunityTopic(community, topicTitle, topicContent, permission);

		String replyTitle = SearchRestAPIUtils.generateTitle(permission, Application.forum, purpose);
		String replyContent = SearchRestAPIUtils.generateDescription(replyTitle);

		ForumReply forumReply = new ForumReply(replyTitle, replyContent, forumTopic.toEntry(), false);
		communitiesHelper.createCommunityTopicReply(forumTopic, forumReply, permission);

		LOGGER.exiting("CommunitiesCreator", methodName);
	}

	public void createCommunityWithWikiAttachment(Permissions permission, Purpose communityPurpose,
			Purpose wikiFilePurpose) throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.community, communityPurpose);
		String tag = SearchRestAPIUtils.generateTagValue(communityPurpose);
		String commDescription = SearchRestAPIUtils.generateDescription(title);

		Community community = new Community(title.toString(), commDescription, permission, tag);
		LOGGER.fine("Create community: " + community.toString());

		if (commService != null) {
			Entry response = (Entry) commService.createCommunity(community);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The community is not created");

			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(community, permission);
				LOGGER.fine("Community created: " + response.toString());
				Community returnCommunity = new Community(
						(Entry) commService.getCommunity(response.getEditLinkResolvedHref().toString()));
				Populator.COMMUNITY_WITH_WIKI_ATTACHMENT_UUID = returnCommunity.getUuid();
				AddFileToCommunityWiki(returnCommunity, wikiFilePurpose);
			}
		} else {
			LOGGER.log(Level.WARNING, "The Communities service is NULL.");
		}
	}

	public void createCommunityWithWikiAndWikiPageinCloud(Permissions permission, Purpose communityPurpose)
			throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.community, communityPurpose);
		String tag = SearchRestAPIUtils.generateTagValue(communityPurpose);
		String commDescription = SearchRestAPIUtils.generateDescription(title);

		Community community = new Community(title.toString(), commDescription, permission, tag);
		LOGGER.fine("Create community: " + community.toString());

		if (commService != null) {
			Entry response = (Entry) commService.createCommunity(community);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The community is not created");

			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(community, permission);
				LOGGER.fine("Community created: " + response.toString());
				Community returnCommunity = new Community(
						(Entry) commService.getCommunity(response.getEditLinkResolvedHref().toString()));

				AddWikiAndWikiPageToCommunity(returnCommunity, communityPurpose);
			}
		} else {
			LOGGER.log(Level.WARNING, "The Communities service is NULL.");
		}
	}

	public void createCommunityWithFileAndActivity(Permissions permission, Purpose purpose) throws IOException {

		String title = SearchRestAPIUtils.generateTitle(permission, Application.community, purpose);
		String tag = SearchRestAPIUtils.generateTagValue(purpose); // tag
		// is
		// inverted
		// execId
		String commDescription = SearchRestAPIUtils.generateDescription(title);// Content
		// is
		// inverted
		// title

		Community community = new Community(title + " with File and Activity", commDescription, permission, tag);
		LOGGER.fine("Create community: " + community.toString());

		if (commService != null) {
			Entry response = (Entry) commService.createCommunity(community);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The community is not created");

			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(community, permission);
				LOGGER.fine("Community created: " + response.toString());
				Community returnCommunity = new Community(
						(Entry) commService.getCommunity(response.getEditLinkResolvedHref().toString()));
				Populator.COMMUNITY_WITH_FILE_AND_ACTIVITY_UUID = returnCommunity.getUuid();
				addActivityToCommunity(returnCommunity, permission, content_purpose);
				addFileToCommunity(returnCommunity, permission, content_purpose);
			}
		} else {
			LOGGER.log(Level.WARNING, "The Communities service is NULL.");
		}
	}

	private void addActivityToCommunity(Community returnCommunity, Permissions permission, Purpose purpose) {

		ServiceEntry activitiesServiceEntry = restAPIUser.getService("activities");
		try {
			_activitiesService = new ActivitiesService(restAPIUser.getAbderaClient(), activitiesServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING,
					"The activity in community is not created" + " LCServiceException: " + e.toString());
		}
		String title = SearchRestAPIUtils.generateTitle(permission, Application.activity, purpose) + " in community";
		String tag = SearchRestAPIUtils.generateTagValue(purpose);
		String content = SearchRestAPIUtils.generateDescription(title);
		Activity simpleActivity = new Activity(title, content, tag, getTimeInHalfYear(), false, true);

		String publishURL = _activitiesService.getServiceURLString() + URLConstants.ACTIVITIES_MY;

		Entry activityResult = (Entry) _activitiesService.createCommunityActivity(publishURL, simpleActivity,
				returnCommunity.getUuid(), "");

		if (activityResult != null && (activityResult.getAttributeValue(StringConstants.API_ERROR) == null
				|| Boolean.parseBoolean(activityResult.getAttributeValue(StringConstants.API_ERROR)) != true)) {
			LOGGER.fine("Activity in community: " + activityResult.getTitle() + " created successfully.");
			PopulatedData.getInstance().setPopulatedLcEntry(simpleActivity, permission, purpose);
		} else {

			LOGGER.fine("Activity in community creation failure.");

		}
	}

	private void addFileToCommunity(Community returnCommunity, Permissions permission, Purpose purpose)
			throws IOException {

		ServiceEntry filesServiceEntry = restAPIUser.getService("files");
		try {
			fileService = new FilesService(restAPIUser.getAbderaClient(), filesServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The file in community is not created" + " LCServiceException: " + e.toString());
		}
		String title = SearchRestAPIUtils.generateTitle(permission, Application.file, purpose) + " in community";
		String tag = SearchRestAPIUtils.generateTagValue(purpose);
		String description = SearchRestAPIUtils.generateDescription(title);

		String file_path = "/resources/Jellyfish.jpg";
		InputStream is = this.getClass().getResourceAsStream(file_path);

		FileEntry newFileEntry = new FileEntry(null, is, title, description, tag, Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true, SharePermission.EDIT, null, null);

		Entry fileResult = (Entry) fileService.uploadFileToCommunity(newFileEntry, returnCommunity);
		if (fileResult != null && (fileResult.getAttributeValue(StringConstants.API_ERROR) == null
				|| Boolean.parseBoolean(fileResult.getAttributeValue(StringConstants.API_ERROR)) != true)) {
			LOGGER.fine("File in community: " + fileResult.getTitle() + " created successfully.");
			PopulatedData.getInstance().setPopulatedLcEntry(newFileEntry, permission, purpose);
			String commentLink = fileResult.getLink(StringConstants.REL_REPLIES).getHref().toString();

			FileComment comment = new FileComment("File in community - this is a comment from file owner");
			Entry commentResult = (Entry) fileService.postFileComment(commentLink, comment);
		} else {

			LOGGER.fine("File in community creation failure.");

		}

	}

	private void AddFileToCommunityWiki(Community response, Purpose wikiFilePurpose) {
		ServiceEntry wikisServiceEntry = restAPIUser.getService("wikis");

		wikisService = new WikisService(restAPIUser.getAbderaClient(), wikisServiceEntry);
		if (wikisService != null) {
			Entry wikiOfCommunity = (Entry) wikisService.getWikiOfCommunity(response.getUuid());
			String title = SearchRestAPIUtils.generateTitleFromTemplateString("Wiki page for file attachment ",
					wikiFilePurpose);
			String tag = SearchRestAPIUtils.generateTagValue(wikiFilePurpose);
			String content = SearchRestAPIUtils.generateDescription(title);

			WikiPage newWikiPage = new WikiPage(title, content, tag);
			wikisService.createWikiPage(wikiOfCommunity, newWikiPage);

		}

		LOGGER.fine("");

	}

	private void AddWikiAndWikiPageToCommunity(Community response, Purpose wikiPurpose) {
		ServiceEntry wikisServiceEntry = restAPIUser.getService("wikis");

		wikisService = new WikisService(restAPIUser.getAbderaClient(), wikisServiceEntry);

		if (wikisService != null) {

			String title = SearchRestAPIUtils.generateTitleFromTemplateString("Wiki page in community wiki ",
					wikiPurpose);
			String tag = SearchRestAPIUtils.generateTagValue(wikiPurpose);
			String content = SearchRestAPIUtils.generateDescription(title);

			WikiPage newWikiPage = new WikiPage(title, content, tag);
			wikisService.createWikiPageInCommunity(response.getUuid(), newWikiPage);

		}

		LOGGER.fine("");

	}

	private Date getTimeInHalfYear() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 6);
		return cal.getTime();
	}

}
