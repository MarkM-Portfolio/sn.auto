package com.ibm.lconn.automation.framework.services.activities.impersonated;

import static org.testng.AssertJUnit.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.ActivitiesService;
import com.ibm.lconn.automation.framework.services.activities.ActivitiesTestBase;
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.FieldElement;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Section;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

/**
 * JUnit Tests via Connections API for Activities Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class ActivitiesImpersonatedTest extends ActivitiesTestBase {

	static Abdera abdera = new Abdera();

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesImpersonatedTest.class.getName());

	// static UserPerspective user, imUser, otherUser, visitor,
	// extendedEmployee, localUser, impersonateByotherUser;
	// static ActivitiesService service, visitorService, extendedEmpService,
	// realUserService, localService, otherUserService;
	static UserPerspective impersonateByotherUser;

	static ActivitiesService otherUserService;

	private static boolean useSSL = true;

	private static Factory _factory = Abdera.getNewFactory();

	private static Date createdDate = new Date(1409656000); // fictitious

	// creation date
	private static Date lastModifiedDate = new Date(1409659000);

	private static String role_owner = "owner";

	private static String editUrl, aclUrl, editaclUrl, activityColUrl,
			entryEditUrl, edittodoUrl;

	private static Activity activity;

	private static String filePath = "/resources/dogs.txt";

	private static Entry setupActivityResult, setupTodoResult,
			setupEntryResult, setupSectionResult;

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Activities impersonation Test");

		int IMPERSONATED_USER_INDEX = StringConstants.CURRENT_USER;
		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				IMPERSONATED_USER_INDEX, Component.ACTIVITIES.toString());
		user = userEnv.getLoginUser();
		service = user.getActivitiesService();
		imUser = userEnv.getImpersonatedUser();
		/*
		 * For impersonation test: service belong to admin and localService
		 * belong to impersonated user Otherwise service and localService are
		 * the same in the sense that objects created are created by the same
		 * user.
		 */

		impersonateByotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.ACTIVITIES.toString(),
				useSSL, StringConstants.CURRENT_USER);
		otherUserService = impersonateByotherUser.getActivitiesService();

		/*
		 * tjb 2.5.15 AssignedUser is a user that matches the impersonated user.
		 * It is used to execute GET and DELETE calls which are not supported
		 * with impersonation.
		 */
		assignedUser = new UserPerspective(StringConstants.CURRENT_USER,
				Component.ACTIVITIES.toString(), useSSL,
				StringConstants.CURRENT_USER);
		assignedService = assignedUser.getActivitiesService();

		// TJB 2/12/15 Create Activity to be used for subsequent API tests.
		String randomEntryEnding = RandomStringUtils.randomAlphanumeric(4);
		String title = "Activity Impersonation " + randomEntryEnding;
		String content = "Content Impersonation " + randomEntryEnding;
		String tags = "apiimpersonation" + randomEntryEnding;

		LOGGER.debug("Creating Impersonation Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				RandomUtils.nextBoolean(new Random()), false);
		setupActivityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), setupActivityResult.getAuthor().getName());
		assertTrue(setupActivityResult != null);
		LOGGER.debug("Impersonation Activity: " + setupActivityResult.getTitle()
				+ " was created successfully.");

		// Create a Todo, Entry and Section
		Collection activityNodeCollection = setupActivityResult
				.getExtension(StringConstants.APP_COLLECTION);

		String todoTitle = "ToDo_" + randomEntryEnding;
		String todoContent = "ToDo Content " + randomEntryEnding;
		String entryTitle = "Entry_" + randomEntryEnding;
		String entryContent = "Entry Content " + randomEntryEnding;
		String sectionTitle = "Section_" + randomEntryEnding;

		LOGGER.debug("Create ToDo with impersonation.");
		Todo todoEntry = new Todo(todoTitle, todoContent, "tag1 tag2 tag3",
				1 * 1000, RandomUtils.nextBoolean(new Random()),
				RandomUtils.nextBoolean(new Random()), setupActivityResult,
				null, null);
		setupTodoResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), todoEntry);
		assertEquals("add Node to activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), setupTodoResult.getAuthor().getName());
		LOGGER.debug("ToDo: " + setupTodoResult.getTitle()
				+ " was created successfully.");

		LOGGER.debug("Create Entry with impersonation.");
		ActivityEntry activityEntry = new ActivityEntry(entryTitle,
				entryContent, "tag1 tag2 tag3", 1 * 1000, true, null,
				setupActivityResult, false);
		setupEntryResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), activityEntry);
		assertEquals("add entry to activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), setupEntryResult.getAuthor().getName());
		LOGGER.debug("Entry: " + setupEntryResult.getTitle()
				+ " was created successfully.");

		LOGGER.debug("Create Section with impersonation.");
		Section section = new Section(sectionTitle, 1 * 1000,
				setupActivityResult);
		setupSectionResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), section);
		assertTrue(setupSectionResult != null);
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), setupSectionResult.getAuthor().getName());
		LOGGER.debug("Section: " + setupSectionResult.getTitle()
				+ " was created successfully.");

		LOGGER.debug("Finished Initializing Activities impersonate Test");
	}

	/*
	 * Test list for activity API testing 1. create activity DONE 2. update
	 * activity DONE 3. create activity from template DONE 4. restore activity
	 * DONE
	 * 
	 * 5. create to do DONE 6. update to do DONE 7. create reply to entry DONE
	 * 8. update reply 9. create entry DONE 10. update entry DONE 11. create
	 * section DONE 12. update section DONE
	 * 
	 * 13. create to do from template 14. create reply from template 15. create
	 * entry from template 16. updating entry from template
	 * 
	 * 17. add member DONE 18. updating member
	 */

	/* The following tests depend on the activity created in setup */
	@Test
	public void addMember() {
		// create members for the created activity created in setup()
		Member newMember1 = new Member(StringConstants.RANDOM1_USER_EMAIL,
				null, Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);

		Entry addMemberResult = (Entry) service.addMemberToActivity(
				setupActivityResult.getLink(StringConstants.REL_MEMBERS)
						.getHref().toString(), newMember1);
		assertTrue(addMemberResult != null);
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), addMemberResult.getAuthor().getName());
		assertEquals("add menember", 201, service.getRespStatus());
	}

	@Test
	public void putTests() {
		/*
		 * This test validates updating ToDos, Entries and Sections via
		 * impersonation. This test relies on the activity created in the
		 * setup() method of this class. Updated titles are not reset at the end
		 * of this test. So, the titles set in setup activity are changed this
		 * test.
		 * 
		 * Process: 1. Update Todo title 2. Validate impersonation and title 3.
		 * Update Entry title 4. Validate impersonation and title 5. Update
		 * Section title 6. Validate impersonation and title
		 */
		LOGGER.debug("BEGINNING: put tests");

		String todoTitle = "Updated Todo name";
		setupTodoResult.setTitle(todoTitle);
		// update Todo
		Entry todoResult = (Entry) service.updateActivity(setupTodoResult
				.getEditLink().getHref().toString(), setupTodoResult);
		assertEquals("update activity", 200, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), todoResult.getAuthor().getName());
		assertEquals("updated title not found", todoTitle,
				todoResult.getTitle());

		String entryTitle = "Updated Entry Name";
		setupEntryResult.setTitle(entryTitle);
		// update Entry
		Entry ntryEntry = (Entry) service.updateActivity(setupTodoResult
				.getEditLink().getHref().toString(), setupEntryResult);
		assertEquals("update activity", 200, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), ntryEntry.getAuthor().getName());
		assertEquals("updated title not found", entryTitle,
				ntryEntry.getTitle());

		String sectionTitle = "Updated Section Name";
		setupSectionResult.setTitle(sectionTitle);
		// update Section
		Entry sectionEntry = (Entry) service.updateActivity(setupTodoResult
				.getEditLink().getHref().toString(), setupSectionResult);
		assertEquals("update activity", 200, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), sectionEntry.getAuthor().getName());
		assertEquals("updated title not found", sectionTitle,
				sectionEntry.getTitle());

		LOGGER.debug("ENDING: put tests");
	}

	/* End of tests depending on the activity created in setup */

	@Test
	public void createActivityWithEntries() {
		String title = "Impersonate Activity_"
				+ StringGenerator.randomSentence(3);
		String content = StringUtils.join(StringConstants.LOREM_1);
		String tags = "tagActivity_"
				+ Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				RandomUtils.nextBoolean(new Random()), false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), activityResult.getAuthor().getName());
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);

		LOGGER.debug("Creating 2 To-Do's in the Activity:");
		for (int j = 0; j < 2; j++) {
			String todoTitle = "ToDo_" + StringGenerator.randomLorem1Sentence();
			String todoContent = StringUtils.join(ArrayUtils.addAll(
					StringConstants.LOREM_1, StringConstants.LOREM_2));

			Todo todoEntry = new Todo(todoTitle, todoContent, "tag1 tag2 tag3",
					(j + 5) * 1000, RandomUtils.nextBoolean(new Random()),
					RandomUtils.nextBoolean(new Random()), activityResult,
					null, null);
			Entry todoResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), todoEntry);
			assertEquals("add Node to activity", 201, service.getRespStatus());
			assertEquals("impersonate userName check failed ",
					imUser.getRealName(), todoResult.getAuthor().getName());

			LOGGER.debug("\t\tTodo: " + todoResult.getTitle()
					+ " was created successfully.");
			// ActivitiesService.API_LOGGER.debug("Creating To-Do in the Activity");
		}
		LOGGER.debug("Finished creating 2 To-Do's in the Activity.");

		LOGGER.debug("Creating 2 Entries in the Activity:");
		for (int i = 0; i < 2; i++) {
			String entryTitle = StringGenerator.randomLorem2Sentence();
			String entryContent = StringUtils.join(ArrayUtils.addAll(
					StringConstants.LOREM_1, StringConstants.LOREM_2));

			ActivityEntry activityEntry = new ActivityEntry(entryTitle,
					entryContent, "tag1 tag2 tag3", (i + 3) * 1000, true, null,
					activityResult, false);
			Entry entryResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), activityEntry);
			assertEquals("add entry to activity", 201, service.getRespStatus());
			assertEquals("impersonate userName check failed ",
					imUser.getRealName(), entryResult.getAuthor().getName());

			LOGGER.debug("\tEntry: " + entryResult.getTitle()
					+ " was created successfully.");
			// ActivitiesService.API_LOGGER.debug("Creating Entry in the Activity");

			LOGGER.debug("Creating 1 Reply to the current Entry:");
			for (int j = 0; j < 1; j++) {
				String replyTitle = StringGenerator.randomLorem1Sentence();
				String replyContent = StringUtils.join(ArrayUtils.addAll(
						StringConstants.LOREM_1, StringConstants.LOREM_2));

				Reply replyEntry = new Reply(replyTitle, replyContent,
						j * 1000, true, entryResult);
				Entry replyResult = (Entry) service
						.addNodeToActivity(activityNodeCollection.getHref()
								.toString(), replyEntry);
				assertTrue(replyResult != null);
				LOGGER.debug("\t\tReply: " + replyResult.getTitle()
						+ " was created successfully.");
				assertEquals("impersonate userName check failed ",
						imUser.getRealName(), replyResult.getAuthor().getName());
			}
			LOGGER.debug("Finished creating 1 reply to the current Entry.");
		}
		LOGGER.debug("Finished creating 2 Entries in the Activity.");

		LOGGER.debug("Creating 3 Sections within the activity:");
		for (int i = 0; i < 3; i++) {
			String sectionTitle = StringGenerator.randomLorem2Sentence();
			Section section = new Section(sectionTitle, i * 1000,
					activityResult);
			Entry sectionResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), section);
			assertTrue(sectionResult != null);
			assertEquals("impersonate userName check failed ",
					imUser.getRealName(), sectionResult.getAuthor().getName());
			LOGGER.debug("Section: " + sectionResult.getTitle()
					+ " was created successfully.");

			LOGGER.debug("Creating 2 To-Do's in the current Section:");
			for (int j = 0; j < 2; j++) {
				String todoTitle = StringGenerator.randomLorem1Sentence();
				String todoContent = StringUtils.join(ArrayUtils.addAll(
						StringConstants.LOREM_1, StringConstants.LOREM_2));

				Todo todoEntry = new Todo(todoTitle, todoContent,
						"tag1 tag2 tag3", (j + 6) * 1000,
						RandomUtils.nextBoolean(new Random()),
						RandomUtils.nextBoolean(new Random()), sectionResult,
						null, null);
				Entry todoResult = (Entry) service.addNodeToActivity(
						activityNodeCollection.getHref().toString(), todoEntry);
				assertTrue(todoResult != null);
				LOGGER.debug("\t\tTodo: " + todoResult.getTitle()
						+ " was created successfully.");
				// ActivitiesService.API_LOGGER.debug("Creating To-Do in the Activity");
			}
			LOGGER.debug("Finished creating 2 To-Do's in the current Section.");

			String entryTitle = StringGenerator.randomLorem1Sentence();
			String entryContent = StringUtils.join(ArrayUtils.addAll(
					StringConstants.LOREM_1, StringConstants.LOREM_2));

			LOGGER.debug("Creating an Entry in the current Section.");
			ActivityEntry activityEntry = new ActivityEntry(entryTitle,
					entryContent, "tag1 tag2 tag3", i * 1000, true, null,
					sectionResult, false);
			Entry entryResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), activityEntry);
			assertTrue(entryResult != null);
			assertEquals("impersonate userName check failed ",
					imUser.getRealName(), entryResult.getAuthor().getName());
			LOGGER.debug("\tEntry: " + entryResult.getTitle()
					+ " was created successfully.");

			LOGGER.debug("Creating 1 Reply in the current Entry:");
			for (int j = 0; j < 1; j++) {
				String replyTitle = StringGenerator.randomLorem2Sentence();
				String replyContent = StringUtils.join(ArrayUtils.addAll(
						StringConstants.LOREM_1, StringConstants.LOREM_2));

				Reply replyEntry = new Reply(replyTitle, replyContent,
						(j + 2) * 1000, true, entryResult);
				Entry replyResult = (Entry) service
						.addNodeToActivity(activityNodeCollection.getHref()
								.toString(), replyEntry);
				assertTrue(replyResult != null);
				LOGGER.debug("\t\tReply: " + replyResult.getTitle()
						+ " was created successfully.");
				// ActivitiesService.API_LOGGER.debug("Creating Reply in the Activity");
			}

			LOGGER.debug("Finished creating 1 Reply in the current Entry.");
		}
	}

	@Test
	public void createActivityTemplateWithEntries() {
		String randomEntryEnding = RandomStringUtils.randomAlphanumeric(4);
		String title = "Activity Impersonation " + randomEntryEnding;
		String content = "Content Impersonation " + randomEntryEnding;
		String tags = "impersonationTagage" + randomEntryEnding;

		LOGGER.debug("Creating Activity template:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				RandomUtils.nextBoolean(new Random()), false);
		simpleActivity.setIsTemplate(true);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), activityResult.getAuthor().getName());
		assertTrue(activityResult != null);
		LOGGER.debug("Activity template: " + activityResult.getTitle()
				+ " was created successfully.");

		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);

		LOGGER.debug("Create Activity from the template previously created.");
		Activity template = new Activity(assignedService.getAllTemplates().get(
				0));
		template.setIsTemplate(false);
		template.setTitle("I've changed the title!"
				+ StringGenerator.randomStringWithSC(4));
		Entry activityResult2 = (Entry) service.createActivity(template);
		assertEquals("createActivity "+service.getDetail(),
				201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), activityResult2.getAuthor().getName());

		LOGGER.debug("Creating 2 Entries in the Activity:");
		for (int i = 0; i < 2; i++) {
			String entryTitle = "Entry_"
					+ StringGenerator.randomLorem2Sentence();
			String entryContent = StringUtils.join(ArrayUtils.addAll(
					StringConstants.LOREM_1, StringConstants.LOREM_2));

			ActivityEntry activityEntry = new ActivityEntry(entryTitle,
					entryContent, "tag1 tag2 tag3", (i + 3) * 1000, true, null,
					activityResult, true);
			Entry entryResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), activityEntry);
			assertTrue(entryResult != null);
			LOGGER.debug("\tEntry: " + entryResult.getTitle()
					+ " was created successfully.");

			LOGGER.debug("Creating 1 Reply to the current Entry:");
			for (int j = 0; j < 1; j++) {
				String replyTitle = "Reply_"
						+ StringGenerator.randomLorem1Sentence();
				String replyContent = StringUtils.join(ArrayUtils.addAll(
						StringConstants.LOREM_1, StringConstants.LOREM_2));

				Reply replyEntry = new Reply(replyTitle, replyContent,
						j * 1000, true, entryResult);
				Entry replyResult = (Entry) service
						.addNodeToActivity(activityNodeCollection.getHref()
								.toString(), replyEntry);
				assertTrue(replyResult != null);
				LOGGER.debug("\t\tReply: " + replyResult.getTitle()
						+ " was created successfully.");
			}
			LOGGER.debug("Finished creating 1 reply to the current Entry.");
		}
		LOGGER.debug("Finished creating 2 Entries in the Activity.");

		LOGGER.debug("Creating 3 Sections within the activity:");
		for (int i = 0; i < 2; i++) {
			String sectionTitle = "Sections_"
					+ StringGenerator.randomLorem2Sentence();
			Section section = new Section(sectionTitle, i * 1000,
					activityResult);
			Entry sectionResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), section);
			assertTrue(sectionResult != null);
			LOGGER.debug("Section: " + sectionResult.getTitle()
					+ " was created successfully.");
		}

	}

	@Test
	public void updateActivity() {
		LOGGER.debug("BEGINNING TEST: Update Activity");
		service.deleteTests();

		// Create an activity
		Activity simpleActivity = new Activity("Brogramming", "test",
				"activitiessearchtag", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), activityResult.getAuthor().getName());

		// Created activityEntry toUpdate
		Activity updatedActivity = new Activity("Updated", "test",
				"activitiessearchtag", null, false, false);
		// update
		ExtensibleElement eEle = service.editActivity(activityResult
				.getEditLink().getHref().toString(), updatedActivity);
		assertEquals("update activity", 200, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), ((Entry) eEle).getAuthor().getName());

		Entry entryToValidate = (Entry) assignedService
				.getActivity(activityResult.getEditLink().getHref().toString());
		assertEquals("get activity", 200, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), entryToValidate.getAuthor().getName());

		if (entryToValidate.getId().toString()
				.equals(activityResult.getId().toString())) {
			LOGGER.debug("SUCCESS: Activity was correctly updated");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Activity was not updated correctly");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Update Activity");
	}

	@Test
	public void restoreDeletedActivity() {
		/*
		 * Tests the ability to restore an activity from trash Step 1: Create an
		 * activity Step 2: Delete the activity Step 3: Restore the activity
		 * Step 4: Verify the activity exists
		 */
		LOGGER.debug("Beginning Test: Restore deleted activity");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create an activity");
		Activity testActivity = new Activity("TestRestore" + dateCode,
				"I've come back to life!", null, null, false, false);
		Entry result = (Entry) service.createActivity(testActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), result.getAuthor().getName());

		LOGGER.debug("Step 2: Delete the activity");
		assertTrue(assignedService.deleteActivity(result.getEditLink()
				.getHref().toString()));

		LOGGER.debug("Step 3: Restore the activity");
		String activityIdElement = result.getId().toString();
		String activityUuid = activityIdElement.substring(activityIdElement
				.indexOf("oa:") + 3);

		Entry trashedEntry = (Entry) assignedService
				.getFeed(service.getServiceURLString()
						+ "/service/atom2/trashednode?activityNodeUuid="
						+ activityUuid);
		trashedEntry.getCategories().get(2).discard(); // remove the "deleted"
		// flag
		Entry ntry = (Entry) service.updateActivity(trashedEntry.getEditLink()
				.getHref().toString(), trashedEntry);
		// Impersonation can not be validated at this point. The return is HTTP
		// 204, no author information appears in the entry
		assertEquals("update activity", 204, service.getRespStatus());

		LOGGER.debug("Step 4: Verify the activity exists");
		ArrayList<Activity> activities = assignedService.getMyActivities();
		boolean foundActivity = false;
		for (Activity activity : activities) {
			if (activity.getTitle().equals(testActivity.getTitle()))
				foundActivity = true;
		}
		assertTrue(foundActivity);
	}

	@Test
	public void commentTagRetrieval() {
		/**
		 * This test validates that tags are returned in the entry for
		 * comments(replies) using the following apis: 1. activity?activityUuid
		 * 2. nodechildren 3. activitydescendants
		 * 
		 * Process: Step 1: Create an activity and get its Uuid. Step 2: Create
		 * an entry under the created activity. Step 3: Post a comment to the
		 * entry. Step 4: Verify that the comment was posted to the entry. Step
		 * 5: Verify that the tags are in the entry of the comment (reply) using
		 * activity?activityUuid Step 6: Verify that the tags are in the entry
		 * of the comment (reply) using nodechildren Step 7: Verify that the
		 * tags are in the entry of the comment (reply) using
		 * activitydescendants
		 * 
		 * Although this test is primarily for retrieving comment tags, entry
		 * tags are also validated.
		 * 
		 */

		LOGGER.debug("BEGINNING TEST: Getting tags from activity reply nodes.  OCS RTC 136794");
		String randomEntryEnding = RandomStringUtils.randomAlphanumeric(4);
		String commentContent = "This is the comment.  OCS RTC 136794 "
				+ randomEntryEnding;
		String commentTitle = "This is the comment title";
		String entryTitle = "This is the entry title.";
		String entryContent = "This is the entry content";

		LOGGER.debug("Step 1: Create the activity and get its Uuid");
		Activity newActivity = new Activity("RTC OCS 136794 "
				+ randomEntryEnding, "Retrieve tags from feed of reply nodes",
				"ActivityLevel_Tag1 ActivityLevel_Tag2", null, false, false);
		Entry newActivityResponse = (Entry) service.createActivity(newActivity);
		validateEntry(newActivityResponse);
		String selfLink = newActivityResponse.getSelfLinkResolvedHref()
				.toString();

		LOGGER.debug("Step 2: Create a entry under the created activity");
		Collection activityNodeCollection = newActivityResponse
				.getExtension(StringConstants.APP_COLLECTION);
		ActivityEntry activityEntry = new ActivityEntry(entryTitle,
				entryContent, "EntryLevel_Tag1 EntryLevel_Tag2", 0, true, null,
				newActivityResponse, false);
		Entry entryResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), activityEntry);
		assertTrue(entryResult != null);
		validateEntry(entryResult);

		// Comment and Reply are the same thing. UI uses the term "comment" and
		// API uses "reply"
		LOGGER.debug("Step 3: Post a comment to the entry");
		Reply commentReply = new Reply(commentTitle, commentContent, 0, false,
				entryResult);
		commentReply.setTags("ComReplyLevel_Tag1 ComReplyLevel_Tag2");
		String url = activityNodeCollection.getHref().toString();
		service.addNodeToActivity(url, commentReply);

		LOGGER.debug("Step 4: Verify that the comment was posted to the Entry");
		Feed commentFeed = (Feed) assignedService
				.getToDoCommentFeed(activityEntry.getActivityId());
		assertTrue(commentFeed.getEntries().get(0).getContent().trim()
				.equals(commentContent));
		assertTrue(commentFeed.getEntries().get(0).getTitle()
				.equals(commentTitle));

		LOGGER.debug("Step 5: Verify that the tags are in the entry of the comment (reply) using activity?activityUuid");
		Feed activityFeed = (Feed) assignedService.getActivity(selfLink); // uses
		// activities/service/atom2/activity?activityUuid=[activity
		// uid]

		boolean entryTag1Found = false;
		boolean entryTag2Found = false;
		boolean commentTag1Found = false;
		boolean commentTag2Found = false;
		String childrenLink = "";
		for (Entry ntry : activityFeed.getEntries()) {
			validateEntry(ntry);
			if (ntry.getTitle().equalsIgnoreCase(entryTitle)) {
				List<Category> catList = ntry.getCategories();
				for (Category cat : catList) {
					if (cat.getTerm().equalsIgnoreCase("EntryLevel_Tag1")) {
						entryTag1Found = true;
					}
					if (cat.getTerm().equalsIgnoreCase("EntryLevel_Tag2")) {
						entryTag2Found = true;
					}
				}
				childrenLink = ntry.getLinkResolvedHref("children").toString();

			} else if (ntry.getTitle().equalsIgnoreCase(commentTitle)) {
				List<Category> catList = ntry.getCategories();
				for (Category cat : catList) {
					if (cat.getTerm().equalsIgnoreCase("ComReplyLevel_Tag1")) {
						commentTag1Found = true;
					}
					if (cat.getTerm().equalsIgnoreCase("ComReplyLevel_Tag2")) {
						commentTag2Found = true;
					}
				}
			}
		}
		// Validate that the tags were found for the entry and the comment
		// (reply).
		assertEquals(true, entryTag1Found && entryTag2Found);
		assertEquals(true, commentTag1Found && commentTag2Found);

		LOGGER.debug("Step 6: Verify that the tags are in the entry of the comment (reply) using nodechildren");
		Feed entryFeed = (Feed) assignedService.getActivity(childrenLink); // uses
		// activities/service/atom2/nodechildren?nodeUuid=[entry
		// Uuid]
		commentTag1Found = false;
		commentTag2Found = false;

		for (Entry ntry2 : entryFeed.getEntries()) {
			if (ntry2.getTitle().equalsIgnoreCase(commentTitle)) {
				List<Category> catList = ntry2.getCategories();
				for (Category cat : catList) {
					if (cat.getTerm().equalsIgnoreCase("ComReplyLevel_Tag1")) {
						commentTag1Found = true;
					}
					if (cat.getTerm().equalsIgnoreCase("ComReplyLevel_Tag2")) {
						commentTag2Found = true;
					}
				}
			}
		}
		// Validate that the tags are in the comment (reply).
		assertEquals(true, commentTag1Found && commentTag2Found);
		// Reset flags for next test
		commentTag1Found = false;
		entryTag1Found = false;
		commentTag2Found = false;
		entryTag2Found = false;

		LOGGER.debug("Step 7: Verify that the tags are in the entry of the comment using activitydescendants"); // uses
		// activities/service/atom2/activitydescendants?activityUuid=[activity
		// uid]
		// activitydescendants is a private API for now, but used by customer.
		// Manually constructed for now.
		String descendantLink = selfLink.replaceFirst("activity",
				"activitydescendants");
		Feed entryFeed2 = (Feed) assignedService.getActivity(descendantLink);

		for (Entry ntry : entryFeed2.getEntries()) {
			validateEntry(ntry);
			if (ntry.getTitle().equalsIgnoreCase(entryTitle)) {
				List<Category> catList = ntry.getCategories();
				for (Category cat : catList) {
					if (cat.getTerm().equalsIgnoreCase("EntryLevel_Tag1")) {
						entryTag1Found = true;
					}
					if (cat.getTerm().equalsIgnoreCase("EntryLevel_Tag2")) {
						entryTag2Found = true;
					}
				}

			} else if (ntry.getTitle().equalsIgnoreCase(commentTitle)) {
				List<Category> catList = ntry.getCategories();
				for (Category cat : catList) {
					if (cat.getTerm().equalsIgnoreCase("ComReplyLevel_Tag1")) {
						commentTag1Found = true;
					}
					if (cat.getTerm().equalsIgnoreCase("ComReplyLevel_Tag2")) {
						commentTag2Found = true;
					}
				}
			}
		}
		// Validate that the tags were found for the entry and the comment
		// (reply).
		assertEquals(true, entryTag1Found && entryTag2Found);
		assertEquals(true, commentTag1Found && commentTag2Found);

		LOGGER.debug("END TEST: Getting tags from activity reply nodes.  OCS RTC 136794");
	}

	// @Test
	// TJB 2/5/15 This test is commented out, probably because the
	// addMemberToActivity throws and error.
	// TODO research this. Is it a defect?
	public void createActivityForBVTSearchImp() {
		Activity simpleActivity = new Activity("BVT Search Activity", "",
				"activitiessearchtag", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		// Validate entry
		validateEntry(activityResult);

		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);

		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();

		// Get service config for server, assert that it was retrieved and
		// contains the activities service information
		ServiceConfig config = null;
		try {
			config = new ServiceConfig(client,
					URLConstants.SERVER_URL, useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}

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

		// Validation
		for (Element ele : addMemberResult.getElements()) {
			if (ele.toString().startsWith("<author")) {
				for (Element el : ele.getElements()) {
					if (el.toString().startsWith("<name")) {
						assertEquals("Wrong author: ", true, el.getText()
								.equalsIgnoreCase(imUser.getRealName()));
					}
				}
			}
		}
	}

	/*
	 * Validation method for most Activity POST/PUT ops Simply finds the author
	 * and validates that is is the impersonated user. This validates only the
	 * name, not the email, userid or other values.
	 */
	private void validateEntry(Entry ntry) {
		for (Element ele : ntry.getElements()) {
			if (ele.toString().startsWith("<author")) {
				for (Element el : ele.getElements()) {
					if (el.toString().startsWith("<name")) {
						assertEquals("Wrong author: ", true, el.getText()
								.equalsIgnoreCase(imUser.getRealName()));
					}
				}
			}
		}

	}

	/*
	 * TJB 2.6.15 This test seems to break other tests. Need more research. Is
	 * this a bug?
	 * 
	 * Cross Org Test This test requires a second organization with a regular
	 * user. This test only executes on SC as on prem does not have more than
	 * one org.
	 * 
	 * Test cases: b. Org A Admin uses Org B regular user for impersonation in
	 * org a. The expected result is that the created artifact is authored by
	 * the org a admin, not the org b regular user!
	 * 
	 * Test risk. Org B may not be set up on all SC servers.
	 */
	// @Test
	public void crossOrgTest() throws FileNotFoundException, IOException {
		LOGGER.debug("Beginning test RTC 136834: Cross org operations");
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";

			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			int ORG_B_REGULAR_USER_INDEX = 15;

			UserPerspective orgBRegular=null;
			try {
				orgBRegular = new UserPerspective(
						ORG_B_REGULAR_USER_INDEX, Component.ACTIVITIES.toString(),
						useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LOGGER.debug("Step 1: Create an activity with default impersonated user.");
			Activity activity = new Activity(
					"ORG A Test " + uniqueNameAddition,
					"This test ensures that OrgA admin impersonation works.",
					null, null, false, false);
			Entry activityResult = (Entry) service.createActivity(activity);
			assertEquals("create activity", 201, service.getRespStatus());
			assertEquals("impersonate userName check failed ",
					imUser.getRealName(), activityResult.getAuthor().getName());

			LOGGER.debug("Step 2: Change the value of X-LConn_RunAs user to an Org b regular user.");
			LOGGER.debug("Before setting the request option: "
					+ service.getRequestOption("X-LConn-RunAs"));
			service.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId());
			LOGGER.debug("After setting the request option: "
					+ service.getRequestOption("X-LConn-RunAs")
					+ " This should be different than before.");

			LOGGER.debug("Step 3: Create a new activity where org a admin uses org b user for impersonation.");
			Activity simpleActivity = new Activity("Cross Org Test "
					+ uniqueNameAddition,
					"Org A Admin using org B regular user", null, null, false,
					false);
			Entry activityResult2 = (Entry) service
					.createActivity(simpleActivity);
			assertEquals("create activity", 201, service.getRespStatus());
			// Notice the expected author is the admin user - not the org b
			// regular user!
			assertEquals("Impersonate author check failed ",
					user.getRealName(), activityResult2.getAuthor().getName());

			LOGGER.debug("Ending test RTC 136834: Cross org operations");
		}
	}

	@Test
	public void impersonateWithRegularUser() {
		/**
		 * 
		 * Try to create an activity using a non-admin, regular user
		 * impersonating as another regular user. The expected response is that
		 * the activity is created and owned by the user attempting
		 * impersonation.
		 * 
		 */

		LOGGER.debug("BEGINNING TEST: Impersonate by a non-admin account");
		String randomEntryEnding = RandomStringUtils.randomAlphanumeric(4);
		String activityName = "Impersonate as non-admin, regular user "
				+ randomEntryEnding;

		LOGGER.debug("Step 1: Create the activity with impersonation as regular user.");
		Activity newActivity = new Activity(activityName,
				"Try impersonation as a regular user", "impersonation", null,
				false, false);
		Entry newActivityResponse = (Entry) otherUserService
				.createActivity(newActivity);

		for (Element ele : newActivityResponse.getElements()) {
			if (ele.toString().startsWith("<author")) {
				for (Element el : ele.getElements()) {
					if (el.toString().startsWith("<name")) {
						LOGGER.debug("From the server entry: " + el.getText()
								+ ".  The expected value: "
								+ impersonateByotherUser.getRealName());
						assertEquals(
								"Wrong author: ",
								true,
								el.getText().equalsIgnoreCase(
										impersonateByotherUser.getRealName()));
					}
				}
			}
		}

		LOGGER.debug("ENDING TEST: Impersonate by a non-admin account");
	}

	/*
	 * Cross Org Test GET and DELETE are not supported by activities
	 * 
	 * Test cases: a. on premise, GET will return 200 b. on sc, GET will return
	 * 403 c. on sc, DELETE will return 403 d. on prem, DELETE will return 204
	 */
	@Test
	public void testGetAndDelete() {
		LOGGER.debug("START TEST: Impersonate test GET and DELETE");
		String uniqueSuffix = RandomStringUtils.randomAlphanumeric(4);
		String activityName = "Test GET and DELETE " + uniqueSuffix;

		LOGGER.debug("Step 1: Create the activity");
		Activity simpleActivity = new Activity(activityName,
				"Test GET and DELETE w/ Impersonation", null, null, false,
				false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		LOGGER.debug("impersonate userName check failed " + imUser.getRealName()
				+ " and " + activityResult.getAuthor().getName());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), activityResult.getAuthor().getName());

		LOGGER.debug("Step 2: GET the activity, should fail on smart cloud");
		Entry entryToValidate = (Entry) service.getActivity(activityResult
				.getEditLink().getHref().toString());
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD &&
				StringConstants.ORGADMINGK.equalsIgnoreCase("false")) {
			assertEquals("GET activity", 403, service.getRespStatus());
		} else {
			assertEquals("GET activity", 200, service.getRespStatus());
		}

		LOGGER.debug("Step 3: DELETE the activity, should fail on smart cloud");
		service.deleteActivity(activityResult.getEditLink().getHref()
				.toString());
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD &&
				StringConstants.ORGADMINGK.equalsIgnoreCase("false")) {
			assertEquals("GET activity", 403, service.getRespStatus());
		} else {
			assertEquals("GET activity", 204, service.getRespStatus());
		}

		LOGGER.debug("END TEST: Impersonate test GET and DELETE");
	}

	// /////////////////////////////////////////// THESE TESTS WERE WRITTEN AND
	// OWNED BY ACTIVITIES TEAM
	// //////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * TJB 2/5/15 - These tests have a couple problems: 1. Except for
	 * testImpersonatedCreateUpdateActivity, the tests are not executable
	 * individually. This is because the tests rely on class instance variables
	 * ("aclUrl" for example), that are only set when the entire test suite is
	 * executed. These must be fixed. These tests should be independently
	 * executable.
	 * 
	 * 2. I don't think these tests were ever validated on SC w/ impersonation.
	 * testImpersonatedCreateUpdateActivity will fail executing
	 * getMemberFromActivity. As result i've stopped these tests from running on
	 * SC. Activities team needs to investigate to determine if this is a
	 * problem with the test or an actual defect.
	 * 
	 * 2/26/15 Commenting all of the following tests. Because the runtime order
	 * of the tests is not fixed, the test that creates the activity may not be
	 * first, and tests dependent on that activity will fail.
	 */
	// @Test
	public void testImpersonatedCreateUpdateActivity() throws Exception {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			/* start: Create a test activity */
			Entry actEntry = _factory.newEntry();

			actEntry.setTitle("knightsw");
			actEntry.setContent("Save Dulcinea from evil giants");
			actEntry.setPublished(createdDate);

			// Get service config for server, assert that it was retrieved and
			// contains the activities service information
			// ActivitiesServiceConfig config1 = new
			// ActivitiesServiceConfig(client, URLConstants.SERVER_URL, true);
			// assert (config1.getService("activities") != null);

			// Map<String, String> headers = new HashMap<String, String>();
			// headers.put(impersonationHeaderKey,
			// impersonationHeaderValue_userid + "=" +
			// impersonatedUser.getUserId());

			// Retrieve the activities service document and assert that it
			// exists
			// as = new ActivitiesService(client,
			// config1.getService("activities"), headers);
			// assert (as.isFoundService());

			Category tag1 = _factory.newCategory();
			Category tag2 = _factory.newCategory();
			tag1.setTerm("tagone");
			tag2.setTerm("tagtwo");
			actEntry.addCategory(tag1);
			actEntry.addCategory(tag2);

			// add an activity for test
			Activity simpleActivity = new Activity(actEntry);

			// Negative Test for impersonate
			Entry activityResult_impersonateByotherUser = (Entry) otherUserService
					.createActivity(simpleActivity);
			assertEquals("Create Activity failed", 201,
					otherUserService.getRespStatus());
			assertEquals(
					"Non admin can't impersonate, should just return itself as auth",
					impersonateByotherUser.getRealName(),
					activityResult_impersonateByotherUser.getAuthor().getName());

			Entry activityResult = (Entry) service
					.createActivity(simpleActivity);
			assertTrue(activityResult != null);
			assertEquals(201, service.getRespStatus());
			if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD))
				assertEquals(createdDate.getTime(), activityResult
						.getPublished().getTime()); // TODO: not working on SC
			assertEquals(imUser.getRealName(), activityResult.getAuthor()
					.getName());
			boolean foundtags = false;
			List<Category> categories = activityResult.getCategories();
			for (Category cat : categories) {
				if (cat.getTerm().equalsIgnoreCase("tagone")
						|| cat.getTerm().equalsIgnoreCase("tagtwo")) {
					foundtags = true;
				}
			}
			assertTrue(foundtags);
			editUrl = activityResult.getEditLink().getHref().toURL().toString();
			aclUrl = activityResult
					.getLink("http://www.ibm.com/xmlns/prod/sn/member-list")
					.getHref().toURL().toString();
			activityColUrl = ((Collection) activityResult
					.getExtension(StringConstants.APP_COLLECTION)).getHref()
					.toString();
			Feed myMembers = (Feed) service.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			assertEquals(
					"Expected number of activity members is 1, but the actual value is "
							+ members.size(), 1, members.size());
			for (Entry member : members) {
				assertEquals(member.getContributors().get(0).getName(),
						imUser.getRealName());
				assertEquals(member.getExtension(StringConstants.SNX_ROLE)
						.getText(), role_owner);
			}

			Entry actEntry2 = _factory.newEntry();
			actEntry2.setTitle("1knights_updated");
			actEntry2.setContent("Save Dulcinea from evil giants_updated");
			actEntry2.setUpdated(lastModifiedDate);
			Category isActivityCategory = _factory.newCategory();
			isActivityCategory.setScheme(StringConstants.SCHEME_TYPE);
			isActivityCategory
					.setTerm(StringConstants.STRING_ACTIVITY_LOWERCASE);
			isActivityCategory
					.setLabel(StringConstants.STRING_ACTIVITY_CAPITALIZED);
			actEntry2.addCategory(isActivityCategory);
			Activity simpleActivity2 = new Activity(actEntry2);

			// Negative Test for impersonate
			Entry activityResult2_impersonateByotherUser = (Entry) otherUserService
					.editActivity(editUrl, simpleActivity2);
			assertEquals("Non admin impersonate edit Activity should failed",
					403, otherUserService.getRespStatus());

			Entry activityResult2 = (Entry) service.editActivity(editUrl,
					simpleActivity2);
			activity = new Activity(activityResult2);
			assertTrue(activityResult2 != null);
			assertEquals(200, service.getRespStatus());
			assertEquals(lastModifiedDate.getTime(), activityResult2
					.getUpdated().getTime());
		}
	}

	// @Test
	public void testImpersonatedCreateUpdateAcl() throws Exception {
		// Skip this test if running on SC.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			ProfileData newProfile = ProfileLoader.getProfile(3);
			Member newMember = new Member(newProfile.getEmail(),
					newProfile.getUserId(), Component.ACTIVITIES, Role.OWNER,
					MemberType.PERSON);
			newMember.setPublished(lastModifiedDate);
			Entry addMemberResult = (Entry) service.addMemberToActivity(aclUrl,
					newMember);
			assertTrue(addMemberResult != null);
			editaclUrl = addMemberResult.getEditLink().getHref().toURL()
					.toString();
			Feed myMembers = (Feed) service.getMemberFromActivity(aclUrl);
			List<Entry> members = myMembers.getEntries();
			boolean found = false;
			assertEquals(
					"Expected number of activity members is 2, but the actual value is "
							+ members.size(), 2, members.size());
			for (Entry member : members) {
				if (member.getContributors().get(0).getName()
						.equalsIgnoreCase(newProfile.getRealName())) {
					found = true;
					assertEquals(member.getContributors().get(0)
							.getSimpleExtension(StringConstants.SNX_ROLE),
							"owner");
				}
			}
			assertTrue(found);

			Member updatedMember = new Member(newProfile.getEmail(),
					newProfile.getUserId(), Component.ACTIVITIES, Role.READER,
					MemberType.PERSON);
			Entry updatedMemberResult = (Entry) service.updateMemberInActivity(
					editaclUrl, updatedMember);
			assertTrue(updatedMemberResult != null);

			myMembers = (Feed) service.getMemberFromActivity(aclUrl);
			members = myMembers.getEntries();
			found = false;
			assertEquals(
					"Expected number of activity members is 2, but the actual value is "
							+ members.size(), 2, members.size());
			for (Entry member : members) {
				if (member.getContributors().get(0).getName()
						.equalsIgnoreCase(newProfile.getRealName())) {
					found = true;
					assertEquals(member.getContributors().get(0)
							.getSimpleExtension(StringConstants.SNX_ROLE),
							"reader");
				}
			}
			assertTrue(found);
		}

	}

	// @Test
	public void testImpersonatedCreateUpdateTodo() throws Exception {
		// Skip this test if running on SC.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			/* start: create a todo */
			Date createTodoDate = new Date(1409759000);
			Date updateTodoDate = new Date(1409859000);
			Entry entry = _factory.newEntry();
			entry.setTitle("Don Quixote Homepage - Todo");
			entry.setContent("Save Dulcinea from evil giants.");
			entry.setPublished(createTodoDate);

			Category category = _factory.newCategory();
			category.setScheme("http://www.ibm.com/xmlns/prod/sn/type");
			category.setTerm("todo");
			category.setText("To Do");

			Category todotag1 = _factory.newCategory();
			Category todotag2 = _factory.newCategory();
			todotag1.setTerm("todotagone");
			todotag2.setTerm("todotagtwo");
			entry.addCategory(todotag1);
			entry.addCategory(todotag2);

			entry.addSimpleExtension(StringConstants.SNX_ASSIGNEDTO,
					imUser.getEmail());

			Date date = new Date();
			long tempTime = createTodoDate.getTime();
			tempTime += (3 * 24 * 60 * 60 * 1000); // add three days
			date.setTime(tempTime);

			entry.addCategory(category);
			Todo simpleTodo = new Todo(entry);
			simpleTodo.setDueDate(date);
			String presetDate = simpleTodo.getDueDateInText();
			Entry createTodoResult = (Entry) service.addNodeToActivity(
					activityColUrl, simpleTodo);
			assertTrue(createTodoResult != null);
			assertEquals(201, service.getRespStatus());

			String todoEditUrl = service.getRespLocation();

			entryEditUrl = createTodoResult.getEditLinkResolvedHref().toURL()
					.toString();
			assertTrue(todoEditUrl.equalsIgnoreCase(entryEditUrl));
			boolean foundtags = false;
			assertEquals(imUser.getRealName(), createTodoResult.getAuthor()
					.getName());
			List<Category> categories = createTodoResult.getCategories();
			for (Category cat : categories) {
				if (cat.getTerm().equalsIgnoreCase("todotagone")
						|| cat.getTerm().equalsIgnoreCase("todotagtwo")) {
					foundtags = true;
				}
			}
			assertTrue(foundtags);

			Element assignedTo = createTodoResult
					.getExtension(StringConstants.SNX_ASSIGNEDTO);
			assertEquals(imUser.getRealName(),
					assignedTo.getAttributeValue("name"));
			Element duedate = createTodoResult
					.getExtension(StringConstants.SNX_DUEDATE);
			assertEquals(presetDate, duedate.getText());
			/* end: create a todo */

			edittodoUrl = createTodoResult.getEditLink().getHref().toURL()
					.toString();
			Entry entry2 = _factory.newEntry();
			entry2.setTitle("Don Quixote Homepage - Todo_updated");
			entry2.setContent("Save Dulcinea from evil giants.");
			// entry2.setPublished(createTodoDate);
			entry2.setUpdated(updateTodoDate);
			entry2.addCategory(category);
			Todo updatedTodo = new Todo(entry2);

			Entry updateTodoResult = (Entry) service.editNodeInActivity(
					edittodoUrl, updatedTodo);
			assertTrue(updateTodoResult != null);
			assertEquals(200, service.getRespStatus());
			assertEquals(createTodoDate.getTime(), updateTodoResult
					.getPublished().getTime());
			assertEquals(updateTodoDate.getTime(), updateTodoResult
					.getUpdated().getTime());
		}
	}

	// @Test
	public void testImpersonatedCreateTodoWithAttachment() throws Exception {
		// Skip this test if running on SC.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {

			ArrayList<FieldElement> fields = new ArrayList<FieldElement>();
			FieldElement el1 = new FieldElement(null, false, "Attachment", 0,
					FieldType.FILE, null, null);
			fields.add(el1);
			ActivityEntry entry = new ActivityEntry("Test Entry",
					"This is a test entry", "tag1 tag2 tag3", 0, true, fields,
					((Activity) activity).toEntry(), false);
			entry.setPublished(new Date(1409759000));

			LCService.getApiLogger().debug(filePath);
			InputStream is = this.getClass().getResourceAsStream(filePath);
			File file2 = new File("holder.txt");
			OutputStream outputStream = new FileOutputStream(file2);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			// Entry result = (Entry)
			// service.addMultipartNodeToActivityImpersonated(activityColUrl,
			// entry, file2);
			Entry result = (Entry) service.addMultipartNodeToActivity(
					activityColUrl, entry, file2);
			assertTrue(result != null);

			LCService.getApiLogger().debug("Entry node verify");
			ArrayList<Entry> nodes = service.getActivityNodes(activityColUrl);
			for (Entry node : nodes) {
				Category type = node.getCategories(StringConstants.SCHEME_TYPE)
						.get(0);
				if (type.getTerm().equals(
						StringConstants.STRING_ENTRY_LOWERCASE)) {
					ActivityEntry activityEntry = new ActivityEntry(node);
					assertTrue(activityEntry.getTitle() != null);
				}
			}

			file2.deleteOnExit();
		}
	}

	/**
	 * Test case for batch API for following multiple activities
	 */
	@Test
	public void testFollowMultipleActivities() {
		try {
			Entry actEntry = _factory.newEntry();

			actEntry.setTitle("activity 1");
			actEntry.setContent("Save Dulcinea from evil giants");
			actEntry.setPublished(createdDate);

			Category tag1 = _factory.newCategory();
			Category tag2 = _factory.newCategory();
			tag1.setTerm("tagone");
			tag2.setTerm("tagtwo");
			actEntry.addCategory(tag1);
			actEntry.addCategory(tag2);

			// add an activity for test
			Activity simpleActivity = new Activity(actEntry);

			Entry activityResult = (Entry) service
					.createActivity(simpleActivity);
			assertTrue(activityResult != null);
			assertEquals(201, service.getRespStatus());

			String id = activityResult.getId().toString();
			String uuid1 = id.substring(id.indexOf("oa:") + 3);

			actEntry = _factory.newEntry();

			actEntry.setTitle("activity 2");
			actEntry.setContent("Save Dulcinea from evil giants");
			actEntry.setPublished(createdDate);

			tag1 = _factory.newCategory();
			tag2 = _factory.newCategory();
			tag1.setTerm("tagone");
			tag2.setTerm("tagtwo");
			actEntry.addCategory(tag1);
			actEntry.addCategory(tag2);

			// add an activity for test
			Activity simpleActivity2 = new Activity(actEntry);

			Entry activityResult2 = (Entry) service
					.createActivity(simpleActivity2);
			assertTrue(activityResult2 != null);
			assertEquals(201, service.getRespStatus());

			id = activityResult2.getId().toString();
			String uuid2 = id.substring(id.indexOf("oa:") + 3);

			Feed feed = Abdera.getNewFactory().newFeed();

			feed.setAttributeValue("xmlns:thr",
					"http://purl.org/syndication/thread/1.0");

			String[] activityUuids = { uuid1, uuid2 };

			for (int i = 0; i < activityUuids.length; i++) {
				Entry entry = Abdera.getNewFactory().newEntry();
				entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source",
						"activities", null);
				entry.addCategory(
						"http://www.ibm.com/xmlns/prod/sn/resource-type",
						"activity", null);
				entry.addCategory(
						"http://www.ibm.com/xmlns/prod/sn/resource-id",
						activityUuids[i], null);
				feed.addEntry(entry);
			}
			String serverUrl = service.getActivityDashboardURLs().get(
					StringConstants.ACTIVITIES_OVERVIEW);
			serverUrl = serverUrl.substring(0,
					serverUrl.indexOf("/activities/"));
			ExtensibleElement response = service.postFeed(serverUrl
					+ "/activities/follow/atom/resources", feed);
			assertTrue(service.getRespStatus() == HttpServletResponse.SC_OK);

			ProfileData profileData = ProfileLoader
					.getProfile(StringConstants.ADMIN_USER);
			// [IC149109] This logical has problem against SmartCloud Env,
			// and need more analysis for SmartCloud Env.
			String userCredential = "";
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			    userCredential = profileData.getEmail();
			} else {
				userCredential = profileData.getUserName();
			}
			service.deleteFeedWithBody(serverUrl
					+ "/activities/follow/atom/resources", feed.toString(),
					userCredential + ":" + profileData.getPassword());
		} catch (Exception e) {
			fail(e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
		assertTrue(service.getRespStatus() == HttpServletResponse.SC_OK);
	}

	/**
	 * Test case for batch API for following for multiple persons for one
	 * resource
	 * 
	 * @throws IOException
	 */
	@Test
	public void testFollowActivityByMultipleUsers() {
		try {
			Entry actEntry = _factory.newEntry();

			actEntry.setTitle("activity 1");
			actEntry.setContent("Save Dulcinea from evil giants");
			actEntry.setPublished(createdDate);

			Category tag1 = _factory.newCategory();
			Category tag2 = _factory.newCategory();
			tag1.setTerm("tagone");
			tag2.setTerm("tagtwo");
			actEntry.addCategory(tag1);
			actEntry.addCategory(tag2);

			// add an activity for test
			Activity simpleActivity = new Activity(actEntry);

			Entry activityResult = (Entry) service
					.createActivity(simpleActivity);
			assertTrue(activityResult != null);
			assertEquals(201, service.getRespStatus());

			String id = activityResult.getId().toString();
			String uuid1 = id.substring(id.indexOf("oa:") + 3);

			Feed feed = Abdera.getNewFactory().newFeed();

			feed.setAttributeValue("xmlns:thr",
					"http://purl.org/syndication/thread/1.0");
			UsersEnvironment userEnv = new UsersEnvironment();
			UserPerspective user1 = userEnv.getLoginUserEnvironment(
					StringConstants.RANDOM1_USER + 1,
					Component.ACTIVITIES.toString());

			String[] personIds = { user1.getUserId(),
					impersonateByotherUser.getUserId() };

			for (int i = 0; i < personIds.length; i++) {
				Entry entry = Abdera.getNewFactory().newEntry();
				entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source",
						"profiles", null);
				entry.addCategory(
						"http://www.ibm.com/xmlns/prod/sn/resource-type",
						"profile", null);
				entry.addCategory(
						"http://www.ibm.com/xmlns/prod/sn/resource-id",
						personIds[i], null);
				entry.setPublished(new Date());
				feed.addEntry(entry);
			}
			String serverUrl = service.getActivityDashboardURLs().get(
					StringConstants.ACTIVITIES_OVERVIEW);
			serverUrl = serverUrl.substring(0,
					serverUrl.indexOf("/activities/"));
			ExtensibleElement response = service
					.postFeed(
							serverUrl
									+ "/activities/follow/atom/resources?type=activity&source=activities&resource="
									+ uuid1, feed);
			assertTrue(service.getRespStatus() == HttpServletResponse.SC_OK);

			ProfileData profileData = ProfileLoader
					.getProfile(StringConstants.ADMIN_USER);
			// [IC149109] This logical has problem against SmartCloud Env,
			// and need more analysis for SmartCloud Env.
			String userCredential = "";
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			    userCredential = profileData.getEmail();
			} else {
				userCredential = profileData.getUserName();
			}
			service.deleteFeedWithBody(
					serverUrl
							+ "/activities/follow/atom/resources?type=activity&source=activities&resource="
							+ uuid1, feed.toString(), userCredential
							+ ":" + profileData.getPassword());

		} catch (Exception e) {
			fail(e.getMessage());
			LOGGER.error(e.getMessage(), e);
		}
		assertTrue(service.getRespStatus() == HttpServletResponse.SC_OK);
	}

	// //////////////////////////////////////////// END TESTS WRITTEN BY
	// ACTIVITIES TEAM
	// //////////////////////////////////////////////////////
	
	@Test
	public void getDeletedActivities() {
		super.getDeletedActivities();
	}
	
	@Test
	public void postTodoComment() {
		super.postTodoComment();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		otherUserService.tearDown();
		assignedService.tearDown();
	}
}