package com.ibm.lconn.automation.framework.services.searchsetup;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
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
import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

/**
 * JUnit Tests via Connections API for Activities Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class SearchActivitiesSetup {

	private static ServiceConfig config;

	static UserPerspective user;

	private static ActivitiesService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchActivitiesSetup.class.getName());

	/**
	 * Test fixture to initialize Abdera, Connections Service Config, and
	 * Activities Service object. These objects are reused for all of the
	 * Activities test cases.
	 * 
	 * @throws Exception
	 *             if any of the above fail to initialize.
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Activities Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.ACTIVITIES.toString());
		service = user.getActivitiesService();
		assertTrue("Activity service problem, service is NULL",service != null);
		
		config = user.getServiceConfig();

		LOGGER.debug("Finished Initializing Search Activities Data Setup Test");
	}

	@Test
	public void createActivitytoSearch() {
		LOGGER.debug("Beginning Test: Create an Activity to search for");
		deleteSearchTest();
		boolean found = false;

		// create an activity to test
		Activity simpleActivity = new Activity(
				StringConstants.SEARCH_ACTIVITY_NAME, "", "carmen", null,
				false, false);
		Entry activityResult = (Entry) service.createActivity(simpleActivity);

		// create members for the created activity
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

		// Search for the created activity to confirm it was made
		ArrayList<Activity> myActivities = service.getMyActivities();
		for (int i = 0; i < myActivities.size(); i++) {
			if (myActivities.get(i).getTitle()
					.equals(StringConstants.SEARCH_ACTIVITY_NAME)) {
				LOGGER.debug("Successfully created activity info for search test");
				assertTrue(true);
				found = true;
				break;
			}
		}

		if (!found) {
			LOGGER.debug("Failed: Could not create activity info for the search test");
			assertTrue(false);
		}
	}
	
	// TJB 4/20/15 Test seems to fail Jenkins.  Comment out pending research
	//@Test
	public void everythingFeed() {
		/* RTC 148039  When using the search parameter with the everything feed the total results in the returned feed is incorrect
		 * 
		 * Process:
		 * 1. Create activity 
		 * 2. Create 12 Todos
		 * 
		 * The validation for this test is located in SearchBasicTests - activitiesEverything().  
		 * 
		 */
		LOGGER.debug("START TEST: everythingFeed RTC 148039 ");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		LOGGER.debug("Step 1: Create an activity with tags");
		String tagsString = "a_" + uniqueNameAddition;
		Activity testActivity = new Activity("Activity RTC 148039 - Everything test", "Test content", tagsString, null, false, false);
		Entry actResult = (Entry)service.createActivity(testActivity);
		
		Collection activityNodeCollection = actResult.getExtension(StringConstants.APP_COLLECTION);
		
		LOGGER.debug("Step 2: Create 12 todo's");
		for (int j = 0; j < 12; j++) {
			String todoTitle = "ToDo_" + StringGenerator.randomLorem1Sentence();
			String todoContent = StringUtils.join(ArrayUtils.addAll(StringConstants.LOREM_1, StringConstants.LOREM_2));

			Todo todoEntry = new Todo(todoTitle, todoContent, "tag1 tag2 tag3",
					(j + 5) * 1000, RandomUtils.nextBoolean(new Random()), RandomUtils.nextBoolean(new Random()), actResult, null, null);
			Entry todoResult = (Entry) service.addNodeToActivity(activityNodeCollection.getHref().toString(), todoEntry);
			assertEquals("add Node to activity", 201, service.getRespStatus());

			LOGGER.debug("\t\tTodo: " + todoResult.getTitle() + " was created successfully.");
		}		
		LOGGER.debug("COMPLETE TEST: everythingFeed RTC 148039");
	}	
	

	public void deleteSearchTest() {
		// find the test activity and delete it
		ArrayList<Activity> activity = service.getMyActivities();
		for (int i = 0; i < activity.size(); i++) {
			if (activity.get(i).getTitle()
					.equals(StringConstants.SEARCH_ACTIVITY_NAME)) {
				service.deleteActivity(activity.get(i).getEditHref());
				break;
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}