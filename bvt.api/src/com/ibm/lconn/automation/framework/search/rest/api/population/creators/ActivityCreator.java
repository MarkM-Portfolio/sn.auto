package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.commons.lang.StringUtils;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.FieldElement;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Section;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.search.data.Application;

public class ActivityCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;
	private static Purpose content_purpose = Purpose.SEARCH_SCOPE;
	private ActivitiesService _activitiesService;

	private RestAPIUser restAPIUser;

	private String userName;
	private String userId;

	public ActivityCreator() throws Exception {
		restAPIUser = new RestAPIUser(UserType.LOGIN);
		userName = restAPIUser.getProfData().getUserName();
		userId = restAPIUser.getProfData().getUserId();
		ServiceEntry activitiesServiceEntry = restAPIUser.getService("activities");
		try {
			_activitiesService = new ActivitiesService(restAPIUser.getAbderaClient(), activitiesServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The activity is not created" + " LCServiceException: " + e.toString());
			assertTrue("Activity service problem, activity is not created", false);
		}

	}

	public ActivityCreator(UserType userType, int userIndex) throws Exception {
		restAPIUser = new RestAPIUser(userType, userIndex);
		userName = restAPIUser.getProfData().getUserName();
		userId = restAPIUser.getProfData().getUserId();
		ServiceEntry activitiesServiceEntry = restAPIUser.getService("activities");
		try {
			_activitiesService = new ActivitiesService(restAPIUser.getAbderaClient(), activitiesServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The activity is not created" + " LCServiceException: " + e.toString());
			assertTrue("Activity service problem, activity is not created", false);
		}

	}

	public void createActivity(Permissions permission) {
		createActivity(permission, Purpose.SEARCH);
	}

	public Entry createActivity(Permissions permission, Purpose purpose) {
		return createActivity(permission, purpose, null);
	}

	public Entry createJapaneseActivityWithTitle(String titleTemplate, Permissions permission, Purpose purpose,
			String tagName) {
		Entry response;
		String title = SearchRestAPIUtils.generateTitleFromTemplateString(titleTemplate, purpose);
		String tag = (tagName != null ? tagName : SearchRestAPIUtils.generateTagValue(purpose));
		String content = titleTemplate;
		response = createActivity(title, content, tag, permission, purpose);
		return response;
	}

	public Entry createActivityWithTitle(String titleTemplate, Permissions permission, Purpose purpose,
			String tagName) {
		Entry response;
		String title = SearchRestAPIUtils.generateTitleFromTemplateString(titleTemplate, purpose);
		String tag = (tagName != null ? tagName : SearchRestAPIUtils.generateTagValue(purpose));
		String content = SearchRestAPIUtils.generateDescription(title);
		response = createActivity(title, content, tag, permission, purpose);
		return response;
	}

	public Entry createActivity(Permissions permission, Purpose purpose, String tagName) {
		Entry response;
		String title = SearchRestAPIUtils.generateTitle(permission, Application.activity, purpose);
		String tag = (tagName != null ? tagName : SearchRestAPIUtils.generateTagValue(purpose));
		String content = SearchRestAPIUtils.generateDescription(title);
		response = createActivity(title, content, tag, permission, purpose);
		return response;
	}

	public Entry createActivityWithSection(Permissions permission, Purpose purpose, String tagName) {
		Entry response;
		String title = SearchRestAPIUtils.generateTitle(permission, Application.activity, purpose);
		String tag = (tagName != null ? tagName : SearchRestAPIUtils.generateTagValue(purpose));
		String content = SearchRestAPIUtils.generateDescription(title);
		response = createActivity(title, content, tag, permission, purpose);
		Section section = new Section("Section " + title, 0, response);
		_activitiesService.addNodeToActivity(response.getSelfLinkResolvedHref().toString(), section);
		return response;
	}

	public Entry createActivityWithTodo(Permissions permission, Purpose purpose, String tagName) {
		Entry response;
		String title = SearchRestAPIUtils.generateTitle(permission, Application.activity, purpose);
		String tag = (tagName != null ? tagName : SearchRestAPIUtils.generateTagValue(purpose));
		String content = SearchRestAPIUtils.generateDescription(title);
		response = createActivity(title, content, tag, permission, purpose);
		Collection activityNodeCollection = response.getExtension(StringConstants.APP_COLLECTION);
		Todo singleTodo = new Todo("Todo " + title, "Todo " + content, tag, 1, false, false, response, userName,
				userId);
		Entry newTodoResponse = (Entry) _activitiesService
				.addNodeToActivity(activityNodeCollection.getHref().toString(), singleTodo);
		Element codeEntryElement = newTodoResponse.getExtension(new QName("api", "code"));
		if (codeEntryElement != null) {
			LOGGER.log(Level.WARNING, "The activity todo is not created.");
			assertTrue("Activity service problem, activity todo is not created", false);

		} else {
			LOGGER.fine("Activity todo created: " + newTodoResponse.toString());
		}
		return response;
	}

	public Entry createActivity(String title, String content, String tag, Permissions permission, Purpose purpose) {
		return createActivity(title, content, tag, permission, purpose, true);
	}

	public Entry createActivity(String title, String content, String tag, Permissions permission, Purpose purpose,
			boolean addToPopulatedLcEntry) {
		Entry response;
		boolean isPublic = isPublic(permission);
		Activity activity = new Activity(title, content, tag, getTimeInHalfYear(), false, false);
		LOGGER.fine("Create activity: " + activity.toString());
		if (_activitiesService != null) {
			response = (Entry) _activitiesService.createActivity(activity);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The activity is not created.");
				assertTrue("Activity service problem, activity  is not created", false);
				return null;
			}
		} else {
			LOGGER.log(Level.WARNING, "The Activity service is NULL.");
			assertTrue("Activity service problem, activity is not created", false);
			return null;
		}
		Entry publicMemberEntry;
		if (isPublic) {
			if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
				Member newMember = getNewMember();
				addMemberToActivity(response, newMember);
			} else {
				// From makeActivityPublic
				Feed membersFeed = (Feed) _activitiesService
						.getFeed(response.getLink(StringConstants.REL_MEMBERS).getHref().toString());

				Person feedAuthor = membersFeed.getAuthor();

				String snxUserId = feedAuthor.toString();
				snxUserId = snxUserId.substring(snxUserId.indexOf("<snx:userid"), snxUserId.indexOf("</snx:userid>"));
				snxUserId = snxUserId.substring(snxUserId.lastIndexOf('>') + 1);

				Member publicMember = new Member(null, snxUserId, Component.ACTIVITIES, Role.MEMBER, null);
				publicMember.getContributors().clear();

				Person contributor = Abdera.getNewFactory().newContributor();
				for (Element currentElement : feedAuthor.getExtensions()) {
					if (currentElement.toString().indexOf("snx:role") > -1) {
						currentElement.setText("member");
					}
					contributor.addExtension(currentElement);
				}

				publicMember.addContributor(contributor);

				publicMemberEntry = publicMember.toEntry();
				publicMemberEntry.getCategories().get(0).setAttributeValue("term", "organization");

				Entry publicActivityResponse = (Entry) _activitiesService.postFeed(
						response.getLink(StringConstants.REL_MEMBERS).getHref().toString(), publicMemberEntry);

				if (publicActivityResponse.toString().indexOf("resp:error=\"true\"") == -1) {
					LOGGER.fine("INFO: The activity was successfully made public");

				} else {
					LOGGER.fine("ERROR: The activity could not be made public");
					LOGGER.fine(publicActivityResponse.toString());

				}

			}
		}
		if (addToPopulatedLcEntry) {
			PopulatedData.getInstance().setPopulatedLcEntry(activity, permission, purpose);
		}
		LOGGER.fine("Activity created: " + response.toString());

		return response;

	}

	private Member getNewMember() {
		Member newMember;
		if (!restAPIUser.getConfigService().isEmailHidden()) {
			newMember = new Member("*", null, Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		} else {
			newMember = new Member(null, "*", Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		}
		return newMember;
	}

	private void addMemberToActivity(Entry response, Member member) {
		ExtensibleElement addMemberResult = _activitiesService
				.addMemberToActivity(response.getLink(StringConstants.REL_MEMBERS).getHref().toString(), member);
		assertTrue(addMemberResult != null);
	}

	public void createActivityWithEntryAndReplyAnd4LangEntryUnify(Permissions permission, Purpose purpose,
			String langEntry1, String langEntry2, String langEntry3, String langEntry4) {
		String tag = SearchRestAPIUtils.generateTagValue(purpose); // tag is
		// inverted
		// execId
		Entry response = createActivity(permission, purpose, tag);
		String title = response.getTitle();
		String content = response.getContent();
		LOGGER.fine("Activity created: " + response.toString());
		createLangActivityEntry(response, content, title, tag, langEntry1, false, purpose);
		createLangActivityEntry(response, content, title, tag, langEntry2, false, purpose);
		createLangActivityEntry(response, content, title, "foo", langEntry3, true, purpose);
		createLangActivityEntry(response, content, title, tag, langEntry4, false, purpose);

		ActivityEntry activityEntry = new ActivityEntry("Entry " + title, "Entry " + content,
				"entry " + purpose + " " + tag, 0, false, null, response, false);
		String activityColUrl = ((Collection) response.getExtension(StringConstants.APP_COLLECTION)).getHref()
				.toString();
		Entry entryResponse = (Entry) _activitiesService.addNodeToActivity(activityColUrl, activityEntry);
		Element codeEntryElement = entryResponse.getExtension(new QName("api", "code"));
		if (codeEntryElement != null) {
			LOGGER.fine("The activity entry is not created.");
			return;
		}
		LOGGER.fine("Activity entry created: " + entryResponse.toString());
		Reply activityEntryReply = new Reply("Entry " + title, "Reply " + title, 0, false, entryResponse);

		Entry replyResponse = (Entry) _activitiesService.addNodeToActivity(activityColUrl, activityEntryReply);
		Element codeReplyElement = replyResponse.getExtension(new QName("api", "code"));
		if (codeReplyElement != null) {
			LOGGER.fine("The activity reply is not created.");
			return;
		}
		LOGGER.fine("Activity reply created: " + replyResponse.toString());
	}

	public void CreateActivityWithEntryAndAttachmentAndBookmark(Permissions permission, Purpose purpose)
			throws IOException {
		String tag = SearchRestAPIUtils.generateTagValue(purpose); // tag is
		// inverted
		// execId
		Entry response = createActivity(permission, purpose, tag);
		String title = response.getTitle();
		String content = response.getContent();

		ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
		FieldElement attachBookmark = new FieldElement(null, false, "Link", 0, FieldType.LINK, null, null);
		String titleForBookmark = SearchRestAPIUtils.getExecId(content_purpose);
		attachBookmark.setLink("http://google.com", titleForBookmark + " Link");
		fields.add(attachBookmark);
		ActivityEntry activityEntry = new ActivityEntry("Entry " + title, "Entry " + content,
				"entry " + purpose + " " + tag, 0, false, fields, response, false);
		String activityColUrl = ((Collection) response.getExtension(StringConstants.APP_COLLECTION)).getHref()
				.toString();
		File attachedFile = createTempFile(SearchRestAPIUtils.getExecId(content_purpose));
		Entry entryResponse = (Entry) _activitiesService.addMultipartNodeToActivity64(activityColUrl, activityEntry,
				attachedFile);
		Element codeEntryElement = entryResponse.getExtension(new QName("api", "code"));
		if (codeEntryElement != null) {
			LOGGER.log(Level.WARNING, "The activity attachment is not created.");
			assertTrue("Activity service problem, activity  is not created", false);
			return;
		}
		PopulatedData.getInstance().setPopulatedLcEntry((LCEntry) activityEntry, permission);
		LOGGER.fine("Activity created: " + response.toString());
		LOGGER.fine("Activity entry and attachment created: " + entryResponse.toString());
	}

	private void createLangActivityEntry(Entry response, String content, String title, String tag, String unifyWord,
			boolean ifShort, Purpose purpose) {
		ActivityEntry activityEntry;
		if (ifShort) {
			activityEntry = new ActivityEntry("Entry " + title,
					StringUtils.reverse(SearchRestAPIUtils.getExecId(purpose)) + " " + unifyWord, tag, 0, false, null,
					response, false);
		} else {
			activityEntry = new ActivityEntry("Entry " + title, "Entry " + unifyWord + " " + content,
					"entry " + purpose + " " + tag, 0, false, null, response, false);
		}
		String activityColUrl = ((Collection) response.getExtension(StringConstants.APP_COLLECTION)).getHref()
				.toString();
		Entry entryResponse = (Entry) _activitiesService.addNodeToActivity(activityColUrl, activityEntry);
		Element codeEntryElement = entryResponse.getExtension(new QName("api", "code"));
		if (codeEntryElement != null) {
			LOGGER.log(Level.WARNING, "The activity entry is not created.");
			assertTrue("Activity service problem, activity  is not created", false);
		} else {
			LOGGER.fine("Activity entry created: " + entryResponse.toString());
		}
	}

	private Date getTimeInHalfYear() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 6);
		return cal.getTime();
	}

	private File createTempFile(String execId) throws IOException {
		File tempFile = File.createTempFile(execId + " attach file", ".txt");
		tempFile.deleteOnExit();
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(SearchRestAPIUtils.contentForSearchExtracted(content_purpose));
		out.flush();
		out.close();
		return tempFile;
	}

	private boolean isPublic(Permissions permission) {
		boolean isPublic = true;
		if (Permissions.PRIVATE == permission) {
			isPublic = false;
		}
		return isPublic;
	}
}
