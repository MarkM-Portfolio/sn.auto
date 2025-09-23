/**
 * 
 */
package com.ibm.lconn.automation.framework.services.activities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesTestBase;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.FieldElement;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

/**
 * JUnit Tests via Connections API for Activities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ActivitiesPopulate extends ActivitiesTestBase {

	private static ServiceConfig config;

	private static final String TEMPLATES_LINK = "http://www.ibm.com/xmlns/prod/sn/templates:application/atom+xml";

	private static final String HREF = "href";

	private static final String TEMPLATE_ENTRY_TITLE = "templateEntry";

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesPopulate.class.getName());

	// protected static FileHandler fh;

	/**
	 * Set Test users Environment
	 * 
	 * @throws Exception
	 *             if any of the above fail to initialize.
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Activities Data Population Test");

		// fh = new FileHandler("logs/" + Utils.logDateFormatter.format(new
		// Date()) + "_ActivitiesPopulate.xml", false);
		// LOGGER.addHandler(fh);

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.CURRENT_USER,
				StringConstants.CURRENT_USER, Component.ACTIVITIES.toString());
		user = userEnv.getLoginUser();
		service = user.getActivitiesService();
		//imUser = userEnv.getImpersonatedUser();
		//impersonatedService = imUser.getActivitiesService();

		// tjb original code 3.19.15
		// UsersEnvironment userEnv = new UsersEnvironment();
		// user = userEnv.getLoginUserEnvironment( StringConstants.CURRENT_USER,
		// Component.ACTIVITIES.toString());
		// service = user.getActivitiesService();
		imUser = user; // to deal with the verify part, which added for
		// impersonate test

		/*
		 * tjb 3.19.15 AssignedUser is a user that matches the default user (non
		 * impersonation). It is used to execute GET and DELETE calls which are
		 * not supported with impersonation. However, since the TestBase class
		 * supports impersonated and non impersonated tests, this user must also
		 * be defined in this Populate class.
		 */
		assignedUser = user;
		assignedService = service;

		config = user.getServiceConfig();

		LOGGER.debug("Finished Initializing Activities Data Population Test");
	}

	// TODO: Finish CSV file load implementation
	// @Test - Currently being implemented...
	// public void todoCSVTest() throws URISyntaxException, IOException {
	// System.out.println(Utils.parseCSV(new
	// File(this.getClass().getResource("/resources/todo1.csv").toURI())));
	// }
	@Test
	public void createActivityForBVTSearch() {
		Activity simpleActivity = new Activity("BVT Search Activity", "",
				"activitiessearchtag", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);

		Member newMember1;
		if (!config.isEmailHidden()) {
			newMember1 = new Member("*", null, Component.ACTIVITIES,
					Role.MEMBER, MemberType.GROUP);
		} else {
			newMember1 = new Member(null, "*", Component.ACTIVITIES,
					Role.MEMBER, MemberType.GROUP);
		}
		assertTrue(activityResult != null);

		ExtensibleElement addMemberResult = service.addMemberToActivity(
				activityResult.getLink(StringConstants.REL_MEMBERS).getHref()
						.toString(), newMember1);

		assertTrue(addMemberResult != null);

	}

	@Test
	public void deleteMember() throws FileNotFoundException, IOException,
			InterruptedException {
		LOGGER.debug("Beginning Test: Delete Member of an activity");
		service.deleteTests();

		Activity simpleActivity = new Activity("General Survival", "",
				"tornados earthquakes blizzards", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);

		boolean foundMember = false;
		String activityACLURL = activityResult
				.getLink(StringConstants.REL_MEMBERS).getHref().toString();
		Member newMember1;

		// Get test user profile
		ProfileData test_user = ProfileLoader.getProfile(5);

		String verify = "Member profile for " + test_user.getRealName();

		if (!config.isEmailHidden()
				|| StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newMember1 = new Member(test_user.getEmail(), null,
					Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		} else {
			newMember1 = new Member(null, test_user.getUserId(),
					Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		}
		assertTrue(activityResult != null);
		Entry addMemberResult = (Entry) service.addMemberToActivity(
				activityACLURL, newMember1);
		assertTrue(addMemberResult != null);

		// RTC 105900 verify content - only work after 11/2013
		assertEquals(verify, addMemberResult.getSummary());

		Feed myMembers = (Feed) service.getMemberFromActivity(activityACLURL);

		for (Entry e : myMembers.getEntries()) {
			if (e.getTitle().equals(test_user.getRealName())) {
				foundMember = true;
				assertTrue(true);
				LOGGER.debug("Member was successfully added to the activity");
			}
		}
		if (!foundMember) {
			LOGGER.debug("Test Failed: Member was not successfully added");
			assertTrue(false);
		}

		service.removeMemberFromActivity(addMemberResult
				.getEditLinkResolvedHref().toString());
		Thread.sleep(2000);
		myMembers = (Feed) service.getMemberFromActivity(activityACLURL);
		for (Entry e : myMembers.getEntries()) {
			if (e.getTitle().equals(test_user.getRealName())) {
				LOGGER.debug("Test Failed: Member was found and not deleted");
				Assert.fail("Test Failed: Member was found and not deleted");

			}
		}

		LOGGER.debug("Test Successful: Member was successfully deleted");
		assertTrue(true);
	}

	@Test
	public void testEntryTemplate() {
		LOGGER.debug("1: Create Entry Template");
		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmss");
		String date = tmformat.format(new Date());
		String actName = "Test" + date;
		Activity simpleActivity = new Activity(actName, "", "tags", new Date(
				System.currentTimeMillis() + 86400000), false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		Activity act = new Activity(activityResult);

		ActivityEntry ae = new ActivityEntry(TEMPLATE_ENTRY_TITLE, "",
				"templates", 0, false, null, activityResult, true);
		ExtensibleElement result = service.createEntryTemplate(act, ae);
		ActivityEntry resultEntry = new ActivityEntry((Entry) result);
		assert (resultEntry.getIsEntryTemplateCategory() != null);
		LOGGER.debug("COMPLETED TEST: Create Entry Template");

		LOGGER.debug("2: Retrieve Entry Templates");
		Link templateLink = act.getLinks().get(TEMPLATES_LINK);
		Feed feed = (Feed) service.getEntryTemplate(templateLink
				.getAttributeValue(HREF));
		List<Entry> entries = feed.getEntries();
		ae = null;
		for (Entry e : entries) {
			if (e.getTitle().equals(TEMPLATE_ENTRY_TITLE)) {
				ae = new ActivityEntry(e);
				break;
			}
		}
		assert (ae != null);
		assert (ae.getIsEntryTemplateCategory() != null);
		LOGGER.debug("COMPLETED TEST: Retrieve Entry Templates");

		LOGGER.debug("3: Update Entry Template");
		FieldElement l1 = new FieldElement(null, false, "Link", 0,
				FieldType.LINK, null, null);
		l1.setLinkToFileInfo("http://google.com", "File.jpg");

		FieldElement l2 = new FieldElement(null, false, "Link", 0,
				FieldType.LINK, null, null);
		l2.setLinkToFolderInfo("http://google.com", "Folder Name");

		FieldElement l3 = new FieldElement(null, false, "Link", 0,
				FieldType.LINK, null, null);
		l3.setLink("http://google.com", "Google");

		ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
		fields.add(l1);
		fields.add(l2);
		fields.add(l3);
		ae.setFields(fields);

		result = service.editEntryTemplate(ae.getEditLink(), ae);
		resultEntry = new ActivityEntry((Entry) result);
		assertTrue(resultEntry.getFields().size() == 3);
		LOGGER.debug("COMPLETED TEST: Update Entry Template");

		LOGGER.debug("4: Delete Entry Template");
		assertTrue(service.deleteEntryTemplate(ae.getEditLink()));
		LOGGER.debug("COMPLETED TEST: Delete Entry Template");
	}

	// This test is here because it has a dependency on default user
	// (StringConstants.USER_REALNAME)
	@Test
	public void getRecentUpdates() {
		LOGGER.debug("BEGINNING TEST: get Activites Recent Updates Feed");
		service.deleteTests();

		Activity simpleActivity = new Activity("General Survival", "",
				"tornados earthquakes blizzards", null, false, false);
		service.createActivity(simpleActivity);

		Feed updates = (Feed) service.getRecentUpdatesFeed();
		if (updates.getTitle().equals("Recent Updates")
				&& updates
						.getEntries()
						.get(0)
						.getTitle()
						.equals(StringConstants.USER_REALNAME
								+ " created activity \"General Survival\"")) {
			LOGGER.debug("SUCCESS: Activities Recent Updates Feed was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Activities Recent update feed was not found");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: Get Activities Recent Updates Feed");
	}

	// @Test need search index setup
	public void searchMyActivities() {
		LOGGER.debug("BEGINNING TEST: Search My Activities");
		service.deleteTests();

		Activity simpleActivity = new Activity("General Survival", "",
				"tornados earthquakes blizzards", null, false, false);
		service.createActivity(simpleActivity);

		Feed searchResults = (Feed) service
				.searchMyActivites("General Survival");

		if (searchResults.getTitle().equals(
				"Activity Dashboard for " + StringConstants.USER_REALNAME)
				&& searchResults.getEntries().get(0).getTitle()
						.equals("General Survival")) {
			LOGGER.debug("SUCCESS: Correct Search Results Found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Correct Search Results Not Found");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: Search My Activities");
	}

	@Test
	public void getMyActivitiesTest() {
		super.getMyActivitiesTest();
	}

	@Test
	public void createActivityWithEntries() {
		super.createActivityWithEntries();
	}

	@Test
	public void createActivityTemplateWithEntries() {
		super.createActivityTemplateWithEntries();
	}

	@Test
	public void entryTest() {
		super.entryTest();
	}

	@Test
	public void fileEntryTest() throws URISyntaxException, IOException {
		super.fileEntryTest();
	}

	@Test
	public void chatEntryTest() {
		super.chatEntryTest();
	}

	@Test
	public void emailEntryTest() {
		super.emailEntryTest();
	}

	@Test
	public void createToDoComment() {
		super.createToDoComment();
	}

	@Test
	public void updateActivityPriority() {
		super.updateActivityPriority();
	}

	@Test
	public void updateActivity() {
		super.updateActivity();
	}

	@Test
	public void restoreDeletedActivity() {
		super.restoreDeletedActivity();
	}

	@Test
	public void getAPIVersion() {
		super.getAPIVersion();
	}

	@Test
	public void getActivityOverview() {
		super.getActivityOverview();
	}

	@Test
	public void getActivitiesMemberInfo() {
		super.getActivitiesMemberInfo();
	}

	@Test
	public void getActivityDocument() {
		super.getActivityDocument();
	}

	@Test
	public void getActivityEntry() {
		super.getActivityEntry();
	}

	@Test
	public void getTags() {
		super.getTags();
	}

	@Test
	public void getDeletedActivities() {
		super.getDeletedActivities();
	}

	@Test
	public void verifyTotalResultMyActivities() {
		super.verifyTotalResultMyActivities();
	}

	@Test
	public void activitiesCSRF() throws FileNotFoundException, IOException {
		super.activitiesCSRF();
	}

	@Test
	public void activitiesUsers() throws FileNotFoundException, IOException {
		super.activitiesUsers();
	}

	@Test
	public void getActivityTagsLink() {
		super.getActivityTagsLink();
	}

	@Test
	public void getActivityTodoTagsLink() throws FileNotFoundException,
			IOException {
		super.getActivityTodoTagsLink();
	}

	@Test
	public void postEntrytoDeletedActivity() {
		super.postEntrytoDeletedActivity();
	}

	@Test
	public void postTodoComment() {
		super.postTodoComment();
	}

	@Test
	public void getActivityMembers() {
		super.addActivityMembers();
	}

	@Test
	public void testActivitySectionMove() throws Exception {
		super.testActivitySectionMove();
	}

	@Test
	public void templateHiddenFields() {
		super.templateHiddenFields();
	}

	@Test
	public void testHiddenFields() {
		super.testHiddenFields();
	}

	@Test
	public void getActivitySections() {
		super.getActivitySections();
	}

	@Test
	public void postActivityWithSpecialHeaders() {
		super.postActivityWithSpecialHeaders();
	}

	@Test
	public void commentTagRetrieval() {
		super.commentTagRetrieval();
	}
	
	@Test
	public void addActivityMembers() {
		super.addActivityMembers();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}

}
