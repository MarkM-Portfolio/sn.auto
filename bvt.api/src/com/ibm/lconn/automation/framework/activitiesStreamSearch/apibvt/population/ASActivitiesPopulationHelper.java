package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

// 16.02.2012
// During Community Activities creation error 500 received
// Can't provide permission to the setPermissions method (asked Piyush what value to provide) 

import java.util.logging.Logger;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.commons.lang.math.RandomUtils;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

public class ASActivitiesPopulationHelper {
	
	private static ActivitiesService service;
	
	private RestAPIUser restAPIUser;
	
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	public ASActivitiesPopulationHelper() throws Exception {
		restAPIUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry activitiesServiceEntry = restAPIUser
				.getService("activities");
		restAPIUser.addCredentials(activitiesServiceEntry);
		service = new ActivitiesService(
				restAPIUser.getAbderaClient(), activitiesServiceEntry);
		
	}

	
	
	
	
	public void createStandalonePublicActivity() {

		if (service != null) {

			Entry activityResult = createStandaloneActivity(
					PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_CONTENT,
					PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TAG, true);

			if (activityResult != null) {
				Collection activityNodeCollection = activityResult
						.getExtension(StringConstants.APP_COLLECTION);

				Entry entryResult = createActivityEntry(
						activityResult,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_TITLE,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_CONTENT,
						"Public",
						activityNodeCollection,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_TAG);

				createActivityEntryReply(
						entryResult,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_COMMENT_TITLE,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_COMMENT_CONTENT,
						"Public", activityNodeCollection);

				Entry entryResult1 = createActivityEntry(
						activityResult,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_PRIVATE_ENTRY_TITLE,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_PRIVATE_ENTRY_CONTENT,
						"Private",
						activityNodeCollection,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_PRIVATE_ENTRY_TAG);

				createActivityEntryReply(
						entryResult1,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_PRIVATE_COMMENT_TITLE,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_PRIVATE_COMMENT_CONTENT,
						"Private", activityNodeCollection);

				createActivityTodo(
						activityResult,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_CONTENT,
						"Public",
						activityNodeCollection,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TAG);

				createActivityTodo(
						activityResult,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_PRIVATE_TODO_TITLE,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_PRIVATE_TODO_CONTENT,
						"Private",
						activityNodeCollection,
						PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_PRIVATE_TODO_TAG);
			}
		}
	}

	public void createStandalonePrivateActivity() {
		if (service != null) {
			Entry activityResult = createStandaloneActivity(
					PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TITLE
							+ " " + PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_CONTENT,
					PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TAG,
					false);
			if (activityResult != null) {
				Collection activityNodeCollection = activityResult
						.getExtension(StringConstants.APP_COLLECTION);

				Entry entryResult = createActivityEntry(
						activityResult,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TITLE,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_CONTENT,
						"Public",
						activityNodeCollection,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TAG);

				createActivityEntryReply(
						entryResult,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_COMMENT_TITLE,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_COMMENT_CONTENT,
						"Public", activityNodeCollection);

				createActivityTodo(
						activityResult,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_CONTENT,
						"Public",
						activityNodeCollection,
						PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TAG);
			}
		}
	}

	public void populate() {
		try {
			
			createStandalonePrivateActivity();
			createStandalonePublicActivity();
		} catch (Exception e) {
			LOGGER.fine("Exception in communities population: "
					+ e.getMessage());
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public Entry createStandaloneActivity(String activityTitle,
			String activityContent, String activityTags, Boolean isPublic) {

		Activity standaloneActivity = new Activity(activityTitle,
				activityContent, activityTags, null, false, false);

		Entry activityCreationResult = (Entry) service
				.createActivity(standaloneActivity);
		if (activityCreationResult != null) {
			if (isPublic == true) {
				Member newMember1;
				if (!restAPIUser.getConfigService().isEmailHidden()) {
					newMember1 = new Member("*", null, Component.ACTIVITIES,
							Role.MEMBER, MemberType.GROUP);
				} else {
					newMember1 = new Member(null, "*", Component.ACTIVITIES,
							Role.MEMBER, MemberType.GROUP);
				}
				ExtensibleElement addMemberResult = service
						.addMemberToActivity(
								activityCreationResult
										.getLink(StringConstants.REL_MEMBERS)
										.getHref().toString(), newMember1);

				if (addMemberResult == null) {
					LOGGER.fine("Add member to activity failed.");
				}
			}
		} else {
			LOGGER.fine("Activity:" + activityTitle + " creation failed");
		}
		return activityCreationResult;
	}

	public Entry createActivityEntry(Entry entryParent, String entryTitle,
			String entryContent, String entryPermissions,
			Collection activityNCollection, String entryTags) {
		boolean newEntryPermissions = false;
		if (entryPermissions.equals("Private")) {
			newEntryPermissions = true;
		}

		ActivityEntry activityEntry = new ActivityEntry(entryTitle,
				entryContent, entryTags, RandomUtils.nextInt(),
				newEntryPermissions, null, entryParent, false);
		Entry entryCreationResult = (Entry) service.addNodeToActivity(
				activityNCollection.getHref().toString(), activityEntry);
		if (entryCreationResult == null) {
			LOGGER.fine("Failed entry:" + entryTitle + " creation.");
		}
		return entryCreationResult;
	}

	public Entry createActivityEntryReply(Entry replyParent, String replyTitle,
			String replyContent, String replyPermissions,
			Collection activityNCollection) {
		boolean newReplyPermissions = false;
		if (replyPermissions.equals("Private")) {
			newReplyPermissions = true;
		}

		Reply replyEntry = new Reply(replyTitle, replyContent,
				RandomUtils.nextInt(), newReplyPermissions, replyParent);
		Entry replyCreationResult = (Entry) service.addNodeToActivity(
				activityNCollection.getHref().toString(), replyEntry);
		if (replyCreationResult == null) {
			LOGGER.fine("Reply creation failed ");
		}
		return replyCreationResult;
	}

	public Entry createActivityTodo(Entry todoParent, String todoTitle,
			String todoContent, String todoPermissions,
			Collection activityNCollection, String todoTags) {
		boolean newTodoPermissions = false;
		if (todoPermissions.equals("Private")) {
			newTodoPermissions = true;
		}

		Todo todoEntry = new Todo(todoTitle, todoContent, todoTags,
				RandomUtils.nextInt(), false, newTodoPermissions, todoParent,
				null, null);
		Entry todoCreationResult = (Entry) service.addNodeToActivity(
				activityNCollection.getHref().toString(), todoEntry);
		if (todoCreationResult == null) {
			LOGGER.fine("Todo creation failed");
		}
		return todoCreationResult;
	}

}
