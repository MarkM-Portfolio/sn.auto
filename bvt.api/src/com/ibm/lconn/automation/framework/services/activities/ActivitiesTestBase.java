package com.ibm.lconn.automation.framework.services.activities;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Chat;
import com.ibm.lconn.automation.framework.services.activities.nodes.Email;
import com.ibm.lconn.automation.framework.services.activities.nodes.FieldElement;
import com.ibm.lconn.automation.framework.services.activities.nodes.RelatedActivity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Reply;
import com.ibm.lconn.automation.framework.services.activities.nodes.Section;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

abstract public class ActivitiesTestBase {

	static Abdera abdera = new Abdera();

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ActivitiesTestBase.class.getName());

	protected static UserPerspective user, imUser, otherUser, visitor, extendedEmployee,
			 assignedUser;

	// For impersonation test: service belong to admin, realUserService belong
	// to impersonated user
	// Otherwise service and realUserService is same
	protected static ActivitiesService service, visitorService, extendedEmpService,
			realUserService, impersonatedService, assignedService;

	static boolean useSSL = true;

	@Test
	public void getMyActivitiesTest() {
		LOGGER.debug("Getting user activities:");
		ArrayList<Activity> activities = assignedService.getMyActivities();

		if (activities.size() > 0) {
			for (Activity activity : activities) {
				assert (activity.getTitle() != null);
				LOGGER.debug(activity.getTitle());
			}
		} else {
			LOGGER.warn("No activities found.");
		}
		LOGGER.debug("Finished getting user activities.");
	}

	@Test
	public void createActivityWithEntries() {
		String title = "DOGSTAR Activity_" + StringGenerator.randomSentence(3);
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
				// ActivitiesService.API_LOGGER.debug("Creating Reply in the Activity");
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
			LOGGER.debug("Section: " + sectionResult.getTitle()
					+ " was created successfully.");
			// ActivitiesService.API_LOGGER.debug("Creating Section in the Activity");

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

			ActivityEntry activityEntry = new ActivityEntry(entryTitle,
					entryContent, "tag1 tag2 tag3", i * 1000, true, null,
					sectionResult, false);
			Entry entryResult = (Entry) service.addNodeToActivity(
					activityNodeCollection.getHref().toString(), activityEntry);
			assertTrue(entryResult != null);
			LOGGER.debug("\tEntry: " + entryResult.getTitle()
					+ " was created successfully.");
			// ActivitiesService.API_LOGGER.debug("Creating Entry in the Activity");

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

		// Member newMember1;
		// if(!config.isEmailHidden()) {
		// newMember1 = new Member("ajones3@janet.iris.com", null,
		// Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		// } else {
		// newMember1 = new Member(null, "4fda6cc0-0101-102e-88de-f78755f7e0ed",
		// Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		// }
		// service.addMemberToActivity(activityResult.getLink(StringConstants.REL_MEMBERS).getHref().toString(),
		// newMember1);

		// ActivitiesService.writer.println("Post: " +
		// activityNodeCollection.getHref().toString() +
		// " Reply/Section/To-do Entry");
	}

	// @Test
	public void createActivityTemplateWithEntries() {
		String title = "Activity_" + StringGenerator.randomLorem1Sentence();
		String content = "Content_" + StringGenerator.randomStringWithSC(4);
		String tags = "apitest " + StringGenerator.randomLorem1Sentence();

		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				RandomUtils.nextBoolean(new Random()), false);
		simpleActivity.setIsTemplate(true);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("create activity", 201, service.getRespStatus());
		assertEquals("impersonate userName check failed ",
				imUser.getRealName(), activityResult.getAuthor().getName());
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);

		LOGGER.debug("createActivityFromTemplate");
		Activity template = new Activity(assignedService.getAllTemplates().get(
				0));
		template.setIsTemplate(false);
		template.setTitle("I've changed the title!"
				+ StringGenerator.randomStringWithSC(4));
		service.createActivity(template);

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
	public void entryTest() {
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String actName = "TestEntry" + timeStamp;
		Activity simpleActivity = new Activity(actName, "content", "tags",
				null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		Activity activity = new Activity(activityResult);
		// Activity activity = service.getMyActivities().get(0);

		ArrayList<FieldElement> fields = new ArrayList<FieldElement>();

		// Create Date Field Element
		FieldElement d1 = new FieldElement(null, false, "Date Field", 0,
				FieldType.DATE, null, Utils.dateFormatter.format(new Date()));
		FieldElement d2 = new FieldElement(null, false, "Date Field", 0,
				FieldType.DATE, null, null);
		d2.setDateInfo(new Date());

		// Create Person Field Element
		Element name = abdera.getFactory().newName();
		name.setText("Amy Jones125");
		Element userid = abdera.getFactory().newElement(
				StringConstants.SNX_USERID);
		userid.setText("2bcb73ed-785511de-8074e703-179b6183");
		Element userstate = abdera.getFactory().newElement(
				StringConstants.SNX_USER_STATE);
		userstate.setText("active");

		FieldElement p1 = new FieldElement(null, false, "Person", 0,
				FieldType.PERSON, new Element[] { name, userid, userstate },
				null);
		FieldElement p2 = new FieldElement(null, false, "Person", 0,
				FieldType.PERSON, null, null);
		p2.setPersonInfo("Amy Jones125", "2bcb73ed-785511de-8074e703-179b6183",
				"active");

		FieldElement p3 = new FieldElement(null, false, "Person", 0,
				FieldType.PERSON, null, null);
		p3.setPersonInfo(activity.getAuthors().get(0));

		Element summary = abdera.getFactory().newSummary();
		summary.setText("Hello World!");

		FieldElement s1 = new FieldElement(null, false, "Text", 0,
				FieldType.TEXT, new Element[] { summary }, null);

		FieldElement l1 = new FieldElement(null, false, "Link", 0,
				FieldType.LINK, null, null);
		l1.setLinkToFileInfo("http://google.com", "File.jpg");

		FieldElement l2 = new FieldElement(null, false, "Link", 0,
				FieldType.LINK, null, null);
		l2.setLinkToFolderInfo("http://google.com", "Folder Name");

		FieldElement l3 = new FieldElement(null, false, "Link", 0,
				FieldType.LINK, null, null);
		l3.setLink("http://google.com", "Google");

		// FieldElement el4 = new FieldElement(null, false, "File Field", 0,
		// FieldType.FILE, "");
		// FieldElement el5 = new FieldElement(null, false, "Link Field", 0,
		// FieldType.LINK, "");

		fields.add(d1);
		fields.add(d2);

		fields.add(p1);
		fields.add(p2);
		fields.add(p3);

		fields.add(s1);

		fields.add(l1);
		fields.add(l2);
		fields.add(l3);

		ActivityEntry entry = new ActivityEntry("Test Entry",
				"This is a test entry", "tag1 tag2 tag3", 0, true, fields,
				activityResult, false);

		Entry result = (Entry) service.addNodeToActivity(activity
				.getAppCollection().getHref().toString(), entry);
		assert (result != null);

		// ActivitiesService.API_LOGGER.debug("Creating Entry in the Activity");

	}

	@Test
	public void fileEntryTest() throws URISyntaxException, IOException {

		String timeStamp = Utils.logDateFormatter.format(new Date());
		String actName = "TestFileEntry" + timeStamp;
		Activity simpleActivity = new Activity(actName, "", "tags", null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		Activity activity = new Activity(activityResult);
		// Activity activity = service.getMyActivities().get(0);

		ArrayList<FieldElement> fields = new ArrayList<FieldElement>();

		FieldElement el1 = new FieldElement(null, false, "Attachment", 0,
				FieldType.FILE, null, null);
		fields.add(el1);

		ActivityEntry entry = new ActivityEntry("Test Entry",
				"This is a test entry", "tag1 tag2 tag3", 0, true, fields,
				activityResult, false);

		File file = File.createTempFile("lamborghini_murcielago_lp640", ".jpg");
		file.deleteOnExit();
		InputStream is = this.getClass().getResourceAsStream(
				"/resources/lamborghini_murcielago_lp640.jpg");
		OutputStream os = new FileOutputStream(file);
		IOUtils.copy(is, os);
		os.close();
		service.addMultipartNodeToActivity(
				activity.getAppCollection().getHref().toString(),
				entry, file);
		LCService.getApiLogger().debug("POST:" + activity.getAppCollection().getHref().toString());
		if (service.getRespStatus() != 201) {
			assertEquals("File entry is updated to activitynode"+service.getDetail(), 200, service.getRespStatus());
		} else {
			assertEquals("File entry is updated to activitynode"+service.getDetail(), 201, service.getRespStatus());
		}

		LCService.getApiLogger().debug("Entry node verify");
		ArrayList<Entry> nodes = assignedService.getActivityNodes(activity
				.getAppCollection().getHref().toString());
		for (Entry node : nodes) {
			Category type = node.getCategories(StringConstants.SCHEME_TYPE)
					.get(0);
			if (type.getTerm().equals(StringConstants.STRING_ENTRY_LOWERCASE)) {
				ActivityEntry activityEntry = new ActivityEntry(node);
				assert (activityEntry.getTitle() != null);
			}
		}

		LCService.getApiLogger().debug(
				"Creating RelatedActivity in the Activity");
		RelatedActivity relAct = new RelatedActivity("Related Activity",
				"some related activity content", "related tag awesome", false,
				activityResult);
		service.addNodeToActivity(activity.getAppCollection().getHref()
				.toString(), relAct);

	}

	@Test
	public void chatEntryTest() {
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String actName = "TestChatEntry" + timeStamp;
		Activity simpleActivity = new Activity(actName, "Content", "tags",
				null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		Activity activity = new Activity(activityResult);

		Chat chatNode = new Chat(
				"Chat Test 1",
				"<html><head><style type=\"text/css\">h1 {color:red;} h2 {color:blue;} p {color:green;}</style></head><body><h1>All header 1 elements will be red</h1><h2>All header 2 elements will be blue</h2><p>All text in paragraphs will be green.</p></body></html>",
				"chat tag test", true, activityResult);
		System.out.println(chatNode.toString());
		Entry result = (Entry) service.addNodeToActivity(activity
				.getAppCollection().getHref().toString(), chatNode);
		Chat newNode = new Chat(result);
		assert (newNode.getTitle() != null);

		// ActivitiesService.API_LOGGER.debug("Creating chat in the Activity");
	}

	@Test
	public void emailEntryTest() {
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String actName = "TestEmailEntry" + timeStamp;
		Activity simpleActivity = new Activity(actName, "", "tags", null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		Activity activity = new Activity(activityResult);

		Email emailNode = new Email(
				"Email Test 1",
				"<html><head><style type=\"text/css\">h1 {color:red;} h2 {color:blue;} p {color:green;}</style></head><body><h1>All header 1 elements will be red</h1><h2>All header 2 elements will be blue</h2><p>All text in paragraphs will be green.</p></body></html>",
				"chat tag test", true, activityResult);
		// System.out.println(emailNode.toString());
		Entry result = (Entry) service.addNodeToActivity(activity
				.getAppCollection().getHref().toString(), emailNode);
		Email newNode = new Email(result);
		assert (newNode.getTitle() != null);
		// ActivitiesService.API_LOGGER.debug("Creating email in the Activity");
	}

	// @Test //TODO test this case again
	// public void createTemplateFromActivity() {
	// Activity activity = service.getMyActivities().get(0);
	// activity.setIsTemplate(true);
	// activity.setId(null);
	// activity.setAppCollection(null);
	// activity.setLinks(new HashMap<String, Link>());
	// activity.setContent("");
	// //activity.setLinks(new HashMap<String, Link>());
	//
	// Entry result = (Entry) service.createActivity(activity);
	// System.out.println(result);
	//
	// }

	// @Test
	// public void createEntryTemplate() throws URISyntaxException {
	// Activity activity = service.getMyActivities().get(0);
	// ActivityEntry entry = new ActivityEntry("Test Entry",
	// "This is a test entry", "tag1 tag2 tag3", 0, true, null,
	// activity.toEntry(),
	// true);
	// entry.setId(activity.getId());
	// Entry result = (Entry)
	// service.addNodeToActivity(activity.getAppCollection().getHref().toString(),
	// entry);
	//
	// //ExtensibleElement result =
	// activitiesService.createEntryTemplate(activity, entry);
	// //ExtensibleElement result2 = service.createEntryTemplate(result, result,
	// new
	// File(this.getClass().getResource("/resources/Forwarded16.png").toURI()));
	// ExtensibleElement result2 = service.createEntryTemplate(result, result,
	// new
	// File(this.getClass().getResource("/resources/Forwarded16.png").getFile()));
	// System.out.println(result2);
	//
	// }

	@Test
	public void createToDoComment() {
		LOGGER.debug("Begining Test: Add comment to ToDo entry");

		// clear out the old test
		service.deleteTests();

		String title = "General Survival";
		String content = "Bad Situations";
		String tags = "tagActivity_"
				+ Utils.logDateFormatter.format(new Date());
		boolean addComment = false;
		String todoTitle = "Survive Zombies";
		String todoContent = "Run";
		String activityId;

		// create activity to use for testing
		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		// create a todo entry to use for testings
		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);
		Todo todoEntry = new Todo(todoTitle, todoContent,
				"zombies brains movement", 5000, false, false, activityResult,
				null, null);
		Entry todoResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), todoEntry);
		assertTrue(todoResult != null);
		LOGGER.debug("\t\tTodo: " + todoResult.getTitle()
				+ " was created successfully.");

		// atempt to add a comment to the todo entry
		activityId = todoEntry.getActivityId();
		Reply comment = new Reply("Tips", "Dont forget to eat", 6000, false,
				todoResult);
		service.addNodeToActivity(activityNodeCollection.getHref().toString(),
				comment);

		// check if the comment was added successfully
		Feed todoComments = (Feed) assignedService
				.getToDoCommentFeed(activityId);
		for (Entry e : todoComments.getEntries()) {
			if (e.getContent().trim().equals("Dont forget to eat")) {
				addComment = true;
			}
		}

		if (addComment) {
			LOGGER.debug("Test Successful: Comment to ToDo entry was found");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Comment to ToDo entry was not found");
			assertTrue(false);
		}
	}

	// @Test
	public void updateActivityPriority() {
		LOGGER.debug("BEGINNING TEST: Update Activity Priority");
		service.deleteTests();
		String newPriority = "3000";

		// Create an activity
		Activity simpleActivity = new Activity("Update Activity Priority Test",
				"test", "activitiessearchtag", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);

		String editLink = activityResult.getEditLink().getHref().toString();

		// set the new priority
		Category defaultPriority = activityResult
				.getCategories("http://www.ibm.com/xmlns/prod/sn/priority")
				.get(0).setTerm(newPriority);

		service.updateActivity(editLink, activityResult);

		// validate
		Entry entryToValidate = (Entry) assignedService.getActivity(editLink);
		String updatedPriority = entryToValidate
				.getCategories("http://www.ibm.com/xmlns/prod/sn/priority")
				.get(0).getTerm();
		if (updatedPriority.equals(newPriority)) {
			LOGGER.debug("SUCCESS: Priority was correctly updated");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Priority was not updated correctly");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Update Activity Priority");
	}

	@Test
	public void updateActivity() {
		LOGGER.debug("BEGINNING TEST: Update Activity");
		assignedService.deleteTests();

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

		// validate
		Entry entryToValidate = (Entry) assignedService
				.getActivity(activityResult.getEditLink().getHref().toString());
		assertEquals("get activity", 200, assignedService.getRespStatus());
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

	// @Test
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
		assertTrue(service.deleteActivity(result.getEditLink().getHref()
				.toString()));

		LOGGER.debug("Step 3: Restore the activity");
		String activityIdElement = result.getId().toString();
		String activityUuid = activityIdElement.substring(activityIdElement
				.indexOf("oa:") + 3);
		ExtensibleElement eEle = service.restoreActivity(activityUuid);
		assertEquals("update activity", 204, service.getRespStatus());
		// assertEquals("impersonate userName check failed ",
		// imUser.getRealName(), ((Entry)eEle).getAuthor().getName());

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
	public void getAPIVersion() {
		LOGGER.debug("Beginning Test: Find Version of the Activities API");

		// retrieve the header information from the server
		Entry apiVersion = (Entry) service.getAPIVersion();

		String version = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			version = apiVersion
					.getExtension(StringConstants.HEADER_VERSION_SC).getText();
		} else {
			version = apiVersion.getExtension(StringConstants.HEADER_VERSION)
					.getText();
		}

		if (version != null) {
			LOGGER.debug("Test Successful: Found the Version number of the Activities API : "
					+ version);
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: No Version number was found from the Activities API");
			assertTrue(false);
		}
	}

	/**
	 * TB 10/1/13. Test fails with HTTP 403 (forbidden) on SmartCloud, works on
	 * prem. OCS defect 119062 updated
	 * 
	 * Tests the overview feed of a user
	 * 
	 * @return AssertionError if test fails
	 * @see Endpoint: /activities/service/atom/activities/overview
	 */
	// @Test
	public void getActivityOverview() {
		/**
		 * Step 1: Create an Activity Step 2: Post the Activity Step 3: Verify
		 * that the Activity appears in the feed
		 */

		LOGGER.debug("Beginning Test");

		LOGGER.debug("Step 1: Create an Activity");
		Activity postMe = new Activity("Mr. Overview_"
				+ RandomStringUtils.randomAlphanumeric(5),
				"Global-Takeover-Mission", "Launch", null, false, false);

		LOGGER.debug("Step 2: Post the Activity");
		ExtensibleElement response = service.createActivity(postMe);

		LOGGER.debug("Step 3: Verify that the Activity appears in the feed");
		String feedURL = service.getServiceURLString()
				+ URLConstants.ACTIVITIES_ATOM_MY + "/overview";
		Feed overviewFeed = (Feed) service.getFeed(feedURL);
		List entries = overviewFeed.getEntries();
		boolean found = false;
		for (int i = 0; i < entries.size(); i++) {
			if (((Entry) entries.get(i)).getTitle().equals(
					((Entry) response).getTitle())) {
				assertTrue(true);
				return;
			}
		}
		assertTrue(found);
	}

	@Test
	// Test needs an email to work otherwise the test will not work
	public void getActivitiesMemberInfo() {
		LOGGER.debug("Beginning Test: Find the Member Document of an user");

		Entry memberInfo = (Entry) assignedService.getActivitiesMemberInfo();

		if (memberInfo.getTitle().equals(StringConstants.USER_REALNAME)) {
			LOGGER.debug("Test Successful: Found the Member Document of "
					+ StringConstants.USER_REALNAME);
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: No Member Document was found");
			assertTrue(false);
		}
	}

	@Test
	public void getActivityDocument() {
		LOGGER.debug("Beginning Test: Find the Activity Document for an activity");
		service.deleteTests();

		Activity simpleActivity = new Activity("General Survival", "",
				"tornados earthquakes blizzards", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		String activityId = activityResult
				.getId()
				.toString()
				.substring(
						activityResult.getId().toString().lastIndexOf(":") + 1);
		Entry activityDocument = (Entry) assignedService
				.getActivityDocument(activityId);

		if (activityResult.getId().toString()
				.equals(activityDocument.getId().toString())) {
			LOGGER.debug("Test Successful: Found the document for an activity");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Not able to find the document for an activity");
			assertTrue(false);
		}
	}

	@Test
	public void getActivityEntry() {
		LOGGER.debug("Begining Test: Add comment to ToDo entry");

		// clear out the old test
		service.deleteTests();

		String title = "General Survival";
		String content = "Bad Situations";
		String tags = "tagActivity_"
				+ Utils.logDateFormatter.format(new Date());
		boolean addComment = false;
		String todoTitle = "Survive Zombies";
		String todoContent = "Run";
		String activityId;

		// create activity to use for testing
		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		// create a todo entry to use for testings
		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);
		Todo todoEntry = new Todo(todoTitle, todoContent,
				"zombies brains movement", 5000, false, false, activityResult,
				null, null);
		service.addNodeToActivity(activityNodeCollection.getHref().toString(),
				todoEntry);

		activityId = todoEntry.getActivityId();

		// check if the comment was added successfully
		Feed todoComments = (Feed) assignedService
				.getToDoCommentFeed(activityId);
		for (Entry e : todoComments.getEntries()) {
			if (e.getTitle().trim().equals(todoTitle)) {
				addComment = true;
			}
		}

		if (addComment) {
			LOGGER.debug("Test Successful: Entry was found");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Entry was not found");
			assertTrue(false);
		}
	}

	@Test
	public void getTags() {
		/*
		 * Get a list of tags assigned to all activities Test Process: 1. Create
		 * an activity with tags 2. Get all activity Tags 3. Parse through tags
		 * to verify that the added ones are there
		 */
		LOGGER.debug("Beginning Test: Get Tags");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		LOGGER.debug("Step 1: Create an activity with tags");
		String tagsString = "puppies_" + uniqueNameAddition + " lemons_"
				+ uniqueNameAddition + " landscape_" + uniqueNameAddition;
		Activity testActivity = new Activity("Test Activity", "Test content",
				tagsString, null, false, false);
		service.createActivity(testActivity);

		LOGGER.debug("Step 2: Get all activity Tags");
		ArrayList<Category> tags = assignedService.getAllActivityTags();
		// Create an array list of only the tag names
		ArrayList<String> tagNames = new ArrayList<String>();
		for (Category cat : tags) {
			tagNames.add(cat.getTerm());
		}

		LOGGER.debug("Step 3: Verify that added tags exist");
		for (String tag : tagsString.split(" ")) {
			assertEquals(true, tagNames.contains(tag.toLowerCase()));
		}

		LOGGER.debug("Ending Test: Get Tags");
	}

	@Test
	public void getDeletedActivities() {
		/*
		 * Tests the ability to get activities in trash Step 1: Create an
		 * activity Step 2: Delete the activity Step 3: Get deleted activities
		 * Step 4: Verify the deleted activity is there
		 */
		LOGGER.debug("Beginning Test: Get deleted activities");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create an activity");
		Activity testActivity = new Activity("DeletedActivity" + dateCode,
				"I'm so sad because I've been deleted.", "trashed", null,
				false, false);
		Entry result = (Entry) assignedService.createActivity(testActivity);

		LOGGER.debug("Step 2: Delete the activity");
		assignedService.deleteActivity(result.getEditLink().getHref()
				.toString());
		
		// TJB 7/30/15 need to use 'search' param to find deleted activity
		String trashUrl = URLConstants.SERVER_URL + "/activities/service/atom2/trash?search=" + "DeletedActivity" + dateCode;
		
		LOGGER.debug("Step 3: Get deleted activities");
		Feed trashedFeed = (Feed) assignedService.getFeed(trashUrl);

		LOGGER.debug("Step 4: Verify the deleted Activity is there");
		boolean foundActivity = false;
		for (Entry activityEntry : trashedFeed.getEntries()) {
			if (activityEntry.getTitle().equals(testActivity.getTitle()))
				foundActivity = true;
		}
		assertTrue(foundActivity);
	}

	// @Test
	public void verifyTotalResultMyActivities() {
		LOGGER.debug("BEGINNING TEST: Verify Total Results for Activities");
		assignedService.deleteTests();

		Activity simpleActivity = new Activity("General Survival", "",
				"tornados earthquakes blizzards", null, false, false);
		service.createActivity(simpleActivity);

		Activity simpleActivity2 = new Activity("Brogramming", "",
				"deadlines UI translation", null, false, false);
		service.createActivity(simpleActivity2);

		int totalResults = -1; // assume failure

		Feed searchResults = (Feed) assignedService.getMyActivitiesFeed();
		if (searchResults != null) {
			Element e = searchResults
					.getFirstChild(StringConstants.OPENSEARCH_TOTALRESULTS);
			if (e != null) {
				try {
					totalResults = Integer.parseInt(e.getText());
				} catch (NumberFormatException nfe) {
					totalResults = -1;
				}
			}
		}

		if (totalResults >= 2) {
			LOGGER.debug("SUCCESS: Verify Total Results for Activities");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Verify Total Results for Activities"
					+ searchResults.toString());
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Verify Total Results for Activities");
	}

	public static Entry createActivity(String title, String content,
			String tags, Date date, Boolean complete, Boolean community) {
		// TODO add members to activity
		Activity simpleActivity = new Activity(title, content, tags, date,
				complete, community);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);

		return activityResult;

	}

	@Test
	public void activitiesCSRF() throws FileNotFoundException, IOException {
		// RTC 78808
		LOGGER.debug("BEGINNING TEST: Activities RTC 78808 - Connections core API CSRF fix.");
		// 1. Create activity
		// 2. POST with Origin header
		// 3. Validate 403 return.

		assignedService.deleteTests();

		String title = "API Datapop Activities RTC 78808 - Connections core API CSRF fix.";
		String content = "Cross scripting fix. Validate 403 return when using Origin header";
		String tags = "tagActivity_"
				+ Utils.logDateFormatter.format(new Date());

		// create activity to use for testing
		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		// update priority - this will use POST
		String activityId = activityResult
				.getId()
				.toString()
				.substring(
						activityResult.getId().toString().lastIndexOf(":") + 1);

		String entryTitle = StringGenerator.randomLorem2Sentence();
		String entryContent = StringUtils.join(ArrayUtils.addAll(
				StringConstants.LOREM_1, StringConstants.LOREM_2));
		ActivityEntry activityEntry = new ActivityEntry(entryTitle,
				entryContent, "tag1 tag2 tag3", 3000, true, null,
				activityResult, false);

		Entry test = activityEntry.toEntry();
		int returnCode = service.updateActivityPriorityCRX(activityId, test);

		// validate
		if (returnCode == 403) {
			LOGGER.debug("Correct response returned: " + returnCode
					+ ".  This test should fail.");
			assertTrue(true);
		} else {
			LOGGER.debug("Incorrect response returned: " + returnCode
					+ ".  Instead the correct response is HTTP 403");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Activities RTC 78808 - Connections core API CSRF fix.");
	}

	@Test
	public void activitiesUsers() throws FileNotFoundException, IOException {

		LOGGER.debug("BEGINNING TEST: Activities multi users test.");
		// 1. Create an activity as current user
		// 2. Try to add a "todo" as user 'b', but user 'b' is not a member of
		// the activity
		// 3. validate that the error code is 403, not 500
		LOGGER.debug("Begining Test: Add comment to ToDo entry");

		// clear out the old test
		assignedService.deleteTests();

		String title = "General Survival";
		String content = "Bad Situations";
		String tags = "tagActivity_"
				+ Utils.logDateFormatter.format(new Date());
		String todoTitle = "Survive Zombies";
		String todoContent = "Run";

		// create activity from current user for testing
		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		// create a TODO entry for testings
		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);
		Todo todoEntry = new Todo(todoTitle, todoContent,
				"zombies brains movement", 5000, false, false, activityResult,
				null, null);

		// other user ( index 10 )
		UserPerspective usr10=null;
		try {
			usr10 = new UserPerspective(10,
					Component.ACTIVITIES.toString(), useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// add TODO from other user
		Entry todoResult = (Entry) usr10.getActivitiesService()
				.addNodeToActivity(activityNodeCollection.getHref().toString(),
						todoEntry);
		String returnCode = todoResult.getFirstChild().getText();
		assertEquals("403", returnCode);
		LOGGER.debug("\t\tTodo: 403: Forbidden. Test successfully.");

		// add TODO from current user
		todoResult = (Entry) service.addNodeToActivity(activityNodeCollection
				.getHref().toString(), todoEntry);
		returnCode = todoResult.getTitle();
		assertEquals("Survive Zombies", returnCode);
		LOGGER.debug("\t\tTodo: 201: Created. Test successfully.");

	}

	// Defect #40889
	@Test
	public void getActivityTagsLink() {
		LOGGER.debug("Begining Test: Get Tag cloud link");

		// activities/service/atom2/activities <--> atom2/tags
		// activities/service/atom2/completed <--> atom2/tags?completed=only
		// activities/service/atom2/activities?priority=High <-->
		// atom2/tags?priority=high
		// activities/service/atom2/activities?priority=Medium <-->
		// atom2/tags?priority=medium
		ArrayList<String> tag_urls = assignedService
				.getTagCloudUrls(StringConstants.REL_TAG_CLOUD);

		assertTrue(tag_urls.get(0).contains("atom2/tags"));
		assertTrue(tag_urls.get(1).contains("atom2/tags?completed=only"));
		assertTrue(tag_urls.get(2).contains("atom2/tags?priority=High"));
		assertTrue(tag_urls.get(3).contains("atom2/tags?priority=Medium"));

		LOGGER.debug("Finished Get Tag-cloud link.");

	}

	// Defect #90229
	@Test
	public void getActivityTodoTagsLink() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Begining Test: Get Tag cloud link");
		ProfileData user = ProfileLoader.getProfile(2);
		String userID = user.getUserId();

		// activities/service/atom2/forms/todos?completedTodos=only <-->
		// completed=only
		// activities/service/atom2/forms/todos?completedTodos=no <-->
		// completed=no ( default )
		// activities/service/atom2/forms/todos?assignedToUserid= <-->
		// assignedToUserid=
		// activities/service/atom2/forms/todos?createdByUserId= <-->
		// createdByUserId=
		ArrayList<String> tag_urls = assignedService.getTodoTagCloudUrls(
				StringConstants.REL_TAG_CLOUD, userID);

		assertTrue(tag_urls.get(0).contains("completed=only"));
		assertTrue(tag_urls.get(1).contains("completed=no"));
		assertTrue(tag_urls.get(2).contains("assignedToUserid=" + userID));
		assertTrue(tag_urls.get(3).contains("createdByUserId=" + userID));

		LOGGER.debug("Finished Get Todo Tag-cloud link.");

	}

	// RTC#79596, 57132
	@Test
	public void postEntrytoDeletedActivity() {
		LOGGER.debug("Delete activities and post entry to it:");

		// create activity - delete - then, add entry to it - return 404
		String title = "For delete" + StringGenerator.randomLorem1Sentence();
		String content = StringUtils.join(StringConstants.LOREM_1);
		String tags = "tagActivity_"
				+ Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Creating Activity:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				RandomUtils.nextBoolean(new Random()), false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		// Collection activityNodeCollection =
		// activityResult.getExtension(StringConstants.APP_COLLECTION);

		String url = activityResult.getEditLinkResolvedHref().toString();

		assignedService.deleteActivity(url);

		String todoTitle = StringGenerator.randomLorem1Sentence();
		String todoContent = StringUtils.join(ArrayUtils.addAll(
				StringConstants.LOREM_1, StringConstants.LOREM_2));

		Todo todoEntry = new Todo(todoTitle, todoContent, "tag1 tag2 tag3",
				5000, RandomUtils.nextBoolean(new Random()),
				RandomUtils.nextBoolean(new Random()), activityResult, null,
				null);
		Entry todoResult = (Entry) service.addNodeToActivity(activityResult
				.getSelfLinkResolvedHref().toString(), todoEntry);
		LOGGER.debug("code : " + todoResult.getFirstChild().getText());

		assertEquals("404", todoResult.getFirstChild().getText());

	}

	/**
	 * Tests the ability to post a comment for a todo
	 * 
	 * @return assertionError if the test fails
	 * @see /activities/service/atom2/activitynode ( +
	 *      "?activityNodeUuid={Uuid}")
	 */
	@Test
	public void postTodoComment() {
		/**
		 * Step 1: Create an activity and get its Uuid Step 2: Create a todo
		 * under the created activity Step 3: Post a comment to the todo Step 4:
		 * Verify that the comment was posted to the todo
		 */

		String randomEntryEnding = RandomStringUtils.randomAlphanumeric(5);
		String commentContent = "Captain, are you ready to transport down with landing party "
				+ randomEntryEnding + "?";

		LOGGER.debug("Beginning Test");
		LOGGER.debug("Step 1: Create an activity and get its Uuid");
		Activity newActivity = new Activity("Captain Kirk_"
				+ RandomStringUtils.randomAlphanumeric(3),
				"Captain of the Enterprise", "Epic", null, false, false);
		Entry newActivityResponse = (Entry) service.createActivity(newActivity);
		LOGGER.debug("Step 1 Complete");

		LOGGER.debug("Step 2: Create a todo under the created activity");
		Collection activityNodeCollection = newActivityResponse
				.getExtension(StringConstants.APP_COLLECTION);
		Todo todo = new Todo("Go down with Spock in the landing party "
				+ randomEntryEnding, "A difficult and dangerous mission",
				"difficult kirk captain enterprise", 1, false, false,
				newActivityResponse, imUser.getUserName(),
				imUser.getUserId());
		Entry newTodoResponse = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), todo);
		LOGGER.debug("Step 2 Complete");

		LOGGER.debug("Step 3: Post a comment to the todo");
		Reply commentReply = new Reply("Mr. Spock's response", commentContent,
				0, false, newTodoResponse);
		String url = activityNodeCollection.getHref().toString();
		service.addNodeToActivity(url, commentReply);
		LOGGER.debug("Step 3: COMPLETE");

		LOGGER.debug("Step 4: Verify that the comment was posted to the todo");
		Feed commentFeed = (Feed) assignedService.getToDoCommentFeed(todo
				.getActivityId());
		assertTrue(commentFeed.getEntries().get(0).getContent().trim()
				.equals(commentContent));
		assertTrue(commentFeed.getEntries().get(0).getTitle()
				.equals("Mr. Spock's response"));
		LOGGER.debug("Step 4 Complete");

		LOGGER.debug("Ending Test");

	}

	// RTC# 89310
	@Test
	public void getActivityMembers() throws FileNotFoundException, IOException,
			URISyntaxException {

		// add an activity for test
		Activity simpleActivity = new Activity("TestMembers", "",
				"tornados earthquakes blizzards", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		assertEquals(201, service.getRespStatus());

		String acl_url = activityResult.getLink(StringConstants.REL_MEMBERS)
				.getHref().toURL().toString();

		for (int i = 0; i <= 12; i++) {
			// Get test user profile
		  if(i!=1 && i!= 4)
		  {
			ProfileData test_user = ProfileLoader.getProfile(i);
			String user_mail = test_user.getEmail();

			Member newMember = new Member(user_mail, null,
					Component.ACTIVITIES, Role.MEMBER, MemberType.PERSON);

			Entry addMemberResult = (Entry) service.addMemberToActivity(
					acl_url, newMember);
			assertTrue(addMemberResult != null);
			assertEquals(201, service.getRespStatus());
		  }
		}
		Feed myMembers = (Feed) assignedService
				.getNotesMemberFromActivity(acl_url);

		// System.out.println(myMembers.getEntries().size());
		assertTrue(myMembers.getEntries().size() > 10);
	}

	@Test
	public void addActivityMembers() {
		LOGGER.debug("Beginning Test: add Activity members");

		// create an activity to test
		Activity simpleActivity = new Activity("TestMembers2", "",
				"tornados earthquakes blizzards", null, false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertEquals("Create Activity", 201, service.getRespStatus());

		// create members for the created activity
		Member newMember1 = new Member(StringConstants.RANDOM1_USER_EMAIL,
				null, Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP);
		/*
		 * userId not working - return 403 * if(!config.isEmailHidden()) {
		 * //newMember1 = new Member("*", null, Component.ACTIVITIES,
		 * Role.MEMBER, MemberType.GROUP); newMember1 = new
		 * Member(StringConstants.RANDOM1_USER_EMAIL, null,
		 * Component.ACTIVITIES, Role.MEMBER, MemberType.GROUP); } else {
		 * newMember1 = new Member(null, "*", Component.ACTIVITIES, Role.MEMBER,
		 * MemberType.GROUP); } assertTrue(activityResult != null);
		 */

		ExtensibleElement addMemberResult = service.addMemberToActivity(
				activityResult.getLink(StringConstants.REL_MEMBERS).getHref()
						.toString(), newMember1);
		assertTrue(addMemberResult != null);
		assertEquals("add menember", 201, service.getRespStatus());
	}

	// RTC 90584
	// TODO @Test
	public void testActivitySectionMove() throws Exception {
		LOGGER.debug("RTC 90584 Activity Section/Entry Move Test:");

		LOGGER.debug("Creating Source Activity:");
		String title = "Source A " + StringGenerator.randomSentence(1);
		String content = "Source C " + StringGenerator.randomSentence(1);
		String tags = "apitest so";
		Activity simpleActivity = new Activity(title, content, tags, null,
				false, false);
		Entry sourceActivityResult = (Entry) service
				.createActivity(simpleActivity);
		assertTrue(sourceActivityResult != null);
		LOGGER.debug("Activity: " + sourceActivityResult.getTitle()
				+ " was created successfully.");

		LOGGER.debug("Creating Target Activity:");
		String title2 = "Target A " + StringGenerator.randomSentence(1);
		String content2 = "Target C " + StringGenerator.randomSentence(1);
		String tags2 = "apitest ta";
		simpleActivity = new Activity(title2, content2, tags2, null, false,
				false);
		Entry targetActivityResult = (Entry) service
				.createActivity(simpleActivity);
		assertTrue(targetActivityResult != null);
		LOGGER.debug("Activity: " + targetActivityResult.getTitle()
				+ " was created successfully.");

		String targetUUID = targetActivityResult.getSelfLinkResolvedHref()
				.toString();
		targetUUID = targetActivityResult.getId().toString();

		Collection activityNodeCollection = sourceActivityResult
				.getExtension(StringConstants.APP_COLLECTION);

		LOGGER.debug("Creating an entry within the Source activity:");
		String entryTitle = "Entry for " + title;
		ActivityEntry activityEntry = new ActivityEntry(entryTitle, "my entry",
				"entry tags", 1000, true, null, sourceActivityResult, false);
		Entry entryResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), activityEntry);
		assertTrue(entryResult != null);
		LOGGER.debug(entryResult.getTitle() + " was created successfully.");
		String entryUUID = entryResult.getId().toString();

		LOGGER.debug("Creating a Section within the Source activity:");
		String sectionTitle = "Section for " + title;
		Section section = new Section(sectionTitle, 1000, sourceActivityResult);
		Entry sectionResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), section);
		assertTrue(sectionResult != null);
		LOGGER.debug(sectionResult.getTitle() + " was created successfully.");
		String sectionUUID = sectionResult.getId().toString();

		targetUUID = targetUUID.substring(20);
		sectionUUID = sectionUUID.substring(20);
		entryUUID = entryUUID.substring(20);

		// move the section by owner from source to target activity
		service.moveToActivity(sectionUUID, targetUUID);

		// move the entry by owner from source to target activity
		service.moveToActivity(entryUUID, targetUUID);
	}

	@Test
	public void templateHiddenFields() {
		/*
		 * This test was originally for RTC 98993 but was abandoned. Am keeping
		 * it in case it's needed for RTC Story 96638.
		 */
		LOGGER.debug("BEGINNING TEST: Test for creating template with entry and custom fields.");

		String title = "Template_Test";
		String content = "Template_Test";
		String tags = "apitest";

		LOGGER.debug("Creating Template:");
		Activity simpleActivity = new Activity(title, content, tags, null,
				false, false);
		simpleActivity.setIsTemplate(true);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);
		assertTrue(activityResult != null);
		LOGGER.debug("Activity: " + activityResult.getTitle()
				+ " was created successfully.");

		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);

		LOGGER.debug("Creating Entry in the Activity:");

		String entryTitle = "TemplateEntry";
		String entryContent = "TemplateContent";

		ActivityEntry activityEntry = new ActivityEntry(entryTitle,
				entryContent, "tag1 tag2 tag3", 0, false, null, activityResult,
				true);
		Entry entryResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), activityEntry);
		assertTrue(entryResult != null);
		LOGGER.debug("\tEntry: " + entryResult.getTitle()
				+ " was created successfully.");

		LOGGER.debug("Creating entry with custom fields");
		FieldElement l1 = new FieldElement(null, false, "Link", 1000,
				FieldType.LINK, null, null);
		l1.setLinkToFileInfo("http://www.google.com", "Google");

		FieldElement l2 = new FieldElement(null, true, "Link", 2000,
				FieldType.LINK, null, null);
		l2.setLinkToFolderInfo("http://www.google.com", "Folder Name Hidden");

		FieldElement l3 = new FieldElement(null, false, "Link", 3000,
				FieldType.LINK, null, null);
		l3.setLink("http://www.google.com", "Google");

		FieldElement l4 = new FieldElement(null, false, "Date Field", 4000,
				FieldType.DATE, null, null);
		l4.setDateInfo(new Date());

		FieldElement l5 = new FieldElement(null, false, "Person", 5000,
				FieldType.PERSON, null, null);
		l5.setPersonInfo("Amy Jones125", "2bcb73ed-785511de-8074e703-179b6183",
				"active");

		ArrayList<FieldElement> fields1 = new ArrayList<FieldElement>();
		fields1.add(l1);
		fields1.add(l2);
		fields1.add(l3);
		fields1.add(l4);
		fields1.add(l5);

		ActivityEntry entry = new ActivityEntry("Entry Created by API",
				"Entry Description By API", "APItag1 APItag2 APItag3", 0, true,
				fields1, activityResult, false);
		Entry result = (Entry) service.addNodeToActivity(activityNodeCollection
				.getHref().toString(), entry);

		String entryId = result.getId().toString();
		String justTheNumber = entryId.substring(entryId.lastIndexOf(":") + 1);
		String url1 = URLConstants.SERVER_URL
				+ "/activities/service/atom2/descendants?nodeUuid="
				+ justTheNumber + "&nodetype=entrytemplate";
		ExtensibleElement fd = assignedService.getFeed(url1);

		LOGGER.debug("ENDING TEST: Test for creating template with entry and custom fields.");
	}

	@Test
	public void testHiddenFields() {
		/*
		 * 1. Create normal activity
		 * 
		 * 2. Create entries with custom fields and add some fields where
		 * "hidden=false" while other fields specify "hidden=true" in
		 * alternating order. For example: position = 1000, hidden =
		 * false" position = 2000, hidden = "
		 * true" position = 3000, hidden = "false" Make sure if any field is set
		 * to "true" that the following field is set to "false"
		 * 
		 * 3. Validate the values of the attribute "hidden". Values should be as
		 * initially set. No values initially set to "false" should now be
		 * "true". For example, the field at position =3000 should have hidden
		 * set to "false", not "true".
		 */

		LOGGER.debug("BEGINNING TEST: RTC 98993 Hidden attribute changes from false to true");
		LOGGER.debug("Creating Activity");
		Activity simpleActivity = new Activity("RTC 98993 Activity",
				"Hidden attribute changes from false to true", null, null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);

		String entryTitle = "Activity Entry";
		String entryContent = "Activity Content";

		Collection activityNodeCollection = activityResult
				.getExtension(StringConstants.APP_COLLECTION);

		LOGGER.debug("Creating entry");
		ActivityEntry activityEntry = new ActivityEntry(entryTitle,
				entryContent, null, 0, false, null, activityResult, true);
		Entry entryResult = (Entry) service.addNodeToActivity(
				activityNodeCollection.getHref().toString(), activityEntry);
		assertTrue(entryResult != null);
		LOGGER.debug("\tEntry: " + entryResult.getTitle()
				+ " was created successfully.");

		LOGGER.debug("Creating custom fields");
		FieldElement l1 = new FieldElement(null, false, "Link", 1000,
				FieldType.LINK, null, null);
		l1.setLinkToFileInfo("http://www.google.com", "Google");

		FieldElement l2 = new FieldElement(null, true, "Link", 2000,
				FieldType.LINK, null, null);
		l2.setLinkToFolderInfo("http://www.google.com", "Folder Name Hidden");

		FieldElement l3 = new FieldElement(null, false, "Link", 3000,
				FieldType.LINK, null, null);
		l3.setLink("http://www.google.com", "Google");

		FieldElement l4 = new FieldElement(null, true, "Date Field", 4000,
				FieldType.DATE, null, null);
		l4.setDateInfo(new Date());

		FieldElement l5 = new FieldElement(null, false, "Person", 5000,
				FieldType.PERSON, null, null);
		l5.setPersonInfo("Amy Jones125", "2bcb73ed-785511de-8074e703-179b6183",
				"active");

		ArrayList<FieldElement> fields1 = new ArrayList<FieldElement>();
		fields1.add(l1);
		fields1.add(l2);
		fields1.add(l3);
		fields1.add(l4);
		fields1.add(l5);

		ActivityEntry entry = new ActivityEntry("Entry Created by API",
				"Entry Description By API", "APItag1 APItag2 APItag3", 0, true,
				fields1, activityResult, false);
		Entry result = (Entry) service.addNodeToActivity(activityNodeCollection
				.getHref().toString(), entry);

		/*
		 * Validate - Unfortunately the "hidden" attribute is not defined in the
		 * fields if the value is "false". "hidden" is only specified if the
		 * value is true. The validation is to ensure that the field after a
		 * field that has "hidden=true" does not have the "hidden" attribute.
		 */

		LOGGER.debug("Validation");
		if (result.getTitle().equalsIgnoreCase("Entry Created by API")) {
			for (Element e : result.getElements()) {
				if (e.toString().startsWith("<snx:field")
						&& e.getAttributeValue("name").equalsIgnoreCase(
								"bookmark")) {
					assertEquals(null, e.getAttributeValue("hidden"));
				}
				if (e.toString().startsWith("<snx:field")
						&& e.getAttributeValue("name").equalsIgnoreCase(
								"person")) {
					assertEquals(null, e.getAttributeValue("hidden"));
				}
			}
		}

		LOGGER.debug("ENDING TEST: RTC 98993 Hidden attribute changes from false to true");
	}

	@Test
	public void getActivitySections() {
		/*
		 * Tests the ability to get an activity's sections Step 1: Create an
		 * activity Step 2: Add a section to the activity Step 3: Get activity
		 * sections, verify the section added is there
		 */
		LOGGER.debug("BEGINNING TEST: Get Activity Sections");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create an activity");
		Activity testActivity = new Activity("TestActivity" + rand,
				"That's right.", null, null, false, false);
		Entry result = (Entry) service.createActivity(testActivity);

		LOGGER.debug("Step 2... Add a section to the activity");
		Section section = new Section("TestSection" + rand, 0, result);
		service.addNodeToActivity(result.getSelfLinkResolvedHref().toString(),
				section);

		LOGGER.debug("Step 3... Get activity sections, verify the section added is there");
		String activityUuid = result.getId().toString().split(":oa:")[1]; // Get
		// the
		// activity
		// uuid
		Feed activitySections = (Feed) assignedService
				.getActivitySections(activityUuid);
		// Go through the sections feed
		boolean foundSection = false;
		for (Entry e : activitySections.getEntries())
			if (e.getTitle().equals(section.getTitle()))
				foundSection = true;
		// Verify
		assertEquals(true, foundSection);

		LOGGER.debug("ENDING TEST: Get Activity Sections");
	}

	@Test
	public void postActivityWithSpecialHeaders() {
		/*
		 * Test for RTC story 119328 - allowing non-browser API access Step 1:
		 * POST activity w/ 'origin' header set to the test server domain,
		 * verify 201 status Step 2: POST activity w/ 'origin' header set to a
		 * different domain, verify 403 status Step 3: POST activity w/ 'origin'
		 * header set to a different domain AND 'X-Update-Nonce' header, verify
		 * 201 status Step 4: POST activity w/ 'X-Update-Nonce' header, verify
		 * 201 status
		 */
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) { // Change
																			// is
																			// not
																			// on
																			// SC
																			// yet
			LOGGER.debug("BEGINNING TEST... Post with special headers for RTC 119328");

			LOGGER.debug("Step 1... POST activity w/ 'origin' header set to the test server domain, verify 201 status");
			Activity testActivity = new Activity(
					"TestActivity-special headers", "Yes.", null, null, false,
					false);
			service.addRequestOption("origin", URLConstants.SERVER_URL);
			service.createActivity(testActivity);
			assertEquals("Should return 201 status", 201,
					service.getRespStatus());

			LOGGER.debug("Step 2... POST activity w/ 'origin' header set to a different domain, verify 403 status");
			service.addRequestOption("origin", "http://www.npr.org");
			service.createActivity(testActivity);
			assertEquals("Should return 403 status", 403,
					service.getRespStatus());

			LOGGER.debug("Step 3... POST activity w/ 'origin' header set to a different domain AND 'X-Update-Nonce' header, verify 201 status");
			service.addRequestOption("origin", "http://www.npr.org");
			service.addRequestOption("X-Update-Nonce", "any_value");
			service.createActivity(testActivity);
			assertEquals("Should return 201 status", 201,
					service.getRespStatus());

			LOGGER.debug("Step 4... POST activity w/ 'X-Update-Nonce' header, verify 201 status");
			service.addRequestOption("X-Update-Nonce", "any_value");
			service.createActivity(testActivity);
			assertEquals("Should return 201 status", 201,
					service.getRespStatus());

			LOGGER.debug("ENDING TEST... Post with special headers for RTC 119328");
		}
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
						LOGGER.debug("IN THE VALIDATOR.  From the server: "
								+ el.getText() + " Expected value: "
								+ imUser.getRealName());
						assertEquals("Wrong author: ", true, el.getText()
								.equalsIgnoreCase(imUser.getRealName()));
					}
				}
			}
		}
	}

}
